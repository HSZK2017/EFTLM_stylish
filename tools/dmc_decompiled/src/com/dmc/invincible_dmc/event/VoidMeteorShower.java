package com.dmc.invincible_dmc.event;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.server.S2CMeteorShowerPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;

@EventBusSubscriber(
   modid = "invincible_dmc"
)
public class VoidMeteorShower {
   private static int tickCounter = 0;
   private static int showerRemaining;
   private static int showerCursor;
   private static float[] showerBaseData;
   private static final int PER_TICK = 2;
   private static final float RX_RAD_MIN = (float)Math.toRadians(-30.0);
   private static final float RX_RAD_RANGE = (float)Math.toRadians(27.0);
   private static final float RY_RAD_MIN = (float)Math.toRadians(-999.0);
   private static final float RY_RAD_RANGE = (float)Math.toRadians(1998.0);

   private VoidMeteorShower() {
   }

   @SubscribeEvent
   public static void onLevelTick(LevelTickEvent event) {
      if (event.phase == Phase.END) {
         if (event.level instanceof ServerLevel serverLevel) {
            if (serverLevel.m_46472_().equals(VoidEvents.VOID_KEY)) {
               if (showerRemaining > 0) {
                  sendMeteorBatch(serverLevel);
               } else {
                  int interval = (Integer)DMConfig.METEOR_SHOWER_INTERVAL.get();
                  tickCounter++;
                  if (tickCounter >= interval) {
                     tickCounter = 0;
                     if (!serverLevel.m_6907_().isEmpty()) {
                        beginMeteorShower(serverLevel);
                        sendMeteorBatch(serverLevel);
                     }
                  }
               }
            }
         }
      }
   }

   private static void beginMeteorShower(ServerLevel level) {
      int maxCount = (Integer)DMConfig.METEOR_SHOWER_COUNT.get();
      showerRemaining = 1 + level.f_46441_.m_188503_(maxCount);
      showerCursor = 0;
      float minScale = ((Double)DMConfig.METEOR_SHOWER_MIN_SCALE.get()).floatValue();
      float maxScale = ((Double)DMConfig.METEOR_SHOWER_MAX_SCALE.get()).floatValue();
      float scaleRange = maxScale - minScale;
      showerBaseData = new float[showerRemaining * 7];

      for (int i = 0; i < showerRemaining; i++) {
         int off = i * 7;
         showerBaseData[off] = pickRange(level.f_46441_);
         showerBaseData[off + 1] = 300.0F;
         showerBaseData[off + 2] = pickRange(level.f_46441_);
         showerBaseData[off + 3] = RX_RAD_MIN + level.f_46441_.m_188501_() * RX_RAD_RANGE;
         showerBaseData[off + 4] = RY_RAD_MIN + level.f_46441_.m_188501_() * RY_RAD_RANGE;
         showerBaseData[off + 5] = RX_RAD_MIN + level.f_46441_.m_188501_() * RX_RAD_RANGE;
         showerBaseData[off + 6] = minScale + level.f_46441_.m_188501_() * scaleRange;
      }
   }

   private static void sendMeteorBatch(ServerLevel level) {
      int batchSize = Math.min(2, showerRemaining);
      int dataLen = batchSize * 7;
      int srcBase = showerCursor * 7;

      for (ServerPlayer player : level.m_6907_()) {
         if (!player.m_213877_()) {
            float[] shifted = new float[dataLen];
            float px = (float)player.m_20185_();
            float py = (float)player.m_20186_();
            float pz = (float)player.m_20189_();

            for (int i = 0; i < batchSize; i++) {
               int s = srcBase + i * 7;
               int d = i * 7;
               shifted[d] = showerBaseData[s] + px;
               shifted[d + 1] = showerBaseData[s + 1] + py;
               shifted[d + 2] = showerBaseData[s + 2] + pz;
               shifted[d + 3] = showerBaseData[s + 3];
               shifted[d + 4] = showerBaseData[s + 4];
               shifted[d + 5] = showerBaseData[s + 5];
               shifted[d + 6] = showerBaseData[s + 6];
            }

            DMCNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new S2CMeteorShowerPacket(shifted));
         }
      }

      showerCursor += batchSize;
      showerRemaining -= batchSize;
      if (showerRemaining <= 0) {
         showerBaseData = null;
      }
   }

   private static float pickRange(RandomSource random) {
      return random.m_188499_() ? 100.0F + random.m_188501_() * 200.0F : -300.0F + random.m_188501_() * 200.0F;
   }
}
