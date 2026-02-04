<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="examineApprove"
    :rejectApi="examineReject"
    :auditable="reportDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :submit="{
      permi: 'system:poi-report:submit',
      handler: () => {}
    }"
    :cancel="{
      permi: 'system:poi-report:submit',
      handler: () => {}
    }"
    :edit="{
      permi: 'system:poi-report:update',
      handler: () => goEdit('模板')
    }"
    :approve="{
      permi: 'system:poi-report:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="基本信息"
      :data="reportDetail"
      :items="BasicInfoSchema"
    />
    <eplus-description
      title="附件信息"
      :data="reportDetail"
      :items="annexSchemas"
    >
      <template #annex>
        <el-tag
          type="primary"
          v-for="item in reportDetail.annex"
          :key="item.id"
        >
          <span
            style="cursor: pointer; color: #333"
            @click="handleDownload(item)"
            >{{ item.name }}</span
          >
        </el-tag>
      </template>
    </eplus-description>
  </eplus-detail>
</template>
<script setup lang="tsx">
import { examineApprove, examineReject } from '@/api/audit/poireport-model'
import * as ReportApi from '@/api/system/poiReport'

const message = useMessage()

let reportDetail = reactive({}) // 详情
const showProcessInstanceTaskListFlag = ref(true)
const loading = ref(true)
const outDialogFlag = ref(false)
const { query } = useRoute()

const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
const handleDownload = (item) => {
  window.location.href = `${import.meta.env.VITE_BASE_URL}/admin-api${item.fileUrl}`
}
const getDetail = async () => {
  loading.value = true
  try {
    reportDetail = props.id
      ? await ReportApi.getReport({ id: props.id })
      : await ReportApi.getAuditReport({ id: query?.id })
  } finally {
    loading.value = false
  }
}
const BasicInfoSchema = [
  {
    field: 'code',
    label: '模板编号'
  },
  {
    field: 'name',
    label: '模板名称'
  },
  {
    field: 'businessType',
    label: '模板业务类型',
    dictType: DICT_TYPE.REPORT_BUSINESS_TYPE
  }
]
//附件信息
const annexSchemas = [
  {
    field: 'annex',
    label: '',
    slotName: 'annex',
    span: 24
  }
]
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const handleUpdate = async () => {
  await getDetail()
}
onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('客户ID不能为空')
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
  await getDetail()
})
</script>
<style lang="scss" scoped></style>
