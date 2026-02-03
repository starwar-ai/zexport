<template>
  <div class="mb15px">
    <el-button @click="handleAdd">选择产品</el-button>
    <el-button @click="handleCopyFromQuotation">从报价单复制</el-button>
    <el-button @click="handleRemove">移除</el-button>
  </div>
  <eplus-form-table
    ref="TableRef"
    selectionFlag
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
    @selection-change="handleSelectionChange"
    showSummary
    :summaryMethod="handleSummary"
    :maxHeight="props.maxHeight"
  />
  <ProductDialog
    ref="ProductDialogRef"
    @sure="handleSure"
    :selectionFlag="true"
    :defaultVal="{}"
    :schema="productColumns"
    :isShowTabs="true"
    :agentTabFlag="true"
  />
  <CopyFromQuotationDialog
    ref="copyFromQuotationDialogRef"
    @confirm="handleCopyFromQuotationConfirm"
  />
  <InventoryQuantityDialog
    ref="InventoryQuantityDialogRef"
    @sure="handleSureInventory"
  />

  <StockLockCreateDia
    ref="StockLockCreateDiaRef"
    @sure="handleSureStockLockCreat"
  />
  <MoqDia
    ref="MoqDiaRef"
    @sure="updateRow"
  />

  <SelectVenderQuote
    channel="exportSale"
    ref="SelectVenderQuoteRef"
    @sure="quoteSure"
  />
</template>
<script setup lang="tsx">
import { ProductDialog } from '@/components/ProductDialog'
import { TableColumn } from '@/types/table'
import { DICT_TYPE, getDictLabel, getDictValue } from '@/utils/dict'
import * as ConfigApi from '@/api/infra/config'
import * as DomesticSaleContractApi from '@/api/sms/saleContract/domestic'

import { isAmount, isNumber, isValidArray, validNumber } from '@/utils/is'
import { cloneDeep } from 'lodash-es'
import StockLockCreateDia from './StockLockCreateDia.vue'
import InventoryQuantityDialog from './InventoryQuantityDialog.vue'
import { formatNum, formatterPrice, getCurrency, getTotalAmountSum } from '@/utils/index'
import { columnWidth } from '@/utils/table'
import CopyFromQuotationDialog from '../exportSaleContract/CopyFromQuotationDialog.vue'
import UpdateSku from './UpdateSku.vue'
import { EplusMoneyLabel, EplusNumInput } from '@/components/EplusMoney'
import { EplusImgEnlarge } from '@/components/EplusTemplate'
import MoqDia from '@/views/sms/quotation/components/MoqDia.vue'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import SelectVenderQuote from '@/views/scm/purchase/components/SelectVenderQuote.vue'
import { setSourceId } from '@/utils/auth'
import { checkPermi } from '@/utils/permission'
import { EplusSkuName } from '@/components/EplusSkuName'
import { moneyPrecision, volPrecision, VolumeUnit } from '@/utils/config'
import { getOuterbox, getOuterboxVal } from '@/utils/outboxSpec'

defineOptions({ name: 'PurchaseProducts' })
const props = defineProps<{
  mode
  formData
  companyIdList
  producedFlag
  contractType
  maxHeight
  rateList
  loading?: boolean
}>()
const rateList = props.rateList

const TableRef = ref()
const message = useMessage()
const ProductDialogRef = ref()
const InventoryQuantityDialogRef = ref()
const copyFromQuotationDialogRef = ref()
const tableList = ref([])
const computedTableData = ref({})
//20尺柜
const twentyFootCabinetFeeConfigValue = ref(0)
//40尺柜
const fortyFootCabinetFeeConfigValue = ref(0)
//40尺高柜
const fortyFootContainerFeeConfigValue = ref(0)
//散货
const bulkHandlingFeeConfigValue = ref(0)
//散货起始价格
const bulkHandlingStartFeeConfigValue = ref(0)
const changeUser = (row, val) => {
  if (val) {
    row.purchaseUser = { userId: val?.id, ...val }
  }
}
const StockLockCreateDiaRef = ref()

const handleLock = (row, index) => {
  if (row.quantity) {
    StockLockCreateDiaRef.value?.open({
      producedFlag: props.producedFlag,
      onlyAvailableQuantityFlag: props.producedFlag === 0 ? 1 : undefined,
      companyIdList: props.companyIdList,
      skuCode: row.code || row.skuCode,
      sourceOrderCode: row.contractCode,
      sourceOrderItemId: row.id,
      stockDetailRespVOList: row.stockDetailRespVOList,
      stockLockSaveReqVOList: row.stockLockSaveReqVOList || [],
      index,
      quantity: row.quantity,
      saleContractCode: props.formData.code || undefined
    })
  } else {
    message.warning('请先录入数量！')
  }
}

