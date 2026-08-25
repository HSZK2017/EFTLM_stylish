# EFN（史诗战斗·夜幕 Nightfall 3.4.0）武器技能表

反编译自 `[史诗战斗·夜幕] EpicFight Nightfall-3.4.0.jar`（CFR 0.152）。
技能结构：`Skill`（EpicFight）+ `ComboNode` 技能树（com.p1nero.invincible 库），
每个武器一个 Innate 技能（普攻/技能派生树），部分带 Passive（被动）、ExclusiveDodge/Guard（专属闪避/格挡）。

## 按键与条件图例

| 记号 | 含义 |
|---|---|
| key1 | 攻击键（普攻） |
| key3 | 技能键（长按有特殊派生） |
| key4 | 收刀/架势键（村雨系） |
| key1_3 | 攻击+技能同按 |
| 天使/恶魔 | EFNAngelKeyCondition / EFNDemonKeyCondition（阎魔刀模式键） |
| 上/下 | EFNUpKeyCondition / EFNDownKeyCondition（方向键上/下） |
| 跳跃/潜行/冲刺 | EFNJumpKeyCondition / EFNSneakKeyCondition / EFNSprintKeyCondition |
| 双冲刺 | DoubleTapSprintCondition（冲刺键两连击） |
| 后前 | BackwardForwardCondition（后→前方向） |
| 弹反成功 | ParrySuccessCondition |
| 地面/空中 | EFNOnGroundCondition / EFNAirborneCondition |
| 耐力 | EFNStaminaCondition(n)（消耗/要求 n 耐力） |
| 堆叠 | EFNStackCondition(min, max)（技能层数门控，timeConsumeStack 消耗） |
| 阶段 | PlayerPhaseCondition（技能自身状态机） |
| 蓄力 | RuinGreatswordIsChargingCondition / MeenLanceIsChargingCondition |
| 收刀 | MurasamaSheathCondition / EFNMurasamaSheathCondition |
| 召唤物 | TimeEvents：summonBlastSword(Lite/Mid)=幻影剑，summonHeavyRainLite=剑雨，summonDamoclesSword=达摩克利斯剑 |
| 倍率 | setDamageMultiplier / setImpactMultiplier |

物品→能力绑定：`data/efn/capabilities/weapons/<item>.json` 的 `"type"` 字段指向 `efn:<preset>`。

---

## 1. 阎魔刀 Yamato（4 个物品）

- 物品：`efn:yamato_dmc`、`efn:yamato_dmc4`、`efn:yamato_dmc_in_sheath`、`efn:yamato_dmc4_in_sheath`
- 能力：`efn:yamato`（AdvanceWeaponCapability，类别 `EFN_YAMATO`，持握 TWO_HAND）
- 动画路径：`efn:biped/yamato/*`（YAMATO_JUDEMENCUT_ALL 等）

### 专属技能 `efn:yamato`（YamatoInnate，ComboBasicAttack）

**普攻（key1）**：Auto1→Auto2→Auto3→(Extend_Auto3)→Extend_Auto4→Extend_Auto5
- Auto1/2/3：`dmcyamato_slasher_1/2/3`（地面，0.5×/0.5×/1.2×，Auto3 带幻影剑+回耐力）
- Extend_Auto3：`slasher_crosscut_1`（连按间隔 415–1000ms 触发，剑雨）
- Extend_Auto4/5：`slasher_crosscut_2/3`（1.0×/1.5×，Auto5 带幻影剑）

**空中连段**：Aerialrave_Auto1/2/3（`aerialrave_1/2/3`，空中限定）

