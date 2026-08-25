package com.dmc.invincible_dmc.compat.controlify;

import com.dmc.invincible_dmc.InvincibleMod_DMC;
import com.dmc.invincible_dmc.client.DMCKeyMappings;
import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.bind.ControlifyBindApi;
import dev.isxander.controlify.api.bind.InputBindingSupplier;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.isxander.controlify.api.entrypoint.InitContext;
import dev.isxander.controlify.api.entrypoint.PreInitContext;
import dev.isxander.controlify.bindings.BindContext;
import dev.isxander.controlify.bindings.RadialIcons;
import dev.isxander.controlify.utils.render.Blit;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.client.ClientEngine;

public class ControlifyCompat implements ControlifyEntrypoint {
   private static final BindContext IN_GAME_EPIC_FIGHT_CONTEXT = new BindContext(InvincibleMod_DMC.rl("epicfight_combat"), mc -> {
      boolean isInGame = mc.f_91080_ == null && mc.f_91073_ != null && mc.f_91074_ != null;
      return isInGame && ClientEngine.getInstance().isEpicFightMode();
   });
   private static InputBindingSupplier primaryAction;
   private static InputBindingSupplier secondaryAction;
   private static InputBindingSupplier specialAbility1;
   private static InputBindingSupplier specialAbility2;

   @Nullable
   public static InputBindingSupplier getInputBindingFromKeyMapping(@NotNull KeyMapping keyMapping) {
      if (keyMapping == DMCKeyMappings.KEY1) {
         return primaryAction;
      } else if (keyMapping == DMCKeyMappings.KEY2) {
         return secondaryAction;
      } else if (keyMapping == DMCKeyMappings.KEY3) {
         return specialAbility1;
      } else {
         return keyMapping == DMCKeyMappings.KEY4 ? specialAbility2 : null;
      }
   }

   private static void registerInputBindings(ControlifyBindApi registrar) {
      primaryAction = registrar.registerBinding(
         builder -> builder.id(InvincibleMod_DMC.rl("primary_action"))
               .category(ControlifyCompat.ComponentConstants.COMMON_CATEGORY)
               .allowedContexts(new BindContext[]{IN_GAME_EPIC_FIGHT_CONTEXT})
               .name(ControlifyCompat.ComponentConstants.PRIMARY_ACTION)
               .description(ControlifyCompat.ComponentConstants.PRIMARY_ACTION_DESCRIPTION)
               .addKeyCorrelation(DMCKeyMappings.KEY1)
               .keyEmulation(DMCKeyMappings.KEY1)
      );
      secondaryAction = registrar.registerBinding(
         builder -> builder.id(InvincibleMod_DMC.rl("secondary_action"))
               .category(ControlifyCompat.ComponentConstants.COMMON_CATEGORY)
               .allowedContexts(new BindContext[]{IN_GAME_EPIC_FIGHT_CONTEXT})
               .name(ControlifyCompat.ComponentConstants.SECONDARY_ACTION)
               .description(ControlifyCompat.ComponentConstants.SECONDARY_ACTION_DESCRIPTION)
               .addKeyCorrelation(DMCKeyMappings.KEY2)
               .keyEmulation(DMCKeyMappings.KEY2)
      );
      specialAbility1 = registrar.registerBinding(
         builder -> builder.id(InvincibleMod_DMC.rl("special_ability_1"))
               .category(ControlifyCompat.ComponentConstants.COMMON_CATEGORY)
               .allowedContexts(new BindContext[]{IN_GAME_EPIC_FIGHT_CONTEXT})
               .name(ControlifyCompat.ComponentConstants.SPECIAL_ABILITY_1)
               .description(ControlifyCompat.ComponentConstants.SPECIAL_ABILITY_1_DESCRIPTION)
               .addKeyCorrelation(DMCKeyMappings.KEY3)
               .keyEmulation(DMCKeyMappings.KEY3)
               .radialCandidate(ControlifyCompat.InvincibleRadialIcons.COMBO_ATTACKS.getId())
      );
      specialAbility2 = registrar.registerBinding(
         builder -> builder.id(InvincibleMod_DMC.rl("special_ability_2"))
               .category(ControlifyCompat.ComponentConstants.COMMON_CATEGORY)
               .allowedContexts(new BindContext[]{IN_GAME_EPIC_FIGHT_CONTEXT})
               .name(ControlifyCompat.ComponentConstants.SPECIAL_ABILITY_2)
               .description(ControlifyCompat.ComponentConstants.SPECIAL_ABILITY_2_DESCRIPTION)
               .addKeyCorrelation(DMCKeyMappings.KEY4)
               .keyEmulation(DMCKeyMappings.KEY4)
               .radialCandidate(ControlifyCompat.InvincibleRadialIcons.COMBO_ATTACKS.getId())
      );
   }

   private static void registerCustomRadialIcons() {
      for (ControlifyCompat.InvincibleRadialIcons icon : ControlifyCompat.InvincibleRadialIcons.values()) {
         ResourceLocation location = icon.getId();
         RadialIcons.registerIcon(location, (graphics, x, y, tickDelta) -> {
            graphics.m_280168_().m_85836_();
            graphics.m_280168_().m_252880_((float)x, (float)y, 0.0F);
            graphics.m_280168_().m_85841_(0.5F, 0.5F, 1.0F);
            Blit.blitTex(graphics, location, 0, 0, 0, 0, 32, 32, 32, 32);
            graphics.m_280168_().m_85849_();
         });
      }
   }

   public void onControllersDiscovered(ControlifyApi controlify) {
   }

   public void onControlifyInit(InitContext context) {
   }

   public void onControlifyPreInit(PreInitContext context) {
      ControlifyModAvailability.setIsModInstalled(true);
      ControlifyBindApi registrar = ControlifyBindApi.get();
      registerCustomRadialIcons();
      registrar.registerBindContext(IN_GAME_EPIC_FIGHT_CONTEXT);
      registerInputBindings(registrar);
   }

   private static class ComponentConstants {
      private static final Component COMMON_CATEGORY = Component.m_237115_("key.invincible_dmc.category");
      private static final Component PRIMARY_ACTION = Component.m_237115_("key.invincible_dmc.key1");
      private static final Component SECONDARY_ACTION = Component.m_237115_("key.invincible_dmc.key2");
      private static final Component SPECIAL_ABILITY_1 = Component.m_237115_("key.invincible_dmc.key3");
      private static final Component SPECIAL_ABILITY_2 = Component.m_237115_("key.invincible_dmc.key4");
      private static final Component PRIMARY_ACTION_DESCRIPTION = Component.m_237115_("key.invincible_dmc.key1.description");
      private static final Component SECONDARY_ACTION_DESCRIPTION = Component.m_237115_("key.invincible_dmc.key2.description");
      private static final Component SPECIAL_ABILITY_1_DESCRIPTION = Component.m_237115_("key.invincible_dmc.key3.description");
      private static final Component SPECIAL_ABILITY_2_DESCRIPTION = Component.m_237115_("key.invincible_dmc.key4.description");
   }

   private static enum InvincibleRadialIcons {
      COMBO_ATTACKS(InvincibleMod_DMC.rl("textures/gui/skills/weapon_innate/combo_demo.png"));

      @NotNull
      private final ResourceLocation id;

      private InvincibleRadialIcons(@NotNull ResourceLocation id) {
         this.id = id;
      }

      @NotNull
      public ResourceLocation getId() {
         return this.id;
      }
   }
}
