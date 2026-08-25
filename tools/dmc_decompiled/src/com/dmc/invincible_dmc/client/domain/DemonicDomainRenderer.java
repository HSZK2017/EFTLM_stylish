package com.dmc.invincible_dmc.client.domain;

import com.guhao.vix.client.lib.AbstractPostChainScreenEffect;
import com.guhao.vix.client.lib.PostChainScreenEffectRegistry;
import com.guhao.vix.util.OjangUtils;
import com.mojang.blaze3d.shaders.AbstractUniform;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class DemonicDomainRenderer extends AbstractPostChainScreenEffect {
   private static final ResourceLocation POST_CHAIN_LOC = OjangUtils.newRL("invincible_dmc:shaders/post/demonic_domain.json");
   public static final int LIFETIME_TICKS = 36;
   private static final int FADE_OUT_TICKS = 8;
   private static final float LIFETIME_SECONDS = 1.8F;
   private static final DemonicDomainRenderer INSTANCE = new DemonicDomainRenderer();
   private volatile boolean enabled;
   private int elapsedTicks;
   private final DemonicDomainEffect effect = new DemonicDomainEffect();

   private DemonicDomainRenderer() {
   }

   public static DemonicDomainRenderer getInstance() {
      return INSTANCE;
   }

   public static void init() {
      PostChainScreenEffectRegistry.register(INSTANCE);
   }

   protected ResourceLocation getPostChainLocation() {
      return POST_CHAIN_LOC;
   }

   protected boolean isEnabled() {
      return this.enabled;
   }

   protected void setEnabled(boolean value) {
      this.enabled = value;
   }

   protected void onClientTick(Minecraft mc) {
      if (this.enabled) {
         this.elapsedTicks++;
         if (this.elapsedTicks >= 36) {
            this.disable();
            this.closeChain();
         }
      }
   }

   protected void onDisable() {
      this.elapsedTicks = 0;
      this.startTimeNanos = 0L;
   }

   protected void applyUniforms(Minecraft mc, float partialTicks, Matrix4f projectionMatrix, PoseStack poseStack, List<PostPass> passes) {
      Matrix4f invProjection = new Matrix4f(projectionMatrix);
      invProjection.invert();
      Matrix4f modelView = poseStack.m_85850_().m_252922_();
      Camera camera = mc.f_91063_.m_109153_();
      Vec3 camPos = camera.m_90583_();
      float elapsed = ((float)this.elapsedTicks + partialTicks) / 20.0F;
      float progress = elapsed / 1.8F;
      float fadeThreshold = 0.7777778F;
      float fadeFactor;
      if (progress < fadeThreshold) {
         fadeFactor = 1.0F;
      } else {
         float t = (progress - fadeThreshold) / (1.0F - fadeThreshold);
         fadeFactor = 1.0F - t * t * (3.0F - 2.0F * t);
      }

      float currentAlpha = this.effect.getTintAlpha() * fadeFactor;

      for (PostPass pass : passes) {
         EffectInstance eff = pass.m_110074_();
         AbstractUniform u = eff.m_108960_("InverseTransformMatrix");
         if (u != null) {
            u.m_5679_(invProjection);
         }

         u = eff.m_108960_("ModelViewMat");
         if (u != null) {
            u.m_5679_(modelView);
         }

         u = eff.m_108960_("CameraPosition");
         if (u != null) {
            u.m_5889_((float)camPos.f_82479_, (float)camPos.f_82480_, (float)camPos.f_82481_);
         }

         u = eff.m_108960_("DomainCenter");
         if (u != null) {
            Vector3f c = this.effect.getDomainCenter();
            u.m_5889_(c.x(), c.y(), c.z());
         }

         u = eff.m_108960_("DomainRadius");
         if (u != null) {
            u.m_5985_(this.effect.getDomainRadius());
         }

         u = eff.m_108960_("ScanTime");
         if (u != null) {
            u.m_5985_(elapsed);
         }

         u = eff.m_108960_("LifetimeSeconds");
         if (u != null) {
            u.m_5985_(1.8F);
         }

         u = eff.m_108960_("FadeFactor");
         if (u != null) {
            u.m_5985_(fadeFactor);
         }

         u = eff.m_108960_("ScanSpeed");
         if (u != null) {
            u.m_5985_(this.effect.getScanSpeed());
         }

         u = eff.m_108960_("ScanFrequency");
         if (u != null) {
            u.m_5985_(this.effect.getScanFrequency());
         }

         u = eff.m_108960_("SweepSpeed");
         if (u != null) {
            u.m_5985_(this.effect.getSweepSpeed());
         }

         u = eff.m_108960_("TintAlpha");
         if (u != null) {
            u.m_5985_(currentAlpha);
         }

         u = eff.m_108960_("TintColor");
         if (u != null) {
            Vector3f tc = this.effect.getTintColor();
            u.m_5889_(tc.x(), tc.y(), tc.z());
         }

         u = eff.m_108960_("GlowColor");
         if (u != null) {
            Vector3f gc = this.effect.getGlowColor();
            u.m_5889_(gc.x(), gc.y(), gc.z());
         }
      }
   }

   public DemonicDomainEffect getEffect() {
      return this.effect;
   }

   public void expand(double x, double y, double z, float radius) {
      this.effect.setDomainCenter((float)x, (float)y, (float)z);
      this.effect.setDomainRadius(radius);
      this.elapsedTicks = 0;
      this.startTimeNanos = 0L;
      this.enable();
   }

   public boolean isExpanded() {
      return this.isActive();
   }
}
