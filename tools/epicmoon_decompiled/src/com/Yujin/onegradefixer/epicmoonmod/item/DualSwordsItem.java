package com.Yujin.onegradefixer.epicmoonmod.item;

import com.Yujin.onegradefixer.epicmoonmod.effect.EMeffects;
import com.Yujin.onegradefixer.epicmoonmod.skill.weapon_innate.DualInnate;
import com.merlin204.avalon.item.animationitem.IAvalonAnimationItem;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.item.WeaponItem;

public class DualSwordsItem extends WeaponItem implements IAvalonAnimationItem {
   public final ArmatureAccessor<? extends Armature> ARMATUREACCESSOR = ArmatureAccessor.create("epicmoonmod", "valencina_dual_swords", Armature::new);

   public DualSwordsItem(Tier tier, int damageIn, float speedIn, Properties builder) {
      super(tier, damageIn, speedIn, builder);
   }

   public void m_7373_(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
      super.m_7373_(pStack, pLevel, pTooltipComponents, pIsAdvanced);
      CompoundTag PS = pStack.m_41784_();
      if (!PS.m_128441_("amount")) {
         PS.m_128405_("amount", 0);
      }

      pTooltipComponents.add(
         Component.m_237115_("tooltip.epicmoonmod.dstip").m_130940_(ChatFormatting.DARK_RED).m_130940_(ChatFormatting.ITALIC).m_130940_(ChatFormatting.BOLD)
      );
   }

   public void m_6883_(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
      super.m_6883_(pStack, pLevel, pEntity, pSlotId, pIsSelected);
      if (!pLevel.f_46443_ && pEntity instanceof Player) {
         ItemStack a = ((Player)pEntity).m_21205_();
         if (a.m_41720_() == EpicmoonItems.VALENCINA_DUAL_SWORDS.get()) {
            pEntity.getCapability(EpicFightCapabilities.CAPABILITY_SKILL).ifPresent(capabilitySkill -> {
               SkillContainer skillContainer = capabilitySkill.getSkillContainerFor(SkillSlots.WEAPON_INNATE);
               if (skillContainer.getSkill() != null) {
                  DualInnate dualInnate = (DualInnate)skillContainer.getSkill();
                  float b = (float)dualInnate.getSin(skillContainer);
                  if (b == 1.0F) {
                     ((Player)pEntity).m_7292_(new MobEffectInstance((MobEffect)EMeffects.DUAL_SIN.get(), 30, 0, true, true));
                  }
               }
            });
         } else {
            pEntity.getCapability(EpicFightCapabilities.CAPABILITY_SKILL).ifPresent(capabilitySkill -> {
               SkillContainer skillContainer = capabilitySkill.getSkillContainerFor(SkillSlots.WEAPON_INNATE);
               if (skillContainer.getSkill() instanceof DualInnate) {
                  DualInnate dualInnate = (DualInnate)skillContainer.getSkill();
                  int b = dualInnate.getSin(skillContainer);
                  if (b != 0) {
                     dualInnate.setSin(skillContainer, 0);
                  }
               }
            });
         }
      }
   }

   public ArmatureAccessor<? extends Armature> getArmature() {
      return this.ARMATUREACCESSOR;
   }
}
