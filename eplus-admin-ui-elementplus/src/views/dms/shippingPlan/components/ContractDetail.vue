<template>
  <eplus-form-table
    ref="TableRef"
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
    showSummary
    :summaryMethod="handleSummary"
    :maxHeight="props.maxHeight"
    :height="props.maxHeight"
  />
  <UpdateOuterboxSpec
    ref="UpdateOuterboxSpecRef"
    @sure="
      (val, index) => {
        tableList[index].specificationList = val
      }
    "
  />
</template>
<script setup lang="tsx">
import { cloneDeep } from 'lodash-es'
import { SplitKey, volPrecision, VolumeUnit, weightUnit } from '@/utils/config'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import { formatNum, formatTime, formatWeight, getCurrency, getTotalAmount } from '@/utils/index'
import { getSkuInfo } from '@/api/sms/saleContract/domestic'
import CheckStatusLog from '@/views/dms/shippingPlan/components/CheckStatusLog.vue'
import { getDictLabel } from '@/utils/dict'
import { columnWidth } from '@/utils/table'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import UpdateOuterboxSpec from '@/views/pms/product/main/components/UpdateOuterboxSpec.vue'
import { setSourceId } from '@/utils/auth'
import { checkPermi } from '@/utils/permission.js'
import { isAmount, isNumber, isWeight } from '@/utils/is'
import { getOuterbox, getOuterboxVal } from '@/utils/outboxSpec'

defineOptions({ name: 'ContractDetail' })
const props = defineProps<{
  formData
  maxHeight
}>()

