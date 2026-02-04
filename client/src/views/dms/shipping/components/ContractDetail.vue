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
    :showIndexPlus="false"
  />
  <DivideDialog
    ref="divideDialogRef"
    @sure="handleSure"
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
import { moneyInputPrecision, volPrecision, VolumeUnit, weightUnit } from '@/utils/config'
import {
  formatNum,
  formatterPrice,
  formatTime,
  formatWeight,
  getCurrency,
  getTotalAmount
} from '@/utils/index'
import { isAmount, isNumber, isWeight } from '@/utils/is'
import DivideDialog from './DivideDialog.vue'
import { getDictLabel, getDictValue } from '@/utils/dict'
import { getSkuInfo } from '@/api/sms/saleContract/domestic'
import * as HsdataApi from '@/api/system/hsdata'
import CheckStatusLog from '@/views/dms/shippingPlan/components/CheckStatusLog.vue'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import UpdateOuterboxSpec from '@/views/pms/product/main/components/UpdateOuterboxSpec.vue'
import { getOuterbox, getOuterboxVal } from '@/utils/outboxSpec'
import PurchaseContractTooltip from './PurchaseContractTooltip.vue'
import { setSourceId } from '@/utils/auth'
import { checkPermi } from '@/utils/permission.js'
import { useRateStore } from '@/store/modules/rate'
import { columnWidth } from '@/utils/table'
import { EplusMoneyLabel, EplusNumInput } from '@/components/EplusMoney'
import { EplusImgEnlarge } from '@/components/EplusTemplate'

defineOptions({ name: 'ContractDetail' })
const props = defineProps<{
  formData
  changeFlag?: boolean
  maxHeight?: number
}>()
const divideDialogRef = ref()
const message = useMessage()
const tableList: any = ref([])

const handleSure = (val: any) => {
  let divideItemId = val[0]?.id
  let splitIndex = 0
  tableList.value.forEach((item, index) => {
    if (item.id === divideItemId) {
      splitIndex = index
      item.taxRefundUnitPrice = item.taxRefundPrice.amount / item.shippingQuantity
      item.shippingQuantity = item.shippingQuantity - val[1].shippingQuantity
      if (val[1].shippingQuantity >= item.thisPurchaseQuantity) {
        val[1].thisPurchaseQuantity = item.thisPurchaseQuantity
        item.thisPurchaseQuantity = 0
      } else {
        item.thisPurchaseQuantity =
          Number(item.thisPurchaseQuantity) - Number(val[1].shippingQuantity)
        val[1].thisPurchaseQuantity = val[1].shippingQuantity
      }
      if (item.shippingQuantity >= item.declarationQuantity) {
        val[1].declarationQuantity = 0
        item.declarationQuantity = item.declarationQuantity
      } else {
        val[1].declarationQuantity =
          Number(item.declarationQuantity) - Number(item.shippingQuantity)
        item.declarationQuantity = item.shippingQuantity
      }

      if (item.shippingQuantity >= item.realLockQuantity) {
        val[1].realLockQuantity = 0
        item.realLockQuantity = item.realLockQuantity
      } else {
        val[1].realLockQuantity = Number(item.realLockQuantity) - Number(item.shippingQuantity)
        item.realLockQuantity = item.shippingQuantity
      }
    }
  })
  tableList.value.splice(splitIndex + 1, 0, {
    ...val[1],
    declarationName: tableList.value[splitIndex].declarationName,
    customsDeclarationNameEng: tableList.value[splitIndex].customsDeclarationNameEng,
    estPickupTime: tableList.value[splitIndex].estPickupTime || null,
    taxRefundUnitPrice: tableList.value[splitIndex].taxRefundUnitPrice,
    packageTypeName: tableList.value[splitIndex].packageTypeName,
    insuranceFeeStr:
      tableList.value[splitIndex].insuranceFeeStr ||
      tableList.value[splitIndex].insuranceFee?.amount ||
      0,
    declarationUnitPriceStr:
      tableList.value[splitIndex].declarationUnitPriceStr ||
      tableList.value[splitIndex].declarationUnitPrice?.amount ||
      0,
    outerboxGrossweightStr: tableList.value[splitIndex]?.outerboxGrossweight?.weight || 0,
    outerboxNetweightStr: tableList.value[splitIndex]?.outerboxNetweight?.weight || 0,
    declarationQuantityOld: 0,
    settlementQuantity: 0,
    invoicedQuantity: 0
  })
}

