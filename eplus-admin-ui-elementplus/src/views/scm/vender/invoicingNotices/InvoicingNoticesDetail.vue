<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="InvoicingNoticesApi.invoicingNoticesApprove"
    :rejectApi="InvoicingNoticesApi.invoicingNoticesReject"
    :auditable="invoicingNoticesDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: 'scm:invoicing-notices:audit',
      handler: () => {}
    }"
    :approve="{
      permi: 'scm:invoicing-notices:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="基本信息"
      :data="invoicingNoticesDetail"
      :items="BasicInfoSchemas"
    >
      <template #inputUser>
        {{ invoicingNoticesDetail?.inputUser ? invoicingNoticesDetail?.inputUser?.nickname : '' }}
      </template>
      <template #managerUserCode>
        {{ invoicingNoticesDetail?.manager ? invoicingNoticesDetail?.manager?.userCode : '' }}
      </template>
      <template #managerUserName>
        {{ invoicingNoticesDetail?.manager ? invoicingNoticesDetail?.manager?.nickname : '' }}
      </template>
    </eplus-description>
    <eplus-description
      title="通知明细"
      :data="invoicingNoticesDetail"
      :items="invoicingItemListSchemas"
    >
      <template #children>
        <Table
          :columns="invoicingNoticesColumns"
          headerAlign="center"
          align="center"
          :data="invoicingNoticesDetail?.children"
        />
      </template>
    </eplus-description>
  </eplus-detail>

  <ToOrderNotice
    ref="toOrderNoticeRef"
    @success="handleUpdate"
  />
</template>
<script setup lang="tsx">
import { EplusDialog } from '@/components/EplusDialog'
import * as InvoicingNoticesApi from '@/api/scm/invoicingNotices'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { formatDictColumn, formatMoneyColumn, formatNumColumn } from '@/utils/table'
import ToOrderNotice from '@/views/scm/purchase/components/ToOrderNotice.vue'
import { formatNum, openPdf } from '@/utils/index'
import { checkPermi } from '@/utils/permission'
import { BpmProcessInstanceResultEnum } from '@/utils/constants'

const handleUpdate = async () => {
  await getDetail()
}
const props = defineProps<{
  type: string
  title?: string
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
const toOrderNoticeRef = ref()
const emits = defineEmits(['success'])
const message = useMessage()
const { query } = useRoute()
const { close } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
    })
  : { close: () => {} }

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }
const loading = ref(true)
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)

type invoicingNoticesDetailType = {
  [key: string]: any
}
const invoicingNoticesDetail: invoicingNoticesDetailType = ref({})
const BasicInfoSchemas = reactive([
  {
    field: 'code',
    label: '单据编号'
  },
  {
    field: 'companyName',
    label: '付款主体'
  },
  {
    field: 'inputUser',
    label: '录入人',
    slotName: 'inputUser'
  },
  {
    field: 'inputDate',
    label: '录入日期',
    type: 'date'
  },
  {
    field: 'venderCode',
    label: '供应商编号'
  },
  {
    field: 'venderName',
    label: '供应商名称'
  },
  {
    field: 'managerUserCode',
    label: '跟单员编号',
    slotName: 'managerUserCode'
  },
  {
    field: 'managerUserName',
    label: '跟单员名称',
    slotName: 'managerUserName'
  },
  {
    field: 'shipmentCode',
    label: '出运单号'
  },
  {
    field: 'shipInvoiceCode',
    label: '出运发票号'
  },
  {
    field: 'shipDate',
    label: '出运日期',
    type: 'date'
  },
  {
    field: 'printFlag',
    label: '打印状态',
    dictType: DICT_TYPE.PRINT_FLAG
  },
  {
    field: 'purOrderCode',
    label: '采购单号'
  },
  {
    field: 'status',
    label: '状态',
    dictType: DICT_TYPE.INVOICE_STATUS
  },
  {
    field: 'manuallyFlag',
    label: '是否手动生成',
    dictType: DICT_TYPE.CONFIRM_TYPE
  },
  {
    field: 'remark',
    label: '备注',
    xl: 8,
    lg: 12
  }
])
const invoicingNoticesColumns = [
  {
    label: '序号',
    type: 'index'
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同号',
    width: '120px'
  },
  // {
  //   field: 'purchaseSortNum',
  //   label: '采购序号'
  // },
  {
    field: 'skuCode',
    label: '产品编号',
    width: '120px'
  },
  {
    field: 'cskuCode',
    label: '客户货号'
  },
  {
    field: 'declarationQuantity',
    label: '出运数量',
    formatter: formatNumColumn()
  },
  {
    field: 'hsCode',
    label: 'HS编码'
  },
  {
    field: 'taxRate',
    label: '退税率(%)',
    formatter: formatNumColumn()
  },
  {
    field: 'invoicNoticesQuantity',
    label: '通知开票数量',
    minWidth: 150,
    formatter: formatNumColumn()
  },
  {
    field: 'hsMeasureUnit',
    label: '海关计量单位',
    minWidth: 150,
    formatter: formatDictColumn(DICT_TYPE.HS_MEASURE_UNIT)
  },
  {
    field: 'invoicSkuName',
    label: '开票品名'
  },
  {
    field: 'invoicPrice',
    label: '开票单价',
    formatter: formatNumColumn(3, true)
  },
  {
    field: 'invoicPrice',
    label: '本次开票金额',
    minWidth: 150,
    formatter: (row) => {
      if (row.invoicPrice && row.baseInvoiceQuantity) {
        return formatNum(Number(row.invoicPrice) * Number(row.baseInvoiceQuantity), 3, true)
      } else {
        return 0
      }
    }
  },
  {
    field: 'inveicRegisteredStatus',
    label: '发票签收状态',
    minWidth: 150,
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.REGISTERED_STATUS, row?.inveicRegisteredStatus) || '-'
    }
  },
  {
    field: 'inveicRegisteredQuantity',
    label: '发票签收数量',
    minWidth: 150,
    formatter: formatNumColumn()
  },
  {
    field: 'purchaseTotalQuantity',
    label: '总采购数量',
    formatter: formatNumColumn()
  },
  {
    field: 'purchaseWithTaxPrice',
    label: '采购单价',
    formatter: formatMoneyColumn(3, true)
  },
  {
    field: 'purchaseTotalAmount',
    label: '总采购金额',
    formatter: formatMoneyColumn(3, true)
  },
  {
    field: 'purchaseCurrency',
    label: '采购币别'
  },
  {
    field: 'custName',
    label: '客户名称',
    minWidth: 240
  },
  {
    field: 'custCode',
    label: '客户编号',
    width: '120px'
  },
  {
    field: 'saleContractCode',
    label: '关联销售合同',
    minWidth: 150
  },
  {
    field: 'manuallyFlag',
    label: '手动开票通知',
    minWidth: 150,
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.CONFIRM_TYPE, row?.manuallyFlag) || '-'
    }
  }
]
const invoicingItemListSchemas = reactive([
  {
    field: 'children',
    label: '',
    slotName: 'children',
    span: 24
  }
])

