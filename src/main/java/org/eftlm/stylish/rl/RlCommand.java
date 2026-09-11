package org.eftlm.stylish.rl;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.eftlm.stylish.EFTLMStylish;

import java.nio.file.Files;

/**
 * RL 运维命令（权限等级 2）：
 * <ul>
 *     <li>/rl reload —— 热重载 config/eftlm_stylish/rl_model.bin（迭代部署无需重启服务器）</li>
 *     <li>/rl status —— 模型维度 / 版本信息 / 已注册执行器 / 运行时配置</li>
 *     <li>/rl trace on|off —— P0 观测：决策链路追踪开关（默认开）</li>
 *     <li>/rl shadow on|off —— P0 观测：影子模式（模型只推理不执行，行为表接管）</li>
 *     <li>/rl dump [uuid|all] —— 导出决策链路追踪 CSV（config/eftlm_stylish/dumps/）</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = EFTLMStylish.MODID)
public class RlCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("rl")
                        .requires(src -> src.hasPermission(2))
                        .then(Commands.literal("reload").executes(RlCommand::reload))
                        .then(Commands.literal("status").executes(RlCommand::status))
                        .then(Commands.literal("trace")
                                .then(Commands.literal("on").executes(ctx -> setTrace(ctx, true)))
                                .then(Commands.literal("off").executes(ctx -> setTrace(ctx, false))))
                        .then(Commands.literal("shadow")
                                .then(Commands.literal("on").executes(ctx -> setShadow(ctx, true)))
                                .then(Commands.literal("off").executes(ctx -> setShadow(ctx, false))))
                        .then(Commands.literal("dump")
                                .executes(ctx -> dump(ctx, "all"))
                                .then(Commands.argument("target", com.mojang.brigadier.arguments.StringArgumentType.word())
                                        .executes(ctx -> dump(ctx, com.mojang.brigadier.arguments.StringArgumentType.getString(ctx, "target")))))
                        .then(Commands.literal("layout").executes(RlCommand::layout))
                        .then(Commands.literal("adaptive").executes(RlCommand::adaptive))
                        .then(Commands.literal("tickrate")
                                .then(Commands.argument("multiplier", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0.1F, 10.0F))
                                        .executes(RlCommand::tickrate)))
                        .then(Commands.literal("autotick")
                                .then(Commands.literal("on").executes(ctx -> setAutoTick(ctx, true)))
                                .then(Commands.literal("off").executes(ctx -> setAutoTick(ctx, false)))
                                .then(Commands.literal("max")
                                        .then(Commands.argument("value", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(1.0F, 10.0F))
                                                .executes(RlCommand::autoTickMax)))
                                .then(Commands.literal("min")
                                        .then(Commands.argument("value", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0.1F, 10.0F))
                                                .executes(RlCommand::autoTickMin)))
                                .then(Commands.literal("status").executes(RlCommand::autoTickStatus)))
                        .then(Commands.literal("dumpthreads").executes(RlCommand::dumpThreads)));
    }

    /**
     * P5.7：智能性能调度开关（/rl autotick on|off）——空闲自动加速、负载自动降速、看门狗。
     */
    private static int setAutoTick(CommandContext<CommandSourceStack> ctx, boolean on) {
        org.eftlm.stylish.util.AutoTicker.setEnabled(on);
        ctx.getSource().sendSuccess(() -> Component.literal(
                "[rl] autotick " + (on ? "ON" : "OFF")
                        + (on ? " (idle->accelerate, load->decelerate, watchdog armed)" : "")), false);
        return 1;
    }

    /**
     * P5.7：智能调度状态（倍率范围/当前值/最近采样摘要）。
     */
    private static int autoTickStatus(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendSuccess(() -> Component.literal(
                org.eftlm.stylish.util.AutoTicker.status()), false);
        return 1;
    }

    /**
     * P5.7 死锁定位：手动触发全线程转储（/rl dumpthreads）——写入
     * config/eftlm_stylish/thread_dump_&lt;epoch&gt;.txt，主线程栈即阻塞点实锤。
     */
    private static int dumpThreads(CommandContext<CommandSourceStack> ctx) {
        org.eftlm.stylish.util.AutoTicker.dumpThreads("manual /rl dumpthreads");
        ctx.getSource().sendSuccess(() -> Component.literal(
                "[rl] thread dump written to config/eftlm_stylish/thread_dump_*.txt"), false);
        return 1;
    }

    /**
     * P5.7：热改自动调度倍率上限（/rl autotick max 4.0）——无需重启，
     * 供性能探测/容量测试逐步提高加速档位。
     */
    private static int autoTickMax(CommandContext<CommandSourceStack> ctx) {
        float v = com.mojang.brigadier.arguments.FloatArgumentType.getFloat(ctx, "value");
        RlConfig.ensureLoaded();
        RlConfig.autoTickrateMax = Math.max(RlConfig.autoTickrateMin, Math.min(10.0F, v));
        org.eftlm.stylish.util.AutoTicker.setRange(
                RlConfig.autoTickrateMin, RlConfig.autoTickrateMax, RlConfig.autoTickrateStep);
        ctx.getSource().sendSuccess(() -> Component.literal(
                "[rl] autotick max -> " + RlConfig.autoTickrateMax
                        + " (auto range " + RlConfig.autoTickrateMin + "~" + RlConfig.autoTickrateMax + ")"), false);
        return 1;
    }

    /**
     * P5.7：热改自动调度倍率下限（/rl autotick min 1.0）。
     */
    private static int autoTickMin(CommandContext<CommandSourceStack> ctx) {
        float v = com.mojang.brigadier.arguments.FloatArgumentType.getFloat(ctx, "value");
        RlConfig.ensureLoaded();
        RlConfig.autoTickrateMin = Math.max(0.1F, Math.min(10.0F, v));
        if (RlConfig.autoTickrateMax < RlConfig.autoTickrateMin) {
            RlConfig.autoTickrateMax = RlConfig.autoTickrateMin;
        }
        org.eftlm.stylish.util.AutoTicker.setRange(
                RlConfig.autoTickrateMin, RlConfig.autoTickrateMax, RlConfig.autoTickrateStep);
        ctx.getSource().sendSuccess(() -> Component.literal(
                "[rl] autotick min -> " + RlConfig.autoTickrateMin
                        + " (auto range " + RlConfig.autoTickrateMin + "~" + RlConfig.autoTickrateMax + ")"), false);
        return 1;
    }

    /**
     * P5.6：游戏刻加速热调（/rl tickrate 1.5 → 30 TPS；服务端虚拟时钟）。
     * 手动设置会暂停自动调度（manual 优先），/rl autotick on 恢复。
     */
    private static int tickrate(CommandContext<CommandSourceStack> ctx) {
        float m = com.mojang.brigadier.arguments.FloatArgumentType.getFloat(ctx, "multiplier");
        org.eftlm.stylish.util.TickAccelerator.setMultiplier(m);
        org.eftlm.stylish.util.AutoTicker.pauseForManual();
        ctx.getSource().sendSuccess(() -> Component.literal(
                "[rl] tickrate -> " + m + " (" + Math.round(20 * m) + " TPS"
                        + (org.eftlm.stylish.util.TickAccelerator.active() ? ", virtual clock active" : ", client/no-op")
                        + ", auto scheduler paused)"), false);
        return 1;
    }

    /**
     * P5.6：自适应学习状态查询（敌方节奏统计 / 命中经验桶 / 增益窃取 / 规则模式）。
     */
    private static int adaptive(CommandContext<CommandSourceStack> ctx) {
        RlConfig.ensureLoaded();
        ctx.getSource().sendSuccess(() -> Component.literal(CombatLibrary.status()), false);
        return 1;
    }

    /**
     * P3：导出当前稳定技能槽布局（技能 id 列表，按稳定排序）——训练侧语义对齐依据
     * （iterate 经 RCON 拉取，标签重映射：技能 id → 布局槽位索引）。
     * 布局与主手武器相关：以 arena 配置的主手为参考（竞技场数据主导训练）。
     */
    private static int layout(CommandContext<CommandSourceStack> ctx) {
        RlConfig.ensureLoaded();
        StringBuilder sb = new StringBuilder("[rl] layout v2:");
        sb.append("\n  generic:");
        for (int i = 0; i < RlActEvent.NUM_ACTIONS; i++) {
            sb.append(' ').append(i).append('=').append(GenericCombatExecutor.LABELS[i]);
        }
        sb.append("\n  defense:");
        sb.append(" 62=dodge_step 63=blade_clash");
        sb.append("\n  skills:");
        // 参考布局：arena 主手武器目录（yamato 优先）技能按 rank+id 稳定排序截断
        java.util.List<org.eftlm.stylish.compat.efn.SkillSpec> skills = new java.util.ArrayList<>();
        var catalog = org.eftlm.stylish.compat.efn.EfnSkillCatalog.get();
        var entry = catalog.entryByDir("yamato");
        if (entry == null) {
            entry = catalog.entryByDir("dmc_yamato");
        }
        if (entry != null) {
            skills.addAll(entry.skills());
        } else {
            // 回退：全部武器目录技能按 rank+id 稳定排序的前 MAX_SKILL_SLOTS 个
            for (org.eftlm.stylish.compat.efn.EfnSkillCatalog.WeaponEntry we : catalog.entries().values()) {
                skills.addAll(we.skills());
            }
        }
        skills.sort(java.util.Comparator.comparingInt(org.eftlm.stylish.compat.efn.EfnSkillCatalog::rank)
                .thenComparing(org.eftlm.stylish.compat.efn.SkillSpec::id));
        if (skills.size() > RlActEvent.MAX_SKILL_SLOTS) {
            skills = skills.subList(0, RlActEvent.MAX_SKILL_SLOTS);
        }
        int idx = 0;
        for (var spec : skills) {
            sb.append(' ').append(RlActEvent.ACT_SKILL_BASE + idx++).append('=').append(spec.id());
        }
        ctx.getSource().sendSuccess(() -> Component.literal(sb.toString()), false);
        return 1;
    }

    private static int setTrace(CommandContext<CommandSourceStack> ctx, boolean on) {
        RlConfig.ensureLoaded();
        RlConfig.traceEnabled = on;
        ctx.getSource().sendSuccess(() -> Component.literal("[rl] trace_enabled=" + on), false);
        return 1;
    }

    private static int setShadow(CommandContext<CommandSourceStack> ctx, boolean on) {
        RlConfig.ensureLoaded();
        RlConfig.shadowMode = on;
        ctx.getSource().sendSuccess(() -> Component.literal("[rl] shadow_mode=" + on
                + " (model inference recorded, actions not executed; behavior table takes over)"), false);
        return 1;
    }

    private static int dump(CommandContext<CommandSourceStack> ctx, String target) {
        int written = RlTrace.dump(target);
        ctx.getSource().sendSuccess(() -> Component.literal("[rl] trace dump: " + written
                + " file(s) -> config/eftlm_stylish/dumps/"), false);
        return written > 0 ? 1 : 0;
    }

    private static int reload(CommandContext<CommandSourceStack> ctx) {
        boolean ok = RlBrain.reloadModel();
        RlModel m = RlBrain.currentModel();
        String msg = ok && m != null
                ? String.format("[rl] model reloaded: input=%d output=%d layers=%d",
                        m.getInputDim(), m.getOutputDim(), m.getLayerCount())
                : "[rl] model reload FAILED, rule fallback active (check [RL] logs)";
        ctx.getSource().sendSuccess(() -> Component.literal(msg), false);
        return ok ? 1 : 0;
    }

    private static int status(CommandContext<CommandSourceStack> ctx) {
        RlConfig.ensureLoaded();
        StringBuilder sb = new StringBuilder("[rl] status:");
        RlModel m = RlBrain.currentModel();
        if (m != null) {
            sb.append(String.format("\n  model: input=%d output=%d layers=%d", m.getInputDim(), m.getOutputDim(), m.getLayerCount()));
        } else {
            sb.append("\n  model: NOT LOADED (rule fallback)");
        }
        try {
            if (Files.exists(RlConfig.modelPath())) {
                sb.append("\n  model file: ").append(RlConfig.modelPath().getFileName())
                        .append(" (").append(Files.size(RlConfig.modelPath())).append(" bytes)");
            }
        } catch (Exception ignored) {
        }
        sb.append("\n  config: enable_all_maids=").append(RlConfig.enableAllMaids)
                .append(" epsilon=").append(RlConfig.epsilon)
                .append(" arbitration=").append(RlConfig.arbitration)
                .append(" trace=").append(RlConfig.traceEnabled)
                .append(" shadow=").append(RlConfig.shadowMode)
                .append(" slot_stable=").append(RlConfig.slotStable);
        sb.append("\n  commitment: cached_frames=").append(CommitmentCatalog.cacheSize());
        sb.append("\n  executors: ");
        for (RlActionExecutor ex : RlActionRegistry.executors()) {
            sb.append(ex.id()).append(" ");
        }
        ctx.getSource().sendSuccess(() -> Component.literal(sb.toString()), false);
        return 1;
    }
}
