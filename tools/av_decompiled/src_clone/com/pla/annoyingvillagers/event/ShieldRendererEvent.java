package com.pla.annoyingvillagers.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class ShieldRendererEvent extends BlockEntityWithoutLevelRenderer {
   public static ShieldRendererEvent instance;

   public ShieldRendererEvent(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
      super(blockEntityRenderDispatcher, entityModelSet);
   }

   @SubscribeEvent
   public static void onRegisterReloadListener(RegisterClientReloadListenersEvent event) {
      instance = new ShieldRendererEvent(Minecraft.m_91087_().m_167982_(), Minecraft.m_91087_().m_167973_());
      event.registerReloadListener(instance);
   }

   public void m_108829_(
      ItemStack stack,
      @NotNull ItemDisplayContext itemDisplayContext,
      PoseStack matrixStack,
      @NotNull MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      matrixStack.m_85836_();
      matrixStack.m_85841_(1.0F, -1.0F, -1.0F);
      Material renderMaterial = ModelBakery.f_119226_;
      Item shield = stack.m_41720_();
      if (shield == AnnoyingVillagersModItems.JESSICA_THE_DARK_SHIELD.get()) {
         renderMaterial = ModModelPredicateProvider.LOCATION_JESSICA_THE_DARK_SHIELD;
      } else if (shield == AnnoyingVillagersModItems.HEATER_SHIELD.get()) {
         renderMaterial = ModModelPredicateProvider.LOCATION_HEATER_SHIELD;
      } else if (shield == AnnoyingVillagersModItems.GEM_SHIELD.get()) {
         renderMaterial = ModModelPredicateProvider.LOCATION_GEM_SHIELD;
      } else if (shield == AnnoyingVillagersModItems.NETHERITE_SHIELD.get()) {
         renderMaterial = ModModelPredicateProvider.LOCATION_NETHERITE_SHIELD;
      }

      VertexConsumer ivertexBuilder = renderMaterial.m_119204_()
         .m_118381_(ItemRenderer.m_115222_(buffer, this.f_108823_.m_103119_(renderMaterial.m_119193_()), true, stack.m_41790_()));
      this.f_108823_.m_103711_().m_104306_(matrixStack, ivertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      this.f_108823_.m_103701_().m_104306_(matrixStack, ivertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.m_85849_();
   }
}
