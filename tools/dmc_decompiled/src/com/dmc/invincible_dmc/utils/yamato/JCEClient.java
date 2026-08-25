package com.dmc.invincible_dmc.utils.yamato;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.client.config.YamatoClientConfig;
import com.dmc.invincible_dmc.client.effeks.ComboSlashDisorderEffek;
import com.dmc.invincible_dmc.client.effeks.ComboSlashEffek;
import com.dmc.invincible_dmc.client.effeks.ComboSlashStyle1DisorderEffek;
import com.dmc.invincible_dmc.client.effeks.ComboSlashStyle1Effek;
import com.dmc.invincible_dmc.client.effeks.DanceBSlashDisorderEffek;
import com.dmc.invincible_dmc.client.effeks.Dirt_2_Effek;
import com.dmc.invincible_dmc.client.effeks.Disorder2Effek;
import com.dmc.invincible_dmc.client.effeks.DisorderEffek;
import com.dmc.invincible_dmc.client.effeks.Door1Effek;
import com.dmc.invincible_dmc.client.effeks.Door2Effek;
import com.dmc.invincible_dmc.client.effeks.FastSlashDisorderEffek;
import com.dmc.invincible_dmc.client.effeks.FastSlashEffek;
import com.dmc.invincible_dmc.client.effeks.FlashEffek;
import com.dmc.invincible_dmc.client.effeks.FlashSmallEffek;
import com.dmc.invincible_dmc.client.effeks.GroundEffek;
import com.dmc.invincible_dmc.client.effeks.JCE_FireEffek;
import com.dmc.invincible_dmc.client.effeks.JudgementCutEffectBudget;
import com.dmc.invincible_dmc.client.effeks.NormalAdjustSlashDisorderEffek;
import com.dmc.invincible_dmc.client.effeks.NormalAdjustSlashEffek;
import com.dmc.invincible_dmc.client.effeks.NormalSlashDisorderEffek;
import com.dmc.invincible_dmc.client.effeks.NormalSlashEffek;
import com.dmc.invincible_dmc.client.effeks.PowerFloorEffek;
import com.dmc.invincible_dmc.client.effeks.RushSlashEffek;
import com.dmc.invincible_dmc.client.effeks.SDT1ChargeEffek;
import com.dmc.invincible_dmc.client.effeks.SDT1_DoneEffek;
import com.dmc.invincible_dmc.client.effeks.SDT2_DoneEffek;
import com.dmc.invincible_dmc.client.effeks.SDTEffek;
import com.dmc.invincible_dmc.client.effeks.SDTMiniEffek;
import com.dmc.invincible_dmc.client.effeks.SDTOutEffek;
import com.dmc.invincible_dmc.client.effeks.SDT_Fire1Effek;
import com.dmc.invincible_dmc.client.effeks.SDT_Fire2Effek;
import com.dmc.invincible_dmc.client.effeks.SDT_SparkEffek;
import com.dmc.invincible_dmc.client.effeks.SheathEffek;
import com.dmc.invincible_dmc.client.effeks.ShockWaveEffek;
import com.dmc.invincible_dmc.client.effeks.SlowSlashDisorderEffek;
import com.dmc.invincible_dmc.client.effeks.SlowSlashEffek;
import com.dmc.invincible_dmc.client.effeks.SlowerSlashDisorderEffek;
import com.dmc.invincible_dmc.client.effeks.SlowerSlashEffek;
import com.dmc.invincible_dmc.client.effeks.SparkEffek;
import com.dmc.invincible_dmc.client.effeks.Stone_2_Effek;
import com.dmc.invincible_dmc.client.effeks.VoidSlashEffek;
import com.dmc.invincible_dmc.client.particles.PhantomsParticle;
import com.dmc.invincible_dmc.client.particles.PhantomsParticle_Return;
import com.dmc.invincible_dmc.client.particles.SpaceBrokenParticle;
import com.dmc.invincible_dmc.client.particles.VergilSlashSequenceAltParticle;
import com.dmc.invincible_dmc.client.particles.VergilSlashSequenceParticle;
import com.dmc.invincible_dmc.client.particles.YamatoSpaceRiftParticle;
import com.dmc.invincible_dmc.client.render.screenshader.BlackWhiteFlashEffect;
import com.dmc.invincible_dmc.client.render.screenshader.ColdGrayEffect;
import com.dmc.invincible_dmc.client.render.screenshader.ColorRadialBlurEffect;
import com.dmc.invincible_dmc.client.render.screenshader.DemonicDomainEffek;
import com.dmc.invincible_dmc.client.render.screenshader.PureChromaticAberrationEffect;
import com.dmc.invincible_dmc.client.render.screenshader.ScreenDistortionEffect;
import com.dmc.invincible_dmc.client.render.screenshader.ScreenFlashEffect;
import com.dmc.invincible_dmc.client.renderer.SdtWeaponAfterimageManager;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.client.vfx.YamatoTearEffects;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.particle.DMCParticles;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.ConcentrationManager;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.dmc.invincible_dmc.utils.vfx.LocalScreenEffectGate;
import com.guhao.vix.camera.CameraEventsFix;
import com.guhao.vix.camera.VIXCameraFOV;
import com.guhao.vix.camera.VIXCameraShake;
import com.guhao.vix.camera.VIXCameraFOV.Easing;
import com.guhao.vix.util.RenderUtils;
import com.merlin204.avalon.client.CameraShake;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@OnlyIn(Dist.CLIENT)
public class JCEClient {
   private static int uiHideDepth;
   private static boolean restoreUiAfterJce;

   public static void beginUiHide(LivingEntityPatch<?> patch) {
      if ((Boolean)DMConfig.HIDE_UI_DURING_JCE.get() && patch.getOriginal() instanceof Player player && player.m_7578_()) {
         Minecraft minecraft = Minecraft.m_91087_();
         if (uiHideDepth == 0) {
            restoreUiAfterJce = !minecraft.f_91066_.f_92062_;
         }

         uiHideDepth++;
         minecraft.f_91066_.f_92062_ = true;
      }
   }

   public static void endUiHide(LivingEntityPatch<?> patch) {
      if (patch.getOriginal() instanceof Player player && player.m_7578_() && uiHideDepth != 0) {
         uiHideDepth--;
         if (uiHideDepth == 0) {
            if (restoreUiAfterJce) {
               Minecraft.m_91087_().f_91066_.f_92062_ = false;
            }

            restoreUiAfterJce = false;
         }

         return;
      }
   }

   public static void endEffects(LivingEntityPatch<?> patch) {
      JudgementCutEffectBudget.markBurstEnded();
      endUiHide(patch);
   }

