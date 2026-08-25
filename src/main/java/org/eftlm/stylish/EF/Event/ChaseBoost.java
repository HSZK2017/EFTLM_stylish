package org.eftlm.stylish.EF.Event;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.eftlm.stylish.EFTLMStylish;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

import java.util.UUID;

/**
 * 追击加速：战斗模式下目标较远时临时提高女仆移动速度，
 * 防止被弓兵等远程敌人放风筝（TLM 任务固定的 0.6F 走速追不上）。
 * <p>
 * 最终移动速度由 MOVEMENT_SPEED 属性决定（MoveControl 的 speed 只是输入），
 * 因此通过瞬态属性修改器加速是可靠的；脱离追击或退出战斗模式自动移除。
 */
@Mod.EventBusSubscriber(modid = EFTLMStylish.MODID)
public class ChaseBoost {

    private static final UUID CHASE_SPEED_UUID = UUID.fromString("2f7a1c3e-8b4d-4e9a-9f6c-1d2e3f4a5b6c");
    /** 追击速度加成：+100%（乘法，V46 从 +50% 提高，增强对快速位移敌人的追踪） */
    private static final AttributeModifier CHASE_SPEED =
            new AttributeModifier(CHASE_SPEED_UUID, "eftlm_stylish_chase", 1.0,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);
    /** 追击加速触发的最小距离 */
    private static final double CHASE_DIST = 2.5;

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof EntityMaid maid)) {
            return;
        }
        if (maid.level().isClientSide()) {
            return;
        }
        AttributeInstance speed = maid.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed == null) {
            return;
        }
        MaidPatch<?> patch = EpicFightCapabilities.getEntityPatch(maid, MaidPatch.class);
        boolean chase = patch != null && patch.isFightMode()
                && patch.getTarget() != null && patch.getTarget().isAlive()
                && maid.distanceTo(patch.getTarget()) > CHASE_DIST;
        boolean hasBoost = speed.getModifier(CHASE_SPEED_UUID) != null;
        if (chase && !hasBoost) {
            speed.addTransientModifier(CHASE_SPEED);
        } else if (!chase && hasBoost) {
            speed.removeModifier(CHASE_SPEED_UUID);
        }
    }
}
