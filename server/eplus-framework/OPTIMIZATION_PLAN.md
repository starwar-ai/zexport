# Eplus Framework 模块优化方案

## 📊 模块现状分析

### 基本信息
- **模块名称**: eplus-framework
- **子模块**: eplus-common
- **Java文件数**: 223个
- **代码总行数**: 9,417行
- **主要职责**: 提供通用工具类、实体类、枚举、类型处理器等基础设施

### 目录结构
```
eplus-framework/
└── eplus-common/
    └── src/main/java/com/syj/eplus/framework/common/
        ├── annotations/          # 注解
        ├── config/              # 配置类
        │   └── handler/         # MyBatis类型处理器(40+个)
        ├── dict/                # 业务字典
        ├── entity/              # 通用实体
        ├── enums/               # 业务枚举(70+个)
        ├── util/                # 工具类
        └── code/                # 代码生成器
```

---

## 🔍 发现的问题

### 1. 代码质量问题

#### 🔴 P0 - 调试代码遗留
**位置**: `AmountToChineseUtil.java:119-128`
```java
// 存在测试代码在main方法中
System.out.println(toChineseUpper(amount1));
System.out.println(toChineseUpper(amount2));
System.out.println(toChineseUpper(amount3));
System.out.println(toChineseUpper(amount4));
```

**位置**: `NoEmptyLineLogger.java:16`
```java
// 使用System.out.println而非日志框架
System.out.println("\u001b[0;32m" + text + "\u001b[0m");
```

**影响**:
- 生产环境性能损耗
- 日志管理混乱
- 无法通过日志级别控制

#### 🟡 P1 - 代码重复
**问题**: 存在两个金额转中文的工具类
- `AmountToChineseUtil.java` - 100行
- `MoneyUtil.java` - 94行

**分析**:
- 功能高度重复(都是金额转中文大写)
- 实现逻辑不同,可能导致结果不一致
- 维护成本高

#### 🟡 P1 - 类型处理器过多
**问题**: `config/handler/` 目录下有40+个TypeHandler

**分析**:
```java
JsonAmountListTypeHandler
JsonAmountTypeHandler
JsonBaseDataListTypeHandler
JsonChangeRecordHandler
JsonChangeRecordListHandler
JsonCollectionPlanItemListHandler
JsonCompanyPathListTypeHandler
JsonCompanyPathTypeHandler
... (还有30+个)
```

**潜在问题**:
- 类爆炸,难以维护
- 很多TypeHandler只是泛型不同
- 缺少统一的基类抽象

### 2. 架构设计问题

#### 🟡 P1 - 职责不清
**问题**: eplus-framework只有一个子模块eplus-common

**建议结构**:
```
eplus-framework/
├── eplus-common          # 通用工具类
├── eplus-spring-boot-starter-web      # Web增强
├── eplus-spring-boot-starter-mybatis  # 持久层增强
├── eplus-spring-boot-starter-excel    # Excel处理
└── eplus-spring-boot-starter-dict     # 字典管理
```

#### 🟡 P2 - 业务耦合
**问题**: 框架模块包含业务实体

**示例**:
```java
// entity目录下存在大量业务实体
PaymentAppDTO.java
PaymentApplyEntity.java
ManufactureSkuReqVO.java
SimpleContractDetailDTO.java
...
```

**影响**: 框架层混入业务逻辑,不利于复用

### 3. 性能问题

#### 🟡 P2 - 枚举数量过多
**问题**: 枚举类70+个,全部加载到内存

**优化方向**:
- 考虑使用配置化字典表
- 延迟加载不常用枚举
- 评估是否需要这么多枚举

### 4. 文档问题

#### 🟡 P2 - 缺少文档
- 无README说明框架用途
- 无使用示例
- 无API文档

---

## 🚀 优化方案

### 阶段一: 紧急修复 (1-2天)

#### 1.1 清理调试代码

**文件**: `AmountToChineseUtil.java`
```java
// 删除main方法中的测试代码
void main(String[] args) {
    // 删除这些System.out.println
}
```

**文件**: `NoEmptyLineLogger.java`
```java
// 替换为SLF4J
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoEmptyLineLogger extends FormattedLogger {
    private static final Logger log = LoggerFactory.getLogger(NoEmptyLineLogger.class);

    @Override
    public void logText(String text) {
        if (text == null || "null".equals(text.trim()) || "".equals(text.trim())) {
            return;
        }
        // 使用日志框架
        log.debug("\u001b[0;32m{}\u001b[0m", text);
    }
}
```

#### 1.2 合并重复工具类

**方案**: 保留`MoneyUtil`,删除`AmountToChineseUtil`

**理由**:
- `MoneyUtil`命名更规范
- 代码结构更清晰
- 已被多处使用

**迁移步骤**:
1. 搜索所有使用`AmountToChineseUtil`的地方
2. 替换为`MoneyUtil`
3. 删除`AmountToChineseUtil.java`

#### 1.3 添加单元测试

