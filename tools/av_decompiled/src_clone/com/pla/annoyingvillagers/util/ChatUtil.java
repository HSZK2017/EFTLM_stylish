package com.pla.annoyingvillagers.util;

import com.pla.annoyingvillagers.config.AnnoyingVillagersConfig;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class ChatUtil {
   public static void joinGame(Entity entity) {
      if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_CHAT.get() && entity.m_9236_().m_7654_() != null) {
         Objects.requireNonNull(entity.m_9236_().m_7654_())
            .m_6846_()
            .m_240416_(Component.m_237113_(entity.m_5446_().getString() + " has joined the game").m_130940_(ChatFormatting.YELLOW), false);
      }
   }

   public static void joinGame(Entity entity, String string) {
      if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_CHAT.get() && entity.m_9236_().m_7654_() != null) {
         Objects.requireNonNull(entity.m_9236_().m_7654_())
            .m_6846_()
            .m_240416_(Component.m_237113_(string + " has joined the game").m_130940_(ChatFormatting.YELLOW), false);
      }
   }

   public static void leaveGame(Entity entity) {
      if ((Boolean)AnnoyingVillagersConfig.TURN_ON_NPC_CHAT.get() && entity.m_9236_().m_7654_() != null) {
         Objects.requireNonNull(entity.m_9236_().m_7654_())
            .m_6846_()
            .m_240416_(Component.m_237113_(entity.m_5446_().getString() + " has left the game").m_130940_(ChatFormatting.YELLOW), false);
      }
   }
}
