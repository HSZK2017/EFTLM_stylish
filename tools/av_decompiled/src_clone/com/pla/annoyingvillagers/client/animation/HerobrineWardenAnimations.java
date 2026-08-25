package com.pla.annoyingvillagers.client.animation;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.animation.AnimationChannel.Interpolations;
import net.minecraft.client.animation.AnimationChannel.Targets;
import net.minecraft.client.animation.AnimationDefinition.Builder;

public class HerobrineWardenAnimations {
   public static final AnimationDefinition HEROBRINE_WARDEN_IDLE = Builder.m_232275_(2.0F)
      .m_232274_()
      .m_232279_(
         "left_ribs_extra",
         new AnimationChannel(
            Targets.f_232251_,
            new Keyframe[]{
               new Keyframe(0.0F, KeyframeAnimations.m_253186_(0.0F, 0.0F, 0.0F), Interpolations.f_232230_),
               new Keyframe(1.0F, KeyframeAnimations.m_253186_(0.0F, -5.0F, 0.0F), Interpolations.f_232230_),
               new Keyframe(2.0F, KeyframeAnimations.m_253186_(0.0F, 0.0F, 0.0F), Interpolations.f_232230_)
            }
         )
      )
      .m_232279_(
         "right_ribs_extra",
         new AnimationChannel(
            Targets.f_232251_,
            new Keyframe[]{
               new Keyframe(0.0F, KeyframeAnimations.m_253186_(0.0F, 0.0F, 0.0F), Interpolations.f_232229_),
               new Keyframe(1.0F, KeyframeAnimations.m_253186_(0.0F, 5.0F, 0.0F), Interpolations.f_232229_),
               new Keyframe(2.0F, KeyframeAnimations.m_253186_(0.0F, 0.0F, 0.0F), Interpolations.f_232229_)
            }
         )
      )
      .m_232282_();
   public static final AnimationDefinition HEROBRINE_WARDEN_EATING = Builder.m_232275_(2.0F)
      .m_232274_()
      .m_232279_(
         "left_ribs_extra",
         new AnimationChannel(
            Targets.f_232251_,
            new Keyframe[]{
               new Keyframe(0.0F, KeyframeAnimations.m_253186_(0.0F, -15.0F, 0.0F), Interpolations.f_232230_),
               new Keyframe(1.0F, KeyframeAnimations.m_253186_(0.0F, -15.0F, 0.0F), Interpolations.f_232230_),
               new Keyframe(2.0F, KeyframeAnimations.m_253186_(0.0F, -15.0F, 0.0F), Interpolations.f_232230_)
            }
         )
      )
      .m_232279_(
         "right_ribs_extra",
         new AnimationChannel(
            Targets.f_232251_,
            new Keyframe[]{
               new Keyframe(0.0F, KeyframeAnimations.m_253186_(0.0F, 15.0F, 0.0F), Interpolations.f_232229_),
               new Keyframe(1.0F, KeyframeAnimations.m_253186_(0.0F, 15.0F, 0.0F), Interpolations.f_232229_),
               new Keyframe(2.0F, KeyframeAnimations.m_253186_(0.0F, 15.0F, 0.0F), Interpolations.f_232229_)
            }
         )
      )
      .m_232282_();
}
