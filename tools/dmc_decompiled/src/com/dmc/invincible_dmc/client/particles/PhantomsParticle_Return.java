package com.dmc.invincible_dmc.client.particles;

import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.guhao.vix.util.RenderUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class PhantomsParticle_Return extends NoRenderParticle {
   private static final ResourceLocation AFTERIMAGE_TEXTURE = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/entity/white.png");
   private final LivingEntityPatch<?> entityPatch;

   public PhantomsParticle_Return(ClientLevel clientLevel, double x, double y, double z, LivingEntityPatch<?> livingEntityPatch) {
      super(clientLevel, x, y, z, 0.0, 0.0, 0.0);
      this.entityPatch = livingEntityPatch;
      this.f_107219_ = false;
   }

   public void m_5989_() {
      this.f_107209_ = this.f_107212_;
      this.f_107210_ = this.f_107213_;
      this.f_107211_ = this.f_107214_;
      if (this.f_107224_++ >= this.f_107225_) {
         this.m_107274_();
      } else if (((LivingEntity)this.entityPatch.getOriginal()).m_6084_() && this.f_107224_ % 3 == 1) {
         JudgementCutAfterimagePath.Sample path = JudgementCutAfterimagePath.sample(this.f_107223_, this.f_107212_, this.f_107213_, this.f_107214_, true);
         Vec3 origin = path.origin();
         Vec3 velocity = path.velocity();
         DynamicEntityAfterImgParticle particle = DynamicEntityAfterImgParticle.create(
            this.entityPatch,
            YamatoAnimations.YAMATO_JUDGEMENT_CUT_END,
            origin.f_82479_,
            origin.f_82480_,
            origin.f_82481_,
            velocity.f_82479_,
            velocity.f_82480_,
            velocity.f_82481_,
            4,
            4.9333334F,
            AFTERIMAGE_TEXTURE,
            0.82F,
            0.85F,
            0.88F,
            0.28F
         );
         RenderUtils.AddParticle(this.f_107208_, particle);
      }
   }

   public boolean shouldCull() {
      return false;
   }
}
