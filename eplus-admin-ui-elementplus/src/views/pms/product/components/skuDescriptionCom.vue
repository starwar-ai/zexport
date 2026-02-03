<template>
  <div class="des flex items-center gap-8px">
    <el-scrollbar style="height: 280px; width: 90%; border: 1px solid #e5e5e5; border-radius: 4px">
      <div v-html="pageText"></div>
    </el-scrollbar>
    <ElTooltip
      placement="top"
      content="复制"
    >
      <Icon
        icon="ep:document-copy"
        class="copy"
        @click.stop="
          async () => {
            await handleCopy(text)
          }
        "
      />
    </ElTooltip>
  </div>
</template>
<script setup lang="tsx">
const props = defineProps<{
  text: string
}>()

const pageText = computed(() => {
  return props.text.replace(/\n/g, '<br>')
})

const message = useMessage()
const handleCopy = (text) => {
  const textarea = document.createElement('textarea')
  textarea.value = text
  document.body.appendChild(textarea)
  textarea.select()
  document.execCommand('copy')
  document.body.removeChild(textarea)
  message.success('复制成功')
}
</script>
<style lang="scss" scoped>
.des:hover {
  .copy {
    display: block;
    cursor: pointer;
  }
}

.copy {
  display: none;
  cursor: pointer;
}
</style>
