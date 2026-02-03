# Foreign Trade数据库备份系统

## 概述

这是一个专门为`foreign_trade`数据库定制的MySQL备份解决方案，适用于CentOS等Linux系统。

## 配置信息

- ✅ **备份数据库**: `foreign_trade`
- ✅ **备份频率**: 每天凌晨2点
- ✅ **保留时间**: 7天
- ✅ **脚本位置**: `/home/db/script`
- ✅ **备份位置**: `/home/db/data`
- ✅ **MySQL版本**: 8.0
- ✅ **认证方式**: 密码认证

## 文件说明

### 核心脚本
| 文件名 | 作用 |
|--------|------|
| `mysql_backup.sh` | 核心备份脚本 |
| `deploy_foreign_trade_backup.sh` | 快速部署脚本 |
| `setup_paths.sh` | 路径设置脚本 |

### 文档文件
| 文件名 | 作用 |
|--------|------|
| `README.md` | 本文件 - 完整使用说明 |

## 快速部署

### 1. 设置目录结构

```bash
cd mysql_backup_system
chmod +x *.sh
sudo ./setup_paths.sh
```

### 2. 部署备份系统

```bash
sudo /home/db/script/deploy_foreign_trade_backup.sh
```

### 3. 按提示输入MySQL密码

脚本会提示您输入MySQL root密码，输入后按回车确认。

## 部署过程

部署脚本会自动执行以下步骤：

1. **检查系统环境** - 验证root权限和MySQL环境
2. **创建目录** - 创建备份目录和日志目录
3. **安装脚本** - 安装备份脚本到系统目录
4. **配置密码** - 配置MySQL连接密码
5. **测试连接** - 验证MySQL连接和数据库存在
6. **设置权限** - 配置文件权限
7. **配置定时任务** - 设置每天凌晨2点执行备份
8. **测试备份** - 执行一次备份测试
9. **显示状态** - 显示安装结果

## 验证安装

### 查看安装状态

```bash
sudo /home/db/script/deploy_foreign_trade_backup.sh status
```

### 手动执行备份测试

```bash
sudo /home/db/script/mysql_backup.sh
```

### 查看备份文件

```bash
ls -lh /home/db/data/
```

### 查看备份日志

```bash
tail -f /var/log/mysql_backup.log
```

### 查看定时任务

```bash
crontab -l
```

## 常用命令

### 备份管理

```bash
# 手动执行备份
sudo /home/db/script/mysql_backup.sh

# 查看备份日志
tail -f /var/log/mysql_backup.log

# 查看备份文件
ls -lh /home/db/data/

# 查看最近的备份记录
grep "SUCCESS" /var/log/mysql_backup.log | tail -5
```

### 定时任务管理

```bash
# 查看当前定时任务
crontab -l

# 编辑定时任务
crontab -e

# 删除所有定时任务
crontab -r
```

### 备份文件管理

```bash
# 查看备份目录大小
du -sh /home/db/data/

# 查看备份文件数量
ls -1 /home/db/data/*.sql* | wc -l

# 查看最大的备份文件
ls -lh /home/db/data/*.sql* | sort -k5 -hr | head -3
```

## 备份文件说明

### 文件命名规则

```
foreign_trade_YYYYMMDD_HHMMSS.sql.gz
```

例如：`foreign_trade_20240101_020000.sql.gz`

### 备份文件内容

- ✅ 数据库结构（表、索引、约束）
- ✅ 完整数据内容
- ✅ 存储过程和函数
- ✅ 触发器和事件
- ✅ 视图定义

## 恢复数据

### 恢复整个数据库

```bash
# 解压备份文件
gunzip foreign_trade_20240101_020000.sql.gz

# 恢复数据库
mysql -u root -p foreign_trade < foreign_trade_20240101_020000.sql
```

### 恢复特定表

```bash
# 从备份文件中提取特定表
sed -n '/^-- Table structure for table `表名`/,/^-- Table structure for table/p' foreign_trade_20240101_020000.sql > table_backup.sql

# 恢复特定表
mysql -u root -p foreign_trade < table_backup.sql
```

## 监控和维护

### 日志监控

```bash
# 实时查看备份日志
tail -f /var/log/mysql_backup.log

# 查看成功备份记录
grep "SUCCESS" /var/log/mysql_backup.log | tail -10

# 查看错误信息
grep "ERROR" /var/log/mysql_backup.log | tail -10
```

### 磁盘空间监控

```bash
# 查看备份目录使用情况
df -h /home/db/data/

# 查看备份文件大小
du -sh /home/db/data/*.sql* | sort -hr
```

### 性能监控

```bash
# 查看备份执行时间
grep "备份完成" /var/log/mysql_backup.log | tail -5

# 查看备份文件大小变化
ls -lh /home/db/data/*.sql* | tail -5
```

## 故障排除

### 常见问题

#### 1. MySQL连接失败
```bash
# 检查MySQL服务状态
systemctl status mysqld

# 测试MySQL连接
mysql -u root -p -e "SELECT 1;"

# 检查foreign_trade数据库
mysql -u root -p -e "USE foreign_trade;"
```

#### 2. 权限问题
```bash
# 检查脚本权限
ls -la /home/db/script/mysql_backup.sh

# 修复权限
chmod +x /home/db/script/mysql_backup.sh
chown root:root /home/db/script/mysql_backup.sh
```

#### 3. 定时任务不执行
```bash
# 检查cron服务
systemctl status crond

# 查看cron日志
tail -f /var/log/cron

# 手动测试备份
sudo /home/db/script/mysql_backup.sh
```

#### 4. 磁盘空间不足
```bash
# 检查磁盘使用情况
df -h

# 清理旧备份文件
find /home/db/data/ -name "*.sql*" -mtime +7 -delete
```

## 配置说明

### 备份脚本配置

主要配置参数（位于 `/home/db/script/mysql_backup.sh`）：

```bash
# 数据库配置
DATABASES="foreign_trade"           # 指定备份的数据库

# 备份配置
BACKUP_DIR="/home/db/data"         # 备份目录
BACKUP_RETAIN_DAYS=7               # 保留7天

# MySQL连接配置
MYSQL_HOST="localhost"              # MySQL主机
MYSQL_PORT="3306"                  # MySQL端口
MYSQL_USER="root"                  # MySQL用户
MYSQL_PASSWORD="your_password"     # MySQL密码
```

### 定时任务配置

```bash
# 每天凌晨2点执行备份
0 2 * * * /home/db/script/mysql_backup.sh >> /var/log/mysql_backup_cron.log 2>&1
```

## 目录结构

```
/home/db/
├── script/
│   ├── mysql_backup.sh                 # 备份脚本
│   └── deploy_foreign_trade_backup.sh  # 部署脚本
└── data/
    └── foreign_trade_*.sql.gz          # 备份文件

/var/log/
├── mysql_backup.log                    # 备份日志
└── mysql_backup_cron.log               # 定时任务日志
```

## 安全建议

1. **密码安全** - 定期更换MySQL密码
2. **文件权限** - 确保备份文件权限合理
3. **定期检查** - 定期验证备份文件的完整性
4. **异地备份** - 考虑将备份文件同步到其他服务器

## 技术支持

如有问题，请检查：
1. MySQL服务是否正常运行
2. 密码是否正确
3. foreign_trade数据库是否存在
4. 磁盘空间是否充足
5. 系统权限是否正确

---

**注意**: 在生产环境中使用前，请务必在测试环境中充分测试备份和恢复功能。 