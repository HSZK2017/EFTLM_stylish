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
- **RCON**：`25575`（训练流水线经 RCON 拉布局、热重载模型、发 stop）。
  密码**不再硬编码**（P0 修复 2026-09-10）：解析顺序 = `--rcon-password` CLI 参数 →
  环境变量 `EFTLM_RCON_PASSWORD` → `tools/rcon_password.txt`（单行，已入 `.gitignore`，不入库）；
  缺失时脚本 fail-closed 退出。真实密码以 `<prod_server>/server.properties` 的 `rcon.password` 为准。
  > 人工待办：本轮为不断线保留了现有密码，建议在维护窗口轮换
  > （改 `server.properties` → 同步 `tools/rcon_password.txt` → 重启服务器与 guardian）。

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
| `tick_multiplier` | 1.0 | 游戏刻加速倍率（1.0=20TPS；1.5=30TPS；/rl tickrate 热调） |
| `auto_tickrate` | false | P5.7 智能性能调度：空闲自动加速、负载自动降速、tick 停滞看门狗 |
| `auto_tickrate_min` / `max` | 1.0 / 8.0 | 自动调度倍率下限/上限（8.0=160TPS；本机实测最佳稳定档） |
| `auto_tickrate_step` | 0.5 | 每决策周期倍率步长 |
| `auto_tickrate_cpu_low` / `high` | 0.5 / 0.8 | 系统 CPU 空闲/过载阈值（升档/降档） |
| `auto_tickrate_tick_low_ms` / `high_ms` | 25 / 40 | tick 95%ile 耗时健康/过载阈值 |
| `auto_tickrate_interval` | 5 | 采样决策周期（秒） |
| `auto_tickrate_watchdog_stall` | 30 | tick 停滞秒数（看门狗强制降速阈值） |

**`config/eftlm_stylish/arena.properties`**

| 键 | 默认 | 说明 |
|---|---|---|
| `enabled` | true | 竞技场开关 |
| `own_world` | **false** | 是否"本存档归竞技场所有"：**只有 true 才在启动时清场**（discard 出生点 1024×128×1024 内的全部女仆与生物）。P0 修复 2026-09-10 前该行为是无条件执行的，普通存档会被误删；训练专用服请显式写 `own_world=true` |
| `entity` | 逗号分隔 | 标靶实体列表（多标靶轮流生成）。默认已**移除 `annoyingvillagers:alex`**（V54 实证：单 tick 4290 秒寻路 → watchdog 强杀） |
| `entity_blacklist` | `annoyingvillagers:alex` | 标靶黑名单；`course.json suggested_entities` 同样过滤。过滤后列表为空时回退默认标靶并 ERROR 告警（P0：旧实现会因空列表在补刷时除零崩服） |
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
/rl tickrate <倍率>   手动游戏刻加速（0.1~10；会暂停自动调度，/rl autotick on 恢复）
/rl autotick on|off|status  智能性能调度开关与状态
/arena stats     胜率统计（kills/deaths/revives/win_rate/shadow/selfplay/course）
```

---

## 智能性能调度（P5.7）+ Spark 监控 + 守护进程

### 架构

```
                    ┌────────────────────────────────────────────────┐
   AutoTicker（模组内，Java）                                        │
    每 5s 采样：系统 CPU（EMA）/ tick 95%ile / 实际 TPS（墙钟）       │
    滞回决策：CPU 空闲且 tick 健康 → 升档（+step，≤ max）            │
              CPU 过载 或 tick 慢 或 TPS 未达成 → 降档（-step）       │
              tick 停滞 ≥30s + CPU 满载 → 看门狗强制降速 + 报警文件    │
                    ↓ setMultiplier                                  │
   TickAccelerator（虚拟时钟 = Util.getMillis()×倍率，同源比较）       │
                    ↓                                                │
   spark（mods/spark-1.10.53-forge.jar）                             │
     /spark health / spark tps：系统 CPU/进程 CPU/tick 耗时/TPS 报告  │
                    ↓（guardian 经 RCON 触发 + 解析日志）              │
   guardian.ps1（外部守护进程）                                      │
     15s 心跳检查（RCON + 日志 heartbeat 新鲜度）                    │
     60s spark TPS 采样 → spark_tps.csv（实时监控曲线）               │
     TPS<15 连续 2 次 → RCON /rl tickrate 1.0 强制降速（应急减速）    │
     心跳超时 120s → 优雅 stop → 强杀 → 重启（上限 3 次）            │
     读取 watchdog_alarm.txt（AutoTicker 报警）→ 记录并纳入重启判定   │
                    └────────────────────────────────────────────────┘
