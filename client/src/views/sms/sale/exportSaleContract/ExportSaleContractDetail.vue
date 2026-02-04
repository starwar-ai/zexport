<template>
  <eplus-detail
    v-if="!loading"
    ref="eplusDetailRef"
    :page="pageFlag"
    :approveApi="contractType === 1 ? examineApprove : domExamineApprove"
    :rejectApi="contractType === 1 ? examineReject : domExamineReject"
    :auditable="exportSaleContractDetail"
    :showProcessInstanceTaskList="showProcessInstanceTaskListFlag"
    :outDialog="outDialogFlag"
    :cancel="{
      permi: `${permiPrefix}:submit`,
      handler: () => {}
    }"
    :edit="{
      permi: `${permiPrefix}:update`,
      handler: () => handleGoEdit()
    }"
    :approve="{
      permi: `${permiPrefix}:audit`,
      handler: () => {}
    }"
    @update="handleUpdate"
    :backUrl="
      contractType === 1
        ? '/sms/sale-orders/exportSaleContract'
        : contractType === 2
          ? '/sms/sale-orders/domesticSaleContract'
          : '/sms/sale-orders/factoryContract'
    "
  >
    <el-tabs v-model="tabActive">
      <el-tab-pane
        label="销售合同"
        name="Contract"
      />
      <el-tab-pane
        label="订单费用"
        name="Cost"
      />
    </el-tabs>
    <template v-if="tabActive === 'Contract'">
      <eplus-description
        title="基本信息"
        :data="exportSaleContractDetail"
        :items="exportSaleContractSchemas"
      >
        <template #salesNickName>
          <span>
            {{
              exportSaleContractDetail.sales?.nickname
                ? exportSaleContractDetail.sales.nickname
                : '-'
            }}
          </span>
        </template>
        <template #salesDeptName>
          <span>
            {{
              exportSaleContractDetail.sales.deptName
                ? exportSaleContractDetail.sales.deptName
                : '-'
            }}
          </span>
        </template>
        <template #managerNickName>
          <span>
            {{
              exportSaleContractDetail.manager?.nickname
                ? exportSaleContractDetail.manager.nickname
                : '-'
            }}
          </span>
        </template>
        <template #managerDeptName>
          <span>
            {{
              exportSaleContractDetail.manager?.deptName
                ? exportSaleContractDetail.manager.deptName
                : '-'
            }}
          </span>
        </template>

        <template #annex>
          <UploadList
            :fileList="exportSaleContractDetail.annex"
            disabled
          />
        </template>

        <template #designDraftList>
          <UploadList
            :fileList="exportSaleContractDetail.designDraftList"
            @success="
              (data) => {
                exportSaleContractDetail.designDraftList = data
                saveDesignDraft()
              }
            "
          />
          <!-- <el-button
              size="small"
              type="primary"
              @click="saveDesignDraft"
              >保存设计稿
            </el-button> -->
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
      />
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
        <template #grossProfitMargin>
          <span
            v-if="exportSaleContractDetail.grossProfitMargin < 0"
            style="color: red"
          >
            {{ Number(exportSaleContractDetail.grossProfitMargin * 100).toFixed(2) + ' %' }}
          </span>
          <span v-else>
            {{ Number(exportSaleContractDetail.grossProfitMargin * 100).toFixed(2) + ' %' }}
          </span>
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
      />
      <eplus-description
        title=""
        :data="exportSaleContractDetail"
        :items="otherFeeSchemas"
        v-if="feeActive === 'fourth'"
      />
      <el-tabs
        v-model="activeName"
        @tab-click="handleClick"
      >
        <el-tab-pane
          label="销售明细"
          name="first"
        />
        <el-tab-pane
          label="加/减项"
          name="second"
        />
        <el-tab-pane
          label="收款计划"
          name="third"
        />
        <el-tab-pane
          label="关联单据"
          name="fifth"
        />
        <el-tab-pane
          label="单据流程"
          name="seventh"
        />
        <el-tab-pane
          label="操作记录"
          name="four"
        />
      </el-tabs>
      <div v-show="activeName === 'first'">
        <div class="mb10px flex items-center justify-between">
          <el-radio-group v-model="unitRadio">
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
          <el-radio-group v-model="splitRadio">
            <el-radio-button
              label="before"
              value="拆分前明细"
              >拆分前明细
            </el-radio-button>
            <el-radio-button
              label="after"
              value="拆分后明细"
              >拆分后明细
            </el-radio-button>
          </el-radio-group>
        </div>

        <Table
          :columns="productTableColumns"
          headerAlign="center"
          align="center"
          :data="
            splitRadio === 'after'
              ? exportSaleContractDetail?.children
              : exportSaleContractDetail?.groupChildren
          "
          :maxHeight="430"
          showSummary
          sumText="合计"
          :summaryMethod="handleSummary"
        />
      </div>
      <div v-show="activeName === 'second'">
        <Table
          :columns="subItemTableColumns"
          headerAlign="center"
          align="center"
          :data="exportSaleContractDetail?.addSubItemList"
        />
      </div>
      <div v-show="activeName === 'third'">
        <eplus-table :eplusTableSchema="eplusTableSchema">
          <template #tableActions>
            <el-button
              v-if="
                exportSaleContractDetail?.confirmFlag !== 0 &&
                exportSaleContractDetail?.changeStatus !== 2
              "
              v-hasPermi="[`${permiPrefix}:update-collection-plan`]"
              @click="handleUpdateCollectionPlan"
            >
              修改控制
            </el-button>
          </template>
        </eplus-table>
      </div>
      <div v-show="activeName === 'fifth'">
        <RelateTable :data="exportSaleContractDetail" />
      </div>
      <div v-show="activeName === 'seventh'">
        <EplusFlowChart
          ref="FlowChartRef"
          :orderData="exportSaleContractDetail?.orderLingDTO"
        />
      </div>
      <div v-show="activeName === 'four'">
        <eplus-operate-log :logList="exportSaleContractDetail?.operateLogRespDTOList" />
      </div>
    </template>

    <div v-if="tabActive === 'Cost'">
      <CostPage :id="pageId" />
    </div>
    <template #otherAction>
      <component
        v-for="(item, index) in btnList"
        :key="index"
        :is="item"
      />
    </template>
  </eplus-detail>
  <PrintDia
    @sure="handlePrintSure"
    ref="PrintDiaRef"
  />
  <AuxiliaryShareDialog ref="auxiliaryShareRef" />

  <AgainLockStock
    ref="AgainLockStockRef"
    @success="getInfo"
  />

  <SignBackForm
    ref="SignBackFormRef"
    :type="contractType"
    @success="getInfo"
  />

  <OtherActionCom
    ref="OtherActionComRef"
    @success="getInfo"
  />

  <UpdateCollectionPlan
    ref="UpdateCollectionPlanRef"
    @success="getInfo"
  />

  <TtnoticeDia
    ref="TtnoticeDiaRef"
    @success="getInfo"
  />
