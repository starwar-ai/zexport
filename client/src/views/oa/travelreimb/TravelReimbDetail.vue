<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineApprove"
    :rejectApi="examineReject"
    :auditable="travelReimbInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: 'oa:travel-reimb:submit',
      handler: () => {}
    }"
    :edit="{
      user: travelReimbInfo?.reimbUser?.userId,
      permi: 'oa:travel-reimb:update',
      handler: () => goEdit('差旅费')
    }"
    :approve="{
      permi: 'oa:travel-reimb:audit',
      handler: handleApprove
    }"
    @get-tasks="getTasks"
    @update="handleUpdate"
  >
    <div class="mb10px flex">
      <el-card
        style="width: 180px; text-align: center"
        class="mr20px"
      >
        <div class="mb10px font-600">报销总金额</div>
        <div style="color: #409eff">
          <EplusMoneyLabel :val="travelReimbInfo.amountList || {}" />
        </div>
      </el-card>

      <el-card
        style="width: 180px; text-align: center"
        class="mr20px"
      >
        <div class="mb10px font-600">发票总金额</div>
        <div style="color: #409eff">
          <EplusMoneyLabel :val="travelReimbInfo.invoiceAmountList || {}" />
        </div>
      </el-card>
      <el-card
        style="width: 180px; text-align: center"
        class="mr20px"
      >
        <div class="mb10px font-600">本次还款金额</div>
        <div style="color: #409eff">
          <EplusMoneyLabel :val="travelReimbInfo.repayAmountList || {}" />
        </div>
      </el-card>
      <el-card
        style="width: 180px; text-align: center"
        class="mr20px"
      >
        <div class="mb10px font-600">本次报销金额</div>
        <div style="color: #409eff">
          <EplusMoneyLabel :val="travelReimbInfo.totalAmountList" />
        </div>
      </el-card>
    </div>

    <DocInfo :pageInfo="travelReimbInfo" />
    <eplus-description
      title="报销说明"
      :data="travelReimbInfo"
      :items="explainSchemas"
    >
      <template #reimbUserName>
        {{ travelReimbInfo?.reimbUser?.nickname }} ({{
          travelReimbInfo?.reimbUser.deptName || '-'
        }})
      </template>
      <template #actualUser>
        <span
          >{{ travelReimbInfo?.actualUser?.nickname }} ({{
            travelReimbInfo?.actualUser.deptName || '-'
          }})
        </span>
      </template>
      <template #expenseUseToFormal>
        <el-input v-model="travelReimbInfo.expenseUseToFormal" />
      </template>
      <template #applyList>
        <Table
          :columns="applyListColumns"
          headerAlign="center"
          align="center"
          :data="travelReimbInfo.applyList || []"
        />
      </template>
      <template #bankAccount>
        {{ maskBankAccount(travelReimbInfo?.bankAccount) }}
      </template>
    </eplus-description>

    <eplus-description
      title="费用小项"
      :data="travelReimbInfo"
      :items="detailSchemas"
    >
      <template #trafficFeeList>
        <Table
          class="mb20px"
          :columns="trafficFeeColumns"
          headerAlign="center"
          align="center"
          :data="travelReimbInfo?.trafficFeeList"
        />
        <!-- <div class="total_box">
          <div> 总计 </div>
          <div class="pr15px">
            报销金额： <TotalLayout :list="getTotal(travelReimbInfo?.trafficFeeList)" />
          </div>
        </div> -->
      </template>
      <template #accommodationFeeList>
        <Table
          class="mb20px"
          :columns="accommodationFeeColumns"
          headerAlign="center"
          align="center"
          :data="travelReimbInfo?.accommodationFeeList"
        />
        <!-- <div class="total_box">
          <div> 总计 </div>
          <div class="pr15px">
            报销金额： <TotalLayout :list="getTotal(travelReimbInfo?.accommodationFeeList)" />
          </div>
        </div> -->
      </template>
      <template #selfDrivingList>
        <Table
          class="mb20px"
          :columns="selfDrivingColumns"
          headerAlign="center"
          align="center"
          :data="travelReimbInfo?.selfDrivingList"
        />
        <!-- <div class="total_box">
          <div> 总计 </div>
          <div class="pr15px">
            报销金额： <TotalLayout :list="getTotal(travelReimbInfo?.selfDrivingList)" />
          </div>
        </div> -->
      </template>
      <template #travelAllowanceList>
        <Table
          class="mb20px"
          :columns="travelAllowanceColumns"
          headerAlign="center"
          align="center"
          :data="travelReimbInfo?.travelAllowanceList"
        />
        <!-- <div class="total_box">
          <div> 总计 </div>
          <div class="pr15px">
            报销金额： <TotalLayout :list="getTotal(travelReimbInfo?.travelAllowanceList)" />
          </div>
        </div> -->
      </template>
      <template #otherDescList>
        <Table
          class="mb20px"
          :columns="otherDescListColumns"
          headerAlign="center"
          align="center"
          :data="travelReimbInfo?.otherDescList"
        />
        <!-- <div class="total_box">
          <div> 总计 </div>
          <div class="pr15px">
            报销金额： <TotalLayout :list="getTotal(travelReimbInfo?.otherDescList)" />
          </div>
        </div> -->
      </template>

      <template #loanAppList>
        <Table
          :columns="loanAppColumns"
          headerAlign="center"
          align="center"
          :data="travelReimbInfo?.loanAppList"
        />
      </template>
    </eplus-description>

    <FeeShareDetail
      v-if="travelReimbInfo.auxiliaryType === 1 && isValidArray(travelReimbInfo.feeShare)"
      :info="travelReimbInfo.feeShare"
    />
    <div
      v-else
      class="mb10px text-red"
      >该报销单未进行费用归集
    </div>

    <eplus-description
      v-if="travelReimbInfo?.cancelReason"
      title="作废信息"
      :data="travelReimbInfo"
      :items="cancelSchemas"
    />
  </eplus-detail>
