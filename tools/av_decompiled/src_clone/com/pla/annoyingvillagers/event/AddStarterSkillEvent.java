package com.pla.annoyingvillagers.event;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@EventBusSubscriber
public class AddStarterSkillEvent {
   private static final String KEY = "annoyingvillagers:has_joined_before";

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent playerloggedinevent) {
      execute(
         playerloggedinevent,
         playerloggedinevent.getEntity().m_9236_(),
         playerloggedinevent.getEntity().m_20185_(),
         playerloggedinevent.getEntity().m_20186_(),
         playerloggedinevent.getEntity().m_20189_(),
         playerloggedinevent.getEntity()
      );
   }

   @SubscribeEvent
   public static void onClone(Clone event) {
      if (event.isWasDeath()) {
         Player oldPlayer = event.getOriginal();
         Player newPlayer = event.getEntity();
         CompoundTag oldData = oldPlayer.getPersistentData().m_128469_("PlayerPersisted");
         CompoundTag newRoot = newPlayer.getPersistentData();
         CompoundTag newData = newRoot.m_128469_("PlayerPersisted");
         newData.m_128391_(oldData);
         newRoot.m_128365_("PlayerPersisted", newData);
      }
   }

   public static void execute(LevelAccessor levelaccessor, double d0, double d1, double d2, Entity entity) {
      execute(null, levelaccessor, d0, d1, d2, entity);
   }

   private static CompoundTag persisted(Player p) {
      CompoundTag root = p.getPersistentData();
      CompoundTag data = root.m_128469_("PlayerPersisted");
      root.m_128365_("PlayerPersisted", data);
      return data;
   }

   public static boolean hasJoinedBefore(Player p) {
      return persisted(p).m_128471_("annoyingvillagers:has_joined_before");
   }

   public static void markJoined(Player p) {
      persisted(p).m_128379_("annoyingvillagers:has_joined_before", true);
   }

   private static void giveSkill(ServerPlayer player, ServerPlayerPatch patch, SkillSlot slot, Skill skill) {
      if (skill != null) {
         SkillContainer container = patch.getSkillCapability().getSkillContainerFor(slot);
         if (container != null) {
            if (container.setSkill(skill)) {
               if (skill.getCategory().learnable()) {
                  patch.getSkillCapability().addLearnedSkill(skill);
               }

               EpicFightNetworkManager.sendToPlayer(container.createSyncPacketToLocalPlayer(), player, new Object[0]);
               EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(container.createSyncPacketToRemotePlayer(), player, new Object[0]);
            }
         }
      }
   }

   private static void execute(@Nullable Event event, LevelAccessor levelaccessor, double d0, double d1, double d2, Entity entity) {
      if (entity != null
         && entity instanceof ServerPlayer serverPlayer
         && !hasJoinedBefore(serverPlayer)
         && !entity.m_9236_().m_5776_()
         && entity.m_20194_() != null) {
         ServerPlayerPatch playerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(serverPlayer, ServerPlayerPatch.class);
         if (playerPatch == null) {
            return;
         }

         giveSkill(serverPlayer, playerPatch, SkillSlots.GUARD, EpicFightSkills.GUARD);
         giveSkill(serverPlayer, playerPatch, SkillSlots.GUARD, EpicFightSkills.PARRYING);
         giveSkill(serverPlayer, playerPatch, SkillSlots.DODGE, EpicFightSkills.ROLL);
         markJoined((Player)entity);
      }
   }
}