const handleCopyFromQuotation = () => {
  if (props.formData?.custId && props.formData?.custCode) {
    copyFromQuotationDialogRef.value.open(props.formData?.custId)
  } else {
    message.error('未选择客户，不可选择报价单！')
  }
}
const handleCopyFromQuotationConfirm = async (list) => {
  let res = await getQuantity(list)
  if (list && list.length > 0) {
    list.forEach((item, index) => {
      item?.id && delete item?.id
      //可用库存赋值
      item.availableQuantity = res[item.code]
      //根据产品弹窗是否多选来控制序号的递增
      item.sortNum =
        list.length === 1 ? tableList.value.length + 1 : tableList.value.length + index + 1
      //佣金类型默认选择佣金金额
      let value = getDictValue(DICT_TYPE.COMMISSION_TYPE, '无')
      if (value) {
        item.commissionSubTotal = 0
        item.commissionType = Number(value)
        item.commissionRateDiasbled = true
        item.commissionAmountFormatDiasbled = true
        item.commissionRate = '0'
        item.commissionAmountFormat = '0'
      }
      let bookingFlag = getDictValue(DICT_TYPE.CONFIRM_TYPE, '否')
      if (bookingFlag) {
        item.bookingFlag = Number(bookingFlag)
      }
      //销售单价 =  合同销售单价 * 币种
      let currencyContract = props.formData.currency
      let currencyPrice = item.quotation?.currency
      item.unitPriceAmount = item.quotation?.amount
        ? (item.quotation?.amount * rateList[currencyPrice]) / rateList[currencyContract]
        : '0'
      item.totalSaleAmountFormat = '0'
      item.cskuCodeDisabled = item.cskuCode ? true : false
      // let defaultUser = item.buyers?.find((obj) => obj.defaultFlag === 1) || item.buyers[0]
      // item.purchaseUserName = defaultUser.nickname
      item.purchaseUser = {
        deptId: item.purchaseUserDeptId,
        deptName: item.purchaseUserDeptName,
        nickname: item.purchaseUserName,
        userCode: null,
        userId: item.purchaseUserId
      }
      //采购总金额默认值
      item.purchaseTotalAmount = { amount: 0, currency: '' }
      //图片
      item.mainPicture = item.picture.find((el) => el.mainFlag == 1) || item.picture[0]
    })
    tableList.value?.push(...list)
    tableList.value.forEach((item) => {
      refershTableItemPage(item, false)
    })
    // 所有项目处理完后，只调用一次聚合计算
    setComputedTableData()
  }
}
const handleOpenSelect = (row) => {
  let usedStockList = []
  tableList.value.forEach((item) => {
    let usedStockItem = {
      index: item.sortNum,
      list: []
    }
    if (item.skuId == row.skuId) {
      usedStockItem.list = item.stockForSaleList
      usedStockList.push(usedStockItem)
    }
  })
  let params = {
    index: row.sortNum,
    skuCode: row.skuCode,
    companyId: props.formData.companyId,
    skuName: row.name,
    usedStockList: usedStockList
  }
  InventoryQuantityDialogRef.value?.open(params)
}
let platformFeeRateRef = ref(0)
let sinosureFeeRateRef = ref(0)
let tableColumns = reactive<TableColumn[]>([
  {
    label: '序号',
    field: 'sortNum',
    fixed: 'left',
    width: columnWidth.s
  },
  {
    field: 'mainPicture',
    label: '图片',
    fixed: 'left',
    width: columnWidth.s,
    slot: (item, row: Recordable, index: number) => {
      return (
        <EplusImgEnlarge
          mainPicture={row?.mainPicture}
          thumbnail={row?.thumbnail}
        />
      )
    }
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    fixed: 'left',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-input
          v-model={row.cskuCode}
          style="width: 150px"
          clearable={true}
          disabled={row.cskuCodeDisabled}
        />
      )
    }
  },
  {
    field: 'barcode',
    label: '条形码',
    fixed: 'left',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-input
          v-model={row.barcode}
          style="width: 150px"
          clearable={true}
        />
      )
    }
  },
  {
    field: 'name',
    label: '中文名称',
    width: columnWidth.xxl,
    slot: (item, row: Recordable, index: number) => {
      return (
        <EplusSkuName
          name={row.name}
          type={row.skuType}
        />
      )
    }
  },
  {
    field: 'quantity',
    label: '数量',
    width: columnWidth.l,
    sortable: true,
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-input-number
          v-model={row.quantity}
          precision={0}
          min={0}
          controls={false}
          clearable={true}
          valueOnClear={0}
          onChange={(value) => {
            quantityChange(row, value)
          }}
        />
      )
    },
    rules: [{ required: true, message: '请输入数量' }]
  },
  {
    field: 'unitPriceAmount',
    label: '销售单价',
    sortable: true,
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <EplusNumInput
          v-model={row.unitPriceAmount}
          style="width: 150px"
          precision={3}
          min={0}
          disabled={row.freeFlag == 1}
          onChange={(value) => {
            refershTableRow(row)
          }}
        />
      )
    },
    rules: [{ required: true, message: '请输入销售单价' }]
  },

  {
    field: 'totalSaleAmountFormat',
    label: '总金额',
    width: columnWidth.l,
    sortable: true,
    slot: (item, row: Recordable, index: number) => {
      return (
        <EplusNumInput
          v-model={row.totalSaleAmountFormat}
          style="width: 150px"
          precision={3}
          disabled
        />
      )
    }
  },

  {
    field: 'purchaseWithTaxPrice',
    label: '采购含税单价',
    width: '150',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.withTaxPriceRemoveFree} />
    }
  },
  {
    field: 'purchaseTotalAmount',
    label: '采购总金额',
    width: '150',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.purchaseTotalAmount} />
    }
  },
  {
    field: 'skuCode',
    label: '产品编号',
    width: columnWidth.xxl,
    sortable: true,
    slot: (item, row: Recordable, index: number) => {
      return (
        <UpdateSku
          row={row}
          onDetail={() => openProductDetail(row)}
          onUpdate={() => handleUpdateSku(row, index)}
        />
      )
    }
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: columnWidth.xxl,
    showOverflowTooltip: true
  },
  {
    field: 'freeFlag',
    label: '是否赠品',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          clearable={false}
          v-model={row.freeFlag}
          dictType={DICT_TYPE.CONFIRM_TYPE}
          onChange={(value) => {
            if (value === 1) {
              row.unitPriceAmount = 0
              refershTableRow(row)
            }
          }}
        />
      )
    },
    rules: [{ required: true, message: '请选择是否赠品' }]
  },
  // {
  //   field: 'freeQuantity',
  //   label: '赠品数量',
  //   width: columnWidth.l,
  //   slot: (item, row: Recordable, index: number) => {
  //     return (
  //       <el-input-number
  //         v-model={row.freeQuantity}
  //         precision={0}
  //         min={0}
  //         max={row.quantity}
  //         controls={false}
  //         clearable={true}
  //         disabled={!row.freeFlag || row.quantity <= 0}
  //         onChange={() => {
  //           refershTableRow(row)
  //         }}
  //       />
  //     )
  //   }
  // },
  {
    field: 'purchaseFreeQuantity',
    label: '采购赠品数量',
    width: columnWidth.l,
    formatter: (item, row) => {
      return row.purchaseFreeQuantity || 0
    }
  },
  {
    field: 'description',
    label: '中文说明',
    width: columnWidth.xxl,
    showOverflowTooltip: true
  },
  {
    field: 'descriptionEng',
    label: '英文说明',
    width: columnWidth.xxl,
    showOverflowTooltip: true
  },
  {
    field: 'orderGrossProfit',
    label: '订单毛利',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      if (row.orderGrossProfit?.amount < 0) {
        return (
          <span class="color-red">
            <EplusMoneyLabel val={row.orderGrossProfit} />
          </span>
        )
      } else {
        return <EplusMoneyLabel val={row.orderGrossProfit} />
      }
    }
  },
  {
    field: 'orderGrossProfitRateFormat',
    label: '毛利率',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      if (row.orderGrossProfitRateFormat < 0) {
        return <span class="color-red">{row.orderGrossProfitRateFormat + ' %'} </span>
      } else {
        return <span>{row.orderGrossProfitRateFormat + ' %'}</span>
      }
    }
  },
  {
    field: 'taxRefundRate',
    label: '退税率(%)',
    width: columnWidth.l
  },
  {
    field: 'taxRefundPrice',
    label: '退税金额(RMB)',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.taxRefundPrice} />
    }
  },
  {
    field: 'commissionType',
    label: '佣金类型',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.commissionType}
          style="width: 120px"
          dictType={DICT_TYPE.COMMISSION_TYPE}
          onChange={(value) => {
            commissionTypeChange(row)
          }}
        />
      )
    },
    rules: [{ required: true, message: '请选择佣金类型' }]
  },
  {
    field: 'commissionRate',
    label: '佣金比例(%)',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <EplusNumInput
          v-model={row.commissionRate}
          disabled={row.commissionRateDiasbled}
          style="width: 150px"
          precision={2}
          min={0}
          onChange={(value) => {
            refershTableRow(row)
          }}
        />
      )
    }
  },
  {
    field: 'commissionAmountFormat',
    label: '佣金金额',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <EplusNumInput
          v-model={row.commissionAmountFormat}
          min={0}
          precision={moneyPrecision}
          class="!w-90%"
          disabled={row.commissionAmountFormatDiasbled}
          onChange={(value) => {
            commissionAmountChange(row)
          }}
        />
      )
    }
  },
  {
    field: 'commissionSubTotal',
    label: '是否扣减总金额',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.commissionSubTotal}
          dictType={DICT_TYPE.IS_INT}
          clearable={false}
          onChange={(value) => {
            refershTableRow(row)
          }}
        />
      )
    }
  },

  {
    field: 'purchaseCurrency',
    label: '采购币种',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <eplus-dict-select
            v-model={row.currency}
            style="width: 90px"
            dictType={DICT_TYPE.CURRENCY_TYPE}
            clearable={true}
            disabled
          />
        )
      }
    }
  },
  // {
  //   field: 'inventoryQuantity', availableQuantity
  //   label: '库存',
  //   minWidth: '150px',
  //   slot: (item, row: Recordable, index: number) => {
  //     return (
  //       <span>
  //         {row?.inventoryQuantity || '-'}
  //         &nbsp;&nbsp;&nbsp;
  //         <span
  //           class="selectQuote"
  //           onClick={() => handleOpenSelect(row)}
  //         >
  //           查看库存
  //         </span>
  //       </span>
  //     )
  //   }
  // },
  {
    field: 'availableQuantity',
    label: '可用库存',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      if (row?.availableQuantity && props.mode != 'change') {
        return (
          <span>
            {row?.availableQuantity || 0}
            &nbsp;&nbsp;&nbsp;
            <el-button
              link
              type="primary"
              onClick={() => handleLock(row, index)}
            >
              分配库存
            </el-button>
          </span>
        )
      } else {
        return <span>{row?.availableQuantity || 0}</span>
      }
    }
  },
  {
    field: 'currentLockQuantity',
    label: '锁定库存',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return <span>{row?.currentLockQuantity || 0}</span>
    }
  },
  {
    field: 'stockLockPrice',
    label: '库存单价',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.stockLockPrice} />
    }
  },
  {
    field: 'stockLockTotalPrice',
    label: '库存总金额',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.stockLockTotalPrice} />
    }
  },
  {
    field: 'needPurQuantity',
    label: '待采购数量',
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return <span>{row?.needPurQuantity || 0}</span>
    }
  },
  // {
  //   field: 'purchaseUnitPrice',
  //   label: '采购单价',
  //   width: columnWidth.l,
  //   formatter: (item, row: Recordable, index: number) => {
  //     if (row.purchaseUnitPrice?.amount) {
  //       return (
  //         Number(row.purchaseUnitPrice.amount).toFixed(2) + ' ' + row.purchaseUnitPrice.currency
  //       )
  //     } else {
  //       return '-'
  //     }
  //   }
  // },
  {
    field: 'purchasePackagingPrice',
    label: '包装价',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.purchasePackagingPrice} />
    }
  },

  {
    field: 'nameEng',
    label: '英文名称',
    width: columnWidth.xxl
  },
  {
    field: 'unit',
    label: '计量单位',
    width: columnWidth.l,
    formatter: (item, row) => {
      return getDictLabel(DICT_TYPE.MEASURE_UNIT, row?.unit)
    }
  },
  {
    field: 'qtyPerInnerbox',
    label: '内盒装量',
    width: columnWidth.l
  },
  {
    field: 'qtyPerOuterbox',
    label: '外箱装量',
    width: columnWidth.l
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    width: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'boxCount',
    label: '箱数',
    width: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return formatNum(row.boxCount * row.specificationList?.length || 1)
    }
  },
  {
    field: 'volume',
    label: `总体积(${VolumeUnit})`,
    width: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      if (row.volume) {
        return formatNum(Number(row.volume) / 1000000, volPrecision)
      } else {
        return '-'
      }
    }
  },
  {
    field: 'reorderFlag',
    label: '是否翻单',
    width: columnWidth.l,
    formatter: (item, row) => {
      return getDictLabel(DICT_TYPE.CONFIRM_TYPE, row?.reorderFlag)
    }
  },
  {
    field: 'outerboxGrossweight',
    label: '外箱毛重 kg',
    width: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return getOuterbox(row, 'grossweight')
    }
  },
  {
    field: 'outerboxNetweight',
    label: '外箱净重 kg',
    width: columnWidth.l,
    formatter: (item, row: Recordable, index: number) => {
      return getOuterbox(row, 'netweight')
    }
  },

  {
    field: 'venderName',
    label: '厂商名称',
    width: columnWidth.xl,
    formatter: (item, row: Recordable, index: number) => {
      return (
        <span>
          {row?.venderName}
          &nbsp;&nbsp;&nbsp;
          <el-button
            link
            type="primary"
            onClick={() => selectQuote(row, index)}
          >
            选择报价
          </el-button>
        </span>
      )
    }
  },
  {
    field: 'purchaseUserId',
    label: '采购员',
    editable: true,
    width: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-user-select
          v-model={row.purchaseUserId}
          onChange={(value) => {
            changeUser(row, value)
          }}
        ></eplus-user-select>
      )
    },
    rules: [{ required: true, message: '请选择采购员' }]
  },
  {
    field: 'venderDeliveryDate',
    label: '工厂交期',
    width: columnWidth.xl,
    component: (
      <el-date-picker
        clearable
        valueFormat="x"
        type="date"
        style="width: 100%"
        disabled-date={(time) => {
          return time.getTime() > new Date(props.formData.custDeliveryDate).getTime()
        }}
      />
    ),
    rules: [{ required: true, message: '请选择工厂交期' }]
  },

  {
    field: 'inspectionFee',
    label: '验货费',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.inspectionFee} />
    }
  },
  {
    field: 'packageTypeName',
    label: '包装方式',
    width: columnWidth.l
  },
  {
    field: 'trailerFee',
    label: '拖柜费',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.trailerFee} />
    }
  },
  {
    field: 'insuranceFee',
    label: '保险费',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.insuranceFee} />
    }
  },
  {
    field: 'platformFee',
    label: '平台费',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.platformFee} />
    }
  },
  {
    field: 'forecastTotalCost',
    label: '预估总费用',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.forecastTotalCost} />
    }
  },
  {
    field: 'innerCalcPrice',
    label: '定价差价',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.innerCalcPrice} />
    }
  },
  {
    field: 'sinosureFee',
    label: '中信保费用',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.sinosureFee} />
    }
  },
  {
    field: 'action',
    label: '操作',
    width: columnWidth.l,
    fixed: 'right',
    align: 'left',
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-button
          link
          type="primary"
          onClick={async () => {
            await delRow(index)
          }}
        >
          移除
        </el-button>
      )
    }
  }
])

