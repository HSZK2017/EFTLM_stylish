package com.dmc.invincible_dmc.item.enchantment;

import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerState;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerStateProvider;
import com.dmc.invincible_dmc.item.YamatoItem;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.ConcentrationManager;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.FORGE
)
public final class SuperYamatoEnchantmentHandler {
   private static final float DT_RESOURCE_REGEN_PER_TICK = 8.0F;
   private static final float SDT_REGEN_PER_TICK = 20.0F;

   private SuperYamatoEnchantmentHandler() {
   }

   @SubscribeEvent
   public static void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END && event.player instanceof ServerPlayer player) {
         boolean var8 = hasSuperYamato(player.m_21205_());
         ConcentrationManager.setSuperYamatoLock(player.m_20148_(), var8);
         SinDevilTriggerManager.setSuperYamatoLock(player.m_20148_(), var8);
         if (var8) {
            ServerPlayerPatch playerPatch = EpicFightCapabilities.getServerPlayerPatch(player);
            if (playerPatch != null) {
               SkillContainer container = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
               YamatoPlayerState state = YamatoPlayerStateProvider.get(player);
               if (state.getConcentration() < 10000.0F) {
                  state.setConcentration(10000.0F);
               }

               if (container != null && !container.isEmpty() && container.getSkill() instanceof VergilSkill skill) {
                  if (ConcentrationManager.getConcentration(container) < 10000.0F) {
                     ConcentrationManager.setConcentrationRaw(container, 10000.0F);
                  }

                  float currentSdt = SinDevilTriggerManager.getSDTValue(container);
                  if (currentSdt < 1000.0F) {
                     SinDevilTriggerManager.setSDTValueRaw(container, Math.min(1000.0F, currentSdt + 20.0F));
                  }

                  restoreDt(container, skill, state);
               } else {
                  state.setSdtValue(Math.min(1000.0F, state.getSdtValue() + 20.0F));
               }
            }
         }
      }
   }

   private static void restoreDt(SkillContainer container, VergilSkill skill, YamatoPlayerState state) {
      int maxStack = skill.getMaxStack();
      float maxResource = container.getMaxResource();
      if (maxStack > 0 && !(maxResource <= 0.0F)) {
         int stack = Math.max(0, Math.min(container.getStack(), maxStack));
         float resource = Math.max(0.0F, Math.min(container.getResource(), maxResource));
         if (stack < maxStack || !(resource >= maxResource)) {
            float nextResource = resource + 8.0F;

            int nextStack;
            for (nextStack = stack; nextResource >= maxResource && nextStack < maxStack; nextStack++) {
               nextResource -= maxResource;
            }

            if (nextStack >= maxStack) {
               nextStack = maxStack;
               nextResource = maxResource;
            }

            int stackGained = nextStack - stack;

            for (int index = 0; index < stackGained; index++) {
               skill.setConsumptionSynchronize(container, maxResource);
            }

            if (nextResource > 1.0E-4F || nextStack >= maxStack && nextResource >= maxResource) {
               skill.setConsumptionSynchronize(container, nextResource);
            }

            state.setDtStack(container.getStack());
            state.setDtResource(container.getResource());
         }
      }
   }

   private static boolean hasSuperYamato(ItemStack stack) {
      return !stack.m_41619_()
         && stack.m_41720_() instanceof YamatoItem
         && EnchantmentHelper.m_44843_((Enchantment)DMCEnchantments.SUPER_YAMATO.get(), stack) > 0;
   }
}
