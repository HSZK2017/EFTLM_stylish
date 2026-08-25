package org.eftlm.stylish.strategy;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import org.eftlm.stylish.util.AnimKit;

/**
 * 华丽连段状态：全部持久化在女仆 persistentData 中，随女仆存档保存。
 * <p>
 * 风格 / 华丽度 / 连段计数等状态由 CombatBehaviors 行为表的 custom 谓词与
 * behavior 回调读写，由本类统一封装。
 */
public final class StyleState {

    // ------------------------------------------------------------------
    // 键
    // ------------------------------------------------------------------
    public static final String STYLE = "eftlm_stylish:style";
    public static final String FLAIR = "eftlm_stylish:flair";
    public static final String COMBO = "eftlm_stylish:combo";
    public static final String LAST_PARRY = "eftlm_stylish:last_parry";
    public static final String BLOCK_START = "eftlm_stylish:block_start";
    public static final String LAST_HIT = "eftlm_stylish:last_hit";
    public static final String LAST_ROLL = "eftlm_stylish:last_roll";
    public static final String LAST_DODGE = "eftlm_stylish:last_dodge";
    public static final String LAST_DEFENSE_SKILL = "eftlm_stylish:last_defense_skill";
    public static final String LAST_SWAP = "eftlm_stylish:last_swap";
    /** 近战武器轮换冷却（与距离/远程切换的 LAST_SWAP 分离，避免远程循环阻塞轮换） */
    public static final String LAST_MELEE_SWAP = "eftlm_stylish:last_melee_swap";
    public static final String LAST_CYCLE = "eftlm_stylish:last_cycle";
    public static final String FINISHER = "eftlm_stylish:finisher";
    public static final String FINISHER_START = "eftlm_stylish:finisher_start";
    public static final String FINISHER_SWAPPED = "eftlm_stylish:finisher_swapped";
    /** 行为表连段系列结束回调置位：连段将结束 → 技能检测后触发枪神收尾 */
    public static final String COMBO_END = "eftlm_stylish:combo_end";
    /** 命中计数：每累计 6 次命中轮换一次近战武器（不依赖行为表，对 EFN/WOM 武器同样生效） */
    public static final String HIT_COUNT = "eftlm_stylish:hit_count";
    /** 命中计数触发轮换的次数 */
    public static final int HIT_COUNT_SWAP = 6;
    /** 目标锁定：记录当前锁定目标的实体 id，防止多目标脱锁跳变 */
    public static final String LOCKED_TARGET = "eftlm_stylish:locked_target";
    /** 箭矢反应冷却：预测到箭矢即将命中后反应的记录 tick */
    public static final String LAST_ARROW_REACT = "eftlm_stylish:last_arrow_react";
    /** 霸体免责期起点：弹反/格挡/闪避成功后一段时间内受击不扣奖励（鼓励贴脸拼刀） */
    public static final String PARRIED_TICK = "eftlm_stylish:parried_tick";
    /** 免责期长度（tick） */
    public static final int PARRY_IMMUNE_TICKS = 60;
    /** 击倒惩罚边沿触发标记：上次结算被击倒惩罚的 tick（击倒动画持续多 tick，只惩罚一次） */
    public static final String LAST_KNOCKDOWN = "eftlm_stylish:last_knockdown";
    /** 击倒惩罚去抖：一次击倒动画内（含倒地到起身）只结算一次 */
    public static final int KNOCKDOWN_PENALTY_COOLDOWN = 60;
    /** RL 执行反馈：上次行动成功执行的 tick（RlState s[16]，10 tick 窗口） */
    public static final String LAST_EXEC_OK = "eftlm_stylish:last_exec_ok";
    /** RL 执行反馈：上次行动被拒（忙/无效/失败）的 tick（RlState s[17]，10 tick 窗口） */
    public static final String LAST_EXEC_REJECTED = "eftlm_stylish:last_exec_rejected";
    /** 技能层数：命中 / 击杀 / 弹反攒层，满层解锁武器技能大招（资源型技能循环） */
    public static final String SKILL_STACK = "eftlm_stylish:skill_stack";
    /** 满层阈值：攒满后下一次出招自动释放武器技能大招 */
    public static final int MAX_SKILL_STACK = 5;

    public static int getSkillStack(EntityMaid maid) {
        return maid.getPersistentData().getInt(SKILL_STACK);
    }

    public static void addSkillStack(EntityMaid maid, int amount) {
        int value = Math.min(MAX_SKILL_STACK, getSkillStack(maid) + amount);
        maid.getPersistentData().putInt(SKILL_STACK, value);
    }

    public static void setSkillStack(EntityMaid maid, int value) {
        maid.getPersistentData().putInt(SKILL_STACK, Math.max(0, Math.min(MAX_SKILL_STACK, value)));
    }

    // ------------------------------------------------------------------
    // 华丽度阈值
    // ------------------------------------------------------------------
    public static final float FLAIR_MAX = 100.0F;
    public static final float FLAIR_D = 10.0F;
    public static final float FLAIR_C = 25.0F;
    public static final float HIT_FLAIR = 3.0F;
    public static final float PARRY_FLAIR = 10.0F;
    public static final float KILL_FLAIR = 20.0F;
    public static final float HURT_FLAIR = -8.0F;

    private StyleState() {
    }

    public static int getStyle(EntityMaid maid) {
        return maid.getPersistentData().getInt(STYLE) == 0 && !maid.getPersistentData().contains(STYLE)
                ? AnimKit.STYLE_SWORDMASTER : maid.getPersistentData().getInt(STYLE);
    }

    public static void setStyle(EntityMaid maid, int style) {
        maid.getPersistentData().putInt(STYLE, style);
    }

    public static void toggleStyle(EntityMaid maid) {
        setStyle(maid, getStyle(maid) == AnimKit.STYLE_SWORDMASTER ? AnimKit.STYLE_GUNSLINGER : AnimKit.STYLE_SWORDMASTER);
    }

    public static float getFlair(EntityMaid maid) {
        return maid.getPersistentData().getFloat(FLAIR);
    }

    public static void addFlair(EntityMaid maid, float amount) {
        float flair = Math.max(0.0F, Math.min(FLAIR_MAX, getFlair(maid) + amount));
        maid.getPersistentData().putFloat(FLAIR, flair);
    }

    public static void setFlair(EntityMaid maid, float value) {
        maid.getPersistentData().putFloat(FLAIR, Math.max(0.0F, Math.min(FLAIR_MAX, value)));
    }

    public static void setInt(EntityMaid maid, String key, int value) {
        maid.getPersistentData().putInt(key, value);
    }

    public static int getInt(EntityMaid maid, String key, int def) {
        return maid.getPersistentData().contains(key) ? maid.getPersistentData().getInt(key) : def;
    }

    public static int getTick(EntityMaid maid, String key) {
        return maid.getPersistentData().getInt(key);
    }

    public static void setTick(EntityMaid maid, String key, int tick) {
        maid.getPersistentData().putInt(key, tick);
    }
}
