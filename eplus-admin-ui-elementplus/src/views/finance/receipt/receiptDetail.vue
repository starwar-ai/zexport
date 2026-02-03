<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineApprove"
    :rejectApi="examineReject"
    :auditable="feesDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :submit="{
      permi: 'fms:payment:submit',
      handler: () => {}
    }"
    :cancel="{
      permi: 'fms:payment:submit',
      handler: () => {}
    }"
    :approve="{
      permi: 'fms:payment:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="收款说明"
      :data="feesDetail"
      :items="PaymentInstructionSchemas"
    >
      <template #paymentList>
        <el-tag
          type="primary"
          v-for="item in feesDetail.paymentList"
          :key="item.id"
        >
          <span>{{ item.name }}</span>
        </el-tag>
      </template>
      <template #applyer>
        <span>{{
          feesDetail?.applyer?.nickname ? feesDetail?.applyer?.nickname : defaultData
        }}</span>
        <span>{{
          feesDetail?.applyer?.deptName ? '(' + feesDetail?.applyer?.deptName + ')' : defaultData
        }}</span>
      </template>
    </eplus-description>
    <eplus-description
      title="收款信息"
      :data="feesDetail"
      :items="PaymentInfoSchemas"
    >
      <template #bankList>
        <Table
          :columns="pocColumns"
          headerAlign="center"
          align="center"
          :data="feesDetail?.bankList"
        />
      </template>
      <template #amount>
        <span>{{ feesDetail?.amount?.amount ? feesDetail.amount.amount : defaultData }}</span>
        &nbsp;
        <span type="primary">{{
          feesDetail?.amount?.amount ? feesDetail.amount.currency : defaultData
        }}</span>
      </template>
      <template #businessSubjectName>
        <span>{{
          feesDetail?.businessSubjectName?.nickname
            ? feesDetail.businessSubjectName?.nickname
            : defaultData
        }}</span>
        <span type="primary">{{
          feesDetail?.businessSubjectName?.deptName
            ? '(' + feesDetail.businessSubjectName.deptName + ')'
            : '(' + defaultData + ')'
        }}</span>
      </template>
      <template #receiptName>
        <span>{{
          feesDetail?.receiptUser?.nickname ? feesDetail.receiptUser?.nickname : defaultData
        }}</span>
        <span type="primary">{{
          feesDetail?.receiptUser?.deptName
            ? '(' + feesDetail.receiptUser.deptName + ')'
            : '(' + defaultData + ')'
        }}</span>
      </template>
    </eplus-description>
    <ReceiptDialog
      ref="receiptDialogRef"
      @success="handleUpdate"
    />
  </eplus-detail>
</template>
<script setup lang="tsx">
import { examineApprove, examineReject } from '@/api/audit/payment'
import * as ReceiptApi from '@/api/finance/receipt'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDialog } from '@/components/EplusDialog'
import ReceiptDialog from './components/ReceiptDialog.vue'
import { DICT_TYPE, getStrDictOptions } from '@/utils/dict'

