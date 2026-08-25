package com.dmc.invincible_dmc.capability.weapon;

import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import com.dmc.invincible_dmc.api.weapon.WeaponActionChainRegistry;
import com.dmc.invincible_dmc.capability.DMCPlayer;
import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerState;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerStateProvider;
import com.dmc.invincible_dmc.gameassets.DmcWeaponProfiles;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import java.util.HashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.AnimationBeginEvent;
import yesman.epicfight.world.entity.eventlistener.AnimationEndEvent;

public final class WeaponSwitchCoordinator {
   private WeaponSwitchCoordinator() {
   }

   public static boolean requestSwitch(ServerPlayer player, DmcWeaponType targetWeapon, boolean suppressEntryTransition) {
      if (player != null && targetWeapon != null && player.m_6084_() && !player.m_5833_() && DmcWeaponManager.isArsenalItem(player.m_21205_())) {
         ServerPlayerPatch playerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
         if (playerPatch != null && !playerPatch.isStunned() && DmcWeaponProfiles.get(targetWeapon) != null) {
            SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
            if (container != null && !container.isEmpty() && container.getSkill() instanceof VergilSkill vergilSkill) {
               YamatoPlayerState state = YamatoPlayerStateProvider.get(player);
               if (state != YamatoPlayerState.EMPTY && state.getActiveWeapon() != targetWeapon) {
                  state.setActiveWeapon(targetWeapon);
                  vergilSkill.switchComboRootPreservingAction(container);
                  DMCPlayer dmcPlayer = DMCPlayerCapabilityProvider.get(player);
                  WeaponActionSession actionSession = dmcPlayer.getActionSession();
                  ResourceLocation currentAction = getCurrentAction(playerPatch);
                  if (currentAction == null && actionSession == null) {
                     state.setRenderedWeapon(targetWeapon);
                     state.clearPendingRenderedWeapon();
                     state.clearLivingMotionRefreshPending();
                     refreshLivingMotion(playerPatch, state);
                  } else if (state.getRenderedWeapon() == targetWeapon) {
                     state.clearPendingRenderedWeapon();
                     state.clearLivingMotionRefreshPending();
                  } else {
                     state.queueRenderedWeapon(targetWeapon, currentAction, actionSession != null ? actionSession.sessionId() : 0L, suppressEntryTransition);
                  }

                  state.advanceWeaponSwitchSequence();
                  DmcWeaponManager.syncToTrackingAndSelf(player);
                  return true;
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static void onAnimationEnd(AnimationEndEvent event) {
      if (event.getPlayerPatch() instanceof ServerPlayerPatch playerPatch) {
         YamatoPlayerState state = YamatoPlayerStateProvider.get((Player)playerPatch.getOriginal());
         if (state != YamatoPlayerState.EMPTY) {
            if (state.getPendingRenderedWeapon() == null
               && state.isLivingMotionRefreshPending()
               && event.isEnd()
               && DMCAnimationUtils.isAnimationType(event.getAnimation(), ActionAnimation.class)) {
               refreshLivingMotion(playerPatch, state);
            }

            ResourceLocation pendingAnimation = state.getPendingRenderAnimation();
            if (state.getPendingRenderedWeapon() != null && event.isEnd() && DMCAnimationUtils.isAnimationType(event.getAnimation(), ActionAnimation.class)) {
               ResourceLocation endingAnimation = event.getAnimation().getRegistryName();
               WeaponActionSession actionSession = DMCPlayerCapabilityProvider.get((Player)playerPatch.getOriginal()).getActionSession();
               boolean sameAnimationBarrier = pendingAnimation != null
                  && (pendingAnimation.equals(endingAnimation) || WeaponActionChainRegistry.belongsToSameChain(pendingAnimation, endingAnimation));
               boolean sameSessionBarrier = actionSession != null
                  && actionSession.sessionId() == state.getPendingRenderBarrierSessionId()
                  && WeaponActionChainRegistry.matchesSession(endingAnimation, actionSession);
               if (!sameAnimationBarrier && !sameSessionBarrier
                  || !WeaponActionChainRegistry.isRegistered(endingAnimation)
                  || WeaponActionChainRegistry.isTerminal(endingAnimation, actionSession)) {
                  commitPendingPresentation(playerPatch, state, true);
               }
            }
         }
      }
   }

   public static void onAnimationBegin(AnimationBeginEvent event) {
      if (event.getPlayerPatch() instanceof ServerPlayerPatch playerPatch) {
         YamatoPlayerState state = YamatoPlayerStateProvider.get((Player)playerPatch.getOriginal());
         if (state != YamatoPlayerState.EMPTY) {
            StaticAnimation nextAnimation = event.getAnimation();
            if (state.getPendingRenderedWeapon() == null) {
               if (state.isLivingMotionRefreshPending() && !DMCAnimationUtils.isAnimationType(nextAnimation, ActionAnimation.class)) {
                  refreshLivingMotion(playerPatch, state);
               }
            } else {
               ResourceLocation pendingAnimation = state.getPendingRenderAnimation();
               DMCPlayer dmcPlayer = DMCPlayerCapabilityProvider.get((Player)playerPatch.getOriginal());
               WeaponActionSession actionSession = dmcPlayer.getActionSession();
               if (pendingAnimation != null
                  && DMCAnimationUtils.isAnimationType(nextAnimation, ActionAnimation.class)
                  && isDeferredContinuation(pendingAnimation, nextAnimation.getRegistryName())) {
                  boolean suppressEntryTransition = state.isPendingEntryTransitionSuppressed();
                  state.queueRenderedWeapon(
                     state.getPendingRenderedWeapon(), nextAnimation.getRegistryName(), state.getPendingRenderBarrierSessionId(), suppressEntryTransition
                  );
               } else if (DMCAnimationUtils.isAnimationType(nextAnimation, ActionAnimation.class)
                  && actionSession != null
                  && actionSession.sessionId() == state.getPendingRenderBarrierSessionId()
                  && WeaponActionChainRegistry.matchesSession(nextAnimation.getRegistryName(), actionSession)) {
                  boolean suppressEntryTransition = state.isPendingEntryTransitionSuppressed();
                  state.queueRenderedWeapon(
                     state.getPendingRenderedWeapon(), nextAnimation.getRegistryName(), actionSession.sessionId(), suppressEntryTransition
                  );
               } else {
                  commitPendingPresentation(playerPatch, state, !DMCAnimationUtils.isAnimationType(nextAnimation, ActionAnimation.class));
               }
            }
         }
      }
   }

   public static void flushPendingPresentation(ServerPlayerPatch playerPatch) {
      YamatoPlayerState state = YamatoPlayerStateProvider.get((Player)playerPatch.getOriginal());
      if (state != YamatoPlayerState.EMPTY && state.getPendingRenderedWeapon() != null) {
         commitPendingPresentation(playerPatch, state, true);
      }
   }

   public static boolean isDeferredContinuation(ResourceLocation currentAnimation, ResourceLocation nextAnimation) {
      return WeaponActionChainRegistry.belongsToSameChain(currentAnimation, nextAnimation);
   }

   private static void commitPendingPresentation(ServerPlayerPatch playerPatch, YamatoPlayerState state, boolean refreshLivingMotionImmediately) {
      DmcWeaponType pendingWeapon = state.getPendingRenderedWeapon();
      if (pendingWeapon != null) {
         state.setRenderedWeapon(pendingWeapon);
         state.clearPendingRenderedWeapon();
         state.advanceWeaponSwitchSequence();
         if (refreshLivingMotionImmediately) {
            refreshLivingMotion(playerPatch, state);
         } else {
            state.markLivingMotionRefreshPending();
         }

         DmcWeaponManager.syncToTrackingAndSelf((ServerPlayer)playerPatch.getOriginal());
      }
   }

   private static void refreshLivingMotion(ServerPlayerPatch playerPatch, YamatoPlayerState state) {
      state.clearLivingMotionRefreshPending();
      HashMap<LivingMotion, AnimationAccessor<? extends StaticAnimation>> livingMotions = new HashMap<>(
         playerPatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getLivingMotionModifier(playerPatch, InteractionHand.MAIN_HAND)
      );
      livingMotions.putAll(
         playerPatch.getAdvancedHoldingItemCapability(InteractionHand.OFF_HAND).getLivingMotionModifier(playerPatch, InteractionHand.OFF_HAND)
      );
      playerPatch.getAnimator().resetLivingAnimations();
      livingMotions.forEach(playerPatch.getAnimator()::addLivingAnimation);
   }

   @Nullable
   private static ResourceLocation getCurrentAction(ServerPlayerPatch playerPatch) {
      AnimationPlayer animationPlayer = DMCAnimationUtils.getMainPlayer(playerPatch);
      if (animationPlayer != null && !animationPlayer.isEmpty()) {
         StaticAnimation animation = DMCAnimationUtils.getRealAnimation(animationPlayer);
         return DMCAnimationUtils.isAnimationType(animation, ActionAnimation.class) ? animation.getRegistryName() : null;
      } else {
         return null;
      }
   }
}
