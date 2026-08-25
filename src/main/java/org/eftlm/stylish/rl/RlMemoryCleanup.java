package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.eftlm.stylish.EFTLMStylish;
import org.eftlm.stylish.compat.efn.EfnSkillCatalog;

import java.util.UUID;

/**
 * RL 训练状态清理：女仆被击杀 / 移除（discard）时释放其按 UUID 隔离的
 * 全部决策历史 / 战技冷却 / 轨迹缓冲，防止长期运行的训练服务器内存无限增长。
 * <p>
 * 竞技场女仆每次死亡重生都是新实体（新 UUID），旧条目无人清理会持续累积。
 */
@Mod.EventBusSubscriber(modid = EFTLMStylish.MODID)
public class RlMemoryCleanup {

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (!(event.getEntity() instanceof EntityMaid maid)) {
            return;
        }
        Entity.RemovalReason reason = maid.getRemovalReason();
        if (reason != Entity.RemovalReason.KILLED && reason != Entity.RemovalReason.DISCARDED) {
            return; // 换维度等临时离场不清理，返回后状态可继续使用
        }
        UUID id = maid.getUUID();
        RlBrain.forgetMaid(id);
        RlDataRecorder.forgetMaid(id);
        EfnSkillCatalog.forgetMaid(id);
    }
}
