package com.dmc.invincible_dmc.entity.summonedsword;

import com.dmc.invincible_dmc.InvincibleMod_DMC;
import com.dmc.invincible_dmc.client.sound.DMCSounds;
import com.dmc.invincible_dmc.entity.DMCEntities;
import com.dmc.invincible_dmc.entity.doppelganger.DoppelgangerEntity;
import com.dmc.invincible_dmc.entity.summonedsword.motion.SummonedSwordMotionController;
import com.dmc.invincible_dmc.entity.summonedsword.motion.SummonedSwordMotionMode;
import com.dmc.invincible_dmc.skill.weapon_innate.vergil.VergilSkill;
import com.dmc.invincible_dmc.utils.DMCLog;
import com.dmc.invincible_dmc.utils.DamageFilterUtils;
import com.merlin204.avalon.entity.vfx.VFXEntity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.EpicFightDamageTypeTags;
import yesman.epicfight.world.damagesource.StunType;

public class HeavyRainBladesEntity extends Entity implements SummonedSwordMotionController {
   private int standbyTicks = 10;
   private int launchInterval = 1;
   private int spawnsPerTick = 4;
   private double[][] ringsConfig = null;
   private static final double INITIAL_FALL_SPEED = -1.5;
   private static final double GRAVITY_ACCELERATION = 1.5;
   private static final double MAX_FALL_SPEED = -10.0;
   private static final Map<UUID, HeavyRainBladesEntity> ACTIVE_CONTROLLERS = new HashMap<>();
   private UUID ownerUUID;
   private final List<UUID> childSwords = new ArrayList<>();
   private final Queue<UUID> launchQueue = new LinkedList<>();
   private final List<UUID> launchedSwords = new ArrayList<>();
   private final Map<UUID, HeavyRainBladesEntity.StuckSwordData> stuckSwords = new HashMap<>();
   private final Map<UUID, Vec3> swordOffsets = new HashMap<>();
   private final Queue<HeavyRainBladesEntity.PendingSpawnData> spawnQueue = new LinkedList<>();
   private int launchTickCounter = 0;
   private HeavyRainBladesEntity.State currentState = HeavyRainBladesEntity.State.STANDBY;

   public HeavyRainBladesEntity(EntityType<?> pEntityType, Level pLevel) {
      super(pEntityType, pLevel);
      this.f_19811_ = true;
      this.f_19794_ = true;
   }

   public HeavyRainBladesEntity(Level level, LivingEntity owner, Vec3 targetPos) {
      super((EntityType)DMCEntities.HEAVY_RAIN_BLADES.get(), level);
      this.setOwner(owner);
      this.f_19794_ = true;
      this.f_19811_ = true;
      this.m_6034_(targetPos.f_82479_, targetPos.f_82480_ + 7.0, targetPos.f_82481_);
   }

   public static void summon(Level level, LivingEntity owner, @Nullable LivingEntity target) {
      SummonedSwordSpawner.heavyRain(level, owner, target, Integer.MAX_VALUE, 1, 4, null);
   }

   public static void summon(Level level, LivingEntity owner) {
      SummonedSwordSpawner.heavyRain(level, owner, null, Integer.MAX_VALUE, 1, 4, null);
   }

   public static void triggerLaunch(LivingEntity owner) {
      if (!owner.m_9236_().f_46443_) {
         HeavyRainBladesEntity hb = ACTIVE_CONTROLLERS.get(owner.m_20148_());
         if (hb != null && hb.m_6084_() && hb.currentState == HeavyRainBladesEntity.State.STANDBY) {
            hb.prepareToLaunch();
         }
      }
   }

   public static void summonCustom(
      Level level, LivingEntity owner, @Nullable LivingEntity target, int standbyTicks, int launchInterval, int spawnsPerTick, @Nullable double[][] customRings
   ) {
      SummonedSwordSpawner.heavyRain(level, owner, target, standbyTicks, launchInterval, spawnsPerTick, customRings);
   }

   public static void summonCustom(Level level, LivingEntity owner, int standbyTicks, int launchInterval, int spawnsPerTick, @Nullable double[][] customRings) {
      SummonedSwordSpawner.heavyRain(level, owner, null, standbyTicks, launchInterval, spawnsPerTick, customRings);
   }

