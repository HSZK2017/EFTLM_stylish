package com.pla.annoyingvillagers.client.model;

import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.animal.Chicken;

public class ModelBbq<T extends Chicken> extends ChickenModel<T> {
   private final ModelPart beak;

   public ModelBbq(ModelPart root) {
      super(root);
      this.beak = root.m_171324_("beak");
   }

   public ModelPart getBeak() {
      return this.beak;
   }
}
