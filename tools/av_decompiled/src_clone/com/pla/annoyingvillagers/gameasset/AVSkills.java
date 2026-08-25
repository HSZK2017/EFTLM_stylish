package com.pla.annoyingvillagers.gameasset;

import com.pla.annoyingvillagers.skill.BedrockWeaponSkill;
import com.pla.annoyingvillagers.skill.DemoniacVoltageReaverSkill;
import com.pla.annoyingvillagers.skill.DiamondAttractorSwordSkill;
import com.pla.annoyingvillagers.skill.EnderAegisSkill;
import com.pla.annoyingvillagers.skill.EnderGlaiveSkill;
import com.pla.annoyingvillagers.skill.EnderSlayerScytheSkill;
import com.pla.annoyingvillagers.skill.GreatSwordSkill;
import com.pla.annoyingvillagers.skill.GuandaoSkill;
import com.pla.annoyingvillagers.skill.HookSwordSkill;
import com.pla.annoyingvillagers.skill.LegendarySwordSkill;
import com.pla.annoyingvillagers.skill.NullWeaponSkill;
import com.pla.annoyingvillagers.skill.ObsidianSledgeHammerSkill;
import com.pla.annoyingvillagers.skill.ObsidianWeaponSkill;
import com.pla.annoyingvillagers.skill.ShadowObsidianPillarSkill;
import com.pla.annoyingvillagers.skill.ShadowObsidianPillarSwordSkill;
import com.pla.annoyingvillagers.skill.ShadowObsidianSwordDualSkill;
import com.pla.annoyingvillagers.skill.ShadowObsidianSwordSkill;
import com.pla.annoyingvillagers.skill.TridentFestivalSkill;
import com.pla.annoyingvillagers.skill.WoopieTheSwordSkill;
import java.util.Set;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.animations.weapons.AnimsHerrscher;
import reascer.wom.gameasset.animations.weapons.AnimsRuine;
import reascer.wom.gameasset.animations.weapons.AnimsSolar;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.api.forgeevent.SkillBuildEvent.ModRegistryWorker;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.weaponinnate.SimpleWeaponInnateSkill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.skill.weaponinnate.SimpleWeaponInnateSkill.Builder;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.StunType;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.MOD
)
public class AVSkills {
   public static Skill ENDER_AEGIS;
   public static Skill ENDER_GLAIVE;
   public static Skill DEMONIAC_VOLTAGE_REAVER;
   public static Skill OBSIDIAN_SLEDGEHAMMER;
   public static Skill ENDER_SLAYER_SCYTHE;
   public static Skill NULL_WEAPON;
   public static Skill OBSIDIAN_WEAPON;
   public static Skill BEDROCK_WEAPON;
   public static Skill SHADOW_OBSIDIAN_PILLAR;
   public static Skill SHADOW_OBSIDIAN_PILLAR_SWORD;
   public static Skill SHADOW_OBSIDIAN_SWORD;
   public static Skill SHADOW_OBSIDIAN_SWORD_DUAL;
   public static Skill TRIDENT_FESTIVAL;
   public static Skill LEGENDARY_SWORD;
   public static Skill WOOPIE_THE_SWORD;
   public static Skill GREAT_SWORD;
   public static Skill CRAFTING_TABLE;
   public static Skill WOODEN_DOOR;
   public static Skill TRAPDOOR;
   public static Skill LADDER;
   public static Skill BLACK_FIRE_SWORD;
   public static Skill BLUE_FLAME_SWORD;
   public static Skill CLOW_SWORD;
   public static Skill CLEAVER;
   public static Skill DIAMOND_ATTRACTOR_SWORD;
   public static Skill DIAMOND_BLASTER_SWORD;
   public static Skill HACKER_SWORD;
   public static Skill DIAMOND_SABRE;
   public static Skill DIAMOND_WARBLADE;
   public static Skill DIAMOND_LAEVATEINN;
   public static Skill HOOK_SWORD;
   public static Skill DUAL_HOOK_SWORD;
   public static Skill FLANKER_HOOK_SWORD;
   public static Skill DNAX_HOOK_SWORD;
   public static Skill DUAL_DNAX_HOOK_SWORD;
   public static Skill CHIPPED_LONGSWORD;
   public static Skill HELICOPTER;
   public static Skill THUNDER_DIAMOND_BLADE;
   public static Skill DUAL_THUNDER_DIAMOND_BLADE;
   public static Skill EARTH_AXE;
   public static Skill RED_AXE;
   public static Skill DUAL_AXE_SPIN;
   public static Skill GREATAXE;
   public static Skill GIANT_AXE;
   public static Skill BATTLE_AXE;
   public static Skill HALBERD;
   public static Skill KILLER_AXE;
   public static Skill KNIFE;
   public static Skill DUAL_KNIFE;
   public static Skill CLAW;
   public static Skill ARM_BLADE;
   public static Skill MOON_BLADE;
   public static Skill SWORD;
   public static Skill DUAL_SWORD;
   public static Skill LONGSWORD;
   public static Skill FALCHION;
   public static Skill DUAL_FALCHION;
   public static Skill GUANDAO;
   public static Skill BLACKSCRATCHER;

