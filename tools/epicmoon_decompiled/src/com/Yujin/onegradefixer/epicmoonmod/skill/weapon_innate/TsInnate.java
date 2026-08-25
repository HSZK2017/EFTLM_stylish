package com.Yujin.onegradefixer.epicmoonmod.skill.weapon_innate;

import com.Yujin.onegradefixer.epicmoonmod.skill.EMSkillDataKeys;
import com.Yujin.onegradefixer.epicmoonmod.util.skillparameter;
import com.google.common.collect.Lists;
import com.p1nero.invincible.client.InvincibleKeyMappings;
import com.p1nero.invincible.skill.ComboBasicAttack;
import com.p1nero.invincible.skill.ComboBasicAttack.Builder;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class TsInnate extends ComboBasicAttack {
   private static final UUID PRE_ARMOR_DAMAGE_EVENT_UUID = UUID.fromString("4c303ab6-4d74-4d62-8520-3cb59d0e5511");

   public TsInnate(Builder builder) {
      super(builder);
   }

   public void setParams(CompoundTag parameters) {
      super.setParams(parameters);
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

   public float getAura(SkillContainer container) {
      return (Float)container.getDataManager().getDataValue((SkillDataKey)EMSkillDataKeys.TS_AURA.get());
   }

   public void setAura(SkillContainer container, float aura) {
      container.getDataManager().setDataSync((SkillDataKey)EMSkillDataKeys.TS_AURA.get(), Math.max(0.0F, aura));
   }

   public void addAura(SkillContainer container, float amount) {
      float currentAura = this.getAura(container);
      this.setAura(container, currentAura + amount);
   }

   public boolean consumeAura(SkillContainer container, float amount) {
      float currentAura = this.getAura(container);
      if (currentAura < amount) {
         return false;
      } else {
         this.setAura(container, currentAura - amount);
         return true;
      }
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      List<Component> list = Lists.newArrayList();
      list.add(Component.m_237115_("skill.epicmoonmod.ts.tooltip").m_130940_(ChatFormatting.DARK_RED).m_130940_(ChatFormatting.BOLD));
      list.add(Component.m_237115_("skill.epicmoonmod.ts.tooltip1").m_130940_(ChatFormatting.GRAY).m_7220_(InvincibleKeyMappings.getTranslatableKey3()));
      list.add(Component.m_237115_("skill.epicmoonmod.ts.tooltip3").m_130940_(ChatFormatting.GRAY).m_7220_(InvincibleKeyMappings.getTranslatableKey4()));
      list.add(Component.m_237115_("skill.epicmoonmod.ts.tooltip8").m_130940_(ChatFormatting.DARK_RED));
      list.add(Component.m_237115_("skill.epicmoonmod.ts.tooltip2"));
      list.add(Component.m_237115_("skill.epicmoonmod.ts.tooltip4").m_130940_(ChatFormatting.DARK_GRAY).m_130940_(ChatFormatting.ITALIC));
      list.add(Component.m_237115_("skill.epicmoonmod.ts.tooltip5").m_130940_(ChatFormatting.DARK_RED));
      list.add(Component.m_237115_("skill.epicmoonmod.ts.tooltip6"));
      list.add(Component.m_237115_("skill.epicmoonmod.ts.tooltip7").m_130940_(ChatFormatting.DARK_GRAY).m_130940_(ChatFormatting.ITALIC));
      return list;
   }
}
