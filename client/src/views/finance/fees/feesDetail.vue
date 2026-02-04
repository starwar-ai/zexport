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
      title="付款说明"
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
        <div
          type="primary"
          :key="feesDetail?.applyer?.userId"
        >
          <span>{{
            feesDetail?.applyer
              ? feesDetail?.applyer?.nickname + '(' + feesDetail?.applyer?.deptName + ')'
              : '-'
          }}</span>
        </div>
      </template>
    </eplus-description>
    <eplus-description
      title="支付信息"
      :data="feesDetail"
      :items="PaymentInfoSchemas"
    >
      <template #cashier>
        <span>
          {{
            feesDetail.cashier?.nickname
              ? feesDetail.cashier?.nickname + '(' + feesDetail.cashier?.deptName + ')'
              : '-'
          }}
        </span>
      </template>
      <template #pocList>
        <Table
          :columns="pocColumns"
          headerAlign="center"
          align="center"
          :data="feesDetail?.pocList"
        />
      </template>
    </eplus-description>
    <eplus-description
      v-if="feesDetail?.cancelReason"
      title="作废信息"
      :data="feesDetail"
      :items="cancelSchemas"
    />
    <el-tabs v-model="activeName">
      <el-tab-pane
        label="关联单据"
        name="1"
      >
        <RelateTable :data="feesDetail" />
      </el-tab-pane>
    </el-tabs>
    <PayDialog
      ref="payDialogRef"
      @success="handleUpdate"
    />
  </eplus-detail>
</template>
<script setup lang="tsx">
import { examineApprove, examineReject } from '@/api/audit/payment'
import * as FeesApi from '@/api/finance/fees'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDialog } from '@/components/EplusDialog'
import PayDialog from './components/PayDialog.vue'

const payDialogRef = ref()
const openPayDialog = (id) => {
  payDialogRef.value.open(id)
}
const emit = defineEmits(['success'])
const message = useMessage()
const feesDetail = ref() // 借款申请单详情
const activeName = ref('1')
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

const cancelSchemas = [
  {
    field: 'cancelUser',
    label: '操作人',
    formatter: (val) => {
      return val?.nickname ? val.nickname : '-'
    }
  },
  {
    field: 'cancelTime',
    label: '作废时间',
    type: 'date'
  },
  {
    field: 'cancelReason',
    label: '作废原因'
  }
]

const getFmsPaymentDetail = async () => {
  loading.value = true
  try {
    feesDetail.value = props.id
      ? await FeesApi.getFees({ id: props.id }).then((res) => {
          // if (res && outDialogFlag.value === false) {
          //   paymentBtnShow(res)
          // }
          return res
        })
      : await FeesApi.getAuditFees({ id: query?.id })
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
//付款说明
const PaymentInstructionSchemas = [
  {
    field: 'code',
    label: '付款单号'
    // dictType: DICT_TYPE.CURRENCY_TYPE
  },
  {
    field: 'companyName',
    label: '付款主体'
  },
  {
    field: 'businessSubjectType',
    label: '业务类型',
    dictType: DICT_TYPE.BUSINESS_SUBJECT_TYPE
  },
  {
    field: 'businessType',
    label: '来源类型',
    dictType: DICT_TYPE.BUSINESS_TYPE
  },
  {
    field: 'businessCode',
    label: '来源编号'
  },
  {
    field: 'applyAmount',
    label: '申请付款金额',
    type: 'money'
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
    label: '付款事由',
    xl: 8,
    lg: 12
  }
]
//支付信息
let PaymentInfoSchemas = [
  {
    field: 'businessSubjectName',
    label: '支付对象'
  },
  // {
  //   field: 'paymentMethod',
  //   label: '支付类型',
  //   dictType: DICT_TYPE.LOAN_TYPE
  // },
  {
    field: 'status',
    label: '支付状态',
    dictType: DICT_TYPE.PAYMENT_STATUS
  },
  {
    field: 'cashier',
    label: '出纳员',
    slotName: 'cashier'
  },
  {
    field: 'date',
    label: '支付时间',
    type: 'date'
  },
  {
    field: 'amount',
    label: '本次支付金额',
    type: 'money'
  },
  {
    field: 'paymentMethod',
    label: '支付方式',
    dictType: DICT_TYPE.PAY_METHOD
  },
  {
    field: 'acceptanceDays',
    label: '承兑天数',
    dictType: DICT_TYPE.ACCEPTANCE_DAYS
  },
  {
    field: 'bank',
    label: '对方账户'
  },
  {
    field: 'bankAccount',
    label: '对方账号'
  },
  {
    field: 'pocList',
    label: '账户信息',
    span: 24,
    disabled: true,
    labelWidth: '100px',
    slotName: 'pocList'
  }
]
//如果是现金支付，则不需要银行信息
if (feesDetail.value?.payMethod) {
  PaymentInfoSchemas = PaymentInfoSchemas.filter((item) => item.field !== 'pocList')
}
const handleUpdate = async () => {
  await getFmsPaymentDetail().then((res) => {
    const { bank, bankAccount, bankAddress, bankCode, bankPoc } = feesDetail.value
    feesDetail.value.pocList = [{ bank, bankAccount, bankAddress, bankCode, bankPoc }]
  })
}

onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('费用支付单ID不能为空')
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
  await getFmsPaymentDetail().then((res) => {
    const { bank, bankAccount, bankAddress, bankCode, bankPoc } = feesDetail.value
    feesDetail.value.pocList = [{ bank, bankAccount, bankAddress, bankCode, bankPoc }]
  })
})
// onUnmounted(() => {
//   clearDialogActions()
// })
</script>
<style lang="scss" scoped></style>
