package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.item.BlueDemonTridentItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.FORGE
)
public final class BlueDemonTridentOnPlayerHandEvent {
   private static final String TAG_NEXT_STORM_ROLL = "BlueDemonNextStormRoll";
   private static final String TAG_LAST_LIGHTNING_UUID = "BlueDemonLastLightningUUID";
   private static final int STORM_CHECK_INTERVAL = 20;
   private static final double STORM_STRIKE_CHANCE = 0.05;
   private static final int STRIKE_COOLDOWN_MIN = 60;
   private static final int STRIKE_COOLDOWN_MAX = 120;

   private BlueDemonTridentOnPlayerHandEvent() {
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END) {
         if (event.player instanceof ServerPlayer player) {
            ServerLevel var8 = player.m_284548_();
            if (isStormAttractorActive(player, var8)) {
               CompoundTag data = player.getPersistentData();
               long gameTime = var8.m_46467_();
               long nextAllowedRoll = data.m_128454_("BlueDemonNextStormRoll");
               if (gameTime >= nextAllowedRoll) {
                  data.m_128356_("BlueDemonNextStormRoll", gameTime + 20L);
                  if (!(var8.f_46441_.m_188500_() > 0.05)) {
                     summonNaturalLightning(var8, BlockPos.m_274561_(player.m_20185_(), player.m_20186_(), player.m_20189_()));
                     data.m_128356_("BlueDemonNextStormRoll", gameTime + (long)Mth.m_216271_(var8.f_46441_, 60, 120));
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEntityStruckByLightning(EntityStruckByLightningEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         LightningBolt var6 = event.getLightning();
         if (!alreadyChargedFromThisLightning(player, var6)) {
            List<ItemStack> candidates = getChargeCandidates(player);
            if (!candidates.isEmpty()) {
               ItemStack chosen = candidates.get(player.m_217043_().m_188503_(candidates.size()));
               int gained = Mth.m_216271_(player.m_217043_(), 2, 4);
               BlueDemonTridentItem.addStormEnergy(chosen, gained);
            }
         }
      }
   }

   private static boolean isStormAttractorActive(ServerPlayer player, ServerLevel serverLevel) {
      if (!player.m_6084_() || player.m_5833_()) {
         return false;
      } else if (!serverLevel.m_46470_()) {
         return false;
      } else {
         BlockPos headPos = BlockPos.m_274561_(player.m_20185_(), player.m_20186_() + (double)player.m_20206_() + 0.25, player.m_20189_());
         return !serverLevel.m_45527_(headPos)
            ? false
            : BlueDemonTridentItem.isBlueDemonTrident(player.m_21205_()) || BlueDemonTridentItem.isBlueDemonTrident(player.m_21206_());
      }
   }

   private static void summonNaturalLightning(ServerLevel serverLevel, BlockPos strikePos) {
      LightningBolt lightning = (LightningBolt)EntityType.f_20465_.m_20615_(serverLevel);
      if (lightning != null) {
         lightning.m_20219_(Vec3.m_82539_(strikePos));
         lightning.m_20874_(false);
         lightning.setDamage(new Random().nextFloat(1.0F, 3.0F));
         serverLevel.m_7967_(lightning);
      }
   }

   private static List<ItemStack> getChargeCandidates(ServerPlayer player) {
      List<ItemStack> candidates = new ArrayList<>(2);
      ItemStack mainHand = player.m_21205_();
      ItemStack offHand = player.m_21206_();
      if (BlueDemonTridentItem.isBlueDemonTrident(mainHand) && !BlueDemonTridentItem.isFullyCharged(mainHand)) {
         candidates.add(mainHand);
      }

      if (BlueDemonTridentItem.isBlueDemonTrident(offHand) && !BlueDemonTridentItem.isFullyCharged(offHand)) {
         candidates.add(offHand);
      }

      return candidates;
   }

   private static boolean alreadyChargedFromThisLightning(ServerPlayer player, LightningBolt lightning) {
      CompoundTag data = player.getPersistentData();
      UUID lightningUUID = lightning.m_20148_();
      if (data.m_128403_("BlueDemonLastLightningUUID") && lightningUUID.equals(data.m_128342_("BlueDemonLastLightningUUID"))) {
         return true;
      } else {
         data.m_128362_("BlueDemonLastLightningUUID", lightningUUID);
         return false;
      }
   }
}