</template>
<script setup lang="tsx">
import { examineApprove, examineReject } from '@/api/audit/export-sale-contract'
import { domExamineApprove, domExamineReject } from '@/api/audit/domestic-sale-contract'
import * as ExportSaleContractApi from '@/api/sms/saleContract/export'
import * as DomesticSaleContractApi from '@/api/sms/saleContract/domestic'
import * as FactorySaleContractApi from '@/api/sms/saleContract/factory'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { getCompanyIdList, getCompanyPathNameFromObj } from '@/utils/companyPathUtils'
import { formatDate } from '@/utils/formatTime'
import { formatNum, getCurrency, openPdf } from '@/utils/index'
import PrintDia from '../components/PrintDia.vue'
import * as ReportApi from '@/api/system/poiReport'
import { columnWidth, formatDictColumn, formatMoneyColumn, formatNumColumn } from '@/utils/table'
import AuxiliaryShareDialog from '../components/AuxiliaryShareDialog.vue'
import EplusFlowChart from '@/components/EplusFlowChart/index.vue'
import { moneyTotalPrecision, volPrecision, VolumeUnit } from '@/utils/config'
import { globalStore } from '@/store/modules/globalVariable'
import { SaleContractStatusEnum } from '@/utils/constants'
import AgainLockStock from './components/AgainLockStock.vue'
import LockCom from './components/LockCom.vue'
import download from '@/utils/download'
import SkuCodeCom from './components/SkuCodeCom.vue'
import SignBackForm from './SignBackForm.vue'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import OtherActionCom from './components/OtherActionCom.vue'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import CostPage from './components/CostPage.vue'
import UpdateCollectionPlan from './components/UpdateCollectionPlan.vue'
import TtnoticeDia from './components/TtnoticeDia.vue'
import { setSourceId } from '@/utils/auth'
import { checkPermi } from '@/utils/permission'
import { EplusSkuName } from '@/components/EplusSkuName'
import { isAmount, isNumber, isObject } from '@/utils/is'
import { getOuterbox } from '@/utils/outboxSpec'
import { EplusDesc } from '@/components/EplusTemplate'

const showProcessInstanceTaskListFlag = ref(true)
const outDialogFlag = ref(false)
const { query } = useRoute()
const message = useMessage()
const auxiliaryShareRef = ref()
const FlowChartRef = ref(null)
const planParams = ref()
const pageId = ref()
const pageFlag = ref(false)
const btnList = ref<any[]>([])
const tabActive = ref('Contract')
const eplusDetailRef = ref()
const props = defineProps<{
  title?: string
  id?: number
  type?: number
}>()
const contractType = ref(1)
const UpdateCollectionPlanRef = ref()
const handleUpdateCollectionPlan = () => {
  UpdateCollectionPlanRef.value.open(
    exportSaleContractDetail.value.collectionPlanList,
    contractType.value
  )
}

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
const OtherActionComRef = ref()
const handleGoEdit = () => {
  changeStore.setChangeType('edit')
  OtherActionComRef.value.handleUpdate(exportSaleContractDetail.value.id, contractType.value)
}

const handleSubmit = async () => {
  if (contractType.value == 1) {
    await ExportSaleContractApi.submitExportSaleContract(exportSaleContractDetail.value.id)
  } else if (contractType.value == 2) {
    await DomesticSaleContractApi.submitDomesticSaleContract(exportSaleContractDetail.value.id)
  } else if (contractType.value == 3) {
    await FactorySaleContractApi.submitFactoryContract(exportSaleContractDetail.value.id)
  }
  message.success('提交成功')
  getInfo()
}

