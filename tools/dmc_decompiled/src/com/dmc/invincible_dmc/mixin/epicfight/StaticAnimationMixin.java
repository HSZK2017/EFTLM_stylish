package com.dmc.invincible_dmc.mixin.epicfight;

import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ICrazyComboNode;
import com.dmc.invincible_dmc.api.skill.SubComboNode;
import com.dmc.invincible_dmc.api.weapon.WeaponActionChainRegistry;
import com.dmc.invincible_dmc.capability.DMCPlayer;
import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionSession;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.event.SheathInEvents;
import com.dmc.invincible_dmc.skill.weapon_innate.AbstractDmcInnateSkill;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@Mixin(
   value = {StaticAnimation.class},
   remap = false
)
public abstract class StaticAnimationMixin extends DynamicAnimation {
   @Shadow
   @Final
   protected Map<AnimationProperty<?>, Object> properties;

   @Inject(
      method = {"end"},
      at = {@At("HEAD")}
   )
   private void invincible_dmc$onAnimationEnd(
      LivingEntityPatch<?> entityPatch, AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd, CallbackInfo ci
   ) {
      if (entityPatch instanceof PlayerPatch<?> playerPatch
         && playerPatch.getSkill(SkillSlots.WEAPON_INNATE).getSkill() instanceof AbstractDmcInnateSkill
         && !this.isLinkAnimation()) {
         DMCPlayer DMCPlayer = DMCPlayerCapabilityProvider.get((Player)playerPatch.getOriginal());
         ComboNode activeNode = DMCPlayer.getCurrentLogicNode();
         AssetAccessor<? extends StaticAnimation> nextRealAnim = null;
         if (nextAnimation != null && nextAnimation.get() != null) {
            nextRealAnim = DMCAnimationUtils.getRealAnimationAccessor((DynamicAnimation)nextAnimation.get());
         }

         AssetAccessor<? extends DynamicAnimation> outgoingAccessor = this.getAccessor();
         ResourceLocation outgoingAnimation = outgoingAccessor != null ? outgoingAccessor.registryName() : null;
         ResourceLocation incomingAnimation = nextRealAnim != null ? nextRealAnim.registryName() : null;
         if (outgoingAnimation != null && incomingAnimation != null && WeaponActionChainRegistry.belongsToSameChain(outgoingAnimation, incomingAnimation)) {
            return;
         }

         if (activeNode instanceof ICrazyComboNode ccNode && nextRealAnim != null && invincible_DMC$isCCNextAnim(ccNode, nextRealAnim)) {
            return;
         }

         WeaponActionSession actionSession = DMCPlayer.getActionSession();
         if (actionSession != null && outgoingAnimation != null && WeaponActionChainRegistry.matchesSession(outgoingAnimation, actionSession)) {
            DMCPlayer.clearComboStatePreservingAction();
         } else {
            DMCPlayer.clear();
         }

         SheathInEvents.consumeTntBlocks(((LivingEntity)entityPatch.getOriginal()).m_20148_());
         if (entityPatch.isLogicalClient()) {
            EpicFightCameraAPI camera;
            boolean var10000;
            label58: {
               camera = EpicFightCameraAPI.getInstance();
               if (this.properties.get(YamatoAttackAnimation.CORRECT_YROT_TO_CAMERA) instanceof Boolean b && b) {
                  var10000 = true;
                  break label58;
               }

               var10000 = false;
            }

            boolean correctYRot = var10000;
            if (camera.isLockingOnTarget() && correctYRot) {
               entityPatch.setYRot(camera.getCameraYRot());
            }
         }
      }

      if (entityPatch instanceof DoppelgangerPatch doppelgangerPatch && !this.isLinkAnimation()) {
         DoppelgangerEntity doppelgangerEntity = (DoppelgangerEntity)doppelgangerPatch.getOriginal();
         doppelgangerEntity.resetAnimationSpeed();
      }
   }

   @Unique
   private static boolean invincible_DMC$isCCNextAnim(ICrazyComboNode ccNode, AssetAccessor<? extends StaticAnimation> nextRealAnim) {
      SubComboNode sub = ccNode.getCcChase();
      return sub != null && DMCAnimationUtils.sameAccessor(nextRealAnim, sub.getAnimationAccessor())
         ? true
         : nextRealAnim != null
            && nextRealAnim.registryName() != null
            && (
               ICrazyComboNode.matches(ccNode.getCcFinish(), nextRealAnim.registryName())
                  || ICrazyComboNode.matches(ccNode.getCcFinishNoChase(), nextRealAnim.registryName())
            );
   }
}