const SelectVenderQuoteRef = ref()
const selectQuote = (row, index) => {
  SelectVenderQuoteRef.value?.open(row, index)
}

const quoteSure = (...args: unknown[]) => {
  const dataObj = args[0] as {
    index: number
    data: Recordable
    row: Recordable
  }
  tableList.value[dataObj.index] = Object.assign(tableList.value[dataObj.index], {
    ...dataObj.data,
    id: dataObj?.row?.id,
    purchaseWithTaxPrice: dataObj?.data?.withTaxPrice,
    withTaxPriceRemoveFree: dataObj?.data?.withTaxPrice
  })
  refershTableRow(tableList.value[dataObj.index])
}

const MoqDiaRef = ref()
const currentRow = ref()
const quantityChange = (row, value) => {
  if (value < row.currentLockQuantity) {
    row.quantity = row.currentLockQuantity
  } else {
    row.quantity = value
  }

  refershTableRow(row)
  currentRow.value = row
  let quantity = Number(row.quantity)
  let remainder = quantity % Number(row.qtyPerOuterbox),
    integer = Math.floor(quantity / Number(row.qtyPerOuterbox))
  if (remainder > 0) {
    row.boxCount = integer + 1
    if (integer * Number(row.qtyPerOuterbox) < row.currentLockQuantity) {
      let des = `输入的数量计算得出箱数为${integer},余数为${remainder}。您可以选择向上取整箱数起订量为${(integer + 1) * Number(row.qtyPerOuterbox)}为${integer + 1}`
      MoqDiaRef.value.open(des, 'floor')
    } else {
      let des = `输入的数量计算得出箱数为${integer},余数为${remainder}。您可以选择向下取整起订量为${integer * Number(row.qtyPerOuterbox)}箱数为${integer}，或者向上取整箱数起订量为${(integer + 1) * Number(row.qtyPerOuterbox)}为${integer + 1}`
      MoqDiaRef.value.open(des)
    }
  } else {
    row.boxCount = quantity / Number(row.qtyPerOuterbox)
  }
}
const updateRow = (type) => {
  let quantity = Number(currentRow.value.quantity)
  let integer = Math.floor(quantity / Number(currentRow.value.qtyPerOuterbox))
  if (type === 'ceil') {
    currentRow.value.boxCount = integer + 1
    currentRow.value.quantity = (integer + 1) * Number(currentRow.value.qtyPerOuterbox)
  } else if (type === 'floor') {
    currentRow.value.boxCount = integer
    currentRow.value.quantity = integer * Number(currentRow.value.qtyPerOuterbox)
  }
  refershTableRow(currentRow.value)
}

let productColumns = reactive<TableColumn[]>([
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    minWidth: columnWidth.l
  },
  {
    field: 'mainPicture',
    label: '图片',
    minWidth: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return (
        <EplusImgEnlarge
          mainPicture={row?.mainPicture}
          thumbnail={row?.thumbnail}
        />
      )
    }
  },
  {
    field: 'skuName',
    label: '中文名称',
    minWidth: columnWidth.l
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    minWidth: columnWidth.l
  },
  {
    field: 'oskuCode',
    label: '自营产品货号',
    minWidth: columnWidth.l
  },
  {
    field: 'categoryName',
    label: '分类',
    minWidth: columnWidth.l
  },
  {
    field: 'brandName',
    label: '品牌',
    minWidth: columnWidth.l
  },
  {
    field: 'skuType',
    label: '产品类型',
    minWidth: columnWidth.l,
    slot: (item, row: Recordable, index: number) => {
      return getDictLabel(DICT_TYPE.SKU_TYPE, row?.skuType)
    }
  }
])

const handleAdd = async () => {
  if (props.formData?.custId && props.formData?.custCode) {
    ProductDialogRef.value.open([], props.formData?.custCode)
  } else {
    message.error('未选择客户，不可选择产品信息！')
  }
}

const handleRemove = () => {
  if (selectedList.value?.length > 0) {
    let delArr = selectedList.value.map((el) => {
      return el.sortNum
    })
    tableList.value = tableList.value.filter((item, index) => {
      if (!delArr.includes(item.sortNum)) {
        return item
      }
    })
    tableList.value.forEach((item, index) => {
      return (item.sortNum = index + 1)
    })
    setComputedTableData()
  } else {
    message.error('请选择移除的数据')
  }
}

const delRow = (index) => {
  tableList.value.splice(index, 1)
  tableList.value.forEach((item, index) => {
    return (item.sortNum = index + 1)
  })
  setComputedTableData()
}
const selectedList = ref([])
const handleSelectionChange = (val) => {
  selectedList.value = val
}
const router = useRouter()
const emit = defineEmits(['open', 'loaded'])
const openProductDetail = (row) => {
  if (row.skuDeletedFlag) {
    emit('open', row)
  } else {
    setSourceId(row.skuId)
    if (row.custProFlag == 1) {
      if (checkPermi(['pms:csku:query']) && checkPermi(['pms:csku:detail'])) {
        router.push({ path: '/base/product-manage/csku' })
      } else {
        message.error('暂无权限查看详情')
      }
    } else if (row.custProFlag == 0 && row.ownBrandFlag == 1) {
      if (checkPermi(['pms:own-brand-sku:query']) && checkPermi(['pms:own-brand-sku:detail'])) {
        router.push({ path: '/base/product-manage/own' })
      } else {
        message.error('暂无权限查看详情')
      }
    } else if (row.custProFlag == 0 && row.ownBrandFlag == 0) {
      if (checkPermi(['pms:sku:query']) && checkPermi(['pms:sku:detail'])) {
        router.push({ path: '/base/product-manage/main' })
      } else {
        message.error('暂无权限查看详情')
      }
    }
  }
}

const handleUpdateSku = async (row, index) => {
  const oldId = tableList.value[index]?.id
  let res = await DomesticSaleContractApi.getSkuInfo(row.skuCode)
  let defaultQuoteitemList = res?.quoteitemDTOList?.filter((item) => item.defaultFlag === 1)[0]
  // let defaultUser =
  //   defaultQuoteitemList?.buyers.find((obj) => obj.defaultFlag === 1) ||
  //   defaultQuoteitemList?.buyers[0]
  tableList.value[index] = Object.assign(tableList.value[index], {
    skuId: res.id,
    ...res,
    ...defaultQuoteitemList,
    skuCode: res?.code,
    // totalPurchaseAmount: defaultQuoteitemList?.withTaxPrice * tableList.value[index].quantity || res?.quantity || 0,
    purchaseCurrency: defaultQuoteitemList?.currency,
    supplierName: defaultQuoteitemList?.venderName,
    purchaseUnitPrice: defaultQuoteitemList?.unitPrice,
    purchasePackagingPrice: defaultQuoteitemList?.packagingPrice,
    purchaseWithTaxPrice: defaultQuoteitemList?.withTaxPrice,
    name: res?.skuName,
    unit: res?.measureUnit,
    quantity: tableList.value[index].quantity || res?.quantity || 0,
    skuDeletedFlag: 0,
    purchaseUserList: defaultQuoteitemList?.buyers,
    purchaseUser: {
      deptId: defaultQuoteitemList.purchaseUserDeptId,
      deptName: defaultQuoteitemList.purchaseUserDeptName,
      nickname: defaultQuoteitemList.purchaseUserName,
      userCode: null,
      userId: defaultQuoteitemList.purchaseUserId
    },
    mainPicture: res.picture.find((el) => el.mainFlag == 1) || res.picture[0],
    id: oldId,
    taxRefundRate: res.hsdata?.taxRefundRate
  })
  refershTableRow(tableList.value[index])
}