   private static void spawnController(
      Level level, LivingEntity owner, Vec3 targetPos, int standbyTicks, int launchInterval, int spawnsPerTick, @Nullable double[][] rings
   ) {
      HeavyRainBladesEntity controller = new HeavyRainBladesEntity(level, owner, targetPos);
      controller.setConfig(standbyTicks, launchInterval, spawnsPerTick, rings);
      level.m_7967_(controller);
      controller.spawnChildSwords();
   }

   public void setConfig(int standbyTicks, int launchInterval, int spawnsPerTick, @Nullable double[][] customRings) {
      this.standbyTicks = standbyTicks;
      this.launchInterval = launchInterval;
      this.spawnsPerTick = spawnsPerTick;
      this.ringsConfig = customRings;
   }

   public void setOwner(LivingEntity owner) {
      this.ownerUUID = owner.m_20148_();
      ACTIVE_CONTROLLERS.put(this.ownerUUID, this);
   }

   @Nullable
   public LivingEntity getOwner() {
      return this.ownerUUID != null && this.m_9236_() instanceof ServerLevel ? (LivingEntity)((ServerLevel)this.m_9236_()).m_8791_(this.ownerUUID) : null;
   }

   private void forceLockRotationDownwards(DMCSummonedSwordEntity sword) {
      sword.m_146926_(90.0F);
      sword.m_146922_(0.0F);
      sword.f_19860_ = 90.0F;
      sword.f_19859_ = 0.0F;
      sword.setSyncXRot(90.0F);
      sword.f_20885_ = 0.0F;
      sword.f_20886_ = 0.0F;
      sword.f_20883_ = 0.0F;
      sword.f_20884_ = 0.0F;
   }

   private double[][] getRings() {
      return this.ringsConfig != null ? this.ringsConfig : new double[][]{{1.0, 0.0}, {3.0, 1.0}, {5.0, 2.0}, {3.0, 4.0}, {3.0, 5.0}};
   }

   public static boolean isActiveFor(LivingEntity owner) {
      if (owner != null && !owner.m_9236_().f_46443_) {
         HeavyRainBladesEntity hb = ACTIVE_CONTROLLERS.get(owner.m_20148_());
         return hb != null && hb.m_6084_() && hb.currentState != HeavyRainBladesEntity.State.FINISHED;
      } else {
         return false;
      }
   }

   void spawnChildSwords() {
      LivingEntity owner = this.getOwner();
      if (owner == null) {
         this.m_146870_();
      } else if (this.m_9236_() instanceof ServerLevel serverLevel) {
         double[][] var31 = this.getRings();
         int totalSwordCount = 0;

         for (double[] ring : var31) {
            totalSwordCount += (int)ring[0];
         }

         totalSwordCount = Math.min(totalSwordCount, 64);
         AABB searchBox = new AABB(
            this.m_20185_() - 4.5, this.m_20186_() - 10.0, this.m_20189_() - 4.5, this.m_20185_() + 4.5, this.m_20186_() + 2.0, this.m_20189_() + 4.5
         );
         List<LivingEntity> targets = serverLevel.m_6443_(
            LivingEntity.class,
            searchBox,
            livingEntity -> livingEntity.m_6084_()
                  && livingEntity != owner
                  && !(livingEntity instanceof DMCSummonedSwordEntity)
                  && !(livingEntity instanceof DoppelgangerEntity)
                  && !(livingEntity instanceof VFXEntity)
         );
         int currentRing = 0;
         int swordsInCurrentRing = 0;
         double currentRingBaseAngle = this.f_19796_.m_188500_() * 2.0 * Math.PI;
         int targetIdx = 0;

         for (int i = 0; i < totalSwordCount; i++) {
            Vec3 offset;
            if (targetIdx < targets.size()) {
               LivingEntity target = targets.get(targetIdx);
               targetIdx++;
               double offsetX = target.m_20185_() - this.m_20185_();
               double offsetZ = target.m_20189_() - this.m_20189_();
               double offsetY = (this.f_19796_.m_188500_() - 0.5) * 1.5;
               offset = new Vec3(offsetX, offsetY, offsetZ);
            } else {
               if (currentRing >= var31.length) {
                  break;
               }

               if (swordsInCurrentRing >= (int)var31[currentRing][0]) {
                  if (++currentRing >= var31.length) {
                     break;
                  }

                  swordsInCurrentRing = 0;
                  currentRingBaseAngle = this.f_19796_.m_188500_() * 2.0 * Math.PI;
               }

               int countInRing = Math.max(1, (int)var31[currentRing][0]);
               double baseRadius = var31[currentRing][1];
               double angleStep = (Math.PI * 2) / (double)countInRing;
               double theta = currentRingBaseAngle + (double)swordsInCurrentRing * angleStep;
               double jitter = baseRadius > 0.0 ? this.f_19796_.m_188500_() * 0.2 : 0.0;
               double finalRadius = baseRadius + jitter;
               double offsetX = finalRadius * Math.cos(theta);
               double offsetZ = finalRadius * Math.sin(theta);
               double offsetY = (this.f_19796_.m_188500_() - 0.5) * 1.5;
               offset = new Vec3(offsetX, offsetY, offsetZ);
               swordsInCurrentRing++;
            }

            this.spawnQueue.add(new HeavyRainBladesEntity.PendingSpawnData(offset));
         }
      }
   }

