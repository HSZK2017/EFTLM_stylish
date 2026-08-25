package org.merlin204.mimic.entity.ai;

import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.merlin204.mimic.entity.MimicEntity;
import org.merlin204.mimic.entity.proteus.ProteusEntity;
import org.merlin204.mimic.entity.shadow.ShadowMimicEntity;

public class MimicTargetSelector extends NearestAttackableTargetGoal<LivingEntity> {
   private int timer = 0;

   public MimicTargetSelector(MimicEntity mimicEntity) {
      super(mimicEntity, LivingEntity.class, false);
   }

   public boolean m_8036_() {
      this.m_26073_();
      return this.f_26050_ != null;
   }

   protected double m_7623_() {
      return 50.0;
   }

   protected void m_26073_() {
      this.timer++;
      if (this.f_26050_ != null && this.f_26050_.m_6084_()) {
         if (this.timer >= 600) {
            LivingEntity nearest = this.getNearest();
            if (nearest != null && nearest != this.f_26050_) {
               this.f_26050_ = nearest;
               this.timer = 0;
            }
         }
      } else {
         this.f_26050_ = this.getNearest();
      }
   }

   protected LivingEntity getNearest() {
      LivingEntity nearest = this.f_26135_.m_9236_().m_45930_(this.f_26135_, this.m_7623_());
      if (nearest == null || nearest instanceof Player player && player.m_7500_()) {
         Vec3 Pos = this.f_26135_.m_20182_();
         Level world = this.f_26135_.m_9236_();
         AABB searchArea = new AABB(Pos.f_82479_ - 40.0, Pos.f_82480_ - 2.0, Pos.f_82481_ - 40.0, Pos.f_82479_ + 40.0, Pos.f_82480_ + 2.0, Pos.f_82481_ + 40.0);
         List<LivingEntity> entities = world.m_6443_(LivingEntity.class, searchArea, e -> e.m_6084_() && !e.m_20145_() && e != this.f_26135_);
         if (entities.isEmpty()) {
            return null;
         }

         nearest = entities.get(Math.abs(this.f_26135_.m_217043_().m_188502_() % entities.size()));
      }

      if (this.f_26135_ instanceof ProteusEntity proteus) {
         if (nearest instanceof ShadowMimicEntity shadowMimicEntity && shadowMimicEntity.getOwner() == proteus) {
            return null;
         }

         if (nearest instanceof Player player && player.m_7500_()) {
            return null;
         }
      }

      return nearest;
   }
}
