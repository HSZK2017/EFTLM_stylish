package com.pla.annoyingvillagers.mixin;

import com.pla.annoyingvillagers.animations.BowAttackAnimation;
import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.clazz.HookDisarmLaunch;
import com.pla.annoyingvillagers.compat.EfKick;
import com.pla.annoyingvillagers.compat.EpicFightNightFall;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.entity.AegisHerobrineEntity;
import com.pla.annoyingvillagers.entity.GlaiveHerobrineEntity;
import com.pla.annoyingvillagers.entity.HerobrineChrisEntity;
import com.pla.annoyingvillagers.entity.ReaperHerobrineEntity;
import com.pla.annoyingvillagers.entity.ShadowHerobrineEntity;
import com.pla.annoyingvillagers.entity.SledgehammerHerobrineEntity;
import com.pla.annoyingvillagers.entity.SwordsmanHerobrineEntity;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFight;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import com.pla.annoyingvillagers.item.FlankerHookedSwordItem;
import com.pla.annoyingvillagers.item.HookedDiamondSwordItem;
import com.pla.annoyingvillagers.item.HookedGoldenSwordItem;
import com.pla.annoyingvillagers.item.HookedIronSwordItem;
import com.pla.annoyingvillagers.util.CommonUtil;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import com.pla.annoyingvillagers.util.EscapeUtil;
import com.pla.efclash_blade.event.MobClashBladeEvent;
import java.util.Objects;
import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.animations.weapons.AnimsAgony;
import reascer.wom.gameasset.animations.weapons.AnimsMoonless;
import reascer.wom.gameasset.animations.weapons.AnimsSolar;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(
   value = {MobClashBladeEvent.class},
   remap = false
)
public class MobClashBladeMixin {
   @Inject(
      method = {"customAdditionClashBladeLogic"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void addMoreClashBladeCondition(
      LivingAttackEvent livingAttackEvent,
      LivingEntityPatch<?> defenderLivingEntityPatch,
      AssetAccessor<? extends StaticAnimation> defenderDynamicAnimation,
      EntityState defenderEntityState,
      Entity attacker,
      Entity defender,
      CallbackInfoReturnable<Boolean> cir
   ) {
      if (EpicfightUtil.isLongHitAnimation(defenderDynamicAnimation, defenderLivingEntityPatch)) {
         cir.setReturnValue(false);
      } else if (defender instanceof AegisHerobrineEntity
         && defenderDynamicAnimation == AnimsEpicFight.AEGIS_SHIELD_SHOOT
         && defenderEntityState.getLevel() == 3) {
         cir.setReturnValue(true);
      } else if (defender instanceof AegisHerobrineEntity && defenderDynamicAnimation == AnimsWom.ENDER_AEGIS_NAPOLEON_RELOAD_1) {
         cir.setReturnValue(true);
      } else if (defender instanceof SwordsmanHerobrineEntity && defenderDynamicAnimation == WOMAnimations.TORMENT_BERSERK_CONVERT) {
         cir.setReturnValue(true);
      } else if (defender instanceof GlaiveHerobrineEntity && defenderDynamicAnimation == AnimsWom.AGONY_GUARD_HIT_1) {
         cir.setReturnValue(true);
      } else if (defender instanceof SledgehammerHerobrineEntity && defenderDynamicAnimation == WOMAnimations.TORMENT_BERSERK_CONVERT) {
         cir.setReturnValue(true);
      } else if (defender instanceof HerobrineChrisEntity && defenderDynamicAnimation == AnimsMoonless.MOONLESS_GUARD_HIT_1) {
         cir.setReturnValue(true);
      }
   }

   @Inject(
      method = {"customPreAdditionClashBlade"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void customLogicBeforeClashing(
      LivingAttackEvent livingAttackEvent,
      LivingEntityPatch<?> defenderLivingEntityPatch,
      AssetAccessor<? extends StaticAnimation> defenderDynamicAnimation,
      EntityState defenderEntityState,
      Entity attacker,
      Entity defender,
      int clashBy,
      CallbackInfo ci
   ) {
      if (defender instanceof LivingEntity livingEntity && defender.m_9236_() instanceof ServerLevel serverLevel && clashBy != 0) {
         if (ModList.get().isLoaded("efn")) {
            if (defender instanceof AegisHerobrineEntity
               || defender instanceof GlaiveHerobrineEntity
               || defender instanceof SledgehammerHerobrineEntity
               || defender instanceof ReaperHerobrineEntity
               || defender instanceof SwordsmanHerobrineEntity
               || defender instanceof ShadowHerobrineEntity) {
               HerobrineMob herobrineMob = (HerobrineMob)defender;
               if (herobrineMob.getLivingEntityPatch() != null) {
                  EpicFightNightFall.playEfnGuardHit(herobrineMob.getLivingEntityPatch(), herobrineMob.getEfnGuardHitState(), livingAttackEvent.getSource());
                  herobrineMob.postPlayEfnGuardHit();
               }
            }
         } else {
            if (defender instanceof AegisHerobrineEntity
               || defender instanceof GlaiveHerobrineEntity
               || defender instanceof SledgehammerHerobrineEntity
               || defender instanceof ReaperHerobrineEntity) {
               defenderLivingEntityPatch.playAnimationSynchronized(AnimsAgony.AGONY_GUARD_HIT_1, 0.0F);
            }

            if (defender instanceof SwordsmanHerobrineEntity) {
               defenderLivingEntityPatch.playAnimationSynchronized(AnimsSolar.SOLAR_GUARD_HIT, 0.0F);
            }
         }
      }
   }

   @Inject(
      method = {"blacklistClashBladeAnimation"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void rejectClashBladeFromAnimationsCondition(
      LivingAttackEvent livingAttackEvent,
      LivingEntityPatch<?> defenderLivingEntityPatch,
      AssetAccessor<? extends StaticAnimation> defenderDynamicAnimation,
      EntityState defenderEntityState,
      Entity attacker,
      Entity defender,
      CallbackInfoReturnable<Boolean> cir
   ) {
      if (defenderDynamicAnimation.get() instanceof BowAttackAnimation) {
         cir.setReturnValue(false);
      } else {
         if (defender instanceof LivingEntity livingDefender
            && CommonUtil.isHookSword(livingDefender.m_21205_())
            && CommonUtil.isHookSwordClashAnimation(defenderDynamicAnimation)) {
            cir.setReturnValue(false);
         }
      }
   }

   @Inject(
      method = {"customPostAdditionClashBlade"},
      at = {@At("HEAD")}
   )
   private static void moreLogicAfterClashing(
      LivingAttackEvent livingAttackEvent,
      LivingEntityPatch<?> defenderLivingEntityPatch,
      AssetAccessor<? extends StaticAnimation> defenderDynamicAnimation,
      EntityState defenderEntityState,
      Entity attacker,
      Entity defender,
      int clashBy,
      CallbackInfo ci
   ) {
      if (defender.m_9236_() instanceof ServerLevel serverLevel) {
         if (attacker instanceof LivingEntity livingAttacker
            && defender instanceof LivingEntity defenderLivingDefender
            && (
               defenderLivingDefender.m_21205_().m_41720_() instanceof HookedIronSwordItem
                  || defenderLivingDefender.m_21205_().m_41720_() instanceof HookedGoldenSwordItem
                  || defenderLivingDefender.m_21205_().m_41720_() instanceof HookedDiamondSwordItem
                  || defenderLivingDefender.m_21205_().m_41720_() instanceof FlankerHookedSwordItem
            )) {
            if (defenderDynamicAnimation == AnimsEpicFight.HOOK_AXE_AUTO1) {
               CommonUtil.applyHookClashDisarmLogic(
                  defenderLivingDefender, livingAttacker, serverLevel, AnimsPugilistSteve.KNOCKDOWN_RIGHT, HookDisarmLaunch.RIGHT
               );
            }

            if (defenderDynamicAnimation == AnimsEpicFight.HOOK_AXE_AUTO2) {
               CommonUtil.applyHookClashDisarmLogic(
                  defenderLivingDefender, livingAttacker, serverLevel, AnimsPugilistSteve.KNOCKDOWN_LEFT, HookDisarmLaunch.LEFT
               );
            }

            if (defenderDynamicAnimation == AnimsEpicFight.HOOK_DANCING_EDGE || defenderDynamicAnimation == AnimsWom.HOOK_HERRSCHER_UP) {
               CommonUtil.applyHookClashDisarmLogic(
                  defenderLivingDefender, livingAttacker, serverLevel, AnimsPugilistSteve.GUARD_BREAK_ATTACK, HookDisarmLaunch.BACKWARD
               );
            }
         }

         LivingEntityPatch<?> attackerLivingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(attacker, LivingEntityPatch.class);
         if (clashBy == 0 && attackerLivingEntityPatch != null) {
            AssetAccessor<? extends StaticAnimation> attackerDynamicAnimation = Objects.requireNonNull(
                  attackerLivingEntityPatch.getAnimator().getPlayerFor(null)
               )
               .getRealAnimation();
            if (attackerDynamicAnimation != null) {
               if (defender instanceof ServerPlayer serverPlayer
                  && EscapeUtil.isAnimationDangerous(attackerDynamicAnimation)
                  && CommonUtil.isAvDamageableEfnWeaponsMob(attacker)) {
                  boolean damaged = false;
                  int breakValue = (Integer)AnnoyingVillagersConfig.WEAPON_BREAKING_MECHANISM_VALUE.get();
                  if (ModList.get().isLoaded("efn") && EpicFightNightFall.isEfnWeapons(serverPlayer.m_21205_())) {
                     breakValue = (Integer)AnnoyingVillagersConfig.WEAPON_BREAKING_MECHANISM_VALUE.get() * 5;
                  }

                  if ((serverPlayer.m_21206_().m_41720_() instanceof SwordItem || serverPlayer.m_21206_().m_41720_() instanceof AxeItem)
                     && new Random().nextBoolean()) {
                     damaged = true;
                     serverPlayer.m_21206_().m_41622_(breakValue, serverPlayer, player -> player.m_21190_(InteractionHand.OFF_HAND));
                  }

                  if (!damaged) {
                     serverPlayer.m_21205_().m_41622_(breakValue, serverPlayer, player -> player.m_21190_(InteractionHand.MAIN_HAND));
                  }
               }

               if (ModList.get().isLoaded("efkick")) {
                  EfKick.tryDealKickStaminaDamage(livingAttackEvent.getSource(), attackerLivingEntityPatch, attackerDynamicAnimation);
               }
            }
         }
      }
   }
}
