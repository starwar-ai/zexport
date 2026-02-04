<template>
  <el-popover
    placement="top-start"
    :width="800"
    trigger="hover"
    @show="handlePopoverShow()"
    :disabled="popoverDisabled"
  >
    <template #reference>
      {{ row.son_quantity }}
    </template>
    <template #default>
      <div v-if="tableList.length > 0">
        <Table
          :columns="tableColumns"
          headerAlign="center"
          align="center"
          :data="tableList"
        />
      </div>
    </template>
  </el-popover>
</template>
<script setup lang="tsx">
import { formatDictColumn } from '@/utils/table'

const props = defineProps<{
  row: any
}>()
console.log(props.row)

let tableList = ref([])
const tableColumns = reactive([
  {
    field: 'batchCode',
    label: '批次号'
  },
  {
    field: 'companyName',
    label: '采购主体'
  },
  {
    field: 'skuName',
    label: '产品名称'
  },
  {
    field: 'availableQuantity',
    label: '使用数量'
  },
  {
    field: 'stockMethod',
    label: '库存类型',
    formatter: formatDictColumn(DICT_TYPE.STOCK_METHOD)
  }
])

const popoverDisabled = computed(() => {
  return false
})

// Popover表格展示之后调接口
const handlePopoverShow = () => {
  tableList.value = props.row.son_stockList
}
</script>
<style lang="scss" scoped></style>
