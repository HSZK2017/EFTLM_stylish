package org.eftlm.stylish.util;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 智能性能调度器（AutoTicker）：根据实时计算资源余量自动调整游戏刻加速倍率，
 * 并内置看门狗（watchdog）在服务器负载过大或 tick 停滞时强制降速。
 * <p>
 * <b>职责</b>（与 {@link TickAccelerator} 配合：本类只做"决策"，倍率由
 * {@link TickAccelerator#setMultiplier(float)} 执行）：
 * <ul>
 *   <li><b>空闲加速</b>：系统 CPU 低 + tick 健康（95%ile 耗时低 + 实际 TPS 达成）
 *       → 每周期提升一档倍率（1.0 → 1.5 → 2.0 → … ≤ 上限），上限按 CPU 余量配置
 *       （本机实测最佳稳定档 8.0=160TPS；9.0 目标 180TPS 超过 tick 循环自然上限会震荡）；</li>
 *   <li><b>负载降速</b>：系统 CPU 高 / tick 95%ile 超阈值 / 实际 TPS 明显低于期望
 *       → 每周期降低一档（≥ 下限 1.0，即降回原速）；</li>
 *   <li><b>看门狗</b>：tickCount 停滞（服务器卡死征兆）且 CPU 满载
 *       → 立即强制降到下限，并写 {@code config/eftlm_stylish/watchdog_alarm.txt}
 *       供外部守护脚本（tools/guardian.ps1）读取联动。</li>
 * </ul>
 * <p>
 * 采样与平滑：每 {@code interval} 秒采样一次系统 CPU（{@code OperatingSystemMXBean}，
 * Windows 下可用）与服务器实际 TPS（tickCount 增量/墙上时间）；CPU 用 EMA(0.3) 平滑，
 * 倍率变化带滞回（升档需同时满足 CPU 低与 tick 健康，降档任一坏指标即触发），避免抖动。
 * <p>
 * 配置（rl.properties）：{@code auto_tickrate}（总开关，默认 false 保守）、
 * {@code auto_tickrate_min/max}（倍率范围）、{@code auto_tickrate_step}、
 * {@code auto_tickrate_cpu_low/high}（0~1）、{@code auto_tickrate_tick_low/high_ms}、
 * {@code auto_tickrate_interval}（秒）、{@code auto_tickrate_watchdog_stall_ticks}。
 * 手动 {@code /rl tickrate x} 会暂停自动调度（manual 优先），{@code /rl autotick on} 恢复。
 */
public final class AutoTicker {

    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    // ---------- 运行时状态 ----------
    /** 调度线程池：P1 修复后可在 shutdown 后重建（旧实现 static final + shutdown = 二次 start 永久失效） */
    private static volatile ScheduledExecutorService scheduler;
    private static volatile java.util.concurrent.ScheduledFuture<?> scheduledTask;
    /** 当前是否允许自动调度（false = 手动 /rl tickrate 接管） */
    private static volatile boolean enabled = false;
    private static volatile boolean running = false;
    /** 采样间隔（秒），从配置读取 */
    private static volatile long intervalSec = 5L;

    // 倍率范围
    private static volatile float minMultiplier = 1.0F;
    private static volatile float maxMultiplier = 2.0F;
    private static volatile float step = 0.5F;

    // 决策阈值
    private static volatile double cpuLow = 0.5;    // 系统 CPU 低于此 → 可升档
    private static volatile double cpuHigh = 0.8;   // 系统 CPU 高于此 → 降档
    private static volatile double tickLowMs = 25.0;   // tick 95%ile 低于此 → 可升档
    private static volatile double tickHighMs = 40.0;  // tick 95%ile 高于此 → 降档
    /** 实际 TPS 低于 期望TPS×此比例 → 降档（加速未达成，性能瓶颈） */
    private static volatile double tpsSlack = 0.85;

    /** 看门狗：tickCount 停滞多少**秒**（墙上时间）判死（配置键 auto_tickrate_watchdog_stall） */
    private static volatile long watchdogStallSeconds = 30L;

    // 采样状态
    private static final AtomicLong lastTickCount = new AtomicLong(-1L);
    private static final AtomicLong lastSampleWall = new AtomicLong(0L);
    /** 上次观察到 tickCount 前进的墙上时间（P1 修复：看门狗改用真实停滞时长，而非"采样次数×间隔"） */
    private static final AtomicLong lastProgressWallMs = new AtomicLong(0L);
    private static final AtomicReference<Double> cpuEma = new AtomicReference<>(0.0);
    private static final AtomicInteger stallStreak = new AtomicInteger(0);
    private static final AtomicLong lastStallLog = new AtomicLong(0L);
    private static final AtomicLong lastResetLog = new AtomicLong(0L);
    private static final AtomicLong lastSampleWarnLog = new AtomicLong(0L);

    /** 上次决策摘要（/rl autotick status 展示） */
    private static final AtomicReference<String> lastSummary = new AtomicReference<>("not started");
    /** 当前服务器实例（时钟复位用） */
    private static final AtomicReference<MinecraftServer> CURRENT_SERVER = new AtomicReference<>(null);

    private AutoTicker() {
    }

    /** 服务端启动（ServerStartedEvent 时调用；客户端无操作） */
    public static synchronized void start(MinecraftServer server) {
        if (running || FMLEnvironment.dist.isClient()) {
            return;
        }
        org.eftlm.stylish.rl.RlConfig.ensureLoaded();
        CURRENT_SERVER.set(server);
        enabled = org.eftlm.stylish.rl.RlConfig.autoTickrate;
        if (!enabled) {
            LOGGER.info("[AutoTick] scheduler disabled by config (auto_tickrate=false)");
            return;
        }
        minMultiplier = org.eftlm.stylish.rl.RlConfig.autoTickrateMin;
        maxMultiplier = org.eftlm.stylish.rl.RlConfig.autoTickrateMax;
        step = org.eftlm.stylish.rl.RlConfig.autoTickrateStep;
        cpuLow = org.eftlm.stylish.rl.RlConfig.autoTickrateCpuLow;
        cpuHigh = org.eftlm.stylish.rl.RlConfig.autoTickrateCpuHigh;
        tickLowMs = org.eftlm.stylish.rl.RlConfig.autoTickrateTickLowMs;
        tickHighMs = org.eftlm.stylish.rl.RlConfig.autoTickrateTickHighMs;
        intervalSec = org.eftlm.stylish.rl.RlConfig.autoTickrateInterval;
        watchdogStallSeconds = org.eftlm.stylish.rl.RlConfig.autoTickrateWatchdogStall;
        lastTickCount.set(-1L);
        lastSampleWall.set(0L);
        lastProgressWallMs.set(System.currentTimeMillis());
        cpuEma.set(0.0);
        stallStreak.set(0);
        // P1 修复（2026-09-10）：running 只在调度真正提交成功后置位；调度器已 shutdown 时重建
        // （旧实现 `running=true` 先于提交，且 SCHEDULER 是 static final + shutdown() 永久终止 →
        //  集成服/单机二次 start 抛 RejectedExecutionException，而 status() 仍显示"运行中"）
        try {
            ScheduledExecutorService exec = scheduler;
            if (exec == null || exec.isShutdown()) {
                exec = Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r, "eftlm-auto-ticker");
                    t.setDaemon(true);
                    return t;
                });
                scheduler = exec;
            }
            scheduledTask = exec.scheduleAtFixedRate(() -> tick(server), intervalSec, intervalSec, TimeUnit.SECONDS);
            running = true;
        } catch (RuntimeException ex) {
            running = false;
            LOGGER.error("[AutoTick] failed to start scheduler, auto tick disabled", ex);
            return;
        }
        LOGGER.info("[AutoTick] scheduler started: interval={}s range={}~{} step={} cpu<{} up / cpu>{} down / tick95<{}ms up / >{}ms down / stall>{}s",
                intervalSec, minMultiplier, maxMultiplier, step, cpuLow, cpuHigh, tickLowMs, tickHighMs, watchdogStallSeconds);
    }

    /** 手动 /rl tickrate 接管：暂停自动调度 */
    public static void pauseForManual() {
        enabled = false;
        LOGGER.info("[AutoTick] manual override, auto scheduler paused (/rl autotick on to resume)");
    }

    /** /rl autotick on|off（重新启用时重置采样基线，避免暂停期偏差） */
    public static synchronized void setEnabled(boolean on) {
        if (on && !enabled) {
            lastTickCount.set(-1L);
            lastSampleWall.set(0L);
            lastProgressWallMs.set(System.currentTimeMillis());
            cpuEma.set(0.0);
            stallStreak.set(0);
        }
        enabled = on;
        LOGGER.info("[AutoTick] auto scheduling {}", on ? "enabled" : "disabled");
    }

    public static boolean isEnabled() {
        return enabled;
    }

    /**
     * 热改倍率范围/步长（/rl autotick max|min）——无需重启立即生效；
     * 重置采样基线，避免倍率跳变后 TPS/CPU 采样失真。
     */
    public static synchronized void setRange(float min, float max, float step) {
        minMultiplier = Math.max(0.1F, Math.min(10.0F, min));
        maxMultiplier = Math.max(minMultiplier, Math.min(10.0F, max));
        AutoTicker.step = Math.max(0.1F, Math.min(5.0F, step));
        lastTickCount.set(-1L);
        lastSampleWall.set(0L);
        lastProgressWallMs.set(System.currentTimeMillis());
        cpuEma.set(0.0);
        stallStreak.set(0);
        LOGGER.info("[AutoTick] range hot-updated: {}~{} step={} (current multiplier={})",
                minMultiplier, maxMultiplier, AutoTicker.step, TickAccelerator.multiplier());
    }

    public static String status() {
        String s = lastSummary.get();
        return "[rl] autotick: " + (enabled ? "ON" : "OFF")
                + (running ? "" : " (not running)")
                + " | current multiplier=" + TickAccelerator.multiplier()
                + " | range=" + minMultiplier + "~" + maxMultiplier
                + " | last: " + s;
    }

    /** 周期决策（scheduler 线程） */
    private static void tick(MinecraftServer server) {
        try {
            if (!enabled) {
                return;
            }
            long now = System.currentTimeMillis();
            long tickCount = server.getTickCount();

            // ---- 采样：系统 CPU（EMA；采样失败返回 -1，不参与判定）----
            double sysCpu = readSystemCpu();
            if (sysCpu >= 0) {
                double prev = cpuEma.get();
                cpuEma.set(prev <= 0 ? sysCpu : prev * 0.7 + sysCpu * 0.3);
            }
            double ema = cpuEma.get();

            // ---- 采样：实际 TPS（tickCount 增量 / 墙上时间）----
            double actualTps = -1;
            long prevTick = lastTickCount.get();
            long prevWall = lastSampleWall.get();
            if (prevTick >= 0 && prevWall > 0 && now > prevWall) {
                actualTps = (tickCount - prevTick) * 1000.0 / (now - prevWall);
            }
            boolean advanced = prevTick < 0 || tickCount > prevTick;
            if (advanced) {
                lastProgressWallMs.set(now);
            }
            lastTickCount.set(tickCount);
            lastSampleWall.set(now);

            // ---- 采样：tick 95%ile 耗时（server.tickTimes 纳秒数组）----
            double tick95ms = readTick95Ms(server);

            float cur = TickAccelerator.multiplier();
            float targetTps = 20.0F * cur;
            float next = cur;

            // ---- 看门狗（P1 修复 2026-09-10）：基准改为"真实墙上时间停滞时长" ----
            // 旧实现用 `stallStreak * intervalSec` 与按秒命名的 watchdogStallTicks 比较：
            // intervalSec 可配到 120s（RlConfig 允许），于是"两次采样间少 1 个 tick"会被算成
            // "停滞 120 秒" → 立即触发时钟复位 + 写 watchdog_alarm.txt → guardian 误重启。
            long progressWall = lastProgressWallMs.get();
            long stalledSec = progressWall > 0 ? (now - progressWall) / 1000L : 0L;
            boolean stalled = prevTick >= 0 && (tickCount - prevTick) < 1 && stalledSec > 0;
            if (stalled) {
                stallStreak.incrementAndGet();
            } else {
                stallStreak.set(0);
            }
            if (stalled && stalledSec >= Math.max(1L, watchdogStallSeconds / 2)) {
                // 停滞一半阈值：先"时钟复位"（nextTickTime→0 + 倍率降下限）免重启恢复
                long nowLog = System.currentTimeMillis();
                if (nowLog - lastResetLog.get() > 60000) {
                    lastResetLog.set(nowLog);
                    boolean ok = resetTickClock();
                    LOGGER.error("[AutoTick] WATCHDOG: tick stalled {}s → clock reset {} (nextTickTime→0, multiplier→min), waiting for recovery",
                            stalledSec, ok ? "OK" : "FAILED");
                    dumpThreads("watchdog: tick stalled " + stalledSec + "s (clock reset)");
                }
            }
            if (stalled && stalledSec >= watchdogStallSeconds) {
                next = Math.min(cur, minMultiplier);
                long nowLog = System.currentTimeMillis();
                if (nowLog - lastStallLog.get() > 60000) {
                    lastStallLog.set(nowLog);
                    LOGGER.error("[AutoTick] WATCHDOG: server tick stalled {}s (tickCount stuck at {}, cpu={}%) → force multiplier={}",
                            stalledSec, tickCount, Math.round(ema * 100), next);
                    writeWatchdogAlarm("tick stalled " + stalledSec + "s, cpu=" + Math.round(ema * 100) + "%, forced multiplier=" + next);
                    dumpThreads("watchdog: tick stalled " + stalledSec + "s");
                }
            }

            // ---- 滞回决策（仅当看门狗未强制时）----
            if (next == cur) {
                // P1 修复：采样失败视为"未知"，既不升档也不据此降档（旧实现把 -1/0 当成
                // "tick 健康 / CPU 空闲"，在反射失效后会一路升档到上限）
                boolean cpuSampleOk = sysCpu >= 0;
                boolean tickSampleOk = tick95ms >= 0;
                boolean samplesOk = cpuSampleOk && tickSampleOk;
                boolean tickHealthy = tickSampleOk && tick95ms < tickLowMs;
                boolean cpuIdle = cpuSampleOk && ema < cpuLow;
                boolean tpsAchieved = actualTps < 0 || actualTps >= targetTps * tpsSlack;

                boolean overloaded = (cpuSampleOk && ema > cpuHigh)
                        || (tickSampleOk && tick95ms > tickHighMs)
                        || (actualTps >= 0 && actualTps < targetTps * tpsSlack);

                if (overloaded && cur > minMultiplier) {
                    next = Math.max(minMultiplier, cur - step);
                    LOGGER.info("[AutoTick] load high (cpu={}% tick95={}ms tps={}/{} TPS) → {}",
                            Math.round(ema * 100), Math.round(tick95ms), String.format("%.1f", actualTps), Math.round(targetTps), next);
                } else if (samplesOk && !overloaded && cpuIdle && tickHealthy && tpsAchieved && cur < maxMultiplier) {
                    next = Math.min(maxMultiplier, cur + step);
                    LOGGER.info("[AutoTick] resources idle (cpu={}% tick95={}ms tps={}/{} TPS) → {}",
                            Math.round(ema * 100), Math.round(tick95ms), String.format("%.1f", actualTps), Math.round(targetTps), next);
                } else if (!samplesOk && now - lastSampleWarnLog.get() > 60000) {
                    lastSampleWarnLog.set(now);
                    LOGGER.warn("[AutoTick] sampling unavailable (cpu={} tick95={}) → hold multiplier={} "
                                    + "(reflection/mapping broken? check logs)",
                            cpuSampleOk ? String.format("%.0f%%", ema * 100) : "n/a",
                            tickSampleOk ? Math.round(tick95ms) + "ms" : "n/a", cur);
                }
            }

            if (next != cur) {
                TickAccelerator.setMultiplier(next);
            }
            lastSummary.set(String.format("cpu=%s tick95=%s tps=%s stalled=%ss cur=%.1f next=%.1f",
                    sysCpu < 0 ? "n/a" : String.format("%.0f%%", ema * 100),
                    tick95ms < 0 ? "n/a" : String.format("%.0fms", tick95ms),
                    actualTps < 0 ? "n/a" : String.format("%.1f", actualTps),
                    stalled ? String.valueOf(stalledSec) : "0", cur, next));
        } catch (Exception e) {
            LOGGER.warn("[AutoTick] scheduler tick error", e);
        }
    }

    /** 系统 CPU 使用率（0~1）；不可用时返回 **-1**（P1：不再用 0 冒充"空闲"） */
    private static double readSystemCpu() {
        try {
            var os = ManagementFactory.getOperatingSystemMXBean();
            if (os instanceof com.sun.management.OperatingSystemMXBean sun) {
                double v = sun.getSystemCpuLoad();
                if (v >= 0 && v <= 1) {
                    return v;
                }
            }
        } catch (Throwable ignored) {
        }
        return -1.0;
    }

    /**
     * 时钟复位（tick 停滞免重启恢复）：把 nextTickTime（f_129727_）与 lastTime
     * （f_129726_）反射置 0 → waitUntilNextTick 的等待条件（virtualNow &lt; nextTickTime）
     * 立即满足 → tick 循环恢复执行。同时把倍率降到下限，追 tick 阶段保持 1× 安全速率。
     * <p>
     * P1 修复（2026-09-10）：字段解析改为"只按 SRG/官方名匹配 + 命中即停 + 类型校验 + 缓存"，
     * 并区分完全/部分成功。旧实现遍历全部声明字段、无 break、无类型校验，别名表里混入混淆名
     * （ai/ah），匹配结果依赖字段声明顺序——一旦命中无关 long 字段就会把它清零。
     */
    private static volatile java.lang.reflect.Field nextTickTimeField;
    private static volatile java.lang.reflect.Field lastTimeField;
    private static volatile boolean clockFieldsResolved = false;

    private static void resolveClockFields() {
        if (clockFieldsResolved) {
            return;
        }
        for (java.lang.reflect.Field f : MinecraftServer.class.getDeclaredFields()) {
            if (f.getType() != long.class || java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
                continue;
            }
            String n = f.getName();
            if (nextTickTimeField == null && (n.equals("f_129727_") || n.equals("nextTickTime"))) {
                nextTickTimeField = f;
            } else if (lastTimeField == null && (n.equals("f_129726_") || n.equals("lastTime"))) {
                lastTimeField = f;
            }
            if (nextTickTimeField != null && lastTimeField != null) {
                break;
            }
        }
        clockFieldsResolved = true;
        if (nextTickTimeField == null) {
            LOGGER.warn("[AutoTick] clock reset unavailable: nextTickTime field not found "
                    + "(mapping changed? expected f_129727_/nextTickTime)");
        } else if (lastTimeField == null) {
            LOGGER.warn("[AutoTick] clock reset partial: lastTime field not found (only nextTickTime will be reset)");
        }
    }

    private static boolean resetTickClock() {
        try {
            MinecraftServer server = CURRENT_SERVER.get();
            if (server == null) {
                return false;
            }
            // 1) 倍率降到下限（追 tick 安全）
            TickAccelerator.setMultiplier(Math.min(TickAccelerator.multiplier(), minMultiplier));
            // 2) 反射复位 nextTickTime（关键）与 lastTime（可选）
            resolveClockFields();
            java.lang.reflect.Field f727 = nextTickTimeField;
            if (f727 == null) {
                return false;
            }
            f727.setAccessible(true);
            f727.setLong(server, 0L);
            java.lang.reflect.Field f726 = lastTimeField;
            if (f726 != null) {
                f726.setAccessible(true);
                f726.setLong(server, 0L);
            }
            return true;
        } catch (Throwable t) {
            LOGGER.warn("[AutoTick] clock reset failed", t);
            return false;
        }
    }

    /** 服务器最近 100 tick 耗时 95%ile（毫秒）；-1 表示不可用（反射读取，兼容映射差异） */
    private static double readTick95Ms(MinecraftServer server) {        try {
            long[] times = null;
            // 方法通道（MCP 环境有 getTickTimes()；SRG 生产环境可能只有字段）
            for (java.lang.reflect.Method cand : MinecraftServer.class.getMethods()) {
                if (cand.getReturnType() == long[].class && cand.getParameterCount() == 0) {
                    times = (long[]) cand.invoke(server);
                    break;
                }
            }
            // 字段通道（SRG 生产环境：public final long[] f_129748_ = tickTimes）
            if (times == null) {
                for (java.lang.reflect.Field f : MinecraftServer.class.getFields()) {
                    if (f.getType() == long[].class) {
                        times = (long[]) f.get(server);
                        break;
                    }
                }
            }
            if (times == null || times.length == 0) {
                return -1;
            }
            long[] copy = times.clone();
            java.util.Arrays.sort(copy);
            int idx = Math.min(copy.length - 1, (int) Math.ceil(copy.length * 0.95) - 1);
            return copy[Math.max(0, idx)] / 1_000_000.0;
        } catch (Throwable ignored) {
            return -1;
        }
    }

    /** 写看门狗报警文件（外部 guardian.ps1 读取联动：可触发重启/告警） */
    private static void writeWatchdogAlarm(String reason) {
        try {
            Path dir = FMLPaths.CONFIGDIR.get().resolve("eftlm_stylish");
            Files.createDirectories(dir);
            Files.writeString(dir.resolve("watchdog_alarm.txt"),
                    System.currentTimeMillis() + " | " + reason + "\n");
        } catch (Exception e) {
            LOGGER.warn("[AutoTick] failed to write watchdog alarm", e);
        }
    }

    /**
     * 全线程转储（死锁定位）：看门狗触发时自动调用，亦可 /rl dumpthreads 手动触发。
     * 输出 config/eftlm_stylish/thread_dump_&lt;epoch&gt;.txt——主线程栈即死锁/阻塞点实锤。
     */
    public static void dumpThreads(String reason) {
        // P1 修复：转储放到独立低优先级守护线程——getAllStackTraces 需要全局 safepoint，
        // 在服务器已经卡死时不该再让调度线程（乃至主线程）同步等待它。
        Thread t = new Thread(() -> dumpThreadsNow(reason), "eftlm-thread-dump");
        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    private static void dumpThreadsNow(String reason) {
        try {
            Path dir = FMLPaths.CONFIGDIR.get().resolve("eftlm_stylish");
            Files.createDirectories(dir);
            Path file = dir.resolve("thread_dump_" + System.currentTimeMillis() + ".txt");
            StringBuilder sb = new StringBuilder();
            sb.append("=== EFTLM thread dump ").append(new java.util.Date()).append(" ===\n");
            sb.append("reason: ").append(reason).append("\n\n");
            for (var entry : Thread.getAllStackTraces().entrySet()) {
                Thread t = entry.getKey();
                sb.append('"').append(t.getName()).append("\" id=").append(t.getId())
                        .append(" state=").append(t.getState())
                        .append(" daemon=").append(t.isDaemon()).append('\n');
                for (StackTraceElement el : entry.getValue()) {
                    sb.append("    at ").append(el).append('\n');
                }
                sb.append('\n');
            }
            Files.writeString(file, sb.toString(), java.nio.charset.StandardCharsets.UTF_8);
            LOGGER.error("[AutoTick] thread dump written to {} (reason: {})", file, reason);
        } catch (Exception ex) {
            LOGGER.warn("[AutoTick] failed to write thread dump", ex);
        }
    }

    /**
     * 关服/卸载时停止（进程退出自然终止，防泄漏）。
     * P1 修复（2026-09-10）：只取消任务并关闭当前执行器；{@link #start} 会按需重建，
     * 因此集成服/单机反复进出世界不会再出现"running=true 但调度器已死"的静默失效。
     */
    public static synchronized void shutdown() {
        running = false;
        java.util.concurrent.ScheduledFuture<?> task = scheduledTask;
        if (task != null) {
            task.cancel(false);
            scheduledTask = null;
        }
        ScheduledExecutorService exec = scheduler;
        if (exec != null) {
            exec.shutdown();
            scheduler = null;
        }
    }
}
