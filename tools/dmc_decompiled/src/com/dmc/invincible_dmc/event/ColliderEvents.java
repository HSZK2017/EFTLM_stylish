package com.dmc.invincible_dmc.event;

import com.dmc.invincible_dmc.api.collider.ScalableMultiOBBCollider;
import com.dmc.invincible_dmc.api.collider.ScalableOBBCollider;
import com.dmc.invincible_dmc.api.forgeevent.ColliderScaleEvent;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.ConcentrationManager;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@EventBusSubscriber(
   modid = "invincible_dmc"
)
public class ColliderEvents {
   private static final double[] TIER_SCALES = new double[]{1.0, 1.25, 1.4};

   @SubscribeEvent
   public static void onColliderScale(ColliderScaleEvent event) {
      if (event.getCollider() instanceof ScalableOBBCollider || event.getCollider() instanceof ScalableMultiOBBCollider) {
         PlayerPatch<?> pp = resolvePlayerPatch(event);
         if (pp != null) {
            SkillContainer sc = pp.getSkill(SkillSlots.WEAPON_INNATE);
            if (sc != null && !sc.isEmpty()) {
               int tier = ConcentrationManager.getConcentrationTier(sc);
               double scale = TIER_SCALES[tier];
               if (scale != 1.0 && event.getCollider() != YamatoAnimations.COMBO_C_END) {
                  event.scaleX *= scale;
                  event.scaleY *= scale;
                  event.scaleZ *= scale;
               }

               if (event.getCollider() == YamatoAnimations.COMBO_C_END && SinDevilTriggerManager.isLivingInSDT((LivingEntity)pp.getOriginal())) {
                  event.scaleX *= 1.35;
                  event.scaleY *= 1.35;
                  event.scaleZ *= 1.35;
               }
            }
         }
      }
   }

   private static PlayerPatch<?> resolvePlayerPatch(ColliderScaleEvent event) {
      LivingEntity entity = (LivingEntity)event.getEntityPatch().getOriginal();
      if (event.getEntityPatch() instanceof DoppelgangerPatch dp) {
         return dp.getOwnerPatch();
      } else {
         return entity instanceof Player ? (PlayerPatch)EpicFightCapabilities.getEntityPatch(entity, PlayerPatch.class) : null;
      }
   }
}
