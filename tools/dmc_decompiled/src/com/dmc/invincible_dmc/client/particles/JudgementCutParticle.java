package com.dmc.invincible_dmc.client.particles;

import com.dmc.invincible_dmc.client.config.YamatoClientConfig;
import com.dmc.invincible_dmc.client.effeks.LightSlashEffek;
import com.guhao.vix.util.RenderUtils;
import java.util.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class JudgementCutParticle extends NoRenderParticle {
   private static final int HIGH_QUALITY_TRAILS_PER_TICK = 2;
   private double bladeTrailBudget;

   public JudgementCutParticle(ClientLevel level, double x, double y, double z, double rx, double ry, double rz) {
      super(level, x, y, z, rx, ry, rz);
      this.f_107225_ = 29;
   }

   public boolean shouldCull() {
      return false;
   }

   public void m_5989_() {
      if (this.f_107224_++ >= this.f_107225_) {
         this.m_107274_();
      }

      float scale = 2.5F;
      this.bladeTrailBudget = this.bladeTrailBudget
         + 2.0 * ((YamatoClientConfig.JudgementCutBladeTrailQuality)YamatoClientConfig.JUDGEMENT_CUT_BLADE_TRAIL_QUALITY.get()).multiplier();
      int bladeTrailCount = (int)this.bladeTrailBudget;
      this.bladeTrailBudget -= (double)bladeTrailCount;

      for (int i = 0; i < bladeTrailCount; i++) {
         JudgementCutAfterimagePath.Sample path = JudgementCutAfterimagePath.sample(this.f_107223_, this.f_107212_, this.f_107213_, this.f_107214_, false);
         Vec3 origin = path.origin();
         Vec3 displacement = path.displacement();
         RenderUtils.AddParticle(
            this.f_107208_,
            new JCBladeTrail(
               this.f_107208_, origin.f_82479_, origin.f_82480_, origin.f_82481_, displacement.f_82479_, displacement.f_82480_, displacement.f_82481_
            )
         );
      }

      Random r3 = new Random();
      LightSlashEffek.playLightSlash(
         LightSlashEffek.Type.LEVEL1,
         this.f_107208_,
         this.f_107212_,
         this.f_107213_,
         this.f_107214_,
         (float)this.f_107223_.m_188503_(90),
         (float)this.f_107223_.m_188503_(90),
         (float)this.f_107223_.m_188503_(90),
         scale * r3.nextFloat(0.12F, 0.22F)
      );
   }

   @NotNull
   public ParticleRenderType m_7556_() {
      return ParticleRenderType.f_107430_;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Provider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(
         @NotNull SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new JudgementCutParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
      }
   }
}
