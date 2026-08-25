package com.dmc.invincible_dmc.entity;

import com.dmc.invincible_dmc.entity.chair.PowerChairSeatEntity;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.dummy.DummyEntity;
import com.dmc.invincible_dmc.entity.judgementcut.JudgementCutEntity;
import com.dmc.invincible_dmc.entity.portal.PortalEntity;
import com.dmc.invincible_dmc.entity.soul.SoulEntity;
import com.dmc.invincible_dmc.entity.summonedsword.BlisteringBladesEntity;
import com.dmc.invincible_dmc.entity.summonedsword.DMCSummonedSwordEntity;
import com.dmc.invincible_dmc.entity.summonedsword.HeavyRainBladesEntity;
import com.dmc.invincible_dmc.entity.summonedsword.ProvocationBladesEntity;
import com.dmc.invincible_dmc.entity.summonedsword.SpineBladeEntity;
import com.dmc.invincible_dmc.entity.summonedsword.SpiralBladesEntity;
import com.dmc.invincible_dmc.entity.summonedsword.StormBladesEntity;
import com.dmc.invincible_dmc.entity.summonedsword.TripleBladesEntity;
import com.dmc.invincible_dmc.entity.util.DMCDodgeLocationIndicator;
import com.dmc.invincible_dmc.entity.vfx.DMCSlashEffect;
import com.dmc.invincible_dmc.entity.vfx.DMCVoidSlashEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class DMCEntities {
   public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, "invincible_dmc");
   public static final RegistryObject<EntityType<PowerChairSeatEntity>> POWER_CHAIR_SEAT = ENTITIES.register(
      "power_chair_seat",
      () -> Builder.m_20704_(PowerChairSeatEntity::new, MobCategory.MISC)
            .m_20699_(0.01F, 0.01F)
            .m_20702_(10)
            .m_20717_(1)
            .m_20716_()
            .m_20698_()
            .m_20712_("power_chair_seat")
   );
   public static final RegistryObject<EntityType<DMCSlashEffect>> SLASH_EFFECT = ENTITIES.register(
      "slash_effect",
      () -> Builder.m_20704_(DMCSlashEffect::new, MobCategory.MISC)
            .m_20699_(1.0F, 1.0F)
            .setTrackingRange(4)
            .setUpdateInterval(20)
            .m_20716_()
            .setCustomClientFactory(DMCSlashEffect::createInstance)
            .m_20712_("slash_effect")
   );
   public static final RegistryObject<EntityType<DMCVoidSlashEffect>> VOID_SLASH_EFFECT = ENTITIES.register(
      "void_slash_effect",
      () -> Builder.m_20704_(DMCVoidSlashEffect::new, MobCategory.MISC)
            .m_20699_(1.0F, 1.0F)
            .setTrackingRange(4)
            .setUpdateInterval(20)
            .m_20716_()
            .setCustomClientFactory(DMCVoidSlashEffect::createInstance)
            .m_20712_("void_slash_effect")
   );
   public static final RegistryObject<EntityType<DummyEntity>> DUMMY = ENTITIES.register(
      "dummy",
      () -> Builder.m_20704_(DummyEntity::new, MobCategory.MISC)
            .m_20699_(0.6F, 1.95F)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .setCustomClientFactory((spawnEntity, level) -> new DummyEntity((EntityType<? extends DummyEntity>)DUMMY.get(), level))
            .m_20712_("dummy")
   );
   public static final RegistryObject<EntityType<SoulEntity>> SOUL = ENTITIES.register(
      "soul",
      () -> Builder.m_20704_(SoulEntity::new, MobCategory.MISC)
            .m_20699_(0.6F, 1.95F)
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .m_20716_()
            .setCustomClientFactory((spawnEntity, level) -> new SoulEntity((EntityType<? extends SoulEntity>)SOUL.get(), level))
            .m_20712_("soul")
   );
   public static final RegistryObject<EntityType<DoppelgangerEntity>> DOPPELGANGER = ENTITIES.register(
      "doppelganger",
      () -> Builder.m_20704_(DoppelgangerEntity::new, MobCategory.MISC)
            .m_20699_(0.6F, 1.8F)
            .m_20716_()
            .m_20719_()
            .setTrackingRange(64)
            .setUpdateInterval(1)
            .setShouldReceiveVelocityUpdates(true)
            .m_20712_("doppelganger")
   );
   public static final RegistryObject<EntityType<JudgementCutEntity>> JUDGEMENT_CUT = ENTITIES.register(
      "judgement_cut",
      () -> Builder.m_20704_(JudgementCutEntity::new, MobCategory.MISC)
            .m_20699_(1.0F, 1.0F)
            .m_20702_(64)
            .m_20717_(1)
            .m_20716_()
            .m_20719_()
            .m_20712_("judgement_cut")
   );
   public static final RegistryObject<EntityType<DMCSummonedSwordEntity>> SUMMONED_SWORD = ENTITIES.register(
      "summoned_sword",
      () -> Builder.m_20704_(DMCSummonedSwordEntity::new, MobCategory.MISC)
            .m_20699_(0.9F, 0.8F)
            .m_20702_(64)
            .m_20717_(1)
            .m_20716_()
            .m_20719_()
            .m_20712_("summoned_sword")
   );
   public static final RegistryObject<EntityType<StormBladesEntity>> STORM_BLADES = ENTITIES.register(
      "storm_blades", () -> Builder.m_20704_(StormBladesEntity::new, MobCategory.MISC).m_20699_(0.1F, 0.1F).m_20716_().m_20712_("storm_blades")
   );
   public static final RegistryObject<EntityType<SpiralBladesEntity>> SPIRAL_BLADES = ENTITIES.register(
      "spiral_blades", () -> Builder.m_20704_(SpiralBladesEntity::new, MobCategory.MISC).m_20699_(0.1F, 0.1F).m_20716_().m_20712_("spiral_blades")
   );
   public static final RegistryObject<EntityType<BlisteringBladesEntity>> BLISTERING_BLADES = ENTITIES.register(
      "blistering_blades", () -> Builder.m_20704_(BlisteringBladesEntity::new, MobCategory.MISC).m_20699_(0.1F, 0.1F).m_20716_().m_20712_("blistering_blades")
   );
   public static final RegistryObject<EntityType<HeavyRainBladesEntity>> HEAVY_RAIN_BLADES = ENTITIES.register(
      "heavy_rain_blades", () -> Builder.m_20704_(HeavyRainBladesEntity::new, MobCategory.MISC).m_20699_(0.1F, 0.1F).m_20716_().m_20712_("heavy_rain_blades")
   );
   public static final RegistryObject<EntityType<ProvocationBladesEntity>> PROVOCATION_BLADES = ENTITIES.register(
      "provocation_blades",
      () -> Builder.m_20704_(ProvocationBladesEntity::new, MobCategory.MISC).m_20699_(0.1F, 0.1F).m_20716_().m_20712_("provocation_blades")
   );
   public static final RegistryObject<EntityType<TripleBladesEntity>> TRIPLE_BLADES = ENTITIES.register(
      "triple_blades", () -> Builder.m_20704_(TripleBladesEntity::new, MobCategory.MISC).m_20699_(0.1F, 0.1F).m_20716_().m_20712_("triple_blades")
   );
   public static final RegistryObject<EntityType<DMCDodgeLocationIndicator>> DMC_DODGELOCATION_INDICATOR = ENTITIES.register(
      "dmc_dodge_location_indicator",
      () -> Builder.m_20704_(DMCDodgeLocationIndicator::new, MobCategory.MISC)
            .m_20699_(0.0F, 0.0F)
            .m_20702_(6)
            .m_20717_(1)
            .m_20698_()
            .m_20716_()
            .m_20712_("dmc_dodge_location_indicator")
   );
   public static final RegistryObject<EntityType<SpineBladeEntity>> SPINE_BLADE = ENTITIES.register(
      "spine_blade",
      () -> Builder.m_20704_(SpineBladeEntity::new, MobCategory.MISC)
            .m_20699_(0.3F, 0.3F)
            .m_20702_(64)
            .m_20717_(1)
            .m_20716_()
            .m_20698_()
            .m_20719_()
            .m_20712_("spine_blade")
   );
   public static final RegistryObject<EntityType<PortalEntity>> PORTAL = ENTITIES.register(
      "portal",
      () -> Builder.m_20704_(PortalEntity::new, MobCategory.MISC)
            .m_20699_(1.0F, 3.0F)
            .m_20702_(128)
            .m_20717_(1)
            .m_20716_()
            .m_20698_()
            .m_20719_()
            .m_20712_("portal")
   );
}
