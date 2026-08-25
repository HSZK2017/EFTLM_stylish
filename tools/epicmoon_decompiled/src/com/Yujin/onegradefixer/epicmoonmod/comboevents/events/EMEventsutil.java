package com.Yujin.onegradefixer.epicmoonmod.comboevents.events;

import com.Yujin.onegradefixer.epicmoonmod.item.EpicmoonItems;
import com.Yujin.onegradefixer.epicmoonmod.particle.EMparticles;
import com.merlin204.avalon.particle.AvalonParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.property.AnimationEvent.InTimeEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.Side;
import yesman.epicfight.api.animation.property.AnimationEvent.SimpleEvent;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class EMEventsutil {
   public static InTimeEvent ModeChange(int startFrame, int mode) {
      float start = (float)startFrame / 60.0F;
      return InTimeEvent.create(start, (entityPatch, self, params) -> {
         Player player = (Player)entityPatch.getOriginal();
         ItemStack stack = player.m_21205_();
         CompoundTag tag = stack.m_41784_();
         tag.m_128405_("weapon_mode", mode);
      }, Side.CLIENT);
   }

   public static InTimeEvent Image(float time) {
      return InTimeEvent.create(
         time,
         (entityPatch, self, params) -> {
            Player player = (Player)entityPatch.getOriginal();
            Level level = player.m_9236_();
            level.m_7106_(
               (ParticleOptions)EMparticles.IMAGE.get(),
               player.m_20185_(),
               player.m_20186_(),
               player.m_20189_(),
               Double.longBitsToDouble((long)player.m_19879_()),
               5.0,
               0.0
            );
         },
         Side.CLIENT
      );
   }

   public static InTimeEvent Image2(float time) {
      return InTimeEvent.create(
         time,
         (entityPatch, self, params) -> {
            Player player = (Player)entityPatch.getOriginal();
            Level level = player.m_9236_();
            level.m_7106_(
               (ParticleOptions)AvalonParticles.AVALON_INTERPOLATION_ENTITY_AFTER_IMAGE.get(),
               player.m_20185_(),
               player.m_20186_(),
               player.m_20189_(),
               Double.longBitsToDouble((long)player.m_19879_()),
               5.0,
               0.0
            );
         },
         Side.CLIENT
      );
   }

   public static SimpleEvent EndChange(int mode) {
      return SimpleEvent.create((entityPatch, self, params) -> {
         boolean interrupted = Boolean.TRUE.equals(params.first());
         if (!interrupted) {
            if (entityPatch.getOriginal() instanceof Player player) {
               ItemStack stack = player.m_21205_();
               if (!stack.m_41619_()) {
                  CompoundTag tag = stack.m_41784_();
                  tag.m_128379_("pending_mode_change", true);
                  tag.m_128405_("pending_weapon_mode", mode);
               }
            }
         }
      }, Side.CLIENT);
   }

   public static void applyPendingMode(PlayerPatch<?> entityPatch) {
      if (entityPatch.getOriginal() instanceof Player) {
         Player player = (Player)entityPatch.getOriginal();
         ItemStack stack = player.m_21205_();
         if (stack.m_41720_() == EpicmoonItems.VALENCINA_DUAL_SWORDS.get()) {
            CompoundTag tag = stack.m_41784_();
            if (tag.m_128471_("pending_mode_change")) {
               LivingMotion motion = entityPatch.getCurrentLivingMotion();
               if (motion == LivingMotions.IDLE || motion == LivingMotions.WALK || motion == LivingMotions.RUN) {
                  tag.m_128405_("weapon_mode", tag.m_128451_("pending_weapon_mode"));
                  tag.m_128473_("pending_mode_change");
                  tag.m_128473_("pending_weapon_mode");
               }
            }
         }
      }
   }
}
