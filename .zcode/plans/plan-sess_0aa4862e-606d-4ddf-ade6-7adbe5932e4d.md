## 计划：新建 companion mod 实现武器补发与耐久重置

### 背景结论（已探索确认）
- 女仆武器仅由 `AutoArena.spawnMaid` 在生成/重生时发放（主手 + 背包），**存活期间损坏无补发**
- Forge 1.20.1 无"非玩家武器损坏"事件 → 只能**轮询检测**（每 20 tick）
- 击杀钩子：`LivingDeathEvent` + `annoyingvillagers:` 前缀判定 boss（现成模式），需自行判断击杀者是否为女仆
- arena 女仆可通过 NBT 标记 `eftlm_stylish:spawn_tick` 识别（AutoArena 写入），**无需引用其类**，实现解耦
- EFN 武器池 33 个（全部 extends `WeaponItem`），运行时用 `ForgeRegistries.ITEMS` 枚举（namespace=efn && instanceof WeaponItem）即可，**无需硬编码**

### 方案（用户已确认）
- **独立 companion mod**（新项目 `maid_weapon_refill`，复用 maid_pilot 的 Forge Gradle 环境）
- **断裂后补发**：检测武器总数减少（槽位 EMPTY）→ 补发随机 EFN 武器
- **重置范围**：手持 + 背包全部武器 + 4 件护甲

### 实现步骤

**1. 创建项目** `E:\program\JAVA\touhou little maid - unknow sky area\maid_weapon_refill\`
- 复制 maid_pilot 的 `gradle/wrapper`、`settings.gradle`、`build.gradle`（改 mod_id/名称）、`gradle.properties`
- 依赖（编译期，全部本地/缓存可用）：forge 1.20.1-47.4.13（maven 缓存）、TLM（modrinth 缓存）、**epicfight 本地 jar 文件依赖**（mods 目录）、eftlm_stylish 仅运行时（用 NBT 键判断，不引用类）
- `mods.toml`：modid=`maid_weapon_refill`，依赖 forge+TLM+epicfight（运行时）

**2. 核心代码**（Java 17，MinecraftForge.EVENT_BUS）
- `WeaponRefillHandler`：
  - **武器计数记录**：`Map<UUID, Integer>` 记录每个 arena 女仆的 EF 武器（`instanceof WeaponItem`）总数；新实体（UUID 未记录）时扫描并记录
  - **轮询补发**（`ServerTickEvent`，每 20 tick）：arena 女仆当前 EF 武器数 < 记录数 → 差额把随机 EFN 武器（枚举+缓存列表+Random）补发到空槽（主手优先，其次背包首空槽），并更新记录
  - **击杀重置**（`LivingDeathEvent`）：被杀实体 type 注册 ID 以 `annoyingvillagers:` 开头 且 击杀者（`source.getEntity()`）为 arena 女仆 → 遍历手持 2 槽 + 背包 36 槽（仅 WeaponItem）+ 护甲 4 槽，`isDamageableItem()` 时 `setDamageValue(0)` 并写回
- arena 女仆判定：PersistentData 含 `eftlm_stylish:spawn_tick`

**3. 构建**：`gradlew build`（复用本地 gradle 缓存；epicfight 用本地 jar 依赖避免网络）

**4. 部署**：产物 jar → `prod_server/mods/`，备份原文件

**5. 重启服务器并验证**
- RCON 优雅停服（世界保存）→ 启动 → 确认 mod 加载日志
- **验证补发**：RCON `data modify entity` 把女仆主手武器 Damage 设为 999 → 观察断裂后自动补发随机 EFN 武器（`data get entity` 确认）
- **验证重置**：确认 arena boss 被女仆击杀后，手持/背包武器 + 护甲 Damage 归 0（`data get entity`）
- F 盘飞行模拟服务器全程不动

### 风险与说明
- 首次 gradle 构建可能需少量网络下载（forge 依赖大概率已有缓存，maid_pilot 构建过）
- 若模组更新（eftlm_stylish/EFN），武器池自动适配（运行时枚举）；计数逻辑不依赖内部 API
- 补发最多 1 秒延迟（20 tick 轮询）；女仆死亡重生后按新实体重新计数（原逻辑本身会重发武器）
- 轮换系统（武器在手持/背包间移动）不影响计数方案（总数不变）