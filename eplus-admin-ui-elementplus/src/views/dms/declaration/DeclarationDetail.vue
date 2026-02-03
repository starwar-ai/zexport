<template>
  <eplus-detail
    ref="eplusDetailRef"
    :page="pageFlag"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :edit="{
      permi: 'dms:declaration:update',
      handler: () => goEdit()
    }"
    @update="getPageInfo"
    backUrl="/dms/shipping-orders/declaration"
  >
    <el-row>
      <el-col :span="24">
        <eplus-description
          title="基础信息"
          :data="pageInfo"
          :items="basicInfo"
        >
          <!-- <template #inputUser>
            {{ pageInfo?.inputUser?.nickname }}
          </template> -->
          <template #jzxNum>
            {{
              pageInfo?.twentyFootCabinetNum +
              pageInfo?.fortyFootCabinetNum +
              pageInfo?.fortyFootContainerNum
            }}
          </template>
        </eplus-description>

        <el-tabs v-model="activeTab">
          <el-tab-pane
            label="出货信息"
            name="1"
          >
            <eplus-description
              title=""
              :data="pageInfo"
              :items="shipmentSchema"
            />
          </el-tab-pane>
          <el-tab-pane
            label="船信息"
            name="3"
          >
            <eplus-description
              title=""
              :data="pageInfo"
              :items="shipSchema"
            />
          </el-tab-pane>
          <el-tab-pane
            label="合计信息"
            name="4"
          >
            <eplus-description
              title=""
              :data="pageInfo"
              :items="totalSchema"
            />
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>
    <el-tabs
      v-model="activeName"
      class="demo-tabs"
    >
      <el-tab-pane
        label="报关单明细"
        name="1"
      >
        <Table
          :columns="tableColumns"
          headerAlign="center"
          align="center"
          :data="pageInfo?.children"
          row-key="id"
        />
      </el-tab-pane>
      <!-- <el-tab-pane
          label="加减项"
          name="2"
        >
          <Table
            :columns="subColumns"
            headerAlign="center"
            align="center"
            :data="pageInfo?.addSubItemList"
          />
        </el-tab-pane> -->
    </el-tabs>
    <template #otherAction>
      <component
        v-for="(item, index) in btnList"
        :key="index"
        :is="item"
      />
    </template>
  </eplus-detail>
  <OtherActionCom
    ref="otherActionComRef"
    @success="getPageInfo"
  />
</template>
<script setup lang="tsx">
import * as DeclarationApi from '@/api/dms/declaration/index'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { formatStringConcat } from '@/utils/formatStringConcat'
import { columnWidth, formatDictColumn, formatMoneyColumn, formatWeightColumn } from '@/utils/table'
import { volPrecision, VolumeUnit } from '@/utils/config'
import download from '@/utils/download'
import { checkPermi } from '@/utils/permission'
import { formatNum } from '@/utils'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { getOuterbox } from '@/utils/outboxSpec'
import OtherActionCom from './OtherActionCom.vue'

const message = useMessage()
const pageInfo = ref<any>({})
const activeTab = ref('1')
const pageId = ref()
const pageFlag = ref(false)
const btnList = ref<any[]>([])

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'CommodityInspectionDetail' })

const loading = ref(true)
const { query } = useRoute()
const activeName = ref('1')
const { updateDialogActions, clearDialogActions } =
  props.id && !useRoute().params.id
    ? (inject('dialogActions') as {
        updateDialogActions: (...args: any[]) => void
        clearDialogActions: () => void
      })
    : useRoute().params.id
      ? {
          updateDialogActions: (...args: any[]) => {
            btnList.value.push(...args)
          },
          clearDialogActions: () => {
            btnList.value.splice(0, btnList.value.length)
          }
        }
      : { updateDialogActions: () => {}, clearDialogActions: () => {} }

const otherActionComRef = ref()
const goEdit = () => {
  otherActionComRef.value.handleUpdate(pageInfo.value.id)
}

