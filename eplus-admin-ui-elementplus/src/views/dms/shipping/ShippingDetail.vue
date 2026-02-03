<template>
  <eplus-detail
    ref="eplusDetailRef"
    scrollFlag
    :mainRatio="3"
    :page="pageFlag"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    @update="handleRefresh"
    :revertAudit="{
      permi: 'scm:shipment:anti-audit',
      handler: handleRevertAudit
    }"
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
        <template #documenter>
          {{ pageInfo?.documenter?.nickname }}
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
          label="尺柜信息"
          name="2"
        >
          <eplus-description
            title=""
            :data="pageInfo"
            :items="cabinetSchema"
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
    </template>

    <template #pageBottomTabs>
      <el-tabs v-model="activeName">
        <el-tab-pane
          label="出运明细"
          name="1"
        />
        <el-tab-pane
          label="临时产品"
          name="2"
        />
        <el-tab-pane
          label="加减项"
          name="3"
        />
        <el-tab-pane
          label="关联单据"
          name="4"
        />
        <el-tab-pane
          label="单证费用"
          name="5"
          v-if="
            [
              ShipmentDetailStatusEnum.SHIPPED.status,
              ShipmentDetailStatusEnum.COMPLETED.status,
              ShipmentDetailStatusEnum.WAITING_SHIPMENT.status
            ].includes(pageInfo?.status)
          "
        />
      </el-tabs>
    </template>
    <template #pageBottom>
      <el-scrollbar always>
        <div v-show="activeName === '1'">
          <div
            class="h-35px"
            v-if="checkPermi(['dms:shipment:delete-item', 'dms:shipment:merge-item'])"
          >
            <el-button
              @click="handleRemove"
              v-hasPermi="['dms:shipment:delete-item']"
            >
              批量移除
            </el-button>
            <el-button
              @click="handleMigrate"
              v-hasPermi="['dms:shipment:merge-item']"
              >批量迁移
            </el-button>
          </div>
          <Table
            class="childrenTable"
            :columns="tableColumns"
            headerAlign="center"
            align="center"
            :data="pageInfo?.children"
            :height="
              checkPermi(['dms:shipment:delete-item', 'dms:shipment:merge-item'])
                ? tableMaxHeight - 35
                : tableMaxHeight
            "
            showSummary
            sumText="合计"
            :summaryMethod="handleSummary"
            :row-key="(row) => row.id"
            @selection-change="handleSelectionChange"
          />
        </div>
        <div v-show="activeName === '2'">
          <Table
            v-if="isValidArray(pageInfo?.temporarySkuList)"
            :columns="skuColumns"
            headerAlign="center"
            align="center"
            :data="pageInfo?.temporarySkuList"
          />
          <span v-else>暂无数据</span>
        </div>
        <div v-show="activeName === '3'">
          <Table
            v-if="isValidArray(pageInfo?.addSubItemList)"
            :columns="subColumns"
            headerAlign="center"
            align="center"
            :data="pageInfo?.addSubItemList"
          />
          <span v-else>暂无数据</span>
        </div>
        <div v-show="activeName === '4'">
          <RelateTable
            ref="RelateTableRef"
            v-if="!loading"
            :data="pageInfo"
          />
        </div>
        <div v-show="activeName === '5'">
          <ForwarderFeeDetail
            :info="pageInfo"
            @refresh="handleRefresh"
          />
        </div>
      </el-scrollbar>
    </template>
    <template #otherAction>
      <component
        v-for="(item, index) in btnList"
        :key="index"
        :is="item"
      />
    </template>
  </eplus-detail>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
  >
    <template #edit="{ key, param }">
      <ShippingForm
        :type="param"
        :id="key"
        mode="edit"
      />
    </template>
  </eplus-dialog>
  <ForwardDialogs
    v-if="pageInfo?.children"
    ref="ContainerNoticeRef"
    :detailInfo="pageInfo?.children"
    @sure="handleRefresh"
  />

  <InspectDia
    ref="InspectDiaRef"
    @sure="handleRefresh"
  />
  <DeclarationDia
    ref="DeclarationDiaRef"
    @sure="handleRefresh"
  />
  <SettlementFormDia
    ref="SettlementFormDiaRef"
    @sure="handleRefresh"
  />
  <ToOrderNotice
    ref="ToOrderNoticeRef"
    @success="handleRefresh"
  />
  <ExportDia ref="ExportDiaRef" />

  <MigrateDia
    :exceptId="pageInfo?.id"
    ref="MigrateDiaRef"
    @sure="handleRefresh"
  />

  <ShipmentDia
    ref="ShipmentDiaRef"
    @success="handleRefresh"
  />
