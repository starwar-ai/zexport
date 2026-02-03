<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :approveApi="
      contractType === 1
        ? changeApprove
        : contractType === 2
          ? domExamineApprove
          : FactorySaleContractApi.changeApproveApi
    "
    :rejectApi="
      contractType === 1
        ? changeReject
        : contractType === 2
          ? domExamineReject
          : FactorySaleContractApi.changeRejectApi
    "
    :auditable="exportSaleContractDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: `${permiPrefix}:audit`,
      handler: () => {}
    }"
    :approve="{
      permi: `${permiPrefix}:audit`,
      handler: () => {}
    }"
    @update="handleUpdate"
  >
    <el-row>
      <el-col :span="24">
        <eplus-description
          title="基本信息"
          :data="exportSaleContractDetail"
          :items="exportSaleContractSchemas"
          oldChangeField="oldData"
        >
          <template #annex>
            <!-- <UploadList
              :fileList="exportSaleContractDetail.annex"
              disabled
            /> -->
            <span
              v-for="item in exportSaleContractDetail.annex"
              :key="item?.id"
              class="mb5px mr5px"
            >
              <el-tag
                v-if="item.info == 'new' && item.name"
                type="warning"
                style="cursor: pointer"
                @click="handleDownload(item)"
                >{{ item.name }}
              </el-tag>
              <el-tag
                v-else-if="item.info == 'old' && item.name"
                type="info"
                class="oldVal"
                style="cursor: pointer"
                @click="handleDownload(item)"
                >{{ item.name }}
              </el-tag>
              <span v-else>
                <el-tag
                  v-if="item.name"
                  style="cursor: pointer"
                  @click="handleDownload(item)"
                  >{{ item.name }}</el-tag
                >
              </span>
            </span>
          </template>

          <template #designDraftList>
            <span
              v-for="item in exportSaleContractDetail.designDraftList"
              :key="item?.id"
              class="mb5px mr5px"
            >
              <el-tag
                v-if="item.info == 'new' && item.name"
                type="warning"
                style="cursor: pointer"
                @click="handleDownload(item)"
                >{{ item.name }}
              </el-tag>
              <el-tag
                v-else-if="item.info == 'old' && item.name"
                type="info"
                class="oldVal"
                style="cursor: pointer"
                @click="handleDownload(item)"
                >{{ item.name }}
              </el-tag>
              <span v-else>
                <el-tag
                  v-if="item.name"
                  style="cursor: pointer"
                  @click="handleDownload(item)"
                  >{{ item.name }}</el-tag
                >
              </span>
            </span>
          </template>
          <template #signBackAnnex>
            <UploadList
              :fileList="exportSaleContractDetail.signBackAnnex"
              disabled
            />
          </template>
        </eplus-description>

        <eplus-description
          title="运输信息"
          :data="exportSaleContractDetail"
          :items="transportSchemas"
          oldChangeField="oldData"
        >
          <template #custDeliveryDate>
            <div
              v-if="
                exportSaleContractDetail.custDeliveryDate ==
                exportSaleContractDetail.oldData.custDeliveryDate
              "
            >
              {{ formatDate(exportSaleContractDetail?.custDeliveryDate, 'YYYY-MM-DD') }}
            </div>
            <div v-else>
              <span class="newVal mr5">{{
                formatDate(exportSaleContractDetail?.custDeliveryDate, 'YYYY-MM-DD')
              }}</span>
              <span class="oldVal">{{
                formatDate(exportSaleContractDetail?.oldData.custDeliveryDate, 'YYYY-MM-DD')
              }}</span>
            </div>
          </template>
        </eplus-description>
        <div class="flex items-center">
          <p class="font-600">预估费用</p>
          <el-divider direction="vertical" />
          <div class="flex items-center">
            <span
              class="tab_item"
              :class="{ on: feeActive === 'first' }"
              @click="feeActive = 'first'"
            >
              <span>合计信息</span>
              <div
                class="text-underline"
                :class="{ on: feeActive === 'first' }"
              ></div>
            </span>
            <span
              class="tab_item"
              :class="{ on: feeActive === 'second' }"
              @click="feeActive = 'second'"
              ><span>佣金/平台费</span>
              <div
                class="text-underline"
                :class="{ on: feeActive === 'second' }"
              ></div
            ></span>
            <span
              class="tab_item"
              :class="{ on: feeActive === 'third' }"
              @click="feeActive = 'third'"
              ><span>出运费</span>
              <div
                class="text-underline"
                :class="{ on: feeActive === 'third' }"
              ></div
            ></span>
            <span
              class="tab_item"
              :class="{ on: feeActive === 'fourth' }"
              @click="feeActive = 'fourth'"
              ><span>其他</span>
              <div
                class="text-underline"
                :class="{ on: feeActive === 'fourth' }"
              ></div
            ></span>
          </div>
        </div>

        <eplus-description
          title=""
          :data="exportSaleContractDetail"
          :items="sumSchemas"
          v-if="feeActive === 'first'"
        >
          <template #totalVolume>
            <span>{{ volumnFormat(exportSaleContractDetail.totalVolume) }}</span>
          </template>
        </eplus-description>
        <eplus-description
          title=""
          :data="exportSaleContractDetail"
          :items="platFormFeeSchemas"
          v-if="feeActive === 'second'"
        />
        <eplus-description
          title=""
          :data="exportSaleContractDetail"
          :items="feeSchemas"
          v-if="feeActive === 'third'"
        >
          <template #bulkHandlingVolume>
            <span>{{ volumnFormat(exportSaleContractDetail.bulkHandlingVolume) }}</span>
          </template>
          <template #trailerFee>
            <span>{{ amountFormat(exportSaleContractDetail.trailerFee) }}</span>
          </template>
          <template #estimatedTotalFreight>
            <span>{{ amountFormat(exportSaleContractDetail.estimatedTotalFreight) }}</span>
          </template>
        </eplus-description>
        <eplus-description
          title=""
          :data="exportSaleContractDetail"
          :items="otherFeeSchemas"
          v-if="feeActive === 'fourth'"
          oldChangeField="oldData"
        />
      </el-col>
    </el-row>
    <el-tabs
      v-model="activeName"
      class="demo-tabs mt20px"
      @tab-click="handleClick"
    >
      <el-tab-pane
        label="销售明细"
        name="first"
      >
        <el-radio-group
          v-model="unitRadio"
          class="mb10px"
        >
          <el-radio-button
            label="metric"
            value="公制"
            >公制
          </el-radio-button>
          <el-radio-button
            label="eng"
            value="英制"
            >英制
          </el-radio-button>
        </el-radio-group>
        <Table
          :columns="productTableColumns"
          headerAlign="center"
          align="center"
          :data="exportSaleContractDetail?.children"
        />
      </el-tab-pane>
      <el-tab-pane
        label="加/减项"
        name="second"
      >
        <Table
          :columns="subItemTableColumns"
          headerAlign="center"
          align="center"
          :data="exportSaleContractDetail?.addSubItemList"
        />
      </el-tab-pane>
      <el-tab-pane
        label="收款计划"
        name="third"
      >
        <Table
          :columns="collectionPlanTableColumns"
          headerAlign="center"
          align="center"
          :data="exportSaleContractDetail?.collectionPlanList"
        />
      </el-tab-pane>

      <el-tab-pane
        label="操作记录"
        name="four"
      >
        <eplus-operate-log :logList="exportSaleContractDetail?.operateLogRespDTOList" />
      </el-tab-pane>
    </el-tabs>
  </eplus-detail>
  <PrintDia
    @sure="handlePrintSure"
    ref="PrintDiaRef"
  />
  <AuxiliaryShareDialog ref="auxiliaryShareRef" />
