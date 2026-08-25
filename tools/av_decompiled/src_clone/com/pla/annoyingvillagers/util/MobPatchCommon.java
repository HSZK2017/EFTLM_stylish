package com.pla.annoyingvillagers.util;

import com.p1nero.epicfightbow.item.EFBowItems;
import com.pla.annoyingvillagers.combatbehaviour.AvNpcAxe;
import com.pla.annoyingvillagers.combatbehaviour.AvNpcBlockWeapon;
import com.pla.annoyingvillagers.combatbehaviour.AvNpcDagger;
import com.pla.annoyingvillagers.combatbehaviour.AvNpcGreatsword;
import com.pla.annoyingvillagers.combatbehaviour.AvNpcLongsword;
import com.pla.annoyingvillagers.combatbehaviour.AvNpcSpear;
import com.pla.annoyingvillagers.combatbehaviour.AvNpcSword;
import com.pla.annoyingvillagers.combatbehaviour.AvNpcTachi;
import com.pla.annoyingvillagers.combatbehaviour.HerobrineObsidianWeapon;
import com.pla.annoyingvillagers.combatbehaviour.HerobrineShadowObsidianPillar;
import com.pla.annoyingvillagers.combatbehaviour.HerobrineShadowObsidianSword;
import com.pla.annoyingvillagers.combatbehaviour.NpcBow;
import com.pla.annoyingvillagers.combatbehaviour.PlayerNpcBow;
import com.pla.annoyingvillagers.compat.p1nero_bow.NpcP1neroBow;
import com.pla.annoyingvillagers.compat.p1nero_bow.NpcP1neroMortisBow;
import com.pla.annoyingvillagers.compat.p1nero_bow.PlayerNpcP1neroBow;
import com.pla.annoyingvillagers.compat.p1nero_bow.PlayerNpcP1neroMortisBow;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.RegistryObject;
import net.shelmarow.combat_evolution.ai.CECombatBehaviors.Builder;
import reascer.wom.world.item.WOMItems;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;

