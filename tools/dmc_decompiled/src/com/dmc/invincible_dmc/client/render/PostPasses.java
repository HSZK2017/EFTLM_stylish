package com.dmc.invincible_dmc.client.render;

import com.dmc.invincible_dmc.client.render.shaderpasses.BlackWhiteContrastSimple;
import com.dmc.invincible_dmc.client.render.shaderpasses.Blur;
import com.dmc.invincible_dmc.client.render.shaderpasses.ChromaticAberration;
import com.dmc.invincible_dmc.client.render.shaderpasses.ChromaticAberrationEnhanced;
import com.dmc.invincible_dmc.client.render.shaderpasses.ColdGrayPass;
import com.dmc.invincible_dmc.client.render.shaderpasses.ColorRadialBlurPass;
import com.dmc.invincible_dmc.client.render.shaderpasses.DepthCull;
import com.dmc.invincible_dmc.client.render.shaderpasses.DownSampling;
import com.dmc.invincible_dmc.client.render.shaderpasses.EdgeGlowPass;
import com.dmc.invincible_dmc.client.render.shaderpasses.ImpactBlurPass;
import com.dmc.invincible_dmc.client.render.shaderpasses.PureChromaticAberrationPass;
import com.dmc.invincible_dmc.client.render.shaderpasses.RiftAttractionPass;
import com.dmc.invincible_dmc.client.render.shaderpasses.ScreenDistortion;
import com.dmc.invincible_dmc.client.render.shaderpasses.ScreenFlashPass;
import com.dmc.invincible_dmc.client.render.shaderpasses.ScreenVignettePass;
import com.dmc.invincible_dmc.client.render.shaderpasses.SpaceBroken;
import com.dmc.invincible_dmc.client.render.shaderpasses.StaticAirDisturbance;
import com.dmc.invincible_dmc.client.render.shaderpasses.UnityComposite;
import com.dmc.invincible_dmc.client.render.shaderpasses.UpSampling;
import com.guhao.vix.client.shaderpasses.PostPassBase;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;

@OnlyIn(Dist.CLIENT)
public class PostPasses {
   public static PostPassBase blit;
   public static Blur blur;
   public static DepthCull depth_cull;
   public static SpaceBroken space_broken;
   public static DownSampling downSampler;
   public static UpSampling upSampler;
   public static UnityComposite unity_composite;
   public static ScreenDistortion screen_distortion;
   public static RiftAttractionPass rift_attraction;
   public static ColdGrayPass cold_gray;
   public static ScreenFlashPass screen_flash;
   public static ScreenVignettePass screen_vignette;
   public static ChromaticAberration chromatic_aberration;
   public static ChromaticAberrationEnhanced chromatic_aberration_enhanced;
   public static StaticAirDisturbance static_air_disturbance;
   public static EdgeGlowPass edge_glow;
   public static BlackWhiteContrastSimple black_white_contrast;
   public static ColorRadialBlurPass color_radial_blur;
   public static PureChromaticAberrationPass pure_chromatic_aberration;
   public static ImpactBlurPass impact_blur;

   public static void register(RegisterShadersEvent event) {
      ResourceManager rm = Minecraft.m_91087_().m_91098_();

      try {
         blit = new PostPassBase("invincible_dmc:blit", rm);
      } catch (IOException var22) {
      }

      try {
         downSampler = new DownSampling("invincible_dmc:down_sampling", rm);
      } catch (IOException var21) {
      }

      try {
         upSampler = new UpSampling("invincible_dmc:up_sampling", rm);
      } catch (IOException var20) {
      }

      try {
         unity_composite = new UnityComposite("invincible_dmc:unity_composite", rm);
      } catch (IOException var19) {
      }

      try {
         space_broken = new SpaceBroken("invincible_dmc:space_broken", rm);
      } catch (IOException var18) {
      }

      try {
         depth_cull = new DepthCull("invincible_dmc:depth_cull", rm);
      } catch (IOException var17) {
      }

      try {
         blur = new Blur(rm);
      } catch (IOException var16) {
      }

      try {
         screen_distortion = new ScreenDistortion("invincible_dmc:screen_distortion", rm);
      } catch (IOException var15) {
      }

      try {
         rift_attraction = new RiftAttractionPass("invincible_dmc:rift_attraction", rm);
      } catch (IOException var14) {
      }

      try {
         cold_gray = new ColdGrayPass("invincible_dmc:cold_gray", rm);
      } catch (IOException var13) {
      }

      try {
         screen_flash = new ScreenFlashPass("invincible_dmc:screen_flash", rm);
      } catch (IOException var12) {
      }

      try {
         screen_vignette = new ScreenVignettePass("invincible_dmc:screen_vignette", rm);
      } catch (IOException var11) {
      }

      try {
         chromatic_aberration = new ChromaticAberration("invincible_dmc:chromatic_aberration", rm);
      } catch (IOException var10) {
      }

      try {
         chromatic_aberration_enhanced = new ChromaticAberrationEnhanced("invincible_dmc:chromatic_aberration_enhanced", rm);
      } catch (IOException var9) {
      }

      try {
         static_air_disturbance = new StaticAirDisturbance("invincible_dmc:static_air_disturbance", rm);
      } catch (IOException var8) {
      }

      try {
         edge_glow = new EdgeGlowPass("invincible_dmc:edge_glow", rm);
      } catch (IOException var7) {
      }

      try {
         black_white_contrast = new BlackWhiteContrastSimple("invincible_dmc:black_white_simple", rm);
      } catch (IOException var6) {
      }

      try {
         color_radial_blur = new ColorRadialBlurPass("invincible_dmc:color_radial_blur", rm);
      } catch (IOException var5) {
      }

      try {
         pure_chromatic_aberration = new PureChromaticAberrationPass("invincible_dmc:pure_chromatic_aberration", rm);
      } catch (IOException var4) {
      }

      try {
         impact_blur = new ImpactBlurPass("invincible_dmc:impact_blur", rm);
      } catch (IOException var3) {
      }
   }
}
