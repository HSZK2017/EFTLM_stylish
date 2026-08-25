package com.Yujin.onegradefixer.epicmoonmod.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EMsounds {
   public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "epicmoonmod");
   public static final RegistryObject<SoundEvent> SHOT5 = registerSoundEvents("shot5");
   public static final RegistryObject<SoundEvent> RELOAD3 = registerSoundEvents("reload3");
   public static final RegistryObject<SoundEvent> SHOT = registerSoundEvents("shot");
   public static final RegistryObject<SoundEvent> SHOT2 = registerSoundEvents("shot2");
   public static final RegistryObject<SoundEvent> SHOT3 = registerSoundEvents("shot3");
   public static final RegistryObject<SoundEvent> SHOT4 = registerSoundEvents("shot4");
   public static final RegistryObject<SoundEvent> RELOAD1 = registerSoundEvents("reload1");
   public static final RegistryObject<SoundEvent> RELOAD2 = registerSoundEvents("reload2");
   public static final RegistryObject<SoundEvent> TREMORBURST = registerSoundEvents("tremorburst");

   private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
      return SOUND_EVENTS.register(name, () -> SoundEvent.m_262824_(new ResourceLocation("epicmoonmod", name)));
   }

   public static void register(IEventBus iEventBus) {
      SOUND_EVENTS.register(iEventBus);
   }
}
