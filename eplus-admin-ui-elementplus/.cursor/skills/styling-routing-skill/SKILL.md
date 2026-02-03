---
name: styling-routing-skill
description: 样式和路由规范。提供 SCSS 样式规范、Vue Router 配置、布局组件等最佳实践。当编写样式、配置路由、开发布局组件时使用此技能。
---

# 样式和路由规范

## 何时使用

- 编写 SCSS 样式时
- 配置 Vue Router 路由时
- 开发布局组件时
- 实现响应式设计时
- 自定义 Element Plus 样式时

## 样式规范 (SCSS)

### SCSS 使用规范

```scss
// 使用 BEM 命名规范
.user-profile {
  &__header {
    display: flex;
    align-items: center;

    &--loading {
      opacity: 0.6;
    }
  }

  &__content {
    padding: 16px;
  }

  &__footer {
    border-top: 1px solid #eee;
    padding-top: 16px;
  }
}

// 使用 CSS 变量
:root {
  --primary-color: #409eff;
  --success-color: #67c23a;
  --warning-color: #e6a23c;
  --danger-color: #f56c6c;
  --text-color: #303133;
  --border-color: #dcdfe6;
}
```

### 响应式设计

```scss
// 使用 mixin 定义断点
@mixin mobile {
  @media (max-width: 768px) {
    @content;
  }
}

@mixin tablet {
  @media (min-width: 769px) and (max-width: 1024px) {
    @content;
  }
}

@mixin desktop {
  @media (min-width: 1025px) {
    @content;
  }
}

// 使用示例
.responsive-component {
  @include mobile {
    padding: 8px;
  }

  @include tablet {
    padding: 16px;
  }

  @include desktop {
    padding: 24px;
  }
}
```

### Element Plus 样式覆盖

```scss
// 自定义 Element Plus 组件样式
.el-button {
  &--eplus {
    background-color: var(--primary-color);
    border-color: var(--primary-color);

    &:hover {
      background-color: darken(#409eff, 10%);
      border-color: darken(#409eff, 10%);
    }
  }
}

.el-table {
  .el-table__header {
    background-color: #f5f7fa;
  }

  .el-table__row {
    &:hover {
      background-color: #f5f7fa;
    }
  }
}
```

## 路由规范 (Vue Router)

### 路由配置

```typescript
// router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: {
          title: '仪表盘',
          icon: 'dashboard',
          requiresAuth: true
        }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        meta: {
          title: '用户管理',
          icon: 'user',
          requiresAuth: true,
          permissions: ['user:read']
        },
        children: [
          {
            path: 'list',
            name: 'UserList',
            component: () => import('@/views/user/list.vue'),
            meta: {
              title: '用户列表',
              requiresAuth: true
            }
          }
        ]
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录',
      requiresAuth: false
    }
  }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
```

### 路由守卫

```typescript
// router/guard.ts
import { Router } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

export function setupRouterGuard(router: Router) {
  router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore()

    // 设置页面标题
    document.title = to.meta.title ? `${to.meta.title} - Eplus Admin` : 'Eplus Admin'

    // 检查是否需要认证
    if (to.meta.requiresAuth !== false) {
      if (!userStore.isLoggedIn) {
        ElMessage.warning('请先登录')
        next('/login')
        return
      }

      // 检查权限
      if (to.meta.permissions) {
        const hasPermission = to.meta.permissions.some((permission: string) =>
          userStore.hasPermission(permission)
        )

        if (!hasPermission) {
          ElMessage.error('没有访问权限')
          next('/403')
          return
        }
      }
    }

    next()
  })
}
```

### 路由类型定义

```typescript
// types/router.ts
import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    icon?: string
    requiresAuth?: boolean
    permissions?: string[]
    keepAlive?: boolean
    hidden?: boolean
  }
}
```

## 布局组件规范

### 主布局结构

```vue
<!-- layout/index.vue -->
<template>
  <div class="app-layout">
    <el-container>
      <el-aside width="200px">
        <Sidebar />
      </el-aside>
      <el-container>
        <el-header height="60px">
          <Header />
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import Sidebar from './components/Sidebar.vue'
import Header from './components/Header.vue'
</script>

<style lang="scss" scoped>
.app-layout {
  height: 100vh;

  .el-header {
    background-color: #fff;
    border-bottom: 1px solid var(--border-color);
  }

  .el-aside {
    background-color: #304156;
  }

  .el-main {
    background-color: #f0f2f5;
    padding: 20px;
  }
}
</style>
```

## 最佳实践

1. **样式组织** - 使用 BEM 命名规范
2. **响应式设计** - 使用 mixin 定义断点
3. **CSS 变量** - 统一管理主题色彩
4. **路由懒加载** - 提高应用性能
5. **权限控制** - 路由级别的权限验证
6. **类型安全** - 为路由元信息定义类型
