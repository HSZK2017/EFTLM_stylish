package com.dmc.invincible_dmc.api.skill;

public class ComboNodeGroup extends ComboNode {
   public static ComboNodeGroup creatGroup(ComboNode first, ComboNode... rest) {
      ComboNodeGroup node = new ComboNodeGroup();
      addOrExpand(node, first);

      for (ComboNode n : rest) {
         addOrExpand(node, n);
      }

      return node;
   }

   private static void addOrExpand(ComboNodeGroup target, ComboNode node) {
      if (node instanceof ComboNodeGroup) {
         for (ComboNode child : node.conditionNodes) {
            target.addConditionNode(child);
         }
      } else {
         target.addConditionNode(node);
      }
   }

   public ComboNodeGroup step(ComboNode main, ComboNode... extras) {
      ComboNodeGroup node = creatGroup(main, extras);

      for (ComboNode n : this.conditionNodes) {
         node.addConditionNode(n);
      }

      return node;
   }
}
