# EPlus Quick Deploy Script (PowerShell)
# Usage: .\deploy-quick.ps1 [-SkipBuild] [-ServerOnly] [-ClientOnly] [-FrontendOnly] [-BackendOnly]
# This script only compiles, uploads, and restarts services (no nginx configuration)

param(
    [switch]$SkipBuild,
    [switch]$ServerOnly,
    [switch]$ClientOnly,
    [switch]$FrontendOnly,
    [switch]$BackendOnly
)

# ========== Config ==========
$SERVER_IP = "192.168.10.25"
$SERVER_USER = "root"
$SERVER_PASS = "wykj.123.vk"
$DEPLOY_DIR = "/root/erp"

$FRONTEND_DIR = "eplus-admin-ui-elementplus"
$BACKEND_DIR = "eplus-admin-server"

# Server paths
$BACKEND_DEPLOY_DIR = "$DEPLOY_DIR/backend"
$FRONTEND_DEPLOY_DIR = "$DEPLOY_DIR/frontend"

# ========== Process Parameters ==========
# Normalize aliases
if ($ServerOnly) { $BackendOnly = $true }
if ($ClientOnly) { $FrontendOnly = $true }

# Validate parameter combinations
if ($FrontendOnly -and $BackendOnly) {
    Write-Err "Cannot use FrontendOnly/ClientOnly and BackendOnly/ServerOnly together"
    exit 1
}

# ========== Output Functions ==========
function Write-Info { param($msg) Write-Host "[INFO] $msg" -ForegroundColor Cyan }
function Write-OK { param($msg) Write-Host "[OK] $msg" -ForegroundColor Green }
function Write-Err { param($msg) Write-Host "[ERROR] $msg" -ForegroundColor Red }
function Write-Step { param($msg) Write-Host "`n========== $msg ==========" -ForegroundColor Yellow }

# ========== Check Tools ==========
Write-Step "Check Tools"

$hasOpenSSH = (Get-Command ssh -ErrorAction SilentlyContinue) -and (Get-Command scp -ErrorAction SilentlyContinue)

if (-not $hasOpenSSH) {
    Write-Err "OpenSSH client not found"
    Write-Host "Windows 10/11: Settings -> Apps -> Optional Features -> Add Feature -> OpenSSH Client" -ForegroundColor Yellow
    exit 1
}

Write-Info "Using OpenSSH"

# ========== Build Frontend ==========
if (-not $SkipBuild -and -not $BackendOnly) {
    Write-Step "Build Frontend"

    Push-Location $FRONTEND_DIR

    if (-not (Get-Command pnpm -ErrorAction SilentlyContinue)) {
        Write-Err "pnpm not found, install: npm install -g pnpm"
        Pop-Location
        exit 1
    }

    Write-Info "Running pnpm install..."
    pnpm install
    if ($LASTEXITCODE -ne 0) {
        Write-Err "pnpm install failed"
        Pop-Location
        exit 1
    }

    Write-Info "Running pnpm build:pro..."
    pnpm build:pro
    if ($LASTEXITCODE -ne 0) {
        Write-Err "Frontend build failed"
        Pop-Location
        exit 1
    }

    Write-OK "Frontend build complete"
    Pop-Location
}

# ========== Build Backend ==========
if (-not $SkipBuild -and -not $FrontendOnly) {
    Write-Step "Build Backend"

    Push-Location $BACKEND_DIR

    $mvnCmd = $null
    if (Get-Command mvn -ErrorAction SilentlyContinue) {
        $mvnCmd = "mvn"
    } elseif (Test-Path ".\mvnw.cmd") {
        $mvnCmd = ".\mvnw.cmd"
    } elseif (Test-Path ".\mvnw") {
        $mvnCmd = ".\mvnw"
    }

    if (-not $mvnCmd) {
        Write-Err "Maven not found"
        Pop-Location
        exit 1
    }

    Write-Info "Running $mvnCmd clean package -DskipTests..."
    & $mvnCmd clean package "-DskipTests" "-Dmaven.test.skip=true"
    if ($LASTEXITCODE -ne 0) {
        Write-Err "Backend build failed"
        Pop-Location
        exit 1
    }

    Write-OK "Backend build complete"
    Pop-Location
}

# ========== Prepare Files ==========
Write-Step "Prepare Files"

$tempDir = ".\deploy-temp"
if (Test-Path $tempDir) {
    Remove-Item -Recurse -Force $tempDir
}
New-Item -ItemType Directory -Path $tempDir | Out-Null

# Copy frontend
if (-not $BackendOnly) {
    if (Test-Path "$FRONTEND_DIR\dist") {
        Write-Info "Copy frontend files..."
        Copy-Item -Recurse "$FRONTEND_DIR\dist" "$tempDir\frontend"
    } else {
        Write-Err "Frontend dist not found: $FRONTEND_DIR\dist"
        exit 1
    }
}

