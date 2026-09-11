# EFTLM mod deploy: build (optional) -> stop guardian -> stop server -> swap jar -> start -> verify.
# ASCII-only on purpose (Chinese text in .ps1 has repeatedly caused encoding damage here).
#
# Usage:
#   powershell -ExecutionPolicy Bypass -File tools\deploy_mod.ps1                 # deploy current build/libs jar
#   ... -Build                                                                     # run gradlew jar reobfJar first
#   ... -BumpEpoch 3                                                               # set SEMANTICS_EPOCH in train_ppo.py (verified)
#   ... -DryRun                                                                    # print plan only
param(
    [string]$ServerDir = "E:\program\JAVA\touhou little maid - unknow sky area\prod_server",
    [string]$RepoDir = "E:\program\JAVA\EFTLM-example",
    [int]$RconPort = 25575,
    [string]$AgentSessionId = "session-013a71d2-5c4f-4f4e-b126-0d447596db21",
    [string]$JarPath = "",
    [int]$BumpEpoch = 0,
    [switch]$Build,
    [switch]$DryRun
)

$ErrorActionPreference = "Stop"
$ModsDir = Join-Path $ServerDir "mods"
$Target = Join-Path $ModsDir "eftlm_stylish-1.0.0.jar"
$LogFile = Join-Path $ServerDir "guardian\deploy.log"

function Write-Log($msg) {
    $line = "[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] $msg"
    Write-Host $line
    try { Add-Content -Path $LogFile -Value $line -Encoding UTF8 } catch { }
}

function Test-Rcon {
    try {
        $c = New-Object System.Net.Sockets.TcpClient
        $c.Connect("127.0.0.1", $RconPort)
        $c.Close()
        return $true
    } catch { return $false }
}

Write-Log "=== deploy start (dry=$DryRun build=$Build bumpEpoch=$BumpEpoch) ==="

# ---------- 1) build ----------
if ($Build) {
    Write-Log "building jar (gradlew jar reobfJar --offline)"
    Push-Location $RepoDir
    try {
        $out = & .\gradlew.bat jar reobfJar --offline 2>&1 | Out-String
        if ($LASTEXITCODE -ne 0) {
            Write-Log "BUILD FAILED (exit=$LASTEXITCODE); last lines:"
            ($out -split "`r?`n" | Select-Object -Last 12) | ForEach-Object { Write-Log "  | $_" }
            exit 1
        }
        Write-Log "build OK"
    } finally { Pop-Location }
}

if (-not $JarPath) { $JarPath = Join-Path $RepoDir "build\libs\eftlm_stylish-1.0.0.jar" }
if (-not (Test-Path $JarPath)) { Write-Log "ERROR: jar not found: $JarPath"; exit 1 }
$jarSize = (Get-Item $JarPath).Length
Write-Log "source jar: $JarPath ($jarSize B)"
if ($jarSize -lt 100000) { Write-Log "ERROR: jar suspiciously small ($jarSize B), aborting"; exit 1 }

# ---------- 2) bump SEMANTICS_EPOCH (verified, all-or-nothing) ----------
if ($BumpEpoch -gt 0) {
    $tppo = Join-Path $RepoDir "train\train_ppo.py"
    $src = [System.IO.File]::ReadAllText($tppo, [System.Text.Encoding]::UTF8)
    if ($src -notmatch '(?m)^SEMANTICS_EPOCH = (\d+)\s*$') {
        Write-Log "ERROR: cannot locate SEMANTICS_EPOCH in $tppo"; exit 1
    }
    $old = [int]$Matches[1]
    if ($old -eq $BumpEpoch) {
        Write-Log "SEMANTICS_EPOCH already $BumpEpoch (no change)"
    } else {
        $new = [regex]::Replace($src, '(?m)^SEMANTICS_EPOCH = \d+\s*$', "SEMANTICS_EPOCH = $BumpEpoch")
        if ($DryRun) {
            Write-Log "[dry] would set SEMANTICS_EPOCH $old -> $BumpEpoch"
        } else {
            [System.IO.File]::WriteAllText($tppo, $new, (New-Object System.Text.UTF8Encoding($false)))
            $chk = [System.IO.File]::ReadAllText($tppo, [System.Text.Encoding]::UTF8)
            if ($chk -notmatch "(?m)^SEMANTICS_EPOCH = $BumpEpoch\s*$") {
                Write-Log "ERROR: epoch write-back verification failed"; exit 1
            }
            Write-Log "SEMANTICS_EPOCH $old -> $BumpEpoch (verified)"
        }
    }
}

