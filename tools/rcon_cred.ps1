# RCON credential resolution (P0 fix 2026-09-10). ASCII-only on purpose:
# this file may be dot-sourced by Windows PowerShell 5.1, which mis-decodes
# UTF-8 files without BOM.
#
# Priority: explicit parameter > $env:EFTLM_RCON_PASSWORD > <ScriptRoot>/rcon_password.txt
# Returns "" when nothing is configured; callers must fail closed.
function Resolve-RconPassword {
    [CmdletBinding()]
    param(
        [string]$Explicit = "",
        [string]$ScriptRoot = ""
    )
    if (-not [string]::IsNullOrWhiteSpace($Explicit)) { return $Explicit.Trim() }
    if (-not [string]::IsNullOrWhiteSpace($env:EFTLM_RCON_PASSWORD)) { return $env:EFTLM_RCON_PASSWORD.Trim() }
    if ([string]::IsNullOrWhiteSpace($ScriptRoot)) { $ScriptRoot = $PSScriptRoot }
    if (-not [string]::IsNullOrWhiteSpace($ScriptRoot)) {
        $file = Join-Path $ScriptRoot "rcon_password.txt"
        if (Test-Path $file) {
            $value = Get-Content $file -Raw -ErrorAction SilentlyContinue
            if ($null -ne $value -and -not [string]::IsNullOrWhiteSpace($value)) { return $value.Trim() }
        }
    }
    return ""
}
