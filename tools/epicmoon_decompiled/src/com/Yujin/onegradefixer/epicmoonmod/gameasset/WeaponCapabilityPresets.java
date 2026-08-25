package com.Yujin.onegradefixer.epicmoonmod.gameasset;

import com.Yujin.onegradefixer.epicmoonmod.animations.EMAnimations;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.forgeevent.WeaponCapabilityPresetRegistryEvent;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Builder;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;

@EventBusSubscriber(
   modid = "epicmoonmod",
   bus = Bus.MOD
)
public class WeaponCapabilityPresets {
   public static final Function<Item, Builder> TEST = item -> WeaponCapability.builder()
         .category(WeaponCategories.UCHIGATANA)
         .styleProvider(entityPatch -> Styles.TWO_HAND)
         .collider(ColliderPreset.GREATSWORD)
         .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
         .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
         .hitParticle((HitParticleType)EpicFightParticles.HIT_BLADE.get())
         .canBePlacedOffhand(false)
         .newStyleCombo(
            Styles.TWO_HAND,
            new AnimationAccessor[]{
               EMAnimations.TENTAI_SEITOU_SKILL,
               EMAnimations.TENTAI_SEITOU_AUTO1,
               EMAnimations.TENTAI_SEITOU_AUTO2,
               EMAnimations.TENTAI_SEITOU_AUTO3,
               EMAnimations.TENTAI_SEITOU_AIRSLASH,
               EMAnimations.TENTAI_SEITOU_DASH,
               EMAnimations.TENTAI_SEITOU_TSHOT1,
               EMAnimations.TENTAI_SEITOU_TSHOT2,
               EMAnimations.TENTAI_SEITOU_TSHOT5,
               EMAnimations.TENTAI_SEITOU_TDASHOT,
               EMAnimations.TENTAI_SEITOU_TAIRSHOT,
               EMAnimations.TENTAI_SEITOU_TSKILL,
               EMAnimations.TENTAI_SEITOU_TSKILL2,
               EMAnimations.TENTAI_SEITOU_TSKILL3,
               EMAnimations.TENTAI_SEITOU_TSKILL4,
               EMAnimations.TENTAI_SEITOU_TSKILL5,
               EMAnimations.TENTAI_SEITOU_TSKILL6,
               EMAnimations.TENTAI_SEITOU_TSKILL7,
               EMAnimations.TENTAI_SEITOU_TSKILL8,
               EMAnimations.TENTAI_SEITOU_TSKILL9,
               EMAnimations.TENTAI_SEITOU_TSKILL10,
               EMAnimations.TENTAI_SEITOU_ESKILL,
               EMAnimations.TENTAI_SEITOU_ESKILL2,
               EMAnimations.TENTAI_SEITOU_ESKILL3,
               EMAnimations.TENTAI_SEITOU_ESKILL4,
               EMAnimations.TENTAI_SEITOU_ESKILL5,
               EMAnimations.TENTAI_SEITOU_ESKILL6,
               EMAnimations.TENTAI_SEITOU_ESKILL7,
               EMAnimations.TENTAI_SEITOU_ESKILL8,
               EMAnimations.TENTAI_SEITOU_ESKILL9,
               EMAnimations.TENTAI_SEITOU_ESKILL10
            }
         )
         .innateSkill(Styles.TWO_HAND, itemstack -> tsskill.tsskill)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, EMAnimations.TENTAI_SEITOU_GUARD)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, EMAnimations.TENTAI_SEITOU_IDLE)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, EMAnimations.TENTAI_SEITOU_WALK)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, EMAnimations.TENTAI_SEITOU_RUN)
         .comboCancel(style -> false);
   public static final Function<Item, Builder> DUAL = item -> WeaponCapability.builder()
         .category(WeaponCategories.UCHIGATANA)
         .styleProvider(entityPatch -> Styles.TWO_HAND)
         .collider(ColliderPreset.GREATSWORD)
         .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
         .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
         .hitParticle((HitParticleType)EpicFightParticles.HIT_BLADE.get())
         .canBePlacedOffhand(false)
         .innateSkill(Styles.TWO_HAND, itemStack -> dualskill.dualskill)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, EMAnimations.DUAL_IDLE)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, EMAnimations.DUAL_WALK)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, EMAnimations.DUAL_RUN)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.BLOCK, EMAnimations.DUAL_GUARD)
         .newStyleCombo(Styles.TWO_HAND, new AnimationAccessor[]{EMAnimations.DUAL_AUTO1});

   @SubscribeEvent
   public static void register(WeaponCapabilityPresetRegistryEvent event) {
      ResourceLocation a = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "tsskill");
      ResourceLocation b = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "dualskill");
      event.getTypeEntry().put(a, TEST);
      event.getTypeEntry().put(b, DUAL);
   }
}
