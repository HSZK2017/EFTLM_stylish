package com.Yujin.onegradefixer.epicmoonmod.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class EpicmoonTabs {
   public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.f_279569_, "epicmoonmod");
   public static final RegistryObject<CreativeModeTab> EPICMOON_TAB = TABS.register(
      "epicmoon_tab",
      () -> CreativeModeTab.builder()
            .m_257941_(Component.m_237115_("creativetabs.epicmoon_tab"))
            .m_257737_(((Item)EpicmoonItems.TENTAI_SEITOU.get())::m_7968_)
            .m_257501_((pParameters, pOutput) -> {
               pOutput.m_246326_((ItemLike)EpicmoonItems.TENTAI_SEITOU.get());
               pOutput.m_246326_((ItemLike)EpicmoonItems.VALENCINA_DUAL_SWORDS.get());
               pOutput.m_246326_((ItemLike)EpicmoonItems.TIGERMARK_ROUND.get());
               pOutput.m_246326_((ItemLike)EpicmoonItems.SAVAGE_TIGERMARK_ROUND.get());
               pOutput.m_246326_((ItemLike)EpicmoonItems.Accel_ROUND.get());
               pOutput.m_246326_((ItemLike)EpicmoonItems.THUMB_GUNPOWDER.get());
               pOutput.m_246326_((ItemLike)EpicmoonItems.CARTRIDGE_CASE.get());
               pOutput.m_246326_((ItemLike)EpicmoonItems.LEIHENG_HAT.get());
               pOutput.m_246326_((ItemLike)EpicmoonItems.LEIHENG_SUIT.get());
               pOutput.m_246326_((ItemLike)EpicmoonItems.LEIHENG_PANTS.get());
               pOutput.m_246326_((ItemLike)EpicmoonItems.LEIHENG_SHOES.get());
            })
            .m_257652_()
   );

   public static void register(IEventBus eventBus) {
      TABS.register(eventBus);
   }
}