   public static void prev(LivingEntityPatch<?> ep) {
      JudgementCutEffectBudget.markBurstStarted();
      Vec3 pos = ((LivingEntity)ep.getOriginal()).m_20182_();
      ep.playSound((SoundEvent)DMCSounds.DMC5_JC0.get(), 1.0F, 1.0F, 1.0F);
      if ((Boolean)DMConfig.YAMATO_JC_PREV_CAMERA_SHAKE.get()) {
         VIXCameraShake.shake(38, 20.0F, ((LivingEntity)ep.getOriginal()).m_20182_(), 64.0F);
      }

      if (ep instanceof LocalPlayerPatch) {
         VIXCameraFOV.pulseZoom(1.5F, 36, 0.15F, Easing.EASE_IN_SINE, Easing.EASE_OUT_CUBIC);
      }

      PowerFloorEffek.playPowerFloor(
         PowerFloorEffek.Type.LEVEL1, ((LivingEntity)ep.getOriginal()).m_9236_(), pos.f_82479_, pos.f_82480_ + 0.1, pos.f_82481_, 1.6F
      );
      DisorderEffek.playDisorder(DisorderEffek.Type.LEVEL1, ((LivingEntity)ep.getOriginal()).m_9236_(), pos.f_82479_, pos.f_82480_ + 0.1, pos.f_82481_, 0.75F);
      Disorder2Effek.playDisorder(
         Disorder2Effek.Type.LEVEL1, ((LivingEntity)ep.getOriginal()).m_9236_(), pos.f_82479_, pos.f_82480_ + 0.1, pos.f_82481_, 0.1875F
      );
      ShockWaveEffek.playShockWave(
         ShockWaveEffek.Type.LEVEL1, ((LivingEntity)ep.getOriginal()).m_9236_(), pos.f_82479_, pos.f_82480_ + 1.15, pos.f_82481_, 1.0F
      );
      GroundEffek.playGround(GroundEffek.Type.LEVEL1, ((LivingEntity)ep.getOriginal()).m_9236_(), pos.f_82479_, pos.f_82480_, pos.f_82481_, 2.4F);
      ((LivingEntity)ep.getOriginal())
         .m_9236_()
         .m_7107_((ParticleOptions)DMCParticles.YAMATO_LAST_SPHERE.get(), pos.f_82479_, pos.f_82480_, pos.f_82481_, 0.0, 0.0, 0.0);
      ((LivingEntity)ep.getOriginal())
         .m_9236_()
         .m_7107_((ParticleOptions)DMCParticles.YAMATO_SPHERE.get(), pos.f_82479_, pos.f_82480_ + 0.05, pos.f_82481_, 0.0, 0.0, 0.0);
      ((LivingEntity)ep.getOriginal())
         .m_9236_()
         .m_7107_((ParticleOptions)DMCParticles.YAMATO_FLOOR.get(), pos.f_82479_, pos.f_82480_ + 0.1, pos.f_82481_, 0.0, 0.0, 0.0);
      ColorRadialBlurEffect blur = new ColorRadialBlurEffect(((LivingEntity)ep.getOriginal()).m_20182_(), 16, 1.0F, 1.0F, 32, 0.5F, 0.5F);
      LocalScreenEffectGate.pushNearby(ep, 48.0, blur);
      DemonicDomainEffek.playDomain(
         DemonicDomainEffek.Type.LEVEL1,
         ((LivingEntity)ep.getOriginal()).m_9236_(),
         ((LivingEntity)ep.getOriginal()).m_20185_(),
         ((LivingEntity)ep.getOriginal()).m_20186_(),
         ((LivingEntity)ep.getOriginal()).m_20189_(),
         16.0F
      );
      if ((Boolean)YamatoClientConfig.JUDGEMENT_CUT_END_CAMERA_ENABLED.get()) {
         CameraEventsFix.SetAnimUntilAnimationInterrupted(
            YamatoAnimations.JUDGEMENT_CUT_END_CAMERA, (LivingEntity)ep.getOriginal(), true, YamatoAnimations.YAMATO_JUDGEMENT_CUT_END
         );
      }
   }

   public static void prev2(LivingEntityPatch<?> ep) {
      JudgementCutEffectBudget.markBurstStarted();
      Vec3 pos = ((LivingEntity)ep.getOriginal()).m_20182_();
      if ((Boolean)DMConfig.YAMATO_JC_PREV2_CAMERA_SHAKE.get()) {
         VIXCameraShake.shake(38, 20.0F, ((LivingEntity)ep.getOriginal()).m_20182_(), 64.0F);
      }

      ep.playSound((SoundEvent)DMCSounds.DMC5_JC0.get(), 1.0F, 1.0F, 1.0F);
      ShockWaveEffek.playShockWave(
         ShockWaveEffek.Type.LEVEL1, ((LivingEntity)ep.getOriginal()).m_9236_(), pos.f_82479_, pos.f_82480_ + 1.15, pos.f_82481_, 1.0F
      );
      ((LivingEntity)ep.getOriginal())
         .m_9236_()
         .m_7107_((ParticleOptions)DMCParticles.YAMATO_SPHERE.get(), pos.f_82479_, pos.f_82480_ + 0.05, pos.f_82481_, 0.0, 0.0, 0.0);
      ((LivingEntity)ep.getOriginal())
         .m_9236_()
         .m_7107_((ParticleOptions)DMCParticles.YAMATO_FLOOR.get(), pos.f_82479_, pos.f_82480_ + 0.1, pos.f_82481_, 0.0, 0.0, 0.0);
   }

   public static void HandleAtk1(LivingEntityPatch<?> ep) {
      Vec3 pos = ((LivingEntity)ep.getOriginal()).m_20182_();
      PhantomsParticle particle = new PhantomsParticle(Minecraft.m_91087_().f_91073_, pos.f_82479_, pos.f_82480_, pos.f_82481_, ep);
      particle.m_107257_(30);
      RenderUtils.AddParticle(Minecraft.m_91087_().f_91073_, particle);
      PhantomsParticle_Return particle2 = new PhantomsParticle_Return(Minecraft.m_91087_().f_91073_, pos.f_82479_, pos.f_82480_, pos.f_82481_, ep);
      particle2.m_107257_(30);
      RenderUtils.AddParticle(Minecraft.m_91087_().f_91073_, particle2);
   }

   public static void post1(LivingEntityPatch<?> ep) {
      ep.playSound((SoundEvent)DMCSounds.DMC5_JC1.get(), 1.0F, 1.0F, 1.0F);
      Level worldIn = ((LivingEntity)ep.getOriginal()).m_9236_();
      Vec3 pos = ((LivingEntity)ep.getOriginal()).m_20182_();
      YamatoTearEffects.playJudgementCutEnd(worldIn, pos);
      if (ep instanceof LocalPlayerPatch) {
         VIXCameraFOV.pulseZoom(1.86F, 40, 0.1F, Easing.EASE_IN_QUAD, Easing.EASE_OUT_CUBIC);
      }

      worldIn.m_7106_((ParticleOptions)DMCParticles.JUDGEMENT_CUT_PARTICLE.get(), pos.f_82479_, pos.f_82480_, pos.f_82481_, 0.0, 0.0, 0.0);
      CameraShake.shake(400, 12.0F, 20.0F, ((LivingEntity)ep.getOriginal()).m_20182_(), 32.0F);
      ScreenDistortionEffect distortion = new ScreenDistortionEffect(((LivingEntity)ep.getOriginal()).m_20182_(), 7, 0.6F, 16.0F, 5.1F);
      LocalScreenEffectGate.pushNearbyAdditive(ep, 48.0, distortion);
      ColdGrayEffect coldGray = new ColdGrayEffect(pos);
      LocalScreenEffectGate.pushNearby(ep, 48.0, coldGray);
      BlackWhiteFlashEffect effect2 = new BlackWhiteFlashEffect(pos, BlackWhiteFlashEffect.ImpactMode.LIGHT);
      LocalScreenEffectGate.pushNearbyAdditive(ep, 48.0, effect2);
      PureChromaticAberrationEffect ca = new PureChromaticAberrationEffect(pos, 76, 0.7F, 0.5F, 0.5F);
      LocalScreenEffectGate.pushNearbyAdditive(ep, 48.0, ca);
   }

