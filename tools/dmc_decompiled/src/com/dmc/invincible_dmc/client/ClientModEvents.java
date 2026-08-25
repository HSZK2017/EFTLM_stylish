package com.dmc.invincible_dmc.client;

import com.dmc.invincible_dmc.InvincibleMod_DMC;
import com.dmc.invincible_dmc.client.camera.YamatoRapidSlashCameraController;
import com.dmc.invincible_dmc.client.dimension.VoidColorGradeRenderer;
import com.dmc.invincible_dmc.client.dimension.VoidDimensionEffects;
import com.dmc.invincible_dmc.client.domain.DemonicDomainRenderer;
import com.dmc.invincible_dmc.client.effeks.EffekBloomPostProcessing;
import com.dmc.invincible_dmc.client.effeks.EffekChromaticAberrationPostProcessing;
import com.dmc.invincible_dmc.client.gui.vergilstatus.SDTScreenOverlay;
import com.dmc.invincible_dmc.client.gui.vergilstatus.VergilStatusOverlay;
import com.dmc.invincible_dmc.client.input.ComboEngineDebugHUD;
import com.dmc.invincible_dmc.client.input.DMComboEngine;
import com.dmc.invincible_dmc.client.input.judegementCut.debug.JudgementCutDebugHUD;
import com.dmc.invincible_dmc.client.model.ACGModel;
import com.dmc.invincible_dmc.client.particles.AttackMainParticle;
import com.dmc.invincible_dmc.client.particles.AttackMainRenderParticle;
import com.dmc.invincible_dmc.client.particles.ColorShaderParticle;
import com.dmc.invincible_dmc.client.particles.JudgementCutEndDMC4Particle;
import com.dmc.invincible_dmc.client.particles.JudgementCutParticle;
import com.dmc.invincible_dmc.client.particles.JudgementCutSequenceDMC4Particle;
import com.dmc.invincible_dmc.client.particles.JudgementCutSequenceNormalDMC4Particle;
import com.dmc.invincible_dmc.client.particles.JudgementCutSequenceNormalParticle;
import com.dmc.invincible_dmc.client.particles.JudgementCutSequenceParticle;
import com.dmc.invincible_dmc.client.particles.NbParticle;
import com.dmc.invincible_dmc.client.particles.NullParticle;
import com.dmc.invincible_dmc.client.particles.SdtPhase2ChromaticParticle;
import com.dmc.invincible_dmc.client.particles.TransparentEntityAfterImageParticle;
import com.dmc.invincible_dmc.client.particles.VergilSlashSequenceAltParticle;
import com.dmc.invincible_dmc.client.particles.VergilSlashSequenceParticle;
import com.dmc.invincible_dmc.client.particles.YamatoFloor;
import com.dmc.invincible_dmc.client.particles.YamatoLastSphere;
import com.dmc.invincible_dmc.client.particles.YamatoSphere;
import com.dmc.invincible_dmc.client.particles.parryflash.ParryFlashMainParticle;
import com.dmc.invincible_dmc.client.particles.parryflash.ParryFlashMainRenderParticle;
import com.dmc.invincible_dmc.client.particles.portal.PortalParticle;
import com.dmc.invincible_dmc.client.particles.portal.ProceduralEndPortalParticle;
import com.dmc.invincible_dmc.client.particles.spark.AllSpark;
import com.dmc.invincible_dmc.client.particles.spark.SparkParticle;
import com.dmc.invincible_dmc.client.particles.trail.BloomTrailParticle;
import com.dmc.invincible_dmc.client.particles.trail.FlowingAnimationTrailParticle;
import com.dmc.invincible_dmc.client.particles.trail.StaticAirTrailParticle;
import com.dmc.invincible_dmc.client.particles.trail.SummonedSwordTrailParticle;
import com.dmc.invincible_dmc.client.render.PostPasses;
import com.dmc.invincible_dmc.client.render.custom.BloomParticleRenderType;
import com.dmc.invincible_dmc.client.render.custom.SummonedSwordBloomPipeline;
import com.dmc.invincible_dmc.client.renderer.RenderYamato;
import com.dmc.invincible_dmc.client.renderer.entity.DMCSlashEffectRenderer;
import com.dmc.invincible_dmc.client.renderer.entity.DoppelgangerRenderer;
import com.dmc.invincible_dmc.client.renderer.entity.DummyRenderer;
import com.dmc.invincible_dmc.client.renderer.entity.SoulRenderer;
import com.dmc.invincible_dmc.client.renderer.patched.entity.PDoppelgangerRenderer;
import com.dmc.invincible_dmc.client.renderer.patched.entity.PDummyRenderer;
import com.dmc.invincible_dmc.client.renderer.patched.entity.PSoulRenderer;
import com.dmc.invincible_dmc.client.renderer.patched.entity.PSummonedSwordRenderer;
import com.dmc.invincible_dmc.client.renderer.patched.layer.TorsoMountLayer;
import com.dmc.invincible_dmc.entity.DMCEntities;
import com.dmc.invincible_dmc.particle.DMCParticles;
import com.merlin204.avalon.entity.client.renderer.EmptyRenderer;
import com.merlin204.avalon.entity.client.renderer.patch.entity.AvalonRendererPatch;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent.Add;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent.Modify;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent.RegisterItemRenderer;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class ClientModEvents {
   @SubscribeEvent
   public static void onRegisterClientReloadListeners(RegisterClientReloadListenersEvent event) {
      event.registerReloadListener(new SimplePreparableReloadListener<Void>() {
         protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
            return null;
         }

         protected void apply(Void state, ResourceManager resourceManager, ProfilerFiller profiler) {
            BloomParticleRenderType.Pipeline.releaseCachedTargets();
            SummonedSwordBloomPipeline.releaseCachedTargets();
            VoidColorGradeRenderer.releaseShaderPackResources();
         }
      });
   }

   @SubscribeEvent
   public static void registerPostPasses(RegisterShadersEvent event) {
      PostPasses.register(event);
   }

   @SubscribeEvent
   public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
      event.register(InvincibleMod_DMC.rl("void"), new VoidDimensionEffects());
   }

   @SubscribeEvent
   public static void onParticleRegistry(RegisterParticleProvidersEvent event) {
      event.registerSpecial((ParticleType)DMCParticles.TRANSPARENT_AFTER_IMAGE.get(), new TransparentEntityAfterImageParticle.Provider());
      event.registerSpriteSet((ParticleType)DMCParticles.YAMATO_SPHERE.get(), YamatoSphere.Provider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.YAMATO_LAST_SPHERE.get(), YamatoLastSphere.Provider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.YAMATO_FLOOR.get(), YamatoFloor.Provider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.COLOR_SHADER_PARTICLE.get(), ColorShaderParticle.Provider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.JUDGEMENT_CUT_SEQUENCE.get(), JudgementCutSequenceParticle.Provider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.JUDGEMENT_CUT_SEQUENCE_NORMAL.get(), JudgementCutSequenceNormalParticle.Provider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.JUDGEMENT_CUT_SEQUENCE_DMC4.get(), JudgementCutSequenceDMC4Particle.Provider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.JUDGEMENT_CUT_SEQUENCE_NORMAL_DMC4.get(), JudgementCutSequenceNormalDMC4Particle.Provider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.JUDGEMENT_CUT_END_DMC4.get(), JudgementCutEndDMC4Particle.Provider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.VERGIL_SLASH_SEQUENCE.get(), VergilSlashSequenceParticle.Provider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.VERGIL_SLASH_SEQUENCE_ALT.get(), VergilSlashSequenceAltParticle.Provider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.JUDGEMENT_CUT_PARTICLE.get(), JudgementCutParticle.Provider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.BLOOM_TRAIL.get(), BloomTrailParticle.Provider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.BLOOM_TRAIL_SWORD.get(), SummonedSwordTrailParticle.Provider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.FLOWING_ANIMATION_TRAIL.get(), FlowingAnimationTrailParticle.Provider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.STATIC_AIR_TRAIL.get(), StaticAirTrailParticle.Provider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.PORTAL.get(), PortalParticle.Provider::new);
      event.registerSpecial((ParticleType)DMCParticles.PROCEDURAL_END_PORTAL.get(), new ProceduralEndPortalParticle.Provider());
      event.registerSpecial((ParticleType)DMCParticles.PARRY_FLASH_MAIN.get(), new ParryFlashMainParticle.Provider());
      event.registerSpecial((ParticleType)DMCParticles.ATTACK_MAIN.get(), new AttackMainParticle.Provider());
      event.registerSpriteSet((ParticleType)DMCParticles.PARRY_FLASH_MAIN_RENDER.get(), ParryFlashMainRenderParticle.Provider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.ATTACK_MAIN_RENDER.get(), AttackMainRenderParticle.Provider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.NORMAL_SPARK.get(), SparkParticle.NormalDustProvider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.SPARK_CONTRACTILE.get(), SparkParticle.ContractiveDustProvider::new);
      event.registerSpriteSet((ParticleType)DMCParticles.SPARK_EXPANSIVE.get(), SparkParticle.ExpansiveDustProvider::new);
      event.registerSpecial((ParticleType)DMCParticles.ALL_SPARK.get(), new AllSpark.Provider());
      event.registerSpriteSet((ParticleType)DMCParticles.SDT_PHASE2_CHROMATIC.get(), SdtPhase2ChromaticParticle.Provider::new);
      event.registerSpecial((ParticleType)DMCParticles.NB.get(), new NbParticle.Provider());
      event.registerSpecial((ParticleType)DMCParticles.NULL.get(), new NullParticle.Provider());
   }

   @SubscribeEvent
   public static void registerOverlays(RegisterGuiOverlaysEvent event) {
      event.registerBelow(VanillaGuiOverlay.HOTBAR.id(), "sdt_screen_overlay", SDTScreenOverlay.INSTANCE);
      event.registerAbove(VanillaGuiOverlay.PLAYER_HEALTH.id(), "jc_debug_hud", JudgementCutDebugHUD.OVERLAY);
      event.registerAbove(VanillaGuiOverlay.PLAYER_HEALTH.id(), "combo_engine_debug", ComboEngineDebugHUD.OVERLAY);
      event.registerAbove(VanillaGuiOverlay.PLAYER_HEALTH.id(), "vergil_status", VergilStatusOverlay.VERGIL_STATUS_OVERLAY);
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void onPatchedRenderersAdd(Add event) {
      event.addPatchedEntityRenderer((EntityType)DMCEntities.DOPPELGANGER.get(), entityType -> new PDoppelgangerRenderer(event.getContext(), entityType));
      event.addPatchedEntityRenderer(
         (EntityType)DMCEntities.DUMMY.get(), entityType -> new PDummyRenderer(event.getContext(), entityType).initLayerLast(event.getContext(), entityType)
      );
      event.addPatchedEntityRenderer(
         (EntityType)DMCEntities.SOUL.get(), entityType -> new PSoulRenderer(event.getContext(), entityType).initLayerLast(event.getContext(), entityType)
      );
      event.addPatchedEntityRenderer(
         (EntityType)DMCEntities.SUMMONED_SWORD.get(),
         entityType -> new PSummonedSwordRenderer(event.getContext(), entityType).initLayerLast(event.getContext(), entityType)
      );
      event.addPatchedEntityRenderer(
         (EntityType)DMCEntities.JUDGEMENT_CUT.get(),
         entityType -> new AvalonRendererPatch(event.getContext(), entityType).initLayerLast(event.getContext(), entityType)
      );
      event.addPatchedEntityRenderer(
         (EntityType)DMCEntities.STORM_BLADES.get(),
         entityType -> new AvalonRendererPatch(event.getContext(), entityType).initLayerLast(event.getContext(), entityType)
      );
      event.addPatchedEntityRenderer(
         (EntityType)DMCEntities.SPIRAL_BLADES.get(),
         entityType -> new AvalonRendererPatch(event.getContext(), entityType).initLayerLast(event.getContext(), entityType)
      );
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void onPatchedRenderersModify(Modify event) {
      if (event.get(EntityType.f_20532_) instanceof PatchedLivingEntityRenderer<?, ?, ?, ?, ?> renderer) {
         renderer.addCustomLayer(new TorsoMountLayer());
      }
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void registerRenderers(RegisterItemRenderer event) {
      event.addItemRenderer(ResourceLocation.fromNamespaceAndPath("invincible_dmc", "yamato_mesh"), RenderYamato::new);
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void registerEntityRenderers(RegisterRenderers event) {
      event.registerEntityRenderer((EntityType)DMCEntities.POWER_CHAIR_SEAT.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)DMCEntities.SLASH_EFFECT.get(), DMCSlashEffectRenderer::new);
      event.registerEntityRenderer((EntityType)DMCEntities.VOID_SLASH_EFFECT.get(), DMCSlashEffectRenderer::new);
      event.registerEntityRenderer((EntityType)DMCEntities.DUMMY.get(), DummyRenderer::new);
      event.registerEntityRenderer((EntityType)DMCEntities.SOUL.get(), SoulRenderer::new);
      event.registerEntityRenderer((EntityType)DMCEntities.DOPPELGANGER.get(), DoppelgangerRenderer::new);
      event.registerEntityRenderer((EntityType)DMCEntities.BLISTERING_BLADES.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)DMCEntities.SPIRAL_BLADES.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)DMCEntities.STORM_BLADES.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)DMCEntities.HEAVY_RAIN_BLADES.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)DMCEntities.PROVOCATION_BLADES.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)DMCEntities.TRIPLE_BLADES.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)DMCEntities.SPINE_BLADE.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)DMCEntities.DMC_DODGELOCATION_INDICATOR.get(), NoopRenderer::new);
      event.registerEntityRenderer((EntityType)DMCEntities.PORTAL.get(), NoopRenderer::new);
   }

   @SubscribeEvent
   @OnlyIn(Dist.CLIENT)
   public static void handleClientSetup(FMLClientSetupEvent event) {
      event.enqueueWork(EffekBloomPostProcessing::register);
      event.enqueueWork(EffekChromaticAberrationPostProcessing::register);
      event.enqueueWork(YamatoRapidSlashCameraController::register);
      DMComboEngine.init();
      ACGModel.LoadOtherModel();
      DemonicDomainRenderer.init();
      VoidColorGradeRenderer.init();
      EntityRenderers.m_174036_((EntityType)DMCEntities.SUMMONED_SWORD.get(), EmptyRenderer::new);
      EntityRenderers.m_174036_((EntityType)DMCEntities.JUDGEMENT_CUT.get(), EmptyRenderer::new);
   }
}
