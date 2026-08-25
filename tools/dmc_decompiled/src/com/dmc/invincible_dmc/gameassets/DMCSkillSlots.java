package com.dmc.invincible_dmc.gameassets;

import com.dmc.invincible_dmc.InvincibleMod_DMC;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillSlot;

public final class DMCSkillSlots {
   public static final SkillCategory INSTANT_JUDGEMENT_CUT_END = new DMCSkillSlots.CustomSkillCategory(true, true, true, InvincibleMod_DMC.rl("skillbook_dmc5"));
   public static final SkillSlot SKILL_BOOK = new DMCSkillSlots.CustomSkillSlot(INSTANT_JUDGEMENT_CUT_END);

   public static void init() {
   }

   private DMCSkillSlots() {
   }

   private static final class CustomSkillCategory implements SkillCategory {
      private final int id;
      private final boolean shouldSave;
      private final boolean shouldSynchronize;
      private final boolean modifiable;
      private final ResourceLocation bookIcon;

      CustomSkillCategory(boolean shouldSave, boolean shouldSynchronize, boolean modifiable, ResourceLocation bookIcon) {
         this.shouldSave = shouldSave;
         this.shouldSynchronize = shouldSynchronize;
         this.modifiable = modifiable;
         this.bookIcon = bookIcon;
         this.id = SkillCategory.ENUM_MANAGER.assign(this);
      }

      public boolean shouldSave() {
         return this.shouldSave;
      }

      public boolean shouldSynchronize() {
         return this.shouldSynchronize;
      }

      public boolean learnable() {
         return this.modifiable;
      }

      public ResourceLocation bookIcon() {
         return this.bookIcon;
      }

      public int universalOrdinal() {
         return this.id;
      }

      @Override
      public String toString() {
         return "dmc5";
      }
   }

   private static final class CustomSkillSlot implements SkillSlot {
      private final int id;
      private final SkillCategory category;

      CustomSkillSlot(SkillCategory category) {
         this.category = category;
         this.id = SkillSlot.ENUM_MANAGER.assign(this);
      }

      public SkillCategory category() {
         return this.category;
      }

      public int universalOrdinal() {
         return this.id;
      }

      @Override
      public String toString() {
         return "dmc5";
      }
   }
}
