package com.pla.annoyingvillagers.skill;

import com.pla.annoyingvillagers.gameasset.AVSkills;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import reascer.wom.gameasset.animations.weapons.AnimsEnderblaster;
import reascer.wom.gameasset.animations.weapons.AnimsMoonless;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class BedrockWeaponSkill extends WeaponInnateSkill {
   private static final UUID EVENT_UUID = UUID.fromString("64062d4e-095e-468b-a25a-12811e92fd73");

   public BedrockWeaponSkill(SkillBuilder<? extends WeaponInnateSkill> builder) {
      super(builder);
   }

   public static void onParry(ServerPlayerPatch serverPlayerPatch) {
      SkillContainer skillContainer = serverPlayerPatch.getSkill(AVSkills.BEDROCK_WEAPON);
      if (skillContainer != null) {
         BedrockWeaponSkill bedrockWeaponSkill = (BedrockWeaponSkill)skillContainer.getSkill();
         if (!skillContainer.isActivated() && skillContainer.getStack() < 1) {
            float currentResource = skillContainer.getResource();
            float neededResource = skillContainer.getNeededResource();
            float addResource = Math.min(2.0F, neededResource);
            bedrockWeaponSkill.setConsumptionSynchronize(skillContainer, currentResource + addResource);
         } else if (skillContainer.isActivated()) {
            bedrockWeaponSkill.setDurationSynchronize(skillContainer, skillContainer.getRemainDuration() + 40);
         }
      }
   }

   public void executeOnServer(SkillContainer skillContainer, FriendlyByteBuf friendlyByteBuf) {
      if (!skillContainer.isActivated()) {
         skillContainer.getExecutor().playAnimationSynchronized(AnimsEnderblaster.ENDERBLASTER_ONEHAND_RELOAD, 0.0F);
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
                  if (!damageSource.m_276093_(DamageTypes.f_268515_)
                     && !damageSource.m_276093_(DamageTypes.f_268565_)
                     && !damageSource.m_276093_(DamageTypes.f_268468_)
                     && !damageSource.m_276093_(DamageTypes.f_268631_)
                     && !damageSource.m_276093_(DamageTypes.f_268671_)
                     && skillContainer.isActivated()) {
                     Entity entity = damageSource.m_7639_();
                     if (entity != null) {
                        pre.setCanceled(true);
                        pre.setResult(ResultType.BLOCKED);
                        playerPatch.playAnimationSynchronized(AnimsMoonless.MOONLESS_GUARD_HIT_1, 0.0F);
                        entity.m_20256_(new Vec3(entity.m_20154_().f_82479_ * -0.2, 0.0, entity.m_20154_().f_82481_ * -0.2));
                        serverPlayer.m_20256_(new Vec3(serverPlayer.m_20154_().f_82479_ * -0.2, 0.0, serverPlayer.m_20154_().f_82481_ * -0.2));
                        serverPlayer.m_5634_(2.0F);
                        if (serverPlayer.m_9236_() instanceof ServerLevel serverLevel) {
                           EpicfightUtil.damageBlocked(damageSource, serverPlayer, serverLevel);
                        }
                     }
                  }
               }
            }
         );
   }

   public void onRemoved(SkillContainer container) {
      container.getExecutor().getEventListener().removeListener(EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID);
   }
}
