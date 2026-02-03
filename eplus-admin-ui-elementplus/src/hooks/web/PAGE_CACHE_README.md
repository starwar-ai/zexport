# 页面缓存优化方案

## 问题描述
1. 屏幕关闭后再打开，页面有时候会重新加载，导致所有页面数据丢失
2. 有时候点击页面光加载不打开，只能重新刷新

## 解决方案

### 1. 添加 Pinia 持久化插件

**安装依赖：**
```bash
cd client
pnpm install pinia-plugin-persistedstate
```

### 2. 配置说明

#### 2.1 Store 持久化
已为 `tagsView` store 添加持久化配置，将访问过的页面记录保存到 `sessionStorage`，防止页面刷新后数据丢失。

位置：`client/src/store/modules/tagsView.ts`

```typescript
persist: {
  enabled: true,
  strategies: [
    {
      key: 'tagsView',
      storage: sessionStorage,
      paths: ['visitedViews']
    }
  ]
}
```

#### 2.2 页面缓存恢复
在路由守卫中添加缓存恢复逻辑，确保页面刷新后能正确恢复缓存状态。

位置：`client/src/permission.ts`

```typescript
// 恢复页面缓存（防止刷新后丢失）
tagsViewStore.restoreCachedViews()
```

#### 2.3 Keep-alive 优化
添加 `max` 属性限制缓存组件数量为 20，防止内存溢出。

位置：`client/src/layout/components/AppView.vue`

```vue
<keep-alive :include="getCaches" :max="20">
  <component :is="Component" :key="route.fullPath" />
</keep-alive>
```

### 3. 新增页面缓存管理工具

提供了 `usePageCache` 组合式函数，用于处理页面的激活/停用状态。

位置：`client/src/hooks/web/usePageCache.ts`

#### 基础使用
```vue
<script setup lang="ts">
import { usePageCache } from '@/hooks/web/usePageCache'

const { isActivated, isFirstMount } = usePageCache({
  // 页面激活时的回调
  onActivatedCallback: () => {
    console.log('页面已激活')
    // 可以在这里刷新数据
  },
  // 页面停用时的回调
  onDeactivatedCallback: () => {
    console.log('页面已停用')
    // 可以在这里保存状态
  },
  // 自动恢复滚动位置
  autoRestoreScroll: true
})
</script>
```

#### 配合数据刷新使用
```vue
<script setup lang="ts">
import { usePageCache, usePageRefresh } from '@/hooks/web/usePageCache'

const { isActivated } = usePageCache()
const { refresh, isRefreshing } = usePageRefresh()

// 刷新数据
const loadData = async () => {
  await refresh(async () => {
    // 加载数据的逻辑
  })
}

// 页面激活时自动刷新
watch(isActivated, (activated) => {
  if (activated) {
    loadData()
  }
})
</script>
```

### 4. 路由元信息配置

如果某些页面不需要缓存，可以在路由配置中设置：

```typescript
{
  path: '/some-page',
  component: () => import('@/views/SomePage.vue'),
  meta: {
    noCache: true // 不缓存此页面
  }
}
```

### 5. 在现有页面中应用

对于需要优化的页面（如 CorporatePaymentsForm.vue），可以添加：

```vue
<script setup lang="ts">
import { usePageCache } from '@/hooks/web/usePageCache'

// 添加页面缓存管理
const { isActivated, isFirstMount } = usePageCache({
  onActivatedCallback: async () => {
    // 页面激活时重新验证数据
    if (!isFirstMount.value) {
      // 非首次加载时，可以选择性刷新数据
      console.log('页面从缓存中恢复')
    }
  }
})
</script>
```

## 测试步骤

1. **安装依赖**
   ```bash
   cd client
   pnpm install
   ```

2. **启动项目**
   ```bash
   pnpm run dev:localhost
   ```

3. **验证缓存功能**
   - 打开多个页面
   - 在页面中填写一些数据（不提交）
   - 切换到其他页面
   - 关闭屏幕或锁定电脑
   - 重新打开屏幕
   - 切换回之前的页面，数据应该保留

4. **验证刷新功能**
   - F5 刷新浏览器
   - 之前打开的标签页应该保留
   - 点击标签页能正常打开，不会卡住

## 注意事项

1. **内存管理**：设置了最大缓存 20 个组件，超过后会自动清理最早的缓存
2. **数据安全**：使用 sessionStorage 存储，浏览器关闭后会自动清除
3. **组件命名**：确保需要缓存的组件都有唯一的 `name` 选项
4. **大数据量**：如果页面数据量很大，建议在 `onDeactivated` 时清理部分临时数据

## 性能优化建议

1. 对于数据量大的列表页，考虑使用虚拟滚动
2. 定期清理不需要的缓存标签页
3. 避免在 `keep-alive` 组件中使用过多的全局事件监听
4. 在 `onDeactivated` 中取消未完成的请求

## 常见问题

### Q: 页面还是会重新加载？
A: 检查以下几点：
- 组件是否设置了 `name` 选项
- 路由配置中是否设置了 `meta.noCache: true`
- 浏览器是否启用了隐私模式（会禁用 sessionStorage）

### Q: 缓存的数据不对？
A: 使用 `refreshKey` 或在 `onActivated` 中验证和刷新数据

### Q: 内存占用过高？
A: 减少 `max` 值或为大型组件设置 `noCache: true`
