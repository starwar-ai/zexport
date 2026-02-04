<template>
  <div class="flex">
    <EplusNumInput
      class="lenInput"
      v-model="length"
      :precision="2"
      :min="0"
      placeholder="长"
      style="width: 80px"
      @change="updateValue"
    />
    <EplusNumInput
      class="otherInput"
      style="width: 80px"
      v-model="width"
      :precision="2"
      :min="0"
      :controls="false"
      placeholder="宽"
      @change="updateValue"
    />
    <EplusNumInput
      class="heightInput"
      style="width: 80px"
      v-model="height"
      :precision="2"
      :min="0"
      :controls="false"
      placeholder="高"
      @change="updateValue"
    />
    <!-- <el-button disabled>{{ LengthUnit }}</el-button> -->
  </div>
</template>

<script setup lang="tsx">
import { defineEmits, defineProps, ref, watchEffect } from 'vue'
import { SplitKey } from '@/utils/config'

const props = defineProps({
  modelValue: String
})
const length = ref()
const width = ref()
const height = ref()

watchEffect(() => {
  if (props.modelValue) {
    const [l, w, h] = props.modelValue.split(SplitKey)
    length.value = validVal(l)
    width.value = validVal(w)
    height.value = validVal(h)
  }
})
function validVal(val) {
  if (val != 'undefined') {
    return Number(val)
  } else {
    return 0
  }
}

const emit = defineEmits(['update:modelValue', 'update:opacity'])

function updateValue() {
  emit(
    'update:modelValue',
    `${length.value}${SplitKey}${width.value || 0}${SplitKey}${height.value || 0}`
  )
}
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

.heightInput :deep(.el-input__wrapper) {
  border-right: 1px solid #dcdfe6;
  border-top-right-radius: 4px !important;
  border-bottom-right-radius: 4px !important;
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
