package com.pla.annoyingvillagers.item;

import com.pla.annoyingvillagers.AnnoyingVillagers;
import com.pla.annoyingvillagers.entity.EnderAegisProjectile;
import com.pla.annoyingvillagers.gameasset.AVAnimations;
import com.pla.annoyingvillagers.gameasset.AVSkills;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModEntities;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModItems;
import com.pla.annoyingvillagers.init.AnnoyingVillagersModSounds;
import com.pla.annoyingvillagers.network.ClientboundEnderAegisSparkFx;
import com.pla.annoyingvillagers.util.HerobrineUtil;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class EnderAegisItem extends SwordItem {
   public EnderAegisItem() {
      super(new Tier() {
         public int m_6609_() {
            return 1561;
         }

         public float m_6624_() {
            return 4.0F;
         }

         public float m_6631_() {
            return 2.0F;
         }

         public int m_6604_() {
            return 1;
         }

         public int m_6601_() {
            return 2;
         }

         @NotNull
         public Ingredient m_6282_() {
            return Ingredient.m_43929_(new ItemLike[]{(ItemLike)AnnoyingVillagersModItems.ELITE_OBSIDIAN.get()});
         }
      }, 3, -2.3F, new Properties().m_41486_());
   }

   public static void shieldShoot(Level level, Entity entity) {
      if (level instanceof ServerLevel serverLevel) {
         Vec3 eye = entity.m_20299_(1.0F);
         Vec3 look = entity.m_20154_();
         if (entity instanceof Mob mob) {
            LivingEntity target = mob.m_5448_();
            if (target != null) {
               look = target.m_20299_(1.0F).m_82546_(eye);
            }
         } else if (entity instanceof Player) {
            look = new Vec3(look.f_82479_, 0.0, look.f_82481_);
         }

         if (look.m_82556_() < 1.0E-6) {
            float yawRad = (float)Math.toRadians((double)entity.m_146908_());
            look = new Vec3((double)(-Mth.m_14031_(yawRad)), 0.0, (double)Mth.m_14089_(yawRad));
         }

         Vec3 forward = look.m_82541_();
         Vec3 up = new Vec3(0.0, 1.0, 0.0);
         Vec3 right = forward.m_82537_(up).m_82541_();
         double spawnForward = 0.0;
         double spread = 0.05;
         float velocity = 1.2F;
         float inaccuracy = 0.0F;
         Vec3[] offsets = new Vec3[]{Vec3.f_82478_, up, up.m_82490_(-1.0), right.m_82490_(-1.0), right};

         for (Vec3 off : offsets) {
            Vec3 spawnPos = eye.m_82549_(forward.m_82490_(spawnForward)).m_82549_(off.m_82490_(0.15));
            Vec3 dir = forward.m_82549_(off.m_82490_(spread)).m_82541_();
            EnderAegisProjectile proj = new EnderAegisProjectile(
               (EntityType<? extends EnderAegisProjectile>)AnnoyingVillagersModEntities.ENDER_AEGIS_PROJECTILE.get(), level
            );
            proj.m_5602_(entity);
            proj.m_36781_(15.0);
            proj.m_36735_(5);
            proj.m_20225_(true);
            proj.m_36767_((byte)5);
            proj.m_6034_(spawnPos.f_82479_, spawnPos.f_82480_, spawnPos.f_82481_);
            proj.m_6686_(dir.f_82479_, dir.f_82480_, dir.f_82481_, velocity, inaccuracy);
            serverLevel.m_7967_(proj);
         }

         Vec3 sparkFrom = eye.m_82520_(0.0, -1.0, 0.0);
         Vec3 sparkTo = eye.m_82549_(forward.m_82490_(1.2)).m_82520_(0.0, -1.0, 0.0);
         AnnoyingVillagers.PACKET_HANDLER
            .send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new ClientboundEnderAegisSparkFx(sparkFrom, sparkTo));
         level.m_5594_(null, entity.m_20183_(), (SoundEvent)AnnoyingVillagersModSounds.COOL_DOWN.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
         level.m_5594_(null, entity.m_20183_(), (SoundEvent)AnnoyingVillagersModSounds.ENDER_SHOT.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
         level.m_5594_(null, entity.m_20183_(), (SoundEvent)AnnoyingVillagersModSounds.BLOOM.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);
         LivingEntityPatch<?> livingentitypatch = (LivingEntityPatch<?>)EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
         if (livingentitypatch != null) {
            livingentitypatch.playAnimationSynchronized(AVAnimations.IDLE_BREAK, 0.0F);
         }
      }
   }

   public void m_6883_(@NotNull ItemStack itemstack, @NotNull Level level, @NotNull Entity entity, int i, boolean flag) {
      super.m_6883_(itemstack, level, entity, i, flag);
      if (flag && itemstack.m_41783_() != null && itemstack.m_41783_().m_128471_("SecondForm")) {
         HerobrineUtil.spawnEliteEffect(level, entity.m_20185_(), entity.m_20186_(), entity.m_20189_(), entity);
      }

      if (entity instanceof Player player) {
         PlayerPatch<?> playerPatch = (PlayerPatch<?>)EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
         if (playerPatch instanceof ServerPlayerPatch serverPlayerPatch) {
            SkillContainer skillContainer = serverPlayerPatch.getSkill(AVSkills.ENDER_AEGIS);
            if (skillContainer != null && itemstack.m_41783_() != null) {
               if (!skillContainer.isActivated() && itemstack.m_41783_().m_128471_("SecondForm")) {
                  itemstack.m_41783_().m_128379_("SecondForm", false);
               }

               if (skillContainer.isActivated() && !itemstack.m_41783_().m_128471_("SecondForm")) {
                  itemstack.m_41783_().m_128379_("SecondForm", true);
               }
            }
         }
      }
   }

   public void m_7373_(@NotNull ItemStack itemstack, Level level, @NotNull List<Component> list, @NotNull TooltipFlag tooltipflag) {
      super.m_7373_(itemstack, level, list, tooltipflag);
      list.add(Component.m_237113_(Component.m_237115_("tooltip.annoyingvillagers.ender_aegis").getString()));
   }
}
