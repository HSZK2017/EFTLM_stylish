package com.dmc.invincible_dmc.capability;

import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerState;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerStateProvider;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.mixin.epicfight.EntityPatchProviderAccessor;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.yamato.JCEServer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.api.forgeevent.InnateSkillChangeEvent;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

@EventBusSubscriber(
   modid = "invincible_dmc"
)
public class DMCapabilities {
   public static DMCEntity getEntityCap(LivingEntity entity) {
      return (DMCEntity)entity.getCapability(DMCapabilityProvider.DMC_ENTITY).orElse(DMCEntity.EMPTY);
   }

   public static DMCPlayer getPlayerCap(Player player) {
      return (DMCPlayer)player.getCapability(DMCPlayerCapabilityProvider.DMC_PLAYER).orElse(DMCPlayer.EMPTY);
   }

   @SubscribeEvent
   public static void attachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
      if (event.getObject() instanceof LivingEntity living
         && (
            EntityPatchProviderAccessor.getCustomCapabilities().containsKey(living.m_6095_())
               || EntityPatchProviderAccessor.getCapabilities().containsKey(living.m_6095_())
         )
         && !((Entity)event.getObject()).getCapability(DMCapabilityProvider.DMC_ENTITY).isPresent()) {
         event.addCapability(ResourceLocation.fromNamespaceAndPath("invincible_dmc", "invincible_entity"), new DMCapabilityProvider());
      }

      if (event.getObject() instanceof Player) {
         if (!((Entity)event.getObject()).getCapability(DMCPlayerCapabilityProvider.DMC_PLAYER).isPresent()) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath("invincible_dmc", "invincible_player"), new DMCPlayerCapabilityProvider());
         }

         if (!((Entity)event.getObject()).getCapability(YamatoPlayerStateProvider.YAMATO_PLAYER_STATE).isPresent()) {
            event.addCapability(ResourceLocation.fromNamespaceAndPath("invincible_dmc", "yamato_player_state"), new YamatoPlayerStateProvider());
         }
      }
   }

   @SubscribeEvent
   public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
      event.register(DMCPlayer.class);
      event.register(DMCEntity.class);
      event.register(YamatoPlayerState.class);
   }

   @SubscribeEvent
   public static void onInnateSkillChange(InnateSkillChangeEvent event) {
      ServerPlayerPatch spp = event.getPlayerPatch();
      if (!spp.isLogicalClient() && event.getHand() == InteractionHand.MAIN_HAND) {
         CapabilityItem fromCap = event.getFromItemCapability();
         CapabilityItem toCap = event.getToItemCapability();
         Skill fromSkill = fromCap == null ? null : fromCap.getInnateSkill(spp, event.getFrom());
         Skill toSkill = toCap == null ? null : toCap.getInnateSkill(spp, event.getTo());
         YamatoPlayerState state = YamatoPlayerStateProvider.get((Player)spp.getOriginal());
         if (fromSkill instanceof VergilSkill && !(toSkill instanceof VergilSkill) && state.isSdtActive()) {
            state.setSdtActive(false);
            JCEServer.onSDTExitServer((Player)spp.getOriginal());
         }

         if (toSkill instanceof VergilSkill) {
            SkillContainer container = spp.getSkill(SkillSlots.WEAPON_INNATE);
            if (container != null && !container.isEmpty() && container.getSkill() == toSkill) {
               int restoredStack = Math.max(0, Math.min(toSkill.getMaxStack(), state.getDtStack()));
               float maxResource = container.getMaxResource();
               float restoredResource = Math.max(0.0F, state.getDtResource());
               if (maxResource > 0.0F && restoredResource >= maxResource) {
                  if (restoredStack < toSkill.getMaxStack()) {
                     restoredStack++;
                     restoredResource = 0.0F;
                  } else {
                     restoredResource = maxResource;
                  }
               }

               state.setDtStack(restoredStack);
               state.setDtResource(restoredResource);
               toSkill.setConsumptionSynchronize(container, restoredResource);
               toSkill.setStackSynchronize(container, restoredStack);
               SkillDataManager dataManager = container.getDataManager();
               if (dataManager.hasData((SkillDataKey)DMCSkillDataKeys.CONCENTRATION.get())) {
                  dataManager.setDataSync((SkillDataKey)DMCSkillDataKeys.CONCENTRATION.get(), state.getConcentration());
               }

               if (dataManager.hasData((SkillDataKey)DMCSkillDataKeys.CONC_LAST_TIER.get())) {
                  dataManager.setDataSync((SkillDataKey)DMCSkillDataKeys.CONC_LAST_TIER.get(), state.getConcentrationTier());
               }

               if (dataManager.hasData((SkillDataKey)DMCSkillDataKeys.SDT_VALUE.get())) {
                  dataManager.setDataSync((SkillDataKey)DMCSkillDataKeys.SDT_VALUE.get(), state.getSdtValue());
               }

               if (dataManager.hasData((SkillDataKey)DMCSkillDataKeys.IS_SDT.get())) {
                  dataManager.setDataSync((SkillDataKey)DMCSkillDataKeys.IS_SDT.get(), state.isSdtActive());
               }
            }
         }
      }
   }
}
