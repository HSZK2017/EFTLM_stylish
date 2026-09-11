<#
.SYNOPSIS
EFTLM 训练服务器守护进程（guardian）：spark 监控联动 + 心跳看门狗 + 自动重启 + 应急降速。

.DESCRIPTION
与模组内 AutoTicker（智能性能调度）联动：
  - AutoTicker（Java 侧）：空闲自动加速 / 负载自动降速 / tick 停滞看门狗（写 watchdog_alarm.txt）
  - guardian（本脚本，外部进程）：
      1) 每 15s 检查 RCON 可达 + 服务器日志心跳新鲜度（[RL] heartbeat）
      2) 每 60s 经 RCON 触发 `spark tps`，从服务器日志解析 TPS 写入 spark_tps.csv（实时监控）
      3) spark TPS 持续低于阈值 → RCON 发 `/rl tickrate 1.0` 强制降速（应急减速）
      4) 心跳超时 / RCON 无响应 → 优雅 stop → 超时强杀 → 重启服务器（最多 3 次，之后告警暂停）
      5) 读取 config/eftlm_stylish/watchdog_alarm.txt（AutoTicker 看门狗报警）→ 记录并纳入重启判定

.PARAMETER ServerDir
服务器根目录（含 user_jvm_args.txt 与 libraries/）。默认 prod_server。

.PARAMETER RconPort
RCON 端口（默认 25575）。

.PARAMETER RconPassword
RCON 密码（来源：环境变量 EFTLM_RCON_PASSWORD 或 tools\rcon_password.txt；P0 修复后不再硬编码）。

.PARAMETER HeartbeatTimeoutSec
心跳超时（秒，默认 120）：超过则判定服务器卡死，进入重启流程。

.PARAMETER SparkTpsThreshold
spark 实测 TPS 低于此值（连续 2 次采样）→ 强制降速到 1.0（默认 15）。

.PARAMETER RestartLimit
连续重启上限（默认 3），超出后告警并暂停（避免重启风暴）。

.EXAMPLE
.\guardian.ps1 -ServerDir "E:\program\JAVA\touhou little maid - unknow sky area\prod_server" -RconPort 25575 # password comes from env EFTLM_RCON_PASSWORD or tools\rcon_password.txt
#>

param(
    [string]$ServerDir = "E:\program\JAVA\touhou little maid - unknow sky area\prod_server",
    [int]$RconPort = 25575,
    [string]$RconPassword = "",
    [int]$HeartbeatTimeoutSec = 120,
    [int]$SparkTpsThreshold = 15,
    [int]$RestartLimit = 3,
    [string]$AgentSessionId = "",
    [string]$InjectScript = "F:\Deepseek Harness\dsh-easy-use\dsh-inject\dsh-inject.py",
    [int]$AgentInjectCooldownSec = 300
)

