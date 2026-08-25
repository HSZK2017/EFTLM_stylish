package com.dmc.invincible_dmc.mixin.epicfight;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.TransformSheet.InterpolationInfo;

@Mixin(
   value = {AnimationClip.class},
   remap = false
)
public abstract class AnimationClipMixin {
   @Shadow
   protected Map<String, TransformSheet> jointTransforms;
   @Shadow
   protected float[] bakedTimes;
   @Shadow
   protected float clipTime;

   @Inject(
      method = {"bakeKeyframes"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void invincible_dmc$bakeKeyframes(CallbackInfo ci) {
      ci.cancel();
      synchronized (this) {
         Set<Float> timestamps = new HashSet<>();
         this.jointTransforms.values().forEach(transformSheet -> transformSheet.forEach((i, keyframe) -> timestamps.add(keyframe.time())));
         float[] timestampsArr = new float[timestamps.size()];
         MutableInt mi = new MutableInt(0);
         timestamps.stream().sorted().toList().forEach(f -> timestampsArr[mi.getAndAdd(1)] = f);
         Map<String, TransformSheet> baked = new LinkedHashMap<>();
         this.jointTransforms.forEach((jointName, transformSheet) -> baked.put(jointName, transformSheet.createInterpolated(timestampsArr)));
         this.jointTransforms = Collections.synchronizedMap(baked);
         this.bakedTimes = timestampsArr;
      }
   }

   @Inject(
      method = {"getPoseInTime"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void invincible_dmc$getPoseInTime(float time, CallbackInfoReturnable<Pose> cir) {
      cir.cancel();
      Pose pose = new Pose();
      if (time < 0.0F) {
         time += this.clipTime;
      }

      if (this.bakedTimes != null && this.bakedTimes.length > 0) {
         InterpolationInfo iInfo = this.invincible_DMC$getInterpolationInfo(time);
         Map<String, TransformSheet> jt = this.jointTransforms;
         synchronized (jt) {
            for (String jointName : jt.keySet()) {
               pose.putJointData(jointName, jt.get(jointName).getInterpolatedTransform(iInfo));
            }
         }
      } else {
         Map<String, TransformSheet> jt = this.jointTransforms;
         synchronized (jt) {
            for (String jointName : jt.keySet()) {
               pose.putJointData(jointName, jt.get(jointName).getInterpolatedTransform(time));
            }
         }
      }

      cir.setReturnValue(pose);
   }

   @Unique
   @NotNull
   private InterpolationInfo invincible_DMC$getInterpolationInfo(float time) {
      if (Float.isNaN(time)) {
         time = 0.0F;
      }

      int begin = 0;
      int end = this.bakedTimes.length - 1;

      while (end - begin > 1) {
         int i = begin + (end - begin) / 2;
         if (this.bakedTimes[i] <= time && this.bakedTimes[i + 1] > time) {
            begin = i;
            end = i + 1;
            break;
         }

         if (this.bakedTimes[i] > time) {
            end = i;
         } else if (this.bakedTimes[i + 1] <= time) {
            begin = i;
         } else {
            begin = i;
         }
      }

      float denom = this.bakedTimes[end] - this.bakedTimes[begin];
      float delta = denom != 0.0F ? Mth.m_14036_((time - this.bakedTimes[begin]) / denom, 0.0F, 1.0F) : 0.0F;
      return new InterpolationInfo(begin, end, delta);
   }
}
