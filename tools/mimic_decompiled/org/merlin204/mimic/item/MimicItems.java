package org.merlin204.mimic.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MimicItems {
   public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "mimic");
   public static final RegistryObject<Item> MERLIN_GG = ITEMS.register("merlin_gg", () -> new MimicMerlinSuperGG(new Properties().m_41487_(1)));
   public static final RegistryObject<Item> MIMIC_INVITATION = ITEMS.register("mimic_invitation", () -> new MimicInvitationItem(new Properties().m_41487_(1)));
   public static final RegistryObject<Item> PROTEUS_COMMENDATION = ITEMS.register(
      "proteus_commendation", () -> new ProteusCommendationItem(new Properties().m_41487_(1))
   );
   public static final DeferredRegister<CreativeModeTab> MIMIC_TAB = DeferredRegister.create(Registries.f_279569_, "mimic");
   public static final RegistryObject<CreativeModeTab> DEFAULT_TAB = MIMIC_TAB.register(
      "mimic_items",
      () -> CreativeModeTab.builder()
            .m_257737_(() -> new ItemStack((ItemLike)MIMIC_INVITATION.get()))
            .m_257941_(Component.m_237115_("itemGroup.mimic.items"))
            .m_257501_((parameters, tabData) -> {
               tabData.m_246326_((ItemLike)MIMIC_INVITATION.get());
               tabData.m_246326_((ItemLike)PROTEUS_COMMENDATION.get());
            })
            .m_257652_()
   );
}
