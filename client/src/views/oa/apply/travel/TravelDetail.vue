<template>
  <eplus-detail
    v-if="!loading"
    :page="pageFlag"
    ref="eplusDetailRef"
    :approveApi="TravelApplyApi.approveTravelApply"
    :rejectApi="TravelApplyApi.rejectTravelApply"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :approve="{
      permi: 'oa:travel-apply:audit',
      handler: () => {}
    }"
    :cancel="{
      permi: 'oa:travel-apply:submit',
      handler: () => {}
    }"
    :edit="{
      permi: 'oa:travel-apply:update',
      handler: () => goEdit()
    }"
    @update="handleUpdate"
  >
    <eplus-description
      title="单据信息"
      :data="pageInfo"
      :items="applyInfo"
    >
      <!-- <template #reimbUserName>
        {{ pageInfo.reimbUser.nickname }} ({{ pageInfo.reimbUser.deptName || '-' }})
      </template> -->
    </eplus-description>

    <eplus-description
      title="基本信息"
      :data="pageInfo"
      :items="basicInfo"
    >
      <template #travelEntourage>
        <el-tag
          class="mr5px"
          v-for="item in pageInfo.travelEntourage"
          :key="item.userId"
        >
          {{ item.nickname }}
        </el-tag>
      </template>
    </eplus-description>

    <FeeShareDetail
      v-if="isValidArray(pageInfo.feeShare)"
      :info="pageInfo.feeShare"
      :feeType="0"
    />
    <div
      v-else
      class="mb10px text-red"
      >该报销单未进行费用归集
    </div>
    <template #otherAction>
      <component
        v-for="(item, index) in btnList"
        :key="index"
        :is="item"
      />
    </template>
  </eplus-detail>
  <OtherActionCom
    ref="otherActionComRef"
    @success="handleUpdate"
  />
</template>
<script setup lang="tsx">
import * as TravelApplyApi from '@/api/oa/travelApply'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import FeeShareDetail from '@/views/ems/send/components/FeeShare/FeeShareDetail.vue'
import { isValidArray } from '@/utils/is'
import { DICT_TYPE } from '@/utils/dict'
import OtherActionCom from './OtherActionCom.vue'

const message = useMessage()
const pageInfo: any = ref({}) // 借款申请单详情
const eplusDetailRef = ref()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const editFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
const pageId = ref()
const pageFlag = ref(false)
defineOptions({ name: 'TravelmentExpensesDetail' })
const btnList = ref<any[]>([])
//定义edit事件

// const { updateDialogActions, clearDialogActions } =
//   props.id && !useRoute().params.id
//     ? (inject('dialogActions') as {
//         updateDialogActions: (...args: any[]) => void
//         clearDialogActions: () => void
//       })
//     : useRoute().params.id
//       ? {
//           updateDialogActions: (...args: any[]) => {
//             btnList.value.push(...args)
//           },
//           clearDialogActions: () => {
//             btnList.value.splice(0, btnList.value.length)
//           }
//         }
//       : { updateDialogActions: () => {}, clearDialogActions: () => {} }

const loading = ref(true)
const { query } = useRoute()
const otherActionComRef = ref()
const goEdit = () => {
  otherActionComRef.value.handleUpdate(pageInfo.value.id)
}

const getPageInfo = async () => {
  loading.value = true
  try {
    pageInfo.value =
      props.id || pageFlag.value
        ? await TravelApplyApi.getTravelApplyInfo({ id: pageId.value })
        : await TravelApplyApi.getTravelApplyAuditInfo({ id: pageId.value })
  } finally {
    loading.value = false
  }
}
const applyInfo: EplusDescriptionItemSchema[] = reactive([
  {
    field: 'code',
    label: '单据编号'
  },
  {
    field: 'auditStatus',
    label: '状态',
    dictType: DICT_TYPE.BPM_PROCESS_INSTANCE_RESULT
  },
  {
    field: 'applyer',
    label: '申请人',
    formatter: (val) => {
      return val?.nickname
    }
  },
  {
    field: 'applyer',
    label: '申请人部门',
    formatter: (val) => {
      return val?.deptName
    }
  },
  {
    field: 'createTime',
    label: '申请日期',
    type: 'date'
  },
  {
    field: 'isApplyExpense',
    label: '是否申请报销',
    dictType: DICT_TYPE.IS_INT
  }
])

const basicInfo: EplusDescriptionItemSchema[] = reactive([
  {
    field: 'travelType',
    label: '出差类型',
    dictType: DICT_TYPE.TRAVEL_TYPE
  },
  {
    field: 'dest',
    label: '出差地点'
  },
  {
    field: 'transportationType',
    label: '交通工具',
    dictType: DICT_TYPE.TRAVAL_TRANSPORTATION_TYPE
  },
  {
    field: 'startTime',
    label: '开始时间',
    type: 'date'
  },
  {
    field: 'endTime',
    label: '结束时间',
    type: 'date'
  },
  {
    field: 'duration',
    label: '出差时长'
  },
  {
    field: 'amount',
    label: '预估费用',
    type: 'money'
  },
  {
    field: 'purpose',
    label: '出差事由'
  },
  {
    field: 'intendedObjectives',
    label: '拟达成目标'
  },
  {
    field: 'remarks',
    label: '备注'
  }
])

const handleApprove = async () => {
  if (editFlag.value) {
    await TravelApplyApi.updateTravelApply(pageInfo.value)
    return true
  }
}

const handleUpdate = async () => {
  await getPageInfo()
}

onMounted(async () => {
  if (useRoute().params.id) {
    pageId.value = useRoute().params.id
    pageFlag.value = true
  } else {
    pageFlag.value = false
    if (query.id) {
      showProcessInstanceTaskListFlag.value = false
      outDialogFlag.value = true
      pageId.value = query.id
    }
    if (props.id) {
      showProcessInstanceTaskListFlag.value = true
      outDialogFlag.value = false
      pageId.value = props.id
    }
  }
  await getPageInfo()
})
</script>
<style lang="scss" scoped>
.total_box {
  display: flex;
  justify-content: space-between;
  height: 40px;
  align-items: center;
}
</style>
