package org.merlin204.mimic.client.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MimicSounds {
   public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "mimic");
   public static RegistryObject<SoundEvent> PHASE_1 = registerSoundEvent("phase_1");
   public static RegistryObject<SoundEvent> PHASE_2 = registerSoundEvent("phase_2");
   public static RegistryObject<SoundEvent> PHASE_3 = registerSoundEvent("phase_3");

   private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
      return SOUND_EVENTS.register(name, () -> SoundEvent.m_262824_(ResourceLocation.fromNamespaceAndPath("mimic", name)));
   }
}
