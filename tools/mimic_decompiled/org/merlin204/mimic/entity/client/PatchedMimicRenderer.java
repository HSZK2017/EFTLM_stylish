package org.merlin204.mimic.entity.client;

import com.merlin204.avalon.entity.client.model.EmptyEntityModel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.merlin204.mimic.entity.MimicEntity;
import org.merlin204.mimic.entity.MimicPatch;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;

@OnlyIn(Dist.CLIENT)
public class PatchedMimicRenderer
   extends PatchedLivingEntityRenderer<MimicEntity, MimicPatch<MimicEntity>, EmptyEntityModel<MimicEntity>, LivingEntityRenderer<MimicEntity, EmptyEntityModel<MimicEntity>>, SkinnedMesh> {
   private AssetAccessor<? extends SkinnedMesh> meshAssetAccessor = null;

   public PatchedMimicRenderer(Context context, EntityType<?> entityType) {
      super(context, entityType);
   }

   public void render(
      MimicEntity entity,
      MimicPatch entitypatch,
      LivingEntityRenderer renderer,
      MultiBufferSource buffer,
      PoseStack poseStack,
      int packedLight,
      float partialTicks
   ) {
      if (entity != null && entitypatch != null && renderer != null && buffer != null && poseStack != null) {
         Armature armature = entitypatch.getArmature();
         AssetAccessor<? extends SkinnedMesh> meshAccessor = entity.getMesh();
         if (meshAccessor != null) {
            this.meshAssetAccessor = meshAccessor;
            SkinnedMesh mesh = (SkinnedMesh)meshAccessor.get();
            ResourceLocation texture = entity.getTexture();
            if (armature != null && mesh != null && texture != null) {
               ResourceLocation litTexture = entity.getLitTexture();
               poseStack.m_85836_();
               this.mulPoseStack(poseStack, armature, entity, entitypatch, partialTicks);
               this.setArmaturePose(entitypatch, armature, partialTicks);
               ItemStack offHandStack = entity.m_21206_();
               ItemStack mainHandStack = entity.m_21205_();
               ClientEngine clientEngine = ClientEngine.getInstance();
               RenderEngine renderEngine = clientEngine == null ? null : clientEngine.renderEngine;
               if (renderEngine != null && mainHandStack != null && mainHandStack.m_41720_() != Items.f_41852_) {
                  RenderItemBase itemRenderer = renderEngine.getItemRenderer(mainHandStack);
                  if (itemRenderer != null) {
                     itemRenderer.renderItemInHand(
                        mainHandStack, entitypatch, InteractionHand.MAIN_HAND, armature.getPoseMatrices(), buffer, poseStack, packedLight, partialTicks
                     );
                  }
               }

               if (renderEngine != null && entitypatch.isOffhandItemValid() && offHandStack != null && offHandStack.m_41720_() != Items.f_41852_) {
                  RenderItemBase itemRenderer = renderEngine.getItemRenderer(offHandStack);
                  if (itemRenderer != null) {
                     itemRenderer.renderItemInHand(
                        offHandStack, entitypatch, InteractionHand.OFF_HAND, armature.getPoseMatrices(), buffer, poseStack, packedLight, partialTicks
                     );
                  }
               }

               mesh.draw(
                  poseStack,
                  buffer,
                  RenderType.m_110452_(texture),
                  packedLight,
                  1.0F,
                  1.0F,
                  1.0F,
                  1.0F,
                  OverlayTexture.f_118083_,
                  entitypatch.getArmature(),
                  armature.getPoseMatrices()
               );
               if (litTexture != null) {
                  mesh.draw(
                     poseStack,
                     buffer,
                     RenderType.m_234338_(litTexture),
                     packedLight,
                     1.0F,
                     1.0F,
                     1.0F,
                     1.0F,
                     OverlayTexture.f_118083_,
                     entitypatch.getArmature(),
                     armature.getPoseMatrices()
                  );
               }

               this.renderLayer(renderer, entitypatch, entity, armature.getPoseMatrices(), buffer, poseStack, packedLight, partialTicks);
               if (Minecraft.m_91087_().m_91290_().m_114377_() && entitypatch.getClientAnimator() != null) {
                  entitypatch.getClientAnimator().renderDebuggingInfoForAllLayers(poseStack, buffer, partialTicks);
               }

               poseStack.m_85849_();
            }
         }
      }
   }

   public AssetAccessor<SkinnedMesh> getDefaultMesh() {
      return (AssetAccessor<SkinnedMesh>)(this.meshAssetAccessor != null && this.meshAssetAccessor.get() instanceof SkinnedMesh
         ? this.meshAssetAccessor
         : Meshes.BOOTS);
   }
}
