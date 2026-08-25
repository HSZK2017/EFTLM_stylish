package com.dmc.invincible_dmc.skill.weapon_innate;

import com.dmc.invincible_dmc.DMConfig;
import com.dmc.invincible_dmc.api.events.BaseEvent;
import com.dmc.invincible_dmc.api.events.HitEvent;
import com.dmc.invincible_dmc.api.skill.ComboNode;
import com.dmc.invincible_dmc.capability.DMCPlayer;
import com.dmc.invincible_dmc.capability.DMCPlayerCapabilityProvider;
import com.dmc.invincible_dmc.damagesource.DMCDamageTypeTags;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerPatch;
import com.dmc.invincible_dmc.gameassets.DMCSkillDataKeys;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.ConcentrationManager;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.SinDevilTriggerManager;
import com.dmc.invincible_dmc.utils.DMCAnimationUtils;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.AnimationManager.AnimationAccessor;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.api.utils.math.Vec2f;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.common.AnimatorControlPacket.Action;
import yesman.epicfight.network.common.AnimatorControlPacket.Layer;
import yesman.epicfight.network.common.AnimatorControlPacket.Priority;
import yesman.epicfight.network.server.SPAnimatorControl;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.eventlistener.DodgeSuccessEvent;
import yesman.epicfight.world.entity.eventlistener.DealDamageEvent.Damage;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.TakeDamageEvent.Attack;
import yesman.epicfight.world.entity.eventlistener.TakeDamageEvent.Hurt;
import yesman.epicfight.world.gamerule.EpicFightGameRules;

public class AbstractDmcInnateSkill extends Skill {
   protected static final UUID EVENT_UUID = UUID.fromString("d1d114cc-f11f-11ed-a05b-0242ac114514");
   protected final ConcentrationManager concentrationManager = new ConcentrationManager();
   protected final SinDevilTriggerManager sinDevilTriggerManager = new SinDevilTriggerManager();
   protected static final Vec2f[] CLOCK_POS = new Vec2f[]{
      new Vec2f(0.5F, 0.5F), new Vec2f(0.5F, 0.0F), new Vec2f(0.0F, 0.0F), new Vec2f(0.0F, 1.0F), new Vec2f(1.0F, 1.0F), new Vec2f(1.0F, 0.0F)
   };
   protected boolean shouldDrawGui;
   protected ResourceLocation skillTexture;
   protected List<String> translationKeys;

   public AbstractDmcInnateSkill(SkillBuilder<? extends Skill> builder) {
      super(builder);
   }

   public void onInitiate(SkillContainer container) {
      super.onInitiate(container);
      DMCPlayerCapabilityProvider.get((Player)container.getExecutor().getOriginal()).resetPhase();
      container.getDataManager().setData((SkillDataKey)DMCSkillDataKeys.COOLDOWN.get(), 0);
      container.getExecutor().getEventListener().addEventListener(EventType.DODGE_SUCCESS_EVENT, EVENT_UUID, event -> this.onDodgeSuccess(event, container));
      container.getExecutor()
         .getEventListener()
         .addEventListener(EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID, event -> this.onTakeDamageEventAttack(event, container));
      container.getExecutor()
         .getEventListener()
         .addEventListener(EventType.TAKE_DAMAGE_EVENT_HURT, EVENT_UUID, event -> this.onTakeDamageEventHurt(event, container));
      container.getExecutor()
         .getEventListener()
         .addEventListener(EventType.TAKE_DAMAGE_EVENT_DAMAGE, EVENT_UUID, event -> this.concentrationManager.onTakeDamage(event, container));
      container.getExecutor()
         .getEventListener()
         .addEventListener(EventType.DEAL_DAMAGE_EVENT_ATTACK, EVENT_UUID, event -> this.onDealDamageEventAttack(event, container));
      container.getExecutor()
         .getEventListener()
         .addEventListener(EventType.DEAL_DAMAGE_EVENT_DAMAGE, EVENT_UUID, event -> this.onDealDamageEventDamage(event, container));
   }