   public static void post2(LivingEntityPatch<?> ep) {
      Level worldIn = ((LivingEntity)ep.getOriginal()).m_9236_();
      Vec3 pos = ((LivingEntity)ep.getOriginal()).m_20182_();
      float baseYaw = ((LivingEntity)ep.getOriginal()).f_20883_;
      ClientLevel cl = (ClientLevel)worldIn;
      Random random = new Random();
      RenderUtils.AddParticle(cl, new SpaceBrokenParticle(cl, pos.f_82479_, pos.f_82480_, pos.f_82481_, baseYaw, 59, 0));
      RenderUtils.AddParticle(cl, new SpaceBrokenParticle(cl, pos.f_82479_, pos.f_82480_ + 4.0, pos.f_82481_, baseYaw, 59, 1));
      int ringCount = 6;

      for (int i = 0; i < ringCount; i++) {
         float angle = (float)((double)i * Math.PI * 2.0 / (double)ringCount + (double)random.nextFloat(-0.4F, 0.4F));
         float dist = 3.0F + random.nextFloat(0.0F, 3.5F);
         double px = pos.f_82479_ + (double)dist * Math.sin((double)angle);
         double pz = pos.f_82481_ + (double)dist * Math.cos((double)angle);
         double py = pos.f_82480_ + (double)random.nextFloat(-1.5F, 3.0F);
         int lyr = random.nextInt(2);
         int life = 59;
         RenderUtils.AddParticle(cl, new SpaceBrokenParticle(cl, px, py, pz, baseYaw, life, lyr));
      }
   }

   public static void post3(LivingEntityPatch<?> ep) {
      ScreenFlashEffect flash = new ScreenFlashEffect(8, 4.5F, 2.0F);
      LocalScreenEffectGate.pushNearbyAdditive(ep, 48.0, flash);
      ColorRadialBlurEffect blur = new ColorRadialBlurEffect(((LivingEntity)ep.getOriginal()).m_20182_(), 9, 0.8F, 1.0F, 16, 0.5F, 0.5F);
      LocalScreenEffectGate.pushNearbyAdditive(ep, 48.0, blur);
      ep.playSound((SoundEvent)DMCSounds.DMC5_JC2.get(), 1.0F, 1.0F, 1.0F);
      ClientLevel level = (ClientLevel)((LivingEntity)ep.getOriginal()).m_9236_();
      LivingEntity entity = (LivingEntity)ep.getOriginal();
      BlockPos centerPos = entity.m_20183_();
      int radius = 4;
      int particleCountPerBlock = 1;

      for (int x = -radius; x <= radius; x++) {
         for (int z = -radius; z <= radius; z++) {
            BlockPos pos = centerPos.m_7918_(x, -1, z);
            BlockState state = level.m_8055_(pos);
            if (!state.m_60795_() && !state.m_60713_(Blocks.f_50110_)) {
               for (int i = 0; i < particleCountPerBlock; i++) {
                  double px = (double)pos.m_123341_() + level.f_46441_.m_188500_();
                  double py = (double)pos.m_123342_() + 1.0;
                  double pz = (double)pos.m_123343_() + level.f_46441_.m_188500_();
                  BlockParticleOption option = new BlockParticleOption(ParticleTypes.f_123794_, state);
                  Particle particle = Minecraft.m_91087_()
                     .f_91061_
                     .m_107370_(
                        option,
                        px,
                        py,
                        pz,
                        (level.f_46441_.m_188500_() - 0.5) * 0.36,
                        level.f_46441_.m_188500_() * 0.5 + 0.2,
                        (level.f_46441_.m_188500_() - 0.5) * 0.36
                     );
                  if (particle != null) {
                     particle.m_107257_(40 + level.f_46441_.m_188503_(30));
                     if (particle instanceof TerrainParticle terrainParticle) {
                        terrainParticle.f_107226_ = 0.5F;
                     }
                  }
               }
            }
         }
      }

      if ((Boolean)DMConfig.YAMATO_JC_EXECUTION_CAMERA_SHAKE.get()) {
         VIXCameraShake.shake(100, 46.0F, ((LivingEntity)ep.getOriginal()).m_20182_(), 12.0F);
      }

      Stone_2_Effek.playStone_2(
         Stone_2_Effek.Type.LEVEL1,
         ((LivingEntity)ep.getOriginal()).f_19853_,
         ((LivingEntity)ep.getOriginal()).m_20185_(),
         ((LivingEntity)ep.getOriginal()).m_20186_(),
         ((LivingEntity)ep.getOriginal()).m_20189_(),
         1.0F
      );
      SparkEffek.playSpark(
         SparkEffek.Type.LEVEL2,
         ((LivingEntity)ep.getOriginal()).f_19853_,
         ((LivingEntity)ep.getOriginal()).m_20185_(),
         ((LivingEntity)ep.getOriginal()).m_20186_(),
         ((LivingEntity)ep.getOriginal()).m_20189_(),
         1.0F
      );
      SDT_SparkEffek.playSDT_Spark(SDT_SparkEffek.Type.LEVEL1, ((LivingEntity)ep.getOriginal()).m_9236_(), 0.0, 0.6F, 0.0, 1.0F, ep.getOriginal());
      Dirt_2_Effek.playDirt_2(
         Dirt_2_Effek.Type.LEVEL1,
         ((LivingEntity)ep.getOriginal()).f_19853_,
         ((LivingEntity)ep.getOriginal()).m_20185_(),
         ((LivingEntity)ep.getOriginal()).m_20186_(),
         ((LivingEntity)ep.getOriginal()).m_20189_(),
         1.0F
      );
   }

   public static void HandleAtk2(LivingEntityPatch<?> ep) {
      Level level = ((LivingEntity)ep.getOriginal()).m_9236_();
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(ep);
      if (player != null) {
         float prevElapsedTime = player.getPrevElapsedTime();
         float elapsedTime = player.getElapsedTime();
         float step = (elapsedTime - prevElapsedTime) / 10.0F;
         Vec3f pointOffset = Vec3f.fromDoubleVector(Vec3.f_82478_);
         Vec3 worldPos = AvalonAnimationUtils.getJointWorldRawPos(ep, ((HumanoidArmature)Armatures.BIPED.get()).toolL, step, pointOffset);
         FlashEffek.playFlash(FlashEffek.Type.LEVEL1, level, worldPos.m_7096_(), worldPos.m_7098_(), worldPos.m_7094_(), 1.5F);
      }
   }

   public static void tryRestoreLock(LivingEntityPatch<?> ep) {
      CameraLockUtil.stepSequenceToLock();
   }

   public static void JC(LivingEntityPatch<?> ep) {
      Level level = ((LivingEntity)ep.getOriginal()).m_9236_();
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(ep);
      if (player != null) {
         float elapsedTime = player.getElapsedTime();
         Vec3f pointOffset = Vec3f.fromDoubleVector(Vec3.f_82478_);
         Vec3 worldPos = AvalonAnimationUtils.getJointWorldPos(ep, ((HumanoidArmature)Armatures.BIPED.get()).toolL, pointOffset, elapsedTime);
         FlashSmallEffek.playFlashSmall(FlashSmallEffek.Type.LEVEL1, level, worldPos.m_7096_(), worldPos.m_7098_(), worldPos.m_7094_(), 0.8F);
         ep.playSound((SoundEvent)DMCSounds.JUDGEMENT_CUT_SWING.get(), 1.5F, 1.0F, 1.0F);
      }
   }

   public static void post4(LivingEntityPatch<?> ep) {
      ep.playSound((SoundEvent)DMCSounds.DMC5_JC3.get(), 0.35F, 1.0F, 1.0F);
   }

