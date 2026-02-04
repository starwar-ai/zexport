<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineApprove"
    :rejectApi="examineReject"
    :auditable="auditInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :approve="{
      permi: 'oa:general-reimb:audit',
      handler: () => {}
    }"
    :cancel="{
      permi: 'oa:other-reimb:cancel',
      handler: () => {}
    }"
  >
    <div class="mb10px flex">
      <el-card
        class="mr20px"
        style="width: 180px; text-align: center"
      >
        <div class="mb10px font-600">报销总金额</div>
        <div style="color: #409eff">
          <EplusMoneyLabel :val="generalReimbInfo.amountList || {}" />
        </div>
      </el-card>

      <el-card
        class="mr20px"
        style="width: 180px; text-align: center"
      >
        <div class="mb10px font-600">发票总金额</div>
        <div style="color: #409eff">
          <EplusMoneyLabel :val="generalReimbInfo.invoiceAmountList || {}" />
        </div>
      </el-card>
      <el-card
        class="mr20px"
        style="width: 180px; text-align: center"
      >
        <div class="mb10px font-600">本次还款金额</div>
        <div style="color: #409eff">
          <EplusMoneyLabel :val="generalReimbInfo.repayAmountList || {}" />
        </div>
      </el-card>
      <el-card
        class="mr20px"
        style="width: 180px; text-align: center"
      >
        <div class="mb10px font-600">本次报销金额</div>
        <div style="color: #409eff">
          <EplusMoneyLabel :val="generalReimbInfo.totalAmountList" />
        </div>
      </el-card>
    </div>

    <DocInfo :pageInfo="generalReimbInfo" />
    <eplus-description
      title="报销说明"
      :data="generalReimbInfo"
      :items="explainSchemas"
    >
      <template #reimbUser>
        <span>{{ generalReimbInfo?.reimbUser?.nickname }}</span>
        <span>{{ '(' + (generalReimbInfo?.reimbUser?.deptName || '-') + ')' }}</span>
      </template>
      <template #actualUser>
        <span>{{ generalReimbInfo?.actualUser?.nickname }}</span>
        <span>{{ '(' + (generalReimbInfo?.actualUser?.deptName || '-') + ')' }}</span>
      </template>
      <template #expenseUseToFormal>
        <el-input v-model="generalReimbInfo.expenseUseToFormal" />
      </template>
      <template #bankAccount>
        {{ maskBankAccount(generalReimbInfo?.bankAccount) }}
      </template>

      <template #applyList>
        <Table
          :columns="applyColumns"
          headerAlign="center"
          align="center"
          :data="generalReimbInfo?.applyList"
        />
      </template>
    </eplus-description>
    <eplus-description
      title="费用小项"
      :data="generalReimbInfo"
      :items="expenseInfo"
    >
      <template #ExpenseList>
        <Table
          :columns="expenseListColumns"
          headerAlign="center"
          align="center"
          :data="generalReimbInfo?.reimbDetailList"
        />
      </template>
      <template #loanAppList>
        <Table
          :columns="loanAppColumns"
          headerAlign="center"
          align="center"
          :data="generalReimbInfo?.loanAppList"
        />
      </template>
    </eplus-description>

    <FeeShareDetail
      v-if="generalReimbInfo.auxiliaryType === 1 && isValidArray(generalReimbInfo.feeShare)"
      :info="generalReimbInfo.feeShare"
    />
    <div
      v-else
      class="mb10px text-red"
      >该报销单未进行费用归集
    </div>
  </eplus-detail>
</template>
<script setup lang="tsx">
import { maskBankAccount } from '@/utils/index'
import { examineApprove, examineReject } from '@/api/audit/travel-reimb'
import * as OtherApi from '@/api/oa/otherExpense'
import { isValidArray } from '@/utils/is'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import { columnWidth, formatMoneyColumn } from '@/utils/table'
import FeeShareDetail from '@/views/ems/send/components/FeeShare/FeeShareDetail.vue'
import UploadList from '@/components/UploadList/index.vue'
import DocInfo from '../../components/DocInfo.vue'

const generalReimbInfo = ref<any>({})
const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  pageInfo?: object
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
  param?: any
  status?: any
  auditInfo: any
}>()

defineOptions({ name: 'GeneralReimbDetail' })
//定义edit事件

const loading = ref(true)

