package com.pla.annoyingvillagers.client.engine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class ThunderRender {
   private static final float REFRESH_TIME = 3.0F;
   private static final double MAX_OWNER_TRACK_TIME = 100.0;
   private ThunderRender.Timestamp refreshTimestamp = new ThunderRender.Timestamp();
   private final Random random = new Random();
   private final Minecraft minecraft = Minecraft.m_91087_();
   private final Map<Object, ThunderRender.BoltOwnerData> boltOwners = new Object2ObjectOpenHashMap();

   public void render(float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn) {
      VertexConsumer buffer = bufferIn.m_6299_(RenderType.m_110502_());
      Matrix4f matrix = matrixStackIn.m_85850_().m_252922_();

      assert this.minecraft.f_91073_ != null;

      ThunderRender.Timestamp timestamp = new ThunderRender.Timestamp(this.minecraft.f_91073_.m_46467_(), partialTicks);
      boolean refresh = timestamp.isPassed(this.refreshTimestamp, 0.33333334F);
      if (refresh) {
         this.refreshTimestamp = timestamp;
      }

      Iterator<Entry<Object, ThunderRender.BoltOwnerData>> iter = this.boltOwners.entrySet().iterator();

      while (iter.hasNext()) {
         Entry<Object, ThunderRender.BoltOwnerData> entry = iter.next();
         ThunderRender.BoltOwnerData data = entry.getValue();
         if (refresh) {
            data.bolts.removeIf(bolt -> bolt.tick(timestamp));
         }

         if (data.bolts.isEmpty() && data.lastBolt != null && data.lastBolt.getSpawnFunction().isConsecutive()) {
            data.addBolt(new ThunderRender.ThunderInstance(data.lastBolt, timestamp), timestamp);
         }

         data.bolts.forEach(bolt -> bolt.render(matrix, buffer, timestamp));
         if (data.bolts.isEmpty() && timestamp.isPassed(data.lastUpdateTimestamp, 100.0)) {
            iter.remove();
         }
      }
   }

   public void update(Object owner, ThunderRender.ThunderData newBoltData, float partialTicks) {
      if (this.minecraft.f_91073_ != null) {
         ThunderRender.BoltOwnerData data = this.boltOwners.computeIfAbsent(owner, o -> new ThunderRender.BoltOwnerData());
         data.lastBolt = newBoltData;
         ThunderRender.Timestamp timestamp = new ThunderRender.Timestamp(this.minecraft.f_91073_.m_46467_(), partialTicks);
         if ((!data.lastBolt.getSpawnFunction().isConsecutive() || data.bolts.isEmpty()) && timestamp.isPassed(data.lastBoltTimestamp, data.lastBoltDelay)) {
            data.addBolt(new ThunderRender.ThunderInstance(newBoltData, timestamp), timestamp);
         }

         data.lastUpdateTimestamp = timestamp;
      }
   }

   public class BoltOwnerData {
      private final Set<ThunderRender.ThunderInstance> bolts = new ObjectOpenHashSet();
      private ThunderRender.ThunderData lastBolt;
      private ThunderRender.Timestamp lastBoltTimestamp = new ThunderRender.Timestamp();
      private ThunderRender.Timestamp lastUpdateTimestamp = new ThunderRender.Timestamp();
      private double lastBoltDelay;

      private void addBolt(ThunderRender.ThunderInstance instance, ThunderRender.Timestamp timestamp) {
         this.bolts.add(instance);
         this.lastBoltDelay = (double)instance.bolt.getSpawnFunction().getSpawnDelay(ThunderRender.this.random);
         this.lastBoltTimestamp = timestamp;
      }
   }

   public static class ThunderData {
      private final Random random = new Random();
      private final ThunderRender.ThunderData.ThunderRenderInfo renderInfo;
      private final Vec3 start;
      private final Vec3 end;
      private final int segments;
      private int count = 1;
      private float size = 0.1F;
      private int lifespan = 30;
      private ThunderRender.ThunderData.SpawnFunction spawnFunction = ThunderRender.ThunderData.SpawnFunction.delay(60.0F);
      private ThunderRender.ThunderData.FadeFunction fadeFunction = ThunderRender.ThunderData.FadeFunction.fade(0.5F);

      public ThunderData(Vec3 start, Vec3 end) {
         this(ThunderRender.ThunderData.ThunderRenderInfo.DEFAULT, start, end, (int)Math.sqrt(start.m_82554_(end) * 100.0));
      }

      public ThunderData(ThunderRender.ThunderData.ThunderRenderInfo info, Vec3 start, Vec3 end, int segments) {
         this.renderInfo = info;
         this.start = start;
         this.end = end;
         this.segments = segments;
      }

      public ThunderRender.ThunderData count(int count) {
         this.count = count;
         return this;
      }

      public ThunderRender.ThunderData size(float size) {
         this.size = size;
         return this;
      }

      public ThunderRender.ThunderData spawn(ThunderRender.ThunderData.SpawnFunction spawnFunction) {
         this.spawnFunction = spawnFunction;
         return this;
      }

      public ThunderRender.ThunderData fade(ThunderRender.ThunderData.FadeFunction fadeFunction) {
         this.fadeFunction = fadeFunction;
         return this;
      }

      public ThunderRender.ThunderData lifespan(int lifespan) {
         this.lifespan = lifespan;
         return this;
      }

      public int getLifespan() {
         return this.lifespan;
      }

      public ThunderRender.ThunderData.SpawnFunction getSpawnFunction() {
         return this.spawnFunction;
      }

      public ThunderRender.ThunderData.FadeFunction getFadeFunction() {
         return this.fadeFunction;
      }

      public Vector4f getColor() {
         return this.renderInfo.color;
      }

      public List<ThunderRender.ThunderData.BoltQuads> generate() {
         List<ThunderRender.ThunderData.BoltQuads> quads = new ArrayList<>();
         Vec3 diff = this.end.m_82546_(this.start);
         float totalDistance = (float)diff.m_82553_();

         for (int i = 0; i < this.count; i++) {
            LinkedList<ThunderRender.ThunderData.BoltInstructions> drawQueue = new LinkedList<>();
            drawQueue.add(new ThunderRender.ThunderData.BoltInstructions(this.start, 0.0F, new Vec3(0.0, 0.0, 0.0), null, false));

            while (!drawQueue.isEmpty()) {
               ThunderRender.ThunderData.BoltInstructions data = drawQueue.poll();
               Vec3 perpendicularDist = data.perpendicularDist;
               float progress = data.progress
                  + 1.0F / (float)this.segments * (1.0F - this.renderInfo.parallelNoise + this.random.nextFloat() * this.renderInfo.parallelNoise * 2.0F);
               Vec3 segmentEnd;
               if (progress >= 1.0F) {
                  segmentEnd = this.end;
               } else {
                  float segmentDiffScale = this.renderInfo.spreadFunction.getMaxSpread(progress);
                  float maxDiff = this.renderInfo.spreadFactor * segmentDiffScale * totalDistance * this.renderInfo.randomFunction.getRandom(this.random);
                  Vec3 randVec = findRandomOrthogonalVector(diff, this.random);
                  perpendicularDist = this.renderInfo.segmentSpreader.getSegmentAdd(perpendicularDist, randVec, maxDiff, segmentDiffScale, progress);
                  if (this.renderInfo.spreadFactor <= 1.0E-4F) {
                     perpendicularDist = Vec3.f_82478_;
                  }

                  segmentEnd = this.start.m_82549_(diff.m_82490_((double)progress)).m_82549_(perpendicularDist);
               }

               float boltSize = this.size * (0.5F + (1.0F - progress) * 0.5F);
               Pair<ThunderRender.ThunderData.BoltQuads, ThunderRender.ThunderData.QuadCache> quadData = this.createQuads(
                  data.cache, data.start, segmentEnd, boltSize
               );
               quads.add((ThunderRender.ThunderData.BoltQuads)quadData.getLeft());
               if (segmentEnd == this.end) {
                  break;
               }

               if (!data.isBranch) {
                  drawQueue.add(
                     new ThunderRender.ThunderData.BoltInstructions(
                        segmentEnd, progress, perpendicularDist, (ThunderRender.ThunderData.QuadCache)quadData.getRight(), false
                     )
                  );
               } else if (this.random.nextFloat() < this.renderInfo.branchContinuationFactor) {
                  drawQueue.add(
                     new ThunderRender.ThunderData.BoltInstructions(
                        segmentEnd, progress, perpendicularDist, (ThunderRender.ThunderData.QuadCache)quadData.getRight(), true
                     )
                  );
               }

               while (this.random.nextFloat() < this.renderInfo.branchInitiationFactor * (1.0F - progress)) {
                  drawQueue.add(
                     new ThunderRender.ThunderData.BoltInstructions(
                        segmentEnd, progress, perpendicularDist, (ThunderRender.ThunderData.QuadCache)quadData.getRight(), true
                     )
                  );
               }
            }
         }

         return quads;
      }

      private static Vec3 findRandomOrthogonalVector(Vec3 vec, Random rand) {
         Vec3 newVec = new Vec3(-0.5 + rand.nextDouble(), -0.5 + rand.nextDouble(), -0.5 + rand.nextDouble());
         return vec.m_82537_(newVec).m_82541_();
      }

      private Pair<ThunderRender.ThunderData.BoltQuads, ThunderRender.ThunderData.QuadCache> createQuads(
         ThunderRender.ThunderData.QuadCache cache, Vec3 startPos, Vec3 end, float size
      ) {
         Vec3 diff = end.m_82546_(startPos);
         Vec3 rightAdd = diff.m_82537_(new Vec3(0.5, 0.5, 0.5)).m_82541_().m_82490_((double)size);
         Vec3 backAdd = diff.m_82537_(rightAdd).m_82541_().m_82490_((double)size);
         Vec3 rightAddSplit = rightAdd.m_82490_(0.5);
         Vec3 start = cache != null ? cache.prevEnd : startPos;
         Vec3 startRight = cache != null ? cache.prevEndRight : start.m_82549_(rightAdd);
         Vec3 startBack = cache != null ? cache.prevEndBack : start.m_82549_(rightAddSplit).m_82549_(backAdd);
         Vec3 endRight = end.m_82549_(rightAdd);
         Vec3 endBack = end.m_82549_(rightAddSplit).m_82549_(backAdd);
         ThunderRender.ThunderData.BoltQuads quads = new ThunderRender.ThunderData.BoltQuads();
         quads.addQuad(start, end, endRight, startRight);
         quads.addQuad(startRight, endRight, end, start);
         quads.addQuad(startRight, endRight, endBack, startBack);
         quads.addQuad(startBack, endBack, endRight, startRight);
         return Pair.of(quads, new ThunderRender.ThunderData.QuadCache(end, endRight, endBack));
      }

      protected static class BoltInstructions {
         private final Vec3 start;
         private final Vec3 perpendicularDist;
         private final ThunderRender.ThunderData.QuadCache cache;
         private final float progress;
         private final boolean isBranch;

         private BoltInstructions(Vec3 start, float progress, Vec3 perpendicularDist, ThunderRender.ThunderData.QuadCache cache, boolean isBranch) {
            this.start = start;
            this.perpendicularDist = perpendicularDist;
            this.progress = progress;
            this.cache = cache;
            this.isBranch = isBranch;
         }
      }

      public static class BoltQuads {
         private final List<Vec3> vecs = new ArrayList<>();

         protected void addQuad(Vec3... quadVecs) {
            this.vecs.addAll(Arrays.asList(quadVecs));
         }

         public List<Vec3> getVecs() {
            return this.vecs;
         }
      }

      public interface FadeFunction {
         ThunderRender.ThunderData.FadeFunction NONE = (totalBolts, lifeScale) -> Pair.of(0, totalBolts);

         static ThunderRender.ThunderData.FadeFunction fade(float fade) {
            return (totalBolts, lifeScale) -> {
               int start = lifeScale > 1.0F - fade ? (int)((float)totalBolts * (lifeScale - (1.0F - fade)) / fade) : 0;
               int end = lifeScale < fade ? (int)((float)totalBolts * (lifeScale / fade)) : totalBolts;
               return Pair.of(start, end);
            };
         }

         Pair<Integer, Integer> getRenderBounds(int var1, float var2);
      }

      private static record QuadCache(Vec3 prevEnd, Vec3 prevEndRight, Vec3 prevEndBack) {
      }

      public interface RandomFunction {
         ThunderRender.ThunderData.RandomFunction DEFAULT = rand -> (float)rand.nextGaussian();

         float getRandom(Random var1);
      }

      public interface SegmentSpreader {
         ThunderRender.ThunderData.SegmentSpreader DEFAULT = (perpendicularDist, randVec, maxDiff, scale, progress) -> randVec.m_82490_((double)maxDiff);

         static ThunderRender.ThunderData.SegmentSpreader memory(float memoryFactor) {
            return (perpendicularDist, randVec, maxDiff, spreadScale, progress) -> {
               float nextDiff = maxDiff * (1.0F - memoryFactor);
               Vec3 cur = randVec.m_82490_((double)nextDiff);
               if (progress > 0.5F) {
                  float pull = (1.0F - spreadScale) * (1.0F - memoryFactor) * 0.35F;
                  cur = cur.m_82549_(perpendicularDist.m_82490_((double)(-pull)));
               }

               return perpendicularDist.m_82549_(cur);
            };
         }

         Vec3 getSegmentAdd(Vec3 var1, Vec3 var2, float var3, float var4, float var5);
      }

      public interface SpawnFunction {
         ThunderRender.ThunderData.SpawnFunction DEFAULT = rand -> Pair.of(0.0F, 0.0F);

         static ThunderRender.ThunderData.SpawnFunction delay(float delay) {
            return rand -> Pair.of(delay, delay);
         }

         Pair<Float, Float> getSpawnDelayBounds(Random var1);

         default float getSpawnDelay(Random rand) {
            Pair<Float, Float> bounds = this.getSpawnDelayBounds(rand);
            return (Float)bounds.getLeft() + ((Float)bounds.getRight() - (Float)bounds.getLeft()) * rand.nextFloat();
         }

         default boolean isConsecutive() {
            return false;
         }
      }

      public interface SpreadFunction {
         ThunderRender.ThunderData.SpreadFunction DEFAULT = progress -> 1.0F;

         float getMaxSpread(float var1);
      }

      public static class ThunderRenderInfo {
         public static final ThunderRender.ThunderData.ThunderRenderInfo DEFAULT = new ThunderRender.ThunderData.ThunderRenderInfo();
         public static final ThunderRender.ThunderData.ThunderRenderInfo DRAGON_THUNDER = dragonThunder();
         public static final ThunderRender.ThunderData.ThunderRenderInfo BLUE_DEMON_THUNDER = blueDemonThunder();
         private float parallelNoise = 0.1F;
         private float spreadFactor = 0.0F;
         private float branchInitiationFactor = 0.0F;
         private float branchContinuationFactor = 0.0F;
         private Vector4f color = new Vector4f(0.45F, 0.45F, 0.5F, 0.8F);
         private final ThunderRender.ThunderData.RandomFunction randomFunction = ThunderRender.ThunderData.RandomFunction.DEFAULT;
         private final ThunderRender.ThunderData.SpreadFunction spreadFunction = ThunderRender.ThunderData.SpreadFunction.DEFAULT;
         private ThunderRender.ThunderData.SegmentSpreader segmentSpreader = ThunderRender.ThunderData.SegmentSpreader.DEFAULT;

         public static ThunderRender.ThunderData.ThunderRenderInfo dragonThunder() {
            return new ThunderRender.ThunderData.ThunderRenderInfo(0.15F, 0.025F, 0.0F, 0.0F, new Vector4f(0.85F, 0.55F, 1.0F, 0.85F), 0.8F);
         }

         public static ThunderRender.ThunderData.ThunderRenderInfo blueDemonThunder() {
            return new ThunderRender.ThunderData.ThunderRenderInfo(0.15F, 0.025F, 0.0F, 0.0F, new Vector4f(0.65F, 1.0F, 1.0F, 0.9F), 0.8F);
         }

         public ThunderRenderInfo() {
         }

         public ThunderRenderInfo(
            float parallelNoise, float spreadFactor, float branchInitiationFactor, float branchContinuationFactor, Vector4f color, float closeness
         ) {
            this.parallelNoise = parallelNoise;
            this.spreadFactor = spreadFactor;
            this.branchInitiationFactor = branchInitiationFactor;
            this.branchContinuationFactor = branchContinuationFactor;
            this.color = color;
            this.segmentSpreader = ThunderRender.ThunderData.SegmentSpreader.memory(closeness);
         }
      }
   }

   public static class ThunderInstance {
      private final ThunderRender.ThunderData bolt;
      private final List<ThunderRender.ThunderData.BoltQuads> renderQuads;
      private final ThunderRender.Timestamp createdTimestamp;

      public ThunderInstance(ThunderRender.ThunderData bolt, ThunderRender.Timestamp timestamp) {
         this.bolt = bolt;
         this.renderQuads = bolt.generate();
         this.createdTimestamp = timestamp;
      }

      public void render(Matrix4f matrix, VertexConsumer buffer, ThunderRender.Timestamp timestamp) {
         float lifeScale = timestamp.subtract(this.createdTimestamp).value() / (float)this.bolt.getLifespan();
         Pair<Integer, Integer> bounds = this.bolt.getFadeFunction().getRenderBounds(this.renderQuads.size(), lifeScale);

         for (int i = (Integer)bounds.getLeft(); i < bounds.getRight(); i++) {
            this.renderQuads
               .get(i)
               .getVecs()
               .forEach(
                  v -> buffer.m_252986_(matrix, (float)v.f_82479_, (float)v.f_82480_, (float)v.f_82481_)
                        .m_85950_(this.bolt.getColor().x(), this.bolt.getColor().y(), this.bolt.getColor().z(), this.bolt.getColor().w())
                        .m_5752_()
               );
         }
      }

      public boolean tick(ThunderRender.Timestamp timestamp) {
         return timestamp.isPassed(this.createdTimestamp, (double)this.bolt.getLifespan());
      }
   }

   public static class Timestamp {
      private final long ticks;
      private final float partial;

      public Timestamp() {
         this(0L, 0.0F);
      }

      public Timestamp(long ticks, float partial) {
         this.ticks = ticks;
         this.partial = partial;
      }

      public ThunderRender.Timestamp subtract(ThunderRender.Timestamp other) {
         long newTicks = this.ticks - other.ticks;
         float newPartial = this.partial - other.partial;
         if (newPartial < 0.0F) {
            newPartial++;
            newTicks--;
         }

         return new ThunderRender.Timestamp(newTicks, newPartial);
      }

      public float value() {
         return (float)this.ticks + this.partial;
      }

      public boolean isPassed(ThunderRender.Timestamp prev, double duration) {
         long ticksPassed = this.ticks - prev.ticks;
         if ((double)ticksPassed > duration) {
            return true;
         } else {
            duration -= (double)ticksPassed;
            return duration >= 1.0 ? false : (double)(this.partial - prev.partial) >= duration;
         }
      }
   }
}
