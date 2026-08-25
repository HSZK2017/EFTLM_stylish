package com.pla.annoyingvillagers.util;

import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.clazz.VillagerArmyEntity;
import com.pla.annoyingvillagers.entity.AlexEntity;
import com.pla.annoyingvillagers.entity.AngrySteveEntity;
import com.pla.annoyingvillagers.entity.BlueDemonEntity;
import com.pla.annoyingvillagers.entity.BlueVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.ChrisEntity;
import com.pla.annoyingvillagers.entity.EliteHerobrineKnockedEntity;
import com.pla.annoyingvillagers.entity.GreenVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.HerobrineGregEntity;
import com.pla.annoyingvillagers.entity.JevEntity;
import com.pla.annoyingvillagers.entity.LowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.LowShadowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.PlayerNpcEntity;
import com.pla.annoyingvillagers.entity.PurpleVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.RedVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.SteveEntity;
import com.pla.annoyingvillagers.entity.TransporterHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.VillagerScoutCaptainEntity;
import com.pla.annoyingvillagers.entity.VillagerScoutEntity;
import com.pla.annoyingvillagers.entity.goal.PortalApproachGoal;
import com.pla.annoyingvillagers.util.CommonGoals.1;
import com.pla.annoyingvillagers.util.CommonGoals.2;
import com.pla.annoyingvillagers.util.CommonGoals.3;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import se.gory_moon.player_mobs.entity.PlayerMobEntity;

public class CommonGoals {
   private static boolean hasCombatTarget(Mob mob) {
      LivingEntity target = mob.m_5448_();
      return target != null && target.m_6084_();
   }

