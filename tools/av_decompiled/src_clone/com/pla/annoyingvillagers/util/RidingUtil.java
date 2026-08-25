package com.pla.annoyingvillagers.util;

import com.pla.annoyingvillagers.entity.BlueVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.GreenVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.PurpleVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.RedVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.VillagerScoutCaptainEntity;
import com.pla.annoyingvillagers.entity.VillagerScoutEntity;
import java.util.List;
import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;

public class RidingUtil {
   public static void rideRandomAnimal(ServerLevel serverLevel, Entity entity) {
      List<EntityType<? extends LivingEntity>> pool = List.of(
         EntityType.f_20457_, EntityType.f_20560_, EntityType.f_243976_, EntityType.f_20514_, EntityType.f_20557_
      );
      Random rand = new Random();
      EntityType<? extends LivingEntity> type = pool.get(rand.nextInt(pool.size()));
      LivingEntity mount = (LivingEntity)type.m_20615_(serverLevel);
      if (mount != null) {
         mount.m_7678_(entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), entity.m_146908_(), entity.m_146909_());
         ((Mob)mount).m_21530_();
         ((Mob)mount).m_6518_(serverLevel, serverLevel.m_6436_(entity.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
         if (entity instanceof VillagerScoutEntity
            || entity instanceof VillagerScoutCaptainEntity
            || entity instanceof RedVillagerKnightEntity
            || entity instanceof BlueVillagerKnightEntity
            || entity instanceof GreenVillagerKnightEntity
            || entity instanceof PurpleVillagerKnightEntity) {
            TeamUtil.addOrJoinTeam(mount, "villagers");
         }

         serverLevel.m_7967_(mount);
         entity.m_20329_(mount);
         mount.m_7292_(new MobEffectInstance(MobEffects.f_19606_, 99999, new Random().nextInt(1, 3), false, false));
         mount.m_7292_(new MobEffectInstance(MobEffects.f_19605_, 99999, 1, false, false));
         mount.m_7292_(new MobEffectInstance(MobEffects.f_19596_, 99999, new Random().nextInt(1, 9), false, false));
      }
   }
}