const emit = defineEmits(['handle-success', 'create-plan', 'handle-change'])

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
    disabled: true
  },
  {
    field: 'companyName',
    label: '下单主体'
  },
  {
    field: 'custPo',
    label: '客户合同号'
  },
  {
    field: 'settlementName',
    label: '收款方式'
  },
  {
    field: 'custCountryName',
    label: '客户国别'
  },
  {
    field: 'currency',
    label: '交易币别',
    dictType: DICT_TYPE.CURRENCY_TYPE
  },
  {
    field: 'exchangeRate',
    label: '币别汇率'
  },
  {
    field: 'settlementTermType',
    label: '价格条款',
    dictType: DICT_TYPE.PRICE_TYPE,
    disabled: true
  },
  {
    field: 'agentFlag',
    label: '是否联营',
    dictType: DICT_TYPE.CONFIRM_TYPE
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
    slotName: 'salesNickName'
  },
  {
    field: 'salesDeptName',
    label: '业务部门',
    slotName: 'salesDeptName'
  },
  {
    field: 'managerNickName',
    label: '业务跟单',
    slotName: 'managerNickName'
  },
  {
    field: 'managerDeptName',
    label: '跟单部门',
    slotName: 'managerDeptName'
  },
  {
    field: 'signBackDate',
    label: '回签日期',
    type: 'date'
  },
  {
    field: 'status',
    label: '状态',
    dictType:
      contractType.value == 1 ? DICT_TYPE.SALE_CONTRACT_STATUS : DICT_TYPE.DOME_SALE_CONTRACT_STATUS
  },
  {
    field: 'creatorName',
    label: '创建人'
  },
  {
    field: 'creatorDeptName',
    label: '创建人部门'
  },
  {
    field: 'createTime',
    label: '创建日期',
    type: 'date'
  },
  {
    field: 'remark',
    label: '备注'
  },
  {
    slotName: 'annex',
    field: 'annex',
    label: '附件',
    span: 8
  },
  {
    slotName: 'designDraftList',
    field: 'designDraftList',
    label: '设计稿',
    span: 8
  },
  {
    slotName: 'signBackAnnex',
    field: 'signBackAnnex',
    label: '回签附件',
    span: 8
  }
])

let transportSchemas = reactive([
  {
    field: 'tradeCountryName',
    label: '进口国'
  },
  {
    field: 'tradeCountryArea',
    label: '进口国区域'
  },
  {
    field: 'destinationPortName',
    label: '目的口岸'
  },
  {
    field: 'deliveryAddress',
    label: '送货地址'
  },
  {
    field: 'departureCountryName',
    label: '出口国'
  },
  {
    field: 'departureCountryArea',
    label: '出口国区域'
  },
  {
    field: 'departurePortName',
    label: '出运口岸'
  },
  {
    label: '运输方式',
    field: 'transportType',
    dictType: DICT_TYPE.TRANSPORT_TYPE
  },
  {
    field: 'custDeliveryDate',
    label: '客户交期',
    type: 'date'
  }
])

const feeSchemas = reactive([
  {
    field: 'twentyFootCabinetNum',
    label: '20尺柜(个)',
    type: 'num'
  },
  {
    field: 'fortyFootCabinetNum',
    label: '40尺柜(个)',
    type: 'num'
  },
  {
    field: 'fortyFootContainerNum',
    label: '40尺高柜(个)',
    type: 'num'
  },
  {
    label: `散货(${VolumeUnit})`,
    field: 'bulkHandlingVolume',
    type: 'volume'
  },
  {
    field: 'trailerFee',
    label: '拖柜费',
    type: 'money'
  },
  {
    field: 'estimatedTotalFreight',
    label: '预估总运费',
    type: 'money'
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
    type: 'money'
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
    type: 'money'
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
    label: `体积合计(${VolumeUnit})`,
    type: 'volume'
  },
  {
    field: 'totalGoodsValue',
    label: '货值合计',
    type: 'money',
    precision: moneyTotalPrecision
  },
  {
    field: 'totalPurchase',
    label: '采购合计',
    type: 'money',
    precision: moneyTotalPrecision
  },
  {
    field: 'totalStockCost',
    label: '库存合计',
    type: 'money',
    precision: moneyTotalPrecision
  },
  {
    field: 'totalVatRefund',
    label: '退税合计',
    type: 'money',
    precision: moneyTotalPrecision
  },
  {
    field: 'totalQuantity',
    label: '数量合计',
    type: 'num'
  },
  {
    field: 'orderGrossProfit',
    label: '订单毛利',
    type: 'money',
    precision: moneyTotalPrecision
  },
  {
    label: '毛利率(%)',
    field: 'grossProfitMargin',
    slotName: 'grossProfitMargin'
  },
  {
    label: '销售总金额(原币种)',
    field: 'totalAmountThisCurrency',
    type: 'money',
    precision: moneyTotalPrecision
  },
  {
    label: '销售总金额(RMB)',
    field: 'totalAmount',
    type: 'money',
    precision: moneyTotalPrecision
  },
  {
    label: '销售总金额(美金)',
    field: 'totalAmountUsd',
    type: 'money',
    precision: moneyTotalPrecision
  },
  {
    label: '收款合计',
    field: 'collectionTotal',
    type: 'money',
    precision: moneyTotalPrecision
  },
  {
    label: '已收款金额',
    field: 'receivedAmount',
    type: 'money',
    precision: moneyTotalPrecision
  },
  {
    label: '未收款金额',
    field: 'unReceivedAmount',
    type: 'money',
    precision: moneyTotalPrecision
  }
])

const subItemTableColumns = reactive([
  {
    field: 'calculationType',
    label: '加/减项',
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.CALCULATION_TYPE, row?.calculationType) || '-'
    }
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
])

