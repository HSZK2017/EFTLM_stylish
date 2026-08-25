package com.pla.annoyingvillagers.init;

import com.pla.annoyingvillagers.client.renderer.AlexRenderer;
import com.pla.annoyingvillagers.client.renderer.AngrySteveRenderer;
import com.pla.annoyingvillagers.client.renderer.ArmoredHerobrineRenderer;
import com.pla.annoyingvillagers.client.renderer.BbqRenderer;
import com.pla.annoyingvillagers.client.renderer.BlackFireRenderer;
import com.pla.annoyingvillagers.client.renderer.BlockProjectileRenderer;
import com.pla.annoyingvillagers.client.renderer.BlueDemonRenderer;
import com.pla.annoyingvillagers.client.renderer.BlueDemonThunderBeamRenderer;
import com.pla.annoyingvillagers.client.renderer.BlueVillagerKnightRenderer;
import com.pla.annoyingvillagers.client.renderer.ChrisRenderer;
import com.pla.annoyingvillagers.client.renderer.DiamondBoltProjectileRenderer;
import com.pla.annoyingvillagers.client.renderer.DragonBeamRenderer;
import com.pla.annoyingvillagers.client.renderer.DragonMeteoriteRenderer;
import com.pla.annoyingvillagers.client.renderer.ElectricAreaRenderer;
import com.pla.annoyingvillagers.client.renderer.ElectricPhaseRenderer;
import com.pla.annoyingvillagers.client.renderer.EliteHerobrineKnockedRenderer;
import com.pla.annoyingvillagers.client.renderer.EliteHerobrineRenderer;
import com.pla.annoyingvillagers.client.renderer.FloatingLookBlockRenderer;
import com.pla.annoyingvillagers.client.renderer.FlyingShockwaveRenderer;
import com.pla.annoyingvillagers.client.renderer.GreenVillagerKnightRenderer;
import com.pla.annoyingvillagers.client.renderer.HerobrineChrisRenderer;
import com.pla.annoyingvillagers.client.renderer.HerobrineCloneRenderer;
import com.pla.annoyingvillagers.client.renderer.HerobrineDragonRenderer;
import com.pla.annoyingvillagers.client.renderer.HerobrineGregRenderer;
import com.pla.annoyingvillagers.client.renderer.HerobrineWardenRenderer;
import com.pla.annoyingvillagers.client.renderer.HookGunHookRenderer;
import com.pla.annoyingvillagers.client.renderer.InfectedChrisRenderer;
import com.pla.annoyingvillagers.client.renderer.InfectedTheMostMoistBurrit0Renderer;
import com.pla.annoyingvillagers.client.renderer.ItemProjectileRenderer;
import com.pla.annoyingvillagers.client.renderer.JevRenderer;
import com.pla.annoyingvillagers.client.renderer.LowShadowHerobrineCloneRenderer;
import com.pla.annoyingvillagers.client.renderer.NullRenderer;
import com.pla.annoyingvillagers.client.renderer.NullWeaponRenderer;
import com.pla.annoyingvillagers.client.renderer.ObsidianSledgehammerProjectileRenderer;
import com.pla.annoyingvillagers.client.renderer.PortalEntityRenderer;
import com.pla.annoyingvillagers.client.renderer.PurpleVillagerKnightRenderer;
import com.pla.annoyingvillagers.client.renderer.RedVillagerKnightRenderer;
import com.pla.annoyingvillagers.client.renderer.RisingWallBlockRenderer;
import com.pla.annoyingvillagers.client.renderer.ShadowHerobrineRenderer;
import com.pla.annoyingvillagers.client.renderer.ShockWaveBlockRenderer;
import com.pla.annoyingvillagers.client.renderer.SnakeBladeRenderer;
import com.pla.annoyingvillagers.client.renderer.SpriteArrowRenderer;
import com.pla.annoyingvillagers.client.renderer.SteveRenderer;
import com.pla.annoyingvillagers.client.renderer.TransporterHerobrineRenderer;
import com.pla.annoyingvillagers.client.renderer.VillagerScoutCaptainRenderer;
import com.pla.annoyingvillagers.client.renderer.VillagerScoutRenderer;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.entity.WitherSkeletonRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import se.gory_moon.player_mobs.client.render.PlayerMobRenderer;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent.Add;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.renderer.patched.entity.PHumanoidRenderer;
import yesman.epicfight.client.renderer.patched.entity.PIllagerRenderer;

