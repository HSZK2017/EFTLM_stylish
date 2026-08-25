package com.pla.annoyingvillagers.task;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class TaskScheduler {
   private static final List<TaskScheduler.ScheduledTask> tasks = new LinkedList<>();
   private static final List<TaskScheduler.ScheduledTask> pendingTasks = new LinkedList<>();

   public static void schedule(Runnable task, int delayTicks) {
      synchronized (pendingTasks) {
         pendingTasks.add(new TaskScheduler.ScheduledTask(task, delayTicks));
      }
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         synchronized (pendingTasks) {
            tasks.addAll(pendingTasks);
            pendingTasks.clear();
         }

         Iterator<TaskScheduler.ScheduledTask> iterator = tasks.iterator();

         while (iterator.hasNext()) {
            TaskScheduler.ScheduledTask task = iterator.next();
            task.ticksRemaining--;
            if (task.ticksRemaining <= 0) {
               try {
                  task.task.run();
               } catch (Exception var4) {
                  var4.printStackTrace();
               }

               iterator.remove();
            }
         }
      }
   }

   private static class ScheduledTask {
      Runnable task;
      int ticksRemaining;

      ScheduledTask(Runnable task, int ticks) {
         this.task = task;
         this.ticksRemaining = ticks;
      }
   }
}
