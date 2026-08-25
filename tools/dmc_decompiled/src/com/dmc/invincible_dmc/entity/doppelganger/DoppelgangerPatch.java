package com.dmc.invincible_dmc.entity.doppelganger;

import com.dmc.invincible_dmc.InvincibleMod_DMC;
import com.dmc.invincible_dmc.api.animation.types.customStun.ICustomStunDamageSource;
import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboNodeManager;
import com.dmc.invincible_dmc.api.skill.ComboType;
import com.dmc.invincible_dmc.api.skill.IHitExtendNode;
import com.dmc.invincible_dmc.api.skill.ITapHoldNode;
import com.dmc.invincible_dmc.api.skill.SubComboNode;
import com.dmc.invincible_dmc.capability.doppelganger.DoppelgangerBindingService;
import com.dmc.invincible_dmc.client.input.PlayerInputState;
import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import com.dmc.invincible_dmc.entity.util.DMCDodgeLocationIndicator;
import com.dmc.invincible_dmc.gameassets.DMCWeaponCategories;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.skill.weapon_combo.Yamato;
import com.dmc.invincible_dmc.skill.weapon_innate.ComboBasicAttack;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.DMCLog;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.lang.reflect.Field;
import java.util.PriorityQueue;
import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.Factions;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.entity.DodgeLocationIndicator;

public class DoppelgangerPatch extends HumanoidMobPatch<DoppelgangerEntity> {
   private static final double FOLLOW_DISTANCE = 0.7;
   public static final TagKey<DamageType> DOPPELGANGER_DAMAGE = InvincibleMod_DMC.createDamageType("doppelganger_damage");
   private static Field resultTypeField;
   private static Field damageField;
   @Nullable
   private PlayerPatch<?> ownerPatch;
   public final DoppelgangerComboState comboState = new DoppelgangerComboState();
   private final PriorityQueue<DoppelgangerInputEvent> inputQueue = new PriorityQueue<>(DoppelgangerInputEvent::compareTo);
   private final DoppelgangerInputBuffer inputBuffer = new DoppelgangerInputBuffer();
   private DoppelgangerComboExecutor comboExecutor;
   public int aerialAttackCount;
   int tickSinceLastAction;
   private int defaultBufferTtlTicks = 8;
   private DoppelgangerScript recordedScript;
   private long scriptStartTick;
   private boolean scriptAnimPlaying = false;

   public void setRecordedScript(DoppelgangerScript script) {
      this.recordedScript = script;
   }

   public DoppelgangerScript getRecordedScript() {
      return this.recordedScript;
   }

   public void setScriptStartTick(long tick) {
      this.scriptStartTick = tick;
   }

   public boolean isScriptAnimPlaying() {
      return this.scriptAnimPlaying;
   }

   public void abortScript() {
      if (this.recordedScript != null) {
         this.recordedScript.clear();
      }

      this.scriptAnimPlaying = false;
   }

   public void updateInputBufferTtl(int nodeBufferDurationTicks) {
      if (nodeBufferDurationTicks >= 0) {
         this.inputBuffer.setTtlTicks(nodeBufferDurationTicks);
      } else {
         this.inputBuffer.setTtlTicks(this.defaultBufferTtlTicks);
      }
   }

   public DoppelgangerPatch() {
      super(Factions.NEUTRAL);
   }

   public void initAnimator(Animator animator) {
      super.initAnimator(animator);
      super.commonAggresiveMobAnimatorInit(animator);
   }

   protected void initAI() {
   }

   protected void setWeaponMotions() {
      super.setWeaponMotions();
      this.weaponLivingMotions = Maps.newHashMap();
      this.weaponLivingMotions
         .put(
            DMCWeaponCategories.DMC5_YAMATO,
            ImmutableMap.of(
               Styles.TWO_HAND,
               Set.of(
                  Pair.of(LivingMotions.WALK, YamatoAnimations.YAMATO_WALK),
                  Pair.of(LivingMotions.CHASE, YamatoAnimations.YAMATO_RUN),
                  Pair.of(LivingMotions.IDLE, YamatoAnimations.YAMATO_IDLE),
                  Pair.of(LivingMotions.RUN, YamatoAnimations.YAMATO_RUN),
                  Pair.of(LivingMotions.SNEAK, YamatoAnimations.YAMATO_SNEAK),
                  Pair.of(LivingMotions.KNEEL, YamatoAnimations.YAMATO_KNEEL)
               )
            )
         );
   }

