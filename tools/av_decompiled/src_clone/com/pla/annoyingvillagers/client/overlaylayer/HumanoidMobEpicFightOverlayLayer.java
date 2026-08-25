package com.pla.annoyingvillagers.client.overlaylayer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.entity.HerobrineGregEntity;
import com.pla.annoyingvillagers.entity.LowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.LowShadowHerobrineCloneEntity;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModMobEffects;
import com.pla.annoyingvillagers.potion.ObedienceMobEffect;
import com.pla.annoyingvillagers.util.HerobrineEyesUtil;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.layer.ModelRenderLayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class HumanoidMobEpicFightOverlayLayer<E extends LivingEntity, AM extends HumanoidMesh>
   extends ModelRenderLayer<E, LivingEntityPatch<E>, HumanoidModel<E>, RenderLayer<E, HumanoidModel<E>>, AM> {
   private final ResourceLocation BLOOD_TEXTURE = ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/player_mob_blood.png");
   private final ResourceLocation DEFAULT_EYE = ResourceLocation.fromNamespaceAndPath(
      "annoyingvillagers", "textures/entities/herobrine_eyes/default/default.png"
   );

   public HumanoidMobEpicFightOverlayLayer(AssetAccessor<AM> mesh) {
      super(mesh);
   }

   private ResourceLocation pickTexture(E e) {
      if (e instanceof LowHerobrineCloneEntity) {
         String name = e.m_8077_() ? e.m_7770_().getString() : e.m_7755_().getString();
         return HerobrineEyesUtil.getHerobrineEyesTexture(name);
      } else if (EntityType.m_20613_(e.m_6095_()).equals(ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "infected_player_mob"))) {
         return this.BLOOD_TEXTURE;
      } else if (e instanceof HerobrineMob || e instanceof LowShadowHerobrineCloneEntity) {
         return this.DEFAULT_EYE;
      } else {
         if (e instanceof HerobrineGregEntity herobrineGregEntity && herobrineGregEntity.isWhiteEye()) {
            return this.DEFAULT_EYE;
         }

         if (ObedienceMobEffect.canBeObedientMob(e) && e.m_21023_((MobEffect)AnnoyingVillagersModMobEffects.OBEDIENCE.get())) {
            if (e instanceof ZombieVillager) {
               return ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/obedience/zombie_villager.png");
            }

            if (e instanceof Zombie) {
               return ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/obedience/zombie.png");
            }

            if (e instanceof AbstractSkeleton) {
               return ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/obedience/skeleton.png");
            }

            if (e instanceof AbstractPiglin) {
               return ResourceLocation.fromNamespaceAndPath("annoyingvillagers", "textures/entities/obedience/piglin.png");
            }
         }

         return null;
      }
   }

   protected void renderLayer(
      LivingEntityPatch<E> eLivingEntityPatch,
      E e,
      @Nullable RenderLayer<E, HumanoidModel<E>> eHumanoidModelRenderLayer,
      PoseStack poseStack,
      MultiBufferSource multiBufferSource,
      int i,
      OpenMatrix4f[] openMatrix4fs,
      float v,
      float v1,
      float v2,
      float v3
   ) {
      ResourceLocation tex = this.pickTexture(e);
      if (tex != null) {
         if (tex == this.BLOOD_TEXTURE) {
            ((HumanoidMesh)this.mesh.get())
               .draw(
                  poseStack,
                  multiBufferSource,
                  RenderType.m_110458_(tex),
                  i,
                  1.0F,
                  1.0F,
                  1.0F,
                  1.0F,
                  OverlayTexture.f_118083_,
                  eLivingEntityPatch.getArmature(),
                  openMatrix4fs
               );
         } else {
            ((HumanoidMesh)this.mesh.get())
               .draw(
                  poseStack,
                  multiBufferSource,
                  RenderType.m_110488_(tex),
                  i,
                  1.0F,
                  1.0F,
                  1.0F,
                  1.0F,
                  OverlayTexture.f_118083_,
                  eLivingEntityPatch.getArmature(),
                  openMatrix4fs
               );
         }
      }
   }
}
