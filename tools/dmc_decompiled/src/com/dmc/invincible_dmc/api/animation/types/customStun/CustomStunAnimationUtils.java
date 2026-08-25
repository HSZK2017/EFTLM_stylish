package com.dmc.invincible_dmc.api.animation.types.customStun;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.JointColliderPair;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.OBBCollider;

public class CustomStunAnimationUtils {
   public static CustomStunPhase createWorldSpacePhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      Joint joint,
      double centerX,
      double centerY,
      double centerZ,
      double halfX,
      double halfY,
      double halfZ,
      Supplier<? extends StaticAnimation> groundHit,
      Supplier<? extends StaticAnimation> airHit
   ) {
      Collider collider = new OBBCollider(halfX, halfY, halfZ, centerX, centerY, centerZ);
      return createCustomStunPhase(startFrame, endFrame, waitFrame, hand, damageMulti, joint, collider, groundHit, airHit);
   }

   public static CustomStunPhase createWorldSpacePhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      Joint joint,
      double centerX,
      double centerY,
      double centerZ,
      double halfX,
      double halfY,
      double halfZ
   ) {
      Collider collider = new OBBCollider(halfX, halfY, halfZ, centerX, centerY, centerZ);
      return createCustomStunPhase(startFrame, endFrame, waitFrame, hand, damageMulti, joint, collider);
   }

   public static CustomStunPhase createCustomStunPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      return createCustomStunPhase(startFrame, endFrame, waitFrame, hand, joint, collider, groundHitAnimation, null, airHitAnimation, null);
   }

   public static CustomStunPhase createCustomStunPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      @Nullable Supplier<? extends StaticAnimation> groundHitAlt,
      Supplier<? extends StaticAnimation> airHitAnimation,
      @Nullable Supplier<? extends StaticAnimation> airHitAlt
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new CustomStunPhase(0.0F, start, start, end, wait, wait, hand, joint, collider, groundHitAnimation, groundHitAlt, airHitAnimation, airHitAlt);
   }

   public static CustomStunPhase createCustomStunPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new CustomStunPhase(0.0F, start, start, end, wait, wait, hand, damageMulti, joint, collider, groundHitAnimation, airHitAnimation);
   }

   public static CustomStunPhase createCustomStunPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      float impactMulti,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new CustomStunPhase(0.0F, start, start, end, wait, wait, hand, damageMulti, impactMulti, joint, collider, groundHitAnimation, airHitAnimation);
   }

   public static CustomStunPhase createCustomStunPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      float impactMulti,
      float armorNegationMulti,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new CustomStunPhase(
         0.0F, start, start, end, wait, wait, hand, damageMulti, impactMulti, armorNegationMulti, joint, collider, groundHitAnimation, airHitAnimation
      );
   }

   public static CustomStunPhase createCustomStunPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      JointColliderPair[] colliders,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new CustomStunPhase(0.0F, start, start, end, wait, wait, hand, colliders, groundHitAnimation, airHitAnimation);
   }

   public static CustomStunPhase createCustomStunPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      JointColliderPair[] colliders,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new CustomStunPhase(0.0F, start, start, end, wait, wait, hand, damageMulti, colliders, groundHitAnimation, airHitAnimation);
   }

   public static CustomStunPhase createCustomStunPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      float impactMulti,
      JointColliderPair[] colliders,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new CustomStunPhase(0.0F, start, start, end, wait, wait, hand, damageMulti, impactMulti, colliders, groundHitAnimation, airHitAnimation);
   }

   public static CustomStunPhase createCustomStunPhase(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      float impactMulti,
      float armorNegationMulti,
      JointColliderPair[] colliders,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new CustomStunPhase(
         0.0F, start, start, end, wait, wait, hand, damageMulti, impactMulti, armorNegationMulti, colliders, groundHitAnimation, airHitAnimation
      );
   }

   public static CustomStunPhase createCustomStunPhase(
      int startFrame, int endFrame, int waitFrame, InteractionHand hand, float damageMulti, Joint joint, Collider collider
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new CustomStunPhase(0.0F, start, start, end, wait, wait, hand, damageMulti, joint, collider, null, null);
   }

   public static CustomStunPhase createCustomStunPhase(
      int startFrame, int endFrame, int waitFrame, InteractionHand hand, float damageMulti, float impactMulti, Joint joint, Collider collider
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new CustomStunPhase(0.0F, start, start, end, wait, wait, hand, damageMulti, impactMulti, joint, collider, null, null);
   }

   public static CustomStunPhase createCustomStunPhaseSDT(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHit,
      Supplier<? extends StaticAnimation> airHit,
      @Nullable Supplier<? extends StaticAnimation> groundHitSDT,
      @Nullable Supplier<? extends StaticAnimation> airHitSDT
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new CustomStunPhase(
         0.0F, start, start, end, wait, wait, hand, damageMulti, 1.0F, 1.0F, null, groundHit, null, airHit, null, groundHitSDT, null, airHitSDT, null
      );
   }

   public static CustomStunPhase createCustomStunPhaseSDT(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      float damageMulti,
      float impactMulti,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHit,
      Supplier<? extends StaticAnimation> airHit,
      @Nullable Supplier<? extends StaticAnimation> groundHitSDT,
      @Nullable Supplier<? extends StaticAnimation> airHitSDT
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new CustomStunPhase(
         0.0F, start, start, end, wait, wait, hand, damageMulti, impactMulti, 1.0F, null, groundHit, null, airHit, null, groundHitSDT, null, airHitSDT, null
      );
   }

   public static CustomStunPhase createCustomStunPhaseSDT(
      int startFrame,
      int endFrame,
      int waitFrame,
      InteractionHand hand,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHit,
      @Nullable Supplier<? extends StaticAnimation> groundHitAlt,
      Supplier<? extends StaticAnimation> airHit,
      @Nullable Supplier<? extends StaticAnimation> airHitAlt,
      @Nullable Supplier<? extends StaticAnimation> groundHitSDT,
      @Nullable Supplier<? extends StaticAnimation> groundHitAltSDT,
      @Nullable Supplier<? extends StaticAnimation> airHitSDT,
      @Nullable Supplier<? extends StaticAnimation> airHitAltSDT
   ) {
      float start = (float)startFrame / 60.0F;
      float end = (float)endFrame / 60.0F;
      float wait = (float)waitFrame / 60.0F;
      return new CustomStunPhase(
         0.0F,
         start,
         start,
         end,
         wait,
         wait,
         hand,
         1.0F,
         1.0F,
         1.0F,
         null,
         groundHit,
         groundHitAlt,
         airHit,
         airHitAlt,
         groundHitSDT,
         groundHitAltSDT,
         airHitSDT,
         airHitAltSDT
      );
   }
}
