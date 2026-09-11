package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.eftlm.stylish.EFTLMStylish;
import org.eftlm.stylish.strategy.StyleState;

/**
 * RL 执行反馈闭环：订阅 {@link RlExecResultEvent}，
 * <ul>
 *     <li>把执行状况写入女仆状态键（{@link StyleState#LAST_EXEC_OK} /
 *         {@link StyleState#LAST_EXEC_REJECTED}），由 {@link RlState} 编码为
 *         传感器特征 s[16]/s[17]（执行结果反哺 RL 状态输入）；</li>
 *     <li>无效 / 失败的决策给轻微负奖励（模型学会在可执行时机出招；
 *         "忙"是正常时序，只记特征不惩罚）。</li>
 * </ul>
 */
@Mod.EventBusSubscriber(modid = EFTLMStylish.MODID)
public class RlFeedback {

    /** 每女仆的执行结果计数 [ok, rejected_busy, rejected_invalid/failed]（取样后清空，用于心跳打印拒绝率） */
    private static final java.util.Map<java.util.UUID, int[]> EXEC_STATS = new java.util.HashMap<>();

    /** 取出并清空该女仆的执行计数（无记录时返回全 0） */
    public static int[] drainExecStats(EntityMaid maid) {
        int[] v = EXEC_STATS.remove(maid.getUUID());
        return v != null ? v : new int[3];
    }

    /** 女仆移除时清理计数（P1：静态状态收口） */
    public static void forget(java.util.UUID id) {
        EXEC_STATS.remove(id);
    }

    private static void count(EntityMaid maid, int idx) {
        int[] v = EXEC_STATS.computeIfAbsent(maid.getUUID(), k -> new int[3]);
        if (idx >= 0 && idx < v.length) {
            v[idx]++;
        }
    }

    @SubscribeEvent
    public static void onExecResult(RlExecResultEvent event) {
        EntityMaid maid = event.getMaid();
        if (maid.level().isClientSide()) {
            return;
        }
        int tick = maid.tickCount;
        switch (event.getExecResult()) {
            case EXECUTED -> {
                StyleState.setTick(maid, StyleState.LAST_EXEC_OK, tick);
                count(maid, 0);
            }
            case REJECTED_BUSY -> {
                StyleState.setTick(maid, StyleState.LAST_EXEC_REJECTED, tick);
                count(maid, 1);
                // P5.7 拒绝标签修正：被"忙"拒绝的决策实际未执行 → 轨迹标签改写为 idle
                // （避免"忙时输出攻击"被当作正样本训练 → 拒绝率恶性循环；详见 RlDataRecorder）
                RlDataRecorder.rewriteLastStepLabel(maid, event.getAction(), "idle");
            }
            case REJECTED_INVALID, FAILED -> {
                StyleState.setTick(maid, StyleState.LAST_EXEC_REJECTED, tick);
                count(maid, 2);
                // P0 修复（2026-09-10）：这是"本次决策本身无效"的惩罚（动作刚被拒），
                // 属于本步塑形奖励 → addStepReward；事件后果类奖励（命中/受击）才走 addReward。
                RlDataRecorder.addStepReward(maid, -2);
                RlDataRecorder.rewriteLastStepLabel(maid, event.getAction(), "idle");
            }
            default -> {
                // NOOP（IDLE 让位行为表）不写反馈
            }
        }
    }
}
