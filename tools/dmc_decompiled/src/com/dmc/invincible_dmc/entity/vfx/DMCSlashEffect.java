package com.dmc.invincible_dmc.entity.vfx;

import com.dmc.invincible_dmc.entity.DMCEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PlayMessages.SpawnEntity;

public class DMCSlashEffect extends Entity {
   private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.m_135353_(DMCSlashEffect.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Float> ROTATION_OFFSET = SynchedEntityData.m_135353_(DMCSlashEffect.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> ROTATION_ROLL = SynchedEntityData.m_135353_(DMCSlashEffect.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Float> BASESIZE = SynchedEntityData.m_135353_(DMCSlashEffect.class, EntityDataSerializers.f_135029_);
   private static final EntityDataAccessor<Integer> MOTION_MODE = SynchedEntityData.m_135353_(DMCSlashEffect.class, EntityDataSerializers.f_135028_);
   private static final EntityDataAccessor<Integer> LIFETIME = SynchedEntityData.m_135353_(DMCSlashEffect.class, EntityDataSerializers.f_135028_);

   public DMCSlashEffect(EntityType<?> entityTypeIn, Level worldIn) {
      super(entityTypeIn, worldIn);
      this.f_19794_ = true;
      this.f_19811_ = true;
   }

   public static DMCSlashEffect createInstance(SpawnEntity packet, Level worldIn) {
      return new DMCSlashEffect((EntityType<?>)DMCEntities.SLASH_EFFECT.get(), worldIn);
   }

   protected void m_8097_() {
      this.f_19804_.m_135372_(COLOR, 3355647);
      this.f_19804_.m_135372_(ROTATION_OFFSET, 0.0F);
      this.f_19804_.m_135372_(ROTATION_ROLL, 0.0F);
      this.f_19804_.m_135372_(BASESIZE, 1.0F);
      this.f_19804_.m_135372_(MOTION_MODE, SlashMotionMode.NORMAL.ordinal());
      this.f_19804_.m_135372_(LIFETIME, 10);
   }

   protected void m_7378_(CompoundTag compound) {
      this.setColor(compound.m_128451_("Color"));
      this.setRotationOffset(compound.m_128457_("RotationOffset"));
      this.setRotationRoll(compound.m_128457_("RotationRoll"));
      this.setBaseSize(compound.m_128457_("BaseSize"));
      this.setMotionMode(SlashMotionMode.fromOrdinal(compound.m_128451_("MotionMode")));
      this.setLifetime(compound.m_128451_("Lifetime"));
   }

   protected void m_7380_(CompoundTag compound) {
      compound.m_128405_("Color", this.getColor());
      compound.m_128350_("RotationOffset", this.getRotationOffset());
      compound.m_128350_("RotationRoll", this.getRotationRoll());
      compound.m_128350_("BaseSize", this.getBaseSize());
      compound.m_128405_("MotionMode", this.getMotionMode().ordinal());
      compound.m_128405_("Lifetime", this.getLifetime());
   }

   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean m_6783_(double distance) {
      double d0 = 64.0 * m_20150_();
      return distance < d0 * d0;
   }

   @OnlyIn(Dist.CLIENT)
   public void m_6453_(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
      this.m_6034_(x, y, z);
      this.m_19915_(yaw, pitch);
   }

   @OnlyIn(Dist.CLIENT)
   public void m_6001_(double x, double y, double z) {
      this.m_20334_(0.0, 0.0, 0.0);
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.getLifetime() < this.f_19797_) {
         this.m_146870_();
      }
   }

   public int getColor() {
      return (Integer)this.f_19804_.m_135370_(COLOR);
   }

   public void setColor(int value) {
      this.f_19804_.m_135381_(COLOR, value);
   }

   public int getLifetime() {
      return (Integer)this.f_19804_.m_135370_(LIFETIME);
   }

   public void setLifetime(int value) {
      this.f_19804_.m_135381_(LIFETIME, Math.max(0, Math.min(value, 1000)));
   }

   public float getRotationOffset() {
      return (Float)this.f_19804_.m_135370_(ROTATION_OFFSET);
   }

   public void setRotationOffset(float value) {
      this.f_19804_.m_135381_(ROTATION_OFFSET, value);
   }

   public float getRotationRoll() {
      return (Float)this.f_19804_.m_135370_(ROTATION_ROLL);
   }

   public void setRotationRoll(float value) {
      this.f_19804_.m_135381_(ROTATION_ROLL, value);
   }

   public float getBaseSize() {
      return (Float)this.f_19804_.m_135370_(BASESIZE);
   }

   public void setBaseSize(float value) {
      this.f_19804_.m_135381_(BASESIZE, value);
   }

   public SlashMotionMode getMotionMode() {
      return SlashMotionMode.fromOrdinal((Integer)this.f_19804_.m_135370_(MOTION_MODE));
   }

   public void setMotionMode(SlashMotionMode mode) {
      this.f_19804_.m_135381_(MOTION_MODE, mode.ordinal());
   }

   public boolean m_6087_() {
      return false;
   }
}