   @SubscribeEvent
   public static void buildSkillEvent(SkillBuildEvent skillbuildevent) {
      ModRegistryWorker modRegistry = skillbuildevent.createRegistryWorker("annoyingvillagers");
      ENDER_AEGIS = modRegistry.build("ender_aegis", EnderAegisSkill::new, WeaponInnateSkill.createWeaponInnateBuilder().setActivateType(ActivateType.DURATION));
      ENDER_GLAIVE = modRegistry.build("ender_glaive", EnderGlaiveSkill::new, WeaponInnateSkill.createWeaponInnateBuilder());
      DEMONIAC_VOLTAGE_REAVER = modRegistry.build("demoniac_voltage_reaver", DemoniacVoltageReaverSkill::new, WeaponInnateSkill.createWeaponInnateBuilder());
      OBSIDIAN_SLEDGEHAMMER = modRegistry.build(
         "obsidian_sledgehammer", ObsidianSledgeHammerSkill::new, WeaponInnateSkill.createWeaponInnateBuilder().setActivateType(ActivateType.DURATION)
      );
      ENDER_SLAYER_SCYTHE = modRegistry.build(
         "ender_slayer_scythe", EnderSlayerScytheSkill::new, WeaponInnateSkill.createWeaponInnateBuilder().setActivateType(ActivateType.DURATION)
      );
      NULL_WEAPON = modRegistry.build("null_weapon", NullWeaponSkill::new, WeaponInnateSkill.createWeaponInnateBuilder().setActivateType(ActivateType.DURATION));
      OBSIDIAN_WEAPON = modRegistry.build("obsidian_weapon", ObsidianWeaponSkill::new, WeaponInnateSkill.createWeaponInnateBuilder());
      BEDROCK_WEAPON = modRegistry.build(
         "bedrock_weapon", BedrockWeaponSkill::new, WeaponInnateSkill.createWeaponInnateBuilder().setActivateType(ActivateType.DURATION)
      );
      SHADOW_OBSIDIAN_PILLAR = modRegistry.build("shadow_obsidian_pillar", ShadowObsidianPillarSkill::new, WeaponInnateSkill.createWeaponInnateBuilder());
      SHADOW_OBSIDIAN_PILLAR_SWORD = modRegistry.build(
         "shadow_obsidian_pillar_sword", ShadowObsidianPillarSwordSkill::new, WeaponInnateSkill.createWeaponInnateBuilder()
      );
      SHADOW_OBSIDIAN_SWORD = modRegistry.build("shadow_obsidian_sword", ShadowObsidianSwordSkill::new, WeaponInnateSkill.createWeaponInnateBuilder());
      SHADOW_OBSIDIAN_SWORD_DUAL = modRegistry.build(
         "shadow_obsidian_sword_dual", ShadowObsidianSwordDualSkill::new, WeaponInnateSkill.createWeaponInnateBuilder()
      );
      LEGENDARY_SWORD = modRegistry.build("legendary_sword", LegendarySwordSkill::new, WeaponInnateSkill.createWeaponInnateBuilder());
      TRIDENT_FESTIVAL = modRegistry.build("trident_festival", TridentFestivalSkill::new, WeaponInnateSkill.createWeaponInnateBuilder());
      WOOPIE_THE_SWORD = modRegistry.build("woopie_the_sword", WoopieTheSwordSkill::new, WeaponInnateSkill.createWeaponInnateBuilder());
      GREAT_SWORD = modRegistry.build("great_sword", GreatSwordSkill::new, WeaponInnateSkill.createWeaponInnateBuilder().setActivateType(ActivateType.DURATION));
      CRAFTING_TABLE = ((SimpleWeaponInnateSkill)modRegistry.build(
            "crafting_table",
            SimpleWeaponInnateSkill::new,
            (Builder)SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder()
               .setAnimations(AnimsSolar.SOLAR_AUTO_2)
               .setActivateType(ActivateType.ONE_SHOT)
               .setResource(Resource.STAMINA)
         ))
         .newProperty();
      WOODEN_DOOR = ((SimpleWeaponInnateSkill)modRegistry.build(
            "wooden_door",
            SimpleWeaponInnateSkill::new,
            (Builder)SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder()
               .setAnimations(WOMAnimations.TORMENT_BERSERK_DASH)
               .setActivateType(ActivateType.ONE_SHOT)
               .setResource(Resource.STAMINA)
         ))
         .newProperty();
      TRAPDOOR = ((SimpleWeaponInnateSkill)modRegistry.build(
            "trapdoor",
            SimpleWeaponInnateSkill::new,
            (Builder)SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder()
               .setAnimations(AnimsPugilistSteve.GIANT_WHIRLWIND)
               .setActivateType(ActivateType.ONE_SHOT)
               .setResource(Resource.STAMINA)
         ))
         .newProperty();
      LADDER = ((SimpleWeaponInnateSkill)modRegistry.build(
            "ladder",
            SimpleWeaponInnateSkill::new,
            (Builder)SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder()
               .setAnimations(AnimsPugilistSteve.SWORD_HEAVY_AUTO_3)
               .setActivateType(ActivateType.ONE_SHOT)
               .setResource(Resource.STAMINA)
         ))
         .newProperty();
      BLACK_FIRE_SWORD = ((SimpleWeaponInnateSkill)modRegistry.build(
            "black_fire_sword",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AVAnimations.BLACK_FIRE_SWORD_SKILL)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      BLUE_FLAME_SWORD = ((SimpleWeaponInnateSkill)modRegistry.build(
            "blue_flame_sword",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsHerrscher.HERRSCHER_AUSROTTUNG)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      CLOW_SWORD = ((SimpleWeaponInnateSkill)modRegistry.build(
            "clow_sword",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsHerrscher.HERRSCHER_BEFREIUNG)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      CLEAVER = ((SimpleWeaponInnateSkill)modRegistry.build(
            "cleaver",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFightBattleArts.SQUIRE_SWORD_HEAVY_BLOW)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
         .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
         .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      DIAMOND_ATTRACTOR_SWORD = modRegistry.build("diamond_attractor_sword", DiamondAttractorSwordSkill::new, WeaponInnateSkill.createWeaponInnateBuilder());
      DIAMOND_BLASTER_SWORD = ((SimpleWeaponInnateSkill)modRegistry.build(
            "diamond_blaster_sword",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AVAnimations.DIAMOND_BLASTER_SKILL)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      HACKER_SWORD = ((SimpleWeaponInnateSkill)modRegistry.build(
            "hacker_sword", SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsWom.HACKER_SWORD_SKILL)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      DIAMOND_SABRE = ((SimpleWeaponInnateSkill)modRegistry.build(
            "diamond_sabre",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFightBattleArts.SABRE_QUAD_STING)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      DIAMOND_WARBLADE = ((SimpleWeaponInnateSkill)modRegistry.build(
            "diamond_warblade",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsWom.WARBLADE_SATSUJIN_TSUKUYOMI)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      DIAMOND_LAEVATEINN = ((SimpleWeaponInnateSkill)modRegistry.build(
            "diamond_laevateinn",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFightBattleArts.TACHI_BLOSSOM_SLASH)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
         .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
         .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      HOOK_SWORD = modRegistry.build("hook_sword", HookSwordSkill::new, WeaponInnateSkill.createWeaponInnateBuilder());
      DUAL_HOOK_SWORD = ((SimpleWeaponInnateSkill)modRegistry.build(
            "dual_hook_sword",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFight.HOOK_DANCING_EDGE)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      FLANKER_HOOK_SWORD = ((SimpleWeaponInnateSkill)modRegistry.build(
            "flanker_hook_sword",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsWom.HOOK_HERRSCHER_UP)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      DNAX_HOOK_SWORD = ((SimpleWeaponInnateSkill)modRegistry.build(
            "dnax_hook_sword",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFight.DNAX_HOOK_SWEEPING_EDGE)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      DUAL_DNAX_HOOK_SWORD = ((SimpleWeaponInnateSkill)modRegistry.build(
            "dual_dnax_hook_sword",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFight.DNAX_HOOK_DANCING_EDGE)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      LONGSWORD = ((SimpleWeaponInnateSkill)modRegistry.build(
            "longsword",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFightAwaken.DP_FALLING_SHADOW)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
         .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
         .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      CHIPPED_LONGSWORD = ((SimpleWeaponInnateSkill)modRegistry.build(
            "chipped_longsword",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsRuine.RUINE_REDEMPTION)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
         .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
         .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      HELICOPTER = ((SimpleWeaponInnateSkill)modRegistry.build(
            "helicopter",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsPugilistSteve.GREATSWORD_SKILL)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
         .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
         .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      THUNDER_DIAMOND_BLADE = ((SimpleWeaponInnateSkill)modRegistry.build(
            "thunder_diamond_blade",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFight.THUNDER_SWEEPING_EDGE)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
         .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
         .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      DUAL_THUNDER_DIAMOND_BLADE = ((SimpleWeaponInnateSkill)modRegistry.build(
            "dual_thunder_diamond_blade",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFight.THUNDER_DANCING_EDGE)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE))
         .newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE))
         .newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      EARTH_AXE = ((SimpleWeaponInnateSkill)modRegistry.build(
            "earth_axe", SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsWom.EARTH_AXE)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      RED_AXE = ((SimpleWeaponInnateSkill)modRegistry.build(
            "red_axe", SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AVAnimations.RED_AXE_ATTACK)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      DUAL_AXE_SPIN = ((SimpleWeaponInnateSkill)modRegistry.build(
            "dual_axe_spin",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsPugilistSteve.DUAL_SWORD_SKILL)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
         .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
         .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      GREATAXE = ((SimpleWeaponInnateSkill)modRegistry.build(
            "greataxe", SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsYonchiChikito.SLAM_THIRD)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      BATTLE_AXE = ((SimpleWeaponInnateSkill)modRegistry.build(
            "battle_axe", SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsYonchiChikito.SLAM_FIRST)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      GIANT_AXE = ((SimpleWeaponInnateSkill)modRegistry.build(
            "giant_axe", SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsYonchiChikito.SLAM_SECOND)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      KILLER_AXE = ((SimpleWeaponInnateSkill)modRegistry.build(
            "killer_axe",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFightBattleArts.AXE_INNATE)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
         .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
         .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      HALBERD = ((SimpleWeaponInnateSkill)modRegistry.build(
            "halberd",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFightBattleArts.BAXE_SEISMIC_IMPACT)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      KNIFE = ((SimpleWeaponInnateSkill)modRegistry.build(
            "knife",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFightBattleArts.THIEF_STEAL)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
         .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
         .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      DUAL_KNIFE = ((SimpleWeaponInnateSkill)modRegistry.build(
            "dual_knife",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFightBattleArts.DUAL_BLADES_WHIRLEDGE)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      CLAW = ((SimpleWeaponInnateSkill)modRegistry.build(
            "claw", SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(Animations.FIST_AIR_SLASH)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
         .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
         .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      ARM_BLADE = ((SimpleWeaponInnateSkill)modRegistry.build(
            "arm_blade",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFightSanji.SANJI_CONCASSER)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      MOON_BLADE = ((SimpleWeaponInnateSkill)modRegistry.build(
            "moon_blade",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFightSanji.SANJI_DIABLE)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      SWORD = ((SimpleWeaponInnateSkill)modRegistry.build(
            "sword",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFightAwaken.CUT_LEFT_DP_DUSK_REAVER_2)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
         .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
         .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      DUAL_SWORD = ((SimpleWeaponInnateSkill)modRegistry.build(
            "dual_sword",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFightAwaken.HOOK_SLASH_GROUND)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
         .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
         .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      FALCHION = ((SimpleWeaponInnateSkill)modRegistry.build(
            "falchion",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFightAwaken.CUT_LEFT_DP_PHANTOM_DANCE_END_1_ENHANCED)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
         .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
         .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      DUAL_FALCHION = ((SimpleWeaponInnateSkill)modRegistry.build(
            "dual_falchion",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AnimsEpicFightAwaken.DP_PHANTOM_DANCE_END_2_ENHANCED)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
         .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
         .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
      GUANDAO = modRegistry.build("guandao", GuandaoSkill::new, WeaponInnateSkill.createWeaponInnateBuilder());
      BLACKSCRATCHER = ((SimpleWeaponInnateSkill)modRegistry.build(
            "blackscratcher",
            SimpleWeaponInnateSkill::new,
            SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(AVAnimations.BLACKSCRATCHER_ATTACK)
         ))
         .newProperty()
         .addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1.0F))
         .addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
         .addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
         .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
         .addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
         .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(new float[0])))
         .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageTypeTags.WEAPON_INNATE));
   }
}
