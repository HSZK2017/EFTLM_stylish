package com.pla.annoyingvillagers.mixin.plugin;

import java.util.List;
import java.util.Set;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public final class CompatMixinPlugin implements IMixinConfigPlugin {
   private static final String EFN_COMPAT_PREFIX = "com.pla.annoyingvillagers.mixin.compat.efn.";
   private static final String DUAL_AXES_COMPAT_PREFIX = "com.pla.annoyingvillagers.mixin.compat.dualaxes.";
   private static final String DUAL_GREATSWORDS_COMPAT_PREFIX = "com.pla.annoyingvillagers.mixin.compat.dualgreatswords.";
   private static final String CLASH_BLADE_MIXIN = "com.pla.annoyingvillagers.mixin.ClashBladeMixin";
   private static final String MOB_CLASH_BLADE_MIXIN = "com.pla.annoyingvillagers.mixin.MobClashBladeMixin";

   private static boolean isModLoadedEarly(String modId) {
      LoadingModList list = FMLLoader.getLoadingModList();
      return list != null && list.getModFileById(modId) != null;
   }

   public void onLoad(String mixinPackage) {
   }

   public String getRefMapperConfig() {
      return null;
   }

   public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      if (mixinClassName.startsWith("com.pla.annoyingvillagers.mixin.compat.efn.")) {
         return isModLoadedEarly("efn");
      } else if (mixinClassName.startsWith("com.pla.annoyingvillagers.mixin.compat.dualaxes.")) {
         return isModLoadedEarly("dualaxes");
      } else if (mixinClassName.startsWith("com.pla.annoyingvillagers.mixin.compat.dualgreatswords.")) {
         return isModLoadedEarly("dualgreatswords");
      } else {
         return !mixinClassName.equals("com.pla.annoyingvillagers.mixin.ClashBladeMixin")
               && !mixinClassName.equals("com.pla.annoyingvillagers.mixin.MobClashBladeMixin")
            ? true
            : isModLoadedEarly("efclash_blade");
      }
   }

   public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   public List<String> getMixins() {
      return null;
   }

   public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }
}
