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
      permi: 'oa:travel-reimb:audit',
      handler: () => {}
    }"
    :cancel="{
      permi: 'oa:travel-reimb:cancel',
      handler: () => {}
    }"
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
      </template>
      <template #accommodationFeeList>
        <Table
          class="mb20px"
          :columns="accommodationFeeColumns"
          headerAlign="center"
          align="center"
          :data="travelReimbInfo?.accommodationFeeList"
        />
      </template>
      <template #selfDrivingList>
        <Table
          class="mb20px"
          :columns="selfDrivingColumns"
          headerAlign="center"
          align="center"
          :data="travelReimbInfo?.selfDrivingList"
        />
      </template>
      <template #travelAllowanceList>
        <Table
          class="mb20px"
          :columns="travelAllowanceColumns"
          headerAlign="center"
          align="center"
          :data="travelReimbInfo?.travelAllowanceList"
        />
      </template>
      <template #otherDescList>
        <Table
          class="mb20px"
          :columns="otherDescListColumns"
          headerAlign="center"
          align="center"
          :data="travelReimbInfo?.otherDescList"
        />
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
  </eplus-detail>
</template>
<script setup lang="tsx">
import { maskBankAccount } from '@/utils/index'
import { examineApprove, examineReject } from '@/api/audit/travel-reimb'
import * as TravelReimbApi from '@/api/oa/travelreimb'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import { columnWidth, formatDateColumn, formatDictColumn, formatMoneyColumn } from '@/utils/table'

import UploadList from '@/components/UploadList/index.vue'
import DocInfo from '../../components/DocInfo.vue'
import { isValidArray } from '@/utils/is'
import FeeShareDetail from '@/views/ems/send/components/FeeShare/FeeShareDetail.vue'
import { EplusMoneyLabel } from '@/components/EplusMoney'

const travelReimbInfo = ref<any>({})
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
defineOptions({ name: 'TravelReimbDetail' })

const loading = ref(true)

const getInfo = async () => {
  loading.value = true
  try {
    travelReimbInfo.value = await TravelReimbApi.getTravelReimb(props.id)
    travelReimbInfo.value.processInstanceId = props?.param
      ? props?.param
      : travelReimbInfo.value.processInstanceId
    travelReimbInfo.value.auditStatus = props?.status ? props?.status : ''

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
    span: 24
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

const applyListColumns = reactive([
  {
    field: 'code',
    label: '申请单号',
    width: columnWidth.l
  },
  {
    field: 'companyName',
    label: '费用主体',
    width: columnWidth.l
  },
  {
    field: 'travelType',
    label: '出差类型',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.TRAVEL_TYPE)
  },
  {
    field: 'purpose',
    label: '出差事由',
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
</style>
