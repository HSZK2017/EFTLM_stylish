package com.dmc.invincible_dmc.client.render.screenshader;

import com.dmc.invincible_dmc.client.domain.DemonicDomainParticle;
import com.dmc.invincible_dmc.client.domain.DemonicDomainRenderer;
import com.dmc.invincible_dmc.client.effeks.EffekConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DemonicDomainEffek {
   public static final ResourceLocation DOMAIN_EFFEK = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "demonic_domain");

   public static void playDomain(DemonicDomainEffek.Type type, Level level, double x, double y, double z, float radius) {
      if (EffekConfig.isEnabled("demonic_domain")) {
         if (level instanceof ClientLevel clientLevel) {
            float scaledRadius = radius / type.intrinsicRadius();
            DemonicDomainRenderer renderer = DemonicDomainRenderer.getInstance();
            renderer.expand(x, y, z, scaledRadius);
            Vec3 center = new Vec3(x, y, z);
            DemonicDomainParticle.spawnRing(clientLevel, center, scaledRadius, 64, 0.0F, 15);
            DemonicDomainParticle.spawnRing(clientLevel, center, scaledRadius * 0.7F, 40, 0.3F, 9);
            DemonicDomainParticle.spawnSphere(clientLevel, center, scaledRadius * 0.9F, (int)(scaledRadius * 6.0F));
         }
      }
   }

   public static record Type(ResourceLocation effekId, float intrinsicRadius) {
      public static final DemonicDomainEffek.Type LEVEL1 = new DemonicDomainEffek.Type(DemonicDomainEffek.DOMAIN_EFFEK, 1.0F);
   }
}
