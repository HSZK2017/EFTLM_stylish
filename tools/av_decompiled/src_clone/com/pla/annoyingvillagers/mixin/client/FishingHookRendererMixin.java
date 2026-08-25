package com.pla.annoyingvillagers.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.FishingHookRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolActions;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;

@Mixin({FishingHookRenderer.class})
public abstract class FishingHookRendererMixin extends EntityRenderer<FishingHook> {
   @Unique
   private static final ResourceLocation annoyingVillagers$textureLocation = ResourceLocation.withDefaultNamespace("textures/entity/fishing_hook.png");
   @Unique
   private static final RenderType annoyingVillagers$renderType = RenderType.m_110452_(annoyingVillagers$textureLocation);
   @Unique
   private static final double annoyingVillagers$viewBobbingScale = 960.0;
   @Unique
   private static final Vec3f annoyingVillagers$noTranslation = new Vec3f(0.0F, 0.0F, 0.0F);

   protected FishingHookRendererMixin(Context context) {
      super(context);
   }

   @Inject(
      method = {"render(Lnet/minecraft/world/entity/projectile/FishingHook;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void annoyingVillagers$renderWithEpicFightRodAnchor(
      FishingHook hook, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci
   ) {
      if (hook.m_19749_() instanceof LivingEntity owner) {
         poseStack.m_85836_();
         poseStack.m_85836_();
         poseStack.m_85841_(0.5F, 0.5F, 0.5F);
         poseStack.m_252781_(this.f_114476_.m_253208_());
         poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0F));
         Pose pose = poseStack.m_85850_();
         Matrix4f matrix = pose.m_252922_();
         Matrix3f normal = pose.m_252943_();
         VertexConsumer hookBuffer = buffer.m_6299_(annoyingVillagers$renderType);
         annoyingVillagers$vertex(hookBuffer, matrix, normal, packedLight, 0.0F, 0, 0, 1);
         annoyingVillagers$vertex(hookBuffer, matrix, normal, packedLight, 1.0F, 0, 1, 1);
         annoyingVillagers$vertex(hookBuffer, matrix, normal, packedLight, 1.0F, 1, 1, 0);
         annoyingVillagers$vertex(hookBuffer, matrix, normal, packedLight, 0.0F, 1, 0, 0);
         poseStack.m_85849_();
         int armSign = owner.m_5737_() == HumanoidArm.RIGHT ? 1 : -1;
         ItemStack mainHand = owner.m_21205_();
         if (!mainHand.canPerformAction(ToolActions.FISHING_ROD_CAST)) {
            armSign = -armSign;
         }

         double lineX;
         double lineY;
         double lineZ;
         float lineYOffset;
         label39: {
            float attackAnim = owner.m_21324_(partialTicks);
            float attackSwing = Mth.m_14031_(Mth.m_14116_(attackAnim) * (float) Math.PI);
            float bodyYaw = Mth.m_14179_(partialTicks, owner.f_20884_, owner.f_20883_) * (float) (Math.PI / 180.0);
            double sinYaw = (double)Mth.m_14031_(bodyYaw);
            double cosYaw = (double)Mth.m_14089_(bodyYaw);
            double armOffset = (double)armSign * 0.35;
            if (owner instanceof Player player && this.annoyingVillagers$isFirstPersonOwner(player)) {
               double fovScale = 960.0 / (double)((Integer)this.f_114476_.f_114360_.m_231837_().m_231551_()).intValue();
               Vec3 nearPlane = this.f_114476_.f_114358_.m_167684_().m_167695_((float)armSign * 0.525F, -0.1F);
               nearPlane = nearPlane.m_82490_(fovScale);
               nearPlane = nearPlane.m_82524_(attackSwing * 0.5F);
               nearPlane = nearPlane.m_82496_(-attackSwing * 0.7F);
               lineX = Mth.m_14139_((double)partialTicks, player.f_19854_, player.m_20185_()) + nearPlane.f_82479_;
               lineY = Mth.m_14139_((double)partialTicks, player.f_19855_, player.m_20186_()) + nearPlane.f_82480_;
               lineZ = Mth.m_14139_((double)partialTicks, player.f_19856_, player.m_20189_()) + nearPlane.f_82481_;
               lineYOffset = player.m_20192_();
               break label39;
            }

            Vec3 epicFightAnchor = annoyingVillagers$getEpicFightRodAnchor(owner, armSign, partialTicks);
            if (epicFightAnchor != null) {
               lineX = epicFightAnchor.f_82479_;
               lineY = epicFightAnchor.f_82480_;
               lineZ = epicFightAnchor.f_82481_;
               lineYOffset = 0.0F;
            } else {
               lineX = Mth.m_14139_((double)partialTicks, owner.f_19854_, owner.m_20185_()) - cosYaw * armOffset - sinYaw * 0.8;
               lineY = owner.f_19855_ + (double)owner.m_20192_() + (owner.m_20186_() - owner.f_19855_) * (double)partialTicks - 0.45;
               lineZ = Mth.m_14139_((double)partialTicks, owner.f_19856_, owner.m_20189_()) - sinYaw * armOffset + cosYaw * 0.8;
               lineYOffset = owner.m_6047_() ? -0.1875F : 0.0F;
            }
         }

         double hookX = Mth.m_14139_((double)partialTicks, hook.f_19854_, hook.m_20185_());
         double hookY = Mth.m_14139_((double)partialTicks, hook.f_19855_, hook.m_20186_()) + 0.25;
         double hookZ = Mth.m_14139_((double)partialTicks, hook.f_19856_, hook.m_20189_());
         float stringX = (float)(lineX - hookX);
         float stringY = (float)(lineY - hookY) + lineYOffset;
         float stringZ = (float)(lineZ - hookZ);
         VertexConsumer lineBuffer = buffer.m_6299_(RenderType.m_173247_());
         Pose linePose = poseStack.m_85850_();

         for (int segment = 0; segment <= 16; segment++) {
            annoyingVillagers$stringVertex(
               stringX, stringY, stringZ, lineBuffer, linePose, annoyingVillagers$fraction(segment, 16), annoyingVillagers$fraction(segment + 1, 16)
            );
         }

         poseStack.m_85849_();
         super.m_7392_(hook, entityYaw, partialTicks, poseStack, buffer, packedLight);
      }

