<template>
  <div class="flex items-center justify-between">
    <el-input-number
      v-model="skuRate"
      :min="1"
      :controls="false"
      :precision="0"
      @change="updateValue"
    />
    <span style="width: 20px; text-align: center">:</span>
    <el-input-number
      v-model="auxiliarySkuRate"
      :min="1"
      :controls="false"
      :precision="0"
      @change="updateValue"
    />
  </div>
</template>

<script setup lang="tsx">
import { defineEmits, defineProps, ref, watchEffect } from 'vue'
import { SplitKey } from '@/utils/config'

const props = defineProps({
  modelValue: String
})

const skuRate = ref()
const auxiliarySkuRate = ref()
watchEffect(() => {
  if (props.modelValue) {
    const [f, t] = props.modelValue.split(SplitKey)
    skuRate.value = validVal(f, true)
    auxiliarySkuRate.value = validVal(t, true)
  }
})

function validVal(val, flag = false) {
  if (val && !isNaN(val)) {
    return flag ? Number(val) : val
  } else {
    return undefined
  }
}

const emit = defineEmits(['update:modelValue'])

function updateValue() {
  emit(
    'update:modelValue',
    `${skuRate.value}${SplitKey}${isNaN(auxiliarySkuRate.value) ? '' : auxiliarySkuRate.value}`
  )
}
</script>
