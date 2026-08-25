package com.pla.annoyingvillagers.potion;

import com.pla.annoyingvillagers.init.AnnoyingVillagersModParticleTypes;
import java.util.Random;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

public class HerobrineMobEffect extends MobEffect {
   public HerobrineMobEffect() {
      super(MobEffectCategory.HARMFUL, -6710887);
   }

   @NotNull
   public String m_19481_() {
      return "effect.annoyingvillagers.herobrine";
   }

   public boolean m_8093_() {
      return true;
   }

   public void m_6742_(@NotNull LivingEntity livingEntity, int i) {
      if (livingEntity instanceof Player player) {
         player.m_36399_(0.1F);
      }

      if (Math.random() <= 0.05) {
         float damage = Math.min(livingEntity.m_21223_(), new Random().nextFloat(0.5F, 1.5F));
         if (damage == livingEntity.m_21223_()) {
            livingEntity.m_6074_();
         } else {
            livingEntity.m_6469_(livingEntity.m_9236_().m_269111_().m_269264_(), damage);
         }
      }

      if (livingEntity.m_9236_() instanceof ServerLevel serverLevel) {
         Random random = new Random();
         double dx = (double)random.nextInt(-3, 3);
         double dy = (double)random.nextInt(-3, 3);
         double dz = (double)random.nextInt(-3, 3);
         int count = random.nextInt(50, 200);
         serverLevel.m_8767_(
            (SimpleParticleType)AnnoyingVillagersModParticleTypes.GLOWINGEYES.get(),
            livingEntity.m_20185_() + dx,
            livingEntity.m_20186_() + dy,
            livingEntity.m_20189_() + dz,
            count,
            0.0,
            0.0,
            0.0,
            0.1
         );
      }
   }

   public void m_6386_(@NotNull LivingEntity livingentity, @NotNull AttributeMap attributemap, int i) {
      super.m_6386_(livingentity, attributemap, i);
   }

   public boolean m_6584_(int i, int j) {
      return true;
   }
}