      ci.cancel();
   }

   @Unique
   private boolean annoyingVillagers$isFirstPersonOwner(Player player) {
      return this.f_114476_.f_114360_ != null && this.f_114476_.f_114360_.m_92176_().m_90612_() && player == Minecraft.m_91087_().f_91074_;
   }

   @Unique
   private static Vec3 annoyingVillagers$getEpicFightRodAnchor(LivingEntity owner, int armSign, float partialTicks) {
      if (!owner.m_21205_().canPerformAction(ToolActions.FISHING_ROD_CAST) && !owner.m_21206_().canPerformAction(ToolActions.FISHING_ROD_CAST)) {
         return null;
      } else {
         try {
            if (Armatures.BIPED.get() == null) {
               return null;
            } else {
               Joint joint = armSign > 0 ? ((HumanoidArmature)Armatures.BIPED.get()).toolR : ((HumanoidArmature)Armatures.BIPED.get()).toolL;
               if (joint == null) {
                  return null;
               } else {
                  Vec3 anchor = EpicfightUtil.getJointWithTranslation(owner, annoyingVillagers$noTranslation, joint, 0.5F, 0.0);
                  return anchor == null
                     ? null
                     : anchor.m_82520_(
                        Mth.m_14139_((double)partialTicks, owner.f_19854_, owner.m_20185_()) - owner.m_20185_(),
                        Mth.m_14139_((double)partialTicks, owner.f_19855_, owner.m_20186_()) - owner.m_20186_(),
                        Mth.m_14139_((double)partialTicks, owner.f_19856_, owner.m_20189_()) - owner.m_20189_()
                     );
               }
            }
         } catch (Exception var5) {
            return null;
         }
      }
   }

   @Unique
   private static float annoyingVillagers$fraction(int numerator, int denominator) {
      return (float)numerator / (float)denominator;
   }

   @Unique
   private static void annoyingVillagers$vertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normal, int lightmapUV, float x, int y, int u, int v) {
      consumer.m_252986_(matrix, x - 0.5F, (float)y - 0.5F, 0.0F)
         .m_6122_(255, 255, 255, 255)
         .m_7421_((float)u, (float)v)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(lightmapUV)
         .m_252939_(normal, 0.0F, 1.0F, 0.0F)
         .m_5752_();
   }

   @Unique
   private static void annoyingVillagers$stringVertex(float x, float y, float z, VertexConsumer consumer, Pose pose, float currentFraction, float nextFraction) {
      float currentX = x * currentFraction;
      float currentY = y * (currentFraction * currentFraction + currentFraction) * 0.5F + 0.25F;
      float currentZ = z * currentFraction;
      float normalX = x * nextFraction - currentX;
      float normalY = y * (nextFraction * nextFraction + nextFraction) * 0.5F + 0.25F - currentY;
      float normalZ = z * nextFraction - currentZ;
      float normalLength = Mth.m_14116_(normalX * normalX + normalY * normalY + normalZ * normalZ);
      normalX /= normalLength;
      normalY /= normalLength;
      normalZ /= normalLength;
      consumer.m_252986_(pose.m_252922_(), currentX, currentY, currentZ).m_6122_(0, 0, 0, 255).m_252939_(pose.m_252943_(), normalX, normalY, normalZ).m_5752_();
   }
}
