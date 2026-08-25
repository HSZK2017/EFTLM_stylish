package com.Yujin.onegradefixer.epicmoonmod.client;

import com.Yujin.onegradefixer.epicmoonmod.item.EpicmoonItems;
import com.mojang.blaze3d.platform.Window;
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
public class TSHud {
   private static final ResourceLocation EmptyBullet = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/EmptyBullet.png");
   private static final ResourceLocation TigerBullet1 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/TigerBullet1.png");
   private static final ResourceLocation TigerBullet2 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/TigerBullet2.png");
   private static final ResourceLocation TigerBullet3 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/TigerBullet3.png");
   private static final ResourceLocation TigerBullet4 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/TigerBullet4.png");
   private static final ResourceLocation TigerBullet5 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/TigerBullet5.png");
   private static final ResourceLocation TigerBullet6 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/TigerBullet6.png");
   private static final ResourceLocation TigerBullet7 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/TigerBullet7.png");
   private static final ResourceLocation TigerBulletFull = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/TigerBulletFull.png");
   private static final ResourceLocation SavageTigerBullet1 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/SavageTigerBullet1.png");
   private static final ResourceLocation SavageTigerBullet2 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/SavageTigerBullet2.png");
   private static final ResourceLocation SavageTigerBullet3 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/SavageTigerBullet3.png");
   private static final ResourceLocation SavageTigerBullet4 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/SavageTigerBullet4.png");
   private static final ResourceLocation SavageTigerBullet5 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/SavageTigerBullet5.png");
   private static final ResourceLocation SavageTigerBullet6 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/SavageTigerBullet6.png");
   private static final ResourceLocation SavageTigerBullet7 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/SavageTigerBullet7.png");
   private static final ResourceLocation SavageTigerBulletFull = ResourceLocation.fromNamespaceAndPath(
      "epicmoonmod", "textures/gui/weapon/SavageTigerBulletFull.png"
   );
   private static final ResourceLocation Light = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/Light.png");
   private static final ResourceLocation Light1 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/Light1.png");
   private static final ResourceLocation Light2 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/Light2.png");
   private static final ResourceLocation Light3 = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/Light3.png");
   private static final ResourceLocation Test = ResourceLocation.fromNamespaceAndPath("epicmoonmod", "textures/gui/weapon/Test.png");

   public static void RenderGui(GuiGraphics guiGraphics, Window window, float partialTick) {
      LocalPlayerPatch localPlayerPatch = ClientEngine.getInstance().getPlayerPatch();
      int y = window.m_85446_() - 64;
      int x = window.m_85445_() - 110;
      int v = window.m_85446_() - 64;
      int z = window.m_85445_() - 110;
      int l = window.m_85446_() - 100;
      int s = window.m_85445_() - 110;
      if (localPlayerPatch != null) {
         Player player = (Player)localPlayerPatch.getOriginal();
         ItemStack a = player.m_21205_();
         Item b = a.m_41720_();
         Item c = (Item)EpicmoonItems.TENTAI_SEITOU.get();
         CompoundTag TSTG = a.m_41783_();
         int f = localPlayerPatch.getChargingAmount();
         if (TSTG != null) {
            int d = TSTG.m_128451_("ammotype");
            int e = TSTG.m_128451_("amount");
            if (b == c) {
               player.getCapability(EpicFightCapabilities.CAPABILITY_SKILL).ifPresent(capabilitySkill -> {
                  SkillContainer skillContainer = capabilitySkill.getSkillContainerFor(SkillSlots.WEAPON_INNATE);
                  int aa = skillContainer.getStack();
                  switch (aa) {
                     case 0:
                        guiGraphics.m_280411_(Light, s, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                        break;
                     case 1:
                        guiGraphics.m_280411_(Light1, s, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                        break;
                     case 2:
                        guiGraphics.m_280411_(Light2, s, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                        break;
                     case 3:
                        guiGraphics.m_280411_(Light3, s, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                  }
               });
               guiGraphics.m_280411_(Test, x, v, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
               switch (d) {
                  case 1:
                     switch (e) {
                        case 1:
                           guiGraphics.m_280411_(TigerBullet1, z, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                           return;
                        case 2:
                           guiGraphics.m_280411_(TigerBullet2, z, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                           return;
                        case 3:
                           guiGraphics.m_280411_(TigerBullet3, z, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                           return;
                        case 4:
                           guiGraphics.m_280411_(TigerBullet4, z, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                           return;
                        case 5:
                           guiGraphics.m_280411_(TigerBullet5, z, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                           return;
                        case 6:
                           guiGraphics.m_280411_(TigerBullet6, z, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                           return;
                        case 7:
                           guiGraphics.m_280411_(TigerBullet7, z, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                           return;
                        case 8:
                           guiGraphics.m_280411_(TigerBulletFull, z, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                           return;
                        default:
                           return;
                     }
                  case 2:
                     switch (e) {
                        case 1:
                           guiGraphics.m_280411_(SavageTigerBullet1, z, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                           return;
                        case 2:
                           guiGraphics.m_280411_(SavageTigerBullet2, z, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                           return;
                        case 3:
                           guiGraphics.m_280411_(SavageTigerBullet3, z, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                           return;
                        case 4:
                           guiGraphics.m_280411_(SavageTigerBullet4, z, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                           return;
                        case 5:
                           guiGraphics.m_280411_(SavageTigerBullet5, z, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                           return;
                        case 6:
                           guiGraphics.m_280411_(SavageTigerBullet6, z, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                           return;
                        case 7:
                           guiGraphics.m_280411_(SavageTigerBullet7, z, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                           return;
                        case 8:
                           guiGraphics.m_280411_(SavageTigerBulletFull, z, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
                           return;
                        default:
                           return;
                     }
                  default:
                     guiGraphics.m_280411_(EmptyBullet, z, y, 105, 54, 0.0F, 0.0F, 64, 64, 64, 64);
               }
            }
         }
      }
   }
}
