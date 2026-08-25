package com.dmc.invincible_dmc.skill.weapon_innate.vergil;

import com.dmc.invincible_dmc.api.collider.YamatoExecutionLineCollider;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.server.S2CCameraShakePacket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.PacketDistributor;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.FORGE
)
public final class YamatoExecutionTargetManager {
   private static final double MIN_DIRECTION_LENGTH_SQR = 1.0E-8;
   private static final Map<UUID, YamatoExecutionTargetManager.CapturedTarget> CAPTURED_TARGETS = new HashMap<>();

   private YamatoExecutionTargetManager() {
   }

   public static void beginDash(LivingEntityPatch<?> attackerPatch) {
      if (!attackerPatch.isLogicalClient()) {
         CAPTURED_TARGETS.remove(((LivingEntity)attackerPatch.getOriginal()).m_20148_());
      }
   }

   public static boolean capture(LivingEntityPatch<?> attackerPatch, Entity hitEntity, YamatoExecutionLineCollider.HitSample hitSample) {
      if (!attackerPatch.isLogicalClient() && hitEntity instanceof LivingEntity target) {
         LivingEntity attacker = (LivingEntity)attackerPatch.getOriginal();
         if (attacker.m_20194_() != null && target != attacker && target.m_6084_() && target.m_9236_() == attacker.m_9236_()) {
            float attackerYaw = Mth.m_14177_(hitSample.attackerYaw());
            float targetYaw = resolveTargetYaw(attacker, hitSample.worldAnchor(), attackerYaw);
            alignAttacker(attackerPatch, attackerYaw);
            CAPTURED_TARGETS.entrySet().removeIf(entry -> entry.getValue().targetId().equals(target.m_20148_()));
            CAPTURED_TARGETS.put(
               attacker.m_20148_(),
               new YamatoExecutionTargetManager.CapturedTarget(attacker.m_9236_().m_46472_(), target.m_20148_(), hitSample.localAnchor(), targetYaw)
            );
            sendCaptureFeedback(attackerPatch, target);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Nullable
   public static YamatoExecutionTargetManager.CapturedTarget consumeCapturedTarget(LivingEntityPatch<?> attackerPatch) {
      return attackerPatch.isLogicalClient() ? null : CAPTURED_TARGETS.remove(((LivingEntity)attackerPatch.getOriginal()).m_20148_());
   }

   @Nullable
   public static YamatoExecutionTargetManager.CapturedTarget peekCapturedTarget(LivingEntityPatch<?> attackerPatch) {
      return attackerPatch.isLogicalClient() ? null : CAPTURED_TARGETS.get(((LivingEntity)attackerPatch.getOriginal()).m_20148_());
   }

   public static void abort(LivingEntityPatch<?> attackerPatch) {
      CAPTURED_TARGETS.remove(((LivingEntity)attackerPatch.getOriginal()).m_20148_());
   }

   public static void finish(LivingEntityPatch<?> attackerPatch) {
      abort(attackerPatch);
   }

   private static float resolveTargetYaw(LivingEntity attacker, Vec3 targetAnchor, float attackerYaw) {
      Vec3 targetToAttacker = new Vec3(attacker.m_20185_() - targetAnchor.f_82479_, 0.0, attacker.m_20189_() - targetAnchor.f_82481_);
      return targetToAttacker.m_82556_() <= 1.0E-8 ? Mth.m_14177_(attackerYaw + 180.0F) : Mth.m_14177_((float)MathUtils.getYRotOfVector(targetToAttacker));
   }

   private static void alignAttacker(LivingEntityPatch<?> attackerPatch, float yaw) {
      LivingEntity attacker = (LivingEntity)attackerPatch.getOriginal();
      attackerPatch.setYRotO(yaw);
      attackerPatch.setYRot(yaw);
      attacker.m_146922_(yaw);
      attacker.m_5618_(yaw);
      attacker.m_5616_(yaw);
      attacker.f_19859_ = yaw;
      attacker.f_20884_ = yaw;
      attacker.f_20886_ = yaw;
   }

   private static void sendCaptureFeedback(LivingEntityPatch<?> attackerPatch, LivingEntity target) {
      if (attackerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
         DMCNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(serverPlayerPatch::getOriginal), new S2CCameraShakePacket(target.m_20182_(), 8.0F, 3, 4.0F));
      }
   }

   @SubscribeEvent
   public static void onServerStopped(ServerStoppedEvent event) {
      CAPTURED_TARGETS.clear();
   }

   public static record CapturedTarget(ResourceKey<Level> dimension, UUID targetId, Vec3 localBladeAnchor, float targetYaw) {
   }
}
