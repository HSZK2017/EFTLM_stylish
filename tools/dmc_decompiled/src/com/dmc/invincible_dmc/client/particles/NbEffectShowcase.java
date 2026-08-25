package com.dmc.invincible_dmc.client.particles;

import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.client.render.afterimage.ClientAfterimageHandler;
import com.dmc.invincible_dmc.client.render.screenshader.BlackWhiteFlashEffect;
import com.dmc.invincible_dmc.client.render.screenshader.ColdGrayEffect;
import com.dmc.invincible_dmc.client.render.screenshader.ColorRadialBlurEffect;
import com.dmc.invincible_dmc.client.render.screenshader.DemonicDomainEffek;
import com.dmc.invincible_dmc.client.render.screenshader.ImpactBlurEffect;
import com.dmc.invincible_dmc.client.render.screenshader.PureChromaticAberrationEffect;
import com.dmc.invincible_dmc.client.render.screenshader.ScreenDistortionEffect;
import com.dmc.invincible_dmc.client.render.screenshader.ScreenFlashEffect;
import com.dmc.invincible_dmc.client.render.screenshader.ScreenVignetteEffect;
import com.dmc.invincible_dmc.entity.DMCEntities;
import com.dmc.invincible_dmc.entity.portal.PortalEntity;
import com.dmc.invincible_dmc.entity.vfx.DMCSlashEffect;
import com.dmc.invincible_dmc.entity.vfx.DMCVoidSlashEffect;
import com.dmc.invincible_dmc.entity.vfx.SlashMotionMode;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.particle.DMCParticles;
import com.dmc.invincible_dmc.utils.vfx.LocalScreenEffectGate;
import com.dmc.invincible_dmc.utils.vfx.RenderUtils;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import mod.chloeprime.aaaparticles.api.common.AAALevel;
import mod.chloeprime.aaaparticles.api.common.ParticleEmitterInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
final class NbEffectShowcase {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int STEPS_PER_TICK = 4;
   private static final String EFFEK_DIRECTORY = "effeks/";
   private static final String EFFEK_EXTENSION = ".efkefc";
   private static final AtomicInteger CLIENT_ENTITY_IDS = new AtomicInteger(Integer.MIN_VALUE);
   private final ClientLevel level;
   private final List<NbEffectShowcase.Step> steps;
   private int cursor;

   private NbEffectShowcase(ClientLevel level, List<NbEffectShowcase.Step> steps) {
      this.level = level;
      this.steps = steps;
   }

   static NbEffectShowcase create(ClientLevel level, Vec3 origin) {
      Minecraft minecraft = Minecraft.m_91087_();
      LocalPlayer player = minecraft.f_91074_;
      float yaw = player == null ? 0.0F : player.m_146908_();
      List<NbEffectShowcase.Step> steps = new ArrayList<>();
      addScreenEffectSteps(steps, level, origin);
      addVisualEntitySteps(steps, level, origin, yaw, player);
      addRegisteredParticleSteps(steps, level, origin, yaw, player);
      addManualParticleSteps(steps, level, origin, yaw, player);
      int effekCount = addEffekseerSteps(steps, level, origin, yaw);
      LOGGER.info("NB particle scheduled {} visual effect steps, including {} Effekseer resources", steps.size(), effekCount);
      return new NbEffectShowcase(level, List.copyOf(steps));
   }

   boolean tick() {
      if (Minecraft.m_91087_().f_91073_ != this.level) {
         return false;
      } else {
         int executed = 0;

         while (this.cursor < this.steps.size() && executed++ < 4) {
            NbEffectShowcase.Step step = this.steps.get(this.cursor++);

            try {
               step.action().run();
            } catch (LinkageError | RuntimeException var4) {
               LOGGER.warn("NB visual effect step '{}' failed; continuing", step.name(), var4);
            }
         }

         return this.cursor < this.steps.size();
      }
   }

