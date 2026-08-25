package com.pla.annoyingvillagers.mixin;

import com.pla.annoyingvillagers.util.TeamUtil;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {AbstractVillager.class},
   remap = true
)
public class VillagerMixin {
   @Inject(
      method = {"finalizeSpawn"},
      at = {@At("RETURN")}
   )
   private void villagerTeamJoin(
      ServerLevelAccessor world,
      DifficultyInstance difficulty,
      MobSpawnType reason,
      @Nullable SpawnGroupData spawnData,
      @Nullable CompoundTag dataTag,
      CallbackInfoReturnable<SpawnGroupData> cir
   ) {
      AbstractVillager self = (AbstractVillager)this;
      if (self.m_9236_() instanceof ServerLevel) {
         TeamUtil.addOrJoinTeam(self, "villagers");
      }
   }
}
