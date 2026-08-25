package org.merlin204.mimic.entity.shadow;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.merlin204.mimic.entity.MimicEntity;
import org.merlin204.mimic.entity.proteus.ProteusEntity;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class ShadowMimicEntity extends MimicEntity {
   protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNER_UUID = SynchedEntityData.m_135353_(
      ShadowMimicEntity.class, EntityDataSerializers.f_135041_
   );
   protected static final EntityDataAccessor<Integer> DATA_OWNER_ID = SynchedEntityData.m_135353_(ShadowMimicEntity.class, EntityDataSerializers.f_135028_);
   private final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("mimic", "textures/entity/mimic_s.png");
   private final ResourceLocation TEXTURE_L = ResourceLocation.fromNamespaceAndPath("mimic", "textures/entity/mimic_s_l.png");

   public ShadowMimicEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
      super(entityType, level);
   }

   public ShadowMimicEntity(EntityType<? extends PathfinderMob> entityType, Level level, LivingEntity livingEntity) {
      super(entityType, level);
      this.tame(livingEntity);
   }

   @Override
   public void m_8119_() {
      super.m_8119_();
      LivingEntity owner = this.getOwner();
      if (owner == null) {
         this.m_146870_();
      } else {
         if (!this.m_9236_().f_46443_) {
            Vec3 ownerPosition = owner.m_20182_();
            if (this.m_20182_().m_82554_(ownerPosition) > 40.0) {
               this.m_146884_(ownerPosition);
            }
         }
      }
   }

   @Override
   public boolean m_6469_(@NotNull DamageSource damageSource, float amount) {
      LivingEntity owner = this.getOwner();
      if (damageSource.m_7639_() instanceof ShadowMimicEntity shadowMimicEntity && owner != null && shadowMimicEntity.getOwner() == owner) {
         return false;
      }

      return owner != null && damageSource.m_7639_() == owner ? false : super.m_6469_(damageSource, amount);
   }

   public void m_6667_(DamageSource p_21014_) {
      if (!this.m_9236_().f_46443_ && this.getOwner() instanceof ProteusEntity proteus) {
         CompoundTag copyInfo = new CompoundTag();
         this.saveCopy(copyInfo);
         proteus.loadCopy(copyInfo);
      }

      super.m_6667_(p_21014_);
   }

   public static AttributeSupplier getDefaultAttribute() {
      return Mob.m_21552_()
         .m_22268_(Attributes.f_22276_, 200.0)
         .m_22268_(Attributes.f_22281_, 2.0)
         .m_22268_(Attributes.f_22284_, 20.0)
         .m_22268_(Attributes.f_22277_, 72.0)
         .m_22268_(Attributes.f_22278_, 1000.0)
         .m_22268_((Attribute)EpicFightAttributes.MAX_STRIKES.get(), 50.0)
         .m_22268_((Attribute)EpicFightAttributes.WEIGHT.get(), 2000.0)
         .m_22265_();
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(DATA_OWNER_UUID, Optional.empty());
      this.f_19804_.m_135372_(DATA_OWNER_ID, 0);
   }

   @Override
   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128405_("owner_id", this.getOwnerID());
      UUID ownerUuid = this.getOwnerUUID();
      if (ownerUuid != null) {
         tag.m_128362_("owner_uuid", ownerUuid);
      }
   }

   @Override
   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128441_("owner_id")) {
         this.setOwnerID(tag.m_128451_("owner_id"));
      }

      if (tag.m_128441_("owner_uuid")) {
         this.setOwnerUUID(tag.m_128342_("owner_uuid"));
      }
   }

   @Nullable
   public UUID getOwnerUUID() {
      return (UUID)((Optional)this.f_19804_.m_135370_(DATA_OWNER_UUID)).orElse(null);
   }

   public void setOwnerUUID(@Nullable UUID pUuid) {
      this.f_19804_.m_135381_(DATA_OWNER_UUID, Optional.ofNullable(pUuid));
   }

   public int getOwnerID() {
      return (Integer)this.f_19804_.m_135370_(DATA_OWNER_ID);
   }

   public void setOwnerID(int id) {
      this.f_19804_.m_135381_(DATA_OWNER_ID, id);
   }

   public void tame(@Nullable LivingEntity livingEntity) {
      if (livingEntity != null) {
         this.setOwnerUUID(livingEntity.m_20148_());
         this.setOwnerID(livingEntity.m_19879_());
      }
   }

   @Nullable
   public LivingEntity getOwner() {
      UUID uuid = this.getOwnerUUID();
      if (uuid != null) {
         Player player = this.m_9236_().m_46003_(uuid);
         if (player == null) {
            if (this.m_9236_() instanceof ServerLevel serverLevel) {
               return serverLevel.m_8791_(uuid) instanceof LivingEntity livingEntity ? livingEntity : null;
            } else {
               return this.m_9236_().m_6815_(this.getOwnerID()) instanceof LivingEntity livingEntity ? livingEntity : null;
            }
         } else {
            return player;
         }
      } else {
         return null;
      }
   }

   @Nullable
   public LivingEntityPatch<?> getOwnerPatch() {
      LivingEntity owner = this.getOwner();
      return owner == null ? null : (LivingEntityPatch)EpicFightCapabilities.getEntityPatch(owner, LivingEntityPatch.class);
   }

   @Nullable
   @Override
   public ResourceLocation getTexture() {
      return this.TEXTURE;
   }

   @Nullable
   @Override
   public ResourceLocation getLitTexture() {
      return this.TEXTURE_L;
   }

   @Nullable
   @Override
   public AssetAccessor<? extends SkinnedMesh> getMesh() {
      return MeshAccessor.create("mimic", "entity/mimic", jsonModelLoader -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
   }
}
