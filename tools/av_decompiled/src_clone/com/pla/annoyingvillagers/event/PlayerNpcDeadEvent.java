package com.pla.annoyingvillagers.event;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import com.pla.annoyingvillagers.entity.PlayerNpcEntity;
import com.pla.annoyingvillagers.task.DelayedTask;
import com.pla.annoyingvillagers.util.ChatUtil;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber
public class PlayerNpcDeadEvent {
   @SubscribeEvent
   public static void onEntityDeath(LivingDeathEvent livingdeathevent) {
      if (livingdeathevent != null && livingdeathevent.getEntity() != null) {
         execute(
            livingdeathevent,
            livingdeathevent.getEntity().m_9236_(),
            livingdeathevent.getEntity().m_20185_(),
            livingdeathevent.getEntity().m_20186_(),
            livingdeathevent.getEntity().m_20189_(),
            livingdeathevent.getEntity(),
            livingdeathevent.getSource().m_7639_()
         );
      }
   }

   public static void execute(LevelAccessor levelaccessor, double d0, double d1, double d2, Entity entity, Entity entity1) {
      execute((Event)null, levelaccessor, d0, d1, d2, entity, entity1);
   }

   private static void execute(
      @Nullable Event event, final LevelAccessor levelaccessor, final double d0, final double d1, final double d2, final Entity entity, final Entity entity1
   ) {
      if (entity != null && entity1 != null) {
         if (entity1 instanceof PlayerNpcEntity) {
            new DelayedTask(Mth.m_216271_(RandomSource.m_216327_(), 70, 100)) {
               @Override
               public void run() {
                  if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_CHAT.get()) {
                     if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_().m_6846_().m_240416_(Component.m_237113_("<" + entity1.m_5446_().getString() + "> fw"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_().m_6846_().m_240416_(Component.m_237113_("<" + entity1.m_5446_().getString() + "> Is that all ?"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_().m_6846_().m_240416_(Component.m_237113_("<" + entity1.m_5446_().getString() + "> LLLLLLLLLLLLL"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_()
                              .m_6846_()
                              .m_240416_(Component.m_237113_("<" + entity1.m_5446_().getString() + "> Hey, what happened to you?"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_().m_6846_().m_240416_(Component.m_237113_("<" + entity1.m_5446_().getString() + "> Poet's grasp"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist = levelaccessor.m_7654_().m_6846_();
                           String s = entity1.m_5446_().getString();
                           playerlist.m_240416_(
                              Component.m_237113_("<" + s + "> " + entity.m_5446_().getString() + ", is that all the strength you've got?"), false
                           );
                        }
                     } else if (Math.random() <= 0.1) {
                        if (ForgeRegistries.ENTITY_TYPES.getKey(entity.m_6095_()).toString().equals("minecraft:player")
                           && !levelaccessor.m_5776_()
                           && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist = levelaccessor.m_7654_().m_6846_();
                           String s = entity1.m_5446_().getString();
                           playerlist.m_240416_(
                              Component.m_237113_("<" + s + "> " + entity.m_5446_().getString() + ", that's all the Little Hajiki can do?"), false
                           );
                        }
                     } else if (Math.random() <= 0.05) {
                        if (ForgeRegistries.ENTITY_TYPES.getKey(entity.m_6095_()).toString().equals("minecraft:player")
                           && !levelaccessor.m_5776_()
                           && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist = levelaccessor.m_7654_().m_6846_();
                           String s = entity1.m_5446_().getString();
                           playerlist.m_240416_(Component.m_237113_("<" + s + "> " + entity.m_5446_().getString() + ", you're godlike too"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist = levelaccessor.m_7654_().m_6846_();
                           String s = entity1.m_5446_().getString();
                           playerlist.m_240416_(
                              Component.m_237113_(
                                 "<"
                                    + s
                                    + "> Hahaha \ud83d\ude02 so funny, a "
                                    + entity.m_5446_().getString()
                                    + " is lying in bed, rhythmically chanting “Garen~ fafah”"
                              ),
                              false
                           );
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist = levelaccessor.m_7654_().m_6846_();
                           String s = entity1.m_5446_().getString();
                           playerlist.m_240416_(Component.m_237113_("<" + s + "> " + entity.m_5446_().getString() + ", don't act tough if you're weak"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist = levelaccessor.m_7654_().m_6846_();
                           String s = entity1.m_5446_().getString();
                           playerlist.m_240416_(Component.m_237113_("<" + s + "> " + entity.m_5446_().getString() + " plays like a bot"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist = levelaccessor.m_7654_().m_6846_();
                           String s = entity1.m_5446_().getString();
                           playerlist.m_240416_(
                              Component.m_237113_("<" + s + "> " + entity.m_5446_().getString() + ", I'm dying of laughter, a total \ud83e\udd21"), false
                           );
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist = levelaccessor.m_7654_().m_6846_();
                           String s = entity1.m_5446_().getString();
                           playerlist.m_240416_(
                              Component.m_237113_(
                                 "<" + s + "> Even giving it your all, you still can't defeat me? Haki " + entity.m_5446_().getString() + ", you bastard!"
                              ),
                              false
                           );
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist = levelaccessor.m_7654_().m_6846_();
                           String s = entity1.m_5446_().getString();
                           playerlist.m_240416_(Component.m_237113_("<" + s + "> Joke " + entity.m_5446_().getString()), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist = levelaccessor.m_7654_().m_6846_();
                           String s = entity1.m_5446_().getString();
                           playerlist.m_240416_(
                              Component.m_237113_("<" + s + "> " + entity.m_5446_().getString() + ", weren't you acting all tough, bro?"), false
                           );
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_().m_6846_().m_240416_(Component.m_237113_("<" + entity1.m_5446_().getString() + "> lol"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist = levelaccessor.m_7654_().m_6846_();
                           String s = entity1.m_5446_().getString();
                           playerlist.m_240416_(
                              Component.m_237113_(
                                 "<"
                                    + s
                                    + "> "
                                    + entity.m_5446_().getString()
                                    + "\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95 So weak!"
                              ),
                              false
                           );
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist = levelaccessor.m_7654_().m_6846_();
                           String s = entity1.m_5446_().getString();
                           playerlist.m_240416_(
                              Component.m_237113_("<" + s + "> " + entity.m_5446_().getString() + ", if you're bad, just practice more, little bro"), false
                           );
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_().m_6846_().m_240416_(Component.m_237113_("<" + entity1.m_5446_().getString() + "> It's over"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_()
                              .m_6846_()
                              .m_240416_(Component.m_237113_("<" + entity1.m_5446_().getString() + "> Haha, you're getting mad"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_().m_6846_().m_240416_(Component.m_237113_("<" + entity1.m_5446_().getString() + "> Oh really?"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_()
                              .m_6846_()
                              .m_240416_(Component.m_237113_("<" + entity1.m_5446_().getString() + "> \ud83e\udd13\ud83e\udd13\ud83e\udd13"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_()
                              .m_6846_()
                              .m_240416_(Component.m_237113_("<" + entity1.m_5446_().getString() + "> I pinned you to the ground and beat you up, haha"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_()
                              .m_6846_()
                              .m_240416_(
                                 Component.m_237113_(
                                    "<" + entity1.m_5446_().getString() + "> So bad? Could it be that you've played too much Genshin Impact?\ud83d\ude05"
                                 ),
                                 false
                              );
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_().m_6846_().m_240416_(Component.m_237113_("<" + entity1.m_5446_().getString() + "> Desperate now?"), false);
                        }
                     } else if (Math.random() <= 0.05 && !levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                        levelaccessor.m_7654_()
                           .m_6846_()
                           .m_240416_(
                              Component.m_237113_("<" + entity1.m_5446_().getString() + ">, all you can do is spam left-click to death\ud83e\udd13"), false
                           );
                     }
                  }
               }
            };
         }

         if (entity instanceof PlayerNpcEntity) {
            if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
               PlayerList playerlist = levelaccessor.m_7654_().m_6846_();
               String s = entity.m_5446_().getString();
               if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_CHAT.get()) {
                  playerlist.m_240416_(Component.m_237113_(s + " was killed by " + entity1.m_5446_().getString()), false);
               }
            }

            new DelayedTask(5) {
               @Override
               public void run() {
                  if (!entity.getPersistentData().m_128471_("die_by_possess")) {
                     LevelAccessor levelaccessor1 = levelaccessor;
                     LivingEntity livingentity = (LivingEntity)entity;
                     if (levelaccessor1 instanceof Level level && !level.m_5776_()) {
                        ItemStack itemstack;
                        if (entity instanceof LivingEntity) {
                           itemstack = livingentity.m_6844_(EquipmentSlot.FEET);
                        } else {
                           itemstack = ItemStack.f_41583_;
                        }

                        ItemEntity itementity = new ItemEntity(level, d0, d1 + 1.0, d2, itemstack);
                        itementity.m_32010_(10);
                        level.m_7967_(itementity);
                     }

                     if (levelaccessor instanceof Level level && !level.m_5776_()) {
                        ItemStack itemstack;
                        if (entity instanceof LivingEntity) {
                           itemstack = livingentity.m_6844_(EquipmentSlot.LEGS);
                        } else {
                           itemstack = ItemStack.f_41583_;
                        }

                        ItemEntity itementity = new ItemEntity(level, d0, d1 + 1.0, d2, itemstack);
                        itementity.m_32010_(10);
                        level.m_7967_(itementity);
                     }

                     if (levelaccessor instanceof Level level && !level.m_5776_()) {
                        ItemStack itemstack;
                        if (entity instanceof LivingEntity) {
                           itemstack = livingentity.m_6844_(EquipmentSlot.CHEST);
                        } else {
                           itemstack = ItemStack.f_41583_;
                        }

                        ItemEntity itementity = new ItemEntity(level, d0, d1 + 1.0, d2, itemstack);
                        itementity.m_32010_(10);
                        level.m_7967_(itementity);
                     }

                     if (levelaccessor instanceof Level level && !level.m_5776_()) {
                        ItemStack itemstack;
                        if (entity instanceof LivingEntity) {
                           itemstack = livingentity.m_6844_(EquipmentSlot.HEAD);
                        } else {
                           itemstack = ItemStack.f_41583_;
                        }

                        ItemEntity itementity = new ItemEntity(level, d0, d1 + 1.0, d2, itemstack);
                        itementity.m_32010_(10);
                        level.m_7967_(itementity);
                     }

                     if (levelaccessor instanceof Level level && !level.m_5776_()) {
                        ItemStack itemstack;
                        if (entity instanceof LivingEntity) {
                           itemstack = livingentity.m_21205_();
                        } else {
                           itemstack = ItemStack.f_41583_;
                        }

                        ItemEntity itementity = new ItemEntity(level, d0, d1 + 1.0, d2, itemstack);
                        itementity.m_32010_(10);
                        level.m_7967_(itementity);
                     }

                     if (levelaccessor instanceof Level level && !level.m_5776_()) {
                        ItemStack itemstack;
                        if (entity instanceof LivingEntity) {
                           itemstack = livingentity.m_21206_();
                        } else {
                           itemstack = ItemStack.f_41583_;
                        }

                        ItemEntity itementity = new ItemEntity(level, d0, d1 + 1.0, d2, itemstack);
                        itementity.m_32010_(10);
                        level.m_7967_(itementity);
                     }
                  }

                  if (levelaccessor instanceof Level level && !level.m_5776_()) {
                     ItemEntity itementity = new ItemEntity(level, d0, d1 + 1.0, d2, new ItemStack(Blocks.f_50705_));
                     itementity.m_32010_(10);
                     level.m_7967_(itementity);
                  }

                  if (levelaccessor instanceof Level level && !level.m_5776_()) {
                     ItemEntity itementity = new ItemEntity(level, d0, d1 + 1.0, d2, new ItemStack(Blocks.f_50705_));
                     itementity.m_32010_(10);
                     level.m_7967_(itementity);
                  }

                  if (levelaccessor instanceof Level level && !level.m_5776_()) {
                     ItemEntity itementity = new ItemEntity(level, d0, d1 + 1.0, d2, new ItemStack(Items.f_42411_));
                     itementity.m_32010_(10);
                     level.m_7967_(itementity);
                  }

                  if (levelaccessor instanceof Level level && !level.m_5776_()) {
                     ItemEntity itementity = new ItemEntity(level, d0, d1 + 1.0, d2, new ItemStack(Items.f_42412_));
                     itementity.m_32010_(10);
                     level.m_7967_(itementity);
                  }

                  if (levelaccessor instanceof Level level && !level.m_5776_()) {
                     ItemEntity itementity = new ItemEntity(level, d0, d1 + 1.0, d2, new ItemStack(Items.f_42412_));
                     itementity.m_32010_(10);
                     level.m_7967_(itementity);
                  }

                  if (levelaccessor instanceof Level level && !level.m_5776_()) {
                     ItemEntity itementity = new ItemEntity(level, d0, d1 + 1.0, d2, new ItemStack(Items.f_42412_));
                     itementity.m_32010_(10);
                     level.m_7967_(itementity);
                  }

                  if (levelaccessor instanceof Level level && !level.m_5776_()) {
                     ItemEntity itementity = new ItemEntity(level, d0, d1 + 1.0, d2, new ItemStack(Items.f_42412_));
                     itementity.m_32010_(10);
                     level.m_7967_(itementity);
                  }

                  if (levelaccessor instanceof Level level && !level.m_5776_()) {
                     ItemEntity itementity = new ItemEntity(level, d0, d1 + 1.0, d2, new ItemStack(Items.f_42584_));
                     itementity.m_32010_(10);
                     level.m_7967_(itementity);
                  }

                  if (levelaccessor instanceof Level level && !level.m_5776_()) {
                     ItemEntity itementity = new ItemEntity(level, d0, d1 + 1.0, d2, new ItemStack(Items.f_42584_));
                     itementity.m_32010_(10);
                     level.m_7967_(itementity);
                  }

                  if (levelaccessor instanceof Level level && !level.m_5776_()) {
                     ItemEntity itementity = new ItemEntity(level, d0, d1 + 1.0, d2, new ItemStack(Items.f_42436_));
                     itementity.m_32010_(10);
                     level.m_7967_(itementity);
                  }

                  if (levelaccessor instanceof Level level && !level.m_5776_()) {
                     ItemEntity itementity = new ItemEntity(level, d0, d1 + 1.0, d2, new ItemStack(Items.f_42436_));
                     itementity.m_32010_(10);
                     level.m_7967_(itementity);
                  }
               }
            };
            if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_CHAT.get()) {
               new DelayedTask(Mth.m_216271_(RandomSource.m_216327_(), 40, 80)) {
                  @Override
                  public void run() {
                     if (Math.random() <= 0.05) {
                        Entity entity2 = entity;
                        if (!entity2.m_9236_().m_5776_() && entity2.m_20194_() != null) {
                           try {
                              entity2.m_20194_()
                                 .m_129892_()
                                 .m_82094_()
                                 .execute(
                                    "tellraw @a [{\"text\":\"<\"},{\"selector\":\"@s\"},{\"text\":\"> "
                                       + entity1.m_5446_().getString()
                                       + " Bro, I'll remember you for this\ud83d\ude21\"}]",
                                    entity2.m_20203_().m_81324_().m_81325_(4)
                                 );
                           } catch (CommandSyntaxException var6) {
                           }
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_()
                              .m_6846_()
                              .m_240416_(Component.m_237113_("<" + entity.m_5446_().getString() + "> I'm breaking down\ud83d\ude2d"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist1 = levelaccessor.m_7654_().m_6846_();
                           String s1 = entity.m_5446_().getString();
                           playerlist1.m_240416_(Component.m_237113_("<" + s1 + "> " + entity1.m_5446_().getString() + ", f** you"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_()
                              .m_6846_()
                              .m_240416_(Component.m_237113_("<" + entity.m_5446_().getString() + "> So speechless\ud83d\ude05"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_()
                              .m_6846_()
                              .m_240416_(Component.m_237113_("<" + entity.m_5446_().getString() + "> 666 this guy is a boss"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_()
                              .m_6846_()
                              .m_240416_(Component.m_237113_("<" + entity.m_5446_().getString() + "> That was tough\ud83d\ude05"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_()
                              .m_6846_()
                              .m_240416_(Component.m_237113_("<" + entity.m_5446_().getString() + "> I really give up\ud83d\ude2d"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist1 = levelaccessor.m_7654_().m_6846_();
                           String s1 = entity.m_5446_().getString();
                           playerlist1.m_240416_(Component.m_237113_("<" + s1 + "> " + entity1.m_5446_().getString() + " , your should die. F**"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist1 = levelaccessor.m_7654_().m_6846_();
                           String s1 = entity.m_5446_().getString();
                           playerlist1.m_240416_(
                              Component.m_237113_("<" + s1 + "> " + entity1.m_5446_().getString() + " bro, be honest, are you hacking?\ud83d\ude05"), false
                           );
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_()
                              .m_6846_()
                              .m_240416_(
                                 Component.m_237113_("<" + entity.m_5446_().getString() + "> Played as Bull Demon, lost all my gear\ud83d\ude05"), false
                              );
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist1 = levelaccessor.m_7654_().m_6846_();
                           String s1 = entity.m_5446_().getString();
                           playerlist1.m_240416_(
                              Component.m_237113_("<" + s1 + "> " + entity1.m_5446_().getString() + ", I'll get you later\ud83d\ude21"), false
                           );
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist1 = levelaccessor.m_7654_().m_6846_();
                           String s1 = entity.m_5446_().getString();
                           playerlist1.m_240416_(
                              Component.m_237113_("<" + s1 + "> " + entity1.m_5446_().getString() + ", I will take revenge on you soon\ud83d\ude21"), false
                           );
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist1 = levelaccessor.m_7654_().m_6846_();
                           String s1 = entity.m_5446_().getString();
                           playerlist1.m_240416_(
                              Component.m_237113_(
                                 "<" + s1 + "> " + entity1.m_5446_().getString() + ", f*** you! Dare to fight fair with proper gear?\ud83d\ude21"
                              ),
                              false
                           );
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist1 = levelaccessor.m_7654_().m_6846_();
                           String s1 = entity.m_5446_().getString();
                           playerlist1.m_240416_(Component.m_237113_("<" + s1 + "> " + entity1.m_5446_().getString() + ", you're dead for sure"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist1 = levelaccessor.m_7654_().m_6846_();
                           String s1 = entity.m_5446_().getString();
                           playerlist1.m_240416_(
                              Component.m_237113_(
                                 "<" + s1 + "> " + entity1.m_5446_().getString() + ", you just killed me like that. Are you happy now?\ud83d\ude05"
                              ),
                              false
                           );
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist1 = levelaccessor.m_7654_().m_6846_();
                           String s1 = entity.m_5446_().getString();
                           playerlist1.m_240416_(
                              Component.m_237113_("<" + s1 + "> Please, " + entity1.m_5446_().getString() + ", don't burnt my items !!!!"), false
                           );
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_()
                              .m_6846_()
                              .m_240416_(Component.m_237113_("<" + entity.m_5446_().getString() + "> ????????????????????????????????"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_()
                              .m_6846_()
                              .m_240416_(Component.m_237113_("<" + entity.m_5446_().getString() + "> I haven't even gotten serious yet"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist1 = levelaccessor.m_7654_().m_6846_();
                           String s1 = entity.m_5446_().getString();
                           playerlist1.m_240416_(
                              Component.m_237113_("<" + s1 + "> " + entity1.m_5446_().getString() + ", I'll get you next time\ud83d\ude21"), false
                           );
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist1 = levelaccessor.m_7654_().m_6846_();
                           String s1 = entity.m_5446_().getString();
                           playerlist1.m_240416_(
                              Component.m_237113_("<" + s1 + "> " + entity1.m_5446_().getString() + ", you are really a \ud83d\udc36"), false
                           );
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           PlayerList playerlist1 = levelaccessor.m_7654_().m_6846_();
                           String s1 = entity.m_5446_().getString();
                           playerlist1.m_240416_(
                              Component.m_237113_(
                                 "<"
                                    + s1
                                    + "> "
                                    + entity1.m_5446_().getString()
                                    + " , ambushing an ordinary player like me, is this okay? No, it's not\ud83d\udc4e"
                              ),
                              false
                           );
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_().m_6846_().m_240416_(Component.m_237113_("<" + entity.m_5446_().getString() + "> ......"), false);
                        }
                     } else if (Math.random() <= 0.05) {
                        if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                           levelaccessor.m_7654_()
                              .m_6846_()
                              .m_240416_(
                                 Component.m_237113_(
                                    "<"
                                       + entity.m_5446_().getString()
                                       + "> Idiot, f** you \ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95\ud83d\udd95"
                                 ),
                                 false
                              );
                        }
                     } else if (Math.random() <= 0.05) {
                        Entity entity2 = entity;
                        if (!entity2.m_9236_().m_5776_() && entity2.m_20194_() != null) {
                           try {
                              entity2.m_20194_()
                                 .m_129892_()
                                 .m_82094_()
                                 .execute(
                                    "tellraw @a [{\"text\":\"<\"},{\"selector\":\"@s\"},{\"text\":\"> Don't leave, I'm calling some people\ud83d\ude21\ud83d\ude21\ud83d\ude21\"}]",
                                    entity2.m_20203_().m_81324_().m_81325_(4)
                                 );
                           } catch (CommandSyntaxException var5) {
                           }
                        }

                        new DelayedTask(50) {
                           @Override
                           public void run() {
                              try {
                                 entity.m_20194_()
                                    .m_129892_()
                                    .m_82094_()
                                    .execute("summon annoyingvillagers:player_npc", entity.m_20203_().m_81324_().m_81325_(4));
                              } catch (CommandSyntaxException var2) {
                              }
                           }
                        };
                        new DelayedTask(20) {
                           @Override
                           public void run() {
                              try {
                                 entity.m_20194_()
                                    .m_129892_()
                                    .m_82094_()
                                    .execute("summon annoyingvillagers:player_npc", entity.m_20203_().m_81324_().m_81325_(4));
                              } catch (CommandSyntaxException var2) {
                              }
                           }
                        };
                        new DelayedTask(20) {
                           @Override
                           public void run() {
                              try {
                                 entity.m_20194_()
                                    .m_129892_()
                                    .m_82094_()
                                    .execute("summon annoyingvillagers:player_npc", entity.m_20203_().m_81324_().m_81325_(4));
                              } catch (CommandSyntaxException var2) {
                              }
                           }
                        };
                        new DelayedTask(20) {
                           @Override
                           public void run() {
                              try {
                                 entity.m_20194_()
                                    .m_129892_()
                                    .m_82094_()
                                    .execute(
                                       "tellraw @a [{\"text\":\"<\"},{\"selector\":\"@s\"},{\"text\":\"> Hmmm, you're here… bro.\ud83e\udd13\"}]",
                                       entity.m_20203_().m_81324_().m_81325_(4)
                                    );
                              } catch (CommandSyntaxException var2) {
                              }
                           }
                        };
                     } else if (Math.random() <= 0.05 && !levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                        PlayerList playerlist1 = levelaccessor.m_7654_().m_6846_();
                        String s1 = entity.m_5446_().getString();
                        playerlist1.m_240416_(
                           Component.m_237113_("<" + s1 + "> " + entity1.m_5446_().getString() + ", using your OP weapon, is that fun for you?\ud83d\ude05"),
                           false
                        );
                     }

                     new DelayedTask(Mth.m_216271_(RandomSource.m_216327_(), 25, 100)) {
                        @Override
                        public void run() {
                           if (!levelaccessor.m_5776_() && levelaccessor.m_7654_() != null) {
                              ChatUtil.leaveGame(entity);
                           }
                        }
                     };
                  }
               };
            }
         }
      }
   }
}
