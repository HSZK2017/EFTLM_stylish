package com.pla.annoyingvillagers.init;

import com.pla.annoyingvillagers.blockentity.CryingObsidianBlockEntity;
import com.pla.annoyingvillagers.blockentity.ObsidianBlockEntity;
import com.pla.annoyingvillagers.blockentity.ShadowObsidianBlockEntity;
import com.pla.annoyingvillagers.blockentity.ShadowObsidianLongPillarBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class AnnoyingVillagersModBlockEntities {
   public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "annoyingvillagers");
   public static final RegistryObject<BlockEntityType<ShadowObsidianLongPillarBlockEntity>> SHADOW_OBSIDIAN_SHORT_PILLAR = REGISTRY.register(
      "shadow_obsidian_short_pillar",
      () -> Builder.m_155273_(ShadowObsidianLongPillarBlockEntity::new, new Block[]{(Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_SHORT_PILLAR.get()})
            .m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<ShadowObsidianLongPillarBlockEntity>> SHADOW_OBSIDIAN_MIDDLE_PILLAR = REGISTRY.register(
      "shadow_obsidian_middle_pillar",
      () -> Builder.m_155273_(ShadowObsidianLongPillarBlockEntity::new, new Block[]{(Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_MIDDLE_PILLAR.get()})
            .m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<ShadowObsidianLongPillarBlockEntity>> SHADOW_OBSIDIAN_LONG_PILLAR = REGISTRY.register(
      "shadow_obsidian_long_pillar",
      () -> Builder.m_155273_(ShadowObsidianLongPillarBlockEntity::new, new Block[]{(Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_LONG_PILLAR.get()})
            .m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<ShadowObsidianBlockEntity>> SHADOW_OBSIDIAN_BLOCK = REGISTRY.register(
      "shadow_obsidian",
      () -> Builder.m_155273_(ShadowObsidianBlockEntity::new, new Block[]{(Block)AnnoyingVillagersModBlocks.SHADOW_OBSIDIAN_BLOCK.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<ObsidianBlockEntity>> OBSIDIAN_BLOCK = REGISTRY.register(
      "obsidian", () -> Builder.m_155273_(ObsidianBlockEntity::new, new Block[]{(Block)AnnoyingVillagersModBlocks.OBSIDIAN_BLOCK.get()}).m_58966_(null)
   );
   public static final RegistryObject<BlockEntityType<CryingObsidianBlockEntity>> CRYING_OBSIDIAN_BLOCK = REGISTRY.register(
      "crying_obsidian",
      () -> Builder.m_155273_(CryingObsidianBlockEntity::new, new Block[]{(Block)AnnoyingVillagersModBlocks.CRYING_OBSIDIAN_BLOCK.get()}).m_58966_(null)
   );

   private AnnoyingVillagersModBlockEntities() {
   }

   public static void register(IEventBus modEventBus) {
      REGISTRY.register(modEventBus);
   }
}
