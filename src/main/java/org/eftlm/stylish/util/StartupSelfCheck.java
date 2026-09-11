package org.eftlm.stylish.util;

import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eftlm.stylish.EFTLMStylish;
import org.eftlm.stylish.rl.RlActEvent;
import org.eftlm.stylish.rl.RlBrain;
import org.eftlm.stylish.rl.RlConfig;
import org.eftlm.stylish.rl.RlState;

/**
 * 启动期自检（P1 修复 2026-09-10）：把"静默失效"变成可观测的日志。
 * <p>
 * 背景：本模组的失败模式大量是"配置/注入没生效但没人知道"——
 * mixin 走 {@code required:false} + {@code @Pseudo} + IMixinConnector 提前注册，
 * 时钟虚拟化的 {@code TickrateMixin} 一旦没应用，倍率、P0/P5.7 的一整套能力都会静默消失；
 * 模型维度/路径写错则整轮训练退化为规则策略；奖励/动作契约写错只在训练指标上体现。
 * 因此在服务器启动完成时做一次便宜的自检并明确打印结论。
 */
@Mod.EventBusSubscriber(modid = EFTLMStylish.MODID)
public final class StartupSelfCheck {

    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    private StartupSelfCheck() {
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        try {
            checkTickAcceleration();
            checkModelContract();
            checkClientMixins();
        } catch (Throwable t) {
            LOGGER.error("[SELFCHECK] startup self-check crashed (non-fatal)", t);
        }
    }

    /**
     * 时钟虚拟化自检：倍率 != 1.0 时，{@code TickAccelerator.virtualNow()} 必须与
     * {@code Util.getMillis()} 明显不同；否则说明 TickrateMixin 没有生效（加速名存实亡）。
     */
    private static void checkTickAcceleration() {
        if (!TickAccelerator.active()) {
            LOGGER.info("[SELFCHECK] tick acceleration inactive (client or not initialized)");
            return;
        }
        float mult = TickAccelerator.multiplier();
        long raw = net.minecraft.Util.getMillis();
        long virt = TickAccelerator.virtualNow();
        if (Math.abs(mult - 1.0F) < 1.0E-4F) {
            LOGGER.info("[SELFCHECK] tick multiplier=1.0 (virtual clock == vanilla clock, {}ms)", raw);
            return;
        }
        long expected = Math.round(raw * mult);
        if (virtualNowIsScaled(raw, mult, virt)) {
            LOGGER.info("[SELFCHECK] tick acceleration OK: multiplier={} virtualNow={} raw={} (~{}x)",
                    mult, virt, raw, expected == 0 ? 0 : virt / Math.max(1L, raw));
        } else {
            LOGGER.error("[SELFCHECK] tick acceleration NOT effective: multiplier={} but virtualNow={} == raw={} "
                            + "→ TickrateMixin 可能未应用（检查 eftlm_stylish.mixins.json 是否包含 TickrateMixin）",
                    mult, virt, raw);
        }
    }

    private static boolean virtualNowIsScaled(long raw, float mult, long virtual) {
        // 允许 ±10% + 1ms 误差（round/时钟前进导致）
        long expected = Math.round(raw * mult);
        long tolerance = Math.max(2L, Math.round(Math.abs(expected) * 0.10));
        return Math.abs(virtual - expected) <= tolerance;
    }

    /** 模型契约自检：模型是否加载成功、维度是否与 RL 契约一致。 */
    private static void checkModelContract() {
        RlConfig.ensureLoaded();
        var model = RlBrain.currentModel();
        if (model == null) {
            LOGGER.warn("[SELFCHECK] no RL model loaded → rule fallback active "
                            + "(model_file={}, path={})", RlConfig.modelFile, RlConfig.modelPath());
            return;
        }
        boolean dimOk = model.getInputDim() == RlState.STATE_DIM
                || model.getInputDim() == RlState.OLD_STATE_DIM_18
                || model.getInputDim() == RlState.LEGACY_STATE_DIM;
        if (!dimOk || model.getOutputDim() != RlActEvent.TOTAL_ACTIONS) {
            LOGGER.error("[SELFCHECK] model dim mismatch: input={} output={} (need {}/{}/{} and {}) → rule fallback",
                    model.getInputDim(), model.getOutputDim(), RlState.STATE_DIM,
                    RlState.OLD_STATE_DIM_18, RlState.LEGACY_STATE_DIM, RlActEvent.TOTAL_ACTIONS);
        } else {
            LOGGER.info("[SELFCHECK] model OK: input={} output={} layers={}", model.getInputDim(),
                    model.getOutputDim(), model.getLayerCount());
        }
    }

    /** 提示哪些第三方 mixin 目标可能不存在（仅信息级，@Pseudo 缺失是正常情况）。 */
    private static void checkClientMixins() {
        LOGGER.info("[SELFCHECK] contracts: state={} actions={} (generic={} + skills={}), "
                        + "itemBlockParry={} blockWeapon={} slotStable={} trace={} shadow={}",
                RlState.STATE_DIM, RlActEvent.TOTAL_ACTIONS, RlActEvent.NUM_ACTIONS,
                RlActEvent.MAX_SKILL_SLOTS, RlConfig.itemBlockParry, RlConfig.itemBlockWeapon,
                RlConfig.slotStable, RlConfig.traceEnabled, RlConfig.shadowMode);
    }
}
