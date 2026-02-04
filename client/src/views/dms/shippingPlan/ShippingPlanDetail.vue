<template>
  <eplus-detail
    ref="eplusDetailRef"
    scrollFlag
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    @update="handleUpdate"
  >
    <template #pageTop>
      <eplus-description
        title="基础信息"
        :data="pageInfo"
        :items="basicInfo"
      >
        <template #inputUser>
          {{ pageInfo?.inputUser?.nickname }}
        </template>
      </eplus-description>
      <el-tabs v-model="activeName">
        <el-tab-pane
          label="尺柜信息"
          name="1"
        >
          <eplus-description
            title=""
            :data="pageInfo"
            :items="cabinetSchema"
          />
        </el-tab-pane>
        <el-tab-pane
          label="出货信息"
          name="2"
        >
          <eplus-description
            title=""
            :data="pageInfo"
            :items="shipmentSchema"
          />
        </el-tab-pane>
        <el-tab-pane
          label="合计信息"
          name="3"
        >
          <eplus-description
            title=""
            :data="pageInfo"
            :items="totalSchema"
          />
        </el-tab-pane>
      </el-tabs>
    </template>
    <template #pageBottomTabs>
      <el-tabs v-model="tabActive">
        <el-tab-pane
          label="销售合同明细"
          name="1"
        />
        <el-tab-pane
          label="加减项"
          name="2"
        />
        <el-tab-pane
          label="关联单据"
          name="3"
        />
      </el-tabs>
    </template>
    <template #pageBottom>
      <el-scrollbar always>
        <div v-show="tabActive === '1'">
          <Table
            :columns="tableColumns"
            headerAlign="center"
            align="center"
            :data="pageInfo?.children"
            showSummary
            sumText="合计"
            :summaryMethod="handleSummary"
            :height="tableMaxHeight"
          />
        </div>
        <div v-show="tabActive === '2'">
          <Table
            v-if="isValidArray(pageInfo?.addSubItemList)"
            :columns="subColumns"
            headerAlign="center"
            align="center"
            :data="pageInfo?.addSubItemList"
          />
          <span v-else>暂无数据</span>
        </div>
        <div v-show="tabActive === '3'">
          <RelateTable
            v-if="!loading"
            :data="pageInfo"
          />
        </div>
      </el-scrollbar>
    </template>
  </eplus-detail>

  <AppendDia
    ref="AppendDiaRef"
    @sure="handleUpdate"
  />
</template>
<script setup lang="tsx">
import * as ShippingPlanApi from '@/api/dms/shippingPlan/index'
import { EplusDesc, EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import {
  columnWidth,
  formatDateColumn,
  formatDictColumn,
  formatMoneyColumn,
  formatNumColumn,
  formatWeightColumn
} from '@/utils/table'
import { volPrecision, VolumeUnit } from '@/utils/config'
import RelateTable from '@/components/RelateTable/src/RelateTable.vue'
import { isAmount, isNumber, isValidArray, isWeight } from '@/utils/is'
import { formatNum, getCurrency, getTotalAmount } from '@/utils/index'
import { checkPermi } from '@/utils/permission'
import CheckStatusLog from '@/views/dms/shippingPlan/components/CheckStatusLog.vue'
import AppendDia from './components/AppendDia.vue'
import { getOuterbox } from '@/utils/outboxSpec'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import download from '@/utils/download'

const message = useMessage()
const pageInfo = ref({})

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'ShippingPlanDetail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const { updateDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
    })
  : { updateDialogActions: () => {} }

const loading = ref(true)
const { query } = useRoute()
const activeName = ref('1')
const tabActive = ref('1')
const getPageInfo = async () => {
  loading.value = true
  try {
    pageInfo.value = await ShippingPlanApi.getShipmentPlanInfo({ id: props.id })
    pageInfo.value?.children.forEach((cItem: any) => {
      //采购合计
      cItem.purchaseTotalAmount = { amount: 0, currency: '' }
      cItem.purchaseTotalAmount.amount =
        Number(cItem.purchaseWithTaxPrice.amount) * Number(cItem.purchaseQuantity)
      cItem.purchaseTotalAmount.currency = cItem.purchaseWithTaxPrice.currency
    })
    if (pageInfo.value?.children?.length) {
      pageInfo.value.purchaseTotalAmount = getTotalAmount(
        pageInfo.value?.children,
        'purchaseTotalAmount'
      )
    }
  } catch {
    pageInfo.value = {}
  } finally {
    loading.value = false
  }
}