</template>
<script setup lang="tsx">
import * as ShipmentApi from '@/api/dms/shipment/index'
import { EplusDesc, EplusDetail, EplusImgEnlarge } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE } from '@/utils/dict'
import { formatNum, getCurrency, getTotalAmount } from '@/utils/index'
import {
  columnWidth,
  formatDateColumn,
  formatDictColumn,
  formatMoneyColumn,
  formatNumColumn,
  formatWeightColumn
} from '@/utils/table'
import { volPrecision, VolumeUnit, weightUnit } from '@/utils/config'
import ShippingForm from './ShippingForm.vue'
import RelateTable from '@/components/RelateTable/src/RelateTable.vue'
import ForwarderFeeDetail from './components/ForwarderFeeDetail.vue'
import { ShipmentDetailStatusEnum } from '@/utils/constants'
import ForwardDialogs from './components/ForwardDialogs.vue'
import InspectDia from './components/InspectDia.vue'
import DeclarationDia from './components/DeclarationDia.vue'
import SettlementFormDia from './components/SettlementFormDia.vue'
import { getContractIdByCode } from '@/api/sms/saleContract/export/index'
import LockCom from '@/views/sms/sale/exportSaleContract/components/LockCom.vue'
import ExportDia from './components/ExportDia.vue'
import CheckStatusLog from '@/views/dms/shippingPlan/components/CheckStatusLog.vue'
import ToOrderNotice from './components/ToOrderNotice.vue'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import download from '@/utils/download'
import MigrateDia from './components/MigrateDia.vue'
import PurchaseContractTooltip from './components/PurchaseContractTooltip.vue'
import ShipmentDia from './components/ShipmentDia.vue'
import { setSourceId } from '@/utils/auth'
import { checkPermi } from '@/utils/permission.js'
import { isAmount, isNumber, isValidArray, isWeight } from '@/utils/is'
import { getOuterbox, getOuterboxVal } from '@/utils/outboxSpec'

const eplusDialogRef = ref()
const message = useMessage()
const pageInfo = ref({})
const handleChange = (id, type) => {
  eplusDialogRef.value?.openEdit(id, type === 'change' ? '变更' : undefined, type)
}
const eplusDetailRef = ref()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
}>()
const ContainerNoticeRef = ref()
const pageId = ref()
const pageFlag = ref(false)
const btnList = ref<any[]>([])
const toNotice = (type) => {
  let childrenIds = pageInfo.value?.children.map((item) => item.id)
  ContainerNoticeRef.value?.open(childrenIds, type)
}

