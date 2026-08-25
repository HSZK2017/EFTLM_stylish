package com.dmc.invincible_dmc.api.collider;

import com.dmc.invincible_dmc.api.forgeevent.ColliderScaleEvent;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ScalableMultiOBBCollider extends MultiOBBCollider {
   public ScalableMultiOBBCollider(int arrayLength, double vertexX, double vertexY, double vertexZ, double centerX, double centerY, double centerZ) {
      super(arrayLength, vertexX, vertexY, vertexZ, centerX, centerY, centerZ);
      this.colliders.clear();
      ScalableOBBCollider scalable = new ScalableOBBCollider(vertexX, vertexY, vertexZ, centerX, centerY, centerZ);

      for (int i = 0; i < arrayLength; i++) {
         this.colliders.add(scalable);
      }
   }

   public ScalableMultiOBBCollider(ScalableOBBCollider... colliders) {
      super(colliders);
   }

   public void setScale(double x, double y, double z) {
      for (OBBCollider c : this.colliders) {
         if (c instanceof ScalableOBBCollider scalable) {
            scalable.setScale(x, y, z);
         }
      }
   }

   public Vec3 getScaleMultiplier() {
      return !this.colliders.isEmpty() && this.colliders.get(0) instanceof ScalableOBBCollider scalable
         ? scalable.getScaleMultiplier()
         : new Vec3(1.0, 1.0, 1.0);
   }

   public void setCenterOffset(double x, double y, double z) {
      for (OBBCollider c : this.colliders) {
         if (c instanceof ScalableOBBCollider scalable) {
            scalable.setCenterOffset(x, y, z);
         }
      }
   }

   public Vec3 getCenterOffset() {
      return !this.colliders.isEmpty() && this.colliders.get(0) instanceof ScalableOBBCollider scalable ? scalable.getCenterOffset() : Vec3.f_82478_;
   }

   public ScalableMultiOBBCollider deepCopy() {
      ScalableOBBCollider[] copies = new ScalableOBBCollider[this.colliders.size()];

      for (int i = 0; i < this.colliders.size(); i++) {
         copies[i] = (ScalableOBBCollider)((OBBCollider)this.colliders.get(i)).deepCopy();
      }

      return new ScalableMultiOBBCollider(copies);
   }

   public List<Entity> updateAndSelectCollideEntity(
      LivingEntityPatch<?> entitypatch, AttackAnimation animation, float prevElapsedTime, float elapsedTime, Joint joint, float attackSpeed
   ) {
      Vec3 savedScale = this.getScaleMultiplier();
      Vec3 savedOffset = this.getCenterOffset();
      this.postAndApplyEvent(entitypatch);

      List var9;
      try {
         var9 = super.updateAndSelectCollideEntity(entitypatch, animation, prevElapsedTime, elapsedTime, joint, attackSpeed);
      } finally {
         this.setScale(savedScale.f_82479_, savedScale.f_82480_, savedScale.f_82481_);
         this.setCenterOffset(savedOffset.f_82479_, savedOffset.f_82480_, savedOffset.f_82481_);
      }

      return var9;
   }

   @OnlyIn(Dist.CLIENT)
   public void draw(
      PoseStack poseStack,
      MultiBufferSource buffer,
      LivingEntityPatch<?> entitypatch,
      AttackAnimation animation,
      Joint joint,
      float prevElapsedTime,
      float elapsedTime,
      float partialTicks,
      float attackSpeed
   ) {
      Vec3 savedScale = this.getScaleMultiplier();
      Vec3 savedOffset = this.getCenterOffset();
      this.postAndApplyEvent(entitypatch);

      try {
         super.draw(poseStack, buffer, entitypatch, animation, joint, prevElapsedTime, elapsedTime, partialTicks, attackSpeed);
      } finally {
         this.setScale(savedScale.f_82479_, savedScale.f_82480_, savedScale.f_82481_);
         this.setCenterOffset(savedOffset.f_82479_, savedOffset.f_82480_, savedOffset.f_82481_);
      }
   }

   private void postAndApplyEvent(LivingEntityPatch<?> entitypatch) {
      ColliderScaleEvent event = new ColliderScaleEvent(entitypatch, this);
      MinecraftForge.EVENT_BUS.post(event);
      this.setScale(event.scaleX, event.scaleY, event.scaleZ);
      this.setCenterOffset(event.centerOffsetX, event.centerOffsetY, event.centerOffsetZ);
   }
}
