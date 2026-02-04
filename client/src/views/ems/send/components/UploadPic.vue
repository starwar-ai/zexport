<template>
  <el-upload
    v-model:file-list="fileList"
    :action="uploadUrl"
    list-type="picture-card"
    :on-change="handleChange"
    :on-success="setImgList"
    :before-upload="beforeUpload"
    :headers="uploadHeaders"
    multiple
    drag
    class="emsUploadImg"
    accept=".jpg, .png, .jpeg, .gif"
    :class="{ disabled: props.disabled }"
  >
    <el-icon>
      <span class="uploadText">上传图片</span>

      <icon-picture style="width: 16px; height: 16px; color: #ccc; margin-top: 30px" />
    </el-icon>
    <template #file="{ file }">
      <div>
        <div class="img_box">
          <img
            class="el-upload-list__item-thumbnail"
            :src="file.url"
            alt=""
          />
        </div>
        <span class="el-upload-list__item-actions">
          <span
            class="el-upload-list__item-preview"
            @click="handlePictureCardPreview(file)"
          >
            <Icon
              icon="ep:zoom-in"
              style="font-size: 16px"
            />
          </span>
          <span
            v-if="!props.disabled"
            class="el-upload-list__item-delete"
            @click="handleRemove(file)"
          >
            <Icon
              icon="ep:delete"
              style="font-size: 16px"
            />
          </span>
        </span>
      </div>
    </template>
  </el-upload>
  <el-dialog
    v-model="dialogVisible"
    :align-center="true"
    :append-to-body="true"
    width="700px"
    :show-close="false"
  >
    <template #header="{ close }">
      <div
        class="my-header"
        style="text-align: right"
      >
        <el-button
          link
          @click="close"
        >
          <Icon
            icon="ep:circle-close-filled"
            style="font-size: 24px; color: #409eff"
          />
        </el-button>
      </div>
    </template>

    <div style="text-align: center">
      <img
        style="max-width: 100%"
        w-full
        :src="dialogImageUrl"
        alt="Preview Image"
      />
    </div>
  </el-dialog>
</template>

<script lang="ts" setup>
import { inject, ref, watch } from 'vue'
import { getAccessToken } from '@/utils/auth'
import { Picture as IconPicture } from '@element-plus/icons-vue'
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
  fileList.value.splice(fileList.value.length - 1, 1)
  fileList.value.push({ url: prefixUrl + val.data?.fileUrl, mainFlag: 0, ...val.data })
  emit('success', fileList.value)
}

const uploadUrl = import.meta.env.VITE_UPLOAD_URL
const dialogImageUrl = ref('')
const dialogVisible = ref(false)

const setMainImg = (file) => {
  if (props.disabled) {
    message.error('该状态不可设置主图')
    return false
  } else {
    fileList.value.forEach((item) => {
      item.mainFlag = 0
    })
    file.mainFlag = 1
    emit('success', fileList.value)
  }
}

const handleRemove = (uploadFile) => {
  if (props.disabled) {
    message.error('该状态不可删除')
    return false
  } else {
    let index = fileList.value.findIndex((item) => item.url == uploadFile.url)
    fileList.value.splice(index, 1)
    emit('success', fileList.value)
  }
}

const handleChange: UploadProps['onChange'] = (uploadFile, uploadFiles) => {
  if (uploadFile.response && uploadFile.response.code != 0) {
    message.error('上传图片错误，详情：' + uploadFile.response.msg)
  } else {
    fileList.value = uploadFiles.filter((item) => {
      if (item.uid != uploadFile.uid) {
        return item
      }
    })
  }
}

//文件上传之前请求一个无意义的接口，主要为了实现refreshToken
const beforeUpload = async (file) => {
  await getSystemEmpty()
  uploadHeaders.value = {
    Authorization: 'Bearer ' + getAccessToken()
  }
  return true
}

const handlePictureCardPreview: UploadProps['onPreview'] = (uploadFile) => {
  dialogImageUrl.value = uploadFile.url!
  dialogVisible.value = true
}

const handleDownload = () => {
  window.location.href = dialogImageUrl.value
}

watch(
  () => props.pictureList,
  (val) => {
    if (val?.length > 0) fileData(val)
  },
  {
    immediate: true,
    deep: true
  }
)
</script>
<style lang="scss" scoped>
.emsUploadImg {
  width: 120px;
  height: 40px;
}

.el-icon {
  position: relative;
  width: 20px;
  height: 20px;

  .uploadText {
    position: absolute;
    font-size: 12px;
    top: 1%;
    text-wrap: nowrap;
    font-style: normal;
    color: #ccc;
    margin-top: 1px;
  }
}

.full-screen {
  text-align: center !important;
}

:deep(.el-dialog__body) {
  text-align: center;
}

:deep(.el-upload-dragger) {
  // height: 100%;
  border-style: hidden;
}

.disabled :deep(.el-upload.is-drag) {
  display: none;
}

:deep(.el-upload-list) {
  width: 100px;
  height: 40px;
}

:deep(.el-upload-dragger) {
  width: 97px;
  height: 38px !important;
  padding: 0 !important;
}

:deep(.el-upload) {
  width: 100px;
  height: 40px;
}

.img_box {
  width: 90px;
  height: 40px;
  position: relative;

  // .el-upload-list__item-thumbnail {
  //   position: absolute;
  //   left: 0;
  //   top: 0;
  // }

  .tag {
    position: absolute;
    right: 5px;
  }
}
</style>
