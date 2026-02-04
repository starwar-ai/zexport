<template>
  <div class="tabs_box">
    <el-tabs
      v-model="activeName"
      class="demo-tabs"
      @tab-change="handleTabsClick"
    >
      <el-tab-pane
        label="全部"
        :name="0"
      />
      <el-tab-pane :name="99">
        <template #label>
          <el-badge
            :value="changeCount"
            :hidden="changeCount <= 0"
          >
            <span>上游变更</span>
          </el-badge>
        </template>
      </el-tab-pane>
      <el-tab-pane
        label="草稿"
        :name="1"
      />
      <el-tab-pane
        label="执行中"
        :name="2"
      />
      <el-tab-pane
        label="已出运"
        :name="4"
      />
      <el-tab-pane
        label="已交单"
        :name="5"
      />
      <el-tab-pane
        label="已作废"
        :name="6"
      />
    </el-tabs>
  </div>
  <eplus-table
    :eplusSearchSchema="eplusSearchSchema"
    :eplusTableSchema="eplusTableSchema"
    ref="eplusListRef"
    :key="activeName"
    :exportFileName="exportFileName"
    @emit-open-detail="handleDetail"
  >
    <template #tableIsTreeActions>
      <el-button
        @click="batchToSettlementForm"
        v-hasPermi="['dms:shipment:to-settlement-form']"
      >
        批量转结汇单
      </el-button>
      <el-button
        @click="batchToDeclaration"
        v-hasPermi="['dms:shipment:to-declaration']"
      >
        批量转报关单
      </el-button>
    </template>
    <template #tableActions>
      <el-button
        v-if="activeName == 2"
        @click="handleToNotice()"
      >
        转拉柜通知单
      </el-button>
    </template>
  </eplus-table>
  <eplus-dialog
    ref="eplusDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
  >
    <template #detail="{ key }">
      <ShippingDetail :id="key" />
    </template>
    <template #edit="{ key, param }">
      <ShippingForm
        :type="param"
        :id="key"
        mode="edit"
      />
    </template>
    <template #create>
      <ShippingForm mode="create" />
    </template>
    <template #confirm="{ key }">
      <ExportSaleContractConfirm
        :id="key"
        mode="confirm"
        :row="confirmRow"
        :type="3"
        @handle-success="handleRefresh"
        @success="backChange"
      />
    </template>
  </eplus-dialog>

  <eplus-dialog ref="skuDialogRef">
    <template #detail="{ key }">
      <CustProductDetail :id="key" />
    </template>
  </eplus-dialog>

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

  <ForwardDialogs
    ref="ContainerNoticeRef"
    @sure="handleRefresh"
  />
  <ToOrderNotice
    ref="ToOrderNoticeRef"
    @success="handleRefresh"
  />

  <ExportDia
    ref="ExportDiaRef"
    @success="handleRefresh"
  />

  <ShipmentDia
    ref="ShipmentDiaRef"
    @success="handleRefresh"
  />

  <SplitDia
    ref="SplitDiaRef"
    @success="handleRefresh"
  />
</template>

<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusDialog } from '@/components/EplusDialog'
import {
  columnWidth,
  formatDateColumn,
  formatDictColumn,
  formatMoneyColumn,
  formatNumColumn
} from '@/utils/table'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import * as ShipmentApi from '@/api/dms/shipment/index'
import * as VenderApi from '@/api/common/index'
import { getcompanySimpleList } from '@/api/common/index'
import { EplusSearchSchema } from '@/components/EplusSearch/types'
import ShippingDetail from './ShippingDetail.vue'
import ShippingForm from './ShippingForm.vue'
import InspectDia from './components/InspectDia.vue'
import DeclarationDia from './components/DeclarationDia.vue'
import SettlementFormDia from './components/SettlementFormDia.vue'
import ForwardDialogs from './components/ForwardDialogs.vue'
import ExportSaleContractConfirm from '@/views/sms/sale/exportSaleContract/ExportSaleContractConfirm.vue'
import { ShipmentDetailStatusEnum } from '@/utils/constants'
import ToOrderNotice from './components/ToOrderNotice.vue'
import ExportDia from './components/ExportDia.vue'
import CustProductDetail from '@/views/pms/product/cust/custProductDetail.vue'
import { isValidArray } from '@/utils/is'
import * as UserApi from '@/api/system/user'
import ShipmentDia from './components/ShipmentDia.vue'
import SplitDia from './components/SplitDia.vue'
import download from '@/utils/download'
import { currencyJsonAnalysis } from '@/utils'
import { getSourceId, removeSourceId, setSourceId } from '@/utils/auth'
import { checkPermi } from '@/utils/permission.js'
import { useAppStore } from '@/store/modules/app'

const eplusDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const skuDialogRef = ref<InstanceType<typeof EplusDialog> | undefined>(undefined)
const ToOrderNoticeRef = ref()
const message = useMessage()

const exportFileName = computed(() => {
  if (checkPermi(['dms:shipment:export'])) {
    return '出运明细.xlsx'
  } else {
    return ''
  }
})
const eplusListRef = ref()

defineOptions({ name: 'Shipping' })
const activeName = ref(0)
const handleTabsClick = () => {
  eplusListRef.value.handleSearch()
}
const confirmRow = ref({})

// 供应商名称
const venderNameStr = ref('')
const getVenderName = (e) => {
  if (e && e[1]) {
    e[1].forEach((item) => {
      if (item.code === e[0]) {
        venderNameStr.value = item.name
      }
    })
  }
}