@EventBusSubscriber(
   bus = Bus.MOD,
   value = {Dist.CLIENT}
)
public class AnnoyingVillagersModEntityRenderers {
   @SubscribeEvent
   public static void registerEntityRenderers(RegisterRenderers registerrenderers) {
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.HEROBRINE_CLONE.get(), HerobrineCloneRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.BLUE_DEMON.get(), BlueDemonRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.SHADOW_HEROBRINE_CLONE.get(), ShadowHerobrineRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.TRANSPORTER_HEROBRINE_CLONE.get(), TransporterHerobrineRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.VILLAGER_SCOUT.get(), VillagerScoutRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.VILLAGER_SCOUT_CAPTAIN.get(), VillagerScoutCaptainRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.BLUE_VILLAGER_KNIGHT.get(), BlueVillagerKnightRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.GREEN_VILLAGER_KNIGHT.get(), GreenVillagerKnightRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.RED_VILLAGER_KNIGHT.get(), RedVillagerKnightRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.PURPLE_VILLAGER_KNIGHT.get(), PurpleVillagerKnightRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.ENCHANTED_ENDER_PEARL_PROJECTILE.get(), ThrownItemRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.THROWN_POISON_EGG.get(), ThrownItemRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.ENDER_AEGIS_PROJECTILE.get(), ThrownItemRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.HOOK_GUN_HOOK.get(), HookGunHookRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.ALEX.get(), AlexRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.JEV.get(), JevRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.BBQ.get(), BbqRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.CHRIS.get(), ChrisRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.INFECTED_CHRIS.get(), InfectedChrisRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.HEROBRINE_CHRIS.get(), HerobrineChrisRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.HEROBRINE_7.get(), ShadowHerobrineRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.ARMORED_HEROBRINE.get(), ArmoredHerobrineRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.STEVE.get(), SteveRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.ANGRY_STEVE.get(), AngrySteveRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.INFECTED_PLAYER_NPC.get(), PlayerMobRenderer::new);
      registerrenderers.registerEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.INFECTED_THEMOSTMOISTBURRIT0.get(), InfectedTheMostMoistBurrit0Renderer::new
      );
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.SHADOW_HEROBRINE.get(), ShadowHerobrineRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.GLAIVE_HEROBRINE.get(), EliteHerobrineRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.REAPER_HEROBRINE.get(), EliteHerobrineRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.PORTAL.get(), PortalEntityRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.SNAKE_BLADE.get(), SnakeBladeRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.SWORDSMAN_HEROBRINE.get(), EliteHerobrineRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.SLEDGEHAMMER_HEROBRINE.get(), EliteHerobrineRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.AEGIS_HEROBRINE.get(), EliteHerobrineRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.DRAGON_BEAM.get(), DragonBeamRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.ELITE_HEROBRINE_KNOCKED.get(), EliteHerobrineKnockedRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.LOW_HEROBRINE_CLONE.get(), PlayerMobRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.LOW_SHADOW_HEROBRINE_CLONE.get(), LowShadowHerobrineCloneRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.NULL.get(), NullRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.NULL_SWORD.get(), NullWeaponRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.NULL_AXE.get(), NullWeaponRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.NULL_PICKAXE.get(), NullWeaponRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.NULL_SHOVEL.get(), NullWeaponRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.NULL_HOE.get(), NullWeaponRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.BLOCK_PROJECTILE.get(), BlockProjectileRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.HEROBRINE_GREG.get(), HerobrineGregRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.PLAYER_NPC.get(), PlayerMobRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.HEROBRINE_WARDEN.get(), HerobrineWardenRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.HEROBRINE_DRAGON.get(), HerobrineDragonRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.DRAGON_METEORITE.get(), DragonMeteoriteRenderer::new);
      registerrenderers.registerEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.OBSIDIAN_SLEDGEHAMMER_PROJECTILE.get(), ObsidianSledgehammerProjectileRenderer::new
      );
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.SHOCKWAVE_BLOCK.get(), ShockWaveBlockRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.NULL_SKELETON.get(), WitherSkeletonRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.BLUE_DEMON_THUNDER_BEAM.get(), BlueDemonThunderBeamRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.TRIDENT_LIGHTNING_BOLT.get(), LightningBoltRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.BLUE_DEMON_THROWN_TRIDENT.get(), ThrownTridentRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.DIAMOND_BOLT_PROJECTILE.get(), DiamondBoltProjectileRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.ELECTRIC_AREA.get(), ElectricAreaRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.BLACK_FIRE.get(), BlackFireRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.ENCHANTED_ARROW.get(), SpriteArrowRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.ITEM_PROJECTILE.get(), ItemProjectileRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.FLYING_SHOCKWAVE.get(), FlyingShockwaveRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.ELECTRIC_PHASE.get(), ElectricPhaseRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.RISING_WALL_BLOCK.get(), RisingWallBlockRenderer::new);
      registerrenderers.registerEntityRenderer((EntityType)AnnoyingVillagersModEntities.FLOATING_LOOK_BLOCK.get(), FloatingLookBlockRenderer::new);
   }

   @SubscribeEvent
   public static void onPatchedRenderer(Add add) {
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.PLAYER_NPC.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.VILLAGER_SCOUT.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.VILLAGER_SCOUT_CAPTAIN.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.RED_VILLAGER_KNIGHT.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.BLUE_VILLAGER_KNIGHT.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.GREEN_VILLAGER_KNIGHT.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.PURPLE_VILLAGER_KNIGHT.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.STEVE.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.ANGRY_STEVE.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.ALEX.get(),
         entitytype -> new PHumanoidRenderer(Meshes.ALEX, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.JEV.get(),
         entitytype -> new PIllagerRenderer(add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.CHRIS.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.LOW_HEROBRINE_CLONE.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.LOW_SHADOW_HEROBRINE_CLONE.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.AEGIS_HEROBRINE.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.SWORDSMAN_HEROBRINE.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.GLAIVE_HEROBRINE.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.SLEDGEHAMMER_HEROBRINE.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.REAPER_HEROBRINE.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.NULL_SWORD.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.NULL_AXE.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.NULL_PICKAXE.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.NULL_SHOVEL.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.NULL_HOE.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.NULL_SKELETON.get(),
         entitytype -> new PHumanoidRenderer(Meshes.SKELETON, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.NULL.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.HEROBRINE_CLONE.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.SHADOW_HEROBRINE_CLONE.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.TRANSPORTER_HEROBRINE_CLONE.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.HEROBRINE_CHRIS.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.ARMORED_HEROBRINE.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.HEROBRINE_7.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.SHADOW_HEROBRINE.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.BLUE_DEMON.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.HEROBRINE_GREG.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.INFECTED_PLAYER_NPC.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.INFECTED_THEMOSTMOISTBURRIT0.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.INFECTED_CHRIS.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
      add.addPatchedEntityRenderer(
         (EntityType)AnnoyingVillagersModEntities.ELITE_HEROBRINE_KNOCKED.get(),
         entitytype -> new PHumanoidRenderer(Meshes.BIPED, add.getContext(), entitytype).initLayerLast(add.getContext(), entitytype)
      );
   }
}
