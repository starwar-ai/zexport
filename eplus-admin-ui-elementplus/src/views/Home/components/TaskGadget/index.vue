<template>
  <div style="height: 100%; overflow: auto">
    <el-table
      :data="state.tableData"
      size="small"
    >
      <el-table-column
        label="序号"
        type="index"
        width="50"
      />
      <el-table-column
        prop="name"
        label="任务名称"
        show-overflow-tooltip
      />
      <el-table-column
        prop="createTime"
        label="创建日期"
        show-overflow-tooltip
      >
        <template #default="scope">
          {{ formatTime(scope.row.createTime, 'yyyy-MM-dd') }}
        </template>
      </el-table-column>
      <!-- <el-table-column
          prop="processInstance.name"
          label="流程名称"
          show-overflow-tooltip
        >
          <template #default="scope">
            {{ getDictLabel(DICT_TYPE.INVOICE_TYPE, scope.row.invoiceType) }}
          </template>
        </el-table-column> -->
      <el-table-column
        prop="processInstance.name"
        label="流程名称"
        show-overflow-tooltip
        width="200"
      >
        <!-- <template #default="scope">
            {{ getDictLabel(DICT_TYPE.INVOICE_TYPE, scope.row.invoiceType) }}
          </template> -->
      </el-table-column>

      <el-table-column
        prop="processInstance.startUserNickname"
        label="发起人"
        show-overflow-tooltip
      />

      <el-table-column
        fixed="right"
        label="操作"
        min-width="40"
      >
        <template #default="scope">
          <el-button
            link
            type="primary"
            size="small"
            @click="handleDetails(scope.row, scope.$index)"
          >
            详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-if="state.total > 0"
      :page-size="10"
      size="small"
      :background="false"
      layout="total, prev, pager,next"
      :total="state.total"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script lang="ts" setup>
import { formatTime } from '@/utils/index'

import * as TaskApi from '@/api/bpm/task'

const props = defineProps<{
  refresh: any
}>()
const router = useRouter() // 路由
const state = reactive({
  tableData: [],
  total: 0
})
const queryParams = reactive({
  pageNo: 1,
  pageSize: 10
})

// 定时器变量
let refreshTimer: NodeJS.Timeout | null = null

// 获取列表
const getList = async () => {
  const res = await TaskApi.getTodoTaskList(queryParams)
  state.tableData = res.list
  state.total = res.total
}

// 开始定时刷新
const startAutoRefresh = () => {
  // 清除可能存在的旧定时器
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
  // 设置每30秒刷新一次
  refreshTimer = setInterval(() => {
    getList()
  }, 30000) // 30秒 = 30000毫秒
}

// 停止定时刷新
const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
    refreshTimer = null
  }
}

const handleCurrentChange = (val: number) => {
  queryParams.pageNo = val
  getList()
}

const handleDetails = (row, index) => {
  router.push({
    name: 'BpmProcessInstanceDetail',
    query: {
      id: row.processInstance.id,
      processDefinitionKey: row.processInstance.processDefinitionKey,
      index: (queryParams.pageNo - 1) * queryParams.pageSize + index + 1
    }
  })
}

onMounted(() => {
  getList()
  startAutoRefresh()
})

onActivated(() => {
  getList()
})

onUnmounted(() => {
  stopAutoRefresh()
})
</script>
