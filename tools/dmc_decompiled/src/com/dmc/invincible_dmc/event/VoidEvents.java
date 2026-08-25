package com.dmc.invincible_dmc.event;

import com.dmc.invincible_dmc.entity.DMCEntities;
import com.dmc.invincible_dmc.entity.dummy.DummyEntity;
import com.dmc.invincible_dmc.entity.portal.PortalEntity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraftforge.event.TickEvent.LevelTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
   modid = "invincible_dmc"
)
public class VoidEvents {
   private static final String NATURAL_VOID_DUMMY_TAG = "invincible_dmc:natural_void_dummy";
   private static final double DUMMY_SPAWN_X = -5.0;
   private static final double DUMMY_SPAWN_Y = -0.7;
   private static final double DUMMY_SPAWN_Z = -5.0;
   private static final double LEGACY_DUMMY_POSITION_TOLERANCE_SQR = 1.0;
   private static final Set<ServerLevel> PENDING_VOID_INITIALIZATIONS = Collections.newSetFromMap(new WeakHashMap<>());
   public static final ResourceKey<Level> VOID_KEY = ResourceKey.m_135785_(
      Registries.f_256858_, ResourceLocation.fromNamespaceAndPath("invincible_dmc", "void")
   );

   @SubscribeEvent
   public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
      if (event.getLevel() instanceof ServerLevel serverLevel) {
         if (serverLevel.m_46472_().equals(VOID_KEY)) {
            if (event.getEntity() instanceof Player) {
               PENDING_VOID_INITIALIZATIONS.add(serverLevel);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onVoidLevelTick(LevelTickEvent event) {
      if (event.phase == Phase.END) {
         if (event.level instanceof ServerLevel serverLevel) {
            if (serverLevel.m_46472_().equals(VOID_KEY)) {
               if (PENDING_VOID_INITIALIZATIONS.remove(serverLevel)) {
                  if (!serverLevel.m_6907_().isEmpty()) {
                     ensureVoidEntities(serverLevel);
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
      if (event.getEntity() instanceof Player player) {
         if (event.getLevel() instanceof ServerLevel serverLevel) {
            if (serverLevel.m_46472_().equals(VOID_KEY)) {
               long remainingPlayers = serverLevel.m_6907_().stream().filter(p -> p != player && !p.m_213877_()).count();
               if (remainingPlayers == 0L) {
                  clearVoidEntities(serverLevel);
               }
            }
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onLivingHurt(LivingHurtEvent event) {
      if (event.getEntity() instanceof Player) {
         if (event.getEntity().m_9236_().m_46472_().equals(VOID_KEY)) {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void onBlockBreak(BreakEvent event) {
      if (event.getPlayer().m_9236_().m_46472_().equals(VOID_KEY)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onBlockPlace(EntityPlaceEvent event) {
      if (Objects.requireNonNull(event.getEntity()).m_9236_().m_46472_().equals(VOID_KEY)) {
         event.setCanceled(true);
      }
   }

   private static void clearVoidEntities(ServerLevel serverLevel) {
      PENDING_VOID_INITIALIZATIONS.remove(serverLevel);
      serverLevel.m_143280_((EntityTypeTest)DMCEntities.DUMMY.get(), dummy -> true).forEach(Entity::m_146870_);
      serverLevel.m_143280_((EntityTypeTest)DMCEntities.PORTAL.get(), portal -> true).forEach(Entity::m_146870_);
   }

   private static void ensureVoidEntities(ServerLevel serverLevel) {
      Player owner = (Player)serverLevel.m_6907_().get(0);
      ensureSingleDummy(serverLevel);
      ensureSinglePortal(serverLevel, owner);
   }

   private static void ensureSingleDummy(ServerLevel serverLevel) {
      ArrayList<DummyEntity> dummies = new ArrayList<>(serverLevel.m_143280_((EntityTypeTest)DMCEntities.DUMMY.get(), VoidEvents::isNaturalVoidDummy));
      discardExcessEntities(dummies);
      if (dummies.isEmpty()) {
         DummyEntity dummy = (DummyEntity)((EntityType)DMCEntities.DUMMY.get()).m_20615_(serverLevel);
         if (dummy != null) {
            markNaturalVoidDummy(dummy);
            dummy.m_6034_(-5.0, -0.7, -5.0);
            serverLevel.m_7967_(dummy);
         }
      } else {
         markNaturalVoidDummy(dummies.get(0));
      }
   }

   private static boolean isNaturalVoidDummy(DummyEntity dummy) {
      return dummy.getPersistentData().m_128471_("invincible_dmc:natural_void_dummy") || dummy.m_20275_(-5.0, -0.7, -5.0) <= 1.0;
   }

   private static void markNaturalVoidDummy(DummyEntity dummy) {
      dummy.getPersistentData().m_128379_("invincible_dmc:natural_void_dummy", true);
   }

   private static void ensureSinglePortal(ServerLevel serverLevel, Player owner) {
      ArrayList<PortalEntity> portals = new ArrayList<>(serverLevel.m_143280_((EntityTypeTest)DMCEntities.PORTAL.get(), portalx -> true));
      discardExcessEntities(portals);
      if (portals.isEmpty()) {
         PortalEntity portal = (PortalEntity)((EntityType)DMCEntities.PORTAL.get()).m_20615_(serverLevel);
         if (portal != null) {
            portal.m_6034_(5.0, -0.7, 5.0);
            portal.setOwner(owner);
            portal.setInfiniteLifetime(true);
            serverLevel.m_7967_(portal);
         }
      } else {
         portals.get(0).setInfiniteLifetime(true);
      }
   }

   private static void discardExcessEntities(ArrayList<? extends Entity> entities) {
      for (int index = 1; index < entities.size(); index++) {
         entities.get(index).m_146870_();
      }
   }
}
