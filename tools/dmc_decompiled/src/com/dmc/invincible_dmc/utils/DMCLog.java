package com.dmc.invincible_dmc.utils;

import com.dmc.invincible_dmc.DMConfig;
import com.mojang.logging.LogUtils;
import java.util.EnumSet;
import java.util.Locale;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;

public final class DMCLog {
   private static final EnumSet<DMCLog.Category> ENABLED = EnumSet.noneOf(DMCLog.Category.class);

   public static void loadFromConfig() {
      setByConfig(DMCLog.Category.COMBO_ENGINE, DMConfig.LOG_COMBO_ENGINE);
      setByConfig(DMCLog.Category.COMBO_EXECUTE, DMConfig.LOG_COMBO_EXECUTE);
      setByConfig(DMCLog.Category.COMBO_SERVER, DMConfig.LOG_COMBO_SERVER);
      setByConfig(DMCLog.Category.DOPPEL_INPUT, DMConfig.LOG_DOPPEL_INPUT);
      setByConfig(DMCLog.Category.DOPPEL_COMBO, DMConfig.LOG_DOPPEL_COMBO);
      setByConfig(DMCLog.Category.DOPPEL_CC, DMConfig.LOG_DOPPEL_CC);
      setByConfig(DMCLog.Category.DOPPEL_GENERAL, DMConfig.LOG_DOPPEL_GENERAL);
      setByConfig(DMCLog.Category.DOPPEL_NET, DMConfig.LOG_DOPPEL_NET);
      setByConfig(DMCLog.Category.JC, DMConfig.LOG_JC);
      setByConfig(DMCLog.Category.SWORD, DMConfig.LOG_SWORD);
      setByConfig(DMCLog.Category.YAMATO, DMConfig.LOG_YAMATO);
      setByConfig(DMCLog.Category.NETWORK, DMConfig.LOG_NETWORK);
      setByConfig(DMCLog.Category.RENDER, DMConfig.LOG_RENDER);
      setByConfig(DMCLog.Category.COMPAT, DMConfig.LOG_COMPAT);
      setByConfig(DMCLog.Category.DIRECTION, DMConfig.LOG_DIRECTION);
      setByConfig(DMCLog.Category.CAPABILITY, DMConfig.LOG_CAPABILITY);
      setByConfig(DMCLog.Category.STUN, DMConfig.LOG_STUN);
   }

   private static void setByConfig(DMCLog.Category cat, BooleanValue config) {
      if ((Boolean)config.get()) {
         ENABLED.add(cat);
      } else {
         ENABLED.remove(cat);
      }
   }

   public static void enable(DMCLog.Category cat) {
      ENABLED.add(cat);
      syncConfig(cat, true);
   }

   public static void disable(DMCLog.Category cat) {
      ENABLED.remove(cat);
      syncConfig(cat, false);
   }

   public static void toggle(DMCLog.Category cat) {
      if (ENABLED.contains(cat)) {
         disable(cat);
      } else {
         enable(cat);
      }
   }

   public static boolean isEnabled(DMCLog.Category cat) {
      return ENABLED.contains(cat);
   }

   private static void syncConfig(DMCLog.Category cat, boolean value) {
      switch (cat) {
         case COMBO_ENGINE:
            DMConfig.LOG_COMBO_ENGINE.set(value);
            break;
         case COMBO_EXECUTE:
            DMConfig.LOG_COMBO_EXECUTE.set(value);
            break;
         case COMBO_SERVER:
            DMConfig.LOG_COMBO_SERVER.set(value);
            break;
         case DOPPEL_INPUT:
            DMConfig.LOG_DOPPEL_INPUT.set(value);
            break;
         case DOPPEL_COMBO:
            DMConfig.LOG_DOPPEL_COMBO.set(value);
            break;
         case DOPPEL_CC:
            DMConfig.LOG_DOPPEL_CC.set(value);
            break;
         case DOPPEL_GENERAL:
            DMConfig.LOG_DOPPEL_GENERAL.set(value);
            break;
         case DOPPEL_NET:
            DMConfig.LOG_DOPPEL_NET.set(value);
            break;
         case JC:
            DMConfig.LOG_JC.set(value);
            break;
         case SWORD:
            DMConfig.LOG_SWORD.set(value);
            break;
         case YAMATO:
            DMConfig.LOG_YAMATO.set(value);
            break;
         case NETWORK:
            DMConfig.LOG_NETWORK.set(value);
            break;
         case RENDER:
            DMConfig.LOG_RENDER.set(value);
            break;
         case COMPAT:
            DMConfig.LOG_COMPAT.set(value);
            break;
         case DIRECTION:
            DMConfig.LOG_DIRECTION.set(value);
            break;
         case CAPABILITY:
            DMConfig.LOG_CAPABILITY.set(value);
            break;
         case STUN:
            DMConfig.LOG_STUN.set(value);
      }
   }

