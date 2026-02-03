<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="PaymentApi.examineApprove"
    :rejectApi="PaymentApi.examineReject"
    :auditable="paymentApplyDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :submit="{
      permi: 'scm:payment-apply:submit',
      handler: () => goEdit('付款申请')
    }"
    :cancel="{
      permi: 'scm:payment-apply:submit',
      handler: () => {},
      user: paymentApplyDetail?.creator
    }"
    :edit="{
      permi: 'scm:payment-apply:update',
      handler: () => goEdit('付款申请'),
      user: paymentApplyDetail?.creator
    }"
    :approve="{
      permi: 'scm:payment-apply:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="基本信息"
      :data="paymentApplyDetail"
      :items="basicInfoSchemas"
    >
      <template #applyer>
        {{ paymentApplyDetail?.applyer?.nickname ? paymentApplyDetail.applyer.nickname : '-' }}
      </template>
      <template #applyerDept>
        {{ paymentApplyDetail?.applyer?.deptName ? paymentApplyDetail.applyer.deptName : '-' }}
      </template>
      <template #purchaseContractCodeList>
        {{
          [...new Set(paymentApplyDetail?.purchaseContractCodeList.map((item) => item))].join(',')
        }}
      </template>
      <template #salesContractCodeList>
        {{
          [
            ...new Set(
              paymentApplyDetail?.applyerPurchaseItemList.map((item) => item.saleContractCode)
            )
          ].join(',')
        }}
      </template>
      <template #applyTotalAmount>
        <EplusMoneyLabel
          :val="{
            amount: paymentApplyDetail.applyTotalAmount,
            currency: paymentApplyDetail.currency
          }"
        />
      </template>
      <template #goodsTotalAmount>
        <EplusMoneyLabel
          :val="{
            amount: paymentApplyDetail.goodsTotalAmount,
            currency: paymentApplyDetail.currency
          }"
        />
      </template>
    </eplus-description>
    <eplus-description
      title="应付供应商信息"
      :data="paymentApplyDetail"
      :items="payableVenderSchemas"
    />
    <eplus-description
      title="付款信息"
      :data="paymentApplyDetail"
      :items="paymentInfoSchemas"
    >
      <template #realPaymentAmount>
        <span v-if="paymentApplyDetail.realPaymentAmount == 0">-</span>
        <EplusMoneyLabel
          v-else
          :val="{
            amount: paymentApplyDetail.realPaymentAmount,
            currency: paymentApplyDetail.currency
          }"
        />
      </template>
      <template #paymentUser>
        {{
          paymentApplyDetail.paymentUser?.nickname ? paymentApplyDetail.paymentUser.nickname : '-'
        }}
      </template>
      <template #annex>
        <UploadList
          :fileList="paymentApplyDetail.annex"
          disabled
        />
      </template>
    </eplus-description>
    <eplus-description
      title="付款计划信息"
      :data="paymentApplyDetail"
      :items="paymentPlanSchemas"
    >
      <template #applyPaymentPlanList>
        <Table
          :columns="applyPaymentPlanTableColumns"
          headerAlign="center"
          align="center"
          :data="paymentApplyDetail.applyPaymentPlanList"
        />
      </template>
    </eplus-description>
    <el-tabs
      v-model="activeName"
      class="demo-tabs mb20px"
      @tab-change="handleTabChange"
    >
      <el-tab-pane
        label="采购明细"
        name="first"
      >
        <eplus-form-items
          title=""
          :formSchemas="purchaseItemSchemas"
        >
          <template #applyerPurchaseItemList>
            <PaymentPurchaseItem
              v-if="paymentApplyDetail?.applyerPurchaseItemList"
              :formData="paymentApplyDetail"
              ref="childrenRef"
              type="detail"
            />
          </template>
        </eplus-form-items>
      </el-tab-pane>
      <el-tab-pane
        label="加减项"
        name="second"
      >
        <Table
          :columns="addSubTermColumns"
          headerAlign="center"
          align="center"
          :data="paymentApplyDetail?.purchaseAddSubTermList"
        />
      </el-tab-pane>
      <el-tab-pane
        label="关联单据"
        name="three"
      >
        <RelateTable :data="paymentApplyDetail" />
      </el-tab-pane>
    </el-tabs>
    <eplus-description
      v-if="paymentApplyDetail?.cancelReason"
      title="作废信息"
      :data="paymentApplyDetail"
      :items="cancelSchemas"
    />
  </eplus-detail>

  <PaymentPlanListDia ref="PaymentPlanListDiaRef" />
