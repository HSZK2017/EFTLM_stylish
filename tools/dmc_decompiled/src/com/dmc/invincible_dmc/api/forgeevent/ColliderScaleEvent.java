package com.dmc.invincible_dmc.api.forgeevent;

import net.minecraftforge.eventbus.api.Event;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ColliderScaleEvent extends Event {
   private final LivingEntityPatch<?> entityPatch;
   private final Collider collider;
   public double scaleX = 1.0;
   public double scaleY = 1.0;
   public double scaleZ = 1.0;
   public double centerOffsetX;
   public double centerOffsetY;
   public double centerOffsetZ;

   public ColliderScaleEvent(LivingEntityPatch<?> entityPatch, Collider collider) {
      this.entityPatch = entityPatch;
      this.collider = collider;
   }

   public LivingEntityPatch<?> getEntityPatch() {
      return this.entityPatch;
   }

   public Collider getCollider() {
      return this.collider;
   }
}
