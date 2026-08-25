package com.dmc.invincible_dmc.client.render.afterimage;

import com.dmc.invincible_dmc.client.particles.StaticPoseAfterimageParticle;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent.Stage;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   modid = "invincible_dmc",
   value = {Dist.CLIENT}
)
public class ClientAfterimageHandler {
   private static final Map<Integer, AfterimageSnapshot.Buffer> ACTIVE_BUFFERS = new ConcurrentHashMap<>();

   public static void start(
      LivingEntityPatch<?> patch,
      int maxSnapshots,
      int intervalFrames,
      int maxAgeFrames,
      float alpha,
      boolean whiteMode,
      int contourRgb,
      float offsetForward,
      float offsetRight,
      float offsetUp
   ) {
      int id = ((LivingEntity)patch.getOriginal()).m_19879_();
      AfterimageSnapshot.Buffer buffer = ACTIVE_BUFFERS.computeIfAbsent(id, k -> new AfterimageSnapshot.Buffer(patch));
      buffer.configure(maxSnapshots, intervalFrames, maxAgeFrames, alpha, whiteMode, contourRgb, offsetForward, offsetRight, offsetUp);
   }

   public static void stop(LivingEntityPatch<?> patch) {
      int id = ((LivingEntity)patch.getOriginal()).m_19879_();
      AfterimageSnapshot.Buffer buffer = ACTIVE_BUFFERS.get(id);
      if (buffer != null) {
         buffer.stop();
      }
   }

   public static void clear(LivingEntityPatch<?> patch) {
      ACTIVE_BUFFERS.remove(((LivingEntity)patch.getOriginal()).m_19879_());
   }

   public static boolean isBufferActive(LivingEntityPatch<?> patch) {
      AfterimageSnapshot.Buffer buffer = ACTIVE_BUFFERS.get(((LivingEntity)patch.getOriginal()).m_19879_());
      return buffer != null && buffer.active;
   }

   public static void spawnInstantPoseParticle(LivingEntityPatch<?> patch, StaticAnimation animation, float time) {
      StaticPoseAfterimageParticle particle = StaticPoseAfterimageParticle.create(patch, animation, time, 6);
      if (particle != null) {
         Minecraft.m_91087_().f_91061_.m_107344_(particle);
      }
   }

   @SubscribeEvent
   public static void onRenderLevelStage(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_PARTICLES) {
         if (!ACTIVE_BUFFERS.isEmpty()) {
            Minecraft mc = Minecraft.m_91087_();
            Camera camera = mc.f_91063_.m_109153_();
            PoseStack poseStack = event.getPoseStack();
            BufferSource buffers = mc.m_91269_().m_110104_();
            Iterator<Entry<Integer, AfterimageSnapshot.Buffer>> it = ACTIVE_BUFFERS.entrySet().iterator();

            while (it.hasNext()) {
               Entry<Integer, AfterimageSnapshot.Buffer> entry = it.next();
               AfterimageSnapshot.Buffer buffer = entry.getValue();
               Entity entity = buffer.patch.getOriginal();
               if (!entity.m_6084_() || entity.m_213877_()) {
                  buffer.stop();
               }

               buffer.captureIfNeeded();
               buffer.ageAndCull();
               if (!buffer.active && buffer.snapshots.isEmpty()) {
                  it.remove();
               } else {
                  buffer.renderAll(poseStack, buffers, camera, event.getPartialTick());
               }
            }

            buffers.m_173043_();
         }
      }
   }
}
