package com.pla.annoyingvillagers.block;

import com.pla.annoyingvillagers.blockentity.ShadowObsidianLongPillarBlockEntity;
import com.pla.annoyingvillagers.clazz.HerobrineObsidianBlock;
import com.pla.annoyingvillagers.gameasset.AVSkills;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.skill.ShadowObsidianPillarSkill;
import com.pla.annoyingvillagers.skill.ShadowObsidianSwordSkill;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.OffsetType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.StunType;

public class ShadowObsidianLongPillarBlock extends HerobrineObsidianBlock implements EntityBlock {
   public static final DirectionProperty FACING = BlockStateProperties.f_61374_;

   public ShadowObsidianLongPillarBlock() {
      super(
         Properties.m_284310_()
            .m_60918_(SoundType.f_56742_)
            .m_222979_(OffsetType.XYZ)
            .m_60913_(3.0F, 50.0F)
            .m_60955_()
            .m_60982_((blockstate, blockgetter, blockpos) -> true)
            .m_60991_((blockstate, blockgetter, blockpos) -> true)
            .m_60924_((blockstate, blockgetter, blockpos) -> false)
            .m_60988_()
      );
      super.m_49959_((BlockState)((BlockState)super.m_49965_().m_61090_()).m_61124_(FACING, Direction.NORTH));
   }

   @Override
   protected void m_7926_(Builder<Block, BlockState> builder) {
      super.m_7926_(builder);
      builder.m_61104_(new Property[]{FACING});
   }

   public BlockState m_6843_(BlockState state, Rotation rotation) {
      return (BlockState)state.m_61124_(FACING, rotation.m_55954_((Direction)state.m_61143_(FACING)));
   }

   public BlockState m_6943_(BlockState state, Mirror mirror) {
      return state.m_60717_(mirror.m_54846_((Direction)state.m_61143_(FACING)));
   }

   @Override
   public BlockState m_5573_(@NotNull BlockPlaceContext blockPlaceContext) {
      BlockState state = super.m_5573_(blockPlaceContext);
      if (state == null) {
         state = this.m_49966_();
      }

      return (BlockState)state.m_61124_(FACING, blockPlaceContext.m_8125_().m_122424_());
   }

   @NotNull
   public VoxelShape m_5940_(
      @NotNull BlockState blockstate, @NotNull BlockGetter blockgetter, @NotNull BlockPos blockpos, @NotNull CollisionContext collisioncontext
   ) {
      return Shapes.m_83124_(
         m_49796_(6.0, 0.0, 12.0, 10.0, 16.0, 16.0), new VoxelShape[]{m_49796_(6.0, 16.0, 12.0, 10.0, 32.0, 16.0), m_49796_(6.0, -16.0, 12.0, 10.0, 0.0, 16.0)}
      );
   }

   @Override
   public void customPlaceSound(ServerLevel serverLevel, BlockPos blockPos) {
      super.customPlaceSound(serverLevel, blockPos);
      serverLevel.m_6263_(
         null,
         (double)blockPos.m_123341_(),
         (double)blockPos.m_123342_(),
         (double)blockPos.m_123343_(),
         (SoundEvent)AnnoyingVillagersModSounds.OB_PLACE.get(),
         SoundSource.BLOCKS,
         0.5F,
         1.0F
      );
   }

   @Override
   public void customTickSound(ServerLevel serverLevel, BlockPos blockPos) {
      super.customTickSound(serverLevel, blockPos);
   }

   public void increaseSkillPoint(Entity entity, float value) {
      if (entity instanceof Player pEntity) {
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(pEntity, PlayerPatch.class);
         if (playerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillContainer skillContainer = null;
            Object skill = null;
            if (serverPlayerPatch.getSkill(AVSkills.SHADOW_OBSIDIAN_PILLAR) != null) {
               skillContainer = serverPlayerPatch.getSkill(AVSkills.SHADOW_OBSIDIAN_PILLAR);
               if (skillContainer == null) {
                  return;
               }

               skill = (ShadowObsidianPillarSkill)skillContainer.getSkill();
            } else if (serverPlayerPatch.getSkill(AVSkills.SHADOW_OBSIDIAN_SWORD) != null) {
               skillContainer = serverPlayerPatch.getSkill(AVSkills.SHADOW_OBSIDIAN_SWORD);
               if (skillContainer == null) {
                  return;
               }

               skill = (ShadowObsidianSwordSkill)skillContainer.getSkill();
            }

            if (skillContainer != null && skill != null) {
               float currentResource = skillContainer.getResource();
               float neededResource = skillContainer.getNeededResource();
               float addResource = Math.min(value, neededResource);
               skill.setConsumptionSynchronize(skillContainer, currentResource + addResource);
            }
         }
      }
   }

   @Override
   public void customHurtLogic(Entity entity, Entity owner, ServerLevel serverLevel, BlockPos blockPos) {
      super.customHurtLogic(entity, owner, serverLevel, blockPos);
      serverLevel.m_8767_(
         (SimpleParticleType)AnnoyingVillagersModParticleTypes.SPARK.get(),
         entity.m_20185_(),
         entity.m_20186_() + 1.5,
         entity.m_20189_() + 0.8,
         5,
         0.0,
         0.0,
         0.0,
         0.1
      );
      if (entity instanceof Mob mob) {
         LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
         mob.m_7292_(new MobEffectInstance(MobEffects.f_19597_, 2, 8, false, false));
         if (livingEntityPatch != null && !livingEntityPatch.isStunned()) {
            livingEntityPatch.applyStun(StunType.SHORT, 1.0F);
         }
      }

      if (owner != null) {
         if (owner instanceof Player player) {
            entity.m_6469_(entity.m_9236_().m_269111_().m_269075_(player), 1.0F);
            this.increaseSkillPoint(player, 0.2F);
         } else {
            entity.m_6469_(entity.m_9236_().m_269111_().m_269333_((LivingEntity)owner), 1.0F);
         }
      } else {
         entity.m_6469_(entity.m_9236_().m_269111_().m_269264_(), 1.0F);
      }

      entity.m_20256_(new Vec3(0.0, 0.0, 0.0));
      if (Math.random() <= 0.2 && entity.f_19797_ % 10 == 0 && entity instanceof LivingEntity livingEntity) {
         float strength = 1.0F;
         double dx = (double)blockPos.m_123341_() - entity.m_20185_();
         double dz = (double)blockPos.m_123343_() - entity.m_20189_();
         livingEntity.m_147240_((double)strength, dx, dz);
      }
   }

   @Nullable
   public BlockEntity m_142194_(@NotNull BlockPos pPos, @NotNull BlockState pState) {
      return new ShadowObsidianLongPillarBlockEntity(pPos, pState);
   }
}
