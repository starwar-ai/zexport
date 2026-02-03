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
      permi: 'qms:quality-inspection:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <!-- 质检单信息 -->
    <eplus-description
      title="质检单信息"
      :data="pageInfo"
      :items="basicInfo"
    >
      <template #amount>
        <EplusMoneyLabel :val="pageInfo?.amount" />
        <el-button @click="setAmount">修改验货金额</el-button>
      </template>
      <template #reinspectionFlag>
        {{ pageInfo?.reinspectionFlag ? '是' : '否' }}
      </template>
      <!-- 质检单附件 -->
      <template #annex>
        <UploadList
          :fileList="pageInfo.annex"
          disabled
        />
      </template>
      <template #acceptDesc>
        {{ pageInfo.acceptDesc || '无' }}
      </template>
      <template #guaranteeLetter>
        <UploadList
          :fileList="pageInfo.guaranteeLetter"
          disabled
        />
      </template>
      <template #picture>
        <UploadPic
          :pictureList="pageInfo?.picture"
          disabled
        />
      </template>
    </eplus-description>

    <!-- 供应商信息 -->
    <eplus-description
      title="供应商信息"
      :data="pageInfo"
      :items="supplierInfo"
    />

    <!-- 验货信息列表 待验货和上游变更不展示-->

    <el-tabs
      v-model="activeName"
      class="demo-tabs"
      @tab-click="handleClick"
    >
      <el-tab-pane
        label="验货明细"
        name="first"
      >
        <!-- v-if="!(pageInfo.qualityInspectionStatus == 2 || pageInfo.qualityInspectionStatus == 3)" -->
        <!-- 验货明细  附件  待验货和上游变更不展示 -->
        <eplus-description
          title=""
          :data="pageInfo"
          :items="annexInfo"
        >
          <template #resultAnnex>
            <!-- 附件 -->
            <UploadList
              :fileList="pageInfo.resultAnnex"
              disabled
            />
          </template>
        </eplus-description>
        <InspectInfoList :info="pageInfo" />
      </el-tab-pane>
      <el-tab-pane
        label="操作记录"
        name="third"
      >
        <eplus-operate-log :logList="pageInfo?.operateLogRespDTOList" />
      </el-tab-pane>
    </el-tabs>
  </eplus-detail>
  <!-- 返工放行 -->
  <ReworkReleaseDialog
    ref="reworkReleaseRef"
    @success="close"
    @handle-success="close"
    :key="Date.now()"
  />

  <!-- 审批 -->
  <!-- <InspectAuditForm
    ref="formRef"
    @success="refreshInfo"
  /> -->

  <SetPriceCom
    ref="setPriceComRef"
    @sure="refreshInfo"
  />
</template>
<script setup lang="tsx">
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import InspectInfoList from './components/InspectInfoList.vue'
import * as QualityInspectionApi from '@/api/qms/qualityinspection' // 验货单接口集合
import { examineApprove, examineReject } from '@/api/audit/inspect'
import ReworkReleaseDialog from './components/ReworkReleaseDialog.vue' // 返工放行弹框
import SetPriceCom from './components/SetPriceCom.vue'
import { checkPermi } from '@/utils/permission'
import download from '@/utils/download'
import { QualityInspectStatusEnum } from '@/utils/constants'

const activeName = ref('first')
const auditStatusFlag = ref(false)

const handleClick = (val) => {
  // console.log(val)
}

const message = useMessage()
const pageInfo = ref<typeObj>({})
// 返工/放行
const reworkReleaseRef = ref()

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

const { close, goChange, goConfirm } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goChange: (val) => void
      goConfirm: (val) => void
    })
  : { close: () => {}, goConfirm: () => {}, goChange: () => {} }

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }

// 审批传参
const auditForm = reactive({
  id: props.id,
  reason: '',
  auditName: '',
  startUserId: ''
}) // 审批任务的表单

const loading = ref(true)
const { query } = useRoute()

// 返工重验/让步放行
const handleReworkRelease = (row, type) => {
  reworkReleaseRef.value?.open(row, type)
}

//作废
const closeQualityInspection = (id) => {
  ElMessageBox.confirm('是否确认作废？', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      await QualityInspectionApi.closeQualityInspection(id)
      message.success('作废成功')
    })
    .catch(() => {})
}
const getPageInfo = async () => {
  loading.value = true
  try {
    await refreshInfo()
    let { qualityInspectionStatus } = pageInfo.value
    let { children } = pageInfo.value
    if (
      [
        QualityInspectStatusEnum.WAITING_FOR_INSPECTION.status //待验货
      ].includes(qualityInspectionStatus) &&
      checkPermi(['qms:quality-inspection:uploadInspection'])
    ) {
      updateDialogActions(
        <el-button
          onClick={() => {
            goChange(props.id, '')
          }}
          key="uploadInspection"
        >
          验货
        </el-button>
      )
    }
    if (
      [
        QualityInspectStatusEnum.WAITING_FOR_CONFIRMATION.status //上游变更
      ].includes(qualityInspectionStatus) &&
      checkPermi(['qms:quality-inspection:verification'])
    ) {
      updateDialogActions(
        <el-button
          onClick={() => {
            goConfirm('验货单')
          }}
          key="verification"
        >
          确认排验
        </el-button>
      )
    }
    if (checkPermi(['qms:quality-inspection:export'])) {
      updateDialogActions(
        <el-button
          onClick={async () => {
            await exportExcel()
          }}
          key="export"
        >
          导出
        </el-button>
      )
    }
    if (
      [
        QualityInspectStatusEnum.WAITING_FAILED.status, //验货不通过
        QualityInspectStatusEnum.COMPLETED.status, //部分通过
        QualityInspectStatusEnum.WAITING_FOR_INSPECTION.status,
        QualityInspectStatusEnum.WAITING_FOR_CONFIRMATION.status
      ].includes(qualityInspectionStatus) &&
      checkPermi(['qms:quality-inspection:close'])
    ) {
      updateDialogActions(
        <el-button
          onClick={async () => {
            await closeQualityInspection(props.id)
          }}
          key="close"
        >
          作废
        </el-button>
      )
    }
    if (
      children.some((item) => [1, 2].includes(item?.inspectionStatus) && item?.handleFlag === 0) &&
      checkPermi(['qms:quality-inspection:rework'])
    ) {
      updateDialogActions(
        <el-button
          onClick={async () => {
            await handleReworkRelease(pageInfo.value, 1)
          }}
          key="rework"
        >
          返工重验
        </el-button>
      )
      if (
        children.some(
          (item) => [1, 2].includes(item?.inspectionStatus) && item?.handleFlag === 0
        ) &&
        checkPermi(['qms:quality-inspection:release'])
      ) {
        updateDialogActions(
          <el-button
            onClick={async () => {
              await handleReworkRelease(pageInfo.value, 2)
            }}
            key="release"
          >
            让步放行
          </el-button>
        )
      }
    }
  } finally {
    loading.value = false
  }
}

