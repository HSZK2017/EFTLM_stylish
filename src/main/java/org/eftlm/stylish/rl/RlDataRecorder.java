package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * RL 轨迹采集：把每步的（状态, 行动, 奖励, 行动标签）写入训练数据文件（离线训练用）。
 * <p>
 * 文件格式 v2（大端字节序；v1 旧格式仍可读——训练脚本按 version 自适应）：
 * <pre>
 *   int32   version（1=旧格式无标签；2=带动作标签字典）
 *   int32   numSteps
 *   int32   stateDim
 *   int32   numActions
 *   [v2] int32   numLabels + 字典：numLabels × (int16 len + utf8 bytes)
 *   float32 states[numSteps * stateDim]
 *   int32   actions[numSteps]
 *   float32 rewards[numSteps]
 *   [v2] int16   labelIdx[numSteps]（-1 = 无标签；索引指向字典）
 * </pre>
 * <p>
 * 动作标签（slot.label()：generic 名如 swordmaster_atk，技能槽为技能 id）是 P3 训练
 * 流水线的语义对齐依据：稳定槽位布局下同一动作索引在不同武器/布局下指向不同技能，
 * 训练脚本按标签把动作重映射到当前布局（报告 3.4.2/P3），旧轨迹无标签时仅 generic 段可用。
 * 一个文件 = 一条战斗轨迹；写入目录：config/eftlm_stylish/trajectories/
 */
public final class RlDataRecorder {

    /** P3 轨迹格式版本（2 = 带动作标签字典） */
    public static final int FORMAT_VERSION = 2;
    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    /** 每个女仆的独立缓冲（竞技场女仆与残留女仆互不干扰） */
    private static final Map<UUID, List<Step>> BUFFERS = new HashMap<>();
    private static final Map<UUID, Long> LAST_ACTIVE = new HashMap<>();
    private static final Map<UUID, Boolean> COMBAT = new HashMap<>();
    private static final Map<UUID, Integer> PENDING = new HashMap<>();
    /** 单条轨迹最大步数：达到后自动落盘并开启新轨迹（600 步 ≈ 2.5 分钟战斗） */
    private static final int MAX_STEPS = 600;

    private record Step(float[] state, int action, int reward, String label) {
    }

    private RlDataRecorder() {
    }

    /** 记录一步（决策点调用，按女仆隔离）；label 为动作语义标签（slot.label()） */
    public static void recordStep(EntityMaid maid, float[] state, int action, String label) {
        UUID id = maid.getUUID();
        COMBAT.put(id, true);
        LAST_ACTIVE.put(id, (long) maid.tickCount);
        BUFFERS.computeIfAbsent(id, k -> new ArrayList<>())
                .add(new Step(state.clone(), action, PENDING.getOrDefault(id, 0), label));
        PENDING.put(id, 0);
        if (BUFFERS.get(id).size() >= MAX_STEPS) {
            flush(maid, id); // 轨迹长度上限 → 落盘后继续新轨迹
        }
    }

    /** 累积奖励（事件源：命中/击杀/受击等，绑定到对应女仆） */
    public static void addReward(EntityMaid maid, int amount) {
        UUID id = maid.getUUID();
        PENDING.put(id, PENDING.getOrDefault(id, 0) + amount);
    }

    /**
     * 每 tick 维护：女仆死亡 → 立即落盘；长时间无新记录 → 结束当前轨迹。
     */
    public static void tick(EntityMaid maid) {
        UUID id = maid.getUUID();
        if (maid.isDeadOrDying()) {
            flush(maid, id);
            return;
        }
        if (COMBAT.getOrDefault(id, false) && maid.tickCount - LAST_ACTIVE.getOrDefault(id, 0L) > 600) {
            flush(maid, id); // 脱离战斗超时 → 轨迹结束
        }
    }

