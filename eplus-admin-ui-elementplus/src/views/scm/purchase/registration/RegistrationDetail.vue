<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineRegistrationApprove"
    :rejectApi="examineRegistrationReject"
    :auditable="registrationDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: 'scm:purchase-registration:submit',
      handler: () => {},
      user: registrationDetail.creator
    }"
    :approve="{
      permi: 'scm:purchase-registration:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="发票签收信息"
      :data="registrationDetail"
      :items="purchaseRegistrationSchemas"
    >
      <template #invoiceAmount>
        {{ formatterPrice(registrationDetail.invoiceAmount, 2) }}
      </template>
    </eplus-description>
    <eplus-description
      title="附件信息"
      :data="registrationDetail"
      :items="annexSchemas"
    >
      <template #annex>
        <el-tag
          type="primary"
          v-for="item in registrationDetail.annex"
          :key="item.id"
        >
          <span
            style="cursor: pointer; color: #333"
            @click="handleDownload(item)"
            >{{ item.name }}</span
          >
        </el-tag>
      </template>
    </eplus-description>
    <el-tabs v-model="activeName">
      <el-tab-pane
        label="发票详情"
        name="first"
      >
        <Table
          :columns="registrationTableColumns"
          headerAlign="center"
          align="center"
          :data="registrationDetail?.children"
          showSummary
          sumText="合计"
          :summaryMethod="handleSummary"
          :max-height="430"
        />
      </el-tab-pane>
      <!-- <el-tab-pane
        label="操作记录"
        name="third"
      >
        <eplus-operate-log :logList="registrationDetail?.operateLogRespDTOList" />
      </el-tab-pane> -->
    </el-tabs>
  </eplus-detail>
</template>
<script setup lang="tsx">
import { examineRegistrationApprove, examineRegistrationReject } from '@/api/audit/purchase-plan'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import * as RegistrationApi from '@/api/scm/registration'
import { formatDictColumn, formatMoneyColumn } from '@/utils/table'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import { formatNum, formatterPrice, getCurrency } from '@/utils/index'
import { isAmount, isNumber, isWeight } from '@/utils/is'
import { checkPermi } from '@/utils/permission'
import { BpmProcessInstanceResultEnum, PurchaseRegistrationStatusEnum } from '@/utils/constants'

const message = useMessage()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const loading = ref(true)
const activeName = ref('first')
const { query } = useRoute()
const props = defineProps<{
  type: string
  title?: string
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()

const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }

const handleUpdate = async () => {
  await getInfo()
}
const handleDownload = (item) => {
  window.location.href = `${import.meta.env.VITE_BASE_URL}/admin-api${item.fileUrl}`
}

interface RegistrationDetailType {
  id: number
  code: string
  annex: any[]
  children: any[]
  contractList: any[]
  operateLogRespDTOList: any[]
  creator: string
}

const registrationDetail = ref<RegistrationDetailType>({
  id: 0,
  code: '',
  annex: [],
  children: [],
  contractList: [],
  operateLogRespDTOList: [],
  creator: ''
})

const purchaseRegistrationSchemas = reactive([
  {
    field: 'code',
    label: '单据编号'
  },
  {
    field: 'companyName',
    label: '付款单位'
  },
  {
    field: 'venderName',
    label: '供应商名称'
  },
  {
    field: 'venderCode',
    label: '供应商编号'
  },
  {
    field: 'invoiceCode',
    label: '发票编号'
  },
  {
    field: 'invoicedDate',
    label: '收票日期',
    type: 'date'
  },
  {
    slotName: 'invoiceAmount',
    field: 'invoiceAmount',
    label: '发票总金额'
  }
  // {
  //   field: 'reviewUser',
  //   label: '复核人',
  //   formatter: (val) => {
  //     return val ? val.nickname : '-'
  //   }
  // },
  // {
  //   field: 'reviewDate',
  //   label: '复核日期',
  //   type: 'date'
  // }
  // {
  //   field: 'taxRate',
  //   label: '税率%'
  // }
])
const annexSchemas = [
  {
    field: 'annex',
    label: '',
    slotName: 'annex',
    span: 24
  }
]
let registrationTableColumns = reactive([
  {
    label: '序号',
    field: 'id',
    fixed: 'left',
    width: 80
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同号'
  },
  {
    field: 'venderName',
    label: '供应商名称'
  },
  {
    field: 'shipInvoiceCode',
    label: '出运发票号'
  },
  {
    field: 'shippingQuantity',
    label: '数量'
  },
  {
    field: 'hsMeasureUnit',
    label: '单位',
    width: 120,
    formatter: formatDictColumn(DICT_TYPE.HS_MEASURE_UNIT)
  },
  {
    field: 'invoicPrice',
    label: '开票单价',
    formatter: formatMoneyColumn()
  },
  {
    field: 'invoicSkuName',
    label: '报关品名'
  },
  {
    field: 'invoicThisPrice',
    label: '本次开票金额',
    width: 120,
    formatter: formatMoneyColumn()
  },
  {
    field: 'invoicThisQuantity',
    label: '本次登票数'
  },
  {
    field: 'invoicNoticesQuantity',
    label: '通知开票数量',
    width: 120
  },
  {
    field: 'inveicRegisteredQuantity',
    label: '已登票数量'
  },
  // {
  //   field: 'companyName',
  //   label: '付款主体'
  // },
  {
    field: 'skuCode',
    label: '产品编号'
  },
  {
    field: 'cskuCode',
    label: '客户货号'
  },
  // {
  //   field: 'qtyPerInnerbox',
  //   label: '已开票金额'
  // },
  {
    field: 'purchaseTotalQuantity',
    label: '总采购数量'
  },
  {
    field: 'purchaseWithTaxPrice',
    label: '采购含税单价',
    width: '150',
    formatter: (row) => {
      return row.purchaseWithTaxPrice?.amount ? (
        <EplusMoneyLabel val={row.purchaseWithTaxPrice} />
      ) : (
        '0'
      )
    }
  },
  {
    field: 'purchaseTotalAmount',
    label: '采购总金额',
    width: '150',
    formatter: formatMoneyColumn()
  },
  {
    field: 'saleContractCode',
    label: '关联销售合同号',
    width: 120
  },
  {
    field: 'hsCode',
    label: 'HS编码'
  },
  {
    field: 'venderCode',
    label: '供应商编号'
  }
])

