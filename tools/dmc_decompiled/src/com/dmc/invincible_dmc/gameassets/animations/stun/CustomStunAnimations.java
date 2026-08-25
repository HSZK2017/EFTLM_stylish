package com.dmc.invincible_dmc.gameassets.animations.stun;

import com.dmc.invincible_dmc.api.animation.types.DmcStunAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.utils.yamato.TeleportGroundUtils;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.PlaybackSpeedModifier;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.LongHitAnimation;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.gameasset.Armatures;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.MOD
)
public class CustomStunAnimations {
   public static AnimationAccessor<DmcStunAnimation> HIT_UP_0;
   public static AnimationAccessor<DmcStunAnimation> HIT_UP_1;
   public static AnimationAccessor<DmcStunAnimation> HIT_UP_2;
   public static AnimationAccessor<DmcStunAnimation> HIT_UP_3;
   public static AnimationAccessor<DmcStunAnimation> HIT_UP_4;
   public static AnimationAccessor<DmcStunAnimation> HIT_FROM_LEFT;
   public static AnimationAccessor<DmcStunAnimation> HIT_FROM_LEFT_AIR;
   public static AnimationAccessor<DmcStunAnimation> HIT_FROM_RIGHT;
   public static AnimationAccessor<DmcStunAnimation> HIT_FROM_RIGHT_AIR;
   public static AnimationAccessor<DmcStunAnimation> HIT_KNOCKDOWN;
   public static AnimationAccessor<DmcStunAnimation> HIT_KNOCKDOWN_AIR;
   public static AnimationAccessor<DmcStunAnimation> HIT_KNOCK_BACK_CLOSE;
   public static AnimationAccessor<DmcStunAnimation> HIT_KNOCK_BACK;
   public static AnimationAccessor<DmcStunAnimation> HIT_KNOCK_BACK_FAR;
   public static AnimationAccessor<DmcStunAnimation> HIT_KNOCK_BACK_AIR_CLOSE;
   public static AnimationAccessor<DmcStunAnimation> HIT_KNOCK_BACK_AIR;
   public static AnimationAccessor<DmcStunAnimation> HIT_KNOCK_BACK_AIR_FAR;
   public static AnimationAccessor<DmcStunAnimation> HIT_DOWN_BOUNCE_0;
   public static AnimationAccessor<DmcStunAnimation> HIT_DOWN_BOUNCE_1;
   public static AnimationAccessor<DmcStunAnimation> HIT_DOWN_BOUNCE_2;
   public static AnimationAccessor<DmcStunAnimation> HIT_DOWN_BOUNCE_3;
   public static AnimationAccessor<DmcStunAnimation> HIT_BLOW_BACK_0;
   public static AnimationAccessor<DmcStunAnimation> HIT_BLOW_BACK_1;
   public static AnimationAccessor<LongHitAnimation> HIT_AWAITING_EXECUTION;
   public static AnimationAccessor<LongHitAnimation> HIT_EXECUTED;
   public static AnimationAccessor<DmcStunAnimation> HIT_EXECUTED_BEGIN;
   public static AnimationAccessor<DmcStunAnimation> HIT_EXECUTED_END;
   private static final String pre_fix_stun = "biped/stun/hit_";

