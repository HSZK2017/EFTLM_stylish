package org.merlin204.mimic.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.merlin204.mimic.entity.MimicEntities;
import org.merlin204.mimic.entity.shadow.PlayerShadowMimicEntity;
import org.merlin204.mimic.worldgen.WraithonDimensions;

public class ProteusCommendationItem extends Item {
   public ProteusCommendationItem(Properties p_41383_) {
      super(p_41383_);
   }

   @NotNull
   public InteractionResultHolder<ItemStack> m_7203_(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand pUsedHand) {
      if (player instanceof ServerPlayer serverPlayer) {
         if (level.m_46472_() != WraithonDimensions.THE_LETHEAN_SEA_LEVEL_KEY) {
            ItemStack itemStack = player.m_21120_(pUsedHand);
            if (itemStack.m_41782_()) {
               CompoundTag tag = itemStack.m_41783_();
               if (tag != null && tag.m_128441_("copy_info")) {
                  CompoundTag copy = tag.m_128469_("copy_info");
                  PlayerShadowMimicEntity playerShadowMimicEntity = new PlayerShadowMimicEntity(
                     (EntityType<? extends PathfinderMob>)MimicEntities.PLAYER_SHADOW_MIMIC.get(), level, player
                  );
                  playerShadowMimicEntity.m_146884_(player.m_20182_());
                  playerShadowMimicEntity.loadCopy(copy);
                  level.m_7967_(playerShadowMimicEntity);
               }
            }
         } else {
            teleportToRespawnPoint(serverPlayer);
         }
      }

      return super.m_7203_(level, player, pUsedHand);
   }

   public void m_7373_(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      super.m_7373_(stack, worldIn, tooltip, flagIn);
      tooltip.add(Component.m_237115_("item.mimic.proteus_commendation.tip_1").m_130940_(ChatFormatting.DARK_RED));
      tooltip.add(Component.m_237115_("item.mimic.proteus_commendation.tip_2").m_130940_(ChatFormatting.GRAY));
      tooltip.add(Component.m_237115_("item.mimic.proteus_commendation.tip_3").m_130940_(ChatFormatting.GRAY));
      tooltip.add(Component.m_237115_("item.mimic.proteus_commendation.tip_4").m_130940_(ChatFormatting.GRAY));
      if (stack.m_41782_()) {
         CompoundTag tag = stack.m_41783_();
         if (tag != null && tag.m_128441_("copy_info")) {
            CompoundTag copy = tag.m_128469_("copy_info");
            tooltip.add(
               Component.m_237113_(Component.m_237115_("item.mimic.proteus_commendation.tip_5").getString() + copy.m_128451_("number"))
                  .m_130940_(ChatFormatting.RED)
            );
         }
      }
   }

   public static void teleportToRespawnPoint(ServerPlayer player) {
      if (player != null) {
         MinecraftServer server = player.m_20194_();
         if (server != null) {
            BlockPos respawnPos = player.m_8961_();
            ResourceKey<Level> respawnDimension = player.m_8963_();
            float respawnAngle = player.m_8962_();
            if (respawnPos == null) {
               ServerLevel level = server.m_129880_(respawnDimension != null ? respawnDimension : Level.f_46428_);
               if (level == null) {
                  level = server.m_129880_(Level.f_46428_);
               }

               if (level == null) {
                  return;
               }

               respawnPos = level.m_220360_();
            }

            ServerLevel respawnLevel = server.m_129880_(respawnDimension != null ? respawnDimension : Level.f_46428_);
            if (respawnLevel == null) {
               respawnLevel = server.m_129880_(Level.f_46428_);
            }

            if (respawnLevel != null) {
               player.m_8999_(
                  respawnLevel,
                  (double)respawnPos.m_123341_() + 0.5,
                  (double)respawnPos.m_123342_() + 0.1,
                  (double)respawnPos.m_123343_() + 0.5,
                  respawnAngle,
                  player.m_146909_()
               );
            }
         }
      }
   }
}
