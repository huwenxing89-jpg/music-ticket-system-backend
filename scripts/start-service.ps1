# 后端服务启动脚本
$ErrorActionPreference = "Stop"

$JAR_FILE = "C:\inetpub\wwwroot\music-ticket-backend\app.jar"
$LOG_DIR = "C:\inetpub\wwwroot\music-ticket-backend\logs"
$LOG_FILE = "$LOG_DIR\backend.log"
$PID_FILE = "C:\inetpub\wwwroot\music-ticket-backend\app.pid"

# 确保日志目录存在
if (-not (Test-Path $LOG_DIR)) {
    New-Item -ItemType Directory -Path $LOG_DIR -Force | Out-Null
}

# 停止现有服务
if (Test-Path $PID_FILE) {
    $pid = Get-Content $PID_FILE
    $process = Get-Process -Id $pid -ErrorAction SilentlyContinue
    if ($process) {
        Write-Host "停止现有服务 (PID: $pid)"
        Stop-Process -Id $pid -Force
        Start-Sleep -Seconds 2
    }
    Remove-Item $PID_FILE -Force
}

# 启动新服务
Write-Host "启动后端服务..."
$arguments = @(
    "-jar"
    $JAR_FILE
    "--spring.profiles.active=prod"
)

# 启动进程并保存 PID
$process = Start-Process -FilePath "java" -ArgumentList $arguments -NoNewWindow -PassThru -RedirectStandardOutput $LOG_FILE -RedirectStandardError "$LOG_FILE\error.log"

# 保存 PID
$process.Id | Out-File -FilePath $PID_FILE -Force

Write-Host "后端服务已启动 (PID: $($process.Id))"
Write-Host "日志文件: $LOG_FILE"
