package com.pla.annoyingvillagers.clazz;

import com.pla.annoyingvillagers.blockentity.CryingObsidianBlockEntity;
import com.pla.annoyingvillagers.blockentity.ObsidianBlockEntity;
import com.pla.annoyingvillagers.blockentity.ShadowObsidianBlockEntity;
import com.pla.annoyingvillagers.blockentity.ShadowObsidianLongPillarBlockEntity;
import com.pla.annoyingvillagers.blockentity.ShadowObsidianMiddlePillarBlockEntity;
import com.pla.annoyingvillagers.blockentity.ShadowObsidianShortPillarBlockEntity;
import com.pla.annoyingvillagers.entity.BlueDemonEntity;
import com.pla.annoyingvillagers.entity.PlayerNpcEntity;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.util.HerobrineUtil;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class HerobrineObsidianBlock extends Block {
   public static final BooleanProperty FROM_PLAYER = BooleanProperty.m_61465_("from_player");
   public static final IntegerProperty REPLACE_BY_LIQUID = IntegerProperty.m_61631_("replace_by_liquid", 0, 2);
   private static final String NBT_LIFE = "life";
   private static final int LIFE_TICKS = 25;

   public HerobrineObsidianBlock(Properties pProperties) {
      super(pProperties);
      this.m_49959_((BlockState)((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(FROM_PLAYER, Boolean.FALSE)).m_61124_(REPLACE_BY_LIQUID, 0));
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{FROM_PLAYER, REPLACE_BY_LIQUID});
   }

   public BlockState m_5573_(@NotNull BlockPlaceContext blockPlaceContext) {
      BlockState base = super.m_5573_(blockPlaceContext);
      if (base == null) {
         base = this.m_49966_();
      }

      return (BlockState)base.m_61124_(FROM_PLAYER, blockPlaceContext.m_43723_() != null);
   }

   public int m_7753_(@NotNull BlockState blockstate, @NotNull BlockGetter blockgetter, @NotNull BlockPos blockpos) {
      return 15;
   }

   @NotNull
   public VoxelShape m_5909_(
      @NotNull BlockState blockstate, @NotNull BlockGetter blockgetter, @NotNull BlockPos blockpos, @NotNull CollisionContext collisioncontext
   ) {
      return Shapes.m_83040_();
   }

   @NotNull
   public List<ItemStack> m_49635_(@NotNull BlockState blockstate, @NotNull net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
      List<ItemStack> list = super.m_49635_(blockstate, builder);
      return !list.isEmpty() ? list : Collections.singletonList(new ItemStack(this, 1));
   }

   public void customTickSound(ServerLevel serverLevel, BlockPos blockPos) {
   }

   public void customPlaceSound(ServerLevel serverLevel, BlockPos blockPos) {
   }

   public void m_6807_(@NotNull BlockState blockstate, @NotNull Level level, @NotNull BlockPos blockPos, @NotNull BlockState oldState, boolean flag) {
      super.m_6807_(blockstate, level, blockPos, oldState, flag);
      if (level instanceof ServerLevel serverLevel) {
         this.customPlaceSound(serverLevel, blockPos);
         BlockEntity blockEntity = serverLevel.m_7702_(blockPos);
         if (blockEntity != null) {
            CompoundTag data = blockEntity.getPersistentData();
            if (!data.m_128441_("life")) {
               data.m_128405_("life", 25);
               blockEntity.m_6596_();
            }
         }
      }

      level.m_186460_(blockPos, this, 1);
   }

   public boolean conditionEveryTickEntityInside(Entity entity) {
      return true;
   }

   public void customHurtLogic(Entity entity, Entity owner, ServerLevel serverLevel, BlockPos blockPos) {
   }

   public void m_213897_(@NotNull BlockState blockState, @NotNull ServerLevel serverLevel, @NotNull BlockPos blockPos, @NotNull RandomSource randomSource) {
      super.m_213897_(blockState, serverLevel, blockPos, randomSource);
      this.customTickSound(serverLevel, blockPos);

      for (Entity entity : serverLevel.m_45933_(null, new AABB(blockPos))) {
         this.runEntityInsideLogic(blockState, serverLevel, blockPos, entity);
      }

      BlockEntity blockEntity = serverLevel.m_7702_(blockPos);
      if (blockEntity != null) {
         CompoundTag data = blockEntity.getPersistentData();
         int life = data.m_128441_("life") ? data.m_128451_("life") : 25;
         if (life > 0) {
            data.m_128405_("life", life - 1);
            blockEntity.m_6596_();
            serverLevel.m_186460_(blockPos, this, 1);
         } else {
            BlockState current = serverLevel.m_8055_(blockPos);
            life = 0;
            if (current.m_60734_() instanceof HerobrineObsidianBlock && current.m_61138_(REPLACE_BY_LIQUID)) {
               life = (Integer)current.m_61143_(REPLACE_BY_LIQUID);
            }
            BlockState replacement = switch (life) {
               case 1 -> Blocks.f_49990_.m_49966_();
               case 2 -> Blocks.f_49991_.m_49966_();
               default -> Blocks.f_50016_.m_49966_();
            };
            serverLevel.m_7731_(blockPos, replacement, 3);
            serverLevel.m_6263_(
               null,
               (double)blockPos.m_123341_(),
               (double)blockPos.m_123342_(),
               (double)blockPos.m_123343_(),
               (SoundEvent)AnnoyingVillagersModSounds.POP.get(),
               SoundSource.BLOCKS,
               0.5F,
               1.0F
            );
         }
      } else {
         serverLevel.m_186460_(blockPos, this, 1);
      }
   }

   private void runEntityInsideLogic(@NotNull BlockState blockState, @NotNull ServerLevel serverLevel, @NotNull BlockPos blockPos, @NotNull Entity entity) {
      if (this.conditionEveryTickEntityInside(entity)) {
         boolean fromPlayer = blockState.m_60734_() instanceof HerobrineObsidianBlock
            && blockState.m_61138_(FROM_PLAYER)
            && (Boolean)blockState.m_61143_(FROM_PLAYER);
         if (fromPlayer || !HerobrineUtil.isHerobrineFaction(entity)) {
            if (!(entity instanceof Player) || !fromPlayer || serverLevel.m_7654_().m_129799_()) {
               Entity owner = null;
               BlockEntity blockEntity = serverLevel.m_7702_(blockPos);
               if (blockEntity instanceof ObsidianBlockEntity obsidianBlockEntity) {
                  UUID ownerUuid = obsidianBlockEntity.getOwner();
                  if (ownerUuid != null) {
                     if (ownerUuid.equals(entity.m_20148_())) {
                        return;
                     }

                     owner = (Entity)(fromPlayer ? serverLevel.m_46003_(ownerUuid) : serverLevel.m_8791_(ownerUuid));
                  }
               } else if (blockEntity instanceof ShadowObsidianBlockEntity shadowObsidianBlockEntity) {
                  UUID ownerUuid = shadowObsidianBlockEntity.getOwner();
                  if (ownerUuid != null) {
                     if (ownerUuid.equals(entity.m_20148_())) {
                        return;
                     }

                     owner = (Entity)(fromPlayer ? serverLevel.m_46003_(ownerUuid) : serverLevel.m_8791_(ownerUuid));
                  }
               } else if (blockEntity instanceof CryingObsidianBlockEntity cryingObsidianBlockEntity) {
                  UUID ownerUuid = cryingObsidianBlockEntity.getOwner();
                  if (ownerUuid != null) {
                     if (ownerUuid.equals(entity.m_20148_())) {
                        return;
                     }

                     owner = (Entity)(fromPlayer ? serverLevel.m_46003_(ownerUuid) : serverLevel.m_8791_(ownerUuid));
                  }
               } else if (blockEntity instanceof ShadowObsidianShortPillarBlockEntity shadowObsidianShortPillarBlockEntity) {
                  UUID ownerUuid = shadowObsidianShortPillarBlockEntity.getOwner();
                  if (ownerUuid != null) {
                     if (ownerUuid.equals(entity.m_20148_())) {
                        return;
                     }

                     owner = (Entity)(fromPlayer ? serverLevel.m_46003_(ownerUuid) : serverLevel.m_8791_(ownerUuid));
                  }
               } else if (blockEntity instanceof ShadowObsidianMiddlePillarBlockEntity shadowObsidianMiddlePillarBlockEntity) {
                  UUID ownerUuid = shadowObsidianMiddlePillarBlockEntity.getOwner();
                  if (ownerUuid != null) {
                     if (ownerUuid.equals(entity.m_20148_())) {
                        return;
                     }

                     owner = (Entity)(fromPlayer ? serverLevel.m_46003_(ownerUuid) : serverLevel.m_8791_(ownerUuid));
                  }
               } else if (blockEntity instanceof ShadowObsidianLongPillarBlockEntity shadowObsidianLongPillarBlockEntity) {
                  UUID ownerUuid = shadowObsidianLongPillarBlockEntity.getOwner();
                  if (ownerUuid != null) {
                     if (ownerUuid.equals(entity.m_20148_())) {
                        return;
                     }

                     owner = (Entity)(fromPlayer ? serverLevel.m_46003_(ownerUuid) : serverLevel.m_8791_(ownerUuid));
                  }
               }

               if (entity instanceof Player player) {
                  CompoundTag data = player.getPersistentData();
                  if (data.m_128441_("StunEscapeCooldown")) {
                     int coolDownValue = data.m_128451_("StunEscapeCooldown");
                     if (coolDownValue < 5) {
                        data.m_128405_("StunEscapeCooldown", coolDownValue + 1);
                     }
                  }
               }

               if (entity instanceof PlayerNpcEntity playerNpcEntity) {
                  int currentValue = playerNpcEntity.getStunEscapeCooldown();
                  if (currentValue < 100) {
                     playerNpcEntity.setStunEscapeCooldown(currentValue + 20);
                  }
               }

               if (entity instanceof HerobrineMob herobrineMob) {
                  int currentValue = herobrineMob.getStunEscapeCooldown();
                  if (currentValue < 100) {
                     herobrineMob.setStunEscapeCooldown(currentValue + 20);
                  }
               }

               if (entity instanceof AVNpc avNpc) {
                  int currentValue = avNpc.getStunEscapeCooldown();
                  if (currentValue < 100) {
                     avNpc.setStunEscapeCooldown(currentValue + 20);
                  }
               }

               if (entity instanceof BlueDemonEntity blueDemonEntity) {
                  int currentValue = blueDemonEntity.getStunEscapeCooldown();
                  if (currentValue < 100) {
                     blueDemonEntity.setStunEscapeCooldown(currentValue + 20);
                  }
               }

               this.customHurtLogic(entity, owner, serverLevel, blockPos);
            }
         }
      }
   }

   public void m_6402_(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, LivingEntity placer, @NotNull ItemStack stack) {
      super.m_6402_(level, pos, state, placer, stack);
      if (level instanceof ServerLevel serverLevel) {
         BlockEntity blockEntity = level.m_7702_(pos);
         if (blockEntity != null) {
            if (blockEntity instanceof ObsidianBlockEntity obsidianBlockEntity) {
               obsidianBlockEntity.setOwner(placer.m_20148_());
            } else if (blockEntity instanceof ShadowObsidianBlockEntity shadowObsidianBlockEntity) {
               shadowObsidianBlockEntity.setOwner(placer.m_20148_());
            } else if (blockEntity instanceof CryingObsidianBlockEntity cryingObsidianBlockEntity) {
               cryingObsidianBlockEntity.setOwner(placer.m_20148_());
            } else if (blockEntity instanceof ShadowObsidianShortPillarBlockEntity shadowObsidianShortPillarBlockEntity) {
               shadowObsidianShortPillarBlockEntity.setOwner(placer.m_20148_());
            } else if (blockEntity instanceof ShadowObsidianMiddlePillarBlockEntity shadowObsidianMiddlePillarBlockEntity) {
               shadowObsidianMiddlePillarBlockEntity.setOwner(placer.m_20148_());
            } else if (blockEntity instanceof ShadowObsidianLongPillarBlockEntity shadowObsidianLongPillarBlockEntity) {
               shadowObsidianLongPillarBlockEntity.setOwner(placer.m_20148_());
            }

            CompoundTag data = blockEntity.getPersistentData();
            if (!data.m_128441_("life")) {
               data.m_128405_("life", 25);
            }

            blockEntity.m_6596_();
            serverLevel.m_7260_(pos, state, state, 3);
         }
      }
   }
}
