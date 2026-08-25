package com.dmc.invincible_dmc.client.domain;

import com.guhao.vix.client.screeneffect.EasingFunctions.Type;
import org.joml.Vector3f;

public class DemonicDomainEffect {
   private float domainRadius = 12.0F;
   private float scanSpeed = 1.0F;
   private float scanFrequency = 2.2F;
   private float sweepSpeed = 0.45F;
   private float tintAlpha = 0.58F;
   private final Vector3f tintColor = new Vector3f(0.025F, 0.08F, 0.38F);
   private final Vector3f glowColor = new Vector3f(0.28F, 0.52F, 1.0F);
   private final Vector3f domainCenter = new Vector3f(0.0F, 0.0F, 0.0F);
   private Type easingType = Type.EASE_OUT_CUBIC;

   public float getDomainRadius() {
      return this.domainRadius;
   }

   public void setDomainRadius(float v) {
      this.domainRadius = v;
   }

   public float getScanSpeed() {
      return this.scanSpeed;
   }

   public void setScanSpeed(float v) {
      this.scanSpeed = v;
   }

   public float getScanFrequency() {
      return this.scanFrequency;
   }

   public void setScanFrequency(float v) {
      this.scanFrequency = v;
   }

   public float getSweepSpeed() {
      return this.sweepSpeed;
   }

   public void setSweepSpeed(float v) {
      this.sweepSpeed = v;
   }

   public float getTintAlpha() {
      return this.tintAlpha;
   }

   public void setTintAlpha(float v) {
      this.tintAlpha = v;
   }

   public Vector3f getTintColor() {
      return this.tintColor;
   }

   public void setTintColor(float r, float g, float b) {
      this.tintColor.set(r, g, b);
   }

   public Vector3f getGlowColor() {
      return this.glowColor;
   }

   public void setGlowColor(float r, float g, float b) {
      this.glowColor.set(r, g, b);
   }

   public Vector3f getDomainCenter() {
      return this.domainCenter;
   }

   public void setDomainCenter(float x, float y, float z) {
      this.domainCenter.set(x, y, z);
   }

   public Type getEasingType() {
      return this.easingType;
   }

   public void setEasing(Type type) {
      this.easingType = type;
   }
}