const getQuantity = async (list) => {
  let data: any[] = []
  list.map((item) => {
    data.push({
      producedFlag: props.producedFlag,
      companyIdList: props.companyIdList,
      skuCode: item.skuCode,
      sourceOrderCode: props.formData.code || undefined,
      sourceOrderType: 1,
      saleContractCode: props.formData.code || undefined
    })
  })
  //
  //
  return await DomesticSaleContractApi.queryTotalStock(data)
}
const handleSure = async (list) => {
  let res = await getQuantity(list)
  if (list && list.length > 0) {
    list.forEach((item, index) => {
      ;((item.trailerFee = item.trailerFee ? item.trailerFee : { amount: '0', currency: 'RMB' }),
        (item.orderGrossProfit = item.orderGrossProfit
          ? item.orderGrossProfit
          : { amount: '0', currency: 'RMB' }),
        (item.taxRefundPrice = item.taxRefundPrice
          ? item.taxRefundPrice
          : { amount: '0', currency: 'RMB' }),
        (item.purchasePackagingPrice = item.purchasePackagingPrice
          ? item.purchasePackagingPrice
          : { amount: '0', currency: 'RMB' }),
        (item.inspectionFee = item.inspectionFee
          ? item.inspectionFee
          : { amount: '0', currency: 'RMB' }),
        (item.insuranceFee = item.insuranceFee
          ? item.insuranceFee
          : { amount: '0', currency: 'RMB' }),
        (item.platformFee = item.platformFee ? item.platformFee : { amount: '0', currency: 'RMB' }),
        (item.forecastTotalCost = item.forecastTotalCost
          ? item.forecastTotalCost
          : { amount: '0', currency: 'RMB' }),
        (item.totalSaleAmount = item.totalSaleAmount
          ? item.totalSaleAmount
          : { amount: '0', currency: 'RMB' }),
        (item.unitPrice = item.unitPrice ? item.unitPrice : { amount: '0', currency: 'RMB' }),
        (item.commissionAmount = item.commissionAmount
          ? item.commissionAmount
          : { amount: '0', currency: 'RMB' }),
        (item.sinosureFee = item.sinosureFee ? item.sinosureFee : { amount: '0', currency: 'USD' }),
        (item.withTaxPriceRemoveFree = item.purchaseWithTaxPrice))
      item?.id && delete item?.id
      //可用库存赋值
      item.availableQuantity = res[item.code]
      //根据产品弹窗是否多选来控制序号的递增
      item.sortNum =
        list.length === 1 ? tableList.value.length + 1 : tableList.value.length + index + 1
      //佣金类型默认选择佣金金额
      let value = getDictValue(DICT_TYPE.COMMISSION_TYPE, '无')
      if (value) {
        item.commissionSubTotal = 0
        item.commissionType = Number(value)
        item.commissionRateDiasbled = true
        item.commissionAmountFormatDiasbled = true
        item.commissionRate = '0'
        item.commissionAmountFormat = '0'
      }
      let bookingFlag = getDictValue(DICT_TYPE.CONFIRM_TYPE, '否')
      if (bookingFlag) {
        item.bookingFlag = Number(bookingFlag)
      }
      //销售单价 =  合同销售单价 * 币种
      let currencyContract = props.formData.currency
      let currencyPrice = item.price?.currency
      item.unitPriceAmount = item.price?.amount
        ? (item.price?.amount * rateList[currencyPrice]) / rateList[currencyContract]
        : '0'
      item.totalSaleAmountFormat = '0'
      item.cskuCodeDisabled = item.cskuCode ? true : false
      // let defaultUser =
      //   item.purchaseUserList?.find((obj) => obj.defaultFlag === 1) || item.purchaseUserList[0]
      // item.purchaseUserName = defaultUser.nickname
      item.purchaseUser = {
        deptId: item.purchaseUserDeptId,
        deptName: item.purchaseUserDeptName,
        nickname: item.purchaseUserName,
        userCode: null,
        userId: item.purchaseUserId
      }
      //采购总金额默认值
      item.purchaseTotalAmount = { amount: 0, currency: '' }
      //是否赠品赋值
      item.freeFlag = 0
      //客户货号赋值
      item.cskuCode = item.cskuCode || item.oskuCode || ''
      //工厂交期赋值
      if (props.formData.custDeliveryDate) {
        item.venderDeliveryDate =
          new Date(props.formData.custDeliveryDate).getTime() - 1000 * 60 * 60 * 24 * 7
      }
    })
    tableList.value?.push(...list)
    tableList.value.forEach((item) => {
      refershTableItemPage(item, false)
    })
    // 所有项目处理完后，只调用一次聚合计算
    setComputedTableData()
  }
}

const handleSureStockLockCreat = (obj) => {
  let item: any = tableList.value[obj.index]
  item.availableQuantity = obj.availableQuantityTotal
  item.currentLockQuantity = obj.lockQuantityTotal
  item.stockLockSaveReqVOList = obj.list
  //内部客户更新 销售单价 为库存单价
  let totalAmount = 0,
    totalNum = 0
  obj.list.forEach((el) => {
    totalNum += el.sourceOrderLockedQuantity
    totalAmount += el.price.amount * el.sourceOrderLockedQuantity
  })
  let currencyContract = props.formData.currency
  let currencyPrice = obj.list[0].price.currency
  item.stockLockPrice = {
    amount: totalAmount / totalNum,
    currency: currencyPrice
  }
  item.stockLockTotalPrice = {
    amount: totalAmount,
    currency: currencyPrice
  }
  if (!props.producedFlag) {
    item.unitPriceAmount =
      ((totalAmount / totalNum) * rateList[currencyPrice]) / rateList[currencyContract]
  }
  refershTableRow(item)
}

// watch(
//   () => tableList.value,
//   (list) => {
//     //库存采购总金额
//     let listTotalStockCost = {
//       amount: 0,
//       currency: ''
//     }
//     list.forEach((item: any) => {
//       if (item.quantity > 0) {
//         let val = Number(item.quantity) - Number(item.currentLockQuantity || 0)
//         item.needPurQuantity = val < 0 ? 0 : val
//         item.purchaseTotalAmount.amount =
//           Number(item.purchaseWithTaxPrice?.amount) * Number(item.needPurQuantity)
//         item.purchaseTotalAmount.currency = item.purchaseWithTaxPrice?.currency

//         listTotalStockCost.amount += Number(item.stockLockTotalPrice?.amount) || 0
//         listTotalStockCost.currency = item.stockLockTotalPrice?.currency || 'RMB'
//       }
//     })
//     computedTableData.value = {
//       ...computedTableData.value,
//       listTotalStockCost: listTotalStockCost
//     }
//   },
//   {
//     deep: true,
//     immediate: true
//   }
// )

//--修改库存完成
const handleSureInventory = (list) => {
  let arr = tableList.value
  arr.forEach((item: any) => {
    if (item?.sortNum === list[0]) {
      let arr = item.stockForSaleList
      if (arr?.length) {
        let usedIds = arr.map((item) => {
          return item.id
        })
        list[1].forEach((ls) => {
          if (usedIds.includes(ls.id)) {
            arr = arr.filter((a) => {
              if (a.id === ls.id) {
                a.lockQuantity = ls.lockQuantity
              }
              return a
            })
          } else {
            arr.push(ls)
          }
        })
        item.stockForSaleList = arr
      } else {
        item.stockForSaleList = list[1]
      }
      let inventoryQuantity = 0
      item.stockForSaleList.forEach((element) => {
        inventoryQuantity += Number(element.lockQuantity)
      })
      item.inventoryQuantity = inventoryQuantity
    }
    tableList.value = arr
  })
}

//修改佣金金额
const commissionAmountChange = (row) => {
  row.commissionAmountComputed = row.commissionAmountFormat
  refershTableRow(row)
}

const parentPatamsRef = ref<object>({})
const setParentParams = (param) => {
  parentPatamsRef.value[param.key] = param.value
}

const getParantParams = (pKey) => {
  let value
  for (let key of Object.keys(parentPatamsRef.value)) {
    if (key == pKey) {
      value = parentPatamsRef.value[key]
    }
  }
  return value
}

