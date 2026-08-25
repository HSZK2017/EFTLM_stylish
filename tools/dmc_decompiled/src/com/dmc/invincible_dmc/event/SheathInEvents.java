package com.dmc.invincible_dmc.event;

import com.dmc.invincible_dmc.api.animation.types.yamato.JudgementCutEndAnimation;
import com.dmc.invincible_dmc.api.forgeevent.YamatoSheathEvent;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.judgementcut.JudgementCutEntity;
import com.dmc.invincible_dmc.entity.soul.SoulEntity;
import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import com.dmc.invincible_dmc.gameassets.DMCEffects;
import com.dmc.invincible_dmc.gameassets.animations.stun.CustomStunAnimations;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.ConcentrationManager;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@EventBusSubscriber(
   modid = "invincible_dmc"
)
public class SheathInEvents {
   private static final Map<UUID, List<BlockPos>> PENDING_TNT = new HashMap<>();

   public static void cacheTntBlocks(UUID playerUUID, List<BlockPos> positions) {
      PENDING_TNT.computeIfAbsent(playerUUID, k -> new ArrayList<>()).addAll(positions);
   }

   public static List<BlockPos> consumeTntBlocks(UUID playerUUID) {
      List<BlockPos> result = PENDING_TNT.remove(playerUUID);
      return result != null ? result : Collections.emptyList();
   }

   private static boolean isNearGround(LivingEntity target) {
      BlockPos ground = target.m_20183_().m_7495_();
      return target.m_9236_().m_8055_(ground).m_280296_() && target.m_20186_() - ((double)ground.m_123342_() + 1.0) <= 0.5;
   }

   private static void moveTargetSynchronized(LivingEntity target, Vec3 destination) {
      target.m_20256_(Vec3.f_82478_);
      target.f_19789_ = 0.0F;
      if (target instanceof ServerPlayer serverPlayer) {
         serverPlayer.m_6021_(destination.f_82479_, destination.f_82480_, destination.f_82481_);
      } else {
         target.m_6021_(destination.f_82479_, destination.f_82480_, destination.f_82481_);
      }

      target.f_19812_ = true;
      target.f_19864_ = true;
   }

