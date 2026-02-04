# EplusImgEnlarge 图片显示组件

## 概述

`EplusImgEnlarge` 是一个优化的图片显示组件，专门用于外销合同等业务场景中的产品图片展示。该组件解决了图片加载不稳定的问题，提供了完善的加载状态管理和性能优化。

## 主要特性

### 🚀 性能优化

- **图片预加载** - 提前加载图片，提升用户体验
- **智能缓存** - 避免重复加载相同图片
- **并发控制** - 控制同时加载的图片数量，避免阻塞

### 🛡️ 错误处理

- **静默重试** - 网络异常时自动重试加载，不显示错误状态
- **超时控制** - 设置加载超时，避免无限等待
- **默认图片降级** - 3次重试失败后显示默认图片，保持界面美观

### 📱 用户体验

- **持续加载状态** - 加载失败时继续显示"加载中"，避免界面闪烁
- **平滑过渡** - 图片加载完成后的淡入效果
- **响应式设计** - 支持不同尺寸的图片显示

### 🔧 可配置性

- **重试策略** - 可配置重试次数和延迟
- **超时设置** - 可配置加载超时时间
- **默认图片** - 可配置加载失败后的默认图片
- **样式定制** - 支持自定义宽高和样式

## 使用方法

### 基础用法

```vue
<template>
  <EplusImgEnlarge
    :main-picture="product.mainPicture"
    :thumbnail="product.thumbnail"
    width="60px"
    height="60px"
  />
</template>

<script setup lang="ts">
import EplusImgEnlarge from '@/components/EplusTemplate/src/EplusImgEnlarge.vue'

const product = {
  mainPicture: { fileUrl: '/api/files/product1.jpg' },
  thumbnail: '/api/files/product1_thumb.jpg'
}
</script>
```

### 配置默认图片

```vue
<template>
  <EplusImgEnlarge
    :main-picture="product.mainPicture"
    :thumbnail="product.thumbnail"
    width="80px"
    height="80px"
    default-image-url="/api/files/default-product.jpg"
    :retry-count="5"
    :timeout="15000"
  />
</template>
```

### 在表格中使用

```typescript
// 表格列配置
const columns = [
  {
    prop: 'mainPicture',
    label: '图片',
    minWidth: '60px',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusImgEnlarge
            mainPicture={row?.mainPicture}
            thumbnail={row?.thumbnail}
            width="50px"
            height="50px"
            default-image-url="/api/files/default-product.jpg"
          />
        )
      }
    }
  }
]
```

## 属性说明

| 属性名            | 类型                    | 默认值   | 说明                     |
| ----------------- | ----------------------- | -------- | ------------------------ |
| `mainPicture`     | `ImageSource \| string` | -        | 主图片，支持对象或字符串 |
| `thumbnail`       | `string`                | -        | 缩略图路径               |
| `width`           | `number \| string`      | `'40px'` | 图片容器宽度             |
| `height`          | `number \| string`      | `'40px'` | 图片容器高度             |
| `disabled`        | `boolean`               | `false`  | 是否禁用                 |
| `retryCount`      | `number`                | `3`      | 重试次数                 |
| `timeout`         | `number`                | `10000`  | 加载超时时间（毫秒）     |
| `showLoading`     | `boolean`               | `true`   | 是否显示加载状态         |
| `defaultImageUrl` | `string`                | `''`     | 加载失败后的默认图片路径 |

## 事件说明

| 事件名  | 说明         | 回调参数         |
| ------- | ------------ | ---------------- |
| `load`  | 图片加载成功 | `(event: Event)` |
| `error` | 图片加载失败 | `(event: Event)` |

## 插槽说明

| 插槽名    | 说明                     |
| --------- | ------------------------ |
| `default` | 弹窗内容，默认为大图显示 |

## 类型定义

