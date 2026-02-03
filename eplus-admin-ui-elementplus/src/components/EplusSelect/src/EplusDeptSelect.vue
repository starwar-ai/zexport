<template>
  <el-tree-select
    v-model="value"
    filterable
    :data="deptList"
    clearable
    :multiple="multipleFlag"
    :props="defaultProps"
    node-key="id"
    :popper-class="prefixCls"
  />
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { defaultProps, handleTree } from '@/utils/tree'
import * as DeptApi from '@/api/system/dept'
import { useDesign } from '@/hooks/web/useDesign'

defineOptions({ name: 'EplusDeptSelect' })

const props = withDefaults(
  defineProps<{
    multipleFlag?: boolean
  }>(),
  { multipleFlag: false }
)

const { getPrefixCls } = useDesign()

const prefixCls = getPrefixCls('dept-select')

const deptList = ref<Tree[]>([]) // 树形结构
const deptData = ref<any[]>([])

const value = defineModel<string>()

/** 查询列表 */
const getList = async () => {
  try {
    deptData.value = await DeptApi.getSimpleDeptList()
    deptList.value = handleTree(deptData.value)
  } finally {
  }
}

const emit = defineEmits(['change'])

watch(
  () => value.value,
  (val) => {
    let item = deptData.value.find((item) => item.id == val)
    if (item) {
      emit('change', item)
    } else {
      emit('change', undefined)
    }
  }
)

/** 初始化 **/
onMounted(() => {
  getList()
})
</script>

<style lang="scss">
$prefix-cls: #{$namespace}-dept-select;

.#{$prefix-cls} {
  .#{$elNamespace}-tree {
    min-width: 230px;
  }
}
</style>
