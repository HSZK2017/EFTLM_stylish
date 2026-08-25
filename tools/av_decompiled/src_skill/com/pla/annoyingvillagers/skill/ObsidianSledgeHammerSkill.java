package com.pla.annoyingvillagers.skill;

import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class ObsidianSledgeHammerSkill extends WeaponInnateSkill {
   private static final UUID EVENT_UUID = UUID.fromString("f79be742-fddd-454d-bd28-4d030613b284");

   public ObsidianSledgeHammerSkill(SkillBuilder<? extends WeaponInnateSkill> builder) {
      super(builder);
   }

   public void executeOnServer(SkillContainer skillContainer, FriendlyByteBuf friendlyByteBuf) {
      if (!skillContainer.isActivated()) {
         skillContainer.getExecutor().playAnimationSynchronized(AnimsPugilistSteve.POSE_UP, 0.0F);
         ((Player)skillContainer.getExecutor().getOriginal())
            .m_5496_((SoundEvent)AnnoyingVillagersModSounds.ELITE_HEROBRINE_WEAPON_SCREAMING.get(), 0.5F, 1.0F);
         super.executeOnServer(skillContainer, friendlyByteBuf);
         skillContainer.activate();
      }
   }

   public void onInitiate(SkillContainer container) {
      super.onInitiate(container);
      container.getExecutor()
         .getEventListener()
         .addEventListener(
            EventType.BASIC_ATTACK_EVENT,
            EVENT_UUID,
            event -> {
               if (!((ServerPlayerPatch)event.getPlayerPatch()).isLogicalClient()) {
                  SkillContainer skillContainer = ((ServerPlayerPatch)event.getPlayerPatch()).getSkill(this);
                  if (skillContainer.isActivated()) {
                     event.setCanceled(true);
                     PlayerPatch<?> playerPatch = event.getPlayerPatch();
                     AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null))
                        .getRealAnimation();
                     if (dynamicAnimation != null && dynamicAnimation == AnimsWom.SLEDGEHAMMER_TORMENT_BERSERK_AUTO_1) {
                        skillContainer.getExecutor().playAnimationSynchronized(AnimsWom.SLEDGEHAMMER_TORMENT_BERSERK_AUTO_2, 0.0F);
                     } else {
                        skillContainer.getExecutor().playAnimationSynchronized(AnimsWom.SLEDGEHAMMER_TORMENT_BERSERK_AUTO_1, 0.0F);
                     }
                  }
               }
            }
         );
   }

   public void cancelOnServer(SkillContainer container, FriendlyByteBuf args) {
      container.deactivate();
      super.cancelOnServer(container, args);
   }

   public void executeOnClient(SkillContainer container, FriendlyByteBuf args) {
      super.executeOnClient(container, args);
      container.activate();
   }

   public void cancelOnClient(SkillContainer container, FriendlyByteBuf args) {
      super.cancelOnClient(container, args);
      container.deactivate();
   }

   public void onRemoved(SkillContainer container) {
      container.getExecutor().getEventListener().removeListener(EventType.BASIC_ATTACK_EVENT, EVENT_UUID);
   }
}