/**
 * 生成明细信息
 * @param r
 */
const basicInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'code',
    label: '计划单号'
  },
  {
    field: 'foreignTradeCompanyName',
    label: '归属公司'
  },
  {
    field: 'status',
    label: '单据状态',
    dictType: DICT_TYPE.SHIPPING_PLAN_STATUS
  },
  {
    field: 'custDeliveryDate',
    label: '客户交期',
    type: 'date'
  },
  {
    field: 'transportType',
    label: '运输方式',
    dictType: DICT_TYPE.TRANSPORT_TYPE
  },
  {
    field: 'departureCountryName',
    label: '出运国'
  },
  {
    field: 'departurePortName',
    label: '出运口岸'
  },
  {
    field: 'tradeCountryName',
    label: '贸易国别'
  },
  {
    field: 'destinationPortName',
    label: '目的口岸'
  },
  {
    field: 'estDeliveryDate',
    label: '预计交货日期',
    type: 'date'
  },
  {
    field: 'settlementTermType',
    label: '价格条款'
  },
  {
    field: 'managerName',
    label: '业务员'
  },
  {
    field: 'saleContractCode',
    label: '销售合同号'
  },
  {
    field: 'custName',
    label: '客户名称'
  },
  {
    field: 'custPo',
    label: '客户PO'
  },
  {
    slotName: 'inputUser',
    field: 'inputUser',
    label: '录入人'
  },
  {
    field: 'inputDate',
    label: '录入日期',
    type: 'date'
  },
  {
    field: 'remark',
    label: '备注',
    xl: 8,
    lg: 12
  },
  {
    field: 'annex',
    label: '附件',
    span: 24
  }
]

const cabinetSchema: EplusDescriptionItemSchema[] = [
  {
    field: 'twentyFootCabinetNum',
    label: '20尺柜',
    type: 'num'
  },
  {
    field: 'fortyFootCabinetNum',
    label: '40尺柜',
    type: 'num'
  },
  {
    field: 'fortyFootContainerNum',
    label: '40尺高柜',
    type: 'num'
  },
  {
    field: 'bulkHandlingVolume',
    label: `散货`,
    type: 'volume'
  }
]
const shipmentSchema = [
  {
    field: 'receivePerson',
    label: '收货人'
  },
  {
    field: 'notifyPerson',
    label: '通知人'
  },
  {
    field: 'frontShippingMark',
    label: '正面唛头'
  }
]

const totalSchema: EplusDescriptionItemSchema[] = [
  {
    field: 'totalGoodsValue',
    label: '货值合计',
    type: 'money'
  },
  {
    field: 'purchaseTotalAmount',
    label: '采购合计',
    type: 'money'
  },
  {
    field: 'totalQuantity',
    label: '数量合计',
    type: 'num'
  },
  {
    field: 'totalBoxes',
    label: '箱数合计',
    type: 'num'
  },
  {
    field: 'totalGrossweight',
    label: '毛重合计',
    type: 'weight'
  },
  {
    field: 'totalWeight',
    label: '净重合计',
    type: 'weight'
  },
  {
    field: 'totalVolume',
    label: '体积合计',
    type: 'volume'
  }
]

