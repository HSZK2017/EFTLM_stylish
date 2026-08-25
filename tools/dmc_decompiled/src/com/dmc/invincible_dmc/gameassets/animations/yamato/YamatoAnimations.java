package com.dmc.invincible_dmc.gameassets.animations.yamato;

import com.dmc.invincible_dmc.InvincibleMod_DMC;
import com.dmc.invincible_dmc.api.animation.types.yamato.JudgementCutEndAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoAttackAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoDodgeAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoExecutionAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoIdleSelectiveAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoLivingAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoMovementAnimation;
import com.dmc.invincible_dmc.api.animation.types.yamato.YamatoMovingLivingAnimation;
import com.dmc.invincible_dmc.api.collider.ScalableMultiOBBCollider;
import com.dmc.invincible_dmc.api.collider.ScalableOBBCollider;
import com.dmc.invincible_dmc.api.collider.YamatoExecutionLineCollider;
import com.dmc.invincible_dmc.event.SheathInEvents;
import com.guhao.vix.camera.CameraAnimationFix;
import com.guhao.vix.util.OjangUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.AnimationManager.AnimationBuilder;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.GuardAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@EventBusSubscriber(
   modid = "invincible_dmc",
   bus = Bus.MOD
)
public class YamatoAnimations {
   public static final TagKey<DamageType> SLOW_PERSISTENT = InvincibleMod_DMC.createDamageType("slow_persistent");
   public static final TagKey<DamageType> CRAZY_COMBO = InvincibleMod_DMC.createDamageType("crazy_combo");
   public static final TagKey<DamageType> RAPID_SLASH = InvincibleMod_DMC.createDamageType("rapid_slash");
   public static final TagKey<DamageType> CRAZY_COMBO_FINISH = InvincibleMod_DMC.createDamageType("crazy_combo_finish");
   public static final Collider SHEATH_COLLIDER = new ScalableMultiOBBCollider(3, 0.4, 0.4, 1.15, 0.0, 0.0, 0.75);
   public static final Collider BLADE_COLLIDER = new ScalableMultiOBBCollider(3, 0.4, 0.4, 1.25, 0.0, 0.0, -0.75);
   public static final YamatoExecutionLineCollider EXECUTION_LINE_COLLIDER = new YamatoExecutionLineCollider(
      5, new Vec3(0.0, 0.0, -0.25), new Vec3(0.0, 0.0, -2.0)
   );
   public static final Collider BLADE_COLLIDER_EX = new ScalableMultiOBBCollider(3, 0.4, 0.4, 2.15, 0.0, 0.0, -1.2);
   public static final Collider BLADE_COLLIDER_SDT = new ScalableMultiOBBCollider(3, 1.0, 1.0, 4.0, 0.0, 0.0, -3.0);
   public static final double JUDGEMENT_CUT_END_HALF_X = 15.0;
   public static final double JUDGEMENT_CUT_END_HALF_Y = 20.0;
   public static final double JUDGEMENT_CUT_END_HALF_Z = 15.0;
   public static final double JUDGEMENT_CUT_END_CENTER_Y = 1.0;
   public static final Collider JUDGEMENT_CUT_END = new OBBCollider(15.0, 20.0, 15.0, 0.0, 1.0, 0.0);
   public static final Collider RAPIDSLASH = new MultiOBBCollider(3, 1.5, 1.65, 1.75, 0.0, 1.0, -0.7);
   public static final Collider RISINGSTAR = new MultiOBBCollider(3, 1.5, 2.0, 1.9, 0.0, 1.0, -0.95);
   public static final Collider RAPIDSLASH_END = new ScalableOBBCollider(3.0, 2.0, 3.0, 0.0, 1.0, 0.0);
   public static final Collider COMBO_B = new OBBCollider(2.0, 2.2, 3.5, 0.0, 1.2, -1.5);
   public static final Collider AERIALRAVE = new ScalableMultiOBBCollider(3, 0.95, 0.95, 1.5, 0.0, 0.0, -1.35);
   public static final Collider AERIALRAVE_B = new ScalableMultiOBBCollider(3, 1.0, 1.0, 2.3, 0.0, 0.0, -1.35);
   public static final Collider VOID_SLASH = new OBBCollider(3.5, 1.65, 3.7, 0.0, 1.2, -2.1);
   public static final Collider AERIALCLEAVE = new ScalableMultiOBBCollider(5, 1.5, 8.0, 2.25, 0.0, -2.0, -1.5);
   public static final Collider EXECUTION_DASH = new MultiOBBCollider(5, 0.8F, 1.2, 2.0, 0.0, 1.2, -1.25);
   public static final Collider EXECUTION_FINISHER = new MultiOBBCollider(5, 1.1F, 1.3, 2.5, 0.0, 1.2, -1.2);
   public static final Collider COMBO_C = new OBBCollider(2.0, 1.9, 3.0, 0.0, 1.7, -1.0);
   public static final Collider COMBO_C_END = new ScalableMultiOBBCollider(3, 2.7, 2.8, 2.7, 0.0, 1.0, -0.5);
   public static final Collider SDT_A4 = new OBBCollider(2.0, 3.0, 3.5, 0.0, 1.5, -2.0);
   public static final Collider SDT_A5 = new OBBCollider(2.5, 5.0, 5.5, 0.0, 1.5, -2.0);
   public static final Function<DamageSource, ResultType> INVINCIBLE_SOURCE_VALIDATOR = damagesource -> damagesource.m_7639_() != null
            && !damagesource.m_269533_(DamageTypeTags.f_268738_)
         ? ResultType.MISSED
         : ResultType.SUCCESS;
   public static final TagKey<DamageType> JUDGEMENT_CUT = InvincibleMod_DMC.createDamageType("judgement_cut");
   static final String pre_fix_living = "biped/yamato/living/yamato_";
   static final String pre_fix_doppelganger = "biped/yamato/doppelganger/";
   static final String pre_fix_provocation = "biped/yamato/provocation/yamato_";
   static final String pre_fix_dodge = "biped/yamato/dodge/yamato_dodge_";
   static final String pre_fix_attack = "biped/yamato/attack/yamato_";
   public static AnimationAccessor<YamatoLivingAnimation> TEST;
   public static AnimationAccessor<YamatoIdleSelectiveAnimation> YAMATO_IDLE;
   public static AnimationAccessor<YamatoMovingLivingAnimation> YAMATO_IDLE_2_START;
   public static AnimationAccessor<YamatoLivingAnimation> YAMATO_IDLE_2;
   public static AnimationAccessor<YamatoLivingAnimation> YAMATO_IDLE_3;
   public static AnimationAccessor<YamatoLivingAnimation> YAMATO_GUARD;
   public static AnimationAccessor<YamatoLivingAnimation> YAMATO_BLOCK_RANGE;
   public static AnimationAccessor<GuardAnimation> YAMATO_GUARD_HIT;
   public static AnimationAccessor<GuardAnimation> YAMATO_PARRY_LEFT;
   public static AnimationAccessor<GuardAnimation> YAMATO_PARRY_RIGHT;
   public static AnimationAccessor<YamatoLivingAnimation> YAMATO_SIN_DEVIL_TRIGGER;
   public static AnimationAccessor<YamatoLivingAnimation> YAMATO_SIN_DEVIL_TRIGGER_BACK;
   public static AnimationAccessor<YamatoLivingAnimation> YAMATO_KNEEL;
   public static AnimationAccessor<YamatoLivingAnimation> YAMATO_SNEAK;
   public static AnimationAccessor<YamatoLivingAnimation> YAMATO_JUMP;
   public static AnimationAccessor<YamatoMovementAnimation> YAMATO_WALK;
   public static AnimationAccessor<YamatoMovementAnimation> YAMATO_RUN;
   public static AnimationAccessor<ActionAnimation> YAMATO_ENEMY_STEP_FORWARD;
   public static AnimationAccessor<ActionAnimation> YAMATO_ENEMY_STEP_BACKWARD;
   public static AnimationAccessor<ActionAnimation> YAMATO_SUMMON_DOPPELGANGER_GROUND;
   public static AnimationAccessor<ActionAnimation> YAMATO_SUMMON_DOPPELGANGER_AIR;
   public static AnimationAccessor<ActionAnimation> YAMATO_PROVOCATION_A;
   public static AnimationAccessor<ActionAnimation> YAMATO_PROVOCATION_A_AERIAL;
   public static AnimationAccessor<ActionAnimation> YAMATO_PROVOCATION_B;
   public static AnimationAccessor<ActionAnimation> YAMATO_PROVOCATION_SPINE_BLADE;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_PROVOCATION_B_AERIAL;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_PROVOCATION_C;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_PROVOCATION_D;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_PROVOCATION_PORTAL;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_EXECUTION_ALL;
   public static AnimationAccessor<YamatoExecutionAnimation> YAMATO_EXECUTION_DASH;
   public static AnimationAccessor<YamatoExecutionAnimation> YAMATO_EXECUTION_END;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_COMBO_A_1;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_COMBO_A_2;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_COMBO_A_3;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_DODGE_COUNTER;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_STRIKE;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_COMBO_A_4;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_COMBO_A_4_SDT;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_COMBO_A_5_SDT;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_COMBO_B_1;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_COMBO_B_2_SDT;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_COMBO_C_START;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_COMBO_C_LOOP;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_COMBO_C_END;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_COMBO_C_END_THROUGH;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_RAPIDSLASH;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_RAPIDSLASH_RE;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_RAPIDSLASH_AIR;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_RISINGSTAR;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_UPPERSLASH_1;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_UPPERSLASH_2;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_VOID_SLASH;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_AERIALRAVE_COMBO_A_1;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_AERIALRAVE_COMBO_A_2;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_AERIALRAVE_COMBO_A_3;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_AERIALRAVE_COMBO_B_1;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_AERIALRAVE_COMBO_B_2;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_AERIALCLEAVE;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_AERIALCLEAVE_FAST;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_AERIALCLEAVE_DASH;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_JUDGEMENT_CUT_GROUND;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_JUDGEMENT_CUT_GROUND_FS;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_JUDGEMENT_CUT_AIR;
   public static AnimationAccessor<YamatoAttackAnimation> YAMATO_JUDGEMENT_CUT_AIR_FS;
   public static AnimationAccessor<JudgementCutEndAnimation> YAMATO_JUDGEMENT_CUT_END_DMC3;
   public static AnimationAccessor<JudgementCutEndAnimation> YAMATO_JUDGEMENT_CUT_END;
   public static AnimationAccessor<JudgementCutEndAnimation> YAMATO_JUDGEMENT_CUT_END_INSTANT;
   public static AnimationAccessor<YamatoDodgeAnimation> YAMATO_STEP_F;
   public static AnimationAccessor<YamatoDodgeAnimation> YAMATO_STEP_B;
   public static AnimationAccessor<YamatoDodgeAnimation> YAMATO_STEP_L;
   public static AnimationAccessor<YamatoDodgeAnimation> YAMATO_STEP_L_SHORT;
   public static AnimationAccessor<YamatoDodgeAnimation> YAMATO_STEP_L_COMBAT;
   public static AnimationAccessor<YamatoDodgeAnimation> YAMATO_STEP_R;
   public static AnimationAccessor<YamatoDodgeAnimation> YAMATO_STEP_R_SHORT;
   public static AnimationAccessor<YamatoDodgeAnimation> YAMATO_STEP_R_COMBAT;
   public static AnimationAccessor<YamatoDodgeAnimation> YAMATO_STEP_D;
   public static AnimationAccessor<YamatoDodgeAnimation> YAMATO_STEP_U;
   public static CameraAnimationFix EXECUTION;
   public static CameraAnimationFix JUDGEMENT_CUT_END_CAMERA;
   public static CameraAnimationFix JUDGEMENT_CUT_END_INSTANT_CAMERA;

