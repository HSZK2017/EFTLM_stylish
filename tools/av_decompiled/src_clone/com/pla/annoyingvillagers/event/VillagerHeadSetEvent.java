package com.pla.annoyingvillagers.event;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.task.DelayedTask;
import com.pla.annoyingvillagers.util.TeamUtil;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   modid = "annoyingvillagers",
   bus = Bus.FORGE
)
public class VillagerHeadSetEvent {
   private static final String VILLAGER_HEAD_MODE_KEY = "villager_head";
   private static final String VILLAGER_HEAD_COOLDOWN_KEY = "villager_head_used";

   @SubscribeEvent
   public static void onRightClickBlock(RightClickBlock rightclickblock) {
      if (rightclickblock.getHand() == rightclickblock.getEntity().m_7655_()) {
         execute(rightclickblock, rightclickblock.getLevel(), rightclickblock.getEntity());
      }
   }

   @SubscribeEvent
   public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
      if (event.getSlot() == EquipmentSlot.HEAD) {
         boolean hadVillagerHead = event.getFrom().m_41720_() == AnnoyingVillagersModItems.VILLAGER_HEAD.get();
         boolean hasVillagerHead = event.getTo().m_41720_() == AnnoyingVillagersModItems.VILLAGER_HEAD.get();
         if (hadVillagerHead != hasVillagerHead) {
            Entity entity = event.getEntity();
            entity.getPersistentData().m_128379_("villager_head", false);
            entity.getPersistentData().m_128379_("villager_head_used", false);
            if (hasVillagerHead) {
               TeamUtil.addOrJoinTeam(entity, "villagers");
               if (entity instanceof Player player && !player.m_9236_().m_5776_()) {
                  player.m_5661_(Component.m_237113_("You have put on the villager helmet. Villager soldiers will no longer attack you."), false);
               }
            } else {
               TeamUtil.leaveTeam(entity, "villagers");
               if (entity instanceof Player player && !player.m_9236_().m_5776_()) {
                  player.m_5661_(Component.m_237113_("You have removed your helmet. Villager soldiers will now attack you."), false);
               }
            }
         }
      }
   }

   public static void execute(LevelAccessor levelaccessor, Entity entity) {
      execute((Event)null, levelaccessor, entity);
   }

   private static void execute(@Nullable Event event, LevelAccessor levelaccessor, final Entity entity) {
      if (entity != null && entity.m_6144_()) {
         ItemStack itemstack;
         if (entity instanceof LivingEntity livingentity) {
            itemstack = livingentity.m_6844_(EquipmentSlot.HEAD);
         } else {
            itemstack = ItemStack.f_41583_;
         }

         if (itemstack.m_41720_() == AnnoyingVillagersModItems.VILLAGER_HEAD.get()) {
            if (!entity.getPersistentData().m_128471_("villager_head")) {
               if (!entity.getPersistentData().m_128471_("villager_head_used")) {
                  if (!entity.m_9236_().m_5776_() && entity.m_20194_() != null) {
                     try {
                        entity.m_20194_().m_129892_().m_82094_().execute("team leave @s[team=villagers]", entity.m_20203_().m_81324_().m_81325_(4));
                     } catch (CommandSyntaxException var7) {
                     }
                  }

                  if (entity instanceof Player player && !player.m_9236_().m_5776_()) {
                     player.m_5661_(Component.m_237113_("Switched to Attack Mode"), false);
                  }

                  entity.getPersistentData().m_128379_("villager_head_used", true);
                  new DelayedTask(200) {
                     @Override
                     public void run() {
                        entity.getPersistentData().m_128379_("villager_head", true);
                        entity.getPersistentData().m_128379_("villager_head_used", false);
                     }
                  };
               } else if (entity instanceof Player player && !player.m_9236_().m_5776_()) {
                  player.m_5661_(Component.m_237113_("On Cooldown"), true);
               }
            } else if (entity.getPersistentData().m_128471_("villager_head")) {
               if (!entity.getPersistentData().m_128471_("villager_head_used")) {
                  if (!entity.m_9236_().m_5776_() && entity.m_20194_() != null) {
                     try {
                        entity.m_20194_().m_129892_().m_82094_().execute("team join villagers @s", entity.m_20203_().m_81324_().m_81325_(4));
                     } catch (CommandSyntaxException var6) {
                     }
                  }

                  if (entity instanceof Player player && !player.m_9236_().m_5776_()) {
                     player.m_5661_(Component.m_237113_("Switched to Disguise Mode"), false);
                  }

                  entity.getPersistentData().m_128379_("villager_head_used", true);
                  new DelayedTask(200) {
                     @Override
                     public void run() {
                        entity.getPersistentData().m_128379_("villager_head", false);
                        entity.getPersistentData().m_128379_("villager_head_used", false);
                     }
                  };
               } else if (entity instanceof Player player && !player.m_9236_().m_5776_()) {
                  player.m_5661_(Component.m_237113_("On Cooldown"), true);
               }
            }
         }
      }
   }
}
