package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RL 行动执行器注册表（类 Agent 模式核心）：
 * <ul>
 *     <li>固定段执行器占用通用行动 0..{@link RlActEvent#NUM_ACTIONS}-1；</li>
 *     <li>技能池执行器按注册顺序把各自可用行动贡献到槽位
 *         {@link RlActEvent#ACT_SKILL_BASE}..{@link RlActEvent#TOTAL_ACTIONS}-1（截断 16）；</li>
 *     <li>{@link #buildLayout} 在每个决策点组装完整行动布局（空槽 = 无效，RL 掩码置零）；</li>
 *     <li>{@link #dispatch} 把事件总线上到达的 {@link RlActEvent} 分发回对应执行器实施，
 *         返回 {@link RlExecResult} 供 {@link RlExecResultEvent} 反哺 RL。</li>
 * </ul>
 * 新的执行状态机实现 {@link RlActionExecutor} 后调用 {@link #register} 即接入总线，
 * 无需改动 RL 决策与训练契约（模型输出维度恒为 TOTAL_ACTIONS）。
 */
public final class RlActionRegistry {

    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    /** 固定段执行器（全局编号 0..NUM_ACTIONS-1） */
    private static final List<RlActionExecutor> FIXED_EXECUTORS = new ArrayList<>();
    /** 技能池执行器（全局编号 ACT_SKILL_BASE..，按注册顺序） */
    private static final List<RlActionExecutor> SKILL_EXECUTORS = new ArrayList<>();
    private static final Map<String, RlActionExecutor> BY_ID = new HashMap<>();

    static {
        register(new GenericCombatExecutor());
        register(new EfnSkillExecutor());
        register(new DefenseSkillExecutor());
    }

    private RlActionRegistry() {
    }

    /**
     * 注册执行器：首个固定段执行器按通用行动数占用固定段，其余作为技能池贡献者。
     * 重复 id 的注册被忽略。
     */
    public static synchronized void register(RlActionExecutor executor) {
        if (BY_ID.containsKey(executor.id())) {
            LOGGER.warn("[RL] executor '{}' already registered, ignored", executor.id());
            return;
        }
        if (FIXED_EXECUTORS.isEmpty()) {
            FIXED_EXECUTORS.add(executor);
        } else {
            SKILL_EXECUTORS.add(executor);
        }
        BY_ID.put(executor.id(), executor);
        LOGGER.info("[RL] action executor registered: {} (fixed={})", executor.id(), FIXED_EXECUTORS.contains(executor));
    }

    public static List<RlActionExecutor> executors() {
        List<RlActionExecutor> all = new ArrayList<>(FIXED_EXECUTORS);
        all.addAll(SKILL_EXECUTORS);
        return all;
    }

    /**
     * 决策点行动布局：index = 全局行动编号；null = 无效槽位（RL 掩码置零）。
     */
    public static RlActionSlot[] buildLayout(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        int tick = maid.tickCount;
        RlActionSlot[] layout = new RlActionSlot[RlActEvent.TOTAL_ACTIONS];
        int g = 0;
        for (RlActionExecutor ex : FIXED_EXECUTORS) {
            for (RlActionSlot s : ex.available(patch, tick)) {
                if (g >= RlActEvent.ACT_SKILL_BASE) {
                    LOGGER.warn("[RL] fixed executor '{}' overflowed generic segment", ex.id());
                    break;
                }
                layout[g++] = s;
            }
        }
        int skillIdx = 0;
        outer:
        for (RlActionExecutor ex : SKILL_EXECUTORS) {
            // P2 稳定模式：防守技能（dodge_step/blade_clash）固定在全局尾部两槽，
            // 不受武器切换影响（语义永久稳定）
            if (RlConfig.slotStable && ex instanceof DefenseSkillExecutor) {
                int base = RlActEvent.TOTAL_ACTIONS - 2;
                for (RlActionSlot s : ex.available(patch, tick)) {
                    if (base >= RlActEvent.TOTAL_ACTIONS) {
                        break;
                    }
                    layout[base++] = s;
                }
                continue;
            }
            for (RlActionSlot s : ex.available(patch, tick)) {
                if (skillIdx >= RlActEvent.MAX_SKILL_SLOTS) {
                    break outer;
                }
                layout[RlActEvent.ACT_SKILL_BASE + skillIdx++] = s;
            }
        }
        return layout;
    }

    /** 事件总线分发：查槽 → 校验 → 执行 → 返回结果（异常兜底 FAILED） */
    public static RlExecResult dispatch(MaidPatch<?> patch, RlActEvent event) {
        RlActionSlot slot = event.getSlot();
        if (slot == null) {
            return RlExecResult.REJECTED_INVALID;
        }
        RlActionExecutor ex = BY_ID.get(slot.executorId());
        if (ex == null) {
            LOGGER.warn("[RL] no executor for slot '{}'", slot.executorId());
            return RlExecResult.REJECTED_INVALID;
        }
        if (!ex.canExecute(patch, slot)) {
            return patch.getEntityState().inaction()
                    ? RlExecResult.REJECTED_BUSY : RlExecResult.REJECTED_INVALID;
        }
        try {
            RlExecResult result = ex.execute(patch, slot);
            // P5.6 命中网格起点标记：执行成功时记录出招起点（命中回调据此结算经验网格）
            if (result == RlExecResult.EXECUTED) {
                CombatLibrary.markAttack((EntityMaid) patch.getOriginal());
            }
            return result;
        } catch (Throwable t) {
            LOGGER.error("[RL] executor {} action={} threw", ex.id(), slot.label(), t);
            return RlExecResult.FAILED;
        }
    }
}
