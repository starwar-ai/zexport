# Eplus Framework

> Eplus项目的基础框架模块,提供通用工具类、类型处理器、枚举等基础设施组件

## 📦 模块说明

### eplus-common
通用基础模块,包含工具类、实体类、枚举、类型处理器等

**目录结构**:
```
eplus-common/
├── annotations/        # 自定义注解
├── config/            # 配置类
│   └── handler/       # MyBatis TypeHandler
├── dict/              # 业务字典常量
├── entity/            # 通用实体类
├── enums/             # 业务枚举
├── util/              # 工具类集合
└── code/              # 代码生成器
```

---

## 🔧 核心功能

### 1. 工具类 (util/)

#### MoneyUtil - 金额工具类
提供金额相关的转换和格式化功能

**功能**:
- 金额转中文大写
- 金额格式化

**使用示例**:
```java
import com.syj.eplus.framework.common.util.MoneyUtil;
import java.math.BigDecimal;

// 金额转中文大写
BigDecimal amount = new BigDecimal("12345.67");
String chinese = MoneyUtil.convertToChinese(amount);
// 输出: 壹万贰仟叁佰肆拾伍元陆角柒分
```

#### DateTimeUtil - 日期时间工具类
日期时间处理相关功能

#### NumberFormatUtil - 数字格式化工具类
数字格式化和转换

#### ImageUtils - 图片处理工具类
图片相关操作

#### CurrencyUtil - 货币工具类
货币转换和计算

---

### 2. MyBatis TypeHandler (config/handler/)

提供40+个JSON类型处理器,支持复杂对象与数据库JSON字段的自动转换

#### AbstractJsonTypeHandler - JSON类型处理器基类
所有JSON TypeHandler的基类,提供统一的序列化/反序列化能力

**使用示例**:
```java
// 1. 继承AbstractJsonTypeHandler
public class JsonAmountListTypeHandler extends AbstractJsonTypeHandler<List<JsonAmount>> {
    public JsonAmountListTypeHandler() {
        super(new TypeReference<List<JsonAmount>>() {});
    }
}

// 2. 在实体类中使用
@TableField(typeHandler = JsonAmountListTypeHandler.class)
private List<JsonAmount> amounts;
```

#### 常用TypeHandler列表

| TypeHandler | 说明 | 使用场景 |
|-------------|------|----------|
| JsonAmountTypeHandler | JSON金额对象 | 金额数据存储 |
| JsonAmountListTypeHandler | JSON金额列表 | 多个金额数据 |
| JsonFileTypeHandler | JSON文件对象 | 文件信息存储 |
| JsonFileListTypeHandler | JSON文件列表 | 附件列表 |
| LongListTypeHandler | Long列表 | ID列表存储 |
| StringListTypeHandler | String列表 | 字符串列表 |
| JsonObjectTypeHandler | 通用JSON对象 | 复杂对象存储 |

---

### 3. 业务枚举 (enums/)

提供70+个业务枚举类,涵盖各个业务领域

**主要枚举**:
```java
BooleanEnum              // 布尔枚举
BusinessTypeEnum         // 业务类型
BusinessSubjectTypeEnum  // 业务主体类型
AuditStatusEnum          // 审核状态
ApprovalStatusEnum       // 审批状态
...
```

**使用示例**:
```java
import com.syj.eplus.framework.common.enums.BooleanEnum;

// 获取枚举值
BooleanEnum bool = BooleanEnum.YES;
Integer code = bool.getCode();  // 1
String label = bool.getLabel(); // "是"

// 根据code获取枚举
BooleanEnum found = BooleanEnum.fromCode(1);
```

---

### 4. 通用实体 (entity/)

提供可复用的通用实体类

**主要实体**:
```java
BaseData            // 基础数据
SimpleData          // 简单数据
SimpleFile          // 文件信息
CompanyPath         // 公司路径
JsonAmount          // JSON金额
JsonWeight          // JSON重量
ChangeRecord        // 变更记录
...
```

**使用示例**:
```java
import com.syj.eplus.framework.common.entity.SimpleFile;

// 文件信息
SimpleFile file = new SimpleFile();
file.setFileId(1L);
file.setFileName("contract.pdf");
file.setFileUrl("http://...");
```

---

### 5. 业务字典 (dict/)

业务常量字典定义

**主要字典**:
```java
BusinessNameDict        // 业务名称
CalculationDict         // 计算方式
CommonCurrencyDict      // 通用货币
SaleContractDict        // 销售合同
...
```

---

