package com.dmc.invincible_dmc.event;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.api.animation.types.customStun.CustomStunBridge;
import com.dmc.invincible_dmc.compat.combat_evolution.CombatEvolutionDamageTypeTags;
import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordPatch;
import com.dmc.invincible_dmc.gameassets.DMCEffects;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.server.S2CCameraShakePacket;
import com.dmc.invincible_dmc.skill.weapon_combo.Yamato;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent.ImpactResult;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.forgeevent.EntityStunEvent;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.HurtableEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.FORGE
)
public class EntityEvents {
   private static final Logger LOGGER = LoggerFactory.getLogger(EntityEvents.class);

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void prepareCustomStun(LivingHurtEvent event) {
      if (event.getSource() instanceof EpicFightDamageSource damageSource) {
         CustomStunBridge.resolveAndSetPending(damageSource, event.getEntity());
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST,
      receiveCanceled = true
   )
   public static void clearCustomStun(LivingHurtEvent event) {
      CustomStunBridge.clearPending(event.getEntity());
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onYamatoProjectileImpact(ProjectileImpactEvent event) {
      if (event.getRayTraceResult() instanceof EntityHitResult hitResult && hitResult.m_82443_() instanceof ServerPlayer player) {
         ServerPlayerPatch playerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
         if (playerPatch == null) {
            return;
         }

         SkillContainer container = playerPatch.getSkill(Yamato.YAMATO);
         if (container != null && container.getSkill() instanceof VergilSkill vergilSkill && vergilSkill.tryReflectProjectile(container, event.getProjectile())
            )
          {
            event.setImpactResult(ImpactResult.SKIP_ENTITY);
         }

         return;
      }
   }

   @SubscribeEvent
   public static void onLivingHurt(LivingHurtEvent event) {
      if (event.getSource().m_7639_() instanceof ServerPlayer player
         && event.getSource() instanceof EpicFightDamageSource efs
         && !efs.m_269533_(DMCSummonedSwordPatch.SPINE_SUMMONED_SWORD_DAMAGE)
         && !efs.m_269533_(DMCSummonedSwordPatch.SPIRAL_SWORD_DAMAGE)
         && (Boolean)DMConfig.HIT_ENTITY_CAMERA_SHAKE.get()
         && !isJudgementCutEndActive(player)) {
         Vec3 targetPos = event.getEntity().m_20191_().m_82399_();
         float intensity = 0.4F;
         int durationTicks = 6;
         float frequency = 5.5F;
         DMCNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new S2CCameraShakePacket(targetPos, intensity, durationTicks, frequency));
      }

      if (event.getSource() instanceof EpicFightDamageSource efs
         && efs.m_269533_(YamatoAnimations.CRAZY_COMBO)
         && event.getAmount() > event.getEntity().m_21223_()) {
         event.setCanceled(true);
         event.getEntity().m_21153_(1.0F);
         event.getEntity().m_7292_(new MobEffectInstance((MobEffect)DMCEffects.STOP.get(), 40, 0, false, false, false));
      }

      if (event.getSource() instanceof EpicFightDamageSource efs
         && efs.m_269533_(CombatEvolutionDamageTypeTags.EXECUTION)
         && !efs.m_269533_(CombatEvolutionDamageTypeTags.EXECUTION_FINISHED)
         && event.getAmount() > event.getEntity().m_21223_()) {
         event.setCanceled(true);
         event.getEntity().m_21153_(1.0F);
      }

      if (event.getSource() instanceof EpicFightDamageSource efs
         && efs.m_269533_(YamatoAnimations.CRAZY_COMBO_FINISH)
         && event.getEntity().m_21023_((MobEffect)DMCEffects.STOP.get())) {
         event.getEntity().m_21195_((MobEffect)DMCEffects.STOP.get());
      }
   }

   private static boolean isJudgementCutEndActive(ServerPlayer player) {
      ServerPlayerPatch playerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
      AssetAccessor<? extends StaticAnimation> animation = DMCAnimationUtils.getRealAnimationAccessor(playerPatch);
      return DMCAnimationUtils.sameAccessor(animation, YamatoAnimations.YAMATO_JUDGEMENT_CUT_END)
         || DMCAnimationUtils.sameAccessor(animation, YamatoAnimations.YAMATO_JUDGEMENT_CUT_END_INSTANT);
   }

   @SubscribeEvent
   public static void onLivingAttack(LivingAttackEvent event) {
      if (event.getSource().m_7639_() instanceof LivingEntity attacker
         && (attacker.m_21023_((MobEffect)DMCEffects.STOP.get()) || attacker.m_21023_((MobEffect)DMCEffects.SLOW.get()))) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onEntityStun(EntityStunEvent event) {
      HurtableEntityPatch<?> hurtableEntityPatch = event.getStunnedEntityPatch();
      LivingEntity entity = (LivingEntity)hurtableEntityPatch.getOriginal();
      if (!event.isCanceled()) {
         if (entity.m_21023_((MobEffect)DMCEffects.DMC_STUN_IMMUNITY.get())) {
            event.setCanceled(true);
         } else {
            if (hurtableEntityPatch instanceof ServerPlayerPatch serverPlayerPatch
               && SinDevilTriggerManager.isPlayerInSDT((Player)serverPlayerPatch.getOriginal())) {
               event.setCanceled(true);
            }
         }
      }
   }
}
