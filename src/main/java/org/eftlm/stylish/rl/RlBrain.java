package org.eftlm.stylish.rl;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.EFTLM.EF.Capability.MaidPatch;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eftlm.stylish.strategy.StyleState;
import org.eftlm.stylish.strategy.WeaponArsenal;
import org.eftlm.stylish.util.AnimKit;

import java.nio.file.Path;
import java.util.Random;

/**
 * RL 宏观决策脑：每 5 tick 采集状态 → 行动概率预测 → 把概率最高的行动发布到事件总线。
 * <p>
 * 模型已加载 → 神经网络推理；未加载 → 规则默认策略（保证无模型时可玩，同时作为
 * 离线训练的初始数据来源）。训练数据在决策点由 {@link RlDataRecorder} 记录。
 * <p>
 * V13（Agent 模式）：学习技能书且战斗模式的女仆全部由 RL 决策（rl.properties 可关），
 * 行动空间由 {@link RlActionRegistry} 按注册执行器动态组装；执行结果经
 * {@link RlExecResultEvent} 反哺（状态 s[16]/s[17] + 奖励）。轨迹采集仍仅限竞技场女仆。
 */
public final class RlBrain {

    private static final Logger LOGGER = LogManager.getLogger("eftlm_stylish");

    /** 决策间隔（tick） */
    public static final int DECISION_INTERVAL = 5;

    // ---- 多样性奖励塑形参数 ----
    /** 连段熵窗口（最近 N 次决策） */
    private static final int ENTROPY_WINDOW = 50;
    /** 熵奖励上限：窗口内动作分布越均匀，每步额外奖励越高（0~MAX） */
    private static final int ENTROPY_BONUS_MAX = 3;
    /** 动作衰减阈值：窗口内同动作超过该次数视为"滥用" */
    private static final int STALE_THRESHOLD = 8;
    /** 滥用惩罚：每步负奖励 */
    private static final int STALE_PENALTY = -8;

    private static RlModel model;
    private static boolean modelChecked = false;
    /** P4 影子评估模型（竞技场影子女仆专用；shadow_model_file 空时关闭） */
    private static RlModel shadowModel;
    private static boolean shadowModelChecked = false;
    /** 维度不匹配告警只打一次（避免每 5 tick 刷屏），reload 后重置 */
    private static boolean dimWarnLogged = false;
    private static final Random RANDOM = new Random();
    /** 每个女仆的决策历史窗口（环形） */
    private static final java.util.Map<java.util.UUID, int[]> HISTORY = new java.util.HashMap<>();
    /** 每个女仆的窗口内动作计数 */
    private static final java.util.Map<java.util.UUID, int[]> STALENESS = new java.util.HashMap<>();
    private static final java.util.Map<java.util.UUID, Integer> HISTORY_IDX = new java.util.HashMap<>();
    /** 每个女仆的远程连射计数（同态衰减池，近战/闪避/弹反后重置） */
    private static final java.util.Map<java.util.UUID, Integer> RANGED_STREAK = new java.util.HashMap<>();
    /** 远程连射衰减系数：第 1/2/3/4+ 次 */
    private static final float[] RANGED_DECAY = {1.0F, 0.5F, 0.1F, 0.0F};
    /** 超过衰减档后的"怯战"惩罚 */
    private static final int RANGED_COWARD_PENALTY = -5;

