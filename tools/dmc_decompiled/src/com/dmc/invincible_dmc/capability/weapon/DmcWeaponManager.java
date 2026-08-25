package com.dmc.invincible_dmc.capability.weapon;

import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerStateProvider;
import com.dmc.invincible_dmc.item.YamatoItem;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.server.SPWeaponState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.AnimationBeginEvent;
import yesman.epicfight.world.entity.eventlistener.AnimationEndEvent;

public final class DmcWeaponManager {
   private DmcWeaponManager() {
   }

   public static DmcWeaponType getActiveWeapon(Player player) {
      return YamatoPlayerStateProvider.get(player).getActiveWeapon();
   }

   public static boolean isActiveWeapon(Player player, DmcWeaponType type) {
      return getActiveWeapon(player) == type;
   }

   public static DmcWeaponType getRenderedWeapon(Player player) {
      return YamatoPlayerStateProvider.get(player).getRenderedWeapon();
   }

   public static boolean isRenderedWeapon(Player player, DmcWeaponType type) {
      return getRenderedWeapon(player) == type;
   }

   public static DmcWeaponType getActionWeapon(Player player) {
      WeaponActionSession actionSession = DMCPlayerCapabilityProvider.get(player).getActionSession();
      return actionSession != null ? actionSession.ownerWeapon() : getActiveWeapon(player);
   }

   public static boolean isActionWeapon(Player player, DmcWeaponType type) {
      return getActionWeapon(player) == type;
   }

   public static boolean isArsenalItem(ItemStack stack) {
      return stack.m_41720_() instanceof YamatoItem;
   }

   public static boolean isWeaponSwitchEnabled() {
      return true;
   }

   public static boolean switchWeapon(ServerPlayer player, DmcWeaponType targetWeapon, boolean suppressEntryTransition) {
      return !isWeaponSwitchEnabled() ? false : WeaponSwitchCoordinator.requestSwitch(player, targetWeapon, suppressEntryTransition);
   }

   public static void onAnimationEnd(AnimationEndEvent event) {
      WeaponSwitchCoordinator.onAnimationEnd(event);
   }

   public static void onAnimationBegin(AnimationBeginEvent event) {
      WeaponSwitchCoordinator.onAnimationBegin(event);
   }

   public static void flushPendingRender(ServerPlayerPatch playerPatch) {
      WeaponSwitchCoordinator.flushPendingPresentation(playerPatch);
   }

   public static boolean isDeferredRenderContinuation(ResourceLocation currentAnimation, ResourceLocation nextAnimation) {
      return WeaponSwitchCoordinator.isDeferredContinuation(currentAnimation, nextAnimation);
   }

   public static void syncToTrackingAndSelf(ServerPlayer player) {
      DMCNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), SPWeaponState.from(player));
   }

   public static void syncRuntimeState(ServerPlayer player) {
      YamatoPlayerStateProvider.get(player).advanceWeaponSwitchSequence();
      syncToTrackingAndSelf(player);
   }

   public static void syncTo(ServerPlayer receiver, Player subject) {
      DMCNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> receiver), SPWeaponState.from(subject));
   }
}
