<template>
  <div class="head-container">
    <el-input
      v-model="deptName"
      class="mb-20px"
      clearable
      placeholder="请输入部门名称"
    >
      <template #prefix>
        <Icon icon="ep:search" />
      </template>
    </el-input>
  </div>
  <el-scrollbar height="70vh">
    <div class="left-container">
      <el-tree
        ref="treeRef"
        :data="deptList"
        :expand-on-click-node="false"
        :filter-node-method="filterNode"
        :props="defaultProps"
        default-expand-all
        highlight-current
        node-key="id"
        @node-click="handleNodeClick"
      />
    </div>
  </el-scrollbar>
</template>

<script lang="ts" setup>
import { ElTree } from 'element-plus'
import { defaultProps } from '@/utils/tree'
import * as SysTableApi from '@/api/system/role/fieldPermi'

defineOptions({ name: 'SystemUserDeptTree' })

const deptName = ref('')
const deptList = ref<Tree[]>([]) // 树形结构
const treeRef = ref<InstanceType<typeof ElTree>>()
const message = useMessage()

const setCurrentTreeKey = async (params) => {
  if (params) {
    treeRef.value!.setCurrentKey(params)
  }
}
/** 获得部门树 */
const getTree = async () => {
  try {
    const sysTable = await SysTableApi.getSysTableName()
    for (const item of sysTable) {
      item.id = item.tableName
      item.name = item.tableComment ? item.tableComment : item.tableName
      item.parentId = 0
    }
    deptList.value = []
    deptList.value.push(...sysTable)
    nextTick(() => {
      setCurrentTreeKey(unref(deptList)[0].tableName)
    })
    return deptList.value
  } catch {
    message.error('获取部门树失败')
  }
}

/** 基于名字过滤 */
const filterNode = (name: string, data: Tree) => {
  if (!name) return true
  return data.name.includes(name)
}

/** 处理部门被点击 */
const handleNodeClick = async (row: { [key: string]: any }) => {
  emits('node-click', row)
}
const emits = defineEmits(['node-click'])

/** 监听deptName */
watch(deptName, (val) => {
  treeRef.value!.filter(val)
})

defineExpose({ getTree, setCurrentTreeKey })
/** 初始化 */
onMounted(async () => {
  await getTree()
})
</script>
<style lang="scss" scoped>
.left-container {
  :deep(.is-current) {
    background-color: #f0f5fe;
    font-weight: bold;
    color: #005bf5;
  }

  :deep(.el-tree-node__content:hover) {
    background-color: #f0f5fe;
    color: #005bf5;
  }

  :deep(.el-tree-node__content) {
    height: 38px;
    border-radius: 5px;
  }

  :deep(.el-tree-node) {
    border-radius: 5px;
    margin: 3px 0px;
  }
}
</style>
