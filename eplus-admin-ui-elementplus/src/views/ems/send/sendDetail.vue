<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="EmsListApi.examineApprove"
    :rejectApi="EmsListApi.examineReject"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: 'ems:send:submit',
      handler: () => {}
    }"
    :approve="{
      permi: 'ems:send:audit',
      handler: () => {}
    }"
    @update="refreshInfo"
  >
    <!-- 信息 -->
    <eplus-description
      title="基本信息"
      :data="pageInfo"
      :items="basicInfo"
    />

    <FeeShareDetail :info="pageInfo.feeShare" />

    <!-- 信息列表 -->

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

  <EmsSendDialog
    ref="emsDialogRef"
    :key="Date.now()"
    @handle-success="refreshInfo"
  />

  <ChangeVenderDialog
    ref="changeVenderDialogRef"
    :sendId="props.id"
    :currentVenderId="pageInfo.venderId"
    @success="refreshInfo"
  />
</template>
<script setup lang="tsx">
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import InspectInfoList from './components/InspectInfoList.vue'
import * as EmsListApi from '@/api/ems/emsList' // 寄件接口集合
import FeeShareDetail from '@/views/ems/send/components/FeeShare/FeeShareDetail.vue'
import { SendStatusEnum } from '@/utils/constants'
import { checkPermi } from '@/utils/permission'
import EmsSendDialog from './components/EmsSendDialog.vue'
import ChangeVenderDialog from './components/ChangeVenderDialog.vue'

const activeName = ref('1')

const message = useMessage()
const pageInfo = ref<typeObj>({})

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
  readonly?: boolean
}>()
defineOptions({ name: 'InspectDetail' })
//定义edit事件
const { close, goEdit, goChange } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
      goChange: (val) => void
    })
  : { close: () => {}, goEdit: () => {}, goChange: () => {} }

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }

const loading = ref(true)
const { query } = useRoute()

const getPageInfo = async () => {
  loading.value = true
  try {
    await refreshInfo()
  } finally {
    loading.value = false
  }
}

const emsDialogRef = ref()
const changeVenderDialogRef = ref()

const setBtns = () => {
  clearDialogActions()

  if (
    [SendStatusEnum.WAITING_SUBMIT.status].includes(pageInfo.value?.sendStatus) &&
    checkPermi(['ems:send:update'])
  ) {
    updateDialogActions(
      <el-button
        onClick={() => {
          goEdit('')
        }}
        key="emsEdit"
      >
        编辑
      </el-button>,
      <el-button
        onClick={async () => {
          await EmsListApi.submitSend(props.id)
          message.success('提交成功')
          refreshInfo()
          emsDialogRef.value?.open(props.id, 1)
        }}
        key="emsSubmit"
      >
        提交
      </el-button>
    )
  }
  if (
    [SendStatusEnum.SENT_OUT.status].includes(pageInfo.value?.sendStatus) &&
    checkPermi(['ems:send:import'])
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          emsDialogRef.value?.open(props.id, 1)
        }}
        key="emsimport"
      >
        回填费用
      </el-button>
    )
  }
  if (
    [SendStatusEnum.WAITINGG_SEND.status].includes(pageInfo.value?.sendStatus) &&
    checkPermi(['ems:send:upload-number'])
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          goChange('上传单号')
        }}
        key="emsupload"
      >
        上传单号
      </el-button>
    )
  }
  if (
    [SendStatusEnum.WAITINGG_SEND.status, SendStatusEnum.SENT_OUT.status, 6].includes(pageInfo.value?.sendStatus) &&
    checkPermi(['ems:send:update-vender'])
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          changeVenderDialogRef.value?.open()
        }}
        key="emsChangeVender"
      >
        修改快递公司
      </el-button>
    )
  }
}

const refreshInfo = async () => {
  pageInfo.value = props.id
    ? await EmsListApi.getEmSDetail(props.id)
    : await EmsListApi.getEmSAuditDetail(query.id)
  pageInfo.value.feeShare = pageInfo.value.feeShare == null ? {} : pageInfo.value.feeShare
  setBtns()
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
    field: 'venderShortName',
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
  await getPageInfo()
})
</script>
<style lang="scss" scoped></style>
