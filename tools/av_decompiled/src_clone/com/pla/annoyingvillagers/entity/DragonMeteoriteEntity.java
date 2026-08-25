package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.block.EndFireBlock;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModBlocks;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.EnderSlayerScytheItem;
import com.pla.annoyingvillagers.task.DelayedTask;
import com.pla.annoyingvillagers.util.ScreenShakeUtil;
import com.pla.annoyingvillagers.util.WeaponEnchantmentDamageUtil;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DragonMeteoriteEntity extends PathfinderMob {
   private Vec3 posToAim;
   private HerobrineDragonEntity owner;
   private boolean motionInited = false;
   private double xd = 0.0;
   private double yd = 0.0;
   private double zd = 0.0;

   public void setPosToAim(@Nullable Vec3 pos) {
      this.posToAim = pos;
      this.motionInited = false;
   }

   public HerobrineDragonEntity getOwner() {
      return this.owner;
   }

   public void setOwner(HerobrineDragonEntity owner) {
      this.owner = owner;
   }

   private float getExplosionDamage() {
      LivingEntity summoner = this.owner != null ? this.owner.getSummoner() : null;
      return WeaponEnchantmentDamageUtil.addSharpnessBonus(12.0F, summoner, EnderSlayerScytheItem.class);
   }

   public DragonMeteoriteEntity(SpawnEntity spawnEntity, Level level) {
      this((EntityType<DragonMeteoriteEntity>)AnnoyingVillagersModEntities.DRAGON_METEORITE.get(), level);
   }

   public DragonMeteoriteEntity(EntityType<DragonMeteoriteEntity> entitytype, Level level) {
      super(entitytype, level);
      this.m_274367_(0.6F);
      this.f_21364_ = 0;
      this.m_21557_(false);
      this.m_21530_();
   }

   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      if (this.posToAim != null) {
         tag.m_128347_("AimX", this.posToAim.f_82479_);
         tag.m_128347_("AimY", this.posToAim.f_82480_);
         tag.m_128347_("AimZ", this.posToAim.f_82481_);
      }
   }

   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128441_("AimX") && tag.m_128441_("AimY") && tag.m_128441_("AimZ")) {
         this.posToAim = new Vec3(tag.m_128459_("AimX"), tag.m_128459_("AimY"), tag.m_128459_("AimZ"));
      } else {
         this.posToAim = null;
      }

      this.motionInited = false;
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   protected void m_8099_() {
      super.m_8099_();
   }

   @NotNull
   public MobType m_6336_() {
      return MobType.f_21640_;
   }

   public boolean m_6785_(double d0) {
      return false;
   }

   public boolean m_6469_(DamageSource damagesource, float f) {
      return !damagesource.m_276093_(DamageTypes.f_268631_)
         && !(damagesource.m_7640_() instanceof AbstractArrow)
         && !(damagesource.m_7640_() instanceof Player)
         && !(damagesource.m_7640_() instanceof ThrownPotion)
         && !(damagesource.m_7640_() instanceof AreaEffectCloud)
         && !damagesource.m_276093_(DamageTypes.f_268671_)
         && !damagesource.m_276093_(DamageTypes.f_268585_)
         && !damagesource.m_276093_(DamageTypes.f_268722_)
         && !damagesource.m_276093_(DamageTypes.f_268450_)
         && !damagesource.m_276093_(DamageTypes.f_268565_)
         && !damagesource.m_276093_(DamageTypes.f_268448_)
         && !damagesource.m_276093_(DamageTypes.f_268714_)
         && !damagesource.m_276093_(DamageTypes.f_268526_)
         && !damagesource.m_276093_(DamageTypes.f_268482_)
         && !damagesource.m_276093_(DamageTypes.f_268493_)
         && !damagesource.m_276093_(DamageTypes.f_268641_)
         && super.m_6469_(damagesource, f);
   }

   public boolean m_6128_() {
      return true;
   }

   public boolean m_5825_() {
      return true;
   }

   @Nullable
   public SpawnGroupData m_6518_(
      @NotNull ServerLevelAccessor pLevel,
      @NotNull DifficultyInstance pDifficulty,
      @NotNull MobSpawnType pReason,
      @Nullable SpawnGroupData pSpawnData,
      @Nullable CompoundTag pDataTag
   ) {
      this.m_20331_(true);
      this.m_5496_((SoundEvent)AnnoyingVillagersModSounds.MUFFLED_BOOM.get(), new Random().nextFloat(34.0F, 42.0F), new Random().nextFloat(0.0F, 0.2F));
      return super.m_6518_(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
   }

   public void m_6075_() {
      super.m_6075_();
      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         final double d0 = this.m_20185_();
         final double d1 = this.m_20186_();
         final double d2 = this.m_20189_();
         this.m_20331_(true);
         if (!this.motionInited) {
            if (this.posToAim != null) {
               Vec3 from = this.m_20182_();
               Vec3 dir = this.posToAim.m_82546_(from);
               double dist = dir.m_82553_();
               if (dist > 1.0E-4) {
                  double speed = 1.8;
                  Vec3 vel = dir.m_82490_(speed / dist);
                  this.xd = vel.f_82479_;
                  this.yd = vel.f_82480_;
                  this.zd = vel.f_82481_;
               } else {
                  this.xd = this.yd = this.zd = 0.0;
               }
            } else {
               RandomSource r = serverLevel.m_213780_();
               this.xd = Mth.m_216263_(r, -0.7, 0.7);
               this.yd = -1.8;
               this.zd = Mth.m_216263_(r, -0.7, 0.7);
            }

            this.motionInited = true;
         }

         if (this.m_20096_() || this.m_5830_() || this.posToAim != null && this.m_20182_().m_82557_(this.posToAim) < 1.0) {
            serverLevel.m_254849_(null, d0, d1, d2, new Random().nextFloat(2.0F, 4.0F), ExplosionInteraction.MOB);
            ScreenShakeUtil.applyScreenShake(serverLevel, this.m_20182_(), 24.0, 20, 6);
            RandomSource randomSource = serverLevel.m_213780_();
            BlockState endfireState = ((EndFireBlock)AnnoyingVillagersModBlocks.END_FIRE.get()).m_49966_();

            for (int i = 0; i < 25; i++) {
               BlockPos pos = BlockPos.m_274561_(d0, d1, d2);
               serverLevel.m_7731_(pos, endfireState, 3);
               FallingBlockEntity fallingBlockEntity = FallingBlockEntity.m_201971_(serverLevel, pos, endfireState);
               fallingBlockEntity.f_31942_ = 1;
               fallingBlockEntity.f_31943_ = false;
               double vx = Mth.m_216263_(randomSource, -0.15, 0.15);
               double vy = Mth.m_216263_(randomSource, 0.2, 0.5);
               double vz = Mth.m_216263_(randomSource, -0.15, 0.15);
               fallingBlockEntity.m_20334_(vx, vy, vz);
               fallingBlockEntity.f_19812_ = true;
            }

            FallingBlockEntity.m_201971_(serverLevel, BlockPos.m_274561_(d0, d1, d2), Blocks.f_50723_.m_49966_());
            serverLevel.m_8767_(ParticleTypes.f_123812_, d0, d1, d2, 1, 0.0, 0.0, 0.0, 0.0);
            Vec3 center = new Vec3(d0, d1, d2);
            AABB box = new AABB(center, center).m_82400_(10.0);
            Registry<DamageType> damageTypeReg = serverLevel.m_9598_().m_175515_(Registries.f_268580_);
            DamageSource damageSource = new DamageSource(damageTypeReg.m_246971_(DamageTypes.f_268565_), this);
            LivingEntity summoner = this.owner != null ? this.owner.getSummoner() : null;
            float damage = this.getExplosionDamage();

            for (LivingEntity entity : serverLevel.m_6443_(
               LivingEntity.class,
               box,
               livingEntity -> livingEntity.m_6084_()
                     && !(livingEntity instanceof DragonMeteoriteEntity)
                     && !(livingEntity instanceof HerobrineDragonEntity)
                     && livingEntity != summoner
            )) {
               Vec3 dir = entity.m_20182_().m_82546_(center);
               double dist = Math.max(0.001, dir.m_82553_());
               double falloff = 1.0 - Math.min(dist / 10.0, 1.0);
               Vec3 push = dir.m_82490_(1.0 / dist).m_82490_(1.2 * falloff).m_82520_(0.0, 0.35 * falloff, 0.0);
               entity.m_20256_(entity.m_20184_().m_82549_(push));
               if (summoner != null) {
                  entity.m_6469_(this.m_269291_().m_269104_(this, summoner), damage);
               } else {
                  entity.m_6469_(damageSource, damage);
               }

               entity.f_19812_ = true;
            }

            this.m_5496_(SoundEvents.f_11913_, 5.0F, 0.0F);
            this.m_5496_(SoundEvents.f_11935_, 6.0F, 0.0F);
            this.m_5496_(SoundEvents.f_12090_, 10.0F, 0.0F);
            this.m_146870_();
         }

         this.m_20242_(true);
         this.m_20334_(this.xd, this.yd, this.zd);
         this.f_19812_ = true;
         if (this.posToAim != null) {
            Vec3 fromEye = this.m_146892_();
            Vec3 toEye = this.posToAim;
            double dx = toEye.f_82479_ - fromEye.f_82479_;
            double dz = toEye.f_82481_ - fromEye.f_82481_;
            double dy = toEye.f_82480_ - fromEye.f_82480_;
            double distXZ = Math.sqrt(dx * dx + dz * dz);
            float yaw = (float)(Mth.m_14136_(dz, dx) * (180.0 / Math.PI)) - 90.0F;
            float pitch = (float)(-(Mth.m_14136_(dy, distXZ) * (180.0 / Math.PI)));
            this.m_146922_(yaw);
            this.m_5616_(yaw);
            this.m_5618_(yaw);
            this.m_146926_(pitch);
         }

         double d3 = -5.0;

         for (int i = 0; i < 10; i++) {
            double d4 = -5.0;

            for (int j = 0; j < 10; j++) {
               double d5 = -5.0;

               for (int k = 0; k < 10; k++) {
                  if ((double)serverLevel.m_8055_(BlockPos.m_274561_(d0 + d3, d1 + d4, d2 + d5))
                           .m_60800_(serverLevel, BlockPos.m_274561_(d0 + d3, d1 + d4, d2 + d5))
                        < 0.4
                     && serverLevel.m_8055_(BlockPos.m_274561_(d0 + d3, d1 + d4, d2 + d5)).m_60800_(serverLevel, BlockPos.m_274561_(d0 + d3, d1 + d4, d2 + d5))
                        >= 0.0F) {
                     serverLevel.m_46961_(BlockPos.m_274561_(d0 + d3, d1 + d4, d2 + d5), false);
                     serverLevel.m_46672_(
                        BlockPos.m_274561_(d0 + d3, d1 + d4, d2 + d5), serverLevel.m_8055_(BlockPos.m_274561_(d0 + d3, d1 + d4, d2 + d5)).m_60734_()
                     );
                     serverLevel.m_186460_(
                        BlockPos.m_274561_(d0 + d3, d1 + d4, d2 + d5), serverLevel.m_8055_(BlockPos.m_274561_(d0 + d3, d1 + d4, d2 + d5)).m_60734_(), 1
                     );
                  }

                  if (serverLevel.m_8055_(BlockPos.m_274561_(d0 + d3, d1 + d4, d2 + d5))
                     .m_204336_(BlockTags.create(ResourceLocation.fromNamespaceAndPath("minecraft", "logs")))) {
                     serverLevel.m_46961_(BlockPos.m_274561_(d0 + d3, d1 + d4, d2 + d5), false);
                     serverLevel.m_46672_(
                        BlockPos.m_274561_(d0 + d3, d1 + d4, d2 + d5), serverLevel.m_8055_(BlockPos.m_274561_(d0 + d3, d1 + d4, d2 + d5)).m_60734_()
                     );
                     serverLevel.m_186460_(
                        BlockPos.m_274561_(d0 + d3, d1 + d4, d2 + d5), serverLevel.m_8055_(BlockPos.m_274561_(d0 + d3, d1 + d4, d2 + d5)).m_60734_(), 1
                     );
                  }

                  d5++;
               }

               d4++;
            }

            d3++;
         }

         if (this.m_20072_()) {
            for (int i = 0; i < 10; i++) {
               serverLevel.m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.BIG_SPLASH.get(),
                  d0 + Mth.m_216263_(RandomSource.m_216327_(), -1.0, 1.0),
                  d1 + 2.0,
                  d2 + Mth.m_216263_(RandomSource.m_216327_(), -1.0, 1.0),
                  0.0,
                  1.0,
                  0.0
               );
            }
         }

         if (!this.m_20072_()) {
            final Entity entity = this;
            new DelayedTask(2) {
               @Override
               public void run() {
                  if (entity.m_20072_()) {
                     serverLevel.m_5594_(null, BlockPos.m_274561_(d0, d1, d2), SoundEvents.f_12278_, SoundSource.NEUTRAL, 6.0F, 0.0F);
                  }
               }
            };
         }

         serverLevel.m_8767_(ParticleTypes.f_123813_, d0, d1 + 0.5, d2, 0, 0.0, 1.0, 0.0, 0.0);
         serverLevel.m_8767_(ParticleTypes.f_123747_, d0, d1 + 0.5, d2, 0, 0.0, 1.0, 0.0, 0.0);
         serverLevel.m_8767_((SimpleParticleType)AnnoyingVillagersModParticleTypes.METEORITE_TRAIL.get(), d0, d1 + 0.5, d2, 0, 0.0, 0.01, 0.0, 0.0);

         for (int i = 0; i < 20; i++) {
            serverLevel.m_8767_(
               ParticleTypes.f_123777_,
               d0 + Mth.m_216263_(RandomSource.m_216327_(), -2.0, 2.0),
               d1 + Mth.m_216263_(RandomSource.m_216327_(), -1.5, 2.5),
               d2 + Mth.m_216263_(RandomSource.m_216327_(), -2.0, 2.0),
               0,
               0.0,
               0.01,
               0.0,
               0.0
            );
         }
      }
   }

   public static void init() {
   }

   public static Builder createAttributes() {
      Builder builder = Mob.m_21552_();
      builder = builder.m_22268_(Attributes.f_22279_, 0.3);
      builder = builder.m_22268_(Attributes.f_22276_, 10.0);
      builder = builder.m_22268_(Attributes.f_22284_, 0.0);
      builder = builder.m_22268_(Attributes.f_22281_, 3.0);
      return builder.m_22268_(Attributes.f_22277_, 200.0);
   }
}