**技能键（key3）派生**：
| 节点 | 动画 | 条件 | 效果 |
|---|---|---|---|
| Judgement_Cut 次元斩 | `judgementcut_all` | 攻击键长按 | 主派生节点，可衔接任意招后 |
| Flare 五月雨(火焰斩) | `flare_cut` | 天使+地面 | 0.8×，剑雨+横扫硬直，接 Flare_Rise |
| Flare_Rise / _Rapid | `flare_just` | 技能键长按 / 跳跃键 | 0.9×，剑雨 |
| UpperSlash 上斩 | `upperslash` | 地面 | 击飞效果+幻影剑 |
| UpperSlash_Rise | `upperslash_hold` | 地面+上方向 | 蓄力上斩 |
| Volcano_cut 火山斩 | `volcano_cut` | 恶魔+耐力3 | 1.5×，剑雨，可接火山蓄力 |
| Volcano_hold 火山蓄力 | `volcano_hold` | 技能键长按 | 2.5×，幻影剑，0.45s 后接次元斩 |
| Drive 冲击 | `drive` | 空中+恶魔 | 1.0×，剑雨 |
| Flush 空中扫击 | `aerialflush` | 空中+天使 | 0.3× |
| Helmbreaker 头盔破坏者 | `helmbreaker` | 空中+恶魔+天使 | 0.8×，剑雨+解除缓慢/垂直停止 |
| Stomp 踩踏 | `stomp` | 恶魔 | 1.0×，消耗耐力 |
| KillerBee 杀手蜂 | `killerbee` | 空中+恶魔 | 1.2×，幻影剑 |
| Orbit_1/2 幻影剑环绕 | `orbit_1/orbit_2` | 空中+天使+耐力2 | 0.7×，剑雨 |
| RepaidSlash 连击斩 | `rapidslash` | 天使 | 疾风迅雷派生（时间内接次元斩/五月雨/火山/上斩） |
| Divorce_Auto1/2/3 次元斩·魔 | `divorce_1/2/3` | 恶魔+地面 | 2.0×/2.5×/3.0×，幻影剑/剑雨/达摩克利斯剑 |

### 被动 `efn:yamato_passive`（YamatoPassive，WEAPON_PASSIVE）
### 专属闪避 `efn:yamato_step`（YamatoDodge，6 向瞬步）

---

## 2. 真村雨 HF_MURASAMA（1 个物品）

- 物品：`efn:hf_murasama`
- 能力：`efn:murasama`（AdvanceWeaponCapability，类别 TACHI，持握 TWO_HAND / COMMON(双冲刺) / SHEATH(收刀)）
- 动画路径：`efn:biped/murasama/*`（HF_MURASAMA_X/XX/XXX/XXXX、Y、Y_CHARGE、SHEATH_IN 等）

### 专属技能 `efn:murasama`（MurasamaInnate，ComboBasicAttack）

**X 普攻链（key1）**：X→XX→XXX→XXXX（0.7×/0.85×/0.8×/1.35×，均回鞘）
- Air_X / Air_XX（空中，耐力2）；Air_XX_DOUBLEJUMP（二段跳强化）
- DASH_X / DASH_X_SP（冲刺中）

**Y 技能链（key3）**：Y→长按 Y_CHARGE（2.5× 蓄力拔刀）；XY / XXY / XXXY 派生+各自蓄力变体
- Y_SHEATH（收刀拔刀斩 1.3×）、Y_SHEATH_DASH（突进拔刀 2.5×）、Y_SHEATH_DASH_THROUGH（贯穿拔刀 2.5×）
- Y_DASH / Y_DASH_SP（冲刺斩 1.1×/1.4×）、Y_KICK（后前+技能键，踢击 0.75×）、Y_AIR / Y_CHARGE_AIR（空中）

**架势（key4 收刀）**：SHEATH_IN（收刀）/ SHEATH_IN_RUN（跑动收刀）/ SWORD_OUT（拔刀）/ TAUNT（潜行键挑衅）
- 收刀状态 = SHEATH 持握风格，所有技能形态切换

**防御派生**：COUNTER（弹反成功+上方向，1.5× 弹反反击）、DodgeCounter（闪避反击）
- 全程免硬直效果（STUN_IMMUNITY + SIN_STUN_IMMUNITY），斩绝（Zansetsu）状态限制部分节点

### 被动 `efn:murasama_passive`（MurasamaPassive：双冲刺 / SHEATH 持握切换）
### 专属闪避 `efn:murasama_dodge`（MurasamaDodge，翻滚）/ 专属格挡 `efn:murasama_parry`（MurasamaParry）

