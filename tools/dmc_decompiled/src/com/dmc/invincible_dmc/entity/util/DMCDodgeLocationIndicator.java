package com.dmc.invincible_dmc.entity.util;

import com.dmc.invincible_dmc.entity.DMCEntities;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.entity.DodgeLocationIndicator;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class DMCDodgeLocationIndicator extends DodgeLocationIndicator {
   private LivingEntityPatch<?> livingEntityPatch;

   public DMCDodgeLocationIndicator(EntityType<? extends LivingEntity> type, Level level) {
      super(type, level);
   }

   public DMCDodgeLocationIndicator(LivingEntityPatch<?> livingEntityPatch) {
      this((EntityType<? extends LivingEntity>)DMCEntities.DMC_DODGELOCATION_INDICATOR.get(), ((LivingEntity)livingEntityPatch.getOriginal()).m_9236_());
      this.livingEntityPatch = livingEntityPatch;
      this.m_146884_(((LivingEntity)livingEntityPatch.getOriginal()).m_20182_());
      this.m_20011_(((LivingEntity)livingEntityPatch.getOriginal()).m_20191_().m_82363_(2.0, 0.0, 2.0));
      if (this.m_9236_().m_5776_()) {
         this.m_146870_();
      }
   }

   public void m_8119_() {
      if (this.f_19797_ > 10) {
         this.m_146870_();
      }
   }

   public static AttributeSupplier getDefaultAttribute() {
      return Animal.m_21552_()
         .m_22268_(Attributes.f_22276_, 19.9F)
         .m_22268_(Attributes.f_22281_, 3.0)
         .m_22268_((Attribute)EpicFightAttributes.MAX_STRIKES.get(), 10.0)
         .m_22265_();
   }

   public boolean m_6469_(@NotNull DamageSource damageSource, float amount) {
      if (!this.m_9236_().m_5776_() && this.livingEntityPatch != null) {
         if (damageSource.m_7639_() == this.livingEntityPatch.getOriginal()) {
            return false;
         } else {
            if (!((ResultType)DodgeAnimation.DODGEABLE_SOURCE_VALIDATOR.apply(damageSource)).dealtDamage()
               && damageSource.m_7639_() != this.livingEntityPatch.getOriginal()) {
               this.livingEntityPatch.onDodgeSuccess(damageSource, this.m_20191_().m_82399_());
            }

            this.m_146870_();
            return false;
         }
      } else {
         return false;
      }
   }

   public LivingEntityPatch<?> getEntityPatch() {
      return this.livingEntityPatch;
   }
}
