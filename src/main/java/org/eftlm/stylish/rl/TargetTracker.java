package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.eftlm.stylish.arena.AutoArena;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * P1 目标记忆（重写）：每帧扫描女仆周围敌对生物，维护攻击目标列表并持续追踪。
 * <p>
 * 相对 V46 版本的升级：
 * <ul>
 *     <li><b>幽灵目标（Ghost Target）</b>：脱锁（离开扫描范围）不删除——保留最后位置 +
 *         速度外推的预期位置，置信度随时间衰减；删除条件从"40 tick 未扫到"改为
 *         "置信度低于阈值"（解决闪现/放风筝脱锁后 AI 失明，见报告 3.2.1）；</li>
 *     <li><b>技能阶段状态机</b>：读 EpicFight 目标 patch 的 {@code getLevel()}（官方语义
 *         1=前摇 2=攻击 3=后摇），WINDUP 时经 {@code AttackAnimation.getPhaseByTime}
 *         解析前摇剩余帧（报告 3.2.1 分级数据源：EpicFight patch 敌方直接读）；</li>
 *     <li><b>霸体观测</b>：由命中事件 {@link #reportHitstun} 反馈目标是否产生硬直，
 *         连续无硬直判定霸体（报告 3.3-5）；</li>
 *     <li><b>Avalon 弹道分流</b>：mod id 为 {@code epic_fight_avalon} 的 LivingEntity
 *         是技能弹道实体（如幻影剑），不再误分类为目标（报告 1.4/2.2）；</li>
 *     <li>威胁分加入 WINDUP 加成与幽灵置信度折扣。</li>
 * </ul>
 * <p>
 * {@link #collectFeatures} 保持 14 维聚合输出不变（32 维 RL 状态契约兼容，模型无需重训）；
 * 感知升级信息主要服务 P1 反应层（{@link ReactiveLayer}），P2 增维时再进状态空间。
 */
public final class TargetTracker {

    /** 锁定扫描半径（格） */
    public static final int SCAN_RANGE = 32;
    /** 目标列表上限（防止极端多实体场景撑爆状态） */
    private static final int MAX_TARGETS = 8;
    /** 幽灵置信度每 tick 衰减系数（0.98 ≈ 半衰期 35 tick ≈ 1.75 秒） */
    private static final float GHOST_DECAY = 0.98F;
    /** 幽灵目标删除阈值（置信度低于该值即遗忘） */
    private static final float GHOST_FORGET = 0.1F;
    /** 速度平滑系数（EMA）：新采样权重 */
    private static final float VELOCITY_SMOOTH = 0.3F;
    /** 霸体判定：连续无硬直命中次数达到该值判霸体 */
    private static final int HYPERARMOR_STREAK = 3;

    private static final Map<UUID, Map<UUID, TrackedTarget>> PER_MAID = new HashMap<>();

    private TargetTracker() {
    }

    /** 每帧扫描并更新女仆的攻击目标列表（应在服务器 tick 中调用） */
    public static void update(EntityMaid maid) {
        if (!(maid.level() instanceof ServerLevel level)) {
            return;
        }
        UUID maidId = maid.getUUID();
        Map<UUID, TrackedTarget> targets = PER_MAID.computeIfAbsent(maidId, k -> new HashMap<>());
        int tick = maid.tickCount;
        AABB box = maid.getBoundingBox().inflate(SCAN_RANGE);

        // 扫描（排除 Avalon 技能弹道实体——它们是 LivingEntity(Mob) 子类，会被误扫为目标）
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, box,
                e -> e != maid && e.isAlive() && !isAvalonProjectile(e) && isHostile(maid, e))) {
            TrackedTarget t = targets.computeIfAbsent(e.getUUID(), k -> new TrackedTarget());
            t.onSeen(maid, e, tick);
        }

        // 幽灵目标维护：未扫到的目标保留并外推（脱锁记忆），置信度衰减
        targets.forEach((uuid, t) -> {
            if (!t.ghost && t.entity != null && t.entity.isAlive()) {
                return;
            }
            if (t.entity != null && t.entity.isAlive()) {
                t.onNotSeen(tick);
            }
        });
        // 删除死亡 / 置信度归零的目标
        targets.entrySet().removeIf(entry -> {
            TrackedTarget t = entry.getValue();
            return t.entity == null || !t.entity.isAlive() || t.confidence <= GHOST_FORGET;
        });

        // 超出上限时保留最近 MAX_TARGETS 个
        if (targets.size() > MAX_TARGETS) {
            List<Map.Entry<UUID, TrackedTarget>> list = new ArrayList<>(targets.entrySet());
            list.sort(Comparator.comparingDouble(e -> e.getValue().distance));
            for (int i = MAX_TARGETS; i < list.size(); i++) {
                targets.remove(list.get(i).getKey());
            }
        }
    }

    /** 女仆移除时清理其目标追踪状态 */
    public static void forgetMaid(UUID maidId) {
        PER_MAID.remove(maidId);
    }

    /** 当前攻击目标列表（按距离升序；含幽灵目标） */
    public static List<TrackedTarget> getTargets(EntityMaid maid) {
        Map<UUID, TrackedTarget> map = PER_MAID.get(maid.getUUID());
        if (map == null || map.isEmpty()) {
            return List.of();
        }
        List<TrackedTarget> list = new ArrayList<>(map.values());
        list.sort(Comparator.comparingDouble(t -> t.distance));
        return list;
    }

    /** 当前非幽灵威胁目标（反应层用：最近且置信度高的目标） */
    public static TrackedTarget getNearestSolidTarget(EntityMaid maid) {
        Map<UUID, TrackedTarget> map = PER_MAID.get(maid.getUUID());
        if (map == null || map.isEmpty()) {
            return null;
        }
        TrackedTarget best = null;
        for (TrackedTarget t : map.values()) {
            if (t.entity == null || !t.entity.isAlive() || t.ghost) {
                continue;
            }
            if (best == null || t.distance < best.distance) {
                best = t;
            }
        }
        return best;
    }

    /**
     * 根据全部目标的威胁 / 血量 / 距离选择当前应优先攻击的目标。
     * 威胁分高者优先，同分取更近者；幽灵目标按置信度折扣威胁。
     */
    public static TrackedTarget selectPriorityTarget(EntityMaid maid) {
        List<TrackedTarget> targets = getTargets(maid);
        if (targets.isEmpty()) {
            return null;
        }
        TrackedTarget best = null;
        for (TrackedTarget t : targets) {
            float score = t.threatScore * (t.ghost ? t.confidence : 1.0F);
            if (best == null || score > best.threatScore * (best.ghost ? best.confidence : 1.0F)
                    || (score == best.threatScore * (best.ghost ? best.confidence : 1.0F) && t.distance < best.distance)) {
                best = t;
            }
        }
        return best;
    }

    /**
     * 霸体观测：命中事件反馈目标是否产生硬直（P1，报告 3.3-5）。
     * 由伤害结算处调用：命中后 1~2 tick 检查目标是否播放 HitAnimation / hurtLevel>0。
     * 连续 {@link #HYPERARMOR_STREAK} 次无硬直 → 判霸体；任意一次有硬直 → 清零重计。
     */
    public static void reportHitstun(EntityMaid maid, LivingEntity target, boolean hadHitstun) {
        Map<UUID, TrackedTarget> map = PER_MAID.get(maid.getUUID());
        if (map == null) {
            return;
        }
        TrackedTarget t = map.get(target.getUUID());
        if (t == null) {
            return;
        }
        if (hadHitstun) {
            t.noHitstunStreak = 0;
            t.hyperarmor = false;
        } else {
            t.noHitstunStreak++;
            if (t.noHitstunStreak >= HYPERARMOR_STREAK) {
                t.hyperarmor = true;
            }
        }
    }

    /**
     * 生成多目标聚合特征（追加在 RlState 18 维基础特征之后）。
     * 当前返回 14 维：数量/第二目标/威胁/危险/传送接近等（与 V46 契约一致，模型兼容）。
     */
    public static float[] collectFeatures(EntityMaid maid) {
        List<TrackedTarget> targets = getTargets(maid);
        float[] f = new float[14];

        TrackedTarget nearest = targets.isEmpty() ? null : targets.get(0);
        TrackedTarget second = targets.size() > 1 ? targets.get(1) : null;

        // 18: 目标数量 / 4（幽灵目标计 0.5）
        float count = 0;
        for (TrackedTarget t : targets) {
            count += t.ghost ? 0.5F : 1.0F;
        }
        f[0] = Math.min(1.0F, count / 4.0F);
        // 19: 第二近目标距离 / 16
        f[1] = second != null ? Math.min(1.0F, (float) (second.distance / 16.0)) : 1.0F;
        // 20: 第二近目标血量比例
        f[2] = second != null ? second.health / Math.max(1.0F, second.maxHealth) : 0.0F;
        // 21: 是否存在任意目标正在攻击
        f[3] = any(targets, TrackedTarget::isAttacking) ? 1.0F : 0.0F;
        // 22: 是否存在任意目标处于危险范围（攻击中且距离 <= 4）
        f[4] = any(targets, TrackedTarget::isInDangerRange) ? 1.0F : 0.0F;
        // 23: 是否需要闪避/格挡（最近危险目标）
        f[5] = nearest != null && nearest.inDangerRange ? 1.0F : 0.0F;
        // 24: 是否存在低血量目标
        f[6] = any(targets, TrackedTarget::isLowHealth) ? 1.0F : 0.0F;
        // 25: 是否存在带增益效果的目标
        f[7] = any(targets, TrackedTarget::hasBuff) ? 1.0F : 0.0F;
        // 26: 是否可通过末影珍珠 / 阎魔刀瞬移接近
        f[8] = canTeleportApproach(maid) ? 1.0F : 0.0F;
        // 27: 总威胁度（归一化；幽灵按置信度折扣）
        float threat = 0.0F;
        for (TrackedTarget t : targets) {
            threat += t.threatScore * (t.ghost ? t.confidence : 1.0F);
        }
        f[9] = Math.min(1.0F, threat / 4.0F);
        // 28: 最近目标护甲值 / 20
        f[10] = nearest != null ? Math.min(1.0F, nearest.armor / 20.0F) : 0.0F;
        // 29: 最近目标当前技能阶段 / 4
        f[11] = nearest != null ? Math.min(1.0F, nearest.phase / 4.0F) : 0.0F;
        // 30: 最近目标是否正在使用技能
        f[12] = nearest != null && nearest.usingSkill ? 1.0F : 0.0F;
        // 31: 是否存在任意目标的伤害技能覆盖女仆当前位置
        f[13] = f[4];

        return f;
    }

    private static boolean any(List<TrackedTarget> targets, java.util.function.Predicate<TrackedTarget> predicate) {
        for (TrackedTarget t : targets) {
            if (predicate.test(t)) {
                return true;
            }
        }
        return false;
    }

    /** Avalon 技能弹道实体识别（mod id 匹配，无编译期依赖；供 ProjectilePerception 复用） */
    public static boolean isAvalonProjectile(LivingEntity e) {
        var key = ForgeRegistries.ENTITY_TYPES.getKey(e.getType());
        return key != null && "epic_fight_avalon".equals(key.getNamespace());
    }

    private static boolean isHostile(EntityMaid maid, LivingEntity e) {
        if (AutoArena.isArenaTargetEntity(e)) {
            return true;
        }
        if (e instanceof Mob mob && mob.getTarget() == maid) {
            return true;
        }
        return maid.canAttack(e);
    }

    private static boolean canTeleportApproach(EntityMaid maid) {
        // 末影珍珠
        var inv = maid.getAvailableBackpackInv();
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && stack.is(net.minecraft.world.item.Items.ENDER_PEARL)) {
                return true;
            }
        }
        // 阎魔刀（Yamato 系列武器，含瞬移/次元斩接近能力）
        for (ItemStack stack : new ItemStack[]{maid.getMainHandItem(), maid.getOffhandItem()}) {
            if (!stack.isEmpty() && isYamato(stack)) {
                return true;
            }
        }
        var backpack = maid.getAvailableBackpackInv();
        for (int i = 0; i < backpack.getSlots(); i++) {
            ItemStack stack = backpack.getStackInSlot(i);
            if (!stack.isEmpty() && isYamato(stack)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isYamato(ItemStack stack) {
        var key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return key != null && key.getPath().toLowerCase(java.util.Locale.ROOT).contains("yamato");
    }

    /** 单个已追踪目标的动态信息（含 P1 幽灵/阶段/霸体扩展） */
    public static final class TrackedTarget {
        private LivingEntity entity;
        private int lastSeenTick;
        /** 最近扫描位置（幽灵外推的基准） */
        private Vec3 position;
        /** 平滑速度估计（格/tick，EMA） */
        private Vec3 velocity = Vec3.ZERO;
        /** 置信度 [0,1]：扫描到=1；脱锁后每 tick 衰减 */
        private float confidence = 1.0F;
        /** 幽灵目标（当前不在扫描范围内，位置为外推） */
        private boolean ghost = false;
        private float distance;
        private float health;
        private float maxHealth;
        private float armor;
        /** EpicFight 阶段：0=IDLE 1=WINDUP(前摇) 2=ACTIVE(攻击) 3=RECOVERY(后摇) */
        private int phase;
        /** WINDUP 剩余前摇帧（tick；无法解析时为 -1） */
        private int windupRemainTicks = -1;
        private boolean attacking;
        private boolean usingSkill;
        private boolean inDangerRange;
        private boolean lowHealth;
        private boolean hasBuff;
        /** 霸体观测：连续命中无硬直（由 reportHitstun 更新） */
        private boolean hyperarmor;
        private int noHitstunStreak;
        private float threatScore;

        private void onSeen(EntityMaid maid, LivingEntity e, int tick) {
            Vec3 now = e.position();
            // 速度平滑（EMA）：新采样权重 VELOCITY_SMOOTH
            if (this.entity != null && !this.ghost && this.position != null) {
                Vec3 delta = now.subtract(this.position);
                this.velocity = this.velocity.scale(1.0F - VELOCITY_SMOOTH).add(delta.scale(VELOCITY_SMOOTH));
            }
            this.entity = e;
            this.lastSeenTick = tick;
            this.position = now;
            this.confidence = 1.0F;
            this.ghost = false;
            this.distance = maid.distanceTo(e);
            this.health = e.getHealth();
            this.maxHealth = Math.max(1.0F, e.getMaxHealth());
            this.armor = e.getArmorValue();

            // 技能阶段：EpicFight patch 直接读（官方语义 1=前摇 2=攻击 3=后摇）
            LivingEntityPatch<?> ep = EpicFightCapabilities.getEntityPatch(e, LivingEntityPatch.class);
            if (ep != null) {
                int lvl = ep.getEntityState().getLevel();
                this.phase = lvl >= 1 && lvl <= 3 ? lvl : 0;
                this.attacking = lvl == 2 || lvl == 1;
                this.usingSkill = lvl > 0;
                if (lvl == 1) {
                    this.windupRemainTicks = readWindupRemainTicks(ep);
                } else {
                    this.windupRemainTicks = -1;
                }
            } else {
                this.phase = e.swingTime > 0 ? 2 : 0;
                this.attacking = e.swingTime > 0;
                this.usingSkill = e.swingTime > 0;
                this.windupRemainTicks = -1;
            }
            this.inDangerRange = this.attacking && distance <= 4.0;
            this.lowHealth = health / maxHealth < 0.3F;
            this.hasBuff = hasBeneficialEffect(e);

            // 威胁分（P1：WINDUP 是应对窗口权重更高；幽灵置信度由调用方折扣）
            this.threatScore = (this.phase == 1 ? 2.5F : (this.attacking ? 2.0F : 0.0F))
                    + (this.inDangerRange ? 2.0F : 0.0F)
                    + (this.lowHealth ? 1.0F : 0.0F)
                    + (distance <= 4.0 ? 1.0F : 0.0F)
                    + (this.hyperarmor ? 1.5F : 0.0F);
        }

        /** 本 tick 未被扫描到：幽灵化 + 位置外推 + 置信度衰减 */
        private void onNotSeen(int tick) {
            this.ghost = true;
            int dt = Math.max(1, tick - this.lastSeenTick);
            this.confidence *= (float) Math.pow(GHOST_DECAY, dt);
            // 预期位置外推：pos + velocity × dt（限幅防飞越）
            this.position = this.position.add(this.velocity.scale(Math.min(dt, 20)));
            this.distance = this.distance + (float) this.velocity.horizontalDistance() * Math.min(dt, 20);
            // 脱锁后技能阶段信息降级（保守按 IDLE，等待重锁）
            this.phase = 0;
            this.attacking = false;
            this.usingSkill = false;
            this.windupRemainTicks = -1;
        }

        /** 读取目标当前动画的前摇剩余帧（仅 level==1 时调用） */
        private static int readWindupRemainTicks(LivingEntityPatch<?> ep) {
            try {
                AnimationPlayer player = ep.getAnimator().getPlayerFor(null);
                if (player == null) {
                    return -1;
                }
                AssetAccessor<? extends StaticAnimation> real = player.getRealAnimation();
                if (real == null || !(real.get() instanceof AttackAnimation attack)) {
                    return -1;
                }
                float elapsed = player.getElapsedTime();
                float preDelay = attack.getPhaseByTime(elapsed).preDelay;
                float remainSec = preDelay - elapsed;
                return remainSec > 0 ? Math.max(1, (int) (remainSec * 20)) : -1;
            } catch (Throwable t) {
                return -1;
            }
        }

        private static boolean hasBeneficialEffect(LivingEntity e) {
            for (var effect : e.getActiveEffects()) {
                if (effect.getEffect().isBeneficial()) {
                    return true;
                }
            }
            return false;
        }

        public LivingEntity getEntity() {
            return entity;
        }

        public UUID getUuid() {
            return entity != null ? entity.getUUID() : null;
        }

        public Vec3 getPosition() {
            return position;
        }

        /** 预期位置（含幽灵外推） */
        public Vec3 getPredictedPosition() {
            return position;
        }

        public Vec3 getVelocity() {
            return velocity;
        }

        public float getConfidence() {
            return confidence;
        }

        /** 是否幽灵目标（脱锁记忆，位置为外推） */
        public boolean isGhost() {
            return ghost;
        }

        public float getDistance() {
            return distance;
        }

        public float getHealth() {
            return health;
        }

        public float getMaxHealth() {
            return maxHealth;
        }

        public float getArmor() {
            return armor;
        }

        public int getPhase() {
            return phase;
        }

        /** WINDUP 剩余前摇 tick（-1 = 未知） */
        public int getWindupRemainTicks() {
            return windupRemainTicks;
        }

        public boolean isAttacking() {
            return attacking;
        }

        public boolean isUsingSkill() {
            return usingSkill;
        }

        public boolean isInDangerRange() {
            return inDangerRange;
        }

        public boolean isLowHealth() {
            return lowHealth;
        }

        public boolean hasBuff() {
            return hasBuff;
        }

        /** 霸体标志（连续命中无硬直） */
        public boolean isHyperarmor() {
            return hyperarmor;
        }

        public float getThreatScore() {
            return threatScore;
        }
    }
}
