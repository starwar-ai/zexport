<template>
  <eplus-description
    title=""
    :data="pageInfo"
    :items="quoteitemListInfo"
  >
    <template #quoteitemList>
      <Table
        style="width: 100%"
        :columns="quoteitemListColumns"
        headerAlign="center"
        align="center"
        :data="pageInfo"
      />
    </template>
  </eplus-description>
</template>
<script setup lang="tsx">
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { formatDateColumn } from '@/utils/table'

const props = defineProps<{
  info?: any
}>()

const pageInfo = computed(() => props.info)
const quoteitemListInfo: EplusDescriptionItemSchema[] = [
  {
    slotName: 'quoteitemList',
    field: 'quoteitemList',
    label: '',
    span: 24
  }
]

const quoteitemListColumns = [
  {
    label: '认领人',
    field: 'designerName'
  },
  {
    label: '认领时间',
    field: 'acceptDate',
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    label: '计划开始时间',
    field: 'planStartDate',
    width: '200',
    formatter: formatDateColumn('YYYY-MM-DD')
  },

  {
    label: '计划完成时间',
    field: 'planCompleteDate',
    width: '200',
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    label: '实际完成时间',
    field: 'completeDate',
    width: '200',
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    label: '文件位置',
    field: 'designFilePath'
  }
]
</script>
