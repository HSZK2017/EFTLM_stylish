package com.dmc.invincible_dmc.event;

import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@EventBusSubscriber(
   modid = "invincible_dmc",
   value = {Dist.CLIENT}
)
public class RenderEvents {
   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void OnLivingEntityRender(Pre<?, ?> event) {
      LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(event.getEntity(), LivingEntityPatch.class);
      if (patch != null) {
         AnimationPlayer animPlayer = patch.getClientAnimator().baseLayer.animationPlayer;
         if (animPlayer != null) {
            DynamicAnimation anim = DMCAnimationUtils.getCurrentAnimation(animPlayer);
            if (anim != null) {
               if (anim.isLinkAnimation()) {
                  DynamicAnimation realAnim = DMCAnimationUtils.getRealAnimation(anim);
                  if (realAnim != null) {
                     realAnim.getProperty(YamatoAttackAnimation.INVISIBLE_TIME).ifPresent(time -> {
                        if (time.isTimeInPairs(animPlayer.getElapsedTime())) {
                           event.setCanceled(true);
                           event.setResult(Result.DENY);
                        }
                     });
                  }
               } else {
                  anim.getProperty(YamatoAttackAnimation.INVISIBLE_TIME).ifPresent(time -> {
                     if (time.isTimeInPairs(animPlayer.getElapsedTime())) {
                        event.setCanceled(true);
                        event.setResult(Result.DENY);
                     }
                  });
               }
            }
         }
      }
   }
}