if ($DryRun) { Write-Log "[dry] plan: stop guardian -> stop server -> backup+swap jar -> start -> verify"; exit 0 }

# ---------- 3) stop guardian ----------
$lock = Join-Path $ServerDir "guardian\guardian.lock"
if (Test-Path $lock) {
    $gpid = 0
    try { $gpid = [int](Get-Content $lock -Raw).Trim() } catch { $gpid = 0 }
    if ($gpid -gt 0) {
        $proc = Get-CimInstance Win32_Process -Filter "ProcessId=$gpid" -ErrorAction SilentlyContinue
        if ($proc -and $proc.CommandLine -match 'guardian\.ps1') {
            Write-Log "stopping guardian pid=$gpid"
            Stop-Process -Id $gpid -Force -ErrorAction SilentlyContinue
            Start-Sleep -Seconds 2
        } else {
            Write-Log "lock pid=$gpid is not a guardian (skip); removing stale lock"
            Remove-Item $lock -Force -ErrorAction SilentlyContinue
        }
    }
}

# ---------- 4) stop server (graceful, then force if needed) ----------
Write-Log "stopping server (graceful RCON save+stop)"
Push-Location $RepoDir
try {
    & python (Join-Path $RepoDir "tools\stop_server.py") --server-dir "$ServerDir" 2>&1 |
        ForEach-Object { Write-Log "  | $_" }
} catch { Write-Log "stop_server.py failed: $($_.Exception.Message)" } finally { Pop-Location }

for ($i = 0; $i -lt 30; $i++) {
    if (-not (Test-Rcon)) { break }
    Start-Sleep -Seconds 2
}
if (Test-Rcon) {
    Write-Log "server still listening after 60s -> force kill java"
    Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
    Start-Sleep -Seconds 5
} else {
    Write-Log "server stopped"
}

# ---------- 5) swap jar ----------
$stamp = Get-Date -Format "yyyyMMdd_HHmmss"
if (Test-Path $Target) {
    $bak = "$Target.bak_deploy_$stamp"
    Move-Item -Path $Target -Destination $bak -Force
    Write-Log "previous jar backed up -> $(Split-Path $bak -Leaf)"
}
Copy-Item -Path $JarPath -Destination $Target -Force
$newSize = (Get-Item $Target).Length
if ($newSize -ne $jarSize) { Write-Log "ERROR: deployed size $newSize != source $jarSize"; exit 1 }
Write-Log "jar deployed ($newSize B)"

# ---------- 6) start server ----------
Push-Location $ServerDir
$p = Start-Process -FilePath "cmd.exe" -ArgumentList "/c", "`"E:\Program Files\Java\jdk-17\bin\java.exe`" @user_jvm_args.txt @libraries/net/minecraftforge/forge/1.20.1-47.4.13/win_args.txt nogui" -WindowStyle Hidden -PassThru
Pop-Location
Write-Log "server starting pid=$($p.Id)"

$ready = $false
for ($i = 0; $i -lt 30; $i++) {
    Start-Sleep -Seconds 10
    if (Test-Rcon) { $ready = $true; break }
}
if (-not $ready) { Write-Log "ERROR: server not ready after 300s"; exit 1 }
Write-Log "server ready on port $RconPort"

# ---------- 7) start guardian ----------
Start-Sleep -Seconds 10
$g = Start-Process -FilePath "powershell.exe" -ArgumentList @(
    "-ExecutionPolicy", "Bypass", "-File", (Join-Path $RepoDir "tools\guardian.ps1"),
    "-ServerDir", "`"$ServerDir`"",
    "-RconPort", "$RconPort",
    "-HeartbeatTimeoutSec", "120",
    "-AgentSessionId", "`"$AgentSessionId`""
) -WindowStyle Hidden -PassThru
Write-Log "guardian started pid=$($g.Id)"

# ---------- 8) verify ----------
Start-Sleep -Seconds 20
$latest = Join-Path $ServerDir "logs\latest.log"
Write-Log "--- verify: self-check / model / contract ---"
if (Test-Path $latest) {
    $tail = Get-Content $latest -Tail 400 | Where-Object { $_ -match 'SELFCHECK|model loaded|model OK|failed to load model|RL\] contract' }
    if ($tail) { $tail | Select-Object -Last 12 | ForEach-Object { Write-Log "  | $_" } }
    else { Write-Log "  (no SELFCHECK/model line in last 400 lines yet)" }
}
Write-Log "=== deploy done ==="
