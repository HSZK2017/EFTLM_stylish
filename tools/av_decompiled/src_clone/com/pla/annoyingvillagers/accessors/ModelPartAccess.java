package com.pla.annoyingvillagers.accessors;

public interface ModelPartAccess {
   float getXScale();

   float getYScale();

   float getZScale();

   void setXScale(float var1);

   void setYScale(float var1);

   void setZScale(float var1);

   default void setRenderScale(float x, float y, float z) {
      this.setXScale(x);
      this.setYScale(y);
      this.setZScale(z);
   }
}
