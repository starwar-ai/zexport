import zhCN from '@/locales/zh-CN'

// 获取嵌套对象的值
function getNestedValue(obj: any, path: string): string {
  const keys = path.split('.')
  let result = obj
  
  for (const key of keys) {
    if (result && typeof result === 'object' && key in result) {
      result = result[key]
    } else {
      return path // 如果找不到对应的值，返回key本身
    }
  }
  
  return typeof result === 'string' ? result : path
}

// 返回中文翻译的 useI18n hook
export const useI18n = () => {
  const t = (key: string) => {
    return getNestedValue(zhCN, key)
  }
  return { t }
}

export const t = (key: string) => {
  return getNestedValue(zhCN, key)
}
