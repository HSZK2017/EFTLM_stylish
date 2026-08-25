package com.dmc.invincible_dmc.skill.weapon_innate.vergil;

import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboType;
import com.dmc.invincible_dmc.api.skill.JudgementCutNode;
import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import com.dmc.invincible_dmc.api.weapon.WeaponCombatProfile;
import com.dmc.invincible_dmc.capability.DMCPlayer;
import com.dmc.invincible_dmc.capability.doppelganger.DoppelgangerBindingService;
import com.dmc.invincible_dmc.capability.weapon.DmcWeaponManager;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerState;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerStateProvider;
import com.dmc.invincible_dmc.client.DMCKeyMappings;
import com.dmc.invincible_dmc.client.input.DirectionTracker;
import com.dmc.invincible_dmc.client.renderer.SdtWeaponAfterimageManager;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.conditions.DirectionalSequenceCondition;
import com.dmc.invincible_dmc.damagesource.DMCDamageTypeTags;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.entity.judgementcut.JudgementCutEntity;
import com.dmc.invincible_dmc.entity.soul.SoulEntity;
import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordPatch;
import com.dmc.invincible_dmc.gameassets.DMCEffects;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.gameassets.DMCWeaponCategories;
import com.dmc.invincible_dmc.gameassets.DmcWeaponProfiles;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.particle.DMCParticles;
import com.dmc.invincible_dmc.skill.dodge.VergilDodgeSkill;
import com.dmc.invincible_dmc.skill.weapon_innate.ComboBasicAttack;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.DMCLog;
import com.dmc.invincible_dmc.utils.yamato.JCEClient;
import com.google.common.collect.Lists;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.GuardAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPSkillExecutionFeedback;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.AnimationBeginEvent;
import yesman.epicfight.world.entity.eventlistener.DodgeSuccessEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
import yesman.epicfight.world.entity.eventlistener.SkillCastEvent;
import yesman.epicfight.world.entity.eventlistener.DealDamageEvent.Attack;
import yesman.epicfight.world.entity.eventlistener.DealDamageEvent.Damage;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.TakeDamageEvent.Hurt;

public class VergilSkill extends ComboBasicAttack {
   private static final int TOOLTIP_PAGE_COUNT = 10;
   @OnlyIn(Dist.CLIENT)
   private static int tooltipPage;
   @OnlyIn(Dist.CLIENT)
   private static boolean tooltipPreviousLeft;
   @OnlyIn(Dist.CLIENT)
   private static boolean tooltipPreviousRight;
   public static final BiFunction<Entity, Entity, Vector3d> FRONT_OF_EYES = (target, attacker) -> {
      Vec3 eyePosition = target.m_146892_();
      Vec3 viewVec = target.m_20154_().m_82490_(0.7);
      return new Vector3d(eyePosition.f_82479_ + viewVec.f_82479_, eyePosition.f_82480_, eyePosition.f_82481_ + viewVec.f_82481_);
   };
   protected static final UUID EVENT_UUID = UUID.fromString("d1d124cc-f12f-11ed-a05b-1242ac114514");
   @Nullable
   private final JudgementCutNode jcNormalGround;
   @Nullable
   private final JudgementCutNode jcPerfectGround;
   @Nullable
   private final JudgementCutNode jcNormalAir;
   @Nullable
   private final JudgementCutNode jcPerfectAir;
   private final int jcMaxPerfectChain;
   private final DoppelgangerManager doppelgangerManager;
   private boolean prevClientSDT = false;
   private boolean prevClientFirstChargeFired = false;
   private boolean prevClientSecondChargeFired = false;
   private int clientSdtActiveTickCounter = 0;
   private static final long AUTO_REFLECT_BONUS_WINDOW_TICKS = 20L;
   private final Map<UUID, Long> autoReflectBonusWindowUntil = new HashMap<>();

   @Nullable
   public VergilSkill(VergilSkill.JudgmentCutBuilder builder) {
      super(builder);
      this.jcNormalGround = builder.jcNormalGround;
      this.jcPerfectGround = builder.jcPerfectGround;
      this.jcNormalAir = builder.jcNormalAir;
      this.jcPerfectAir = builder.jcPerfectAir;
      this.jcMaxPerfectChain = builder.jcMaxPerfectChain;
      this.doppelgangerManager = new DoppelgangerManager(this.root);
   }

   private static void onSdtEnterExitClient(Player player, boolean enter) {
      if (player.m_9236_().m_5776_()) {
         if (enter) {
            JCEClient.onSDTEnterClient(player);
         } else {
            JCEClient.onSDTExitClient(player);
         }
      }
   }

   public static VergilSkill.JudgmentCutBuilder createJudgmentCutSkill() {
      return new VergilSkill.JudgmentCutBuilder().setCategory(SkillCategories.WEAPON_INNATE).setActivateType(ActivateType.ONE_SHOT).setResource(Resource.NONE);
   }

   @Override
   public ComboNode getRoot(SkillContainer container) {
      WeaponCombatProfile profile = DmcWeaponProfiles.get(DmcWeaponManager.getActiveWeapon((Player)container.getExecutor().getOriginal()));
      return profile != null ? profile.root() : super.getRoot(container);
   }

   @Override
   public int getResetTime(SkillContainer container) {
      WeaponCombatProfile profile = DmcWeaponProfiles.get(DmcWeaponManager.getActiveWeapon((Player)container.getExecutor().getOriginal()));
      return profile != null && profile.comboResetTicks() != 0 ? profile.comboResetTicks() : super.getResetTime(container);
   }

   public boolean isYamatoActive(SkillContainer container) {
      return DmcWeaponManager.isActiveWeapon((Player)container.getExecutor().getOriginal(), DmcWeaponType.YAMATO);
   }

   public static boolean NotHoldingYamato(Player player) {
      return player == null
         ? true
         : Stream.of(player.m_21205_(), player.m_21206_())
            .<CapabilityItem>map(EpicFightCapabilities::getItemStackCapability)
            .filter(Objects::nonNull)
            .noneMatch(cap -> cap.getWeaponCategory() == DMCWeaponCategories.DMC5_YAMATO);
   }

   @Nullable
   public static ComboNode findNodeById(ComboNode root, int id) {
      return findNodeById(root, id, new HashSet<>());
   }

