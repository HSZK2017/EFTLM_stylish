package com.dmc.invincible_dmc.gameassets;

import yesman.epicfight.world.capabilities.item.WeaponCategory;

public enum DMCWeaponCategories implements WeaponCategory {
   DMC5_YAMATO;

   final int id = WeaponCategory.ENUM_MANAGER.assign(this);

   public int universalOrdinal() {
      return this.id;
   }
}
