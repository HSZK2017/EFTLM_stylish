package com.dmc.invincible_dmc.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@EventBusSubscriber(
   modid = "invincible_dmc"
)
public class DMCPlayerCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
   public static Capability<DMCPlayer> DMC_PLAYER = CapabilityManager.get(new CapabilityToken<DMCPlayer>() {
   });
   private DMCPlayer dmcPlayer = null;
   private final LazyOptional<DMCPlayer> optional = LazyOptional.of(this::createInvinciblePlayer);

   public static DMCPlayer get(Player player) {
      return (DMCPlayer)player.getCapability(DMC_PLAYER).orElse(DMCPlayer.EMPTY);
   }

   public static DMCPlayer get(PlayerPatch<?> playerPatch) {
      return get((Player)playerPatch.getOriginal());
   }

   @SubscribeEvent
   public static void onPlayerCloned(Clone event) {
      event.getOriginal().reviveCaps();
      if (event.isWasDeath()) {
         event.getOriginal()
            .getCapability(DMC_PLAYER)
            .ifPresent(oldStore -> event.getEntity().getCapability(DMC_PLAYER).ifPresent(newStore -> newStore.copyFrom(oldStore)));
      }
   }

   private DMCPlayer createInvinciblePlayer() {
      if (this.dmcPlayer == null) {
         this.dmcPlayer = new DMCPlayer();
      }

      return this.dmcPlayer;
   }

   @NotNull
   public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction direction) {
      return capability == DMC_PLAYER ? this.optional.cast() : LazyOptional.empty();
   }

   public CompoundTag serializeNBT() {
      CompoundTag tag = new CompoundTag();
      this.createInvinciblePlayer().saveNBTData(tag);
      tag.m_128473_("weaponActionSession");
      tag.m_128473_("activeCrazyComboNodeId");
      return tag;
   }

   public void deserializeNBT(CompoundTag tag) {
      this.createInvinciblePlayer().loadNBTData(tag);
   }
}
