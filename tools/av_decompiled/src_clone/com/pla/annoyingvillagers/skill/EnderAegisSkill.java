package com.pla.annoyingvillagers.skill;

import com.pla.annoyingvillagers.gameasset.AVSkills;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFight;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class EnderAegisSkill extends WeaponInnateSkill {
   private static final UUID EVENT_UUID = UUID.fromString("348aa19d-7c78-4959-9639-00c467ed258d");

   public EnderAegisSkill(SkillBuilder<? extends WeaponInnateSkill> builder) {
      super(builder);
   }

   public static void onParry(ServerPlayerPatch serverPlayerPatch) {
      SkillContainer skillContainer = serverPlayerPatch.getSkill(AVSkills.ENDER_AEGIS);
      if (skillContainer != null) {
         EnderAegisSkill enderAegisSkill = (EnderAegisSkill)skillContainer.getSkill();
         if (!skillContainer.isActivated() && skillContainer.getStack() < 1) {
            float currentResource = skillContainer.getResource();
            float neededResource = skillContainer.getNeededResource();
            float addResource = Math.min(5.0F, neededResource);
            enderAegisSkill.setConsumptionSynchronize(skillContainer, currentResource + addResource);
         } else if (skillContainer.isActivated()) {
            enderAegisSkill.setDurationSynchronize(skillContainer, skillContainer.getRemainDuration() + 40);
         }
      }
   }

   public void executeOnServer(SkillContainer skillContainer, FriendlyByteBuf friendlyByteBuf) {
      if (!skillContainer.isActivated()) {
         skillContainer.getExecutor().playAnimationSynchronized(AnimsWom.ENDER_AEGIS_NAPOLEON_RELOAD_1, 0.0F);
         ((Player)skillContainer.getExecutor().getOriginal())
            .m_5496_((SoundEvent)AnnoyingVillagersModSounds.ELITE_HEROBRINE_WEAPON_SCREAMING.get(), 0.5F, 1.0F);
         super.executeOnServer(skillContainer, friendlyByteBuf);
         skillContainer.activate();
      }
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
      container.getExecutor().getEventListener().addEventListener(EventType.BASIC_ATTACK_EVENT, EVENT_UUID, event -> {
         SkillContainer skillContainer = ((ServerPlayerPatch)event.getPlayerPatch()).getSkill(this);
         ItemStack itemStack = ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).m_21205_();
         if (skillContainer != null) {
            if (skillContainer.isActivated() && itemStack.m_41783_() != null) {
               event.setCanceled(true);
               if (((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).m_36335_().m_41521_(itemStack.m_41720_(), 0.0F) == 0.0F) {
                  skillContainer.getExecutor().playAnimationSynchronized(AnimsEpicFight.AEGIS_SHIELD_SHOOT, 0.0F);
               }
            }
         }
      });
      container.getExecutor()
         .getEventListener()
         .addEventListener(
            EventType.TAKE_DAMAGE_EVENT_ATTACK,
            EVENT_UUID,
            pre -> {
               PlayerPatch<?> playerPatch = pre.getPlayerPatch();
               ServerPlayer serverPlayer = (ServerPlayer)((ServerPlayerPatch)pre.getPlayerPatch()).getOriginal();
               DamageSource damageSource = pre.getDamageSource();
               SkillContainer skillContainer = ((ServerPlayerPatch)pre.getPlayerPatch()).getSkill(this);
               if (skillContainer != null) {
                  EnderAegisSkill enderAegisSkill = (EnderAegisSkill)skillContainer.getSkill();
                  AnimationPlayer animationPlayer = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null));
                  AssetAccessor<? extends StaticAnimation> dynamicAnimation = animationPlayer.getRealAnimation();
                  float elapsedTimeFloat = animationPlayer.getElapsedTime();
                  EntityState entityState = ((StaticAnimation)dynamicAnimation.get()).getState(playerPatch, elapsedTimeFloat);
                  if (!damageSource.m_276093_(DamageTypes.f_268515_)
                     && !damageSource.m_276093_(DamageTypes.f_268565_)
                     && !damageSource.m_276093_(DamageTypes.f_268468_)
                     && !damageSource.m_276093_(DamageTypes.f_268631_)
                     && !damageSource.m_276093_(DamageTypes.f_268671_)
                     && skillContainer.isActivated()
                     && dynamicAnimation == AnimsEpicFight.AEGIS_SHIELD_SHOOT
                     && entityState.getLevel() < 3) {
                     Entity entity = damageSource.m_7639_();
                     if (entity != null) {
                        Vec3 entityPosition = entity.m_20182_();
                        Vec3 entityViewVector = ((ServerPlayer)((ServerPlayerPatch)pre.getPlayerPatch()).getOriginal()).m_20252_(1.0F);
                        Vec3 entitySubtract = entityPosition.m_82546_(((ServerPlayer)((ServerPlayerPatch)pre.getPlayerPatch()).getOriginal()).m_146892_())
                           .m_82541_();
                        if (entitySubtract.m_82526_(entityViewVector) > 0.0) {
                           pre.setCanceled(true);
                           pre.setResult(ResultType.BLOCKED);
                           entity.m_20256_(new Vec3(entity.m_20154_().f_82479_ * -0.2, 0.0, entity.m_20154_().f_82481_ * -0.2));
                           serverPlayer.m_20256_(new Vec3(serverPlayer.m_20154_().f_82479_ * -0.2, 0.0, serverPlayer.m_20154_().f_82481_ * -0.2));
                           if (serverPlayer.m_9236_() instanceof ServerLevel serverLevel) {
                              EpicfightUtil.damageBlocked(damageSource, serverPlayer, serverLevel);
                           }

                           enderAegisSkill.setDurationSynchronize(skillContainer, skillContainer.getRemainDuration() + 40);
                        }
                     }
                  }
               }
            }
         );
   }

   public void onRemoved(SkillContainer container) {
      container.getExecutor().getEventListener().removeListener(EventType.BASIC_ATTACK_EVENT, EVENT_UUID);
      container.getExecutor().getEventListener().removeListener(EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID);
   }
}
