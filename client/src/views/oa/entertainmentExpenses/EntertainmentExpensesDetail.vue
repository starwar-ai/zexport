<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="EntertainApi.approveEntertain"
    :rejectApi="EntertainApi.rejectEntertain"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :submit="{
      permi: 'oa:entertain-reimb:submit',
      handler: () => goEdit('还款单')
    }"
    :cancel="{
      permi: 'oa:entertain-reimb:submit'
    }"
    :edit="{
      user: pageInfo?.reimbUser?.userId,
      permi: 'oa:entertain-reimb:update',
      handler: () => goEdit('还款单')
    }"
    :approve="{
      permi: 'oa:entertain-reimb:audit',
      handler: handleApprove
    }"
    @update="handleUpdate"
    @get-tasks="getTasks"
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
        <!-- <div class="total_box">
          <div> 总计 </div>
          <div class="pr15px"
            <span class="pr20px">
              发票金额：<TotalLayout :list="pageInfo?.invoiceAmountList || []" />
            </span>
            <span>报销金额：<TotalLayout :list="pageInfo?.totalAmountList" /></span>
          </div>
        </div> -->
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

    <eplus-description
      v-if="pageInfo?.cancelReason"
      title="作废信息"
      :data="pageInfo"
      :items="cancelSchemas"
    />
  </eplus-detail>
</template>
<script setup lang="tsx">
import { maskBankAccount, openPdf } from '@/utils/index'
import { EplusDetail } from '@/components/EplusTemplate'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import * as EntertainApi from '@/api/oa/entertain'
import UploadList from '@/components/UploadList/index.vue'
import DocInfo from '../components/DocInfo.vue'
import { useUserStore } from '@/store/modules/user'
import FeeShareDetail from '@/views/ems/send/components/FeeShare/FeeShareDetail.vue'
import { isValidArray } from '@/utils/is'
import LoanAppListDetail from '../components/LoanAppListDetail.vue'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import { setSourceId } from '@/utils/auth'

const router = useRouter()
const permissions = useUserStore().getPermissions
const userId = useUserStore().getUser.id
const message = useMessage()
const pageInfo: any = ref({}) // 借款申请单详情
const eplusDetailRef = ref()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const editFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'EntertainmentExpensesDetail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const loading = ref(true)
const { query } = useRoute()

const getPageInfo = async () => {
  loading.value = true
  try {
    pageInfo.value = props.id
      ? await EntertainApi.getEntertainInfo({ id: props.id })
      : await EntertainApi.getAuditEntertainInfo({ id: query?.id })

    expenseInfo[2].disabled = !isValidArray(pageInfo.value.loanAppList)

    editFlag.value =
      pageInfo.value.processInstanceId && permissions.includes('oa:entertain-reimb:saveAndAudit')

    if (editFlag.value) {
      expenseListColumns.splice(3, 0, editItem)
    }
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
          onChange={(...$event) => {
            row.financialSubjectName = ''
          }}
        />
      )
    }
  }
})

const expenseAppColumns = reactive([
  {
    field: 'code',
    label: '申请单号',
    minWidth: columnWidth.l,
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
  },
  {
    field: 'remark',
    label: '备注',
    minWidth: columnWidth.l
  }
])

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

const handleApprove = async () => {
  if (editFlag.value) {
    for (let index = 0; index < pageInfo.value?.reimbDetailList.length; index++) {
      const item = pageInfo.value?.reimbDetailList[index]
      if (!item.financialSubjectId) {
        message.warning(`费用小项信息第${index + 1}条数据 管理科目为空`)
        return false
      }
    }
    await EntertainApi.updateEntertain({
      ...pageInfo.value,
      actualUserId: pageInfo.value.actualUser.userId
    })
    return true
  }
}

const handleUpdate = async () => {
  await getPageInfo()
}

const getTasks = (data) => {
  if (data.result == 2) {
    expenseListColumns[3] = {
      field: 'financialSubjectName',
      label: '管理科目'
    }
  }
}

const { updateDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
    })
  : { updateDialogActions: () => {} }

onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('招待费报销单ID不能为空')
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
  await getPageInfo()
  if (permissions.includes('oa:general-reimb:print')) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await print()
        }}
        type="primary"
        key="print"
      >
        打印
      </el-button>
    )
  }
  if (
    permissions.includes('oa:entertain-reimb:delete') &&
    [0, 3, 4].includes(pageInfo.value.auditStatus) &&
    userId === pageInfo.value.reimbUser.userId
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await message.confirm('确定删除吗？')
          await EntertainApi.deleteEntertain(pageInfo.value.id)
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
  const url = await EntertainApi.print({
    id: props?.id,
    reportCode: 'reimb',
    companyId: pageInfo.value.companyId
  })
  openPdf(url)
}

const handleDetail = (id) => {
  setSourceId(id)
  router.push({ path: '/oa/apply/entertainIndex' })
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
</style>
