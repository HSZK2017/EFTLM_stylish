package com.pla.annoyingvillagers.compat.dualgreatsword;

import M6FGR.dualgreatswords.gameassets.DualGreatSwordsAnimations;
import M6FGR.dualgreatswords.gameassets.DualGreatSwordsSkills;
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
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Builder;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;

public class EpicFightDualGreatsword {
   public static final Function<Item, Builder> GREATSWORD_DUAL = item -> WeaponCapability.builder()
         .category(WeaponCategories.GREATSWORD)
         .styleProvider(
            playerpatch -> playerpatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() == WeaponCategories.GREATSWORD
                  ? Styles.OCHS
                  : Styles.TWO_HAND
         )
         .collider(ColliderPreset.GREATSWORD)
         .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
         .swingSound((SoundEvent)EpicFightSounds.WHOOSH_BIG.get())
         .newStyleCombo(
            Styles.TWO_HAND,
            new AnimationAccessor[]{Animations.GREATSWORD_AUTO1, Animations.GREATSWORD_AUTO2, Animations.GREATSWORD_DASH, Animations.GREATSWORD_AIR_SLASH}
         )
         .innateSkill(Styles.TWO_HAND, itemstack -> EpicFightSkills.STEEL_WHIRLWIND)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, Animations.BIPED_HOLD_GREATSWORD)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, Animations.BIPED_WALK_GREATSWORD)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.CHASE, Animations.BIPED_WALK_GREATSWORD)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, Animations.BIPED_RUN_GREATSWORD)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.JUMP, Animations.BIPED_WALK_GREATSWORD)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.KNEEL, Animations.BIPED_HOLD_GREATSWORD)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.SNEAK, Animations.BIPED_SNEAK)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.SWIM, Animations.BIPED_HOLD_GREATSWORD)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, Animations.GREATSWORD_GUARD)
         .newStyleCombo(
            Styles.OCHS,
            new AnimationAccessor[]{
               DualGreatSwordsAnimations.GREATSWORD_DUAL_AUTO_1,
               DualGreatSwordsAnimations.GREATSWORD_DUAL_AUTO_2,
               DualGreatSwordsAnimations.GREATSWORD_DUAL_AUTO_3,
               DualGreatSwordsAnimations.GREATSWORD_DUAL_AUTO_4,
               DualGreatSwordsAnimations.GREATSWORD_DUAL_DASH,
               DualGreatSwordsAnimations.GREATSWORD_DUAL_AIRSLASH
            }
         )
         .innateSkill(Styles.OCHS, itemstack -> DualGreatSwordsSkills.EARTHQUAKE)
         .livingMotionModifier(Styles.OCHS, LivingMotions.IDLE, DualGreatSwordsAnimations.GREATSWORD_DUAL_IDLE)
         .livingMotionModifier(Styles.OCHS, LivingMotions.WALK, DualGreatSwordsAnimations.GREATSWORD_DUAL_WALK)
         .livingMotionModifier(Styles.OCHS, LivingMotions.CHASE, DualGreatSwordsAnimations.GREATSWORD_DUAL_IDLE)
         .livingMotionModifier(Styles.OCHS, LivingMotions.RUN, DualGreatSwordsAnimations.GREATSWORD_DUAL_RUN)
         .livingMotionModifier(Styles.OCHS, LivingMotions.JUMP, DualGreatSwordsAnimations.GREATSWORD_DUAL_IDLE)
         .livingMotionModifier(Styles.OCHS, LivingMotions.KNEEL, DualGreatSwordsAnimations.GREATSWORD_DUAL_IDLE)
         .livingMotionModifier(Styles.OCHS, LivingMotions.SNEAK, DualGreatSwordsAnimations.GREATSWORD_DUAL_IDLE)
         .livingMotionModifier(Styles.OCHS, LivingMotions.SWIM, DualGreatSwordsAnimations.GREATSWORD_DUAL_IDLE)
         .livingMotionModifier(Styles.OCHS, LivingMotions.BLOCK, Animations.SWORD_DUAL_GUARD)
         .weaponCombinationPredicator(
            entitypatch -> entitypatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() == WeaponCategories.GREATSWORD
         );
}
