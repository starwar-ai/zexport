# Flyway脚本命名规范

<cite>
**本文档引用文件**  
- [V1_0_0_001__框架初始化.sql](file://eplus-flyway/src/main/resources/db/migration/common/V1_0_0_001__框架初始化.sql)
- [V1_0_0_002__Eplus初始化.sql](file://eplus-flyway/src/main/resources/db/migration/common/V1_0_0_002__Eplus初始化.sql)
- [V1_0_0_050__新增加工单表.sql](file://eplus-flyway/src/main/resources/db/migration/common/V1_0_0_050__新增加工单表.sql)
- [V1_0_0_500__新增费用申请表.sql](file://eplus-flyway/src/main/resources/db/migration/common/V1_0_0_500__新增费用申请表.sql)
- [R__系统配置.sql](file://eplus-flyway/src/main/resources/db/migration/common/R__系统配置.sql)
- [R__定时任务相关.sql](file://eplus-flyway/src/main/resources/db/migration/dev/R__定时任务相关.sql)
- [afterMigrateError__清除失败的运行记录.sql](file://eplus-flyway/src/main/resources/db/migration/common/afterMigrateError__清除失败的运行记录.sql)
</cite>

## 目录
1. [引言](#引言)
2. [版本化迁移脚本命名规范](#版本化迁移脚本命名规范)
3. [可重复迁移脚本命名规范](#可重复迁移脚本命名规范)
4. [错误处理迁移脚本命名规范](#错误处理迁移脚本命名规范)
5. [版本号递增规则](#版本号递增规则)
6. [描述部分命名规范](#描述部分命名规范)
7. [最佳实践](#最佳实践)

## 引言
本文档旨在规范eplus-admin-server项目中Flyway数据库迁移脚本的命名规则，确保数据库变更的可追溯性、一致性和可维护性。通过定义清晰的命名规范，团队成员能够快速理解每个迁移脚本的目的和影响范围，避免命名冲突和混乱。

**Section sources**
- [V1_0_0_001__框架初始化.sql](file://eplus-flyway/src/main/resources/db/migration/common/V1_0_0_001__框架初始化.sql)
- [V1_0_0_002__Eplus初始化.sql](file://eplus-flyway/src/main/resources/db/migration/common/V1_0_0_002__Eplus初始化.sql)

## 版本化迁移脚本命名规范
版本化迁移脚本采用`V`前缀，遵循`V{主版本}_{次版本}_{修订版本}_{补丁版本}__{描述}.sql`的命名格式。

- **V**: 表示版本化迁移（Versioned Migration），每个脚本都有唯一的版本号，按顺序执行且仅执行一次。
- **主版本、次版本、修订版本、补丁版本**: 四位数字组成的版本号，用于标识迁移的版本层次。
- **双下划线（__）**: 分隔版本号和描述部分，必须使用两个连续的下划线。
- **描述**: 对迁移内容的简要说明，使用中文描述，语义清晰、简洁明了。

示例：`V1_0_0_001__框架初始化.sql`

**Section sources**
- [V1_0_0_001__框架初始化.sql](file://eplus-flyway/src/main/resources/db/migration/common/V1_0_0_001__框架初始化.sql)
- [V1_0_0_002__Eplus初始化.sql](file://eplus-flyway/src/main/resources/db/migration/common/V1_0_0_002__Eplus初始化.sql)

## 可重复迁移脚本命名规范
可重复迁移脚本采用`R`前缀，遵循`R__{描述}.sql`的命名格式。

- **R**: 表示可重复迁移（Repeatable Migration），当脚本内容发生变化时会重新执行。
- **双下划线（__）**: 分隔前缀和描述部分。
- **描述**: 对迁移内容的简要说明，通常用于创建视图、存储过程、函数或初始化数据等需要保持最新状态的SQL脚本。

与版本化迁移不同，可重复迁移脚本没有版本号，Flyway会根据脚本内容的校验和来判断是否需要重新执行。

示例：`R__系统配置.sql`

**Section sources**
- [R__系统配置.sql](file://eplus-flyway/src/main/resources/db/migration/common/R__系统配置.sql)
- [R__定时任务相关.sql](file://eplus-flyway/src/main/resources/db/migration/dev/R__定时任务相关.sql)

## 错误处理迁移脚本命名规范
错误处理迁移脚本采用`afterMigrateError__`前缀，用于在迁移失败后执行清理或修复操作。

- **afterMigrateError__**: 特殊前缀，表示该脚本在迁移失败后执行。
- **描述**: 说明脚本的用途，如"清除失败的运行记录"。

这类脚本用于处理迁移过程中可能出现的异常情况，确保数据库元数据的一致性。

示例：`afterMigrateError__清除失败的运行记录.sql`

**Section sources**
- [afterMigrateError__清除失败的运行记录.sql](file://eplus-flyway/src/main/resources/db/migration/common/afterMigrateError__清除失败的运行记录.sql)

## 版本号递增规则
版本号由四位数字组成：`主版本_次版本_修订版本_补丁版本`，遵循语义化版本控制原则。

### 主版本（第一位数字）
- 当进行不兼容的数据库架构修改时递增
- 例如：大规模重构、表结构重大变更、数据迁移等
- 示例：从`V1_x_x_x`升级到`V2_x_x_x`

### 次版本（第二位数字）
- 当进行向后兼容的功能新增时递增
- 例如：新增模块、新增一组相关表等
- 示例：从`V1_0_x_x`升级到`V1_1_x_x`

### 修订版本（第三位数字）
- 当进行向后兼容的问题修正时递增
- 例如：修正字段类型、调整索引、优化查询性能等
- 示例：从`V1_0_0_x`升级到`V1_0_1_x`

### 补丁版本（第四位数字）
- 用于标识具体的迁移脚本序号
- 从`001`开始递增，确保同一修订版本下的脚本按顺序执行
- 示例：`V1_0_0_001`、`V1_0_0_002`、`V1_0_0_003`

**Section sources**
- [V1_0_0_001__框架初始化.sql](file://eplus-flyway/src/main/resources/db/migration/common/V1_0_0_001__框架初始化.sql)
- [V1_0_0_002__Eplus初始化.sql](file://eplus-flyway/src/main/resources/db/migration/common/V1_0_0_002__Eplus初始化.sql)
- [V1_0_0_050__新增加工单表.sql](file://eplus-flyway/src/main/resources/db/migration/common/V1_0_0_050__新增加工单表.sql)
- [V1_0_0_500__新增费用申请表.sql](file://eplus-flyway/src/main/resources/db/migration/common/V1_0_0_500__新增费用申请表.sql)

## 描述部分命名规范
描述部分位于双下划线之后，对迁移脚本的内容进行简要说明，需遵循以下规范：

- 使用中文描述，避免使用英文或拼音
- 语义清晰，准确表达迁移的目的
- 简洁明了，避免过长的描述
- 使用动词开头，如"新建"、"新增"、"修改"、"删除"、"重构"等
- 对于表操作，采用"动词+表名+表"的格式
- 对于数据初始化，采用"初始化+模块+数据"的格式

### 命名示例
- `新建银行登记表`：创建新的银行登记表
- `新增客户认领明细表`：添加客户认领明细表
- `采购变更表重构`：对采购变更表进行结构重构
- `系统配置数据初始化`：初始化系统配置相关数据
- `增加版本表`：为系统增加版本管理功能

避免使用模糊的描述，如"修改数据库"、"更新脚本"等，应具体说明修改的内容。

**Section sources**
- [V1_0_0_001__框架初始化.sql](file://eplus-flyway/src/main/resources/db/migration/common/V1_0_0_001__框架初始化.sql)
- [V1_0_0_038__采购模块加减项表创建.sql](file://eplus-flyway/src/main/resources/db/migration/common/V1_0_0_038__采购模块加减项表创建.sql)
- [V1_0_0_113__采购变更表重构.sql](file://eplus-flyway/src/main/resources/db/migration/common/V1_0_0_113__采购变更表重构.sql)

## 最佳实践
为确保Flyway迁移脚本的质量和可维护性，建议遵循以下最佳实践：

1. **原子性原则**：每个迁移脚本应只包含一个逻辑变更，避免将多个不相关的变更放在同一个脚本中。

2. **可逆性考虑**：虽然Flyway不强制要求提供回滚脚本，但应考虑关键变更的可逆性，在必要时提供相应的回滚策略。

3. **环境一致性**：确保开发、测试和生产环境使用相同的迁移脚本，避免环境间差异导致的问题。

4. **版本控制**：所有迁移脚本必须纳入版本控制系统（如Git），确保变更的可追溯性。

5. **测试验证**：在提交迁移脚本前，应在本地环境进行充分测试，确保脚本能够正确执行。

6. **文档注释**：在SQL脚本中添加必要的注释，说明复杂的逻辑或重要的决策。

7. **避免DDL和DML混合**：尽量将表结构变更（DDL）和数据变更（DML）分开在不同的脚本中执行。

8. **性能考虑**：对于大数据量的表变更，应考虑分批处理或在低峰期执行，避免影响系统性能。

9. **命名一致性**：严格遵守命名规范，确保团队成员之间的命名一致性。

10. **定期审查**：定期审查迁移脚本，清理不再需要的脚本或优化现有脚本。

通过遵循这些最佳实践，可以确保数据库迁移过程的稳定性和可靠性，降低生产环境的风险。

**Section sources**
- [V1_0_0_001__框架初始化.sql](file://eplus-flyway/src/main/resources/db/migration/common/V1_0_0_001__框架初始化.sql)
- [R__系统配置.sql](file://eplus-flyway/src/main/resources/db/migration/common/R__系统配置.sql)
- [afterMigrateError__清除失败的运行记录.sql](file://eplus-flyway/src/main/resources/db/migration/common/afterMigrateError__清除失败的运行记录.sql)