const eplusSearchSchema: EplusSearchSchema = {
  fields: [
    {
      component: <el-input clearable></el-input>,
      name: 'invoiceCode',
      label: '发票号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'saleContractCode',
      label: '外销合同号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'purchaseContractCode',
      label: '采购合同号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'custName',
      label: '客户名称'
    },
    {
      component: (
        <eplus-input-search-select
          api={VenderApi.getVenderSimpleList}
          params={{ pageSize: 100, pageNo: 1, venderType: 1, skuQuoteFlag: 1 }}
          keyword="name"
          label="name"
          value="code"
          class="!w-100%"
          placeholder="请选择供应商"
          onChangeEmit={(...$event) => getVenderName($event)}
        />
      ),
      name: 'venderCode',
      label: '供应商名称',
      formatter: async (args: any[]) => {
        return venderNameStr.value
      }
    },
    {
      component: <el-input clearable></el-input>,
      name: 'custPo',
      label: '客户PO'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'cskuCode',
      label: '客户货号'
    },
    {
      name: 'foreignTradeCompanyId',
      label: '报关公司',
      component: (
        <eplus-custom-select
          api={getcompanySimpleList}
          label="name"
          value="id"
          placeholder="请选择"
          style="width:100%"
        />
      )
    },
    {
      component: <el-input></el-input>,
      name: 'code',
      label: '出运单号'
    },
    {
      component: <el-input></el-input>,
      name: 'shipmentPlanCode',
      label: '计划单号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'skuCode',
      label: '产品编号'
    },
    {
      component: <el-input clearable></el-input>,
      name: 'basicSkuCode',
      label: '基础产品编号'
    },
    {
      component: <eplus-user-select></eplus-user-select>,
      name: 'inputUserId',
      label: '录入人',
      formatter: async (args: any[]) => {
        const user = await UserApi.getSimpleUser(args[0])
        return user.nickname
      }
    }
  ],
  moreFields: []
}
const router = useRouter()
const openCustSkuDetail = (id) => {
  setSourceId(id)
  if (checkPermi(['pms:csku:query']) && checkPermi(['pms:csku:detail'])) {
    router.push({ path: '/base/product-manage/csku' })
  } else {
    message.error('暂无权限查看详情')
  }
}

const changeCount = ref(0)

