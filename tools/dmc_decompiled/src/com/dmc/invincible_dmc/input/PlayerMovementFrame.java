package com.dmc.invincible_dmc.input;

import net.minecraft.network.FriendlyByteBuf;

public record PlayerMovementFrame(float forward, float strafe, boolean jump, boolean sneak, float cameraYaw) {
   public static final PlayerMovementFrame EMPTY = new PlayerMovementFrame(0.0F, 0.0F, false, false, 0.0F);

   public boolean hasMovementInput() {
      return Math.abs(this.forward) > 0.01F || Math.abs(this.strafe) > 0.01F;
   }

   public void write(FriendlyByteBuf buf) {
      buf.writeFloat(this.forward);
      buf.writeFloat(this.strafe);
      buf.writeBoolean(this.jump);
      buf.writeBoolean(this.sneak);
      buf.writeFloat(this.cameraYaw);
   }

   public static PlayerMovementFrame read(FriendlyByteBuf buf) {
      return new PlayerMovementFrame(buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readBoolean(), buf.readFloat());
   }
}
