package com.dmc.invincible_dmc.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class DMCBlocks {
   public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "invincible_dmc");
   private static final VoxelShape CARPET_SHAPE = Block.m_49796_(0.0, 0.0, 0.0, 10.0, 1.0, 10.0);
   public static final RegistryObject<Block> POWER_CHAIR = BLOCKS.register(
      "power_chair", () -> new PowerChairBlock(Properties.m_284310_().m_60913_(2.0F, 6.0F).m_60955_().m_60918_(SoundType.f_56743_))
   );
   public static final RegistryObject<Block> VOID_BARRIER = BLOCKS.register(
      "void_barrier",
      () -> new Block(
            Properties.m_284310_()
               .m_60913_(-1.0F, 3600000.0F)
               .m_222994_()
               .m_60922_((s, r, p, t) -> false)
               .m_60955_()
               .m_60960_((s, r, p) -> false)
               .m_60971_((s, r, p) -> false)
               .m_60918_(SoundType.f_56744_)
         ) {
            public VoxelShape m_5940_(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
               return DMCBlocks.CARPET_SHAPE;
            }
         }
   );
}
