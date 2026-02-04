import imageCompression from 'browser-image-compression'

interface ThumbnailOptions {
  width: number
  height: number
  quality?: number
}

/**
 * 根据主图生成缩略图
 * @param imageUrl 原始图片URL
 * @param options 缩略图配置选项
 * @returns Promise<string> 返回缩略图base64数据
 */
export const generateThumbnail = async (
  imageUrl: string,
  options: ThumbnailOptions = { width: 100, height: 100, quality: 0.8 }
): Promise<string> => {
  if (!imageUrl) return ''

  try {
    // 对URL进行编码处理
    const encodedUrl = encodeURI(imageUrl)
    console.log('编码后的URL:', encodedUrl)

    // 获取原始图片
    const response = await fetch(encodedUrl)
    if (!response.ok) {
      throw new Error(`获取图片失败: ${response.status} ${response.statusText}`)
    }

    let imageBlob = await response.blob()
    console.log('获取到的文件类型:', imageBlob.type)

    // 处理文件类型
    const validImageTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp', 'image/jpg']
    if (
      !validImageTypes.includes(imageBlob.type) ||
      imageBlob.type === 'application/octet-stream'
    ) {
      // 尝试通过文件扩展名判断
      const fileExtension = encodedUrl.split('.').pop()?.toLowerCase()
      const validExtensions = ['jpg', 'jpeg', 'png', 'gif', 'webp']

      if (!fileExtension || !validExtensions.includes(fileExtension)) {
        throw new Error(
          `不支持的图片类型: ${imageBlob.type || '未知类型'}, 文件扩展名: ${fileExtension || '无'}`
        )
      }

      // 如果扩展名有效，强制设置正确的MIME类型
      const mimeTypeMap: Record<string, string> = {
        jpg: 'image/jpeg',
        jpeg: 'image/jpeg',
        png: 'image/png',
        gif: 'image/gif',
        webp: 'image/webp'
      }

      // 创建新的Blob对象
      const arrayBuffer = await imageBlob.arrayBuffer()
      imageBlob = new Blob([arrayBuffer], { type: mimeTypeMap[fileExtension] })
      console.log('重新设置的文件类型:', imageBlob.type)
    }

    // 压缩配置
    const compressionOptions = {
      maxWidthOrHeight: Math.max(options.width, options.height),
      maxSizeMB: 1,
      useWebWorker: true,
      quality: options.quality
    }

    console.log('开始压缩图片...')
    // 压缩图片
    const compressedBlob = await imageCompression(imageBlob, compressionOptions)
    console.log('图片压缩完成')

    // 转换为base64
    const result = await imageCompression.getDataUrlFromFile(compressedBlob)
    console.log('转换base64完成')
    return result
  } catch (error) {
    console.error('生成缩略图失败:', error)
    if (error instanceof Error) {
      console.error('错误详情:', {
        message: error.message,
        stack: error.stack,
        url: imageUrl
      })
    }
    return imageUrl // 失败时返回原图
  }
}
