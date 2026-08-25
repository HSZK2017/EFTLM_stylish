package com.pla.annoyingvillagers.entity.goal;

import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.clazz.IdleAnimation;
import com.pla.annoyingvillagers.compat.EfDancing;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.entity.JevEntity;
import com.pla.annoyingvillagers.entity.PlayerNpcEntity;
import com.pla.annoyingvillagers.gameasset.AVAnimations;
import com.pla.annoyingvillagers.task.DelayedTask;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraftforge.fml.ModList;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class PlayIdleAnimationGoal extends Goal {
   private final Mob mob;
   private final int minDurationTicks;
   private int ticksLeft;
   private static final Map<IdleAnimation, List<String>> idleMessageKeys = Map.ofEntries(
      Map.entry(IdleAnimation.LAY, keys("idle.annoyingvillagers.lay")),
      Map.entry(IdleAnimation.SLEEP, keys("idle.annoyingvillagers.sleep")),
      Map.entry(IdleAnimation.SIT, keys("idle.annoyingvillagers.sit")),
      Map.entry(IdleAnimation.FUN_SIT, keys("idle.annoyingvillagers.fun_sit")),
      Map.entry(IdleAnimation.SLIGHT, keys("idle.annoyingvillagers.slight")),
      Map.entry(IdleAnimation.PUSH_UP, keys("idle.annoyingvillagers.push_up")),
      Map.entry(IdleAnimation.LAY_RELAX_EMOTE, keys("idle.annoyingvillagers.lay_relax_emote")),
      Map.entry(IdleAnimation.ONE_ARM_LAY_EMOTE, keys("idle.annoyingvillagers.one_arm_lay_emote")),
      Map.entry(IdleAnimation.SALUTE_LEFT_HAND_EMOTE, keys("idle.annoyingvillagers.salute_left_hand_emote")),
      Map.entry(IdleAnimation.SIT_NO_WEAPON_EMOTE, keys("idle.annoyingvillagers.sit_no_weapon_emote")),
      Map.entry(IdleAnimation.SORROW_EMOTE, keys("idle.annoyingvillagers.sorrow_emote")),
      Map.entry(IdleAnimation.SURRENDER_EMOTE, keys("idle.annoyingvillagers.surrender_emote")),
      Map.entry(IdleAnimation.ATTENTION_EMOTE, keys("idle.annoyingvillagers.attention_emote")),
      Map.entry(IdleAnimation.FLAPPING_EMOTE, keys("idle.annoyingvillagers.flapping_emote")),
      Map.entry(IdleAnimation.FUN_JUMP_EMOTE, keys("idle.annoyingvillagers.fun_jump_emote")),
      Map.entry(IdleAnimation.JUMP_EMOTE, keys("idle.annoyingvillagers.jump_emote")),
      Map.entry(IdleAnimation.PRONE_EMOTE, keys("idle.annoyingvillagers.prone_emote")),
      Map.entry(IdleAnimation.SALUTE_EMOTE, keys("idle.annoyingvillagers.salute_emote"))
   );

   private static List<String> keys(String prefix) {
      List<String> list = new ArrayList<>(20);

      for (int i = 1; i <= 20; i++) {
         list.add(prefix + "." + i);
      }

      return List.copyOf(list);
   }

   public PlayIdleAnimationGoal(Mob mob, int minDurationTicks) {
      this.mob = mob;
      this.minDurationTicks = minDurationTicks;
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean m_8036_() {
      if (!ModList.get().isLoaded("efdancing")) {
         return false;
      } else if (this.mob.m_9236_().f_46443_) {
         return false;
      } else if (this.mob instanceof JevEntity) {
         return false;
      } else if (this.mob.f_19797_ <= 30) {
         return false;
      } else if (!this.mob.m_6084_() || this.mob.m_213877_() || this.mob.m_21224_()) {
         return false;
      } else if (this.mob.m_20159_()) {
         return false;
      } else if (this.mob.m_5448_() != null) {
         return false;
      } else if (this.mob.m_21573_().m_26572_()) {
         return false;
      } else if (!this.mob.m_20096_()) {
         return false;
      } else {
         if (this.mob instanceof PlayerNpcEntity playerNpcEntity
            && (playerNpcEntity.isHealing() || playerNpcEntity.getPlayingIdleCooldown() != 0 || playerNpcEntity.isStrolling())) {
            return false;
         }

         if (this.mob instanceof AVNpc avNpc && (avNpc.isHealing() || avNpc.getPlayingIdleCooldown() != 0 || avNpc.isStrolling())) {
            return false;
         }

         LivingEntityPatch<?> patch = null;
         if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
            patch = playerNpcEntity.getLivingEntityPatch();
         }

         if (this.mob instanceof AVNpc avNpc) {
            patch = avNpc.getLivingEntityPatch();
         }

         if (patch == null) {
            return false;
         } else {
            AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(patch.getAnimator().getPlayerFor(null)).getRealAnimation();
            return EpicfightUtil.isLongHitAnimation(dynamicAnimation, patch) ? false : dynamicAnimation == Animations.EMPTY_ANIMATION;
         }
      }
   }

   public boolean m_8045_() {
      if (!ModList.get().isLoaded("efdancing")) {
         return false;
      } else if (this.mob.m_9236_().f_46443_) {
         return false;
      } else if (this.mob instanceof JevEntity) {
         return false;
      } else if (this.mob.f_19797_ <= 30) {
         return false;
      } else if (!this.mob.m_6084_() || this.mob.m_213877_() || this.mob.m_21224_()) {
         return false;
      } else if (this.mob.m_20159_()) {
         return false;
      } else if (!this.mob.m_20096_()) {
         return false;
      } else if (this.mob.m_5448_() != null) {
         return false;
      } else if (this.mob.m_21573_().m_26572_()) {
         return false;
      } else {
         if (this.mob instanceof PlayerNpcEntity playerNpcEntity
            && (playerNpcEntity.isHealing() || playerNpcEntity.getPlayingIdleCooldown() != 0 || playerNpcEntity.isStrolling())) {
            return false;
         }

         if (this.mob instanceof AVNpc avNpc && (avNpc.isHealing() || avNpc.getPlayingIdleCooldown() != 0 || avNpc.isStrolling())) {
            return false;
         }

         LivingEntityPatch<?> patch = null;
         if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
            patch = playerNpcEntity.getLivingEntityPatch();
         }

         if (this.mob instanceof AVNpc avNpc) {
            patch = avNpc.getLivingEntityPatch();
         }

         if (patch == null) {
            return false;
         } else {
            AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(patch.getAnimator().getPlayerFor(null)).getRealAnimation();
            return EpicfightUtil.isLongHitAnimation(dynamicAnimation, patch) ? false : this.ticksLeft > 0;
         }
      }
   }

   public void m_8056_() {
      if (ModList.get().isLoaded("efdancing")) {
         if (this.mob.m_6084_() && !this.mob.m_213877_() && !this.mob.m_21224_()) {
            this.ticksLeft = this.minDurationTicks;
            this.mob.m_21573_().m_26573_();
            this.mob.m_20334_(0.0, 0.0, 0.0);
            IdleAnimation choice = null;
            if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
               choice = playerNpcEntity.getIdleAnimationChoice();
            }

            if (this.mob instanceof AVNpc avNpc) {
               choice = avNpc.getIdleAnimationChoice();
            }

            if (choice == null) {
               choice = this.pickIdleAnimation();
               if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
                  playerNpcEntity.setIdleAnimationChoice(choice);
               }

               if (this.mob instanceof AVNpc avNpc) {
                  avNpc.setIdleAnimationChoice(choice);
               }
            }

            final AssetAccessor<? extends StaticAnimation> anim = this.resolveAnimation(choice);
            if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
               playerNpcEntity.setIdleAnimation(anim);
            }

            if (this.mob instanceof AVNpc avNpc) {
               avNpc.setIdleAnimation(anim);
            }

            if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
               playerNpcEntity.setPlayingIdle(true);
            }

            if (this.mob instanceof AVNpc avNpc) {
               avNpc.setPlayingIdle(true);
            }

            final IdleAnimation finalChoice = choice;
            new DelayedTask(30) {
               @Override
               public void run() {
                  if (PlayIdleAnimationGoal.this.mob.m_5448_() == null) {
                     if (PlayIdleAnimationGoal.this.mob.m_6084_() && !PlayIdleAnimationGoal.this.mob.m_213877_() && !PlayIdleAnimationGoal.this.mob.m_21224_()) {
                        PlayIdleAnimationGoal.this.playIdleAnimation(anim);
                        PlayIdleAnimationGoal.this.tryBroadcastIdleMessage(finalChoice);
                     }
                  }
               }
            };
         }
      }
   }

   public void m_8037_() {
      if (!ModList.get().isLoaded("efdancing")) {
         this.ticksLeft = 0;
      } else if (this.mob.m_5448_() != null || this.mob.m_21573_().m_26572_() || !this.mob.m_20096_()) {
         this.ticksLeft = 0;
      } else if (this.mob.m_9236_() instanceof ServerLevel) {
         this.mob.m_21573_().m_26573_();
         this.mob.m_20334_(0.0, 0.0, 0.0);
         LivingEntityPatch<?> patch = null;
         if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
            patch = playerNpcEntity.getLivingEntityPatch();
         }

         if (this.mob instanceof AVNpc avNpc) {
            patch = avNpc.getLivingEntityPatch();
         }

         AssetAccessor<? extends StaticAnimation> idleAnimation = null;
         if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
            idleAnimation = playerNpcEntity.getIdleAnimation();
         }

         if (this.mob instanceof AVNpc avNpc) {
            idleAnimation = avNpc.getIdleAnimation();
         }

         if (patch != null && idleAnimation != null) {
            AssetAccessor<? extends StaticAnimation> staticAnimation = Objects.requireNonNull(patch.getAnimator().getPlayerFor(null)).getRealAnimation();
            if (staticAnimation != idleAnimation) {
               if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
                  this.playIdleAnimation(playerNpcEntity.getIdleAnimation());
               }

               if (this.mob instanceof AVNpc avNpc) {
                  this.playIdleAnimation(avNpc.getIdleAnimation());
               }
            }
         }

         this.ticksLeft--;
      }
   }

   public void m_8041_() {
      if (this.mob instanceof AVNpc avNpc) {
         avNpc.clearIdleAnimationState();
         LivingEntityPatch<?> patch = avNpc.getLivingEntityPatch();
         if (patch != null) {
            patch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
         }

         avNpc.setPlayingIdle(false);
         avNpc.setPlayingIdleCooldown(new Random().nextInt(400, 1200));
      } else if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
         playerNpcEntity.clearIdleAnimationState();
         LivingEntityPatch<?> patch = playerNpcEntity.getLivingEntityPatch();
         if (patch != null) {
            patch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
         }

         playerNpcEntity.setPlayingIdle(false);
         playerNpcEntity.setPlayingIdleCooldown(new Random().nextInt(400, 1200));
      }
   }

   private void playIdleAnimation(AssetAccessor<? extends StaticAnimation> anim) {
      if (this.mob.m_6084_() && !this.mob.m_213877_() && !this.mob.m_21224_()) {
         LivingEntityPatch<?> patch = null;
         if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
            patch = playerNpcEntity.getLivingEntityPatch();
         }

         if (this.mob instanceof AVNpc avNpc) {
            patch = avNpc.getLivingEntityPatch();
         }

         if (patch != null) {
            patch.playAnimationSynchronized(anim, 0.0F);
         }
      }
   }

   private IdleAnimation pickIdleAnimation() {
      IdleAnimation[] all = IdleAnimation.values();
      return all[this.mob.m_217043_().m_188503_(all.length)];
   }

   private AssetAccessor<? extends StaticAnimation> resolveAnimation(IdleAnimation idle) {
      return EfDancing.resolveIdleAnimation(idle);
   }

   private void tryBroadcastIdleMessage(IdleAnimation idle) {
      if (this.mob.m_9236_() instanceof ServerLevel serverLevel) {
         if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_CHAT.get()) {
            if (this.mob instanceof PlayerNpcEntity playerNpcEntity && !playerNpcEntity.isIdleMessageBroadcast()) {
               List<String> pool = idleMessageKeys.get(idle);
               if (pool == null || pool.isEmpty()) {
                  return;
               }

               String key = pool.get(this.mob.m_217043_().m_188503_(pool.size()));
               serverLevel.m_7654_()
                  .m_6846_()
                  .m_240416_(
                     Component.m_237119_()
                        .m_7220_(Component.m_237113_("<"))
                        .m_7220_(this.mob.m_5446_())
                        .m_7220_(Component.m_237113_("> "))
                        .m_7220_(Component.m_237115_(key)),
                     false
                  );
               playerNpcEntity.setIdleMessageBroadcast(true);
            }
         }
      }
   }
}
