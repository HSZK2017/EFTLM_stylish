package com.dmc.invincible_dmc.client;

import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import com.dmc.invincible_dmc.api.weapon.WeaponActionChainRegistry;
import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.capability.weapon.DmcWeaponManager;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionSession;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerState;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerStateProvider;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.FORGE,
   value = {Dist.CLIENT}
)
public final class ClientWeaponRenderManager {
   private ClientWeaponRenderManager() {
   }

   public static boolean applyServerState(
      Player player,
      DmcWeaponType activeWeapon,
      DmcWeaponType serverRenderedWeapon,
      @Nullable DmcWeaponType serverPendingWeapon,
      long barrierSessionId,
      int sequence
   ) {
      YamatoPlayerState state = YamatoPlayerStateProvider.get(player);
      if (state != YamatoPlayerState.EMPTY && sequence >= state.getWeaponSwitchSequence()) {
         boolean activeWeaponChanged = state.getActiveWeapon() != activeWeapon;
         state.setActiveWeapon(activeWeapon);
         state.setWeaponSwitchSequence(sequence);
         if (serverPendingWeapon != null) {
            ResourceLocation barrierAnimation = state.getPendingRenderBarrierSessionId() == barrierSessionId ? state.getPendingRenderAnimation() : null;
            if (barrierAnimation == null) {
               barrierAnimation = getCurrentClientAction(player);
            }

            state.queueRenderedWeapon(serverPendingWeapon, barrierAnimation, barrierSessionId);
            deferOrCommit(player, state, serverPendingWeapon, barrierSessionId);
         } else if (state.getRenderedWeapon() == serverRenderedWeapon) {
            state.clearPendingRenderedWeapon();
         } else {
            ResourceLocation currentAction = getCurrentClientAction(player);
            ResourceLocation barrierAnimation = state.getPendingRenderAnimation();
            long localBarrierSessionId = state.getPendingRenderBarrierSessionId();
            if (currentAction != null) {
               state.queueRenderedWeapon(
                  serverRenderedWeapon,
                  barrierAnimation != null ? barrierAnimation : currentAction,
                  localBarrierSessionId != 0L ? localBarrierSessionId : barrierSessionId
               );
               deferOrCommit(player, state, serverRenderedWeapon, barrierSessionId);
            } else {
               commitPresentation(player, state, serverRenderedWeapon);
            }
         }

         return activeWeaponChanged;
      } else {
         return false;
      }
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         Minecraft minecraft = Minecraft.m_91087_();
         if (minecraft.f_91073_ != null) {
            for (Player player : minecraft.f_91073_.m_6907_()) {
               YamatoPlayerState state = YamatoPlayerStateProvider.get(player);
               DmcWeaponType pendingWeapon = state.getPendingRenderedWeapon();
               if (state != YamatoPlayerState.EMPTY && pendingWeapon != null) {
                  deferOrCommit(player, state, pendingWeapon, state.getPendingRenderBarrierSessionId());
               }
            }
         }
      }
   }

   private static void deferOrCommit(Player player, YamatoPlayerState state, DmcWeaponType targetWeapon, long barrierSessionId) {
      if (state.getRenderedWeapon() == targetWeapon) {
         state.clearPendingRenderedWeapon();
      } else {
         ResourceLocation currentAction = getCurrentClientAction(player);
         ResourceLocation pendingAction = state.getPendingRenderAnimation();
         if (currentAction == null) {
            commitPresentation(player, state, targetWeapon);
         } else if (pendingAction == null) {
            state.queueRenderedWeapon(targetWeapon, currentAction, barrierSessionId);
         } else if (!pendingAction.equals(currentAction)) {
            if (DmcWeaponManager.isDeferredRenderContinuation(pendingAction, currentAction)) {
               state.queueRenderedWeapon(targetWeapon, currentAction, barrierSessionId);
            } else {
               WeaponActionSession actionSession = DMCPlayerCapabilityProvider.get(player).getActionSession();
               if (actionSession != null
                  && actionSession.sessionId() == barrierSessionId
                  && WeaponActionChainRegistry.matchesSession(currentAction, actionSession)) {
                  state.queueRenderedWeapon(targetWeapon, currentAction, barrierSessionId);
               } else {
                  commitPresentation(player, state, targetWeapon);
               }
            }
         }
      }
   }

   private static void commitPresentation(Player player, YamatoPlayerState state, DmcWeaponType weaponType) {
      DmcWeaponType previousWeapon = state.getRenderedWeapon();
      state.setRenderedWeapon(weaponType);
      state.clearPendingRenderedWeapon();
      if (previousWeapon != weaponType) {
         refreshLivingMotions(player, weaponType);
      }
   }

   private static void refreshLivingMotions(Player player, DmcWeaponType weaponType) {
      PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
      if (playerPatch != null) {
         ClientAnimator animator = playerPatch.getClientAnimator();
         AnimationPlayer animationPlayer = DMCAnimationUtils.getMainPlayer(animator);
         String previousAnimation = animationPlayer != null && !animationPlayer.isEmpty()
            ? DMCAnimationUtils.getRealAnimationAccessor(animationPlayer).registryName().toString()
            : "none";
         Map<LivingMotion, AssetAccessor<? extends StaticAnimation>> livingMotions = new HashMap<>(
            playerPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getLivingMotionModifier(playerPatch, InteractionHand.MAIN_HAND)
         );
         livingMotions.putAll(
            playerPatch.getAdvancedHoldingItemCapability(InteractionHand.OFF_HAND).getLivingMotionModifier(playerPatch, InteractionHand.OFF_HAND)
         );
         animator.resetLivingAnimations();
         livingMotions.forEach(animator::addLivingAnimation);
         AssetAccessor<? extends StaticAnimation> expectedAnimation = animator.getLivingMotion(playerPatch.currentLivingMotion);
         if (expectedAnimation != null && !playerPatch.getEntityState().inaction()) {
            animator.playAnimation(expectedAnimation, 0.0F);
         }

         DMCLog.info(
            DMCLog.Category.RENDER,
            "[WeaponMotion] CLIENT_COMMIT player={} weapon={} motion={} inaction={} previous={} expected={}",
            player.m_7755_().getString(),
            weaponType,
            playerPatch.currentLivingMotion,
            playerPatch.getEntityState().inaction(),
            previousAnimation,
            expectedAnimation != null ? expectedAnimation.registryName() : "none"
         );
      }
   }

   @Nullable
   private static ResourceLocation getCurrentClientAction(Player player) {
      PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
      if (playerPatch == null) {
         return null;
      } else {
         AnimationPlayer animationPlayer = DMCAnimationUtils.getMainPlayer(playerPatch.getClientAnimator());
         if (animationPlayer != null && !animationPlayer.isEmpty()) {
            StaticAnimation animation = DMCAnimationUtils.getRealAnimation(animationPlayer);
            return DMCAnimationUtils.isAnimationType(animation, ActionAnimation.class) ? animation.getRegistryName() : null;
         } else {
            return null;
         }
      }
   }
}
