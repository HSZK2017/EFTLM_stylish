package com.Yujin.onegradefixer.epicmoonmod.mixin;

import com.Yujin.onegradefixer.epicmoonmod.config.EMConfig;
import com.Yujin.onegradefixer.epicmoonmod.skill.weapon_innate.DualInnate;
import com.Yujin.onegradefixer.epicmoonmod.skill.weapon_innate.TsInnate;
import com.Yujin.onegradefixer.epicmoonmod.util.skillparameter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

@Mixin(
   value = {Skill.class},
   remap = false,
   priority = 1000
)
public abstract class SkillMixin {
   @ModifyVariable(
      method = {"setSkillConsumptionSynchronize"},
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0,
      remap = false
   )
   private static float epicmoon$modifySynchronizedWeaponCharge(float originalValue, SkillContainer container) {
      Skill skill = container.getSkill();
      if (!(skill instanceof DualInnate) && !(skill instanceof TsInnate)) {
         return originalValue;
      } else if (container.getExecutor() instanceof ServerPlayerPatch playerPatch) {
         float var11 = container.getResource();
         float synchronizedGain = originalValue - var11;
         if (synchronizedGain <= 0.0F) {
            return originalValue;
         } else {
            float preArmorDamage = skillparameter.consume(((ServerPlayer)playerPatch.getOriginal()).m_20148_());
            if (preArmorDamage <= 0.0F) {
               return originalValue;
            } else {
               float attackPower = (float)((ServerPlayer)playerPatch.getOriginal()).m_21133_(Attributes.f_22281_);
               float maxResource = container.getMaxResource();
               if (!(attackPower <= 0.0F) && !(maxResource <= 0.0F)) {
                  float gainMultiplier;
                  if (skill instanceof TsInnate) {
                     gainMultiplier = EMConfig.getTsParameterGainMultiplier();
                  } else {
                     gainMultiplier = EMConfig.getDualParameterGainMultiplier();
                  }

                  float relativeGain = preArmorDamage / attackPower * maxResource * gainMultiplier;
                  return var11 + relativeGain;
               } else {
                  return originalValue;
               }
            }
         }
      } else {
         return originalValue;
      }
   }
}