const eplusTableSchema = {
  getListApi: async () => {
    return {
      list: exportSaleContractDetail.value.collectionPlanList,
      total: 0
    }
  },
  columns: [
    {
      prop: 'step',
      parent: true,
      label: '步骤',
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.COLLECTION_PLAN_STEP, row?.step) || '-'
      }
    },
    {
      prop: 'dateType',
      parent: true,
      label: '起始点',
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.SETTLEMENT_DATE_TYPE, row?.dateType) || '-'
      }
    },
    {
      prop: 'startDate',
      parent: true,
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
      prop: 'days',
      parent: true,
      label: '天数'
    },
    {
      prop: 'expectedReceiptDate',
      parent: true,
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
      prop: 'collectionRatio',
      parent: true,
      label: '收款比例%'
    },
    {
      prop: 'realCollectionRatio',
      parent: true,
      label: '实际收款比例%'
    },
    {
      prop: 'receivableAmount',
      parent: true,
      label: '应收金额',
      formatter: (row) => {
        return <EplusMoneyLabel val={row.receivableAmount}></EplusMoneyLabel>
      }
    },
    {
      prop: 'receivedAmount',
      parent: true,
      label: '实收金额',
      formatter: (row) => {
        return <EplusMoneyLabel val={row.receivedAmount}></EplusMoneyLabel>
      }
    },
    {
      prop: 'controlPurchaseFlag',
      parent: true,
      label: '是否控制采购',
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.IS_INT, row?.controlPurchaseFlag) || '-'
      }
    },
    {
      prop: 'controlShipmentFlag',
      parent: true,
      label: '是否控制出运',
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.IS_INT, row?.controlShipmentFlag) || '-'
      }
    },
    {
      prop: 'exeStatus',
      parent: true,
      label: '状态',
      formatter: (row) => {
        return getDictLabel(DICT_TYPE.EXECUTE_STATUS, row?.exeStatus) || '-'
      }
    },
    {
      prop: 'invoiceCode',
      label: '出运发票号'
    },
    {
      prop: 'settlementCode',
      label: '收款单号'
    },
    {
      prop: 'date',
      label: '实际收款日期',
      formatter: (row) => {
        if (row.date) {
          return formatDate(row.date, 'YYYY-MM-DD')
        } else {
          return '-'
        }
      }
    },
    {
      prop: 'amount',
      label: '收款金额',
      formatter: formatMoneyColumn()
    },
    {
      prop: 'user',
      label: '收款人',
      formatter: (row) => {
        if (row.user?.nickname) {
          return row.user?.nickname
        } else {
          return '-'
        }
      }
    }
  ],
  showSettings: false
}

const AgainLockStockRef = ref()
const handleReassignInventory = (row) => {
  AgainLockStockRef.value.open({
    saleContractItemId: row.id,
    saleContractCode: exportSaleContractDetail.value.code,
    companyIdList: getCompanyIdList(exportSaleContractDetail.value.companyPath),
    skuCode: row.skuCode,
    realLockQuantity: row.realLockQuantity
  })
}

