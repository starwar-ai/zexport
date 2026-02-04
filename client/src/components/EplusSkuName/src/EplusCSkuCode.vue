<template>
  <div class="box flex">
    <el-button
      link
      type="primary"
      @click="handleClick"
    >
      {{ props.row.cskuCode }}
    </el-button>
    <ElTooltip
      placement="top"
      content="复制"
      :disabled="!props.row.cskuCode"
    >
      <ElIcon
        v-if="props.row.cskuCode"
        class="tip"
        style="margin-left: 10px"
        @click.stop="
          async () => {
            await copyToClipboard(props.row.cskuCode)
          }
        "
      >
        <DocumentCopy />
      </ElIcon>
    </ElTooltip>
  </div>
</template>

<script setup lang="tsx">
import { DocumentCopy } from '@element-plus/icons-vue'
import { setSourceId } from '@/utils/auth'

const message = useMessage()
const props = defineProps<{
  row: any
}>()
const router = useRouter()
const handleClick = () => {
  setSourceId(props.row.skuId)
  router.push({ path: '/base/product-manage/csku' })
}

const copyToClipboard = async (text) => {
  const textarea = document.createElement('textarea')
  textarea.value = text
  document.body.appendChild(textarea)
  textarea.select()
  try {
    document.execCommand('copy')
    message.success('复制成功！')
  } catch (err) {
    message.error('失败！' + err)
  }
  document.body.removeChild(textarea)
}
onMounted(() => {})
</script>
<style scoped lang="scss">
.tip {
  visibility: hidden;
}

.box:hover {
  .tip {
    visibility: visible;
  }
}
</style>
