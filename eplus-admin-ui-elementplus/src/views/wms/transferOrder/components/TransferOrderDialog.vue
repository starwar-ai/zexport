<template>
  <Dialog
    v-model="dialogTableVisible"
    title="选择需要调拨的产品"
    width="1200"
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
        @select="handleSelectionChange"
        @select-all="handleSelectionAllChange"
        row-key="code"
        ref="tableRef"
        v-loading="loading"
        @row-click="getRow"
      >
        <el-table-column
          :reserve-selection="true"
          align="center"
          type="selection"
        />
        <el-table-column
          align="center"
          label="产品编号"
          prop="skuCode"
          show-overflow-tooltip
          width="200px"
        />
        <el-table-column
          align="center"
          label="客户货号"
          prop="cskuCode"
          show-overflow-tooltip
          width="200px"
        />
        <el-table-column
          align="center"
          label="产品名称"
          prop="skuName"
          show-overflow-tooltip
          width="200px"
        />
        <el-table-column
          align="center"
          label="批号"
          prop="batchCode"
          show-overflow-tooltip
          width="130px"
        />
        <el-table-column
          align="center"
          label="当前可用库存"
          prop="availableQuantity"
          show-overflow-tooltip
          width="180px"
        />
        <el-table-column
          align="center"
          label="销售订单号"
          prop="saleContractCode"
          show-overflow-tooltip
          width="120px"
        />
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
        layout="total, sizes, prev, pager, next, jumper"
      />
      <!-- </div> -->
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button
          type="primary"
          @click="handleSure"
        >
          确认
        </el-button>
        <el-button @click="handleCancel">取消</el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
import { cloneDeep } from 'lodash-es'
// import { formatTime } from '@/utils/index'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusSearchMultiInput } from '@/components/EplusSearch'
// import { formatDictValue } from '@/utils/table'
import * as TransferOrderApi from '@/api/wms/transferOrder'

defineOptions({ name: 'CostDialog' })
const message = useMessage()
const dialogTableVisible = ref(false)
const tableRef = ref()
const tableData = ref([])
let parentList = ref([])
const loading = ref(false)
const pageForm = reactive({
  pageSize: 10,
  pageNo: 1,
  total: 0
})
const contractCodeRef = ref('')
let searchForm = reactive({})
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <EplusSearchMultiInput placeholder="请输入客户产品名称" />,
      subfields: [
        {
          name: 'skuName',
          label: '客户产品名称'
        }
      ]
    }
  ],
  moreFields: []
}
const handleSearch = (model) => {
  searchForm = { saleContractCode: contractCodeRef.value, ...model }
  pageForm.pageNo = 1
  getList()
}
const handleReset = () => {
  searchForm = { saleContractCode: contractCodeRef.value }
  pageForm.pageNo = 1
  getList()
}

const emit = defineEmits<{
  sure: [link: string]
}>()

const getList = async (saleContractCode?) => {
  loading.value = true
  try {
    let res = await TransferOrderApi.getTransferOrderDetailStock({
      pageSize: pageForm.pageSize,
      pageNo: pageForm.pageNo,
      saleContractCode: saleContractCode,
      ...searchForm
    })
    tableData.value = res.list?.length ? res.list : []
    // tableData.value.categoryList = categoryList || []
    pageForm.total = res.total || 0
    loading.value = false
  } catch {
    loading.value = false
  } finally {
    loading.value = false
  }
}
const toggleSelection = () => {
  if (parentList.value) {
    console.log(parentList.value, tableData.value)
    tableData.value.forEach((item: any) => {
      parentList.value.forEach((el: any) => {
        if (item.batchCode == el.batchCode) {
          console.log(tableRef.value, 'tableRef.value')
          tableRef.value.toggleRowSelection(item, undefined)
        }
      })
    })
  }
}
const open = async (list, saleContractCode = '') => {
  parentList.value = cloneDeep(list)
  console.log(list, 'list')
  selectedDiaData.value = cloneDeep(list)
  contractCodeRef.value = saleContractCode
  searchForm = { saleContractCode: saleContractCode }
  pageForm.pageNo = 1
  dialogTableVisible.value = true
  await getList(saleContractCode)
  toggleSelection()
}
defineExpose({ open })

// toggleRowSelection

const selectedDiaData = ref([])
const handleSelectionChange = (selection, row: any) => {
  if (parentList.value?.length > 0) {
    let codeList = selectedDiaData.value.map((item: any) => item.batchCode)
    let rowIndex = codeList.findIndex((item) => item === row.batchCode)
    console.log(codeList, row.batchCode, 's')
    if (codeList.includes(row.batchCode)) {
      selectedDiaData.value.splice(rowIndex, 1)
    } else {
      selectedDiaData.value.push(row)
    }
    console.log(selectedDiaData.value, 'selectedDiaData.value')
  } else {
    selectedDiaData.value = selection
  }
}

const handleSelectionAllChange = (selection) => {
  selection.forEach((item) => {
    handleSelectionChange(selection, item)
  })
}

const getRow = (row) => {
  // let codeList = selectedDiaData.value.map((item) => item.code)
  // let rowIndex = codeList.findIndex((item) => item === row.code)
  // if (codeList.includes(row.code)) {
  //   // selectedDiaData.value.splice(rowIndex, 1)
  // } else {
  //   selectedDiaData.value.push(row)
  //   tableRef.value.toggleRowSelection(row, undefined)
  // }
}

const handleCancel = () => {
  // tableData.value = []
  // tableRef.value.clearSelection()
  dialogTableVisible.value = false
}
const handleSure = () => {
  let len = selectedDiaData.value?.length
  if (len > 0) {
    console.log(selectedDiaData.value, 'selectedDiaData.value')
    emit(
      'sure',
      // @ts-ignore
      selectedDiaData.value
    )
    handleCancel()
  } else {
    message.warning('请选择需要调拨的产品')
  }
}

onMounted(async () => {
  try {
  } catch {}
})
</script>

<style lang="scss" scoped>
.totalAction {
  cursor: pointer;
  width: 2000px;
}

.totalAction:hover {
  border-color: #ccc;
  box-shadow: 0 0 0 1px #eee;
}
</style>
