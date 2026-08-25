package com.dmc.invincible_dmc.mixin.epicfight;

import com.dmc.invincible_dmc.api.animation.types.customStun.ICustomStunDamageSource;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

@Mixin(
   value = {EpicFightDamageSource.class},
   remap = false
)
public class EpicFightDamageSourceMixin implements ICustomStunDamageSource {
   @Unique
   private Supplier<? extends StaticAnimation> invincible$customStunGroundLeft;
   @Unique
   private Supplier<? extends StaticAnimation> invincible$customStunGroundRight;
   @Unique
   private Supplier<? extends StaticAnimation> invincible$customStunAirLeft;
   @Unique
   private Supplier<? extends StaticAnimation> invincible$customStunAirRight;
   @Unique
   private Supplier<? extends StaticAnimation> invincible$customStunGroundLeftSDT;
   @Unique
   private Supplier<? extends StaticAnimation> invincible$customStunGroundRightSDT;
   @Unique
   private Supplier<? extends StaticAnimation> invincible$customStunAirLeftSDT;
   @Unique
   private Supplier<? extends StaticAnimation> invincible$customStunAirRightSDT;
   @Unique
   private float invincible$customStunVerticalOffset;
   @Unique
   private Float invincible$customStunSourceYRot;

   @Override
   public void invincible$setCustomStunAnimations(
      Supplier<? extends StaticAnimation> groundLeft,
      @Nullable Supplier<? extends StaticAnimation> groundRight,
      Supplier<? extends StaticAnimation> airLeft,
      @Nullable Supplier<? extends StaticAnimation> airRight
   ) {
      this.invincible$customStunGroundLeft = groundLeft;
      this.invincible$customStunGroundRight = groundRight;
      this.invincible$customStunAirLeft = airLeft;
      this.invincible$customStunAirRight = airRight;
   }

   @Override
   public void invincible$setCustomStunAnimationsSDT(
      @Nullable Supplier<? extends StaticAnimation> groundLeft,
      @Nullable Supplier<? extends StaticAnimation> groundRight,
      @Nullable Supplier<? extends StaticAnimation> airLeft,
      @Nullable Supplier<? extends StaticAnimation> airRight
   ) {
      this.invincible$customStunGroundLeftSDT = groundLeft;
      this.invincible$customStunGroundRightSDT = groundRight;
      this.invincible$customStunAirLeftSDT = airLeft;
      this.invincible$customStunAirRightSDT = airRight;
   }

   @Override
   public boolean invincible$hasCustomStunAnimations() {
      return this.invincible$customStunGroundLeft != null || this.invincible$customStunAirLeft != null;
   }

   @Override
   public boolean invincible$hasCustomStunAnimationsSDT() {
      return this.invincible$customStunGroundLeftSDT != null || this.invincible$customStunAirLeftSDT != null;
   }

   @Override
   public void invincible$setCustomStunVerticalOffset(float verticalOffset) {
      this.invincible$customStunVerticalOffset = verticalOffset;
   }

   @Override
   public float invincible$getCustomStunVerticalOffset() {
      return this.invincible$customStunVerticalOffset;
   }

   @Override
   public void invincible$setCustomStunSourceYRot(@Nullable Float sourceYRot) {
      this.invincible$customStunSourceYRot = sourceYRot;
   }

   @Nullable
   @Override
   public Float invincible$getCustomStunSourceYRot() {
      return this.invincible$customStunSourceYRot;
   }

   @Nullable
   @Override
   public AnimationAccessor<? extends StaticAnimation> invincible$resolveCustomStunAnimation(LivingEntity target, boolean isAttackerSDT) {
      Supplier<? extends StaticAnimation> primary;
      Supplier<? extends StaticAnimation> alt;
      if (invincible$isEffectivelyGrounded(target)) {
         primary = isAttackerSDT && this.invincible$customStunGroundLeftSDT != null
            ? this.invincible$customStunGroundLeftSDT
            : this.invincible$customStunGroundLeft;
         alt = isAttackerSDT && this.invincible$customStunGroundRightSDT != null
            ? this.invincible$customStunGroundRightSDT
            : this.invincible$customStunGroundRight;
      } else {
         primary = isAttackerSDT && this.invincible$customStunAirLeftSDT != null ? this.invincible$customStunAirLeftSDT : this.invincible$customStunAirLeft;
         alt = isAttackerSDT && this.invincible$customStunAirRightSDT != null ? this.invincible$customStunAirRightSDT : this.invincible$customStunAirRight;
      }

      if (primary == null) {
         return null;
      } else {
         Supplier<? extends StaticAnimation> chosen = invincible_DMC$pickRandom(target, primary, alt);
         if (chosen != null) {
            StaticAnimation anim = chosen.get();
            if (anim != null) {
               return anim.getAccessor();
            }
         }

         return null;
      }
   }

   @Unique
   private static boolean invincible$isEffectivelyGrounded(LivingEntity entity) {
      return entity.m_20096_() ? true : invincible$computeHeightAboveGround(entity) < 0.5;
   }

   @Unique
   private static double invincible$computeHeightAboveGround(LivingEntity entity) {
      double entityY = entity.m_20186_();
      BlockPos entityPos = entity.m_20183_();
      int scanDown = Math.min(entityPos.m_123342_() - entity.m_9236_().m_141937_(), 20);

      for (int y = entityPos.m_123342_() - 1; y >= entityPos.m_123342_() - scanDown; y--) {
         BlockPos checkPos = new BlockPos(entityPos.m_123341_(), y, entityPos.m_123343_());
         if (entity.m_9236_().m_8055_(checkPos).m_280555_()) {
            return entityY - ((double)y + 1.0);
         }
      }

      return Double.MAX_VALUE;
   }

   @Unique
   private static Supplier<? extends StaticAnimation> invincible_DMC$pickRandom(
      LivingEntity target, Supplier<? extends StaticAnimation> primary, @Nullable Supplier<? extends StaticAnimation> alt
   ) {
      if (alt != null && primary != null && alt.get() != null && primary.get() != null) {
         return target.m_217043_().m_188499_() ? primary : alt;
      } else {
         return primary;
      }
   }
}