---

## 3. 苍刃 HF_BLADE（1 个物品）

- 物品：`efn:hf_blade`
- 能力：`efn:hf_blade`（AdvanceWeaponCapability，类别 TACHI，持握结构同村雨）
- 动画路径：`efn:biped/hfblade/*`

### 专属技能 `efn:hf_blade`（HfBladeInnate，ComboBasicAttack）

与村雨完全同构（X/XX/XXX/XXXX、Y/蓄力、XY/XXY/XXXY、收刀、COUNTER、Y_DASH、Y_KICK、TAUNT），
动画换为 `HF_BLADE_*`（如 `hf_blade_x`、`hf_blade_y_charge`），数值倍率一致（蓄力 2.5×/3.0× 冲击）。

### 被动 `efn:hf_blade_passive`（HfBladePassive）
### 专属闪避 `efn:murasama_dodge` / 专属格挡 `efn:murasama_parry`（与村雨共用）

---

## 4. 废墟大剑 Ruins Greatsword（1 个物品）

- 物品：`efn:ruinsgreatsword`
- 能力：`efn:ruinsgreatsword`（GREATSWORD，TWO_HAND）
- 动画路径：`efn:biped/greatsword/*`（NG_GREATSWORD_AUTO1/2/3、SKILL_CLASH、CHARG1MAX_SECOND）

### 专属技能 `efn:ruinsgreatsword`（RuinsGreatSwordInnate，ComboBasicAttack）

**普攻（key1）**：Auto1→Auto2→Auto3（0.95s/0.98s/1.25s 判定），Dash（冲刺），Air_Normal（空中劈，1.7×，耐力5）

**技能（key3）**：NG_GREATSWORD_SKILL_CLASH（斩击·弹刀，1.2×，消耗1层堆叠）
- GP_Strike：满蓄力重击（CHARG1MAX_SECOND，消耗1层）
- 蓄力状态派生根：RuinExtendRoot_1_GP / 1_Clash / 2_ChargeMin / 3_ChargeMax（蓄力各阶段接不同攻击）

### 被动 `efn:ruinsgreatsword_passive`（RuinGreatSwordPassive：蓄力机制）

---

## 5. 荆棘轮 Thornwheel（1 个物品）

- 物品：`efn:thornwheel`
- 能力：`efn:thornwheel`（GREATSWORD，TWO_HAND）
- 动画：THORNWHEEL_AUTO1/2/3 + GREATSWORD_DASH/AIR_SLASH

### 专属技能 `efn:thornwheel`（ThornWheelSkill，普通 WeaponInnate，非 Combo 树）
无被动。

---

## 6. 冥枪 Meen Lance（2 个物品）

- 物品：`efn:meen_spear`、`efn:meen_spear_e`
- 能力：`efn:meenlance`（SPEAR，TWO_HAND）
- 动画：NF_MEEN_AUTO1/2/3/4、DASH、AIRSLASH、FINISHER

### 专属技能 `efn:meenlance`（MeenLanceInnate，ComboBasicAttack）

**普攻（key1）**：Auto1→Auto2→Auto3→Auto4（带"失血伤害"吸血，蓄力中禁用）

**技能（key3）Finisher 终结技**：NF_MEEN_FINISHER
- 条件：持有 MEEN_LANCE 效果 + 堆叠≥10，消耗10层并移除效果
- 效果：0.5× 失血伤害 + 终结
- 之后接 Charge_Strike_1/2/3（蓄力打击，HOLD/LONG 硬直，消耗充能）
- 蓄力状态派生：MeenExtendRoot_1~4

### 被动 `efn:meenlance_passive`（MeenLancePassive：蓄力/充能机制）

---

## 7. 以太暮光双刀 Aetherial Dusk Dual Sword（1 个物品）

- 物品：`efn:nf_dual_sword`
- 能力：`efn:aetherialdusk`（SWORD，TWO_HAND）
- 动画：NF_DUAL_AUTO1/2/3/4、DASH、AIRSLASH、SKILL、SKILL_EXTEND、STORMATK

