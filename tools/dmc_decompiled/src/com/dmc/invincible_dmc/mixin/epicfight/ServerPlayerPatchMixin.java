package com.dmc.invincible_dmc.mixin.epicfight;

import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.skill.weapon_innate.ComboBasicAttack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.DealDamageEvent.Damage;

@Mixin(
   value = {ServerPlayerPatch.class},
   remap = false
)
public class ServerPlayerPatchMixin {
   @Shadow
   private LivingEntity attackTarget;

   @Inject(
      method = {"tick"},
      at = {@At("TAIL")}
   )
   public void tick(LivingTickEvent event, CallbackInfo ci) {
      ServerPlayerPatch serverPlayerPatch = (ServerPlayerPatch)this;
      if (this.attackTarget instanceof DoppelgangerEntity doppelganger
         && doppelganger.getOwner() != null
         && doppelganger.getOwner().equals(serverPlayerPatch.getOriginal())) {
         this.attackTarget = null;
      }
   }

   @Inject(
      method = {"lambda$onJoinWorld$0"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private void invincible_dmc$onDealDamageEvent(Damage dealDamageEvent, CallbackInfo ci) {
      if (((ServerPlayerPatch)dealDamageEvent.getPlayerPatch()).getSkill(SkillSlots.WEAPON_INNATE).getSkill() instanceof ComboBasicAttack) {
         ci.cancel();
      }
   }
}