   public static String listStatus() {
      StringBuilder sb = new StringBuilder("Log categories:\n");

      for (DMCLog.Category cat : DMCLog.Category.values()) {
         sb.append(String.format("  %-16s [%s]\n", cat.name(), ENABLED.contains(cat) ? "ON" : "OFF"));
      }

      return sb.toString();
   }

   private static String tag(DMCLog.Category cat) {
      return "[" + cat.name() + "]";
   }

   public static void info(DMCLog.Category cat, String msg) {
      if (ENABLED.contains(cat)) {
         LogUtils.getLogger().info(tag(cat) + " " + msg);
      }
   }

   public static void info(DMCLog.Category cat, String msg, Object arg1) {
      if (ENABLED.contains(cat)) {
         LogUtils.getLogger().info(tag(cat) + " " + msg, arg1);
      }
   }

   public static void info(DMCLog.Category cat, String msg, Object arg1, Object arg2) {
      if (ENABLED.contains(cat)) {
         LogUtils.getLogger().info(tag(cat) + " " + msg, arg1, arg2);
      }
   }

   public static void info(DMCLog.Category cat, String msg, Object... args) {
      if (ENABLED.contains(cat)) {
         LogUtils.getLogger().info(tag(cat) + " " + msg, args);
      }
   }

   public static void debug(DMCLog.Category cat, String msg) {
      if (ENABLED.contains(cat)) {
         LogUtils.getLogger().debug(tag(cat) + " " + msg);
      }
   }

   public static void debug(DMCLog.Category cat, String msg, Object arg1) {
      if (ENABLED.contains(cat)) {
         LogUtils.getLogger().debug(tag(cat) + " " + msg, arg1);
      }
   }

   public static void debug(DMCLog.Category cat, String msg, Object arg1, Object arg2) {
      if (ENABLED.contains(cat)) {
         LogUtils.getLogger().debug(tag(cat) + " " + msg, arg1, arg2);
      }
   }

   public static void debug(DMCLog.Category cat, String msg, Object... args) {
      if (ENABLED.contains(cat)) {
         LogUtils.getLogger().debug(tag(cat) + " " + msg, args);
      }
   }

   public static void warn(DMCLog.Category cat, String msg) {
      if (ENABLED.contains(cat)) {
         LogUtils.getLogger().warn(tag(cat) + " " + msg);
      }
   }

   public static void warn(DMCLog.Category cat, String msg, Object arg1) {
      if (ENABLED.contains(cat)) {
         LogUtils.getLogger().warn(tag(cat) + " " + msg, arg1);
      }
   }

   public static void warn(DMCLog.Category cat, String msg, Object arg1, Object arg2) {
      if (ENABLED.contains(cat)) {
         LogUtils.getLogger().warn(tag(cat) + " " + msg, arg1, arg2);
      }
   }

   public static void warn(DMCLog.Category cat, String msg, Object... args) {
      if (ENABLED.contains(cat)) {
         LogUtils.getLogger().warn(tag(cat) + " " + msg, args);
      }
   }

   public static void error(DMCLog.Category cat, String msg) {
      if (ENABLED.contains(cat)) {
         LogUtils.getLogger().error(tag(cat) + " " + msg);
      }
   }

   public static void error(DMCLog.Category cat, String msg, Object arg1) {
      if (ENABLED.contains(cat)) {
         LogUtils.getLogger().error(tag(cat) + " " + msg, arg1);
      }
   }

   public static void error(DMCLog.Category cat, String msg, Object arg1, Object arg2) {
      if (ENABLED.contains(cat)) {
         LogUtils.getLogger().error(tag(cat) + " " + msg, arg1, arg2);
      }
   }

   public static void error(DMCLog.Category cat, String msg, Object... args) {
      if (ENABLED.contains(cat)) {
         LogUtils.getLogger().error(tag(cat) + " " + msg, args);
      }
   }

   public static enum Category {
      COMBO_ENGINE,
      COMBO_EXECUTE,
      COMBO_SERVER,
      DOPPEL_INPUT,
      DOPPEL_COMBO,
      DOPPEL_CC,
      DOPPEL_GENERAL,
      DOPPEL_NET,
      JC,
      SWORD,
      YAMATO,
      NETWORK,
      RENDER,
      COMPAT,
      DIRECTION,
      CAPABILITY,
      STUN;

      public String configKey() {
         return "log." + this.name().toLowerCase(Locale.ROOT).replace("__", "_");
      }
   }
}
