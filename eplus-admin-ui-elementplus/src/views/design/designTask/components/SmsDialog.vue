<template>
  <Dialog
    v-model="dialogTableVisible"
    title="选择销售合同"
    width="1000"
    append-to-body
    destroy-on-close
  >
    <div class="pb50px">
      <div class="mb10px">
        <eplus-search
          :fields="eplusSearchSchema.fields"
          :moreFields="eplusSearchSchema.moreFields"
          @search="handleSearch"
          @reset="handleReset"
        />
      </div>

      <el-table
        border
        :data="tableData"
        row-key="code"
        ref="tableRef"
        @row-click="getRow"
      >
        <el-table-column
          align="center"
          label="选择"
          width="60px"
        >
          <template #default="{ row }">
            <el-radio
              v-model="radioVal"
              :label="row.code"
            >
              &nbsp;
            </el-radio>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="销售编号"
          prop="code"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="创建时间"
          prop="createTime"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            {{ formatTime(row.createTime, 'yyyy-MM-dd HH:mm:ss') }}
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="类型"
          prop="saleType"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            {{ getDictLabel(DICT_TYPE.SALE_TYPE, row.saleType) }}
          </template>
        </el-table-column>

        <el-table-column
          align="center"
          label="客户名称"
          prop="custName"
          show-overflow-tooltip
        />
      </el-table>
      <!-- 分页 -->
      <Pagination
        v-model:limit="pageForm.pageSize"
        v-model:page="pageForm.pageNo"
        :total="pageForm.total"
        @pagination="getList"
      />
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="primary"
          @click="handleSure"
        >
          确认
        </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import * as emsApi from '@/api/ems/emsList/index'
import { formatTime } from '@/utils/index'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'

defineOptions({ name: 'SmsDialog' })
const message = useMessage()
const dialogTableVisible = ref(false)
const tableRef = ref()
const tableData = ref([])
const radioVal = ref('')
const loading = ref(false)
const pageForm = reactive({
  pageSize: 10,
  pageNo: 1,
  total: 0
})
const custCodeList = ref([])
let searchForm = reactive({})
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input></el-input>,
      name: 'code',
      label: '合同编号'
    },
    {
      component: <el-input></el-input>,
      name: 'custName',
      label: '客户名称'
    }
  ],
  moreFields: []
}

const handleSearch = (model) => {
  searchForm = model
  pageForm.pageNo = 1
  getList()
}
const handleReset = () => {
  searchForm = {}
  pageForm.pageNo = 1
  getList()
}

const emit = defineEmits(['sure'])

const getList = async () => {
  loading.value = true
  try {
    let res = await emsApi.contractList({
      pageSize: pageForm.pageSize,
      pageNo: pageForm.pageNo,
      custCodeList: custCodeList.value,
      ...searchForm
    })
    tableData.value = res.list.map((item) => {
      return {
        ...item,
        children: undefined
      }
    })
    pageForm.total = res.total
    loading.value = false
  } catch {
    loading.value = false
  }
}

const open = async () => {
  searchForm = {}
  pageForm.pageNo = 1
  await getList()
  dialogTableVisible.value = true
}

const getRow = (row) => {
  radioVal.value = row.code
}

const handleCancel = () => {
  tableData.value = []
  dialogTableVisible.value = false
}
const handleSure = () => {
  if (radioVal.value) {
    emit('sure', radioVal.value)
    handleCancel()
  } else {
    message.warning('请选择一条数据!')
  }
}
defineExpose({ open })
</script>
