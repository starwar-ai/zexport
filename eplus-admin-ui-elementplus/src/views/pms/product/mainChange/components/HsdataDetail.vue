<template>
  <eplus-description
    title="海关信息"
    :data="pageInfo"
    :items="hsdataInfo"
  >
    <template #hsdataTaxRefundRate>
      <span v-if="pageInfo.hsdataTaxRefundRate">{{ pageInfo.hsdataTaxRefundRate }} % </span>
      <span v-else>-</span>
    </template>
  </eplus-description>
</template>
<script setup lang="tsx">
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'

const props = defineProps<{
  info?: any
}>()

const pageInfo = computed(() => props.info)

const hsdataInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'hsdataCode',
    label: '报关HSCODE'
  },
  {
    field: 'hsdataUnit',
    label: '报关单位'
  },
  {
    slotName: 'hsdataTaxRefundRate',
    field: 'hsdataTaxRefundRate',
    label: '退税率'
  }
]
</script>
