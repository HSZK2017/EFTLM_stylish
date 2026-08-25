package com.dmc.invincible_dmc.entity.doppelganger;

import com.dmc.invincible_dmc.api.skill.ActionTag;
import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboNodeManager;
import com.dmc.invincible_dmc.api.skill.ComboType;
import com.dmc.invincible_dmc.api.skill.ICrazyComboNode;
import com.dmc.invincible_dmc.api.skill.IHitExtendNode;
import com.dmc.invincible_dmc.api.skill.ITapHoldNode;
import com.dmc.invincible_dmc.api.skill.SubComboNode;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DoppelgangerScriptCompiler {
   private final Map<ActionTag, ComboNode> tagIndex = new EnumMap<>(ActionTag.class);

   public DoppelgangerScriptCompiler(ComboNode root) {
      HashSet<ComboNode> visited = new HashSet<>();
      this.buildIndex(root, visited);
      this.scanSubNodes(root, new HashSet<>());
   }

   public DoppelgangerScript compile(Deque<Long> comboPath, int jcChainCount, ActionTag fallbackTag) {
      DMCLog.info(
         DMCLog.Category.DOPPEL_GENERAL, "[ScriptCompiler] compile() pathSize={} jcChainCount={} fallbackTag={}", comboPath.size(), jcChainCount, fallbackTag
      );
      ActionTag matchedTag = ActionTag.NONE;
      ComboNode matchedNode = null;
      if (!comboPath.isEmpty()) {
         List<Long> reversed = new ArrayList<>(comboPath);
         Collections.reverse(reversed);
         DMCLog.info(DMCLog.Category.DOPPEL_GENERAL, "[ScriptCompiler] reversed path={}", reversed);

         for (long nodeId : reversed) {
            ComboNode node = ComboNodeManager.get((int)nodeId);
            if (node != null) {
               ActionTag tag = node.getActionTag();
               if (tag != ActionTag.NONE) {
                  matchedTag = tag;
                  matchedNode = node;
                  break;
               }
            }
         }
      }

      if (matchedTag == ActionTag.NONE && fallbackTag != ActionTag.NONE) {
         DMCLog.info(DMCLog.Category.DOPPEL_GENERAL, "[ScriptCompiler] FALLBACK to lastTag={}", fallbackTag);
         matchedTag = fallbackTag;
      }

      if (matchedTag == ActionTag.NONE) {
         DMCLog.warn(DMCLog.Category.DOPPEL_GENERAL, "[ScriptCompiler] NO tag matched → return null");
         return null;
      } else {
         DMCLog.info(DMCLog.Category.DOPPEL_GENERAL, "[ScriptCompiler] MATCHED tag={} → routing", matchedTag);

         return switch (matchedTag) {
            case RAPID_SLASH, VOID_SLASH, AERIAL_CLEAVE -> this.buildSpecial(matchedTag, matchedNode);
            case COMBO_B_1 -> this.buildComboB();
            case AERIAL_RAVE_B1 -> this.buildAerialComboB1();
            case AERIAL_RAVE_B2 -> this.buildAerialComboB2();
            case AERIAL_RAVE_A3 -> this.buildAerialComboA();
            case COMBO_B_2_SDT -> this.buildComboBSdt();
            case COMBO_C_BASE, COMBO_C_CHASE, COMBO_C_FINISH_NO_CHASE, COMBO_C_FINISH -> this.buildComboC(jcChainCount);
            case COMBO_A_4 -> this.buildComboAEndNormal(jcChainCount);
            case COMBO_A_5_SDT -> this.buildComboAEndSDT();
            default -> null;
         };
      }
   }

   private long id(ActionTag tag) {
      ComboNode n = this.tagIndex.get(tag);
      return n != null ? (long)n.getId() : -1L;
   }

   private ScriptInstruction.PlayNode play(ActionTag tag) {
      return new ScriptInstruction.PlayNode(this.id(tag), 0);
   }

   private DoppelgangerScript buildSpecial(ActionTag tag, ComboNode node) {
      return tag == ActionTag.AERIAL_CLEAVE
         ? DoppelgangerScript.create().ground(this.play(ActionTag.UPPER_SLASH_TAP)).air(this.play(ActionTag.AERIAL_CLEAVE)).build()
         : DoppelgangerScript.create().ground(new ScriptInstruction.PlayNode((long)node.getId(), 0)).air(this.play(ActionTag.AERIAL_CLEAVE)).build();
   }

   private DoppelgangerScript buildComboB() {
      return DoppelgangerScript.create()
         .ground(this.play(ActionTag.COMBO_A_1), this.play(ActionTag.COMBO_A_2), this.play(ActionTag.COMBO_B_1))
         .air(this.play(ActionTag.AERIAL_RAVE_A1), this.play(ActionTag.AERIAL_RAVE_A2), this.play(ActionTag.AERIAL_RAVE_A3))
         .build();
   }

   private DoppelgangerScript buildComboBSdt() {
      return DoppelgangerScript.create()
         .ground(this.play(ActionTag.COMBO_A_1), this.play(ActionTag.COMBO_A_2), this.play(ActionTag.COMBO_B_1), this.play(ActionTag.COMBO_B_2_SDT))
         .air(this.play(ActionTag.AERIAL_RAVE_A1), this.play(ActionTag.AERIAL_RAVE_A2), this.play(ActionTag.AERIAL_RAVE_A3))
         .build();
   }

   private DoppelgangerScript buildAerialComboB1() {
      return DoppelgangerScript.create()
         .ground(this.play(ActionTag.COMBO_A_1), this.play(ActionTag.COMBO_A_2), this.play(ActionTag.COMBO_B_1))
         .air(this.play(ActionTag.AERIAL_RAVE_A1), this.play(ActionTag.AERIAL_RAVE_A2), this.play(ActionTag.AERIAL_RAVE_B1))
         .build();
   }

   private DoppelgangerScript buildAerialComboB2() {
      return DoppelgangerScript.create()
         .ground(
            this.play(ActionTag.COMBO_A_1),
            this.play(ActionTag.COMBO_A_2),
            this.play(ActionTag.COMBO_A_3),
            this.play(ActionTag.COMBO_C_BASE),
            new ScriptInstruction.LoopStart(1),
            this.play(ActionTag.COMBO_C_CHASE),
            new ScriptInstruction.LoopEnd(),
            this.play(ActionTag.COMBO_C_FINISH)
         )
         .air(
            this.play(ActionTag.AERIAL_RAVE_A1), this.play(ActionTag.AERIAL_RAVE_A2), this.play(ActionTag.AERIAL_RAVE_B1), this.play(ActionTag.AERIAL_RAVE_B2)
         )
         .build();
   }

   private DoppelgangerScript buildComboC(int jcCount) {
      int n = Math.max(1, Math.min(jcCount, 8));
      return DoppelgangerScript.create()
         .ground(
            this.play(ActionTag.COMBO_A_1),
            this.play(ActionTag.COMBO_A_2),
            this.play(ActionTag.COMBO_A_3),
            this.play(ActionTag.COMBO_C_BASE),
            new ScriptInstruction.LoopStart(n),
            this.play(ActionTag.COMBO_C_CHASE),
            new ScriptInstruction.LoopEnd(),
            this.play(ActionTag.COMBO_C_FINISH)
         )
         .air(
            this.play(ActionTag.AERIAL_RAVE_A1),
            this.play(ActionTag.AERIAL_RAVE_A2),
            this.play(ActionTag.AERIAL_RAVE_B1),
            this.play(ActionTag.AERIAL_RAVE_B2),
            new ScriptInstruction.LoopEnd()
         )
         .build();
   }

   private DoppelgangerScript buildComboAEndNormal(int jcCount) {
      return jcCount <= 1
         ? DoppelgangerScript.create()
            .ground(this.play(ActionTag.COMBO_A_1), this.play(ActionTag.COMBO_A_2), this.play(ActionTag.COMBO_A_3), this.play(ActionTag.COMBO_A_4))
            .air(this.play(ActionTag.AERIAL_CLEAVE), this.play(ActionTag.VOID_SLASH))
            .build()
         : DoppelgangerScript.create()
            .ground(
               this.play(ActionTag.COMBO_A_1),
               this.play(ActionTag.COMBO_A_2),
               this.play(ActionTag.COMBO_A_3),
               this.play(ActionTag.COMBO_A_4_SDT),
               this.play(ActionTag.COMBO_A_5_SDT)
            )
            .air(this.play(ActionTag.AERIAL_CLEAVE), this.play(ActionTag.VOID_SLASH))
            .build();
   }

   private DoppelgangerScript buildAerialComboA() {
      return DoppelgangerScript.create()
         .ground(this.play(ActionTag.COMBO_A_1), this.play(ActionTag.COMBO_A_2), this.play(ActionTag.COMBO_B_1))
         .air(this.play(ActionTag.AERIAL_RAVE_A1), this.play(ActionTag.AERIAL_RAVE_A2), this.play(ActionTag.AERIAL_RAVE_A3))
         .build();
   }

   private DoppelgangerScript buildComboAEndSDT() {
      return DoppelgangerScript.create()
         .ground(
            this.play(ActionTag.COMBO_A_1),
            this.play(ActionTag.COMBO_A_2),
            this.play(ActionTag.COMBO_A_3),
            this.play(ActionTag.COMBO_A_4_SDT),
            this.play(ActionTag.COMBO_A_5_SDT)
         )
         .air(this.play(ActionTag.AERIAL_CLEAVE), this.play(ActionTag.VOID_SLASH))
         .build();
   }

   private void buildIndex(ComboNode node, Set<ComboNode> visited) {
      if (node != null && visited.add(node)) {
         ActionTag tag = node.getActionTag();
         if (tag != ActionTag.NONE) {
            this.tagIndex.put(tag, node);
         }

         for (ComboType type : ComboType.ENUM_MANAGER.universalValues()) {
            this.buildIndex(node.getNext(type), visited);
         }

         for (ComboNode cond : node.getConditionNodes()) {
            this.buildIndex(cond, visited);
         }

         if (node instanceof ICrazyComboNode cc) {
            this.buildIndex(cc.getCcFinish(), visited);
            this.buildIndex(cc.getCcFinishNoChase(), visited);
         }
      }
   }

   private void scanSubNodes(ComboNode node, Set<ComboNode> visited) {
      if (node != null && visited.add(node)) {
         if (node instanceof SubComboNode sub) {
            this.registerSub(sub);
         }

         if (node instanceof IHitExtendNode he) {
            this.registerSub(he.getBase());
            this.registerSub(he.getExtend());
         }

         if (node instanceof ITapHoldNode th) {
            this.registerSub(th.getTap());
            this.registerSub(th.getHold());
         }

         if (node instanceof ICrazyComboNode cc) {
            this.registerSub(cc.getCcBase());
            this.registerSub(cc.getCcChase());
            this.scanSubNodes(cc.getCcFinish(), visited);
            this.scanSubNodes(cc.getCcFinishNoChase(), visited);
         }

         for (ComboType type : ComboType.ENUM_MANAGER.universalValues()) {
            this.scanSubNodes(node.getNext(type), visited);
         }

         for (ComboNode cond : node.getConditionNodes()) {
            this.scanSubNodes(cond, visited);
         }
      }
   }

   private void registerSub(SubComboNode sub) {
      if (sub != null) {
         ActionTag tag = sub.getActionTag();
         if (tag != ActionTag.NONE) {
            this.tagIndex.put(tag, sub);
         }
      }
   }
}
