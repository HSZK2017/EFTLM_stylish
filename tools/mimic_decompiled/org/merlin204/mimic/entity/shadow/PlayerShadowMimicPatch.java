package org.merlin204.mimic.entity.shadow;

import net.minecraft.world.entity.LivingEntity;

public class PlayerShadowMimicPatch extends ShadowMimicPatch<PlayerShadowMimicEntity> {
   @Override
   public void tryToLearnAnimation(LivingEntity livingEntity) {
   }

   @Override
   public void tickNearbyEntity(LivingEntity entity) {
      super.tickNearbyEntity(entity);
      if (entity instanceof PlayerShadowMimicEntity playerShadowMimicEntity) {
         LivingEntity owner = ((PlayerShadowMimicEntity)this.original).getOwner();
         if (owner != null
            && owner == playerShadowMimicEntity.getOwner()
            && playerShadowMimicEntity.m_21223_() <= ((PlayerShadowMimicEntity)this.original).m_21223_()) {
            playerShadowMimicEntity.m_146870_();
         }
      }
   }
}
