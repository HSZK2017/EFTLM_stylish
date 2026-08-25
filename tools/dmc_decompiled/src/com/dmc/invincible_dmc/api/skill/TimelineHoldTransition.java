package com.dmc.invincible_dmc.api.skill;

import com.dmc.invincible_dmc.api.events.BaseEvent;
import com.dmc.invincible_dmc.api.events.TimePeriodEvent;
import com.dmc.invincible_dmc.api.events.TimeStampedEvent;
import com.dmc.invincible_dmc.client.input.PlayerInputState;
import com.dmc.invincible_dmc.skill.weapon_innate.ComboBasicAttack;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public final class TimelineHoldTransition {
   private final ComboNode targetNode;
   private final float transitionTime;
   private final int inputBit;
   private final int minimumHoldTicks;
   private final Map<UUID, TimelineHoldTransition.HoldState> holdStates = new ConcurrentHashMap<>();

   private TimelineHoldTransition(ComboNode targetNode, float transitionTime, int inputBit, int minimumHoldTicks) {
      this.targetNode = targetNode;
      this.transitionTime = transitionTime;
      this.inputBit = inputBit;
      this.minimumHoldTicks = minimumHoldTicks;
   }

   public static TimelineHoldTransition attach(ComboNode sourceNode, ComboNode targetNode, float transitionTime, int inputBit, int minimumHoldTicks) {
      Objects.requireNonNull(sourceNode, "sourceNode");
      Objects.requireNonNull(targetNode, "targetNode");
      if (transitionTime <= 0.0F) {
         throw new IllegalArgumentException("transitionTime must be greater than zero");
      } else if (minimumHoldTicks <= 0) {
         throw new IllegalArgumentException("minimumHoldTicks must be greater than zero");
      } else {
         TimelineHoldTransition transition = new TimelineHoldTransition(targetNode, transitionTime, inputBit, minimumHoldTicks);
         transition.bind(sourceNode);
         return transition;
      }
   }

   private void bind(ComboNode sourceNode) {
      sourceNode.addBeginEvent(
         BaseEvent.createServerEvent((playerPatch, target, dmcPlayer) -> this.holdStates.remove(((Player)playerPatch.getOriginal()).m_20148_()))
      );
      sourceNode.addTimePeriodEvent(new TimePeriodEvent(0.0F, this.transitionTime, (playerPatch, target, dmcPlayer) -> this.trackHold(playerPatch)));
      sourceNode.addTimeEvent(new TimeStampedEvent(this.transitionTime, (playerPatch, target, dmcPlayer) -> this.executeTransition(playerPatch)));
   }

   private void trackHold(PlayerPatch<?> playerPatch) {
      UUID playerId = ((Player)playerPatch.getOriginal()).m_20148_();
      long currentTick = ((Player)playerPatch.getOriginal()).m_9236_().m_46467_();
      if (!PlayerInputState.isRemoteDown((Player)playerPatch.getOriginal(), this.inputBit)) {
         this.holdStates.remove(playerId);
      } else {
         this.holdStates.compute(playerId, (id, previousState) -> {
            if (previousState != null && previousState.lastTick() == currentTick) {
               return (TimelineHoldTransition.HoldState)previousState;
            } else {
               int heldTicks = previousState != null && previousState.lastTick() == currentTick - 1L ? previousState.heldTicks() + 1 : 1;
               return new TimelineHoldTransition.HoldState(currentTick, heldTicks);
            }
         });
      }
   }

   private void executeTransition(PlayerPatch<?> playerPatch) {
      UUID playerId = ((Player)playerPatch.getOriginal()).m_20148_();
      TimelineHoldTransition.HoldState holdState = this.holdStates.remove(playerId);
      if (holdState != null && holdState.heldTicks() >= this.minimumHoldTicks) {
         long currentTick = ((Player)playerPatch.getOriginal()).m_9236_().m_46467_();
         boolean continuouslyHeld = currentTick - holdState.lastTick() <= 1L && PlayerInputState.isRemoteDown((Player)playerPatch.getOriginal(), this.inputBit);
         if (continuouslyHeld && playerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            ComboBasicAttack.executeNodeOnServer(serverPlayerPatch, this.targetNode, holdState.heldTicks(), 0L);
         }
      }
   }

   private static record HoldState(long lastTick, int heldTicks) {
   }
}