const getInfo = async () => {
  loading.value = true
  try {
    generalReimbInfo.value = await OtherApi.getOtherReimb(props.id)
    generalReimbInfo.value.processInstanceId = props?.param
      ? props?.param
      : generalReimbInfo.value.processInstanceId
    generalReimbInfo.value.auditStatus = props?.status ? props?.status : ''
  } finally {
    loading.value = false
  }
}
/**
 * 生成明细信息
 * @param r
 */

let explainSchemas: EplusDescriptionItemSchema[] = reactive([
  {
    field: 'code',
    label: '报销单号'
  },
  {
    field: 'companyName',
    label: '费用主体'
  },
  {
    field: 'currency',
    label: '报销币种'
  },
  {
    field: 'actualUser',
    label: '实际报销人',
    slotName: 'actualUser'
  },
  {
    field: 'bankPoc',
    label: '账户名称'
  },
  {
    field: 'bank',
    label: '开户银行'
  },
  {
    slotName: 'bankAccount',
    field: 'bankAccount',
    label: '账户号码'
  },
  {
    field: 'description',
    label: '备注'
  },
  {
    field: 'reimbUser',
    label: '录入人',
    slotName: 'reimbUser'
  },
  {
    field: 'createTime',
    label: '申请日期',
    type: 'date'
  },
  {
    field: 'printDate',
    label: '打印日期',
    type: 'date'
  },
  {
    slotName: 'applyList',
    field: 'applyList',
    label: '申请单',
    span: 24
  }
])

const applyColumns = reactive([
  {
    field: 'code',
    label: '申请单号',
    minWidth: columnWidth.m
  },
  {
    field: 'applyer',
    label: '费用申请人',
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return <div>{row.applyer?.nickname ? row.applyer.nickname : ''}</div>
      }
    }
  },
  {
    field: 'amount',
    label: '预估费用金额',
    minWidth: columnWidth.m,
    formatter: formatMoneyColumn()
  },
  {
    field: 'amountDesc',
    minWidth: columnWidth.m,
    label: '明细说明'
  },
  {
    field: 'purpose',
    label: '申请事由',
    minWidth: columnWidth.m,
    isTooltip: true
  }
])

const expenseInfo: EplusDescriptionItemSchema[] = reactive([
  {
    slotName: 'ExpenseList',
    field: 'ExpenseList',
    label: '',
    span: 24,
    disabled: false
  },
  {
    field: 'repayFlag',
    label: '是否还款',
    span: 24,
    dictType: DICT_TYPE.IS_INT
  },
  {
    slotName: 'loanAppList',
    field: 'loanAppList',
    label: '借款单',
    span: 24,
    disabled: true
  }
])

const expenseListColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    label: '申请事由',
    field: 'expenseUseToOccur'
  },
  {
    field: 'feeName',
    label: '发票名称'
  },
  // {
  //   field: 'expenseUseToFormal',
  //   label: '费用名称(实际)'
  // },
  {
    field: 'invoiceAmount',
    label: '发票金额',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusMoneyLabel
            val={{
              amount: row.invoiceAmount,
              currency: row.expenseCurrency
            }}
          />
        )
      }
    }
  },
  {
    field: 'expenseAmount',
    label: '报销金额',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusMoneyLabel
            val={{
              amount: row.expenseAmount,
              currency: row.expenseCurrency
            }}
          />
        )
      }
    }
  },
  {
    field: 'invoice',
    label: '发票附件',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <UploadList
            fileList={row.invoice}
            disabled
          />
        )
      }
    }
  },
  {
    field: 'picture',
    label: '支付截图',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <UploadList
            fileList={row.picture}
            disabled
          />
        )
      }
    }
  },
  {
    field: 'remark',
    label: '备注'
  }
])

const loanAppColumns = reactive([
  {
    field: 'code',
    label: '借款单号'
  },
  {
    field: 'companyName',
    label: '公司主体'
  },
  {
    field: 'amount',
    label: '借款金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'outstandingAmount',
    label: '当前待还金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'inRepaymentAmount',
    label: '还款中金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'purpose',
    label: '借款事由'
  }
])

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
<style lang="scss" scoped>
.total_box {
  display: flex;
  justify-content: space-between;
  height: 40px;
  align-items: center;
}

.totalTitle {
  font-weight: 600;
  height: 47px;
  display: flex;
  align-items: center;
  span {
    font-weight: 500;
  }
}
</style>
