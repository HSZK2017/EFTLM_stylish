package org.eftlm.stylish.rl;

import net.EFTLM.EF.Capability.MaidPatch;

import java.util.List;

/**
 * RL 行动执行状态机接入接口（类 Agent 模式）。
 * <p>
 * 任何新的执行状态机（WOM 枪械状态机、新武器技能状态机等）实现本接口并经
 * {@link RlActionRegistry#register} 注册后，其可用行动即进入 RL 决策点行动空间，
 * RL 输出的行动经 {@link RlActEvent} 在事件总线上分发回该执行器实施；
 * 执行结果经 {@link RlExecResultEvent} 反哺 RL（闭环）。
 * <p>
 * 契约：模型输出维度恒为 {@link RlActEvent#TOTAL_ACTIONS}（11 通用 + 16 技能池），
 * 固定段执行器只允许占用通用段，其余执行器贡献技能池（注册顺序即槽位顺序）。
 */
public interface RlActionExecutor {

    /** 执行器唯一标识（事件分发 / 诊断用） */
    String id();

    /**
     * 决策点贡献的可用行动槽（局部编号，全局编号由注册表组装时分配）。
     * 只返回当前确实可用的行动：条件不满足 / 冷却中 / 动画缺失的行动不应出现。
     */
    List<RlActionSlot> available(MaidPatch<?> patch, int tick);

    /** 执行前最终校验（忙 / 技能失效等），false 时注册表返回被拒结果 */
    boolean canExecute(MaidPatch<?> patch, RlActionSlot slot);

    /** 执行行动并返回结果（服务器线程调用） */
    RlExecResult execute(MaidPatch<?> patch, RlActionSlot slot);
}
