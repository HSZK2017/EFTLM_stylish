package com.pla.annoyingvillagers.init;

import com.pla.annoyingvillagers.potion.CaptiveMobEffect;
import com.pla.annoyingvillagers.potion.ElectrifyMobEffect;
import com.pla.annoyingvillagers.potion.EnchantBedEffectMobEffect;
import com.pla.annoyingvillagers.potion.HerobrineMobEffect;
import com.pla.annoyingvillagers.potion.ObedienceMobEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AnnoyingVillagersModMobEffects {
   public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "annoyingvillagers");
   public static final RegistryObject<MobEffect> ENCHANT_BED_EFFECT = REGISTRY.register("enchant_bed_effect", EnchantBedEffectMobEffect::new);
   public static final RegistryObject<MobEffect> ELECTRIFY = REGISTRY.register("electrify", ElectrifyMobEffect::new);
   public static final RegistryObject<MobEffect> CAPTIVE = REGISTRY.register("captive", CaptiveMobEffect::new);
   public static final RegistryObject<MobEffect> HEROBRINE = REGISTRY.register("herobrine", HerobrineMobEffect::new);
   public static final RegistryObject<MobEffect> OBEDIENCE = REGISTRY.register("obedience", ObedienceMobEffect::new);
}
