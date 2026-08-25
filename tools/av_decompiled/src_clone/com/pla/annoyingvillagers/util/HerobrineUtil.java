package com.pla.annoyingvillagers.util;

import com.pla.annoyingvillagers.AnnoyingVillagers;
import com.pla.annoyingvillagers.blockentity.CryingObsidianBlockEntity;
import com.pla.annoyingvillagers.blockentity.ObsidianBlockEntity;
import com.pla.annoyingvillagers.blockentity.ShadowObsidianBlockEntity;
import com.pla.annoyingvillagers.blockentity.ShadowObsidianLongPillarBlockEntity;
import com.pla.annoyingvillagers.blockentity.ShadowObsidianMiddlePillarBlockEntity;
import com.pla.annoyingvillagers.blockentity.ShadowObsidianShortPillarBlockEntity;
import com.pla.annoyingvillagers.clazz.HerobrineMob;
import com.pla.annoyingvillagers.clazz.HerobrineObsidianBlock;
import com.pla.annoyingvillagers.clazz.ProjectileBreakableBlocks;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.entity.AegisHerobrineEntity;
import com.pla.annoyingvillagers.entity.ArmoredHerobrineEntity;
import com.pla.annoyingvillagers.entity.BlockProjectileEntity;
import com.pla.annoyingvillagers.entity.EliteHerobrineKnockedEntity;
import com.pla.annoyingvillagers.entity.GlaiveHerobrineEntity;
import com.pla.annoyingvillagers.entity.Herobrine7Entity;
import com.pla.annoyingvillagers.entity.HerobrineChrisEntity;
import com.pla.annoyingvillagers.entity.HerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.HerobrineGregEntity;
import com.pla.annoyingvillagers.entity.InfectedChrisEntity;
import com.pla.annoyingvillagers.entity.InfectedPlayerNpcEntity;
import com.pla.annoyingvillagers.entity.InfectedTheMostMoistBurrit0Entity;
import com.pla.annoyingvillagers.entity.LowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.LowShadowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.NullAxeEntity;
import com.pla.annoyingvillagers.entity.NullEntity;
import com.pla.annoyingvillagers.entity.NullHoeEntity;
import com.pla.annoyingvillagers.entity.NullPickaxeEntity;
import com.pla.annoyingvillagers.entity.NullShovelEntity;
import com.pla.annoyingvillagers.entity.NullSwordEntity;
import com.pla.annoyingvillagers.entity.PlayerNpcEntity;
import com.pla.annoyingvillagers.entity.ReaperHerobrineEntity;
import com.pla.annoyingvillagers.entity.ShadowHerobrineCloneEntity;
import com.pla.annoyingvillagers.entity.ShadowHerobrineEntity;
import com.pla.annoyingvillagers.entity.SledgehammerHerobrineEntity;
import com.pla.annoyingvillagers.entity.SwordsmanHerobrineEntity;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModBlocks;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.network.ClientboundEliteHerobrineFx;
import com.pla.annoyingvillagers.task.DelayedTask;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.PacketDistributor;
import se.gory_moon.player_mobs.entity.PlayerMobEntity;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.utils.math.Vec3f;

public class HerobrineUtil {
   private static final int HEROBRINE_ASSISTANCE_FALLBACK_TICKS = 34;
   private static final double HEROBRINE_ASSISTANCE_FALLBACK_HEIGHT = 3.25;
   private static final double HEROBRINE_ASSISTANCE_FALLBACK_RADIUS = 1.15;
   private static final List<HerobrineUtil.AssistanceSpiralFx> ACTIVE_ASSISTANCE_SPIRALS = new ArrayList<>();
   private static Level assistanceSpiralLevel;
   private static final HerobrineUtil.Pattern2D[] OBSIDIAN_PATTERNS = new HerobrineUtil.Pattern2D[]{
      new HerobrineUtil.Pattern2D(1, 3, new int[][]{{0, 0}, {0, 1}, {0, 2}}),
      new HerobrineUtil.Pattern2D(2, 3, new int[][]{{0, 0}, {0, 1}, {0, 2}, {1, 2}}),
      new HerobrineUtil.Pattern2D(2, 3, new int[][]{{1, 0}, {1, 1}, {1, 2}, {0, 1}}),
      new HerobrineUtil.Pattern2D(3, 3, new int[][]{{0, 0}, {1, 0}, {2, 0}, {1, 1}, {1, 2}}),
      new HerobrineUtil.Pattern2D(3, 3, new int[][]{{0, 2}, {1, 2}, {2, 2}, {1, 1}, {1, 0}}),
      new HerobrineUtil.Pattern2D(3, 3, new int[][]{{1, 1}, {1, 2}, {1, 0}, {0, 1}, {2, 1}}),
      new HerobrineUtil.Pattern2D(3, 4, new int[][]{{1, 0}, {1, 1}, {1, 2}, {1, 3}, {0, 2}, {2, 2}}),
      new HerobrineUtil.Pattern2D(2, 2, new int[][]{{0, 0}, {1, 0}, {0, 1}, {1, 1}}),
      new HerobrineUtil.Pattern2D(3, 3, new int[][]{{0, 0}, {0, 1}, {1, 1}, {2, 1}, {0, 2}, {1, 2}}),
      new HerobrineUtil.Pattern2D(3, 3, new int[][]{{0, 0}, {1, 0}, {1, 1}, {1, 2}, {2, 2}}),
      new HerobrineUtil.Pattern2D(3, 2, new int[][]{{0, 0}, {1, 0}, {2, 0}, {0, 1}})
   };

   public static void startHerobrineAssistanceFallback(Level level, Vec3 origin) {
      if (level != null && level.m_5776_() && origin != null) {
         resetAssistanceSpirals(level);
         RandomSource rand = level.m_213780_();
         HerobrineUtil.AssistanceSpiralFx fx = new HerobrineUtil.AssistanceSpiralFx(origin, rand.m_188500_() * Math.PI * 2.0);
         ACTIVE_ASSISTANCE_SPIRALS.add(fx);
         fx.spawnBaseBurst(level);
      }
   }

   public static void tickHerobrineAssistanceFallbacks(Level level) {
      if (level == null) {
         ACTIVE_ASSISTANCE_SPIRALS.clear();
         assistanceSpiralLevel = null;
      } else {
         resetAssistanceSpirals(level);
         Iterator<HerobrineUtil.AssistanceSpiralFx> iterator = ACTIVE_ASSISTANCE_SPIRALS.iterator();

         while (iterator.hasNext()) {
            if (!iterator.next().tick(level)) {
               iterator.remove();
            }
         }
      }
   }

   private static void resetAssistanceSpirals(Level level) {
      if (assistanceSpiralLevel != level) {
         ACTIVE_ASSISTANCE_SPIRALS.clear();
         assistanceSpiralLevel = level;
      }
   }

   public static boolean isHerobrineFaction(Entity e) {
      return e instanceof HerobrineMob
         || e instanceof HerobrineGregEntity
         || e instanceof LowHerobrineCloneEntity
         || e instanceof LowShadowHerobrineCloneEntity
         || e instanceof InfectedPlayerNpcEntity
         || e instanceof InfectedTheMostMoistBurrit0Entity
         || e instanceof InfectedChrisEntity
         || e instanceof NullSwordEntity
         || e instanceof NullAxeEntity
         || e instanceof NullPickaxeEntity
         || e instanceof NullShovelEntity
         || e instanceof NullHoeEntity
         || e instanceof BlockProjectileEntity
         || e instanceof EliteHerobrineKnockedEntity;
   }

   private static Vec3 randomUnit(RandomSource rand) {
      double z = rand.m_188500_() * 2.0 - 1.0;
      double angle = rand.m_188500_() * Math.PI * 2.0;
      double radius = Math.sqrt(Math.max(0.0, 1.0 - z * z));
      return new Vec3(radius * Math.cos(angle), z, radius * Math.sin(angle));
   }

   private static void spawnParticle(Level level, ParticleOptions particle, Vec3 pos, Vec3 velocity) {
      level.m_6493_(particle, true, pos.f_82479_, pos.f_82480_, pos.f_82481_, velocity.f_82479_, velocity.f_82480_, velocity.f_82481_);
   }

