package org.merlin204.mimic.entity.proteus;

import com.google.common.collect.Maps;
import com.merlin204.avalon.entity.ai.AvalonAnimatedAttackGoal;
import com.merlin204.avalon.entity.ai.AvalonCombatBehaviors.Builder;
import com.merlin204.avalon.epicfight.AvalonFctions;
import com.merlin204.avalon.util.AvalonParticleUtils;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import org.merlin204.mimic.entity.MimicEntities;
import org.merlin204.mimic.entity.MimicPatch;
import org.merlin204.mimic.entity.ai.MimicCombatBehaviors;
import org.merlin204.mimic.entity.shadow.ShadowMimicEntity;
import org.merlin204.mimic.epicfight.MimicAnimations;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangeLivingMotion;
import yesman.epicfight.world.capabilities.entitypatch.Faction;

public class ProteusPatch<T extends ProteusEntity> extends MimicPatch<T> {
   protected Map<Integer, Set<Pair<LivingMotion, AnimationAccessor<? extends StaticAnimation>>>> phaseLivingMotions;
   protected Map<Integer, Builder<MimicPatch<?>>> weaponAttackMotions;
   private int phaseOld;

   @Override
   public Faction getFaction() {
      return AvalonFctions.EMPTY;
   }

   @Override
   public void tick(LivingTickEvent event) {
      super.tick(event);
      if (!((ProteusEntity)this.original).m_9236_().f_46443_) {
         if (this.phaseOld != ((ProteusEntity)this.original).getPhase()) {
            this.setAIAsInfantry();
            this.modifyLivingMotionByPhase(false);
         }

         LivingEntity target = this.getTarget();
         if (target != null && !target.m_6084_()) {
            this.setAttakTargetSync(null);
         }

         if (((ProteusEntity)this.original).m_21223_() <= ((ProteusEntity)this.original).m_21233_() * 0.5F
            && ((ProteusEntity)this.original).getPhase() <= 1
            && !this.getEntityState().inaction()) {
            Vec3 vec3 = ((ProteusEntity)this.original).m_20182_();
            Vec3 spawn1 = new Vec3(
               vec3.f_82479_ + (double)(5.0F * (((ProteusEntity)this.original).m_217043_().m_188501_() - 0.5F)),
               vec3.f_82480_,
               vec3.f_82481_ + (double)(5.0F * (((ProteusEntity)this.original).m_217043_().m_188501_() - 0.5F))
            );
            Vec3 spawn2 = new Vec3(
               vec3.f_82479_ + (double)(5.0F * (((ProteusEntity)this.original).m_217043_().m_188501_() - 0.5F)),
               vec3.f_82480_,
               vec3.f_82481_ + (double)(5.0F * (((ProteusEntity)this.original).m_217043_().m_188501_() - 0.5F))
            );
            ShadowMimicEntity shadowMimicEntity1 = new ShadowMimicEntity(
               (EntityType<? extends PathfinderMob>)MimicEntities.SHADOW_MIMIC.get(), ((ProteusEntity)this.original).m_9236_(), (LivingEntity)this.original
            );
            ShadowMimicEntity shadowMimicEntity2 = new ShadowMimicEntity(
               (EntityType<? extends PathfinderMob>)MimicEntities.SHADOW_MIMIC.get(), ((ProteusEntity)this.original).m_9236_(), (LivingEntity)this.original
            );
            CompoundTag copyInfo = new CompoundTag();
            ((ProteusEntity)this.original).saveCopy(copyInfo);
            shadowMimicEntity1.m_146884_(spawn1);
            shadowMimicEntity2.m_146884_(spawn2);
            shadowMimicEntity1.loadCopy(copyInfo);
            shadowMimicEntity2.loadCopy(copyInfo);
            ((ProteusEntity)this.original).m_9236_().m_7967_(shadowMimicEntity1);
            ((ProteusEntity)this.original).m_9236_().m_7967_(shadowMimicEntity2);
            this.playAnimationSynchronized(MimicAnimations.PHASE_2, 0.0F);
         }
      } else if (this.phaseOld < ((ProteusEntity)this.original).getPhase()) {
         ((ProteusEntity)this.original).stopMusic();
         ((ProteusEntity)this.original).startMusic();
         if (((ProteusEntity)this.original).getPhase() == 3) {
            AvalonParticleUtils.createJointSphereParticles(this, ((HumanoidArmature)Armatures.BIPED.get()).chest, ParticleTypes.f_123755_, 1.0, 0.3, 0.3, 200);
         }
      }

      this.phaseOld = ((ProteusEntity)this.original).getPhase();
   }

   @Override
   protected void initAI() {
      super.initAI();
      this.setAIAsInfantry();
   }

   public void onConstructed(T entityIn) {
      super.onConstructed(entityIn);
      this.setPhaseLivingMotions();
      if (!((ProteusEntity)this.original).m_9236_().f_46443_) {
         this.setMaxStunShield(150.0F);
         this.setStunShield(150.0F);
      }
   }

   protected Builder<MimicPatch<?>> getHoldingItemWeaponMotionBuilder() {
      int phase = ((ProteusEntity)this.original).getPhase();
      return this.weaponAttackMotions != null && this.weaponAttackMotions.containsKey(phase)
         ? this.weaponAttackMotions.get(phase)
         : MimicCombatBehaviors.PHASE1;
   }

