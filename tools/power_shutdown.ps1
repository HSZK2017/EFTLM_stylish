<#
.SYNOPSIS
EFTLM daily power shutdown (dormitory blackout at 23:25).
Scheduled via schtasks at 23:15 daily: gracefully save & stop server,
then kill guardian/iterate so nothing restarts before power loss.

.NOTES
Order matters: RCON stop first (graceful world save), then kill guardian
(kills its restart loop), then wait for java to exit naturally.
#>
param(
    [string]$ServerDir = "E:\program\JAVA\touhou little maid - unknow sky area\prod_server",
    [int]$RconPort = 25575,
    [string]$RconPassword = ""
)

. (Join-Path $PSScriptRoot "rcon_cred.ps1")
$RconPassword = Resolve-RconPassword -Explicit $RconPassword -ScriptRoot $PSScriptRoot
if ([string]::IsNullOrWhiteSpace($RconPassword)) {
    Write-Host "FATAL: RCON password not configured (env EFTLM_RCON_PASSWORD or tools/rcon_password.txt)"
    exit 1
}

$LogFile = Join-Path $ServerDir "guardian\power.log"
function Write-Log($msg) {
    $line = "[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] $msg"
    Add-Content -Path $LogFile -Value $line -Encoding UTF8
}

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
            while ($read -lt 12) { $n = $ns.Read($hdr, $read, 12 - $read); if ($n -le 0) { return "" }; $read += $n }
            $len = [BitConverter]::ToInt32($hdr, 0)
            $body = New-Object byte[] ($len - 8)
            $read = 0
            while ($read -lt $body.Length) { $n = $ns.Read($body, $read, $body.Length - $read); if ($n -le 0) { break }; $read += $n }
            return [System.Text.Encoding]::UTF8.GetString($body, 0, $body.Length).TrimEnd([char]0)
        }
        $ns.Write((Pkt 3 $RconPassword), 0, (Pkt 3 $RconPassword).Length); ReadPkt $ns | Out-Null
        $ns.Write((Pkt 2 $cmd), 0, (Pkt 2 $cmd).Length)
        $resp = ReadPkt $ns
        $s.Close()
        return $resp
    } catch {
        return $null
    }
}

Write-Log "=== power shutdown start ==="

# 1) graceful stop (saves world/chunks)
$r = Send-Rcon "stop"
Write-Log "RCON stop sent: $r"

# 2) kill guardian processes (prevent auto-restart while shutting down)
Start-Sleep -Seconds 2
Get-CimInstance Win32_Process -Filter "Name='powershell.exe' OR Name='pwsh.exe'" |
    Where-Object { $_.CommandLine -match 'guardian\.ps1' } |
    ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue; Write-Log "guardian killed pid=$($_.ProcessId)" }

# 3) kill iterate (training pipeline)
Get-CimInstance Win32_Process -Filter "Name='python.exe'" |
    Where-Object { $_.CommandLine -match 'iterate\.py' } |
    ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue; Write-Log "iterate killed pid=$($_.ProcessId)" }

# 4) wait for java to exit gracefully (up to 90s), force kill if stuck
$deadline = (Get-Date).AddSeconds(90)
while ((Get-Date) -lt $deadline) {
    if (-not (Get-Process java -ErrorAction SilentlyContinue)) { break }
    Start-Sleep -Seconds 5
}
if (Get-Process java -ErrorAction SilentlyContinue) {
    Write-Log "java did not exit in 90s, force killing"
    Get-Process java | Stop-Process -Force
    Start-Sleep -Seconds 2
}

$javaLeft = @(Get-Process java -ErrorAction SilentlyContinue).Count
Write-Log "shutdown complete: java=$javaLeft guardian=0 iterate=0 (blackout-safe at $(Get-Date -Format 'HH:mm'))"
