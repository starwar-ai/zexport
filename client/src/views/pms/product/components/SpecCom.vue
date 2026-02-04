<template>
  <div class="flex !w-100%">
    <!-- <el-input-number
      class="lenInput"
      :disabled="props.disabled"
      v-model="length"
      :precision="2"
      :min="0"
      :controls="false"
      placeholder="长"
    /> -->
    <EplusNumInput
      class="lenInput"
      v-model="length"
      :precision="2"
      :min="0"
      placeholder="长"
      :disabled="props.disabled"
    />
    <EplusNumInput
      :disabled="props.disabled"
      v-model="width"
      :precision="2"
      :min="0"
      placeholder="宽"
    />
    <EplusNumInput
      :disabled="props.disabled"
      v-model="height"
      :precision="2"
      :min="0"
      placeholder="高"
    />
    <el-button disabled>cm</el-button>
  </div>
</template>

<script setup lang="tsx">
const length = defineModel<number>('length')
const width = defineModel<number>('width')
const height = defineModel<number>('height')

const props = withDefaults(
  defineProps<{
    disabled?: boolean
  }>(),
  { disabled: false }
)
onMounted(() => {})
</script>
<style lang="scss" scoped>
:deep(.el-input__wrapper) {
  border: 1px solid #dcdfe6;
  border-right: 0;
  border-radius: 0 !important;
  box-shadow: none;
  box-sizing: border-box;
  height: 24px;
}

.lenInput :deep(.el-input__wrapper) {
  border-top-left-radius: 4px !important;
  border-bottom-left-radius: 4px !important;
}

:deep(.el-button) {
  border-top-left-radius: 0 !important;
  border-bottom-left-radius: 0 !important;
}
</style>
