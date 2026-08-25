package com.pla.annoyingvillagers.skill;

import com.pla.annoyingvillagers.gameasset.AVAnimations;
import com.pla.annoyingvillagers.gameasset.AVSkillDataKeys;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightAwaken;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightBattleArts;
import com.pla.annoyingvillagers.gameasset.AnimsPugilistSteve;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import com.pla.annoyingvillagers.item.BlueDemonTridentItem;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class TridentFestivalSkill extends WeaponInnateSkill {
   private static final UUID EVENT_UUID = UUID.fromString("10cefa54-8fee-4627-a321-64a1a6388a25");

   public boolean isRangedMode(SkillContainer container) {
      return (Boolean)container.getDataManager().getDataValue((SkillDataKey)AVSkillDataKeys.IS_TRIDENT_RANGED_MODE.get());
   }

   public boolean isMeleeMode(SkillContainer container) {
      return !this.isRangedMode(container);
   }

   public void toggleMode(SkillContainer container) {
      SkillDataManager data = container.getDataManager();
      boolean current = (Boolean)data.getDataValue((SkillDataKey)AVSkillDataKeys.IS_TRIDENT_RANGED_MODE.get());
      data.setDataSync((SkillDataKey)AVSkillDataKeys.IS_TRIDENT_RANGED_MODE.get(), !current);
   }

   public TridentFestivalSkill(SkillBuilder<? extends WeaponInnateSkill> builder) {
      super(builder);
   }

   public void executeOnServer(SkillContainer skillContainer, FriendlyByteBuf friendlyByteBuf) {
      if (!this.isActivated(skillContainer)) {
         super.executeOnServer(skillContainer, friendlyByteBuf);
         skillContainer.activate();
         if (this.isRangedMode(skillContainer)) {
            Player player = (Player)skillContainer.getExecutor().getOriginal();
            ItemStack mainHand = player.m_21205_();
            ItemStack offHand = player.m_21206_();
            boolean bothFullyCharged = BlueDemonTridentItem.isBlueDemonTrident(mainHand)
               && BlueDemonTridentItem.isBlueDemonTrident(offHand)
               && BlueDemonTridentItem.isFullyCharged(mainHand)
               && BlueDemonTridentItem.isFullyCharged(offHand);
            if (bothFullyCharged) {
               skillContainer.getExecutor().playAnimationSynchronized(AnimsPugilistSteve.TRIDENT_FESTIVAL, 0.0F);
            } else {
               skillContainer.getExecutor().playAnimationSynchronized(AVAnimations.TRIDENT_ATTACK, 0.0F);
            }
         } else {
            skillContainer.getExecutor().playAnimationSynchronized(AnimsWom.ELECTRIC_FIELD, 0.0F);
         }
      }
   }

   public void cancelOnServer(SkillContainer skillContainer, FriendlyByteBuf friendlyByteBuf) {
      skillContainer.deactivate();
      super.cancelOnServer(skillContainer, friendlyByteBuf);
   }

   public void executeOnClient(SkillContainer container, FriendlyByteBuf args) {
      super.executeOnClient(container, args);
      container.activate();
   }

   public void cancelOnClient(SkillContainer container, FriendlyByteBuf args) {
      super.cancelOnClient(container, args);
      container.deactivate();
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      Player player = (Player)container.getExecutor().getOriginal();
      if (player.m_9236_() instanceof ServerLevel serverLevel && player.f_19797_ % 20 == 0) {
         SkillDataManager data = container.getDataManager();
         data.setDataSync((SkillDataKey)AVSkillDataKeys.TRIDENT_AMOUNT.get(), BlueDemonTridentItem.getAllOwnerTridents(serverLevel, player).size());
      }
   }

   public void onInitiate(SkillContainer container) {
      super.onInitiate(container);
      container.getDataManager().setDataSync((SkillDataKey)AVSkillDataKeys.IS_TRIDENT_RANGED_MODE.get(), false);
      container.getExecutor()
         .getEventListener()
         .addEventListener(
            EventType.BASIC_ATTACK_EVENT,
            EVENT_UUID,
            event -> {
               if (!((ServerPlayerPatch)event.getPlayerPatch()).isLogicalClient()) {
                  SkillContainer skillContainer = ((ServerPlayerPatch)event.getPlayerPatch()).getSkill(this);
                  if (skillContainer != null) {
                     if (this.isRangedMode(skillContainer)) {
                        event.setCanceled(true);
                        PlayerPatch<?> playerPatch = event.getPlayerPatch();
                        AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null))
                           .getRealAnimation();
                        if (dynamicAnimation != null && dynamicAnimation == AnimsEpicFightBattleArts.TRIDENT_THROW_1) {
                           skillContainer.getExecutor().playAnimationSynchronized(AnimsPugilistSteve.TRIDENT_THROW_2, 0.0F);
                        } else if (dynamicAnimation != null && dynamicAnimation == AnimsPugilistSteve.TRIDENT_THROW_2) {
                           skillContainer.getExecutor().playAnimationSynchronized(AnimsEpicFightBattleArts.TRIDENT_THROW_3, 0.0F);
                        } else if (dynamicAnimation != null && dynamicAnimation == AnimsEpicFightBattleArts.TRIDENT_THROW_3) {
                           skillContainer.getExecutor().playAnimationSynchronized(AnimsEpicFightAwaken.DP_THROW_BLADE_AUTO_2, 0.0F);
                        } else if (dynamicAnimation != null && dynamicAnimation == AnimsEpicFightAwaken.DP_THROW_BLADE_AUTO_2) {
                           skillContainer.getExecutor().playAnimationSynchronized(AnimsEpicFightBattleArts.TRIDENT_THROW_5, 0.0F);
                        } else if (((Player)playerPatch.getOriginal()).m_20142_()) {
                           skillContainer.getExecutor().playAnimationSynchronized(AnimsEpicFightAwaken.DP_THROW_BLADE_AUTO_1, 0.0F);
                        } else if (!((Player)playerPatch.getOriginal()).m_20096_() && !((Player)playerPatch.getOriginal()).m_20069_()) {
                           skillContainer.getExecutor().playAnimationSynchronized(AnimsEpicFightAwaken.THROW_HOOK_SLASH_AIR, 0.0F);
                        } else {
                           skillContainer.getExecutor().playAnimationSynchronized(AnimsEpicFightBattleArts.TRIDENT_THROW_1, 0.0F);
                        }
                     }
                  }
               }
            }
         );
      container.getExecutor()
         .getEventListener()
         .addEventListener(
            EventType.TAKE_DAMAGE_EVENT_ATTACK,
            EVENT_UUID,
            pre -> {
               if (!((ServerPlayerPatch)pre.getPlayerPatch()).isLogicalClient()) {
                  PlayerPatch<?> playerPatch = pre.getPlayerPatch();
                  AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null))
                     .getRealAnimation();
                  if (dynamicAnimation != null) {
                     if (dynamicAnimation == AVAnimations.TRIDENT_ATTACK
                        || dynamicAnimation == AnimsWom.ELECTRIC_FIELD
                        || dynamicAnimation == AnimsPugilistSteve.TRIDENT_FESTIVAL) {
                        pre.setCanceled(true);
                        pre.setResult(ResultType.BLOCKED);
                     }

                     if (((Player)playerPatch.getOriginal()).m_20142_() && pre.getDamageSource().m_7640_() instanceof Projectile projectile) {
                        Vec3 entityPosition = projectile.m_20182_();
                        Vec3 entityViewVector = ((ServerPlayer)((ServerPlayerPatch)pre.getPlayerPatch()).getOriginal()).m_20252_(1.0F);
                        Vec3 entitySubtract = entityPosition.m_82546_(((ServerPlayer)((ServerPlayerPatch)pre.getPlayerPatch()).getOriginal()).m_146892_())
                           .m_82541_();
                        if (entitySubtract.m_82526_(entityViewVector) > 0.0) {
                           pre.setCanceled(true);
                           pre.setResult(ResultType.BLOCKED);
                           if (((Player)playerPatch.getOriginal()).m_9236_() instanceof ServerLevel serverLevel) {
                              EpicfightUtil.damageBlocked(pre.getDamageSource(), playerPatch.getOriginal(), serverLevel);
                           }

                           if (new Random().nextBoolean()) {
                              playerPatch.playAnimationSynchronized(AnimsWom.TRIDENT_GUARD_HIT_1, 0.0F);
                           } else {
                              playerPatch.playAnimationSynchronized(AnimsWom.TRIDENT_GUARD_HIT_2, 0.0F);
                           }
                        }
                     }
                  }
               }
            }
         );
   }

   public void onRemoved(SkillContainer container) {
      container.getExecutor().getEventListener().removeListener(EventType.BASIC_ATTACK_EVENT, EVENT_UUID);
      container.getExecutor().getEventListener().removeListener(EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID);
   }

   public ResourceLocation getSkillTexture() {
      return super.getSkillTexture();
   }
}