   public void updateMotion(boolean considerInaction) {
      PlayerPatch<?> ownerPatch = this.getOwnerPatch();
      if (ownerPatch == null) {
         this.currentLivingMotion = LivingMotions.IDLE;
         this.currentCompositeMotion = LivingMotions.IDLE;
      } else {
         if (this.getEntityState().updateLivingMotion()) {
            if (ownerPatch.getEntityState().updateLivingMotion()) {
               this.currentLivingMotion = ownerPatch.currentLivingMotion;
               this.currentCompositeMotion = ownerPatch.currentCompositeMotion;
            } else {
               this.updateIndependentMovementMotion((Player)ownerPatch.getOriginal());
            }
         }
      }
   }

   private void updateIndependentMovementMotion(Player owner) {
      DoppelgangerEntity doppel = (DoppelgangerEntity)this.getOriginal();
      if (doppel.m_21223_() <= 0.0F) {
         this.currentLivingMotion = LivingMotions.DEATH;
      } else if (doppel.m_20202_() != null) {
         this.currentLivingMotion = LivingMotions.MOUNT;
      } else if (!(doppel.m_20184_().f_82480_ < -0.55) && !this.isAirborneState()) {
         Vec3 movement = doppel.m_20184_();
         boolean moving = doppel.f_267362_.m_267731_() > 0.01F || movement.f_82479_ * movement.f_82479_ + movement.f_82481_ * movement.f_82481_ > 1.0E-5;
         if (!moving) {
            this.currentLivingMotion = LivingMotions.IDLE;
         } else if (owner.m_6047_()) {
            this.currentLivingMotion = LivingMotions.SNEAK;
         } else if (owner.m_20142_()) {
            this.currentLivingMotion = LivingMotions.RUN;
         } else {
            this.currentLivingMotion = LivingMotions.WALK;
         }
      } else {
         this.currentLivingMotion = LivingMotions.FALL;
      }

      this.currentCompositeMotion = this.currentLivingMotion;
   }

   public AttackResult attack(EpicFightDamageSource damageSource, Entity target, InteractionHand hand) {
      DoppelgangerEntity clone = (DoppelgangerEntity)this.getOriginal();
      if (target == clone.getOwner()) {
         return AttackResult.missed(0.0F);
      } else if (!(target instanceof LivingEntity)) {
         return AttackResult.missed(0.0F);
      } else if (target instanceof DodgeLocationIndicator) {
         return AttackResult.missed(0.0F);
      } else if (target instanceof DMCDodgeLocationIndicator) {
         return AttackResult.missed(0.0F);
      } else if (target instanceof DMCSummonedSwordEntity) {
         return AttackResult.missed(0.0F);
      } else {
         if (target instanceof LivingEntity livingEntity && !livingEntity.m_142066_()) {
            return AttackResult.missed(0.0F);
         }

         AttackResult attackResult;
         if (this.getOwnerPatch() != null) {
            EpicFightDamageSource modifiedSource = damageSource.addRuntimeTag(DOPPELGANGER_DAMAGE);
            attackResult = this.getOwnerPatch().attack(modifiedSource, target, hand);
         } else {
            damageSource.addRuntimeTag(DOPPELGANGER_DAMAGE);
            attackResult = super.attack(damageSource, target, hand);
         }

         IHitExtendNode activeHE = this.comboState.getActiveHitExtendNode();
         if (activeHE != null && attackResult.resultType.dealtDamage() && activeHE.matchesBaseAnimation(DMCAnimationUtils.getRealAnimationAccessor(this))) {
            boolean newlyLatched = this.comboState.latchHitExtend(((DoppelgangerEntity)this.getOriginal()).f_19797_, target.m_19879_());
            if (activeHE.shouldStabilizeContact() && (newlyLatched || this.comboState.getHitExtendTargetId() == target.m_19879_())) {
               this.stabilizeHitExtendContact(target);
            }

            if (newlyLatched) {
               DMCLog.info(
                  DMCLog.Category.DOPPEL_COMBO,
                  "[HitExtend] LATCH doppel={} node={} target={} hitTick={} heldTicks={}/{}",
                  ((DoppelgangerEntity)this.getOriginal()).m_7755_().getString(),
                  ((ComboNode)activeHE).getId(),
                  target.m_19879_(),
                  ((DoppelgangerEntity)this.getOriginal()).f_19797_,
                  ((DoppelgangerEntity)this.getOriginal()).f_19797_ - this.comboState.getHitExtendStartTick(),
                  activeHE.getMinimumHoldTicks()
               );
            }
         }

         this.setDoppelgangerAttackResult(attackResult);
         if (target instanceof LivingEntity livingTarget) {
            this.setLastAttackEntity(livingTarget);
         }

         return attackResult;
      }
   }