const message = useMessage()
const tableList = ref([])
const UpdateOuterboxSpecRef = ref()
let tableColumns = reactive([
  {
    field: 'companyName',
    label: '下单主体',
    width: columnWidth.l
  },
  {
    field: 'shippedAddress',
    label: '发货地点',
    width: columnWidth.l,
    component: (
      <eplus-dict-select
        class="!w-90%"
        dictType={DICT_TYPE.SHIPPED_ADDRESS}
        clearable={false}
      />
    ),
    rules: [{ required: true, message: '请选择发货地点' }]
  },

  {
    field: 'custPo',
    label: '客户PO号',
    width: columnWidth.l,
    component: <el-input></el-input>
  },
  {
    field: 'saleContractCode',
    label: '外销合同',
    width: columnWidth.l
  },
  {
    field: 'custName',
    label: '客户名称',
    width: columnWidth.l
  },
  // {
  //   field: 'skuCode',
  //   label: '产品编号',
  //   width: columnWidth.l,
  //   slot: (item, row: Recordable, index: number) => {
  //     if (row.skuCode) {
  //       return (
  //         <UpdateSku
  //           row={row}
  //           onDetail={() => openDetail(row)}
  //           onUpdate={() => handleUpdateSku(row, index)}
  //         />
  //       )
  //     } else {
  //       return <span>-</span>
  //     }
  //   }
  // },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: columnWidth.xl
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: columnWidth.l,
    fixed: 'left'
  },
  {
    field: 'nameEng',
    label: '英文品名',
    width: columnWidth.l,
    showOverflowTooltip: true
  },
  {
    field: 'name',
    label: '中文品名',
    width: columnWidth.l,
    showOverflowTooltip: true
  },
  {
    field: 'barcode',
    label: '条形码',
    width: columnWidth.l,
    showOverflowTooltip: true
  },
  {
    field: 'shippingQuantity',
    label: '出运数量',
    width: columnWidth.l,
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <>
          <el-input-number
            v-model={row.shippingQuantity}
            min={0}
            max={row.unshippedQuantity}
            controls={false}
            precision={0}
            class="!w-90%"
          />
        </>
      )
    },
    rules: [{ required: true, message: '请输入出运数量' }]
  },
  {
    field: 'shippingAmount',
    label: '出运总金额',
    width: columnWidth.l,
    formatter: (item, row) => {
      return `${getCurrency(row.saleUnitPrice.currency)} ${formatNum(row.shippingQuantity * row.saleUnitPrice.amount)}`
    }
  },
  {
    field: 'saleQuantity',
    label: '数量',
    width: '100',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return formatNum(row.saleQuantity)
    }
  },
  {
    field: 'saleUnitPrice',
    label: '销售单价',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row.saleUnitPrice?.amount ? <EplusMoneyLabel val={row.saleUnitPrice} /> : '-'
    }
  },
  {
    field: 'saleAmount',
    label: '销售金额',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row.saleAmount?.amount ? <EplusMoneyLabel val={row.saleAmount} /> : '-'
    }
  },
  {
    field: 'deliveryDate',
    label: '交货日期',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return formatTime(row.deliveryDate, 'yyyy-MM-dd')
    }
  },
  {
    field: 'freeFlag',
    label: '是否赠品',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return getDictLabel(DICT_TYPE.CONFIRM_TYPE, row.freeFlag)
    }
  },
  // {
  //   field: 'freeQuantity',
  //   label: '赠品数量',
  //   width: columnWidth.l,
  //   formatter: (item, row, index) => {
  //     return formatNum(row.freeQuantity)
  //   }
  // },
  {
    field: 'commodityInspectionFlag',
    label: '是否商检',
    width: columnWidth.l,
    component: (
      <eplus-dict-select
        class="!w-90%"
        dictType={DICT_TYPE.IS_INT}
      />
    ),
    rules: { required: true, message: '请选择' }
  },
  {
    field: 'hsCode',
    label: 'HS编码',
    width: columnWidth.l
  },
  // {
  //   field: 'taxRefundRate',
  //   label: '退税率%',
  //   width: columnWidth.l
  // },
  // {
  //   field: 'taxRefundPrice',
  //   label: '退税金额',
  //   width: columnWidth.l,
  //   formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
  //     return row.taxRefundPrice ? <EplusMoneyLabel val={row.taxRefundPrice} /> : '-'
  //   }
  // },
  // {
  //   field: 'purchaseContractCode',
  //   label: '采购合同',
  //   width: columnWidth.l,
  //   showOverflowTooltip: true
  // },
  {
    field: 'stockPurchaseContractCodes',
    label: '关联采购合同',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      if (row.stockPurchaseContractCodes && Array.isArray(row.stockPurchaseContractCodes)) {
        return row.stockPurchaseContractCodes.join(', ')
      }
      return ''
    },
    showOverflowTooltip: true
  },
  {
    field: 'checkStatus',
    label: '验货结果',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <CheckStatusLog row={row} />
    }
  },
  {
    field: 'purchaseQuantity',
    label: '总采购数量',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return formatNum(row.purchaseQuantity)
    }
  },
  {
    field: 'purchaseUserName',
    label: '采购员',
    width: columnWidth.l
  },
  // {
  //   field: 'purchaseCurrency',
  //   label: '采购币种',
  //   width: columnWidth.l
  // },
  {
    field: 'purchaseWithTaxPrice',
    label: '采购含税单价',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.purchaseWithTaxPrice} />
    }
  },
  {
    field: 'purchaseTotalAmount',
    label: '采购总金额',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.purchaseTotalAmount} />
    }
  },
  {
    field: 'packageTypeName',
    label: '包装方式',
    width: columnWidth.l
  },
  {
    field: 'qtyPerInnerbox',
    label: '内盒装量',
    width: columnWidth.l,
    slot: (item, row, index) => {
      return (
        <el-input-number
          v-model={row.qtyPerInnerbox}
          min={1}
          controls={false}
          precision={0}
          class="!w-90%"
        />
      )
    }
  },
  {
    field: 'qtyPerOuterbox',
    label: '外箱装量',
    width: columnWidth.l,
    slot: (item, row, index) => {
      return (
        <el-input-number
          v-model={row.qtyPerOuterbox}
          min={0}
          controls={false}
          precision={0}
          class="!w-90%"
          disabled={row.splitBoxFlag}
        />
      )
    },
    rules: [{ required: true, message: '请输入应收数量' }]
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    width: '100',
    formatter: (item, row, index) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'boxCount',
    label: '箱数',
    width: '100',
    formatter: (item, row, index) => {
      return <span>{formatNum(row.boxCount * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    width: '150',
    formatter: (item, row, index) => {
      return (
        <div class="flex items-center justify-between">
          <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>

          <Icon
            icon="ep:edit"
            class="ml5px cursor-pointer text-#409EFF"
            onClick={() => {
              UpdateOuterboxSpecRef.value.open(row, index)
            }}
          />
        </div>
      )
    }
  },
  {
    field: 'outerboxVolume',
    label: '外箱体积',
    width: '150',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <div>{getOuterbox(row, 'vol')}</div>
    }
  },
  {
    field: 'totalVolume',
    label: `总体积`,
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return `${formatNum(row.totalVolume / 1000000, volPrecision)} ${VolumeUnit}`
    }
  },
  {
    field: 'outerboxGrossweight',
    label: '外箱毛重',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return (
        <div class="flex items-center justify-between">
          <div>{getOuterbox(row, 'grossweight')}</div>

          <Icon
            icon="ep:edit"
            class="ml5px cursor-pointer text-#409EFF"
            onClick={() => {
              UpdateOuterboxSpecRef.value.open(row, index)
            }}
          />
        </div>
      )
    }
  },
  {
    field: 'totalGrossweight',
    label: '总毛重',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return formatWeight(row.totalGrossweight)
    }
  },
  {
    field: 'outerboxNetweightStr',
    label: '外箱净重',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return (
        <div class="flex items-center justify-between">
          <div>{getOuterbox(row, 'netweight')}</div>

          <Icon
            icon="ep:edit"
            class="ml5px cursor-pointer text-#409EFF"
            onClick={() => {
              UpdateOuterboxSpecRef.value.open(row, index)
            }}
          />
        </div>
      )
    }
  },
  {
    field: 'totalNetweight',
    label: '总净重',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return formatWeight(row.totalNetweight)
    }
  },
  {
    field: 'description',
    label: '中文说明',
    width: columnWidth.l,
    showOverflowTooltip: true
  },
  {
    field: 'descriptionEng',
    label: '英文说明',
    width: columnWidth.l,
    showOverflowTooltip: true
  },
  {
    field: 'stockCode',
    label: '仓库编号',
    width: columnWidth.l
  },
  {
    field: 'stockName',
    label: '仓库名称',
    width: columnWidth.l
  },
  {
    field: 'remark',
    label: '备注',
    width: columnWidth.l
  },
  {
    field: 'commissionAmount',
    label: '佣金金额',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row.commissionAmount?.amount ? <EplusMoneyLabel val={row.commissionAmount} /> : '-'
    }
  },
  {
    field: 'sales',
    label: '业务员',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row?.sales?.nickname
    }
  }
])

