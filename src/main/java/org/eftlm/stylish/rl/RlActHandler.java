package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.eftlm.stylish.EFTLMStylish;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

/**
 * RL 行动薄分发器：订阅 {@link RlActEvent}，把行动交给 {@link RlActionRegistry}
 * 路由到注册的对应执行状态机实施，并把执行结果经 {@link RlExecResultEvent}
 * 发布回事件总线（执行反馈闭环）。
 */
@Mod.EventBusSubscriber(modid = EFTLMStylish.MODID)
public class RlActHandler {

    @SubscribeEvent
    public static void onRlAct(RlActEvent event) {
        EntityMaid maid = event.getMaid();
        if (maid.level().isClientSide()) {
            return;
        }
        MaidPatch<?> patch = EpicFightCapabilities.getEntityPatch(maid, MaidPatch.class);
        if (patch == null) {
            return;
        }
        RlExecResult result = RlActionRegistry.dispatch(patch, event);
        MinecraftForge.EVENT_BUS.post(new RlExecResultEvent(maid, event.getAction(), result, event.getSlot()));
        // P0 观测：执行结果 + 执行后动画帧/EntityState 补全到决策链路追踪（trace 行 res=PENDING → 实际结果）
        RlTrace.resolveExec(maid, result, patch, patch.getTarget());
    }
}
