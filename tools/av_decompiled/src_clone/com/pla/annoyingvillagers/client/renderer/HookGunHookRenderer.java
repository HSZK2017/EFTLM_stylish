package com.pla.annoyingvillagers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Axis;
import com.pla.annoyingvillagers.entity.HookGunHookEntity;
import com.pla.annoyingvillagers.item.HookGunItem;
import com.pla.annoyingvillagers.util.HookUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class HookGunHookRenderer extends EntityRenderer<HookGunHookEntity> {
   private static final ResourceLocation ROPE_TEXTURE = ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/hook_gun_rope.png");
   private static final RenderType ROPE_RENDER = RenderType.m_110446_(ROPE_TEXTURE);
   private final Context context;

   public HookGunHookRenderer(Context context) {
      super(context);
      this.context = context;
   }

   public void render(
      @NotNull HookGunHookEntity hook, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight
   ) {
      LivingEntity owner = hook.getHookOwner();
      if (owner != null && owner.m_6084_()) {
         int handRight = hook.isRightHand() ? 1 : -1;
         Vec3 handPosition = HookGunItem.getHookStartPosition(owner, hook.isRightHand());
         Vec3 attachDirection = getAttachDirection(hook, handPosition, partialTicks);
         this.renderHookItem(hook, poseStack, buffer, packedLight, attachDirection, handRight, partialTicks);
         this.renderRope(hook, handPosition, partialTicks, poseStack, buffer, packedLight);
         super.m_7392_(hook, entityYaw, partialTicks, poseStack, buffer, packedLight);
      } else {
         this.renderHookItem(hook, poseStack, buffer, packedLight, new Vec3(0.0, 0.0, 1.0), 1, partialTicks);
         super.m_7392_(hook, entityYaw, partialTicks, poseStack, buffer, packedLight);
      }
   }

   private void renderHookItem(
      HookGunHookEntity hook, PoseStack poseStack, MultiBufferSource buffer, int packedLight, Vec3 attachDirection, int handRight, float partialTicks
   ) {
      ItemStack stack = hook.m_7846_();
      if (!stack.m_41619_()) {
         BakedModel model = this.context.m_174025_().m_174264_(stack, hook.m_9236_(), null, hook.m_19879_());
         ItemDisplayContext displayContext = HookItemRenderTransforms.getHookGunProjectileDisplayContext(stack, model);
         poseStack.m_85836_();
         float projectileScale = HookItemRenderTransforms.getHookGunProjectileScale(stack);
         poseStack.m_85841_(projectileScale, projectileScale, projectileScale);
         if (HookUtil.shouldUseShieldFacing(stack)) {
            poseStack.m_252781_(Axis.f_252436_.m_252977_(180.0F - getOwnerLookYaw(hook, partialTicks)));
            if (displayContext == ItemDisplayContext.FIXED) {
               HookItemRenderTransforms.applyShieldProjectileTransform(poseStack, model);
            }
         } else if (!HookUtil.shouldRenderWithoutProjectileSpin(stack)) {
            Vec3 shootDirection = attachDirection.m_82490_(-1.0);
            double horizontal = Math.sqrt(shootDirection.f_82479_ * shootDirection.f_82479_ + shootDirection.f_82481_ * shootDirection.f_82481_);
            float yaw = (float)(Mth.m_14136_(shootDirection.f_82479_, shootDirection.f_82481_) * 180.0F / (float)Math.PI);
            float pitch = (float)(Mth.m_14136_(shootDirection.f_82480_, horizontal) * 180.0F / (float)Math.PI);
            HookItemRenderTransforms.applyProjectileFacing(poseStack, stack, model, yaw, pitch);
            if (!HookUtil.shouldAlignSharpEdge(stack)) {
               poseStack.m_252781_(Axis.f_252436_.m_252977_(45.0F * (float)handRight));
               poseStack.m_252781_(Axis.f_252403_.m_252977_(-45.0F));
            }
         }

         this.context.m_174025_().m_115143_(stack, displayContext, false, poseStack, buffer, packedLight, OverlayTexture.f_118083_, model);
         poseStack.m_85849_();
      }
   }

   private static float getOwnerLookYaw(HookGunHookEntity hook, float partialTick) {
      Entity owner = hook.getHookOwner();
      return owner != null ? Mth.m_14179_(partialTick, owner.f_19859_, owner.m_146908_()) : Mth.m_14179_(partialTick, hook.f_19859_, hook.m_146908_());
   }

   private void renderRope(HookGunHookEntity hook, Vec3 handPosition, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
      poseStack.m_85836_();
      Pose pose = poseStack.m_85850_();
      VertexConsumer vertexBuffer = buffer.m_6299_(ROPE_RENDER);
      Vec3 hookPosition = partialPosition(hook, partialTicks);
      drawSegment(Vec3.f_82478_, handPosition.m_82546_(hookPosition), vertexBuffer, pose.m_252922_(), pose.m_252943_(), packedLight);
      poseStack.m_85849_();
   }

   private static Vec3 getAttachDirection(HookGunHookEntity hook, Vec3 handPosition, float partialTicks) {
      Vec3 direction = hook.m_20184_().m_82490_(-1.0);
      if (direction.m_82556_() <= 1.0E-7) {
         direction = handPosition.m_82546_(partialPosition(hook, partialTicks));
      }

      return direction.m_82556_() <= 1.0E-7 ? new Vec3(0.0, 0.0, 1.0) : direction.m_82541_();
   }

   private static Vec3 partialPosition(Entity entity, float partialTicks) {
      return new Vec3(
         Mth.m_14139_((double)partialTicks, entity.f_19790_, entity.m_20185_()),
         Mth.m_14139_((double)partialTicks, entity.f_19791_, entity.m_20186_()),
         Mth.m_14139_((double)partialTicks, entity.f_19792_, entity.m_20189_())
      );
   }

   private static void drawSegment(Vec3 start, Vec3 finish, VertexConsumer vertexBuffer, Matrix4f matrix, Matrix3f normalMatrix, int packedLight) {
      if (!(start.m_82546_(finish).m_82553_() < 0.05)) {
         Vec3 diff = finish.m_82546_(start);
         Vec3 forward = diff.m_82541_();
         Vec3 up = forward.m_82537_(new Vec3(1.0, 0.0, 0.0));
         if (up.m_82556_() <= 1.0E-7) {
            up = forward.m_82537_(new Vec3(0.0, 0.0, 1.0));
         }

         up = up.m_82541_().m_82490_(0.025);
         Vec3 side = forward.m_82537_(up).m_82541_().m_82490_(0.025);
         Vec3[] corners = new Vec3[]{
            up.m_82490_(-1.0).m_82549_(side.m_82490_(-1.0)), up.m_82549_(side.m_82490_(-1.0)), up.m_82549_(side), up.m_82490_(-1.0).m_82549_(side)
         };

         for (int size = 0; size < 4; size++) {
            Vec3 corner1 = corners[size];
            Vec3 corner2 = corners[(size + 1) % 4];
            Vec3 normal1 = corner1.m_82541_();
            Vec3 normal2 = corner2.m_82541_();
            Vec3 corner1Start = start.m_82549_(corner1);
            Vec3 corner2Start = start.m_82549_(corner2);
            Vec3 corner1Finish = finish.m_82549_(corner1);
            Vec3 corner2Finish = finish.m_82549_(corner2);
            vertex(vertexBuffer, matrix, normalMatrix, corner1Start, normal1, 0.0F, 0.0F, packedLight);
            vertex(vertexBuffer, matrix, normalMatrix, corner2Start, normal2, 1.0F, 0.0F, packedLight);
            vertex(vertexBuffer, matrix, normalMatrix, corner2Finish, normal2, 1.0F, 1.0F, packedLight);
            vertex(vertexBuffer, matrix, normalMatrix, corner1Finish, normal1, 0.0F, 1.0F, packedLight);
         }
      }
   }

   private static void vertex(
      VertexConsumer vertexBuffer, Matrix4f matrix, Matrix3f normalMatrix, Vec3 position, Vec3 normal, float u, float v, int packedLight
   ) {
      vertexBuffer.m_252986_(matrix, (float)position.f_82479_, (float)position.f_82480_, (float)position.f_82481_)
         .m_6122_(255, 255, 255, 255)
         .m_7421_(u, v)
         .m_86008_(OverlayTexture.f_118083_)
         .m_85969_(packedLight)
         .m_252939_(normalMatrix, (float)normal.f_82479_, (float)normal.f_82480_, (float)normal.f_82481_)
         .m_5752_();
   }

   public boolean shouldRender(@NotNull HookGunHookEntity entity, @NotNull Frustum frustum, double x, double y, double z) {
      return true;
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull HookGunHookEntity entity) {
      return ROPE_TEXTURE;
   }
}
