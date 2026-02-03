<template>
  <el-radio-group
    v-model="model"
    :disabled="props.disabled"
  >
    <template v-if="props.isBtn">
      <el-radio-button
        v-for="dict in dictList"
        :key="dict.value"
        :label="dict.value"
        >{{ dict.label }}
      </el-radio-button>
    </template>
    <template v-else>
      <el-radio
        v-for="dict in dictList"
        :key="dict.value"
        :label="dict.value"
        >{{ dict.label }}
      </el-radio>
    </template>
  </el-radio-group>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'

const model = defineModel<string | number>()

const props = withDefaults(
  defineProps<{
    dictType: DICT_TYPE
    defaultValue?: string
    disabled?: boolean
    filter?: any
    isBtn?: boolean
  }>(),
  { isBtn: false }
)

const dictList = ref()
watchEffect(() => {
  dictList.value = getIntDictOptions(props.dictType)
  if (props.filter) {
    dictList.value = props.filter(dictList.value)
  }
  if (props.defaultValue) {
    model.value = props.defaultValue
  }
})
</script>
