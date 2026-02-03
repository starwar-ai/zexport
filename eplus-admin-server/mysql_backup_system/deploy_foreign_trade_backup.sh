#!/bin/bash

# Foreign Trade数据库备份快速部署脚本
# 作者：波波
# 日期：2024-01-01
# 描述：专门用于部署foreign_trade数据库的定时备份

# ==================== 配置参数 ====================
BACKUP_SCRIPT="/home/db/script/mysql_backup.sh"
BACKUP_DIR="/home/db/data"
LOG_DIR="/var/log"
CRON_TIME="0 2"  # 每天凌晨2点执行

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

check_mysql() {
    print_step "检查MySQL环境..."
    
    # 检查MySQL客户端
    if ! command -v mysql &> /dev/null; then
        print_error "MySQL客户端未安装，请先安装MySQL客户端"
        exit 1
    fi
    
    # 检查mysqldump
    if ! command -v mysqldump &> /dev/null; then
        print_error "mysqldump工具未安装"
        exit 1
    fi
    
    # 检查MySQL服务状态
    if systemctl is-active --quiet mysqld 2>/dev/null || systemctl is-active --quiet mysql 2>/dev/null; then
        print_info "MySQL服务正在运行"
    else
        print_warning "MySQL服务未运行，请确保MySQL已启动"
    fi
    
    print_info "MySQL环境检查完成"
}

# ==================== 安装函数 ====================
create_directories() {
    print_step "创建备份目录..."
    
    mkdir -p "$BACKUP_DIR"
    mkdir -p "$LOG_DIR"
    
    if [ $? -eq 0 ]; then
        print_info "目录创建完成"
        print_info "备份目录: $BACKUP_DIR"
        print_info "日志目录: $LOG_DIR"
    else
        print_error "目录创建失败"
        exit 1
    fi
}

install_backup_script() {
    print_step "安装备份脚本..."
    
    # 创建脚本目录
    mkdir -p "/home/db/script"
    
    if [ -f "mysql_backup.sh" ]; then
        cp mysql_backup.sh "$BACKUP_SCRIPT"
        chmod +x "$BACKUP_SCRIPT"
        print_info "备份脚本安装完成: $BACKUP_SCRIPT"
    else
        print_error "备份脚本文件不存在: mysql_backup.sh"
        exit 1
    fi
}

configure_mysql_password() {
    print_step "配置MySQL密码..."
    
    read -s -p "请输入MySQL root密码: " mysql_password
    echo
    
    if [ -z "$mysql_password" ]; then
        print_error "密码不能为空"
        exit 1
    fi
    
    # 更新脚本中的MySQL密码
    sed -i "s/MYSQL_PASSWORD=\"your_password\"/MYSQL_PASSWORD=\"$mysql_password\"/" "$BACKUP_SCRIPT"
    
    print_info "MySQL密码配置完成"
}

test_mysql_connection() {
    print_step "测试MySQL连接..."
    
    # 从脚本中提取MySQL配置
    local mysql_host=$(grep "MYSQL_HOST=" "$BACKUP_SCRIPT" | cut -d'"' -f2)
    local mysql_port=$(grep "MYSQL_PORT=" "$BACKUP_SCRIPT" | cut -d'"' -f2)
    local mysql_user=$(grep "MYSQL_USER=" "$BACKUP_SCRIPT" | cut -d'"' -f2)
    local mysql_password=$(grep "MYSQL_PASSWORD=" "$BACKUP_SCRIPT" | cut -d'"' -f2)
    
    # 测试连接
    if mysql -h"$mysql_host" -P"$mysql_port" -u"$mysql_user" -p"$mysql_password" -e "SELECT 1;" >/dev/null 2>&1; then
        print_info "MySQL连接测试成功"
        
        # 检查foreign_trade数据库是否存在
        if mysql -h"$mysql_host" -P"$mysql_port" -u"$mysql_user" -p"$mysql_password" -e "USE foreign_trade;" >/dev/null 2>&1; then
            print_info "foreign_trade数据库存在"
        else
            print_error "foreign_trade数据库不存在"
            exit 1
        fi
    else
        print_error "MySQL连接测试失败，请检查密码和权限"
        exit 1
    fi
}

setup_permissions() {
    print_step "配置文件权限..."
    
    # 设置备份目录权限
    chown -R mysql:mysql "$BACKUP_DIR" 2>/dev/null || chown -R root:root "$BACKUP_DIR"
    chmod 755 "$BACKUP_DIR"
    
    # 设置日志文件权限
    touch "$LOG_DIR/mysql_backup.log"
    chown mysql:mysql "$LOG_DIR/mysql_backup.log" 2>/dev/null || chown root:root "$LOG_DIR/mysql_backup.log"
    chmod 644 "$LOG_DIR/mysql_backup.log"
    
    print_info "文件权限配置完成"
}

