import { floatToFixed2 } from '@/utils'

// 格式化金额【分转元】
// @ts-ignore
export const fenToYuanFormat = (_, __, cellValue: any, ___) => {
  return `￥${floatToFixed2(cellValue)}`
}
/**
 * 金额格式化
 * @param number 数字
 * @param currency 金额前缀
 */
export const formatCurrency = (number, currency = 'CNY', i18n = 'zh-CN'): string => {
  return Number(number).toLocaleString(i18n, {
    style: 'currency',
    currency: currency
  })
}
