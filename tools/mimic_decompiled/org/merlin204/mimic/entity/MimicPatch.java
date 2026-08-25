package org.merlin204.mimic.entity;

import com.merlin204.avalon.item.IChangeArmatureItem;
import com.merlin204.avalon.item.animationitem.IAvalonAnimationItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import org.jetbrains.annotations.Nullable;
import org.merlin204.mimic.copy.CopyAnimationInfo;
import org.merlin204.mimic.entity.ai.MimicChaseGoal;
import org.merlin204.mimic.epicfight.MimicAnimations;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.main.EpicFightSharedConstants;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;

public class MimicPatch<T extends MimicEntity> extends MobPatch<T> {
   public Vec3 animationStartPos;
   public float animationStartYRot;
   public final List<AssetAccessor<? extends StaticAnimation>> WHITE_LIST = List.of(
      MimicAnimations.SKILL_1, MimicAnimations.SKILL_2, MimicAnimations.SKILL_3, MimicAnimations.PHASE_2, MimicAnimations.COME
   );
   public CopyAnimationInfo copyAnimationInfoNow;
   public Map<AssetAccessor<? extends AttackAnimation>, CopyAnimationInfo> copyMap = new HashMap<>();
   public List<CopyAnimationInfo> comboList = new ArrayList<>();
   public int combo = 0;
   public int clearTime = 0;

   protected void initAnimator(Animator animator) {
      super.initAnimator(animator);
      animator.addLivingAnimation(LivingMotions.IDLE, MimicAnimations.IDLE);
      animator.addLivingAnimation(LivingMotions.WALK, MimicAnimations.WALK);
      animator.addLivingAnimation(LivingMotions.DEATH, MimicAnimations.DEATH);
   }

   public Faction getFaction() {
      return Factions.WITHER;
   }

   protected void initAI() {
      super.initAI();
      ((MimicEntity)this.original).f_21345_.m_25352_(1, new MimicChaseGoal(this, 2.0F));
   }

   public void tryPlayCombo() {
      if (this.copyAnimationInfoNow != null && this.comboList.size() >= 5 && this.getEntityState().canBasicAttack()) {
         this.playCopyAnimation(this.copyAnimationInfoNow);
      }
   }

   public void playCopyAnimation(@Nullable CopyAnimationInfo copyAnimationInfo) {
      if (copyAnimationInfo != null) {
         this.clearTime = 0;
         Animator animator = this.getAnimator();
         AnimationPlayer animationPlayer = animator == null ? null : animator.getPlayerFor(null);
         AssetAccessor<? extends DynamicAnimation> currentAnimation = animationPlayer == null ? null : animationPlayer.getAnimation();
         if (currentAnimation == null || currentAnimation.get() == null || !((DynamicAnimation)currentAnimation.get()).isLinkAnimation()) {
            if (this.copyMap.size() > 15) {
               int number = ((MimicEntity)this.getOriginal()).m_217043_().m_188503_(this.copyMap.values().size());
               if (this.copyMap.values().toArray()[number] instanceof CopyAnimationInfo copyAnimationInfo1) {
                  this.comboList.add(copyAnimationInfo1);
               }
            }

            if (this.comboList.size() < 5) {
               if (!this.comboList.contains(copyAnimationInfo)) {
                  this.comboList.add(copyAnimationInfo);
               }

               copyAnimationInfo.playAnimation();
            } else {
               if (this.combo < 0 || this.combo >= this.comboList.size()) {
                  this.combo = 0;
               }

               this.comboList.get(this.combo).playAnimation();
               this.combo++;
               if (this.combo == 5) {
                  this.combo = 0;
                  this.comboList.clear();
               }
            }
         }
      }
   }

   public void playCanHitAnimation() {
      List<CopyAnimationInfo> canAtkInfo = new ArrayList<>();

      for (CopyAnimationInfo copyAnimationInfo : this.copyMap.values()) {
         if (copyAnimationInfo != null) {
            copyAnimationInfo.tick();
            if (copyAnimationInfo.checkCanHit() && (copyAnimationInfo != this.copyAnimationInfoNow || this.copyMap.size() < 4)) {
               canAtkInfo.add(copyAnimationInfo);
            }
         }
      }

      if (!canAtkInfo.isEmpty()) {
         this.playCopyAnimation(canAtkInfo.get(((MimicEntity)this.original).m_217043_().m_188503_(canAtkInfo.size())));
      }
   }

   public void playRandomAnimation() {
      if (!this.copyMap.isEmpty()) {
         int number = ((MimicEntity)this.getOriginal()).m_217043_().m_188503_(this.copyMap.values().size());
         if (this.copyMap.values().toArray()[number] instanceof CopyAnimationInfo copyAnimationInfo) {
            this.playCopyAnimation(copyAnimationInfo);
         }
      }
   }

