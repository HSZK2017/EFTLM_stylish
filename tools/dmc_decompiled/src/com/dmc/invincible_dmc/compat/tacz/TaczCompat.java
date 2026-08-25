package com.dmc.invincible_dmc.compat.tacz;

import com.dmc.invincible_dmc.compat.ICompatModule;
import com.dmc.invincible_dmc.skill.weapon_combo.Yamato;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.DMCLog;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent.Pre;
import com.tacz.guns.api.event.server.AmmoHitBlockEvent;
import com.tacz.guns.entity.EntityKineticBullet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public final class TaczCompat implements ICompatModule {
   private final Map<UUID, TaczCompat.ReflectedBulletState> reflectedBullets = new HashMap<>();

   @Override
   public void onLoad(FMLJavaModLoadingContext context) {
      IEventBus forgeBus = MinecraftForge.EVENT_BUS;
      forgeBus.addListener(EventPriority.HIGHEST, false, Pre.class, this::onGunDamagePre);
      forgeBus.addListener(EventPriority.HIGHEST, false, AmmoHitBlockEvent.class, this::onAmmoHitBlock);
      forgeBus.addListener(this::onServerTick);
   }

   public void onGunDamagePre(Pre event) {
      if (event.getBullet() instanceof EntityKineticBullet bullet) {
         long var13 = bullet.m_9236_().m_46467_();
         TaczCompat.ReflectedBulletState reflectedState = this.reflectedBullets.get(bullet.m_20148_());
         if (reflectedState != null && reflectedState.reflectionTick() == var13) {
            event.setCanceled(true);
         } else if (event.getHurtEntity() instanceof ServerPlayer player) {
            ServerPlayerPatch playerPatch = (ServerPlayerPatch)EpicFightCapabilities.getEntityPatch(player, ServerPlayerPatch.class);
            if (playerPatch != null) {
               SkillContainer container = playerPatch.getSkill(Yamato.YAMATO);
               LivingEntity attacker = event.getAttacker();
               Vec3 attackSourcePosition = attacker != null
                  ? attacker.m_146892_()
                  : (bullet.m_19749_() != null ? bullet.m_19749_().m_146892_() : bullet.m_20182_());
               if (container != null && container.getSkill() instanceof VergilSkill vergilSkill) {
                  if (vergilSkill.tryReflectProjectile(container, bullet, attackSourcePosition)) {
                     event.setCanceled(true);
                     Integer originalPierce = TaczProjectileCompat.beginReflection(bullet);
                     if (originalPierce == null) {
                        DMCLog.warn(
                           DMCLog.Category.COMPAT,
                           "[TaczCompat] Reflected TACZ bullet damage was canceled, but bullet pierce retention failed: {}",
                           bullet.m_20148_()
                        );
                        return;
                     }

                     this.reflectedBullets.put(bullet.m_20148_(), new TaczCompat.ReflectedBulletState(bullet, var13, originalPierce));
                  }
               }
            }
         }
      }
   }

   public void onAmmoHitBlock(AmmoHitBlockEvent event) {
      EntityKineticBullet bullet = event.getAmmo();
      TaczCompat.ReflectedBulletState reflectedState = this.reflectedBullets.get(bullet.m_20148_());
      if (reflectedState != null && reflectedState.reflectionTick() == event.getLevel().m_46467_()) {
         event.setCanceled(true);
      }
   }

   public void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END && !this.reflectedBullets.isEmpty()) {
         this.reflectedBullets.values().forEach(state -> TaczProjectileCompat.finishReflection(state.bullet(), state.originalPierce()));
         this.reflectedBullets.clear();
      }
   }

   private static record ReflectedBulletState(EntityKineticBullet bullet, long reflectionTick, int originalPierce) {
   }
}
