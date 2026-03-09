# 后端服务停止脚本
$ErrorActionPreference = "Continue"

$PID_FILE = "C:\inetpub\wwwroot\music-ticket-backend\app.pid"

if (-not (Test-Path $PID_FILE)) {
    Write-Host "未找到 PID 文件，服务可能未运行"
    exit 0
}

$pid = Get-Content $PID_FILE
$process = Get-Process -Id $pid -ErrorAction SilentlyContinue

if ($process) {
    Write-Host "停止后端服务 (PID: $pid)..."
    Stop-Process -Id $pid -Force
    Start-Sleep -Seconds 2

    # 确认进程已停止
    $process = Get-Process -Id $pid -ErrorAction SilentlyContinue
    if (-not $process) {
        Write-Host "后端服务已停止"
        Remove-Item $PID_FILE -Force
    } else {
        Write-Host "警告: 进程仍在运行"
    }
} else {
    Write-Host "进程 $pid 不存在"
    Remove-Item $PID_FILE -Force
}
