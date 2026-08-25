package com.dmc.invincible_dmc.network;

import com.dmc.invincible_dmc.InvincibleMod_DMC;
import com.dmc.invincible_dmc.network.client.CPComboReset;
import com.dmc.invincible_dmc.network.client.CPCrazyComboReset;
import com.dmc.invincible_dmc.network.client.CPDiscardSpineBlade;
import com.dmc.invincible_dmc.network.client.CPDoppelgangerControl;
import com.dmc.invincible_dmc.network.client.CPDoppelgangerDelayMode;
import com.dmc.invincible_dmc.network.client.CPDoppelgangerTapHold;
import com.dmc.invincible_dmc.network.client.CPEnemyStep;
import com.dmc.invincible_dmc.network.client.CPInstantJudgementCutEndToggle;
import com.dmc.invincible_dmc.network.client.CPJudgementCutStyleSync;
import com.dmc.invincible_dmc.network.client.CPMovementInputPacket;
import com.dmc.invincible_dmc.network.client.CPPlayCC;
import com.dmc.invincible_dmc.network.client.CPPlayJC;
import com.dmc.invincible_dmc.network.client.CPPlayerInputEvent;
import com.dmc.invincible_dmc.network.client.CPPlayerInputSync;
import com.dmc.invincible_dmc.network.client.CPPortalDestinationChosen;
import com.dmc.invincible_dmc.network.client.CPPortalStyleSync;
import com.dmc.invincible_dmc.network.client.CPSummonedSword;
import com.dmc.invincible_dmc.network.client.CPTapHoldTrigger;
import com.dmc.invincible_dmc.network.client.CPWeaponSwitch;
import com.dmc.invincible_dmc.network.client.CPYamatoSheath;
import com.dmc.invincible_dmc.network.server.S2CAnimationElapsedTimePacket;
import com.dmc.invincible_dmc.network.server.S2CCameraShakePacket;
import com.dmc.invincible_dmc.network.server.S2CDoppelgangerSyncPacket;
import com.dmc.invincible_dmc.network.server.S2CMeteorShowerPacket;
import com.dmc.invincible_dmc.network.server.S2CPortalDestinationsPacket;
import com.dmc.invincible_dmc.network.server.S2CSdtEffectPacket;
import com.dmc.invincible_dmc.network.server.SPCrazyComboReset;
import com.dmc.invincible_dmc.network.server.SPDirectionConsumed;
import com.dmc.invincible_dmc.network.server.SPInstantJudgementCutEndState;
import com.dmc.invincible_dmc.network.server.SPWeaponState;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class DMCNetwork {
   private static final String PROTOCOL_VERSION = "2";
   public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(InvincibleMod_DMC.rl("network"), () -> "2", "2"::equals, "2"::equals);

   public static void sendToServer(Object message) {
      INSTANCE.sendToServer(message);
   }

   public static void registerPackets() {
      int id = 0;
      INSTANCE.registerMessage(id++, CPComboReset.class, CPComboReset::toBytes, CPComboReset::fromBytes, CPComboReset::handle);
      INSTANCE.registerMessage(id++, CPPlayJC.class, CPPlayJC::encode, CPPlayJC::decode, CPPlayJC::handle);
      INSTANCE.registerMessage(id++, CPPlayCC.class, CPPlayCC::toBytes, CPPlayCC::fromBytes, CPPlayCC::handle);
      INSTANCE.registerMessage(id++, CPTapHoldTrigger.class, CPTapHoldTrigger::toBytes, CPTapHoldTrigger::fromBytes, CPTapHoldTrigger::handle);
      INSTANCE.registerMessage(id++, CPEnemyStep.class, CPEnemyStep::toBytes, CPEnemyStep::fromBytes, CPEnemyStep::handle);
      INSTANCE.registerMessage(
         id++, CPDoppelgangerTapHold.class, CPDoppelgangerTapHold::toBytes, CPDoppelgangerTapHold::fromBytes, CPDoppelgangerTapHold::handle
      );
      INSTANCE.registerMessage(
         id++, CPDoppelgangerDelayMode.class, CPDoppelgangerDelayMode::toBytes, CPDoppelgangerDelayMode::fromBytes, CPDoppelgangerDelayMode::handle
      );
      INSTANCE.registerMessage(id++, CPCrazyComboReset.class, CPCrazyComboReset::toBytes, CPCrazyComboReset::fromBytes, CPCrazyComboReset::handle);
      INSTANCE.registerMessage(
         id++, CPDoppelgangerControl.class, CPDoppelgangerControl::toBytes, CPDoppelgangerControl::fromBytes, CPDoppelgangerControl::handle
      );
      INSTANCE.registerMessage(id++, CPPlayerInputEvent.class, CPPlayerInputEvent::toBytes, CPPlayerInputEvent::fromBytes, CPPlayerInputEvent::handle);
      INSTANCE.registerMessage(
         id++, CPMovementInputPacket.class, CPMovementInputPacket::toBytes, CPMovementInputPacket::fromBytes, CPMovementInputPacket::handle
      );
      INSTANCE.registerMessage(id++, CPSummonedSword.class, CPSummonedSword::toBytes, CPSummonedSword::fromBytes, CPSummonedSword::handle);
      INSTANCE.registerMessage(id++, CPWeaponSwitch.class, CPWeaponSwitch::encode, CPWeaponSwitch::decode, CPWeaponSwitch::handle);
      INSTANCE.registerMessage(id++, SPWeaponState.class, SPWeaponState::encode, SPWeaponState::decode, SPWeaponState::handle);
      INSTANCE.registerMessage(id++, CPYamatoSheath.class, CPYamatoSheath::encode, CPYamatoSheath::decode, CPYamatoSheath::handle);
      INSTANCE.registerMessage(
         id++, CPJudgementCutStyleSync.class, CPJudgementCutStyleSync::encode, CPJudgementCutStyleSync::decode, CPJudgementCutStyleSync::handle
      );
      INSTANCE.registerMessage(id++, CPPortalStyleSync.class, CPPortalStyleSync::encode, CPPortalStyleSync::decode, CPPortalStyleSync::handle);
      INSTANCE.messageBuilder(S2CCameraShakePacket.class, id++)
         .encoder(S2CCameraShakePacket::encode)
         .decoder(S2CCameraShakePacket::decode)
         .consumerNetworkThread(S2CCameraShakePacket::handle)
         .add();
      INSTANCE.messageBuilder(S2CDoppelgangerSyncPacket.class, id++)
         .encoder(S2CDoppelgangerSyncPacket::encode)
         .decoder(S2CDoppelgangerSyncPacket::decode)
         .consumerNetworkThread(S2CDoppelgangerSyncPacket::handle)
         .add();
      INSTANCE.messageBuilder(S2CPortalDestinationsPacket.class, id++)
         .encoder(S2CPortalDestinationsPacket::encode)
         .decoder(S2CPortalDestinationsPacket::decode)
         .consumerNetworkThread(S2CPortalDestinationsPacket::handle)
         .add();
      INSTANCE.messageBuilder(CPPortalDestinationChosen.class, id++)
         .encoder(CPPortalDestinationChosen::toBytes)
         .decoder(CPPortalDestinationChosen::fromBytes)
         .consumerNetworkThread(CPPortalDestinationChosen::handle)
         .add();
      INSTANCE.registerMessage(id++, CPPlayerInputSync.class, CPPlayerInputSync::toBytes, CPPlayerInputSync::fromBytes, CPPlayerInputSync::handle);
      INSTANCE.messageBuilder(SPDirectionConsumed.class, id++)
         .encoder(SPDirectionConsumed::encode)
         .decoder(SPDirectionConsumed::decode)
         .consumerNetworkThread(SPDirectionConsumed::handle)
         .add();
      INSTANCE.messageBuilder(SPCrazyComboReset.class, id++)
         .encoder(SPCrazyComboReset::toBytes)
         .decoder(SPCrazyComboReset::fromBytes)
         .consumerNetworkThread(SPCrazyComboReset::handle)
         .add();
      INSTANCE.messageBuilder(S2CSdtEffectPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
         .encoder(S2CSdtEffectPacket::encode)
         .decoder(S2CSdtEffectPacket::decode)
         .consumerNetworkThread(S2CSdtEffectPacket::handle)
         .add();
      INSTANCE.messageBuilder(S2CMeteorShowerPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
         .encoder(S2CMeteorShowerPacket::encode)
         .decoder(S2CMeteorShowerPacket::decode)
         .consumerNetworkThread(S2CMeteorShowerPacket::handle)
         .add();
      INSTANCE.messageBuilder(S2CAnimationElapsedTimePacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
         .encoder(S2CAnimationElapsedTimePacket::encode)
         .decoder(S2CAnimationElapsedTimePacket::decode)
         .consumerNetworkThread(S2CAnimationElapsedTimePacket::handle)
         .add();
      INSTANCE.messageBuilder(CPDiscardSpineBlade.class, id++, NetworkDirection.PLAY_TO_SERVER)
         .encoder(CPDiscardSpineBlade::encode)
         .decoder(CPDiscardSpineBlade::decode)
         .consumerNetworkThread(CPDiscardSpineBlade::handle)
         .add();
      INSTANCE.messageBuilder(CPInstantJudgementCutEndToggle.class, id++, NetworkDirection.PLAY_TO_SERVER)
         .encoder(CPInstantJudgementCutEndToggle::encode)
         .decoder(CPInstantJudgementCutEndToggle::decode)
         .consumerNetworkThread(CPInstantJudgementCutEndToggle::handle)
         .add();
      INSTANCE.messageBuilder(SPInstantJudgementCutEndState.class, id++, NetworkDirection.PLAY_TO_CLIENT)
         .encoder(SPInstantJudgementCutEndState::encode)
         .decoder(SPInstantJudgementCutEndState::decode)
         .consumerNetworkThread(SPInstantJudgementCutEndState::handle)
         .add();
   }
}
