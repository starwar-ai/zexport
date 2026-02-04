<template>
  <eplus-detail
    v-if="pageInfo?.id"
    ref="eplusDetailRef"
    :auditable="pageInfo"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :approveApi="ShipmentApi.examineChangeApprove"
    :rejectApi="ShipmentApi.examineChangeReject"
    :cancel="{
      permi: ['dms:shipment-change:submit', 'dms:shipment-change:business'],
      handler: () => {}
    }"
    :approve="{
      permi: 'dms:shipment-change:audit',
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <el-row>
      <el-col :span="24">
        <eplus-description
          title="基础信息"
          :data="pageInfo"
          :items="basicInfo"
          oldChangeField="oldData"
        />
        <el-tabs v-model="activeTab">
          <el-tab-pane
            label="出货信息"
            name="1"
          >
            <!-- <eplus-description
              v-for="item in pageInfo?.shipmentCustList"
              :key="item.custId"
              title=""
              :data="item"
              :items="shipmentSchema"
              oldChangeField="oldData"
            /> -->
            <eplus-description
              title=""
              :data="pageInfo"
              :items="shipmentSchema"
              oldChangeField="oldData"
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
              oldChangeField="oldData"
            >
              <!-- <template #bulkHandlingVolume>
                <EplusFieldComparison
                  v-if="pageInfo?.bulkHandlingVolume"
                  filed="bulkHandlingVolume"
                  oldChangeField="oldData"
                  :formatter="
                    (val) => {
                      return val ? val / 1000000 : '-'
                    }
                  "
                  :item="pageInfo"
                />
              </template> -->
            </eplus-description>
          </el-tab-pane>
          <el-tab-pane
            label="船信息"
            name="3"
          >
            <eplus-description
              title=""
              :data="pageInfo"
              :items="shipSchema"
              oldChangeField="oldData"
            >
              <template #estDepartureTime>
                <EplusFieldComparison
                  v-if="pageInfo?.estDepartureTime"
                  filed="estDepartureTime"
                  oldChangeField="oldData"
                  :formatter="
                    (val) => {
                      return formatDate(val, 'YYYY-MM-DD HH:mm:ss')
                    }
                  "
                  :item="pageInfo"
                />
              </template>
              <template #estClosingTime>
                <EplusFieldComparison
                  v-if="pageInfo?.estClosingTime"
                  filed="estClosingTime"
                  oldChangeField="oldData"
                  :formatter="
                    (val) => {
                      return formatDate(val, 'YYYY-MM-DD HH:mm:ss')
                    }
                  "
                  :item="pageInfo"
                />
              </template>
              <template #estClearanceTime>
                <EplusFieldComparison
                  v-if="pageInfo?.estClearanceTime"
                  filed="estClearanceTime"
                  oldChangeField="oldData"
                  :formatter="
                    (val) => {
                      return formatDate(val, 'YYYY-MM-DD HH:mm:ss')
                    }
                  "
                  :item="pageInfo"
                />
              </template>
            </eplus-description>
          </el-tab-pane>
          <el-tab-pane
            label="合计信息"
            name="4"
          >
            <eplus-description
              title=""
              :data="pageInfo"
              :items="totalSchema"
              oldChangeField="oldData"
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
        label="销售合同明细"
        name="1"
      >
        <Table
          :columns="tableColumns"
          headerAlign="center"
          align="center"
          :data="pageInfo?.children"
        />
      </el-tab-pane>
      <el-tab-pane
        label="临时产品"
        name="2"
      >
        <Table
          v-if="pageInfo?.temporarySkuList?.length > 0"
          :columns="skuColumns"
          headerAlign="center"
          align="center"
          :data="pageInfo?.temporarySkuList"
        />
        <span v-else>暂无数据</span>
      </el-tab-pane>
      <el-tab-pane
        label="加减项"
        name="3"
      >
        <Table
          v-if="pageInfo?.addSubItemList?.length > 0"
          :columns="subColumns"
          headerAlign="center"
          align="center"
          :data="pageInfo?.addSubItemList"
        />
        <span v-else>暂无数据</span>
      </el-tab-pane>
      <el-tab-pane
        label="单证费用"
        name="4"
      >
        <ForwarderFeeDetail
          :info="pageInfo"
          readonly
        />
      </el-tab-pane>
    </el-tabs>
  </eplus-detail>