   private void processSpawnQueue() {
      if (!this.spawnQueue.isEmpty()) {
         LivingEntity owner = this.getOwner();
         if (owner != null && this.m_9236_() instanceof ServerLevel serverLevel) {
            int var6 = this.spawnsPerTick;

            while (var6 > 0 && !this.spawnQueue.isEmpty()) {
               HeavyRainBladesEntity.PendingSpawnData data = this.spawnQueue.poll();
               DMCSummonedSwordEntity sword = SummonedSwordSpawner.createSword(this.m_9236_(), owner, 1.45F, true);
               if (sword != null) {
                  this.swordOffsets.put(sword.m_20148_(), data.offset);
                  this.bindSwordMotion(sword, this.childSwords.size(), data.offset);
                  sword.m_20256_(Vec3.f_82478_);
                  sword.setHeavyRain(true);
                  sword.setNoAim(true);
                  this.m_9236_().m_7967_(sword);
                  this.childSwords.add(sword.m_20148_());
                  var6--;
               }
            }
         }
      }
   }

   public void m_8119_() {
      super.m_8119_();
      if (!this.m_9236_().f_46443_) {
         LivingEntity owner = this.getOwner();
         if (owner != null && !owner.m_21224_()) {
            switch (this.currentState) {
               case STANDBY:
                  this.tickStandby();
                  break;
               case LAUNCHING:
                  this.tickLaunching();
                  break;
               case FINISHED:
                  this.tickFinished();
            }
         } else {
            this.cleanup();
         }
      }
   }

   private void tickStandby() {
      this.processSpawnQueue();
      this.updateStandbySwordsPosition();
      if (this.spawnQueue.isEmpty() && this.f_19797_ >= this.standbyTicks) {
         this.prepareToLaunch();
      }

      if (this.getOwner() instanceof Player player && VergilSkill.NotHoldingYamato(player)) {
         this.cleanup();
      }
   }

   private void tickLaunching() {
      this.updateStandbySwordsPosition();
      this.checkLaunchedSwordsCollision();
      this.tickStuckSwords();
      this.launchTickCounter++;
      if (this.launchTickCounter >= this.launchInterval) {
         this.launchTickCounter = 0;
         this.launchNextSword();
      }

      if (this.launchQueue.isEmpty() && this.childSwords.isEmpty()) {
         this.currentState = HeavyRainBladesEntity.State.FINISHED;
      }
   }

   private void tickFinished() {
      this.checkLaunchedSwordsCollision();
      this.tickStuckSwords();
      if (this.launchedSwords.isEmpty() && this.stuckSwords.isEmpty()) {
         this.m_146870_();
      }
   }

