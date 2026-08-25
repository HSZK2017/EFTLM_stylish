package com.dmc.invincible_dmc.compat.oculus;

import com.dmc.invincible_dmc.compat.ICompatModule;
import com.dmc.invincible_dmc.utils.DMCLog;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@OnlyIn(Dist.CLIENT)
public class OculusCompat implements ICompatModule {
   private static volatile OculusCompat.Backend backend = OculusCompat.Backend.NONE;

   public static boolean isShaderActive() {
      return backend.isShaderActive();
   }

   public static RenderType wrapEndPortalRenderType(RenderType renderType) {
      return backend.wrapEndPortalRenderType(renderType);
   }

   public static int beginEndPortalBlockEntityContext() {
      return backend.beginEndPortalBlockEntityContext();
   }

   public static void restoreBlockEntityContext(int blockEntityId) {
      backend.restoreBlockEntityContext(blockEntityId);
   }

   @Override
   public void onLoad(FMLJavaModLoadingContext context) {
      try {
         backend = OculusRuntimeCompat.create();
         DMCLog.info(DMCLog.Category.COMPAT, "[OculusCompat] Oculus API bridge loaded");
      } catch (RuntimeException | LinkageError var3) {
         backend = OculusCompat.Backend.NONE;
         DMCLog.warn(DMCLog.Category.COMPAT, "[OculusCompat] Oculus API bridge unavailable; compatibility disabled", var3);
      }
   }

   interface Backend {
      OculusCompat.Backend NONE = new OculusCompat.Backend() {
      };

      default boolean isShaderActive() {
         return false;
      }

      default RenderType wrapEndPortalRenderType(RenderType renderType) {
         return renderType;
      }

      default int beginEndPortalBlockEntityContext() {
         return 0;
      }

      default void restoreBlockEntityContext(int blockEntityId) {
      }
   }
}