   private void setDoppelgangerAttackResult(AttackResult attackResult) {
      try {
         if (resultTypeField != null && damageField != null) {
            resultTypeField.set(this, attackResult.resultType);
            damageField.set(this, attackResult.damage);
         }
      } catch (Exception var3) {
         DMCLog.error(DMCLog.Category.DOPPEL_GENERAL, "Failed to set doppelganger attack result", var3);
      }
   }

   public EpicFightDamageSource getDamageSource(AnimationAccessor<? extends StaticAnimation> animation, InteractionHand hand) {
      Player owner = ((DoppelgangerEntity)this.getOriginal()).getOwner();
      if (owner != null && this.ownerPatch != null) {
         EpicFightDamageSource damageSource = this.ownerPatch
            .getDamageSource(animation, hand)
            .addRuntimeTag(DOPPELGANGER_DAMAGE)
            .setAnimation(animation)
            .setBaseArmorNegation(this.getArmorNegation(hand))
            .setBaseImpact(this.getImpact(hand))
            .setUsedItem(((DoppelgangerEntity)this.getOriginal()).m_21120_(hand));
         ((ICustomStunDamageSource)damageSource).invincible$setCustomStunSourceYRot(this.getYRot());
         return damageSource;
      } else {
         return super.getDamageSource(animation, hand).addRuntimeTag(DOPPELGANGER_DAMAGE);
      }
   }