const unitRadio = ref('metric')
const splitRadio = ref('after')
//产品信息table
let productTableColumns = reactive([
  {
    field: 'mainPicture',
    label: '图片',
    fixed: 'left',
    minWidth: '60px',
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
    fixed: 'left',
    minWidth: columnWidth.l
  },
  {
    field: 'name',
    label: '中文名称',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <EplusSkuName
            name={row.name}
            type={row.skuType}
          />
        )
      }
    }
  },
  {
    field: 'quantity',
    label: '数量',
    minWidth: columnWidth.m,
    sortable: true,
    formatter: formatNumColumn()
  },
  {
    field: 'unitPrice',
    label: '销售单价',
    minWidth: columnWidth.l,
    sortable: true,
    formatter: formatMoneyColumn()
  },
  {
    field: 'totalSaleAmount',
    label: '总金额',
    sortable: true,
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'withTaxPriceRemoveFree',
    label: '采购含税单价',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'purchaseTotalAmount',
    label: '采购总金额',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'skuCode',
    label: '产品编码',
    minWidth: '250px',
    sortable: true,
    slots: {
      default: (data) => {
        const { row } = data
        if (row.skuDeletedFlag) {
          return (
            <div>
              <span
                onClick={() => openProductDetail(row)}
                style="color:rgb(0, 91, 245);cursor:pointer;"
              >
                <SkuCodeCom
                  code={row.skuCode}
                  flag={row.splitFlag}
                />
              </span>
              <el-tag
                type="info"
                effect="dark"
                size="small"
                class="ml10px"
              >
                被修改
              </el-tag>
            </div>
          )
        } else {
          return (
            <div>
              <span
                onClick={() => openProductDetail(row)}
                style="color:rgb(0, 91, 245);cursor:pointer;"
              >
                <SkuCodeCom
                  code={row.skuCode}
                  flag={row.splitFlag}
                />
              </span>
            </div>
          )
        }
      }
    }
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编码',
    minWidth: '250px'
  },
  {
    field: 'freeFlag',
    label: '是否赠品',
    width: columnWidth.l,
    formatter: formatDictColumn(DICT_TYPE.CONFIRM_TYPE)
  },
  {
    field: 'purchaseFreeQuantity',
    label: '采购赠品数量',
    width: columnWidth.l,
    formatter: (row) => {
      return row.purchaseFreeQuantity || 0
    }
  },
  {
    field: 'description',
    label: '中文说明',
    minWidth: columnWidth.xl,
    showOverflowTooltip: false,
    formatter: (row) => {
      return <EplusDesc info={row.description} />
    }
  },
  {
    field: 'descriptionEng',
    label: '英文说明',
    minWidth: columnWidth.xl,
    showOverflowTooltip: false,
    formatter: (row) => {
      return <EplusDesc info={row.descriptionEng} />
    }
  },
  {
    field: 'orderGrossProfit',
    label: '订单毛利',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return <EplusMoneyLabel val={row.orderGrossProfit} />
      }
    }
    // formatter: formatMoneyColumn()
  },
  {
    field: 'orderGrossProfitRate',
    label: '毛利率(%)',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        if (row.orderGrossProfitRate < 0) {
          return (
            <span style="color:red">
              {row.orderGrossProfitRate ? Number(row.orderGrossProfitRate * 100).toFixed(2) : '-'}
            </span>
          )
        } else {
          return row.orderGrossProfitRate ? Number(row.orderGrossProfitRate * 100).toFixed(2) : '-'
        }
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
    formatter: formatMoneyColumn()
  },
  {
    field: 'commissionType',
    label: '佣金类型',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.COMMISSION_TYPE, row?.commissionType) || '-'
    }
  },
  {
    field: 'commissionRate',
    label: '佣金比例(%)',
    minWidth: columnWidth.l
  },
  {
    field: 'commissionAmount',
    label: '佣金金额',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'commissionSubTotal',
    label: '是否扣减总金额',
    minWidth: '140px',
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.IS_INT, row?.commissionSubTotal) || '-'
    }
  },
  {
    field: 'purchaseCurrency',
    label: '采购币种',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.CURRENCY_TYPE, row?.purchaseCurrency) || '-'
    }
  },
  // {
  //   field: 'needPurQuantity',
  //   label: '待采购数量',
  //   minWidth: columnWidth.l,
  //   formatter: formatNumColumn()
  // },
  {
    field: 'purchaseQuantity',
    label: '采购数量',
    width: columnWidth.l,
    formatter: formatNumColumn()
  },
  {
    field: 'purchasePackagingPrice',
    label: '包装价',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  },

  // {
  //   field: 'currentLockQuantity',
  //   label: '锁定库存（原锁定数量）',
  //   minWidth: columnWidth.l,
  //   formatter: formatNumColumn()
  // },
  {
    field: 'currentLockQuantity',
    label: '锁定库存',
    width: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <LockCom
            data={{
              saleContractItemId: row.id,
              saleContractCode: exportSaleContractDetail.value.code,
              companyIdList: getCompanyIdList(exportSaleContractDetail.value.companyPath),
              skuCode: row.skuCode,
              realLockQuantity: row.currentLockQuantity,
              onlyLockFlag: 1
            }}
          />
        )
      }
    }
  },
  {
    field: 'stockLockPrice',
    label: '锁定库存单价',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'stockLockTotalPrice',
    label: '锁定库存总金额',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  },

  {
    field: 'nameEng',
    label: '英文名称',
    minWidth: columnWidth.l
  },
  {
    field: 'unit',
    label: '计量单位',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.MEASURE_UNIT, row?.commissionType) || '-'
    }
  },
  {
    field: 'qtyPerInnerbox',
    label: '内盒装量',
    minWidth: columnWidth.l,
    formatter: formatNumColumn()
  },
  {
    field: 'qtyPerOuterbox',
    label: '外箱装量',
    minWidth: columnWidth.l,
    formatter: formatNumColumn()
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
    width: columnWidth.m,
    formatter: (row) => {
      return <span>{formatNum(row.boxCount * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'volume',
    label: `总体积(${VolumeUnit})`,
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        if (row.volume) {
          return formatNum(Number(row.volume) / 1000000, volPrecision)
        } else {
          return '-'
        }
      }
    }
  },
  {
    field: 'reorderFlag',
    label: '是否翻单',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.CONFIRM_TYPE, row?.reorderFlag) || '-'
    }
  },
  {
    field: 'outerboxGrossweight',
    label: '外箱毛重',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return <span>{getOuterbox(row, 'grossweight', unitRadio.value)}</span>
    }
  },
  {
    field: 'outerboxNetweight',
    label: '外箱净重',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return <span>{getOuterbox(row, 'netweight', unitRadio.value)}</span>
    }
  },
  {
    field: 'venderName',
    label: '厂商名称',
    minWidth: columnWidth.l
  },
  {
    field: 'purchaseUser',
    label: '采购员',
    minWidth: columnWidth.l,
    slots: {
      default: (data) => {
        const { row } = data
        return row.purchaseUser?.nickname ? row.purchaseUser.nickname : '-'
      }
    }
  },
  {
    field: 'billStatus',
    label: '入库地点',
    minWidth: columnWidth.l,
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.SALE_ITEM_BILL_STATUS, row?.billStatus) || ''
    }
  },
  {
    field: 'venderDeliveryDate',
    label: '工厂交期',
    minWidth: columnWidth.l,
    formatter: (row) => {
      if (row.venderDeliveryDate) {
        return formatDate(row.venderDeliveryDate, 'YYYY-MM-DD')
      } else {
        return '-'
      }
    }
  },

  {
    field: 'inspectionFee',
    label: '验货费',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'packageTypeName',
    label: '包装方式',
    minWidth: columnWidth.l
  },
  {
    field: 'trailerFee',
    label: '拖柜费',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'insuranceFee',
    label: '保险费',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'platformFee',
    label: '平台费',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'forecastTotalCost',
    label: '预估总费用',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'innerCalcPrice',
    label: '定价差价',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  },
  {
    field: 'sinosureFee',
    label: '中信保费用',
    minWidth: columnWidth.l,
    formatter: formatMoneyColumn()
  },

  {
    field: 'action',
    label: '操作',
    width: columnWidth.l,
    fixed: 'right',
    slots: {
      default: (data) => {
        const { row } = data
        if (
          row.reLockFlag == 1 &&
          Number(row.quantity) - Number(row.shippedQuantity) > 0 &&
          checkPermi([`${permiPrefix.value}:re-lock`])
        ) {
          return (
            <el-button
              link
              type="primary"
              onClick={() => {
                handleReassignInventory(row)
              }}
            >
              重新分配库存
            </el-button>
          )
        }
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
const handleUpdate = async () => {
  await getInfo()
}

const getInfo = async () => {
  loading.value = true
  try {
    let obj: any = null

    if (props.id || pageFlag.value) {
      // 普通详情 contractType => 1 外销  2内销
      obj =
        contractType.value == 1
          ? await ExportSaleContractApi.getExportSaleContract({ id: pageId.value })
          : contractType.value == 2
            ? await DomesticSaleContractApi.getDomesticSaleContract({ id: pageId.value })
            : await FactorySaleContractApi.getFactoryContract({ id: pageId.value })
    } else {
      // 审核详情
      obj =
        contractType.value == 1
          ? await ExportSaleContractApi.getAuditExportSaleContract({ id: pageId.value })
          : contractType.value == 2
            ? await DomesticSaleContractApi.getAuditDomesticSaleContract({ id: pageId.value })
            : await FactorySaleContractApi.getAuditFactoryContract({ id: pageId.value })
    }

    if (obj.companyPath) {
      obj.companyPathName = getCompanyPathNameFromObj(obj.companyPath)
    }

    //采购总金额计算 采购含税单价 * 待采购数量
    obj.children?.forEach((item: any) => {
      getRealVal(item)
      console.log(item.purchaseWithTaxPrice)
      item.purchaseTotalAmount = { amount: 0, currency: '' }
      item.purchaseTotalAmount.amount =
        Number(item.purchaseWithTaxPrice.amount) * Number(item.purchaseQuantity)
      item.purchaseTotalAmount.currency = item.purchaseWithTaxPrice.currency
    })
    obj.groupChildren?.forEach((item: any) => {
      getRealVal(item)
      item.purchaseTotalAmount = { amount: 0, currency: '' }
      item.purchaseTotalAmount.amount =
        Number(item.purchaseWithTaxPrice.amount) * Number(item.purchaseQuantity)
      item.purchaseTotalAmount.currency = item.purchaseWithTaxPrice.currency
    })

    exportSaleContractDetail.value = obj
    //审核通过显示打印按钮
    setPrintBtns()
    backCreatePlanParams(exportSaleContractDetail.value)
  } finally {
    loading.value = false
  }
}

const capitalizeFirstLetter = (str) => {
  return 'real' + str.charAt(0).toUpperCase() + str.slice(1)
}
const getRealVal = (item) => {
  let realFieldList = [
    'boxCount',
    'lockQuantity',
    'prchaseCurrency',
    'purchaseQuantity',
    'purchaseWithTaxPrice',
    'specificationList',
    'splitBoxFlag',
    'taxRefundPrice',
    'taxRefundRate',
    'venderCode',
    'venderId',
    'venderName'
  ]
  realFieldList.forEach((f) => {
    if (
      isObject(item[capitalizeFirstLetter(f)]) &&
      Object.prototype.hasOwnProperty.call(item[capitalizeFirstLetter(f)], 'currency')
    ) {
      item[f] = {
        amount:
          item[capitalizeFirstLetter(f)]?.amount > 0
            ? item[capitalizeFirstLetter(f)]?.amount
            : item[f].amount,
        currency: item[f]?.currency
      }
    } else if (f === 'purchaseQuantity') {
      item[f] = item[capitalizeFirstLetter(f)] || item.needPurQuantity
    } else {
      item[f] = item[capitalizeFirstLetter(f)] || item[f]
    }
  })
  //库存单独处理
  item.currentLockQuantity = item.realLockQuantity || item.currentLockQuantity || 0
}

// 拿到数据以后，将转出运计划的数据返给详情组件
const backCreatePlanParams = (data) => {
  let idsArray = data.children
    ?.filter((obj) => Number(obj.quantity) - Number(obj.shippedQuantity) > 0)
    .map((item) => item.id)
  planParams.value = {
    companyId: data.foreignTradeCompanyId,
    companyName: data.foreignTradeCompanyName,
    companyTitle: data.companyTitle,
    childrenIdList: idsArray?.join(',')
  }
  emit('create-plan', planParams.value)
}

const changeStore = globalStore()

const reportCode = ref('export-sale-contract')
const reportDOListRef = ref()

const TtnoticeDiaRef = ref()
//--打印按钮设置
const setPrintBtns = async () => {
  let params = {
    reportCode: reportCode.value,
    companyId: exportSaleContractDetail.value.companyId
  }
  //获取特定打印类型
  reportDOListRef.value = await ReportApi.getCompanySpecificReport(params)
  clearDialogActions()
  if (
    exportSaleContractDetail.value.confirmFlag == 0 &&
    checkPermi([`${permiPrefix.value}-change:confirm`])
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          OtherActionComRef.value.confirmChange(exportSaleContractDetail.value, contractType.value)
        }}
        key="confirm"
      >
        确认变更
      </el-button>
    )
  }
  //获取特定打印字典
  if (
    exportSaleContractDetail.value.confirmFlag !== 0 &&
    checkPermi([`${permiPrefix.value}:print`])
  ) {
    if (reportDOListRef.value?.length) {
      const buttons = [
        <el-button
          onClick={async () => {
            let params = {
              reportCode: reportCode.value
            }
            await print(params)
          }}
          key="payment"
        >
          打印
        </el-button>,
        <el-button
          onClick={() => {
            openPrintDia()
          }}
          key="payment"
        >
          按条件打印
        </el-button>
      ]

      // 外销合同和外币采购合同增加hsCode打印 - 放在打印按钮后面
      if (contractType.value === 1 || contractType.value === 3) {
        buttons.push(
          <el-button
            onClick={async () => {
              await printHsCode()
            }}
            key="printHsCode"
          >
            hsCode打印
          </el-button>
        )
      }

      updateDialogActions(...buttons)
    } else {
      const buttons = [
        <el-button
          onClick={async () => {
            let params = {
              reportCode: reportCode.value
            }
            await print(params)
          }}
          key="payment"
        >
          打印
        </el-button>
      ]

      // 外销合同和外币采购合同增加hsCode打印 - 放在打印按钮后面
      if (contractType.value === 1 || contractType.value === 3) {
        buttons.push(
          <el-button
            onClick={async () => {
              await printHsCode()
            }}
            key="printHsCode"
          >
            hsCode打印
          </el-button>
        )
      }

      updateDialogActions(...buttons)
    }
  }

  if (
    exportSaleContractDetail.value.status == SaleContractStatusEnum.INSHIPPING.status &&
    exportSaleContractDetail.value.confirmFlag !== 0 &&
    exportSaleContractDetail.value.changeStatus !== 2 &&
    checkPermi([`${permiPrefix.value}:to-shipment-plan`]) &&
    contractType.value === 1
  ) {
    updateDialogActions(
      <el-button
        onClick={() => {
          handleCreatePlan()
        }}
        key="payment"
      >
        转出运计划
      </el-button>
    )
  }
  if (
    exportSaleContractDetail.value.status == SaleContractStatusEnum.INSHIPPING.status &&
    exportSaleContractDetail.value.confirmFlag !== 0 &&
    checkPermi([`${permiPrefix.value}:to-notice`]) &&
    contractType.value === 2
  ) {
    updateDialogActions(
      <el-button
        onClick={() => {
          TtnoticeDiaRef.value.open(exportSaleContractDetail.value)
        }}
        key="payment"
      >
        转出库通知单
      </el-button>
    )
  }
  if (
    [
      SaleContractStatusEnum.INSINGBACK.status,
      SaleContractStatusEnum.INPURCHASE.status,
      SaleContractStatusEnum.INSHIPPING.status
    ].includes(exportSaleContractDetail.value?.status) &&
    checkPermi([`${permiPrefix.value}:change`]) &&
    exportSaleContractDetail.value.confirmFlag !== 0 &&
    exportSaleContractDetail.value?.changeStatus !== 2
  ) {
    updateDialogActions(
      <el-button
        onClick={() => {
          changeStore.setChangeType('change')
          OtherActionComRef.value.handleChange(
            exportSaleContractDetail.value.id,
            contractType.value
          )
        }}
        key="contractChange"
      >
        变更
      </el-button>
    )
  }

  if (
    [SaleContractStatusEnum.INPURCHASE.status].includes(exportSaleContractDetail.value?.status) &&
    checkPermi([`${permiPrefix.value}:to-purchase-plan`]) &&
    exportSaleContractDetail.value.confirmFlag !== 0 &&
    exportSaleContractDetail.value.changeStatus !== 2
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await handleToPurchasePlan()
        }}
        key="toPurchasePlan"
      >
        下推采购计划
      </el-button>
    )
  }

  if (
    [SaleContractStatusEnum.INSINGBACK.status].includes(exportSaleContractDetail.value?.status) &&
    checkPermi([`${permiPrefix.value}:sign-back`]) &&
    exportSaleContractDetail.value.confirmFlag !== 0 &&
    exportSaleContractDetail.value.changeStatus !== 2
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await handleSignBack()
        }}
        key="signBack"
      >
        回签
      </el-button>
    )
  }
  if (
    exportSaleContractDetail.value.confirmFlag !== 0 &&
    checkPermi([`${permiPrefix.value}:export`])
  ) {
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
  }

  // 外销合同和外币采购合同增加hsCode导出
  if (
    (contractType.value === 1 || contractType.value === 3) &&
    exportSaleContractDetail.value.confirmFlag !== 0 &&
    checkPermi([`${permiPrefix.value}:export`])
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          await exportHsCodeExcel()
        }}
        key="exportHsCode"
      >
        hsCode导出
      </el-button>
    )
  }

  if (
    exportSaleContractDetail.value.confirmFlag !== 0 &&
    checkPermi([`${permiPrefix.value}:create`])
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          OtherActionComRef.value.handleCopy(exportSaleContractDetail.value.id, contractType.value)
        }}
        key="copy"
      >
        复制
      </el-button>
    )
  }
  if (
    [SaleContractStatusEnum.INSUBMITTED.status].includes(exportSaleContractDetail.value?.status) &&
    exportSaleContractDetail.value.confirmFlag !== 0 &&
    checkPermi([`${permiPrefix.value}:delete`])
  ) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          handleDel()
        }}
        key="delete"
      >
        删除
      </el-button>
    )
  }
}

