package com.pla.annoyingvillagers.client.overlaylayer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import se.gory_moon.player_mobs.entity.PlayerMobEntity;

public class PlayerMobBloodOverlayLayer extends RenderLayer<PlayerMobEntity, PlayerModel<PlayerMobEntity>> {
   private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/player_mob_blood.png");

   public PlayerMobBloodOverlayLayer(RenderLayerParent<PlayerMobEntity, PlayerModel<PlayerMobEntity>> parent) {
      super(parent);
   }

   public void render(
      PoseStack pose,
      MultiBufferSource buf,
      int light,
      PlayerMobEntity e,
      float limbSwing,
      float limbSwingAmount,
      float partialTick,
      float age,
      float headYaw,
      float headPitch
   ) {
      PlayerModel<PlayerMobEntity> model = (PlayerModel<PlayerMobEntity>)this.m_117386_();
      VertexConsumer vc = buf.m_6299_(RenderType.m_110458_(TEX));
      model.m_7695_(pose, vc, light, OverlayTexture.f_118083_, 1.0F, 1.0F, 1.0F, 1.0F);
   }
}
