package com.dmc.invincible_dmc.client.input;

import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboType;
import java.util.List;
import javax.annotation.Nullable;

public final class ComboRoutePlanner {
   @Nullable
   public static ComboNode getNextNode(@Nullable ComboNode currentNode, ComboType type) {
      return currentNode == null ? null : currentNode.getNext(type);
   }

   @Nullable
   public static ComboRoutePlanner.ComboRoute routeIntent(@Nullable ComboNode currentNode, ComboIntentResolver.ComboInputIntent intent) {
      if (currentNode == null) {
         return null;
      } else {
         ComboNode next = getNextNode(currentNode, intent.type());
         return next == null
            ? null
            : new ComboRoutePlanner.ComboRoute(intent, currentNode, next, intent.directionMask(), intent.directionEvents(), intent.captureTick());
      }
   }

   public static record ComboRoute(
      ComboIntentResolver.ComboInputIntent intent,
      ComboNode currentNode,
      ComboNode nextNode,
      int directionMask,
      List<DirectionTracker.DirectionEvent> directionEvents,
      long captureTick
   ) {
   }
}