   public void playDodgeAnimation() {
      this.playAnimationSynchronized(MimicAnimations.SKILL_2, 0.0F);
   }

   public void playRandomAnimationWithoutCanHit() {
      List<CopyAnimationInfo> canUes = new ArrayList<>();

      for (CopyAnimationInfo copyAnimationInfo : this.copyMap.values()) {
         if (copyAnimationInfo != null) {
            copyAnimationInfo.tick();
            if (this.getEntityState().canBasicAttack()
               && !copyAnimationInfo.checkCanHit()
               && (copyAnimationInfo != this.copyAnimationInfoNow || this.copyMap.size() < 4)) {
               canUes.add(copyAnimationInfo);
            }
         }
      }

      if (!canUes.isEmpty()) {
         this.playCopyAnimation(canUes.get(((MimicEntity)this.original).m_217043_().m_188503_(canUes.size())));
      } else {
         this.playCanHitAnimation();
      }
   }

   public void tick(LivingTickEvent event) {
      super.tick(event);
      if (!(((MimicEntity)this.getOriginal()).m_21223_() < 3.0F)) {
         if (!((MimicEntity)this.original).m_9236_().f_46443_) {
            if (!this.getEntityState().inaction()) {
               this.clearTime++;
               if (this.clearTime == 5) {
                  ((MimicEntity)this.getOriginal()).m_21008_(InteractionHand.MAIN_HAND, ItemStack.f_41583_);
                  ((MimicEntity)this.getOriginal()).m_21008_(InteractionHand.OFF_HAND, ItemStack.f_41583_);
                  this.clearTime = 0;
               }
            }

            Vec3 Pos = ((MimicEntity)this.getOriginal()).m_20182_();
            Level world = ((MimicEntity)this.getOriginal()).m_9236_();
            AABB searchArea = new AABB(
               Pos.f_82479_ - 30.0, Pos.f_82480_ - 2.0, Pos.f_82481_ - 30.0, Pos.f_82479_ + 30.0, Pos.f_82480_ + 2.0, Pos.f_82481_ + 30.0
            );

            for (LivingEntity livingEntity : world.m_6443_(LivingEntity.class, searchArea, e -> e.m_6084_() && e != this.getOriginal())) {
               this.tickNearbyEntity(livingEntity);
            }
         }
      }
   }

   public void tickNearbyEntity(LivingEntity entity) {
      if (entity != null) {
         this.tryToLearnAnimation(entity);
      }
   }

   public void tryToLearnAnimation(@Nullable LivingEntity livingEntity) {
      if (livingEntity != null) {
         LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(livingEntity, LivingEntityPatch.class);
         if (livingEntityPatch != null && livingEntityPatch.getArmature() instanceof HumanoidArmature) {
            for (MobEffectInstance mobEffectInstance : livingEntity.m_21220_()) {
               if (mobEffectInstance.m_19544_().m_19483_() == MobEffectCategory.BENEFICIAL) {
                  ((MimicEntity)this.original).m_7292_(mobEffectInstance);
               }
            }

            Animator animator = livingEntityPatch.getAnimator();
            AnimationPlayer animationPlayer = animator == null ? null : animator.getPlayerFor(null);
            AssetAccessor<? extends DynamicAnimation> animationAccessor = animationPlayer == null ? null : animationPlayer.getAnimation();
            if (animationAccessor != null && animationAccessor.get() instanceof AttackAnimation animation) {
               ItemStack main = livingEntity.m_21205_();
               ItemStack off = livingEntity.m_21206_();
               if (main.m_41720_() instanceof IAvalonAnimationItem
                  || main.m_41720_() instanceof IChangeArmatureItem
                  || off.m_41720_() instanceof IAvalonAnimationItem
                  || off.m_41720_() instanceof IChangeArmatureItem
                  || animation.getRegistryName() != null && animation.getRegistryName().toString().contains("wom")) {
                  if (livingEntity instanceof Player player) {
                     float random = player.m_217043_().m_188501_();
                     Component translatedMessage = Component.m_237115_("mod.mimic.tip_1");
                     if (random > 0.66F) {
                        translatedMessage = Component.m_237115_("mod.mimic.tip_2");
                     }

                     if (random < 0.33F) {
                        translatedMessage = Component.m_237115_("mod.mimic.tip_3");
                     }

                     ((MimicEntity)this.getOriginal()).m_5634_(3.0F);
                     player.m_213846_(translatedMessage);
                  }
               } else if (main != ItemStack.f_41583_) {
                  CopyAnimationInfo copyAnimationInfo = new CopyAnimationInfo(animation.getAccessor(), this, livingEntity.m_21205_(), livingEntity.m_21206_());
                  if (animation.getTotalTime() < 8.0F) {
                     this.copyMap.put(animation.getAccessor(), copyAnimationInfo);
                  }
               }
            }
         }
      }
   }

