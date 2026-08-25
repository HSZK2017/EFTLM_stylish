package com.dmc.invincible_dmc.skill.weapon_innate.vergil;

import com.dmc.invincible_dmc.api.skill.ActionTag;
import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboNodeManager;
import com.dmc.invincible_dmc.api.skill.JudgementCutNode;
import com.dmc.invincible_dmc.capability.DMCPlayer;
import com.dmc.invincible_dmc.conditions.GroundedCondition;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerScript;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerScriptCompiler;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.skill.SkillContainer;

public class DoppelgangerManager {
   private final DoppelgangerScriptCompiler scriptCompiler;
   private static final Map<UUID, ActionTag> LAST_COMPILED_TAG = new HashMap<>();

   public DoppelgangerManager(ComboNode rootNode) {
      this.scriptCompiler = new DoppelgangerScriptCompiler(rootNode);
   }

   private static ActionTag extractTagFromPath(Deque<Long> path) {
      Iterator<Long> it = path.descendingIterator();

      while (it.hasNext()) {
         ComboNode node = ComboNodeManager.get((int)it.next().longValue());
         if (node != null) {
            ActionTag tag = node.getActionTag();
            if (tag != ActionTag.NONE) {
               return tag;
            }
         }
      }

      return ActionTag.NONE;
   }

   public void checkDoppelDimension(SkillContainer container) {
      if (container.getExecutor().getOriginal() instanceof ServerPlayer sp) {
         UUID var9 = sp.m_20148_();

         for (ServerLevel level : sp.f_8924_.m_129785_()) {
            if (level != sp.m_284548_()) {
               for (Entity entity : level.m_142646_().m_142273_()) {
                  if (entity instanceof DoppelgangerEntity) {
                     DoppelgangerEntity doppel = (DoppelgangerEntity)entity;
                     if (var9.equals(doppel.getOwnerUUID()) && doppel.m_6084_()) {
                        DMCLog.info(DMCLog.Category.DOPPEL_GENERAL, "[Yamato] Doppel in other dimension — discarding. player={}", sp.m_7755_().getString());
                        DoppelgangerEntity.discardDoppelganger(doppel);
                     }
                  }
               }
            }
         }
      }
   }

   public void handleDoppelgangerMirror(ServerPlayer player, DMCPlayer ip, boolean isPerfect, int chainCount, boolean inAir, VergilSkill skill) {
      DoppelgangerPatch nearest = this.playJudgementCut(player, isPerfect, skill);
      if (nearest != null) {
         if (isPerfect && inAir) {
            nearest.aerialAttackCount = 0;
         }

         if (isPerfect) {
            UUID uuid = player.m_20148_();
            ActionTag extracted = extractTagFromPath(ip.getComboPath());
            ActionTag lastTag = extracted != ActionTag.NONE ? extracted : LAST_COMPILED_TAG.getOrDefault(uuid, ActionTag.NONE);
            LAST_COMPILED_TAG.put(uuid, lastTag);
            DMCLog.info(
               DMCLog.Category.DOPPEL_GENERAL, "[ScriptCompile] START — pathSize={} chainCount={} lastTag={}", ip.getComboPath().size(), chainCount, lastTag
            );
            DoppelgangerScript script = this.scriptCompiler.compile(ip.getComboPath(), chainCount, lastTag);
            if (script != null) {
               nearest.setRecordedScript(script);
               DMCLog.info(DMCLog.Category.DOPPEL_GENERAL, "[ScriptCompile] script stored into nearestDoppel");
            } else {
               DMCLog.warn(DMCLog.Category.DOPPEL_GENERAL, "[ScriptCompile] script is NULL — not stored");
            }
         }
      }
   }

   private DoppelgangerPatch playJudgementCut(ServerPlayer player, boolean isPerfect, VergilSkill skill) {
      DoppelgangerPatch nearest = DoppelgangerPatch.getNearestDoppelganger(player);
      if (nearest == null) {
         return null;
      } else {
         DoppelgangerEntity doppel = (DoppelgangerEntity)nearest.getOriginal();
         doppel.setCcMode(false);
         doppel.setCcNodeId(0);
         if (isPerfect) {
            DoppelgangerEntity.recallDoppelganger(doppel);
         }

         boolean doppelGrounded = doppel.m_20096_() || GroundedCondition.check(doppel);
         JudgementCutNode doppelTarget = skill.getTargetNode(isPerfect, !doppelGrounded);
         if (doppelTarget != null && doppelTarget.getAnimationAccessor() != null) {
            nearest.playForcedComboNode(doppelTarget);
            if (isPerfect && !doppelGrounded) {
               nearest.aerialAttackCount = 0;
            }

            return nearest;
         } else {
            return nearest;
         }
      }
   }
}