</template>
<script setup lang="tsx">
import { maskBankAccount, openPdf } from '@/utils/index'
import { examineApprove, examineReject } from '@/api/audit/travel-reimb'
import * as TravelReimbApi from '@/api/oa/travelreimb'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'
import UploadList from '@/components/UploadList/index.vue'
import DocInfo from '../components/DocInfo.vue'
import { isValidArray } from '@/utils/is'
import FeeShareDetail from '@/views/ems/send/components/FeeShare/FeeShareDetail.vue'
import { useUserStore } from '@/store/modules/user'
import { EplusMoneyLabel } from '@/components/EplusMoney' //金额格式化
import { getConfigKey } from '@/api/common'
import { setSourceId } from '@/utils/auth'

const permissions = useUserStore().getPermissions
const userId = useUserStore().getUser.id
const message = useMessage()
const travelReimbInfo = ref<typeObj>({})
const editFlag = ref(false)
const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  pageInfo?: object
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'TravelReimbDetail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const { updateDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
    })
  : { updateDialogActions: () => {} }

const loading = ref(true)
const { query } = useRoute()

const getColIndex = (list) => {
  const index = list.findIndex((item) => item.field === 'feeName')
  return index + 1
}
const getInfo = async () => {
  loading.value = true
  try {
    travelReimbInfo.value = props.id
      ? await TravelReimbApi.getTravelReimb(props.id)
      : await TravelReimbApi.getTravelReimbProcess({ id: query?.id })
    editFlag.value =
      travelReimbInfo.value.processInstanceId &&
      permissions.includes('oa:travel-reimb:saveAndAudit')
    console.log(getColIndex(selfDrivingColumns))
    if (editFlag.value) {
      trafficFeeColumns.splice(getColIndex(trafficFeeColumns), 0, editItem)
      accommodationFeeColumns.splice(getColIndex(accommodationFeeColumns), 0, editItem)
      selfDrivingColumns.splice(getColIndex(selfDrivingColumns), 0, editItem)
      travelAllowanceColumns.splice(getColIndex(travelAllowanceColumns), 0, editItem)
      otherDescListColumns.splice(getColIndex(otherDescListColumns), 0, editItem)
    }
    detailSchemas[0].disabled = !isValidArray(travelReimbInfo.value.trafficFeeList)
    detailSchemas[1].disabled = !isValidArray(travelReimbInfo.value.selfDrivingList)
    detailSchemas[2].disabled = !isValidArray(travelReimbInfo.value.accommodationFeeList)
    detailSchemas[3].disabled = !isValidArray(travelReimbInfo.value.travelAllowanceList)
    detailSchemas[4].disabled = !isValidArray(travelReimbInfo.value.otherDescList)
    detailSchemas[6].disabled = !isValidArray(travelReimbInfo.value.loanAppList)
  } finally {
    loading.value = false
  }
}
/**
 * 生成明细信息
 * @param r
 */

