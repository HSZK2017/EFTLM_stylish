package com.dmc.invincible_dmc.entity.soul;

import com.dmc.invincible_dmc.entity.DMCEntities;
import com.dmc.invincible_dmc.gameassets.animations.stun.CustomStunAnimations;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class SoulEntity extends PathfinderMob {
   static final int LIFETIME_TICKS = 400;
   private static final int LINGER_TICKS = 400;
   private static final float INITIAL_ALPHA = 0.7F;
   private static final float INITIAL_TINT = 0.0F;
   private static final Map<SoulEntity, UUID> DETONATABLE_SOULS = new ConcurrentHashMap<>();
   private boolean lingering = false;
   private static final EntityDataAccessor<Float> RENDER_ALPHA = SynchedEntityData.m_135353_(SoulEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> COLOR_TINT_INTENSITY = SynchedEntityData.m_135353_(SoulEntity.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.m_135353_(SoulEntity.class, EntityDataSerializers.f_135041_);
   private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.m_135353_(SoulEntity.class, EntityDataSerializers.f_135028_);

   public SoulEntity(EntityType<? extends SoulEntity> type, Level level) {
      super(type, level);
   }

   public static void spawn(ServerLevel level, Vec3 pos, @Nullable Entity owner) {
      SoulEntity soul = (SoulEntity)((EntityType)DMCEntities.SOUL.get()).m_20615_(level);
      if (soul != null) {
         soul.m_7678_(pos.f_82479_, pos.f_82480_, pos.f_82481_, level.f_46441_.m_188501_() * 360.0F, 0.0F);
         soul.setOwner(owner);
         level.m_7967_(soul);
         SoulPatch<?> soulPatch = (SoulPatch<?>)EpicFightCapabilities.getEntityPatch(soul, SoulPatch.class);
         if (soulPatch != null) {
            soulPatch.playAnimationSynchronized(CustomStunAnimations.HIT_UP_4, 0.0F);
         }
      }
   }

   public static Builder createAttributes() {
      return PathfinderMob.m_21552_()
         .m_22268_(Attributes.f_22276_, 1024.0)
         .m_22268_(Attributes.f_22278_, 1.0)
         .m_22268_(Attributes.f_22281_, 0.0)
         .m_22266_((Attribute)EpicFightAttributes.WEIGHT.get())
         .m_22266_((Attribute)EpicFightAttributes.ARMOR_NEGATION.get())
         .m_22266_((Attribute)EpicFightAttributes.IMPACT.get())
         .m_22266_((Attribute)EpicFightAttributes.MAX_STRIKES.get())
         .m_22266_(Attributes.f_22279_);
   }

   public float getRenderAlpha() {
      return (Float)this.f_19804_.m_135370_(RENDER_ALPHA);
   }

   public void setRenderAlpha(float alpha) {
      this.f_19804_.m_135381_(RENDER_ALPHA, Math.min(1.0F, Math.max(0.0F, alpha)));
   }

   public float getColorTintIntensity() {
      return (Float)this.f_19804_.m_135370_(COLOR_TINT_INTENSITY);
   }

   public void setColorTintIntensity(float intensity) {
      this.f_19804_.m_135381_(COLOR_TINT_INTENSITY, Math.min(1.0F, Math.max(0.0F, intensity)));
   }

   public Optional<UUID> getOwnerUUID() {
      return (Optional<UUID>)this.f_19804_.m_135370_(OWNER_UUID);
   }

   public void setOwner(@Nullable Entity owner) {
      if (owner == null) {
         this.f_19804_.m_135381_(OWNER_UUID, Optional.empty());
         this.f_19804_.m_135381_(OWNER_ID, -1);
      } else {
         this.f_19804_.m_135381_(OWNER_UUID, Optional.of(owner.m_20148_()));
         this.f_19804_.m_135381_(OWNER_ID, owner.m_19879_());
         if (!this.m_9236_().m_5776_()) {
            Scoreboard sb = this.m_9236_().m_6188_();
            PlayerTeam team = owner instanceof LivingEntity le ? (PlayerTeam)le.m_5647_() : null;
            if (team == null) {
               team = sb.m_83500_(owner.m_6302_());
            }

            if (team == null) {
               team = sb.m_83492_("dmc_soul_" + owner.m_20149_().replace("-", "").substring(0, 12));
               sb.m_6546_(owner.m_6302_(), team);
            }

            sb.m_6546_(this.m_20149_(), team);
         }
      }
   }

   @Nullable
   public Entity getOwnerEntity() {
      int id = (Integer)this.f_19804_.m_135370_(OWNER_ID);
      return id >= 0 ? this.m_9236_().m_6815_(id) : null;
   }

   public boolean isOwner(@Nullable Entity entity) {
      if (entity == null) {
         return false;
      } else {
         int ownerId = (Integer)this.f_19804_.m_135370_(OWNER_ID);
         return ownerId >= 0 && ownerId == entity.m_19879_();
      }
   }

   public boolean m_7307_(@NotNull Entity entity) {
      Entity owner = this.getOwnerEntity();
      return owner != null && !(owner instanceof Player) ? entity.m_20148_().equals(this.getOwnerUUID().orElse(null)) : super.m_7307_(entity);
   }

   public void m_8119_() {
      super.m_8119_();
      int age = this.f_19797_;
      if (!this.m_9236_().m_5776_()) {
         Entity owner = this.getOwnerEntity();
         if (owner != null && !owner.m_6084_()) {
            this.m_146870_();
            return;
         }

         int totalTicks = 800;
         if (age >= totalTicks) {
            this.m_146870_();
            return;
         }

         if (age >= 400) {
            if (!this.lingering) {
               this.lingering = true;
               this.setRenderAlpha(0.15F);
               this.setColorTintIntensity(1.0F);
            }

            return;
         }

         float progress = (float)age / 400.0F;
         this.setRenderAlpha(Math.max(0.15F, 0.7F * (1.0F - progress)));
         this.setColorTintIntensity(0.0F + progress);
      } else {
         SoulEntityClientHandler.updateOverlay(this, age);
      }
   }

   public void m_142687_(@NotNull RemovalReason reason) {
      super.m_142687_(reason);
      SoulEntityClientHandler.clearOverlay(this);
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(RENDER_ALPHA, 0.7F);
      this.f_19804_.m_135372_(COLOR_TINT_INTENSITY, 0.0F);
      this.f_19804_.m_135372_(OWNER_UUID, Optional.empty());
      this.f_19804_.m_135372_(OWNER_ID, -1);
   }

   public void m_7380_(CompoundTag tag) {
      super.m_7380_(tag);
      this.getOwnerUUID().ifPresent(uuid -> tag.m_128362_("OwnerUUID", uuid));
   }

   public void m_7378_(CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128403_("OwnerUUID")) {
         this.f_19804_.m_135381_(OWNER_UUID, Optional.of(tag.m_128342_("OwnerUUID")));
      }
   }

   public boolean m_6469_(@Nonnull DamageSource source, float amount) {
      if (this.isOwner(source.m_7639_())) {
         return false;
      } else {
         boolean result = super.m_6469_(source, amount);
         this.m_21153_(this.m_21233_());
         this.f_20916_ = 0;
         if (this.lingering) {
            if (source.m_7639_() instanceof Player player && hasYamatoSkill(player)) {
               DETONATABLE_SOULS.put(this, player.m_20148_());
            }
         } else {
            this.reflectDamageToOwner(source, amount);
         }

         return result;
      }
   }

   private void reflectDamageToOwner(DamageSource source, float amount) {
      Entity owner = this.getOwnerEntity();
      if (!(owner instanceof LivingEntity livingOwner) || !owner.m_6084_()) {
         return;
      }

      if (owner != this) {
         Entity attacker = source.m_7639_();
         DamageSource reflected = null;
         if (attacker != null) {
            reflected = new DamageSource(
               livingOwner.m_9236_().m_9598_().m_175515_(Registries.f_268580_).m_246971_(DamageTypes.f_268724_), attacker, attacker, attacker.f_19825_
            );
         }

         if (reflected != null) {
            livingOwner.m_6469_(reflected, amount);
         }
      }
   }

   public static void detonateForPlayer(Player player) {
      Iterator<Entry<SoulEntity, UUID>> it = DETONATABLE_SOULS.entrySet().iterator();

      while (it.hasNext()) {
         Entry<SoulEntity, UUID> entry = it.next();
         SoulEntity soul = entry.getKey();
         UUID attackerId = entry.getValue();
         if (soul.m_213877_() || !soul.m_6084_()) {
            it.remove();
         } else if (soul.isOwner(player)) {
            it.remove();
         } else if (attackerId.equals(player.m_20148_())) {
            it.remove();
            soul.m_9236_().m_6263_(null, soul.m_20185_(), soul.m_20186_(), soul.m_20189_(), SoundEvents.f_11913_, SoundSource.PLAYERS, 1.0F, 0.5F);
            Entity owner = soul.getOwnerEntity();
            if (owner instanceof LivingEntity) {
               LivingEntity livingOwner = (LivingEntity)owner;
               if (!soul.m_9236_().m_5776_()) {
                  DamageSource detonate = new DamageSource(
                     owner.m_9236_().m_9598_().m_175515_(Registries.f_268580_).m_246971_(DamageTypes.f_268724_), player, player, null
                  );
                  livingOwner.m_6469_(detonate, livingOwner.m_21233_() * 0.05F);
               }
            }

            soul.m_146870_();
         }
      }
   }

   private static boolean hasYamatoSkill(Player player) {
      PlayerPatch patch = (PlayerPatch)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
      if (patch == null) {
         return false;
      } else {
         SkillContainer container = patch.getSkill(SkillSlots.WEAPON_INNATE);
         return container != null && container.getSkill() instanceof VergilSkill;
      }
   }
}
