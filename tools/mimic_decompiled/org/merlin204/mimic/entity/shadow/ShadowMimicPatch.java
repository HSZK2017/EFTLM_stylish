package org.merlin204.mimic.entity.shadow;

import com.merlin204.avalon.entity.ai.AvalonAnimatedAttackGoal;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.merlin204.mimic.entity.MimicPatch;
import org.merlin204.mimic.entity.ai.MimicCombatBehaviors;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public class ShadowMimicPatch<T extends ShadowMimicEntity> extends MimicPatch<T> {
   @Override
   protected void initAI() {
      super.initAI();
      ((ShadowMimicEntity)this.original).f_21345_.m_25352_(0, new AvalonAnimatedAttackGoal(this, MimicCombatBehaviors.PHASE1.build(this)));
   }

   @Override
   public AttackResult attack(EpicFightDamageSource damageSource, Entity target, InteractionHand hand) {
      AttackResult attackResult = super.attack(damageSource, target, hand);
      if (attackResult == null) {
         return AttackResult.missed(0.0F);
      } else {
         LivingEntity owner = ((ShadowMimicEntity)this.getOriginal()).getOwner();
         if (attackResult.resultType.dealtDamage() && owner != null) {
            owner.m_5634_(attackResult.damage * 0.3F);
         }

         return owner != null && target == owner ? AttackResult.missed(0.0F) : attackResult;
      }
   }

   @Nullable
   public LivingEntity getTarget() {
      LivingEntityPatch<?> ownerPatch = ((ShadowMimicEntity)this.getOriginal()).getOwnerPatch();
      if (ownerPatch == null) {
         return null;
      } else {
         LivingEntity target = ownerPatch.getTarget();
         LivingEntity owner = ((ShadowMimicEntity)this.original).getOwner();
         if (target != null && target != owner && target != this.original) {
            this.setAttakTargetSync(target);
            return target;
         } else {
            return null;
         }
      }
   }
}
