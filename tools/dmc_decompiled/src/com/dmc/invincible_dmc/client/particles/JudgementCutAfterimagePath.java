package com.dmc.invincible_dmc.client.particles;

import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

final class JudgementCutAfterimagePath {
   static final int TRAVEL_TICKS = 4;
   private static final float SCALE = 2.5F;

   private JudgementCutAfterimagePath() {
   }

   static JudgementCutAfterimagePath.Sample sample(RandomSource random, double centerX, double centerY, double centerZ, boolean reverse) {
      float radius = Mth.m_216267_(random, 5.0F, 8.0F);
      float theta = Mth.m_216267_(random, 0.0F, 360.0F);
      float beta = Mth.m_216267_(random, 45.0F, 80.0F);
      float endRadius = 8.0F;
      float endTheta = Mth.m_216267_(random, 135.0F + theta, 225.0F + theta);
      float endBeta = Mth.m_216267_(random, 160.0F + beta, 200.0F + beta);
      double thetaRadians = Math.toRadians((double)theta);
      double betaRadians = Math.toRadians((double)beta);
      double endThetaRadians = Math.toRadians((double)endTheta);
      double endBetaRadians = Math.toRadians((double)endBeta);
      double startHorizontalRadius = (double)radius * Math.sin(betaRadians);
      Vec3 start = new Vec3(
         centerX + startHorizontalRadius * Math.sin(thetaRadians) * 2.5,
         centerY + (double)radius * Math.cos(betaRadians) * 2.5 + 1.2,
         centerZ + startHorizontalRadius * Math.cos(thetaRadians) * 2.5
      );
      double endHorizontalRadius = (double)endRadius * Math.sin(endBetaRadians);
      Vec3 end = new Vec3(
         centerX - endHorizontalRadius * Math.sin(endThetaRadians) * 2.5,
         centerY + (double)endRadius * Math.cos(endBetaRadians) * 2.5 + 1.2,
         centerZ - endHorizontalRadius * Math.cos(endThetaRadians) * 2.5
      );
      Vec3 origin = reverse ? end : start;
      Vec3 destination = reverse ? start : end;
      return new JudgementCutAfterimagePath.Sample(origin, destination.m_82546_(origin));
   }

   static record Sample(Vec3 origin, Vec3 displacement) {
      Vec3 velocity() {
         return this.displacement.m_82490_(0.25);
      }
   }
}
