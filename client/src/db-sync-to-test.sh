#!/bin/bash

##############################################################################
# 数据库同步脚本 - 从生产服务器dump数据并同步到测试服务器
# 使用方法: ./db-sync-to-test.sh
##############################################################################

set -e  # 遇到错误立即退出

# ==================== 配置区域 ====================

# 源数据库配置（生产服务器）
SOURCE_DB_HOST="127.0.0.1"
SOURCE_DB_PORT="3306"
SOURCE_DB_NAME="foreign_trade"
SOURCE_DB_USER="root"
SOURCE_DB_PASSWORD="Wykj.123"

# 目标服务器配置（测试服务器）
TARGET_SERVER_HOST="110.40.190.51"
TARGET_SERVER_USER="ubuntu"
TARGET_SERVER_PORT="22"

# 目标数据库配置（测试服务器上的数据库）
TARGET_DB_HOST="localhost"
TARGET_DB_PORT="3306"
TARGET_DB_NAME="foreign_trade"
TARGET_DB_USER="root"
TARGET_DB_PASSWORD="root123"

# Docker配置（如果目标数据库在Docker中）
DOCKER_SERVICE_NAME="eplus-mysql"  # Docker服务名称或容器名称
USE_DOCKER=true  # 是否使用Docker容器中的数据库

# 备份文件配置
BACKUP_DIR="/tmp/db_backup"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_FILE="eplus_backup_${TIMESTAMP}.sql"
BACKUP_FILE_TAR_GZ="eplus_backup_${TIMESTAMP}.tar.gz"
TARGET_BACKUP_DIR="/tmp/db_restore"

# 保留最近的备份数量
KEEP_BACKUPS=5

# ==================== 函数定义 ====================

# 日志函数
log_info() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] [INFO] $1"
}

log_error() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] [ERROR] $1" >&2
}

log_success() {
    echo "[$(date +'%Y-%m-%d %H:%M:%S')] [SUCCESS] $1"
}

# 检查命令是否存在
check_command() {
    if ! command -v $1 &> /dev/null; then
        log_error "$1 命令未找到，请先安装"
        exit 1
    fi
}

# 清理旧备份
cleanup_old_backups() {
    log_info "清理旧备份文件,保留最近 ${KEEP_BACKUPS} 个..."
    cd ${BACKUP_DIR}
    ls -t eplus_backup_*.tar.gz 2>/dev/null | tail -n +$((KEEP_BACKUPS + 1)) | xargs -r rm -f
    log_success "旧备份清理完成"
}

# ==================== 主流程 ====================