const setBtns = () => {
  clearDialogActions()
  if (checkPermi(['dms:declaration:delete'])) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await handleDel()
        }}
        key="Del"
      >
        删除
      </el-button>
    )
  }

  if (checkPermi(['dms:declaration:export'])) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await exportExcel()
        }}
        key="export"
      >
        导出
      </el-button>
    )
    updateDialogActions(
      <el-button
        onClick={async () => {
          await exportExcelENS()
        }}
        key="export"
      >
        ENS导出
      </el-button>
    )
  }
}
const getPageInfo = async () => {
  loading.value = true
  try {
    let res = await DeclarationApi.getDeclarationInfo({ id: pageId.value })
    let saleContractCodes = ''
    let custPos = ''
    let custNames = ''
    let currencys = ''
    let settlementTermTypes = ''
    let settlementNames = ''
    let declarationUnits = ''
    res.children.forEach((item) => {
      saleContractCodes = formatStringConcat(saleContractCodes, item.saleContractCode)
      custNames = formatStringConcat(custNames, item.custName)
      currencys = formatStringConcat(currencys, item.currency)
      settlementTermTypes = formatStringConcat(settlementTermTypes, item.settlementTermType)
      settlementNames = formatStringConcat(settlementNames, item.settlementName)
      custPos = formatStringConcat(custPos, item.custPo)
      declarationUnits = formatStringConcat(declarationUnits, item.declarationUnit)
    })
    pageInfo.value = {
      ...res,
      saleContractCodes: saleContractCodes,
      custPos: custPos,
      custNames: custNames,
      currencys: currencys,
      declarationUnits: declarationUnits,
      settlementNames: settlementNames,
      settlementTermTypes: settlementTermTypes
    }
    pageInfo.value?.children.forEach((cItem: any) => {
      //采购合计
      cItem.purchaseTotalAmount = { amount: 0, currency: '' }
      cItem.purchaseTotalAmount.amount =
        Number(cItem.purchaseWithTaxPrice.amount) * Number(cItem.purchaseTotalQuantity)
      cItem.purchaseTotalAmount.currency = cItem.purchaseWithTaxPrice.currency
    })
    setBtns()
  } catch {
    pageInfo.value = {}
  } finally {
    loading.value = false
  }
}

const handleDel = async () => {
  await message.confirm('是否删除所选中数据？')
  await DeclarationApi.deleteDeclaration(pageInfo.value?.id)
  message.success('删除成功')
  eplusDetailRef.value?.close()
}
const exportExcel = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    const data = await DeclarationApi.exportDeclaration({
      id: pageId.value,
      reportCode: 'declaration-form-export',
      exportType: 'detail'
    })
    let exportName = pageInfo.value?.custPo || pageInfo.value?.saleContractCodes || ''
    if (data && data.size) {
      download.excel(data, `报关单${exportName}.xlsx`)
    }
  } catch {}
  return
}
const exportExcelENS = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    const data = await DeclarationApi.exportDeclaration({
      id: pageId.value,
      reportCode: 'declaration-form-export-ens',
      exportType: 'ens'
    })
    let exportName = pageInfo.value?.custPo || pageInfo.value?.saleContractCodes || ''
    if (data && data.size) {
      download.excel(data, `报关单(ENS)${exportName}.xlsx`)
    }
  } catch {}
  return
}
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
const shipSchema: EplusDescriptionItemSchema[] = [
  {
    field: 'forwarderCompanyName',
    label: '船代公司'
  },
  {
    field: 'shipNum',
    label: '航名/船次'
  },
  {
    field: 'billLadingNum',
    label: '提单号'
  }
]

/**
 * 生成明细信息
 * @param r
 */
