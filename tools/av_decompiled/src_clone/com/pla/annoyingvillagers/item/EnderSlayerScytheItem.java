package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.gameasset.AVSkills;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.util.HerobrineUtil;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class EnderSlayerScytheItem extends SwordItem {
   public EnderSlayerScytheItem() {
      super(new Tier() {
         public int m_6609_() {
            return 1561;
         }

         public float m_6624_() {
            return 4.0F;
         }

         public float m_6631_() {
            return 4.0F;
         }

         public int m_6604_() {
            return 1;
         }

         public int m_6601_() {
            return 2;
         }

         @NotNull
         public Ingredient m_6282_() {
            return Ingredient.m_43929_(new ItemLike[]{(ItemLike)AnnoyingVillagersModItems.ELITE_OBSIDIAN.get()});
         }
      }, 3, -2.3F, new Properties().m_41486_());
   }

   public void m_6883_(@NotNull ItemStack pStack, @NotNull Level pLevel, @NotNull Entity pEntity, int pSlotId, boolean pIsSelected) {
      super.m_6883_(pStack, pLevel, pEntity, pSlotId, pIsSelected);
      if (pIsSelected && pEntity instanceof Player player && pLevel instanceof ServerLevel serverLevel) {
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         if (playerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillContainer skillContainer = serverPlayerPatch.getSkill(AVSkills.ENDER_SLAYER_SCYTHE);
            if (skillContainer != null && skillContainer.isActivated()) {
               HerobrineUtil.spawnEliteEffect(serverLevel, pEntity.m_20185_(), pEntity.m_20186_(), pEntity.m_20189_(), pEntity);
            }
         }
      }
   }

   public void m_7373_(@NotNull ItemStack itemstack, Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipflag) {
      super.m_7373_(itemstack, level, list, tooltipflag);
      list.add(Component.m_237113_(Component.m_237115_("tooltip.annoyingvillagers.ender_slayer_scythe").getString()));
   }
}
