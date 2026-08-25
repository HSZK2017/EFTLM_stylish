package com.dmc.invincible_dmc.command;

import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerState;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerStateProvider;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.ConcentrationManager;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class SetPlayerStateCommands {
   static LiteralArgumentBuilder<CommandSourceStack> buildStateNode() {
      LiteralArgumentBuilder<CommandSourceStack> self = Commands.m_82127_("state");
      self = (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)self.then(
            Commands.m_82127_("setPlayerPhase").then(Commands.m_82129_("value", IntegerArgumentType.integer()).executes(ctx -> {
               applySelf((CommandSourceStack)ctx.getSource(), p -> DMCPlayerCapabilityProvider.get(p).setPhase(IntegerArgumentType.getInteger(ctx, "value")));
               return 0;
            }))
         ))
         .then(Commands.m_82127_("resetPhase").executes(ctx -> {
            applySelf((CommandSourceStack)ctx.getSource(), p -> DMCPlayerCapabilityProvider.get(p).resetPhase());
            return 0;
         }));
      self = (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)self.then(
                        Commands.m_82127_("setDtStack")
                           .then(
                              Commands.m_82129_("value", IntegerArgumentType.integer())
                                 .executes(
                                    ctx -> {
                                       applySelf(
                                          (CommandSourceStack)ctx.getSource(),
                                          p -> {
                                             ServerPlayerPatch sp = patch(p);
                                             sp.getSkill(SkillSlots.WEAPON_INNATE)
                                                .getSkill()
                                                .setStackSynchronize(sp.getSkill(SkillSlots.WEAPON_INNATE), IntegerArgumentType.getInteger(ctx, "value"));
                                          }
                                       );
                                       return 0;
                                    }
                                 )
                           )
                     ))
                     .then(Commands.m_82127_("consumeDtStack").then(Commands.m_82129_("value", IntegerArgumentType.integer()).executes(ctx -> {
                        applySelf((CommandSourceStack)ctx.getSource(), p -> {
                           ServerPlayerPatch sp = patch(p);
                           SkillContainer c = sp.getSkill(SkillSlots.WEAPON_INNATE);
                           c.getSkill().setStackSynchronize(c, Math.max(0, c.getStack() - IntegerArgumentType.getInteger(ctx, "value")));
                        });
                        return 0;
                     }))))
                  .then(
                     Commands.m_82127_("setDtConsumption")
                        .then(
                           Commands.m_82129_("value", FloatArgumentType.floatArg())
                              .executes(
                                 ctx -> {
                                    applySelf(
                                       (CommandSourceStack)ctx.getSource(),
                                       p -> {
                                          ServerPlayerPatch sp = patch(p);
                                          sp.getSkill(SkillSlots.WEAPON_INNATE)
                                             .getSkill()
                                             .setConsumptionSynchronize(sp.getSkill(SkillSlots.WEAPON_INNATE), FloatArgumentType.getFloat(ctx, "value"));
                                       }
                                    );
                                    return 0;
                                 }
                              )
                        )
                  ))
               .then(Commands.m_82127_("consumeDtConsumption").then(Commands.m_82129_("value", FloatArgumentType.floatArg()).executes(ctx -> {
                  applySelf((CommandSourceStack)ctx.getSource(), p -> {
                     ServerPlayerPatch sp = patch(p);
                     SkillContainer c = sp.getSkill(SkillSlots.WEAPON_INNATE);
                     c.getSkill().setConsumptionSynchronize(c, Math.max(0.0F, c.getResource() - FloatArgumentType.getFloat(ctx, "value")));
                  });
                  return 0;
               }))))
            .then(Commands.m_82127_("consumeStamina").then(Commands.m_82129_("value", FloatArgumentType.floatArg()).executes(ctx -> {
               applySelf((CommandSourceStack)ctx.getSource(), p -> {
                  ServerPlayerPatch sp = patch(p);
                  sp.setStamina(Math.max(0.0F, sp.getStamina() - FloatArgumentType.getFloat(ctx, "value")));
                  SkillContainer c = sp.getSkill(SkillSlots.WEAPON_INNATE);
                  c.getSkill().setConsumptionSynchronize(c, Math.max(0.0F, c.getResource() - FloatArgumentType.getFloat(ctx, "value")));
               });
               return 0;
            }))))
         .then(Commands.m_82127_("setStamina").then(Commands.m_82129_("value", FloatArgumentType.floatArg()).executes(ctx -> {
            applySelf((CommandSourceStack)ctx.getSource(), p -> patch(p).setStamina(FloatArgumentType.getFloat(ctx, "value")));
            return 0;
         })));
      self = (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)self.then(
                  Commands.m_82127_("setConcentration").then(Commands.m_82129_("value", FloatArgumentType.floatArg(0.0F, 10000.0F)).executes(ctx -> {
                     applySelf((CommandSourceStack)ctx.getSource(), p -> setConc(p, FloatArgumentType.getFloat(ctx, "value")));
                     return 0;
                  }))
               ))
               .then(Commands.m_82127_("addConcentration").then(Commands.m_82129_("value", FloatArgumentType.floatArg()).executes(ctx -> {
                  applySelf((CommandSourceStack)ctx.getSource(), p -> addConc(p, FloatArgumentType.getFloat(ctx, "value")));
                  return 0;
               }))))
            .then(Commands.m_82127_("lockConcentration").executes(ctx -> {
               applySelf((CommandSourceStack)ctx.getSource(), p -> lockConc(p, true));
               return 0;
            })))
         .then(Commands.m_82127_("unlockConcentration").executes(ctx -> {
            applySelf((CommandSourceStack)ctx.getSource(), p -> lockConc(p, false));
            return 0;
         }));
      self = (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)self.then(
                              Commands.m_82127_("setSdt").then(Commands.m_82129_("value", FloatArgumentType.floatArg(0.0F, 1000.0F)).executes(ctx -> {
                                 applySelf((CommandSourceStack)ctx.getSource(), p -> setSdtVal(p, FloatArgumentType.getFloat(ctx, "value")));
                                 return 0;
                              }))
                           ))
                           .then(Commands.m_82127_("addSdt").then(Commands.m_82129_("value", FloatArgumentType.floatArg()).executes(ctx -> {
                              applySelf((CommandSourceStack)ctx.getSource(), p -> addSdtVal(p, FloatArgumentType.getFloat(ctx, "value")));
                              return 0;
                           }))))
                        .then(Commands.m_82127_("lockSdt").executes(ctx -> {
                           applySelf((CommandSourceStack)ctx.getSource(), p -> lockSdt(p, true));
                           return 0;
                        })))
                     .then(Commands.m_82127_("unlockSdt").executes(ctx -> {
                        applySelf((CommandSourceStack)ctx.getSource(), p -> lockSdt(p, false));
                        return 0;
                     })))
                  .then(Commands.m_82127_("addSdt").then(Commands.m_82129_("value", FloatArgumentType.floatArg()).executes(ctx -> {
                     applySelf((CommandSourceStack)ctx.getSource(), p -> addSdtVal(p, FloatArgumentType.getFloat(ctx, "value")));
                     return 0;
                  }))))
               .then(Commands.m_82127_("toggleSdt").executes(ctx -> {
                  applySelf((CommandSourceStack)ctx.getSource(), SetPlayerStateCommands::toggleSdt);
                  return 0;
               })))
            .then(Commands.m_82127_("setSdtPhase").then(Commands.m_82129_("value", IntegerArgumentType.integer(0, 4)).executes(ctx -> {
               applySelf((CommandSourceStack)ctx.getSource(), p -> setSdtPhase(p, IntegerArgumentType.getInteger(ctx, "value")));
               return 0;
            }))))
         .then(Commands.m_82127_("resetYamatoState").executes(ctx -> {
            applySelf((CommandSourceStack)ctx.getSource(), SetPlayerStateCommands::resetYamatoState);
            return 0;
         }));
      return (LiteralArgumentBuilder<CommandSourceStack>)self.then(
         ((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.m_82129_(
                                                      "players", EntityArgument.m_91470_()
                                                   )
                                                   .then(
                                                      Commands.m_82127_("setPlayerPhase")
                                                         .then(Commands.m_82129_("value", IntegerArgumentType.integer()).executes(ctx -> {
                                                            applyTargets(
                                                               ctx,
                                                               p -> DMCPlayerCapabilityProvider.get(p).setPhase(IntegerArgumentType.getInteger(ctx, "value"))
                                                            );
                                                            return 0;
                                                         }))
                                                   ))
                                                .then(Commands.m_82127_("resetPhase").executes(ctx -> {
                                                   applyTargets(ctx, p -> DMCPlayerCapabilityProvider.get(p).resetPhase());
                                                   return 0;
                                                })))
                                             .then(
                                                Commands.m_82127_("setStack")
                                                   .then(
                                                      Commands.m_82129_("value", IntegerArgumentType.integer())
                                                         .executes(
                                                            ctx -> {
                                                               applyTargets(
                                                                  ctx,
                                                                  p -> {
                                                                     ServerPlayerPatch sp = patch(p);
                                                                     sp.getSkill(SkillSlots.WEAPON_INNATE)
                                                                        .getSkill()
                                                                        .setStackSynchronize(
                                                                           sp.getSkill(SkillSlots.WEAPON_INNATE), IntegerArgumentType.getInteger(ctx, "value")
                                                                        );
                                                                  }
                                                               );
                                                               return 0;
                                                            }
                                                         )
                                                   )
                                             ))
                                          .then(
                                             Commands.m_82127_("setConcentration")
                                                .then(Commands.m_82129_("value", FloatArgumentType.floatArg(0.0F, 10000.0F)).executes(ctx -> {
                                                   applyTargets(ctx, p -> setConc(p, FloatArgumentType.getFloat(ctx, "value")));
                                                   return 0;
                                                }))
                                          ))
                                       .then(
                                          Commands.m_82127_("addConcentration").then(Commands.m_82129_("value", FloatArgumentType.floatArg()).executes(ctx -> {
                                             applyTargets(ctx, p -> addConc(p, FloatArgumentType.getFloat(ctx, "value")));
                                             return 0;
                                          }))
                                       ))
                                    .then(Commands.m_82127_("lockConcentration").executes(ctx -> {
                                       applyTargets(ctx, p -> lockConc(p, true));
                                       return 0;
                                    })))
                                 .then(Commands.m_82127_("unlockConcentration").executes(ctx -> {
                                    applyTargets(ctx, p -> lockConc(p, false));
                                    return 0;
                                 })))
                              .then(Commands.m_82127_("setSdt").then(Commands.m_82129_("value", FloatArgumentType.floatArg(0.0F, 1000.0F)).executes(ctx -> {
                                 applyTargets(ctx, p -> setSdtVal(p, FloatArgumentType.getFloat(ctx, "value")));
                                 return 0;
                              }))))
                           .then(Commands.m_82127_("addSdt").then(Commands.m_82129_("value", FloatArgumentType.floatArg()).executes(ctx -> {
                              applyTargets(ctx, p -> addSdtVal(p, FloatArgumentType.getFloat(ctx, "value")));
                              return 0;
                           }))))
                        .then(Commands.m_82127_("lockSdt").executes(ctx -> {
                           applyTargets(ctx, p -> lockSdt(p, true));
                           return 0;
                        })))
                     .then(Commands.m_82127_("unlockSdt").executes(ctx -> {
                        applyTargets(ctx, p -> lockSdt(p, false));
                        return 0;
                     })))
                  .then(Commands.m_82127_("toggleSdt").executes(ctx -> {
                     applyTargets(ctx, SetPlayerStateCommands::toggleSdt);
                     return 0;
                  })))
               .then(Commands.m_82127_("setSdtPhase").then(Commands.m_82129_("value", IntegerArgumentType.integer(0, 4)).executes(ctx -> {
                  applyTargets(ctx, p -> setSdtPhase(p, IntegerArgumentType.getInteger(ctx, "value")));
                  return 0;
               }))))
            .then(Commands.m_82127_("resetYamatoState").executes(ctx -> {
               applyTargets(ctx, SetPlayerStateCommands::resetYamatoState);
               return 0;
            }))
      );
   }

   private static ServerPlayerPatch patch(Player p) {
      return (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(p, ServerPlayerPatch.class);
   }

   private static SkillContainer weaponInnate(Player p) {
      return patch(p).getSkill(SkillSlots.WEAPON_INNATE);
   }

   private static void setConc(Player p, float v) {
      ConcentrationManager.setConcentrationRaw(weaponInnate(p), v);
   }

   private static void addConc(Player p, float delta) {
      SkillContainer container = weaponInnate(p);
      ConcentrationManager.setConcentrationRaw(container, ConcentrationManager.getConcentration(container) + delta);
   }

   private static void setSdtVal(Player p, float v) {
      SinDevilTriggerManager.setSDTValueRaw(p, v);
   }

   private static void addSdtVal(Player p, float delta) {
      float current = YamatoPlayerStateProvider.get(p).getSdtValue();
      SinDevilTriggerManager.setSDTValueRaw(p, current + delta);
   }

   private static void toggleSdt(Player p) {
      SkillContainer container = weaponInnate(p);
      if (container.getSkill() instanceof VergilSkill ys) {
         ys.getSDTManager().toggleSdt(container, p);
      }
   }

   private static void setSdtPhase(Player p, int phase) {
      SkillDataManager dm = weaponInnate(p).getDataManager();
      if (dm.hasData((SkillDataKey)DMCSkillDataKeys.SDT_PHASE.get())) {
         dm.setDataSync((SkillDataKey)DMCSkillDataKeys.SDT_PHASE.get(), phase);
      }
   }

   private static void lockConc(Player p, boolean locked) {
      ConcentrationManager.setAdminLock(p.m_20148_(), locked);
      if (locked) {
         setConc(p, 10000.0F);
      }
   }

   private static void lockSdt(Player p, boolean locked) {
      SinDevilTriggerManager.setAdminLock(p.m_20148_(), locked);
      if (locked) {
         setSdtVal(p, 1000.0F);
      }
   }

   private static void resetYamatoState(Player p) {
      YamatoPlayerState state = YamatoPlayerStateProvider.get(p);
      state.setSdtValue(0.0F);
      state.setDtStack(0);
      state.setDtResource(0.0F);
      SkillContainer container = weaponInnate(p);
      ConcentrationManager.setConcentrationRaw(container, 0.0F);
      SkillDataManager dm = container.getDataManager();
      if (dm.hasData((SkillDataKey)DMCSkillDataKeys.SDT_VALUE.get())) {
         dm.setDataSync((SkillDataKey)DMCSkillDataKeys.SDT_VALUE.get(), 0.0F);
      }

      if (container.getSkill() instanceof VergilSkill) {
         container.getSkill().setStackSynchronize(container, 0);
         container.getSkill().setConsumptionSynchronize(container, 0.0F);
      }
   }

   private static void applySelf(CommandSourceStack src, SetPlayerStateCommands.PlayerAction action) {
      try {
         if (src.m_230896_() != null) {
            action.run(src.m_230896_());
         }
      } catch (Exception var3) {
      }
   }

   private static void applyTargets(CommandContext<CommandSourceStack> ctx, SetPlayerStateCommands.PlayerAction action) {
      try {
         for (Player player : EntityArgument.m_91477_(ctx, "players")) {
            action.run(player);
         }
      } catch (Exception var4) {
      }
   }

   @FunctionalInterface
   private interface PlayerAction {
      void run(Player var1) throws Exception;
   }
}
