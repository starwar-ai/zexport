<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="approvePaymentApp"
    :rejectApi="rejectPaymentApp"
    :auditable="auditInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    @update="handleUpdate"
    :approve="{
      permi: 'oa:payment-app:audit',
      handler: () => {}
    }"
    :cancel="{
      permi: 'oa:payment-app:audit',
      handler: () => {}
    }"
  >
    <DocInfo :pageInfo="pageInfo" />
    <eplus-description
      title="支付说明"
      :data="pageInfo"
      :items="explainSchemas"
    >
      <template #applyerName>
        {{ pageInfo.applyer?.nickname }}
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
        <span>{{ pageInfo.financialSubjectName }}</span>
      </template>
    </eplus-description>
    <FeeShareDetail
      v-if="pageInfo.auxiliaryType === 1 && isValidArray(pageInfo.feeShare)"
      :info="pageInfo.feeShare"
    />
  </eplus-detail>
</template>
<script setup lang="tsx">
import { approvePaymentApp, rejectPaymentApp } from '@/api/audit/corporate-payments'
import * as corporatePaymentsApi from '@/api/oa/corporatePayments'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import DocInfo from '../../components/DocInfo.vue'
import UploadList from '@/components/UploadList/index.vue'
import FeeShareDetail from '@/views/ems/send/components/FeeShare/FeeShareDetail.vue'
import { isValidArray } from '@/utils/is'

const message = useMessage()
const pageInfo = ref()
const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
  param: any
  status: any
  auditInfo: any
}>()
defineOptions({ name: 'CorporatePaymentsDetail' })
//定义edit事件

const loading = ref(true)
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
    label: '账户号码'
  },
  {
    field: 'bankCode',
    label: '银行行号'
  }
])
const bankList = ref([])
const getInfo = async () => {
  loading.value = true
  bankList.value = []
  try {
    pageInfo.value = await corporatePaymentsApi.getPaymentAppInfo({ id: props.id })
    pageInfo.value.processInstanceId = props?.param
      ? props?.param
      : pageInfo.value.processInstanceId
    pageInfo.value.auditStatus = props?.status ? props?.status : ''

    if (pageInfo.value?.prepaidFlag == 1 && isValidArray(pageInfo?.paymentAppList)) {
      infoSchemas[4].disabled = false
    } else {
      infoSchemas[4].disabled = true
    }
    infoSchemas[6].disabled = !(pageInfo.value.invoiceFlag == 1)
    infoSchemas[7].disabled = !pageInfo.value.financialSubjectId
    infoSchemas[8].disabled = !(pageInfo.value.invoiceFlag == 1)
    infoSchemas[9].disabled = !(pageInfo.value.invoiceFlag == 1)
    // infoSchemas[10].disabled = !(pageInfo.value.invoiceFlag == 1)

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

const explainSchemas: EplusDescriptionItemSchema[] = [
  {
    field: 'code',
    label: '报销单号'
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
    label: '公司主体',
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
    label: '对公单号'
  },
  {
    field: 'companyName',
    label: '公司主体'
  },
  {
    field: 'amount',
    label: '预付金额',
    slots: {
      default: (data) => {
        const { row } = data
        return row.amount?.amount ? `${row.amount.amount} ${row.amount.currency}` : '-'
      }
    }
  }
])

const handleUpdate = async () => {
  await getInfo()
}

onMounted(async () => {
  if (props?.param) {
    showProcessInstanceTaskListFlag.value = true
    outDialogFlag.value = false
  } else {
    showProcessInstanceTaskListFlag.value = false
    outDialogFlag.value = true
  }
  await getInfo()
})
</script>
<style lang="scss" scoped></style>
