<template>
  <Dialog
    v-model="dialogTableVisible"
    title="添加产品"
    width="1000"
    append-to-body
    destroy-on-close
  >
    <div class="pb50px">
      <el-tabs
        v-model="activeName"
        @tab-click="handleClick"
      >
        <el-tab-pane
          label="客户产品"
          :name="1"
        />
        <el-tab-pane
          label="自营产品"
          :name="2"
        />
      </el-tabs>
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
        :row-key="rowKey"
        ref="tableRef"
        height="350px"
      >
        <el-table-column
          :reserve-selection="true"
          align="center"
          type="selection"
        />
        <el-table-column
          align="center"
          label="图片"
          prop="createTime"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <EplusImgEnlarge :mainPicture="row.mainPicture" />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="归属公司"
          prop="companyName"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="批次编号"
          prop="batchCode"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="产品编号"
          prop="skuCode"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="基础产品编号"
          prop="basicSkuCode"
          width="100px"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="客户货号"
          prop="cskuCode"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="客户名称"
          prop="custName"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="产品名称"
          prop="skuName"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="采购合同号"
          prop="purchaseContractCode"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="库存数"
          prop="remainderQuantity"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="入库日期"
          prop="receiptTime"
          show-overflow-tooltip
        >
          <template #default="{ row }">{{ formatTime(row.receiptTime, 'yyyy-MM-dd') }}</template>
        </el-table-column>
        <el-table-column
          align="center"
          label="备注"
          prop="remark"
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
import { cloneDeep } from 'lodash-es'
import { formatTime } from '@/utils/index'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import * as StocktakeApi from '@/api/wms/stocktake/index'
import { EplusSearchAmountRange, EplusSearchMultiDatePicker } from '@/components/EplusSearch'
import { getcompanySimpleList } from '@/api/common/index'

defineOptions({ name: 'SelectProDia' })
const message = useMessage()
const dialogTableVisible = ref(false)
const tableRef = ref()
const tableData = ref([])
let parentList = ref([])
const activeName = ref(1)
const rowKey = 'batchCode'
const loading = ref(false)
const pageForm = reactive({
  pageSize: 10,
  pageNo: 1,
  total: 0
})
let searchData = reactive({})
let searchForm = reactive({})
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: (
        <eplus-custom-select
          api={getcompanySimpleList}
          label="name"
          value="id"
          placeholder="公司主体"
          class="!w-150px"
        />
      ),
      name: 'companyId',
      label: '公司主体',
      formatter: async (args: any[]) => {
        let list = await getcompanySimpleList()
        return list.find((item) => item.id == args[0]).name
      }
    },
    {
      component: <el-input></el-input>,
      name: 'basicSkuCode',
      label: '基础产品编号'
    },
    {
      component: <el-input></el-input>,
      name: 'skuName',
      label: '产品名称'
    },
    {
      component: <el-input></el-input>,
      name: 'cskuCode',
      label: '客户货号'
    },
    {
      component: <el-input></el-input>,
      name: 'custName',
      label: '客户名称'
    },
    // {
    //   component: <el-input></el-input>,
    //   name: 'remainderQuantityMax',
    //   label: '库存数'
    // },
    {
      component: <EplusSearchAmountRange />,
      subfields: [{ name: 'remainderQuantity', label: '库存数', formatter: '从{0}到{1}' }]
    },
    {
      component: <el-input></el-input>,
      name: 'purchaseContractCode',
      label: '采购合同号'
    },
    {
      component: <el-input></el-input>,
      name: 'saleContractCode',
      label: '销售合同号'
    },

    {
      component: <EplusSearchMultiDatePicker />,
      subfields: [
        {
          name: 'receiptTime',
          label: '入库日期',
          formatter: '从{0}到{1}'
        }
      ]
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

const emit = defineEmits<{
  sure
}>()

const handleClick = ({ props }) => {
  activeName.value = props.name
  handleReset()
}

const getList = async () => {
  loading.value = true
  try {
    let res = await StocktakeApi.queryBatch({
      pageSize: pageForm.pageSize,
      pageNo: pageForm.pageNo,
      ownBrandFlag: activeName.value === 2 ? 1 : undefined,
      custProFlag: activeName.value === 1 ? 1 : 0,
      ...searchForm,
      ...searchData
    })
    tableData.value = res.list
    pageForm.total = res.total
    loading.value = false
    toggleSelection()
  } catch {
    loading.value = false
  }
}

const open = async (obj, list) => {
  searchData = obj
  parentList.value = cloneDeep(list.value)
  selectedDiaData.value = cloneDeep(list.value)
  activeName.value = 1
  pageForm.pageNo = 1
  dialogTableVisible.value = true
  await getList()
}

const toggleSelection = () => {
  if (parentList.value) {
    tableData.value.forEach((item: any) => {
      parentList.value.forEach((el: any) => {
        if (item[rowKey] == el[rowKey]) {
          tableRef.value.toggleRowSelection(item, undefined)
        }
      })
    })
  }
}
// toggleRowSelection

const selectedDiaData = ref([])
const handleSelectionChange = (selection, row: any) => {
  if (parentList.value.length > 0) {
    let codeList = selectedDiaData.value.map((item: any) => item[rowKey])
    let rowIndex = codeList.findIndex((item) => item === row[rowKey])
    if (codeList.includes(row[rowKey])) {
      selectedDiaData.value.splice(rowIndex, 1)
    } else {
      selectedDiaData.value.push(row)
    }
  } else {
    selectedDiaData.value = selection
  }
}
const handleSelectionAllChange = (selection) => {
  selection.forEach((item) => {
    handleSelectionChange(selection, item)
  })
}
const handleCancel = () => {
  tableData.value = []
  tableRef.value.clearSelection()
  dialogTableVisible.value = false
}
const handleSure = () => {
  let len = selectedDiaData.value?.length
  if (len > 0) {
    emit(
      'sure',
      selectedDiaData.value.map((item: any) => {
        return {
          ...item,
          stockQuantity: item.remainderQuantity
        }
      })
    )
    handleCancel()
  } else {
    message.warning('请选择产品')
  }
}
defineExpose({ open })
</script>
