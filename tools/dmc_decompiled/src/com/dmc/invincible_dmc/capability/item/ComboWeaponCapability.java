package com.dmc.invincible_dmc.capability.item;

import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;

public class ComboWeaponCapability {
   public static ComboWeaponCapability.Builder builder() {
      return new ComboWeaponCapability.Builder();
   }

   public static class Builder extends yesman.epicfight.world.capabilities.item.WeaponCapability.Builder {
      protected Builder() {
         this.newStyleCombo(Styles.COMMON, new AnimationAccessor[]{Animations.SWORD_AUTO1, Animations.SWORD_AUTO1, Animations.SWORD_AUTO1});
      }
   }
}
