package com.pla.annoyingvillagers.skill;

import com.pla.annoyingvillagers.gameasset.AVAnimations;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.DemoniacVoltageReaverItem;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class DemoniacVoltageReaverSkill extends WeaponInnateSkill {
   private static final UUID EVENT_UUID = UUID.fromString("a86b0713-5f98-4e04-9930-fee81f157780");

   public DemoniacVoltageReaverSkill(SkillBuilder<? extends WeaponInnateSkill> builder) {
      super(builder);
   }

   public void executeOnServer(SkillContainer skillContainer, FriendlyByteBuf friendlyByteBuf) {
      Player player = (Player)skillContainer.getExecutor().getOriginal();
      if (DemoniacVoltageReaverItem.checkNearbyTarget(player)) {
         skillContainer.getExecutor().playAnimationSynchronized(AVAnimations.SNAKE_BLADE, 0.0F);
         super.executeOnServer(skillContainer, friendlyByteBuf);
      }
   }

   public boolean canExecute(SkillContainer container) {
      Player player = (Player)container.getExecutor().getOriginal();
      ItemStack stack = player.m_21205_();
      boolean isCorrectItem = stack.m_41720_() instanceof DemoniacVoltageReaverItem;
      boolean isSnaking = stack.m_41782_() && stack.m_41783_() != null && stack.m_41783_().m_128471_("SnakeAnimation");
      boolean isActivated = container.isActivated();
      return isCorrectItem && !isSnaking && !isActivated && super.canExecute(container);
   }

   public void onInitiate(SkillContainer container) {
      super.onInitiate(container);
      container.getExecutor()
         .getEventListener()
         .addEventListener(
            EventType.SKILL_CAST_EVENT,
            EVENT_UUID,
            event -> {
               Player player = (Player)container.getExecutor().getOriginal();
               ItemStack item = player.m_21205_();
               Skill skill = event.getSkillContainer().getSkill();
               if (skill.getCategory() == SkillCategories.GUARD) {
                  if (container.getExecutor() instanceof ServerPlayerPatch serverPlayerPatch
                     && container.getStack() >= 1
                     && item.m_41720_() instanceof DemoniacVoltageReaverItem
                     && item.m_41783_() != null) {
                     event.setCanceled(true);
                     if (!item.m_41783_().m_128471_("SnakeAnimation")) {
                        container.getExecutor().playAnimationSynchronized(AVAnimations.SNAKE_BLADE_GUARD, 0.0F);
                        this.getResourceType().consumer.consume(container, serverPlayerPatch, this.getDefaultConsumptionAmount(serverPlayerPatch));
                     }
                  }
               } else if (skill.getCategory() == SkillCategories.BASIC_ATTACK
                  && item.m_41720_() instanceof DemoniacVoltageReaverItem
                  && item.m_41783_() != null
                  && item.m_41783_().m_128471_("SnakeAnimation")) {
                  event.setCanceled(true);
               }
            }
         );
      container.getExecutor().getEventListener().addEventListener(EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID, pre -> {
         if (!((ServerPlayerPatch)pre.getPlayerPatch()).isLogicalClient()) {
            PlayerPatch<?> playerPatch = pre.getPlayerPatch();
            AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null)).getRealAnimation();
            if (dynamicAnimation != null) {
               if (dynamicAnimation == AVAnimations.SNAKE_BLADE_GUARD) {
                  pre.setCanceled(true);
                  pre.setResult(ResultType.BLOCKED);
               }
            }
         }
      });
   }

   public void onRemoved(SkillContainer container) {
      container.getExecutor().getEventListener().removeListener(EventType.SKILL_CAST_EVENT, EVENT_UUID);
      container.getExecutor().getEventListener().removeListener(EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID);
   }

   public void cancelOnServer(SkillContainer skillContainer, FriendlyByteBuf friendlyByteBuf) {
      skillContainer.deactivate();
      super.cancelOnServer(skillContainer, friendlyByteBuf);
   }

   public void executeOnClient(SkillContainer container, FriendlyByteBuf args) {
      super.executeOnClient(container, args);
      container.activate();
   }

   public void cancelOnClient(SkillContainer container, FriendlyByteBuf args) {
      super.cancelOnClient(container, args);
      container.deactivate();
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      Player player = (Player)container.getExecutor().getOriginal();
      ItemStack itemStack = player.m_21205_();
      if (container.getStack() == 1
         && itemStack.m_41783_() != null
         && itemStack.m_41720_() instanceof DemoniacVoltageReaverItem
         && !itemStack.m_41783_().m_128471_("SnakeAnimation")
         && !itemStack.m_41783_().m_128471_("PlaySound")) {
         ((Player)container.getExecutor().getOriginal()).m_5496_((SoundEvent)AnnoyingVillagersModSounds.ELITE_HEROBRINE_WEAPON_SCREAMING.get(), 0.5F, 1.0F);
         itemStack.m_41783_().m_128379_("PlaySound", true);
      } else if (container.getStack() < 1
         && itemStack.m_41783_() != null
         && itemStack.m_41720_() instanceof DemoniacVoltageReaverItem
         && itemStack.m_41783_().m_128471_("PlaySound")) {
         itemStack.m_41783_().m_128473_("PlaySound");
      }
   }
}