</template>
<script setup lang="tsx">
import { changeApprove, changeReject } from '@/api/audit/export-sale-contract'

import { domExamineApprove, domExamineReject } from '@/api/audit/domestic-sale-contract'
import * as ExportSaleContractApi from '@/api/sms/saleContract/export'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { getCompanyIdList, getCompanyPathNameFromObj } from '@/utils/companyPathUtils'
import { formatDate } from '@/utils/formatTime'
import { formatNum, openPdf } from '@/utils/index'
import PrintDia from '../../components/PrintDia.vue'
import * as ExportSaleContractChangeApi from '@/api/sms/saleContract/export/change'
import * as DomesticSaleContractChangeApi from '@/api/sms/saleContract/domestic/change'
import * as FactorySaleContractApi from '@/api/sms/saleContract/factory'
import { EplusFieldComparison } from '@/components/EplusFieldComparison'
import { columnWidth, formatMoneyColumn } from '@/utils/table'
import AuxiliaryShareDialog from '../../components/AuxiliaryShareDialog.vue'
import { VolumeUnit, weightConvert } from '@/utils/config'
import { useRateStore } from '@/store/modules/rate'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import { setSourceId } from '@/utils/auth'
import { checkPermi } from '@/utils/permission'

const rateList = useRateStore().rateList

