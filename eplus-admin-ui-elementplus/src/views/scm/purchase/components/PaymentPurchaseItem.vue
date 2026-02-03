<template>
  <eplus-form-table
    ref="TableRef"
    :list="formData?.applyerPurchaseItemList"
    :defaultVal="{}"
    :schema="tableColumns"
    @selection-change="handleSelectionChange"
    showSummary
    :summaryMethod="handleSummary"
  />
</template>
<script setup lang="tsx">
import { EplusMoneyLabel, EplusNumInput } from '@/components/EplusMoney'
import { formatNum, formatTime, getCurrency } from '@/utils'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { isAmount, isNumber, isWeight } from '@/utils/is'
import { columnWidth } from '@/utils/table'

const message = useMessage()

const TableRef = ref()
const emit = defineEmits(['applyAmountChange'])
const applyAmountChange = (item, row) => {
  if (typeof row?.totalPrice?.amount !== 'number' || typeof row?.appliedAmount !== 'number') {
    message.warning('请确认金额是否正确')
    row.applyAmount = 0
    return 0
  }
  if (row?.withTaxPrice?.amount && row?.quantity) {
    if (
      row.withTaxPrice.amount * row.quantity <
      Number(item) + row?.appliedAmount + row?.paymentAmount
    ) {
      row.applyAmount =
        row.withTaxPrice.amount * row.quantity - row?.appliedAmount - row?.paymentAmount
      message.warning('本次请款金额加已申请金额不能大于采购金额')
    } else {
    }
  }
  emit('applyAmountChange')
}
const tableColumns = [
  {
    label: '序号',
    field: 'sortNum',
    fixed: 'left',
    width: columnWidth.m
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同号',
    width: columnWidth.l
  },
  {
    field: 'skuCode',
    label: '产品编号',
    width: columnWidth.l
  },
  {
    field: 'skuName',
    label: '产品名称',
    width: columnWidth.l
  },
  {
    field: 'totalPrice',
    label: '采购金额',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      if (row?.withTaxPrice?.amount && row?.quantity) {
        return formatNum(Number(row.withTaxPrice.amount * row.quantity))
      } else {
        return '-'
      }
    }
  },
  {
    field: 'paymentAmount',
    label: '已付金额',
    width: columnWidth.l
  },
  {
    field: 'appliedAmount',
    label: '已申请金额',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return (
        <EplusNumInput
          v-model={row.appliedAmount}
          disabled
        />
      )
    }
  },
  {
    field: 'applyAmount',
    label: '本次请款金额',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return (
        <EplusNumInput
          v-model={row.applyAmount}
          min={0}
          max={row?.maxApplyAmount}
          onChange={(val) => applyAmountChange(val, row)}
        />
      )
    }
  },
  {
    field: 'invoiceStatus',
    label: '开票状态',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return getDictLabel(DICT_TYPE.INVOICE_STATUS, row?.invoiceStatus) || '-'
    }
  },
  {
    field: 'invoicedAmount',
    label: '已开票金额',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return (
        <EplusMoneyLabel
          val={{ amount: row.withTaxPrice?.amount * row.invoicedQuantity, currency: row.currency }}
        />
      )
    }
  },
  {
    field: 'invoicedQuantity',
    label: '已开票数',
    width: columnWidth.l
  },
  {
    field: 'currency',
    label: '采购币别',
    width: columnWidth.l
  },
  {
    field: 'quantity',
    label: '采购数量',
    width: columnWidth.l
  },
  {
    field: 'withTaxPrice',
    label: '单价',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return (
        <EplusMoneyLabel
          val={row.withTaxPrice}
          symbolFlag={false}
        />
      )
    }
  },
  {
    field: 'saleContractCode',
    label: '关联销售合同号',
    width: columnWidth.l
  },
  {
    field: 'warehousingType',
    label: '入库状态',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return getDictLabel(DICT_TYPE.IN_OUT_STATUS, row?.warehousingType) || '-'
    }
  },
  {
    field: 'checkStatus',
    label: '验货结果',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      if (getDictLabel(DICT_TYPE.QMS_HANDLE_STATE, row?.handleFlag)) {
        return `${getDictLabel(DICT_TYPE.INSPECT_RESULT_TYPE, row?.checkStatus)}(${getDictLabel(DICT_TYPE.QMS_HANDLE_STATE, row?.handleFlag)})`
      } else {
        return `${getDictLabel(DICT_TYPE.INSPECT_RESULT_TYPE, row?.checkStatus)}`
      }
    }
  },
  {
    field: 'inspectionTime',
    label: '验货时间',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return formatTime(row.inspectionTime, 'yyyy-MM-dd') || '-'
    }
  },
  {
    field: 'estDepartureTime',
    label: '出运/出库日期',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return formatTime(row.estDepartureTime, 'yyyy-MM-dd') || '-'
    }
  }
]

const handleSummary = (param) => {
  const { columns, data } = param
  const sums: (string | VNode)[] = []
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '合计'
      return
    }
    const sumFileds = [
      'quantity',
      'paymentAmount',
      'appliedAmount',
      'applyAmount',
      'invoicedAmount',
      'invoicedQuantity',
      'quantity'
    ]
    let values = []
    if (sumFileds.includes(column.property) && isNumber(data[0][column.property])) {
      values = data.map((item) => Number(item[column.property]))
      let numVal = `${values.reduce((prev, curr) => {
        const value = Number(curr)
        if (!Number.isNaN(value)) {
          return prev + curr
        } else {
          return prev
        }
      }, 0)}`
      sums[index] = formatNum(numVal)
    } else if (sumFileds.includes(column.property) && isAmount(data[0][column.property])) {
      values = data.map((item) => Number(item[column.property].amount))
      let amountVal = `${values.reduce((prev, curr) => {
        const value = Number(curr)
        if (!Number.isNaN(value)) {
          return prev + curr
        } else {
          return prev
        }
      }, 0)}`
      sums[index] = `${getCurrency(data[0][column.property].currency)}${formatNum(amountVal)}`
    } else if (sumFileds.includes(column.property) && isWeight(data[0][column.property])) {
      values = data.map((item) => Number(item[column.property].weight))
      let weightVal = `${values.reduce((prev, curr) => {
        const value = Number(curr)
        if (!Number.isNaN(value)) {
          return prev + curr
        } else {
          return prev
        }
      }, 0)}`
      sums[index] = `${formatNum(weightVal)} ${data[0][column.property].unit}`
    } else {
      sums[index] = ''
    }
  })

  return sums
}
const formData = inject('formData')
const handleSelectionChange = (val) => {}
onMounted(() => {})
</script>
<style lang="scss" scoped></style>
