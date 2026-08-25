package com.dmc.invincible_dmc.gameassets;

import com.dmc.invincible_dmc.api.skill.JudgementCutChargePhase;
import com.dmc.invincible_dmc.skill.dodge.VergilDodgeSkill;
import com.dmc.invincible_dmc.skill.weapon_innate.AbstractDmcInnateSkill;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.api.utils.PacketBufferCodec;
import yesman.epicfight.skill.SkillDataKey;

public class DMCSkillDataKeys {
   public static final PacketBufferCodec<Long> LONG = new PacketBufferCodec<Long>() {
      public void encode(Long obj, FriendlyByteBuf buffer) {
         buffer.writeLong(obj);
      }

      public Long decode(FriendlyByteBuf buffer) {
         return buffer.readLong();
      }
   };
   public static final PacketBufferCodec<ResourceLocation> RESOURCE_LOCATION = new PacketBufferCodec<ResourceLocation>() {
      public void encode(ResourceLocation obj, FriendlyByteBuf buffer) {
         buffer.m_130085_(obj);
      }

      public ResourceLocation decode(FriendlyByteBuf buffer) {
         return buffer.m_130281_();
      }
   };
   public static final DeferredRegister<SkillDataKey<?>> DATA_KEYS = DeferredRegister.create(
      ResourceLocation.fromNamespaceAndPath("epicfight", "skill_data_keys"), "invincible_dmc"
   );
   public static final RegistryObject<SkillDataKey<Boolean>> CAMERA_LOCKING_ON = DATA_KEYS.register(
      "camera_locking_on", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> PARRY_TIMER = DATA_KEYS.register(
      "parry_timer", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Boolean>> PARRY_TOGGLE = DATA_KEYS.register(
      "parry_toggle", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> DODGE_SUCCESS_TIMER = DATA_KEYS.register(
      "dodge_success_timer", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> COOLDOWN = DATA_KEYS.register(
      "cooldown", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Boolean>> IS_ON_GROUND = DATA_KEYS.register(
      "is_on_ground", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, true, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> AERIAL_ATTACK_COUNT = DATA_KEYS.register(
      "aerial_attack_count", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> AIR_TIME_TICKS = DATA_KEYS.register(
      "air_time_ticks", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> UP_DODGE_COUNT = DATA_KEYS.register(
      "up_dodge_count", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{VergilDodgeSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> AIR_DODGE_COUNT = DATA_KEYS.register(
      "air_dodge_count", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{VergilDodgeSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> DODGE_COUNTER_SUCCESS_TIMER = DATA_KEYS.register(
      "dodge_counter_success_timer", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{VergilSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Boolean>> PERFECT_DODGE_CHAIN_FREE = DATA_KEYS.register(
      "perfect_dodge_chain_free", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, false, new Class[]{VergilSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Long>> YAMATO_AUTO_REFLECT_WINDOW_UNTIL = DATA_KEYS.register(
      "yamato_auto_reflect_window_until", () -> SkillDataKey.createSkillDataKey(LONG, 0L, false, new Class[]{VergilSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> JUDGEMENT_CUT_CHARGE_PHASE = DATA_KEYS.register(
      "judgement_cut_charge_phase",
      () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, JudgementCutChargePhase.IDLE.networkId(), false, new Class[]{VergilSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Float>> AERIAL_ACTION_COUNT = DATA_KEYS.register(
      "aerial_action_count", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.FLOAT, 0.0F, true, new Class[]{VergilSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Float>> CONCENTRATION = DATA_KEYS.register(
      "concentration", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.FLOAT, 0.0F, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Boolean>> IS_SDT = DATA_KEYS.register(
      "is_sdt", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Float>> SDT_VALUE = DATA_KEYS.register(
      "sdt_value", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.FLOAT, 0.0F, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> SDT_PHASE = DATA_KEYS.register(
      "sdt_phase", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> SDT_CONFIRM_TIMER = DATA_KEYS.register(
      "sdt_confirm_timer", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> CONC_LAST_TIER = DATA_KEYS.register(
      "conc_last_tier", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> SDT_CHARGE_HOLD_TIMER = DATA_KEYS.register(
      "sdt_charge_hold_timer", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Boolean>> SDT_PREV_CHARGING = DATA_KEYS.register(
      "sdt_prev_charging", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> SDT_TICK_COUNTER = DATA_KEYS.register(
      "sdt_tick_counter", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> SDT_SECOND_DELAY_TIMER = DATA_KEYS.register(
      "sdt_second_delay_timer", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Boolean>> SDT_FIRST_CHARGE_FIRED = DATA_KEYS.register(
      "sdt_first_charge_fired", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Boolean>> SDT_SECOND_CHARGE_FIRED = DATA_KEYS.register(
      "sdt_second_charge_fired", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Boolean>> SDT_WAS_MAX_AT_START = DATA_KEYS.register(
      "sdt_was_max_at_start", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> SDT_PREV_PHASE = DATA_KEYS.register(
      "sdt_prev_phase", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<ResourceLocation>> LAST_MISS_PROCESSED = DATA_KEYS.register(
      "last_miss_processed",
      () -> SkillDataKey.createSkillDataKey(
            RESOURCE_LOCATION, ResourceLocation.fromNamespaceAndPath("empty", "empty"), true, new Class[]{AbstractDmcInnateSkill.class}
         )
   );
   public static final RegistryObject<SkillDataKey<Integer>> ATTACK_CYCLE_ID = DATA_KEYS.register(
      "attack_cycle_id", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> ATTACK_CYCLE_HIT_MASK = DATA_KEYS.register(
      "attack_cycle_hit_mask", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Integer>> ATTACK_CYCLE_PROCESSED_MASK = DATA_KEYS.register(
      "attack_cycle_processed_mask", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.INTEGER, 0, true, new Class[]{AbstractDmcInnateSkill.class})
   );
   public static final RegistryObject<SkillDataKey<Boolean>> SDT_PHASE1_TICK_ACTIVE = DATA_KEYS.register(
      "sdt_phase1_tick_active", () -> SkillDataKey.createSkillDataKey(PacketBufferCodec.BOOLEAN, false, true, new Class[]{AbstractDmcInnateSkill.class})
   );
}
