package com.dmc.invincible_dmc.gameassets;

import com.dmc.invincible_dmc.conditions.AerialAttackLimitCondition;
import com.dmc.invincible_dmc.conditions.BlockingCondition;
import com.dmc.invincible_dmc.conditions.ConcentrationTierCondition;
import com.dmc.invincible_dmc.conditions.CooldownCondition;
import com.dmc.invincible_dmc.conditions.DirectionCondition;
import com.dmc.invincible_dmc.conditions.DirectionalSequenceCondition;
import com.dmc.invincible_dmc.conditions.DodgeSuccessCondition;
import com.dmc.invincible_dmc.conditions.EnchantmentCondition;
import com.dmc.invincible_dmc.conditions.InTargetPovCondition;
import com.dmc.invincible_dmc.conditions.JumpCondition;
import com.dmc.invincible_dmc.conditions.JumpKeyCondition;
import com.dmc.invincible_dmc.conditions.LockedOnTargetCondition;
import com.dmc.invincible_dmc.conditions.LockonKeyCondition;
import com.dmc.invincible_dmc.conditions.LongPressCondition;
import com.dmc.invincible_dmc.conditions.MobEffectCondition;
import com.dmc.invincible_dmc.conditions.ParrySuccessCondition;
import com.dmc.invincible_dmc.conditions.PlayerPhaseCondition;
import com.dmc.invincible_dmc.conditions.PovTargetPovAngle;
import com.dmc.invincible_dmc.conditions.PressIntervalCondition;
import com.dmc.invincible_dmc.conditions.PressedTimeCondition;
import com.dmc.invincible_dmc.conditions.SDTCondition;
import com.dmc.invincible_dmc.conditions.SDTConsumeCondition;
import com.dmc.invincible_dmc.conditions.SneakKeyCondition;
import com.dmc.invincible_dmc.conditions.SprintKeyCondition;
import com.dmc.invincible_dmc.conditions.SprintingCondition;
import com.dmc.invincible_dmc.conditions.StackCondition;
import com.dmc.invincible_dmc.conditions.TargetBlockingCondition;
import com.dmc.invincible_dmc.conditions.VehicleCondition;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.data.conditions.Condition;

public class DMCConditions {
   public static final DeferredRegister<Supplier<Condition<?>>> CONDITIONS = DeferredRegister.create(
      ResourceLocation.fromNamespaceAndPath("epicfight", "conditions"), "invincible_dmc"
   );
   public static final RegistryObject<Supplier<Condition<?>>> JUMP_CONDITION = CONDITIONS.register("jumping", () -> JumpCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> DASH_CONDITION = CONDITIONS.register("sprinting", () -> SprintingCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> STACK_CONDITION = CONDITIONS.register("stack_count", () -> StackCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> PHASE = CONDITIONS.register("phase", () -> PlayerPhaseCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> COOLDOWN = CONDITIONS.register("cooldown", () -> CooldownCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> MOB_EFFECT = CONDITIONS.register("mob_effect", () -> MobEffectCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> ENCHANTMENT = CONDITIONS.register("enchantment", () -> EnchantmentCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> BLOCKING = CONDITIONS.register("blocking", () -> BlockingCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> TARGET_BLOCKING = CONDITIONS.register("target_blocking", () -> TargetBlockingCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> HAS_VEHICLE = CONDITIONS.register("has_vehicle", () -> VehicleCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> DODGE_SUCCESS = CONDITIONS.register("dodge_success", () -> DodgeSuccessCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> PARRY_SUCCESS = CONDITIONS.register("parry_success", () -> ParrySuccessCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> DIRECTION = CONDITIONS.register("direction", () -> DirectionCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> UP = CONDITIONS.register(
      "up", () -> () -> new DirectionCondition(DirectionCondition.Direction.UP)
   );
   public static final RegistryObject<Supplier<Condition<?>>> DOWN = CONDITIONS.register(
      "down", () -> () -> new DirectionCondition(DirectionCondition.Direction.DOWN)
   );
   public static final RegistryObject<Supplier<Condition<?>>> LEFT = CONDITIONS.register(
      "left", () -> () -> new DirectionCondition(DirectionCondition.Direction.LEFT)
   );
   public static final RegistryObject<Supplier<Condition<?>>> RIGHT = CONDITIONS.register(
      "right", () -> () -> new DirectionCondition(DirectionCondition.Direction.RIGHT)
   );
   public static final RegistryObject<Supplier<Condition<?>>> JUMP_KEY = CONDITIONS.register("jump_key", () -> JumpKeyCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> SPRINT_KEY = CONDITIONS.register("sprint_key", () -> SprintKeyCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> SNEAK_KEY = CONDITIONS.register("sneak_key", () -> SneakKeyCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> PRESS_TIME_CONDITION = CONDITIONS.register(
      "press_time_condition", () -> PressedTimeCondition::new
   );
   public static final RegistryObject<Supplier<Condition<?>>> LONG_PRESS = CONDITIONS.register("long_press", () -> LongPressCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> PRESS_INTERVAL_CONDITION = CONDITIONS.register(
      "press_interval_condition", () -> PressIntervalCondition::new
   );
   public static final RegistryObject<Supplier<Condition<?>>> DIRECTIONAL_SEQUENCE = CONDITIONS.register(
      "directional_sequence", () -> DirectionalSequenceCondition::new
   );
   public static final RegistryObject<Supplier<Condition<?>>> IN_TARGET_POV = CONDITIONS.register("within_target_angle", () -> InTargetPovCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> IN_TARGET_POV_HORIZONTAL = CONDITIONS.register(
      "within_target_angle_horizontal", () -> InTargetPovCondition.InTargetPovHorizontal::new
   );
   public static final RegistryObject<Supplier<Condition<?>>> VIEW_AND_TARGET_VIEW = CONDITIONS.register(
      "view_and_target_view_within_angle", () -> PovTargetPovAngle::new
   );
   public static final RegistryObject<Supplier<Condition<?>>> AERIAL_ATTACK_LIMIT = CONDITIONS.register(
      "aerial_attack_limit", () -> AerialAttackLimitCondition::new
   );
   public static final RegistryObject<Supplier<Condition<?>>> LOCK_ON_KEY = CONDITIONS.register("lock_on_key", () -> LockonKeyCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> LOCKED_ON_TARGET = CONDITIONS.register("locked_on_target", () -> LockedOnTargetCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> CONCENTRATION_TIER = CONDITIONS.register(
      "concentration_tier", () -> ConcentrationTierCondition::new
   );
   public static final RegistryObject<Supplier<Condition<?>>> SDT = CONDITIONS.register("sdt", () -> SDTCondition::new);
   public static final RegistryObject<Supplier<Condition<?>>> SDT_CONSUME = CONDITIONS.register("sdt_consume", () -> SDTConsumeCondition::new);
}
