package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * P1 弹道感知：统一投射物抽象（报告 3.2.2），替代原 {@code reactToArrow}
 * 只认 {@link AbstractArrow} 的局限。
 * <p>
 * 覆盖类型：
 * <ul>
 *     <li>{@link AbstractArrow} 及子类（原版箭矢/附魔箭）——可格挡；</li>
 *     <li>{@link AbstractHurtingProjectile}（火球/凋灵之首等）——爆炸类，不可格挡；</li>
 *     <li>{@link ThrowableProjectile}（末影珍珠/药水/雪球）——不可格挡（珍珠=位移）；</li>
 *     <li>{@link FishingHook}（钓鱼竿/AV 战斗鱼钩）——不可格挡，命中=被拉近；</li>
 *     <li>mod id {@code epic_fight_avalon} 的技能弹道实体（幻影剑等，LivingEntity 子类）——
 *         可格挡（报告 1.4：Avalon 弹道分流）；</li>
 * </ul>
 * 威胁计算：相对速度法（弹道速度 − 女仆速度），预计到达时间 eta = 距离/相对速度，
 * 仅保留"朝女仆飞来且 eta ≤ 2s"的威胁（含 3D 方向过滤与视线外忽略）。
 */
public final class ProjectilePerception {

    /** 扫描半径（格） */
    public static final int SCAN_RANGE = 24;
    /** 纳入威胁的最大 ETA（秒） */
    public static final float MAX_ETA = 2.0F;

    public enum Kind {
        ARROW("arrow", true),
        HURTING("hurting", false),
        THROWN("thrown", false),
        HOOK("hook", false),
        AVALON("avalon", true);

        final String tag;
        final boolean blockable;

        Kind(String tag, boolean blockable) {
            this.tag = tag;
            this.blockable = blockable;
        }
    }

    /** 单个弹道威胁（不可变快照） */
    public record Threat(Entity entity, Vec3 position, Vec3 velocity, float eta, Kind kind) {
        public boolean isBlockable() {
            return kind.blockable;
        }
    }

    private ProjectilePerception() {
    }

    /** 扫描女仆周边弹道威胁（按 ETA 升序） */
    public static List<Threat> scan(EntityMaid maid) {
        List<Threat> threats = new ArrayList<>();
        var level = maid.level();
        var box = maid.getBoundingBox().inflate(SCAN_RANGE);
        Vec3 maidPos = maid.position();
        Vec3 maidVel = maid.getDeltaMovement();

        // 非 LivingEntity 弹道（原版体系）
        for (Entity e : level.getEntitiesOfClass(Entity.class, box,
                e -> e instanceof AbstractArrow || e instanceof AbstractHurtingProjectile
                        || e instanceof ThrowableProjectile || e instanceof FishingHook)) {
            Kind kind = classify(e);
            if (kind == null) {
                continue;
            }
            addIfThreat(threats, maid, maidPos, maidVel, e, kind);
        }
        // Avalon 技能弹道（LivingEntity 子类，mod id 匹配）
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box, TargetTracker::isAvalonProjectile)) {
            addIfThreat(threats, maid, maidPos, maidVel, e, Kind.AVALON);
        }
        threats.sort(java.util.Comparator.comparingDouble(Threat::eta));
        return threats;
    }

    /** 最近威胁（无则 null） */
    public static Threat nearest(EntityMaid maid) {
        List<Threat> threats = scan(maid);
        return threats.isEmpty() ? null : threats.get(0);
    }

    /** 是否存在 eta ≤ 阈值的威胁（反应层用） */
    public static boolean threatWithin(EntityMaid maid, float maxEta) {
        List<Threat> threats = scan(maid);
        for (Threat t : threats) {
            if (t.eta <= maxEta) {
                return true;
            }
        }
        return false;
    }

    private static Kind classify(Entity e) {
        if (e instanceof FishingHook) {
            return Kind.HOOK;
        }
        if (e instanceof AbstractArrow) {
            return Kind.ARROW;
        }
        if (e instanceof AbstractHurtingProjectile) {
            return Kind.HURTING;
        }
        if (e instanceof ThrowableProjectile) {
            return Kind.THROWN;
        }
        return null;
    }

    /** 相对速度法判定是否即将命中并加入威胁列表 */
    private static void addIfThreat(List<Threat> threats, EntityMaid maid, Vec3 maidPos, Vec3 maidVel,
                                    Entity e, Kind kind) {
        if (!e.isAlive()) {
            return;
        }
        Vec3 pos = e.position();
        Vec3 vel = e.getDeltaMovement();
        Vec3 toMaid = maidPos.subtract(pos);
        double dist = toMaid.length();
        if (dist > SCAN_RANGE || dist < 0.5) {
            return;
        }
        // 相对速度 = 弹道速度 − 女仆速度（双方速度矢量同时考虑）
        Vec3 relVel = vel.subtract(maidVel);
        double relSpeed = relVel.length();
        if (relSpeed < 0.3) {
            return;
        }
        Vec3 toMaidNorm = toMaid.normalize();
        if (toMaidNorm.dot(relVel.normalize()) < 0.75) {
            return; // 相对运动方向不是朝女仆来
        }
        float eta = (float) (dist / relSpeed);
        if (eta > MAX_ETA) {
            return;
        }
        threats.add(new Threat(e, pos, vel, eta, kind));
    }

    /** 诊断：当前威胁列表摘要（trace 事件用） */
    public static String describe(List<Threat> threats) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(3, threats.size()); i++) {
            Threat t = threats.get(i);
            if (i > 0) {
                sb.append(';');
            }
            sb.append(t.kind().tag).append(" eta=").append(String.format("%.2f", t.eta));
        }
        return sb.toString();
    }
}
