<template>
  <Table
    style="width: 100%"
    :columns="crmColumns"
    headerAlign="center"
    align="center"
    :data="formData?.crmChildren"
  />
</template>
<script setup lang="tsx">
import { DICT_TYPE, getDictLabels } from '@/utils/dict'
import { formatDateColumn } from '@/utils/table'

const props = defineProps<{
  formData
}>()
const crmColumns = [
  {
    field: 'code',
    label: '客户编号'
  },
  {
    field: 'createTime',
    label: '创建时间',
    formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
  },
  {
    field: 'customerTypes',
    label: '类型',
    slots: {
      default: (data) => {
        const { row } = data
        return getDictLabels(DICT_TYPE.CUSTOM_TYPE, row.customerTypes)
      }
    }
  },
  {
    field: 'name',
    label: '客户名称'
  },
  {
    field: 'ratio',
    label: '费用分摊（100%）'
  }
]
</script>
