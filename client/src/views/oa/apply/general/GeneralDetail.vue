<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :page="pageFlag"
    :approveApi="OaGeneralApplyApi.approveGeneralApply"
    :rejectApi="OaGeneralApplyApi.rejectGeneralApply"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: 'oa:general-apply:submit',
      handler: () => {}
    }"
    :approve="{
      permi: 'oa:general-apply:audit',
      handler: () => {}
    }"
    :edit="{
      permi: 'oa:general-apply:update',
      handler: () => goEdit()
    }"
    @update="handleUpdate"
  >
    <!-- 信息 -->
    <eplus-description
      title="单据信息"
      :data="pageInfo"
      :items="orderInfo"
    >
      <!-- <div class=""></div> -->
    </eplus-description>
    <eplus-description
      title="基本信息"
      :data="pageInfo"
      :items="basicInfo"
    >
      <!-- <div class=""></div> -->
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
  </eplus-detail>
  <OtherActionCom
    ref="otherActionComRef"
    @success="handleUpdate"
  />
</template>
<script setup lang="tsx">
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import { isValidArray } from '@/utils/is'
import * as OaGeneralApplyApi from '@/api/oa/generalApply/index'
import FeeShareDetail from '@/views/ems/send/components/FeeShare/FeeShareDetail.vue'
import OtherActionCom from './OtherActionCom.vue'

const message = useMessage()
const pageInfo = ref({})
const otherActionComRef = ref<ComponentRef<typeof OtherActionCom>>()
const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const loading = ref(true)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
  readonly?: boolean
}>()
defineOptions({ name: 'ApplyGeneralDetail' })
const pageId = ref()
const pageFlag = ref(false)
const handleUpdate = async () => {
  await getPageInfo()
}
//定义edit事件
const goEdit = () => {
  otherActionComRef.value.handleUpdate(pageInfo.value.id)
}

const { query } = useRoute()

const getPageInfo = async () => {
  loading.value = true
  try {
    await refreshInfo()
  } finally {
    loading.value = false
  }
}

const refreshInfo = async () => {
  pageInfo.value =
    props.id || pageFlag.value
      ? await OaGeneralApplyApi.getGeneralApplyInfo({ id: pageId.value })
      : await OaGeneralApplyApi.getGeneralApplyAuditInfo({ id: pageId.value })
  pageInfo.value.feeShare = pageInfo.value.feeShare == null ? {} : pageInfo.value.feeShare
}

// 信息
const orderInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'code',
    label: '单据编号',
    disabled: false
  },
  {
    field: 'auditStatus',
    label: '状态',
    type: 'date',
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
    label: '申请部门',
    formatter: (val) => {
      return val?.deptName
    }
  }
]
// 信息
const basicInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'amount',
    label: '预估费用金额',
    type: 'money'
  },
  {
    field: 'amountDesc',
    label: '预估费用明细说明'
  },
  {
    field: 'purpose',
    label: '申请事由'
  },
  {
    field: 'remarks',
    label: '备注'
  }
]
const feeInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'companyName',
    label: '费用主体',
    disabled: false
  }
]

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
<style lang="scss" scoped></style>
