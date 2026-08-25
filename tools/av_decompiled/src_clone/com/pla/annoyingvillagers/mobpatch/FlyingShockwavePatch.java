package com.pla.annoyingvillagers.mobpatch;

import com.mojang.logging.LogUtils;
import com.pla.annoyingvillagers.entity.FlyingShockwaveProjectile;
import yesman.epicfight.world.capabilities.projectile.ProjectilePatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public class FlyingShockwavePatch extends ProjectilePatch<FlyingShockwaveProjectile> {
   protected void setMaxStrikes(FlyingShockwaveProjectile flyingShockwaveProjectile, int i) {
      flyingShockwaveProjectile.setMaxStrikes(i);
   }

   public void onAddedToWorld() {
      LogUtils.getLogger().debug("onAddedToWorld");
      if (((FlyingShockwaveProjectile)this.getOriginal()).m_9236_().m_5776_()) {
      }
   }

   public EpicFightDamageSource createEpicFightDamageSource() {
      return new EpicFightDamageSource(((FlyingShockwaveProjectile)this.getOriginal()).m_9236_().m_269111_().m_269264_());
   }
}
