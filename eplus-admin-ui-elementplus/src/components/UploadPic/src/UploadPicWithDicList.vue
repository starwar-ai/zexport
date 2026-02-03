<template>
  <div style="display: flex; align-items: center; width: 146px">
    <div
      v-for="(item, index) in picturesRef"
      :key="index"
    >
      <div style="display: flex; flex-direction: column; align-items: center">
        <el-upload
          v-model:file-list="item.fileList"
          :action="uploadUrl"
          list-type="picture-card"
          :on-change="
            (uploadFile, uploadFiles) => {
              return handleChange(uploadFile, uploadFiles, item.value)
            }
          "
          :on-success="
            (response, file, fileList) => {
              return setImgList(response, item.value)
            }
          "
          :before-upload="
            (file) => {
              return beforeUpload(file)
            }
          "
          :headers="uploadHeaders"
          multiple
          :limit="1"
          drag
          accept=".jpg, .png, .jpeg, .gif"
          :class="{ disabled: props.disabled || item.fileList.length >= 1 }"
        >
          <el-icon>
            <span class="uploadText">上传图片</span>
            <icon-picture style="width: 3em; height: 3em; color: #ccc" />
          </el-icon>
          <template #file="{ file }">
            <div class="uploadImg">
              <div class="img_box">
                <img
                  class="el-upload-list__item-thumbnail"
                  :src="file.url"
                  alt=""
                />
              </div>
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
                @click="handleRemovePictuer(file, item.value)"
              >
                <Icon
                  icon="ep:delete"
                  style="font-size: 16px"
                />
              </span>
            </span>
          </template>
        </el-upload>
        <div
          v-if="
            !props.disabled &&
            index == picturesRef.length - 1 &&
            index < defaultPictureTypeOption.length - 1
          "
          style="display: flex; align-items: center; height: 27px; margin-top: 5px"
        >
          <el-select
            v-model="pictureType"
            class="w-4/5"
            placeholder="图片类型"
            @change="pictureTypeChange(item.value)"
          >
            <el-option
              v-for="i in pictureTypeDicList"
              :key="i.value"
              :value="i.value"
              :label="i.label"
            />
          </el-select>
          <Icon
            v-if="!props.disabled"
            icon="ep:circle-plus"
            style="font-size: 20px"
            @click="handleAdd(item)"
          />
        </div>
        <div
          v-else
          style="display: flex; align-items: center; height: 27px"
        >
          <div style="font-size: 14px; align-items: center">{{ item.label }}</div>
          <Icon
            v-if="!props.disabled"
            @click="handleRemove(item)"
            icon="ep:circle-close"
            style="font-size: 20px"
          />
        </div>
      </div>
    </div>
  </div>
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
import { getDictLabel, getDictOptions } from '@/utils/dict'

const emit = defineEmits(['success', 'error'])

type UploadPicWithDicListParam = {
  value?: string
  label?: string
  fileList: UploadUserFile[]
}

const defaultObj: UploadPicWithDicListParam = {
  value: '',
  label: '',
  fileList: []
}

const message = useMessage() // 消息弹窗
const props = withDefaults(
  defineProps<{
    dictType?: any
    disabled?: boolean
    pictureList?: array
  }>(),
  {}
)

const uploadHeaders = ref() // 上传 Header 头
uploadHeaders.value = {
  Authorization: 'Bearer ' + getAccessToken()
}

//图片列表
const picturesRef = ref<UploadPicWithDicListParam[]>([{ ...defaultObj }])

const defaultPictureTypeOption = getDictOptions(props.dictType)
const pictureTypeDicList = ref(defaultPictureTypeOption)

let formData: any = inject('formData')

const fileData = (params?: any) => {
  let list = params
  if (list) {
    for (let index = 0; index < list.length; index++) {
      var fileList = list[index].fileList
      fileList.forEach((fl) => {
        fl.url = prefixUrl + fl?.fileUrl
      })
      if (index == list.length - 1) {
        pictureType.value = list[index].value
      }
    }
  }
  picturesRef.value = list || []
  resetPictureTypeDicList()
}

