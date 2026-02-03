<template>
  <el-select
    v-model="model"
    :clearable="props.clearable"
    ref="selectRef"
    :disabled="props.disabled"
    :multiple="props.multiple"
    :placeholder="props.placeholder"
    filterable
    @change="onChangeEmit"
    :filter-method="selectFilter"
  >
    <el-option
      v-for="dict in dictList"
      :key="dict.value"
      :label="dict.label"
      :value="dict.value"
    />
  </el-select>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'

const model = defineModel<string>()
const selectRef = ref()
const emit = defineEmits(['changeEmit'])
const onChangeEmit = (val) => {
  emit('changeEmit', val)
  if (!props?.multiple) {
    selectRef.value?.blur()
  }
}

watch(
  () => model.value,
  (val, oldVal) => {
    if (oldVal) {
      emit('changeEmit', val)
      if (!props?.multiple) {
        selectRef.value?.blur()
      }
    }
  }
)

const props = withDefaults(
  defineProps<{
    dictType: DICT_TYPE
    onSelect?: Function
    defaultValue?: string | number | boolean
    disabled?: boolean
    clearable?: boolean
    multiple?: boolean
    placeholder?: string
    filter?: any
  }>(),
  { clearable: true }
)
const dictList = ref()

watchEffect(() => {
  dictList.value = getIntDictOptions(props.dictType)
  if (props.filter) {
    dictList.value = props.filter(dictList.value)
  }
  if (
    (props.defaultValue || props.defaultValue === 0) &&
    (model.value === undefined || model.value === null)
  ) {
    model.value = props.defaultValue
  }
  // if (!model.value && model.value !== 0) {
  //   if (props.defaultValue || props.defaultValue === 0) {
  //     model.value = props.defaultValue
  //   } else {
  //     model.value = undefined
  //   }
  // }
})

const selectFilter = (val: string) => {
  if (val) {
    dictList.value = dictList.value.filter((item) => item.label.includes(val.trim()))
  } else {
    dictList.value = getIntDictOptions(props.dictType)
  }
}
</script>