</template>
<script setup lang="tsx">
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import * as PaymentApi from '@/api/finance/payment'
import * as VenderApi from '@/api/scm/vender'
import PaymentPurchaseItem from './PaymentPurchaseItem.vue'
import { formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import { openPdf } from '@/utils'
import PaymentPlanListDia from '@/views/scm/purchase/contract/PaymentPlanListDia.vue'

const message = useMessage()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const loading = ref(true)
const activeName = ref('first')
const { query } = useRoute()
const handleTabChange = (val) => {}
const props = defineProps<{
  type: string
  title?: string
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
const { goEdit } = props.id
  ? (inject('dialogEmits') as {
      goEdit: (val) => void
    })
  : { goEdit: () => {} }
const getRegistrationDetail = () => {}
const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }

const handleUpdate = async () => {
  await getRegistrationDetail()
}

let addSubTermColumns = reactive([
  {
    field: 'contractCode',
    label: '采购合同'
  },
  {
    field: 'calculationType',
    label: '加/减项',
    formatter: formatDictColumn(DICT_TYPE.CALCULATION_TYPE)
  },
  {
    field: 'feeName',
    label: '费用名称'
  },
  {
    field: 'amount',
    label: '金额',
    formatter: formatMoneyColumn()
  }
])

let paymentApplyDetail = reactive({
  id: 0,
  code: '',
  annex: [],
  children: [],
  contractList: [],
  operateLogRespDTOList: [],
  creator: ''
})

const basicInfoSchemas = reactive([
  {
    field: 'code',
    label: '申请单编号'
  },
  {
    field: 'companyName',
    label: '付款单位'
  },
  {
    field: 'applyer',
    label: '申请人',
    slotName: 'applyer'
  },
  {
    field: 'applyerDept',
    label: '申请部门',
    slotName: 'applyerDept'
  },
  {
    field: 'applyDate',
    label: '申请日期',
    type: 'date'
  },
  {
    slotName: 'applyTotalAmount',
    field: 'applyTotalAmount',
    label: '申请总金额'
  },
  {
    field: 'subAddTotalAmount',
    label: '加减项总金额',
    type: 'money'
  },
  {
    slotName: 'goodsTotalAmount',
    field: 'goodsTotalAmount',
    label: '货款金额'
  },
  {
    field: 'applyPaymentDate',
    label: '申请付款日期',
    type: 'date'
  },
  {
    field: 'purchaseContractCodeList',
    label: '采购合同号',
    slotName: 'purchaseContractCodeList'
  },
  {
    slotName: 'salesContractCodeList',
    field: 'salesContractCodeList',
    label: '关联销售合同号'
  },
  {
    field: 'printFlag',
    label: '打印状态',
    dictType: DICT_TYPE.PRINT_FLAG
  },
  {
    field: 'paymentMethod',
    label: '付款方式',
    dictType: DICT_TYPE.PAY_METHOD
  },
  {
    field: 'paymentMarkType',
    label: '付款类型',
    dictType: DICT_TYPE.PAYMENT_MARK_TYPE
  },
  {
    field: 'acceptanceDays',
    label: '承兑天数',
    dictType: DICT_TYPE.ACCEPTANCE_DAYS
  },
  {
    field: 'step',
    label: '申请类型',
    dictType: DICT_TYPE.PAYMENT_TYPE
  },
  {
    field: 'remark',
    label: '申请备注'
  }
])
const payableVenderSchemas = [
  {
    field: 'venderName',
    label: '供应商名称'
  },
  {
    field: 'venderCode',
    label: '供应商编码'
  },
  {
    field: 'currency',
    label: '币别'
  },
  {
    field: 'taxRate',
    label: '税率(%)',
    type: 'num'
  },
  {
    field: 'venderBank',
    label: '开户行'
  },
  {
    field: 'venderBankAccount',
    label: '银行账户'
  }
]
const paymentInfoSchemas = [
  {
    field: 'bank',
    label: '付款银行'
  },
  {
    field: 'bankAccount',
    label: '付款账号'
  },
  {
    slotName: 'realPaymentAmount',
    field: 'realPaymentAmount',
    label: '付款金额'
  },
  {
    field: 'paymentDate',
    label: '付款日期',
    type: 'date'
  },
  {
    slotName: 'paymentUser',
    field: 'paymentUser',
    label: '付款人'
  },
  {
    slotName: 'annex',
    field: 'annex',
    label: '附件',
    span: 24
  }
]

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

const paymentPlanSchemas = [
  {
    slotName: 'applyPaymentPlanList',
    field: 'applyPaymentPlanList',
    label: '',
    labelWidth: '0px',
    span: 24
  }
]

const PaymentPlanListDiaRef = ref()
let applyPaymentPlanTableColumns = reactive([
  {
    field: 'contractCode',
    label: '采购合同号'
  },
  {
    field: 'paymentName',
    label: '付款方式',
    minWidth: '150px'
  },
  {
    field: 'step',
    label: '步骤',
    formatter: formatDictColumn(DICT_TYPE.PAYMENT_PLAN_STEP)
  },
  {
    field: 'paymentRatio',
    label: '计划付款比例(%)'
  },
  {
    field: 'receivableAmount',
    label: '应付金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'expectedReceiptDate',
    label: '计划付款日期',
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    field: 'appliedAmount',
    label: '货款已申请金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'action',
    label: '操作',
    width: '100px',
    fixed: 'right',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <el-button
            link
            type="primary"
            onClick={async () => {
              PaymentPlanListDiaRef.value.open(row.contractCode)
            }}
          >
            付款计划
          </el-button>
        )
      }
    }
  }
])
const purchaseItemSchemas = [
  {
    field: 'applyerPurchaseItemList',
    label: '',
    labelWidth: '0px',
    editable: true,
    span: 24
  }
]
const getInfo = async () => {
  loading.value = true
  try {
    paymentApplyDetail = props.id
      ? await PaymentApi.getPaymentApply(props?.id)
      : await PaymentApi.getAuditPaymentApply(query?.id)
  } finally {
    loading.value = false
  }
}

