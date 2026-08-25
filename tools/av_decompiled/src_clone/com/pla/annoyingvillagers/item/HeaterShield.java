package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.event.ShieldRendererEvent;
import java.util.function.Consumer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class HeaterShield extends ShieldItem {
   public HeaterShield() {
      super(new Properties().m_41487_(1).m_41503_(1561));
   }

   public void initializeClient(Consumer<IClientItemExtensions> consumer) {
      consumer.accept(new IClientItemExtensions() {
         public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return ShieldRendererEvent.instance;
         }
      });
   }
}