main() {
    log_info "========== 数据库同步任务开始 =========="
    
    # 1. 检查必要的命令
    log_info "检查必要的命令..."
    check_command mysqldump
    check_command tar
    check_command scp
    check_command ssh
    
    # 2. 创建备份目录
    log_info "创建备份目录..."
    mkdir -p ${BACKUP_DIR}
    
    # 3. 导出数据库
    log_info "开始导出数据库: ${SOURCE_DB_NAME}"
    log_info "数据库地址: ${SOURCE_DB_HOST}:${SOURCE_DB_PORT}"
    
    mysqldump -h${SOURCE_DB_HOST} \
              -P${SOURCE_DB_PORT} \
              -u${SOURCE_DB_USER} \
              -p${SOURCE_DB_PASSWORD} \
              --single-transaction \
              --quick \
              --lock-tables=false \
              --routines \
              --triggers \
              --events \
              --hex-blob \
              --default-character-set=utf8mb4 \
              ${SOURCE_DB_NAME} > ${BACKUP_DIR}/${BACKUP_FILE}
    
    if [ $? -ne 0 ]; then
        log_error "数据库导出失败"
        exit 1
    fi
    
    BACKUP_SIZE=$(du -h ${BACKUP_DIR}/${BACKUP_FILE} | cut -f1)
    log_success "数据库导出成功，文件大小: ${BACKUP_SIZE}"
    
    # 4. 压缩备份文件为tar.gz
    log_info "压缩备份文件为tar.gz..."
    tar -czf ${BACKUP_DIR}/${BACKUP_FILE_TAR_GZ} -C ${BACKUP_DIR} ${BACKUP_FILE}
    rm -f ${BACKUP_DIR}/${BACKUP_FILE}
        
    COMPRESSED_SIZE=$(du -h ${BACKUP_DIR}/${BACKUP_FILE_TAR_GZ} | cut -f1)
    log_success "备份文件压缩完成,压缩后大小: ${COMPRESSED_SIZE}"
    
    # 5. 在目标服务器创建目录
    log_info "在目标服务器创建备份目录..."
    ssh -p ${TARGET_SERVER_PORT} ${TARGET_SERVER_USER}@${TARGET_SERVER_HOST} \
        "mkdir -p ${TARGET_BACKUP_DIR}"
    
    # 6. 传输备份文件到测试服务器
    log_info "传输备份文件到测试服务器: ${TARGET_SERVER_HOST}"
    scp -P ${TARGET_SERVER_PORT} \
        ${BACKUP_DIR}/${BACKUP_FILE_TAR_GZ} \
        ${TARGET_SERVER_USER}@${TARGET_SERVER_HOST}:${TARGET_BACKUP_DIR}/
    
    if [ $? -ne 0 ]; then
        log_error "文件传输失败"
        exit 1
    fi
    
    log_success "文件传输完成"
    
    # 7. 在测试服务器上导入数据库
    log_info "在测试服务器上解压并导入数据库..."
    
    if [ "${USE_DOCKER}" = true ]; then
        log_info "使用Docker容器: ${DOCKER_SERVICE_NAME}"
        
        ssh -p ${TARGET_SERVER_PORT} ${TARGET_SERVER_USER}@${TARGET_SERVER_HOST} << EOF
            set -e
            
            echo "[导入] 解压备份文件..."
            tar -xzf ${TARGET_BACKUP_DIR}/${BACKUP_FILE_TAR_GZ} -C ${TARGET_BACKUP_DIR}
            rm -f ${TARGET_BACKUP_DIR}/${BACKUP_FILE_TAR_GZ}
            
            echo "[导入] 检查Docker容器状态..."
            docker ps | grep ${DOCKER_SERVICE_NAME} || { echo "Docker容器未运行"; exit 1; }
            
            echo "[导入] 删除目标数据库（如果存在）..."
            docker exec ${DOCKER_SERVICE_NAME} mysql -u${TARGET_DB_USER} -p${TARGET_DB_PASSWORD} \
                  -e "DROP DATABASE IF EXISTS ${TARGET_DB_NAME};"
            
            echo "[导入] 创建目标数据库..."
            docker exec ${DOCKER_SERVICE_NAME} mysql -u${TARGET_DB_USER} -p${TARGET_DB_PASSWORD} \
                  -e "CREATE DATABASE ${TARGET_DB_NAME} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
            
            echo "[导入] 导入数据..."
            docker exec -i ${DOCKER_SERVICE_NAME} mysql -u${TARGET_DB_USER} -p${TARGET_DB_PASSWORD} \
                  --default-character-set=utf8mb4 \
                  ${TARGET_DB_NAME} < ${TARGET_BACKUP_DIR}/${BACKUP_FILE}
            
            echo "[导入] 清理临时文件..."
            rm -f ${TARGET_BACKUP_DIR}/${BACKUP_FILE}
            
            echo "[导入] 数据库导入完成"
EOF
    else
        ssh -p ${TARGET_SERVER_PORT} ${TARGET_SERVER_USER}@${TARGET_SERVER_HOST} << EOF
            set -e
            
            echo "[导入] 解压备份文件..."
            tar -xzf ${TARGET_BACKUP_DIR}/${BACKUP_FILE_TAR_GZ} -C ${TARGET_BACKUP_DIR}
            rm -f ${TARGET_BACKUP_DIR}/${BACKUP_FILE_TAR_GZ}
            
            echo "[导入] 删除目标数据库（如果存在）..."
            mysql -h${TARGET_DB_HOST} -P${TARGET_DB_PORT} -u${TARGET_DB_USER} -p${TARGET_DB_PASSWORD} \
                  -e "DROP DATABASE IF EXISTS ${TARGET_DB_NAME};"
            
            echo "[导入] 创建目标数据库..."
            mysql -h${TARGET_DB_HOST} -P${TARGET_DB_PORT} -u${TARGET_DB_USER} -p${TARGET_DB_PASSWORD} \
                  -e "CREATE DATABASE ${TARGET_DB_NAME} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
            
            echo "[导入] 导入数据..."
            mysql -h${TARGET_DB_HOST} -P${TARGET_DB_PORT} -u${TARGET_DB_USER} -p${TARGET_DB_PASSWORD} \
                  --default-character-set=utf8mb4 \
                  ${TARGET_DB_NAME} < ${TARGET_BACKUP_DIR}/${BACKUP_FILE}
            
            echo "[导入] 清理临时文件..."
            rm -f ${TARGET_BACKUP_DIR}/${BACKUP_FILE}
            
            echo "[导入] 数据库导入完成"
EOF
    fi
    
    if [ $? -ne 0 ]; then
        log_error "数据库导入失败"
        exit 1
    fi
    
    log_success "数据库导入成功"
    
    # 8. 清理本地旧备份
    cleanup_old_backups
    
    # 9. 完成
    log_success "========== 数据库同步任务完成 =========="
    log_info "备份文件: ${BACKUP_DIR}/${BACKUP_FILE_TAR_GZ}"
    log_info "压缩大小: ${COMPRESSED_SIZE}"
}

# 执行主流程
main

exit 0
