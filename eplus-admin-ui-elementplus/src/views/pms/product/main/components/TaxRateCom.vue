<template>
  <eplus-dict-select
    :disabled="false"
    style="width: 100px"
    v-model="faxFlag"
    :dictType="DICT_TYPE.IS_INT"
    :clearable="false"
    @change-emit="updateValue"
  />
  <el-input-number
    style="width: 100px"
    v-model="taxRate"
    :precision="2"
    :min="0"
    :max="100"
    :controls="false"
    :disabled="disabled"
  />
  <el-button disabled>%</el-button>
</template>

<script setup lang="tsx">
import { DICT_TYPE } from '@/utils/dict'
import { EplusDictSelect } from '@/components/EplusSelect'
import { defineEmits, defineProps, ref, watchEffect } from 'vue'
import { SplitKey } from '@/utils/config'

const props = defineProps({
  modelValue: String
})

const faxFlag = ref()
const taxRate = ref()
const disabled = ref(false)
watchEffect(() => {
  if (props.modelValue) {
    const [f, t] = props.modelValue.split(SplitKey)
    disabled.value = Number(f) == 0
    faxFlag.value = validVal(f, true)
    taxRate.value = validVal(t, true)
  }
})

function validVal(val, flag = false) {
  if (val != 'undefined') {
    return flag ? Number(val) : val
  } else {
    return undefined
  }
}

const emit = defineEmits(['update:modelValue'])

function updateValue(data) {
  emit('update:modelValue', `${faxFlag.value}${SplitKey}${taxRate.value}`)
}
</script>
