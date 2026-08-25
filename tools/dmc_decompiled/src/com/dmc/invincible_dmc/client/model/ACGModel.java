package com.dmc.invincible_dmc.client.model;

import com.guhao.vix.client.NoTextureJsonModel;
import com.guhao.vix.util.OjangUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ACGModel {
   public static NoTextureJsonModel SpaceBrokenModel;
   public static NoTextureJsonModel Sphere;

   public static void LoadOtherModel() {
      SpaceBrokenModel = NoTextureJsonModel.loadFromJson(OjangUtils.newRL("invincible_dmc", "models/effect/spacebroken.json"));
      Sphere = NoTextureJsonModel.loadFromJson(OjangUtils.newRL("invincible_dmc", "models/effect/sphere.json"));
   }
}
