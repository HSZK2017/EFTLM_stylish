package com.pla.annoyingvillagers.client.engine;

import java.util.Comparator;
import java.util.PriorityQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
public class CameraEngine {
   private static CameraEngine instance;
   private final PriorityQueue<CameraEngine.ShakeEntry> queue = new PriorityQueue<>(Comparator.comparingDouble(e -> -e.strength));
   private final CameraEngine.ShakeEntry default_entry = new CameraEngine.ShakeEntry(1.0, 3, 0.3, 0);

   public CameraEngine() {
      instance = this;
   }

   public static CameraEngine getInstance() {
      return instance;
   }

   public PriorityQueue<CameraEngine.ShakeEntry> getQueue() {
      return this.queue;
   }

   public void tick(ComputeCameraAngles event, Player player) {
      if (!Minecraft.m_91087_().m_91104_() && !this.queue.isEmpty()) {
         this.queue.removeIf(entry -> {
            entry.remainingTicks--;
            if (entry.remainingTicks < entry.decay_time) {
               entry.strength *= 0.97;
               entry.frequency *= 0.97;
            }

            return entry.remainingTicks <= 0;
         });
         if (!this.queue.isEmpty()) {
            CameraEngine.ShakeEntry top = this.queue.peek();
            double ticksExistedDelta = (double)player.f_19797_ + event.getPartialTick();
            double k = top.strength / 4.0;
            double f = top.frequency;
            event.setPitch((float)((double)event.getPitch() + k * Math.cos(ticksExistedDelta * f + 2.0)));
            event.setYaw((float)((double)event.getYaw() + k * Math.cos(ticksExistedDelta * f + 1.0)));
            event.setRoll((float)((double)event.getRoll() + k * Math.cos(ticksExistedDelta * f)));
         }
      }
   }

   public void shakeCamera(CameraEngine.ShakeEntry entry) {
      CameraEngine.ShakeEntry entry1 = entry.copy();
      this.queue.add(entry1);
   }

   public void shakeCamera(float strength, int time, float frequency, int decay_time) {
      this.shakeCamera(new CameraEngine.ShakeEntry((double)strength, time, (double)frequency, decay_time));
   }

   public void shakeCamera(int time, float strength, int decay_time) {
      this.shakeCamera(new CameraEngine.ShakeEntry((double)strength, time, 0.3, decay_time));
   }

   @EventBusSubscriber(
      modid = "annoyingvillagers",
      value = {Dist.CLIENT}
   )
   public static class Events {
      @SubscribeEvent(
         priority = EventPriority.LOW
      )
      public static void cameraSetupEvent(ComputeCameraAngles event) {
         Player player = Minecraft.m_91087_().f_91074_;
         if (player != null) {
            if (CameraEngine.instance != null) {
               CameraEngine.instance.tick(event, player);
            }
         }
      }
   }

   public static class ShakeEntry {
      double strength;
      int remainingTicks;
      int decay_time;
      double frequency;

      public ShakeEntry(double strength, int tick, double frequency, int decay_time) {
         this.strength = strength;
         this.remainingTicks = tick;
         this.frequency = frequency;
         this.decay_time = decay_time;
      }

      public ShakeEntry(double strength, int tick, int decay_time) {
         this(strength, tick, 0.3F, decay_time);
      }

      public CameraEngine.ShakeEntry copy() {
         return new CameraEngine.ShakeEntry(this.strength, this.remainingTicks, this.frequency, this.decay_time);
      }
   }
}
