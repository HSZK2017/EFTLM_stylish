package com.dmc.invincible_dmc.client.renderer.patched.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class PSoulRenderer
   extends PHumanoidRenderer<Mob, LivingEntityPatch<Mob>, HumanoidModel<Mob>, HumanoidMobRenderer<Mob, HumanoidModel<Mob>>, HumanoidMesh> {
   public PSoulRenderer(Context context, EntityType<?> entityType) {
      super(Meshes.SKELETON, context, entityType);
   }

   public AssetAccessor<HumanoidMesh> getMeshProvider(LivingEntityPatch<Mob> mobLivingEntityPatch) {
      return Meshes.SKELETON;
   }

   public AssetAccessor<HumanoidMesh> getDefaultMesh() {
      return Meshes.SKELETON;
   }
}
