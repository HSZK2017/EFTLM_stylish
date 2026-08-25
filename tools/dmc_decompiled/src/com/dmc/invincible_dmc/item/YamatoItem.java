package com.dmc.invincible_dmc.item;

import com.dmc.invincible_dmc.client.DMCKeyMappings;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.gameassets.animations.yamato.YamatoAnimations;
import com.dmc.invincible_dmc.item.enchantment.DMCEnchantments;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.item.WeaponItem;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.FORGE
)
public class YamatoItem extends WeaponItem {
   public YamatoItem(Tier tier, int attackDamage, float attackSpeed, Properties properties) {
      super(tier, attackDamage, attackSpeed, properties);
   }

   @SubscribeEvent
   public static void onLivingKnockBack(LivingKnockBackEvent event) {
      if (event.getEntity() instanceof Player player && isHoldingYamato(player)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onPlayerJump(LivingJumpEvent event) {
      if (event.getEntity() instanceof Player player && isHoldingYamato(player)) {
         player.m_20256_(player.m_20184_().m_82520_(0.0, 0.2, 0.0));
      }

      if (event.getEntity() instanceof ServerPlayer serverPlayer) {
         DoppelgangerPatch doppelgangerPatch = DoppelgangerPatch.getNearestDoppelganger(serverPlayer);
         if (doppelgangerPatch != null) {
            PlayerPatch<?> ownerPatch = doppelgangerPatch.getOwnerPatch();
            DoppelgangerEntity doppelganger = (DoppelgangerEntity)doppelgangerPatch.getOriginal();
            if (ownerPatch != null
               && !doppelganger.isInSpawnCooldown()
               && !doppelgangerPatch.getEntityState().inaction()
               && !ownerPatch.getEntityState().inaction()) {
               doppelganger.jumpYOffset = serverPlayer.m_20186_() - doppelganger.m_20186_();
               doppelgangerPatch.playAnimationSynchronized(YamatoAnimations.YAMATO_JUMP, 0.0F);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onLivingFall(LivingFallEvent event) {
      if (event.getEntity() instanceof Player player && isHoldingYamato(player)) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onLivingUpdate(LivingTickEvent event) {
      LivingEntity entity = event.getEntity();
      if (entity instanceof Player player) {
         if (isHoldingYamato(player) && entity.m_21023_(MobEffects.f_19620_)) {
            entity.m_21195_(MobEffects.f_19620_);
         }

         if (player.m_20094_() > 0) {
            int fireTicks = player.m_20094_();
            if (fireTicks > 20) {
               player.m_7311_(fireTicks - 1);
               if (player.m_6060_()) {
                  player.m_20095_();
               }
            }
         }
      }
   }

   private static boolean isHoldingYamato(Player player) {
      return player.m_21205_().m_41720_() instanceof YamatoItem || player.m_21206_().m_41720_() instanceof YamatoItem;
   }

   @NotNull
   public Multimap<Attribute, AttributeModifier> m_7167_(EquipmentSlot slot) {
      Multimap<Attribute, AttributeModifier> mods = HashMultimap.create(super.m_7167_(slot));
      if (slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND) {
         mods.put(
            Attributes.f_22278_,
            new AttributeModifier(UUID.fromString("d1d124cc-f12f-11ed-a05b-1242ac114515"), "Yamato knockback resist", 1.0, Operation.ADDITION)
         );
      }

      return mods;
   }

   public boolean isDamageable(ItemStack stack) {
      return false;
   }

   public boolean m_8120_(@NotNull ItemStack stack) {
      return true;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return enchantment == DMCEnchantments.SUPER_YAMATO.get() || enchantment.f_44672_ == EnchantmentCategory.WEAPON;
   }

   public int m_6473_() {
      return 100;
   }

   public void m_7373_(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
      tooltip.add(
         Component.m_237115_("item.invincible_dmc.yamato.description1")
            .m_130940_(ChatFormatting.BOLD)
            .m_130940_(ChatFormatting.AQUA)
            .m_130940_(ChatFormatting.UNDERLINE)
      );
      if (DMCKeyMappings.KEY1.getKey().m_84873_() != 0) {
         tooltip.add(Component.m_237115_("tooltip.invincible_dmc.keybind_warning"));
      }
   }
}
