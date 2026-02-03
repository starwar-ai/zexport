# HSTS配置

<cite>
**本文档引用文件**   
- [YudaoWebSecurityConfigurerAdapter.java](file://yudao-framework/yudao-spring-boot-starter-security/src/main/java/cn/iocoder/yudao/framework/security/config/YudaoWebSecurityConfigurerAdapter.java)
- [application.yaml](file://yudao-server/src/main/resources/application.yaml)
- [application-prod.yaml](file://yudao-server/src/main/resources/application-prod.yaml)
- [SecurityProperties.java](file://yudao-framework/yudao-spring-boot-starter-security/src/main/java/cn/iocoder/yudao/framework/security/config/SecurityProperties.java)
- [README.md](file://README.md)
</cite>

## 目录
1. [引言](#引言)
2. [HSTS头实现配置](#hsts头实现配置)
3. [HSTS指令详解](#hsts指令详解)
4. [HTTPS强制策略部署](#https强制策略部署)
5. [HSTS预加载列表](#hsts预加载列表)
6. [混合内容环境迁移](#混合内容环境迁移)
7. [风险评估与回滚方案](#风险评估与回滚方案)
8. [结论](#结论)

## 引言

HSTS（HTTP Strict Transport Security）是一种重要的Web安全机制，通过强制浏览器使用HTTPS连接来保护网站免受中间人攻击和协议降级攻击。本项目作为企业级ERP管理系统，安全配置至关重要。根据项目文档中的安全检查清单，生产环境必须使用HTTPS协议，这为实施HSTS提供了基础要求。

本系统基于Spring Boot框架构建，采用Spring Security进行安全控制。虽然在现有代码中未直接发现HSTS配置，但系统具备完善的安全框架，为后续实施HSTS提供了良好的基础。系统已包含XSS防护、CSRF禁用、权限控制等安全特性，符合实施HSTS的安全前提条件。

**Section sources**
- [README.md](file://README.md#L453-L467)

## HSTS头实现配置

在Spring Security框架中，HSTS配置通常通过`HttpSecurity`的`headers()`方法实现。虽然当前代码库中未直接配置HSTS，但系统已具备相应的安全配置基础。在`YudaoWebSecurityConfigurerAdapter.java`文件中，可以看到系统已配置了基本的安全头信息：

```java
httpSecurity
    .cors().and()
    .csrf().disable()
    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
    .headers().frameOptions().disable().and()
```

要实现HSTS，需要在此基础上添加HSTS配置。标准的HSTS配置方式如下：

```java
httpSecurity
    .headers()
        .httpStrictTransportSecurity()
            .maxAgeInSeconds(31536000)
            .includeSubDomains(true)
            .preload(true);
```

此配置将向响应头中添加`Strict-Transport-Security`字段，其值为`max-age=31536000; includeSubDomains; preload`。该配置应添加在现有的安全配置链中，确保与其他安全头信息协同工作。

**Section sources**
- [YudaoWebSecurityConfigurerAdapter.java](file://yudao-framework/yudao-spring-boot-starter-security/src/main/java/cn/iocoder/yudao/framework/security/config/YudaoWebSecurityConfigurerAdapter.java#L104-L113)

## HSTS指令详解

### max-age指令

`max-age`指令定义了浏览器应记住该网站只能通过HTTPS访问的时间长度，单位为秒。例如，`max-age=31536000`表示一年（365天）。在此期间，浏览器将自动将所有HTTP请求转换为HTTPS请求，即使用户手动输入HTTP地址。

在实际配置中，建议初始设置较短的`max-age`值（如5分钟），以降低配置错误的风险。确认配置正确后，可逐步增加到更长的时间。对于生产环境，推荐设置为一年（31536000秒），以提供长期保护。

### includeSubDomains指令

`includeSubDomains`指令指示HSTS策略应应用于主域名及其所有子域名。例如，如果主域名为`example.com`，启用此指令后，`api.example.com`、`admin.example.com`等所有子域名都将强制使用HTTPS。

此指令对于拥有多个子服务的企业级应用尤为重要。在本ERP系统中，可能存在API网关、管理后台、文件服务等多个子域名，启用此指令可确保所有子服务都受到HSTS保护。

### preload指令

`preload`指令表示该网站希望被加入浏览器的HSTS预加载列表。预加载列表是浏览器内置的HSTS网站列表，即使用户从未访问过该网站，浏览器也会强制使用HTTPS连接。

提交到预加载列表需要满足严格的要求，包括：
- 必须重定向所有HTTP请求到HTTPS
- 证书必须有效
- `max-age`必须至少为31536000秒
- 必须包含`includeSubDomains`指令
- 必须包含`preload`指令

**Section sources**
- [YudaoWebSecurityConfigurerAdapter.java](file://yudao-framework/yudao-spring-boot-starter-security/src/main/java/cn/iocoder/yudao/framework/security/config/YudaoWebSecurityConfigurerAdapter.java#L109)

## HTTPS强制策略部署

### 部署方案

1. **环境准备**：确保所有生产环境服务器都已正确配置SSL证书，并能通过HTTPS正常访问。

2. **配置实施**：在Spring Security配置中添加HSTS头，建议初始配置为：
   ```
   Strict-Transport-Security: max-age=300; includeSubDomains
   ```

3. **监控与测试**：部署后，通过浏览器开发者工具检查HSTS头是否正确发送，并测试HTTP到HTTPS的自动重定向。

4. **逐步增加max-age**：确认配置正确后，逐步增加`max-age`值，最终达到生产环境要求。

### 安全考虑

1. **证书有效性**：确保SSL证书在HSTS`max-age`期间内有效，否则用户将无法访问网站。

2. **备份访问方式**：在实施HSTS前，确保有备用的管理访问方式，以防配置错误导致服务不可用。

3. **跨域考虑**：如果系统有跨域需求，需确保CORS配置与HSTS兼容。

4. **混合内容**：确保页面中所有资源（图片、脚本、样式表等）都通过HTTPS加载，避免混合内容警告。

**Section sources**
- [application-prod.yaml](file://yudao-server/src/main/resources/application-prod.yaml#L1-L245)
- [application.yaml](file://yudao-server/src/main/resources/application.yaml#L1-L318)

## HSTS预加载列表

### 提交流程

1. **准备阶段**：确保网站满足预加载列表的所有要求，包括正确的重定向、有效的证书和完整的HSTS配置。

2. **提交申请**：访问https://hstspreload.org，输入主域名进行检查。

3. **验证结果**：网站必须通过所有检查项，包括重定向、证书有效性、HSTS头完整性等。

4. **提交请求**：通过验证后，提交预加载请求。

5. **等待审核**：浏览器厂商将审核请求，审核通过后，网站将被加入预加载列表。

6. **发布更新**：新的浏览器版本将包含更新后的预加载列表。

### 要求

1. **重定向要求**：所有HTTP请求必须301重定向到HTTPS。

2. **证书要求**：SSL证书必须由受信任的CA颁发且有效。

3. **HSTS头要求**：
   - `max-age`至少为31536000秒
   - 必须包含`includeSubDomains`指令
   - 必须包含`preload`指令

4. **子域名要求**：所有子域名必须支持HTTPS，或明确不使用（通过DNS记录表明）。

**Section sources**
- [README.md](file://README.md#L462)

## 混合内容环境迁移

### 迁移策略

在混合内容环境下实施HSTS需要谨慎的迁移策略：

1. **评估阶段**：
   - 扫描所有页面，识别通过HTTP加载的资源
   - 记录第三方资源的HTTPS支持情况

2. **准备阶段**：
   - 将内部资源迁移到HTTPS
   - 与第三方服务提供商沟通，获取HTTPS版本的资源
   - 准备备用方案，如资源代理或本地缓存

3. **测试阶段**：
   - 在非生产环境测试HSTS配置
   - 使用`Content-Security-Policy`头来监控混合内容

4. **实施阶段**：
   - 先在部分服务上实施HSTS
   - 逐步扩大范围，直到覆盖所有服务

5. **监控阶段**：
   - 监控错误日志，及时发现混合内容问题
   - 收集用户反馈，解决访问问题

### 注意事项

- 避免在包含大量第三方HTTP资源的页面上直接实施HSTS
- 对于无法立即迁移到HTTPS的资源，考虑使用代理服务器
- 使用`upgrade-insecure-requests` CSP指令作为过渡方案

**Section sources**
- [YudaoWebSecurityConfigurerAdapter.java](file://yudao-framework/yudao-spring-boot-starter-security/src/main/java/cn/iocoder/yudao/framework/security/config/YudaoWebSecurityConfigurerAdapter.java#L115-L145)

## 风险评估与回滚方案

### 风险评估

1. **服务中断风险**：如果SSL证书失效或配置错误，用户将无法访问网站。

2. **配置错误风险**：错误的HSTS配置可能导致合法的HTTP服务被阻止。

3. **第三方依赖风险**：依赖的第三方服务可能不支持HTTPS。

4. **回滚困难**：一旦HSTS生效，浏览器将在`max-age`期间内强制使用HTTPS，即使服务器端移除HSTS头。

### 回滚方案

1. **预防性措施**：
   - 初始设置较短的`max-age`（如5分钟）
   - 确保有备用的管理访问通道
   - 准备应急证书和配置

2. **监控机制**：
   - 实施实时监控，及时发现访问问题
   - 设置告警，当HTTPS服务异常时立即通知

3. **应急响应**：
   - 如果发现问题，立即修复服务器配置
   - 通过其他渠道通知用户可能的访问问题
   - 对于关键服务，考虑临时使用不同的域名

4. **长期策略**：
   - 建立完善的证书管理流程
   - 定期审查HSTS配置
   - 保持与浏览器厂商的沟通，了解预加载列表的更新

**Section sources**
- [SecurityProperties.java](file://yudao-framework/yudao-spring-boot-starter-security/src/main/java/cn/iocoder/yudao/framework/security/config/SecurityProperties.java#L1-L51)

## 结论

HSTS是提升Web应用安全性的关键措施，尤其对于企业级ERP系统这样的敏感应用。虽然当前代码库中未直接配置HSTS，但系统已具备实施HSTS的良好基础。建议按照分阶段的方式实施HSTS，从测试环境开始，逐步过渡到生产环境。

实施过程中应特别注意风险控制，初始设置较短的`max-age`值，并确保有完善的监控和应急响应机制。对于希望加入预加载列表的网站，需满足更严格的要求，并经过充分的测试和验证。

通过正确实施HSTS，可以有效防止中间人攻击和协议降级攻击，提升系统的整体安全性，为用户提供更安全的访问体验。