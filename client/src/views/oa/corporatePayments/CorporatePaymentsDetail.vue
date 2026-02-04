<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="approvePaymentApp"
    :rejectApi="rejectPaymentApp"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: 'oa:payment-app:submit',
      handler: () => {}
    }"
    :edit="{
      user: pageInfo.applyer?.userId,
      permi: 'oa:payment-app:update',
      handler: () => goEdit('')
    }"
    :approve="{
      permi: 'oa:payment-app:audit',
      handler: handleApprove
    }"
    @get-tasks="getTasks"
    @update="handleUpdate"
  >
    <DocInfo :pageInfo="pageInfo" />
    <eplus-description
      title="支付说明"
      :data="pageInfo"
      :items="explainSchemas"
    >
      <template #applyerName>
        {{ pageInfo.applyer?.nickname }}（{{ pageInfo.applyer?.deptName }}）
      </template>
    </eplus-description>

    <eplus-description
      title="支付信息"
      :data="pageInfo"
      :items="infoSchemas"
    >
      <template #bankList>
        <Table
          :columns="bankListColumns"
          headerAlign="center"
          align="center"
          :data="bankList"
        />
      </template>
      <template #paymentAppList>
        <Table
          :columns="paymentAppListColumns"
          headerAlign="center"
          align="center"
          :data="pageInfo?.paymentAppLinkList"
        />
      </template>
      <template #invoice>
        <UploadList
          :fileList="pageInfo.invoice"
          disabled
        />
      </template>
      <template #financialSubjectId>
        <span v-if="auditFlag">{{ pageInfo.financialSubjectName }}</span>
        <eplus-subject-tree-select
          v-else
          v-model="pageInfo.financialSubjectId"
          class="!w-100%"
          :clearable="false"
          @change="
            () => {
              pageInfo.financialSubjectName = ''
            }
          "
        />
      </template>
    </eplus-description>
    <ShipmentlistSendFeeShareDetail
      v-if="[2, 3, 4].includes(pageInfo.relationType)"
      :info="pageInfo"
    />
    <FeeShareDetail
      v-if="pageInfo.auxiliaryType === 1 && isValidArray(pageInfo.feeShare)"
      :info="pageInfo.feeShare"
    />
  </eplus-detail>
</template>
<script setup lang="tsx">
import { maskBankAccount, openPdf } from '@/utils/index'
import { approvePaymentApp, rejectPaymentApp } from '@/api/audit/corporate-payments'
import * as corporatePaymentsApi from '@/api/oa/corporatePayments'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import DocInfo from '../components/DocInfo.vue'
import UploadList from '@/components/UploadList/index.vue'
import FeeShareDetail from '@/views/ems/send/components/FeeShare/FeeShareDetail.vue'
import ShipmentlistSendFeeShareDetail from './ShipmentlistSendFeeShareDetail.vue'
import { isValidArray } from '@/utils/is'
import { useUserStore } from '@/store/modules/user'
import download from '@/utils/download'
import { formatMoneyColumn } from '@/utils/table'
import { checkPermi } from '@/utils/permission'

const permissions = useUserStore().getPermissions
const userId = useUserStore().getUser.id
const message = useMessage()
const pageInfo = ref()
const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'CorporatePaymentsDetail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }
const { updateDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
    })
  : { updateDialogActions: () => {} }
const loading = ref(true)
const { query } = useRoute()
const bankListColumns = reactive([
  {
    field: 'bank',
    label: '开户行'
  },
  {
    field: 'bankAddress',
    label: '开户行地址'
  },
  {
    field: 'bankPoc',
    label: '账户名称'
  },
  {
    field: 'bankAccount',
    label: '账户号码',
    slots: {
      default: (data) => {
        const { row } = data
        return maskBankAccount(row.bankAccount)
      }
    }
  },
  {
    field: 'bankCode',
    label: '银行行号'
  }
])
const bankList = ref([])
const editFlag = ref(false)
const getInfo = async () => {
  loading.value = true
  bankList.value = []
  try {
    pageInfo.value = props.id
      ? await corporatePaymentsApi.getPaymentAppInfo({ id: props.id })
      : await corporatePaymentsApi.getAuditPaymentAppInfo({ id: query?.id })

    if (isValidArray(pageInfo.value?.paymentAppList)) {
      infoSchemas[4].disabled = false
    } else {
      infoSchemas[4].disabled = true
    }

    editFlag.value =
      pageInfo.value.processInstanceId &&
      permissions.includes('oa:payment-app:saveAndAudit') &&
      pageInfo.value.invoiceFlag == 1

    infoSchemas[6].disabled = !(pageInfo.value.invoiceFlag == 1)
    infoSchemas[7].disabled = !editFlag.value
    infoSchemas[8].disabled = !(pageInfo.value.invoiceFlag == 1)
    infoSchemas[9].disabled = !(pageInfo.value.invoiceFlag == 1)
    infoSchemas[10].disabled = !(pageInfo.value.invoiceFlag == 1)

    let bankObj = {
      bank: pageInfo.value.bank,
      bankAccount: pageInfo.value.bankAccount,
      bankAddress: pageInfo.value.bankAddress,
      bankCode: pageInfo.value.bankCode,
      bankPoc: pageInfo.value.bankPoc
    }
    bankList.value.push(bankObj)
  } catch {
    pageInfo.value = {}
  } finally {
    loading.value = false
  }
}
// const editItem = reactive()
const explainSchemas: EplusDescriptionItemSchema[] = [
  {
    field: 'code',
    label: '费用单号'
  },
  {
    slotName: 'applyerName',
    field: 'applyerName',
    label: '录入人'
  },
  {
    field: 'createTime',
    type: 'date',
    label: '申请日期'
  },
  {
    field: 'printDate',
    type: 'date',
    label: '打印日期'
  },
  {
    field: 'prepaidFlag',
    label: '单据类型',
    dictType: DICT_TYPE.PAYMENT_APPLY_TYPE
  },
  {
    field: 'code',
    label: '付款单号'
  },
  {
    field: 'amount',
    label: '本次支付金额',
    type: 'money'
  },
  {
    field: 'reason',
    label: '支付事由'
  },
  {
    field: 'remark',
    label: '备注'
  }
]

