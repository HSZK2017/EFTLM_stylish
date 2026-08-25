package com.dmc.invincible_dmc.api.events;

import com.dmc.invincible_dmc.capability.DMCPlayer;
import java.util.function.BiConsumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class BaseEvent {
   public final BaseConsumer consumer;
   public final Side side;

   public BaseEvent(BaseConsumer consumer, Side side) {
      this.consumer = consumer;
      this.side = side;
   }

   public BaseEvent(BaseConsumer event) {
      this(event, Side.BOTH);
   }

   @Deprecated
   public BaseEvent(BiConsumer<PlayerPatch<?>, Entity> biConsumer) {
      this((playerPatch, target, invinciblePlayer) -> biConsumer.accept(playerPatch, target));
   }

   @Deprecated
   public BaseEvent(BiConsumer<PlayerPatch<?>, Entity> biConsumer, Side side) {
      this((playerPatch, target, invinciblePlayer) -> biConsumer.accept(playerPatch, target), side);
   }

   public static BaseEvent createBiCommandEvent(String command, boolean isTarget) {
      BaseConsumer event = (entityPatch, target, invinciblePlayer) -> {
         Level server = ((Player)entityPatch.getOriginal()).m_9236_();
         CommandSourceStack css = ((Player)entityPatch.getOriginal()).m_20203_().m_81325_(2).m_81324_();
         if (isTarget && target instanceof LivingEntity) {
            css = css.m_81329_(target);
         }

         if (server.m_7654_() != null && entityPatch.getOriginal() != null) {
            server.m_7654_().m_129892_().m_230957_(css, command);
         }
      };
      return new BaseEvent(event, Side.SERVER);
   }

   public static BaseEvent create(BaseConsumer event, Side side) {
      return new BaseEvent(event, side);
   }

   @Deprecated
   public static BaseEvent create(BiConsumer<PlayerPatch<?>, Entity> event, Side side) {
      return new BaseEvent(event, side);
   }

   public static BaseEvent createServerEvent(BaseConsumer event) {
      return new BaseEvent(event, Side.SERVER);
   }

   @Deprecated
   public static BaseEvent createServerEvent(BiConsumer<PlayerPatch<?>, Entity> event) {
      return new BaseEvent(event, Side.SERVER);
   }

   public static BaseEvent createClientEvent(BaseConsumer event) {
      return new BaseEvent(event, Side.LOCAL_CLIENT);
   }

   @Deprecated
   public static BaseEvent createClientEvent(BiConsumer<PlayerPatch<?>, Entity> event) {
      return new BaseEvent(event, Side.LOCAL_CLIENT);
   }

   public static BaseEvent create(BaseConsumer event) {
      return new BaseEvent(event, Side.BOTH);
   }

   @Deprecated
   public static BaseEvent create(BiConsumer<PlayerPatch<?>, Entity> event) {
      return new BaseEvent(event, Side.BOTH);
   }

   public void testAndExecute(PlayerPatch<?> entityPatch, Entity target, DMCPlayer DMCPlayer) {
      if (this.side.test(entityPatch.getOriginal())) {
         this.consumer.accept(entityPatch, target, DMCPlayer);
      }
   }
}