const eplusTableSchema = {
  getListApi: async (ps) => {
    const res = await ShipmentApi.getShipmentPage({
      ...ps,
      status: activeName.value == 0 || activeName.value == 99 ? '' : activeName.value,
      confirmFlag: activeName.value == 99 ? 0 : ''
    })
    changeCount.value = res?.summary?.changeCount || 0
    return {
      list:
        res.list.map((item) => {
          // 为每个子项计算 purchaseTotalAmount（用于已选合计）
          if (item.children && Array.isArray(item.children)) {
            item.children = item.children.map((child) => {
              if (child.purchaseWithTaxPrice?.amount && child.thisPurchaseQuantity) {
                child.purchaseTotalAmount = {
                  amount: child.purchaseWithTaxPrice.amount * child.thisPurchaseQuantity,
                  currency: child.purchaseWithTaxPrice.currency
                }
              }
              return child
            })
          }

          return {
            ...item,
            parentSaleContractCode: item.saleContractCode
          }
        }) || [],
      total: res.total || 0,
      sum: {
        declarationAmount: currencyJsonAnalysis(res?.summary?.declarationAmount),
        purchaseTotalAmount: currencyJsonAnalysis(res?.summary?.purchaseTotalAmount)
      }
    }
  },
  exportListApi: async (ps) => {
    return await ShipmentApi.exportShipment(ps)
  },
  showTabs: true,
  setRowStyle: (data) => {
    if (data.row?.batchFlag === 1) {
      return { color: '#409EFF' }
    }
    if (data.row?.parent?.batchFlag === 1) {
      return { color: '#409EFF' }
    }
  },
  tabs: [
    {
      label: '产品',
      selection: true,
      isTree: true,
      summary: true,
      columns: [
        {
          prop: 'code',
          label: '出运单号',
          minWidth: columnWidth.m,
          parent: true,
          isHyperlink: true
        },
        {
          prop: 'shipmentPlanCode',
          label: '计划单号',
          minWidth: columnWidth.m,
          parent: true
        },
        {
          prop: 'invoiceCode',
          label: '出运发票号',
          minWidth: columnWidth.m,
          parent: true
        },
        {
          prop: 'foreignTradeCompanyName',
          label: '报关公司',
          parent: true,
          minWidth: columnWidth.m
        },
        {
          prop: 'custDeliveryDate',
          label: '客户交期',
          minWidth: columnWidth.m,
          formatter: formatDateColumn(),
          parent: true
        },
        {
          prop: 'departurePortName',
          label: '出运口岸',
          minWidth: columnWidth.m,
          parent: true
        },
        {
          prop: 'destinationPortName',
          label: '目的口岸',
          minWidth: columnWidth.m,
          parent: true
        },
        {
          prop: 'transportType',
          label: '运输方式',
          minWidth: columnWidth.m,
          parent: true,
          formatter: formatDictColumn(DICT_TYPE.TRANSPORT_TYPE)
        },
        {
          prop: 'inputUser',
          label: '录入人',
          minWidth: columnWidth.m,
          parent: true,
          slots: {
            default: (row) => {
              return `${row?.inputUser?.nickname}`
            }
          }
        },
        {
          prop: 'inputDate',
          label: '录入日期',
          minWidth: columnWidth.m,
          parent: true,
          formatter: formatDateColumn()
        },
        {
          prop: 'status',
          label: '状态',
          minWidth: columnWidth.m,
          parent: true,
          formatter: formatDictColumn(DICT_TYPE.SHIPPING_STATUS)
        },
        {
          prop: 'confirmFlag',
          label: '确认状态',
          parent: true,
          minWidth: columnWidth.l,
          // formatter: formatDictColumn(DICT_TYPE.RECEIPT_STATUS)
          formatter: (row) => {
            return getDictLabel(DICT_TYPE.RECEIPT_STATUS, row?.confirmFlag)
          }
        },
        {
          prop: 'saleContractCode',
          label: '外销合同号',
          minWidth: columnWidth.m
        },
        {
          prop: 'companyPath',
          label: '下单主体',
          minWidth: columnWidth.m,
          formatter: (row) => {
            return row.companyPath?.name || '-'
          }
        },

        {
          prop: 'custName',
          label: '客户名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'cskuCode',
          label: '客户货号',
          minWidth: columnWidth.m
        },
        {
          prop: 'basicSkuCode',
          label: '基础产品编号',
          minWidth: columnWidth.m
        },
        {
          prop: 'name',
          label: '产品名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'skuCode',
          label: '产品编号',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              return (
                <span
                  onClick={() => openCustSkuDetail(row.skuId)}
                  style="color:rgb(0, 91, 245);cursor:pointer;"
                >
                  {row.skuCode}
                </span>
              )
            }
          }
        },
        {
          prop: 'declarationName',
          label: '报关中文品名',
          minWidth: columnWidth.m
        },
        {
          prop: 'shippingQuantity',
          label: '出货数量',
          minWidth: columnWidth.m,
          formatter: formatNumColumn()
        },
        {
          prop: 'hsMeasureUnit',
          label: '海关计量单位',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.HS_MEASURE_UNIT)
        },
        {
          prop: 'deliveryDate',
          label: '交货日期',
          minWidth: columnWidth.m,
          formatter: formatDateColumn('YYYY-MM-DD')
        },
        {
          prop: 'hsCode',
          label: 'HS编码',
          minWidth: columnWidth.m
        },
        {
          prop: 'settlementName',
          label: '结汇方式',
          minWidth: columnWidth.m
        },
        {
          prop: 'invoiceStatus',
          label: '开票状态',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.INVOICE_STATUS)
        },
        {
          prop: 'declarationUnitPrice',
          label: '报关价',
          minWidth: columnWidth.m,
          formatter: formatMoneyColumn()
        },
        {
          prop: 'declarationAmount',
          label: '报关金额',
          minWidth: columnWidth.m,
          formatter: formatMoneyColumn(),
          summary: true,
          summarySlots: {
            default: (val) => {
              if (val && val.amount) {
                return { number: val.amount, unit: val.currency }
              }
              return { number: 0, unit: '' }
            }
          }
        },
        {
          prop: 'purchaseContractCode',
          label: '采购合同',
          minWidth: columnWidth.m
        },
        {
          prop: 'stockPurchaseContractCodes',
          label: '库存采购合同',
          minWidth: columnWidth.m,
          formatter: (row) => {
            if (row.stockPurchaseContractCodes && Array.isArray(row.stockPurchaseContractCodes)) {
              return row.stockPurchaseContractCodes.join(', ')
            }
            return ''
          }
        },
        {
          prop: 'thisPurchaseQuantity',
          label: '本次采购数量',
          minWidth: columnWidth.m,
          formatter: formatNumColumn()
        },
        {
          prop: 'thisUsedStockQuantity',
          label: '本次使用库存数',
          minWidth: columnWidth.m,
          formatter: (row) => {
            if (row.shippingQuantity && row.thisPurchaseQuantity) {
              const val = row.shippingQuantity - row.thisPurchaseQuantity
              return val > 0 ? val : 0
            } else {
              return row.shippingQuantity || 0
            }
          }
        },
        {
          prop: 'stockCost',
          label: '库存成本',
          minWidth: columnWidth.m,
          formatter: formatMoneyColumn()
        },
        {
          prop: 'purchaseWithTaxPrice',
          label: '采购含税单价',
          minWidth: columnWidth.m,
          formatter: formatMoneyColumn()
        },
        {
          prop: 'purchaseTotalAmount',
          label: '总采购金额',
          minWidth: columnWidth.m,
          formatter: (row) => {
            // 优先使用已预计算的 purchaseTotalAmount
            if (row.purchaseTotalAmount?.amount) {
              return h(EplusMoneyLabel, { val: row.purchaseTotalAmount })
            }
            return '-'
          },
          summary: true,
          summarySlots: {
            default: (val) => {
              if (val && val.amount) {
                return { number: val.amount, unit: val.currency }
              }
              return { number: 0, unit: '' }
            }
          }
        },
        {
          prop: 'taxRefundRate',
          label: '退税率',
          minWidth: columnWidth.m
        },
        {
          prop: 'venderName',
          label: '厂商名称',
          minWidth: columnWidth.m
        },
        {
          prop: 'purchaseUserName',
          label: '采购员',
          minWidth: columnWidth.m
        },
        {
          prop: 'sales',
          label: '销售员',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              return `${row?.sales?.nickname || ''}`
            }
          }
        },
        {
          prop: 'estDepartureTime',
          label: '出运日期',
          minWidth: columnWidth.m,
          parent: true,
          formatter: formatDateColumn()
        },
        {
          prop: 'action',
          label: '操作',
          fixed: 'right',
          minWidth: columnWidth.l,
          parent: true,
          slots: {
            default: (data) => {
              const { row } = data
              return (
                <div class="flex items-center justify-start">
                  <el-button
                    link
                    type="primary"
                    onClick={() => {
                      handleDetail(row.id)
                    }}
                    hasPermi="['dms:shipment:query']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    otherItems={[
                      {
                        isShow:
                          [
                            ShipmentDetailStatusEnum.WAITING_PROCESS.status,
                            ShipmentDetailStatusEnum.WAITING_SHIPMENT.status
                          ].includes(row?.status) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row?.changeStatus !== 2,
                        otherKey: 'batchFlag',
                        label: '拆分出运明细',
                        permi: 'dms:shipment:batch-flag',
                        handler: async (row) => {
                          updateBatchFlag(row, 'batch-flag')
                        }
                      },
                      {
                        isShow:
                          [
                            ShipmentDetailStatusEnum.WAITING_PROCESS.status,
                            ShipmentDetailStatusEnum.WAITING_SHIPMENT.status
                          ].includes(row?.status) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 1 &&
                          row?.changeStatus !== 2,
                        otherKey: 'split',
                        label: '分批出运',
                        permi: 'dms:shipment:cancel-batch-flag',
                        handler: async (row) => {
                          handleSplit(row)
                        }
                      },
                      {
                        isShow:
                          [
                            ShipmentDetailStatusEnum.WAITING_PROCESS.status,
                            ShipmentDetailStatusEnum.WAITING_SHIPMENT.status
                          ].includes(row?.status) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 1 &&
                          row?.changeStatus !== 2,
                        otherKey: 'cancelBatchFlag',
                        label: '取消分批出运',
                        permi: 'dms:shipment:cancel-batch-flag',
                        handler: async (row) => {
                          updateBatchFlag(row, 'cancel-batch-flag')
                        }
                      },
                      {
                        isShow:
                          [
                            ShipmentDetailStatusEnum.SHIPPED.status,
                            ShipmentDetailStatusEnum.COMPLETED.status
                          ].includes(row?.status) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row?.changeStatus !== 2,
                        otherKey: 'genContract',
                        label: '重新生成购销合同',
                        permi: 'dms:shipment:gen-contract',
                        handler: async (row) => {
                          handleGenContract(row)
                        }
                      },
                      {
                        isShow:
                          [ShipmentDetailStatusEnum.WAITING_PROCESS.status].includes(row?.status) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row.changeStatus != 2,
                        otherKey: 'dmsShipmentUpdate',
                        label: '编辑',
                        permi: 'dms:shipment:update',
                        handler: async (row) => {
                          handleUpdate(row?.id, 'edit')
                        }
                      },
                      /* {
                        isShow:
                          [ShipmentDetailStatusEnum.WAITING_PROCESS.status].includes(row?.status) &&
                          row?.confirmFlag !== 0 && row?.batchFlag === 0 &&
                          row.changeStatus != 2,
                        otherKey: 'dmsShipmentUpdate',
                        label: '提交',
                        permi: 'dms:shipment:update',
                        handler: async (row) => {
                          handleUpdate(row?.id, 'edit')
                        }
                      },*/
                      {
                        isShow:
                          row.status !== ShipmentDetailStatusEnum.WAITING_PROCESS.status &&
                          row.status != ShipmentDetailStatusEnum.FINISH.status &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row.changeStatus != 2 &&
                          toSettlementFormCheck(row),
                        otherKey: 'dmsShipmentToSettlementForm',
                        label: '转结汇单',
                        permi: 'dms:shipment:to-settlement-form',
                        handler: async (row) => {
                          toSettlementForm(row)
                        }
                      },
                      {
                        isShow:
                          row.status !== ShipmentDetailStatusEnum.WAITING_PROCESS.status &&
                          row.status != ShipmentDetailStatusEnum.FINISH.status &&
                          toInspectCheck(row) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row.changeStatus != 2,
                        otherKey: 'dmsShipmentToCommodityInspection',
                        permi: 'dms:shipment:to-commodity-inspection',
                        label: '转商检单',
                        handler: async (row) => {
                          toInspect(row)
                        }
                      },
                      {
                        isShow:
                          row.status !== ShipmentDetailStatusEnum.WAITING_PROCESS.status &&
                          row.status != ShipmentDetailStatusEnum.FINISH.status &&
                          toDeclarationCheck(row) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row.changeStatus != 2,
                        otherKey: 'dmsShipmentToDeclaration',
                        label: '转报关单',
                        permi: 'dms:shipment:to-declaration',
                        handler: async (row) => {
                          toDeclaration(row)
                        }
                      },
                      {
                        isShow:
                          row.status == ShipmentDetailStatusEnum.WAITING_SHIPMENT.status &&
                          toNoticeCheck(row) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row?.changeStatus != 2,
                        otherKey: 'containerTransportation',
                        permi: 'dms:container-transportation:query',
                        label: '转拉柜通知单',
                        handler: async (row) => {
                          let childrenIds = row?.children.map((item) => item.id)
                          toNotice(childrenIds)
                        }
                      },
                      {
                        isShow:
                          row.status == ShipmentDetailStatusEnum.WAITING_SHIPMENT.status &&
                          toNoticeCheck(row, 1) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row?.changeStatus != 2,
                        otherKey: 'factoryOutbound',
                        permi: 'dms:container-transportation-out:factory',
                        label: '工厂出库',
                        handler: async (row) => {
                          let childrenIds = row?.children.map((item) => item.id)
                          toNotice(childrenIds, 1)
                        }
                      },
                      {
                        isShow:
                          row.status < 4 &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row.changeStatus != 2,
                        otherKey: 'dmsShipmentClose',
                        label: '作废',
                        permi: 'dms:shipment:close',
                        handler: async (row) => {
                          handleClose({ parentId: row.id })
                        }
                      },
                      {
                        isShow:
                          row.status == ShipmentDetailStatusEnum.WAITING_SHIPMENT.status &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row.changeStatus != 2,
                        otherKey: 'dmsShipmentShipment',
                        label: '确认出运',
                        permi: 'dms:shipment:shipment',
                        handler: async (row) => {
                          handleShipment(row)
                        }
                      },
                      {
                        isShow:
                          (row.status == ShipmentDetailStatusEnum.WAITING_SHIPMENT.status ||
                            row.status == ShipmentDetailStatusEnum.SHIPPED.status) &&
                          row.changeStatus != 2 &&
                          row?.confirmFlag !== 0,
                        otherKey: 'dmsShipmentChange',
                        label: '变更',
                        permi: [
                          'dms:export-sale-contract-change-business:update',
                          'dms:export-sale-contract-change:update'
                        ],
                        handler: async (row) => {
                          handleUpdate(row?.id, 'change')
                        }
                      },
                      {
                        isShow:
                          row.status == ShipmentDetailStatusEnum.SHIPPED.status &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row.changeStatus != 2,
                        otherKey: 'dmsShipmentFinish',
                        permi: 'dms:shipment:finish',
                        label: '交单',
                        handler: async (row) => {
                          handleFinish(row?.id)
                        }
                      },
                      {
                        isShow:
                          [
                            ShipmentDetailStatusEnum.SHIPPED.status,
                            ShipmentDetailStatusEnum.WAITING_SHIPMENT.status
                          ].includes(row.status) &&
                          checkToNotice(row) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row.changeStatus != 2,
                        otherKey: 'dmsShipmentToInvoicNotice',
                        permi: 'dms:shipment:transform-invoicing-notices',
                        label: '转开票通知',
                        handler: async (row) => {
                          ToOrderNoticeRef.value.open(row)
                        }
                      },
                      {
                        isShow: row.confirmFlag == 0,
                        otherKey: 'confirm',
                        label: '确认变更',
                        permi: 'dms:shipment-change:confirm',
                        handler: async (row) => {
                          confirmRow.value = row
                          confirmRow.value.changeType =
                            row.status == ShipmentDetailStatusEnum.WAITING_SHIPMENT.status &&
                            row.changeStatus === 1
                              ? true
                              : false

                          eplusDialogRef.value?.openConfirm(row.id, '确认变更')
                        }
                      },
                      {
                        isShow: row.confirmFlag !== 0,
                        otherKey: 'dmsShipmentDetailExport',
                        label: '托单导出',
                        permi: 'dms:shipment:detail-export',
                        handler: async (row) => {
                          handleRowExport(row)
                        }
                      },
                      {
                        isShow: row.confirmFlag !== 0,
                        otherKey: 'dmsShipmentHsCodeExport',
                        label: 'hscode导出',
                        permi: 'dms:shipment:detail-export',
                        handler: async (row) => {
                          exportHsCode(row)
                        }
                      },
                      {
                        isShow: row.confirmFlag !== 0,
                        otherKey: 'dmsShipmentExport',
                        label: '明细导出',
                        permi: 'dms:shipment:detail-export',
                        handler: async (row) => {
                          exportDetail(row)
                        }
                      }
                    ]}
                    auditable={row}
                  ></eplus-dropdown>
                </div>
              )
            }
          }
        }
      ]
    },
    {
      label: '单据',
      selection: true,
      isDefault: true,
      columns: [
        {
          prop: 'code',
          label: '出运单号',
          minWidth: columnWidth.m,
          isHyperlink: true
        },
        {
          prop: 'invoiceCode',
          label: '发票号',
          minWidth: columnWidth.l
        },
        {
          prop: 'shipmentPlanCode',
          label: '出运计划单号',
          minWidth: columnWidth.l
        },
        {
          prop: 'parentSaleContractCode',
          label: '外销合同号',
          minWidth: columnWidth.l
        },
        {
          prop: 'custName',
          label: '客户名称',
          minWidth: columnWidth.l
        },
        {
          prop: 'custPo',
          label: '客户PO',
          minWidth: columnWidth.l,
          formatter: (row) => {
            return [...new Set(row.children.map((item) => item.custPo))].join(',')
          }
        },
        {
          prop: 'foreignTradeCompanyName',
          label: '报关公司',
          minWidth: columnWidth.m
        },
        {
          prop: 'custDeliveryDate',
          label: '客户交期',
          minWidth: columnWidth.l,
          formatter: formatDateColumn()
        },
        {
          prop: 'departurePortName',
          label: '出运口岸',
          minWidth: columnWidth.m
        },
        {
          prop: 'destinationPortName',
          label: '目的口岸',
          minWidth: columnWidth.m
        },
        {
          prop: 'transportType',
          label: '运输方式',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.TRANSPORT_TYPE)
        },
        {
          prop: 'inputUser',
          label: '录入人',
          minWidth: columnWidth.m,
          slots: {
            default: (data) => {
              const { row } = data
              return `${row?.inputUser?.nickname}`
            }
          }
        },
        {
          prop: 'inputDate',
          label: '录入日期',
          minWidth: columnWidth.m,
          formatter: formatDateColumn()
        },
        {
          prop: 'status',
          label: '状态',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.SHIPPING_STATUS)
        },
        {
          prop: 'outboundFlag',
          label: '已出库',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.IS_INT)
        },
        {
          prop: 'shipmentFlag',
          label: '已出运',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.IS_INT)
        },
        {
          prop: 'declarationFlag',
          label: '已转报关',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.IS_INT)
        },
        {
          prop: 'settleOrderFlag',
          label: '已转结汇',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.IS_INT)
        },
        {
          prop: 'invoiceNoticeFlag',
          label: '已转开票',
          minWidth: columnWidth.m,
          formatter: formatDictColumn(DICT_TYPE.IS_INT)
        },
        {
          prop: 'estDepartureTime',
          label: '出运日期',
          minWidth: columnWidth.m,
          formatter: formatDateColumn()
        },
        {
          prop: 'action',
          label: '操作',
          fixed: 'right',
          minWidth: columnWidth.l,
          slots: {
            default: (data) => {
              const { row } = data
              return (
                <div class="flex items-center justify-start">
                  <el-button
                    link
                    type="primary"
                    onClick={() => {
                      handleDetail(row.id)
                    }}
                    hasPermi="['dms:shipment:query']"
                  >
                    详情
                  </el-button>
                  <eplus-dropdown
                    otherItems={[
                      {
                        isShow:
                          [
                            ShipmentDetailStatusEnum.WAITING_PROCESS.status,
                            ShipmentDetailStatusEnum.WAITING_SHIPMENT.status
                          ].includes(row?.status) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row?.changeStatus !== 2,
                        otherKey: 'batchFlag',
                        label: '拆分出运明细',
                        permi: 'dms:shipment:batch-flag',
                        handler: async (row) => {
                          updateBatchFlag(row, 'batch-flag')
                        }
                      },
                      {
                        isShow:
                          [
                            ShipmentDetailStatusEnum.WAITING_PROCESS.status,
                            ShipmentDetailStatusEnum.WAITING_SHIPMENT.status
                          ].includes(row?.status) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 1 &&
                          row?.changeStatus !== 2,
                        otherKey: 'split',
                        label: '分批出运',
                        permi: 'dms:shipment:cancel-batch-flag',
                        handler: async (row) => {
                          handleSplit(row)
                        }
                      },
                      {
                        isShow:
                          [
                            ShipmentDetailStatusEnum.WAITING_PROCESS.status,
                            ShipmentDetailStatusEnum.WAITING_SHIPMENT.status
                          ].includes(row?.status) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 1 &&
                          row?.changeStatus !== 2,
                        otherKey: 'cancelBatchFlag',
                        label: '取消分批出运',
                        permi: 'dms:shipment:cancel-batch-flag',
                        handler: async (row) => {
                          updateBatchFlag(row, 'cancel-batch-flag')
                        }
                      },
                      {
                        isShow:
                          [
                            ShipmentDetailStatusEnum.SHIPPED.status,
                            ShipmentDetailStatusEnum.COMPLETED.status
                          ].includes(row?.status) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row?.changeStatus !== 2,
                        otherKey: 'genContract',
                        label: '重新生成购销合同',
                        permi: 'dms:shipment:gen-contract',
                        handler: async (row) => {
                          handleGenContract(row)
                        }
                      },
                      {
                        isShow:
                          [ShipmentDetailStatusEnum.WAITING_PROCESS.status].includes(row?.status) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row.changeStatus != 2,
                        otherKey: 'dmsShipmentUpdate',
                        label: '编辑',
                        permi: 'dms:shipment:update',
                        handler: async (row) => {
                          handleUpdate(row?.id, 'edit')
                        }
                      },
                      /*{
                        isShow:
                          [ShipmentDetailStatusEnum.WAITING_PROCESS.status].includes(row?.status) &&
                          row?.confirmFlag !== 0 && row?.batchFlag === 0 &&
                          row.changeStatus != 2,
                        otherKey: 'dmsShipmentUpdate',
                        label: '提交',
                        permi: 'dms:shipment:update',
                        handler: async (row) => {
                          handleUpdate(row?.id, 'edit')
                        }
                      },*/
                      {
                        isShow:
                          row.status == ShipmentDetailStatusEnum.WAITING_SHIPMENT.status &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row.changeStatus != 2 &&
                          toSettlementFormCheck(row),
                        otherKey: 'dmsShipmentToSettlementForm',
                        label: '转结汇单',
                        permi: 'dms:shipment:to-settlement-form',
                        handler: async (row) => {
                          toSettlementForm(row)
                        }
                      },
                      {
                        isShow:
                          row.status == ShipmentDetailStatusEnum.WAITING_SHIPMENT.status &&
                          toInspectCheck(row) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row.changeStatus != 2,
                        otherKey: 'dmsShipmentToCommodityInspection',
                        permi: 'dms:shipment:to-commodity-inspection',
                        label: '转商检单',
                        handler: async (row) => {
                          toInspect(row)
                        }
                      },
                      {
                        isShow:
                          row.status == ShipmentDetailStatusEnum.WAITING_SHIPMENT.status &&
                          toDeclarationCheck(row) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row.changeStatus != 2,
                        otherKey: 'dmsShipmentToDeclaration',
                        label: '转报关单',
                        permi: 'dms:shipment:to-declaration',
                        handler: async (row) => {
                          toDeclaration(row)
                        }
                      },
                      {
                        isShow:
                          row.status == ShipmentDetailStatusEnum.WAITING_SHIPMENT.status &&
                          toNoticeCheck(row) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row?.changeStatus != 2,
                        otherKey: 'containerTransportation',
                        permi: 'dms:container-transportation:query',
                        label: '转拉柜通知单',
                        handler: async (row) => {
                          let childrenIds = row?.children.map((item) => item.id)
                          toNotice(childrenIds)
                        }
                      },
                      {
                        isShow:
                          row.status == ShipmentDetailStatusEnum.WAITING_SHIPMENT.status &&
                          toNoticeCheck(row, 1) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row?.changeStatus != 2,
                        otherKey: 'factoryOutbound',
                        permi: 'dms:container-transportation-out:factory',
                        label: '工厂出库',
                        handler: async (row) => {
                          let childrenIds = row?.children.map((item) => item.id)
                          toNotice(childrenIds, 1)
                        }
                      },
                      {
                        isShow:
                          row.status < 4 &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row.changeStatus != 2,
                        otherKey: 'dmsShipmentClose',
                        label: '作废',
                        permi: 'dms:shipment:close',
                        handler: async (row) => {
                          handleClose({ parentId: row.id })
                        }
                      },
                      {
                        isShow:
                          row.status == ShipmentDetailStatusEnum.WAITING_SHIPMENT.status &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row.changeStatus != 2,
                        otherKey: 'dmsShipmentShipment',
                        label: '确认出运',
                        permi: 'dms:shipment:shipment',
                        handler: async (row) => {
                          handleShipment(row)
                        }
                      },
                      {
                        isShow:
                          (row.status == ShipmentDetailStatusEnum.WAITING_SHIPMENT.status ||
                            row.status == ShipmentDetailStatusEnum.SHIPPED.status) &&
                          row.changeStatus !== 2 &&
                          row?.confirmFlag !== 0,
                        otherKey: 'dmsShipmentChange',
                        label: '变更',
                        permi: 'dms:export-sale-contract-change-business:update',
                        handler: async (row) => {
                          handleUpdate(row?.id, 'change')
                        }
                      },
                      {
                        isShow:
                          row.status == ShipmentDetailStatusEnum.SHIPPED.status &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row.changeStatus != 2,
                        otherKey: 'dmsShipmentFinish',
                        permi: 'dms:shipment:finish',
                        label: '交单',
                        handler: async (row) => {
                          handleFinish(row?.id)
                        }
                      },
                      {
                        isShow:
                          [
                            ShipmentDetailStatusEnum.SHIPPED.status,
                            ShipmentDetailStatusEnum.WAITING_SHIPMENT.status
                          ].includes(row.status) &&
                          checkToNotice(row) &&
                          row?.confirmFlag !== 0 &&
                          row?.batchFlag === 0 &&
                          row.changeStatus != 2,
                        otherKey: 'dmsShipmentToInvoicNotice',
                        permi: 'dms:shipment:transform-invoicing-notices',
                        label: '转开票通知',
                        handler: async (row) => {
                          ToOrderNoticeRef.value.open(row)
                        }
                      },
                      {
                        isShow: row.confirmFlag == 0,
                        otherKey: 'confirm',
                        label: '确认变更',
                        permi: 'dms:shipment-change:confirm',
                        handler: async (row) => {
                          confirmRow.value = row
                          confirmRow.value.changeType =
                            row.status == ShipmentDetailStatusEnum.WAITING_SHIPMENT.status &&
                            row.changeStatus === 1
                              ? true
                              : false

                          eplusDialogRef.value?.openConfirm(row.id, '确认变更')
                        }
                      },
                      {
                        isShow: row.confirmFlag !== 0,
                        otherKey: 'dmsShipmentDetailExport',
                        label: '托单导出',
                        permi: 'dms:shipment:detail-export',
                        handler: async (row) => {
                          handleRowExport(row)
                        }
                      },
                      {
                        isShow: row.confirmFlag !== 0,
                        otherKey: 'dmsShipmentHsCodeExport',
                        label: 'hscode导出',
                        permi: 'dms:shipment:detail-export',
                        handler: async (row) => {
                          exportHsCode(row)
                        }
                      },
                      {
                        isShow: row.confirmFlag !== 0,
                        otherKey: 'dmsShipmentExport',
                        label: '明细导出',
                        permi: 'dms:shipment:detail-export',
                        handler: async (row) => {
                          exportDetail(row)
                        }
                      }
                    ]}
                    auditable={row}
                  ></eplus-dropdown>
                </div>
              )
            }
          }
        }
      ]
    }
  ]
}

