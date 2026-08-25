package com.dmc.invincible_dmc.client.sound;

import com.dmc.invincible_dmc.utils.DMCLog;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.MOD
)
public class DMCSounds {
   public static final DeferredRegister<SoundEvent> INVINCIBLE_SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, "invincible_dmc");
   public static final RegistryObject<SoundEvent> PARRY = registerSound("parry");
   public static final RegistryObject<SoundEvent> DODGE = registerSound("dodge");
   public static final RegistryObject<SoundEvent> DODGE_EX = registerSound("dodge_ex");
   public static final RegistryObject<SoundEvent> DOPPELGANGER_OPEN = registerSound("doppelganger_open");
   public static final RegistryObject<SoundEvent> DOPPELGANGER_CLOSE = registerSound("doppelganger_close");
   public static final RegistryObject<SoundEvent> DOPPELGANGER_SWITCH = registerSound("doppelganger_switch");
   public static final RegistryObject<SoundEvent> SHEATH = registerSound("sheath");
   public static final RegistryObject<SoundEvent> SHEATH_LIGHT = registerSound("sheath_light");
   public static final RegistryObject<SoundEvent> SHEATH_HEAVY = registerSound("sheath_heavy");
   public static final RegistryObject<SoundEvent> DMC5_JC0 = registerSound("dmc5_jc0");
   public static final RegistryObject<SoundEvent> DMC5_JC1 = registerSound("dmc5_jc1");
   public static final RegistryObject<SoundEvent> DMC5_JC2 = registerSound("dmc5_jc2");
   public static final RegistryObject<SoundEvent> DMC5_JC3 = registerSound("dmc5_jc3");
   public static final RegistryObject<SoundEvent> JUDGEMENT_CUT = registerSound("judgement_cut");
   public static final RegistryObject<SoundEvent> JUDGEMENT_CUT_SWING = registerSound("judgement_cut_swing");
   public static final RegistryObject<SoundEvent> JUDGEMENT_CUT_JUST = registerSound("judgement_cut_just");
   public static final RegistryObject<SoundEvent> JUDGEMENT_CUT_CHARGED = registerSound("judgement_cut_charged");
   public static final RegistryObject<SoundEvent> JUDGEMENT_CUT_BEGIN = registerSound("judgement_cut_begin");
   public static final RegistryObject<SoundEvent> SHEATH_ATTACK1 = registerSound("sheath_attack1");
   public static final RegistryObject<SoundEvent> SHEATH_ATTACK2 = registerSound("sheath_attack2");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_1 = registerSound("yamato_swing_1");
   public static final RegistryObject<SoundEvent> YAMATO_VOIDSLASH_1 = registerSound("yamato_voidslash_1");
   public static final RegistryObject<SoundEvent> YAMATO_VOIDSLASH_2 = registerSound("yamato_voidslash_2");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_2 = registerSound("yamato_swing_2");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_3 = registerSound("yamato_swing_3");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_4 = registerSound("yamato_swing_4");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_5 = registerSound("yamato_swing_5");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_6 = registerSound("yamato_swing_6");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_7 = registerSound("yamato_swing_7");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_8 = registerSound("yamato_swing_8");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_9 = registerSound("yamato_swing_9");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_10 = registerSound("yamato_swing_10");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_11 = registerSound("yamato_swing_11");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_12 = registerSound("yamato_swing_12");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_13 = registerSound("yamato_swing_13");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_14 = registerSound("yamato_swing_14");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_15 = registerSound("yamato_swing_15");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_16 = registerSound("yamato_swing_16");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_17 = registerSound("yamato_swing_17");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_18 = registerSound("yamato_swing_18");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_19 = registerSound("yamato_swing_19");
   public static final RegistryObject<SoundEvent> YAMATO_SWING_20 = registerSound("yamato_swing_20");
   public static final RegistryObject<SoundEvent> DOOR1 = registerSound("door1");
   public static final RegistryObject<SoundEvent> DOOR2 = registerSound("door2");
   public static final RegistryObject<SoundEvent> SDT_DONE = registerSound("sdt_done");
   public static final RegistryObject<SoundEvent> SDT1_DONE = registerSound("sdt1_done");
   public static final RegistryObject<SoundEvent> SDT2_DONE = registerSound("sdt2_done");
   public static final RegistryObject<SoundEvent> SDT1_CHARGE = registerSound("sdt1_charge");
   public static final RegistryObject<SoundEvent> SDT2_CHARGE = registerSound("sdt2_charge");
   public static final RegistryObject<SoundEvent> SDT_OUT = registerSound("sdt_out");
   public static final RegistryObject<SoundEvent> NOSOUND = registerSound("nosound");
   public static final RegistryObject<SoundEvent> SUMMONED_SWORD = registerSound("summoned_sword");
   public static final RegistryObject<SoundEvent> SUMMONED_SWORD_ARRAY = registerSound("summoned_sword_array");
   public static final RegistryObject<SoundEvent> SUMMONED_SWORD_HEAVY_RAIN = registerSound("summoned_sword_heavy_rain");
   public static final RegistryObject<SoundEvent> SUMMONED_SWORD_SPIRAL = registerSound("summoned_sword_spiral");
   public static final RegistryObject<SoundEvent> SUMMONED_SWORD_BREAK = registerSound("summoned_sword_break");
   public static final RegistryObject<SoundEvent> SUMMONED_SWORD_BLISTER = registerSound("summoned_sword_blister");
   public static final RegistryObject<SoundEvent> SUMMONED_SWORD_SHOOT = registerSound("summoned_sword_shoot");
   public static final RegistryObject<SoundEvent> WHOOSH_LIGHT_1 = registerSound("whoosh_light_1");
   public static final RegistryObject<SoundEvent> WHOOSH_LIGHT_2 = registerSound("whoosh_light_2");
   public static final RegistryObject<SoundEvent> WHOOSH_LIGHT_3 = registerSound("whoosh_light_3");
   public static final RegistryObject<SoundEvent> WHOOSH_LIGHT_4 = registerSound("whoosh_light_4");
   public static final RegistryObject<SoundEvent> WHOOSH_HEAVY_1 = registerSound("whoosh_heavy_1");
   public static final RegistryObject<SoundEvent> WHOOSH_HEAVY_2 = registerSound("whoosh_heavy_2");
   public static final RegistryObject<SoundEvent> WHOOSH_HEAVY_3 = registerSound("whoosh_heavy_3");
   public static final RegistryObject<SoundEvent> WHOOSH_HEAVY_4 = registerSound("whoosh_heavy_4");
   public static final RegistryObject<SoundEvent> YAMATO = registerSound("yamato");

   private static RegistryObject<SoundEvent> registerSound(String name) {
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath("invincible_dmc", name);
      DMCLog.debug(DMCLog.Category.NETWORK, "[InvincibleSounds] Registering sound: {}", id);
      return INVINCIBLE_SOUNDS.register(name, () -> SoundEvent.m_262824_(id));
   }

   static {
      try {
         IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
         DMCLog.info(
            DMCLog.Category.NETWORK,
            "[InvincibleSounds] Registering {} sounds via DeferredRegister (modid={})",
            INVINCIBLE_SOUNDS.getEntries().size(),
            "invincible_dmc"
         );
         INVINCIBLE_SOUNDS.register(bus);
         DMCLog.info(DMCLog.Category.NETWORK, "[InvincibleSounds] Registration complete. Count={}", INVINCIBLE_SOUNDS.getEntries().size());
      } catch (Exception var1) {
         DMCLog.error(DMCLog.Category.NETWORK, "[InvincibleSounds] CRITICAL: Failed to register sounds!", var1);
      }
   }
}
