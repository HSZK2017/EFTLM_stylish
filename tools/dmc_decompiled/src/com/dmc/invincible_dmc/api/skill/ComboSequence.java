package com.dmc.invincible_dmc.api.skill;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public class ComboSequence {
   private final ComboType key;
   private final List<ComboSequence.StepDef> steps = new ArrayList<>();
   private ComboNodeGroup commonGroup;
   private ComboNodeGroup flowGroup;

   private ComboSequence(ComboType key) {
      this.key = key;
   }

   public static ComboSequence linear(ComboType key) {
      return new ComboSequence(key);
   }

   public ComboSequence common(ComboNode first, ComboNode... rest) {
      this.commonGroup = ComboNodeGroup.creatGroup(first, rest);
      return this;
   }

   public ComboSequence flow(ComboNode first, ComboNode... rest) {
      this.flowGroup = ComboNodeGroup.creatGroup(first, rest);
      return this;
   }

   public ComboSequence step(ComboNode main) {
      this.steps.add(new ComboSequence.StepDef(main, null, false));
      return this;
   }

   public ComboSequence step(ComboNode main, @Nullable ComboNode heavyVariant) {
      this.steps.add(new ComboSequence.StepDef(main, heavyVariant, false));
      return this;
   }

   public ComboSequence step(ComboNode main, @Nullable ComboNode heavyVariant, boolean heavyResets) {
      this.steps.add(new ComboSequence.StepDef(main, heavyVariant, heavyResets));
      return this;
   }

   public ComboNodeGroup build(ComboNode root) {
      int n = this.steps.size();
      if (n != 0 && this.commonGroup != null) {
         ComboNodeGroup[] stepNodes = new ComboNodeGroup[n];
         ComboNode[] mains = new ComboNode[n];
         ComboNode[] heavies = new ComboNode[n];
         boolean[] heavyResets = new boolean[n];

         for (int i = 0; i < n; i++) {
            ComboSequence.StepDef def = this.steps.get(i);
            mains[i] = def.main;
            heavies[i] = def.heavyVariant;
            heavyResets[i] = def.heavyResets;
            stepNodes[i] = heavies[i] != null ? this.commonGroup.step(mains[i], heavies[i]) : this.commonGroup.step(mains[i]);
         }

         if (this.flowGroup != null) {
            for (int i = 0; i < n; i++) {
               for (ComboNode leaf : this.flowGroup.conditionNodes) {
                  ComboNode clone = leaf.copyForBranching();
                  clone.root = root;
                  clone.addChild(this.key, this.commonGroup.step(mains[i]));
                  stepNodes[i].addConditionNode(clone);
               }
            }
         }

         root.addChild(this.key, stepNodes[0]);
         stepNodes[0].fanIn(this.key, new ComboNode[]{this.commonGroup});
         Set<ComboNode> wiredMains = new HashSet<>();

         for (int i = 0; i < n; i++) {
            if (wiredMains.add(mains[i])) {
               mains[i].addChild(this.key, stepNodes[(i + 1) % n]);
            }
         }

         for (int ix = 0; ix < n; ix++) {
            if (heavies[ix] != null) {
               ComboNode target = ix != 0 && !heavyResets[ix] ? this.commonGroup.step(mains[ix]) : stepNodes[0];
               if (heavies[ix] instanceof ComboNodeGroup) {
                  for (ComboNode leaf : heavies[ix].conditionNodes) {
                     ComboNode clone = leaf.copyForBranching();
                     clone.root = root;
                     clone.addChild(this.key, target);
                     stepNodes[ix].addConditionNode(clone);
                  }
               } else {
                  heavies[ix].addChild(this.key, target);
               }
            }
         }

         return stepNodes[0];
      } else {
         return null;
      }
   }

   private static class StepDef {
      final ComboNode main;
      @Nullable
      final ComboNode heavyVariant;
      final boolean heavyResets;

      StepDef(ComboNode main, @Nullable ComboNode heavyVariant, boolean heavyResets) {
         this.main = main;
         this.heavyVariant = heavyVariant;
         this.heavyResets = heavyResets;
      }
   }
}