   public static AABB getJudgementCutEndArea(LivingEntity owner) {
      Vec3 center = owner.m_20182_().m_82520_(0.0, 1.0, 0.0);
      return new AABB(
         center.f_82479_ - 15.0, center.f_82480_ - 20.0, center.f_82481_ - 15.0, center.f_82479_ + 15.0, center.f_82480_ + 20.0, center.f_82481_ + 15.0
      );
   }

   public static void LoadCamAnims() {
      EXECUTION = CameraAnimationFix.load(OjangUtils.newRL("invincible_dmc", "camera_animation/execution_test.json"));
      JUDGEMENT_CUT_END_CAMERA = CameraAnimationFix.load(OjangUtils.newRL("invincible_dmc", "camera_animation/judgement_cut_end.json"));
      JUDGEMENT_CUT_END_INSTANT_CAMERA = CameraAnimationFix.load(OjangUtils.newRL("invincible_dmc", "camera_animation/judgement_cut_end_instant.json"));
   }

   public static void build(AnimationBuilder builder) {
      YamatoLivingAnimationBuilder.build(builder);
      YamatoDoppelgangerAnimationBuilder.build(builder);
      YamatoProvocationAnimationBuilder.build(builder);
      YamatoExecutionAnimationBuilder.build(builder);
      YamatoGroundComboAAnimationBuilder.build(builder);
      YamatoGroundComboBAnimationBuilder.build(builder);
      YamatoGroundComboCAnimationBuilder.build(builder);
      YamatoSpecialAttackAnimationBuilder.build(builder);
      YamatoAerialComboAnimationBuilder.build(builder);
      YamatoJudgementCutAnimationBuilder.build(builder);
      YamatoDodgeAnimationBuilder.build(builder);
   }

