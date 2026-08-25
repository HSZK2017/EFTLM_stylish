package org.merlin204.mimic.entity;

import com.merlin204.avalon.entity.IAvalonMeshEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.merlin204.mimic.copy.CopyAnimationInfo;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class MimicEntity extends PathfinderMob implements IAvalonMeshEntity {
   private final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("mimic", "textures/entity/mimic.png");
   private final ResourceLocation TEXTURE_L = ResourceLocation.fromNamespaceAndPath("mimic", "textures/entity/mimic_l.png");
   private int protectTick = 0;
   private int hurtCount = 0;
   private int breakTick = 100;
   @Nullable
   private CompoundTag pendingCopyData;

   public MimicEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
      super(entityType, level);
   }

   public void m_8119_() {
      super.m_8119_();
      MimicPatch<?> patch = this.getPatch();
      if (patch != null && this.pendingCopyData != null) {
         this.loadCopyIntoPatch(this.pendingCopyData, patch);
         this.pendingCopyData = null;
      }

      if (!this.m_9236_().f_46443_ && patch != null) {
         if (patch.getStunShield() <= 0.0F) {
            this.breakTick--;
            if (this.breakTick < 0) {
               patch.setStunShield(patch.getMaxStunShield());
            }
         } else {
            this.breakTick = 100;
            if (this.protectTick == 0) {
               patch.setStunShield(patch.getStunShield() + patch.getMaxStunShield() * 0.01F);
            }
         }
      }

      if (this.protectTick > 0) {
         this.protectTick--;
         if (this.protectTick == 0) {
            this.hurtCount = 0;
         }
      }
   }

   @Nullable
   public MimicPatch<?> getPatch() {
      return (MimicPatch<?>)EpicFightCapabilities.getEntityPatch(this, MimicPatch.class);
   }

   public boolean m_6469_(@NotNull DamageSource damageSource, float amount) {
      if (amount > 30.0F) {
         amount = 30.0F;
      }

      MimicPatch<?> patch = this.getPatch();
      if (patch == null) {
         return super.m_6469_(damageSource, amount);
      } else {
         int animation = patch.copyMap.size();
         if (this.protectTick > 0) {
            this.hurtCount++;
            amount *= 1.0F - Math.min(0.9F, (float)this.hurtCount * 0.15F);
         }

         this.protectTick = 35;
         if (animation == 0) {
            this.m_5634_(99999.0F);
         }

         return patch.getStunShield() > 0.0F
            ? super.m_6469_(damageSource, amount * Math.min(0.0F + (float)animation * 0.8F, 1.5F) * 0.3F)
            : super.m_6469_(damageSource, amount * Math.min(0.0F + (float)animation * 0.8F, 1.5F));
      }
   }

   @Nullable
   public Armature getArmature() {
      return Armatures.BIPED.get();
   }

   public boolean m_6785_(double p_21542_) {
      return false;
   }

   public void saveCopy(CompoundTag tag) {
      if (tag != null) {
         MimicPatch<?> patch = this.getPatch();
         if (patch == null) {
            if (this.pendingCopyData != null) {
               tag.m_128391_(this.pendingCopyData.m_6426_());
            } else {
               tag.m_128405_("number", 0);
            }
         } else {
            int number = 0;

            for (CopyAnimationInfo copyAnimationInfo : patch.copyMap.values()) {
               if (copyAnimationInfo != null) {
                  String info = "copy_" + number;
                  CompoundTag copyTag = copyAnimationInfo.savaInTag();
                  if (copyTag != null) {
                     tag.m_128365_(info, copyTag);
                     number++;
                  }
               }
            }

            tag.m_128405_("number", number);
         }
      }
   }

   public void loadCopy(CompoundTag tag) {
      if (tag != null) {
         MimicPatch<?> patch = this.getPatch();
         if (patch == null) {
            this.pendingCopyData = this.extractCopyData(tag);
         } else {
            this.loadCopyIntoPatch(tag, patch);
         }
      }
   }

   private CompoundTag extractCopyData(CompoundTag tag) {
      CompoundTag copyData = new CompoundTag();
      int number = Math.max(0, tag.m_128451_("number"));
      copyData.m_128405_("number", number);

      for (int i = 0; i < number; i++) {
         String key = "copy_" + i;
         if (tag.m_128441_(key)) {
            copyData.m_128365_(key, tag.m_128469_(key).m_6426_());
         }
      }

      return copyData;
   }

   private void loadCopyIntoPatch(CompoundTag tag, MimicPatch<?> patch) {
      int number = tag.m_128451_("number");

      for (int i = 0; i < number; i++) {
         String info = "copy_" + i;
         if (tag.m_128441_(info)) {
            CopyAnimationInfo copyAnimationInfo = CopyAnimationInfo.loadFormTag(tag.m_128469_(info), patch);
            if (copyAnimationInfo != null) {
               patch.copyMap.put(copyAnimationInfo.animation, copyAnimationInfo);
            }
         }
      }
   }

   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      this.saveCopy(tag);
   }

   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      this.loadCopy(tag);
   }

   public static AttributeSupplier getDefaultAttribute() {
      return Mob.m_21552_()
         .m_22268_(Attributes.f_22276_, 1000.0)
         .m_22268_(Attributes.f_22281_, 5.0)
         .m_22268_(Attributes.f_22284_, 20.0)
         .m_22268_(Attributes.f_22277_, 72.0)
         .m_22268_(Attributes.f_22278_, 1000.0)
         .m_22268_((Attribute)EpicFightAttributes.MAX_STRIKES.get(), 50.0)
         .m_22268_((Attribute)EpicFightAttributes.WEIGHT.get(), 2000.0)
         .m_22265_();
   }

   @Nullable
   public ResourceLocation getTexture() {
      return this.TEXTURE;
   }

   @Nullable
   public ResourceLocation getLitTexture() {
      return this.TEXTURE_L;
   }

   @Nullable
   public AssetAccessor<? extends SkinnedMesh> getMesh() {
      return MeshAccessor.create("mimic", "entity/mimic", jsonModelLoader -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
   }
}
