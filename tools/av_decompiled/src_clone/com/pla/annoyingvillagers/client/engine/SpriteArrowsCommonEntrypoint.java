package com.pla.annoyingvillagers.client.engine;

import com.pla.annoyingvillagers.client.renderer.SpriteArrowRenderer;
import java.util.Map;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class SpriteArrowsCommonEntrypoint {
   public static void replace() {
      Map<EntityType<?>, EntityRendererProvider<?>> providers = EntityRenderers.f_174031_;
      replaceForArrow(providers, EntityType.f_20548_);
      replaceForArrow(providers, EntityType.f_20478_);
   }

   private static <T extends AbstractArrow> void replaceForArrow(Map<EntityType<?>, EntityRendererProvider<?>> providers, EntityType<T> type) {
      if (providers.containsKey(type)) {
         providers.put(type, SpriteArrowRenderer::new);
      }
   }
}