# ---- RCON credential (P0 fix 2026-09-10): no hardcoded password ----
. (Join-Path $PSScriptRoot "rcon_cred.ps1")
$RconPassword = Resolve-RconPassword -Explicit $RconPassword -ScriptRoot $PSScriptRoot
if ([string]::IsNullOrWhiteSpace($RconPassword)) {
    Write-Host "[guardian] FATAL: RCON password not configured. Set env EFTLM_RCON_PASSWORD or tools\rcon_password.txt" -ForegroundColor Red
    exit 1
}$ErrorActionPreference = "Continue"
$CheckIntervalSec = 15
$SparkIntervalSec = 60
$LogDir = Join-Path $ServerDir "guardian"
$LogFile = Join-Path $LogDir "guardian.log"
$StateFile = Join-Path $LogDir "guardian.state"
$SparkCsv = Join-Path $LogDir "spark_tps.csv"
$ServerLog = Join-Path $ServerDir "logs\latest.log"
$WatchdogAlarm = Join-Path $ServerDir "config\eftlm_stylish\watchdog_alarm.txt"
$JavaExe = "E:\Program Files\Java\jdk-17\bin\java.exe"
$StartCmd = "cmd /c `"$JavaExe`" @user_jvm_args.txt @libraries/net/minecraftforge/forge/1.20.1-47.4.13/win_args.txt nogui"

New-Item -ItemType Directory -Force -Path $LogDir | Out-Null

# ---------- 单实例锁（V54 教训：多 guardian 并发 → 各自判定 unhealthy → 并发重启风暴） ----------
# 2026-09-11 修复（断电重启实测故障）：旧检查只比对 PID 是否"存在"，
# 断电重启后 PID 会被系统复用（本次锁里的 10812 被 ChsIME.exe 占用）→ 误判"另一个 guardian 在跑"
# → 守护进程静默退出，服务器整夜无人守护。现在必须同时满足"该 PID 存在 **且** 命令行里确实是
# guardian.ps1"才认定为并发实例；否则视为陈旧锁接管。
$LockFile = Join-Path $LogDir "guardian.lock"
try {
    if (Test-Path $LockFile) {
        $lockPid = 0
        try { $lockPid = [int](Get-Content $LockFile -Raw).Trim() } catch { $lockPid = 0 }
        $lockIsGuardian = $false
        if ($lockPid -gt 0 -and $lockPid -ne $PID) {
            $proc = Get-CimInstance Win32_Process -Filter "ProcessId=$lockPid" -ErrorAction SilentlyContinue
            if ($proc -and $proc.CommandLine -and $proc.CommandLine -match 'guardian\.ps1') {
                $lockIsGuardian = $true
            }
        }
        if ($lockIsGuardian) {
            Write-Host "[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] another guardian already running (pid=$lockPid), exiting to avoid restart storm"
            exit 0
        }
        Write-Host "[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] stale/foreign lock detected (pid=$lockPid), taking over"
    }
    [System.IO.File]::WriteAllText($LockFile, [string]$PID)
} catch {
    Write-Host "[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] lock check failed: $($_.Exception.Message)"
}

# ---------- dsh-inject 自动唤起 agent（V54 追加：同事件去重 + 失败重试对数增长） ----------
# 去重：同类型事件（标题前缀）10 分钟内只发一条——解决崩溃-重启循环期间刷屏
# （如 06:23-07:50 事故期 150+ 条历史通知：多 guardian 并发 + 每 300s 重复投递）。
# 失败重试：发送失败注册 pending，重试间隔对数（指数）增长 1/2/4/8/16 分钟，最多 5 次。
$notifyDedup = @{}                 # 标题前缀 -> 最近发送 epoch 毫秒
$NotifyDedupWindowMs = 600000      # 同类型去重窗口：10 分钟
$script:pendingNotify = $null      # 待重试通知 {title, detail, tries, nextRetryMs}
$NotifyRetryBaseMs = 60000         # 首重试间隔 1 分钟
$NotifyMaxTries = 5                # 最大重试次数

function Send-DshAgent($title, $detail) {
    $prompt = "[守护进程自动通知] $title ($(Get-Date -Format 'yyyy-MM-dd HH:mm:ss'))`n$detail`n请检查服务器状态并进行必要维护。"
    $injectArgs = @($prompt, "--autolunch")
    if ($AgentSessionId) {
        $injectArgs += @("--session", $AgentSessionId)
    }
    # 2026-09-11 修复（P1）：原实现无论成败都 return $true（不看脚本是否存在/退出码），
    # 于是"通知静默丢失"时不会重试、也不会告警——而它恰是无人值守时唯一的报警通道。
    if (-not (Test-Path $InjectScript)) {
        Write-Log "ALERT: inject script not found: $InjectScript (agent notify disabled)"
        return $false
    }
    try {
        $out = (& python $InjectScript @injectArgs 2>&1 | Out-String).Trim()
        if ($LASTEXITCODE -ne 0) {
            Write-Log "ALERT: agent notify FAILED (exit=$LASTEXITCODE): $(($out -replace "`r?`n", ' | '))"
            return $false
        }
        Write-Log "agent notify sent: $(($out -replace "`r?`n", ' | '))"
        return $true
    } catch {
        Write-Log "ALERT: agent notify exception: $($_.Exception.Message)"
        return $false
    }
}

function Invoke-DshAgent($title, $detail) {
    # 去重键：标题括号前的类型名（如 "服务器异常" / "服务器已自动恢复"）
    $sig = ($title -split '\(')[0].Trim()
    $nowMs = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
    if ($notifyDedup.ContainsKey($sig) -and ($nowMs - $notifyDedup[$sig]) -lt $NotifyDedupWindowMs) {
        Write-Log "agent notify deduped ($sig, last=$(([DateTimeOffset]::FromUnixTimeMilliseconds($notifyDedup[$sig])).LocalDateTime.ToString('HH:mm:ss')))"
        return
    }
    if (Send-DshAgent $title $detail) {
        $notifyDedup[$sig] = $nowMs
        Write-Log "agent notified: $sig"
    } else {
        $script:pendingNotify = @{ title = $title; detail = $detail; tries = 0; nextRetryMs = $nowMs + $NotifyRetryBaseMs }
        Write-Log "agent notify FAILED, retry scheduled (base ${NotifyRetryBaseMs}ms, max $NotifyMaxTries tries)"
    }
}

function Retry-PendingNotify {
    if ($null -eq $script:pendingNotify) { return }
    $nowMs = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
    if ($nowMs -lt $script:pendingNotify.nextRetryMs) { return }
    if (Send-DshAgent $script:pendingNotify.title $script:pendingNotify.detail) {
        $sig = ($script:pendingNotify.title -split '\(')[0].Trim()
        $notifyDedup[$sig] = $nowMs
        Write-Log "agent notify retry OK (tries=$($script:pendingNotify.tries + 1))"
        $script:pendingNotify = $null
    } else {
        $script:pendingNotify.tries++
        if ($script:pendingNotify.tries -ge $NotifyMaxTries) {
            Write-Log "agent notify retry exhausted ($NotifyMaxTries tries), dropping notification"
            $script:pendingNotify = $null
        } else {
            $script:pendingNotify.nextRetryMs = $nowMs + $NotifyRetryBaseMs * [Math]::Pow(2, $script:pendingNotify.tries)
            Write-Log "agent notify retry #$($script:pendingNotify.tries) failed, next in $([Math]::Pow(2, $script:pendingNotify.tries))min"
        }
    }
}

function Write-Log($msg) {
    $line = "[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] $msg"
    Add-Content -Path $LogFile -Value $line -Encoding UTF8
    Write-Host $line
}

function Write-State($state, $detail) {
    $json = [ordered]@{
        time       = (Get-Date -Format 'yyyy-MM-dd HH:mm:ss')
        state      = $state
        detail     = $detail
        multiplier = "n/a"
        sparkTps   = "n/a"
    }
    $json | ConvertTo-Json -Compress | Set-Content -Path $StateFile -Encoding UTF8
}

# ---------- RCON 客户端 ----------
function Send-Rcon($cmd) {
    # P1 修复（2026-09-10）：
    #   ① 校验认证应答**类型**（2 = 成功 / -1 = 失败）——旧实现丢弃应答，密码错误时命令静默
    #      返回 $null，健康判定/降速/清理全部失效却无告警；
    #   ② try/finally 释放 TcpClient 与 NetworkStream——旧实现从不 Dispose，每 15s 泄漏 2~4 个句柄。
    # 注意：认证成功与失败都是"空体"应答，只能靠包类型区分（这是上一版"空体=失败"写法的坑）。
    $s = $null
    $ns = $null
    try {
        $s = New-Object System.Net.Sockets.TcpClient
        $s.Connect("127.0.0.1", $RconPort)
        $s.ReceiveTimeout = 4000
        $ns = $s.GetStream()
        function New-Pkt($ptype, $payload) {
            $body = [System.Text.Encoding]::UTF8.GetBytes($payload)
            $ms = New-Object System.IO.MemoryStream
            $bw = New-Object System.IO.BinaryWriter($ms)
            $bw.Write([int]($body.Length + 10)); $bw.Write([int]0)
            $bw.Write([int]$ptype); $bw.Write($body)
            $bw.Write([byte]0); $bw.Write([byte]0)
            return $ms.ToArray()
        }
        function Read-Pkt($stream) {
            $hdr = New-Object byte[] 12
            $read = 0
            while ($read -lt 12) {
                $n = $stream.Read($hdr, $read, 12 - $read)
                if ($n -le 0) { return $null }
                $read += $n
            }
            $len = [BitConverter]::ToInt32($hdr, 0)
            # 包头布局：length(0) + requestId(4) + type(8) + body + 2×NUL
            # 2026-09-10 实测教训：type 在偏移 8；写成偏移 4 会读到 requestId(=0)，
            # 于是把正常握手误判成"认证失败"（guardian 每 15s 刷一条 RCON auth FAILED）。
            $ptype = [BitConverter]::ToInt32($hdr, 8)
            $bodyLen = [Math]::Max(0, $len - 8)
            $body = New-Object byte[] $bodyLen
            $read = 0
            while ($read -lt $bodyLen) {
                $n = $stream.Read($body, $read, $bodyLen - $read)
                if ($n -le 0) { break }
                $read += $n
            }
            return @{ Type = $ptype; Body = [System.Text.Encoding]::UTF8.GetString($body, 0, $body.Length).TrimEnd([char]0) }
        }
        $authBytes = New-Pkt 3 $RconPassword
        $ns.Write($authBytes, 0, $authBytes.Length)
        $auth = Read-Pkt $ns
        if ($null -eq $auth) {
            Write-Log "RCON auth: no response (connection closed?)"
            return $null
        }
        # 认证判定：失败回 -1；成功回 AUTH_RESPONSE(2)，其间可能出现空 RESPONSE_VALUE(0)。
        # 因此"只有看到 -1 才算失败"，最多读 3 个包再兜底判失败。
        $authed = ($auth.Type -eq 2)
        for ($i = 0; -not $authed -and $i -lt 2; $i++) {
            if ($auth.Type -eq -1) {
                Write-Log "RCON auth REJECTED by server (bad password; set env EFTLM_RCON_PASSWORD or tools/rcon_password.txt)"
                return $null
            }
            $auth = Read-Pkt $ns
            if ($null -eq $auth) { break }
            if ($auth.Type -eq -1) {
                Write-Log "RCON auth REJECTED by server (bad password)"
                return $null
            }
            $authed = ($auth.Type -eq 2)
        }
        if (-not $authed) {
            Write-Log "RCON auth: no AUTH_RESPONSE received (types seen: $($auth.Type))"
            return $null
        }
        $cmdBytes = New-Pkt 2 $cmd
        $ns.Write($cmdBytes, 0, $cmdBytes.Length)
        $resp = Read-Pkt $ns
        if ($null -eq $resp) { return $null }
        return $resp.Body
    } catch {
        return $null
    } finally {
        if ($null -ne $ns) { try { $ns.Dispose() } catch { } }
        if ($null -ne $s) { try { $s.Close(); $s.Dispose() } catch { } }
    }
}

# ---------- 工具 ----------
function Get-ServerPid {
    $c = Get-NetTCPConnection -State Listen -LocalPort $RconPort -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($c) { return $c.OwningProcess }
    return $null
}

function Get-LastHeartbeatAgeSec {
    if (-not (Test-Path $ServerLog)) { return -1 }
    try {
        $line = Get-Content $ServerLog -Tail 3000 | Where-Object { $_ -match "heartbeat: tick=" } | Select-Object -Last 1
        if (-not $line) { return -1 }
        # 格式： [278?2026 00:49:55.994] 或 [2026-08-26 00:49:55.994]
        if ($line -match '\[(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})\.\d+\]') {
            $ts = Get-Date -Year $matches[1] -Month $matches[2] -Day $matches[3] -Hour $matches[4] -Minute $matches[5] -Second $matches[6]
            return [int]((Get-Date) - $ts).TotalSeconds
        }
        if ($line -match '\[[^\]]*?(\d{2}):(\d{2}):(\d{2})\.\d+\]') {
            $ts = Get-Date -Hour $matches[1] -Minute $matches[2] -Second $matches[3]
            # P1 修复：日志时间戳无日期时，跨日/跨零点会得到"未来时刻"或巨大 age。
            # 未来 → 回退一天；仍比现在晚 5 分钟以上（时钟不同步/跨日边界）也回退一天。
            if ($ts -gt (Get-Date)) { $ts = $ts.AddDays(-1) }
            if (((Get-Date) - $ts).TotalSeconds -lt -300) { $ts = $ts.AddDays(-1) }
            return [int]((Get-Date) - $ts).TotalSeconds
        }
        return -1
    } catch {
        return -1
    }
}

function Get-LastSparkTps {
    if (-not (Test-Path $ServerLog)) { return -1 }
    try {
        $lines = Get-Content $ServerLog -Tail 3000
        for ($i = $lines.Count - 1; $i -ge 0; $i--) {
            if ($lines[$i] -match "TPS from last") {
                # 数值在下一行：  [..]  19.95, 19.98, 19.99, 20.0, 20.0
                if ($i + 1 -lt $lines.Count -and $lines[$i + 1] -match '(\d+\.\d+),') {
                    return [double]$matches[1]   # 最近 5s TPS
                }
                return -1
            }
        }
        return -1
    } catch {
        return -1
    }
}

function Get-WatchdogAlarm {
    if (Test-Path $WatchdogAlarm) {
        try { return Get-Content $WatchdogAlarm -Raw } catch { return "" }
    }
    return $null
}

# ---------- 服务器生命周期 ----------
function Stop-ServerGraceful {
    $r = Send-Rcon "stop"
    if ($null -eq $r) { Write-Log "RCON unreachable, cannot graceful stop" }
    else { Write-Log "RCON stop sent: $r" }
    for ($i = 0; $i -lt 6; $i++) {
        Start-Sleep -Seconds 5
        if (-not (Get-ServerPid)) { return $true }
    }
    return $false
}

function Kill-Server {
    $pid2 = Get-ServerPid
    if ($pid2) {
        Write-Log "force killing server pid $pid2"
        Stop-Process -Id $pid2 -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 3
    }
}

function Start-Server {
    # P1 修复（2026-09-10）：Start-Process 失败（java 路径不存在/端口占用）会抛异常，
    # 旧实现让它冒泡且主循环无 try/catch → guardian 整体退出、服务器永久无人守护。
    Push-Location $ServerDir
    try {
        Write-Log "starting server: $StartCmd"
        $p = Start-Process -FilePath "cmd.exe" -ArgumentList "/c", "`"$JavaExe`" @user_jvm_args.txt @libraries/net/minecraftforge/forge/1.20.1-47.4.13/win_args.txt nogui" -WindowStyle Hidden -PassThru -ErrorAction Stop
        if ($null -eq $p) {
            Write-Log "ALERT: Start-Process returned null (server NOT started; check JavaExe path / port conflict)"
            return $null
        }
        Write-Log "server started pid=$($p.Id)"
        return $p
    } catch {
        Write-Log "ALERT: Start-Server failed: $($_.Exception.Message)"
        return $null
    } finally {
        Pop-Location
    }
}

