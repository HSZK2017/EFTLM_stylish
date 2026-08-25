package org.merlin204.mimic.copy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.merlin204.mimic.entity.MimicEntity;
import org.merlin204.mimic.entity.MimicPatch;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.asset.AssetAccessor;

public class CopyAnimationInfo {
   public final AssetAccessor<? extends AttackAnimation> animation;
   public final MimicPatch<?> ownerPatch;
   public final ItemStack itemMain;
   public final ItemStack itemOff;
   public int uesCount = 0;
   private List<CopyAnimationInfo.Rectangle> rectangles = new ArrayList<>();

   public CopyAnimationInfo(AssetAccessor<? extends AttackAnimation> animation, MimicPatch<?> ownerPatch, ItemStack itemMain, ItemStack itemOff) {
      this.animation = animation;
      this.ownerPatch = ownerPatch;
      this.itemMain = itemMain == null ? ItemStack.f_41583_ : itemMain.m_41777_();
      this.itemOff = itemOff == null ? ItemStack.f_41583_ : itemOff.m_41777_();
   }

   @Nullable
   public CompoundTag savaInTag() {
      AttackAnimation attackAnimation = this.animation == null ? null : (AttackAnimation)this.animation.get();
      if (attackAnimation != null && attackAnimation.getRegistryName() != null) {
         CompoundTag tag = new CompoundTag();
         tag.m_128359_("animation", attackAnimation.getRegistryName().toString());
         List<Integer> xs = new ArrayList<>();
         List<Integer> zs = new ArrayList<>();
         if (this.rectangles != null) {
            for (CopyAnimationInfo.Rectangle rectangle : this.rectangles) {
               if (rectangle != null) {
                  xs.add(rectangle.x);
                  zs.add(rectangle.z);
               }
            }
         }

         tag.m_128405_("ues_count", this.uesCount);
         tag.m_128408_("x", xs);
         tag.m_128408_("z", zs);
         tag.m_128365_("main", this.itemMain.m_41739_(new CompoundTag()));
         tag.m_128365_("off", this.itemOff.m_41739_(new CompoundTag()));
         return tag;
      } else {
         return null;
      }
   }

