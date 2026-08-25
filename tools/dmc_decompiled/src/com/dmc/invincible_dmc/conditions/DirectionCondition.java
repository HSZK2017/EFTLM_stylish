package com.dmc.invincible_dmc.conditions;

import com.dmc.invincible_dmc.client.input.PlayerInputState;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class DirectionCondition implements Condition<ServerPlayerPatch> {
   private DirectionCondition.Direction direction;

   public DirectionCondition.Direction getDirection() {
      return this.direction;
   }

   public DirectionCondition() {
   }

   public DirectionCondition(DirectionCondition.Direction direction) {
      this.direction = direction;
   }

   public DirectionCondition(String direction) {
      this.direction = DirectionCondition.Direction.valueOf(direction.toUpperCase());
   }

   public Condition<ServerPlayerPatch> read(CompoundTag compoundTag) {
      String dir = compoundTag.m_128461_("direction");
      if (!dir.isEmpty()) {
         this.direction = DirectionCondition.Direction.valueOf(dir.toUpperCase());
      }

      return this;
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      if (this.direction != null) {
         tag.m_128359_("direction", this.direction.name().toLowerCase());
      }

      return tag;
   }

   public boolean predicate(ServerPlayerPatch serverPlayerPatch) {
      return this.direction == null ? false : PlayerInputState.isRemoteDown((Player)serverPlayerPatch.getOriginal(), this.direction.getBit());
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }

   public static enum Direction {
      UP(0),
      DOWN(1),
      LEFT(2),
      RIGHT(3);

      private final int bit;

      private Direction(int bit) {
         this.bit = bit;
      }

      public int getBit() {
         return this.bit;
      }
   }
}
