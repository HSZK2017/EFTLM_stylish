package com.dmc.invincible_dmc.gameassets.animations.yamato;

import com.dmc.invincible_dmc.entity.DMCEntities;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.judgementcut.JudgementCutEntity;
import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.PartEntity;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.HurtableEntityPatch;

public class YamatoVfxUtils {
   public static InTimeEvent summonJudgementCut(int startFrame) {
      return summonJudgementCut(startFrame, true);
   }

   public static InTimeEvent summonJudgementCut(int startFrame, boolean isPerfect) {
      byte fx = (byte)(isPerfect ? 1 : 2);
      float start = (float)startFrame / 60.0F;
      return InTimeEvent.create(
         start,
         (entityPatch, self, params) -> {
            LivingEntity original = (LivingEntity)entityPatch.getOriginal();
            Level level = original.f_19853_;
            Entity target = entityPatch.getTarget();
            if (!level.f_46443_ && target == null) {
               LivingEntity lastHurt = original.m_21214_();
               if (lastHurt != null
                  && lastHurt.m_6084_()
                  && lastHurt.m_9236_() == level
                  && original.m_20154_().m_82526_(lastHurt.m_20182_().m_82546_(original.m_146892_()).m_82541_()) > 0.0) {
                  target = lastHurt;
               }
            }

            boolean targetIsCrystal = false;
            if (!level.f_46443_ && target == null) {
               float yawRad = (float)Math.toRadians((double)(-original.m_146908_()));
               Vec3 lookDir = new Vec3(Math.sin((double)yawRad), 0.0, Math.cos((double)yawRad));
               Vec3 eyePos = original.m_146892_();
               float maxDist = 64.0F;
               List<EndCrystal> nearby = level.m_6443_(
                  EndCrystal.class,
                  original.m_20191_().m_82369_(lookDir.m_82490_((double)maxDist)).m_82400_(2.0),
                  crystal -> crystal.m_6084_() && !crystal.m_213877_() && crystal.m_20182_().m_82546_(eyePos).m_82541_().m_82526_(lookDir) > 0.5
               );
               if (!nearby.isEmpty()) {
                  target = (Entity)nearby.get(0);
                  targetIsCrystal = true;
               }
            }

            Vec3 spawnPos;
            if (target != null) {
               spawnPos = targetIsCrystal ? target.m_20182_().m_82520_(0.0, 0.5, 0.0) : hitPos(target);
            } else {
               float yawRad = (float)Math.toRadians((double)(-original.m_146908_()));
               float forward = 4.95F;
               spawnPos = original.m_20182_().m_82520_(Math.sin((double)yawRad) * (double)forward, 1.4F, Math.cos((double)yawRad) * (double)forward);
            }

            spawnJudgementCut(level, original, spawnPos, fx, isPerfect);
            if (!level.f_46443_ && SinDevilTriggerManager.isLivingInSDT(original)) {
               for (Entity bonusTarget : findBonusTargets(level, original, target, spawnPos)) {
                  spawnJudgementCut(level, original, hitPos(bonusTarget), fx, isPerfect);
               }
            }
         },
         Side.SERVER
      );
   }

   public static InTimeEvent summonRapidSlash(int startFrame) {
      float start = (float)startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         LivingEntity original = (LivingEntity)entityPatch.getOriginal();
         Level level = original.f_19853_;
         if (!level.f_46443_) {
            Vec3 spawnPos = original.m_20182_().m_82520_(0.0, 0.5, 0.0);
            JudgementCutEntity entity = new JudgementCutEntity((EntityType<? extends Mob>)DMCEntities.JUDGEMENT_CUT.get(), original, level);
            entity.setClientFx((byte)0);
            entity.setRapidSlash(true);
            entity.m_146884_(spawnPos);
            level.m_7967_(entity);
         }
      }, Side.SERVER);
   }

   private static Vec3 hitPos(Entity entity) {
      if (entity instanceof PartEntity<?> part) {
         return part.m_20191_().m_82399_();
      } else {
         return entity instanceof LivingEntity living
            ? living.m_20182_().m_82520_(0.0, (double)(living.m_20206_() * 0.65F), 0.0)
            : entity.m_20191_().m_82399_();
      }
   }

   private static void spawnJudgementCut(Level level, LivingEntity original, Vec3 pos, byte clientFx, boolean isPerfect) {
      JudgementCutEntity judgementCutEntity = new JudgementCutEntity((EntityType<? extends Mob>)DMCEntities.JUDGEMENT_CUT.get(), original, level);
      judgementCutEntity.setClientFx(clientFx);
      judgementCutEntity.setPerfect(isPerfect);
      judgementCutEntity.setSpawnTearEffect(true);
      judgementCutEntity.m_146884_(pos);
      level.m_7967_(judgementCutEntity);
   }

   public static boolean spawnPerfectJudgementCut(ServerLevel level, LivingEntity owner, Vec3 position) {
      if (owner != null && owner.m_6084_()) {
         BlockPos blockPos = BlockPos.m_274446_(position);
         if (!level.m_46805_(blockPos)) {
            return false;
         } else {
            JudgementCutEntity entity = new JudgementCutEntity((EntityType<? extends Mob>)DMCEntities.JUDGEMENT_CUT.get(), owner, level);
            entity.setClientFx((byte)1);
            entity.setPerfect(true);
            entity.m_6034_(position.f_82479_, position.f_82480_, position.f_82481_);
            return level.m_7967_(entity);
         }
      } else {
         return false;
      }
   }

   private static List<Entity> findBonusTargets(Level level, LivingEntity owner, Entity primaryTarget, Vec3 spawnPos) {
      LivingEntity primaryParent = primaryTarget != null ? getLivingParent(primaryTarget) : null;
      List<Entity> nearby = level.m_6443_(Entity.class, new AABB(spawnPos, spawnPos).m_82400_(16.0), ex -> {
         if (ex == owner || ex == primaryTarget || !ex.m_6084_() || ex.m_213877_() || ex.m_5833_()) {
            return false;
         } else if (!(ex instanceof JudgementCutEntity) && !(ex instanceof DoppelgangerEntity) && !(ex instanceof DMCSummonedSwordEntity)) {
            LivingEntity parentx = getLivingParent(ex);
            return parentx != null && parentx != owner ? EpicFightCapabilities.getEntityPatch(parentx, HurtableEntityPatch.class) != null : false;
         } else {
            return false;
         }
      });
      nearby.sort(Comparator.comparingDouble(ex -> ex.m_20238_(spawnPos)));
      List<Entity> filtered = new ArrayList<>();
      Set<LivingEntity> targetedParents = new HashSet<>();
      if (primaryParent != null) {
         targetedParents.add(primaryParent);
      }

      for (Entity e : nearby) {
         LivingEntity parent = getLivingParent(e);
         if (parent != null && targetedParents.add(parent)) {
            filtered.add(e);
            if (filtered.size() >= 2) {
               break;
            }
         }
      }

      if (filtered.size() < 2) {
         for (Entity ex : nearby) {
            if (!filtered.contains(ex)) {
               filtered.add(ex);
               if (filtered.size() >= 2) {
                  break;
               }
            }
         }
      }

      return filtered;
   }

   private static LivingEntity getLivingParent(Entity entity) {
      if (entity instanceof LivingEntity) {
         return (LivingEntity)entity;
      } else {
         if (entity instanceof PartEntity<?> part) {
            Entity var3 = part.getParent();
            if (var3 instanceof LivingEntity) {
               return (LivingEntity)var3;
            }
         }

         return null;
      }
   }
}