const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const { query } = useRoute()
const message = useMessage()
const auxiliaryShareRef = ref()
const FlowChartRef = ref(null)
const planParams = ref()
const props = defineProps<{
  title?: string
  id?: number
  isFromComfirmPage?: boolean
  type?: number
}>()
const { close, goEdit } = props.id
  ? (inject('dialogEmits') as {
      close: () => void
      goEdit: (val) => void
    })
  : { close: () => {}, goEdit: () => {} }

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }

// const ContractInfoRef = ref()
// const openProductDetail = (data) => {
//   ContractInfoRef.value.open(data)
// }

const router = useRouter()

const openProductDetail = (row) => {
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

const emit = defineEmits(['handle-success', 'create-plan'])

const feeActive = ref('first')
const activeName = ref('first')
const exportSaleContractDetail = ref({
  accessoriesPurchaseTotal: {},
  addSubItemList: [],
  additionAmount: {},
  annex: [],
  children: [],
  collectionPlanList: [],
  commission: {},
  companyPath: {},
  deductionAmount: {},
  designDraftList: [],
  signBackAnnex: [],
  estimatedPackingMaterials: {},
  estimatedTotalFreight: {},
  inspectionFee: {},
  insuranceFee: {},
  lumpSumFee: {},
  operateLogRespDTOList: [],
  orderGrossProfit: {},
  orderLingDTO: [],
  platformFee: {},
  receivableExchange: {},
  sales: {},
  signBackUser: {},
  sinosureFee: {},
  totalGoodsValue: {},
  totalGrossweight: {},
  totalPurchase: [],
  totalVatRefund: {},
  totalWeight: {},
  trailerFee: {}
})

const pagePath = useRoute().path

const contractType = props.type || 1 // 1外销 ：2 内销 3 外币采购
const permiPrefix =
  props.type === 1
    ? 'sms:export-sale-contract-change'
    : props.type === 2
      ? 'sms:domestic-sale-contract-change'
      : 'sms:factory-sale-contract-change'
const amountFormat = (val) => {
  if (!val) return '-'
  if (val.amount && val.currency) {
    return `${val.amount.toFixed(3)} ${val.currency}`
  } else {
    return '-'
  }
}
const volumnFormat = (val) => {
  if (!val) return '-'
  return `${formatNum(val / 1000000, 6)} ${VolumeUnit}`
}

const exportSaleContractSchemas = reactive([
  {
    field: 'code',
    label: '合同号'
  },
  {
    field: 'custName',
    label: '客户名称'
  },
  {
    field: 'companyPathName',
    label: '订单路径',
    disabled: contractType == 2
  },
  {
    field: 'companyName',
    label: '下单主体'
  },
  {
    field: 'custPo',
    label: '客户合同号',
    isCompare: true
  },
  {
    field: 'settlementName',
    label: '收款方式',
    isCompare: true
  },
  {
    field: 'custCountryName',
    label: '客户国别'
  },
  {
    field: 'currency',
    label: '交易币别',
    // dictType: DICT_TYPE.CURRENCY_TYPE,
    isCompare: true
  },
  {
    field: 'exchangeRate',
    label: '币别汇率'
  },
  {
    field: 'settlementTermType',
    label: '价格条款',
    // dictType: DICT_TYPE.PRICE_TYPE,
    isCompare: true
  },
  {
    field: 'agentFlag',
    label: '是否联营',
    dictType: DICT_TYPE.CONFIRM_TYPE,
    isCompare: true
  },
  {
    field: 'collectedCustName',
    label: '付款方'
  },
  {
    field: 'receiveCustName',
    label: '收货方'
  },
  {
    field: 'salesNickName',
    label: '业务员',
    isCompare: true
  },
  {
    field: 'salesDeptName',
    label: '业务部门',
    isCompare: true
  },
  {
    field: 'signBackDate',
    label: '回签日期',
    type: 'date',
    isCompare: true
  },
  {
    slotName: 'annex',
    field: 'annex',
    label: '附件',
    span: 24
  },
  {
    slotName: 'designDraftList',
    field: 'designDraftList',
    label: '设计稿',
    span: 24
  },
  {
    slotName: 'signBackAnnex',
    field: 'signBackAnnex',
    label: '回签附件',
    span: 24
  }
])

let transportSchemas = reactive([
  {
    field: 'tradeCountryName',
    label: '贸易国别',
    isCompare: true
  },
  {
    field: 'tradeCountryArea',
    label: '贸易国区域',
    isCompare: true
  },
  {
    field: 'destinationPortName',
    label: '目的口岸',
    isCompare: true
  },
  {
    field: 'deliveryAddress',
    label: '送货地址',
    isCompare: true
  },
  {
    field: 'departureCountryName',
    label: '出运国',
    isCompare: true
  },
  {
    field: 'departureCountryArea',
    label: '出运国区域',
    isCompare: true
  },
  {
    field: 'departurePortName',
    label: '出运口岸',
    isCompare: true
  },
  {
    label: '运输方式',
    field: 'transportType',
    dictType: DICT_TYPE.TRANSPORT_TYPE,
    isCompare: true
  },
  {
    field: 'custDeliveryDate',
    label: '客户交期',
    slotName: 'custDeliveryDate'
  }
])

const feeSchemas = reactive([
  {
    field: 'twentyFootCabinetNum',
    label: '20尺柜(个)'
  },
  {
    field: 'fortyFootCabinetNum',
    label: '40尺柜(个)'
  },
  {
    field: 'fortyFootContainerNum',
    label: '40尺高柜(个)'
  },
  {
    field: 'bulkHandlingVolume',
    label: '散货',
    slotName: 'bulkHandlingVolume'
  },
  {
    field: 'trailerFee',
    label: '拖柜费',
    slotName: 'trailerFee'
  },
  {
    field: 'estimatedTotalFreight',
    label: '预估总运费',
    slotName: 'estimatedTotalFreight'
  }
])

const platFormFeeSchemas = reactive([
  {
    field: 'commission',
    label: '佣金',
    type: 'money'
  },
  {
    field: 'platformFee',
    label: '平台费',
    type: 'money'
  }
])
const otherFeeSchemas = reactive([
  {
    field: 'insuranceFee',
    label: '保险费(RMB)',
    isCompare: true
  },
  {
    field: 'sinosureFee',
    label: '中信保费用',
    type: 'money'
  },
  {
    field: 'additionAmount',
    label: '加项金额(RMB)',
    type: 'money'
  },
  {
    field: 'deductionAmount',
    label: '减项金额(RMB)',
    type: 'money'
  },
  {
    field: 'inspectionFee',
    label: '验货费用',
    isCompare: true
  },
  {
    field: 'estimatedPackingMaterials',
    label: '预计包材合计',
    type: 'money'
  },
  {
    field: 'accessoriesPurchaseTotal',
    label: '配件采购费用',
    type: 'money'
  }
])
let sumSchemas = reactive([
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
    slotName: 'totalVolume'
  },
  {
    field: 'totalGoodsValue',
    label: '货值合计',
    type: 'money'
  },
  {
    field: 'totalPurchase',
    label: '采购合计',
    type: 'money'
  },
  {
    field: 'totalStockCost',
    label: '库存合计',
    type: 'money'
  },
  {
    field: 'totalVatRefund',
    label: '退税合计',
    type: 'money'
  },
  {
    field: 'totalQuantity',
    label: '数量合计',
    type: 'num'
  },
  {
    field: 'orderGrossProfit',
    label: '订单毛利',
    type: 'money'
  },
  {
    field: 'grossProfitMargin',
    label: '毛利率%',
    type: 'rate'
  },
  {
    field: 'totalAmount',
    label: '销售总金额',
    type: 'money'
  },
  {
    field: 'totalAmountUSD',
    label: '销售总金额(美金)',
    type: 'money'
  },
  {
    label: '收款合计',
    field: 'collectionTotal',
    type: 'money'
  },
  {
    label: '已收款金额',
    field: 'receivedAmount',
    type: 'money'
  },
  {
    label: '未收款金额',
    field: 'unReceivedAmount',
    type: 'money'
  }
])