   public void tick(LivingTickEvent event) {
      super.tick(event);
      PlayerPatch<?> ownerPatch = this.getOwnerPatch();
      if (ownerPatch != null) {
         Player owner = (Player)ownerPatch.getOriginal();
         DoppelgangerEntity clone = (DoppelgangerEntity)this.getOriginal();
         if (owner.m_9236_() == clone.m_9236_() && !(owner.m_20280_(clone) > 2304.0)) {
            if (!clone.isInSpawnCooldown()) {
               AnimationPlayer animPlayer = DMCAnimationUtils.getMainPlayer(this);
               if (animPlayer != null
                  && !animPlayer.isEmpty()
                  && DMCAnimationUtils.sameAccessor(DMCAnimationUtils.getCurrentAnimationAccessor(animPlayer), YamatoAnimations.YAMATO_JUMP)) {
                  clone.m_6034_(clone.m_20185_(), owner.m_20186_() - clone.jumpYOffset, clone.m_20189_());
               }
            }

            if (this.comboExecutor == null && Yamato.YAMATO instanceof ComboBasicAttack cba) {
               this.setComboRoot(cba.getRoot());
               this.defaultBufferTtlTicks = cba.getInputBufferDurationTicks();
               this.inputBuffer.setTtlTicks(this.defaultBufferTtlTicks);
            }

            if (clone.m_20096_()) {
               this.aerialAttackCount = 0;
            }

            long currentTick = clone.m_9236_().m_46467_();
            DoppelgangerScript script = this.recordedScript;
            if (this.scriptAnimPlaying && (this.recordedScript == null || !this.recordedScript.isActive()) && this.getEntityState().canBasicAttack()) {
               this.scriptAnimPlaying = false;
            }

            if (clone.isInSpawnCooldown()) {
               this.inputQueue.clear();
               this.inputBuffer.consume();
            } else if (script != null && script.isActive()) {
               this.tickScriptExecution(currentTick);
            } else {
               int currentDelayTicks = DoppelgangerEntity.getDelayTicks(clone.getDoppelDelayMode());

               while (!this.inputQueue.isEmpty() && this.inputQueue.peek().scheduledTick() <= currentTick) {
                  DoppelgangerInputEvent evt = this.inputQueue.poll();
                  if (currentDelayTicks >= evt.intendedDelayTicks()) {
                     this.inputBuffer.accept(evt, currentTick);
                  }
               }

               if (!this.inputBuffer.isEmpty() && this.comboExecutor != null) {
                  DoppelgangerInputEvent evt = this.inputBuffer.peekValid(currentTick);
                  if (evt != null) {
                     boolean sdtRapidSlashLoopInput = evt.type() == ComboNode.ComboTypes.KEY_1 && evt.holdingUp() && Yamato.isSdtRapidSlashLoopWindow(this);
                     if (this.getEntityState().canBasicAttack() || sdtRapidSlashLoopInput) {
                        if (this.comboState.getActiveHitExtendNode() != null
                           && !evt.isLongPress()
                           && keyIndexToType(this.comboState.getComboKeyIndex()) == evt.type()) {
                           this.comboState.setActiveHitExtendNode(null);
                        }

                        if (!clone.isCcMode()) {
                           this.comboExecutor.tryExecute(evt);
                        }

                        this.inputBuffer.consume();
                     }
                  }
               }

               ITapHoldNode activeTH = this.comboState.getActiveTapHoldNode();
               if (activeTH != null) {
                  boolean keyHeld = this.isOwnerComboKeyHeld(ownerPatch, this.comboState.getComboKeyIndex());
                  int elapsed = clone.f_19797_ - this.comboState.getWindupStartTick();
                  if (!keyHeld) {
                     this.comboState.setActiveTapHoldNode(null);
                     clone.setTapHoldActive(false);
                  } else if (elapsed >= activeTH.getWindupDurationTicks()) {
                     this.comboState.setActiveTapHoldNode(null);
                     clone.setTapHoldActive(false);
                     SubComboNode holdSub = activeTH.getHold();
                     if (holdSub != null && holdSub.getAnimationAccessor() != null) {
                        this.playAnimationSynchronized(holdSub.getAnimationAccessor(), holdSub.getConvertTime());
                     }
                  }
               }

               IHitExtendNode activeHE = this.comboState.getActiveHitExtendNode();
               if (activeHE != null) {
                  boolean keyHeld = this.isOwnerComboKeyHeld(ownerPatch, this.comboState.getComboKeyIndex());
                  if (!keyHeld) {
                     DMCLog.info(
                        DMCLog.Category.DOPPEL_COMBO,
                        "[HitExtend] CANCEL doppel={} node={} reason=key_released latched={}",
                        clone.m_7755_().getString(),
                        ((ComboNode)activeHE).getId(),
                        this.comboState.hasLatchedHitExtend()
                     );
                     this.comboState.setActiveHitExtendNode(null);
                  } else if (!activeHE.matchesBaseAnimation(DMCAnimationUtils.getRealAnimationAccessor(this))) {
                     DMCLog.info(
                        DMCLog.Category.DOPPEL_COMBO,
                        "[HitExtend] CANCEL doppel={} node={} reason=base_animation_ended latched={}",
                        clone.m_7755_().getString(),
                        ((ComboNode)activeHE).getId(),
                        this.comboState.hasLatchedHitExtend()
                     );
                     this.comboState.setActiveHitExtendNode(null);
                  } else {
                     if (this.comboState.hasLatchedHitExtend() && activeHE.shouldStabilizeContact()) {
                        Entity hitTarget = clone.m_9236_().m_6815_(this.comboState.getHitExtendTargetId());
                        if (hitTarget != null && hitTarget.m_6084_()) {
                           this.stabilizeHitExtendContact(hitTarget);
                        }
                     }

                     if (this.comboState.isHitExtendReady(clone.f_19797_) && this.comboExecutor != null) {
                        this.comboExecutor.triggerHitExtend(activeHE);
                     }
                  }
               }

               clone.ensureConfiguredWeapon();
               LivingEntity selectedTarget = DoppelgangerTargetingController.selectAttackTarget(ownerPatch, clone);
               if (this.getTarget() != selectedTarget) {
                  this.setAttakTargetSync(selectedTarget);
               }

               ComboNode dataNode = this.comboState.getCurrentDataNode();
               if (dataNode != null && dataNode.getComboResetAtAnimTime() >= 0.0F) {
                  AnimationPlayer player = DMCAnimationUtils.getMainPlayer(this);
                  if (player != null && !player.isEmpty() && player.getElapsedTime() >= dataNode.getComboResetAtAnimTime()) {
                     this.comboState.setCurrentLogicNode(null);
                     this.comboState.setComboResetTicks(-1);
                  }
               }

               int nodeResetTicks = this.comboState.getComboResetTicks();
               if (nodeResetTicks > 0) {
                  boolean shouldReset;
                  if (this.comboState.getLastExecutionTick() > 0L) {
                     long elapsed = (long)clone.f_19797_ - this.comboState.getLastExecutionTick();
                     shouldReset = elapsed >= (long)nodeResetTicks;
                  } else {
                     if (this.getEntityState().canBasicAttack()) {
                        this.tickSinceLastAction++;
                     }

                     shouldReset = this.tickSinceLastAction > nodeResetTicks;
                  }

                  if (shouldReset) {
                     this.comboState.setCurrentLogicNode(null);
                     this.comboState.setComboResetTicks(-1);
                  }
               }

               if (this.getEntityState().canBasicAttack()) {
                  this.tickSinceLastAction++;
               }

               int skillResetTicks = this.comboState.getDefaultResetTicks();
               if (skillResetTicks <= 0) {
                  skillResetTicks = 16;
               }

               if (this.tickSinceLastAction > skillResetTicks) {
                  this.comboState.setCurrentLogicNode(null);
                  this.comboState.setComboResetTicks(-1);
               }
            }
         } else {
            this.teleportToOwner(owner, clone);
         }
      }
   }

