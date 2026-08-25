package com.pla.annoyingvillagers.skill;

import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.task.DelayedTask;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class GreatSwordSkill extends WeaponInnateSkill {
   private static final UUID EVENT_UUID = UUID.fromString("5a6ceb12-eacb-49c6-8030-37942b192e1d");
   private static final float FRONT_DOT_THRESHOLD = 0.25F;
   private static final float COUNTER_DAMAGE = 6.0F;
   private static final double KNOCKBACK_STRENGTH = 1.0;

   public GreatSwordSkill(SkillBuilder<? extends WeaponInnateSkill> builder) {
      super(builder);
   }

   public void executeOnServer(SkillContainer skillContainer, FriendlyByteBuf friendlyByteBuf) {
      final LivingEntity livingEntity = (LivingEntity)skillContainer.getExecutor().getOriginal();
      final ServerLevel serverLevel = (ServerLevel)livingEntity.m_9236_();
      if (skillContainer.isActivated()) {
         this.cancelOnServer(skillContainer, friendlyByteBuf);
      } else {
         skillContainer.getExecutor().playAnimationSynchronized(AnimsPugilistSteve.HARD_GREATSWORD_GUARD_SKILL, 0.0F);
         livingEntity.m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 60, 2));
         new DelayedTask(4) {
            @Override
            public void run() {
               serverLevel.m_8767_(
                  (SimpleParticleType)AnnoyingVillagersModParticleTypes.RED_SPARK.get(),
                  livingEntity.m_20185_(),
                  livingEntity.m_20186_() + 1.5,
                  livingEntity.m_20189_() + 1.0,
                  35,
                  0.0,
                  0.0,
                  0.0,
                  0.6
               );
               serverLevel.m_6263_(
                  null,
                  livingEntity.m_20185_(),
                  livingEntity.m_20186_(),
                  livingEntity.m_20189_(),
                  (SoundEvent)AnnoyingVillagersModSounds.HARD_GREATSWORD_SKILL.get(),
                  SoundSource.NEUTRAL,
                  1.0F,
                  1.0F
               );
            }
         };
         super.executeOnServer(skillContainer, friendlyByteBuf);
         skillContainer.activate();
      }
   }

   public void cancelOnServer(SkillContainer container, FriendlyByteBuf args) {
      container.deactivate();
      super.cancelOnServer(container, args);
   }

   public boolean canExecute(SkillContainer container) {
      return container.isActivated() ? true : super.canExecute(container);
   }

   public void executeOnClient(SkillContainer container, FriendlyByteBuf args) {
      super.executeOnClient(container, args);
      container.activate();
   }

   public void cancelOnClient(SkillContainer container, FriendlyByteBuf args) {
      super.cancelOnClient(container, args);
      container.deactivate();
   }

   public void onInitiate(SkillContainer container) {
      super.onInitiate(container);
      container.getExecutor().getEventListener().addEventListener(EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID, event -> {
         if (!((ServerPlayerPatch)event.getPlayerPatch()).isLogicalClient()) {
            PlayerPatch<?> playerPatch = event.getPlayerPatch();
            Player defender = (Player)playerPatch.getOriginal();
            AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null)).getRealAnimation();
            if (dynamicAnimation != null && dynamicAnimation == AnimsPugilistSteve.HARD_GREATSWORD_GUARD_SKILL) {
               Entity attacker = event.getDamageSource().m_7639_();
               if (attacker instanceof LivingEntity livingEntity && attacker.m_6084_()) {
                  Vec3 fwd = defender.m_20252_(1.0F).m_82541_();
                  Vec3 toAttacker = attacker.m_20182_().m_82546_(defender.m_20182_()).m_82541_();
                  if (fwd.m_82526_(toAttacker) <= 0.25) {
                     return;
                  }

                  LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(attacker, LivingEntityPatch.class);
                  if (livingEntityPatch != null) {
                     livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.GUARD_BREAK_ATTACK, 0.0F);
                  }

                  livingEntity.m_147240_(1.0, defender.m_20185_() - attacker.m_20185_(), defender.m_20189_() - attacker.m_20189_());
                  if (container.getStack() < 1) {
                     GreatSwordSkill greatSwordSkill = (GreatSwordSkill)container.getSkill();
                     float currentResource = container.getResource();
                     float neededResource = container.getNeededResource();
                     float addResource = Math.min(20.0F, neededResource);
                     greatSwordSkill.setConsumptionSynchronize(container, currentResource + addResource);
                  }

                  attacker.f_19864_ = true;
                  attacker.m_6469_(defender.m_269291_().m_269075_(defender), 6.0F);
                  event.setCanceled(true);
                  event.setResult(ResultType.BLOCKED);
                  return;
               }
            }
         }
      }, 10);
   }

   public void onRemoved(SkillContainer container) {
      container.getExecutor().getEventListener().removeListener(EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID);
      super.onRemoved(container);
   }
}
