package com.pla.annoyingvillagers.mixin;

import com.pla.annoyingvillagers.block.EndFireBlock;
import com.pla.annoyingvillagers.entity.TridentLightningBolt;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModBlocks;
import com.pla.annoyingvillagers.item.EnderGlaiveItem;
import com.pla.annoyingvillagers.util.ExplosionFxMute;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(
   value = {Explosion.class},
   remap = true
)
public abstract class ExplosionMixin {
   @Shadow
   @Final
   private Level f_46012_;
   @Shadow
   @Final
   @Nullable
   private Entity f_46016_;

   @Shadow
   @Nullable
   public abstract LivingEntity m_252906_();

   @Unique
   private boolean muteAtThisPos() {
      if (!this.f_46012_.m_5776_()) {
         return false;
      } else {
         Vec3 pos = ((Explosion)this).getPosition();
         long key = BlockPos.m_121882_(Mth.m_14107_(pos.f_82479_), Mth.m_14107_(pos.f_82480_), Mth.m_14107_(pos.f_82481_));
         return ExplosionFxMute.shouldMute(key, this.f_46012_.m_46467_());
      }
   }

   @ModifyArg(
      method = {"finalizeExplosion(Z)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V"
      ),
      index = 5
   )
   private float muteExplosionSound(float vol) {
      return this.muteAtThisPos() ? 0.0F : vol;
   }

   @ModifyVariable(
      method = {"finalizeExplosion(Z)V"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private boolean disableParticlesWhenMuted(boolean spawnParticles) {
      return this.muteAtThisPos() ? false : spawnParticles;
   }

   @ModifyArg(
      method = {"finalizeExplosion(Z)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"
      ),
      index = 1
   )
   private BlockState replaceVanillaFire(BlockState originalState) {
      if (!(originalState.m_60734_() instanceof BaseFireBlock)) {
         return originalState;
      } else {
         LivingEntity owner = this.m_252906_();
         if (owner != null && owner.m_6084_()) {
            ItemStack stack = owner.m_21205_();
            if (stack.m_41720_() instanceof EnderGlaiveItem) {
               return ((EndFireBlock)AnnoyingVillagersModBlocks.END_FIRE.get()).m_49966_();
            }
         }

         return originalState;
      }
   }

   @Redirect(
      method = {"explode"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"
      )
   )
   private void noKnockbackFromTridentLightning(Entity instance, Vec3 pDeltaMovement) {
      if (!(this.f_46016_ instanceof TridentLightningBolt)) {
         instance.m_20256_(pDeltaMovement);
      }
   }

   @Redirect(
      method = {"explode"},
      at = @At(
         value = "INVOKE",
         target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
      )
   )
   private Object noPlayerExplosionVector(Map<?, ?> map, Object key, Object value) {
      return this.f_46016_ instanceof TridentLightningBolt ? null : ((Map<?, Object>)map).put(key, value);
   }
}
