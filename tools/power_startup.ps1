<#
.SYNOPSIS
EFTLM startup after power restore / login (dormitory power returns ~06:00).
Triggered via schtasks ONLOGON, or manually. Starts server, guardian, iterate
if not already running.

.NOTES
Requires the machine to be on & logged in. For fully automatic recovery:
 1) BIOS: set "AC Power Loss / Restore on AC Power" = Power On (if supported)
 2) Windows auto-logon (netplwiz) so ONLOGON task fires right after boot
Then the whole pipeline restarts by itself after power returns.
#>
param(
    [string]$ServerDir = "E:\program\JAVA\touhou little maid - unknow sky area\prod_server",
    [int]$RconPort = 25575,
    [string]$AgentSessionId = "session-013a71d2-5c4f-4f4e-b126-0d447596db21",
    # Iteration window (hours). Default 2.5 so a round always finishes before the 23:15
    # graceful shutdown / 23:30 dorm power cut (a 6h round would fire at 23:47 and never run).
    [double]$Hours = 2.5
)

$LogFile = Join-Path $ServerDir "guardian\power.log"
function Write-Log($msg) {
    $line = "[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] $msg"
    Add-Content -Path $LogFile -Value $line -Encoding UTF8
}

Write-Log "=== power startup start ==="

# already running? (RCON port listening = server up)
$listening = Get-NetTCPConnection -LocalPort $RconPort -State Listen -ErrorAction SilentlyContinue
if ($listening) {
    Write-Log "server already running (port $RconPort), startup skipped"
    exit 0
}

# any leftover java? kill first (safety)
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 3

# 1) start server (detached)
Push-Location $ServerDir
$p = Start-Process -FilePath "cmd.exe" -ArgumentList "/c", "`"E:\Program Files\Java\jdk-17\bin\java.exe`" @user_jvm_args.txt @libraries/net/minecraftforge/forge/1.20.1-47.4.13/win_args.txt nogui" -WindowStyle Hidden -PassThru
Pop-Location
Write-Log "server starting pid=$($p.Id)"

# 2) wait for RCON (up to 240s)
$ready = $false
for ($i = 0; $i -lt 24; $i++) {
    Start-Sleep -Seconds 10
    if (Get-NetTCPConnection -LocalPort $RconPort -State Listen -ErrorAction SilentlyContinue) {
        $ready = $true
        break
    }
}
if (-not $ready) {
    Write-Log "ERROR: server not ready after 240s, aborting startup"
    exit 1
}
Write-Log "server ready on port $RconPort"

# 3) start guardian (detached)
Start-Sleep -Seconds 10
$g = Start-Process -FilePath "powershell.exe" -ArgumentList @(
    "-ExecutionPolicy", "Bypass", "-File",
    "E:\program\JAVA\EFTLM-example\tools\guardian.ps1",
    "-ServerDir", "`"$ServerDir`"",
    "-RconPort", "$RconPort",
    "-HeartbeatTimeoutSec", "120",
    "-AgentSessionId", "`"$AgentSessionId`""
) -WindowStyle Hidden -PassThru
Write-Log "guardian started pid=$($g.Id)"

# 4) start iterate (detached, output redirected to file for troubleshooting)
Start-Sleep -Seconds 5
$itLog = "E:\program\JAVA\EFTLM-example\train\models\iterate_auto.log"
$it = Start-Process -FilePath "python.exe" -ArgumentList @(
    "E:\program\JAVA\EFTLM-example\train\iterate.py",
    "--server-dir", "`"$ServerDir`"",
    "--project-dir", "`"E:\program\JAVA\EFTLM-example`"",
    "--anchor", "server",
    "--hours", "$Hours",
    "--agent-session", "`"$AgentSessionId`""
) -WorkingDirectory "E:\program\JAVA\EFTLM-example" -WindowStyle Hidden -PassThru -RedirectStandardOutput $itLog -RedirectStandardError "$itLog.err"
Write-Log "iterate started pid=$($it.Id) (log: $itLog)"

Write-Log "power startup complete: server+guardian+iterate all running"
