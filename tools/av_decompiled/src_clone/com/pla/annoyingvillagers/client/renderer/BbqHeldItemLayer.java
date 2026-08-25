package com.pla.annoyingvillagers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.pla.annoyingvillagers.client.model.ModelBbq;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BbqHeldItemLayer extends RenderLayer<Chicken, ChickenModel<Chicken>> {
   private final ItemInHandRenderer itemInHandRenderer;

   public BbqHeldItemLayer(RenderLayerParent<Chicken, ChickenModel<Chicken>> parent, ItemInHandRenderer itemInHandRenderer) {
      super(parent);
      this.itemInHandRenderer = itemInHandRenderer;
   }

   public void render(
      @NotNull PoseStack poseStack,
      @NotNull MultiBufferSource buffer,
      int packedLight,
      @NotNull Chicken entity,
      float limbSwing,
      float limbSwingAmount,
      float partialTick,
      float ageInTicks,
      float netHeadYaw,
      float headPitch
   ) {
      ItemStack mainHandItem = entity.m_21205_();
      if (!mainHandItem.m_41619_() && this.m_117386_() instanceof ModelBbq<?> bbqModel) {
         poseStack.m_85836_();
         bbqModel.getBeak().m_104299_(poseStack);
         poseStack.m_85837_(-0.8, -0.1875, -0.1875);
         poseStack.m_252781_(Axis.f_252436_.m_252977_(90.0F));
         poseStack.m_252781_(Axis.f_252529_.m_252977_(90.0F));
         this.itemInHandRenderer.m_269530_(entity, mainHandItem, ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight);
         poseStack.m_85849_();
      }

      ItemStack offHandItem = entity.m_21206_();
      if (!offHandItem.m_41619_() && this.m_117386_() instanceof ModelBbq<?> bbqModel) {
         poseStack.m_85836_();
         bbqModel.getBeak().m_104299_(poseStack);
         poseStack.m_85837_(0.0, -0.1875, -0.1875);
         poseStack.m_252781_(Axis.f_252436_.m_252977_(90.0F));
         poseStack.m_252781_(Axis.f_252529_.m_252977_(90.0F));
         this.itemInHandRenderer.m_269530_(entity, offHandItem, ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight);
         poseStack.m_85849_();
      }
   }
}
