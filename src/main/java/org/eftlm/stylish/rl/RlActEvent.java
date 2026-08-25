package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraftforge.eventbus.api.Event;
import org.eftlm.stylish.compat.efn.SkillSpec;

import javax.annotation.Nullable;

/**
 * RL 行动事件：强化学习把预测概率最高的行动发布到 Forge 事件总线，
 * 由 {@link RlActionRegistry} 分发给对应执行状态机实施
 * （武器切换 / 攻击招式 / 防守 / 武器技能）。
 * <p>
 * 行动空间（与训练脚本契约）：
 * 0..{@link #NUM_ACTIONS}-1 为通用行动（内置通用战斗执行器）；
 * {@link #ACT_SKILL_BASE} 起为技能槽位（决策点由 {@link RlActionRegistry#buildLayout}
 * 组装，槽位对应的具体技能经 {@link #getSlot()} 携带）。
 * 训练脚本动作数必须等于 {@link #TOTAL_ACTIONS}。
 * <p>
 * 类 Agent 模式：事件只携带行动槽描述（executorId + SkillSpec），
 * 具体实施由注册表中对应执行器完成；执行结果经 {@link RlExecResultEvent} 反哺。
 */
public class RlActEvent extends Event {
    /** 通用行动编号（与训练脚本一致） */
    public static final int ACT_IDLE = 0;
    public static final int ACT_SWORDMASTER_ATK = 1;
    public static final int ACT_GUNSLINGER_ATK = 2;
    public static final int ACT_ULTIMATE = 3;
    public static final int ACT_JC = 4;
    public static final int ACT_PARRY = 5;
    public static final int ACT_BLOCK = 6;
    public static final int ACT_DODGE = 7;
    public static final int ACT_ROLL = 8;
    public static final int ACT_CYCLE_MELEE = 9;
    public static final int ACT_RANGED = 10;
    /** 通用行动数量 */
    public static final int NUM_ACTIONS = 11;
    /** 技能槽位起始行动编号（>= 该值的行动 = 执行器技能池槽位） */
    public static final int ACT_SKILL_BASE = NUM_ACTIONS;
    /** 技能槽位上限（模型输出维度 = NUM_ACTIONS + MAX_SKILL_SLOTS） */
    public static final int MAX_SKILL_SLOTS = 53;
    /** 总行动数（训练脚本动作数） */
    public static final int TOTAL_ACTIONS = NUM_ACTIONS + MAX_SKILL_SLOTS;

    private final EntityMaid maid;
    private final int action;
    private final float[] state;
    /** 行动槽（执行器 + 技能）；通用行动携带 generic 槽，技能槽位携带 SkillSpec */
    @Nullable
    private final RlActionSlot slot;

    public RlActEvent(EntityMaid maid, int action, float[] state) {
        this(maid, action, state, null);
    }

    public RlActEvent(EntityMaid maid, int action, float[] state, @Nullable RlActionSlot slot) {
        this.maid = maid;
        this.action = action;
        this.state = state;
        this.slot = slot;
    }

    public EntityMaid getMaid() {
        return maid;
    }

    public int getAction() {
        return action;
    }

    public float[] getState() {
        return state;
    }

    @Nullable
    public RlActionSlot getSlot() {
        return slot;
    }

    /** 技能槽位行动对应的技能（通用行动为 null）；兼容旧调用方 */
    @Nullable
    public SkillSpec getSkill() {
        return slot != null ? slot.skill() : null;
    }
}