   private static void addScreenEffectSteps(List<NbEffectShowcase.Step> steps, ClientLevel level, Vec3 origin) {
      Vec3 center = origin.m_82520_(0.0, 1.0, 0.0);
      addStep(steps, "vix:cold_gray", () -> LocalScreenEffectGate.pushNearby(level, center, 24.0, new ColdGrayEffect(center)));
      addStep(
         steps,
         "vix:black_white_flash",
         () -> LocalScreenEffectGate.pushNearbyAdditive(level, center, 24.0, new BlackWhiteFlashEffect(center, BlackWhiteFlashEffect.ImpactMode.LIGHT))
      );
      addStep(steps, "vix:color_radial_blur", () -> LocalScreenEffectGate.pushNearbyAdditive(level, center, 24.0, new ColorRadialBlurEffect(center)));
      addStep(steps, "vix:impact_blur", () -> LocalScreenEffectGate.pushNearbyAdditive(level, center, 24.0, new ImpactBlurEffect(1.5F, 18)));
      addStep(steps, "vix:chromatic_aberration", () -> LocalScreenEffectGate.pushNearbyAdditive(level, center, 24.0, new PureChromaticAberrationEffect(center)));
      addStep(steps, "vix:screen_distortion", () -> LocalScreenEffectGate.pushNearbyAdditive(level, center, 24.0, new ScreenDistortionEffect(center)));
      addStep(steps, "vix:screen_flash", () -> LocalScreenEffectGate.pushNearbyAdditive(level, center, 24.0, new ScreenFlashEffect()));
      addStep(steps, "vix:screen_vignette", () -> LocalScreenEffectGate.pushNearbyAdditive(level, center, 24.0, new ScreenVignetteEffect()));
      addStep(
         steps,
         "vix:demonic_domain",
         () -> DemonicDomainEffek.playDomain(DemonicDomainEffek.Type.LEVEL1, level, center.f_82479_, center.f_82480_, center.f_82481_, 8.0F)
      );
   }

   private static void addVisualEntitySteps(List<NbEffectShowcase.Step> steps, ClientLevel level, Vec3 origin, float yaw, LocalPlayer player) {
      SlashMotionMode[] modes = SlashMotionMode.values();
      int[] colors = new int[]{4487167, 10841343, 14281983};

      for (int index = 0; index < modes.length; index++) {
         SlashMotionMode mode = modes[index];
         Vec3 position = orientedOffset(origin, yaw, (double)(index - 1) * 2.6, 1.4, 4.0);
         int color = colors[index % colors.length];
         int modeIndex = index;
         addStep(steps, "entity:slash_" + mode.name().toLowerCase(), () -> {
            DMCSlashEffect effect = new DMCSlashEffect((EntityType<?>)DMCEntities.SLASH_EFFECT.get(), level);
            effect.m_6034_(position.f_82479_, position.f_82480_, position.f_82481_);
            effect.m_146922_(yaw + (float)modeIndex * 20.0F);
            effect.setRotationRoll(-25.0F + (float)modeIndex * 25.0F);
            effect.setRotationOffset((float)modeIndex * 18.0F);
            effect.setBaseSize(1.1F);
            effect.setColor(color);
            effect.setMotionMode(mode);
            effect.setLifetime(mode.defaultLifetime());
            addClientEntity(level, effect);
         });
      }

      Vec3 voidSlashPosition = orientedOffset(origin, yaw, 0.0, 2.0, 7.0);
      addStep(steps, "entity:void_slash", () -> {
         DMCVoidSlashEffect effect = new DMCVoidSlashEffect((EntityType<?>)DMCEntities.VOID_SLASH_EFFECT.get(), level);
         effect.m_6034_(voidSlashPosition.f_82479_, voidSlashPosition.f_82480_, voidSlashPosition.f_82481_);
         effect.m_146922_(yaw - 22.0F);
         effect.setRotationRoll(180.0F);
         effect.setBaseSize(1.25F);
         effect.setColor(3234815);
         effect.setLifetime(50);
         if (player != null) {
            effect.setOwner(player);
         }

         addClientEntity(level, effect);
      });
      Vec3 portalPosition = orientedOffset(origin, yaw, 4.2, 0.0, 6.0);
      addStep(steps, "entity:portal", () -> {
         NbEffectShowcase.ShowcasePortalEntity portal = new NbEffectShowcase.ShowcasePortalEntity(level);
         portal.m_6034_(portalPosition.f_82479_, portalPosition.f_82480_, portalPosition.f_82481_);
         portal.m_146922_(yaw);
         portal.setScale(0.8F);
         addClientEntity(level, portal);
      });
   }

