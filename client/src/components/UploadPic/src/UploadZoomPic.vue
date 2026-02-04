<template>
  <div class="imgList">
    <div
      v-for="file in fileList"
      :key="file.id"
      class="imgItem"
    >
      <div
        class="item_main"
        style="width: 100%; height: 100%"
      >
        <img
          :src="setUrl(file)"
          alt=""
          style="width: 100%; height: 100%; object-fit: contain"
        />
      </div>
      <div
        class="item_tag"
        v-if="file.mainFlag == 1"
        >主图
      </div>
      <div class="item_mask">
        <span @click="handlePictureCardPreview(file)">
          <Icon
            icon="ep:zoom-in"
            style="font-size: 18px"
          />
        </span>
        <span
          v-if="!props.disabled"
          @click="handleRemove(file)"
        >
          <Icon
            icon="ep:delete"
            style="font-size: 18px"
          />
        </span>
        <span
          style="font-size: 12px"
          v-if="!props.disabled && props.mainFlag && file.mainFlag == 0"
          @click="setMainImg(file)"
        >
          设置主图
        </span>
      </div>
    </div>
    <div
      class="addBtn"
      @click="handleOpen"
      v-if="!props.disabled && fileList.length < limit"
    >
      <Icon
        icon="ep:picture"
        style="font-size: 28px"
      />
      <span class="uploadText">上传图片</span>
    </div>
  </div>
  <span v-if="props.disabled && fileList.length == 0">无</span>

  <!-- 预览弹窗，支持拖拽、缩放、原生拖动到桌面 -->
  <div
    v-show="previewVisible"
    class="preview-overlay"
    @click="handleOverlayClick"
  >
    <div
      class="preview-window"
      :style="windowStyle"
      @click.stop
    >
      <div
        class="preview-header"
        @mousedown="onWindowDragStart"
      >
        <button
          type="button"
          @click="closePreview"
          @mousedown.stop
          class="close-btn"
          title="关闭预览"
          >&times;</button
        >
      </div>
      <div class="preview-content">
        <img
          :src="previewImage"
          class="preview-img"
          draggable="true"
        />
      </div>
      <button
        v-if="fileList.length > 1"
        type="button"
        class="nav-arrow prev"
        @click.stop="prevImage"
        title="上一张 (←)"
      >
        &lt;
      </button>
      <button
        v-if="fileList.length > 1"
        type="button"
        class="nav-arrow next"
        @click.stop="nextImage"
        title="下一张 (→)"
      >
        &gt;
      </button>
      <div
        class="resize-handle se"
        @mousedown.stop="onWindowResizeStart($event, 'se')"
      ></div>
      <div
        class="resize-handle sw"
        @mousedown.stop="onWindowResizeStart($event, 'sw')"
      ></div>
      <div
        class="resize-handle nw"
        @mousedown.stop="onWindowResizeStart($event, 'nw')"
      ></div>
      <div
        class="resize-handle ne"
        @mousedown.stop="onWindowResizeStart($event, 'ne')"
      ></div>
    </div>
  </div>

  <CopperModal
    ref="cropperModelRef"
    @upload-success="handleUpload"
  />
</template>

<script lang="ts" setup>
import { onMounted, onUnmounted, ref, watchEffect } from 'vue'
import { useMessage } from '@/hooks/web/useMessage'
import { uploadApi } from '@/api/common'
import CopperModal from './CopperModal.vue'

interface FileItem {
  id: string | number
  fileUrl: string
  mainFlag: number
  [key: string]: any
}

const emit = defineEmits(['success', 'error', 'update:modelValue'])
const message = useMessage()
const fileList = ref<FileItem[]>([])

const props = withDefaults(
  defineProps<{
    pictureList?: FileItem[]
    disabled?: boolean
    mainFlag?: boolean
    limit?: number
    modelValue: FileItem[]
  }>(),
  {
    mainFlag: false,
    limit: 50
  }
)

watchEffect(() => {
  fileList.value = props?.modelValue || []
})

const cropperModelRef = ref()
const previewVisible = ref(false)
const previewImage = ref<string>('')

// 预览窗口的状态
const windowStyle = ref({
  width: '600px',
  height: '600px',
  top: '50%',
  left: '50%',
  transform: 'translate(-50%, -50%)'
})
const isDraggingWindow = ref(false)
const dragWindowStart = ref({ x: 0, y: 0 })
const windowStart = ref({ left: 0, top: 0 })
const currentIndex = ref(0)

const isResizingWindow = ref(false)
const resizeWindowStart = ref({ x: 0, y: 0, width: 0, height: 0 })
const resizeDirection = ref('')