### 专属技能 `efn:aetherialdusk`（AetherialDuskDualSwordInnate，ComboBasicAttack）

**普攻（key1）**：Auto1→Auto2→Auto3→Auto4（击退+失血伤害，Auto3/4 不可打断）

**技能（key3）**：NF_DUAL_SKILL（乱舞，消耗1层堆叠，施加中毒+缓速）
- 技能键长按接 Skill_Extend（NF_DUAL_SKILL_EXTEND，延长连击，失血伤害）
- DodgeCounter：NF_DUAL_STORMATK（闪避反击·风暴攻击，消耗耐力）

### 被动 `efn:aetherialdusk_passive`（AetherialduskPassive）

---

## 8. 圣剑 Exsiliumgladius（4 个物品）

- 物品：`efn:exsiliumgladius`、`efn:exsiliumgladius_e`、`efn:fire_exsiliumgladius`、`efn:fire_exsiliumgladius_e`
- 能力：`efn:exsiliumgladius`（SWORD；单持=ONE_HAND，主手圣剑+副手火圣剑=特殊双持 TWO_HAND）
- 动画：EXSILIUMGLADIUS_A/AA/AAA/AAAA、AB/ABA/ABAA/ABAB、ABB/ABBA/ABBB、AAB/AABA/AABB、AAAB、D/DD/DDD

### 专属技能 `efn:exsiliumgladius`（ExsiliumgladiusInnate，ComboBasicAttack）— 仅双持形态
单持形态使用 EpicFight 原版 `SWEEPING_EDGE`。

**A/B 派生系统**（A=攻击键，B=技能键）：
- A→AA→AAA→AAAA（0.7×/0.7×/0.8×/1.0×）
- A→AB→ABA→ABAA/ABAB（0.8×/0.9×/1.2×/1.5×）；AB→ABB→ABBA/ABBB（1.1×/1.3×/2.0×）
- AA→AAB→AABA/AABB2（1.0×/1.3×/2.0×）；AAA→AAAB（1.5×）
- D（恶魔斩）→DD→DDD（0.8× 三段）

无被动。

---

## 9. 先锋剑 Sword of Pioneer（1 个物品）

- 物品：`efn:sword_of_pioneer`
- 能力：`efn:pioneer`（UCHIGATANA，ONE_HAND）
- 动画：NF_SWORD_AUTO1/2/3/4、DASH、AIRSLASH、SKILL_FIRST、SKILL_SECOND

### 专属技能 `efn:pioneer`（PioneerInnate，ComboBasicAttack）

**普攻（key1）**：Auto1→Auto2→Auto3→Auto4

**技能（key3）**：Skill_1（NF_SWORD_SKILL_FIRST，消耗1层堆叠）→ Skill_2（NF_SWORD_SKILL_SECOND）→ Auto2_Enhanced（强化追击，1.2×，HOLD 硬直+失血伤害）
- 层数来源：被动/命中累积（startComboCounter）

无被动。

---

## 10. 阔刃 Broad Blade（1 个物品）

- 物品：`efn:broadblade`
- 能力：`efn:broadblade`（LONGSWORD，BOARD_BLADE 风格）
- 动画：BROADBLADE_AUTO1~8、DASHSLASH、AIRSLASH、COUNTER、EXECUTE（enhance）

### 专属技能 `efn:broadblade`（BroadBladeInnate，ComboBasicAttack）

**普攻（key1）**：8 段连击 auto1→auto2→…→auto8，dashSlash（冲刺斩）、airSlash（空中斩）

**技能（key3）**：counter（弹反成功触发，BROADBLADE_COUNTER）
**处决（key4）**：execute（EFNTargetHealthCondition 目标低血量，BROADBLADE_EXECUTE，enhance 动画，优先级100）

无被动。

---

## 11. 短刀 Short Sword（4 个物品）