const router = useRouter()

const openDetail = (row) => {
  setSourceId(row.skuId)
  if (checkPermi(['pms:csku:query']) && checkPermi(['pms:csku:detail'])) {
    router.push({ path: '/base/product-manage/csku' })
  } else {
    message.error('暂无权限查看详情')
  }
}
const handleUpdateSku = async (row, index) => {
  let res = await getSkuInfo(row.skuCode)
  let defaultQuoteitemList = res?.quoteitemDTOList?.filter((item) => item.defaultFlag === 1)[0]
  tableList.value[index] = Object.assign(tableList.value[index], {
    skuId: res.id,
    skuCode: res.code,
    name: res.name,
    nameEng: res.nameEng,
    commodityInspectionFlag: res.commodityInspectionFlag,
    description: res.description,
    descriptionEng: res.descriptionEng,
    hsCode: res.hsdata?.code,
    taxRefundRate: res.hsdata?.taxRefundRate,
    packageTypeName: defaultQuoteitemList?.packageTypeName,
    qtyPerOuterbox: defaultQuoteitemList?.qtyPerOuterbox,
    outerboxLength: defaultQuoteitemList?.outerboxLength,
    outerboxWidth: defaultQuoteitemList?.outerboxWidth,
    outerboxHeight: defaultQuoteitemList?.outerboxHeight,
    outerboxGrossweight: defaultQuoteitemList?.outerboxGrossweight,
    outerboxGrossweightStr: defaultQuoteitemList?.outerboxGrossweight.weight,
    outerboxNetweight: defaultQuoteitemList?.outerboxNetweight,
    outerboxNetweightStr: defaultQuoteitemList?.outerboxNetweight.weight,
    skuDeletedFlag: 0
  })
}

const weightJson = (item, key, str) => {
  let [w, u] = item[str] ? item[str].split(SplitKey) : []
  item[key] = {
    weight: Number(w),
    unit: u
  }
}

