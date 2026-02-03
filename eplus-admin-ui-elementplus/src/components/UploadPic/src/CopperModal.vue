<template>
  <div>
    <Dialog
      v-model="dialogVisible"
      :canFullscreen="false"
      title="图片上传"
      :append-to-body="true"
      maxHeight="380px"
      width="800px"
    >
      <div :class="prefixCls">
        <div :class="`${prefixCls}-left`">
          <div :class="`${prefixCls}-cropper`">
            <div
              v-if="loading"
              :class="`${prefixCls}-loading`"
            >
              <el-icon class="is-loading"><Loading /></el-icon>
              <p>图片压缩中...</p>
            </div>
            <CropperImage
              v-else-if="src"
              :circled="circled"
              :src="src"
              height="300px"
              @cropend="handleCropend"
              @ready="handleReady"
            />
          </div>

          <div :class="`${prefixCls}-toolbar`">
            <el-upload
              :beforeUpload="handleBeforeUpload"
              :fileList="[]"
              :show-file-list="false"
              accept="image/*"
              class="flex"
            >
              <el-button
                type="primary"
                :loading="loading"
              >
                {{ loading ? '处理中...' : '选择图片' }}
              </el-button>
            </el-upload>
            <el-space>
              <el-tooltip
                :content="t('cropper.btn_reset')"
                placement="bottom"
              >
                <XButton
                  :disabled="!src"
                  preIcon="ant-design:reload-outlined"
                  size="small"
                  type="primary"
                  @click="handlerToolbar('reset')"
                />
              </el-tooltip>
              <el-tooltip
                :content="t('cropper.btn_rotate_left')"
                placement="bottom"
              >
                <XButton
                  :disabled="!src"
                  preIcon="ant-design:rotate-left-outlined"
                  size="small"
                  type="primary"
                  @click="handlerToolbar('rotate', -45)"
                />
              </el-tooltip>
              <el-tooltip
                :content="t('cropper.btn_rotate_right')"
                placement="bottom"
              >
                <XButton
                  :disabled="!src"
                  preIcon="ant-design:rotate-right-outlined"
                  size="small"
                  type="primary"
                  @click="handlerToolbar('rotate', 45)"
                />
              </el-tooltip>
              <el-tooltip
                :content="t('cropper.btn_scale_x')"
                placement="bottom"
              >
                <XButton
                  :disabled="!src"
                  preIcon="vaadin:arrows-long-h"
                  size="small"
                  type="primary"
                  @click="handlerToolbar('scaleX')"
                />
              </el-tooltip>
              <el-tooltip
                :content="t('cropper.btn_scale_y')"
                placement="bottom"
              >
                <XButton
                  :disabled="!src"
                  preIcon="vaadin:arrows-long-v"
                  size="small"
                  type="primary"
                  @click="handlerToolbar('scaleY')"
                />
              </el-tooltip>
              <el-tooltip
                :content="t('cropper.btn_zoom_in')"
                placement="bottom"
              >
                <XButton
                  :disabled="!src"
                  preIcon="ant-design:zoom-in-outlined"
                  size="small"
                  type="primary"
                  @click="handlerToolbar('zoom', 0.1)"
                />
              </el-tooltip>
              <el-tooltip
                :content="t('cropper.btn_zoom_out')"
                placement="bottom"
              >
                <XButton
                  :disabled="!src"
                  preIcon="ant-design:zoom-out-outlined"
                  size="small"
                  type="primary"
                  @click="handlerToolbar('zoom', -0.1)"
                />
              </el-tooltip>
            </el-space>
          </div>
        </div>
        <div :class="`${prefixCls}-right`">
          <div :class="`${prefixCls}-preview`">
            <img
              v-if="previewSource"
              :alt="t('cropper.preview')"
              :src="previewSource"
            />
          </div>
          <div :class="`${prefixCls}-drag`">
            <el-upload
              :beforeUpload="handleBeforeUpload"
              drag
              :show-file-list="false"
            >
              <div class="flex items-center justify-center text-gray-500">
                <Icon
                  icon="ep:upload-filled"
                  style="font-size: 20px; margin-right: 10px"
                />
                拖拽区
              </div>
            </el-upload>
          </div>
          <!-- <template v-if="previewSource">
            <div :class="`${prefixCls}-group`">
              <el-avatar
                :src="previewSource"
                size="large"
              />
              <el-avatar
                :size="48"
                :src="previewSource"
              />
              <el-avatar
                :size="64"
                :src="previewSource"
              />
              <el-avatar
                :size="80"
                :src="previewSource"
              />
            </div>
          </template> -->
        </div>
      </div>
      <template #footer>
        <el-button
          type="primary"
          @click="handleOk"
          >{{ t('cropper.okText') }}
        </el-button>
      </template>
    </Dialog>
  </div>
