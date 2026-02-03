#!/bin/bash

# 路径设置脚本
# 作者：波波
# 日期：2024-01-01
# 描述：设置MySQL备份系统的目录结构

# ==================== 配置参数 ====================
SCRIPT_DIR="/home/db/script"
DATA_DIR="/home/db/data"
LOG_DIR="/var/log"

# ==================== 颜色输出 ====================
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# ==================== 检查函数 ====================
check_root() {
    if [ "$EUID" -ne 0 ]; then
        print_error "请使用root权限运行此脚本"
        exit 1
    fi
}

# ==================== 创建目录 ====================
create_directories() {
    print_step "创建目录结构..."
    
    # 创建脚本目录
    if [ ! -d "$SCRIPT_DIR" ]; then
        mkdir -p "$SCRIPT_DIR"
        print_info "创建脚本目录: $SCRIPT_DIR"
    else
        print_info "脚本目录已存在: $SCRIPT_DIR"
    fi
    
    # 创建数据目录
    if [ ! -d "$DATA_DIR" ]; then
        mkdir -p "$DATA_DIR"
        print_info "创建数据目录: $DATA_DIR"
    else
        print_info "数据目录已存在: $DATA_DIR"
    fi
    
    # 创建日志目录（如果不存在）
    if [ ! -d "$LOG_DIR" ]; then
        mkdir -p "$LOG_DIR"
        print_info "创建日志目录: $LOG_DIR"
    else
        print_info "日志目录已存在: $LOG_DIR"
    fi
}

# ==================== 设置权限 ====================
set_permissions() {
    print_step "设置目录权限..."
    
    # 设置脚本目录权限
    chmod 755 "$SCRIPT_DIR"
    print_info "设置脚本目录权限: 755"
    
    # 设置数据目录权限
    chmod 755 "$DATA_DIR"
    print_info "设置数据目录权限: 755"
    
    # 尝试设置所有者（如果mysql用户存在）
    if id "mysql" &>/dev/null; then
        chown mysql:mysql "$SCRIPT_DIR" 2>/dev/null || chown root:root "$SCRIPT_DIR"
        chown mysql:mysql "$DATA_DIR" 2>/dev/null || chown root:root "$DATA_DIR"
        print_info "设置目录所有者: mysql"
    else
        chown root:root "$SCRIPT_DIR"
        chown root:root "$DATA_DIR"
        print_info "设置目录所有者: root"
    fi
}

# ==================== 复制脚本 ====================
copy_scripts() {
    print_step "复制脚本文件..."
    
    # 复制备份脚本
    if [ -f "mysql_backup.sh" ]; then
        cp mysql_backup.sh "$SCRIPT_DIR/"
        chmod +x "$SCRIPT_DIR/mysql_backup.sh"
        print_info "复制备份脚本: $SCRIPT_DIR/mysql_backup.sh"
    else
        print_error "备份脚本不存在: mysql_backup.sh"
        return 1
    fi
    
    # 复制部署脚本
    if [ -f "deploy_foreign_trade_backup.sh" ]; then
        cp deploy_foreign_trade_backup.sh "$SCRIPT_DIR/"
        chmod +x "$SCRIPT_DIR/deploy_foreign_trade_backup.sh"
        print_info "复制部署脚本: $SCRIPT_DIR/deploy_foreign_trade_backup.sh"
    fi
    
    # 复制其他脚本（如果有的话）
    for script in *.sh; do
        if [ -f "$script" ] && [ "$script" != "setup_paths.sh" ]; then
            cp "$script" "$SCRIPT_DIR/"
            chmod +x "$SCRIPT_DIR/$script"
            print_info "复制脚本: $SCRIPT_DIR/$script"
        fi
    done
}

# ==================== 创建日志文件 ====================
create_log_files() {
    print_step "创建日志文件..."
    
    # 创建备份日志文件
    touch "$LOG_DIR/mysql_backup.log"
    chmod 644 "$LOG_DIR/mysql_backup.log"
    print_info "创建备份日志文件: $LOG_DIR/mysql_backup.log"
    
    # 创建cron日志文件
    touch "$LOG_DIR/mysql_backup_cron.log"
    chmod 644 "$LOG_DIR/mysql_backup_cron.log"
    print_info "创建cron日志文件: $LOG_DIR/mysql_backup_cron.log"
    
    # 设置日志文件所有者
    if id "mysql" &>/dev/null; then
        chown mysql:mysql "$LOG_DIR/mysql_backup.log" 2>/dev/null || chown root:root "$LOG_DIR/mysql_backup.log"
        chown mysql:mysql "$LOG_DIR/mysql_backup_cron.log" 2>/dev/null || chown root:root "$LOG_DIR/mysql_backup_cron.log"
    else
        chown root:root "$LOG_DIR/mysql_backup.log"
        chown root:root "$LOG_DIR/mysql_backup_cron.log"
    fi
}

# ==================== 显示状态 ====================
show_status() {
    print_step "显示目录状态..."
    
    echo "=========================================="
    echo "        目录结构状态"
    echo "=========================================="
    
    # 检查脚本目录
    if [ -d "$SCRIPT_DIR" ]; then
        echo "✓ 脚本目录: $SCRIPT_DIR"
        echo "  权限: $(ls -ld "$SCRIPT_DIR" | awk '{print $1, $3, $4}')"
        echo "  文件数量: $(ls -1 "$SCRIPT_DIR"/*.sh 2>/dev/null | wc -l)"
    else
        echo "✗ 脚本目录不存在: $SCRIPT_DIR"
    fi
    
    # 检查数据目录
    if [ -d "$DATA_DIR" ]; then
        echo "✓ 数据目录: $DATA_DIR"
        echo "  权限: $(ls -ld "$DATA_DIR" | awk '{print $1, $3, $4}')"
        echo "  大小: $(du -sh "$DATA_DIR" 2>/dev/null | cut -f1)"
    else
        echo "✗ 数据目录不存在: $DATA_DIR"
    fi
    
    # 检查日志文件
    if [ -f "$LOG_DIR/mysql_backup.log" ]; then
        echo "✓ 备份日志: $LOG_DIR/mysql_backup.log"
        echo "  大小: $(du -h "$LOG_DIR/mysql_backup.log" 2>/dev/null | cut -f1)"
    else
        echo "✗ 备份日志不存在: $LOG_DIR/mysql_backup.log"
    fi
    
    echo "=========================================="
}

# ==================== 主函数 ====================
main() {
    echo "=========================================="
    echo "    MySQL备份系统路径设置工具"
    echo "=========================================="
    echo "配置信息："
    echo "- 脚本目录: $SCRIPT_DIR"
    echo "- 数据目录: $DATA_DIR"
    echo "- 日志目录: $LOG_DIR"
    echo "=========================================="
    
    check_root
    create_directories
    set_permissions
    copy_scripts
    create_log_files
    show_status
    
    echo
    print_info "路径设置完成！"
    echo
    echo "下一步操作："
    echo "1. 运行部署脚本: sudo $SCRIPT_DIR/deploy_foreign_trade_backup.sh"
    echo "2. 或者手动配置MySQL密码"
    echo "3. 测试备份功能"
    echo
}

# ==================== 脚本执行 ====================
if [ "$1" = "status" ]; then
    show_status
elif [ "$1" = "setup" ]; then
    main
else
    main
fi 