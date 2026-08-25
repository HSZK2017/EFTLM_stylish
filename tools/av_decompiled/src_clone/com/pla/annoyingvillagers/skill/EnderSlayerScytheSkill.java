package com.pla.annoyingvillagers.skill;

import com.pla.annoyingvillagers.entity.HerobrineDragonEntity;
import com.pla.annoyingvillagers.gameasset.AVSkills;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightACG;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightIronSpell;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.item.EnderSlayerScytheItem;
import com.pla.annoyingvillagers.task.DelayedTask;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import reascer.wom.gameasset.animations.weapons.AnimsAgony;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class EnderSlayerScytheSkill extends WeaponInnateSkill {
   private static final UUID EVENT_UUID = UUID.fromString("f79be742-fddd-454d-bd28-4d030613b284");

   public EnderSlayerScytheSkill(SkillBuilder<? extends WeaponInnateSkill> builder) {
      super(builder);
   }

   private static Vec3 findOrbitSpawnPos(ServerLevel level, Player player, HerobrineDragonEntity dragon) {
      RandomSource rng = level.m_213780_();
      boolean hasCeiling = level.m_6042_().f_63856_();

      for (int i = 0; i < 32; i++) {
         double ang = rng.m_188500_() * (Math.PI * 2);
         double r = Mth.m_216263_(rng, 20.0, 50.0);
         double x = player.m_20185_() + Math.cos(ang) * r;
         double z = player.m_20189_() + Math.sin(ang) * r;
         if (!hasCeiling) {
            BlockPos col = BlockPos.m_274561_(x, 0.0, z);
            int groundY = level.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, col).m_123342_();
            double y = Math.max(player.m_20186_() + 12.0, (double)groundY + 18.0);
            y += Mth.m_216263_(rng, -2.0, 6.0);
            y = Mth.m_14008_(y, (double)level.m_141937_() + 6.0, (double)level.m_151558_() - 6.0);
            dragon.m_7678_(x, y, z, rng.m_188501_() * 360.0F, 0.0F);
            AABB box = dragon.m_20191_();
            if (level.m_45756_(dragon, box) && !level.m_46855_(box)) {
               return new Vec3(x, y, z);
            }
         } else {
            BlockPos col = BlockPos.m_274561_(x, 0.0, z);
            int roofAirY = level.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, col).m_123342_();
            double yWanted = player.m_20186_() + 12.0 + Mth.m_216263_(rng, -2.0, 6.0);
            double minY = (double)level.m_141937_() + 6.0;
            double maxY = Math.min((double)level.m_151558_() - 2.0, (double)roofAirY - 2.0);
            int yStart = Mth.m_14107_(Mth.m_14008_(yWanted, minY, maxY));
            int yMin = Mth.m_14107_(minY);

            for (int y = yStart; y >= yMin && yStart - y <= 96; y--) {
               dragon.m_7678_(x, (double)y, z, rng.m_188501_() * 360.0F, 0.0F);
               AABB box = dragon.m_20191_();
               if (level.m_45756_(dragon, box) && !level.m_46855_(box)) {
                  return new Vec3(x, (double)y, z);
               }
            }
         }
      }

      double fx = player.m_20185_();
      double fz = player.m_20189_();
      double fy = player.m_20186_() + 16.0;
      if (hasCeiling) {
         BlockPos col = BlockPos.m_274561_(fx, 0.0, fz);
         int roofAirY = level.m_5452_(Types.MOTION_BLOCKING_NO_LEAVES, col).m_123342_();
         fy = Math.min(fy, (double)roofAirY - 8.0);
      }

      fy = Mth.m_14008_(fy, (double)level.m_141937_() + 6.0, (double)level.m_151558_() - 6.0);
      int start = Mth.m_14107_(fy);

      for (int yx = start; yx >= level.m_141937_() + 6 && start - yx <= 64; yx--) {
         dragon.m_7678_(fx, (double)yx, fz, rng.m_188501_() * 360.0F, 0.0F);
         AABB box = dragon.m_20191_();
         if (level.m_45756_(dragon, box) && !level.m_46855_(box)) {
            return new Vec3(fx, (double)yx, fz);
         }
      }

      return player.m_20182_().m_82520_(0.0, 8.0, 0.0);
   }

   public void executeOnServer(SkillContainer skillContainer, FriendlyByteBuf friendlyByteBuf) {
      if (!skillContainer.isActivated()) {
         skillContainer.getExecutor().playAnimationSynchronized(AnimsWom.AGONY_GUARD_HIT_1, 0.0F);
         ((Player)skillContainer.getExecutor().getOriginal())
            .m_5496_((SoundEvent)AnnoyingVillagersModSounds.ELITE_HEROBRINE_WEAPON_SCREAMING.get(), 0.5F, 1.0F);
         super.executeOnServer(skillContainer, friendlyByteBuf);
         skillContainer.activate();
      }
   }

   public void onInitiate(SkillContainer container) {
      super.onInitiate(container);
      container.getExecutor()
         .getEventListener()
         .addEventListener(
            EventType.BASIC_ATTACK_EVENT,
            EVENT_UUID,
            event -> {
               if (!((ServerPlayerPatch)event.getPlayerPatch()).isLogicalClient()) {
                  SkillContainer skillContainer = ((ServerPlayerPatch)event.getPlayerPatch()).getSkill(this);
                  final ItemStack itemStack = ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).m_21205_();
                  ServerPlayerPatch serverPlayerPatch = skillContainer.getServerExecutor();
                  Player player = (Player)serverPlayerPatch.getOriginal();
                  if (skillContainer.isActivated()) {
                     event.setCanceled(true);
                     if (((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).m_36335_().m_41521_(itemStack.m_41720_(), 0.0F) == 0.0F
                        && itemStack.m_41720_() instanceof EnderSlayerScytheItem
                        && player.m_9236_() instanceof ServerLevel serverLevel
                        && player.getPersistentData().m_128441_("DragonUUID")) {
                        UUID dragonId = player.getPersistentData().m_128342_("DragonUUID");
                        Entity entity = serverLevel.m_8791_(dragonId);
                        if (entity == null) {
                           player.getPersistentData().m_128473_("DragonUUID");
                           return;
                        }

                        LivingEntity target = player.m_21214_();
                        if (target == null || !target.m_6084_() || target == player) {
                           target = player.m_21188_();
                        }

                        if (target == null || !target.m_6084_() || target == player) {
                           target = HerobrineDragonEntity.getNearestLivingEntity(player.m_9236_(), player, 48.0);
                        }

                        if (entity instanceof HerobrineDragonEntity herobrineDragonEntity && target != null && target.m_6084_()) {
                           skillContainer.getExecutor().playAnimationSynchronized(AnimsEpicFightIronSpell.CASTING_ONE_HAND_TOP, 0.0F);
                           final LivingEntity finalTarget = target;
                           new DelayedTask(10) {
                              @Override
                              public void run() {
                                 herobrineDragonEntity.shootThunderBreathAtTarget(finalTarget);
                                 ItemCooldowns cooldowns = ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).m_36335_();
                                 cooldowns.m_41524_(itemStack.m_41720_(), 120);
                              }
                           };
                        }
                     }
                  } else if (!skillContainer.isActivated()
                     && player.m_20159_()
                     && player.m_20202_() != null
                     && player.m_20202_() instanceof HerobrineDragonEntity) {
                     event.setCanceled(true);
                     if (player.m_21205_().m_41720_() instanceof BowItem) {
                        skillContainer.getExecutor().playAnimationSynchronized(AnimsEpicFightACG.BOW_AUTO_1, 0.0F);
                     } else {
                        skillContainer.getExecutor().playAnimationSynchronized(AnimsAgony.AGONY_AUTO_1, 0.0F);
                     }
                  }
               }
            }
         );
      container.getExecutor()
         .getEventListener()
         .addEventListener(
            EventType.SKILL_CAST_EVENT,
            EVENT_UUID,
            event -> {
               if (!event.getPlayerPatch().isLogicalClient()) {
                  SkillContainer skillContainer = event.getPlayerPatch().getSkill(this);
                  ItemStack itemStack = ((Player)event.getPlayerPatch().getOriginal()).m_21205_();
                  ServerPlayerPatch serverPlayerPatch = skillContainer.getServerExecutor();
                  Player player = (Player)serverPlayerPatch.getOriginal();
                  Skill skill = event.getSkillContainer().getSkill();
                  if (skillContainer.isActivated() && itemStack.m_41783_() != null && skill.getCategory() == SkillCategories.GUARD) {
                     event.setCanceled(true);
                     if (((Player)event.getPlayerPatch().getOriginal()).m_36335_().m_41521_(itemStack.m_41720_(), 0.0F) == 0.0F
                        && itemStack.m_41720_() instanceof EnderSlayerScytheItem
                        && player.m_9236_() instanceof ServerLevel serverLevel
                        && player.getPersistentData().m_128441_("DragonUUID")) {
                        UUID dragonId = player.getPersistentData().m_128342_("DragonUUID");
                        Entity entity = serverLevel.m_8791_(dragonId);
                        if (entity == null) {
                           player.getPersistentData().m_128473_("DragonUUID");
                           return;
                        }

                        LivingEntity target = player.m_21214_();
                        if (target == null || !target.m_6084_() || target == player) {
                           target = player.m_21188_();
                        }

                        if (target == null || !target.m_6084_() || target == player) {
                           target = HerobrineDragonEntity.getNearestLivingEntity(player.m_9236_(), player, 40.0);
                        }

                        ItemCooldowns cooldowns = ((Player)event.getPlayerPatch().getOriginal()).m_36335_();
                        cooldowns.m_41524_(itemStack.m_41720_(), 20);
                        if (entity instanceof HerobrineDragonEntity herobrineDragonEntity && target != null && target.m_6084_()) {
                           skillContainer.getExecutor().playAnimationSynchronized(AnimsEpicFightIronSpell.CASTING_ONE_HAND_BUFF, 0.0F);
                           final LivingEntity finalTarget = target;
                           new DelayedTask(10) {
                              @Override
                              public void run() {
                                 herobrineDragonEntity.shootMeteoriteAtTarget(finalTarget);
                              }
                           };
                        }
                     }
                  }
               }
            }
         );
      container.getExecutor().getEventListener().addEventListener(EventType.DODGE_SUCCESS_EVENT, EVENT_UUID, event -> {
         SkillContainer skillContainer = container.getExecutor().getSkill(AVSkills.ENDER_SLAYER_SCYTHE);
         if (skillContainer != null) {
            EnderSlayerScytheSkill enderSlayerScytheSkill = (EnderSlayerScytheSkill)skillContainer.getSkill();
            if (!skillContainer.isActivated() && skillContainer.getStack() < 1) {
               float currentResource = skillContainer.getResource();
               float neededResource = skillContainer.getNeededResource();
               float addResource = Math.min(10.0F, neededResource);
               enderSlayerScytheSkill.setConsumptionSynchronize(skillContainer, currentResource + addResource);
            } else if (skillContainer.isActivated()) {
               enderSlayerScytheSkill.setDurationSynchronize(skillContainer, skillContainer.getRemainDuration() + 80);
            }
         }
      });
   }

   public void cancelOnServer(SkillContainer container, FriendlyByteBuf args) {
      container.deactivate();
      super.cancelOnServer(container, args);
   }

   public void executeOnClient(SkillContainer container, FriendlyByteBuf args) {
      super.executeOnClient(container, args);
      container.activate();
   }

   public void cancelOnClient(SkillContainer container, FriendlyByteBuf args) {
      super.cancelOnClient(container, args);
      container.deactivate();
   }

   public boolean canExecute(SkillContainer container) {
      ItemStack itemstack = ((Player)container.getExecutor().getOriginal()).m_21205_();
      return EpicFightCapabilities.getItemStackCapability(itemstack).getInnateSkill(container.getExecutor(), itemstack) == this
         && (
            ((Player)container.getExecutor().getOriginal()).m_20202_() == null
               || ((Player)container.getExecutor().getOriginal()).m_20202_() != null
                  && ((Player)container.getExecutor().getOriginal()).m_20202_() instanceof HerobrineDragonEntity
         )
         && (!this.isActivated(container) || this.activateType == ActivateType.TOGGLE);
   }

   public void onRemoved(SkillContainer container) {
      container.getExecutor().getEventListener().removeListener(EventType.BASIC_ATTACK_EVENT, EVENT_UUID);
      container.getExecutor().getEventListener().removeListener(EventType.SKILL_CAST_EVENT, EVENT_UUID);
      container.getExecutor().getEventListener().removeListener(EventType.DODGE_SUCCESS_EVENT, EVENT_UUID);
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (!container.getExecutor().isLogicalClient()) {
         ServerPlayerPatch serverPlayerPatch = container.getServerExecutor();
         Player player = (Player)serverPlayerPatch.getOriginal();
         if (player.m_9236_() instanceof ServerLevel serverLevel) {
            if (player.f_19797_ % 5 == 0) {
               ItemStack main = player.m_21205_();
               if (main.m_41720_() instanceof EnderSlayerScytheItem) {
                  if (player.getPersistentData().m_128441_("DragonUUID")) {
                     UUID id = player.getPersistentData().m_128342_("DragonUUID");
                     Entity entity = serverLevel.m_8791_(id);
                     if (!(entity instanceof HerobrineDragonEntity) || entity.m_213877_()) {
                        player.getPersistentData().m_128473_("DragonUUID");
                     }
                  }

                  if (!player.getPersistentData().m_128441_("DragonUUID")) {
                     HerobrineDragonEntity herobrineDragonEntity = this.spawnEnderDragon(player, serverLevel);
                     if (herobrineDragonEntity != null) {
                        player.getPersistentData().m_128362_("DragonUUID", herobrineDragonEntity.m_20148_());
                     }
                  }
               }
            }
         }
      }
   }

   private HerobrineDragonEntity spawnEnderDragon(Player player, ServerLevel serverLevel) {
      if (!player.m_6084_()) {
         return null;
      } else {
         HerobrineDragonEntity herobrineDragonEntity = new HerobrineDragonEntity(
            (EntityType<? extends HerobrineDragonEntity>)AnnoyingVillagersModEntities.HEROBRINE_DRAGON.get(), serverLevel
         );
         Vec3 spawnPos = findOrbitSpawnPos(serverLevel, player, herobrineDragonEntity);
         herobrineDragonEntity.m_7678_(spawnPos.f_82479_, spawnPos.f_82480_, spawnPos.f_82481_, serverLevel.m_213780_().m_188501_() * 360.0F, 0.0F);
         herobrineDragonEntity.setSummoner(player);
         herobrineDragonEntity.setSummonerUUID(player.m_20148_());
         serverLevel.m_7967_(herobrineDragonEntity);
         return herobrineDragonEntity;
      }
   }
}
