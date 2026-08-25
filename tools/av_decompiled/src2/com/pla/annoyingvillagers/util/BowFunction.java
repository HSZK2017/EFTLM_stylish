package com.pla.annoyingvillagers.util;

import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.entity.BlueVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.GreenVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.PurpleVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.RedVillagerKnightEntity;
import com.pla.annoyingvillagers.entity.VillagerScoutCaptainEntity;
import com.pla.annoyingvillagers.entity.VillagerScoutEntity;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class BowFunction {
   public static void bowShoot(LivingEntityPatch<?> livingEntityPatch) {
      LivingEntity shooter = (LivingEntity)livingEntityPatch.getOriginal();
      Level level = shooter.m_9236_();
      if (!level.f_46443_) {
         ItemStack bowStack = shooter.m_21120_(InteractionHand.MAIN_HAND);
         if (bowStack.m_41720_() instanceof BowItem bowItem) {
            if (!(shooter instanceof Player) || hasArrowOrInfinity(shooter, bowStack)) {
               LivingEntity target = !(shooter instanceof Player) ? livingEntityPatch.getTarget() : null;
               if (target == null || !target.m_6084_() || hasClearShot(shooter, target)) {
                  if (!bowStack.m_41619_() && bowStack.m_41783_() != null) {
                     bowStack.m_41783_().m_128350_("Pulling", 0.65F);
                  }

                  ItemStack arrowStack;
                  boolean creativeOrInfinity;
                  if (shooter instanceof Player player) {
                     arrowStack = player.m_6298_(bowStack);
                     creativeOrInfinity = player.m_150110_().f_35937_ || EnchantmentHelper.m_44843_(Enchantments.f_44952_, bowStack) > 0;
                  } else {
                     if ((
                           shooter instanceof VillagerScoutEntity
                              || shooter instanceof RedVillagerKnightEntity
                              || shooter instanceof BlueVillagerKnightEntity
                              || shooter instanceof GreenVillagerKnightEntity
                              || shooter instanceof PurpleVillagerKnightEntity
                              || shooter instanceof VillagerScoutCaptainEntity
                        )
                        && ((AVNpc)shooter).m_5448_() instanceof HerobrineMob) {
                        arrowStack = new ItemStack((ItemLike)AnnoyingVillagersModItems.ENCHANTED_ARROW.get());
                     } else {
                        arrowStack = new ItemStack(Items.f_42412_);
                     }

                     creativeOrInfinity = true;
                  }

                  if (!arrowStack.m_41619_() || creativeOrInfinity) {
                     if (arrowStack.m_41619_()) {
                        arrowStack = new ItemStack(Items.f_42412_);
                     }

                     int charge = 20;
                     float power = BowItem.m_40661_(charge);
                     if (!(power < 0.1F)) {
                        ArrowItem arrowItem = arrowStack.m_41720_() instanceof ArrowItem ai ? ai : (ArrowItem)Items.f_42412_;
                        AbstractArrow abstractArrow = arrowItem.m_6394_(level, arrowStack, shooter);
                        abstractArrow = bowItem.customArrow(abstractArrow);
                        float arrowInaccuracy = 1.0F;
                        float yRot;
                        float xRot;
                        if (!(shooter instanceof Player)) {
                           if (target != null && target.m_6084_()) {
                              double distance = (double)shooter.m_20270_(target);
                              double horizontalSpread = 0.15 + distance * 0.03;
                              double verticalSpread = 0.05 + distance * 0.02;
                              double aimX = target.m_20185_() + (level.m_213780_().m_188500_() - 0.5) * 2.0 * horizontalSpread;
                              double aimY = target.m_20188_() + (level.m_213780_().m_188500_() - 0.5) * 2.0 * verticalSpread;
                              double aimZ = target.m_20189_() + (level.m_213780_().m_188500_() - 0.5) * 2.0 * horizontalSpread;
                              double dx = aimX - shooter.m_20185_();
                              double dz = aimZ - shooter.m_20189_();
                              double dy = aimY - shooter.m_20188_();
                              double horiz = Math.sqrt(dx * dx + dz * dz);
                              yRot = (float)(Mth.m_14136_(dz, dx) * (180.0 / Math.PI)) - 90.0F;
                              float xRotx = (float)(-(Mth.m_14136_(dy, horiz) * (180.0 / Math.PI)));
                              xRot = Mth.m_14036_(xRotx, -89.9F, 89.9F);
                              shooter.m_146922_(yRot);
                              shooter.m_146926_(xRot);
                              shooter.m_5618_(yRot);
                              shooter.m_5616_(yRot);
                              arrowInaccuracy = 2.0F;
                           } else {
                              xRot = shooter.m_146909_();
                              yRot = shooter.m_146908_();
                              arrowInaccuracy = 2.0F;
                           }
                        } else {
                           xRot = shooter.m_146909_();
                           yRot = shooter.m_146908_();
                        }

                        abstractArrow.m_5602_(shooter);
                        abstractArrow.m_37251_(shooter, xRot, yRot, 0.0F, power * 3.0F, arrowInaccuracy);
                        if (!bowStack.m_41619_() && bowStack.m_41783_() != null) {
                           bowStack.m_41783_().m_128473_("Pulling");
                        }

                        if (power == 1.0F) {
                           abstractArrow.m_36762_(true);
                        }

                        int powerLevel = EnchantmentHelper.m_44843_(Enchantments.f_44988_, bowStack);
                        if (powerLevel > 0) {
                           abstractArrow.m_36781_(abstractArrow.m_36789_() + (double)powerLevel * 0.5 + 0.5);
                        }

                        int punchLevel = EnchantmentHelper.m_44843_(Enchantments.f_44989_, bowStack);
                        if (punchLevel > 0) {
                           abstractArrow.m_36735_(punchLevel);
                        }

                        if (EnchantmentHelper.m_44843_(Enchantments.f_44990_, bowStack) > 0) {
                           abstractArrow.m_20254_(100);
                        }

                        level.m_7967_(abstractArrow);
                        if ((shooter instanceof VillagerScoutEntity || shooter instanceof VillagerScoutCaptainEntity)
                           && ((AVNpc)shooter).getVoiceCooldown() == 0) {
                           shooter.m_5496_((SoundEvent)AnnoyingVillagersModSounds.VILLAGER_SCOUTS_SAY_ON_FIRE.get(), 0.5F, 1.0F);
                           ((AVNpc)shooter).resetVoiceCooldown((Mob)shooter);
                        }

                        if ((
                              shooter instanceof RedVillagerKnightEntity
                                 || shooter instanceof BlueVillagerKnightEntity
                                 || shooter instanceof GreenVillagerKnightEntity
                                 || shooter instanceof PurpleVillagerKnightEntity
                           )
                           && ((AVNpc)shooter).getVoiceCooldown() == 0) {
                           shooter.m_5496_((SoundEvent)AnnoyingVillagersModSounds.VILLAGER_KNIGHTS_SAY_ON_FIRE.get(), 0.5F, 1.0F);
                           ((AVNpc)shooter).resetVoiceCooldown((Mob)shooter);
                        }

                        level.m_6263_(
                           null,
                           shooter.m_20185_(),
                           shooter.m_20186_(),
                           shooter.m_20189_(),
                           SoundEvents.f_11687_,
                           SoundSource.PLAYERS,
                           1.0F,
                           1.0F / (level.m_213780_().m_188501_() * 0.4F + 1.2F) + power * 0.5F
                        );
                        if (shooter instanceof Player player && !player.m_150110_().f_35937_) {
                           boolean infiniteArrow = creativeOrInfinity
                              || arrowItem.isInfinite(arrowStack, bowStack, player) && arrowStack.m_150930_(Items.f_42412_);
                           if (!infiniteArrow) {
                              arrowStack.m_41774_(1);
                              if (arrowStack.m_41619_()) {
                                 player.m_150109_().m_36057_(arrowStack);
                              }
                           }

                           bowStack.m_41622_(1, player, p -> p.m_21190_(InteractionHand.MAIN_HAND));
                           player.m_36246_(Stats.f_12982_.m_12902_(bowItem));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean hasArrowOrInfinity(LivingEntity entity, ItemStack bowStack) {
      if (entity instanceof Player player) {
         if (player.m_150110_().f_35937_) {
            return true;
         } else if (!(bowStack.m_41720_() instanceof BowItem)) {
            return false;
         } else {
            int infinityLevel = EnchantmentHelper.getTagEnchantmentLevel(Enchantments.f_44952_, bowStack);
            boolean hasInfinity = infinityLevel > 0;
            ItemStack projectile = player.m_6298_(bowStack);
            boolean hasArrow = !projectile.m_41619_();
            return hasArrow || hasInfinity;
         }
      } else {
         return true;
      }
   }

   public static boolean hasClearShot(LivingEntity shooter, LivingEntity target) {
      return target != null && target.m_6084_() && shooter.m_9236_() == target.m_9236_()
         ? hasClearShotFrom(shooter.m_9236_(), shooter, shooter.m_146892_(), target)
         : false;
   }

   public static boolean hasClearShotFrom(Level level, Entity clipOwner, Vec3 from, LivingEntity target) {
      if (target != null && target.m_6084_() && level == target.m_9236_()) {
         Vec3 eye = target.m_146892_();
         Vec3 body = new Vec3(target.m_20185_(), target.m_20227_(0.5), target.m_20189_());
         return hasClearPath(level, clipOwner, from, eye) || hasClearPath(level, clipOwner, from, body);
      } else {
         return false;
      }
   }

   private static boolean hasClearPath(Level level, Entity clipOwner, Vec3 from, Vec3 to) {
      return level.m_45547_(new ClipContext(from, to, Block.COLLIDER, Fluid.NONE, clipOwner)).m_6662_() == Type.MISS;
   }
}
