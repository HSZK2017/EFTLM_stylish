package org.merlin204.mimic.event;

import com.merlin204.avalon.entity.client.renderer.EmptyRenderer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.merlin204.mimic.entity.MimicEntities;
import org.merlin204.mimic.entity.client.PatchedMimicRenderer;
import org.merlin204.mimic.entity.proteus.ProteusEntity;
import org.merlin204.mimic.entity.proteus.ProteusPatch;
import org.merlin204.mimic.entity.shadow.PlayerShadowMimicEntity;
import org.merlin204.mimic.entity.shadow.PlayerShadowMimicPatch;
import org.merlin204.mimic.entity.shadow.ShadowMimicEntity;
import org.merlin204.mimic.entity.shadow.ShadowMimicPatch;
import org.merlin204.mimic.network.PacketHandler;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent.Add;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;

@EventBusSubscriber(
   modid = "mimic",
   bus = Bus.MOD
)
public class MimicEntityEventHandler {
   @SubscribeEvent
   public static void handleEntityPatchRegistry(EntityPatchRegistryEvent event) {
      event.getTypeEntry().put((EntityType)MimicEntities.PROTEUS.get(), (Function<Entity, Supplier<ProteusPatch<T>>>)entity -> ProteusPatch::new);
      event.getTypeEntry().put((EntityType)MimicEntities.SHADOW_MIMIC.get(), (Function<Entity, Supplier<ShadowMimicPatch<T>>>)entity -> ShadowMimicPatch::new);
      event.getTypeEntry().put((EntityType)MimicEntities.PLAYER_SHADOW_MIMIC.get(), entity -> PlayerShadowMimicPatch::new);
   }

   @SubscribeEvent
   public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
      event.put((EntityType)MimicEntities.PROTEUS.get(), ProteusEntity.getDefaultAttribute());
      event.put((EntityType)MimicEntities.SHADOW_MIMIC.get(), ShadowMimicEntity.getDefaultAttribute());
      event.put((EntityType)MimicEntities.PLAYER_SHADOW_MIMIC.get(), PlayerShadowMimicEntity.getDefaultAttribute());
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void handleClientSetup(FMLClientSetupEvent event) {
      EntityRenderers.m_174036_((EntityType)MimicEntities.PROTEUS.get(), EmptyRenderer::new);
      EntityRenderers.m_174036_((EntityType)MimicEntities.SHADOW_MIMIC.get(), EmptyRenderer::new);
      EntityRenderers.m_174036_((EntityType)MimicEntities.PLAYER_SHADOW_MIMIC.get(), EmptyRenderer::new);
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void handlePatchedRenderers(Add event) {
      event.addPatchedEntityRenderer(
         (EntityType)MimicEntities.PROTEUS.get(),
         entityType -> new PatchedMimicRenderer(event.getContext(), entityType).initLayerLast(event.getContext(), entityType)
      );
      event.addPatchedEntityRenderer(
         (EntityType)MimicEntities.SHADOW_MIMIC.get(),
         entityType -> new PatchedMimicRenderer(event.getContext(), entityType).initLayerLast(event.getContext(), entityType)
      );
      event.addPatchedEntityRenderer(
         (EntityType)MimicEntities.PLAYER_SHADOW_MIMIC.get(),
         entityType -> new PatchedMimicRenderer(event.getContext(), entityType).initLayerLast(event.getContext(), entityType)
      );
   }

   @SubscribeEvent
   public static void commonSetup(FMLCommonSetupEvent event) {
      PacketHandler.register();
   }
}
