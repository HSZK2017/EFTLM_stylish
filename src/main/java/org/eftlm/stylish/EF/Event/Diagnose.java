package org.eftlm.stylish.EF.Event;

import com.github.tartaricacid.touhoulittlemaid.api.event.InteractMaidEvent;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import net.EFTLM.EF.Item.MaidSkillBookItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eftlm.stylish.EFTLMStylish;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

/**
 * 诊断日志（排查渲染 / 技能失效问题）：节流打印女仆的 EF patch 挂载状态、
 * 任务类型、已学习技能，以及关键模组版本。
 */
@Mod.EventBusSubscriber(modid = EFTLMStylish.MODID)
public class Diagnose {
    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof EntityMaid maid)) {
            return;
        }
        if (maid.level().isClientSide()) {
            return;
        }
        tickCounter++;
        // 每 5 秒（100 tick）打印一次，避免刷屏
        if (tickCounter % 100 != 0) {
            return;
        }
        MaidPatch<?> patch = EpicFightCapabilities.getEntityPatch(maid, MaidPatch.class);
        String taskId = "none";
        com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid iMaid =
                com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid.convert(maid);
        if (iMaid != null && iMaid.getTask() != null) {
            taskId = iMaid.getTask().getUid().toString();
        }
        String learned = "[]";
        if (patch != null) {
            StringBuilder sb = new StringBuilder("[");
            for (ResourceLocation rl : patch.getLearnedSkills()) {
                sb.append(rl).append(", ");
            }
            sb.append("]");
            learned = sb.toString();
        }
        LOGGER.info("[DIAG] maid={} pos={} patch={} isFightMode={} task={} learnedSkills={}",
                maid.getName().getString(),
                maid.blockPosition(),
                patch != null ? patch.getClass().getSimpleName() : "NULL",
                patch != null && patch.isFightMode(),
                taskId,
                learned);
    }

    /**
     * 女仆加入世界时检查 EF patch 是否挂载成功（渲染 / 技能学习的前提）。
     */
    @SubscribeEvent
    public static void onMaidJoinWorld(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof EntityMaid maid)) {
            return;
        }
        if (maid.level().isClientSide()) {
            return;
        }
        MaidPatch<?> patch = EpicFightCapabilities.getEntityPatch(maid, MaidPatch.class);
        LOGGER.info("[DIAG] Maid join world: name={} patch={}",
                maid.getName().getString(),
                patch != null ? patch.getClass().getSimpleName() : "NULL");
    }

    /**
     * 技能书交互路径诊断：右键女仆使用技能书时的 patch 状态。
     */
    @SubscribeEvent
    public static void onInteractMaid(InteractMaidEvent event) {
        if (!(event.getStack().getItem() instanceof MaidSkillBookItem)) {
            return;
        }
        EntityMaid maid = event.getMaid();
        MaidPatch<?> patch = EpicFightCapabilities.getEntityPatch(maid, MaidPatch.class);
        String containSkill = "unknown";
        try {
            var skill = MaidSkillBookItem.getContainSkill(event.getStack());
            containSkill = skill != null ? skill.getRegistryName().toString() : "null";
        } catch (Throwable t) {
            containSkill = "error: " + t.getMessage();
        }
        LOGGER.info("[DIAG] SkillBook interact: maid={} patch={} bookSkill={} isFightMode={}",
                maid.getName().getString(),
                patch != null ? patch.getClass().getSimpleName() : "NULL",
                containSkill,
                patch != null && patch.isFightMode());
    }

    /**
     * 打印关键模组版本（供排查环境差异）。
     */
    public static void printEnvironment() {
        LOGGER.info("[DIAG] ===== EFTLM Stylish environment =====");
        LOGGER.info("[DIAG] ef_tlm version: {}", modVersion("ef_tlm"));
        LOGGER.info("[DIAG] epicfight version: {}", modVersion("epicfight"));
        LOGGER.info("[DIAG] touhou_little_maid version: {}", modVersion("touhou_little_maid"));
        LOGGER.info("[DIAG] wom loaded: {} version: {}", ModList.get().isLoaded("wom"), modVersion("wom"));
        LOGGER.info("[DIAG] =======================================");
    }

    private static String modVersion(String modId) {
        return ModList.get().getModContainerById(modId)
                .map(c -> c.getModInfo().getVersion().toString())
                .orElse("NOT LOADED");
    }
}
