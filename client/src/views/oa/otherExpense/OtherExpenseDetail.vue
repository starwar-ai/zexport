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
      permi: 'oa:other-reimb:submit',
      user: generalReimbInfo.reimbUser?.userId,
      handler: () => goEdit('')
    }"
    :cancel="{ permi: 'oa:other-reimb:submit', handler: () => {} }"
    :edit="{
      permi: 'oa:other-reimb:update',
      user: generalReimbInfo.reimbUser?.userId,
      handler: () => goEdit('')
    }"
    :approve="{
      permi: 'oa:other-reimb:audit',
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
      <template #totalAmountList>
        <span>{{
          generalReimbInfo?.totalAmountList[0]?.amount
            ? generalReimbInfo?.totalAmountList[0]?.amount
            : '-'
        }}</span>
        &nbsp;
        <span>{{
          generalReimbInfo?.totalAmountList[0]?.currency
            ? generalReimbInfo?.totalAmountList[0]?.currency
            : '-'
        }}</span>
      </template>
      <template #expenseUseToFormal>
        <el-input v-model="generalReimbInfo.expenseUseToFormal" />
      </template>
      <template #financialSubjectId>
        <eplus-subject-tree-select
          v-model="generalReimbInfo.financialSubjectId"
          class="!w-100%"
          :clearable="false"
          @change="financialSubjectIdChange"
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
import { getTotal, maskBankAccount, openPdf } from '@/utils/index'
import { examineApprove, examineReject } from '@/api/audit/travel-reimb'
import * as OtherApi from '@/api/oa/otherExpense'
import { isValidArray } from '@/utils/is'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import { columnWidth, formatMoneyColumn } from '@/utils/table'
import { useUserStore } from '@/store/modules/user'
import DocInfo from '../components/DocInfo.vue'
import FeeShareDetail from '@/views/ems/send/components/FeeShare/FeeShareDetail.vue'
import UploadList from '@/components/UploadList/index.vue'
import { moneyInputPrecision } from '@/utils/config'
import { EplusMoneyLabel, EplusNumInput } from '@/components/EplusMoney'
import { useFeeStore } from '@/store/modules/fee'
import { setSourceId } from '@/utils/auth'

const feeList = useFeeStore().feeList
const dictSubjectList: Array<{ id: number; feeName: string; showFeeFlag: number }> = isValidArray(
  feeList
)
  ? feeList.filter((item: any) => item.showFeeFlag != 1)
  : []
