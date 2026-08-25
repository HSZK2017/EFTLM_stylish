package com.dmc.invincible_dmc.entity.summonedsword;

import com.dmc.invincible_dmc.InvincibleMod_DMC;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import com.merlin204.avalon.epicfight.AvalonFctions;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightSharedConstants;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.damagesource.StunType;

public class DMCSummonedSwordPatch<T extends DMCSummonedSwordEntity> extends MobPatch<T> {
   private int tickCount = 0;
   @Nullable
   private LivingEntityPatch<?> ownerPatch;
   public static final TagKey<DamageType> HEAVY_RAIN_SWORD_DAMAGE = InvincibleMod_DMC.createDamageType("dmc_heavy_rain_sword_damage");
   public static final TagKey<DamageType> BLAST_SWORD_DAMAGE = InvincibleMod_DMC.createDamageType("dmc_blast_sword_damage");
   public static final TagKey<DamageType> SUMMONED_SWORD_DAMAGE = InvincibleMod_DMC.createDamageType("dmc_summoned_sword_damage");
   public static final TagKey<DamageType> SPINE_SUMMONED_SWORD_DAMAGE = InvincibleMod_DMC.createDamageType("dmc_spine_summoned_sword_damage");
   public static final TagKey<DamageType> STORM_SWORD_DAMAGE = InvincibleMod_DMC.createDamageType("dmc_storm_sword_damage");
   public static final TagKey<DamageType> SPIRAL_SWORD_DAMAGE = InvincibleMod_DMC.createDamageType("dmc_spiral_sword_damage");
   private boolean hasCachedZRot = false;
   private float cachedZRot = 0.0F;
   private static final int TARGET_SCAN_INTERVAL = 10;
   private int targetScanCooldown = 0;
   @Nullable
   private LivingEntity cachedTarget;

   public boolean isHostileMob(Entity entity) {
      if (!(entity instanceof LivingEntity)) {
         return false;
      } else {
         return entity instanceof DoppelgangerEntity
            ? false
            : entity instanceof Enemy
               || entity instanceof Monster
               || entity instanceof Mob mob && mob.m_5448_() == ((DMCSummonedSwordEntity)this.original).getOwner()
               || entity.m_6095_() == EntityType.f_20526_
               || entity.m_6095_() == EntityType.f_20468_
               || entity.m_6095_() == EntityType.f_20509_
               || entity.m_6095_() == EntityType.f_20453_;
      }
   }

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

   public void updateMotion(boolean b) {
      if (b) {
         this.currentLivingMotion = LivingMotions.IDLE;
      }
   }

