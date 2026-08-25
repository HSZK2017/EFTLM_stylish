package com.pla.annoyingvillagers.mixin;

import com.pla.annoyingvillagers.item.BedrockWeaponItem;
import com.pla.annoyingvillagers.item.EnderAegisItem;
import com.pla.annoyingvillagers.skill.BedrockWeaponSkill;
import com.pla.annoyingvillagers.skill.EnderAegisSkill;
import java.util.Objects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.skill.guard.ImpactGuardSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.TakeDamageEvent.Attack;

@Mixin(
   value = {ImpactGuardSkill.class},
   remap = false
)
public abstract class ImpactGuardSkillMixin {
   private static boolean isAdvancedBlockableDamageSource(DamageSource damageSource) {
      return damageSource.m_269533_(DamageTypeTags.f_268415_)
         || damageSource.m_276093_(DamageTypes.f_268515_)
         || damageSource.m_269533_(DamageTypeTags.f_268745_)
         || damageSource.m_269533_(DamageTypeTags.f_268524_)
         || damageSource.m_269533_(DamageTypeTags.f_268490_);
   }

   @Inject(
      method = {"dealEvent"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void playerOnGuard(PlayerPatch<?> playerpatch, Attack event, boolean advanced, CallbackInfo ci) {
      boolean isSpecialSource = isAdvancedBlockableDamageSource(event.getDamageSource());
      if (!isSpecialSource) {
         Player player = (Player)playerpatch.getOriginal();
         if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
         }

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