const checkToNotice = (row) => {
  return row.children.some((item) => item.invoiceStatus === 0)
}

const uniqueArr = (arr) => {
  return Array.from(new Set(arr.map((item) => JSON.stringify(item)))).map((item) =>
    JSON.parse(item)
  )
}
const exportHsCode = async (row) => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    var custList = uniqueArr(
      row?.children.map((item) => {
        return {
          label: item.custName,
          value: item.custCode
        }
      })
    )
    const data = await ShipmentApi.exportShipmentDetail({
      id: row.id,
      reportCode: 'dms-shipment-hscode',
      exportType: 'hsCode',
      custCode: custList[0].value
    })
    if (data && data.size) {
      download.excel(data, `海关编码${row.code}.xlsx`)
    }
  } catch {}
  return
}
const exportDetail = async (row) => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    var custList = uniqueArr(
      row?.children.map((item) => {
        return {
          label: item.custName,
          value: item.custCode
        }
      })
    )
    const data = await ShipmentApi.exportShipmentDetail({
      id: row.id,
      reportCode: 'dms-shipment-detail',
      exportType: 'detail',
      custCode: custList[0].value
    })
    if (data && data.size) {
      download.excel(data, `明细${row.code}.xlsx`)
    }
  } catch {}
  return
}
const handleGenContract = async (row) => {
  await message.confirm('确认重新生成购销合同?')
  await ShipmentApi.genContract(row.id)
  message.success('操作成功')
}

