/**
 * Axios 相关常量配置
 */

/**
 * 需要忽略的提示信息
 * 这些错误信息不会显示给用户
 */
export const IGNORE_ERROR_MSGS = [
  '无效的刷新令牌', // 刷新令牌被删除时，不用提示
  '刷新令牌已过期' // 使用刷新令牌，刷新获取新的访问令牌时，结果因为过期失败，此时需要忽略。否则，会导致继续 401，无法跳转到登出界面
] as const

/**
 * 白名单接口列表
 * 这些接口不需要 token 认证
 */
export const WHITE_LIST_URLS = ['/login', '/refresh-token'] as const

/**
 * HTTP 状态码
 */
export const HTTP_STATUS = {
  SUCCESS: 0,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  SERVER_ERROR: 500,
  LICENSE_ERROR: 901
} as const

/**
 * 内容类型
 */
export const CONTENT_TYPE = {
  JSON: 'application/json',
  FORM_URLENCODED: 'application/x-www-form-urlencoded',
  FORM_DATA: 'multipart/form-data'
} as const

/**
 * 请求超时时间 (毫秒)
 */
export const REQUEST_TIMEOUT = 60000

/**
 * Loading 延迟显示时间 (毫秒)
 */
export const LOADING_DELAY = 100

/**
 * 是否显示重新登录提示
 */
export const RELOGIN_STATUS = { show: false }

/**
 * 不需要显示 Loading 的 URL 模式列表
 * 支持字符串匹配，包含任一模式的 URL 将不显示 loading
 */
export const NO_LOADING_URLS = [
  '/polling',      // 轮询接口
  '/heartbeat',    // 心跳接口
  '/statistics',   // 统计接口
  '/metrics',      // 指标接口
  '/health',       // 健康检查
  '/ping',         // ping 接口
  '/page',         // 分页列表接口
  'Page'           // 分页列表接口（驼峰命名）
] as const



