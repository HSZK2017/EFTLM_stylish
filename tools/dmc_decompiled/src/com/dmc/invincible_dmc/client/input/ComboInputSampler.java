package com.dmc.invincible_dmc.client.input;

import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.api.skill.ComboType;
import com.dmc.invincible_dmc.client.DMCKeyMappings;
import com.dmc.invincible_dmc.compat.controlify.ControlifyCompat;
import com.dmc.invincible_dmc.compat.controlify.ControlifyModAvailability;
import com.dmc.invincible_dmc.skill.weapon_innate.AbstractDmcInnateSkill;
import com.dmc.invincible_dmc.utils.DMCLog;
import com.mojang.blaze3d.platform.InputConstants.Type;
import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.bind.InputBinding;
import dev.isxander.controlify.api.bind.InputBindingSupplier;
import dev.isxander.controlify.controller.ControllerEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.Key;
import net.minecraftforge.client.event.InputEvent.MouseButton;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;

@OnlyIn(Dist.CLIENT)
public final class ComboInputSampler {
   static final Map<ComboType, KeyMapping> TYPE_KEY_MAP = new HashMap<>();
   static ComboType[] STATES_TO_TYPE = new ComboType[0];
   static ComboInputSampler.ComboInputState[] INPUT_STATES = new ComboInputSampler.ComboInputState[0];
   private static final Map<KeyMapping, Boolean> rawKeyStates = new ConcurrentHashMap<>();

   public static ComboType getComboTypeByIndex(int index) {
      return index >= 0 && index < STATES_TO_TYPE.length ? STATES_TO_TYPE[index] : null;
   }

   static void init() {
      List<ComboType> allTypes = new ArrayList<>(ComboType.ENUM_MANAGER.universalValues());
      STATES_TO_TYPE = new ComboType[allTypes.size()];
      INPUT_STATES = new ComboInputSampler.ComboInputState[allTypes.size()];

      for (int i = 0; i < allTypes.size(); i++) {
         INPUT_STATES[i] = new ComboInputSampler.ComboInputState();
         STATES_TO_TYPE[i] = allTypes.get(i);
      }

      register(ComboNode.ComboTypes.KEY_1, DMCKeyMappings.KEY1);
      register(ComboNode.ComboTypes.KEY_2, DMCKeyMappings.KEY2);
      register(ComboNode.ComboTypes.KEY_3, DMCKeyMappings.KEY3);
      register(ComboNode.ComboTypes.KEY_4, DMCKeyMappings.KEY4);
      register(ComboNode.ComboTypes.PROVOCATION, DMCKeyMappings.PROVOCATION);
      register(ComboNode.ComboTypes.WEAPON_INNATE, EpicFightKeyMappings.WEAPON_INNATE_SKILL);
      MinecraftForge.EVENT_BUS.register(ComboInputSampler.class);
   }

   static void register(ComboType type, KeyMapping keyMapping) {
      TYPE_KEY_MAP.put(type, keyMapping);
      int idx = findStateIndex(type);
      if (idx >= 0) {
         INPUT_STATES[idx].clear();
         INPUT_STATES[idx].keyMapping = keyMapping;
      }
   }

   static int findStateIndex(ComboType type) {
      for (int i = 0; i < STATES_TO_TYPE.length; i++) {
         if (STATES_TO_TYPE[i] == type) {
            return i;
         }
      }

      return -1;
   }

   @SubscribeEvent
   public static void onKeyInput(Key event) {
      if (event.getAction() != 2) {
         com.mojang.blaze3d.platform.InputConstants.Key inputKey = Type.KEYSYM.m_84895_(event.getKey());
         boolean isDown = event.getAction() == 1;
         updateRawDirectionKeys(inputKey, isDown);
         if (Minecraft.m_91087_().f_91080_ == null) {
            postRawInputEvent(inputKey, isDown);
         }
      }
   }

   @SubscribeEvent
   public static void onMouseInput(MouseButton event) {
      if (event.getAction() != 2) {
         com.mojang.blaze3d.platform.InputConstants.Key inputKey = Type.MOUSE.m_84895_(event.getButton());
         boolean isDown = event.getAction() == 1;
         updateRawDirectionKeys(inputKey, isDown);
         if (Minecraft.m_91087_().f_91080_ == null) {
            postRawInputEvent(inputKey, isDown);
         }
      }
   }

