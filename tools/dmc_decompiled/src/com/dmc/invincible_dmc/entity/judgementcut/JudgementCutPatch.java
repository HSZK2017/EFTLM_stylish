package com.dmc.invincible_dmc.entity.judgementcut;

import com.dmc.invincible_dmc.InvincibleMod_DMC;
import com.dmc.invincible_dmc.api.animation.types.customStun.ICustomStunDamageSource;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.damagesource.DMCDamageTypeTags;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.gameassets.animations.stun.CustomStunAnimations;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import com.merlin204.avalon.epicfight.AvalonFctions;
import java.util.Locale;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightSharedConstants;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;

public class JudgementCutPatch<T extends JudgementCutEntity> extends MobPatch<T> {
   private int tickCount = 0;
   @Nullable
   private LivingEntityPatch<?> ownerPatch;
   public static final TagKey<DamageType> JUDGEMENT_CUT_DAMAGE = InvincibleMod_DMC.createDamageType("dmc_judgement_cut_damage");

   public boolean applyStun(StunType stunType, float stunTime) {
      return false;
   }

   public AssetAccessor<? extends StaticAnimation> getHitAnimation(StunType stunType) {
      return null;
   }

   public Faction getFaction() {
      return AvalonFctions.EMPTY;
   }

   public void onConstructed(T entityIn) {
      this.original = entityIn;
      this.armature = this.getArmature();
      Animator animator = EpicFightSharedConstants.getAnimator(this);
      this.animator = animator;
      this.initAnimator(animator);
      animator.postInit();
   }

   public void tick(LivingTickEvent event) {
      super.tick(event);
      this.tickCount++;
      if (!((JudgementCutEntity)this.original).getPlayAnimation()) {
         AnimationAccessor<? extends StaticAnimation> defaultAnim = ((JudgementCutEntity)this.original).getDefaultAnimation();
         if (this.prepareAnimation(defaultAnim)) {
            if (this.isLogicalClient()) {
               this.getClientAnimator().playAnimation(defaultAnim, 0.0F);
            } else {
               this.playAnimationSynchronized(defaultAnim, 0.0F);
            }

            ((JudgementCutEntity)this.original).setPlayAnimation(true);
            ((JudgementCutEntity)this.original).setShouldRender(true);
         }
      }
   }

   private boolean prepareAnimation(@Nullable AnimationAccessor<? extends StaticAnimation> animationAccessor) {
      if (animationAccessor == null) {
         return false;
      } else {
         try {
            StaticAnimation animation = (StaticAnimation)animationAccessor.get();
            if (animation == null) {
               return false;
            } else {
               Armature loadedArmature = (Armature)animation.getArmature().get();
               if (loadedArmature == null) {
                  return false;
               } else {
                  this.armature = loadedArmature;
                  return animation.getAnimationClip() != null;
               }
            }
         } catch (NullPointerException var4) {
            if (isTransientArmatureReloadFailure(var4)) {
               return false;
            } else {
               throw var4;
            }
         }
      }
   }

   private static boolean isTransientArmatureReloadFailure(NullPointerException exception) {
      String message = exception.getMessage();
      if (message != null && message.toLowerCase(Locale.ROOT).contains("armature")) {
         for (StackTraceElement element : exception.getStackTrace()) {
            if (element.getClassName().equals("yesman.epicfight.api.asset.JsonAssetLoader") && element.getMethodName().equals("loadClipForAnimation")) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public AttackResult attack(EpicFightDamageSource epicFightDamageSource, Entity target, InteractionHand hand) {
      JudgementCutEntity judgementCutEntity = (JudgementCutEntity)this.getOriginal();
      epicFightDamageSource.setStunType(StunType.HOLD).addRuntimeTag(JUDGEMENT_CUT_DAMAGE).addRuntimeTag(DMCDamageTypeTags.NOT_CHARGE);
      ((ICustomStunDamageSource)epicFightDamageSource)
         .invincible$setCustomStunAnimations(
            CustomStunAnimations.HIT_FROM_LEFT,
            CustomStunAnimations.HIT_FROM_RIGHT,
            CustomStunAnimations.HIT_FROM_LEFT_AIR,
            CustomStunAnimations.HIT_FROM_RIGHT_AIR
         );
      if (target instanceof JudgementCutEntity) {
         return AttackResult.missed(0.0F);
      } else if (target == judgementCutEntity.getOwner()) {
         return AttackResult.missed(0.0F);
      } else {
         if (this.getOwnerPatch() instanceof DoppelgangerPatch doppelgangerPatch) {
            PlayerPatch<?> doppelOwnerPatch = doppelgangerPatch.getOwnerPatch();
            if (doppelOwnerPatch != null && target == doppelOwnerPatch.getOriginal()) {
               return AttackResult.missed(0.0F);
            }
         }

         AttackResult attackResult;
         if (this.getOwnerPatch() != null) {
            EpicFightDamageSource modifiedSource = epicFightDamageSource.addRuntimeTag(JUDGEMENT_CUT_DAMAGE);
            attackResult = this.getOwnerPatch().attack(modifiedSource, target, hand);
         } else {
            epicFightDamageSource.addRuntimeTag(JUDGEMENT_CUT_DAMAGE);
            attackResult = super.attack(epicFightDamageSource, target, hand);
         }

         return attackResult;
      }
   }

   public EpicFightDamageSource getDamageSource(AnimationAccessor<? extends StaticAnimation> animation, InteractionHand hand) {
      return this.getOwnerPatch() != null ? this.getOwnerPatch().getDamageSource(animation, hand) : super.getDamageSource(animation, hand);
   }

   public void updateMotion(boolean b) {
      if (b) {
         this.currentLivingMotion = LivingMotions.IDLE;
      }
   }

   @Nullable
   public LivingEntityPatch<?> getOwnerPatch() {
      if (this.ownerPatch != null) {
         return this.ownerPatch;
      } else if (((JudgementCutEntity)this.getOriginal()).getOwner() != null) {
         this.ownerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(
            ((JudgementCutEntity)this.getOriginal()).getOwner(), LivingEntityPatch.class
         );
         return this.ownerPatch;
      } else {
         return null;
      }
   }

   public Armature getArmature() {
      return ((JudgementCutEntity)this.original).getArmature();
   }

   public SoundEvent getSwingSound(InteractionHand hand) {
      return (SoundEvent)DMCSounds.NOSOUND.get();
   }

   public SoundEvent getWeaponHitSound(InteractionHand hand) {
      return (SoundEvent)DMCSounds.NOSOUND.get();
   }

   protected void initAnimator(Animator animator) {
      super.initAnimator(animator);
      animator.addLivingAnimation(LivingMotions.IDLE, ((JudgementCutEntity)this.original).getIdleAnimation());
   }

   public boolean isTargetInvulnerable(Entity entity) {
      if (entity.equals(((JudgementCutEntity)this.getOriginal()).getOwner())) {
         return true;
      } else {
         if (entity instanceof VFXEntity artifactSpiritEntity && this.getOwnerPatch() != null) {
            return ((LivingEntity)this.getOwnerPatch().getOriginal()).equals(artifactSpiritEntity.getOwner());
         }

         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean flashTargetIndicator(LocalPlayerPatch playerPatch) {
      return false;
   }
}