const handleClose = async (row: any) => {
  await message.confirm('确认作废?')
  await InvoicingNoticesApi.invoicingNoticesClose({ id: row.id })
  message.success('操作成功!')
  close()
}

const setBtn = () => {
  clearDialogActions()
  if (
    checkPermi(['scm:invoicing-notices:update']) &&
    [
      BpmProcessInstanceResultEnum.UNSUBMITTED.status,
      BpmProcessInstanceResultEnum.REJECT.status,
      BpmProcessInstanceResultEnum.CANCEL.status
    ].includes(invoicingNoticesDetail.value.auditStatus)
  ) {
    updateDialogActions(
      <el-button
        onClick={() => {
          toOrderNoticeRef.value.handleEdit(invoicingNoticesDetail.value)
        }}
      >
        编辑
      </el-button>
    )
  }
  if (
    checkPermi(['scm:invoicing-notices:close']) &&
    [
      BpmProcessInstanceResultEnum.PROCESS.status,
      BpmProcessInstanceResultEnum.CLOSE.status
    ].includes(invoicingNoticesDetail.value.auditStatus)
  ) {
    updateDialogActions(
      <el-button
        onClick={() => {
          handleClose(invoicingNoticesDetail.value)
        }}
      >
        作废
      </el-button>
    )
  }

  if (checkPermi(['scm:invoicing-notices:print'])) {
    updateDialogActions(
      <el-button
        onClick={() => {
          handlePrint('print')
        }}
      >
        打印
      </el-button>
    )
  }
  if (checkPermi(['scm:invoicing-notices:merge-print'])) {
    updateDialogActions(
      <el-button
        onClick={() => {
          handlePrint('merge-print')
        }}
      >
        合并打印
      </el-button>
    )
  }
}
const getDetail = async () => {
  loading.value = true
  try {
    let result = props.id
      ? await InvoicingNoticesApi.getInvoicingNoticesDetail({ id: props.id })
      : await InvoicingNoticesApi.getInvoicingNoticesAuditDetail({ id: query?.id })
    result?.children.forEach((cItem: any) => {
      //采购合计
      cItem.purchaseTotalAmount = { amount: 0, currency: '' }
      cItem.purchaseTotalAmount.amount =
        Number(cItem.purchaseWithTaxPrice.amount) * Number(cItem.purchaseTotalQuantity)
      cItem.purchaseTotalAmount.currency = cItem.purchaseWithTaxPrice.currency
    })
    invoicingNoticesDetail.value = result
    setBtn()
  } finally {
    loading.value = false
  }
}

const handlePrint = async (type) => {
  const url = await InvoicingNoticesApi.invoicingNoticesPrint(type, {
    id: props?.id,
    reportCode: 'scm_invoice_notice',
    companyId: invoicingNoticesDetail.value.companyId
  })
  openPdf(url)
}
onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('ID不能为空')
    if (!props.id) {
      close()
    }
  }
  if (props.id) {
    showProcessInstanceTaskListFlag.value = true
    outDialogFlag.value = false
  }
  if (query.id) {
    showProcessInstanceTaskListFlag.value = false
    outDialogFlag.value = true
  }
  await getDetail()
})
</script>
<style scoped lang="scss"></style>