   void resetActionTick() {
      this.tickSinceLastAction = 0;
   }

   private void stabilizeHitExtendContact(Entity target) {
      DoppelgangerEntity doppel = (DoppelgangerEntity)this.getOriginal();
      Vec3 previous = new Vec3(doppel.f_19854_, doppel.m_20186_(), doppel.f_19856_);
      Vec3 current = doppel.m_20182_();
      Vec3 travel = current.m_82546_(previous).m_82542_(1.0, 0.0, 1.0);
      if (travel.m_82556_() < 1.0E-6) {
         travel = doppel.m_20154_().m_82542_(1.0, 0.0, 1.0);
      }

      if (!(travel.m_82556_() < 1.0E-6)) {
         Vec3 direction = travel.m_82541_();
         double separation = Math.max(0.65, (double)(doppel.m_20205_() + target.m_20205_()) * 0.5 + 0.15);
         Vec3 desired = target.m_20182_().m_82546_(direction.m_82490_(separation));
         double correction = current.m_82546_(desired).m_82526_(direction);
         if (!(correction <= 0.02)) {
            Vec3 positionCorrection = desired.m_82546_(current).m_82542_(1.0, 0.0, 1.0);
            if (doppel.m_9236_().m_45756_(doppel, doppel.m_20191_().m_82383_(positionCorrection))) {
               doppel.m_6034_(desired.f_82479_, doppel.m_20186_(), desired.f_82481_);
            }

            Vec3 velocity = doppel.m_20184_();
            double forwardVelocity = velocity.m_82526_(direction);
            if (forwardVelocity > 0.0) {
               doppel.m_20256_(velocity.m_82546_(direction.m_82490_(forwardVelocity)));
            }
         }
      }
   }

   private void teleportToOwner(Player owner, DoppelgangerEntity clone) {
      float yawRad = owner.m_146908_() * (float) (Math.PI / 180.0);
      Vec3 offset = new Vec3(-Math.sin((double)yawRad + (Math.PI / 2)), 0.0, Math.cos((double)yawRad + (Math.PI / 2))).m_82541_().m_82490_(0.7);
      clone.m_6034_(owner.m_20185_() + offset.f_82479_, owner.m_20186_() + offset.f_82480_, owner.m_20189_() + offset.f_82481_);
   }

