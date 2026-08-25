package com.dmc.invincible_dmc.client;

import com.dmc.invincible_dmc.client.input.PlayerInputState;
import com.dmc.invincible_dmc.client.particles.SdtPhase2Particle;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.FORGE,
   value = {Dist.CLIENT}
)
public class ClientSdtSoundHandler {
   private static boolean wasCharging;
   private static int prevPhase = 0;
   private static SdtPhase2Particle phase2Particle;

   @SubscribeEvent
   public static void onClientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         Minecraft mc = Minecraft.m_91087_();
         if (mc.f_91074_ != null && !mc.m_91104_()) {
            LocalPlayerPatch lpp = (LocalPlayerPatch)EpicFightCapabilities.getEntityPatch(mc.f_91074_, LocalPlayerPatch.class);
            if (lpp != null) {
               SkillContainer container = lpp.getSkill(SkillSlots.WEAPON_INNATE);
               if (container != null && !container.isEmpty()) {
                  SkillDataManager dm = container.getDataManager();
                  if (dm != null) {
                     int phase = dm.hasData((SkillDataKey)DMCSkillDataKeys.SDT_PHASE.get())
                        ? (Integer)dm.getDataValue((SkillDataKey)DMCSkillDataKeys.SDT_PHASE.get())
                        : 0;
                     boolean keyDown = PlayerInputState.isLocalDown(8);
                     boolean isSdt = dm.hasData((SkillDataKey)DMCSkillDataKeys.IS_SDT.get())
                        && (Boolean)dm.getDataValue((SkillDataKey)DMCSkillDataKeys.IS_SDT.get());
                     boolean isCharging = keyDown && !isSdt && (phase == 1 || phase == 2);
                     if (prevPhase == 1 && phase == 2) {
                        mc.m_91106_().m_120386_(DMCSounds.SDT1_CHARGE.getId(), null);
                     }

                     if (prevPhase == 2 && phase == 3) {
                        mc.m_91106_().m_120386_(DMCSounds.SDT2_CHARGE.getId(), null);
                     }

                     if (!isCharging && wasCharging) {
                        mc.m_91106_().m_120386_(DMCSounds.SDT1_CHARGE.getId(), null);
                        mc.m_91106_().m_120386_(DMCSounds.SDT2_CHARGE.getId(), null);
                     }

                     if (!SinDevilTriggerManager.hasDT(container)) {
                        mc.m_91106_().m_120386_(DMCSounds.SDT1_CHARGE.getId(), null);
                     }

                     boolean inPhase2 = phase == 2;
                     if (inPhase2 && phase2Particle == null && SdtPhase2Particle.SHARED_SPRITE_SET != null) {
                        phase2Particle = new SdtPhase2Particle(mc.f_91073_, mc.f_91074_);
                        mc.f_91061_.m_107344_(phase2Particle);
                     }

                     if (!inPhase2 && phase2Particle != null) {
                        phase2Particle.m_107274_();
                        phase2Particle = null;
                     }

                     wasCharging = isCharging;
                     prevPhase = phase;
                  }
               }
            }
         }
      }
   }
}
