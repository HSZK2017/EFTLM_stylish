package com.dmc.invincible_dmc.mixin.epicfight;

import com.dmc.invincible_dmc.api.collider.ColliderProvider;
import java.util.List;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(
   value = {Phase.class},
   remap = false
)
public abstract class AttackAnimationPhaseMixin {
   @Redirect(
      method = {"getCollidingEntities"},
      at = @At(
         value = "INVOKE",
         target = "Lyesman/epicfight/api/collider/Collider;updateAndSelectCollideEntity(Lyesman/epicfight/world/capabilities/entitypatch/LivingEntityPatch;Lyesman/epicfight/api/animation/types/AttackAnimation;FFLyesman/epicfight/api/animation/Joint;F)Ljava/util/List;"
      )
   )
   private List<Entity> invincible_dmc$resolveCollider(
      Collider originalCollider,
      LivingEntityPatch<?> entitypatch,
      AttackAnimation animation,
      float prevElapsedTime,
      float elapsedTime,
      Joint joint,
      float attackSpeed
   ) {
      Collider resolved = originalCollider;
      ColliderProvider provider = ColliderProvider.resolveFrom(animation);
      if (provider != null) {
         Phase phase = (Phase)this;
         Collider provided = provider.resolve(entitypatch, animation, phase, joint, elapsedTime);
         if (provided != null) {
            resolved = provided;
         }
      }

      return resolved.updateAndSelectCollideEntity(entitypatch, animation, prevElapsedTime, elapsedTime, joint, attackSpeed);
   }
}
