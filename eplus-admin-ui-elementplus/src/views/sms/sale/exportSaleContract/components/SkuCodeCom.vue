<template>
  <el-badge
    value="拆"
    v-if="props.flag === 1"
  >
    <span>{{ props.code || '' }}</span>
  </el-badge>
  <span v-else>{{ props.code || '' }}</span>
</template>

<script setup lang="tsx">
const props = defineProps<{
  code: number | string
  flag: number | string
}>()

onMounted(() => {})
</script>
<style scoped>
:deep(.el-badge__content) {
  background-color: transparent !important;
  color: #7cc440;
  font-size: 12px !important;
}

:deep(.el-badge__content.is-fixed) {
  top: 5px;
  right: 3px;
  border: 0;
}
</style>
