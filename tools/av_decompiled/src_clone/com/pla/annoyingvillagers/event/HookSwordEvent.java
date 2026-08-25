package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.clazz.HookDisarmLaunch;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFight;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import com.pla.annoyingvillagers.util.CommonUtil;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.FORGE
)
public class HookSwordEvent {
   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public static void onLivingDamage(LivingDamageEvent event) {
      if (!ModList.get().isLoaded("efclash_blade")) {
         if (event.isCanceled() || event.getAmount() <= 0.0F) {
            return;
         }

         LivingEntity defender = event.getEntity();
         if (!(defender.m_9236_() instanceof ServerLevel serverLevel)) {
            return;
         }

         if (!CommonUtil.isHookSword(defender.m_21205_())) {
            return;
         }

         DamageSource damageSource = event.getSource();
         if (isIgnoredDamageSource(damageSource)) {
            return;
         }

         LivingEntity attacker = getLivingAttacker(damageSource, defender);
         if (attacker == null || !attacker.m_6084_() || !isAttackerInFront(defender, attacker)) {
            return;
         }

         LivingEntityPatch<?> defenderPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(defender, LivingEntityPatch.class);
         if (defenderPatch == null) {
            return;
         }

         AnimationPlayer animationPlayer = defenderPatch.getAnimator().getPlayerFor(null);
         if (animationPlayer == null) {
            return;
         }

         AssetAccessor<? extends StaticAnimation> defenderAnimation = animationPlayer.getRealAnimation();
         if (!isValidHookClash(defenderPatch, animationPlayer, defenderAnimation)) {
            return;
         }

         event.setAmount(0.0F);
         event.setCanceled(true);
         EpicfightUtil.damageBlockedForce(defender, attacker, serverLevel);
         CommonUtil.applyHookClashDisarmLogic(defender, attacker, serverLevel, getKnockdownAnimation(defenderAnimation), getLaunchDirection(defenderAnimation));
      }
   }

   private static boolean isValidHookClash(
      LivingEntityPatch<?> defenderPatch, AnimationPlayer animationPlayer, AssetAccessor<? extends StaticAnimation> defenderAnimation
   ) {
      if (!CommonUtil.isHookSwordClashAnimation(defenderAnimation)) {
         return false;
      } else if (!(defenderAnimation.get() instanceof AttackAnimation)) {
         return false;
      } else {
         EntityState entityState = ((StaticAnimation)defenderAnimation.get()).getState(defenderPatch, animationPlayer.getElapsedTime());
         return entityState.getLevel() < 3;
      }
   }

   private static LivingEntity getLivingAttacker(DamageSource damageSource, LivingEntity defender) {
      if (damageSource.m_7639_() instanceof LivingEntity livingAttacker && livingAttacker != defender) {
         return livingAttacker;
      }

      if (damageSource.m_7640_() instanceof LivingEntity livingAttacker && livingAttacker != defender) {
         return livingAttacker;
      }

      return null;
   }

   private static boolean isAttackerInFront(LivingEntity defender, LivingEntity attacker) {
      Vec3 toAttacker = attacker.m_20182_().m_82546_(defender.m_146892_());
      return toAttacker.m_82556_() < 1.0E-7 ? false : toAttacker.m_82541_().m_82526_(defender.m_20252_(1.0F)) > 0.0;
   }

   private static boolean isIgnoredDamageSource(DamageSource damageSource) {
      return damageSource.m_276093_(DamageTypes.f_268515_)
         || damageSource.m_269533_(DamageTypeTags.f_268415_)
         || damageSource.m_276093_(DamageTypes.f_268468_)
         || damageSource.m_276093_(DamageTypes.f_268631_)
         || damageSource.m_276093_(DamageTypes.f_268671_);
   }

   private static AssetAccessor<? extends StaticAnimation> getKnockdownAnimation(AssetAccessor<? extends StaticAnimation> defenderAnimation) {
      if (defenderAnimation == AnimsEpicFight.HOOK_AXE_AUTO1) {
         return AnimsPugilistSteve.KNOCKDOWN_RIGHT;
      } else {
         return defenderAnimation == AnimsEpicFight.HOOK_AXE_AUTO2 ? AnimsPugilistSteve.KNOCKDOWN_LEFT : AnimsPugilistSteve.GUARD_BREAK_ATTACK;
      }
   }

   private static HookDisarmLaunch getLaunchDirection(AssetAccessor<? extends StaticAnimation> defenderAnimation) {
      if (defenderAnimation == AnimsEpicFight.HOOK_AXE_AUTO1) {
         return HookDisarmLaunch.RIGHT;
      } else if (defenderAnimation == AnimsEpicFight.HOOK_AXE_AUTO2) {
         return HookDisarmLaunch.LEFT;
      } else {
         return defenderAnimation != AnimsEpicFight.HOOK_DANCING_EDGE && defenderAnimation != AnimsWom.HOOK_HERRSCHER_UP
            ? HookDisarmLaunch.BACKWARD
            : HookDisarmLaunch.BACKWARD;
      }
   }
}
