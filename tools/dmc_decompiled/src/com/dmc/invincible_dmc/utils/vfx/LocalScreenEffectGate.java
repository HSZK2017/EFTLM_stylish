package com.dmc.invincible_dmc.utils.vfx;

import com.guhao.vix.client.event.ScreenEffectEngine;
import com.guhao.vix.client.screeneffect.ScreenEffectBase;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public final class LocalScreenEffectGate {
   private LocalScreenEffectGate() {
   }

   public static void pushNearby(LivingEntityPatch<?> source, double radius, ScreenEffectBase effect) {
      if (source != null) {
         pushNearby(source.getOriginal(), radius, effect);
      }
   }

   public static void pushNearby(Entity source, double radius, ScreenEffectBase effect) {
      if (source != null && isWithinRange(source.m_9236_(), source.m_20182_(), radius)) {
         ScreenEffectEngine.PushScreenEffect(effect);
      }
   }

   public static void pushNearby(Level level, Vec3 sourcePosition, double radius, ScreenEffectBase effect) {
      if (isWithinRange(level, sourcePosition, radius)) {
         ScreenEffectEngine.PushScreenEffect(effect);
      }
   }

   public static void pushNearbyAdditive(LivingEntityPatch<?> source, double radius, ScreenEffectBase effect) {
      if (source != null) {
         pushNearbyAdditive(source.getOriginal(), radius, effect);
      }
   }

   public static void pushNearbyAdditive(Entity source, double radius, ScreenEffectBase effect) {
      if (source != null && isWithinRange(source.m_9236_(), source.m_20182_(), radius)) {
         ScreenEffectEngine.PushScreenEffectADD(effect);
      }
   }

   public static void pushNearbyAdditive(Level level, Vec3 sourcePosition, double radius, ScreenEffectBase effect) {
      if (isWithinRange(level, sourcePosition, radius)) {
         ScreenEffectEngine.PushScreenEffectADD(effect);
      }
   }

   private static boolean isWithinRange(Level level, Vec3 sourcePosition, double radius) {
      Minecraft minecraft = Minecraft.m_91087_();
      Entity cameraEntity = minecraft.m_91288_();
      if (cameraEntity != null && level != null && sourcePosition != null && cameraEntity.m_9236_() == level) {
         double safeRadius = Math.max(0.0, radius);
         return cameraEntity.m_20182_().m_82557_(sourcePosition) <= safeRadius * safeRadius;
      } else {
         return false;
      }
   }
}
