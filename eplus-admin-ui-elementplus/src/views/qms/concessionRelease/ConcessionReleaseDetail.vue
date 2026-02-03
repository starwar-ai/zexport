<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="ConcessionReleaseApi.approveConcessionRelease"
    :rejectApi="ConcessionReleaseApi.rejectConcessionRelease"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :approve="{
      permi: 'scm:concession-release:audit',
      handler: () => {}
    }"
    :cancel="{
      permi: 'scm:concession-release:submit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="验货明细"
      :data="pageInfo"
      :items="inspectInfo"
    >
      <template #annex>
        <UploadList
          :fileList="pageInfo.annex"
          disabled
        />
      </template>
      <template #children>
        <Table
          style="width: 100%"
          :columns="quoteitemListColumns"
          headerAlign="center"
          align="center"
          :data="pageInfo?.children"
        />
      </template>
      <template #concessionReleaseAnnex>
        <UploadList
          :fileList="pageInfo.concessionReleaseAnnex"
          disabled
        />
      </template>

      <template #picture>
        <UploadZoomPic
          v-model="pageInfo.picture"
          disabled
        />
      </template>
    </eplus-description>
    <!-- 质检单信息 -->
    <eplus-description
      title="质检单信息"
      :data="pageInfo"
      :items="basicInfo"
    >
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
  </eplus-detail>
</template>
<script setup lang="tsx">
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import { formatDictColumn } from '@/utils/table'
import * as ConcessionReleaseApi from '@/api/qms/concessionRelease/index'
import UploadList from '@/components/UploadList/index.vue'

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
defineOptions({ name: 'ConcessionReleaseDetail' })
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

const loading = ref(true)
const { query } = useRoute()

const getPageInfo = async () => {
  loading.value = true
  try {
    pageInfo.value = props.id
      ? await ConcessionReleaseApi.getConcessionRelease({ id: props.id })
      : await ConcessionReleaseApi.getAuditConcessionRelease({ id: query?.id })
  } finally {
    loading.value = false
  }
}
const inspectInfo: EplusDescriptionItemSchema[] = [
  {
    slotName: 'annex',
    field: 'annex',
    label: '验货结果附件',
    span: 24
  },
  {
    slotName: 'children',
    field: 'children',
    label: '验货明细',
    span: 24
  },
  {
    field: 'annexFlag',
    label: '是否工厂保函',
    dictType: DICT_TYPE.IS_INT
  },
  {
    field: 'description',
    label: '让步描述'
  },
  {
    slotName: 'picture',
    field: 'picture',
    label: '图片',
    span: 24
  },
  {
    slotName: 'concessionReleaseAnnex',
    field: 'concessionReleaseAnnex',
    label: '工厂保函附件',
    span: 24
  }
]
const quoteitemListColumns = [
  {
    label: '序号',
    type: 'index',
    fixed: 'left',
    width: 60
  },
  {
    label: '验货结果',
    field: 'inspectionStatus',
    formatter: formatDictColumn(DICT_TYPE.INSPECT_RESULT_TYPE)
  },
  {
    label: '问题描述',
    field: 'failDesc'
  },
  {
    label: '历史出现问题',
    field: 'lastFailDesc',
    width: '200'
  },
  {
    label: '基础产品编号',
    field: 'basicSkuCode'
  },
  {
    label: '客户货号',
    field: 'cskuCode'
  },
  {
    label: '产品名称',
    field: 'skuName'
  },

  {
    label: '数量',
    field: 'quantity'
  },
  {
    label: '外箱数量',
    field: 'qtyPerOuterbox'
  },

  {
    label: '计算箱数',
    field: 'boxCount'
  },

  {
    label: '备注',
    field: 'remark'
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
    field: 'venderName',
    label: '供应商'
  },
  {
    slotName: 'reinspectionFlag',
    field: 'reinspectionFlag',
    label: '是否重验'
  },
  {
    field: 'inspectionType',
    label: '验货方式',
    dictType: DICT_TYPE.INSPECTION_TYPE
  },
  {
    field: 'specialAttentionNotice',
    label: '特别注意事项'
  },
  {
    field: 'applyInspectorName',
    label: '申请验货人'
  },
  {
    field: 'inspectorName',
    label: '验货人'
  },
  {
    field: 'inspectionTime',
    label: '实际验货时间',
    type: 'date'
  },
  {
    field: 'totalPriceAmount',
    label: '验货金额'
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
  }
]

const handleUpdate = async () => {
  await getPageInfo()
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