const receiptDialogRef = ref()
const openReceiptDialog = (code, id) => {
  receiptDialogRef.value.open(code, id)
}
const emit = defineEmits(['success'])
const message = useMessage()
const feesDetail = ref() // 借款申请单详情
const defaultData = '-'
const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'FeesDetail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const loading = ref(true)
const { query } = useRoute()
const pocColumns = reactive([
  {
    field: 'bankPoc',
    label: '账户名称'
  },
  {
    field: 'bankAccount',
    label: '账户号码'
  },
  {
    field: 'bank',
    label: '开户行'
  },
  {
    field: 'bankAddress',
    label: '开户行地址'
  },
  {
    field: 'bankCode',
    label: '银行行号'
  }
])
const { updateDialogActions, clearDialogActions } = query.id
  ? {}
  : (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
const paymentBtnShow = (data) => {
  if (!data.status && data.auditStatus === 2) {
    updateDialogActions(
      <el-button
        onClick={() => {
          openReceiptDialog(data.businessCode, data?.id)
        }}
        type="primary"
        key="payment"
      >
        收款确认
      </el-button>
    )
  } else {
    clearDialogActions('payment')
  }
}
const getFmsPaymentDetail = async () => {
  loading.value = true
  try {
    feesDetail.value = props.id
      ? await ReceiptApi.getReceipt({ id: props.id }).then((res) => {
          if (res && outDialogFlag.value === false) {
            paymentBtnShow(res)
          }
          return res
        })
      : await ReceiptApi.getAuditReceipt({ id: query?.id })
  } catch {
    feesDetail.value = {}
  } finally {
    loading.value = false
  }
}
/**
 * 生成明细信息
 * @param r
 */
//收款说明
const PaymentInstructionSchemas = [
  {
    field: 'code',
    label: '收款单号'
    // dictType: DICT_TYPE.CURRENCY_TYPE
  },
  {
    field: 'businessType',
    label: '业务类型',
    dictType: DICT_TYPE.BUSINESS_TYPE
  },
  {
    field: 'createTime',
    label: '申请时间',
    type: 'time'
  },
  {
    field: 'applyer',
    label: '申请人(部门)',
    slotName: 'applyer'
  },
  {
    field: 'remark',
    label: '收款事由',
    xl: 8,
    lg: 12
  }
]
//收款信息
const PaymentInfoSchemas = reactive([
  {
    field: 'companyName',
    label: '收款主体'
  },
  {
    field: 'businessSubjectName',
    label: '收款对象名称',
    slotName: 'businessSubjectName'
  },
  {
    field: 'businessSubjectType',
    label: '收款对象类型',
    dictType: DICT_TYPE.BUSINESS_SUBJECT_TYPE
  },
  {
    field: 'status',
    label: '收款状态',
    dictType: DICT_TYPE.RECEIPT_STATUS
  },
  {
    field: 'receiptName',
    label: '收款人(部门)',
    slotName: 'receiptName'
  },
  {
    field: 'receiptTime',
    label: '收款时间',
    type: 'time'
  },
  {
    field: 'receiptType',
    label: '收款方式',
    dictType: DICT_TYPE.LOAN_TYPE
  },
  {
    field: 'amount',
    label: '收款金额',
    slotName: 'amount'
  },
  {
    field: 'bankList',
    label: '账户信息',
    span: 24,
    editable: true,
    disabled: true,
    labelWidth: '100px',
    slotName: 'bankList'
  }
])

const handleUpdate = async () => {
  formatFormData()
}
const dictKeyFormat = (arr, key) => {
  if (arr && key && arr?.length) {
    let result
    arr.forEach((item) => {
      if (Number(item.value) === key) {
        result = item.label
      }
    })
    return result
  } else {
    return '收款对象'
  }
}
const formatFormData = async () => {
  await getFmsPaymentDetail().then((res) => {
    const { bank, bankAccount, bankAddress, bankCode, bankPoc } = feesDetail.value
    feesDetail.value.bankList = [{ bank, bankAccount, bankAddress, bankCode, bankPoc }]
    if (feesDetail.value.businessSubjectName) {
      feesDetail.value.businessSubjectName = JSON.parse(feesDetail.value?.businessSubjectName)
    }
    PaymentInfoSchemas.map((item, index) => {
      if (item.field === 'businessSubjectName') {
        return (item.label = `${dictKeyFormat(getStrDictOptions(DICT_TYPE.BUSINESS_SUBJECT_TYPE), feesDetail.value?.businessSubjectType)}名称`)
      }
      if (feesDetail.value.receiptType === 1) {
        //收款方式为现金则去除银行账户信息
        if (item.field === 'bankList') {
          PaymentInfoSchemas.splice(index, 1)
        }
      }
    })
  })
}
onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('费用收款单ID不能为空')
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
  formatFormData()
})
// onUnmounted(() => {
//   clearDialogActions()
// })
</script>
<style lang="scss" scoped></style>