const formatLength = (length, width, height) => {
  if (length && width && height) {
    return (
      <span>
        <span>{length.toFixed(2) + '*' + width.toFixed(2) + '*' + height.toFixed(2) + 'cm'}</span>
      </span>
    )
  } else {
    return '-'
  }
}
const subItemTableColumns = reactive([
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
            filed="feeName"
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
            item={row}
          />
        )
      }
    }
  }
])
const collectionPlanTableColumns = reactive([
  {
    field: 'step',
    label: '步骤',
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.COLLECTION_PLAN_STEP, row?.step) || '-'
    }
  },
  {
    field: 'dateType',
    label: '起始点',
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.DATE_TYPE, row?.dateType) || '-'
    }
  },
  {
    field: 'startDate',
    label: '起始日',
    formatter: (row) => {
      if (row.startDate) {
        return formatDate(row.startDate, 'YYYY-MM-DD')
      } else {
        return '-'
      }
    }
  },
  {
    field: 'days',
    label: '天数'
  },
  {
    field: 'expectedReceiptDate',
    label: '预计收款日',
    formatter: (row) => {
      if (row.expectedReceiptDate) {
        return formatDate(row.expectedReceiptDate, 'YYYY-MM-DD')
      } else {
        return '-'
      }
    }
  },
  {
    field: 'collectionRatio',
    label: '收款比例%'
  },
  {
    field: 'receivableAmount',
    label: '应收金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'receivedAmount',
    label: '实收金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'controlPurchaseFlag',
    label: '是否控制采购',
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.CALCULATION_TYPE, row?.controlPurchaseFlag) || '-'
    }
  },
  {
    field: 'controlShipmentFlag',
    label: '是否控制出运',
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.CALCULATION_TYPE, row?.controlShipmentFlag) || '-'
    }
  },
  {
    field: 'exeStatus',
    label: '状态',
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.EXECUTE_STATUS, row?.exeStatus) || '-'
    }
  }
])

