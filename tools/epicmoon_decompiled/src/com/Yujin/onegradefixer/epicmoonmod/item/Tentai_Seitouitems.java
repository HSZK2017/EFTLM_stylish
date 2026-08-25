package com.Yujin.onegradefixer.epicmoonmod.item;

import com.Yujin.onegradefixer.epicmoonmod.effect.EMeffects;
import com.Yujin.onegradefixer.epicmoonmod.skill.weapon_innate.TsInnate;
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

public class Tentai_Seitouitems extends WeaponItem implements IAvalonAnimationItem {
   public final ArmatureAccessor<? extends Armature> ARMATUREACCESSOR = ArmatureAccessor.create("epicmoonmod", "tentai_seitou", Armature::new);

   public Tentai_Seitouitems(Tier tier, int damageIn, float speedIn, Properties builder) {
      super(tier, damageIn, speedIn, builder);
   }

   public void m_7373_(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
      CompoundTag TSTG = pStack.m_41783_();
      if (TSTG == null) {
         TSTG = new CompoundTag();
         pStack.m_41751_(TSTG);
         TSTG.m_128405_("ammotype", 0);
         TSTG.m_128405_("amount", 0);
      }

      pTooltipComponents.add(
         Component.m_237115_("tooltip.epicmoonmod.tstip").m_130940_(ChatFormatting.DARK_RED).m_130940_(ChatFormatting.ITALIC).m_130940_(ChatFormatting.BOLD)
      );
      super.m_7373_(pStack, pLevel, pTooltipComponents, pIsAdvanced);
   }

   public void m_6883_(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
      super.m_6883_(pStack, pLevel, pEntity, pSlotId, pIsSelected);
      if (!pLevel.f_46443_ && pEntity instanceof Player) {
         ItemStack a = ((Player)pEntity).m_21205_();
         if (a.m_41720_() == EpicmoonItems.TENTAI_SEITOU.get()) {
            pEntity.getCapability(EpicFightCapabilities.CAPABILITY_SKILL).ifPresent(capabilitySkill -> {
               SkillContainer skillContainer = capabilitySkill.getSkillContainerFor(SkillSlots.WEAPON_INNATE);
               if (skillContainer.getSkill() != null) {
                  TsInnate tsInnate = (TsInnate)skillContainer.getSkill();
                  float b = tsInnate.getAura(skillContainer);
                  if (b == 1.0F) {
                     ((Player)pEntity).m_7292_(new MobEffectInstance((MobEffect)EMeffects.UNRELENTING_SPIRIT.get(), 30, 0, true, true));
                  }

                  if (b == 2.0F) {
                     ((Player)pEntity).m_7292_(new MobEffectInstance((MobEffect)EMeffects.UNRELENTING_SPIRIT_SIN.get(), 30, 0, true, true));
                  }
               }
            });
         } else {
            pEntity.getCapability(EpicFightCapabilities.CAPABILITY_SKILL).ifPresent(capabilitySkill -> {
               SkillContainer skillContainer = capabilitySkill.getSkillContainerFor(SkillSlots.WEAPON_INNATE);
               if (skillContainer.getSkill() instanceof TsInnate) {
                  TsInnate tsInnate = (TsInnate)skillContainer.getSkill();
                  float b = tsInnate.getAura(skillContainer);
                  if (b != 0.0F) {
                     tsInnate.setAura(skillContainer, 0.0F);
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
