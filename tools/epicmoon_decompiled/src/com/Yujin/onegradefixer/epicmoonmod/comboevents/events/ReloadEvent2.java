package com.Yujin.onegradefixer.epicmoonmod.comboevents.events;

import com.Yujin.onegradefixer.epicmoonmod.item.EpicmoonItems;
import com.Yujin.onegradefixer.epicmoonmod.sound.EMsounds;
import com.p1nero.invincible.api.events.TimeStampedEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ReloadEvent2 {
   private static final ItemStack STR = ((Item)EpicmoonItems.SAVAGE_TIGERMARK_ROUND.get()).m_7968_();
   private static final ItemStack TR = ((Item)EpicmoonItems.TIGERMARK_ROUND.get()).m_7968_();

   public static TimeStampedEvent Reload2(float time) {
      return new TimeStampedEvent(time, entitypatch -> {
         Player player = (Player)entitypatch.getOriginal();
         Inventory inventory = player.m_150109_();
         int ASTR = inventory.m_18947_(STR.m_41720_());
         int ATR = inventory.m_18947_(TR.m_41720_());
         ItemStack TS = inventory.f_35978_.m_21205_();
         CompoundTag TSTG = TS.m_41783_();
         int count = 0;
         if (TSTG == null) {
            TSTG = new CompoundTag();
            TS.m_41751_(TSTG);
            if (TSTG.m_128441_("ammotype")) {
               return;
            }

            TSTG.m_128405_("ammotype", 0);
         }

         for (int i = 0; i < 36; i++) {
            if (TSTG.m_128451_("ammotype") == 0) {
               if (inventory.m_8020_(i).m_41720_() == EpicmoonItems.SAVAGE_TIGERMARK_ROUND.get()) {
                  TSTG.m_128405_("ammotype", 2);
                  if (ASTR > 8) {
                     TSTG.m_128405_("amount", 8);

                     for (int c = 0; c < 36; c++) {
                        if (inventory.m_8020_(c).m_41720_() == EpicmoonItems.SAVAGE_TIGERMARK_ROUND.get()) {
                           int a = inventory.m_8020_(c).m_41613_();
                           if (a > 8) {
                              int b = 8 - count;
                              inventory.m_7407_(c, b);
                              count += 8;
                           } else if (count + a >= 8) {
                              int b = 8 - count;
                              inventory.m_7407_(c, b);
                              count = 8;
                           } else {
                              inventory.m_7407_(c, a);
                              count += a;
                           }
                        }

                        if (count == 8) {
                           count -= 8;
                           break;
                        }
                     }

                     ItemStack b = new ItemStack((ItemLike)EpicmoonItems.CARTRIDGE_CASE.get(), 8);
                     entitypatch.playSound((SoundEvent)EMsounds.RELOAD2.get(), 0.5F, 0.0F, 0.0F);
                     player.m_36176_(b, false);
                  } else {
                     TSTG.m_128405_("amount", ASTR);

                     for (int c = 0; c < 36; c++) {
                        if (inventory.m_8020_(c).m_41720_() == EpicmoonItems.SAVAGE_TIGERMARK_ROUND.get()) {
                           int a = inventory.m_8020_(c).m_41613_();
                           if (a > ASTR) {
                              int b = ASTR - count;
                              inventory.m_7407_(c, b);
                              count += ASTR;
                           } else if (count + a >= ASTR) {
                              int b = ASTR - count;
                              inventory.m_7407_(c, b);
                              count = ASTR;
                           } else {
                              inventory.m_7407_(c, a);
                              count += a;
                           }
                        }

                        if (count == ASTR) {
                           count -= ASTR;
                           break;
                        }
                     }

                     ItemStack b = new ItemStack((ItemLike)EpicmoonItems.CARTRIDGE_CASE.get(), ASTR);
                     if (ASTR != 0) {
                        entitypatch.playSound((SoundEvent)EMsounds.RELOAD2.get(), 0.5F, 0.0F, 0.0F);
                     }

                     player.m_36176_(b, false);
                  }

                  entitypatch.playSound((SoundEvent)EMsounds.RELOAD2.get(), 0.5F, 0.0F, 0.0F);
                  break;
               }

               if (inventory.m_8020_(i).m_41720_() == EpicmoonItems.TIGERMARK_ROUND.get()) {
                  TSTG.m_128405_("ammotype", 1);
                  if (ATR > 8) {
                     TSTG.m_128405_("amount", 8);

                     for (int c = 0; c < 36; c++) {
                        if (inventory.m_8020_(c).m_41720_() == EpicmoonItems.TIGERMARK_ROUND.get()) {
                           int a = inventory.m_8020_(c).m_41613_();
                           if (a > 8) {
                              int b = 8 - count;
                              inventory.m_7407_(c, b);
                              count += 8;
                           } else if (count + a >= 8) {
                              int b = 8 - count;
                              inventory.m_7407_(c, b);
                              count = 8;
                           } else {
                              inventory.m_7407_(c, a);
                              count += a;
                           }
                        }

                        if (count == 8) {
                           count -= 8;
                           break;
                        }
                     }

                     ItemStack b = new ItemStack((ItemLike)EpicmoonItems.CARTRIDGE_CASE.get(), 8);
                     entitypatch.playSound((SoundEvent)EMsounds.RELOAD2.get(), 0.5F, 0.0F, 0.0F);
                     player.m_36176_(b, false);
                  } else {
                     TSTG.m_128405_("amount", ATR);

                     for (int c = 0; c < 36; c++) {
                        if (inventory.m_8020_(c).m_41720_() == EpicmoonItems.TIGERMARK_ROUND.get()) {
                           int a = inventory.m_8020_(c).m_41613_();
                           if (a > ATR) {
                              int b = ATR - count;
                              inventory.m_7407_(c, b);
                              count += ATR;
                           } else if (count + a >= ATR) {
                              int b = ATR - count;
                              inventory.m_7407_(c, b);
                              count = ATR;
                           } else {
                              inventory.m_7407_(c, a);
                              count += a;
                           }
                        }

                        if (count == ATR) {
                           count -= ATR;
                           break;
                        }
                     }

                     ItemStack b = new ItemStack((ItemLike)EpicmoonItems.CARTRIDGE_CASE.get(), ATR);
                     if (ATR != 0) {
                        entitypatch.playSound((SoundEvent)EMsounds.RELOAD2.get(), 0.5F, 0.0F, 0.0F);
                     }

                     player.m_36176_(b, false);
                  }
                  break;
               }
            } else {
               int CA = TSTG.m_128451_("amount");
               int ND = 8 - CA;
               if (TSTG.m_128451_("ammotype") == 2) {
                  if (ASTR > ND) {
                     TSTG.m_128405_("amount", 8);

                     for (int c = 0; c < 36; c++) {
                        if (inventory.m_8020_(c).m_41720_() == EpicmoonItems.SAVAGE_TIGERMARK_ROUND.get()) {
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
                           break;
                        }
                     }

                     ItemStack b = new ItemStack((ItemLike)EpicmoonItems.CARTRIDGE_CASE.get(), ND);
                     entitypatch.playSound((SoundEvent)EMsounds.RELOAD2.get(), 0.5F, 0.0F, 0.0F);
                     player.m_36176_(b, false);
                  } else {
                     TSTG.m_128405_("amount", ASTR + CA);

                     for (int c = 0; c < 36; c++) {
                        if (inventory.m_8020_(c).m_41720_() == EpicmoonItems.SAVAGE_TIGERMARK_ROUND.get()) {
                           int a = inventory.m_8020_(c).m_41613_();
                           if (a > ASTR) {
                              int b = ASTR - count;
                              inventory.m_7407_(c, b);
                              count += ASTR;
                           } else if (count + a >= ASTR) {
                              int b = ASTR - count;
                              inventory.m_7407_(c, b);
                              count = ASTR;
                           } else {
                              inventory.m_7407_(c, a);
                              count += a;
                           }
                        }

                        if (count == ASTR) {
                           count -= ASTR;
                           break;
                        }
                     }

                     ItemStack b = new ItemStack((ItemLike)EpicmoonItems.CARTRIDGE_CASE.get(), ASTR);
                     if (ASTR != 0) {
                        entitypatch.playSound((SoundEvent)EMsounds.RELOAD2.get(), 0.5F, 0.0F, 0.0F);
                     }

                     player.m_36176_(b, false);
                  }
                  break;
               }

               if (TSTG.m_128451_("ammotype") == 1) {
                  if (ATR > ND) {
                     TSTG.m_128405_("amount", 8);

                     for (int c = 0; c < 36; c++) {
                        if (inventory.m_8020_(c).m_41720_() == EpicmoonItems.TIGERMARK_ROUND.get()) {
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
                           break;
                        }
                     }

                     ItemStack b = new ItemStack((ItemLike)EpicmoonItems.CARTRIDGE_CASE.get(), ND);
                     entitypatch.playSound((SoundEvent)EMsounds.RELOAD2.get(), 0.5F, 0.0F, 0.0F);
                     player.m_36176_(b, false);
                  } else {
                     TSTG.m_128405_("amount", ATR + CA);

                     for (int c = 0; c < 36; c++) {
                        if (inventory.m_8020_(c).m_41720_() == EpicmoonItems.TIGERMARK_ROUND.get()) {
                           int a = inventory.m_8020_(c).m_41613_();
                           if (a > ATR) {
                              int b = ATR - count;
                              inventory.m_7407_(c, b);
                              count += ATR;
                           } else if (count + a >= ATR) {
                              int b = ATR - count;
                              inventory.m_7407_(c, b);
                              count = ATR;
                           } else {
                              inventory.m_7407_(c, a);
                              count += a;
                           }
                        }

                        if (count == ATR) {
                           count -= ATR;
                           break;
                        }
                     }

                     ItemStack b = new ItemStack((ItemLike)EpicmoonItems.CARTRIDGE_CASE.get(), ATR);
                     if (ATR != 0) {
                        entitypatch.playSound((SoundEvent)EMsounds.RELOAD2.get(), 0.5F, 0.0F, 0.0F);
                     }

                     player.m_36176_(b, false);
                  }
                  break;
               }
            }
         }
      });
   }
}
