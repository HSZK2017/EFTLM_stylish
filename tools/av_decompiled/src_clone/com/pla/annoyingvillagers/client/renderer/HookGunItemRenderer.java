package com.pla.annoyingvillagers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pla.annoyingvillagers.item.HookGunItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.ModelEvent.RegisterAdditional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class HookGunItemRenderer extends BlockEntityWithoutLevelRenderer {
   private static final ResourceLocation BASE_MODEL = ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "item/hook_gun_body");
   private static final ThreadLocal<HookGunItemRenderer.RenderedHandContext> RENDERED_HAND_CONTEXT = new ThreadLocal<>();
   public static HookGunItemRenderer instance;

   public HookGunItemRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher, EntityModelSet entityModelSet) {
      super(blockEntityRenderDispatcher, entityModelSet);
   }

   @SubscribeEvent
   public static void onRegisterReloadListener(RegisterClientReloadListenersEvent event) {
      instance = createInstance();
      event.registerReloadListener(instance);
   }

   @SubscribeEvent
   public static void onRegisterAdditionalModels(RegisterAdditional event) {
      event.register(BASE_MODEL);
   }

   public static HookGunItemRenderer getInstance() {
      if (instance == null) {
         instance = createInstance();
      }

      return instance;
   }

   private static HookGunItemRenderer createInstance() {
      return new HookGunItemRenderer(Minecraft.m_91087_().m_167982_(), Minecraft.m_91087_().m_167973_());
   }

   public static void setRenderedHandContext(LivingEntity entity, InteractionHand hand) {
      if (entity != null && hand != null) {
         RENDERED_HAND_CONTEXT.set(new HookGunItemRenderer.RenderedHandContext(entity, hand));
      } else {
         RENDERED_HAND_CONTEXT.remove();
      }
   }

   public static void clearRenderedHandContext() {
      RENDERED_HAND_CONTEXT.remove();
   }

   public void m_108829_(
      ItemStack stack,
      @NotNull ItemDisplayContext itemDisplayContext,
      @NotNull PoseStack poseStack,
      @NotNull MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      Minecraft minecraft = Minecraft.m_91087_();
      ItemRenderer itemRenderer = minecraft.m_91291_();
      BakedModel baseModel = minecraft.m_91304_().getModel(BASE_MODEL);
      renderBakedModel(itemRenderer, baseModel, stack, poseStack, buffer, combinedLight, combinedOverlay);
      ItemStack boundItem = HookGunItem.getBoundItem(stack);
      if (!boundItem.m_41619_() && !HookGunItem.isVisualHookOut(stack) && !isHookingWithRenderedStack(minecraft, stack)) {
         poseStack.m_85836_();
         HookItemRenderTransforms.applyHookGunAttachment(poseStack, boundItem, itemDisplayContext);
         if (HookItemRenderTransforms.shouldUseDisplayAttachmentRenderer(boundItem, itemDisplayContext)) {
            itemRenderer.m_269128_(
               boundItem,
               HookItemRenderTransforms.getHookGunAttachmentDisplayContext(boundItem, itemDisplayContext),
               combinedLight,
               OverlayTexture.f_118083_,
               poseStack,
               buffer,
               minecraft.f_91073_,
               0
            );
         } else {
            BakedModel boundModel = itemRenderer.m_174264_(boundItem, minecraft.f_91073_, null, 0);
            renderBakedModel(itemRenderer, boundModel, boundItem, poseStack, buffer, combinedLight, combinedOverlay);
         }

         poseStack.m_85849_();
      }
   }

   private static boolean isHookingWithRenderedStack(Minecraft minecraft, ItemStack stack) {
      HookGunItemRenderer.RenderedHandContext renderedHandContext = RENDERED_HAND_CONTEXT.get();
      if (renderedHandContext != null && minecraft.f_91073_ != null) {
         ItemStack handStack = renderedHandContext.entity.m_21120_(renderedHandContext.hand);
         if (stack == handStack || ItemStack.m_41728_(stack, handStack)) {
            return HookGunItem.hasActiveHook(minecraft.f_91073_, renderedHandContext.entity, renderedHandContext.hand == InteractionHand.MAIN_HAND);
         }
      }

      Player player = minecraft.f_91074_;
      if (player != null && minecraft.f_91073_ != null) {
         ItemStack mainHand = player.m_21205_();
         ItemStack offHand = player.m_21206_();
         if (stack == mainHand) {
            return HookGunItem.hasActiveHook(minecraft.f_91073_, player, true);
         } else if (stack == offHand) {
            return HookGunItem.hasActiveHook(minecraft.f_91073_, player, false);
         } else {
            boolean matchesMainHand = ItemStack.m_41728_(stack, mainHand);
            boolean matchesOffHand = ItemStack.m_41728_(stack, offHand);
            if (matchesMainHand && !matchesOffHand) {
               return HookGunItem.hasActiveHook(minecraft.f_91073_, player, true);
            } else if (matchesOffHand && !matchesMainHand) {
               return HookGunItem.hasActiveHook(minecraft.f_91073_, player, false);
            } else {
               return matchesMainHand ? HookGunItem.hasActiveHook(minecraft.f_91073_, player) : false;
            }
         }
      } else {
         return false;
      }
   }

   private static void renderBakedModel(
      ItemRenderer itemRenderer, BakedModel model, ItemStack stack, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay
   ) {
      for (BakedModel renderPass : model.getRenderPasses(stack, true)) {
         for (RenderType renderType : renderPass.getRenderTypes(stack, true)) {
            VertexConsumer vertexConsumer = ItemRenderer.m_115222_(buffer, renderType, true, stack.m_41790_());
            itemRenderer.m_115189_(renderPass, stack, combinedLight, combinedOverlay, poseStack, vertexConsumer);
         }
      }
   }

   private static record RenderedHandContext(LivingEntity entity, InteractionHand hand) {
   }
}
