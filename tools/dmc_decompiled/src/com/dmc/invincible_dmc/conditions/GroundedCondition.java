package com.dmc.invincible_dmc.conditions;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class GroundedCondition implements Condition<ServerPlayerPatch> {
   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      return this;
   }

   public CompoundTag serializePredicate() {
      return new CompoundTag();
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      ServerPlayer player = (ServerPlayer)serverPlayerPatch.getOriginal();
      return check(player);
   }

   public static boolean check(LivingEntity entity) {
      if (entity.m_20096_()) {
         return true;
      } else {
         double feetY = entity.m_20186_();
         BlockPos pos = BlockPos.m_274561_(entity.m_20185_(), feetY - 0.1, entity.m_20189_());
         BlockState state = entity.m_9236_().m_8055_(pos);
         if (!state.m_60795_()) {
            VoxelShape shape = state.m_60812_(entity.m_9236_(), pos);
            if (!shape.m_83281_()) {
               double surfaceY = shape.m_83297_(Axis.Y) + (double)pos.m_123342_();
               return Math.abs(feetY - surfaceY) < 0.5;
            }
         }

         return false;
      }
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }
}
