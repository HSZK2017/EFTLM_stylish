package com.Yujin.onegradefixer.epicmoonmod.client;

import com.Yujin.onegradefixer.epicmoonmod.item.LeiHengArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class LeiHengArmorRenderer extends GeoArmorRenderer<LeiHengArmorItem> {
   public LeiHengArmorRenderer() {
      super(new LeiHengArmorModel());
   }
}
