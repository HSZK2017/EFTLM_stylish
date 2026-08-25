package com.dmc.invincible_dmc.api.animation.types.yamato;

import com.dmc.invincible_dmc.entity.util.DMCDodgeLocationIndicator;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent.ImpactResult;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.E1;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.gameasset.Animations.ReusableSources;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;

public class YamatoDodgeAnimation extends ActionAnimation {
   public static final Function<DamageSource, ResultType> DODGEABLE_SOURCE_VALIDATOR = damagesource -> damagesource.m_7639_() != null
            && !damagesource.m_269533_(DamageTypeTags.f_268415_)
            && !damagesource.m_276093_(DamageTypes.f_268515_)
            && !damagesource.m_269533_(DamageTypeTags.f_268490_)
            && !damagesource.m_269533_(DamageTypeTags.f_268738_)
            && !damagesource.m_269533_(EpicFightDamageTypeTags.BYPASS_DODGE)
         ? ResultType.MISSED
         : ResultType.SUCCESS;
   public static final Consumer<ProjectileImpactEvent> IGNORE_ALL_PROJECTILES = event -> event.setImpactResult(ImpactResult.SKIP_ENTITY);
   public static final E1<LivingEntity> AFTER_IMAGE = (livingEntityPatch, animation, params) -> {
      if (!DMCAnimationUtils.sameAccessor(DMCAnimationUtils.getRealAnimationAccessor((DynamicAnimation)animation.get()), YamatoAnimations.YAMATO_STEP_L_SHORT)) {
         if (!DMCAnimationUtils.sameAccessor(
            DMCAnimationUtils.getRealAnimationAccessor((DynamicAnimation)animation.get()), YamatoAnimations.YAMATO_STEP_R_SHORT
         )) {
            if (livingEntityPatch.isLogicalClient()) {
               LivingEntity entity = (LivingEntity)livingEntityPatch.getOriginal();
               entity.m_9236_()
                  .m_7106_(
                     (ParticleOptions)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                     entity.m_20185_(),
                     entity.m_20186_(),
                     entity.m_20189_(),
                     Double.longBitsToDouble((long)entity.m_19879_()),
                     0.0,
                     0.0
                  );
            }
         }
      }
   };

   public YamatoDodgeAnimation(
      float transitionTime,
      float delayTime,
      AnimationAccessor<? extends ActionAnimation> accessor,
      float width,
      float height,
      AssetAccessor<? extends Armature> armature
   ) {
      super(transitionTime, delayTime, accessor, armature);
      this.stateSpectrumBlueprint
         .clear()
         .newTimePair(0.0F, delayTime)
         .addState(EntityState.TURNING_LOCKED, true)
         .addState(EntityState.MOVEMENT_LOCKED, true)
         .addState(EntityState.UPDATE_LIVING_MOTION, false)
         .addState(EntityState.CAN_BASIC_ATTACK, false)
         .addState(EntityState.CAN_SKILL_EXECUTION, false)
         .addState(EntityState.INACTION, true)
         .newTimePair(0.0F, Float.MAX_VALUE)
         .addState(EntityState.ATTACK_RESULT, DODGEABLE_SOURCE_VALIDATOR)
         .addState(EntityState.PROJECTILE_IMPACT_RESULT, IGNORE_ALL_PROJECTILES);
      this.addProperty(ActionAnimationProperty.AFFECT_SPEED, false);
      this.addEvents(StaticAnimationProperty.ON_BEGIN_EVENTS, new AnimationEvent[]{SimpleEvent.create(AFTER_IMAGE, Side.CLIENT)});
      this.addEvents(StaticAnimationProperty.ON_END_EVENTS, new AnimationEvent[]{SimpleEvent.create(ReusableSources.RESTORE_BOUNDING_BOX, Side.BOTH)});
      this.addEvents(
         StaticAnimationProperty.TICK_EVENTS,
         new AnimationEvent[]{SimpleEvent.create(ReusableSources.RESIZE_BOUNDING_BOX, Side.BOTH).params(EntityDimensions.m_20395_(width, height))}
      );
   }

   public void begin(LivingEntityPatch<?> livingEntityPatch) {
      super.begin(livingEntityPatch);
      if (!livingEntityPatch.isLogicalClient()) {
         ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_().m_7967_(new DMCDodgeLocationIndicator(livingEntityPatch));
      }
   }
}
