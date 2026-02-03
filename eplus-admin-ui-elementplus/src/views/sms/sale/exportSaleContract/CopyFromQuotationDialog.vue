<template>
  <Dialog
    v-model="dialogTableVisible"
    title="从报价单复制"
    width="950"
    append-to-body
    destroy-on-close
  >
    <div class="mb10px">
      <eplus-search
        :fields="eplusSearchSchema.fields"
        :moreFields="eplusSearchSchema.moreFields"
        @search="handleSearch"
        @reset="handleReset"
      />
    </div>
    <div style="overflow: hidden">
      <Table
        :columns="tableColumns"
        headerAlign="center"
        align="center"
        :data="tableData"
        row-key="id"
        @selection-change="handleSelectionChange"
      />
      <Pagination
        v-show="tableData?.length"
        v-model:limit="pageForm.pageSize"
        v-model:page="pageForm.pageNo"
        :total="pageForm.total"
        @pagination="getList"
        layout="total, sizes, prev, pager, next, jumper"
      />
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
import { formatDictColumn, formatMoneyColumn } from '@/utils/table'
import { DICT_TYPE } from '@/utils/dict'
import { formatProductTypeColumn, formatSkuCodeColumn } from '@/utils/pms/index'
import * as quotationApi from '@/api/sms/quotation'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import { isValidArray } from '@/utils/is'
import { getSkuInfo } from '@/api/sms/saleContract/domestic'

const message = useMessage()
const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      name: 'skuCode',
      label: '产品编码',
      component: <el-input clearable></el-input>
    }
  ],
  moreFields: []
}
const pageForm = reactive({
  pageSize: 10,
  pageNo: 1,
  total: 0
})
let searchForm = reactive({})
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
let tableData = ref([])
const getList = async () => {
  const res = await quotationApi.getQuotationItemPage({
    custId: custId.value,
    status: 3,
    custProFlag: 1,
    ...searchForm,
    ...pageForm
  })
  tableData.value = res.list
  pageForm.total = res.total
}

const dialogTableVisible = ref(false)

const custId = ref()
const open = async (newCustId: number) => {
  pageForm.pageNo = 1
  custId.value = newCustId
  await getList()
  dialogTableVisible.value = true
}

const emit = defineEmits<{ confirm: Array<any> }>()
defineExpose({ open })

const tableColumns = [
  {
    width: '60px',
    type: 'selection',
    fixed: true
  },
  {
    field: 'custName',
    label: '客户名称',
    minWidth: '100px'
  },
  {
    field: 'settlementTermType',
    label: '价格条款',
    formatter: formatDictColumn(DICT_TYPE.PRICE_TYPE),
    minWidth: '100px'
  },
  {
    field: 'currency',
    label: '币种',
    dictType: formatDictColumn(DICT_TYPE.CURRENCY_TYPE),
    minWidth: '100px'
  },
  {
    field: 'skuCode',
    label: '产品编码',
    formatter: formatSkuCodeColumn,
    minWidth: '100px'
  },
  {
    label: '产品类型',
    field: 'skutype',
    formatter: formatProductTypeColumn,
    minWidth: '100px'
  },
  {
    field: 'name',
    label: '中文名称',
    minWidth: '100px'
  },
  {
    field: 'nameEng',
    label: '产品英文名称',
    minWidth: '100px'
  },
  {
    field: 'withTaxPrice',
    label: '工厂报价',
    minWidth: '150px',
    formatter: formatMoneyColumn()
  },
  {
    field: 'profitRate',
    label: '利润率(%)',
    minWidth: '150px'
  },
  {
    field: 'quotation',
    label: '产品报价',
    minWidth: '150px',
    formatter: formatMoneyColumn()
  }
]
const seletedList = ref([])
const handleSelectionChange = (selection) => {
  seletedList.value = selection
}

const getSkuInfoList = async (list) => {
  let reqList = []
  list.forEach(async (item) => {
    reqList.push(getSkuInfo(item.skuCode))
  })
  let infoList = await Promise.all(reqList)
  return infoList.map((item: any, index) => {
    let defaultQuoteitemList = item?.quoteitemDTOList?.filter((item) => item.defaultFlag === 1)[0]
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
      unitCost: defaultQuoteitemList?.unitPrice,
      unitCostRate: defaultQuoteitemList?.packagingPrice,
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
      realTaxRefundRate: realTaxRefundRate,
      quotation: list.find((el) => el.skuCode === item?.code)?.quotation || {}
    }
  })
}
const handleSure = async () => {
  if (isValidArray(seletedList.value)) {
    let resList = await getSkuInfoList(seletedList.value)
    emit('confirm', resList || [])
    dialogTableVisible.value = false
  } else {
    message.warning('请选择数据')
  }
}
const handleCancel = () => {
  dialogTableVisible.value = false
}
</script>
