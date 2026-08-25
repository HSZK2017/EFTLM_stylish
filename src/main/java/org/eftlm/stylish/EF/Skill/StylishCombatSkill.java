package org.eftlm.stylish.EF.Skill;

import com.github.tartaricacid.touhoulittlemaid.api.event.MaidAttackEvent;
import com.github.tartaricacid.touhoulittlemaid.api.event.MaidTickEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.API.Event.MaidHurtTargetEvent;
import net.EFTLM.EF.API.Event.MaidKilledEvent;
import net.EFTLM.EF.Capability.MaidPatch;
import net.EFTLM.EF.Skill.MaidSkill;
import net.EFTLM.EF.Skill.MaidSkillBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.phys.Vec3;
import org.eftlm.stylish.strategy.CombatActions;
import org.eftlm.stylish.strategy.StyleState;
import org.eftlm.stylish.strategy.WeaponArsenal;
import org.eftlm.stylish.util.AnimKit;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

import java.util.List;
import java.util.Random;

/**
 * 华丽连段技能：与 {@link org.eftlm.stylish.EF.Event.StylishBehaviorRegister} 注册的
 * 行为表协同工作。
 * <p>
 * 攻击连段 / 防守 / 浮空 JC / 风格切换由 EpicFight 的 AnimatedAttackGoal 按行为表
 * 自动驱动；本技能负责行为表无法表达的部分（每 tick 状态机）：
 * <ul>
 *     <li>被击倒 → 翻滚起身</li>
 *     <li>枪神收尾状态机：瞄准 → 射击（弓 / 弩 / 枪械）→ 换回近战</li>
 *     <li>连段结束（行为表回调置位）→ 轮换近战武器 / 触发远程收尾</li>
 *     <li>华丽度管理：命中 / 击杀加分，受击扣分，空闲衰减</li>
 *     <li>弹反 / 格挡窗口内命中全额取消伤害（+CLASH 音效粒子）</li>
 * </ul>
 */
public class StylishCombatSkill extends MaidSkill {

    public static final String MODID = "eftlm_stylish";
    public static final String SKILL_PATH = "stylish_combat";
    public static final net.minecraft.resources.ResourceLocation SKILL_ID =
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(MODID, SKILL_PATH);

    private static final int FINISHER_SHOOT_TICK = 12;
    private static final int FINISHER_SWAP_BACK_TICK = 22;
    private static final float FINISHER_HP_RATIO = 0.25F;
    private static final int PARRY_WINDOW = 10;
    private static final int BLOCK_WINDOW = 20;

    private static final Random RANDOM = new Random();
    /** 专用服务器（无客户端渲染）：AimAnimation.getPoseByTime 加载客户端类直接崩溃，禁止播放瞄准动画 */
    private static final boolean DEDICATED_SERVER = net.minecraftforge.fml.loading.FMLEnvironment.dist
            == net.minecraftforge.api.distmarker.Dist.DEDICATED_SERVER;
    /** 距离武器切换：目标距离超过该值且背包有远程武器 → 切远程反击（防放风筝） */
    private static final double RANGED_SWITCH_DIST = 4.5;
    /** 距离武器切换：主手为远程且目标距离小于该值 → 切回近战 */
    private static final double MELEE_SWITCH_DIST = 3.0;

    public StylishCombatSkill(MaidSkillBuilder<? extends MaidSkill> builder) {
        super(builder);
    }

    // ==================================================================
    // 每 tick 状态机
    // ==================================================================

