package com.pla.annoyingvillagers.task;

import com.pla.annoyingvillagers.AnnoyingVillagers;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public abstract class DelayedTask {
   private static final DelayedTask.Scheduler SCHEDULER = new DelayedTask.Scheduler();
   private static final Queue<DelayedTask> PENDING_ADD = new ConcurrentLinkedQueue<>();
   private static boolean schedulerRegistered = false;
   private int remainingTicks;
   private boolean cancelled = false;
   private boolean finished = false;

   public DelayedTask(int waitTicks) {
      if (waitTicks < 0) {
         throw new IllegalArgumentException("waitTicks must be >= 0");
      } else {
         this.remainingTicks = waitTicks;
         ensureSchedulerRegistered();
         PENDING_ADD.add(this);
      }
   }

   public abstract void run();

   protected void onCancel() {
   }

   public final void cancel() {
      this.cancelled = true;
   }

   public final boolean isCancelled() {
      return this.cancelled;
   }

   public final boolean isFinished() {
      return this.finished;
   }

   public final int getRemainingTicks() {
      return Math.max(this.remainingTicks, 0);
   }

   private boolean tickInternal() {
      if (this.finished) {
         return true;
      } else if (this.cancelled) {
         this.finished = true;
         safeRun(this::onCancel, "DelayedTask onCancel");
         return true;
      } else {
         if (this.remainingTicks > 0) {
            this.remainingTicks--;
            if (this.remainingTicks > 0) {
               return false;
            }
         }

         this.finished = true;
         safeRun(this::run, "DelayedTask run");
         return true;
      }
   }

   private static synchronized void ensureSchedulerRegistered() {
      if (!schedulerRegistered) {
         MinecraftForge.EVENT_BUS.register(SCHEDULER);
         schedulerRegistered = true;
      }
   }

   private static void safeRun(Runnable action, String label) {
      try {
         action.run();
      } catch (Exception var3) {
         AnnoyingVillagers.LOGGER.error("[AV MOD DEBUG] {} failed", label, var3);
      }
   }

   private static final class Scheduler {
      private final List<DelayedTask> activeTasks = new ArrayList<>();

      @SubscribeEvent
      public void onServerTick(ServerTickEvent event) {
         if (event.phase == Phase.END) {
            DelayedTask pendingTask;
            while ((pendingTask = DelayedTask.PENDING_ADD.poll()) != null) {
               this.activeTasks.add(pendingTask);
            }

            Iterator<DelayedTask> iterator = this.activeTasks.iterator();

            while (iterator.hasNext()) {
               DelayedTask task = iterator.next();
               if (task.tickInternal()) {
                  iterator.remove();
               }
            }
         }
      }

      @SubscribeEvent
      public void onServerStopped(ServerStoppedEvent event) {
         this.activeTasks.clear();
         DelayedTask.PENDING_ADD.clear();
      }
   }
}
