package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.entity.BlackFireEntity;
import com.pla.annoyingvillagers.entity.ElectricPhaseEntity;
import com.pla.annoyingvillagers.entity.HerobrineDragonEntity;
import com.pla.annoyingvillagers.gameasset.AVAnimations;
import com.pla.annoyingvillagers.gameasset.AVSkills;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightACG;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightBattleArts;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightInfernalGainer;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightIronSpell;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.gameasset.AnimsSculkSteve;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.item.HerobrineEnderEyeItem;
import com.pla.annoyingvillagers.item.HookGunItem;
import com.pla.annoyingvillagers.item.TransporterFragmentItem;
import com.pla.annoyingvillagers.skill.EnderGlaiveSkill;
import com.pla.annoyingvillagers.skill.EnderSlayerScytheSkill;
import com.pla.annoyingvillagers.skill.LegendarySwordSkill;
import com.pla.annoyingvillagers.skill.NullWeaponSkill;
import com.pla.annoyingvillagers.skill.ObsidianSledgeHammerSkill;
import com.pla.annoyingvillagers.skill.ObsidianWeaponSkill;
import com.pla.annoyingvillagers.skill.ShadowObsidianPillarSkill;
import com.pla.annoyingvillagers.skill.TridentFestivalSkill;
import com.pla.annoyingvillagers.skill.WoopieTheSwordSkill;
import com.pla.annoyingvillagers.task.DelayedTask;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.fml.ModList;
import reascer.wom.gameasset.WOMAnimations;
import reascer.wom.gameasset.animations.weapons.AnimsAgony;
import reascer.wom.gameasset.animations.weapons.AnimsHerrscher;
import reascer.wom.gameasset.animations.weapons.AnimsRuine;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.effect.EpicFightMobEffects;

public class SpecialAttackOnKeyPressedEvent {
   private static void registerMoreSpecialAttackCategories(PlayerPatch<?> playerpatch, Entity entity, LivingEntityPatch<?> livingEntityPatch) {
   }

   private static void playHookGunBindAnimationAfterHandRefresh(final Player player) {
      new DelayedTask(2) {
         @Override
         public void run() {
            if (!player.m_213877_() && !player.m_9236_().m_5776_()) {
               LivingEntityPatch<?> freshPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(player, LivingEntityPatch.class);
               if (freshPatch != null) {
                  freshPatch.playAnimationSynchronized(AnimsPugilistSteve.HOOK_GUN, 0.0F);
               }
            }
         }
      };
   }

   private static void playTransporterFragmentAnimation(Player player, LivingEntityPatch<?> livingEntityPatch, TransporterFragmentItem.UseMode useMode) {
      switch (useMode) {
         case BOTH_HANDS:
         case MAIN_HAND:
            livingEntityPatch.playAnimationSynchronized(AnimsSculkSteve.PORTAL_SUMMON, 0.0F);
            break;
         case OFF_HAND:
            livingEntityPatch.playAnimationSynchronized(AnimsEpicFightIronSpell.CASTING_ONE_HAND_TOP, 0.0F);
         case NONE:
      }
   }

   public static void execute(LevelAccessor world, Entity entity) {
      execute(world, entity, null);
   }

