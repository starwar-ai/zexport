---
name: performance-testing-skill
description: 性能优化和测试规范。提供组件懒加载、虚拟滚动、防抖节流、单元测试、E2E 测试等最佳实践。当进行性能优化、编写测试代码时使用此技能。
---

# 性能优化和测试规范

## 何时使用

- 进行性能优化时
- 编写单元测试时
- 编写 E2E 测试时
- 实现组件懒加载时
- 处理大数据量列表时
- 优化频繁触发的事件时

## 性能优化规范

### 组件懒加载

```typescript
// 路由懒加载
const UserList = () => import('@/views/user/list.vue')

// 组件懒加载
const LazyComponent = defineAsyncComponent(() => import('@/components/LazyComponent.vue'))

// 使用示例
<script setup lang="ts">
import { defineAsyncComponent } from 'vue'

const AsyncUserList = defineAsyncComponent({
  loader: () => import('@/components/UserList.vue'),
  loadingComponent: LoadingSpinner,
  errorComponent: ErrorComponent,
  delay: 200,
  timeout: 3000
})
</script>
```

### 虚拟滚动

```vue
<template>
  <el-table
    :data="tableData"
    height="400"
    :virtual-scrolling="{ enabled: true, itemSize: 50 }"
  >
    <el-table-column
      prop="name"
      label="姓名"
    />
    <el-table-column
      prop="email"
      label="邮箱"
    />
  </el-table>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const tableData = ref([])

// 大数据量表格优化
const loadTableData = async () => {
  // 分页加载数据
  const response = await api.getUserList({ pageNo: 1, pageSize: 1000 })
  tableData.value = response.data.list
}
</script>
```

### 图片懒加载

```vue
<template>
  <el-image
    :src="imageUrl"
    lazy
    :preview-src-list="[imageUrl]"
    fit="cover"
  />
</template>

<script setup lang="ts">
import { ref } from 'vue'

const imageUrl = ref('https://example.com/image.jpg')
</script>
```

### 防抖和节流

```typescript
// utils/debounce.ts
export function debounce<T extends (...args: any[]) => any>(
  func: T,
  wait: number
): (...args: Parameters<T>) => void {
  let timeout: NodeJS.Timeout

  return (...args: Parameters<T>) => {
    clearTimeout(timeout)
    timeout = setTimeout(() => func(...args), wait)
  }
}

// utils/throttle.ts
export function throttle<T extends (...args: any[]) => any>(
  func: T,
  wait: number
): (...args: Parameters<T>) => void {
  let lastCall = 0

  return (...args: Parameters<T>) => {
    const now = Date.now()
    if (now - lastCall >= wait) {
      lastCall = now
      func(...args)
    }
  }
}

// 使用示例
<script setup lang="ts">
import { debounce, throttle } from '@/utils'

const handleSearch = debounce((keyword: string) => {
  // 搜索逻辑
}, 300)

const handleScroll = throttle(() => {
  // 滚动逻辑
}, 100)
</script>
```

### 内存管理

```vue
<script setup lang="ts">
import { onMounted, onUnmounted } from 'vue'

let timer: NodeJS.Timeout
let eventListener: (event: Event) => void

onMounted(() => {
  // 设置定时器
  timer = setInterval(() => {
    // 定时任务
  }, 1000)

  // 添加事件监听器
  eventListener = (event: Event) => {
    // 事件处理
  }
  window.addEventListener('resize', eventListener)
})

onUnmounted(() => {
  // 清理定时器
  if (timer) {
    clearInterval(timer)
  }

  // 移除事件监听器
  if (eventListener) {
    window.removeEventListener('resize', eventListener)
  }
})
</script>
```

## 测试规范

### 单元测试 (Vitest)

```typescript
// tests/utils/date.test.ts
import { describe, it, expect } from 'vitest'
import { formatDate, isValidDate } from '@/utils/date'

describe('Date Utils', () => {
  it('should format date correctly', () => {
    const date = new Date('2023-01-01')
    expect(formatDate(date)).toBe('2023-01-01')
  })

  it('should validate date correctly', () => {
    expect(isValidDate('2023-01-01')).toBe(true)
    expect(isValidDate('invalid-date')).toBe(false)
  })
})
```

