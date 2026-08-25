package org.merlin204.mimic.entity.proteus;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent.BossBarColor;
import net.minecraft.world.BossEvent.BossBarOverlay;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.merlin204.mimic.client.gui.BossBar;
import org.merlin204.mimic.client.sound.ProteusBossMusicManager;
import org.merlin204.mimic.entity.MimicEntity;
import org.merlin204.mimic.entity.ai.MimicTargetSelector;
import org.merlin204.mimic.entity.shadow.ShadowMimicEntity;
import org.merlin204.mimic.network.PacketHandler;
import org.merlin204.mimic.network.PacketRelay;
import org.merlin204.mimic.network.packet.client.SyncBossBarPacket;
import yesman.epicfight.api.asset.AssetAccessor;
import yesman.epicfight.api.client.model.SkinnedMesh;
import yesman.epicfight.api.client.model.Meshes.MeshAccessor;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class ProteusEntity extends MimicEntity {
   protected static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.m_135353_(ProteusEntity.class, EntityDataSerializers.f_135028_);
   private final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("mimic", "textures/entity/mimic.png");
   private final ResourceLocation TEXTURE_L = ResourceLocation.fromNamespaceAndPath("mimic", "textures/entity/mimic_l.png");
   private final ResourceLocation TEXTURE_END = ResourceLocation.fromNamespaceAndPath("mimic", "textures/entity/mimic_end.png");
   private final ResourceLocation TEXTURE_L_END = ResourceLocation.fromNamespaceAndPath("mimic", "textures/entity/mimic_l_end.png");
   protected final ServerBossEvent bossInfo;
   private boolean musicStarted = false;

   public ProteusEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
      super(entityType, level);
      this.bossInfo = new ServerBossEvent(this.m_5446_(), BossBarColor.PURPLE, BossBarOverlay.PROGRESS);
      if (!this.m_9236_().f_46443_) {
         BossBar.BOSSES.put(this.bossInfo.m_18860_(), this.m_19879_());
         PacketRelay.sendToAll(PacketHandler.INSTANCE, new SyncBossBarPacket(this.bossInfo.m_18860_(), this.m_19879_()));
      }
   }

   @Override
   public void m_8119_() {
      super.m_8119_();
      if (this.m_9236_().f_46443_) {
         if (!this.musicStarted) {
            this.startMusic();
         }
      } else {
         this.bossInfo.m_142711_(this.m_21223_() / this.m_21233_());
      }
   }

   @Override
   public boolean m_6469_(@NotNull DamageSource damageSource, float amount) {
      if (this.getPhase() == 2) {
         return false;
      } else if (this.getPhase() == 1 && this.m_21223_() < this.m_21233_() * 0.5F) {
         return false;
      } else {
         if (damageSource.m_7639_() instanceof ShadowMimicEntity shadowMimicEntity && shadowMimicEntity.getOwner() == this) {
            return false;
         }

         return this.getPhase() == 3 ? super.m_6469_(damageSource, amount * 0.5F) : super.m_6469_(damageSource, amount);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void startMusic() {
      Player player = Minecraft.m_91087_().f_91074_;
      if (player != null) {
         ProteusBossMusicManager.startBossMusic(player, this);
         this.musicStarted = true;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void stopMusic() {
      ProteusBossMusicManager.stopBossMusic();
      this.musicStarted = false;
   }

   public void m_6667_(DamageSource cause) {
      super.m_6667_(cause);
      if (this.m_9236_().f_46443_) {
         this.stopMusic();
      }
   }

   public void onRemovedFromWorld() {
      super.onRemovedFromWorld();
      if (this.m_9236_().f_46443_) {
         this.stopMusic();
      }
   }

   public void setPhase(int phase) {
      this.f_19804_.m_135381_(PHASE, phase);
   }

   public int getPhase() {
      return (Integer)this.f_19804_.m_135370_(PHASE);
   }

   protected void m_8097_() {
      super.m_8097_();
      this.f_19804_.m_135372_(PHASE, 1);
   }

   @Override
   public void m_7380_(@NotNull CompoundTag tag) {
      super.m_7380_(tag);
      tag.m_128405_("phase", this.getPhase());
   }

   @Override
   public void m_7378_(@NotNull CompoundTag tag) {
      super.m_7378_(tag);
      this.setPhase(tag.m_128451_("phase"));
   }

   protected void m_8024_() {
      super.m_8024_();
      if (!this.m_9236_().m_5776_()) {
         this.bossInfo.m_142711_(this.m_21223_() / this.m_21233_());
      }
   }

   protected void m_8099_() {
      super.m_8099_();
      this.f_21346_.m_25352_(0, new MimicTargetSelector(this));
   }

   public void m_6457_(@NotNull ServerPlayer player) {
      super.m_6457_(player);
      this.bossInfo.m_6543_(player);
   }

   public void m_6452_(@NotNull ServerPlayer player) {
      super.m_6452_(player);
      this.bossInfo.m_6539_(player);
   }

   public static AttributeSupplier getDefaultAttribute() {
      return Mob.m_21552_()
         .m_22268_(Attributes.f_22276_, 1000.0)
         .m_22268_(Attributes.f_22281_, 5.0)
         .m_22268_(Attributes.f_22284_, 30.0)
         .m_22268_(Attributes.f_22277_, 72.0)
         .m_22268_(Attributes.f_22278_, 1000.0)
         .m_22268_(Attributes.f_22283_, 1.5)
         .m_22268_((Attribute)EpicFightAttributes.IMPACT.get(), 5.0)
         .m_22268_((Attribute)EpicFightAttributes.ARMOR_NEGATION.get(), 50.0)
         .m_22268_((Attribute)EpicFightAttributes.MAX_STRIKES.get(), 50.0)
         .m_22268_((Attribute)EpicFightAttributes.WEIGHT.get(), 0.0)
         .m_22265_();
   }

   @Nullable
   @Override
   public ResourceLocation getTexture() {
      return this.getPhase() == 3 ? this.TEXTURE_END : this.TEXTURE;
   }

   @Nullable
   @Override
   public ResourceLocation getLitTexture() {
      return this.getPhase() == 3 ? this.TEXTURE_L_END : this.TEXTURE_L;
   }

   @Nullable
   @Override
   public AssetAccessor<? extends SkinnedMesh> getMesh() {
      return this.getPhase() == 3
         ? MeshAccessor.create("mimic", "entity/mimic_end", jsonModelLoader -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new))
         : MeshAccessor.create("mimic", "entity/mimic", jsonModelLoader -> jsonModelLoader.loadSkinnedMesh(SkinnedMesh::new));
   }
}
