package com.pla.annoyingvillagers.compat;

import com.hm.efn.animations.types.DeferredDamageAttackAnimation;
import com.hm.efn.animations.types.MoveAttackAnimation;
import com.hm.efn.animations.types.murasama.MurasamaAttackAnimation;
import com.hm.efn.animations.types.sekiro.SekiroArtsAnimation;
import com.hm.efn.animations.types.sekiro.SekiroAttackAnimation;
import com.hm.efn.animations.types.stun.EFNStunAnimation;
import com.hm.efn.animations.types.yamato.YamatoAttackAnimation;
import com.hm.efn.client.sound.EFNSounds;
import com.hm.efn.gameasset.EFNAnimations;
import com.hm.efn.gameasset.animations.EFNGreatSwordAnimations;
import com.hm.efn.gameasset.animations.EFNLanceAnimations;
import com.hm.efn.gameasset.animations.EFNMurasamaAnimations;
import com.hm.efn.gameasset.animations.EFNScytheAnimations;
import com.hm.efn.gameasset.animations.EFNSekiroAnimations;
import com.hm.efn.gameasset.animations.EFNSkillAnimations;
import com.hm.efn.gameasset.animations.EFNThornWheelAnimations;
import com.hm.efn.gameasset.animations.EFNYamatoAnimations;
import com.hm.efn.item.custom.AetherialDuskDualSword_MainHandItem;
import com.hm.efn.item.custom.AirTachiItem;
import com.hm.efn.item.custom.BroadBladeItem;
import com.hm.efn.item.custom.CoTachiItem;
import com.hm.efn.item.custom.CrescentMoonItem;
import com.hm.efn.item.custom.ExsiliumgladiusItem;
import com.hm.efn.item.custom.FireExsiliumgladiusItem;
import com.hm.efn.item.custom.HfMurasamaItem;
import com.hm.efn.item.custom.KusabimaruItem;
import com.hm.efn.item.custom.Meen_SpearItem;
import com.hm.efn.item.custom.NFShortSwordItem;
import com.hm.efn.item.custom.NFShortSwordTwoItem;
import com.hm.efn.item.custom.NfClawItem;
import com.hm.efn.item.custom.PioneerItem;
import com.hm.efn.item.custom.RuinsgreatswordItem;
import com.hm.efn.item.custom.ScytheItem;
import com.hm.efn.item.custom.ThornWheelItem;
import com.hm.efn.item.custom.YamatoItem;
import com.hm.efn.item.geo.ExcaliburItem;
import com.hm.efn.particle.EFNParticles;
import com.merlin204.avalon.epicfight.animations.AvalonAttackAnimation;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.shelmarow.combat_evolution.ai.CEHumanoidPatch;
import org.joml.Vector3d;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AirSlashAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class EpicFightNightFall {
   private static final Set<String> DANGEROUS_ANIMATIONS = new HashSet<>();
   public static final int MULTIPLIER_DAMAGE_VALUE = 5;

   public static Set<String> getDangerousAnimations() {
      return DANGEROUS_ANIMATIONS;
   }

   public static boolean isEFNStun(AssetAccessor<? extends StaticAnimation> assetAccessor) {
      return assetAccessor.get() instanceof EFNStunAnimation;
   }

   private static Vector3d getParticleArgumentsForAnimation(AnimationAccessor<? extends StaticAnimation> animation) {
      if (animation.get() == EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1) {
         return new Vector3d(1.0, -0.6, 0.0);
      } else if (animation.get() == EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2) {
         return new Vector3d(1.0, 0.6, 0.0);
      } else {
         return animation.get() == EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT3 ? new Vector3d(1.2, 0.7, 0.0) : new Vector3d(1.0, 0.0, 0.0);
      }
   }

   private static Vector3d getParticlePositionForAnimation(Entity owner, Entity target, AnimationAccessor<? extends StaticAnimation> animation) {
      Vec3 lookVec = owner.m_20154_();
      Vec3 playerPos = owner.m_20182_().m_82520_(0.0, (double)owner.m_20206_() * 0.6, 0.0);
      Vec3 targetPos = target.m_20182_().m_82520_(0.0, (double)target.m_20206_() * 0.6, 0.0);
      Vec3 middlePos = playerPos.m_82549_(targetPos.m_82546_(playerPos).m_82490_(0.5));
      Vec3 toMiddle = middlePos.m_82546_(playerPos);
      double distanceToMiddle = toMiddle.m_82553_();
      double maxDistance = 1.0;
      Vec3 limitedMiddlePos;
      if (distanceToMiddle > maxDistance) {
         Vec3 direction = toMiddle.m_82541_();
         limitedMiddlePos = playerPos.m_82549_(direction.m_82490_(maxDistance));
      } else {
         limitedMiddlePos = middlePos;
      }

      Vector3d basePosition = new Vector3d(limitedMiddlePos.f_82479_, limitedMiddlePos.f_82480_, limitedMiddlePos.f_82481_);
      if (animation.get() == EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1) {
         Vec3 leftOffset = lookVec.m_82524_((float)Math.toRadians(-90.0)).m_82490_(0.2);
         Vector3d finalPos = new Vector3d(basePosition.x + leftOffset.f_82479_, basePosition.y, basePosition.z + leftOffset.f_82481_);
         return limitDistanceFromOwner(playerPos, finalPos, maxDistance);
      } else if (animation.get() == EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2) {
         Vec3 rightOffset = lookVec.m_82524_((float)Math.toRadians(90.0)).m_82490_(0.2);
         Vector3d finalPos = new Vector3d(basePosition.x + rightOffset.f_82479_, basePosition.y, basePosition.z + rightOffset.f_82481_);
         return limitDistanceFromOwner(playerPos, finalPos, maxDistance);
      } else {
         return basePosition;
      }
   }

   private static Vector3d limitDistanceFromOwner(Vec3 playerPos, Vector3d particlePos, double maxDistance) {
      Vec3 toParticle = new Vec3(particlePos.x, particlePos.y, particlePos.z).m_82546_(playerPos);
      double distance = toParticle.m_82553_();
      if (distance > maxDistance) {
         Vec3 direction = toParticle.m_82541_();
         Vec3 limitedPos = playerPos.m_82549_(direction.m_82490_(maxDistance));
         return new Vector3d(limitedPos.f_82479_, limitedPos.f_82480_, limitedPos.f_82481_);
      } else {
         return particlePos;
      }
   }

   private static void spawnParryFlashParticle(Entity owner, Entity target, AnimationAccessor<? extends StaticAnimation> animation, ServerLevel serverLevel) {
      Vector3d particleArgs = getParticleArgumentsForAnimation(animation);
      ((HitParticleType)EFNParticles.EFN_PARRY_FLASH_MAIN.get())
         .spawnParticleWithArgument(
            serverLevel, (player, entity) -> getParticlePositionForAnimation(player, entity, animation), (player, entity) -> particleArgs, owner, target
         );
      ((HitParticleType)EFNParticles.ALL_SPARK.get())
         .spawnParticleWithArgument(
            serverLevel, (player, entity) -> getParticlePositionForAnimation(player, entity, animation), HitParticleType.ZERO, owner, target
         );
   }

   public static void playEfnGuardHit(LivingEntityPatch<?> livingEntityPatch, int index, DamageSource damageSource) {
      if (((LivingEntity)livingEntityPatch.getOriginal()).m_9236_() instanceof ServerLevel serverLevel) {
         AnimationAccessor<? extends StaticAnimation> animation;
         if (index == 0) {
            animation = EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1;
         } else if (index == 1) {
            animation = EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2;
         } else {
            animation = EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT3;
         }

         livingEntityPatch.playAnimationSynchronized(animation, 0.0F);
         spawnParryFlashParticle(livingEntityPatch.getOriginal(), damageSource.m_7640_(), animation, serverLevel);
         livingEntityPatch.playSound((SoundEvent)EFNSounds.PARRY.get(), 0.5F, 0.0F, 0.0F);
      }
   }

   public static boolean isPlayingEfnGuardHit(CEHumanoidPatch<?> ceHumanoidPatch) {
      AnimationPlayer animationPlayer = ceHumanoidPatch.getAnimator().getPlayerFor(null);
      if (animationPlayer == null) {
         return false;
      } else {
         AssetAccessor<? extends StaticAnimation> dynamicAnimation = animationPlayer.getRealAnimation();
         return dynamicAnimation == EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT1
            || dynamicAnimation == EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT2
            || dynamicAnimation == EFNSkillAnimations.EFN_GUARD_ACTIVE_HIT3;
      }
   }

   public static boolean isEfnWeapons(ItemStack itemStack) {
      return itemStack.m_41720_() instanceof RuinsgreatswordItem
         || itemStack.m_41720_() instanceof ThornWheelItem
         || itemStack.m_41720_() instanceof AetherialDuskDualSword_MainHandItem
         || itemStack.m_41720_() instanceof Meen_SpearItem
         || itemStack.m_41720_() instanceof PioneerItem
         || itemStack.m_41720_() instanceof NFShortSwordItem
         || itemStack.m_41720_() instanceof NFShortSwordTwoItem
         || itemStack.m_41720_() instanceof ExsiliumgladiusItem
         || itemStack.m_41720_() instanceof FireExsiliumgladiusItem
         || itemStack.m_41720_() instanceof AirTachiItem
         || itemStack.m_41720_() instanceof CoTachiItem
         || itemStack.m_41720_() instanceof KusabimaruItem
         || itemStack.m_41720_() instanceof BroadBladeItem
         || itemStack.m_41720_() instanceof ScytheItem
         || itemStack.m_41720_() instanceof NfClawItem
         || itemStack.m_41720_() instanceof YamatoItem
         || itemStack.m_41720_() instanceof HfMurasamaItem
         || itemStack.m_41720_() instanceof CrescentMoonItem
         || itemStack.m_41720_() instanceof ExcaliburItem;
   }

   static {
      DANGEROUS_ANIMATIONS.addAll(
         Set.of(
            ((ActionAnimation)EFNGreatSwordAnimations.NG_GREATSWORD_CHARGING.get()).getRegistryName().toString(),
            ((AvalonAttackAnimation)EFNGreatSwordAnimations.NG_GREATSWORD_CHARG1MAX_FIRST.get()).getRegistryName().toString(),
            ((AvalonAttackAnimation)EFNGreatSwordAnimations.NG_GREATSWORD_CHARG1MAX_SECOND.get()).getRegistryName().toString(),
            ((ActionAnimation)EFNGreatSwordAnimations.NG_GREATSWORD_CHARGING_MOB.get()).getRegistryName().toString(),
            ((AttackAnimation)EFNGreatSwordAnimations.NG_GREATSWORD_AIRSLASH.get()).getRegistryName().toString(),
            ((AvalonAttackAnimation)EFNLanceAnimations.NF_MEEN_DASH.get()).getRegistryName().toString(),
            ((AttackAnimation)EFNLanceAnimations.NF_MEEN_CHARGING.get()).getRegistryName().toString(),
            ((AttackAnimation)EFNLanceAnimations.NF_MEEN_CHARGING_MOB.get()).getRegistryName().toString(),
            ((AvalonAttackAnimation)EFNLanceAnimations.NF_MEEN_CHARGE1.get()).getRegistryName().toString(),
            ((AvalonAttackAnimation)EFNLanceAnimations.NF_MEEN_CHARGE2.get()).getRegistryName().toString(),
            ((AvalonAttackAnimation)EFNLanceAnimations.NF_MEEN_CHARGE3.get()).getRegistryName().toString(),
            ((AvalonAttackAnimation)EFNLanceAnimations.NF_MEEN_FINISHER.get()).getRegistryName().toString(),
            ((YamatoAttackAnimation)EFNYamatoAnimations.YAMATO_JUDEMENCUT_ALL.get()).getRegistryName().toString(),
            ((YamatoAttackAnimation)EFNYamatoAnimations.YAMATO_JUDEMENCUT.get()).getRegistryName().toString(),
            ((YamatoAttackAnimation)EFNYamatoAnimations.YAMATO_JUDEMENCUT_CHARGE.get()).getRegistryName().toString(),
            ((YamatoAttackAnimation)EFNYamatoAnimations.YAMATO_JUDEMENCUT_JUST.get()).getRegistryName().toString(),
            ((YamatoAttackAnimation)EFNYamatoAnimations.YAMATO_VOLCANOL_ALL.get()).getRegistryName().toString(),
            ((YamatoAttackAnimation)EFNYamatoAnimations.YAMATO_VOLCANOL.get()).getRegistryName().toString(),
            ((YamatoAttackAnimation)EFNYamatoAnimations.YAMATO_VOLCANOL_CHARGE.get()).getRegistryName().toString(),
            ((DeferredDamageAttackAnimation)EFNAnimations.DMC5_V_JC.get()).getRegistryName().toString(),
            ((AvalonAttackAnimation)EFNSkillAnimations.EXECUTION.get()).getRegistryName().toString(),
            ((MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_ZANDATSU.get()).getRegistryName().toString(),
            ((MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_ZANDATSU_AIR.get()).getRegistryName().toString(),
            ((MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_DASH_Y.get()).getRegistryName().toString(),
            ((MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_DASH_Y_SP.get()).getRegistryName().toString(),
            ((MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_KICK_Y.get()).getRegistryName().toString(),
            ((MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_Y.get()).getRegistryName().toString(),
            ((MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_Y_CHARGE.get()).getRegistryName().toString(),
            ((MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_Y_CHARGE_THROUGH.get()).getRegistryName().toString(),
            ((MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_Y_AIR.get()).getRegistryName().toString(),
            ((MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_Y_CHARGE_AIR.get()).getRegistryName().toString(),
            ((MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_XY.get()).getRegistryName().toString(),
            ((MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_XY_CHARGE.get()).getRegistryName().toString(),
            ((MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_XXY.get()).getRegistryName().toString(),
            ((MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_XXY_CHARGE.get()).getRegistryName().toString(),
            ((MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_XXX.get()).getRegistryName().toString(),
            ((MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_XXXY.get()).getRegistryName().toString(),
            ((MurasamaAttackAnimation)EFNMurasamaAnimations.HF_MURASAMA_XXXY_CHARGE.get()).getRegistryName().toString(),
            ((SekiroAttackAnimation)EFNSekiroAnimations.DRAGON_FLASH.get()).getRegistryName().toString(),
            ((SekiroArtsAnimation)EFNSekiroAnimations.MORTAL_BLADE_1.get()).getRegistryName().toString(),
            ((SekiroArtsAnimation)EFNSekiroAnimations.MORTAL_BLADE_2.get()).getRegistryName().toString(),
            ((AttackAnimation)EFNThornWheelAnimations.THORNWHEEL_SKILL_START.get()).getRegistryName().toString(),
            ((MoveAttackAnimation)EFNThornWheelAnimations.THORNWHEEL_SKILL_LOOP.get()).getRegistryName().toString(),
            ((AttackAnimation)EFNThornWheelAnimations.THORNWHEEL_SKILL_START_N.get()).getRegistryName().toString(),
            ((MoveAttackAnimation)EFNThornWheelAnimations.THORNWHEEL_SKILL_LOOP_N.get()).getRegistryName().toString(),
            ((AttackAnimation)EFNScytheAnimations.SCYTHE_HARVEST.get()).getRegistryName().toString(),
            ((AirSlashAnimation)EFNScytheAnimations.SCYTHE_AIR_SLASH.get()).getRegistryName().toString(),
            ((AttackAnimation)EFNScytheAnimations.SCYTHE_SCARLET_END.get()).getRegistryName().toString()
         )
      );
   }
}
