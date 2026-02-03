<template>
  <Dialog
    v-model="dialogTableVisible"
    title="选择产品"
    width="950"
    append-to-body
    destroy-on-close
    align-center
  >
    <div style="overflow: hidden">
      <el-tabs
        v-if="isShowTabs"
        v-model="activeName"
        @tab-click="handleTabsClick"
      >
        <el-tab-pane
          v-if="custTabFlag"
          label="客户产品"
          name="customer"
        />
        <el-tab-pane
          label="自营产品"
          name="owner"
        />
        <el-tab-pane
          v-if="commonTabFlag"
          label="普通产品"
          name="common"
        />
        <el-tab-pane
          v-if="agentTabFlag"
          label="联营产品"
          name="agent"
        />
        <el-tab-pane
          v-if="auxiliaryTabFlag"
          label="包材"
          name="auxiliary"
        />
      </el-tabs>
      <div class="mb10px">
        <eplus-search
          v-if="activeName === 'customer'"
          :key="activeName"
          :fields="eplusSearchSchema.fields"
          :moreFields="eplusSearchSchema.moreFields"
          @search="handleSearch"
          @reset="handleReset"
        />
        <eplus-search
          v-if="activeName === 'owner'"
          :key="activeName"
          :fields="ownerEplusSearchSchema.fields"
          :moreFields="ownerEplusSearchSchema.moreFields"
          @search="handleSearch"
          @reset="handleReset"
        />
        <eplus-search
          v-if="activeName === 'common'"
          :key="activeName"
          :fields="commonEplusSearchSchema.fields"
          :moreFields="commonEplusSearchSchema.moreFields"
          @search="handleSearch"
          @reset="handleReset"
        />
        <eplus-search
          v-if="activeName === 'agent'"
          :key="activeName"
          :fields="agentEplusSearchSchema.fields"
          :moreFields="agentEplusSearchSchema.moreFields"
          @search="handleSearch"
          @reset="handleReset"
        />
        <eplus-search
          v-if="activeName === 'auxiliary'"
          :key="activeName"
          :fields="auxiliaryEplusSearchSchema.fields"
          :moreFields="auxiliaryEplusSearchSchema.moreFields"
          @search="handleSearch"
          @reset="handleReset"
        />
      </div>
      <el-table
        border
        :data="tableData"
        @selection-change="handleSelectionChange"
        @select-all="handleSelectionAllChange"
        row-key="code"
        ref="tableRef"
        headerAlign="center"
        table-layout="auto"
        @row-click="getRow"
        :key="activeName"
        height="350"
      >
        <el-table-column
          v-if="props.selectionFlag"
          :reserve-selection="true"
          align="center"
          type="selection"
          :selectable="selectEnable"
          fixed="left"
        />
        <el-table-column
          v-for="item in tableSchema"
          :key="item.field"
          :prop="item.field"
          :width="item.width"
          :min-width="item.minWidth || '250px'"
          :label="item.label"
        >
          <template #default="{ row, $index }">
            <!--静态数据处理项-->
            <Slot
              v-if="item.slot"
              :row="row"
              :item="item"
              :index="$index"
            />
            <Formatter
              v-else-if="item.formatter"
              :row="row"
              :item="item"
              :index="$index"
            />
            <div v-else>
              <!--显示项-->
              {{ item.field && row[item.field] }}
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <Pagination
        v-show="tableData?.length"
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
import * as PurchasePlanApi from '@/api/scm/purchasePlan'
import { cloneDeep } from 'lodash-es'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import * as classApi from '@/api/pms/class'
import { getDictValue } from '@/utils/dict'
import { EplusFormTableSchema } from '../index'
import { isValidArray } from '@/utils/is'

defineOptions({ name: 'CostDialog' })
const message = useMessage()
const dialogTableVisible = ref(false)
const tableRef = ref()
const tableData = ref([])
let parentList = ref([])
const loading = ref(false)
const activeName = ref('')
const props = defineProps({
  schema: {
    type: Array as PropType<EplusFormTableSchema[]>,
    required: true
  },
  defaultVal: {
    type: Object,
    required: false
  },
  selectionFlag: {
    type: Boolean,
    required: false
  },
  isShowTabs: {
    type: Boolean,
    required: false
  },
  commonTabFlag: {
    type: Boolean,
    required: false
  },
  agentTabFlag: {
    type: Boolean,
    required: false
  },
  auxiliaryTabFlag: {
    type: Boolean,
    required: false
  },
  ownCustCodeFlag: {
    type: Boolean,
    required: false,
    default: false
  }
})

const Formatter = (itemProps: { row: Recordable; item: EplusFormTableSchema; index: number }) => {
  const item = itemProps.item
  const row = itemProps.row
  const index = itemProps.index
  return item.formatter(item, row, index)
}

