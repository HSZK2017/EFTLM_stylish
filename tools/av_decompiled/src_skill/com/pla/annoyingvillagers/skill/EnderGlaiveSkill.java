package com.pla.annoyingvillagers.skill;

import com.pla.annoyingvillagers.gameasset.AnimsWom;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.EnderGlaiveItem;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;

public class EnderGlaiveSkill extends WeaponInnateSkill {
   private static final UUID EVENT_UUID = UUID.fromString("f79be742-fddd-454d-bd28-4d030613b284");

   public EnderGlaiveSkill(SkillBuilder<? extends WeaponInnateSkill> builder) {
      super(builder);
   }

   public void executeOnServer(SkillContainer skillContainer, FriendlyByteBuf friendlyByteBuf) {
      if (!this.isActivated(skillContainer)) {
         super.executeOnServer(skillContainer, friendlyByteBuf);
         skillContainer.activate();
         skillContainer.getExecutor().playAnimationSynchronized(AnimsWom.ENDER_GLAIVE_AGONY_AUTO_1, 0.0F);
      }
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
         && itemStack.m_41720_() instanceof EnderGlaiveItem
         && !itemStack.m_41783_().m_128471_("PlaySound")) {
         ((Player)container.getExecutor().getOriginal()).m_5496_((SoundEvent)AnnoyingVillagersModSounds.ELITE_HEROBRINE_WEAPON_SCREAMING.get(), 0.5F, 1.0F);
         itemStack.m_41783_().m_128379_("PlaySound", true);
      } else if (container.getStack() < 1
         && itemStack.m_41783_() != null
         && itemStack.m_41720_() instanceof EnderGlaiveItem
         && itemStack.m_41783_().m_128471_("PlaySound")) {
         itemStack.m_41783_().m_128473_("PlaySound");
      }
   }
}
