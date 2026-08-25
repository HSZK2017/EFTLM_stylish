package com.pla.annoyingvillagers.entity;

import com.mojang.logging.LogUtils;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import java.util.Objects;
import net.mehvahdjukaar.dummmmmmy.common.TargetDummyEntity;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.ModList;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

public class FlyingShockwaveProjectile extends Projectile {
   protected int lifetime = 40;
   protected Vec3 deceleration = null;
   protected double decelerationConstant = 0.2;
   protected float damage = 1.0F;
   protected int maxStrikes = 1;

   public FlyingShockwaveProjectile(EntityType<? extends Projectile> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
   }

   public void m_8119_() {
      super.m_8119_();
      Vec3 originalVec = this.m_20184_();
      if (this.deceleration == null) {
         this.deceleration = originalVec.m_82542_(this.decelerationConstant, this.decelerationConstant, this.decelerationConstant);
      }

      HitResult hitresult = ProjectileUtil.m_278158_(this, this::m_5603_);
      if (hitresult.m_6662_() != Type.MISS && !ForgeEventFactory.onProjectileImpact(this, hitresult)) {
         this.m_6532_(hitresult);
      }

      double d7;
      double d2;
      double d3;
      if (this.magnitude(originalVec) > 0.0) {
         Vec3 moveVec = originalVec.m_82546_(this.deceleration);
         double d5 = moveVec.f_82479_;
         double d6 = moveVec.f_82480_;
         double d1 = moveVec.f_82481_;
         d7 = this.m_20185_() + d5;
         d2 = this.m_20186_() + d6;
         d3 = this.m_20189_() + d1;
      } else {
         d7 = this.m_20185_() + 0.0;
         d2 = this.m_20186_() + 0.0;
         d3 = this.m_20189_() + 0.0;
      }

      this.m_6034_(d7, d2, d3);
      this.lifetime--;
      if (this.lifetime <= 0) {
         this.m_146870_();
      }
   }

   private double magnitude(Vec3 vec) {
      return Math.sqrt(vec.f_82479_ * vec.f_82479_ + vec.f_82480_ * vec.f_82480_ + vec.f_82481_ * vec.f_82481_);
   }

   public void m_6686_(double pX, double pY, double pZ, float pVelocity, float pInaccuracy) {
      super.m_6686_(pX, pY, pZ, pVelocity, pInaccuracy);
   }

   public void setDamage(float damage) {
      this.damage = damage;
   }

   protected void m_6532_(HitResult pResult) {
      super.m_6532_(pResult);
   }

   public void setMaxStrikes(int maxStrikes) {
      this.maxStrikes = maxStrikes;
   }

   protected void m_5790_(@NotNull EntityHitResult hitResult) {
      super.m_5790_(hitResult);
      LogUtils.getLogger().debug("Smack!");
      if (!this.m_9236_().m_5776_()) {
         Entity entity = hitResult.m_82443_();
         Entity entity1 = this.m_19749_();
         PlayerPatch<?> playerpatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(this.m_19749_(), PlayerPatch.class);
         if (entity1 instanceof LivingEntity livingEntity && playerpatch != null) {
            LogUtils.getLogger().debug("Check passed");
            if (!(entity instanceof Enemy) && (!ModList.get().isLoaded("dummmmmmy") || !(entity instanceof TargetDummyEntity))) {
               if (entity instanceof TamableAnimal pet
                  && (
                     Objects.requireNonNull(pet.m_269323_()).m_7306_(entity1)
                        || pet.m_269323_().m_5647_() == entity1.m_5647_()
                        || pet.m_269323_().m_5647_() != null && pet.m_269323_().m_5647_().m_83536_(entity1.m_5647_())
                  )) {
                  LogUtils.getLogger().debug("Pet");
                  return;
               }

               if (livingEntity.m_5647_() == entity1.m_5647_() || livingEntity.m_5647_() != null && livingEntity.m_5647_().m_83536_(entity1.m_5647_())) {
                  LogUtils.getLogger().debug("Teammate");
                  return;
               }
            }

            EpicFightDamageSource damage = playerpatch.getDamageSource(AnimsWom.WARBLADE_SATSUJIN_TSUKUYOMI, InteractionHand.MAIN_HAND);
            damage.setStunType(StunType.HOLD);
            damage.setBaseImpact(0.5F);
            damage.addRuntimeTag(EpicFightDamageTypeTags.WEAPON_INNATE);
            entity.f_19802_ = 0;
            playerpatch.attack(damage, entity, InteractionHand.MAIN_HAND);
            entity.m_5496_((SoundEvent)EpicFightSounds.BLADE_HIT.get(), 1.0F, 1.0F);
            entity.m_9236_()
               .m_7106_((ParticleOptions)EpicFightParticles.HIT_BLADE.get(), entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), 0.0, 0.0, 0.0);
            this.m_146870_();
            return;
         }

         entity.m_6469_(this.m_269291_().m_269425_(), 6.0F);
      }
   }

   protected void m_8097_() {
   }

   protected boolean m_5603_(Entity pTarget) {
      return pTarget instanceof LivingEntity livingEntity ? true : super.m_5603_(pTarget);
   }

   public boolean isFoil() {
      return false;
   }
}
