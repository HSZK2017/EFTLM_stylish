package com.pla.annoyingvillagers.entity.goal;

import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.entity.PlayerNpcEntity;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;

public class RecoverWeaponInCombatGoal extends Goal {
   private final Mob mob;
   private final double speed;
   private final double searchRadius;
   private ItemEntity targetItem;
   private int inventoryWeaponSlot = -1;
   private boolean restoreCachedWeapon;
   private boolean finished;
   private LivingEntity savedCombatTarget;
   private int lockTicks;
   private int repathCooldown;
   private static final int MAX_LOCK_TICKS = 60;
   private static final double PICKUP_DISTANCE_SQR = 5.76;

   public RecoverWeaponInCombatGoal(Mob mob, double speed, double searchRadius) {
      this.mob = mob;
      this.speed = speed;
      this.searchRadius = searchRadius;
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
   }

   public boolean m_6767_() {
      return false;
   }

   public boolean m_183429_() {
      return true;
   }

   public boolean m_8036_() {
      if (this.mob.m_9236_().f_46443_) {
         return false;
      } else if (!this.mob.m_6084_() || this.mob.m_213877_() || this.mob.m_21224_()) {
         return false;
      } else if (this.mob.m_20159_()) {
         return false;
      } else if (this.mob.m_21525_()) {
         return false;
      } else {
         LivingEntity target = this.mob.m_5448_();
         if (target == null || !target.m_6084_()) {
            return false;
         } else if (!this.mainWeaponIsEmpty()) {
            return false;
         } else {
            this.restoreCachedWeapon = !this.getCachedMainWeapon().m_41619_();
            if (this.restoreCachedWeapon) {
               this.targetItem = null;
               this.inventoryWeaponSlot = -1;
               return true;
            } else {
               this.inventoryWeaponSlot = this.findWeaponSlotInNpcInventory();
               if (this.inventoryWeaponSlot >= 0) {
                  this.targetItem = null;
                  return true;
               } else {
                  this.targetItem = this.findNearestWeaponItem();
                  return this.targetItem != null;
               }
            }
         }
      }
   }

   public boolean m_8045_() {
      if (this.finished) {
         return false;
      } else if (this.mob.m_9236_().f_46443_) {
         return false;
      } else if (!this.mob.m_6084_() || this.mob.m_213877_() || this.mob.m_21224_()) {
         return false;
      } else if (this.mob.m_20159_()) {
         return false;
      } else if (this.mob.m_21525_()) {
         return false;
      } else if (!this.mainWeaponIsEmpty()) {
         return false;
      } else {
         return this.targetItem != null && this.targetItem.m_6084_() && !this.targetItem.m_32055_().m_41619_() ? this.lockTicks < 60 : false;
      }
   }

   public void m_8056_() {
      this.savedCombatTarget = this.mob.m_5448_();
      this.lockTicks = 0;
      this.repathCooldown = 0;
      this.finished = false;
      if (this.restoreCachedWeapon && this.restoreCachedMainWeapon()) {
         this.finished = true;
         this.targetItem = null;
         this.restoreCachedWeapon = false;
      } else if (this.inventoryWeaponSlot >= 0 && this.tryEquipWeaponFromInventory(this.inventoryWeaponSlot)) {
         this.finished = true;
         this.targetItem = null;
      } else {
         this.inventoryWeaponSlot = -1;
         if (this.targetItem == null || !this.targetItem.m_6084_() || this.targetItem.m_32055_().m_41619_()) {
            this.targetItem = this.findNearestWeaponItem();
         }

         if (this.targetItem != null) {
            this.mob.m_6710_(null);
            this.mob.m_21573_().m_26573_();
            this.mob.m_21573_().m_5624_(this.targetItem, this.speed);
         }
      }
   }

   private int findWeaponSlotInNpcInventory() {
      SimpleContainer inventory = this.getNpcInventory();
      if (inventory == null) {
         return -1;
      } else {
         for (int i = 0; i < inventory.m_6643_(); i++) {
            ItemStack stack = inventory.m_8020_(i);
            if (!stack.m_41619_() && this.isUsefulWeapon(stack)) {
               return i;
            }
         }

         return -1;
      }
   }

   private boolean tryEquipWeaponFromInventory(int slot) {
      SimpleContainer inventory = this.getNpcInventory();
      if (inventory == null) {
         return false;
      } else if (slot < 0 || slot >= inventory.m_6643_()) {
         return false;
      } else if (!this.mob.m_21205_().m_41619_()) {
         return false;
      } else {
         ItemStack slotStack = inventory.m_8020_(slot);
         if (!slotStack.m_41619_() && this.isUsefulWeapon(slotStack)) {
            ItemStack equipStack = slotStack.m_41620_(1);
            if (slotStack.m_41619_()) {
               inventory.m_6836_(slot, ItemStack.f_41583_);
            } else {
               inventory.m_6836_(slot, slotStack);
            }

            inventory.m_6596_();
            return this.equipRecoveredWeapon(equipStack);
         } else {
            return false;
         }
      }
   }

