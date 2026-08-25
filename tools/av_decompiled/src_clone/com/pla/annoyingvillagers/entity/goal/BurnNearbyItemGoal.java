package com.pla.annoyingvillagers.entity.goal;

import com.pla.annoyingvillagers.clazz.AVNpc;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.entity.PlayerNpcEntity;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.pathfinder.Path;

public class BurnNearbyItemGoal extends Goal {
   private final Mob mob;
   private final double speed;
   private final double searchRadius;
   private ItemEntity targetItem;
   private ItemStack burnToolRestoreItem = ItemStack.f_41583_;
   private boolean equippedBurnTool;
   private static final List<String> burnMessageKeys = keys("burn_item.annoyingvillagers", 56);

   private static List<String> keys(String prefix, int count) {
      List<String> list = new ArrayList<>(count);

      for (int i = 1; i <= count; i++) {
         list.add(prefix + "." + i);
      }

      return List.copyOf(list);
   }

   public BurnNearbyItemGoal(Mob mob, double speed, double searchRadius) {
      this.mob = mob;
      this.speed = speed;
      this.searchRadius = searchRadius;
      this.m_7021_(EnumSet.of(Flag.MOVE, Flag.LOOK));
   }

   public boolean m_8036_() {
      if (this.mob.m_9236_().f_46443_) {
         return false;
      } else if (!this.mob.m_6084_() || this.mob.m_213877_() || this.mob.m_21224_()) {
         return false;
      } else if (this.mob.m_20159_()) {
         return false;
      } else if (this.mob.m_5448_() != null) {
         return false;
      } else if (this.mob.m_21525_()) {
         return false;
      } else if (!(Boolean)AnnoyingVillagersConfig.AV_MOB_CAN_BURN_ITEM.get()) {
         return false;
      } else {
         if (this.mob instanceof PlayerNpcEntity playerNpcEntity && playerNpcEntity.isHealing()) {
            return false;
         }

         if (this.mob instanceof AVNpc avNpc && avNpc.isHealing()) {
            return false;
         }

         this.targetItem = this.findTargetItem();
         return this.targetItem != null;
      }
   }

   public boolean m_8045_() {
      if (this.mob.m_9236_().f_46443_) {
         return false;
      } else if (!this.mob.m_6084_() || this.mob.m_213877_() || this.mob.m_21224_()) {
         return false;
      } else if (this.mob.m_20159_()) {
         return false;
      } else if (this.mob.m_5448_() != null) {
         return false;
      } else if (this.mob.m_21525_()) {
         return false;
      } else {
         return !AnnoyingVillagersConfig.AV_MOB_CAN_BURN_ITEM.get()
            ? false
            : this.targetItem != null && this.targetItem.m_6084_() && !this.targetItem.m_32055_().m_41619_();
      }
   }

   public void m_8056_() {
      this.burnToolRestoreItem = ItemStack.f_41583_;
      this.equippedBurnTool = false;
      if (this.targetItem != null) {
         if (this.shouldPickupOrEquipInsteadOfBurn(this.targetItem.m_32055_())) {
            this.restoreMainWeapon(false);
         } else {
            this.equipFlintAndSteel();
         }

         this.mob.m_21573_().m_5624_(this.targetItem, this.speed);
      }
   }

