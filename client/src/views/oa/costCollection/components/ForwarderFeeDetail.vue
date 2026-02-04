<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
  >
    <eplus-description
      title="基础信息"
      :data="pageInfo"
      :items="pageSchemas"
    />
    <FeeShareDetail
      v-if="isValidArray(pageInfo.feeShare)"
      :info="pageInfo.feeShare"
    />
  </eplus-detail>
</template>
<script setup lang="tsx">
import { forwarderFeeDetail } from '@/api/dms/forwarderCompanyInfo'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDialog } from '@/components/EplusDialog'
import FeeShareDetail from '@/views/ems/send/components/FeeShare/FeeShareDetail.vue'
import { isValidArray } from '@/utils/is'
import { formatNum, getCurrency } from '@/utils'
import { moneyTotalPrecision } from '@/utils/config'

const message = useMessage()
const pageInfo = ref()
const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
  param: any
  status: any
}>()
defineOptions({ name: 'ForwarderFeeDetail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const loading = ref(true)
const { query } = useRoute()

const getInfo = async () => {
  loading.value = true
  try {
    pageInfo.value = await forwarderFeeDetail({ id: props.id })
  } catch {
    pageInfo.value = {}
  } finally {
    loading.value = false
  }
}

const pageSchemas = [
  {
    field: 'applyer',
    label: '申请人',
    formatter: (val) => {
      return val?.nickname
    }
  },
  {
    field: 'amount',
    label: '费用金额',
    formatter: (val) => {
      return `${getCurrency(val.currency)} ${formatNum(val.amount, moneyTotalPrecision)}`
    }
  },
  {
    field: 'venderName',
    label: '船代公司'
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
  await getInfo()
})
</script>
<style lang="scss" scoped></style>