   private static void updateRawDirectionKeys(com.mojang.blaze3d.platform.InputConstants.Key inputKey, boolean isDown) {
      Options opts = Minecraft.m_91087_().f_91066_;
      if (opts.f_92085_.isActiveAndMatches(inputKey)) {
         rawKeyStates.put(opts.f_92085_, isDown);
      }

      if (opts.f_92087_.isActiveAndMatches(inputKey)) {
         rawKeyStates.put(opts.f_92087_, isDown);
      }

      if (opts.f_92086_.isActiveAndMatches(inputKey)) {
         rawKeyStates.put(opts.f_92086_, isDown);
      }

      if (opts.f_92088_.isActiveAndMatches(inputKey)) {
         rawKeyStates.put(opts.f_92088_, isDown);
      }

      if (DMCKeyMappings.DOPPEL_CONTROL.isActiveAndMatches(inputKey)) {
         rawKeyStates.put(DMCKeyMappings.DOPPEL_CONTROL, isDown);
      }

      if (DMCKeyMappings.DMC_DODGE.isActiveAndMatches(inputKey)) {
         rawKeyStates.put(DMCKeyMappings.DMC_DODGE, isDown);
      }

      if (DMCKeyMappings.DMC_LOCK_ON.isActiveAndMatches(inputKey)) {
         rawKeyStates.put(DMCKeyMappings.DMC_LOCK_ON, isDown);
      }

      if (DMCKeyMappings.SDT_CHARGE.isActiveAndMatches(inputKey)) {
         rawKeyStates.put(DMCKeyMappings.SDT_CHARGE, isDown);
      }
   }

   private static void postRawInputEvent(com.mojang.blaze3d.platform.InputConstants.Key inputKey, boolean isDown) {
      long now = InputClock.nowMillis();
      long tick = InputClock.currentTick();
      if (!BypassInput()) {
         List<Integer> matchingIndices = new ArrayList<>();

         for (int i = 0; i < INPUT_STATES.length; i++) {
            ComboInputSampler.ComboInputState state = INPUT_STATES[i];
            if (state.keyMapping != null && state.keyMapping.isActiveAndMatches(inputKey)) {
               matchingIndices.add(i);
            }
         }

         if (matchingIndices.size() > 1) {
            boolean hasPrimaryKey = matchingIndices.stream().anyMatch(ix -> isPrimaryComboType(STATES_TO_TYPE[ix]));
            if (hasPrimaryKey) {
               matchingIndices.removeIf(ix -> !isPrimaryComboType(STATES_TO_TYPE[ix]));
            }
         }

         for (int ix : matchingIndices) {
            ComboInputSampler.ComboInputState state = INPUT_STATES[ix];
            state.eventQueue.offer(new ComboInputSampler.InputEventRecord(isDown, now, tick, InputClock.nextSequence()));
            rawKeyStates.put(state.keyMapping, isDown);
            if (isDown) {
               DMCLog.info(DMCLog.Category.COMBO_ENGINE, "[KeyInput] PRESS key={}", state.keyMapping.m_90860_());
            }
         }
      }
   }

   private static boolean isPrimaryComboType(ComboType type) {
      return type == ComboNode.ComboTypes.KEY_1
         || type == ComboNode.ComboTypes.KEY_2
         || type == ComboNode.ComboTypes.KEY_3
         || type == ComboNode.ComboTypes.KEY_4
         || type == ComboNode.ComboTypes.PROVOCATION;
   }

   public static void setRawKeyState(KeyMapping key, boolean pressed) {
      rawKeyStates.put(key, pressed);
   }

   public static boolean isRawKeyDown(KeyMapping key) {
      return rawKeyStates.getOrDefault(key, false);
   }

   static void sampleInput() {
      pollControlifyEvents();
   }