const refershTableRow = (row) => {
  refershTableItemPage(row, true)
}
//计算结果赋值
const setComputedTableData = () => {
  if (!platformFeeRateRef.value) {
    message.error('平台费比例配置项为空')
    return
  }
  if (!sinosureFeeRateRef.value) {
    message.error('中信保比例配置项为空')
    return
  }
  if (!twentyFootCabinetFeeConfigValue.value) {
    message.error('20尺柜value值配置项为空')
    return
  }
  if (!fortyFootCabinetFeeConfigValue.value) {
    message.error('40尺柜value值配置项为空')
    return
  }
  if (!fortyFootContainerFeeConfigValue.value) {
    message.error('40尺高柜value值配置项为空')
    return
  }
  if (!bulkHandlingFeeConfigValue.value) {
    message.error('散货一立方米价格配置项为空')
    return
  }
  if (!bulkHandlingStartFeeConfigValue.value) {
    message.error('散货起始价格配置项为空')
    return
  }
  let trailerFeeContract = getParantParams('trailerFeeContract')
  if (!trailerFeeContract && trailerFeeContract != 0) {
    trailerFeeContract = props.formData.trailerFee?.amount
  }
  //采购合计
  // let totalPurchaseSum = '0'
  //数量合计
  let totalQuantitySum = '0'
  //体积合计
  let totalVolumeSum = '0'
  //佣金金额合计
  let commissionAmountSum = '0'
  //扣减金额合计
  let subCommissionAmountSum = '0'
  //箱数合计
  let totalBoxesSum = '0'
  //平台费合计
  let platformFeeAmountSum = '0'
  //货值合计
  let totalGoodsValueAmountSum = '0'
  //中信保费用
  let sinosureFeeSum = '0'
  //毛重合计
  let totalGrossWeightSum = '0'
  //净重合计
  let totalWeightSum = '0'
  //退税率合计
  let totalVatRefundAmountSum = '0'
  //预计包材合计
  let estimatedPackingMaterialsSum = '0'
  //配件费合计
  let accessoriesPurchaseTotalSum = '0'
  //库存合计
  let listTotalStockCost = {
    amount: 0,
    currency: ''
  }

  let fortyFootContainerNum = 0
  let twentyFootCabinetNum = 0
  let fortyFootCabinetNum = 0
  let bulkHandlingVolume = 0
  let bulkHandlingMoney = 0
  //预估总运费
  let estimatedTotalFreightSum = '0'
  //采购币种
  let purchaseCurrency = null
  tableList.value.forEach((item: any) => {
    purchaseCurrency = item.purchaseCurrency
    //总体积：明细体积和
    if (item.volume) {
      totalVolumeSum = Number(totalVolumeSum) + Number(item.volume)
    }
    //佣金金额：明细佣金和
    if (item.commissionAmountComputed) {
      commissionAmountSum = Number(commissionAmountSum) + Number(item.commissionAmountComputed)
      //佣金否扣减总金额为是，需要将佣金计算到成本
      if (item.commissionSubTotal == getDictValue(DICT_TYPE.IS_INT, '是')) {
        subCommissionAmountSum =
          Number(subCommissionAmountSum) + Number(item.commissionAmount.amount)
      }
    }
    //箱数合计:明细箱数和
    if (item.boxCount) {
      totalBoxesSum = Number(totalBoxesSum) + Number(item.boxCount)
    }
    //平台费：明细平台费和
    if (item.platformFee?.amount) {
      platformFeeAmountSum = Number(platformFeeAmountSum) + Number(item.platformFee?.amount)
    }
    //货值合计：明细销售总金额和
    if (item.totalSaleAmountComputed) {
      totalGoodsValueAmountSum =
        Number(totalGoodsValueAmountSum) + Number(item.totalSaleAmountComputed)
    }
    //中信保费用
    if (item.sinosureFee?.amount) {
      sinosureFeeSum = Number(sinosureFeeSum) + Number(item.sinosureFee?.amount)
    }
    //毛重合计:sum(外箱毛重*箱数)
    if (item.boxCount && Number(getOuterboxVal(item, 'grossweight'))) {
      totalGrossWeightSum =
        Number(totalGrossWeightSum) +
        Number(item.boxCount) * Number(getOuterboxVal(item, 'grossweight'))
    }

    //净重合计:sum(外箱净重*箱数)
    if (item.boxCount && Number(getOuterboxVal(item, 'netweight'))) {
      totalWeightSum =
        Number(totalWeightSum) + Number(item.boxCount) * Number(getOuterboxVal(item, 'netweight'))
    }

    //采购合计:sum(采购总金额)
    // if (item.purchaseWithTaxPrice?.amount && item.purchaseQuantity) {
    //   totalPurchaseSum = roundToSixDecimals(
    //     Number(totalPurchaseSum) +
    //       Number(item.purchaseWithTaxPrice?.amount) * Number(item.purchaseQuantity)
    //   )
    // }
    //退税合计:sum(退税金额)
    if (item.taxRefundPrice.amount && props.contractType == 1) {
      totalVatRefundAmountSum = Number(totalVatRefundAmountSum) + Number(item.taxRefundPrice.amount)
    }
    //数量合计:sum(数量)
    if (item.quantity) {
      totalQuantitySum = Number(totalQuantitySum) + Number(item.quantity)
      //预计包材合计：sum(包装价*数量)
      if (item.purchasePackagingPrice?.amount) {
        estimatedPackingMaterialsSum =
          Number(estimatedPackingMaterialsSum) +
          Number(item.purchasePackagingPrice?.amount) * Number(item.quantity)
      }
    }
    //待采购数量赋值
    let val = Number(item.quantity) - Number(item.currentLockQuantity || 0)
    item.needPurQuantity = val < 0 ? 0 : val
    item.purchaseTotalAmount.amount =
      Number(item.purchaseWithTaxPrice?.amount) * Number(item.needPurQuantity)
    item.purchaseTotalAmount.currency = item.purchaseWithTaxPrice?.currency
    //配件总费用：产品类型为配件的 含税总金额*数量
    if (item.purchaseWithTaxPrice?.amount && item.skuType) {
      if (getDictLabel(DICT_TYPE.SKU_TYPE, item.skuType) == '配件') {
        accessoriesPurchaseTotalSum =
          Number(item.purchaseWithTaxPrice?.amount) * Number(item.quantity)
      }
    }

    //库存金额累计
    listTotalStockCost.amount += Number(item.stockLockTotalPrice?.amount || 0)
    listTotalStockCost.currency = item.stockLockTotalPrice?.currency || 'RMB'
  })
  let listTotalPurchase = getTotalAmountSum(
    tableList.value,
    'purchaseWithTaxPrice',
    'needPurQuantity'
  )
  if (totalVolumeSum) {
    let totalVolumeFormat = Number(totalVolumeSum) / 1000000
    //容器计算
    let remainNum = Number(totalVolumeFormat)
    if (Number(remainNum) > 68) {
      fortyFootContainerNum = (Number(totalVolumeFormat) / 68) | 0
      remainNum = Number(remainNum) - 68 * Number(fortyFootContainerNum)
    }
    if (Number(remainNum) > 59 && Number(remainNum) <= 68) {
      fortyFootContainerNum += 1
    } else {
      if (Number(remainNum) > 49 && Number(remainNum) <= 59) {
        fortyFootCabinetNum = 1
      } else {
        if (Number(remainNum) > 29 && Number(remainNum) <= 49) {
          twentyFootCabinetNum = 1
          remainNum = Number(remainNum) - 29
        }
        if (Number(remainNum) >= 20 && Number(remainNum) <= 29) {
          twentyFootCabinetNum += 1
        } else {
          bulkHandlingMoney = bulkHandlingStartFeeConfigValue.value
          if (remainNum > 1) {
            bulkHandlingMoney =
              Number(bulkHandlingMoney) +
              Number(bulkHandlingFeeConfigValue.value) * (Math.ceil(remainNum) - 1)
          }
          if (Number(bulkHandlingMoney) - Number(twentyFootCabinetFeeConfigValue.value) > 0) {
            twentyFootCabinetNum += 1
            bulkHandlingVolume = 0
            bulkHandlingMoney =
              Number(bulkHandlingMoney) - Number(twentyFootCabinetFeeConfigValue.value)
          } else {
            bulkHandlingVolume = remainNum * 1000000
          }
        }
      }
    }

    //预估总运费计算 （订舱杂费先不管）
    //[销售单]预估总运费
    //运输方式=海运， 如果价格条款=FOB，不计算海运费（总运费=拖柜费），否则计算海运费（总运费=拖柜费+海运费）
    //运输方式=  非海运， 不计算海运费（总运费=拖柜费）
    // 海运费 -> 当自定义表单共用明细表中key为20尺柜时，(对应value*销售单中20尺柜)
    //          当自定义表单共用明细表中key为40尺柜时，(对应value*销售单中40尺柜)
    //          当自定义表单共用明细表中key为40尺高柜时，(对应value*销售单中40尺高柜)
    //          当自定义表单共用明细表中key为散货/M3时，(对应value*销售单中散货/M3)
    //运输方式
    let transportTypeContract = getParantParams('transportTypeContract')
    if (!transportTypeContract) {
      transportTypeContract = props.formData.transportType
    }
    //价格条款
    let settlementTermTypeContract = getParantParams('settlementTermTypeContract')
    if (!settlementTermTypeContract) {
      settlementTermTypeContract = props.formData.settlementTermType
    }
    if (transportTypeContract == '2') {
      const rateUsd = rateList['USD']
      //由于尺柜费用默认USD,需要转成RMB
      if (twentyFootCabinetNum && twentyFootCabinetFeeConfigValue.value) {
        estimatedTotalFreightSum =
          Number(estimatedTotalFreightSum) +
          Number(twentyFootCabinetNum) * Number(twentyFootCabinetFeeConfigValue.value)
      }
      if (fortyFootCabinetNum && fortyFootCabinetFeeConfigValue.value) {
        estimatedTotalFreightSum =
          Number(estimatedTotalFreightSum) +
          Number(fortyFootCabinetNum) * Number(fortyFootCabinetFeeConfigValue.value)
      }
      if (fortyFootContainerNum && fortyFootContainerFeeConfigValue.value) {
        estimatedTotalFreightSum =
          Number(estimatedTotalFreightSum) +
          Number(fortyFootContainerNum) * Number(fortyFootContainerFeeConfigValue.value)
      }
      if (Number(bulkHandlingVolume) > 0 && bulkHandlingMoney) {
        estimatedTotalFreightSum = Number(estimatedTotalFreightSum) + Number(bulkHandlingMoney)
      }
    }
    if (trailerFeeContract) {
      estimatedTotalFreightSum = Number(estimatedTotalFreightSum) + Number(trailerFeeContract)
    }
  }

  setParentParams({
    key: 'estimatedTotalFreightContract',
    value: roundToSixDecimals(Number(estimatedTotalFreightSum))
  })
  setParentParams({
    key: 'totalGoodsValueContract',
    value: roundToSixDecimals(Number(totalGoodsValueAmountSum))
  })
  setParentParams({ key: 'totalVolumeContract', value: roundToSixDecimals(Number(totalVolumeSum)) })

  let obj = {
    purchaseCurrency: purchaseCurrency,
    // totalPurchaseSum: totalPurchaseSum,
    totalQuantitySum: totalQuantitySum,
    totalVolumeSum: totalVolumeSum,
    commissionAmountSum: commissionAmountSum,
    totalBoxesSum: totalBoxesSum,
    platformFeeAmountSum: platformFeeAmountSum,
    totalGoodsValueAmountSum: totalGoodsValueAmountSum,
    totalGrossWeightSum: totalGrossWeightSum,
    totalWeightSum: totalWeightSum,
    totalVatRefundAmountSum: totalVatRefundAmountSum,
    estimatedPackingMaterialsSum: estimatedPackingMaterialsSum,
    fortyFootContainerNum: fortyFootContainerNum,
    twentyFootCabinetNum: twentyFootCabinetNum,
    fortyFootCabinetNum: fortyFootCabinetNum,
    bulkHandlingVolume: bulkHandlingVolume,
    estimatedTotalFreightSum: estimatedTotalFreightSum,
    accessoriesPurchaseTotalSum: accessoriesPurchaseTotalSum,
    listTotalPurchase: listTotalPurchase,
    sinosureFeeSum: sinosureFeeSum,
    subCommissionAmountSum: subCommissionAmountSum,
    listTotalStockCost: listTotalStockCost
  }
  computedTableData.value = obj
  refershTableItemAll()
}

