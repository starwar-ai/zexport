<template>
  <Table
    ref="tableRef"
    :columns="columns"
    headerAlign="center"
    align="center"
    :data="list"
  />
</template>
<script setup lang="tsx">
import Table from '@/components/Table/src/Table.vue'

defineOptions({ name: 'LoanAppListDetail' })
const props = defineProps<{
  list: any[]
}>()
const tableRef = ref()
const columns = reactive([
  {
    field: 'code',
    label: '借款单号'
  },
  {
    field: 'companyName',
    label: '公司主体'
  },
  {
    field: 'amount',
    label: '借款金额',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.amount.amount} ${row.amount.currency} `
      }
    }
  },
  {
    field: 'outstandingAmount',
    label: '当前待还金额',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.outstandingAmount.amount} ${row.outstandingAmount.currency} `
      }
    }
  },
  {
    field: 'inRepaymentAmount',
    label: '还款中金额',
    slots: {
      default: (data) => {
        const { row } = data
        if (row.inRepaymentAmount) {
          return `${row.inRepaymentAmount.amount} ${row.inRepaymentAmount.currency} `
        } else {
          return '0'
        }
      }
    }
  },
  {
    field: 'purpose',
    label: '借款事由'
  }
])

onMounted(() => {})
</script>
<style lang="scss" scoped>
.total_box {
  width: 100%;
  display: flex;
  justify-content: space-between;
  height: 40px;
  align-items: center;
}
</style>
