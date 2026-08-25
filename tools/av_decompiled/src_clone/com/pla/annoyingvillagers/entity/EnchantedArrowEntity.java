package com.pla.annoyingvillagers.entity;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.util.GlintColorHelper;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class EnchantedArrowEntity extends Arrow {
   private static final EntityDataAccessor<Integer> COLOR_GLINT = SynchedEntityData.m_135353_(EnchantedArrowEntity.class, EntityDataSerializers.f_135028_);

   public EnchantedArrowEntity(EntityType<? extends EnchantedArrowEntity> type, Level level) {
      super(type, level);
   }

   public EnchantedArrowEntity(@NotNull Level level, @NotNull LivingEntity shooter) {
      this((EntityType<? extends EnchantedArrowEntity>)AnnoyingVillagersModEntities.ENCHANTED_ARROW.get(), level);
      this.m_5602_(shooter);
      this.m_6034_(shooter.m_20185_(), shooter.m_20188_() - 0.1, shooter.m_20189_());
      if (shooter instanceof Player player) {
         this.f_36705_ = player.m_150110_().f_35937_ ? Pickup.CREATIVE_ONLY : Pickup.ALLOWED;
      } else {
         this.f_36705_ = Pickup.ALLOWED;
      }
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(COLOR_GLINT, 0);
   }

   public void setColorGlint(int mode) {
      this.f_19804_.m_135381_(COLOR_GLINT, GlintColorHelper.sanitize(mode));
   }

   public int getColorGlint() {
      return (Integer)this.f_19804_.m_135370_(COLOR_GLINT);
   }

   public void m_7380_(CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128405_("ColorGlint", this.getColorGlint());
   }

   public void m_8119_() {
      super.m_8119_();
      if (this.m_9236_().f_46443_) {
         int amount = this.f_36703_ ? (this.f_36704_ % 5 == 0 ? 1 : 0) : 2;
         if (amount > 0) {
            this.spawnColoredParticles(amount);
         }
      }
   }

   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      if (tag.m_128425_("ColorGlint", 3)) {
         this.setColorGlint(tag.m_128451_("ColorGlint"));
      } else if (tag.m_128425_("ColorGlint", 8)) {
         this.setColorGlint(GlintColorHelper.fromName(tag.m_128461_("ColorGlint")));
      }
   }

   private void spawnColoredParticles(int amount) {
      Vec3 rgb = GlintColorHelper.getParticleColor(this.getColorGlint());

      for (int i = 0; i < amount; i++) {
         this.m_9236_().m_7106_(ParticleTypes.f_123811_, this.m_20208_(0.5), this.m_20187_(), this.m_20262_(0.5), rgb.f_82479_, rgb.f_82480_, rgb.f_82481_);
      }
   }

   @NotNull
   public ItemStack m_7941_() {
      ItemStack stack = new ItemStack((ItemLike)AnnoyingVillagersModItems.ENCHANTED_ARROW.get());
      GlintColorHelper.setColor(stack, this.getColorGlint());
      return stack;
   }

   protected boolean m_142470_(@NotNull Player pPlayer) {
      ItemStack stack = this.m_7941_();
      GlintColorHelper.clearColor(stack);

      return switch (this.f_36705_) {
         case ALLOWED -> pPlayer.m_150109_().m_36054_(stack);
         case CREATIVE_ONLY -> pPlayer.m_150110_().f_35937_;
         default -> false;
      };
   }
}
