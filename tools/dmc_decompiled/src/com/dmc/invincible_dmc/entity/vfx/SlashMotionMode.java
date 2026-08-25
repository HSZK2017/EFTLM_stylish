package com.dmc.invincible_dmc.entity.vfx;

public enum SlashMotionMode {
   NORMAL {
      @Override
      public float ease(float t) {
         return 1.0F - (float)Math.pow((double)(1.0F - t), 4.0);
      }

      @Override
      public float alpha(float t) {
         return t < 0.3F ? 1.0F : 1.0F - SlashMotionMode.sq((t - 0.3F) / 0.7F);
      }

      @Override
      public int defaultLifetime() {
         return 10;
      }

      @Override
      public float sweepDegrees() {
         return 180.0F;
      }
   },
   FAST_BURST {
      @Override
      public float ease(float t) {
         return 1.0F - (float)Math.pow((double)(1.0F - t), 2.5);
      }

      @Override
      public float alpha(float t) {
         if (t < 0.38F) {
            return 0.0F;
         } else {
            return t < 0.85F ? 1.0F : 1.0F - SlashMotionMode.sq((t - 0.85F) / 0.15F);
         }
      }

      @Override
      public int defaultLifetime() {
         return 11;
      }

      @Override
      public float sweepDegrees() {
         return 180.0F;
      }

      @Override
      public float xzScaleStart() {
         return 0.88F;
      }

      @Override
      public float xzScaleEnd() {
         return 1.1F;
      }

      @Override
      public float yScaleStart() {
         return 0.9F;
      }

      @Override
      public float yScaleEnd() {
         return 0.35F;
      }

      @Override
      public float meshSweepAngle() {
         return 160.0F;
      }
   },
   HEAVY {
      @Override
      public float ease(float t) {
         float ta = (float)Math.pow((double)t, 2.0);
         float tb = (float)Math.pow((double)(1.0F - t), 4.0);
         return ta / (ta + tb);
      }

      @Override
      public float alpha(float t) {
         return t < 0.55F ? 1.0F : 1.0F - (float)Math.pow((double)((t - 0.55F) / 0.45F), 3.0);
      }

      @Override
      public int defaultLifetime() {
         return 70;
      }

      @Override
      public float sweepDegrees() {
         return 210.0F;
      }

      @Override
      public float xzScaleStart() {
         return 0.85F;
      }

      @Override
      public float xzScaleEnd() {
         return 1.45F;
      }

      @Override
      public float yScaleStart() {
         return 1.3F;
      }

      @Override
      public float yScaleEnd() {
         return 0.22F;
      }
   };

   private static final SlashMotionMode[] VALUES = values();

   public abstract float ease(float var1);

   public abstract float alpha(float var1);

   public abstract int defaultLifetime();

   public abstract float sweepDegrees();

   public float xzScaleStart() {
      return 0.9F;
   }

   public float xzScaleEnd() {
      return 1.15F;
   }

   public float yScaleStart() {
      return 1.0F;
   }

   public float yScaleEnd() {
      return 0.4F;
   }

   public float meshSweepAngle() {
      return 210.0F;
   }

   public static SlashMotionMode fromOrdinal(int ordinal) {
      return ordinal >= 0 && ordinal < VALUES.length ? VALUES[ordinal] : NORMAL;
   }

   private static float sq(float x) {
      return x * x;
   }
}
