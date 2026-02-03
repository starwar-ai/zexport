<template>
  <span>{{ row.skuCode }}</span>
  <el-tag
    v-if="row.skuDeletedFlag"
    type="info"
    effect="dark"
    size="small"
    >被修改
  </el-tag>
  <el-button
    link
    type="primary"
    class="ml10px"
    v-if="updateFlag"
    @click="handleUpdate"
    >更新
  </el-button>
</template>
<script setup lang="tsx">
const props = defineProps<{
  row
  updateFlag
}>()
const emit = defineEmits(['detail', 'update'])

// const handleInfo = () => {
//   emit('detail')
// }
const handleUpdate = () => {
  emit('update')
}
</script>
<style lang="scss" scoped></style>