const updateBatchFlag = async (row, type) => {
  let des = type === 'batch-flag' ? '确认拆分出运明细吗？' : '确认取消拆分出运明细吗？'
  await message.confirm(des)
  await ShipmentApi.updateBatchFlag(row.id, type)
  message.success('操作成功')
  handleRefresh()
}

const SplitDiaRef = ref()
const handleSplit = async (row) => {
  SplitDiaRef.value.open(row.children)
}

const toInspectCheck = (row) => {
  let tableList: any = []
  row.children.forEach((item: any) => {
    if (!item.isToCommodityInspection && item.commodityInspectionFlag === 1) {
      tableList.push(item)
    }
  })
  return isValidArray(tableList)
}

const toSettlementFormCheck = (row) => {
  let tableList: any = []
  row.children.forEach((item: any) => {
    if (!item.settlementQuantity) {
      tableList.push(item)
    }
  })
  return isValidArray(tableList)
}
const toDeclarationCheck = (row) => {
  let tableList: any = []
  row.children.forEach((item: any) => {
    if (item.declarationQuantity > item.declarationQuantityOld) {
      tableList.push(item)
    }
  })
  return isValidArray(tableList)
}

const toNoticeCheck = (row, type = 0) => {
  let tableList: any = []
  if (type === 1) {
    tableList = row.children.filter(
      (item) => item.converNoticeFlag === 0 && item.shippedAddress === 1
    )
  } else {
    tableList = row.children.filter((item) => item.converNoticeFlag === 0)
  }
  return isValidArray(tableList)
}