    @Override
    public void MaidTick(MaidTickEvent event) {
        EntityMaid maid = event.getMaid();
        if (!(maid.level() instanceof ServerLevel)) {
            return;
        }
        MaidPatch<?> patch = EpicFightCapabilities.getEntityPatch(maid, MaidPatch.class);
        if (patch == null) {
            return;
        }
        int tick = maid.tickCount;

        // ---- 0.5 RL 宏观决策：每 5 tick 预测行动并发布到事件总线（状态机实施） ----
        org.eftlm.stylish.rl.RlBrain.tick(patch);

        // ---- 0.55 P2.5 道具战斗：临时方块/水清理 + 着火水桶灭火（仿 AVNpc Steve/Alex） ----
        org.eftlm.stylish.rl.ItemCombat.tick(maid);

        // ---- 0.56 P5.6 自适应学习：敌方节奏统计 + 增益窃取 + 规则模式调度出招 ----
        org.eftlm.stylish.rl.CombatLibrary.tick(maid, patch);

        // ---- 0. 空闲自愈：无目标且血量偏低时消耗药水 / 金苹果或缓慢回血 ----
        if (canSelfHeal(maid, patch)) {
            tickSelfHeal(maid);
        }

        // ---- 0.5 增益药水：战斗中自动使用力量/迅捷等背包增益（不依赖 RL 学会新动作） ----
        tryConsumeBuffPotion(maid);

        // ---- 1.5 被击倒 → 翻滚起身（优先级最高） ----
        if (patch.getEntityState().knockDown()) {
            // 边沿触发：击倒动画持续 30~60 tick，此前每 tick 都结算 -30（一次击倒累计
            // -900~-1800，约等于 60 次命中奖励，严重失衡）。现在一次击倒只结算一次。
            if (tick - StyleState.getTick(maid, StyleState.LAST_KNOCKDOWN) > StyleState.KNOCKDOWN_PENALTY_COOLDOWN) {
                org.eftlm.stylish.rl.RlDataRecorder.addReward(maid, -30); // 被击倒惩罚
                StyleState.setTick(maid, StyleState.LAST_KNOCKDOWN, tick);
                org.eftlm.stylish.rl.RlTrace.event(maid, "knockdown", "penalty applied, forced roll recovery");
            }
            org.eftlm.stylish.rl.RlTrace.event(maid, "roll_recovery", "knockdown state -> roll");
            CombatActions.rollRecovery(patch);
            return;
        }

        // ---- 1.6 按键类战技程序化（V12）：突刺 / 次元斩 / 上挑 / 横扫 / 火山等条件释放 ----
        org.eftlm.stylish.strategy.AutoSkill.tick(patch, maid, tick);

        // ---- 1.7 弹道拦截（P1：由 ReactiveLayer 每 tick 接管，覆盖箭矢/火球/珍珠/
        //      钓鱼钩/Avalon 幻影剑等全部弹道类型；原 reactToArrow 方法保留作参考）----

        // ---- 2. 枪神收尾状态机 ----
        int finisher = StyleState.getInt(maid, StyleState.FINISHER, 0);
        if (finisher != 0) {
            updateFinisher(patch, maid, tick);
            return;
        }

        // ---- 2.5 目标锁定加固：反击锁定攻击者，防止多目标脱锁跳变 ----
        LivingEntity target = patch.getTarget();
        double dist = target != null ? maid.distanceTo(target) : Double.MAX_VALUE;
        if (tick % 5 == 0) {
            lockHighThreatTarget(patch, maid, target);
        }

        // ---- 2.55 灵动步伐兜底（P1：由 ReactiveLayer 前摇规避每 tick 接管，
        //      含弹反/闪避选择与统一弹道拦截；tryDodgeIncomingAttack 方法保留作参考）----

        // ---- 2.6 距离武器切换：被远程敌人放风筝时切远程反击，近身切回近战 ----
        ItemStack mainHand = maid.getMainHandItem();
        WeaponArsenal.Kind mainKind = WeaponArsenal.classify(mainHand);
        boolean mainIsRanged = mainKind == WeaponArsenal.Kind.BOW
                || mainKind == WeaponArsenal.Kind.CROSSBOW
                || mainKind == WeaponArsenal.Kind.GUN;
        if (tick - StyleState.getTick(maid, StyleState.LAST_SWAP) > WeaponArsenal.SWAP_COOLDOWN) {
            if (dist > RANGED_SWITCH_DIST && !mainIsRanged && WeaponArsenal.hasUsableRanged(maid)) {
                // 目标太远（弓兵放风筝）→ 切换远程武器反击（换装失败则继续后续逻辑）
                if (CombatActions.startRangedAim(patch)) {
                    return;
                }
            }
            if (dist < MELEE_SWITCH_DIST && mainIsRanged) {
                // 目标近身 → 切回近战
                swapBackToMelee(patch, maid);
                return;
            }
        }

        // ---- 3. 连段结束（行为表回调置位）→ 轮换武器 / 远程收尾 ----
        if (StyleState.getInt(maid, StyleState.COMBO_END, 0) == 1) {
            StyleState.setInt(maid, StyleState.COMBO_END, 0);
            boolean lowHp = target != null && target.getHealth() < target.getMaxHealth() * FINISHER_HP_RATIO;
            boolean gunslinger = StyleState.getStyle(maid) == AnimKit.STYLE_GUNSLINGER;
            if ((lowHp || gunslinger) && WeaponArsenal.hasUsableRanged(maid) && StyleState.getFlair(maid) >= StyleState.FLAIR_D) {
                // 连段将结束 → 切换远程武器，枪神风格收尾
                CombatActions.startRangedAim(patch);
            } else {
                // 每完成一个连段小循环切换一次近战武器
                CombatActions.cycleWeapon(patch);
            }
        }

        // ---- 4. 华丽度衰减 ----
        if (tick - StyleState.getTick(maid, StyleState.LAST_HIT) > 40) {
            float flair = StyleState.getFlair(maid);
            if (flair > 0) {
                StyleState.setFlair(maid, Math.max(0.0F, flair - 1.2F));
            }
        }
    }

    // ==================================================================
    // 箭矢反应（闪避 / 格挡 / 弹刀）
    // ==================================================================

