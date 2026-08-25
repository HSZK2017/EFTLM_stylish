package com.dmc.invincible_dmc.api.events;

import com.dmc.invincible_dmc.capability.DMCPlayer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

@FunctionalInterface
public interface BaseConsumer {
   void accept(PlayerPatch<?> var1, @Nullable Entity var2, DMCPlayer var3);
}