**新建**: `MoneyUtilTest.java`
```java
@Test
public void testConvertToChinese() {
    BigDecimal amount1 = new BigDecimal("42114.24");
    assertEquals("肆万贰仟壹佰壹拾肆元贰角肆分",
                 MoneyUtil.convertToChinese(amount1));

    BigDecimal amount2 = new BigDecimal("100.00");
    assertEquals("壹佰元整",
                 MoneyUtil.convertToChinese(amount2));
}
```

---

### 阶段二: 重构优化 (1-2周)

#### 2.1 TypeHandler基类抽象

**新建**: `AbstractJsonTypeHandler.java`
```java
package com.syj.eplus.framework.common.config.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * JSON类型处理器基类
 * 提供统一的JSON序列化/反序列化能力
 */
public abstract class AbstractJsonTypeHandler<T> extends BaseTypeHandler<T> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final TypeReference<T> typeReference;

    protected AbstractJsonTypeHandler(TypeReference<T> typeReference) {
        this.typeReference = typeReference;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, toJson(parameter));
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return fromJson(rs.getString(columnName));
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return fromJson(rs.getString(columnIndex));
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return fromJson(cs.getString(columnIndex));
    }

    private String toJson(T object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("转换为JSON失败", e);
        }
    }

    private T fromJson(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return OBJECT_MAPPER.readValue(json, typeReference);
        } catch (Exception e) {
            throw new RuntimeException("解析JSON失败: " + json, e);
        }
    }
}
```

**改造现有TypeHandler**:
```java
// 改造前
public class JsonAmountListTypeHandler extends BaseTypeHandler<List<JsonAmount>> {
    // 大量重复的序列化/反序列化代码
}

// 改造后
public class JsonAmountListTypeHandler extends AbstractJsonTypeHandler<List<JsonAmount>> {
    public JsonAmountListTypeHandler() {
        super(new TypeReference<List<JsonAmount>>() {});
    }
}
```

**效果**:
- 减少80%的重复代码
- 统一异常处理
- 便于维护和扩展

#### 2.2 模块拆分重组

**新模块结构**:
```
eplus-framework/
├── eplus-common/                           # 通用基础
│   ├── annotations/                        # 注解
│   ├── enums/                             # 枚举
│   └── util/                              # 工具类
│       ├── MoneyUtil                      # 金额工具
│       ├── DateTimeUtil                   # 时间工具
│       ├── NumberFormatUtil               # 数字格式化
│       └── ...
├── eplus-spring-boot-starter-mybatis/      # 持久层增强
│   ├── handler/                           # TypeHandler
│   ├── config/                            # MyBatis配置
│   └── interceptor/                       # 拦截器
├── eplus-spring-boot-starter-dict/         # 字典管理
│   └── dict/                              # 业务字典
└── eplus-domain/                          # 领域模型(可选)
    └── entity/                            # 通用实体
```

**迁移计划**:
```bash
# 阶段1: 创建新模块
mvn archetype:generate -DgroupId=com.syj.eplus \
    -DartifactId=eplus-spring-boot-starter-mybatis

# 阶段2: 迁移TypeHandler
mv eplus-common/config/handler/* \
   eplus-spring-boot-starter-mybatis/handler/

# 阶段3: 更新依赖
# 在需要TypeHandler的模块pom.xml中添加
<dependency>
    <groupId>com.syj.eplus</groupId>
    <artifactId>eplus-spring-boot-starter-mybatis</artifactId>
</dependency>
```

#### 2.3 业务实体下沉

**原则**: 框架层不应包含业务实体

**迁移方案**:
```java
// 从 eplus-common/entity/ 迁移到对应业务模块
PaymentAppDTO.java        → eplus-module-oa/api/dto/
ManufactureSkuReqVO.java  → eplus-module-mms/controller/vo/
SimpleContractDetailDTO.java → eplus-module-scm/api/dto/
```

**保留通用实体**:
```java
// 这些可以保留在eplus-common
BaseData.java           // 通用基础数据
SimpleData.java         // 简单数据
SimpleFile.java         // 文件信息
CompanyPath.java        // 公司路径
...
```

---

### 阶段三: 长期优化 (1-2月)

#### 3.1 枚举优化

**方案1: 字典表替代**
```java
// 替换为配置化
// 从数据库或配置中心加载
public interface DictService {
    String getDictLabel(String dictType, String dictValue);
    List<DictData> getDictDataList(String dictType);
}
```

**方案2: 枚举注册中心**
```java
public class EnumRegistry {
    private static final Map<String, Map<Object, Enum<?>>> REGISTRY = new ConcurrentHashMap<>();

    public static <E extends Enum<E>> void register(Class<E> enumClass) {
        // 延迟加载
    }

    public static Enum<?> getEnum(String enumType, Object code) {
        // 按需获取
    }
}
```

#### 3.2 工具类增强