    /**
     * 扫描半径 16 格内的箭矢实体，用"箭矢速度矢量 - 女仆速度矢量"的相对速度
     * 预测是否即将命中：
     * <ul>
     *     <li>相对速度方向与"箭矢→女仆"方向一致（夹角足够小）且相对速度足够大</li>
     *     <li>预计到达时间 &lt; 0.6 秒 → 即将命中</li>
     * </ul>
     * 反应：持近战武器 → 格挡（概率弹刀）；无近战武器 → 侧向闪避（消耗耐力）。
     * 命中时的伤害由弹反 / 格挡窗口（MaidAttack 处理）全额取消。
     *
     * @return 是否做出了反应
     */
    private boolean reactToArrow(MaidPatch<?> patch, EntityMaid maid, int tick) {
        List<net.minecraft.world.entity.projectile.AbstractArrow> arrows = maid.level()
                .getEntitiesOfClass(net.minecraft.world.entity.projectile.AbstractArrow.class,
                        maid.getBoundingBox().inflate(16.0));
        if (arrows.isEmpty()) {
            return false;
        }
        Vec3 maidPos = maid.position();
        Vec3 maidVel = maid.getDeltaMovement();
        boolean melee = WeaponArsenal.classify(maid.getMainHandItem()) == WeaponArsenal.Kind.MELEE;
        for (net.minecraft.world.entity.projectile.AbstractArrow arrow : arrows) {
            if (!arrow.isAlive()) {
                continue;
            }
            Vec3 toMaid = maidPos.subtract(arrow.position());
            double dist = toMaid.length();
            if (dist > 16.0 || dist < 1.0) {
                continue;
            }
            // 相对速度 = 箭矢速度 - 女仆速度（用户要求：同时考虑双方速度矢量）
            Vec3 relVel = arrow.getDeltaMovement().subtract(maidVel);
            double relSpeed = relVel.length();
            if (relSpeed < 0.4) {
                continue;
            }
            Vec3 toMaidNorm = toMaid.normalize();
            if (toMaidNorm.dot(relVel.normalize()) < 0.75) {
                continue; // 相对运动方向不是朝女仆来
            }
            // 预计到达时间（秒），即将命中时反应
            double eta = dist / relSpeed;
            if (eta > 0.6) {
                continue;
            }
            if (melee) {
                // 近战武器 → 格挡 / 弹刀
                if (RANDOM.nextFloat() < 0.4F) {
                    CombatActions.parry(patch); // 弹刀（ACTIVE 格挡窗口）
                } else {
                    WeaponCategory category = AnimKit.categoryOf(patch);
                    patch.playAnimationSynchronized(AnimKit.guardHit(category), 0F);
                    StyleState.setTick(maid, StyleState.BLOCK_START, tick);
                }
            } else {
                // 无近战武器 → 侧向闪避
                dodgeArrow(patch, maid, toMaidNorm);
            }
            StyleState.setTick(maid, StyleState.LAST_ARROW_REACT, tick);
            org.eftlm.stylish.rl.RlTrace.event(maid, "arrow_react",
                    String.format("eta=%.2f arrows=%d melee=%s", eta, arrows.size(), melee));
            return true;
        }
        return false;
    }

    /**
     * 朝箭矢来向的垂直方向侧向闪避（左右随机），消耗耐力。
     */
    private void dodgeArrow(MaidPatch<?> patch, EntityMaid maid, Vec3 toArrow) {
        AttributeInstance weightAttr = maid.getAttribute(
                yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes.WEIGHT.get());
        float cost = weightAttr == null ? 2.0F : (float) (weightAttr.getValue() * 0.1F);
        if (patch.getStamina() < cost) {
            return;
        }
        boolean left = RANDOM.nextBoolean();
        List<AnimationManager.AnimationAccessor<? extends StaticAnimation>> dodges = AnimKit.dodgeMoves();
        patch.setStamina(patch.getStamina() - cost);
        // 索引：0前 1后 2左 3右；按箭矢来向垂直方向闪避
        patch.playAnimationSynchronized(left ? dodges.get(2) : dodges.get(3), 0F);
        StyleState.setTick(maid, StyleState.LAST_DODGE, maid.tickCount);
    }

    /**
     * 参考 EFTLM“灵动步伐”：敌方攻击前摇（EpicFight phase 1~2）且近身时，
     * 自动朝攻击反方向闪避。带短冷却，避免连续触发导致耐力耗尽。
     */
    private boolean tryDodgeIncomingAttack(MaidPatch<?> patch, EntityMaid maid, LivingEntity target, int tick) {
        if (target == null || !target.isAlive()) {
            return false;
        }
        if (tick - StyleState.getTick(maid, StyleState.LAST_DODGE) < 10) {
            return false;
        }
        if (maid.distanceTo(target) > 3.5) {
            return false;
        }
        LivingEntityPatch<?> targetPatch = EpicFightCapabilities.getEntityPatch(target, LivingEntityPatch.class);
        if (targetPatch == null) {
            return false;
        }
        int phase = targetPatch.getEntityState().getLevel();
        if (phase <= 0 || phase >= 3) {
            return false;
        }
        return CombatActions.dodgeFromAttack(patch);
    }

    // ==================================================================
    // 空闲自愈（替代原冥想）：无目标且血量偏低时，
    // 优先消耗背包中的治疗 / 再生药水或金苹果，否则缓慢自然回血。
    // ==================================================================

    private static final String HEAL_COOLDOWN = "eftlm_stylish:heal_cd";
    private static final int HEAL_CHECK_INTERVAL = 100;
    private static final float HEAL_HP_RATIO = 0.6F;
    /** 增益药水检查冷却（每 6 秒一次，避免频繁扫背包） */
    private static final String BUFF_COOLDOWN = "eftlm_stylish:buff_cd";
    private static final int BUFF_CHECK_INTERVAL = 120;

