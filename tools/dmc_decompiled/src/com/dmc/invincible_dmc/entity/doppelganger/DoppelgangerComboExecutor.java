package com.dmc.invincible_dmc.entity.doppelganger;

import com.dmc.invincible_dmc.api.events.Side;
import com.dmc.invincible_dmc.api.skill.ActionTag;
import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.CrazyComboNode;
import com.dmc.invincible_dmc.api.skill.HitExtendNode;
import com.dmc.invincible_dmc.api.skill.IHitExtendNode;
import com.dmc.invincible_dmc.api.skill.SubComboNode;
import com.dmc.invincible_dmc.api.skill.TapHoldNode;
import com.dmc.invincible_dmc.client.input.ComboRoutePlanner;
import com.dmc.invincible_dmc.conditions.AerialAttackLimitCondition;
import com.dmc.invincible_dmc.conditions.AirborneCondition;
import com.dmc.invincible_dmc.conditions.AnimationElapsedTimeCondition;
import com.dmc.invincible_dmc.conditions.BlockingCondition;
import com.dmc.invincible_dmc.conditions.ComboInterruptWindowCondition;
import com.dmc.invincible_dmc.conditions.CooldownCondition;
import com.dmc.invincible_dmc.conditions.DirectionCondition;
import com.dmc.invincible_dmc.conditions.DirectionalSequenceCondition;
import com.dmc.invincible_dmc.conditions.DodgeSuccessCondition;
import com.dmc.invincible_dmc.conditions.EnchantmentCondition;
import com.dmc.invincible_dmc.conditions.GroundedCondition;
import com.dmc.invincible_dmc.conditions.InTargetPovCondition;
import com.dmc.invincible_dmc.conditions.JumpCondition;
import com.dmc.invincible_dmc.conditions.JumpKeyCondition;
import com.dmc.invincible_dmc.conditions.LockonKeyCondition;
import com.dmc.invincible_dmc.conditions.LongPressCondition;
import com.dmc.invincible_dmc.conditions.MobEffectCondition;
import com.dmc.invincible_dmc.conditions.ParrySuccessCondition;
import com.dmc.invincible_dmc.conditions.PlayerOnlyCondition;
import com.dmc.invincible_dmc.conditions.PlayerPhaseCondition;
import com.dmc.invincible_dmc.conditions.PovTargetPovAngle;
import com.dmc.invincible_dmc.conditions.PressIntervalCondition;
import com.dmc.invincible_dmc.conditions.PressedTimeCondition;
import com.dmc.invincible_dmc.conditions.SDTCondition;
import com.dmc.invincible_dmc.conditions.SneakKeyCondition;
import com.dmc.invincible_dmc.conditions.SprintKeyCondition;
import com.dmc.invincible_dmc.conditions.SprintingCondition;
import com.dmc.invincible_dmc.conditions.StackCondition;
import com.dmc.invincible_dmc.conditions.TargetBlockingCondition;
import com.dmc.invincible_dmc.skill.weapon_combo.Yamato;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.Comparator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class DoppelgangerComboExecutor {
   private final DoppelgangerPatch patch;
   private final DoppelgangerComboState state;
   private final ComboNode root;
   private final DoppelgangerMirrorController mirrorController;
   private boolean aerialPending = false;

   public DoppelgangerComboExecutor(DoppelgangerPatch patch, ComboNode root) {
      this.patch = patch;
      this.state = patch.comboState;
      this.root = root;
      this.mirrorController = new DoppelgangerMirrorController(patch);
   }

   ComboNode getRoot() {
      return this.root;
   }

   public boolean tryExecute(DoppelgangerInputEvent event) {
      DoppelgangerEntity doppel = (DoppelgangerEntity)this.patch.getOriginal();
      this.mirrorController.refresh();
      this.aerialPending = false;
      ComboNode last = this.state.getCurrentLogicNode();
      if (last == null) {
         last = this.root;
         this.state.setCurrentLogicNode(last);
         this.state.setDefaultResetTicks(this.findResetTime());
         this.state.setLastInputEngineTick(event.scheduledTick());
      }

      ComboNode current = ComboRoutePlanner.getNextNode(last, event.type());
      if (current == null) {
         return false;
      } else {
         long effectiveInterval = event.pressIntervalMs();
         if (doppel.getDoppelDelayMode() != 0) {
            long lastTick = this.state.getLastInputEngineTick();
            if (lastTick >= 0L && event.scheduledTick() > lastTick) {
               effectiveInterval = (event.scheduledTick() - lastTick) * 50L;
            }
         }

         int keyIndex = event.type().universalOrdinal() <= 3 ? event.type().universalOrdinal() : 4;
         this.state.setComboKeyIndex(keyIndex);
         ComboNode next = this.resolveConditionNodes(current, event, effectiveInterval);
         if (next == null) {
            DMCLog.info(
               DMCLog.Category.DOPPEL_COMBO,
               "[DoppelSvr] NODE REJECTED doppel={} from={} type={} interval={}",
               doppel.m_7755_().getString(),
               current.getId(),
               event.type().universalOrdinal(),
               effectiveInterval
            );
            return false;
         } else if (!this.checkNodeConditions(next, event, effectiveInterval)) {
            DMCLog.info(DMCLog.Category.DOPPEL_COMBO, "[DoppelSvr] COND FAIL doppel={} node={}", doppel.m_7755_().getString(), next.getId());
            return false;
         } else {
            boolean played;
            if (next instanceof CrazyComboNode cc) {
               played = this.playCrazyCombo(cc);
            } else if (next instanceof TapHoldNode th) {
               played = this.playTapHold(th);
            } else if (next instanceof HitExtendNode he) {
               played = this.playHitExtend(he);
            } else {
               played = this.playNormal(next);
            }

            if (played) {
               this.state.setLastExecutionTick((long)doppel.f_19797_);
               this.patch.resetActionTick();
               this.state.setLastInputEngineTick(event.scheduledTick());
               this.state.addProcessedEngineTick(event.engineTick());
               if (this.isGrounded(doppel)) {
                  this.patch.aerialAttackCount = 0;
               }

               if (this.aerialPending && !doppel.m_20096_()) {
                  this.patch.aerialAttackCount++;
               }

               DMCLog.info(
                  DMCLog.Category.DOPPEL_COMBO,
                  "[DoppelSvr] EXEC doppel={} node={} scheduledTick={} interval={}",
                  doppel.m_7755_().getString(),
                  next.getId(),
                  event.scheduledTick(),
                  effectiveInterval
               );
            }

            return played;
         }
      }
   }

   public boolean tryExecuteNode(ComboNode node, DoppelgangerInputEvent event) {
      this.mirrorController.refresh();
      this.aerialPending = false;
      long effectiveInterval = event.pressIntervalMs();
      ComboNode resolved = this.resolveConditionNodes(node, event, effectiveInterval);
      if (resolved != null && this.checkNodeConditions(resolved, event, effectiveInterval)) {
         boolean played = resolved instanceof TapHoldNode tapHoldNode
            ? this.playTapHold(tapHoldNode)
            : (
               resolved instanceof HitExtendNode hitExtendNode
                  ? this.playHitExtend(hitExtendNode)
                  : (resolved instanceof CrazyComboNode crazyComboNode ? this.playCrazyCombo(crazyComboNode) : this.playNormal(resolved))
            );
         if (played) {
            this.state.setLastExecutionTick((long)((DoppelgangerEntity)this.patch.getOriginal()).f_19797_);
            this.patch.resetActionTick();
         }

         return played;
      } else {
         return false;
      }
   }

   @Nullable
   private ComboNode resolveConditionNodes(ComboNode current, DoppelgangerInputEvent event, long effectiveInterval) {
      boolean sdtRapidSlashLoopWindow = Yamato.isSdtRapidSlashLoopWindow(this.patch);
      if (current.getAnimationAccessor() != null && current.getConditionNodes().isEmpty()) {
         return sdtRapidSlashLoopWindow && !Yamato.isRapidSlashNode(current) ? null : current;
      } else {
         if (!current.getConditionNodes().isEmpty()) {
            current.getConditionNodes().sort(Comparator.comparingInt(ComboNode::getPriority).reversed());

            for (ComboNode condNode : current.getConditionNodes()) {
               if ((!sdtRapidSlashLoopWindow || Yamato.isRapidSlashNode(condNode)) && this.checkNodeConditions(condNode, event, effectiveInterval)) {
                  DMCLog.info(
                     DMCLog.Category.DOPPEL_COMBO,
                     "[DoppelSvr] COND MATCH doppel={} node={} parent={}",
                     ((DoppelgangerEntity)this.patch.getOriginal()).m_7755_().getString(),
                     condNode.getId(),
                     current.getId()
                  );
                  return condNode;
               }
            }
         }

         if (current.getAnimationAccessor() == null) {
            return null;
         } else {
            return sdtRapidSlashLoopWindow && !Yamato.isRapidSlashNode(current) ? null : current;
         }
      }
   }

   private boolean checkNodeConditions(ComboNode node, DoppelgangerInputEvent event, long effectiveInterval) {
      boolean sdtRapidSlashLoop = Yamato.isSdtRapidSlashLoopWindow(this.patch) && Yamato.isRapidSlashNode(node);

      for (Condition condition : node.getConditions(Side.SERVER, Side.BOTH)) {
         if (sdtRapidSlashLoop
            && condition instanceof DirectionCondition directionCondition
            && directionCondition.getDirection() == DirectionCondition.Direction.UP) {
            if (!this.mirrorController.isHoldingDirection(event, DirectionCondition.Direction.UP)) {
               return false;
            }
            continue;
         }

         if (!this.evaluateSingleCondition(condition, event, effectiveInterval)) {
            return false;
         }
      }

      return true;
   }

   private boolean evaluateSingleCondition(Condition condition, DoppelgangerInputEvent event, long effectiveInterval) {
      DoppelgangerEntity doppel = (DoppelgangerEntity)this.patch.getOriginal();
      boolean result;
      String detail;
      if (condition instanceof PressedTimeCondition ptc) {
         result = event.isLongPress() && event.pressDuration() >= ptc.getMin() && event.pressDuration() <= ptc.getMax();
         detail = String.format("pressedTime=%d [%d,%d] isLP=%b", event.pressDuration(), ptc.getMin(), ptc.getMax(), event.isLongPress());
      } else if (condition instanceof LongPressCondition) {
         result = event.isLongPress();
         detail = String.format("isLP=%b", event.isLongPress());
      } else if (condition instanceof PressIntervalCondition pic) {
         result = effectiveInterval >= pic.getMin() && effectiveInterval <= pic.getMax();
         detail = String.format("interval=%d [%d,%d]", effectiveInterval, pic.getMin(), pic.getMax());
      } else if (condition instanceof DirectionalSequenceCondition dsc) {
         DirectionalSequenceCondition.Sequence effectiveSequence = this.mirrorController.resolveSequence(dsc.getSequence());
         result = DirectionalSequenceCondition.check(
            new DirectionalSequenceCondition(effectiveSequence, dsc.getMatchWindowTicks(), dsc.getActivationWindowTicks()),
            event.directionMask(),
            event.directionEvents(),
            event.engineTick()
         );
         detail = String.format("mask=%d dirEvtCnt=%d eng=%d", event.directionMask(), event.directionEvents().size(), event.engineTick());
      } else if (condition instanceof GroundedCondition) {
         result = this.isGrounded(doppel);
         detail = String.format("grounded=%b", result);
      } else if (condition instanceof AirborneCondition ac) {
         if (this.isGrounded(doppel) && !doppel.m_20069_()) {
            result = false;
         } else {
            result = true;
         }

         detail = String.format("grounded=%b", this.isGrounded(doppel));
      } else if (condition instanceof SprintingCondition) {
         result = doppel.m_20142_();
         detail = String.format("sprinting=%b", doppel.m_20142_());
      } else if (condition instanceof JumpCondition) {
         result = !doppel.m_20096_() && !doppel.m_20069_() && doppel.m_20184_().f_82480_ > 0.05;
         detail = String.format("grounded=%b dy=%.3f", doppel.m_20096_(), doppel.m_20184_().f_82480_);
      } else if (condition instanceof DirectionCondition dc) {
         DirectionCondition.Direction dir = dc.getDirection();
         result = dir != null && this.mirrorController.isHoldingDirection(event, dir);
         detail = String.format("dir=%s U=%b D=%b L=%b R=%b", dir, event.holdingUp(), event.holdingDown(), event.holdingLeft(), event.holdingRight());
      } else if (condition instanceof JumpKeyCondition) {
         result = event.holdingJump();
         detail = String.format("jump=%b", event.holdingJump());
      } else if (condition instanceof SprintKeyCondition) {
         result = event.holdingSprint();
         detail = String.format("sprint=%b", event.holdingSprint());
      } else if (condition instanceof SneakKeyCondition) {
         result = event.holdingSneak();
         detail = String.format("sneak=%b", event.holdingSneak());
      } else if (condition instanceof LockonKeyCondition) {
         result = event.holdingLockOn();
         detail = String.format("lockon=%b", event.holdingLockOn());
      } else if (condition instanceof DodgeSuccessCondition) {
         result = event.dodgeSuccessTimer() > 0;
         detail = String.format("dodgeTimer=%d", event.dodgeSuccessTimer());
      } else if (condition instanceof ParrySuccessCondition) {
         result = event.parryTimer() > 0;
         detail = String.format("parryTimer=%d", event.parryTimer());
      } else if (condition instanceof CooldownCondition cc) {
         boolean inCooldown = event.cooldownTimer() > 0;
         result = cc.isInCooldown() == inCooldown;
         detail = String.format("cooldown=%d expectedInCooldown=%b", event.cooldownTimer(), cc.isInCooldown());
      } else if (condition instanceof AerialAttackLimitCondition alc) {
         result = doppel.m_20096_() || this.patch.aerialAttackCount < alc.getMaxCount();
         if (result && !doppel.m_20096_()) {
            this.aerialPending = true;
         }

         detail = String.format("aerialCnt=%d max=%d", this.patch.aerialAttackCount, alc.getMaxCount());
      } else if (condition instanceof StackCondition sc) {
         result = event.skillStack() >= sc.getMin() && event.skillStack() <= sc.getMax();
         detail = String.format("stack=%d [%d,%d]", event.skillStack(), sc.getMin(), sc.getMax());
      } else if (condition instanceof PlayerPhaseCondition ppc) {
         result = event.playerPhase() >= ppc.getMin() && event.playerPhase() <= ppc.getMax();
         detail = String.format("phase=%d [%d,%d]", event.playerPhase(), ppc.getMin(), ppc.getMax());
      } else if (condition instanceof AnimationElapsedTimeCondition aetc) {
         result = AnimationElapsedTimeCondition.check(this.patch, aetc.getMinTime(), aetc.getMaxTime());
         detail = String.format("[%.2f,%.2f]", aetc.getMinTime(), aetc.getMaxTime());
      } else if (condition instanceof BlockingCondition) {
         result = false;
         detail = "blocking(false)";
      } else if (condition instanceof EnchantmentCondition) {
         result = true;
         detail = "enchantment(true)";
      } else if (condition instanceof MobEffectCondition) {
         result = true;
         detail = "mobEffect(true)";
      } else if (condition instanceof TargetBlockingCondition) {
         result = false;
         detail = "targetBlocking(false)";
      } else if (condition instanceof PlayerOnlyCondition) {
         result = false;
         detail = "playerOnly(false)";
      } else if (condition instanceof SDTCondition sdtc) {
         result = event.sdtActive() == sdtc.isWanted();
         detail = String.format("sdtActive=%b wanted=%b", event.sdtActive(), sdtc.isWanted());
      } else if (condition instanceof InTargetPovCondition) {
         result = true;
         detail = "inTargetPov(true)";
      } else if (condition instanceof PovTargetPovAngle) {
         result = true;
         detail = "povAngle(true)";
      } else {
         if (!(condition instanceof ComboInterruptWindowCondition)) {
            return true;
         }

         result = ComboInterruptWindowCondition.check(this.patch);
         detail = "interruptWindow";
      }

      DMCLog.info(
         DMCLog.Category.DOPPEL_COMBO,
         "[DoppelSvr] COND {} {} doppel={}: {}",
         result ? "PASS" : "FAIL",
         condition.getClass().getSimpleName(),
         doppel.m_7755_().getString(),
         detail
      );
      return result;
   }

   private boolean playCrazyCombo(CrazyComboNode cc) {
      SubComboNode ccBase = cc.getCcBase();
      if (ccBase != null && ccBase.getAnimationAccessor() != null) {
         DoppelgangerEntity doppel = (DoppelgangerEntity)this.patch.getOriginal();
         if (ccBase.getActionTag() == ActionTag.COMBO_C_BASE) {
            this.alignToCrazyComboTarget(doppel);
         }

         this.patch.playAnimationSynchronized(ccBase.getAnimationAccessor(), ccBase.getConvertTime());
         doppel.setCcMode(true);
         doppel.setCcNodeId(cc.getId());
         this.state.setCurrentLogicNode(cc);
         this.state.setCurrentDataNode(ccBase);
         this.state.setComboResetTicks(this.findResetTicks(cc));
         this.patch.updateInputBufferTtl(ccBase.getBufferDurationTicks());
         return true;
      } else {
         return false;
      }
   }

   private void alignToCrazyComboTarget(DoppelgangerEntity doppel) {
      PlayerPatch<?> ownerPatch = this.patch.getOwnerPatch();
      LivingEntity target = ownerPatch != null ? ownerPatch.getTarget() : null;
      if (target == null || !target.m_6084_()) {
         target = this.patch.getTarget();
      }

      if (target != null && target.m_6084_()) {
         Vec3 toTarget = target.m_20182_().m_82546_(doppel.m_20182_()).m_82542_(1.0, 0.0, 1.0);
         if (!(toTarget.m_82556_() < 1.0E-6)) {
            float targetYaw = Mth.m_14177_((float)MathUtils.getYRotOfVector(toTarget));
            doppel.m_146922_(targetYaw);
            doppel.f_19859_ = targetYaw;
            doppel.m_5618_(targetYaw);
            doppel.f_20884_ = targetYaw;
            doppel.m_5616_(targetYaw);
            doppel.f_20886_ = targetYaw;
         }
      }
   }

   private boolean playTapHold(TapHoldNode th) {
      SubComboNode tapSub = th.getTap();
      if (tapSub != null && tapSub.getAnimationAccessor() != null) {
         DoppelgangerEntity doppel = (DoppelgangerEntity)this.patch.getOriginal();
         this.patch.playAnimationSynchronized(tapSub.getAnimationAccessor(), tapSub.getConvertTime());
         doppel.setCcMode(false);
         doppel.setTapHoldActive(true);
         doppel.setTapHoldKeyIndex(this.state.getComboKeyIndex());
         doppel.setTapHoldWindupTicks(th.getWindupDurationTicks());
         this.state.setCurrentLogicNode((ComboNode)(th.hasNext() ? th : this.root));
         this.state.setCurrentDataNode(tapSub);
         this.state.setComboResetTicks(this.findResetTicks(th));
         this.state.setActiveTapHoldNode(th);
         this.state.setWindupStartTick(doppel.f_19797_);
         this.patch.updateInputBufferTtl(tapSub.getBufferDurationTicks());
         return true;
      } else {
         return false;
      }
   }

   private boolean playHitExtend(HitExtendNode he) {
      SubComboNode baseSub = he.getBase();
      if (baseSub != null && baseSub.getAnimationAccessor() != null) {
         DoppelgangerEntity doppel = (DoppelgangerEntity)this.patch.getOriginal();
         if (!Yamato.tryLoopSdtRapidSlash(this.patch, he)) {
            this.patch.playAnimationSynchronized(baseSub.getAnimationAccessor(), baseSub.getConvertTime());
         }

         doppel.setCcMode(false);
         this.state.setCurrentLogicNode((ComboNode)(he.hasNext() ? he : this.root));
         this.state.setCurrentDataNode(baseSub);
         this.state.setComboResetTicks(this.findResetTicks(he));
         this.state.beginHitExtend(he, ((DoppelgangerEntity)this.patch.getOriginal()).f_19797_);
         this.patch.updateInputBufferTtl(baseSub.getBufferDurationTicks());
         return true;
      } else {
         return false;
      }
   }

   boolean triggerHitExtend(IHitExtendNode activeNode) {
      if (activeNode != null && this.state.getActiveHitExtendNode() == activeNode) {
         SubComboNode extendSub = activeNode.getExtend();
         if (extendSub != null && extendSub.getAnimationAccessor() != null) {
            int heldTicks = ((DoppelgangerEntity)this.patch.getOriginal()).f_19797_ - this.state.getHitExtendStartTick();
            int hitTick = this.state.getHitExtendHitTick();
            int targetId = this.state.getHitExtendTargetId();
            this.state.setActiveHitExtendNode(null);
            this.state.setCurrentDataNode(extendSub);
            this.patch.updateInputBufferTtl(extendSub.getBufferDurationTicks());
            this.patch.playAnimationInstantly(extendSub.getAnimationAccessor());
            DMCLog.info(
               DMCLog.Category.DOPPEL_COMBO,
               "[HitExtend] TRIGGER doppel={} node={} target={} hitTick={} heldTicks={} extendAnim={}",
               ((DoppelgangerEntity)this.patch.getOriginal()).m_7755_().getString(),
               ((ComboNode)activeNode).getId(),
               targetId,
               hitTick,
               heldTicks,
               extendSub.getAnimationAccessor()
            );
            return true;
         } else {
            this.state.setActiveHitExtendNode(null);
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean playNormal(ComboNode node) {
      if (node.getAnimationAccessor() == null) {
         return false;
      } else {
         DoppelgangerEntity doppel = (DoppelgangerEntity)this.patch.getOriginal();
         this.patch.playAnimationSynchronized(node.getAnimationAccessor(), node.getConvertTime());
         doppel.setCcMode(false);
         this.state.setCurrentLogicNode(node.hasNext() ? node : this.root);
         this.state.setCurrentDataNode(node);
         this.state.setComboResetTicks(this.findResetTicks(node));
         this.patch.updateInputBufferTtl(node.getBufferDurationTicks());
         return true;
      }
   }

   private boolean isGrounded(DoppelgangerEntity entity) {
      if (entity.m_20096_()) {
         return true;
      } else {
         double feetY = entity.m_20186_();
         BlockPos pos = BlockPos.m_274561_(entity.m_20185_(), feetY - 0.01, entity.m_20189_());
         BlockState state = entity.m_9236_().m_8055_(pos);
         if (!state.m_60795_()) {
            VoxelShape shape = state.m_60812_(entity.m_9236_(), pos);
            if (!shape.m_83281_()) {
               double surfaceY = shape.m_83297_(Axis.Y) + (double)pos.m_123342_();
               return Math.abs(feetY - surfaceY) < 0.5;
            }
         }

         return false;
      }
   }

   private int findResetTicks(ComboNode node) {
      return node.getComboResetTime() >= 0.0F ? Math.round(node.getComboResetTime() * 20.0F) : node.getComboResetTicks();
   }

   private int findResetTime() {
      return this.state.getDefaultResetTicks() >= 0 ? this.state.getDefaultResetTicks() : 16;
   }
}
