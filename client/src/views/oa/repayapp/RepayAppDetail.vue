<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="repayapp.approveRepayApp"
    :rejectApi="repayapp.rejectRepayApp"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :submit="{
      permi: 'oa:repay-app:submit',
      handler: () => {}
    }"
    :cancel="{
      permi: 'oa:repay-app:submit'
    }"
    :edit="{
      user: pageInfo.repayer.userId,
      permi: 'oa:repay-app:update',
      handler: () => goEdit('还款单')
    }"
    :approve="{
      permi: 'oa:repay-app:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="借款单信息"
      :data="pageInfo"
      :items="loanInfo"
    />
    <eplus-description
      title="还款信息"
      :data="pageInfo"
      :items="repayInfo"
    >
      <template #repayer> {{ pageInfo.repayer?.nickname }} </template>
      <template #annex>
        <UploadList
          :fileList="pageInfo.annex"
          disabled
        />
      </template>
    </eplus-description>
  </eplus-detail>
</template>
<script setup lang="tsx">
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import * as repayapp from '@/api/oa/repayapp'

const message = useMessage()
const pageInfo = ref({}) // 借款申请单详情
const bankList = ref([])
const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'LoanAppDetail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const loading = ref(true)
const { query } = useRoute()

const getPageInfo = async () => {
  loading.value = true
  try {
    pageInfo.value = props.id
      ? await repayapp.getRepayApp({ id: props.id })
      : await repayapp.getAuditRepayApp({ id: query?.id })
  } catch {
    pageInfo.value = {}
  } finally {
    loading.value = false
  }
}
const loanInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'loanAppCode',
    label: '借款单号'
  },
  {
    field: 'companyName',
    label: '公司主体'
  },
  {
    field: 'loanAmount',
    label: '借款金额',
    type: 'money'
  },
  {
    field: 'outstandingAmount',
    label: '待还金额',
    type: 'money'
  },
  {
    field: 'repayAmount',
    label: '已还金额',
    type: 'money'
  },
  {
    field: 'inRepaymentAmount',
    label: '还款中金额',
    type: 'money'
  }
]

const repayInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'code',
    label: '还款单号'
  },
  {
    field: 'createTime',
    label: '申请时间',
    type: 'time'
  },
  {
    field: 'amount',
    label: '本次还款金额',
    type: 'money'
  },
  {
    field: 'repayType',
    label: '还款方式',
    dictType: DICT_TYPE.LOAN_TYPE
  },
  {
    slotName: 'repayer',
    field: 'repayer',
    label: '申请人'
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
    message.error('还款单ID不能为空')
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
