<template>
  <el-checkbox-group v-model="checkList">
    <el-checkbox
      v-for="dict in dictList"
      :key="dict.label"
      :label="dict.label"
      :value="dict.value"
    />
  </el-checkbox-group>
</template>

<script setup lang="tsx">
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
// const checkList = defineModel<array>()
const checkList = ref([])
const props = defineProps<{
  dictType: DICT_TYPE
  filter?: any
}>()
const emit = defineEmits<{
  change: [link: string]
}>()

const dictList = ref()

watchEffect(() => {
  dictList.value = getIntDictOptions(props.dictType)
  if (props.filter) {
    dictList.value = props.filter(dictList.value)
  }
})
</script>
