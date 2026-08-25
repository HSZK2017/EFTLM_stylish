package com.dmc.invincible_dmc.command.arguments;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DMCommandArgumentTypes {
   public static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create(
      ForgeRegistries.COMMAND_ARGUMENT_TYPES, "invincible_dmc"
   );
   public static final RegistryObject<ArgumentTypeInfo<AllSkillArgument, ?>> SKILL = COMMAND_ARGUMENT_TYPES.register(
      "skill", () -> SingletonArgumentInfo.m_235451_(AllSkillArgument::skill)
   );

   public static void registerArgumentTypes() {
      ArgumentTypeInfos.registerByClass(AllSkillArgument.class, (ArgumentTypeInfo)SKILL.get());
   }
}
