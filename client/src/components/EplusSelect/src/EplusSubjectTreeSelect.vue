<template>
  <el-tree-select
    v-model="model"
    :data="subjectTree"
    :props="defaultProps"
    node-key="id"
    placeholder="请选择对应科目"
    filterable
    @change="modelChange"
  />
</template>

<script setup lang="tsx">
import * as DictSubjectApi from '@/api/oa/dictsubject'
import { defaultProps } from '@/utils/tree'

const model = defineModel<string | number>()

// const props = defineProps<{
//   api?: any
//   params?: any
//   label?: string
//   value?: string
//   class?: string
// }>()
const subjectTree = ref<Tree[]>([])
const getList = async () => {
  subjectTree.value = await DictSubjectApi.getSubjectTree()
}
const emit = defineEmits(['change'])
const modelChange = (val) => {
  let itemObj = subjectTree.value.find((item) => item.id == val)
  emit('change', itemObj)
}
onMounted(() => {
  getList()
})
</script>
