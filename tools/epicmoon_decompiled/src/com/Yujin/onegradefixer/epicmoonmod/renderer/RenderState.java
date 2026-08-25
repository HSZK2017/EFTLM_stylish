package com.Yujin.onegradefixer.epicmoonmod.renderer;

import org.lwjgl.opengl.GL30;

public final class RenderState {
   private final int fbo;
   private final int[] viewport = new int[4];
   private final boolean depthTest;
   private final int depthFunc;
   private final boolean blend;
   private final int blendSrcRgb;
   private final int blendDstRgb;
   private final int blendSrcAlpha;
   private final int blendDstAlpha;
   private final boolean stencilTest;
   private final int activeTexture;
   private final int currentProgram;

   private RenderState() {
      this.fbo = GL30.glGetInteger(36006);
      GL30.glGetIntegerv(2978, this.viewport);
      this.depthTest = GL30.glGetBoolean(2929);
      this.depthFunc = GL30.glGetInteger(2932);
      this.blend = GL30.glGetBoolean(3042);
      this.blendSrcRgb = GL30.glGetInteger(32969);
      this.blendDstRgb = GL30.glGetInteger(32968);
      this.blendSrcAlpha = GL30.glGetInteger(32971);
      this.blendDstAlpha = GL30.glGetInteger(32970);
      this.stencilTest = GL30.glGetBoolean(2960);
      this.activeTexture = GL30.glGetInteger(34016);
      this.currentProgram = GL30.glGetInteger(35725);
   }

   public static RenderState capture() {
      return new RenderState();
   }

   public void restore() {
      GL30.glBindFramebuffer(36160, this.fbo);
      GL30.glViewport(this.viewport[0], this.viewport[1], this.viewport[2], this.viewport[3]);
      if (this.depthTest) {
         GL30.glEnable(2929);
      } else {
         GL30.glDisable(2929);
      }

      GL30.glDepthFunc(this.depthFunc);
      if (this.blend) {
         GL30.glEnable(3042);
      } else {
         GL30.glDisable(3042);
      }

      GL30.glBlendFuncSeparate(this.blendSrcRgb, this.blendDstRgb, this.blendSrcAlpha, this.blendDstAlpha);
      if (this.stencilTest) {
         GL30.glEnable(2960);
      } else {
         GL30.glDisable(2960);
      }

      GL30.glActiveTexture(this.activeTexture);
      GL30.glUseProgram(this.currentProgram);
   }
}