   public static void registerGoalForHostileNpc(Monster monster) {
      monster.m_21573_().m_26575_().m_77355_(true);
      monster.f_21346_.m_25352_(1, new HurtByTargetGoal(monster, new Class[0]));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, Player.class, true, false));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, PlayerNpcEntity.class, true, false));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, SteveEntity.class, true, false));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, AngrySteveEntity.class, true, false));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, ChrisEntity.class, true, false));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, AlexEntity.class, true, false));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, JevEntity.class, true, false));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, Villager.class, true, false));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, IronGolem.class, true, false));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, BlueDemonEntity.class, true, false));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, VillagerScoutEntity.class, true, false));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, VillagerScoutCaptainEntity.class, true, false));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, RedVillagerKnightEntity.class, true, false));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, BlueVillagerKnightEntity.class, true, false));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, GreenVillagerKnightEntity.class, true, false));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, PurpleVillagerKnightEntity.class, true, false));
      if (!(monster instanceof TransporterHerobrineCloneEntity)) {
         monster.f_21345_.m_25352_(0, new PortalApproachGoal(monster));
         monster.f_21345_.m_25352_(2, new MeleeAttackGoal(monster, 1.2, false));
      }

      monster.f_21345_.m_25352_(3, new RandomStrollGoal(monster, 1.0));
      monster.f_21345_.m_25352_(4, new RandomLookAroundGoal(monster));
      monster.f_21345_.m_25352_(5, new FloatGoal(monster));
   }

   public static void registerGoalForBlueDemonNpc(Monster monster) {
      monster.m_21573_().m_26575_().m_77355_(true);
      monster.f_21346_.m_25352_(1, new HurtByTargetGoal(monster, new Class[0]));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, HerobrineMob.class, true, false));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, LowHerobrineCloneEntity.class, true, false));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, LowShadowHerobrineCloneEntity.class, true, false));
      monster.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(monster, EliteHerobrineKnockedEntity.class, true, false));
      monster.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(monster, Player.class, true, false));
      monster.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(monster, PlayerNpcEntity.class, true, false));
      monster.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(monster, AngrySteveEntity.class, true, false));
      monster.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(monster, Villager.class, true, false));
      monster.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(monster, VillagerScoutEntity.class, true, false));
      monster.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(monster, VillagerScoutCaptainEntity.class, true, false));
      monster.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(monster, RedVillagerKnightEntity.class, true, false));
      monster.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(monster, BlueVillagerKnightEntity.class, true, false));
      monster.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(monster, GreenVillagerKnightEntity.class, true, false));
      monster.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(monster, PurpleVillagerKnightEntity.class, true, false));
      monster.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(monster, AbstractIllager.class, true, false));
      monster.f_21345_.m_25352_(3, new MeleeAttackGoal(monster, 1.2, false));
      monster.f_21345_.m_25352_(4, new RandomStrollGoal(monster, 1.0));
      monster.f_21345_.m_25352_(5, new RandomLookAroundGoal(monster));
      monster.f_21345_.m_25352_(6, new FloatGoal(monster));
   }

   public static void registerGoalForVillagerKnightNpc(PathfinderMob mob) {
      mob.m_21573_().m_26575_().m_77355_(true);
      if (!(mob instanceof VillagerArmyEntity)) {
         mob.f_21346_.m_25352_(1, new HurtByTargetGoal(mob, new Class[0]).m_26044_(new Class[0]));
      }

      mob.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(mob, HerobrineMob.class, true, false));
      mob.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(mob, LowHerobrineCloneEntity.class, true, false));
      mob.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(mob, LowShadowHerobrineCloneEntity.class, true, false));
      mob.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(mob, PlayerNpcEntity.class, true, false));
      mob.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(mob, Player.class, true, false));
      mob.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(mob, Monster.class, true, false));
      mob.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(mob, AbstractIllager.class, true, false));
      mob.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(mob, BlueDemonEntity.class, true, false));
      mob.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(mob, SteveEntity.class, true, false));
      mob.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(mob, AngrySteveEntity.class, true, false));
      mob.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(mob, AlexEntity.class, true, false));
      mob.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(mob, ChrisEntity.class, true, false));
      mob.f_21345_.m_25352_(5, new MeleeAttackGoal(mob, 1.2, false));
      mob.f_21345_.m_25352_(6, new 1(mob, 1.0));
      mob.f_21345_.m_25352_(7, new 2(mob, 1.3, 20.0F, 15.0F, mob));
      mob.f_21345_.m_25352_(8, new OpenDoorGoal(mob, true));
      mob.f_21345_.m_25352_(9, new OpenDoorGoal(mob, false));
      mob.f_21345_.m_25352_(10, new 3(mob, mob));
      mob.f_21345_.m_25352_(11, new FloatGoal(mob));
   }

   public static void registerGoalForNeutralNpc(PathfinderMob mob) {
      mob.m_21573_().m_26575_().m_77355_(true);
      mob.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(mob, HerobrineMob.class, true, false));
      mob.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(mob, LowHerobrineCloneEntity.class, true, false));
      mob.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(mob, LowShadowHerobrineCloneEntity.class, true, false));
      mob.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(mob, EliteHerobrineKnockedEntity.class, true, false));
      mob.f_21346_.m_25352_(2, new HurtByTargetGoal(mob, new Class[0]));
      mob.f_21345_.m_25352_(2, new MeleeAttackGoal(mob, 1.2, false));
      if (!(mob.m_5448_() instanceof VillagerScoutEntity)) {
         mob.f_21345_.m_25352_(2, new AvoidEntityGoal(mob, VillagerScoutEntity.class, 12.0F, 1.2, 1.4));
      }

      if (!(mob.m_5448_() instanceof VillagerScoutCaptainEntity)) {
         mob.f_21345_.m_25352_(2, new AvoidEntityGoal(mob, VillagerScoutCaptainEntity.class, 12.0F, 1.2, 1.4));
      }

      if (!(mob.m_5448_() instanceof BlueVillagerKnightEntity)) {
         mob.f_21345_.m_25352_(2, new AvoidEntityGoal(mob, BlueVillagerKnightEntity.class, 12.0F, 1.2, 1.4));
      }

      if (!(mob.m_5448_() instanceof GreenVillagerKnightEntity)) {
         mob.f_21345_.m_25352_(2, new AvoidEntityGoal(mob, GreenVillagerKnightEntity.class, 12.0F, 1.2, 1.4));
      }

      if (!(mob.m_5448_() instanceof RedVillagerKnightEntity)) {
         mob.f_21345_.m_25352_(2, new AvoidEntityGoal(mob, RedVillagerKnightEntity.class, 12.0F, 1.2, 1.4));
      }

      if (!(mob.m_5448_() instanceof PurpleVillagerKnightEntity)) {
         mob.f_21345_.m_25352_(2, new AvoidEntityGoal(mob, PurpleVillagerKnightEntity.class, 12.0F, 1.2, 1.4));
      }

      mob.f_21346_
         .m_25352_(
            3,
            new NearestAttackableTargetGoal(mob, Monster.class, false, target -> !(target instanceof PlayerMobEntity) && !(target instanceof BlueDemonEntity))
         );
      mob.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(mob, AbstractIllager.class, true, false));
      mob.f_21345_.m_25352_(3, new MeleeAttackGoal(mob, 1.2, false));
      mob.f_21345_.m_25352_(4, new RandomStrollGoal(mob, 1.0));
      mob.f_21345_.m_25352_(5, new OpenDoorGoal(mob, true));
      mob.f_21346_.m_25352_(6, new HurtByTargetGoal(mob, new Class[0]));
      mob.f_21345_.m_25352_(7, new OpenDoorGoal(mob, false));
      mob.f_21345_.m_25352_(8, new RandomLookAroundGoal(mob));
      mob.f_21345_.m_25352_(9, new FloatGoal(mob));
   }

   public static void registerGoalForCrazyNpc(PathfinderMob mob) {
      mob.m_21573_().m_26575_().m_77355_(true);
      mob.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(mob, Monster.class, false, false));
      mob.f_21346_.m_25352_(1, new HurtByTargetGoal(mob, new Class[0]));
      mob.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(mob, HerobrineMob.class, true, false));
      mob.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(mob, LowHerobrineCloneEntity.class, true, false));
      mob.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(mob, LowShadowHerobrineCloneEntity.class, true, false));
      mob.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(mob, PlayerNpcEntity.class, true, false));
      mob.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(mob, VillagerScoutEntity.class, false, false));
      mob.f_21346_.m_25352_(4, new NearestAttackableTargetGoal(mob, VillagerScoutCaptainEntity.class, false, false));
      mob.f_21346_.m_25352_(5, new NearestAttackableTargetGoal(mob, RedVillagerKnightEntity.class, false, false));
      mob.f_21346_.m_25352_(6, new NearestAttackableTargetGoal(mob, BlueVillagerKnightEntity.class, false, false));
      mob.f_21346_.m_25352_(7, new NearestAttackableTargetGoal(mob, GreenVillagerKnightEntity.class, false, false));
      mob.f_21346_.m_25352_(8, new NearestAttackableTargetGoal(mob, PurpleVillagerKnightEntity.class, false, false));
      mob.f_21346_.m_25352_(6, new NearestAttackableTargetGoal(mob, BlueDemonEntity.class, false, false));
      mob.f_21346_.m_25352_(8, new NearestAttackableTargetGoal(mob, EliteHerobrineKnockedEntity.class, true, false));
      mob.f_21346_.m_25352_(20, new NearestAttackableTargetGoal(mob, AlexEntity.class, false, false));
      mob.f_21346_.m_25352_(20, new NearestAttackableTargetGoal(mob, ChrisEntity.class, false, false));
      mob.f_21346_.m_25352_(21, new NearestAttackableTargetGoal(mob, Player.class, true, true));
      mob.f_21345_.m_25352_(22, new MeleeAttackGoal(mob, 1.2, false));
      mob.f_21345_.m_25352_(23, new RandomStrollGoal(mob, 1.0));
      mob.f_21345_.m_25352_(24, new RandomLookAroundGoal(mob));
      mob.f_21345_.m_25352_(25, new FloatGoal(mob));
   }

   public static void attackAllMonstersGoals(PlayerNpcEntity playerMobEntity) {
      playerMobEntity.f_21346_.m_25352_(1, new NearestAttackableTargetGoal(playerMobEntity, HerobrineMob.class, true, false));
      playerMobEntity.f_21346_
         .m_25352_(4, new NearestAttackableTargetGoal(playerMobEntity, Monster.class, true, target -> !(target instanceof PlayerMobEntity)));
      playerMobEntity.f_21346_.m_25352_(4, new NearestAttackableTargetGoal(playerMobEntity, AbstractIllager.class, true, false));
      playerMobEntity.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(playerMobEntity, LowHerobrineCloneEntity.class, true, false));
      playerMobEntity.f_21346_.m_25352_(2, new NearestAttackableTargetGoal(playerMobEntity, LowShadowHerobrineCloneEntity.class, true, false));
      playerMobEntity.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(playerMobEntity, BlueDemonEntity.class, true));
      playerMobEntity.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(playerMobEntity, EliteHerobrineKnockedEntity.class, true));
   }

   public static void runAwayFromHerobrineGoals(PathfinderMob playerMobEntity, float distance) {
      if (!(playerMobEntity.m_5448_() instanceof HerobrineMob)) {
         playerMobEntity.f_21345_.m_25352_(1, new AvoidEntityGoal(playerMobEntity, HerobrineMob.class, distance, 1.2, 1.4));
      }

      if (!(playerMobEntity.m_5448_() instanceof HerobrineGregEntity)) {
         playerMobEntity.f_21345_.m_25352_(1, new AvoidEntityGoal(playerMobEntity, HerobrineGregEntity.class, distance, 1.2, 1.4));
      }

      if (!(playerMobEntity.m_5448_() instanceof LowHerobrineCloneEntity)) {
         playerMobEntity.f_21345_.m_25352_(1, new AvoidEntityGoal(playerMobEntity, LowHerobrineCloneEntity.class, distance, 1.2, 1.4));
      }

      if (!(playerMobEntity.m_5448_() instanceof LowShadowHerobrineCloneEntity)) {
         playerMobEntity.f_21345_.m_25352_(1, new AvoidEntityGoal(playerMobEntity, LowShadowHerobrineCloneEntity.class, distance, 1.2, 1.4));
      }
   }

   public static void runAwayFromVillagerArmyGoals(PlayerMobEntity playerMobEntity) {
      if (!(playerMobEntity.m_5448_() instanceof VillagerScoutEntity)) {
         playerMobEntity.f_21345_.m_25352_(1, new AvoidEntityGoal(playerMobEntity, VillagerScoutEntity.class, 12.0F, 1.2, 1.4));
      }

      if (!(playerMobEntity.m_5448_() instanceof VillagerScoutCaptainEntity)) {
         playerMobEntity.f_21345_.m_25352_(1, new AvoidEntityGoal(playerMobEntity, VillagerScoutCaptainEntity.class, 12.0F, 1.2, 1.4));
      }

      if (!(playerMobEntity.m_5448_() instanceof BlueVillagerKnightEntity)) {
         playerMobEntity.f_21345_.m_25352_(1, new AvoidEntityGoal(playerMobEntity, BlueVillagerKnightEntity.class, 12.0F, 1.2, 1.4));
      }

      if (!(playerMobEntity.m_5448_() instanceof GreenVillagerKnightEntity)) {
         playerMobEntity.f_21345_.m_25352_(1, new AvoidEntityGoal(playerMobEntity, GreenVillagerKnightEntity.class, 12.0F, 1.2, 1.4));
      }

      if (!(playerMobEntity.m_5448_() instanceof RedVillagerKnightEntity)) {
         playerMobEntity.f_21345_.m_25352_(1, new AvoidEntityGoal(playerMobEntity, RedVillagerKnightEntity.class, 12.0F, 1.2, 1.4));
      }

      if (!(playerMobEntity.m_5448_() instanceof PurpleVillagerKnightEntity)) {
         playerMobEntity.f_21345_.m_25352_(1, new AvoidEntityGoal(playerMobEntity, PurpleVillagerKnightEntity.class, 12.0F, 1.2, 1.4));
      }
   }

   public static void attackAllNpcGoals(Mob playerMobEntity) {
      playerMobEntity.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(playerMobEntity, AlexEntity.class, true));
      playerMobEntity.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(playerMobEntity, JevEntity.class, true));
      playerMobEntity.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(playerMobEntity, ChrisEntity.class, true));
      playerMobEntity.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(playerMobEntity, SteveEntity.class, true));
      playerMobEntity.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(playerMobEntity, AngrySteveEntity.class, true));
   }

   public static void attackAllVillagerArmyGoal(Mob playerMobEntity) {
      playerMobEntity.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(playerMobEntity, VillagerScoutEntity.class, true));
      playerMobEntity.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(playerMobEntity, VillagerScoutCaptainEntity.class, true));
      playerMobEntity.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(playerMobEntity, RedVillagerKnightEntity.class, true));
      playerMobEntity.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(playerMobEntity, BlueVillagerKnightEntity.class, true));
      playerMobEntity.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(playerMobEntity, GreenVillagerKnightEntity.class, true));
      playerMobEntity.f_21346_.m_25352_(3, new NearestAttackableTargetGoal(playerMobEntity, PurpleVillagerKnightEntity.class, true));
   }
}