const StockLockCreateDiaRef = ref()
const handleLock = (row) => {
  StockLockCreateDiaRef.value.open({
    ...row,
    companyIdList: getCompanyIdList(row?.parent?.companyPath)
  })
}

const unitRadio = ref('metric')
//产品信息table
let productTableColumns = reactive([
  {
    field: 'mainPicture',
    label: '图片',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusImgEnlarge
            mainPicture={row?.mainPicture}
            thumbnail={row?.thumbnail}
          />
        )
      }
    }
  },
  {
    field: 'cskuCode',
    label: '客户货号',
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
    field: 'name',
    label: '中文名称',
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
    field: 'quantity',
    label: '数量',
    formatter: (row) => {
      return (
        <>
          <EplusFieldComparison
            filed="quantity"
            oldChangeField="oldData"
            item={row}
          />
        </>
      )
    }
  },
  {
    field: 'unitPrice',
    label: '销售单价',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return (
        <>
          <EplusFieldComparison
            filed="unitPrice"
            oldChangeField="oldData"
            item={row}
          />
        </>
      )
    }
  },
  {
    field: 'totalSaleAmount',
    label: '总金额',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="totalSaleAmount"
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
    minWidth: columnWidth.l,
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
    field: 'purchaseTotalAmount',
    label: '采购总金额',
    minWidth: columnWidth.l,
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
    field: 'skuCode',
    label: '产品编码',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <span
            onClick={() => openProductDetail(row)}
            style="color:rgb(0, 91, 245);cursor:pointer;"
          >
            <EplusFieldComparison
              filed="skuCode"
              oldChangeField="oldData"
              item={row}
            />
          </span>
        )
      }
    }
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    minWidth: columnWidth.l,
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
    field: 'description',
    label: '中文说明',
    minWidth: columnWidth.l,
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
    minWidth: columnWidth.l,
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
    field: 'orderGrossProfit',
    label: '订单毛利',
    minWidth: columnWidth.l,
    // formatter: formatMoneyColumn()
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="orderGrossProfit"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'orderGrossProfitRate',
    label: '毛利率',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        // return row.orderGrossProfitRate ? Number(row.orderGrossProfitRate * 100).toFixed(2) : '-'
        return (
          <EplusFieldComparison
            filed="orderGrossProfitRate"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'taxRefundRate',
    label: '退税率',
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
    label: '退税金额(RMB)',
    width: columnWidth.l,
    // formatter: formatMoneyColumn()
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
    field: 'commissionType',
    label: '佣金类型',
    minWidth: columnWidth.l,
    // formatter: (row) => {
    //   return getDictLabel(DICT_TYPE.COMMISSION_TYPE, row?.commissionType) || '-'
    // }
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="commissionType"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.COMMISSION_TYPE, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'commissionRate',
    label: '佣金比例(%)',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="commissionRate"
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
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="commissionAmount"
            oldChangeField="oldData"
            formatter={(val) => {
              return `${val?.amount} ${val?.currency}`
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'purchaseCurrency',
    label: '采购币种',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="purchaseCurrency"
            oldChangeField="oldData"
            formatter={(val) => {
              // return formatDate(val, 'YYYY-MM-DD')
              return getDictLabel(DICT_TYPE.CURRENCY_TYPE, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'realLockQuantity',
    label: '锁定库存',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="currentLockQuantity"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'needPurQuantity',
    label: '待采购数量',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="needPurQuantity"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'purchasePackagingPrice',
    label: '包装价',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="purchasePackagingPrice"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },

  {
    field: 'nameEng',
    label: '英文名称',
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
    field: 'unit',
    label: '计量单位',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="unit"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.MEASURE_UNIT, val)
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'qtyPerInnerbox',
    label: '内盒装量',
    minWidth: columnWidth.l,
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
    width: columnWidth.m,
    formatter: (row) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'boxCount',
    label: '箱数',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="boxCount"
            oldChangeField="oldData"
            formatter={(val) => {
              return formatNum(val * (row.specificationList?.length || 1))
            }}
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'volume',
    label: '体积(m3)',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="volume"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'reorderFlag',
    label: '是否翻单',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="reorderFlag"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.CONFIRM_TYPE, val)
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
    minWidth: columnWidth.l,
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
    field: 'outerboxNetweight',
    label: '外箱净重',
    minWidth: columnWidth.l,
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
    field: 'venderName',
    label: '厂商名称',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="venderName"
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
    minWidth: columnWidth.l,
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
    field: 'billStatus',
    label: '入库状态',
    formatter: (row) => {
      return (
        <div class="relative h-50px flex items-center">
          {/* 普通产品的角标不展示 */}
          <EplusFieldComparison
            filed="billStatus"
            oldChangeField="oldData"
            formatter={(val) => {
              return getDictLabel(DICT_TYPE.BILL_STATUS, val)
            }}
            item={row}
          />
          {row?.abnormalStatus ? (
            <el-badge
              class="item"
              value={getDictLabel(DICT_TYPE.BILL_STATUS, row?.billStatus).split('')[0] || ''}
              type="primary"
            >
              {row?.billStatus}
            </el-badge>
          ) : (
            ''
          )}
        </div>
      )
    }
  },
  {
    field: 'venderDeliveryDate',
    label: '工厂交期',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="venderDeliveryDate"
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
    field: 'inspectionFee',
    label: '验货费',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="inspectionFee"
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
    field: 'trailerFee',
    label: '拖柜费',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="trailerFee"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'insuranceFee',
    label: '保险费',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="insuranceFee"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'platformFee',
    label: '平台费',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="platformFee"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'forecastTotalCost',
    label: '预估总费用',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="forecastTotalCost"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'innerCalcPrice',
    label: '定价差价',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="innerCalcPrice"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  },
  {
    field: 'sinosureFee',
    label: '中信保费用',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusFieldComparison
            filed="sinosureFee"
            oldChangeField="oldData"
            item={row}
          />
        )
      }
    }
  }
  // {
  //   field: 'inventoryQuantity',
  //   label: '库存',
  //   slots: {
  //     default: (data) => {
  //       const { row } = data
  //       return (
  //         <EplusFieldComparison
  //           filed="inventoryQuantity"
  //           oldChangeField="oldData"
  //           item={row}
  //         />
  //       )
  //     }
  //   }
  // },
  // {
  //   field: 'purchaseUnitPrice',
  //   label: '采购单价',
  //   minWidth: columnWidth.l,
  //   slots: {
  //     default: (data) => {
  //       const { row } = data
  //       return (
  //         <EplusFieldComparison
  //           filed="purchaseUnitPrice"
  //           oldChangeField="oldData"
  //           item={row}
  //         />
  //       )
  //     }
  //   }
  // },
])

