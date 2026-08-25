package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.gameasset.AVSkills;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import com.pla.annoyingvillagers.skill.EnderGlaiveSkill;
import com.pla.annoyingvillagers.util.HerobrineUtil;
import java.util.List;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class EnderGlaiveItem extends SwordItem {
   public EnderGlaiveItem() {
      super(new Tier() {
         public int m_6609_() {
            return 1561;
         }

         public float m_6624_() {
            return 4.0F;
         }

         public float m_6631_() {
            return 5.0F;
         }

         public int m_6604_() {
            return 1;
         }

         public int m_6601_() {
            return 2;
         }

         @NotNull
         public Ingredient m_6282_() {
            return Ingredient.m_43929_(new ItemLike[]{(ItemLike)AnnoyingVillagersModItems.ELITE_OBSIDIAN.get()});
         }
      }, 3, -2.5F, new Properties().m_41486_());
   }

   public static void spawnExplosionFallback(Level level, Vec3 center) {
      if (level != null && center != null) {
         RandomSource rand = level.m_213780_();
         level.m_6493_(
            (ParticleOptions)AnnoyingVillagersModParticleTypes.FIREBALL.get(), true, center.f_82479_, center.f_82480_, center.f_82481_, 5.0, 1.0, 0.0
         );

         for (int i = 0; i < 6; i++) {
            Vec3 normal = randomUnit(rand);
            spawnRing3d(level, rand, center, normal, 52, 2.0, 0.1, 0.12, 0.035);
            spawnRing3d(level, rand, center, normal, 60, 2.8, 0.14, 0.11, 0.03);
         }
      }
   }

   private static void spawnRing3d(
      Level level, RandomSource rand, Vec3 center, Vec3 normal, int points, double radius, double thickness, double tangentialSpeed, double outwardSpeed
   ) {
      Vec3 n = normal.m_82541_();
      Vec3 u = n.m_82537_(new Vec3(0.0, 1.0, 0.0));
      if (u.m_82556_() < 1.0E-6) {
         u = n.m_82537_(new Vec3(1.0, 0.0, 0.0));
      }

      u = u.m_82541_();
      Vec3 v = n.m_82537_(u).m_82541_();

      for (int i = 0; i < points; i++) {
         double angle = (double)i / (double)points * (Math.PI * 2) + rand.m_188500_() * 0.1;
         double cos = Math.cos(angle);
         double sin = Math.sin(angle);
         Vec3 radial = u.m_82490_(cos).m_82549_(v.m_82490_(sin));
         Vec3 tangent = n.m_82537_(radial).m_82541_();
         Vec3 pos = center.m_82549_(radial.m_82490_(radius)).m_82549_(n.m_82490_((rand.m_188500_() - 0.5) * 2.0 * thickness));
         Vec3 velocity = tangent.m_82490_(tangentialSpeed)
            .m_82549_(radial.m_82490_(outwardSpeed))
            .m_82520_((rand.m_188500_() - 0.5) * 0.02, (rand.m_188500_() - 0.5) * 0.02, (rand.m_188500_() - 0.5) * 0.02);
         level.m_6493_(
            (ParticleOptions)AnnoyingVillagersModParticleTypes.ENDER.get(),
            true,
            pos.f_82479_,
            pos.f_82480_,
            pos.f_82481_,
            velocity.f_82479_,
            velocity.f_82480_,
            velocity.f_82481_
         );
         if ((i & 3) == 0) {
            level.m_6493_(
               ParticleTypes.f_123789_,
               true,
               pos.f_82479_,
               pos.f_82480_,
               pos.f_82481_,
               velocity.f_82479_ * 0.35,
               velocity.f_82480_ * 0.2,
               velocity.f_82481_ * 0.35
            );
         }
      }
   }

   private static Vec3 randomUnit(RandomSource rand) {
      double z = rand.m_188500_() * 2.0 - 1.0;
      double angle = rand.m_188500_() * Math.PI * 2.0;
      double radius = Math.sqrt(Math.max(0.0, 1.0 - z * z));
      return new Vec3(radius * Math.cos(angle), z, radius * Math.sin(angle));
   }

   public boolean m_7579_(@NotNull ItemStack pStack, @NotNull LivingEntity pTarget, @NotNull LivingEntity pAttacker) {
      if (pAttacker instanceof Player player) {
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         if (playerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillContainer skillContainer = serverPlayerPatch.getSkill(AVSkills.ENDER_GLAIVE);
            if (skillContainer == null) {
               return super.m_7579_(pStack, pTarget, pAttacker);
            }

            EnderGlaiveSkill enderGlaiveSkill = (EnderGlaiveSkill)skillContainer.getSkill();
            float currentResource = skillContainer.getResource();
            float neededResource = skillContainer.getNeededResource();
            float addResource = Math.min(2.0F, neededResource);
            enderGlaiveSkill.setConsumptionSynchronize(skillContainer, currentResource + addResource);
         }
      }

      return super.m_7579_(pStack, pTarget, pAttacker);
   }

   public void m_6883_(@NotNull ItemStack itemstack, @NotNull Level level, @NotNull Entity entity, int i, boolean flag) {
      super.m_6883_(itemstack, level, entity, i, flag);
      if (flag && entity instanceof Player player) {
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         if (playerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillContainer skillContainer = serverPlayerPatch.getSkill(AVSkills.ENDER_GLAIVE);
            if (skillContainer != null && skillContainer.getStack() >= 1) {
               HerobrineUtil.spawnEliteEffect(level, entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), entity);
            }
         }
      }
   }

   public void m_7373_(ItemStack itemstack, Level level, List<Component> list, TooltipFlag tooltipflag) {
      super.m_7373_(itemstack, level, list, tooltipflag);
      list.add(Component.m_237113_(Component.m_237115_("tooltip.annoyingvillagers.ender_glaive").getString()));
   }
}
