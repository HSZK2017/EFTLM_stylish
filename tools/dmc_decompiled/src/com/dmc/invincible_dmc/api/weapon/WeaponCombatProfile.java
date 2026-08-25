package com.dmc.invincible_dmc.api.weapon;

import com.dmc.invincible_dmc.api.skill.ComboNode;
import java.util.Objects;

public record WeaponCombatProfile(DmcWeaponType type, ComboNode root, int comboResetTicks) {
   public WeaponCombatProfile(DmcWeaponType type, ComboNode root, int comboResetTicks) {
      Objects.requireNonNull(type);
      Objects.requireNonNull(root);
      if (comboResetTicks < 0) {
         throw new IllegalArgumentException("comboResetTicks must be non-negative");
      } else {
         this.type = type;
         this.root = root;
         this.comboResetTicks = comboResetTicks;
      }
   }
}