   static void scanTntInFront(LivingEntityPatch<?> patch) {
      if (!patch.isLogicalClient()) {
         LivingEntity player = (LivingEntity)patch.getOriginal();
         Level level = player.m_9236_();
         Vec3 eyePos = player.m_146892_();
         Vec3 look = player.m_20154_();
         Vec3 center = eyePos.m_82549_(look.m_82490_(1.5));
         int minX = (int)Math.floor(center.f_82479_ - 2.0);
         int minY = (int)Math.floor(center.f_82480_ - 1.0);
         int minZ = (int)Math.floor(center.f_82481_ - 2.0);
         int maxX = (int)Math.ceil(center.f_82479_ + 2.0);
         int maxY = (int)Math.ceil(center.f_82480_ + 1.0);
         int maxZ = (int)Math.ceil(center.f_82481_ + 2.0);
         List<BlockPos> tntPositions = new ArrayList<>();

         for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
               for (int z = minZ; z <= maxZ; z++) {
                  BlockPos pos = new BlockPos(x, y, z);
                  if (level.m_8055_(pos).m_60734_() == Blocks.f_50077_) {
                     Vec3 blockCenter = Vec3.m_82512_(pos);
                     ClipContext ctx = new ClipContext(eyePos, blockCenter, Block.COLLIDER, Fluid.NONE, player);
                     BlockHitResult hit = level.m_45547_(ctx);
                     if (hit.m_6662_() == Type.BLOCK && hit.m_82425_().equals(pos)) {
                        tntPositions.add(pos.m_7949_());
                     }
                  }
               }
            }
         }

         if (!tntPositions.isEmpty()) {
            SheathInEvents.cacheTntBlocks(player.m_20148_(), tntPositions);
         }
      }
   }
}
