package com.syj.eplus.module.infra.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * Infra 错误码枚举类
 * <p>
 * infra 系统,使用 1-015-000-000 段
 */
public interface ErrorCodeConstants {

    //=========== 付款方式信息 1-015-001-000 ==========
    ErrorCode PAYMENT_NOT_EXISTS = new ErrorCode(1_015_001_000, "付款方式不存在");

    //=========== 法人单位信息 1-015-002-000 ==========
    ErrorCode COMPANY_NOT_EXISTS = new ErrorCode(1_015_002_000, "法人单位不存在,ID-{}");
    ErrorCode COMPANY_ENABLE = new ErrorCode(1_015_002_001, "法人单位已启用");
    ErrorCode COMPANY_DIS_ENABLE = new ErrorCode(1_015_002_002, "法人单位已禁用");
    ErrorCode COMPANY_NOT_CURRENCY_EXISTS = new ErrorCode(1_015_002_003, "主体采购币种不能为空");

    //=========== 路径信息 1-015-003-000 ==========
    ErrorCode PATH_NOT_EXISTS = new ErrorCode(1_015_003_001, "路径不存在");
    ErrorCode PATH_PARENT_NOT_EXISTS = new ErrorCode(1_015_003_002, "未能找到层级为{}的父节点，无法挂载节点:{}");

    //=========== 表单字段变更 1-015-004-000 ==========
    ErrorCode FORM_CHANGE_NOT_EXISTS = new ErrorCode(1_015_004_001, "表单字段变更管理信息不存在");
    ErrorCode FORM_CHANGE_ITEM_NOT_EXISTS = new ErrorCode(1_015_004_002, "表单字段变更管理子信息不存在");
    ErrorCode UNKNOWN_FEE_SHARE_TYPE = new ErrorCode(1_015_004_003, "未知归集类型");
    ErrorCode FEE_SHARE_NOT_EMPTY = new ErrorCode(1_015_004_004, "归集对象不可为空");
    ErrorCode FORM_CHANGE_MUL = new ErrorCode(1_015_004_005, "配置项重复");

    //=========== 卡片 1-015-005-000 ==========
    ErrorCode CARD_NOT_EXISTS = new ErrorCode(1_015_005_001, "卡片不存在");
    ErrorCode CARD_ENABLE = new ErrorCode(1_015_005_002, "卡片已启用,请勿重复操作");
    ErrorCode CARD_DISABLE = new ErrorCode(1_015_005_003, "卡片已禁用,请勿重复操作");
    ErrorCode PARAM_ERROR = new ErrorCode(1_015_005_004, "参数错误");
    ErrorCode TABLE_NAME_EMPTY = new ErrorCode(1_015_005_005, "表名为空");
    ErrorCode FIELD_CHANGE_CONFIG_NOT_EXISTS = new ErrorCode(1_015_005_006, "未找到{}变更字段配置");

    //=========== 结算方式 1-015-006-000 ==========
    ErrorCode SETTLEMENT_NOT_EXISTS = new ErrorCode(1_015_006_001, "结算方式不存在");

    //=========== 编号 1-015-007-000 ==========
    ErrorCode SN_NOT_EXISTS = new ErrorCode(1_015_007_001, "编号不存在");

    //=========== 版本 1-015-008-000 ==========
    ErrorCode VERSION_NOT_EXISTS = new ErrorCode(1_015_008_001, "版本不存在");

    //=========== 编码生成器 1-015-009-000 ==========
    ErrorCode CODE_GENERATOR_ERROR = new ErrorCode(1_015_009_001, "编码生成失败");
}