const router = useRouter()
const openProductDetail = async (row, type) => {
  if (type === 1) {
    setSourceId(row.skuId)
    if (checkPermi(['pms:csku:query']) && checkPermi(['pms:csku:detail'])) {
      router.push({ path: '/base/product-manage/csku' })
    } else {
      message.error('暂无权限查看详情')
    }
  } else {
    let saleContractId = await getContractIdByCode(row.saleContractCode)
    setSourceId(saleContractId)
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

defineOptions({ name: 'ShippingDetail' })
//定义edit事件
const { close } =
  props.id && !pageFlag.value
    ? (inject('dialogEmits') as {
        close: () => void
      })
    : { close: () => {} }
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
const loading = ref(true)
const { query } = useRoute()
const activeName = ref('1')
const activeTab = ref('1')

const ShipmentDiaRef = ref()
const handleShipment = async () => {
  ShipmentDiaRef.value.open(pageInfo.value)
}
const ToOrderNoticeRef = ref()
const handleToInvoicNotice = () => {
  // ElMessageBox.confirm('确认对选中数据进行转开票通知吗？', '提示', {
  //   confirmButtonText: '确定',
  //   cancelButtonText: '取消',
  //   type: 'warning'
  // }).then(async () => {
  //   await ShipmentApi.toInvoicNotice({ itemIds: id })
  //   message.success('操作成功!')
  //   push('/scm/vender-payment/invoicingNotices')
  // })
  ToOrderNoticeRef.value.open(pageInfo.value)
}
const handleFinish = (id) => {
  ElMessageBox.confirm('出运单号的交接是否已达成，确认完毕意味着该单据流程结束。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await ShipmentApi.shipmentFinish({ id })
    message.success('操作成功!')
  })
}
const handleRevertAudit = async () => {
  await ShipmentApi.revertAudit(pageInfo.value?.id)
}

const toInspectCheck = () => {
  let tableList: any = []
  pageInfo.value.children.forEach((item: any) => {
    if (!item.isToCommodityInspection && item.commodityInspectionFlag === 1) {
      tableList.push(item)
    }
  })
  return isValidArray(tableList)
}

const toSettlementFormCheck = () => {
  let tableList: any = []
  pageInfo.value.children.forEach((item: any) => {
    if (!item.settlementQuantity) {
      tableList.push(item)
    }
  })
  return isValidArray(tableList)
}
const toDeclarationCheck = () => {
  let tableList: any = []
  pageInfo.value.children.forEach((item: any) => {
    if (item.shippingQuantity > item.declarationQuantityOld) {
      tableList.push(item)
    }
  })
  return isValidArray(tableList)
}

const toNoticeCheck = (type = 0) => {
  // let tableList: any = []
  // pageInfo.value.children.forEach((item: any) => {
  //   if (item.converNoticeFlag === 0) {
  //     tableList.push(item)
  //   }
  // })
  // return isValidArray(tableList)

  let tableList: any = []
  if (type === 1) {
    tableList = pageInfo.value.children.filter(
      (item) => item.converNoticeFlag === 0 && item.shippedAddress === 1
    )
  } else {
    tableList = pageInfo.value.children.filter((item) => item.converNoticeFlag === 0)
  }
  return isValidArray(tableList)
}

const handleShipmentClose = async (id) => {
  await message.confirm('确认进行作废操作吗？')
  await ShipmentApi.shipmentClose({ parentId: id })
  message.success('操作成功!')
  getPageInfo()
}

const setBtns = () => {
  if (
    pageInfo.value?.confirmFlag !== 0 &&
    pageInfo.value.changeStatus != 2 &&
    pageInfo.value?.batchFlag === 0
  ) {
    if (
      pageInfo.value?.status === ShipmentDetailStatusEnum.WAITING_PROCESS.status &&
      checkPermi(['dms:shipment:update'])
    ) {
      updateDialogActions(
        <el-button
          onClick={() => {
            handleChange(pageInfo.value?.id, 'edit')
          }}
          key="edit"
        >
          编辑
        </el-button>,
        <el-button
          onClick={() => {
            handleChange(pageInfo.value?.id, 'edit')
          }}
          key="edit"
        >
          提交
        </el-button>
      )
    }
    if (
      pageInfo.value?.status !== ShipmentDetailStatusEnum.WAITING_PROCESS.status &&
      pageInfo.value?.status !== ShipmentDetailStatusEnum.FINISH.status
    ) {
      if (checkPermi(['dms:shipment:to-settlement-form'])) {
        updateDialogActions(
          <el-button
            onClick={() => {
              toSettlementForm()
            }}
            key="settlement"
            disabled={!toSettlementFormCheck()}
          >
            转结汇单
          </el-button>
        )
      }
      if (checkPermi(['dms:shipment:to-commodity-inspection'])) {
        updateDialogActions(
          <el-button
            onClick={() => {
              toInspect()
            }}
            key="commodityInspection"
            disabled={!toInspectCheck()}
          >
            转商检单
          </el-button>
        )
      }
      if (checkPermi(['dms:shipment:to-declaration'])) {
        updateDialogActions(
          <el-button
            onClick={() => {
              toDeclaration()
            }}
            key="dmsShipmentToDeclaration"
            disabled={!toDeclarationCheck()}
          >
            转报关单
          </el-button>
        )
      }
    }
    if (pageInfo.value?.status === ShipmentDetailStatusEnum.WAITING_SHIPMENT.status) {
      if (checkPermi(['dms:container-transportation:query'])) {
        updateDialogActions(
          <el-button
            onClick={() => {
              toNotice(0)
            }}
            key="containerTransportation"
            disabled={!toNoticeCheck(0)}
          >
            转拉柜通知单
          </el-button>
        )
      }
      if (checkPermi(['dms:container-transportation-out:factory'])) {
        updateDialogActions(
          <el-button
            onClick={() => {
              toNotice(1)
            }}
            key="containerTransportation"
            disabled={!toNoticeCheck(1)}
          >
            工厂出库
          </el-button>
        )
      }
    }
    if (pageInfo.value?.status < 4 && checkPermi(['dms:shipment:close'])) {
      updateDialogActions(
        <el-button
          key="dmsShipmentClose"
          onClick={() => {
            handleShipmentClose(pageInfo.value?.id)
          }}
        >
          作废
        </el-button>
      )
    }
    if (
      pageInfo.value?.status == ShipmentDetailStatusEnum.WAITING_SHIPMENT.status &&
      checkPermi(['dms:shipment:shipment'])
    ) {
      updateDialogActions(
        <el-button
          onClick={() => {
            handleShipment()
          }}
          key="dmsShipmentClose"
        >
          确认出运
        </el-button>
      )
    }
    if (
      (pageInfo.value?.status == ShipmentDetailStatusEnum.WAITING_SHIPMENT.status ||
        pageInfo.value?.status == ShipmentDetailStatusEnum.SHIPPED.status) &&
      checkPermi(['dms:export-sale-contract-change-business:update'])
    ) {
      updateDialogActions(
        <el-button
          onClick={() => {
            handleChange(pageId.value, 'change')
          }}
          key="change"
        >
          变更
        </el-button>
      )
    }
    if (
      pageInfo.value?.status == ShipmentDetailStatusEnum.SHIPPED.status &&
      checkPermi(['dms:shipment:finish'])
    ) {
      updateDialogActions(
        <el-button
          onClick={() => {
            handleFinish(pageInfo.value?.id)
          }}
          key="dmsShipmentFinish"
        >
          交单
        </el-button>
      )
    }
    if (
      [
        ShipmentDetailStatusEnum.SHIPPED.status,
        ShipmentDetailStatusEnum.WAITING_SHIPMENT.status
      ].includes(pageInfo.value?.status) &&
      pageInfo.value?.children.some((item) => item.invoiceStatus === 0) &&
      checkPermi(['dms:shipment:transform-invoicing-notices'])
    ) {
      updateDialogActions(
        <el-button
          onClick={() => {
            handleToInvoicNotice()
          }}
          key="dmsShipmentToInvoicNotice"
        >
          转开票通知
        </el-button>
      )
    }
  }
  if (pageInfo.value?.confirmFlag !== 0 && checkPermi(['dms:shipment:detail-export'])) {
    updateDialogActions(
      <el-button
        onClick={() => {
          handleRowExport()
        }}
        key="rowExport"
      >
        托单导出
      </el-button>
    )
    updateDialogActions(
      <el-button
        onClick={() => {
          exportHsCode()
        }}
        key="hscodeExport"
      >
        hscode导出
      </el-button>
    )
    updateDialogActions(
      <el-button
        onClick={() => {
          exportDetail()
        }}
        key="detailExport"
      >
        明细导出
      </el-button>
    )
  }
}
const getPageInfo = async () => {
  loading.value = true
  try {
    pageInfo.value = await ShipmentApi.getShipmentInfo({ id: pageId.value })
    computedPageInfo()
    setBtns()
  } catch {
    pageInfo.value = {}
  } finally {
    loading.value = false
  }
}

//托单导出
const ExportDiaRef = ref()
const handleRowExport = () => {
  ExportDiaRef.value?.open(pageInfo.value)
}
const uniqueArr = (arr) => {
  return Array.from(new Set(arr.map((item) => JSON.stringify(item)))).map((item) =>
    JSON.parse(item)
  )
}
const exportHsCode = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    var custList = uniqueArr(
      pageInfo.value?.children.map((item) => {
        return {
          label: item.custName,
          value: item.custCode
        }
      })
    )
    const data = await ShipmentApi.exportShipmentDetail({
      id: props?.id ? props?.id : pageId.value,
      reportCode: 'dms-shipment-hscode',
      exportType: 'hsCode',
      custCode: custList[0].value
    })
    if (data && data.size) {
      download.excel(data, `海关编码${pageInfo.value?.code}.xlsx`)
    }
  } catch {}
  return
}
const exportDetail = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    var custList = uniqueArr(
      pageInfo.value?.children.map((item) => {
        return {
          label: item.custName,
          value: item.custCode
        }
      })
    )
    const data = await ShipmentApi.exportShipmentDetail({
      id: props?.id ? props?.id : pageId.value,
      reportCode: 'dms-shipment-detail',
      exportType: 'detail',
      custCode: custList[0].value
    })
    if (data && data.size) {
      download.excel(data, `明细${pageInfo.value?.code}.xlsx`)
    }
  } catch {}
  return
}

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
    field: 'foreignTradeCompanyName',
    label: '报关公司'
  },
  {
    field: 'shipmentPlanCode',
    label: '出运计划单号'
  },
  {
    field: 'code',
    label: '出运单号'
  },
  {
    field: 'custDeliveryDate',
    label: '客户交期',
    type: 'date'
  },
  {
    field: 'custName',
    label: '客户名称',
    xl: 8,
    lg: 12
  },
  {
    field: 'saleContractCode',
    label: '外销合同号',
    xl: 8,
    lg: 12
  },
  {
    field: 'custPo',
    label: '客户PO号',
    xl: 8,
    lg: 12
  },
  {
    field: 'settlementNamelist',
    label: '结汇方式',
    xl: 8,
    lg: 12,
    formatter: () => {
      return [...new Set(pageInfo.value?.children.map((item) => item.settlementName))].join(',')
    }
  },
  {
    field: 'settlementTermType',
    label: '价格条款'
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
    field: 'departureCountryName',
    label: '出运国'
  },
  {
    field: 'departurePortName',
    label: '出运口岸'
  },
  {
    field: 'transportType',
    label: '运输方式',
    dictType: DICT_TYPE.TRANSPORT_TYPE
  },
  {
    slotName: 'documenter',
    field: 'documenter',
    label: '单证员'
  },
  {
    field: 'inboundDate',
    label: '实际拉柜/进仓日期',
    type: 'date'
  },
  {
    field: 'estDepartureTime',
    label: '实际开船日期',
    type: 'date'
  },
  {
    field: 'managerName',
    label: '业务员'
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
    field: 'status',
    label: '单据状态',
    dictType: DICT_TYPE.SHIPPING_STATUS
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

const shipSchema = reactive([
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

  // {
  //   field: 'estDepartureTime',
  //   label: '预计结港时间',
  //   type: 'time'
  // },
  // {
  //   field: 'estClearanceTime',
  //   label: '预计结关时间',
  //   type: 'time'
  // },
  // {
  //   field: 'estClosingTime',
  //   label: '预计结单时间',
  //   type: 'time'
  // }
])

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
    field: 'totalDeclaration',
    label: '报关合计',
    type: 'money'
  },
  {
    field: 'purchaseTotalAmount',
    label: '采购合计',
    type: 'money'
  }

  // 12.5 罗总让去掉
  // {
  //   slotName: 'totalTaxRefundPrice',
  //   field: 'totalTaxRefundPrice',
  //   label: '退税总额'
  // },
  // {
  //   slotName: 'commissionAmount',
  //   field: 'commissionAmount',
  //   label: '佣金金额'
  // },
  // {
  //   slotName: 'receivedNum',
  //   field: 'receivedNum',
  //   label: '已收货值'
  // },
  // {
  //   slotName: 'unreceivedNum',
  //   field: 'unreceivedNum',
  //   label: '未收货值'
  // },
  // {
  //   slotName: 'insuranceFee',
  //   field: 'insuranceFee',
  //   label: '保险费用'
  // }
]

