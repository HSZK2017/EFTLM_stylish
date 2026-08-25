package com.dmc.invincible_dmc.mixin.epicfight.client;

import com.google.common.collect.BiMap;
import java.util.function.Function;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;

@Mixin({RenderEngine.class})
public interface RenderEngineAccessor {
   @Accessor(
      value = "entityRendererProvider",
      remap = false
   )
   BiMap<EntityType<?>, Function<EntityType<?>, PatchedEntityRenderer>> getEntityRendererProvider();
}
