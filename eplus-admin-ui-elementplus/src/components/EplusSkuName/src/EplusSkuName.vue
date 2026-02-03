<template>
  <el-badge
    value="组"
    v-if="props.type === 2"
  >
    <span>{{ props.name || '' }}</span>
  </el-badge>
  <span v-else>{{ props.name || '' }}</span>
</template>

<script setup lang="tsx">
const props = defineProps<{
  name: string
  type: number | string
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