//刷新页面展示数据
const refershTableItemPage = (item, shouldUpdateAggregate = false) => {
  let currencyContract = getParantParams('currencyContract')
  if (!currencyContract) {
    currencyContract = props.formData.currency
  }
  item.platformFee.currency = currencyContract
  item.totalSaleAmount.currency = currencyContract
  item.unitPrice.currency = currencyContract
  if (item.unitPriceAmount) {
    item.unitPrice.amount = item.unitPriceAmount
  } else {
    item.unitPrice.amount = '0'
  }
  item.commissionAmount.currency = currencyContract
  let rate = rateList[currencyContract]

  //箱数计算（数量/外箱装量）向上取整
  if (item.qtyPerOuterbox && item.quantity && Number(item.qtyPerOuterbox) > 0) {
    item.boxCount = Math.ceil(Number(item.quantity) / Number(item.qtyPerOuterbox))
  } else {
    item.boxCount = '0'
  }
  //体积计算 外箱体积(四舍五入）*箱数  保留2位小数
  item.volume = roundToSixDecimals(getOuterboxVal(item, 'vol') * Number(item.boxCount))
  //销售总金额计算（订单价格 * 销售单币种汇率 * 订单数量）  item.platformFeeComputed = '0'
  item.totalSaleAmountComputed = '0'
  if (item.unitPriceAmount && item.quantity) {
    item.totalSaleAmountComputed = roundToSixDecimals(
      Number(item.unitPriceAmount) * Number(item.quantity)
    )
    //平台费计算
    if (rate && platformFeeRateRef.value) {
      item.platformFee.amount = roundToSixDecimals(
        Number(item.totalSaleAmountComputed) * Number(platformFeeRateRef.value)
      )
    } else {
      item.platformFee.amount = '0'
    }
  } else {
    item.totalSaleAmountComputed = '0'
    item.platformFee.amount = '0'
  }
  item.totalSaleAmountFormat = formatterPrice(item.totalSaleAmountComputed, moneyPrecision)
  item.totalSaleAmount.amount = item.totalSaleAmountComputed

  //中信保费用计算 如客户在客户资料设置了要中信保，则销售合同 其他tab里的中信保费用需要按照货值合计的  8‰（千分之8）  ①这个8‰需要配置在其他参数配置里        ②中信保费用需要放到成本里，计算利润率的时候要考虑进去
  let creditFlagContract = getParantParams('creditFlagContract')
  if (!creditFlagContract) {
    creditFlagContract = props.formData.creditFlag
  }
  if (creditFlagContract && creditFlagContract && sinosureFeeRateRef.value) {
    item.sinosureFee.amount = roundToSixDecimals(
      Number(
        Number(item.totalSaleAmountComputed) *
          Number(rateList[currencyContract]) *
          Number(sinosureFeeRateRef.value)
      ) / Number(rateList[item.sinosureFee.currency])
    )
  } else {
    item.sinosureFee.amount = '0'
  }

  //佣金计算
  item.commissionAmountComputed = item.commissionAmountFormat
  if (item.totalSaleAmountComputed && item.commissionType && item.commissionRate) {
    let commissionTypeLabel = getDictLabel(DICT_TYPE.COMMISSION_TYPE, item.commissionType)
    if (commissionTypeLabel == '佣金比例') {
      let fixedCommissionRate = Number(item.commissionRate) * 0.01
      item.commissionAmountComputed = roundToSixDecimals(
        Number(item.totalSaleAmountComputed) * Number(fixedCommissionRate)
      )
      item.commissionAmountFormat = formatterPrice(item.commissionAmountComputed, moneyPrecision)
    }
  }
  item.commissionAmount.amount = item.commissionAmountComputed
  //退税金额计算
  //当退税率为0时退税金额为0 否则 销售总金额 * 汇率/(1+退税率*0.01）* 退税率*0.01
  if (
    item.taxRefundRate &&
    Number(item.taxRefundRate) > 0 &&
    item.purchaseWithTaxPrice?.amount &&
    item.quantity &&
    props.contractType == 1
  ) {
    const purchaseCurrencyRate = rateList[item.purchaseWithTaxPrice.currency]
    item.taxRefundPrice.amount = roundToSixDecimals(
      ((Number(item.purchaseWithTaxPrice?.amount) * purchaseCurrencyRate * item.quantity) /
        (Number(item.taxRefundRate) * 0.01 + 1)) *
        (Number(item.taxRefundRate) * 0.01)
    )
  } else {
    item.taxRefundPrice.amount = '0'
  }
  if (shouldUpdateAggregate) {
    setComputedTableData()
  }
}