    private static void flush(EntityMaid maid, UUID id) {
        List<Step> buffer = BUFFERS.get(id);
        int pending = PENDING.getOrDefault(id, 0);
        if (buffer == null || buffer.isEmpty()) {
            BUFFERS.remove(id);
            COMBAT.put(id, false);
            PENDING.put(id, 0);
            return;
        }
        // 尾步信用分配：flush 前把最后一次 recordStep 之后到达的奖励（击杀 +100 /
        // 死亡结算等终点信号）附加到轨迹最后一步，避免关键胜负信号被丢弃
        if (pending != 0) {
            int last = buffer.size() - 1;
            Step old = buffer.get(last);
            buffer.set(last, new Step(old.state(), old.action(), old.reward() + pending, old.label()));
        }
        // P5 评分结算（报告 4.2）：战斗结束（死亡）按当前华丽度结算一次——
        // flair/10 附加到尾步（华丽打法获得终点奖励，强化 DMC 式风格目标）
        if (maid.isDeadOrDying()) {
            int flairReward = Math.round(org.eftlm.stylish.strategy.StyleState.getFlair(maid) / 10.0F);
            if (flairReward != 0) {
                int last = buffer.size() - 1;
                Step old = buffer.get(last);
                buffer.set(last, new Step(old.state(), old.action(), old.reward() + flairReward, old.label()));
                RlTrace.event(maid, "flair_settle",
                        "combat end, flair=" + org.eftlm.stylish.strategy.StyleState.getFlair(maid)
                                + " -> reward " + flairReward);
            }
        }
        flushToDisk(id, buffer);
        BUFFERS.remove(id);
        PENDING.put(id, 0);
        COMBAT.put(id, false);
    }

    /**
     * 女仆被击杀 / 移除时释放其全部轨迹状态：未落盘的缓冲先写入磁盘
     * （死亡女仆不再 tick，flush 时机可能错过），再移除全部记录。
     */
    public static void forgetMaid(UUID id) {
        List<Step> buffer = BUFFERS.get(id);
        if (buffer != null && !buffer.isEmpty()) {
            // 同 flush：未消费的 PENDING 附加到尾步后再落盘
            int pending = PENDING.getOrDefault(id, 0);
            if (pending != 0) {
                int last = buffer.size() - 1;
                Step old = buffer.get(last);
                buffer.set(last, new Step(old.state(), old.action(), old.reward() + pending, old.label()));
            }
            flushToDisk(id, buffer);
        }
        BUFFERS.remove(id);
        LAST_ACTIVE.remove(id);
        COMBAT.remove(id);
        PENDING.remove(id);
    }

    private static void flushToDisk(UUID id, List<Step> buffer) {
        try {
            Path dir = FMLPaths.CONFIGDIR.get().resolve("eftlm_stylish").resolve("trajectories");
            Files.createDirectories(dir);
            Path file = dir.resolve("traj_" + id.toString().substring(0, 8) + "_" + System.currentTimeMillis() + ".bin");
            try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(file)))) {
                // P3 v2 头：version + 尺寸 + 动作标签字典（去重、顺序稳定）
                java.util.LinkedHashMap<String, Integer> labelDict = new java.util.LinkedHashMap<>();
                for (Step step : buffer) {
                    if (step.label() != null && !step.label().isEmpty() && !labelDict.containsKey(step.label())) {
                        labelDict.put(step.label(), labelDict.size());
                    }
                }
                out.writeInt(FORMAT_VERSION);
                out.writeInt(buffer.size());
                out.writeInt(RlState.STATE_DIM);
                out.writeInt(RlActEvent.TOTAL_ACTIONS);
                out.writeInt(labelDict.size());
                for (String label : labelDict.keySet()) {
                    byte[] bytes = label.getBytes(StandardCharsets.UTF_8);
                    out.writeShort(bytes.length);
                    out.write(bytes);
                }
                for (Step step : buffer) {
                    for (float v : step.state) {
                        out.writeFloat(v);
                    }
                }
                for (Step step : buffer) {
                    out.writeInt(step.action);
                }
                for (Step step : buffer) {
                    out.writeFloat(step.reward);
                }
                for (Step step : buffer) {
                    Integer idx = step.label() == null ? null : labelDict.get(step.label());
                    out.writeShort(idx != null ? idx : -1);
                }
            }
            LOGGER.info("[RL] trajectory saved: {} ({} steps, {} bytes, v{})", file.getFileName(), buffer.size(), Files.size(file), FORMAT_VERSION);
        } catch (IOException e) {
            LOGGER.error("[RL] failed to save trajectory", e);
        }
    }

    public static void reset() {
        BUFFERS.clear();
        PENDING.clear();
        COMBAT.clear();
        LAST_ACTIVE.clear();
    }

    /** 当前缓冲步数（诊断用，返回所有女仆总步数） */
    public static int bufferSize() {
        return BUFFERS.values().stream().mapToInt(List::size).sum();
    }
}
