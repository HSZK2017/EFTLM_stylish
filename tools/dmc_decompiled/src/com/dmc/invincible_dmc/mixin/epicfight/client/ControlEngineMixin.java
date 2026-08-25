package com.dmc.invincible_dmc.mixin.epicfight.client;

import com.dmc.invincible_dmc.client.input.enemystep.EnemyStepController;
import com.dmc.invincible_dmc.client.input.jumpcancel.JumpCancelController;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.client.events.engine.ControlEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

@Mixin(
   value = {ControlEngine.class},
   remap = false
)
public abstract class ControlEngineMixin {
   @Shadow
   private LocalPlayer player;
   @Shadow
   private LocalPlayerPatch playerPatch;
   @Shadow
   private int tickSinceLastJump;
   @Unique
   private JumpCancelController invincible_DMC$jumpCancelController;
   @Unique
   private EnemyStepController invincible_DMC$enemyStepController;

   @Inject(
      method = {"inputTick"},
      at = {@At("HEAD")}
   )
   private void invincible_dmc$captureDmcJumpInput(Input input, CallbackInfo callbackInfo) {
      this.invincible_DMC$getEnemyStepController().captureInput(input);
   }

   @Inject(
      method = {"inputTick"},
      at = {@At("TAIL")}
   )
   private void invincible_dmc$handleDmcJumpActions(Input input, CallbackInfo callbackInfo) {
      if (this.playerPatch != null) {
         EnemyStepController enemyStepController = this.invincible_DMC$getEnemyStepController();
         enemyStepController.beginTick(this.player);
         if (!this.invincible_DMC$getJumpCancelController().tryExecute(input, this.player, this.playerPatch, this.tickSinceLastJump)) {
            enemyStepController.tryExecute(input, this.player, this.playerPatch);
         }
      }
   }

   @Unique
   private JumpCancelController invincible_DMC$getJumpCancelController() {
      if (this.invincible_DMC$jumpCancelController == null) {
         this.invincible_DMC$jumpCancelController = new JumpCancelController();
      }

      return this.invincible_DMC$jumpCancelController;
   }

   @Unique
   private EnemyStepController invincible_DMC$getEnemyStepController() {
      if (this.invincible_DMC$enemyStepController == null) {
         this.invincible_DMC$enemyStepController = new EnemyStepController();
      }

      return this.invincible_DMC$enemyStepController;
   }
}