   private static SheathInEvents.SheathAction resolve(AnimationAccessor<?> anim) {
      if (anim == YamatoAnimations.YAMATO_COMBO_A_3) {
         return new SheathInEvents.SheathAction(1, 0.0F);
      } else if (anim == YamatoAnimations.YAMATO_COMBO_A_4) {
         return new SheathInEvents.SheathAction(1, 0.0F);
      } else if (anim == YamatoAnimations.YAMATO_COMBO_A_4_SDT) {
         return new SheathInEvents.SheathAction(1, 50.0F);
      } else if (anim == YamatoAnimations.YAMATO_COMBO_A_5_SDT) {
         return new SheathInEvents.SheathAction(3, 0.0F);
      } else if (anim == YamatoAnimations.YAMATO_COMBO_B_1) {
         return new SheathInEvents.SheathAction(1, 0.0F);
      } else if (anim == YamatoAnimations.YAMATO_COMBO_B_2_SDT) {
         return new SheathInEvents.SheathAction(1, 50.0F);
      } else if (anim == YamatoAnimations.YAMATO_COMBO_C_END) {
         return new SheathInEvents.SheathAction(1, 0.0F);
      } else if (anim == YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_1) {
         return new SheathInEvents.SheathAction(0, 10.0F);
      } else if (anim == YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_2) {
         return new SheathInEvents.SheathAction(0, 20.0F);
      } else if (anim == YamatoAnimations.YAMATO_AERIALRAVE_COMBO_A_3) {
         return new SheathInEvents.SheathAction(0, 30.0F);
      } else if (anim == YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_1) {
         return new SheathInEvents.SheathAction(1, 0.0F);
      } else if (anim == YamatoAnimations.YAMATO_AERIALRAVE_COMBO_B_2) {
         return new SheathInEvents.SheathAction(0, 50.0F);
      } else if (anim == YamatoAnimations.YAMATO_UPPERSLASH_1) {
         return new SheathInEvents.SheathAction(0, 50.0F);
      } else if (anim == YamatoAnimations.YAMATO_UPPERSLASH_2) {
         return new SheathInEvents.SheathAction(0, 20.0F);
      } else if (anim == YamatoAnimations.YAMATO_RAPIDSLASH) {
         return new SheathInEvents.SheathAction(0, 30.0F);
      } else if (anim == YamatoAnimations.YAMATO_RAPIDSLASH_RE) {
         return new SheathInEvents.SheathAction(0, 30.0F);
      } else if (anim == YamatoAnimations.YAMATO_RISINGSTAR) {
         return new SheathInEvents.SheathAction(1, 50.0F);
      } else if (anim == YamatoAnimations.YAMATO_AERIALCLEAVE) {
         return new SheathInEvents.SheathAction(0, 50.0F);
      } else if (anim == YamatoAnimations.YAMATO_AERIALCLEAVE_FAST) {
         return new SheathInEvents.SheathAction(0, 50.0F);
      } else if (anim == YamatoAnimations.YAMATO_VOID_SLASH) {
         return new SheathInEvents.SheathAction(1, 50.0F);
      } else if (anim == YamatoAnimations.YAMATO_JUDGEMENT_CUT_END) {
         return new SheathInEvents.SheathAction(2, 0.0F);
      } else if (anim == YamatoAnimations.YAMATO_JUDGEMENT_CUT_END_INSTANT) {
         return new SheathInEvents.SheathAction(2, 0.0F);
      } else if (anim == YamatoAnimations.YAMATO_EXECUTION_ALL) {
         return new SheathInEvents.SheathAction(1, 50.0F);
      } else if (anim == YamatoAnimations.YAMATO_EXECUTION_DASH) {
         return new SheathInEvents.SheathAction(1, 50.0F);
      } else if (anim == YamatoAnimations.YAMATO_EXECUTION_END) {
         return new SheathInEvents.SheathAction(1, 50.0F);
      } else if (anim == YamatoAnimations.YAMATO_PROVOCATION_C) {
         return new SheathInEvents.SheathAction(3, 0.0F);
      } else if (anim == YamatoAnimations.YAMATO_PROVOCATION_D) {
         return new SheathInEvents.SheathAction(3, 0.0F);
      } else {
         return anim == YamatoAnimations.YAMATO_PROVOCATION_PORTAL ? new SheathInEvents.SheathAction(3, 0.0F) : null;
      }
   }

