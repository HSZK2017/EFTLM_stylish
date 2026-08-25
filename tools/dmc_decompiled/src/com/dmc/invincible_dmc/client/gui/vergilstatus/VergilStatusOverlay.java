package com.dmc.invincible_dmc.client.gui.vergilstatus;

import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.skill.weapon_combo.Yamato;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.ConcentrationManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class VergilStatusOverlay implements IGuiOverlay {
   public static final VergilStatusOverlay INSTANCE = new VergilStatusOverlay();
   public static final IGuiOverlay VERGIL_STATUS_OVERLAY = INSTANCE;
   private static final ResourceLocation TEXTURE_BG = ResourceLocation.fromNamespaceAndPath("invincible_dmc", "textures/gui/hud/vergil_background.png");
   private static final ResourceLocation TEXTURE_SCALE = ResourceLocation.fromNamespaceAndPath(
      "invincible_dmc", "textures/gui/hud/vergil_concentration_scale.png"
   );
   public static final int BASE_W = 300;
   public static final int BASE_H = 110;
   public static final int TEX_SIZE = 512;
   private static final int TYPE_CONC = 0;
   private static final int TYPE_SDT = 1;
   private static final int TYPE_SECOND = 2;
   private float clientConcentration = 0.0F;
   private float concentrationVelocity = 0.0F;
   private float clientSDT = 0.0F;
   private float sdtVelocity = 0.0F;
   private int lastPhase = 0;
   private float clientSecondProgress = 0.0F;
   private float secondProgressVelocity = 0.0F;
   private float firstChargeFlash = 0.0F;
   private float secondChargeFlash = 0.0F;
   private float concJcFlash = 0.0F;
   private float pulseTime = 0.0F;
   private float activeTransition = 0.0F;
   private long lastSystemTime = 0L;

   public static void triggerConcFlash() {
      INSTANCE.concJcFlash = 1.0F;
   }

   public static float getAdaptiveScale(int screenWidth, int screenHeight, float configScale) {
      float maxAllowedW = (float)screenWidth * 0.45F;
      float maxAllowedH = (float)screenHeight * 0.35F;
      return Math.min(configScale, Math.min(maxAllowedW / 300.0F, maxAllowedH / 110.0F));
   }

   public void render(ForgeGui gui, GuiGraphics g, float partialTick, int screenWidth, int screenHeight) {
      Minecraft mc = Minecraft.m_91087_();
      if (this.shouldSkipRender(mc)) {
         this.lastSystemTime = 0L;
         this.concentrationVelocity = 0.0F;
         this.sdtVelocity = 0.0F;
         this.secondProgressVelocity = 0.0F;
         DevilTriggerRenderer.reset();
      } else {
         float deltaTime = this.calculateDeltaTime();
         this.pulseTime += deltaTime;
         this.updateConcentration(mc, deltaTime);
         this.updateSDTAnimation(mc, deltaTime);
         LocalPlayerPatch localPlayerPatch = EpicFightCapabilities.getLocalPlayerPatch(mc.f_91074_);
         if (localPlayerPatch != null) {
            SkillContainer skillContainer = localPlayerPatch.getSkill(Yamato.YAMATO);
            if (skillContainer != null) {
               float dtFill = (float)skillContainer.getStack() + skillContainer.getResource(partialTick);
               int doppelMode = findDoppelMode(mc);
               DevilTriggerRenderer.update(dtFill, deltaTime, doppelMode);
            }
         }

         VergilStatusOverlay.LayoutConfig layout = VergilStatusOverlay.LayoutConfig.calculate(screenWidth, screenHeight);
         this.renderPipeline(g, layout);
      }
   }

   private boolean shouldSkipRender(Minecraft mc) {
      if (mc.f_91074_ != null && !mc.f_91066_.f_92062_) {
         LocalPlayerPatch localPlayerPatch = EpicFightCapabilities.getLocalPlayerPatch(mc.f_91074_);
         if (localPlayerPatch != null) {
            SkillContainer skillContainer = localPlayerPatch.getSkill(Yamato.YAMATO);
            return skillContainer == null;
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   private float calculateDeltaTime() {
      long now = System.nanoTime();
      if (this.lastSystemTime == 0L) {
         this.lastSystemTime = now;
      }

      float deltaTime = (float)(now - this.lastSystemTime) / 1.0E9F;
      this.lastSystemTime = now;
      return Mth.m_14036_(deltaTime, 0.001F, 0.1F);
   }

   private void updateConcentration(Minecraft mc, float deltaTime) {
      float target = 0.0F;
      if (mc.f_91074_ != null) {
         LocalPlayerPatch lpp = (LocalPlayerPatch)EpicFightCapabilities.getEntityPatch(mc.f_91074_, LocalPlayerPatch.class);
         if (lpp != null) {
            SkillContainer container = lpp.getSkill(SkillSlots.WEAPON_INNATE);
            if (container != null) {
               float raw = ConcentrationManager.getConcentration(container) / 10000.0F;
               target = concentrationToProgress(raw);
            }
         }
      }

      this.clientConcentration = this.smoothDamp(this.clientConcentration, target, 0.06F, 15.0F, deltaTime, 0);
   }

   private void updateSDTAnimation(Minecraft mc, float deltaTime) {
      if (mc.f_91074_ != null) {
         LocalPlayerPatch lpp = (LocalPlayerPatch)EpicFightCapabilities.getEntityPatch(mc.f_91074_, LocalPlayerPatch.class);
         if (lpp != null) {
            SkillContainer container = lpp.getSkill(SkillSlots.WEAPON_INNATE);
            if (container != null) {
               SkillDataManager dm = container.getDataManager();
               float rawSDT = dm.hasData((SkillDataKey)DMCSkillDataKeys.SDT_VALUE.get())
                  ? (Float)dm.getDataValue((SkillDataKey)DMCSkillDataKeys.SDT_VALUE.get())
                  : 0.0F;
               int currentPhase = dm.hasData((SkillDataKey)DMCSkillDataKeys.SDT_PHASE.get())
                  ? (Integer)dm.getDataValue((SkillDataKey)DMCSkillDataKeys.SDT_PHASE.get())
                  : 0;
               float targetSDTProgress = rawSDT / 1000.0F;
               this.clientSDT = this.smoothDamp(this.clientSDT, targetSDTProgress, 0.05F, 10.0F, deltaTime, 1);
               if (currentPhase == 2 && this.lastPhase == 1) {
                  this.firstChargeFlash = 1.0F;
               }

               if (currentPhase == 3 && this.lastPhase == 2) {
                  this.secondChargeFlash = 1.0F;
               }

               this.lastPhase = currentPhase;
               if (this.firstChargeFlash > 0.0F) {
                  this.firstChargeFlash = Math.max(0.0F, this.firstChargeFlash - deltaTime * 2.0F);
               }

               if (this.secondChargeFlash > 0.0F) {
                  this.secondChargeFlash = Math.max(0.0F, this.secondChargeFlash - deltaTime * 1.8F);
               }

               if (this.concJcFlash > 0.0F) {
                  this.concJcFlash = Math.max(0.0F, this.concJcFlash - deltaTime * 3.5F);
               }

               float targetSecondProgress = 0.0F;
               if (currentPhase == 2) {
                  int confirmTicks = dm.hasData((SkillDataKey)DMCSkillDataKeys.SDT_CONFIRM_TIMER.get())
                     ? (Integer)dm.getDataValue((SkillDataKey)DMCSkillDataKeys.SDT_CONFIRM_TIMER.get())
                     : 0;
                  targetSecondProgress = (float)confirmTicks / 20.0F;
               } else if (currentPhase >= 3) {
                  targetSecondProgress = 1.0F;
               }

               this.clientSecondProgress = this.smoothDamp(this.clientSecondProgress, targetSecondProgress, 0.04F, 20.0F, deltaTime, 2);
               if (currentPhase == 4) {
                  this.activeTransition = Math.min(1.0F, this.activeTransition + deltaTime * 4.0F);
               } else {
                  this.activeTransition = Math.max(0.0F, this.activeTransition - deltaTime * 1.5F);
               }

               SDTScreenOverlay.INSTANCE.updateState(currentPhase, this.clientSDT, this.clientSecondProgress);
            }
         }
      }
   }

   private float smoothDamp(float current, float target, float smoothTime, float maxSpeed, float deltaTime, int type) {
      smoothTime = Math.max(1.0E-4F, smoothTime);
      float omega = 2.0F / smoothTime;
      float x = omega * deltaTime;
      float exp = 1.0F / (1.0F + x + 0.48F * x * x + 0.235F * x * x * x);
      float change = current - target;
      float maxChange = maxSpeed * smoothTime;
      change = Mth.m_14036_(change, -maxChange, maxChange);
      target = current - change;
      float velocity = 0.0F;
      if (type == 0) {
         velocity = this.concentrationVelocity;
      } else if (type == 1) {
         velocity = this.sdtVelocity;
      } else if (type == 2) {
         velocity = this.secondProgressVelocity;
      }

      float temp = (velocity + omega * change) * deltaTime;
      float newVelocity = (velocity - omega * temp) * exp;
      if (type == 0) {
         this.concentrationVelocity = newVelocity;
      } else if (type == 1) {
         this.sdtVelocity = newVelocity;
      } else if (type == 2) {
         this.secondProgressVelocity = newVelocity;
      }

      float output = target + (change + temp) * exp;
      if (target - current > 0.0F == output > target) {
         output = target;
         if (type == 0) {
            this.concentrationVelocity = 0.0F;
         } else if (type == 1) {
            this.sdtVelocity = 0.0F;
         } else if (type == 2) {
            this.secondProgressVelocity = 0.0F;
         }
      }

      return output;
   }

   private static int findDoppelMode(Minecraft mc) {
      if (mc.f_91074_ == null) {
         return -1;
      } else {
         for (Entity entity : mc.f_91074_.m_9236_().m_142646_().m_142273_()) {
            if (entity instanceof DoppelgangerEntity de && mc.f_91074_.m_20148_().equals(de.getOwnerUUID()) && de.m_6084_()) {
               return de.getDoppelDelayMode();
            }
         }

         return -1;
      }
   }

   private static float concentrationToProgress(float raw) {
      float P0 = 0.0F;
      float C0 = 0.0F;
      float P1 = 0.49271F;
      float C1 = 0.4F;
      float P2 = 0.7676F;
      float C2 = 0.8F;
      float P3 = 1.0F;
      float C3 = 1.0F;
      if (raw <= 0.4F) {
         return 0.0F + (raw - 0.0F) / 0.4F * 0.49271F;
      } else {
         return raw <= 0.8F ? 0.49271F + (raw - 0.4F) / 0.4F * 0.27489F : 0.7676F + (raw - 0.8F) / 0.19999999F * 0.2324F;
      }
   }

   private void renderPipeline(GuiGraphics g, VergilStatusOverlay.LayoutConfig layout) {
      g.m_280168_().m_85836_();
      this.setupRenderStates();
      g.m_280168_().m_252880_(0.0F, 0.0F, 5000.0F);
      g.m_280246_(1.0F, 1.0F, 1.0F, 1.0F);
      g.m_280411_(TEXTURE_BG, layout.xOffset(), layout.yOffset(), layout.width(), layout.height(), 0.0F, 0.0F, 300, 110, 512, 512);
      ConcentrationBarRenderer.render(g, (float)layout.xOffset(), (float)layout.yOffset(), layout.scale(), this.clientConcentration, this.concJcFlash);
      g.m_280262_();
      Minecraft mc = Minecraft.m_91087_();
      LocalPlayerPatch lpp = (LocalPlayerPatch)EpicFightCapabilities.getEntityPatch(mc.f_91074_, LocalPlayerPatch.class);
      if (lpp != null) {
         SkillContainer container = lpp.getSkill(SkillSlots.WEAPON_INNATE);
         if (container != null) {
            int phase = container.getDataManager().hasData((SkillDataKey)DMCSkillDataKeys.SDT_PHASE.get())
               ? (Integer)container.getDataManager().getDataValue((SkillDataKey)DMCSkillDataKeys.SDT_PHASE.get())
               : 0;
            SinDevilTriggerRenderer.render(
               g,
               (float)layout.xOffset(),
               (float)layout.yOffset(),
               layout.scale(),
               this.clientSDT,
               this.clientSecondProgress,
               phase,
               this.firstChargeFlash,
               this.secondChargeFlash,
               this.pulseTime,
               this.activeTransition
            );
         }
      }

      g.m_280411_(TEXTURE_SCALE, layout.xOffset(), layout.yOffset(), layout.width(), layout.height(), 0.0F, 0.0F, 300, 110, 512, 512);
      DevilTriggerRenderer.render(g, layout.xOffset(), layout.yOffset(), layout.width(), layout.height(), layout.scale());
      this.restoreRenderStates();
      g.m_280168_().m_85849_();
   }

   private void setupRenderStates() {
      RenderSystem.disableDepthTest();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableCull();
   }

   private void restoreRenderStates() {
      RenderSystem.enableCull();
      RenderSystem.disableBlend();
      RenderSystem.enableDepthTest();
   }

   private static record LayoutConfig(int xOffset, int yOffset, int width, int height, float scale) {
      public static VergilStatusOverlay.LayoutConfig calculate(int screenWidth, int screenHeight) {
         float configScale = ((Double)VergilStatusConfig.SCALE.get()).floatValue();
         float adaptiveScale = VergilStatusOverlay.getAdaptiveScale(screenWidth, screenHeight, configScale);
         int w = Math.round(300.0F * adaptiveScale);
         int h = Math.round(110.0F * adaptiveScale);
         int posX = ((Double)VergilStatusConfig.POS_X.get()).intValue();
         int posY = ((Double)VergilStatusConfig.POS_Y.get()).intValue();
         posX = Mth.m_14045_(posX, 0, screenWidth - w);
         posY = Mth.m_14045_(posY, 0, screenHeight - h);
         return new VergilStatusOverlay.LayoutConfig(posX, posY, w, h, adaptiveScale);
      }
   }
}
