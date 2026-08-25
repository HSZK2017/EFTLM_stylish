package com.Yujin.onegradefixer.epicmoonmod.skill;

import com.Yujin.onegradefixer.epicmoonmod.skill.weapon_innate.DualInnate;
import com.Yujin.onegradefixer.epicmoonmod.skill.weapon_innate.TsInnate;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.api.utils.PacketBufferCodec;
import yesman.epicfight.skill.SkillDataKey;

public final class EMSkillDataKeys {
   public static final DeferredRegister<SkillDataKey<?>> DATA_KEYS = DeferredRegister.create(
      ResourceLocation.fromNamespaceAndPath("epicfight", "skill_data_keys"), "epicmoonmod"
   );
   public static final RegistryObject<SkillDataKey<Integer>> DUAL_EYE = DATA_KEYS.register(
      "dual_eye", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 30, true, new Class[]{DualInnate.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> DUAL_SIN = DATA_KEYS.register(
      "dual_sin", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{DualInnate.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> DUAL_RESTORE_TIME = DATA_KEYS.register(
      "dual_restore_time", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, false, new Class[]{DualInnate.class})
   );
   public static final RegistryObject<SkillDataKey<Float>> TS_AURA = DATA_KEYS.register(
      "ts_aura", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.FLOAT, 0.0F, true, new Class[]{TsInnate.class})
   );

   private EMSkillDataKeys() {
   }
}