function Wait-ServerReady($timeoutSec) {
    for ($i = 0; $i -lt $timeoutSec; $i += 10) {
        Start-Sleep -Seconds 10
        $r = Send-Rcon "list"
        if ($null -ne $r) { return $true }
    }
    return $false
}

# ---------- 主循环 ----------
Write-Log "===== guardian started (interval=${CheckIntervalSec}s, heartbeat-timeout=${HeartbeatTimeoutSec}s, spark-tps-threshold=${SparkTpsThreshold}, restart-limit=${RestartLimit}) ====="
if (-not (Test-Path $SparkCsv)) {
    Set-Content -Path $SparkCsv -Value "time,tps5s,tick95_ms" -Encoding UTF8
}

$restartCount = 0
$lowTpsStreak = 0
$lastSparkCheck = [DateTime]::MinValue
$pauseUntil = [DateTime]::MinValue
# 实体清理（V53 教训：竞技场长时间运行累积掉落物/墓碑实体（实测 725 个）
# → 寻路节点爆炸 + tick 卡死 + 重启崩溃。每 30 分钟清一次物品实体与 TLM 墓碑，
# 女仆/标靶等训练实体不受影响）。
$lastEntityClean = [DateTime]::MinValue
$EntityCleanIntervalSec = 1800
# 启动宽限期：guardian 刚启动时服务器可能正在拉起（外部管理/手动启动），
# 90s 内不做 unhealthy 重启判定，避免"服务器未就绪被误判 → 重复启动 → 端口/SSL 冲突"。
$startupGraceUntil = (Get-Date).AddSeconds(90)

