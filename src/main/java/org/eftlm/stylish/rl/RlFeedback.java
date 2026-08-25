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

    @SubscribeEvent
    public static void onExecResult(RlExecResultEvent event) {
        EntityMaid maid = event.getMaid();
        if (maid.level().isClientSide()) {
            return;
        }
        int tick = maid.tickCount;
        switch (event.getExecResult()) {
            case EXECUTED -> StyleState.setTick(maid, StyleState.LAST_EXEC_OK, tick);
            case REJECTED_BUSY -> StyleState.setTick(maid, StyleState.LAST_EXEC_REJECTED, tick);
            case REJECTED_INVALID, FAILED -> {
                StyleState.setTick(maid, StyleState.LAST_EXEC_REJECTED, tick);
                RlDataRecorder.addReward(maid, -2); // 无效/失败决策惩罚
            }
            default -> {
                // NOOP（IDLE 让位行为表）不写反馈
            }
        }
    }
}