</template>
<script lang="ts" setup>
import { useDesign } from '@/hooks/web/useDesign'
import { dataURLtoBlob } from '@/utils/filt'
import { useI18n } from '@/hooks/web/useI18n'
import type { CropendResult, Cropper } from '@/components/Cropper/src/types'
import { propTypes } from '@/utils/propTypes'
import { CropperImage } from '@/components/Cropper'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'

defineOptions({ name: 'CopperModal' })

const props = defineProps({
  srcValue: propTypes.string.def(''),
  circled: propTypes.bool.def(false)
})
const emit = defineEmits(['uploadSuccess'])
const { t } = useI18n()
const { getPrefixCls } = useDesign()
const prefixCls = getPrefixCls('cropper-am')

const src = ref(props.srcValue)
const previewSource = ref('')
const cropper = ref<Cropper>()
const dialogVisible = ref(false)
const loading = ref(false)
let filename = ''
let scaleX = 1
let scaleY = 1

// 优化的文件读取函数
const readFileAsDataURL = (file: File): Promise<string> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = (e) => resolve(e.target?.result as string)
    reader.onerror = reject
    reader.readAsDataURL(file)
  })
}

// canvas 预缩放函数，返回File，支持quality参数
async function resizeImageWithCanvas(file: File, maxSize = 1200, quality = 0.8): Promise<File> {
  return new Promise((resolve, reject) => {
    const img = new window.Image()
    img.onload = function () {
      let { width, height } = img
      // 计算缩放比例
      let scale = 1
      if (width > height && width > maxSize) {
        scale = maxSize / width
      } else if (height > width && height > maxSize) {
        scale = maxSize / height
      } else if (width === height && width > maxSize) {
        scale = maxSize / width
      }
      width = Math.round(width * scale)
      height = Math.round(height * scale)
      const canvas = document.createElement('canvas')
      canvas.width = width
      canvas.height = height
      const ctx = canvas.getContext('2d')
      ctx?.drawImage(img, 0, 0, width, height)
      // 只支持jpg/png
      let type = file.type
      if (type !== 'image/png' && type !== 'image/jpeg') {
        type = 'image/jpeg'
      }
      canvas.toBlob(
        (blob) => {
          if (blob) {
            // 用File包装，保证后续处理一致
            const newFile = new File([blob], file.name, { type })
            resolve(newFile)
          } else {
            reject(new Error('canvas toBlob failed'))
          }
        },
        type,
        quality
      )
    }
    img.onerror = reject
    img.crossOrigin = 'Anonymous'
    const reader = new FileReader()
    reader.onload = (e) => {
      img.src = e.target?.result as string
    }
    reader.onerror = reject
    reader.readAsDataURL(file)
  })
}

