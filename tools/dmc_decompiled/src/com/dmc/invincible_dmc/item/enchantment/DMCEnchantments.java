package com.dmc.invincible_dmc.item.enchantment;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class DMCEnchantments {
   public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, "invincible_dmc");
   public static final RegistryObject<Enchantment> SUPER_YAMATO = ENCHANTMENTS.register("super_yamato", SuperYamatoEnchantment::new);

   private DMCEnchantments() {
   }
}
