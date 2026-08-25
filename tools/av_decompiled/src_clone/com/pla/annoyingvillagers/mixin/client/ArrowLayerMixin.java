package com.pla.annoyingvillagers.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.entity.EnchantedArrowEntity;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ArrowLayer.class})
public abstract class ArrowLayerMixin<T extends LivingEntity, M extends PlayerModel<T>> {
   @Shadow
   @Final
   private EntityRenderDispatcher f_116562_;

   @Inject(
      method = {"renderStuckItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void av$renderColoredStuckArrow(
      PoseStack poseStack, MultiBufferSource buffer, int packedLight, Entity entity, float x, float y, float z, float partialTick, CallbackInfo ci
   ) {
      if (entity instanceof HerobrineMob) {
         float f = Mth.m_14116_(x * x + z * z);
         EnchantedArrowEntity arrow = new EnchantedArrowEntity(
            (EntityType<? extends EnchantedArrowEntity>)AnnoyingVillagersModEntities.ENCHANTED_ARROW.get(), entity.m_9236_()
         );
         arrow.m_6034_(entity.m_20185_(), entity.m_20186_(), entity.m_20189_());
         arrow.m_146922_((float)(Math.atan2((double)x, (double)z) * (180.0 / Math.PI)));
         arrow.m_146926_((float)(Math.atan2((double)y, (double)f) * (180.0 / Math.PI)));
         arrow.f_19859_ = arrow.m_146908_();
         arrow.f_19860_ = arrow.m_146909_();
         arrow.setColorGlint(annoyingVillagers$pickMode(entity, x, y, z));
         this.f_116562_.m_114384_(arrow, 0.0, 0.0, 0.0, 0.0F, partialTick, poseStack, buffer, packedLight);
         ci.cancel();
      }
   }

   @Unique
   private static int annoyingVillagers$pickMode(Entity entity, float x, float y, float z) {
      int seed = entity.m_19879_();
      seed = 31 * seed + Float.floatToIntBits(x);
      seed = 31 * seed + Float.floatToIntBits(y);
      seed = 31 * seed + Float.floatToIntBits(z);

      return switch (Math.floorMod(seed, 11)) {
         case 0 -> 1;
         case 1 -> 2;
         case 2 -> 3;
         case 3 -> 4;
         case 4 -> 5;
         case 5 -> 6;
         case 6 -> 7;
         case 7 -> 8;
         case 8 -> 9;
         case 9 -> 10;
         default -> 11;
      };
   }
}