while ($true) {
  try {
    $now = [DateTime]::Now
    # 待重试通知检查（失败重试，对数增长间隔）
    Retry-PendingNotify
    $pid2 = Get-ServerPid
    $rconOk = $null -ne (Send-Rcon "list")
    $hbAge = Get-LastHeartbeatAgeSec
    $alarm = Get-WatchdogAlarm

    # --- 周期实体清理（掉落物/墓碑；训练实体不动） ---
    if (($now - $lastEntityClean).TotalSeconds -ge $EntityCleanIntervalSec -and $rconOk) {
        $lastEntityClean = $now
        Send-Rcon "kill @e[type=item]" | Out-Null
        Send-Rcon "kill @e[type=touhou_little_maid:tombstone]" | Out-Null
        Write-Log "entity cleanup: item/tombstone entities cleared (every 30min)"
    }

    # --- spark 周期性 TPS 采样（RCON 触发，从日志解析） ---
    if (($now - $lastSparkCheck).TotalSeconds -ge $SparkIntervalSec -and $rconOk) {
        $lastSparkCheck = $now
        Send-Rcon "spark tps" | Out-Null
        Start-Sleep -Seconds 2
        $tps = Get-LastSparkTps
        if ($tps -ge 0) {
            Add-Content -Path $SparkCsv -Value "$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss'),$tps,n/a" -Encoding UTF8
            Write-Log "spark tps: $tps"
            if ($tps -lt $SparkTpsThreshold) {
                $lowTpsStreak++
                if ($lowTpsStreak -ge 2) {
                    Write-Log "ALERT: spark TPS=$tps below threshold ${SparkTpsThreshold} x2 -> forcing /rl tickrate 1.0"
                    Send-Rcon "rl tickrate 1.0" | Out-Null
                    $lowTpsStreak = 0
                }
            } else {
                $lowTpsStreak = 0
            }
        }
    }

    # --- 看门狗报警联动：新鲜报警（模组内看门狗判定 tick 停滞/卡死）→ 立即进入重启流程，不等心跳超时 ---
    $alarmFresh = $false
    if ($alarm) {
        try {
            $alarmAge = (Get-Date) - (Get-Item $WatchdogAlarm).LastWriteTime
            $alarmFresh = $alarmAge.TotalSeconds -lt 90
            Write-Log "WATCHDOG alarm file present: $alarm (age=$([int]$alarmAge.TotalSeconds)s, fresh=$alarmFresh)"
        } catch {
            Write-Log "WATCHDOG alarm file present: $alarm"
        }
    }

    # --- 健康判定 ---
    # --- 健康判定 ---
    # V54 追加（2026-08-28 09:40 教训）：RCON 失败不再独立判死——服务器卡顿期间
    # RCON 线程可能受影响（list 超时）而 AutoTicker 的 clock reset 正在自愈；
    # 心跳（[RL] heartbeat）是主线程活的直接证据，以其新鲜度为健康准绳，
    # 心跳超时才进重启流程（RCON 仍用于 spark 采样/实体清理等辅助功能）。
    $healthy = ($hbAge -ge 0 -and $hbAge -le $HeartbeatTimeoutSec) -and (-not $alarmFresh)
    if ($healthy) {
        $restartCount = 0
        $pauseUntil = [DateTime]::MinValue
        # V54 追加（12:30 教训）：心跳健康时清除残留 alarm 文件——否则新实例启动期
        # AutoTicker 的停滞报警残留会让 guardian 误判 unhealthy → 连环重启循环
        if ($alarm) {
            Remove-Item $WatchdogAlarm -Force -ErrorAction SilentlyContinue
            Write-Log "stale alarm cleared (server healthy)"
        }
        Write-State "healthy" "rcon=ok hb_age=${hbAge}s alarm=$([bool]$alarm)"
        Start-Sleep -Seconds $CheckIntervalSec
        continue
    }

    # --- 启动宽限期（不做任何重启动作） ---
    if ($now -lt $startupGraceUntil) {
        Write-State "grace" "startup grace until $startupGraceUntil"
        Start-Sleep -Seconds $CheckIntervalSec
        continue
    }

    # --- 异常处理 ---
    if ($now -lt $pauseUntil) {
        Write-Log "restart pause until $pauseUntil (limit reached), skipping"
        Write-State "paused" "restart limit reached"
        Start-Sleep -Seconds $CheckIntervalSec
        continue
    }

    Write-Log "UNHEALTHY: pid=$pid2 rcon=$rconOk hb_age=${hbAge}s alarm=$([bool]$alarm) restartCount=$restartCount"
    Write-State "unhealthy" "pid=$pid2 rcon=$rconOk hb_age=${hbAge}s"
    Invoke-DshAgent "服务器异常" "pid=$pid2 rcon=$rconOk hb_age=${hbAge}s watchdog_alarm=$([bool]$alarm) restartCount=$restartCount spark_tps=见 guardian.log"

    if ($restartCount -ge $RestartLimit) {
        Write-Log "ALERT: restart limit ($RestartLimit) reached, pausing guardian actions until $((Get-Date).AddMinutes(30))"
        Invoke-DshAgent "服务器恢复失败（重启上限）" "连续重启 $RestartLimit 次仍未恢复，guardian 暂停 30 分钟。请人工介入检查服务器进程/日志/崩溃原因。"
        $pauseUntil = (Get-Date).AddMinutes(30)
        Start-Sleep -Seconds $CheckIntervalSec
        continue
    }

    $restartCount++
    Write-Log "RESTART #$restartCount ..."
    if (-not (Stop-ServerGraceful)) {
        Kill-Server
    }
    Start-Sleep -Seconds 3
    Start-Server | Out-Null
    # P1 修复：每次重启后重置启动宽限期（覆盖 Wait-ServerReady 的 240s 上限），
    # 否则服务器仍在拉起时下一轮检查会立刻再次判 unhealthy → 快速吃掉重启上限（V54 重启风暴同型）。
    $startupGraceUntil = (Get-Date).AddSeconds(300)
    $ready = Wait-ServerReady 240
    if ($ready) {
        Write-Log "server restarted and ready (attempt #$restartCount)"
        Write-State "restarted" "attempt=$restartCount"
        Invoke-DshAgent "服务器已自动恢复" "崩溃/卡死后由 guardian 自动重启成功（attempt #$restartCount，pid=$pid2）。请检查：tick 停滞原因（thread_dump_*.txt）、AutoTicker 恢复情况、训练流水线锚定。"
    } else {
        Write-Log "ALERT: server did not become ready within 240s after restart #$restartCount"
        Invoke-DshAgent "服务器重启未就绪" "restart #$restartCount 后 240s 内 RCON 不可达。请检查启动日志（latest.log）与 mods 目录。"
    }
    Start-Sleep -Seconds $CheckIntervalSec
  } catch {
    # P1 修复：主循环兜底——任何未预期异常都只跳过本轮，不再让守护进程整体退出
    Write-Log "ALERT: guardian loop exception: $($_.Exception.Message)"
    Write-State "error" "loop exception: $($_.Exception.Message)"
    Start-Sleep -Seconds $CheckIntervalSec
  }
}
