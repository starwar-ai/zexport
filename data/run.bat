@echo off
chcp 65001 >nul
echo ========================================
echo   EPlus Data Analytics Dashboard
echo ========================================
echo.

cd /d %~dp0

echo 检查 Python 环境...
python --version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未找到 Python，请先安装 Python 3.10+
    pause
    exit /b 1
)

echo 检查依赖是否已安装...
python -c "import streamlit" >nul 2>&1
if errorlevel 1 (
    echo [提示] 首次运行，正在安装依赖...
    pip install -r requirements.txt
    if errorlevel 1 (
        echo [错误] 依赖安装失败
        pause
        exit /b 1
    )
)

echo.
echo 启动 Dashboard...
echo 访问地址: http://localhost:8501
echo 按 Ctrl+C 停止服务
echo.
streamlit run app.py --server.port 8501 --server.headless true

pause