   public static void JCEFire(LivingEntityPatch<?> ep) {
      SDT_Fire1Effek.playSDT_Fire1(SDT_Fire1Effek.Type.LEVEL1, ((LivingEntity)ep.getOriginal()).m_9236_(), 0.0, 0.0, 0.0, 0.8F, ep.getOriginal());
      SDT_Fire2Effek.playSDT_Fire2(
         SDT_Fire2Effek.Type.LEVEL1,
         ((LivingEntity)ep.getOriginal()).m_9236_(),
         ((LivingEntity)ep.getOriginal()).m_20185_(),
         ((LivingEntity)ep.getOriginal()).m_20186_(),
         ((LivingEntity)ep.getOriginal()).m_20189_(),
         0.8F
      );
      JCE_FireEffek.playJCE_Fire(JCE_FireEffek.Type.LEVEL1, ((LivingEntity)ep.getOriginal()).m_9236_(), 0.0, 0.0, 0.0, 0.8F, ep.getOriginal());
   }

   public static void ComboCSlash(LivingEntityPatch<?> ep) {
      Random random = new Random();
      ComboCSlash(ep, Math.toDegrees((double)random.nextFloat((float) (-Math.PI / 2), (float) (Math.PI / 2))));
   }

   public static void ComboCSlash(LivingEntityPatch<?> ep, double rotationZDegrees) {
      ComboCSlash(ep, rotationZDegrees, 0.37F);
   }

   private static void ComboCSlash(LivingEntityPatch<?> ep, double rotationZDegrees, float size) {
      Level level = ((LivingEntity)ep.getOriginal()).m_9236_();
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(ep);
      if (player != null) {
         Vec3f pointOffset = Vec3f.fromDoubleVector(Vec3.f_82478_);
         Vec3 worldPos = AvalonAnimationUtils.getJointWorldPos(ep, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, pointOffset);
         LivingEntity original = (LivingEntity)ep.getOriginal();
         Vec3 hLook = MathUtils.getVectorForRotation(0.0F, ep.getYRot());
         double fx = worldPos.m_7096_() + hLook.f_82479_ * 1.1;
         double fy = worldPos.m_7098_() + 0.5;
         double fz = worldPos.m_7094_() + hLook.f_82481_ * 1.1;
         float rotationY = (float)((double)getRY(ep) + (Math.PI / 2));
         float rotationZ = (float)Math.toRadians(rotationZDegrees);
         boolean disorder = useDisorder(ep);
         switch ((YamatoClientConfig.ComboSlashStyle)YamatoClientConfig.COMBO_SLASH_STYLE.get()) {
            case COMBO_SLASH:
               if (disorder) {
                  ComboSlashDisorderEffek.playSlash(ComboSlashDisorderEffek.Type.LEVEL1, level, fx, fy, fz, 0.0F, rotationY, rotationZ, size);
               } else {
                  ComboSlashEffek.playSlash(ComboSlashEffek.Type.LEVEL1, level, fx, fy, fz, 0.0F, rotationY, rotationZ, size);
               }
               break;
            case COMBO_SLASH_STYLE_1:
               if (disorder) {
                  ComboSlashStyle1DisorderEffek.playSlash(ComboSlashStyle1DisorderEffek.Type.LEVEL1, level, fx, fy, fz, 0.0F, rotationY, rotationZ, size);
               } else {
                  ComboSlashStyle1Effek.playSlash(ComboSlashStyle1Effek.Type.LEVEL1, level, fx, fy, fz, 0.0F, rotationY, rotationZ, size);
               }
         }
      }
   }

   public static void ComboCSlash(LivingEntityPatch<?> ep, boolean additionalSlash) {
      Random random = new Random();
      ComboCSlash(ep, additionalSlash, Math.toDegrees((double)random.nextFloat((float) (-Math.PI / 2), (float) (Math.PI / 2))));
   }

   public static void ComboCSlash(LivingEntityPatch<?> ep, boolean additionalSlash, double rotationZDegrees) {
      if (!additionalSlash || YamatoClientConfig.COMBO_SLASH_STYLE.get() != YamatoClientConfig.ComboSlashStyle.COMBO_SLASH) {
         ComboCSlash(ep, rotationZDegrees, 0.37F);
      }
   }

   public static void sheathSlash(
      LivingEntityPatch<?> ep, double rollZ, float size, double rollX, double heightOffset, double forwardOffset, double strafeOffset
   ) {
      Level level = ((LivingEntity)ep.getOriginal()).m_9236_();
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(ep);
      if (player != null) {
         LivingEntity original = (LivingEntity)ep.getOriginal();
         Vec3 hLook = MathUtils.getVectorForRotation(0.0F, ep.getYRot());
         Vec3 hRight = new Vec3(-hLook.f_82481_, 0.0, hLook.f_82479_);
         float boostedSize = size + getSizeBoost(ep);
         SheathEffek.playSlash(
            SheathEffek.Type.LEVEL1,
            level,
            original.m_20185_() + hLook.f_82479_ * forwardOffset + hRight.f_82479_ * strafeOffset,
            original.m_20186_() + (double)original.m_20192_() * 0.75 + heightOffset,
            original.m_20189_() + hLook.f_82481_ * forwardOffset + hRight.f_82481_ * strafeOffset,
            (float)Math.toRadians(rollX),
            (float)((double)getRY(ep) + (Math.PI / 2)),
            (float)Math.toRadians(rollZ),
            boostedSize
         );
      }
   }

   public static void FastSlash(LivingEntityPatch<?> ep, double rollZ, float size, double rollX, double heightOffset, double forwardOffset, double strafeOffset) {
      Level level = ((LivingEntity)ep.getOriginal()).m_9236_();
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(ep);
      if (player != null) {
         LivingEntity original = (LivingEntity)ep.getOriginal();
         Vec3 hLook = MathUtils.getVectorForRotation(0.0F, ep.getYRot());
         Vec3 hRight = new Vec3(-hLook.f_82481_, 0.0, hLook.f_82479_);
         float boostedSize = size + getSizeBoost(ep);
         if (useDisorder(ep)) {
            FastSlashDisorderEffek.playSlash(
               FastSlashDisorderEffek.Type.LEVEL1,
               level,
               original.m_20185_() + hLook.f_82479_ * forwardOffset + hRight.f_82479_ * strafeOffset,
               original.m_20186_() + (double)original.m_20192_() * 0.75 + heightOffset,
               original.m_20189_() + hLook.f_82481_ * forwardOffset + hRight.f_82481_ * strafeOffset,
               (float)Math.toRadians(rollX),
               (float)((double)getRY(ep) + (Math.PI / 2)),
               (float)Math.toRadians(rollZ),
               boostedSize
            );
         } else {
            FastSlashEffek.playSlash(
               FastSlashEffek.Type.LEVEL1,
               level,
               original.m_20185_() + hLook.f_82479_ * forwardOffset + hRight.f_82479_ * strafeOffset,
               original.m_20186_() + (double)original.m_20192_() * 0.75 + heightOffset,
               original.m_20189_() + hLook.f_82481_ * forwardOffset + hRight.f_82481_ * strafeOffset,
               (float)Math.toRadians(rollX),
               (float)((double)getRY(ep) + (Math.PI / 2)),
               (float)Math.toRadians(rollZ),
               boostedSize
            );
         }
      }
   }

   public static void VergilSlashSequence(
      LivingEntityPatch<?> ep, double rollZ, float size, double rollX, double heightOffset, double forwardOffset, double strafeOffset
   ) {
      VergilSlashSequence(ep, rollZ, size, rollX, 0.0, heightOffset, forwardOffset, strafeOffset);
   }

   public static void VergilSlashSequence(
      LivingEntityPatch<?> ep, double rollZ, float size, double rollX, double rollY, double heightOffset, double forwardOffset, double strafeOffset
   ) {
      spawnVergilSlashSequence(ep, false, rollX, rollY, rollZ, size, heightOffset, forwardOffset, strafeOffset);
   }