const Slot = (itemProps: { row: Recordable; item: EplusFormTableSchema; index: number }) => {
  const item = itemProps.item
  const row = itemProps.row
  const index = itemProps.index
  return item.slot(item, row, index)
}
const pageForm = reactive({
  pageSize: 10,
  pageNo: 1,
  total: 0
})

const treeList = ref([])
const custCodeRef = ref('')
let searchForm = reactive({})
const handleTabsClick = async (val) => {
  tableData.value = []
  activeName.value = val.props.name
  searchForm = {}
  await getList()
}
const findCategoryName = (val) => {
  if (Array.isArray(val) && val?.length > 0) {
    let tempList: any = cloneDeep(treeList.value)
    let selectList = []
    selectList = tempList.find((item) => item.id === val[0])
    return selectList.name || ''
  }
}
const commonEplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'skuCode',
      label: '产品编码'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'skuName',
      label: '中文名称'
    },
    {
      component: (
        <eplus-tree-select
          checkStrictly={false}
          placeholder={'产品分类'}
          treeList={treeList}
          filterable
        />
      ),
      name: 'categoryId',
      label: '产品分类',
      formatter: (args: any[]) => {
        return findCategoryName(args)
      }
    }
  ],
  moreFields: []
}

const agentEplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'skuName',
      label: '中文名称'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'cskuCode',
      label: '客户货号'
    }
  ],
  moreFields: []
}

const auxiliaryEplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'skuCode',
      label: '产品编码'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'skuName',
      label: '中文名称'
    }
  ],
  moreFields: []
}

const searchSchema: EplusSearchSchema = {
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
      component: (
        <eplus-tree-select
          checkStrictly={false}
          placeholder={'产品分类'}
          treeList={treeList}
        />
      ),
      name: 'categoryId',
      label: '产品分类',
      formatter: (args: any[]) => {
        return findCategoryName(args)
      }
    }
  ],
  moreFields: []
}

const ownerEplusSearchSchema: EplusSearchSchema = {
  fields: [
    ...searchSchema.fields,
    {
      component: <el-input clearable></el-input>,
      name: 'oskuCode',
      label: '自营产品货号'
    }
  ],
  moreFields: []
}
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    ...searchSchema.fields,
    {
      component: <el-input clearable></el-input>,
      name: 'cskuCode',
      label: '客户货号'
    }
  ],
  moreFields: []
}

//--不可选择没有报价信息的产品
const selectEnable = (row) => {
  return activeName.value == 'auxiliary' || row.quoteitemList?.length
}
const handleSearch = (model) => {
  searchForm = { ...model }
  pageForm.pageNo = 1
  getList()
}
const handleReset = () => {
  searchForm = {}
  pageForm.pageNo = 1
  getList()
}

const emit = defineEmits<{
  sure: [link: string]
}>()
const tableSchema = ref([])

const getList = async () => {
  loading.value = true
  try {
    var params, ownerParams, res
    if (activeName.value == 'owner') {
      tableSchema.value = props.schema.filter((item) => item.field !== 'cskuCode')
      ownerParams = {
        pageSize: pageForm.pageSize,
        allProduct: 1,
        pageNo: pageForm.pageNo,
        custProFlag: 0,
        custCode: props.ownCustCodeFlag ? custCodeRef.value : undefined,
        ownBrandFlag: getDictValue(DICT_TYPE.CONFIRM_TYPE, '是'),
        ...searchForm,
        ...props.defaultVal
      }
      res = await PurchasePlanApi.getSkuSimpleOwnList({ ...ownerParams })
    } else if (activeName.value == 'agent') {
      tableSchema.value = props.schema.filter(
        (item) => !['basicSkuCode', 'categoryName', 'brandName', 'oskuCode'].includes(item.field)
      )
      params = {
        agentFlag: 1,
        pageSize: pageForm.pageSize,
        pageNo: pageForm.pageNo,
        custCode: custCodeRef.value,
        ...searchForm,
        ...props.defaultVal
      }
      res = await PurchasePlanApi.getSkuSimpleList({ ...params })
    } else if (activeName.value == 'auxiliary') {
      params = {
        skuType: 4,
        pageSize: pageForm.pageSize,
        pageNo: pageForm.pageNo,
        ...searchForm,
        ...props.defaultVal
      }
      res = await PurchasePlanApi.getSkuSimpleList({ ...params })
    } else {
      tableSchema.value = props.schema.filter((item) => item.field !== 'oskuCode')
      params = {
        agentFlag: 0,
        custProFlag: activeName.value == 'customer' ? 1 : 0,
        pageSize: pageForm.pageSize,
        pageNo: pageForm.pageNo,
        custCode: activeName.value == 'customer' ? custCodeRef.value : '',
        ownBrandFlag:
          activeName.value == 'customer' ? undefined : getDictValue(DICT_TYPE.CONFIRM_TYPE, '否'),
        ...searchForm,
        ...props.defaultVal
      }
      res = await PurchasePlanApi.getSkuSimpleList({ ...params })
    }
    tableData.value = res.list || []
    pageForm.total = res.total || 0
    loading.value = false
    toggleSelection()
  } catch {
    loading.value = false
  } finally {
    loading.value = false
  }
}
const custTabFlag = ref(true)
const open = async (list, custCode = '') => {
  let treeListTemp = await classApi.getClassTree()
  treeList.value = cloneDeep(treeListTemp)
  parentList.value = cloneDeep(list.value)
  selectedDiaData.value = cloneDeep(list.value || [])
  custCodeRef.value = custCode
  searchForm = {}
  pageForm.pageNo = 1
  dialogTableVisible.value = true
  if (props.isShowTabs) {
    if (custCode) {
      activeName.value = 'customer'
      custTabFlag.value = true
    } else {
      activeName.value = 'owner'
      custTabFlag.value = false
    }
  } else {
    activeName.value = ''
  }
  await getList()
}

