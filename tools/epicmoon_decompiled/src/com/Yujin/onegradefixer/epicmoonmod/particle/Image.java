package com.Yujin.onegradefixer.epicmoonmod.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.utils.EntitySnapshot;
import yesman.epicfight.api.utils.EntitySnapshot.PlayerSnapshot;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class Image implements ParticleProvider<SimpleParticleType> {
   public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
      Entity entity = level.m_6815_((int)Double.doubleToLongBits(xSpeed));
      if (entity instanceof LivingEntity living) {
         Object snapshot = null;
         if (entity instanceof AbstractClientPlayer clientPlayer) {
            AbstractClientPlayerPatch<?> playerPatch = (AbstractClientPlayerPatch<?>)EpicFightCapabilities.getEntityPatch(
               clientPlayer, AbstractClientPlayerPatch.class
            );
            if (playerPatch != null) {
               snapshot = new PlayerSnapshot(playerPatch);
            }
         }

         if (snapshot == null) {
            LivingEntityPatch<?> patch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(living, LivingEntityPatch.class);
            if (patch == null) {
               return null;
            }

            snapshot = patch.captureEntitySnapshot();
         }

         if (snapshot == null) {
            return null;
         } else {
            ImageParticle particle = new ImageParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, (EntitySnapshot<?>)snapshot);
            particle.m_107257_(5);
            return particle;
         }
      } else {
         return null;
      }
   }
}