</template>
<script setup lang="tsx">
import * as ShipmentApi from '@/api/dms/shipment/index'
import { EplusDetail } from '@/components/EplusTemplate'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { EplusDialog } from '@/components/EplusDialog'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { volPrecision, VolumeUnit } from '@/utils/config'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import { formatDate } from '@/utils/formatTime'
import ForwarderFeeDetail from '../shipping/components/ForwarderFeeDetail.vue'
import { formatNum, formatTime } from '@/utils'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { getOuterbox, getOuterboxVal } from '@/utils/outboxSpec'
import { columnWidth } from '@/utils/table'

const message = useMessage()
const pageInfo: any = ref({})

const eplusDetailRef = ref<ComponentRef<typeof EplusDetail>>()
const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const props = defineProps<{
  id?: number
  eplusDialogRef?: ComponentRef<typeof EplusDialog>
}>()
defineOptions({ name: 'ShippingDetail' })
//定义edit事件
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

// const { updateDialogActions, clearDialogActions } = inject('dialogActions') as {
//   updateDialogActions: (...args: any[]) => void
//   clearDialogActions: () => void
// }
const loading = ref(true)
const { query } = useRoute()
const activeName = ref('1')
const activeTab = ref('1')
const getPageInfo = async () => {
  loading.value = true
  try {
    pageInfo.value = props.id
      ? await ShipmentApi.getChangeDetail({ id: props.id })
      : await ShipmentApi.getAuditChangeDetail({ id: query?.id })

    pageInfo.value.children.forEach((item2) => {
      item2.purchaseTotalAmount = {
        amount: item2.purchaseTotalQuantity * item2.purchaseWithTaxPrice.amount,
        currency: item2.purchaseWithTaxPrice.currency
      }
      item2.oldChildren = pageInfo.value.oldData.children
        .filter((item3) => item3?.id === item2?.id)
        .map((el) => {
          return {
            ...el,
            purchaseTotalAmount: {
              amount: el.purchaseTotalQuantity * el.purchaseWithTaxPrice.amount,
              currency: el.purchaseWithTaxPrice.currency
            }
          }
        })
      return item2
    })
    pageInfo.value.addSubItemList.forEach((item2) => {
      item2.oldChildren = pageInfo.value.oldData.addSubItemList.filter(
        (item3) => item3?.id === item2?.id
      )
      return item2
    })
    pageInfo.value.temporarySkuList.forEach((item2) => {
      item2.oldChildren = pageInfo.value.oldData.temporarySkuList.filter(
        (item3) => item3?.id === item2?.id
      )
      return item2
    })
    setOuterbox(pageInfo.value.children, pageInfo.value.oldData.children)
  } catch {
    pageInfo.value = {}
  } finally {
    loading.value = false
  }
}