```

### 用法

```powershell
# 1) rl.properties 开启（prod_server/config/eftlm_stylish/rl.properties）
#    auto_tickrate=true  auto_tickrate_max=8.0（空闲自动加速到 160TPS；本机实测最佳稳定档）

# 2) 启动服务器（自动调度随 ServerStarted 启动）

# 3) 启动守护进程（可放计划任务/后台窗口常驻）
powershell -ExecutionPolicy Bypass -File E:\program\JAVA\EFTLM-example\tools\guardian.ps1 `
    -ServerDir "E:\program\JAVA\touhou little maid - unknow sky area\prod_server" `
    -RconPort 25575

# 4) 监控
#    /rl autotick status         模组内调度状态（倍率/CPU/tick95/实际TPS）
#    prod_server/guardian/spark_tps.csv   spark TPS 实时曲线（每 60s 一行）
#    prod_server/guardian/guardian.state  守护状态（healthy/unhealthy/restarted）
#    /spark health / spark tps            手动查看 spark 报告（输出到服务器日志）
```

**说明**：spark 的 TPS 以游戏时间为基准（虚拟时钟下 8.0× 加速恒定报 ~20 = 100% 达成），
AutoTicker 用墙钟计算**真实** TPS（160 达成即判定健康）；guardian 的 TPS<15 阈值在加速
模式下等价于"游戏 tick 落后 25%"→ 强制降速，两级监控互补。

**性能探测结论（2026-08-27 实测）**：CPU 单核峰值 <80% 前提下逐档探测（2→8×），
tick95 全程 1~3ms、真实 TPS 达预期；9.0× 时目标 180TPS 超过 tick 循环自然上限 ~160，
AutoTicker 在 8.5↔9.0 震荡 → 最佳稳定倍率 **7.5~8.0（150~160TPS）**（战斗负载波动时
AutoTicker 自动在 7.0~7.5 间自适应）。探测脚本：`tools/monitor_perf.ps1`
（10s 采样整机/单核 CPU、内存、tick95、真实 TPS 写 CSV）。

**加速死锁根因与修复（2026-08-27 线程转储实证）**：超长 tick（100ms+，女仆死亡/复活
路径偶发）后，tick 节流判断 `virtualNow < nextTickTime` 进入等待，而
`Util.getMillis()`（已被 EFN 虚拟化为游戏运行毫秒，增长依赖 tick 执行）与 nextTickTime
双双冻结 → 互相等待无法自愈（降档无效）。修复三层：
1. **时钟复位**（AutoTicker）：停滞 15s 反射置 `f_129727_/f_129726_`=0 + 倍率降下限 →
   等待条件立即满足，tick 免重启恢复（最优先路径；09:51 实测自动恢复成功）；
2. **看门狗报警**：停滞 30s（无论 CPU 高低）写 `watchdog_alarm.txt` + 全线程转储
   `thread_dump_*.txt`（主线程栈=阻塞点实锤，`/rl dumpthreads` 可手动抓取）；
3. **guardian 联动**：新鲜报警（<90s）立即重启（不等 120s 心跳超时）；心跳超时 60s；
   启动后 90s 宽限期避免"服务器未就绪误判→重复启动"。

**V54 事故链与根治（2026-08-28，ServerWatchdog FATAL 实证）**：竞技场女仆在虚空平台
（invincible_dmc:void，y=-60）氧气不满（窒息判定）→ TLM `MaidBreathAirTask` 反复启动 →
`findAirPosition` → `canPathReach` 全图寻路（`MaidWrappedPathFinder` → `PathFinder` BFS →
`MaidUnderWaterNodeEvaluator`/`WalkNodeEvaluator` VoxelShape 碰撞）→ 单 tick 78~490 秒 →
ServerWatchdog 强杀（07:39:43/07:54:22 两次 crash）。处置链：
1. **根因定位**：`MaidBreathAirTask` 字节码实证触发条件 = 氧气<100 且无水下呼吸效果；
   `canPathReach` 不受 restrictTo 限制（全图 BFS）；
2. **效果方案失败**：给女仆加无限 `WATER_BREATHING` 效果 → 实测女仆 NBT 无
   `ActiveEffects`（TLM addEffect 被覆写/清除，原因未明）→ 弃用；
3. **根治（mixin）**：`MaidBreathTaskMixin`（`eftlm_stylish.mixins.json`）双方法 HEAD
   注入恒 false——`checkExtraStartConditions`（MCP 源码名）+ `m_6114_`（SRG 桥方法，
   `Behavior.canStart` 运行时分派入口）→ 呼吸任务永不启动（javap 实证两方法名均存在，
   `remap=false`，defaultRequire=1 失败即报错不静默）；
4. **guardian 单实例锁**（V54 放大器教训：多 guardian 并发 → 各自判 unhealthy → 并发
   重启风暴）：`guardian.lock`（含 pid，存活检测，陈旧锁自动接管）；
   **健康判定以心跳为准**（09:40 教训：RCON 失败不再独立判死——服务器卡顿期间 RCON
   可能受影响而 AutoTicker clock reset 正在自愈；心跳超时 120s 才进重启流程）。
5. **AutoTicker 取证改进**：15s 时钟复位分支同步写线程转储（原仅 30s 报警分支写，
   07:30 实例 3 次复位均无卡点证据）。
6. **目标实体黑名单**（09:23 crash 追加实证）：AV 弓箭手（alex）的
   `BowLineOfSightGoal.findClearShotPosition` → repath 全图寻路 → WalkNodeEvaluator
   节点缓存爆炸（单 tick 4290 秒）——与呼吸任务同模式（寻路到虚空爆炸）。
   `arena.properties entity_blacklist=annoyingvillagers:alex`（逗号分隔）：
   course.json `suggested_entities` 与 `entity` 均过滤，黑名单目标不生成。
7. **BlockItem 强转保护**（12:28 crash 实证）：AV 蛇刃（SnakeBladeEntity）AOE 攻击触发
   女仆反击 → `ItemCombat.tryPlaceBlockParry/tryActiveBlockWall` 把主手/背包物品强转
   `BlockItem`——女仆黑曜石耗尽/换手瞬间为 AirItem → ClassCastException 崩服。
   所有强转点改为 `instanceof BlockItem` 保护（含 pillar/active wall 分支）。
8. **guardian alarm 清理**（12:30 教训）：心跳健康时清除残留 `watchdog_alarm.txt`——
   否则新实例启动期 AutoTicker 停滞报警残留 → guardian 误判 unhealthy → 连环重启循环。
当前线上 = v52 模型 + restrictTo 24 + 弱目标 swordsman_herobrine + 上述修复，稳定。

**自动唤起 agent（dsh-inject 集成，2026-08-27）**：守护进程发现异常 / 训练迭代完成时
自动向 DeepSeek Harness 会话注入提示词（`--autolunch --session` **服务端直接投递**，
无需浏览器在线——插件 2026-08-28 更新后的直投模式，解决浏览器后台时的通知延迟）：
- guardian.ps1：UNHEALTHY（崩溃/卡死）→ 注入异常详情；重启成功/失败/达到上限 → 注入
  恢复状态；限频 300s 防风暴。参数：`-AgentSessionId`（**必填**，指定目标会话走直投）、
  `-InjectScript`（默认 `F:\Deepseek Harness\dsh-easy-use\dsh-inject\dsh-inject.py`）。
- iterate.py：每轮迭代完成 → 注入指标摘要（acc/nll/samples/弱点）。参数：
  `--inject-script`、`--agent-session`（**必填**，直投）。
- 本会话 ID：`session-013a71d2-5c4f-4f4e-b126-0d447596db21`（2026-09-11 起；改会话只需改 `power_startup.ps1` 的 `-AgentSessionId` 默认值）（`--list-sessions` 按
  cwd=EFTLM-example 确认）。
- **注意**：guardian.ps1 含中文，必须保留 **UTF-8 BOM**（Windows PowerShell 5.1 按
  GBK 读取无 BOM 文件会因乱码破坏语法）。

**P5.8 selfplay 自我博弈（2026-08-27 构建，v51 部署时启用）**：
- **模型池（历史策略池）**：`config/eftlm_stylish/model_pool/*.bin`（iterate 部署时自动
  维护最近 3 个历史版本）→ 对手按策略选择：`rl.properties selfplay_opponent=`
  `history`（池中随机，默认）/ `champion`（池中最新=冠军基准）/ `latest`（当前模型）/
  `random`（随机动作注入，比例 `selfplay_random_ratio=0.2`）。
- **对手驱动**：selfplay 对手女仆（isAdaptiveMaid）由模型池推理驱动（原规则 AI 模式
  仅保留给 adaptive shadow），`CombatLibrary.isRuleControlled` 已移除对手女仆。
- **延迟对手更新**：iterate 每轮（~6h）才部署新模型并刷新池 → 对手天然滞后一版；
  每次对手女仆 spawn 随机取池（回合级多样化）。
- **启用**：course.json 或 arena.properties `selfplay=true`（instructor 课程完成后自动
  设置）→ 对手女仆登场；主女仆轨迹照常采集（对手不采集，防数据污染）。
- **训练稳定性约束（train_ppo.py）**：① 反向 KL 正则（kl_beta=0.05，BC/AWR 均对
  训练起点快照约束，防策略剧变）；② 熵正则（AWR 0.05 + BC 0.01）；③ 严格数据过滤
  （单动作占比 >95% 的低信息轨迹丢弃 + 奖励峰值裁剪 ±100）。


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

### 产物保留与快照一致性（P2-5 / P2-4，2026-09-11）

```bash
python train/retention.py                            # 干跑：打印清理计划与可回收空间
python train/retention.py --apply                    # 执行（session 归档需再加 --archive-sessions）
python train/retention.py --only live --apply        # 只清理已进过快照的活跃轨迹
python train/seed_snapshot.py --verify               # 自检：水位线之前是否还有漏采文件
python train/seed_snapshot.py --session-dir <目录>   # 部署前播种（逐条核验后才推进水位线）
python train/seed_snapshot.py --quarantine           # 把孤儿轨迹移入 trajectories_quarantine/
```

- `retention.py` **默认干跑**；活跃轨迹必须"同名文件存在于某个 session 快照"才删，未命中记为孤儿并保留；
  删除动作全部写审计日志 `train/models/retention_audit.log`。实测一次性回收 101,490 项 / 5.3 GB。
- `seed_snapshot.py` 解决"部署时把 `snapshot_state.txt` 直接写成 now → 上一轮快照到重启之间
  采集的轨迹**永久漏采**且无告警"这一事故（2026-09-11 实测 1,343 条）。
  **部署流程请改用 `--session-dir` 播种，不要手写时间戳。**
- 部署模组：`powershell -ExecutionPolicy Bypass -File tools/deploy_mod.ps1 [-Build] [-BumpEpoch N] [-DryRun]`
  （停守护 → 优雅停服 → 备份并替换 jar → 启服 → 等 RCON → 启守护 → 打印自检行；日志在 `<prod>/guardian/deploy.log`）

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
├── mixin/         # 第三方防御 mixin（EFN LinkAnimation NPE / AV NullEntity patch 缺失 / TickrateMixin）
├── util/          # TickAccelerator（虚拟时钟）/ AutoTicker（智能性能调度+看门狗）
├── EF/            # 技能书（StylishCombatSkill）/ 事件挂钩 / 行为表注册
└── arena/         # AutoArena（竞技场/课程/影子评估/自我博弈）
train/             # 训练流水线（train_ppo.py / iterate.py / instructor.py / 清洗脚本）
tools/             # 反编译产物与工具；guardian.ps1（守护进程：spark 监控/看门狗/自动重启）
docs/              # 技术文档（架构重写技术报告等）
```

---

## P0 修复记录（2026-09-10）

依据 `docs/代码审查报告_20260910.md` 的阻断级清单，本轮完成 8 项 P0（Java 编译通过、Python 自检通过）：

| # | 问题 | 修复 | 影响面 |
|---|---|---|---|
| P0-1 | 幽灵目标机制是死代码（脱锁目标永不遗忘/永不降权，陈旧目标每 tick 写回 `ATTACK_TARGET`） | `TargetTracker.update` 改用 `lastSeenTick == tick` 判定"本帧已扫到"；`onNotSeen` 置信度按脱锁时长衰减、位置每 tick 外推一步并限幅；`RlBrain` 增加"幽灵置信度 < 0.5 不写回攻击目标" | 感知层行为变化：脱锁敌人重新变得"会被遗忘" |
| P0-2 | 奖励与动作错位一步（训练信用分配系统性偏移） | 轨迹格式升 **v3**：后果奖励（命中/击杀/受击…）回填上一步，塑形奖励（熵/衰减/防守/距离/熔断/无效动作）记本步；`traj_io.py` 对 v1/v2 旧数据做一步平移补偿 | **训练数据语义变化**（新旧可混用） |
| P0-3 | `--pretrain` / `--relabel` 只注册不读取（近战预训练与 PER 重标注从未生效） | `train_ppo.py` 真正加载两个 npz 并作为 **BC 阶段带权样本**参与（片段无轨迹边界 → 不进 GAE/AWR）；meta 记录样本数 | 训练流程变化：BC 阶段多了预训练/重标注数据 |
| P0-4 | RCON 密码硬编码 5 处 + README 公开 + 明文命令行 | 新增 `tools/rcon_password.py` + `tools/rcon_cred.ps1`：CLI → `EFTLM_RCON_PASSWORD` → `tools/rcon_password.txt`，缺失即 fail-closed；`.gitignore` 排除密码文件；README 去掉明文 | 运维脚本用法不变（密码已写入本地文件） |
| P0-5 | 临时方块 3 tick 后无条件删除该格任意方块 | `ItemCombat` 只清理"仍是我们放下的那个方块"，且只有真正清理才补回库存 | 世界安全（不再误删他人方块） |
| P0-6 | 默认标靶含 `annoyingvillagers:alex`、黑名单默认空 → 开箱即崩 | 默认标靶移除 alex；默认黑名单 = `annoyingvillagers:alex`；过滤后为空则回退默认并 ERROR | 默认配置即可安全启动 |
| P0-7 | 启动无条件 `discard` 出生点 1024×128×1024 内全部女仆与生物（普通存档丢档） | 新增 `arena.properties own_world`（**默认 false**）；prod_server 已显式写入 `own_world=true` | **行为变更**：默认不再清场 |
| P0-8 | `spawnTarget` 在 `entityIds` 为空时除零 → 崩服 | 入口空列表早退 + 配置过滤回退；`findNearestTarget`/`isArenaTargetEntity`/9 处实体 id 解析改 `tryParse` + `instanceof` 防护 | 配置写错不再打断 tick |

自检与验证：

```bash
python train/selftest_p0.py            # 奖励对齐 + npz 载入 14 项断言（无需 torch）
gradlew.bat compileJava                # Java 编译
gradlew.bat jar reobfJar               # 打包（build/libs/eftlm_stylish-1.0.0.jar）
```

部署后应观测到的变化：训练轮次日志出现 `legacy_reward_shifted=N`（旧 v2 轨迹被补偿）与 `bc_extra: +N samples`；
服务器启动日志出现 `own_world=true: cleared ...` 或"跳过启动清场"告警；竞技场不再生成 alex。

---

## 致谢

- **merlin204** ——《The Mimic》模组作者，慷慨授权本模组借鉴其"经验驱动招式调度"的机制思路（命中经验、节奏学习、连招调度、增益窃取等均基于此思路重新设计实现）
- **AnnoyingVillagers（烦人的村民）** —— 道具战斗、方块武器、NullEntity 防御 mixin 等机制的**思路来源**（源码 GPL-3.0 开源）

第三方模组的 mixin 均以 `@Pseudo` + `targets` 字符串 + `required:false` 静默兼容（未安装不影响运行），并仅做防御性修复/机制参考，不包含其专有资源。

---

## 开源许可

本模组开源发布。借鉴思路的代码均为独立实现（详见 `docs/架构重写技术报告_20260826.md` 3.9 合规说明）；不包含任何第三方模组的专有资源（模型/贴图/音频/源码）。