// 包材分摊
const auxiliaryShareTableColumns = reactive([
  {
    field: 'skuCode',
    label: '包材编号'
  },
  {
    field: 'skuName',
    label: '包材名称'
  },
  {
    field: 'skuUnit',
    label: '计量单位'
  },
  {
    prop: 'mainPicture',
    label: '图片',
    // fixed: 'left',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusImgEnlarge
            mainPicture={row?.mainPicture}
            thumbnail={row?.thumbnail}
          />
        )
      }
    }
  },
  {
    field: 'purchaseContractCode',
    label: '包材采购合同'
  },
  {
    field: 'purchaseQuantity',
    label: '包材采购数量'
  },
  {
    field: 'unitPrice',
    label: '包材采购单价'
  },
  {
    field: 'totalSaleAmount',
    label: '包材采购总价'
  },
  {
    field: 'shareFlag',
    label: '是否分摊',
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.CALCULATION_TYPE, row?.shareFlag) || '-'
    }
  },
  {
    field: 'receivableAmount',
    label: '分摊金额',
    slots: {
      default: (data) => {
        const { row } = data
        return row.receivableAmount?.amount ? Number(row.receivableAmount?.amount).toFixed(3) : '-'
      }
    }
  },

  {
    field: 'action',
    label: '操作',
    minWidth: '120px',
    fixed: 'right',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <div class="flex items-center justify-between">
            <el-button
              link
              type="primary"
              onClick={() => {
                handleShare(row)
              }}
            >
              {row.status == 1 ? '分摊' : '取消分摊'}
            </el-button>
          </div>
        )
      }
    }
  }
])