   public void setAIAsInfantry() {
      Builder<MimicPatch<?>> builder = this.getHoldingItemWeaponMotionBuilder();
      if (builder != null) {
         ((ProteusEntity)this.original).f_21345_.m_25352_(0, new AvalonAnimatedAttackGoal(this, builder.build(this)));
      }
   }

   protected void setPhaseLivingMotions() {
      this.phaseLivingMotions = Maps.newHashMap();
      this.phaseLivingMotions
         .put(
            1,
            Set.of(
               Pair.of(LivingMotions.IDLE, MimicAnimations.IDLE),
               Pair.of(LivingMotions.WALK, MimicAnimations.WALK),
               Pair.of(LivingMotions.DEATH, MimicAnimations.DEATH)
            )
         );
      this.phaseLivingMotions
         .put(
            2,
            Set.of(
               Pair.of(LivingMotions.IDLE, MimicAnimations.IDLE),
               Pair.of(LivingMotions.WALK, MimicAnimations.WALK),
               Pair.of(LivingMotions.DEATH, MimicAnimations.DEATH)
            )
         );
      this.phaseLivingMotions
         .put(
            3,
            Set.of(
               Pair.of(LivingMotions.IDLE, MimicAnimations.IDLE_END),
               Pair.of(LivingMotions.WALK, MimicAnimations.WALK_END),
               Pair.of(LivingMotions.DEATH, MimicAnimations.DEATH)
            )
         );
      this.weaponAttackMotions = Maps.newHashMap();
      this.weaponAttackMotions.put(1, MimicCombatBehaviors.PHASE1);
      this.weaponAttackMotions.put(2, MimicCombatBehaviors.PHASE1);
      this.weaponAttackMotions.put(3, MimicCombatBehaviors.PHASE3);
   }

   public void onStartTracking(ServerPlayer trackingPlayer) {
      this.setPhaseLivingMotions();
      this.modifyLivingMotionByPhase(true);
   }

   protected void serverTick(LivingTickEvent event) {
      super.serverTick(event);
      Vec3 Pos = ((ProteusEntity)this.getOriginal()).m_20182_();
      Level world = ((ProteusEntity)this.getOriginal()).m_9236_();
      AABB searchArea = new AABB(Pos.f_82479_ - 60.0, Pos.f_82480_ - 2.0, Pos.f_82481_ - 60.0, Pos.f_82479_ + 60.0, Pos.f_82480_ + 2.0, Pos.f_82481_ + 60.0);
      List<ShadowMimicEntity> entities = world.m_6443_(ShadowMimicEntity.class, searchArea, e -> e.m_6084_() && e.getOwner() == this.original);
      if (entities.isEmpty() && ((ProteusEntity)this.original).getPhase() == 2) {
         this.setStunShield(1.0E8F);
         ((ProteusEntity)this.original).setPhase(3);
      }
   }

   @Override
   public void tickNearbyEntity(LivingEntity entity) {
      super.tickNearbyEntity(entity);
      if (entity instanceof ProteusEntity proteus && proteus.m_21223_() <= ((ProteusEntity)this.original).m_21223_()) {
         CompoundTag copy = new CompoundTag();
         proteus.saveCopy(copy);
         ((ProteusEntity)this.original).loadCopy(copy);
         proteus.m_146870_();
      }
   }

   public void modifyLivingMotionByPhase(boolean onStartTracking) {
      Animator animator = this.getAnimator();
      if (animator != null) {
         Map<LivingMotion, AssetAccessor<? extends StaticAnimation>> oldLivingAnimations = animator.getLivingAnimations();
         if (oldLivingAnimations == null) {
            oldLivingAnimations = Map.of();
         }

         Map<LivingMotion, AssetAccessor<? extends StaticAnimation>> newLivingAnimations = Maps.newHashMap();
         int phase = ((ProteusEntity)this.original).getPhase();
         boolean hasChange = false;
         if (this.phaseLivingMotions != null && this.phaseLivingMotions.containsKey(phase)) {
            for (Pair<LivingMotion, AnimationAccessor<? extends StaticAnimation>> pair : this.phaseLivingMotions.get(phase)) {
               LivingMotion motion = (LivingMotion)pair.getFirst();
               AnimationAccessor<? extends StaticAnimation> newAnim = (AnimationAccessor<? extends StaticAnimation>)pair.getSecond();
               if (oldLivingAnimations.containsKey(motion)) {
                  if (oldLivingAnimations.get(motion) != newAnim) {
                     hasChange = true;
                  }
               } else {
                  hasChange = true;
               }

               newLivingAnimations.put(motion, newAnim);
            }
         }

         if (!hasChange) {
            for (LivingMotion oldMotion : oldLivingAnimations.keySet()) {
               if (!newLivingAnimations.containsKey(oldMotion)) {
                  hasChange = true;
                  break;
               }
            }
         }

         if (hasChange || onStartTracking) {
            animator.resetLivingAnimations();
            newLivingAnimations.forEach(animator::addLivingAnimation);
            SPChangeLivingMotion msg = new SPChangeLivingMotion(((ProteusEntity)this.original).m_19879_());
            msg.putEntries(newLivingAnimations.entrySet());
            EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(msg, this.original, new Object[0]);
         }
      }
   }
}
