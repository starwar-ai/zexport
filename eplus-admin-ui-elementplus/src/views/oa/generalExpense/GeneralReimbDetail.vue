<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineApprove"
    :rejectApi="examineReject"
    :auditable="generalReimbInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :submit="{
      permi: 'oa:general-reimb:submit',
      user: generalReimbInfo.reimbUser.userId,
      handler: () => goEdit('')
    }"
    :cancel="{ permi: 'oa:general-reimb:submit', handler: () => {} }"
    :edit="{
      permi: 'oa:general-reimb:update',
      user: generalReimbInfo.reimbUser.userId,
      handler: () => goEdit('')
    }"
    :approve="{
      permi: 'oa:general-reimb:audit',
      handler: handleApprove
    }"
    @get-tasks="getTasks"
    @update="handleUpdate"
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
      <template #financialSubjectId>
        <eplus-subject-tree-select
          v-model="generalReimbInfo.financialSubjectId"
          class="!w-100%"
          :clearable="false"
          @change="(...$event) => financialSubjectIdChange($event, row)"
        />
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

    <eplus-description
      v-if="generalReimbInfo?.cancelReason"
      title="作废信息"
      :data="generalReimbInfo"
      :items="cancelSchemas"
    />
  </eplus-detail>
</template>
<script setup lang="tsx">
import { maskBankAccount, openPdf } from '@/utils/index'
import { examineApprove, examineReject } from '@/api/audit/travel-reimb'
import * as GeneralApi from '@/api/oa/generalExpense'
import { isValidArray } from '@/utils/is'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import { columnWidth, formatDateColumn, formatMoneyColumn } from '@/utils/table'
import { useUserStore } from '@/store/modules/user'
import DocInfo from '../components/DocInfo.vue'
import FeeShareDetail from '@/views/ems/send/components/FeeShare/FeeShareDetail.vue'
import UploadList from '@/components/UploadList/index.vue'
import { setSourceId } from '@/utils/auth'

const userId = useUserStore().getUser.id
const message = useMessage()
const generalReimbInfo = ref<typeObj>({})
const permissions = useUserStore().getPermissions

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  pageInfo?: object
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()

const editFlag = ref(false)
defineOptions({ name: 'GeneralReimbDetail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const loading = ref(true)
const { query } = useRoute()

const financialSubjectIdChange = () => {
  generalReimbInfo.financialSubjectName = ''
}

const getInfo = async () => {
  loading.value = true
  try {
    generalReimbInfo.value = props.id
      ? await GeneralApi.getGeneralReimb(props.id)
      : await GeneralApi.getGeneralReimbProcess({ id: query?.id })

    expenseInfo[2].disabled = generalReimbInfo.value.repayFlag === 0

    editFlag.value =
      generalReimbInfo.value.processInstanceId &&
      permissions.includes('oa:general-reimb:saveAndAudit')
    if (editFlag.value) {
      expenseListColumns.splice(4, 0, editItem)
    }
    //通过类型判断展示的table列
    if (generalReimbInfo.value.auxiliaryType === 1) {
      auxiliaryListColumns = custColumns
    } else if (generalReimbInfo.value.auxiliaryType === 2) {
      auxiliaryListColumns = venderColumns
    }
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
    minWidth: columnWidth.m,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <el-button
            link
            type="primary"
            onClick={() => {
              handleDetail(row.id)
            }}
          >
            {row.code}
          </el-button>
        )
      }
    }
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
  },
  {
    field: 'remark',
    label: '备注'
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
const editItem = reactive({
  field: 'financialSubjectId',
  label: '管理科目',
  slots: {
    default: (data) => {
      const { row } = data
      return (
        <eplus-subject-tree-select
          v-model={row.financialSubjectId}
          class="!w-100%"
          clearable={false}
          onChange={(...$event) => financialSubjectIdChange($event, row)}
        />
      )
    }
  }
})

const expenseListColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  // {
  //   label: '申请事由',
  //   field: 'expenseUseToOccur'
  // },
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
    minWidth: '200px',
    showOverflowTooltip: false,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <div class="flex flex-wrap !w-100%">
            <UploadList
              fileList={row.invoice}
              disabled
            />
          </div>
        )
      }
    }
  },
  {
    field: 'picture',
    label: '支付截图',
    minWidth: '200px',
    showOverflowTooltip: false,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <div class="flex flex-wrap !w-100%">
            <UploadList
              fileList={row.picture}
              disabled
            />
          </div>
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

// const totalAmountListSchema: EplusDescriptionItemSchema[] = [
//   {
//     slotName: 'totalAmountList',
//     field: 'totalAmountList',
//     label: '',
//     span: 12
//   }
// ]
const collectionSchemas: EplusDescriptionItemSchema[] = [
  {
    slotName: 'feeShare',
    field: 'feeShare',
    label: '',
    span: 24
  }
]
const handleApprove = async () => {
  if (editFlag.value) {
    for (let index = 0; index < generalReimbInfo.value?.reimbDetailList.length; index++) {
      const item = generalReimbInfo.value?.reimbDetailList[index]
      if (!item.financialSubjectId) {
        message.warning(`费用小项信息第${index + 1}条数据 管理科目为空`)
        return false
      }
    }
    await GeneralApi.updateGeneralReimb({
      ...generalReimbInfo.value,
      actualUserId: generalReimbInfo.value.actualUser.userId
    })
    return true
  }
}
const getTasks = (data) => {
  if (data.result == 2 && editFlag.value) {
    nextTick(() => {
      expenseListColumns[4] = {
        field: 'financialSubjectName',
        label: '管理科目'
      }
    })
  }
}
const handleUpdate = async () => {
  await getInfo()
}

const custColumns = reactive([
  {
    field: 'custRespDTO.code',
    label: '客户编号'
  },
  {
    field: 'custRespDTO.createTime',
    label: '创建时间',
    formatter: formatDateColumn()
  },
  {
    field: 'custRespDTO.customerTypes',
    label: '类型'
  },
  {
    field: 'custRespDTO.name',
    label: '客户名称'
  },
  {
    field: 'ratio',
    label: '报销费用分摊（100%）'
  }
])
const venderColumns = reactive([
  {
    field: 'venderRespDTO.code',
    label: '供应商编号'
  },
  {
    field: 'venderRespDTO.createTime',
    label: '创建时间',
    formatter: formatDateColumn()
  },
  {
    field: 'venderRespDTO.name',
    label: '供应商名称'
  },
  {
    field: 'ratio',
    label: '报销费用分摊（100%）'
  }
])
let auxiliaryListColumns = reactive([])
// const { updateDialogActions } = inject('dialogActions') as {
//   updateDialogActions: (...args: any[]) => void
// }
const { updateDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
    })
  : { updateDialogActions: () => {} }
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
  if (permissions.includes('oa:general-reimb:print')) {
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
  if (
    permissions.includes('oa:general-reimb:delete') &&
    [0, 3, 4].includes(generalReimbInfo.value.auditStatus) &&
    userId === generalReimbInfo.value.reimbUser.userId
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await message.confirm('确定删除吗？')
          await GeneralApi.deleteGeneralReimb(generalReimbInfo.value.id)
          message.success('删除成功')
          close()
        }}
      >
        删除
      </el-button>
    )
  }
})
const print = async () => {
  const url = await GeneralApi.print({
    id: props?.id,
    reportCode: 'reimb',
    companyId: generalReimbInfo.value.companyId
  })
  openPdf(url)
}

const router = useRouter()
const handleDetail = (id) => {
  setSourceId(id)
  router.push({ path: '/oa/apply/generalIndex' })
}

defineExpose({ handleApprove })
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
