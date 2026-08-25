package com.dmc.invincible_dmc.mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({LivingEntity.class})
public interface LivingEntityAccessor {
   @Accessor("noJumpDelay")
   void setNoJumpDelay(int var1);

   @Invoker("jumpFromGround")
   void callJumpFromGround();

   @Invoker("getJumpPower")
   float callGetJumpPower();
}
