package com.pla.annoyingvillagers.event;

import com.pla.annoyingvillagers.gameasset.AnimsSculkSteve;
import com.pla.annoyingvillagers.gameasset.AnimsWom;
import com.pla.annoyingvillagers.item.BlueDemonChestplateItem;
import com.pla.annoyingvillagers.item.TransporterFragmentItem;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.Objects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class SpecialAttackOnKeyHeldEvent {
   public static void execute(LevelAccessor world, Entity entity) {
      if (entity != null) {
         PlayerPatch<?> playerpatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(entity, PlayerPatch.class);
         LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
         if (livingEntityPatch != null) {
            AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(livingEntityPatch.getAnimator().getPlayerFor(null))
               .getRealAnimation();
            if (!EpicfightUtil.isLongHitAnimation(dynamicAnimation, livingEntityPatch)) {
               if (entity instanceof Player player && !player.m_9236_().m_5776_()) {
                  TransporterFragmentItem.UseResult transporterUseResult = TransporterFragmentItem.tryUseHeldSpecialAttack(player);
                  if (transporterUseResult.consumed()) {
                     if (transporterUseResult.activated()) {
                        livingEntityPatch.playAnimationSynchronized(AnimsSculkSteve.PORTAL_SUMMON, 0.0F);
                     }

                     return;
                  }
               }

               if (!(entity.m_9236_() instanceof ServerLevel) || dynamicAnimation == Animations.EMPTY_ANIMATION) {
                  if (entity instanceof Player playerx
                     && playerx.m_6844_(EquipmentSlot.CHEST).m_41720_() instanceof BlueDemonChestplateItem
                     && BlueDemonChestplateItem.isBlueDemonChestplate(playerx.m_6844_(EquipmentSlot.CHEST))
                     && entity.m_9236_() instanceof ServerLevel) {
                     livingEntityPatch.playAnimationSynchronized(AnimsWom.CUT_ANTITHEUS_ASCENSION, 0.0F);
                     return;
                  }
               }
            }
         }
      }
   }
}
