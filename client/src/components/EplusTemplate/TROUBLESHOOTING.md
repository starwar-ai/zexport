# 图片加载问题故障排查指南

## 🚨 问题描述

如果您遇到图片一直显示"加载中"状态，但实际上图片是存在的情况，请按照以下步骤进行排查。

## 🔍 第一步：检查浏览器控制台

### 1. 打开开发者工具

- 按 `F12` 或右键选择"检查"
- 切换到 `Console` 标签页

### 2. 查看错误信息

组件已经添加了详细的日志输出，您应该能看到类似这样的信息：

```
图片URL变化: {oldUrl: "", newUrl: "http://localhost:48080/admin-api/api/files/product1.jpg"}
开始加载图片: http://localhost:48080/admin-api/api/files/product1.jpg
开始预加载图片: http://localhost:48080/admin-api/api/files/product1.jpg
预加载图片失败: http://localhost:48080/admin-api/api/files/product1.jpg
图片加载失败: http://localhost:48080/admin-api/api/files/product1.jpg 重试次数: 0
准备第 1 次重试
```

### 3. 常见错误类型

- **404 Not Found**: 图片文件不存在
- **403 Forbidden**: 没有访问权限
- **CORS Error**: 跨域访问被阻止
- **Network Error**: 网络连接失败
- **Timeout**: 请求超时

## 🛠️ 第二步：使用诊断工具

### 1. 在控制台运行诊断

复制以下代码到浏览器控制台并运行：

```javascript
// 导入诊断工具（如果可用）
import { runDiagnosticsInConsole } from '@/utils/imageDiagnostics'

// 运行诊断
runDiagnosticsInConsole()
```

### 2. 手动诊断单个图片

```javascript
// 检查特定图片
const testImage = async (url) => {
  try {
    const response = await fetch(url, { method: 'HEAD' })
    console.log('状态码:', response.status)
    console.log('响应头:', Object.fromEntries(response.headers.entries()))
    console.log('可访问:', response.ok)
  } catch (error) {
    console.error('错误:', error.message)
  }
}

// 测试图片
testImage('http://localhost:48080/admin-api/api/files/product1.jpg')
```

## 🔧 第三步：常见问题解决方案

### 问题1：图片路径错误

**症状**: 控制台显示 404 错误 **解决方案**:

1. 检查图片路径是否正确
2. 确认文件是否存在于服务器
3. 检查路径拼接逻辑

```javascript
// 检查当前图片URL
console.log('当前图片URL:', currentImageUrl.value)
```

### 问题2：权限问题

**症状**: 控制台显示 403 错误 **解决方案**:

1. 检查文件访问权限
2. 确认用户是否有权限访问该文件
3. 检查服务器权限配置

### 问题3：跨域问题

**症状**: 控制台显示 CORS 错误 **解决方案**:

1. 检查服务器 CORS 配置
2. 确认图片域名是否在白名单中
3. 使用代理或调整图片服务配置

### 问题4：网络超时

**症状**: 控制台显示超时错误 **解决方案**:

1. 检查网络连接
2. 增加超时时间设置
3. 检查图片服务响应速度

```vue
<EplusImgEnlarge
  :main-picture="product.mainPicture"
  :timeout="20000"  <!-- 增加超时时间到20秒 -->
/>
```

## 📋 第四步：收集调试信息

### 1. 环境信息

```javascript
console.log('环境信息:', {
  userAgent: navigator.userAgent,
  baseURL: import.meta.env.VITE_BASE_URL,
  timestamp: new Date().toISOString()
})
```

### 2. 图片信息

```javascript
// 获取所有图片元素
const images = document.querySelectorAll('img')
console.log('页面图片数量:', images.length)

// 检查图片状态
images.forEach((img, index) => {
  console.log(`图片 ${index + 1}:`, {
    src: img.src,
    complete: img.complete,
    naturalWidth: img.naturalWidth,
    naturalHeight: img.naturalHeight,
    currentSrc: img.currentSrc
  })
})
```

### 3. 网络请求信息

在开发者工具的 `Network` 标签页中：

1. 刷新页面
2. 查看图片请求的状态
3. 检查请求头和响应头
4. 查看请求耗时

## 🎯 第五步：针对性解决

### 针对特定图片的问题

如果只有某些图片有问题，请检查：

1. **图片格式**: 确认图片格式是否支持（jpg, png, gif等）
2. **文件大小**: 检查图片文件是否过大
3. **文件完整性**: 确认图片文件是否损坏
4. **路径一致性**: 检查图片路径是否一致

### 针对所有图片的问题

如果所有图片都有问题，请检查：

1. **服务器配置**: 检查图片服务是否正常运行
2. **网络配置**: 检查网络连接和防火墙设置
3. **权限配置**: 检查文件访问权限设置
4. **环境变量**: 检查 `VITE_BASE_URL` 配置

## 📞 第六步：寻求帮助

如果以上步骤都无法解决问题，请提供以下信息：

1. **错误截图**: 包含控制台错误信息的截图
2. **环境信息**: 浏览器版本、操作系统等
3. **网络信息**: 网络环境、代理设置等
4. **复现步骤**: 详细的问题复现步骤
5. **相关代码**: 相关的组件代码和配置

## 🚀 预防措施

### 1. 定期检查

- 定期检查图片服务状态
- 监控图片加载成功率
- 检查服务器日志

### 2. 配置优化

- 合理设置超时时间
- 配置合适的重试策略
- 使用图片压缩和优化

### 3. 监控告警

- 设置图片加载失败告警
- 监控图片服务响应时间
- 建立问题快速响应机制

## 📚 相关资源

- [EplusImgEnlarge 组件文档](./README.md)
- [图片预加载工具](../utils/imagePreloader.ts)
- [图片加载 Hook](../hooks/useImageLoader.ts)
- [图片诊断工具](../utils/imageDiagnostics.ts)

---

**注意**: 如果问题仍然存在，请联系技术支持团队获取进一步帮助。
