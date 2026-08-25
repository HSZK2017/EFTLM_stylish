package com.dmc.invincible_dmc.conditions;

import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.data.conditions.Condition.ParameterEditor;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class ComboInterruptWindowCondition implements Condition<PlayerPatch<?>> {
   public Condition<PlayerPatch<?>> read(CompoundTag tag) {
      return this;
   }

   public CompoundTag serializePredicate() {
      return new CompoundTag();
   }

   public boolean predicate(PlayerPatch<?> patch) {
      return check(patch);
   }

   public List<ParameterEditor> getAcceptingParameters(Screen screen) {
      return null;
   }

   public static boolean check(LivingEntityPatch<?> patch) {
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(patch);
      StaticAnimation real = DMCAnimationUtils.getRealAnimation(patch);
      if (player != null && real != null) {
         Optional<TimePairList> yamatoWindow = real.getProperty(YamatoAttackAnimation.COMBO_INTERRUPT_TIME);
         return yamatoWindow.isPresent() ? yamatoWindow.get().isTimeInPairs(player.getElapsedTime()) : false;
      } else {
         return false;
      }
   }
}
