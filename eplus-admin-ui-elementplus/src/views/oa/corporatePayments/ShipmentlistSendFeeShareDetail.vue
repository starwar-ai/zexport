<template>
  <eplus-description
    title="费用归集"
    :data="props.info"
    :items="FeeShareSchemas"
  >
    <template #tableList>
      <Table
        v-if="props.info.relationType === 4"
        :columns="auxiliaryColumns"
        headerAlign="center"
        align="center"
        :data="tableList"
      />
      <Table
        v-if="props.info.relationType === 3"
        :columns="shipColumns"
        headerAlign="center"
        align="center"
        :data="tableList"
      />
      <Table
        v-if="props.info.relationType === 2"
        :columns="sendColumns"
        headerAlign="center"
        align="center"
        :data="tableList"
      />
    </template>
  </eplus-description>
</template>
<script setup lang="tsx">
import { formatMoneyColumn } from '@/utils/table'
import FeeShareInfo from '@/views/oa/components/FeeShareInfo.vue'
import * as corporatePaymentsApi from '@/api/oa/corporatePayments'

defineOptions({ name: 'ShipmentlistSendFeeShareDetail' })

const props = defineProps<{
  info?: any
}>()
const FeeShareSchemas = reactive([
  {
    slotName: 'tableList',
    field: 'tableList',
    label: '',
    span: 24
  }
])

const auxiliaryColumns = reactive([
  {
    field: 'code',
    label: '包材采购合同编号'
  },
  {
    field: 'amount',
    label: '归集金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'desc',
    label: '归集内容',
    slots: {
      default: (data) => {
        const { row } = data
        return <FeeShareInfo info={row.desc} />
      }
    }
  }
])
const shipColumns = reactive([
  {
    field: 'code',
    label: '出运发票号'
  },
  {
    field: 'amount',
    label: '归集金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'desc',
    label: '归集内容',
    slots: {
      default: (data) => {
        const { row } = data
        return <FeeShareInfo info={row.desc} />
      }
    }
  }
])

const sendColumns = reactive([
  {
    field: 'code',
    label: '快递编号'
  },
  {
    field: 'cost',
    label: '归集金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'desc',
    label: '归集内容',
    slots: {
      default: (data) => {
        const { row } = data
        return <FeeShareInfo info={row.feeShare} />
      }
    }
  }
])

const tableList = ref([])
onMounted(async () => {
  if (props.info.relationType === 3) {
    tableList.value = await corporatePaymentsApi.shipmentList({
      codeList: props.info.relationCode.join(',')
    })
  } else if (props.info.relationType === 2) {
    tableList.value = await corporatePaymentsApi.sendList({
      codeList: props.info.relationCode.join(',')
    })
  } else if (props.info.relationType === 4) {
    tableList.value = await corporatePaymentsApi.auxiliaryPurchaseList({
      codeList: props.info.relationCode.join(',')
    })
  }
})
</script>
<style lang="scss" scoped></style>
