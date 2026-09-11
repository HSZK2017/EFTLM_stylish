<#
.SYNOPSIS
EFTLM 性能探测监控脚本：持续采样系统资源 + 服务器加速状态，用于测出最佳加速倍率。

.DESCRIPTION
每 IntervalSec 秒采样一次并追加写入 CSV：
  - 整机 CPU%（Get-Counter _Total）
  - 单核峰值 CPU%（所有 Processor 实例取最大）
  - 内存剩余 GB
  - 服务器侧：/rl autotick status（RCON）→ 当前倍率 / EMA cpu / tick95ms / 真实 TPS
  - 日志心跳推导真实 TPS（备用，RCON 失败时用）
  - 新出现的 [AutoTick] 升降档事件行（打印到控制台）

.PARAMETER ServerDir
服务器根目录（默认 prod_server）。
.PARAMETER OutCsv
CSV 输出路径（默认 E:\program\JAVA\EFTLM-example\perf_monitor\perf.csv）。
.PARAMETER IntervalSec
采样间隔（秒，默认 10）。
.PARAMETER DurationMin
监控时长（分钟，默认 0 = 无限，Ctrl-C 停止）。
.EXAMPLE
.\monitor_perf.ps1 -DurationMin 60
#>

param(
    [string]$ServerDir = "E:\program\JAVA\touhou little maid - unknow sky area\prod_server",
    [string]$OutCsv = "E:\program\JAVA\EFTLM-example\perf_monitor\perf.csv",
    [int]$IntervalSec = 10,
    [int]$DurationMin = 0
)

$RconPort = 25575
# RCON credential (P0 fix 2026-09-10): env var or tools/rcon_password.txt
. (Join-Path $PSScriptRoot "rcon_cred.ps1")
$RconPassword = Resolve-RconPassword -ScriptRoot $PSScriptRoot
if ([string]::IsNullOrWhiteSpace($RconPassword)) { Write-Host "[monitor_perf] RCON password not configured - RCON sampling will fail" }
$ServerLog = Join-Path $ServerDir "logs\latest.log"
$OutDir = Split-Path $OutCsv -Parent
New-Item -ItemType Directory -Force -Path $OutDir | Out-Null

if (-not (Test-Path $OutCsv)) {
    Set-Content -Path $OutCsv -Value "time,mult,cpu_total,cpu_maxcore,mem_free_gb,tick95_ms,realtps,rcon_ok" -Encoding UTF8
}

# ---------- RCON ----------
function Send-Rcon($cmd) {
    try {
        $s = New-Object System.Net.Sockets.TcpClient
        $s.Connect("127.0.0.1", $RconPort)
        $s.ReceiveTimeout = 4000
        $ns = $s.GetStream()
        function Pkt($ptype, $payload) {
            $body = [System.Text.Encoding]::UTF8.GetBytes($payload)
            $ms = New-Object System.IO.MemoryStream
            $bw = New-Object System.IO.BinaryWriter($ms)
            $bw.Write([int]($body.Length + 10)); $bw.Write([int]0)
            $bw.Write([int]$ptype); $bw.Write($body)
            $bw.Write([byte]0); $bw.Write([byte]0)
            return $ms.ToArray()
        }
        function ReadPkt($ns) {
            $hdr = New-Object byte[] 12
            $read = 0
            while ($read -lt 12) {
                $n = $ns.Read($hdr, $read, 12 - $read)
                if ($n -le 0) { return "" }
                $read += $n
            }
            $len = [BitConverter]::ToInt32($hdr, 0)
            $payload = New-Object byte[] ($len - 8)
            $read = 0
            while ($read -lt $payload.Length) {
                $n = $ns.Read($payload, $read, $payload.Length - $read)
                if ($n -le 0) { break }
                $read += $n
            }
            return [System.Text.Encoding]::UTF8.GetString($payload, 0, $payload.Length).TrimEnd([char]0)
        }
        $ns.Write((Pkt 3 $RconPassword), 0, (Pkt 3 $RconPassword).Length)
        ReadPkt $ns | Out-Null
        $ns.Write((Pkt 2 $cmd), 0, (Pkt 2 $cmd).Length)
        $resp = ReadPkt $ns
        $s.Close()
        return $resp
    } catch {
        return $null
    }
}

# ---------- 采样函数 ----------
function Get-CpuSamples {
    $r = @{ total = -1; maxcore = -1 }
    try {
        $c = Get-Counter '\Processor(_Total)\% Processor Time' -ErrorAction Stop
        $r.total = [math]::Round($c.CounterSamples[0].CookedValue, 1)
    } catch { }
    try {
        $c = Get-Counter '\Processor(*)\% Processor Time' -ErrorAction Stop
        $max = -1
        foreach ($s in $c.CounterSamples) {
            if ($s.InstanceName -eq '_total' -or $s.InstanceName -eq 'Total') { continue }
            if ($s.CookedValue -gt $max) { $max = $s.CookedValue }
        }
        $r.maxcore = [math]::Round($max, 1)
    } catch { }
    return $r
}