const tableColumns = [
  {
    field: 'companyName',
    label: '下单主体',
    width: columnWidth.l
  },
  {
    field: 'shippedAddress',
    label: '发货地点',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.SHIPPED_ADDRESS)
  },
  {
    field: 'custPo',
    label: '客户PO号',
    width: columnWidth.l
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
  //   minWidth: '250px',
  //   slots: {
  //     default: (data) => {
  //       const { row } = data
  //       if (row.skuDeletedFlag) {
  //         return (
  //           <div>
  //             <span>{row.skuCode}</span>
  //             <el-tag
  //               type="info"
  //               effect="dark"
  //               size="small"
  //               class="ml10px"
  //             >
  //               被修改
  //             </el-tag>
  //           </div>
  //         )
  //       } else {
  //         return <span>{row.skuCode}</span>
  //       }
  //     }
  //   }
  // },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: columnWidth.l
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
    width: columnWidth.l
  },
  {
    field: 'barcode',
    label: '条形码',
    width: columnWidth.l
  },
  {
    field: 'name',
    label: '中文品名',
    width: columnWidth.l
  },
  {
    field: 'shippingQuantity',
    label: '出运数量',
    width: columnWidth.l,
    formatter: formatNumColumn()
  },
  {
    field: 'shippingAmount',
    label: '出运总金额',
    width: columnWidth.l,
    formatter: (row) => {
      return `${getCurrency(row.saleUnitPrice.currency)} ${formatNum(row.shippingQuantity * row.saleUnitPrice.amount)}`
    }
  },
  {
    field: 'saleQuantity',
    label: '数量',
    width: columnWidth.l,
    formatter: formatNumColumn()
  },
  {
    field: 'saleUnitPrice',
    label: '销售单价',
    width: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'saleAmount',
    label: '销售金额',
    width: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'deliveryDate',
    label: '交货日期',
    width: columnWidth.l,
    formatter: formatDateColumn('YYYY-MM-DD')
  },
  {
    field: 'freeFlag',
    label: '是否赠品',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.CONFIRM_TYPE)
  },
  // {
  //   field: 'freeQuantity',
  //   label: '赠品数量',
  //   width: columnWidth.l,
  //   formatter: formatNumColumn()
  // },
  {
    field: 'commodityInspectionFlag',
    label: '是否商检',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.IS_INT)
  },
  {
    field: 'hsCode',
    label: 'HS编码',
    width: columnWidth.l
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
    formatter: formatMoneyColumn()
  },
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
    formatter: (row) => {
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
    formatter: (row) => {
      return <CheckStatusLog row={row} />
    }
  },
  {
    field: 'purchaseQuantity',
    label: '总采购数量',
    width: columnWidth.l,
    formatter: formatNumColumn()
  },
  {
    field: 'purchaseUserName',
    label: '采购员',
    width: columnWidth.l
  },
  {
    field: 'purchaseWithTaxPrice',
    label: '采购含税单价',
    width: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'purchaseTotalAmount',
    label: '采购总金额',
    width: columnWidth.l,
    formatter: formatMoneyColumn()
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
    formatter: formatNumColumn()
  },
  {
    field: 'qtyPerOuterbox',
    label: '外箱装量',
    width: columnWidth.l,
    formatter: formatNumColumn()
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    width: '100',
    formatter: (row) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'boxCount',
    label: '箱数',
    width: '100',
    formatter: (row) => {
      return <span>{formatNum(row.boxCount * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    width: '150',
    formatter: (row) => {
      return <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>
    }
  },
  {
    field: 'outerboxVolume',
    label: '外箱体积',
    width: '150',
    formatter: (row) => {
      return <div>{getOuterbox(row, 'vol')}</div>
    }
  },
  {
    field: 'totalVolume',
    label: `总体积${VolumeUnit}`,
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return formatNum(row.totalVolume / 1000000)
      }
    }
  },
  {
    field: 'outerboxGrossweight',
    label: '外箱毛重',
    width: columnWidth.l,
    formatter: (row) => {
      return <div>{getOuterbox(row, 'grossweight')}</div>
    }
  },
  {
    field: 'totalGrossweight',
    label: '总毛重',
    width: columnWidth.l,
    formatter: formatWeightColumn()
  },

  {
    field: 'outerboxNetweight',
    label: '外箱净重',
    width: columnWidth.l,
    formatter: (row) => {
      return <div>{getOuterbox(row, 'netweight')}</div>
    }
  },
  {
    field: 'totalNetweight',
    label: '总净重',
    width: columnWidth.l,
    formatter: formatWeightColumn()
  },
  {
    field: 'description',
    label: '中文说明',
    width: columnWidth.l,
    showOverflowTooltip: false,
    formatter: (row) => {
      return <EplusDesc info={row.description} />
    }
  },
  {
    field: 'descriptionEng',
    label: '英文说明',
    width: columnWidth.l,
    showOverflowTooltip: false,
    formatter: (row) => {
      return <EplusDesc info={row.descriptionEng} />
    }
  },
  // {
  //   field: 'stockCode',
  //   label: '仓库编号',
  //   width: columnWidth.l
  // },
  // {
  //   field: 'stockName',
  //   label: '仓库名称',
  //   width: columnWidth.l
  // },
  {
    field: 'remark',
    label: '备注',
    width: columnWidth.l
  },
  {
    field: 'commissionAmount',
    label: '佣金金额',
    width: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'orderGrossProfitRate',
    label: '毛利率%',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return row.orderGrossProfitRate ? Number(row.orderGrossProfitRate * 100).toFixed(2) : '-'
      }
    }
  },
  {
    field: 'sales',
    label: '业务员',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return row?.sales?.nickname || '-'
      }
    }
  }
]
const subColumns = [
  {
    field: 'contractCode',
    label: '销售合同',
    width: '300'
  },
  {
    field: 'calculationType',
    label: '加/减项',
    formatter: formatDictColumn(DICT_TYPE.CALCULATION_TYPE)
  },
  {
    field: 'feeName',
    label: '费用名称'
  },
  {
    field: 'amount',
    label: '金额',
    formatter: formatMoneyColumn()
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

const handleUpdate = async () => {
  await getPageInfo()
  setPageBtns()
}

const handleTransformShipment = async () => {
  let params = { ids: pageInfo.value?.children.map((item) => item.id).join(',') }
  await message.confirm('确认转出运明细吗?')
  let res = await ShippingPlanApi.transformShipment(params)
  message.notifyPushSuccess('转出运明细成功', res, 'shipment')
  handleUpdate()
}

const AppendDiaRef = ref()
const handleAppend = () => {
  AppendDiaRef.value.open(pageInfo.value)
}

const setPageBtns = () => {
  if (pageInfo.value?.status === 1 && checkPermi(['dms:shipment-plan:update'])) {
    updateDialogActions(
      <el-button
        onClick={() => {
          goEdit('')
        }}
      >
        编辑
      </el-button>
    )
  }
  if (pageInfo.value?.status === 1 && checkPermi(['dms:shipment:transform'])) {
    updateDialogActions(
      <el-button
        onClick={() => {
          handleTransformShipment()
        }}
      >
        转出运明细
      </el-button>,
      <el-button
        onClick={() => {
          handleAppend()
        }}
      >
        追加
      </el-button>
    )
  }

  if (pageInfo.value?.confirmFlag !== 0 && checkPermi(['dms:shipment-plan:detail-export'])) {
    updateDialogActions(
      <el-button
        onClick={() => {
          exportDetail(pageInfo.value)
        }}
        key="detailExport"
      >
        明细导出
      </el-button>
    )
  }
}
const exportDetail = async (row) => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    const data = await ShippingPlanApi.exportShipmentPlanDetail({
      id: row.id,
      reportCode: 'dms-shipment-plan-detail',
      exportType: 'detail'
    })
    if (data && data.size) {
      download.excel(data, `明细${row.code}.xlsx`)
    }
  } catch {}
  return
}

const tableMaxHeight = ref(0)
onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('ID不能为空')
    if (!props.id) {
      close()
    }
  }
  if (query.id) {
    showProcessInstanceTaskListFlag.value = false
    outDialogFlag.value = true
  }
  if (props.id) {
    showProcessInstanceTaskListFlag.value = true
    outDialogFlag.value = false
  }
  await getPageInfo()
  setPageBtns()

  setTimeout(() => {
    tableMaxHeight.value = eplusDetailRef.value.bottomHeight - 54
  }, 1000)
})
</script>
<style lang="scss" scoped></style>