public class MobPatchCommon {
   public static Builder<MobPatch<?>> overideCustomWeaponMotionBuilderForAvNpc(CapabilityItem mainHandCap, Style style) {
      Builder<MobPatch<?>> avNpcWeaponOverride = overideRequestedAvNpcWeaponMotionBuilder(mainHandCap, style);
      if (avNpcWeaponOverride != null) {
         return avNpcWeaponOverride;
      } else if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)WOMItems.DIAMOND_STAFF.get()).m_7968_()) && style == Styles.TWO_HAND) {
         return AvNpcSpear.STAFF;
      } else if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)WOMItems.GOLDEN_STAFF.get()).m_7968_()) && style == Styles.TWO_HAND) {
         return AvNpcSpear.STAFF;
      } else if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)WOMItems.IRON_STAFF.get()).m_7968_()) && style == Styles.TWO_HAND) {
         return AvNpcSpear.STAFF;
      } else if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)WOMItems.STONE_STAFF.get()).m_7968_()) && style == Styles.TWO_HAND) {
         return AvNpcSpear.STAFF;
      } else if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)WOMItems.WOODEN_STAFF.get()).m_7968_()) && style == Styles.TWO_HAND) {
         return AvNpcSpear.STAFF;
      } else {
         if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.BLACK_FIRE_SWORD.get()).m_7968_())) {
            if (style == Styles.ONE_HAND) {
               return AvNpcSword.BLACK_FIRE_SWORD;
            }

            if (style == Styles.TWO_HAND) {
               return AvNpcSword.DUAL_BLACK_FIRE_SWORD;
            }
         }

         if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.BLUE_FLAME_SWORD.get()).m_7968_())) {
            if (style == Styles.ONE_HAND) {
               return AvNpcSword.BLUE_FLAME_SWORD;
            }

            if (style == Styles.TWO_HAND) {
               return AvNpcSword.DUAL_BLUE_FLAME_SWORD;
            }
         }

         if ((
               mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.CENTRANOS_SWORD.get()).m_7968_())
                  || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.IRON_CLEAVER.get()).m_7968_())
            )
            && style == Styles.TWO_HAND) {
            return AvNpcGreatsword.CLEAVER;
         } else {
            if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.CLOW_SWORD.get()).m_7968_())) {
               if (style == Styles.ONE_HAND) {
                  return AvNpcSword.CLOW_SWORD;
               }

               if (style == Styles.TWO_HAND) {
                  return AvNpcSword.DUAL_CLOW_SWORD;
               }
            }

            if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_ATTRACTOR_SWORD.get()).m_7968_())) {
               if (style == Styles.ONE_HAND) {
                  return AvNpcSword.DIAMOND_ATTRACTOR_SWORD;
               }

               if (style == Styles.TWO_HAND) {
                  return AvNpcSword.DUAL_DIAMOND_ATTRACTOR_SWORD;
               }
            }

            if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_BLASTER_SWORD.get()).m_7968_())) {
               if (style == Styles.ONE_HAND) {
                  return AvNpcSword.DIAMOND_BLASTER_SWORD;
               }

               if (style == Styles.TWO_HAND) {
                  return AvNpcSword.DUAL_DIAMOND_BLASTER_SWORD;
               }
            }

            if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.HACKER_SWORD.get()).m_7968_())) {
               if (style == Styles.ONE_HAND) {
                  return AvNpcSword.HACKER_SWORD;
               }

               if (style == Styles.TWO_HAND) {
                  return AvNpcSword.DUAL_HACKER_SWORD;
               }
            }

            if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_WARBLADE.get()).m_7968_())) {
               return AvNpcTachi.DIAMOND_WARBLADE;
            } else if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_LAEVATEINN.get()).m_7968_())) {
               return AvNpcTachi.DIAMOND_LAEVATEINN;
            } else {
               if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_FALCHION.get()).m_7968_())
                  || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_GREAT_FALCHION.get()).m_7968_())
                  || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.NETHERITE_FALCHION.get()).m_7968_())) {
                  if (style == Styles.ONE_HAND) {
                     return AvNpcTachi.FALCHION;
                  }

                  if (style == Styles.TWO_HAND) {
                     return AvNpcTachi.DUAL_FALCHION;
                  }
               }

               if (mainHandCap != EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_SABRE.get()).m_7968_())
                  && mainHandCap != EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.NETHERITE_SABRE.get()).m_7968_())) {
                  if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.HOOKED_IRON_SWORD.get()).m_7968_())
                     || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.HOOKED_GOLDEN_SWORD.get()).m_7968_())) {
                     if (style == Styles.ONE_HAND) {
                        return AvNpcSword.HOOK_SWORD;
                     }

                     if (style == Styles.TWO_HAND) {
                        return AvNpcSword.DUAL_HOOK_SWORD;
                     }
                  }

                  if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.FLANKER_HOOKED_SWORD.get()).m_7968_())) {
                     if (style == Styles.ONE_HAND) {
                        return AvNpcSword.FLANKER_HOOK_SWORD;
                     }

                     if (style == Styles.TWO_HAND) {
                        return AvNpcSword.DUAL_HOOK_SWORD;
                     }
                  }

                  if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DNAX_HOOKED_SWORD.get()).m_7968_())) {
                     if (style == Styles.ONE_HAND) {
                        return AvNpcSword.DNAX_HOOK_SWORD;
                     }

                     if (style == Styles.TWO_HAND) {
                        return AvNpcSword.DUAL_DNAX_HOOK_SWORD;
                     }
                  }

                  if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_LONGSWORD.get()).m_7968_())
                     || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.GOLDEN_LONGSWORD.get()).m_7968_())
                     || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.IRON_LONGSWORD.get()).m_7968_())
                     || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.RUBY_LONGSWORD.get()).m_7968_())) {
                     if (style == Styles.ONE_HAND) {
                        return AvNpcLongsword.AV_LONGSWORD;
                     }

                     if (style == Styles.TWO_HAND) {
                        return AvNpcLongsword.DUAL_AV_LONGSWORD;
                     }
                  }

                  if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_CHIPPED_LONGSWORD.get()).m_7968_())) {
                     return AvNpcLongsword.CHIPPED_LONGSWORD;
                  } else if ((
                        mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_GREATSWORD.get()).m_7968_())
                           || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.RUBY_GREATSWORD.get()).m_7968_())
                     )
                     && style == Styles.TWO_HAND) {
                     return AvNpcGreatsword.AV_GREATSWORD;
                  } else {
                     if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.RUBY_SWORD.get()).m_7968_())
                        || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.THUNDER_DIAMOND_BLADE.get()).m_7968_())
                        || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.JADE_SWORD.get()).m_7968_())
                        || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.RED_DIAMOND_SWORD.get()).m_7968_())
                        || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.WOOPIE_THE_SWORD.get()).m_7968_())
                        || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_KNIGHT_SWORD.get()).m_7968_())
                        || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.RUBY_KNIGHT_SWORD.get()).m_7968_())
                        || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.PALADIN_SWORD.get()).m_7968_())
                        || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.GREAT_SWORD.get()).m_7968_())) {
                        if (style == Styles.ONE_HAND) {
                           return AvNpcSword.AV_SWORD;
                        }

                        if (style == Styles.TWO_HAND) {
                           return AvNpcSword.AV_DUAL_SWORD;
                        }
                     }

                     if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.EARTH_AXE.get()).m_7968_())
                        && style == Styles.ONE_HAND) {
                        return AvNpcAxe.EARTH_AXE;
                     } else if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.RED_AXE.get()).m_7968_())
                        && style == Styles.ONE_HAND) {
                        return AvNpcAxe.RED_AXE;
                     } else if (mainHandCap
                           == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_BATTLEAXE.get()).m_7968_())
                        && style == Styles.TWO_HAND) {
                        return AvNpcGreatsword.BATTLE_AXE;
                     } else if (mainHandCap
                           == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.GIANT_NETHERITE_AXE.get()).m_7968_())
                        && style == Styles.TWO_HAND) {
                        return AvNpcGreatsword.GIANT_AXE;
                     } else {
                        if (mainHandCap
                              == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.EXTERMINATOR_BATTLEAXE.get()).m_7968_())
                           || mainHandCap
                              == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.EXTERMINATOR_BATTLEAXE_GREEN.get()).m_7968_())
                           || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.GOLDEN_MACE.get()).m_7968_())
                           || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_MACE.get()).m_7968_())) {
                           if (style == Styles.ONE_HAND) {
                              return AvNpcAxe.EXTERMINATOR_BATTLE_AXE;
                           }

                           if (style == Styles.TWO_HAND) {
                              return AvNpcAxe.DUAL_EXTERMINATOR_BATTLE_AXE;
                           }
                        }

                        if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.IRON_GREATAXE.get()).m_7968_())
                           || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_GREATAXE.get()).m_7968_())
                           || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.NETHERITE_GREATAXE.get()).m_7968_())
                           )
                         {
                           return AvNpcGreatsword.GREATAXE;
                        } else if (mainHandCap
                              == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_HALBERD.get()).m_7968_())
                           || mainHandCap
                              == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.IRON_DOUBLE_BLADED_HALBERD.get()).m_7968_())
                           || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_HALBERD.get()).m_7968_())) {
                           return AvNpcAxe.HALBERD;
                        } else if (mainHandCap
                           == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.SAMANTHA_THE_KILLER_AXE.get()).m_7968_())) {
                           return AvNpcAxe.KILLER_AXE;
                        } else {
                           if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.KNIFE.get()).m_7968_())
                              || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_KNIFE.get()).m_7968_())
                              || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.NETHERITE_KNIFE.get()).m_7968_())
                              )
                            {
                              if (style == Styles.ONE_HAND) {
                                 return AvNpcDagger.KNIFE;
                              }

                              if (style == Styles.TWO_HAND) {
                                 return AvNpcDagger.DUAL_KNIFE;
                              }
                           }

                           if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_ARMBLADE.get()).m_7968_())) {
                              return AvNpcDagger.ARM_BLADE;
                           } else if (mainHandCap
                                 == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.GOLDEN_MOON_BLADE.get()).m_7968_())
                              || mainHandCap
                                 == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_MOON_BLADE.get()).m_7968_())) {
                              return AvNpcDagger.MOON_BLADE;
                           } else if (mainHandCap
                              == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_CLAW.get()).m_7968_())) {
                              return AvNpcDagger.CLAW;
                           } else if (mainHandCap != EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.SPEAR_AXE.get()).m_7968_())
                              && mainHandCap != EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_BOLT.get()).m_7968_())
                              && mainHandCap != EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_SPEAR.get()).m_7968_())
                              && mainHandCap != EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.NETHERITE_SPEAR.get()).m_7968_())
                              )
                            {
                              if (mainHandCap
                                 == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.TWIN_DIAMOND_SPEAR.get()).m_7968_())) {
                                 if (style == Styles.ONE_HAND) {
                                    return AvNpcSpear.GUANDAO;
                                 }

                                 if (style == Styles.TWO_HAND) {
                                    return AvNpcSpear.SPEAR_STAFF;
                                 }
                              }

                              if (mainHandCap
                                    == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.IRON_TWIN_BLADE_KATANA.get()).m_7968_())
                                 || mainHandCap
                                    == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DOUBLE_DIAMOND_GLAIVE.get()).m_7968_())) {
                                 return AvNpcSpear.SPEAR_STAFF;
                              } else if (mainHandCap
                                 == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.BLACKSCRATCHER.get()).m_7968_())) {
                                 return AvNpcSpear.BLACK_SCRATCHER;
                              } else if (mainHandCap
                                    == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.DIAMOND_SICKLE.get()).m_7968_())
                                 || mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.IRON_SICKLE.get()).m_7968_())) {
                                 return AvNpcSpear.SICKLE;
                              } else if (mainHandCap
                                    == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.WOODEN_DOOR.get()).m_7968_())
                                 && style == Styles.TWO_HAND) {
                                 return AvNpcBlockWeapon.WOODEN_DOOR;
                              } else if (mainHandCap
                                    == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.CRAFTING_TABLE.get()).m_7968_())
                                 && style == Styles.TWO_HAND) {
                                 return AvNpcBlockWeapon.CRAFTING_TABLE;
                              } else if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.LADDER.get()).m_7968_())
                                 && style == Styles.TWO_HAND) {
                                 return AvNpcBlockWeapon.LADDER;
                              } else {
                                 return mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.TRAPDOOR.get()).m_7968_())
                                       && style == Styles.TWO_HAND
                                    ? AvNpcBlockWeapon.TRAPDOOR
                                    : null;
                              }
                           } else {
                              return AvNpcSpear.GUANDAO;
                           }
                        }
                     }
                  }
               } else {
                  return AvNpcLongsword.DIAMOND_SABRE;
               }
            }
         }
      }
   }

   private static Builder<MobPatch<?>> overideRequestedAvNpcWeaponMotionBuilder(CapabilityItem mainHandCap, Style style) {
      if (matches(
         mainHandCap,
         AnnoyingVillagersModItems.HOOKED_DIAMOND_SWORD,
         AnnoyingVillagersModItems.HOOKED_IRON_SWORD,
         AnnoyingVillagersModItems.HOOKED_GOLDEN_SWORD
      )) {
         if (style == Styles.ONE_HAND) {
            return AvNpcSword.HOOK_SWORD;
         }

         if (style == Styles.TWO_HAND) {
            return AvNpcSword.DUAL_HOOK_SWORD;
         }
      }

      if (matches(mainHandCap, AnnoyingVillagersModItems.FLANKER_HOOKED_SWORD)) {
         if (style == Styles.ONE_HAND) {
            return AvNpcSword.FLANKER_HOOK_SWORD;
         }

         if (style == Styles.TWO_HAND) {
            return AvNpcSword.DUAL_HOOK_SWORD;
         }
      }

      if (matches(mainHandCap, AnnoyingVillagersModItems.DNAX_HOOKED_SWORD)) {
         if (style == Styles.ONE_HAND) {
            return AvNpcSword.DNAX_HOOK_SWORD;
         }

         if (style == Styles.TWO_HAND) {
            return AvNpcSword.DUAL_DNAX_HOOK_SWORD;
         }
      }

      if (matches(mainHandCap, AnnoyingVillagersModItems.DIAMOND_SABRE, AnnoyingVillagersModItems.NETHERITE_SABRE) && style == Styles.TWO_HAND) {
         return AvNpcLongsword.DIAMOND_SABRE;
      } else if (matches(mainHandCap, AnnoyingVillagersModItems.DIAMOND_HALBERD, AnnoyingVillagersModItems.IRON_HALBERD) && style == Styles.TWO_HAND) {
         return AvNpcAxe.HALBERD;
      } else if (matches(mainHandCap, AnnoyingVillagersModItems.IRON_DOUBLE_BLADED_HALBERD) && style == Styles.TWO_HAND) {
         return AvNpcAxe.DOUBLE_HALBERD;
      } else if (matches(mainHandCap, AnnoyingVillagersModItems.SAMANTHA_THE_KILLER_AXE) && style == Styles.TWO_HAND) {
         return AvNpcAxe.KILLER_AXE;
      } else if (matches(mainHandCap, AnnoyingVillagersModItems.EARTH_AXE) && style == Styles.ONE_HAND) {
         return AvNpcAxe.EARTH_AXE;
      } else if (matches(mainHandCap, AnnoyingVillagersModItems.RED_AXE) && style == Styles.ONE_HAND) {
         return AvNpcAxe.RED_AXE;
      } else {
         if (matches(
            mainHandCap,
            AnnoyingVillagersModItems.EXTERMINATOR_BATTLEAXE,
            AnnoyingVillagersModItems.EXTERMINATOR_BATTLEAXE_GREEN,
            AnnoyingVillagersModItems.GOLDEN_MACE,
            AnnoyingVillagersModItems.DIAMOND_MACE
         )) {
            if (style == Styles.ONE_HAND) {
               return AvNpcAxe.EXTERMINATOR_BATTLE_AXE;
            }

            if (style == Styles.TWO_HAND) {
               return AvNpcAxe.DUAL_EXTERMINATOR_BATTLE_AXE;
            }
         }

         if (matches(mainHandCap, AnnoyingVillagersModItems.DIAMOND_SPEAR, AnnoyingVillagersModItems.NETHERITE_SPEAR, AnnoyingVillagersModItems.SPEAR_AXE)
            && style == Styles.TWO_HAND) {
            return AvNpcSpear.GUANDAO;
         } else if (matches(mainHandCap, AnnoyingVillagersModItems.DOUBLE_DIAMOND_GLAIVE, AnnoyingVillagersModItems.IRON_TWIN_BLADE_KATANA)
            && style == Styles.TWO_HAND) {
            return AvNpcSpear.SPEAR_STAFF;
         } else {
            if (matches(mainHandCap, AnnoyingVillagersModItems.TWIN_DIAMOND_SPEAR)) {
               if (style == Styles.ONE_HAND) {
                  return AvNpcSpear.GUANDAO;
               }

               if (style == Styles.TWO_HAND) {
                  return AvNpcSpear.SPEAR_STAFF;
               }
            }

            if (matches(mainHandCap, AnnoyingVillagersModItems.DIAMOND_SICKLE, AnnoyingVillagersModItems.IRON_SICKLE) && style == Styles.TWO_HAND) {
               return AvNpcSpear.SICKLE;
            } else if (matches(mainHandCap, AnnoyingVillagersModItems.DIAMOND_BOLT) && style == Styles.TWO_HAND) {
               return AvNpcSpear.BOLT;
            } else if (matches(mainHandCap, AnnoyingVillagersModItems.BLACKSCRATCHER) && style == Styles.TWO_HAND) {
               return AvNpcSpear.BLACK_SCRATCHER;
            } else if (matches(mainHandCap, AnnoyingVillagersModItems.DIAMOND_WARBLADE) && style == Styles.TWO_HAND) {
               return AvNpcTachi.DIAMOND_WARBLADE;
            } else if (matches(mainHandCap, AnnoyingVillagersModItems.DIAMOND_LAEVATEINN) && style == Styles.TWO_HAND) {
               return AvNpcTachi.DIAMOND_LAEVATEINN;
            } else {
               if (matches(
                  mainHandCap,
                  AnnoyingVillagersModItems.DIAMOND_FALCHION,
                  AnnoyingVillagersModItems.DIAMOND_GREAT_FALCHION,
                  AnnoyingVillagersModItems.NETHERITE_FALCHION
               )) {
                  if (style == Styles.TWO_HAND) {
                     return AvNpcTachi.FALCHION;
                  }

                  if (style == Styles.OCHS) {
                     return AvNpcTachi.DUAL_FALCHION;
                  }
               }

               if (matches(
                  mainHandCap,
                  AnnoyingVillagersModItems.DIAMOND_LONGSWORD,
                  AnnoyingVillagersModItems.GOLDEN_LONGSWORD,
                  AnnoyingVillagersModItems.IRON_LONGSWORD,
                  AnnoyingVillagersModItems.RUBY_LONGSWORD
               )) {
                  if (style == Styles.ONE_HAND) {
                     return AvNpcLongsword.AV_LONGSWORD;
                  }

                  if (style == Styles.TWO_HAND) {
                     return AvNpcLongsword.DUAL_AV_LONGSWORD;
                  }
               }

               if (matches(mainHandCap, AnnoyingVillagersModItems.DIAMOND_CHIPPED_LONGSWORD) && style == Styles.TWO_HAND) {
                  return AvNpcLongsword.CHIPPED_LONGSWORD;
               } else if (matches(mainHandCap, AnnoyingVillagersModItems.DIAMOND_GREATSWORD, AnnoyingVillagersModItems.RUBY_GREATSWORD)
                  && style == Styles.TWO_HAND) {
                  return AvNpcGreatsword.AV_GREATSWORD;
               } else if (matches(
                     mainHandCap,
                     AnnoyingVillagersModItems.DIAMOND_GREATAXE,
                     AnnoyingVillagersModItems.IRON_GREATAXE,
                     AnnoyingVillagersModItems.NETHERITE_GREATAXE
                  )
                  && style == Styles.TWO_HAND) {
                  return AvNpcGreatsword.GREATAXE;
               } else if (matches(mainHandCap, AnnoyingVillagersModItems.GIANT_NETHERITE_AXE) && style == Styles.TWO_HAND) {
                  return AvNpcGreatsword.GIANT_AXE;
               } else {
                  return matches(mainHandCap, AnnoyingVillagersModItems.DIAMOND_BATTLEAXE) && style == Styles.TWO_HAND ? AvNpcGreatsword.BATTLE_AXE : null;
               }
            }
         }
      }
   }

   @SafeVarargs
   private static boolean matches(CapabilityItem mainHandCap, RegistryObject<Item>... items) {
      for (RegistryObject<Item> item : items) {
         if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)item.get()).m_7968_())) {
            return true;
         }
      }

      return false;
   }

   public static Builder<MobPatch<?>> overideCustomWeaponMotionBuilderForShadowHerobrine(CapabilityItem mainHandCap, Style style) {
      if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_WEAPON.get()).m_7968_())
         && style == Styles.TWO_HAND) {
         return HerobrineObsidianWeapon.OBSIDIAN_WEAPON;
      } else {
         if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_PILLAR.get()).m_7968_())) {
            if (style == Styles.TWO_HAND) {
               return HerobrineShadowObsidianPillar.SHADOW_OBSIDIAN_PILLAR_WEAPON;
            }

            if (style == Styles.OCHS) {
               return HerobrineShadowObsidianPillar.SHADOW_OBSIDIAN_PILLAR_SWORD_WEAPON;
            }
         }

         if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)AnnoyingVillagersModItems.SHADOW_OBSIDIAN_SWORD.get()).m_7968_())) {
            if (style == Styles.TWO_HAND) {
               return HerobrineShadowObsidianSword.SHADOW_OBSIDIAN_DUAL_SWORD;
            }

            if (style == Styles.ONE_HAND) {
               return HerobrineShadowObsidianSword.SHADOW_OBSIDIAN_SWORD;
            }
         }

         return null;
      }
   }

   public static Builder<MobPatch<?>> overideBowMotionBuilderForNpc(CapabilityItem mainHandCap, Style style) {
      if (ModList.get().isLoaded("p1nero_bow")) {
         if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)EFBowItems.MORTIS.get()).m_7968_())) {
            return NpcP1neroMortisBow.MORTIS_BOW;
         }

         if (mainHandCap == EpicFightCapabilities.getItemStackCapability(Items.f_42411_.m_7968_())) {
            return NpcP1neroBow.BOW;
         }
      } else if (mainHandCap == EpicFightCapabilities.getItemStackCapability(Items.f_42411_.m_7968_())) {
         return NpcBow.BOW;
      }

      return null;
   }

   public static Builder<MobPatch<?>> overideBowMotionBuilderForPlayerNpc(CapabilityItem mainHandCap, Style style) {
      if (ModList.get().isLoaded("p1nero_bow")) {
         if (mainHandCap == EpicFightCapabilities.getItemStackCapability(((Item)EFBowItems.MORTIS.get()).m_7968_())) {
            return PlayerNpcP1neroMortisBow.MORTIS_BOW;
         }

         if (mainHandCap == EpicFightCapabilities.getItemStackCapability(Items.f_42411_.m_7968_())) {
            return PlayerNpcP1neroBow.BOW;
         }
      } else if (mainHandCap == EpicFightCapabilities.getItemStackCapability(Items.f_42411_.m_7968_())) {
         return PlayerNpcBow.BOW;
      }

      return null;
   }
}
