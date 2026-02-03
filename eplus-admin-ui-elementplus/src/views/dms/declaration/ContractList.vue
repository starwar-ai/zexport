<template>
  <eplus-form-table
    ref="TableRef"
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
    showIndex
    :showIndexPlus="false"
  />
</template>
<script setup lang="tsx">
import { cloneDeep } from 'lodash-es'
import { volPrecision, VolumeUnit, weightUnit } from '@/utils/config'
import { formatNum, formatterPrice, formatWeight, getTotalAmount } from '@/utils/index'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { getOuterbox, getOuterboxVal } from '@/utils/outboxSpec'
import type { EplusFormTableSchema } from '@/components/EplusTemplate'
import { useRateStore } from '@/store/modules/rate'
import EplusMoneyLabel from '@/components/EplusMoney/src/EplusMoneyLabel.vue'
import { columnWidth } from '@/utils/table'
import { getDictLabel } from '@/utils/dict'

defineOptions({ name: 'ContractList' })
const props = defineProps<{
  formData
}>()
const message = useMessage()
const tableList: any = ref([])
const hsdataCodeChange = (e, row) => {
  // let item = e[1].find((item) => item.code === e[0])
  // row.hsMeasureUnit = item?.unit || ''
  // row.taxRefundRate = item?.taxRefundRate || ''
}
const updateCustomerNames = () => {
  const custName = tableList.value
    .map((item) => item.custName)
    .filter((name) => name && name.trim())
    .filter((name, index, arr) => arr.indexOf(name) === index) // 去除重复
    .join(', ')
  emit('customer-names-change', custName)
}

// const custChange = (e, row) => {
//   let item = e[1].find((item) => item.id === e[0])
//   row.custCode = item.code
//   row.custName = item.name
// }
const tableColumns = reactive([
  {
    field: 'index',
    label: '序号',
    width: '60',
    align: 'center',
    formatter: (item: any, row: any, index: number) => index + 1
  },
  {
    field: 'saleContractCode',
    label: '外销合同',
    width: columnWidth.l,
    sortable: true
  },
  {
    field: 'custName',
    label: '客户名称',
    width: columnWidth.l
  },
  {
    field: 'custPo',
    label: '客户PO号',
    width: columnWidth.l,
    sortable: true
  },
  {
    field: 'hsCode',
    label: 'HS编码',
    width: columnWidth.l
  },
  {
    field: 'customsDeclareElements',
    label: '报关要素',
    width: columnWidth.l,
    component: (
      <el-input
        type="textarea"
        rows={3}
      />
    )
  },
  {
    field: 'customsDeclarationNameEng',
    label: '报关英文名',
    width: columnWidth.l
  },
  {
    field: 'declarationName',
    label: '报关中文名',
    width: columnWidth.l
  },
  {
    field: 'barcode',
    label: '条形码',
    width: columnWidth.l
  },
  {
    field: 'currency',
    label: '外销币种',
    width: columnWidth.l
  },
  {
    field: 'settlementTermType',
    label: '价格条款',
    width: columnWidth.l,
    component: (
      <eplus-dict-select
        class="w-90%"
        dictType={DICT_TYPE.PRICE_TYPE}
        clearable={false}
      />
    ),
    rules: [{ required: true, message: '请选择价格条款' }]
  },
  {
    field: 'settlementName',
    label: '收款方式',
    width: columnWidth.l
  },
  {
    field: 'shippingQuantity',
    label: '出运数量',
    width: columnWidth.l
  },
  {
    field: 'declarationQuantity',
    label: '报关数量',
    width: columnWidth.l
  },
  {
    field: 'hsMeasureUnit',
    label: '海关计量单位',
    width: columnWidth.l
  },
  {
    field: 'declarationUnitPrice',
    label: '报关单价',
    width: columnWidth.l,
    formatter: (item, row) => {
      return <EplusMoneyLabel val={row.declarationUnitPrice} />
    }
  },
  {
    field: 'declarationAmount',
    label: '报关总价',
    width: columnWidth.l,
    formatter: (item, row) => {
      return <EplusMoneyLabel val={row.declarationAmount} />
    }
  },
  {
    field: 'taxRefundRate',
    label: '退税率%',
    width: columnWidth.l
  },
  {
    field: 'taxRefundPrice',
    label: '退税金额',
    width: columnWidth.l,
    formatter: (item, row) => {
      return <EplusMoneyLabel val={row.taxRefundPrice} />
    }
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'boxCount',
    label: '箱数',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return <span>{formatNum(row.boxCount * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'unitPerOuterbox',
    label: '外箱单位',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return getDictLabel(DICT_TYPE.UNIT_PER_OUTERBOX_TYPE, row.unitPerOuterbox)
    }
  },
  {
    field: 'outerboxVolume',
    label: '外箱体积',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <div>{getOuterbox(row, 'vol')}</div>
    }
  },
  {
    field: 'totalVolume',
    label: `总体积${VolumeUnit}`,
    width: columnWidth.l,
    formatter: (item, row) => {
      return formatNum(row.totalVolume / 1000000, volPrecision)
    }
  },
  {
    field: 'outerboxGrossweight',
    label: '外箱毛重',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return <div>{getOuterbox(row, 'grossweight')}</div>
    }
  },
  {
    field: 'totalGrossweight',
    label: '总毛重',
    width: columnWidth.l,
    formatter: (item, row) => {
      return formatWeight(row.totalGrossweight)
    }
  },
  {
    field: 'outerboxNetweight',
    label: '外箱净重',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return <div>{getOuterbox(row, 'netweight')}</div>
    }
  },
  {
    field: 'totalNetweight',
    label: '总净重',
    width: columnWidth.l,
    formatter: (item, row) => {
      return formatWeight(row.totalNetweight)
    }
  }
])
const emit = defineEmits(['getCabinet', 'customer-names-change'])

