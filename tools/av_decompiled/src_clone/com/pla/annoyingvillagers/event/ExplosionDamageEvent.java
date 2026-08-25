package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.entity.TridentLightningBolt;
import com.pla.annoyingvillagers.gameasset.AVSkills;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import com.pla.annoyingvillagers.item.EnderGlaiveItem;
import com.pla.annoyingvillagers.item.WoopieTheSwordItem;
import com.pla.annoyingvillagers.skill.EnderGlaiveSkill;
import com.pla.annoyingvillagers.skill.WoopieTheSwordSkill;
import com.pla.annoyingvillagers.task.DelayedTask;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.level.ExplosionEvent.Detonate;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import reascer.wom.gameasset.animations.weapons.AnimsHerrscher;
import reascer.wom.world.entity.mob.EnderHand;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@EventBusSubscriber
public class ExplosionDamageEvent {
   @SubscribeEvent
   public static void onExplode(Detonate detonate) {
      if (detonate.getExplosion().m_253049_() instanceof TridentLightningBolt) {
         detonate.getAffectedEntities().removeIf(entityx -> entityx instanceof ItemEntity);
      }

      final LivingEntity livingEntity = detonate.getExplosion().m_252906_();
      Vec3 center = detonate.getExplosion().getPosition();
      if (livingEntity != null && livingEntity.m_6084_() && livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
         LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(livingEntity, LivingEntityPatch.class);
         if (livingEntityPatch == null) {
            return;
         }

         AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(livingEntityPatch.getAnimator().getPlayerFor(null))
            .getRealAnimation();
         if (livingEntity.m_21205_().m_41720_() instanceof EnderGlaiveItem
            && (dynamicAnimation == AnimsWom.ENDER_GLAIVE_AGONY_AUTO_1 || dynamicAnimation == AnimsWom.ENDER_GLAIVE_NAPOLEON_SHOOT_3)) {
            SkillContainer skillContainer = null;
            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
               skillContainer = serverPlayerPatch.getSkill(AVSkills.ENDER_GLAIVE);
            }

            for (final Entity entity : detonate.getAffectedEntities()) {
               if (entity.m_6084_() && entity != detonate.getExplosion().m_252906_() && entity instanceof LivingEntity) {
                  final LivingEntity livingExploded = (LivingEntity)entity;
                  if (!(entity instanceof EnderHand)) {
                     if (entity instanceof Player) {
                        Player player = (Player)entity;
                        if (player.m_7500_()) {
                           continue;
                        }
                     }

                     LivingEntityPatch<?> explodedPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
                     new DelayedTask(10) {
                        @Override
                        public void run() {
                           EnderHand enderHand = new EnderHand(
                              serverLevel, new Vec3(entity.m_20185_(), entity.m_20186_(), entity.m_20189_()), livingEntity, livingExploded
                           );
                           serverLevel.m_7967_(enderHand);
                        }
                     };
                     if (skillContainer != null && skillContainer.getStack() < 3) {
                        EnderGlaiveSkill enderGlaiveSkill = (EnderGlaiveSkill)skillContainer.getSkill();
                        float currentResource = skillContainer.getResource();
                        float neededResource = skillContainer.getNeededResource();
                        float addResource = Math.min(50.0F, neededResource);
                        enderGlaiveSkill.setConsumptionSynchronize(skillContainer, currentResource + addResource);
                     }

                     entity.m_6469_(entity.m_9236_().m_269111_().m_269333_(livingEntity), 12.0F);
                     EpicfightUtil.dealStaminaDamageByPercentage(detonate.getExplosion().m_46077_(), explodedPatch, 0.2F, false);
                  }
               }
            }
         }

         if (livingEntity.m_21205_().m_41720_() instanceof WoopieTheSwordItem && dynamicAnimation == AnimsHerrscher.HERRSCHER_AUTO_2) {
            SkillContainer skillContainer = null;
            if (livingEntityPatch instanceof ServerPlayerPatch serverPlayerPatch) {
               skillContainer = serverPlayerPatch.getSkill(AVSkills.WOOPIE_THE_SWORD);
            }

            for (Entity entityx : detonate.getAffectedEntities()) {
               if (entityx.m_6084_() && entityx != detonate.getExplosion().m_252906_() && entityx instanceof LivingEntity) {
                  LivingEntity livingExploded = (LivingEntity)entityx;
                  if (!(entityx instanceof EnderHand)) {
                     if (entityx instanceof Player) {
                        Player player = (Player)entityx;
                        if (player.m_7500_()) {
                           continue;
                        }
                     }

                     LivingEntityPatch<?> explodedPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entityx, LivingEntityPatch.class);
                     if (explodedPatch != null) {
                        AssetAccessor<? extends StaticAnimation> explodedDynamicAnimation = Objects.requireNonNull(
                              explodedPatch.getAnimator().getPlayerFor(null)
                           )
                           .getRealAnimation();
                        if (!EpicfightUtil.isLongHitAnimation(explodedDynamicAnimation, explodedPatch)) {
                           explodedPatch.playAnimationSynchronized(AnimsPugilistSteve.LONGEST_HIT, 0.0F);
                        }
                     }

                     if (entityx.m_6084_()) {
                        double dx = center.f_82479_ - entityx.m_20185_();
                        double dz = center.f_82481_ - entityx.m_20189_();
                        double dist = entityx.m_20182_().m_82554_(center);
                        double falloff = Mth.m_14008_(1.0 - dist / 8.0, 0.0, 1.0);
                        double horizontal = 6.0 * falloff;
                        double up = 2.6 * falloff;
                        livingExploded.m_147240_(horizontal, dx, dz);
                        livingExploded.m_5997_(0.0, up, 0.0);
                        livingExploded.f_19864_ = true;
                        if (skillContainer != null && skillContainer.getStack() < 1) {
                           WoopieTheSwordSkill woopieTheSwordSkill = (WoopieTheSwordSkill)skillContainer.getSkill();
                           float currentResource = skillContainer.getResource();
                           float neededResource = skillContainer.getNeededResource();
                           float addResource = Math.min(50.0F, neededResource);
                           woopieTheSwordSkill.setConsumptionSynchronize(skillContainer, currentResource + addResource);
                        }
                     }
                  }
               }
            }
         }
      }
   }
}
