package com.dmc.invincible_dmc.compat.oculus;

import com.dmc.invincible_dmc.utils.DMCLog;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.layer.BlockEntityRenderStateShard;
import net.irisshaders.iris.layer.OuterWrappedRenderType;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.irisshaders.iris.uniforms.CapturedRenderingState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;

final class OculusRuntimeCompat implements OculusCompat.Backend {
   private static final String BLOCK_ENTITY_RENDER_TYPE_NAME = "iris:is_block_entity";
   private volatile boolean shaderActive;

   static OculusCompat.Backend create() {
      OculusRuntimeCompat compat = new OculusRuntimeCompat();
      compat.shaderActive = IrisApi.getInstance().isShaderPackInUse();
      DMCLog.info(DMCLog.Category.COMPAT, "[OculusCompat] Initial shaderActive = {}", compat.shaderActive);
      MinecraftForge.EVENT_BUS.addListener(event -> {
         if (event.phase == Phase.END) {
            compat.updateShaderState();
         }
      });
      return compat;
   }

   private void updateShaderState() {
      boolean active = IrisApi.getInstance().isShaderPackInUse();
      if (active != this.shaderActive) {
         this.shaderActive = active;
         DMCLog.info(DMCLog.Category.COMPAT, "[OculusCompat] Shader toggled -> {}", active);
      }
   }

   @Override
   public boolean isShaderActive() {
      return this.shaderActive;
   }

   @Override
   public RenderType wrapEndPortalRenderType(RenderType renderType) {
      return OuterWrappedRenderType.wrapExactlyOnce("iris:is_block_entity", renderType, BlockEntityRenderStateShard.INSTANCE);
   }

   @Override
   public int beginEndPortalBlockEntityContext() {
      CapturedRenderingState renderingState = CapturedRenderingState.INSTANCE;
      int previousBlockEntityId = renderingState.getCurrentRenderedBlockEntity();
      Object2IntMap<BlockState> blockStateIds = WorldRenderingSettings.INSTANCE.getBlockStateIds();
      int endPortalId = blockStateIds == null ? -1 : blockStateIds.getOrDefault(Blocks.f_50257_.m_49966_(), -1);
      renderingState.setCurrentBlockEntity(endPortalId);
      return previousBlockEntityId;
   }

   @Override
   public void restoreBlockEntityContext(int blockEntityId) {
      CapturedRenderingState.INSTANCE.setCurrentBlockEntity(blockEntityId);
   }
}