   @Nullable
   public PlayerPatch<?> getOwnerPatch() {
      Player owner = ((DoppelgangerEntity)this.getOriginal()).getOwner();
      if (owner == null) {
         this.ownerPatch = null;
         return null;
      } else if (this.ownerPatch != null && this.ownerPatch.getOriginal() == owner) {
         return this.ownerPatch;
      } else {
         this.ownerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(owner, PlayerPatch.class);
         return this.ownerPatch;
      }
   }

   public boolean isComboCFacingLocked() {
      AnimationPlayer animationPlayer = DMCAnimationUtils.getMainPlayer(this);
      if (animationPlayer != null && !animationPlayer.isEmpty()) {
         AssetAccessor<? extends StaticAnimation> realAnimation = DMCAnimationUtils.getRealAnimationAccessor(
            DMCAnimationUtils.getCurrentAnimation(animationPlayer)
         );
         return DMCAnimationUtils.isOneOfAccessor(
            realAnimation, YamatoAnimations.YAMATO_COMBO_C_START, YamatoAnimations.YAMATO_COMBO_C_LOOP, YamatoAnimations.YAMATO_COMBO_C_END
         );
      } else {
         return false;
      }
   }

   public OpenMatrix4f getModelMatrix(float partialTicks) {
      return super.getModelMatrix(partialTicks).scale(0.9375F, 0.9375F, 0.9375F);
   }

   @Nullable
   private static ComboType keyIndexToType(int keyIndex) {
      return switch (keyIndex) {
         case 0 -> ComboNode.ComboTypes.KEY_1;
         case 1 -> ComboNode.ComboTypes.KEY_2;
         case 2 -> ComboNode.ComboTypes.KEY_3;
         case 3 -> ComboNode.ComboTypes.KEY_4;
         case 4 -> ComboNode.ComboTypes.WEAPON_INNATE;
         default -> null;
      };
   }

   private boolean isOwnerComboKeyHeld(PlayerPatch<?> ownerPatch, int keyIndex) {
      return keyIndex >= 0 && keyIndex < 5 ? PlayerInputState.isRemoteDown((Player)ownerPatch.getOriginal(), 9 + keyIndex) : false;
   }

   @Nullable
   public static DoppelgangerPatch getNearestDoppelganger(ServerPlayer player) {
      DoppelgangerEntity doppel = DoppelgangerBindingService.findBoundEntity(player);
      return doppel != null && doppel.m_9236_() == player.m_284548_()
         ? (DoppelgangerPatch)EpicFightCapabilities.getEntityPatch(doppel, DoppelgangerPatch.class)
         : null;
   }

   public void enqueueInput(DoppelgangerInputEvent event) {
      this.inputQueue.add(event);
   }

   public void setComboRoot(ComboNode root) {
      this.comboExecutor = new DoppelgangerComboExecutor(this, root);
   }

   public boolean executeComboNode(ComboNode node, DoppelgangerInputEvent event) {
      return this.comboExecutor != null && this.comboExecutor.tryExecuteNode(node, event);
   }

   public void clearInputPipeline() {
      this.inputQueue.clear();
      this.inputBuffer.consume();
   }

   public void playForcedComboNode(ComboNode node) {
      if (node != null && node.getAnimationAccessor() != null) {
         this.clearInputPipeline();
         this.abortScript();
         this.comboState.clear();
         this.tickSinceLastAction = 0;
         this.playAnimationSynchronized(node.getAnimationAccessor(), node.getConvertTime());
         this.comboState.setCurrentDataNode(node);
         this.comboState.setLastExecutionTick((long)((DoppelgangerEntity)this.getOriginal()).f_19797_);
         this.updateInputBufferTtl(node.getBufferDurationTicks());
      }
   }

