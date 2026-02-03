<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="ExhibitionApi.approveExhibition"
    :rejectApi="ExhibitionApi.rejectExhibition"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :submit="{
      permi: 'exms:exhibition:submit',
      handler: () => {}
    }"
    :cancel="{
      permi: 'exms:exhibition:submit',
      handler: () => {}
    }"
    :edit="{
      permi: 'exms:exhibition:update',
      handler: () => goEdit('')
    }"
    :approve="{
      permi: 'exms:exhibition:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title=""
      :data="pageInfo"
      :items="basicInfo"
    >
      <template #budget> {{ pageInfo?.budget?.amount }} {{ pageInfo?.budget?.currency }} </template>
      <template #planDate>
        {{ formatTime(pageInfo?.planDate[0], 'yyyy-MM-dd') }} -
        {{ formatTime(pageInfo?.planDate[1], 'yyyy-MM-dd') }}
      </template>
      <template #stallThemeList>
        {{ getDictLabels(DICT_TYPE.EXMS_STALL_THEME, pageInfo?.stallThemeList) }}
      </template>
      <template #deptList>
        {{ pageInfo?.deptMsgList.map((item) => item.name).join(',') }}
      </template>
    </eplus-description>
  </eplus-detail>
</template>
<script setup lang="tsx">
import * as ExhibitionApi from '@/api/exms/exhibition/index'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE, getDictLabels } from '@/utils/dict'
import { formatTime } from '@/utils/index'

const message = useMessage()
const pageInfo: any = ref({
  budget: {}
})

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'ExhibitionDetail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

// const { updateDialogActions, clearDialogActions } = inject('dialogActions') as {
//   updateDialogActions: (...args: any[]) => void
//   clearDialogActions: () => void
// }

const loading = ref(true)
const { query } = useRoute()

const getPageInfo = async () => {
  loading.value = true
  try {
    pageInfo.value = props.id
      ? await ExhibitionApi.getExhibitionInfo({ id: props.id })
      : await ExhibitionApi.getExhibitionAuditInfo({ id: query?.id })
  } catch {
    pageInfo.value = {}
  } finally {
    loading.value = false
  }
}

const basicInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'companyName',
    label: '公司主体'
  },
  {
    field: 'name',
    label: '展会名称'
  },
  {
    slotName: 'deptList',
    field: 'deptList',
    label: '费用承担部门'
  },
  {
    slotName: 'budget',
    field: 'budget',
    label: '展会预算'
  },
  {
    field: 'theme',
    label: '展会主题',
    dictType: DICT_TYPE.EXMS_EXHIBITION_THEME
  },
  {
    slotName: 'stallThemeList',
    field: 'stallThemeList',
    label: '摊位主题'
  },
  {
    field: 'countryName',
    label: '国家/地区'
  },
  {
    field: 'cityName',
    label: '城市'
  },
  {
    field: 'exmsEventCategoryName',
    label: '展会系列'
  },
  {
    slotName: 'planDate',
    field: 'planDate',
    label: '计划日期'
  },
  {
    field: 'stallArea',
    label: '摊位面积'
  },
  {
    field: 'ownerUserName',
    label: '展会负责人'
  },
  {
    field: 'ownerDeptName',
    label: '负责人部门'
  },
  {
    field: 'applyUserName',
    label: '申请人'
  },
  {
    field: 'applyDeptName',
    label: '申请人部门'
  },
  {
    field: 'applyDate',
    label: '申请日期',
    type: 'date'
  },
  {
    field: 'remark',
    label: '备注',
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
