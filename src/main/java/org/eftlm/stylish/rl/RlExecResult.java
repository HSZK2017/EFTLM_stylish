package org.eftlm.stylish.rl;

/**
 * RL 行动执行结果（执行反馈反哺通道）：
 * <ul>
 *     <li>{@link #EXECUTED}：行动被状态机接受并执行</li>
 *     <li>{@link #REJECTED_BUSY}：女仆正在动作中，行动被门控拒绝</li>
 *     <li>{@link #REJECTED_INVALID}：行动槽无效（无执行器 / 技能已不可用）</li>
 *     <li>{@link #FAILED}：执行器执行过程异常</li>
 *     <li>{@link #NOOP}：明确的无操作（如 IDLE 让位行为表）</li>
 * </ul>
 */
public enum RlExecResult {
    EXECUTED,
    REJECTED_BUSY,
    REJECTED_INVALID,
    FAILED,
    NOOP
}
