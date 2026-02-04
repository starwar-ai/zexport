<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="EntertainApi.approveEntertain"
    :rejectApi="EntertainApi.rejectEntertain"
    :auditable="auditInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :approve="{
      permi: 'oa:entertain-reimb:audit',
      handler: () => {}
    }"
    :cancel="{
      permi: 'oa:entertain-reimb:audit',
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
          <EplusMoneyLabel :val="pageInfo.amountList || {}" />
        </div>
      </el-card>
      <el-card
        class="mr20px"
        style="width: 180px; text-align: center"
      >
        <div class="mb10px font-600">发票总金额</div>
        <div style="color: #409eff">
          <EplusMoneyLabel :val="pageInfo.invoiceAmountList || {}" />
        </div>
      </el-card>
      <el-card
        class="mr20px"
        style="width: 180px; text-align: center"
      >
        <div class="mb10px font-600">本次还款金额</div>
        <div style="color: #409eff">
          <EplusMoneyLabel :val="pageInfo.repayAmountList || {}" />
        </div>
      </el-card>
      <el-card
        class="mr20px"
        style="width: 180px; text-align: center"
      >
        <div class="mb10px font-600">本次报销金额</div>
        <div style="color: #409eff">
          <EplusMoneyLabel :val="pageInfo.totalAmountList" />
        </div>
      </el-card>
    </div>

    <DocInfo :pageInfo="pageInfo" />

    <eplus-description
      title="报销说明"
      :data="pageInfo"
      :items="basicInfo"
    >
      <template #reimbUserName>
        {{ pageInfo.reimbUser.nickname }} ({{ pageInfo.reimbUser.deptName || '-' }})
      </template>
      <template #actualUser>
        {{ pageInfo.actualUser.nickname }} ({{ pageInfo.actualUser.deptName || '-' }})
      </template>

      <template #entertainEntourage>
        <span
          class="mr5px"
          v-for="item in pageInfo.entertainEntourage"
          :key="item.userId"
        >
          {{ item.nickname }}
        </span>
      </template>
      <template #expenseUseToFormal>
        <el-input v-model="pageInfo.expenseUseToFormal" />
      </template>
      <template #bankAccount>
        {{ maskBankAccount(pageInfo?.bankAccount) }}
      </template>
      <template #applyList>
        <Table
          :columns="expenseAppColumns"
          headerAlign="center"
          align="center"
          :data="pageInfo?.applyList || []"
        />
      </template>
    </eplus-description>
    <eplus-description
      title="费用小项"
      :data="pageInfo"
      :items="expenseInfo"
    >
      <template #ExpenseList>
        <Table
          :columns="expenseListColumns"
          headerAlign="center"
          align="center"
          :data="pageInfo?.reimbDetailList"
        />
      </template>
      <template #loanAppList>
        <LoanAppListDetail :list="pageInfo?.loanAppList" />
      </template>
    </eplus-description>

    <FeeShareDetail
      v-if="pageInfo.auxiliaryType === 1 && isValidArray(pageInfo.feeShare)"
      :info="pageInfo.feeShare"
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
import { EplusDetail } from '@/components/EplusTemplate'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import * as EntertainApi from '@/api/oa/entertain'
import UploadList from '@/components/UploadList/index.vue'
import FeeShareDetail from '@/views/ems/send/components/FeeShare/FeeShareDetail.vue'
import { isValidArray } from '@/utils/is'
import DocInfo from '../../components/DocInfo.vue'
import LoanAppListDetail from '../../components/LoanAppListDetail.vue'
import { EplusMoneyLabel } from '@/components/EplusMoney'

const pageInfo: any = ref({}) // 借款申请单详情
const eplusDetailRef = ref()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
  param?: any
  status?: any
  auditInfo: any
}>()
defineOptions({ name: 'EntertainmentExpensesDetail' })
//定义edit事件

const loading = ref(true)

const getPageInfo = async () => {
  loading.value = true
  try {
    pageInfo.value = await EntertainApi.getEntertainInfo({ id: props.id })
    pageInfo.value.processInstanceId = props?.param
      ? props?.param
      : pageInfo.value.processInstanceId
    pageInfo.value.auditStatus = props?.status ? props?.status : ''
  } finally {
    loading.value = false
  }
}
const basicInfo: EplusDescriptionItemSchema[] = reactive([
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
    slotName: 'actualUser',
    field: 'actualUser',
    label: '实际报销人'
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
  // {
  //   field: 'expenseUseToOccur',
  //   label: '费用实际用途说明'
  // },
  {
    field: 'remark',
    label: '备注'
  },
  {
    slotName: 'reimbUserName',
    field: 'reimbUserName',
    label: '录入人'
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

const expenseInfo: EplusDescriptionItemSchema[] = [
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
]

const expenseListColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'expenseType',
    label: '类型',
    formatter: formatDictColumn(DICT_TYPE.ENTERTAIN_EXPENSE_TYPE)
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

const expenseAppColumns = reactive([
  {
    field: 'code',
    label: '申请单号',
    minWidth: columnWidth.l
  },
  {
    field: 'entertainTime',
    label: '招待日期',
    minWidth: columnWidth.l,
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    field: 'entertainType',
    label: '招待对象类型',
    minWidth: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.ENTWETAIN_TYPE)
  },
  {
    field: 'entertainName',
    label: '招待对象名称',
    minWidth: columnWidth.l
  },
  {
    field: 'entertainNum',
    label: '招待人数',
    minWidth: columnWidth.l
  },
  {
    field: 'entertainLevel',
    label: '招待对象等级',
    minWidth: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.LEVEL_TYPE)
  },
  {
    field: 'entertainEntourage',
    label: '陪同人员',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return row.entertainEntourage.map((item) => item.nickname).join(',')
    }
  },
  {
    field: 'purpose',
    label: '招待事由',
    minWidth: columnWidth.l
  },
  {
    field: 'amount',
    label: '预估费用',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
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
  await getPageInfo()
})
</script>
<style lang="scss" scoped>
.total_box {
  display: flex;
  justify-content: space-between;
  height: 40px;
  align-items: center;
}
</style>
