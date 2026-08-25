package com.Yujin.onegradefixer.epicmoonmod.comboevents.events;

import com.Yujin.onegradefixer.epicmoonmod.item.EpicmoonItems;
import com.p1nero.invincible.api.events.TimeStampedEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ReloadEventD {
   private static final ItemStack AR = ((Item)EpicmoonItems.Accel_ROUND.get()).m_7968_();

   public static TimeStampedEvent Reload(float time) {
      return new TimeStampedEvent(time, entitypatch -> {
         Player player = (Player)entitypatch.getOriginal();
         Inventory inventory = player.m_150109_();
         int AAR = inventory.m_18947_(AR.m_41720_());
         ItemStack D = inventory.f_35978_.m_21205_();
         CompoundTag DT = D.m_41783_();
         int count = 0;
         if (AAR != 0) {
            for (int i = 0; i < 36; i++) {
               if (DT.m_128451_("amount") != 0) {
                  int CA = DT.m_128451_("amount");
                  int ND = 10 - CA;
                  if (AAR > ND) {
                     DT.m_128405_("amount", 10);

                     for (int c = 0; c < 36; c++) {
                        if (inventory.m_8020_(c).m_41720_() == EpicmoonItems.Accel_ROUND.get()) {
                           int a = inventory.m_8020_(c).m_41613_();
                           if (a > ND) {
                              int b = ND - count;
                              inventory.m_7407_(c, b);
                              count += ND;
                           } else if (count + a >= ND) {
                              int b = ND - count;
                              inventory.m_7407_(c, b);
                              count = ND;
                           } else {
                              inventory.m_7407_(c, a);
                              count += a;
                           }
                        }

                        if (count == ND) {
                           count -= ND;
                           return;
                        }
                     }
                  } else {
                     DT.m_128405_("amount", AAR + CA);

                     for (int c = 0; c < 36; c++) {
                        if (inventory.m_8020_(c).m_41720_() == EpicmoonItems.Accel_ROUND.get()) {
                           int a = inventory.m_8020_(c).m_41613_();
                           if (a > AAR) {
                              int b = AAR - count;
                              inventory.m_7407_(c, b);
                              count += AAR;
                           } else if (count + a >= AAR) {
                              int b = AAR - count;
                              inventory.m_7407_(c, b);
                              count = AAR;
                           } else {
                              inventory.m_7407_(c, a);
                              count += a;
                           }
                        }

                        if (count == AAR) {
                           count -= AAR;
                           return;
                        }
                     }
                  }
                  break;
               }

               if (inventory.m_8020_(i).m_41720_() == EpicmoonItems.Accel_ROUND.get()) {
                  if (AAR > 10) {
                     DT.m_128405_("amount", 10);

                     for (int c = 0; c < 36; c++) {
                        if (inventory.m_8020_(c).m_41720_() == EpicmoonItems.Accel_ROUND.get()) {
                           int a = inventory.m_8020_(c).m_41613_();
                           if (a > 10) {
                              int b = 10 - count;
                              inventory.m_7407_(c, b);
                              count += 10;
                           } else if (count + a >= 10) {
                              int b = 10 - count;
                              inventory.m_7407_(c, b);
                              count = 10;
                           } else {
                              inventory.m_7407_(c, a);
                              count += a;
                           }
                        }

                        if (count == 10) {
                           count -= 10;
                           return;
                        }
                     }
                  } else {
                     DT.m_128405_("amount", AAR);

                     for (int c = 0; c < 36; c++) {
                        if (inventory.m_8020_(c).m_41720_() == EpicmoonItems.Accel_ROUND.get()) {
                           int a = inventory.m_8020_(c).m_41613_();
                           if (a > AAR) {
                              int b = AAR - count;
                              inventory.m_7407_(c, b);
                              count += AAR;
                           } else if (count + a >= AAR) {
                              int b = AAR - count;
                              inventory.m_7407_(c, b);
                              count = AAR;
                           } else {
                              inventory.m_7407_(c, a);
                              count += a;
                           }
                        }

                        if (count == AAR) {
                           count -= AAR;
                           return;
                        }
                     }
                  }
                  break;
               }
            }
         }
      });
   }
}
