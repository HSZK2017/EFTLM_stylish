package com.dmc.invincible_dmc.skill.dodge;

import com.dmc.invincible_dmc.client.input.ComboInputSampler;
import com.dmc.invincible_dmc.client.input.DMComboEngine;
import com.dmc.invincible_dmc.client.input.IComboExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.client.CPSkillRequest;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class DMCDodgeSkill extends Skill {
   public static final int DIRECTION_FORWARD = 0;
   public static final int DIRECTION_BACKWARD = 1;
   public static final int DIRECTION_LEFT = 2;
   public static final int DIRECTION_RIGHT = 3;
   public static final int DIRECTION_UP = 4;
   protected final AnimationAccessor<? extends StaticAnimation> forwardAnim;
   protected final AnimationAccessor<? extends StaticAnimation> backwardAnim;
   protected final AnimationAccessor<? extends StaticAnimation> leftAnim;
   protected final AnimationAccessor<? extends StaticAnimation> rightAnim;
   protected final AnimationAccessor<? extends StaticAnimation> upAnim;

   public DMCDodgeSkill(DMCDodgeSkill.Builder<? extends DMCDodgeSkill> builder) {
      super(builder);
      this.forwardAnim = builder.forwardAnim;
      this.backwardAnim = builder.backwardAnim;
      this.leftAnim = builder.leftAnim;
      this.rightAnim = builder.rightAnim;
      this.upAnim = builder.upAnim;
   }

   public static DMCDodgeSkill.Builder<DMCDodgeSkill> createDodgeBuilder() {
      return (DMCDodgeSkill.Builder<DMCDodgeSkill>)new DMCDodgeSkill.Builder()
         .setCategory(SkillCategories.DODGE)
         .setActivateType(ActivateType.ONE_SHOT)
         .setResource(Resource.STAMINA);
   }

   protected AnimationAccessor<? extends StaticAnimation> getAnimation(int direction) {
      return switch (direction) {
         case 0 -> this.forwardAnim;
         case 1 -> this.backwardAnim;
         case 2 -> this.leftAnim;
         case 3 -> this.rightAnim;
         case 4 -> this.upAnim;
         default -> this.forwardAnim;
      };
   }

   @OnlyIn(Dist.CLIENT)
   public Object getExecutionPacket(SkillContainer skillContainer, FriendlyByteBuf args) {
      IComboExecutor dispatcher = DMComboEngine.getLocalPlayerDispatcher();
      if (dispatcher != null) {
         Options opts = Minecraft.m_91087_().f_91066_;
         dispatcher.getDirectionTracker()
            .clearForDodge(
               DMComboEngine.engineTick,
               ComboInputSampler.isRawKeyDown(opts.f_92085_),
               ComboInputSampler.isRawKeyDown(opts.f_92087_),
               ComboInputSampler.isRawKeyDown(opts.f_92086_),
               ComboInputSampler.isRawKeyDown(opts.f_92088_)
            );
      }

      LocalPlayerPatch executor = skillContainer.getClientExecutor();
      LocalPlayer localPlayer = (LocalPlayer)executor.getOriginal();
      float pulse = Mth.m_14036_(0.3F + EnchantmentHelper.m_220302_((LivingEntity)executor.getOriginal()), 0.0F, 1.0F);
      Input input = localPlayer.f_108618_;
      input.m_214106_(false, pulse);
      int forward = input.f_108568_ ? 1 : 0;
      int backward = input.f_108569_ ? -1 : 0;
      int left = input.f_108570_ ? 1 : 0;
      int right = input.f_108571_ ? -1 : 0;
      int vertical = forward + backward;
      int horizon = left + right;
      float yRot = Minecraft.m_91087_().f_91063_.m_109153_().m_90590_();
      boolean isInAir = !((LocalPlayer)executor.getOriginal()).m_20096_();
      float degree;
      int animation;
      if (vertical == 0 && horizon == 0) {
         if (!isInAir) {
            animation = 4;
            degree = yRot;
         } else {
            animation = 0;
            degree = yRot;
         }
      } else if (vertical == 0) {
         if (this.leftAnim != null && this.rightAnim != null) {
            animation = horizon >= 0 ? 2 : 3;
            degree = yRot;
         } else {
            animation = 0;
            degree = yRot + (horizon >= 0 ? -90.0F : 90.0F);
         }
      } else {
         animation = vertical >= 0 ? 0 : 1;
         degree = yRot - 45.0F * (float)vertical * (float)horizon;
      }

      CPSkillRequest packet = new CPSkillRequest(skillContainer.getSlot());
      packet.getBuffer().writeInt(animation);
      packet.getBuffer().writeFloat(degree);
      packet.getBuffer().writeBoolean(isInAir);
      return packet;
   }

   public void executeOnServer(SkillContainer skillContainer, FriendlyByteBuf args) {
      super.executeOnServer(skillContainer, args);
      ServerPlayerPatch executor = skillContainer.getServerExecutor();
      int direction = args.readInt();
      float yRot = args.readFloat();
      AnimationAccessor<? extends StaticAnimation> animation = this.getAnimation(direction);
      if (animation != null) {
         executor.playAnimationSynchronized(animation, 0.0F);
         executor.setModelYRot(yRot, true);
      }
   }

   public boolean isExecutableState(PlayerPatch<?> executor) {
      EntityState playerState = executor.getEntityState();
      return !executor.isInAir()
         && playerState.canUseSkill()
         && !((Player)executor.getOriginal()).m_20069_()
         && !((Player)executor.getOriginal()).m_6147_()
         && ((Player)executor.getOriginal()).m_20202_() == null;
   }

   public static class Builder<T extends DMCDodgeSkill> extends SkillBuilder<T> {
      protected AnimationAccessor<? extends StaticAnimation> forwardAnim;
      protected AnimationAccessor<? extends StaticAnimation> backwardAnim;
      protected AnimationAccessor<? extends StaticAnimation> leftAnim;
      protected AnimationAccessor<? extends StaticAnimation> rightAnim;
      protected AnimationAccessor<? extends StaticAnimation> upAnim;

      public DMCDodgeSkill.Builder<T> setAnimations(
         AnimationAccessor<? extends StaticAnimation> forwardAnim,
         AnimationAccessor<? extends StaticAnimation> backwardAnim,
         AnimationAccessor<? extends StaticAnimation> leftAnim,
         AnimationAccessor<? extends StaticAnimation> rightAnim,
         AnimationAccessor<? extends StaticAnimation> upAnim
      ) {
         this.forwardAnim = forwardAnim;
         this.backwardAnim = backwardAnim;
         this.leftAnim = leftAnim;
         this.rightAnim = rightAnim;
         this.upAnim = upAnim;
         return this;
      }

      public DMCDodgeSkill.Builder<T> setAnimations(
         AnimationAccessor<? extends StaticAnimation> forwardAnim,
         AnimationAccessor<? extends StaticAnimation> backwardAnim,
         AnimationAccessor<? extends StaticAnimation> upAnim
      ) {
         return this.setAnimations(forwardAnim, backwardAnim, forwardAnim, forwardAnim, upAnim);
      }
   }
}
