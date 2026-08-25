package org.merlin204.mimic.event;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.merlin204.mimic.client.gui.BossBar;
import org.merlin204.mimic.entity.MimicEntity;
import org.merlin204.mimic.entity.proteus.ProteusEntity;
import org.merlin204.mimic.entity.shadow.ShadowMimicEntity;
import org.merlin204.mimic.item.MimicItems;
import org.merlin204.mimic.network.PacketHandler;
import org.merlin204.mimic.network.PacketRelay;
import org.merlin204.mimic.network.packet.client.SyncBossBarPacket;
import org.merlin204.mimic.util.DimensionResourceCopier;
import org.merlin204.mimic.worldgen.WraithonDimensions;

@EventBusSubscriber(
   modid = "mimic"
)
public class ForgeEvents {
   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer serverPlayer) {
         BossBar.BOSSES.forEach((uuid, integer) -> PacketRelay.sendToPlayer(PacketHandler.INSTANCE, new SyncBossBarPacket(uuid, integer), serverPlayer));
      }
   }

   @SubscribeEvent
   public static void onLivingDrops(LivingDropsEvent event) {
      if (event.getEntity() instanceof MimicEntity) {
         event.getDrops().clear();
         if (event.getEntity() instanceof ProteusEntity proteus) {
            ItemStack itemStack = new ItemStack((ItemLike)MimicItems.PROTEUS_COMMENDATION.get());
            itemStack.m_41764_(1);
            CompoundTag tag = new CompoundTag();
            proteus.saveCopy(tag);
            itemStack.m_41784_().m_128365_("copy_info", tag);
            double x = event.getEntity().m_20185_();
            double y = event.getEntity().m_20186_();
            double z = event.getEntity().m_20189_();
            ItemEntity itemEntity = new ItemEntity(event.getEntity().m_9236_(), x, y, z, itemStack);
            event.getDrops().add(itemEntity);
         }
      }
   }

   @SubscribeEvent
   public static void hurtEvent(LivingHurtEvent event) {
      if (event.getSource().m_7639_() instanceof ShadowMimicEntity shadowMimicEntity && shadowMimicEntity.getOwner() == event.getEntity()) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onServerAboutToStart(ServerAboutToStartEvent event) {
      DimensionResourceCopier.copyDimensionToSaves(event.getServer());
   }

   @SubscribeEvent
   public static void onBreakBlock(EntityPlaceEvent event) {
      if (event.getLevel() instanceof ServerLevel serverLevel && serverLevel.m_46472_() == WraithonDimensions.THE_LETHEAN_SEA_LEVEL_KEY) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onBreakBlock(BreakEvent event) {
      if (event.getLevel() instanceof ServerLevel serverLevel && serverLevel.m_46472_() == WraithonDimensions.THE_LETHEAN_SEA_LEVEL_KEY) {
         event.setCanceled(true);
      }
   }
}
