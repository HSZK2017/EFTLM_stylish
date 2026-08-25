package com.dmc.invincible_dmc.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.particle.HitParticle;

@OnlyIn(Dist.CLIENT)
public class AttackMainRenderParticle extends HitParticle {
   private final double rotationBias;

   public AttackMainRenderParticle(ClientLevel world, double x, double y, double z, SpriteSet animatedSprite) {
      super(world, x, y, z, animatedSprite);
      this.f_107663_ = 1.5F;
      this.f_107225_ = 5;
      Random rand = new Random();
      float angle = (float)Math.toRadians((double)(rand.nextFloat() * 90.0F));
      this.f_107204_ = angle;
      this.f_107231_ = angle;
      this.rotationBias = 0.0;
   }

   public AttackMainRenderParticle(
      ClientLevel world, double x, double y, double z, double sizeScale, double rotationBias, double _null, SpriteSet animatedSprite
   ) {
      super(world, x, y, z, animatedSprite);
      float effectiveScale = (float)(sizeScale > 0.0 ? sizeScale : 1.0);
      this.f_107663_ = 1.85F * effectiveScale;
      this.f_107225_ = 4;
      this.rotationBias = rotationBias;
      Random rand = new Random();
      float angle;
      if (rotationBias < 0.0) {
         angle = (float)Math.toRadians((double)(-(15.0F + rand.nextFloat() * 75.0F)));
      } else if (rotationBias > 0.0) {
         angle = (float)Math.toRadians((double)(15.0F + rand.nextFloat() * 75.0F));
      } else {
         angle = (float)Math.toRadians((double)((rand.nextFloat() - 0.5F) * 90.0F));
      }

      this.f_107204_ = angle;
      this.f_107231_ = angle;
   }

   public void m_5744_(VertexConsumer buffer, Camera camera, float partialTick) {
      super.m_5744_(buffer, camera, partialTick);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Provider(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new AttackMainRenderParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
      }
   }
}