   private void tickStuckSwords() {
      ServerLevel serverLevel = (ServerLevel)this.m_9236_();
      List<UUID> toRemove = new ArrayList<>();

      for (Entry<UUID, HeavyRainBladesEntity.StuckSwordData> entry : this.stuckSwords.entrySet()) {
         UUID uuid = entry.getKey();
         HeavyRainBladesEntity.StuckSwordData data = entry.getValue();
         data.ticks++;
         Entity entity = serverLevel.m_8791_(uuid);
         if (entity != null) {
            entity.f_19797_ = 0;
         }

         boolean entityVanished = entity == null || !entity.m_6084_();
         boolean timerFinished = data.ticks >= data.maxTicks;
         if (entityVanished || timerFinished) {
            LivingEntity owner = this.getOwner();
            if (owner != null) {
               DamageSource damageSource = EpicFightDamageSources.mobAttack(owner)
                  .setAnimation(null)
                  .setInitialPosition(data.pos)
                  .setStunType(StunType.NONE)
                  .setBaseImpact(2.0F)
                  .addRuntimeTag(DMCSummonedSwordPatch.HEAVY_RAIN_SWORD_DAMAGE)
                  .addRuntimeTag(EpicFightDamageTypeTags.FINISHER);
               AABB damageBox = new AABB(
                  data.pos.f_82479_ - 0.8,
                  data.pos.f_82480_ - 0.2,
                  data.pos.f_82481_ - 0.8,
                  data.pos.f_82479_ + 0.8,
                  data.pos.f_82480_ + 1.6,
                  data.pos.f_82481_ + 0.8
               );
               List<LivingEntity> targets = serverLevel.m_6443_(
                  LivingEntity.class,
                  damageBox,
                  livingEntity -> livingEntity.m_6084_()
                        && livingEntity != owner
                        && (livingEntity instanceof Monster || livingEntity instanceof Mob mob && mob.m_5448_() == owner)
               );
               float explosionDamage = 5.0F;

               for (LivingEntity target : targets) {
                  if (!DamageFilterUtils.shouldSkipTarget(owner, target)) {
                     try {
                        target.m_6469_(damageSource, explosionDamage);
                     } catch (Exception var18) {
                        DMCLog.error(
                           DMCLog.Category.SWORD, "[HeavyRain] target.hurt() failed: target={} id={}", target.m_7755_().getString(), target.m_19879_(), var18
                        );
                     }
                  }
               }
            }

            float pitch = 1.4F + this.f_19796_.m_188501_() * 0.3F;
            serverLevel.m_6263_(
               null,
               data.pos.f_82479_,
               data.pos.f_82480_,
               data.pos.f_82481_,
               (SoundEvent)DMCSounds.SUMMONED_SWORD_BREAK.get(),
               SoundSource.HOSTILE,
               0.8F,
               pitch
            );
            if (entity != null && entity.m_6084_()) {
               entity.m_146870_();
            }

            toRemove.add(uuid);
         }
      }

      for (UUID uuidx : toRemove) {
         this.stuckSwords.remove(uuidx);
      }
   }

   private void prepareToLaunch() {
      for (int batch = 0; batch < 16 && !this.spawnQueue.isEmpty(); batch++) {
         this.processSpawnQueue();
      }

      List<UUID> sortedSwords = new ArrayList<>(this.childSwords);
      sortedSwords.sort((uuid1, uuid2) -> {
         Vec3 offset1 = this.swordOffsets.getOrDefault(uuid1, Vec3.f_82478_);
         Vec3 offset2 = this.swordOffsets.getOrDefault(uuid2, Vec3.f_82478_);
         double distSq1 = offset1.f_82479_ * offset1.f_82479_ + offset1.f_82481_ * offset1.f_82481_;
         double distSq2 = offset2.f_82479_ * offset2.f_82479_ + offset2.f_82481_ * offset2.f_82481_;
         return Double.compare(distSq1, distSq2);
      });
      this.launchQueue.addAll(sortedSwords);
      this.currentState = HeavyRainBladesEntity.State.LAUNCHING;
   }