   private static void addRegisteredParticleSteps(List<NbEffectShowcase.Step> steps, ClientLevel level, Vec3 origin, float yaw, LocalPlayer player) {
      List<NbEffectShowcase.RegisteredParticle> particles = new ArrayList<>();

      for (RegistryObject<ParticleType<?>> particleObject : DMCParticles.PARTICLES.getEntries()) {
         ParticleType<?> particleType = (ParticleType<?>)particleObject.get();
         if (particleType instanceof SimpleParticleType) {
            SimpleParticleType simpleParticleType = (SimpleParticleType)particleType;
            if (simpleParticleType != DMCParticles.NB.get() && !isContextBoundParticle(simpleParticleType)) {
               particles.add(new NbEffectShowcase.RegisteredParticle(particleObject.getId(), simpleParticleType));
            }
         }
      }

      for (int index = 0; index < particles.size(); index++) {
         NbEffectShowcase.RegisteredParticle particle = particles.get(index);
         Vec3 position = gridPosition(origin, yaw, index, 7, 2.7, 4.5);
         int particleIndex = index;
         addStep(steps, "particle:" + particle.id(), () -> spawnRegisteredParticle(level, player, particle.type(), position, particleIndex));
      }

      addContextBoundParticleSteps(steps, level, player);
   }

   private static void spawnRegisteredParticle(ClientLevel level, LocalPlayer player, SimpleParticleType particleType, Vec3 position, int index) {
      double xSpeed = 0.0;
      double ySpeed = 0.0;
      double zSpeed = 0.0;
      if (player != null) {
         double encodedPlayerId = Double.longBitsToDouble((long)player.m_19879_());
         if (particleType == DMCParticles.TRANSPARENT_AFTER_IMAGE.get()) {
            xSpeed = encodedPlayerId;
         } else if (particleType == DMCParticles.YAMATO_SPHERE.get()
            || particleType == DMCParticles.YAMATO_LAST_SPHERE.get()
            || particleType == DMCParticles.YAMATO_FLOOR.get()) {
            ySpeed = encodedPlayerId;
         } else if (particleType == DMCParticles.VERGIL_SLASH_SEQUENCE.get() || particleType == DMCParticles.VERGIL_SLASH_SEQUENCE_ALT.get()) {
            ySpeed = Math.toRadians((double)player.m_146908_());
         }
      }

      if (particleType == DMCParticles.ATTACK_MAIN.get()
         || particleType == DMCParticles.PARRY_FLASH_MAIN.get()
         || particleType == DMCParticles.ATTACK_MAIN_RENDER.get()
         || particleType == DMCParticles.PARRY_FLASH_MAIN_RENDER.get()) {
         xSpeed = 1.0;
      } else if (particleType == DMCParticles.NORMAL_SPARK.get()
         || particleType == DMCParticles.SPARK_CONTRACTILE.get()
         || particleType == DMCParticles.SPARK_EXPANSIVE.get()) {
         double angle = (double)index * Math.PI * 0.61803398875;
         xSpeed = Math.cos(angle) * 0.18;
         ySpeed = 0.08 + (double)(index % 3) * 0.03;
         zSpeed = Math.sin(angle) * 0.18;
      }

      level.m_7106_(particleType, position.f_82479_, position.f_82480_, position.f_82481_, xSpeed, ySpeed, zSpeed);
   }

