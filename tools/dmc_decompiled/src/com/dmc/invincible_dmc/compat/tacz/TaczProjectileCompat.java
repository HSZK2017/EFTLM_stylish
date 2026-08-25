package com.dmc.invincible_dmc.compat.tacz;

import com.dmc.invincible_dmc.utils.DMCLog;
import com.tacz.guns.entity.EntityKineticBullet;
import java.lang.reflect.Field;

public final class TaczProjectileCompat {
   private static final Field PIERCE_FIELD = findPierceField();
   private static final int REFLECTION_TICK_PIERCE = Integer.MAX_VALUE;

   private TaczProjectileCompat() {
   }

   public static Integer beginReflection(EntityKineticBullet bullet) {
      if (PIERCE_FIELD == null) {
         return null;
      } else {
         try {
            int originalPierce = PIERCE_FIELD.getInt(bullet);
            PIERCE_FIELD.setInt(bullet, Integer.MAX_VALUE);
            return originalPierce;
         } catch (IllegalAccessException var2) {
            DMCLog.warn(DMCLog.Category.COMPAT, "[TaczProjectileCompat] Failed to begin TACZ bullet reflection", var2);
            return null;
         }
      }
   }

   public static void finishReflection(EntityKineticBullet bullet, int originalPierce) {
      if (PIERCE_FIELD != null && !bullet.m_213877_()) {
         try {
            PIERCE_FIELD.setInt(bullet, Math.max(1, originalPierce));
         } catch (IllegalAccessException var3) {
            DMCLog.warn(DMCLog.Category.COMPAT, "[TaczProjectileCompat] Failed to restore TACZ bullet pierce", var3);
         }
      }
   }

   private static Field findPierceField() {
      try {
         Field field = EntityKineticBullet.class.getDeclaredField("pierce");
         field.setAccessible(true);
         return field;
      } catch (ReflectiveOperationException var1) {
         DMCLog.warn(DMCLog.Category.COMPAT, "[TaczProjectileCompat] TACZ bullet pierce field is unavailable", var1);
         return null;
      }
   }
}
