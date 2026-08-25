package com.dmc.invincible_dmc.conditions;

import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class AnimationElapsedTimeCondition implements Condition<PlayerPatch<?>> {
   private float minTime;
   private float maxTime = Float.MAX_VALUE;

   public AnimationElapsedTimeCondition() {
   }

   public AnimationElapsedTimeCondition(float minTime, float maxTime) {
      this.minTime = minTime;
      this.maxTime = maxTime;
   }

   public Condition<PlayerPatch<?>> read(CompoundTag tag) {
      if (!tag.m_128441_("min")) {
         throw new IllegalArgumentException("AnimationElapsedTimeCondition: 'min' not specified!");
      } else {
         this.minTime = tag.m_128457_("min");
         this.maxTime = tag.m_128441_("max") ? tag.m_128457_("max") : Float.MAX_VALUE;
         return this;
      }
   }

   public CompoundTag serializePredicate() {
      CompoundTag tag = new CompoundTag();
      tag.m_128350_("min", this.minTime);
      tag.m_128350_("max", this.maxTime);
      return tag;
   }

   public boolean predicate(PlayerPatch<?> patch) {
      return check(patch, this.minTime, this.maxTime);
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }

   public float getMinTime() {
      return this.minTime;
   }

   public float getMaxTime() {
      return this.maxTime;
   }

   public static boolean check(LivingEntityPatch<?> patch, float minTime, float maxTime) {
      float prev = DMCAnimationUtils.getPreviousElapsedTime(patch);
      float curr = DMCAnimationUtils.getElapsedTime(patch);
      return !(prev < 0.0F) && !(curr < 0.0F) ? curr >= minTime && prev <= maxTime : false;
   }
}
