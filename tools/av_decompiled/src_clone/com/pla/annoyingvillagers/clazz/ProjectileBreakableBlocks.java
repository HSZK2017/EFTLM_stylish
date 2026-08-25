package com.pla.annoyingvillagers.clazz;

import java.util.function.Predicate;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;

public enum ProjectileBreakableBlocks {
   GLASS_LIKE(s -> neverBreak(s) && (s.m_60734_() instanceof AbstractGlassBlock || s.m_60734_() instanceof StainedGlassPaneBlock), 0.1F, 0.2F),
   STONE_LIKE(
      s -> neverBreak(s) && s.m_204336_(BlockTags.f_144282_) && !isOre(s) && !s.m_204336_(BlockTags.f_13069_) && !s.m_204336_(BlockTags.f_13070_), 0.85F, 1.0F
   ),
   WOOD_LIKE(
      s -> neverBreak(s)
            && (
               s.m_204336_(BlockTags.f_13106_)
                  || s.m_204336_(BlockTags.f_13105_)
                  || s.m_204336_(BlockTags.f_13090_)
                  || s.m_204336_(BlockTags.f_13095_)
                  || s.m_204336_(BlockTags.f_13102_)
                  || s.m_204336_(BlockTags.f_13098_)
                  || s.m_204336_(BlockTags.f_13055_)
                  || s.m_204336_(BlockTags.f_13096_)
                  || s.m_204336_(BlockTags.f_13097_)
                  || s.m_204336_(BlockTags.f_13092_)
                  || s.m_204336_(BlockTags.f_13100_)
                  || s.m_204336_(BlockTags.f_260523_)
                  || s.m_204336_(BlockTags.f_244320_)
                  || s.m_60713_(Blocks.f_50091_)
            ),
      0.55F,
      0.5F
   ),
   SOFT_GROUND(
      s -> neverBreak(s)
            && (
               s.m_204336_(BlockTags.f_144274_)
                  || s.m_204336_(BlockTags.f_13029_)
                  || s.m_204336_(BlockTags.f_144279_)
                  || s.m_60713_(Blocks.f_49994_)
                  || s.m_204336_(BlockTags.f_144276_)
                  || s.m_204336_(BlockTags.f_144277_)
            ),
      0.85F,
      1.0F
   ),
   PLANTS(
      s -> neverBreak(s)
            && (
               s.m_204336_(BlockTags.f_13035_)
                  || s.m_204336_(BlockTags.f_13041_)
                  || s.m_204336_(BlockTags.f_13037_)
                  || s.m_204336_(BlockTags.f_13040_)
                  || s.m_204336_(BlockTags.f_13073_)
                  || s.m_204336_(BlockTags.f_13104_)
                  || s.m_204336_(BlockTags.f_144275_)
                  || s.m_60713_(Blocks.f_50191_)
                  || s.m_60713_(Blocks.f_50128_)
            ),
      0.05F,
      0.08F
   );

   public final Predicate<BlockState> matcher;
   public final float requiredPower;
   public final float powerCost;

   private ProjectileBreakableBlocks(Predicate<BlockState> matcher, float requiredPower, float powerCost) {
      this.matcher = matcher;
      this.requiredPower = requiredPower;
      this.powerCost = powerCost;
   }

   public static ProjectileBreakableBlocks find(BlockState state) {
      for (ProjectileBreakableBlocks rule : values()) {
         if (rule.matcher.test(state)) {
            return rule;
         }
      }

      return null;
   }

   private static boolean isOre(BlockState s) {
      return s.m_204336_(BlockTags.f_144262_)
         || s.m_204336_(BlockTags.f_144258_)
         || s.m_204336_(BlockTags.f_13043_)
         || s.m_204336_(BlockTags.f_144259_)
         || s.m_204336_(BlockTags.f_144260_)
         || s.m_204336_(BlockTags.f_144261_)
         || s.m_204336_(BlockTags.f_144263_)
         || s.m_204336_(BlockTags.f_144264_);
   }

   private static boolean neverBreak(BlockState s) {
      return !s.m_60713_(Blocks.f_50752_) && !s.m_60713_(Blocks.f_50080_) && !s.m_60713_(Blocks.f_50723_) && !isSurfaceGrassLike(s);
   }

   private static boolean isSurfaceGrassLike(BlockState s) {
      return s.m_60713_(Blocks.f_50440_)
         || s.m_60713_(Blocks.f_50599_)
         || s.m_60713_(Blocks.f_50195_)
         || s.m_60713_(Blocks.f_152481_)
         || s.m_60713_(Blocks.f_49992_);
   }
}
