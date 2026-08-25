package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import java.util.Set;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.ApiStatus.Internal;

@EventBusSubscriber(
   value = {Dist.CLIENT},
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class ModModelPredicateProvider {
   public static final Material LOCATION_JESSICA_THE_DARK_SHIELD = material("item/jessica_the_dark_shield");
   public static final Material LOCATION_HEATER_SHIELD = material("item/heater_shield");
   public static final Material LOCATION_GEM_SHIELD = material("item/gem_shield");
   public static final Material LOCATION_NETHERITE_SHIELD = material("item/netherite_shield");

   @SubscribeEvent
   public static void init(FMLClientSetupEvent event) {
      event.enqueueWork(
         () -> {
            addShieldPropertyOverrides(
               ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "blocking"),
               (stack, world, entity, seed) -> entity != null && entity.m_6117_() && entity.m_21211_() == stack ? 1.0F : 0.0F,
               (ItemLike)AnnoyingVillagersModItems.JESSICA_THE_DARK_SHIELD.get()
            );
            addShieldPropertyOverrides(
               ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "blocking"),
               (stack, world, entity, seed) -> entity != null && entity.m_6117_() && entity.m_21211_() == stack ? 1.0F : 0.0F,
               (ItemLike)AnnoyingVillagersModItems.HEATER_SHIELD.get()
            );
            addShieldPropertyOverrides(
               ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "blocking"),
               (stack, world, entity, seed) -> entity != null && entity.m_6117_() && entity.m_21211_() == stack ? 1.0F : 0.0F,
               (ItemLike)AnnoyingVillagersModItems.GEM_SHIELD.get()
            );
            addShieldPropertyOverrides(
               ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "blocking"),
               (stack, world, entity, seed) -> entity != null && entity.m_6117_() && entity.m_21211_() == stack ? 1.0F : 0.0F,
               (ItemLike)AnnoyingVillagersModItems.NETHERITE_SHIELD.get()
            );
         }
      );
   }

   private static void addShieldPropertyOverrides(ResourceLocation override, ClampedItemPropertyFunction propertyGetter, ItemLike... shields) {
      for (ItemLike shield : shields) {
         ItemProperties.register(shield.m_5456_(), override, propertyGetter);
      }
   }

   @SubscribeEvent
   public static void onStitch(ModModelPredicateProvider.Pre event) {
      if (event.getAtlas().m_118330_().equals(TextureAtlas.f_118259_)) {
         for (Material textures : new Material[]{LOCATION_JESSICA_THE_DARK_SHIELD, LOCATION_HEATER_SHIELD, LOCATION_GEM_SHIELD, LOCATION_NETHERITE_SHIELD}) {
            event.addSprite(textures.m_119203_());
         }
      }
   }

   private static Material material(String path) {
      return new Material(TextureAtlas.f_118259_, ResourceLocation.fromNamespaceAndPath("annoyingvillagers", path));
   }

   public static class Pre extends TextureStitchEvent {
      private final Set<ResourceLocation> sprites;

      @Internal
      public Pre(TextureAtlas map, Set<ResourceLocation> sprites) {
         super(map);
         this.sprites = sprites;
      }

      public boolean addSprite(ResourceLocation sprite) {
         return this.sprites.add(sprite);
      }
   }
}
