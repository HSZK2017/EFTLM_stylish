package com.dmc.invincible_dmc.api.animation.types.yamato;

import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.AnimationVariables;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationVariables.IndependentAnimationVariableKey;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPSyncPlayerAnimationPosition;
import yesman.epicfight.network.server.SPSyncAnimationPosition;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class YamatoMovingLivingAnimation extends YamatoLivingAnimation {
   private static final IndependentAnimationVariableKey<TransformSheet> ROOT_COORD = AnimationVariables.independent(animator -> new TransformSheet(), true);

   public YamatoMovingLivingAnimation(
      float transitionTime, AnimationAccessor<? extends YamatoMovingLivingAnimation> accessor, AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, false, accessor, armature);
   }

   public void putOnPlayer(AnimationPlayer animationPlayer, LivingEntityPatch<?> patch) {
      TransformSheet rootCoord = new TransformSheet();
      MoveCoordFunctions.RAW_COORD.set(this, patch, rootCoord);
      patch.getAnimator().getVariables().put(ROOT_COORD, this.getAccessor(), rootCoord);
      super.putOnPlayer(animationPlayer, patch);
   }

   public void tick(LivingEntityPatch<?> patch) {
      super.tick(patch);
      if (shouldMoveOnCurrentSide(patch)) {
         AnimationPlayer player = DMCAnimationUtils.getPlayerFor(patch, this.getAccessor());
         TransformSheet rootCoord = (TransformSheet)patch.getAnimator().getVariables().getOrDefault(ROOT_COORD, this.getAccessor());
         Vec3f movement = null;
         if (player != null) {
            movement = MoveCoordFunctions.MODEL_COORD.get(this, patch, rootCoord, player.getPrevElapsedTime(), player.getElapsedTime());
         }

         Vec3 movementVector = null;
         if (movement != null) {
            movementVector = movement.toDoubleVector();
         }

         if (movementVector == null || !(movementVector.m_82556_() <= 1.0E-8)) {
            LivingEntity entity = (LivingEntity)patch.getOriginal();
            if (movementVector != null) {
               entity.m_6478_(MoverType.SELF, movementVector);
            }

            if (patch.isLogicalClient()) {
               if (player != null) {
                  EpicFightNetworkManager.sendToServer(new CPSyncPlayerAnimationPosition(entity.m_19879_(), player.getElapsedTime(), entity.m_20182_(), 1));
               }
            } else if (player != null) {
               EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(
                  new SPSyncAnimationPosition(entity.m_19879_(), player.getElapsedTime(), entity.m_20182_(), 1), entity, new Object[0]
               );
            }
         }
      }
   }

   public void modifyPose(DynamicAnimation animation, Pose pose, LivingEntityPatch<?> patch, float time, float partialTicks) {
      JointTransform rootTransform = pose.orElseEmpty("Root");
      Vec3f rootPosition = rootTransform.translation();
      OpenMatrix4f rootLocalTransform = patch.getArmature().searchJointByName("Root").getLocalTransform().removeTranslation();
      OpenMatrix4f inverseRootTransform = OpenMatrix4f.invert(rootLocalTransform, null);
      Vec3f modelPosition = OpenMatrix4f.transform3v(rootLocalTransform, rootPosition, null);
      modelPosition.x = 0.0F;
      modelPosition.z = 0.0F;
      OpenMatrix4f.transform3v(inverseRootTransform, modelPosition, modelPosition);
      rootPosition.x = modelPosition.x;
      rootPosition.y = modelPosition.y;
      rootPosition.z = modelPosition.z;
      super.modifyPose(animation, pose, patch, time, partialTicks);
   }

   private static boolean shouldMoveOnCurrentSide(LivingEntityPatch<?> patch) {
      return !patch.isLogicalClient() || ((LivingEntity)patch.getOriginal()).m_6109_();
   }
}
