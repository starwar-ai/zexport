<template>
  <div class="flex">
    <!-- <el-input-number
      class="weightInput"
      v-model="weight"
      :precision="2"
      :min="0"
      :controls="false"
      @change="updateValue"
      @blur="updateValue"
    /> -->
    <EplusNumInput
      class="weightInput"
      v-model="weight"
      :precision="2"
      :min="0"
      @change="updateValue"
      @blur="updateValue"
    />
    <eplus-dict-select
      class="unitSelect"
      style="width: 80px"
      v-model="unit"
      :dictType="DICT_TYPE.SINGLE_WIEGHT_UNIT"
      :clearable="false"
      @change-emit="updateValue"
    />
  </div>
</template>

<script setup lang="tsx">
import { DICT_TYPE } from '@/utils/dict'
import { EplusDictSelect } from '@/components/EplusSelect'
import { defineEmits, defineProps, ref, watchEffect } from 'vue'
import { SplitKey } from '@/utils/config'

const props = defineProps({
  modelValue: String
})

const weight = ref()
const unit = ref()

watchEffect(() => {
  if (props.modelValue) {
    const [w, u] = props.modelValue.split(SplitKey)
    weight.value = validVal(w)
    unit.value = validVal(u)
  }
})

function validVal(val, flag = false) {
  if (val != 'undefined') {
    return flag ? Number(val) : val
  } else {
    return 0
  }
}

const emit = defineEmits(['update:modelValue', 'update:opacity'])

function updateValue() {
  emit('update:modelValue', `${weight.value}${SplitKey}${unit.value}`)
}
</script>

<style scoped>
.weightInput :deep(.el-input__wrapper) {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

.unitSelect :deep(.el-input__wrapper) {
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}
</style>
