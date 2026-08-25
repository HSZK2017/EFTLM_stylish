package com.dmc.invincible_dmc.entity.judgementcut;

import com.dmc.invincible_dmc.client.model.DMCArmatures;
import com.dmc.invincible_dmc.client.vfx.YamatoTearEffects;
import com.dmc.invincible_dmc.gameassets.animations.yamato.SummonedSwordAnimations;
import com.dmc.invincible_dmc.particle.DMCParticles;
import com.merlin204.avalon.entity.IAvalonMeshEntity;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class JudgementCutEntity extends Mob implements IAvalonMeshEntity {
   protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.m_135353_(
      JudgementCutEntity.class, EntityDataSerializers.f_135041_
   );
   protected static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.m_135353_(JudgementCutEntity.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Byte> CLIENT_FX = SynchedEntityData.m_135353_(JudgementCutEntity.class, EntityDataSerializers.f_135027_);
   private static final EntityDataAccessor<Byte> SEQUENCE_STYLE = SynchedEntityData.m_135353_(JudgementCutEntity.class, EntityDataSerializers.f_135027_);
   protected static final EntityDataAccessor<Boolean> PLAY_ANIMATION = SynchedEntityData.m_135353_(JudgementCutEntity.class, EntityDataSerializers.f_135035_);
   protected static final EntityDataAccessor<Boolean> SHOULD_RENDER = SynchedEntityData.m_135353_(JudgementCutEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> IS_PERFECT = SynchedEntityData.m_135353_(JudgementCutEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> IS_RAPID_SLASH = SynchedEntityData.m_135353_(JudgementCutEntity.class, EntityDataSerializers.f_135035_);
   private static final EntityDataAccessor<Boolean> SPAWN_TEAR_EFFECT = SynchedEntityData.m_135353_(JudgementCutEntity.class, EntityDataSerializers.f_135035_);
   private static final MeshAccessor<SkinnedMesh> JUDGEMENT_CUT_MESH = MeshAccessor.create(
      "invincible_dmc", "entity/effect/judgement_cut", jsonModelLoader -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new)
   );
   public static final byte FX_NONE = 0;
   public static final byte FX_JUDGEMENT_CUT = 1;
   public static final byte FX_JUDGEMENT_CUT_NORMAL = 2;
   private boolean clientParticlesSpawned = false;

   public JudgementCutEntity(EntityType<? extends Mob> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_19811_ = true;
      this.m_20331_(true);
      this.f_19794_ = true;
      this.m_20242_(true);
   }

   public JudgementCutEntity(EntityType<? extends Mob> pEntityType, LivingEntity owner, Level pLevel) {
      super(pEntityType, pLevel);
      this.tame(owner);
      this.f_19811_ = true;
      this.m_20331_(true);
      this.f_19794_ = true;
      this.m_20242_(true);
   }

   public void tame(LivingEntity livingEntity) {
      this.setOwnerUUID(livingEntity.m_20148_());
      this.setOwnerID(livingEntity.m_19879_());
      this.setSequenceStyle(JudgementCutStyleSync.getOwnerStyle(livingEntity));
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.m_9236_().f_46443_ && !this.clientParticlesSpawned) {
         this.clientParticlesSpawned = true;
         byte fx = (Byte)this.f_19804_.m_135370_(CLIENT_FX);
         boolean useDmc4Style = (Byte)this.f_19804_.m_135370_(SEQUENCE_STYLE) == 1;
         if (fx == 1) {
            this.m_9236_()
               .m_7106_(
                  useDmc4Style ? (ParticleOptions)DMCParticles.JUDGEMENT_CUT_SEQUENCE_DMC4.get() : (ParticleOptions)DMCParticles.JUDGEMENT_CUT_SEQUENCE.get(),
                  this.m_20185_(),
                  this.m_20186_() + 0.5,
                  this.m_20189_(),
                  0.0,
                  0.0,
                  0.0
               );
         } else if (fx == 2) {
            this.m_9236_()
               .m_7106_(
                  useDmc4Style
                     ? (ParticleOptions)DMCParticles.JUDGEMENT_CUT_SEQUENCE_NORMAL_DMC4.get()
                     : (ParticleOptions)DMCParticles.JUDGEMENT_CUT_SEQUENCE_NORMAL.get(),
                  this.m_20185_(),
                  this.m_20186_() - 0.5,
                  this.m_20189_(),
                  0.0,
                  0.0,
                  0.0
               );
         }

         if ((Boolean)this.f_19804_.m_135370_(SPAWN_TEAR_EFFECT)) {
            double yOffset = fx == 2 ? -0.5 : 0.5;
            YamatoTearEffects.playJudgementCut(this.m_9236_(), new Vec3(this.m_20185_(), this.m_20186_() + yOffset, this.m_20189_()));
         }
      }
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_OWNER_UUID, Optional.empty());
      this.f_19804_.m_135372_(DATA_OWNER_ID, 0);
      this.f_19804_.m_135372_(PLAY_ANIMATION, false);
      this.f_19804_.m_135372_(SHOULD_RENDER, false);
      this.f_19804_.m_135372_(CLIENT_FX, (byte)0);
      this.f_19804_.m_135372_(SEQUENCE_STYLE, (byte)0);
      this.f_19804_.m_135372_(IS_PERFECT, true);
      this.f_19804_.m_135372_(IS_RAPID_SLASH, false);
      this.f_19804_.m_135372_(SPAWN_TEAR_EFFECT, false);
   }

   @Nullable
   public LivingEntity getOwner() {
      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         if (this.m_9236_() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.m_8791_(uuid);
            return entity instanceof LivingEntity ? (LivingEntity)entity : null;
         } else {
            Player player = this.m_9236_().m_46003_(uuid);
            if (player != null) {
               return player;
            } else {
               Entity entity = this.m_9236_().m_6815_(this.getOwnerID());
               return entity instanceof LivingEntity ? (LivingEntity)entity : null;
            }
         }
      } else {
         return null;
      }
   }

   public void setClientFx(byte fx) {
      this.f_19804_.m_135381_(CLIENT_FX, fx);
   }

   public void setSequenceStyle(byte style) {
      this.f_19804_.m_135381_(SEQUENCE_STYLE, JudgementCutStyleSync.normalize(style));
   }

   public boolean getPlayAnimation() {
      return (Boolean)this.f_19804_.m_135370_(PLAY_ANIMATION);
   }

   public void setPlayAnimation(boolean b) {
      this.f_19804_.m_135381_(PLAY_ANIMATION, b);
   }

   public boolean getShouldRender() {
      return (Boolean)this.f_19804_.m_135370_(SHOULD_RENDER);
   }

   public void setShouldRender(boolean b) {
      this.f_19804_.m_135381_(SHOULD_RENDER, b);
   }

   public int getOwnerID() {
      return (Integer)this.f_19804_.m_135370_(DATA_OWNER_ID);
   }

   public void setOwnerID(int id) {
      this.f_19804_.m_135381_(DATA_OWNER_ID, id);
   }

   @Nullable
   public UUID getOwnerUUID() {
      return (UUID)((Optional)this.f_19804_.m_135370_(DATA_OWNER_UUID)).orElse(null);
   }

   public void setOwnerUUID(@Nullable UUID pUuid) {
      this.f_19804_.m_135381_(DATA_OWNER_UUID, Optional.ofNullable(pUuid));
   }

   public static AttributeSupplier getDefaultAttribute() {
      return Animal.m_21552_()
         .m_22268_(Attributes.f_22276_, 19.9F)
         .m_22268_(Attributes.f_22281_, 3.0)
         .m_22268_((Attribute)EpicFightAttributes.MAX_STRIKES.get(), 10.0)
         .m_22265_();
   }

   public boolean m_6094_() {
      return false;
   }

   public boolean m_6087_() {
      return false;
   }

   public boolean m_142066_() {
      return false;
   }

   public boolean m_20068_() {
      return true;
   }

   public boolean m_5829_() {
      return false;
   }

   protected void m_7324_(@NotNull Entity pEntity) {
   }

   public void m_7334_(@NotNull Entity pEntity) {
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getIdleAnimation() {
      return SummonedSwordAnimations.JUDGEMENT_CUT_ENTITY_IDLE;
   }

   @Nullable
   public AnimationAccessor<? extends StaticAnimation> getDefaultAnimation() {
      if (this.isRapidSlash()) {
         return SummonedSwordAnimations.RAPID_SLASH_ATTACK;
      } else {
         return this.isPerfect() ? SummonedSwordAnimations.JUDGEMENT_CUT_ENTITY_ATTACK : SummonedSwordAnimations.JUDGEMENT_CUT_ENTITY_ATTACK_NORMAL;
      }
   }

   public boolean isPerfect() {
      return (Boolean)this.f_19804_.m_135370_(IS_PERFECT);
   }

   public void setPerfect(boolean perfect) {
      this.f_19804_.m_135381_(IS_PERFECT, perfect);
   }

   public boolean isRapidSlash() {
      return (Boolean)this.f_19804_.m_135370_(IS_RAPID_SLASH);
   }

   public void setRapidSlash(boolean rapidSlash) {
      this.f_19804_.m_135381_(IS_RAPID_SLASH, rapidSlash);
   }

   public void setSpawnTearEffect(boolean spawnTearEffect) {
      this.f_19804_.m_135381_(SPAWN_TEAR_EFFECT, spawnTearEffect);
   }

   @Nullable
   public Armature getArmature() {
      return DMCArmatures.JUDGEMENT_CUT.get();
   }

   @Nullable
   public AssetAccessor<? extends SkinnedMesh> getMesh() {
      return JUDGEMENT_CUT_MESH;
   }

   @Nullable
   public ResourceLocation getTexture() {
      return ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/entity/empty.png");
   }

   @Nullable
   public ResourceLocation getLitTexture() {
      return ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/entity/empty.png");
   }

   public boolean m_6469_(@NotNull DamageSource source, float p_21017_) {
      return false;
   }

   protected void m_7355_(@NotNull BlockPos pPos, @NotNull BlockState pState) {
   }

   public boolean m_142535_(float pFallDistance, float pMultiplier, @NotNull DamageSource pSource) {
      return false;
   }

   public boolean m_5843_() {
      return false;
   }

   public static void discardAllOwnedBy(LivingEntity owner) {
      if (owner != null && !owner.m_9236_().f_46443_) {
         ArrayList<JudgementCutEntity> toDiscard = new ArrayList<>();

         for (Entity entry : owner.m_9236_().m_142646_().m_142273_()) {
            if (entry instanceof JudgementCutEntity) {
               JudgementCutEntity jc = (JudgementCutEntity)entry;
               if (jc.m_6084_() && jc.isRapidSlash() && owner.equals(jc.getOwner())) {
                  toDiscard.add(jc);
               }
            }
         }

         for (JudgementCutEntity jc : toDiscard) {
            jc.m_146870_();
         }
      }
   }
}
