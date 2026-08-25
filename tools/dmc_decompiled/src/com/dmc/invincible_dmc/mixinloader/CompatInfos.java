package com.dmc.invincible_dmc.mixinloader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import net.minecraftforge.fml.loading.FMLLoader;

public class CompatInfos {
   public static final HashMap<CompatInfos.MixinClassName, CompatInfos.CompatMixinInfo> CompatMixins = Maps.newHashMap();
   static final List<CompatInfos.AbstractCompatMod> CompatMods = Lists.newArrayList();
   public static CompatInfos.AbstractCompatMod LeawindThirdPerson;
   public static CompatInfos.AbstractCompatMod CATACLYSM_MOD;
   public static CompatInfos.AbstractCompatMod LIONFISHAPI;
   public static CompatInfos.AbstractCompatMod CITADEL;
   public static CompatInfos.AbstractCompatMod GECKOLIB;
   public static CompatInfos.AbstractCompatMod COMBAT_EVOLUTION;
   public static CompatInfos.AbstractCompatMod AAA_PARTICLES;
   public static CompatInfos.AbstractCompatMod ARMOURERS_WORKSHOP;

   static void register() {
      LIONFISHAPI = new CompatInfos.CompatMod("lionfishapi", m -> new CompatInfos.CompatMixinInfo(m, "AnimationHandlerMixin"));
      CITADEL = new CompatInfos.CompatMod("citadel", m -> new CompatInfos.CompatMixinInfo(m, "AnimationHandlerMixin"));
      GECKOLIB = new CompatInfos.CompatMod("geckolib", m -> new CompatInfos.CompatMixinInfo(m, "AnimationProcessorMixin"));
      COMBAT_EVOLUTION = new CompatInfos.CompatMod("combat_evolution", m -> new CompatInfos.CompatMixinInfo(m, "ExecutionHandlerMixin"));
      AAA_PARTICLES = new CompatInfos.CompatMod(
         "aaa_particles",
         m -> new CompatInfos.CompatMixinInfo(m, "AAAPEffekRendererMixin"),
         m -> new CompatInfos.CompatMixinInfo(m, "AAAPEffectDefinitionMixin"),
         m -> new CompatInfos.CompatMixinInfo(m, "AAAPCollisionCallbackMixin"),
         m -> new CompatInfos.CompatMixinInfo(m, "AAAPParticleEmitterMixin"),
         m -> new CompatInfos.CompatMixinInfo(m, "AAAPRenderUtilMixin"),
         m -> new CompatInfos.CompatMixinInfo(m, "AAAPSamplerRestorerMixin")
      );
      ARMOURERS_WORKSHOP = new CompatInfos.CompatMod("armourers_workshop", m -> new CompatInfos.CompatMixinInfo(m, "ArmourersWorkshopRenderLayerMixin"));
      CATACLYSM_MOD = new CompatInfos.CompatMod(
         "cataclysm", m -> new CompatInfos.CompatMixinInfo(m, "MixinLLibraryBossMonster"), m -> new CompatInfos.CompatMixinInfo(m, "MixinIABossMonster")
      );
   }

   public static void initCompatInfo() {
      register();
      CompatMods.forEach(CompatInfos.AbstractCompatMod::check);
   }

   static String getClassName(String classPath) {
      String[] s = classPath.split("\\.");
      return s[s.length - 1];
   }

   public static boolean shouldMixin(String targetClassName, String mixinClassName_) {
      String mixinClassName = getClassName(mixinClassName_);
      if (CompatMixins.containsKey(CompatInfos.MixinClassName.of(mixinClassName))) {
         boolean should = CompatMixins.get(CompatInfos.MixinClassName.of(mixinClassName)).shouldApplyMixin();
         if (should) {
            System.out.println("[Invincible MixinLoader] Apply Compat Mixin: " + mixinClassName_ + " -> " + targetClassName);
         } else {
            System.out.println("[Invincible MixinLoader] Skip Compat Mixin: " + mixinClassName_ + " -> " + targetClassName);
         }

         return should;
      } else {
         return true;
      }
   }

   public abstract static class AbstractCompatMod {
      protected boolean loaded;

      public AbstractCompatMod() {
         CompatInfos.CompatMods.add(this);
      }

      public abstract void check();

      public boolean isLoaded() {
         return this.loaded;
      }
   }

   public static class CompatMixinInfo {
      protected final CompatInfos.AbstractCompatMod mod;

      public CompatMixinInfo(CompatInfos.AbstractCompatMod mod, String mixinClass) {
         this.mod = mod;
         CompatInfos.CompatMixins.put(CompatInfos.MixinClassName.of(mixinClass), this);
      }

      public boolean shouldApplyMixin() {
         return this.mod.isLoaded();
      }
   }

   public static class CompatMod extends CompatInfos.AbstractCompatMod {
      final String modid;

      public CompatMod(String modid, String... mixinClasses) {
         this.modid = modid;

         for (String mixinClass : mixinClasses) {
            new CompatInfos.CompatMixinInfo(this, mixinClass);
         }
      }

      @SafeVarargs
      public CompatMod(String modid, Function<CompatInfos.AbstractCompatMod, CompatInfos.CompatMixinInfo>... creators) {
         this.modid = modid;

         for (Function<CompatInfos.AbstractCompatMod, CompatInfos.CompatMixinInfo> creator : creators) {
            creator.apply(this);
         }
      }

      @Override
      public void check() {
         this.loaded = FMLLoader.getLoadingModList().getModFileById(this.modid) != null;
      }

      @Override
      public boolean isLoaded() {
         return this.loaded;
      }
   }

   public static record MixinClassName(String className) {
      public static CompatInfos.MixinClassName of(String n) {
         return new CompatInfos.MixinClassName(n);
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else {
            return o instanceof CompatInfos.MixinClassName that ? this.className.equals(that.className) : false;
         }
      }

      @Override
      public int hashCode() {
         return this.className.hashCode();
      }
   }
}
