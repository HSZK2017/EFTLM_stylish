package com.pla.annoyingvillagers.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pla.annoyingvillagers.accessors.ModelPartAccess;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ModelPart.class})
public class ModelPartMixin implements ModelPartAccess {
   @Unique
   public float dm_xScale = 1.0F;
   @Unique
   public float dm_yScale = 1.0F;
   @Unique
   public float dm_zScale = 1.0F;

   @Inject(
      method = {"translateAndRotate(Lcom/mojang/blaze3d/vertex/PoseStack;)V"},
      at = {@At("TAIL")}
   )
   public void dragonmounts_scalePoseStack(PoseStack pPoseStack, CallbackInfo cbi) {
      pPoseStack.m_85841_(this.dm_xScale, this.dm_yScale, this.dm_zScale);
   }

   @Override
   public float getXScale() {
      return this.dm_xScale;
   }

   @Override
   public float getYScale() {
      return this.dm_yScale;
   }

   @Override
   public float getZScale() {
      return this.dm_zScale;
   }

   @Override
   public void setXScale(float x) {
      this.dm_xScale = x;
   }

   @Override
   public void setYScale(float y) {
      this.dm_yScale = y;
   }

   @Override
   public void setZScale(float z) {
      this.dm_zScale = z;
   }
}
