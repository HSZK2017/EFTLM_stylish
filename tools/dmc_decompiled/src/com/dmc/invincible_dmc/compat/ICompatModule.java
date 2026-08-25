package com.dmc.invincible_dmc.compat;

import java.lang.reflect.Constructor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public interface ICompatModule {
   static void loadCompatModule(FMLJavaModLoadingContext context, Class<? extends ICompatModule> clazz) {
      try {
         Constructor<? extends ICompatModule> ctor = clazz.getConstructor();
         ICompatModule instance = ctor.newInstance();
         instance.onLoad(context);
      } catch (Exception var4) {
         throw new RuntimeException("Failed to load compat module: " + clazz.getSimpleName(), var4);
      }
   }

   void onLoad(FMLJavaModLoadingContext var1);
}
