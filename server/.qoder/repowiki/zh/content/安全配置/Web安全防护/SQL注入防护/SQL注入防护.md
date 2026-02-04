# SQL注入防护

<cite>
**本文档引用的文件**
- [CustMapper.java](file://eplus-module-crm/eplus-module-crm-biz/src/main/java/com/syj/eplus/module/crm/dal/mysql/cust/CustMapper.java)
- [CustMapper.xml](file://eplus-module-crm/eplus-module-crm-biz/src/main/resources/mapper/CustMapper.xml)
- [ShipmentMapper.java](file://eplus-module-dms/eplus-module-dms-biz/src/main/java/com/syj/eplus/module/dms/dal/mysql/shipment/ShipmentMapper.java)
- [ShipmentItemMapper.xml](file://eplus-module-dms/eplus-module-dms-biz/src/main/resources/mapper/ShipmentItemMapper.xml)
- [LambdaQueryWrapperX.java](file://yudao-framework/yudao-spring-boot-starter-mybatis/src/main/java/cn/iocoder/yudao/framework/mybatis/core/query/LambdaQueryWrapperX.java)
- [BaseMapperX.java](file://yudao-framework/yudao-spring-boot-starter-mybatis/src/main/java/cn/iocoder/yudao/framework/mybatis/core/mapper/BaseMapperX.java)
</cite>

## 目录
1. [引言](#引言)
2. [预编译语句的使用方法和优势](#预编译语句的使用方法和优势)
3. [MyBatis框架的安全使用规范](#mybatis框架的安全使用规范)
4. [JPA/Hibernate安全配置指南](#jpa/hibernate安全配置指南)
5. [ORM框架的自动转义机制](#orm框架的自动转义机制)
6. [动态SQL构建的安全实践](#动态sql构建的安全实践)
7. [SQL注入漏洞的检测工具和测试方法](#sql注入漏洞的检测工具和测试方法)
8. [结论](#结论)

## 引言

SQL注入是一种常见的网络安全漏洞，攻击者通过在SQL查询中插入恶意代码来操纵数据库。在本项目中，我们采用了多种技术手段来防止SQL注入攻击，包括预编译语句、MyBatis框架的安全使用以及ORM框架的自动转义机制等。本文档将详细介绍这些防护措施，并为开发者提供最佳实践指南。

## 预编译语句的使用方法和优势

预编译语句（Prepared Statements）是防止SQL注入的有效方法之一。它通过将SQL语句与参数分离，确保用户输入的数据不会被解释为SQL代码的一部分。

### 使用方法

在Java中，可以使用`PreparedStatement`接口来实现预编译语句。例如：

```java
String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
PreparedStatement pstmt = connection.prepareStatement(sql);
pstmt.setString(1, username);
pstmt.setString(2, password);
ResultSet rs = pstmt.executeQuery();
```

在这个例子中，`?` 是占位符，实际的值通过`setString`方法设置，这样可以确保输入数据被正确地转义。

### 优势

- **安全性**：预编译语句能够有效防止SQL注入攻击，因为参数不会被当作SQL代码执行。
- **性能**：预编译语句可以在数据库服务器上进行缓存，从而提高查询效率。
- **可读性**：代码更加清晰，易于维护。

**Section sources**
- [LambdaQueryWrapperX.java](file://yudao-framework/yudao-spring-boot-starter-mybatis/src/main/java/cn/iocoder/yudao/framework/mybatis/core/query/LambdaQueryWrapperX.java#L21-L25)

## MyBatis框架的安全使用规范

MyBatis是一个流行的持久层框架，支持自定义SQL、存储过程和高级映射。为了确保安全，我们需要遵循一些最佳实践。

### #{} 和 ${} 的正确使用场景

- **#{}**：用于预编译参数，推荐使用。它可以防止SQL注入，因为参数会被自动转义。
- **${}**：用于字符串替换，不推荐用于用户输入。如果必须使用，需要确保输入已经过严格的验证和清理。

#### 示例

```xml
<!-- 推荐使用 #{} -->
<select id="selectUser" resultType="User">
    SELECT * FROM users WHERE username = #{username}
</select>

<!-- 不推荐使用 ${}，除非输入已验证 -->
<select id="selectUser" resultType="User">
    SELECT * FROM users WHERE username = '${username}'
</select>
```

### 安全使用建议

- 尽量使用`#{}`来传递参数，避免使用`${}`。
- 对于动态表名或列名，应使用白名单机制，限制可选范围。
- 在使用`<if>`、`<choose>`等标签时，确保逻辑正确，避免意外的SQL拼接。

**Section sources**
- [CustMapper.xml](file://eplus-module-crm/eplus-module-crm-biz/src/main/resources/mapper/CustMapper.xml#L120-L184)
- [ShipmentItemMapper.xml](file://eplus-module-dms/eplus-module-dms-biz/src/main/resources/mapper/ShipmentItemMapper.xml#L12-L17)

## JPA/Hibernate安全配置指南

JPA（Java Persistence API）和Hibernate是广泛使用的ORM框架，它们提供了丰富的功能来简化数据库操作。为了确保安全，我们需要进行适当的配置。

### 配置建议

- **启用SQL日志**：通过配置`hibernate.show_sql`和`hibernate.format_sql`，可以查看生成的SQL语句，便于调试和审计。
- **使用命名查询**：定义命名查询可以减少SQL注入的风险，因为查询语句在编译时就已经确定。
- **限制查询结果**：使用`setMaxResults`方法限制查询返回的结果数量，防止大规模数据泄露。

#### 示例配置

```properties
# 启用SQL日志
hibernate.show_sql=true
hibernate.format_sql=true

# 命名查询示例
@NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username")
```

### 安全注解

- 使用`@Transactional`注解确保事务的一致性。
- 使用`@Entity`和`@Table`注解明确指定实体和表的关系，避免歧义。

**Section sources**
- [BaseMapperX.java](file://yudao-framework/yudao-spring-boot-starter-mybatis/src/main/java/cn/iocoder/yudao/framework/mybatis/core/mapper/BaseMapperX.java#L63-L99)

## ORM框架的自动转义机制

ORM（Object-Relational Mapping）框架如MyBatis、JPA等，通常具备自动转义机制，能够有效地防止SQL注入攻击。

### 自动转义原理

- **参数化查询**：ORM框架会将用户输入作为参数传递给SQL语句，而不是直接拼接到SQL字符串中。
- **类型安全**：通过类型检查，确保输入数据符合预期的数据类型，减少错误和潜在的安全风险。

### 实现方式

- **MyBatis**：使用`#{}`占位符，MyBatis会自动处理参数的转义。
- **JPA**：使用JPQL（Java Persistence Query Language），JPA会自动处理查询中的参数。

#### 示例

```java
// MyBatis
@Select("SELECT * FROM users WHERE username = #{username}")
List<User> findUserByUsername(@Param("username") String username);

// JPA
@Query("SELECT u FROM User u WHERE u.username = :username")
List<User> findUserByUsername(@Param("username") String username);
```

**Section sources**
- [LambdaQueryWrapperX.java](file://yudao-framework/yudao-spring-boot-starter-mybatis/src/main/java/cn/iocoder/yudao/framework/mybatis/core/query/LambdaQueryWrapperX.java#L21-L25)
- [BaseMapperX.java](file://yudao-framework/yudao-spring-boot-starter-mybatis/src/main/java/cn/iocoder/yudao/framework/mybatis/core/mapper/BaseMapperX.java#L63-L99)

## 动态SQL构建的安全实践

动态SQL构建是应用程序中常见的需求，但如果不当使用，可能会引入SQL注入漏洞。以下是一些安全实践。

### 使用条件构造器

MyBatis Plus提供了`LambdaQueryWrapperX`等条件构造器，可以安全地构建动态查询。

#### 示例

```java
LambdaQueryWrapperX<User> queryWrapper = new LambdaQueryWrapperX<>();
if (StringUtils.hasText(username)) {
    queryWrapper.eq(User::getUsername, username);
}
if (age != null) {
    queryWrapper.gt(User::getAge, age);
}
List<User> users = userMapper.selectList(queryWrapper);
```

### 避免字符串拼接

尽量避免使用字符串拼接来构建SQL语句，特别是当涉及到用户输入时。

#### 不推荐的做法

```java
String sql = "SELECT * FROM users WHERE username = '" + username + "'";
```

#### 推荐的做法

```java
String sql = "SELECT * FROM users WHERE username = ?";
PreparedStatement pstmt = connection.prepareStatement(sql);
pstmt.setString(1, username);
```

**Section sources**
- [LambdaQueryWrapperX.java](file://yudao-framework/yudao-spring-boot-starter-mybatis/src/main/java/cn/iocoder/yudao/framework/mybatis/core/query/LambdaQueryWrapperX.java#L21-L25)

## SQL注入漏洞的检测工具和测试方法

为了确保系统的安全性，我们需要使用各种工具和方法来检测和预防SQL注入漏洞。

### 检测工具

- **OWASP ZAP**：一个开源的Web应用安全扫描器，可以自动检测SQL注入等常见漏洞。
- **Burp Suite**：一个功能强大的Web应用安全测试平台，支持手动和自动化测试。
- **SQLMap**：一个专门用于检测和利用SQL注入漏洞的工具。

### 测试方法

- **黑盒测试**：模拟攻击者的行为，尝试注入恶意SQL代码，观察系统反应。
- **白盒测试**：审查源代码，查找潜在的SQL注入点，确保所有用户输入都经过适当的验证和转义。
- **渗透测试**：邀请专业的安全团队进行全面的安全评估，发现并修复潜在的安全问题。

### 最佳实践

- **输入验证**：对所有用户输入进行严格的验证，确保其符合预期的格式和类型。
- **最小权限原则**：数据库账户应具有最小必要的权限，避免使用高权限账户执行普通操作。
- **定期更新**：保持框架和库的最新版本，及时应用安全补丁。

**Section sources**
- [CustMapper.java](file://eplus-module-crm/eplus-module-crm-biz/src/main/java/com/syj/eplus/module/crm/dal/mysql/cust/CustMapper.java#L119-L202)
- [ShipmentMapper.java](file://eplus-module-dms/eplus-module-dms-biz/src/main/java/com/syj/eplus/module/dms/dal/mysql/shipment/ShipmentMapper.java#L45-L59)

## 结论

SQL注入是Web应用中常见的安全威胁，但通过采用预编译语句、合理使用MyBatis框架、配置JPA/Hibernate以及利用ORM框架的自动转义机制，我们可以有效防止此类攻击。此外，定期使用专业的检测工具和测试方法，可以帮助我们及时发现和修复潜在的安全漏洞。希望本文档能为开发者提供有价值的指导，确保系统的安全性和稳定性。