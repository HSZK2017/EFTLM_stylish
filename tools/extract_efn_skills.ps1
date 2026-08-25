<#
.EXAMPLE
  .\tools\extract_efn_skills.ps1
  .\tools\extract_efn_skills.ps1 -Jar "E:\.minecraft\versions\EPIC mod test\mods" -EnhanceJar "E:\program\JAVA\EpicFight_TouhouLittleMaid\libs\Nightfall-Enhance.jar" -Out "src\main\resources\eftlm_stylish\skills.json"

.DESCRIPTION
  Extracts the EFN weapon skill catalog WITHOUT decompiling bytecode:
  animations are plain JSON assets inside the jar, and the asset path IS the
  EpicFight animation key. Entries are classified by directory + naming rules
  into autos / skills / motion and emitted as skills.json:
    - item mapping comes from data/efn/capabilities/weapons/*.json "type" values
    - resource gate: "eftlm" for the 9 weapons EFTLM provides a WeaponInnateSkill
      (BehaviorsBuild stack), "own" for the rest (this mod's own stack)
    - classification is heuristic: review the output, re-run after EFN updates
#>
param(
    [string]$Jar = "E:\.minecraft\versions\EPIC mod test\mods",
    [string]$EnhanceJar = "E:\program\JAVA\EpicFight_TouhouLittleMaid\libs\Nightfall-Enhance.jar",
    [string]$WomJar = "E:\.minecraft\versions\EPIC mod test\mods",
    [string]$Out = "src\main\resources\eftlm_stylish\skills.json"
)

Add-Type -AssemblyName System.IO.Compression.FileSystem

function Find-Jar([string]$path, [string]$pattern) {
    if (Test-Path $path -PathType Leaf) { return (Get-Item $path).FullName }
    $hit = Get-ChildItem $path -Filter $pattern -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($hit) { return $hit.FullName }
    return $null
}

$jarPath = Find-Jar $Jar "*Nightfall*"
if (-not $jarPath) { throw "EFN jar not found: $Jar" }
$womJarPath = Find-Jar $WomJar "*WeaponsOfMiracles*"

# ---------------------------------------------------------------
# item registry name -> animation directory
# ---------------------------------------------------------------
$itemsOf = @{
    "yamato"                 = @("yamato_dmc", "yamato_dmc4", "yamato_dmc_in_sheath", "yamato_dmc4_in_sheath")
    "hf_blade"               = @("hf_blade")
    "hf_murasama"            = @("hf_murasama")
    "ng_greatsword"          = @("ruinsgreatsword")
    "nf_meen"                = @("meen_spear", "meen_spear_e")
    "nf_dual"                = @("nf_dual_sword")
    "broadblade"             = @("broadblade")
    "nf_claw"                = @("nf_claw")
    "nf_claw_n"              = @("nf_claw")
    "nf_tachi"               = @("air_tachi", "air_tachi_e", "co_tachi")
    "nf_sword"               = @("sword_of_pioneer")
    "nf_shortsword"          = @("nf_shortsword", "nf_shortsword_2")
    "scythe"                 = @("crimson_moon", "crimson_moon_e", "scythe")
    "falchion"               = @("crescent_moon", "crescent_moon_e", "flag_bearer", "flag_bearer_e")
    "sekiro"                 = @("kusabimaru")
    "exsiliumgladius_reborn" = @("exsiliumgladius", "exsiliumgladius_e", "fire_exsiliumgladius", "fire_exsiliumgladius_e")
    "thornwheel"             = @("thornwheel")
    "arc_test"               = @("arc_tachi")
}

# weapons whose stack is managed by EFTLM WeaponInnateSkill (BehaviorsBuild)
$eftlmStackDirs = @("yamato", "hf_blade", "hf_murasama", "sekiro", "nf_meen", "scythe", "broadblade", "nf_claw", "nf_claw_n", "nf_tachi")

# pure motion / generic dirs: excluded from the weapon catalog
$motionDirs = @("dodge", "dodge_mob", "perfect_dodge", "yamato_judgementcut_end", "dmc5_v_jc", "nf_skill", "arc_test")

function Classify-Name([string]$name) {
    $n = $name.ToLower()
    if ($n -match "(^|_)(idle|run|walk|jump|fall|sneak|kneel|taunt|sword_out|sheath_in|hitback|hitdown|hitup|step)(_|\d|$)" -or $n -match "^biped_hit") { return "motion" }
    if ($n -match "dodge|perfect_dodge") { return "motion" }
    if ($n -match "(^|_)(auto|slasher|divorce|aerialrave|airslash|air_slash|airatk|stormatk|strike|dash)(_|\d|$)") { return "auto" }
    if ($n -match "(^|_)(skill|charge|zandatsu|judgementcut|drive|upperslash|aerialflush|volcano|rapidslash|flare|helmbreaker|orbit|killerbee|stomp|execute|flashblock|bloodlust|fushigiri|ichimonji|dragon_flash|sakura_dance|shadow_rush|clash|push|counter|ex[0-9])(_|\d|$)") { return "skill" }
    if ($n -match "_y$|_y_|_xy|_xxy|_xxxy") { return "skill" }
    if ($n -match "^exsiliumgladius_[a-d]+$") { return "auto" }
    if ($n -match "exsiliumgladius_c_[bflr]") { return "skill" }
    return "unknown"
}

function Classify-Path([string]$rel) {
    # rel like biped/yamato/dmcyamato_drive or biped/hf_blade/skill/hf_blade_y
    if ($rel -match "^[^/]+/[^/]+/data/") { return $null }        # animation data files
    if ($rel -match "/visual_effect/") { return $null }           # particle/model helpers, not skills
    if ($rel -match "/living/") { return "motion" }
    if ($rel -match "/skill/") { return "skill" }
    if ($rel -match "/combat/") { return "auto" }
    $leaf = ($rel -split "/")[-1]
    return Classify-Name $leaf
}

# ---------------------------------------------------------------
# read animation inventory from jars
# ---------------------------------------------------------------
$anims = @{}   # dir -> @(fullKey)
function Read-Jar([string]$file, [string]$nsPrefix) {
    $zip = [System.IO.Compression.ZipFile]::OpenRead($file)
    foreach ($entry in $zip.Entries) {
        if ($entry.FullName -match "^assets/$([regex]::Escape($nsPrefix))/animmodels/animations/biped/(.+\.json)$") {
            $rel = $Matches[1] -replace "\.json$", ""
            $rel = $rel -replace "/data/", "/"
            $dir = ($rel -split "/")[0]
            if (-not $anims.ContainsKey($dir)) { $anims[$dir] = @() }
            $full = "$($nsPrefix):biped/$rel"
            if ($anims[$dir] -notcontains $full) { $anims[$dir] += $full }
        }
    }
    $zip.Dispose()
}
Read-Jar $jarPath "efn"
if (Test-Path $EnhanceJar -PathType Leaf) { Read-Jar $EnhanceJar "efn_enhance" }

# ---------------------------------------------------------------
# WOM（奇迹武器，闭源）：动画资产同 EFN，位于 biped/{combat,skill,living}/，
# 武器前缀扁平命名。防御/位移/通用类技能不进目录（guard/counter/step/roll 等）。
# ---------------------------------------------------------------
$womItems = @{
    "enderblaster" = @("ender_blaster")
    "nova"         = @("nova")
    "moonless"     = @("moonless")
    "ruine"        = @("ruine")
    "staff"        = @("wooden_staff", "stone_staff", "iron_staff", "golden_staff", "diamond_staff", "netherite_staff")
    "herrscher"    = @("herrscher")
    "solar"        = @("solar")
    "agony"        = @("agony")
    "torment"      = @("tormented_mind")
    "satsujin"     = @("satsujin")
    "antitheus"    = @("antitheus")
    "napoleon"     = @("napoleon", "blackstar")
    "orbit"        = @("orbit")
    "katana"       = @("evil_tachi")
}

$womSkillExclude = "guard|counter|meditation|roll|shadow_step|enderstep|dodgemaster|sprint|aiming|reload|evade|^biped|^time$|^strong$|^ravanger|^gezets|^kick|sakura_timed"

# 原 WomCompat 行为表技能的冷却（统一到目录门控后沿用原 CD）
$womCdOverrides = @{
    "solar_brasero_infierno" = 1200
    "nova_flash_mutilation"  = 240
    "orbit_light_beam"       = 240
}

function Read-Wom([string]$file) {
    $zip = [System.IO.Compression.ZipFile]::OpenRead($file)
    $wom = @{}   # prefix -> @{ autos=@(); skills=@() }
    foreach ($entry in $zip.Entries) {
        if ($entry.FullName -match "^assets/wom/animmodels/animations/biped/(combat|skill|living)/(.+\.json)$") {
            $sub = $Matches[1]
            $name = $Matches[2] -replace "\.json$", ""
            if ($sub -eq "living") { continue }
            if ($name -match "/data") { continue }
            if ($name -notmatch "^(enderblaster|nova|moonless|ruine|staff|herrscher|solar|agony|torment|satsujin|antitheus|napoleon|orbit|katana)") { continue }
            $prefix = $Matches[1]
            if (-not $wom.ContainsKey($prefix)) { $wom[$prefix] = @{ autos = @(); skills = @() } }
            $full = "wom:biped/$sub/$name"
            $n = $name.ToLower()
            if ($n -match "auto|dash|airslash|attack|jumpkick|tishnaw|clawstrike|strike") {
                if ($wom[$prefix].autos -notcontains $full) { $wom[$prefix].autos += $full }
            } elseif ($n -match $womSkillExclude) {
                # 防御 / 位移 / 通用技能不进目录
            } else {
                $cond = if ($n -match "air|sky_dive|rising|airslam") { "airborne" }
                        elseif ($n -match "shoot|fatal_draw") { "mid_range" }
                        else { "melee" }
                $cd = 120
                if ($n -match "buster|sky_dive|airslam") { $cd = 240 }
                $leaf = ($name -split "/")[-1]
                if ($womCdOverrides.ContainsKey($leaf)) { $cd = $womCdOverrides[$leaf] }
                if ($wom[$prefix].skills -notcontains $full) {
                    $wom[$prefix].skills += @{ id = [string]$leaf; anim = [string]$full; cd = [int]$cd; cond = [string]$cond; resource = "own"; cost = 1 }
                }
            }
        }
    }
    $zip.Dispose()
    return $wom
}
if ($womJarPath) { $womData = Read-Wom $womJarPath } else { $womData = @{}; Write-Output "WOM jar not found (skipped)" }

# ---------------------------------------------------------------
# 原版 EpicFight 武器天赋技能（EF 开源，源码 gameasset/EpicFightSkills.java）：
# 玩家侧 WEAPON_INNATE 技能槽，女仆侧经本目录以动画键释放（播放即结算）。
# 按武器类别挂载（skills.json 的 categories 段），类别武器无物品条目时回退使用。
# ---------------------------------------------------------------
$vanillaCategories = @{
    "SWORD" = @{ skills = @(
        @{ id = "sweeping_edge"; anim = "epicfight:biped/skill/sweeping_edge"; cd = 120; cond = "melee"; resource = "own"; cost = 1 },
        @{ id = "dancing_edge"; anim = "epicfight:biped/skill/dancing_edge"; cd = 120; cond = "melee"; resource = "own"; cost = 1 }
    ) }
    "AXE" = @{ skills = @(
        @{ id = "the_guillotine"; anim = "epicfight:biped/skill/the_guillotine"; cd = 120; cond = "melee"; resource = "own"; cost = 1 }
    ) }
    "SPEAR" = @{ skills = @(
        @{ id = "heartpiercer"; anim = "epicfight:biped/skill/heartpiercer"; cd = 120; cond = "mid_range"; resource = "own"; cost = 1 },
        @{ id = "grasping_spire_first"; anim = "epicfight:biped/skill/grasping_spire_first"; cd = 120; cond = "melee"; resource = "own"; cost = 1 },
        @{ id = "grasping_spire_second"; anim = "epicfight:biped/skill/grasping_spire_second"; cd = 120; cond = "melee"; resource = "own"; cost = 1 }
    ) }
    "UCHIGATANA" = @{ skills = @(
        @{ id = "battojutsu"; anim = "epicfight:biped/skill/battojutsu"; cd = 120; cond = "mid_range"; resource = "own"; cost = 1 },
        @{ id = "battojutsu_dash"; anim = "epicfight:biped/skill/battojutsu_dash"; cd = 120; cond = "mid_range"; resource = "own"; cost = 1 },
        @{ id = "blade_rush_try"; anim = "epicfight:biped/skill/blade_rush_try"; cd = 120; cond = "melee"; resource = "own"; cost = 1 },
        @{ id = "blade_rush_combo1"; anim = "epicfight:biped/skill/blade_rush_combo1"; cd = 120; cond = "melee"; resource = "own"; cost = 1 },
        @{ id = "blade_rush_combo2"; anim = "epicfight:biped/skill/blade_rush_combo2"; cd = 120; cond = "melee"; resource = "own"; cost = 1 },
        @{ id = "blade_rush_combo3"; anim = "epicfight:biped/skill/blade_rush_combo3"; cd = 120; cond = "melee"; resource = "own"; cost = 1 },
        @{ id = "blade_rush_execute"; anim = "epicfight:biped/skill/blade_rush_execute"; cd = 120; cond = "melee"; resource = "own"; cost = 1 }
    ) }
    "TACHI" = @{ skills = @(
        @{ id = "rushing_tempo1"; anim = "epicfight:biped/skill/rushing_tempo1"; cd = 120; cond = "melee"; resource = "own"; cost = 1 },
        @{ id = "rushing_tempo2"; anim = "epicfight:biped/skill/rushing_tempo2"; cd = 120; cond = "melee"; resource = "own"; cost = 1 },
        @{ id = "rushing_tempo3"; anim = "epicfight:biped/skill/rushing_tempo3"; cd = 120; cond = "melee"; resource = "own"; cost = 1 }
    ) }
    "LONGSWORD" = @{ skills = @(
        @{ id = "sharp_stab"; anim = "epicfight:biped/skill/sharp_stab"; cd = 120; cond = "mid_range"; resource = "own"; cost = 1 },
        @{ id = "eviscerate_first"; anim = "epicfight:biped/skill/eviscerate_first"; cd = 120; cond = "melee"; resource = "own"; cost = 1 },
        @{ id = "eviscerate_second"; anim = "epicfight:biped/skill/eviscerate_second"; cd = 120; cond = "melee"; resource = "own"; cost = 1 }
    ) }
    "GREATSWORD" = @{ skills = @(
        @{ id = "steel_whirlwind_charging"; anim = "epicfight:biped/skill/steel_whirlwind_charging"; cd = 240; cond = "melee"; resource = "own"; cost = 1 },
        @{ id = "steel_whirlwind"; anim = "epicfight:biped/skill/steel_whirlwind"; cd = 240; cond = "melee"; resource = "own"; cost = 1 }
    ) }
    "FIST" = @{ skills = @(
        @{ id = "relentless_combo"; anim = "epicfight:biped/skill/relentless_combo"; cd = 120; cond = "melee"; resource = "own"; cost = 1 }
    ) }
    "TRIDENT" = @{ skills = @(
        @{ id = "tsunami"; anim = "epicfight:biped/skill/tsunami"; cd = 120; cond = "melee"; resource = "own"; cost = 1 },
        @{ id = "tsunami_reinforced"; anim = "epicfight:biped/skill/tsunami_reinforced"; cd = 120; cond = "melee"; resource = "own"; cost = 1 },
        @{ id = "wrathful_lighting"; anim = "epicfight:biped/skill/wrathful_lighting"; cd = 120; cond = "mid_range"; resource = "own"; cost = 1 }
    ) }
}

# ---------------------------------------------------------------
# 分类 EFN 条目并生成 JSON
# ---------------------------------------------------------------
$weapons = @{}
foreach ($dir in ($anims.Keys | Sort-Object)) {
    if ($motionDirs -contains $dir) { continue }
    $autos = @()
    $skills = @()
    foreach ($full in ($anims[$dir] | Sort-Object)) {
        $rel = $full -replace "^[^:]+:", ""
        $kind = Classify-Path $rel
        if ($null -eq $kind -or $kind -eq "motion") { continue }
        if ($kind -eq "auto") {
            $autos += $rel
        } else {
            $leaf = ($rel -split "/")[-1]
            $n = $leaf.ToLower()
            if ($n -match "drive|dash") { $cond = "mid_range" }
            elseif ($n -match "air|aerial|stomp") { $cond = "airborne" }
            else { $cond = "melee" }
            $cd = 120
            if ($n -match "judgementcut|volcano|zandatsu|charge|all") { $cd = 240 }
            $resource = if ($eftlmStackDirs -contains $dir) { "eftlm" } else { "own" }
            $skills += @{ id = [string]$leaf; anim = [string]$full; cd = [int]$cd; cond = [string]$cond; resource = [string]$resource; cost = 1 }
        }
    }
    if ($skills.Count -eq 0 -and $autos.Count -eq 0) { continue }
    $itemList = if ($itemsOf.ContainsKey($dir)) { $itemsOf[$dir] } else { @() }
    $entry = @{ items = $itemList; autos = $autos; skills = $skills }
    $weapons[$dir] = $entry
}

# WOM 条目（已在 Read-Wom 内分类）直接并入
if ($womData) {
    foreach ($prefix in $womData.Keys) {
        $itemList = if ($womItems.ContainsKey($prefix)) { $womItems[$prefix] } else { @() }
        $weapons[$prefix] = @{ items = $itemList; autos = $womData[$prefix].autos; skills = $womData[$prefix].skills }
    }
}
# 迅捷护腕无 WOM 专属动画：技能为原版 EF relentless_combo（原 WomCompat 行为表条目）
$weapons["celerity_bracelet"] = @{
    items  = @("celerity_bracelet")
    autos  = @()
    skills = @(
        @{ id = "relentless_combo"; anim = "epicfight:biped/skill/relentless_combo"; cd = 120; cond = "melee"; resource = "own"; cost = 1 }
    )
}

$root = @{
    comment = "Skill catalog extracted by tools/extract_efn_skills.ps1 from EFN/WOM jar animation assets + curated vanilla EpicFight innate skills (heuristic classification, re-run after mod updates; tune cd/cond/resource manually if needed)"
    version = 2
    weapons = $weapons
    categories = $vanillaCategories
}

Add-Type -AssemblyName System.Web.Extensions
$ser = New-Object System.Web.Script.Serialization.JavaScriptSerializer
$ser.MaxJsonLength = [int]::MaxValue
$ser.Serialize($root) | Set-Content -Path $Out -Encoding UTF8
$totalSkills = ($weapons.Values | ForEach-Object { $_.skills.Count } | Measure-Object -Sum).Sum
$totalAutos = ($weapons.Values | ForEach-Object { $_.autos.Count } | Measure-Object -Sum).Sum
Write-Output "done: $Out"
Write-Output "weapons=$($weapons.Count) skills=$totalSkills autos=$totalAutos (jar=$jarPath)"