const toggleSelection = () => {
  if (parentList.value) {
    tableData.value.forEach((item) => {
      parentList.value.forEach((el) => {
        if (item.cskuCode == el.cskuCode) {
          tableRef.value.toggleRowSelection(item, undefined)
        }
      })
    })
  }
}

const selectedDiaData = ref([])
const handleSelectionChange = (selection, row) => {
  if (parentList.value?.length > 0) {
    let codeList = selectedDiaData.value.map((item) => item.code)
    let rowIndex = codeList.findIndex((item) => item === row.code)
    if (codeList.includes(row.code)) {
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

const getRow = (row) => {
  if (isValidArray(row.quoteitemList)) {
    let codeList = selectedDiaData.value.map((item) => item.code)
    let rowIndex = codeList.findIndex((item) => item === row.code)
    if (codeList.includes(row.code)) {
      selectedDiaData.value.splice(rowIndex, 1)
      tableRef.value.toggleRowSelection(row, undefined)
    } else {
      selectedDiaData.value.push(row)
      tableRef.value.toggleRowSelection(row, undefined)
    }
  } else {
    message.warning('该产品暂无供应商报价，不可选择')
  }
}

const handleCancel = () => {
  tableData.value = []
  tableRef.value.clearSelection()
  dialogTableVisible.value = false
}
const handleSure = async () => {
  // await checkValidSku(selectedDiaData.value?.map((item: any) => item.code).join(','))
  let len = selectedDiaData.value?.length
  if (len > 0) {
    emit(
      'sure',
      // @ts-ignore
      selectedDiaData.value.map((item: any, index) => {
        let defaultQuoteitemList = item?.quoteitemList?.filter((item) => item.defaultFlag === 1)[0]
        item.skuId = item?.id
        //真实退税率 报关退税率和供应商退税率进行比较， 谁小取谁， 如果有一个为空， 取另一个， 如果两个都为空， 则空
        let realTaxRefundRate = null
        if (item.taxRefundRate) {
          realTaxRefundRate = item.taxRefundRate
        }
        if (defaultQuoteitemList && defaultQuoteitemList.taxRate) {
          if (!realTaxRefundRate) {
            realTaxRefundRate = defaultQuoteitemList.taxRate
          } else if (Number(realTaxRefundRate) > Number(defaultQuoteitemList.taxRate)) {
            realTaxRefundRate = defaultQuoteitemList.taxRate
          }
        }
        return {
          ...item,
          ...defaultQuoteitemList,
          skuCode: item?.code,
          // totalPurchaseAmount: defaultQuoteitemList?.withTaxPrice * item?.purchaseQuantity,
          purchaseCurrency: defaultQuoteitemList?.currency,
          supplierName: defaultQuoteitemList?.venderName,
          purchaseUnitPrice: defaultQuoteitemList?.unitPrice,
          purchasePackagingPrice: defaultQuoteitemList?.packagingPrice,
          purchaseWithTaxPrice: defaultQuoteitemList?.withTaxPrice,
          purchaseTotalAmount: defaultQuoteitemList?.withTaxPrice * item?.purchaseQuantity,
          name: item?.skuName,
          freeFlag: item?.freeFlag || 0,
          unit: item?.measureUnit,
          purchaseQuantity: item?.purchaseQuantity || 0,
          quantity: item?.quantity || 0,
          realTaxRefundRate: realTaxRefundRate
        }
      })
    )
    handleCancel()
  } else {
    message.warning('请选择产品')
  }
}
defineExpose({ open })
onMounted(async () => {
  try {
    treeList.value = await classApi.getClassTree()
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
