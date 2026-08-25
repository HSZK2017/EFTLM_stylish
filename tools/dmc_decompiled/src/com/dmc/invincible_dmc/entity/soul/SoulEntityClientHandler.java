package com.dmc.invincible_dmc.entity.soul;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector4f;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.EntityDecorations.DecorationOverlay;

@OnlyIn(Dist.CLIENT)
public final class SoulEntityClientHandler {
   private static final ResourceLocation SOUL_OVERLAY_KEY = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "soul_overlay");
   private static final ResourceLocation CRACK_L1 = ResourceLocation.fromNamespaceAndPath("epicfight", "textures/entity/overlay/crack_level1.png");
   private static final ResourceLocation CRACK_L2 = ResourceLocation.fromNamespaceAndPath("epicfight", "textures/entity/overlay/crack_level2.png");
   private static final Map<SoulEntity, Integer> OVERLAY_LEVELS = new HashMap<>();

   static void updateOverlay(final SoulEntity soul, int age) {
      float progress = (float)age / 400.0F;
      int newLevel = progress < 0.15F ? 0 : (progress < 0.5F ? 1 : 2);
      int prevLevel = OVERLAY_LEVELS.getOrDefault(soul, -1);
      if (newLevel != prevLevel) {
         OVERLAY_LEVELS.put(soul, newLevel);
         LivingEntityPatch patch = (LivingEntityPatch)EpicFightCapabilities.getEntityPatch(soul, LivingEntityPatch.class);
         if (patch != null) {
            patch.getEntityDecorations().removeDecorationOverlay(SOUL_OVERLAY_KEY);
            if (newLevel > 0) {
               final ResourceLocation tex = newLevel == 1 ? CRACK_L1 : CRACK_L2;
               patch.getEntityDecorations().addDecorationOverlay(SOUL_OVERLAY_KEY, new DecorationOverlay() {
                  public RenderType getRenderType() {
                     return RenderType.m_110473_(tex);
                  }

                  public Vector4f color(float partialTick) {
                     float alpha = soul.getRenderAlpha();
                     return new Vector4f(1.0F, 1.0F, 1.0F, alpha);
                  }
               });
            }
         }
      }
   }

   static void clearOverlay(SoulEntity soul) {
      OVERLAY_LEVELS.remove(soul);
      LivingEntityPatch patch = (LivingEntityPatch)EpicFightCapabilities.getEntityPatch(soul, LivingEntityPatch.class);
      if (patch != null) {
         patch.getEntityDecorations().removeDecorationOverlay(SOUL_OVERLAY_KEY);
      }
   }

   static int getCurrentLevel(SoulEntity soul, int age, int lifetimeTicks) {
      float progress = (float)age / (float)lifetimeTicks;
      return progress < 0.15F ? 0 : (progress < 0.5F ? 1 : 2);
   }
}
