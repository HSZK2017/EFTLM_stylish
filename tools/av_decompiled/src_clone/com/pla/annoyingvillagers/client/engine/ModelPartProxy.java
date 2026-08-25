package com.pla.annoyingvillagers.client.engine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pla.annoyingvillagers.accessors.ModelPartAccess;
import java.util.List;
import net.minecraft.client.model.geom.ModelPart;

public class ModelPartProxy {
   public final ModelPart part;
   private final List<ModelPartProxy> children;
   public float scaleX = 1.0F;
   public float scaleY = 1.0F;
   public float scaleZ = 1.0F;
   private float x;
   private float y;
   private float z;
   private float xRot;
   private float yRot;
   private float zRot;
   private boolean visible;

   public ModelPartProxy(ModelPart part) {
      this.part = part;
      this.children = part.m_171331_().skip(1L).map(ModelPartProxy::new).toList();
      this.update();
   }

   public void copy(ModelPartProxy other) {
      other.x = this.x;
      other.y = this.y;
      other.z = this.z;
      other.xRot = this.xRot;
      other.yRot = this.yRot;
      other.zRot = this.zRot;
      other.scaleX = this.scaleX;
      other.scaleY = this.scaleY;
      other.scaleZ = this.scaleZ;
      other.visible = this.visible;
      if (this.children.size() != other.children.size()) {
         throw new IllegalArgumentException("Proxies do not share the same children.");
      } else {
         for (int i = 0; i < this.children.size(); i++) {
            this.children.get(i).copy(other.children.get(i));
         }
      }
   }

   public final void update() {
      this.x = this.part.f_104200_;
      this.y = this.part.f_104201_;
      this.z = this.part.f_104202_;
      this.xRot = this.part.f_104203_;
      this.yRot = this.part.f_104204_;
      this.zRot = this.part.f_104205_;
      ModelPartAccess mixinPart = (ModelPartAccess)this.part;
      this.scaleX = mixinPart.getXScale();
      this.scaleY = mixinPart.getYScale();
      this.scaleZ = mixinPart.getZScale();
      this.visible = this.part.f_104207_;

      for (ModelPartProxy child : this.children) {
         child.update();
      }
   }

   public final void apply() {
      this.part.f_104200_ = this.x;
      this.part.f_104201_ = this.y;
      this.part.f_104202_ = this.z;
      this.part.f_104203_ = this.xRot;
      this.part.f_104204_ = this.yRot;
      this.part.f_104205_ = this.zRot;
      ((ModelPartAccess)this.part).setRenderScale(this.scaleX, this.scaleY, this.scaleZ);
      this.part.f_104207_ = this.visible;

      for (ModelPartProxy child : this.children) {
         child.apply();
      }
   }

   public void render(PoseStack ps, VertexConsumer vertices, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
      this.apply();
      this.part.m_104306_(ps, vertices, packedLightIn, packedOverlayIn, red, green, blue, alpha);
   }
}