   public static void VergilSlashSequenceAlt(
      LivingEntityPatch<?> ep, double rollZ, float size, double rollX, double heightOffset, double forwardOffset, double strafeOffset
   ) {
      VergilSlashSequenceAlt(ep, rollZ, size, rollX, 0.0, heightOffset, forwardOffset, strafeOffset);
   }

   public static void VergilSlashSequenceAlt(
      LivingEntityPatch<?> ep, double rollZ, float size, double rollX, double rollY, double heightOffset, double forwardOffset, double strafeOffset
   ) {
      spawnVergilSlashSequence(ep, true, rollX, rollY, rollZ, size, heightOffset, forwardOffset, strafeOffset);
   }

   private static void spawnVergilSlashSequence(
      LivingEntityPatch<?> ep,
      boolean alternate,
      double rollX,
      double rollY,
      double rollZ,
      float size,
      double heightOffset,
      double forwardOffset,
      double strafeOffset
   ) {
      if (((LivingEntity)ep.getOriginal()).m_9236_() instanceof ClientLevel level) {
         LivingEntity var29 = (LivingEntity)ep.getOriginal();
         Vec3 horizontalLook = MathUtils.getVectorForRotation(0.0F, ep.getYRot() + 90.0F);
         Vec3 horizontalRight = new Vec3(-horizontalLook.f_82481_, 0.0, horizontalLook.f_82479_);
         double x = var29.m_20185_() + horizontalLook.f_82479_ * forwardOffset + horizontalRight.f_82479_ * strafeOffset;
         double y = var29.m_20186_() + (double)var29.m_20192_() * 0.75 + heightOffset;
         double z = var29.m_20189_() + horizontalLook.f_82481_ * forwardOffset + horizontalRight.f_82481_ * strafeOffset;
         float pitchRadians = (float)Math.toRadians(rollX);
         float yawRadians = (float)((double)getRY(ep) + (Math.PI / 2) + Math.toRadians(rollY));
         float rollRadians = (float)Math.toRadians(rollZ);
         Particle particle = alternate
            ? VergilSlashSequenceAltParticle.create(level, x, y, z, pitchRadians, yawRadians, rollRadians, size)
            : VergilSlashSequenceParticle.create(level, x, y, z, pitchRadians, yawRadians, rollRadians, size);
         if (particle != null) {
            com.dmc.invincible_dmc.utils.vfx.RenderUtils.AddParticle(level, particle);
         }
      }
   }

   public static void FastSlashRush(
      LivingEntityPatch<?> ep, double rollZ, float size, double rollX, double heightOffset, double forwardOffset, double strafeOffset
   ) {
      Level level = ((LivingEntity)ep.getOriginal()).m_9236_();
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(ep);
      if (player != null) {
         LivingEntity original = (LivingEntity)ep.getOriginal();
         Vec3 hLook = MathUtils.getVectorForRotation(0.0F, ep.getYRot());
         Vec3 hRight = new Vec3(-hLook.f_82481_, 0.0, hLook.f_82479_);
         float boostedSize = size + getSizeBoost(ep);
         RushSlashEffek.playSlash(
            RushSlashEffek.Type.LEVEL1,
            level,
            original.m_20185_() + hLook.f_82479_ * forwardOffset + hRight.f_82479_ * strafeOffset,
            original.m_20186_() + (double)original.m_20192_() * 0.75 + heightOffset,
            original.m_20189_() + hLook.f_82481_ * forwardOffset + hRight.f_82481_ * strafeOffset,
            (float)Math.toRadians(rollX),
            (float)((double)getRY(ep) + (Math.PI / 2)),
            (float)Math.toRadians(rollZ),
            boostedSize
         );
      }
   }

   public static void FastSlashRound(
      LivingEntityPatch<?> ep, double rollZ, float size, double rollX, double heightOffset, double forwardOffset, double strafeOffset
   ) {
      Level level = ((LivingEntity)ep.getOriginal()).m_9236_();
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(ep);
      if (player != null) {
         LivingEntity original = (LivingEntity)ep.getOriginal();
         Vec3 hLook = MathUtils.getVectorForRotation(0.0F, ep.getYRot());
         Vec3 hRight = new Vec3(-hLook.f_82481_, 0.0, hLook.f_82479_);
         float boostedSize = size + getSizeBoost(ep);
         if (useDisorder(ep)) {
            DanceBSlashDisorderEffek.playSlash(
               DanceBSlashDisorderEffek.Type.LEVEL1,
               level,
               original.m_20185_() + hLook.f_82479_ * forwardOffset + hRight.f_82479_ * strafeOffset,
               original.m_20186_() + (double)original.m_20192_() * 0.75 + heightOffset,
               original.m_20189_() + hLook.f_82481_ * forwardOffset + hRight.f_82481_ * strafeOffset,
               (float)Math.toRadians(rollX),
               (float)((double)getRY(ep) + (Math.PI / 2)),
               (float)Math.toRadians(rollZ),
               boostedSize
            );
         } else {
            RushSlashEffek.playSlash(
               RushSlashEffek.Type.LEVEL1,
               level,
               original.m_20185_() + hLook.f_82479_ * forwardOffset + hRight.f_82479_ * strafeOffset,
               original.m_20186_() + (double)original.m_20192_() * 0.75 + heightOffset,
               original.m_20189_() + hLook.f_82481_ * forwardOffset + hRight.f_82481_ * strafeOffset,
               (float)Math.toRadians(rollX),
               (float)((double)getRY(ep) + (Math.PI / 2)),
               (float)Math.toRadians(rollZ),
               boostedSize
            );
         }
      }
   }

   public static void FastSlashAnti(
      LivingEntityPatch<?> ep, double rollZ, float size, double rollX, double heightOffset, double forwardOffset, double strafeOffset
   ) {
      Level level = ((LivingEntity)ep.getOriginal()).m_9236_();
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(ep);
      if (player != null) {
         LivingEntity original = (LivingEntity)ep.getOriginal();
         Vec3 hLook = MathUtils.getVectorForRotation(0.0F, -ep.getYRot());
         Vec3 hRight = new Vec3(-hLook.f_82481_, 0.0, hLook.f_82479_);
         float boostedSize = size + getSizeBoost(ep);
         if (useDisorder(ep)) {
            FastSlashDisorderEffek.playSlash(
               FastSlashDisorderEffek.Type.LEVEL1,
               level,
               original.m_20185_() + hLook.f_82479_ * forwardOffset + hRight.f_82479_ * strafeOffset,
               original.m_20186_() + (double)original.m_20192_() * 0.75 + heightOffset,
               original.m_20189_() + hLook.f_82481_ * forwardOffset + hRight.f_82481_ * strafeOffset,
               (float)Math.toRadians(rollX),
               (float)((double)getRY(ep) + (Math.PI / 2)),
               (float)Math.toRadians(rollZ),
               boostedSize
            );
         } else {
            FastSlashEffek.playSlash(
               FastSlashEffek.Type.LEVEL1,
               level,
               original.m_20185_() + hLook.f_82479_ * forwardOffset + hRight.f_82479_ * strafeOffset,
               original.m_20186_() + (double)original.m_20192_() * 0.75 + heightOffset,
               original.m_20189_() + hLook.f_82481_ * forwardOffset + hRight.f_82481_ * strafeOffset,
               (float)Math.toRadians(rollX),
               (float)((double)getRY(ep) + (Math.PI / 2)),
               (float)Math.toRadians(rollZ),
               boostedSize
            );
         }
      }
   }

   public static void FastSlash(LivingEntityPatch<?> ep, double rollZ, float size, double rollX) {
      FastSlash(ep, rollZ, size, rollX, 0.0, 0.0, 0.0);
   }