# Copy backend
if (-not $FrontendOnly) {
    if (Test-Path "$BACKEND_DIR\yudao-server\target\yudao-server.jar") {
        Write-Info "Copy backend JAR..."
        New-Item -ItemType Directory -Path "$tempDir\backend" -Force | Out-Null
        Copy-Item "$BACKEND_DIR\yudao-server\target\yudao-server.jar" "$tempDir\backend\"
    } else {
        Write-Err "Backend JAR not found: $BACKEND_DIR\yudao-server\target\yudao-server.jar"
        exit 1
    }
}

# ========== Upload to Server ==========
Write-Step "Upload to Server"

Write-Info "Using OpenSSH..."
Write-Host "Password: $SERVER_PASS" -ForegroundColor Magenta

$sshTarget = "$SERVER_USER@$SERVER_IP"

Write-Info "Create remote directories..."
ssh $sshTarget "mkdir -p $DEPLOY_DIR/backend $DEPLOY_DIR/frontend $DEPLOY_DIR/logs"

# Upload backend
if (-not $FrontendOnly) {
    Write-Info "Upload backend JAR..."
    scp "$tempDir/backend/yudao-server.jar" "${sshTarget}:${BACKEND_DEPLOY_DIR}/"
    if ($LASTEXITCODE -ne 0) {
        Write-Err "Backend upload failed"
        exit 1
    }
}

# Upload frontend
if (-not $BackendOnly) {
    Write-Info "Upload frontend files..."
    scp -r "$tempDir/frontend/*" "${sshTarget}:${FRONTEND_DEPLOY_DIR}/"
    if ($LASTEXITCODE -ne 0) {
        Write-Err "Frontend upload failed"
        exit 1
    }
}

Write-OK "Upload complete"

# ========== Restart Services ==========
Write-Step "Restart Services"

$restartScript = @'
#!/bin/bash
set -e

DEPLOY_DIR="/root/erp"
BACKEND_DIR="$DEPLOY_DIR/backend"
LOG_DIR="$DEPLOY_DIR/logs"

# Stop existing backend service
if [ -f "$BACKEND_DIR/yudao-server.jar" ]; then
    echo "Stopping existing backend service..."
    pkill -f "yudao-server.jar" 2>/dev/null || true
    sleep 2

    echo "Starting backend service..."
    cd $BACKEND_DIR
    
    # Start Java application in background
    nohup java -jar \
        -Xms2g \
        -Xmx8g \
        -Dspring.profiles.active=prod \
        yudao-server.jar \
        > $LOG_DIR/backend.log 2>&1 &
    
    echo "Backend PID: $!"
    echo $! > $BACKEND_DIR/app.pid
    
    echo "Waiting for backend to start..."
    sleep 5
    
    if ps -p $(cat $BACKEND_DIR/app.pid) > /dev/null 2>&1; then
        echo "Backend started successfully"
    else
        echo "Backend failed to start. Check logs at $LOG_DIR/backend.log"
        exit 1
    fi
fi

echo ""
echo "========================================="
echo "Deploy Complete!"
echo "========================================="
echo "Frontend: http://$(hostname -I | awk '{print $1}'):18030"
echo "Backend:  http://$(hostname -I | awk '{print $1}'):18031"
echo "Backend logs: $LOG_DIR/backend.log"
echo ""
echo "Check backend status: ps aux | grep yudao-server"
echo "View backend logs: tail -f $LOG_DIR/backend.log"
echo "========================================="
'@

Write-Host "Password: $SERVER_PASS" -ForegroundColor Magenta
$restartScript -replace "`r", "" | ssh $sshTarget "bash -s"

if ($LASTEXITCODE -ne 0) {
    Write-Err "Restart failed"
    exit 1
}

# ========== Cleanup ==========
Write-Step "Cleanup"
Remove-Item -Recurse -Force $tempDir
Write-OK "Cleanup complete"

# ========== Done ==========
Write-Step "Deploy Complete"

if ($FrontendOnly) {
    Write-OK "Frontend deployed to http://${SERVER_IP}:18030"
    Write-Host ""
    Write-Host "Frontend: http://${SERVER_IP}:18030" -ForegroundColor Green
} elseif ($BackendOnly) {
    Write-OK "Backend deployed to http://${SERVER_IP}:18031"
    Write-Host ""
    Write-Host "Backend:  http://${SERVER_IP}:18031" -ForegroundColor Green
} else {
    Write-OK "App deployed to http://${SERVER_IP}:18030"
    Write-Host ""
    Write-Host "Frontend: http://${SERVER_IP}:18030" -ForegroundColor Green
    Write-Host "Backend:  http://${SERVER_IP}:18031" -ForegroundColor Green
}
Write-Host ""
Write-Host "Useful commands:" -ForegroundColor Yellow
Write-Host "  Check backend:  ssh $SERVER_USER@$SERVER_IP 'ps aux | grep yudao-server'" -ForegroundColor Gray
Write-Host "  View logs:      ssh $SERVER_USER@$SERVER_IP 'tail -f $DEPLOY_DIR/logs/backend.log'" -ForegroundColor Gray
Write-Host "  Stop backend:   ssh $SERVER_USER@$SERVER_IP 'pkill -f yudao-server.jar'" -ForegroundColor Gray
Write-Host ""