const exportExcel = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    const data = await QualityInspectionApi.exportExcel({
      id: props?.id,
      reportCode: 'quality-inspection-export'
    })
    if (data && data.size) {
      download.excel(data, `验货单${pageInfo.value.code}.xlsx`)
    }
  } catch {}
  return
}
const setPriceComRef = ref()
const setAmount = () => {
  setPriceComRef.value?.open(pageInfo.value)
}

const refreshInfo = async () => {
  pageInfo.value = await QualityInspectionApi.getQualityInspectionDetail(props.id)

  pageInfo.value.children.forEach((item) => (item.itemAmount = item.totalPrice.amount))
  auditStatusFlag.value = pageInfo.value.auditStatus == 1 ? true : false
}

// 验货明细-附件
const annexInfo: EplusDescriptionItemSchema[] = [
  {
    slotName: 'resultAnnex',
    field: 'resultAnnex',
    label: '验货结果附件',
    span: 24
  }
]

// 质检信息
const basicInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'code',
    label: '验货单号',
    disabled: false // 用于区分哪个现实哪个不显示
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同号'
  },
  {
    field: 'sourceType',
    label: '单据来源',
    dictType: DICT_TYPE.SOURCE_FLAG
  },
  {
    field: 'inspectionType',
    label: '验货方式',
    dictType: DICT_TYPE.INSPECTION_TYPE
  },
  {
    field: 'inspectionNode',
    label: '验货节点',
    dictType: DICT_TYPE.INSPECTION_NODE
  },

  {
    // slotName: 'expectInspectionTime',
    field: 'expectInspectionTime',
    label: '期望验货时间',
    type: 'date'
  },

  {
    field: 'inspectionTime',
    label: '实际验货时间',
    type: 'date'
  },
  {
    field: 'planInspectionTime',
    label: '计划验货时间',
    type: 'date'
  },
  {
    field: 'createTime',
    label: '创建时间',
    type: 'date'
  },
  {
    field: 'applyInspectorName',
    label: '申请验货人'
  },

  {
    slotName: 'reinspectionFlag',
    field: 'reinspectionFlag',
    label: '是否重验'
  },
  {
    field: 'inspectorName',
    label: '验货人'
  },
  {
    field: 'specialAttentionNotice',
    label: '特别注意事项'
  },
  {
    slotName: 'amount',
    field: 'amount',
    label: '验货金额'
  },
  {
    field: 'saleContractCode',
    label: '销售合同'
  },
  {
    field: 'custName',
    label: '客户名称'
  },
  {
    field: 'purchaseUser',
    label: '采购员',
    formatter: (val) => {
      return val?.nickname
    }
  },
  {
    field: 'sales',
    label: '业务员',
    formatter: (val) => {
      return val?.nickname
    }
  },
  {
    field: 'sourceCode',
    label: '关联验货单'
  },
  {
    field: 'qualityInspectionStatus',
    label: '状态',
    dictType: DICT_TYPE.QUALITY_INSPECTION_STATUS
  },
  {
    field: 'remark',
    label: '备注',
    xl: 8,
    lg: 12
  },
  {
    slotName: 'annex',
    field: 'annex',
    label: '附件',
    span: 24
  },
  {
    slotName: 'acceptDesc', // 接受说明
    field: 'acceptDesc',
    label: '接受说明',
    span: 24
  },
  {
    slotName: 'guaranteeLetter',
    field: 'guaranteeLetter',
    label: '工厂保函',
    span: 24
  },
  {
    slotName: 'picture',
    field: 'picture',
    label: '图片',
    span: 24
  }
]

// 供应商信息
const supplierInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'venderName',
    label: '供应商'
  },
  {
    field: 'factoryContacter',
    label: '工厂联系人'
  },
  {
    field: 'factoryTelephone',
    label: '联系电话'
  },
  {
    field: 'inspectionAddress',
    label: '工厂地址'
  }
  // {
  //   slotName: 'annex',
  //   field: 'annex',
  //   label: '附件'
  // },
  // {
  //   slotName: 'picture',
  //   field: 'picture',
  //   label: '图片'
  // }
]

const getPurchaseContractDetail = () => {}
const handleUpdate = async () => {
  await getPurchaseContractDetail()
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
<style lang="scss" scoped></style>
