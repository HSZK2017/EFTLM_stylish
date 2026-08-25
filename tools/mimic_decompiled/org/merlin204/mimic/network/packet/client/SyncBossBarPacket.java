package org.merlin204.mimic.network.packet.client;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import org.merlin204.mimic.client.gui.BossBar;
import org.merlin204.mimic.network.packet.BasePacket;

public record SyncBossBarPacket(UUID serverUuid, int id) implements BasePacket {
   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.m_130077_(this.serverUuid);
      buf.writeInt(this.id);
   }

   public static SyncBossBarPacket decode(FriendlyByteBuf buf) {
      return new SyncBossBarPacket(buf.m_130259_(), buf.readInt());
   }

   @Override
   public void execute(@Nullable Player player) {
      BossBar.BOSSES.put(this.serverUuid, this.id);
   }
}