const setOuterbox = (newData, oldData) => {
  newData.forEach((e, i) => {
    e.changeFlag = false
    // 新的外箱规格
    e.outerbox = getOuterbox(e, 'spec')
    e.outerboxVolume = getOuterboxVal(e, 'vol')
    e.outerboxGrossweight = getOuterbox(e, 'grossweight')
    e.outerboxNetweight = getOuterbox(e, 'netweight')

    // totalVolume

    oldData.forEach((item, index) => {
      // 旧的外箱规格
      item.outerbox = getOuterbox(item, 'spec')
      item.outerboxVolume = getOuterboxVal(item, 'vol')
      item.outerboxGrossweight = getOuterbox(item, 'grossweight')
      item.outerboxNetweight = getOuterbox(item, 'netweight')
      if (i == index) {
        e.oldData = item
      }
    })
  })
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
    field: 'companyName',
    label: '归属公司'
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
    field: 'estShipDate',
    label: '客户交期',
    isCompare: true,
    formatter: (val) => {
      return formatTime(val, 'yyyy-MM-dd')
    }
  },
  {
    field: 'custName',
    label: '客户名称'
  },
  {
    field: 'saleContractCode',
    label: '外销合同号'
  },
  {
    field: 'custPo',
    label: '客户PO号'
  },
  {
    field: 'settlementTermType',
    label: '价格条款',
    isCompare: true
  },
  {
    field: 'tradeCountryName',
    label: '贸易国别',
    isCompare: true
  },
  {
    field: 'destinationPortName',
    label: '目的口岸',
    isCompare: true
  },
  {
    field: 'departureCountryName',
    label: '出运国',
    isCompare: true
  },
  {
    field: 'departurePortName',
    label: '出运口岸',
    isCompare: true
  },
  {
    field: 'transportType',
    label: '运输方式',
    isCompare: true,
    formatter: (val) => {
      return getDictLabel(DICT_TYPE.TRANSPORT_TYPE, val)
    }
  },
  {
    field: 'documenter',
    label: '单证员',
    isCompare: true,
    formatter: (val) => {
      return val?.nickname
    }
  },
  {
    field: 'inboundDate',
    label: '实际拉柜/进仓日期',
    isCompare: true,
    formatter: (val) => {
      return formatTime(val, 'yyyy-MM-dd')
    }
  },
  {
    field: 'estDepartureTime',
    label: '实际开船日期',
    isCompare: true,
    formatter: (val) => {
      return formatTime(val, 'yyyy-MM-dd')
    }
  },
  {
    field: 'managerName',
    label: '业务员'
  },
  {
    field: 'inputUser',
    label: '录入人',
    formatter: (val) => {
      return val?.nickname
    }
  },
  {
    field: 'inputDate',
    label: '录入日期',
    type: 'date'
  },
  {
    field: 'status',
    label: '单据状态',
    dictType: DICT_TYPE.SHIPPING_PLAN_STATUS
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
    label: '船代公司',
    isCompare: true
  },
  {
    field: 'shipNum',
    label: '航名/船次',
    isCompare: true
  },
  {
    field: 'billLadingNum',
    label: '提单号',
    isCompare: true
  },
  {
    field: 'estDepartureDate',
    label: '实际开船日期',
    type: 'time'
  }
  // {
  //   field: 'estDepartureTime',
  //   label: '预计结港时间',
  //   slotName: 'estDepartureTime'
  // },
  // {
  //   field: 'estClearanceTime',
  //   label: '预计结关时间',
  //   slotName: 'estClearanceTime'
  // },
  // {
  //   field: 'estClosingTime',
  //   label: '预计结单时间',
  //   slotName: 'estClosingTime'
  // }
])

const cabinetSchema: EplusDescriptionItemSchema[] = [
  {
    field: 'twentyFootCabinetNum',
    label: '20尺柜',
    isCompare: true,
    formatter: (val) => {
      return val ? formatNum(val) : '0'
    }
  },
  {
    field: 'fortyFootCabinetNum',
    label: '40尺柜',
    isCompare: true,
    formatter: (val) => {
      return val ? formatNum(val) : '0'
    }
  },
  {
    field: 'fortyFootContainerNum',
    label: '40尺高柜',
    isCompare: true,
    formatter: (val) => {
      return val ? formatNum(val) : '0'
    }
  },
  {
    field: 'bulkHandlingVolume',
    label: `散货`,
    isCompare: true,
    formatter: (val) => {
      return val ? `${formatNum(val / 1000000, 6)} ${VolumeUnit}` : '-'
    }
  }
]
const shipmentSchema = [
  {
    field: 'receivePerson',
    label: '收货人',
    isCompare: true
  },
  {
    field: 'notifyPerson',
    label: '通知人',
    isCompare: true
  },
  {
    field: 'frontShippingMark',
    label: '正面唛头',
    isCompare: true
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
    isCompare: true
  },
  {
    field: 'totalWeight',
    label: '净重合计',
    isCompare: true
  },
  {
    field: 'totalVolume',
    label: '体积合计',
    isCompare: true,
    formatter: (val) => {
      return val ? `${formatNum(val / 1000000, volPrecision)} ${VolumeUnit}` : '-'
    }
  },
  {
    field: 'totalDeclaration',
    label: '报关合计',
    type: 'money'
  },
  {
    field: 'totalPurchase',
    label: '采购合计',
    type: 'money'
  }
]