const handleDel = async () => {
  await message.confirm('确认删除吗？')
  contractType.value == 1
    ? await ExportSaleContractApi.deleteExportSaleContract(exportSaleContractDetail.value?.id)
    : contractType.value == 2
      ? await DomesticSaleContractApi.deleteDomesticSaleContract(exportSaleContractDetail.value?.id)
      : await FactorySaleContractApi.deleteFactoryContract(exportSaleContractDetail.value?.id)
  message.success('删除成功')
  eplusDetailRef.value.close()
}

const SignBackFormRef = ref()

const handleSignBack = () => {
  let obj = {
    id: exportSaleContractDetail.value.id,
    code: exportSaleContractDetail.value.code,
    createTime: exportSaleContractDetail.value.createTime
  }
  SignBackFormRef.value.open('回签', obj)
}

const handleToPurchasePlan = async () => {
  await message.confirm('确认下推采购计划吗？')
  const res = await ExportSaleContractApi.toPurchasePlan({
    saleContractId: exportSaleContractDetail.value.id
  })
  message.notifyPushSuccess('下推采购计划成功', res, 'purchase-plan')
  getInfo()
}

const exportWord = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    const data = await ExportSaleContractApi.exportSaleContractWord(contractType.value, {
      id: exportSaleContractDetail.value?.id,
      reportCode: reportCode.value,
      companyId: exportSaleContractDetail.value.companyId
    })
    if (data && data.size) {
      download.word(data, exportSaleContractDetail.value.code + '.docx')
    }
  } catch {}
  return
}
const exportExcel = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    let reportCode =
      contractType.value === 1
        ? 'export-sale-contract-detail'
        : contractType.value === 2
          ? 'domestic-sale-contract-detail'
          : 'export-sale-contract-detail'
    let exportType =
      contractType.value === 1 ? 'detail' : contractType.value === 2 ? 'domestic' : 'factory'
    // 发起导出
    const data = await ExportSaleContractApi.exportSaleContract({
      id: exportSaleContractDetail.value?.id,
      reportCode: reportCode,
      exportType: exportType
    })
    if (data && data.size) {
      download.excel(data, exportSaleContractDetail.value.code + '.xlsx')
    }
  } catch {}
  return
}

