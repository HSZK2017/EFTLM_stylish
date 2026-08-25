package com.Yujin.onegradefixer.epicmoonmod.client;

import com.Yujin.onegradefixer.epicmoonmod.item.LeiHengArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class LeiHengArmorModel extends GeoModel<LeiHengArmorItem> {
   public ResourceLocation getModelResource(LeiHengArmorItem leiHengArmorItem) {
      return new ResourceLocation("epicmoonmod", "geo/leiheng_armor.geo.json");
   }

   public ResourceLocation getTextureResource(LeiHengArmorItem leiHengArmorItem) {
      return new ResourceLocation("epicmoonmod", "textures/armor/leiheng_armor.png");
   }

   public ResourceLocation getAnimationResource(LeiHengArmorItem leiHengArmorItem) {
      return new ResourceLocation("epicmoonmod", "animations/leiheng.animation.json");
   }
}
