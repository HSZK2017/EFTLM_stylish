package com.dmc.invincible_dmc.gameassets.animations.yamato;

import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.dmc.invincible_dmc.utils.yamato.JCEClient;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InPeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public final class YamatoAnimationEffectManager {
   private YamatoAnimationEffectManager() {
   }

   public static List<YamatoAnimationEffectManager.EffectDefinition> getEffects(String animationName) {
      return switch (animationName) {
         case "YAMATO_AERIALRAVE_COMBO_A_1" -> YamatoAnimationEffectManager.AerialRaveComboA1.effects();
         case "YAMATO_AERIALRAVE_COMBO_A_2" -> YamatoAnimationEffectManager.AerialRaveComboA2.effects();
         case "YAMATO_AERIALRAVE_COMBO_A_3" -> YamatoAnimationEffectManager.AerialRaveComboA3.effects();
         case "YAMATO_AERIALRAVE_COMBO_B_1" -> YamatoAnimationEffectManager.AerialRaveComboB1.effects();
         case "YAMATO_AERIALRAVE_COMBO_B_2" -> YamatoAnimationEffectManager.AerialRaveComboB2.effects();
         case "YAMATO_AERIALCLEAVE" -> YamatoAnimationEffectManager.AerialCleave.effects();
         case "YAMATO_AERIALCLEAVE_DASH" -> YamatoAnimationEffectManager.AerialCleaveDash.effects();
         case "YAMATO_AERIALCLEAVE_FAST" -> YamatoAnimationEffectManager.AerialCleaveFast.effects();
         case "YAMATO_COMBO_A_1" -> YamatoAnimationEffectManager.ComboA1.effects();
         case "YAMATO_COMBO_A_2" -> YamatoAnimationEffectManager.ComboA2.effects();
         case "YAMATO_COMBO_A_3" -> YamatoAnimationEffectManager.ComboA3.effects();
         case "YAMATO_DODGE_COUNTER" -> YamatoAnimationEffectManager.DodgeCounter.effects();
         case "YAMATO_STRIKE" -> YamatoAnimationEffectManager.Strike.effects();
         case "YAMATO_COMBO_A_4" -> YamatoAnimationEffectManager.ComboA4.effects();
         case "YAMATO_COMBO_A_4_SDT" -> YamatoAnimationEffectManager.ComboA4Sdt.effects();
         case "YAMATO_COMBO_A_5_SDT" -> YamatoAnimationEffectManager.ComboA5Sdt.effects();
         case "YAMATO_COMBO_B_1" -> YamatoAnimationEffectManager.ComboB1.effects();
         case "YAMATO_COMBO_B_2_SDT" -> YamatoAnimationEffectManager.ComboB2Sdt.effects();
         case "YAMATO_COMBO_C_START" -> YamatoAnimationEffectManager.ComboCStart.effects();
         case "YAMATO_COMBO_C_LOOP" -> YamatoAnimationEffectManager.ComboCLoop.effects();
         case "YAMATO_COMBO_C_END" -> YamatoAnimationEffectManager.ComboCEnd.effects();
         case "YAMATO_JUDGEMENT_CUT_GROUND" -> YamatoAnimationEffectManager.JudgementCutGround.effects();
         case "YAMATO_JUDGEMENT_CUT_GROUND_FS" -> YamatoAnimationEffectManager.JudgementCutGroundFs.effects();
         case "YAMATO_JUDGEMENT_CUT_AIR" -> YamatoAnimationEffectManager.JudgementCutAir.effects();
         case "YAMATO_JUDGEMENT_CUT_AIR_FS" -> YamatoAnimationEffectManager.JudgementCutAirFs.effects();
         case "YAMATO_JUDGEMENT_CUT_END" -> YamatoAnimationEffectManager.JudgementCutEnd.effects();
         case "YAMATO_JUDGEMENT_CUT_END_INSTANT" -> YamatoAnimationEffectManager.JudgementCutEndInstant.effects();
         case "YAMATO_JUDGEMENT_CUT_END_DMC3" -> YamatoAnimationEffectManager.JudgementCutEndDmc3.effects();
         case "YAMATO_PROVOCATION_B_AERIAL" -> YamatoAnimationEffectManager.ProvocationBAerial.effects();
         case "YAMATO_PROVOCATION_C" -> YamatoAnimationEffectManager.ProvocationC.effects();
         case "YAMATO_PROVOCATION_PORTAL" -> YamatoAnimationEffectManager.ProvocationPortal.effects();
         case "YAMATO_RAPIDSLASH" -> YamatoAnimationEffectManager.RapidSlash.effects();
         case "YAMATO_RAPIDSLASH_RE" -> YamatoAnimationEffectManager.RapidSlashRe.effects();
         case "YAMATO_RAPIDSLASH_AIR" -> YamatoAnimationEffectManager.RapidSlashAir.effects();
         case "YAMATO_RISINGSTAR" -> YamatoAnimationEffectManager.RisingStar.effects();
         case "YAMATO_VOID_SLASH" -> YamatoAnimationEffectManager.VoidSlash.effects();
         case "YAMATO_UPPERSLASH_1" -> YamatoAnimationEffectManager.UpperSlash1.effects();
         case "YAMATO_UPPERSLASH_2" -> YamatoAnimationEffectManager.UpperSlash2.effects();
         default -> List.of();
      };
   }

   public static List<YamatoAnimationEffectManager.EffectDefinition> getEffects(String animationName, YamatoAnimationEffectManager.Stage stage) {
      return getEffects(animationName).stream().filter(effect -> effect.stage() == stage).toList();
   }

   public static <T extends StaticAnimation> Function<AnimationAccessor<T>, T> withEffects(
      String animationName, Function<AnimationAccessor<T>, T> animationFactory
   ) {
      return accessor -> attach(animationName, animationFactory.apply(accessor));
   }

   private static <T extends StaticAnimation> T attach(String animationName, T animation) {
      for (YamatoAnimationEffectManager.EffectDefinition effect : getEffects(animationName)) {
         switch (effect.channel()) {
            case TICK:
               animation.addEvents(new AnimationEvent[]{effect.event()});
               break;
            case STATIC_BEGIN:
            case ACTION_BEGIN:
               animation.addEvents(StaticAnimationProperty.ON_BEGIN_EVENTS, new AnimationEvent[]{effect.simpleEvent()});
               break;
            case STATIC_END:
            case ACTION_END:
               animation.addEvents(StaticAnimationProperty.ON_END_EVENTS, new AnimationEvent[]{effect.simpleEvent()});
         }
      }

      return animation;
   }

   private static YamatoAnimationEffectManager.EffectDefinition at(float time, YamatoAnimationEffectManager.ClientEffect effect) {
      return new YamatoAnimationEffectManager.EffectDefinition(
         YamatoAnimationEffectManager.Stage.TIMELINE,
         YamatoAnimationEffectManager.Channel.TICK,
         time,
         time,
         () -> InTimeEvent.create(time, (patch, animation, parameters) -> effect.play(patch), Side.CLIENT)
      );
   }

   private static YamatoAnimationEffectManager.EffectDefinition begin(YamatoAnimationEffectManager.ClientEffect effect) {
      return simple(YamatoAnimationEffectManager.Stage.BEGIN, YamatoAnimationEffectManager.Channel.STATIC_BEGIN, effect);
   }

   private static YamatoAnimationEffectManager.EffectDefinition actionEnd(YamatoAnimationEffectManager.ClientEffect effect) {
      return simple(YamatoAnimationEffectManager.Stage.END, YamatoAnimationEffectManager.Channel.ACTION_END, effect);
   }

   private static YamatoAnimationEffectManager.EffectDefinition staticEnd(YamatoAnimationEffectManager.ClientEffect effect) {
      return simple(YamatoAnimationEffectManager.Stage.END, YamatoAnimationEffectManager.Channel.STATIC_END, effect);
   }

   private static YamatoAnimationEffectManager.EffectDefinition simple(
      YamatoAnimationEffectManager.Stage stage, YamatoAnimationEffectManager.Channel channel, YamatoAnimationEffectManager.ClientEffect effect
   ) {
      return new YamatoAnimationEffectManager.EffectDefinition(
         stage, channel, Float.NaN, Float.NaN, () -> SimpleEvent.create((patch, animation, parameters) -> effect.play(patch), Side.CLIENT)
      );
   }

   private static YamatoAnimationEffectManager.EffectDefinition period(
      float startTime, float endTime, Supplier<YamatoAnimationEffectManager.ClientEffect> effectFactory
   ) {
      return new YamatoAnimationEffectManager.EffectDefinition(
         YamatoAnimationEffectManager.Stage.PERIOD, YamatoAnimationEffectManager.Channel.TICK, startTime, endTime, () -> {
            YamatoAnimationEffectManager.ClientEffect effect = effectFactory.get();
            return InPeriodEvent.create(startTime, endTime, (patch, animation, parameters) -> effect.play(patch), Side.CLIENT);
         }
      );
   }

   public static final class AerialCleave {
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_GROUND_1 = YamatoAnimationEffectManager.at(
         0.6166667F, patch -> JCEClient.SlashGround(patch, -15.0, 0.5F, -0.3F)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_GROUND_2 = YamatoAnimationEffectManager.at(
         0.6166667F, patch -> JCEClient.SlashGround(patch, -16.0, 0.5F, -0.3F)
      );

      private AerialCleave() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SLASH_GROUND_1, SLASH_GROUND_2);
      }
   }

   public static final class AerialCleaveDash {
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_1 = YamatoAnimationEffectManager.at(
         0.5F, patch -> JCEClient.Slash(patch, -15.0, 0.45F, 0.0, 0.2, 0.5, 0.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_2 = YamatoAnimationEffectManager.at(
         0.5F, patch -> JCEClient.Slash(patch, -80.0, 0.5F, 0.0, 0.2, 0.5, 0.0)
      );

      private AerialCleaveDash() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SLASH_1, SLASH_2);
      }
   }

   public static final class AerialCleaveFast {
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_GROUND_1 = YamatoAnimationEffectManager.at(
         0.36666667F, patch -> JCEClient.SlashGround(patch, -15.0, 0.5F, -0.3F)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_GROUND_2 = YamatoAnimationEffectManager.at(
         0.36666667F, patch -> JCEClient.SlashGround(patch, -16.0, 0.5F, -0.3F)
      );

      private AerialCleaveFast() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SLASH_GROUND_1, SLASH_GROUND_2);
      }
   }

   public static final class AerialRaveComboA1 {
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH_1 = YamatoAnimationEffectManager.at(
         0.06666667F, patch -> JCEClient.FastSlash(patch, -110.0, 0.4F, 0.0, 0.0, 0.0, 0.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH_2 = YamatoAnimationEffectManager.at(
         0.06666667F, patch -> JCEClient.FastSlash(patch, -110.0, 0.4F, 0.0, 0.0, 0.0, -0.05)
      );

      private AerialRaveComboA1() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(FAST_SLASH_1, FAST_SLASH_2);
      }
   }

   public static final class AerialRaveComboA2 {
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH_1 = YamatoAnimationEffectManager.at(
         0.05F, patch -> JCEClient.FastSlash(patch, 37.0, 0.4F)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH_2 = YamatoAnimationEffectManager.at(
         0.05F, patch -> JCEClient.FastSlash(patch, 37.0, 0.4F, 0.0, 0.0, 0.0, -0.05)
      );

      private AerialRaveComboA2() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(FAST_SLASH_1, FAST_SLASH_2);
      }
   }

   public static final class AerialRaveComboA3 {
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_1 = YamatoAnimationEffectManager.at(
         0.18333334F, patch -> JCEClient.Slash(patch, -90.0, 0.5F)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_2 = YamatoAnimationEffectManager.at(
         0.18333334F, patch -> JCEClient.Slash(patch, -88.0, 0.5F)
      );

      private AerialRaveComboA3() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SLASH_1, SLASH_2);
      }
   }

   public static final class AerialRaveComboB1 {
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH_ROUND_1 = YamatoAnimationEffectManager.at(
         0.15F, patch -> JCEClient.FastSlashRound(patch, 160.0, 0.29F, 0.0, 0.25, 0.0, 0.25)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH_ROUND_2 = YamatoAnimationEffectManager.at(
         0.3F, patch -> JCEClient.FastSlashRound(patch, 160.0, 0.29F, 0.0, 0.25, 0.0, 0.25)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH_ROUND_3 = YamatoAnimationEffectManager.at(
         0.45F, patch -> JCEClient.FastSlashRound(patch, 160.0, 0.35F, 0.0, 0.25, 0.0, 0.25)
      );

      private AerialRaveComboB1() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(FAST_SLASH_ROUND_1, FAST_SLASH_ROUND_2, FAST_SLASH_ROUND_3);
      }
   }

   public static final class AerialRaveComboB2 {
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_ADJUST_1 = YamatoAnimationEffectManager.at(
         0.45F, patch -> JCEClient.SlashAdjust(patch, -20.0, 0.5F)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_ADJUST_2 = YamatoAnimationEffectManager.at(
         0.5F, patch -> JCEClient.SlashAdjust(patch, -23.0, 0.5F)
      );

      private AerialRaveComboB2() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SLASH_ADJUST_1, SLASH_ADJUST_2);
      }
   }

   public static enum Channel {
      TICK,
      STATIC_BEGIN,
      STATIC_END,
      ACTION_BEGIN,
      ACTION_END;
   }

   @FunctionalInterface
   private interface ClientEffect {
      void play(LivingEntityPatch<?> var1);
   }

   public static final class ComboA1 {
      public static final YamatoAnimationEffectManager.EffectDefinition SHEATH_SLASH = YamatoAnimationEffectManager.at(
         0.15F, patch -> JCEClient.sheathSlash(patch, -110.0, 0.25F, 0.0, -0.2, 0.0, 0.0)
      );

      private ComboA1() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SHEATH_SLASH);
      }
   }

   public static final class ComboA2 {
      public static final YamatoAnimationEffectManager.EffectDefinition SHEATH_SLASH = YamatoAnimationEffectManager.at(
         0.1F, patch -> JCEClient.sheathSlash(patch, 75.0, 0.25F, 0.0, -0.2, 0.0, 0.0)
      );

      private ComboA2() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SHEATH_SLASH);
      }
   }

   public static final class ComboA3 {
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_1 = YamatoAnimationEffectManager.at(
         0.2F, patch -> JCEClient.Slash(patch, -140.0, 0.3F, 10.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_2 = YamatoAnimationEffectManager.at(
         0.38333333F, patch -> JCEClient.Slash(patch, 60.0, 0.3F, 0.0)
      );

      private ComboA3() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SLASH_1, SLASH_2);
      }
   }

   public static final class ComboA4 {
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_1 = YamatoAnimationEffectManager.at(
         0.43333334F, patch -> JCEClient.Slash(patch, -60.0, 0.4F, 0.0, 0.0, 0.0, 0.3)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_2 = YamatoAnimationEffectManager.at(
         0.46666667F, patch -> JCEClient.FastSlashRound(patch, -60.0, 0.4F, 0.0, 0.0, 0.0, 0.3)
      );

      private ComboA4() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SLASH_1, SLASH_2);
      }
   }

   public static final class ComboA4Sdt {
      public static final YamatoAnimationEffectManager.EffectDefinition SLOW_SLASH = YamatoAnimationEffectManager.at(
         0.5F, patch -> JCEClient.SlowSlash(patch, -15.0, 0.75F, 0.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_ADJUST = YamatoAnimationEffectManager.at(
         0.8F, patch -> JCEClient.SlashAdjust(patch, 165.0, 0.75F, 0.0, -1.0F, 0.0, 0.0)
      );

      private ComboA4Sdt() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SLOW_SLASH, SLASH_ADJUST);
      }
   }

   public static final class ComboA5Sdt {
      public static final YamatoAnimationEffectManager.EffectDefinition SLOW_SLASH_1 = YamatoAnimationEffectManager.at(
         0.8833333F, patch -> JCEClient.SlowSlash(patch, -47.0, 0.8F, 0.0, 0.3, 0.7, 0.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition SLOW_SLASH_2 = YamatoAnimationEffectManager.at(
         0.9166667F, patch -> JCEClient.SlowSlash(patch, -47.0, 0.8F, 0.0, 0.3, 0.7, 0.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition SLOW_SLASH_3 = YamatoAnimationEffectManager.at(1.1666666F, patch -> {
      });

      private ComboA5Sdt() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SLOW_SLASH_1, SLOW_SLASH_2, SLOW_SLASH_3);
      }
   }

   public static final class ComboB1 {
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_ADJUST = YamatoAnimationEffectManager.at(
         0.4F, patch -> JCEClient.Slash(patch, -130.0, 0.45F, 0.0, -0.15F, 0.3, -0.3)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_ADJUST1 = YamatoAnimationEffectManager.at(
         0.45F, patch -> JCEClient.FastSlashRound(patch, -130.0, 0.45F, 0.0, -0.15F, 0.6, -0.15)
      );

      private ComboB1() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SLASH_ADJUST, SLASH_ADJUST1);
      }
   }

   public static final class ComboB2Sdt {
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH = YamatoAnimationEffectManager.at(
         0.28333333F, patch -> JCEClient.Slash(patch, 0.0, 0.6F)
      );

      private ComboB2Sdt() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SLASH);
      }
   }

   public static final class ComboCEnd {
      public static final YamatoAnimationEffectManager.EffectDefinition SDT_AWARE_SLASH_1 = YamatoAnimationEffectManager.at(0.53333336F, patch -> {
         if (patch instanceof PlayerPatch<?> playerPatch) {
            if (SinDevilTriggerManager.isPlayerInSDT((Player)playerPatch.getOriginal())) {
               JCEClient.Slash(patch, -70.0, 0.55F, 0.0, 0.2, 0.0, 0.8);
            } else {
               JCEClient.Slash(patch, -63.0, 0.45F, 0.0, 0.0, 0.0, 0.6);
            }
         } else if (patch instanceof DoppelgangerPatch doppelgangerPatch) {
            PlayerPatch<?> ownerPatch = doppelgangerPatch.getOwnerPatch();
            if (ownerPatch != null && SinDevilTriggerManager.isPlayerInSDT((Player)ownerPatch.getOriginal())) {
               JCEClient.Slash(patch, -70.0, 0.55F, 0.0, 0.2, 0.0, 0.8);
            } else {
               JCEClient.Slash(patch, -63.0, 0.45F, 0.0, 0.0, 0.0, 0.6);
            }
         } else {
            JCEClient.Slash(patch, -70.0, 0.55F, 0.0, 0.2, 0.0, 0.8);
         }
      });
      public static final YamatoAnimationEffectManager.EffectDefinition SDT_AWARE_SLASH_2 = YamatoAnimationEffectManager.at(0.5833333F, patch -> {
         if (patch instanceof PlayerPatch<?> playerPatch) {
            if (SinDevilTriggerManager.isPlayerInSDT((Player)playerPatch.getOriginal())) {
               JCEClient.Slash(patch, -73.0, 0.55F, 0.0, 0.2, 0.0, 0.85);
            } else {
               JCEClient.Slash(patch, -66.0, 0.45F, 0.0, 0.0, 0.0, 0.65);
            }
         } else if (patch instanceof DoppelgangerPatch doppelgangerPatch) {
            PlayerPatch<?> ownerPatch = doppelgangerPatch.getOwnerPatch();
            if (ownerPatch != null && SinDevilTriggerManager.isPlayerInSDT((Player)ownerPatch.getOriginal())) {
               JCEClient.Slash(patch, -73.0, 0.55F, 0.0, 0.2, 0.0, 0.85);
            } else {
               JCEClient.Slash(patch, -66.0, 0.45F, 0.0, 0.0, 0.0, 0.65);
            }
         } else {
            JCEClient.Slash(patch, -73.0, 0.55F, 0.0, 0.2, 0.0, 0.85);
         }
      });

      private ComboCEnd() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SDT_AWARE_SLASH_1, SDT_AWARE_SLASH_2);
      }
   }

   public static final class ComboCLoop {
      public static final YamatoAnimationEffectManager.EffectDefinition COMBO_C_SLASH_1 = YamatoAnimationEffectManager.at(
         0.016666668F, patch -> JCEClient.ComboCSlash(patch, -90.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition COMBO_C_SLASH_2 = YamatoAnimationEffectManager.at(
         0.06666667F, patch -> JCEClient.ComboCSlash(patch, true, -90.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition COMBO_C_SLASH_3 = YamatoAnimationEffectManager.at(
         0.18333334F, patch -> JCEClient.ComboCSlash(patch, -106.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition COMBO_C_SLASH_4 = YamatoAnimationEffectManager.at(
         0.25F, patch -> JCEClient.ComboCSlash(patch, true, -106.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition COMBO_C_SLASH_5 = YamatoAnimationEffectManager.at(
         0.33333334F, patch -> JCEClient.ComboCSlash(patch, 45.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition COMBO_C_SLASH_7 = YamatoAnimationEffectManager.at(
         0.51666665F, patch -> JCEClient.ComboCSlash(patch, -56.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition COMBO_C_SLASH_8 = YamatoAnimationEffectManager.at(
         0.56666666F, patch -> JCEClient.ComboCSlash(patch, true, -56.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition COMBO_C_SLASH_9 = YamatoAnimationEffectManager.at(
         0.6F, patch -> JCEClient.ComboCSlash(patch, -20.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition COMBO_C_SLASH_6 = YamatoAnimationEffectManager.at(
         0.6333333F, patch -> JCEClient.ComboCSlash(patch, true, 55.0)
      );

      private ComboCLoop() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(
            COMBO_C_SLASH_1,
            COMBO_C_SLASH_2,
            COMBO_C_SLASH_3,
            COMBO_C_SLASH_4,
            COMBO_C_SLASH_5,
            COMBO_C_SLASH_6,
            COMBO_C_SLASH_7,
            COMBO_C_SLASH_8,
            COMBO_C_SLASH_9
         );
      }
   }

   public static final class ComboCStart {
      public static final YamatoAnimationEffectManager.EffectDefinition COMBO_C_SLASH_1 = YamatoAnimationEffectManager.at(
         0.51666665F, patch -> JCEClient.ComboCSlash(patch, -90.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition COMBO_C_SLASH_2 = YamatoAnimationEffectManager.at(
         0.6166667F, patch -> JCEClient.ComboCSlash(patch, true, -90.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition COMBO_C_SLASH_3 = YamatoAnimationEffectManager.at(
         0.71666664F, patch -> JCEClient.ComboCSlash(patch, -106.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition COMBO_C_SLASH_4 = YamatoAnimationEffectManager.at(
         0.78333336F, patch -> JCEClient.ComboCSlash(patch, true, -106.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition COMBO_C_SLASH_5 = YamatoAnimationEffectManager.at(
         0.8666667F, patch -> JCEClient.ComboCSlash(patch, 45.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition COMBO_C_SLASH_7 = YamatoAnimationEffectManager.at(
         1.0F, patch -> JCEClient.ComboCSlash(patch, -56.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition COMBO_C_SLASH_8 = YamatoAnimationEffectManager.at(
         1.0666667F, patch -> JCEClient.ComboCSlash(patch, true, -56.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition COMBO_C_SLASH_9 = YamatoAnimationEffectManager.at(
         1.1F, patch -> JCEClient.ComboCSlash(patch, -20.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition COMBO_C_SLASH_6 = YamatoAnimationEffectManager.at(
         1.1333333F, patch -> JCEClient.ComboCSlash(patch, true, 55.0)
      );

      private ComboCStart() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(
            COMBO_C_SLASH_1,
            COMBO_C_SLASH_2,
            COMBO_C_SLASH_3,
            COMBO_C_SLASH_4,
            COMBO_C_SLASH_5,
            COMBO_C_SLASH_6,
            COMBO_C_SLASH_7,
            COMBO_C_SLASH_8,
            COMBO_C_SLASH_9
         );
      }
   }

   public static final class DodgeCounter {
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_1 = YamatoAnimationEffectManager.at(
         0.2F, patch -> JCEClient.Slash(patch, -140.0, 0.3F, 10.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_2 = YamatoAnimationEffectManager.at(
         0.38333333F, patch -> JCEClient.Slash(patch, 60.0, 0.3F, 0.0)
      );

      private DodgeCounter() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SLASH_1, SLASH_2);
      }
   }

   public static record EffectDefinition(
      YamatoAnimationEffectManager.Stage stage,
      YamatoAnimationEffectManager.Channel channel,
      float startTime,
      float endTime,
      Supplier<AnimationEvent<?, ?>> eventFactory
   ) {
      public AnimationEvent<?, ?> event() {
         return this.eventFactory.get();
      }

      public SimpleEvent<?> simpleEvent() {
         return (SimpleEvent<?>)this.eventFactory.get();
      }
   }

   public static final class JudgementCutAir {
      public static final YamatoAnimationEffectManager.EffectDefinition JUDGEMENT_CUT = YamatoAnimationEffectManager.at(
         0.083333336F, patch -> JCEClient.JC(patch)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH = YamatoAnimationEffectManager.at(
         0.13333334F, patch -> JCEClient.FastSlash(patch, -110.0, 0.15F, 0.0, -0.5, 0.0, -0.5)
      );

      private JudgementCutAir() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(JUDGEMENT_CUT, FAST_SLASH);
      }
   }

   public static final class JudgementCutAirFs {
      public static final YamatoAnimationEffectManager.EffectDefinition JUDGEMENT_CUT = YamatoAnimationEffectManager.at(0.05F, patch -> JCEClient.JC(patch));
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH = YamatoAnimationEffectManager.at(
         0.083333336F, patch -> JCEClient.FastSlash(patch, -110.0, 0.15F, 0.0, -0.5, 0.0, -0.5)
      );

      private JudgementCutAirFs() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(JUDGEMENT_CUT, FAST_SLASH);
      }
   }

   public static final class JudgementCutEnd {
      public static final YamatoAnimationEffectManager.EffectDefinition BEGIN_EFFECTS = YamatoAnimationEffectManager.begin(patch -> {
         JCEClient.beginUiHide(patch);
         JCEClient.prev(patch);
      });
      public static final YamatoAnimationEffectManager.EffectDefinition END_EFFECTS = YamatoAnimationEffectManager.actionEnd(
         patch -> JCEClient.endEffects(patch)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition JCE_FIRE_PERIOD = YamatoAnimationEffectManager.period(0.0F, 1.6666666F, () -> {
         int[] fireTickCounter = new int[]{0};
         return patch -> {
            fireTickCounter[0]++;
            if (fireTickCounter[0] % 5 == 0) {
               JCEClient.JCEFire(patch);
            }
         };
      });
      public static final YamatoAnimationEffectManager.EffectDefinition HANDLE_ATTACK_2 = YamatoAnimationEffectManager.at(
         1.6833333F, patch -> JCEClient.HandleAtk2(patch)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition HANDLE_ATTACK_1 = YamatoAnimationEffectManager.at(
         1.7666667F, patch -> JCEClient.HandleAtk1(patch)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition POST_EFFECT_1 = YamatoAnimationEffectManager.at(
         1.7833333F, patch -> JCEClient.post1(patch)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition POST_EFFECT_2 = YamatoAnimationEffectManager.at(1.9F, patch -> JCEClient.post2(patch));
      public static final YamatoAnimationEffectManager.EffectDefinition POST_EFFECT_4 = YamatoAnimationEffectManager.at(3.2F, patch -> JCEClient.post4(patch));
      public static final YamatoAnimationEffectManager.EffectDefinition POST_EFFECT_3 = YamatoAnimationEffectManager.at(4.9F, patch -> JCEClient.post3(patch));

      private JudgementCutEnd() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(
            BEGIN_EFFECTS, END_EFFECTS, JCE_FIRE_PERIOD, HANDLE_ATTACK_2, HANDLE_ATTACK_1, POST_EFFECT_1, POST_EFFECT_2, POST_EFFECT_4, POST_EFFECT_3
         );
      }
   }

   public static final class JudgementCutEndDmc3 {
      public static final YamatoAnimationEffectManager.EffectDefinition BEGIN_UI_HIDE = YamatoAnimationEffectManager.begin(
         patch -> JCEClient.beginUiHide(patch)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition END_UI_HIDE = YamatoAnimationEffectManager.staticEnd(
         patch -> JCEClient.endUiHide(patch)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition POST_EFFECT_3 = YamatoAnimationEffectManager.at(
         3.2333333F, patch -> JCEClient.post3(patch)
      );

      private JudgementCutEndDmc3() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(BEGIN_UI_HIDE, END_UI_HIDE, POST_EFFECT_3);
      }
   }

   public static final class JudgementCutEndInstant {
      public static final YamatoAnimationEffectManager.EffectDefinition BEGIN_EFFECTS = YamatoAnimationEffectManager.begin(patch -> {
         JCEClient.beginUiHide(patch);
         JCEClient.prev2(patch);
      });
      public static final YamatoAnimationEffectManager.EffectDefinition END_EFFECTS = YamatoAnimationEffectManager.actionEnd(
         patch -> JCEClient.endEffects(patch)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition HANDLE_ATTACK_2 = YamatoAnimationEffectManager.at(
         0.016666668F, patch -> JCEClient.HandleAtk2(patch)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition HANDLE_ATTACK_1 = YamatoAnimationEffectManager.at(
         0.1F, patch -> JCEClient.HandleAtk1(patch)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition POST_EFFECT_1 = YamatoAnimationEffectManager.at(
         0.11666667F, patch -> JCEClient.post1(patch)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition POST_EFFECT_2 = YamatoAnimationEffectManager.at(
         0.23333333F, patch -> JCEClient.post2(patch)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition POST_EFFECT_4 = YamatoAnimationEffectManager.at(
         1.5333333F, patch -> JCEClient.post4(patch)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition POST_EFFECT_3 = YamatoAnimationEffectManager.at(
         3.2333333F, patch -> JCEClient.post3(patch)
      );

      private JudgementCutEndInstant() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(BEGIN_EFFECTS, END_EFFECTS, HANDLE_ATTACK_2, HANDLE_ATTACK_1, POST_EFFECT_1, POST_EFFECT_2, POST_EFFECT_4, POST_EFFECT_3);
      }
   }

   public static final class JudgementCutGround {
      public static final YamatoAnimationEffectManager.EffectDefinition JUDGEMENT_CUT = YamatoAnimationEffectManager.at(0.75F, patch -> JCEClient.JC(patch));
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH = YamatoAnimationEffectManager.at(
         0.8F, patch -> JCEClient.FastSlash(patch, -110.0, 0.15F, 0.0, -0.5, 0.0, -0.5)
      );

      private JudgementCutGround() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(JUDGEMENT_CUT, FAST_SLASH);
      }
   }

   public static final class JudgementCutGroundFs {
      public static final YamatoAnimationEffectManager.EffectDefinition JUDGEMENT_CUT = YamatoAnimationEffectManager.at(0.05F, patch -> JCEClient.JC(patch));
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH = YamatoAnimationEffectManager.at(
         0.083333336F, patch -> JCEClient.FastSlash(patch, -110.0, 0.15F, 0.0, -0.5, 0.0, -0.5)
      );

      private JudgementCutGroundFs() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(JUDGEMENT_CUT, FAST_SLASH);
      }
   }

   public static final class ProvocationBAerial {
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH = YamatoAnimationEffectManager.at(
         0.13333334F, patch -> JCEClient.FastSlash(patch, -110.0, 0.35F, 0.0, -0.5, 0.0, -0.25)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition JUDGEMENT_CUT = YamatoAnimationEffectManager.at(
         0.083333336F, patch -> JCEClient.JC(patch)
      );

      private ProvocationBAerial() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(FAST_SLASH, JUDGEMENT_CUT);
      }
   }

   public static final class ProvocationC {
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_1 = YamatoAnimationEffectManager.at(
         13.533334F, patch -> JCEClient.Slash(patch, -70.0, 0.4F, 0.0, 0.2, -0.5, 0.5)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_2 = YamatoAnimationEffectManager.at(
         13.55F, patch -> JCEClient.Slash(patch, -73.0, 0.4F, 0.0, 0.2, -0.5, 0.5)
      );

      private ProvocationC() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SLASH_1, SLASH_2);
      }
   }

   public static final class ProvocationPortal {
      public static final YamatoAnimationEffectManager.EffectDefinition DOOR_2 = YamatoAnimationEffectManager.at(
         0.81666666F, patch -> JCEClient.Door2(patch, (float) (Math.PI / 2))
      );
      public static final YamatoAnimationEffectManager.EffectDefinition DOOR_1 = YamatoAnimationEffectManager.at(
         1.5166667F, patch -> JCEClient.Door1(patch, 0.0F)
      );

      private ProvocationPortal() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(DOOR_2, DOOR_1);
      }
   }

   public static final class RapidSlash {
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_1 = YamatoAnimationEffectManager.at(
         0.033333335F, patch -> JCEClient.Slash(patch, -47.0, 0.35F, 0.0, -0.3, 0.0, -0.3)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH_ROUND_1 = YamatoAnimationEffectManager.at(
         0.05F, patch -> JCEClient.FastSlashRound(patch, -80.0, 0.35F, 0.0, 0.0, 1.0, 0.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_2 = YamatoAnimationEffectManager.at(
         0.15F, patch -> JCEClient.Slash(patch, 47.0, 0.35F, 0.0, -0.3, 3.0, 0.3)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH_ROUND_2 = YamatoAnimationEffectManager.at(
         0.16666667F, patch -> JCEClient.FastSlashRound(patch, 80.0, 0.35F, 0.0, 0.0, 1.0, 0.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH_ROUND_3 = YamatoAnimationEffectManager.at(
         0.28333333F, patch -> JCEClient.FastSlashRound(patch, -80.0, 0.35F, 0.0, 0.0, 1.0, 0.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition SLOW_SLASH = YamatoAnimationEffectManager.at(
         0.51666665F, patch -> JCEClient.SlowSlash(patch, -60.0, 0.45F, 0.0, 0.2, 0.0, 0.5)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH_ROUND_4 = YamatoAnimationEffectManager.at(
         0.5833333F, patch -> JCEClient.FastSlashRound(patch, -60.0, 0.45F, 0.0, 0.2, 0.0, 0.5)
      );

      private RapidSlash() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SLASH_1, FAST_SLASH_ROUND_1, SLASH_2, FAST_SLASH_ROUND_2, FAST_SLASH_ROUND_3, SLOW_SLASH, FAST_SLASH_ROUND_4);
      }
   }

   public static final class RapidSlashAir {
      public static final YamatoAnimationEffectManager.EffectDefinition SLOW_SLASH_1 = YamatoAnimationEffectManager.at(
         0.2F, patch -> JCEClient.SlowSlash(patch, -60.0, 0.3F, 0.0, 0.2, 0.0, 0.3)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition SLOW_SLASH_2 = YamatoAnimationEffectManager.at(
         0.21666667F, patch -> JCEClient.SlowSlash(patch, -63.0, 0.3F, 0.0, 0.2, 0.0, 0.35)
      );

      private RapidSlashAir() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SLOW_SLASH_1, SLOW_SLASH_2);
      }
   }

   public static final class RapidSlashRe {
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH = YamatoAnimationEffectManager.at(
         0.0F, patch -> JCEClient.Slash(patch, 47.0, 0.35F, 0.0, -0.3, 0.5, 0.3)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH_ROUND_1 = YamatoAnimationEffectManager.at(
         0.0F, patch -> JCEClient.FastSlashRound(patch, 80.0, 0.35F, 0.0, 0.0, 1.0, 0.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH_ROUND_2 = YamatoAnimationEffectManager.at(
         0.13333334F, patch -> JCEClient.FastSlashRound(patch, -80.0, 0.35F, 0.0, 0.0, 1.0, 0.0)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition SLOW_SLASH = YamatoAnimationEffectManager.at(
         0.36666667F, patch -> JCEClient.SlowSlash(patch, -60.0, 0.45F, 0.0, 0.2, 0.0, 0.5)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH_ROUND_3 = YamatoAnimationEffectManager.at(
         0.43333334F, patch -> JCEClient.FastSlashRound(patch, -60.0, 0.45F, 0.0, 0.2, 0.0, 0.5)
      );

      private RapidSlashRe() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SLASH, FAST_SLASH_ROUND_1, FAST_SLASH_ROUND_2, SLOW_SLASH, FAST_SLASH_ROUND_3);
      }
   }

   public static final class RisingStar {
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH_ROUND_1 = YamatoAnimationEffectManager.at(
         0.016666668F, patch -> JCEClient.FastSlashRound(patch, -150.0, 0.4F, 0.0, 2.5, 0.0, 0.2)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH_ROUND_2 = YamatoAnimationEffectManager.at(
         0.31666666F, patch -> JCEClient.FastSlashRound(patch, -150.0, 0.4F, 0.0, 2.5, 0.0, 0.2)
      );

      private RisingStar() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(FAST_SLASH_ROUND_1, FAST_SLASH_ROUND_2);
      }
   }

   public static enum Stage {
      BEGIN,
      TIMELINE,
      PERIOD,
      END;
   }

   public static final class Strike {
      public static final YamatoAnimationEffectManager.EffectDefinition SLOW_SLASH_1 = YamatoAnimationEffectManager.at(
         0.28333333F, patch -> JCEClient.SlowSlash(patch, -60.0, 0.3F, 0.0, 0.2, 0.0, 0.3)
      );
      public static final YamatoAnimationEffectManager.EffectDefinition SLOW_SLASH_2 = YamatoAnimationEffectManager.at(
         0.31666666F, patch -> JCEClient.SlowSlash(patch, -63.0, 0.3F, 0.0, 0.2, 0.0, 0.35)
      );

      private Strike() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SLOW_SLASH_1, SLOW_SLASH_2);
      }
   }

   public static final class UpperSlash1 {
      public static final YamatoAnimationEffectManager.EffectDefinition SLASH_1 = YamatoAnimationEffectManager.at(
         0.35F, patch -> JCEClient.SlashAdjust(patch, -170.0, 0.5F, 0.0, -1.0F, 0.35, -0.03)
      );

      private UpperSlash1() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(SLASH_1);
      }
   }

   public static final class UpperSlash2 {
      public static final YamatoAnimationEffectManager.EffectDefinition FAST_SLASH_1 = YamatoAnimationEffectManager.at(
         0.1F, patch -> JCEClient.FastSlash(patch, -170.0, 0.55F, 0.0, 1.0, 0.0, -0.25)
      );

      private UpperSlash2() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(FAST_SLASH_1);
      }
   }

   public static final class VoidSlash {
      public static final YamatoAnimationEffectManager.EffectDefinition VOID_SLASH = YamatoAnimationEffectManager.at(0.85F, patch -> JCEClient.VoidSlash(patch));

      private VoidSlash() {
      }

      public static List<YamatoAnimationEffectManager.EffectDefinition> effects() {
         return List.of(VOID_SLASH);
      }
   }
}
