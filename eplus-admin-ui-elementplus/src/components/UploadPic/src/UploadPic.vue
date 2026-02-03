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
    :limit="limit"
    drag
    class="uploadImg"
    accept=".jpg, .png, .jpeg, .gif"
    :class="{ disabled: props.disabled || fileList.length >= limit }"
  >
    <el-icon>
      <span class="uploadText">上传图片</span>
      <icon-picture style="width: 3em; height: 3em; color: #ccc" />
    </el-icon>
    <template #file="{ file }">
      <div>
        <div class="img_box">
          <img
            class="el-upload-list__item-thumbnail"
            :src="file.url"
            alt=""
          />
          <span
            class="tag"
            v-if="file.mainFlag == 1"
            >主图
          </span>
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
          <span
            v-if="!props.disabled && props.mainFlag && file.mainFlag == 0"
            class="el-upload-list__item-preview"
            @click="setMainImg(file)"
            style="font-size: 12px"
            >设置主图
          </span>
        </span>
      </div>
    </template>
  </el-upload>
  <span v-if="props.disabled && fileList.length == 0">无</span>
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
import { ref, watch } from 'vue'
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
    limit?: number
  }>(),
  { mainFlag: false, limit: 50 }
)
// const setMainFlag = computed(() => {
//   if (props.mainFlag) {
//   } else {
//   }
// })
const uploadHeaders = ref() // 上传 Header 头
uploadHeaders.value = {
  Authorization: 'Bearer ' + getAccessToken()
  // 'tenant-id': getTenantId()
}
const fileList = ref<UploadUserFile[]>([])
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
const resList = ref<any>([])
const uploadNum = ref(0)

const setImgList = (val) => {
  const len = uploadNum.value
  resList.value.push(val)
  if (resList.value.length === len) {
    fileList.value.splice(fileList.value.length - 1, len)
    resList.value.forEach((val) => {
      fileList.value.push({
        url: prefixUrl + val.data?.fileUrl,
        mainFlag: fileList.value.length === 0 && props?.mainFlag ? 1 : 0,
        ...val.data
      })
    })
    emit('success', fileList.value)
    resList.value = []
    uploadNum.value = 0
  }
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
  uploadNum.value++
  try {
    await getSystemEmpty()
    uploadHeaders.value = {
      Authorization: 'Bearer ' + getAccessToken()
    }
    return true
  } catch {
    return false
  }
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
.el-icon {
  position: relative;
  width: 2em;
  height: 2em;

  .uploadText {
    position: absolute;
    font-size: 14px;
    top: 100%;
    text-wrap: nowrap;
    font-style: normal;
    color: #ccc;
    margin-top: 30px;
  }
}

.full-screen {
  text-align: center !important;
}

:deep(.el-dialog__body) {
  text-align: center;
}

:deep(.el-upload-dragger) {
  height: 100%;
  border-style: hidden;
}

.disabled :deep(.el-upload-list) {
  display: none;
}

.img_box {
  width: 146px;
  height: 146px;
  position: relative;

  .el-upload-list__item-thumbnail {
    position: absolute;
    left: 0;
    top: 0;
  }

  .tag {
    position: absolute;
    right: 5px;
  }
}
</style>
