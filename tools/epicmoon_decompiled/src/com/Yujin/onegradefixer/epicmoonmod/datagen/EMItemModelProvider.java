package com.Yujin.onegradefixer.epicmoonmod.datagen;

import com.Yujin.onegradefixer.epicmoonmod.item.EpicmoonItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class EMItemModelProvider extends ItemModelProvider {
   public EMItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
      super(output, modid, existingFileHelper);
   }

   protected void registerModels() {
      this.simpleItem(EpicmoonItems.LEIHENG_HAT);
      this.simpleItem(EpicmoonItems.LEIHENG_SUIT);
      this.simpleItem(EpicmoonItems.LEIHENG_PANTS);
      this.simpleItem(EpicmoonItems.LEIHENG_SHOES);
   }

   private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
      return (ItemModelBuilder)((ItemModelBuilder)this.withExistingParent(item.getId().m_135815_(), new ResourceLocation("item/generated")))
         .texture("layer0", new ResourceLocation("epicmoonmod", "item/" + item.getId().m_135815_()));
   }
}
