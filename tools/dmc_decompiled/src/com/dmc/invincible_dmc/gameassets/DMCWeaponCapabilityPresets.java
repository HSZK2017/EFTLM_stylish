package com.dmc.invincible_dmc.gameassets;

import com.dmc.invincible_dmc.capability.item.AdvanceWeaponCapability;
import com.dmc.invincible_dmc.capability.item.ComboWeaponCapability;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.particle.DMCParticles;
import com.dmc.invincible_dmc.skill.weapon_combo.DMCDemoSkills;
import com.dmc.invincible_dmc.skill.weapon_combo.Yamato;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.forgeevent.WeaponCapabilityPresetRegistryEvent;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Builder;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.MOD
)
public class DMCWeaponCapabilityPresets {
   public static final Function<Item, Builder> DEMO = item -> ComboWeaponCapability.builder()
         .category(WeaponCategories.SWORD)
         .styleProvider(entityPatch -> Styles.COMMON)
         .collider(ColliderPreset.SWORD)
         .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
         .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
         .hitParticle((HitParticleType)EpicFightParticles.HIT_BLADE.get())
         .canBePlacedOffhand(false)
         .innateSkill(Styles.COMMON, itemstack -> DMCDemoSkills.COMBO_DEMO)
         .comboCancel(style -> false);
   public static final Function<Item, Builder> DEMO_YAMATO = item -> AdvanceWeaponCapability.builder()
         .category(WeaponCategories.SWORD)
         .styleProvider(entityPatch -> Styles.COMMON)
         .collider(ColliderPreset.SWORD)
         .swingSound((SoundEvent)EpicFightSounds.WHOOSH.get())
         .hitSound((SoundEvent)EpicFightSounds.BLADE_HIT.get())
         .hitParticle((HitParticleType)EpicFightParticles.HIT_BLADE.get())
         .canBePlacedOffhand(false)
         .exclusiveDodge(Yamato.YAMATO_DODGE)
         .innateSkill(Styles.COMMON, itemstack -> Yamato.YAMATO)
         .comboCancel(style -> false);
   public static final Function<Item, Builder> YAMATO_DMC5 = item -> AdvanceWeaponCapability.builder()
         .category(DMCWeaponCategories.DMC5_YAMATO)
         .preventCrouching()
         .styleProvider(livingEntityPatch -> Styles.TWO_HAND)
         .collider(YamatoAnimations.BLADE_COLLIDER)
         .canBePlacedOffhand(false)
         .reach(0.75F)
         .hitParticle((HitParticleType)DMCParticles.ATTACK_MAIN.get())
         .newAdvanceStyleCombo(
            Styles.TWO_HAND, Animations.TACHI_AUTO1, Animations.TACHI_AUTO2, Animations.TACHI_AUTO3, Animations.TACHI_DASH, Animations.LONGSWORD_AIR_SLASH
         )
         .exclusiveDodge(Yamato.YAMATO_DODGE)
         .innateSkill(Styles.TWO_HAND, itemstack -> Yamato.YAMATO)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.IDLE, YamatoAnimations.YAMATO_IDLE)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.CREATIVE_IDLE, YamatoAnimations.YAMATO_IDLE)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.FALL, YamatoAnimations.YAMATO_IDLE)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.WALK, YamatoAnimations.YAMATO_WALK)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.RUN, YamatoAnimations.YAMATO_RUN)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.KNEEL, YamatoAnimations.YAMATO_KNEEL)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.SNEAK, YamatoAnimations.YAMATO_SNEAK)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.SNEAK, YamatoAnimations.YAMATO_SNEAK)
         .livingMotionModifier(Styles.TWO_HAND, LivingMotions.JUMP, YamatoAnimations.YAMATO_JUMP)
         .livingMotionProvider(DMCWeaponCapabilityPresets::createArsenalLivingMotions);

   @SubscribeEvent
   public static void register(WeaponCapabilityPresetRegistryEvent event) {
      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("invincible_dmc", "demo"), DEMO);
      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("invincible_dmc", "demo_yamato"), DEMO_YAMATO);
      event.getTypeEntry().put(ResourceLocation.fromNamespaceAndPath("invincible_dmc", "yamato_dmc5"), YAMATO_DMC5);
   }

   private static Map<LivingMotion, AnimationAccessor<? extends StaticAnimation>> createArsenalLivingMotions(LivingEntityPatch<?> playerPatch) {
      Map<LivingMotion, AnimationAccessor<? extends StaticAnimation>> motions = new HashMap<>();
      motions.put(LivingMotions.IDLE, YamatoAnimations.YAMATO_IDLE);
      motions.put(LivingMotions.CREATIVE_IDLE, YamatoAnimations.YAMATO_IDLE);
      motions.put(LivingMotions.FALL, YamatoAnimations.YAMATO_IDLE);
      motions.put(LivingMotions.WALK, YamatoAnimations.YAMATO_WALK);
      motions.put(LivingMotions.RUN, YamatoAnimations.YAMATO_RUN);
      motions.put(LivingMotions.KNEEL, YamatoAnimations.YAMATO_KNEEL);
      motions.put(LivingMotions.SNEAK, YamatoAnimations.YAMATO_SNEAK);
      motions.put(LivingMotions.JUMP, YamatoAnimations.YAMATO_JUMP);
      return motions;
   }
}
