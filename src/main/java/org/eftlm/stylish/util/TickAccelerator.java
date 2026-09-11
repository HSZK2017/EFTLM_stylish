package org.eftlm.stylish.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 游戏刻加速器（tick 节流时钟缩放）。
 * <p>
 * <b>思路来源与致谢</b>：借鉴《TickrateChanger:Reborn》（MegaDarkness）的时钟虚拟化思路——
 * 原版 1.20.1 服务器 tick 循环的节流完全依赖 {@code MinecraftServer.shouldKeepTicking}
 * （SRG {@code m_129960_}）中的 {@code Util.getMillis() < nextTickTime} 判断，
 * 缩放该时钟源即可压缩 50ms 等待、真实加速游戏刻（倍率 1.5 = 30 TPS）。
 * 实现为独立原创（字段/命名/结构均不同，未复制原模组代码；原模组 license = All rights reserved）。
 * <p>
 * <b>实现要点（2026-08-26 卡死修复实证）</b>：本服务器环境的 {@code Util.getMillis()}
 * 已被第三方模组（EFN UtilMixin）虚拟化为<b>游戏运行毫秒</b>（小数值，非 epoch 时间戳），
 * 原版 tick 节流的 nextTickTime 与 shouldKeepTicking 均以该值为基准（同源比较）。
 * 因此虚拟时钟必须<b>基于 {@code Util.getMillis()} 缩放</b>：
 * {@code virtualNow = round(Util.getMillis() × multiplier)}——
 * 倍率 1.0 时与 {@code Util.getMillis()} 严格恒等（零副作用），
 * 倍率 1.5 时虚拟毫秒以 1.5 倍速增长 → 等待条件提前满足 → 每秒真实执行 30 个游戏刻。
 * 此前版本误用 {@code System.currentTimeMillis()}（epoch 大数）作基准，
 * 导致与 nextTickTime 量级不匹配（大数 &lt; 小数恒 false）→ shouldKeepTicking 恒 false →
 * chunk 生成任务永不执行 → prepareLevels 死循环卡死。
 * <p>
 * 生效范围：仅服务端（{@link Dist#DEDICATED_SERVER}）；客户端不激活，
 * mixin 在未激活时直接放行原版时钟（客户端/单机不受影响）。
 * <p>
 * 配置：{@code rl.properties tick_multiplier}（启动倍率，默认 1.0 = 20 TPS 原速）；
 * 运行时热调：{@code /rl tickrate <倍率>}。
 */
public final class TickAccelerator {

    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    /** 当前倍率（1.0 = 20 TPS；1.5 = 30 TPS；0.5 = 10 TPS） */
    private static volatile float multiplier = 1.0F;
    /** 服务端激活标志（mixin 处理器据此放行原版时钟） */
    private static volatile boolean active = false;

    private TickAccelerator() {
    }

    /** 服务端初始化（mod 构造时调用）：仅置激活标志，无需后台线程 */
    public static void init() {
        if (FMLEnvironment.dist != Dist.DEDICATED_SERVER) {
            LOGGER.info("[Tickrate] client side, virtual clock disabled");
            return;
        }
        if (active) {
            return;
        }
        active = true;
        LOGGER.info("[Tickrate] virtual clock active (server side), multiplier={}", multiplier);
    }

    /**
     * 缩放虚拟时钟（tick 节流判断用）：{@code round(Util.getMillis() × multiplier)}。
     * 以游戏时钟 {@code Util.getMillis()}（本环境已被第三方虚拟化为游戏运行毫秒）为基准，
     * 与 tick 循环中的 nextTickTime 保持同源比较：
     * 倍率 >1 时虚拟毫秒比游戏毫秒快 → 等待条件提前满足 → 每秒真实执行 倍率×20 个游戏刻；
     * 倍率 1.0 时严格等于 {@code Util.getMillis()}，行为与原版完全一致。
     */
    public static long virtualNow() {
        return Math.round(net.minecraft.Util.getMillis() * multiplier);
    }

    /** 是否虚拟时钟激活（服务端已 init） */
    public static boolean active() {
        return active;
    }

    /** 热调倍率（0.1 ~ 10.0；1.0 = 原速 20 TPS） */
    public static synchronized void setMultiplier(float m) {
        float clamped = Math.max(0.1F, Math.min(10.0F, m));
        multiplier = clamped;
        LOGGER.info("[Tickrate] multiplier -> {} ({} TPS)", clamped, Math.round(20 * clamped));
    }

    public static float multiplier() {
        return multiplier;
    }
}
