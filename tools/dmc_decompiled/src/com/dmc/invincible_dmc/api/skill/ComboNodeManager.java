package com.dmc.invincible_dmc.api.skill;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;

public class ComboNodeManager {
   private static final Map<Integer, ComboNode> NODES = new HashMap<>();
   private static final Map<String, ComboNode> NODES_BY_NAME = new HashMap<>();
   private static int currentId = 0;

   public static int getNodeSize() {
      return NODES.size();
   }

   public static ComboNode get(int id) {
      return NODES.get(id);
   }

   @Internal
   public static void assignId(ComboNode node) {
      if (!node.isAssigned()) {
         currentId++;
         NODES.put(currentId, node);
         node.assign(currentId);
      }
   }

   public static void assignName(ComboNode node, String name) {
      if (NODES_BY_NAME.containsKey(name)) {
         throw new IllegalStateException("Node name [" + name + "] is already exist!");
      } else {
         NODES_BY_NAME.put(name, node);
      }
   }

   public static ComboNode getNodesByName(String name) {
      return NODES_BY_NAME.get(name);
   }
}
