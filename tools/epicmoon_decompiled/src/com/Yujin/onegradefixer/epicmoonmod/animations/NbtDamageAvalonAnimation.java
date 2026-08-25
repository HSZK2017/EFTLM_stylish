package com.Yujin.onegradefixer.epicmoonmod.animations;

import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation.AvalonPhase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public class NbtDamageAvalonAnimation extends AvalonAttackAnimation {
   private static final String SNAPSHOT_TAG = "epicmoon_damage_multi";

   public NbtDamageAvalonAnimation(
      float convertTime,
      AnimationAccessor<? extends BasicAttackAnimation> accessor,
      AssetAccessor<? extends Armature> armature,
      float playSpeed,
      float damageMulti,
      AvalonPhase... phases
   ) {
      super(convertTime, accessor, armature, playSpeed, damageMulti, phases);
   }

   public void begin(LivingEntityPatch<?> entityPatch) {
      super.begin(entityPatch);
      LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
      ItemStack weapon = entity.m_21205_();
      float multiplier = 1.0F;
      CompoundTag tag = weapon.m_41783_();
      if (tag != null) {
         int weaponMode = tag.m_128451_("amount");

         multiplier = switch (weaponMode) {
            case 1 -> 1.15F;
            case 2 -> 1.3F;
            case 3 -> 1.45F;
            case 4 -> 1.6F;
            case 5 -> 1.75F;
            case 6 -> 1.9F;
            case 7 -> 2.05F;
            case 8 -> 2.2F;
            case 9 -> 2.35F;
            case 10 -> 2.5F;
            default -> 1.0F;
         };
      }

      entity.getPersistentData().m_128350_("epicmoon_damage_multi", multiplier);
   }

   public EpicFightDamageSource getEpicFightDamageSource(DamageSource originalSource, LivingEntityPatch<?> entityPatch, Entity target, Phase phase) {
      EpicFightDamageSource source = super.getEpicFightDamageSource(originalSource, entityPatch, target, phase);
      LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
      float multiplier = entity.getPersistentData().m_128457_("epicmoon_damage_multi");
      if (multiplier <= 0.0F) {
         multiplier = 1.0F;
      }

      source.attachDamageModifier(ValueModifier.multiplier(multiplier));
      return source;
   }

   public void end(LivingEntityPatch<?> entityPatch, AssetAccessor<? extends DynamicAnimation> nextAnimation, boolean isEnd) {
      ((LivingEntity)entityPatch.getOriginal()).getPersistentData().m_128473_("epicmoon_damage_multi");
      super.end(entityPatch, nextAnimation, isEnd);
   }
}