   public void onRemoved(SkillContainer container) {
      super.onRemoved(container);
      container.getExecutor().getEventListener().removeListener(EventType.DODGE_SUCCESS_EVENT, EVENT_UUID);
      container.getExecutor().getEventListener().removeListener(EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID);
      container.getExecutor().getEventListener().removeListener(EventType.TAKE_DAMAGE_EVENT_HURT, EVENT_UUID);
      container.getExecutor().getEventListener().removeListener(EventType.TAKE_DAMAGE_EVENT_DAMAGE, EVENT_UUID);
      container.getExecutor().getEventListener().removeListener(EventType.DEAL_DAMAGE_EVENT_ATTACK, EVENT_UUID);
      container.getExecutor().getEventListener().removeListener(EventType.DEAL_DAMAGE_EVENT_DAMAGE, EVENT_UUID);
   }

   protected void handleStiff(SkillContainer container, AnimationAccessor animationAccessor) {
      boolean stiffAttack = (Boolean)EpicFightGameRules.STIFF_COMBO_ATTACKS.getRuleValue(((Player)container.getExecutor().getOriginal()).m_9236_());
      SPAnimatorControl animatorControlPacket;
      if (stiffAttack) {
         animatorControlPacket = new SPAnimatorControl(Action.PLAY, animationAccessor, 0.0F, container.getExecutor());
      } else {
         animatorControlPacket = new SPAnimatorControl(
            Action.PLAY_CLIENT, animationAccessor, 0.0F, container.getExecutor(), Layer.COMPOSITE_LAYER, Priority.HIGHEST
         );
      }

      EpicFightNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(
         animatorControlPacket, (ServerPlayer)container.getServerExecutor().getOriginal(), new Object[0]
      );
   }