const UpdateOuterboxSpecRef = ref()
let tableColumns = reactive([
  {
    field: 'mainPicture',
    label: '图片',
    width: columnWidth.m,
    fixed: 'left',
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
    field: 'index',
    label: '序号',
    width: '60',
    slot: (item: any, row: Recordable, index: number) => (index + 1).toString()
  },

  {
    field: 'billStatus',
    label: '入库地点',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return getDictLabel(DICT_TYPE.SALE_ITEM_BILL_STATUS, row.billStatus)
    }
  },
  {
    field: 'shippedAddress',
    label: '发货地点',
    width: columnWidth.l,
    component: (
      <eplus-dict-select
        class="!w-90%"
        dictType={DICT_TYPE.SHIPPED_ADDRESS}
      />
    )
  },
  {
    field: 'outboundFlag',
    label: '是否出库',
    width: columnWidth.m,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return getDictLabel(DICT_TYPE.IS_INT, row.outboundFlag)
    }
  },
  {
    field: 'saleContractCode',
    label: '外销合同',
    width: columnWidth.l,
    sortable: true,
    slot: (item, row: Recordable, index: number) => {
      if (row.saleContractCode) {
        return (
          <el-button
            type="primary"
            link
            onClick={() => openDetail(row, 2)}
          >
            {row.saleContractCode}
          </el-button>
        )
      } else {
        return <span>-</span>
      }
    }
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    sortable: true,
    width: columnWidth.l,
    component: (
      <el-input
        type="textarea"
        rows={3}
      ></el-input>
    )
  },
  {
    field: 'custPo',
    label: '客户PO号',
    component: <el-input></el-input>,
    width: columnWidth.xxl,
    sortable: true
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: columnWidth.l
  },
  {
    field: 'nameEng',
    label: '英文品名',
    width: columnWidth.xxl,
    component: (
      <el-input
        type="textarea"
        rows={3}
      ></el-input>
    ),
    rules: [{ required: true, message: '请输入英文品名' }]
  },
  {
    field: 'name',
    label: '中文品名',
    width: columnWidth.xxl,
    component: (
      <el-input
        type="textarea"
        rows={3}
      ></el-input>
    ),
    rules: [{ required: true, message: '请输入中文品名' }]
  },
  {
    field: 'barcode',
    label: '条形码',
    width: columnWidth.l
  },
  {
    field: 'shippingQuantity',
    label: '出运数量',
    width: columnWidth.l,
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      if (!props.changeFlag) {
        return <span>{formatNum(row.shippingQuantity)}</span>
      } else {
        return (
          <el-input-number
            v-model={row.shippingQuantity}
            min={1}
            controls={false}
            precision={0}
            class="!w-90%"
            onChange={() => {
              row.boxCountModified = false
            }}
          />
        )
      }
    },
    rules: [{ required: true, message: '请输入出运数量' }]
  },
  {
    field: 'declarationQuantity',
    label: '报关数量',
    width: columnWidth.l,
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <el-input-number
          v-model={row.declarationQuantity}
          min={1}
          controls={false}
          precision={0}
          class="!w-90%"
        />
      )
    },
    rules: [{ required: true, message: '请输入报关数量' }]
  },
  {
    field: 'saleUnitPriceAmount',
    label: '销售单价',
    width: columnWidth.l,
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <EplusNumInput
          v-model={row.saleUnitPriceAmount}
          min={0}
          controls={false}
          precision={moneyInputPrecision}
          class="!w-90%"
          onChange={() => {
            row.saleUnitPrice.amount = row.saleUnitPriceAmount
          }}
        />
      )
    },
    rules: [{ required: true, message: '请输入销售单价' }]
    // formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
    //   return row?.saleUnitPrice?.amount ? <EplusMoneyLabel val={row.saleUnitPrice} /> : '0'
    // }
  },
  {
    field: 'saleAmount',
    label: '销售金额',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row?.saleAmount?.amount ? <EplusMoneyLabel val={row.saleAmount} /> : '0'
    }
  },
  {
    field: 'declarationUnitPriceStr',
    label: '报关单价',
    width: columnWidth.l,
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <EplusNumInput
          v-model={row.declarationUnitPriceStr}
          min={0}
          precision={moneyInputPrecision}
          class="!w-90%"
          onChange={() => {
            row.declarationUnitPrice.amount = row.declarationUnitPriceStr
          }}
        />
      )
    },
    rules: { required: true, message: '请输入报关单价' }
  },
  {
    field: 'declarationAmount',
    label: '报关金额',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row?.declarationAmount?.amount ? <EplusMoneyLabel val={row.declarationAmount} /> : '0'
    }
  },
  // {
  //   field: 'freeFlag',
  //   label: '是否含赠品',
  //   width: columnWidth.l,
  //   formatter: (item, row, index) => {
  //     return getDictLabel(DICT_TYPE.CONFIRM_TYPE, row.freeFlag)
  //   }
  // },
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
    batchEditFlag: true,
    component: (
      <eplus-dict-select
        class="!w-90%"
        dictType={DICT_TYPE.IS_INT}
      />
    ),
    rules: { required: true, message: '请选择' }
  },
  //动态匹配是否必填
  {
    label: '商检负责方',
    field: 'commodityInspectionType',
    width: columnWidth.l,
    slot: (item, row, index) => {
      return (
        <eplus-dict-select
          v-model={row.commodityInspectionType}
          disabled={!row?.commodityInspectionFlag}
          class="!w-90%"
          dictType={DICT_TYPE.COMMODITY_INSPECTION_TYPE}
        />
      )
    },
    rules: {
      required: true,
      message: '请选择',
      validator: (rule, value, callback) => {
        if (value === undefined) {
          callback()
        } else if (value === null) {
          callback(new Error('请选择商检负责方'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'hsCode',
    label: 'HS编码',
    width: columnWidth.xl,
    batchEditFlag: true,
    batchEditCom: <el-input></el-input>,
    slot: (item, row, index) => {
      return (
        <el-autocomplete
          v-model={row.hsCode}
          fetch-suggestions={remoteMethod}
          trigger-on-focus={false}
          class="!w-100%"
          clearable
          placeholder="请输入HS编码"
        />
        // <EplusSearchSelect
        //   v-model={row.hsCode}
        //   api={HsdataApi.getHsdataPage}
        //   params={{ pageSize: 100, pageNo: 1 }}
        //   keyword="code"
        //   label="code"
        //   value="code"
        //   class="!w-100%"
        //   placeholder="请选择"
        //   defaultObj={{
        //     code: row.hsCode || undefined,
        //     unit: getDictLabel(DICT_TYPE.HS_MEASURE_UNIT, row?.hsMeasureUnit) || undefined,
        //     taxRefundRate: row.taxRefundRate || undefined
        //   }}
        //   onChangeEmit={(...e) => hsdataCodeChange(e, row)}
        // />
      )
    },
    rules: {
      required: true,
      message: '请选择HS编码'
    }
  },
  {
    field: 'customsDeclarationNameEng',
    label: '报关英文名',
    width: columnWidth.xl,
    batchEditFlag: true,
    component: (
      <el-input
        type="textarea"
        rows={3}
      ></el-input>
    ),
    rules: [{ required: true, message: '请输入报关英文名' }]
  },
  {
    field: 'declarationName',
    label: '报关中文名',
    width: columnWidth.xl,
    batchEditFlag: true,
    component: (
      <el-input
        type="textarea"
        rows={3}
      ></el-input>
    ),
    rules: [{ required: true, message: '请输入报关中文名' }]
  },
  {
    field: 'hsMeasureUnit',
    label: '海关计量单位',
    width: columnWidth.xl,
    batchEditFlag: true,
    component: <el-input></el-input>,
    rules: [{ required: true, message: '请选择海关计量单位' }]
  },
  {
    field: 'taxRefundRate',
    label: '退税率%',
    width: columnWidth.l,
    batchEditFlag: true,
    component: (
      <EplusNumInput
        min={0}
        controls={false}
        precision={2}
        class="!w-90%"
      />
    ),
    rules: [{ required: true, message: '请输入退税率' }]
  },
  {
    field: 'taxRefundPrice',
    label: '退税金额',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row?.taxRefundPrice?.amount ? <EplusMoneyLabel val={row.taxRefundPrice} /> : '-'
    }
  },
  {
    field: 'custName',
    label: '客户名称',
    width: columnWidth.l,
    showOverflowTooltip: true
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
    formatter: (item, row) => {
      return (
        <PurchaseContractTooltip
          contractCode={row.purchaseContractCode || ''}
          rowData={row}
        />
      )
    }
  },
  {
    field: 'venderName',
    label: '供应商名称',
    width: columnWidth.l
  },
  {
    field: 'manager',
    label: '跟单员',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row?.manager?.nickname
    }
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
    field: 'thisPurchaseQuantity',
    label: '本次采购数量',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return formatNum(row?.thisPurchaseQuantity)
    }
  },
  {
    field: 'realLockQuantity',
    label: '本次使用库存数',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return formatNum(row?.realLockQuantity)
    }
  },
  {
    field: 'stockCost',
    label: '库存成本',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.stockCost} />
    }
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
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.purchaseWithTaxPrice} />
    }
  },
  {
    field: 'purchaseTotalQuantity',
    label: '总采购数量',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return formatNum(row?.purchaseTotalQuantity)
    }
  },
  {
    field: 'purchaseTotalAmount',
    label: '总采购金额',
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
          min={1}
          controls={false}
          precision={0}
          class="!w-90%"
          onChange={() => {
            row.boxCountModified = false
          }}
        />
      )
    },
    rules: [{ required: true, message: '请输入应收数量' }]
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
      return (
        <el-input-number
          v-model={row.boxCount}
          min={0}
          controls={false}
          precision={0}
          class="!w-90%"
          onChange={() => {
            row.boxCountModified = true
          }}
        />
      )
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
    label: `外箱体积${VolumeUnit}`,
    width: '150',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return formatNum(getOuterboxVal(row, 'vol') / 1000000, volPrecision)
    }
  },
  {
    field: 'totalVolume',
    label: `总体积${VolumeUnit}`,
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return `${formatNum(row.totalVolume / 1000000, volPrecision)}`
    }
  },
  {
    field: 'unitPerOuterbox',
    label: '外箱单位',
    width: columnWidth.l,
    component: <eplus-dict-select dictType={DICT_TYPE.UNIT_PER_OUTERBOX_TYPE} />,
    rules: [{ required: true, message: '请选择外箱单位' }]
  },
  {
    field: 'outerboxGrossweightStr',
    label: `外箱毛重 ${weightUnit}`,
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return (
        <div class="flex items-center justify-between">
          <div>{getOuterboxVal(row, 'grossweight')}</div>

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
    label: `总毛重 ${weightUnit}`,
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return formatWeight(row.totalGrossweight, true)
    }
  },
  {
    field: 'outerboxNetweightStr',
    label: `外箱净重 ${weightUnit}`,
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return (
        <div class="flex items-center justify-between">
          <div>{getOuterboxVal(row, 'netweight')}</div>

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
    label: `总净重 ${weightUnit}`,
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return formatWeight(row.totalNetweight, true)
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
    field: 'remark',
    label: '备注',
    component: (
      <el-input
        type="textarea"
        rows={1}
      />
    ),
    width: columnWidth.l
  },
  {
    field: 'commissionAmount',
    label: '佣金金额',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row?.commissionAmount?.amount ? <EplusMoneyLabel val={row.commissionAmount} /> : '-'
    }
  },
  {
    field: 'outDate',
    label: '最新出库日期',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row.outDate ? formatTime(row.outDate, 'yyyy-MM-dd') : '-'
    }
  },
  {
    field: 'declarationQuantityOld',
    label: '已报关数',
    width: columnWidth.l
  },
  {
    field: 'outQuantity',
    label: '已出库数量',
    width: columnWidth.l
  },
  {
    field: 'settleOrderFlag',
    label: '是否转结汇单',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return getDictLabel(DICT_TYPE.IS_INT, row.outboundFlag)
    }
  },
  {
    field: 'settlementQuantity',
    label: '结汇数量',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row.settlementQuantity || 0
    }
  },
  {
    field: 'action',
    label: '操作',
    width: columnWidth.l,
    fixed: 'right',
    slot: (item, row, index) => {
      return (
        row?.id && (
          <el-button
            link
            type="primary"
            onClick={() => {
              if (row.converNoticeFlag === 1) {
                message.warning('该条明细已转拉柜通知单，不可拆分')
                return
              } else {
                divideDialogRef.value.open(row)
              }
            }}
            v-hasPermi="dms:shipment:split-item"
          >
            拆分
          </el-button>
        )
      )
    }
  }
])

