package com.dmc.invincible_dmc.entity.doppelganger;

import com.dmc.invincible_dmc.api.skill.ComboType;
import com.dmc.invincible_dmc.client.input.DirectionTracker;
import java.util.List;

public record DoppelgangerInputEvent(
   long scheduledTick,
   ComboType type,
   long engineTick,
   int intendedDelayTicks,
   int pressDuration,
   long pressIntervalMs,
   boolean isLongPress,
   int directionMask,
   List<DirectionTracker.DirectionEvent> directionEvents,
   boolean holdingUp,
   boolean holdingDown,
   boolean holdingLeft,
   boolean holdingRight,
   boolean holdingJump,
   boolean holdingSprint,
   boolean holdingSneak,
   boolean holdingLockOn,
   int dodgeSuccessTimer,
   int parryTimer,
   int cooldownTimer,
   int skillStack,
   int playerPhase,
   boolean sdtActive
) implements Comparable<DoppelgangerInputEvent> {
   public int compareTo(DoppelgangerInputEvent o) {
      return Long.compare(this.scheduledTick, o.scheduledTick);
   }
}
