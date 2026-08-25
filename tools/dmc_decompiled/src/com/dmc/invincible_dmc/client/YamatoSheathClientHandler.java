package com.dmc.invincible_dmc.client;

import com.dmc.invincible_dmc.api.forgeevent.YamatoSheathEvent;
import com.dmc.invincible_dmc.client.effeks.FlashSmallEffek;
import com.dmc.invincible_dmc.network.DMCNetwork;
import com.dmc.invincible_dmc.network.client.CPYamatoSheath;
import com.merlin204.avalon.util.AvalonAnimationUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;

@EventBusSubscriber(
   modid = "invincible_dmc",
   value = {Dist.CLIENT}
)
public final class YamatoSheathClientHandler {
   private YamatoSheathClientHandler() {
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public static void onClientSheath(YamatoSheathEvent.Client event) {
      if (event.getResult() != Result.DENY) {
         Vec3 worldPos = AvalonAnimationUtils.getJointWorldPos(
            event.getEntityPatch(), ((HumanoidArmature)Armatures.BIPED.get()).toolL, Vec3f.fromDoubleVector(Vec3.f_82478_), event.getSheathTime()
         );
         FlashSmallEffek.playFlashSmall(
            FlashSmallEffek.Type.LEVEL1,
            ((LivingEntity)event.getEntityPatch().getOriginal()).m_9236_(),
            worldPos.m_7096_(),
            worldPos.m_7098_(),
            worldPos.m_7094_(),
            0.35F
         );
         if (event.getEntityPatch() instanceof LocalPlayerPatch) {
            DMCNetwork.sendToServer(new CPYamatoSheath(event.getAnimation().registryName()));
         }
      }
   }
}
