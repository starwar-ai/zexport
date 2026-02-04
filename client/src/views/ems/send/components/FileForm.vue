<template>
  <Dialog
    v-model="dialogVisible"
    title="上传文件"
    :append-to-body="true"
    :before-close="close"
  >
    <el-upload
      ref="uploadRef"
      v-model:file-list="fileList"
      :action="url"
      :auto-upload="false"
      :data="data"
      :disabled="formLoading"
      :headers="uploadHeaders"
      :limit="1"
      :on-change="handleFileChange"
      :on-error="submitFormError"
      :on-exceed="handleExceed"
      :on-success="submitFormSuccess"
      :before-upload="beforeUpload"
      drag
      :accept="accept"
    >
      <i class="el-icon-upload"></i>
      <div class="el-upload__text"> 将文件拖到此处，或 <em>点击上传</em></div>
      <template #tip>
        <div
          class="el-upload__tip"
          style="color: red"
        >
          {{ props?.accept ? `请传入${props?.accept}格式的文件,且大小不超过50MB` : '' }}
          <!-- 提示：仅允许导入 jpg、png、gif 格式文件！ -->
        </div>
      </template>
    </el-upload>
    <template #footer>
      <el-button
        :disabled="formLoading"
        type="primary"
        @click="submitFileForm"
        >确 定</el-button
      >
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import { getAccessToken } from '@/utils/auth'
import { getSystemEmpty } from '@/api/system/empty/index'

defineOptions({ name: 'InfraFileForm' })
const props = defineProps<{
  accept: string
  uploadUrl?: string
}>()
const { t } = useI18n() // 国际化
const message = useMessage() // 消息弹窗

const dialogVisible = ref(false) // 弹窗的是否展示
const formLoading = ref(false) // 表单的加载中
const url = import.meta.env.VITE_BASE_URL + '/admin-api/ems/send-bill/import'
const uploadHeaders = ref() // 上传 Header 头
const fileList = ref([]) // 文件列表
const data = ref({ expressCompanyCode: 'SF' })
const uploadRef = ref()

/** 打开弹窗 */
const open = async () => {
  dialogVisible.value = true
  resetForm()
}
defineExpose({ open }) // 提供 open 方法，用于打开弹窗

/** 处理上传的文件发生变化 */
const handleFileChange = (file) => {
  // data.value.path = file.name
  data.value.expressCompanyCode = 'SF'
}
const close = () => {
  dialogVisible.value = false
}
/** 提交表单 */
const submitFileForm = () => {
  if (fileList.value.length == 0) {
    message.error('请上传文件')
    return
  }
  // 提交请求
  uploadHeaders.value = {
    Authorization: 'Bearer ' + getAccessToken()
  }
  unref(uploadRef)?.submit()
}

/** 文件上传成功处理 */
const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
const submitFormSuccess = (res) => {
  if (res.code != 0) {
    message.error('上传失败，请您重新上传！详情：' + res.msg)
    formLoading.value = false
    return
  }
  // 清理
  dialogVisible.value = false
  formLoading.value = false
  unref(uploadRef)?.clearFiles()
  // 提示成功，并刷新
  message.success(t('common.createSuccess'))
  emit('success', { name: data.value.path, ...res.data })
}

/** 上传错误提示 */
const submitFormError = (): void => {
  message.error('上传失败，请您重新上传！')
  formLoading.value = false
}

/** 重置表单 */
const resetForm = () => {
  // 重置上传状态和文件
  formLoading.value = false
  uploadRef.value?.clearFiles()
}

/** 文件数超出提示 */
const handleExceed = (): void => {
  message.error('最多只能上传一个文件！')
}

const beforeUpload = async (rawFile) => {
  const fileType = rawFile.name.substring(rawFile.name.lastIndexOf('.') + 1)
  if (rawFile.size / 1024 / 1024 > 50) {
    message.error('上传文件最大50MB!')
    return false
  } else if (props?.accept && !props?.accept.includes(fileType)) {
    message.error(`请传入${props?.accept}格式的文件!`)
    fileList.value = []
    return false
  }
  await getSystemEmpty()
  uploadHeaders.value = {
    Authorization: 'Bearer ' + getAccessToken()
  }
  return true
}
</script>
