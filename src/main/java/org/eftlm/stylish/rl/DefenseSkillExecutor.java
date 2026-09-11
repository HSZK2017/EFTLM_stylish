package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import org.eftlm.stylish.compat.efn.SkillSpec;
import org.eftlm.stylish.strategy.CombatActions;
import org.eftlm.stylish.strategy.StyleState;

import java.util.ArrayList;
import java.util.List;

/**
 * 防守状态机技能执行器：把“灵动步伐”和“刀光剑影”作为 RL 技能池槽位，
 * 由 RL 模型决定是否释放。
 *
 * <ul>
 *     <li>dodge_step：参考 EFTLM 灵动步伐，根据敌方攻击朝向选择前/后/左/右闪避。</li>
 *     <li>blade_clash：参考 EFTLM 刀光剑影，进入弹反/格挡姿态，由 MaidAttack 窗口结算正面可格挡攻击。</li>
 * </ul>
 *
 * 注册在 EfnSkillExecutor 之后，因此追加在技能池末尾；不改变已有 EFN 技能槽位顺序。
 */
public final class DefenseSkillExecutor implements RlActionExecutor {

    public static final String ID = "defense";

    private static final int DODGE_COOLDOWN = 10;
    private static final int BLADE_CLASH_COOLDOWN = 40;

    @Override
    public String id() {
        return ID;
    }

    /**
     * 防守技能可用性（2026-09-10）：冷却中以 {@code null} 占位。
     * 旧实现无条件提供两个槽位，而 {@link #canExecute} 又会因冷却拒绝 → 实测
     * dodge_step 16/16 全被拒；这些步仍按"模型选择"进入训练（rejected_high 的一部分）。
     * 注意必须占位而非跳过：{@code RlActionRegistry.buildLayout} 按顺序写入稳定防守槽。
     */
    @Override
    public List<RlActionSlot> available(MaidPatch<?> patch, int tick) {
        List<RlActionSlot> slots = new ArrayList<>(2);
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        // 2026-09-10：dodge_step 走的是 CombatActions.dodgeFromAttack，它会因**耐力不足**直接失败
        // （现场实测 37/37 被拒）——可用性判定必须与执行条件一致，否则又是"提供了却执行不了"。
        boolean dodgeReady = tick - StyleState.getTick(maid, StyleState.LAST_DODGE) >= DODGE_COOLDOWN
                && CombatActions.canDodge(patch);
        boolean clashReady = tick - StyleState.getTick(maid, StyleState.LAST_DEFENSE_SKILL) >= BLADE_CLASH_COOLDOWN;
        slots.add(dodgeReady ? RlActionSlot.skill(ID, 0,
                new SkillSpec("dodge_step", "ef_tlm:dodge_step", DODGE_COOLDOWN, 1,
                        SkillSpec.Condition.NONE, SkillSpec.Gate.NONE)) : null);
        slots.add(clashReady ? RlActionSlot.skill(ID, 1,
                new SkillSpec("blade_clash", "ef_tlm:blade_clash", BLADE_CLASH_COOLDOWN, 1,
                        SkillSpec.Condition.NONE, SkillSpec.Gate.NONE)) : null);
        return slots;
    }

    @Override
    public boolean canExecute(MaidPatch<?> patch, RlActionSlot slot) {
        if (slot == null || slot.skill() == null) {
            return false;
        }
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        int tick = maid.tickCount;
        return switch (slot.localId()) {
            case 0 -> tick - StyleState.getTick(maid, StyleState.LAST_DODGE) >= DODGE_COOLDOWN;
            case 1 -> tick - StyleState.getTick(maid, StyleState.LAST_DEFENSE_SKILL) >= BLADE_CLASH_COOLDOWN;
            default -> false;
        };
    }

    @Override
    public RlExecResult execute(MaidPatch<?> patch, RlActionSlot slot) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        return switch (slot.localId()) {
            case 0 -> {
                if (!CombatActions.dodgeFromAttack(patch)) {
                    yield RlExecResult.REJECTED_BUSY;
                }
                StyleState.setTick(maid, StyleState.LAST_DODGE, maid.tickCount);
                yield RlExecResult.EXECUTED;
            }
            case 1 -> {
                StyleState.setTick(maid, StyleState.LAST_DEFENSE_SKILL, maid.tickCount);
                // 刀光剑影：进入弹反/格挡姿态，后续正面可格挡攻击由 MaidAttack 窗口结算
                StyleState.setTick(maid, StyleState.LAST_PARRY, maid.tickCount);
                StyleState.setTick(maid, StyleState.BLOCK_START, maid.tickCount);
                CombatActions.parry(patch);
                yield RlExecResult.EXECUTED;
            }
            default -> RlExecResult.REJECTED_INVALID;
        };
    }
}
