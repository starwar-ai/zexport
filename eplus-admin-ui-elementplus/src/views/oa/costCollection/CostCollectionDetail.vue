<template>
  <SendDetail
    v-if="pageBusinessType === CostTypes.SEND.status && pageId"
    :param="pageParam"
    :status="pageStatus"
    :id="pageId"
    :auditInfo="auditInfo"
  />
  <GeneralReimbDetail
    v-if="pageBusinessType === CostTypes.GENERAL.status && pageId"
    :param="pageParam"
    :status="pageStatus"
    :id="pageId"
    :auditInfo="auditInfo"
  />
  <TravelReimbDetail
    v-if="pageBusinessType === CostTypes.TRAVELREIMB.status && pageId"
    :param="pageParam"
    :status="pageStatus"
    :id="pageId"
    :auditInfo="auditInfo"
  />
  <CorporatePaymentsDetail
    v-if="pageBusinessType === CostTypes.CORPORATE.status && pageId"
    :param="pageParam"
    :status="pageStatus"
    :id="pageId"
    :auditInfo="auditInfo"
  />
  <EntertainmentExpensesDetail
    v-if="pageBusinessType === CostTypes.ENTERTAINMENT.status && pageId"
    :param="pageParam"
    :status="pageStatus"
    :id="pageId"
    :auditInfo="auditInfo"
  />
  <ForwarderFeeDetail
    v-if="pageBusinessType === CostTypes.FORWARDER.status && pageId"
    :param="pageParam"
    :status="pageStatus"
    :id="pageId"
  />
  <OtherReimbDetail
    v-if="pageBusinessType === CostTypes.OTHER.status && pageId"
    :param="pageParam"
    :status="pageStatus"
    :id="pageId"
    :auditInfo="auditInfo"
  />
</template>
<script setup lang="tsx">
import SendDetail from './components/sendDetail.vue'
import GeneralReimbDetail from './components/GeneralReimbDetail.vue'
import TravelReimbDetail from './components/TravelReimbDetail.vue'
import CorporatePaymentsDetail from './components/CorporatePaymentsDetail.vue'
import EntertainmentExpensesDetail from './components/EntertainmentExpensesDetail.vue'
import ForwarderFeeDetail from './components/ForwarderFeeDetail.vue'
import OtherReimbDetail from './components/OtherReimbDetail.vue'
import { CostTypes } from '@/utils/constants'
import { getFeeShareDetail } from '@/api/oa/feeShare'

const props = defineProps<{
  param?: string
  businessType: number
  status: string
  id: number
  rowId: number
}>()

const pageId = ref()
const pageStatus = ref()
const pageBusinessType = ref()
const pageParam = ref()
const auditInfo = ref()
const { query } = useRoute()

onMounted(async () => {
  if (query.id) {
    let res = await getFeeShareDetail({ processInstanceId: query.id })
    pageId.value = res.businessId
    pageStatus.value = res.auditStatus
    pageBusinessType.value = res.businessType
  } else {
    auditInfo.value = await getFeeShareDetail({ id: props.rowId })
    pageId.value = props.id
    pageStatus.value = props.status
    pageBusinessType.value = props.businessType
    pageParam.value = props.param
  }
})
</script>