## 🚀 快速开始

### 引入依赖

```xml
<dependency>
    <groupId>com.syj.eplus</groupId>
    <artifactId>eplus-common</artifactId>
    <version>${eplus.version}</version>
</dependency>
```

### 完整使用示例

```java
import com.syj.eplus.framework.common.util.MoneyUtil;
import com.syj.eplus.framework.common.entity.JsonAmount;
import com.syj.eplus.framework.common.enums.BooleanEnum;
import com.syj.eplus.framework.common.config.handler.JsonAmountTypeHandler;

import java.math.BigDecimal;

public class Example {

    static void main(String[] args) {
        // 1. 使用工具类
        BigDecimal amount = new BigDecimal("12345.67");
        String chineseAmount = MoneyUtil.convertToChinese(amount);
        System.out.println(chineseAmount);

        // 2. 使用枚举
        BooleanEnum isActive = BooleanEnum.YES;
        if (isActive == BooleanEnum.YES) {
            System.out.println("激活状态");
        }

        // 3. 使用实体
        JsonAmount jsonAmount = new JsonAmount();
        jsonAmount.setAmount(amount);
        jsonAmount.setCurrency("CNY");
    }
}

// 4. 在实体类中使用TypeHandler
@Data
@TableName("t_order")
public class OrderDO {

    @TableId
    private Long id;

    // 使用JSON类型处理器
    @TableField(typeHandler = JsonAmountTypeHandler.class)
    private JsonAmount totalAmount;

    @TableField(typeHandler = JsonFileListTypeHandler.class)
    private List<SimpleFile> attachments;
}
```

---

## 📝 开发规范

### 新增TypeHandler规范

1. **继承AbstractJsonTypeHandler**
```java
public class YourTypeHandler extends AbstractJsonTypeHandler<YourType> {
    public YourTypeHandler() {
        super(new TypeReference<YourType>() {});
    }
}
```

2. **命名规范**
- 格式: `Json{类型名}TypeHandler`
- 列表: `Json{类型名}ListTypeHandler`

3. **注册使用**
```java
@TableField(typeHandler = YourTypeHandler.class)
private YourType field;
```

### 新增工具类规范

1. **工具类特点**
- 全部使用static方法
- 无状态设计
- 参数非空校验
- 完善的Javadoc注释

2. **命名规范**
- 以`Util`结尾
- 功能明确,单一职责

3. **示例模板**
```java
/**
 * XXX工具类
 *
 * @author your-name
 */
public class XxxUtil {

    private XxxUtil() {
        // 私有构造函数,防止实例化
    }

    /**
     * 方法说明
     *
     * @param param 参数说明
     * @return 返回值说明
     */
    public static ReturnType methodName(ParamType param) {
        // 参数校验
        if (param == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // 业务逻辑
        return result;
    }
}
```

---

## 🧪 单元测试

### 运行测试

```bash
# 运行所有测试
cd eplus-framework/eplus-common
mvn test

# 运行特定测试类
mvn test -Dtest=MoneyUtilTest

# 生成测试覆盖率报告
mvn clean test jacoco:report
```

### 测试覆盖率目标
- **工具类**: >90%
- **TypeHandler**: >80%
- **整体**: >80%

---

## 📊 性能优化建议

### 1. 工具类缓存
对于频繁调用的转换方法,考虑添加缓存:

```java
public class MoneyUtil {
    private static final Cache<BigDecimal, String> CACHE =
        Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    public static String convertToChinese(BigDecimal amount) {
        return CACHE.get(amount, MoneyUtil::doConvert);
    }
}
```

### 2. TypeHandler优化
- 使用对象池复用ObjectMapper
- 避免频繁创建TypeReference

---

## ⚠️ 注意事项

### 1. JSON序列化
- 所有JSON序列化统一使用`JsonUtils`
- 避免直接使用ObjectMapper

### 2. 类型转换
- BigDecimal运算注意精度
- 日期转换注意时区

### 3. 异常处理
- 参数校验抛出IllegalArgumentException
- 业务异常抛出自定义异常

---

## 🔗 相关文档

- [优化方案](./OPTIMIZATION_PLAN.md)
- [API文档](./API.md) (待补充)
- [变更日志](./CHANGELOG.md) (待补充)

---

## 🤝 贡献指南

欢迎贡献代码!

1. Fork本仓库
2. 创建特性分支
3. 提交代码(包含单元测试)
4. 提交Pull Request

---

## 📄 许可证

MIT License

---

## 📮 联系方式

技术支持: [待补充]
