import type { App } from 'vue'
import { CACHE_KEY, useCache } from '@/hooks/web/useCache'

const { t } = useI18n() // 国际化

export function hasPermi(app: App<Element>) {
  app.directive('hasPermi', (el, binding) => {
    const { wsCacheSession } = useCache()
    const { value } = binding
    const all_permission = '*:*:*'
    const permissions = wsCacheSession.get(CACHE_KEY.USER).permissions
    if (value) {
      let permissionFlag = []
      if (value instanceof Array && value.length > 0) {
        permissionFlag = value
      } else {
        permissionFlag.push(value)
      }

      const hasPermissions = permissions.some((permission: string) => {
        return all_permission === permission || permissionFlag.includes(permission)
      })
      if (!hasPermissions) {
        el.parentNode && el.parentNode.removeChild(el)
      }
    } else {
      throw new Error('请设置指令值')
    }
  })
}
