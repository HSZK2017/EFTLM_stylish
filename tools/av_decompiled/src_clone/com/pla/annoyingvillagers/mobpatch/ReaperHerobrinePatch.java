package com.pla.annoyingvillagers.mobpatch;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.combatbehaviour.HerobrineEnderSlayerScythe;
import com.pla.annoyingvillagers.compat.EpicFightNightFall;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import com.pla.annoyingvillagers.util.EscapeUtil;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.fml.ModList;
import net.shelmarow.combat_evolution.ai.CEHumanoidPatch;
import net.shelmarow.combat_evolution.ai.iml.CustomExecuteEntity;
import net.shelmarow.combat_evolution.execution.ExecutionTypeManager.Type;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.animations.weapons.AnimsEnderblaster;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;

public class ReaperHerobrinePatch extends CEHumanoidPatch implements CustomExecuteEntity {
   public ReaperHerobrinePatch() {
      super(Factions.UNDEAD);
   }

   public void initAnimator(Animator animator) {
      super.initAnimator(animator);
      animator.addLivingAnimation(LivingMotions.BLOCK, Animations.BIPED_BLOCK);
      animator.addLivingAnimation(LivingMotions.IDLE, Animations.BIPED_IDLE);
      animator.addLivingAnimation(LivingMotions.WALK, Animations.BIPED_WALK);
      animator.addLivingAnimation(LivingMotions.RUN, Animations.BIPED_RUN);
      animator.addLivingAnimation(LivingMotions.CHASE, Animations.BIPED_RUN);
      animator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH);
   }

   protected void setWeaponMotions() {
      this.weaponLivingMotions
         .put(
            WeaponCategories.SPEAR,
            ImmutableMap.of(
               Styles.TWO_HAND,
               Set.of(
                  Pair.of(LivingMotions.BLOCK, AnimsWom.GLOWING_AGONY_GUARD),
                  Pair.of(LivingMotions.IDLE, AnimsEnderblaster.ENDERBLASTER_TWOHAND_IDLE),
                  Pair.of(LivingMotions.WALK, AnimsEnderblaster.ENDERBLASTER_TWOHAND_IDLE),
                  Pair.of(LivingMotions.RUN, AnimsEnderblaster.ENDERBLASTER_TWOHAND_IDLE),
                  Pair.of(LivingMotions.CHASE, AnimsEnderblaster.ENDERBLASTER_TWOHAND_IDLE),
                  Pair.of(LivingMotions.DEATH, Animations.BIPED_DEATH)
               ),
               Styles.MOUNT,
               Set.of(
                  Pair.of(LivingMotions.BLOCK, AnimsWom.GLOWING_AGONY_GUARD),
                  Pair.of(LivingMotions.IDLE, AnimsEnderblaster.ENDERBLASTER_TWOHAND_IDLE),
                  Pair.of(LivingMotions.WALK, AnimsEnderblaster.ENDERBLASTER_TWOHAND_IDLE),
                  Pair.of(LivingMotions.RUN, AnimsEnderblaster.ENDERBLASTER_TWOHAND_IDLE),
                  Pair.of(LivingMotions.CHASE, AnimsEnderblaster.ENDERBLASTER_TWOHAND_IDLE),
                  Pair.of(LivingMotions.DEATH, Animations.BIPED_DEATH)
               )
            )
         );
      this.weaponAttackMotions
         .put(
            WeaponCategories.SPEAR,
            ImmutableMap.of(Styles.TWO_HAND, HerobrineEnderSlayerScythe.ENDER_SLAYER_SCYTHE, Styles.MOUNT, HerobrineEnderSlayerScythe.ENDER_SLAYER_SCYTHE)
         );
      this.guardHitMotions
         .put(WeaponCategories.SPEAR, ImmutableMap.of(Styles.TWO_HAND, List.of(Animations.SPEAR_GUARD_HIT), Styles.MOUNT, List.of(Animations.SPEAR_GUARD_HIT)));
   }

   public void playGuardHitAnimation(DamageSource damageSource, boolean canCounter) {
      if (ModList.get().isLoaded("efn") && this.getOriginal() instanceof HerobrineMob herobrineMob && herobrineMob.getLivingEntityPatch() != null) {
         EpicFightNightFall.playEfnGuardHit(herobrineMob.getLivingEntityPatch(), herobrineMob.getEfnGuardHitState(), damageSource);
         herobrineMob.postPlayEfnGuardHit();
      } else {
         super.playGuardHitAnimation(damageSource, canCounter);
      }
   }

   public void playGuardHitSound() {
      if (!ModList.get().isLoaded("efn")) {
         super.playGuardHitSound();
      }
   }

   public boolean dealStaminaDamage(DamageSource damageSource, float amount) {
      return ModList.get().isLoaded("efn") && EpicFightNightFall.isPlayingEfnGuardHit(this) ? false : super.dealStaminaDamage(damageSource, amount);
   }

   public AttackResult tryHurt(DamageSource damageSource, float amount) {
      AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(this.getAnimator().getPlayerFor(null)).getRealAnimation();
      if (!this.getOriginal().m_20159_()
         && !EpicfightUtil.isLongHitAnimation(dynamicAnimation, this)
         && this.getOriginal().m_9236_() instanceof ServerLevel
         && dynamicAnimation == Animations.EMPTY_ANIMATION) {
         if (new Random().nextFloat() <= 0.3F) {
            float chance = new Random().nextFloat();
            if (chance <= 0.25F) {
               this.playAnimationSynchronized(WOMAnimations.ENDERSTEP_BACKWARD, 0.0F);
            } else if (chance <= 0.5F) {
               this.playAnimationSynchronized(WOMAnimations.ENDERSTEP_FORWARD, 0.0F);
            } else if (chance <= 0.75F) {
               this.playAnimationSynchronized(WOMAnimations.ENDERSTEP_RIGHT, 0.0F);
            } else {
               this.playAnimationSynchronized(WOMAnimations.ENDERSTEP_RIGHT, 0.0F);
            }
         } else {
            EscapeUtil.stepLeftRightOnHurtByDangerousAnimation(damageSource, this);
         }
      } else {
         EscapeUtil.stepLeftRightOnHurtByDangerousAnimation(damageSource, this);
      }

      return super.tryHurt(damageSource, amount);
   }

   public void playGuardBreakSound() {
      this.playSound((SoundEvent)EpicFightSounds.NEUTRALIZE_MOBS.get(), 0.0F, 0.0F);
   }

   public AttackResult attack(EpicFightDamageSource epicFightDamageSource, Entity entity, InteractionHand interactionhand) {
      AttackResult attackresult = super.attack(epicFightDamageSource, entity, interactionhand);
      if (attackresult.resultType == ResultType.SUCCESS && entity.m_6084_()) {
      }

      return attackresult;
   }

   public void tick(LivingTickEvent livingTickEvent) {
      super.tick(livingTickEvent);
   }

   public void onDeath(LivingDeathEvent livingDeathEvent) {
      super.onDeath(livingDeathEvent);
   }

   public void onGuardHit(DamageSource damageSource) {
      super.onGuardHit(damageSource);
      if (this.getOriginal().m_9236_() instanceof ServerLevel serverLevel) {
         ((HitParticleType)EpicFightParticles.HIT_BLUNT.get())
            .spawnParticleWithArgument(serverLevel, HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, this.getOriginal(), damageSource.m_7639_());
      }

      EpicfightUtil.breakWeaponOnParryOpAttack(damageSource);
   }

   public boolean isBlockableSource(DamageSource damageSource) {
      return true;
   }

   public AnimationAccessor<? extends StaticAnimation> getHitAnimation(StunType stuntype) {
      return switch (stuntype) {
         case LONG -> Animations.BIPED_HIT_LONG;
         case SHORT, HOLD -> Animations.BIPED_HIT_SHORT;
         case KNOCKDOWN -> Animations.BIPED_KNOCKDOWN;
         case NEUTRALIZE -> this.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WeaponCategories.GREATSWORD
         ? Animations.GREATSWORD_GUARD_BREAK
         : Animations.BIPED_COMMON_NEUTRALIZED;
         case FALL -> Animations.BIPED_LANDING;
         default -> null;
      };
   }

   public boolean canBeExecuted(LivingEntityPatch<?> livingEntityPatch) {
      return (Boolean)AnnoyingVillagersConfig.CAN_EXECUTE_AV_MOB.get();
   }

   public boolean canUseCustomType(LivingEntityPatch<?> livingEntityPatch, Type type) {
      return true;
   }

   public Type getExecutionType(LivingEntityPatch<?> livingEntityPatch, Type type) {
      return type;
   }
}
