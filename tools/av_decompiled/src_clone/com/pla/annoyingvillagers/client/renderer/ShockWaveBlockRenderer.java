package com.pla.annoyingvillagers.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pla.annoyingvillagers.entity.ShockWaveBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ShockWaveBlockRenderer extends EntityRenderer<ShockWaveBlockEntity> {
   private final BlockRenderDispatcher blockRenderDispatcher;

   public ShockWaveBlockRenderer(Context context) {
      super(context);
      this.blockRenderDispatcher = context.m_234597_();
      this.f_114477_ = 0.0F;
   }

   public void render(
      ShockWaveBlockEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight
   ) {
      BlockState blockState = entity.getBlockState();
      if (blockState.m_60799_() == RenderShape.MODEL && blockState.m_60799_() != RenderShape.INVISIBLE) {
         Level level = entity.m_9236_();
         poseStack.m_85836_();
         poseStack.m_85837_(-0.5, 0.0, -0.5);
         BlockPos renderPos = BlockPos.m_274561_(entity.m_20185_(), entity.m_20191_().f_82292_, entity.m_20189_());
         BakedModel model = this.blockRenderDispatcher.m_110910_(blockState);
         BlockPos seedPos = entity.getSourceBlockPos();
         long seed = blockState.m_60726_(seedPos);
         RandomSource seededRandom = RandomSource.m_216335_(seed);

         for (RenderType renderType : model.getRenderTypes(blockState, seededRandom, ModelData.EMPTY)) {
            this.blockRenderDispatcher
               .m_110937_()
               .tesselateBlock(
                  level,
                  model,
                  blockState,
                  renderPos,
                  poseStack,
                  bufferSource.m_6299_(renderType),
                  false,
                  RandomSource.m_216327_(),
                  seed,
                  OverlayTexture.f_118083_,
                  ModelData.EMPTY,
                  renderType
               );
         }

         poseStack.m_85849_();
         super.m_7392_(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
      }
   }

   @NotNull
   public ResourceLocation getTextureLocation(@NotNull ShockWaveBlockEntity entity) {
      return TextureAtlas.f_118259_;
   }
}
