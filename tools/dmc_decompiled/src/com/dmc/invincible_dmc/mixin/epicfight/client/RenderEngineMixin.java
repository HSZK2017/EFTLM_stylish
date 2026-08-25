package com.dmc.invincible_dmc.mixin.epicfight.client;

import com.dmc.invincible_dmc.client.renderer.patched.entity.PSdtPlayerRenderer;
import java.util.function.Function;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;

@Mixin({RenderEngine.class})
public class RenderEngineMixin {
   @Inject(
      method = {"reloadEntityRenderers"},
      at = {@At(
         value = "NEW",
         target = "yesman/epicfight/client/renderer/FirstPersonRenderer",
         shift = Shift.BEFORE
      )},
      remap = false
   )
   private void invincible$replacePlayerRenderer(Context context, CallbackInfo ci) {
      ((RenderEngineAccessor)this)
         .getEntityRendererProvider()
         .put(
            EntityType.f_20532_,
            (Function<EntityType, PatchedEntityRenderer>)entityType -> new PSdtPlayerRenderer(context, entityType).initLayerLast(context, entityType)
         );
   }
}