**新增缓存功能**:
```java
public class MoneyUtil {
    // 使用Caffeine缓存转换结果
    private static final Cache<BigDecimal, String> CACHE = Caffeine.newBuilder()
        .maximumSize(1000)
        .expireAfterWrite(1, TimeUnit.HOURS)
        .build();

    public static String convertToChinese(BigDecimal amount) {
        return CACHE.get(amount, MoneyUtil::doConvert);
    }

    private static String doConvert(BigDecimal amount) {
        // 实际转换逻辑
    }
}
```

**添加参数校验**:
```java
public class MoneyUtil {
    public static String convertToChinese(BigDecimal amount) {
        Preconditions.checkNotNull(amount, "金额不能为空");
        Preconditions.checkArgument(amount.compareTo(BigDecimal.ZERO) >= 0,
                                   "金额不能为负数");
        // ...
    }
}
```

#### 3.3 完善文档

**新建**: `eplus-framework/README.md`
````markdown
# Eplus Framework

Eplus项目的基础框架模块,提供通用工具类、类型处理器、枚举等基础设施。

## 模块说明

### eplus-common
通用工具类和基础组件

#### 核心工具类
- `MoneyUtil`: 金额转换工具(人民币大写、格式化)
- `DateTimeUtil`: 日期时间处理
- `NumberFormatUtil`: 数字格式化
- `ImageUtils`: 图片处理

### eplus-spring-boot-starter-mybatis
MyBatis增强模块

#### TypeHandler
提供40+个JSON类型处理器,支持复杂对象与JSON的自动转换

**使用示例**:
```java
@TableField(typeHandler = JsonAmountTypeHandler.class)
private JsonAmount amount;
```

## 快速开始

### 引入依赖
```xml
<dependency>
    <groupId>com.syj.eplus</groupId>
    <artifactId>eplus-common</artifactId>
    <version>${eplus.version}</version>
</dependency>
```

### 使用示例

#### 金额转换
```java
BigDecimal amount = new BigDecimal("12345.67");
String chinese = MoneyUtil.convertToChinese(amount);
// 输出: 壹万贰仟叁佰肆拾伍元陆角柒分
```
````

---

## 📋 优化检查清单

### 代码质量
- [x] 清理System.out.println
- [x] 合并重复工具类
- [ ] 添加单元测试(覆盖率>80%)
- [ ] 添加Javadoc注释
- [ ] SonarQube扫描通过

### 架构设计
- [ ] TypeHandler基类抽象
- [ ] 模块拆分完成
- [ ] 业务实体下沉
- [ ] 依赖关系清晰

### 性能优化
- [ ] 工具类缓存
- [ ] 枚举延迟加载
- [ ] 性能测试通过

### 文档完善
- [ ] README编写
- [ ] API文档生成
- [ ] 使用示例补充

---

## 📊 预期收益

### 代码质量提升
- **代码行数**: 减少30% (通过基类抽象)
- **重复度**: 降低50%
- **可维护性**: 提升40%

### 性能提升
- **工具类调用**: 提升20%(缓存)
- **枚举加载**: 减少内存占用15%

### 开发效率
- **新TypeHandler开发**: 减少80%代码量
- **工具类查找**: 提升50%(文档+分类)

---

## 🗓️ 实施时间表

| 阶段 | 任务 | 预计时间 | 优先级 |
|------|------|----------|--------|
| 第1周 | 清理调试代码 | 0.5天 | P0 |
| 第1周 | 合并重复工具类 | 1天 | P0 |
| 第1周 | 添加单元测试 | 1.5天 | P1 |
| 第2周 | TypeHandler基类抽象 | 2天 | P1 |
| 第2周 | 改造现有TypeHandler | 2天 | P1 |
| 第3周 | 模块拆分 | 3天 | P1 |
| 第3周 | 业务实体迁移 | 1天 | P2 |
| 第4周 | 枚举优化 | 2天 | P2 |
| 第4周 | 工具类增强 | 2天 | P2 |
| 第5周 | 文档编写 | 2天 | P2 |
| 第5周 | 性能测试 | 1天 | P2 |

**总计**: 约5周(25个工作日)

---

## ⚠️ 风险与应对

### 风险1: TypeHandler改造影响现有功能
**应对**:
- 先完成单元测试覆盖
- 小批量迁移,每次迁移后回归测试
- 保留旧TypeHandler一段时间

### 风险2: 模块拆分影响编译
**应对**:
- 使用特性分支开发
- 分模块逐步迁移
- CI/CD自动化测试

### 风险3: 业务实体迁移破坏依赖
**应对**:
- 使用IDE重构工具
- 先搜索所有引用
- 测试环境验证

---

## 📝 总结

通过本次优化,eplus-framework将:
1. **更清晰**: 职责明确,模块边界清晰
2. **更高效**: 减少重复代码,提升性能
3. **更易用**: 完善文档,统一接口
4. **更可靠**: 单元测试覆盖,质量保证

**核心原则**:
- 框架层只包含通用组件
- 业务逻辑下沉到业务模块
- 接口统一,实现可扩展
- 文档齐全,易于上手
