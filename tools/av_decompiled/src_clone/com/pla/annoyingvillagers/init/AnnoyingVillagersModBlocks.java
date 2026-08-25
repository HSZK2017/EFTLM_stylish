package com.pla.annoyingvillagers.init;

import com.pla.annoyingvillagers.block.CryingObsidianBlock;
import com.pla.annoyingvillagers.block.EnchantBedBlock;
import com.pla.annoyingvillagers.block.EndFireBlock;
import com.pla.annoyingvillagers.block.ObsidianBlock;
import com.pla.annoyingvillagers.block.ShadowObsidianBlock;
import com.pla.annoyingvillagers.block.ShadowObsidianLongPillarBlock;
import com.pla.annoyingvillagers.block.ShadowObsidianMiddlePillarBlock;
import com.pla.annoyingvillagers.block.ShadowObsidianShortPillarBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AnnoyingVillagersModBlocks {
   public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, "annoyingvillagers");
   public static final RegistryObject<Block> ENCHANT_BED = REGISTRY.register("enchant_bed", EnchantBedBlock::new);
   public static final RegistryObject<Block> SHADOW_OBSIDIAN_SHORT_PILLAR = REGISTRY.register(
      "shadow_obsidian_short_pillar", ShadowObsidianShortPillarBlock::new
   );
   public static final RegistryObject<Block> SHADOW_OBSIDIAN_MIDDLE_PILLAR = REGISTRY.register(
      "shadow_obsidian_middle_pillar", ShadowObsidianMiddlePillarBlock::new
   );
   public static final RegistryObject<Block> SHADOW_OBSIDIAN_LONG_PILLAR = REGISTRY.register("shadow_obsidian_long_pillar", ShadowObsidianLongPillarBlock::new);
   public static final RegistryObject<Block> SHADOW_OBSIDIAN_BLOCK = REGISTRY.register("shadow_obsidian", ShadowObsidianBlock::new);
   public static final RegistryObject<Block> OBSIDIAN_BLOCK = REGISTRY.register("obsidian", ObsidianBlock::new);
   public static final RegistryObject<Block> CRYING_OBSIDIAN_BLOCK = REGISTRY.register("crying_obsidian", CryingObsidianBlock::new);
   public static final RegistryObject<EndFireBlock> END_FIRE = REGISTRY.register(
      "end_fire",
      () -> new EndFireBlock(
            Properties.m_284310_()
               .m_284180_(MapColor.f_283889_)
               .m_280170_()
               .m_60910_()
               .m_60966_()
               .m_60953_(s -> 15)
               .m_60918_(SoundType.f_56745_)
               .m_278166_(PushReaction.DESTROY)
         )
   );
}
