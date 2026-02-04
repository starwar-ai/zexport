<template>
  <div style="height: 100%; overflow: auto">
    <el-tabs
      v-model="activeName"
      class="demo-tabs"
      @tab-click="handleTabs"
    >
      <el-tab-pane
        v-for="item in options.editableTabs"
        :key="item.name"
        :label="item.title"
        :name="item.name"
      />
    </el-tabs>
    <el-button
      type="primary"
      @click="handleAdd"
      size="small"
      >新增</el-button
    >
    <el-table
      :data="options.tableData"
      size="small"
    >
      <el-table-column
        label="序号"
        type="index"
        width="50"
      />
      <el-table-column
        prop="invoiceName"
        label="发票名称"
        show-overflow-tooltip
      />
      <el-table-column
        prop="createTime"
        label="发票日期"
        show-overflow-tooltip
      >
        <template #default="scope">
          {{ formatTime(scope.row.createTime, 'yyyy-MM-dd') }}
        </template>
      </el-table-column>
      <el-table-column
        prop="invoiceType"
        label="发票类型"
        show-overflow-tooltip
      >
        <template #default="scope">
          {{ getDictLabel(DICT_TYPE.INVOICE_TYPE, scope.row.invoiceType) }}
        </template>
      </el-table-column>

      <el-table-column
        prop="invoiceAmount"
        label="发票金额"
        show-overflow-tooltip
      />
      <el-table-column
        prop="invoiceCode"
        label="发票号"
        show-overflow-tooltip
      />

      <el-table-column
        prop="invoice"
        label="发票"
        width="80"
      >
        <template #default="scope">
          <el-button
            link
            type="primary"
            size="small"
            @click="handleDownload(scope.row.invoice)"
          >
            查看
          </el-button>
        </template>
      </el-table-column>

      <el-table-column
        fixed="right"
        label="操作"
        min-width="80"
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
          <el-button
            link
            type="primary"
            size="small"
            @click="handleDel(scope.row)"
            v-if="scope.row.status === 0"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      class="pagination"
      :page-size="5"
      size="small"
      :background="background"
      layout="total, prev, pager"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script lang="ts" setup>
import { ElButton } from 'element-plus'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import * as HomeApi from '@/api/home'
import eventBus from './eventBus.ts'
import { useFeeStore } from '@/store/modules/fee'
import { formatTime } from '@/utils/index'

const props = defineProps<{
  refresh: any
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
  readonly?: boolean
}>()

const dialogFormVisible = ref(false)
const visible = ref(false)
const formSize = ref<ComponentSize>('default')
const ruleFormRef = ref<FormInstance>()
const message = useMessage()

const feeList = useFeeStore().feeList

const activeName = ref(0)
const size = ref<ComponentSize>('default')
const background = ref(false)
const total = ref(0)

const emit = defineEmits(['acceptAdd'])

const options = reactive({
  tabs: '',
  columnLabel: '',
  prop: '',
  add: false,
  operation: false,
  tableData: [],
  editableTabs: [
    {
      title: '未使用',
      name: 0
    },
    {
      title: '已使用',
      name: 1
    }
  ]
})
const setDate = (timestamp) => {
  let date = new Date(timestamp)
  return date.toLocaleDateString('zh-CN')
}

const inveoiceMap = reactive({
  1: '专票',
  2: '普票'
})

const option = reactive<RuleForm>({
  item: {},
  diaTitle: '新增',
  visible: false
})

const params = ref({
  pageSize: 5,
  pageNo: 1,
  status: 0
})

const handleTabs = (pane: TabsPaneContext, ev: Event) => {
  params.value.status = pane.props.name
  getList()
}

// 获取列表
const getList = async () => {
  const res = await HomeApi.invoiceHolderPage(params.value)
  options.tableData = res.list
  total.value = res.total
}

const handleAdd = (type) => {
  option.visible = true
  option.diaTitle = '新增'
  option.item = {}
  eventBus.emit('addCard', option)
}

const handleSizeChange = (val: number) => {
  params.value.pageSize = val
  getList()
}
const handleCurrentChange = (val: number) => {
  params.value.pageNo = val
  getList()
}

// 查看附件
const handleDownload = (invoice) => {
  invoice.forEach((item) => {
    window.open(import.meta.env.VITE_BASE_URL + '/admin-api' + item.fileUrl, '_blank')

    // const a = document.createElement('a')
    // a.href = import.meta.env.VITE_BASE_URL + '/admin-api' + item.fileUrl
    // a.download = item.name // 修改文件名
    // a.style.display = 'none'
    // document.body.appendChild(a)
    // a.click()
    // document.body.removeChild(a)
  })
}

// 详情
const handleDetails = (item, index) => {
  option.visible = true
  option.item = item
  option.diaTitle = '详情'
  option.item.index = index + 1
  eventBus.emit('addCard', option)
}

const handleDel = async (item) => {
  await message.confirm('确定删除吗？')
  await HomeApi.deleteHemoInvoice(item.id)
  getList()
}

let unsubscribe
onMounted(() => {
  getList()
  unsubscribe = eventBus.on('closeDialog', (data) => {
    if (data.refresh) {
      getList()
    }
  })
})
// onUnmounted(() => {
//   unsubscribe(); // 组件卸载时移除事件监听
// });
</script>

<style scoped lang="scss">
.table-box {
  width: 100%;
  display: flex;
  justify-content: space-between;

  .left-table {
    width: 100%;

    .demo-tabs {
      // margin-top: 10px;
    }
  }
}

.pagination {
  float: right;
  margin-top: 10px;
}

.el-tabs__header {
  margin: 0 !important;
}

::v-deep .el-dialog__body {
  padding: 0 !important;
}

::v-deep .el-form-item,
.el-form-item--default {
  margin-bottom: 0 !important;
}

::v-deep .el-dialog {
  margin: 1px 2px !important;
}
</style>