   private boolean equipRecoveredWeapon(ItemStack equipStack) {
      if (!equipStack.m_41619_() && this.isUsefulWeapon(equipStack)) {
         equipStack.m_41764_(1);
         this.mob.m_8061_(EquipmentSlot.MAINHAND, equipStack.m_41777_());
         if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
            playerNpcEntity.setMainWeaponItem(equipStack.m_41777_());
            playerNpcEntity.setMainWeaponDisarmed(false);
         }

         if (this.mob instanceof AVNpc avNpc) {
            avNpc.setMainWeaponItem(equipStack.m_41777_());
            avNpc.setMainWeaponDisarmed(false);
         }

         this.mob.m_6674_(InteractionHand.MAIN_HAND);
         this.mob.m_9236_().m_5594_(null, this.mob.m_20183_(), SoundEvents.f_12019_, SoundSource.HOSTILE, 0.35F, 1.0F);
         return true;
      } else {
         return false;
      }
   }

   private boolean restoreCachedMainWeapon() {
      ItemStack weapon = this.getCachedMainWeapon();
      if (weapon.m_41619_()) {
         return false;
      } else {
         this.mob.m_8061_(EquipmentSlot.MAINHAND, weapon.m_41777_());
         if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
            playerNpcEntity.setMainWeaponItem(weapon.m_41777_());
            playerNpcEntity.setMainWeaponDisarmed(false);
         }

         if (this.mob instanceof AVNpc avNpc) {
            avNpc.setMainWeaponItem(weapon.m_41777_());
            avNpc.setMainWeaponDisarmed(false);
         }

         return true;
      }
   }

   private SimpleContainer getNpcInventory() {
      if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
         return playerNpcEntity.getInventory();
      } else {
         return this.mob instanceof AVNpc avNpc ? avNpc.getInventory() : null;
      }
   }

   public void m_8037_() {
      this.lockTicks++;
      if (this.targetItem != null && this.targetItem.m_6084_() && !this.targetItem.m_32055_().m_41619_()) {
         this.mob.m_6710_(null);
         this.mob
            .m_21563_()
            .m_24950_(
               this.targetItem.m_20185_(), this.targetItem.m_20186_() + (double)this.targetItem.m_20206_() * 0.5, this.targetItem.m_20189_(), 60.0F, 60.0F
            );
         if (this.mob.m_20280_(this.targetItem) <= 5.76) {
            if (this.forceEquipWeaponFromItemEntity(this.targetItem)) {
               this.finished = true;
            }

            this.targetItem = null;
         } else {
            if (this.repathCooldown-- <= 0 || this.mob.m_21573_().m_26571_()) {
               this.repathCooldown = 4;
               this.mob.m_21573_().m_26519_(this.targetItem.m_20185_(), this.targetItem.m_20186_(), this.targetItem.m_20189_(), this.speed);
            }
         }
      }
   }

   private boolean forceEquipWeaponFromItemEntity(ItemEntity itemEntity) {
      if (itemEntity == null || !itemEntity.m_6084_()) {
         return false;
      } else if (!this.mob.m_21205_().m_41619_()) {
         return false;
      } else {
         ItemStack groundStack = itemEntity.m_32055_();
         if (!groundStack.m_41619_() && this.isUsefulWeapon(groundStack)) {
            ItemStack equipStack = groundStack.m_41620_(1);
            if (groundStack.m_41619_()) {
               itemEntity.m_146870_();
            } else {
               itemEntity.m_32045_(groundStack);
            }

            return this.equipRecoveredWeapon(equipStack);
         } else {
            return false;
         }
      }
   }

   public void m_8041_() {
      this.mob.m_21573_().m_26573_();
      if (this.savedCombatTarget != null && this.savedCombatTarget.m_6084_()) {
         this.mob.m_6710_(this.savedCombatTarget);
      }

      this.savedCombatTarget = null;
      this.targetItem = null;
      this.inventoryWeaponSlot = -1;
      this.lockTicks = 0;
      this.repathCooldown = 0;
      this.restoreCachedWeapon = false;
      this.finished = false;
   }

   private ItemEntity findNearestWeaponItem() {
      List<ItemEntity> items = this.mob
         .m_9236_()
         .m_6443_(
            ItemEntity.class,
            this.mob.m_20191_().m_82400_(this.searchRadius),
            itemEntityx -> itemEntityx.m_6084_() && !itemEntityx.m_32055_().m_41619_() && this.isUsefulWeapon(itemEntityx.m_32055_())
         );
      if (items.isEmpty()) {
         return null;
      } else {
         ItemEntity best = null;
         double bestDistance = Double.MAX_VALUE;

         for (ItemEntity itemEntity : items) {
            double distance = this.mob.m_20280_(itemEntity);
            if (distance < bestDistance) {
               bestDistance = distance;
               best = itemEntity;
            }
         }

         return best;
      }
   }

   private boolean mainWeaponIsEmpty() {
      return this.mob.m_21205_().m_41619_() || this.isFlintAndSteel(this.mob.m_21205_());
   }

   private ItemStack getCachedMainWeapon() {
      if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
         return playerNpcEntity.getMainWeaponItem();
      } else {
         return this.mob instanceof AVNpc avNpc ? avNpc.getMainWeaponItem() : ItemStack.f_41583_;
      }
   }

   private boolean isFlintAndSteel(ItemStack stack) {
      return !stack.m_41619_() && stack.m_41720_() == Items.f_42409_;
   }

   private boolean isUsefulWeapon(ItemStack stack) {
      if (stack.m_41619_()) {
         return false;
      } else {
         Item item = stack.m_41720_();
         return item instanceof SwordItem || item instanceof DiggerItem || item instanceof TridentItem;
      }
   }
}
