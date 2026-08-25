package com.dmc.invincible_dmc.client.render.afterimage;

import com.dmc.invincible_dmc.api.events.BaseConsumer;
import com.dmc.invincible_dmc.api.events.BaseEvent;
import com.dmc.invincible_dmc.capability.DMCPlayer;
import java.util.function.Consumer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import yesman.epicfight.api.animation.property.AnimationEvent.E0;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class PerFrameAfterimageRenderer {
   public static void start(LivingEntityPatch<?> patch, int maxSnapshots, int intervalFrames, int maxAgeFrames, float alpha, boolean whiteMode) {
      start(patch, maxSnapshots, intervalFrames, maxAgeFrames, alpha, whiteMode, 4491519, 0.0F, 0.0F, 0.0F);
   }

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
      if (FMLEnvironment.dist == Dist.CLIENT) {
         ClientAfterimageHandler.start(patch, maxSnapshots, intervalFrames, maxAgeFrames, alpha, whiteMode, contourRgb, offsetForward, offsetRight, offsetUp);
      }
   }

   public static void stop(LivingEntityPatch<?> patch) {
      if (FMLEnvironment.dist == Dist.CLIENT) {
         ClientAfterimageHandler.stop(patch);
      }
   }

   public static void clear(LivingEntityPatch<?> patch) {
      if (FMLEnvironment.dist == Dist.CLIENT) {
         ClientAfterimageHandler.clear(patch);
      }
   }

   public static void dash(LivingEntityPatch<?> patch) {
      start(patch, 1, 1, 10, 0.35F, false, 37111, -1.45F, 0.0F, 0.0F);
   }

   public static void instant(LivingEntityPatch<?> patch) {
      start(patch, 3, 1, 6, 0.7F, true, 16777215, 0.0F, 0.0F, 0.0F);
   }

   public static Consumer<LivingEntityPatch<?>> instantAtPose(StaticAnimation animation, float time) {
      return patch -> {
         if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientAfterimageHandler.spawnInstantPoseParticle(patch, animation, time);
         }
      };
   }

   public static void fade(LivingEntityPatch<?> patch) {
      start(patch, 8, 2, 15, 0.4F, false, 1118498, -0.2F, 0.0F, 0.0F);
   }

   public static InTimeEvent<?> inTimeEvent(float time, Consumer<LivingEntityPatch<?>> action, Side side) {
      E0 event = (entitypatch, animation, params) -> action.accept(entitypatch);
      return InTimeEvent.create(time, event, side);
   }

   public static InTimeEvent<?> stopInTimeEvent(float time, Side side) {
      E0 event = (entitypatch, animation, params) -> stop(entitypatch);
      return InTimeEvent.create(time, event, side);
   }

   public static InPeriodEvent<?> inPeriodEvent(float startTime, float endTime, Consumer<LivingEntityPatch<?>> action, Side side) {
      E0 event = (entitypatch, animation, params) -> {
         if (FMLEnvironment.dist == Dist.CLIENT && !ClientAfterimageHandler.isBufferActive(entitypatch)) {
            action.accept(entitypatch);
         }
      };
      return InPeriodEvent.create(startTime, endTime, event, side);
   }

   public static SimpleEvent<?> simpleEvent(Consumer<LivingEntityPatch<?>> action, Side side) {
      E0 event = (entitypatch, animation, params) -> action.accept(entitypatch);
      return SimpleEvent.create(event, side);
   }

   public static BaseEvent baseEvent(Consumer<LivingEntityPatch<?>> action) {
      BaseConsumer consumer = (playerPatch, target, invinciblePlayer) -> action.accept(playerPatch);
      return BaseEvent.createClientEvent(consumer);
   }

   public static BaseEvent stopEvent() {
      return baseEvent(PerFrameAfterimageRenderer::stop);
   }

   public static BaseEvent clearEvent() {
      return baseEvent(PerFrameAfterimageRenderer::clear);
   }

   public static BaseConsumer periodConsumer(final Consumer<LivingEntityPatch<?>> startAction) {
      return new BaseConsumer() {
         boolean started;

         @Override
         public void accept(PlayerPatch<?> playerPatch, Entity target, DMCPlayer DMCPlayer) {
            if (!this.started) {
               startAction.accept(playerPatch);
               this.started = true;
            }
         }
      };
   }
}