const tableColumns = [
  {
    type: 'selection',
    width: '50',
    align: 'center',
    fixed: 'left'
  },
  // {
  //   type: 'index',
  //   label: '序号',
  //   width: '60',
  //   align: 'center',
  //   fixed: 'left'
  // },
  {
    field: 'mainPicture',
    label: '图片',
    width: columnWidth.m,
    fixed: 'left',
    align: 'center',
    formatter: (row) => {
      return (
        <EplusImgEnlarge
          mainPicture={row?.mainPicture}
          thumbnail={row?.thumbnail}
        />
      )
    }
  },
  {
    field: 'companyName',
    label: '下单主体',
    width: columnWidth.l,
    formatter: (row) => {
      return row.companyPath?.name || '-'
    }
  },
  // {
  //   field: 'companyPath',
  //   label: '订单路径',
  //   width: '220',
  //   slots: {
  //     default: (data) => {
  //       const { row } = data
  //       return getCompanyPathNameFromObj(row.companyPath) || ''
  //     }
  //   }
  // },
  {
    field: 'billStatus',
    label: '入库地点',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.SALE_ITEM_BILL_STATUS)
  },
  {
    field: 'shippedAddress',
    label: '发货地点',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.SHIPPED_ADDRESS)
  },
  {
    field: 'outboundFlag',
    label: '是否出库',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.IS_INT)
  },
  {
    field: 'saleContractCode',
    label: '外销合同',
    width: columnWidth.l,
    sortable: true,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <span
            onClick={() => openProductDetail(row, 2)}
            style="color:rgb(0, 91, 245);cursor:pointer;"
          >
            {row.saleContractCode}
          </span>
        )
      }
    }
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    sortable: true,
    width: columnWidth.l
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: columnWidth.l
  },
  {
    field: 'custPo',
    label: '客户PO号',
    width: columnWidth.l,
    sortable: true
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
  //             <span
  //               onClick={() => openProductDetail(row, 1)}
  //               style="color:rgb(0, 91, 245);cursor:pointer;"
  //             >
  //               {row.skuCode}
  //             </span>
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
  //         return (
  //           <span
  //             onClick={() => openProductDetail(row, 1)}
  //             style="color:rgb(0, 91, 245);cursor:pointer;"
  //           >
  //             {row.skuCode}
  //           </span>
  //         )
  //       }
  //     }
  //   }
  // },
  {
    field: 'nameEng',
    label: '英文品名',
    width: columnWidth.l,
    formatter: (row) => {
      return <div class="break-word">{row.nameEng}</div>
    }
  },
  {
    field: 'name',
    label: '中文品名',
    width: columnWidth.l,
    formatter: (row) => {
      return <div class="break-word">{row.name}</div>
    }
  },
  {
    field: 'barcode',
    label: '条形码',
    width: columnWidth.l,
    formatter: (row) => {
      return <div class="break-word">{row.barcode}</div>
    }
  },
  {
    field: 'shippingQuantity',
    label: '出运数量',
    width: columnWidth.l,
    formatter: formatNumColumn()
  },
  {
    field: 'declarationQuantity',
    label: '报关数量',
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
    field: 'declarationUnitPrice',
    label: '报关单价',
    width: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'declarationAmount',
    label: '报关金额',
    width: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  // {
  //   field: 'freeFlag',
  //   label: '是否含赠品',
  //   width: columnWidth.l,
  //   formatter: formatDictColumn(DICT_TYPE.CONFIRM_TYPE)
  // },
  // {
  //   field: 'freeQuantity',
  //   label: '赠品数量',
  //   width: columnWidth.l,
  //   formatter: formatNumColumn()
  // },
  // {
  //   field: 'estPickupTime',
  //   label: '预计拉柜日期',
  //   width: '120',
  //   formatter: formatDateColumn('YYYY-MM-DD')
  // },
  {
    field: 'commodityInspectionFlag',
    label: '是否商检',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.IS_INT)
  },
  {
    field: 'commodityInspectionType',
    label: '商检负责方',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.COMMODITY_INSPECTION_TYPE)
  },
  {
    field: 'hsCode',
    label: 'HS编码',
    width: columnWidth.l
  },
  {
    field: 'customsDeclarationNameEng',
    label: '报关英文名',
    width: columnWidth.l,
    formatter: (row) => {
      return <div class="break-word">{row.customsDeclarationNameEng}</div>
    }
  },
  {
    field: 'declarationName',
    label: '报关中文名',
    width: columnWidth.l,
    formatter: (row) => {
      return <div class="break-word">{row.declarationName}</div>
    }
  },
  {
    field: 'hsMeasureUnit',
    label: '海关计量单位',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.HS_MEASURE_UNIT)
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
  {
    field: 'custName',
    label: '客户名称',
    width: columnWidth.l
  },

  {
    field: 'declarationQuantity',
    label: '报关数量',
    width: columnWidth.l,
    formatter: formatNumColumn()
  },
  {
    field: 'stockQuantity',
    label: '锁库数量',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <LockCom
            data={{
              saleContractItemId: row.saleContractItemId,
              saleContractCode: row.saleContractCode,
              companyIdList: row.companyIdList,
              skuCode: row.skuCode,
              realLockQuantity: row.realLockQuantity
            }}
          />
        )
      }
    }
  },

  // {
  //   field: 'purchaseContractCode',
  //   label: '采购合同',
  //   width: columnWidth.l,
  //   slots: {
  //     default: (data) => {
  //       const { row } = data
  //       return (
  //         <PurchaseContractTooltip
  //           contractCode={row.purchaseContractCode || ''}
  //           rowData={row}
  //           contractType="purchase"
  //         />
  //       )
  //     }
  //   }
  // },
  {
    field: 'stockPurchaseContractCodes',
    label: '关联采购合同',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <PurchaseContractTooltip
            contractCode={row.purchaseContractCode || ''}
            rowData={row}
          />
        )
      }
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
    slots: {
      default: (data) => {
        const { row } = data
        return row?.manager?.nickname || '-'
      }
    }
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
    field: 'thisPurchaseQuantity',
    label: '本次采购数量',
    width: columnWidth.l,
    formatter: formatNumColumn()
  },
  {
    field: 'thisUsedStockQuantity',
    label: '本次使用库存数',
    width: columnWidth.l,
    formatter: (row) => {
      if (row.shippingQuantity && row.thisPurchaseQuantity) {
        let val = row.shippingQuantity - row.thisPurchaseQuantity
        return val > 0 ? val : 0
      } else {
        return row.shippingQuantity
      }
    }
  },
  {
    field: 'stockCost',
    label: '库存成本',
    width: columnWidth.l,
    formatter: formatMoneyColumn()
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
    field: 'purchaseTotalQuantity',
    label: '总采购数量',
    width: columnWidth.l,
    formatter: formatNumColumn()
  },
  {
    field: 'purchaseTotalAmount',
    label: '总采购金额',
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
    width: columnWidth.l
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
    field: 'outerbox',
    label: '外箱规格',
    width: columnWidth.l,
    formatter: (row) => {
      return <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>
    }
  },
  {
    field: 'outerboxVolume',
    label: `外箱体积${VolumeUnit}`,
    width: columnWidth.l,
    formatter: (row) => {
      return formatNum(getOuterboxVal(row, 'vol') / 1000000, volPrecision)
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
    field: 'unitPerOuterbox',
    label: '外箱单位',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.UNIT_PER_OUTERBOX_TYPE)
  },
  {
    field: 'outerboxGrossweight',
    label: `外箱毛重${weightUnit}`,
    width: columnWidth.l,
    formatter: (row) => {
      return <div>{getOuterboxVal(row, 'grossweight')}</div>
    }
  },
  {
    field: 'totalGrossweight',
    label: `总毛重${weightUnit}`,
    width: columnWidth.l,
    formatter: formatWeightColumn(true)
  },
  {
    field: 'outerboxNetweight',
    label: `外箱净重${weightUnit}`,
    width: columnWidth.l,
    formatter: (row) => {
      return <div>{getOuterboxVal(row, 'netweight')}</div>
    }
  },
  {
    field: 'totalNetweight',
    label: `总净重${weightUnit}`,
    width: columnWidth.l,
    formatter: formatWeightColumn(true)
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
  // {
  //   field: 'orderGrossProfitRate',
  //   label: '毛利率%',
  //   width: columnWidth.l
  // },

  {
    field: 'outDate',
    label: '最新出库日期',
    width: columnWidth.l,
    formatter: formatDateColumn()
  },
  // {
  //   field: 'declarationUnit',
  //   label: '报关单位',
  //   width: columnWidth.l
  // },
  {
    field: 'declaredQuantity',
    label: '已报关数量',
    width: columnWidth.l,
    formatter: formatNumColumn()
  },
  {
    field: 'outQuantity',
    label: '已出库数量',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return formatNum(row?.outQuantity) || '0'
      }
    }
  },
  {
    field: 'settleOrderFlag',
    label: '是否转结汇单',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.IS_INT)
  },
  {
    field: 'settlementName',
    label: '结汇方式',
    width: columnWidth.l
  },
  {
    field: 'forwarderShareAmount',
    label: '单证费用均摊金额',
    width: columnWidth.xl,
    formatter: formatMoneyColumn()
  }
]
const skuColumns = [
  {
    field: 'custName',
    label: '客户',
    width: '300'
  },
  {
    field: 'hsCode',
    label: '海关编码'
  },
  {
    field: 'declarationElement',
    label: '报关要素',
    width: '300'
  },
  {
    field: 'declarationName',
    label: '报关品名'
  },
  {
    field: 'declarationNameEng',
    label: '报关英文名'
  },
  {
    field: 'expectCount',
    label: '出货数量',
    width: columnWidth.l,
    formatter: formatNumColumn()
  },
  {
    field: 'hsMeasureUnit',
    label: '海关计量单位',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.HS_MEASURE_UNIT)
  },
  {
    field: 'declarationCount',
    label: '报关数量',
    width: columnWidth.l,
    formatter: formatNumColumn()
  },
  {
    field: 'boxCount',
    label: '箱数',
    width: columnWidth.l,
    formatter: formatNumColumn()
  },
  // {
  //   field: 'unitPerOuterbox',
  //   label: '外箱单位',
  //   component: <el-input />,
  //   rules: { required: true, message: '请输入外箱单位' }
  // },
  // {
  //   field: 'pricingMethod',
  //   label: '计价方式',
  //   component: <el-input type="number" />,
  //   rules: { required: true, message: '请输入计价方式' }
  // },
  {
    field: 'declarationUnit',
    label: '报关单位'
  },
  {
    field: 'declarationTotalPrice',
    label: `报关总价`,
    formatter: formatMoneyColumn()
  },
  {
    field: 'outerboxVolume',
    label: `外箱体积${VolumeUnit}`,
    width: columnWidth.l
  },
  {
    field: 'totalVolume',
    label: `总体积${VolumeUnit}`,
    rules: { required: true, message: '请输入总体积' }
  },
  {
    field: 'outerboxGrossweight',
    label: ' 外箱毛重',
    slots: {
      default: (data) => {
        const { row } = data
        return row?.outerboxGrossweight?.weight
          ? `${row.outerboxGrossweight.weight} ${row.outerboxGrossweight.unit}`
          : '-'
      }
    }
  },
  {
    field: 'totalGrossweight',
    label: '总毛重',
    slots: {
      default: (data) => {
        const { row } = data
        return row?.totalGrossweight?.weight
          ? `${row.totalGrossweight.weight} ${row.totalGrossweight.unit}`
          : '-'
      }
    }
  },
  {
    field: 'outerboxNetweight',
    label: '外箱净重',
    slots: {
      default: (data) => {
        const { row } = data
        return row?.outerboxNetweight?.weight
          ? `${row.outerboxNetweight.weight} ${row.outerboxNetweight.unit}`
          : '-'
      }
    }
  },
  {
    field: 'totalNetweight',
    label: '总净重 ',
    slots: {
      default: (data) => {
        const { row } = data
        return row?.totalNetweight?.weight
          ? `${row.totalNetweight.weight} ${row.totalNetweight.unit}`
          : '-'
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

const selectedList = ref([])
const MigrateDiaRef = ref()
const handleRemove = async () => {
  if (!isValidArray(selectedList.value)) {
    message.error('请选择要移除的记录')
    return
  }
  await message.confirm('确定要移除吗？')
  await ShipmentApi.deleteShipmentItem({
    ids: selectedList.value.map((item) => item.id).join(',')
  })
  message.success('移除成功')
  await handleRefresh()
}
const handleMigrate = () => {
  if (!isValidArray(selectedList.value)) {
    message.error('请选择要迁移的记录')
    return
  }
  MigrateDiaRef.value.open(selectedList.value.map((item) => item.id).join(','), pageInfo.value?.id)
}
const handleSelectionChange = (val) => {
  selectedList.value = val
}

const computedPageInfo = async () => {
  pageInfo.value?.children.forEach((cItem: any) => {
    //采购合计
    cItem.purchaseTotalAmount = { amount: 0, currency: '' }
    cItem.purchaseTotalAmount.amount =
      Number(cItem.purchaseWithTaxPrice.amount) * Number(cItem.thisPurchaseQuantity)
    cItem.purchaseTotalAmount.currency = cItem.purchaseWithTaxPrice.currency
  })
  if (pageInfo.value?.children?.length) {
    pageInfo.value.purchaseTotalAmount = getTotalAmount(
      pageInfo.value?.children,
      'purchaseTotalAmount'
    )
  }
}

const RelateTableRef = ref()
const handleRefresh = async () => {
  clearDialogActions()
  await getPageInfo()
  RelateTableRef.value.init()
}

const InspectDiaRef = ref()
const toInspect = () => {
  InspectDiaRef.value?.open(pageInfo.value?.children)
}
const DeclarationDiaRef = ref()
const toDeclaration = () => {
  DeclarationDiaRef.value?.open(pageInfo.value?.children)
}
const SettlementFormDiaRef = ref()
const toSettlementForm = () => {
  SettlementFormDiaRef.value?.open(pageInfo.value)
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
      sums[index] = `${formatNum(weightVal)}`
    } else {
      sums[index] = ''
    }
  })

  return sums
}
const tableMaxHeight = ref(0)

onMounted(async () => {
  if (useRoute().params.id) {
    pageId.value = useRoute().params.id
    pageFlag.value = true
  } else {
    pageFlag.value = false
    if (!props.id && !query.id) {
      message.error('ID不能为空')
      if (!props.id) {
        close()
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
  setTimeout(() => {
    tableMaxHeight.value = eplusDetailRef.value.bottomHeight - 54
  }, 1000)
})
</script>
<style lang="scss">
.break-word {
  white-space: pre-wrap;
}
</style>
