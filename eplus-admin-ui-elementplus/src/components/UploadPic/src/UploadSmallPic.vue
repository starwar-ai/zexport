<template>
  <div class="flex">
    <div
      v-for="item in fileList"
      :key="item.url"
      class="imgBox"
    >
      <el-image
        class="imgItem"
        :src="item.url"
        fit="fill"
        @click="handlePictureCardPreview(item)"
      />
      <Icon
        icon="ep:circle-close-filled"
        class="iconClose"
        @click="handleRemove(item)"
        v-if="!props.disabled"
      />
    </div>
  </div>

  <el-upload
    v-model:file-list="fileList"
    :action="uploadUrl"
    :on-change="handleChange"
    :on-success="setImgList"
    :before-upload="beforeUpload"
    :headers="uploadHeaders"
    :show-file-list="false"
    multiple
    :limit="limit"
    class="uploadImg"
    accept=".jpg, .png, .jpeg, .gif"
    :class="{ disabled: props.disabled || fileList.length >= limit }"
  >
    <el-button
      text
      type="primary"
      size="small"
    >
      上传图片
    </el-button>
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
const uploadHeaders = ref() // 上传 Header 头
uploadHeaders.value = {
  Authorization: 'Bearer ' + getAccessToken()
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

const handlePictureCardPreview = (file) => {
  dialogImageUrl.value = file.url
  dialogVisible.value = true
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
.disabled {
  display: none;
}

.imgBox {
  width: 50px;
  height: 50px;
  position: relative;
  margin-right: 5px;
  border: 1px solid #ebeef5;
}

.imgItem {
  width: 50px;
  height: 50px;
  position: absolute;
  top: 0;
  left: 0;
}

.iconClose {
  position: absolute;
  right: 0;
  top: 0;
  cursor: pointer;
}
</style>