const ExportDiaRef = ref()
const handleRowExport = async (row) => {
  ExportDiaRef.value.open(row)
}

const handleToNotice = async () => {
  let list = eplusListRef.value?.checkedItems
  if (isValidArray(list)) {
    let ids = list.map((item) => {
      if (isValidArray(item.children)) {
        return item.children?.map((child) => child.id)
      } else {
        return item.id
      }
    })
    toNotice(ids)
  } else {
    ElMessage.error('请先选择需要转拉柜通知单产品')
    return
  }
}

// const handleExport = async () => {
//   return await eplusListRef.value.exportList('出运明细.xlsx')
// }
const appStore = useAppStore()
const handleDetail = (id) => {
  if (appStore.windowShowFlag) {
    eplusDialogRef.value?.openDetail(id)
  } else {
    router.push({
      path: `/dms/shipping-manage/shippingDetail/${id}`
    })
  }
}

const backChange = (data) => {
  handleUpdate(data.id, data.type)
}

const handleUpdate = (id, type) => {
  eplusDialogRef.value?.openEdit(id, type === 'change' ? '变更' : undefined, type)
}
// const handleCreate = () => {
//   eplusDialogRef.value?.openCreate()
// }
const InspectDiaRef = ref()
const toInspect = (row) => {
  InspectDiaRef.value?.open(row.children, 'Inspect')
}
const SettlementFormDiaRef = ref()
const ContainerNoticeRef = ref()
const toNotice = (row, factoryOutboundFlag = 0) => {
  ContainerNoticeRef.value?.open(row, factoryOutboundFlag)
}

