package com.pla.annoyingvillagers.skill;

import com.pla.annoyingvillagers.clazz.NullWeapon;
import com.pla.annoyingvillagers.entity.NullAxeEntity;
import com.pla.annoyingvillagers.entity.NullHoeEntity;
import com.pla.annoyingvillagers.entity.NullPickaxeEntity;
import com.pla.annoyingvillagers.entity.NullShovelEntity;
import com.pla.annoyingvillagers.entity.NullSwordEntity;
import com.pla.annoyingvillagers.gameasset.AVSkills;
import com.pla.annoyingvillagers.gameasset.AnimsEpicFightIronSpell;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import com.pla.annoyingvillagers.item.NullWeaponItem;
import com.pla.annoyingvillagers.util.EpicfightUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import reascer.wom.gameasset.WOMAnimations;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.utils.AttackResult.ResultType;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillBuilder;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.SkillCastEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class NullWeaponSkill extends WeaponInnateSkill {
   private static final UUID EVENT_UUID = UUID.fromString("08b6bf0d-2fbe-4b7a-87da-ad4c4ebb9597");
   private static final String NBT_SPENT_STACKS = "AV_NullWeaponSpentStacks";
   public static final List<String> NULL_WEAPON_KEYS = List.of("NullSwordUUID", "NullAxeUUID", "NullPickaxeUUID", "NullHoeUUID", "NullShovelUUID");

   public static NullWeapon pickRandomNullWeapon(ServerLevel serverLevel, CompoundTag data, RandomSource rand) {
      List<NullWeapon> candidates = new ArrayList<>();

      for (String key : NULL_WEAPON_KEYS) {
         if (data.m_128403_(key)) {
            Entity entity = serverLevel.m_8791_(data.m_128342_(key));
            if (entity instanceof NullWeapon) {
               NullWeapon nullWeapon = (NullWeapon)entity;
               if (nullWeapon.m_6084_() && !nullWeapon.m_213877_()) {
                  candidates.add(nullWeapon);
               }
            }
         }
      }

      return candidates.isEmpty() ? null : candidates.get(rand.m_188503_(candidates.size()));
   }

   public NullWeaponSkill(SkillBuilder<? extends WeaponInnateSkill> builder) {
      super(builder);
   }

   public void executeOnServer(SkillContainer skillContainer, FriendlyByteBuf friendlyByteBuf) {
      if (!skillContainer.isActivated()) {
         skillContainer.getExecutor().playAnimationSynchronized(AnimsEpicFightIronSpell.CASTING_ONE_HAND_TOP, 0.0F);
         Player player = (Player)skillContainer.getExecutor().getOriginal();
         int stack = player.getPersistentData().m_128451_("AV_NullWeaponSpentStacks");
         player.getPersistentData().m_128473_("AV_NullWeaponSpentStacks");
         if (player.m_9236_() instanceof ServerLevel serverLevel) {
            List<String> weaponKeys = List.of("NullAxeUUID", "NullPickaxeUUID", "NullShovelUUID", "NullHoeUUID", "NullSwordUUID");
            List<String> shuffledKeys = new ArrayList<>(weaponKeys);
            Collections.shuffle(shuffledKeys, new Random());

            for (int i = 0; i < stack; i++) {
               String key = shuffledKeys.get(i);
               if (player.getPersistentData().m_128403_(key)) {
                  UUID uuid = player.getPersistentData().m_128342_(key);
                  if (serverLevel.m_8791_(uuid) instanceof NullWeapon nullWeapon) {
                     nullWeapon.setReleased(true);
                  }
               }
            }
         }

         super.executeOnServer(skillContainer, friendlyByteBuf);
         skillContainer.activate();
      }
   }

   public boolean resourcePredicate(PlayerPatch<?> playerPatch, SkillCastEvent event) {
      if (playerPatch instanceof ServerPlayerPatch serverPatch) {
         SkillContainer container = serverPatch.getSkill(AVSkills.NULL_WEAPON);
         if (container != null && container.getSkill() == this) {
            Player player = (Player)serverPatch.getOriginal();
            if (player.m_7500_()) {
               return true;
            } else {
               int available = container.getStack();
               if (available <= 0) {
                  return false;
               } else {
                  player.getPersistentData().m_128405_("AV_NullWeaponSpentStacks", available);
                  Skill.setSkillStackSynchronize(container, 0);
                  Skill.setSkillConsumptionSynchronize(container, 0.0F);
                  return true;
               }
            }
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public void onInitiate(SkillContainer container) {
      super.onInitiate(container);
      container.getExecutor()
         .getEventListener()
         .addEventListener(
            EventType.BASIC_ATTACK_EVENT,
            EVENT_UUID,
            event -> {
               if (!((ServerPlayerPatch)event.getPlayerPatch()).isLogicalClient()) {
                  SkillContainer skillContainer = ((ServerPlayerPatch)event.getPlayerPatch()).getSkill(this);
                  if (!skillContainer.isActivated()) {
                     event.setCanceled(true);
                     PlayerPatch<?> playerPatch = event.getPlayerPatch();
                     AssetAccessor<? extends StaticAnimation> dynamicAnimation = Objects.requireNonNull(playerPatch.getAnimator().getPlayerFor(null))
                        .getRealAnimation();
                     if (dynamicAnimation != null && dynamicAnimation == WOMAnimations.ANTITHEUS_ASCENDED_AUTO_2) {
                        skillContainer.getExecutor().playAnimationSynchronized(WOMAnimations.ANTITHEUS_ASCENDED_AUTO_3, 0.0F);
                     } else if (dynamicAnimation != null && dynamicAnimation == WOMAnimations.ANTITHEUS_ASCENDED_AUTO_1) {
                        skillContainer.getExecutor().playAnimationSynchronized(WOMAnimations.ANTITHEUS_ASCENDED_AUTO_2, 0.0F);
                     } else {
                        skillContainer.getExecutor().playAnimationSynchronized(WOMAnimations.ANTITHEUS_ASCENDED_AUTO_1, 0.0F);
                     }
                  }
               }
            }
         );
      container.getExecutor()
         .getEventListener()
         .addEventListener(
            EventType.TAKE_DAMAGE_EVENT_ATTACK,
            EVENT_UUID,
            pre -> {
               DamageSource damageSource = pre.getDamageSource();
               if (!damageSource.m_276093_(DamageTypes.f_268515_)
                  && !damageSource.m_276093_(DamageTypes.f_268565_)
                  && !damageSource.m_276093_(DamageTypes.f_268468_)
                  && !damageSource.m_276093_(DamageTypes.f_268631_)
                  && !damageSource.m_276093_(DamageTypes.f_268671_)
                  && !damageSource.m_276093_(DamageTypes.f_268724_)
                  && !damageSource.m_276093_(DamageTypes.f_268722_)) {
                  Player player = (Player)((ServerPlayerPatch)pre.getPlayerPatch()).getOriginal();
                  if (player.m_9236_() instanceof ServerLevel serverLevel) {
                     CompoundTag data = player.getPersistentData();
                     if (new Random().nextFloat() > (container.isActivated() ? 0.5F : 0.25F)) {
                        return;
                     }

                     NullWeapon nullWeapon = pickRandomNullWeapon(serverLevel, data, player.m_217043_());
                     if (nullWeapon != null) {
                        nullWeapon.m_7678_(player.m_20185_(), player.m_20186_(), player.m_20189_(), nullWeapon.m_146908_(), nullWeapon.m_146909_());
                        pre.setCanceled(true);
                        pre.setResult(ResultType.BLOCKED);
                        EpicfightUtil.damageBlocked(pre.getDamageSource(), player, serverLevel);
                        nullWeapon.spinfor5seconds();
                        ((HitParticleType)EpicFightParticles.HIT_BLUNT.get())
                           .spawnParticleWithArgument(serverLevel, HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, player, pre.getDamageSource().m_7639_());
                     }
                  }
               }
            }
         );
      container.getExecutor().getEventListener().addEventListener(EventType.SKILL_CAST_EVENT, EVENT_UUID, event -> {
         Player player = (Player)container.getExecutor().getOriginal();
         Skill skill = event.getSkillContainer().getSkill();
         if (skill.getCategory() == SkillCategories.GUARD) {
            for (String key : List.of("NullAxeUUID", "NullPickaxeUUID", "NullShovelUUID", "NullHoeUUID", "NullSwordUUID")) {
               if (player.getPersistentData().m_128403_(key)) {
                  Level patt10269$temp = player.m_9236_();
                  if (patt10269$temp instanceof ServerLevel) {
                     ServerLevel serverLevel = (ServerLevel)patt10269$temp;
                     UUID uuid = player.getPersistentData().m_128342_(key);
                     Entity entity = serverLevel.m_8791_(uuid);
                     if (entity instanceof NullWeapon) {
                        NullWeapon nullWeapon = (NullWeapon)entity;
                        if (!nullWeapon.isReleased()) {
                           nullWeapon.setSpinning(true);
                        }
                     }
                  }
               }
            }
         }
      });
   }

   public void onRemoved(SkillContainer container) {
      container.getExecutor().getEventListener().removeListener(EventType.TAKE_DAMAGE_EVENT_ATTACK, EVENT_UUID);
      container.getExecutor().getEventListener().removeListener(EventType.SKILL_CAST_EVENT, EVENT_UUID);
      container.getExecutor().getEventListener().removeListener(EventType.BASIC_ATTACK_EVENT, EVENT_UUID);
   }

   public void cancelOnServer(SkillContainer container, FriendlyByteBuf args) {
      container.deactivate();
      Player player = (Player)container.getExecutor().getOriginal();

      for (String key : List.of("NullAxeUUID", "NullPickaxeUUID", "NullShovelUUID", "NullHoeUUID", "NullSwordUUID")) {
         if (player.getPersistentData().m_128403_(key)) {
            Level uuid = player.m_9236_();
            if (uuid instanceof ServerLevel) {
               ServerLevel serverLevel = (ServerLevel)uuid;
               UUID uuidx = player.getPersistentData().m_128342_(key);
               if (serverLevel.m_8791_(uuidx) instanceof NullWeapon nullWeapon) {
                  nullWeapon.setReleased(false);
               }
            }
         }
      }

      super.cancelOnServer(container, args);
   }

   public void executeOnClient(SkillContainer container, FriendlyByteBuf args) {
      super.executeOnClient(container, args);
      container.activate();
   }

   public void cancelOnClient(SkillContainer container, FriendlyByteBuf args) {
      super.cancelOnClient(container, args);
      container.deactivate();
   }

   private static void removeTrackedEntityIfWrongType(
      ServerLevel serverLevel, CompoundTag persistentData, String uuidKey, Class<? extends Entity> expectedClass
   ) {
      if (persistentData.m_128403_(uuidKey)) {
         Entity trackedEntity = serverLevel.m_8791_(persistentData.m_128342_(uuidKey));
         if (trackedEntity != null && (!expectedClass.isInstance(trackedEntity) || !trackedEntity.m_6084_())) {
            persistentData.m_128473_(uuidKey);
         }
      }
   }

   public void updateContainer(SkillContainer container) {
      super.updateContainer(container);
      if (container.getExecutor().getValidItemInHand(InteractionHand.MAIN_HAND) != null) {
         LivingEntityPatch<?> livingEntityPatch = container.getExecutor();
         if (livingEntityPatch == null) {
            return;
         }

         if (livingEntityPatch.getAnimator() == null) {
            return;
         }

         if (livingEntityPatch.getArmature() == null) {
            return;
         }

         if (Armatures.BIPED.get() == null || ((HumanoidArmature)Armatures.BIPED.get()).toolL == null) {
            return;
         }

         if (livingEntityPatch.getOriginal() == null) {
            return;
         }

         byte poseSampleCount = 3;
         float poseStep = 1.0F / (float)(poseSampleCount - 1);
         float poseProgress = 0.0F;

         for (int poseSampleIndex = 0; poseSampleIndex < poseSampleCount; poseSampleIndex++) {
            OpenMatrix4f toolLeftTransform = livingEntityPatch.getArmature()
               .getBoundTransformFor(livingEntityPatch.getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).toolL);
            toolLeftTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(-((float)Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 180.0F))), new Vec3f(0.0F, 1.0F, 0.0F)),
               toolLeftTransform,
               toolLeftTransform
            );

            for (int particleIndex = 0; particleIndex < 1; particleIndex++) {
               ((LivingEntity)livingEntityPatch.getOriginal())
                  .m_9236_()
                  .m_7106_(
                     (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                     (double)toolLeftTransform.m30 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_(),
                     (double)toolLeftTransform.m31 + ((Player)livingEntityPatch.getOriginal()).m_20186_(),
                     (double)toolLeftTransform.m32 + ((Player)livingEntityPatch.getOriginal()).m_20189_(),
                     (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                     (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                     (double)((new Random().nextFloat() - 0.5F) * 0.15F)
                  );
            }

            for (int var25 = 0; var25 < 1; var25++) {
               ((LivingEntity)livingEntityPatch.getOriginal())
                  .m_9236_()
                  .m_7106_(
                     (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                     (double)toolLeftTransform.m30 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_(),
                     (double)toolLeftTransform.m31 + ((Player)livingEntityPatch.getOriginal()).m_20186_(),
                     (double)toolLeftTransform.m32 + ((Player)livingEntityPatch.getOriginal()).m_20189_(),
                     0.0,
                     0.0,
                     0.0
                  );
            }

            poseProgress += poseStep;
         }

         poseProgress = 0.0F;

         for (int var27 = 0; var27 < poseSampleCount; var27++) {
            OpenMatrix4f jointTransform = livingEntityPatch.getArmature()
               .getBoundTransformFor(livingEntityPatch.getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).toolR);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 1.8F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(-((float)Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 180.0F))), new Vec3f(0.0F, 1.0F, 0.0F)),
               jointTransform,
               jointTransform
            );
            jointTransform.translate(new Vec3f(0.0F, 0.0F, -(new Random().nextFloat() * 4.0F)));
            ((LivingEntity)livingEntityPatch.getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_(),
                  (double)jointTransform.m31 + ((Player)livingEntityPatch.getOriginal()).m_20186_(),
                  (double)jointTransform.m32 + ((Player)livingEntityPatch.getOriginal()).m_20189_(),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            ((LivingEntity)livingEntityPatch.getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_(),
                  (double)jointTransform.m31 + ((Player)livingEntityPatch.getOriginal()).m_20186_(),
                  (double)jointTransform.m32 + ((Player)livingEntityPatch.getOriginal()).m_20189_(),
                  0.0,
                  0.0,
                  0.0
               );
            poseProgress += poseStep;
         }

         for (int particleIndex = 0; particleIndex < 14; particleIndex++) {
            ((LivingEntity)livingEntityPatch.getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_(),
                  ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() + 0.03F,
                  ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_(),
                  (double)((new Random().nextFloat() - 0.5F) * 0.65F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.05F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.65F)
               );
         }

         poseStep = 1.0F;
         poseProgress = 0.0F;

         for (int var28 = 0; var28 < poseSampleCount; var28++) {
            OpenMatrix4f jointTransform = livingEntityPatch.getArmature()
               .getBoundTransformFor(livingEntityPatch.getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).head);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(-((float)Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 180.0F))), new Vec3f(0.0F, 1.0F, 0.0F)),
               jointTransform,
               jointTransform
            );
            ((LivingEntity)livingEntityPatch.getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m31 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() + (double)((new Random().nextFloat() + 0.1F) * 0.55F),
                  (double)jointTransform.m32 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 1.0F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            poseProgress += poseStep;
         }

         poseProgress = 0.0F;

         for (int var29 = 0; var29 < poseSampleCount; var29++) {
            OpenMatrix4f jointTransform = livingEntityPatch.getArmature()
               .getBoundTransformFor(livingEntityPatch.getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).chest);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(-((float)Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 180.0F))), new Vec3f(0.0F, 1.0F, 0.0F)),
               jointTransform,
               jointTransform
            );
            ((LivingEntity)livingEntityPatch.getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m31 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m32 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 1.0F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            poseProgress += poseStep;
         }

         poseProgress = 0.0F;

         for (int var30 = 0; var30 < poseSampleCount; var30++) {
            OpenMatrix4f jointTransform = livingEntityPatch.getArmature()
               .getBoundTransformFor(livingEntityPatch.getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).armL);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(-((float)Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 180.0F))), new Vec3f(0.0F, 1.0F, 0.0F)),
               jointTransform,
               jointTransform
            );
            ((LivingEntity)livingEntityPatch.getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m31 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m32 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 1.0F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            poseProgress += poseStep;
         }

         poseProgress = 0.0F;

         for (int var31 = 0; var31 < poseSampleCount; var31++) {
            OpenMatrix4f jointTransform = livingEntityPatch.getArmature()
               .getBoundTransformFor(livingEntityPatch.getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).armR);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(-((float)Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 180.0F))), new Vec3f(0.0F, 1.0F, 0.0F)),
               jointTransform,
               jointTransform
            );
            ((LivingEntity)livingEntityPatch.getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m31 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m32 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 1.0F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            poseProgress += poseStep;
         }

         poseProgress = 0.0F;

         for (int var32 = 0; var32 < poseSampleCount; var32++) {
            OpenMatrix4f jointTransform = livingEntityPatch.getArmature()
               .getBoundTransformFor(livingEntityPatch.getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).torso);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(-((float)Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 180.0F))), new Vec3f(0.0F, 1.0F, 0.0F)),
               jointTransform,
               jointTransform
            );
            ((LivingEntity)livingEntityPatch.getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m31 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m32 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 1.0F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            poseProgress += poseStep;
         }

         poseProgress = 0.0F;

         for (int var33 = 0; var33 < poseSampleCount; var33++) {
            OpenMatrix4f jointTransform = livingEntityPatch.getArmature()
               .getBoundTransformFor(livingEntityPatch.getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).thighL);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(-((float)Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 180.0F))), new Vec3f(0.0F, 1.0F, 0.0F)),
               jointTransform,
               jointTransform
            );
            ((LivingEntity)livingEntityPatch.getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m31 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m32 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 1.0F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            poseProgress += poseStep;
         }

         poseProgress = 0.0F;

         for (int var34 = 0; var34 < poseSampleCount; var34++) {
            OpenMatrix4f jointTransform = livingEntityPatch.getArmature()
               .getBoundTransformFor(livingEntityPatch.getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).thighR);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(-((float)Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 180.0F))), new Vec3f(0.0F, 1.0F, 0.0F)),
               jointTransform,
               jointTransform
            );
            ((LivingEntity)livingEntityPatch.getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m31 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m32 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 1.0F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            poseProgress += poseStep;
         }

         poseProgress = 0.0F;

         for (int var35 = 0; var35 < poseSampleCount; var35++) {
            OpenMatrix4f jointTransform = livingEntityPatch.getArmature()
               .getBoundTransformFor(livingEntityPatch.getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).legL);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(-((float)Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 180.0F))), new Vec3f(0.0F, 1.0F, 0.0F)),
               jointTransform,
               jointTransform
            );
            ((LivingEntity)livingEntityPatch.getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m31 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m32 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 1.0F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            poseProgress += poseStep;
         }

         poseProgress = 0.0F;

         for (int var36 = 0; var36 < poseSampleCount; var36++) {
            OpenMatrix4f jointTransform = livingEntityPatch.getArmature()
               .getBoundTransformFor(livingEntityPatch.getAnimator().getPose(poseProgress), ((HumanoidArmature)Armatures.BIPED.get()).legR);
            jointTransform.translate(new Vec3f(0.0F, 0.0F, 0.0F));
            OpenMatrix4f.mul(
               new OpenMatrix4f()
                  .rotate(-((float)Math.toRadians((double)(((LivingEntity)livingEntityPatch.getOriginal()).f_20884_ + 180.0F))), new Vec3f(0.0F, 1.0F, 0.0F)),
               jointTransform,
               jointTransform
            );
            ((LivingEntity)livingEntityPatch.getOriginal())
               .m_9236_()
               .m_7106_(
                  (ParticleOptions)AnnoyingVillagersModParticleTypes.NULL.get(),
                  (double)jointTransform.m30 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20185_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m31 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20186_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)jointTransform.m32 + ((LivingEntity)livingEntityPatch.getOriginal()).m_20189_() + (double)((new Random().nextFloat() - 0.5F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F),
                  (double)((new Random().nextFloat() - 1.0F) * 0.55F),
                  (double)((new Random().nextFloat() - 0.5F) * 0.15F)
               );
            poseProgress += poseStep;
         }
      }

      if (!container.getExecutor().isLogicalClient()) {
         ServerPlayerPatch serverPlayerPatch = container.getServerExecutor();
         Player player = (Player)serverPlayerPatch.getOriginal();
         ServerLevel serverLevel = (ServerLevel)player.m_9236_();
         CompoundTag data = player.getPersistentData();
         if (player.f_19797_ >= 40 && player.f_19797_ % 10 == 0) {
            if (((Player)container.getExecutor().getOriginal()).m_21205_().m_41720_() instanceof NullWeaponItem) {
               ensureWeapon(
                  serverLevel,
                  player,
                  data,
                  "NullSwordUUID",
                  (EntityType<? extends NullWeapon>)AnnoyingVillagersModEntities.NULL_SWORD.get(),
                  NullSwordEntity.class
               );
               ensureWeapon(
                  serverLevel, player, data, "NullAxeUUID", (EntityType<? extends NullWeapon>)AnnoyingVillagersModEntities.NULL_AXE.get(), NullAxeEntity.class
               );
               ensureWeapon(
                  serverLevel,
                  player,
                  data,
                  "NullPickaxeUUID",
                  (EntityType<? extends NullWeapon>)AnnoyingVillagersModEntities.NULL_PICKAXE.get(),
                  NullPickaxeEntity.class
               );
               ensureWeapon(
                  serverLevel,
                  player,
                  data,
                  "NullShovelUUID",
                  (EntityType<? extends NullWeapon>)AnnoyingVillagersModEntities.NULL_SHOVEL.get(),
                  NullShovelEntity.class
               );
               ensureWeapon(
                  serverLevel, player, data, "NullHoeUUID", (EntityType<? extends NullWeapon>)AnnoyingVillagersModEntities.NULL_HOE.get(), NullHoeEntity.class
               );
            }

            this.teleportWeapon("NullSwordUUID", serverLevel, data);
            this.teleportWeapon("NullAxeUUID", serverLevel, data);
            this.teleportWeapon("NullPickaxeUUID", serverLevel, data);
            this.teleportWeapon("NullHoeUUID", serverLevel, data);
            this.teleportWeapon("NullShovelUUID", serverLevel, data);
         }
      }
   }

   private static void ensureWeapon(
      ServerLevel level, Player player, CompoundTag data, String key, EntityType<? extends NullWeapon> type, Class<? extends Entity> expectedClass
   ) {
      removeTrackedEntityIfWrongType(level, data, key, expectedClass);
      if (!data.m_128403_(key)) {
         NullWeapon nullWeapon = (NullWeapon)type.m_20615_(level);
         if (nullWeapon != null) {
            nullWeapon.summonNullWeaponForPlayer(key, level, player);
         }
      }
   }

   private void teleportWeapon(String uuidNbt, ServerLevel serverLevel, CompoundTag compoundTag) {
      if (compoundTag.m_128403_(uuidNbt) && serverLevel.m_8791_(compoundTag.m_128342_(uuidNbt)) instanceof NullWeapon nullWeapon) {
         nullWeapon.processTeleportByPlayer();
      }
   }
}