### 组件测试

```typescript
// tests/components/UserProfile.test.ts
import { mount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import UserProfile from '@/components/UserProfile.vue'

describe('UserProfile', () => {
  it('should render user info correctly', () => {
    const wrapper = mount(UserProfile, {
      props: {
        user: {
          name: 'John Doe',
          email: 'john@example.com'
        }
      }
    })

    expect(wrapper.text()).toContain('John Doe')
    expect(wrapper.text()).toContain('john@example.com')
  })

  it('should emit update event when edit button clicked', async () => {
    const wrapper = mount(UserProfile, {
      props: {
        user: { name: 'John Doe', email: 'john@example.com' }
      }
    })

    await wrapper.find('.edit-button').trigger('click')
    expect(wrapper.emitted('update')).toBeTruthy()
  })
})
```

### API 测试

```typescript
// tests/api/user.test.ts
import { describe, it, expect, vi } from 'vitest'
import { userApi } from '@/api/user'

// Mock axios
vi.mock('@/utils/request', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn()
  }
}))

describe('User API', () => {
  it('should get user list successfully', async () => {
    const mockResponse = {
      code: 0,
      data: {
        list: [{ id: 1, name: 'John' }],
        total: 1
      },
      message: 'success'
    }

    // Mock API response
    const { default: request } = await import('@/utils/request')
    vi.mocked(request.get).mockResolvedValue(mockResponse)

    const result = await userApi.getUserList({ pageNo: 1, pageSize: 10 })
    expect(result.data.list).toHaveLength(1)
    expect(result.data.list[0].name).toBe('John')
  })
})
```

### E2E 测试 (Playwright)

```typescript
// tests/e2e/login.spec.ts
import { test, expect } from '@playwright/test'

test('user can login successfully', async ({ page }) => {
  await page.goto('/login')

  await page.fill('[data-testid="username"]', 'admin')
  await page.fill('[data-testid="password"]', 'password')
  await page.click('[data-testid="login-button"]')

  await expect(page).toHaveURL('/dashboard')
  await expect(page.locator('[data-testid="user-menu"]')).toBeVisible()
})

test('user cannot login with invalid credentials', async ({ page }) => {
  await page.goto('/login')

  await page.fill('[data-testid="username"]', 'invalid')
  await page.fill('[data-testid="password"]', 'invalid')
  await page.click('[data-testid="login-button"]')

  await expect(page.locator('.el-message--error')).toBeVisible()
})
```

### 测试配置

```typescript
// vitest.config.ts
import { defineConfig } from 'vitest/config'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: ['./tests/setup.ts']
  },
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  }
})
```

## 性能监控

### 性能指标

```typescript
// utils/performance.ts
export function measurePerformance(name: string, fn: () => void) {
  const start = performance.now()
  fn()
  const end = performance.now()
  console.log(`${name} took ${end - start} milliseconds`)
}

// 使用示例
measurePerformance('Data Processing', () => {
  // 数据处理逻辑
})
```

### 错误监控

```typescript
// utils/error-monitor.ts
export function setupErrorMonitor() {
  window.addEventListener('error', (event) => {
    console.error('Global Error:', event.error)
    // 发送错误到监控服务
  })

  window.addEventListener('unhandledrejection', (event) => {
    console.error('Unhandled Promise Rejection:', event.reason)
    // 发送错误到监控服务
  })
}
```

## 最佳实践

1. **组件懒加载** - 减少初始包大小
2. **虚拟滚动** - 处理大数据量列表
3. **防抖节流** - 优化频繁触发的事件
4. **内存管理** - 及时清理定时器和事件监听器
5. **单元测试** - 保证代码质量和重构安全
6. **E2E 测试** - 验证用户流程
7. **性能监控** - 持续优化应用性能
