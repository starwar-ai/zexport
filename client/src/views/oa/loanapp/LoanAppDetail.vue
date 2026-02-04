<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineApprove"
    :rejectApi="examineReject"
    :auditable="loanApp"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :submit="{
      permi: 'oa:loan-app:submit',
      handler: () => {}
    }"
    :cancel="{
      permi: 'oa:loan-app:submit'
    }"
    :edit="{
      user: loanApp.applyer.userId,
      permi: 'oa:loan-app:update',
      handler: () => goEdit('借款单')
    }"
    :approve="{
      permi: 'oa:loan-app:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="借款信息"
      :data="loanApp"
      :items="basicInfo"
    >
      <template #applyerName>
        {{ loanApp?.applyer?.nickname }}
      </template>
      <template #applyerCurrency>
        {{ loanApp?.amount?.currency }}
      </template>
    </eplus-description>

    <eplus-description
      v-if="'转账' === getDictLabel(DICT_TYPE.LOAN_TYPE, loanApp.loanType)"
      title="转账信息"
      :data="loanApp"
      :items="transferInfo"
    >
      <template #bankAccount>
        {{ maskBankAccount(loanApp?.bankAccount) }}
      </template>
    </eplus-description>
    <eplus-form-items
      title="还款信息"
      :formSchemas="repayListSchemas"
      v-if="isValidArray(loanApp?.repayList)"
    >
      <template #repayList>
        <Table
          :columns="repayColumns"
          headerAlign="center"
          align="center"
          :data="loanApp?.repayList"
        />
      </template>
    </eplus-form-items>
  </eplus-detail>

  <eplus-dialog
    ref="repayDialogRef"
    @success="handleRefresh"
  >
    <template #create>
      <repay-app-form
        mode="create"
        :row="loanApp"
        @handleSuccess="handleRefresh"
      />
    </template>
  </eplus-dialog>
</template>
<script setup lang="tsx">
import { maskBankAccount, openPdf } from '@/utils/index'
import { examineApprove, examineReject } from '@/api/audit/loan-app'
import * as LoanAppApi from '@/api/oa/loanapp'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import RepayAppForm from '../repayapp/RepayAppForm.vue'
import { isValidArray } from '@/utils/is'
import { checkPermi } from '@/utils/permission'

const message = useMessage()
const loanApp = ref<LoanAppApi.LoanAppVO>({} as LoanAppApi.LoanAppVO) // 借款申请单详情

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'LoanAppDetail' })
const loading = ref(true)
const { query } = useRoute()
//定义edit事件
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

const setBtn = () => {
  if (
    loanApp.value?.paymentStatus === 1 &&
    loanApp.value?.auditStatus === 2 &&
    loanApp.value.outstandingAmount?.amount > 0 &&
    showProcessInstanceTaskListFlag.value
  ) {
    updateDialogActions(
      <el-button
        onClick={() => {
          handleToRepay()
        }}
        key="payment"
      >
        去还款
      </el-button>
    )
  }
  if (checkPermi(['oa:loan-app:print'])) {
    updateDialogActions(
      <el-button
        onClick={() => {
          handlePrint()
        }}
        key="print"
      >
        打印
      </el-button>
    )
  }
}

const handlePrint = async () => {
  const url = await LoanAppApi.print({
    id: props?.id,
    reportCode: 'oa_loan_app'
  })
  openPdf(url)
}

const getLoanApp = async () => {
  loading.value = true
  try {
    loanApp.value = props.id
      ? await LoanAppApi.getLoanApp({ id: props.id })
      : await LoanAppApi.getAuditLoanApp({ id: query?.id })
    loanApp.value.amountRMB = (
      Number(loanApp.value.amount?.amount) * Number(loanApp.value.exchangeRate)
    ).toFixed(2)
    setBtn()
  } finally {
    loading.value = false
  }
}

const repayDialogRef = ref()
const handleToRepay = () => {
  repayDialogRef.value?.openCreate('还款单')
}
const emit = defineEmits(['success'])
const handleRefresh = () => {
  emit('success')
}

/**
 * 生成明细信息
 * @param r
 */
const basicInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'loanDate',
    label: '申请借款时间',
    type: 'time'
  },
  {
    field: 'code',
    label: '借款编号'
  },
  {
    field: 'loanType',
    label: '借款方式',
    dictType: DICT_TYPE.LOAN_TYPE
  },
  {
    field: 'companyName',
    label: '公司主体'
  },
  {
    slotName: 'applyerName',
    field: 'applyerName',
    label: '借款人'
  },
  {
    field: 'amount',
    label: '借款金额',
    type: 'money'
  },
  {
    field: 'outstandingAmount',
    label: '待还金额',
    type: 'money'
  },
  {
    field: 'purpose',
    label: '借款事由',
    span: 24
  },
  {
    slotName: 'applyerCurrency',
    field: 'amount',
    label: '借款币种'
  },
  {
    field: 'exchangeRate',
    label: '汇率'
  },
  {
    field: 'amountRMB',
    label: '借款金额(RMB)'
  }
]

const transferInfo = [
  {
    label: '开户姓名',
    field: 'bankPoc'
  },
  {
    label: '开户银行',
    field: 'bank'
  },
  {
    slotName: 'bankAccount',
    label: '开户账号',
    field: 'bankAccount'
  },
  {
    label: '支付状态',
    field: 'paymentStatus',
    dictType: DICT_TYPE.PAYMENT_STATUS
  },
  {
    label: '支付公司',
    field: 'companyName'
  },
  {
    label: '支付金额',
    field: 'paymentAmount',
    type: 'money'
  },
  {
    label: '打款时间',
    field: 'paymentDate',
    type: 'date'
  }
]

const repaymentInfo = [
  {
    label: '还款人',
    field: ''
  },
  {
    label: '还款时间',
    field: 'repayDate',
    type: 'time'
  },
  {
    label: '开户金额',
    field: 'repayAmount'
  },
  {
    label: '还款编码',
    field: ''
  }
]

//还款信息
const repayListSchemas = [
  {
    field: 'repayList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px',
    rules: {
      required: true
    }
  }
]
const repayColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'repaySourceType',
    label: '还款类型',
    formatter: formatDictColumn(DICT_TYPE.REPAY_SOURCE_TYPE)
  },
  {
    field: 'repaySourceCode',
    label: '还款单号'
  },
  {
    field: 'repayTime',
    label: '还款时间',
    formatter: formatDateColumn()
  },
  {
    field: 'reimbUser',
    label: '还款人(部门)',
    slots: {
      default: (data) => {
        const { row } = data
        return row.reimbUser ? `${row.reimbUser.nickname}(${row.reimbUser.deptName})` : '-'
      }
    }
  },
  {
    field: 'repayStatus',
    label: '还款状态',
    formatter: formatDictColumn(DICT_TYPE.REPAY_STATUS)
  },
  {
    field: 'repayAmount',
    label: '还款金额',
    formatter: formatMoneyColumn()
  }
])
const handleUpdate = async () => {
  await getLoanApp()
}

onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('借款申请单ID不能为空')
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
  await getLoanApp()
})
</script>
<style lang="scss" scoped></style>
