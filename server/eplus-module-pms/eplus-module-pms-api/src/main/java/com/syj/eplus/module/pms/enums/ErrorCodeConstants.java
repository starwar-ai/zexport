package com.syj.eplus.module.pms.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * CRM 错误码枚举类
 * <p>
 * crm 系统，使用 1-020-000-000 段
 */
public interface ErrorCodeConstants {
    ErrorCode SPU_NOT_EXISTS = new ErrorCode(1_120_000_000, "产品信息不存在");

    ErrorCode CATEGORY_NOT_EXISTS = new ErrorCode(1_120_000_001, "产品分类不存在");

    ErrorCode SKU_NOT_EXISTS = new ErrorCode(1_120_000_002, "产品SKU不存在");

    ErrorCode SKU_BOM_NOT_EXISTS = new ErrorCode(1_120_000_003, "产品SKU不存在");
    ErrorCode BRAND_NOT_EXISTS = new ErrorCode(1_120_000_004, "品牌不存在");
    ErrorCode HSDATA_NOT_EXISTS = new ErrorCode(1_120_000_004, "海关编码不存在");

    ErrorCode UNDERREVIEW_EDITTING_NOT_ALLOWED = new ErrorCode(1_120_000_005, "审核中,禁止修改");
    ErrorCode PROHIBIT_CREATING_PRODUCTS = new ErrorCode(1_120_000_006, "禁止该分类创建产品");
    ErrorCode NOT_UPDATE_PROCESS = new ErrorCode(1_120_000_007, "产品已在审核流程中,禁止修改");
    ErrorCode CATEGORY_NOT_EMPTY = new ErrorCode(1_120_000_008, "产品分类等级不可为空");
    ErrorCode CATEGORY_NOT_EXCEED_THREE = new ErrorCode(1_120_000_008, "产品分类等级不可超过三级");
    ErrorCode BASE_SKU_CODE_NOT_EMPTY = new ErrorCode(1_120_000_009, "基础产品编号不可为空");
    ErrorCode BASE_SKU_CODE_NUL = new ErrorCode(1_120_000_009, "基础产品编号重复");

    ErrorCode CUST_SKU_CREATE_MISS = new ErrorCode(1_120_000_010, "创建客户产品缺少必填字段");
    ErrorCode CUST_SKU_CREATE_EXIST = new ErrorCode(1_120_000_011, "客户产品已存在");
    ErrorCode SKU_NAME_LENGTH_ERROR = new ErrorCode(1_120_000_012, "名称搜索条件异常");

    ErrorCode SKU_NAME_ACCURATE_YES_ERROR = new ErrorCode(1_120_000_013, "名称精确搜索数据异常");
    ErrorCode SKU_CUST_CODE_ACCURATE_YES_ERROR = new ErrorCode(1_120_000_014, "客户产品编号精确搜索数据异常");
    ErrorCode SKU_CUST_NAME_ACCURATE_YES_ERROR = new ErrorCode(1_120_000_015, "客户产品名称精确搜索数据异常");

    ErrorCode OSKU_CODE_NULL = new ErrorCode(1_120_000_016, "自营产品货号为空");

    ErrorCode OSKU_CODE_EXIST = new ErrorCode(1_120_000_017, "自营产品货号已经存在");

    ErrorCode CSKU_CODE_EXIST = new ErrorCode(1_120_000_017, "客户货号已经存在");
    ErrorCode OLD_SKU_NOT_EXISTS = new ErrorCode(1_120_000_002, "旧产品不存在");
    ErrorCode HSDATA_EXISTS = new ErrorCode(1_120_000_018, "海关编码已经存在");

    ErrorCode UNSUBMIT_EDIT_DEL_NOT_ALLOWED = new ErrorCode(1_120_001_001, "只有提交中的可以修改删除");

    ErrorCode CHANGE_NOT_ALLOWED = new ErrorCode(1_120_001_002, "已经有高版本客户信息，无法变更");

    ErrorCode IN_CHANGE_NOT_ALLOWED = new ErrorCode(1_120_001_003, "客户信息正在变更中，无法变更");

    ErrorCode UNAPPROVE_EDIT_DEL_NOT_ALLOWED = new ErrorCode(1_120_001_004, "只有已通过的可以删除旧数据");
    ErrorCode FIELD_CHANGE_CONFIG_NOT_EXISTS = new ErrorCode(1_120_001_005, "未找到相关表变更字段的配置！");
    ErrorCode UPDATE_SKU_FAIL = new ErrorCode(1_120_001_006, "修改产品失败");
    ErrorCode COUNTRY_MAP_NOT_EXIT = new ErrorCode(1_120_001_007, "国家信息不存在");
    ErrorCode CUST_MAP_NOT_EXIT = new ErrorCode(1_120_001_008, "客户信息不存在");
    ErrorCode PACKAGE_TYPE_NOT_EXISTS = new ErrorCode(1_120_002_001, "包装方式不存在");
    ErrorCode PACKAGE_TYPE_IN_USED = new ErrorCode(1_120_002_002, "包装方式在使用中，不可删除");

    ErrorCode SKU_AUXILIARY_NOT_EXISTS = new ErrorCode(1_120_003_001, "产品辅料配比不存在");
    ErrorCode AGENT_CODE_PREFIX_NOT_EMPTY = new ErrorCode(1_120_003_002, "代理产品编号前缀未配置");
    ErrorCode SKU_CODE_EXIST = new ErrorCode(1_120_003_003, "产品编号已存在");
}