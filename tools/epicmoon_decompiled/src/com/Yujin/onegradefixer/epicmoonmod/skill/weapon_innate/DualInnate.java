package com.Yujin.onegradefixer.epicmoonmod.skill.weapon_innate;

import com.Yujin.onegradefixer.epicmoonmod.effect.EMeffects;
import com.Yujin.onegradefixer.epicmoonmod.item.EpicmoonItems;
import com.Yujin.onegradefixer.epicmoonmod.skill.EMSkillDataKeys;
import com.Yujin.onegradefixer.epicmoonmod.util.skillparameter;
import com.google.common.collect.Lists;
import com.p1nero.invincible.client.InvincibleKeyMappings;
import com.p1nero.invincible.skill.ComboBasicAttack;
import com.p1nero.invincible.skill.ComboBasicAttack.Builder;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.DodgeSuccessEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.TakeDamageEvent.Attack;

public class DualInnate extends ComboBasicAttack {
   private static final int MAX_EYE = 30;
   private static final int EYE_RESTORE_TICKS = 60;
   private static final UUID PRE_ARMOR_DAMAGE_EVENT_UUID = UUID.fromString("31ce825e-4c65-42cc-9c48-3f025904af1e");

   public DualInnate(Builder builder) {
      super(builder);
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (container.getExecutor() instanceof ServerPlayerPatch playerPatch) {
         Player player = (Player)playerPatch.getOriginal();
         if (player.m_21205_().m_41720_() == EpicmoonItems.VALENCINA_DUAL_SWORDS.get()) {
            int eye = this.getEye(container);
            int restoreTime = this.getRestoreTime(container);
            if (eye < 30) {
               if (++restoreTime >= 60) {
                  this.setEye(container, eye + 1);
                  restoreTime = 0;
               }

               this.setRestoreTime(container, restoreTime);
            } else if (restoreTime != 0) {
               this.setRestoreTime(container, 0);
            }
         }
      }
   }

   protected void onTakeDamageEventAttack(Attack event, SkillContainer container) {
      if (container.getExecutor() instanceof ServerPlayerPatch playerPatch) {
         int eye = this.getEye(container);
         if (eye > 1 && event.isParried()) {
            this.applyAcceleratingFuture((Player)playerPatch.getOriginal());
            this.setEye(container, eye - 1);
         }
      }

      super.onTakeDamageEventAttack(event, container);
   }

   protected void onDodgeSuccess(DodgeSuccessEvent event, SkillContainer container) {
      if (container.getExecutor() instanceof ServerPlayerPatch playerPatch) {
         int eye = this.getEye(container);
         if (eye > 1) {
            this.applyAcceleratingFuture((Player)playerPatch.getOriginal());
            this.setEye(container, eye - 1);
         }
      }

      super.onDodgeSuccess(event, container);
   }

   private void applyAcceleratingFuture(Player player) {
      MobEffectInstance currentEffect = player.m_21124_((MobEffect)EMeffects.ACCELERATING_FUTURE.get());
      if (currentEffect == null) {
         player.m_7292_(new MobEffectInstance((MobEffect)EMeffects.ACCELERATING_FUTURE.get(), 80, 0));
      } else {
         int newDuration = currentEffect.m_19557_() + 40;
         int newAmplifier = Math.min(currentEffect.m_19564_() + 1, 4);
         player.m_7292_(
            new MobEffectInstance(
               (MobEffect)EMeffects.ACCELERATING_FUTURE.get(),
               newDuration,
               newAmplifier,
               currentEffect.m_19571_(),
               currentEffect.m_19572_(),
               currentEffect.m_19575_()
            )
         );
      }
   }

   public int getEye(SkillContainer container) {
      return (Integer)container.getDataManager().getDataValue((SkillDataKey)EMSkillDataKeys.DUAL_EYE.get());
   }

   public void setEye(SkillContainer container, int eye) {
      container.getDataManager().setDataSync((SkillDataKey)EMSkillDataKeys.DUAL_EYE.get(), Mth.m_14045_(eye, 0, 30));
   }

   public int getSin(SkillContainer container) {
      return (Integer)container.getDataManager().getDataValue((SkillDataKey)EMSkillDataKeys.DUAL_SIN.get());
   }

   public void setSin(SkillContainer container, int sin) {
      container.getDataManager().setDataSync((SkillDataKey)EMSkillDataKeys.DUAL_SIN.get(), sin);
   }