    // ---- V9 约束：斗兽场逼战 / 突进奖励 / 距离惩罚 / 连击熔断 / 远程归零 ----
    /** 近战最大有效判定距离（格）：超过视为脱离近战（Proximity Penalty 阈值） */
    private static final double MELEE_RANGE = 3.5;
    /** 脱离近战判定阈值（状态0 = 距离/16） */
    private static final float PROX_STATE = (float) (MELEE_RANGE / 16.0);
    /** 距离惩罚 N 帧（tick）：持续超标该时长后开始指数惩罚 */
    private static final int PROX_START_TICKS = 20;
    /** 距离惩罚指数上限（每超标 1 秒翻倍，封顶值） */
    private static final int PROX_MAX = -16;
    /** 连击熔断窗口（tick）：3 秒无近战命中 → 清空华丽倍率并倒扣 */
    private static final int COMBO_TIMEOUT_TICKS = 60;
    /** 连击熔断倒扣 */
    private static final int COMBO_TIMEOUT_PENALTY = -30;
    /** 突进判定：最近 4 个决策点（20 tick）水平位移超过该值视为突进 */
    private static final double GAP_CLOSE_DIST = 2.0;
    /** 每个女仆的位置历史（决策点采样，4 个） */
    private static final java.util.Map<java.util.UUID, java.util.ArrayDeque<net.minecraft.world.phys.Vec3>> POS_HISTORY = new java.util.HashMap<>();
    /** 每个女仆脱离近战持续 tick */
    private static final java.util.Map<java.util.UUID, Integer> PROX_TICKS = new java.util.HashMap<>();
    /** 每个女仆连击熔断冷却 tick（防反复触发） */
    private static final java.util.Map<java.util.UUID, Integer> COMBO_COOLDOWN = new java.util.HashMap<>();

    private RlBrain() {
    }

