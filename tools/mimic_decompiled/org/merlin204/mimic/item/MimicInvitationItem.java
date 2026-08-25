package org.merlin204.mimic.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.jetbrains.annotations.NotNull;
import org.merlin204.mimic.entity.MimicEntities;
import org.merlin204.mimic.entity.MimicPatch;
import org.merlin204.mimic.entity.proteus.ProteusEntity;
import org.merlin204.mimic.epicfight.MimicAnimations;
import org.merlin204.mimic.util.WraithonFieldTeleporter;
import org.merlin204.mimic.worldgen.WraithonDimensions;

public class MimicInvitationItem extends Item {
   public MimicInvitationItem(Properties pProperties) {
      super(pProperties);
   }

   @NotNull
   public InteractionResultHolder<ItemStack> m_7203_(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand pUsedHand) {
      if (player instanceof ServerPlayer serverPlayer) {
         MinecraftServer server = serverPlayer.m_20194_();
         if (server == null) {
            return InteractionResultHolder.m_19100_(player.m_21120_(pUsedHand));
         }

         ServerLevel seaLevel = server.m_129880_(WraithonDimensions.THE_LETHEAN_SEA_LEVEL_KEY);
         if (seaLevel == null) {
            return InteractionResultHolder.m_19100_(player.m_21120_(pUsedHand));
         }

         if (level.m_46472_() != WraithonDimensions.THE_LETHEAN_SEA_LEVEL_KEY) {
            serverPlayer.changeDimension(seaLevel, new WraithonFieldTeleporter());
         } else if (seaLevel.m_143280_((EntityTypeTest)MimicEntities.PROTEUS.get(), LivingEntity::m_6084_).isEmpty()) {
            ProteusEntity proteus = new ProteusEntity((EntityType<? extends PathfinderMob>)MimicEntities.PROTEUS.get(), level);
            proteus.m_146884_(serverPlayer.m_20182_());
            level.m_7967_(proteus);
            MimicPatch<?> patch = proteus.getPatch();
            if (patch != null) {
               patch.reserveAnimation(MimicAnimations.COME);
            }
         }
      }

      return super.m_7203_(level, player, pUsedHand);
   }

   public void m_7373_(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      super.m_7373_(stack, worldIn, tooltip, flagIn);
      tooltip.add(Component.m_237115_("item.mimic.mimic_invitation.tip_1").m_130940_(ChatFormatting.DARK_RED));
      tooltip.add(Component.m_237115_("item.mimic.mimic_invitation.tip_2").m_130940_(ChatFormatting.GRAY));
   }
}