const batchToSettlementForm = () => {
  let list = eplusListRef.value?.checkedItems.filter((item) => !item.settlementQuantity)
  if (isValidArray(list)) {
    SettlementFormDiaRef.value?.open(list)
  } else {
    ElMessage.error('请先选择需要转结汇单的数据')
    return
  }
}

const toSettlementForm = (row) => {
  SettlementFormDiaRef.value?.open(row)
}
const DeclarationDiaRef = ref()
const toDeclaration = (row) => {
  DeclarationDiaRef.value?.open(row.children)
}
const batchToDeclaration = () => {
  let list = eplusListRef.value?.checkedItems.filter(
    (item) => item.declarationQuantity > item.declarationQuantityOld
  )
  if (isValidArray(list)) {
    DeclarationDiaRef.value?.open(list)
  } else {
    ElMessage.error('请先选择需要转报关单的数据')
    return
  }
}

const handleClose = (row) => {
  ElMessageBox.confirm('确认对选中数据进行作废操作吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await ShipmentApi.shipmentClose(row)
    message.success('操作成功!')
    handleRefresh()
  })
}

const handleRollbackClose = (row) => {
  ElMessageBox.confirm('确认对选中数据进行反作废操作吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await ShipmentApi.shipmentRollbackClose(row)
    message.success('操作成功!')
    handleRefresh()
  })
}

