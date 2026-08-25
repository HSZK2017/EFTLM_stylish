package com.pla.annoyingvillagers.util;

import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class TeamUtil {
   public static boolean isInTeam(Entity entity, String teamName) {
      if (entity == null) {
         return false;
      } else if (entity.m_9236_() instanceof ServerLevel serverLevel) {
         Scoreboard scoreboard = serverLevel.m_6188_();
         PlayerTeam team = scoreboard.m_83489_(teamName);
         if (team == null) {
            return false;
         } else {
            String entry = entity.m_6302_();
            PlayerTeam current = scoreboard.m_83500_(entry);
            return current != null && current == team;
         }
      } else {
         return false;
      }
   }

   public static void addOrJoinTeam(Entity entity, String teamName) {
      if (entity != null) {
         if (entity.m_9236_() instanceof ServerLevel serverLevel) {
            Scoreboard scoreboard = serverLevel.m_6188_();
            PlayerTeam team = scoreboard.m_83489_(teamName);
            if (team == null) {
               team = scoreboard.m_83492_(teamName);
            }

            team.m_83355_(false);
            String entry = entity.m_6302_();
            PlayerTeam current = scoreboard.m_83500_(entry);
            if (current != team) {
               if (current != null) {
                  scoreboard.m_6519_(entry, current);
               }

               scoreboard.m_6546_(entry, team);
            }
         }
      }
   }

   public static void leaveTeam(Entity entity, String teamName) {
      if (entity != null) {
         if (entity.m_9236_() instanceof ServerLevel serverLevel) {
            Scoreboard scoreboard = serverLevel.m_6188_();
            PlayerTeam team = scoreboard.m_83489_(teamName);
            if (team != null) {
               String entry = entity.m_6302_();
               PlayerTeam current = scoreboard.m_83500_(entry);
               if (current == team) {
                  scoreboard.m_6519_(entry, team);
               }
            }
         }
      }
   }

   @Nullable
   public static String getTeamName(Entity entity) {
      if (entity == null) {
         return null;
      } else if (entity.m_9236_() instanceof ServerLevel serverLevel) {
         Scoreboard scoreboard = serverLevel.m_6188_();
         PlayerTeam current = scoreboard.m_83500_(entity.m_6302_());
         return current != null ? current.m_5758_() : null;
      } else {
         return null;
      }
   }

   public static void leaveCurrentTeam(Entity entity) {
      String teamName = getTeamName(entity);
      if (teamName != null) {
         leaveTeam(entity, teamName);
      }
   }
}