- 物品：`efn:nf_shortsword`、`efn:nf_shortsword_e`、`efn:nf_shortsword_2`、`efn:nf_shortsword_2_e`
- 能力：`efn:shortsword`（SWORD，ONE_HAND）
- 动画：NF_SHORTSWORD_AUTO1~6、DASH、AIRSLASH

### 专属技能 `efn:shortsword`（ShortSwordInnate，ComboBasicAttack）

**普攻（key1）**：6 连击 Auto1→Auto2→…→Auto6（Auto6 终结判定音），Dash、Air、DodgeCounter（闪避反击=AUTO3）

### 被动 `efn:shortsword_passive`（ShortswordPassive）

---

## 12. 血欲太刀 Bloodlust（2 个物品）

- 物品：`efn:air_tachi`、`efn:air_tachi_e`、`efn:co_tachi`
- 能力：`efn:bloodlust`（TACHI，TWO_HAND）
- 动画：NF_TACHI_AUTO1~5、DASH、AIRSLASH、BLOODLUST、BLOODLUST_END

### 专属技能 `efn:bloodlust`（BloodlustInnate，ComboBasicAttack）

**普攻（key1）**：Auto1→Auto2→Auto3→Auto4→Auto5（全程吸血 1.2% 失血伤害）

**技能（key3）**：
- Skill_Start：NF_TACHI_BLOODLUST（阶段0→2，施加 BLODDLUST 效果，15% 失血伤害）
- Skill_End：NF_TACHI_BLOODLUST_END（阶段2→1，移除效果，15% 失血伤害）

### 被动 `efn:bloodlust_passive`（BloodlustPassive）

> 注：`efn:arc_tachi` 使用示例能力 `efn:example`（ExampleCombo，测试用，SWEEPING_EDGE 技能）。

---

## 13. 楔丸 Kusabimaru（1 个物品）

- 物品：`efn:kusabimaru`
- 能力：`efn:kusabimaru`（UCHIGATANA，TWO_HAND）；若装有 `efn_enhance` 则自动替换为 `efn:kusabimaru` 增强版（KUSABIMARU_ENHANCE，动画 EFN_ESekiroAnimations，倍率1.2×）
- 动画：KUSABIMARU_AUTO1~5、DRAGON_FLASH、ICHIMONJI_1/2、SHADOW_RUSH、SAKURA_DANCE

### 专属技能 `efn:kusabimaru`（KusabimaruInnate，ComboBasicAttack）

**普攻（key1）**：Auto1→Auto2→Auto3→Auto4→Auto5

**技能（key3）**：
| 节点 | 动画 | 条件 | 效果 |
|---|---|---|---|
| DRAGON_FLASH 龙闪 | `dragon_flash` | 地面+堆叠≥2 | 消耗2层 |
| ICHIMONJI_1 一文字斩 | `ichimonji_1` | 潜行键+地面+堆叠≥1 | 1.3×，LONG 硬直，消耗1层+耐力5 → 接 ICHIMONJI_2 |
| ICHIMONJI_2 | `ichimonji_2` | 地面 | 1.3×，LONG 硬直，耐力5 |
| SHADOW_RUSH 影袭 | `shadow_rush` | 冲刺键+地面+耐力4.5 | 突进 |
| SAKURA_DANCE 樱花舞 | `sakura_dance` | 空中+堆叠≥2 | 消耗2层 |

无被动绑定。

---

## 14. 兽爪 Beast Claw（1 个物品）

- 物品：`efn:nf_claw`
- 能力：`efn:beastclaw`（FIST，TWO_HAND）
- 动画：NF_CLAW_AUTO1/2/3、DASH、AIRSLASH、BEASTROAR

### 专属技能 `efn:beastclaw`（BeastclawInnate，ComboBasicAttack）

**普攻（key1）**：Auto1→Auto2→Auto3、Dash、Air

**技能（key3）**：NF_CLAW_BEASTROAR（兽吼，堆叠≥1，消耗1层；施加 CLAW 强化 30s + 眩晕免疫 10s + 10% 失血伤害）

无被动。

---

## 15. 血月镰刀 Scythe（2 个物品）

