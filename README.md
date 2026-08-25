# EFTLM-Stylish（史诗战斗：车万女仆「华丽连段」）

为《车万女仆（Touhou Little Maid）》+《EpicFight》战斗体系打造的全自动战斗 AI 扩展模组。
女仆学会「华丽连段」技能书后，由 **感知 → 三层决策 → 执行 → 反馈** 的闭环自主作战：
反应层（每 tick 规则）→ 战术层（RL 神经网络，5 tick）→ 战略层（低频规则），
并在专用服务器上持续采集轨迹、离线训练、自动迭代部署。

- **Minecraft 1.20.1 / Forge 47.4.13 / Java 17**
- 依赖：EpicFight 20.14.17、车万女仆 TLM 1.5.3、EFN（Invincible）、WOM（WeaponsOfMiracles）、Avalon 等（见 `mods.toml`）

---

## 功能特性

| 阶段 | 特性 |
|---|---|
| **P0 观测** | 决策链路追踪（`/rl dump` CSV）、影子模式（模型只推理不执行）、RL 状态 32 维 |
| **P1 反应层** | 受控状态机起身/前摇规避/弹道拦截（每 tick 抢占式防守，忙碌窗口 6t） |
| **P2 动作语义** | Commitment 目录（运行时读取 Avalon 动画帧数据：前摇/判定窗/后摇/可打断性）、稳定技能槽（槽位=技能身份+掩码） |
| **P2.5 第三方兼容** | 行为表仲裁（hybrid）、AV/DMC/epicmoon 武器支持、道具战斗（方块格挡/水桶/珍珠反击） |
| **P3 训练重写** | 轨迹 v2（32 维状态 + 动作 + 奖励 + 语义标签）、Actor-Critic/AWR 训练、部署门禁（acc 回退 >0.02 拒绝）、教官 AI 课程 |
| **P4 战略层** | 武器适配（霸体切近战/放风筝切远程）、评分策略（切换连携）、课程模式（course.json 自动换对手）、影子评估（双女仆 A/B） |
| **P5 空间+华丽** | 危险区空间感知（SpatialMap）、评分结算奖励、切换连携 +15、AV 防御 mixin、胜率统计 |
| **P5.5 方块武器** | 主手方块=方块武器（圆石/深板岩/黑曜石注册 + 通用模板），放置技能放主手方块且不消耗 |
| **P5.6 自适应学习** | 敌方节奏统计（攻击间隔 EMA）、命中经验（极坐标扇区桶）、经验驱动技能调度（热度衰减）、增益窃取（`buff_steal=auto` 仅训练时）、课程终局自我博弈（自适应对手女仆） |

---

## 架构总览

```
                         ┌─────────────────────────────────────┐
  感知层（每 tick）       │ TargetTracker（威胁排序/幽灵目标/霸体） │
                         │ ProjectilePerception（弹道 ETA）      │
                         │ SpatialMap（危险区栅格）               │
                         └─────────────────────────────────────┘
                                        ▼
  决策层                  反应层 ReactiveLayer（每 tick 规则：受控起身/前摇闪避/弹道拦截）
                        ──────── 抢占式防守，接管时本 tick 不输出 RL ────────
                          战术层 RlBrain（5 tick：RlState 32 维 → MLP 64-64 → 64 动作）
                        ──────── 高频决策主体（训练数据来源）────────
                          战略层 StrategicLayer（40 tick 规则：武器适配/评分/资源提示）
                                        ▼
  执行层                  事件总线 RlActEvent → RlActionRegistry（generic/efn_skill/defense）
                         CommitmentCatalog 门控（前摇/判定窗/后摇/取消点）
                                        ▼
  反馈闭环                RlExecResultEvent（结果反哺状态 s[16]/s[17]）
                         RlDataRecorder（轨迹 v2，仅竞技场采集）→ train/ 离线训练 → 自动部署
```

### 规则层与 RL 契约

- **RL 契约（训练/推理双端稳定）**：32 维状态、64 动作（11 通用 + 53 技能槽；防守槽固定 62/63）、Java 模型格式（`RlModel`）、槽位语义稳定（`slot_stable`）
- **规则层不进训练数据**：反应层/战略层/道具战斗/自适应学习全部为规则，只产生 trace 事件与推理期掩码，避免污染轨迹

---

## 快速开始（构建）

```bash
gradlew.bat compileJava        # 编译
gradlew.bat jar reobfJar       # 打包 + 反混淆（build/libs/eftlm_stylish-1.0.0.jar）
```

产物 jar 放入客户端/服务端 `mods/` 即可。mixin 通过 `targets` 字符串 + `@Pseudo` 兼容第三方闭源模组（未安装时静默跳过）。

---

## 服务器部署（训练环境）

```bash
# prod_server 目录下启动（jdk-17）
java @user_jvm_args.txt @libraries/net/minecraftforge/forge/1.20.1-47.4.13/win_args.txt nogui
```

- **竞技场**：全虚空超平坦世界 + 基岩平台（世界边界 = 平台边缘），女仆（绀珠之药 6 命 + 钻石甲 + 力量 II）对阵 AV 系 Boss；标靶不足自动补充、游离拉回、击杀立即补刷
- **课程模式**：`config/eftlm_stylish/course.json`（教官 AI 输出）每 5 分钟自动加载，换对手/调斗兽场参数
- **RCON**：`25575 / maidpilot123`（训练流水线经 RCON 拉布局、热重载模型、发 stop）

### 配置文件

**`config/eftlm_stylish/rl.properties`**

