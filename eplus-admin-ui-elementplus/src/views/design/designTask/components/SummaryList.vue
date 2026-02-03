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
    label: '当前进度',
    field: 'progress'
  },
  {
    label: '进度描述',
    field: 'progressDesc'
  },
  {
    label: '填写时间',
    field: 'createTime',
    width: '200',
    formatter: formatDateColumn('YYYY-MM-DD')
  }
]
</script>
