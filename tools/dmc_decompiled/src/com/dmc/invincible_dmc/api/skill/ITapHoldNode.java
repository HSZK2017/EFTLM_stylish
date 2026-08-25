package com.dmc.invincible_dmc.api.skill;

import org.jetbrains.annotations.Nullable;

public interface ITapHoldNode {
   @Nullable
   SubComboNode getTap();

   @Nullable
   SubComboNode getHold();

   int getWindupDurationTicks();
}