   public static void execute(LevelAccessor world, Entity entity, Vec3 crosshairTarget) {
      if (entity != null) {
         PlayerPatch<?> playerpatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(entity, PlayerPatch.class);
         final LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
         if (livingEntityPatch != null) {
            AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(livingEntityPatch.getAnimator().getPlayerFor(null))
               .getRealAnimation();
            if (!EpicfightUtil.isLongHitAnimation(dynamicAnimation, livingEntityPatch)) {
               if (entity instanceof Player player && !player.m_9236_().m_5776_() && HookGunItem.tryBindFromSpecialAttack(player)) {
                  playHookGunBindAnimationAfterHandRefresh(player);
                  return;
               }

               if (entity instanceof Player player && !player.m_9236_().m_5776_()) {
                  TransporterFragmentItem.UseResult transporterUseResult = TransporterFragmentItem.tryUseSpecialAttack(player, crosshairTarget);
                  if (transporterUseResult.consumed()) {
                     if (transporterUseResult.activated()) {
                        playTransporterFragmentAnimation(player, livingEntityPatch, transporterUseResult.mode());
                     }

                     return;
                  }
               }

               if (entity instanceof Player playerx) {
                  ItemStack holdingItem = playerx.m_21205_();
                  ItemStack offHandItem = playerx.m_21206_();
                  if (holdingItem.m_41720_().equals(AnnoyingVillagersModItems.BLACK_FIRE_SWORD.get())) {
                     PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(playerx, PlayerPatch.class);
                     if (playerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
                        SkillContainer skillContainer = serverPlayerPatch.getSkill(AVSkills.BLACK_FIRE_SWORD);
                        if (skillContainer != null && entity.m_9236_() instanceof ServerLevel serverLevel && skillContainer.getResource() >= 5.0F) {
                           Skill.setSkillConsumptionSynchronize(skillContainer, skillContainer.getResource() - 5.0F);
                           BlackFireEntity.spawnOnOwnerSword(serverLevel, playerx);
                           return;
                        }
                     }
                  }

                  if (holdingItem.m_41720_().equals(AnnoyingVillagersModItems.THUNDER_DIAMOND_BLADE.get())) {
                     PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(playerx, PlayerPatch.class);
                     if (playerPatch instanceof ServerPlayerPatch serverPlayerPatchx) {
                        SkillContainer skillContainer = serverPlayerPatchx.getSkill(AVSkills.THUNDER_DIAMOND_BLADE);
                        if (skillContainer != null && entity.m_9236_() instanceof ServerLevel serverLevel && skillContainer.getResource() >= 10.0F) {
                           Skill.setSkillConsumptionSynchronize(skillContainer, skillContainer.getResource() - 10.0F);
                           ElectricPhaseEntity.spawnOnOwnerSword(serverLevel, playerx);
                           return;
                        }

                        skillContainer = serverPlayerPatchx.getSkill(AVSkills.DUAL_THUNDER_DIAMOND_BLADE);
                        if (skillContainer != null && entity.m_9236_() instanceof ServerLevel serverLevel && skillContainer.getResource() >= 10.0F) {
                           Skill.setSkillConsumptionSynchronize(skillContainer, skillContainer.getResource() - 10.0F);
                           ElectricPhaseEntity.spawnOnOwnerSword(serverLevel, playerx);
                           if (offHandItem.m_41720_().equals(AnnoyingVillagersModItems.THUNDER_DIAMOND_BLADE.get())) {
                              ElectricPhaseEntity.spawnOnOwnerSword(serverLevel, playerx, true);
                           }

                           return;
                        }
                     }
                  }
               }

               if (!(entity.m_9236_() instanceof ServerLevel) || dynamicAnimation == Animations.EMPTY_ANIMATION) {
                  if (entity instanceof Player playerx
                     && !playerx.m_9236_().m_5776_()
                     && !playerx.m_21205_().m_41720_().equals(AnnoyingVillagersModItems.HEROBRINE_ENDER_EYE.get())
                     && !playerx.m_21206_().m_41720_().equals(AnnoyingVillagersModItems.HEROBRINE_ENDER_EYE.get())) {
                     playerx.m_150109_()
                        .f_35974_
                        .stream()
                        .filter(s -> !s.m_41619_() && s.m_150930_((Item)AnnoyingVillagersModItems.HEROBRINE_ENDER_EYE.get()))
                        .findFirst()
                        .map(stack -> {
                           if (stack.m_41720_() instanceof HerobrineEnderEyeItem herobrineEnderEyeItem) {
                              ItemCooldowns cooldowns = player.m_36335_();
                              if (cooldowns.m_41519_(herobrineEnderEyeItem)) {
                                 return false;
                              } else {
                                 HerobrineEnderEyeItem.spawnAndShootDarkObPillars((ServerLevel)player.m_9236_(), player, 10);
                                 player.m_36335_().m_41524_(herobrineEnderEyeItem, 40);
                                 stack.m_41622_(5, player, p -> {
                                 });
                                 return true;
                              }
                           } else {
                              return false;
                           }
                        });
                  }

                  if (entity instanceof Player playerx) {
                     ItemStack holdingItemx = playerx.m_21205_();
                     ItemStack offHandItemx = playerx.m_21206_();
                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.BLUE_DEMON_TRIDENT.get()) && entity.m_9236_() instanceof ServerLevel) {
                        if (offHandItemx.m_41720_().equals(AnnoyingVillagersModItems.BLUE_DEMON_TRIDENT.get())) {
                           livingEntityPatch.playAnimationSynchronized(AnimsWom.CUT_ENDERBLASTER_TWOHAND_RELOAD, 0.0F);
                           PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(playerx, PlayerPatch.class);
                           if (playerPatch instanceof ServerPlayerPatch serverPlayerPatchx) {
                              SkillContainer skillContainerx = serverPlayerPatchx.getSkill(AVSkills.TRIDENT_FESTIVAL);
                              if (skillContainerx != null && skillContainerx.getSkill() instanceof TridentFestivalSkill tridentFestivalSkill) {
                                 tridentFestivalSkill.toggleMode(skillContainerx);
                              }
                           }
                        } else {
                           livingEntityPatch.playAnimationSynchronized(AnimsEpicFightBattleArts.TRIDENT_THROW_3, 0.0F);
                        }

                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.ENDER_AEGIS.get()) && entity.m_9236_() instanceof ServerLevel) {
                        livingEntityPatch.playAnimationSynchronized(AnimsWom.ENDER_AEGIS_BULL_CHARGE, 0.0F);
                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.EARTH_AXE.get()) && entity.m_9236_() instanceof ServerLevel) {
                        livingEntityPatch.playAnimationSynchronized(AVAnimations.EARTH_AXE_SHOOT, 0.0F);
                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.TWIN_DIAMOND_SPEAR.get()) && entity.m_9236_() instanceof ServerLevel) {
                        livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.SPEAR_THRUST, 0.0F);
                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.ENDER_GLAIVE.get()) && entity.m_9236_() instanceof ServerLevel) {
                        boolean success = false;
                        PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(playerx, PlayerPatch.class);
                        if (playerPatch instanceof ServerPlayerPatch serverPlayerPatchxx) {
                           SkillContainer skillContainerx = serverPlayerPatchxx.getSkill(AVSkills.ENDER_GLAIVE);
                           if (skillContainerx != null
                              && skillContainerx.getSkill() instanceof EnderGlaiveSkill enderGlaiveSkill
                              && skillContainerx.getStack() >= 1) {
                              livingEntityPatch.playAnimationSynchronized(AnimsWom.ENDER_GLAIVE_NAPOLEON_SHOOT_3, 0.0F);
                              enderGlaiveSkill.getResourceType()
                                 .consumer
                                 .consume(skillContainerx, serverPlayerPatchxx, enderGlaiveSkill.getDefaultConsumptionAmount(serverPlayerPatchxx));
                              success = true;
                           }
                        }

                        if (!success) {
                           livingEntityPatch.playAnimationSynchronized(AnimsAgony.AGONY_RISING_EAGLE, 0.0F);
                           new DelayedTask(10) {
                              @Override
                              public void run() {
                                 livingEntityPatch.playAnimationSynchronized(AnimsAgony.AGONY_RIPPING_FANGS, 0.0F);
                              }
                           };
                        }

                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.DEMONIAC_VOLTAGE_REAVER.get())) {
                        if (entity.m_9236_() instanceof ServerLevel && holdingItemx.m_41783_() != null && !holdingItemx.m_41783_().m_128471_("SnakeAnimation")) {
                           livingEntityPatch.playAnimationSynchronized(WOMAnimations.TORMENT_CHARGED_ATTACK_1, 0.0F);
                        }

                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.OBSIDIAN_SLEDGEHAMMER.get()) && entity.m_9236_() instanceof ServerLevel) {
                        boolean successx = false;
                        PlayerPatch<?> playerPatchx = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(playerx, PlayerPatch.class);
                        if (playerPatchx instanceof ServerPlayerPatch serverPlayerPatchxxx) {
                           SkillContainer skillContainerx = serverPlayerPatchxxx.getSkill(AVSkills.OBSIDIAN_SLEDGEHAMMER);
                           if (skillContainerx != null && skillContainerx.getSkill() instanceof ObsidianSledgeHammerSkill && skillContainerx.isActivated()) {
                              livingEntityPatch.playAnimationSynchronized(AnimsWom.SLEDGEHAMMER_SOLAR_AUTO_3, 0.0F);
                              successx = true;
                           }
                        }

                        if (!successx) {
                           livingEntityPatch.playAnimationSynchronized(WOMAnimations.TORMENT_BERSERK_DASH, 0.0F);
                        }

                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.ENDER_SLAYER_SCYTHE.get()) && entity.m_9236_() instanceof ServerLevel) {
                        PlayerPatch<?> playerPatchxx = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(playerx, PlayerPatch.class);
                        if (playerPatchxx instanceof ServerPlayerPatch serverPlayerPatchxxxx) {
                           SkillContainer skillContainerx = serverPlayerPatchxxxx.getSkill(AVSkills.ENDER_SLAYER_SCYTHE);
                           if (skillContainerx != null
                              && entity.m_9236_() instanceof ServerLevel serverLevel
                              && entity.m_20096_()
                              && skillContainerx.getSkill() instanceof EnderSlayerScytheSkill
                              && entity.getPersistentData().m_128441_("DragonUUID")
                              && !playerx.m_36335_().m_41519_(holdingItemx.m_41720_())
                              && serverLevel.m_8791_(playerx.getPersistentData().m_128342_("DragonUUID")) instanceof HerobrineDragonEntity herobrineDragonEntity
                              && herobrineDragonEntity.m_20197_().isEmpty()) {
                              livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.POSE_UP, 0.0F);
                              herobrineDragonEntity.recallAndLand(true);
                              playerx.m_36335_().m_41524_(holdingItemx.m_41720_(), 60);
                           }
                        }

                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.NULL_WEAPON.get()) && entity.m_9236_() instanceof ServerLevel) {
                        PlayerPatch<?> playerPatchxx = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(playerx, PlayerPatch.class);
                        if (playerPatchxx instanceof ServerPlayerPatch serverPlayerPatchxxxxx) {
                           SkillContainer skillContainerx = serverPlayerPatchxxxxx.getSkill(AVSkills.NULL_WEAPON);
                           if (skillContainerx != null && skillContainerx.getSkill() instanceof NullWeaponSkill && !skillContainerx.isActivated()) {
                              livingEntityPatch.playAnimationSynchronized(AnimsWom.CLONE_ANTITHEUS_SHOOT, 0.0F);
                           } else {
                              livingEntityPatch.playAnimationSynchronized(AnimsWom.NULL_SKELETON_ANTITHEUS_ASCENSION, 0.0F);
                           }
                        }

                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.OBSIDIAN_WEAPON.get()) && entity.m_9236_() instanceof ServerLevel) {
                        boolean successxx = false;
                        PlayerPatch<?> playerPatchxx = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(playerx, PlayerPatch.class);
                        if (playerPatchxx instanceof ServerPlayerPatch serverPlayerPatchxxxxxx) {
                           SkillContainer skillContainerx = serverPlayerPatchxxxxxx.getSkill(AVSkills.OBSIDIAN_WEAPON);
                           if (skillContainerx != null
                              && skillContainerx.getStack() >= 1
                              && entity.m_9236_() instanceof ServerLevel
                              && skillContainerx.getSkill() instanceof ObsidianWeaponSkill obsidianWeaponSkill) {
                              successxx = true;
                              obsidianWeaponSkill.getResourceType()
                                 .consumer
                                 .consume(skillContainerx, serverPlayerPatchxxxxxx, obsidianWeaponSkill.getDefaultConsumptionAmount(serverPlayerPatchxxxxxx));
                           }
                        }

                        if (successxx) {
                           livingEntityPatch.playAnimationSynchronized(AnimsWom.OBSIDIAN_ANTITHEUS_ASCENDED_DEATHFALL, 0.0F);
                        } else {
                           livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.OBSIDIAN_FIST_DASH, 0.0F);
                        }

                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.SHADOW_OBSIDIAN_WEAPON.get()) && entity.m_9236_() instanceof ServerLevel) {
                        boolean successxxx = false;
                        PlayerPatch<?> playerPatchxxx = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(playerx, PlayerPatch.class);
                        if (playerPatchxxx instanceof ServerPlayerPatch serverPlayerPatchxxxxxxx) {
                           SkillContainer skillContainerx = serverPlayerPatchxxxxxxx.getSkill(AVSkills.OBSIDIAN_WEAPON);
                           if (skillContainerx != null
                              && skillContainerx.getStack() >= 1
                              && entity.m_9236_() instanceof ServerLevel
                              && skillContainerx.getSkill() instanceof ObsidianWeaponSkill obsidianWeaponSkill) {
                              successxxx = true;
                              obsidianWeaponSkill.getResourceType()
                                 .consumer
                                 .consume(skillContainerx, serverPlayerPatchxxxxxxx, obsidianWeaponSkill.getDefaultConsumptionAmount(serverPlayerPatchxxxxxxx));
                           }
                        }

                        if (successxxx) {
                           livingEntityPatch.playAnimationSynchronized(AnimsWom.OBSIDIAN_ANTITHEUS_ASCENDED_DEATHFALL, 0.0F);
                        } else {
                           livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.OBSIDIAN_FIST_DASH, 0.0F);
                        }

                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.BEDROCK_WEAPON.get()) && entity.m_9236_() instanceof ServerLevel) {
                        livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.SUPER_PUNCH, 0.0F);
                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.SHADOW_OBSIDIAN_PILLAR.get()) && entity.m_9236_() instanceof ServerLevel) {
                        boolean successxxxx = false;
                        PlayerPatch<?> playerPatchxxxx = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(playerx, PlayerPatch.class);
                        if (playerPatchxxxx instanceof ServerPlayerPatch serverPlayerPatchxxxxxxxx) {
                           SkillContainer skillContainerx = serverPlayerPatchxxxxxxxx.getSkill(AVSkills.SHADOW_OBSIDIAN_PILLAR);
                           if (skillContainerx != null
                              && skillContainerx.getStack() >= 1
                              && entity.m_9236_() instanceof ServerLevel
                              && skillContainerx.getSkill() instanceof ShadowObsidianPillarSkill shadowObsidianPillarSkill) {
                              successxxxx = true;
                              shadowObsidianPillarSkill.getResourceType()
                                 .consumer
                                 .consume(
                                    skillContainerx,
                                    serverPlayerPatchxxxxxxxx,
                                    shadowObsidianPillarSkill.getDefaultConsumptionAmount(serverPlayerPatchxxxxxxxx)
                                 );
                           }
                        }

                        if (successxxxx) {
                           livingEntityPatch.playAnimationSynchronized(AnimsWom.OBSIDIAN_ANTITHEUS_ASCENDED_DEATHFALL, 0.0F);
                        } else {
                           livingEntityPatch.playAnimationSynchronized(AnimsEpicFightInfernalGainer.OBSIDIAN_INFERNAL_AUTO_2, 0.0F);
                        }

                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get()) && entity.m_9236_() instanceof ServerLevel) {
                        if (offHandItemx.m_41720_().equals(AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get())) {
                           livingEntityPatch.playAnimationSynchronized(AnimsWom.SHADOW_OBSIDIAN_SWORD_GESETZ_AUTO_3, 0.0F);
                        } else {
                           livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.OBSIDIAN_FIST_DASH, 0.0F);
                        }

                        return;
                     }

                     if ((
                           holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.HEROBRINE_ENDER_EYE.get())
                              || offHandItemx.m_41720_().equals(AnnoyingVillagersModItems.HEROBRINE_ENDER_EYE.get())
                        )
                        && entity.m_9236_() instanceof ServerLevel) {
                        livingEntityPatch.playAnimationSynchronized(Animations.BIPED_LANDING, 0.0F);
                        HerobrineEnderEyeItem.startShadowObsidianMachineGun((ServerLevel)playerx.m_9236_(), playerx);
                        if (playerx.m_21205_().m_41720_().equals(AnnoyingVillagersModItems.HEROBRINE_ENDER_EYE.get())) {
                           playerx.m_21205_().m_41622_(10, playerx, p -> {
                           });
                        } else if (playerx.m_21206_().m_41720_().equals(AnnoyingVillagersModItems.HEROBRINE_ENDER_EYE.get())) {
                           playerx.m_21206_().m_41622_(10, playerx, p -> {
                           });
                        }

                        return;
                     }

                     if (holdingItemx.m_41720_() instanceof BowItem && entity.m_9236_() instanceof ServerLevel) {
                        livingEntityPatch.playAnimationSynchronized(AnimsEpicFightACG.BOW_AUTO_2, 0.0F);
                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.LEGENDARY_SWORD.get()) && entity.m_9236_() instanceof ServerLevel) {
                        boolean successxxxxx = false;
                        boolean holdingTridentOffhand = offHandItemx.m_41720_().equals(AnnoyingVillagersModItems.BLUE_DEMON_TRIDENT.get());
                        PlayerPatch<?> playerPatchxxxxx = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(playerx, PlayerPatch.class);
                        if (playerPatchxxxxx instanceof ServerPlayerPatch serverPlayerPatchxxxxxxxxx) {
                           SkillContainer skillContainerx = serverPlayerPatchxxxxxxxxx.getSkill(AVSkills.LEGENDARY_SWORD);
                           if (skillContainerx != null
                              && skillContainerx.getSkill() instanceof LegendarySwordSkill legendarySwordSkill
                              && playerx.m_9236_() instanceof ServerLevel
                              && skillContainerx.getStack() >= 1) {
                              if (holdingTridentOffhand) {
                                 livingEntityPatch.playAnimationSynchronized(AnimsWom.ELECTRIC_FIELD, 0.0F);
                              } else {
                                 livingEntityPatch.playAnimationSynchronized(AnimsWom.YELLOW_TORMENT_CHARGED_ATTACK_3, 0.0F);
                              }

                              legendarySwordSkill.getResourceType()
                                 .consumer
                                 .consume(
                                    skillContainerx, serverPlayerPatchxxxxxxxxx, legendarySwordSkill.getDefaultConsumptionAmount(serverPlayerPatchxxxxxxxxx)
                                 );
                              successxxxxx = true;
                           }
                        }

                        if (!successxxxxx) {
                           if (holdingTridentOffhand) {
                              livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.TRIDENT_THROW_LEGENDARY, 0.0F);
                           } else {
                              playerx.m_7292_(new MobEffectInstance((MobEffect)EpicFightMobEffects.STUN_IMMUNITY.get(), 60, 2));
                              livingEntityPatch.playAnimationSynchronized(AnimsWom.CLONE_NAPOLEON_WATERLOW_SHOOT, 0.0F);
                           }
                        }

                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.WOOPIE_THE_SWORD.get()) && entity.m_9236_() instanceof ServerLevel) {
                        boolean successxxxxxx = false;
                        PlayerPatch<?> playerPatchxxxxxx = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(playerx, PlayerPatch.class);
                        if (playerPatchxxxxxx instanceof ServerPlayerPatch serverPlayerPatchxxxxxxxxxx) {
                           SkillContainer skillContainerx = serverPlayerPatchxxxxxxxxxx.getSkill(AVSkills.WOOPIE_THE_SWORD);
                           if (skillContainerx != null
                              && skillContainerx.getStack() == 1
                              && entity.m_9236_() instanceof ServerLevel
                              && skillContainerx.getSkill() instanceof WoopieTheSwordSkill woopieTheSwordSkill) {
                              successxxxxxx = true;
                              woopieTheSwordSkill.getResourceType()
                                 .consumer
                                 .consume(
                                    skillContainerx, serverPlayerPatchxxxxxxxxxx, woopieTheSwordSkill.getDefaultConsumptionAmount(serverPlayerPatchxxxxxxxxxx)
                                 );
                           }
                        }

                        if (successxxxxxx) {
                           livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.RUSH_SWORD, 0.0F);
                        } else {
                           livingEntityPatch.playAnimationSynchronized(AnimsRuine.RUINE_AUTO_4, 0.0F);
                        }

                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.GREAT_SWORD.get()) && entity.m_9236_() instanceof ServerLevel) {
                        livingEntityPatch.playAnimationSynchronized(AnimsHerrscher.HERRSCHER_AUTO_2, 0.0F);
                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.WOODEN_DOOR.get()) && entity.m_9236_() instanceof ServerLevel) {
                        livingEntityPatch.playAnimationSynchronized(WOMAnimations.TORMENT_CHARGED_ATTACK_2, 0.0F);
                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.CRAFTING_TABLE.get()) && entity.m_9236_() instanceof ServerLevel) {
                        livingEntityPatch.playAnimationSynchronized(WOMAnimations.TORMENT_AIRSLAM, 0.0F);
                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.LADDER.get()) && entity.m_9236_() instanceof ServerLevel) {
                        livingEntityPatch.playAnimationSynchronized(Animations.VINDICATOR_SWING_AXE3, 0.0F);
                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.TRAPDOOR.get()) && entity.m_9236_() instanceof ServerLevel) {
                        livingEntityPatch.playAnimationSynchronized(Animations.VINDICATOR_SWING_AXE2, 0.0F);
                        return;
                     }

                     if (holdingItemx.m_41720_().equals(AnnoyingVillagersModItems.BLUE_FLAME_SWORD.get())
                        && entity.m_9236_() instanceof ServerLevel
                        && playerx.m_9236_() instanceof ServerLevel level) {
                        double reach = playerx.getBlockReach();
                        HitResult hitResult = playerx.m_19907_(reach, 0.0F, false);
                        if (hitResult.m_6662_() == Type.BLOCK) {
                           BlockHitResult blockHit = (BlockHitResult)hitResult;
                           BlockPos lookedPos = blockHit.m_82425_();
                           BlockState lookedState = level.m_8055_(lookedPos);
                           if (lookedState.m_60713_(Blocks.f_50135_) || lookedState.m_60713_(Blocks.f_50136_)) {
                              BlockPos firePos = lookedPos.m_7494_();
                              if (level.m_46859_(firePos)) {
                                 BlockState soulFireState = Blocks.f_50084_.m_49966_();
                                 if (soulFireState.m_60710_(level, firePos)) {
                                    level.m_7731_(firePos, soulFireState, 3);
                                    livingEntityPatch.playAnimationSynchronized(AVAnimations.BLUE_FLAME_SWORD, 0.0F);
                                    holdingItemx.m_41622_(1, playerx, serverPlayer1 -> serverPlayer1.m_21190_(InteractionHand.MAIN_HAND));
                                    return;
                                 }
                              }
                           }
                        }
                     }

                     if (playerpatch == null) {
                        return;
                     }

                     ResourceLocation key = BuiltInRegistries.f_257033_.m_7981_(holdingItemx.m_41720_());
                     if (ModList.get().isLoaded("efn") && key.m_135827_().equals("efn")) {
                        return;
                     }

                     if (playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WeaponCategories.AXE
                        && entity.m_9236_() instanceof ServerLevel) {
                        if (!entity.getPersistentData().m_128441_("AxeCombo")) {
                           livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.AXE_HEAVY_AUTO_1, 0.0F);
                           entity.getPersistentData().m_128347_("AxeCombo", 1.0);
                        } else if (entity.getPersistentData().m_128459_("AxeCombo") == 1.0) {
                           livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.AXE_HEAVY_AUTO_2, 0.0F);
                           entity.getPersistentData().m_128347_("AxeCombo", 2.0);
                        } else if (entity.getPersistentData().m_128459_("AxeCombo") == 2.0) {
                           livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.AXE_FUN_SKILL, 0.0F);
                           entity.getPersistentData().m_128473_("AxeCombo");
                        }

                        return;
                     }

                     if ((
                           playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WeaponCategories.SWORD
                              || playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WeaponCategories.LONGSWORD
                              || playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WeaponCategories.TACHI
                              || playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WeaponCategories.DAGGER
                        )
                        && (
                           playerpatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() == WeaponCategories.SWORD
                              || playerpatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() == WeaponCategories.TACHI
                              || playerpatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() == WeaponCategories.AXE
                        )
                        && entity.m_9236_() instanceof ServerLevel) {
                        if (!entity.getPersistentData().m_128441_("DualSwordCombo")) {
                           livingEntityPatch.playAnimationSynchronized(Animations.DAGGER_DUAL_DASH, 0.0F);
                           entity.getPersistentData().m_128347_("DualSwordCombo", 1.0);
                        } else if (entity.getPersistentData().m_128459_("DualSwordCombo") == 1.0) {
                           livingEntityPatch.playAnimationSynchronized(Animations.LONGSWORD_AUTO2, 0.0F);
                           entity.getPersistentData().m_128347_("DualSwordCombo", 2.0);
                        } else if (entity.getPersistentData().m_128459_("DualSwordCombo") == 2.0) {
                           livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.DUAL_DANCING_EDGE, 0.0F);
                           entity.getPersistentData().m_128347_("DualSwordCombo", 3.0);
                        } else if (entity.getPersistentData().m_128459_("DualSwordCombo") == 3.0) {
                           livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.DUAL_SWORD_DANCING_EDGE, 0.0F);
                           entity.getPersistentData().m_128473_("DualSwordCombo");
                        }

                        return;
                     }

                     if ((
                           playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WeaponCategories.SWORD
                              || playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WeaponCategories.LONGSWORD
                              || playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WeaponCategories.TACHI
                              || playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WeaponCategories.DAGGER
                              || playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WeaponCategories.UCHIGATANA
                        )
                        && playerpatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() != WeaponCategories.SWORD
                        && playerpatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() != WeaponCategories.TACHI
                        && playerpatch.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() != WeaponCategories.AXE
                        && entity.m_9236_() instanceof ServerLevel) {
                        if (!entity.getPersistentData().m_128441_("SwordCombo")) {
                           livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.SWORD_HEAVY_AUTO_1, 0.0F);
                           entity.getPersistentData().m_128347_("SwordCombo", 1.0);
                        } else if (entity.getPersistentData().m_128459_("SwordCombo") == 1.0) {
                           livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.SWORD_HEAVY_AUTO_2, 0.0F);
                           entity.getPersistentData().m_128347_("SwordCombo", 2.0);
                        } else if (entity.getPersistentData().m_128459_("SwordCombo") == 2.0) {
                           livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.SWORD_HEAVY_AUTO_3, 0.0F);
                           entity.getPersistentData().m_128473_("SwordCombo");
                        }

                        return;
                     }

                     if (playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WeaponCategories.GREATSWORD
                        && entity.m_9236_() instanceof ServerLevel) {
                        livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.GIANT_WHIRLWIND, 0.0F);
                        return;
                     }

                     if ((
                           playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WeaponCategories.FIST
                              || playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WeaponCategories.NOT_WEAPON
                              || playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WeaponCategories.BOW
                              || playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WeaponCategories.CROSSBOW
                        )
                        && entity.m_9236_() instanceof ServerLevel) {
                        if (entity.m_20142_()) {
                           if (entity.m_6144_()) {
                              if (entity.m_9236_() instanceof ServerLevel) {
                                 livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.WHIRLWIND_KICK_LEFT, 0.0F);
                              }
                           } else if (entity.m_9236_() instanceof ServerLevel) {
                              livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.WHIRLWIND_KICK, 0.0F);
                           }
                        } else if (!entity.getPersistentData().m_128441_("FistCombo")) {
                           livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.FIST_LEFT, 0.0F);
                           entity.getPersistentData().m_128347_("FistCombo", 1.0);
                        } else if (entity.getPersistentData().m_128459_("FistCombo") == 1.0) {
                           livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.FIST_UP, 0.0F);
                           entity.getPersistentData().m_128347_("FistCombo", 2.0);
                        } else if (entity.getPersistentData().m_128459_("FistCombo") == 2.0) {
                           livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.FIST_DASH, 0.0F);
                           entity.getPersistentData().m_128473_("FistCombo");
                        }

                        return;
                     }

                     if ((
                           playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WeaponCategories.SPEAR
                              || playerpatch.getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory() == WeaponCategories.TRIDENT
                        )
                        && entity.m_9236_() instanceof ServerLevel) {
                        livingEntityPatch.playAnimationSynchronized(AnimsPugilistSteve.SPEAR_THRUST, 0.0F);
                     }

                     registerMoreSpecialAttackCategories(playerpatch, entity, livingEntityPatch);
                  }
               }
            }
         }
      }
   }
}