- 物品：`efn:crimson_moon`、`efn:crimson_moon_e`
- 能力：`efn:scythe`（LONGSWORD，TWO_HAND / SHEATH(收刀状态)）
- 动画：SCYTHE_AUTO1~5、DASH、AIR_SLASH、BLOCK

### 专属技能 `efn:scythe_skill`（ScytheSkill，普通 WeaponInnate）
收刀状态（SHEATH 风格）有独立三连普攻（SCYTHE_AUTO1×3）。

无被动。

---

## 16. 新月 Crescent Moon / 旗手 Flag Bearer（4 个物品）

- 物品：`efn:crescent_moon`、`efn:crescent_moon_e`、`efn:flag_bearer`、`efn:flag_bearer_e`
- 能力：`efn:crescentmoon`（LONGSWORD，FALCHION 风格）
- 动画：FALCHION_AUTO1/2/3、DASHATTACK、AIRSLASH、SKILL、STRIKE、EX2

### 专属技能 `efn:crescentmoon`（CrescentMoonInnate，ComboBasicAttack）

**普攻（key1）**：Auto1→Auto2→Auto3、Dash、Air

**技能（key3）**：FALCHION_SKILL（堆叠≥1，冷却 600tick）
**突刺（key4）**：FALCHION_STRIKE（耐力4）→ 接 Strike_Extend（FALCHION_EX2，需 COMBO_EXECUTE_WINDOW 效果，追击）

无被动。

---

## 17. 誓约胜利之剑 Excalibur（1 个物品）

- 物品：`efn:excalibur`（geo 模型武器，ExcaliburItem）
- 无 `capabilities/weapons/excalibur.json`，能力由 geo 系统/其他兼容注册，未在此表列全

---

## 全局技能（EFNSkills，技能书/通用，不绑定武器）

| 技能 id | 类型 | 说明 |
|---|---|---|
| `efn:efn_dodge` | 闪避 | 通用翻滚（EFNDodgeSkill_Roll） |
| `efn:efn_step` | 闪避 | 通用侧步（EFNDodgeSkill_Step） |
| `efn:yamato_step` | 闪避 | 阎魔刀 6 向瞬步（仅阎魔刀装备时替换） |
| `efn:murasama_dodge` | 闪避 | 村雨翻滚（村雨/苍刃专属） |
| `efn:efn_parry` | 格挡 | 通用弹反（EFNParryingSkill） |
| `efn:murasama_parry` | 格挡 | 村雨弹反（村雨/苍刃专属） |
| `efn:indestructible` | 被动 | 不屈（IndestructiblePassive） |
| `efn:parry_master` | 被动 | 弹反大师（ParryMasterPassive，冷却资源） |
| `efn:precise_parry` | 被动 | 精准弹反（PreciseParryPassive） |
| `efn:judgementcutend` | 主动 | 次元斩绝·真（JudgmentCutEndSkill，ONE_SHOT） |
| `efn:stomp` | 主动 | 踩踏（StompSkill） |
| `efn:zansetsu` | 主动 | 斩绝（ZansetsuSkill，村雨/苍刃架势） |
| `efn:execution` | 主动 | 处决（ExecuteSkill） |
| `efn:mortal_blade` | 主动 | 不死斩（MortalBladeSkill，只狼） |

## 补充说明

- 武器全部技能 = Innate（专属技能树）+ Passive（被动）+ ExclusiveDodge/Guard（专属闪避/格挡）；
  动画资源均在 `assets/efn/animmodels` 与 `efn:biped/**` 下注册，可由
  `AnimationManager.byKey(ResourceLocation("efn", "biped/yamato/dmcyamato_judgementcut_all"))` 之类路径引用。
- 行为表（CombatBehaviors）与 ComboNode 技能树是两套独立机制：ComboNode 是玩家输入驱动；
  若要让女仆（TLM/EFTLM）使用这些技能，仍需像 EFTLM 的 `Yamato$EFNCompatHolder` 那样
  按本表把各动画手动组织成 CombatBehaviors 行为序列。
