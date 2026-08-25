package com.dmc.invincible_dmc.client;

import com.dmc.invincible_dmc.capability.weapon.DmcWeaponManager;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.client.input.CombatKeyMapping;

@EventBusSubscriber(
   modid = "invincible_dmc",
   value = {Dist.CLIENT},
   bus = Bus.MOD
)
public class DMCKeyMappings {
   public static final KeyMapping KEY1 = new CombatKeyMapping("key.invincible_dmc.key1", Type.MOUSE, 0, "key.invincible_dmc.category");
   public static final KeyMapping KEY2 = new CombatKeyMapping("key.invincible_dmc.key2", Type.MOUSE, 1, "key.invincible_dmc.category");
   public static final KeyMapping KEY3 = new CombatKeyMapping("key.invincible_dmc.key3", Type.MOUSE, 4, "key.invincible_dmc.category");
   public static final KeyMapping KEY4 = new CombatKeyMapping("key.invincible_dmc.key4", Type.MOUSE, 3, "key.invincible_dmc.category");
   public static final KeyMapping DOPPEL_FAST = new CombatKeyMapping("key.invincible_dmc.doppel_fast", Type.KEYSYM, 90, "key.invincible_dmc.category");
   public static final KeyMapping DOPPEL_MEDIUM = new CombatKeyMapping("key.invincible_dmc.doppel_medium", Type.KEYSYM, 88, "key.invincible_dmc.category");
   public static final KeyMapping DOPPEL_SLOW = new CombatKeyMapping("key.invincible_dmc.doppel_slow", Type.KEYSYM, 86, "key.invincible_dmc.category");
   public static final KeyMapping DOPPEL_CONTROL = new CombatKeyMapping("key.invincible_dmc.doppel_control", Type.KEYSYM, 70, "key.invincible_dmc.category");
   public static final KeyMapping DOPPEL_DISCARD = new CombatKeyMapping("key.invincible_dmc.doppel_discard", Type.KEYSYM, 67, "key.invincible_dmc.category");
   public static final KeyMapping DMC_LOCK_ON = new CombatKeyMapping("key.invincible_dmc.lock_on", Type.KEYSYM, 341, "key.invincible_dmc.category");
   public static final KeyMapping DMC_DODGE = new CombatKeyMapping("key.invincible_dmc.dodge", Type.MOUSE, 2, "key.invincible_dmc.category");
   public static final KeyMapping PROVOCATION = new CombatKeyMapping("key.invincible_dmc.provocation", Type.KEYSYM, 82, "key.invincible_dmc.category");
   public static final KeyMapping SDT_CHARGE = new CombatKeyMapping("key.invincible_dmc.sdt_charge", Type.KEYSYM, 341, "key.invincible_dmc.category");
   public static final KeyMapping WEAPON_SWITCH = new CombatKeyMapping("key.invincible_dmc.weapon_switch", Type.KEYSYM, 71, "key.invincible_dmc.category");

   @SubscribeEvent
   public static void registerKeys(RegisterKeyMappingsEvent event) {
      event.register(KEY1);
      event.register(KEY2);
      event.register(KEY3);
      event.register(DOPPEL_FAST);
      event.register(DOPPEL_MEDIUM);
      event.register(DOPPEL_SLOW);
      event.register(DOPPEL_CONTROL);
      event.register(DOPPEL_DISCARD);
      event.register(DMC_LOCK_ON);
      event.register(PROVOCATION);
      event.register(SDT_CHARGE);
      if (DmcWeaponManager.isWeaponSwitchEnabled()) {
         event.register(WEAPON_SWITCH);
      }
   }

   public static Component getName(KeyMapping keyMapping) {
      return keyMapping.m_90863_();
   }

   public static boolean isLockOnBoundToCrouch() {
      return DMC_LOCK_ON.m_90850_(Minecraft.m_91087_().f_91066_.f_92090_);
   }

   public static Component getTranslatableKey1() {
      return getName(KEY1);
   }

   public static Component getTranslatableKey2() {
      return getName(KEY2);
   }

   public static Component getTranslatableKey3() {
      return getName(KEY3);
   }

   public static Component getTranslatableKey4() {
      return getName(KEY4);
   }
}