    /**
     * 每 tick 调用（技能 MaidTick 内）：节流决策 + 数据记录维护。
     */
    public static void tick(MaidPatch<?> patch) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        // V13：学习技能书且战斗模式的女仆全部由 RL 决策（rl.properties enable_all_maids 可关）；
        // 训练数据采集仍仅限竞技场女仆（残留女仆混入会污染训练数据）
        RlConfig.ensureLoaded();
        if (!RlConfig.enableAllMaids || !patch.isFightMode()) {
            return;
        }
        // V46：每帧扫描锁定范围内的敌对生物，维护多目标攻击列表
        TargetTracker.update(maid);
        // 根据攻击列表中全部目标的威胁/血量/距离选择当前主目标
        TargetTracker.TrackedTarget priority = TargetTracker.selectPriorityTarget(maid);
        if (priority != null && priority.getEntity() != null && priority.getEntity().isAlive()) {
            maid.getBrain().setMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.ATTACK_TARGET, priority.getEntity());
            patch.setAttakTargetSync(priority.getEntity());
        }
        boolean arena = org.eftlm.stylish.arena.AutoArena.isArenaMaid(maid);
        RlDataRecorder.tick(maid);
        // P4 战略层：低频规则修正（武器适配/评分策略/资源提示），先于反应层
        StrategicLayer.tick(patch, maid, maid.tickCount);
        // P1 反应层：每 tick 抢占式防守（受控状态机/前摇规避/弹道拦截），
        // 接管时本 tick 不输出 RL 动作（目标追踪/数据维护照常）
        if (ReactiveLayer.tick(patch, maid)) {
            return;
        }
        // 反应层忙碌窗口内跳过决策输出（防双系统抢动画）
        if (ReactiveLayer.isBusy(maid)) {
            return;
        }
        if (maid.tickCount % DECISION_INTERVAL != 0) {
            return;
        }
        // P5.6 自适应规则模式：自适应影子女仆 / 自我博弈对手女仆不推理 RL
        // （CombatLibrary.tick 已驱动节奏统计/经验调度出招；反应层防守照常在其上方运行）
        if (CombatLibrary.isRuleControlled(maid)) {
            return;
        }
        float[] state = RlState.collect(patch);
        // 动态行动空间（Agent 注册表）：固定段 0..10 + 各执行器贡献的技能池槽位 11..26
        RlActionSlot[] layout = RlActionRegistry.buildLayout(patch);
        java.util.List<Integer> valid = validActions(layout);
        int action;
        float[] probs = null;
        boolean modelSrc = false;
        RlModel m = ensureModel(maid);
        if (m != null
                && (m.getInputDim() == RlState.STATE_DIM
                || m.getInputDim() == RlState.OLD_STATE_DIM_18
                || m.getInputDim() == RlState.LEGACY_STATE_DIM)
                && m.getOutputDim() == RlActEvent.TOTAL_ACTIONS) {
            // 旧 16/18 维模型兼容：截取前 N 维（新多目标特征不参与）
            float[] in = m.getInputDim() == state.length
                    ? state : java.util.Arrays.copyOf(state, m.getInputDim());
            probs = m.forward(in);
            // 无效行动掩码：无执行器的槽位置 0（argMax 天然不选）；
            // P5.6 命中网格经验掩码：该技能动画已有命中网格且当前目标不在网格 → 置 0
            // （只作用推理期选择，不进训练数据；减少无效出招、加快训练收敛）
            LivingEntity maskTarget = patch.getTarget();
            for (int i = 0; i < layout.length; i++) {
                if (layout[i] == null || CombatLibrary.shouldMaskSlot(maid, layout[i], maskTarget)) {
                    probs[i] = 0.0F;
                }
            }
            // ε-greedy 探索（仅在有效行动内随机）
            if (RANDOM.nextFloat() < RlConfig.epsilon) {
                action = valid.get(RANDOM.nextInt(valid.size()));
            } else {
                action = argMax(probs);
            }
            modelSrc = true;
        } else {
            if (m != null && !dimWarnLogged) {
                dimWarnLogged = true;
                LOGGER.warn("[RL] model dim mismatch input={} output={} (need {} or {} / {}), rule fallback",
                        m.getInputDim(), m.getOutputDim(), RlState.STATE_DIM, RlState.LEGACY_STATE_DIM,
                        RlActEvent.TOTAL_ACTIONS);
            }
            action = defaultPolicy(patch, state);
        }
        // V46 起身硬约束：被击倒时无论模型输出什么都强制翻滚起身
        // （与 MaidTick 自动起身保持一致，并让训练数据记录为 roll）
        if (state[9] > 0.5F) {
            action = RlActEvent.ACT_ROLL;
            RlTrace.event(maid, "forced_roll", "knockdown hard constraint overrides model");
        }
        // 近战强制（V11+V18 约束）：主手非远程（枪/弓弩）时"远程"决策一律回退近战——
        // 自动换枪射击会污染行为多样性（装备枪后纯远程流劣化）且未持枪也会射击（行为不匹配）
        if (action == RlActEvent.ACT_RANGED && !(state[6] >= 1.0F && state[6] < 3.0F)) {
            action = state[0] < 0.24F || state[6] < 1.0F
                    ? (state[5] > 0.5F ? RlActEvent.ACT_GUNSLINGER_ATK : RlActEvent.ACT_SWORDMASTER_ATK)
                    : RlActEvent.ACT_IDLE;
            RlTrace.event(maid, "ranged_fallback", "melee-only weapon, ranged demoted to " + action);
        }
        // 无目标约束（V17 修复）：s[0] 无目标与远目标同值 1.0，模型无法区分空场——
        // 无有效目标时攻击类动作（近战/远程/JC/终极/技能槽/切武器）降级为待机，杜绝空挥
        // V18 扩展：闲时切武器（cycle）与闲时翻滚（roll，未被击倒时）同样降级
        if (action >= RlActEvent.ACT_SKILL_BASE
                || action == RlActEvent.ACT_SWORDMASTER_ATK || action == RlActEvent.ACT_GUNSLINGER_ATK
                || action == RlActEvent.ACT_ULTIMATE || action == RlActEvent.ACT_JC
                || action == RlActEvent.ACT_RANGED
                || action == RlActEvent.ACT_CYCLE_MELEE
                || (action == RlActEvent.ACT_ROLL && state[9] < 0.5F)) {
            LivingEntity t = patch.getTarget();
            if ((t == null || !t.isAlive()) && !hasAnyTarget(maid)) {
                action = RlActEvent.ACT_IDLE;
                RlTrace.event(maid, "idle_no_target", "no valid target, demoted to idle");
            }
        }
        // 行动槽（技能槽位行动携带对应 SkillSpec）
        RlActionSlot slot = layout[action];
        if (slot == null) {
            slot = RlActionSlot.generic(RlActEvent.ACT_IDLE, "idle");
            action = RlActEvent.ACT_IDLE;
        }
        // P0 观测：决策链路追踪（执行结果由 RlActHandler 补全到 trace）
        RlTrace.recordDecision(maid, maid.tickCount, modelSrc ? "model" : "rule", action, slot.label(), probs);
        // P0 shadow：模型只推理不执行——行为表/规则接管（rlDrivesAttacks 同时视为未加载），
        // 不 post 事件、不采集训练数据（避免影子决策污染轨迹）；trace 行标记 NOOP 供对照
        if (RlConfig.shadowMode) {
            RlTrace.resolveExec(maid, RlExecResult.NOOP, patch, patch.getTarget());
            return;
        }
        // 行动输出进入事件总线 → 注册执行状态机实施（执行结果由 RlExecResultEvent 反哺）
        try {
            MinecraftForge.EVENT_BUS.post(new RlActEvent(maid, action, state, slot));
        } catch (Throwable t) {
            LOGGER.error("[RL] RlActEvent post FAILED action={}", action, t);
            return; // 事件执行失败则不记录该步
        }
        // 位置采样：所有启用女仆（供命中事件做突进判定）
        samplePosition(maid);
        if (!arena) {
            return; // 非竞技场女仆不采集轨迹、不做奖励塑形（防污染训练数据）
        }
        // 记录训练数据：若模型输出待机但女仆实际在连段/攻击，记录为对应攻击动作
        // （行为表驱动的实际战斗与 RL 输出对齐，避免训练数据与真实行为脱节）
        int recorded = action;
        if (action == RlActEvent.ACT_IDLE && patch.getEntityState().attacking()) {
            recorded = StyleState.getStyle(maid) == AnimKit.STYLE_GUNSLINGER
                    ? RlActEvent.ACT_GUNSLINGER_ATK : RlActEvent.ACT_SWORDMASTER_ATK;
        }
        // 多样性奖励塑形：连段熵奖励 + 动作滥用衰减（作用于当前决策步）
        applyDiversityReward(maid, recorded);
        // 同态动作衰减：远程连射收益递减，近战/闪避/弹反后重置（把远程逼成近战后的附加输出）
        applyRangedStreakReward(maid, recorded);
        // V46 防守塑形：鼓励在威胁/倒地时使用闪避、翻滚、弹反
        applyDefensiveReward(maid, recorded, state);
        // V9：距离扣分惩罚（脱离近战判定范围持续超标 → 指数级负奖励，逼 AI 追击贴脸）
        applyProximityPenalty(maid, state);
        // V9：连击断档熔断（3 秒无近战命中 → 清空华丽倍率 + 大幅倒扣）
        applyComboTimeout(maid);
        // P3：轨迹 v2 记录动作语义标签（slot.label()），供训练侧跨布局语义对齐
        RlDataRecorder.recordStep(maid, state, recorded, slot.label());
        // 节流诊断：每 100 tick 打印一次（验证决策链路持续运行）
        if (maid.tickCount % 100 == 0) {
            LOGGER.info("[RL] heartbeat: tick={} action={} buffer={} trace={} stack={} skills={}", maid.tickCount, action,
                    RlDataRecorder.bufferSize(), RlTrace.bufferSize(),
                    StyleState.getSkillStack(maid),
                    countSkills(layout));
        }
        // 临时诊断：目标获取链路验证（每 200 tick）
        if (maid.tickCount % 200 == 0) {
            net.minecraft.world.entity.LivingEntity t1 = patch.getTarget();
            net.minecraft.world.entity.LivingEntity t2 = maid.getTarget();
            LOGGER.info("[RL] diag target: patch={} tlm={} dist={}", t1 != null ? t1.getType().getDescriptionId() : "null",
                    t2 != null ? t2.getType().getDescriptionId() : "null",
                    t1 != null ? String.format("%.1f", maid.distanceTo(t1)) : (t2 != null ? String.format("%.1f", maid.distanceTo(t2)) : "n/a"));
        }
    }

    /** 无目标判定（与 RlState 目标通道一致：patch 目标 → TLM brain 目标 → arena 最近标靶） */
    private static boolean hasAnyTarget(EntityMaid maid) {
        LivingEntity t = maid.getTarget();
        if (t != null && t.isAlive()) {
            return true;
        }
        return org.eftlm.stylish.arena.AutoArena.findNearestTarget(maid) != null;
    }

    /** 布局中非空槽位（有效行动）索引与技能槽数量 */
    private static java.util.List<Integer> validActions(RlActionSlot[] layout) {
        java.util.List<Integer> valid = new java.util.ArrayList<>(RlActEvent.TOTAL_ACTIONS);
        for (int i = 0; i < layout.length; i++) {
            if (layout[i] != null) {
                valid.add(i);
            }
        }
        return valid;
    }

    private static int countSkills(RlActionSlot[] layout) {
        int n = 0;
        for (int i = RlActEvent.ACT_SKILL_BASE; i < layout.length; i++) {
            if (layout[i] != null) {
                n++;
            }
        }
        return n;
    }

    /**
     * 加载模型（首次调用时；路径由 rl.properties model_file 决定，
     * 默认 config/eftlm_stylish/rl_model.bin）。
     * P4：影子评估——竞技场影子女仆使用 shadow_model_file 对应模型
     * （未配置/加载失败时回退主模型）。
     */
    public static RlModel ensureModel(EntityMaid maid) {
        if (org.eftlm.stylish.arena.AutoArena.isShadowMaid(maid)) {
            return ensureShadowModel();
        }
        return ensureModel();
    }

    private static RlModel ensureModel() {
        if (!modelChecked) {
            modelChecked = true;
            Path path = RlConfig.modelPath();
            model = RlModel.load(path);
            if (model == null) {
                LOGGER.info("[RL] no model found at {}, using rule default policy", path);
            }
        }
        return model;
    }

    private static RlModel ensureShadowModel() {
        if (!shadowModelChecked) {
            shadowModelChecked = true;
            Path path = RlConfig.shadowModelPath();
            shadowModel = RlModel.load(path);
            if (shadowModel == null) {
                LOGGER.info("[RL] shadow model disabled/not found ({}), shadow maid falls back to main model", path);
            }
        }
        return shadowModel != null ? shadowModel : ensureModel();
    }

    /** 热重载模型（迭代部署：替换 rl_model.bin 后经 /rl reload 生效，无需重启服务器）；
     *  P4：同时重载影子评估模型。 */
    public static synchronized boolean reloadModel() {
        Path path = RlConfig.modelPath();
        RlModel next = RlModel.load(path);
        if (next == null) {
            LOGGER.error("[RL] reload failed: {} not loadable, keeping current model", path);
            return false;
        }
        model = next;
        modelChecked = true;
        dimWarnLogged = false;
        // 影子模型重载（失败不影响主模型）
        shadowModelChecked = false;
        Path sPath = RlConfig.shadowModelPath();
        if (sPath != null) {
            RlModel sNext = RlModel.load(sPath);
            if (sNext != null) {
                shadowModel = sNext;
                shadowModelChecked = true;
            }
        } else {
            shadowModel = null;
        }
        if (!(next.getInputDim() == RlState.STATE_DIM
                || next.getInputDim() == RlState.OLD_STATE_DIM_18
                || next.getInputDim() == RlState.LEGACY_STATE_DIM)
                || next.getOutputDim() != RlActEvent.TOTAL_ACTIONS) {
            LOGGER.warn("[RL] reloaded model dim mismatch input={} output={} (need {} or {} / {}), rule fallback will be used",
                    next.getInputDim(), next.getOutputDim(), RlState.STATE_DIM, RlState.LEGACY_STATE_DIM,
                    RlActEvent.TOTAL_ACTIONS);
        }
        LOGGER.info("[RL] model reloaded from {}: input={} output={}", path, next.getInputDim(), next.getOutputDim());
        return true;
    }

    /** 当前模型（诊断用，可能为 null；影子女仆返回影子模型） */
    public static RlModel currentModel() {
        return ensureModel();
    }

    public static boolean isModelLoaded() {
        return ensureModel() != null;
    }

    /**
     * 女仆被击杀 / 移除时释放其全部按 UUID 隔离的 RL 状态（决策历史 / 衰减池 /
     * 位置采样 / 惩罚计数器），防止长期运行的训练服务器内存无限增长。
     */
    public static void forgetMaid(java.util.UUID id) {
        HISTORY.remove(id);
        STALENESS.remove(id);
        HISTORY_IDX.remove(id);
        RANGED_STREAK.remove(id);
        POS_HISTORY.remove(id);
        PROX_TICKS.remove(id);
        COMBO_COOLDOWN.remove(id);
        TargetTracker.forgetMaid(id);
        ReactiveLayer.forget(id);
        ItemCombat.forget(id);
        CombatLibrary.forget(id);
        RlTrace.forget(id);
    }

    /**
     * 规则默认策略（离线训练初始数据的行动来源；无模型时的兜底）。
     */
    private static int defaultPolicy(MaidPatch<?> patch, float[] s) {
        EntityMaid maid = (EntityMaid) patch.getOriginal();
        // 被击倒 → 翻滚
        if (s[9] > 0.5F) {
            return RlActEvent.ACT_ROLL;
        }
        // 敌人攻击中且近身 → 弹反/格挡
        if (s[7] > 0.5F && s[0] < 0.22F) {
            return StyleState.getFlair(maid) < 25.0F ? RlActEvent.ACT_PARRY : RlActEvent.ACT_BLOCK;
        }
        // 目标远 + 有远程 → 切远程
        if (s[0] > 0.3F && s[13] > 0.5F && s[6] < 2.0F) {
            return RlActEvent.ACT_RANGED;
        }
        // 技能满层 → 大招
        if (s[12] > 0.95F) {
            return RlActEvent.ACT_ULTIMATE;
        }
        // 目标浮空 → JC
        if (s[8] > 0.5F) {
            return RlActEvent.ACT_JC;
        }
        // 主手远程且目标近 → 切回近战
        if (s[6] >= 1.0F && s[0] < 0.2F) {
            return RlActEvent.ACT_CYCLE_MELEE;
        }
        // 连段计数满 → 轮换近战武器
        if (s[11] > 0.95F && s[6] < 1.0F) {
            return RlActEvent.ACT_CYCLE_MELEE;
        }
        // 有目标且近身 → 按风格攻击
        if (s[0] < 0.24F) {
            return s[5] > 0.5F ? RlActEvent.ACT_GUNSLINGER_ATK : RlActEvent.ACT_SWORDMASTER_ATK;
        }
        return RlActEvent.ACT_IDLE;
    }

    /**
     * 同态动作衰减（V7 约束）：连续远程射击收益递减 1.0→0.5→0.1→0→负数（怯战惩罚）。
     * 只有执行近战攻击 / 有效闪避 / 弹反后，远程衰减池才重置回 1.0。
     */
    private static void applyRangedStreakReward(EntityMaid maid, int action) {
        java.util.UUID id = maid.getUUID();
        int streak = RANGED_STREAK.getOrDefault(id, 0);
        if (action == RlActEvent.ACT_RANGED) {
            streak++;
            RANGED_STREAK.put(id, streak);
            if (streak - 1 < RANGED_DECAY.length) {
                int reward = (int) (10 * RANGED_DECAY[streak - 1]);
                if (reward > 0) {
                    RlDataRecorder.addReward(maid, reward);
                }
            } else {
                RlDataRecorder.addReward(maid, RANGED_COWARD_PENALTY); // 龟缩怯战惩罚
            }
        } else if (action == RlActEvent.ACT_SWORDMASTER_ATK || action == RlActEvent.ACT_GUNSLINGER_ATK
                || action == RlActEvent.ACT_ULTIMATE || action == RlActEvent.ACT_JC
                || action == RlActEvent.ACT_PARRY || action == RlActEvent.ACT_BLOCK
                || action == RlActEvent.ACT_DODGE) {
            RANGED_STREAK.put(id, 0); // 近战/弹反/闪避 → 重置衰减池
        }
    }

    private static int argMax(float[] probs) {
        int best = 0;
        for (int i = 1; i < probs.length; i++) {
            if (probs[i] > probs[best]) {
                best = i;
            }
        }
        return best;
    }

    // ------------------------------------------------------------------
    // V9 约束：突进奖励 / 距离惩罚 / 连击熔断 / 远程归零
    // ------------------------------------------------------------------

    /** 决策点采样位置（保留最近 4 个 = 20 tick 窗口，供命中事件做突进判定） */
    private static void samplePosition(EntityMaid maid) {
        java.util.UUID id = maid.getUUID();
        java.util.ArrayDeque<net.minecraft.world.phys.Vec3> deque = POS_HISTORY.computeIfAbsent(id, k -> new java.util.ArrayDeque<>());
        deque.addLast(maid.position());
        while (deque.size() > 4) {
            deque.removeFirst();
        }
    }

    /**
     * 突进（Gap-Closing）判定：近战命中事件调用。
     * 最近 4 个决策点（20 tick）内水平位移超过 {@value #GAP_CLOSE_DIST} 格 → 视为带位移的突进攻击。
     */
    public static boolean isGapClose(EntityMaid maid) {
        java.util.ArrayDeque<net.minecraft.world.phys.Vec3> deque = POS_HISTORY.get(maid.getUUID());
        if (deque == null || deque.size() < 4) {
            return false;
        }
        net.minecraft.world.phys.Vec3 old = deque.peekFirst();
        net.minecraft.world.phys.Vec3 now = maid.position();
        double dx = now.x - old.x;
        double dz = now.z - old.z;
        return Math.sqrt(dx * dx + dz * dz) >= GAP_CLOSE_DIST;
    }

    /** 纯远程收益归零判定：远程连射 ≥ 4 次（无近战穿插）时，远程命中不再结算任何奖励 */
    public static boolean isRangedStreakDead(EntityMaid maid) {
        return RANGED_STREAK.getOrDefault(maid.getUUID(), 0) >= 4;
    }

    /**
     * 距离扣分惩罚（Proximity Penalty）：
     * 女仆与最近有效敌方单位（或无目标兜底 16 格）的距离超过近战判定范围
     * 持续超过 {@value #PROX_START_TICKS} tick → 负奖励按超标秒数指数级增长（封顶 {@value #PROX_MAX}）。
     * 只要回到近战范围内，计数器立即清零——惩罚不是惩罚"远程"，而是惩罚"不贴脸"。
     */
    private static void applyProximityPenalty(EntityMaid maid, float[] state) {
        java.util.UUID id = maid.getUUID();
        int ticks = PROX_TICKS.getOrDefault(id, 0);
        if (state[0] <= PROX_STATE) {
            PROX_TICKS.put(id, 0);
            return;
        }
        ticks += DECISION_INTERVAL;
        PROX_TICKS.put(id, ticks);
        if (ticks > PROX_START_TICKS) {
            int seconds = ticks / 20;
            // 每超标 1 秒惩罚翻倍，封顶 PROX_MAX(-16)；seconds 先钳制再位移，
            // 修复 1 << seconds 在 seconds>=31 时溢出为负 → 惩罚反转为 +21 亿奖励的 bug
            int magnitude = seconds >= 4 ? -PROX_MAX : (1 << seconds);
            int penalty = -Math.min(magnitude, -PROX_MAX);
            RlDataRecorder.addReward(maid, Math.max(penalty, PROX_MAX));
        }
    }

    /**
     * 连击断档熔断（Combo Timeout）：
     * 距上次近战伤害判定超过 {@value #COMBO_TIMEOUT_TICKS} tick（3 秒）→ 清空华丽倍率
     * 并大幅倒扣（冷却 {@value #COMBO_TIMEOUT_TICKS} tick 防反复触发）。
     */
    private static void applyComboTimeout(EntityMaid maid) {
        int combo = maid.getPersistentData().getInt("eftlm_stylish:combo_count");
        if (combo <= 0) {
            return;
        }
        int lastHit = StyleState.getTick(maid, StyleState.LAST_HIT);
        if (maid.tickCount - lastHit <= COMBO_TIMEOUT_TICKS) {
            return;
        }
        maid.getPersistentData().putInt("eftlm_stylish:combo_count", 0);
        java.util.UUID id = maid.getUUID();
        int cooldown = COMBO_COOLDOWN.getOrDefault(id, 0);
        if (maid.tickCount - cooldown > COMBO_TIMEOUT_TICKS) {
            RlDataRecorder.addReward(maid, COMBO_TIMEOUT_PENALTY);
            COMBO_COOLDOWN.put(id, maid.tickCount);
        }
    }

    /**
     * V46 防守塑形：在敌方攻击前摇 / 被击倒 / 弹反窗口内使用对应防守动作时给额外奖励，
     * 缓解模型因狭小竞技场过拟合而放弃翻滚/闪避的问题。
     */
    private static void applyDefensiveReward(EntityMaid maid, int action, float[] state) {
        if (action == RlActEvent.ACT_DODGE && state[7] > 0.5F) {
            RlDataRecorder.addReward(maid, 12); // 敌方攻击中闪避：先读/规避
        } else if (action == RlActEvent.ACT_ROLL && state[9] > 0.5F) {
            RlDataRecorder.addReward(maid, 10); // 被击倒立即翻滚起身
        } else if (action == RlActEvent.ACT_PARRY && state[7] > 0.5F) {
            RlDataRecorder.addReward(maid, 8);
        } else if (action == RlActEvent.ACT_BLOCK && state[7] > 0.5F) {
            RlDataRecorder.addReward(maid, 5);
        }
    }

    /**
     * 多样性奖励塑形（每决策步注入当前步奖励）：
     * <ol>
     *     <li><b>连段熵（Combo Entropy）</b>：最近 50 次决策的动作分布香农熵越高，
     *         额外奖励越大（熵 ∈ [0, log2(N)]，归一化后 × {@value #ENTROPY_BONUS_MAX}）——奖励动作多样性。</li>
     *     <li><b>动作衰减池（Staleness Penalty）</b>：窗口内同一动作使用超过
     *         {@value #STALE_THRESHOLD} 次 → 每步 {@value #STALE_PENALTY} 惩罚（该动作奖励权重趋零）；
     *         打出切换武器 / 远程 / 浮空 JC / 弹反时衰减池立即重置。</li>
     * </ol>
     * 按女仆 UUID 隔离（多女仆采集互不干扰），女仆死亡后状态自动废弃。
     */
    private static void applyDiversityReward(EntityMaid maid, int action) {
        java.util.UUID id = maid.getUUID();
        // 窗口初始化为 -1（空哨兵），与重置后的 fill(-1) 一致；此前 new int[] 全 0
        // 会被当作"动作 0 已记录 50 次"，导致每个女仆前 50 步动作 0 恒超滥用阈值
        int[] window = HISTORY.computeIfAbsent(id, k -> {
            int[] w = new int[ENTROPY_WINDOW];
            java.util.Arrays.fill(w, -1);
            return w;
        });
        int[] stale = STALENESS.computeIfAbsent(id, k -> new int[RlActEvent.TOTAL_ACTIONS]);
        Integer idxBox = HISTORY_IDX.computeIfAbsent(id, k -> 0);
        int idx = idxBox;

        // 环形窗口：覆盖最旧动作，更新窗口计数
        int oldest = window[idx];
        if (oldest >= 0) {
            stale[oldest]--;
        }
        window[idx] = action;
        stale[action]++;
        idx = (idx + 1) % ENTROPY_WINDOW;
        HISTORY_IDX.put(id, idx);

        // 衰减池重置：切换近战/远程、浮空 JC、弹反 → 全部清零
        if (action == RlActEvent.ACT_CYCLE_MELEE || action == RlActEvent.ACT_RANGED
                || action == RlActEvent.ACT_JC || action == RlActEvent.ACT_PARRY) {
            java.util.Arrays.fill(window, -1);
            java.util.Arrays.fill(stale, 0);
            HISTORY_IDX.put(id, 0);
            return; // 重置步本身不参与惩罚
        }

        // Staleness 惩罚：窗口内同动作滥用
        if (stale[action] > STALE_THRESHOLD) {
            RlDataRecorder.addReward(maid, STALE_PENALTY);
        }

        // Combo Entropy 奖励：窗口内动作分布香农熵（归一化）
        int total = 0;
        for (int c : stale) {
            total += c;
        }
        if (total > 0) {
            float entropy = 0.0F;
            for (int c : stale) {
                if (c > 0) {
                    double p = (double) c / total;
                    entropy -= p * (float) (Math.log(p) / Math.log(2));
                }
            }
            float bonus = (entropy / (float) Math.log(RlActEvent.TOTAL_ACTIONS)) * ENTROPY_BONUS_MAX;
            if (bonus >= 1.0F) {
                RlDataRecorder.addReward(maid, (int) bonus);
            }
        }
    }
}
