param(
  [string]$AppId = "com.example.edgeview",
  [string]$Output = "web/public/frame.png"
)

$ErrorActionPreference = 'Stop'

# Resolve project root as parent of this script directory
$ProjectRoot = Split-Path -Parent $PSScriptRoot

# Ensure output directory exists
$OutPath = Join-Path $ProjectRoot $Output
$OutDir = Split-Path -Parent $OutPath
if (!(Test-Path $OutDir)) { New-Item -ItemType Directory -Path $OutDir | Out-Null }

Write-Host "Pulling latest processed frame from $AppId to $OutPath"

# Use adb with run-as to read the file from app-internal storage
# Requires a debuggable build of the app installed on a connected device
& adb exec-out run-as $AppId cat files/edgeview_frame.png > $OutPath

if ($LASTEXITCODE -ne 0) {
  Write-Error "Failed to pull frame. Ensure device connected, app installed (debuggable), and a frame was saved."
} else {
  Write-Host "Saved to $OutPath"
}