const explainSchemas: EplusDescriptionItemSchema[] = reactive([
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
  {
    field: 'expenseUseToOccur',
    label: '申请事由'
  },
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
    label: '关联申请单',
    span: 24,
    disabled: true
  }
])

const detailSchemas: EplusDescriptionItemSchema[] = [
  {
    slotName: 'trafficFeeList',
    field: 'trafficFeeList',
    label: '交通费',
    span: 24,
    disabled: true
  },
  {
    slotName: 'selfDrivingList',
    field: 'selfDrivingList',
    label: '自驾',
    span: 24,
    disabled: true
  },
  {
    slotName: 'accommodationFeeList',
    field: 'accommodationFeeList',
    label: '住宿',
    span: 24,
    disabled: true
  },
  {
    slotName: 'travelAllowanceList',
    field: 'travelAllowanceList',
    label: '出差补贴',
    span: 24,
    disabled: true
  },
  {
    slotName: 'otherDescList',
    field: 'otherDescList',
    label: '其他',
    span: 24,
    disabled: true
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

const checkFinancialSubjectId = (list, name) => {
  if (isValidArray(list)) {
    for (let index = 0; index < list.length; index++) {
      const item = list[index]
      if (!item.financialSubjectId) {
        message.warning(`费用小项${name}信息第${index + 1}条数据管理科目为空`)
        return false
      }
    }
  }
}
const handleApprove = async () => {
  if (editFlag.value) {
    // checkFinancialSubjectId(travelReimbInfo.value.otherDescList, '其他')
    // checkFinancialSubjectId(travelReimbInfo.value.travelAllowanceList, '出差补贴')
    // checkFinancialSubjectId(travelReimbInfo.value.selfDrivingList, '自驾')
    // checkFinancialSubjectId(travelReimbInfo.value.accommodationFeeList, '住宿费')
    // checkFinancialSubjectId(travelReimbInfo.value.trafficFeeList, '交通费')
    if (isValidArray(travelReimbInfo.value.trafficFeeList)) {
      for (let index = 0; index < travelReimbInfo.value.trafficFeeList.length; index++) {
        const item = travelReimbInfo.value.trafficFeeList[index]
        if (!item.financialSubjectId) {
          message.warning(`费用小项交通费信息第${index + 1}条数据管理科目为空`)
          return false
        }
      }
    }
    if (isValidArray(travelReimbInfo.value.selfDrivingList)) {
      for (let index = 0; index < travelReimbInfo.value.selfDrivingList.length; index++) {
        const item = travelReimbInfo.value.selfDrivingList[index]
        if (!item.financialSubjectId) {
          message.warning(`费用小项自驾信息第${index + 1}条数据管理科目为空`)
          return false
        }
      }
    }
    if (isValidArray(travelReimbInfo.value.accommodationFeeList)) {
      for (let index = 0; index < travelReimbInfo.value.accommodationFeeList.length; index++) {
        const item = travelReimbInfo.value.accommodationFeeList[index]
        if (!item.financialSubjectId) {
          message.warning(`费用小项住宿费信息第${index + 1}条数据管理科目为空`)
          return false
        }
      }
    }
    if (isValidArray(travelReimbInfo.value.travelAllowanceList)) {
      for (let index = 0; index < travelReimbInfo.value.travelAllowanceList.length; index++) {
        const item = travelReimbInfo.value.travelAllowanceList[index]
        if (!item.financialSubjectId) {
          message.warning(`费用小项出差补贴信息第${index + 1}条数据管理科目为空`)
          return false
        }
      }
    }

    if (isValidArray(travelReimbInfo.value.otherDescList)) {
      for (let index = 0; index < travelReimbInfo.value.otherDescList.length; index++) {
        const item = travelReimbInfo.value.otherDescList[index]
        if (!item.financialSubjectId) {
          message.warning(`费用小项其他信息第${index + 1}条数据管理科目为空`)
          return false
        }
      }
    }

    await TravelReimbApi.updateTravelReimb({
      ...travelReimbInfo.value,
      actualUserId: travelReimbInfo.value.actualUser.userId
    })
    return true
  }
}

const getTasks = (data) => {
  if (data.result == 2) {
    if (isValidArray(travelReimbInfo.value.otherDescList)) {
      otherDescListColumns[getColIndex(otherDescListColumns)] = {
        field: 'financialSubjectName',
        label: '管理科目'
      }
    }

    if (isValidArray(travelReimbInfo.value.travelAllowanceList)) {
      travelAllowanceColumns[getColIndex(travelAllowanceColumns)] = {
        field: 'financialSubjectName',
        label: '管理科目'
      }
    }

    if (isValidArray(travelReimbInfo.value.selfDrivingList)) {
      console.log(getColIndex(selfDrivingColumns))
      selfDrivingColumns[getColIndex(selfDrivingColumns)] = {
        field: 'financialSubjectName',
        label: '管理科目'
      }
    }

    if (isValidArray(travelReimbInfo.value.accommodationFeeList)) {
      accommodationFeeColumns[getColIndex(accommodationFeeColumns)] = {
        field: 'financialSubjectName',
        label: '管理科目'
      }
    }
    if (isValidArray(travelReimbInfo.value.trafficFeeList)) {
      trafficFeeColumns[getColIndex(trafficFeeColumns)] = {
        field: 'financialSubjectName',
        label: '管理科目'
      }
    }
  }
}

const handleUpdate = async () => {
  await getInfo()
}

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

const trafficFeeColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'transportationType',
    label: '类别',
    formatter: formatDictColumn(DICT_TYPE.TRANSPORTATION_TYPE)
  },
  {
    field: 'transportationDate',
    label: '日期',
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    field: 'transportationFrom',
    label: '出发地'
  },
  {
    field: 'transportationTo',
    label: '目的地'
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
const selfDrivingColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'routeDesc',
    label: '线路说明'
  },
  {
    field: 'mileageStart',
    label: '出发里程数（km）'
  },
  {
    field: 'mileageEnd',
    label: '最终里程数（km）'
  },
  {
    field: 'mileage',
    label: '花费里程数（km）'
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

const accommodationFeeColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'lodgingCity',
    label: '住宿标准',
    formatter: formatDictColumn(DICT_TYPE.ACCOMMODATION_FEE_STANDARD)
  },
  {
    field: 'stayLength',
    label: '总晚数'
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

const applyListColumns = reactive([
  {
    field: 'code',
    label: '申请单号',
    width: columnWidth.l,
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
    field: 'travelType',
    label: '出差类型',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.TRAVEL_TYPE)
  },
  {
    field: 'dest',
    label: '出差地点',
    minWidth: columnWidth.m
  },
  {
    field: 'startTime',
    label: '开始时间',
    minWidth: columnWidth.m,
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    field: 'endTime',
    label: '结束时间',
    minWidth: columnWidth.m,
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    field: 'amount',
    label: '预估费用',
    minWidth: columnWidth.m,
    formatter: formatMoneyColumn()
  },
  {
    field: 'purpose',
    label: '出差事由',
    minWidth: columnWidth.m
  },
  {
    field: 'remark',
    label: '备注',
    minWidth: columnWidth.m
  }
])

const travelAllowanceColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'allowanceStandard',
    label: '补贴标准',
    slots: {
      default: (data) => {
        const { column, row } = data
        return `${row.allowanceStandard}RMB/天`
      }
    }
  },
  {
    field: 'allowanceDay',
    label: '出差天数'
  },
  {
    field: 'feeName',
    label: '发票名称'
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
    field: 'remark',
    label: '备注'
  }
])

const otherDescListColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'otherDesc',
    label: '说明'
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

const getApplyFlag = async () => {
  const flag = await getConfigKey('oa.travel.apply')
  explainSchemas[explainSchemas.length - 1].disabled = !Number(flag)
}

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
  await getApplyFlag()
  await getInfo()
  if (permissions.includes('oa:travel-reimb:print')) {
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
    permissions.includes('oa:travel-reimb:delete') &&
    [0, 3, 4].includes(travelReimbInfo.value.auditStatus) &&
    userId === travelReimbInfo.value.reimbUser.userId
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await message.confirm('确定删除吗？')
          await TravelReimbApi.deleteTravelReimb(travelReimbInfo.value.id)
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
  const url = await TravelReimbApi.print({
    id: props?.id,
    reportCode: 'reimb',
    companyId: travelReimbInfo.value.companyId
  })
  openPdf(url)
}

const router = useRouter()
const handleDetail = (id) => {
  setSourceId(id)
  router.push({ path: '/oa/apply/travelIndex' })
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
