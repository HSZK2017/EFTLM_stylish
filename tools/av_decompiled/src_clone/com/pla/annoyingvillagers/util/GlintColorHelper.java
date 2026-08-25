package com.pla.annoyingvillagers.util;

import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public final class GlintColorHelper {
   public static final String TAG_COLOR_GLINT = "ColorGlint";
   public static final int NONE = 0;
   public static final int ORANGE = 1;
   public static final int CYAN = 2;
   public static final int BLUE = 3;
   public static final int GREEN = 4;
   public static final int LIGHT_BLUE = 5;
   public static final int LIME = 6;
   public static final int MAGENTA = 7;
   public static final int PINK = 8;
   public static final int PURPLE = 9;
   public static final int RED = 10;
   public static final int YELLOW = 11;

   private GlintColorHelper() {
   }

   public static int getRandomColor() {
      return switch (new Random().nextInt(11)) {
         case 0 -> 1;
         case 1 -> 2;
         case 2 -> 3;
         case 3 -> 4;
         case 4 -> 5;
         case 5 -> 6;
         case 6 -> 7;
         case 7 -> 8;
         case 8 -> 9;
         case 9 -> 10;
         default -> 11;
      };
   }

   public static Vec3 getParticleColor(int mode) {
      return switch (mode) {
         case 1 -> new Vec3(1.0, 0.55, 0.1);
         case 2 -> new Vec3(0.2, 0.9, 1.0);
         case 3 -> new Vec3(0.25, 0.45, 1.0);
         case 4 -> new Vec3(0.2, 0.9, 0.2);
         case 5 -> new Vec3(0.45, 0.8, 1.0);
         case 6 -> new Vec3(0.65, 1.0, 0.2);
         case 7 -> new Vec3(1.0, 0.25, 0.95);
         case 8 -> new Vec3(1.0, 0.55, 0.75);
         case 9 -> new Vec3(0.65, 0.3, 1.0);
         case 10 -> new Vec3(1.0, 0.15, 0.15);
         case 11 -> new Vec3(1.0, 0.95, 0.2);
         default -> new Vec3(0.5, 0.5, 0.5);
      };
   }

   public static void setColor(ItemStack stack, int mode) {
      if (!stack.m_41619_()) {
         if (mode == 0) {
            CompoundTag tag = stack.m_41783_();
            if (tag != null) {
               tag.m_128473_("ColorGlint");
            }
         } else {
            stack.m_41784_().m_128359_("ColorGlint", toName(mode));
         }
      }
   }

   public static void clearColor(ItemStack stack) {
      if (stack.m_41782_()) {
         stack.m_41783_().m_128473_("ColorGlint");
         if (stack.m_41783_().m_128456_()) {
            stack.m_41751_(null);
         }
      }
   }

   public static int getColor(ItemStack stack) {
      CompoundTag tag = stack.m_41783_();
      if (tag == null) {
         return 0;
      } else if (tag.m_128425_("ColorGlint", 3)) {
         return sanitize(tag.m_128451_("ColorGlint"));
      } else {
         return tag.m_128425_("ColorGlint", 8) ? fromName(tag.m_128461_("ColorGlint")) : 0;
      }
   }

   public static boolean hasColor(ItemStack stack) {
      return getColor(stack) != 0;
   }

   public static int sanitize(int mode) {
      return mode >= 0 && mode <= 11 ? mode : 0;
   }

   public static int fromName(String name) {
      return switch (name) {
         case "orange" -> 1;
         case "cyan" -> 2;
         case "blue" -> 3;
         case "green" -> 4;
         case "light_blue" -> 5;
         case "lime" -> 6;
         case "magenta" -> 7;
         case "pink" -> 8;
         case "purple" -> 9;
         case "red" -> 10;
         case "yellow" -> 11;
         default -> 0;
      };
   }

   public static String toName(int mode) {
      return switch (mode) {
         case 1 -> "orange";
         case 2 -> "cyan";
         case 3 -> "blue";
         case 4 -> "green";
         case 5 -> "light_blue";
         case 6 -> "lime";
         case 7 -> "magenta";
         case 8 -> "pink";
         case 9 -> "purple";
         case 10 -> "red";
         case 11 -> "yellow";
         default -> "";
      };
   }
}
