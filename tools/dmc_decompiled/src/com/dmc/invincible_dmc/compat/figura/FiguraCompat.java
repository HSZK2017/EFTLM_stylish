package com.dmc.invincible_dmc.compat.figura;

import com.dmc.invincible_dmc.client.compat.CosmeticRenderCompat;
import com.dmc.invincible_dmc.compat.ICompatModule;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;

@OnlyIn(Dist.CLIENT)
public final class FiguraCompat implements ICompatModule {
   @Override
   public void onLoad(FMLJavaModLoadingContext context) {
      CosmeticRenderCompat.registerFiguraAvatarDetector(FiguraCompat::hasRenderableAvatar);
   }

   private static boolean hasRenderableAvatar(Entity entity) {
      Avatar avatar = AvatarManager.getAvatar(entity);
      return avatar != null && avatar.loaded && avatar.renderer != null && avatar.renderer.root != null;
   }
}
