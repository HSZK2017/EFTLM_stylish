package com.dmc.invincible_dmc.capability.yamato;

import com.dmc.invincible_dmc.capability.weapon.DmcWeaponManager;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(
   modid = "invincible_dmc"
)
public class YamatoPlayerStateProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
   public static final Capability<YamatoPlayerState> YAMATO_PLAYER_STATE = CapabilityManager.get(new CapabilityToken<YamatoPlayerState>() {
   });
   private YamatoPlayerState state;
   private final LazyOptional<YamatoPlayerState> optional = LazyOptional.of(this::getOrCreate);

   public static YamatoPlayerState get(Player player) {
      return (YamatoPlayerState)player.getCapability(YAMATO_PLAYER_STATE).orElse(YamatoPlayerState.EMPTY);
   }

   @SubscribeEvent
   public static void onPlayerCloned(Clone event) {
      event.getOriginal().reviveCaps();
      YamatoPlayerState old = get(event.getOriginal());
      if (old != YamatoPlayerState.EMPTY) {
         YamatoPlayerState state = get(event.getEntity());
         if (state != YamatoPlayerState.EMPTY) {
            state.copyFrom(old);
         }
      }
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         DmcWeaponManager.syncTo(player, player);
      }
   }

   @SubscribeEvent
   public static void onPlayerRespawn(PlayerRespawnEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         DmcWeaponManager.syncToTrackingAndSelf(player);
      }
   }

   @SubscribeEvent
   public static void onPlayerChangedDimension(PlayerChangedDimensionEvent event) {
      if (event.getEntity() instanceof ServerPlayer player) {
         DmcWeaponManager.syncToTrackingAndSelf(player);
      }
   }

   @SubscribeEvent
   public static void onStartTracking(StartTracking event) {
      if (event.getEntity() instanceof ServerPlayer receiver && event.getTarget() instanceof Player subject) {
         DmcWeaponManager.syncTo(receiver, subject);
      }
   }

   private YamatoPlayerState getOrCreate() {
      if (this.state == null) {
         this.state = new YamatoPlayerState();
      }

      return this.state;
   }

   @NotNull
   public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
      return cap == YAMATO_PLAYER_STATE ? this.optional.cast() : LazyOptional.empty();
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      this.getOrCreate().saveNBTData(tag);
      return tag;
   }

   public void deserializeNBT(CompoundTag tag) {
      this.getOrCreate().loadNBTData(tag);
   }
}
