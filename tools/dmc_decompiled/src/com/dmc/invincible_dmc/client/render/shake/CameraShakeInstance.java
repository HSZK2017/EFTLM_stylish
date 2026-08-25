package com.dmc.invincible_dmc.client.render.shake;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class CameraShakeInstance {
   private Vec3 targetWorldPos;
   private float intensity;
   private float frequency;
   private int maxTicks;
   private int elapsedTicks = 0;

   public CameraShakeInstance(Vec3 targetWorldPos, float intensity, int durationTicks, float frequency) {
      this.targetWorldPos = targetWorldPos;
      this.intensity = intensity;
      this.maxTicks = Math.max(1, durationTicks);
      this.frequency = frequency;
   }

   public void merge(Vec3 newTargetPos, float newIntensity, int newDuration, float newFrequency, Vec3 cameraPos) {
      double distCurrent = this.targetWorldPos.m_82554_(cameraPos);
      double distNew = newTargetPos.m_82554_(cameraPos);
      if (distNew < distCurrent) {
         this.targetWorldPos = newTargetPos;
      }

      float maxI = Math.max(this.intensity, newIntensity);
      float minI = Math.min(this.intensity, newIntensity);
      this.intensity = Math.min(maxI + minI * 0.25F, 1.5F);
      this.maxTicks = Math.max(this.maxTicks, newDuration);
      this.frequency = Math.max(this.frequency, newFrequency);
      this.elapsedTicks = 0;
   }

   public boolean tick() {
      this.elapsedTicks++;
      return this.elapsedTicks >= this.maxTicks;
   }

   public int getElapsedTicks() {
      return this.elapsedTicks;
   }

   public Vec3 getOffset(Camera camera, float partialTicks) {
      double distance = camera.m_90583_().m_82554_(this.targetWorldPos);
      if (!(distance > 24.0) && !(distance < 0.1)) {
         float distFade = (float)(1.0 - distance / 24.0);
         distFade *= distFade;
         float progress = ((float)this.elapsedTicks + partialTicks) / (float)this.maxTicks;
         if (progress >= 1.0F) {
            return Vec3.f_82478_;
         } else {
            float envelope = (float)Math.exp((double)(-4.0F * progress));
            float wave = (float)Math.sin((double)(progress * this.frequency) * Math.PI * 2.0);
            float currentStrength = this.intensity * envelope * wave * distFade;
            Vec3 relativePos = this.targetWorldPos.m_82546_(camera.m_90583_());
            Vector3f toTargetDir = new Vector3f((float)relativePos.f_82479_, (float)relativePos.f_82480_, (float)relativePos.f_82481_).normalize();
            Vector3f up = camera.m_253028_();
            Vector3f right = new Vector3f(camera.m_252775_()).negate();
            Vector3f look = camera.m_253058_();
            float screenX = toTargetDir.dot(right);
            float screenY = toTargetDir.dot(up);
            float screenZ = toTargetDir.dot(look);
            double offsetX = (double)(-right.x * screenX * currentStrength - up.x * screenY * currentStrength);
            double offsetY = (double)(-right.y * screenX * currentStrength - up.y * screenY * currentStrength);
            double offsetZ = (double)(-right.z * screenX * currentStrength - up.z * screenY * currentStrength);
            if (screenZ > 0.1F) {
               offsetX -= (double)(look.x * screenZ * currentStrength * 0.5F);
               offsetY -= (double)(look.y * screenZ * currentStrength * 0.5F);
               offsetZ -= (double)(look.z * screenZ * currentStrength * 0.5F);
            }

            return new Vec3(offsetX, offsetY, offsetZ);
         }
      } else {
         return Vec3.f_82478_;
      }
   }

   public Vec3 getTargetWorldPos() {
      return this.targetWorldPos;
   }
}
