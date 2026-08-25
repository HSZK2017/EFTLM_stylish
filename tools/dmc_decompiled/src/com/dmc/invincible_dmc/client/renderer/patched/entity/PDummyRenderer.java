package com.dmc.invincible_dmc.client.renderer.patched.entity;

import com.dmc.invincible_dmc.client.renderer.entity.DummyRenderer;
import com.dmc.invincible_dmc.entity.dummy.DummyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.world.entity.EntityType;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;

public class PDummyRenderer extends PHumanoidRenderer<DummyEntity, HumanoidMobPatch<DummyEntity>, PlayerModel<DummyEntity>, DummyRenderer, HumanoidMesh> {
   public PDummyRenderer(Context context, EntityType<?> entityType) {
      super(Meshes.BIPED, context, entityType);
      this.addPatchedLayer(PlayerItemInHandLayer.class, new PatchedItemInHandLayer());
   }

   protected void prepareModel(HumanoidMesh mesh, DummyEntity entity, HumanoidMobPatch<DummyEntity> dummyPlayerEntityHumanoidMobPatch, DummyRenderer renderer) {
      super.prepareModel(mesh, entity, dummyPlayerEntityHumanoidMobPatch, renderer);
      PlayerModel<DummyEntity> model = (PlayerModel<DummyEntity>)renderer.m_7200_();
      mesh.head.setHidden(!model.f_102808_.f_104207_);
      mesh.hat.setHidden(!model.f_102809_.f_104207_);
      mesh.jacket.setHidden(!model.f_103378_.f_104207_);
      mesh.torso.setHidden(!model.f_102810_.f_104207_);
      mesh.leftArm.setHidden(!model.f_102812_.f_104207_);
      mesh.leftLeg.setHidden(!model.f_102814_.f_104207_);
      mesh.leftPants.setHidden(!model.f_103376_.f_104207_);
      mesh.leftSleeve.setHidden(!model.f_103374_.f_104207_);
      mesh.rightArm.setHidden(!model.f_102811_.f_104207_);
      mesh.rightLeg.setHidden(!model.f_102813_.f_104207_);
      mesh.rightPants.setHidden(!model.f_103377_.f_104207_);
      mesh.rightSleeve.setHidden(!model.f_103375_.f_104207_);
   }

   public AssetAccessor<HumanoidMesh> getMeshProvider(HumanoidMobPatch<DummyEntity> dummyPlayerEntityHumanoidMobPatch) {
      return Minecraft.m_91087_().f_91074_ != null && "slim".equals(Minecraft.m_91087_().f_91074_.m_108564_()) ? Meshes.ALEX : Meshes.BIPED;
   }

   public AssetAccessor<HumanoidMesh> getDefaultMesh() {
      return Meshes.BIPED;
   }
}
