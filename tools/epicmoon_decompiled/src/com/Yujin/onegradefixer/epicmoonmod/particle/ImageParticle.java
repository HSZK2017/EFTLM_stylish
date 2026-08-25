package com.Yujin.onegradefixer.epicmoonmod.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.EntitySnapshot;
import yesman.epicfight.client.particle.EntityAfterimageParticle.WhiteAfterimageParticle;

@OnlyIn(Dist.CLIENT)
public class ImageParticle extends WhiteAfterimageParticle {
   public ImageParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, EntitySnapshot<?> snapshot) {
      super(level, x, y, z, xd, yd, zd, snapshot, p -> {
      });
   }

   public void m_5989_() {
      super.m_5989_();
      this.alphaO = this.f_107230_;
      this.f_107230_ -= 0.08F;
      if (this.f_107230_ <= 0.0F) {
         this.m_107274_();
      }
   }
}