   public static void FastSlash(LivingEntityPatch<?> ep, double rollZ, float size) {
      FastSlash(ep, rollZ, size, 0.0);
   }

   public static void Slash(LivingEntityPatch<?> ep, double rollZ, float size, double rollX, double heightOffset, double forwardOffset, double strafeOffset) {
      Level level = ((LivingEntity)ep.getOriginal()).m_9236_();
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(ep);
      if (player != null) {
         LivingEntity original = (LivingEntity)ep.getOriginal();
         Vec3 hLook = MathUtils.getVectorForRotation(0.0F, ep.getYRot());
         Vec3 hRight = new Vec3(-hLook.f_82481_, 0.0, hLook.f_82479_);
         float boostedSize = size + getSizeBoost(ep);
         if (useDisorder(ep)) {
            NormalSlashDisorderEffek.playSlash(
               NormalSlashDisorderEffek.Type.LEVEL1,
               level,
               original.m_20185_() + hLook.f_82479_ * forwardOffset + hRight.f_82479_ * strafeOffset,
               original.m_20186_() + (double)original.m_20192_() * 0.75 + heightOffset,
               original.m_20189_() + hLook.f_82481_ * forwardOffset + hRight.f_82481_ * strafeOffset,
               (float)Math.toRadians(rollX),
               (float)((double)getRY(ep) + (Math.PI / 2)),
               (float)Math.toRadians(rollZ),
               boostedSize
            );
         } else {
            NormalSlashEffek.playSlash(
               NormalSlashEffek.Type.LEVEL1,
               level,
               original.m_20185_() + hLook.f_82479_ * forwardOffset + hRight.f_82479_ * strafeOffset,
               original.m_20186_() + (double)original.m_20192_() * 0.75 + heightOffset,
               original.m_20189_() + hLook.f_82481_ * forwardOffset + hRight.f_82481_ * strafeOffset,
               (float)Math.toRadians(rollX),
               (float)((double)getRY(ep) + (Math.PI / 2)),
               (float)Math.toRadians(rollZ),
               boostedSize
            );
         }
      }
   }

   public static void Slash(LivingEntityPatch<?> ep, double rollZ, float size, double rollX) {
      Slash(ep, rollZ, size, rollX, 0.0, 0.0, 0.0);
   }

   public static void SlashAdjust(LivingEntityPatch<?> ep, double rollZ, float size, double rollX, float yOffset, double forwardOffset, double strafeOffset) {
      Level level = ((LivingEntity)ep.getOriginal()).m_9236_();
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(ep);
      if (player != null) {
         LivingEntity original = (LivingEntity)ep.getOriginal();
         Vec3 hLook = MathUtils.getVectorForRotation(0.0F, ep.getYRot());
         Vec3 hRight = new Vec3(-hLook.f_82481_, 0.0, hLook.f_82479_);
         float boostedSize = size + getSizeBoost(ep);
         if (useDisorder(ep)) {
            NormalAdjustSlashDisorderEffek.playSlash(
               NormalAdjustSlashDisorderEffek.Type.LEVEL1,
               level,
               original.m_20185_() + hLook.f_82479_ * forwardOffset + hRight.f_82479_ * strafeOffset,
               original.m_20186_() + (double)original.m_20192_() * 0.75 + (double)yOffset,
               original.m_20189_() + hLook.f_82481_ * forwardOffset + hRight.f_82481_ * strafeOffset,
               (float)Math.toRadians(rollX),
               (float)((double)getRY(ep) + (Math.PI / 2)),
               (float)Math.toRadians(rollZ),
               boostedSize
            );
         } else {
            NormalAdjustSlashEffek.playSlash(
               NormalAdjustSlashEffek.Type.LEVEL1,
               level,
               original.m_20185_() + hLook.f_82479_ * forwardOffset + hRight.f_82479_ * strafeOffset,
               original.m_20186_() + (double)original.m_20192_() * 0.75 + (double)yOffset,
               original.m_20189_() + hLook.f_82481_ * forwardOffset + hRight.f_82481_ * strafeOffset,
               (float)Math.toRadians(rollX),
               (float)((double)getRY(ep) + (Math.PI / 2)),
               (float)Math.toRadians(rollZ),
               boostedSize
            );
         }
      }
   }

   public static void SlowSlashAnti(
      LivingEntityPatch<?> ep, double rollZ, float size, double rollX, double heightOffset, double forwardOffset, double strafeOffset
   ) {
      Level level = ((LivingEntity)ep.getOriginal()).m_9236_();
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(ep);
      if (player != null) {
         LivingEntity original = (LivingEntity)ep.getOriginal();
         Vec3 hLook = MathUtils.getVectorForRotation(0.0F, ep.getYRot());
         Vec3 hRight = new Vec3(-hLook.f_82481_, 0.0, hLook.f_82479_);
         if (useDisorder(ep)) {
            SlowSlashDisorderEffek.playSlash(
               SlowSlashDisorderEffek.Type.LEVEL1,
               level,
               original.m_20185_() + hLook.f_82479_ * forwardOffset + hRight.f_82479_ * strafeOffset,
               original.m_20186_() + (double)original.m_20192_() * 0.75 + heightOffset,
               original.m_20189_() + hLook.f_82481_ * forwardOffset + hRight.f_82481_ * strafeOffset,
               (float)Math.toRadians(rollX),
               (float)(-((double)getRY(ep) + (Math.PI / 2))),
               (float)Math.toRadians(rollZ),
               size
            );
         } else {
            SlowSlashEffek.playSlash(
               SlowSlashEffek.Type.LEVEL1,
               level,
               original.m_20185_() + hLook.f_82479_ * forwardOffset + hRight.f_82479_ * strafeOffset,
               original.m_20186_() + (double)original.m_20192_() * 0.75 + heightOffset,
               original.m_20189_() + hLook.f_82481_ * forwardOffset + hRight.f_82481_ * strafeOffset,
               (float)Math.toRadians(rollX),
               (float)(-((double)getRY(ep) + (Math.PI / 2))),
               (float)Math.toRadians(rollZ),
               size
            );
         }
      }
   }

   public static void SlowerSlash(
      LivingEntityPatch<?> ep, double rollZ, float size, double rollX, double heightOffset, double forwardOffset, double strafeOffset
   ) {
      Level level = ((LivingEntity)ep.getOriginal()).m_9236_();
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(ep);
      if (player != null) {
         LivingEntity original = (LivingEntity)ep.getOriginal();
         Vec3 hLook = MathUtils.getVectorForRotation(0.0F, ep.getYRot());
         Vec3 hRight = new Vec3(-hLook.f_82481_, 0.0, hLook.f_82479_);
         if (useDisorder(ep)) {
            SlowerSlashDisorderEffek.playSlash(
               SlowerSlashDisorderEffek.Type.LEVEL1,
               level,
               original.m_20185_() + hLook.f_82479_ * forwardOffset + hRight.f_82479_ * strafeOffset,
               original.m_20186_() + (double)original.m_20192_() * 0.75 + heightOffset,
               original.m_20189_() + hLook.f_82481_ * forwardOffset + hRight.f_82481_ * strafeOffset,
               (float)Math.toRadians(rollX),
               (float)((double)getRY(ep) + (Math.PI / 2)),
               (float)Math.toRadians(rollZ),
               size
            );
         } else {
            SlowerSlashEffek.playSlash(
               SlowerSlashEffek.Type.LEVEL1,
               level,
               original.m_20185_() + hLook.f_82479_ * forwardOffset + hRight.f_82479_ * strafeOffset,
               original.m_20186_() + (double)original.m_20192_() * 0.75 + heightOffset,
               original.m_20189_() + hLook.f_82481_ * forwardOffset + hRight.f_82481_ * strafeOffset,
               (float)Math.toRadians(rollX),
               (float)((double)getRY(ep) + (Math.PI / 2)),
               (float)Math.toRadians(rollZ),
               size
            );
         }
      }
   }

