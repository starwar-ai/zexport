<template>
  <el-tag
    v-for="tag in list"
    :key="tag.name"
    :closable="!props.disabled"
    type="info"
    @close="handleDelete(tag, list)"
    class="mr10px"
  >
    <span
      style="cursor: pointer; color: #333"
      @click="handleDownload(tag)"
      >{{ tag?.oldName || tag?.name }}</span
    >
  </el-tag>
  <span v-if="list.length == 0 && !isUpload">无</span>
  <el-button
    text
    @click="openForm"
    type="primary"
    size="small"
    v-if="isUpload"
  >
    <Icon
      icon="ep:upload"
      class="mr-3px"
    />
    <span v-if="!props.isLabelHide"> 上传文件 </span>
  </el-button>
  <!-- 表单弹窗：添加/修改 -->
  <FileForm
    ref="formRef"
    @success="getList"
    :accept="accept || ''"
  />
</template>
<script lang="ts" setup>
import { encodeFileUrl } from '@/utils'
import FileForm from './FileForm.vue'

defineOptions({ name: 'UploadList' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(false) // 列表的加载中
const list = ref<any[]>([]) // 列表的数据
const emit = defineEmits(['success'])
const fileData = async (params?: object) => {
  list.value = params
}

const props = withDefaults(
  defineProps<{
    fileList?: any
    disabled?: boolean
    limit?: number
    accept?: string
    isLabelHide?: boolean
  }>(),
  { limit: 100 }
)

const isUpload = computed(() => {
  if (props.disabled) {
    return false
  } else if (!props.disabled && list.value?.length >= props.limit) {
    return false
  } else {
    return true
  }
})

// onMounted(() => {
//   list.value = props.fileList
// })

defineExpose({ fileData }) // 提供 fileData 方法，用于父传子数据
/** 查询列表 */
const getList = async (data: any[]) => {
  let myList = []
  loading.value = true
  try {
    if (list.value) {
      list.value = [...list.value, ...data]
    } else {
      list.value = [...data]
      list.value = myList
    }
    list.value.forEach((item: any) => {
      item.name = item.oldName || item.name
    })
    emit('success', list.value)
  } finally {
    loading.value = false
  }
}

/** 添加/修改操作 */
const formRef = ref()
const openForm = () => {
  formRef.value.open()
}

/** 删除按钮操作 */
const handleDelete = async (item, params) => {
  // 删除的二次确认
  await message.delConfirm()
  // 发起删除
  // await FileApi.deleteFile(item?.id)
  const index = params.indexOf(item)
  if (index !== -1) {
    params.splice(index, 1)
  }
  emit('success', params)
  message.success(t('common.delSuccess'))
}

const handleDownload = (item) => {
  const encodedUrl = encodeFileUrl(item.fileUrl)
  window.location.href = `${import.meta.env.VITE_BASE_URL}/admin-api${encodedUrl}`
}
watch(
  () => props.fileList,
  (val) => {
    if (val?.length > 0) {
      fileData(val)
    } else {
      list.value = []
    }
  },
  {
    immediate: true,
    deep: true
  }
)
</script>
<style lang="scss" scoped>
.buttonContainer {
  position: relative;
  text-align: right;
  width: 100%;
  margin-top: 50px;

  .el-button {
    position: absolute;
    top: -49px;
    right: 0px;
    z-index: 10;
  }
}

.el-table {
  margin-bottom: 20px;
}
</style>