const tableColumns = [
  {
    field: 'billStatus',
    label: '入库状态',
    width: '100',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="billStatus"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.BILL_STATUS, val) || '-'
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'shippedAddress',
    label: '发货地点',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="shippedAddress"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.SHIPPED_ADDRESS, val) || '-'
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'outboundFlag',
    label: '是否出库',
    width: '100',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="outboundFlag"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.IS_INT, val) || '-'
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'saleContractCode',
    label: '外销合同',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="saleContractCode"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="cskuCode"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="basicSkuCode"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'custPo',
    label: '客户PO号',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="custPo"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'skuCode',
    label: '产品编号',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="skuCode"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'nameEng',
    label: '英文品名',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="nameEng"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'name',
    label: '中文品名',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="name"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'shippingQuantity',
    label: '出运数量',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="shippingQuantity"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },

  {
    field: 'saleUnitPrice',
    label: '销售单价',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="saleUnitPrice"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'saleAmount',
    label: '销售金额',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="saleAmount"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'declarationUnitPrice',
    label: '报关单价',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="declarationUnitPrice"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'declarationAmount',
    label: '报关金额',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="declarationAmount"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'estPickupTime',
    label: '预计拉柜日期',
    width: '200',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="estPickupTime"
            oldChangeField="oldData"
            formatter={(val) => {
              return formatDate(val, 'YYYY-MM-DD')
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'commodityInspectionFlag',
    label: '是否商检',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="commodityInspectionFlag"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.IS_INT, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'commodityInspectionType',
    label: '商检负责方',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="commodityInspectionType"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.COMMODITY_INSPECTION_TYPE, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'hsCode',
    label: 'HS编码',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="hsCode"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'customsDeclarationNameEng',
    label: '报关英文名',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="customsDeclarationNameEng"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'declarationName',
    label: '报关中文名',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="declarationName"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'hsMeasureUnit',
    label: '海关计量单位',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="hsMeasureUnit"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.HS_MEASURE_UNIT, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'taxRefundRate',
    label: '退税率%',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="taxRefundRate"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'taxRefundPrice',
    label: '退税金额',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="taxRefundPrice"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'custName',
    label: '客户名称',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="custName"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'manager',
    label: '跟单员',
    width: '100',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="custName"
            oldChangeField="oldData"
            formatter={(val) => {
              return val?.nickname || '-'
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'declarationQuantity',
    label: '报关数量',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="declarationQuantity"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'stockQuantity',
    label: '锁库数量',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="stockQuantity"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="purchaseContractCode"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'checkStatus',
    label: '验货结果',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="checkStatus"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.INSPECTION_STATUS, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'thisPurchaseQuantity',
    label: '本次采购数量',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="thisPurchaseQuantity"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'thisUsedStockQuantity',
    label: '本次使用库存数',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="thisUsedStockQuantity"
            oldChangeField="oldData"
            formatter={(val) => {
              return row.shippingQuantity - row.thisPurchaseQuantity
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'stockCost',
    label: '库存成本',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="stockCost"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'purchaseUserName',
    label: '采购员',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="purchaseUserName"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },

  {
    field: 'purchaseWithTaxPrice',
    label: '采购含税单价',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="purchaseWithTaxPrice"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'purchaseTotalQuantity',
    label: '总采购数量',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="purchaseTotalQuantity"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'purchaseTotalAmount',
    label: '采购总金额',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="purchaseTotalAmount"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'packageTypeName',
    label: '包装方式',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="packageTypeName"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'qtyPerInnerbox',
    label: '内盒装量',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="qtyPerInnerbox"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'qtyPerOuterbox',
    label: '外箱装量',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="qtyPerOuterbox"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
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
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="boxCount"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="outerbox"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'outerboxVolume',
    label: `外箱体积${VolumeUnit}`,
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="outerboxVolume"
            oldChangeField="oldData"
            formatter={(val) => {
              return val ? formatNum(val / 1000000, volPrecision) : ''
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'totalVolume',
    label: `总体积${VolumeUnit}`,
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="totalVolume"
            oldChangeField="oldData"
            formatter={(val) => {
              return val ? formatNum(val / 1000000, volPrecision) : ''
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'outerboxGrossweight',
    label: '外箱毛重',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="outerboxGrossweight"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'totalGrossweight',
    label: '总毛重',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="totalGrossweight"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'outerboxNetweight',
    label: '外箱净重',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="outerboxNetweight"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'totalNetweight',
    label: '总净重',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="totalNetweight"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'description',
    label: '中文说明',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="description"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'descriptionEng',
    label: '英文说明',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="descriptionEng"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'remark',
    label: '备注',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="remark"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'commissionAmount',
    label: '佣金金额',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="commissionAmount"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'outDate',
    label: '最新出库日期',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="outDate"
            oldChangeField="oldData"
            formatter={(val) => {
              return formatDate(val, 'YYYY-MM-DD') || ''
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'declaredQuantity',
    label: '已报关数量',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="declaredQuantity"
            oldChangeField="oldData"
            formatter={(val) => {
              return formatNum(val) || ''
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'outQuantity',
    label: '已出库数量',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="outQuantity"
            oldChangeField="oldData"
            formatter={(val) => {
              return val ? formatNum(val) : '0'
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'settleOrderFlag',
    label: '是否转结汇单',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="settleOrderFlag"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.IS_INT, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'forwarderShareAmount',
    label: '单证费用均摊金额',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="forwarderShareAmount"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  }
]
const skuColumns = [
  {
    field: 'custName',
    label: '客户',
    width: '300',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="custName"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'hsCode',
    label: '海关编码',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="hsCode"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'declarationElement',
    label: '报关要素',
    width: '300',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="declarationElement"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'declarationName',
    label: '报关品名',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="declarationName"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'declarationNameEng',
    label: '报关英文名',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="declarationNameEng"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'expectCount',
    label: '出货数量',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="expectCount"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'hsMeasureUnit',
    label: '海关计量单位',
    width: columnWidth.l,
    // formatter: formatDictColumn(DICT_TYPE.HS_MEASURE_UNIT),
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="hsMeasureUnit"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.HS_MEASURE_UNIT, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'declarationCount',
    label: '报关数量',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="declarationCount"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'boxCount',
    label: '箱数',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="boxCount"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'declarationUnit',
    label: '报关单位',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="declarationUnit"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'declarationTotalPrice',
    label: `报关总价`,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="declarationTotalPrice"
            oldChangeField="oldData"
            formatter={(val) => {
              return val?.amount ? `${val.amount} ${val.currency}` : '-'
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'outerboxVolume',
    label: `外箱体积${VolumeUnit}`,
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="outerboxVolume"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'totalVolume',
    label: `总体积${VolumeUnit}`,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="totalVolume"
            oldChangeField="oldData"
            formatter={(val) => {
              return val ? val / 1000000 : ''
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'outerboxGrossweight',
    label: ' 外箱毛重',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="outerboxGrossweight"
            oldChangeField="oldData"
            formatter={(val) => {
              return val?.weight ? `${val.weight} ${val.unit}` : '-'
            }}
            item={row}
          />
        )
        // return row?.outerboxGrossweight?.weight
        //   ? `${row.outerboxGrossweight.weight} ${row.outerboxGrossweight.unit}`
        //   : '-'
      }
    }
  },
  {
    field: 'totalGrossweight',
    label: '总毛重',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="totalGrossweight"
            oldChangeField="oldData"
            formatter={(val) => {
              return val?.weight ? `${val.weight} ${val.unit}` : '-'
            }}
            item={row}
          />
        )
        // return row?.totalGrossweight?.weight
        //   ? `${row.totalGrossweight.weight} ${row.totalGrossweight.unit}`
        //   : '-'
      }
    }
  },
  {
    field: 'outerboxNetweight',
    label: '外箱净重',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="outerboxNetweight"
            oldChangeField="oldData"
            formatter={(val) => {
              return val?.weight ? `${val.weight} ${val.unit}` : '-'
            }}
            item={row}
          />
        )
        // return row?.outerboxNetweight?.weight
        //   ? `${row.outerboxNetweight.weight} ${row.outerboxNetweight.unit}`
        //   : '-'
      }
    }
  },
  {
    field: 'totalNetweight',
    label: '总净重 ',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="totalNetweight"
            oldChangeField="oldData"
            formatter={(val) => {
              return val?.weight ? `${val.weight} ${val.unit}` : '-'
            }}
            item={row}
          />
        )
        // return row?.totalNetweight?.weight
        //   ? `${row.totalNetweight.weight} ${row.totalNetweight.unit}`
        //   : '-'
      }
    }
  }
]
const subColumns = [
  {
    field: 'contractCode',
    label: '销售合同',
    width: '300',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="contractCode"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'calculationType',
    label: '加/减项',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="calculationType"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.CALCULATION_TYPE, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'feeName',
    label: '费用名称',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="contractCode"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'amount',
    label: '金额',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="amount"
            oldChangeField="oldData"
            formatter={(val) => {
              return val?.amount ? `${val.amount} ${val.currency}` : '-'
            }}
            item={row}
          />
        )
        // return row?.amount?.amount ? `${row.amount.amount} ${row.amount.currency}` : '-'
      }
    }
  }
]

const handleUpdate = async () => {
  if (!outDialogFlag.value) {
    await getPageInfo()
  }
}

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
})
</script>
<style lang="scss" scoped>
:deep(.oldTag) {
  text-decoration: line-through;
}
</style>