const exportHsCodeExcel = async () => {
  try {
    // 导出的二次确认
    await message.exportConfirm()
    // 发起导出
    const data = await ExportSaleContractApi.exportSaleContract({
      id: exportSaleContractDetail.value?.id,
      reportCode: 'export-sale-contract-detail-hscode',
      exportType: 'detailHsCode'
    })
    if (data && data.size) {
      download.excel(data, exportSaleContractDetail.value.code + '-海关编码.xlsx')
    }
  } catch {}
  return
}
const PrintDiaRef = ref()
const openPrintDia = () => {
  PrintDiaRef.value?.open(reportDOListRef.value)
}

const handleCreatePlan = () => {
  OtherActionComRef.value.createShipmentPlan(exportSaleContractDetail.value)
}

const print = async (params) => {
  let companyId = exportSaleContractDetail.value.companyId
  const url = await ExportSaleContractApi.print(contractType.value, {
    id: exportSaleContractDetail.value?.id,
    companyId: companyId,
    ...params
  })
  openPdf(url)
}

const printHsCode = async () => {
  let companyId = exportSaleContractDetail.value.companyId
  const url = await ExportSaleContractApi.print(contractType.value, {
    id: exportSaleContractDetail.value?.id,
    companyId: companyId,
    reportCode: reportCode.value,
    printType: 'hsCode'
  })
  openPdf(url)
}
const handlePrintSure = async (reportId) => {
  let params = {
    reportCode: reportCode.value,
    reportId: reportId
  }
  await print(params)
}