   private void launchNextSword() {
      if (!this.launchQueue.isEmpty()) {
         UUID swordUUID = this.launchQueue.poll();
         if (swordUUID != null) {
            if (((ServerLevel)this.m_9236_()).m_8791_(swordUUID) instanceof DMCSummonedSwordEntity sword) {
               this.childSwords.remove(swordUUID);
               this.launchedSwords.add(swordUUID);
               this.releaseSwordMotion(sword);
               sword.launch(null);
               this.forceLockRotationDownwards(sword);
               Vec3 initialVelocity = new Vec3(0.0, -1.5, 0.0);
               sword.m_20256_(initialVelocity);
               sword.setLockedTrajectory(initialVelocity);
               sword.m_5496_(SoundEvents.f_12520_, 1.0F, 1.0F);
            }
         }
      }
   }

   private void updateStandbySwordsPosition() {
      ServerLevel serverLevel = (ServerLevel)this.m_9236_();
      List<UUID> toRemove = new ArrayList<>();

      for (int index = 0; index < this.childSwords.size(); index++) {
         UUID swordUUID = this.childSwords.get(index);
         Entity entity = serverLevel.m_8791_(swordUUID);
         if (entity instanceof DMCSummonedSwordEntity sword && sword.isInStandby()) {
            Vec3 offset = this.swordOffsets.get(swordUUID);
            if (offset != null) {
               sword.setMotionOffset(offset);
               if (!sword.isManagedBy(this)) {
                  this.bindSwordMotion(sword, index);
               }
            }
            continue;
         }

         if (entity == null || !entity.m_6084_()) {
            toRemove.add(swordUUID);
         }
      }

      this.childSwords.removeAll(toRemove);
      this.launchQueue.removeAll(toRemove);
   }

   private void checkLaunchedSwordsCollision() {
      ServerLevel serverLevel = (ServerLevel)this.m_9236_();
      List<UUID> toRemove = new ArrayList<>();

      for (UUID uuid : this.launchedSwords) {
         Entity entity = serverLevel.m_8791_(uuid);
         if (entity instanceof DMCSummonedSwordEntity) {
            DMCSummonedSwordEntity sword = (DMCSummonedSwordEntity)entity;
            if (sword.m_213877_()) {
               toRemove.add(uuid);
            } else {
               this.forceLockRotationDownwards(sword);
               Vec3 currentPos = sword.m_20182_();
               Vec3 currentVelocity = sword.m_20184_();
               double newYVelocity = Math.max(-10.0, currentVelocity.f_82480_ - 1.5);
               Vec3 newVelocity = new Vec3(0.0, newYVelocity, 0.0);
               sword.m_20256_(newVelocity);
               sword.setLockedTrajectory(newVelocity);
               Vec3 nextPos = currentPos.m_82549_(newVelocity);
               BlockHitResult hit = serverLevel.m_45547_(new ClipContext(currentPos, nextPos, Block.COLLIDER, Fluid.NONE, sword));
               if (hit.m_6662_() == Type.BLOCK) {
                  BlockPos hitBlockPos = hit.m_82425_();
                  BlockState blockState = serverLevel.m_8055_(hitBlockPos);
                  SoundType soundType = blockState.getSoundType(serverLevel, hitBlockPos, sword);
                  Vec3 hitPos = hit.m_82450_();
                  sword.m_6034_(hitPos.f_82479_, hitPos.f_82480_ + 0.4, hitPos.f_82481_);
                  sword.m_20256_(Vec3.f_82478_);
                  sword.setLockedTrajectory(Vec3.f_82478_);
                  this.forceLockRotationDownwards(sword);
                  sword.f_19797_ = 0;
                  serverLevel.m_6263_(
                     null, hitPos.f_82479_, hitPos.f_82480_, hitPos.f_82481_, soundType.m_56775_(), SoundSource.PLAYERS, 1.2F, soundType.m_56774_() * 0.8F
                  );
                  InvincibleMod_DMC.queueServerWork(4, () -> sword.setStuckInBlock(true));
                  toRemove.add(uuid);
                  int stayTicks = 50 + this.f_19796_.m_188503_(7);
                  this.stuckSwords
                     .put(uuid, new HeavyRainBladesEntity.StuckSwordData(new Vec3(hitPos.f_82479_, hitPos.f_82480_ + 0.4, hitPos.f_82481_), stayTicks));
               }
            }
         } else {
            toRemove.add(uuid);
         }
      }

      this.launchedSwords.removeAll(toRemove);
   }

