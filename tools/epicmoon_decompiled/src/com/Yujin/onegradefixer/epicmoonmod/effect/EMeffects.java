package com.Yujin.onegradefixer.epicmoonmod.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EMeffects {
   public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "epicmoonmod");
   public static final RegistryObject<MobEffect> UNRELENTING_SPIRIT = MOB_EFFECTS.register(
      "unrelenting_spirit",
      () -> new UnrelentingSpirit(MobEffectCategory.BENEFICIAL, 0)
            .m_19472_(Attributes.f_22281_, "5f36ae1c-0856-4901-b712-9261ba03567d", 0.2F, Operation.MULTIPLY_TOTAL)
            .m_19472_(Attributes.f_22279_, "5f36ae1c-0856-4901-b712-9261ba03567d", 0.2F, Operation.MULTIPLY_TOTAL)
   );
   public static final RegistryObject<MobEffect> UNRELENTING_SPIRIT_SIN = MOB_EFFECTS.register(
      "unrelenting_spirit_sin",
      () -> new UnrelentingSpiritSin(MobEffectCategory.BENEFICIAL, 0)
            .m_19472_(Attributes.f_22281_, "5f36ae1c-0856-4901-b712-9261ba03567d", 0.2F, Operation.MULTIPLY_TOTAL)
            .m_19472_(Attributes.f_22279_, "5f36ae1c-0856-4901-b712-9261ba03567d", 0.2F, Operation.MULTIPLY_TOTAL)
            .m_19472_(Attributes.f_22284_, "5f36ae1c-0856-4901-b712-9261ba03567d", 10.0, Operation.ADDITION)
            .m_19472_(Attributes.f_22285_, "5f36ae1c-0856-4901-b712-9261ba03567d", 5.0, Operation.ADDITION)
   );
   public static final RegistryObject<MobEffect> DUAL_SIN = MOB_EFFECTS.register(
      "dual_sin",
      () -> new DualSin(MobEffectCategory.BENEFICIAL, 0)
            .m_19472_(Attributes.f_22281_, "5f36ae1c-0856-4901-b712-9261ba03567d", 0.1F, Operation.MULTIPLY_TOTAL)
            .m_19472_(Attributes.f_22279_, "5f36ae1c-0856-4901-b712-9261ba03567d", 0.1F, Operation.MULTIPLY_TOTAL)
            .m_19472_(Attributes.f_22284_, "5f36ae1c-0856-4901-b712-9261ba03567d", 10.0, Operation.ADDITION)
            .m_19472_(Attributes.f_22285_, "5f36ae1c-0856-4901-b712-9261ba03567d", 5.0, Operation.ADDITION)
   );
   public static final RegistryObject<MobEffect> TREMOR = MOB_EFFECTS.register("tremor", () -> new Tremor(MobEffectCategory.HARMFUL, 0));
   public static final RegistryObject<MobEffect> TREMOR_SCORCH = MOB_EFFECTS.register("tremor_scorch", () -> new TremorScorch(MobEffectCategory.HARMFUL, 0));
   public static final RegistryObject<MobEffect> ACCELERATING_FUTURE = MOB_EFFECTS.register(
      "accelerating_future",
      () -> new AcceleratingFuture(MobEffectCategory.BENEFICIAL, 0)
            .m_19472_(Attributes.f_22281_, "2cd17b34-16b1-74fb-00a0-20df66c5de92", 0.1F, Operation.MULTIPLY_TOTAL)
            .m_19472_(Attributes.f_22279_, "2cd17b34-16b1-74fb-00a0-20df66c5de92", 0.1F, Operation.MULTIPLY_TOTAL)
   );
   public static final RegistryObject<MobEffect> POISE = MOB_EFFECTS.register("poise", () -> new NormalEffect(MobEffectCategory.BENEFICIAL, 0));

   public static void register(IEventBus iEventBus) {
      MOB_EFFECTS.register(iEventBus);
   }
}
