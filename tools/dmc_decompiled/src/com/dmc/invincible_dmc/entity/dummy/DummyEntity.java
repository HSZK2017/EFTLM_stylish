package com.dmc.invincible_dmc.entity.dummy;

import javax.annotation.Nonnull;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.level.Level;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class DummyEntity extends PathfinderMob {
   public DummyEntity(EntityType<? extends DummyEntity> type, Level level) {
      super(type, level);
   }

   public static Builder createAttributes() {
      return PathfinderMob.m_21552_()
         .m_22268_(Attributes.f_22276_, 400.0)
         .m_22268_(Attributes.f_22278_, 1.0)
         .m_22268_(Attributes.f_22281_, 0.0)
         .m_22266_((Attribute)EpicFightAttributes.WEIGHT.get())
         .m_22266_((Attribute)EpicFightAttributes.ARMOR_NEGATION.get())
         .m_22266_((Attribute)EpicFightAttributes.IMPACT.get())
         .m_22266_((Attribute)EpicFightAttributes.MAX_STRIKES.get())
         .m_22266_(Attributes.f_22279_);
   }

   public boolean m_6469_(@Nonnull DamageSource source, float amount) {
      boolean result = super.m_6469_(source, amount);
      this.m_21153_(this.m_21233_());
      return result;
   }
}