   @Nullable
   private static ComboNode findNodeById(ComboNode root, int id, Set<ComboNode> visited) {
      if (root != null && visited.add(root)) {
         if (root.getId() == id) {
            return root;
         } else {
            for (ComboType type : ComboType.ENUM_MANAGER.universalValues()) {
               ComboNode found = findNodeById(root.getNext(type), id, visited);
               if (found != null) {
                  return found;
               }
            }

            for (ComboNode condNode : root.getConditionNodes()) {
               ComboNode found = findNodeById(condNode, id, visited);
               if (found != null) {
                  return found;
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   private static boolean isInParryWindow(PlayerPatch<?> playerPatch) {
      AnimationPlayer animPlayer = DMCAnimationUtils.getMainPlayer(playerPatch);
      if (animPlayer == null) {
         return false;
      } else {
         StaticAnimation currentAnim = DMCAnimationUtils.getRealAnimation(animPlayer);
         return YamatoAttackAnimation.isParryWindow(currentAnim, animPlayer.getPrevElapsedTime());
      }
   }

   private static void playRangeBlockAnimationIfNeeded(SkillContainer container) {
      AnimationPlayer animationPlayer = DMCAnimationUtils.getMainPlayer(container.getExecutor());
      if (animationPlayer == null
         || !DMCAnimationUtils.sameAccessor(DMCAnimationUtils.getRealAnimationAccessor(animationPlayer), YamatoAnimations.YAMATO_BLOCK_RANGE)) {
         container.getExecutor().playAnimationSynchronized(YamatoAnimations.YAMATO_BLOCK_RANGE, 0.0F);
      }
   }

   private static boolean reflectProjectile(ServerPlayer player, Projectile projectile) {
      if (projectile != null && !projectile.m_213877_()) {
         Entity previousOwner = projectile.m_19749_();
         Vec3 direction = previousOwner != null && previousOwner != player
            ? previousOwner.m_146892_().m_82546_(projectile.m_20182_())
            : projectile.m_20184_().m_82548_();
         if (direction.m_82556_() < 1.0E-8) {
            return false;
         } else {
            double speed = Math.max(projectile.m_20184_().m_82553_(), 0.5);
            Vec3 reflectedVelocity = direction.m_82541_().m_82490_(speed);
            projectile.m_5602_(player);
            projectile.m_20256_(reflectedVelocity);
            projectile.f_19812_ = true;
            projectile.f_19864_ = true;
            projectile.m_146922_((float)(Math.toDegrees(Math.atan2(reflectedVelocity.f_82481_, reflectedVelocity.f_82479_)) - 90.0));
            projectile.m_146926_(
               (float)(
                  -Math.toDegrees(
                     Math.atan2(
                        reflectedVelocity.f_82480_,
                        Math.sqrt(reflectedVelocity.f_82479_ * reflectedVelocity.f_82479_ + reflectedVelocity.f_82481_ * reflectedVelocity.f_82481_)
                     )
                  )
               )
            );
            return true;
         }
      } else {
         return false;
      }
   }

   private static boolean reflectProjectileFromPlayer(ServerPlayer player, Projectile projectile, Vec3 targetPosition) {
      if (projectile != null && !projectile.m_213877_() && targetPosition != null) {
         Vec3 playerEyePosition = player.m_146892_();
         Vec3 direction = targetPosition.m_82546_(playerEyePosition);
         if (direction.m_82556_() < 1.0E-8) {
            return false;
         } else {
            Vec3 normalizedDirection = direction.m_82541_();
            Vec3 reflectionOrigin = playerEyePosition.m_82549_(normalizedDirection.m_82490_(0.75));
            double speed = Math.max(projectile.m_20184_().m_82553_(), 0.5);
            Vec3 reflectedVelocity = normalizedDirection.m_82490_(speed);
            projectile.m_6034_(reflectionOrigin.f_82479_, reflectionOrigin.f_82480_, reflectionOrigin.f_82481_);
            projectile.m_5602_(player);
            projectile.m_20256_(reflectedVelocity);
            projectile.f_19812_ = true;
            projectile.f_19864_ = true;
            projectile.m_146922_((float)(Math.toDegrees(Math.atan2(reflectedVelocity.f_82481_, reflectedVelocity.f_82479_)) - 90.0));
            projectile.m_146926_(
               (float)(
                  -Math.toDegrees(
                     Math.atan2(
                        reflectedVelocity.f_82480_,
                        Math.sqrt(reflectedVelocity.f_82479_ * reflectedVelocity.f_82479_ + reflectedVelocity.f_82481_ * reflectedVelocity.f_82481_)
                     )
                  )
               )
            );
            return true;
         }
      } else {
         return false;
      }
   }

   private static boolean reflectProjectile(ServerPlayer player, DamageSource damageSource) {
      Projectile projectile = damageSource.m_7640_() instanceof Projectile direct
         ? direct
         : (damageSource.m_7639_() instanceof Projectile sourceProjectile ? sourceProjectile : null);
      return reflectProjectile(player, projectile);
   }

   private boolean isAutoReflectBonusWindowActive(SkillContainer container, ServerPlayer player) {
      long windowUntil = (Long)container.getDataManager().getDataValue((SkillDataKey)DMCSkillDataKeys.YAMATO_AUTO_REFLECT_WINDOW_UNTIL.get());
      return windowUntil > player.m_9236_().m_46467_();
   }

   private void applyAutoReflectConcentrationCost(SkillContainer container, ServerPlayer player) {
      if (!this.isAutoReflectBonusWindowActive(container, player)) {
         container.getDataManager().setData((SkillDataKey)DMCSkillDataKeys.YAMATO_AUTO_REFLECT_WINDOW_UNTIL.get(), player.m_9236_().m_46467_() + 20L);
         ConcentrationManager.addConcentration(container, -800.0F);
      }
   }

   public boolean tryReflectProjectile(SkillContainer container, Projectile projectile) {
      return this.tryReflectProjectile(container, projectile, null);
   }

   public boolean tryReflectProjectile(SkillContainer container, Projectile projectile, Vec3 attackSourcePosition) {
      if (container != null
         && projectile != null
         && this.isYamatoActive(container)
         && this.isHoldingWeapon(container)
         && container.getExecutor().getEntityState().updateLivingMotion()) {
         ServerPlayer serverPlayer = (ServerPlayer)container.getServerExecutor().getOriginal();
         Vec3 viewVector = serverPlayer.m_20252_(1.0F);
         Vec3 sourcePosition = attackSourcePosition == null ? projectile.m_20182_() : attackSourcePosition;
         Vec3 attackDirection = sourcePosition.m_82546_(serverPlayer.m_20182_()).m_82541_();
         if (attackDirection.m_82526_(viewVector) <= 0.0) {
            return false;
         } else {
            boolean bonusWindowActive = this.isAutoReflectBonusWindowActive(container, serverPlayer);
            if (ConcentrationManager.getConcentrationTier(container) < 1 && !bonusWindowActive
               || (
                  attackSourcePosition == null
                     ? !reflectProjectile(serverPlayer, projectile)
                     : !reflectProjectileFromPlayer(serverPlayer, projectile, sourcePosition)
               )) {
               if (!isInParryWindow(container.getExecutor())
                  || (
                     attackSourcePosition == null
                        ? !reflectProjectile(serverPlayer, projectile)
                        : !reflectProjectileFromPlayer(serverPlayer, projectile, sourcePosition)
                  )) {
                  return false;
               } else {
                  int containerStack = container.getStack();
                  this.setStackSynchronize(container, containerStack + 1);
                  serverPlayer.m_9236_().m_5594_(null, serverPlayer.m_20183_(), (SoundEvent)DMCSounds.PARRY.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                  ConcentrationManager.addConcentration(container, 700.0F);
                  guardVfx(serverPlayer, projectile);
                  return true;
               }
            } else {
               serverPlayer.m_9236_().m_5594_(null, serverPlayer.m_20183_(), (SoundEvent)DMCSounds.PARRY.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
               playRangeBlockAnimationIfNeeded(container);
               this.applyAutoReflectConcentrationCost(container, serverPlayer);
               guardVfx(serverPlayer, projectile);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   public SinDevilTriggerManager getSDTManager() {
      return this.sinDevilTriggerManager;
   }

   @Override
   public void executeOnServer(SkillContainer container, FriendlyByteBuf args) {
      ComboType parsedType = (ComboType)ComboType.ENUM_MANAGER.get(args.readInt());
      if (parsedType != null) {
         int pressedTime = args.readInt();
         long pressInterval = args.readLong();
         boolean isLongPress = args.isReadable() && args.readBoolean();
         int matchedSequencesMask = 0;
         List<DirectionTracker.DirectionEvent> directionEvents = Collections.emptyList();
         long engineTick = 0L;
         if (args.isReadable()) {
            matchedSequencesMask = args.readInt();
            int eventCount = args.readByte();
            if (eventCount > 0) {
               ArrayList<DirectionTracker.DirectionEvent> events = new ArrayList<>();

               for (int i = 0; i < eventCount; i++) {
                  DirectionalSequenceCondition.Direction dir = DirectionalSequenceCondition.Direction.values()[args.readByte()];
                  long tick = args.readLong();
                  events.add(new DirectionTracker.DirectionEvent(dir, tick));
               }

               directionEvents = events;
            }

            if (args.isReadable()) {
               engineTick = args.readLong();
            }
         }

         super.executeOnServer(container, parsedType, pressedTime, pressInterval, isLongPress, matchedSequencesMask, directionEvents, engineTick);
      }
   }

   @Override
   protected void onSkillCastEvent(SkillCastEvent event, SkillContainer container) {
      SkillCategory skillCategory = event.getSkillContainer().getSkill().getCategory();
      if (this.isYamatoActive(container) && skillCategory.equals(SkillCategories.GUARD)) {
         event.setCanceled(true);
      }

      super.onSkillCastEvent(event, container);
   }

   @Override
   protected void onTakeDamageEventHurt(Hurt event, SkillContainer container) {
      super.onTakeDamageEventHurt(event, container);
      if (SinDevilTriggerManager.isPlayerInSDT((Player)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())) {
         event.attachValueModifier(ValueModifier.multiplier(0.5F));
      }

      if (DoppelgangerBindingService.findBoundEntity((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()) != null) {
         event.attachValueModifier(ValueModifier.multiplier(0.7F));
      }

      if (event.getDamageSource().m_7639_() != null && event.getDamageSource().m_7639_() != container.getExecutor().getOriginal()) {
         container.getSkill().setConsumptionSynchronize(container, container.getResource() + 20.0F);
         float currentSdt = SinDevilTriggerManager.getSDTValue(container);
         SinDevilTriggerManager.setSDTValueRaw(container, currentSdt + 20.0F);
      }
   }

   @Override
   protected void onDodgeSuccess(DodgeSuccessEvent event, SkillContainer container) {
      super.onDodgeSuccess(event, container);
      if (this.isYamatoActive(container)) {
         Animator animator = ((ServerPlayerPatch)event.getPlayerPatch()).getAnimator();
         int containerStack = container.getStack();
         container.getSkill().setStackSynchronize(container, containerStack + 1);
         if (animator != null) {
            AnimationPlayer animationPlayer = DMCAnimationUtils.getMainPlayer(animator);
            if (animationPlayer != null) {
               DynamicAnimation currentAnimation = DMCAnimationUtils.getCurrentAnimation(animationPlayer);
               if (this.isVergilDodgeAnimation(container, currentAnimation)) {
                  container.getDataManager().setData((SkillDataKey)DMCSkillDataKeys.PERFECT_DODGE_CHAIN_FREE.get(), true);
               }

               if (DMCAnimationUtils.isPlaying(event.getPlayerPatch(), YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND, YamatoAnimations.YAMATO_STRIKE)) {
                  ((ServerPlayerPatch)event.getPlayerPatch()).playSound(SoundEvents.f_12516_, 1.35F, 1.0F, 1.0F);
                  container.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.DODGE_COUNTER_SUCCESS_TIMER.get(), 30);
                  ((Player)container.getExecutor().getOriginal())
                     .m_7292_(new MobEffectInstance((MobEffect)DMCEffects.DMC_STUN_IMMUNITY.get(), 100, 0, false, false, false));
                  this.concentrationManager.onGpSuccess(container);
                  this.resetCombo(container);
               }
            }
         }
      }
   }

   @Override
   protected void onDealDamageEventAttack(Attack event, SkillContainer container) {
      super.onDealDamageEventAttack(event, container);
      if (event.getTarget() instanceof SoulEntity soulEntity
         && soulEntity.getOwnerEntity() != null
         && soulEntity.getOwnerEntity().m_7306_(container.getExecutor().getOriginal())) {
         event.setCanceled(true);
      }

      if (event.getTarget() instanceof JudgementCutEntity) {
         event.setCanceled(true);
      }
   }

   @Override
   protected boolean shouldCharge(Damage event, SkillContainer container, DMCPlayer DMCPlayer) {
      return !event.getDamageSource().m_269533_(DMCDamageTypeTags.NOT_CHARGE)
         && !DMCPlayer.isNotCharge()
         && DoppelgangerPatch.getNearestDoppelganger((ServerPlayer)container.getServerExecutor().getOriginal()) == null
         && !event.getDamageSource().m_269533_(DoppelgangerPatch.DOPPELGANGER_DAMAGE)
         && !event.getDamageSource().m_269533_(DMCSummonedSwordPatch.SUMMONED_SWORD_DAMAGE)
         && !event.getDamageSource().m_269533_(DMCSummonedSwordPatch.STORM_SWORD_DAMAGE)
         && !event.getDamageSource().m_269533_(DMCSummonedSwordPatch.SPIRAL_SWORD_DAMAGE);
   }

   @Override
   public void onInitiate(SkillContainer container) {
      super.onInitiate(container);
      if (container.getExecutor().isLogicalClient()) {
         SdtWeaponAfterimageManager.clearAll(((Player)container.getExecutor().getOriginal()).m_20148_());
      }

      PlayerEventListener listener = container.getExecutor().getEventListener();
      listener.addEventListener(
         EventType.ANIMATION_BEGIN_EVENT,
         EVENT_UUID,
         event -> {
            DmcWeaponManager.onAnimationBegin(event);
            if (container.getExecutor() instanceof ServerPlayerPatch serverPlayerPatch
               && !DMCAnimationUtils.isAnimation(event.getAnimation(), YamatoAnimations.YAMATO_SUMMON_DOPPELGANGER_GROUND)
               && !DMCAnimationUtils.isAnimation(event.getAnimation(), YamatoAnimations.YAMATO_SUMMON_DOPPELGANGER_AIR)) {
               DoppelgangerBindingService.cancelPendingSummon((ServerPlayer)serverPlayerPatch.getOriginal());
            }

            this.beginAttackMissTracking(container, event);
         }
      );
      listener.addEventListener(EventType.ANIMATION_END_EVENT, EVENT_UUID, DmcWeaponManager::onAnimationEnd);
      listener.addEventListener(
         EventType.DEAL_DAMAGE_EVENT_HURT,
         EVENT_UUID,
         event -> {
            this.recordAttackCycleHit(container, event.getDamageSource().getAnimation());
            LivingEntity target = event.getTarget();
            MobEffectInstance effect = null;
            if (DMCAnimationUtils.sameAccessor(event.getDamageSource().getAnimation(), YamatoAnimations.YAMATO_VOID_SLASH)) {
               effect = new MobEffectInstance((MobEffect)DMCEffects.SLOW.get(), 60, 0, false, false, false);
            } else if (DMCAnimationUtils.sameAccessor(event.getDamageSource().getAnimation(), YamatoAnimations.YAMATO_PROVOCATION_C)) {
               effect = new MobEffectInstance((MobEffect)DMCEffects.SLOW.get(), 85, 0, false, false, false);
            }

            if (effect != null && target.m_20194_() != null) {
               MobEffectInstance finalEffect = effect;
               target.m_20194_().m_6937_(new TickTask(target.m_20194_().m_129921_() + 1, () -> target.m_7292_(finalEffect)));
            }

            if (!event.getDamageSource().m_269533_(DMCSummonedSwordPatch.HEAVY_RAIN_SWORD_DAMAGE)
               || !event.getDamageSource().m_269533_(DMCSummonedSwordPatch.SUMMONED_SWORD_DAMAGE)) {
               int tier = ConcentrationManager.getConcentrationTier(container);
               if (tier == 1) {
                  event.getDamageSource().attachDamageModifier(ValueModifier.multiplier(1.25F));
               }

               if (tier == 2) {
                  event.getDamageSource().attachDamageModifier(ValueModifier.multiplier(1.65F));
               }

               if (SinDevilTriggerManager.isPlayerInSDT((Player)container.getExecutor().getOriginal())) {
                  event.getDamageSource().attachDamageModifier(ValueModifier.multiplier(1.35F));
               }
            }
         }
      );
      listener.addEventListener(
         EventType.TAKE_DAMAGE_EVENT_ATTACK,
         EVENT_UUID,
         event -> {
            if (this.isYamatoActive(container)) {
               DamageSource damageSource = event.getDamageSource();
               ServerPlayer serverPlayerx = (ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal();
               LivingEntity attacker = damageSource.m_7640_() instanceof LivingEntity ? (LivingEntity)damageSource.m_7640_() : null;
               Vec3 viewVector = ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).m_20252_(1.0F);
               Vec3 attackDirection = Optional.ofNullable(damageSource.m_7270_())
                  .map(pos -> pos.m_82546_(((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).m_20182_()).m_82541_())
                  .orElseGet(() -> attacker != null ? attacker.m_20154_() : viewVector);
               if (event.getDamageSource().m_7639_() == ((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()) {
                  event.setResult(ResultType.MISSED);
                  event.setCanceled(true);
               } else {
                  if (ConcentrationManager.getConcentrationTier(container) >= 1
                     && ConcentrationManager.isConcentrationAvailable(container)
                     && attackDirection.m_82526_(viewVector) > 0.0
                     && event.getDamage() > 0.0F
                     && this.isHoldingWeapon(container)
                     && this.isBlockableSource(damageSource)
                     && container.getExecutor().getEntityState().updateLivingMotion()) {
                     if (!(event.getDamageSource().m_7639_() instanceof Projectile) && !event.getDamageSource().m_269533_(DamageTypeTags.f_268524_)) {
                        ((ServerPlayerPatch)event.getPlayerPatch()).playSound((SoundEvent)DMCSounds.PARRY.get(), 1.0F, 1.0F);
                        event.setResult(ResultType.BLOCKED);
                        event.setParried(true);
                        event.setCanceled(true);
                        boolean useRight = false;
                        SkillDataManager dm = container.getDataManager();
                        SkillDataKey<Boolean> toggleKey = (SkillDataKey<Boolean>)DMCSkillDataKeys.PARRY_TOGGLE.get();
                        if (dm.hasData(toggleKey)) {
                           useRight = (Boolean)dm.getDataValue(toggleKey);
                           dm.setDataSync(toggleKey, !useRight);
                        }

                        AnimationAccessor<GuardAnimation> parryAnim = useRight ? YamatoAnimations.YAMATO_PARRY_RIGHT : YamatoAnimations.YAMATO_PARRY_LEFT;
                        container.getExecutor().playAnimationSynchronized(parryAnim, 0.0F);
                        ConcentrationManager.addConcentration(container, -1000.0F);
                        guardVfx(serverPlayerx, damageSource);
                     } else {
                        ((ServerPlayerPatch)event.getPlayerPatch()).playSound((SoundEvent)DMCSounds.PARRY.get(), 1.0F, 1.0F);
                        event.setResult(ResultType.BLOCKED);
                        event.setCanceled(true);
                        boolean reflected = reflectProjectile(serverPlayerx, damageSource);
                        playRangeBlockAnimationIfNeeded(container);
                        if (reflected) {
                           this.applyAutoReflectConcentrationCost(container, serverPlayerx);
                        }

                        guardVfx(serverPlayerx, damageSource);
                     }
                  }

                  if (event.getDamage() > 0.0F
                     && isInParryWindow(container.getExecutor())
                     && this.isBlockableSource(damageSource)
                     && attackDirection.m_82526_(viewVector) > 0.0) {
                     int containerStack = container.getStack();
                     container.getSkill().setStackSynchronize(container, containerStack + 1);
                     ((ServerPlayerPatch)event.getPlayerPatch()).playSound((SoundEvent)DMCSounds.PARRY.get(), 1.0F, 1.0F);
                     event.setCanceled(true);
                     event.setParried(true);
                     event.setResult(ResultType.BLOCKED);
                     if (damageSource.m_7640_() instanceof Projectile || damageSource.m_7639_() instanceof Projectile) {
                        reflectProjectile(serverPlayerx, damageSource);
                     }

                     ConcentrationManager.addConcentration(container, 700.0F);
                     guardVfx(serverPlayerx, damageSource);
                  }
               }
            }
         },
         -1
      );
      if (!container.getExecutor().isLogicalClient()) {
         ServerPlayer serverPlayer = (ServerPlayer)container.getServerExecutor().getOriginal();
         DoppelgangerEntity.reconcileOwnerState(serverPlayer);
      }
   }

   private static void guardVfx(ServerPlayer serverPlayer, DamageSource damageSource) {
      guardVfx(serverPlayer, damageSource.m_7640_());
   }

   private static void guardVfx(ServerPlayer serverPlayer, Entity sourceEntity) {
      ((HitParticleType)EpicFightParticles.AIR_BURST.get())
         .spawnParticleWithArgument(serverPlayer.m_284548_(), FRONT_OF_EYES, HitParticleType.ZERO, serverPlayer, sourceEntity);
      ((HitParticleType)DMCParticles.PARRY_FLASH_MAIN.get())
         .spawnParticleWithArgument(serverPlayer.m_284548_(), FRONT_OF_EYES, HitParticleType.ZERO, serverPlayer, sourceEntity);
      ((HitParticleType)DMCParticles.ALL_SPARK.get())
         .spawnParticleWithArgument(serverPlayer.m_284548_(), FRONT_OF_EYES, HitParticleType.ZERO, serverPlayer, sourceEntity);
   }

   private boolean isBlockableSource(DamageSource damageSource) {
      return !damageSource.m_269533_(DamageTypeTags.f_268738_)
         && !damageSource.m_269533_(DamageTypeTags.f_268490_)
         && !damageSource.m_269533_(DamageTypeTags.f_268630_)
         && !damageSource.m_269533_(DamageTypeTags.f_268745_);
   }

   @Override
   public void resetCombo(SkillContainer container) {
      super.resetCombo(container);
   }

   public void setStackSynchronize(SkillContainer container, int amount) {
      super.setStackSynchronize(container, amount);
      if (!container.getExecutor().isLogicalClient()) {
         persistDtState(container);
      }
   }

   public void setConsumptionSynchronize(SkillContainer container, float amount) {
      super.setConsumptionSynchronize(container, amount);
      if (!container.getExecutor().isLogicalClient()) {
         persistDtState(container);
      }
   }

   private static void persistDtState(SkillContainer container) {
      YamatoPlayerState state = YamatoPlayerStateProvider.get((Player)container.getExecutor().getOriginal());
      state.setDtStack(container.getStack());
      state.setDtResource(container.getResource());
   }

   public static int getAuthoritativeDtStack(PlayerPatch<?> playerPatch) {
      SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
      if (container != null && !container.isEmpty() && container.getSkill() instanceof VergilSkill) {
         persistDtState(container);
         return container.getStack();
      } else {
         return YamatoPlayerStateProvider.get((Player)playerPatch.getOriginal()).getDtStack();
      }
   }

   public static boolean consumeDoppelgangerDt(PlayerPatch<?> playerPatch, float cost) {
      SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
      VergilSkill skill = container != null && !container.isEmpty() && container.getSkill() instanceof VergilSkill vergilSkill ? vergilSkill : null;
      YamatoPlayerState state = YamatoPlayerStateProvider.get((Player)playerPatch.getOriginal());
      int stack = skill != null ? container.getStack() : state.getDtStack();
      float resource = skill != null ? container.getResource() : state.getDtResource();

      for (float maxResource = skill != null ? container.getMaxResource() : 100.0F; resource < cost; resource += maxResource) {
         if (stack <= 0) {
            return false;
         }

         stack--;
      }

      float remaining = resource - cost;
      if (skill != null) {
         skill.setConsumptionSynchronize(container, remaining);
         skill.setStackSynchronize(container, stack);
      } else {
         state.setDtResource(remaining);
         state.setDtStack(stack);
      }

      return true;
   }

   public void executeAuthoritative(SkillContainer container, DMCPlayer ip, boolean isPerfect, int chainCount, boolean inAir) {
      ServerPlayerPatch spp = container.getServerExecutor();
      ServerPlayer player = (ServerPlayer)spp.getOriginal();
      if (this.isYamatoActive(container) && !spp.isStunned() && player.m_6084_()) {
         JudgementCutNode targetNode = this.getTargetNode(isPerfect, inAir);
         if (targetNode != null && targetNode.getAnimationAccessor() != null) {
            DMCLog.info(
               DMCLog.Category.YAMATO,
               "[JC Server] Authoritative Play -> Perfect: {}, Chain: {}/{}, Air: {}",
               isPerfect,
               chainCount,
               this.jcMaxPerfectChain,
               inAir
            );
            spp.playAnimationSynchronized(targetNode.getAnimationAccessor(), targetNode.getConvertTime());
            this.setCurrentNodeSync(container, this.root);
            this.handleStiff(container, targetNode.getAnimationAccessor());
            targetNode.getOnBeginEvents().forEach(event -> event.testAndExecute(spp, spp.getTarget(), ip));
            if (isPerfect && ConcentrationManager.isConcentrationAvailable(container)) {
               SkillContainer dodgeContainer = spp.getSkill(SkillSlots.DODGE);
               if (!dodgeContainer.isEmpty() && dodgeContainer.getSkill() instanceof VergilDodgeSkill) {
                  SkillDataManager dodgeMgr = dodgeContainer.getDataManager();
                  dodgeMgr.setDataSync((SkillDataKey)DMCSkillDataKeys.UP_DODGE_COUNT.get(), 0);
                  dodgeMgr.setDataSync((SkillDataKey)DMCSkillDataKeys.AIR_DODGE_COUNT.get(), 0);
               }

               YamatoAttackAnimation.setAerialActionCount(container.getExecutor(), 0.0F);
               ConcentrationManager.addConcentration(container, 600.0F);
               if (DoppelgangerPatch.getNearestDoppelganger((ServerPlayer)container.getServerExecutor().getOriginal()) == null) {
                  this.setConsumptionSynchronize(container, container.getResource() + 20.0F);
               }
            }

            if (isPerfect && inAir) {
               container.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.AERIAL_ATTACK_COUNT.get(), 0);
            }

            this.initPlayer(container, ip, targetNode);
            SPSkillExecutionFeedback feedback = SPSkillExecutionFeedback.executed(container.getSlotId());
            feedback.getBuffer().m_130079_(ip.saveNBTData(new CompoundTag()));
            EpicFightNetworkManager.sendToPlayer(feedback, player, new Object[0]);
            this.doppelgangerManager.handleDoppelgangerMirror(player, ip, isPerfect, chainCount, inAir, this);
         } else {
            DMCLog.error(DMCLog.Category.YAMATO, "[JC Server] Target animation node is null!");
         }
      } else {
         DMCLog.warn(DMCLog.Category.YAMATO, "[JC Server] Execution rejected: Player cannot attack.");
      }
   }

   @Nullable
   public JudgementCutNode getTargetNode(boolean isPerfect, boolean inAir) {
      if (isPerfect) {
         return inAir ? this.jcPerfectAir : this.jcPerfectGround;
      } else {
         return inAir ? this.jcNormalAir : this.jcNormalGround;
      }
   }

   public static boolean isDoppelgangerAllowed(@Nullable SkillContainer container) {
      return container != null && !container.isEmpty() && container.getSkill() instanceof ComboBasicAttack;
   }

   public static boolean isDoppelgangerAllowed(@Nullable PlayerPatch<?> playerPatch) {
      if (playerPatch == null) {
         return false;
      } else {
         SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
         if (!isDoppelgangerAllowed(container)) {
            return false;
         } else {
            DmcWeaponType activeWeapon = DmcWeaponManager.getActiveWeapon((Player)playerPatch.getOriginal());
            return activeWeapon == DmcWeaponType.YAMATO;
         }
      }
   }

   public boolean isHoldingWeapon(SkillContainer container) {
      ItemStack itemstack = ((Player)container.getExecutor().getOriginal()).m_21205_();
      return this.isYamatoActive(container)
         && EpicFightCapabilities.getItemStackCapability(itemstack).getInnateSkill(container.getExecutor(), itemstack) == this;
   }

   @Override
   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (container.getExecutor().isLogicalClient()) {
         SkillDataManager dm = container.getDataManager();
         Player player = (Player)container.getExecutor().getOriginal();
         SkillDataKey<Boolean> isSdtKey = (SkillDataKey<Boolean>)DMCSkillDataKeys.IS_SDT.get();
         boolean nowSdt = false;
         if (dm.hasData(isSdtKey)) {
            nowSdt = (Boolean)dm.getDataValue(isSdtKey);
            if (nowSdt != this.prevClientSDT) {
               this.prevClientSDT = nowSdt;
               onSdtEnterExitClient(player, nowSdt);
            }
         }

         if (!SinDevilTriggerManager.isPlayerInSDT((Player)container.getExecutor().getOriginal()) && SinDevilTriggerManager.getSDTValue(container) < 1000.0F) {
            SdtWeaponAfterimageManager.clearSwap(((Player)container.getExecutor().getOriginal()).m_20148_());
         }

         SkillDataKey<Boolean> p1TickKey = (SkillDataKey<Boolean>)DMCSkillDataKeys.SDT_PHASE1_TICK_ACTIVE.get();
         if (dm.hasData(p1TickKey) && (Boolean)dm.getDataValue(p1TickKey)) {
            JCEClient.onSdtCharge1TickClient(player);
         }

         SkillDataKey<Boolean> firstFiredKey = (SkillDataKey<Boolean>)DMCSkillDataKeys.SDT_FIRST_CHARGE_FIRED.get();
         if (dm.hasData(firstFiredKey)) {
            boolean firstFired = (Boolean)dm.getDataValue(firstFiredKey);
            if (firstFired && !this.prevClientFirstChargeFired) {
               JCEClient.onSdtCharge1CompleteClient(player);
            }

            this.prevClientFirstChargeFired = firstFired;
         }

         SkillDataKey<Boolean> secondFiredKey = (SkillDataKey<Boolean>)DMCSkillDataKeys.SDT_SECOND_CHARGE_FIRED.get();
         if (dm.hasData(secondFiredKey)) {
            boolean secondFired = (Boolean)dm.getDataValue(secondFiredKey);
            if (secondFired && !this.prevClientSecondChargeFired) {
               JCEClient.onSdtCharge2CompleteClient(player);
            }

            this.prevClientSecondChargeFired = secondFired;
         }

         if (nowSdt) {
            this.clientSdtActiveTickCounter++;
            if (this.clientSdtActiveTickCounter % 5 == 0) {
               JCEClient.onSdtActiveTickClient(player);
            }
         } else {
            this.clientSdtActiveTickCounter = 0;
         }
      }

      if (!container.getExecutor().isLogicalClient()) {
         this.doppelgangerManager.checkDoppelDimension(container);
         this.clearPerfectDodgeChainIfAnimationEnded(container);
         if (this.isYamatoActive(container) && ((Player)container.getExecutor().getOriginal()).m_20096_()) {
            YamatoAttackAnimation.setAerialActionCount(container.getExecutor(), 0.0F);
         }

         this.checkAttackMiss(container);
      }
   }

   public boolean isDodgeConcentrationFree(SkillContainer container, @Nullable DynamicAnimation currentAnimation) {
      if (!(Boolean)container.getDataManager().getDataValue((SkillDataKey)DMCSkillDataKeys.PERFECT_DODGE_CHAIN_FREE.get())) {
         return false;
      } else if (this.isVergilDodgeAnimation(container, currentAnimation)) {
         return true;
      } else {
         container.getDataManager().setData((SkillDataKey)DMCSkillDataKeys.PERFECT_DODGE_CHAIN_FREE.get(), false);
         return false;
      }
   }

   private void clearPerfectDodgeChainIfAnimationEnded(SkillContainer container) {
      if ((Boolean)container.getDataManager().getDataValue((SkillDataKey)DMCSkillDataKeys.PERFECT_DODGE_CHAIN_FREE.get())) {
         AnimationPlayer animationPlayer = DMCAnimationUtils.getMainPlayer(container.getExecutor());
         DynamicAnimation currentAnimation = animationPlayer == null ? null : DMCAnimationUtils.getCurrentAnimation(animationPlayer);
         if (!this.isVergilDodgeAnimation(container, currentAnimation)) {
            container.getDataManager().setData((SkillDataKey)DMCSkillDataKeys.PERFECT_DODGE_CHAIN_FREE.get(), false);
         }
      }
   }

   private boolean isVergilDodgeAnimation(SkillContainer container, @Nullable DynamicAnimation animation) {
      SkillContainer dodgeContainer = container.getExecutor().getSkill(SkillSlots.DODGE);
      return dodgeContainer != null
         && !dodgeContainer.isEmpty()
         && dodgeContainer.getSkill() instanceof VergilDodgeSkill dodgeSkill
         && dodgeSkill.isDodgeAnimation(animation);
   }

   private void beginAttackMissTracking(SkillContainer container, AnimationBeginEvent event) {
      if (!container.getExecutor().isLogicalClient()) {
         StaticAnimation animation = event.getAnimation();
         AssetAccessor<?> accessor = animation.getAccessor();
         if (DMCAnimationUtils.isAnimationType(animation, AvalonAttackAnimation.class)
            && !this.isAttackMissExcluded(accessor)
            && this.hasAttackMissTrackingData(container)) {
            ResourceLocation registryName = accessor.registryName();
            if (registryName != null) {
               SkillDataManager dataManager = container.getDataManager();
               SkillDataKey<Integer> cycleIdKey = (SkillDataKey<Integer>)DMCSkillDataKeys.ATTACK_CYCLE_ID.get();
               int currentCycleId = (Integer)dataManager.getDataValue(cycleIdKey);
               int nextCycleId = currentCycleId == Integer.MAX_VALUE ? 1 : currentCycleId + 1;
               dataManager.setDataSync(cycleIdKey, nextCycleId);
               dataManager.setDataSync((SkillDataKey)DMCSkillDataKeys.LAST_MISS_PROCESSED.get(), registryName);
               dataManager.setDataSync((SkillDataKey)DMCSkillDataKeys.ATTACK_CYCLE_HIT_MASK.get(), 0);
               dataManager.setDataSync((SkillDataKey)DMCSkillDataKeys.ATTACK_CYCLE_PROCESSED_MASK.get(), 0);
            }
         }
      }
   }

   private void recordAttackCycleHit(SkillContainer container, AssetAccessor<?> animation) {
      if (this.isTrackedAttack(container, animation)) {
         AnimationPlayer player = DMCAnimationUtils.getMainPlayer(container.getExecutor());
         AvalonAttackAnimation attackAnimation = DMCAnimationUtils.asAnimation(DMCAnimationUtils.getAnimation(animation), AvalonAttackAnimation.class);
         if (player != null && attackAnimation != null) {
            float elapsedTime = player.getElapsedTime();

            for (int phaseIndex = 0; phaseIndex < attackAnimation.phases.length; phaseIndex++) {
               Phase phase = attackAnimation.phases[phaseIndex];
               float phaseStart = phase.start + phase.preDelay;
               float phaseEnd = phase.start + phase.contact;
               if (elapsedTime >= phaseStart && elapsedTime <= phaseEnd) {
                  this.markAttackPhaseHit(container, phaseIndex);
               }
            }
         }
      }
   }

   private void checkAttackMiss(SkillContainer container) {
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(container.getExecutor());
      AvalonAttackAnimation attackAnimation = DMCAnimationUtils.asAnimation(DMCAnimationUtils.getCurrentAnimation(player), AvalonAttackAnimation.class);
      if (player != null && attackAnimation != null) {
         AssetAccessor<?> animation = DMCAnimationUtils.getRealAnimationAccessor(player);
         if (this.isTrackedAttack(container, animation)) {
            float previousElapsedTime = player.getPrevElapsedTime();
            float elapsedTime = player.getElapsedTime();

            for (int phaseIndex = 0; phaseIndex < attackAnimation.phases.length && phaseIndex < 31; phaseIndex++) {
               Phase phase = attackAnimation.phases[phaseIndex];
               float phaseContactTime = phase.start + phase.contact;
               if (previousElapsedTime < phaseContactTime && elapsedTime >= phaseContactTime) {
                  this.finishAttackMissPhase(container, attackAnimation, phaseIndex);
               }
            }
         }
      }
   }

   private void finishAttackMissPhase(SkillContainer container, AvalonAttackAnimation animation, int phaseIndex) {
      int phaseMask = 1 << phaseIndex;
      SkillDataManager dataManager = container.getDataManager();
      int processedMask = (Integer)dataManager.getDataValue((SkillDataKey)DMCSkillDataKeys.ATTACK_CYCLE_PROCESSED_MASK.get());
      if ((processedMask & phaseMask) == 0) {
         if (!container.getExecutor().getCurrentlyActuallyHitEntities().isEmpty() || !container.getExecutor().getCurrentlyAttackTriedEntities().isEmpty()) {
            this.markAttackPhaseHit(container, phaseIndex);
         }

         int hitMask = (Integer)dataManager.getDataValue((SkillDataKey)DMCSkillDataKeys.ATTACK_CYCLE_HIT_MASK.get());
         if ((hitMask & phaseMask) == 0) {
            this.concentrationManager.onAttackMissed(container, animation);
         }

         dataManager.setDataSync((SkillDataKey)DMCSkillDataKeys.ATTACK_CYCLE_PROCESSED_MASK.get(), processedMask | phaseMask);
      }
   }

   private void markAttackPhaseHit(SkillContainer container, int phaseIndex) {
      if (phaseIndex < 31) {
         SkillDataManager dataManager = container.getDataManager();
         int phaseMask = 1 << phaseIndex;
         int hitMask = (Integer)dataManager.getDataValue((SkillDataKey)DMCSkillDataKeys.ATTACK_CYCLE_HIT_MASK.get());
         if ((hitMask & phaseMask) == 0) {
            dataManager.setDataSync((SkillDataKey)DMCSkillDataKeys.ATTACK_CYCLE_HIT_MASK.get(), hitMask | phaseMask);
         }
      }
   }

   private boolean isTrackedAttack(SkillContainer container, AssetAccessor<?> animation) {
      if (animation != null && this.hasAttackMissTrackingData(container)) {
         SkillDataManager dataManager = container.getDataManager();
         if ((Integer)dataManager.getDataValue((SkillDataKey)DMCSkillDataKeys.ATTACK_CYCLE_ID.get()) <= 0) {
            return false;
         } else {
            ResourceLocation trackedName = (ResourceLocation)dataManager.getDataValue((SkillDataKey)DMCSkillDataKeys.LAST_MISS_PROCESSED.get());
            if (trackedName == null) {
               return false;
            } else {
               if (animation instanceof AnimationAccessor<?> animationAccessor) {
                  ResourceLocation animationName = animationAccessor.registryName();
                  if (animationName != null) {
                     return animationName.equals(trackedName);
                  }
               }

               AnimationAccessor<?> trackedAccessor = AnimationManager.byKey(trackedName);
               return trackedAccessor != null && DMCAnimationUtils.sameAccessor(animation, trackedAccessor);
            }
         }
      } else {
         return false;
      }
   }

   private void clearAttackMissTracking(SkillContainer container) {
      SkillDataManager dataManager = container.getDataManager();
      if (dataManager.hasData((SkillDataKey)DMCSkillDataKeys.LAST_MISS_PROCESSED.get())) {
         dataManager.setDataSync((SkillDataKey)DMCSkillDataKeys.LAST_MISS_PROCESSED.get(), ResourceLocation.fromNamespaceAndPath("empty", "empty"));
      }

      if (dataManager.hasData((SkillDataKey)DMCSkillDataKeys.ATTACK_CYCLE_HIT_MASK.get())) {
         dataManager.setDataSync((SkillDataKey)DMCSkillDataKeys.ATTACK_CYCLE_HIT_MASK.get(), 0);
      }

      if (dataManager.hasData((SkillDataKey)DMCSkillDataKeys.ATTACK_CYCLE_PROCESSED_MASK.get())) {
         dataManager.setDataSync((SkillDataKey)DMCSkillDataKeys.ATTACK_CYCLE_PROCESSED_MASK.get(), 0);
      }
   }

   private boolean hasAttackMissTrackingData(SkillContainer container) {
      SkillDataManager dataManager = container.getDataManager();
      return dataManager.hasData((SkillDataKey)DMCSkillDataKeys.LAST_MISS_PROCESSED.get())
         && dataManager.hasData((SkillDataKey)DMCSkillDataKeys.ATTACK_CYCLE_ID.get())
         && dataManager.hasData((SkillDataKey)DMCSkillDataKeys.ATTACK_CYCLE_HIT_MASK.get())
         && dataManager.hasData((SkillDataKey)DMCSkillDataKeys.ATTACK_CYCLE_PROCESSED_MASK.get());
   }

   private boolean isAttackMissExcluded(AssetAccessor<?> animation) {
      return DMCAnimationUtils.isOneOfAccessor(
         animation,
         YamatoAnimations.YAMATO_JUDGEMENT_CUT_END,
         YamatoAnimations.YAMATO_JUDGEMENT_CUT_END_INSTANT,
         YamatoAnimations.YAMATO_PROVOCATION_B_AERIAL,
         YamatoAnimations.YAMATO_PROVOCATION_C,
         YamatoAnimations.YAMATO_PROVOCATION_D,
         YamatoAnimations.YAMATO_PROVOCATION_PORTAL,
         YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND,
         YamatoAnimations.YAMATO_JUDGEMENT_CUT_AIR,
         YamatoAnimations.YAMATO_JUDGEMENT_CUT_GROUND_FS,
         YamatoAnimations.YAMATO_JUDGEMENT_CUT_AIR_FS
      );
   }

   @Override
   public void onRemoved(SkillContainer container) {
      if (container.getExecutor() instanceof ServerPlayerPatch serverPlayerPatch) {
         this.clearAttackMissTracking(container);
         container.getDataManager().setData((SkillDataKey)DMCSkillDataKeys.PERFECT_DODGE_CHAIN_FREE.get(), false);
         persistDtState(container);
         DmcWeaponManager.flushPendingRender(serverPlayerPatch);
      }

      super.onRemoved(container);
      PlayerEventListener listener = container.getExecutor().getEventListener();
      listener.removeListener(EventType.ANIMATION_BEGIN_EVENT, EVENT_UUID);
      listener.removeListener(EventType.ANIMATION_END_EVENT, EVENT_UUID);
      listener.removeListener(EventType.DEAL_DAMAGE_EVENT_HURT, EVENT_UUID);
      listener.removeListener(EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID, -1);
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      List<Component> list = Lists.newArrayList();
      int page = updateTooltipPage();
      String tooltipKey = this.getTranslationKey() + ".tooltip";
      String pageKey = tooltipKey + ".page." + (page + 1);
      list.add(Component.m_237115_(this.getTranslationKey()).m_130944_(new ChatFormatting[]{ChatFormatting.AQUA, ChatFormatting.BOLD}));
      list.add(Component.m_237110_(tooltipKey + ".page_indicator", new Object[]{page + 1, 10}).m_130940_(ChatFormatting.DARK_GRAY));
      list.add(Component.m_237115_(pageKey + ".title").m_130944_(new ChatFormatting[]{ChatFormatting.GOLD, ChatFormatting.BOLD, ChatFormatting.UNDERLINE}));
      list.addAll(buildTooltipPage(page, tooltipKey));
      list.add(Component.m_237119_());
      Minecraft minecraft = Minecraft.m_91087_();
      list.add(
         Component.m_237110_(tooltipKey + ".navigation", new Object[]{boundKey(minecraft.f_91066_.f_92086_), boundKey(minecraft.f_91066_.f_92088_)})
            .m_130940_(ChatFormatting.DARK_GRAY)
      );
      return list;
   }

   @OnlyIn(Dist.CLIENT)
   private static List<Component> buildTooltipPage(int page, String tooltipKey) {
      Minecraft minecraft = Minecraft.m_91087_();
      KeyMapping attack = DMCKeyMappings.KEY1;
      KeyMapping rangedAttack = DMCKeyMappings.KEY2;
      KeyMapping styleSkill = DMCKeyMappings.KEY3;
      KeyMapping forward = minecraft.f_91066_.f_92085_;
      KeyMapping back = minecraft.f_91066_.f_92087_;
      KeyMapping left = minecraft.f_91066_.f_92086_;
      KeyMapping right = minecraft.f_91066_.f_92088_;
      String pageKey = tooltipKey + ".page." + (page + 1);

      return switch (page) {
         case 0 -> List.of(
         tooltipLine(pageKey, 1, term(tooltipKey, "concentration", ChatFormatting.AQUA)),
         tooltipLine(
            pageKey,
            2,
            term(tooltipKey, "concentration", ChatFormatting.AQUA),
            term(tooltipKey, "finisher", ChatFormatting.RED),
            term(tooltipKey, "auto_guard", ChatFormatting.GREEN)
         ),
         tooltipLine(pageKey, 3, term(tooltipKey, "gain", ChatFormatting.GREEN)),
         tooltipLine(pageKey, 4, term(tooltipKey, "loss", ChatFormatting.RED))
      );
         case 1 -> List.of(
         tooltipLine(pageKey, 1, term(tooltipKey, "magic", ChatFormatting.BLUE), term(tooltipKey, "sdt", ChatFormatting.LIGHT_PURPLE)),
         tooltipLine(
            pageKey,
            2,
            term(tooltipKey, "sdt", ChatFormatting.LIGHT_PURPLE),
            term(tooltipKey, "sdt_value", ChatFormatting.LIGHT_PURPLE),
            boundKey(DMCKeyMappings.SDT_CHARGE)
         ),
         tooltipLine(pageKey, 3, term(tooltipKey, "sdt", ChatFormatting.LIGHT_PURPLE))
      );
         case 2 -> List.of(
         tooltipLine(
            pageKey, 1, term(tooltipKey, "yamato_combo_1", ChatFormatting.AQUA), boundKey(attack), term(tooltipKey, "sdt", ChatFormatting.LIGHT_PURPLE)
         ),
         tooltipLine(
            pageKey, 2, term(tooltipKey, "yamato_combo_2", ChatFormatting.AQUA), boundKey(attack), term(tooltipKey, "sdt", ChatFormatting.LIGHT_PURPLE)
         ),
         tooltipLine(
            pageKey, 3, term(tooltipKey, "yamato_combo_3", ChatFormatting.AQUA), boundKey(attack), term(tooltipKey, "sdt", ChatFormatting.LIGHT_PURPLE)
         ),
         tooltipLine(
            pageKey,
            4,
            term(tooltipKey, "upper_slash_1", ChatFormatting.AQUA),
            term(tooltipKey, "upper_slash_2", ChatFormatting.AQUA),
            boundKey(back),
            boundKey(attack)
         ),
         tooltipLine(
            pageKey,
            5,
            term(tooltipKey, "rapid_slash", ChatFormatting.AQUA),
            boundKey(forward),
            boundKey(attack),
            term(tooltipKey, "sdt", ChatFormatting.LIGHT_PURPLE)
         ),
         tooltipLine(pageKey, 6, term(tooltipKey, "rising_star", ChatFormatting.AQUA), term(tooltipKey, "rapid_slash", ChatFormatting.AQUA), boundKey(attack)),
         tooltipLine(
            pageKey,
            7,
            term(tooltipKey, "void_slash", ChatFormatting.AQUA),
            boundKey(back),
            boundKey(forward),
            boundKey(attack),
            term(tooltipKey, "sdt", ChatFormatting.LIGHT_PURPLE)
         )
      );
         case 3 -> List.of(
         tooltipLine(pageKey, 1, term(tooltipKey, "aerial_rave_1", ChatFormatting.AQUA), boundKey(attack)),
         tooltipLine(pageKey, 2, term(tooltipKey, "aerial_rave_2", ChatFormatting.AQUA), boundKey(attack)),
         tooltipLine(
            pageKey,
            3,
            term(tooltipKey, "aerial_cleave", ChatFormatting.AQUA),
            boundKey(back),
            boundKey(attack),
            term(tooltipKey, "sdt", ChatFormatting.LIGHT_PURPLE)
         )
      );
         case 4 -> List.of(
         tooltipLine(
            pageKey,
            1,
            term(tooltipKey, "judgement_cut", ChatFormatting.AQUA),
            boundKey(attack),
            term(tooltipKey, "perfect_judgement_cut", ChatFormatting.GREEN),
            term(tooltipKey, "sdt", ChatFormatting.LIGHT_PURPLE)
         ),
         tooltipLine(
            pageKey,
            2,
            term(tooltipKey, "judgement_cut_end", ChatFormatting.RED),
            boundKey(forward),
            boundKey(attack),
            boundKey(styleSkill),
            term(tooltipKey, "sdt_value", ChatFormatting.LIGHT_PURPLE),
            term(tooltipKey, "sdt", ChatFormatting.LIGHT_PURPLE)
         ),
         tooltipLine(
            pageKey,
            3,
            term(tooltipKey, "aerial_judgement_cut_end", ChatFormatting.RED),
            boundKey(forward),
            boundKey(attack),
            boundKey(styleSkill),
            term(tooltipKey, "sdt_value", ChatFormatting.LIGHT_PURPLE),
            term(tooltipKey, "sdt", ChatFormatting.LIGHT_PURPLE)
         ),
         tooltipLine(
            pageKey,
            4,
            term(tooltipKey, "void_stinger", ChatFormatting.RED),
            boundKey(back),
            boundKey(attack),
            boundKey(styleSkill),
            term(tooltipKey, "sdt_value", ChatFormatting.LIGHT_PURPLE),
            term(tooltipKey, "sdt", ChatFormatting.LIGHT_PURPLE)
         ),
         tooltipLine(pageKey, 5, term(tooltipKey, "rift_dance", ChatFormatting.AQUA), boundKey(forward), boundKey(EpicFightKeyMappings.DODGE))
      );
         case 5 -> List.of(
         tooltipLine(pageKey, 1, term(tooltipKey, "trick", ChatFormatting.AQUA), boundKey(styleSkill)),
         tooltipLine(pageKey, 2, term(tooltipKey, "spiral_swords", ChatFormatting.AQUA), boundKey(rangedAttack), term(tooltipKey, "magic", ChatFormatting.BLUE)),
         tooltipLine(
            pageKey,
            3,
            term(tooltipKey, "blistering_swords", ChatFormatting.AQUA),
            boundKey(forward),
            boundKey(rangedAttack),
            term(tooltipKey, "magic", ChatFormatting.BLUE)
         ),
         tooltipLine(
            pageKey,
            4,
            term(tooltipKey, "storm_swords", ChatFormatting.AQUA),
            boundKey(back),
            boundKey(rangedAttack),
            term(tooltipKey, "magic", ChatFormatting.BLUE)
         ),
         tooltipLine(
            pageKey,
            5,
            term(tooltipKey, "heavy_rain", ChatFormatting.AQUA),
            boundKey(back),
            boundKey(forward),
            boundKey(rangedAttack),
            term(tooltipKey, "magic", ChatFormatting.BLUE)
         )
      );
         case 6 -> List.of(
         tooltipLine(pageKey, 1, term(tooltipKey, "doppelganger", ChatFormatting.LIGHT_PURPLE), term(tooltipKey, "magic", ChatFormatting.BLUE)),
         tooltipLine(
            pageKey,
            2,
            boundKey(DMCKeyMappings.DOPPEL_CONTROL),
            boundKey(DMCKeyMappings.DOPPEL_DISCARD),
            boundKey(DMCKeyMappings.DOPPEL_FAST),
            boundKey(DMCKeyMappings.DOPPEL_MEDIUM),
            boundKey(DMCKeyMappings.DOPPEL_SLOW)
         ),
         tooltipLine(pageKey, 3, term(tooltipKey, "doppelganger", ChatFormatting.LIGHT_PURPLE)),
         tooltipLine(
            pageKey,
            4,
            term(tooltipKey, "fast_mode", ChatFormatting.AQUA),
            term(tooltipKey, "medium_mode", ChatFormatting.BLUE),
            term(tooltipKey, "slow_mode", ChatFormatting.LIGHT_PURPLE)
         ),
         tooltipLine(pageKey, 5, term(tooltipKey, "magic", ChatFormatting.BLUE), term(tooltipKey, "damage_reduction", ChatFormatting.GREEN)),
         tooltipLine(
            pageKey,
            6,
            boundKey(DMCKeyMappings.DOPPEL_CONTROL),
            boundKey(DMCKeyMappings.DOPPEL_DISCARD),
            term(tooltipKey, "perfect_judgement_cut", ChatFormatting.GREEN)
         )
      );
         case 7 -> List.of(
         tooltipLine(pageKey, 1, boundKey(DMCKeyMappings.PROVOCATION), boundKey(forward), boundKey(back)),
         tooltipLine(pageKey, 2, boundKey(back), boundKey(forward), boundKey(DMCKeyMappings.PROVOCATION)),
         tooltipLine(pageKey, 3, boundKey(left), boundKey(right), boundKey(DMCKeyMappings.PROVOCATION)),
         tooltipLine(pageKey, 4, boundKey(DMCKeyMappings.SDT_CHARGE), boundKey(DMCKeyMappings.PROVOCATION)),
         tooltipLine(pageKey, 5, boundKey(DMCKeyMappings.PROVOCATION), boundKey(back))
      );
         case 8 -> List.of(
         tooltipLine(pageKey, 1, boundKey(attack)), tooltipLine(pageKey, 2), tooltipLine(pageKey, 3), tooltipLine(pageKey, 4), tooltipLine(pageKey, 5)
      );
         default -> List.of(
         tooltipLine(pageKey, 1, term(tooltipKey, "attack", ChatFormatting.AQUA), boundKey(attack)),
         tooltipLine(pageKey, 2, term(tooltipKey, "ranged_attack", ChatFormatting.AQUA), boundKey(rangedAttack)),
         tooltipLine(pageKey, 3, term(tooltipKey, "style_skill", ChatFormatting.AQUA), boundKey(styleSkill)),
         tooltipLine(pageKey, 4, term(tooltipKey, "dodge", ChatFormatting.AQUA), boundKey(EpicFightKeyMappings.DODGE)),
         tooltipLine(pageKey, 5, term(tooltipKey, "taunt", ChatFormatting.AQUA), boundKey(DMCKeyMappings.PROVOCATION)),
         tooltipLine(pageKey, 6, term(tooltipKey, "sdt", ChatFormatting.LIGHT_PURPLE), boundKey(DMCKeyMappings.SDT_CHARGE)),
         tooltipLine(pageKey, 7, boundKey(forward), boundKey(back), boundKey(left), boundKey(right)),
         tooltipLine(
            pageKey,
            8,
            term(tooltipKey, "doppel_control", ChatFormatting.LIGHT_PURPLE),
            boundKey(DMCKeyMappings.DOPPEL_CONTROL),
            term(tooltipKey, "doppel_discard", ChatFormatting.LIGHT_PURPLE),
            boundKey(DMCKeyMappings.DOPPEL_DISCARD)
         ),
         tooltipLine(
            pageKey,
            9,
            term(tooltipKey, "fast_mode", ChatFormatting.AQUA),
            boundKey(DMCKeyMappings.DOPPEL_FAST),
            term(tooltipKey, "medium_mode", ChatFormatting.BLUE),
            boundKey(DMCKeyMappings.DOPPEL_MEDIUM),
            term(tooltipKey, "slow_mode", ChatFormatting.LIGHT_PURPLE),
            boundKey(DMCKeyMappings.DOPPEL_SLOW)
         )
      );
      };
   }

   @OnlyIn(Dist.CLIENT)
   private static MutableComponent tooltipLine(String pageKey, int line, Object... args) {
      return Component.m_237110_(pageKey + ".line." + line, args).m_130940_(ChatFormatting.GRAY);
   }

   @OnlyIn(Dist.CLIENT)
   private static MutableComponent term(String tooltipKey, String name, ChatFormatting color) {
      return Component.m_237115_(tooltipKey + ".term." + name).m_130944_(new ChatFormatting[]{color, ChatFormatting.BOLD});
   }

   @OnlyIn(Dist.CLIENT)
   private static MutableComponent boundKey(KeyMapping keyMapping) {
      return Component.m_237113_("[")
         .m_130940_(ChatFormatting.DARK_GRAY)
         .m_7220_(boundKeyName(keyMapping).m_130944_(new ChatFormatting[]{ChatFormatting.YELLOW, ChatFormatting.BOLD}))
         .m_7220_(Component.m_237113_("]").m_130940_(ChatFormatting.DARK_GRAY));
   }

   @OnlyIn(Dist.CLIENT)
   private static MutableComponent boundKeyName(KeyMapping keyMapping) {
      Key key = keyMapping.getKey();
      if (key.m_84868_() == Type.MOUSE) {
         if (key.m_84873_() == 0) {
            return Component.m_237115_("key.invincible_dmc.mouse_left");
         }

         if (key.m_84873_() == 1) {
            return Component.m_237115_("key.invincible_dmc.mouse_right");
         }

         if (key.m_84873_() == 4) {
            return Component.m_237115_("key.invincible_dmc.mouse_upper_side");
         }

         if (key.m_84873_() == 3) {
            return Component.m_237115_("key.invincible_dmc.mouse_lower_side");
         }
      }

      return keyMapping.m_90863_().m_6881_();
   }

   @OnlyIn(Dist.CLIENT)
   private static int updateTooltipPage() {
      Minecraft minecraft = Minecraft.m_91087_();
      boolean leftPressed = isTooltipNavigationKeyDown(minecraft, minecraft.f_91066_.f_92086_);
      boolean rightPressed = isTooltipNavigationKeyDown(minecraft, minecraft.f_91066_.f_92088_);
      boolean leftJustPressed = leftPressed && !tooltipPreviousLeft;
      boolean rightJustPressed = rightPressed && !tooltipPreviousRight;
      if (leftJustPressed != rightJustPressed) {
         int direction = rightJustPressed ? 1 : -1;
         tooltipPage = Math.floorMod(tooltipPage + direction, 10);
      }

      tooltipPreviousLeft = leftPressed;
      tooltipPreviousRight = rightPressed;
      return tooltipPage;
   }

   @OnlyIn(Dist.CLIENT)
   private static boolean isTooltipNavigationKeyDown(Minecraft minecraft, KeyMapping keyMapping) {
      Key key = keyMapping.getKey();
      long window = minecraft.m_91268_().m_85439_();
      if (key.m_84868_() == Type.MOUSE) {
         return GLFW.glfwGetMouseButton(window, key.m_84873_()) == 1;
      } else {
         return key.m_84868_() == Type.KEYSYM ? InputConstants.m_84830_(window, key.m_84873_()) : keyMapping.m_90857_();
      }
   }

   public static class JudgmentCutBuilder extends ComboBasicAttack.Builder {
      @Nullable
      JudgementCutNode jcNormalGround;
      @Nullable
      JudgementCutNode jcPerfectGround;
      @Nullable
      JudgementCutNode jcNormalAir;
      @Nullable
      JudgementCutNode jcPerfectAir;
      int jcGroundChargeTicks = 12;
      int jcAirChargeTicks = 10;
      int jcMaxPerfectChain = 3;

      public VergilSkill.JudgmentCutBuilder setCategory(SkillCategory category) {
         return (VergilSkill.JudgmentCutBuilder)super.setCategory(category);
      }

      public VergilSkill.JudgmentCutBuilder setActivateType(ActivateType activateType) {
         return (VergilSkill.JudgmentCutBuilder)super.setActivateType(activateType);
      }

      public VergilSkill.JudgmentCutBuilder setResource(Resource resource) {
         return (VergilSkill.JudgmentCutBuilder)super.setResource(resource);
      }

      public VergilSkill.JudgmentCutBuilder setCombo(ComboNode root) {
         return (VergilSkill.JudgmentCutBuilder)super.setCombo(root);
      }

      public VergilSkill.JudgmentCutBuilder setResetTime(int resetTime) {
         return (VergilSkill.JudgmentCutBuilder)super.setResetTime(resetTime);
      }

      public VergilSkill.JudgmentCutBuilder setInputBufferDurationTicks(int v) {
         return (VergilSkill.JudgmentCutBuilder)super.setInputBufferDurationTicks(v);
      }

      public VergilSkill.JudgmentCutBuilder setInputBufferCapacity(int v) {
         return (VergilSkill.JudgmentCutBuilder)super.setInputBufferCapacity(v);
      }

      public VergilSkill.JudgmentCutBuilder setShouldDrawGui(boolean v) {
         return (VergilSkill.JudgmentCutBuilder)super.setShouldDrawGui(v);
      }

      public VergilSkill.JudgmentCutBuilder setWalkBeginAnim(AnimationAccessor<? extends StaticAnimation> v) {
         return (VergilSkill.JudgmentCutBuilder)super.setWalkBeginAnim(v);
      }

      public VergilSkill.JudgmentCutBuilder setWalkEndAnim(AnimationAccessor<? extends StaticAnimation> v) {
         return (VergilSkill.JudgmentCutBuilder)super.setWalkEndAnim(v);
      }

      public VergilSkill.JudgmentCutBuilder setAllowJumpCancel(boolean v) {
         return (VergilSkill.JudgmentCutBuilder)super.setAllowJumpCancel(v);
      }

      public VergilSkill.JudgmentCutBuilder addToolTipOnItem(List<String> v) {
         return (VergilSkill.JudgmentCutBuilder)super.addToolTipOnItem(v);
      }

      public VergilSkill.JudgmentCutBuilder setSkillTextureLocation(ResourceLocation v) {
         return (VergilSkill.JudgmentCutBuilder)super.setSkillTextureLocation(v);
      }

      public VergilSkill.JudgmentCutBuilder setJCNormalGround(JudgementCutNode sub) {
         this.jcNormalGround = sub;
         return this;
      }

      public VergilSkill.JudgmentCutBuilder setJCPerfectGround(JudgementCutNode sub) {
         this.jcPerfectGround = sub;
         return this;
      }

      public VergilSkill.JudgmentCutBuilder setJCNormalAir(JudgementCutNode sub) {
         this.jcNormalAir = sub;
         return this;
      }

      public VergilSkill.JudgmentCutBuilder setJCPerfectAir(JudgementCutNode sub) {
         this.jcPerfectAir = sub;
         return this;
      }
   }
}