const basicInfo: EplusDescriptionItemSchema[] = [
  {
    field: 'invoiceCode',
    label: '发票号'
  },
  {
    field: 'invoiceDate',
    label: '发票日期',
    type: 'date'
  },
  {
    field: 'protocolCode',
    label: '报关协议号'
  },
  {
    field: 'foreignTradeCompanyName',
    label: '归属公司'
  },
  {
    field: 'code',
    label: '报关单号'
  },
  {
    field: 'estDepartureTime',
    label: '实际开船日期',
    type: 'date'
  },
  {
    field: 'inputDate',
    label: '制单日期',
    type: 'date'
  },
  {
    field: 'inputUserName',
    label: '录入人'
  },
  {
    field: 'saleContractCodes',
    label: '外销合同'
  },
  {
    field: 'custPos',
    label: '客户合同'
  },
  {
    field: 'custNames',
    label: '客户名称'
  },
  {
    field: 'containerQuantity',
    label: '集装箱数量'
  },
  {
    field: 'currencys',
    label: '外销币种'
  },
  {
    field: 'tradeCountryName',
    label: '贸易国别'
  },
  {
    field: 'settlementTermTypes',
    label: '价格条款'
  },
  {
    field: 'settlementNames',
    label: '收款方式'
  },
  {
    field: 'departurePortName',
    label: '出运口岸'
  },
  {
    field: 'destinationPortName',
    label: '目的口岸'
  },
  {
    field: 'transportType',
    label: '运输方式',
    dictType: DICT_TYPE.TRANSPORT_TYPE
  },
  {
    field: 'shipmentCode',
    label: '出运单号'
  }
]

const cabinetSchema: EplusDescriptionItemSchema[] = [
  {
    field: 'twentyFootCabinetNum',
    label: '20尺柜'
  },
  {
    field: 'fortyFootCabinetNum',
    label: '40尺柜'
  },
  {
    field: 'fortyFootContainerNum',
    label: '40尺高柜'
  },
  {
    slotName: 'bulkHandlingVolume',
    field: 'bulkHandlingVolume',
    label: `散货${VolumeUnit}`
  }
]

const totalSchema: EplusDescriptionItemSchema[] = [
  // {
  //   field: 'totalGoodsValue',
  //   label: '货值合计',
  //   type: 'money'
  // },
  {
    field: 'totalQuantity',
    label: '数量合计'
  },
  {
    field: 'totalBoxes',
    label: '箱数合计'
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
  },
  {
    field: 'totalQuantity',
    label: '报关合计'
  }
  // {
  //   field: 'totalPurchase',
  //   label: '采购合计',
  //   type: 'money'
  // }
]

const tableColumns = [
  {
    type: 'index',
    label: '序号',
    width: '60',
    align: 'center',
    index: (index: number) => index + 1
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
    width: columnWidth.xl
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
    width: columnWidth.l
  },
  {
    field: 'declarationName',
    label: '报关中文名',
    width: columnWidth.l
  },
  {
    field: 'customsDeclarationNameEng',
    label: '报关英文名',
    width: columnWidth.l
  },
  {
    field: 'barcode',
    label: '条形码',
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
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.HS_MEASURE_UNIT)
  },
  {
    field: 'declarationUnitPrice',
    label: '报关单价',
    width: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'declarationAmount',
    label: '报关总价',
    width: columnWidth.l,
    formatter: formatMoneyColumn()
  },

  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    width: columnWidth.l,
    formatter: (row) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'boxCount',
    label: '箱数',
    width: columnWidth.l,
    formatter: (row) => {
      return <span>{formatNum(row.boxCount * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'unitPerOuterbox',
    label: '外箱单位',
    width: columnWidth.l,
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.UNIT_PER_OUTERBOX_TYPE, row?.unitPerOuterbox) || '-'
    }
  },
  {
    field: 'outerboxVolume',
    label: '外箱体积',
    width: columnWidth.l,
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
        return formatNum(row.totalVolume / 1000000, volPrecision)
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
    field: 'taxRefundRate',
    label: '退税率%',
    width: columnWidth.l
  },
  {
    field: 'custPo',
    label: '客户合同',
    width: columnWidth.l
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

onMounted(async () => {
  if (useRoute().params.id) {
    pageId.value = useRoute().params.id
    pageFlag.value = true
  } else {
    pageFlag.value = false
    if (!props.id && !query.id) {
      message.error('ID不能为空')
      if (!props.id) {
        eplusDetailRef.value?.close()
      }
    }
    if (query.id) {
      showProcessInstanceTaskListFlag.value = false
      outDialogFlag.value = true
      pageId.value = query.id
    }
    if (props.id) {
      showProcessInstanceTaskListFlag.value = true
      outDialogFlag.value = false
      pageId.value = props.id
    }
  }
  await getPageInfo()
})
</script>
<style lang="scss" scoped></style>