const saveDesignDraft = async () => {
  await ExportSaleContractApi.updateDesign({
    id: exportSaleContractDetail.value.id,
    designDraftList: exportSaleContractDetail.value.designDraftList
  })
  message.success('保存成功')
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
      'quantity',
      'totalSaleAmount',
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
const permiPrefix = ref('')
const tableMaxHeight = ref(0)
onMounted(async () => {
  if (useRoute().params.id) {
    pageId.value = useRoute().params.id
    pageFlag.value = true
    contractType.value = Number(useRoute().query.contractType)
  } else {
    pageFlag.value = false
    if (query.id) {
      showProcessInstanceTaskListFlag.value = false
      outDialogFlag.value = true
      pageId.value = query.id
      contractType.value =
        query.processDefinitionKey === 'sms_export_sale_contact'
          ? 1
          : query.processDefinitionKey === 'sms_domestic_sale_contact'
            ? 2
            : 3
    }
    if (props.id) {
      showProcessInstanceTaskListFlag.value = true
      outDialogFlag.value = false
      pageId.value = props.id
      contractType.value = Number(props.type)
    }
  }

  permiPrefix.value =
    contractType.value == 1
      ? 'sms:export-sale-contract'
      : contractType.value == 2
        ? 'sms:domestic-sale-contract'
        : 'sms:factory-sale-contract'

  reportCode.value =
    contractType.value === 1
      ? 'export-sale-contract'
      : contractType.value === 2
        ? 'domestic_sale_contract'
        : 'export-sale-contract'

  exportSaleContractSchemas.find((item) => item.field === 'companyPathName')!.disabled =
    contractType.value == 2
  exportSaleContractSchemas.find((item) => item.field === 'settlementTermType')!.disabled =
    contractType.value == 2

  await getInfo()
  setTimeout(() => {
    tableMaxHeight.value = eplusDetailRef.value.bottomHeight - 90
  }, 1000)
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
  margin: 2px auto 0;
  height: 2px;
  background-color: #fff;
  border-radius: 5px;
}

.text-underline.on {
  background-color: #409eff;
}
</style>
