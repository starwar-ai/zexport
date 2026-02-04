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
      >{{ tag.name }}</span
    >
  </el-tag>
  <el-button
    text
    @click="openForm"
    type="primary"
    size="small"
    v-if="!props.disabled"
  >
    <Icon
      icon="ep:upload"
      class="mr-3px"
    />
    上传文件
  </el-button>
  <!-- 表单弹窗：添加/修改 -->
  <FileForm
    ref="formRef"
    @success="getList"
  />
</template>
<script lang="ts" setup>
import { encodeFileUrl } from '@/utils'
import FileForm from './FileForm.vue'

defineOptions({ name: 'InfraFile' })

const message = useMessage() // 消息弹窗
const { t } = useI18n() // 国际化

const loading = ref(false) // 列表的加载中
const list = ref([]) // 列表的数据
const emit = defineEmits(['success'])
const fileData = async (params?: object) => {
  list.value = params
}
const props = defineProps<{
  fileList?: array
  disabled?: boolean
}>()

onMounted(() => {
  list.value = props.fileList.value?.annex
})

defineExpose({ fileData }) // 提供 fileData 方法，用于父传子数据
/** 查询列表 */
const getList = async (data?: object) => {
  let myList = []
  loading.value = true
  try {
    if (list.value) {
      list.value.push(data)
    } else {
      myList.push(data)
      list.value = myList
    }
    // list.value = data.list
    // total.value = data.total
    emit('success', list)
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
  try {
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
    // 刷新列表
  } catch {}
}

const handleDownload = (item) => {
  const encodedUrl = encodeFileUrl(item.fileUrl)
  window.location.href = `${import.meta.env.VITE_BASE_URL}/admin-api${encodedUrl}`
}
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
