package com.pla.annoyingvillagers.init;

import com.pla.annoyingvillagers.AnnoyingVillagers;
import com.pla.annoyingvillagers.network.SpecialAttackMessage;
import com.pla.annoyingvillagers.network.ThrowingEnderPearlMessage;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.joml.Vector3f;

@EventBusSubscriber(
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class AnnoyingVillagersModKeyMappings {
   private static final double SPECIAL_ATTACK_CROSSHAIR_RANGE = 32.0;
   public static final KeyMapping SPECIAL_ATTACK = new KeyMapping("key.annoyingvillagers.special_attack", 67, "key.categories.annoyingvillagers") {
      private static final int HOLD_THRESHOLD_TICKS = 10;
      private boolean isDownOld = false;
      private int pressedAtTick = -1;

      public void m_7249_(boolean flag) {
         super.m_7249_(flag);
         Minecraft minecraft = Minecraft.m_91087_();
         if (minecraft.f_91074_ == null) {
            this.isDownOld = flag;
         } else {
            if (!this.isDownOld && flag) {
               this.pressedAtTick = minecraft.f_91074_.f_19797_;
            }

            if (this.isDownOld && !flag) {
               int heldTicks = this.pressedAtTick >= 0 ? minecraft.f_91074_.f_19797_ - this.pressedAtTick : 0;
               int type = heldTicks >= 10 ? 1 : 0;
               AnnoyingVillagers.PACKET_HANDLER.sendToServer(AnnoyingVillagersModKeyMappings.createSpecialAttackMessage(type, heldTicks));
               this.pressedAtTick = -1;
            }

            this.isDownOld = flag;
         }
      }
   };
   public static final KeyMapping THROW_ENDER_PEARL = new KeyMapping("key.annoyingvillagers.throw_ender_pearl", 70, "key.categories.annoyingvillagers") {
      private boolean isDownOld = false;

      public void m_7249_(boolean flag) {
         super.m_7249_(flag);
         if (this.isDownOld != flag && flag && Minecraft.m_91087_().f_91074_ != null) {
            AnnoyingVillagers.PACKET_HANDLER.sendToServer(new ThrowingEnderPearlMessage(0, 0));
            ThrowingEnderPearlMessage.pressAction(Minecraft.m_91087_().f_91074_, 0, 0);
         }

         this.isDownOld = flag;
      }
   };
   public static final KeyMapping DRAGON_FLIGHT_DESCENT_KEY = new KeyMapping(
      "key.annoyingvillagers.dragon_flight_descent", 90, "key.categories.annoyingvillagers"
   );

   @SubscribeEvent
   public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
      event.register(SPECIAL_ATTACK);
      event.register(THROW_ENDER_PEARL);
      event.register(DRAGON_FLIGHT_DESCENT_KEY);
   }

   private static SpecialAttackMessage createSpecialAttackMessage(int type, int heldTicks) {
      Vec3 crosshairTarget = findSpecialAttackCrosshairTarget(Minecraft.m_91087_());
      return crosshairTarget == null ? new SpecialAttackMessage(type, heldTicks) : new SpecialAttackMessage(type, heldTicks, crosshairTarget);
   }

   private static Vec3 findSpecialAttackCrosshairTarget(Minecraft minecraft) {
      if (minecraft.f_91077_ != null && minecraft.f_91077_.m_6662_() != Type.MISS) {
         return minecraft.f_91077_.m_82450_();
      } else {
         Camera camera = minecraft.f_91063_.m_109153_();
         if (camera.m_90593_()) {
            Vector3f look = camera.m_253058_();
            return camera.m_90583_().m_82520_((double)look.x() * 32.0, (double)look.y() * 32.0, (double)look.z() * 32.0);
         } else {
            return minecraft.f_91074_ == null ? null : minecraft.f_91074_.m_20299_(1.0F).m_82549_(minecraft.f_91074_.m_20252_(1.0F).m_82490_(32.0));
         }
      }
   }

   @EventBusSubscriber({Dist.CLIENT})
   public static class KeyEventListener {
      @SubscribeEvent
      public static void onClientTick(ClientTickEvent event) {
         if (event.phase == Phase.END) {
            Minecraft mc = Minecraft.m_91087_();
            if (mc.f_91080_ == null) {
               AnnoyingVillagersModKeyMappings.SPECIAL_ATTACK.m_90859_();
               AnnoyingVillagersModKeyMappings.THROW_ENDER_PEARL.m_90859_();
            }
         }
      }
   }
}
