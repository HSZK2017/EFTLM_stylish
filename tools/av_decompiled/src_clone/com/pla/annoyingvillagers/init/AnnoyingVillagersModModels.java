package com.pla.annoyingvillagers.init;

import com.pla.annoyingvillagers.client.model.ModelBlueDemonTrident;
import com.pla.annoyingvillagers.client.model.ModelDragonMeteorite;
import com.pla.annoyingvillagers.client.model.ModelFlyingShockwave;
import com.pla.annoyingvillagers.client.model.ModelGreenVillagerKnightArmor;
import com.pla.annoyingvillagers.client.model.ModelHerobrineDragon;
import com.pla.annoyingvillagers.client.model.ModelHerobrineObsidianDiamondChestplate;
import com.pla.annoyingvillagers.client.model.ModelHerobrineObsidianDiamondHelmet;
import com.pla.annoyingvillagers.client.model.ModelHerobrineWarden;
import com.pla.annoyingvillagers.client.model.ModelSnakeBlade;
import com.pla.annoyingvillagers.client.model.ModelSnakeBladeFragment;
import com.pla.annoyingvillagers.client.model.ModelVillagerKnightArmor;
import com.pla.annoyingvillagers.client.model.ModelVillagerScoutHelmet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterLayerDefinitions;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class AnnoyingVillagersModModels {
   @SubscribeEvent
   public static void registerLayerDefinitions(RegisterLayerDefinitions registerlayerdefinitions) {
      registerlayerdefinitions.registerLayerDefinition(ModelVillagerScoutHelmet.LAYER_LOCATION, ModelVillagerScoutHelmet::createBodyLayer);
      registerlayerdefinitions.registerLayerDefinition(ModelBlueDemonTrident.LAYER_LOCATION, ModelBlueDemonTrident::createBodyLayer);
      registerlayerdefinitions.registerLayerDefinition(ModelGreenVillagerKnightArmor.LAYER_LOCATION, ModelGreenVillagerKnightArmor::createBodyLayer);
      registerlayerdefinitions.registerLayerDefinition(ModelVillagerKnightArmor.LAYER_LOCATION, ModelVillagerKnightArmor::createBodyLayer);
      registerlayerdefinitions.registerLayerDefinition(ModelHerobrineObsidianDiamondHelmet.LAYER_LOCATION, ModelHerobrineObsidianDiamondHelmet::createBodyLayer);
      registerlayerdefinitions.registerLayerDefinition(
         ModelHerobrineObsidianDiamondChestplate.LAYER_LOCATION, ModelHerobrineObsidianDiamondChestplate::createBodyLayer
      );
      registerlayerdefinitions.registerLayerDefinition(ModelSnakeBladeFragment.LAYER_LOCATION, ModelSnakeBladeFragment::createBodyLayer);
      registerlayerdefinitions.registerLayerDefinition(ModelSnakeBlade.LAYER_LOCATION, ModelSnakeBlade::createBodyLayer);
      registerlayerdefinitions.registerLayerDefinition(ModelHerobrineWarden.LAYER_LOCATION, ModelHerobrineWarden::m_233537_);
      registerlayerdefinitions.registerLayerDefinition(ModelHerobrineDragon.LAYER_LOCATION, ModelHerobrineDragon::createBodyLayer);
      registerlayerdefinitions.registerLayerDefinition(ModelDragonMeteorite.LAYER_LOCATION, ModelDragonMeteorite::createBodyLayer);
      registerlayerdefinitions.registerLayerDefinition(ModelFlyingShockwave.LAYER_LOCATION, ModelFlyingShockwave::createBodyLayer);
   }
}