// 反提交
const handleReverseSubmit = async () => {
  close()
  let res = await VenderApi.reverseSubmit(paymentApplyDetail.processInstanceId)
  if (res) {
    message.success('反提交成功')
    close()
  }
}

// 反审核
const handleReverseAudit = async () => {
  let res = await VenderApi.reversePaymentAudit(paymentApplyDetail.id)
  if (res) {
    message.success('反审核成功')
    close()
  }
}

const handlePrint = async () => {
  const url = await PaymentApi.print({
    id: props?.id,
    reportCode: 'scm_payment_apply_horizontal'
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
  if (query?.id) {
    showProcessInstanceTaskListFlag.value = false
    outDialogFlag.value = true
  }
  if (props.id) {
    showProcessInstanceTaskListFlag.value = true
    outDialogFlag.value = false
  }
  await getInfo()

  if (paymentApplyDetail.auditStatus === 2) {
    clearDialogActions()
    updateDialogActions(
      <el-button
        onClick={() => {
          handlePrint()
        }}
        key="print"
        hasPermi="scm:payment-apply:print"
      >
        打印
      </el-button>
    )
  } else {
    updateDialogActions(
      <el-button
        onClick={() => {
          handlePrint()
        }}
        key="print"
        hasPermi="scm:payment-apply:print"
      >
        打印
      </el-button>
    )
  }
})
</script>
<style scoped lang="scss"></style>
