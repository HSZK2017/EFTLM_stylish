package com.pla.annoyingvillagers.mixin;

import com.pla.annoyingvillagers.item.FishingRodGrappleUtil;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.projectile.FishingHook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({FishingHook.class})
public abstract class FishingHookReturnMixin {
   @Inject(
      method = {"tick"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/projectile/Projectile;tick()V",
         shift = Shift.AFTER
      )},
      cancellable = true
   )
   private void annoyingVillagers$tickTonyReturn(CallbackInfo ci) {
      FishingHook hook = (FishingHook)this;
      if (FishingRodGrappleUtil.tickNpcCombatFishingHook(hook) || FishingRodGrappleUtil.tickTonyReturningHook(hook)) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"recreateFromPacket"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/projectile/FishingHook;getPlayerOwner()Lnet/minecraft/world/entity/player/Player;"
      )},
      cancellable = true
   )
   private void annoyingVillagers$allowNpcCombatFishingHookOwner(ClientboundAddEntityPacket packet, CallbackInfo ci) {
      Entity owner = ((FishingHook)this).m_9236_().m_6815_(packet.m_131509_());
      if (FishingRodGrappleUtil.isNpcCombatFishingHookOwner(owner)) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"tick"},
      at = {@At("TAIL")}
   )
   private void annoyingVillagers$afterTonyHookTick(CallbackInfo ci) {
      FishingRodGrappleUtil.afterTonyHookVanillaTick((FishingHook)this);
   }

   @Inject(
      method = {"canHitEntity"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void annoyingVillagers$ignoreEntityHitWhileCarryingItem(Entity target, CallbackInfoReturnable<Boolean> cir) {
      if (FishingRodGrappleUtil.shouldIgnoreHookEntityHit((FishingHook)this, target)) {
         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"remove"},
      at = {@At("HEAD")}
   )
   private void annoyingVillagers$dropStickyItemWhenHookRemoved(RemovalReason reason, CallbackInfo ci) {
      FishingRodGrappleUtil.onGrappleHookRemoved((FishingHook)this);
   }
}
