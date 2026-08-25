package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraftforge.eventbus.api.Event;

/**
 * RL 行动执行结果事件：执行器实施 {@link RlActEvent} 后由分发器发出，
 * 是"执行结果反哺 RL"（闭环）的反馈通道——{@link RlFeedback} 订阅并把
 * 执行状况写入女仆状态（传感器特征 s[16]/s[17]）与奖励塑形。
 * 外部模组同样可以订阅本事件观测/审计 RL 决策执行状况。
 */
public class RlExecResultEvent extends Event {

    private final EntityMaid maid;
    private final int action;
    private final RlExecResult result;
    private final RlActionSlot slot;

    public RlExecResultEvent(EntityMaid maid, int action, RlExecResult result, RlActionSlot slot) {
        this.maid = maid;
        this.action = action;
        this.result = result;
        this.slot = slot;
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public int getAction() {
        return action;
    }

    public RlExecResult getExecResult() {
        return result;
    }

    public RlActionSlot getSlot() {
        return slot;
    }
}