// const weightStr = (item, key, str) => {
//   item[str] = `${item[key].weight || 0}${SplitKey}${item[key].unit || 'kg'}`
// }
const emit = defineEmits(['getCabinet'])
watch(
  () => tableList.value,
  (list) => {
    let listTotalVolume = 0,
      listTotalGoodsValue = [],
      listPurchaseTotalAmount = [],
      listTotalQuantity = 0,
      listTotalBoxes = 0,
      listTotalGrossweight = { weight: 0, unit: '' },
      listTotalWeight = { weight: 0, unit: '' }

    list?.forEach((item: any) => {
      //销售金额
      item.saleAmount.amount = Number(item.saleUnitPrice.amount) * Number(item.saleQuantity)
      item.saleAmount.currency = item.saleUnitPrice.currency

      //采购合计
      item.purchaseTotalAmount.amount =
        Number(item.purchaseWithTaxPrice.amount) * Number(item.purchaseQuantity)
      item.purchaseTotalAmount.currency = item.purchaseWithTaxPrice.currency

      //货值合计
      item.goodsAmount.amount = Number(item.saleUnitPrice.amount) * Number(item.shippingQuantity)
      item.goodsAmount.currency = item.saleUnitPrice.currency
      // listTotalGoodsValue.amount += item.saleAmount.amount
      // listTotalGoodsValue.currency = item.saleAmount.currency
      //箱数计算
      if (item.qtyPerOuterbox > 0) {
        item.boxCount = Math.ceil(Number(item.shippingQuantity) / Number(item.qtyPerOuterbox))
      } else {
        item.boxCount = 0
      }

      //数量合计
      listTotalQuantity += item.shippingQuantity
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
      listTotalGrossweight.weight += item.totalGrossweight.weight
      listTotalGrossweight.unit = weightUnit
      //总净重计算
      item.totalNetweight.weight = getOuterboxVal(item, 'netweight') * Number(item.boxCount)
      item.totalNetweight.unit = weightUnit
      // 净重合计
      listTotalWeight.weight += item.totalNetweight.weight
      listTotalWeight.unit = weightUnit

      // 录入出运数量不可大于待出运数量+本次出运数量
      // item.shipQuantityMax = item.unshippedQuantity
      //   ? item.shippingQuantity + item.unshippedQuantity
      //   : item.shippingQuantity
    })
    //listTotalWeight.weight = formatterPrice(listTotalWeight.weight, 2)
    //货值合计
    listTotalGoodsValue = getTotalAmount(list, 'goodsAmount')
    //采购总金额
    listPurchaseTotalAmount = getTotalAmount(list, 'purchaseTotalAmount')
    emit('getCabinet', {
      listTotalVolume,
      listTotalGoodsValue,
      listPurchaseTotalAmount,
      listTotalQuantity,
      listTotalBoxes,
      listTotalGrossweight,
      listTotalWeight
    })
  },
  { immediate: true, deep: true }
)

const TableRef = ref()
const checkData = async () => {
  let valid = await TableRef.value.validate()
  if (valid) {
    let tableData = cloneDeep(TableRef.value.tableData)

    let arr = tableData.map((item) => {
      return {
        ...item,
        declarationUnitPrice: {
          amount: item.declarationUnitPriceStr,
          currency: item.saleUnitPrice.currency
        }
      }
    })
    return arr
  } else {
    message.warning('销售合同明细提交信息有误')
    return false
  }
}
const handleSummary = (param) => {
  const { columns, data } = param
  const sums: (string | VNode)[] = []
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '合计'
      return
    }
    const sumFileds = [
      'shippingQuantity',
      'saleAmount',
      'declarationAmount',
      'taxRefundPrice',
      'totalVolume',
      'totalNetweight',
      'totalGrossweight',
      'purchaseTotalAmount',
      'stockCost'
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
      if (column.property === 'totalVolume') {
        sums[index] = formatNum(numVal / 1000000, volPrecision)
      } else {
        sums[index] = formatNum(numVal)
      }
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

defineExpose({ tableList, checkData })
watchEffect(() => {
  tableList.value =
    props.formData.children.map((item) => {
      return {
        ...item,
        declarationUnitPriceStr: item.declarationUnitPrice?.amount || 0,
        shippedAddress: item.shippedAddress || 1,
        goodsAmount: { amount: 0, currency: '' },
        purchaseTotalAmount: { amount: 0, currency: '' },
        outerboxGrossweightStr: item.outerboxGrossweight?.weight || 0,
        outerboxNetweightStr: item.outerboxNetweight?.weight || 0
      }
    }) || []
})
</script>
