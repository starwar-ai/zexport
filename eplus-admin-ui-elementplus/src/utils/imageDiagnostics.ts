/**
 * 图片诊断工具
 * @author 波波
 * @date 2024-01-01
 */

/**
 * 诊断单张图片
 * @param url 图片URL
 */
export async function diagnoseImage(url: string): Promise<{
  url: string
  exists: boolean
  status: number
  headers: Record<string, string>
  fileSize: number | null
  contentType: string | null
  error: string | null
  suggestions: string[]
}> {
  console.group(`🔍 诊断图片: ${url}`)

  const result = {
    url,
    exists: false,
    status: 0,
    headers: {},
    fileSize: null,
    contentType: null,
    error: null,
    suggestions: []
  }

  try {
    // 1. HEAD请求检查文件状态
    console.log('1️⃣ 检查文件状态...')
    const headResponse = await fetch(url, {
      method: 'HEAD',
      mode: 'cors'
    })

    result.status = headResponse.status
    result.exists = headResponse.ok

    // 获取响应头
    headResponse.headers.forEach((value, key) => {
      result.headers[key] = value
    })

    if (result.exists) {
      result.fileSize = parseInt(result.headers['content-length'] || '0')
      result.contentType = result.headers['content-type'] || null

      console.log(`✅ 文件存在`)
      console.log(`📏 文件大小: ${result.fileSize} bytes`)
      console.log(`📄 内容类型: ${result.contentType}`)

      // 2. 尝试实际加载图片
      console.log('2️⃣ 尝试加载图片...')
      const img = new Image()

      const loadPromise = new Promise<boolean>((resolve) => {
        img.onload = () => resolve(true)
        img.onerror = () => resolve(false)

        // 设置超时
        setTimeout(() => resolve(false), 10000)
      })

      img.src = url
      const loadSuccess = await loadPromise

      if (loadSuccess) {
        console.log('✅ 图片加载成功')
        result.suggestions.push('图片可以正常加载，问题可能在前端渲染')
      } else {
        console.log('❌ 图片加载失败')
        result.suggestions.push('图片加载失败，可能是格式问题或浏览器兼容性')
      }
    } else {
      console.log(`❌ 文件不存在 (状态码: ${result.status})`)

      if (result.status === 404) {
        result.suggestions.push('文件返回404，检查URL是否正确')
        result.suggestions.push('检查服务器文件路径配置')
      } else if (result.status === 403) {
        result.suggestions.push('文件访问被拒绝，检查权限设置')
      } else if (result.status >= 500) {
        result.suggestions.push('服务器内部错误，联系后端开发人员')
      }
    }
  } catch (error) {
    console.error('❌ 诊断过程出错:', error)
    result.error = error instanceof Error ? error.message : String(error)
    result.suggestions.push('网络请求失败，检查网络连接')
    result.suggestions.push('检查CORS配置')
  }

  // 3. 生成建议
  if (result.exists && !result.error) {
    if (result.contentType && !result.contentType.startsWith('image/')) {
      result.suggestions.push('内容类型不是图片格式，可能是文件损坏')
    }

    if (result.fileSize && result.fileSize < 100) {
      result.suggestions.push('文件大小异常小，可能是空文件或损坏文件')
    }

    if (result.fileSize && result.fileSize > 10 * 1024 * 1024) {
      result.suggestions.push('文件过大，可能影响加载性能')
    }
  }

  console.log('📋 诊断建议:')
  result.suggestions.forEach((suggestion, index) => {
    console.log(`   ${index + 1}. ${suggestion}`)
  })

  console.groupEnd()
  return result
}

/**
 * 批量诊断图片
 * @param urls 图片URL数组
 * @param concurrency 并发数
 */
export async function diagnoseImages(
  urls: string[],
  concurrency = 3
): Promise<Array<Awaited<ReturnType<typeof diagnoseImage>>>> {
  console.group(`🧪 批量诊断 ${urls.length} 张图片`)

  const results: Array<Awaited<ReturnType<typeof diagnoseImage>>> = []

  // 分批处理，避免同时发送太多请求
  for (let i = 0; i < urls.length; i += concurrency) {
    const batch = urls.slice(i, i + concurrency)
    const batchResults = await Promise.all(batch.map((url) => diagnoseImage(url)))
    results.push(...batchResults)

    // 批次间延迟
    if (i + concurrency < urls.length) {
      await new Promise((resolve) => setTimeout(resolve, 1000))
    }
  }

  // 生成统计报告
  const total = results.length
  const exists = results.filter((r) => r.exists).length
  const loadable = results.filter((r) => r.exists && !r.error).length

  console.log(`\n📊 诊断统计:`)
  console.log(`总图片数: ${total}`)
  console.log(`文件存在: ${exists}`)
  console.log(`可加载: ${loadable}`)
  console.log(`成功率: ${((loadable / total) * 100).toFixed(1)}%`)

  console.groupEnd()
  return results
}

