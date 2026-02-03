<template>
  <eplus-form-table
    ref="TableRef"
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
    showSummary
    :summaryMethod="handleSummary"
  />
</template>
<script setup lang="tsx">
import { EplusMoneyLabel, EplusNumInput } from '@/components/EplusMoney'
import { formatNum, formatTime, getCurrency } from '@/utils'
import { moneyInputPrecision } from '@/utils/config'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { isAmount, isNumber, isWeight } from '@/utils/is'
import { columnWidth } from '@/utils/table'

const props = defineProps<{
  type?: string
  formData?: any
}>()
const tableList = ref([])

watch(
  () => props?.formData?.applyerPurchaseItemList,
  () => {
    if (props.type === 'detail') {
      tableList.value = props?.formData?.applyerPurchaseItemList.map((item) => {
        // let applyPaymentPlanList = props?.formData?.applyPaymentPlanList
        // let controlInvoiceFlag = applyPaymentPlanList.find(
        //   (el) => el.contractCode === item.purchaseContractCode
        // )?.controlInvoiceFlag
        let maxVal = item.withTaxPrice?.amount * item.invoicedQuantity - item.paymentAmount
        maxVal = maxVal > 0 ? maxVal : 0
        return {
          ...item,
          usablePaymentAmount: {
            amount: maxVal,
            currency: item.currency
          },
          totalPrice: {
            amount: item.withTaxPrice.amount * item.quantity,
            currency: item.withTaxPrice.currency
          }
        }
      })
      return
    } else {
      tableList.value = props?.formData?.applyerPurchaseItemList.map((item) => {
        let applyPaymentPlanList = props?.formData?.applyPaymentPlanList
        let controlInvoiceFlag = applyPaymentPlanList.find(
          (el) => el.contractCode === item.purchaseContractCode
        )?.controlInvoiceFlag
        let maxVal = item.withTaxPrice?.amount * item.invoicedQuantity - item.paymentAmount
        maxVal = maxVal > 0 ? maxVal : 0
        let applyAmount
        if (
          props?.formData?.step === 1 ||
          (props?.formData?.step !== 1 && controlInvoiceFlag === 0)
        ) {
          applyAmount = item.applyAmount
        } else {
          applyAmount = item.applyAmount < maxVal ? item.applyAmount : maxVal
        }
        return {
          ...item,
          invoicedAmount: item.withTaxPrice?.amount * item.invoicedQuantity,
          usablePaymentAmount: {
            amount: maxVal,
            currency: item.currency
          },
          maxApplyAmount:
            props?.formData?.step === 1 || (props?.formData?.step !== 1 && controlInvoiceFlag === 0)
              ? item.applyAmount
              : maxVal,
          applyAmount: applyAmount,
          totalPrice: {
            amount: item.withTaxPrice.amount * item.quantity,
            currency: item.withTaxPrice.currency
          }
        }
      })
    }
  },
  {
    immediate: true
  }
)
const message = useMessage()
const TableRef = ref()
const emit = defineEmits(['applyAmountChange'])
const applyAmountChange = (item, row) => {

  if (isNaN(row?.totalPrice?.amount) || isNaN(row?.paymentAmount)) {
    message.warning('请确认金额是否正确')
    row.applyAmount = 0
    return 0
  }
  if (row?.withTaxPrice?.amount && row?.quantity) {
    if (row.withTaxPrice.amount * row.quantity < Number(item) + row?.paymentAmount) {
      row.applyAmount = row.withTaxPrice.amount * row.quantity - row?.paymentAmount
      message.warning('本次请款金额加已申请金额不能大于采购金额')
    }
  }
  if (row.applyAmount > row.maxApplyAmount) {
    row.applyAmount = row.maxApplyAmount
  }
  emit('applyAmountChange', tableList.value)
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
      return formatNum(row.totalPrice?.amount)
    }
  },
  {
    field: 'paymentAmount',
    label: '已付金额',
    width: columnWidth.l
  },
  // {
  //   field: 'appliedAmount',
  //   label: '已申请金额',
  //   width: columnWidth.l,
  //   formatter: (item, row, index) => {
  //     return <span>{row.appliedAmount}</span>
  //   }
  // },
  {
    field: 'applyAmount',
    label: '本次请款金额',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      if (props?.type === 'detail') {
        return <span>{row.applyAmount}</span>
      } else {
        return (
          <EplusNumInput
            v-model={row.applyAmount}
            min={0}
            max={row?.maxApplyAmount}
            precision={moneyInputPrecision}
            onChange={(val) => applyAmountChange(val, row)}
            class="!w-90%"
            disabled={props?.type === 'detail'}
          />
        )
      }
    }
  },
  {
    field: 'usablePaymentAmount',
    label: '可申请付款金额',
    hint: (
      <div>
        <div>应付货款的申请金额，不能大于可申请付款金额</div>
        <div>（可申请付款金额=已登票金额-已付款金额）</div>
      </div>
    ),
    hintPlacement: 'top',
    hintWidth: 350,
    width: columnWidth.xl,
    formatter: (item, row, index) => {
      return <EplusMoneyLabel val={row.usablePaymentAmount} />
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
    field: 'invoicedQuantity',
    label: '已开票数',
    width: columnWidth.l
  },
  {
    field: 'invoicedAmount',
    label: '已开票金额',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return <EplusMoneyLabel val={{ amount: row.invoicedAmount, currency: row.currency }} />
    }
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
    field: 'billStatus',
    label: '入库地点',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return getDictLabel(DICT_TYPE.SALE_ITEM_BILL_STATUS, row?.billStatus) || '-'
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
  if (!data || data.length === 0) {
    return sums
  }
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '合计'
      return
    }
    const sumFileds = [
      'totalPrice',
      'quantity',
      'paymentAmount',
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
const checkData = () => {
  let flag = false

  let applyTotalAmount = tableList.value.reduce((prev, curr: any) => {
    return prev + curr.applyAmount
  }, 0)

  if (applyTotalAmount <= 0) {
    flag = true
  }
  return flag
}
defineExpose({
  checkData,
  tableList
})
</script>
<style lang="scss" scoped></style>
