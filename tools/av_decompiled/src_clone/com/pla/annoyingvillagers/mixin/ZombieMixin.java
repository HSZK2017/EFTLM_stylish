package com.pla.annoyingvillagers.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pla.annoyingvillagers.util.CommonGoals;
import com.pla.annoyingvillagers.util.TeamUtil;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {Zombie.class},
   remap = true
)
public class ZombieMixin {
   @Inject(
      method = {"registerGoals"},
      at = {@At("HEAD")}
   )
   private void monsterTargetNpc(CallbackInfo ci) {
      Zombie self = (Zombie)this;
      if (!(self instanceof Drowned) && !(self instanceof ZombifiedPiglin)) {
         CommonGoals.registerGoalForHostileNpc(self);
      }
   }

   @Inject(
      method = {"finalizeSpawn"},
      at = {@At("RETURN")}
   )
   private void monsterJoinHerobrineTeam(
      ServerLevelAccessor world,
      DifficultyInstance difficulty,
      MobSpawnType reason,
      @Nullable SpawnGroupData spawnData,
      @Nullable CompoundTag dataTag,
      CallbackInfoReturnable<SpawnGroupData> cir
   ) {
      Zombie self = (Zombie)this;
      if (!self.m_9236_().m_5776_() && self.m_20194_() != null) {
         TeamUtil.addOrJoinTeam(self, "herobrine");

         try {
            self.m_20194_().m_129892_().m_82094_().execute("data merge entity @s {CanPickUpLoot: 1b}", self.m_20203_().m_81324_().m_81325_(4));
         } catch (CommandSyntaxException var9) {
         }

         Random random = new Random();
         if (random.nextFloat() < 0.2F) {
            self.m_8061_(EquipmentSlot.HEAD, createDyedArmor(Items.f_42407_, random));
         }

         if (random.nextFloat() < 0.2F) {
            self.m_8061_(EquipmentSlot.CHEST, createDyedArmor(Items.f_42408_, random));
         }

         if (random.nextFloat() < 0.2F) {
            self.m_8061_(EquipmentSlot.LEGS, createDyedArmor(Items.f_42462_, random));
         }

         if (random.nextFloat() < 0.2F) {
            self.m_8061_(EquipmentSlot.FEET, createDyedArmor(Items.f_42463_, random));
         }
      }
   }

   private static ItemStack createDyedArmor(Item item, Random random) {
      ItemStack stack = new ItemStack(item);
      if (stack.m_41720_() instanceof DyeableLeatherItem dyeable) {
         int red = random.nextInt(256);
         int green = random.nextInt(256);
         int blue = random.nextInt(256);
         int color = red << 16 | green << 8 | blue;
         dyeable.m_41115_(stack, color);
      }

      return stack;
   }
}