   @Nullable
   public static CopyAnimationInfo loadFormTag(CompoundTag tag, MimicPatch<?> ownerPatch) {
      if (tag != null && ownerPatch != null && tag.m_128441_("animation")) {
         AssetAccessor<? extends AttackAnimation> animation = AnimationManager.byKey(tag.m_128461_("animation"));
         if (animation != null && animation.get() != null) {
            ItemStack itemMain = ItemStack.m_41712_(tag.m_128469_("main"));
            ItemStack itemOff = ItemStack.m_41712_(tag.m_128469_("off"));
            int number = 0;
            CopyAnimationInfo copyAnimationInfo = new CopyAnimationInfo(animation, ownerPatch, itemMain, itemOff);
            List<Integer> xs = Arrays.stream(tag.m_128465_("x")).boxed().toList();
            List<Integer> zs = Arrays.stream(tag.m_128465_("z")).boxed().toList();

            for (int x : xs) {
               if (number >= zs.size()) {
                  break;
               }

               copyAnimationInfo.rectangles.add(new CopyAnimationInfo.Rectangle(x, zs.get(number)));
               number++;
            }

            copyAnimationInfo.uesCount = tag.m_128451_("ues_count");
            return copyAnimationInfo;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public void addRectangle(Vec3 startPos, float startYRot, Vec3 targetPos) {
      if (startPos != null && targetPos != null) {
         if (this.rectangles == null) {
            this.rectangles = new ArrayList<>();
         }

         double theta = Math.toRadians((double)startYRot);
         double cos = Math.cos(theta);
         double sin = Math.sin(theta);
         double dx = targetPos.f_82479_ - startPos.f_82479_;
         double dz = targetPos.f_82481_ - startPos.f_82481_;
         double localX = dx * cos + dz * sin;
         double localZ = -dx * sin + dz * cos;
         int gridX = (int)Math.floor(localX + 0.5);
         int gridZ = (int)Math.floor(localZ + 0.5);

         for (CopyAnimationInfo.Rectangle rect : this.rectangles) {
            if (rect.x == gridX && rect.z == gridZ) {
               return;
            }
         }

         this.rectangles.add(new CopyAnimationInfo.Rectangle(gridX, gridZ));
      }
   }

   public void tick() {
   }

   public boolean checkCanHit() {
      if (this.ownerPatch != null && this.ownerPatch.getOriginal() != null && this.rectangles != null && !this.rectangles.isEmpty()) {
         Vec3 Pos = ((MimicEntity)this.ownerPatch.getOriginal()).m_20182_();
         float yRot = ((MimicEntity)this.ownerPatch.getOriginal()).m_146908_();
         double theta = Math.toRadians((double)yRot);
         double cos = Math.cos(theta);
         double sin = Math.sin(theta);
         List<CopyAnimationInfo.Rectangle> rects = new ArrayList<>(this.rectangles);
         LivingEntity entity = this.ownerPatch.getTarget();
         if (entity == null) {
            return false;
         } else if (Math.abs(entity.m_20182_().f_82480_ - Pos.f_82480_) > 2.0) {
            return false;
         } else {
            Vec3 entityPos = entity.m_20182_();
            double dx = entityPos.f_82479_ - Pos.f_82479_;
            double dz = entityPos.f_82481_ - Pos.f_82481_;
            double localX = dx * cos + dz * sin;
            double localZ = -dx * sin + dz * cos;
            int gridX = (int)Math.floor(localX + 0.5);
            int gridZ = (int)Math.floor(localZ + 0.5);

            for (CopyAnimationInfo.Rectangle rect : rects) {
               if (gridX >= rect.x && gridX <= rect.x && gridZ >= rect.z && gridZ <= rect.z) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   public void playAnimation() {
      if (this.ownerPatch != null && this.ownerPatch.getOriginal() != null && this.animation != null && this.animation.get() != null) {
         this.ownerPatch.copyAnimationInfoNow = this;
         ((MimicEntity)this.ownerPatch.getOriginal()).m_21008_(InteractionHand.MAIN_HAND, this.itemMain.m_41777_());
         ((MimicEntity)this.ownerPatch.getOriginal()).m_21008_(InteractionHand.OFF_HAND, this.itemOff.m_41777_());
         this.ownerPatch.playAnimationSynchronized(this.animation, 0.0F);
         this.uesCount++;
      }
   }

   private void drawAllRectangles() {
      if (this.ownerPatch != null && this.ownerPatch.getOriginal() != null && this.rectangles != null) {
         Vec3 Pos = ((MimicEntity)this.ownerPatch.getOriginal()).m_20182_();
         float yRot = ((MimicEntity)this.ownerPatch.getOriginal()).m_146908_();
         LivingEntity livingEntity = (LivingEntity)this.ownerPatch.getOriginal();

         for (CopyAnimationInfo.Rectangle rect : this.rectangles) {
            double minX = (double)rect.x - 0.5;
            double maxX = (double)rect.x + 0.5;
            double minZ = (double)rect.z - 0.5;
            double maxZ = (double)rect.z + 0.5;
            this.drawLine(Pos, yRot, minX, minZ, maxX, minZ, livingEntity);
            this.drawLine(Pos, yRot, maxX, minZ, maxX, maxZ, livingEntity);
            this.drawLine(Pos, yRot, maxX, maxZ, minX, maxZ, livingEntity);
            this.drawLine(Pos, yRot, minX, maxZ, minX, minZ, livingEntity);
         }
      }
   }

   private void drawLine(Vec3 Pos, float yRot, double x1, double z1, double x2, double z2, LivingEntity entity) {
      double step = 0.1;
      double dx = x2 - x1;
      double dz = z2 - z1;
      double length = Math.sqrt(dx * dx + dz * dz);
      int steps = (int)(length / 0.1);

      for (int i = 0; i <= steps; i++) {
         double progress = (double)i / (double)steps;
         double localX = x1 + dx * progress;
         double localZ = z1 + dz * progress;
         Vec3 worldPos = this.localToWorld(new Vec3(localX, 0.0, localZ), Pos, yRot);
         if (entity.m_9236_() instanceof ServerLevel serverLevel) {
            serverLevel.m_8767_(ParticleTypes.f_123762_, worldPos.f_82479_, worldPos.f_82480_ + 0.5, worldPos.f_82481_, 5, 0.0, 0.0, 0.0, 0.0);
         }
      }
   }

   private Vec3 localToWorld(Vec3 local, Vec3 origin, float yRot) {
      double radian = Math.toRadians((double)yRot);
      double cos = Math.cos(radian);
      double sin = Math.sin(radian);
      return new Vec3(
         origin.f_82479_ + local.f_82479_ * cos - local.f_82481_ * sin, origin.f_82480_, origin.f_82481_ + local.f_82479_ * sin + local.f_82481_ * cos
      );
   }

   public static class Rectangle {
      public final int x;
      public final int z;

      public Rectangle(int x, int z) {
         this.x = x;
         this.z = z;
      }
   }
}