setup_cron_job() {
    print_step "配置定时任务..."
    
    # 删除现有的备份任务
    if crontab -l 2>/dev/null | grep -q "mysql_backup"; then
        crontab -l 2>/dev/null | grep -v "mysql_backup" | crontab -
        print_info "已删除现有备份任务"
    fi
    
    # 添加新的定时任务（每天凌晨2点）
    local cron_expr="$CRON_TIME * * * $BACKUP_SCRIPT >> $LOG_DIR/mysql_backup_cron.log 2>&1"
    (crontab -l 2>/dev/null; echo "$cron_expr") | crontab -
    
    if [ $? -eq 0 ]; then
        print_info "定时任务配置成功（每天凌晨2点执行）"
    else
        print_error "定时任务配置失败"
        exit 1
    fi
}

test_backup() {
    print_step "测试备份功能..."
    
    print_info "执行备份测试..."
    $BACKUP_SCRIPT
    
    if [ $? -eq 0 ]; then
        print_info "备份测试成功"
        
        # 显示备份文件
        echo "备份文件列表："
        ls -lh "$BACKUP_DIR"/*.sql* 2>/dev/null || echo "暂无备份文件"
    else
        print_error "备份测试失败，请检查配置"
        exit 1
    fi
}

show_status() {
    print_step "显示安装状态..."
    
    echo "=========================================="
    echo "        Foreign Trade备份系统状态"
    echo "=========================================="
    
    # 检查脚本文件
    if [ -f "$BACKUP_SCRIPT" ]; then
        echo "✓ 备份脚本已安装: $BACKUP_SCRIPT"
    else
        echo "✗ 备份脚本未安装"
    fi
    
    # 检查目录
    if [ -d "$BACKUP_DIR" ]; then
        echo "✓ 备份目录已创建: $BACKUP_DIR"
        echo "  目录大小: $(du -sh "$BACKUP_DIR" 2>/dev/null | cut -f1)"
    else
        echo "✗ 备份目录未创建"
    fi
    
    # 检查定时任务
    if crontab -l 2>/dev/null | grep -q "mysql_backup"; then
        echo "✓ 定时任务已配置"
        echo "  执行时间: 每天凌晨2点"
        crontab -l | grep "mysql_backup"
    else
        echo "✗ 定时任务未配置"
    fi
    
    # 检查日志文件
    if [ -f "$LOG_DIR/mysql_backup.log" ]; then
        echo "✓ 日志文件已创建: $LOG_DIR/mysql_backup.log"
        echo "  最近日志:"
        tail -3 "$LOG_DIR/mysql_backup.log" 2>/dev/null || echo "  暂无日志"
    else
        echo "✗ 日志文件未创建"
    fi
    
    echo "=========================================="
}

# ==================== 主函数 ====================
main() {
    echo "=========================================="
    echo "    Foreign Trade数据库备份部署工具"
    echo "=========================================="
    echo "配置信息："
    echo "- 备份数据库: foreign_trade"
    echo "- 备份频率: 每天凌晨2点"
    echo "- 保留时间: 7天"
    echo "- 脚本位置: /home/db/script"
    echo "- 备份位置: /home/db/data"
    echo "- MySQL版本: 8.0"
    echo "- 认证方式: 密码认证"
    echo "=========================================="
    
    check_root
    check_mysql
    create_directories
    install_backup_script
    configure_mysql_password
    test_mysql_connection
    setup_permissions
    setup_cron_job
    test_backup
    show_status
    
    echo
    print_info "Foreign Trade数据库备份系统部署完成！"
    echo
    echo "常用命令："
    echo "- 手动执行备份: sudo $BACKUP_SCRIPT"
    echo "- 查看备份日志: tail -f $LOG_DIR/mysql_backup.log"
    echo "- 查看备份文件: ls -lh $BACKUP_DIR/"
    echo "- 查看定时任务: crontab -l"
    echo "- 脚本位置: $BACKUP_SCRIPT"
    echo "- 备份位置: $BACKUP_DIR"
    echo
}

# ==================== 脚本执行 ====================
if [ "$1" = "status" ]; then
    show_status
elif [ "$1" = "test" ]; then
    test_backup
elif [ "$1" = "install" ]; then
    main
else
    main
fi 