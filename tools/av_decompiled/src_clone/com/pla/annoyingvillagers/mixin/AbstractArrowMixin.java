package com.pla.annoyingvillagers.mixin;

import com.pla.annoyingvillagers.entity.EnderAegisProjectile;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({AbstractArrow.class})
public abstract class AbstractArrowMixin {
   @Redirect(
      method = {"onHitEntity(Lnet/minecraft/world/phys/EntityHitResult;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/damagesource/DamageSources;arrow(Lnet/minecraft/world/entity/projectile/AbstractArrow;Lnet/minecraft/world/entity/Entity;)Lnet/minecraft/world/damagesource/DamageSource;"
      )
   )
   private DamageSource redirectArrowDamage(DamageSources sources, AbstractArrow arrow, @Nullable Entity shooter) {
      if (arrow instanceof EnderAegisProjectile) {
         ResourceKey<DamageType> key = shooter instanceof Player
            ? DamageTypes.f_268464_
            : (shooter instanceof LivingEntity ? DamageTypes.f_268566_ : DamageTypes.f_268433_);
         Registry<DamageType> reg = arrow.m_9236_().m_9598_().m_175515_(Registries.f_268580_);
         Holder<DamageType> holder = reg.m_246971_(key);
         return new DamageSource(holder, shooter, arrow);
      } else {
         return sources.m_269418_(arrow, shooter);
      }
   }
}
