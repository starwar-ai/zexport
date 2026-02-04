<template>
  <div class="imgList">
    <div
      v-for="file in fileList"
      :key="file?.id"
      class="imgItem"
    >
      <!-- <el-image
        style="width: 100%; height: 100%"
        :src="setUrl(file)"
        fit="fill"
        class="item_main"
      /> -->
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

  <CopperModal
    ref="cropperModelRef"
    @upload-success="handleUpload"
  />
</template>

<script lang="ts" setup>
import { uploadApi } from '@/api/common'
import CopperModal from './CopperModal.vue'

const emit = defineEmits(['success', 'error', 'update:modelValue'])
const message = useMessage() // 消息弹窗
const fileList = ref([])
const props = withDefaults(
  defineProps<{
    pictureList?: array
    disabled?: boolean
    mainFlag?: boolean
    limit?: number
    modelValue: any
  }>(),
  { mainFlag: false, limit: 50 }
)
watchEffect(() => {
  fileList.value = props?.modelValue || []
})

const cropperModelRef = ref()

const handleOpen = () => {
  cropperModelRef.value?.openModal()
}
const setUrl = (file: any) => {
  return `${import.meta.env.VITE_BASE_URL}/admin-api${file.fileUrl}`
}

const handleUpload = async ({ data, filename }) => {
  let res = await uploadApi({ file: data, path: filename })
  fileList.value = [
    ...fileList.value,
    { ...res.data, mainFlag: fileList.value.length === 0 && props?.mainFlag ? 1 : 0 }
  ]
  emit('success', fileList.value)
  emit('update:modelValue', fileList.value)
  cropperModelRef.value?.closeModal()
}

const dialogVisible = ref(false)
const dialogImageUrl = ref('')
const handlePictureCardPreview: UploadProps['onPreview'] = (uploadFile) => {
  dialogImageUrl.value = setUrl(uploadFile)
  dialogVisible.value = true
}

const handleRemove = (uploadFile) => {
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
    emit('update:modelValue', fileList.value)
  }
}
</script>
<style lang="scss" scoped>
.imgList {
  width: 100%;
  display: flex;

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
      background: rgba($color: #000000, $alpha: 0.5);
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

  /* stylelint-disable-next-line rule-empty-line-before */
  .addBtn:hover {
    border: 1px dashed #409eff;
  }
}
</style>
