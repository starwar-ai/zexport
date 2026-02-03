<template>
  <el-popover
    placement="top"
    :width="400"
  >
    <template #reference>
      <p class="ellipsis-text">{{ props.info }}</p>
    </template>
    <div
      style="max-height: 300px; overflow-y: auto"
      v-html="PageInfo"
    ></div>
  </el-popover>
</template>

<script setup lang="tsx">
const props = withDefaults(
  defineProps<{
    info?: string
  }>(),
  {}
)
const convertNewlinesToBr = (text: string | undefined): string => {
  if (!text) return ''
  return text.replace(/\n/g, '<br />')
}
const PageInfo = computed(() => {
  return convertNewlinesToBr(props.info)
})
</script>

<style scoped lang="scss">
.ellipsis-text {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 100%;
  margin: 0;
}
</style>
