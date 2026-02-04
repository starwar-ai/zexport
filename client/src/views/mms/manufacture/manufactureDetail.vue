<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    @update="handleUpdate"
  >
    <eplus-description
      title="基本信息"
      :data="pageInfo"
      :items="basicInfo"
    />
    <el-tabs
      v-model="activeName"
      class="demo-tabs mb20px"
    >
      <el-tab-pane
        label="加工产品"
        name="1"
      >
        <SkuList
          :formData="pageInfo"
          readonly
        />
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
import * as ManufactureApi from '@/api/mms/manufacture/index'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import SkuList from './components/SkuList.vue'
// import { formatDateColumn, formatDictColumn } from '@/utils/table'
// import { isValidArray } from '@/utils/is'

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
defineOptions({ name: 'manufactureDetail' })
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
    pageInfo.value = await ManufactureApi.getManufactureInfo({ id: props.id })
  } finally {
    loading.value = false
  }
}
const activeName = ref('1')
const basicInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'code',
    label: '加工单号'
  },
  {
    field: 'companyName',
    label: '加工主体'
  },
  {
    field: 'stockName',
    label: '加工仓库'
  },
  {
    field: 'custName',
    label: '客户名称'
  },
  {
    field: 'saleContractCode',
    label: '销售合同号'
  },
  {
    field: 'inputTime',
    label: '录入时间',
    type: 'time'
  },
  {
    field: 'inputUserName',
    label: '录入人'
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

  await getPageInfo()
})
</script>
<style lang="scss" scoped></style>
