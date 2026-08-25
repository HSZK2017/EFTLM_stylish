package com.pla.annoyingvillagers.mixin;

import com.pla.annoyingvillagers.entity.TridentLightningBolt;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModDamageTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({Player.class})
public abstract class PlayerMixin {
   @Redirect(
      method = {"hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/damagesource/DamageSource;is(Lnet/minecraft/tags/TagKey;)Z",
         ordinal = 0
      )
   )
   private boolean customBypassInvulnerability(DamageSource source, TagKey<DamageType> tag) {
      Player self = (Player)this;
      boolean original = source.m_269533_(tag);
      return DamageTypeTags.f_268738_.equals(tag)
            && self.m_150110_().f_35934_
            && source.m_276093_(AnnoyingVillagersModDamageTypes.IMPACT_EXPLOSION)
            && source.m_7640_() instanceof TridentLightningBolt
         ? false
         : original;
   }
}