//添加图片
const handleAdd = (item) => {
  if (!item.value) {
    message.error('请选择图片类型!')
    return
  }
  if (!item.fileList || item.fileList.length <= 0) {
    message.error('请先上传图片!')
    return
  }
  var addObj = { ...defaultObj }
  if (picturesRef.value.length == defaultPictureTypeOption.length - 1) {
    var usedValues = picturesRef.value.map((item) => {
      return item.value
    })
    var lastObj = defaultPictureTypeOption.filter((item) => {
      return usedValues.indexOf(item.value) < 0
    })[0]
    addObj.value = lastObj.value
    addObj.label = lastObj.label
  }
  picturesRef.value.push(addObj)
  pictureType.value = ''
  resetPictureTypeDicList()
  emit('success', picturesRef.value)
}

const resetPictureTypeDicList = () => {
  //重置类型选择框数据
  if (picturesRef.value && picturesRef.value.length > 0) {
    var lastOptions = picturesRef.value[picturesRef.value.length - 1]
    var usedOptions = picturesRef.value
      .filter((item) => {
        return item.value != lastOptions.value
      })
      .map((it) => {
        return it.value
      })
    var newArr = defaultPictureTypeOption.filter((item) => {
      return usedOptions.indexOf(item.value) < 0
    })
    pictureTypeDicList.value = newArr
  }
}

const handleRemove = (item) => {
  //重置公章数据
  var newArr = picturesRef.value.filter((pr) => {
    return pr.value != item.value
  })
  picturesRef.value = newArr
  pictureType.value = ''
  resetPictureTypeDicList()
  emit('success', picturesRef.value)
}

const pictureTypeChange = (value) => {
  picturesRef.value.forEach((item) => {
    if (item.value == value) {
      item.value = pictureType.value
      item.label = getDictLabel(props.dictType, pictureType.value)
      item.fileList = []
    }
  })
}

defineExpose({ fileData })
const prefixUrl = `${import.meta.env.VITE_BASE_URL}/admin-api`

const setImgList = (response, value) => {
  picturesRef.value.forEach((item) => {
    if (item.value == value) {
      if (!item.value) {
        item.value = pictureType.value
        item.label = getDictLabel(props.dictType, pictureType.value)
      }
      item.fileList?.splice(item.fileList.length - 1, 1)
      item.fileList?.push({
        url: prefixUrl + response.data?.fileUrl,
        mainFlag: 1,
        ...response.data
      })
    }
  })
  emit('success', picturesRef.value)
}

const uploadUrl = import.meta.env.VITE_UPLOAD_URL
const dialogImageUrl = ref('')
const dialogVisible = ref(false)

const handleRemovePictuer = (uploadFile, value) => {
  picturesRef.value.forEach((item) => {
    if (item.value == value) {
      let index = item.fileList.findIndex((item) => item.url == uploadFile.url)
      item.fileList.splice(index, 1)
    }
  })
  emit('success', picturesRef.value)
}

const pictureType = ref()

const handleChange = (uploadFile, uploadFiles, value) => {
  if (uploadFile.response && uploadFile.response.code != 0) {
    message.error('上传图片错误，详情：' + uploadFile.response.msg)
  } else {
    picturesRef.value.forEach((item) => {
      if (item.value == value) {
        item.fileList = uploadFiles.filter((item) => {
          if (item.uid != uploadFile.uid) {
            return item
          }
        })
      }
    })
  }
}

//文件上传之前请求一个无意义的接口，主要为了实现refreshToken
const beforeUpload = async (file) => {
  if (!pictureType.value) {
    message.error('请先选择图片类型')
    return false
  }
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

.disabled :deep(.el-upload.is-drag) {
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
