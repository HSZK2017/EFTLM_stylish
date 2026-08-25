package com.pla.annoyingvillagers.combatbehaviour;

import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Builder;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.animations.weapons.AnimsHerrscher;
import reascer.wom.gameasset.animations.weapons.AnimsRuine;
import reascer.wom.gameasset.animations.weapons.AnimsSolar;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class AvNpcBlockWeapon {
   public static final Builder<MobPatch<?>> WOODEN_DOOR = AvNpcCombatBehaviorBuilder.weapon(
      CombatCommon.animations(
         new AnimationAccessor[]{Animations.GREATSWORD_AUTO1, Animations.GREATSWORD_AUTO2, WOMAnimations.TORMENT_AUTO_2, WOMAnimations.TORMENT_AUTO_3}
      ),
      new AnimationAccessor[][]{
         CombatCommon.animations(
            new AnimationAccessor[]{
               Animations.GREATSWORD_DASH, WOMAnimations.TORMENT_CHARGED_ATTACK_2, WOMAnimations.TORMENT_BERSERK_DASH, WOMAnimations.TORMENT_AIRSLAM
            }
         )
      }
   );
   public static final Builder<MobPatch<?>> TRAPDOOR = AvNpcCombatBehaviorBuilder.weapon(
      CombatCommon.animations(new AnimationAccessor[]{Animations.SWORD_AUTO1, Animations.SWORD_AUTO3, Animations.SWORD_AUTO2, AnimsHerrscher.HERRSCHER_AUTO_3}),
      new AnimationAccessor[][]{
         CombatCommon.animations(
            new AnimationAccessor[]{
               Animations.SWORD_DUAL_AUTO1,
               Animations.LONGSWORD_AUTO2,
               Animations.SWORD_DUAL_AUTO2,
               Animations.SWORD_DUAL_DASH,
               Animations.SWORD_AIR_SLASH,
               AnimsPugilistSteve.GIANT_WHIRLWIND,
               AnimsHerrscher.HERRSCHER_VERDAMMNIS
            }
         )
      }
   );
   public static final Builder<MobPatch<?>> LADDER = AvNpcCombatBehaviorBuilder.weapon(
      CombatCommon.animations(
         new AnimationAccessor[]{Animations.SWORD_AUTO1, Animations.SWORD_AUTO3, AnimsPugilistSteve.SWORD_HEAVY_AUTO_1, Animations.TACHI_AUTO3}
      ),
      new AnimationAccessor[][]{
         CombatCommon.animations(
            new AnimationAccessor[]{Animations.SWORD_DASH, Animations.VINDICATOR_SWING_AXE3, Animations.SWORD_AIR_SLASH, AnimsPugilistSteve.SWORD_HEAVY_AUTO_3}
         )
      }
   );
   public static final Builder<MobPatch<?>> CRAFTING_TABLE = AvNpcCombatBehaviorBuilder.weapon(
      CombatCommon.animations(new AnimationAccessor[]{Animations.TACHI_AUTO2, Animations.TACHI_AUTO3, AnimsRuine.RUINE_AUTO_1, AnimsRuine.RUINE_AUTO_2}),
      new AnimationAccessor[][]{
         CombatCommon.animations(
            new AnimationAccessor[]{WOMAnimations.TORMENT_AIRSLAM, Animations.LONGSWORD_AIR_SLASH, AnimsRuine.RUINE_CHATIMENT, AnimsSolar.SOLAR_AUTO_2}
         )
      }
   );
}
