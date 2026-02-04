<template>
  <Table
    ref="tableRef"
    :columns="columns"
    headerAlign="center"
    align="center"
    :data="tableData"
    @selection-change="handleSelectionChange"
  />
  <!-- <div class="total_box">
    <div> 总计 </div>
    <div class="pr15px">
      待还金额：<TotalLayout
        class="pr15px"
        :list="getTotal(tableData, 'outstandingAmountAmount', 'outstandingAmountCurrency')"
      />

      还款中金额：<TotalLayout
        :list="getTotal(tableData, 'inRepaymentAmountAmount', 'inRepaymentAmountCurrency')"
      />
    </div>
  </div> -->
</template>
<script setup lang="tsx">
import Table from '@/components/Table/src/Table.vue'
import { formatMoneyColumn } from '@/utils/table'
import * as travelreimbApi from '@/api/oa/travelreimb'
import { useUserStore } from '@/store/modules/user'
import { getTotal } from '@/utils/index'
import { isValidArray } from '@/utils/is'

defineOptions({ name: 'LoanAppListCom' })
const props = defineProps<{
  formData
}>()
const message = useMessage()
const tableRef = ref()
const tableData = ref([])
const columns = reactive([
  {
    type: 'selection'
  },
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
    formatter: formatMoneyColumn()
  },
  {
    field: 'outstandingAmount',
    label: '当前待还金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'inRepaymentAmount',
    label: '还款中金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'purpose',
    label: '借款事由'
  }
])

const selectedDiaData = ref<any[]>([])
const handleSelectionChange = (val) => {
  selectedDiaData.value = val
  loanTotalList.value = getTotal(selectedDiaData.value, 'outstandingAmountAmount', 'currency')
}
const loanTotalList = ref()
defineExpose({
  tableData,
  loanTotalList,
  selectedDiaData
})

const getLoanAppList = () => {
  travelreimbApi
    .getLoanSimpleList({
      applyerId: useUserStore().getUser.id,
      companyId: props.formData.companyId,
      currency: props.formData.currency
    })
    .then((res) => {
      if (res?.list && res?.list?.length > 0) {
        if (isValidArray(props.formData.loanAppList)) {
          res.list.forEach((item: any) => {
            props.formData.loanAppList.forEach((el: any) => {
              if (item.code === el.code) {
                item.outstandingAmount.amount =
                  Number(item.outstandingAmount?.amount || 0) +
                  Number(el.outstandingAmount?.amount || 0)
                item.inRepaymentAmount.amount =
                  Number(item.inRepaymentAmount?.amount || 0) -
                  Number(el.outstandingAmount?.amount || 0)
              }
            })
          })
        }
        res.list.forEach((item) => {
          if (item.outstandingAmount?.amount > 0) {
            let obj = {
              ...item,
              currency: item.outstandingAmount.currency,
              calculateVal: item.inRepaymentAmount
                ? Number(item.outstandingAmount.amount) - Number(item.inRepaymentAmount.amount)
                : item.outstandingAmount.amount,
              outstandingAmountAmount: item.outstandingAmount.amount,
              outstandingAmountCurrency: item.outstandingAmount.currency,
              inRepaymentAmountAmount: item.inRepaymentAmount?.amount || 0,
              inRepaymentAmountCurrency: item.inRepaymentAmount?.currency || ''
            }
            tableData.value.push(obj)
          }
        })
        nextTick(() => {
          if (props.formData.loanAppList?.length > 0) {
            tableData.value.forEach((item: any) => {
              props.formData.loanAppList.forEach((el: any) => {
                if (item.id == el.id) {
                  selectedDiaData.value.push(item)
                  tableRef.value!.elTableRef.toggleRowSelection(item, undefined)
                }
              })
            })
          }
          loanTotalList.value = getTotal(selectedDiaData.value, 'calculateVal', 'currency')
        })
      } else {
        tableData.value = []
        message.warning('暂无借款单')
      }
    })
}

watch(
  () => [props.formData.currency, props.formData.companyId],
  ([currency, companyId]) => {
    if (currency || companyId) {
      getLoanAppList()
    }
  }
)

onMounted(() => {
  getLoanAppList()
})

// onMounted(() => {
//   if (props.formData.loanAppList?.length > 0) {
//     tableData.value = props.formData.loanAppList.map((item) => {
//       return {
//         ...item,
//         currency: item.outstandingAmount.currency,
//         calculateVal: item.inRepaymentAmount
//           ? Number(item.outstandingAmount.amount) - Number(item.inRepaymentAmount.amount)
//           : item.outstandingAmount.amount,
//         outstandingAmountAmount: item.outstandingAmount.amount,
//         outstandingAmountCurrency: item.outstandingAmount.currency,
//         inRepaymentAmountAmount: item.inRepaymentAmount?.amount || 0,
//         inRepaymentAmountCurrency: item.inRepaymentAmount?.currency || ''
//       }
//     })
//     loanTotalList.value = getTotal(tableData.value, 'calculateVal', 'currency')
//   } else {
//     travelreimbApi.getLoanSimpleList({ applyerId: useUserStore().getUser.id }).then((res) => {
//       if (res.list && res.list.length > 0) {
//         res.list.forEach((item) => {
//           if (item.outstandingAmount?.amount > 0) {
//             tableData.value.push({
//               ...item,
//               currency: item.outstandingAmount.currency,
//               calculateVal: item.inRepaymentAmount
//                 ? Number(item.outstandingAmount.amount) - Number(item.inRepaymentAmount.amount)
//                 : item.outstandingAmount.amount,
//               outstandingAmountAmount: item.outstandingAmount.amount,
//               outstandingAmountCurrency: item.outstandingAmount.currency,
//               inRepaymentAmountAmount: item.inRepaymentAmount?.amount || 0,
//               inRepaymentAmountCurrency: item.inRepaymentAmount?.currency || ''
//             })
//           }
//         })
//         loanTotalList.value = getTotal(tableData.value, 'calculateVal', 'currency')
//       } else {
//         tableData.value = []
//         message.warning('暂无借款单')
//       }
//     })
//   }
// })
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
