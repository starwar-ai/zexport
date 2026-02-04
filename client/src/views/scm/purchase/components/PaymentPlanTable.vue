<template>
  <eplus-form-table
    ref="TableRef"
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
    @selection-change="handleSelectionChange"
  />
</template>
<script setup lang="tsx">
import { TableColumn } from '@/types/table'
import { formatNum, formatterPrice, getCurrency } from '@/utils'
import { getDictLabel } from '@/utils/dict'
import { formatDate } from '@/utils/formatTime'
import { isValidArray } from '@/utils/is'
import { cloneDeep } from 'lodash-es'

defineOptions({ name: 'PaymentPlanTable' })
const props = defineProps<{
  formData
  type: string
}>()
const TableRef = ref()
const message = useMessage()
const tableList = ref([])
let tableColumns = reactive<TableColumn[]>([
  {
    field: 'step',
    label: '步骤',
    width: '150px',
    formatter: (item, row: Recordable, index: number) => {
      return getDictLabel(DICT_TYPE.COLLECTION_PLAN_STEP, row.step)
    }
  },
  {
    field: 'dateType',
    label: '起始点',
    width: '150px',
    component: (
      <eplus-dict-select
        style="width: 120px"
        dictType={DICT_TYPE.DATE_TYPE}
        disabled
      />
    ),
    rules: [{ required: true, message: '请选择起始点' }]
  },
  {
    field: 'controlInvoiceFlag',
    label: '是否控制登票',
    width: '150px',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.IS_INT}
        clearable={false}
        disabled
      />
    )
  },
  {
    field: 'startDate',
    label: '起始日',
    width: '150px',
    formatter: (item, row: Recordable, index: number) => {
      if (row.startDate) {
        return formatDate(row.startDate, 'YYYY-MM-DD')
      } else {
        return '-'
      }
    }
  },
  {
    field: 'days',
    label: '天数',
    width: '150px',
    component: (
      <el-input-number
        precision={0}
        min={0}
        controls={false}
        clearable={true}
        valueOnClear={0}
        style="width: 120px"
        disabled
      />
    ),
    rules: [{ required: true, message: '请输入天数' }]
  },
  {
    field: 'expectedReceiptDate',
    label: '预计付款日',
    width: '150px',
    formatter: (item, row: Recordable, index: number) => {
      if (row.expectedReceiptDate) {
        return formatDate(row.expectedReceiptDate, 'YYYY-MM-DD')
      } else {
        return '-'
      }
    }
  },
  {
    field: 'paymentRatio',
    width: '150px',
    label: '付款比例%'
  },
  {
    field: 'receivableAmount',
    label: '应付金额',
    width: '150px',
    formatter: (item, row: Recordable, index: number) => {
      if (row.receivableAmount?.amount) {
        return `${getCurrency(row.receivableAmount.currency)} ${formatNum(row.receivableAmount.amount)}`
      } else {
        return '-'
      }
    }
  },
  {
    field: 'realPaymentRatio',
    width: '150px',
    label: '实际付款比例%'
  },
  {
    field: 'receivedAmount',
    label: '实付金额',
    width: '150px',
    formatter: (item, row: Recordable, index: number) => {
      if (row.receivedAmount?.amount) {
        return `${getCurrency(row.receivedAmount.currency)} ${formatNum(row.receivedAmount.amount)}`
      } else {
        return '-'
      }
    }
  }
  // {
  //   field: 'controlPurchaseFlag',
  //   label: '是否控制采购',
  //   width: '150px',
  //   component: (
  //     <eplus-dict-select
  //       style="width: 150px"
  //       dictType={DICT_TYPE.CONFIRM_TYPE}
  //     />
  //   ),
  //   rules: [{ required: true, message: '请选择是否控制采购' }]
  // },
  // {
  //   field: 'exeStatus',
  //   label: '状态',
  //   width: '150px',
  //   component: (
  //     <eplus-dict-select
  //       style="width: 120px"
  //       dictType={DICT_TYPE.EXECUTE_STATUS}
  //     />
  //   )
  // }
])

const selectedList = ref([])
const handleSelectionChange = (val) => {
  selectedList.value = val
}

const checkData = async () => {
  if (tableList.value.length) {
    let valid = await TableRef.value.validate()
    if (valid) {
      let tableData = cloneDeep(TableRef.value.tableData)
      let arr = tableData.map((item, index) => {
        const { id, ...res } = item
        return res
      })
      return arr
    } else {
      message.warning('付款计划提交有误')
      return false
    }
  } else {
    message.warning('付款计划提交有误')
    return false
  }
}
const setTotalHeader = (val) => {
  let totalGoodsValue = val?.purchaseMoney
  let executed = tableList.value.filter((item: any) => item.exeStatus !== 0)
  let receivableAmountValue = executed.reduce((acc, item: any) => {
    return acc + Number(item.receivableAmount?.amount || 0)
  }, 0)
  let notExecuted = tableList.value.filter((item: any) => item.exeStatus === 0)
  let totalRatio = notExecuted.reduce((acc, item: any) => {
    return acc + Number(item.paymentRatio)
  }, 0)
  let pendingAmount = Number(totalGoodsValue) - (receivableAmountValue || 0)
  if (pendingAmount) {
    tableList.value?.forEach((item: any) => {
      if (item.exeStatus === 0 && item.paymentRatio) {
        if (!item.receivableAmount) {
          item.receivableAmount = {
            amount: '0',
            currency: props.formData.currency || 'RMB'
          }
        }
        item.receivableAmount.amount =
          (Number(item.paymentRatio) * Number(pendingAmount)) / totalRatio
        item.receivableAmount.currency = props.formData.currency || 'RMB'
      } else {
        item.realPaymentRatio = formatterPrice(
          (Number(item.receivedAmount?.amount) / Number(totalGoodsValue)) * 100,
          2
        )
      }
    })
  }
}
defineExpose({ selectedList, tableList, checkData, setTotalHeader })
watchEffect(() => {
  if (isValidArray(props.formData.purchasePaymentPlanList)) {
    tableList.value = cloneDeep(props.formData.purchasePaymentPlanList)
  } else {
    tableList.value = []
  }
})
onMounted(async () => {})
</script>
<style lang="scss" scoped>
:deep(.el-badge__content) {
  position: relative;
  left: -55px;
  width: 30px;
}

:deep(.el-badge__content--primary) {
  color: #7cc440;
  background-color: rgba(0, 0, 0, 0);
}

.total-header {
  width: 100%;
}
</style>
