package cn.iocoder.yudao.module.system.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * System 错误码枚举类
 * <p>
 * system 系统，使用 1-002-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== AUTH 模块 1-002-000-000 ==========
    ErrorCode AUTH_LOGIN_BAD_CREDENTIALS = new ErrorCode(1_002_000_000, "登录失败，账号密码不正确");
    ErrorCode AUTH_LOGIN_USER_DISABLED = new ErrorCode(1_002_000_001, "登录失败，账号被禁用");
    ErrorCode AUTH_LOGIN_CAPTCHA_CODE_ERROR = new ErrorCode(1_002_000_004, "验证码不正确，原因：{}");
    ErrorCode AUTH_THIRD_LOGIN_NOT_BIND = new ErrorCode(1_002_000_005, "未绑定账号，需要进行绑定");
    ErrorCode AUTH_TOKEN_EXPIRED = new ErrorCode(1_002_000_006, "Token 已经过期");
    ErrorCode AUTH_MOBILE_NOT_EXISTS = new ErrorCode(1_002_000_007, "手机号不存在");

    // ========== 菜单模块 1-002-001-000 ==========
    ErrorCode MENU_NAME_DUPLICATE = new ErrorCode(1_002_001_000, "已经存在该名字的菜单");
    ErrorCode MENU_PARENT_NOT_EXISTS = new ErrorCode(1_002_001_001, "父菜单不存在");
    ErrorCode MENU_PARENT_ERROR = new ErrorCode(1_002_001_002, "不能设置自己为父菜单");
    ErrorCode MENU_NOT_EXISTS = new ErrorCode(1_002_001_003, "菜单不存在");
    ErrorCode MENU_EXISTS_CHILDREN = new ErrorCode(1_002_001_004, "存在子菜单，无法删除");
    ErrorCode MENU_PARENT_NOT_DIR_OR_MENU = new ErrorCode(1_002_001_005, "父菜单的类型必须是目录或者菜单");

    // ========== 角色模块 1-002-002-000 ==========
    ErrorCode ROLE_NOT_EXISTS = new ErrorCode(1_002_002_000, "角色不存在");
    ErrorCode ROLE_NAME_DUPLICATE = new ErrorCode(1_002_002_001, "已经存在名为【{}】的角色");
    ErrorCode ROLE_CODE_DUPLICATE = new ErrorCode(1_002_002_002, "已经存在编码为【{}】的角色");
    ErrorCode ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE = new ErrorCode(1_002_002_003, "不能操作类型为系统内置的角色");
    ErrorCode ROLE_IS_DISABLE = new ErrorCode(1_002_002_004, "名字为【{}】的角色已被禁用");
    ErrorCode ROLE_ADMIN_CODE_ERROR = new ErrorCode(1_002_002_005, "编码【{}】不能使用");
    ErrorCode UNASSIGNED_ROLE = new ErrorCode(1_002_002_006, "用户未分配角色");
    ErrorCode ROLE_HAS_MORE_DEFAULT = new ErrorCode(1_002_002_007, "角色有多个默认值");
    ErrorCode ROLE_NOT_DEFAULT = new ErrorCode(1_002_002_009, "角色没有默认值");

    // ========== 用户模块 1-002-003-000 ==========
    ErrorCode USER_USERNAME_EXISTS = new ErrorCode(1_002_003_000, "用户账号已经存在");
    ErrorCode USER_MOBILE_EXISTS = new ErrorCode(1_002_003_001, "手机号已经存在");
    ErrorCode USER_EMAIL_EXISTS = new ErrorCode(1_002_003_002, "邮箱已经存在");
    ErrorCode USER_NOT_EXISTS = new ErrorCode(1_002_003_003, "用户不存在");
    ErrorCode PAGEKEY_NOT_NULL = new ErrorCode(1_002_004_004, "pagekey属性不可为空");
    ErrorCode TABINDEX_NOT_NULL = new ErrorCode(1_002_004_005, "tabIndex属性不可为空");

    ErrorCode USER_IMPORT_LIST_IS_EMPTY = new ErrorCode(1_002_003_004, "导入用户数据不能为空！");
    ErrorCode USER_PASSWORD_FAILED = new ErrorCode(1_002_003_005, "用户密码校验失败");
    ErrorCode USER_IS_DISABLE = new ErrorCode(1_002_003_006, "名字为【{}】的用户已被禁用");
    ErrorCode USER_COUNT_MAX = new ErrorCode(1_002_003_008, "创建用户失败，原因：超过租户最大租户配额({})！");
    ErrorCode USER_BANK_ACCOUNT_EXISTS = new ErrorCode(1_002_003_009, "银行账户已存在");
    ErrorCode USER_ID_NOT_NULL = new ErrorCode(1_002_003_010, "用户编号不能为空");
    ErrorCode TOKEN_NOT_EXISTS = new ErrorCode(1_002_003_011, "[企业微信]accessToken不存在");
    ErrorCode QW_WECHATUSER_NOT_EXISTS = new ErrorCode(1_002_003_012, "[企业微信]通过code获取登录用户id接口返回为空");
    ErrorCode QW_USER_NOT_EXISTS = new ErrorCode(1_002_003_013, "[企业微信]用户不存在");
    ErrorCode BANKACCOUNT_REPEAT = new ErrorCode(1_002_003_014, "银行账号重复，account-{}");
    ErrorCode USER_USERCODE_EXISTS = new ErrorCode(1_002_003_015, "用户编号已经存在");
    ErrorCode OPERATION_FORBIDDEN = new ErrorCode(1_002_003_016, "无效用户,禁止操作");


    // ========== 部门模块 1-002-004-000 ==========
    ErrorCode DEPT_NAME_DUPLICATE = new ErrorCode(1_002_004_000, "已经存在该名字的部门");
    ErrorCode DEPT_PARENT_NOT_EXITS = new ErrorCode(1_002_004_001, "父级部门不存在");
    ErrorCode DEPT_NOT_FOUND = new ErrorCode(1_002_004_002, "当前部门{}不存在");
    ErrorCode DEPT_EXITS_CHILDREN = new ErrorCode(1_002_004_003, "存在子部门，无法删除");
    ErrorCode DEPT_PARENT_ERROR = new ErrorCode(1_002_004_004, "不能设置自己为父部门");
    ErrorCode DEPT_EXISTS_USER = new ErrorCode(1_002_004_005, "部门中存在员工，无法删除");
    ErrorCode DEPT_NOT_ENABLE = new ErrorCode(1_002_004_006, "部门({})不处于开启状态，不允许选择");
    ErrorCode DEPT_PARENT_IS_CHILD = new ErrorCode(1_002_004_007, "不能设置自己的子部门为父部门");
    ErrorCode DEPT_CODE_IS_NULL = new ErrorCode(1_002_004_008, "部门编号为空");

    // ========== 岗位模块 1-002-005-000 ==========
    ErrorCode POST_NOT_FOUND = new ErrorCode(1_002_005_000, "当前岗位不存在");
    ErrorCode POST_NOT_ENABLE = new ErrorCode(1_002_005_001, "岗位({}) 不处于开启状态，不允许选择");
    ErrorCode POST_NAME_DUPLICATE = new ErrorCode(1_002_005_002, "已经存在该名字的岗位");
    ErrorCode POST_CODE_DUPLICATE = new ErrorCode(1_002_005_003, "已经存在该标识的岗位");

    // ========== 字典类型 1-002-006-000 ==========
    ErrorCode DICT_TYPE_NOT_EXISTS = new ErrorCode(1_002_006_001, "当前字典类型不存在");
    ErrorCode DICT_TYPE_NOT_ENABLE = new ErrorCode(1_002_006_002, "字典类型不处于开启状态，不允许选择");
    ErrorCode DICT_TYPE_NAME_DUPLICATE = new ErrorCode(1_002_006_003, "已经存在该名字的字典类型");
    ErrorCode DICT_TYPE_TYPE_DUPLICATE = new ErrorCode(1_002_006_004, "已经存在该类型的字典类型");
    ErrorCode DICT_TYPE_HAS_CHILDREN = new ErrorCode(1_002_006_005, "无法删除，该字典类型还有字典数据");

    // ========== 字典数据 1-002-007-000 ==========
    ErrorCode DICT_DATA_NOT_EXISTS = new ErrorCode(1_002_007_001, "当前字典数据不存在");
    ErrorCode DICT_DATA_NOT_ENABLE = new ErrorCode(1_002_007_002, "字典数据({})不处于开启状态，不允许选择");
    ErrorCode DICT_DATA_VALUE_DUPLICATE = new ErrorCode(1_002_007_003, "已经存在该值的字典数据");

    // ========== 通知公告 1-002-008-000 ==========
    ErrorCode NOTICE_NOT_FOUND = new ErrorCode(1_002_008_001, "当前通知公告不存在");

    // ========== 错误码模块 1-002-017-000 ==========
    ErrorCode ERROR_CODE_NOT_EXISTS = new ErrorCode(1_002_017_000, "错误码不存在");
    ErrorCode ERROR_CODE_DUPLICATE = new ErrorCode(1_002_017_001, "已经存在编码为【{}】的错误码");

    // ========== 社交用户 1-002-018-000 ==========
    ErrorCode SOCIAL_USER_AUTH_FAILURE = new ErrorCode(1_002_018_000, "社交授权失败，原因是：{}");
    ErrorCode SOCIAL_USER_NOT_FOUND = new ErrorCode(1_002_018_001, "社交授权失败，找不到对应的用户");

    ErrorCode SOCIAL_CLIENT_WEIXIN_MINI_APP_PHONE_CODE_ERROR = new ErrorCode(1_002_018_200, "获得手机号失败");
    ErrorCode SOCIAL_CLIENT_NOT_EXISTS = new ErrorCode(1_002_018_201, "社交客户端不存在");
    ErrorCode SOCIAL_CLIENT_UNIQUE = new ErrorCode(1_002_018_201, "社交客户端已存在配置");

    // ========== 系统敏感词 1-002-019-000 =========
    ErrorCode SENSITIVE_WORD_NOT_EXISTS = new ErrorCode(1_002_019_000, "系统敏感词在所有标签中都不存在");
    ErrorCode SENSITIVE_WORD_EXISTS = new ErrorCode(1_002_019_001, "系统敏感词已在标签中存在");

    // ========== OAuth2 客户端 1-002-020-000 =========
    ErrorCode OAUTH2_CLIENT_NOT_EXISTS = new ErrorCode(1_002_020_000, "OAuth2 客户端不存在");
    ErrorCode OAUTH2_CLIENT_EXISTS = new ErrorCode(1_002_020_001, "OAuth2 客户端编号已存在");
    ErrorCode OAUTH2_CLIENT_DISABLE = new ErrorCode(1_002_020_002, "OAuth2 客户端已禁用");
    ErrorCode OAUTH2_CLIENT_AUTHORIZED_GRANT_TYPE_NOT_EXISTS = new ErrorCode(1_002_020_003, "不支持该授权类型");
    ErrorCode OAUTH2_CLIENT_SCOPE_OVER = new ErrorCode(1_002_020_004, "授权范围过大");
    ErrorCode OAUTH2_CLIENT_REDIRECT_URI_NOT_MATCH = new ErrorCode(1_002_020_005, "无效 redirect_uri: {}");
    ErrorCode OAUTH2_CLIENT_CLIENT_SECRET_ERROR = new ErrorCode(1_002_020_006, "无效 client_secret: {}");

    // ========== OAuth2 授权 1-002-021-000 =========
    ErrorCode OAUTH2_GRANT_CLIENT_ID_MISMATCH = new ErrorCode(1_002_021_000, "client_id 不匹配");
    ErrorCode OAUTH2_GRANT_REDIRECT_URI_MISMATCH = new ErrorCode(1_002_021_001, "redirect_uri 不匹配");
    ErrorCode OAUTH2_GRANT_STATE_MISMATCH = new ErrorCode(1_002_021_002, "state 不匹配");
    ErrorCode OAUTH2_GRANT_CODE_NOT_EXISTS = new ErrorCode(1_002_021_003, "code 不存在");

    // ========== OAuth2 授权 1-002-022-000 =========
    ErrorCode OAUTH2_CODE_NOT_EXISTS = new ErrorCode(1_002_022_000, "code 不存在");
    ErrorCode OAUTH2_CODE_EXPIRE = new ErrorCode(1_002_022_001, "code 已过期");

    // ========== 邮箱账号 1-002-023-000 ==========
    ErrorCode MAIL_ACCOUNT_NOT_EXISTS = new ErrorCode(1_002_023_000, "邮箱账号不存在");
    ErrorCode MAIL_ACCOUNT_RELATE_TEMPLATE_EXISTS = new ErrorCode(1_002_023_001, "无法删除，该邮箱账号还有邮件模板");

    // ========== 邮件模版 1-002-024-000 ==========
    ErrorCode MAIL_TEMPLATE_NOT_EXISTS = new ErrorCode(1_002_024_000, "邮件模版不存在");
    ErrorCode MAIL_TEMPLATE_CODE_EXISTS = new ErrorCode(1_002_024_001, "邮件模版 code({}) 已存在");

    // ========== 邮件发送 1-002-025-000 ==========
    ErrorCode MAIL_SEND_TEMPLATE_PARAM_MISS = new ErrorCode(1_002_025_000, "模板参数({})缺失");
    ErrorCode MAIL_SEND_MAIL_NOT_EXISTS = new ErrorCode(1_002_025_001, "邮箱不存在");

    // ========== 站内信模版 1-002-026-000 ==========
    ErrorCode NOTIFY_TEMPLATE_NOT_EXISTS = new ErrorCode(1_002_026_000, "站内信模版不存在");
    ErrorCode NOTIFY_TEMPLATE_CLOSED = new ErrorCode(1_002_026_000, "站内信模版已关闭");
    ErrorCode NOTIFY_TEMPLATE_CODE_DUPLICATE = new ErrorCode(1_002_026_001, "已经存在编码为【{}】的站内信模板");

    // ========== 站内信模版 1-002-027-000 ==========

    // ========== 站内信发送 1-002-028-000 ==========
    ErrorCode NOTIFY_SEND_TEMPLATE_PARAM_MISS = new ErrorCode(1_002_028_000, "模板参数({})缺失");

    // ========== 国家信息 1-002-029-000 ==========
    ErrorCode COUNTRY_INFO_NOT_EXISTS = new ErrorCode(1_002_029_000, "国家信息不存在");
    ErrorCode SETTLEMENT_NOT_EXISTS = new ErrorCode(1_002_029_001, "收款方式不存在");
    ErrorCode SETTLEMENT_RATE_WRONG = new ErrorCode(1_002_029_002, "收款方式比例之和不等于100");

    //=========== 字段信息 1-002-030-000 ==========
    ErrorCode FIELD_NOT_EXISTS = new ErrorCode(1_002_030_000, "字段不存在");
    ErrorCode ROLE_FIELD_NOT_EXISTS = new ErrorCode(1_002_030_001, "字段权限不存在");

    //=========== 付款方式信息 1-002-031-000 ==========
    ErrorCode PAYMENT_NOT_EXISTS = new ErrorCode(1_002_031_000, "付款方式不存在");
    //=========== 法人单位信息 1-002-032-000 ==========
    ErrorCode COMPANY_NOT_EXISTS = new ErrorCode(1_002_032_000, "法人单位不存在,ID-{}");

    ErrorCode COMPANY_ENABLE = new ErrorCode(1_002_032_001, "法人单位已启用");
    ErrorCode COMPANY_DIS_ENABLE = new ErrorCode(1_002_032_002, "法人单位已禁用");

    ErrorCode COMPANY_NOT_CURRENCY_EXISTS = new ErrorCode(1_002_032_003, "主体采购币种不能为空");

    //=========== 价格条款信息 1-002-033-000 ==========
    ErrorCode PRICE_TYPE_NOT_EXISTS = new ErrorCode(1_002_033_000, "价格条款不存在");

    ErrorCode PORT_NOT_EXISTS = new ErrorCode(1_002_033_001, "口岸不存在");
    ErrorCode PATH_NOT_EXISTS = new ErrorCode(1_002_033_002, "路径不存在");
    ErrorCode PATH_PARENT_NOT_EXISTS = new ErrorCode(1_002_033_003, "未能找到层级为{}的父节点，无法挂载节点:{}");

    //=========== 费用归集 1-002-034-000 ==========

    ErrorCode FEE_SHARE_NOT_EXISTS = new ErrorCode(1_002_034_001, "费用归集不存在");
    ErrorCode FEE_SHARE_ITEM_NOT_EXISTS = new ErrorCode(1_002_034_002, "费用归集明细不存在");

    ErrorCode FEE_SHARE_DESC_NOT_EXISTS = new ErrorCode(1_002_034_003, "费用归集具体名称不存在");
    ErrorCode NOT_DEPARTMENT_EXISTS = new ErrorCode(1_002_034_004, "未查询到部门信息");
    ErrorCode DEPT_LEADER_EMPTY = new ErrorCode(1_002_034_005, "部门({})负责人为空");
    ErrorCode FEE_SHARE_DESC_IS_USED = new ErrorCode(1_002_034_006, "费用归集具体名称被使用，不允许删除");
    ErrorCode FEE_SHARE_BUSINESS_TYPE_MORE = new ErrorCode(1_002_034_007, "费用归集存在多个业务类型");
    ErrorCode FEE_SHARE_BUSINESS_TYPE_NONE = new ErrorCode(1_002_034_008, "费用归集没有业务类型");
    ErrorCode FEE_SHARE_BUSINESS_NOT_SAME = new ErrorCode(1_002_034_009, "费用归集传参业务来源编号不一致");
    ErrorCode FEE_SHARE_IS_PRE_COLLECTION = new ErrorCode(1_002_034_010, "预归集不能编辑");

    ErrorCode FORM_CHANGE_NOT_EXISTS = new ErrorCode(1_002_036_001, "表单字段变更管理信息不存在");
    ErrorCode FORM_CHANGE_ITEM_NOT_EXISTS = new ErrorCode(1_002_036_002, "表单字段变更管理子信息不存在");
    ErrorCode UNKNOWN_FEE_SHARE_TYPE = new ErrorCode(1_002_036_003, "未知归集类型");
    ErrorCode FEE_SHARE_NOT_EMPTY = new ErrorCode(1_002_036_004, "归集对象不可为空");

    //=========== 卡片 1-002-035-000 ==========
    ErrorCode CARD_NOT_EXISTS = new ErrorCode(1_002_035_001, "卡片不存在");
    ErrorCode CARD_ENABLE = new ErrorCode(1_002_035_002, "卡片已启用,请勿重复操作");
    ErrorCode CARD_DISABLE = new ErrorCode(1_002_035_003, "卡片已禁用,请勿重复操作");
    ErrorCode PARAM_ERROR = new ErrorCode(1_002_035_003, "参数错误");
    ErrorCode TABLE_NAME_EMPTY = new ErrorCode(1_002_035_004, "表名为空");
    ErrorCode FIELD_CHANGE_CONFIG_NOT_EXISTS = new ErrorCode(1_002_035_005, "未找到{}变更字段配置");


    //=========== 偏好设置 1-002-036-000 ==========
    ErrorCode USER_PREFENCE_NOT_EXISTS = new ErrorCode(1_002_036_001, "用户偏好设置不存在");
    ErrorCode CREATE_USER_IS_NULL = new ErrorCode(1_002_036_002, "创建人不存在");
}
