package com.pla.annoyingvillagers.compat.dualaxes;

import M6FGR.dualaxes.gameassets.DualAxesAnimations;
import M6FGR.dualaxes.gameassets.DualAxesSkills;
import java.util.function.Function;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Builder;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;

public class EpicFightDualAxe {
   public static final Function<Item, Builder> AXE_DUAL = item -> WeaponCapability.builder()
         .category(WeaponCategories.AXE)
         .styleProvider(
            entityPatch -> {
               if (entityPatch instanceof PlayerPatch<?> playerPatch) {
                  return playerPatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() == WeaponCategories.AXE
                     ? Styles.TWO_HAND
                     : Styles.ONE_HAND;
               } else if (entityPatch instanceof LivingEntityPatch) {
                  return entityPatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() == WeaponCategories.AXE
                     ? Styles.TWO_HAND
                     : Styles.ONE_HAND;
               } else {
                  return Styles.ONE_HAND;
               }
            }
         )
         .collider(ColliderPreset.TOOLS)
         .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
         .newStyleCombo(
            Styles.ONE_HAND,
            new AnimationAccessor[]{
               DualAxesAnimations.AXE_AUTO_1, DualAxesAnimations.AXE_AUTO_2, DualAxesAnimations.AXE_AUTO_3, Animations.BIPED_MOB_TACHI, Animations.AXE_AIRSLASH
            }
         )
         .innateSkill(Styles.ONE_HAND, itemstack -> EpicFightSkills.GUILLOTINE_AXE)
         .livingMotionModifier(Styles.ONE_HAND, LivingMotions.IDLE, Animations.BIPED_IDLE)
         .livingMotionModifier(Styles.ONE_HAND, LivingMotions.WALK, Animations.BIPED_WALK)
         .livingMotionModifier(Styles.ONE_HAND, LivingMotions.RUN, Animations.BIPED_RUN)
         .livingMotionModifier(Styles.ONE_HAND, LivingMotions.JUMP, Animations.BIPED_JUMP)
         .livingMotionModifier(Styles.ONE_HAND, LivingMotions.KNEEL, Animations.BIPED_KNEEL)
         .livingMotionModifier(Styles.ONE_HAND, LivingMotions.SNEAK, Animations.BIPED_SNEAK)
         .livingMotionModifier(Styles.ONE_HAND, LivingMotions.SWIM, Animations.BIPED_SWIM)
         .livingMotionModifier(Styles.ONE_HAND, LivingMotions.BLOCK, Animations.SWORD_DUAL_GUARD)
         .newStyleCombo(
            Styles.TWO_HAND,
            new AnimationAccessor[]{
               DualAxesAnimations.AXE_DUAL_AUTO_1,
               DualAxesAnimations.AXE_DUAL_AUTO_2,
               DualAxesAnimations.AXE_DUAL_AUTO_3,
               DualAxesAnimations.AXE_DUAL_DASH,
               DualAxesAnimations.AXE_DUAL_AIRSLASH
            }
         )
         .innateSkill(Styles.TWO_HAND, itemstack -> DualAxesSkills.SPINNING_DEATH)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, DualAxesAnimations.AXE_DUAL_IDLE)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, Animations.BIPED_WALK)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, Animations.BIPED_RUN_DUAL)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.JUMP, Animations.BIPED_JUMP)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.KNEEL, Animations.BIPED_KNEEL)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.SNEAK, Animations.BIPED_SNEAK)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_SWIM)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.SWORD_DUAL_GUARD)
         .weaponCombinationPredicator(livingEntityPatch -> true);
}
