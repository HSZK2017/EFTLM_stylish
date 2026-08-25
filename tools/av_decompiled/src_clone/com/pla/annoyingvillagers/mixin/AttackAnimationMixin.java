package com.pla.annoyingvillagers.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.pla.annoyingvillagers.entity.FloatingLookBlockEntity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

@Mixin(
   value = {AttackAnimation.class},
   remap = false
)
public abstract class AttackAnimationMixin {
   @Shadow
   public abstract EpicFightDamageSource getEpicFightDamageSource(LivingEntityPatch<?> var1, Entity var2, Phase var3);

   @ModifyExpressionValue(
      method = {"hurtCollidingEntities"},
      at = {@At(
         value = "INVOKE",
         target = "Lyesman/epicfight/api/animation/types/AttackAnimation$Phase;getCollidingEntities(Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lyesman/epicfight/api/animation/types/AttackAnimation;FFF)Ljava/util/List;"
      )}
   )
   private List<Entity> annoyingvillagers$hurtFloatingLookBlocks(
      List<Entity> collidingEntities, @Local(argsOnly = true) LivingEntityPatch<?> attackerPatch, @Local(argsOnly = true) Phase phase
   ) {
      List<Entity> remainingEntities = new ArrayList<>(collidingEntities.size());

      for (Entity entity : collidingEntities) {
         if (entity instanceof FloatingLookBlockEntity floatingLookBlock) {
            this.annoyingvillagers$hurtFloatingLookBlock(attackerPatch, phase, floatingLookBlock);
         } else {
            remainingEntities.add(entity);
         }
      }

      return remainingEntities;
   }

   private void annoyingvillagers$hurtFloatingLookBlock(LivingEntityPatch<?> attackerPatch, Phase phase, FloatingLookBlockEntity target) {
      if (!target.m_9236_().m_5776_()
         && target.m_6084_()
         && !attackerPatch.getCurrentlyAttackTriedEntities().contains(target)
         && !attackerPatch.isTargetInvulnerable(target)) {
         LivingEntity attacker = (LivingEntity)attackerPatch.getOriginal();
         if (annoyingvillagers$canBeSeen(attacker, target)) {
            EpicFightDamageSource damageSource = this.getEpicFightDamageSource(attackerPatch, target, phase);
            target.m_6469_(damageSource, 1.0F);
            attackerPatch.getCurrentlyAttackTriedEntities().add(target);
         }
      }
   }

   private static boolean annoyingvillagers$canBeSeen(LivingEntity attacker, Entity target) {
      AABB targetBox = target.m_20191_();
      double distance = target.m_20182_().m_82554_(attacker.m_146892_())
         + targetBox.m_82399_().m_82554_(new Vec3(targetBox.f_82291_, targetBox.f_82292_, targetBox.f_82293_));
      return MathUtils.canBeSeen(target, attacker, distance);
   }
}