const ShipmentDiaRef = ref()
const handleShipment = (row) => {
  ShipmentDiaRef.value.open(row)
  // ElMessageBox.confirm('出运单号是否已经出运，确认操作出运吗？', '提示', {
  //   confirmButtonText: '确定',
  //   cancelButtonText: '取消',
  //   type: 'warning'
  // }).then(async () => {
  //   await ShipmentApi.handleShipment({ id })
  //   message.success('操作成功!')
  //   handleRefresh()
  // })
}
const handleFinish = (id) => {
  ElMessageBox.confirm('出运单号的交接是否已达成，确认完毕意味着该单据流程结束。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await ShipmentApi.shipmentFinish({ id })
    message.success('操作成功!')
    handleRefresh()
  })
}
// const handleToInvoicNotice = (id) => {
//   ElMessageBox.confirm('确认对选中数据进行转开票通知吗？', '提示', {
//     confirmButtonText: '确定',
//     cancelButtonText: '取消',
//     type: 'warning'
//   }).then(async () => {
//     await ShipmentApi.toInvoicNotice({ shipmentId: id })
//     message.success('操作成功!')
//     handleRefresh()
//     push('/scm/vender-payment/invoicingNotices')
//   })
// }
// const handleDelete = async (id: number) => {
//   await eplusListRef.value.delList(id, false)
// }

const handleRefresh = async () => {
  await eplusListRef.value.handleRefresh()
}

onActivated(() => {
  if (getSourceId()) {
    handleDetail(Number(getSourceId()))
    removeSourceId()
  }
})
</script>
<style>
.tabs_box {
  height: 50px;
  padding-left: 20px;
  padding-right: 20px;
}
</style>
