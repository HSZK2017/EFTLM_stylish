package com.dmc.invincible_dmc.mixin.epicfight;

import com.dmc.invincible_dmc.api.animation.types.DmcStunAnimation;
import com.dmc.invincible_dmc.skill.weapon_combo.Yamato;
import com.dmc.invincible_dmc.skill.weapon_innate.ComboBasicAttack;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.DMCLog;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@Mixin({SkillContainer.class})
public abstract class SkillContainerMixin {
   @Shadow(
      remap = false
   )
   protected Skill containingSkill;

   @Unique
   private SkillContainer epicFight_Invincible_1_20_1$self() {
      return (SkillContainer)this;
   }

   @WrapOperation(
      method = {"canUse"},
      at = {@At(
         value = "INVOKE",
         target = "Lyesman/epicfight/skill/Skill;isExecutableState(Lyesman/epicfight/world/capabilities/entitypatch/player/PlayerPatch;)Z"
      )},
      remap = false
   )
   private boolean invincible$relaxDefenseInStun(Skill skill, PlayerPatch<?> executor, Operation<Boolean> original) {
      if (skill.getCategory().equals(SkillCategories.GUARD) || skill.getCategory().equals(SkillCategories.DODGE)) {
         AnimationPlayer player = DMCAnimationUtils.getMainPlayer(executor);
         if (player != null) {
            StaticAnimation anim = DMCAnimationUtils.getRealAnimation(player);
            if (DMCAnimationUtils.isAnimationType(anim, DmcStunAnimation.class)) {
               return true;
            }
         }
      }

      return (Boolean)original.call(new Object[]{skill, executor});
   }

   @Inject(
      method = {"requestCasting"},
      at = {@At("HEAD")},
      cancellable = true,
      remap = false
   )
   private void invincible_dmc$requestExecute(ServerPlayerPatch executor, FriendlyByteBuf buf, CallbackInfoReturnable<Boolean> cir) {
      if (this.containingSkill instanceof ComboBasicAttack) {
         SkillContainer self = this.epicFight_Invincible_1_20_1$self();
         boolean canExecute = this.containingSkill.canExecute(self);
         boolean executableState = this.containingSkill.isExecutableState(executor);
         if (!canExecute || !executableState) {
            DMCLog.info(
               DMCLog.Category.COMBO_SERVER,
               "[ComboGate] REJECT player={} reason=skill_state canExecute={} executableState={}",
               ((ServerPlayer)executor.getOriginal()).m_7755_().getString(),
               canExecute,
               executableState
            );
            cir.setReturnValue(false);
            return;
         }

         MinecraftServer server = ((ServerPlayer)executor.getOriginal()).f_8924_;
         boolean requiresAuthoritativeGate = server.m_6982_() || !server.m_129792_();
         boolean sdtRapidSlashLoopWindow = Yamato.isSdtRapidSlashLoopWindow(executor);
         if (requiresAuthoritativeGate && !executor.getEntityState().canBasicAttack() && !sdtRapidSlashLoopWindow) {
            DMCLog.info(
               DMCLog.Category.COMBO_SERVER,
               "[ComboGate] REJECT player={} reason=server_can_basic_attack_closed dedicated={} singleplayer={} canBasicAttack={}",
               ((ServerPlayer)executor.getOriginal()).m_7755_().getString(),
               server.m_6982_(),
               server.m_129792_(),
               executor.getEntityState().canBasicAttack()
            );
            cir.setReturnValue(false);
            return;
         }

         this.containingSkill.executeOnServer(self, buf);
         cir.setReturnValue(true);
      }
   }
}
