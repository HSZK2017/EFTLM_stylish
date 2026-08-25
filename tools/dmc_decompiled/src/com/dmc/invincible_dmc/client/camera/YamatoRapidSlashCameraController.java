package com.dmc.invincible_dmc.client.camera;

import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
import net.minecraftforge.client.event.ViewportEvent.ComputeCameraAngles;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.client.camera.EpicFightCameraAPI;
import yesman.epicfight.api.client.event.EpicFightClientHooks.Camera;
import yesman.epicfight.api.client.event.types.BuildCameraTransform.Post;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   modid = "invincible_dmc",
   value = {Dist.CLIENT}
)
public final class YamatoRapidSlashCameraController {
   private static final int EPIC_FIGHT_HOOK_PRIORITY = -4107;
   private static final float NORMAL_FOV_SCALE = 1.0F;
   private static final float RAPID_SLASH_FOV_SCALE = 1.2F;
   private static final float RAPID_SLASH_HEIGHT_OFFSET = 0.6F;
   private static final float RAPID_SLASH_PULLBACK_OFFSET = 3.0F;
   private static final float ENTER_SMOOTHING = 0.55F;
   private static final float EXIT_SMOOTHING = 0.28F;
   private static final float SNAP_EPSILON = 1.0E-4F;
   private static final double COLLISION_MARGIN = 0.08;
   private static float previousFovScale = 1.0F;
   private static float currentFovScale = 1.0F;
   private static float previousHeightOffset;
   private static float currentHeightOffset;
   private static float previousPullbackOffset;
   private static float currentPullbackOffset;
   private static boolean registered;

   private YamatoRapidSlashCameraController() {
   }

   public static void register() {
      if (!registered) {
         registered = true;
         Camera.BUILD_TRANSFORM_POST.registerEvent(YamatoRapidSlashCameraController::onEpicFightCameraPost, -4107);
      }
   }

   private static void onEpicFightCameraPost(Post event) {
      if (event.getCameraApi().isTPSMode()) {
         applyPositionOffset(event.getCamera(), event.getPartialTick());
      }
   }

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END && !Minecraft.m_91087_().m_91104_()) {
         boolean rapidSlashActive = isRapidSlashActive();
         float targetScale = rapidSlashActive ? 1.2F : 1.0F;
         float targetHeight = rapidSlashActive ? 0.6F : 0.0F;
         float targetPullback = rapidSlashActive ? 3.0F : 0.0F;
         previousFovScale = currentFovScale;
         previousHeightOffset = currentHeightOffset;
         previousPullbackOffset = currentPullbackOffset;
         float smoothing = targetScale > currentFovScale ? 0.55F : 0.28F;
         currentFovScale = Mth.m_14179_(smoothing, currentFovScale, targetScale);
         currentHeightOffset = Mth.m_14179_(smoothing, currentHeightOffset, targetHeight);
         currentPullbackOffset = Mth.m_14179_(smoothing, currentPullbackOffset, targetPullback);
         if (Math.abs(currentFovScale - targetScale) <= 1.0E-4F) {
            currentFovScale = targetScale;
         }

         if (Math.abs(currentHeightOffset - targetHeight) <= 1.0E-4F) {
            currentHeightOffset = targetHeight;
         }

         if (Math.abs(currentPullbackOffset - targetPullback) <= 1.0E-4F) {
            currentPullbackOffset = targetPullback;
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onComputeFov(ComputeFovModifierEvent event) {
      Minecraft minecraft = Minecraft.m_91087_();
      if (minecraft.f_91066_.m_92176_() == CameraType.THIRD_PERSON_BACK) {
         float partialTick = minecraft.m_91296_();
         float interpolatedScale = Mth.m_14179_(partialTick, previousFovScale, currentFovScale);
         event.setNewFovModifier(event.getNewFovModifier() * interpolatedScale);
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onComputeCameraAngles(ComputeCameraAngles event) {
      Minecraft minecraft = Minecraft.m_91087_();
      if (!EpicFightCameraAPI.getInstance().isTPSMode() && minecraft.f_91066_.m_92176_() == CameraType.THIRD_PERSON_BACK) {
         applyPositionOffset(event.getCamera(), (float)event.getPartialTick());
      }
   }

   private static void applyPositionOffset(net.minecraft.client.Camera camera, float partialTick) {
      Minecraft minecraft = Minecraft.m_91087_();
      if (minecraft.f_91066_.m_92176_() == CameraType.THIRD_PERSON_BACK) {
         float interpolatedHeight = Mth.m_14179_(partialTick, previousHeightOffset, currentHeightOffset);
         float interpolatedPullback = Mth.m_14179_(partialTick, previousPullbackOffset, currentPullbackOffset);
         if (!(Math.abs(interpolatedHeight) <= 1.0E-4F) || !(Math.abs(interpolatedPullback) <= 1.0E-4F)) {
            Vec3 horizontalLook = new Vec3((double)camera.m_253058_().x(), 0.0, (double)camera.m_253058_().z());
            if (!(horizontalLook.m_82556_() < 1.0E-6) && minecraft.f_91073_ != null) {
               Vec3 currentPosition = camera.m_90583_();
               Vec3 offset = horizontalLook.m_82541_().m_82490_((double)(-interpolatedPullback)).m_82520_(0.0, (double)interpolatedHeight, 0.0);
               Vec3 targetPosition = resolveCollision(currentPosition, currentPosition.m_82549_(offset), camera);
               camera.m_90584_(targetPosition.f_82479_, targetPosition.f_82480_, targetPosition.f_82481_);
            }
         }
      }
   }

   private static Vec3 resolveCollision(Vec3 start, Vec3 target, net.minecraft.client.Camera camera) {
      Vec3 offset = target.m_82546_(start);
      double distance = offset.m_82553_();
      if (distance < 1.0E-6) {
         return start;
      } else {
         BlockHitResult hitResult = Minecraft.m_91087_().f_91073_.m_45547_(new ClipContext(start, target, Block.VISUAL, Fluid.NONE, camera.m_90592_()));
         if (hitResult.m_6662_() == Type.MISS) {
            return target;
         } else {
            double safeDistance = Math.max(0.0, start.m_82554_(hitResult.m_82450_()) - 0.08);
            return start.m_82549_(offset.m_82490_(safeDistance / distance));
         }
      }
   }

   private static boolean isRapidSlashActive() {
      Minecraft minecraft = Minecraft.m_91087_();
      if (minecraft.f_91074_ != null && YamatoAnimations.YAMATO_RAPIDSLASH != null && YamatoAnimations.YAMATO_RAPIDSLASH_RE != null) {
         LocalPlayerPatch playerPatch = (LocalPlayerPatch)EpicFightCapabilities.getEntityPatch(minecraft.f_91074_, LocalPlayerPatch.class);
         if (playerPatch == null) {
            return false;
         } else {
            AnimationPlayer animationPlayer = DMCAnimationUtils.getMainPlayer(playerPatch);
            return animationPlayer != null
               && DMCAnimationUtils.isOneOfAccessor(
                  DMCAnimationUtils.getRealAnimationAccessor(animationPlayer), YamatoAnimations.YAMATO_RAPIDSLASH, YamatoAnimations.YAMATO_RAPIDSLASH_RE
               );
         }
      } else {
         return false;
      }
   }
}
