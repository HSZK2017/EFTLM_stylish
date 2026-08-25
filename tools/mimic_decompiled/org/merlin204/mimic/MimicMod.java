package org.merlin204.mimic;

import net.minecraft.core.BlockPos;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.merlin204.mimic.client.sound.MimicSounds;
import org.merlin204.mimic.entity.MimicEntities;
import org.merlin204.mimic.item.MimicItems;

@Mod("mimic")
public class MimicMod {
   public static final String MOD_ID = "mimic";
   public static final BlockPos PLAYER_SPAWN_POS = new BlockPos(250, 2, 250);

   public MimicMod(FMLJavaModLoadingContext context) {
      IEventBus bus = context.getModEventBus();
      MimicEntities.ENTITIES.register(bus);
      MimicItems.ITEMS.register(bus);
      MimicItems.MIMIC_TAB.register(bus);
      MimicSounds.SOUND_EVENTS.register(bus);
   }
}
