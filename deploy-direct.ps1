# EPlus Direct Deploy Script (PowerShell)
# Usage: .\deploy-direct.ps1 [-SkipBuild] [-FrontendOnly] [-BackendOnly]

param(
    [switch]$SkipBuild,
    [switch]$FrontendOnly,
    [switch]$BackendOnly
)

# ========== Config ==========
$SERVER_IP = "192.168.10.25"
$SERVER_USER = "root"
$SERVER_PASS = "Wykj.123"
$DEPLOY_DIR = "/root/erp"

$FRONTEND_DIR = "eplus-admin-ui-elementplus"
$BACKEND_DIR = "eplus-admin-server"

# Server paths
$BACKEND_DEPLOY_DIR = "$DEPLOY_DIR/backend"
$FRONTEND_DEPLOY_DIR = "$DEPLOY_DIR/frontend"
$NGINX_AVAILABLE = "/etc/nginx/sites-available"
$NGINX_ENABLED = "/etc/nginx/sites-enabled"

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

# Copy Nginx config
if (-not $BackendOnly) {
    Write-Info "Copy Nginx configuration..."
    Copy-Item ".\nginx-direct.conf" "$tempDir\nginx.conf"
}

# ========== Upload to Server ==========
Write-Step "Upload to Server"

Write-Info "Using OpenSSH..."
Write-Host "Password: $SERVER_PASS" -ForegroundColor Magenta

$sshTarget = "$SERVER_USER@$SERVER_IP"

Write-Info "Create remote directories..."
ssh $sshTarget "mkdir -p $DEPLOY_DIR/backend $DEPLOY_DIR/frontend $DEPLOY_DIR/logs $DEPLOY_DIR/config"

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
    
    Write-Info "Upload Nginx configuration..."
    scp "$tempDir/nginx.conf" "${sshTarget}:${DEPLOY_DIR}/config/"
    if ($LASTEXITCODE -ne 0) {
        Write-Err "Nginx config upload failed"
        exit 1
    }
}

Write-OK "Upload complete"

# ========== Deploy on Server ==========
Write-Step "Deploy on Server"

$deployScript = @'
#!/bin/bash
set -e

DEPLOY_DIR="/root/erp"
BACKEND_DIR="$DEPLOY_DIR/backend"
FRONTEND_DIR="$DEPLOY_DIR/frontend"
LOG_DIR="$DEPLOY_DIR/logs"

# Stop existing backend service
echo "Stopping existing backend service..."
pkill -f "yudao-server.jar" 2>/dev/null || true
sleep 2

# Start backend service
if [ -f "$BACKEND_DIR/yudao-server.jar" ]; then
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

# Configure Nginx for frontend
if [ -d "$FRONTEND_DIR" ]; then
    echo "Configuring Nginx..."
    
    # Detect Nginx config style (Debian/Ubuntu vs CentOS/RHEL)
    if [ -d "/etc/nginx/sites-available" ]; then
        # Debian/Ubuntu style
        NGINX_CONF="/etc/nginx/sites-available/eplus"
        cp $DEPLOY_DIR/config/nginx.conf $NGINX_CONF
        ln -sf $NGINX_CONF /etc/nginx/sites-enabled/eplus
    elif [ -d "/etc/nginx/conf.d" ]; then
        # CentOS/RHEL style
        NGINX_CONF="/etc/nginx/conf.d/eplus.conf"
        cp $DEPLOY_DIR/config/nginx.conf $NGINX_CONF
    else
        echo "Unknown Nginx configuration structure"
        exit 1
    fi
    
    # Test and reload Nginx
    nginx -t
    if [ $? -eq 0 ]; then
        systemctl reload nginx
        echo "Nginx configured and reloaded"
    else
        echo "Nginx configuration test failed"
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
$deployScript -replace "`r", "" | ssh $sshTarget "bash -s"

if ($LASTEXITCODE -ne 0) {
    Write-Err "Deploy failed"
    exit 1
}

# ========== Cleanup ==========
Write-Step "Cleanup"
Remove-Item -Recurse -Force $tempDir
Write-OK "Cleanup complete"

# ========== Done ==========
Write-Step "Deploy Complete"
Write-OK "App deployed to http://${SERVER_IP}:18030"
Write-Host ""
Write-Host "Frontend: http://${SERVER_IP}:18030" -ForegroundColor Green
Write-Host "Backend:  http://${SERVER_IP}:18031" -ForegroundColor Green
Write-Host ""
Write-Host "Useful commands:" -ForegroundColor Yellow
Write-Host "  Check backend: ssh $SERVER_USER@$SERVER_IP 'ps aux | grep yudao-server'" -ForegroundColor Gray
Write-Host "  View logs:     ssh $SERVER_USER@$SERVER_IP 'tail -f $DEPLOY_DIR/logs/backend.log'" -ForegroundColor Gray
Write-Host "  Stop backend:  ssh $SERVER_USER@$SERVER_IP 'pkill -f yudao-server.jar'" -ForegroundColor Gray
Write-Host ""
