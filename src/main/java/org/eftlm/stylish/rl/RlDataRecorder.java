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
 * 文件格式（大端字节序；v1/v2 旧格式仍可读——训练脚本按 version 自适应）：
 * <pre>
 *   int32   version（1=旧格式无标签；2=带动作标签字典；3=带标签 + 奖励时间对齐修正）
 *   int32   numSteps
 *   int32   stateDim
 *   int32   numActions
 *   [v2+] int32   numLabels + 字典：numLabels × (int16 len + utf8 bytes)
 *   float32 states[numSteps * stateDim]
 *   int32   actions[numSteps]
 *   float32 rewards[numSteps]
 *   [v2+] int16   labelIdx[numSteps]（-1 = 无标签；索引指向字典）
 * </pre>
 * <p>
 * <b>奖励语义（v3 起，P0 修复 2026-09-10）</b>：{@code rewards[i]} = 执行 {@code actions[i]} 之后
 * 立刻获得的奖励，即训练侧 GAE（{@code delta = r_t + γV_{t+1} - V_t}）所假设的语义。
 * <ul>
 *     <li><b>后果奖励</b>（命中/击杀/受击/弹反/切换连携…）：由战斗事件回调经 {@link #addReward}
 *         累加，属于"上一步动作的后果"，在写下一次决策时<b>回填到上一步</b>；</li>
 *     <li><b>本步塑形奖励</b>（熵/衰减/防守/距离/熔断…）：由决策链在决策点计算，属于"这一步动作"，
 *         经 {@link #addStepReward} 记在<b>即将写入的这一步</b>上。</li>
 * </ul>
 * v1/v2 文件里两者混在同一个累加器、且整体滞后一步（{@code rewards[i]} 实为 {@code actions[i-1]}
 * 的后果）；训练脚本对 v2 及更早的文件做一次一步平移近似补偿（见 {@code train/traj_io.py}）。
 * <p>
 * 动作标签（slot.label()：generic 名如 swordmaster_atk，技能槽为技能 id）是 P3 训练
 * 流水线的语义对齐依据：稳定槽位布局下同一动作索引在不同武器/布局下指向不同技能，
 * 训练脚本按标签把动作重映射到当前布局（报告 3.4.2/P3），旧轨迹无标签时仅 generic 段可用。
 * 一个文件 = 一条战斗轨迹；写入目录：config/eftlm_stylish/trajectories/
 */
public final class RlDataRecorder {

    /** 轨迹格式版本（3 = 标签字典 + 奖励时间对齐修正；2 = 标签字典但奖励整体滞后一步） */
    public static final int FORMAT_VERSION = 3;
    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    /** 每个女仆的独立缓冲（竞技场女仆与残留女仆互不干扰） */
    private static final Map<UUID, List<Step>> BUFFERS = new HashMap<>();
    private static final Map<UUID, Long> LAST_ACTIVE = new HashMap<>();
    private static final Map<UUID, Boolean> COMBAT = new HashMap<>();
    /** 后果奖励池：事件回调写入，回填到"上一步"（见类注释的奖励语义） */
    private static final Map<UUID, Integer> PENDING = new HashMap<>();
    /** 本步塑形奖励池：决策点写入，记在"即将记录的这一新步"上 */
    private static final Map<UUID, Integer> PENDING_STEP = new HashMap<>();
    /** 单条轨迹最大步数：达到后自动落盘并开启新轨迹（600 步 ≈ 2.5 分钟战斗） */
    private static final int MAX_STEPS = 600;

    private record Step(float[] state, int action, int reward, String label) {
    }

    private RlDataRecorder() {
    }

    /**
     * 记录一步（决策点调用，按女仆隔离）；label 为动作语义标签（slot.label()）。
     * <p>
     * 顺序很重要（P0 修复）：先把"上一步动作造成的后果奖励"回填到缓冲里的上一步，
     * 再把本次决策的塑形奖励记在新步上——这样 {@code rewards[i]} 与 {@code actions[i]} 严格对齐。
     */
    public static void recordStep(EntityMaid maid, float[] state, int action, String label) {
        UUID id = maid.getUUID();
        COMBAT.put(id, true);
        LAST_ACTIVE.put(id, (long) maid.tickCount);
        List<Step> buffer = BUFFERS.computeIfAbsent(id, k -> new ArrayList<>());
        // ① 后果奖励 → 上一步（实现"result of action[i-1] 记在 step i-1"）
        int consequence = PENDING.getOrDefault(id, 0);
        if (consequence != 0 && !buffer.isEmpty()) {
            int last = buffer.size() - 1;
            Step old = buffer.get(last);
            buffer.set(last, new Step(old.state(), old.action(), old.reward() + consequence, old.label()));
        }
        PENDING.put(id, 0);
        // ② 本步塑形奖励 → 新写入的这一步
        int stepReward = PENDING_STEP.getOrDefault(id, 0);
        PENDING_STEP.put(id, 0);
        buffer.add(new Step(state.clone(), action, stepReward, label));
        if (buffer.size() >= MAX_STEPS) {
            flush(maid, id); // 轨迹长度上限 → 落盘后继续新轨迹
        }
    }

    /**
     * 累积<b>后果</b>奖励（事件源：命中/击杀/受击/弹反等）。
     * 语义 = "刚刚那次动作造成的结果"，因此在下一次决策记录时回填到<b>上一步</b>。
     */
    public static void addReward(EntityMaid maid, int amount) {
        UUID id = maid.getUUID();
        PENDING.put(id, PENDING.getOrDefault(id, 0) + amount);
    }

    /**
     * 累积<b>本步</b>塑形奖励（决策链在决策点计算：熵/动作衰减/防守/距离/连击熔断/无效动作惩罚等）。
     * 语义 = "这一步动作本身的塑形收益"，因此记在即将由 {@link #recordStep} 写入的新步上。
     */
    public static void addStepReward(EntityMaid maid, int amount) {
        UUID id = maid.getUUID();
        PENDING_STEP.put(id, PENDING_STEP.getOrDefault(id, 0) + amount);
    }

    /**
     * P5.7 拒绝标签修正（执行结果反哺）：把女仆轨迹缓冲<b>最近一步</b>的语义标签改写。
     * 被执行器拒绝（REJECTED_BUSY/INVALID/FAILED）的决策实际未执行，仍以原意图标签
     * 进入训练数据会把"忙时输出攻击"当正样本 → 拒绝率恶性循环。改写为 idle（等待）
     * 后模型学到"不可执行时机 → 等待"。仅当最近一步动作与本次决策一致时改写（防误伤）。
     */
    public static void rewriteLastStepLabel(EntityMaid maid, int action, String newLabel) {
        UUID id = maid.getUUID();
        List<Step> buffer = BUFFERS.get(id);
        if (buffer == null || buffer.isEmpty()) {
            return;
        }
        Step last = buffer.get(buffer.size() - 1);
        if (last.action() != action || last.label() == null) {
            return;
        }
        buffer.set(buffer.size() - 1, new Step(last.state(), last.action(), last.reward(), newLabel));
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
        int pending = PENDING.getOrDefault(id, 0) + PENDING_STEP.getOrDefault(id, 0);
        if (buffer == null || buffer.isEmpty()) {
            BUFFERS.remove(id);
            COMBAT.put(id, false);
            PENDING.put(id, 0);
            PENDING_STEP.put(id, 0);
            return;
        }
        // 尾步信用分配：flush 前把最后一次 recordStep 之后到达的奖励（击杀 +100 /
        // 死亡结算等终点信号）附加到轨迹最后一步，避免关键胜负信号被丢弃。
        // 这些奖励语义上属于"最后一步动作的后果"，记在最后一步与 v3 契约一致。
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
        PENDING_STEP.put(id, 0);
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
            int pending = PENDING.getOrDefault(id, 0) + PENDING_STEP.getOrDefault(id, 0);
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
        PENDING_STEP.remove(id);
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
            // 2026-09-10：落盘日志带轨迹质量指标——"每次近战命中(>=+30)步数占比"是
            // "能不能打中"的现场代理（instructor 的 kill_low 弱点），奖励均值/p50 反映塑造是否失衡。
            // 这些数字让"下一轮是否变好"在服务器日志里即可判读，无需导出 dump。
            int steps = buffer.size();
            long sum = 0;
            int posSteps = 0;
            int hitSteps = 0;
            int nonzeroSteps = 0;
            int[] sorted = new int[steps];
            for (int i = 0; i < steps; i++) {
                int r = buffer.get(i).reward();
                sum += r;
                sorted[i] = r;
                if (r != 0) {
                    nonzeroSteps++;
                }
                if (r > 0) {
                    posSteps++;
                }
                if (r >= 30) {
                    hitSteps++;
                }
            }
            // 2026-09-11：全零奖励轨迹不入库。实测存在"僵持"段（女仆 4.5 格外满血站着、
            // 目标也不动，女仆 Brain 的 ATTACK_TARGET 被清空 → 双方都不导航）：
            // 这类轨迹 600 步奖励全 0、动作 top1 仅 ~42% → 能通过训练侧的"单动作占比"过滤，
            // 于是"什么都不做"会被 BC 克隆、AWR 权重≈1 → 直接把策略往被动方向拉。
            // 判据用"非零奖励步数 == 0"（比 sum==0 严格：+30/−30 相抵不算全零）。
            if (nonzeroSteps == 0 && steps >= 100) {
                LOGGER.info("[RL] trajectory DROPPED (no signal): {} steps all-zero reward, nothing recorded "
                        + "(stalemate? check arena navigation/target lock)", steps);
                return;
            }
            java.util.Arrays.sort(sorted);
            int median = steps == 0 ? 0 : sorted[steps / 2];
            LOGGER.info("[RL] trajectory saved: {} ({} steps, {} bytes, v{}, reward mean={}, median={}, pos={}%, hits>=30={}%)",
                    file.getFileName(), steps, Files.size(file), FORMAT_VERSION,
                    steps == 0 ? 0 : Math.round((double) sum / steps), median,
                    steps == 0 ? 0 : Math.round(100.0 * posSteps / steps),
                    steps == 0 ? 0 : Math.round(100.0 * hitSteps / steps));
        } catch (IOException e) {
            LOGGER.error("[RL] failed to save trajectory", e);
        }
    }

    public static void reset() {
        BUFFERS.clear();
        PENDING.clear();
        PENDING_STEP.clear();
        COMBAT.clear();
        LAST_ACTIVE.clear();
    }

    /** 当前缓冲步数（诊断用，返回所有女仆总步数） */
    public static int bufferSize() {
        return BUFFERS.values().stream().mapToInt(List::size).sum();
    }
}
