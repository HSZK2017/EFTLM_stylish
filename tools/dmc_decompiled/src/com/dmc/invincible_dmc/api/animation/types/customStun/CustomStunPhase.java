package com.dmc.invincible_dmc.api.animation.types.customStun;

import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.JointColliderPair;
import yesman.epicfight.api.collider.Collider;

public class CustomStunPhase extends AvalonPhase {
   private final Supplier<? extends StaticAnimation> groundHitAnimation;
   private final Supplier<? extends StaticAnimation> airHitAnimation;
   @Nullable
   private final Supplier<? extends StaticAnimation> groundHitAlt;
   @Nullable
   private final Supplier<? extends StaticAnimation> airHitAlt;
   @Nullable
   private Supplier<? extends StaticAnimation> groundLeftSDT = null;
   @Nullable
   private Supplier<? extends StaticAnimation> groundRightSDT = null;
   @Nullable
   private Supplier<? extends StaticAnimation> airLeftSDT = null;
   @Nullable
   private Supplier<? extends StaticAnimation> airRightSDT = null;

   public Supplier<? extends StaticAnimation> invincible$getGroundLeft() {
      return this.groundHitAnimation;
   }

   @Nullable
   public Supplier<? extends StaticAnimation> invincible$getGroundRight() {
      return this.groundHitAlt;
   }

   public Supplier<? extends StaticAnimation> invincible$getAirLeft() {
      return this.airHitAnimation;
   }

   @Nullable
   public Supplier<? extends StaticAnimation> invincible$getAirRight() {
      return this.airHitAlt;
   }

   @Nullable
   public Supplier<? extends StaticAnimation> invincible$getGroundLeftSDT() {
      return this.groundLeftSDT;
   }

   @Nullable
   public Supplier<? extends StaticAnimation> invincible$getGroundRightSDT() {
      return this.groundRightSDT;
   }

   @Nullable
   public Supplier<? extends StaticAnimation> invincible$getAirLeftSDT() {
      return this.airLeftSDT;
   }

   @Nullable
   public Supplier<? extends StaticAnimation> invincible$getAirRightSDT() {
      return this.airRightSDT;
   }

   public boolean invincible$hasSDTStunAnimations() {
      return this.groundLeftSDT != null || this.airLeftSDT != null;
   }

   public CustomStunPhase(
      float start,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      float end,
      InteractionHand hand,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      this(start, antic, preDelay, contact, recovery, end, hand, joint, collider, groundHitAnimation, null, airHitAnimation, null);
   }

   public CustomStunPhase(
      float start,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      float end,
      InteractionHand hand,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      @Nullable Supplier<? extends StaticAnimation> groundHitAlt,
      Supplier<? extends StaticAnimation> airHitAnimation,
      @Nullable Supplier<? extends StaticAnimation> airHitAlt
   ) {
      super(start, antic, preDelay, contact, recovery, end, hand, joint, collider);
      this.groundHitAnimation = groundHitAnimation;
      this.groundHitAlt = groundHitAlt;
      this.airHitAnimation = airHitAnimation;
      this.airHitAlt = airHitAlt;
   }

   public CustomStunPhase(
      float start,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      float end,
      InteractionHand hand,
      float damageMulti,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, joint, collider);
      this.groundHitAnimation = groundHitAnimation;
      this.groundHitAlt = null;
      this.airHitAnimation = airHitAnimation;
      this.airHitAlt = null;
   }

   public CustomStunPhase(
      float start,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      float end,
      InteractionHand hand,
      float damageMulti,
      float impactDamageMulti,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, impactDamageMulti, joint, collider);
      this.groundHitAnimation = groundHitAnimation;
      this.groundHitAlt = null;
      this.airHitAnimation = airHitAnimation;
      this.airHitAlt = null;
   }

   public CustomStunPhase(
      float start,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      float end,
      InteractionHand hand,
      float damageMulti,
      float impactDamageMulti,
      float phaseArmorNegationMulti,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, impactDamageMulti, phaseArmorNegationMulti, joint, collider);
      this.groundHitAnimation = groundHitAnimation;
      this.groundHitAlt = null;
      this.airHitAnimation = airHitAnimation;
      this.airHitAlt = null;
   }

   public CustomStunPhase(
      InteractionHand hand,
      Joint joint,
      Collider collider,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      super(hand, joint, collider);
      this.groundHitAnimation = groundHitAnimation;
      this.groundHitAlt = null;
      this.airHitAnimation = airHitAnimation;
      this.airHitAlt = null;
   }

   public CustomStunPhase(
      float start,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      float end,
      InteractionHand hand,
      JointColliderPair[] colliders,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      super(start, antic, preDelay, contact, recovery, end, hand, colliders);
      this.groundHitAnimation = groundHitAnimation;
      this.groundHitAlt = null;
      this.airHitAnimation = airHitAnimation;
      this.airHitAlt = null;
   }

   public CustomStunPhase(
      float start,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      float end,
      InteractionHand hand,
      float damageMulti,
      JointColliderPair[] colliders,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, colliders);
      this.groundHitAnimation = groundHitAnimation;
      this.groundHitAlt = null;
      this.airHitAnimation = airHitAnimation;
      this.airHitAlt = null;
   }

   public CustomStunPhase(
      float start,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      float end,
      InteractionHand hand,
      float damageMulti,
      float impactDamageMulti,
      JointColliderPair[] colliders,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, impactDamageMulti, colliders);
      this.groundHitAnimation = groundHitAnimation;
      this.groundHitAlt = null;
      this.airHitAnimation = airHitAnimation;
      this.airHitAlt = null;
   }

   public CustomStunPhase(
      float start,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      float end,
      InteractionHand hand,
      float damageMulti,
      float impactDamageMulti,
      float phaseArmorNegationMulti,
      JointColliderPair[] colliders,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      Supplier<? extends StaticAnimation> airHitAnimation
   ) {
      super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, impactDamageMulti, phaseArmorNegationMulti, colliders);
      this.groundHitAnimation = groundHitAnimation;
      this.groundHitAlt = null;
      this.airHitAnimation = airHitAnimation;
      this.airHitAlt = null;
   }

   public CustomStunPhase(
      float start,
      float antic,
      float preDelay,
      float contact,
      float recovery,
      float end,
      InteractionHand hand,
      float damageMulti,
      float impactDamageMulti,
      float phaseArmorNegationMulti,
      JointColliderPair[] colliders,
      Supplier<? extends StaticAnimation> groundHitAnimation,
      @Nullable Supplier<? extends StaticAnimation> groundHitAlt,
      Supplier<? extends StaticAnimation> airHitAnimation,
      @Nullable Supplier<? extends StaticAnimation> airHitAlt,
      @Nullable Supplier<? extends StaticAnimation> groundLeftSDT,
      @Nullable Supplier<? extends StaticAnimation> groundRightSDT,
      @Nullable Supplier<? extends StaticAnimation> airLeftSDT,
      @Nullable Supplier<? extends StaticAnimation> airRightSDT
   ) {
      super(start, antic, preDelay, contact, recovery, end, hand, damageMulti, impactDamageMulti, phaseArmorNegationMulti, colliders);
      this.groundHitAnimation = groundHitAnimation;
      this.groundHitAlt = groundHitAlt;
      this.airHitAnimation = airHitAnimation;
      this.airHitAlt = airHitAlt;
      this.groundLeftSDT = groundLeftSDT;
      this.groundRightSDT = groundRightSDT;
      this.airLeftSDT = airLeftSDT;
      this.airRightSDT = airRightSDT;
   }
}
