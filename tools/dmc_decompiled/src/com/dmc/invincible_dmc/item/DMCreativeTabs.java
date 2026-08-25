package com.dmc.invincible_dmc.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.world.item.EpicFightCreativeTabs;

public class DMCreativeTabs {
   public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.f_279569_, "invincible_dmc");
   public static final RegistryObject<CreativeModeTab> ITEMS = TABS.register(
      "items",
      () -> CreativeModeTab.builder()
            .m_257941_(Component.m_237115_("itemGroup.invincible_dmc.items"))
            .m_257737_(() -> new ItemStack((ItemLike)DMCItems.YAMATO_DMC5.get()))
            .withTabsBefore(new ResourceKey[]{EpicFightCreativeTabs.ITEMS.getKey()})
            .m_257501_((params, output) -> {
               output.m_246326_((ItemLike)DMCItems.YAMATO_DMC5.get());
               output.m_246326_((ItemLike)DMCItems.YAMATO_DMC5_MINI.get());
               output.m_246326_((ItemLike)DMCItems.YAMATO_DMC5_BD.get());
               output.m_246326_((ItemLike)DMCItems.YAMATO_DMC4.get());
               output.m_246326_((ItemLike)DMCItems.DEVIL_SWORD_VERGIL.get());
               output.m_246326_((ItemLike)DMCItems.DUMMY_SPAWN_EGG.get());
               output.m_246326_((ItemLike)DMCItems.SDT_ARMOR.get());
               output.m_246326_((ItemLike)DMCItems.POWER_CHAIR.get());
            })
            .m_257652_()
   );
}