   public static void SlowSlash(LivingEntityPatch<?> ep, double rollZ, float size, double rollX, double heightOffset, double forwardOffset, double strafeOffset) {
      Level level = ((LivingEntity)ep.getOriginal()).m_9236_();
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(ep);
      if (player != null) {
         LivingEntity original = (LivingEntity)ep.getOriginal();
         Vec3 hLook = MathUtils.getVectorForRotation(0.0F, ep.getYRot());
         Vec3 hRight = new Vec3(-hLook.f_82481_, 0.0, hLook.f_82479_);
         if (useDisorder(ep)) {
            SlowSlashDisorderEffek.playSlash(
               SlowSlashDisorderEffek.Type.LEVEL1,
               level,
               original.m_20185_() + hLook.f_82479_ * forwardOffset + hRight.f_82479_ * strafeOffset,
               original.m_20186_() + (double)original.m_20192_() * 0.75 + heightOffset,
               original.m_20189_() + hLook.f_82481_ * forwardOffset + hRight.f_82481_ * strafeOffset,
               (float)Math.toRadians(rollX),
               (float)((double)getRY(ep) + (Math.PI / 2)),
               (float)Math.toRadians(rollZ),
               size
            );
         } else {
            SlowSlashEffek.playSlash(
               SlowSlashEffek.Type.LEVEL1,
               level,
               original.m_20185_() + hLook.f_82479_ * forwardOffset + hRight.f_82479_ * strafeOffset,
               original.m_20186_() + (double)original.m_20192_() * 0.75 + heightOffset,
               original.m_20189_() + hLook.f_82481_ * forwardOffset + hRight.f_82481_ * strafeOffset,
               (float)Math.toRadians(rollX),
               (float)((double)getRY(ep) + (Math.PI / 2)),
               (float)Math.toRadians(rollZ),
               size
            );
         }
      }
   }

   public static void SlowSlash(LivingEntityPatch<?> ep, double rollZ, float size, double rollX) {
      SlowSlash(ep, rollZ, size, rollX, 0.0, 0.0, 0.0);
   }

   public static void Slash(LivingEntityPatch<?> ep, double rollZ, float size) {
      Slash(ep, rollZ, size, 0.0);
   }

   public static void SlashGround(LivingEntityPatch<?> ep, double rollZ, float size, float groundOffsetY) {
      Level level = ((LivingEntity)ep.getOriginal()).m_9236_();
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(ep);
      if (player != null) {
         LivingEntity original = (LivingEntity)ep.getOriginal();
         Vec3 ground = TeleportGroundUtils.getGroundPosition(ep, groundOffsetY, null);
         float boostedSize = size + getSizeBoost(ep);
         if (useDisorder(ep)) {
            NormalSlashDisorderEffek.playSlash(
               NormalSlashDisorderEffek.Type.LEVEL1,
               level,
               ground.f_82479_,
               ground.f_82480_ + (double)original.m_20192_() * 0.75,
               ground.f_82481_,
               (float)Math.toRadians(0.0),
               (float)((double)getRY(ep) + (Math.PI / 2)),
               (float)Math.toRadians(rollZ),
               boostedSize
            );
         } else {
            NormalSlashEffek.playSlash(
               NormalSlashEffek.Type.LEVEL1,
               level,
               ground.f_82479_,
               ground.f_82480_ + (double)original.m_20192_() * 0.75,
               ground.f_82481_,
               (float)Math.toRadians(0.0),
               (float)((double)getRY(ep) + (Math.PI / 2)),
               (float)Math.toRadians(rollZ),
               boostedSize
            );
         }
      }
   }

   public static void SlashAdjust(LivingEntityPatch<?> ep, double rollZ, float size) {
      SlashAdjust(ep, rollZ, size, 0.0, 0.0F, 0.0, 0.0);
   }

   public static void SlowSlash(LivingEntityPatch<?> ep, double rollZ, float size) {
      SlowSlash(ep, rollZ, size, 0.0);
   }

   public static void VoidSlash(LivingEntityPatch<?> ep) {
      Level level = ((LivingEntity)ep.getOriginal()).m_9236_();
      AnimationPlayer player = DMCAnimationUtils.getMainPlayer(ep);
      if (player != null) {
         Vec3f pointOffset = Vec3f.fromDoubleVector(Vec3.f_82478_);
         Vec3 worldPos = AvalonAnimationUtils.getJointWorldPos(ep, ((HumanoidArmature)Armatures.BIPED.get()).rootJoint, pointOffset);
         LivingEntity original = (LivingEntity)ep.getOriginal();
         Vec3 hLook = MathUtils.getVectorForRotation(0.0F, ep.getYRot());
         double fx = worldPos.m_7096_() + hLook.f_82479_ * 0.65;
         double fy = worldPos.m_7098_() + 0.5;
         double fz = worldPos.m_7094_() + hLook.f_82481_ * 0.65;
         VoidSlashEffek.playSlash(VoidSlashEffek.Type.LEVEL1, level, fx, fy, fz, 0.0F, (float)((double)getRY(ep) + (Math.PI / 2)), (float) (Math.PI / 2), 0.6F);
      }
   }

   public static void ComboA5SpaceRift(LivingEntityPatch<?> patch) {
      YamatoSpaceRiftParticle.spawn(patch);
   }

   public static void VoidSlashSpaceRift(LivingEntityPatch<?> patch) {
      YamatoSpaceRiftParticle.spawnHorizontal(patch);
   }

   public static void Door1(LivingEntityPatch<?> patch, float rz) {
      Door1Effek.playDoor(
         Door1Effek.Type.LEVEL1,
         ((LivingEntity)patch.getOriginal()).m_9236_(),
         ((LivingEntity)patch.getOriginal()).m_20185_(),
         ((LivingEntity)patch.getOriginal()).m_20188_() - 0.5,
         ((LivingEntity)patch.getOriginal()).m_20189_(),
         0.0F,
         (float)((double)getRY(patch) + (Math.PI / 2)),
         rz,
         0.125F
      );
   }

   public static void Door2(LivingEntityPatch<?> patch, float rz) {
      Door2Effek.playDoor(
         Door2Effek.Type.LEVEL1,
         ((LivingEntity)patch.getOriginal()).m_9236_(),
         ((LivingEntity)patch.getOriginal()).m_20185_(),
         ((LivingEntity)patch.getOriginal()).m_20188_() - 0.5,
         ((LivingEntity)patch.getOriginal()).m_20189_(),
         0.0F,
         (float)((double)getRY(patch) + (Math.PI / 2)),
         rz,
         0.125F
      );
   }

   private static boolean useDisorder(LivingEntityPatch<?> ep) {
      if (ep instanceof PlayerPatch<?> pp) {
         SkillContainer sc = pp.getSkill(SkillSlots.WEAPON_INNATE);
         if (sc != null && !sc.isEmpty()) {
            return ConcentrationManager.getConcentrationTier(sc) == 0;
         }
      }

      if (ep instanceof DoppelgangerPatch dp) {
         LivingEntityPatch<?> lpp = dp.getOwnerPatch();
         if (lpp instanceof PlayerPatch<?> ppx) {
            SkillContainer sc = ppx.getSkill(SkillSlots.WEAPON_INNATE);
            if (sc != null && !sc.isEmpty()) {
               return ConcentrationManager.getConcentrationTier(sc) == 0;
            }
         }
      }

      return false;
   }

