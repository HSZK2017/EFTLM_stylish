package com.dmc.invincible_dmc.api.skill;

import java.util.List;
import yesman.epicfight.api.utils.ExtendableEnum;
import yesman.epicfight.api.utils.ExtendableEnumManager;

public interface ComboType extends ExtendableEnum {
   ExtendableEnumManager<ComboType> ENUM_MANAGER = new ExtendableEnumManager("combo_type");

   List<ComboType> getSubTypes();
}