   public void tick(LivingTickEvent event) {
      super.tick(event);
      this.tickCount++;
      if (!((DMCSummonedSwordEntity)this.original).getPlayAnimation()) {
         AnimationAccessor<? extends StaticAnimation> defaultAnim = ((DMCSummonedSwordEntity)this.original).getDefaultAnimation();
         if (this.prepareAnimation(defaultAnim)) {
            if (this.isLogicalClient()) {
               this.getClientAnimator().playAnimation(defaultAnim, 0.0F);
            } else {
               this.playAnimationSynchronized(defaultAnim, 0.0F);
            }

            ((DMCSummonedSwordEntity)this.original).setPlayAnimation(true);
            ((DMCSummonedSwordEntity)this.original).setShouldRender(true);
         }
      }

      if (((DMCSummonedSwordEntity)this.getOriginal()).isInStandby()) {
         if (!((DMCSummonedSwordEntity)this.getOriginal()).isBlast() && !((DMCSummonedSwordEntity)this.getOriginal()).isNoAim()) {
            if (--this.targetScanCooldown <= 0 || this.cachedTarget == null || !this.cachedTarget.m_6084_()) {
               this.targetScanCooldown = 10;
               this.cachedTarget = this.target();
            }

            if (this.cachedTarget != null && this.cachedTarget.m_6084_()) {
               ((DMCSummonedSwordEntity)this.getOriginal()).aimAtEntity(this.cachedTarget);
            } else if (this.getOwnerPatch() != null) {
               float ownerY = ((LivingEntity)this.getOwnerPatch().getOriginal()).m_6080_();
               float ownerX = ((LivingEntity)this.getOwnerPatch().getOriginal()).m_146909_();
               ((DMCSummonedSwordEntity)this.getOriginal()).m_146922_(ownerY);
               ((DMCSummonedSwordEntity)this.getOriginal()).m_5618_(ownerY);
               ((DMCSummonedSwordEntity)this.getOriginal()).m_5616_(ownerY);
               ((DMCSummonedSwordEntity)this.getOriginal()).setSyncXRot(ownerX);
            }
         }
      } else {
         ((DMCSummonedSwordEntity)this.getOriginal()).m_5618_(((DMCSummonedSwordEntity)this.getOriginal()).m_146908_());
         ((DMCSummonedSwordEntity)this.getOriginal()).f_20884_ = ((DMCSummonedSwordEntity)this.getOriginal()).m_146908_();
         ((DMCSummonedSwordEntity)this.getOriginal()).m_5616_(((DMCSummonedSwordEntity)this.getOriginal()).m_146908_());
         ((DMCSummonedSwordEntity)this.getOriginal()).f_20886_ = ((DMCSummonedSwordEntity)this.getOriginal()).m_146908_();
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

   @Nullable
   public LivingEntityPatch<?> getOwnerPatch() {
      if (this.ownerPatch != null) {
         return this.ownerPatch;
      } else if (((DMCSummonedSwordEntity)this.getOriginal()).getOwner() != null) {
         this.ownerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(
            ((DMCSummonedSwordEntity)this.getOriginal()).getOwner(), LivingEntityPatch.class
         );
         return this.ownerPatch;
      } else {
         return null;
      }
   }

   public OpenMatrix4f getModelMatrix(float partialTicks) {
      return super.getModelMatrix(partialTicks)
         .scale(
            ((DMCSummonedSwordEntity)this.original).m_6134_(),
            ((DMCSummonedSwordEntity)this.original).m_6134_(),
            ((DMCSummonedSwordEntity)this.original).m_6134_()
         );
   }

   public OpenMatrix4f getMatrix(float partialTicks) {
      return super.getMatrix(partialTicks)
         .scale(
            ((DMCSummonedSwordEntity)this.original).m_6134_(),
            ((DMCSummonedSwordEntity)this.original).m_6134_(),
            ((DMCSummonedSwordEntity)this.original).m_6134_()
         );
   }

   public Armature getArmature() {
      return ((DMCSummonedSwordEntity)this.original).getArmature();
   }

   public LivingEntity target() {
      LivingEntityPatch<?> ownerPatch = this.getOwnerPatch();
      if (ownerPatch == null) {
         return null;
      } else if (ownerPatch.getTarget() != null) {
         return ownerPatch.getTarget();
      } else if (this.getTarget() != null) {
         return this.getTarget();
      } else {
         Level level = ((DMCSummonedSwordEntity)this.getOriginal()).m_9236_();
         double range = 16.0;
         LivingEntity ownerLiving = (LivingEntity)ownerPatch.getOriginal();
         List<Entity> nearbyEntities = level.m_6249_(
            ownerLiving,
            ownerLiving.m_20191_().m_82400_(range),
            entityx -> this.isHostileMob(entityx) && entityx != ownerLiving && !(entityx instanceof DoppelgangerEntity)
         );
         Entity nearestTarget = null;
         double minDistance = Double.MAX_VALUE;

         for (Entity entity : nearbyEntities) {
            double distance = ownerLiving.m_20280_(entity);
            if (level.m_45547_(new ClipContext(ownerLiving.m_20299_(1.0F), entity.m_20299_(1.0F), Block.COLLIDER, Fluid.NONE, ownerLiving)).m_6662_()
                  == Type.MISS
               && distance < minDistance) {
               minDistance = distance;
               nearestTarget = entity;
            }
         }

         return nearestTarget instanceof LivingEntity ? (LivingEntity)nearestTarget : null;
      }
   }

   public void poseTick(DynamicAnimation animation, Pose pose, float elapsedTime, float partialTick) {
      float zRot = 0.0F;
      float yRot = 0.0F;
      float interpolatedPitch;
      if (((DMCSummonedSwordEntity)this.original).isHeavyRain()) {
         interpolatedPitch = 90.0F;
         if (!this.hasCachedZRot) {
            long seed = ((DMCSummonedSwordEntity)this.original).m_20148_().getMostSignificantBits()
               ^ ((DMCSummonedSwordEntity)this.original).m_20148_().getLeastSignificantBits();
            this.cachedZRot = new Random(seed).nextFloat() * 360.0F;
            this.hasCachedZRot = true;
         }

         zRot = this.cachedZRot;
      } else if (((DMCSummonedSwordEntity)this.original).isProvocation()) {
         interpolatedPitch = 45.0F;
      } else if (((DMCSummonedSwordEntity)this.original).isSpine()) {
         interpolatedPitch = 0.0F;
      } else {
         interpolatedPitch = ((DMCSummonedSwordEntity)this.original).f_19860_
            + (((DMCSummonedSwordEntity)this.original).m_146909_() - ((DMCSummonedSwordEntity)this.original).f_19860_) * partialTick;
      }

      AvalonAnimationUtils.joinRotationInPose(pose, this, "Root", -interpolatedPitch, 0.0F, zRot);
   }

   public SoundEvent getSwingSound(InteractionHand hand) {
      return (SoundEvent)DMCSounds.NOSOUND.get();
   }

   public SoundEvent getWeaponHitSound(InteractionHand hand) {
      return (SoundEvent)DMCSounds.NOSOUND.get();
   }

   protected void initAnimator(Animator animator) {
      super.initAnimator(animator);
      animator.addLivingAnimation(LivingMotions.IDLE, ((DMCSummonedSwordEntity)this.original).getIdleAnimation());
   }

   public boolean isTargetInvulnerable(Entity entity) {
      if (entity.equals(((DMCSummonedSwordEntity)this.getOriginal()).getOwner())) {
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