   private void cleanup() {
      if (this.ownerUUID != null) {
         ACTIVE_CONTROLLERS.remove(this.ownerUUID);
      }

      if (this.m_9236_() instanceof ServerLevel serverLevel) {
         List<UUID> allSwords = new ArrayList<>();
         allSwords.addAll(this.childSwords);
         allSwords.addAll(this.launchQueue);
         allSwords.addAll(this.launchedSwords);
         allSwords.addAll(this.stuckSwords.keySet());

         for (UUID swordUUID : allSwords) {
            Entity sword = serverLevel.m_8791_(swordUUID);
            if (sword != null) {
               sword.m_146870_();
            }
         }
      }

      this.m_146870_();
   }

   public boolean m_6094_() {
      return false;
   }

   public boolean m_6087_() {
      return false;
   }

   public boolean m_20068_() {
      return true;
   }

   public boolean m_5829_() {
      return false;
   }

   public boolean m_6469_(@NotNull DamageSource source, float amount) {
      return false;
   }

   protected void m_8097_() {
   }

   @Override
   public SummonedSwordMotionMode getMotionMode() {
      return SummonedSwordMotionMode.HEAVY_RAIN_FORMATION;
   }

   protected void m_7378_(@NotNull CompoundTag pCompound) {
      if (pCompound.m_128403_("Owner")) {
         this.ownerUUID = pCompound.m_128342_("Owner");
      }

      if (pCompound.m_128441_("State")) {
         this.currentState = HeavyRainBladesEntity.State.valueOf(pCompound.m_128461_("State"));
      }

      this.standbyTicks = pCompound.m_128451_("StandbyTicks");
      this.launchInterval = pCompound.m_128451_("LaunchInterval");
      this.spawnsPerTick = pCompound.m_128451_("SpawnsPerTick");
      this.launchTickCounter = pCompound.m_128451_("LaunchTickCounter");
      if (pCompound.m_128441_("RingsConfig")) {
         ListTag ringsList = pCompound.m_128437_("RingsConfig", 10);
         this.ringsConfig = new double[ringsList.size()][2];

         for (int i = 0; i < ringsList.size(); i++) {
            CompoundTag ringTag = ringsList.m_128728_(i);
            this.ringsConfig[i][0] = ringTag.m_128459_("Count");
            this.ringsConfig[i][1] = ringTag.m_128459_("Radius");
         }
      }

      this.childSwords.clear();
      pCompound.m_128437_("ChildSwords", 8).forEach(tag -> this.childSwords.add(UUID.fromString(tag.m_7916_())));
      this.launchQueue.clear();
      pCompound.m_128437_("LaunchQueue", 8).forEach(tag -> this.launchQueue.add(UUID.fromString(tag.m_7916_())));
      this.launchedSwords.clear();
      pCompound.m_128437_("LaunchedSwords", 8).forEach(tag -> this.launchedSwords.add(UUID.fromString(tag.m_7916_())));
      this.swordOffsets.clear();
      pCompound.m_128437_("SwordOffsets", 10).forEach(tag -> {
         CompoundTag entry = (CompoundTag)tag;
         this.swordOffsets.put(entry.m_128342_("UUID"), new Vec3(entry.m_128459_("X"), entry.m_128459_("Y"), entry.m_128459_("Z")));
      });
      this.stuckSwords.clear();
      pCompound.m_128437_("StuckSwords", 10)
         .forEach(
            tag -> {
               CompoundTag entry = (CompoundTag)tag;
               this.stuckSwords
                  .put(
                     entry.m_128342_("UUID"),
                     new HeavyRainBladesEntity.StuckSwordData(
                        new Vec3(entry.m_128459_("X"), entry.m_128459_("Y"), entry.m_128459_("Z")), entry.m_128451_("MaxTicks")
                     )
                  );
               this.stuckSwords.get(entry.m_128342_("UUID")).ticks = entry.m_128451_("Ticks");
            }
         );
      this.spawnQueue.clear();
      pCompound.m_128437_("SpawnQueue", 10).forEach(tag -> {
         CompoundTag entry = (CompoundTag)tag;
         this.spawnQueue.add(new HeavyRainBladesEntity.PendingSpawnData(new Vec3(entry.m_128459_("X"), entry.m_128459_("Y"), entry.m_128459_("Z"))));
      });
   }

