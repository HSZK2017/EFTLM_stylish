package org.eftlm.stylish.compat.efn;

/**
 * 武器技能描述（SkillSpec）：由 skills.json（tools/extract_efn_skills.ps1 从 EFN
 * jar 动画资产提取生成）解析出的单条技能条目。
 * <p>
 * 动画键（{@link #animKey}）为完整 ResourceLocation（如
 * {@code efn:biped/yamato/dmcyamato_drive}），运行时经 {@link EfnSkillCatalog} 从
 * AnimationManager 解析，未注册（EFN 未安装 / 版本不符）时解析结果为 null，
 * 该技能自动不可用。
 */
public final class SkillSpec {

    /** 释放条件（本模组战斗谓词，与 EFN 玩家输入条件无关） */
    public enum Condition {
        MELEE, MID_RANGE, AIRBORNE, NONE;

        public static Condition of(String name) {
            try {
                return valueOf(name.toUpperCase());
            } catch (Exception e) {
                return NONE;
            }
        }
    }

    /** 资源门控：EFT LM 层数（BehaviorsBuild）/ 本模组层数（StyleState）/ 无门控 */
    public enum Gate {
        OWN_STACK, EFTLM_STACK, NONE;

        public static Gate of(String name) {
            try {
                return valueOf(name.toUpperCase());
            } catch (Exception e) {
                return OWN_STACK;
            }
        }
    }

    private final String id;
    private final String animKey;
    private final int cooldownTicks;
    private final int cost;
    private final Condition condition;
    private final Gate gate;

    public SkillSpec(String id, String animKey, int cooldownTicks, int cost, Condition condition, Gate gate) {
        this.id = id;
        this.animKey = animKey;
        this.cooldownTicks = Math.max(1, cooldownTicks);
        this.cost = Math.max(1, cost);
        this.condition = condition;
        this.gate = gate;
    }

    public String id() {
        return id;
    }

    public String animKey() {
        return animKey;
    }

    public int cooldownTicks() {
        return cooldownTicks;
    }

    public int cost() {
        return cost;
    }

    public Condition condition() {
        return condition;
    }

    public Gate gate() {
        return gate;
    }

    @Override
    public String toString() {
        return id + "[" + animKey + ", cd=" + cooldownTicks + ", cond=" + condition + ", gate=" + gate + "]";
    }
}