const infoSchemas = reactive([
  {
    label: '费用主体',
    field: 'companyName'
  },
  {
    label: '支付对象类型',
    field: 'businessSubjectType',
    dictType: DICT_TYPE.BUSINESS_SUBJECT_TYPE
  },
  {
    label: '支付对象',
    field: 'businessSubjectName'
  },
  {
    slotName: 'bankList',
    field: 'bankList',
    label: '账户信息',
    span: 24
  },
  {
    slotName: 'paymentAppList',
    field: 'paymentAppList',
    label: '历史付款单',
    span: 24,
    disabled: true
  },
  {
    field: 'invoiceFlag',
    label: '是否有发票',
    dictType: DICT_TYPE.IS_INT,
    span: 24
  },

  {
    field: 'feeName',
    label: '发票名称',
    disabled: true
  },
  {
    slotName: 'financialSubjectId',
    field: 'financialSubjectId',
    label: '管理科目',
    disabled: true
  },
  {
    field: 'invoiceAmountList',
    label: '发票金额',
    type: 'money',
    disabled: true
  },
  {
    slotName: 'invoice',
    field: 'invoice',
    label: '发票附件',
    disabled: true,
    span: 12
  },
  // {
  //   field: 'payAmount',
  //   label: '发票金额',
  //   type: 'money',
  //   disabled: true
  // },
  {
    field: 'totalPaymentAmount',
    type: 'money',
    label: '累计支付金额'
  },
  {
    field: 'totalInvoiceAmount',
    type: 'money',
    label: '累计发票金额'
  }
])

const paymentAppListColumns = reactive([
  {
    field: 'code',
    label: '付款编号'
  },
  {
    field: 'companyName',
    label: '公司主体'
  },
  {
    field: 'amount',
    label: '预付金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'reason',
    label: '支付事由'
  }
])
const handleApprove = async () => {
  if (editFlag.value) {
    if (!pageInfo.value.financialSubjectId) {
      message.warning(`管理科目不能为空`)
      return false
    }

    await corporatePaymentsApi.updatePaymentApp({
      ...pageInfo.value
    })
    return true
  }
}

const auditFlag = ref(false)
const getTasks = (data) => {
  if (data.result == 2) {
    auditFlag.value = true
  }
}
const handleUpdate = async () => {
  await getInfo()
}

onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('ID不能为空')
    if (!props.id) {
      close()
    }
  }
  if (query.id) {
    showProcessInstanceTaskListFlag.value = false
    outDialogFlag.value = true
  }
  if (props.id) {
    showProcessInstanceTaskListFlag.value = true
    outDialogFlag.value = false
  }
  await getInfo()
  if (permissions.includes('oa:payment-app:print')) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await print()
        }}
        key="print"
      >
        打印
      </el-button>
    )
  }
  if (permissions.includes('oa:payment-app:print') && pageInfo.value.relationType === 2) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await exportExcel()
        }}
      >
        导出寄件明细
      </el-button>
    )
  }
  if (permissions.includes('oa:payment-app:print') && pageInfo.value.relationType === 3) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await printShipmentFeeDetail()
        }}
      >
        打印船代费用明细
      </el-button>
    )
  }

  if (
    userId === pageInfo.value.applyer?.userId &&
    [0, 4].includes(pageInfo.value.auditStatus) &&
    checkPermi(['oa:payment-app:delete'])
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await message.confirm('确定删除吗？')
          await corporatePaymentsApi.deletePaymentApp(pageInfo.value.id)
          message.success('删除成功')
          close()
        }}
      >
        删除
      </el-button>
    )
  }
})

const exportExcel = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    const data = await corporatePaymentsApi.exportPoireport({
      id: props?.id,
      reportCode: 'payment-app-send-detail'
    })
    console.log(data)
    if (data && data.size) {
      download.excel(data, '寄件明细.xlsx')
    }
  } catch {}
  return
}

const print = async () => {
  const url = await corporatePaymentsApi.print({
    id: props?.id,
    reportCode: 'payment-app',
    companyId: pageInfo.value.companyId
  })
  openPdf(url)
}

const printShipmentFeeDetail = async () => {
  const url = await corporatePaymentsApi.printShipmentFeeDetail({
    id: props?.id,
    reportCode: 'payment-app-shipment-fee-detail',
    companyId: pageInfo.value.companyId
  })
  openPdf(url)
}

defineExpose({ handleApprove })
</script>
<style lang="scss" scoped></style>
