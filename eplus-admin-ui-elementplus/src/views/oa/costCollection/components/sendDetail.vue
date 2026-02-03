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
      permi: 'ems:send:audit',
      handler: () => {}
    }"
    :cancel="{
      permi: 'ems:send:cancel',
      handler: () => {}
    }"
  >
    <!-- 信息 -->
    <eplus-description
      title="基本信息"
      :data="pageInfo"
      :items="basicInfo"
    />
    <FeeShareDetail :info="pageInfo.feeShare" />
    <el-tabs v-model="activeName">
      <el-tab-pane
        label="寄件明细"
        name="1"
      >
        <InspectInfoList :info="pageInfo.children" />
      </el-tab-pane>

      <el-tab-pane
        label="操作记录"
        name="2"
      >
        <eplus-operate-log :logList="pageInfo?.operateLogRespDTOList" />
      </el-tab-pane>
    </el-tabs>
  </eplus-detail>
</template>
<script setup lang="tsx">
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import InspectInfoList from '@/views/ems/send/components/InspectInfoList.vue'
import * as EmsListApi from '@/api/ems/emsList' // 寄件接口集合
import { examineApprove, examineReject } from '@/api/audit/inspect'
import FeeShareDetail from '@/views/ems/send/components/FeeShare/FeeShareDetail.vue'

const activeName = ref('1')

const pageInfo = ref({})

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
  readonly?: boolean
  param?: any
  status?: any
  auditInfo: any
}>()
defineOptions({ name: 'CostSendDetail' })
//定义edit事件

const loading = ref(true)

const getPageInfo = async () => {
  loading.value = true
  try {
    await refreshInfo()
  } finally {
    loading.value = false
  }
}

const refreshInfo = async () => {
  pageInfo.value = await EmsListApi.getEmSDetail(props.id)
  pageInfo.value.feeShare = pageInfo.value.feeShare == null ? {} : pageInfo.value.feeShare
  pageInfo.value.processInstanceId = props?.param ? props?.param : pageInfo.value.processInstanceId
  pageInfo.value.auditStatus = props?.status ? props?.status : ''
}

// 信息
const basicInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'code',
    label: '单据编号'
  },
  {
    field: 'sendTime',
    label: '寄件日期',
    type: 'date'
  },
  {
    field: 'sendRegion',
    label: '寄件区域',
    dictType: DICT_TYPE.SEND_REGION
  },
  {
    field: 'goodsType',
    label: '物件类型',
    dictType: DICT_TYPE.SEND_PRODUCT_TYPE
  },
  {
    field: 'receiveType',
    label: '收件方类型',
    dictType: DICT_TYPE.EMS_RECEIVE_TYPE
  },
  {
    field: 'receiveName',
    label: '收件方'
  },
  {
    field: 'receiveMsg',
    label: '收件信息',
    xl: 8,
    lg: 12
  },
  {
    field: 'venderName',
    label: '快递公司'
  },
  {
    field: 'payType',
    label: '付款方式',
    dictType: DICT_TYPE.SEND_PAY_TYPE
  },
  {
    field: 'estCost',
    label: '预估金额',
    type: 'money'
  },
  {
    field: 'cost',
    label: '实际金额',
    type: 'money'
  },
  {
    field: 'expressCode',
    label: '物流单号'
  },
  {
    field: 'sendStatus',
    label: '单据状态',
    dictType: DICT_TYPE.SEND_STATUS
  },
  {
    field: 'payStatus',
    label: '付款状态',
    dictType: DICT_TYPE.PAYMENT_STATUS
  },
  {
    field: 'inputUserName',
    label: '录入人'
  },
  {
    field: 'inputDeptName',
    label: '录入人部门'
  },
  {
    field: 'actualUser',
    label: '实际寄件人',
    formatter: (val) => {
      return val?.nickname
    }
  },
  {
    field: 'dealUserName',
    label: '处理人'
  },
  {
    field: 'belongFlag',
    label: '是否费用归属',
    dictType: DICT_TYPE.IS_INT
  },
  {
    field: 'remark',
    label: '备注',
    span: 24
  }
]

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
<style lang="scss" scoped></style>