const handleShare = (row) => {
  auxiliaryShareRef.value?.open(row)
}

const handleClick = (val) => {
  if (val.props.name == 'seventh') {
    setTimeout(() => {
      FlowChartRef.value.init()
    })
  }
}
const loading = ref(true)
const getPurchaseContractDetail = () => {}
const handleUpdate = async () => {
  await getPurchaseContractDetail()
}
if (query?.id) {
  showProcessInstanceTaskListFlag.value = false
  outDialogFlag.value = true
}
if (props.id) {
  showProcessInstanceTaskListFlag.value = true
  outDialogFlag.value = false
}

const getInfo = async () => {
  loading.value = true
  try {
    let obj: any = null
    if (props.id) {
      // 普通详情 contractType => 1 外销  2内销
      obj =
        contractType == 1
          ? await ExportSaleContractChangeApi.getChangeInfo({ id: props?.id })
          : contractType == 2
            ? await DomesticSaleContractChangeApi.getChangeInfo({ id: props?.id })
            : await FactorySaleContractApi.getChangeInfo({ id: props?.id })
    } else {
      // 审核详情
      obj =
        contractType == 1
          ? await ExportSaleContractChangeApi.getChangeAuditInfo({ id: query?.id })
          : contractType == 2
            ? await DomesticSaleContractChangeApi.getChangeAuditInfo({ id: query?.id })
            : await FactorySaleContractApi.getChangeAuditInfo({ id: props?.id })
    }

    obj.changeFlag = false
    obj.salesNickName = obj.sales?.nickname
    obj.oldData.salesNickName = obj.oldData.sales?.nickname
    obj.salesDeptName = obj.sales?.deptName
    obj.oldData.salesDeptName = obj.oldData.sales?.deptName

    obj.annex = changeData(obj.annex, obj.oldData.annex)
    obj.designDraftList = changeData(obj.designDraftList, obj.oldData.designDraftList)

    obj.collectionPlanList = obj.collectionPlanList.filter((item) => item.changeStatus != 4)

    if (obj?.totalGoodsValue?.amount) {
      let totalAmount = obj?.totalGoodsValue.amount * rateList[obj.totalGoodsValue.currency]
      if (obj.additionAmount?.amount) {
        totalAmount = Number(
          totalAmount + obj.additionAmount?.amount * rateList[obj.additionAmount.currency]
        )
      }
      if (obj.deductionAmount?.amount) {
        totalAmount = Number(
          totalAmount - obj.deductionAmount?.amount * rateList[obj.deductionAmount.currency]
        )
      }
      obj.totalAmount = {
        amount: totalAmount,
        currency: 'RMB'
      }
      obj.totalAmountUSD = {
        amount: obj.totalAmount?.amount / rateList['USD'],
        currency: 'USD'
      }
    }

    obj.oldData.children.forEach((item) => {
      item.purchaseUserName = item.purchaseUser?.nickname
      item.volume = Number(item.volume) / 1000000
      item.outerboxGrossweight = weightConvert(item.outerboxGrossweight, unitRadio.value)
      item.outerboxNetweight = weightConvert(item.outerboxNetweight, unitRadio.value)

      item.orderGrossProfitRate = item.orderGrossProfitRate
        ? Number(item.orderGrossProfitRate * 100).toFixed(2)
        : '-'

      item.innerCalcPrice = item.innerCalcPrice?.amount.toFixed(3)
      item.sinosureFee = item.sinosureFee?.amount.toFixed(3)

      item.inspectionFee =
        item.inspectionFee == null
          ? 0
          : `${item.inspectionFee.amount} ${item.inspectionFee?.currency}`
      item.purchaseTotalAmount = { amount: 0, currency: 'RMB' }
      if (item.purchaseWithTaxPrice.amount && item.needPurQuantity) {
        item.purchaseTotalAmount.amount =
          Number(item.purchaseWithTaxPrice.amount) * Number(item.needPurQuantity)
        item.purchaseTotalAmount.currency = item.purchaseWithTaxPrice.currency
      }
    })
    obj.children.forEach((item2) => {
      item2.purchaseUserName = item2.purchaseUser?.nickname
      item2.volume = Number(item2.volume) / 1000000
      item2.outerboxGrossweight = weightConvert(item2.outerboxGrossweight, unitRadio.value)
      item2.outerboxNetweight = weightConvert(item2.outerboxNetweight, unitRadio.value)
      item2.oldChildren = obj.oldData.children.filter((item3) => item3?.id === item2?.id)

      item2.orderGrossProfitRate = item2.orderGrossProfitRate
        ? Number(item2.orderGrossProfitRate * 100).toFixed(2)
        : '-'
      item2.innerCalcPrice = item2.innerCalcPrice?.amount.toFixed(3)
      item2.sinosureFee = item2.sinosureFee?.amount.toFixed(3)
      item2.inspectionFee =
        item2.inspectionFee == null
          ? 0
          : `${item2.inspectionFee.amount} ${item2.inspectionFee?.currency}`

      //采购总金额计算 采购含税单价 * 待采购数量
      item2.purchaseTotalAmount = { amount: 0, currency: 'RMB' }
      if (item2.purchaseWithTaxPrice.amount && item2.needPurQuantity) {
        item2.purchaseTotalAmount.amount =
          Number(item2.purchaseWithTaxPrice.amount) * Number(item2.needPurQuantity)
        item2.purchaseTotalAmount.currency = item2.purchaseWithTaxPrice.currency
      }
      return item2
    })

    if (obj.companyPath) {
      obj.companyPathName = getCompanyPathNameFromObj(obj.companyPath)
    }
    exportSaleContractDetail.value = obj
    //审核通过显示打印按钮

    backCreatePlanParams(exportSaleContractDetail.value)
  } finally {
    loading.value = false
  }
}

