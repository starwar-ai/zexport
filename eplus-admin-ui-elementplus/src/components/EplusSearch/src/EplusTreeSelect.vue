<template>
  <el-tree-select
    v-model="model"
    :data="classTree"
    :props="defaultProps"
    :check-strictly="props?.checkStrictly"
    node-key="id"
    :placeholder="'请选择' + props?.placeholder"
    clearable
  />
</template>

<script setup lang="tsx">
import { defaultProps, handleTree } from '@/utils/tree'

const model = defineModel<string>()
const emit = defineEmits(['changeEmit'])

const classTree = ref<Tree[]>([]) // 树形结构

const props = withDefaults(
  defineProps<{
    dictType: DICT_TYPE
    onSelect?: Function
    defaultValue?: string | number | boolean
    disabled?: boolean
    clearable?: boolean
    multiple?: boolean
    placeholder?: string
    checkStrictly?: boolean
    treeList: any[]
  }>(),
  { clearable: true }
)
const getTree = async () => {
  classTree.value = handleTree(props.treeList.value)
}
watchEffect(() => {
  if (!model.value && model.value !== 0) {
    if (props.defaultValue || props.defaultValue === 0) {
      model.value = props.defaultValue
    } else {
      model.value = undefined
    }
  }
})
onMounted(() => {
  getTree()
})
</script>