   public static void placeIfReplaceable(ServerLevel level, BlockPos pos, BlockState state, Entity ownerEntity) {
      if (level.m_46749_(pos)) {
         BlockState existingState = level.m_8055_(pos);
         if (!existingState.m_247087_()) {
            ProjectileBreakableBlocks rule = ProjectileBreakableBlocks.find(existingState);
            if (rule == null) {
               return;
            }

            boolean requiresTool = existingState.m_60834_();
            boolean destroyed = level.m_46953_(pos, true, ownerEntity);
            if (!destroyed) {
               return;
            }

            if (requiresTool) {
               Item item = existingState.m_60734_().m_5456_();
               if (item != Items.f_41852_) {
                  Block.m_49840_(level, pos, new ItemStack(item));
               }
            }
         }

         if (level.m_8055_(pos).m_247087_()) {
            level.m_46597_(pos, state);
            BlockEntity blockEntity = level.m_7702_(pos);
            if (blockEntity != null) {
               if (blockEntity instanceof ObsidianBlockEntity obsidianBlockEntity) {
                  obsidianBlockEntity.setOwner(ownerEntity.m_20148_());
               } else if (blockEntity instanceof ShadowObsidianBlockEntity shadowObsidianBlockEntity) {
                  shadowObsidianBlockEntity.setOwner(ownerEntity.m_20148_());
               } else if (blockEntity instanceof CryingObsidianBlockEntity cryingObsidianBlockEntity) {
                  cryingObsidianBlockEntity.setOwner(ownerEntity.m_20148_());
               } else if (blockEntity instanceof ShadowObsidianShortPillarBlockEntity shadowObsidianShortPillarBlockEntity) {
                  shadowObsidianShortPillarBlockEntity.setOwner(ownerEntity.m_20148_());
               } else if (blockEntity instanceof ShadowObsidianMiddlePillarBlockEntity shadowObsidianMiddlePillarBlockEntity) {
                  shadowObsidianMiddlePillarBlockEntity.setOwner(ownerEntity.m_20148_());
               } else if (blockEntity instanceof ShadowObsidianLongPillarBlockEntity shadowObsidianLongPillarBlockEntity) {
                  shadowObsidianLongPillarBlockEntity.setOwner(ownerEntity.m_20148_());
               }

               blockEntity.m_6596_();
               level.m_7260_(pos, state, state, 3);
            }
         }
      }
   }

   private static HerobrineUtil.Basis basisFromEntity(Entity e) {
      Vec3 forward = e.m_20154_().m_82541_();
      Vec3 worldUp = new Vec3(0.0, 1.0, 0.0);
      Vec3 right = forward.m_82537_(worldUp);
      if (right.m_82556_() < 1.0E-6) {
         right = new Vec3(1.0, 0.0, 0.0);
      } else {
         right = right.m_82541_();
      }

      Vec3 up = right.m_82537_(forward).m_82541_();
      return new HerobrineUtil.Basis(forward, right, up);
   }

