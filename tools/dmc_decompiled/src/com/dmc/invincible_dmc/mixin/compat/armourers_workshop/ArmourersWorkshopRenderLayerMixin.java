package com.dmc.invincible_dmc.mixin.compat.armourers_workshop;

import com.dmc.invincible_dmc.compat.armourers_workshop.ArmourersWorkshopCompat;
import moe.plushie.armourers_workshop.api.client.IGraphicsContext;
import moe.plushie.armourers_workshop.compat.client.entity.state.AbstractRenderState;
import moe.plushie.armourers_workshop.core.client.render.layer.SkinWardrobeLayer;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {SkinWardrobeLayer.class},
   remap = false
)
public abstract class ArmourersWorkshopRenderLayerMixin {
   @Inject(
      method = {"abi$render"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void invincible$hideWardrobeWhileRenderingSdt(
      EntityRenderState renderState, int packedLight, int packedOverlay, float limbSwing, float partialTicks, IGraphicsContext graphicsContext, CallbackInfo ci
   ) {
      AbstractRenderState.unwrap(renderState, (entity, animationProgress, renderPartialTicks) -> {
         if (entity instanceof AbstractClientPlayer player && ArmourersWorkshopCompat.shouldSuppressWardrobe(player)) {
            ci.cancel();
         }
      });
   }
}
