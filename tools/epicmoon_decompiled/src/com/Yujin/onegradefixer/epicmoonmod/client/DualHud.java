package com.Yujin.onegradefixer.epicmoonmod.client;

import com.Yujin.onegradefixer.epicmoonmod.item.EpicmoonItems;
import com.Yujin.onegradefixer.epicmoonmod.skill.weapon_innate.DualInnate;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@OnlyIn(Dist.CLIENT)
public class DualHud {
   private static final ResourceLocation EmptyBullet = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/EmptyBulletD.png");
   private static final ResourceLocation AccelBullet1 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/AccelBullet1.png");
   private static final ResourceLocation AccelBullet2 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/AccelBullet2.png");
   private static final ResourceLocation AccelBullet3 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/AccelBullet3.png");
   private static final ResourceLocation AccelBullet4 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/AccelBullet4.png");
   private static final ResourceLocation AccelBullet5 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/AccelBullet5.png");
   private static final ResourceLocation AccelBullet6 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/AccelBullet6.png");
   private static final ResourceLocation AccelBullet7 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/AccelBullet7.png");
   private static final ResourceLocation AccelBullet8 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/AccelBullet8.png");
   private static final ResourceLocation AccelBullet9 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/AccelBullet9.png");
   private static final ResourceLocation AccelBullet10 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/AccelBullet10.png");
   private static final ResourceLocation Light = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/dlight.png");
   private static final ResourceLocation Light1 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/dlight1.png");
   private static final ResourceLocation Light2 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/dlight2.png");
   private static final ResourceLocation Eye = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/Eye.png");

   public static void RenderGui(GuiGraphics guiGraphics, Window window, float partialTick) {
      LocalPlayerPatch localPlayerPatch = ClientEngine.getInstance().getPlayerPatch();
      int y = window.m_85446_() - 42;
      int x = window.m_85445_() - 42;
      int v = window.m_85446_() - 48;
      int z = window.m_85445_() - 125;
      int s = window.m_85445_() - 138;
      int l = window.m_85446_() - 20;
      Font font = Minecraft.m_91087_().f_91062_;
      if (localPlayerPatch != null) {
         Player player = (Player)localPlayerPatch.getOriginal();
         ItemStack a = player.m_21205_();
         Item b = a.m_41720_();
         Item c = (Item)EpicmoonItems.VALENCINA_DUAL_SWORDS.get();
         CompoundTag TSTG = a.m_41783_();
         if (TSTG != null) {
            int e = TSTG.m_128451_("amount");
            if (b == c) {
               player.getCapability(EpicFightCapabilities.CAPABILITY_SKILL).ifPresent(capabilitySkill -> {
                  SkillContainer skillContainer = capabilitySkill.getSkillContainerFor(SkillSlots.WEAPON_INNATE);
                  if (skillContainer.getSkill() instanceof DualInnate) {
                     DualInnate dualInnate = (DualInnate)skillContainer.getSkill();
                     int d = dualInnate.getEye(skillContainer);
                     String text = String.valueOf(d);
                     guiGraphics.m_280056_(font, text, x, l, 65535, true);
                  }

                  int aa = skillContainer.getStack();
                  switch (aa) {
                     case 0:
                        guiGraphics.m_280411_(Light, s, v, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                        break;
                     case 1:
                        guiGraphics.m_280411_(Light1, s, v, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                        break;
                     case 2:
                        guiGraphics.m_280411_(Light2, s, v, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                  }
               });
               guiGraphics.m_280411_(Eye, x, y, 32, 32, 0.0F, 0.0F, 64, 64, 64, 64);
               switch (e) {
                  case 0:
                     guiGraphics.m_280411_(EmptyBullet, z, v, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                     break;
                  case 1:
                     guiGraphics.m_280411_(AccelBullet1, z, v, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                     break;
                  case 2:
                     guiGraphics.m_280411_(AccelBullet2, z, v, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                     break;
                  case 3:
                     guiGraphics.m_280411_(AccelBullet3, z, v, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                     break;
                  case 4:
                     guiGraphics.m_280411_(AccelBullet4, z, v, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                     break;
                  case 5:
                     guiGraphics.m_280411_(AccelBullet5, z, v, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                     break;
                  case 6:
                     guiGraphics.m_280411_(AccelBullet6, z, v, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                     break;
                  case 7:
                     guiGraphics.m_280411_(AccelBullet7, z, v, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                     break;
                  case 8:
                     guiGraphics.m_280411_(AccelBullet8, z, v, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                     break;
                  case 9:
                     guiGraphics.m_280411_(AccelBullet9, z, v, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                     break;
                  case 10:
                     guiGraphics.m_280411_(AccelBullet10, z, v, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
               }
            }
         }
      }
   }
}
