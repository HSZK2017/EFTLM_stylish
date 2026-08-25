package com.dmc.invincible_dmc.compat.waystones;

import com.dmc.invincible_dmc.compat.ICompatModule;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.WaystonesAPI;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class WaystonesCompat implements ICompatModule {
   private static boolean loaded = false;

   public static boolean isLoaded() {
      return loaded;
   }

   public static List<IWaystone> getPlayerWaystones(Player player) {
      if (loaded && player != null) {
         try {
            Collection<IWaystone> waystones = WaystonesAPI.getActivatedWaystones(player);
            return (List<IWaystone>)(waystones == null ? Collections.emptyList() : new ArrayList<>(waystones));
         } catch (Exception var2) {
            DMCLog.warn(DMCLog.Category.COMPAT, "[WaystonesCompat] 获取玩家传送石碑失败", var2);
            return Collections.emptyList();
         }
      } else {
         return Collections.emptyList();
      }
   }

   public static boolean teleportToWaystone(Entity entity, UUID waystoneUid) {
      if (loaded && entity != null && waystoneUid != null) {
         try {
            IWaystone waystone = (IWaystone)WaystonesAPI.getWaystone(entity.m_20194_(), waystoneUid).orElse(null);
            if (waystone == null) {
               DMCLog.warn(DMCLog.Category.COMPAT, "[WaystonesCompat] 未找到传送石碑: {}", waystoneUid);
               return false;
            } else {
               WaystonesAPI.forceTeleportToWaystone(entity, waystone);
               return true;
            }
         } catch (Exception var3) {
            DMCLog.warn(DMCLog.Category.COMPAT, "[WaystonesCompat] 传送至石碑失败", var3);
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public void onLoad(FMLJavaModLoadingContext context) {
      loaded = true;
      DMCLog.info(DMCLog.Category.COMPAT, "[WaystonesCompat] Waystones 集成已加载");
   }
}
