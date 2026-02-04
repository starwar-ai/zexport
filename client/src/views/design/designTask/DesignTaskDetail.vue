<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineApprove"
    :rejectApi="examineReject"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :approve="{
      permi: 'dtms:design:audit',
      handler: () => {}
    }"
    :cancel="{
      permi: 'dtms:design:submit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <!-- 基本信息 -->
    <eplus-description
      title="基本信息"
      :data="pageInfo"
      :items="basicInfo"
    >
      <template #specialPermissionFlag>
        {{ pageInfo?.specialPermissionFlag == 1 ? '是' : '否' }}
      </template>

      <template #designType>
        <span
          v-for="item in pageInfo?.designType"
          :key="item"
        >
          <DictTag
            :type="DICT_TYPE.DESIGN_TYPE"
            :value="item"
          />&nbsp;
        </span>
      </template>

      <template #annex>
        <UploadList
          :fileList="pageInfo.annex"
          disabled
        />
      </template>
      <template #planInspectionTime>
        <el-date-picker
          v-model="pageInfo.plannedArrivalTime"
          disabled
          clearable
          valueFormat="x"
          type="date"
          style="width: 100%"
        />
      </template>
    </eplus-description>

    <el-tabs v-model="activeName">
      <el-tab-pane
        label="认领记录"
        name="1"
      >
        <InspectInfoList :info="pageInfo.designItemRespVOList" />
      </el-tab-pane>
      <el-tab-pane
        label="进度说明"
        name="2"
      >
        <SummaryList :info="pageInfo.summaryRespVOList" />
      </el-tab-pane>
      <el-tab-pane
        label="操作记录"
        name="3"
      >
        <eplus-operate-log :logList="pageInfo?.operateLogRespDTOList" />
      </el-tab-pane>
    </el-tabs>
  </eplus-detail>

  <DesignTaskDialog
    ref="designTaskDialogRef"
    :key="Date.now()"
    @handle-success="close"
  />
</template>
<script setup lang="tsx">
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import InspectInfoList from './components/InspectInfoList.vue'
import SummaryList from './components/SummaryList.vue'
import * as DesignTaskApi from '@/api/dtms/designTask' // 设计任务单接口集合
import { examineApprove, examineReject } from '@/api/audit/dtms'
import DesignTaskDialog from './components/DesignTaskDialog.vue' //  弹框
import { useUserStore } from '@/store/modules/user'
import { checkPermi } from '@/utils/permission'

const activeName = ref('1')

const message = useMessage()
const pageInfo: any = ref({})

const designTaskDialogRef = ref()

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
  readonly?: boolean
}>()
defineOptions({ name: 'InspectDetail' })

const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

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
    if (outDialogFlag.value) {
      pageInfo.value = await DesignTaskApi.getDesignTaskAuditDetail(query.id)
      pageInfo.value.designType = pageInfo.value.designType.split(',')
    } else {
      await refreshInfo()
    }
  } finally {
    loading.value = false
  }
}

const handleReworkRelease = (id, type) => {
  designTaskDialogRef.value?.open(id, type)
}

const currEvalStatus = ref(false)
const currCaseStatus = ref(false)
const refreshInfo = async () => {
  pageInfo.value = await DesignTaskApi.designDetail(props.id)
  pageInfo.value.designType = pageInfo.value.designType.split(',')
  let designerId = pageInfo.value.designerIds?.split(',')
  let userId = useUserStore().getUser.id + ''
  let reasonItem = basicInfo.find((item) => item.field == 'specialPermissionReason')
  reasonItem!.disabled =
    getDictLabel(DICT_TYPE.APPROVED_TYPE, pageInfo.value?.specialPermissionFlag) == '特批'
      ? false
      : true
  if (pageInfo.value.designStatus == 3 && designerId != undefined) {
    currEvalStatus.value = designerId.includes(userId) ? true : false
  }
  if (pageInfo.value.designStatus == 4) {
    currCaseStatus.value = userId == pageInfo.value.applyDesignerId ? true : false
  }

  let { designStatus } = pageInfo.value
  if ((designStatus == 1 || designStatus == 7) && checkPermi(['dtms:design:update'])) {
    updateDialogActions(
      <el-button
        onClick={() => {
          goEdit('编辑设计单')
        }}
        key="uploadInspection"
      >
        编辑
      </el-button>
    )
  }
  if (currEvalStatus.value && checkPermi(['dtms:design:speed'])) {
    updateDialogActions(
      <el-button
        onClick={() => {
          handleReworkRelease(props.id, 2)
        }}
        key="speed"
      >
        填写任务进度
      </el-button>
    )
  }
  if (currEvalStatus.value && checkPermi(['dtms:design:complete'])) {
    updateDialogActions(
      <el-button
        onClick={() => {
          handleReworkRelease(props.id, 1)
        }}
        key="complete"
      >
        完成任务
      </el-button>
    )
  }
  if (currCaseStatus.value && checkPermi(['dtms:design:evaluate'])) {
    updateDialogActions(
      <el-button
        onClick={() => {
          handleReworkRelease(props.id, 3)
        }}
        key="evaluate"
      >
        评价
      </el-button>
    )
  }
  if (currCaseStatus.value && checkPermi(['dtms:design:case'])) {
    updateDialogActions(
      <el-button
        onClick={() => {
          handleReworkRelease(props.id, 4)
        }}
        key="caseInspect"
        hasPermi="dtms:design:case"
      >
        作废
      </el-button>
    )
  }
}

const basicInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'code',
    label: '设计任务单号',
    disabled: false // 用于区分哪个现实哪个不显示
  },
  {
    field: 'name',
    label: '任务名称'
  },
  {
    slotName: 'designType',
    field: 'designType',
    label: '任务类型'
    // dictType: DICT_TYPE.DESIGN_TYPE
  },
  {
    field: 'specialPermissionFlag',
    label: '紧急程度',
    dictType: DICT_TYPE.APPROVED_TYPE
  },
  {
    field: 'specialPermissionReason',
    label: '特批原因',
    disabled: true
  },
  {
    field: 'contractType',
    label: '合同类型',
    dictType: DICT_TYPE.CONTRACT_TYPE
  },
  {
    field: 'contractCode',
    label: '合同编号'
  },

  {
    field: 'expectCompleteDate',
    label: '希望完成日期',
    type: 'date'
  },
  {
    field: 'completeDate',
    label: '实际完成日期',
    type: 'date'
  },
  {
    field: 'applyDesignerName',
    label: '申请人'
  },
  {
    field: 'applyTime',
    label: '申请日期',
    type: 'date'
  },
  {
    field: 'designStatus',
    label: '单据状态',
    dictType: DICT_TYPE.DESIGN_STATUS
  },
  {
    field: 'specialPermissionFlag',
    label: '紧急程度',
    dictType: DICT_TYPE.APPROVED_TYPE
  },
  {
    field: 'designRequirement',
    label: '设计要求',
    span: 24
  },
  {
    field: 'materialDesc',
    label: '素材说明',
    span: 24
  },

  {
    field: 'remark',
    label: '备注',
    span: 24
  },
  {
    slotName: 'annex',
    field: 'annex',
    label: '附件',
    span: 24
  }
]

const handleUpdate = async () => {
  await refreshInfo()
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
  await getPageInfo()
})
</script>
<style lang="scss" scoped>
.type-span {
  display: inline-blikc;
  background: #ecf5ff;
  color: #409eff;
  padding: 4px 7px;
  font-size: 12px;
  margin: 0 2px;
  border-radius: 4px;
}
</style>