   public void m_8037_() {
      if (this.mob.m_6084_() && !this.mob.m_213877_() && !this.mob.m_21224_()) {
         if (this.mob.m_9236_() instanceof ServerLevel serverLevel) {
            if (this.targetItem != null && this.targetItem.m_6084_() && !this.targetItem.m_32055_().m_41619_()) {
               if (this.shouldPickupOrEquipInsteadOfBurn(this.targetItem.m_32055_())) {
                  this.restoreMainWeapon(false);
               } else {
                  this.equipFlintAndSteel();
               }

               if (this.mob.m_21573_().m_26571_()) {
                  Path path = this.mob.m_21573_().m_6570_(this.targetItem, 0);
                  if (path == null) {
                     return;
                  }

                  this.mob.m_21573_().m_5624_(this.targetItem, this.speed);
               }

               this.mob
                  .m_21563_()
                  .m_24950_(
                     this.targetItem.m_20185_(),
                     this.targetItem.m_20186_() + (double)this.targetItem.m_20206_() / 2.0,
                     this.targetItem.m_20189_(),
                     30.0F,
                     30.0F
                  );
               double dist = (double)this.mob.m_20270_(this.targetItem);
               if (dist <= 1.5) {
                  if (this.shouldPickupOrEquipInsteadOfBurn(this.targetItem.m_32055_()) && this.tryHandleItemWithoutBurning(this.targetItem)) {
                     this.targetItem = null;
                     this.mob.m_21573_().m_26573_();
                     return;
                  }

                  this.equipFlintAndSteel();
                  ItemStack burnedStack = this.targetItem.m_32055_().m_41777_();
                  this.mob.m_6674_(InteractionHand.MAIN_HAND);
                  this.targetItem.m_6074_();
                  serverLevel.m_8767_(
                     ParticleTypes.f_123744_, this.targetItem.m_20185_(), this.targetItem.m_20186_(), this.targetItem.m_20189_(), 8, 0.2, 0.2, 0.2, 0.01
                  );
                  this.mob.m_9236_().m_5594_(null, this.mob.m_20183_(), SoundEvents.f_11942_, SoundSource.HOSTILE, 1.0F, 1.0F);
                  this.tryBroadcastBurnMessage(serverLevel, burnedStack);
               }
            }
         }
      }
   }

   public void m_8041_() {
      boolean shouldRestoreBurnTool = this.equippedBurnTool || this.isFlintAndSteel(this.mob.m_21205_());
      this.targetItem = null;
      this.mob.m_21573_().m_26573_();
      if (shouldRestoreBurnTool) {
         this.restoreMainWeapon(true);
      }

      this.burnToolRestoreItem = ItemStack.f_41583_;
      this.equippedBurnTool = false;
   }