   public void addSin(SkillContainer container, int amount) {
      this.setSin(container, this.getSin(container) + amount);
   }

   public boolean consumeSin(SkillContainer container, int amount) {
      int current = this.getSin(container);
      if (current < amount) {
         return false;
      } else {
         this.setSin(container, current - amount);
         return true;
      }
   }

   private int getRestoreTime(SkillContainer container) {
      return (Integer)container.getDataManager().getDataValue((SkillDataKey)EMSkillDataKeys.DUAL_RESTORE_TIME.get());
   }

   private void setRestoreTime(SkillContainer container, int time) {
      container.getDataManager().setData((SkillDataKey)EMSkillDataKeys.DUAL_RESTORE_TIME.get(), Math.max(0, time));
   }

   public void onInitiate(SkillContainer container) {
      super.onInitiate(container);
      container.getExecutor().getEventListener().addEventListener(EventType.DEAL_DAMAGE_EVENT_ATTACK, PRE_ARMOR_DAMAGE_EVENT_UUID, event -> {
         ServerPlayerPatch playerPatch = (ServerPlayerPatch)event.getPlayerPatch();
         skillparameter.set(((ServerPlayer)playerPatch.getOriginal()).m_20148_(), event.getAttackDamage());
      });
   }

   public void onRemoved(SkillContainer container) {
      container.getExecutor().getEventListener().removeListener(EventType.DEAL_DAMAGE_EVENT_ATTACK, PRE_ARMOR_DAMAGE_EVENT_UUID);
      skillparameter.clear(((Player)container.getExecutor().getOriginal()).m_20148_());
      super.onRemoved(container);
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      List<Component> list = Lists.newArrayList();
      list.add(Component.m_237115_("skill.epicmoonmod.dual.tooltip11").m_130940_(ChatFormatting.DARK_RED).m_130940_(ChatFormatting.BOLD));
      list.add(Component.m_237115_("skill.epicmoonmod.dual.tooltip1").m_130940_(ChatFormatting.GRAY).m_7220_(InvincibleKeyMappings.getTranslatableKey3()));
      list.add(Component.m_237115_("skill.epicmoonmod.dual.tooltip3").m_130940_(ChatFormatting.GRAY).m_7220_(InvincibleKeyMappings.getTranslatableKey4()));
      list.add(Component.m_237115_("skill.epicmoonmod.dual.tooltip").m_130940_(ChatFormatting.DARK_RED));
      list.add(Component.m_237115_("skill.epicmoonmod.dual.tooltip2"));
      list.add(Component.m_237115_("skill.epicmoonmod.dual.tooltip21"));
      list.add(Component.m_237115_("skill.epicmoonmod.dual.tooltip22"));
      list.add(Component.m_237115_("skill.epicmoonmod.dual.tooltip4").m_130940_(ChatFormatting.DARK_GRAY).m_130940_(ChatFormatting.ITALIC));
      list.add(Component.m_237115_("skill.epicmoonmod.dual.tooltip5").m_130940_(ChatFormatting.DARK_RED));
      list.add(Component.m_237115_("skill.epicmoonmod.dual.tooltip6"));
      list.add(Component.m_237115_("skill.epicmoonmod.dual.tooltip9").m_130940_(ChatFormatting.DARK_GRAY).m_130940_(ChatFormatting.ITALIC));
      list.add(Component.m_237115_("skill.epicmoonmod.dual.tooltip91").m_130940_(ChatFormatting.DARK_GRAY).m_130940_(ChatFormatting.ITALIC));
      list.add(Component.m_237115_("skill.epicmoonmod.dual.tooltip92").m_130940_(ChatFormatting.DARK_GRAY).m_130940_(ChatFormatting.ITALIC));
      list.add(Component.m_237115_("skill.epicmoonmod.dual.tooltip93").m_130940_(ChatFormatting.DARK_GRAY).m_130940_(ChatFormatting.ITALIC));
      list.add(Component.m_237115_("skill.epicmoonmod.dual.tooltip7").m_130940_(ChatFormatting.DARK_RED));
      list.add(Component.m_237115_("skill.epicmoonmod.dual.tooltip8"));
      list.add(Component.m_237115_("skill.epicmoonmod.dual.tooltip10").m_130940_(ChatFormatting.DARK_GRAY).m_130940_(ChatFormatting.ITALIC));
      return list;
   }
}
