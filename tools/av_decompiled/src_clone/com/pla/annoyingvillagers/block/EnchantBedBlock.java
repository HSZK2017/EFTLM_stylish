package com.pla.annoyingvillagers.block;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModMobEffects;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class EnchantBedBlock extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.f_54117_;

   public EnchantBedBlock() {
      super(
         Properties.m_284310_()
            .m_60918_(SoundType.f_56736_)
            .m_60913_(1.25F, 10.0F)
            .m_60953_(blockstate -> 2)
            .m_60967_(5.0F)
            .m_60955_()
            .m_60982_((blockstate, blockgetter, blockpos) -> true)
            .m_60991_((blockstate, blockgetter, blockpos) -> true)
            .m_60924_((blockstate, blockgetter, blockpos) -> false)
      );
      this.m_49959_((BlockState)((BlockState)this.f_49792_.m_61090_()).m_61124_(FACING, Direction.NORTH));
   }

   public void m_5871_(@NotNull ItemStack itemstack, BlockGetter blockgetter, @NotNull List<Component> list, @NotNull TooltipFlag tooltipflag) {
      super.m_5871_(itemstack, blockgetter, list, tooltipflag);
      list.add(Component.m_237115_("tooltip.annoyingvillagers.enchanted_bed"));
   }

   public boolean m_7420_(@NotNull BlockState blockstate, @NotNull BlockGetter blockgetter, @NotNull BlockPos blockpos) {
      return true;
   }

   public int m_7753_(@NotNull BlockState blockstate, @NotNull BlockGetter blockgetter, @NotNull BlockPos blockpos) {
      return 0;
   }

   @NotNull
   public VoxelShape m_5909_(
      @NotNull BlockState blockstate, @NotNull BlockGetter blockgetter, @NotNull BlockPos blockpos, @NotNull CollisionContext collisioncontext
   ) {
      return Shapes.m_83040_();
   }

   @NotNull
   public VoxelShape m_5940_(BlockState blockstate, @NotNull BlockGetter blockgetter, @NotNull BlockPos blockpos, @NotNull CollisionContext collisioncontext) {
      return switch ((Direction)blockstate.m_61143_(FACING)) {
         case NORTH -> Shapes.m_83124_(
         m_49796_(0.0, 0.0, 29.0, 3.0, 3.0, 32.0),
         new VoxelShape[]{
            m_49796_(13.0, 0.0, 29.0, 16.0, 3.0, 32.0),
            m_49796_(0.0, 3.0, 16.0, 16.0, 9.0, 32.0),
            m_49796_(0.0, 3.0, 0.0, 16.0, 9.0, 16.0),
            m_49796_(13.0, 0.0, 0.0, 16.0, 3.0, 3.0),
            m_49796_(0.0, 0.0, 0.0, 3.0, 3.0, 3.0)
         }
      );
         case EAST -> Shapes.m_83124_(
         m_49796_(-16.0, 0.0, 0.0, -13.0, 3.0, 3.0),
         new VoxelShape[]{
            m_49796_(-16.0, 0.0, 13.0, -13.0, 3.0, 16.0),
            m_49796_(-16.0, 3.0, 0.0, 0.0, 9.0, 16.0),
            m_49796_(0.0, 3.0, 0.0, 16.0, 9.0, 16.0),
            m_49796_(13.0, 0.0, 13.0, 16.0, 3.0, 16.0),
            m_49796_(13.0, 0.0, 0.0, 16.0, 3.0, 3.0)
         }
      );
         case WEST -> Shapes.m_83124_(
         m_49796_(29.0, 0.0, 13.0, 32.0, 3.0, 16.0),
         new VoxelShape[]{
            m_49796_(29.0, 0.0, 0.0, 32.0, 3.0, 3.0),
            m_49796_(16.0, 3.0, 0.0, 32.0, 9.0, 16.0),
            m_49796_(0.0, 3.0, 0.0, 16.0, 9.0, 16.0),
            m_49796_(0.0, 0.0, 0.0, 3.0, 3.0, 3.0),
            m_49796_(0.0, 0.0, 13.0, 3.0, 3.0, 16.0)
         }
      );
         default -> Shapes.m_83124_(
         m_49796_(13.0, 0.0, -16.0, 16.0, 3.0, -13.0),
         new VoxelShape[]{
            m_49796_(0.0, 0.0, -16.0, 3.0, 3.0, -13.0),
            m_49796_(0.0, 3.0, -16.0, 16.0, 9.0, 0.0),
            m_49796_(0.0, 3.0, 0.0, 16.0, 9.0, 16.0),
            m_49796_(0.0, 0.0, 13.0, 3.0, 3.0, 16.0),
            m_49796_(13.0, 0.0, 13.0, 16.0, 3.0, 16.0)
         }
      );
      };
   }

   protected void m_7926_(Builder<Block, BlockState> builder) {
      builder.m_61104_(new Property[]{FACING});
   }

   public BlockState m_5573_(BlockPlaceContext blockplacecontext) {
      return (BlockState)this.m_49966_().m_61124_(FACING, blockplacecontext.m_8125_().m_122424_());
   }

   @NotNull
   public BlockState m_6843_(BlockState blockstate, Rotation rotation) {
      return (BlockState)blockstate.m_61124_(FACING, rotation.m_55954_((Direction)blockstate.m_61143_(FACING)));
   }

   @NotNull
   public BlockState m_6943_(BlockState blockstate, Mirror mirror) {
      return blockstate.m_60717_(mirror.m_54846_((Direction)blockstate.m_61143_(FACING)));
   }

   @NotNull
   public List<ItemStack> m_49635_(@NotNull BlockState blockstate, @NotNull net.minecraft.world.level.storage.loot.LootParams.Builder pParams) {
      List<ItemStack> list = super.m_49635_(blockstate, pParams);
      return !list.isEmpty() ? list : Collections.singletonList(new ItemStack((ItemLike)AnnoyingVillagersModItems.ENCHANT_BED_ITEM.get()));
   }

   @NotNull
   public InteractionResult m_6227_(
      @NotNull BlockState blockstate,
      @NotNull Level level,
      @NotNull BlockPos blockpos,
      @NotNull Player player,
      @NotNull InteractionHand interactionhand,
      @NotNull BlockHitResult blockHitResult
   ) {
      super.m_6227_(blockstate, level, blockpos, player, interactionhand, blockHitResult);
      if (player.m_21023_((MobEffect)AnnoyingVillagersModMobEffects.ENCHANT_BED_EFFECT.get()) && !player.m_9236_().m_5776_()) {
         player.m_5661_(Component.m_237113_("You have already used the Enchant Bed!"), true);
      }

      if (player instanceof ServerPlayer serverPlayer) {
         if (player.f_36078_ >= 2) {
            player.m_7292_(new MobEffectInstance((MobEffect)AnnoyingVillagersModMobEffects.ENCHANT_BED_EFFECT.get(), -1, 0, false, false));
            player.m_5661_(Component.m_237113_("You used the Enchant Bed once. Experience level -1."), true);
            player.m_5661_(Component.m_237113_("Respawn point has been reset."), false);
            player.m_6749_(-1);
            serverPlayer.m_9158_(
               player.m_9236_().m_46472_(),
               new BlockPos((int)blockHitResult.m_82450_().f_82479_, (int)blockHitResult.m_82450_().f_82480_, (int)blockHitResult.m_82450_().f_82481_),
               serverPlayer.m_146908_(),
               true,
               false
            );
         } else {
            player.m_5661_(Component.m_237113_("Your experience level is too low. You must be above level 2 to use this!"), true);
         }
      }

      return InteractionResult.SUCCESS;
   }
}