function Get-MemFreeGb {
    try {
        $os = Get-CimInstance Win32_OperatingSystem
        return [math]::Round($os.FreePhysicalMemory / 1MB, 2)
    } catch { return -1 }
}

function Get-HeartbeatTps {
    if (-not (Test-Path $ServerLog)) { return -1 }
    try {
        $lines = Get-Content $ServerLog -Tail 4000 | Where-Object { $_ -match "heartbeat: tick=" }
        if ($lines.Count -lt 2) { return -1 }
        $a = $lines[$lines.Count - 2]
        $b = $lines[$lines.Count - 1]
        $ta = $null; $tb = $null; $ka = 0; $kb = 0
        if ($a -match 'tick=(\d+)') { $ka = [int]$matches[1] }
        if ($b -match 'tick=(\d+)') { $kb = [int]$matches[1] }
        $ta = ConvertTo-DateTime $a
        $tb = ConvertTo-DateTime $b
        if ($null -eq $ta -or $null -eq $tb -or $ka -eq 0 -or $kb -eq 0) { return -1 }
        $dt = ($tb - $ta).TotalSeconds
        if ($dt -le 0) { return -1 }
        return [math]::Round(($kb - $ka) / $dt, 1)
    } catch { return -1 }
}

function ConvertTo-DateTime($line) {
    if ($line -match '\[(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})\.\d+\]') {
        return Get-Date -Year $matches[1] -Month $matches[2] -Day $matches[3] -Hour $matches[4] -Minute $matches[5] -Second $matches[6]
    }
    if ($line -match '\[[^\]]*?(\d{2}):(\d{2}):(\d{2})\.\d+\]') {
        $dt = Get-Date -Hour $matches[1] -Minute $matches[2] -Second $matches[3]
        if ($dt -gt (Get-Date)) { $dt = $dt.AddDays(-1) }
        return $dt
    }
    return $null
}

# 上次日志文件大小（增量打印 AutoTick 事件）
$lastLen = 0
if (Test-Path $ServerLog) { $lastLen = (Get-Item $ServerLog).Length }

$startTime = Get-Date
Write-Host "monitor started: interval=${IntervalSec}s csv=$OutCsv"

while ($true) {
    $now = Get-Date
    if ($DurationMin -gt 0 -and ($now - $startTime).TotalMinutes -ge $DurationMin) {
        Write-Host "duration reached, stopping"
        break
    }

    $cpu = Get-CpuSamples
    $mem = Get-MemFreeGb
    $hbTps = Get-HeartbeatTps

    $mult = "n/a"; $tick95 = "n/a"; $atTps = "n/a"; $rconOk = 0
    $resp = Send-Rcon "rl autotick status"
    if ($null -ne $resp -and $resp.Length -gt 0) {
        $rconOk = 1
        if ($resp -match 'multiplier=([\d.]+)') { $mult = $matches[1] }
        if ($resp -match 'tick95=([-\d.]+)ms') { $tick95 = $matches[1] }
        if ($resp -match 'tps=([\d.]+) cur=') { $atTps = $matches[1] }
    }
    $realtps = $atTps
    if ($realtps -eq "n/a" -and $hbTps -ge 0) { $realtps = $hbTps }

    $line = "$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss'),$mult,$($cpu.total),$($cpu.maxcore),$mem,$tick95,$realtps,$rconOk"
    Add-Content -Path $OutCsv -Value $line -Encoding UTF8
    Write-Host $line

    if (Test-Path $ServerLog) {
        $len = (Get-Item $ServerLog).Length
        if ($len -gt $lastLen) {
            # P1 修复（2026-09-10）：StreamReader 也放进 finally——旧实现异常路径下不关闭，
            # 句柄泄漏并锁住 latest.log（妨碍日志轮转/清理）。
            $fs = $null
            $sr = $null
            try {
                $fs = [System.IO.File]::Open($ServerLog, 'Open', 'Read', 'ReadWrite')
                $fs.Seek($lastLen, 'Begin') | Out-Null
                $sr = New-Object System.IO.StreamReader($fs, [System.Text.Encoding]::UTF8)
                $newText = $sr.ReadToEnd()
                foreach ($m in [regex]::Matches($newText, '\[AutoTick\][^\r\n]*')) {
                    Write-Host ("  EVENT: " + $m.Value)
                }
            } catch { } finally {
                if ($null -ne $sr) { try { $sr.Dispose() } catch { } }
                if ($null -ne $fs) { try { $fs.Dispose() } catch { } }
            }
            $lastLen = $len
        }
    }

    Start-Sleep -Seconds $IntervalSec
}
