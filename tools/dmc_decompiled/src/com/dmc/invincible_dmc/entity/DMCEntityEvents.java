package com.dmc.invincible_dmc.entity;

import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.entity.dummy.DummyEntity;
import com.dmc.invincible_dmc.entity.dummy.DummyPatch;
import com.dmc.invincible_dmc.entity.judgementcut.JudgementCutEntity;
import com.dmc.invincible_dmc.entity.judgementcut.JudgementCutPatch;
import com.dmc.invincible_dmc.entity.soul.SoulEntity;
import com.dmc.invincible_dmc.entity.soul.SoulPatch;
import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordPatch;
import com.dmc.invincible_dmc.entity.util.DMCDodgeLocationIndicator;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.forgeevent.EntityPatchRegistryEvent;
import yesman.epicfight.gameasset.Armatures;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.MOD
)
public class DMCEntityEvents {
   @SubscribeEvent
   public static void onEntityPatchRegistry(EntityPatchRegistryEvent event) {
      event.getTypeEntry().put((EntityType)DMCEntities.DOPPELGANGER.get(), entity -> DoppelgangerPatch::new);
      event.getTypeEntry().put((EntityType)DMCEntities.DUMMY.get(), (Function<Entity, Supplier<DummyPatch<T>>>)entity -> DummyPatch::new);
      event.getTypeEntry().put((EntityType)DMCEntities.SOUL.get(), (Function<Entity, Supplier<SoulPatch<T>>>)entity -> SoulPatch::new);
      event.getTypeEntry()
         .put((EntityType)DMCEntities.SUMMONED_SWORD.get(), (Function<Entity, Supplier<DMCSummonedSwordPatch<T>>>)entity -> DMCSummonedSwordPatch::new);
      event.getTypeEntry().put((EntityType)DMCEntities.JUDGEMENT_CUT.get(), (Function<Entity, Supplier<JudgementCutPatch<T>>>)entity -> JudgementCutPatch::new);
   }

   @SubscribeEvent
   public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
      event.put((EntityType)DMCEntities.DOPPELGANGER.get(), DoppelgangerEntity.createAttributes().m_22265_());
      event.put((EntityType)DMCEntities.DUMMY.get(), DummyEntity.createAttributes().m_22265_());
      event.put((EntityType)DMCEntities.SOUL.get(), SoulEntity.createAttributes().m_22265_());
      event.put((EntityType)DMCEntities.SUMMONED_SWORD.get(), DMCSummonedSwordEntity.getDefaultAttribute());
      event.put((EntityType)DMCEntities.JUDGEMENT_CUT.get(), JudgementCutEntity.getDefaultAttribute());
      event.put((EntityType)DMCEntities.DMC_DODGELOCATION_INDICATOR.get(), DMCDodgeLocationIndicator.getDefaultAttribute());
   }

   public static void registerArmatures() {
      Armatures.registerEntityTypeArmature((EntityType)DMCEntities.DOPPELGANGER.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)DMCEntities.DUMMY.get(), Armatures.BIPED);
      Armatures.registerEntityTypeArmature((EntityType)DMCEntities.SOUL.get(), Armatures.SKELETON);
   }
}
