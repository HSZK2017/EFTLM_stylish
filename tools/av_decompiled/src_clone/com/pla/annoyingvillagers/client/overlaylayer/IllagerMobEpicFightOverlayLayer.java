package com.pla.annoyingvillagers.client.overlaylayer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModMobEffects;
import com.pla.annoyingvillagers.potion.ObedienceMobEffect;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractIllager;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.layer.ModelRenderLayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class IllagerMobEpicFightOverlayLayer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>, R extends RenderLayer<E, M>>
   extends ModelRenderLayer<E, T, M, R, HumanoidMesh> {
   private static final ResourceLocation ILLAGER_EYES = ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/obedience/illager.png");

   public IllagerMobEpicFightOverlayLayer(AssetAccessor<HumanoidMesh> mesh) {
      super(mesh);
   }

   @Nullable
   private ResourceLocation pickTexture(E entity) {
      return entity instanceof AbstractIllager
            && ObedienceMobEffect.canBeObedientMob(entity)
            && entity.m_21023_((MobEffect)AnnoyingVillagersModMobEffects.OBEDIENCE.get())
         ? ILLAGER_EYES
         : null;
   }

   protected void renderLayer(
      T entityPatch,
      E entity,
      @Nullable R vanillaLayer,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int packedLight,
      OpenMatrix4f[] poses,
      float bob,
      float yRot,
      float xRot,
      float partialTicks
   ) {
      ResourceLocation texture = this.pickTexture(entity);
      if (texture != null) {
         ((HumanoidMesh)this.mesh.get())
            .draw(
               poseStack,
               buffer,
               RenderType.m_110488_(texture),
               packedLight,
               1.0F,
               1.0F,
               1.0F,
               1.0F,
               OverlayTexture.f_118083_,
               entityPatch.getArmature(),
               poses
            );
      }
   }
}
