package org.eftlm.stylish.EF.Extension;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.TLM.Task.FightModeTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.Map;

/**
 * 战斗模式下抑制 TLM 慌乱 AI（Panic）。
 * <p>
 * TLM 的 MaidPanicTask 每 tick 检查 HURT_BY（火焰 / 毒等持续伤害会写入）
 * 或 NEAREST_HOSTILE（附近存在敌对即成立）后激活 PANIC activity，
 * 清空移动目标并乱跑——而 EFTLM 的 FightModeTask 未禁用 panic，
 * 导致女仆"受到持续伤害就脱锁发呆 / 乱跑"。
 * <p>
 * 本行为以 priority 0 注册进 CORE（先于 MaidPanicTask 的 priority 1 执行），
 * 在战斗模式下清除这两个触发源，使 MaidPanicTask 判定为无需慌乱。
 */
public class NoPanicInCombat extends Behavior<EntityMaid> {

    public NoPanicInCombat() {
        super(Map.of());
    }

    @Override
    protected void tick(ServerLevel level, EntityMaid maid, long gameTime) {
        if (maid.getTask() instanceof FightModeTask) {
            maid.getBrain().eraseMemory(MemoryModuleType.HURT_BY);
            maid.getBrain().eraseMemory(MemoryModuleType.NEAREST_HOSTILE);
        }
    }
}
