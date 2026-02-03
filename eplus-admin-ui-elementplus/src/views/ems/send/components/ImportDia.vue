<template>
  <Dialog
    v-model="dialogVisible"
    title="回填费用导入"
    width="400"
  >
    <el-upload
      ref="uploadRef"
      v-model:file-list="fileList"
      :action="importUrl"
      :auto-upload="false"
      :disabled="formLoading"
      :headers="uploadHeaders"
      :limit="1"
      :on-error="submitFormError"
      :on-exceed="handleExceed"
      :on-success="submitFormSuccess"
      accept=".xlsx, .xls"
      drag
    >
      <Icon icon="ep:upload" />
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <template #tip>
        <div class="el-upload__tip text-center">
          <span>仅允许导入 xls、xlsx 格式文件。</span>
        </div>
      </template>
    </el-upload>
    <template #footer>
      <el-button
        :disabled="formLoading"
        type="primary"
        @click="submitForm"
        >确 定</el-button
      >
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script lang="ts" setup>
import { getAccessToken } from '@/utils/auth'
import router from '@/router'

defineOptions({ name: 'EmsSendImportDia' })

const message = useMessage()

const dialogVisible = ref(false)
const formLoading = ref(false)
const uploadRef = ref()
const importUrl = import.meta.env.VITE_BASE_URL + '/admin-api/ems/send-bill/import'
const uploadHeaders = ref()
const fileList = ref([])

const open = () => {
  dialogVisible.value = true
  resetForm()
}
defineExpose({ open })

const emits = defineEmits(['success'])

const submitForm = async () => {
  if (fileList.value.length == 0) {
    message.error('请上传文件')
    return
  }
  uploadHeaders.value = {
    Authorization: 'Bearer ' + getAccessToken()
  }
  formLoading.value = true
  uploadRef.value!.submit()
}
const submitFormSuccess = async (response: any) => {
  console.log(response)
  if (response.code !== 0) {
    message.error(response.msg)
    formLoading.value = false
    return
  }
  dialogVisible.value = false
  formLoading.value = false
  emits('success', response.data)
  // 跳转到结果分析页
  try {
    router.push({
      path: '/ems/import-result',
      query: {
        data: encodeURIComponent(JSON.stringify(response.data))
      }
    })
  } catch {}
}

const submitFormError = (): void => {
  message.error('上传失败，请您重新上传！')
  formLoading.value = false
}

const resetForm = () => {
  formLoading.value = false
  uploadRef.value?.clearFiles()
}

const handleExceed = (): void => {
  message.error('最多只能上传一个文件！')
}
</script>
