<template>
  <el-popover
    placement="bottom-start"
    :width="425"
    trigger="hover"
    :disabled="disabled"
  >
    <template #reference>
      <div :style="{ width: formatStyle(props.width), height: formatStyle(props.height) }">
        <img
          :src="miniImgurl"
          alt=""
          style="width: 100%; height: 100%; object-fit: contain"
        />
      </div>
    </template>
    <template #default>
      <div>
        <img
          width="350"
          height="350"
          :src="imgurl"
          alt=""
        />
      </div>
    </template>
  </el-popover>
</template>
<script setup lang="tsx">
import { ref, watchEffect } from 'vue'

// 定义图片源类型
type ImageSource = { fileUrl: string }

const props = withDefaults(
  defineProps<{
    mainPicture: ImageSource
    thumbnail?: string
    width?: number | string
    height?: number | string
    disabled?: boolean
  }>(),
  { width: '40px', height: '40px', disabled: false }
)

const formatStyle = (val) => {
  if (val === '100%') {
    return '100%'
  } else if (val.includes('px')) {
    return val
  } else {
    return `${val}px`
  }
}

const miniImgurl = ref('')
const imgurl = ref('')

watchEffect(() => {
  // 如果mainPicture为空，返回空字符串
  if (!props.thumbnail && !props.mainPicture) {
    imgurl.value = ''
    miniImgurl.value = ''
    return
  }

  if (props.thumbnail) {
    // if (!props.thumbnail.startsWith('data:')) {
    //   miniImgurl.value = 'data:image/png;base64,' + props.thumbnail
    // } else {
    //   miniImgurl.value = props.thumbnail
    // }
    miniImgurl.value = `${import.meta.env.VITE_BASE_URL}/admin-api${props.thumbnail}`
  } else {
    // if (props.mainPicture) {
    //   if (typeof props.mainPicture === 'string') {
    //     miniImgurl.value = `${import.meta.env.VITE_BASE_URL}/admin-api${props.mainPicture}`
    //   } else {
    //     if (typeof props.mainPicture === 'object' && props.mainPicture.fileUrl) {
    //       miniImgurl.value = `${import.meta.env.VITE_BASE_URL}/admin-api${props.mainPicture.fileUrl}`
    //     } else {
    //       miniImgurl.value = ''
    //     }
    //   }
    // }
    miniImgurl.value = ''
  }

  // 判断是否为base64格式

  if (props.mainPicture) {
    if (typeof props.mainPicture === 'string') {
      imgurl.value = `${import.meta.env.VITE_BASE_URL}/admin-api${props.mainPicture}`
    } else {
      if (typeof props.mainPicture === 'object' && props.mainPicture.fileUrl) {
        imgurl.value = `${import.meta.env.VITE_BASE_URL}/admin-api${props.mainPicture.fileUrl}`
      } else {
        imgurl.value = ''
      }
    }
  }
})
</script>
<style lang="scss" scoped></style>
