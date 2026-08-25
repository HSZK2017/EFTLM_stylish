package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.phys.Vec3;
import org.eftlm.stylish.strategy.CombatActions;
import org.eftlm.stylish.strategy.WeaponArsenal;
import org.eftlm.stylish.util.AnimKit;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * P1 反应层（每 tick，规则优先）：生存底线，不依赖模型、不依赖 5 tick 决策节拍
 * （报告 3.3 + 1.6.5 闪避子系统 P1 子集）。
 * <p>
 * 抢占优先级从高到低：
 * <ol>
 *     <li><b>受控状态机（起身防御）</b>：击倒结束后的短暂防御窗口内，近身攻击威胁 →
 *         格挡（AV 处决威胁：起身后 4 格内会被处决，见报告 1.5.4）；</li>
 *     <li><b>前摇规避（WINDUP）</b>：目标技能阶段 1（前摇）且剩余帧 ≤ 预算 →
 *         正面近战弹反 / 否则朝攻击反方向闪避（"跃起那一刻闪避"的实现位置）；
 *         WINDUP 数据来自 {@link TargetTracker}（EpicFight patch 直接读 getLevel()）；</li>
 *     <li><b>弹道拦截</b>：{@link ProjectilePerception} 统一弹道（箭矢/火球/珍珠/
 *         钓鱼钩/Avalon 幻影剑）ETA ≤ 0.45s → 可格挡类格挡/弹反，不可格挡侧向闪避
 *         （闪避自带 IGNORE_ALL_PROJECTILES 弹道免疫，见报告 1.3）。</li>
 * </ol>
 * 反应层抢占后设置 busy 标记（{@link #BUSY_TICKS}），使 RL 决策点跳过输出（避免抢动画）；
 * 全部反应动作记录 trace 事件（reactive_*），供 P0 观测回放。
 * <p>
 * 注意：紧急动作（dodge/parry/roll）直接播放动画——与现有 {@code DefenseSkillExecutor}
 * 行为一致（EFN LinkAnimation 竞态豁免依赖播放纪律，见 docs/EFN_NPE豁免技术报告_20260823.md；
 * 紧急动作动画为独立播放路径，与 RL"空闲才播"纪律并行，服务器长期运行零触发）。
 */
public final class ReactiveLayer {

    /** 抢占后让 RL 决策跳过的 tick 数（防双系统抢动画） */
    static final int BUSY_TICKS = 6;
    /** 反应冷却（防连续触发刷动画 / 耐力耗尽） */
    static final int REACT_COOLDOWN = 8;
    /** 前摇近战判定距离（格） */
    static final float WINDUP_MELEE_RANGE = 3.5F;
    /** 弹道拦截 ETA 阈值（秒） */
    static final float PROJECTILE_ETA = 0.45F;
    /** 起身防御窗口（tick） */
    static final int RECOVERY_GUARD_TICKS = 12;

    /** 每女仆反应层状态 */
    record MaidState(int lastReact, int busyUntil, boolean wasKnocked, int recoveryGuardUntil) {
    }

    private static final Map<UUID, MaidState> STATES = new HashMap<>();

    private ReactiveLayer() {
    }

    /**
     * 每 tick 调用（在 RL 决策之前）。返回 true = 反应层接管了本 tick 动作。
     */
    public static boolean tick(MaidPatch<?> patch, EntityMaid maid) {
        int tick = maid.tickCount;
        UUID id = maid.getUUID();
        MaidState st = STATES.computeIfAbsent(id, k -> new MaidState(-100, 0, false, 0));
        boolean knocked = patch.getEntityState().knockDown();

        // 受控状态机：击倒结束 → 进入起身防御窗口
        int guardUntil = st.recoveryGuardUntil();
        if (st.wasKnocked() && !knocked) {
            guardUntil = tick + RECOVERY_GUARD_TICKS;
            RlTrace.event(maid, "recovery_guard_start",
                    "knockdown ended, guard window " + RECOVERY_GUARD_TICKS + "t");
        }
        STATES.put(id, new MaidState(st.lastReact(), st.busyUntil(), knocked, guardUntil));

        // 被击倒：不抢占（由 MaidTick 的击倒处理 rollRecovery 接管）
        if (knocked) {
            return false;
        }
        // 反应冷却
        if (tick - st.lastReact() < REACT_COOLDOWN) {
            return false;
        }

        // ---- 1. 起身防御窗口：近身攻击威胁 → 格挡 ----
        if (tick < guardUntil) {
            TargetTracker.TrackedTarget t = TargetTracker.getNearestSolidTarget(maid);
            if (t != null && t.isAttacking() && t.getDistance() <= 4.0) {
                CombatActions.block(patch);
                react(maid, id, tick, "reactive_recovery_guard",
                        "guard after roll, threat dist=" + String.format("%.1f", t.getDistance()));
                return true;
            }
        }

        // ---- 2. 前摇规避（WINDUP）：弹反优先，否则朝攻击反方向闪避 ----
        TargetTracker.TrackedTarget target = TargetTracker.getNearestSolidTarget(maid);
        if (target != null && target.getPhase() == 1) {
            int remain = target.getWindupRemainTicks();
            float dist = target.getDistance();
            LivingEntity te = target.getEntity();
            if (remain >= 0 && dist <= WINDUP_MELEE_RANGE) {
                if (remain <= 5 && te != null && isFrontThreat(maid, te) && isParryableWeapon(te)) {
                    CombatActions.parry(patch);
                    react(maid, id, tick, "reactive_windup_parry",
                            "windup remain=" + remain + "t dist=" + String.format("%.1f", dist));
                    return true;
                }
                if (remain <= 10) {
                    CombatActions.dodgeFromAttack(patch);
                    react(maid, id, tick, "reactive_windup_dodge",
                            "windup remain=" + remain + "t dist=" + String.format("%.1f", dist));
                    return true;
                }
            }
        }

        // ---- 3. 弹道拦截（统一弹道感知）----
        ProjectilePerception.Threat threat = ProjectilePerception.nearest(maid);
        if (threat != null && threat.eta() <= PROJECTILE_ETA) {
            // 同步旧状态键（RlState s[10] 箭矢反应窗口语义扩展为"任意弹道威胁窗口"）
            org.eftlm.stylish.strategy.StyleState.setTick(maid, org.eftlm.stylish.strategy.StyleState.LAST_ARROW_REACT, tick);
            boolean melee = WeaponArsenal.classify(maid.getMainHandItem()) == WeaponArsenal.Kind.MELEE;
            if (threat.isBlockable() && melee) {
                // 箭矢/幻影剑等可格挡弹道：弹反窗口（格挡/弹刀）
                CombatActions.parry(patch);
                react(maid, id, tick, "reactive_projectile_parry",
                        "kind=" + threat.kind().tag + " eta=" + String.format("%.2f", threat.eta()));
            } else {
                // 不可格挡（爆炸/珍珠/钩）或未持近战：侧向闪避（闪避 = 官方弹道免疫）
                dodgeProjectile(patch, maid, threat);
                react(maid, id, tick, "reactive_projectile_dodge",
                        "kind=" + threat.kind().tag + " eta=" + String.format("%.2f", threat.eta()));
            }
            return true;
        }
        return false;
    }

    /** RL 决策点查询：反应层是否处于忙碌窗口（忙碌时跳过动作输出） */
    public static boolean isBusy(EntityMaid maid) {
        MaidState st = STATES.get(maid.getUUID());
        return st != null && maid.tickCount < st.busyUntil();
    }

    /** 女仆移除时清理反应层状态 */
    public static void forget(UUID id) {
        STATES.remove(id);
    }

    /** 是否女仆前方威胁（目标面朝女仆，dot > 0） */
    private static boolean isFrontThreat(EntityMaid maid, LivingEntity attacker) {
        Vec3 view = attacker.getViewVector(1.0F);
        Vec3 toMaid = maid.position().subtract(attacker.position()).normalize();
        return view.dot(toMaid) > 0.0;
    }

    /** 弹反可行性：攻击者持近战武器（未知武器保守按可弹反） */
    private static boolean isParryableWeapon(LivingEntity attacker) {
        try {
            return WeaponArsenal.classify(attacker.getMainHandItem()) != WeaponArsenal.Kind.GUN
                    && WeaponArsenal.classify(attacker.getMainHandItem()) != WeaponArsenal.Kind.BOW
                    && WeaponArsenal.classify(attacker.getMainHandItem()) != WeaponArsenal.Kind.CROSSBOW;
        } catch (Throwable t) {
            return true;
        }
    }

    /** 侧向闪避：朝弹道来向的垂直方向（左右随机，P5 避开危险区），消耗耐力 */
    private static void dodgeProjectile(MaidPatch<?> patch, EntityMaid maid, ProjectilePerception.Threat threat) {
        AttributeInstance weight = maid.getAttribute(EpicFightAttributes.WEIGHT.get());
        float cost = weight == null ? 2.0F : (float) (weight.getValue() * 0.1F);
        if (patch.getStamina() < cost) {
            return; // 耐力不足：硬吃（格挡姿态尝试兜底）
        }
        List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> dodges = AnimKit.dodgeMoves();
        // P5：优先闪向安全侧（危险区规避；0=两侧危险退化为随机）
        int prefer = maid.getRandom().nextBoolean() ? 0 : 1;
        int side = SpatialMap.safeDodgeSide(maid, prefer, 2.0);
        boolean left = side == 0 ? maid.getRandom().nextBoolean() : side < 0;
        patch.setStamina(patch.getStamina() - cost);
        // 索引：0前 1后 2左 3右；按弹道来向垂直方向闪避
        patch.playAnimationSynchronized(left ? dodges.get(2) : dodges.get(3), 0F);
    }

    private static void react(EntityMaid maid, UUID id, int tick, String type, String detail) {
        MaidState st = STATES.get(id);
        STATES.put(id, new MaidState(tick, tick + BUSY_TICKS, st.wasKnocked(), st.recoveryGuardUntil()));
        RlTrace.event(maid, type, detail);
    }
}
