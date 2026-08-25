package com.pla.annoyingvillagers.mixin;

import com.pla.annoyingvillagers.item.BedrockWeaponItem;
import com.pla.annoyingvillagers.item.EnderAegisItem;
import com.pla.annoyingvillagers.item.FishingRodGrappleUtil;
import com.pla.annoyingvillagers.item.HookGunItem;
import com.pla.annoyingvillagers.skill.BedrockWeaponSkill;
import com.pla.annoyingvillagers.skill.EnderAegisSkill;
import java.util.Objects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.TakeDamageEvent.Attack;

@Mixin(
   value = {GuardSkill.class},
   remap = false
)
public abstract class GuardSkillMixin {
   @Inject(
      method = {"canExecute"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void annoyingVillagers$skipGuardForOffhandUtilityItem(SkillContainer container, CallbackInfoReturnable<Boolean> cir) {
      Player player = (Player)container.getExecutor().getOriginal();
      if (FishingRodGrappleUtil.shouldOffhandFishingRodTakeRightClick(player) || HookGunItem.shouldOffhandHookGunTakeRightClick(player)) {
         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"dealEvent"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void playerOnGuard(PlayerPatch<?> playerpatch, Attack event, boolean advanced, CallbackInfo ci) {
      Player player = (Player)playerpatch.getOriginal();
      if (player instanceof ServerPlayer serverPlayer) {
         ItemStack main = serverPlayer.m_21205_();
         if (main.m_41720_() instanceof EnderAegisItem) {
            if (main.m_41782_() && Objects.requireNonNull(main.m_41783_()).m_128471_("SecondForm")) {
               EnderAegisItem.shieldShoot(serverPlayer.m_9236_(), serverPlayer);
            } else {
               EnderAegisSkill.onParry((ServerPlayerPatch)playerpatch);
            }
         }

         if (main.m_41720_() instanceof BedrockWeaponItem) {
            BedrockWeaponSkill.onParry((ServerPlayerPatch)playerpatch);
         }
      }
   }
}