   private void tryBroadcastBurnMessage(ServerLevel serverLevel, ItemStack burnedStack) {
      if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_CHAT.get()) {
         if (this.mob instanceof PlayerNpcEntity) {
            if (!(this.mob.m_217043_().m_188501_() >= 0.05F)) {
               String key = burnMessageKeys.get(this.mob.m_217043_().m_188503_(burnMessageKeys.size()));
               serverLevel.m_7654_()
                  .m_6846_()
                  .m_240416_(
                     Component.m_237119_()
                        .m_7220_(Component.m_237113_("<"))
                        .m_7220_(this.mob.m_5446_())
                        .m_7220_(Component.m_237113_("> "))
                        .m_7220_(Component.m_237110_(key, new Object[]{burnedStack.m_41786_()})),
                     false
                  );
            }
         }
      }
   }

   private void restoreMainWeapon(boolean addIdleCooldown) {
      ItemStack weapon = this.getCachedMainWeapon();
      if (this.mob instanceof PlayerNpcEntity playerNpcEntity && addIdleCooldown) {
         playerNpcEntity.setPlayingIdleCooldown(playerNpcEntity.getPlayingIdleCooldown() + 40);
      }

      if (this.mob instanceof AVNpc avNpc && addIdleCooldown) {
         avNpc.setPlayingIdleCooldown(avNpc.getPlayingIdleCooldown() + 40);
      }

      if ((weapon == null || weapon.m_41619_()) && !this.burnToolRestoreItem.m_41619_()) {
         weapon = this.burnToolRestoreItem;
      }

      if (weapon != null && !weapon.m_41619_()) {
         this.mob.m_8061_(EquipmentSlot.MAINHAND, weapon.m_41777_());
         this.cacheMainWeapon(weapon);
      } else if (this.isFlintAndSteel(this.mob.m_21205_())) {
         this.mob.m_8061_(EquipmentSlot.MAINHAND, ItemStack.f_41583_);
      }
   }

   private ItemEntity findTargetItem() {
      List<ItemEntity> items = this.mob
         .m_9236_()
         .m_6443_(ItemEntity.class, this.mob.m_20191_().m_82400_(this.searchRadius), e -> e.m_6084_() && !e.m_32055_().m_41619_());
      if (items.isEmpty()) {
         return null;
      } else {
         ItemEntity best = null;
         double bestDist = Double.MAX_VALUE;

         for (ItemEntity it : items) {
            double d = this.mob.m_20280_(it);
            if (d < bestDist) {
               bestDist = d;
               best = it;
            }
         }

         return best;
      }
   }

   private void equipFlintAndSteel() {
      this.rememberRestoreItemBeforeBurnTool();
      this.equippedBurnTool = true;
      if (!this.isFlintAndSteel(this.mob.m_21205_())) {
         this.mob.m_8061_(EquipmentSlot.MAINHAND, new ItemStack(Items.f_42409_));
      }
   }

   private boolean shouldPickupOrEquipInsteadOfBurn(ItemStack stack) {
      if (stack.m_41619_()) {
         return false;
      } else if (this.npcInventoryCanAccept(stack)) {
         return true;
      } else {
         return this.mainWeaponIsEmpty() && this.isUsefulWeapon(stack) ? true : this.emptyArmorSlotCanUse(stack);
      }
   }

   private boolean tryHandleItemWithoutBurning(ItemEntity itemEntity) {
      if (itemEntity == null || !itemEntity.m_6084_() || itemEntity.m_32055_().m_41619_()) {
         return false;
      } else if (this.mainWeaponIsEmpty() && this.isUsefulWeapon(itemEntity.m_32055_())) {
         return this.tryEquipWeaponFromGround(itemEntity);
      } else if (this.emptyArmorSlotCanUse(itemEntity.m_32055_())) {
         return this.tryEquipArmorFromGround(itemEntity);
      } else {
         return this.npcInventoryCanAccept(itemEntity.m_32055_()) ? this.tryInsertIntoNpcInventory(itemEntity) : false;
      }
   }

   private boolean tryEquipWeaponFromGround(ItemEntity itemEntity) {
      ItemStack groundStack = itemEntity.m_32055_();
      if (!groundStack.m_41619_() && this.isUsefulWeapon(groundStack)) {
         ItemStack equipStack = groundStack.m_41777_();
         equipStack.m_41764_(1);
         this.mob.m_8061_(EquipmentSlot.MAINHAND, equipStack.m_41777_());
         if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
            playerNpcEntity.setMainWeaponItem(equipStack.m_41777_());
         }

         if (this.mob instanceof AVNpc avNpc) {
            avNpc.setMainWeaponItem(equipStack.m_41777_());
         }

         groundStack.m_41774_(1);
         if (groundStack.m_41619_()) {
            itemEntity.m_146870_();
         } else {
            itemEntity.m_32045_(groundStack);
         }

         this.mob.m_6674_(InteractionHand.MAIN_HAND);
         this.mob.m_9236_().m_5594_(null, this.mob.m_20183_(), SoundEvents.f_12019_, SoundSource.HOSTILE, 0.2F, 1.0F);
         return true;
      } else {
         return false;
      }
   }

   private boolean tryEquipArmorFromGround(ItemEntity itemEntity) {
      ItemStack groundStack = itemEntity.m_32055_();
      if (groundStack.m_41619_()) {
         return false;
      } else {
         EquipmentSlot slot = LivingEntity.m_147233_(groundStack);
         if (slot.m_20743_() != Type.ARMOR) {
            return false;
         } else if (!this.mob.m_6844_(slot).m_41619_()) {
            return false;
         } else {
            ItemStack equipStack = groundStack.m_41777_();
            equipStack.m_41764_(1);
            this.mob.m_8061_(slot, equipStack.m_41777_());
            groundStack.m_41774_(1);
            if (groundStack.m_41619_()) {
               itemEntity.m_146870_();
            } else {
               itemEntity.m_32045_(groundStack);
            }

            this.mob.m_6674_(InteractionHand.MAIN_HAND);
            this.mob.m_9236_().m_5594_(null, this.mob.m_20183_(), SoundEvents.f_12019_, SoundSource.HOSTILE, 0.2F, 1.0F);
            return true;
         }
      }
   }

   private boolean tryInsertIntoNpcInventory(ItemEntity itemEntity) {
      SimpleContainer inventory = this.getNpcInventory();
      if (inventory != null && itemEntity != null && !itemEntity.m_32055_().m_41619_()) {
         ItemStack remaining = itemEntity.m_32055_().m_41777_();
         int originalCount = remaining.m_41613_();

         for (int i = 0; i < inventory.m_6643_() && !remaining.m_41619_(); i++) {
            ItemStack slotStack = inventory.m_8020_(i);
            if (slotStack.m_41619_()) {
               int transferable = Math.min(remaining.m_41613_(), Math.min(remaining.m_41741_(), inventory.m_6893_()));
               ItemStack inserted = remaining.m_41777_();
               inserted.m_41764_(transferable);
               inventory.m_6836_(i, inserted);
               remaining.m_41774_(transferable);
            } else if (ItemStack.m_150942_(slotStack, remaining) && slotStack.m_41613_() < slotStack.m_41741_()) {
               int transferable = Math.min(remaining.m_41613_(), slotStack.m_41741_() - slotStack.m_41613_());
               slotStack.m_41769_(transferable);
               remaining.m_41774_(transferable);
            }
         }

         if (remaining.m_41613_() == originalCount) {
            return false;
         } else {
            inventory.m_6596_();
            if (remaining.m_41619_()) {
               itemEntity.m_146870_();
            } else {
               itemEntity.m_32045_(remaining);
            }

            this.mob.m_6674_(InteractionHand.MAIN_HAND);
            this.mob.m_9236_().m_5594_(null, this.mob.m_20183_(), SoundEvents.f_12019_, SoundSource.HOSTILE, 0.2F, 1.0F);
            return true;
         }
      } else {
         return false;
      }
   }

   private boolean npcInventoryCanAccept(ItemStack incoming) {
      SimpleContainer inventory = this.getNpcInventory();
      if (inventory != null && !incoming.m_41619_()) {
         for (int i = 0; i < inventory.m_6643_(); i++) {
            ItemStack slotStack = inventory.m_8020_(i);
            if (slotStack.m_41619_()) {
               return true;
            }

            if (ItemStack.m_150942_(slotStack, incoming) && slotStack.m_41613_() < slotStack.m_41741_()) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private SimpleContainer getNpcInventory() {
      if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
         return playerNpcEntity.getInventory();
      } else {
         return this.mob instanceof AVNpc avNpc ? avNpc.getInventory() : null;
      }
   }

   private ItemStack getCachedMainWeapon() {
      if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
         return playerNpcEntity.getMainWeaponItem();
      } else {
         return this.mob instanceof AVNpc avNpc ? avNpc.getMainWeaponItem() : ItemStack.f_41583_;
      }
   }

   private void cacheMainWeapon(ItemStack weapon) {
      if (weapon != null && !weapon.m_41619_()) {
         if (this.mob instanceof PlayerNpcEntity playerNpcEntity) {
            playerNpcEntity.setMainWeaponItem(weapon.m_41777_());
         }

         if (this.mob instanceof AVNpc avNpc) {
            avNpc.setMainWeaponItem(weapon.m_41777_());
         }
      }
   }

   private void rememberRestoreItemBeforeBurnTool() {
      if (this.burnToolRestoreItem.m_41619_()) {
         ItemStack cachedWeapon = this.getCachedMainWeapon();
         if (!cachedWeapon.m_41619_()) {
            this.burnToolRestoreItem = cachedWeapon.m_41777_();
         } else {
            ItemStack currentMainHand = this.mob.m_21205_();
            if (!currentMainHand.m_41619_() && !this.isFlintAndSteel(currentMainHand) && this.isUsefulWeapon(currentMainHand)) {
               this.burnToolRestoreItem = currentMainHand.m_41777_();
               this.cacheMainWeapon(currentMainHand);
            }
         }
      }
   }

   private boolean mainWeaponIsEmpty() {
      return !this.getCachedMainWeapon().m_41619_() ? false : this.mob.m_21205_().m_41619_() || this.isFlintAndSteel(this.mob.m_21205_());
   }

   private boolean isFlintAndSteel(ItemStack stack) {
      return !stack.m_41619_() && stack.m_41720_() == Items.f_42409_;
   }

   private boolean isUsefulWeapon(ItemStack stack) {
      return stack.m_41619_()
         ? false
         : stack.m_41720_() instanceof SwordItem
            || stack.m_41720_() instanceof AxeItem
            || stack.m_41720_() instanceof DiggerItem
            || stack.m_41720_() instanceof TridentItem
            || stack.m_41720_() instanceof BowItem
            || stack.m_41720_() instanceof CrossbowItem;
   }

   private boolean emptyArmorSlotCanUse(ItemStack stack) {
      if (stack.m_41619_()) {
         return false;
      } else {
         EquipmentSlot slot = LivingEntity.m_147233_(stack);
         return slot.m_20743_() != Type.ARMOR ? false : this.mob.m_6844_(slot).m_41619_();
      }
   }
}
