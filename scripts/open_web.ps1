param(
  [string]$Relative = "web/public/index.html"
)
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$Path = Join-Path $ProjectRoot $Relative
Start-Process $Path
