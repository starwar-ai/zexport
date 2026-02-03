<template>
  <Table
    style="width: 100%"
    :columns="vendorColumns"
    headerAlign="center"
    align="center"
    :data="formData?.venderChildren"
  />
</template>
<script setup lang="tsx">
import { formatDateColumn } from '@/utils/table'

const props = defineProps<{
  formData
}>()

const vendorColumns = [
  {
    field: 'code',
    label: '供应商编号'
  },
  {
    field: 'createTime',
    label: '创建时间',
    formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
  },
  {
    field: 'name',
    label: '供应商名称'
  },
  {
    field: 'ratio',
    label: '报销费用分摊（100%）'
  }
]
</script>
