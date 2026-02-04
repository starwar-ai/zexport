/**
 * 快速图片测试工具
 * @author 波波
 * @date 2024-01-01
 */

/**
 * 快速测试图片加载
 * @param url 图片URL
 * @param timeout 超时时间（毫秒）
 */
export function quickTestImage(url: string, timeout = 10000): Promise<boolean> {
  return new Promise((resolve) => {
    console.log(`🔍 快速测试图片: ${url}`)

    const img = new Image()
    const startTime = Date.now()

    // 设置超时
    const timeoutId = setTimeout(() => {
      console.warn(`⏰ 图片加载超时: ${url}`)
      img.src = ''
      resolve(false)
    }, timeout)

    img.onload = () => {
      clearTimeout(timeoutId)
      const duration = Date.now() - startTime
      console.log(`✅ 图片加载成功: ${url} (耗时: ${duration}ms)`)
      resolve(true)
    }

    img.onerror = (error) => {
      clearTimeout(timeoutId)
      const duration = Date.now() - startTime
      console.error(`❌ 图片加载失败: ${url} (耗时: ${duration}ms)`, error)
      resolve(false)
    }

    img.src = url
  })
}

/**
 * 测试多个图片
 * @param urls 图片URL数组
 * @param timeout 超时时间（毫秒）
 */
export async function testMultipleImages(urls: string[], timeout = 10000): Promise<void> {
  console.group('🧪 批量测试图片加载')
  console.log(`开始测试 ${urls.length} 张图片`)

  const results = await Promise.all(
    urls.map(async (url) => {
      const success = await quickTestImage(url, timeout)
      return { url, success }
    })
  )

  const successCount = results.filter((r) => r.success).length
  const failCount = results.filter((r) => !r.success).length

  console.log(`\n📊 测试结果:`)
  console.log(`✅ 成功: ${successCount}`)
  console.log(`❌ 失败: ${failCount}`)
  console.log(`📈 成功率: ${((successCount / results.length) * 100).toFixed(1)}%`)

  if (failCount > 0) {
    console.log(`\n❌ 失败的图片:`)
    results
      .filter((r) => !r.success)
      .forEach((result, index) => {
        console.log(`${index + 1}. ${result.url}`)
      })
  }

  console.groupEnd()
}

/**
 * 在控制台中运行快速测试
 */
export function runQuickTestInConsole(): void {
  console.log('🚀 快速图片测试工具已加载')
  console.log('使用方法:')
  console.log('1. 测试单张图片: quickTestImage("图片URL")')
  console.log('2. 测试多张图片: testMultipleImages(["URL1", "URL2"])')
  console.log('3. 自动测试页面图片: autoTestPageImages()')

  // 添加到全局对象
  ;(window as any).quickTestImage = quickTestImage
  ;(window as any).testMultipleImages = testMultipleImages
  ;(window as any).autoTestPageImages = autoTestPageImages
}

/**
 * 自动测试页面中的所有图片
 */
export async function autoTestPageImages(): Promise<void> {
  console.group('🔄 自动测试页面图片')

  const images = document.querySelectorAll<HTMLImageElement>('img')
  const urls = Array.from(images)
    .map((img) => img.src)
    .filter((url) => url && !url.startsWith('data:'))

  console.log(`找到 ${urls.length} 张图片`)

  if (urls.length === 0) {
    console.log('没有找到需要测试的图片')
    console.groupEnd()
    return
  }

  await testMultipleImages(urls)
  console.groupEnd()
}

/**
 * 测试特定组件的图片
 * @param selector 组件选择器
 */
export async function testComponentImages(selector = '.eplus-img-enlarge img'): Promise<void> {
  console.group('🎯 测试组件图片')

  const images = document.querySelectorAll<HTMLImageElement>(selector)
  const urls = Array.from(images)
    .map((img) => img.src)
    .filter((url) => url && !url.startsWith('data:'))

  console.log(`在 ${selector} 中找到 ${urls.length} 张图片`)

  if (urls.length === 0) {
    console.log('没有找到需要测试的图片')
    console.groupEnd()
    return
  }

  await testMultipleImages(urls)
  console.groupEnd()
}

/**
 * 生成测试报告
 * @param results 测试结果
 */
export function generateTestReport(results: Array<{ url: string; success: boolean }>): string {
  const total = results.length
  const success = results.filter((r) => r.success).length
  const fail = total - success

  let report = `图片加载测试报告\n`
  report += `==================\n`
  report += `测试时间: ${new Date().toLocaleString()}\n`
  report += `总图片数: ${total}\n`
  report += `成功加载: ${success}\n`
  report += `加载失败: ${fail}\n`
  report += `成功率: ${((success / total) * 100).toFixed(1)}%\n\n`

  if (fail > 0) {
    report += `失败详情:\n`
    results
      .filter((r) => !r.success)
      .forEach((result, index) => {
        report += `${index + 1}. ${result.url}\n`
      })
  }

  return report
}

// 自动运行
if (typeof window !== 'undefined') {
  runQuickTestInConsole()
}