    private boolean canSelfHeal(EntityMaid maid, MaidPatch<?> patch) {
        if (patch.getTarget() != null) {
            return false;
        }
        if (patch.isSleep() || patch.isSit()) {
            return false;
        }
        return !maid.isInWater() && !maid.isInLava() && !maid.isPassenger();
    }

    private void tickSelfHeal(EntityMaid maid) {
        CompoundTag data = maid.getPersistentData();
        if (maid.tickCount - data.getInt(HEAL_COOLDOWN) < HEAL_CHECK_INTERVAL) {
            return;
        }
        if (maid.getHealth() >= maid.getMaxHealth() * HEAL_HP_RATIO) {
            return;
        }
        data.putInt(HEAL_COOLDOWN, maid.tickCount);
        if (tryConsumeMedicine(maid)) {
            return;
        }
        // 背包无药水 / 金苹果 → 缓慢自然回血
        maid.heal(1.0F);
        data.putInt(HEAL_COOLDOWN, maid.tickCount + 40);
    }

    /**
     * 背包中寻找并消耗治疗 / 再生药水或金苹果。
     *
     * @return 是否成功使用
     */
    private boolean tryConsumeMedicine(EntityMaid maid) {
        var inv = maid.getAvailableInv(true);
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof PotionItem) {
                List<MobEffectInstance> effects = PotionUtils.getMobEffects(stack);
                boolean healing = false;
                for (MobEffectInstance effect : effects) {
                    if (effect.getEffect() == MobEffects.HEAL || effect.getEffect() == MobEffects.REGENERATION) {
                        healing = true;
                        break;
                    }
                }
                if (!healing) {
                    continue;
                }
                inv.extractItem(i, 1, false);
                for (MobEffectInstance effect : effects) {
                    maid.addEffect(new MobEffectInstance(effect));
                }
                maid.level().playSound(null, maid.getX(), maid.getY(), maid.getZ(),
                        SoundEvents.GENERIC_DRINK, maid.getSoundSource(), 0.8F, 1.0F);
                return true;
            }
            if (stack.is(Items.GOLDEN_APPLE) || stack.is(Items.ENCHANTED_GOLDEN_APPLE)) {
                boolean enchanted = stack.is(Items.ENCHANTED_GOLDEN_APPLE);
                inv.extractItem(i, 1, false);
                maid.heal(enchanted ? 8.0F : 4.0F);
                maid.addEffect(new MobEffectInstance(MobEffects.REGENERATION,
                        enchanted ? 600 : 200, enchanted ? 3 : 1, false, false));
                maid.level().playSound(null, maid.getX(), maid.getY(), maid.getZ(),
                        SoundEvents.GENERIC_EAT, maid.getSoundSource(), 0.8F, 1.0F);
                return true;
            }
        }
        return false;
    }

    /**
     * 自动使用背包中的增益药水（力量 / 迅捷 / 抗性 / 跳跃等）。
     * <p>
     * 只喝“当前尚未生效”的增益，避免浪费；与自愈共用背包扫描但独立冷却。
     * 这样即使 RL 模型没有学会“使用道具”动作，女仆也会在战斗/赶路中自动获得增益。
     */
    private void tryConsumeBuffPotion(EntityMaid maid) {
        CompoundTag data = maid.getPersistentData();
        if (maid.tickCount - data.getInt(BUFF_COOLDOWN) < BUFF_CHECK_INTERVAL) {
            return;
        }
        data.putInt(BUFF_COOLDOWN, maid.tickCount);
        var inv = maid.getAvailableInv(true);
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof PotionItem)) {
                continue;
            }
            List<MobEffectInstance> effects = PotionUtils.getMobEffects(stack);
            boolean useful = false;
            for (MobEffectInstance effect : effects) {
                if (isBuffEffect(effect.getEffect()) && !maid.hasEffect(effect.getEffect())) {
                    useful = true;
                    break;
                }
            }
            if (!useful) {
                continue;
            }
            inv.extractItem(i, 1, false);
            for (MobEffectInstance effect : effects) {
                if (isBuffEffect(effect.getEffect())) {
                    maid.addEffect(new MobEffectInstance(effect));
                }
            }
            maid.level().playSound(null, maid.getX(), maid.getY(), maid.getZ(),
                    SoundEvents.GENERIC_DRINK, maid.getSoundSource(), 0.8F, 1.0F);
            return;
        }
    }

    private static boolean isBuffEffect(net.minecraft.world.effect.MobEffect effect) {
        return effect == MobEffects.DAMAGE_BOOST
                || effect == MobEffects.MOVEMENT_SPEED
                || effect == MobEffects.JUMP
                || effect == MobEffects.DAMAGE_RESISTANCE
                || effect == MobEffects.ABSORPTION
                || effect == MobEffects.HEALTH_BOOST
                || effect == MobEffects.FIRE_RESISTANCE;
    }

    // ==================================================================
    // 目标锁定加固
    // ==================================================================

    /**
     * 威胁评估（每 20 tick）：
     * 1. 存在正在攻击女仆的实体 → 锁定反击（最高威胁），防止多目标时目标跳变；
     * 2. 当前目标被 brain 清除（脱锁）且原锁定目标仍存活 → 重新锁定；
     * 3. 正常情况记录当前目标。
     * <p>
     * TLM 女仆的目标由 brain 的 ATTACK_TARGET memory 驱动（EntityMaid#getTarget），
     * 必须写 memory 才能真正锁定（setAttakTargetSync 仅同步客户端显示）。
     */
    private void lockHighThreatTarget(MaidPatch<?> patch, EntityMaid maid, LivingEntity current) {
        int lockedId = StyleState.getInt(maid, StyleState.LOCKED_TARGET, -1);
        LivingEntity attacker = maid.getLastAttacker();
        // 1. 正在攻击我们的实体 → 锁定反击（仅限竞技场配置标靶：残留女仆 / 其他生物
        //    攻击女仆时不得抢走 Boss 目标——这是之前"目标被攻击者夺走导致脱锁"的根因）
        if (attacker != null && attacker.isAlive() && !attacker.equals(current)
                && maid.distanceTo(attacker) < 12.0 && maid.canAttack(attacker)
                && isArenaTarget(attacker)) {
            lockTarget(patch, maid, attacker);
            return;
        }
        // 2. 脱锁后重新锁定原目标
        if (current == null) {
            if (lockedId >= 0) {
                net.minecraft.world.entity.Entity locked = maid.level().getEntity(lockedId);
                if (locked instanceof LivingEntity le && le.isAlive()
                        && maid.canAttack(le) && maid.distanceTo(le) < 32.0) {
                    lockTarget(patch, maid, le);
                    return;
                }
            }
            StyleState.setInt(maid, StyleState.LOCKED_TARGET, -1);
            return;
        }
        // 3. 记录当前锁定目标
        StyleState.setInt(maid, StyleState.LOCKED_TARGET, current.getId());
    }

    private void lockTarget(MaidPatch<?> patch, EntityMaid maid, LivingEntity target) {
        // TLM 女仆目标由 brain memory 驱动，必须写 memory
        maid.getBrain().setMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.ATTACK_TARGET, target);
        patch.setAttakTargetSync(target);
        StyleState.setInt(maid, StyleState.LOCKED_TARGET, target.getId());
    }

    /** 是否竞技场配置标靶（annoyingvillagers 系 Boss）——非标靶实体不得参与锁定竞争 */
    private static boolean isArenaTarget(LivingEntity entity) {
        String id = net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();
        return id.startsWith("annoyingvillagers:");
    }

    // ==================================================================
    // 枪神收尾状态机
    // ==================================================================

    private void updateFinisher(MaidPatch<?> patch, EntityMaid maid, int tick) {
        // 目标失效 → 立即结束收尾并换回近战（避免无目标时持续瞄准 / 射击动画）
        LivingEntity target = patch.getTarget();
        if (target == null || !target.isAlive()) {
            swapBackToMelee(patch, maid);
            StyleState.setInt(maid, StyleState.FINISHER, 0);
            return;
        }
        int state = StyleState.getInt(maid, StyleState.FINISHER, 0);
        int elapsed = tick - StyleState.getTick(maid, StyleState.FINISHER_START);
        if (state == 1) {
            ItemStack mainHand = maid.getMainHandItem();
            boolean gun = WeaponArsenal.isGun(mainHand);
            // 换手重置完成后播放瞄准动画（枪械无瞄准阶段），并在被打断时维持
            // 专用服务器禁止播放 AimAnimation（getPoseByTime 加载客户端类崩溃），跳过瞄准阶段直接射击
            if (elapsed == 2 && !gun && !DEDICATED_SERVER) {
                WeaponArsenal.Kind kind = WeaponArsenal.classify(mainHand);
                AnimationManager.AnimationAccessor<? extends StaticAnimation> aim =
                        kind == WeaponArsenal.Kind.CROSSBOW ? Animations.BIPED_CROSSBOW_AIM : AnimKit.bowAim();
                patch.playAnimationSynchronized(aim, 0F);
            } else if (elapsed > 2 && !gun && !DEDICATED_SERVER && !patch.getEntityState().inaction()) {
                WeaponArsenal.Kind kind = WeaponArsenal.classify(mainHand);
                AnimationManager.AnimationAccessor<? extends StaticAnimation> aim =
                        kind == WeaponArsenal.Kind.CROSSBOW ? Animations.BIPED_CROSSBOW_AIM : AnimKit.bowAim();
                patch.playAnimationSynchronized(aim, 0F);
            }
            // 瞄准完成 → 射击
            if (elapsed >= FINISHER_SHOOT_TICK) {
                WeaponArsenal.Kind kind = WeaponArsenal.classify(mainHand);
                if ((kind == WeaponArsenal.Kind.BOW || kind == WeaponArsenal.Kind.CROSSBOW)
                        && !WeaponArsenal.hasAmmo(maid, mainHand)) {
                    // 弹药耗尽 → 立即换回近战
                    swapBackToMelee(patch, maid);
                    StyleState.setInt(maid, StyleState.FINISHER, 0);
                    return;
                }
                shoot(patch, maid, mainHand);
                StyleState.setInt(maid, StyleState.FINISHER, 2);
                StyleState.addFlair(maid, 5.0F);
            }
            return;
        }
        // 射击完成 → 持续远程压制或换回近战
        if (state == 2 && elapsed >= FINISHER_SWAP_BACK_TICK) {
            StyleState.setInt(maid, StyleState.FINISHER, 0);
            // 目标仍远且弹药充足 → 保持远程武器，进入下一轮射击（防放风筝）
            ItemStack current = maid.getMainHandItem();
            boolean keepRanged = maid.distanceTo(target) > RANGED_SWITCH_DIST
                    && (WeaponArsenal.classify(current) == WeaponArsenal.Kind.GUN
                        || WeaponArsenal.hasAmmo(maid, current));
            if (keepRanged && CombatActions.startRangedAim(patch)) {
                // 保持远程武器，进入下一轮射击（防放风筝）
            } else {
                // 换装失败（无可用远程武器 / 背包已满）→ 立即换回近战，
                // 避免女仆持弓卡在收尾状态外（FINISHER 已清零且不再射击）
                swapBackToMelee(patch, maid);
            }
        }
    }

    private void shoot(MaidPatch<?> patch, EntityMaid maid, ItemStack mainHand) {
        WeaponArsenal.Kind kind = WeaponArsenal.classify(mainHand);
        LivingEntity target = patch.getTarget();
        if (target == null || !target.isAlive()) {
            return; // 无目标不射击、不播放射击动画
        }
        if (kind == WeaponArsenal.Kind.BOW) {
            WeaponArsenal.shootBow(maid, target);
            patch.playAnimationSynchronized(AnimKit.bowShot(), 0F);
        } else if (kind == WeaponArsenal.Kind.CROSSBOW) {
            WeaponArsenal.shootCrossbow(maid);
            patch.playAnimationSynchronized(AnimKit.crossbowShot(), 0F);
        } else if (kind == WeaponArsenal.Kind.GUN) {
            // 枪械：播放射击动画，伤害与弹道特效由 WOM 动画自身的攻击相位结算
            AnimationManager.AnimationAccessor<? extends StaticAnimation> shot =
                    AnimKit.gunShot(CombatActions.isTwoHand(patch, mainHand));
            if (shot != null) {
                patch.playAnimationSynchronized(shot, 0F);
            }
        }
    }

    private void swapBackToMelee(MaidPatch<?> patch, EntityMaid maid) {
        // 主手本就是枪械（非本次收尾换上的）→ 保持枪械配置；弓/弩一律换回近战
        WeaponArsenal.Kind kind = WeaponArsenal.classify(maid.getMainHandItem());
        if (!maid.getPersistentData().getBoolean(StyleState.FINISHER_SWAPPED) && kind == WeaponArsenal.Kind.GUN) {
            return;
        }
        var melee = WeaponArsenal.scanMelee(maid);
        if (melee.isEmpty()) {
            return;
        }
        WeaponArsenal.forceHand(maid, melee.get(0));
        StyleState.setTick(maid, StyleState.LAST_SWAP, maid.tickCount);
    }

    // ==================================================================
    // 华丽度反馈
    // ==================================================================

    @Override
    public void onHurtTargetPost(MaidHurtTargetEvent.Post event) {
        MaidPatch<?> patch = event.getMaidPatch();
        if (patch == null || !(patch.getOriginal().level() instanceof ServerLevel)) {
            return;
        }
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        StyleState.addFlair(maid, StyleState.HIT_FLAIR);
        StyleState.setTick(maid, StyleState.LAST_HIT, maid.tickCount);
        // P5.6 命中经验（极坐标扇区桶）：记录当前动画的命中距离/角度桶
        org.eftlm.stylish.rl.CombatLibrary.onHit(maid, event.getTarget());
        // P0 观测：命中事件（含距离分类与伤害量，供决策链路回放）
        double hitDist = maid.distanceTo(event.getTarget());
        org.eftlm.stylish.rl.RlTrace.event(maid, "hit",
                String.format("dist=%.1f dmg=%.1f", hitDist, event.getAmount()));
        // P1 霸体观测：命中后检查目标是否产生硬直（HitAnimation/hurtLevel），
        // 连续无硬直由 TargetTracker 判霸体（报告 3.3-5）
        LivingEntity hitTarget = event.getTarget();
        try {
            yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch<?> tp =
                    yesman.epicfight.world.capabilities.EpicFightCapabilities.getEntityPatch(hitTarget,
                            yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch.class);
            boolean hadHitstun = false;
            if (tp != null) {
                hadHitstun = tp.getEntityState().hurtLevel() > 0;
                if (!hadHitstun) {
                    var player = tp.getAnimator().getPlayerFor(null);
                    var real = player != null ? player.getRealAnimation() : null;
                    hadHitstun = real != null && real.get() instanceof yesman.epicfight.api.animation.types.HitAnimation;
                }
            }
            org.eftlm.stylish.rl.TargetTracker.reportHitstun(maid, hitTarget, hadHitstun);
        } catch (Throwable ignored) {
            // 霸体观测失败不影响战斗
        }
        // P5 切换连携奖励（报告 4.2）：切换武器后 10 tick 内命中 → +15（DMC 式切换评分激励）
        int maidTick = maid.tickCount;
        if (maidTick - StyleState.getTick(maid, StyleState.LAST_MELEE_SWAP) <= 10
                || maidTick - StyleState.getTick(maid, StyleState.LAST_SWAP) <= 10) {
            org.eftlm.stylish.rl.RlDataRecorder.addReward(maid, 15);
            org.eftlm.stylish.rl.RlTrace.event(maid, "switch_combo",
                    "hit within 10t of weapon swap, +15");
        }
        // V12：按键战技测试命中反馈（AutoSkill 测试模式）
        org.eftlm.stylish.strategy.AutoSkill.onHit(maid, maid.tickCount, event.getAmount());
        // 动态距离系数（V7 约束）：同样伤害，肉搏(<2格)奖励×3、中程(2-5格)×1、远程(>5格)×0.2
        double dist = maid.distanceTo(event.getTarget());
        int hitReward;
        if (dist < 2.0) {
            hitReward = 30;      // 肉搏：贴脸输出最值钱
        } else if (dist <= 5.0) {
            hitReward = 10;      // 中程
        } else {
            hitReward = 2;       // 远程龟缩：输出大幅贬值
        }
        boolean rangedHit = dist > 5.0;
        // V9：纯远程收益归零（Absolute Zero）——连续远程攻击 ≥4 次后，远程命中不再结算任何奖励
        if (!(rangedHit && org.eftlm.stylish.rl.RlBrain.isRangedStreakDead(maid))) {
            org.eftlm.stylish.rl.RlDataRecorder.addReward(maid, hitReward);
        }
        // V9：突进奖励（Gap-Closing）——近战命中且 20 tick 内位移 ≥2 格（Stinger/瞬移式突进）→ 巨大单次积分
        if (dist < 2.0 && org.eftlm.stylish.rl.RlBrain.isGapClose(maid)) {
            org.eftlm.stylish.rl.RlDataRecorder.addReward(maid, 50);
        }
        // V9：连击热度计数器（近战判定 ≤5 格内命中才累积；3 秒断档熔断由 RlBrain 检查）
        if (dist <= 5.0) {
            maid.getPersistentData().putInt("eftlm_stylish:combo_count",
                    maid.getPersistentData().getInt("eftlm_stylish:combo_count") + 1);
        }
        // 技能层数：命中攒层（满层解锁武器技能大招）
        StyleState.addSkillStack(maid, 1);
        // 命中计数：每累计 6 次命中轮换一次近战武器（不依赖行为表，对所有武器生效）
        int hits = StyleState.getInt(maid, StyleState.HIT_COUNT, 0) + 1;
        if (hits >= StyleState.HIT_COUNT_SWAP) {
            // 轮换成功才清零：冷却中 / 无候选武器时保留计数，下次命中继续尝试
            if (CombatActions.cycleWeapon(patch)) {
                StyleState.setInt(maid, StyleState.HIT_COUNT, 0);
            } else {
                StyleState.setInt(maid, StyleState.HIT_COUNT, hits);
            }
        } else {
            StyleState.setInt(maid, StyleState.HIT_COUNT, hits);
        }
    }

    @Override
    public void onKillTarget(MaidKilledEvent event) {
        MaidPatch<?> patch = event.getMaidPatch();
        if (patch == null) {
            return;
        }
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        StyleState.addFlair(maid, StyleState.KILL_FLAIR);
        StyleState.addSkillStack(maid, 2);
        org.eftlm.stylish.rl.RlDataRecorder.addReward(maid, 100); // 击杀奖励
        org.eftlm.stylish.rl.RlTrace.event(maid, "kill", "target=" + event.getKilledEntity().getType().getDescriptionId());
        StyleState.setInt(maid, StyleState.COMBO_END, 0);
        org.eftlm.stylish.arena.AutoArena.requestSpawn(); // 击杀后立即补标靶（避免 20 秒空窗）
        org.apache.logging.log4j.LogManager.getLogger("eftlm_stylish")
                .info("[SKILL] KILL detected: {} by {} source={}", event.getKilledEntity().getType().getDescriptionId(),
                        maid.getType().getDescriptionId(),
                        event.getDamageSource() != null && event.getDamageSource().getEntity() != null
                                ? event.getDamageSource().getEntity().getType().getDescriptionId() : "null");
    }

    @Override
    public void MaidAttack(MaidAttackEvent event) {
        EntityMaid maid = event.getMaid();
        if (!(maid.level() instanceof ServerLevel level)) {
            return;
        }
        // 环境伤害（火焰 / 毒 / 掉落 / 溺水等，无攻击者实体）不参与弹反 / 格挡判定：
        // 否则持续伤害会被窗口误取消并反复播放格挡动画，导致女仆无法攻击
        DamageSource source = event.getSource();
        if (source == null || !(source.getEntity() instanceof LivingEntity attacker)) {
            return;
        }
        // 出生无敌保护（竞技场：出生 200 tick 内免伤，防止被 Boss 集火秒杀）。
        // 键缺失（非竞技场生成的普通女仆）时 spawnTick=0，不享受保护——
        // 此前 getInt 缺省 0 导致任何未写该键的女仆前 200 tick 全程无敌
        int spawnTick = maid.getPersistentData().getInt("eftlm_stylish:spawn_tick");
        if (spawnTick > 0 && maid.tickCount - spawnTick < 200) {
            event.setCanceled(true);
            return;
        }
        MaidPatch<?> patch = EpicFightCapabilities.getEntityPatch(maid, MaidPatch.class);
        if (patch == null) {
            return;
        }
        // 被攻击 → 立即锁定攻击者（仅限竞技场标靶：残留女仆等非标靶不得抢 Boss 目标）
        if (attacker.isAlive() && !attacker.equals(patch.getTarget()) && isArenaTarget(attacker)) {
            lockTarget(patch, maid, attacker);
        }
        int tick = maid.tickCount;
        int sinceParry = tick - StyleState.getTick(maid, StyleState.LAST_PARRY);
        int sinceBlock = tick - StyleState.getTick(maid, StyleState.BLOCK_START);
        boolean frontBlockable = isFrontAttack(source, maid) && isBlockableSource(source);

        if (frontBlockable && sinceParry <= PARRY_WINDOW) {
            // 弹反窗口内命中 → 全额取消伤害，极大生存奖励 + 霸体免责期
            event.setCanceled(true);
            StyleState.addFlair(maid, StyleState.PARRY_FLAIR);
            StyleState.addSkillStack(maid, 1);
            org.eftlm.stylish.rl.RlDataRecorder.addReward(maid, 50); // 弹反特化奖励（V7 约束）
            org.eftlm.stylish.rl.RlTrace.event(maid, "parry_success",
                    "attacker=" + attacker.getType().getDescriptionId() + " dmg=" + event.getAmount());
            StyleState.setTick(maid, StyleState.PARRIED_TICK, tick); // 免责期起点
            StyleState.setTick(maid, StyleState.LAST_HIT, tick);
            maid.playSound(EpicFightSounds.CLASH.get(), 0.5F, 0.9F);
            if (source.getDirectEntity() != null) {
                EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument(level, HitParticleType.FRONT_OF_EYES,
                        HitParticleType.ZERO, maid, source.getDirectEntity());
            }
        } else if (frontBlockable && sinceBlock <= BLOCK_WINDOW) {
            // 格挡窗口内命中 → 取消伤害，播放格挡受击动画 + 免责期
            event.setCanceled(true);
            StyleState.addFlair(maid, 2.0F);
            org.eftlm.stylish.rl.RlDataRecorder.addReward(maid, 10);
            org.eftlm.stylish.rl.RlTrace.event(maid, "block_success",
                    "attacker=" + attacker.getType().getDescriptionId());
            StyleState.setTick(maid, StyleState.PARRIED_TICK, tick);
            StyleState.setTick(maid, StyleState.LAST_HIT, tick);
            maid.playSound(EpicFightSounds.CLASH.get(), 0.5F, 0.9F);
            patch.playAnimationSynchronized(AnimKit.guardHit(AnimKit.categoryOf(patch)), 0F);
        } else if (tick - StyleState.getTick(maid, StyleState.PARRIED_TICK) < StyleState.PARRY_IMMUNE_TICKS) {
            // 霸体免责期：弹反/格挡成功后短暂屏蔽受击惩罚（鼓励贴脸拼刀而不是后滚开枪）
            StyleState.addFlair(maid, StyleState.HURT_FLAIR);
        } else if (org.eftlm.stylish.rl.ItemCombat.tryPlaceBlockParry(patch, maid, source)) {
            // P2.5：受击放方块格挡（仿 AVNpc Steve/Alex）——背包方块挡下本次伤害
            event.setCanceled(true);
            StyleState.addFlair(maid, 2.0F);
        } else {
            StyleState.addFlair(maid, StyleState.HURT_FLAIR);
            org.eftlm.stylish.rl.RlDataRecorder.addReward(maid, -30); // 受击惩罚（V4 后加重，鼓励规避伤害）
            org.eftlm.stylish.rl.RlTrace.event(maid, "hurt",
                    "attacker=" + attacker.getType().getDescriptionId() + " dmg=" + event.getAmount());
            // P2.5：受击末影珍珠反击（仿 AV doSteveStyleEnderPearlCounter）——不取消伤害
            org.eftlm.stylish.rl.ItemCombat.tryEnderPearlCounter(maid, source);
        }
    }

    /**
     * 参考 EFTLM“刀光剑影”：只有正面且可格挡的攻击才能触发弹反/格挡窗口。
     */
    private boolean isFrontAttack(DamageSource damageSource, Entity entity) {
        Vec3 sourceLocation = damageSource.getSourcePosition();
        Vec3 viewVector = entity.getViewVector(1.0F);
        if (sourceLocation != null) {
            Vec3 toSourceLocation = sourceLocation.subtract(entity.position()).normalize();
            return toSourceLocation.dot(viewVector) > 0.0;
        }
        return false;
    }

    private boolean isBlockableSource(DamageSource damageSource) {
        return !damageSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY)
                && !damageSource.is(yesman.epicfight.world.damagesource.EpicFightDamageTypeTags.UNBLOCKALBE)
                && !damageSource.is(net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR)
                && !damageSource.is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)
                && !damageSource.is(net.minecraft.world.damagesource.DamageTypes.MAGIC)
                && !damageSource.is(net.minecraft.tags.DamageTypeTags.IS_FIRE);
    }
}
