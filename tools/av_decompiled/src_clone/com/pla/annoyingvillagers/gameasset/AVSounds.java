package com.pla.annoyingvillagers.gameasset;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AVSounds {
   public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "epicfight");
   public static final RegistryObject<SoundEvent> SWORD_WHOOSH = registerSound("entity.weapon.sword_whoosh");
   public static final RegistryObject<SoundEvent> KICK = registerSound("entity.weapon.kick");

   private static RegistryObject<SoundEvent> registerSound(String name) {
      ResourceLocation res = ResourceLocation.fromNamespaceAndPath("annoyingvillagers", name);
      return SOUNDS.register(name, () -> SoundEvent.m_262824_(res));
   }
}