   protected void m_7380_(@NotNull CompoundTag pCompound) {
      if (this.ownerUUID != null) {
         pCompound.m_128362_("Owner", this.ownerUUID);
      }

      pCompound.m_128359_("State", this.currentState.name());
      pCompound.m_128405_("StandbyTicks", this.standbyTicks);
      pCompound.m_128405_("LaunchInterval", this.launchInterval);
      pCompound.m_128405_("SpawnsPerTick", this.spawnsPerTick);
      pCompound.m_128405_("LaunchTickCounter", this.launchTickCounter);
      if (this.ringsConfig != null) {
         ListTag ringsList = new ListTag();

         for (double[] ring : this.ringsConfig) {
            CompoundTag ringTag = new CompoundTag();
            ringTag.m_128347_("Count", ring[0]);
            ringTag.m_128347_("Radius", ring[1]);
            ringsList.add(ringTag);
         }

         pCompound.m_128365_("RingsConfig", ringsList);
      }

      ListTag childList = new ListTag();
      this.childSwords.forEach(uuid -> childList.add(StringTag.m_129297_(uuid.toString())));
      pCompound.m_128365_("ChildSwords", childList);
      ListTag queueList = new ListTag();
      this.launchQueue.forEach(uuid -> queueList.add(StringTag.m_129297_(uuid.toString())));
      pCompound.m_128365_("LaunchQueue", queueList);
      ListTag launchedList = new ListTag();
      this.launchedSwords.forEach(uuid -> launchedList.add(StringTag.m_129297_(uuid.toString())));
      pCompound.m_128365_("LaunchedSwords", launchedList);
      ListTag offsetList = new ListTag();
      this.swordOffsets.forEach((uuid, vec) -> {
         CompoundTag entry = new CompoundTag();
         entry.m_128362_("UUID", uuid);
         entry.m_128347_("X", vec.f_82479_);
         entry.m_128347_("Y", vec.f_82480_);
         entry.m_128347_("Z", vec.f_82481_);
         offsetList.add(entry);
      });
      pCompound.m_128365_("SwordOffsets", offsetList);
      ListTag stuckList = new ListTag();
      this.stuckSwords.forEach((uuid, data) -> {
         CompoundTag entry = new CompoundTag();
         entry.m_128362_("UUID", uuid);
         entry.m_128405_("Ticks", data.ticks);
         entry.m_128405_("MaxTicks", data.maxTicks);
         entry.m_128347_("X", data.pos.f_82479_);
         entry.m_128347_("Y", data.pos.f_82480_);
         entry.m_128347_("Z", data.pos.f_82481_);
         stuckList.add(entry);
      });
      pCompound.m_128365_("StuckSwords", stuckList);
      ListTag spawnList = new ListTag();
      this.spawnQueue.forEach(data -> {
         CompoundTag entry = new CompoundTag();
         entry.m_128347_("X", data.offset.f_82479_);
         entry.m_128347_("Y", data.offset.f_82480_);
         entry.m_128347_("Z", data.offset.f_82481_);
         spawnList.add(entry);
      });
      pCompound.m_128365_("SpawnQueue", spawnList);
   }

   @NotNull
   public Packet<ClientGamePacketListener> m_5654_() {
      return NetworkHooks.getEntitySpawningPacket(this);
   }

   private static class PendingSpawnData {
      final Vec3 offset;

      PendingSpawnData(Vec3 offset) {
         this.offset = offset;
      }
   }

   private static enum State {
      STANDBY,
      LAUNCHING,
      FINISHED;
   }

   private static class StuckSwordData {
      int ticks;
      int maxTicks;
      Vec3 pos;

      StuckSwordData(Vec3 pos, int maxTicks) {
         this.pos = pos;
         this.ticks = 0;
         this.maxTicks = maxTicks;
      }
   }
}
