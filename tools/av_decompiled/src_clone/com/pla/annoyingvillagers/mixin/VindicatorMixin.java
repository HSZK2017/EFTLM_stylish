package com.pla.annoyingvillagers.mixin;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {Vindicator.class},
   remap = true
)
public class VindicatorMixin {
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
   }
}
