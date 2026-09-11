package org.eftlm.stylish;

import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.eftlm.stylish.EF.Event.Diagnose;
import org.slf4j.Logger;

@Mod(EFTLMStylish.MODID)
public class EFTLMStylish {
    public static final String MODID = "eftlm_stylish";
    private static final Logger LOGGER = LogUtils.getLogger();

    public EFTLMStylish() {
        LOGGER.info("EFTLM Stylish Combat loaded!");
        Diagnose.printEnvironment();
        // P5.6 游戏刻加速：服务端启动虚拟时钟（倍率取 rl.properties tick_multiplier，默认 1.0 原速）
        org.eftlm.stylish.util.TickAccelerator.init();
        org.eftlm.stylish.rl.RlConfig.ensureLoaded();
        if (org.eftlm.stylish.util.TickAccelerator.active()
                && Math.abs(org.eftlm.stylish.rl.RlConfig.tickMultiplier - 1.0F) > 1.0E-4F) {
            org.eftlm.stylish.util.TickAccelerator.setMultiplier(org.eftlm.stylish.rl.RlConfig.tickMultiplier);
        }
    }

    /**
     * P5.7 智能性能调度：服务器启动完成后启动 AutoTicker
     * （空闲自动加速 / 负载自动降速 / tick 停滞看门狗，联动 spark 与外部 guardian 脚本）。
     */
    @Mod.EventBusSubscriber(modid = EFTLMStylish.MODID)
    public static class ServerLifecycle {
        @SubscribeEvent
        public static void onServerStarted(ServerStartedEvent event) {
            MinecraftServer server = event.getServer();
            org.eftlm.stylish.util.AutoTicker.start(server);
        }

        @SubscribeEvent
        public static void onServerStopping(ServerStoppingEvent event) {
            org.eftlm.stylish.util.AutoTicker.shutdown();
        }
    }
}
