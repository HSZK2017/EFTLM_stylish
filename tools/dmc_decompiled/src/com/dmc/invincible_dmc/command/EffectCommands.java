package com.dmc.invincible_dmc.command;

import com.dmc.invincible_dmc.particle.DMCParticles;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

public class EffectCommands {
   static LiteralArgumentBuilder<CommandSourceStack> buildEffectNode() {
      return (LiteralArgumentBuilder<CommandSourceStack>)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.m_82127_("effect")
               .then(
                  Commands.m_82127_("whiteAfterImage")
                     .then(
                        Commands.m_82129_("entity", EntityArgument.m_91460_())
                           .executes(
                              context -> {
                                 for (Entity entity : EntityArgument.m_91461_(context, "entity")) {
                                    EntityPatch<?> entityPatch = EpicFightCapabilities.getEntityPatch(entity, EntityPatch.class);
                                    if (entityPatch != null) {
                                       ((CommandSourceStack)context.getSource())
                                          .m_81372_()
                                          .m_8767_(
                                             (SimpleParticleType)EpicFightParticles.WHITE_AFTERIMAGE.get(),
                                             entity.m_20185_(),
                                             entity.m_20186_(),
                                             entity.m_20189_(),
                                             1,
                                             (double)entity.m_19879_(),
                                             1.0,
                                             1.0,
                                             (double)entity.m_19879_()
                                          );
                                    }
                                 }

                                 return 0;
                              }
                           )
                     )
               ))
            .then(
               Commands.m_82127_("transparentAfterImage")
                  .then(
                     Commands.m_82129_("entity", EntityArgument.m_91460_())
                        .executes(
                           context -> {
                              for (Entity entity : EntityArgument.m_91461_(context, "entity")) {
                                 EntityPatch<?> entityPatch = EpicFightCapabilities.getEntityPatch(entity, EntityPatch.class);
                                 if (entityPatch != null) {
                                    ((CommandSourceStack)context.getSource())
                                       .m_81372_()
                                       .m_8767_(
                                          (SimpleParticleType)DMCParticles.TRANSPARENT_AFTER_IMAGE.get(),
                                          entity.m_20185_(),
                                          entity.m_20186_(),
                                          entity.m_20189_(),
                                          1,
                                          (double)entity.m_19879_(),
                                          1.0,
                                          1.0,
                                          (double)entity.m_19879_()
                                       );
                                 }
                              }

                              return 0;
                           }
                        )
                  )
            ))
         .then(
            Commands.m_82127_("groundSlam")
               .then(
                  Commands.m_82129_("entity", EntityArgument.m_91449_())
                     .then(
                        Commands.m_82129_("radius", DoubleArgumentType.doubleArg())
                           .then(
                              Commands.m_82129_("noSound", BoolArgumentType.bool())
                                 .then(
                                    Commands.m_82129_("noParticle", BoolArgumentType.bool())
                                       .then(
                                          ((RequiredArgumentBuilder)Commands.m_82129_("hurtEntities", BoolArgumentType.bool())
                                                .executes(
                                                   context -> {
                                                      Entity entity = EntityArgument.m_91452_(context, "entity");
                                                      LevelUtil.circleSlamFracture(
                                                         entity instanceof LivingEntity livingEntity ? livingEntity : null,
                                                         entity.m_9236_(),
                                                         entity.m_20182_().m_82520_(0.0, -1.0, 0.0),
                                                         DoubleArgumentType.getDouble(context, "radius"),
                                                         BoolArgumentType.getBool(context, "noSound"),
                                                         BoolArgumentType.getBool(context, "noParticle"),
                                                         BoolArgumentType.getBool(context, "hurtEntities")
                                                      );
                                                      return 0;
                                                   }
                                                ))
                                             .then(
                                                Commands.m_82129_("position", Vec3Argument.m_120841_())
                                                   .executes(
                                                      context -> {
                                                         Entity entity = EntityArgument.m_91452_(context, "entity");
                                                         LevelUtil.circleSlamFracture(
                                                            entity instanceof LivingEntity livingEntity ? livingEntity : null,
                                                            entity.m_9236_(),
                                                            Vec3Argument.m_120844_(context, "position"),
                                                            DoubleArgumentType.getDouble(context, "radius"),
                                                            BoolArgumentType.getBool(context, "noSound"),
                                                            BoolArgumentType.getBool(context, "noParticle"),
                                                            BoolArgumentType.getBool(context, "hurtEntities")
                                                         );
                                                         return 0;
                                                      }
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }
}
