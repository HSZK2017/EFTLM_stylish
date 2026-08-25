package com.dmc.invincible_dmc.api.events;

import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public enum Side {
   CLIENT(entity -> entity.m_9236_().f_46443_),
   SERVER(entity -> !entity.m_9236_().f_46443_),
   BOTH(entity -> true),
   LOCAL_CLIENT(entity -> entity instanceof Player player ? player.m_7578_() : false);

   public final Predicate<Entity> predicate;

   private Side(Predicate<Entity> predicate) {
      this.predicate = predicate;
   }

   public boolean test(Entity entity) {
      return this.predicate.test(entity);
   }
}
