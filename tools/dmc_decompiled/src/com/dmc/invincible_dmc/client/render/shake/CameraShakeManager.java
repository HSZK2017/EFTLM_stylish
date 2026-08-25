package com.dmc.invincible_dmc.client.render.shake;

import com.guhao.vix.mixin.accessor.CameraAccessor;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   modid = "invincible_dmc",
   value = {Dist.CLIENT}
)
public class CameraShakeManager {
   private static final List<CameraShakeInstance> ACTIVE_SHAKES = new ArrayList<>();
   private static final int MAX_CONCURRENT_SHAKES = 3;

   public static synchronized void addShake(Vec3 targetPos, float intensity, int durationTicks, float frequency) {
      Minecraft mc = Minecraft.m_91087_();
      if (mc.f_91074_ != null) {
         Vec3 cameraPos = mc.f_91063_.m_109153_().m_90583_();
         CameraShakeInstance mergeTarget = null;

         for (CameraShakeInstance shake : ACTIVE_SHAKES) {
            if (shake.getElapsedTicks() <= 1) {
               mergeTarget = shake;
               break;
            }
         }

         if (mergeTarget != null) {
            mergeTarget.merge(targetPos, intensity, durationTicks, frequency, cameraPos);
         } else if (ACTIVE_SHAKES.size() < 3) {
            ACTIVE_SHAKES.add(new CameraShakeInstance(targetPos, intensity, durationTicks, frequency));
         } else {
            ACTIVE_SHAKES.remove(0);
            ACTIVE_SHAKES.add(new CameraShakeInstance(targetPos, intensity, durationTicks, frequency));
         }
      }
   }

   public static void addShakeAtJoint(LivingEntityPatch<?> entityPatch, String jointName, float intensity, int durationTicks, float frequency) {
      if (entityPatch != null && jointName != null && entityPatch.getArmature() != null) {
         Joint joint = entityPatch.getArmature().searchJointByName(jointName);
         addShakeAtJoint(entityPatch, joint, intensity, durationTicks, frequency);
      }
   }

   public static void addShakeAtJoint(LivingEntityPatch<?> entityPatch, Joint joint, float intensity, int durationTicks, float frequency) {
      Minecraft minecraft = Minecraft.m_91087_();
      if (minecraft.f_91074_ != null && entityPatch != null && joint != null && entityPatch.getArmature() != null) {
         float partialTicks = minecraft.m_91296_();
         Pose pose = entityPatch.getAnimator().getPose(partialTicks);
         Armature armature = entityPatch.getArmature();
         Vec3 entityPosition = ((LivingEntity)entityPatch.getOriginal()).m_20318_(partialTicks);
         OpenMatrix4f modelToWorld = OpenMatrix4f.createTranslation(
               (float)entityPosition.f_82479_, (float)entityPosition.f_82480_, (float)entityPosition.f_82481_
            )
            .rotateDeg(180.0F, Vec3f.Y_AXIS)
            .mulBack(entityPatch.getModelMatrix(partialTicks));
         OpenMatrix4f jointToWorld = armature.getBoundTransformFor(pose, joint).mulFront(modelToWorld);
         addShake(OpenMatrix4f.transform(jointToWorld, Vec3.f_82478_), intensity, durationTicks, frequency);
      }
   }

   public static synchronized Vec3 getAccumulatedOffset(Camera camera, float partialTicks) {
      Vec3 totalOffset = Vec3.f_82478_;
      if (ACTIVE_SHAKES.isEmpty()) {
         return totalOffset;
      } else {
         for (CameraShakeInstance shake : ACTIVE_SHAKES) {
            totalOffset = totalOffset.m_82549_(shake.getOffset(camera, partialTicks));
         }

         double maxOffsetLimit = 1.2;
         if (totalOffset.m_82556_() > maxOffsetLimit * maxOffsetLimit) {
            totalOffset = totalOffset.m_82541_().m_82490_(maxOffsetLimit);
         }

         return totalOffset;
      }
   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL
   )
   public static void onViewportComputeCameraAngles(ComputeCameraAngles event) {
      Camera camera = event.getCamera();
      Vec3 shakeOffset = getAccumulatedOffset(camera, (float)event.getPartialTick());
      if (shakeOffset != Vec3.f_82478_) {
         ((CameraAccessor)camera).setPositionRaw(camera.m_90583_().m_82549_(shakeOffset));
      }
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         synchronized (ACTIVE_SHAKES) {
            ACTIVE_SHAKES.removeIf(CameraShakeInstance::tick);
         }
      }
   }

   public static synchronized List<CameraShakeInstance> getActiveShakes() {
      return new ArrayList<>(ACTIVE_SHAKES);
   }
}
