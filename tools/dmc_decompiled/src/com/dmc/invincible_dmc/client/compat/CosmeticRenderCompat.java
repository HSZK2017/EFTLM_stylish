package com.dmc.invincible_dmc.client.compat;

import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class CosmeticRenderCompat {
   private static Predicate<Entity> figuraAvatarDetector = entity -> false;

   private CosmeticRenderCompat() {
   }

   public static void registerFiguraAvatarDetector(Predicate<Entity> detector) {
      figuraAvatarDetector = detector;
   }

   public static boolean hasFiguraAvatar(Entity entity) {
      return figuraAvatarDetector.test(entity);
   }
}
