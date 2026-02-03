<template>
  <el-button
    link
    type="primary"
    @click="handleInfo"
  >
    {{ row.skuCode }}
  </el-button>
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
    v-if="row.skuDeletedFlag"
    @click="handleUpdate"
    >更新
  </el-button>
</template>
<script setup lang="tsx">
const props = defineProps<{
  row
}>()
const emit = defineEmits(['detail', 'update'])

const handleInfo = () => {
  emit('detail')
}
const handleUpdate = () => {
  emit('update')
}
</script>
<style lang="scss" scoped>
// .code_box {
//   width: 150px;
//   height: 30px;
//   position: relative;

//   .tag {
//     position: absolute;
//     right: 0;
//     top: -5px;
//     font-size: 12px;
//     color: red;
//   }

//   .code {
//     position: absolute;
//     left: 0;
//     top: 4px;
//     color: rgb(0, 91, 245);
//     cursor: pointer;
//   }
// }
</style>