const setBtns = () => {
  clearDialogActions()
  if (
    checkPermi(['scm:purchase-registration:close']) &&
    (registrationDetail.value.status === PurchaseRegistrationStatusEnum.APPROVE.status ||
      registrationDetail.value.auditStatus === BpmProcessInstanceResultEnum.REJECT.status)
  ) {
    updateDialogActions(
      <el-button
        onClick={() => {
          handleClose()
        }}
        key="confirm"
      >
        作废
      </el-button>
    )
  }
}

const handleClose = async () => {
  await message.confirm('确认对选中数据进行作废操作吗？')
  await RegistrationApi.registrationClose(registrationDetail.value.id)
  message.success('操作成功!')
  close()
}
const getInfo = async () => {
  loading.value = true
  try {
    registrationDetail.value = props.id
      ? await RegistrationApi.getRegistrationDetail({ id: props?.id })
      : await RegistrationApi.getRegistrationAuditDetail({ id: query?.id })
    registrationDetail.value.children.forEach((item) => {
      item.invoicPrice = {
        amount: item.invoicPrice,
        currency: item.purchaseCurrency
      }

      item.invoicThisPrice = {
        amount:
          Number(item.invoicPrice.amount || item.invoicPrice) * Number(item.invoicNoticesQuantity),
        currency: item.purchaseCurrency
      }

      item.purchaseTotalAmount = {
        amount: item.purchaseTotalQuantity * item.purchaseWithTaxPrice?.amount || 0,
        currency: item.purchaseWithTaxPrice?.currency
      }
    })
    setBtns()
  } finally {
    loading.value = false
  }
}
const handleSummary = (param) => {
  const { columns, data } = param
  const sums: (string | VNode)[] = []
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '合计'
      return
    }
    const sumFileds = [
      'shippingQuantity',
      'invoicPrice',
      'invoicThisPrice',
      'invoicThisQuantity',
      'invoicNoticesQuantity',
      'inveicRegisteredQuantity',
      'purchaseTotalQuantity',
      'purchaseTotalAmount'
    ]
    let values = []
    if (sumFileds.includes(column.property) && isNumber(data[0][column.property])) {
      values = data.map((item) => Number(item[column.property]))
      let numVal = `${values.reduce((prev, curr) => {
        const value = Number(curr)
        if (!Number.isNaN(value)) {
          return prev + curr
        } else {
          return prev
        }
      }, 0)}`
      if (column.property === 'totalVolume') {
        sums[index] = formatNum(numVal / 1000000, 6)
      } else {
        sums[index] = formatNum(numVal)
      }
    } else if (sumFileds.includes(column.property) && isAmount(data[0][column.property])) {
      values = data.map((item) => Number(item[column.property].amount))
      let amountVal = `${values.reduce((prev, curr) => {
        const value = Number(curr)
        if (!Number.isNaN(value)) {
          return prev + curr
        } else {
          return prev
        }
      }, 0)}`
      sums[index] = `${getCurrency(data[0][column.property].currency)}${formatNum(amountVal)}`
    } else if (sumFileds.includes(column.property) && isWeight(data[0][column.property])) {
      values = data.map((item) => Number(item[column.property].weight))
      let weightVal = `${values.reduce((prev, curr) => {
        const value = Number(curr)
        if (!Number.isNaN(value)) {
          return prev + curr
        } else {
          return prev
        }
      }, 0)}`
      sums[index] = `${formatNum(weightVal)} ${data[0][column.property].unit}`
    } else {
      sums[index] = ''
    }
  })

  return sums
}
onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('发票签收ID不能为空')
    if (!props.id) {
      close()
    }
  }
  if (query?.id) {
    showProcessInstanceTaskListFlag.value = false
    outDialogFlag.value = true
  }
  if (props.id) {
    showProcessInstanceTaskListFlag.value = true
    outDialogFlag.value = false
  }
  await getInfo()
})
</script>
<style scoped lang="scss"></style>