   protected void initPlayer(SkillContainer container, DMCPlayer DMCPlayer, ComboNode dataNode) {
      DMCPlayer.setCurrentDataNode(dataNode);
      if (dataNode.getCooldown() > 0) {
         container.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.COOLDOWN.get(), dataNode.getCooldown());
         DMCPlayer.setItemCooldown(((Player)container.getExecutor().getOriginal()).m_21205_(), dataNode.getCooldown());
      }
   }

   protected void onDodgeSuccess(DodgeSuccessEvent event, SkillContainer container) {
      DMCPlayer DMCPlayer = DMCPlayerCapabilityProvider.get((Player)container.getExecutor().getOriginal());
      List<BaseEvent> dodgeSuccessEvents = DMCPlayerCapabilityProvider.get((Player)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
         .getDodgeSuccessEvents();
      if (dodgeSuccessEvents != null) {
         dodgeSuccessEvents.forEach(
            dodgeEvent -> dodgeEvent.testAndExecute(event.getPlayerPatch(), ((ServerPlayerPatch)event.getPlayerPatch()).getTarget(), DMCPlayer)
         );
      }

      container.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.DODGE_SUCCESS_TIMER.get(), (Integer)DMConfig.EFFECT_TICK.get());
      this.concentrationManager.onDodgeSuccess(event, container);
   }

   protected void onTakeDamageEventAttack(Attack event, SkillContainer container) {
      DMCPlayer DMCPlayer = DMCPlayerCapabilityProvider.get((Player)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal());
      if (event.getDamageSource() instanceof EpicFightDamageSource epicFightDamageSource && !DMCPlayer.canBeInterrupt()) {
         epicFightDamageSource.setStunType(StunType.NONE);
      }

      if (event.isParried()) {
         container.getDataManager().setDataSync((SkillDataKey)DMCSkillDataKeys.PARRY_TIMER.get(), (Integer)DMConfig.EFFECT_TICK.get());
      }
   }

   protected void onTakeDamageEventHurt(Hurt event, SkillContainer container) {
      DMCPlayer DMCPlayer = DMCPlayerCapabilityProvider.get((Player)container.getExecutor().getOriginal());
      List<BaseEvent> hurtEvents = DMCPlayerCapabilityProvider.get((Player)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).getHurtEvents();
      if (hurtEvents != null) {
         hurtEvents.forEach(hurtEvent -> hurtEvent.testAndExecute(event.getPlayerPatch(), ((ServerPlayerPatch)event.getPlayerPatch()).getTarget(), DMCPlayer));
      }

      if (DMCPlayer.getHurtDamageMultiplier() != 0.0F) {
         event.attachValueModifier(ValueModifier.multiplier(DMCPlayer.getHurtDamageMultiplier()));
      }
   }

   protected void onDealDamageEventAttack(yesman.epicfight.world.entity.eventlistener.DealDamageEvent.Attack event, SkillContainer container) {
      DMCPlayer DMCPlayer = DMCPlayerCapabilityProvider.get((Player)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal());
      if (DMCPlayer.getStunTypeModifier() != null) {
         event.getDamageSource().setStunType(DMCPlayer.getStunTypeModifier());
      }

      if (DMCPlayer.getImpactMultiplier() != 1.0F) {
         event.getDamageSource().setBaseImpact(event.getDamageSource().getBaseImpact() * DMCPlayer.getImpactMultiplier());
      }

      if (DMCPlayer.getArmorNegation() != 0.0F) {
         event.getDamageSource().setBaseArmorNegation(DMCPlayer.getArmorNegation());
      }

      if (DMCPlayer.getDamageMultiplier() != null) {
         event.getDamageSource().attachDamageModifier(DMCPlayer.getDamageMultiplier());
      }

      this.concentrationManager.onDealDamage(event, container);
   }

   protected boolean shouldCharge(Damage event, SkillContainer container, DMCPlayer DMCPlayer) {
      return !event.getDamageSource().m_269533_(DMCDamageTypeTags.NOT_CHARGE) && !DMCPlayer.isNotCharge();
   }

   protected void onDealDamageEventDamage(Damage event, SkillContainer container) {
      PlayerPatch<?> playerPatch = event.getPlayerPatch();
      ItemStack mainHandItem = ((Player)playerPatch.getOriginal()).m_21205_();
      CapabilityItem capabilityItem = EpicFightCapabilities.getItemStackCapability(mainHandItem);
      if (capabilityItem != null && capabilityItem.getInnateSkill(playerPatch, mainHandItem) instanceof ComboBasicAttack) {
         DMCPlayer DMCPlayer = DMCPlayerCapabilityProvider.get((Player)container.getExecutor().getOriginal());
         if (this.shouldCharge(event, container, DMCPlayer) && !container.isFull()) {
            float value = container.getResource() + event.getAttackDamage();
            if (value > 0.0F) {
               this.setConsumptionSynchronize(container, value);
            }
         }

         AnimationPlayer animationPlayer = DMCAnimationUtils.getMainPlayer(playerPatch);
         if (animationPlayer != null
            && DMCAnimationUtils.sameAccessor(event.getDamageSource().getAnimation(), DMCAnimationUtils.getCurrentAnimationAccessor(animationPlayer))) {
            List<BaseEvent> hitEvents = DMCPlayerCapabilityProvider.get((Player)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal())
               .getHitSuccessEvents();
            if (hitEvents != null) {
               Entity target = event.getTarget() == null ? ((ServerPlayerPatch)event.getPlayerPatch()).getTarget() : event.getTarget();
               hitEvents.forEach(
                  baseEvent -> {
                     AttackAnimation attackAnimation = DMCAnimationUtils.getRealAnimationAs(animationPlayer, AttackAnimation.class);
                     if (baseEvent instanceof HitEvent hitEvent
                        && attackAnimation != null
                        && hitEvent.phaseIndex >= 0
                        && attackAnimation.getPhaseOrderByTime(animationPlayer.getElapsedTime()) != hitEvent.phaseIndex) {
                        return;
                     }

                     baseEvent.testAndExecute(event.getPlayerPatch(), target, DMCPlayer);
                  }
               );
            }

            if (!event.getDamageSource().m_269533_(DoppelgangerPatch.DOPPELGANGER_DAMAGE) && container.getSkill() instanceof ComboBasicAttack comboSkill) {
               comboSkill.recordHitExtendHit(container, event.getTarget());
            }
         }
      }
   }

   public boolean isExecutableState(PlayerPatch<?> executor) {
      return !((Player)executor.getOriginal()).m_5833_();
   }

   public boolean shouldDraw(SkillContainer container) {
      return this.shouldDrawGui;
   }

   public ResourceLocation getSkillTexture() {
      return this.skillTexture == null ? super.getSkillTexture() : this.skillTexture;
   }

   public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerpatch) {
      if (this.translationKeys != null && !this.translationKeys.isEmpty()) {
         List<Component> list = Lists.newArrayList();

         for (String translationKey : this.translationKeys) {
            list.add(Component.m_237115_(translationKey));
         }

         return list;
      } else {
         return super.getTooltipOnItem(itemStack, cap, playerpatch);
      }
   }

   protected void tickCooldownsAndTimers(SkillContainer container) {
      DMCPlayer DMCPlayer = DMCPlayerCapabilityProvider.get((Player)container.getExecutor().getOriginal());
      SkillDataManager manager = container.getDataManager();
      if (manager.hasData((SkillDataKey)DMCSkillDataKeys.DODGE_SUCCESS_TIMER.get())) {
         manager.setData(
            (SkillDataKey)DMCSkillDataKeys.DODGE_SUCCESS_TIMER.get(),
            Math.max((Integer)manager.getDataValue((SkillDataKey)DMCSkillDataKeys.DODGE_SUCCESS_TIMER.get()) - 1, 0)
         );
      }

      if (manager.hasData((SkillDataKey)DMCSkillDataKeys.DODGE_COUNTER_SUCCESS_TIMER.get())) {
         manager.setData(
            (SkillDataKey)DMCSkillDataKeys.DODGE_COUNTER_SUCCESS_TIMER.get(),
            Math.max((Integer)manager.getDataValue((SkillDataKey)DMCSkillDataKeys.DODGE_COUNTER_SUCCESS_TIMER.get()) - 1, 0)
         );
      }

      if (manager.hasData((SkillDataKey)DMCSkillDataKeys.PARRY_TIMER.get())) {
         manager.setData(
            (SkillDataKey)DMCSkillDataKeys.PARRY_TIMER.get(), Math.max((Integer)manager.getDataValue((SkillDataKey)DMCSkillDataKeys.PARRY_TIMER.get()) - 1, 0)
         );
      }

      if (container.getExecutor() instanceof ServerPlayerPatch serverPlayerPatch) {
         ServerPlayer player = (ServerPlayer)serverPlayerPatch.getOriginal();
         ItemStack itemStack = player.m_21205_();
         int currentCooldown = DMCPlayer.getItemCooldown(itemStack);
         if (currentCooldown > 0) {
            DMCPlayer.setItemCooldown(itemStack, --currentCooldown);
         }

         if (currentCooldown != (Integer)manager.getDataValue((SkillDataKey)DMCSkillDataKeys.COOLDOWN.get())) {
            manager.setDataSync((SkillDataKey)DMCSkillDataKeys.COOLDOWN.get(), currentCooldown);
         }

         boolean onGround = player.m_20096_();
         if (!manager.hasData((SkillDataKey)DMCSkillDataKeys.IS_ON_GROUND.get())
            || (Boolean)manager.getDataValue((SkillDataKey)DMCSkillDataKeys.IS_ON_GROUND.get()) != onGround) {
            manager.setDataSync((SkillDataKey)DMCSkillDataKeys.IS_ON_GROUND.get(), onGround);
         }

         int airTime = manager.hasData((SkillDataKey)DMCSkillDataKeys.AIR_TIME_TICKS.get())
            ? (Integer)manager.getDataValue((SkillDataKey)DMCSkillDataKeys.AIR_TIME_TICKS.get())
            : 0;
         if (onGround) {
            if (airTime > 0) {
               manager.setDataSync((SkillDataKey)DMCSkillDataKeys.AIR_TIME_TICKS.get(), 0);
            }

            if (manager.hasData((SkillDataKey)DMCSkillDataKeys.AERIAL_ATTACK_COUNT.get())
               && (Integer)manager.getDataValue((SkillDataKey)DMCSkillDataKeys.AERIAL_ATTACK_COUNT.get()) > 0) {
               manager.setDataSync((SkillDataKey)DMCSkillDataKeys.AERIAL_ATTACK_COUNT.get(), 0);
            }
         } else {
            int newAirTime = airTime + 1;
            manager.setDataSync((SkillDataKey)DMCSkillDataKeys.AIR_TIME_TICKS.get(), newAirTime);
            if (newAirTime % 160 == 0 && manager.hasData((SkillDataKey)DMCSkillDataKeys.AERIAL_ATTACK_COUNT.get())) {
               manager.setDataSync((SkillDataKey)DMCSkillDataKeys.AERIAL_ATTACK_COUNT.get(), 0);
            }

            if (!player.m_7500_() && !player.m_5833_() && newAirTime > 40) {
               float extraGravity = (float)newAirTime * 5.0E-4F;
               player.m_20256_(player.m_20184_().m_82520_(0.0, (double)(-extraGravity), 0.0));
            }
         }
      }
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (!container.getExecutor().isLogicalClient()) {
         this.concentrationManager.tickConcentrationRegen(container);
         this.concentrationManager.tickSync(container);
         this.sinDevilTriggerManager.tick(container, this);
         if (SinDevilTriggerManager.isPlayerInSDT((Player)container.getExecutor().getOriginal())
            && ((Player)container.getExecutor().getOriginal()).f_19797_ % 10 == 0) {
            ((Player)container.getExecutor().getOriginal()).m_5634_(Math.max(1.0F, ((Player)container.getExecutor().getOriginal()).m_21233_() * 0.1F));
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void drawOnGui(BattleModeGui gui, SkillContainer container, GuiGraphics guiGraphics, float x, float y, float partialTick) {
      guiGraphics.m_280168_().m_85836_();
      guiGraphics.m_280168_().m_252880_(0.0F, (float)gui.getSlidingProgression(), 0.0F);
      boolean creative = ((Player)container.getExecutor().getOriginal()).m_7500_();
      boolean fullstack = creative || container.isFull();
      boolean canUse = !container.isDisabled() && container.getSkill().checkExecuteCondition(container);
      float cooldownRatio = !fullstack && !container.isActivated() ? container.getResource(partialTick) : 1.0F;
      int vertexNum = 0;
      float iconSize = 32.0F;
      float bottom = y + iconSize;
      float right = x + iconSize;
      float middle = x + iconSize * 0.5F;
      float lastVertexX = 0.0F;
      float lastVertexY = 0.0F;
      float lastTexX = 0.0F;
      float lastTexY = 0.0F;
      byte var27;
      if (cooldownRatio < 0.125F) {
         var27 = 6;
         lastTexX = cooldownRatio / 0.25F;
         lastTexY = 0.0F;
         lastVertexX = middle + iconSize * lastTexX;
         lastVertexY = y;
         lastTexX += 0.5F;
      } else if (cooldownRatio < 0.375F) {
         var27 = 5;
         lastTexX = 1.0F;
         lastTexY = (cooldownRatio - 0.125F) / 0.25F;
         lastVertexX = right;
         lastVertexY = y + iconSize * lastTexY;
      } else if (cooldownRatio < 0.625F) {
         var27 = 4;
         lastTexX = (cooldownRatio - 0.375F) / 0.25F;
         lastTexY = 1.0F;
         lastVertexX = right - iconSize * lastTexX;
         lastVertexY = bottom;
         lastTexX = 1.0F - lastTexX;
      } else if (cooldownRatio < 0.875F) {
         var27 = 3;
         lastTexX = 0.0F;
         lastTexY = (cooldownRatio - 0.625F) / 0.25F;
         lastVertexX = x;
         lastVertexY = bottom - iconSize * lastTexY;
         lastTexY = 1.0F - lastTexY;
      } else {
         var27 = 2;
         lastTexX = (cooldownRatio - 0.875F) / 0.25F;
         lastTexY = 0.0F;
         lastVertexX = x + iconSize * lastTexX;
         lastVertexY = y;
      }

      RenderSystem.enableBlend();
      RenderSystem.setShaderTexture(0, container.getSkill().getSkillTexture());
      RenderSystem.setShader(GameRenderer::m_172817_);
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      if (canUse) {
         if (container.getStack() > 0) {
            RenderSystem.setShaderColor(0.0F, 0.64F, 0.72F, 0.8F);
         } else {
            RenderSystem.setShaderColor(0.0F, 0.5F, 0.5F, 0.6F);
         }
      } else {
         RenderSystem.setShaderColor(0.5F, 0.5F, 0.5F, 0.6F);
      }

      Tesselator tessellator = Tesselator.m_85913_();
      BufferBuilder bufferbuilder = tessellator.m_85915_();
      bufferbuilder.m_166779_(Mode.TRIANGLE_FAN, DefaultVertexFormat.f_85817_);

      for (int j = 0; j < var27; j++) {
         bufferbuilder.m_252986_(guiGraphics.m_280168_().m_85850_().m_252922_(), x + iconSize * CLOCK_POS[j].x, y + iconSize * CLOCK_POS[j].y, 0.0F)
            .m_7421_(CLOCK_POS[j].x, CLOCK_POS[j].y)
            .m_5752_();
      }

      bufferbuilder.m_252986_(guiGraphics.m_280168_().m_85850_().m_252922_(), lastVertexX, lastVertexY, 0.0F).m_7421_(lastTexX, lastTexY).m_5752_();
      tessellator.m_85914_();
      if (canUse) {
         RenderSystem.setShaderColor(0.08F, 0.79F, 0.95F, 1.0F);
      } else {
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      }

      GL11.glCullFace(1028);
      bufferbuilder.m_166779_(Mode.TRIANGLE_FAN, DefaultVertexFormat.f_85817_);

      for (int j = 0; j < 2; j++) {
         bufferbuilder.m_252986_(guiGraphics.m_280168_().m_85850_().m_252922_(), x + iconSize * CLOCK_POS[j].x, y + iconSize * CLOCK_POS[j].y, 0.0F)
            .m_7421_(CLOCK_POS[j].x, CLOCK_POS[j].y)
            .m_5752_();
      }

      for (int j = CLOCK_POS.length - 1; j >= var27; j--) {
         bufferbuilder.m_252986_(guiGraphics.m_280168_().m_85850_().m_252922_(), x + iconSize * CLOCK_POS[j].x, y + iconSize * CLOCK_POS[j].y, 0.0F)
            .m_7421_(CLOCK_POS[j].x, CLOCK_POS[j].y)
            .m_5752_();
      }

      bufferbuilder.m_252986_(guiGraphics.m_280168_().m_85850_().m_252922_(), lastVertexX, lastVertexY, 0.0F).m_7421_(lastTexX, lastTexY).m_5752_();
      tessellator.m_85914_();
      GL11.glCullFace(1029);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      if (!container.isActivated()
         || container.getSkill().getActivateType() != ActivateType.DURATION && container.getSkill().getActivateType() != ActivateType.DURATION_INFINITE) {
         if (!fullstack) {
            String s = String.valueOf((int)(cooldownRatio * 100.0F));
            int stringWidth = (gui.getFont().m_92895_(s) - 6) / 3;
            guiGraphics.drawString(gui.getFont(), s, x + 13.0F - (float)stringWidth, y + 13.0F, 16777215, true);
         }
      } else {
         String s = String.format("%.0f", (float)container.getRemainDuration() / 20.0F);
         int stringWidth = (gui.getFont().m_92895_(s) - 6) / 3;
         guiGraphics.drawString(gui.getFont(), s, x + 13.0F - (float)stringWidth, y + 13.0F, 16777215, true);
      }

      if (container.getSkill().getMaxStack() > 1) {
         String s = String.valueOf(container.getStack());
         int stringWidth = (gui.getFont().m_92895_(s) - 6) / 3;
         guiGraphics.drawString(gui.getFont(), s, x + 25.0F - (float)stringWidth, y + 22.0F, 16777215, true);
      }

      SkillDataManager manager = container.getDataManager();
      if (manager.hasData((SkillDataKey)DMCSkillDataKeys.COOLDOWN.get())) {
         int cooldown = (Integer)manager.getDataValue((SkillDataKey)DMCSkillDataKeys.COOLDOWN.get());
         if (cooldown > 0) {
            Font font = gui.getFont();
            String s = String.format("%.1fs", (double)cooldown / 20.0);
            int stringWidth = (font.m_92895_(s) - 6) / 3;
            guiGraphics.drawString(font, s, x - (float)stringWidth, y + 22.0F, 16777215, true);
         }

         guiGraphics.m_280168_().m_85849_();
      }
   }
}
