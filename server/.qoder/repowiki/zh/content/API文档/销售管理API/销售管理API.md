# 销售管理API

<cite>
**本文档引用的文件**
- [SaleContractApi.java](file://eplus-module-sms/eplus-module-sms-api/src/main/java/com/syj/eplus/module/sms/api/SaleContractApi.java)
- [SaleAuxiliaryAllocationApi.java](file://eplus-module-sms/eplus-module-sms-api/src/main/java/com/syj/eplus/module/sms/api/SaleAuxiliaryAllocationApi.java)
- [SaleContractSaveDTO.java](file://eplus-module-sms/eplus-module-sms-api/src/main/java/com/syj/eplus/module/sms/api/dto/SaleContractSaveDTO.java)
- [SaleAuxiliaryAllocationDTO.java](file://eplus-module-sms/eplus-module-sms-api/src/main/java/com/syj/eplus/module/sms/api/dto/SaleAuxiliaryAllocationDTO.java)
- [SaleContractSaveReqVO.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/salecontract/vo/SaleContractSaveReqVO.java)
- [SaleAuxiliaryAllocationSaveReqVO.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/saleauxiliaryallocation/vo/SaleAuxiliaryAllocationSaveReqVO.java)
- [SaleContractStatusEnum.java](file://eplus-framework/eplus-common/src/main/java/com/syj/eplus/framework/common/enums/SaleContractStatusEnum.java)
- [ErrorCodeConstants.java](file://eplus-module-sms/eplus-module-sms-api/src/main/java/com/syj/eplus/module/sms/enums/ErrorCodeConstants.java)
- [DomesticSaleContractController.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/salecontract/DomesticSaleContractController.java)
- [SaleAuxiliaryAllocationController.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/saleauxiliaryallocation/SaleAuxiliaryAllocationController.java)
</cite>

## 目录
1. [简介](#简介)
2. [API规范说明](#api规范说明)
3. [销售合同管理接口](#销售合同管理接口)
4. [辅助项分配接口](#辅助项分配接口)
5. [认证与授权](#认证与授权)
6. [错误码说明](#错误码说明)
7. [集成示例与最佳实践](#集成示例与最佳实践)

## 简介

销售管理API提供了一套完整的销售合同和订单管理功能，支持销售合同的创建、修改、查询、审批等全生命周期管理，以及销售合同与辅助项之间的分配功能。本API遵循OpenAPI/Swagger标准，为开发者提供清晰、规范的接口文档。

**本文档引用的文件**
- [SaleContractApi.java](file://eplus-module-sms/eplus-module-sms-api/src/main/java/com/syj/eplus/module/sms/api/SaleContractApi.java)
- [SaleAuxiliaryAllocationApi.java](file://eplus-module-sms/eplus-module-sms-api/src/main/java/com/syj/eplus/module/sms/api/SaleAuxiliaryAllocationApi.java)
- [SaleContractStatusEnum.java](file://eplus-framework/eplus-common/src/main/java/com/syj/eplus/framework/common/enums/SaleContractStatusEnum.java)

## API规范说明

本API遵循RESTful设计原则，使用JSON格式进行数据交换。所有接口均返回统一的响应格式，包含状态码、消息和数据体。

### 响应格式
```json
{
  "code": 0,
  "msg": "成功",
  "data": {}
}
```

### 数据类型说明
- `String`: 字符串类型
- `Integer`: 整数类型
- `Long`: 长整型
- `BigDecimal`: 高精度数值
- `LocalDateTime`: 日期时间类型
- `JsonAmount`: 金额对象，包含币种和金额值
- `JsonWeight`: 重量对象，包含单位和数值

### 分页参数
所有分页接口均支持以下参数：
- `pageNo`: 页码，从0开始
- `pageSize`: 每页大小，-1表示不分页

## 销售合同管理接口

### 创建销售合同
创建新的销售合同。

**HTTP方法**: `POST`  
**资源路径**: `/sms/domestic/sale-contract/create`

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | Long | 否 | 主键 |
| code | String | 否 | 编号 |
| companyId | Long | 是 | 内部法人单位主键 |
| companyName | String | 是 | 内部法人单位名称 |
| companyPath | JsonCompanyPath | 是 | 订单路径 |
| custId | Long | 是 | 客户主键 |
| custCode | String | 是 | 客户编号 |
| custName | String | 是 | 客户名称 |
| currency | String | 是 | 交易币别 |
| settlementId | Long | 是 | 收款方式主键 |
| settlementName | String | 是 | 收款方式名称 |
| custCountryId | Long | 是 | 客户国别主键 |
| custCountryName | String | 是 | 客户国别 |
| custAreaName | String | 是 | 客户区域 |
| custPo | String | 是 | 客户合同号 |
| agentFlag | Integer | 是 | 是否代理 |
| collectedCustId | Long | 是 | 应收客户主键 |
| collectedCustCode | String | 是 | 应收客户编号 |
| collectedCustName | String | 是 | 应收客户名称 |
| receiveCustId | Long | 是 | 收货客户主键 |
| receiveCustCode | String | 是 | 收货客户编号 |
| receiveCustName | String | 是 | 收货客户名称 |
| sales | UserDept | 是 | 销售人员 |
| children | List<SaleContractItem> | 是 | 销售明细 |

#### 请求体示例
```json
{
  "companyId": 12365,
  "companyName": "公司A",
  "companyPath": {
    "company": {
      "id": 1,
      "name": "母公司"
    },
    "next": {
      "company": {
        "id": 2,
        "name": "子公司"
      }
    }
  },
  "custId": 1001,
  "custCode": "CUST001",
  "custName": "客户A",
  "currency": "USD",
  "settlementId": 2001,
  "settlementName": "电汇",
  "custCountryId": 3001,
  "custCountryName": "美国",
  "custAreaName": "北美",
  "custPo": "PO001",
  "agentFlag": 0,
  "collectedCustId": 1001,
  "collectedCustCode": "CUST001",
  "collectedCustName": "客户A",
  "receiveCustId": 1001,
  "receiveCustCode": "CUST001",
  "receiveCustName": "客户A",
  "sales": {
    "id": 5001,
    "name": "张三"
  },
  "children": [
    {
      "skuCode": "SKU001",
      "skuName": "产品A",
      "quantity": 100,
      "unitPrice": {
        "currency": "USD",
        "amount": 10.00
      }
    }
  ]
}
```

#### 响应数据格式
```json
{
  "code": 0,
  "msg": "成功",
  "data": [1, 2, 3]
}
```

#### HTTP状态码
| 状态码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 500 | 服务器内部错误 |

**本文档引用的文件**
- [DomesticSaleContractController.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/salecontract/DomesticSaleContractController.java)
- [SaleContractSaveReqVO.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/salecontract/vo/SaleContractSaveReqVO.java)
- [SaleContractSaveDTO.java](file://eplus-module-sms/eplus-module-sms-api/src/main/java/com/syj/eplus/module/sms/api/dto/SaleContractSaveDTO.java)

### 更新销售合同
更新现有销售合同信息。

**HTTP方法**: `PUT`  
**资源路径**: `/sms/domestic/sale-contract/update`

#### 请求参数
同创建销售合同接口，需提供`id`字段。

#### 响应数据格式
```json
{
  "code": 0,
  "msg": "成功",
  "data": true
}
```

#### HTTP状态码
| 状态码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

**本文档引用的文件**
- [DomesticSaleContractController.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/salecontract/DomesticSaleContractController.java)
- [SaleContractSaveReqVO.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/salecontract/vo/SaleContractSaveReqVO.java)

### 删除销售合同
删除指定的销售合同。

**HTTP方法**: `DELETE`  
**资源路径**: `/sms/domestic/sale-contract/delete`

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | Long | 是 | 销售合同主键 |

#### 响应数据格式
```json
{
  "code": 0,
  "msg": "成功",
  "data": true
}
```

#### HTTP状态码
| 状态码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

**本文档引用的文件**
- [DomesticSaleContractController.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/salecontract/DomesticSaleContractController.java)

### 查询销售合同详情
根据ID获取销售合同详细信息。

**HTTP方法**: `GET`  
**资源路径**: `/sms/domestic/sale-contract/detail`

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | Long | 是 | 销售合同主键 |

#### 响应数据格式
```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "id": 13247,
    "code": "SC001",
    "companyId": 12365,
    "companyName": "公司A",
    "companyPath": {
      "company": {
        "id": 1,
        "name": "母公司"
      },
      "next": {
        "company": {
          "id": 2,
          "name": "子公司"
        }
      }
    },
    "custId": 1001,
    "custCode": "CUST001",
    "custName": "客户A",
    "currency": "USD",
    "settlementId": 2001,
    "settlementName": "电汇",
    "status": 1,
    "children": [
      {
        "id": 1001,
        "skuCode": "SKU001",
        "skuName": "产品A",
        "quantity": 100,
        "unitPrice": {
          "currency": "USD",
          "amount": 10.00
        }
      }
    ]
  }
}
```

#### HTTP状态码
| 状态码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

**本文档引用的文件**
- [DomesticSaleContractController.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/salecontract/DomesticSaleContractController.java)

### 查询销售合同分页列表
获取销售合同分页列表。

**HTTP方法**: `GET`  
**资源路径**: `/sms/domestic/sale-contract/page`

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| pageNo | Integer | 否 | 页码，从0开始 |
| pageSize | Integer | 否 | 每页大小，-1表示不分页 |
| statusList | Array<Integer> | 否 | 状态列表 |
| custCode | String | 否 | 客户编号 |
| saleType | Integer | 否 | 销售合同类型 |

#### 响应数据格式
```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "list": [
      {
        "id": 13247,
        "code": "SC001",
        "custName": "客户A",
        "currency": "USD",
        "status": 1,
        "totalAmount": {
          "currency": "USD",
          "amount": 1000.00
        }
      }
    ],
    "total": 1
  }
}
```

#### HTTP状态码
| 状态码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 500 | 服务器内部错误 |

**本文档引用的文件**
- [DomesticSaleContractController.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/salecontract/DomesticSaleContractController.java)

### 提交销售合同审批
提交销售合同进行审批流程。

**HTTP方法**: `PUT`  
**资源路径**: `/sms/domestic/sale-contract/submit`

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| contactId | Long | 是 | 合同主键 |

#### 响应数据格式
```json
{
  "code": 0,
  "msg": "成功",
  "data": true
}
```

#### HTTP状态码
| 状态码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

**本文档引用的文件**
- [DomesticSaleContractController.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/salecontract/DomesticSaleContractController.java)

### 审批销售合同
审批销售合同。

**HTTP方法**: `PUT`  
**资源路径**: `/sms/domestic/sale-contract/approve`

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | String | 是 | 流程实例ID |
| reason | String | 否 | 审批理由 |

#### 响应数据格式
```json
{
  "code": 0,
  "msg": "成功",
  "data": true
}
```

#### HTTP状态码
| 状态码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

**本文档引用的文件**
- [DomesticSaleContractController.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/salecontract/DomesticSaleContractController.java)

### 销售合同作废
将销售合同标记为作废状态。

**HTTP方法**: `PUT`  
**资源路径**: `/sms/domestic/sale-contract/close`

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | Long | 是 | 销售合同主键 |
| reason | String | 是 | 作废原因 |

#### 响应数据格式
```json
{
  "code": 0,
  "msg": "成功",
  "data": true
}
```

#### HTTP状态码
| 状态码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

**本文档引用的文件**
- [DomesticSaleContractController.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/salecontract/DomesticSaleContractController.java)

## 辅助项分配接口

### 创建辅助项分配
创建销售合同与辅助项的分配关系。

**HTTP方法**: `POST`  
**资源路径**: `/sms/sale-auxiliary-allocation/create`

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| saleContractCode | String | 是 | 销售合同编号 |
| skuCode | String | 是 | 产品编号 |
| skuName | String | 是 | 产品名称 |
| cskuCode | String | 是 | 客户货号 |
| auxiliaryPurchaseContractCode | String | 是 | 辅料采购合同编号 |
| auxiliarySkuCode | String | 是 | 辅料产品编号 |
| auxiliarySkuName | String | 是 | 辅料产品名称 |
| quantity | String | 是 | 采购数量 |
| allocationAmount | JsonAmount | 是 | 分摊金额 |

#### 请求体示例
```json
{
  "saleContractCode": "SC001",
  "skuCode": "SKU001",
  "skuName": "产品A",
  "cskuCode": "CSKU001",
  "auxiliaryPurchaseContractCode": "APC001",
  "auxiliarySkuCode": "ASKU001",
  "auxiliarySkuName": "辅料A",
  "quantity": "50",
  "allocationAmount": {
    "currency": "USD",
    "amount": 500.00
  }
}
```

#### 响应数据格式
```json
{
  "code": 0,
  "msg": "成功",
  "data": 1
}
```

#### HTTP状态码
| 状态码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 500 | 服务器内部错误 |

**本文档引用的文件**
- [SaleAuxiliaryAllocationController.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/saleauxiliaryallocation/SaleAuxiliaryAllocationController.java)
- [SaleAuxiliaryAllocationSaveReqVO.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/saleauxiliaryallocation/vo/SaleAuxiliaryAllocationSaveReqVO.java)

### 更新辅助项分配
更新现有的辅助项分配信息。

**HTTP方法**: `PUT`  
**资源路径**: `/sms/sale-auxiliary-allocation/update`

#### 请求参数
同创建辅助项分配接口，需提供`id`字段。

#### 响应数据格式
```json
{
  "code": 0,
  "msg": "成功",
  "data": true
}
```

#### HTTP状态码
| 状态码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

**本文档引用的文件**
- [SaleAuxiliaryAllocationController.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/saleauxiliaryallocation/SaleAuxiliaryAllocationController.java)

### 删除辅助项分配
删除指定的辅助项分配记录。

**HTTP方法**: `DELETE`  
**资源路径**: `/sms/sale-auxiliary-allocation/delete`

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | Long | 是 | 辅助项分配主键 |

#### 响应数据格式
```json
{
  "code": 0,
  "msg": "成功",
  "data": true
}
```

#### HTTP状态码
| 状态码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

**本文档引用的文件**
- [SaleAuxiliaryAllocationController.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/saleauxiliaryallocation/SaleAuxiliaryAllocationController.java)

### 查询辅助项分配详情
根据ID获取辅助项分配详细信息。

**HTTP方法**: `GET`  
**资源路径**: `/sms/sale-auxiliary-allocation/detail`

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| id | Long | 是 | 辅助项分配主键 |

#### 响应数据格式
```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "id": 1,
    "saleContractCode": "SC001",
    "skuCode": "SKU001",
    "skuName": "产品A",
    "cskuCode": "CSKU001",
    "auxiliaryPurchaseContractCode": "APC001",
    "auxiliarySkuCode": "ASKU001",
    "auxiliarySkuName": "辅料A",
    "quantity": "50",
    "allocationAmount": {
      "currency": "USD",
      "amount": 500.00
    }
  }
}
```

#### HTTP状态码
| 状态码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

**本文档引用的文件**
- [SaleAuxiliaryAllocationController.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/saleauxiliaryallocation/SaleAuxiliaryAllocationController.java)

### 查询辅助项分配分页列表
获取辅助项分配分页列表。

**HTTP方法**: `GET`  
**资源路径**: `/sms/sale-auxiliary-allocation/page`

#### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|-------|------|------|------|
| pageNo | Integer | 否 | 页码，从0开始 |
| pageSize | Integer | 否 | 每页大小，-1表示不分页 |
| saleContractCode | String | 否 | 销售合同编号 |
| skuCode | String | 否 | 产品编号 |

#### 响应数据格式
```json
{
  "code": 0,
  "msg": "成功",
  "data": {
    "list": [
      {
        "id": 1,
        "saleContractCode": "SC001",
        "skuCode": "SKU001",
        "skuName": "产品A",
        "cskuCode": "CSKU001",
        "auxiliaryPurchaseContractCode": "APC001",
        "auxiliarySkuCode": "ASKU001",
        "auxiliarySkuName": "辅料A",
        "quantity": "50",
        "allocationAmount": {
          "currency": "USD",
          "amount": 500.00
        }
      }
    ],
    "total": 1
  }
}
```

#### HTTP状态码
| 状态码 | 说明 |
|-------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 500 | 服务器内部错误 |

**本文档引用的文件**
- [SaleAuxiliaryAllocationController.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/saleauxiliaryallocation/SaleAuxiliaryAllocationController.java)

## 认证与授权

### 认证要求
所有API接口均需要进行身份认证，通过JWT令牌进行验证。客户端需要在请求头中包含`Authorization`字段，格式如下：

```
Authorization: Bearer <JWT_TOKEN>
```

### 权限要求
不同操作需要不同的权限，通过`@PreAuthorize`注解进行权限控制。主要权限如下：

| 接口 | 所需权限 |
|------|---------|
| 创建销售合同 | `sms:domestic-sale-contract:create` |
| 更新销售合同 | `sms:domestic-sale-contract:update` |
| 删除销售合同 | `sms:domestic-sale-contract:delete` |
| 查询销售合同 | `sms:domestic-sale-contract:query` |
| 提交审批 | `sms:domestic-sale-contract:submit` |
| 审批合同 | `sms:domestic-sale-contract:audit` |
| 作废合同 | `sms:domestic-sale-contract:close` |
| 创建辅助项分配 | `sms:sale-auxiliary-allocation:create` |
| 更新辅助项分配 | `sms:sale-auxiliary-allocation:update` |
| 删除辅助项分配 | `sms:sale-auxiliary-allocation:delete` |
| 查询辅助项分配 | `sms:sale-auxiliary-allocation:query` |
| 提交辅助项分配审批 | `sms:sale-auxiliary-allocation:submit` |
| 审批辅助项分配 | `sms:sale-auxiliary-allocation:audit` |

**本文档引用的文件**
- [DomesticSaleContractController.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/salecontract/DomesticSaleContractController.java)
- [SaleAuxiliaryAllocationController.java](file://eplus-module-sms/eplus-module-sms-biz/src/main/java/com/syj/eplus/module/sms/controller/admin/saleauxiliaryallocation/SaleAuxiliaryAllocationController.java)

## 错误码说明

### 通用错误码
| 错误码 | 说明 |
|-------|------|
| 0 | 成功 |
| 1 | 失败 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 销售合同相关错误码
| 错误码 | 说明 |
|-------|------|
| 1009001001 | 合同不存在 |
| 1009002001 | 合同明细不存在 |
| 1009002002 | 明细拖柜费计算异常 |
| 1009002003 | 预估总运费计算异常 |
| 1009002004 | 总体积不能为0 |
| 1009002005 | 明细预估总费用计算异常 |
| 1009002006 | 明细佣金金额计算异常 |
| 1009002007 | 佣金金额计算异常 |
| 1009002008 | 散货体积计算异常 |
| 1009002009 | 20尺柜数量计算异常 |
| 1009002010 | 40尺柜计算异常 |
| 1009002011 | 40尺高柜计算异常 |
| 1009002012 | 平台费计算异常 |
| 1009002013 | 加项金额计算异常 |
| 1009002014 | 减项金额计算异常 |
| 1009002015 | 毛重合计计算异常 |
| 1009002016 | 净重合计计算异常 |
| 1009002017 | 体积合计计算异常 |
| 1009002018 | 货值合计计算异常 |
| 1009002019 | 采购合计计算异常 |
| 1009002021 | 明细退税金额计算异常 |
| 1009002022 | 退税总额计算异常 |
| 1009002023 | 汇率不存在 |
| 1009002024 | 应收汇款计算异常 |
| 1009002025 | 产品外箱装量不可为0 |
| 1009002026 | 箱数计算异常 |
| 1009002027 | 销售合同变更单不存在 |
| 1009002028 | 销售明细中存在未知合同编号 |
| 1009002029 | 锁定数量不可大于销售数量 |
| 1009002030 | 外销合同明细均已转出运计划 |
| 1009002031 | 销售明细锁定数量超出销售数量 |
| 1009002032 | 重新分配的数量和原数量总和不一致 |
| 1009002033 | 只允许下推相同账套销售明细 |
| 1009002034 | 销售合同状态为空 |
| 1009002035 | 产品没有对应的采购合同或还未锁定库存 |
| 1009002036 | 采购合同明细不存在 |
| 1009002037 | 销售合同出运口岸不一致 |
| 1009002038 | 销售合同目的口岸不一致 |
| 1009002039 | 销售合同运输方式不一致 |
| 1009002040 | 只允许相同外贸公司的销售合同下推出运计划 |
| 1009002041 | 没有可出运的产品 |
| 1009002042 | 当前币种未找到对应的汇率 |

### 辅助项分配相关错误码
| 错误码 | 说明 |
|-------|------|
| 1010001001 | 销售合同辅料分摊不存在 |
| 1010001002 | 客户列表不存在 |
| 1010001003 | 外箱体积为空，请检查数据 |
| 1010001004 | 未获取到尺柜数量计算结果 |

### 出运相关错误码
| 错误码 | 说明 |
|-------|------|
| 1011001001 | [销售明细]出运数量不可大于销售数量 |
| 1011001002 | 箱数不可为空 |
| 1011001003 | 销售合同订单路径为空 |
| 1011001004 | 主体不存在 |
| 1011001005 | 不同路径销售合同不可下推出运计划 |
| 1011001006 | 出运明细-采购合计计算异常 |
| 1011001007 | 出运明细-货值合计计算异常 |
| 1011001008 | 出运明细-数量合计计算异常 |
| 1011001009 | 出运明细-箱数合计计算异常 |
| 1011001010 | 出运明细-体积合计计算异常 |
| 1011001011 | 出运明细-毛重合计计算异常 |
| 1011001012 | 出运明细-净重合计计算异常 |
| 1011001013 | 出运计划-采购合计计算异常 |
| 1011001014 | 出运计划-销售合同明细未找到 |
| 1011001015 | 出运明细-存在不同路径销售合同 |
| 1011001016 | 所选销售合同均无库存 |
| 1011001017 | 所选销售合同无库存 |
| 1011001018 | 所选销售明细无库存 |
| 1011001019 | 产品库存与销售数量不一致 |
| 1011001020 | 收款计划未执行,不可下推采购计划 |
| 1011001021 | 所选销售明细库存不足 |
| 1011001022 | 出运明细-报关合计计算异常 |
| 1011001023 | 出运明细-明细不存在 |
| 1011001024 | 销售明细单价为空 |
| 1011001025 | 税率信息为空 |
| 1011001026 | 内部公司简称未配置 |
| 1011001027 | 销售合同价格条款不一致 |
| 1011001028 | 不存在外贸公司 |
| 1011001029 | 存在唯一编码为空的销售合同明细信息 |
| 1011001030 | 检测到客户为内部客户，销售数量必须全部使用库存 |
| 1011001031 | 外销合同订单路径未找到外贸资质节点 |
| 1011001032 | 库存合计计算异常 |
| 1011001033 | 已有后续单据，不可变更订单路径 |
| 1011001034 | 该合同已有收款，先取消认领后再进行变更收款计划 |
| 1011001035 | 未找到合同币种汇率 |
| 1011001036 | 采购合同号为空 |
| 1011001037 | 采购合同不存在或已删除 |
| 1011001038 | 采购计划已完成或者出运数量为0 |

**本文档引用的文件**
- [ErrorCodeConstants.java](file://eplus-module-sms/eplus-module-sms-api/src/main/java/com/syj/eplus/module/sms/enums/ErrorCodeConstants.java)

## 集成示例与最佳实践

### Java集成示例
```java
// 创建销售合同
SaleContractSaveReqVO createReqVO = new SaleContractSaveReqVO();
createReqVO.setCompanyId(12365L);
createReqVO.setCompanyName("公司A");
createReqVO.setCustId(1001L);
createReqVO.setCustCode("CUST001");
createReqVO.setCustName("客户A");
createReqVO.setCurrency("USD");
createReqVO.setSettlementId(2001L);
createReqVO.setSettlementName("电汇");

// 设置销售明细
List<SaleContractItem> children = new ArrayList<>();
SaleContractItem item = new SaleContractItem();
item.setSkuCode("SKU001");
item.setSkuName("产品A");
item.setQuantity(100);
item.setUnitPrice(new JsonAmount("USD", BigDecimal.valueOf(10.00)));
children.add(item);
createReqVO.setChildren(children);

// 调用API
CommonResult<List<CreatedResponse>> result = saleContractApi.createSaleContract(createReqVO);
if (result.getCode() == 0) {
    System.out.println("创建成功，合同ID: " + result.getData());
} else {
    System.err.println("创建失败: " + result.getMsg());
}
```

### 最佳实践建议
1. **错误处理**: 在调用API时，始终检查返回的错误码，并根据错误码进行相应的处理。
2. **幂等性**: 对于创建和更新操作，建议在请求中包含唯一标识，确保操作的幂等性。
3. **批量操作**: 对于大量数据的处理，尽量使用批量接口，减少网络开销。
4. **缓存**: 对于频繁查询但不经常变更的数据，建议在客户端进行缓存。
5. **并发控制**: 在高并发场景下，注意对共享资源的并发控制，避免数据不一致。
6. **日志记录**: 记录关键操作的日志，便于问题排查和审计。
7. **超时设置**: 设置合理的请求超时时间，避免长时间等待。
8. **重试机制**: 对于网络不稳定的情况，实现合理的重试机制。

**本文档引用的文件**
- [SaleContractApi.java](file://eplus-module-sms/eplus-module-sms-api/src/main/java/com/syj/eplus/module/sms/api/SaleContractApi.java)
- [SaleAuxiliaryAllocationApi.java](file://eplus-module-sms/eplus-module-sms-api/src/main/java/com/syj/eplus/module/sms/api/SaleAuxiliaryAllocationApi.java)