const rateList = useRateStore().rateList

const getTaxRefundPrice = (item) => {
  let result =
    (((item.purchaseWithTaxPrice.amount *
      item.shippingQuantity *
      rateList[item.purchaseWithTaxPrice.currency]) /
      (1 + item.taxRefundRate / 100)) *
      item.taxRefundRate) /
    100
  item.taxRefundPrice.amount = formatterPrice(result, 3)
}
watch(
  () => tableList.value,
  (list) => {
    let listTotalVolume = 0,
      listTotalGoodsValue = [],
      listPurchaseTotalAmount = { amount: 0, currency: '' },
      listTotalQuantity = 0,
      listTotalBoxes = 0,
      listTotalGrossweight = 0,
      listTotalWeight = 0,
      listTotalDeclaration = [],
      listTotalPurchase = [],
      listTotalTaxRefundPrice = { amount: 0, currency: '' },
      listCommissionAmount = { amount: 0, currency: '' },
      receivedNum = 0,
      unreceivedNum = 0,
      listInsuranceFee = { amount: 0, currency: '' }
    list?.forEach((item: any) => {
      //退税金额
      getTaxRefundPrice(item)
      // item.taxRefundPrice.amount = item.taxRefundUnitPrice
      //   ? Number(item.taxRefundUnitPrice) * Number(item.shippingQuantity)
      //   : item.taxRefundPrice.amount

      // taxRefundPrice
      //报关金额
      item.declarationAmount.amount =
        Number(item.declarationUnitPrice.amount) * Number(item.shippingQuantity)
      item.declarationAmount.currency = item.declarationUnitPrice.currency
      //销售金额
      item.saleAmount.amount = Number(item.saleUnitPrice.amount) * Number(item.shippingQuantity)
      item.saleAmount.currency = item.saleUnitPrice.currency
      if (!item.purchaseTotalAmount) {
        item.purchaseTotalAmount = { amount: 0, currency: '' }
      }
      //采购总价
      if (item.purchaseWithTaxPrice?.amount && item.purchaseTotalQuantity) {
        item.purchaseTotalAmount.amount =
          Number(item.purchaseWithTaxPrice.amount) * Number(item.purchaseTotalQuantity)
        item.purchaseTotalAmount.currency = item.purchaseWithTaxPrice.currency
      }
      //货值合计
      // listTotalGoodsValue.amount += item.saleAmount.amount
      // listTotalGoodsValue.currency = item.saleAmount.currency
      //箱数计算
      // item.boxCount = Math.ceil(Number(item.shippingQuantity) / Number(item.qtyPerOuterbox))
      //数量合计
      listTotalQuantity += item.shippingQuantity
      //报关数量
      // listTotalDeclaration += item.declarationQuantity
      //箱数合计
      listTotalBoxes += item.boxCount
      //外箱体积计算
      item.outerboxVolume =
        Number(item.outerboxWidth) * Number(item.outerboxHeight) * Number(item.outerboxLength)
      //总体积计算
      item.totalVolume = Number(item.boxCount) * getOuterboxVal(item, 'vol')
      listTotalVolume += item.totalVolume
      //总毛重计算
      item.totalGrossweight.weight = getOuterboxVal(item, 'grossweight') * Number(item.boxCount)
      item.totalGrossweight.unit = weightUnit
      // 毛重合计
      listTotalGrossweight += item.totalGrossweight.weight

      //总净重计算
      item.totalNetweight.weight = getOuterboxVal(item, 'netweight') * Number(item.boxCount)
      item.totalNetweight.unit = weightUnit
      // 净重合计
      listTotalWeight += item.totalNetweight.weight

      //保险费用计算
      if (typeof item.insuranceFeeStr === 'string') {
        listInsuranceFee.amount += Number(item.insuranceFeeStr)
        listInsuranceFee.currency = 'RMB'
      }
      //退税金额
      // if (typeof item.taxRefundPrice?.amount === 'string') {
      //   listTotalTaxRefundPrice.amount += Number(item.taxRefundPrice?.amount) || 0
      //   listTotalTaxRefundPrice.currency = item.taxRefundPrice.currency
      // }
      //佣金
      if (typeof item.commissionAmount === 'string') {
        listCommissionAmount.amount += Number(item.commissionAmount?.amount) || 0
        listCommissionAmount.currency = item.commissionAmount.currency
      }
      //报关名称
      item.declarationName = item.declarationName || item.name
      item.customsDeclarationNameEng = item.customsDeclarationNameEng || item.nameEng
      //商检负责方
      item.commodityInspectionType = item.commodityInspectionFlag
        ? item.commodityInspectionType || 1
        : undefined
    })
    listTotalPurchase = getTotalAmount(list, 'purchaseTotalAmount')
    listTotalGoodsValue = getTotalAmount(list, 'saleAmount')
    listTotalDeclaration = getTotalAmount(list, 'declarationAmount')
    emit(
      'getCabinet',
      {
        listTotalVolume,
        listTotalGoodsValue,
        listPurchaseTotalAmount,
        listTotalQuantity,
        listTotalBoxes,
        listTotalGrossweight,
        listTotalWeight,
        listTotalDeclaration,
        listTotalPurchase,
        listTotalTaxRefundPrice,
        listCommissionAmount,
        listInsuranceFee
      },
      {}
    )
  },
  { immediate: true, deep: true }
)

const TableRef = ref()
const checkData = async () => {
  let valid = await TableRef.value.validate()
  if (valid) {
    let tableData = cloneDeep(TableRef.value.tableData)
    return tableData
  } else {
    message.warning('报关单明细提交信息有误')
    return false
  }
}

defineExpose({ tableList, checkData })

watchEffect(() => {
  tableList.value = props.formData.children || []
  updateCustomerNames()
})
</script>