   private static void pollControlifyEvents() {
      if (ControlifyModAvailability.isModInstalled()) {
         Optional<ControllerEntity> maybeController = ControlifyApi.get().getCurrentController();
         if (!maybeController.isEmpty()) {
            ControllerEntity controller = maybeController.get();
            long now = System.currentTimeMillis();

            for (ComboInputSampler.ComboInputState state : INPUT_STATES) {
               if (state.keyMapping != null) {
                  InputBindingSupplier inputBindingSupplier = ControlifyCompat.getInputBindingFromKeyMapping(state.keyMapping);
                  if (inputBindingSupplier != null) {
                     InputBinding binding = inputBindingSupplier.on(controller);
                     if (binding.justPressed()) {
                        rawKeyStates.put(state.keyMapping, true);
                        state.eventQueue.offer(new ComboInputSampler.InputEventRecord(true, now, InputClock.currentTick(), InputClock.nextSequence()));
                     } else if (binding.justReleased()) {
                        rawKeyStates.put(state.keyMapping, false);
                        state.eventQueue.offer(new ComboInputSampler.InputEventRecord(false, now, InputClock.currentTick(), InputClock.nextSequence()));
                     }
                  }
               }
            }
         }
      }
   }

   public static int getPressDuration(ComboType comboType, boolean isReserved) {
      if (comboType.getSubTypes().isEmpty()) {
         int idx = findStateIndex(comboType);
         if (idx < 0) {
            return 0;
         } else if (!INPUT_STATES[idx].curDown) {
            return isReserved ? INPUT_STATES[idx].pressedTicks : 0;
         } else {
            return INPUT_STATES[idx].pressedTicks;
         }
      } else {
         int min = Integer.MAX_VALUE;

         for (ComboType sub : comboType.getSubTypes()) {
            int idx = findStateIndex(sub);
            if (idx < 0) {
               return 0;
            }

            if (!INPUT_STATES[idx].curDown) {
               if (!isReserved) {
                  return 0;
               }
            } else {
               min = Math.min(min, INPUT_STATES[idx].pressedTicks);
            }
         }

         return min == Integer.MAX_VALUE ? 0 : min;
      }
   }

   public static boolean isPressed(ComboType comboType) {
      if (comboType.getSubTypes().isEmpty()) {
         int idx = findStateIndex(comboType);
         return idx >= 0 && INPUT_STATES[idx].curDown;
      } else {
         for (ComboType sub : comboType.getSubTypes()) {
            int idx = findStateIndex(sub);
            if (idx < 0 || !INPUT_STATES[idx].curDown) {
               return false;
            }
         }

         return true;
      }
   }

   public static void forceClearAll() {
      for (ComboInputSampler.ComboInputState state : INPUT_STATES) {
         state.clear();
      }
   }

   public static void forceSetPressed(ComboType type) {
      int idx = findStateIndex(type);
      if (idx >= 0) {
         INPUT_STATES[idx].curDown = true;
         INPUT_STATES[idx].pressedTicks = 0;
         INPUT_STATES[idx].pressStartTimeMs = InputClock.nowMillis();
         INPUT_STATES[idx].cycleExecuted = true;
      }
   }

   static boolean isControllerActive() {
      return !ControlifyModAvailability.isModInstalled() ? false : ControlifyApi.get().getCurrentController().isPresent();
   }

   public static boolean BypassInput() {
      Minecraft minecraft = Minecraft.m_91087_();
      if (Minecraft.m_91087_().f_91080_ != null) {
         return false;
      } else {
         LocalPlayerPatch lpp = DMComboEngine.getLocalPlayerPatch();
         if (lpp == null) {
            return true;
         } else {
            SkillContainer container = lpp.getSkill(SkillSlots.WEAPON_INNATE);
            if (!(container.getSkill() instanceof AbstractDmcInnateSkill)) {
               return true;
            } else {
               return minecraft.f_91080_ == null && !minecraft.m_91104_() ? minecraft.f_91074_ == null || !minecraft.f_91074_.m_6084_() : true;
            }
         }
      }
   }

   public static class ComboInputState {
      final Queue<ComboInputSampler.InputEventRecord> eventQueue = new ConcurrentLinkedQueue<>();
      KeyMapping keyMapping;
      boolean curDown;
      int pressedTicks;
      long pressStartTimeMs;
      boolean cycleExecuted;

      void clear() {
         this.curDown = false;
         this.pressedTicks = 0;
         this.pressStartTimeMs = 0L;
         this.cycleExecuted = false;
         this.eventQueue.clear();
      }
   }

   static record InputEventRecord(boolean isDown, long timestampMs, long capturedTick, long sequence) {
   }
}