   @SubscribeEvent
   public static void onYamatoSheath(YamatoSheathEvent.Server event) {
      if (event.getEntityPatch() instanceof ServerPlayerPatch serverPlayerPatch) {
         SoulEntity.detonateForPlayer((Player)serverPlayerPatch.getOriginal());
         SkillContainer container = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
         if (container.getSkill() instanceof VergilSkill vergilSkill) {
            SheathInEvents.SheathAction action = resolve(event.getAnimation());
            if (action != null) {
               if (!ConcentrationManager.isConcentrationLocked(container)) {
                  if (action.stackDelta() != 0) {
                     vergilSkill.setStackSynchronize(container, container.getStack() + action.stackDelta());
                  }

                  if (action.resourceDelta() != 0.0F) {
                     vergilSkill.setConsumptionSynchronize(container, container.getResource() + action.resourceDelta());
                  }
               }

               if (DMCAnimationUtils.sameAccessor(event.getAnimation(), YamatoAnimations.YAMATO_COMBO_B_1)
                  || DMCAnimationUtils.sameAccessor(event.getAnimation(), YamatoAnimations.YAMATO_COMBO_B_2_SDT)) {
                  DMCSummonedSwordEntity.detonateAllImpale((LivingEntity)serverPlayerPatch.getOriginal());
               }

               if (DMCAnimationUtils.sameAccessor(event.getAnimation(), YamatoAnimations.YAMATO_RAPIDSLASH)
                  || DMCAnimationUtils.sameAccessor(event.getAnimation(), YamatoAnimations.YAMATO_RAPIDSLASH_RE)) {
                  DMCSummonedSwordEntity.detonateAllImpale((LivingEntity)serverPlayerPatch.getOriginal());
               }

               if (DMCAnimationUtils.sameAccessor(event.getAnimation(), YamatoAnimations.YAMATO_PROVOCATION_C)) {
                  for (LivingEntity target : serverPlayerPatch.getCurrentlyActuallyHitEntities()) {
                     target.m_21195_((MobEffect)DMCEffects.SLOW.get());
                  }

                  List<BlockPos> tntBlocks = consumeTntBlocks(((ServerPlayer)serverPlayerPatch.getOriginal()).m_20148_());
                  Level level = ((ServerPlayer)serverPlayerPatch.getOriginal()).m_9236_();

                  for (BlockPos pos : tntBlocks) {
                     level.m_254849_(
                        null, (double)pos.m_123341_() + 0.5, (double)pos.m_123342_() + 0.5, (double)pos.m_123343_() + 0.5, 4.0F, ExplosionInteraction.TNT
                     );
                     if (!SinDevilTriggerManager.isPlayerInSDT((Player)serverPlayerPatch.getOriginal())) {
                        serverPlayerPatch.playAnimationSynchronized(CustomStunAnimations.HIT_KNOCK_BACK_AIR_FAR, 0.0F);
                     }
                  }
               }

               if (DMCAnimationUtils.sameAccessor(event.getAnimation(), YamatoAnimations.YAMATO_COMBO_C_END)) {
                  for (LivingEntity target : serverPlayerPatch.getCurrentlyActuallyHitEntities()) {
                     target.m_21195_((MobEffect)DMCEffects.STOP.get());
                  }
               }

               JudgementCutEndAnimation judgementCutEndAnimation = DMCAnimationUtils.getRealAnimationAs(event.getAnimation(), JudgementCutEndAnimation.class);
               if (judgementCutEndAnimation != null) {
                  Set<LivingEntity> livingEntities = judgementCutEndAnimation.getSyncedEntities(event.getEntityPatch());
                  int hitCount = livingEntities.size();
                  vergilSkill.setConsumptionSynchronize(container, container.getResource() + (float)(hitCount * 90));
               }

               if (DMCAnimationUtils.sameAccessor(event.getAnimation(), YamatoAnimations.YAMATO_VOID_SLASH)) {
                  List<LivingEntity> livingEntities = serverPlayerPatch.getCurrentlyActuallyHitEntities();
                  int hitCount = livingEntities.size();
                  if (hitCount <= 0) {
                     return;
                  }

                  ServerPlayer player = (ServerPlayer)serverPlayerPatch.getOriginal();
                  Vec3 lookVec = player.m_20154_();
                  Vec3 targetPoint = player.m_20182_().m_82549_(lookVec.m_82490_(0.75));

                  for (LivingEntity target : livingEntities) {
                     if (target != null
                        && target.m_6084_()
                        && !(target instanceof DoppelgangerEntity)
                        && !(target instanceof JudgementCutEntity)
                        && !(target instanceof DMCSummonedSwordEntity)) {
                        target.m_21195_((MobEffect)DMCEffects.SLOW.get());
                        if (isNearGround(target)) {
                           Vec3 toTarget = targetPoint.m_82546_(target.m_20182_());
                           double dist = toTarget.m_165924_();
                           if (!(dist < 0.1)) {
                              double pullRatio = Math.min(0.95, 1.0 - Math.exp(-dist * 0.5));
                              toTarget = new Vec3(toTarget.f_82479_ * pullRatio, 0.0, toTarget.f_82481_ * pullRatio);
                              moveTargetSynchronized(target, target.m_20182_().m_82549_(toTarget));
                           }
                        }
                     }
                  }

                  vergilSkill.setConsumptionSynchronize(container, container.getResource() + (float)(hitCount * 35));
               }
            }
         }
      }
   }

   private static record SheathAction(int stackDelta, float resourceDelta) {
   }
}