const remoteMethod = async (query: string, cb: any) => {
  if (query) {
    let checkRes = await HsdataApi.getHsdataPage({ pageSize: 100, pageNo: 1, code: query })
    cb(
      checkRes.list.map((item) => {
        return { value: item.code }
      })
    )
  } else {
    cb([])
  }
}

const router = useRouter()
const openDetail = (row, type) => {
  if (type === 1) {
    setSourceId(row.skuId)
    if (checkPermi(['pms:csku:query']) && checkPermi(['pms:csku:detail'])) {
      router.push({ path: '/base/product-manage/csku' })
    } else {
      message.error('暂无权限查看详情')
    }
  } else {
    setSourceId(row.saleContractItemId)
    if (
      checkPermi(['sms:export-sale-contract:query']) &&
      checkPermi(['sms:export-sale-contract:detail'])
    ) {
      router.push({ path: '/sms/sale-orders/exportSaleContract' })
    } else {
      message.error('暂无权限查看详情')
    }
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

// const weightJson = (item, key, str) => {
//   let [w, u] = item[str] ? item[str].split(SplitKey) : []
//   item[key] = {
//     weight: Number(w),
//     unit: u
//   }
// }

// const weightStr = (item, key, str) => {
//   item[str] = `${item[key].weight || 0}${SplitKey}${item[key].unit || 'kg'}`
// }
const emit = defineEmits(['getCabinet'])

const rateList = useRateStore().rateList
const hsdataCodeChange = (e, row) => {
  let item = e[1].find((item) => item.code === e[0])
  row.hsMeasureUnit = Number.parseInt(getDictValue(DICT_TYPE.HS_MEASURE_UNIT, item?.unit)) || ''
  row.taxRefundRate = item?.taxRefundRate || ''
}
const getTaxRefundPrice = (item) => {
  let result =
    (((item.purchaseWithTaxPrice.amount *
      item.shippingQuantity *
      rateList[item.purchaseWithTaxPrice.currency]) /
      (1 + item.taxRefundRate / 100)) *
      item.taxRefundRate) /
    100
  item.taxRefundPrice.amount = formatterPrice(result, moneyInputPrecision)
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
      //报关英文名转大写
      item.customsDeclarationNameEng = item.customsDeclarationNameEng?.toUpperCase()

      //退税金额
      getTaxRefundPrice(item)
      // item.taxRefundPrice.amount = item.taxRefundUnitPrice
      //   ? Number(item.taxRefundUnitPrice) * Number(item.shippingQuantity)
      //   : item.taxRefundPrice.amount

      // taxRefundPrice
      //报关金额
      item.declarationAmount.amount =
        Number(item.declarationUnitPrice.amount) * Number(item.declarationQuantity)
      item.declarationAmount.currency = item.declarationUnitPrice.currency
      //销售金额
      item.saleAmount.amount = Number(item.saleUnitPrice.amount) * Number(item.shippingQuantity)
      item.saleAmount.currency = item.saleUnitPrice.currency

      //采购总价
      if (!item.purchaseTotalAmount) {
        item.purchaseTotalAmount = { amount: 0, currency: '' }
      }
      if (item.purchaseWithTaxPrice?.amount && item.thisPurchaseQuantity) {
        item.purchaseTotalAmount.amount =
          Number(item.purchaseWithTaxPrice.amount) * Number(item.thisPurchaseQuantity)
        item.purchaseTotalAmount.currency = item.purchaseWithTaxPrice.currency
      }
      //货值合计
      // listTotalGoodsValue.amount += item.saleAmount.amount
      // listTotalGoodsValue.currency = item.saleAmount.currency
      //箱数计算
      if (!item.boxCountModified) {
        item.boxCount = Math.ceil(Number(item.shippingQuantity) / Number(item.qtyPerOuterbox))
      }
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
      // if (typeof item.outerboxGrossweightStr !== 'string') {
      //   item.outerboxGrossweightStr = `${item.outerboxGrossweight.weight}${SplitKey}${item.outerboxGrossweight.unit}`
      // }
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
      // item.declarationName = item.declarationName || item.name
      // item.customsDeclarationNameEng = item.customsDeclarationNameEng || item.nameEng
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
    let arr = tableData.map((item) => {
      return {
        ...item,
        declarationUnitPrice: {
          amount: item.declarationUnitPriceStr,
          currency: item.saleUnitPrice.currency
        },
        insuranceFee: {
          amount: item.insuranceFeeStr,
          currency: 'RMB'
        }
      }
    })
    return arr
  } else {
    message.warning('出运明细提交信息有误')
    return false
  }
}

defineExpose({ tableList, checkData })

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
      'boxCount',
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
      sums[index] = `${formatNum(weightVal)} `
    } else {
      sums[index] = ''
    }
  })

  return sums
}

watch(
  () => props.formData.children,
  (newVal) => {
    if (newVal && newVal.length > 0) {
      tableList.value =
        props.formData.children.map((item) => {
          return {
            ...item,
            declarationQuantity: item.declarationQuantity || item.shippingQuantity,
            declarationUnitPriceStr: item?.declarationUnitPrice?.amount || 0,
            insuranceFeeStr: item?.insuranceFee?.amount || 0,
            saleUnitPriceAmount: item?.saleUnitPrice?.amount || 0,
            // outerboxGrossweightStr: item?.outerboxGrossweight?.weight || 0,
            // outerboxNetweightStr: item?.outerboxNetweight?.weight || 0,
            unitPerOuterbox: item.unitPerOuterbox
              ? item.unitPerOuterbox
              : Number(getDictValue(DICT_TYPE.UNIT_PER_OUTERBOX_TYPE, '箱')) || 1,
            declarationName: item.declarationName || item.name,
            customsDeclarationNameEng: item.customsDeclarationNameEng || item.nameEng,
            boxCountModified: true
          }
        }) || []
    }
  },
  { immediate: true }
)
</script>
