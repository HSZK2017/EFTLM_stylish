package com.dmc.invincible_dmc.api.animation;

import com.dmc.invincible_dmc.gameassets.DMCRuntimeArmatures;
import com.dmc.invincible_dmc.utils.DMCLog;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public final class RuntimeArmatureManager {
   private RuntimeArmatureManager() {
   }

   public static boolean apply(LivingEntityPatch<?> patch, ResourceLocation profileId) {
      return apply(patch, profileId, false);
   }

   public static boolean reapply(LivingEntityPatch<?> patch) {
      return apply(patch, getActiveProfile(patch), true);
   }

   public static ResourceLocation getActiveProfile(LivingEntityPatch<?> patch) {
      return patch instanceof RuntimeArmaturePatch runtimePatch ? runtimePatch.invincible$getRuntimeArmatureProfile() : DMCRuntimeArmatures.BIPED_ID;
   }

   private static boolean apply(LivingEntityPatch<?> patch, ResourceLocation profileId, boolean force) {
      RuntimeArmatureProfile profile = DMCRuntimeArmatures.get(profileId).orElse(null);
      if (profile == null) {
         DMCLog.warn(DMCLog.Category.CAPABILITY, "Unknown runtime armature profile: {}", profileId);
         return false;
      } else if (patch instanceof RuntimeArmaturePatch runtimePatch) {
         try {
            runtimePatch.invincible$applyRuntimeArmatureProfile(profile, force);
            return true;
         } catch (RuntimeException var6) {
            DMCLog.error(
               DMCLog.Category.CAPABILITY,
               "Failed to apply runtime armature profile {} to {}: {}",
               profileId,
               ((LivingEntity)patch.getOriginal()).m_20149_(),
               var6.getMessage()
            );
            return false;
         }
      } else {
         DMCLog.warn(DMCLog.Category.CAPABILITY, "LivingEntityPatch does not expose runtime armature support: {}", patch.getClass().getName());
         return false;
      }
   }
}
