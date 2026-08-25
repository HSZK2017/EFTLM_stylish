package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.entity.HerobrineGregEntity;
import com.pla.annoyingvillagers.entity.LowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.LowShadowHerobrineCloneEntity;
import com.pla.annoyingvillagers.item.TransporterFragmentItem;
import com.pla.annoyingvillagers.util.HerobrinePortalUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.FORGE
)
public class RiseFromGroundEvent {
   @SubscribeEvent
   public static void onLivingTick(LivingTickEvent event) {
      LivingEntity entity = event.getEntity();
      Level level = entity.m_9236_();
      if (!level.m_5776_()) {
         CompoundTag tag = entity.getPersistentData();
         if (!tag.m_128471_("rising")) {
            if (tag.m_128471_("sinking")) {
               double speed = tag.m_128459_("sink_speed");
               int ticks = tag.m_128451_("sink_ticks");
               int nextTicks = ticks + 1;
               HerobrinePortalUtil.moveTransitionEntity(entity, entity.m_20185_(), entity.m_20186_() - speed, entity.m_20189_());
               tag.m_128405_("sink_ticks", nextTicks);
               if (tag.m_128471_("TransporterFragmentTeleportPending") && nextTicks >= 40) {
                  TransporterFragmentItem.finishPendingSavedTeleport(entity);
               }
            }
         } else {
            double targetY = tag.m_128459_("rise_target_y");
            double speed = tag.m_128459_("rise_speed");
            int ticks = tag.m_128451_("rise_ticks");
            int max = tag.m_128451_("rise_max_ticks");
            double ny = entity.m_20186_() + speed;
            if (!(ny >= targetY) && ticks <= max) {
               HerobrinePortalUtil.moveTransitionEntity(entity, entity.m_20185_(), ny, entity.m_20189_());
               tag.m_128405_("rise_ticks", ticks + 1);
            } else {
               HerobrinePortalUtil.moveTransitionEntity(entity, entity.m_20185_(), targetY, entity.m_20189_());
               finishRise(entity);
            }
         }
      }
   }

   private static void finishRise(LivingEntity entity) {
      HerobrinePortalUtil.finishGroundTransition(entity);
      HerobrinePortalUtil.clearRiseTransitionData(entity);
      if (entity instanceof HerobrineMob herobrineMob) {
         if (herobrineMob.getGregUUID() != null
            && ((ServerLevel)herobrineMob.m_9236_()).m_8791_(herobrineMob.getGregUUID()) instanceof HerobrineGregEntity herobrineGregEntity
            && herobrineGregEntity.m_6084_()
            && herobrineGregEntity.isSummoning()) {
            herobrineGregEntity.setSummoning(false);
            herobrineGregEntity.m_21557_(false);
         }

         herobrineMob.setInitialSpawn(false);
      }

      if (entity instanceof LowHerobrineCloneEntity lowHerobrineCloneEntity) {
         lowHerobrineCloneEntity.setInitialSpawn(false);
      }

      if (entity instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity) {
         lowShadowHerobrineCloneEntity.setInitialSpawn(false);
      }
   }
}
