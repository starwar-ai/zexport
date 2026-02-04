# API文档

<cite>
**本文档引用文件**   
- [CustApi.java](file://eplus-module-crm/eplus-module-crm-api/src/main/java/com/syj/eplus/module/crm/api/cust/CustApi.java)
- [PurchaseContractApi.java](file://eplus-module-scm/eplus-module-scm-api/src/main/java/com/syj/eplus/module/scm/api/purchasecontract/PurchaseContractApi.java)
- [SaleContractApi.java](file://eplus-module-sms/eplus-module-sms-api/src/main/java/com/syj/eplus/module/sms/api/SaleContractApi.java)
- [IStockApi.java](file://eplus-module-wms/eplus-module-wms-api/src/main/java/com/syj/eplus/module/wms/api/stock/IStockApi.java)
- [PaymentApi.java](file://eplus-module-fms/eplus-module-fms-api/src/main/java/com/syj/eplus/module/fms/api/payment/api/payment/PaymentApi.java)
- [ShipmentApi.java](file://eplus-module-dms/eplus-module-dms-api/src/main/java/com/syj/eplus/module/dms/enums/api/ShipmentApi.java)
- [SkuApi.java](file://eplus-module-pms/eplus-module-pms-api/src/main/java/com/syj/eplus/module/pms/api/sku/SkuApi.java)
- [LoanAppApi.java](file://eplus-module-oa/eplus-module-oa-api/src/main/java/com/syj/eplus/module/oa/api/LoanAppApi.java)
- [CrmCategoryController.java](file://eplus-module-crm/eplus-module-crm-biz/src/main/java/com/syj/eplus/module/crm/controller/admin/category/CrmCategoryController.java)
- [CustController.java](file://eplus-module-crm/eplus-module-crm-biz/src/main/java/com/syj/eplus/module/crm/controller/admin/cust/CustController.java)
- [ShipmentController.java](file://eplus-module-dms/eplus-module-dms-biz/src/main/java/com/syj/eplus/module/dms/controller/admin/shipment/ShipmentController.java)
- [PaymentAppApi.java](file://eplus-module-oa/eplus-module-oa-api/src/main/java/com/syj/eplus/module/oa/api/PaymentAppApi.java)
- [ReimbApi.java](file://eplus-module-oa/eplus-module-oa-api/src/main/java/com/syj/eplus/module/oa/api/ReimbApi.java)
- [TravelAppApi.java](file://eplus-module-oa/eplus-module-oa-api/src/main/java/com/syj/eplus/module/oa/api/TravelAppApi.java)
- [DictSubjectApi.java](file://eplus-module-oa/eplus-module-oa-api/src/main/java/com/syj/eplus/module/oa/api/DictSubjectApi.java)
- [RepayAppApi.java](file://eplus-module-oa/eplus-module-oa-api/src/main/java/com/syj/eplus/module/oa/api/RepayAppApi.java)
- [CodeGeneratorApi.java](file://eplus-module-infra/eplus-module-infra-api/src/main/java/com/syj/eplus/module/infra/api/CodeGeneratorApi.java)
- [CompanyApi.java](file://eplus-module-infra/eplus-module-infra-api/src/main/java/com/syj/eplus/module/infra/api/company/CompanyApi.java)
- [CountryInfoApi.java](file://eplus-module-infra/eplus-module-infra-api/src/main/java/com/syj/eplus/module/infra/api/countryinfo/CountryInfoApi.java)
- [CardApi.java](file://eplus-module-infra/eplus-module-infra-api/src/main/java/com/syj/eplus/module/infra/api/card/CardApi.java)
- [FormChangeApi.java](file://eplus-module-infra/eplus-module-infra-api/src/main/java/com/syj/eplus/module/infra/api/formchange/FormChangeApi.java)
- [OrderLinkApi.java](file://eplus-module-infra/eplus-module-infra-api/src/main/java/com/syj/eplus/module/infra/api/orderlink/OrderLinkApi.java)
- [InvoiceHolderApi.java](file://eplus-module-home/eplus-module-home-api/src/main/java/com/syj/eplus/module/home/api/InvoiceHolderApi.java)
- [QualityInspectionApi.java](file://eplus-module-qms/eplus-module-qms-api/src/main/java/com/syj/eplus/module/qms/api/QualityInspectionApi.java)
- [ExhibitionApi.java](file://eplus-module-exms/eplus-module-exms-api/src/main/java/com/syj/eplus/module/exms/api/exhibition/ExhibitionApi.java)
- [ProjectApi.java](file://eplus-module-pjms/eplus-module-pjms-api/src/main/java/com/syj/eplus/module/pjms/api/ProjectApi.java)
- [DesignApi.java](file://eplus-module-dtms/eplus-module-dtms-api/src/main/java/com/syj/eplus/module/dtms/api/design/DesignApi.java)
- [ForwarderFeeApi.java](file://eplus-module-dms/eplus-module-dms-api/src/main/java/com/syj/eplus/module/dms/enums/api/ForwarderFeeApi.java)
- [ShipmentPlanApi.java](file://eplus-module-dms/eplus-module-dms-api/src/main/java/com/syj/eplus/module/dms/enums/api/ShipmentPlanApi.java)
- [AddSubItemApi.java](file://eplus-module-sms/eplus-module-sms-api/src/main/java/com/syj/eplus/module/sms/api/AddSubItemApi.java)
- [SaleAuxiliaryAllocationApi.java](file://eplus-module-sms/eplus-module-sms-api/src/main/java/com/syj/eplus/module/sms/api/SaleAuxiliaryAllocationApi.java)
- [CustClaimApi.java](file://eplus-module-fms/eplus-module-fms-api/src/main/java/com/syj/eplus/module/fms/api/payment/api/custclaim/CustClaimApi.java)
- [ReceiptApi.java](file://eplus-module-fms/eplus-module-fms-api/src/main/java/com/syj/eplus/module/fms/api/payment/api/receipt/ReceiptApi.java)
- [IManufactureApi.java](file://eplus-module-mms/eplus-module-mms-api/src/main/java/com/syj/eplus/module/mms/api/manufacture/IManufactureApi.java)
</cite>

## 目录
1. [API概览](#api概览)
2. [认证与授权](#认证与授权)
3. [客户管理API](#客户管理api)
4. [销售管理API](#销售管理api)
5. [采购管理API](#采购管理api)
6. [仓储管理API](#仓储管理api)
7. [财务管理API](#财务管理api)
8. [物流管理API](#物流管理api)
9. [产品管理API](#产品管理api)
10. [办公自动化API](#办公自动化api)
11. [基础设施API](#基础设施api)
12. [错误码列表](#错误码列表)
13. [API版本管理](#api版本管理)
14. [调用示例](#调用示例)
15. [SDK使用指南](#sdk使用指南)

## API概览

本API文档为Eplus Admin Server系统提供完整的RESTful接口规范说明。系统采用模块化设计，包含供应链、销售、仓储、财务、CRM等多个业务领域的API接口。所有API遵循统一的RESTful设计规范，使用JSON格式进行数据交换，并通过Springdoc集成Swagger提供在线文档。

系统API主要分为两大类：
- **内部调用API**：通过Java接口（如`*Api.java`）定义，供模块间服务调用
- **外部REST API**：通过Controller类暴露，供前端应用和外部系统调用

API设计遵循以下原则：
- 统一返回格式（Result封装）
- 统一异常处理机制
- 接口版本化管理
- 数据权限控制
- 操作日志记录

**Section sources**
- [README.md](file://README.md#L1-L747)

## 认证与授权

### 认证方式
系统采用OAuth 2.0协议进行认证，通过JWT（JSON Web Token）实现无状态认证。

**认证端点**：
- `POST /system/oauth2/token` - 获取访问令牌
- `POST /system/oauth2/logout` - 注销登录

**请求头要求**：
```
Authorization: Bearer <access_token>
Content-Type: application/json
```

### 权限控制
系统基于RBAC（基于角色的访问控制）模型实现权限管理：

1. **角色管理**：系统预置管理员、财务、采购、销售等角色
2. **菜单权限**：控制用户可访问的菜单项
3. **数据权限**：通过`@DataPermission`注解实现数据范围控制
4. **操作权限**：通过`@PreAuthorize`注解控制具体操作权限

### 作用域（Scope）控制
系统支持细粒度的作用域控制，不同客户端可申请不同范围的权限：
- `system:read` - 系统管理只读权限
- `system:write` - 系统管理读写权限
- `crm:read` - CRM模块只读权限
- `fms:write` - 财务模块读写权限

**Section sources**
- [OAuth2OpenController.java](file://yudao-module-system/yudao-module-system-biz/src/main/java/cn/iocoder/yudao/module/system/controller/admin/oauth2/OAuth2OpenController.java#L58-L72)

## 客户管理API

### 客户信息API
提供客户信息的查询和管理功能。

#### 获取客户精简信息
**HTTP方法**：GET  
**URL路径**：`/crm/cust/get-simple-list`  
**请求参数**：
- `idList` (List<Long>) - 客户ID列表

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": [
    {
      "id": 1001,
      "code": "CUST001",
      "name": "客户A",
      "status": 1
    }
  ]
}
```

#### 获取客户详细信息
**HTTP方法**：GET  
**URL路径**：`/crm/cust/detail`  
**请求参数**：
- `id` (Long) - 客户ID

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "id": 1001,
    "code": "CUST001",
    "name": "客户A",
    "countryId": 1,
    "regionCode": "CN",
    "status": 1,
    "createTime": "2024-01-01T10:00:00"
  }
}
```

### 客户分类API
管理客户分类信息。

#### 创建客户分类
**HTTP方法**：POST  
**URL路径**：`/crm/category/create`  
**请求体**：
```json
{
  "name": "重点客户",
  "code": "KEY_CUST",
  "sort": 1,
  "status": 1
}
```

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": 101
}
```

#### 更新客户分类
**HTTP方法**：PUT  
**URL路径**：`/crm/category/update`  
**请求体**：
```json
{
  "id": 101,
  "name": "战略客户",
  "sort": 1,
  "status": 1
}
```

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": null
}
```

**Section sources**
- [CustApi.java](file://eplus-module-crm/eplus-module-crm-api/src/main/java/com/syj/eplus/module/crm/api/cust/CustApi.java#L15-L151)
- [CrmCategoryController.java](file://eplus-module-crm/eplus-module-crm-biz/src/main/java/com/syj/eplus/module/crm/controller/admin/category/CrmCategoryController.java#L32-L89)
- [CustController.java](file://eplus-module-crm/eplus-module-crm-biz/src/main/java/com/syj/eplus/module/crm/controller/admin/cust/CustController.java#L41-L104)

## 销售管理API

### 销售合同API
管理销售合同的全生命周期。

#### 生成销售合同
**HTTP方法**：POST  
**URL路径**：`/sms/sale-contract/generate`  
**请求体**：
```json
{
  "custId": 1001,
  "custCode": "CUST001",
  "currency": "USD",
  "exchangeRate": 7.2,
  "items": [
    {
      "skuId": 2001,
      "quantity": 100,
      "unitPrice": 10.5,
      "amount": 1050
    }
  ],
  "paymentTerms": "30%预付款，70%见提单副本"
}
```

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": [
    {
      "id": 3001,
      "code": "SC20240001",
      "status": 1
    }
  ]
}
```

#### 更新销售合同确认状态
**HTTP方法**：PUT  
**URL路径**：`/sms/sale-contract/update-confirm-flag`  
**请求参数**：
- `confirmFlag` (Integer) - 确认状态 (0-未确认, 1-已确认)
- `id` (Long) - 合同ID

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": null
}
```

### 加减项管理API
管理销售合同的加减项。

#### 批量更新加减项
**HTTP方法**：PUT  
**URL路径**：`/sms/add-sub-item/batch-update`  
**请求体**：
```json
[
  {
    "id": 4001,
    "type": 1,
    "amount": 500,
    "remark": "包装费"
  },
  {
    "id": 4002,
    "type": 2,
    "amount": 200,
    "remark": "折扣"
  }
]
```

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": null
}
```

**Section sources**
- [SaleContractApi.java](file://eplus-module-sms/eplus-module-sms-api/src/main/java/com/syj/eplus/module/sms/api/SaleContractApi.java#L19-L474)
- [AddSubItemApi.java](file://eplus-module-sms/eplus-module-sms-api/src/main/java/com/syj/eplus/module/sms/api/AddSubItemApi.java)

## 采购管理API

### 采购合同API
管理采购合同的创建和维护。

#### 生成采购合同
**HTTP方法**：POST  
**URL路径**：`/scm/purchase-contract/generate`  
**请求体**：
```json
{
  "venderId": 5001,
  "venderCode": "VEND001",
  "currency": "USD",
  "exchangeRate": 7.2,
  "items": [
    {
      "skuId": 2001,
      "quantity": 100,
      "unitPrice": 8.5,
      "amount": 850
    }
  ],
  "deliveryDate": "2024-12-31"
}
```

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": [
    {
      "id": 6001,
      "code": "PC20240001",
      "status": 1
    }
  ]
}
```

#### 更新采购合同确认状态
**HTTP方法**：PUT  
**URL路径**：`/scm/purchase-contract/update-confirm-flag`  
**请求参数**：
- `confirmFlag` (Integer) - 确认状态 (0-未确认, 1-已确认)
- `id` (Long) - 合同ID

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": null
}
```

### 采购计划API
管理采购计划的创建和执行。

#### 根据销售合同生成采购计划
**HTTP方法**：GET  
**URL路径**：`/scm/purchase-plan/generate-by-sale-contract`  
**请求参数**：
- `saleContractId` (Long) - 销售合同ID

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "id": 7001,
    "code": "PP20240001",
    "status": 1,
    "items": [
      {
        "skuId": 2001,
        "quantity": 100,
        "requiredDate": "2024-11-30"
      }
    ]
  }
}
```

**Section sources**
- [PurchaseContractApi.java](file://eplus-module-scm/eplus-module-scm-api/src/main/java/com/syj/eplus/module/scm/api/purchasecontract/PurchaseContractApi.java#L18-L351)
- [ShipmentPlanApi.java](file://eplus-module-dms/eplus-module-dms-api/src/main/java/com/syj/eplus/module/dms/enums/api/ShipmentPlanApi.java)

## 仓储管理API

### 库存管理API
提供库存的查询和操作功能。

#### 查询可用库存
**HTTP方法**：POST  
**URL路径**：`/wms/stock/query-total`  
**请求体**：
```json
[
  {
    "skuCode": "SKU001",
    "companyId": 1001
  },
  {
    "skuCode": "SKU002",
    "companyId": 1001
  }
]
```

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "SKU001": 1000,
    "SKU002": 500
  }
}
```

#### 批量锁定库存
**HTTP方法**：POST  
**URL路径**：`/wms/stock/batch-lock`  
**请求体**：
```json
[
  {
    "skuId": 2001,
    "quantity": 100,
    "sourceOrderCode": "SC20240001",
    "sourceOrderType": 1
  }
]
```

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": true
}
```

### 入库管理API
管理采购入库等操作。

#### 创建采购入库单
**HTTP方法**：POST  
**URL路径**：`/wms/bill/purchase/create`  
**请求体**：
```json
{
  "purchaseContractCode": "PC20240001",
  "items": [
    {
      "skuId": 2001,
      "quantity": 100,
      "batchCode": "BATCH001"
    }
  ],
  "warehouseId": 8001
}
```

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": 9001
}
```

**Section sources**
- [IStockApi.java](file://eplus-module-wms/eplus-module-wms-api/src/main/java/com/syj/eplus/module/wms/api/stock/IStockApi.java#L15-L245)
- [ShipmentController.java](file://eplus-module-dms/eplus-module-dms-biz/src/main/java/com/syj/eplus/module/dms/controller/admin/shipment/ShipmentController.java)

## 财务管理API

### 付款管理API
处理供应商付款相关操作。

#### 创建付款单
**HTTP方法**：POST  
**URL路径**：`/fms/payment/create`  
**请求体**：
```json
{
  "businessType": 1,
  "businessCode": "PC20240001",
  "amount": 850,
  "currency": "USD",
  "paymentDate": "2024-10-15",
  "paymentMethod": "BANK_TRANSFER"
}
```

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": null
}
```

#### 批量创建付款单
**HTTP方法**：POST  
**URL路径**：`/fms/payment/batch-create`  
**请求体**：
```json
[
  {
    "businessType": 1,
    "businessCode": "PC20240001",
    "amount": 850,
    "currency": "USD",
    "paymentDate": "2024-10-15"
  },
  {
    "businessType": 1,
    "businessCode": "PC20240002",
    "amount": 600,
    "currency": "USD",
    "paymentDate": "2024-10-15"
  }
]
```

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": null
}
```

### 收款管理API
处理客户收款相关操作。

#### 更新收款计划
**HTTP方法**：PUT  
**URL路径**：`/fms/collection-plan/update`  
**请求体**：
```json
{
  "id": 10001,
  "receivedAmount": 500,
  "receivedDate": "2024-10-10",
  "status": 2
}
```

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": null
}
```

**Section sources**
- [PaymentApi.java](file://eplus-module-fms/eplus-module-fms-api/src/main/java/com/syj/eplus/module/fms/api/payment/api/payment/PaymentApi.java#L10-L41)
- [CustClaimApi.java](file://eplus-module-fms/eplus-module-fms-api/src/main/java/com/syj/eplus/module/fms/api/payment/api/custclaim/CustClaimApi.java)
- [ReceiptApi.java](file://eplus-module-fms/eplus-module-fms-api/src/main/java/com/syj/eplus/module/fms/api/payment/api/receipt/ReceiptApi.java)

## 物流管理API

### 出运管理API
管理货物出运相关操作。

#### 更新出运明细
**HTTP方法**：PUT  
**URL路径**：`/dms/shipment/update`  
**请求体**：
```json
{
  "id": 11001,
  "containerNo": "CONT001",
  "sealNo": "SEAL001",
  "departureDate": "2024-11-01",
  "arrivalDate": "2024-11-15"
}
```

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": null
}
```

#### 获取未完成的出运明细
**HTTP方法**：GET  
**URL路径**：`/dms/shipment/unshipped-by-cust`  
**请求参数**：
- `custCode` (String) - 客户编号

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": [
    {
      "id": 11001,
      "code": "SHIP20240001",
      "custCode": "CUST001",
      "status": 1,
      "estimatedDeparture": "2024-11-01"
    }
  ]
}
```

### 报关管理API
处理报关相关操作。

#### 创建报关单
**HTTP方法**：POST  
**URL路径**：`/dms/declaration/create`  
**请求体**：
```json
{
  "shipmentCode": "SHIP20240001",
  "customsCode": "CN001",
  "items": [
    {
      "hsCode": "847130",
      "description": "笔记本电脑",
      "quantity": 100,
      "unitPrice": 1000,
      "totalAmount": 100000
    }
  ]
}
```

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": 12001
}
```

**Section sources**
- [ShipmentApi.java](file://eplus-module-dms/eplus-module-dms-api/src/main/java/com/syj/eplus/module/dms/enums/api/ShipmentApi.java#L22-L231)
- [ForwarderFeeApi.java](file://eplus-module-dms/eplus-module-dms-api/src/main/java/com/syj/eplus/module/dms/enums/api/ForwarderFeeApi.java)

## 产品管理API

### 产品档案API
管理产品基本信息。

#### 获取产品精简信息
**HTTP方法**：GET  
**URL路径**：`/pms/sku/simple-map`  
**请求参数**：
- `idList` (List<Long>) - 产品ID列表

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "2001": {
      "id": 2001,
      "code": "SKU001",
      "name": "产品A",
      "category": "电子"
    }
  }
}
```

#### 获取产品BOM
**HTTP方法**：GET  
**URL路径**：`/pms/sku/bom`  
**请求参数**：
- `skuCode` (String) - 产品编号

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": [
    {
      "componentSkuCode": "COMP001",
      "componentName": "组件A",
      "quantity": 2,
      "unit": "PCS"
    }
  ]
}
```

### 产品分类API
管理产品分类信息。

#### 创建产品分类
**HTTP方法**：POST  
**URL路径**：`/pms/category/create`  
**请求体**：
```json
{
  "name": "电子产品",
  "code": "ELECTRONIC",
  "parentId": 0,
  "sort": 1,
  "status": 1
}
```

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": 13001
}
```

**Section sources**
- [SkuApi.java](file://eplus-module-pms/eplus-module-pms-api/src/main/java/com/syj/eplus/module/pms/api/sku/SkuApi.java#L22-L197)
- [IManufactureApi.java](file://eplus-module-mms/eplus-module-mms-api/src/main/java/com/syj/eplus/module/mms/api/manufacture/IManufactureApi.java)

## 办公自动化API

### 费用报销API
处理员工费用报销。

#### 创建报销单
**HTTP方法**：POST  
**URL路径**：`/oa/reimb/create`  
**请求体**：
```json
{
  "userId": 14001,
  "deptId": 15001,
  "items": [
    {
      "type": "TRAVEL",
      "amount": 1000,
      "currency": "CNY",
      "date": "2024-10-01",
      "remark": "出差费用"
    }
  ],
  "totalAmount": 1000
}
```

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": 16001
}
```

### 借款管理API
处理员工借款申请。

#### 更新借款单支付信息
**HTTP方法**：PUT  
**URL路径**：`/oa/loan-app/update-payment`  
**请求体**：
```json
{
  "id": 17001,
  "paymentStatus": 2,
  "paymentDate": "2024-10-10",
  "paymentMethod": "BANK_TRANSFER"
}
```

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": null
}
```

### 付款申请API
处理付款申请流程。

#### 创建付款申请
**HTTP方法**：POST  
**URL路径**：`/oa/payment-app/create`  
**请求体**：
```json
{
  "applicantId": 14001,
  "amount": 5000,
  "currency": "USD",
  "purpose": "采购付款",
  "businessType": 1,
  "businessCode": "PC20240001"
}
```

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": 18001
}
```

**Section sources**
- [LoanAppApi.java](file://eplus-module-oa/eplus-module-oa-api/src/main/java/com/syj/eplus/module/oa/api/LoanAppApi.java#L9-L55)
- [PaymentAppApi.java](file://eplus-module-oa/eplus-module-oa-api/src/main/java/com/syj/eplus/module/oa/api/PaymentAppApi.java)
- [ReimbApi.java](file://eplus-module-oa/eplus-module-oa-api/src/main/java/com/syj/eplus/module/oa/api/ReimbApi.java)
- [TravelAppApi.java](file://eplus-module-oa/eplus-module-oa-api/src/main/java/com/syj/eplus/module/oa/api/TravelAppApi.java)
- [DictSubjectApi.java](file://eplus-module-oa/eplus-module-oa-api/src/main/java/com/syj/eplus/module/oa/api/DictSubjectApi.java)
- [RepayAppApi.java](file://eplus-module-oa/eplus-module-oa-api/src/main/java/com/syj/eplus/module/oa/api/RepayAppApi.java)

## 基础设施API

### 代码生成API
提供代码生成功能。

#### 生成代码
**HTTP方法**：POST  
**URL路径**：`/infra/code-generator/generate`  
**请求体**：
```json
{
  "tableName": "crm_customer",
  "moduleName": "crm",
  "businessName": "customer",
  "className": "Customer",
  "author": "admin"
}
```

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "zipUrl": "/download/code-generator/123.zip"
  }
}
```

### 公司信息API
管理公司基础信息。

#### 获取公司路径
**HTTP方法**：GET  
**URL路径**：`/infra/company-path/list`  
**请求参数**：
- `companyId` (Long) - 公司ID

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": [
    {
      "id": 19001,
      "companyId": 1001,
      "path": "总公司/华东区/上海分公司",
      "level": 3
    }
  ]
}
```

### 国家信息API
提供国家信息查询。

#### 获取国家列表
**HTTP方法**：GET  
**URL路径**：`/infra/country-info/list`  
**请求参数**：
- `status` (Integer) - 状态 (1-启用, 0-禁用)

**响应格式**：
```json
{
  "code": 0,
  "msg": "成功",
  "data": [
    {
      "id": 1,
      "code": "CN",
      "name": "中国",
      "region": "亚洲",
      "status": 1
    }
  ]
}
```

**Section sources**
- [CodeGeneratorApi.java](file://eplus-module-infra/eplus-module-infra-api/src/main/java/com/syj/eplus/module/infra/api/CodeGeneratorApi.java)
- [CompanyApi.java](file://eplus-module-infra/eplus-module-infra-api/src/main/java/com/syj/eplus/module/infra/api/company/CompanyApi.java)
- [CountryInfoApi.java](file://eplus-module-infra/eplus-module-infra-api/src/main/java/com/syj/eplus/module/infra/api/countryinfo/CountryInfoApi.java)
- [CardApi.java](file://eplus-module-infra/eplus-module-infra-api/src/main/java/com/syj/eplus/module/infra/api/card/CardApi.java)
- [FormChangeApi.java](file://eplus-module-infra/eplus-module-infra-api/src/main/java/com/syj/eplus/module/infra/api/formchange/FormChangeApi.java)
- [OrderLinkApi.java](file://eplus-module-infra/eplus-module-infra-api/src/main/java/com/syj/eplus/module/infra/api/orderlink/OrderLinkApi.java)
- [InvoiceHolderApi.java](file://eplus-module-home/eplus-module-home-api/src/main/java/com/syj/eplus/module/home/api/InvoiceHolderApi.java)
- [QualityInspectionApi.java](file://eplus-module-qms/eplus-module-qms-api/src/main/java/com/syj/eplus/module/qms/api/QualityInspectionApi.java)
- [ExhibitionApi.java](file://eplus-module-exms/eplus-module-exms-api/src/main/java/com/syj/eplus/module/exms/api/exhibition/ExhibitionApi.java)
- [ProjectApi.java](file://eplus-module-pjms/eplus-module-pjms-api/src/main/java/com/syj/eplus/module/pjms/api/ProjectApi.java)
- [DesignApi.java](file://eplus-module-dtms/eplus-module-dtms-api/src/main/java/com/syj/eplus/module/dtms/api/design/DesignApi.java)

## 错误码列表

系统采用统一的错误码管理机制，所有错误码定义在各模块的`ErrorCodeConstants.java`文件中。

| 错误码 | 错误信息 | 说明 |
|--------|--------|------|
| 0 | 成功 | 操作成功 |
| 1 | 失败 | 操作失败 |
| 20001 | 参数校验失败 | 请求参数不符合校验规则 |
| 20002 | 记录不存在 | 请求的记录不存在 |
| 20003 | 记录已存在 | 记录已存在，无法重复创建 |
| 20004 | 状态不允许操作 | 当前状态不允许执行该操作 |
| 20005 | 数据权限不足 | 用户无权访问该数据 |
| 20006 | 业务校验失败 | 业务规则校验不通过 |
| 20007 | 库存不足 | 可用库存数量不足 |
| 20008 | 付款金额超限 | 付款金额超过合同金额 |
| 20009 | 审批中无法修改 | 审批流程中的记录无法修改 |
| 20010 | 已完成无法操作 | 已完成的记录无法执行该操作 |
| 40001 | 未登录 | 用户未登录或登录已过期 |
| 40003 | 无权限访问 | 用户无权访问该资源 |
| 50001 | 系统异常 | 系统内部异常 |
| 50002 | 数据库异常 | 数据库操作异常 |
| 50003 | 网络异常 | 网络连接异常 |

**Section sources**
- [ErrorCodeConstants.java](file://eplus-module-crm/eplus-module-crm-api/src/main/java/com/syj/eplus/module/crm/enums/ErrorCodeConstants.java)
- [ErrorCodeConstants.java](file://eplus-module-scm/eplus-module-scm-api/src/main/java/com/syj/eplus/module/scm/enums/ErrorCodeConstants.java)
- [ErrorCodeConstants.java](file://eplus-module-sms/eplus-module-sms-api/src/main/java/com/syj/eplus/module/sms/enums/ErrorCodeConstants.java)
- [ErrorCodeConstants.java](file://eplus-module-wms/eplus-module-wms-api/src/main/java/com/syj/eplus/module/wms/enums/ErrorCodeConstants.java)
- [ErrorCodeConstants.java](file://eplus-module-fms/eplus-module-fms-api/src/main/java/com/syj/eplus/module/fms/enums/ErrorCodeConstants.java)
- [ErrorCodeConstants.java](file://eplus-module-dms/eplus-module-dms-api/src/main/java/com/syj/eplus/module/dms/enums/ErrorCodeConstants.java)
- [ErrorCodeConstants.java](file://eplus-module-pms/eplus-module-pms-api/src/main/java/com/syj/eplus/module/pms/enums/ErrorCodeConstants.java)
- [ErrorCodeConstants.java](file://eplus-module-oa/eplus-module-oa-api/src/main/java/com/syj/eplus/module/oa/enums/ErrorCodeConstants.java)
- [ErrorCodeConstants.java](file://eplus-module-infra/eplus-module-infra-api/src/main/java/com/syj/eplus/module/infra/enums/ErrorCodeConstants.java)

## API版本管理

### 版本策略
系统采用语义化版本控制（Semantic Versioning），版本号格式为`MAJOR.MINOR.PATCH`：

- **MAJOR**：重大版本更新，包含不兼容的API变更
- **MINOR**：新增功能，向后兼容
- **PATCH**：修复bug，向后兼容

### 版本控制方式
API版本通过以下方式控制：

1. **URL路径版本控制**：
   ```
   /v1/crm/cust/detail
   /v2/crm/cust/detail
   ```

2. **请求头版本控制**：
   ```
   Accept: application/vnd.eplus.v1+json
   Accept: application/vnd.eplus.v2+json
   ```

3. **查询参数版本控制**：
   ```
   /crm/cust/detail?version=1.0
   ```

### 向后兼容性保证
系统承诺在同一个主版本号内保持向后兼容性：

1. **新增字段**：允许在响应中添加新字段，客户端应忽略未知字段
2. **新增接口**：允许添加新API，不影响现有接口
3. **字段弃用**：标记为`@Deprecated`的字段将在下一个主版本中移除
4. **错误码扩展**：允许添加新的错误码

### 版本生命周期
| 版本状态 | 说明 | 支持周期 |
|---------|------|---------|
| Active | 当前活跃版本 | 长期支持 |
| Deprecated | 已弃用版本 | 6个月支持 |
| End-of-Life | 生命周期结束 | 不再支持 |

**Section sources**
- [README.md](file://README.md#L552-L558)

## 调用示例

### Java调用示例
```java
// 创建HTTP客户端
RestTemplate restTemplate = new RestTemplate();

// 设置请求头
HttpHeaders headers = new HttpHeaders();
headers.set("Authorization", "Bearer " + accessToken);
headers.set("Content-Type", "application/json");

// 构建请求
HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

// 调用API
ResponseEntity<String> response = restTemplate.exchange(
    "http://localhost:18080/crm/cust/detail?id=1001",
    HttpMethod.GET,
    request,
    String.class
);

// 处理响应
if (response.getStatusCode() == HttpStatus.OK) {
    // 解析JSON响应
    JSONObject result = new JSONObject(response.getBody());
    if (result.getInt("code") == 0) {
        // 处理成功响应
        JSONObject data = result.getJSONObject("data");
        String custName = data.getString("name");
    } else {
        // 处理业务错误
        String errorMsg = result.getString("msg");
    }
}
```

### Python调用示例
```python
import requests
import json

# API配置
base_url = "http://localhost:18080"
access_token = "your_access_token"

# 设置请求头
headers = {
    "Authorization": f"Bearer {access_token}",
    "Content-Type": "application/json"
}

# 调用客户详情API
response = requests.get(
    f"{base_url}/crm/cust/detail",
    params={"id": 1001},
    headers=headers
)

# 处理响应
if response.status_code == 200:
    result = response.json()
    if result["code"] == 0:
        # 处理成功响应
        cust_data = result["data"]
        print(f"客户名称: {cust_data['name']}")
    else:
        # 处理业务错误
        print(f"错误: {result['msg']}")
else:
    print(f"HTTP错误: {response.status_code}")
```

### JavaScript调用示例
```javascript
// 使用fetch API调用
async function getCustDetail(custId) {
    const response = await fetch(`/crm/cust/detail?id=${custId}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${accessToken}`,
            'Content-Type': 'application/json'
        }
    });
    
    const result = await response.json();
    
    if (result.code === 0) {
        // 处理成功响应
        console.log('客户名称:', result.data.name);
        return result.data;
    } else {
        // 处理业务错误
        console.error('错误:', result.msg);
        throw new Error(result.msg);
    }
}

// 调用示例
getCustDetail(1001)
    .then(data => console.log(data))
    .catch(error => console.error(error));
```

**Section sources**
- [README.md](file://README.md#L350-L352)

## SDK使用指南

### Java SDK
系统提供Java SDK简化API调用。

#### 添加依赖
```xml
<dependency>
    <groupId>com.syj.eplus</groupId>
    <artifactId>eplus-api-sdk</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### 初始化客户端
```java
// 创建API客户端
ApiClient client = new ApiClient();
client.setBasePath("http://localhost:18080");
client.setAccessToken("your_access_token");

// 获取客户API实例
CustApi custApi = new CustApi(client);
```

#### 调用API
```java
try {
    // 调用获取客户详情
    CustDTO cust = custApi.getCustById(1001L);
    System.out.println("客户名称: " + cust.getName());
    
} catch (ApiException e) {
    System.err.println("API调用失败: " + e.getMessage());
    System.err.println("错误码: " + e.getCode());
}
```

### 错误处理
```java
try {
    // 调用API
    custApi.createCust(createReq);
    
} catch (ApiException e) {
    switch (e.getCode()) {
        case 401:
            // 未授权，需要重新登录
            handleUnauthorized();
            break;
        case 403:
            // 无权限
            handleForbidden();
            break;
        case 400:
            // 参数错误
            handleBadRequest(e.getMessage());
            break;
        default:
            // 其他错误
            handleOtherError(e);
    }
}
```

### 连接池配置
```java
// 配置连接池
ApiClient client = new ApiClient();
client.setConnectionTimeout(30000);  // 连接超时30秒
client.setReadTimeout(60000);       // 读取超时60秒
client.setMaxTotalConnections(100); // 最大连接数
client.setDefaultMaxPerRoute(20);   // 每路由最大连接数
```

**Section sources**
- [README.md](file://README.md#L350-L352)