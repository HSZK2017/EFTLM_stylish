package org.eftlm.stylish.rl;

import net.EFTLM.EF.Capability.MaidPatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eftlm.stylish.compat.efn.EfnSkillCatalog;
import org.eftlm.stylish.compat.efn.SkillSpec;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.HitAnimation;
import yesman.epicfight.api.animation.types.KnockdownAnimation;
import yesman.epicfight.api.animation.types.LongHitAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;

import java.util.HashMap;
import java.util.Map;

/**
 * P2 动作语义化：Commitment 目录（报告 3.6）——把技能的时序承诺（前摇/判定/后摇/
 * 可打断性）从运行时动画对象读入统一帧数据结构，供执行器门控与决策观测使用。
 * <p>
 * 数据源（报告 3.6 映射表，无需反编译）：
 * <ul>
 *     <li>EFN/WOM 技能动画运行时是 {@code AvalonAttackAnimation}（继承 EpicFight
 *         {@link AttackAnimation}），其 {@code Phase}（start/preDelay/contact/end，
 *         秒）经 {@link AttackAnimation#getPhaseByTime} 公开读取；</li>
 *     <li>可打断性读 {@code AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE}
 *         （EpicFight 原版普攻默认 true；Avalon/EFN 系构造器显式置 false =
 *         "不可打断"约定的来源，报告 1.3）；</li>
 *     <li>读取失败/非攻击动画 → 保守默认（前摇 0.3s / 判定 0.3s / 后摇 0.5s /
 *         不可打断），skills.json 的 commit 覆盖层留待人工校准（报告 3.6）。</li>
 * </ul>
 * 门控查询：{@link #canExecuteNow} 替换执行器"inaction() 一刀切"——空闲可执行、
 * 动画即将结束（剩余 ≤ 2 tick）可执行、受击/倒地动画可执行、紧急动作（翻滚/闪避/
 * 弹反）直放；其余（长动画活性帧）拒绝（由 RL 反馈 s[17] 学习择时）。
 */
public final class CommitmentCatalog {

    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    /** 统一帧数据结构（秒；tick = ×20） */
    public record SkillFrameData(String animKey, float windupSec, float activeSec,
                                 float recoverySec, boolean cancelableMove) {
        public int windupTicks() {
            return Math.max(1, Math.round(windupSec * 20));
        }

        public int activeTicks() {
            return Math.max(1, Math.round(activeSec * 20));
        }

        public int recoveryTicks() {
            return Math.max(1, Math.round(recoverySec * 20));
        }

        @Override
        public String toString() {
            return animKey + " w=" + windupSec + "s a=" + activeSec + "s r=" + recoverySec
                    + "s cancel=" + cancelableMove;
        }
    }

    /** 技能 id → 帧数据缓存（启动/技能目录加载时惰性构建） */
    private static final Map<String, SkillFrameData> CACHE = new HashMap<>();

    /** 动画即将结束阈值（tick）：剩余帧 ≤ 该值视为可执行 */
    private static final int NEAR_END_TICKS = 2;

    private CommitmentCatalog() {
    }

    /** 查询技能帧数据（惰性构建缓存） */
    public static SkillFrameData of(SkillSpec spec) {
        return CACHE.computeIfAbsent(spec.id(), k -> read(spec));
    }

    /** 缓存条目数（诊断用） */
    public static int cacheSize() {
        return CACHE.size();
    }

    /** 清空缓存（技能目录热重载后调用） */
    public static void invalidate() {
        CACHE.clear();
    }

    /**
     * 从运行时动画对象读取帧数据。
     */
    private static SkillFrameData read(SkillSpec spec) {
        try {
            AssetAccessor<? extends StaticAnimation> acc = EfnSkillCatalog.resolve(spec);
            if (acc != null && acc.get() instanceof AttackAnimation attack) {
                AttackAnimation.Phase p = attack.getPhaseByTime(0F);
                float windup = Math.max(0F, p.preDelay - p.start);
                float active = Math.max(0F, p.contact - p.preDelay);
                // 多 phase 动画的 end 可能是 Float.MAX_VALUE：后摇截断到 3 秒保守值
                float end = Float.isFinite(p.end) ? p.end : p.contact + 3F;
                float recovery = Math.max(0F, end - p.contact);
                boolean cancel = attack.getProperty(
                        AnimationProperty.ActionAnimationProperty.CANCELABLE_MOVE).orElse(true);
                SkillFrameData d = new SkillFrameData(spec.animKey(), windup, active, recovery, cancel);
                LOGGER.debug("[COMMIT] {} -> {}", spec.id(), d);
                return d;
            }
        } catch (Throwable t) {
            LOGGER.debug("[COMMIT] read failed for {}: {}", spec.id(), t.toString());
        }
        // 保守默认：前摇 0.3s / 判定 0.3s / 后摇 0.5s / 不可打断
        return new SkillFrameData(spec.animKey(), 0.3F, 0.3F, 0.5F, false);
    }

    /** 当前动画剩余帧（tick；-1 = 无法解析/无动画） */
    public static int remainingTicks(MaidPatch<?> patch) {
        try {
            AnimationPlayer player = patch.getAnimator().getPlayerFor(null);
            if (player == null || player.getAnimation() == null || player.getAnimation().get() == null) {
                return -1;
            }
            float remain = player.getAnimation().get().getTotalTime() - player.getElapsedTime();
            if (remain <= 0F) {
                return 0;
            }
            return Math.max(1, Math.round(remain * 20F));
        } catch (Throwable t) {
            return -1;
        }
    }

    /**
     * Commitment 门控（P2 执行器 canExecute 统一入口）：
     * <ol>
     *     <li>空闲（非 inaction）→ 可执行；</li>
     *     <li>紧急动作（翻滚/闪避/弹反）→ 可执行（打断保护例外，与现状一致）；</li>
     *     <li>当前动画剩余 ≤ {@link #NEAR_END_TICKS} → 可执行（避免 1 帧之差的误拒）；</li>
     *     <li>受击/倒地动画 → 可执行（覆盖播放脱离受控是常态）；</li>
     *     <li>其余（长动画活性/后摇帧）→ 拒绝（RL 反馈 s[17] 学习择时）。</li>
     * </ol>
     */
    public static boolean canExecuteNow(MaidPatch<?> patch, boolean urgent) {
        try {
            if (!patch.getEntityState().inaction()) {
                return true;
            }
            if (urgent) {
                return true;
            }
            int remain = remainingTicks(patch);
            if (remain >= 0 && remain <= NEAR_END_TICKS) {
                return true;
            }
            AssetAccessor<? extends StaticAnimation> real = patch.getAnimator().getPlayerFor(null).getRealAnimation();
            if (real != null && real.get() != null) {
                StaticAnimation anim = real.get();
                if (anim instanceof HitAnimation || anim instanceof LongHitAnimation
                        || anim instanceof KnockdownAnimation) {
                    return true; // 受控/受击动画可覆盖
                }
            }
            return false;
        } catch (Throwable t) {
            return false;
        }
    }
}
