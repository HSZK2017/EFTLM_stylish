package com.dmc.invincible_dmc.api.weapon;

import com.dmc.invincible_dmc.capability.weapon.WeaponActionSession;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;

public final class WeaponActionChainRegistry {
   private static final Map<ResourceLocation, Map<ResourceLocation, WeaponActionChainRegistry.ChainEntry>> CHAINS_BY_ANIMATION = new HashMap<>();

   private WeaponActionChainRegistry() {
   }

   public static void register(
      ResourceLocation chainId,
      DmcWeaponType ownerWeapon,
      WeaponActionType actionType,
      AnimationAccessor<? extends StaticAnimation> animation,
      boolean terminal
   ) {
      register(chainId, ownerWeapon, actionType, animation, terminal, false);
   }

   public static void registerShared(
      ResourceLocation chainId,
      DmcWeaponType ownerWeapon,
      WeaponActionType actionType,
      AnimationAccessor<? extends StaticAnimation> animation,
      boolean terminal
   ) {
      register(chainId, ownerWeapon, actionType, animation, terminal, true);
   }

   private static void register(
      ResourceLocation chainId,
      DmcWeaponType ownerWeapon,
      WeaponActionType actionType,
      AnimationAccessor<? extends StaticAnimation> animation,
      boolean terminal,
      boolean shared
   ) {
      if (animation != null && animation.registryName() != null) {
         Map<ResourceLocation, WeaponActionChainRegistry.ChainEntry> memberships = CHAINS_BY_ANIMATION.computeIfAbsent(
            animation.registryName(), ignored -> new HashMap<>()
         );
         if (!shared && !memberships.isEmpty() && !memberships.containsKey(chainId)) {
            throw new IllegalStateException("Animation already belongs to another weapon action chain: " + animation.registryName());
         } else {
            WeaponActionChainRegistry.ChainEntry entry = new WeaponActionChainRegistry.ChainEntry(chainId, ownerWeapon, actionType, terminal);
            WeaponActionChainRegistry.ChainEntry previous = memberships.put(chainId, entry);
            if (previous != null && (!Objects.equals(previous.ownerWeapon(), ownerWeapon) || previous.actionType() != actionType)) {
               throw new IllegalStateException("Weapon action chain identity changed for animation: " + animation.registryName());
            }
         }
      }
   }

   public static boolean belongsToSameChain(ResourceLocation first, ResourceLocation second) {
      Map<ResourceLocation, WeaponActionChainRegistry.ChainEntry> firstEntries = CHAINS_BY_ANIMATION.get(first);
      Map<ResourceLocation, WeaponActionChainRegistry.ChainEntry> secondEntries = CHAINS_BY_ANIMATION.get(second);
      return firstEntries != null && secondEntries != null && firstEntries.keySet().stream().anyMatch(secondEntries::containsKey);
   }

   public static boolean isRegistered(ResourceLocation animation) {
      Map<ResourceLocation, WeaponActionChainRegistry.ChainEntry> entries = CHAINS_BY_ANIMATION.get(animation);
      return entries != null && !entries.isEmpty();
   }

   public static boolean isTerminal(ResourceLocation animation) {
      Map<ResourceLocation, WeaponActionChainRegistry.ChainEntry> entries = CHAINS_BY_ANIMATION.get(animation);
      return entries != null && !entries.isEmpty() && entries.values().stream().allMatch(WeaponActionChainRegistry.ChainEntry::terminal);
   }

   public static boolean isTerminal(ResourceLocation animation, WeaponActionSession session) {
      Map<ResourceLocation, WeaponActionChainRegistry.ChainEntry> entries = CHAINS_BY_ANIMATION.get(animation);
      if (entries != null && !entries.isEmpty() && session != null) {
         List<WeaponActionChainRegistry.ChainEntry> sessionEntries = entries.values().stream().filter(entry -> entry.matches(session)).toList();
         return sessionEntries.isEmpty() ? isTerminal(animation) : sessionEntries.stream().allMatch(WeaponActionChainRegistry.ChainEntry::terminal);
      } else {
         return isTerminal(animation);
      }
   }

   public static boolean matchesSession(ResourceLocation animation, WeaponActionSession session) {
      Map<ResourceLocation, WeaponActionChainRegistry.ChainEntry> entries = CHAINS_BY_ANIMATION.get(animation);
      return entries != null && session != null && entries.values().stream().anyMatch(entry -> entry.matches(session));
   }

   private static record ChainEntry(ResourceLocation chainId, DmcWeaponType ownerWeapon, WeaponActionType actionType, boolean terminal) {
      private boolean matches(WeaponActionSession session) {
         return this.ownerWeapon == session.ownerWeapon() && this.actionType == session.actionType();
      }
   }
}
