<template>
  <Dialog
    v-model="dialogTableVisible"
    title="选择产品"
    width="1000"
    append-to-body
    align-center
    destroy-on-close
  >
    <el-tabs
      v-model="activeName"
      @tab-click="handleClick"
    >
      <el-tab-pane
        label="客户产品库存"
        name="cust"
      />
      <el-tab-pane
        label="自营产品库存"
        name="own"
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
    <div style="overflow: hidden">
      <el-table
        border
        :data="tableData"
        @select="handleSelectionChange"
        @select-all="handleSelectionAllChange"
        row-key="id"
        ref="tableRef"
      >
        <el-table-column
          :reserve-selection="true"
          align="center"
          type="selection"
        />
        <el-table-column
          align="center"
          label="仓库名称"
          prop="warehouseName"
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
          label="基础产品编号"
          prop="basicSkuCode"
          show-overflow-tooltip
        />
        <el-table-column
          v-if="activeName === 'cust'"
          align="center"
          label="客户货号"
          prop="cskuCode"
          show-overflow-tooltip
          key="cust"
        />
        <el-table-column
          v-if="activeName === 'own'"
          align="center"
          label="自营产品货号"
          prop="oskuCode"
          key="own"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          prop="picture"
          label="图片"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <EplusImgEnlarge
              :mainPicture="row.mainPicture"
              :thumbnail="row.thumbnail"
              width="40"
              height="40"
            />
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="中文名称"
          prop="skuName"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="已入泛太仓数量"
          prop="totalAvailableQuantity"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="可出库数量"
          prop="outQuantity"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="客户编号"
          prop="custCode"
          show-overflow-tooltip
        />
        <el-table-column
          label="客户名称"
          prop="custName"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          prop="purchaseUser"
          label="采购员"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <span v-if="row.purchaseUser?.nickname"
              >{{ row.purchaseUser?.nickname }}（{{ row.purchaseUser?.deptName }}）
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          label="采购合同"
          prop="purchaseContractCode"
          show-overflow-tooltip
        />
        <el-table-column
          align="center"
          label="销售合同"
          prop="saleContractCode"
          show-overflow-tooltip
        />
      </el-table>
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
import { getSimpleStock } from '@/api/wms/renotice'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { EplusImgEnlarge } from '@/components/EplusTemplate'
import { isValidArray } from '@/utils/is'
import { cloneDeep } from 'lodash-es'

const message = useMessage()
defineOptions({ name: 'SelectSkuStock' })

const pageForm = reactive({
  pageSize: 10,
  pageNo: 1,
  total: 0
})
let searchForm = reactive({})
const activeName = ref('cust')
const tableData = ref([])
const tableRef = ref()
const parentList = ref([])

const handleClick = async (val) => {
  activeName.value = val.props.name
  if (activeName.value === 'cust') {
    eplusSearchSchema.fields[2].name = 'cskuCode'
    eplusSearchSchema.fields[2].label = '客户货号'
  } else {
    eplusSearchSchema.fields[2].name = 'oskuCode'
    eplusSearchSchema.fields[2].label = '自营产品货号'
  }
  await getList()
}

const getList = async () => {
  const res = await getSimpleStock({
    pageSize: pageForm.pageSize,
    pageNo: pageForm.pageNo,
    ...searchForm,
    ownBrandFlag: activeName.value === 'cust' ? undefined : 1,
    custProFlag: activeName.value === 'cust' ? 1 : undefined
  })
  tableData.value = res.list
  pageForm.total = res.total
  toggleSelection()
}

const toggleSelection = () => {
  if (parentList.value) {
    tableData.value.forEach((item: any) => {
      parentList.value.forEach((el: any) => {
        if (`${item.batchCode}${item.code}` == `${el.batchCode}${el.code}`) {
          tableRef.value.toggleRowSelection(item, undefined)
        }
      })
    })
  }
}

const eplusSearchSchema: EplusSearchSchema = reactive({
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'basicSkuCode',
      label: '基础产品编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'skuName',
      label: '中文名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'cskuCode',
      label: '客户货号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'purchaseContractCode',
      label: '采购合同号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'batchCode',
      label: '批次编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'purchaseUserId',
      label: '采购员'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'purchaseUserDeptId',
      label: '采购员部门'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'custName',
      label: '客户名称'
    }
  ],
  moreFields: []
})

const open = async (list) => {
  parentList.value = cloneDeep(list)
  selectedDiaData.value = cloneDeep(list)
  searchForm = {}
  pageForm.pageNo = 1
  activeName.value = 'cust'
  dialogTableVisible.value = true
  await getList()
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

const dialogTableVisible = ref(false)

const selectedDiaData = ref()

const handleSelectionChange = (selection, row) => {
  if (parentList.value.length > 0) {
    let codeList = selectedDiaData.value.map((item) => `${item.batchCode}${item.code}`)
    let rowIndex = codeList.findIndex((item) => item === `${row.batchCode}${row.code}`)
    if (codeList.includes(`${row.batchCode}${row.code}`)) {
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

const emit = defineEmits(['sure'])
const handleSure = async () => {
  if (isValidArray(selectedDiaData.value)) {
    await emit('sure', selectedDiaData.value)
    dialogTableVisible.value = false
  } else {
    message.error('请先选择数据')
  }
}

const handleCancel = () => {
  tableData.value = []
  tableRef.value.clearSelection()
  dialogTableVisible.value = false
}

defineExpose({
  open
})
</script>
