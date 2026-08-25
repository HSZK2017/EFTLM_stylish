package com.dmc.invincible_dmc.client.dimension;

import com.dmc.invincible_dmc.InvincibleMod_DMC;
import com.dmc.invincible_dmc.compat.oculus.OculusCompat;
import com.dmc.invincible_dmc.event.VoidEvents;
import com.guhao.vix.client.lib.AbstractPostChainScreenEffect;
import com.guhao.vix.client.lib.PostChainScreenEffectRegistry;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.slf4j.Logger;

public final class VoidColorGradeRenderer extends AbstractPostChainScreenEffect {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation POST_CHAIN_LOCATION = InvincibleMod_DMC.rl("shaders/post/void_color_grade.json");
   private static final VoidColorGradeRenderer INSTANCE = new VoidColorGradeRenderer();
   private volatile boolean enabled;
   private PostChain shaderPackPostChain;
   private int shaderPackChainWidth = -1;
   private int shaderPackChainHeight = -1;
   private boolean shaderPackChainFailed;

   private VoidColorGradeRenderer() {
   }

   public static void init() {
      PostChainScreenEffectRegistry.register(INSTANCE);
   }

   protected ResourceLocation getPostChainLocation() {
      return POST_CHAIN_LOCATION;
   }

   protected boolean isEnabled() {
      return this.enabled && !OculusCompat.isShaderActive();
   }

   protected void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   protected void onClientTick(Minecraft minecraft) {
      boolean shouldEnable = minecraft.f_91073_ != null && minecraft.f_91073_.m_46472_().equals(VoidEvents.VOID_KEY);
      if (shouldEnable && !this.enabled) {
         this.enable();
      } else if (!shouldEnable && this.enabled) {
         this.disable();
      }
   }

   protected void onEnable() {
      this.shaderPackChainFailed = false;
   }

   protected void onDisable() {
      this.releaseShaderPackChain();
   }

   protected void applyUniforms(Minecraft minecraft, float partialTicks, Matrix4f projectionMatrix, PoseStack poseStack, List<PostPass> passes) {
   }

   public static void renderAfterShaderPack(float partialTick) {
      INSTANCE.renderShaderPackColorGrade(partialTick);
   }

   public static void releaseShaderPackResources() {
      INSTANCE.releaseShaderPackChain();
      INSTANCE.shaderPackChainFailed = false;
   }

   private void renderShaderPackColorGrade(float partialTick) {
      Minecraft minecraft = Minecraft.m_91087_();
      if (this.enabled && OculusCompat.isShaderActive() && minecraft.f_91073_ != null && minecraft.f_91073_.m_46472_().equals(VoidEvents.VOID_KEY)) {
         RenderTarget mainTarget = minecraft.m_91385_();

         try {
            if (this.ensureShaderPackChain(minecraft, mainTarget)) {
               if (this.shaderPackChainWidth != mainTarget.f_83915_ || this.shaderPackChainHeight != mainTarget.f_83916_) {
                  this.shaderPackPostChain.m_110025_(mainTarget.f_83915_, mainTarget.f_83916_);
                  this.shaderPackChainWidth = mainTarget.f_83915_;
                  this.shaderPackChainHeight = mainTarget.f_83916_;
               }

               RenderSystem.disableBlend();
               RenderSystem.disableDepthTest();
               RenderSystem.resetTextureMatrix();
               this.shaderPackPostChain.m_110023_(partialTick);
               return;
            }
         } catch (RuntimeException var8) {
            this.shaderPackChainFailed = true;
            this.releaseShaderPackChain();
            LOGGER.warn("Failed to render the void color grade after the shader-pack composite", var8);
            return;
         } finally {
            mainTarget.m_83947_(false);
         }
      } else {
         this.releaseShaderPackChain();
         this.shaderPackChainFailed = false;
      }
   }

   private boolean ensureShaderPackChain(Minecraft minecraft, RenderTarget mainTarget) {
      if (this.shaderPackPostChain != null) {
         return true;
      } else if (this.shaderPackChainFailed) {
         return false;
      } else {
         try {
            this.shaderPackPostChain = new PostChain(minecraft.m_91097_(), minecraft.m_91098_(), mainTarget, POST_CHAIN_LOCATION);
            this.shaderPackChainWidth = mainTarget.f_83915_;
            this.shaderPackChainHeight = mainTarget.f_83916_;
            return true;
         } catch (RuntimeException | IOException var4) {
            this.shaderPackChainFailed = true;
            this.releaseShaderPackChain();
            LOGGER.warn("Failed to create the shader-pack-compatible void color-grade chain", var4);
            return false;
         }
      }
   }

   private void releaseShaderPackChain() {
      if (this.shaderPackPostChain != null) {
         this.shaderPackPostChain.close();
         this.shaderPackPostChain = null;
      }

      this.shaderPackChainWidth = -1;
      this.shaderPackChainHeight = -1;
   }
}