   public static void build(AnimationBuilder animationBuilder) {
      HIT_UP_0 = animationBuilder.nextAccessor(
         "biped/stun/hit_up_0",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.01F, accessor, Armatures.BIPED)
               .setResourceLocation("invincible_dmc", "biped/stun/hit_up_1")
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.1F, 1.0F}))
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(0.7F, 0.25F))
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                  if (elapsedTime >= 1.0F && elapsedTime < 1.1F) {
                     float dpx = (float)((LivingEntity)entitypatch.getOriginal()).m_20185_();
                     float dpy = (float)((LivingEntity)entitypatch.getOriginal()).m_20186_();
                     float dpz = (float)((LivingEntity)entitypatch.getOriginal()).m_20189_();
                     BlockState block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)dpy, (double)dpz));

                     while ((block.m_60734_() instanceof BushBlock || block.m_60795_()) && !block.m_60713_(Blocks.f_50626_)) {
                        block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)(--dpy), (double)dpz));
                     }

                     float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).m_20186_() - (double)dpy) - 1.0, 0.0);
                     return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
                  } else {
                     return speed;
                  }
               })
      );
      HIT_UP_1 = animationBuilder.nextAccessor(
         "biped/stun/hit_up_1",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.01F, accessor, Armatures.BIPED)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.1F, 1.0F}))
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(0.7F, 0.5F))
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                  if (elapsedTime >= 1.0F && elapsedTime < 1.1F) {
                     float dpx = (float)((LivingEntity)entitypatch.getOriginal()).m_20185_();
                     float dpy = (float)((LivingEntity)entitypatch.getOriginal()).m_20186_();
                     float dpz = (float)((LivingEntity)entitypatch.getOriginal()).m_20189_();
                     BlockState block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)dpy, (double)dpz));

                     while ((block.m_60734_() instanceof BushBlock || block.m_60795_()) && !block.m_60713_(Blocks.f_50626_)) {
                        block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)(--dpy), (double)dpz));
                     }

                     float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).m_20186_() - (double)dpy) - 1.0, 0.0);
                     return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
                  } else {
                     return speed;
                  }
               })
      );
      HIT_UP_2 = animationBuilder.nextAccessor(
         "biped/stun/hit_up_2",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.01F, accessor, Armatures.BIPED)
               .setResourceLocation("invincible_dmc", "biped/stun/hit_up_1")
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.1F, 1.0F}))
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(0.7F, 0.75F))
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                  if (elapsedTime >= 1.0F && elapsedTime < 1.1F) {
                     float dpx = (float)((LivingEntity)entitypatch.getOriginal()).m_20185_();
                     float dpy = (float)((LivingEntity)entitypatch.getOriginal()).m_20186_();
                     float dpz = (float)((LivingEntity)entitypatch.getOriginal()).m_20189_();
                     BlockState block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)dpy, (double)dpz));

                     while ((block.m_60734_() instanceof BushBlock || block.m_60795_()) && !block.m_60713_(Blocks.f_50626_)) {
                        block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)(--dpy), (double)dpz));
                     }

                     float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).m_20186_() - (double)dpy) - 1.0, 0.0);
                     return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
                  } else {
                     return speed;
                  }
               })
      );
      HIT_UP_3 = animationBuilder.nextAccessor(
         "biped/stun/hit_up_3",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.01F, accessor, Armatures.BIPED)
               .setResourceLocation("invincible_dmc", "biped/stun/hit_up_1")
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.1F, 1.0F}))
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(0.7F))
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                  if (elapsedTime >= 1.0F && elapsedTime < 1.1F) {
                     float dpx = (float)((LivingEntity)entitypatch.getOriginal()).m_20185_();
                     float dpy = (float)((LivingEntity)entitypatch.getOriginal()).m_20186_();
                     float dpz = (float)((LivingEntity)entitypatch.getOriginal()).m_20189_();
                     BlockState block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)dpy, (double)dpz));

                     while ((block.m_60734_() instanceof BushBlock || block.m_60795_()) && !block.m_60713_(Blocks.f_50626_)) {
                        block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)(--dpy), (double)dpz));
                     }

                     float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).m_20186_() - (double)dpy) - 1.0, 0.0);
                     return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
                  } else {
                     return speed;
                  }
               })
      );
      HIT_UP_4 = animationBuilder.nextAccessor(
         "biped/stun/hit_up_4",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.01F, accessor, Armatures.BIPED)
               .setResourceLocation("invincible_dmc", "biped/stun/hit_up_1")
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.1F, 1.0F}))
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(0.7F, 1.5F))
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                  if (elapsedTime >= 1.0F && elapsedTime < 1.1F) {
                     float dpx = (float)((LivingEntity)entitypatch.getOriginal()).m_20185_();
                     float dpy = (float)((LivingEntity)entitypatch.getOriginal()).m_20186_();
                     float dpz = (float)((LivingEntity)entitypatch.getOriginal()).m_20189_();
                     BlockState block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)dpy, (double)dpz));

                     while ((block.m_60734_() instanceof BushBlock || block.m_60795_()) && !block.m_60713_(Blocks.f_50626_)) {
                        block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)(--dpy), (double)dpz));
                     }

                     float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).m_20186_() - (double)dpy) - 1.0, 0.0);
                     return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
                  } else {
                     return speed;
                  }
               })
      );
      HIT_KNOCK_BACK_CLOSE = animationBuilder.nextAccessor(
         "biped/stun/hit_knockback_close",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.01F, accessor, Armatures.BIPED)
               .setResourceLocation("invincible_dmc", "biped/stun/hit_knockback")
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(0.5F, 1.0F))
      );
      HIT_KNOCK_BACK = animationBuilder.nextAccessor("biped/stun/hit_knockback", accessor -> new DmcStunAnimation(0.01F, accessor, Armatures.BIPED));
      HIT_KNOCK_BACK_FAR = animationBuilder.nextAccessor(
         "biped/stun/hit_knockback_far",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.01F, accessor, Armatures.BIPED)
               .setResourceLocation("invincible_dmc", "biped/stun/hit_knockback")
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(1.5F, 1.0F))
      );
      HIT_KNOCK_BACK_AIR_CLOSE = animationBuilder.nextAccessor(
         "biped/stun/hit_knockback_air_close",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.01F, accessor, Armatures.BIPED)
               .setResourceLocation("invincible_dmc", "biped/stun/hit_knockback")
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.16666667F}))
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(0.5F, 1.0F))
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                  if (elapsedTime >= 0.18333334F && elapsedTime < 0.25F) {
                     float dpx = (float)((LivingEntity)entitypatch.getOriginal()).m_20185_();
                     float dpy = (float)((LivingEntity)entitypatch.getOriginal()).m_20186_();
                     float dpz = (float)((LivingEntity)entitypatch.getOriginal()).m_20189_();
                     BlockState block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)dpy, (double)dpz));

                     while ((block.m_60734_() instanceof BushBlock || block.m_60795_()) && !block.m_60713_(Blocks.f_50626_)) {
                        block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)(--dpy), (double)dpz));
                     }

                     float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).m_20186_() - (double)dpy) - 1.0, 0.0);
                     return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
                  } else {
                     return speed;
                  }
               })
      );
      HIT_KNOCK_BACK_AIR = animationBuilder.nextAccessor(
         "biped/stun/hit_knockback_air",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.01F, accessor, Armatures.BIPED)
               .setResourceLocation("invincible_dmc", "biped/stun/hit_knockback")
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.16666667F}))
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                  if (elapsedTime >= 0.18333334F && elapsedTime < 0.25F) {
                     float dpx = (float)((LivingEntity)entitypatch.getOriginal()).m_20185_();
                     float dpy = (float)((LivingEntity)entitypatch.getOriginal()).m_20186_();
                     float dpz = (float)((LivingEntity)entitypatch.getOriginal()).m_20189_();
                     BlockState block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)dpy, (double)dpz));

                     while ((block.m_60734_() instanceof BushBlock || block.m_60795_()) && !block.m_60713_(Blocks.f_50626_)) {
                        block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)(--dpy), (double)dpz));
                     }

                     float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).m_20186_() - (double)dpy) - 1.0, 0.0);
                     return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
                  } else {
                     return speed;
                  }
               })
      );
      HIT_KNOCK_BACK_AIR_FAR = animationBuilder.nextAccessor(
         "biped/stun/hit_knockback_air_far",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.01F, accessor, Armatures.BIPED)
               .setResourceLocation("invincible_dmc", "biped/stun/hit_knockback")
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.16666667F}))
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(1.5F, 1.0F))
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                  if (elapsedTime >= 0.18333334F && elapsedTime < 0.25F) {
                     float dpx = (float)((LivingEntity)entitypatch.getOriginal()).m_20185_();
                     float dpy = (float)((LivingEntity)entitypatch.getOriginal()).m_20186_();
                     float dpz = (float)((LivingEntity)entitypatch.getOriginal()).m_20189_();
                     BlockState block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)dpy, (double)dpz));

                     while ((block.m_60734_() instanceof BushBlock || block.m_60795_()) && !block.m_60713_(Blocks.f_50626_)) {
                        block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)(--dpy), (double)dpz));
                     }

                     float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).m_20186_() - (double)dpy) - 1.0, 0.0);
                     return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
                  } else {
                     return speed;
                  }
               })
      );
      HIT_BLOW_BACK_0 = animationBuilder.nextAccessor(
         "biped/stun/hit_blow_back_0",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.05F, accessor, Armatures.BIPED)
               .setResourceLocation("invincible_dmc", "biped/stun/hit_blow_back_1")
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.8333333F}))
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(0.3F, 0.5F))
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                  if (elapsedTime >= 0.36666667F && elapsedTime < 0.41666666F) {
                     float dpx = (float)((LivingEntity)entitypatch.getOriginal()).m_20185_();
                     float dpy = (float)((LivingEntity)entitypatch.getOriginal()).m_20186_();
                     float dpz = (float)((LivingEntity)entitypatch.getOriginal()).m_20189_();
                     BlockState block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)dpy, (double)dpz));

                     while ((block.m_60734_() instanceof BushBlock || block.m_60795_()) && !block.m_60713_(Blocks.f_50626_)) {
                        block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)(--dpy), (double)dpz));
                     }

                     float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).m_20186_() - (double)dpy) - 1.0, 0.0);
                     return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
                  } else {
                     return speed * 0.4F;
                  }
               })
      );
      HIT_BLOW_BACK_1 = animationBuilder.nextAccessor(
         "biped/stun/hit_blow_back_1",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.05F, accessor, Armatures.BIPED)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.8333333F}))
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(0.75F, 1.35F))
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                  if (elapsedTime >= 0.36666667F && elapsedTime < 0.41666666F) {
                     float dpx = (float)((LivingEntity)entitypatch.getOriginal()).m_20185_();
                     float dpy = (float)((LivingEntity)entitypatch.getOriginal()).m_20186_();
                     float dpz = (float)((LivingEntity)entitypatch.getOriginal()).m_20189_();
                     BlockState block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)dpy, (double)dpz));

                     while ((block.m_60734_() instanceof BushBlock || block.m_60795_()) && !block.m_60713_(Blocks.f_50626_)) {
                        block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)(--dpy), (double)dpz));
                     }

                     float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).m_20186_() - (double)dpy) - 1.0, 0.0);
                     return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
                  } else {
                     return speed * 0.4F;
                  }
               })
      );
      HIT_KNOCKDOWN_AIR = animationBuilder.nextAccessor(
         "biped/stun/hit_knockdown_air",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.01F, accessor, Armatures.BIPED)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.16666667F}))
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(self, entitypatch, speed, prevElapsedTime, elapsedTime) -> {
                  if (elapsedTime >= 0.11666667F && elapsedTime < 0.15F) {
                     float dpx = (float)((LivingEntity)entitypatch.getOriginal()).m_20185_();
                     float dpy = (float)((LivingEntity)entitypatch.getOriginal()).m_20186_();
                     float dpz = (float)((LivingEntity)entitypatch.getOriginal()).m_20189_();
                     BlockState block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)dpy, (double)dpz));

                     while ((block.m_60734_() instanceof BushBlock || block.m_60795_()) && !block.m_60713_(Blocks.f_50626_)) {
                        block = ((LivingEntity)entitypatch.getOriginal()).m_9236_().m_8055_(new MutableBlockPos((double)dpx, (double)(--dpy), (double)dpz));
                     }

                     float distanceToGround = (float)Math.max(Math.abs(((LivingEntity)entitypatch.getOriginal()).m_20186_() - (double)dpy) - 1.0, 0.0);
                     return 1.0F - (1.0F / (-distanceToGround - 1.0F) + 1.0F);
                  } else {
                     return speed;
                  }
               })
      );
      HIT_DOWN_BOUNCE_0 = animationBuilder.nextAccessor(
         "biped/stun/hit_down_bounce_0",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.01F, accessor, Armatures.BIPED)
               .setResourceLocation("invincible_dmc", "biped/stun/hit_down_bounce_1")
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.3333334F}))
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(1.0F, 0.25F))
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 0.85F)
               .addEvents(new AnimationEvent[]{TeleportGroundUtils.create(4, -0.1F)})
      );
      HIT_DOWN_BOUNCE_1 = animationBuilder.nextAccessor(
         "biped/stun/hit_down_bounce_1",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.01F, accessor, Armatures.BIPED)
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.3333334F}))
               .addEvents(new AnimationEvent[]{TeleportGroundUtils.create(4, -0.1F)})
      );
      HIT_DOWN_BOUNCE_2 = animationBuilder.nextAccessor(
         "biped/stun/hit_down_bounce_2",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.01F, accessor, Armatures.BIPED)
               .setResourceLocation("invincible_dmc", "biped/stun/hit_down_bounce_1")
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.3333334F}))
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(1.0F, 1.3F))
               .addProperty(StaticAnimationProperty.PLAY_SPEED_MODIFIER, (PlaybackSpeedModifier)(dynamicAnimation, livingEntityPatch, v, v1, v2) -> 0.85F)
               .addEvents(new AnimationEvent[]{TeleportGroundUtils.create(4, -0.1F)})
      );
      HIT_DOWN_BOUNCE_3 = animationBuilder.nextAccessor(
         "biped/stun/hit_down_bounce_3",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.01F, accessor, Armatures.BIPED)
               .setResourceLocation("invincible_dmc", "biped/stun/hit_down_bounce_1")
               .addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 1.3333334F}))
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(1.0F, 1.6F))
               .addEvents(new AnimationEvent[]{TeleportGroundUtils.create(4, -0.1F)})
      );
      HIT_KNOCKDOWN = animationBuilder.nextAccessor(
         "biped/stun/hit_knockdown",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.01F, accessor, Armatures.BIPED)
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.16666667F}))
      );
      HIT_FROM_RIGHT = animationBuilder.nextAccessor("biped/stun/hit_from_right", accessor -> new DmcStunAnimation(0.01F, accessor, Armatures.BIPED));
      HIT_FROM_LEFT = animationBuilder.nextAccessor("biped/stun/hit_from_left", accessor -> new DmcStunAnimation(0.01F, accessor, Armatures.BIPED));
      HIT_AWAITING_EXECUTION = animationBuilder.nextAccessor(
         "biped/stun/hit_awaiting_execution", accessor -> new LongHitAnimation(0.15F, accessor, Armatures.BIPED)
      );
      HIT_EXECUTED = animationBuilder.nextAccessor("biped/stun/hit_executed", accessor -> new LongHitAnimation(0.05F, accessor, Armatures.BIPED));
      HIT_EXECUTED_BEGIN = animationBuilder.nextAccessor("biped/stun/hit_executed_begin", accessor -> new DmcStunAnimation(0.05F, accessor, Armatures.BIPED));
      HIT_EXECUTED_END = animationBuilder.nextAccessor("biped/stun/hit_executed_end", accessor -> new DmcStunAnimation(0.05F, accessor, Armatures.BIPED));
      HIT_FROM_LEFT = animationBuilder.nextAccessor("biped/stun/hit_from_left", accessor -> new DmcStunAnimation(0.01F, accessor, Armatures.BIPED));
      HIT_FROM_RIGHT_AIR = animationBuilder.nextAccessor(
         "biped/stun/hit_from_right_air",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.01F, accessor, Armatures.BIPED)
               .setResourceLocation("invincible_dmc", "biped/stun/hit_from_right")
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(0.25F))
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.85F}))
      );
      HIT_FROM_LEFT_AIR = animationBuilder.nextAccessor(
         "biped/stun/hit_from_left_air",
         accessor -> (DmcStunAnimation)new DmcStunAnimation(0.01F, accessor, Armatures.BIPED)
               .setResourceLocation("invincible_dmc", "biped/stun/hit_from_left")
               .addProperty(ActionAnimationProperty.COORD_GET, YamatoAttackAnimation.modelCoord(0.25F))
               .addProperty(ActionAnimationProperty.NO_GRAVITY_TIME, TimePairList.create(new float[]{0.0F, 0.85F}))
      );
   }
}
