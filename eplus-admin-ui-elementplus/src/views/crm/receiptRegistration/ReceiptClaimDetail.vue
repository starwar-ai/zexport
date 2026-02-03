<template>
  <eplus-detail
    scrollFlag
    v-if="!loading"
    ref="eplusDetailRef"
    :auditable="receiptClaimDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    @update="handleUpdate"
  >
    <template #pageTop>
      <eplus-description
        title="基本信息"
        :data="receiptClaimDetail"
        :items="basicSchemas"
      >
        <template #registeredBy>
          {{ receiptClaimDetail.registeredBy?.nickname }}
        </template>
        <template #financeAmountTotal>
          <EplusMoneyLabel
            :val="{
              amount: receiptClaimDetail?.custClaimItemList
                .filter((item) => item.type === 0)
                .reduce((pre, cur) => pre + cur.financeAmount.amount, 0),
              currency: receiptClaimDetail?.currency
            }"
          />
        </template>
      </eplus-description>

      <eplus-description
        title="收款对象信息"
        :data="receiptClaimDetail"
        :items="payeeSchemas"
      >
        <template #payeeEntityList>
          <Table
            :columns="payeeTableColumns"
            headerAlign="center"
            align="center"
            :data="receiptClaimDetail?.payeeEntityList"
          />
        </template>
      </eplus-description>
    </template>
    <template #pageBottomTabs>
      <el-tabs
        v-model="activeName"
        class="demo-tabs"
        @tab-click="handleClick"
      >
        <el-tab-pane
          label="客户认领明细"
          name="1"
        />
        <el-tab-pane
          label="其他收费明细"
          name="2"
        />
      </el-tabs>
    </template>
    <template #pageBottom>
      <el-scrollbar always>
        <div v-show="activeName === '1'">
          <Table
            :columns="custClaimTableColumns"
            headerAlign="center"
            align="center"
            :data="receiptClaimDetail?.custClaimItemList.filter((item) => item.type === 0)"
            :max-height="430"
          />
        </div>
        <div v-show="activeName === '2'">
          <Table
            :columns="otherFeeTableColumns"
            headerAlign="center"
            align="center"
            :data="receiptClaimDetail?.custClaimItemList.filter((item) => item.type === 1)"
            :max-height="430"
          />
        </div>
      </el-scrollbar>
    </template>
  </eplus-detail>
</template>
<script setup lang="tsx">
import { formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import * as CustClaimApi from '@/api/finance/custClaim'
import { EplusDialog } from '@/components/EplusDialog'
import DifferenceReasonInfo from './components/DifferenceReasonInfo.vue'

const activeName = ref('1')
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const { query } = useRoute()
const message = useMessage()
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
type ReceiptClaimDetail = {
  [key: string]: any
}
const receiptClaimDetail: ReceiptClaimDetail = ref({})
//采购计划信息
const basicSchemas = reactive([
  {
    field: 'companyTitle',
    label: '付款抬头'
  },
  {
    field: 'companyName',
    label: '入账单位'
  },
  {
    field: 'bank',
    label: '入账银行'
  },
  {
    field: 'currency',
    label: '入账币种'
  },
  {
    field: 'amount',
    label: '入账金额'
  },
  {
    field: 'unclaimedAmount',
    label: '待认领金额'
  },
  {
    slotName: 'registeredBy',
    field: 'registeredBy',
    label: '登记人'
  },
  {
    field: 'remark',
    label: '备注'
  },
  {
    slotName: 'financeAmountTotal',
    field: 'financeAmountTotal',
    label: '财务费用'
  }
])
const managerFormat = (val) => {
  if (val) {
    return val?.map((item) => item.nickname).join(',')
  } else {
    return ''
  }
}
let payeeTableColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'payeeCode',
    label: '编号'
  },
  {
    field: 'payeeName',
    label: '名称'
  },
  {
    field: 'manager',
    label: '负责员工',
    slots: {
      default: (scope) => {
        return <span>{managerFormat(scope.row?.manager) || ''}</span>
      }
    }
  },
  {
    field: 'claimTotalAmount',
    label: '认领总金额'
  },
  {
    field: 'currency',
    label: '币种'
  }
])
let payeeSchemas = reactive([
  {
    field: 'payeeEntityList',
    label: '',
    span: 24,
    labelWidth: '0px',
    slotName: 'payeeEntityList'
  }
])

