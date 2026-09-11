package org.eftlm.stylish.mixin;

import net.minecraft.server.MinecraftServer;
import org.eftlm.stylish.util.TickAccelerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 游戏刻加速 mixin：虚拟化服务器 tick 节流判断的时钟源。
 * <p>
 * 机制（反编译实证）：1.20.1 服务器 tick 循环的所有等待（tickServer 的任务 drain 与
 * {@code waitUntilNextTick} 的 managedBlock）都由 {@code shouldKeepTicking}
 * （SRG {@code m_129960_}）判定：
 * {@code return hasRunningRunners() || Util.getMillis() < nextTickTime;}
 * ——把其中 {@code Util.getMillis()}（SRG {@code m_137550_}）的调用替换为
 * <b>按倍率缩放的虚拟时钟</b>（{@link TickAccelerator#virtualNow()}）：
 * 倍率 1.5 → 虚拟毫秒比真实快 1.5 倍 → 等待条件提前满足 → 每秒真实执行
 * 倍率×20 个游戏刻（1.5 → 30 TPS）；倍率 0.5 → 10 TPS。
 * <p>
 * <b>同源虚拟化（2026-08-26 卡死修复实证）</b>：仅缩放 {@code shouldKeepTicking}
 * 内的读取会在数学上不自洽——nextTickTime 仍由主循环（SRG {@code m_130011_}）与
 * prepareLevels（SRG {@code m_129940_}）用<b>未缩放</b>时钟写入，缩放时钟迟早
 * 恒大于 nextTickTime → 等待消失 → TPS 失控。因此本实现把 <b>nextTickTime 的
 * 写入点也一并虚拟化</b>（{@code m_130011_} 的 f_129727_ 设置、{@code m_129940_}
 * 的 f_129726_ 设置），使比较两侧同源：50ms 虚拟间隔 = 50/倍率 真实毫秒。
 * 本服务器环境的 {@code Util.getMillis()} 已被第三方模组（EFN UtilMixin）虚拟化为
 * 游戏运行毫秒（小数值），虚拟时钟以其为基准缩放（非 {@code System.currentTimeMillis}
 * epoch 大数，量级不匹配会破坏比较）。
 * <p>
 * 倍率 1.0 时所有注入点返回 {@code Util.getMillis()} 本身，行为与原版完全一致。
 * <p>
 * 映射机制：Forge 1.20.1 生产环境以 SRG 名运行，method/target 写 SRG 名 +
 * remap=false（与 NullEntityMixin 相同经验）。
 * <p>
 * 思路来源：《TickrateChanger:Reborn》（MegaDarkness，时钟加速思路，独立重实现，
 * 详见 {@link TickAccelerator}）。
 */
@Mixin(MinecraftServer.class)
public abstract class TickrateMixin {

    /**
     * 替换 tick 节流判断（shouldKeepTicking）与 nextTickTime 写入（prepareLevels）
     * 中的 {@code Util.getMillis()} 调用为虚拟时钟。
     * <p>
     * {@code m_130011_}（runServer 主循环）拆两个 ordinal 处理：
     * ordinal=0（f_129726_ 初始化）与 ordinal=2（f_129727_ 更新）→ 虚拟时钟；
     * ordinal=1（"Can't keep up" 落后检测 {@code getMillis() - nextTickTime}）→
     * <b>保持真实时钟</b>：若虚拟化，加速时虚拟时钟恒领先 nextTickTime，会误报落后
     * 并触发追赶修正（f_129726_ += 大值）抵消加速。
     * <p>
     * 倍率 1.0 时所有注入点返回 {@code Util.getMillis()} 本身，行为与原版完全一致。
     */
    @Redirect(
            method = {"m_129960_", "m_129940_"},
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/Util;m_137550_()J"),
            remap = false
    )
    private long eftlm$virtualTickClock() {
        return TickAccelerator.active() ? TickAccelerator.virtualNow() : net.minecraft.Util.getMillis();
    }

    /** runServer 主循环 f_129726_ 初始化（第 1 处 Util.getMillis 调用）→ 虚拟时钟 */
    @Redirect(
            method = "m_130011_",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/Util;m_137550_()J",
                    ordinal = 0),
            remap = false
    )
    private long eftlm$virtualTickClockMainInit() {
        return TickAccelerator.active() ? TickAccelerator.virtualNow() : net.minecraft.Util.getMillis();
    }

    /** runServer 主循环 f_129727_ 更新（第 3 处 Util.getMillis 调用）→ 虚拟时钟 */
    @Redirect(
            method = "m_130011_",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/Util;m_137550_()J",
                    ordinal = 2),
            remap = false
    )
    private long eftlm$virtualTickClockMainUpdate() {
        return TickAccelerator.active() ? TickAccelerator.virtualNow() : net.minecraft.Util.getMillis();
    }
}
