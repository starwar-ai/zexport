<template>
  <eplus-description
    title="单据信息"
    :data="pageInfo"
    :items="docInfoSchemas"
  >
    <template #cashier>
      <span v-if="pageInfo?.cashier?.nickname">
        {{ pageInfo?.cashier?.nickname }}
      </span>
      <span v-else> - </span>
    </template>
  </eplus-description>
</template>
<script setup lang="tsx">
const props = defineProps<{
  pageInfo: any
}>()
const docInfoSchemas: EplusDescriptionItemSchema[] = [
  {
    label: '支付状态',
    field: 'paymentStatus',
    dictType: DICT_TYPE.PAYMENT_STATUS
  },
  {
    label: '支付金额',
    field: 'paymentAmount',
    type: 'money'
  },
  {
    label: '支付日期',
    field: 'paymentDate',
    type: 'date'
  },
  {
    slotName: 'cashier',
    label: '出纳员',
    field: 'cashier'
  }
]
</script>
<style lang="scss" scoped></style>
