package com.pla.annoyingvillagers.item;

import java.util.Objects;
import java.util.Random;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import reascer.wom.gameasset.animations.weapons.AnimsHerrscher;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ClowSwordItem extends SwordItem {
   public boolean m_7579_(@NotNull ItemStack pStack, @NotNull LivingEntity pTarget, @NotNull LivingEntity pAttacker) {
      if (!pAttacker.m_9236_().f_46443_) {
         LivingEntityPatch<?> livingEntityPatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(pAttacker, LivingEntityPatch.class);
         if (livingEntityPatch != null) {
            AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(livingEntityPatch.getAnimator().getPlayerFor(null))
               .getRealAnimation();
            if (dynamicAnimation == AnimsHerrscher.HERRSCHER_BEFREIUNG) {
               pTarget.m_19983_(new ItemStack(Items.f_42534_, new Random().nextInt(1, 3)));
            }
         }
      }

      return super.m_7579_(pStack, pTarget, pAttacker);
   }

   public ClowSwordItem() {
      super(new Tier() {
         public int m_6609_() {
            return 1561;
         }

         public float m_6624_() {
            return 6.0F;
         }

         public float m_6631_() {
            return 2.4F;
         }

         public int m_6604_() {
            return 5;
         }

         public int m_6601_() {
            return 21;
         }

         @NotNull
         public Ingredient m_6282_() {
            return Ingredient.m_43927_(new ItemStack[]{new ItemStack(Items.f_42534_)});
         }
      }, 3, -2.2F, new Properties());
   }
}