   public static void resetAllDoppelgangerCcMode(ServerPlayer player) {
      for (Entity entity : player.m_284548_().m_142646_().m_142273_()) {
         if (entity instanceof DoppelgangerEntity) {
            DoppelgangerEntity doppel = (DoppelgangerEntity)entity;
            if (player.m_20148_().equals(doppel.getOwnerUUID()) && doppel.m_6084_()) {
               doppel.setCcMode(false);
               doppel.setCcNodeId(0);
               DoppelgangerPatch patch = (DoppelgangerPatch)EpicFightCapabilities.getEntityPatch(doppel, DoppelgangerPatch.class);
               if (patch != null) {
                  patch.comboState.clear();
                  patch.tickSinceLastAction = 0;
               }
            }
         }
      }
   }

   private void tickScriptExecution(long currentTick) {
      DoppelgangerEntity doppel = (DoppelgangerEntity)this.getOriginal();
      DoppelgangerScript script = this.recordedScript;
      if (script != null) {
         ScriptInstruction inst = script.peekNext();
         if (inst == null) {
            DMCLog.info(DMCLog.Category.DOPPEL_GENERAL, "[DoppelScript] script DONE — releasing to sync");
            script.clear();
         } else {
            DMCLog.info(
               DMCLog.Category.DOPPEL_GENERAL,
               "[DoppelScript] tick idx={} inst={} canBasicAttack={}",
               script.currentIndex(),
               inst.getClass().getSimpleName(),
               this.getEntityState().canBasicAttack()
            );
            if (inst instanceof ScriptInstruction.PlayNode playNode) {
               if (script.currentIndex() >= 0 && !this.getEntityState().canBasicAttack()) {
                  DMCLog.info(DMCLog.Category.DOPPEL_GENERAL, "[DoppelScript] PlayNode waiting — !canBasicAttack");
                  return;
               }

               this.playScriptNode(playNode.nodeId(), currentTick);
               script.next();
            } else if (inst instanceof ScriptInstruction.LoopStart loopStart) {
               int bodyIdx = script.currentIndex() + 2;
               script.setLoopStartIndex(bodyIdx);
               script.setLoopRemaining(loopStart.count());
               DMCLog.info(DMCLog.Category.DOPPEL_GENERAL, "[DoppelScript] LoopStart count={} loopStartIdx={}", loopStart.count(), bodyIdx);
               script.next();
            } else if (inst instanceof ScriptInstruction.LoopEnd) {
               DMCLog.info(
                  DMCLog.Category.DOPPEL_GENERAL, "[DoppelScript] LoopEnd — remaining={} → jump={}", script.loopRemaining() - 1, script.loopRemaining() > 0
               );
               script.setLoopRemaining(script.loopRemaining() - 1);
               if (script.loopRemaining() > 0) {
                  script.jumpToLoopStart();
               } else {
                  script.next();
               }
            }
         }
      }
   }

   private void playScriptNode(long nodeId, long currentTick) {
      ComboNode node = ComboNodeManager.get((int)nodeId);
      DMCLog.info(
         DMCLog.Category.DOPPEL_GENERAL,
         "[DoppelScript] playScriptNode nodeId={} found={} anim={}",
         nodeId,
         node != null ? "YES" : "NO",
         node != null && node.getAnimationAccessor() != null ? node.getAnimationAccessor().toString() : "null"
      );
      if (node != null && node.getAnimationAccessor() != null) {
         this.playAnimationSynchronized(node.getAnimationAccessor(), node.getConvertTime());
         this.scriptAnimPlaying = true;
         this.comboState.clear();
         this.tickSinceLastAction = 0;
         this.comboState.setCurrentDataNode(node);
         this.comboState.setLastExecutionTick(currentTick);
         this.updateInputBufferTtl(node.getBufferDurationTicks());
      } else {
         DMCLog.warn(DMCLog.Category.DOPPEL_GENERAL, "[DoppelScript] playScriptNode FAILED — node or anim null");
      }
   }

   static {
      try {
         resultTypeField = LivingEntityPatch.class.getDeclaredField("lastAttackResultType");
         damageField = LivingEntityPatch.class.getDeclaredField("lastDealDamage");
         resultTypeField.setAccessible(true);
         damageField.setAccessible(true);
      } catch (Exception var1) {
         DMCLog.error(DMCLog.Category.DOPPEL_GENERAL, "Failed to init doppelganger reflection fields", var1);
      }
   }
}
