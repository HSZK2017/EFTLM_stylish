package org.merlin204.mimic.entity.shadow;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class PlayerShadowMimicEntity extends ShadowMimicEntity {
   public PlayerShadowMimicEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
      super(entityType, level);
   }

   public PlayerShadowMimicEntity(EntityType<? extends PathfinderMob> entityType, Level level, LivingEntity livingEntity) {
      super(entityType, level, livingEntity);
   }

   public static AttributeSupplier getDefaultAttribute() {
      return Mob.m_21552_()
         .m_22268_(Attributes.f_22276_, 300.0)
         .m_22268_(Attributes.f_22281_, 2.0)
         .m_22268_(Attributes.f_22284_, 20.0)
         .m_22268_(Attributes.f_22277_, 72.0)
         .m_22268_(Attributes.f_22278_, 1000.0)
         .m_22268_((Attribute)EpicFightAttributes.MAX_STRIKES.get(), 50.0)
         .m_22268_((Attribute)EpicFightAttributes.WEIGHT.get(), 2000.0)
         .m_22265_();
   }
}
