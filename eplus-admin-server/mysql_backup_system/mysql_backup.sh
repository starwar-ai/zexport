#!/bin/bash

# MySQL数据库定时备份脚本
# 作者：波波
# 日期：2024-01-01
# 描述：自动备份MySQL数据库，支持单库和多库备份

# ==================== 配置参数 ====================
# MySQL连接配置（MySQL 8.0，密码认证）
MYSQL_HOST="localhost"
MYSQL_PORT="3306"
MYSQL_USER="root"
MYSQL_PASSWORD="Wykj.123"
MYSQL_SOCKET="/var/lib/mysql/mysql.sock"

# 备份配置
BACKUP_DIR="/home/db/data"
BACKUP_RETAIN_DAYS=7
BACKUP_TIME=$(date +"%Y%m%d_%H%M%S")
BACKUP_LOG="/var/log/mysql_backup.log"

# 数据库配置（指定备份foreign_trade数据库）
DATABASES="foreign_trade"

# 压缩配置
COMPRESS_BACKUP=true
COMPRESS_CMD="gzip"

# ==================== 日志函数 ====================
log() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] $1" | tee -a "$BACKUP_LOG"
}

log_error() {
    log "ERROR: $1"
}

log_info() {
    log "INFO: $1"
}

log_success() {
    log "SUCCESS: $1"
}

# ==================== 检查函数 ====================
check_mysql_connection() {
    log_info "检查MySQL连接..."
    
    if [ -n "$MYSQL_PASSWORD" ]; then
        mysql -h"$MYSQL_HOST" -P"$MYSQL_PORT" -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "SELECT 1;" >/dev/null 2>&1
    else
        mysql -S"$MYSQL_SOCKET" -u"$MYSQL_USER" -e "SELECT 1;" >/dev/null 2>&1
    fi
    
    if [ $? -eq 0 ]; then
        log_success "MySQL连接正常"
        return 0
    else
        log_error "MySQL连接失败"
        return 1
    fi
}

check_backup_dir() {
    log_info "检查备份目录..."
    
    if [ ! -d "$BACKUP_DIR" ]; then
        log_info "创建备份目录: $BACKUP_DIR"
        mkdir -p "$BACKUP_DIR"
        if [ $? -ne 0 ]; then
            log_error "创建备份目录失败"
            return 1
        fi
    fi
    
    log_success "备份目录检查完成"
    return 0
}

# ==================== 备份函数 ====================
backup_single_database() {
    local db_name="$1"
    local backup_file="$BACKUP_DIR/${db_name}_${BACKUP_TIME}.sql"
    
    log_info "开始备份数据库: $db_name"
    
    if [ -n "$MYSQL_PASSWORD" ]; then
        mysqldump -h"$MYSQL_HOST" -P"$MYSQL_PORT" -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" \
            --single-transaction \
            --routines \
            --triggers \
            --events \
            --hex-blob \
            --opt \
            "$db_name" > "$backup_file" 2>/dev/null
    else
        mysqldump -S"$MYSQL_SOCKET" -u"$MYSQL_USER" \
            --single-transaction \
            --routines \
            --triggers \
            --events \
            --hex-blob \
            --opt \
            "$db_name" > "$backup_file" 2>/dev/null
    fi
    
    if [ $? -eq 0 ]; then
        log_success "数据库 $db_name 备份完成: $backup_file"
        
        # 压缩备份文件
        if [ "$COMPRESS_BACKUP" = true ]; then
            log_info "压缩备份文件: $backup_file"
            $COMPRESS_CMD "$backup_file"
            if [ $? -eq 0 ]; then
                log_success "备份文件压缩完成: ${backup_file}.gz"
                backup_file="${backup_file}.gz"
            else
                log_error "备份文件压缩失败"
            fi
        fi
        
        # 显示备份文件大小
        local file_size=$(du -h "$backup_file" | cut -f1)
        log_info "备份文件大小: $file_size"
        
        return 0
    else
        log_error "数据库 $db_name 备份失败"
        return 1
    fi
}

backup_all_databases() {
    log_info "开始备份所有数据库..."
    
    local backup_file="$BACKUP_DIR/all_databases_${BACKUP_TIME}.sql"
    
    if [ -n "$MYSQL_PASSWORD" ]; then
        mysqldump -h"$MYSQL_HOST" -P"$MYSQL_PORT" -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" \
            --single-transaction \
            --routines \
            --triggers \
            --events \
            --hex-blob \
            --opt \
            --all-databases > "$backup_file" 2>/dev/null
    else
        mysqldump -S"$MYSQL_SOCKET" -u"$MYSQL_USER" \
            --single-transaction \
            --routines \
            --triggers \
            --events \
            --hex-blob \
            --opt \
            --all-databases > "$backup_file" 2>/dev/null
    fi
    
    if [ $? -eq 0 ]; then
        log_success "所有数据库备份完成: $backup_file"
        
        # 压缩备份文件
        if [ "$COMPRESS_BACKUP" = true ]; then
            log_info "压缩备份文件: $backup_file"
            $COMPRESS_CMD "$backup_file"
            if [ $? -eq 0 ]; then
                log_success "备份文件压缩完成: ${backup_file}.gz"
                backup_file="${backup_file}.gz"
            else
                log_error "备份文件压缩失败"
            fi
        fi
        
        # 显示备份文件大小
        local file_size=$(du -h "$backup_file" | cut -f1)
        log_info "备份文件大小: $file_size"
        
        return 0
    else
        log_error "所有数据库备份失败"
        return 1
    fi
}

# ==================== 清理函数 ====================
cleanup_old_backups() {
    log_info "清理过期备份文件（保留 $BACKUP_RETAIN_DAYS 天）..."
    
    local deleted_count=0
    
    # 查找并删除过期的备份文件
    find "$BACKUP_DIR" -name "*.sql" -mtime +$BACKUP_RETAIN_DAYS -delete
    find "$BACKUP_DIR" -name "*.sql.gz" -mtime +$BACKUP_RETAIN_DAYS -delete
    
    deleted_count=$(find "$BACKUP_DIR" -name "*.sql" -mtime +$BACKUP_RETAIN_DAYS | wc -l)
    deleted_count=$((deleted_count + $(find "$BACKUP_DIR" -name "*.sql.gz" -mtime +$BACKUP_RETAIN_DAYS | wc -l)))
    
    if [ $deleted_count -gt 0 ]; then
        log_info "清理了 $deleted_count 个过期备份文件"
    else
        log_info "没有需要清理的过期备份文件"
    fi
}

# ==================== 主函数 ====================
main() {
    log_info "========== MySQL备份开始 =========="
    
    # 检查MySQL连接
    if ! check_mysql_connection; then
        log_error "MySQL连接失败，备份终止"
        exit 1
    fi
    
    # 检查备份目录
    if ! check_backup_dir; then
        log_error "备份目录检查失败，备份终止"
        exit 1
    fi
    
    # 执行备份
    local backup_success=true
    
    if [ -n "$DATABASES" ]; then
        # 备份指定数据库
        for db in $DATABASES; do
            if ! backup_single_database "$db"; then
                backup_success=false
            fi
        done
    else
        # 备份所有数据库
        if ! backup_all_databases; then
            backup_success=false
        fi
    fi
    
    # 清理过期备份
    cleanup_old_backups
    
    # 备份结果
    if [ "$backup_success" = true ]; then
        log_success "========== MySQL备份完成 =========="
        exit 0
    else
        log_error "========== MySQL备份失败 =========="
        exit 1
    fi
}

# ==================== 脚本执行 ====================
main "$@" 