   private static void addContextBoundParticleSteps(List<NbEffectShowcase.Step> steps, ClientLevel level, LocalPlayer player) {
      double entityId = 0.0;
      double animationId = 0.0;
      double jointId = 0.0;
      double trailIndex = 0.0;

      try {
         if (player != null) {
            entityId = Double.longBitsToDouble((long)player.m_19879_());
            LivingEntityPatch<?> playerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(player, LivingEntityPatch.class);
            if (playerPatch != null && YamatoAnimations.YAMATO_COMBO_C_START != null) {
               animationId = Double.longBitsToDouble((long)YamatoAnimations.YAMATO_COMBO_C_START.id());
               List<TrailInfo> trailInfos = ((YamatoAttackAnimation)YamatoAnimations.YAMATO_COMBO_C_START.get())
                  .getProperty(ClientAnimationProperties.TRAIL_EFFECT)
                  .orElse(List.of());
               if (!trailInfos.isEmpty()) {
                  Joint joint = playerPatch.getArmature().searchJointByName(trailInfos.get(0).joint());
                  if (joint != null) {
                     jointId = Double.longBitsToDouble((long)joint.getId());
                  }
               }
            }
         }
      } catch (LinkageError | RuntimeException var14) {
         LOGGER.warn("NB could not prepare complete trail context; empty values will be used", var14);
      }

      addEncodedTrailStep(steps, level, "bloom_trail", (SimpleParticleType)DMCParticles.BLOOM_TRAIL.get(), entityId, animationId, jointId, trailIndex);
      addEncodedTrailStep(
         steps, level, "flowing_trail", (SimpleParticleType)DMCParticles.FLOWING_ANIMATION_TRAIL.get(), entityId, animationId, jointId, trailIndex
      );
      addEncodedTrailStep(
         steps, level, "bloom_trail_sword", (SimpleParticleType)DMCParticles.BLOOM_TRAIL_SWORD.get(), entityId, animationId, jointId, trailIndex
      );
      addEncodedTrailStep(steps, level, "static_air_trail", (SimpleParticleType)DMCParticles.STATIC_AIR_TRAIL.get(), entityId, animationId, jointId, trailIndex);
   }

   private static void addEncodedTrailStep(
      List<NbEffectShowcase.Step> steps,
      ClientLevel level,
      String name,
      SimpleParticleType particleType,
      double entityId,
      double animationId,
      double jointId,
      double trailIndex
   ) {
      addStep(steps, "particle:" + name, () -> level.m_7106_(particleType, entityId, 0.0, animationId, jointId, trailIndex, 0.0));
   }

   private static void addManualParticleSteps(List<NbEffectShowcase.Step> steps, ClientLevel level, Vec3 origin, float yaw, LocalPlayer player) {
      Vec3 airWavePosition = orientedOffset(origin, yaw, -3.0, 1.0, 5.5);
      addStep(
         steps,
         "particle:air_wave",
         () -> RenderUtils.AddParticle(level, new AirWaveParticle(level, airWavePosition.f_82479_, airWavePosition.f_82480_, airWavePosition.f_82481_, 1, 8))
      );

      for (int layer = 0; layer < 2; layer++) {
         int currentLayer = layer;
         Vec3 position = orientedOffset(origin, yaw, 2.7, 1.2 + (double)layer * 2.1, 5.5);
         addStep(
            steps,
            "particle:space_broken_layer_" + layer,
            () -> RenderUtils.AddParticle(level, new SpaceBrokenParticle(level, position.f_82479_, position.f_82480_, position.f_82481_, yaw, 40, currentLayer))
         );
      }

      if (player != null) {
         addStep(steps, "particle:sdt_phase2", () -> RenderUtils.AddParticle(level, new SdtPhase2Particle(level, player) {
               private int showcaseAge;

               @Override
               public void m_5989_() {
                  if (++this.showcaseAge >= 60) {
                     this.m_107274_();
                  } else {
                     super.m_5989_();
                  }
               }
            }));
         LivingEntityPatch<?> playerPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(player, LivingEntityPatch.class);
         if (playerPatch != null) {
            addStep(steps, "particle:phantoms", () -> {
               PhantomsParticle particle = new PhantomsParticle(level, origin.f_82479_, origin.f_82480_, origin.f_82481_, playerPatch);
               particle.m_107257_(30);
               RenderUtils.AddParticle(level, particle);
            });
            addStep(steps, "particle:phantoms_return", () -> {
               PhantomsParticle_Return particle = new PhantomsParticle_Return(level, origin.f_82479_, origin.f_82480_, origin.f_82481_, playerPatch);
               particle.m_107257_(30);
               RenderUtils.AddParticle(level, particle);
            });
            if (YamatoAnimations.YAMATO_JUDGEMENT_CUT_END != null) {
               addStep(
                  steps,
                  "particle:static_pose_afterimage",
                  () -> ClientAfterimageHandler.spawnInstantPoseParticle(playerPatch, (StaticAnimation)YamatoAnimations.YAMATO_JUDGEMENT_CUT_END.get(), 1.75F)
               );
            }
         }
      }
   }