   public void playAnimationSynchronized(AssetAccessor<? extends StaticAnimation> animation, float transitionTimeModifier) {
      StaticAnimation resolvedAnimation = animation == null ? null : (StaticAnimation)animation.get();
      if (resolvedAnimation != null) {
         if (this.copyMap.containsKey(animation)
            || this.WHITE_LIST.contains(animation)
            || !(resolvedAnimation instanceof ActionAnimation)
            || !(this.getStunShield() > 0.0F)) {
            this.animationStartYRot = ((MimicEntity)this.original).m_146908_();
            this.animationStartPos = ((MimicEntity)this.original).m_20182_();
            super.playAnimationSynchronized(animation, transitionTimeModifier);
         }
      }
   }

   public AttackResult attack(EpicFightDamageSource damageSource, Entity target, InteractionHand hand) {
      if (damageSource != null && target != null && hand != null) {
         AttackResult attackResult = super.attack(damageSource, target, hand);
         if (attackResult == null) {
            return AttackResult.missed(0.0F);
         } else {
            int animation = this.copyMap.size();
            if (attackResult.resultType.dealtDamage()) {
               ((MimicEntity)this.original).m_5634_(attackResult.damage * Math.min(0.1F + (float)animation * 0.1F, 1.0F));
            }

            if (this.copyAnimationInfoNow != null && target != null && this.animationStartPos != null) {
               this.copyAnimationInfoNow.addRectangle(this.animationStartPos, this.animationStartYRot, target.m_20182_());
            }

            return attackResult;
         }
      } else {
         return AttackResult.missed(0.0F);
      }
   }

   public void onConstructed(T entityIn) {
      this.original = entityIn;
      this.armature = entityIn.getArmature();
      Animator animator = EpicFightSharedConstants.getAnimator(this);
      this.animator = animator;
      this.initAnimator(animator);
      animator.postInit();
      if (!((MimicEntity)this.original).m_9236_().f_46443_) {
         this.setMaxStunShield(100.0F);
         this.setStunShield(100.0F);
      }
   }

   public void updateMotion(boolean considerInaction) {
      if (((MimicEntity)this.original).m_21223_() <= 0.0F) {
         this.currentLivingMotion = LivingMotions.DEATH;
      } else if (this.state.inaction() && considerInaction) {
         this.currentLivingMotion = LivingMotions.IDLE;
      } else if (((MimicEntity)this.original).m_20202_() != null) {
         this.currentLivingMotion = LivingMotions.MOUNT;
      } else if (!(((MimicEntity)this.original).m_20184_().f_82480_ < -0.55F) && !this.isAirborneState()) {
         if ((double)((MimicEntity)this.original).f_267362_.m_267731_() > 0.005) {
            if (((MimicEntity)this.original).m_5912_()) {
               this.currentLivingMotion = LivingMotions.CHASE;
            } else {
               this.currentLivingMotion = LivingMotions.WALK;
            }
         } else {
            this.currentLivingMotion = LivingMotions.IDLE;
         }
      } else {
         this.currentLivingMotion = LivingMotions.FALL;
      }

      this.currentCompositeMotion = this.currentLivingMotion;
   }

   public AssetAccessor<? extends StaticAnimation> getHitAnimation(StunType stunType) {
      if (stunType == null) {
         return null;
      } else if (this.getStunShield() > 0.0F) {
         return null;
      } else {
         return switch (stunType) {
            case LONG -> Animations.BIPED_HIT_LONG;
            case SHORT -> Animations.BIPED_HIT_SHORT;
            case HOLD -> Animations.BIPED_HIT_SHORT;
            case KNOCKDOWN -> Animations.BIPED_KNOCKDOWN;
            case NEUTRALIZE -> Animations.BIPED_COMMON_NEUTRALIZED;
            case FALL -> Animations.BIPED_LANDING;
            case NONE -> null;
            default -> throw new IncompatibleClassChangeError();
         };
      }
   }

   public void damageStunShield(float damage, float impact) {
      this.setStunShield(this.getStunShield() - Math.min(damage, 30.0F));
      System.out.println(this.getStunShield());
   }

   public boolean applyStun(StunType stunType, float stunTime) {
      if (stunType == null) {
         return false;
      } else {
         return this.getStunShield() > 0.0F ? false : super.applyStun(stunType, stunTime);
      }
   }
}
