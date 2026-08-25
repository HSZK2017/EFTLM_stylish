package com.dmc.invincible_dmc.mixin.epicfight.client;

import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.impl.IEpicFightCameraAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@Mixin(
   value = {EpicFightCameraAPI.class},
   remap = false
)
public abstract class EpicFightCameraAPIMixin implements IEpicFightCameraAPI {
   @Shadow
   private LivingEntity focusingEntity;

   @Shadow
   private void sendTargeting(LivingEntity target) {
   }

   @Override
   public void dmc$forceSetFocusingEntity(LivingEntity target) {
      this.focusingEntity = target;
      this.sendTargeting(target);
   }

   @Inject(
      method = {"setLockOn"},
      at = {@At("TAIL")}
   )
   private void invincible$syncCameraLockingOn(boolean flag, CallbackInfo ci) {
      LocalPlayer player = Minecraft.m_91087_().f_91074_;
      if (player != null) {
         LocalPlayerPatch playerPatch = (LocalPlayerPatch)EpicFightCapabilities.getEntityPatch(player, LocalPlayerPatch.class);
         if (playerPatch != null) {
            SkillDataManager sdm = playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getDataManager();
            if (sdm != null && sdm.hasData((SkillDataKey)DMCSkillDataKeys.CAMERA_LOCKING_ON.get())) {
               boolean current = (Boolean)sdm.getDataValue((SkillDataKey)DMCSkillDataKeys.CAMERA_LOCKING_ON.get());
               if (current != flag) {
                  sdm.setDataSync((SkillDataKey)DMCSkillDataKeys.CAMERA_LOCKING_ON.get(), flag);
               }
            }
         }
      }
   }
}