// 拖拽窗口
const onWindowDragStart = (e: MouseEvent) => {
  if (e.button !== 0) return
  isDraggingWindow.value = true
  dragWindowStart.value = { x: e.clientX, y: e.clientY }
  const rect = (e.currentTarget as HTMLElement).parentElement!.getBoundingClientRect()
  windowStart.value = { left: rect.left, top: rect.top }

  windowStyle.value.left = `${rect.left}px`
  windowStyle.value.top = `${rect.top}px`
  windowStyle.value.transform = 'none'

  window.addEventListener('mousemove', onWindowDrag)
  window.addEventListener('mouseup', onWindowDragEnd)
}
const onWindowDrag = (e: MouseEvent) => {
  if (!isDraggingWindow.value) return
  const dx = e.clientX - dragWindowStart.value.x
  const dy = e.clientY - dragWindowStart.value.y
  windowStyle.value.left = `${windowStart.value.left + dx}px`
  windowStyle.value.top = `${windowStart.value.top + dy}px`
  windowStyle.value.transform = '' // 拖拽时清除 transform
}
const onWindowDragEnd = () => {
  isDraggingWindow.value = false
  window.removeEventListener('mousemove', onWindowDrag)
  window.removeEventListener('mouseup', onWindowDragEnd)
}

// 缩放窗口
const onWindowResizeStart = (e: MouseEvent, direction: string) => {
  isResizingWindow.value = true
  resizeDirection.value = direction
  const rect = (e.currentTarget as HTMLElement).parentElement!.getBoundingClientRect()
  resizeWindowStart.value = { x: e.clientX, y: e.clientY, width: rect.width, height: rect.height }
  windowStart.value = { left: rect.left, top: rect.top }

  windowStyle.value.left = `${rect.left}px`
  windowStyle.value.top = `${rect.top}px`
  windowStyle.value.width = `${rect.width}px`
  windowStyle.value.height = `${rect.height}px`
  windowStyle.value.transform = 'none'

  window.addEventListener('mousemove', onWindowResize)
  window.addEventListener('mouseup', onWindowResizeEnd)
}
const onWindowResize = (e: MouseEvent) => {
  if (!isResizingWindow.value) return
  const dx = e.clientX - resizeWindowStart.value.x
  const dy = e.clientY - resizeWindowStart.value.y

  const minWidth = 200
  const minHeight = 150

  let newWidth = resizeWindowStart.value.width
  let newHeight = resizeWindowStart.value.height
  let newLeft = windowStart.value.left
  let newTop = windowStart.value.top

  if (resizeDirection.value.includes('e')) {
    newWidth += dx
  }
  if (resizeDirection.value.includes('w')) {
    newWidth -= dx
    newLeft += dx
  }
  if (resizeDirection.value.includes('s')) {
    newHeight += dy
  }
  if (resizeDirection.value.includes('n')) {
    newHeight -= dy
    newTop += dy
  }

  if (newWidth > minWidth) {
    windowStyle.value.width = `${newWidth}px`
    windowStyle.value.left = `${newLeft}px`
  }

  if (newHeight > minHeight) {
    windowStyle.value.height = `${newHeight}px`
    windowStyle.value.top = `${newTop}px`
  }
}
const onWindowResizeEnd = () => {
  isResizingWindow.value = false
  window.removeEventListener('mousemove', onWindowResize)
  window.removeEventListener('mouseup', onWindowResizeEnd)
}

const prevImage = () => {
  currentIndex.value = (currentIndex.value - 1 + fileList.value.length) % fileList.value.length
  previewImage.value = setUrl(fileList.value[currentIndex.value])
}

const nextImage = () => {
  currentIndex.value = (currentIndex.value + 1) % fileList.value.length
  previewImage.value = setUrl(fileList.value[currentIndex.value])
}

const closePreview = () => {
  previewVisible.value = false
}

// 点击遮罩层关闭预览
const handleOverlayClick = (e: MouseEvent) => {
  // 只有点击遮罩层本身才关闭，点击预览窗口不关闭
  if (e.target === e.currentTarget) {
    closePreview()
  }
}

// 键盘事件处理
const handleKeydown = (e: KeyboardEvent) => {
  if (!previewVisible.value) return

  switch (e.key) {
    case 'ArrowLeft':
      if (fileList.value.length > 1) {
        e.preventDefault()
        prevImage()
      }
      break
    case 'ArrowRight':
      if (fileList.value.length > 1) {
        e.preventDefault()
        nextImage()
      }
      break
  }
}

const resetPreview = () => {
  windowStyle.value = {
    width: '600px',
    height: '600px',
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)'
  }
}

const handleOpen = () => {
  cropperModelRef.value?.openModal()
}

const setUrl = (file: FileItem) => {
  return `${import.meta.env.VITE_BASE_URL}/admin-api${file.fileUrl}`
}

const handleUpload = async ({ data, filename }: { data: Blob; filename: string }) => {
  let res = await uploadApi({ file: data, path: filename })
  fileList.value = [
    ...fileList.value,
    { ...res.data, mainFlag: fileList.value.length === 0 && props?.mainFlag ? 1 : 0 }
  ]
  emit('success', fileList.value)
  emit('update:modelValue', fileList.value)
  cropperModelRef.value?.closeModal()
}

const handlePictureCardPreview = (file: FileItem) => {
  currentIndex.value = fileList.value.findIndex((item) => item.id === file.id)
  // 先在隐藏状态下重置好位置
  resetPreview()
  // 然后再显示
  previewImage.value = setUrl(file)
  previewVisible.value = true
}