// Block upload
async function handleBeforeUpload(file: File) {
  loading.value = true
  src.value = ''
  previewSource.value = ''
  try {
    console.time('图片处理耗时')
    const fileSize = file.size
    const sizeMB = (fileSize / 1024 / 1024).toFixed(2)
    console.log('原始文件大小:', sizeMB + 'MB')
    let processedFile: File = file
    // 只对大图做canvas缩放
    const img = new window.Image()
    const tempURL = URL.createObjectURL(file)
    img.src = tempURL
    await new Promise((resolve, reject) => {
      img.onload = resolve
      img.onerror = reject
    })
    if (img.width > 1200 || img.height > 1200) {
      console.log('使用canvas预缩放...')
      processedFile = await resizeImageWithCanvas(file, 1200, 0.8)
      console.log('canvas预缩放后大小:', (processedFile.size / 1024 / 1024).toFixed(2) + 'MB')
    } else {
      // 小图直接用原图
      console.log('图片尺寸较小，跳过缩放')
    }
    URL.revokeObjectURL(tempURL)
    // 读取文件为 base64
    const base64 = await readFileAsDataURL(processedFile)
    src.value = base64
    filename = file.name
    console.timeEnd('图片处理耗时')
  } catch (err) {
    console.error('图片处理失败:', err)
    try {
      const base64 = await readFileAsDataURL(file)
      src.value = base64
      filename = file.name
    } catch (fallbackErr) {
      console.error('备用方案也失败:', fallbackErr)
      ElMessage.error('图片处理失败，请重试')
    }
  } finally {
    loading.value = false
  }
  return false
}

function handleCropend({ imgBase64 }: CropendResult) {
  previewSource.value = imgBase64
}

function handleReady(cropperInstance: Cropper) {
  cropper.value = cropperInstance
}

function handlerToolbar(event: string, arg?: number) {
  if (event === 'scaleX') {
    scaleX = arg = scaleX === -1 ? 1 : -1
  }
  if (event === 'scaleY') {
    scaleY = arg = scaleY === -1 ? 1 : -1
  }
  cropper?.value?.[event]?.(arg)
}

async function handleOk() {
  const blob = dataURLtoBlob(previewSource.value)
  emit('uploadSuccess', {
    source: previewSource.value,
    data: blob,
    filename: `${Date.now()}${filename}`
  })
}

function openModal() {
  src.value = ''
  previewSource.value = ''
  dialogVisible.value = true
}

function closeModal() {
  src.value = ''
  previewSource.value = ''
  dialogVisible.value = false
}

defineExpose({ openModal, closeModal })
</script>
<style lang="scss" scoped>
$prefix-cls: #{$namespace}-cropper-am;

.#{$prefix-cls} {
  display: flex;

  &-left,
  &-right {
    height: 340px;
  }

  &-left {
    width: 55%;
  }

  &-right {
    width: 45%;
  }

  &-cropper {
    height: 300px;
    background: #eee;
    background-image:
      linear-gradient(
        45deg,
        rgb(0 0 0 / 25%) 25%,
        transparent 0,
        transparent 75%,
        rgb(0 0 0 / 25%) 0
      ),
      linear-gradient(
        45deg,
        rgb(0 0 0 / 25%) 25%,
        transparent 0,
        transparent 75%,
        rgb(0 0 0 / 25%) 0
      );
    background-position:
      0 0,
      12px 12px;
    background-size: 24px 24px;
    position: relative;
  }

  &-loading {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
    color: #666;

    .el-icon {
      font-size: 24px;
      margin-bottom: 8px;
    }

    p {
      margin: 0;
      font-size: 14px;
    }
  }

  &-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 10px;
  }

  &-preview {
    width: 220px;
    height: 220px;
    margin: 0 auto;
    overflow: hidden;
    border: 1px solid #eee;
    border-radius: 0%;

    img {
      width: 100%;
      height: 100%;
    }
  }

  &-drag {
    width: 220px;
    height: 100px;
    margin: 10px auto;
  }

  &-group {
    display: flex;
    padding-top: 8px;
    margin-top: 8px;
    border-top: 1px solid;
    justify-content: space-around;
    align-items: center;
  }
}
</style>
