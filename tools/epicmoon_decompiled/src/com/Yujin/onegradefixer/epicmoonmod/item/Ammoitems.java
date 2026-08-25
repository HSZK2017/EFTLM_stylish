package com.Yujin.onegradefixer.epicmoonmod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public class Ammoitems extends Item {
   public int AmmoType;

   public Ammoitems(int i, Properties builder) {
      super(builder);
      this.AmmoType = i;
   }
}
