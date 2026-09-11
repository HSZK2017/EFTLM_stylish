package org.eftlm.stylish.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 禁用 TLM 呼吸任务（MaidBreathAirTask）——V54 服务器事故根治（2026-08-28）。
 * <p>
 * 事故链（crash 实证，07:39:43/07:54:22 两次 ServerWatchdog FATAL）：
 * 竞技场女仆在虚空平台氧气不满（窒息判定）→ MaidBreathAirTask.checkExtraStartConditions
 * 反复返回 true → start → findAirPosition → canPathReach 全图寻路
 * （MaidWrappedPathFinder → PathFinder BFS → WalkNodeEvaluator/MaidUnderWaterNodeEvaluator
 * VoxelShape 碰撞）→ 单 tick 78-490 秒 → ServerWatchdog 强杀。
 * <p>
 * 修复尝试：给女仆加无限 WATER_BREATHING 效果（hasWaterBreathing → 任务不启动）——
 * 实测女仆 NBT 无 ActiveEffects（TLM addEffect 被覆写/清除，原因未明）→ 效果方案不可靠。
 * 本 mixin 直接在判定入口拦截：checkExtraStartConditions（MCP 源码方法）与
 * m_6114_（SRG 桥方法，Behavior.canStart 覆写的运行时分派入口）HEAD 注入恒 false
 * → 呼吸任务永不启动。竞技场平台无真实水域，禁用后女仆无溺水风险。
 * <p>
 * 映射说明：TLM 为第三方 mod，类名/方法名不做 MCP→SRG 重映射（remap=false）；
 * javap（1.5.3 运行时 jar）实证两个方法名均存在于 class 文件。
 * injectors.defaultRequire=1 → 注入点缺失会启动报错（不静默失败）。
 */
@Mixin(targets = "com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidBreathAirTask")
public abstract class MaidBreathTaskMixin {

    /** MCP 源码方法：checkExtraStartConditions(ServerLevel, EntityMaid) → 恒 false */
    @Inject(method = "checkExtraStartConditions", at = @At("HEAD"), cancellable = true, remap = false)
    private void eftlm$blockBreathTaskMcp(ServerLevel level, EntityMaid maid,
                                          CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    /** SRG 桥方法：m_6114_(ServerLevel, LivingEntity)（Behavior.canStart 覆写）→ 恒 false */
    @Inject(method = "m_6114_", at = @At("HEAD"), cancellable = true, remap = false)
    private void eftlm$blockBreathTaskSrg(ServerLevel level, LivingEntity maid,
                                          CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