const handleRemove = (uploadFile: FileItem) => {
  if (props.disabled) {
    message.error('该状态不可删除')
    return false
  } else {
    let index = fileList.value.findIndex((item) => item.fileUrl == uploadFile.fileUrl)
    fileList.value.splice(index, 1)
    emit('success', fileList.value)
    emit('update:modelValue', fileList.value)
  }
}

const setMainImg = (file: FileItem) => {
  if (props.disabled) {
    message.error('该状态不可设置主图')
    return false
  } else {
    fileList.value.forEach((item) => {
      item.mainFlag = 0
    })
    file.mainFlag = 1
    let mainFile = fileList.value.filter((item) => item.mainFlag == 1)
    let otherFile = fileList.value.filter((item) => item.mainFlag == 0)
    fileList.value = [...mainFile, ...otherFile]
    emit('success', fileList.value)
    emit('update:modelValue', fileList.value)
  }
}

// 生命周期钩子 - 添加键盘事件监听
onMounted(() => {
  document.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleKeydown)
  // 清理可能存在的事件监听器
  window.removeEventListener('mousemove', onWindowDrag)
  window.removeEventListener('mouseup', onWindowDragEnd)
  window.removeEventListener('mousemove', onWindowResize)
  window.removeEventListener('mouseup', onWindowResizeEnd)
})
</script>

<style lang="scss" scoped>
.el-dialog__body {
  padding: 0;
}

.imgList {
  width: 100%;
  display: flex;
  flex-wrap: wrap;

  .imgItem {
    width: 148px;
    height: 148px;
    border-radius: 6px;
    margin-right: 8px;
    margin-bottom: 8px;
    border: 1px solid #dcdfe6;
    overflow: hidden;
    position: relative;

    .item_main {
      width: 100%;
      height: 100%;
      position: absolute;
      top: 0;
      left: 0;
      z-index: 2;
    }

    .item_tag {
      position: absolute;
      top: 5px;
      right: 5px;
      z-index: 2;
      font-size: 12px;
    }

    .item_mask {
      position: absolute;
      top: 148px;
      left: 0;
      width: 100%;
      height: 100%;
      z-index: 3;
      background: rgb(0 0 0 / 50%);
      color: #ccc;
      display: flex;
      justify-content: space-around;
      align-items: center;

      span {
        cursor: pointer;
        color: #fafafa;
      }
    }
  }

  .imgItem:hover {
    .item_mask {
      top: 0;
    }
  }

  .addBtn {
    width: 148px;
    height: 148px;
    background-color: #fafafa;
    border: 1px dashed #dcdfe6;
    border-radius: 6px;
    cursor: pointer;
    color: #ccc;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    font-size: 14px;

    .uploadText {
      margin-top: 20px;
    }
  }

  .addBtn:hover {
    border: 1px dashed #409eff;
  }
}

.toolbar-custom {
  display: flex;
  gap: 8px;
  padding: 8px;
  background: rgb(0 0 0 / 50%);
  border-radius: 4px;

  button {
    padding: 4px 12px;
    background: #fff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;

    &:hover {
      background: #409eff;
      color: #fff;
    }
  }
}

.preview-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgb(0 0 0 / 50%);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}

.preview-window {
  position: absolute;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgb(0 0 0 / 10%);
  overflow: hidden;
  user-select: none;
}

.preview-header {
  height: 30px;
  background-color: #f7f7f7;
  cursor: move;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 10px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #666;
  transition: color 0.3s;

  &:hover {
    color: #409eff;
  }

  &:focus {
    outline: none;
    color: #409eff;
  }
}

.preview-content {
  position: relative;
  height: calc(100% - 30px);
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;

  .preview-img {
    max-width: 100%;
    max-height: 100%;
    object-fit: contain;
    padding: 1rem;
    box-sizing: border-box;
  }
}

.resize-handle {
  position: absolute;
  background: transparent;
  z-index: 10;
}

.resize-handle.se {
  bottom: 0;
  right: 0;
  width: 10px;
  height: 10px;
  cursor: se-resize;
}

.resize-handle.sw {
  bottom: 0;
  left: 0;
  width: 10px;
  height: 10px;
  cursor: sw-resize;
}

.resize-handle.nw {
  top: 0;
  left: 0;
  width: 10px;
  height: 10px;
  cursor: nw-resize;
}

.resize-handle.ne {
  top: 0;
  right: 0;
  width: 10px;
  height: 10px;
  cursor: ne-resize;
}

.nav-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background-color: rgb(0 0 0 / 30%);
  color: white;
  border: none;
  font-size: 2rem;
  cursor: pointer;
  padding: 10px;
  z-index: 15;
  border-radius: 50%;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  line-height: 1;
  transition: background-color 0.3s;
}

.nav-arrow:hover {
  background-color: rgb(0 0 0 / 60%);
}

.nav-arrow.prev {
  left: 20px;
}

.nav-arrow.next {
  right: 20px;
}
</style>
