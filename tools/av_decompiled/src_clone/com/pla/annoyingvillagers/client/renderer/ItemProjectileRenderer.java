package com.pla.annoyingvillagers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.pla.annoyingvillagers.entity.ItemProjectile;
import com.pla.annoyingvillagers.util.HookUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ItemProjectileRenderer extends EntityRenderer<ItemProjectile> {
   private final ItemRenderer itemRenderer;

   public ItemProjectileRenderer(Context context) {
      super(context);
      this.itemRenderer = context.m_174025_();
      this.f_114477_ = 0.15F;
   }

   private static void applyRandomSpin(PoseStack poseStack, float age, int seed) {
      int mixedSeed = mix(seed);
      float direction = (mixedSeed & 1) == 0 ? 1.0F : -1.0F;
      float secondDirection = (mixedSeed >> 1 & 1) == 0 ? 1.0F : -1.0F;
      float primarySpin = variableSpin(age, mixedSeed, 24.0F, 76.0F) * direction;
      float secondarySpin = variableSpin(age, mixedSeed + 991, 5.0F, 24.0F) * secondDirection;
      float wobble = Mth.m_14031_(age * randomRange(mixedSeed + 31, 0.08F, 0.18F) + randomRange(mixedSeed + 57, 0.0F, (float) (Math.PI * 2)))
         * randomRange(mixedSeed + 73, 5.0F, 18.0F);
      switch (Math.floorMod(mixedSeed, 6)) {
         case 0:
            poseStack.m_252781_(Axis.f_252403_.m_252977_(-primarySpin));
            poseStack.m_252781_(Axis.f_252529_.m_252977_(wobble));
            break;
         case 1:
            poseStack.m_252781_(Axis.f_252529_.m_252977_(primarySpin));
            poseStack.m_252781_(Axis.f_252403_.m_252977_(wobble));
            break;
         case 2:
            poseStack.m_252781_(Axis.f_252436_.m_252977_(primarySpin));
            poseStack.m_252781_(Axis.f_252403_.m_252977_(secondarySpin));
            break;
         case 3:
            poseStack.m_252781_(Axis.f_252529_.m_252977_(primarySpin));
            poseStack.m_252781_(Axis.f_252436_.m_252977_(secondarySpin));
            poseStack.m_252781_(Axis.f_252403_.m_252977_(wobble));
            break;
         case 4:
            poseStack.m_252781_(Axis.f_252403_.m_252977_(primarySpin * 0.75F));
            poseStack.m_252781_(Axis.f_252436_.m_252977_(primarySpin * 0.35F));
            poseStack.m_252781_(Axis.f_252529_.m_252977_(secondarySpin));
            break;
         default:
            poseStack.m_252781_(Axis.f_252436_.m_252977_(primarySpin * 0.85F));
            poseStack.m_252781_(Axis.f_252529_.m_252977_(wobble));
            poseStack.m_252781_(Axis.f_252403_.m_252977_(secondarySpin));
      }
   }

   private static float variableSpin(float age, int seed, float minSpeed, float maxSpeed) {
      float baseSpeed = randomRange(seed, minSpeed, maxSpeed);
      float pulseFrequency = randomRange(seed + 13, 0.045F, 0.13F);
      float phase = randomRange(seed + 29, 0.0F, (float) (Math.PI * 2));
      float waveAmount = randomRange(seed + 47, baseSpeed * 3.0F, baseSpeed * 7.0F);
      return age * baseSpeed
         + Mth.m_14031_(age * pulseFrequency + phase) * waveAmount
         + Mth.m_14031_(age * pulseFrequency * 0.43F + phase * 1.7F) * waveAmount * 0.45F;
   }

   private static float randomRange(int seed, float min, float max) {
      float value = (float)(mix(seed) & 16777215) / 1.6777216E7F;
      return min + (max - min) * value;
   }

   private static int mix(int value) {
      value ^= value >>> 16;
      value *= 2146121005;
      value ^= value >>> 15;
      value *= -2073254261;
      return value ^ value >>> 16;
   }

   private static float getOwnerLookYaw(ItemProjectile entity, float partialTick) {
      Entity owner = entity.m_19749_();
      return owner != null ? Mth.m_14179_(partialTick, owner.f_19859_, owner.m_146908_()) : Mth.m_14179_(partialTick, entity.f_19859_, entity.m_146908_());
   }

   private static Vec3 getSharpProjectileDirection(ItemProjectile entity, float partialTick) {
      if (entity.isHookAttached()) {
         Entity owner = getRopeOwner(entity.m_19749_());
         if (owner != null) {
            Vec3 direction = partialPosition(entity, partialTick).m_82546_(owner.m_20299_(partialTick));
            if (direction.m_82556_() > 1.0E-7) {
               return direction;
            }
         }
      }

      Vec3 motion = entity.m_20184_();
      return motion.m_82556_() > 1.0E-7 ? motion : Vec3.m_82498_(entity.m_146909_(), entity.m_146908_());
   }

   private static Entity getRopeOwner(Entity owner) {
      if (owner instanceof Projectile projectile && projectile.m_19749_() != null) {
         return projectile.m_19749_();
      }

      return owner;
   }

   private static Vec3 partialPosition(Entity entity, float partialTick) {
      return new Vec3(
         Mth.m_14139_((double)partialTick, entity.f_19790_, entity.m_20185_()),
         Mth.m_14139_((double)partialTick, entity.f_19791_, entity.m_20186_()),
         Mth.m_14139_((double)partialTick, entity.f_19792_, entity.m_20189_())
      );
   }

   private static float yawFromDirection(Vec3 direction) {
      return (float)(Mth.m_14136_(direction.f_82479_, direction.f_82481_) * 180.0F / (float)Math.PI);
   }

   private static float pitchFromDirection(Vec3 direction) {
      double horizontal = Math.sqrt(direction.f_82479_ * direction.f_82479_ + direction.f_82481_ * direction.f_82481_);
      return (float)(Mth.m_14136_(direction.f_82480_, horizontal) * 180.0F / (float)Math.PI);
   }

   public void render(
      ItemProjectile entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight
   ) {
      ItemStack stack = entity.m_7846_();
      if (!stack.m_41619_()) {
         poseStack.m_85836_();
         BakedModel model = this.itemRenderer.m_174264_(stack, entity.m_9236_(), null, entity.m_19879_());
         poseStack.m_85837_(0.0, 0.15, 0.0);
         if (HookUtil.shouldUseShieldFacing(stack)) {
            poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0F - getOwnerLookYaw(entity, partialTick)));
            HookItemRenderTransforms.applyShieldProjectileTransform(poseStack, model);
         } else if (!HookUtil.shouldRenderWithoutProjectileSpin(stack) || !entity.isHookAttached()) {
            boolean sharpItem = HookUtil.shouldAlignSharpEdge(stack);
            float yaw;
            float pitch;
            if (sharpItem) {
               Vec3 direction = getSharpProjectileDirection(entity, partialTick);
               yaw = yawFromDirection(direction);
               pitch = pitchFromDirection(direction);
            } else {
               yaw = Mth.m_14179_(partialTick, entity.f_19859_, entity.m_146908_());
               pitch = Mth.m_14179_(partialTick, entity.f_19860_, entity.m_146909_());
            }

            HookItemRenderTransforms.applyProjectileFacing(poseStack, stack, model, yaw, pitch);
            if (!sharpItem && !entity.isHookAttached()) {
               float age = (float)entity.f_19797_ + partialTick;
               int spinSeed = entity.m_20148_().hashCode();
               applyRandomSpin(poseStack, age, spinSeed);
            }
         }

         poseStack.m_85841_(0.85F, 0.85F, 0.85F);
         this.itemRenderer
            .m_115143_(
               stack,
               HookItemRenderTransforms.getProjectileDisplayContext(stack, model),
               false,
               poseStack,
               buffer,
               packedLight,
               OverlayTexture.f_118083_,
               model
            );
         poseStack.m_85849_();
      }

      super.m_7392_(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull ItemProjectile entity) {
      return TextureAtlas.f_118259_;
   }
}
