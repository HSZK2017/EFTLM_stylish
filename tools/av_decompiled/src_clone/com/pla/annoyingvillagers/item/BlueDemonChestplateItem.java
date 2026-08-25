package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.entity.BlueDemonThrownTridentEntity;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.util.ArmorUtil;
import com.pla.annoyingvillagers.util.BlueDemonUtil;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public abstract class BlueDemonChestplateItem extends ArmorItem {
   private static final String TAG_CHEST_CHARGE = "BlueDemonChestCharge";
   public static final int MAX_CHEST_CHARGE = 100;
   private static final int CHEST_CHARGE_METER_STEPS = 10;
   private static final int CHEST_CHARGE_COLOR = 5634047;
   private static final int CHEST_CHARGE_DIM_COLOR = 2306872;
   private static final int CHEST_CHARGE_TEXT_COLOR = 12450815;
   private static final int CHEST_CHARGE_FULL_COLOR = 8191999;
   private static final String TAG_CHEST_BUFF_TICKS = "BlueDemonChestBuffTicks";
   public static final int CHEST_BUFF_DURATION_TICKS = 600;
   public static final double CHEST_TRIDENT_ABSORB_BOX_HALF = 2.5;
   private static final String TAG_BLUE_DEMON_HEALING_FOIL = "BlueDemonHealingFoil";

   public BlueDemonChestplateItem(Type type, Properties properties) {
      super(new ArmorMaterial() {
         public int m_266425_(@NotNull Type pType) {
            return switch (pType) {
               case BOOTS -> 403;
               case LEGGINGS -> 465;
               case CHESTPLATE -> 775;
               case HELMET -> 341;
               default -> throw new IncompatibleClassChangeError();
            };
         }

         public int m_7366_(@NotNull Type pType) {
            return switch (pType) {
               case BOOTS -> 2;
               case LEGGINGS -> 5;
               case CHESTPLATE -> 30;
               case HELMET -> 2;
               default -> throw new IncompatibleClassChangeError();
            };
         }

         public int m_6646_() {
            return 9;
         }

         @NotNull
         public SoundEvent m_7344_() {
            return SoundEvents.f_11675_;
         }

         @NotNull
         public Ingredient m_6230_() {
            return Ingredient.m_43927_(new ItemStack[]{new ItemStack(Items.f_42695_), new ItemStack(Items.f_42696_)});
         }

         @NotNull
         public String m_6082_() {
            return "blue_demon_chestplate";
         }

         public float m_6651_() {
            return 2.0F;
         }

         public float m_6649_() {
            return 0.0F;
         }
      }, type, properties);
   }

   public static boolean isBlueDemonChestplate(ItemStack stack) {
      return !stack.m_41619_() && stack.m_41720_() instanceof BlueDemonChestplateItem;
   }

   public static int getStoredCharge(ItemStack stack) {
      if (!isBlueDemonChestplate(stack)) {
         return 0;
      } else {
         CompoundTag tag = stack.m_41783_();
         return tag == null ? 0 : Mth.m_14045_(tag.m_128451_("BlueDemonChestCharge"), 0, 100);
      }
   }

   public static void setStoredCharge(ItemStack stack, int amount) {
      if (isBlueDemonChestplate(stack)) {
         stack.m_41784_().m_128405_("BlueDemonChestCharge", Mth.m_14045_(amount, 0, 100));
      }
   }

   public static void addStoredCharge(ItemStack stack, int amount) {
      if (isBlueDemonChestplate(stack) && amount > 0) {
         int current = getStoredCharge(stack);
         int added = Math.min(amount, 100 - current);
         if (added > 0) {
            setStoredCharge(stack, current + added);
         }
      }
   }

   public static boolean isFullyCharged(ItemStack stack) {
      return getStoredCharge(stack) >= 100;
   }

   public static boolean hasBlueDemonHealingFoil(ItemStack stack) {
      if (!isBlueDemonChestplate(stack)) {
         return false;
      } else {
         CompoundTag tag = stack.m_41783_();
         return tag != null && tag.m_128471_("BlueDemonHealingFoil");
      }
   }

   public static void setBlueDemonHealingFoil(ItemStack stack, boolean foil) {
      if (isBlueDemonChestplate(stack)) {
         if (foil) {
            stack.m_41784_().m_128379_("BlueDemonHealingFoil", true);
         } else {
            CompoundTag tag = stack.m_41783_();
            if (tag != null) {
               tag.m_128473_("BlueDemonHealingFoil");
               if (tag.m_128456_()) {
                  stack.m_41751_(null);
               }
            }
         }
      }
   }

   public boolean m_5812_(@NotNull ItemStack stack) {
      return super.m_5812_(stack) || isFullyCharged(stack) || isBuffActive(stack) || hasBlueDemonHealingFoil(stack);
   }

   public static int getBuffTicks(ItemStack stack) {
      if (!isBlueDemonChestplate(stack)) {
         return 0;
      } else {
         CompoundTag tag = stack.m_41783_();
         return tag == null ? 0 : Math.max(0, tag.m_128451_("BlueDemonChestBuffTicks"));
      }
   }

   public static void setBuffTicks(ItemStack stack, int ticks) {
      if (isBlueDemonChestplate(stack)) {
         int clamped = Math.max(0, ticks);
         CompoundTag tag = stack.m_41783_();
         if (clamped == 0) {
            if (tag != null) {
               tag.m_128473_("BlueDemonChestBuffTicks");
               if (tag.m_128456_()) {
                  stack.m_41751_(null);
               }
            }
         } else {
            stack.m_41784_().m_128405_("BlueDemonChestBuffTicks", clamped);
         }
      }
   }

   public static void stopBuff(ItemStack stack) {
      if (isBlueDemonChestplate(stack)) {
         setBuffTicks(stack, 0);
      }
   }

   public static void activateBuff(ItemStack stack) {
      if (isBlueDemonChestplate(stack)) {
         if (isFullyCharged(stack)) {
            stopBuff(stack);
            setBuffTicks(stack, 600);
            setStoredCharge(stack, 0);
         }
      }
   }

   public static boolean isBuffActive(ItemStack stack) {
      return getBuffTicks(stack) > 0;
   }

   public static void tickActiveBuff(ItemStack stack, Player player) {
      if (isBlueDemonChestplate(stack)) {
         int ticks = getBuffTicks(stack);
         if (ticks > 0) {
            if (player.m_9236_() instanceof ServerLevel serverLevel) {
               player.m_7292_(new MobEffectInstance(MobEffects.f_19596_, 1, 1, false, false, false));
               player.m_7292_(new MobEffectInstance(MobEffects.f_19603_, 1, 1, false, false, false));
               player.m_7292_(new MobEffectInstance(MobEffects.f_19606_, 1, 2, false, false, false));
               if (serverLevel.f_46441_.m_188500_() <= 0.1) {
                  BlueDemonUtil.spawnBlueDemonChestplateEffect(serverLevel, player);
                  if (serverLevel.f_46441_.m_188500_() <= 0.8) {
                     float volume = (float)Mth.m_216263_(serverLevel.f_46441_, 0.05, 0.5);
                     float pitch = (float)Mth.m_216263_(serverLevel.f_46441_, 0.8, 1.1);
                     serverLevel.m_5594_(
                        null,
                        BlockPos.m_274561_(player.m_20185_(), player.m_20186_(), player.m_20189_()),
                        (SoundEvent)AnnoyingVillagersModSounds.ELECTRIFY.get(),
                        SoundSource.NEUTRAL,
                        volume,
                        pitch
                     );
                  }
               }

               if (player.f_19797_ % 2 == 0) {
                  absorbNearbyGroundedOwnerTridents(serverLevel, player);
               }
            }

            setBuffTicks(stack, ticks - 1);
         }
      }
   }

   private static void absorbNearbyGroundedOwnerTridents(ServerLevel serverLevel, Player player) {
      AABB box = new AABB(
         player.m_20185_() - 2.5, player.m_20186_() - 2.5, player.m_20189_() - 2.5, player.m_20185_() + 2.5, player.m_20186_() + 2.5, player.m_20189_() + 2.5
      );

      for (BlueDemonThrownTridentEntity trident : serverLevel.m_6443_(
         BlueDemonThrownTridentEntity.class,
         box,
         tridentx -> tridentx.m_6084_() && tridentx.isGroundedTrident() && tridentx.belongsToOwner(player) && !tridentx.isAbsorbingToWearer()
      )) {
         trident.beginAbsorbToWearer(player);
      }
   }

   public static class Chestplate extends BlueDemonChestplateItem {
      public Chestplate() {
         super(Type.CHESTPLATE, new Properties().m_41486_());
      }

      public String getArmorTexture(ItemStack itemstack, Entity entity, EquipmentSlot equipmentslot, String s) {
         return "annoyingvillagers:textures/models/armor/blue_demon_chestplate_layer.png";
      }

      public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
         super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
         if (player.m_6844_(EquipmentSlot.CHEST) != stack) {
            if (isBuffActive(stack)) {
               stopBuff(stack);
            }
         } else {
            ArmorUtil.dropArmorSlot(player, EquipmentSlot.FEET, "Blue Demon Chestplate");
            ArmorUtil.dropArmorSlot(player, EquipmentSlot.LEGS, "Blue Demon Chestplate");
            ArmorUtil.dropArmorSlot(player, EquipmentSlot.HEAD, "Blue Demon Chestplate");
            tickActiveBuff(stack, player);
         }
      }

      public void m_7373_(@NotNull ItemStack stack, Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
         super.m_7373_(stack, level, tooltip, flag);
         int charge = getStoredCharge(stack);
         tooltip.add(Component.m_237115_("tooltip.annoyingvillagers.blue_demon_chestplate"));
         addChestChargeTooltip(tooltip, charge);
      }

      private static void addChestChargeTooltip(List<Component> tooltip, int charge) {
         tooltip.add(
            Component.m_237113_(Component.m_237115_("tooltip.annoyingvillagers.blue_demon_chestplate_thunder_charge").getString())
               .m_130938_(style -> style.m_131136_(true).m_131148_(TextColor.m_131266_(5634047)))
         );
         tooltip.add(Component.m_237113_(charge + " / 100").m_130938_(style -> style.m_131148_(TextColor.m_131266_(12450815))));
         tooltip.add(buildChestChargeMeter(charge));
         if (charge >= 100) {
            tooltip.add(
               Component.m_237113_(Component.m_237115_("tooltip.annoyingvillagers.thunder_charged").getString())
                  .m_130938_(style -> style.m_131136_(true).m_131148_(TextColor.m_131266_(8191999)))
            );
         }
      }

      private static Component buildChestChargeMeter(int charge) {
         int filledSteps = Math.round((float)charge / 100.0F * 10.0F);
         filledSteps = Mth.m_14045_(filledSteps, 0, 10);
         MutableComponent meter = Component.m_237119_();
         meter.m_7220_(Component.m_237113_("⛨ ").m_130938_(style -> style.m_131148_(TextColor.m_131266_(5634047))));

         for (int i = 0; i < 10; i++) {
            boolean filled = i < filledSteps;
            meter.m_7220_(Component.m_237113_(filled ? "▰" : "▱").m_130938_(style -> style.m_131148_(TextColor.m_131266_(filled ? 5634047 : 2306872))));
         }

         return meter;
      }
   }
}
