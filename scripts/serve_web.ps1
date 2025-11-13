param(
  [string]$Port = "5173"
)
$ErrorActionPreference = 'Stop'
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$WebRoot = Join-Path $ProjectRoot "web"

Write-Host "Starting http-server on http://localhost:$Port serving $WebRoot/public"
Start-Process -FilePath "pwsh" -ArgumentList "-NoProfile","-Command","Set-Location '$WebRoot'; npx http-server ./public -p $Port --cors" | Out-Null
Start-Sleep -Seconds 1
Start-Process "http://localhost:$Port"
