package com.pla.annoyingvillagers.gameasset;

import com.pla.annoyingvillagers.skill.TridentFestivalSkill;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.api.utils.PacketBufferCodec;
import yesman.epicfight.skill.SkillDataKey;

public class AVSkillDataKeys {
   public static final DeferredRegister<SkillDataKey<?>> DATA_KEYS = DeferredRegister.create(
      ResourceLocation.fromNamespaceAndPath("epicfight", "skill_data_keys"), "annoyingvillagers"
   );
   public static final RegistryObject<SkillDataKey<Boolean>> IS_TRIDENT_RANGED_MODE = DATA_KEYS.register(
      "is_trident_ranged_mode", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{TridentFestivalSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> TRIDENT_AMOUNT = DATA_KEYS.register(
      "trident_amount", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{TridentFestivalSkill.class})
   );
}
