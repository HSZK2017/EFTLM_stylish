package com.dmc.invincible_dmc.client.model;

import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures.ArmatureAccessor;

public class DMCArmatures {
   public static ArmatureAccessor<Armature> SUMMONED_SWORD = ArmatureAccessor.create(
      "invincible_dmc", "entity/effect/summoned_sword/summoned_sword", Armature::new
   );
   public static ArmatureAccessor<Armature> SUMMONED_SWORD_CIRCLE = ArmatureAccessor.create(
      "invincible_dmc", "entity/effect/summoned_sword/summoned_sword_circle", Armature::new
   );
   public static ArmatureAccessor<Armature> JUDGEMENT_CUT = ArmatureAccessor.create("invincible_dmc", "entity/effect/judgement_cut", Armature::new);
}