   public static void transformHerobrine(LevelAccessor world, double x, double y, double z, Entity entity, Entity herobrineEntity) {
      if (entity != null) {
         Random random = new Random();
         if (!(random.nextFloat() >= ((Double)AnnoyingVillagersConfig.HEROBRINE_POSSESS_RATE.get()).floatValue())) {
            if (entity instanceof PlayerNpcEntity victim) {
               if (!(world instanceof ServerLevel serverLevel)) {
                  return;
               }

               entity.getPersistentData().m_128379_("die_by_possess", true);
               Entity possessed;
               if (!(herobrineEntity instanceof HerobrineCloneEntity)
                  && !(herobrineEntity instanceof HerobrineChrisEntity)
                  && !(herobrineEntity instanceof NullEntity)
                  && !(herobrineEntity instanceof NullSwordEntity)
                  && !(herobrineEntity instanceof NullAxeEntity)
                  && !(herobrineEntity instanceof NullPickaxeEntity)
                  && !(herobrineEntity instanceof NullShovelEntity)
                  && !(herobrineEntity instanceof NullHoeEntity)
                  && !(herobrineEntity instanceof GlaiveHerobrineEntity)
                  && !(herobrineEntity instanceof AegisHerobrineEntity)
                  && !(herobrineEntity instanceof ReaperHerobrineEntity)
                  && !(herobrineEntity instanceof SwordsmanHerobrineEntity)
                  && !(herobrineEntity instanceof SledgehammerHerobrineEntity)) {
                  possessed = new LowShadowHerobrineCloneEntity(
                     (EntityType<LowShadowHerobrineCloneEntity>)AnnoyingVillagersModEntities.LOW_SHADOW_HEROBRINE_CLONE.get(), serverLevel
                  );
               } else {
                  possessed = new LowHerobrineCloneEntity(
                     (EntityType<? extends LowHerobrineCloneEntity>)AnnoyingVillagersModEntities.LOW_HEROBRINE_CLONE.get(), serverLevel
                  );
               }

               possessed.m_7678_(entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), entity.m_146908_(), entity.m_146909_());
               victim.m_7770_();
               possessed.getPersistentData().m_128359_("killed_name", victim.m_7770_().getString());
               if (!victim.m_6844_(EquipmentSlot.HEAD).m_41720_().equals(Items.f_42680_)) {
                  possessed.m_8061_(EquipmentSlot.HEAD, victim.m_6844_(EquipmentSlot.HEAD).m_41777_());
               }

               possessed.m_8061_(EquipmentSlot.CHEST, victim.m_6844_(EquipmentSlot.CHEST).m_41777_());
               possessed.m_8061_(EquipmentSlot.LEGS, victim.m_6844_(EquipmentSlot.LEGS).m_41777_());
               possessed.m_8061_(EquipmentSlot.FEET, victim.m_6844_(EquipmentSlot.FEET).m_41777_());
               possessed.m_8061_(EquipmentSlot.MAINHAND, victim.m_6844_(EquipmentSlot.MAINHAND).m_41777_());
               possessed.m_8061_(EquipmentSlot.OFFHAND, victim.m_6844_(EquipmentSlot.OFFHAND).m_41777_());
               Mob mob = (Mob)possessed;
               if (mob instanceof LowHerobrineCloneEntity lowHerobrineCloneEntity) {
                  lowHerobrineCloneEntity.setUsername(((PlayerMobEntity)entity).getUsername());
                  lowHerobrineCloneEntity.setProfile(((PlayerMobEntity)entity).getProfile());
                  if (herobrineEntity instanceof HerobrineMob herobrineMob) {
                     lowHerobrineCloneEntity.setPossessedByEntity(herobrineMob);
                     lowHerobrineCloneEntity.setPossessedByUuid(herobrineMob.m_20148_());
                  } else if (herobrineEntity instanceof NullSwordEntity nullSwordEntity) {
                     lowHerobrineCloneEntity.setPossessedByEntity(nullSwordEntity.getNullEntity());
                     lowHerobrineCloneEntity.setPossessedByUuid(nullSwordEntity.getNullUUID());
                  } else if (herobrineEntity instanceof NullAxeEntity nullAxeEntity) {
                     lowHerobrineCloneEntity.setPossessedByEntity(nullAxeEntity.getNullEntity());
                     lowHerobrineCloneEntity.setPossessedByUuid(nullAxeEntity.getNullUUID());
                  } else if (herobrineEntity instanceof NullPickaxeEntity nullPickaxeEntity) {
                     lowHerobrineCloneEntity.setPossessedByEntity(nullPickaxeEntity.getNullEntity());
                     lowHerobrineCloneEntity.setPossessedByUuid(nullPickaxeEntity.getNullUUID());
                  } else if (herobrineEntity instanceof NullShovelEntity nullShovelEntity) {
                     lowHerobrineCloneEntity.setPossessedByEntity(nullShovelEntity.getNullEntity());
                     lowHerobrineCloneEntity.setPossessedByUuid(nullShovelEntity.getNullUUID());
                  } else {
                     NullHoeEntity nullHoeEntity = (NullHoeEntity)herobrineEntity;
                     lowHerobrineCloneEntity.setPossessedByEntity(nullHoeEntity.getNullEntity());
                     lowHerobrineCloneEntity.setPossessedByUuid(nullHoeEntity.getNullUUID());
                  }
               }

               if (mob instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity && herobrineEntity instanceof HerobrineMob herobrineMob) {
                  lowShadowHerobrineCloneEntity.setPossessedByEntity(herobrineMob);
                  lowShadowHerobrineCloneEntity.setPossessedByUuid(herobrineMob.m_20148_());
               }

               mob.m_6518_(serverLevel, world.m_6436_(entity.m_20183_()), MobSpawnType.MOB_SUMMONED, null, null);
               serverLevel.m_7967_(possessed);
            }
         }
      }
   }

   public static void initialSpawn(LevelAccessor levelaccessor, Entity entity, int recallTicks, MobSpawnType mobSpawnType) {
      int min = (Integer)AnnoyingVillagersConfig.HEROBRINE_RECALL_MIN_TIME.get();
      int max = (Integer)AnnoyingVillagersConfig.HEROBRINE_RECALL_MAX_TIME.get();
      int randomMin = Math.min(min, max);
      int randomMax = Math.max(min, max);
      if (entity != null) {
         if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
            String killedName = entity.getPersistentData().m_128461_("killed_name");
            if (!killedName.isEmpty()) {
               levelaccessor.m_7654_()
                  .m_6846_()
                  .m_240416_(Component.m_237113_(killedName + " " + Component.m_237115_("subtitles.possessed_npc").getString()), false);
            } else {
               label106: {
                  if (entity instanceof LowHerobrineCloneEntity lowHerobrineCloneEntity && !lowHerobrineCloneEntity.isSummoned()
                     || entity instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity && !lowShadowHerobrineCloneEntity.isSummoned()) {
                     levelaccessor.m_7654_().m_6846_().m_240416_(Component.m_237113_(Component.m_237115_("subtitles.possessed_random").getString()), false);
                     break label106;
                  }

                  if (recallTicks == 0) {
                     recallTicks = (randomMin + new Random().nextInt(randomMax - randomMin + 1)) * 60 * 20;
                     if (entity instanceof HerobrineMob herobrineMob) {
                        herobrineMob.setRecallTicks(recallTicks);
                     }
                  }

                  if (!mobSpawnType.equals(MobSpawnType.NATURAL) && !mobSpawnType.equals(MobSpawnType.CHUNK_GENERATION)) {
                     if (!(entity instanceof HerobrineMob herobrineMob)) {
                        if (entity instanceof LivingEntity livingEntity) {
                           HerobrinePortalUtil.spawnHerobrine(livingEntity);
                        }
                        break label106;
                     }

                     if (mobSpawnType.equals(MobSpawnType.SPAWN_EGG) || mobSpawnType.equals(MobSpawnType.COMMAND)) {
                        herobrineMob.setRenderPortal(true);
                     }

                     HerobrinePortalUtil.spawnHerobrine(herobrineMob);
                     levelaccessor.m_7654_()
                        .m_6846_()
                        .m_240416_(Component.m_237113_(herobrineMob.getChatName() + " " + Component.m_237115_("subtitles.herobrine_arrive").getString()), false);
                  } else if (Math.random() <= 0.5) {
                     levelaccessor.m_7654_().m_6846_().m_240416_(Component.m_237113_(Component.m_237115_("subtitles.possessed_random").getString()), false);
                  } else if (entity instanceof HerobrineMob herobrineMobx) {
                     herobrineMobx.setRenderPortal(true);
                     HerobrinePortalUtil.spawnHerobrine(herobrineMobx);
                     levelaccessor.m_7654_()
                        .m_6846_()
                        .m_240416_(
                           Component.m_237113_(herobrineMobx.getChatName() + " " + Component.m_237115_("subtitles.herobrine_arrive").getString()), false
                        );
                  } else if (entity instanceof LowShadowHerobrineCloneEntity lowShadowHerobrineCloneEntity) {
                     lowShadowHerobrineCloneEntity.setRenderPortal(true);
                     HerobrinePortalUtil.spawnHerobrine(lowShadowHerobrineCloneEntity);
                     levelaccessor.m_7654_()
                        .m_6846_()
                        .m_240416_(Component.m_237113_("§5Netherite Herobrine§r " + Component.m_237115_("subtitles.herobrine_arrive").getString()), false);
                  }
               }
            }
         }

         if (entity.m_9236_() instanceof ServerLevel
            && (
               entity instanceof HerobrineCloneEntity
                  || entity instanceof ShadowHerobrineCloneEntity
                  || entity instanceof HerobrineChrisEntity
                  || entity instanceof Herobrine7Entity
                  || entity instanceof ArmoredHerobrineEntity
            )) {
            entity.m_5496_((SoundEvent)AnnoyingVillagersModSounds.HEROBRINE_CLONE_SAY_ON_SPAWN.get(), 0.5F, 1.0F);
         }

         if (entity.m_9236_() instanceof ServerLevel && entity instanceof ShadowHerobrineEntity) {
            entity.m_5496_((SoundEvent)AnnoyingVillagersModSounds.SHADOW_HEROBRINE_SAY_ON_SPAWN.get(), 0.5F, 1.0F);
         }

         TeamUtil.addOrJoinTeam(entity, "herobrine");
      }
   }

   public static void spawnEliteEffect(Level level, double x, double y, double z, Entity entity) {
      if (entity != null && level instanceof ServerLevel serverLevel && Math.random() <= 0.3) {
         boolean extraParticle = Math.random() <= 0.87;
         AnnoyingVillagers.PACKET_HANDLER
            .send(
               PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
               new ClientboundEliteHerobrineFx(entity.m_19879_(), entity.f_19797_, new Vec3(x, y, z), extraParticle)
            );
         if (extraParticle) {
            serverLevel.m_6263_(
               null,
               x,
               y,
               z,
               (SoundEvent)AnnoyingVillagersModSounds.ELECTRIFY.get(),
               SoundSource.NEUTRAL,
               new Random().nextFloat(0.05F, 0.4F),
               new Random().nextFloat(0.5F, 1.2F)
            );
         }
      }
   }

   public static void spawnObsidianEyeLineStaggered(final ServerLevel level, final Entity entity, final BlockState state, int tickGap) {
      if (level != null && entity != null) {
         HerobrineUtil.Basis b = basisFromEntity(entity);
         Vec3 eye = entity.m_20299_(1.0F);
         BlockPos[] sequence = new BlockPos[7];
         sequence[0] = BlockPos.m_274446_(eye.m_82549_(b.fwd().m_82490_(1.0)).m_82549_(b.up().m_82490_(-1.0)));

         for (int i = 1; i <= 6; i++) {
            sequence[i] = BlockPos.m_274446_(eye.m_82549_(b.fwd().m_82490_((double)i)));
         }

         for (int i = 0; i < sequence.length; i++) {
            final BlockPos pos = sequence[i];
            new DelayedTask(i * Math.max(1, tickGap)) {
               @Override
               public void run() {
                  HerobrineUtil.placeIfReplaceable(level, pos, state, entity);
               }
            };
         }
      }
   }

   private static boolean hasGroundWithin(ServerLevel level, Entity e, int maxDown) {
      Vec3 start = new Vec3(e.m_20185_(), e.m_20191_().f_82289_ + 0.001, e.m_20189_());
      Vec3 end = start.m_82520_(0.0, (double)(-maxDown), 0.0);
      BlockHitResult hit = level.m_45547_(new ClipContext(start, end, net.minecraft.world.level.ClipContext.Block.COLLIDER, Fluid.NONE, e));
      return hit.m_6662_() != Type.MISS;
   }

   public static void spawnObsidianPatternAtBody(ServerLevel level, Entity entity, BlockState state) {
      if (level != null && entity != null) {
         if (hasGroundWithin(level, entity, 3)) {
            int minY = level.m_141937_();
            int maxY = level.m_151558_() - 1;
            BlockPos feet = BlockPos.m_274561_(entity.m_20185_(), entity.m_20191_().f_82289_, entity.m_20189_());
            RandomSource rand = level.m_213780_();
            HerobrineUtil.Pattern2D pat = OBSIDIAN_PATTERNS[rand.m_188503_(OBSIDIAN_PATTERNS.length)];
            Direction face = Plane.HORIZONTAL.m_235690_(rand);
            boolean mirror = rand.m_188499_();
            BlockPos origin = feet.m_121945_(face);
            Direction side = mirror ? face.m_122428_() : face.m_122427_();
            int cx = pat.centerX();

            for (int[] c : pat.cells) {
               int localX = c[0] - cx;
               int localY = c[1];
               int y = origin.m_123342_() + localY;
               if (y >= minY && y <= maxY) {
                  BlockPos p = origin.m_7918_(side.m_122429_() * localX, localY, side.m_122431_() * localX);
                  placeIfReplaceable(level, p, state, entity);
               }
            }
         }
      }
   }

   public static void summonObsidianBlocksInfrontOf(
      final ServerLevel level, final LivingEntity caster, final BlockState obsidianState, int amount, final Joint joint
   ) {
      if (level != null && caster != null) {
         final Vec3[] lockedEye = new Vec3[]{null};
         final Vec3[] lockedDir = new Vec3[]{null};
         final int[] anchorY = new int[]{Integer.MIN_VALUE};

         for (int i = 1; i <= amount; i++) {
            final int forwardBlock = i + 1;
            new DelayedTask(i) {
               @Override
               public void run() {
                  if (caster.m_6084_()) {
                     if (caster.m_9236_() == level) {
                        if (lockedDir[0] == null) {
                           lockedEye[0] = caster.m_20299_(1.0F);
                           lockedDir[0] = caster.m_20154_().m_82541_();
                        }

                        Vec3 placeVec;
                        if (forwardBlock == 2) {
                           Vec3 jointVec = EpicfightUtil.getJointWithTranslation(caster, new Vec3f(0.0F, 0.0F, 0.0F), joint, 0.0F, 0.0);
                           if (jointVec == null) {
                              return;
                           }

                           placeVec = jointVec.m_82549_(lockedDir[0].m_82490_(1.0));
                           anchorY[0] = BlockPos.m_274446_(placeVec).m_123342_();
                        } else {
                           if (anchorY[0] == Integer.MIN_VALUE) {
                              return;
                           }

                           Vec3 target = lockedEye[0].m_82549_(lockedDir[0].m_82490_((double)forwardBlock));
                           placeVec = new Vec3(target.f_82479_, (double)anchorY[0] + 0.5, target.f_82481_);
                        }

                        HerobrineUtil.placeIfReplaceable(level, BlockPos.m_274446_(placeVec), obsidianState, caster);
                     }
                  }
               }
            };
         }
      }
   }

   public static void summonObsidianWall(ServerLevel level, LivingEntity caster, BlockState obsidianState) {
      if (level != null && caster != null) {
         Vec3 eye = caster.m_20299_(1.0F);
         Vec3 fwd = caster.m_20154_().m_82541_();
         Vec3 left = new Vec3(fwd.f_82481_, 0.0, -fwd.f_82479_);
         if (left.m_82556_() < 1.0E-6) {
            left = new Vec3(1.0, 0.0, 0.0);
         } else {
            left = left.m_82541_();
         }

         Vec3 up = fwd.m_82537_(left).m_82541_();
         BlockPos p1 = BlockPos.m_274446_(eye.m_82549_(left.m_82490_(-2.0)).m_82549_(up.m_82490_(-1.0)).m_82549_(fwd.m_82490_(3.0)));
         BlockPos p2 = BlockPos.m_274446_(eye.m_82549_(left.m_82490_(2.0)).m_82549_(up.m_82490_(2.0)).m_82549_(fwd.m_82490_(3.0)));
         if (caster.m_6084_()) {
            int minX = Math.min(p1.m_123341_(), p2.m_123341_());
            int minY = Math.min(p1.m_123342_(), p2.m_123342_());
            int minZ = Math.min(p1.m_123343_(), p2.m_123343_());
            int maxX = Math.max(p1.m_123341_(), p2.m_123341_());
            int maxY = Math.max(p1.m_123342_(), p2.m_123342_());
            int maxZ = Math.max(p1.m_123343_(), p2.m_123343_());
            MutableBlockPos pos = new MutableBlockPos();

            for (int x = minX; x <= maxX; x++) {
               for (int y = minY; y <= maxY; y++) {
                  for (int z = minZ; z <= maxZ; z++) {
                     pos.m_122178_(x, y, z);
                     placeIfReplaceable(level, pos, obsidianState, caster);
                  }
               }
            }
         }
      }
   }

   private static void placePillarWorldOffsets(ServerLevel level, Vec3 eye, int dx, int dz, BlockState state, LivingEntity caster) {
      for (int dy = -1; dy <= 1; dy++) {
         BlockPos pos = BlockPos.m_274561_(eye.f_82479_ + (double)dx, eye.f_82480_ + (double)dy, eye.f_82481_ + (double)dz);
         placeIfReplaceable(level, pos, state, caster);
      }
   }

   private static void placeSingleWorldOffset(ServerLevel level, Vec3 eye, int dx, int dy, int dz, BlockState state, LivingEntity caster) {
      BlockPos pos = BlockPos.m_274561_(eye.f_82479_ + (double)dx, eye.f_82480_ + (double)dy, eye.f_82481_ + (double)dz);
      placeIfReplaceable(level, pos, state, caster);
   }

   public static void summonObsidianCross(final ServerLevel level, final LivingEntity caster, final BlockState obsidianState) {
      if (level != null && caster != null) {
         new DelayedTask(2) {
            @Override
            public void run() {
               if (caster.m_6084_()) {
                  Vec3 eye = caster.m_20299_(1.0F);
                  HerobrineUtil.placePillarWorldOffsets(level, eye, 0, 3, obsidianState, caster);
                  HerobrineUtil.placePillarWorldOffsets(level, eye, 0, -3, obsidianState, caster);
                  HerobrineUtil.placePillarWorldOffsets(level, eye, 3, 0, obsidianState, caster);
                  HerobrineUtil.placePillarWorldOffsets(level, eye, -3, 0, obsidianState, caster);
               }
            }
         };
         new DelayedTask(4) {
            @Override
            public void run() {
               if (caster.m_6084_()) {
                  Vec3 eye = caster.m_20299_(1.0F);
                  HerobrineUtil.placeSingleWorldOffset(level, eye, 0, 2, 3, obsidianState, caster);
                  HerobrineUtil.placeSingleWorldOffset(level, eye, 0, 2, -3, obsidianState, caster);
                  HerobrineUtil.placeSingleWorldOffset(level, eye, 3, 2, 0, obsidianState, caster);
                  HerobrineUtil.placeSingleWorldOffset(level, eye, -3, 2, 0, obsidianState, caster);
               }
            }
         };
         new DelayedTask(6) {
            @Override
            public void run() {
               if (caster.m_6084_()) {
                  Vec3 eye = caster.m_20299_(1.0F);
                  int[] dist = new int[]{5, 7};

                  for (int d : dist) {
                     HerobrineUtil.placePillarWorldOffsets(level, eye, 0, d, obsidianState, caster);
                     HerobrineUtil.placePillarWorldOffsets(level, eye, 0, -d, obsidianState, caster);
                     HerobrineUtil.placePillarWorldOffsets(level, eye, d, 0, obsidianState, caster);
                     HerobrineUtil.placePillarWorldOffsets(level, eye, -d, 0, obsidianState, caster);
                  }
               }
            }
         };
         new DelayedTask(8) {
            @Override
            public void run() {
               if (caster.m_6084_()) {
                  Vec3 eye = caster.m_20299_(1.0F);
                  int[] dists = new int[]{5, 7};

                  for (int d : dists) {
                     HerobrineUtil.placeSingleWorldOffset(level, eye, 0, 2, d, obsidianState, caster);
                     HerobrineUtil.placeSingleWorldOffset(level, eye, 0, 2, -d, obsidianState, caster);
                     HerobrineUtil.placeSingleWorldOffset(level, eye, d, 2, 0, obsidianState, caster);
                     HerobrineUtil.placeSingleWorldOffset(level, eye, -d, 2, 0, obsidianState, caster);
                  }
               }
            }
         };
      }
   }

   private static void placePillarWorldOffsetsHeight(ServerLevel level, Vec3 eye, int dx, int dz, int minDy, int maxDy, BlockState state, LivingEntity caster) {
      for (int dy = minDy; dy <= maxDy; dy++) {
         BlockPos pos = BlockPos.m_274561_(eye.f_82479_ + (double)dx, eye.f_82480_ + (double)dy, eye.f_82481_ + (double)dz);
         placeIfReplaceable(level, pos, state, caster);
      }
   }

   public static void summonObsidianSmallCross(final ServerLevel level, final LivingEntity caster, final BlockState obsidianState) {
      if (level != null && caster != null) {
         new DelayedTask(2) {
            @Override
            public void run() {
               if (caster.m_6084_()) {
                  if (caster.m_9236_() == level) {
                     Vec3 eye = caster.m_20299_(1.0F);
                     boolean isLongPillar = obsidianState.m_60713_((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_LONG_PILLAR.get());
                     int minDy = -1;
                     int maxDy = isLongPillar ? -1 : 0;
                     int d = 3;
                     HerobrineUtil.placePillarWorldOffsetsHeight(level, eye, 0, d, minDy, maxDy, obsidianState, caster);
                     HerobrineUtil.placePillarWorldOffsetsHeight(level, eye, 0, -d, minDy, maxDy, obsidianState, caster);
                     HerobrineUtil.placePillarWorldOffsetsHeight(level, eye, d, 0, minDy, maxDy, obsidianState, caster);
                     HerobrineUtil.placePillarWorldOffsetsHeight(level, eye, -d, 0, minDy, maxDy, obsidianState, caster);
                  }
               }
            }
         };
      }
   }

   public static void summonObsidianPillar(final ServerLevel level, final LivingEntity caster, final BlockState obsidianState) {
      if (level != null && caster != null) {
         Vec3 eye = caster.m_20299_(1.0F);
         Vec3 fwd = caster.m_20154_().m_82541_();
         Vec3 ahead = eye.m_82549_(fwd.m_82490_(2.0));
         Vec3 bodyLevelAhead = new Vec3(ahead.f_82479_, caster.m_20186_(), ahead.f_82481_);
         final BlockPos base = BlockPos.m_274446_(bodyLevelAhead).m_6625_(1);

         for (int delay = 1; delay <= 12; delay++) {
            final int yOffset = delay - 1;
            new DelayedTask(delay) {
               @Override
               public void run() {
                  if (caster.m_6084_()) {
                     BlockPos pos = base.m_6630_(yOffset);
                     HerobrineUtil.placeIfReplaceable(level, pos, obsidianState, caster);
                  }
               }
            };
         }
      }
   }

   public static void summonShadowObsidianShortPillarShootToward(ServerLevel level, Entity ownerEntity, int maxDistance, Joint joint) {
      if (level != null && ownerEntity != null) {
         BlockState baseState = (BlockState)((BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_SHORT_PILLAR.get())
               .m_49966_()
               .m_61124_(HerobrineObsidianBlock.FROM_PLAYER, ownerEntity instanceof Player))
            .m_61124_(BlockStateProperties.f_61374_, ownerEntity.m_6350_());
         summonPillarsTowardJoint(level, ownerEntity, baseState, Math.max(2, maxDistance), joint);
      }
   }

   public static void summonShadowObsidianMiddlePillarShootToward(ServerLevel level, Entity ownerEntity, int maxDistance, Joint joint) {
      if (level != null && ownerEntity != null) {
         BlockState baseState = (BlockState)((BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_MIDDLE_PILLAR.get())
               .m_49966_()
               .m_61124_(HerobrineObsidianBlock.FROM_PLAYER, ownerEntity instanceof Player))
            .m_61124_(BlockStateProperties.f_61374_, ownerEntity.m_6350_());
         summonPillarsTowardJoint(level, ownerEntity, baseState, maxDistance, joint);
      }
   }

   private static void summonPillarsTowardJoint(
      final ServerLevel level, final Entity ownerEntity, final BlockState blockState, int maxDistance, final Joint joint
   ) {
      final Vec3[] lockedDir = new Vec3[]{null};
      final Vec3[] lockedJoint = new Vec3[]{null};
      final Direction[] lockedFacing = new Direction[]{null};
      final int[] anchorY = new int[]{Integer.MIN_VALUE};

      for (int dist = 2; dist <= maxDistance + 1; dist++) {
         final int d = dist;
         new DelayedTask(d) {
            @Override
            public void run() {
               if (ownerEntity.m_6084_()) {
                  if (ownerEntity.m_9236_() == level) {
                     if (lockedDir[0] == null) {
                        lockedDir[0] = ownerEntity.m_20154_().m_82541_();
                        lockedFacing[0] = ownerEntity.m_6350_();
                        lockedJoint[0] = EpicfightUtil.getJointWithTranslation(ownerEntity, new Vec3f(0.0F, 0.0F, 0.0F), joint, 0.0F, 0.0);
                        if (lockedJoint[0] == null) {
                           return;
                        }
                     }

                     BlockState stateNow = blockState;
                     if (stateNow.m_61138_(BlockStateProperties.f_61374_) && lockedFacing[0] != null) {
                        stateNow = (BlockState)stateNow.m_61124_(BlockStateProperties.f_61374_, lockedFacing[0]);
                     }

                     Vec3 raw = lockedJoint[0].m_82549_(lockedDir[0].m_82490_((double)d));
                     if (d == 2) {
                        anchorY[0] = BlockPos.m_274446_(raw).m_123342_();
                     } else if (anchorY[0] == Integer.MIN_VALUE) {
                        return;
                     }

                     Vec3 placeVec = d == 2 ? raw : new Vec3(raw.f_82479_, (double)anchorY[0] + 0.5, raw.f_82481_);
                     HerobrineUtil.placeIfReplaceable(level, BlockPos.m_274446_(placeVec), stateNow, ownerEntity);
                  }
               }
            }
         };
      }
   }

   public static void summonShadowObsidianLongPillarDefense(ServerLevel level, Entity ownerEntity) {
      if (level != null && ownerEntity != null) {
         if (ownerEntity.m_6084_()) {
            if (ownerEntity.m_9236_() == level) {
               BlockState longPillarState = (BlockState)((BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_LONG_PILLAR.get())
                     .m_49966_()
                     .m_61124_(HerobrineObsidianBlock.FROM_PLAYER, ownerEntity instanceof Player))
                  .m_61124_(BlockStateProperties.f_61374_, ownerEntity.m_6350_());
               Vec3 origin = ownerEntity.m_20299_(1.0F);
               Vec3 forward = ownerEntity.m_20154_().m_82541_();
               Vec3 worldUp = new Vec3(0.0, 1.0, 0.0);
               Vec3 left = forward.m_82537_(worldUp);
               if (left.m_82556_() < 1.0E-6) {
                  Direction facing = ownerEntity.m_6350_();
                  Direction leftDir = facing.m_122428_();
                  left = new Vec3((double)leftDir.m_122429_(), 0.0, (double)leftDir.m_122431_());
               } else {
                  left = left.m_82541_();
               }

               Vec3 up = left.m_82537_(forward).m_82541_();
               int[][] localOffsets = new int[][]{{0, -1, 2}, {-1, -1, 2}, {1, -1, 2}, {-2, -1, 2}, {2, -1, 2}, {0, -1, 3}, {-1, -1, 3}, {1, -1, 3}};

               for (int[] o : localOffsets) {
                  int dx = o[0];
                  int dy = o[1];
                  int dz = o[2];
                  Vec3 target = origin.m_82549_(left.m_82490_((double)dx)).m_82549_(up.m_82490_((double)dy)).m_82549_(forward.m_82490_((double)dz));
                  BlockPos pos = BlockPos.m_274446_(target);
                  if (level.m_8055_(pos).m_60795_()) {
                     placeIfReplaceable(level, pos, longPillarState, ownerEntity);
                  }
               }
            }
         }
      }
   }

   public static void summonShadowObsidianLongPillarDefenseWide(final ServerLevel level, final Entity ownerEntity) {
      int startDistance = 2;
      int depth = 5;
      int maxHalfWidth = 4;
      final int dy = -1;
      int startDelay = 2;
      if (level != null && ownerEntity != null) {
         if (ownerEntity.m_6084_()) {
            if (ownerEntity.m_9236_() == level) {
               final BlockState longPillarState = (BlockState)((BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_LONG_PILLAR.get())
                     .m_49966_()
                     .m_61124_(HerobrineObsidianBlock.FROM_PLAYER, ownerEntity instanceof Player))
                  .m_61124_(BlockStateProperties.f_61374_, ownerEntity.m_6350_());
               final Vec3 origin = ownerEntity.m_20299_(1.0F);
               Vec3 look = ownerEntity.m_20154_();
               Vec3 forward = new Vec3(look.f_82479_, 0.0, look.f_82481_);
               if (forward.m_82556_() < 1.0E-6) {
                  Direction dir = ownerEntity.m_6350_();
                  forward = new Vec3((double)dir.m_122429_(), 0.0, (double)dir.m_122431_());
               } else {
                  forward = forward.m_82541_();
               }

               final Vec3 worldUp = new Vec3(0.0, 1.0, 0.0);
               final Vec3 left = forward.m_82537_(worldUp).m_82541_();

               for (int dz = startDistance; dz < startDistance + depth; dz++) {
                  final int fdz = dz;
                  int halfWidth = Math.max(0, maxHalfWidth - (dz - startDistance));
                  int rowDelay = startDelay + (dz - startDistance);

                  for (int dx = -halfWidth; dx <= halfWidth; dx++) {
                     final int fdx = dx;
                     final Vec3 finalForward = forward;
                     new DelayedTask(rowDelay) {
                        @Override
                        public void run() {
                           if (ownerEntity.m_6084_()) {
                              if (ownerEntity.m_9236_() == level) {
                                 Vec3 target = origin.m_82549_(left.m_82490_((double)fdx))
                                    .m_82549_(worldUp.m_82490_((double)dy))
                                    .m_82549_(finalForward.m_82490_((double)fdz));
                                 BlockPos pos = BlockPos.m_274446_(target);
                                 HerobrineUtil.placeIfReplaceable(level, pos, longPillarState, ownerEntity);
                              }
                           }
                        }
                     };
                  }
               }
            }
         }
      }
   }

   public static void summonShadowObsidianLongPillarShootToward(ServerLevel level, Entity ownerEntity) {
      if (level != null && ownerEntity != null) {
         BlockState baseState = (BlockState)((BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_LONG_PILLAR.get())
               .m_49966_()
               .m_61124_(HerobrineObsidianBlock.FROM_PLAYER, ownerEntity instanceof Player))
            .m_61124_(BlockStateProperties.f_61374_, ownerEntity.m_6350_());
         Vec3[] lockedEye = new Vec3[]{null};
         HerobrineUtil.Basis[] lockedBasis = new HerobrineUtil.Basis[]{null};
         Direction[] lockedFacing = new Direction[]{null};
         scheduleLocalEyesForwardLine(level, ownerEntity, baseState, 2, 1, 1, lockedEye, lockedBasis, lockedFacing);
         scheduleLocalEyesForwardLine(level, ownerEntity, baseState, 3, 2, 3, lockedEye, lockedBasis, lockedFacing);
         scheduleLocalEyesForwardLine(level, ownerEntity, baseState, 4, 4, 5, lockedEye, lockedBasis, lockedFacing);
         scheduleLocalEyesForwardLine(level, ownerEntity, baseState, 5, 6, 7, lockedEye, lockedBasis, lockedFacing);
         scheduleLocalEyesForwardLine(level, ownerEntity, baseState, 6, 8, 9, lockedEye, lockedBasis, lockedFacing);
         scheduleLocalEyesForwardLine(level, ownerEntity, baseState, 7, 10, 11, lockedEye, lockedBasis, lockedFacing);
         scheduleLocalEyesForwardLine(level, ownerEntity, baseState, 8, 12, 13, lockedEye, lockedBasis, lockedFacing);
         scheduleLocalEyesForwardLine(level, ownerEntity, baseState, 9, 14, 15, lockedEye, lockedBasis, lockedFacing);
         scheduleLocalEyesForwardLine(level, ownerEntity, baseState, 10, 16, 17, lockedEye, lockedBasis, lockedFacing);
         scheduleLocalEyesForwardLine(level, ownerEntity, baseState, 11, 18, 25, lockedEye, lockedBasis, lockedFacing);
      }
   }

   private static void scheduleLocalEyesForwardLine(
      final ServerLevel level,
      final Entity ownerEntity,
      final BlockState baseState,
      int delayTicks,
      final int zStart,
      final int zEnd,
      final Vec3[] lockedEye,
      final HerobrineUtil.Basis[] lockedBasis,
      final Direction[] lockedFacing
   ) {
      new DelayedTask(delayTicks) {
         @Override
         public void run() {
            if (ownerEntity.m_6084_()) {
               if (ownerEntity.m_9236_() == level) {
                  if (lockedEye[0] == null) {
                     lockedEye[0] = ownerEntity.m_20299_(1.0F);
                     lockedBasis[0] = HerobrineUtil.basisFromEntity(ownerEntity);
                     lockedFacing[0] = ownerEntity.m_6350_();
                  }

                  BlockState stateNow = baseState;
                  if (stateNow.m_61138_(BlockStateProperties.f_61374_) && lockedFacing[0] != null) {
                     stateNow = (BlockState)stateNow.m_61124_(BlockStateProperties.f_61374_, lockedFacing[0]);
                  }

                  HerobrineUtil.Basis basis = lockedBasis[0];
                  Vec3 eye = lockedEye[0];
                  int from = Math.min(zStart, zEnd);
                  int to = Math.max(zStart, zEnd);

                  for (int z = from; z <= to; z++) {
                     Vec3 world = eye.m_82549_(basis.up().m_82490_(-1.0)).m_82549_(basis.fwd().m_82490_((double)z));
                     HerobrineUtil.placeIfReplaceable(level, BlockPos.m_274446_(world), stateNow, ownerEntity);
                  }
               }
            }
         }
      };
   }

   public static void summonShadowObsidianLongPillarCircle(ServerLevel level, Entity ownerEntity, BlockPos centerPos) {
      if (level != null && ownerEntity != null && centerPos != null) {
         if (ownerEntity.m_6084_()) {
            if (ownerEntity.m_9236_() == level) {
               BlockState longPillarState = (BlockState)((BlockState)((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_LONG_PILLAR.get())
                     .m_49966_()
                     .m_61124_(HerobrineObsidianBlock.FROM_PLAYER, ownerEntity instanceof Player))
                  .m_61124_(BlockStateProperties.f_61374_, ownerEntity.m_6350_());
               scheduleRing(level, ownerEntity, centerPos, longPillarState, 0, 6, 2.5, (float) (Math.PI * 2.0 / 5.0));
               scheduleRing(level, ownerEntity, centerPos, longPillarState, 2, 11, 3.5, (float) (Math.PI / 5));
               scheduleRing(level, ownerEntity, centerPos, longPillarState, 4, 14, 4.5, (float) (Math.PI / 10));
               scheduleRing(level, ownerEntity, centerPos, longPillarState, 6, 19, 5.5, 0.25132743F);
            }
         }
      }
   }

   private static void scheduleRing(
      final ServerLevel level,
      final Entity ownerEntity,
      final BlockPos centerPos,
      final BlockState blockState,
      int delayTicks,
      final int points,
      final double radius,
      final float angleOffset
   ) {
      new DelayedTask(delayTicks) {
         @Override
         public void run() {
            if (ownerEntity.m_6084_()) {
               if (ownerEntity.m_9236_() == level) {
                  int centerX = centerPos.m_123341_();
                  int centerZ = centerPos.m_123343_();

                  for (int k = 0; k < points; k++) {
                     float angle = (float)k * ((float) (Math.PI * 2) / (float)points) + angleOffset;
                     double worldX = (double)centerX + (double)Mth.m_14089_(angle) * radius;
                     double worldZ = (double)centerZ + (double)Mth.m_14031_(angle) * radius;
                     int x = Mth.m_14107_(worldX);
                     int z = Mth.m_14107_(worldZ);
                     int groundY = level.m_6924_(Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
                     BlockPos placePos = new BlockPos(x, groundY, z);
                     if (!level.m_8055_(placePos).m_247087_()) {
                        placePos = placePos.m_7494_();
                     }

                     HerobrineUtil.placeIfReplaceable(level, placePos, blockState, ownerEntity);
                  }
               }
            }
         }
      };
   }

   private static ItemStack createRandomModdedEnchantedBook() {
      List<Enchantment> pool = BuiltInRegistries.f_256876_.m_123024_().filter(enchantmentx -> !enchantmentx.m_6589_()).toList();
      if (pool.isEmpty()) {
         return new ItemStack(Items.f_42690_);
      } else {
         Enchantment enchantment = pool.get(new Random().nextInt(pool.size()));
         ItemStack book = new ItemStack(Items.f_42690_);
         EnchantedBookItem.m_41153_(book, new EnchantmentInstance(enchantment, new Random().nextInt(5, 10)));
         return book;
      }
   }

   public static void dropNullLoot(LevelAccessor world, double x, double y, double z) {
      if (world instanceof Level level && !level.m_5776_()) {
         Item[] drops = new Item[]{
            Items.f_42415_,
            Items.f_42415_,
            Items.f_42584_,
            Items.f_42522_,
            Items.f_42584_,
            Items.f_42584_,
            Items.f_42616_,
            Items.f_42690_,
            Items.f_42690_,
            Items.f_42690_,
            Items.f_42437_,
            Items.f_42418_,
            Items.f_42584_,
            Items.f_42437_,
            Items.f_42545_,
            Items.f_42710_
         };

         for (Item item : drops) {
            ItemStack stack = item == Items.f_42690_ ? createRandomModdedEnchantedBook() : new ItemStack(item);
            ItemEntity entity = new ItemEntity(level, x, y, z, stack);
            entity.m_32010_(10);
            level.m_7967_(entity);
         }

         return;
      }
   }

   public static void dropEliteHerobrineLoot(LevelAccessor world, double x, double y, double z, String fromElite) {
      if (world instanceof Level level && !level.m_5776_()) {
         Item[] items = new Item[]{
            ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
            ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
            ((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get()).m_5456_(),
            ((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get()).m_5456_(),
            Items.f_42545_,
            Items.f_42545_,
            Items.f_42545_,
            ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
            ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
            Items.f_42690_,
            Items.f_42690_,
            Items.f_42690_,
            Items.f_42413_
         };

         for (Item item : items) {
            ItemStack stack = item == Items.f_42690_ ? createRandomModdedEnchantedBook() : new ItemStack(item);
            ItemEntity entity = new ItemEntity(level, x, y, z, stack);
            entity.m_32010_(10);
            level.m_7967_(entity);
         }

         ItemStack eliteDrop = ItemStack.f_41583_;
         switch (fromElite) {
            case "EnderGlaive":
               eliteDrop = new ItemStack((ItemLike)AnnoyingVillagersModItems.ENDER_GLAIVE.get());
               break;
            case "ObsidianSledgehammer":
               eliteDrop = new ItemStack((ItemLike)AnnoyingVillagersModItems.OBSIDIAN_SLEDGEHAMMER.get());
               break;
            case "EnderSlayerScythe":
               eliteDrop = new ItemStack((ItemLike)AnnoyingVillagersModItems.ENDER_SLAYER_SCYTHE.get());
               break;
            case "EnderAegis":
               eliteDrop = new ItemStack((ItemLike)AnnoyingVillagersModItems.ENDER_AEGIS.get());
               break;
            case "DemoniacVoltageReaver":
               eliteDrop = new ItemStack((ItemLike)AnnoyingVillagersModItems.DEMONIAC_VOLTAGE_REAVER_HILT.get());
         }

         if (!eliteDrop.m_41619_()) {
            ItemEntity drop = new ItemEntity(level, x, y, z, eliteDrop);
            drop.m_32010_(10);
            level.m_7967_(drop);
         }

         return;
      }
   }

   public static void dropShadowHerobrineLoot(LevelAccessor world, double x, double y, double z) {
      if (world instanceof Level level && !level.m_5776_()) {
         if (!world.m_5776_() && world.m_7654_() != null) {
            world.m_7654_().m_6846_().m_240416_(Component.m_237115_("subtitles.shadow_herobrine_die"), false);
         }

         Item[] items = new Item[]{
            ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
            ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
            ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
            ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
            Items.f_42545_,
            Items.f_42545_,
            Items.f_42545_,
            ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
            ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
            Items.f_42690_,
            Items.f_42690_,
            Items.f_42690_,
            Items.f_42413_,
            (Item)AnnoyingVillagersModItems.ENCHANTED_ENDER_PEARL.get(),
            (Item)AnnoyingVillagersModItems.HEROBRINE_ENDER_EYE.get()
         };

         for (Item item : items) {
            ItemStack stack = item == Items.f_42690_ ? createRandomModdedEnchantedBook() : new ItemStack(item);
            ItemEntity entity = new ItemEntity(level, x, y, z, stack);
            entity.m_32010_(10);
            level.m_7967_(entity);
         }

         return;
      }
   }

   public static void dropHerobrine7Loot(LevelAccessor world, double x, double y, double z) {
      if (world instanceof Level level && !level.m_5776_()) {
         if (level.m_7654_() != null) {
            level.m_7654_().m_6846_().m_240416_(Component.m_237115_("subtitles.herobrine_clone_die"), false);
         }

         Item[] items = new Item[]{
            ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
            ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
            ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
            ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
            Items.f_42545_,
            Items.f_42545_,
            Items.f_42545_,
            ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
            ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
            Items.f_42690_,
            Items.f_42413_,
            (Item)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_PILLAR.get(),
            (Item)AnnoyingVillagersModItems.ENCHANTED_ENDER_PEARL.get()
         };

         for (Item item : items) {
            ItemStack stack = item == Items.f_42690_ ? createRandomModdedEnchantedBook() : new ItemStack(item);
            ItemEntity entity = new ItemEntity(level, x, y, z, stack);
            entity.m_32010_(10);
            level.m_7967_(entity);
         }

         return;
      }
   }

   public static void dropLowHerobrineCloneLoot(LevelAccessor world, double x, double y, double z) {
      if (world instanceof Level level && !level.m_5776_()) {
         Item[] items = new Item[]{
            Items.f_42415_,
            Items.f_42415_,
            Items.f_42416_,
            Items.f_42616_,
            Items.f_42616_,
            Items.f_42616_,
            Items.f_42616_,
            Items.f_42418_,
            Items.f_42584_,
            Items.f_42436_
         };

         for (Item item : items) {
            ItemEntity drop = new ItemEntity(level, x, y, z, new ItemStack(item));
            drop.m_32010_(10);
            level.m_7967_(drop);
         }

         return;
      }
   }

   public static void dropHerobrineCloneLoot(LevelAccessor world, double x, double y, double z) {
      if (world instanceof Level level && !level.m_5776_()) {
         if (!world.m_5776_() && world.m_7654_() != null) {
            world.m_7654_().m_6846_().m_240416_(Component.m_237115_("subtitles.herobrine_clone_die"), false);
         }

         Item[] items = new Item[]{
            Items.f_42415_,
            Items.f_42415_,
            Items.f_42710_,
            Items.f_42416_,
            Items.f_42616_,
            Items.f_42616_,
            Items.f_42437_,
            Items.f_42418_,
            Items.f_42584_,
            Items.f_42437_,
            Items.f_42545_,
            Items.f_41996_,
            Items.f_41996_,
            Items.f_42690_,
            (Item)AnnoyingVillagersModItems.OBSIDIAN_WEAPON.get(),
            (Item)AnnoyingVillagersModItems.ENCHANTED_ENDER_PEARL.get()
         };

         for (Item item : items) {
            ItemStack stack = item == Items.f_42690_ ? createRandomModdedEnchantedBook() : new ItemStack(item);
            ItemEntity entity = new ItemEntity(level, x, y, z, stack);
            entity.m_32010_(10);
            level.m_7967_(entity);
         }

         return;
      }
   }

   public static void dropHerobrineChrisLoot(LevelAccessor world, double x, double y, double z) {
      if (world instanceof ServerLevel serverLevel) {
         Item[] items = new Item[]{
            ((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get()).m_5456_(),
            ((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get()).m_5456_(),
            (Item)AnnoyingVillagersModItems.BEDROCK_WEAPON.get(),
            ((Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get()).m_5456_(),
            Blocks.f_50095_.m_5456_(),
            Blocks.f_50080_.m_5456_(),
            Blocks.f_50080_.m_5456_(),
            Items.f_42418_,
            Items.f_42584_,
            Items.f_42437_,
            Items.f_42545_,
            Items.f_42545_,
            (Item)AnnoyingVillagersModItems.ENCHANTED_ENDER_PEARL.get(),
            Items.f_42690_
         };

         for (Item item : items) {
            ItemStack stack = item == Items.f_42690_ ? createRandomModdedEnchantedBook() : new ItemStack(item);
            ItemEntity entity = new ItemEntity(serverLevel, x, y, z, stack);
            entity.m_32010_(10);
            serverLevel.m_7967_(entity);
         }
      }
   }

   public static void dropArmoredHerobrineLoot(LevelAccessor world, double x, double y, double z) {
      if (world instanceof Level level && !level.m_5776_()) {
         if (level instanceof ServerLevel serverLevel) {
            serverLevel.m_7654_().m_6846_().m_240416_(Component.m_237115_("subtitles.herobrine_clone_die"), false);
            Item[] items = new Item[]{
               ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
               ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
               ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
               ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
               ((Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()).m_5456_(),
               Items.f_42545_,
               Items.f_42545_,
               Items.f_42736_,
               Items.f_42690_,
               Blocks.f_50090_.m_5456_(),
               Items.f_42383_,
               (Item)AnnoyingVillagersModItems.ENCHANTED_ENDER_PEARL.get(),
               (Item)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get(),
               (Item)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get()
            };

            for (Item item : items) {
               ItemStack stack = item == Items.f_42690_ ? createRandomModdedEnchantedBook() : new ItemStack(item);
               ItemEntity entity = new ItemEntity(level, x, y, z, stack);
               entity.m_32010_(10);
               serverLevel.m_7967_(entity);
            }
         }

         return;
      }
   }

   public static void dropShadowHerobrineCloneLoot(LevelAccessor world, double x, double y, double z) {
      if (world instanceof Level level && !level.m_5776_()) {
         if (!world.m_5776_() && world.m_7654_() != null) {
            world.m_7654_().m_6846_().m_240416_(Component.m_237115_("subtitles.herobrine_clone_die"), false);
         }

         Item[] drops = new Item[]{
            Items.f_42415_,
            Items.f_42415_,
            Items.f_42710_,
            Items.f_42416_,
            Items.f_42616_,
            Items.f_42616_,
            Items.f_42437_,
            Items.f_42418_,
            Items.f_42584_,
            Items.f_42437_,
            Items.f_42545_,
            Items.f_41996_,
            Items.f_41996_,
            Items.f_42690_,
            (Item)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_WEAPON.get(),
            (Item)AnnoyingVillagersModItems.ENCHANTED_ENDER_PEARL.get()
         };

         for (Item item : drops) {
            ItemStack stack = item == Items.f_42690_ ? createRandomModdedEnchantedBook() : new ItemStack(item);
            ItemEntity entity = new ItemEntity(level, x, y, z, stack);
            entity.m_32010_(10);
            level.m_7967_(entity);
         }

         return;
      }
   }

   private static final class AssistanceSpiralFx {
      private final Vec3 origin;
      private final double seedAngle;
      private int age;

      private AssistanceSpiralFx(Vec3 origin, double seedAngle) {
         this.origin = origin;
         this.seedAngle = seedAngle;
      }

      private boolean tick(Level level) {
         if (this.age >= 34) {
            this.spawnTopBurst(level);
            return false;
         } else {
            RandomSource rand = level.m_213780_();
            double progress = (double)this.age / 34.0;
            double baseHeight = 0.12 + progress * 3.25;
            double turnAngle = this.seedAngle + progress * Math.PI * 7.0;

            for (int arm = 0; arm < 2; arm++) {
               double armAngle = turnAngle + (double)arm * Math.PI;

               for (int trail = 0; trail < 4; trail++) {
                  double trailProgress = Math.max(0.0, progress - (double)trail * 0.022);
                  double angle = armAngle - (double)trail * 0.42;
                  double radius = 1.15 * (1.0 - trailProgress * 0.48) + (rand.m_188500_() - 0.5) * 0.08;
                  double cos = Math.cos(angle);
                  double sin = Math.sin(angle);
                  Vec3 radial = new Vec3(cos, 0.0, sin);
                  Vec3 tangent = new Vec3(-sin, 0.0, cos);
                  Vec3 pos = this.origin
                     .m_82549_(radial.m_82490_(radius))
                     .m_82520_(0.0, baseHeight - (double)trail * 0.055 + (rand.m_188500_() - 0.5) * 0.07, 0.0);
                  Vec3 velocity = tangent.m_82490_(0.045 + rand.m_188500_() * 0.025)
                     .m_82549_(radial.m_82490_(-0.012))
                     .m_82520_(0.0, 0.045 + rand.m_188500_() * 0.035, 0.0);
                  HerobrineUtil.spawnParticle(level, ParticleTypes.f_123809_, pos, velocity);
                  if ((this.age + trail + arm) % 6 == 0) {
                     HerobrineUtil.spawnParticle(level, ParticleTypes.f_123810_, pos, velocity.m_82490_(0.45));
                  }
               }
            }

            for (int i = 0; i < 3; i++) {
               double angle = this.seedAngle - progress * Math.PI * 4.0 + rand.m_188500_() * Math.PI * 2.0;
               double radius = 0.22 + rand.m_188500_() * 0.55;
               double cos = Math.cos(angle);
               double sin = Math.sin(angle);
               Vec3 pos = this.origin.m_82520_(cos * radius, 0.08 + rand.m_188500_() * 0.22, sin * radius);
               Vec3 velocity = new Vec3(-sin, 0.0, cos).m_82490_(0.018).m_82520_(-cos * 0.01, 0.025 + rand.m_188500_() * 0.035, -sin * 0.01);
               HerobrineUtil.spawnParticle(level, ParticleTypes.f_123809_, pos, velocity);
            }

            this.age++;
            return true;
         }
      }

      private void spawnBaseBurst(Level level) {
         RandomSource rand = level.m_213780_();

         for (int i = 0; i < 36; i++) {
            double angle = this.seedAngle + (double)i / 36.0 * Math.PI * 2.0;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double radius = 0.35 + rand.m_188500_() * 0.85;
            Vec3 pos = this.origin.m_82520_(cos * radius, 0.08 + rand.m_188500_() * 0.18, sin * radius);
            Vec3 velocity = new Vec3(-sin, 0.0, cos)
               .m_82490_(0.055 + rand.m_188500_() * 0.035)
               .m_82520_(cos * 0.015, 0.04 + rand.m_188500_() * 0.045, sin * 0.015);
            HerobrineUtil.spawnParticle(level, ParticleTypes.f_123809_, pos, velocity);
            if ((i & 3) == 0) {
               HerobrineUtil.spawnParticle(level, ParticleTypes.f_123810_, pos, velocity.m_82490_(0.35));
            }
         }
      }

      private void spawnTopBurst(Level level) {
         RandomSource rand = level.m_213780_();
         Vec3 top = this.origin.m_82520_(0.0, 3.6, 0.0);

         for (int i = 0; i < 28; i++) {
            Vec3 offset = HerobrineUtil.randomUnit(rand).m_82490_(0.18 + rand.m_188500_() * 0.62);
            Vec3 pos = top.m_82549_(offset);
            Vec3 velocity = offset.m_82541_().m_82490_(0.025 + rand.m_188500_() * 0.045).m_82520_(0.0, 0.015 + rand.m_188500_() * 0.045, 0.0);
            HerobrineUtil.spawnParticle(level, ParticleTypes.f_123809_, pos, velocity);
            if ((i & 2) == 0) {
               HerobrineUtil.spawnParticle(level, ParticleTypes.f_123810_, pos, velocity.m_82490_(0.55));
            }
         }
      }
   }

   private static record Basis(Vec3 fwd, Vec3 right, Vec3 up) {
   }

   @OnlyIn(Dist.CLIENT)
   @EventBusSubscriber(
      modid = "annoyingvillagers",
      value = {Dist.CLIENT}
   )
   public static final class ClientEvents {
      private ClientEvents() {
      }

      @SubscribeEvent
      public static void onClientTick(ClientTickEvent event) {
         if (event.phase == Phase.END) {
            HerobrineUtil.tickHerobrineAssistanceFallbacks(Minecraft.m_91087_().f_91073_);
         }
      }
   }

   private static final class Pattern2D {
      final int w;
      final int h;
      final int[][] cells;

      Pattern2D(int w, int h, int[][] cells) {
         this.w = w;
         this.h = h;
         this.cells = cells;
      }

      int centerX() {
         return this.w / 2;
      }
   }
}