   private static float getSizeBoost(LivingEntityPatch<?> ep) {
      if (ep instanceof PlayerPatch<?> pp) {
         SkillContainer sc = pp.getSkill(SkillSlots.WEAPON_INNATE);
         if (sc != null && !sc.isEmpty()) {
            int tier = ConcentrationManager.getConcentrationTier(sc);
            if (tier == 1) {
               return 0.03F;
            }

            if (tier == 2) {
               return 0.06F;
            }
         }
      }

      if (ep instanceof DoppelgangerPatch dp) {
         LivingEntityPatch<?> lpp = dp.getOwnerPatch();
         if (lpp instanceof PlayerPatch<?> ppx) {
            SkillContainer sc = ppx.getSkill(SkillSlots.WEAPON_INNATE);
            if (sc != null && !sc.isEmpty()) {
               int tierx = ConcentrationManager.getConcentrationTier(sc);
               if (tierx == 1) {
                  return 0.03F;
               }

               if (tierx == 2) {
                  return 0.06F;
               }
            }
         }
      }

      return 0.06F;
   }

   public static float getRY(LivingEntityPatch<?> livingEntityPatch) {
      float yRot = livingEntityPatch.getYRot();
      float converted = -yRot - 90.0F;
      converted %= 360.0F;
      if (converted < 0.0F) {
         converted += 360.0F;
      }

      return (float)Math.toRadians((double)converted);
   }

   public static void onSDTEnterClient(Player player) {
      PlayerPatch<?> pp = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
      if (pp != null) {
         boolean isActionAnim = DMCAnimationUtils.isRealAnimationType(pp, ActionAnimation.class);
         if (!isActionAnim) {
            SDTEffek.playSDT(SDTEffek.Type.LEVEL1, player.m_9236_(), player.m_20185_(), player.m_20186_() + 0.1, player.m_20189_(), 1.8F);
            if ((Boolean)DMConfig.YAMATO_SDT_CAMERA_SHAKE.get()) {
               VIXCameraShake.shake(240, 14.0F, player.f_19825_, 64.0F);
            }

            if (player.m_7578_()) {
               VIXCameraFOV.pulseZoom(1.4F, 18, 0.1F, Easing.EASE_IN_BACK, Easing.EASE_OUT_CUBIC);
            }

            player.m_9236_()
               .m_7107_((ParticleOptions)DMCParticles.YAMATO_SPHERE.get(), player.m_20185_(), player.m_20186_() + 0.05, player.m_20189_(), 0.0, 0.0, 0.0);
            player.m_9236_()
               .m_7107_((ParticleOptions)DMCParticles.YAMATO_FLOOR.get(), player.m_20185_(), player.m_20186_() + 0.1, player.m_20189_(), 0.0, 0.0, 0.0);
            ColorRadialBlurEffect blur = new ColorRadialBlurEffect(player.m_20182_(), 12, 0.5F, 1.0F, 8, 0.5F, 0.5F);
            LocalScreenEffectGate.pushNearby(player, 48.0, blur);
         } else {
            SDTMiniEffek.playSDT(SDTMiniEffek.Type.LEVEL1, player.m_9236_(), player.m_20185_(), player.m_20186_() + 0.1, player.m_20189_(), 1.2F);
         }

         SDT_SparkEffek.playSDT_Spark(SDT_SparkEffek.Type.LEVEL1, player.m_9236_(), 0.0, 0.6F, 0.0, 0.75F, player);
      }
   }

   public static void onSDTExitClient(Player player) {
      SDTOutEffek.playSDTOUT(SDTOutEffek.Type.LEVEL1, player.m_9236_(), 0.0, 0.1, 0.0, 0.5F, player);
      SDT_SparkEffek.playSDT_Spark(SDT_SparkEffek.Type.LEVEL1, player.m_9236_(), 0.0, 0.5, 0.0, 0.25F, player);
   }

   public static void onSdtCharge1TickClient(Player player) {
      SDT1ChargeEffek.playSDT1Charge(SDT1ChargeEffek.Type.LEVEL1, player.m_9236_(), 0.0, 1.0, 0.0, 0.36F, player);
   }

   public static void onSdtCharge2TickClient(Player player) {
   }

   public static void onSdtCharge1CompleteClient(Player player) {
      SDT1_DoneEffek.playSDT1Done(SDT1_DoneEffek.Type.LEVEL1, player.m_9236_(), 0.0, 1.0, 0.0, 1.0F, player);
      SdtWeaponAfterimageManager.triggerAfterimage(player.m_20148_());
   }

   public static void onSdtCharge2CompleteClient(Player player) {
      SDT2_DoneEffek.playSDT2Done(SDT2_DoneEffek.Type.LEVEL1, player.m_9236_(), 0.0, 1.0, 0.0, 1.0F, player);
   }

   public static void onSdtActivatedClient(Player player) {
   }

   public static void onSdtActiveTickClient(Player player) {
      PlayerPatch<?> lep = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
      if (lep != null && !DMCAnimationUtils.isPlaying(lep, YamatoAnimations.YAMATO_JUDGEMENT_CUT_END, YamatoAnimations.YAMATO_JUDGEMENT_CUT_END_INSTANT)) {
         Random random = new Random();
         if (random.nextInt(4) < 1) {
            SDT_SparkEffek.playSDT_Spark(SDT_SparkEffek.Type.LEVEL1, player.m_9236_(), 0.0, 0.6F, 0.0, 0.333F, player);
         }

         SDT_Fire1Effek.playSDT_Fire1(SDT_Fire1Effek.Type.LEVEL1, player.m_9236_(), 0.0, 0.0, 0.0, 0.4F, player);
         SDT_Fire2Effek.playSDT_Fire2(SDT_Fire2Effek.Type.LEVEL1, player.m_9236_(), player.m_20185_(), player.m_20186_(), player.m_20189_(), 0.4F);
      }
   }

   private static float getSdtValue(Player player) {
      PlayerPatch patch = (PlayerPatch)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
      if (patch == null) {
         return 0.0F;
      } else {
         SkillContainer c = patch.getSkill(SkillSlots.WEAPON_INNATE);
         if (c != null && !c.isEmpty()) {
            SkillDataManager dm = c.getDataManager();
            SkillDataKey<Float> key = (SkillDataKey<Float>)DMCSkillDataKeys.SDT_VALUE.get();
            return dm.hasData(key) ? (Float)dm.getDataValue(key) : 0.0F;
         } else {
            return 0.0F;
         }
      }
   }

   public static Vec3 getJointWorldPos(LivingEntityPatch<?> entityPatch, Joint joint, Vec3f offset) {
      LivingEntity entity = (LivingEntity)entityPatch.getOriginal();
      Pose pose = DMCAnimationUtils.getMainPlayer(entityPatch).getCurrentPose(entityPatch, Minecraft.m_91087_().m_91296_());
      OpenMatrix4f transformMatrix = entityPatch.getArmature().getBoundTransformFor(pose, joint);
      transformMatrix.translate(offset);
      OpenMatrix4f rotation = new OpenMatrix4f().rotate(-((float)Math.toRadians((double)(entityPatch.getYRot() + 180.0F))), new Vec3f(0.0F, 1.0F, 0.0F));
      OpenMatrix4f.mul(rotation, transformMatrix, transformMatrix);
      return new Vec3(
         (double)(transformMatrix.m30 + (float)entity.m_20185_()),
         (double)(transformMatrix.m31 + (float)entity.m_20186_()),
         (double)(transformMatrix.m32 + (float)entity.m_20189_())
      );
   }
}
