package com.dmc.invincible_dmc.api.forgeevent;

import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.HasResult;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@HasResult
public abstract class YamatoSheathEvent extends Event {
   private final LivingEntityPatch<?> entityPatch;
   private final float sheathTime;
   private final RegistryObject<SoundEvent> sound;
   private final AnimationAccessor<? extends StaticAnimation> animation;

   protected YamatoSheathEvent(
      LivingEntityPatch<?> entityPatch, float sheathTime, RegistryObject<SoundEvent> sound, AnimationAccessor<? extends StaticAnimation> animation
   ) {
      this.entityPatch = entityPatch;
      this.sheathTime = sheathTime;
      this.sound = sound;
      this.animation = animation;
   }

   public LivingEntityPatch<?> getEntityPatch() {
      return this.entityPatch;
   }

   public float getSheathTime() {
      return this.sheathTime;
   }

   public RegistryObject<SoundEvent> getSound() {
      return this.sound;
   }

   public AnimationAccessor<? extends StaticAnimation> getAnimation() {
      return this.animation;
   }

   @HasResult
   public static final class Client extends YamatoSheathEvent {
      public Client(
         LivingEntityPatch<?> entityPatch, float sheathTime, RegistryObject<SoundEvent> sound, AnimationAccessor<? extends StaticAnimation> animation
      ) {
         super(entityPatch, sheathTime, sound, animation);
      }
   }

   @HasResult
   public static final class Server extends YamatoSheathEvent {
      public Server(
         LivingEntityPatch<?> entityPatch, float sheathTime, RegistryObject<SoundEvent> sound, AnimationAccessor<? extends StaticAnimation> animation
      ) {
         super(entityPatch, sheathTime, sound, animation);
      }
   }
}
