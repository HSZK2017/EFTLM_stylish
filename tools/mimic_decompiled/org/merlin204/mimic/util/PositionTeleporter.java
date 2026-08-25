package org.merlin204.mimic.util;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PositionTeleporter implements ITeleporter {
   @NotNull
   public BlockPos pos;

   public PositionTeleporter(@NotNull BlockPos pos) {
      this.pos = pos;
   }

   public Entity placeEntity(Entity entity, ServerLevel currentWorld, ServerLevel destinationWorld, float yaw, Function<Boolean, Entity> repositionEntity) {
      if (entity != null && repositionEntity != null) {
         Entity repositioned = repositionEntity.apply(false);
         return repositioned == null ? entity : repositioned;
      } else {
         return entity;
      }
   }

   @Nullable
   public PortalInfo getPortalInfo(Entity entity, ServerLevel destinationLevel, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
      if (entity != null && destinationLevel != null && this.pos != null) {
         while (!destinationLevel.m_8055_(this.pos).m_60713_(Blocks.f_50016_)) {
            this.pos = this.pos.m_7494_();
         }

         if (entity instanceof ServerPlayer player) {
            player.m_7292_(new MobEffectInstance(MobEffects.f_19606_, 200, 4, false, false));
         }

         return new PortalInfo(this.pos.m_252807_(), Vec3.f_82478_, entity.m_146908_(), entity.m_146909_());
      } else {
         return null;
      }
   }
}
