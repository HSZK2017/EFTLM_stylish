package com.dmc.invincible_dmc.network.server;

import com.dmc.invincible_dmc.api.weapon.DmcWeaponType;
import com.dmc.invincible_dmc.capability.DMCPlayer;
import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionSession;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionStage;
import com.dmc.invincible_dmc.capability.weapon.WeaponActionType;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerState;
import com.dmc.invincible_dmc.capability.yamato.YamatoPlayerStateProvider;
import com.dmc.invincible_dmc.client.ClientWeaponStateHandler;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent.Context;

public record SPWeaponState(
   int entityId,
   int activeWeaponId,
   int renderedWeaponId,
   int pendingWeaponId,
   int sequence,
   long barrierSessionId,
   long actionSessionId,
   int actionOwnerWeaponId,
   int actionTypeId,
   int actionStageId,
   int actionStageRevision,
   int actionStep,
   int actionNodeId,
   int actionKeyIndex,
   long actionStartedTick
) {
   public static SPWeaponState from(Player player) {
      YamatoPlayerState state = YamatoPlayerStateProvider.get(player);
      DMCPlayer dmcPlayer = DMCPlayerCapabilityProvider.get(player);
      WeaponActionSession action = dmcPlayer.getActionSession();
      return new SPWeaponState(
         player.m_19879_(),
         state.getActiveWeapon().networkId(),
         state.getRenderedWeapon().networkId(),
         state.getPendingRenderedWeapon() != null ? state.getPendingRenderedWeapon().networkId() : -1,
         state.getWeaponSwitchSequence(),
         state.getPendingRenderBarrierSessionId(),
         action != null ? action.sessionId() : 0L,
         action != null ? action.ownerWeapon().networkId() : -1,
         action != null ? action.actionType().ordinal() : -1,
         action != null ? action.stage().ordinal() : -1,
         action != null ? action.stageRevision() : 0,
         action != null ? action.actionStep() : 0,
         action != null ? action.sourceNodeId() : -1,
         action != null ? action.inputKeyIndex() : -1,
         action != null ? action.startedTick() : 0L
      );
   }

   public static void encode(SPWeaponState packet, FriendlyByteBuf buffer) {
      buffer.m_130130_(packet.entityId);
      buffer.m_130130_(packet.activeWeaponId);
      buffer.m_130130_(packet.renderedWeaponId);
      buffer.m_130130_(packet.pendingWeaponId + 1);
      buffer.m_130130_(packet.sequence);
      buffer.m_130103_(packet.barrierSessionId);
      buffer.m_130103_(packet.actionSessionId);
      buffer.m_130130_(packet.actionOwnerWeaponId + 1);
      buffer.m_130130_(packet.actionTypeId + 1);
      buffer.m_130130_(packet.actionStageId + 1);
      buffer.m_130130_(packet.actionStageRevision);
      buffer.m_130130_(packet.actionStep);
      buffer.m_130130_(packet.actionNodeId + 1);
      buffer.m_130130_(packet.actionKeyIndex + 1);
      buffer.m_130103_(packet.actionStartedTick);
   }

   public static SPWeaponState decode(FriendlyByteBuf buffer) {
      return new SPWeaponState(
         buffer.m_130242_(),
         buffer.m_130242_(),
         buffer.m_130242_(),
         buffer.m_130242_() - 1,
         buffer.m_130242_(),
         buffer.m_130258_(),
         buffer.m_130258_(),
         buffer.m_130242_() - 1,
         buffer.m_130242_() - 1,
         buffer.m_130242_() - 1,
         buffer.m_130242_(),
         buffer.m_130242_(),
         buffer.m_130242_() - 1,
         buffer.m_130242_() - 1,
         buffer.m_130258_()
      );
   }

   public static void handle(SPWeaponState packet, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientWeaponStateHandler.handle(packet)));
      context.setPacketHandled(true);
   }

   public WeaponActionSession createActionMirror() {
      WeaponActionType[] types = WeaponActionType.values();
      WeaponActionStage[] stages = WeaponActionStage.values();
      return this.actionSessionId > 0L
            && this.actionOwnerWeaponId >= 0
            && this.actionTypeId >= 0
            && this.actionTypeId < types.length
            && this.actionStageId >= 0
            && this.actionStageId < stages.length
         ? new WeaponActionSession(
            this.actionSessionId,
            DmcWeaponType.byNetworkId(this.actionOwnerWeaponId),
            types[this.actionTypeId],
            this.actionNodeId,
            this.actionKeyIndex,
            this.actionStartedTick,
            stages[this.actionStageId],
            this.actionStageRevision,
            this.actionStep
         )
         : null;
   }
}