const refershTableItemAll = () => {
  let currencyContract = getParantParams('currencyContract')
  if (!currencyContract) {
    currencyContract = props.formData.currency
  }
  tableList.value.forEach((item) => {
    let totalGoodsValueContract = getParantParams('totalGoodsValueContract')
    if (!totalGoodsValueContract) {
      totalGoodsValueContract = props.formData.totalGoodsValue?.amount
    }

    let estimatedTotalFreightContract = getParantParams('estimatedTotalFreightContract')
    if (!estimatedTotalFreightContract) {
      estimatedTotalFreightContract = props.formData.estimatedTotalFreight?.amount
    }

    //拖柜费 -> 当总体积为0时拖柜费为0，否则 (拖柜费/总体积*(外箱体积*箱数)/**保留两位小数**/) /**保留八位小数**/
    //单拖柜费：trailerFeeContract  totalVolumeContract  明细体积：item.volume
    let trailerFeeContract = getParantParams('trailerFeeContract')
    if (!trailerFeeContract && trailerFeeContract != 0) {
      trailerFeeContract = props.formData.trailerFee?.amount
    }
    let totalVolumeContract = getParantParams('totalVolumeContract')
    if (!totalVolumeContract) {
      totalVolumeContract = props.formData.totalVolume
    }

    if (
      item.volume &&
      trailerFeeContract &&
      totalVolumeContract &&
      Number(totalVolumeContract) > 0
    ) {
      let tParam1 = roundToSixDecimals(Number(trailerFeeContract) / Number(totalVolumeContract))
      let tParam2 = roundToSixDecimals(Number(item.volume))
      item.trailerFee.amount = roundToSixDecimals(Number(tParam1) * Number(tParam2))
    } else {
      item.trailerFee.amount = '0'
    }

    //保险费计算
    //保险费用：当货值合计为0时保险费用为0  否则 (保险费用RMB/货值合计*总额) 		/**保留八位小数**/
    //货值合计：totalGoodsValueContract   合同保险费：insuranceFeeContract  总额：item.totalSaleAmountComputed
    let insuranceFeeContract = getParantParams('insuranceFeeContract')
    if (!insuranceFeeContract && insuranceFeeContract != 0) {
      insuranceFeeContract = props.formData.insuranceFee?.amount
    }
    if (
      totalGoodsValueContract &&
      Number(totalGoodsValueContract) > 0 &&
      insuranceFeeContract &&
      insuranceFeeContract > 0 &&
      item.totalSaleAmountComputed
    ) {
      item.insuranceFee.amount = roundToSixDecimals(
        (Number(insuranceFeeContract) / Number(totalGoodsValueContract)) *
          Number(item.totalSaleAmountComputed)
      )
    } else {
      item.insuranceFee.amount = '0'
    }

    //预估总费用计算
    //[销售单明细]预估总费用 -> 当总体积为0时预估总费用为0，否则: (预估总费用/总体积*(外箱体积*箱数)/**保留两位小数**/)/**保留八位小数**/
    //预估总费用：estimatedTotalFreightContract  总体积：totalVolumeContract 体积：item.volume
    if (
      totalVolumeContract &&
      estimatedTotalFreightContract &&
      Number(totalVolumeContract) > 0 &&
      item.volume &&
      Number(getOuterboxVal(item, 'vol')) &&
      item.boxCount
    ) {
      // let param1 = roundToSixDecimals(
      //   Number(estimatedTotalFreightContract) / Number(totalVolumeContract)
      // )
      // let param2 = roundToSixDecimals(Number(item.outerboxVolume) * Number(item.boxCount))

      item.forecastTotalCost.amount = roundToSixDecimals(
        (Number(estimatedTotalFreightContract) *
          Number(getOuterboxVal(item, 'vol')) *
          Number(item.boxCount)) /
          Number(totalVolumeContract)
      )
    } else {
      item.forecastTotalCost.amount = '0'
    }

    //毛利润=收入-成本
    //其中收入=货值合计*汇率+加项金额+退税金额
    //其中成本=采购合计+包材+配件+总运费+验货费+平台费+减项金额+保险费+中信保费（其中是佣金否扣减总金额为是，需要将佣金计算到成本）
    //采购合计=采购总金额*对人民币汇率，需要看采购币种
    let rate = 0
    if (currencyContract) {
      rate = rateList[currencyContract]
    }
    //收入
    let addAmount = 0
    if (item.totalSaleAmountFormat) {
      addAmount = Number(item.totalSaleAmountFormat) * Number(rate)
    }
    //退税金额
    if (item.taxRefundPrice.amount) {
      addAmount = Number(addAmount) + Number(item.taxRefundPrice.amount)
    }

    //成本
    let subAmount = 0
    //采购合计
    if (item.purchaseWithTaxPrice?.amount) {
      let ratePurchase = rateList[item.purchaseWithTaxPrice?.currency]
      subAmount =
        Number(item.purchaseWithTaxPrice?.amount) *
        Number(ratePurchase) *
        Number(item.needPurQuantity)
    }
    //库存合计
    if (item.stockLockTotalPrice?.amount) {
      subAmount = Number(subAmount) + Number(item.stockLockTotalPrice?.amount)
    }
    //包材
    if (item.purchasePackagingPrice?.amount) {
      subAmount =
        Number(subAmount) + Number(item.purchasePackagingPrice?.amount) * Number(item.quantity)
    }
    //配件
    // if (item.purchaseWithTaxPrice?.amount && item.skuType) {
    //   if (getDictLabel(DICT_TYPE.SKU_TYPE, item.skuType) == '配件') {
    //     subAmount =
    //       Number(subAmount) + Number(item.purchaseWithTaxPrice?.amount) * Number(item.quantity)
    //   }
    // }
    //总运费
    if (item.forecastTotalCost?.amount) {
      subAmount = Number(subAmount) + Number(item.forecastTotalCost.amount)
    }
    //验货费
    if (item.inspectionFee?.amount) {
      subAmount = Number(subAmount) + Number(item.inspectionFee.amount)
    }
    //平台费
    if (item.platformFee?.amount) {
      subAmount =
        Number(subAmount) + Number(item.platformFee?.amount) * rateList[item.platformFee.currency]
    }
    //保险费
    if (item.insuranceFee?.amount) {
      subAmount = Number(subAmount) + Number(item.insuranceFee?.amount)
    }
    //中信保费用
    if (item.sinosureFee?.amount) {
      subAmount =
        Number(subAmount) + Number(item.sinosureFee?.amount) * rateList[item.sinosureFee.currency]
    }
    //佣金否扣减总金额为是，需要将佣金计算到成本
    // if (item.commissionSubTotal == getDictValue(DICT_TYPE.IS_INT, '是')) {

    // }
    subAmount =
      Number(subAmount) +
      Number(item.commissionAmount?.amount) * rateList[item.commissionAmount.currency]
    let orderGrossProfitSum = addAmount - subAmount
    if (orderGrossProfitSum || orderGrossProfitSum == 0) {
      item.orderGrossProfit.amount = Number(orderGrossProfitSum).toFixed(3)
    }
    //毛利率=毛利润/收入
    if (orderGrossProfitSum == 0) {
      item.orderGrossProfitRateFormat = '0'
      item.orderGrossProfitRate = '0'
    } else if (orderGrossProfitSum && addAmount) {
      let orderGrossProfitRate = Number(orderGrossProfitSum) / Number(addAmount)
      item.orderGrossProfitRateFormat = Number(orderGrossProfitRate * 100).toFixed(2)
      item.orderGrossProfitRate = Number(orderGrossProfitRate).toFixed(6)
    }
  })
}

const roundToSixDecimals = (num) => {
  return (Math.round(num * Math.pow(10, 6)) / Math.pow(10, 6)).toFixed(6)
}

//修改佣金类型
const commissionTypeChange = (row) => {
  let valueLabel = getDictLabel(DICT_TYPE.COMMISSION_TYPE, row.commissionType)
  row.commissionRate = '0'
  row.commissionAmountFormat = '0'
  if (valueLabel == '佣金比例') {
    row.commissionRateDiasbled = false
    row.commissionAmountFormatDiasbled = true
  } else if (valueLabel == '佣金金额') {
    row.commissionRateDiasbled = true
    row.commissionAmountFormatDiasbled = false
  } else {
    row.commissionRateDiasbled = true
    row.commissionAmountFormatDiasbled = true
  }
  refershTableRow(row)
}

const tableListExtend = computed(() => {
  let tableData = cloneDeep(TableRef.value.tableData)
  let result = tableData.map((item: any, index) => {
    return {
      ...item
    }
  })
  return result
})

const checkData = async () => {
  if (tableList.value.length == 0) {
    message.warning(`请选择产品`)
    return false
  } else {
    let valid = await TableRef.value.validate()

    if (valid) {
      let tempList = tableList.value.filter((item) => !item.quantity)
      if (tempList.length > 0) {
        message.warning(`存在产品数量为0的明细信息,请核对`)
        return false
      }
      let currencyContract = getParantParams('currencyContract')
      if (!currencyContract) {
        currencyContract = props.formData.currency
      }
      let tableData = cloneDeep(TableRef.value.tableData)
      let codeList = [],
        codeTable = []

      let arr = tableData.map((item: any, index) => {
        if (!codeList.includes(item.skuCode)) {
          codeList.push(item.skuCode)
          let obj = {
            code: item.code,
            name: item.name,
            availableQuantity: item.availableQuantity,
            currentLockQuantity: item.currentLockQuantity
          }
          codeTable.push(obj)
        } else {
          let index = codeList.indexOf(item.skuCode)
          codeTable[index].currentLockQuantity += item.currentLockQuantity
        }

        return {
          ...item,
          hsMeasureUnit: item.hsdata?.unit || '',
          totalSaleAmountUsd: {
            amount:
              (Number(item.totalSaleAmount.amount) * rateList[item.totalSaleAmount.currency]) /
              rateList['USD'],
            currency: 'USD'
          }
        }
      })
      if (props.mode !== 'change') {
        for (let index = 0; index < codeTable.length; index++) {
          const item: any = codeTable[index]
          if (item.currentLockQuantity > item.availableQuantity) {
            message.warning(
              `${item.name}的锁定库存数${item.currentLockQuantity}不能大于可用库存数${item.availableQuantity},请重新分配库存`
            )
            return false
          }
        }
      }

      return arr
    } else {
      message.warning('产品提交信息有误')
      return false
    }
  }
}
const resetTable = () => {
  tableList.value = []
  setComputedTableData()
}
const setScorllBar = () => {
  TableRef.value.setScorllBar()
}

const setVenderDeliveryDate = (val) => {
  if (val) {
    tableList.value.forEach((item: any) => {
      item.venderDeliveryDate = new Date(val).getTime() - 1000 * 60 * 60 * 24 * 7
    })
  }
}
defineExpose({
  selectedList,
  resetTable,
  tableList,
  tableListExtend,
  checkData,
  refershTableRow,
  setParentParams,
  computedTableData,
  setScorllBar,
  setVenderDeliveryDate
})

onMounted(async () => {
  const config = await ConfigApi.getConfigSimplelist({})
  if (config?.length) {
    let platformFeeRateConfig = config.filter((item) => item.category == 'platform_fee_rate')
    if (platformFeeRateConfig?.length) {
      platformFeeRateRef.value = platformFeeRateConfig[0].value
    }
    let sinosureFeeRateConfig = config.filter((item) => item.category == 'sinosure_fee_rate')
    if (sinosureFeeRateConfig?.length) {
      sinosureFeeRateRef.value = sinosureFeeRateConfig[0].value
    }
    let twentyFootCabinetFeeConfig = config.filter(
      (item) => item.category == 'twenty_foot_cabinet_fee'
    )
    if (twentyFootCabinetFeeConfig?.length) {
      twentyFootCabinetFeeConfigValue.value = twentyFootCabinetFeeConfig[0].value
    }
    let fortyFootCabinetFeeConfig = config.filter(
      (item) => item.category == 'forty_foot_cabinet_fee'
    )
    if (fortyFootCabinetFeeConfig?.length) {
      fortyFootCabinetFeeConfigValue.value = fortyFootCabinetFeeConfig[0].value
    }
    let fortyFootContainerFeeConfig = config.filter(
      (item) => item.category == 'forty_foot_container_fee'
    )
    if (fortyFootContainerFeeConfig?.length) {
      fortyFootContainerFeeConfigValue.value = fortyFootContainerFeeConfig[0].value
    }
    let bulkHandlingFeeConfig = config.filter((item) => item.category == 'bulk_handling_fee')
    if (bulkHandlingFeeConfig?.length) {
      bulkHandlingFeeConfigValue.value = bulkHandlingFeeConfig[0].value
    }
    let bulkHandlingStartFeeConfig = config.filter(
      (item) => item.category == 'bulk_handling_start_fee'
    )
    if (bulkHandlingStartFeeConfig?.length) {
      bulkHandlingStartFeeConfigValue.value = bulkHandlingStartFeeConfig[0].value
    }
  }
})
const handleSummary = (param) => {
  const { columns, data } = param
  const sums: (string | VNode)[] = []
  
  // Guard clause: if data is empty, return empty sums
  if (!data || data.length === 0) {
    return columns.map(() => '')
  }
  
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = ''
      return
    }
    const sumFileds = [
      'quantity',
      'totalSaleAmountFormat',
      'purchaseTotalAmount',
      'volume',
      'taxRefundPrice',
      'commissionAmount',
      'purchaseQuantity',
      'currentLockQuantity',
      'stockLockTotalPrice',
      'boxCount'
    ]
    let values = []
    if (
      sumFileds.includes(column.property) &&
      (isNumber(data[0][column.property]) || validNumber(data[0][column.property]))
    ) {
      values = data.map((item) => Number(item[column.property]))
      let numVal = `${values.reduce((prev, curr) => {
        const value = Number(curr)
        if (!Number.isNaN(value)) {
          return prev + curr
        } else {
          return prev
        }
      }, 0)}`
      if (column.property === 'volume') {
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
    } else {
      sums[index] = ''
    }
  })

  return sums
}

