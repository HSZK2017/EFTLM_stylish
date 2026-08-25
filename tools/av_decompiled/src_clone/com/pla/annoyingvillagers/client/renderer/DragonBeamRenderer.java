package com.pla.annoyingvillagers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pla.annoyingvillagers.client.engine.ThunderRender;
import com.pla.annoyingvillagers.entity.DragonBeamEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DragonBeamRenderer extends EntityRenderer<DragonBeamEntity> {
   private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/dragon_beam.png");
   private final ThunderRender thunderRender = new ThunderRender();

   public DragonBeamRenderer(Context pContext) {
      super(pContext);
   }

   @NotNull
   public Vec3 getRenderOffset(DragonBeamEntity dragonBeam, float p_114484_) {
      return new Vec3(
         dragonBeam.m_9236_().f_46441_.m_188583_() * 0.03, dragonBeam.m_9236_().f_46441_.m_188583_() * 0.03, dragonBeam.m_9236_().f_46441_.m_188583_() * 0.03
      );
   }

   public void render(
      @NotNull DragonBeamEntity dragonBeamEntity,
      float entityYaw,
      float partialTicks,
      @NotNull PoseStack poseStack,
      @NotNull MultiBufferSource buffer,
      int packedLight
   ) {
      super.m_7392_(dragonBeamEntity, entityYaw, partialTicks, poseStack, buffer, packedLight);
      if (dragonBeamEntity.isSetUseNoVfxThunder()) {
         poseStack.m_85836_();
         Vec3 from = dragonBeamEntity.getThunderStartVec3();
         Vec3 to = dragonBeamEntity.getThunderStopVec3();
         ThunderRender.ThunderData bolt = new ThunderRender.ThunderData(ThunderRender.ThunderData.ThunderRenderInfo.DRAGON_THUNDER, from, to, 15)
            .size(0.2F)
            .lifespan(4)
            .spawn(ThunderRender.ThunderData.SpawnFunction.delay(1.0F));
         this.thunderRender.update(null, bolt, partialTicks);
         poseStack.m_85837_(-dragonBeamEntity.m_20185_(), -dragonBeamEntity.m_20186_(), -dragonBeamEntity.m_20189_());
         this.thunderRender.render(partialTicks, poseStack, buffer);
         poseStack.m_85849_();
      }
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull DragonBeamEntity dragonBeam) {
      return TEXTURE;
   }
}
