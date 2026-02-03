<template>
  <div class="head-container">
    <el-input
      v-model="className"
      class="mb-20px"
      clearable
      placeholder="请输入关键字"
    >
      <template #prefix>
        <Icon icon="ep:search" />
      </template>
    </el-input>
  </div>
  <div class="head-container">
    <el-tree
      ref="treeRef"
      :data="classList"
      :expand-on-click-node="false"
      :filter-node-method="filterNode"
      :props="defaultProps"
      default-expand-all
      highlight-current
      node-key="id"
      @node-click="handleNodeClick"
    />
  </div>
</template>

<script lang="ts" setup>
import { ElTree } from 'element-plus'
import * as CategoryApi from '@/api/crm/category'
import { defaultProps, handleTree } from '@/utils/tree'
import { onMounted, ref, watch } from 'vue'

defineOptions({ name: 'SystemUserDeptTree' })

const className = ref('')
const classList = ref<Tree[]>([]) // 树形结构
const treeRef = ref<InstanceType<typeof ElTree>>()

/** 获得树 */
const getTree = async () => {
  classList.value = []
  const res = await CategoryApi.getCategoryTree()
  let menu: Tree = { id: 0, name: '全部分类', children: [] }
  menu.children = handleTree(res)
  classList.value.push(menu)
}

/** 基于名字过滤 */
const filterNode = (name: string, data: Tree) => {
  if (!name) return true
  return data.name.includes(name)
}

/** 处理被点击 */
const handleNodeClick = async (row: { [key: string]: any }) => {
  emits('node-click', row)
}
const emits = defineEmits(['node-click'])

/** 监听deptName */
watch(className, (val) => {
  treeRef.value!.filter(val)
})

/** 初始化 */
onMounted(async () => {
  await getTree()
})

defineExpose({ getTree })
</script>
