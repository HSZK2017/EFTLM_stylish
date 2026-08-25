package com.pla.annoyingvillagers.mixin;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pla.annoyingvillagers.util.CommonGoals;
import com.pla.annoyingvillagers.util.TeamUtil;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {AbstractSkeleton.class},
   remap = true
)
public class SkeletonMixin {
   @Inject(
      method = {"registerGoals"},
      at = {@At("HEAD")}
   )
   private void monsterTargetNpc(CallbackInfo ci) {
      AbstractSkeleton self = (AbstractSkeleton)this;
      if (!(self instanceof WitherSkeleton)) {
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
      AbstractSkeleton self = (AbstractSkeleton)this;
      if (!self.m_9236_().m_5776_() && self.m_20194_() != null) {
         TeamUtil.addOrJoinTeam(self, "herobrine");

         try {
            self.m_20194_().m_129892_().m_82094_().execute("data merge entity @s {CanPickUpLoot: 1b}", self.m_20203_().m_81324_().m_81325_(4));
         } catch (CommandSyntaxException var9) {
         }
      }
   }
}
