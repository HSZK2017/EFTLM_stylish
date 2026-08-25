package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.capability.doppelganger.DoppelgangerBindingService;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerState;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerStateProvider;
import com.dmc.invincible_dmc.conditions.GroundedCondition;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerScript;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.skill.weapon_innate.ComboBasicAttack;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent.Context;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class CPDoppelgangerControl {
   private final CPDoppelgangerControl.Action action;
   private final int doppelDelayMode;

   public CPDoppelgangerControl(CPDoppelgangerControl.Action action) {
      this(action, -1);
   }

   public CPDoppelgangerControl(CPDoppelgangerControl.Action action, int doppelDelayMode) {
      this.action = action;
      this.doppelDelayMode = doppelDelayMode;
   }

   public static void toBytes(CPDoppelgangerControl msg, FriendlyByteBuf buf) {
      buf.m_130068_(msg.action);
      buf.writeByte(msg.doppelDelayMode);
   }

   public static CPDoppelgangerControl fromBytes(FriendlyByteBuf buf) {
      return new CPDoppelgangerControl((CPDoppelgangerControl.Action)buf.m_130066_(CPDoppelgangerControl.Action.class), buf.readByte());
   }

   public static void handle(CPDoppelgangerControl msg, Supplier<Context> ctx) {
      ctx.get()
         .enqueueWork(
            () -> {
               ServerPlayer sender = ctx.get().getSender();
               if (sender != null) {
                  DoppelgangerEntity.reconcileOwnerState(sender);
                  if (msg.action != CPDoppelgangerControl.Action.DISCARD && msg.action != CPDoppelgangerControl.Action.RECALL) {
                     ServerPlayerPatch gatePatch = EpicFightCapabilities.getServerPlayerPatch(sender);
                     if (!VergilSkill.isDoppelgangerAllowed(gatePatch)) {
                        DMCLog.warn(DMCLog.Category.DOPPEL_NET, "[DoppelCtrl] {} REJECTED: weapon_not_vergil_arsenal", msg.action);
                        return;
                     }
                  }

                  switch (msg.action) {
                     case CREATE:
                        ServerPlayerPatch serverPlayerPatch = EpicFightCapabilities.getServerPlayerPatch(sender);
                        if (serverPlayerPatch == null) {
                           return;
                        }

                        SkillContainer container = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
                        YamatoPlayerState characterState = YamatoPlayerStateProvider.get(sender);
                        int authoritativeDtStack = VergilSkill.getAuthoritativeDtStack(serverPlayerPatch);
                        if (authoritativeDtStack < 3) {
                           if (!sender.m_7500_()) {
                              DMCLog.info(DMCLog.Category.DOPPEL_NET, "[DoppelCtrl] CREATE — BLOCKED: stack {} < 3", container.getStack());
                              return;
                           }

                           characterState.setDtStack(5);
                           if (container != null && !container.isEmpty() && container.getSkill() instanceof VergilSkill) {
                              container.getSkill().setStackSynchronize(container, 5);
                           }
                        }

                        int mode = msg.doppelDelayMode >= 0 ? Math.max(0, Math.min(2, msg.doppelDelayMode)) : 1;
                        boolean skipAnim = false;
                        AnimationPlayer currentPlayer = DMCAnimationUtils.getMainPlayer(serverPlayerPatch);
                        StaticAnimation currentAnim = currentPlayer != null ? DMCAnimationUtils.getRealAnimation(currentPlayer) : null;
                        if (currentAnim != null) {
                           skipAnim = currentAnim.getProperty(YamatoAttackAnimation.NOT_USE_SUMMON_DOPPELGANGER_ANIM).orElse(false);
                        }

                        if (skipAnim) {
                           serverPlayerPatch.playSound((SoundEvent)SoundEvents.f_12377_.get(), 0.5F, 1.0F, 1.0F);
                           DoppelgangerBindingService.spawnImmediate(sender, mode);
                        } else {
                           boolean grounded = sender.m_20096_() || GroundedCondition.check(sender);
                           if (!DMCAnimationUtils.sameAnimation(currentAnim, (DynamicAnimation)YamatoAnimations.YAMATO_SUMMON_DOPPELGANGER_AIR.get())
                              && !DMCAnimationUtils.sameAnimation(currentAnim, (DynamicAnimation)YamatoAnimations.YAMATO_SUMMON_DOPPELGANGER_GROUND.get())) {
                              if (grounded) {
                                 serverPlayerPatch.playSound((SoundEvent)SoundEvents.f_12377_.get(), 0.5F, 1.0F, 1.0F);
                                 serverPlayerPatch.playAnimationSynchronized(YamatoAnimations.YAMATO_SUMMON_DOPPELGANGER_GROUND, 0.0F);
                              } else {
                                 serverPlayerPatch.playSound((SoundEvent)SoundEvents.f_12377_.get(), 0.5F, 1.0F, 1.0F);
                                 serverPlayerPatch.playAnimationSynchronized(YamatoAnimations.YAMATO_SUMMON_DOPPELGANGER_AIR, 0.0F);
                              }

                              if (container.getSkill() instanceof ComboBasicAttack comboBasicAttack) {
                                 comboBasicAttack.resetCombo(container);
                              }

                              DoppelgangerBindingService.requestSummon(sender, mode);
                              DMCLog.info(DMCLog.Category.DOPPEL_NET, "[DoppelCtrl] CREATE — playing summon anim (grounded={} mode={})", grounded, mode);
                           }
                        }
                        break;
                     case EXECUTE_SCRIPT:
                        DoppelgangerPatch patchx = DoppelgangerPatch.getNearestDoppelganger(sender);
                        DMCLog.info(DMCLog.Category.DOPPEL_NET, "[DoppelCtrl] EXECUTE_SCRIPT — patch={}", patchx != null ? "found" : "NULL");
                        if (patchx == null) {
                           clearDoppelUUID(sender);
                        } else {
                           DoppelgangerEntity doppelxx = (DoppelgangerEntity)patchx.getOriginal();
                           if (doppelxx.isInSpawnCooldown()) {
                              DMCLog.info(DMCLog.Category.DOPPEL_NET, "[DoppelCtrl] EXECUTE_SCRIPT — BLOCKED: spawn cooldown");
                           } else {
                              DoppelgangerScript script = patchx.getRecordedScript();
                              DMCLog.info(
                                 DMCLog.Category.DOPPEL_NET,
                                 "[DoppelCtrl] EXECUTE_SCRIPT — recordedScript={} isActive={} groundLen={} airLen={}",
                                 script != null ? "NOT NULL" : "NULL",
                                 script != null ? script.isActive() : "N/A",
                                 script != null ? script.groundInstructions().length : -1,
                                 script != null && script.airInstructions() != null ? script.airInstructions().length : -1
                              );
                              if (script != null && script.groundInstructions().length != 0) {
                                 if (!script.isActive() && !patchx.isScriptAnimPlaying()) {
                                    boolean doppelGrounded = doppelxx.m_20096_() || GroundedCondition.check(doppelxx);
                                    patchx.clearInputPipeline();
                                    patchx.comboState.clear();
                                    doppelxx.setCcMode(false);
                                    script.activate(doppelGrounded);
                                    script.setCurrentIndex(-1);
                                    patchx.setScriptStartTick((long)doppelxx.f_19797_);
                                    DMCLog.info(DMCLog.Category.DOPPEL_NET, "[DoppelCtrl] EXECUTE_SCRIPT — script ACTIVATED grounded={}", doppelGrounded);
                                 } else {
                                    DMCLog.info(
                                       DMCLog.Category.DOPPEL_NET,
                                       "[DoppelCtrl] EXECUTE_SCRIPT — dropped (active={} animPlaying={})",
                                       script.isActive(),
                                       patchx.isScriptAnimPlaying()
                                    );
                                 }
                              }
                           }
                        }
                        break;
                     case DISCARD:
                        boolean found = false;

                        for (Entity entity : sender.m_284548_().m_142646_().m_142273_()) {
                           if (entity instanceof DoppelgangerEntity doppelx && sender.m_20148_().equals(doppelx.getOwnerUUID()) && doppelx.m_6084_()) {
                              if (doppelx.isInSpawnCooldown()) {
                                 DMCLog.info(DMCLog.Category.DOPPEL_NET, "[DoppelCtrl] DISCARD — BLOCKED: spawn cooldown");
                                 found = true;
                              } else {
                                 DoppelgangerEntity.discardDoppelganger(doppelx);
                                 found = true;
                              }
                              break;
                           }
                        }

                        if (!found) {
                           clearDoppelUUID(sender);
                        }
                        break;
                     case RECALL:
                        DoppelgangerPatch patch = DoppelgangerPatch.getNearestDoppelganger(sender);
                        if (patch == null) {
                           clearDoppelUUID(sender);
                        } else {
                           DoppelgangerEntity doppel = (DoppelgangerEntity)patch.getOriginal();
                           if (doppel.isInSpawnCooldown()) {
                              DMCLog.info(DMCLog.Category.DOPPEL_NET, "[DoppelCtrl] RECALL — BLOCKED: spawn cooldown");
                           } else {
                              DoppelgangerScript script = patch.getRecordedScript();
                              if (script != null) {
                                 script.clear();
                              }

                              patch.clearInputPipeline();
                              patch.comboState.clear();
                              DoppelgangerEntity.recallDoppelganger((DoppelgangerEntity)patch.getOriginal());
                           }
                        }
                  }
               }
            }
         );
      ctx.get().setPacketHandled(true);
   }

   private static void clearDoppelUUID(ServerPlayer sp) {
      DoppelgangerBindingService.clearBinding(sp);
   }

   public static enum Action {
      CREATE,
      EXECUTE_SCRIPT,
      DISCARD,
      RECALL;
   }
}
