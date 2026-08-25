package org.eftlm.stylish.rl;

import org.eftlm.stylish.compat.efn.SkillSpec;

import javax.annotation.Nullable;

/**
 * RL 行动槽：某个注册执行器在决策点贡献的一个可执行行动单元。
 * <p>
 * 通用行动：localId = 行动编号（0..{@link RlActEvent#NUM_ACTIONS}-1），skill 为 null；
 * 技能槽位行动：skill 为目录 {@link SkillSpec}（执行器按自身规则释放）。
 * 全局编号由 {@link RlActionRegistry#buildLayout} 组装时确定（固定段 + 技能池）。
 */
public record RlActionSlot(String executorId, int localId, @Nullable SkillSpec skill, String label) {

    /** 通用行动槽（内置通用战斗执行器） */
    public static RlActionSlot generic(int actionId, String label) {
        return new RlActionSlot(GenericCombatExecutor.ID, actionId, null, label);
    }

    /** 技能槽位行动槽 */
    public static RlActionSlot skill(String executorId, int localId, SkillSpec spec) {
        return new RlActionSlot(executorId, localId, spec, spec.id());
    }
}
