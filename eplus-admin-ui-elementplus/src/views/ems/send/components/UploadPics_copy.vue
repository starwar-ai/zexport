<template>
  <span>
    <el-upload
      v-model:file-list="fileList"
      :action="uploadUrl"
      :on-change="handleChange"
      :on-success="setImgList"
      :before-upload="beforeUpload"
      :headers="uploadHeaders"
      multiple
      :limit="limit"
      class="upload-demo"
      accept=".jpg, .png, .jpeg, .gif"
      :show-file-list="false"
      :http-request="test"
      :class="{ disabled: props.disabled || fileList.length >= limit }"
      ><el-button>批量上传图片</el-button></el-upload
    >
  </span>
</template>
<script setup lang="tsx">
import type { UploadProps, UploadUserFile } from 'element-plus'
import { getAccessToken } from '@/utils/auth'
import { getSystemEmpty } from '@/api/system/empty/index'
import * as EmsListApi from '@/api/ems/emsList'

const emit = defineEmits(['success', 'error'])
const message = useMessage()
const fileList = ref<UploadUserFile[]>([])
const props = withDefaults(
  defineProps<{
    pictureList?: array
    disabled?: boolean
    mainFlag?: boolean
    limit?: number
  }>(),
  { mainFlag: false, limit: 50 }
)
const uploadHeaders = ref() // 上传 Header 头
uploadHeaders.value = {
  Authorization: 'Bearer ' + getAccessToken()
  // 'tenant-id': getTenantId()
}
const uploadUrl = import.meta.env.VITE_UPLOAD_URL
const uploadFileList = ref([])
const maxLength = ref(0)
const blobFileList = ref([])
const toBlobPromise = (item) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.onload = function (e: any) {
      const data = e.target.result
      resolve(data)
      // console.log(JSON.parse(data), 'JSON.parse(data)')
    }
    reader.onerror = (e) => {
      reject(e)
    }
    reader.readAsBinaryString(item.raw)
  })
}
const handleChange: UploadProps['onChange'] = (uploadFile, uploadFiles) => {
  console.log(uploadFile, uploadFiles, 'uploadFile,uploadFiles')
  uploadFileList.value.push(uploadFile)
  // [解决on-change多次触发问题]
  let length = uploadFileList.value.length
  maxLength.value = Math.max(length, maxLength.value)
  setTimeout(async () => {
    if (length !== maxLength.value) {
      return
    } else {
      // uploadFile()
      let blobList = uploadFileList.value.map(async (item) => {
        let res = await toBlobPromise(item)
        console.log(res, 'res')
        return res
      })
      let res = await Promise.all(blobList)
      console.log(res, 'blobList')
      await EmsListApi.uploadImgs({ files: res })
      maxLength.value = 0
    }
  }, 10)
  // if (uploadFile.response && uploadFile.response.code != 0) {
  //   message.error('上传图片错误，详情：' + uploadFile.response.msg)
  // } else {
  //   fileList.value = uploadFiles.filter((item) => {
  //     if (item.uid != uploadFile.uid) {
  //       return item
  //     }
  //   })
  // }
}
const test = async (param) => {
  console.log(param, 'param')
  let pic = param.file
  console.log(pic, 'pic')
  let fd = new FormData()
  fd.append(pic, pic.name)
  await EmsListApi.uploadImgs({ files: fd })
}
const setImgList = (val) => {
  fileList.value.splice(fileList.value.length - 1, 1)
  console.log(props?.mainFlag, 'props?.mainFlag')
  fileList.value.push({
    url: prefixUrl + val.data?.fileUrl,
    mainFlag: fileList.value.length === 0 && props?.mainFlag ? 1 : 0,
    ...val.data
  })
  // console.log(fileList.value, 'fileList.value')
  emit('success', fileList.value)
}
//文件上传之前请求一个无意义的接口，主要为了实现refreshToken
const beforeUpload = async (file) => {
  try {
    console.log(file, 'file')
    await getSystemEmpty()
    uploadHeaders.value = {
      Authorization: 'Bearer ' + getAccessToken()
    }
    return true
  } catch {
    return false
  }
}
</script>
<style scoped lang="scss"></style>