```typescript
// 图片源类型
type ImageSource = { fileUrl: string } | string

// 组件属性接口
interface Props {
  mainPicture?: ImageSource
  thumbnail?: string
  width?: number | string
  height?: number | string
  disabled?: boolean
  retryCount?: number
  timeout?: number
  showLoading?: boolean
  defaultImageUrl?: string
}
```

## 错误处理机制

### 静默重试策略

组件采用静默重试策略，确保用户体验的连续性：

1. **首次加载失败** → 继续显示"加载中"状态
2. **自动重试** → 递增延迟重试（1秒、2秒、3秒）
3. **重试失败** → 显示默认图片，不显示错误状态
4. **超时处理** → 超时后也显示默认图片

### 优势

- **界面稳定** - 不会出现错误状态的闪烁
- **用户体验** - 始终有内容显示，不会出现空白
- **自动恢复** - 网络恢复后自动重新加载
- **降级显示** - 确保关键信息始终可见

## 样式定制

组件提供了丰富的 CSS 类名，可以通过覆盖样式来自定义外观：

```scss
.image-container {
  // 图片容器样式
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.image-loading {
  // 加载中状态样式
  background-color: #f0f2f5;
}

.image-empty {
  // 无图片状态样式
  background-color: #fafafa;
}

.product-image {
  // 产品图片样式
  border-radius: 4px;
}
```

## 性能优化建议

### 1. 使用缩略图

优先使用缩略图作为小图显示，大图仅在弹窗中显示：

```vue
<EplusImgEnlarge
  :main-picture="product.mainPicture"
  :thumbnail="product.thumbnail"  <!-- 优先使用缩略图 -->
  width="60px"
  height="60px"
/>
```

### 2. 配置默认图片

为不同产品类型配置合适的默认图片：

```vue
<EplusImgEnlarge
  :main-picture="product.mainPicture"
  default-image-url="/api/files/default-tool.jpg"  <!-- 工具类默认图片 -->
/>
```

### 3. 合理设置重试参数

根据网络环境调整重试次数和超时时间：

```vue
<EplusImgEnlarge
  :retry-count="3"      <!-- 网络较好时减少重试次数 -->
  :timeout="8000"       <!-- 适当减少超时时间 -->
/>
```

### 4. 批量预加载

在页面加载完成后，预加载当前页面的所有图片：

```typescript
import { preloadPageImages } from '@/utils/imagePreloader'

// 在页面加载完成后预加载图片
onMounted(() => {
  preloadPageImages('.product-image')
})
```

## 常见问题

### Q: 图片有时加载不出来，刷新后又正常？

A: 这通常是由于网络不稳定或图片服务响应慢导致的。组件已内置自动重试机制，会自动处理这种情况，并且不会显示错误状态。

### Q: 如何提高图片加载速度？

A: 建议使用缩略图、启用图片预加载、合理设置缓存策略，并配置合适的默认图片。

### Q: 图片加载失败时为什么不显示错误信息？

A: 为了保持界面的美观和用户体验的连续性，组件采用静默重试策略，失败后显示默认图片而不是错误状态。

### Q: 如何监控图片加载性能？

A: 组件提供了加载耗时统计，可以通过 `duration` 属性获取。

### Q: 默认图片如何配置？

A: 通过 `default-image-url` 属性配置，支持相对路径和绝对路径。

## 更新日志

### v2.1.0 (2024-01-01)

- ✨ 新增静默错误处理机制
- ✨ 新增默认图片降级显示
- ✨ 优化加载状态管理
- 🐛 修复界面闪烁问题
- 🎨 提升用户体验连续性

### v2.0.0 (2024-01-01)

- ✨ 新增完善的加载状态管理
- ✨ 新增自动重试机制
- ✨ 新增加载超时控制
- ✨ 新增图片预加载功能
- ✨ 新增性能监控
- 🐛 修复图片加载不稳定的问题
- 🎨 优化用户界面和交互体验

### v1.0.0

- 🎉 初始版本发布

## 技术支持

如有问题或建议，请联系开发团队或提交 Issue。
