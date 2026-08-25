package com.Yujin.onegradefixer.epicmoonmod.compat;

import com.Yujin.onegradefixer.epicmoonmod.animations.EMAnimations;
import java.lang.reflect.Field;
import net.minecraftforge.fml.ModList;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;

public class compat {
   public static StaticAnimation getDualAnimation() {
      if (!ModList.get().isLoaded("efn")) {
         return (StaticAnimation)EMAnimations.DUAL_DODGE.get();
      } else {
         try {
            Class<?> clazz = Class.forName("com.hm.efn.gameasset.animations.EFNDodgeAnimations");
            Field field = clazz.getField("DODGE_STEP_B");
            if (field.get(null) instanceof AnimationAccessor<?> accessor) {
               Object animation = accessor.get();
               if (animation instanceof StaticAnimation) {
                  return (StaticAnimation)animation;
               }
            }
         } catch (Exception var6) {
            var6.printStackTrace();
         }

         return (StaticAnimation)Animations.BIPED_STEP_BACKWARD.get();
      }
   }
}
