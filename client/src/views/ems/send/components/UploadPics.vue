<template>
  <div class="emsUploadImg">
    <el-upload
      v-model:file-list="fileList"
      :action="uploadUrl"
      :on-change="handleChange"
      :on-success="setImgList"
      :before-upload="beforeUpload"
      :headers="uploadHeaders"
      multiple
      accept=".jpg, .png, .jpeg, .gif"
      :class="{ disabled: props.disabled }"
      :show-file-list="false"
    >
      <el-button>批量上传</el-button>
    </el-upload>
  </div>
</template>

<script lang="ts" setup>
import { inject, ref, watch } from 'vue'
import { getAccessToken } from '@/utils/auth'
import type { UploadProps, UploadUserFile } from 'element-plus'
import { getSystemEmpty } from '@/api/system/empty/index'

const emit = defineEmits(['success', 'error'])
const message = useMessage() // 消息弹窗

const props = withDefaults(
  defineProps<{
    pictureList?: array
    disabled?: boolean
    mainFlag?: boolean
  }>(),
  { mainFlag: false }
)
const uploadHeaders = ref() // 上传 Header 头
uploadHeaders.value = {
  Authorization: 'Bearer ' + getAccessToken()
  // 'tenant-id': getTenantId()
}
const fileList = ref<UploadUserFile[]>([])
let formData: any = inject('formData')
const fileData = (params?: any) => {
  let list = params
  if (list) {
    for (let index = 0; index < list.length; index++) {
      list[index].url = prefixUrl + list[index]?.fileUrl
    }
  }
  fileList.value = list || []
}

defineExpose({ fileData })
const prefixUrl = `${import.meta.env.VITE_BASE_URL}/admin-api`
const setImgList = (val) => {
  console.log(fileList.value, 'fileList.value')
}

const uploadUrl = import.meta.env.VITE_UPLOAD_URL
const dialogImageUrl = ref('')
const dialogVisible = ref(false)
const uploadFileList = ref([] as any[])
const totalUploads = ref(0)
const completedUploads = ref(0)

const handleChange: UploadProps['onChange'] = (uploadFile, uploadFiles) => {
  if (uploadFile.response && uploadFile.response.code !== 0) {
    message.error(`上传图片错误，详情：${uploadFile.response.msg}`)
  } else {
    uploadFileList.value.push(uploadFile)
    // 检查是否所有文件都已上传完成
    if (completedUploads.value === totalUploads.value) {
      completedUploads.value = 0
      totalUploads.value = 0
    }
    completedUploads.value++
  }
  emit('success', async () => {
    return uploadFileList.value
  })
  uploadFileList.value = []
}
watch(
  () => fileList.value,
  (newFiles) => {
    if (newFiles) {
      totalUploads.value = newFiles.length
    }
  }
)
//文件上传之前请求一个无意义的接口，主要为了实现refreshToken
const beforeUpload = async (file) => {
  await getSystemEmpty()
  uploadHeaders.value = {
    Authorization: 'Bearer ' + getAccessToken()
  }
  return true
}
</script>
<style lang="scss" scoped></style>