   private static int addEffekseerSteps(List<NbEffectShowcase.Step> steps, ClientLevel level, Vec3 origin, float yaw) {
      List<ResourceLocation> effekIds;
      try {
         effekIds = Minecraft.m_91087_()
            .m_91098_()
            .m_214159_("effeks", resource -> resource.m_135827_().equals("invincible_dmc") && resource.m_135815_().endsWith(".efkefc"))
            .keySet()
            .stream()
            .map(NbEffectShowcase::toEffekId)
            .sorted(Comparator.comparing(ResourceLocation::toString))
            .toList();
      } catch (RuntimeException var9) {
         LOGGER.warn("NB could not enumerate Effekseer resources", var9);
         return 0;
      }

      for (int index = 0; index < effekIds.size(); index++) {
         ResourceLocation effekId = effekIds.get(index);
         Vec3 position = gridPosition(origin, yaw, index, 9, 3.2, 7.0);
         float rotation = (float)index * 47.0F % 360.0F;
         addStep(
            steps,
            "effek:" + effekId,
            () -> {
               ParticleEmitterInfo info = ParticleEmitterInfo.create(level, effekId)
                  .position(position.f_82479_, position.f_82480_, position.f_82481_)
                  .rotation(0.0F, rotation, 0.0F)
                  .scale(1.0F);
               AAALevel.addParticle(level, true, info);
            }
         );
      }

      return effekIds.size();
   }

   private static ResourceLocation toEffekId(ResourceLocation resource) {
      String path = resource.m_135815_();
      String effekPath = path.substring("effeks/".length(), path.length() - ".efkefc".length());
      return ResourceLocation.fromNamespaceAndPath(resource.m_135827_(), effekPath);
   }

   private static boolean isContextBoundParticle(SimpleParticleType particleType) {
      return particleType == DMCParticles.BLOOM_TRAIL.get()
         || particleType == DMCParticles.FLOWING_ANIMATION_TRAIL.get()
         || particleType == DMCParticles.BLOOM_TRAIL_SWORD.get()
         || particleType == DMCParticles.STATIC_AIR_TRAIL.get();
   }

   private static Vec3 gridPosition(Vec3 origin, float yaw, int index, int columns, double spacing, double startDistance) {
      int row = index / columns;
      int column = index % columns;
      double right = ((double)column - (double)(columns - 1) * 0.5) * spacing;
      double up = 0.8 + (double)(row % 2) * 0.65;
      double forward = startDistance + (double)row * spacing;
      return orientedOffset(origin, yaw, right, up, forward);
   }

   private static Vec3 orientedOffset(Vec3 origin, float yaw, double rightDistance, double upDistance, double forwardDistance) {
      double radians = Math.toRadians((double)yaw);
      Vec3 forward = new Vec3(-Math.sin(radians), 0.0, Math.cos(radians));
      Vec3 right = new Vec3(forward.f_82481_, 0.0, -forward.f_82479_);
      return origin.m_82549_(right.m_82490_(rightDistance)).m_82520_(0.0, upDistance, 0.0).m_82549_(forward.m_82490_(forwardDistance));
   }

   private static void addStep(List<NbEffectShowcase.Step> steps, String name, Runnable action) {
      steps.add(new NbEffectShowcase.Step(name, action));
   }

   private static void addClientEntity(ClientLevel level, Entity entity) {
      int entityId = CLIENT_ENTITY_IDS.getAndIncrement();
      entity.m_20234_(entityId);
      level.m_104627_(entityId, entity);
   }

   private static record RegisteredParticle(ResourceLocation id, SimpleParticleType type) {
   }

   private static final class ShowcasePortalEntity extends PortalEntity {
      private static final int SHOWCASE_LIFETIME = 60;

      private ShowcasePortalEntity(ClientLevel level) {
         super((EntityType<?>)DMCEntities.PORTAL.get(), level);
      }

      @Override
      public void m_8119_() {
         super.m_8119_();
         if (this.f_19797_ >= 60) {
            this.m_146870_();
         }
      }
   }

   private static record Step(String name, Runnable action) {
   }
}
