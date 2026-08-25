package com.dmc.invincible_dmc.mixin.epicfight.client;

import com.dmc.invincible_dmc.api.animation.RuntimeArmatureManager;
import com.dmc.invincible_dmc.client.renderer.RuntimePlayerMeshProfiles;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PPlayerRenderer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

@Mixin(
   value = {PPlayerRenderer.class},
   remap = false
)
public abstract class PPlayerRendererMixin {
   @Inject(
      method = {"getMeshProvider*"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void invincible$resolveRuntimeMesh(
      AbstractClientPlayerPatch<AbstractClientPlayer> patch, CallbackInfoReturnable<AssetAccessor<HumanoidMesh>> callback
   ) {
      RuntimePlayerMeshProfiles.resolve(RuntimeArmatureManager.getActiveProfile(patch), patch).ifPresent(callback::setReturnValue);
   }
}