//产品信息table
let custClaimTableColumns = reactive([
  {
    field: 'contractCode',
    label: '订单合同号',
    minWidth: '180px'
  },
  {
    field: 'custName',
    label: '客户名称',
    width: '180px'
  },
  {
    field: 'custCode',
    label: '客户编号',
    minWidth: '150px'
  },
  {
    field: 'settlementName',
    label: '收款方式',
    width: '200px'
  },
  {
    field: 'source',
    label: '来源'
  },
  {
    field: 'currency',
    label: '订单币别'
  },
  {
    field: 'receivableAmount',
    label: '应收金额'
  },
  {
    field: 'receivedAmount',
    label: '已收金额'
  },
  {
    field: 'claimedAmount',
    label: '本次入账币种认领金额'
  },
  {
    field: 'contractAmount',
    label: '订单币种认领金额'
  },
  {
    field: 'completedFlag',
    label: '收款完成',
    formatter: formatDictColumn(DICT_TYPE.CONFIRM_TYPE)
  },
  {
    field: 'differenceAmount',
    label: '差异总金额'
  },
  {
    field: 'remark',
    label: '备注'
  },
  {
    field: 'claimPerson',
    label: '认领人',
    slots: {
      default: (scope) => {
        return <span>{scope.row.claimPerson?.nickname || ''}</span>
      }
    }
  },
  {
    field: 'claimDate',
    label: '认领日期',
    formatter: formatDateColumn()
  },
  {
    field: 'differenceReason',
    label: '差异原因',
    minWidth: '200px',
    showOverflowTooltip: false,
    slots: {
      default: (scope) => {
        return <DifferenceReasonInfo info={scope.row.differenceReason} />
      }
    }
  },
  {
    field: 'financeAmount',
    label: '财务费用',
    minWidth: '120px',
    formatter: formatMoneyColumn()
  }
])

const otherFeeTableColumns = reactive([
  {
    field: 'otherFeeType',
    label: '其他收费类型',
    width: '150px',
    formatter: formatDictColumn(DICT_TYPE.CLAIM_OTHER_FEE_TYPE)
  },

  {
    field: 'custName',
    label: '客户名称',
    width: '180px'
  },
  {
    field: 'custCode',
    label: '客户编号',
    minWidth: '150px'
  },
  {
    field: 'currency',
    label: '订单币别'
  },
  {
    field: 'receivableAmount',
    label: '应收金额'
  },
  {
    field: 'claimedAmount',
    label: '认领金额'
  },
  {
    field: 'differenceAmount',
    label: '差异总金额'
  },
  {
    field: 'claimPerson',
    label: '认领人',
    slots: {
      default: (scope) => {
        return <span>{scope.row.claimPerson?.nickname || ''}</span>
      }
    }
  },
  {
    field: 'claimDate',
    label: '认领日期',
    formatter: formatDateColumn()
  },
  {
    field: 'differenceReason',
    label: '差异原因',
    minWidth: '200px',
    showOverflowTooltip: false,
    slots: {
      default: (scope) => {
        return <DifferenceReasonInfo info={scope.row.differenceReason} />
      }
    }
  }
])

const handleClick = (val) => {
  // console.log(val)
}
const loading = ref(true)
const getPurchasePlanDetail = () => {}
const handleUpdate = async () => {
  await getPurchasePlanDetail()
}

const getInfo = async () => {
  loading.value = true
  try {
    receiptClaimDetail.value = await CustClaimApi.getCustClaimDetail({ id: props?.id })
  } finally {
    loading.value = false
  }
}

const tableMaxHeight = ref(0)
const eplusDetailRef = ref()
onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('采购计划ID不能为空')
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
  setTimeout(() => {
    tableMaxHeight.value = eplusDetailRef.value.bottomHeight - 54
  }, 1000)
})
</script>
<style scoped lang="scss"></style>