// const feeDescList = isValidArray(feeList)
//   ? feeList.filter((item: any) => item.showDescFlag == 1)
//   : []
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
      ? await OtherApi.getOtherReimb(props.id)
      : await OtherApi.getOtherReimbProcess({ id: query?.id })
    expenseInfo[2].disabled = generalReimbInfo.value.repayFlag === 0
    generalReimbInfo.value.actualUserId = generalReimbInfo.value.actualUser?.userId
    editFlag.value =
      generalReimbInfo.value.processInstanceId &&
      permissions.includes('oa:other-reimb:saveAndAudit')
    if (editFlag.value) {
      expenseListColumns.splice(4, 0, editItem)
    }
    if (
      generalReimbInfo.value.processInstanceId &&
      permissions.includes('oa:other-reimb:uploadAndAudit')
    ) {
      expenseListColumns.splice(4, 0, ...uploadItems)
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

const uploadItems = reactive([
  {
    label: '发票名称',
    field: 'dictSubjectId',
    width: '220px',
    slots: {
      default: ({ row }) => {
        return (
          <>
            <eplus-input-select
              v-model={row.dictSubjectId}
              dataList={dictSubjectList}
              label="feeName"
              value="id"
            />
          </>
        )
      }
    }
  },
  {
    label: '发票金额',
    field: 'invoiceAmount',
    width: '220px',
    slots: {
      default: ({ row }) => {
        return (
          <EplusNumInput
            v-model={row.invoiceAmount}
            min={0}
            controls={false}
            precision={moneyInputPrecision}
            class="!w-90%"
          />
        )
      }
    }
  },
  {
    label: '发票附件',
    field: 'invoice',
    slots: {
      default: ({ row }) => {
        return (
          <UploadList
            v-model={row.invoice}
            fileList={row.invoice}
            onSuccess={(params) => {
              if (params.length > 0) {
                row.invoice = params
              }
            }}
          />
        )
      }
    }
  }
])

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
    field: 'picture',
    label: '支付截图',
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
const handleApprove = async () => {
  if (editFlag.value) {
    for (let index = 0; index < generalReimbInfo.value?.reimbDetailList.length; index++) {
      const item = generalReimbInfo.value?.reimbDetailList[index]
      if (!item.financialSubjectId) {
        const errorMsg = `费用小项信息第${index + 1}条数据 会计科目为空`
        message.warning(errorMsg)
        throw new Error(errorMsg)
      }
    }
  }

  let regx = new RegExp(`^(\\d{1,11})(\\.?\\d{1,${moneyInputPrecision}})?$`)
  if (permissions.includes('oa:other-reimb:uploadAndAudit')) {
    for (let index = 0; index < generalReimbInfo.value?.reimbDetailList.length; index++) {
      const item = generalReimbInfo.value?.reimbDetailList[index]
      // 发票名称必填验证
      if (!item.dictSubjectId) {
        const errorMsg = `费用小项信息第${index + 1}条数据 发票名称为必填项`
        message.warning(errorMsg)
        throw new Error(errorMsg)
      }

      // 获取发票名称文本
      const invoiceName =
        dictSubjectList.find((subject) => subject.id === item.dictSubjectId)?.feeName || ''

      // 如果发票名称不是"无"，则发票金额必填
      if (invoiceName !== '无') {
        if (!item.invoiceAmount) {
          const errorMsg = `费用小项信息第${index + 1}条数据 发票金额为必填项`
          message.warning(errorMsg)
          throw new Error(errorMsg)
        }
        if (item.invoiceAmount <= 0) {
          const errorMsg = `费用小项信息第${index + 1}条数据 发票金额必须大于0`
          message.warning(errorMsg)
          throw new Error(errorMsg)
        } else if (!regx.test(item.invoiceAmount)) {
          const errorMsg = `费用小项信息第${index + 1}条数据 发票金额只能输入正数,最多11位整数,${moneyInputPrecision}位小数`
          message.warning(errorMsg)
          throw new Error(errorMsg)
        }
      }
    }
  }

  if (editFlag.value || permissions.includes('oa:other-reimb:uploadAndAudit')) {
    await OtherApi.updateOtherReimb({
      ...generalReimbInfo.value,
      actualUserId: generalReimbInfo.value.actualUser.userId
    })
    return true
  }
}

// 显式暴露方法给父组件
defineExpose({
  handleApprove
})
const getTasks = (data) => {
  // if (data.result == 2 && editFlag.value) {
  //   nextTick(() => {
  //     expenseListColumns[4] = {
  //       field: 'financialSubjectName',
  //       label: '管理科目'
  //     }
  //   })
  // }
}
const handleUpdate = async () => {
  await getInfo()
}

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
  if (permissions.includes('oa:other-reimb:print')) {
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
    permissions.includes('oa:other-reimb:delete') &&
    [0, 3, 4].includes(generalReimbInfo.value.auditStatus) &&
    userId === generalReimbInfo.value.reimbUser.userId
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await message.confirm('确定删除吗？')
          await OtherApi.deleteOtherReimb(generalReimbInfo.value.id)
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
  const url = await OtherApi.print({
    id: props?.id,
    reportCode: 'reimb',
    companyId: generalReimbInfo.value.companyId
  })
  openPdf(url)
}

watch(
  () => generalReimbInfo.value?.reimbDetailList,
  (list) => {
    if (list) {
      generalReimbInfo.value.invoiceAmountList = getTotal(list, 'invoiceAmount', 'expenseCurrency')
    }
  },
  { immediate: true, deep: true }
)

const router = useRouter()
const handleDetail = (id) => {
  setSourceId(id)
  router.push({ path: '/oa/apply/generalIndex' })
}
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
