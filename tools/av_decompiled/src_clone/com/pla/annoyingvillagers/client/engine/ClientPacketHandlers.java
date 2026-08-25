package com.pla.annoyingvillagers.client.engine;

import com.pla.annoyingvillagers.config.AnnoyingVillagersClientConfig;
import com.pla.annoyingvillagers.event.NoVfxPortalEvent;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.BlackFireSwordItem;
import com.pla.annoyingvillagers.item.EnderGlaiveItem;
import com.pla.annoyingvillagers.network.ClientboundBlackFireFx;
import com.pla.annoyingvillagers.network.ClientboundBlueDemonEffectFx;
import com.pla.annoyingvillagers.network.ClientboundDiamondAttractorFx;
import com.pla.annoyingvillagers.network.ClientboundEliteHerobrineFx;
import com.pla.annoyingvillagers.network.ClientboundEnderAegisSparkFx;
import com.pla.annoyingvillagers.network.ClientboundGlaiveExplosionFx;
import com.pla.annoyingvillagers.network.ClientboundHerobrineAssistanceFx;
import com.pla.annoyingvillagers.network.ClientboundHerobrinePortalFx;
import com.pla.annoyingvillagers.network.ClientboundMuteExplosionAtPos;
import com.pla.annoyingvillagers.network.ClientboundTeleportPortalFx;
import com.pla.annoyingvillagers.network.ClientboundWoopieSwordWindFx;
import com.pla.annoyingvillagers.util.AAAParticlesUtil;
import com.pla.annoyingvillagers.util.ExplosionFxMute;
import com.pla.annoyingvillagers.util.HerobrineUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public final class ClientPacketHandlers {
   private static final DustParticleOptions DIAMOND_GREEN_DUST = new DustParticleOptions(new Vector3f(0.0F, 1.0F, 0.3F), 1.15F);
   private static final DustParticleOptions DIAMOND_GLOW_DUST = new DustParticleOptions(new Vector3f(0.9F, 1.0F, 0.2F), 0.9F);

   private ClientPacketHandlers() {
   }

   private static Vec3 randomUnit(RandomSource rand) {
      double z = rand.m_188500_() * 2.0 - 1.0;
      double a = rand.m_188500_() * Math.PI * 2.0;
      double r = Math.sqrt(Math.max(0.0, 1.0 - z * z));
      return new Vec3(r * Math.cos(a), z, r * Math.sin(a));
   }

   private static void spawnParticle(Level level, ParticleOptions particle, Vec3 pos, Vec3 velocity) {
      level.m_6493_(particle, true, pos.f_82479_, pos.f_82480_, pos.f_82481_, velocity.f_82479_, velocity.f_82480_, velocity.f_82481_);
   }

   private static void spawnSpreadParticle(
      Level level, ParticleOptions particle, Vec3 center, RandomSource rand, double xOffset, double yOffset, double zOffset, double speed
   ) {
      Vec3 pos = center.m_82520_((rand.m_188500_() * 2.0 - 1.0) * xOffset, (rand.m_188500_() * 2.0 - 1.0) * yOffset, (rand.m_188500_() * 2.0 - 1.0) * zOffset);
      Vec3 velocity = speed == 0.0 ? Vec3.f_82478_ : randomUnit(rand).m_82490_(speed);
      spawnParticle(level, particle, pos, velocity);
   }

   private static Vec3 randomRaisedSpread(Vec3 center, RandomSource rand, double xOffset, double minYOffset, double maxYOffset, double zOffset) {
      return center.m_82520_(
         (rand.m_188500_() * 2.0 - 1.0) * xOffset,
         minYOffset + rand.m_188500_() * Math.max(0.0, maxYOffset - minYOffset),
         (rand.m_188500_() * 2.0 - 1.0) * zOffset
      );
   }

   private static String pulseKey(String prefix, int entityId, int tickCount, String suffix) {
      return prefix + ":" + entityId + ":" + tickCount + suffix;
   }

   private static boolean followPhotonEntity(Level level, String effectPath, String key, Entity entity, Vec3 offset, int lifetimeTicks) {
      if (entity != null && !entity.m_213877_()) {
         Vec3 fixedOffset = offset == null ? Vec3.f_82478_ : offset;
         return PhotonClientFxUtil.followPosition(
            key, level, effectPath, () -> entity.m_213877_() ? null : entity.m_20182_().m_82549_(fixedOffset), lifetimeTicks
         );
      } else {
         return false;
      }
   }

   public static void handleGlaiveExplosion(ClientboundGlaiveExplosionFx msg) {
      Level level = Minecraft.m_91087_().f_91073_;
      if (level != null) {
         ClientVfxRouter.run(
            AnnoyingVillagersClientConfig.VfxEffect.GLAIVE_EXPLOSION,
            () -> PhotonClientFxUtil.spawnDirectional(level, "av_explodepurpur", msg.from(), msg.to(), true),
            () -> {
               AAAParticlesUtil.sendEnderGlaiveExplosion(msg.from(), msg.to(), level);
               return true;
            },
            () -> EnderGlaiveItem.spawnExplosionFallback(level, msg.to())
         );
         level.m_7785_(
            msg.from().f_82479_,
            msg.from().f_82480_,
            msg.from().f_82481_,
            (SoundEvent)AnnoyingVillagersModSounds.ENDER_SHOT.get(),
            SoundSource.NEUTRAL,
            1.0F,
            1.0F,
            false
         );
      }
   }

   public static void handleMuteExplosionAtPos(ClientboundMuteExplosionAtPos msg) {
      Level level = Minecraft.m_91087_().f_91073_;
      if (level != null) {
         ExplosionFxMute.mark(msg.pos().m_121878_(), level.m_46467_() + (long)msg.lifetimeTicks());
      }
   }

   public static void handleHerobrinePortalFx(ClientboundHerobrinePortalFx msg) {
      Level level = Minecraft.m_91087_().f_91073_;
      if (level != null) {
         ClientVfxRouter.run(
            AnnoyingVillagersClientConfig.VfxEffect.HEROBRINE_PORTAL,
            () -> PhotonClientFxUtil.spawnAt(level, "normalsummoning", msg.from().m_82520_(0.0, 1.0, 0.0)),
            () -> {
               AAAParticlesUtil.sendHerobrinePortal(level, msg.from().f_82479_, msg.from().f_82480_, msg.from().f_82481_);
               return true;
            },
            () -> NoVfxPortalEvent.spawn(msg.from(), 60)
         );
      }
   }

   public static void handleHerobrineAssistanceFx(ClientboundHerobrineAssistanceFx msg) {
      Level level = Minecraft.m_91087_().f_91073_;
      if (level != null) {
         ClientVfxRouter.run(
            AnnoyingVillagersClientConfig.VfxEffect.HEROBRINE_ASSISTANCE,
            () -> PhotonClientFxUtil.spawnAt(level, "requestingassistance", msg.from().m_82520_(0.0, 1.0, 0.0)),
            () -> {
               AAAParticlesUtil.sendHerobrineAssistance(level, msg.from().f_82479_, msg.from().f_82480_, msg.from().f_82481_);
               return true;
            },
            () -> HerobrineUtil.startHerobrineAssistanceFallback(level, msg.from())
         );
      }
   }

   public static void handleEnderAegisSparkFx(ClientboundEnderAegisSparkFx msg) {
      Level level = Minecraft.m_91087_().f_91073_;
      if (level != null) {
         ClientVfxRouter.run(
            AnnoyingVillagersClientConfig.VfxEffect.ENDER_AEGIS_SPARK,
            () -> PhotonClientFxUtil.spawnDirectional(level, "aegissparks", msg.from(), msg.to(), false),
            () -> {
               RandomSource rand = level.m_213780_();

               for (int i = 0; i < 300; i++) {
                  Vec3 velocity = randomUnit(rand).m_82490_(0.02 + rand.m_188500_() * 0.2);
                  spawnParticle(level, (ParticleOptions)AnnoyingVillagersModParticleTypes.SPARK.get(), msg.to(), velocity);
               }
            }
         );
      }
   }

   public static void handleEliteHerobrineFx(ClientboundEliteHerobrineFx msg) {
      Level level = Minecraft.m_91087_().f_91073_;
      if (level != null) {
         RandomSource rand = level.m_213780_();
         ClientVfxRouter.run(
            AnnoyingVillagersClientConfig.VfxEffect.ELITE_HEROBRINE,
            () -> {
               if (!PhotonClientFxUtil.isLoaded()) {
                  return false;
               } else if (msg.tickCount() % 5 != 0) {
                  return true;
               } else {
                  Entity entity = level.m_6815_(msg.entityId());
                  Vec3 mainOffset = randomRaisedSpread(Vec3.f_82478_, rand, 0.4, 0.65, 1.6, 0.4);
                  boolean handled = followPhotonEntity(
                     level, "reaverlightning", pulseKey("elite-herobrine", msg.entityId(), msg.tickCount(), ":main"), entity, mainOffset, 22
                  );
                  if (!handled) {
                     handled = PhotonClientFxUtil.spawnAt(level, "reaverlightning", msg.pos().m_82549_(mainOffset));
                  }

                  if (msg.extraParticle()) {
                     Vec3 extraOffset = randomRaisedSpread(Vec3.f_82478_, rand, 0.45, 0.75, 2.0, 0.3);
                     boolean extraHandled = followPhotonEntity(
                        level, "reaverlightning", pulseKey("elite-herobrine", msg.entityId(), msg.tickCount(), ":extra"), entity, extraOffset, 22
                     );
                     if (!extraHandled) {
                        extraHandled = PhotonClientFxUtil.spawnAt(level, "reaverlightning", msg.pos().m_82549_(extraOffset));
                     }

                     handled = handled || extraHandled;
                  }

                  return handled;
               }
            },
            () -> {
               spawnSpreadParticle(level, (ParticleOptions)AnnoyingVillagersModParticleTypes.PE.get(), msg.pos(), rand, 0.4, 1.1, 0.4, 0.0);
               if (msg.extraParticle()) {
                  spawnSpreadParticle(level, (ParticleOptions)AnnoyingVillagersModParticleTypes.PE.get(), msg.pos(), rand, 0.45, 1.5, 0.3, 0.0);
               }
            }
         );
      }
   }

   public static void handleBlueDemonEffectFx(ClientboundBlueDemonEffectFx msg) {
      Level level = Minecraft.m_91087_().f_91073_;
      if (level != null) {
         ClientVfxRouter.run(
            AnnoyingVillagersClientConfig.VfxEffect.BLUE_DEMON_LIGHTNING,
            () -> {
               if (!PhotonClientFxUtil.isLoaded()) {
                  return false;
               } else {
                  boolean handled;
                  if (msg.followEntity()) {
                     if (msg.tickCount() % 5 != 0) {
                        return true;
                     }

                     handled = followPhotonEntity(
                        level,
                        "bluedemonlightning",
                        pulseKey("blue-demon-chestplate", msg.entityId(), msg.tickCount(), ""),
                        level.m_6815_(msg.entityId()),
                        Vec3.f_82478_,
                        22
                     );
                     if (!handled) {
                        handled = PhotonClientFxUtil.spawnAt(level, "bluedemonlightning", msg.pos());
                     }
                  } else {
                     handled = PhotonClientFxUtil.spawnAt(level, "bluedemonlightning", msg.pos());
                  }

                  return handled;
               }
            },
            () -> {
               RandomSource rand = level.m_213780_();
               int count = Math.max(1, msg.count());

               for (int i = 0; i < count; i++) {
                  spawnSpreadParticle(
                     level,
                     (ParticleOptions)AnnoyingVillagersModParticleTypes.ELECTRIC_SPARK.get(),
                     msg.pos(),
                     rand,
                     msg.xOffset(),
                     msg.yOffset(),
                     msg.zOffset(),
                     msg.speed()
                  );
               }
            }
         );
      }
   }

   public static void handleTeleportPortalFx(ClientboundTeleportPortalFx msg) {
      Level level = Minecraft.m_91087_().f_91073_;
      if (level != null) {
         ClientVfxRouter.run(
            AnnoyingVillagersClientConfig.VfxEffect.TELEPORT_PORTAL,
            () -> PhotonClientFxUtil.spawnPortal(level, "snakeportal", msg.pos(), msg.normal()),
            () -> AAAParticlesUtil.sendTeleportPortal(level, msg.pos(), msg.normal()),
            () -> {
            }
         );
      }
   }

   public static void handleWoopieSwordWind(ClientboundWoopieSwordWindFx msg) {
      Level level = Minecraft.m_91087_().f_91073_;
      if (level != null) {
         ClientVfxRouter.run(
            AnnoyingVillagersClientConfig.VfxEffect.WOOPIE_SWORD_WIND, () -> PhotonClientFxUtil.spawnAt(level, "whoopiewind", msg.from()), () -> {
               AAAParticlesUtil.sendWoopieWind(level, msg.from().f_82479_, msg.from().f_82480_, msg.from().f_82481_);
               return true;
            }, () -> {
               RandomSource rand = level.m_213780_();
               int rings = 3;
               int pointsPerRing = 36;
               double baseRadius = 0.9;
               double radiusStep = 0.35;
               double baseY = 0.15;
               double yStep = 0.18;
               double tangentialSpeed = 0.14;
               double outwardSpeed = 0.03;

               for (int r = 0; r < rings; r++) {
                  double radius = baseRadius + (double)r * radiusStep;
                  double yOff = baseY + (double)r * yStep;

                  for (int i = 0; i < pointsPerRing; i++) {
                     double a = (double)i / (double)pointsPerRing * Math.PI * 2.0 + rand.m_188500_() * 0.12;
                     double cos = Math.cos(a);
                     double sin = Math.sin(a);
                     double px = msg.from().f_82479_ + cos * radius;
                     double py = msg.from().f_82480_ + yOff + (rand.m_188500_() - 0.5) * 0.06;
                     double pz = msg.from().f_82481_ + sin * radius;
                     double vx = -sin * tangentialSpeed + cos * outwardSpeed;
                     double vy = 0.01 + rand.m_188500_() * 0.02;
                     double vz = cos * tangentialSpeed + sin * outwardSpeed;
                     level.m_6493_(ParticleTypes.f_123796_, true, px, py, pz, vx, vy, vz);
                     if ((i & 3) == 0) {
                        level.m_6493_(ParticleTypes.f_123762_, true, px, py, pz, vx * 0.35, vy * 0.2, vz * 0.35);
                     }
                  }
               }

               for (int ix = 0; ix < 14; ix++) {
                  double vx = (rand.m_188500_() - 0.5) * 0.25;
                  double vy = 0.03 + rand.m_188500_() * 0.18;
                  double vz = (rand.m_188500_() - 0.5) * 0.25;
                  level.m_6493_(ParticleTypes.f_123759_, true, msg.from().f_82479_, msg.from().f_82480_ + 0.25, msg.from().f_82481_, vx, vy, vz);
               }

               level.m_6493_(ParticleTypes.f_123813_, true, msg.from().f_82479_, msg.from().f_82480_ + 0.35, msg.from().f_82481_, 0.0, 0.0, 0.0);
            }
         );
         level.m_7785_(
            msg.from().f_82479_,
            msg.from().f_82480_,
            msg.from().f_82481_,
            (SoundEvent)AnnoyingVillagersModSounds.WOOPIE_WIND.get(),
            SoundSource.NEUTRAL,
            1.0F,
            1.0F,
            false
         );
      }
   }

   public static void handleBlackFire(ClientboundBlackFireFx msg) {
      Level level = Minecraft.m_91087_().f_91073_;
      if (level != null) {
         Entity entity = level.m_6815_(msg.entityId());
         ClientVfxRouter.run(
            AnnoyingVillagersClientConfig.VfxEffect.BLACK_FIRE,
            () -> entity != null && PhotonClientFxUtil.followPosition("blackfire:" + msg.entityId(), level, "blackfire", () -> {
                  Entity current = level.m_6815_(msg.entityId());
                  return current != null && current.m_6084_() && !current.m_213877_() ? BlackFireSwordItem.getBlackFireFallbackPosition(current) : null;
               }, 60),
            () -> {
               if (entity == null) {
                  return false;
               } else {
                  AAAParticlesUtil.sendBlackFire(level, entity);
                  return true;
               }
            },
            () -> BlackFireSwordItem.startBlackFireFallback(level, msg.entityId())
         );
      }
   }

   public static void handleDiamondAttractor(ClientboundDiamondAttractorFx msg) {
      Level level = Minecraft.m_91087_().f_91073_;
      if (level != null) {
         Entity entity = level.m_6815_(msg.entityId());
         ClientVfxRouter.run(
            AnnoyingVillagersClientConfig.VfxEffect.DIAMOND_ATTRACTOR,
            () -> entity != null && PhotonClientFxUtil.spawnAt(level, "av_attractor", BlackFireSwordItem.getSwordOrBodyPosition(entity)),
            () -> {
               if (entity == null) {
                  return false;
               } else {
                  AAAParticlesUtil.sendDiamondAttractor(level, entity);
                  return true;
               }
            },
            () -> {
               if (entity != null) {
                  RandomSource rand = level.m_213780_();
                  Vec3 center = BlackFireSwordItem.getSwordOrBodyPosition(entity);
                  Vec3 forward = entity.m_20154_();
                  if (forward.m_82556_() < 1.0E-6) {
                     forward = new Vec3(0.0, 0.0, 1.0);
                  }

                  forward = forward.m_82541_();
                  Vec3 side = new Vec3(-forward.f_82481_, 0.0, forward.f_82479_);
                  if (side.m_82556_() < 1.0E-6) {
                     side = new Vec3(1.0, 0.0, 0.0);
                  }

                  side = side.m_82541_();
                  Vec3 up = side.m_82537_(forward).m_82541_();

                  for (int i = 0; i < 86; i++) {
                     double progress = (double)i / 85.0;
                     double angle = progress * Math.PI * 4.0 + rand.m_188500_() * 0.18;
                     double radius = 0.16 + Math.sin(progress * Math.PI) * 0.72;
                     Vec3 radial = side.m_82490_(Math.cos(angle)).m_82549_(up.m_82490_(Math.sin(angle)));
                     Vec3 pos = center.m_82549_(forward.m_82490_((progress - 0.5) * 1.25)).m_82549_(radial.m_82490_(radius));
                     Vec3 tangent = forward.m_82537_(radial).m_82541_().m_82490_(0.065 + rand.m_188500_() * 0.04);
                     Vec3 pull = center.m_82546_(pos).m_82541_().m_82490_(0.025);
                     Vec3 velocity = tangent.m_82549_(pull).m_82549_(forward.m_82490_(0.015));
                     spawnParticle(level, rand.m_188499_() ? DIAMOND_GREEN_DUST : DIAMOND_GLOW_DUST, pos, velocity);
                     if ((i & 3) == 0) {
                        spawnParticle(level, ParticleTypes.f_123810_, pos, velocity.m_82490_(0.4));
                     }

                     if (i % 6 == 0) {
                        spawnParticle(level, ParticleTypes.f_175830_, pos, velocity.m_82490_(0.7));
                     }
                  }

                  for (int i = 0; i < 32; i++) {
                     Vec3 offset = randomUnit(rand).m_82490_(0.85 + rand.m_188500_() * 1.25);
                     Vec3 posx = center.m_82549_(offset);
                     Vec3 velocityx = center.m_82546_(posx).m_82541_().m_82490_(0.07 + rand.m_188500_() * 0.05);
                     spawnParticle(level, ParticleTypes.f_123809_, posx, velocityx);
                     if ((i & 1) == 0) {
                        spawnParticle(level, DIAMOND_GREEN_DUST, posx, velocityx);
                     }
                  }
               }
            }
         );
      }
   }
}
