package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.entity.BlackFireEntity;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;

public class BlackFireSwordItem extends SwordItem {
   private static final int BLACK_FIRE_FALLBACK_LOOKUP_TICKS = 80;
   private static final Map<Integer, Long> ACTIVE_BLACK_FIRE_FALLBACKS = new HashMap<>();
   private static Level blackFireFallbackLevel;
   private static final DustParticleOptions BLACK_FIRE_DUST = new DustParticleOptions(new Vector3f(0.03F, 0.03F, 0.035F), 1.35F);
   private static final DustParticleOptions BLACK_FIRE_FLASH_DUST = new DustParticleOptions(new Vector3f(0.85F, 0.9F, 0.8F), 0.9F);

   public BlackFireSwordItem() {
      super(new Tier() {
         public int m_6609_() {
            return 1561;
         }

         public float m_6624_() {
            return 6.0F;
         }

         public float m_6631_() {
            return 3.5F;
         }

         public int m_6604_() {
            return 5;
         }

         public int m_6601_() {
            return 21;
         }

         @NotNull
         public Ingredient m_6282_() {
            return Ingredient.m_43927_(new ItemStack[]{new ItemStack(Items.f_42415_)});
         }
      }, 3, -2.1F, new Properties());
   }

   public static Vec3 getSwordOrBodyPosition(Entity entity) {
      try {
         Vec3 pos = EpicfightUtil.getJointWithTranslation(entity, new Vec3f(0.0F, 0.0F, 0.0F), ((HumanoidArmature)Armatures.BIPED.get()).toolR, 1.0F, 0.0);
         if (pos != null) {
            return pos;
         }
      } catch (Exception var2) {
      }

      return entity.m_20182_().m_82520_(0.0, (double)entity.m_20206_() * 0.65, 0.0);
   }

   public static Vec3 getBlackFireFallbackPosition(Entity entity) {
      if (entity instanceof BlackFireEntity blackFire) {
         if (blackFire.isFollowOwnerSwordMode()) {
            Entity owner = blackFire.getOwnerEntity();
            if (owner != null && owner.m_6084_() && !owner.m_213877_()) {
               return getSwordOrBodyPosition(owner);
            }
         }

         return blackFire.m_20182_();
      } else {
         return getSwordOrBodyPosition(entity);
      }
   }

   public static void startBlackFireFallback(Level level, int entityId) {
      if (level != null && level.m_5776_()) {
         resetBlackFireFallbacks(level);
         ACTIVE_BLACK_FIRE_FALLBACKS.put(entityId, level.m_46467_() + 80L);
         Entity entity = level.m_6815_(entityId);
         if (entity != null && entity.m_6084_() && !entity.m_213877_()) {
            spawnBlackFireFallback(level, entity, true);
         }
      }
   }

   public static void spawnBlackFireFallback(Level level, Entity entity) {
      spawnBlackFireFallback(level, entity, false);
   }

   public static void tickBlackFireFallbacks(Level level) {
      if (level == null) {
         ACTIVE_BLACK_FIRE_FALLBACKS.clear();
         blackFireFallbackLevel = null;
      } else {
         resetBlackFireFallbacks(level);
         if (!ACTIVE_BLACK_FIRE_FALLBACKS.isEmpty()) {
            long now = level.m_46467_();
            Iterator<Entry<Integer, Long>> iterator = ACTIVE_BLACK_FIRE_FALLBACKS.entrySet().iterator();

            while (iterator.hasNext()) {
               Entry<Integer, Long> active = iterator.next();
               Entity entity = level.m_6815_(active.getKey());
               if (entity == null) {
                  if (now > active.getValue()) {
                     iterator.remove();
                  }
               } else if (entity.m_6084_() && !entity.m_213877_()) {
                  spawnBlackFireFallback(level, entity);
               } else {
                  iterator.remove();
               }
            }
         }
      }
   }

   private static void resetBlackFireFallbacks(Level level) {
      if (blackFireFallbackLevel != level) {
         ACTIVE_BLACK_FIRE_FALLBACKS.clear();
         blackFireFallbackLevel = level;
      }
   }

   private static void spawnBlackFireFallback(Level level, Entity entity, boolean burst) {
      if (level != null && entity != null) {
         RandomSource rand = level.m_213780_();
         Vec3 center = getBlackFireFallbackPosition(entity);
         double radius = Math.max(0.35, (double)entity.m_20205_() * 0.85);
         double height = Math.max(0.45, (double)entity.m_20206_() * 0.75);
         int ringParticles = burst ? 54 : 16;
         int coreParticles = burst ? 12 : 4;

         for (int i = 0; i < ringParticles; i++) {
            double angle = (double)i / (double)ringParticles * Math.PI * 2.0 + rand.m_188500_() * 0.35;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double ringRadius = radius * (0.55 + rand.m_188500_() * 0.75);
            Vec3 outward = new Vec3(cos, 0.0, sin);
            Vec3 tangent = new Vec3(-sin, 0.0, cos);
            Vec3 pos = center.m_82549_(outward.m_82490_(ringRadius)).m_82520_(0.0, (rand.m_188500_() - 0.35) * height, 0.0);
            Vec3 velocity = tangent.m_82490_(0.035 + rand.m_188500_() * 0.055)
               .m_82549_(outward.m_82490_((rand.m_188500_() - 0.45) * 0.035))
               .m_82520_(0.0, 0.025 + rand.m_188500_() * 0.055, 0.0);
            spawnParticle(level, rand.m_188499_() ? ParticleTypes.f_123762_ : ParticleTypes.f_123755_, pos, velocity);
            if ((i & 3) == 0) {
               spawnParticle(level, BLACK_FIRE_DUST, pos, velocity.m_82490_(0.35));
            }

            if (i % 5 == 0) {
               spawnParticle(level, ParticleTypes.f_123745_, pos, velocity.m_82490_(0.45));
            }
         }

         for (int i = 0; i < coreParticles; i++) {
            Vec3 offset = randomUnit(rand).m_82490_(rand.m_188500_() * radius * 0.45);
            Vec3 posx = center.m_82549_(offset);
            Vec3 velocityx = offset.m_82490_(0.03).m_82520_(0.0, 0.04 + rand.m_188500_() * 0.06, 0.0);
            spawnParticle(level, BLACK_FIRE_FLASH_DUST, posx, velocityx);
            if ((i & 1) == 0) {
               spawnParticle(level, ParticleTypes.f_123759_, posx, velocityx.m_82490_(0.55));
            }
         }
      }
   }

   private static void spawnParticle(Level level, ParticleOptions particle, Vec3 pos, Vec3 velocity) {
      level.m_6493_(particle, true, pos.f_82479_, pos.f_82480_, pos.f_82481_, velocity.f_82479_, velocity.f_82480_, velocity.f_82481_);
   }

   private static Vec3 randomUnit(RandomSource rand) {
      double z = rand.m_188500_() * 2.0 - 1.0;
      double angle = rand.m_188500_() * Math.PI * 2.0;
      double radius = Math.sqrt(Math.max(0.0, 1.0 - z * z));
      return new Vec3(radius * Math.cos(angle), z, radius * Math.sin(angle));
   }

   @OnlyIn(Dist.CLIENT)
   @EventBusSubscriber(
      modid = "annoyingvillagers",
      value = {Dist.CLIENT}
   )
   public static final class ClientEvents {
      private ClientEvents() {
      }

      @SubscribeEvent
      public static void onClientTick(ClientTickEvent event) {
         if (event.phase == Phase.END) {
            BlackFireSwordItem.tickBlackFireFallbacks(Minecraft.m_91087_().f_91073_);
         }
      }
   }
}
