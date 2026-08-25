package com.pla.annoyingvillagers.client.engine;

import com.pla.annoyingvillagers.entity.HerobrineDragonEntity;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;

public class MountCameraManager {
   private static CameraType previousPerspective = CameraType.FIRST_PERSON;

   public static void onDragonMount() {
      previousPerspective = Minecraft.m_91087_().f_91066_.m_92176_();
      Minecraft.m_91087_().f_91066_.m_92157_(CameraType.THIRD_PERSON_BACK);
   }

   public static void onDragonDismount() {
      Minecraft.m_91087_().f_91066_.m_92157_(previousPerspective);
   }

   public static void setMountCameraAngles(Camera camera) {
      if (Minecraft.m_91087_().f_91074_.m_20202_() instanceof HerobrineDragonEntity && !Minecraft.m_91087_().f_91066_.m_92176_().m_90612_()) {
         camera.m_90568_(0.0, 4.0, 0.0);
         camera.m_90568_(-camera.m_90566_(6.0), 0.0, 0.0);
      }
   }
}