/**
 * 分析图片加载问题
 * @param diagnosticResult 诊断结果
 */
export function analyzeImageLoadIssue(diagnosticResult: ReturnType<typeof diagnoseImage>): {
  problem: string
  severity: 'low' | 'medium' | 'high' | 'critical'
  solutions: string[]
} {
  const { exists, status, contentType, fileSize, error } = diagnosticResult

  if (error) {
    return {
      problem: '网络请求失败',
      severity: 'high',
      solutions: ['检查网络连接', '检查CORS配置', '检查防火墙设置', '尝试使用VPN或代理']
    }
  }

  if (!exists) {
    if (status === 404) {
      return {
        problem: '文件不存在',
        severity: 'critical',
        solutions: [
          '检查文件路径是否正确',
          '确认文件是否已上传',
          '检查服务器文件存储配置',
          '联系后端开发人员确认文件状态'
        ]
      }
    }

    if (status === 403) {
      return {
        problem: '访问权限不足',
        severity: 'high',
        solutions: [
          '检查用户权限设置',
          '确认文件访问策略',
          '检查认证token是否有效',
          '联系系统管理员'
        ]
      }
    }

    return {
      problem: `服务器错误 (${status})`,
      severity: 'high',
      solutions: ['检查服务器状态', '查看服务器错误日志', '联系后端开发人员', '稍后重试']
    }
  }

  if (contentType && !contentType.startsWith('image/')) {
    return {
      problem: '文件格式错误',
      severity: 'medium',
      solutions: [
        '检查文件是否损坏',
        '确认文件扩展名是否正确',
        '重新上传正确的图片文件',
        '检查文件转换过程'
      ]
    }
  }

  if (fileSize && fileSize < 100) {
    return {
      problem: '文件大小异常',
      severity: 'medium',
      solutions: ['检查文件是否为空', '确认文件上传是否完整', '重新上传文件', '检查文件压缩设置']
    }
  }

  return {
    problem: '未知问题',
    severity: 'low',
    solutions: ['刷新页面重试', '清除浏览器缓存', '检查浏览器控制台错误', '联系技术支持']
  }
}

/**
 * 生成诊断报告
 * @param results 诊断结果数组
 */
export function generateDiagnosticReport(results: Array<ReturnType<typeof diagnoseImage>>): string {
  let report = `图片诊断报告\n`
  report += `==================\n`
  report += `诊断时间: ${new Date().toLocaleString()}\n`
  report += `总图片数: ${results.length}\n\n`

  const exists = results.filter((r) => r.exists).length
  const loadable = results.filter((r) => r.exists && !r.error).length

  report += `统计信息:\n`
  report += `- 文件存在: ${exists}\n`
  report += `- 可加载: ${loadable}\n`
  report += `- 成功率: ${((loadable / results.length) * 100).toFixed(1)}%\n\n`

  if (results.some((r) => !r.exists)) {
    report += `❌ 不存在的文件:\n`
    results
      .filter((r) => !r.exists)
      .forEach((result, index) => {
        report += `${index + 1}. ${result.url} (状态码: ${result.status})\n`
      })
    report += `\n`
  }

  if (results.some((r) => r.error)) {
    report += `⚠️ 诊断出错的图片:\n`
    results
      .filter((r) => r.error)
      .forEach((result, index) => {
        report += `${index + 1}. ${result.url}\n   错误: ${result.error}\n`
      })
    report += `\n`
  }

  report += `详细诊断结果:\n`
  results.forEach((result, index) => {
    report += `${index + 1}. ${result.url}\n`
    report += `   状态: ${result.exists ? '存在' : '不存在'}\n`
    if (result.exists) {
      report += `   大小: ${result.fileSize || '未知'} bytes\n`
      report += `   类型: ${result.contentType || '未知'}\n`
    }
    if (result.suggestions.length > 0) {
      report += `   建议: ${result.suggestions.join('; ')}\n`
    }
    report += `\n`
  })

  return report
}

/**
 * 在控制台中运行诊断
 */
export function runDiagnosticsInConsole(): void {
  console.log('🔧 图片诊断工具已加载')
  console.log('使用方法:')
  console.log('1. 诊断单张图片: diagnoseImage("图片URL")')
  console.log('2. 批量诊断: diagnoseImages(["URL1", "URL2"])')
  console.log('3. 分析问题: analyzeImageLoadIssue(diagnosticResult)')
  console.log('4. 生成报告: generateDiagnosticReport(results)')

  // 添加到全局对象
  ;(window as any).diagnoseImage = diagnoseImage
  ;(window as any).diagnoseImages = diagnoseImages
  ;(window as any).analyzeImageLoadIssue = analyzeImageLoadIssue
  ;(window as any).generateDiagnosticReport = generateDiagnosticReport
}

// 自动运行
if (typeof window !== 'undefined') {
  runDiagnosticsInConsole()
}
