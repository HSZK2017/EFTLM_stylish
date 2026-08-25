package com.dmc.invincible_dmc.client.vfx;

import com.guhao.vix.api.tear.TearColor;
import com.guhao.vix.api.tear.TearPlane;
import com.guhao.vix.api.tear.TearRevealMode;
import com.guhao.vix.api.tear.TearStyle;
import com.guhao.vix.client.lib.tear.TearEffectRequest;
import com.guhao.vix.client.lib.tear.TearEffects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class YamatoTearEffects {
   private static final Vec3 UP = new Vec3(0.0, 1.0, 0.0);
   private static final AtomicLong NEXT_EFFECT_ID = new AtomicLong();
   private static final TearColor JUDGEMENT_CUT_CORE_COLOR = new TearColor(0.01F, 0.04F, 0.16F, 0.98F);
   private static final TearColor JUDGEMENT_CUT_EDGE_COLOR = new TearColor(0.08F, 0.58F, 1.0F, 0.86F);
   private static final TearColor JUDGEMENT_CUT_END_CORE_COLOR = new TearColor(0.01F, 0.07F, 0.28F, 1.0F);
   private static final TearColor JUDGEMENT_CUT_END_EDGE_COLOR = new TearColor(0.12F, 0.68F, 1.0F, 0.92F);
   private static final double JUDGEMENT_CUT_SURFACE_OFFSET = 0.012;
   private static final double JUDGEMENT_CUT_END_SURFACE_OFFSET = 0.018;
   private static final TearStyle JUDGEMENT_CUT_STYLE = TearStyle.builder()
      .textureAtlas(TearStyle.DEFAULT_TEXTURE, 1, 4)
      .coreColor(JUDGEMENT_CUT_CORE_COLOR)
      .edgeColor(JUDGEMENT_CUT_EDGE_COLOR)
      .coreWidth(0.024)
      .edgeWidth(0.085)
      .surfaceOffset(0.012)
      .jaggedness(0.05)
      .targetSegmentLength(0.18)
      .pointWidthVariation(0.34)
      .taperStrength(0.92)
      .scratchWidthVariation(0.25)
      .scratchOpacityVariation(0.0)
      .branchChance(0.5)
      .maxBranches(1)
      .microScratchChance(0.65)
      .maxMicroScratches(1)
      .lifetimeTicks(70)
      .fadeInTicks(1)
      .fadeOutTicks(20)
      .delayVariationTicks(0)
      .lifetimeVariationTicks(0)
      .fadeInVariationTicks(0)
      .fadeOutVariationTicks(0)
      .revealTicks(0)
      .revealMode(TearRevealMode.FROM_CENTER)
      .maxRenderDistance(64.0)
      .stableEmissiveRendering()
      .build();
   private static final TearStyle JUDGEMENT_CUT_END_STYLE = TearStyle.builder()
      .textureAtlas(TearStyle.DEFAULT_TEXTURE, 1, 4)
      .coreColor(JUDGEMENT_CUT_END_CORE_COLOR)
      .edgeColor(JUDGEMENT_CUT_END_EDGE_COLOR)
      .coreWidth(0.275)
      .edgeWidth(0.9)
      .surfaceOffset(0.018)
      .jaggedness(0.1)
      .targetSegmentLength(0.16)
      .pointWidthVariation(0.38)
      .taperStrength(0.88)
      .scratchWidthVariation(0.3)
      .scratchOpacityVariation(0.0)
      .branchChance(0.78)
      .maxBranches(3)
      .microScratchChance(0.82)
      .maxMicroScratches(2)
      .lifetimeTicks(140)
      .fadeInTicks(2)
      .fadeOutTicks(38)
      .delayVariationTicks(0)
      .lifetimeVariationTicks(0)
      .fadeInVariationTicks(0)
      .fadeOutVariationTicks(0)
      .revealTicks(0)
      .revealMode(TearRevealMode.FROM_CENTER)
      .maxRenderDistance(128.0)
      .stableEmissiveRendering()
      .build();

   private YamatoTearEffects() {
   }

   public static void playJudgementCut(Level level, Vec3 center) {
      if (level instanceof ClientLevel clientLevel) {
         long effectId = NEXT_EFFECT_ID.getAndIncrement();
         Random random = new Random(createSeed(clientLevel, center, effectId));

         for (int index = 0; index < 3; index++) {
            Vec3 tearCenter = randomNearbyCenter(center, random, index, 3, 0.3, 1.2);
            double angle = randomNonRadialAngle(center, tearCenter, random);
            spawnTear(clientLevel, tearCenter, angle, 3.0, 4.0, JUDGEMENT_CUT_STYLE, 4096, 256, "judgement_cut", effectId, index, random.nextLong());
         }
      }
   }

   public static void playJudgementCutEnd(Level level, Vec3 center) {
      if (level instanceof ClientLevel clientLevel) {
         long effectId = NEXT_EFFECT_ID.getAndIncrement();
         Random random = new Random(createSeed(clientLevel, center, effectId));

         for (int index = 0; index < 6; index++) {
            Vec3 tearCenter = randomNearbyCenter(center, random, index, 6, 1.5, 5.0);
            double angle = randomNonRadialAngle(center, tearCenter, random);
            spawnTear(clientLevel, tearCenter, angle, 24.0, 4.0, JUDGEMENT_CUT_END_STYLE, 32768, 768, "judgement_cut_end", effectId, index, random.nextLong());
         }
      }
   }

   private static void spawnTear(
      ClientLevel level,
      Vec3 center,
      double angle,
      double length,
      double breadth,
      TearStyle style,
      int maxBlockChecks,
      int maxDecals,
      String effectName,
      long effectId,
      int index,
      long seed
   ) {
      Vec3 axis = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
      Vec3 normal = axis.m_82537_(UP);
      TearPlane plane = TearPlane.builder(center, normal, axis).length(length).breadth(breadth).build();
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
         "invincible_dmc", "yamato_tear/" + effectName + "/" + Long.toUnsignedString(effectId) + "_" + index
      );
      TearEffectRequest request = TearEffectRequest.builder(id, plane).style(style).seed(seed).maxBlockChecks(maxBlockChecks).maxDecals(maxDecals).build();
      TearEffects.spawn(request);
   }

   private static Vec3 randomNearbyCenter(Vec3 center, Random random, int index, int count, double minRadius, double maxRadius) {
      double sectorSize = (Math.PI * 2) / (double)count;
      double placementAngle = ((double)index + random.nextDouble()) * sectorSize;
      double minRadiusSquared = minRadius * minRadius;
      double maxRadiusSquared = maxRadius * maxRadius;
      double radius = Math.sqrt(minRadiusSquared + random.nextDouble() * (maxRadiusSquared - minRadiusSquared));
      return center.m_82520_(Math.cos(placementAngle) * radius, 0.0, Math.sin(placementAngle) * radius);
   }

   private static double randomNonRadialAngle(Vec3 center, Vec3 tearCenter, Random random) {
      double radialAngle = Math.atan2(tearCenter.f_82481_ - center.f_82481_, tearCenter.f_82479_ - center.f_82479_);
      double tangentJitter = (random.nextDouble() * 2.0 - 1.0) * Math.PI / 4.0;
      return radialAngle + (Math.PI / 2) + tangentJitter;
   }

   private static long createSeed(ClientLevel level, Vec3 center, long effectId) {
      long seed = level.m_46467_() ^ effectId * -7046029254386353131L;
      seed ^= Double.doubleToLongBits(center.f_82479_);
      seed = Long.rotateLeft(seed, 21) ^ Double.doubleToLongBits(center.f_82480_);
      return Long.rotateLeft(seed, 21) ^ Double.doubleToLongBits(center.f_82481_);
   }
}
