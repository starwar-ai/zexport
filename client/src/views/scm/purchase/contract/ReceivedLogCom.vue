<template>
  <el-popover
    placement="top-start"
    :width="500"
    trigger="hover"
    @show="handlePopoverShow()"
    :disabled="popoverDisabled"
  >
    <template #reference>
      {{ row.row.paymentTime ? formatTime(row.row.paymentTime, 'yyyy-MM-dd') : '-' }}
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
import { formatDateColumn, formatMoneyColumn } from '@/utils/table'
import { formatTime } from '@/utils/index'
import { isValidArray } from '@/utils/is'

const props = defineProps<{
  row: any
}>()

let tableList = ref([])
const tableColumns = reactive([
  {
    field: 'paymentTime',
    label: '付款日期',
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    field: 'paymentAmount',
    label: '付款金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'paymentUser',
    label: '付款人',
    formatter: (row) => {
      return row.paymentUser?.nickname || '-'
    }
  }
])

const popoverDisabled = computed(() => {
  return !isValidArray(props.row.row?.paymentMsg)
})

// Popover表格展示之后调接口
const handlePopoverShow = () => {
  tableList.value = props.row?.row?.paymentMsg || []
}
</script>
<style lang="scss" scoped></style>