const convertWeight = (val) => {
  if (val && val?.unit === 'kg') {
    return val
  } else if (val && val?.unit === 'g') {
    return {
      weight: val.weight / 1000,
      unit: 'kg'
    }
  } else {
    return val
  }
}

const hasLoadedData = ref(false)
const isUpdating = ref(false)
watch(
  () => props.formData.children,
  async (newChildren) => {
    console.log('[PurchaseProducts] 数据watch触发, 数据长度:', newChildren?.length, 'isUpdating:', isUpdating.value, 'hasLoadedData:', hasLoadedData.value)
    if (isUpdating.value) return
    if (isValidArray(newChildren)) {
      isUpdating.value = true
      try {
        let res = await getQuantity(newChildren)
        let tableData = cloneDeep(newChildren)
        tableList.value = tableData.map((item, index) => {
      let valueLabel = getDictLabel(DICT_TYPE.COMMISSION_TYPE, item.commissionType)
      if (valueLabel == '佣金比例') {
        item.commissionRateDiasbled = false
        item.commissionAmountFormatDiasbled = true
      } else if (valueLabel == '佣金金额') {
        item.commissionRateDiasbled = true
        item.commissionAmountFormatDiasbled = false
      } else {
        item.commissionRateDiasbled = true
        item.commissionAmountFormatDiasbled = true
      }

      return {
        ...item,
        availableQuantity: res[item.skuCode],
        cskuCodeDisabled: item.cskuCode ? true : false,
        unitPriceAmount: item.unitPrice?.amount,
        totalSaleAmountFormat: item.totalSaleAmount?.amount
          ? formatterPrice(Number(item.totalSaleAmount?.amount), moneyPrecision)
          : '0',
        commissionAmountFormat: item.commissionAmount?.amount
          ? formatterPrice(Number(item.commissionAmount?.amount), moneyPrecision)
          : '0',
        commissionAmountComputed: item.commissionAmount?.amount
          ? Number(item.commissionAmount?.amount)
          : '0',
        trailerFee: item.trailerFee ? item.trailerFee : { amount: '0', currency: 'RMB' },
        orderGrossProfit: item.orderGrossProfit
          ? item.orderGrossProfit
          : { amount: '0', currency: 'RMB' },
        taxRefundPrice: item.taxRefundPrice
          ? item.taxRefundPrice
          : { amount: '0', currency: 'RMB' },
        purchasePackagingPrice: item.purchasePackagingPrice
          ? item.purchasePackagingPrice
          : { amount: '0', currency: 'RMB' },
        inspectionFee: item.inspectionFee ? item.inspectionFee : { amount: '0', currency: 'RMB' },
        insuranceFee: item.insuranceFee ? item.insuranceFee : { amount: '0', currency: 'RMB' },
        platformFee: item.platformFee ? item.platformFee : { amount: '0', currency: 'RMB' },
        forecastTotalCost: item.forecastTotalCost
          ? item.forecastTotalCost
          : { amount: '0', currency: 'RMB' },
        totalSaleAmount: item.totalSaleAmount
          ? item.totalSaleAmount
          : { amount: '0', currency: 'RMB' },
        commissionAmount: item.commissionAmount
          ? item.commissionAmount
          : { amount: '0', currency: 'RMB' },
        unitPrice: item.unitPrice ? item.unitPrice : { amount: '0', currency: 'RMB' },
        sinosureFee: item.sinosureFee ? item.sinosureFee : { amount: '0', currency: 'USD' },
        totalSaleAmountComputed: item.totalSaleAmount?.amount,
        orderGrossProfitRateFormat: item.orderGrossProfitRate
          ? formatterPrice(Number(item.orderGrossProfitRate * 100), moneyPrecision)
          : '0',
        purchaseUserId: item.purchaseUser ? item.purchaseUser.userId : '',
        purchaseTotalAmount: { amount: 0, currency: '' },
        commissionSubTotal: item.commissionSubTotal || 0,
        currentLockQuantity: props.mode == 'copy' ? 0 : item.currentLockQuantity,
        stockLockSaveReqVOList: props.mode == 'copy' ? [] : item.stockLockSaveReqVOList,
        outerboxGrossweight: convertWeight(item.outerboxGrossweight),
        outerboxNetweight: convertWeight(item.outerboxNetweight)
      }
    })
    tableList.value.forEach((item) => {
      getRealVal(item)
      refershTableItemPage(item, false)
    })
    // 所有项目处理完后，只调用一次聚合计算
    setComputedTableData()
    // 数据加载完成后通知父组件
    await nextTick()
    console.log('[PurchaseProducts] 从数据watch触发loaded事件')
    hasLoadedData.value = true
    emit('loaded')
      } finally {
        isUpdating.value = false
      }
    } else {
      tableList.value = []
      // 只在第一次已有数据的情况下才emit loaded，避免初始化时过早触发
      if (hasLoadedData.value) {
        console.log('[PurchaseProducts] 数据变空，从数据watch触发loaded事件')
        emit('loaded')
      }
    }
    if (props.mode === 'change') {
      tableColumns = tableColumns.filter((item) => item.field != 'availableQuantity')
    }
  },
  { immediate: true }
)

// 监听父组件loading状态变化，当父组件数据加载完成且本组件数据为空时触发loaded
watch(
  () => props.loading,
  async (newVal, oldVal) => {
    console.log('[PurchaseProducts] loading变化:', oldVal, '->', newVal, 'hasLoadedData:', hasLoadedData.value, 'isUpdating:', isUpdating.value)
    // 只有在数据不在更新中且没有加载过数据时才触发loaded
    // 如果数据正在更新中，让数据watch来触发loaded
    if (oldVal === true && newVal === false && !hasLoadedData.value && !isUpdating.value) {
      await nextTick()
      // 再次检查，因为数据watch可能在nextTick期间开始执行
      if (!hasLoadedData.value && !isUpdating.value) {
        console.log('[PurchaseProducts] 从loading watch触发loaded事件')
        hasLoadedData.value = true
        emit('loaded')
      }
    }
  }
)
onMounted(async () => {
  console.log('[PurchaseProducts] onMounted, loading:', props.loading, 'hasLoadedData:', hasLoadedData.value, 'isUpdating:', isUpdating.value)
  // 新增模式下数组为空，watchEffect不会触发loaded事件，需要在mounted时触发
  // 只有在父组件数据已加载完成时才触发
  await nextTick()
  // 如果数据正在更新中，让数据watch来触发loaded
  if (!props.loading && !hasLoadedData.value && !isUpdating.value) {
    console.log('[PurchaseProducts] 从onMounted触发loaded事件')
    hasLoadedData.value = true
    emit('loaded')
  }
})

const capitalizeFirstLetter = (str) => {
  return 'real' + str.charAt(0).toUpperCase() + str.slice(1)
}
const getRealVal = (item) => {
  let realFieldList = [
    'boxCount',
    'lockQuantity',
    'packagingPrice',
    'prchaseCurrency',
    'purchaseQuantity',
    'purchaseWithTaxPrice',
    'shippingPrice',
    'specificationList',
    'splitBoxFlag',
    'taxRefundPrice',
    'taxRefundRate',
    'venderCode',
    'venderId',
    'venderName'
  ]
  realFieldList.forEach((f) => {
    if (isAmount(item[f])) {
      if (item[capitalizeFirstLetter(f)]?.amount > 0 && item[capitalizeFirstLetter(f)]?.currency) {
        item[f] = item[capitalizeFirstLetter(f)]
      } else {
        item[f] = item[f]
      }
    } else {
      item[f] = item[capitalizeFirstLetter(f)] || item[f]
    }
  })
  //库存单独处理
  item.currentLockQuantity = item.realLockQuantity || item.currentLockQuantity || 0
}
</script>
<style lang="scss" scoped>
:deep(.el-badge__content) {
  position: relative;
  left: -55px;
  width: 30px;
}

:deep(.el-badge__content--primary) {
  color: #7cc440;
  background-color: #000;
}

.total-header {
  width: 100%;
}
</style>
