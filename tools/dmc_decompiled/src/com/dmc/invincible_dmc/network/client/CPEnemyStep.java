package com.dmc.invincible_dmc.network.client;

import com.dmc.invincible_dmc.api.animation.types.customStun.ICustomStunDamageSource;
import com.dmc.invincible_dmc.gameassets.DMCEffects;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.gameassets.animations.stun.CustomStunAnimations;
import com.dmc.invincible_dmc.skill.dodge.VergilDodgeSkill;
import com.dmc.invincible_dmc.skill.weapon_combo.Yamato;
import com.dmc.invincible_dmc.utils.DamageFilterUtils;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent.Context;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.StunType;

public class CPEnemyStep {
   private final int targetId;

   public CPEnemyStep(int targetId) {
      this.targetId = targetId;
   }

   public static CPEnemyStep fromBytes(FriendlyByteBuf buf) {
      return new CPEnemyStep(buf.m_130242_());
   }

   public static void toBytes(CPEnemyStep msg, FriendlyByteBuf buf) {
      buf.m_130130_(msg.targetId);
   }

   public static void handle(CPEnemyStep msg, Supplier<Context> ctx) {
      ctx.get()
         .enqueueWork(
            () -> EpicFightCapabilities.getUnparameterizedEntityPatch(ctx.get().getSender(), ServerPlayerPatch.class)
                  .ifPresent(
                     playerPatch -> {
                        ServerPlayer player = (ServerPlayer)playerPatch.getOriginal();
                        SkillContainer innateContainer = playerPatch.getSkill(SkillSlots.WEAPON_INNATE);
                        if (innateContainer.getSkill() == Yamato.YAMATO && !player.m_20096_()) {
                           if (player.m_9236_().m_6815_(msg.targetId) instanceof LivingEntity livingTarget
                              && livingTarget.m_6084_()
                              && !(player.m_20280_(livingTarget) > 36.0)
                              && !DamageFilterUtils.shouldSkipTarget(player, livingTarget)) {
                              player.m_21195_((MobEffect)DMCEffects.VERTICALSTOP.get());
                              player.m_7292_(new MobEffectInstance((MobEffect)DMCEffects.ENEMY_STEP.get(), 15, 0, false, false));
                              if (innateContainer.getDataManager().hasData((SkillDataKey)DMCSkillDataKeys.AERIAL_ATTACK_COUNT.get())) {
                                 innateContainer.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.AERIAL_ATTACK_COUNT.get(), 0);
                              }

                              if (innateContainer.getDataManager().hasData((SkillDataKey)DMCSkillDataKeys.AIR_TIME_TICKS.get())) {
                                 innateContainer.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.AIR_TIME_TICKS.get(), 0);
                              }

                              SkillContainer dodgeContainer = playerPatch.getSkill(SkillSlots.DODGE);
                              if (dodgeContainer.getSkill() instanceof VergilDodgeSkill
                                 && dodgeContainer.getDataManager().hasData((SkillDataKey)DMCSkillDataKeys.UP_DODGE_COUNT.get())) {
                                 dodgeContainer.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.UP_DODGE_COUNT.get(), 0);
                              }

                              livingTarget.m_20334_(0.0, 0.8, 0.0);
                              EpicFightDamageSource damageSource = EpicFightDamageSources.playerAttack(player).setStunType(StunType.HOLD);
                              ((ICustomStunDamageSource)damageSource)
                                 .invincible$setCustomStunAnimations(
                                    CustomStunAnimations.HIT_FROM_LEFT,
                                    CustomStunAnimations.HIT_FROM_RIGHT,
                                    CustomStunAnimations.HIT_FROM_LEFT_AIR,
                                    CustomStunAnimations.HIT_FROM_RIGHT_AIR
                                 );
                              livingTarget.m_6469_(damageSource, 0.1F);
                              return;
                           }
                        }
                     }
                  )
         );
      ctx.get().setPacketHandled(true);
   }
}
