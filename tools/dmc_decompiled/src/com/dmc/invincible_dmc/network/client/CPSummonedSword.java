package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.entity.summonedsword.BlisteringBladesEntity;
import com.dmc.invincible_dmc.entity.summonedsword.HeavyRainBladesEntity;
import com.dmc.invincible_dmc.entity.summonedsword.SpiralBladesEntity;
import com.dmc.invincible_dmc.entity.summonedsword.StormBladesEntity;
import com.dmc.invincible_dmc.entity.summonedsword.SummonedSwordSpawner;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.server.SPDirectionConsumed;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.DMCLog;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent.Context;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class CPSummonedSword {
   private static final int COST_STORM = 1;
   private static final int COST_SPIRAL = 1;
   private static final int COST_BLISTERING = 1;
   private static final int COST_HEAVY_RAIN = 2;
   private final CPSummonedSword.SwordType type;

   public CPSummonedSword(CPSummonedSword.SwordType type) {
      this.type = type;
   }

   public static void toBytes(CPSummonedSword msg, FriendlyByteBuf buf) {
      buf.m_130068_(msg.type);
   }

   public static CPSummonedSword fromBytes(FriendlyByteBuf buf) {
      return new CPSummonedSword((CPSummonedSword.SwordType)buf.m_130066_(CPSummonedSword.SwordType.class));
   }

   private static boolean consumeStack(SkillContainer container, VergilSkill skill, int cost) {
      if (cost <= 0) {
         return true;
      } else {
         int stack = container.getStack();
         if (stack < cost) {
            return false;
         } else {
            skill.setStackSynchronize(container, stack - cost);
            return true;
         }
      }
   }

   public static void handle(CPSummonedSword msg, Supplier<Context> ctx) {
      ctx.get().enqueueWork(() -> {
         ServerPlayer sender = ctx.get().getSender();
         if (sender != null) {
            ServerPlayerPatch serverPlayerPatch = EpicFightCapabilities.getServerPlayerPatch(sender);
            if (serverPlayerPatch != null) {
               SkillContainer container = serverPlayerPatch.getSkill(SkillSlots.WEAPON_INNATE);
               if (!VergilSkill.NotHoldingYamato(sender)) {
                  if (container.getSkill() instanceof VergilSkill vergilSkill) {
                     boolean var8 = sender.m_7500_();
                     switch (msg.type) {
                        case NORMAL:
                           SummonedSwordSpawner.summonNormal(serverPlayerPatch, false);
                           serverPlayerPatch.playSound((SoundEvent)DMCSounds.SUMMONED_SWORD.get(), 1.0F, 1.0F, 1.0F);
                           break;
                        case TRICK:
                           SummonedSwordSpawner.summonNormal(serverPlayerPatch, true);
                           serverPlayerPatch.playSound((SoundEvent)DMCSounds.SUMMONED_SWORD.get(), 1.0F, 1.0F, 1.0F);
                           break;
                        case STORM_BLADES:
                           if (!consumeStack(container, vergilSkill, 1) && !var8) {
                              return;
                           }

                           LivingEntity target = serverPlayerPatch.getTarget();
                           if (target != null && target.m_6084_()) {
                              SummonedSwordSpawner.storm(sender.m_284548_(), sender, serverPlayerPatch.getTarget());
                              serverPlayerPatch.playSound((SoundEvent)DMCSounds.SUMMONED_SWORD_ARRAY.get(), 1.0F, 1.0F, 1.0F);
                           }
                           break;
                        case TRIGGER_STORM_BLADES:
                           StormBladesEntity.triggerLaunch(sender);
                           break;
                        case SPIRAL_BLADES:
                           SpiralBladesEntity existing = SpiralBladesEntity.getExisting(sender);
                           if (existing != null) {
                              if (existing.getCurrentState() == SpiralBladesEntity.State.STANDBY) {
                                 existing.stopRotation();
                                 DMCLog.info(DMCLog.Category.SWORD, "[SummonedSword] SPIRAL_BLADES re-trigger → stop rotation");
                              }

                              return;
                           }

                           if (!consumeStack(container, vergilSkill, 1) && !var8) {
                              return;
                           }

                           SummonedSwordSpawner.spiral(sender.m_284548_(), sender);
                           serverPlayerPatch.playSound((SoundEvent)DMCSounds.SUMMONED_SWORD_SPIRAL.get(), 1.0F, 1.0F, 1.0F);
                           break;
                        case TRIGGER_SPIRAL_BLADES:
                           SpiralBladesEntity.triggerLaunch(sender);
                           break;
                        case BLISTERING_BLADES:
                           if (!consumeStack(container, vergilSkill, 1) && !var8) {
                              return;
                           }

                           SummonedSwordSpawner.blistering(sender.m_9236_(), sender, 8, 999999, 2, 4);
                           serverPlayerPatch.playSound((SoundEvent)DMCSounds.SUMMONED_SWORD_BLISTER.get(), 1.35F, 1.0F, 1.0F);
                           break;
                        case TRIGGER_BLISTERING_BLADES:
                           BlisteringBladesEntity.triggerLaunch(sender);
                           break;
                        case HEAVY_RAIN:
                           if (!consumeStack(container, vergilSkill, 2) && !var8) {
                              return;
                           }

                           SummonedSwordSpawner.heavyRain(sender.m_9236_(), sender, serverPlayerPatch.getTarget(), Integer.MAX_VALUE, 1, 4, null);
                           serverPlayerPatch.playSound((SoundEvent)DMCSounds.SUMMONED_SWORD_HEAVY_RAIN.get(), 1.0F, 1.0F, 1.0F);
                           DMCNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sender), new SPDirectionConsumed());
                           break;
                        case TRIGGER_HEAVY_RAIN:
                           HeavyRainBladesEntity.triggerLaunch(sender);
                           serverPlayerPatch.playSound(SoundEvents.f_12520_, 1.0F, 1.0F, 1.0F);
                     }

                     DMCLog.info(DMCLog.Category.SWORD, "[SummonedSword] Spawned {} for player {}", msg.type, sender.m_7755_().getString());
                  }
               }
            }
         }
      });
      ctx.get().setPacketHandled(true);
   }

   @Nullable
   public static LivingEntity resolveTarget(ServerPlayerPatch spp) {
      LivingEntity target = spp.getTarget();
      if (target != null && target.m_6084_()) {
         return target;
      } else {
         Player player = (Player)spp.getOriginal();
         LivingEntity lastHurt = player.m_21214_();
         return lastHurt != null && lastHurt.m_6084_() && lastHurt.m_20270_(player) <= 48.0F ? lastHurt : null;
      }
   }

   public static enum SwordType {
      NORMAL,
      TRICK,
      STORM_BLADES,
      TRIGGER_STORM_BLADES,
      SPIRAL_BLADES,
      TRIGGER_SPIRAL_BLADES,
      BLISTERING_BLADES,
      TRIGGER_BLISTERING_BLADES,
      HEAVY_RAIN,
      TRIGGER_HEAVY_RAIN;
   }
}
