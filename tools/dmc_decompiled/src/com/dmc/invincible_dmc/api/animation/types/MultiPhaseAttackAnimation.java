package com.dmc.invincible_dmc.api.animation.types;

import com.dmc.invincible_dmc.capability.DMCEntity;
import com.dmc.invincible_dmc.capability.DMCapabilities;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.PartEntity;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.AttackAnimation.JointColliderPair;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.HitEntityList;
import yesman.epicfight.api.utils.HitEntityList.Priority;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.entity.eventlistener.AttackPhaseEndEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class MultiPhaseAttackAnimation extends AttackAnimation {
   public MultiPhaseAttackAnimation(
      float transitionTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      @Nullable Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends AttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, antic, preDelay, contact, recovery, collider, colliderJoint, accessor, armature);
   }

   public MultiPhaseAttackAnimation(
      float transitionTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      InteractionHand hand,
      @Nullable Collider collider,
      Joint colliderJoint,
      AnimationAccessor<? extends AttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, antic, preDelay, contact, recovery, hand, collider, colliderJoint, accessor, armature);
   }

   public MultiPhaseAttackAnimation(
      float transitionTime, AnimationAccessor<? extends AttackAnimation> accessor, AssetAccessor<? extends Armature> armature, Phase... phases
   ) {
      super(transitionTime, accessor, armature, phases);
   }

   public MultiPhaseAttackAnimation(
      float convertTime,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      InteractionHand hand,
      @Nullable Collider collider,
      Joint colliderJoint,
      String path,
      AssetAccessor<? extends Armature> armature
   ) {
      super(convertTime, antic, preDelay, contact, recovery, hand, collider, colliderJoint, path, armature);
   }

   public MultiPhaseAttackAnimation(float convertTime, String path, AssetAccessor<? extends Armature> armature, Phase... phases) {
      super(convertTime, path, armature, phases);
   }

   public void begin(LivingEntityPatch<?> entityPatch) {
      super.begin(entityPatch);
      DMCapabilities.getEntityCap((LivingEntity)entityPatch.getOriginal()).resetAttackPhaseCache();
   }

   public void end(LivingEntityPatch<?> entityPatch, AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd) {
      super.end(entityPatch, nextAnimation, isEnd);
      DMCapabilities.getEntityCap((LivingEntity)entityPatch.getOriginal()).resetAttackPhaseCache();
   }

   protected void attackTick(LivingEntityPatch<?> entityPatch, AssetAccessor<? extends DynamicAnimation> animation) {
      if (!((DynamicAnimation)animation.get()).isLinkAnimation()) {
         AnimationPlayer player = DMCAnimationUtils.getPlayerFor(entityPatch, animation);
         float elapsedTime = player.getElapsedTime();
         float prevElapsedTime = player.getPrevElapsedTime();
         EntityState state = this.getState(entityPatch, elapsedTime);
         EntityState prevState = this.getState(entityPatch, prevElapsedTime);

         for (Phase phase : this.phases) {
            if (elapsedTime > phase.end && prevElapsedTime < phase.end) {
               if (entityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                  serverPlayerPatch.getEventListener()
                     .triggerEvents(
                        EventType.ATTACK_PHASE_END_EVENT,
                        new AttackPhaseEndEvent(serverPlayerPatch, this.getAccessor(), phase, this.getPhaseOrderByTime(elapsedTime))
                     );
               }
            } else if (!(elapsedTime < phase.antic)
               && !(elapsedTime > phase.contact)
               && (prevState.attacking() || state.attacking() || prevState.getLevel() < 2 && state.getLevel() > 2)) {
               if (elapsedTime > phase.antic && !DMCapabilities.getEntityCap((LivingEntity)entityPatch.getOriginal()).isPhaseUsed(phase)) {
                  this.onPhaseStart(entityPatch, animation, phase, elapsedTime);
                  DMCapabilities.getEntityCap((LivingEntity)entityPatch.getOriginal()).setPhaseUsed(phase);
               }

               this.hurtCollidingEntities(entityPatch, prevElapsedTime, elapsedTime, prevState, state, phase);
            }
         }
      }
   }

   protected void onPhaseStart(LivingEntityPatch<?> entityPatch, AssetAccessor<? extends DynamicAnimation> animation, Phase phase, float elapsedTime) {
      entityPatch.onStrike(this, phase.hand);
      entityPatch.playSound(this.getSwingSound(entityPatch, phase), 0.0F, 0.0F);
      entityPatch.removeHurtEntities();
   }

   protected void hurtCollidingEntities(
      LivingEntityPatch<?> entityPatch, float prevElapsedTime, float elapsedTime, EntityState prevState, EntityState state, Phase phase
   ) {
      float prevPoseTime = prevState.attacking() ? prevElapsedTime : phase.preDelay;
      float poseTime = state.attacking() ? elapsedTime : phase.contact;
      List<Entity> list = phase.getCollidingEntities(entityPatch, this, prevPoseTime, poseTime, this.getPlaySpeed(entityPatch, this));
      if (!list.isEmpty()) {
         HitEntityList hitEntities = new HitEntityList(entityPatch, list, phase.getProperty(AttackPhaseProperty.HIT_PRIORITY).orElse(Priority.DISTANCE));

         while (hitEntities.next()) {
            Entity hit = hitEntities.getEntity();
            LivingEntity trueEntity = this.getTrueEntity(hit);
            DMCEntity DMCEntity = DMCapabilities.getEntityCap((LivingEntity)entityPatch.getOriginal());
            if (trueEntity != null
               && trueEntity.m_6084_()
               && !DMCEntity.getCurrentlyHurtEntities(phase).contains(trueEntity)
               && !trueEntity.m_7306_(entityPatch.getOriginal())
               && (hit instanceof LivingEntity || hit instanceof PartEntity)) {
               EpicFightDamageSource source = this.getEpicFightDamageSource(entityPatch, hit, phase);
               int prevInvulTime = hit.f_19802_;
               hit.f_19802_ = 0;
               AttackResult attackResult = entityPatch.attack(source, hit, phase.hand);
               hit.f_19802_ = prevInvulTime;
               if (attackResult.resultType.dealtDamage()) {
                  hit.m_9236_().m_6263_(null, hit.m_20185_(), hit.m_20186_(), hit.m_20189_(), this.getHitSound(entityPatch, phase), hit.m_5720_(), 1.0F, 1.0F);
                  this.spawnHitParticle((ServerLevel)hit.m_9236_(), entityPatch, hit, phase);
               }

               DMCEntity.getCurrentlyHurtEntities(phase).add(trueEntity);
               if (attackResult.resultType.shouldCount()) {
                  entityPatch.getCurrentlyAttackTriedEntities().add(trueEntity);
               }
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void renderDebugging(PoseStack poseStack, MultiBufferSource buffer, LivingEntityPatch<?> entityPatch, float playbackTime, float partialTicks) {
      AnimationPlayer animPlayer = DMCAnimationUtils.getPlayerFor(entityPatch, this.getAccessor());
      float prevElapsedTime = animPlayer.getPrevElapsedTime();
      float elapsedTime = animPlayer.getElapsedTime();

      for (Phase phase : this.phases) {
         Iterator<JointColliderPair> iterator = Arrays.stream(phase.colliders).iterator();

         while (iterator.hasNext()) {
            Pair<Joint, Collider> colliderInfo = (Pair<Joint, Collider>)iterator.next();
            Collider collider = (Collider)colliderInfo.getSecond();
            if (collider == null) {
               collider = entityPatch.getColliderMatching(phase.hand);
            }

            collider.draw(
               poseStack,
               buffer,
               entityPatch,
               this,
               (Joint)colliderInfo.getFirst(),
               prevElapsedTime,
               elapsedTime,
               partialTicks,
               this.getPlaySpeed(entityPatch, this)
            );
         }
      }
   }
}