| 键 | 默认 | 说明 |
|---|---|---|
| `enable_all_maids` | true | 学习技能书且战斗模式的女仆全部由 RL 决策 |
| `epsilon` | 0.08 | ε-greedy 探索率 |
| `arbitration` | hybrid | 模型加载时行为表进攻让位 RL |
| `model_file` | rl_model.bin | 模型文件（config/eftlm_stylish/ 相对或绝对路径） |
| `shadow_model_file` | 空 | 影子评估模型（竞技场影子女仆专用） |
| `slot_stable` | true | 稳定技能槽（槽位=技能身份+掩码） |
| `item_block_parry` / `block_parry_chance` | true / 0.4 | 受击方块格挡（放墙→速搭→垫高三级） |
| `item_block_weapon` | true | 主手方块=方块武器（放置技能放主手方块不消耗） |
| `item_water_extinguish` | true | 着火水桶灭火 |
| `item_pearl_counter` | true | 受击末影珍珠反击 |
| `adaptive_learn` | true | 自适应学习：敌方节奏统计（EMA）+ 命中经验数据源 |
| `adaptive_hitgrid` | true | 命中经验掩码（有经验且目标不在命中桶的技能置 0） |
| `buff_steal` | auto | 增益窃取：auto=仅竞技场训练采集时启用 / on / off |

**`config/eftlm_stylish/arena.properties`**

| 键 | 默认 | 说明 |
|---|---|---|
| `enabled` | true | 竞技场开关 |
| `entity` | 逗号分隔 | 标靶实体列表（多标靶轮流生成） |
| `count` / `interval` / `spawn_distance` | 1 / 400 / 12 | 标靶数量 / 补充间隔 / 生成距离 |
| `maid_main` / `maid_melee` / `maid_melee2` / `maid_ranged` | — | 女仆主手/背包武器 |
| `cage_enabled` / `cage_radius` / `cage_growth_minutes` | true / 8 / 30 | 斗兽场（随分钟生长扩圈） |
| `shadow_ai` | model | 影子女仆 AI：model=影子模型评估 / adaptive=自适应规则 AI |
| `selfplay` | false | 自我博弈：生成自适应对手女仆（女仆 vs 女仆；course.json `selfplay=true` 自动开启） |

### 运维命令（权限 2）

```
/rl status       模型/配置/执行器状态
/rl layout       稳定技能槽布局（训练侧语义对齐依据）
/rl adaptive     自适应学习状态（节奏统计/命中桶/窃取/规则模式）
/rl trace on|off 决策链路追踪开关
/rl dump [all|uuid]  导出决策链路 CSV（config/eftlm_stylish/dumps/）
/rl reload       热重载 rl_model.bin（迭代部署无需重启）
/arena stats     胜率统计（kills/deaths/revives/win_rate/shadow/selfplay/course）
```

---

## 训练流水线（train/）

```bash
python train/iterate.py --server-dir <prod_server> --project-dir <EFTLM-example> \
    [--hours 6] [--anchor server] [--immediate] [--once]
```

每轮迭代：轨迹快照（只取新文件，防半写损坏）→ 清洗（extract_melee / crucible / relabel）→
训练（`train_ppo.py`：BC 预热 → AWR/PPO，`--init` 续训 + `--pretrain` 近战数据 + `--relabel`）→
部署门禁（验证集 acc 回退 >0.02 拒绝部署）→ 教官 AI（`instructor.py`：薄弱点分析 → course.json）→
RCON 热重载模型。

- `--anchor server`：锚定服务器启动时刻，每 N 小时一轮；服务器重启自动重新锚定
- 课程完成信号：教官判定全面达标（weakness=balanced）→ course.json 写 `selfplay=true` → 竞技场自动生成**自适应对手女仆**（经验驱动规则 AI，与 RL 主女仆互相仇恨对打）——课程终局自我博弈

---

## 目录结构

```
src/main/java/org/eftlm/stylish/
├── rl/            # RL 决策链（RlBrain/RlState/RlActionRegistry/执行器/CommitmentCatalog）
│                  #   感知（TargetTracker/ProjectilePerception/SpatialMap）
│                  #   规则层（ReactiveLayer/StrategicLayer/ItemCombat/BlockWeaponRegistry/CombatLibrary）
│                  #   数据（RlDataRecorder 轨迹 v2 / RlTrace / RlCommand）
├── strategy/      # 武器库 WeaponArsenal / 评分 StyleState / 行为 CombatActions / AutoSkill
├── compat/        # 第三方武器适配（efn/wom）
├── mixin/         # 第三方防御 mixin（EFN LinkAnimation NPE / AV NullEntity patch 缺失）
├── EF/            # 技能书（StylishCombatSkill）/ 事件挂钩 / 行为表注册
└── arena/         # AutoArena（竞技场/课程/影子评估/自我博弈）
train/             # 训练流水线（train_ppo.py / iterate.py / instructor.py / 清洗脚本）
tools/             # 反编译产物与工具（内部调研用）
docs/              # 技术文档（架构重写技术报告等）
```

---

## 致谢

- **merlin204** ——《The Mimic》模组作者，慷慨授权本模组借鉴其"经验驱动招式调度"的机制思路（命中经验、节奏学习、连招调度、增益窃取等均基于此思路重新设计实现）
- **AnnoyingVillagers（烦人的村民）** —— 道具战斗、方块武器、NullEntity 防御 mixin 等机制的**思路来源**（源码 GPL-3.0 开源）

第三方模组的 mixin 均以 `@Pseudo` + `targets` 字符串 + `required:false` 静默兼容（未安装不影响运行），并仅做防御性修复/机制参考，不包含其专有资源。

---

## 开源许可

本模组开源发布。借鉴思路的代码均为独立实现（详见 `docs/架构重写技术报告_20260826.md` 3.9 合规说明）；不包含任何第三方模组的专有资源（模型/贴图/音频/源码）。
