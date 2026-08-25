package com.dmc.invincible_dmc.client.render.shader;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.io.IOException;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public final class DMCCoreShaders {
   private static ShaderInstance summonedSwordTurbulence;
   private static ShaderInstance summonedSwordBloomMask;
   private static ShaderInstance normalMappedEntity;
   private static ShaderInstance enchantedWeaponOutline;
   private static ShaderInstance enchantedWeaponOutlineMirrored;

   private DMCCoreShaders() {
   }

   @SubscribeEvent
   public static void registerShaders(RegisterShadersEvent event) throws IOException {
      event.registerShader(
         new ShaderInstance(
            event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath("invincible_dmc", "summoned_sword_turbulence"), DefaultVertexFormat.f_85812_
         ),
         shader -> summonedSwordTurbulence = shader
      );
      event.registerShader(
         new ShaderInstance(
            event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath("invincible_dmc", "summoned_sword_bloom_mask"), DefaultVertexFormat.f_85812_
         ),
         shader -> summonedSwordBloomMask = shader
      );
      event.registerShader(
         new ShaderInstance(
            event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath("invincible_dmc", "normal_mapped_entity"), DefaultVertexFormat.f_85812_
         ),
         shader -> normalMappedEntity = shader
      );
      event.registerShader(
         new ShaderInstance(
            event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath("invincible_dmc", "enchanted_weapon_outline"), DefaultVertexFormat.f_85812_
         ),
         shader -> enchantedWeaponOutline = shader
      );
      event.registerShader(
         new ShaderInstance(
            event.getResourceProvider(),
            ResourceLocation.fromNamespaceAndPath("invincible_dmc", "enchanted_weapon_outline_mirrored"),
            DefaultVertexFormat.f_85812_
         ),
         shader -> enchantedWeaponOutlineMirrored = shader
      );
   }

   public static ShaderInstance getSummonedSwordTurbulence() {
      return summonedSwordTurbulence;
   }

   public static ShaderInstance getSummonedSwordBloomMask() {
      return summonedSwordBloomMask;
   }

   public static ShaderInstance getNormalMappedEntity() {
      return normalMappedEntity;
   }

   public static ShaderInstance getEnchantedWeaponOutline() {
      return enchantedWeaponOutline;
   }

   public static ShaderInstance getEnchantedWeaponOutlineMirrored() {
      return enchantedWeaponOutlineMirrored;
   }
}
