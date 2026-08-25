package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * P0 观测层：决策链路追踪（状态 → 决策 → 执行 → 结果 全链路回放）。
 * <p>
 * 为每个启用女仆维护环形缓冲（默认 600 决策点 ≈ 2.5 分钟战斗），记录两类行：
 * <ul>
 *     <li><b>D（决策行）</b>：tick / 决策来源（model|rule|forced） / 动作 / 槽标签 /
 *         模型概率 top3 / 执行结果 / 执行后动画帧（id/elapsed/total） /
 *         EntityState 摘要（level/inaction/attacking/knockdown/movementLocked） /
 *         目标距离与目标动画 level —— 由 {@link #recordDecision} 写入、
 *         {@link #resolveExec} 在事件总线执行后补全；</li>
 *     <li><b>E（事件行）</b>：命中 / 受击 / 击倒 / 弹反 / 格挡 / 箭矢反应 /
 *         硬约束修正（forced_roll / ranged_fallback / idle_no_target）等，
 *         由 {@link #event} 写入。</li>
 * </ul>
 * <p>
 * CSV 输出到 {@code config/eftlm_stylish/dumps/trace_<uuid8>_<ts>.csv}，
 * 供 /rl dump 命令导出回放（不参与训练数据，训练轨迹格式保持兼容）。
 * 女仆移除时经 {@link #forget} 释放缓冲（防止长期运行内存增长）。
 */
public final class RlTrace {

    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    /** 每女仆环形缓冲上限（决策行 + 事件行合计；600 ≈ 5 分钟战斗） */
    private static final int MAX_ROWS = 1200;
    /** 环形缓冲：每女仆按时间序的行队列 */
    private static final Map<UUID, Deque<String>> BUFFERS = new HashMap<>();
    /** 每女仆最近一条决策行是否仍待执行结果补全 */
    private static final Map<UUID, Boolean> PENDING_EXEC = new HashMap<>();

    /** CSV 列头（与行写入列序一致） */
    private static final String HEADER = "kind,tick,src,action,label,p0,p1,p2,res,"
            + "anim,elapsed,total,lvl,inaction,attacking,knockdown,movlocked,tdist,tlvl,detail";

    private RlTrace() {
    }

    // ------------------------------------------------------------------
    // 写入
    // ------------------------------------------------------------------

    /**
     * 记录一个决策步（决策点调用；执行结果由 {@link #resolveExec} 补全）。
     *
     * @param src    决策来源：model（神经网络）/ rule（规则兜底）
     * @param probs  模型输出概率分布（规则决策传 null）
     */
    public static void recordDecision(EntityMaid maid, int tick, String src, int action,
                                      String label, float[] probs) {
        if (!RlConfig.traceEnabled) {
            return;
        }
        UUID id = maid.getUUID();
        Deque<String> q = BUFFERS.computeIfAbsent(id, k -> new ArrayDeque<>());
        // top3 概率（argMax 排序，不含无效掩码后的精确排序——按原始概率取前 3）
        float[] top = top3(probs);
        StringBuilder sb = new StringBuilder(160);
        sb.append("D,").append(tick).append(',').append(src).append(',').append(action)
                .append(',').append(csv(label)).append(',')
                .append(fmt(top[0])).append(',').append(fmt(top[1])).append(',').append(fmt(top[2]))
                .append(',').append("PENDING");
        push(q, id, sb.toString());
        PENDING_EXEC.put(id, Boolean.TRUE);
    }

    /**
     * 执行结果补全：事件总线执行完成后调用，把最后一条 PENDING 决策行的
     * 执行结果与执行后动画帧 / EntityState / 目标状态填入。
     */
    public static void resolveExec(EntityMaid maid, RlExecResult res, MaidPatch<?> patch, LivingEntity target) {
        if (!RlConfig.traceEnabled) {
            return;
        }
        UUID id = maid.getUUID();
        Deque<String> q = BUFFERS.get(id);
        if (q == null || q.isEmpty() || !Boolean.TRUE.equals(PENDING_EXEC.getOrDefault(id, false))) {
            return;
        }
        PENDING_EXEC.put(id, Boolean.FALSE);
        String last = q.peekLast();
        if (last == null || !last.startsWith("D,")) {
            return;
        }
        // 动画帧 / EntityState 上下文（执行后瞬时采样）
        String anim = "";
        float elapsed = -1;
        float total = -1;
        int lvl = -1;
        boolean inaction = false;
        boolean attacking = false;
        boolean knockdown = false;
        boolean movLocked = false;
        float tdist = -1;
        int tlvl = -1;
        if (patch != null) {
            try {
                inaction = patch.getEntityState().inaction();
                attacking = patch.getEntityState().attacking();
                knockdown = patch.getEntityState().knockDown();
                movLocked = patch.getEntityState().movementLocked();
                lvl = patch.getEntityState().getLevel();
                AnimationPlayer player = patch.getAnimator().getPlayerFor(null);
                if (player != null && player.getAnimation() != null && player.getAnimation().get() != null) {
                    var real = player.getRealAnimation();
                    anim = real != null && real.get() != null && real.get().getRegistryName() != null
                            ? real.get().getRegistryName().toString() : "?";
                    elapsed = player.getElapsedTime();
                    total = player.getAnimation().get().getTotalTime();
                }
            } catch (Throwable ignored) {
                // 动画状态采样失败不阻塞观测
            }
        }
        if (target != null && target.isAlive()) {
            try {
                tdist = maid.distanceTo(target);
                LivingEntityPatch<?> tp = EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
                tlvl = tp != null ? tp.getEntityState().getLevel() : -1;
            } catch (Throwable ignored) {
            }
        }
        String execCtx = ',' + csv(anim) + ',' + fmt(elapsed) + ',' + fmt(total)
                + ',' + lvl + ',' + b01(inaction) + ',' + b01(attacking) + ',' + b01(knockdown)
                + ',' + b01(movLocked) + ',' + fmt(tdist) + ',' + tlvl + ",exec=" + res.name();
        q.removeLast();
        push(q, id, last + execCtx);
    }

    /** 事件行（战斗事件 / 硬约束修正等），tick 为当前游戏 tick */
    public static void event(EntityMaid maid, String type, String detail) {
        if (!RlConfig.traceEnabled) {
            return;
        }
        UUID id = maid.getUUID();
        Deque<String> q = BUFFERS.computeIfAbsent(id, k -> new ArrayDeque<>());
        StringBuilder sb = new StringBuilder(96);
        sb.append("E,").append(maid.tickCount).append(',').append(csv(type)).append(",,,,,")
                .append(",,,,,,,,,,,,").append(csv(detail));
        push(q, id, sb.toString());
    }

    // ------------------------------------------------------------------
    // 维护
    // ------------------------------------------------------------------

    /** 女仆移除时释放追踪缓冲 */
    public static void forget(UUID id) {
        BUFFERS.remove(id);
        PENDING_EXEC.remove(id);
    }

    /** 全部清空（调试 / 重置用） */
    public static void reset() {
        BUFFERS.clear();
        PENDING_EXEC.clear();
    }

    /** 当前缓冲总行数（诊断用） */
    public static int bufferSize() {
        return BUFFERS.values().stream().mapToInt(Deque::size).sum();
    }

    /**
     * 导出追踪缓冲到 CSV 文件（config/eftlm_stylish/dumps/）。
     *
     * @param uuidOrAll 女仆 UUID 字符串，或 "all"
     * @return 写出的文件数
     */
    public static int dump(String uuidOrAll) {
        Path dir = FMLPaths.CONFIGDIR.get().resolve("eftlm_stylish").resolve("dumps");
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            LOGGER.error("[TRACE] cannot create dump dir {}", dir, e);
            return 0;
        }
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        int written = 0;
        List<Map.Entry<UUID, Deque<String>>> targets = new ArrayList<>();
        if ("all".equalsIgnoreCase(uuidOrAll)) {
            targets.addAll(BUFFERS.entrySet());
        } else {
            try {
                UUID id = UUID.fromString(uuidOrAll);
                Deque<String> q = BUFFERS.get(id);
                if (q != null) {
                    targets.add(Map.entry(id, q));
                }
            } catch (IllegalArgumentException e) {
                LOGGER.error("[TRACE] invalid uuid: {}", uuidOrAll);
                return 0;
            }
        }
        for (Map.Entry<UUID, Deque<String>> e : targets) {
            Path file = dir.resolve("trace_" + e.getKey().toString().substring(0, 8) + "_" + ts + ".csv");
            try (BufferedWriter w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                w.write(HEADER);
                w.newLine();
                for (String row : e.getValue()) {
                    w.write(row);
                    w.newLine();
                }
                written++;
                LOGGER.info("[TRACE] dumped {} rows -> {}", e.getValue().size(), file);
            } catch (IOException ex) {
                LOGGER.error("[TRACE] dump failed {}", file, ex);
            }
        }
        return written;
    }

    // ------------------------------------------------------------------
    // 内部工具
    // ------------------------------------------------------------------

    private static void push(Deque<String> q, UUID id, String row) {
        q.addLast(row);
        while (q.size() > MAX_ROWS) {
            q.removeFirst();
        }
    }

    /** 概率 top3（probs 为 null 时返回 -1 占位） */
    private static float[] top3(float[] probs) {
        float[] top = {-1, -1, -1};
        if (probs == null) {
            return top;
        }
        for (float p : probs) {
            if (p > top[0]) {
                top[2] = top[1];
                top[1] = top[0];
                top[0] = p;
            } else if (p > top[1]) {
                top[2] = top[1];
                top[1] = p;
            } else if (p > top[2]) {
                top[2] = p;
            }
        }
        return top;
    }

    private static String fmt(float v) {
        return v == (long) v ? String.valueOf((long) v) : String.valueOf(v);
    }

    private static String b01(boolean b) {
        return b ? "1" : "0";
    }

    /** CSV 字段转义（逗号/引号/换行） */
    private static String csv(String s) {
        if (s == null) {
            return "";
        }
        if (s.indexOf(',') >= 0 || s.indexOf('"') >= 0 || s.indexOf('\n') >= 0) {
            return '"' + s.replace("\"", "\"\"") + '"';
        }
        return s;
    }
}