const handleDownload = (item) => {
  window.location.href = `${import.meta.env.VITE_BASE_URL}/admin-api${item.fileUrl}`
}

const changeData = (newData, oldData) => {
  if (newData && oldData) {
    const newArr = newData?.filter((obj1) => !oldData?.some((obj2) => obj1.name === obj2.name))
    newArr?.forEach((obj) => (obj.info = 'new'))

    const oldArr = oldData?.filter((obj2) => !newData?.some((obj1) => obj1.name === obj2.name))
    oldArr?.forEach((obj) => (obj.info = 'old'))
    newData = [...newData, ...oldArr]
  }
  return newData
}

// 拿到数据以后，将转出运计划的数据返给详情组件
const backCreatePlanParams = (data) => {
  let idsArray = data.children?.map((obj) => obj.id)
  planParams.value = {
    companyId: data.companyId,
    companyName: data.companyName,
    companyTitle: data.companyTitle,
    childrenIdList: idsArray?.join(',')
  }
  emit('create-plan', planParams.value)
}
const reportCode = 'export-sale-contract'
const reportDOListRef = ref()
//--打印按钮设置

const PrintDiaRef = ref()

const print = async (params) => {
  let companyId = exportSaleContractDetail.value.companyId
  const url = await ExportSaleContractApi.print({ id: props?.id, companyId: companyId, ...params })
  openPdf(url)
}
const handlePrintSure = async (reportId) => {
  let params = {
    reportCode: reportCode,
    reportId: reportId
  }
  await print(params)
}

onMounted(async () => {
  if (!props.id && !query.id) {
    message.error('外销合同ID不能为空')
    if (!props.id) {
      close()
    }
  }
  await getInfo()
})
</script>
<style lang="scss" scoped>
:deep(.el-badge__content) {
  position: relative;
  left: -55px;
  width: 30px;
}

:deep(.el-badge__content--primary) {
  color: #7cc440;
  background-color: #fff;
}

.tab_item {
  width: 120px;
  text-align: center;
  cursor: pointer;
}

.tab_item.on {
  color: #409eff;
}

.text-underline {
  width: 45%;
  margin: 2px auto 0px;
  height: 2px;
  background-color: #fff;
  border-radius: 5px;
}

.text-underline.on {
  background-color: #409eff;
}

.newVal {
  color: #f7aa49;
}

.oldVal {
  color: #999;
  text-decoration: line-through;
}
</style>
