<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="skuApi.approveSku"
    :rejectApi="skuApi.rejectSku"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :submit="{
      permi: 'pms:auxiliary-sku:submit',
      handler: () => goEdit('')
    }"
    :cancel="{
      permi: 'pms:auxiliary-sku:submit',
      handler: () => {}
    }"
    :edit="{
      permi: 'pms:auxiliary-sku:update',
      handler: () => goEdit('')
    }"
    :approve="{
      permi: 'pms:auxiliary-sku:audit',
      handler: () => {}
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
      <template #annex>
        <UploadList
          :fileList="pageInfo.annex"
          disabled
        />
      </template>
    </eplus-description>
    <eplus-description
      title="采购记录"
      :data="pageInfo"
      :items="purchaseInfo"
    >
      <template #purchaseInfo>
        <PurchaseInfo
          ref="PurchaseInfoRef"
          v-if="pageInfo.code"
          :code="pageInfo.code"
        />
      </template>
    </eplus-description>
  </eplus-detail>
</template>
<script setup lang="tsx">
import * as skuApi from '@/api/pms/main/index'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import PurchaseInfo from './PurchaseInfo.vue'

const message = useMessage()
const pageInfo = ref<typeObj>({})

let purchaseTable = reactive([])
const inputRef = ref('')
const selectRef = ref('cskuCode')
const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
  readonly?: boolean
}>()
defineOptions({ name: 'MainDetail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const loading = ref(true)
const { query } = useRoute()
const basicInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'name',
    label: '中文品名'
  },
  {
    field: 'nameEng',
    label: '英文品名'
  },
  {
    field: 'measureUnit',
    label: '计量单位',
    dictType: DICT_TYPE.MEASURE_UNIT
  }
]

const basic2Info: EplusDescriptionItemSchema[] = [
  {
    slotName: 'annex',
    field: 'annex',
    label: '附件',
    span: 24
  },
  {
    field: 'picture',
    label: '图片',
    span: 24,
    type: 'img'
  }
]
const purchaseInfo: EplusDescriptionItemSchema[] = [
  {
    slotName: 'purchaseInfo',
    field: 'purchaseInfo',
    label: '',
    span: 24
  }
]
const PurchaseInfoRef = ref()
const getPageInfo = async () => {
  loading.value = true
  try {
    await refreshInfo()
  } finally {
    loading.value = false
  }
}

const refreshInfo = async () => {
  pageInfo.value = props.id
    ? await skuApi.getSkuInfo({ id: props.id })
    : await skuApi.getSkuAuditInfo({ id: query?.id })
  pageInfo.value.hsdataCode = pageInfo.value.hsdata?.code
  pageInfo.value.hsdataUnit = pageInfo.value.hsdata?.unit
  pageInfo.value.hsdataTaxRefundRate = pageInfo.value.hsdata?.taxRefundRate
}
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
<style lang="scss" scoped></style>
