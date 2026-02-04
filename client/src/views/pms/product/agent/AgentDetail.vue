<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="agentApi.approveApi"
    :rejectApi="agentApi.rejectApi"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: 'pms:agent-sku:submit'
    }"
    :edit="{
      permi: 'pms:agent-sku:update',
      handler: () => goEdit('')
    }"
    :approve="{
      permi: 'pms:agent-sku:audit',
      handler: () => {}
    }"
    :revertAudit="{
      permi: 'pms:agent-sku:anti-audit',
      handler: handleRevertAudit
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="基本信息"
      :data="pageInfo"
      :items="basicInfo"
    />

    <eplus-description
      title=""
      :data="pageInfo"
      :items="basic2Info"
    >
      <template #description>
        <SkuDescriptionCom :text="pageInfo.description" />
      </template>
      <template #descriptionEng>
        <SkuDescriptionCom :text="pageInfo.descriptionEng" />
      </template>
      <template #annex>
        <UploadList
          :fileList="pageInfo.annex"
          disabled
        />
      </template>
      <template #picture>
        <!-- <UploadPic
          :pictureList="pageInfo.picture"
          disabled
        /> -->
        <UploadZoomPic
          v-model="pageInfo.picture"
          disabled
        />
      </template>
    </eplus-description>
    <QueteDetail :info="pageInfo" />
    <HsdataDetail :info="pageInfo" />
    <OtherDetail :info="pageInfo" />
  </eplus-detail>
</template>
<script setup lang="tsx">
import * as agentApi from '@/api/pms/agent/index'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import OtherDetail from '../main/components/OtherDetail.vue'
import QueteDetail from '../main/components/QueteDetail.vue'
import HsdataDetail from '../main/components/HsdataDetail.vue'
import { LengthUnit } from '@/utils/config'
import { checkPermi } from '@/utils/permission'
import SkuDescriptionCom from '../components/skuDescriptionCom.vue'

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
defineOptions({ name: 'AgentDetail' })

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }

//定义edit事件
const { close, goEdit, goChange } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
      goChange: (val) => void
    })
  : { close: () => {}, goEdit: () => {}, goChange: () => {} }

const loading = ref(true)
const { query } = useRoute()
const handleRevertAudit = async () => {
  await agentApi.revertAudit(pageInfo.value?.id)
}
const getPageInfo = async () => {
  loading.value = true
  try {
    pageInfo.value = props.id
      ? await agentApi.getSkuInfo({ id: props.id })
      : await agentApi.getSkuAuditInfo({ id: query?.id })
    pageInfo.value.hsdataCode = pageInfo.value.hsdata?.code
    pageInfo.value.hsdataUnit = pageInfo.value.hsdata?.unit
    pageInfo.value.hsdataTaxRefundRate = pageInfo.value.hsdata?.taxRefundRate

    pageInfo.value.spec = `${pageInfo.value?.specLength}*${pageInfo.value?.specWidth}*${pageInfo.value?.specHeight} ${LengthUnit}`
    if (
      props.id &&
      pageInfo.value.changeStatus !== 2 &&
      pageInfo.value.auditStatus === 2 &&
      checkPermi(['pms:agent-sku:change'])
    ) {
      clearDialogActions()
      updateDialogActions(
        <el-button
          onClick={() => {
            goChange('客户产品变更')
          }}
          key="changeSku"
        >
          变更
        </el-button>
      )
    }
  } finally {
    loading.value = false
  }
}

const basicInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'code',
    label: '产品编号'
  },
  {
    field: 'cskuCode',
    label: '客户货号'
  },
  {
    field: 'custName',
    label: '客户名称'
  },
  {
    field: 'name',
    label: '中文品名'
  },
  {
    field: 'declarationName',
    label: '报关中文品名'
  },
  {
    field: 'nameEng',
    label: '英文品名'
  },
  {
    field: 'customsDeclarationNameEng',
    label: '报关英文品名'
  }
]
const basic2Info: EplusDescriptionItemSchema[] = [
  {
    slotName: 'description',
    field: 'description',
    label: '中文描述',
    span: 12
  },
  {
    slotName: 'descriptionEng',
    field: 'descriptionEng',
    label: '英文描述',
    span: 12
  },
  {
    slotName: 'annex',
    field: 'annex',
    label: '附件',
    span: 24
  },
  {
    slotName: 'picture',
    field: 'picture',
    label: '图片',
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
