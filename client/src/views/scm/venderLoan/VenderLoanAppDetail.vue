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
      permi: 'scm:vender-loan:submit',
      handler: () => {}
    }"
    :cancel="{
      permi: 'scm:vender-loan:cancel'
    }"
    :edit="{
      user: loanApp.applyer.userId,
      permi: 'scm:vender-loan:update',
      handler: () => goEdit('借款单')
    }"
    :approve="{
      permi: 'scm:vender-loan:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <!-- <DocInfo :pageInfo="loanApp" /> -->
    <eplus-description
      title="借款信息"
      :data="loanApp"
      :items="basicInfo"
    >
      <template #applyerName>
        {{ loanApp?.applyer?.nickname }}
      </template>
      <template #amount> {{ loanApp?.amount.amount }} {{ loanApp?.amount.currency }} </template>
    </eplus-description>

    <eplus-description
      v-if="'转账' === getDictLabel(DICT_TYPE.LOAN_TYPE, loanApp.loanType)"
      title="转账信息"
      :data="loanApp"
      :items="transferInfo"
    >
      <template #paymentAmount>
        {{ loanApp?.paymentAmount?.amount }} {{ loanApp?.paymentAmount?.currency }}
      </template>
    </eplus-description>
    <eplus-form-items
      title="还款信息"
      :formSchemas="repayListSchemas"
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

  <!-- <eplus-dialog
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
  </eplus-dialog> -->
</template>
<script setup lang="tsx">
import { examineApprove, examineReject } from '@/api/audit/loan-app'
import * as LoanAppApi from '@/api/scm/venderLoan'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { formatDateColumn, formatDictColumn } from '@/utils/table'
// import DocInfo from '../components/DocInfo.vue'
// import RepayAppForm from '../repayapp/RepayAppForm.vue'

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
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const { updateDialogActions, clearDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
  clearDialogActions: () => void
}

const loading = ref(true)
const { query } = useRoute()

const getLoanApp = async () => {
  loading.value = true
  try {
    loanApp.value = props.id
      ? await LoanAppApi.getVenderLoan({ id: props.id })
      : await LoanAppApi.getAuditVenderLoan({ id: query?.id })

    if (loanApp.value?.paymentStatus === 1 && loanApp.value?.auditStatus === 2) {
      updateDialogActions(
        <el-button
          onClick={() => {
            handleToRepay()
          }}
          type="primary"
          key="payment"
        >
          去还款
        </el-button>
      )
    }
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
    field: 'code',
    label: '借款编号'
  },
  {
    field: 'companyName',
    label: '公司主体'
  },
  {
    field: 'loanSource',
    label: '借款类型',
    dictType: DICT_TYPE.VENDER_LOAN_TYPE
  },
  {
    field: 'venderName',
    label: '供应商'
  },
  {
    field: 'loanType',
    label: '借款方式',
    dictType: DICT_TYPE.LOAN_TYPE
  },
  {
    field: 'amount',
    label: '欠款金额',
    slotName: 'amount'
  },
  {
    field: 'deadlineTime',
    label: '欠款截止日期',
    type: 'date'
  },
  {
    slotName: 'applyerName',
    field: 'applyerName',
    label: '申请人'
  },
  {
    field: 'purpose',
    label: '借款事由',
    span: 24
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
    field: ''
  },
  {
    slotName: 'paymentAmount',
    label: '支付金额',
    field: 'paymentAmount'
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
    field: 'code',
    label: '关联单号'
  },
  {
    field: 'repayType',
    label: '还款方式',
    formatter: formatDictColumn(DICT_TYPE.LOAN_REPAY_TYPE)
  },
  {
    field: 'repayTime',
    label: '还款时间',
    formatter: formatDateColumn()
  },
  {
    field: 'repayAmount',
    label: '还款金额',
    slots: {
      default: (data) => {
        const { row } = data
        return row.repayAmount ? `${row.repayAmount.amount} ${row.repayAmount.currency}` : '-'
      }
    }
  },
  {
    field: 'venderName',
    label: '还款供应商'
  },
  {
    field: 'repayStatus',
    label: '状态',
    formatter: formatDictColumn(DICT_TYPE.REPAY_STATUS)
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
