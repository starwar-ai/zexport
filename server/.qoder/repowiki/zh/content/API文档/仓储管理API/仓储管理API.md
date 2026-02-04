# 仓储管理API

<cite>
**本文档引用的文件**
- [IStockApi.java](file://eplus-module-wms/eplus-module-wms-api/src/main/java/com/syj/eplus/module/wms/api/stock/IStockApi.java)
- [IStockNoticeApi.java](file://eplus-module-wms/eplus-module-wms-api/src/main/java/com/syj/eplus/module/wms/api/stockNotice/IStockNoticeApi.java)
- [IWarehouseApi.java](file://eplus-module-wms/eplus-module-wms-api/src/main/java/com/syj/eplus/module/wms/api/warehouse/IWarehouseApi.java)
- [StockDTO.java](file://eplus-module-wms/eplus-module-wms-api/src/main/java/com/syj/eplus/module/wms/api/stock/dto/StockDTO.java)
- [StockNoticeRespDTO.java](file://eplus-module-wms/eplus-module-wms-api/src/main/java/com/syj/eplus/module/wms/api/stockNotice/dto/StockNoticeRespDTO.java)
- [WarehouseDTO.java](file://eplus-module-wms/eplus-module-wms-api/src/main/java/com/syj/eplus/module/wms/api/warehouse/dto/WarehouseDTO.java)
- [ErrorCodeConstants.java](file://eplus-module-wms/eplus-module-wms-api/src/main/java/com/syj/eplus/module/wms/enums/ErrorCodeConstants.java)
</cite>

## 目录
1. [简介](#简介)
2. [API概览](#api概览)
3. [库存管理API](#库存管理api)
4. [出入库通知API](#出入库通知api)
5. [仓库管理API](#仓库管理api)
6. [错误码字典](#错误码字典)
7. [安全要求](#安全要求)
8. [调用示例](#调用示例)
9. [集成注意事项](#集成注意事项)

## 简介
仓储管理API为系统提供库存、出入库通知和仓库管理的核心功能。本API文档遵循OpenAPI/Swagger标准，详细描述了所有RESTful接口的端点规范、参数约束、响应结构和状态码。通过这些API，系统能够实现库存的精确管理、出入库流程的自动化以及仓库信息的统一维护。

## API概览
仓储管理模块包含三个核心API接口：
- **库存API (IStockApi)**: 管理库存明细、锁定库存、查询库存等操作
- **出入库通知API (IStockNoticeApi)**: 处理入库通知单、出库通知单的创建和查询
- **仓库管理API (IWarehouseApi)**: 管理仓库信息的创建、查询和维护

这些API共同构成了仓储管理的核心功能，支持采购、销售、生产等业务场景的库存管理需求。

## 库存管理API
库存管理API提供了对库存明细的全面管理功能，包括库存查询、锁定、释放和状态更新。

### 核心功能
库存API支持以下核心操作：
- 采购合同相关的库存处理
- 批量锁定和释放库存
- 库存查询和验证
- 库存成本计算

**API接口定义**
```java
public interface IStockApi {
    // 采购合同下单后的入库单&库存处理
    Boolean afterCreatePurchaseOrder(BillSaveReqVO billSaveReqVO);
    
    // 拒绝/结案采购合同后的库存处理
    Boolean afterCanclePurchaseOrder(Long purchaseContractId);
    
    // 批量锁定库存
    Boolean batchLockStock(List<StockLockSaveReqVO> stockLockSaveReqVOList);
    
    // 释放锁定的库存
    Boolean cancelStockLock(String saleContractCode, Collection<Long> itemIdList, Integer sourceOrderType);
    
    // 采购合同生产完成后的库存转移
    Boolean completePurchaseOrder(Long purchaseContractId, Map<String,Integer> usedQuantityMap, Boolean domesticSaleFlag, List<String> batchCodeList);
}
```

#### 库存查询接口
提供多种库存查询方式，满足不同业务场景的需求。

**批量查询可用库存**
```java
Map<String, Integer> queryTotalStock(List<QueryStockReqVO> queryStockReqVOList);
```
- **功能**: 批量查询指定条件的可用库存数量
- **参数**: QueryStockReqVO列表，包含产品编码、仓库等查询条件
- **返回值**: 以产品编码为键的可用库存数量映射

**根据销售合同查询库存**
```java
List<StockDTO> getStockDTOBySaleContractCodes(List<String> saleContractCodes);
```
- **功能**: 根据销售合同编号列表获取关联的库存明细
- **参数**: 销售合同编号列表
- **返回值**: 库存明细列表

**库存明细数据结构**
```java
@Schema(description = "管理后台 - 仓储管理-库存明细 Response VO")
@Data
public class StockDTO {
    @Schema(description = "主键")
    private Long id;
    
    @Schema(description = "批次号")
    private String batchCode;
    
    @Schema(description = "产品编码")
    private String skuCode;
    
    @Schema(description = "产品中文名称")
    private String skuName;
    
    @Schema(description = "入库数量")
    private Integer initQuantity;
    
    @Schema(description = "出库数量")
    private Integer usedQuantity;
    
    @Schema(description = "锁定数量")
    private Integer lockQuantity;
    
    @Schema(description = "可用数量")
    private Integer availableQuantity;
    
    @Schema(description = "采购合同号")
    private String purchaseContractCode;
    
    @Schema(description = "销售合同号")
    private String saleContractCode;
    
    @Schema(description = "仓库名称")
    private String warehouseName;
}
```

**库存状态计算规则**
- **可用数量** = 入库数量 - 出库数量 - 锁定数量
- **剩余库存** = 入库数量 - 出库数量
- **在制数量**: 采购合同下单时的库存，尚未实际入库

**Section sources**
- [IStockApi.java](file://eplus-module-wms/eplus-module-wms-api/src/main/java/com/syj/eplus/module/wms/api/stock/IStockApi.java#L15-L245)
- [StockDTO.java](file://eplus-module-wms/eplus-module-wms-api/src/main/java/com/syj/eplus/module/wms/api/stock/dto/StockDTO.java#L1-L167)

## 出入库通知API
出入库通知API负责管理入库通知单和出库通知单的创建、查询和状态管理。

### 入库通知单管理
处理采购合同转入库通知单的业务流程。

**创建入库通知单**
```java
CreatedResponse toStockNotice(StockNoticeReqVO stockNoticeReqVO);
```
- **HTTP方法**: POST
- **端点**: `/api/wms/stock-notice/to-stock-notice`
- **请求体**: StockNoticeReqVO
- **响应**: CreatedResponse，包含创建的入库通知单ID和编号
- **状态码**:
  - 200: 创建成功
  - 400: 请求参数无效
  - 404: 采购合同不存在
  - 500: 服务器内部错误

**根据采购合同查询入库通知单**
```java
List<StockNoticeRespDTO> GetStockNoticeListByPurchaseCode(String code);
```
- **功能**: 根据采购合同编号查询关联的入库通知单
- **参数**: 采购合同编号
- **返回值**: 入库通知单响应DTO列表

**入库通知单数据结构**
```java
@Data
public class StockNoticeRespDTO {
    @Schema(description = "主键")
    private Long id;
    
    @Schema(description = "单号")
    private String code;
    
    @Schema(description = "通知类型 1-入库通知单、2-出库通知单")
    private Integer noticeType;
    
    @Schema(description = "通知单状态 1-未转 2-已转 3-作废")
    private Integer noticeStatus;
    
    @Schema(description = "采购合同号")
    private String purchaseContractCode;
    
    @Schema(description = "销售合同号")
    private String saleContractCode;
    
    @Schema(description = "仓库名称")
    private String warehouseName;
    
    @Schema(description = "预计到货日期")
    private LocalDateTime expectDate;
    
    @Schema(description = "明细列表")
    private List<StockNoticeItemRespDTO> children;
}
```

### 拉柜通知单管理
处理出库相关的拉柜通知单。

**创建拉柜通知单**
```java
CreatedResponse createNotice(StockNoticeSaveReqDTO stockNoticeSaveReqDTO);
```
- **功能**: 创建拉柜通知单（出库通知单）
- **参数**: StockNoticeSaveReqDTO
- **返回值**: CreatedResponse

**分页查询通知单**
```java
PageResult<StockNoticeRespDTO> getNoticePage(StockNoticePageReqDTO pageReqDTO);
```
- **功能**: 分页查询入(出)库通知单
- **参数**: StockNoticePageReqDTO，包含分页信息和查询条件
- **返回值**: PageResult<StockNoticeRespDTO>

**Section sources**
- [IStockNoticeApi.java](file://eplus-module-wms/eplus-module-wms-api/src/main/java/com/syj/eplus/module/wms/api/stockNotice/IStockNoticeApi.java#L15-L57)
- [StockNoticeRespDTO.java](file://eplus-module-wms/eplus-module-wms-api/src/main/java/com/syj/eplus/module/wms/api/stockNotice/dto/StockNoticeRespDTO.java#L1-L116)

## 仓库管理API
仓库管理API负责仓库信息的创建、查询和维护。

### 仓库信息管理
提供对仓库资料的CRUD操作。

**创建供应商仓库**
```java
Boolean createVenderWareHouse(WarehouseDTO warehouseDTO);
```
- **功能**: 供应商审核通过后，自动创建供应商仓库
- **参数**: WarehouseDTO
- **返回值**: 操作是否成功

**根据供应商编号删除仓库**
```java
void deleteWarehouseByVenderCode(String venderCode);
```
- **功能**: 根据供应商编号删除所有虚拟仓库
- **参数**: 供应商编码
- **返回值**: 无

**批量查询仓库信息**
```java
List<WarehouseDTO> selectBatchIds(List<Long> ids);
```
- **功能**: 根据仓库主键批量查询仓库信息
- **参数**: 仓库ID列表
- **返回值**: 仓库DTO列表

**仓库数据结构**
```java
@Data
public class WarehouseDTO {
    /**
     * 主键
     */
    private Long id;
    
    /**
     * 仓库编码
     */
    private String code;
    
    /**
     * 仓库名称
     */
    private String name;
    
    /**
     * 仓库地址
     */
    private String address;
    
    /**
     * 启用标识 0-否 1-是
     */
    private Integer enableFlag;
    
    /**
     * 供应仓标识 0-否 1-是
     */
    private Integer venderFlag;
    
    /**
     * 供应仓类型 1-在制仓 2-虚拟仓
     */
    private Integer venderWmsType;
    
    /**
     * 供应商编码
     */
    private String venderCode;
    
    /**
     * 供应商名称
     */
    private String venderName;
}
```

**Section sources**
- [IWarehouseApi.java](file://eplus-module-wms/eplus-module-wms-api/src/main/java/com/syj/eplus/module/wms/api/warehouse/IWarehouseApi.java#L12-L58)
- [WarehouseDTO.java](file://eplus-module-wms/eplus-module-wms-api/src/main/java/com/syj/eplus/module/wms/api/warehouse/dto/WarehouseDTO.java#L1-L64)

## 错误码字典
仓储管理模块定义了详细的错误码，用于标识各种业务异常情况。

### 仓库管理错误码
| 错误码 | 错误消息 | 说明 |
|--------|---------|------|
| 1006001001 | 仓库信息不存在 | 请求的仓库ID不存在 |
| 1006001002 | 仓库名称已存在 | 创建仓库时名称重复 |
| 1006001003 | 供应商仓库不允许删除 | 试图删除供应商仓库 |
| 1006001004 | 仓库没有默认值 | 系统中没有设置默认仓库 |
| 1006001005 | 仓库有多个默认值 | 系统中有多个仓库被标记为默认 |

### 入库通知单错误码
| 错误码 | 错误消息 | 说明 |
|--------|---------|------|
| 1006002001 | 入库通知单不存在 | 请求的入库通知单ID不存在 |
| 1006003001 | 入库通知单-明细不存在 | 入库通知单的某个明细不存在 |
| 1006003002 | 入库通知单-明细不可为空 | 入库通知单必须包含至少一个明细 |

### 出库通知单错误码
| 错误码 | 错误消息 | 说明 |
|--------|---------|------|
| 1006004001 | 出库通知单不存在 | 请求的出库通知单ID不存在 |
| 1006004002 | 通知单据正在转单中，请检查 | 出库通知单正在处理中，不能重复操作 |
| 1006005001 | 出库通知单-明细不存在 | 出库通知单的某个明细不存在 |
| 1006005002 | 单据明细均已出库 | 出库通知单的所有明细都已完成出库 |

### 库存管理错误码
| 错误码 | 错误消息 | 说明 |
|--------|---------|------|
| 1006010001 | 库存明细不存在 | 请求的库存ID不存在 |
| 1006010002 | 库存明细不足，请检查 | 库存数量不足以满足出库需求 |
| 1006010003 | 销售合同不存在 | 关联的销售合同不存在 |
| 1006010006 | 导入编号异常，请关闭页面，重新导入 | 库存导入时编号异常 |

### 错误处理建议
1. **客户端处理**: 捕获API返回的错误码，根据错误消息向用户展示友好的提示
2. **重试机制**: 对于临时性错误（如网络问题），实现指数退避重试机制
3. **日志记录**: 记录详细的错误信息，便于问题排查
4. **用户反馈**: 提供清晰的错误描述，帮助用户理解问题原因并采取相应措施

**Section sources**
- [ErrorCodeConstants.java](file://eplus-module-wms/eplus-module-wms-api/src/main/java/com/syj/eplus/module/wms/enums/ErrorCodeConstants.java#L10-L98)

## 安全要求
仓储管理API遵循系统的安全规范，确保数据访问的安全性和完整性。

### 身份验证
所有API端点都需要进行身份验证，使用JWT（JSON Web Token）进行认证：
- **认证方式**: Bearer Token
- **请求头**: `Authorization: Bearer <token>`
- **Token有效期**: 2小时
- **刷新机制**: 支持refresh token机制

### 数据访问权限
基于RBAC（基于角色的访问控制）模型实现细粒度的权限控制：
- **仓库管理员**: 可以管理所有仓库信息
- **采购员**: 只能查看和操作自己负责的采购合同相关的库存
- **销售员**: 只能查看和操作自己负责的销售合同相关的库存
- **财务人员**: 可以查看库存成本信息，但不能修改库存

### 数据安全
- **敏感数据加密**: 库存价格等敏感信息在传输和存储时进行加密
- **审计日志**: 所有库存变更操作都会记录详细的审计日志
- **防重放攻击**: 使用请求时间戳和签名机制防止重放攻击

## 调用示例
提供实际的API调用示例，帮助开发者快速集成。

### 查询库存示例
```http
POST /api/wms/stock/query-total-stock
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

[
  {
    "skuCode": "SKU001",
    "warehouseId": 1001
  },
  {
    "skuCode": "SKU002",
    "warehouseId": 1001
  }
]
```

**响应示例**
```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "SKU001": 100,
    "SKU002": 50
  }
}
```

### 创建入库通知单示例
```http
POST /api/wms/stock-notice/to-stock-notice
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

{
  "purchaseContractId": 12345,
  "warehouseId": 1001,
  "expectDate": "2024-12-01T00:00:00"
}
```

**响应示例**
```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "id": 67890,
    "code": "RN20241201001"
  }
}
```

### 锁定库存示例
```http
POST /api/wms/stock/batch-lock-stock
Content-Type: application/json
Authorization: Bearer <your-jwt-token>

[
  {
    "stockId": 10001,
    "sourceOrderId": 20001,
    "sourceOrderType": 1,
    "lockQuantity": 50,
    "remark": "销售合同锁库"
  }
]
```

## 集成注意事项
在集成仓储管理API时需要注意以下事项：

### 事务一致性
- **库存变更**: 库存的增减操作必须保证事务一致性，避免出现数据不一致
- **通知单状态**: 入库通知单的状态变更需要与库存操作保持同步

### 性能优化
- **批量操作**: 尽量使用批量接口（如批量查询库存）减少网络开销
- **缓存策略**: 对于频繁查询但不常变更的数据（如仓库信息），建议在客户端实现缓存
- **分页查询**: 对于大量数据的查询，使用分页接口避免一次性加载过多数据

### 异常处理
- **网络异常**: 实现重试机制处理网络抖动
- **业务异常**: 根据错误码进行针对性处理，避免简单地将所有错误都视为系统错误
- **数据校验**: 在调用API前进行数据校验，减少无效请求

### 最佳实践
1. **幂等性设计**: 对于创建类操作，确保接口的幂等性，避免重复操作导致数据异常
2. **资源清理**: 及时释放不再需要的锁库，避免资源长时间占用
3. **监控告警**: 对关键API调用进行监控，设置合理的告警阈值
4. **版本管理**: 关注API版本更新，及时调整集成代码以适应接口变更