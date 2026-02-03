<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :localSaveAction="{
      handler: () => localSaveForm()
    }"
    scrollFlag
  >
    <template #pageTop>
      <eplus-form-items
        title="基本信息"
        :formSchemas="basicSchemas"
      >
        <template #custCode>
          <!-- <el-input v-model="formData.custName" /> -->
          <eplus-input-search-select
            v-if="showFlag"
            v-model="formData.custCode"
            :api="PurchasePlanApi.getCustSimpleList"
            :params="{
              pageSize: 100,
              pageNo: 1,
              internalFlag: currContractType === 1 ? 0 : undefined
            }"
            keyword="nameCode"
            :disabled="currItemType === 'change' || mode === 'edit'"
            label="name"
            :formatLabel="
              (item) => {
                return `${item.name}(${item.code})`
              }
            "
            value="code"
            class="!w-100%"
            placeholder="请选择"
            :clearable="false"
            @change-emit="(...$event) => getCustName($event)"
            :defaultObj="custDefaultObj"
          />
        </template>
        <template #companyPathName>
          <el-select
            v-model="formData.companyPathName"
            value-key="id"
            clearable
            style="width: 100%"
            @change="changecompanyPathName"
          >
            <el-option
              v-for="item in companyPathListRef"
              :key="item.id"
              :label="item.path"
              :value="item.path"
            />
          </el-select>
        </template>
        <template #companyName>
          <el-input
            v-model="formData.companyName"
            disabled
          />
        </template>
        <template #settlementId>
          <el-select
            v-model="formData.settlementId"
            clearable
            style="width: 100%"
            @change="changeSettlement"
          >
            <el-option
              v-for="item in settlementListData"
              :key="item.id"
              :label="item.nameEng"
              :value="item.id"
            />
          </el-select>
        </template>
        <template #annex>
          <UploadList
            ref="UploadListRef"
            :fileList="formData.annex"
            @success="
              (data) => {
                formData.annex = data
              }
            "
          />
        </template>
        <template #designDraftList>
          <UploadList
            :fileList="formData.designDraftList"
            @success="
              (data) => {
                formData.designDraftList = data
              }
            "
          />
        </template>

        <template #collectedCustId>
          <el-select
            v-model="formData.collectedCustId"
            clearable
            filterable
            style="width: 100%"
            @change="getCollectedCustName"
          >
            <el-option
              v-for="item in companyCustLinkRef"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </template>
        <template #receiveCustId>
          <el-select
            v-model="formData.receiveCustId"
            clearable
            filterable
            style="width: 100%"
            @change="getReceiveCustName"
          >
            <el-option
              v-for="item in companyCustLinkRef"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </template>
      </eplus-form-items>

      <eplus-form-items
        title="运输信息"
        :formSchemas="transportSchemas"
      >
        <template #destinationPortId>
          <eplus-port-select
            v-if="formData.tradeCountryId"
            :params="{
              pageSize: 100,
              pageNo: 1,
              status: PortStatusStatusEnum.NORMAL.status,
              countryId: formData.tradeCountryId
            }"
            v-model="formData.destinationPortId"
            @change-emit="changeDestinationPort"
          />
          <el-select
            v-else
            class="!w-100%"
          />
        </template>
        <template #departurePortId>
          <eplus-port-select
            v-if="formData.departureCountryId"
            :params="{
              pageSize: 100,
              pageNo: 1,
              status: PortStatusStatusEnum.NORMAL.status,
              countryId: formData.departureCountryId
            }"
            v-model="formData.departurePortId"
            @change-emit="changeDeparturePort"
          />
          <el-select
            v-else
            class="!w-100%"
          />
        </template>
        <template #custDeliveryDate>
          <el-date-picker
            v-model="formData.custDeliveryDate"
            clearable
            valueFormat="x"
            type="date"
            style="width: 100%"
            @change="() => custDeliveryDateChange()"
          />
        </template>
      </eplus-form-items>

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
              style="width: 50%"
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
              style="width: 40%"
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
              style="width: 65%"
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
              style="width: 30%"
            ></div
          ></span>
        </div>
      </div>
      <eplus-form-items
        title=""
        :formSchemas="sumSchemas"
        v-show="feeActive === 'first'"
      >
        <template #orderGrossProfit>
          <!-- <el-input
            v-model="formData.orderGrossProfitFormat"
            disabled
          >
            <template #append>
              <span style="width: 50px">
                {{ formData.orderGrossProfit.currency }}
              </span>
            </template>
          </el-input> -->
          <span
            class="color-red"
            v-if="formData.orderGrossProfit.amount < 0"
          >
            <EplusMoneyLabel :val="formData.orderGrossProfit" />
          </span>
          <EplusMoneyLabel
            :val="formData.orderGrossProfit"
            v-else
          />
        </template>
        <template #grossProfitMargin>
          <span
            v-if="formData.grossProfitMarginFormat < 0"
            class="color-red"
          >
            {{ formData.grossProfitMarginFormat + ' %' }}
          </span>
          <span v-else>
            {{ formData.grossProfitMarginFormat + ' %' }}
          </span>
        </template>
      </eplus-form-items>
      <eplus-form-items
        title=""
        :formSchemas="platFormFeeSchemas"
        v-show="feeActive === 'second'"
      />
      <eplus-form-items
        title=""
        :formSchemas="feeSchemas"
        v-show="feeActive === 'third'"
      >
        <template #trailerFee>
          <EplusMoney
            v-model:amount="formData.trailerFee.amount"
            v-model:currency="formData.trailerFee.currency"
            @change="changeTrailerFee(formData.trailerFee.amount)"
          />
        </template>
      </eplus-form-items>
      <eplus-form-items
        title=""
        :formSchemas="otherFeeSchemas"
        v-show="feeActive === 'fourth'"
      >
        <template #insuranceFee>
          <EplusMoney
            v-model:amount="formData.insuranceFee.amount"
            v-model:currency="formData.insuranceFee.currency"
            :currencyDisabled="true"
            @change="changeInsuranceFee(formData.insuranceFee.amount)"
          />
        </template>
        <template #inspectionFee>
          <EplusMoney
            v-model:amount="formData.inspectionFee.amount"
            v-model:currency="formData.inspectionFee.currency"
          />
        </template>
      </eplus-form-items>
    </template>
    <template #pageBottomTabs>
      <el-tabs
        v-model="activeName"
        class="demo-tabs mb20px"
        @tab-click="handleTabsClick"
      >
        <el-tab-pane
          label="销售明细"
          name="first"
        />

        <el-tab-pane
          label="加减项"
          name="second"
        />

        <el-tab-pane
          label="收款计划"
          name="third"
        />
      </el-tabs>
    </template>
    <template #pageBottom>
      <el-scrollbar always>
        <div v-show="activeName === 'first'">
          <eplus-form-items
            title=""
            :formSchemas="collectionSchemas"
          >
            <template #children>
              <PurchaseProducts
                :contractType="currContractType"
                :producedFlag="producedFlag"
                :companyIdList="companyIdList"
                :formData="formData"
                :mode="props.mode"
                ref="saleContractItemRef"
                @open="openProductDetail"
                :loading="!parentDataReady"
                @loaded="handleChildLoaded('PurchaseProducts')"
                :maxHeight="tableMaxHeight - 115"
                :rateList="rateList"
              />
            </template>
          </eplus-form-items>
        </div>
        <div v-show="activeName === 'second'">
          <eplus-form-items
            title=""
            :formSchemas="addSubItemListSchemas"
          >
            <template #addSubItemList>
              <subItemTable
                :formData="formData"
                ref="addSubItemListRef"
                :required="true"
                :loading="!parentDataReady"
                @loaded="handleChildLoaded('AddSubItemTable')"
              />
            </template>
          </eplus-form-items>
        </div>
        <div v-show="activeName === 'third'">
          <eplus-form-items
            title=""
            :formSchemas="collectionPlanListSchemas"
          >
            <template #collectionPlanList>
              <collectionPlanTable
                :formData="formData"
                ref="collectionPlanListRef"
                :required="true"
                :loading="!parentDataReady"
                @loaded="handleChildLoaded('CollectionPlanTable')"
              />
            </template>
          </eplus-form-items>
        </div>
      </el-scrollbar>
    </template>
  </eplus-form>
  <ContractInfo ref="ContractInfoRef" />
  <ChangeTips
    ref="changeTipsRef"
    @submit-change="submitChange"
  />
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import PurchaseProducts from '../components/PurchaseProducts.vue'
import * as ExportSaleContractApi from '@/api/sms/saleContract/export'
import * as DomesticSaleContractApi from '@/api/sms/saleContract/domestic'
import * as FactorySaleContractApi from '@/api/sms/saleContract/factory'
import * as ExportSaleContractChangeApi from '@/api/sms/saleContract/export/change'
import * as DomesticSaleContractChangeApi from '@/api/sms/saleContract/domestic/change'
import * as UserApi from '@/api/system/user'
import UploadList from '@/components/UploadList/index.vue'
import * as PurchasePlanApi from '@/api/scm/purchasePlan'
import { useCountryStore } from '@/store/modules/countrylist'
import { PortStatusStatusEnum } from '@/utils/constants'
import subItemTable from '../components/AddSubItemTable.vue'
import collectionPlanTable from '../components/CollectionPlanTable.vue'
import { useRateStore } from '@/store/modules/rate'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import {
  getCompanyIdList,
  getCompanyPathNameFromObj,
  getInnerCustPath
} from '@/utils/companyPathUtils'
import {
  amountListFormat,
  formatNum,
  formatterPrice,
  formatTime,
  getAmountListSumRmb,
  getCurrency
} from '@/utils/index'
import { cloneDeep } from 'lodash-es'
import { useUserStore } from '@/store/modules/user'
import ContractInfo from '../components/ContractInfo.vue'
import { globalStore } from '@/store/modules/globalVariable'
import ChangeTips from '@/components/ChangeTips/index.vue'
import { isValidArray } from '@/utils/is'
import {
  moneyPrecision,
  moneyTotalPrecision,
  volPrecision,
  VolumeUnit,
  weightPrecision
} from '@/utils/config'
import EplusMoneyLabel from '@/components/EplusMoney/src/EplusMoneyLabel.vue'
import { getcompanySimpleList } from '@/api/common'
import { getDateRate } from '@/api/finance/custClaim'

defineOptions({ name: 'ExportSaleContractForm' })
const userInfo = useUserStore()
const changeTipsRef = ref()
const cloneRate = cloneDeep(useRateStore().rateList)
let rateList = reactive(cloneRate)

const setRateList = async () => {
  if (props.mode === 'create' || props.mode === 'copy') {
    rateList = cloneRate
  } else {
    let res = await getDateRate(formatTime(formData.saleContractDate || Date.now(), 'yyyy-MM-dd'))
    if (isValidArray(res)) {
      res.forEach((item) => {
        rateList[item.dailyCurrName] = item.dailyCurrRate
      })
    }
  }
  formData.exchangeRate = rateList[formData.currency]
  formData.usdRate = rateList['USD']
}
const message = useMessage()

const props = defineProps<{
  id?: number
  mode: EplusFormMode
  type?: string
  changeEdit?: Boolean
  contractType?: number
}>()
const currItemType = ref(props?.type)

const queryId: string = (props.id || '') as string
const loading = ref(true)
// 父组件数据是否已加载完成（用于通知子组件可以开始触发loaded事件）
const parentDataReady = ref(false)
// 子组件加载计数器
const CHILD_TABLE_COUNT = 3 // PurchaseProducts, subItemTable, collectionPlanTable
const childLoadedCount = ref(0)
const handleChildLoaded = (source?: string) => {
  childLoadedCount.value++
  console.log(`[ExportSaleContractForm] 子组件触发loaded: ${source || 'unknown'}, 当前计数: ${childLoadedCount.value}/${CHILD_TABLE_COUNT}`)
  if (childLoadedCount.value >= CHILD_TABLE_COUNT) {
    console.log('[ExportSaleContractForm] 所有子组件加载完成，隐藏loading')
    loading.value = false
  }
}
const simpleUserList = ref([])
const settlementListData = ref([])
const companyPathListRef = ref([])
const companyIdList = ref<any[]>([])
const pagePath = useRoute().path
const currContractType = props.contractType || 1
const saleType = currContractType
const { updateDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
}

const handleTabsClick = (val) => {
  activeName.value = val.props.name
  nextTick(() => {
    if (activeName.value === 'first') {
      saleContractItemRef.value.setScorllBar()
    } else if (activeName.value === 'second') {
      addSubItemListRef.value.setScorllBar()
    } else {
      collectionPlanListRef.value.setScorllBar()
    }
  })
}

const custDeliveryDateChange = () => {
  saleContractItemRef.value.setVenderDeliveryDate(formData.custDeliveryDate)
}

const ContractInfoRef = ref()
const openProductDetail = (data) => {
  ContractInfoRef.value.open(data)
}

//关联客户
const companyCustLinkRef = ref([])
const feeActive = ref('first')
const activeName = ref('first')
// const tradeList = ref([])
// const filterMethod = (item) => {
//   const nameRegex = /[A-Z][a-z]+(?: [A-Z][a-z]+)*/
//   const regex = new RegExp(item, 'i')
//   filterCountrys.value = countryListData.countryList.filter((name) => regex.test(name?.name))
// }
const formData: any = reactive({
  totalVolume: '0',
  totalVolumeFormat: '0',
  addSubItemList: [],
  annex: [],
  designDraftList: [],
  collectionPlanList: [],
  fortyFootContainerNum: '0',
  fortyFootCabinetNum: '0',
  twentyFootCabinetNum: '0',
  bulkHandlingVolume: '0',
  bulkHandlingVolumeFormat: '0',
  trailerFee: { amount: '0', currency: 'RMB' },
  estimatedTotalFreightFormat: '0',
  estimatedTotalFreight: { amount: '0', currency: 'RMB' },
  commissionFormat: '0',
  commission: { amount: '0', currency: 'RMB' },
  platformFeeFormat: '0',
  platformFee: { amount: '0', currency: 'RMB' },
  insuranceFee: { amount: '0', currency: 'RMB' },
  sinosureFeeFormat: '0',
  sinosureFee: { amount: '0', currency: 'USD' },
  inspectionFee: { amount: '0', currency: 'RMB' },
  additionAmountFormat: '0',
  additionAmount: { amount: '0', currency: 'RMB' },
  deductionAmountFormat: '0',
  deductionAmount: { amount: '0', currency: 'RMB' },
  totalGoodsValueFormat: '0',
  totalGoodsValue: { amount: '0', currency: 'RMB' },
  totalPurchaseFormat: '0',
  totalPurchase: [],
  totalStockCost: { amount: '0', currency: 'RMB' },
  totalVatRefundFormat: '0',
  totalVatRefund: { amount: '0', currency: 'RMB' },
  orderGrossProfitFormat: '0',
  orderGrossProfit: { amount: '0.090', currency: 'RMB' },
  estimatedPackingMaterialsFormat: '0',
  estimatedPackingMaterials: { amount: '0', currency: 'RMB' },
  accessoriesPurchaseTotalFormat: '0',
  accessoriesPurchaseTotal: { amount: '0', currency: 'RMB' },
  receivableExchange: { amount: '0', currency: 'RMB' },
  receivableAmount: { amount: '0', currency: 'RMB' },
  totalGrossweight: { weight: '0', unit: 'kg' },
  totalGrossweightFormat: '0',
  totalWeight: { weight: '0', unit: 'kg' },
  totalWeightFormat: '0',
  currency: '',
  totalQuantityFormat: '0',
  totalQuantity: 0,
  grossProfitMarginFormat: '0',
  grossProfitMargin: '0',
  boxCountFormat: '0',
  totalBoxes: '0',
  totalAmountFormat: '0',
  totalAmount: { amount: '0', currency: 'RMB' },
  collectionTotal: { amount: '0', currency: 'RMB' },
  totalAmountUsd: { amount: '0', currency: 'USD' },
  usdRate: rateList['USD']
})
let oldSaleContractRespVO = reactive({})
provide('formData', formData)
const { close } = inject('dialogEmits') as {
  close: () => void
}

const addSubItemListRef = ref([])
watch(
  () => addSubItemListRef.value?.tableList,
  (tableList, oldValue) => {
    if (!oldValue || !oldValue.length) return
    let additionAmount = 0
    let deductionAmount = 0
    tableList.forEach((item) => {
      if (item.calculationType && item.amountFormat) {
        let calculationType = getDictLabel(DICT_TYPE.CALCULATION_TYPE, item.calculationType)
        if (calculationType == '加项') {
          additionAmount =
            Number(additionAmount) + Number(item.amountFormat * rateList[item.currency])
        } else {
          deductionAmount =
            Number(deductionAmount) + Number(item.amountFormat * rateList[item.currency])
        }
      }
    })
    formData.additionAmountFormat = Number(additionAmount).toFixed(3)
    formData.additionAmount.amount = Number(additionAmount).toFixed(6)
    formData.deductionAmountFormat = Number(deductionAmount).toFixed(3)
    formData.deductionAmount.amount = Number(deductionAmount).toFixed(6)
    refershSaleContractItem()
  },
  { deep: true, immediate: true }
)

const saleContractItemRef = ref()
watch(
  () => saleContractItemRef.value?.computedTableData,
  (computedTableData) => {
    // Guard clause: if computedTableData is undefined, return early
    if (!computedTableData) {
      return
    }
    
    //采购合计
    // let totalPurchaseSum = computedTableData.totalPurchaseSum
    let listTotalPurchase = computedTableData.listTotalPurchase

    //数量合计
    let totalQuantitySum = computedTableData.totalQuantitySum
    //体积合计
    let totalVolumeSum = computedTableData.totalVolumeSum
    //佣金金额合计
    let commissionAmountSum = computedTableData.commissionAmountSum
    //扣减佣金合计
    let subCommissionAmountSum = computedTableData.subCommissionAmountSum
    //箱数合计
    let totalBoxesSum = computedTableData.totalBoxesSum
    //平台费合计
    let platformFeeAmountSum = computedTableData.platformFeeAmountSum
    //货值合计
    let totalGoodsValueAmountSum = computedTableData.totalGoodsValueAmountSum
    //毛重合计
    let totalGrossWeightSum = computedTableData.totalGrossWeightSum
    //净重合计
    let totalWeightSum = computedTableData.totalWeightSum
    //退税率合计
    let totalVatRefundAmountSum = computedTableData.totalVatRefundAmountSum
    //预计包材合计
    let estimatedPackingMaterialsSum = computedTableData.estimatedPackingMaterialsSum
    //预估总运费
    let estimatedTotalFreightSum = computedTableData.estimatedTotalFreightSum
    //40尺高柜
    let fortyFootContainerNum = computedTableData.fortyFootContainerNum
    //20尺柜
    let twentyFootCabinetNum = computedTableData.twentyFootCabinetNum
    //40尺柜
    let fortyFootCabinetNum = computedTableData.fortyFootCabinetNum
    //散货
    let bulkHandlingVolume = computedTableData.bulkHandlingVolume
    //中信保费用
    let sinosureFeeSum = computedTableData.sinosureFeeSum

    //配件采购费合计
    let accessoriesPurchaseTotalSum = computedTableData.accessoriesPurchaseTotalSum

    if (totalVolumeSum || totalVolumeSum == 0) {
      //体积合计页面显示字段  单位立方米 保留6位小数
      formData.totalVolumeFormat = formatNum(Number(totalVolumeSum) / 1000000, volPrecision)
      //体积合计：销售明细中外箱体积乘以箱数  单位立方厘米 保留6位小数
      formData.totalVolume = Number(totalVolumeSum).toFixed(6)
    }
    formData.fortyFootContainerNum = fortyFootContainerNum
    formData.twentyFootCabinetNum = twentyFootCabinetNum
    formData.fortyFootCabinetNum = fortyFootCabinetNum
    if (bulkHandlingVolume || bulkHandlingVolume == 0) {
      formData.bulkHandlingVolume = Number(bulkHandlingVolume).toFixed(6)
      formData.bulkHandlingVolumeFormat = (Number(bulkHandlingVolume) / 1000000).toFixed(6)
    } else {
      formData.bulkHandlingVolume = 0
      formData.bulkHandlingVolumeFormat = '0'
    }
    //采购合计
    if (listTotalPurchase?.length) {
      formData.totalPurchase = listTotalPurchase
      formData.totalPurchaseFormat = amountListFormat(listTotalPurchase)
    } else {
      formData.totalPurchase = []
    }
    //库存合计
    if (computedTableData.listTotalStockCost?.amount) {
      formData.totalStockCost = computedTableData.listTotalStockCost
    }

    //总佣金金额
    if (commissionAmountSum || commissionAmountSum == 0) {
      formData.commissionFormat = Number(commissionAmountSum).toFixed(3)
      formData.commission.amount = Number(commissionAmountSum).toFixed(6)
    }
    //总箱数
    if (totalBoxesSum || totalBoxesSum == 0) {
      formData.totalBoxes = Number(totalBoxesSum).toFixed(0)
    }
    //总平台费
    if (platformFeeAmountSum || platformFeeAmountSum == 0) {
      formData.platformFeeFormat = Number(platformFeeAmountSum).toFixed(3)
      formData.platformFee.amount = Number(platformFeeAmountSum).toFixed(6)
    }
    //货值合计
    if (totalGoodsValueAmountSum || totalGoodsValueAmountSum == 0) {
      //应收汇款 = 货值合计
      formData.receivableExchange.amount = Number(totalGoodsValueAmountSum).toFixed(6)
      formData.totalGoodsValue.amount = Number(totalGoodsValueAmountSum).toFixed(6)
      formData.totalGoodsValueFormat = Number(totalGoodsValueAmountSum).toFixed(3)
      collectionPlanListRef.value.resetItems()
    }
    //中信保费用
    if (sinosureFeeSum) {
      formData.sinosureFee.amount = sinosureFeeSum
      formData.sinosureFeeFormat = Number(sinosureFeeSum).toFixed(3)
    } else {
      formData.sinosureFee.amount = '0'
      formData.sinosureFeeFormat = '0'
    }

    //净重合计
    if (totalWeightSum || totalWeightSum == 0) {
      formData.totalWeightFormat = formatNum(totalWeightSum, weightPrecision)
      formData.totalWeight.weight = Number(totalWeightSum).toFixed(6)
    }
    //毛重合计
    if (totalGrossWeightSum || totalGrossWeightSum == 0) {
      formData.totalGrossweightFormat = formatNum(totalGrossWeightSum, weightPrecision)
      formData.totalGrossweight.weight = Number(totalGrossWeightSum).toFixed(6)
    }
    //退税率合计
    if (totalVatRefundAmountSum || totalVatRefundAmountSum == 0) {
      formData.totalVatRefund.amount = Number(totalVatRefundAmountSum).toFixed(6)
      formData.totalVatRefundFormat = Number(totalVatRefundAmountSum).toFixed(3)
    }
    //数量合计
    if (totalQuantitySum || totalQuantitySum == 0) {
      formData.totalQuantityFormat = Number(totalQuantitySum).toFixed(0)
      formData.totalQuantity = Number(totalQuantitySum)
    }
    //预计包材合计
    if (estimatedPackingMaterialsSum || estimatedPackingMaterialsSum == 0) {
      formData.estimatedPackingMaterialsFormat = Number(estimatedPackingMaterialsSum).toFixed(3)
      formData.estimatedPackingMaterials.amount = Number(estimatedPackingMaterialsSum).toFixed(6)
    }
    //预估总运费
    if (estimatedTotalFreightSum || estimatedTotalFreightSum == 0) {
      formData.estimatedTotalFreightFormat = Number(estimatedTotalFreightSum).toFixed(3)
      formData.estimatedTotalFreight.amount = Number(estimatedTotalFreightSum).toFixed(6)
    }
    //配件采购费
    if (accessoriesPurchaseTotalSum || accessoriesPurchaseTotalSum == 0) {
      formData.accessoriesPurchaseTotalFormat = Number(accessoriesPurchaseTotalSum).toFixed(3)
      formData.accessoriesPurchaseTotal.amount = Number(accessoriesPurchaseTotalSum).toFixed(6)
    }
    //毛利润=收入-成本
    //其中收入=货值合计*汇率+加项金额+退税金额
    //其中成本=采购合计+包材+配件+总运费+验货费+平台费+减项金额+保险费+中信保费
    //采购合计=采购总金额*对人民币汇率，需要看采购币种
    let rate = 0
    if (formData.currency) {
      rate = rateList[formData.currency]
    }
    //收入
    let addAmount = 0
    if (totalGoodsValueAmountSum && rate) {
      addAmount = Number(totalGoodsValueAmountSum) * Number(rate)
    }
    //退税金额
    if (formData.totalVatRefund.amount) {
      addAmount = Number(addAmount) + Number(formData.totalVatRefund.amount)
    }
    //加项金额
    if (formData.additionAmount.amount) {
      addAmount = Number(addAmount) + Number(formData.additionAmount.amount)
    }
    //成本
    let subAmount = 0
    //采购合计
    if (listTotalPurchase?.length) {
      let tmp = Number(getAmountListSumRmb(listTotalPurchase, rateList))
      if (tmp) {
        subAmount = tmp
      }
      console.log('p1', subAmount)
    }
    //库存合计
    if (computedTableData.listTotalStockCost?.amount) {
      subAmount =
        Number(subAmount) +
        Number(computedTableData.listTotalStockCost.amount) *
          rateList[computedTableData.listTotalStockCost.currency]
      console.log(
        'p9',
        Number(computedTableData.listTotalStockCost.amount) *
          rateList[computedTableData.listTotalStockCost.currency]
      )
    }
    //包材
    if (formData.estimatedPackingMaterials.amount) {
      console.log('p2', Number(formData.estimatedPackingMaterials.amount))
      subAmount = Number(subAmount) + Number(formData.estimatedPackingMaterials.amount)
    }
    //配件
    // if (formData.accessoriesPurchaseTotal.amount) {
    //   subAmount = Number(subAmount) + Number(formData.accessoriesPurchaseTotal.amount)
    // }
    //总运费
    if (formData.estimatedTotalFreight.amount) {
      console.log('p3', Number(formData.estimatedTotalFreight.amount))
      subAmount = Number(subAmount) + Number(formData.estimatedTotalFreight.amount)
    }
    //验货费
    if (formData.inspectionFee.amount) {
      console.log('p4', Number(formData.inspectionFee.amount))
      subAmount = Number(subAmount) + Number(formData.inspectionFee.amount)
    }
    //平台费
    if (formData.platformFee.amount) {
      console.log(
        'p5',
        Number(formData.platformFee.amount) * rateList[formData.platformFee.currency]
      )
      subAmount =
        Number(subAmount) +
        Number(formData.platformFee.amount) * rateList[formData.platformFee.currency]
    }
    //减项金额
    if (formData.deductionAmount.amount) {
      console.log('p8', Number(formData.deductionAmount.amount))
      subAmount = Number(subAmount) + Number(formData.deductionAmount.amount)
    }
    //保险费
    if (formData.insuranceFee.amount) {
      console.log('p6', Number(formData.insuranceFee.amount))
      subAmount = Number(subAmount) + Number(formData.insuranceFee.amount)
    }
    //中中信保费用
    if (formData.sinosureFee.amount) {
      console.log(
        'p7',
        Number(formData.sinosureFee.amount) * rateList[formData.sinosureFee.currency]
      )
      subAmount =
        Number(subAmount) +
        Number(formData.sinosureFee.amount) * rateList[formData.sinosureFee.currency]
    }
    //佣金否扣减总金额为是，需要将佣金计算到成本
    // subCommissionAmountSum
    if (commissionAmountSum) {
      subAmount = Number(subAmount) + Number(commissionAmountSum) * rateList[formData.currency]
    }
    console.log(Number(commissionAmountSum) * rateList[formData.currency])
    let orderGrossProfitSum = addAmount - subAmount
    if (orderGrossProfitSum || orderGrossProfitSum == 0) {
      formData.orderGrossProfit.amount = Number(orderGrossProfitSum).toFixed(6)
      formData.orderGrossProfitFormat = Number(orderGrossProfitSum).toFixed(3)
    }

    //毛利率=毛利润/收入
    let grossProfitMarginSum = 0
    if (orderGrossProfitSum == 0) {
      formData.grossProfitMarginFormat = '0'
      formData.grossProfitMargin = '0'
    } else if (orderGrossProfitSum && addAmount) {
      grossProfitMarginSum = Number(orderGrossProfitSum) / Number(addAmount)
      formData.grossProfitMarginFormat = Number(grossProfitMarginSum * 100).toFixed(2)
      formData.grossProfitMargin = Number(grossProfitMarginSum).toFixed(6)
    }

    //销售总金额
    if (formData?.totalGoodsValue?.amount) {
      let collectionTotalVal = Number(formData?.totalGoodsValue?.amount)
      let totalAmount =
        formData?.totalGoodsValue.amount * rateList[formData.totalGoodsValue.currency]
      if (formData.additionAmount?.amount) {
        totalAmount = Number(
          totalAmount + formData.additionAmount?.amount * rateList[formData.additionAmount.currency]
        )
        //2547 收款合计不需要加项
        // collectionTotalVal += Number(formData.additionAmount?.amount / rateList[formData.currency])
      }
      if (formData.deductionAmount?.amount) {
        totalAmount = Number(
          totalAmount -
            formData.deductionAmount?.amount * rateList[formData.deductionAmount.currency]
        )
        collectionTotalVal -= Number(formData.deductionAmount?.amount / rateList[formData.currency])
      }
      //佣金扣减金额存在，总金额需要减去扣减金额
      // if (subCommissionAmountSum) {
      //   totalAmount = Number(totalAmount) - Number(subCommissionAmountSum)
      // }
      formData.totalAmount = {
        amount: formatterPrice(totalAmount),
        currency: 'RMB'
      }
      formData.totalAmountThisCurrency = {
        amount: formatterPrice(Number(totalAmount / rateList[formData.currency])),
        currency: formData.currency
      }
      //收款合计
      formData.collectionTotal = {
        amount: formatterPrice(Number(collectionTotalVal) - Number(subCommissionAmountSum || 0)),
        currency: formData.currency
      }
      collectionPlanListRef.value.resetItems()
      formData.totalAmountFormat = formatterPrice(totalAmount, moneyPrecision)
      formData.totalAmountUsd = {
        amount: formatterPrice(Number(totalAmount / formData.usdRate)),
        currency: 'USD'
      }
    }
  },
  { deep: true, immediate: true }
)

const collectionPlanListRef = ref([])

const refershSaleContractItem = () => {
  saleContractItemRef.value?.tableList.forEach((item) => {
    saleContractItemRef.value?.refershTableRow(item)
  })
}

const setSaleContractItemParent = (key, value) => {
  saleContractItemRef.value?.setParentParams({
    key: key,
    value: value
  })
}

//运输方式
const changeTransportType = () => {
  setSaleContractItemParent('transportTypeContract', formData.transportType)
  refershSaleContractItem()
}
//价格条款
const changeSettlementTermType = () => {
  transportSchemas[2].rules.required = ['CIF', 'CFR', 'DAP', 'DDP', 'DAT', 'CIP'].includes(
    formData.settlementTermType
  )
  transportSchemas[3].rules.required = ['DDP', 'DAP', 'DAT', 'CIP'].includes(
    formData.settlementTermType
  )
  setSaleContractItemParent('settlementTermTypeContract', formData.settlementTermType)
  refershSaleContractItem()
}

//中信保
const changeSinosureFee = () => {
  //是否启用中信保服务
  setSaleContractItemParent('creditFlagContract', formData.creditFlag)
  refershSaleContractItem()
}

const changeCurrency = (val) => {
  //加项金额，减项金额币种和单据币种一致
  formData.platformFee.currency = val
  formData.exchangeRate = rateList[val]
  //佣金，货值合计币种和单据币种一致
  formData.commission.currency = val
  formData.totalGoodsValue.currency = val
  setSaleContractItemParent('currencyContract', formData.currency)
  refershSaleContractItem()
}

const changeTrailerFee = (val) => {
  if (!val) {
    val = 0
  }
  setSaleContractItemParent('trailerFeeContract', val)
  refershSaleContractItem()
}

const changeInsuranceFee = (val) => {
  if (!val) {
    val = 0
  }
  setSaleContractItemParent('insuranceFeeContract', val)
  refershSaleContractItem()
}
//设置收款计划列表
const setCollectionPlanList = (id) => {
  let collectionPlanList = []
  if (id) {
    let settlementList = settlementListData.value.filter((item) => item.id == id)
    if (settlementList?.length) {
      formData.settlementName = settlementList[0].name
      formData.settlementId = settlementList[0].id
      if (settlementList[0].collectionPlanList?.length) {
        collectionPlanList = settlementList[0].collectionPlanList
      }
    }
  }

  formData.collectionPlanList = collectionPlanList
  return collectionPlanList
}

//收款方式改变
const changeSettlement = (id) => {
  setCollectionPlanList(id)
}

const changecompanyPathName = (val) => {
  if (val) {
    let pathObj = companyPathListRef.value.find((item) => item.path === val)
    formData.companyId = pathObj.id
    formData.companyName = pathObj.path.split('->')[0]
    formData.companyPath = pathObj.companyPath
    companyIdList.value = getCompanyIdList(pathObj.companyPath)
  }
}

const changeDeparturePort = (val, list) => {
  let item = list.find((item) => item.id === val)
  formData.departurePortName = item?.nameEng
}
const changeDestinationPort = (val, list) => {
  let item = list.find((item) => item.id === val)
  formData.destinationPortName = item?.nameEng
}
const changeUser = (val) => {
  if (val) {
    formData.salesDeptName = val.deptName
    formData.sales = { userId: val?.id, ...val }
  }
}
const managerChange = (val) => {
  if (val) {
    formData.managerDeptName = val.deptName
    formData.manager = { userId: val?.id, ...val }
  }
}
const countryListData = useCountryStore()
const filterCountrys = ref(countryListData.countryList)
const oldCustCode = ref()
const custChange = async () => {
  if (
    formData.custCode &&
    isValidArray(saleContractItemRef.value?.tableList) &&
    props.mode === 'create'
  ) {
    return new Promise((resolve, reject) => {
      ElMessageBox.confirm('切换客户会清空销售明细，是否切换?', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          saleContractItemRef.value?.resetTable()
          addSubItemListRef.value?.resetTable()
          collectionPlanListRef.value?.resetTable()
          resolve(true)
        })
        .catch(() => {
          formData.custCode = oldCustCode.value
          reject(false)
        })
    })
  } else {
    return true
  }
}
const localFlag = ref(false)
const producedFlag = ref(0)
const formRef = ref()
const getCustName = async (val) => {
  let flag = await custChange()
  if (!flag) return false
  if (val.length === 2) {
    if (val[1]) {
      val[1].filter((item) => {
        if (item.code === val[0] && props.mode === 'create') {
          oldCustCode.value = item.code
          formData.custId = item.id
          formData.custName = item.name
          formData.custCode = item.code
          formData.custCountryId = item.countryId
          formData.custCountryName = item.countryName
          formData.settlementTermType = item.settlementTermType
          formData.custAreaName = item.regionName
          formData.agentFlag = item.agentFlag
          formData.creditFlag = item.creditFlag
          changeSettlementTermType()
          changeSinosureFee()
          //订单路径
          if ([1, 3].includes(currContractType)) {
            if (item.companyPath?.length) {
              // let defaultCompanyPathObj = item.companyPath[0]
              // let defaultCompanyPath = item.companyPath?.filter((cp) => {
              //   return cp.defaultFlag == 1
              // })
              // if (defaultCompanyPath?.length) {
              //   defaultCompanyPathObj = defaultCompanyPath[0]
              // }
              setCompanyPathListRef(item, 1)
              const defaultCompanyPathObj: any =
                companyPathListRef.value.find((item: any) => item.defaultFlag == 1) ||
                companyPathListRef.value[0]
              formData.companyPathName = defaultCompanyPathObj?.path
              formData.companyId = defaultCompanyPathObj?.companyPath?.id
              formData.companyName = defaultCompanyPathObj?.companyPath?.name
              formData.companyPath = defaultCompanyPathObj?.companyPath
              companyIdList.value = getCompanyIdList(defaultCompanyPathObj.companyPath)
            } else {
              formData.companyPathName = null
              formData.companyId = null
              formData.companyName = null
              companyIdList.value = []
            }
          }

          //关联客户
          if (item.custLink?.length) {
            companyCustLinkRef.value = item.custLink
          } else {
            companyCustLinkRef.value = []
          }

          //收款方式
          if (item.settlementList?.length) {
            let defaultSettlement = item.settlementList.find((item) => item?.defaultFlag == 1)
            formData.settlementName = localFlag.value
              ? formData.settlementName || defaultSettlement?.nameEng
              : defaultSettlement?.nameEng
            formData.settlementId = localFlag.value
              ? formData.settlementId || defaultSettlement?.id
              : defaultSettlement?.id
            settlementListData.value = item.settlementList
            collectionPlanListRef.value.tableList = defaultSettlement?.collectionPlanList
          } else {
            settlementListData.value = []
          }

          // 交易货币
          if (item.currency) {
            if (currContractType === 3) {
              if (item.currency !== 'RMB') {
                changeCurrency(item.currency)
                formData.currency = item.currency
              } else {
                formData.currency = 'USD'
              }
            } else {
              changeCurrency(item.currency)
              formData.currency = item.currency
            }
          }

          // 进口国
          formData.tradeCountryName = item.countryName
          formData.tradeCountryId = item.countryId
          formData.tradeCountryArea = item.regionName

          //设置是否内部客户
          producedFlag.value = item.internalFlag === 1 && currContractType == 2 ? 0 : 1
        }
      })
    }
  }
  nextTick(() => {
    setTimeout(() => {
      formRef.value?.clearValidate()
    }, 30)
  })
}

const getName = (e, type) => {
  let item = e[1].find((item) => item.id === e[0])
  if (type == 1) {
    formData.departureCountryName = item.name
    formData.departureCountryArea = item.regionName
    formData.departureCountryId = item.id
    formData.departurePortId = ''
    formData.departurePortName = ''
  } else if (type == 2) {
    formData.tradeCountryName = item.name
    formData.tradeCountryArea = item.regionName
    formData.destinationPortId = ''
    formData.destinationPortName = ''
    formData.deliveryAddress = ''
  }
}
// getName([1757, countryListData.countryList], 1)

watch([() => formData.custCode, () => formData.companyId], ([val1, val2]) => {
  if (val1 && val2) {
    ExportSaleContractApi.getCollectionAccount({ custCode: val1, companyId: val2 }).then((res) => {
      formData.collectionAccountId = res.id
      formData.collectionAccountBankCode = res.bankCode
    })
  }
})

const setCompanyPathListRef = async (item, type) => {
  let companyPath: any[] = []
  if (currContractType === 3) {
    const innerCustIdList = companyList.value
      .filter((item: any) => item.companyNature === 4)
      .map((item) => item.id)
    companyPath = getInnerCustPath(item.companyPath, innerCustIdList)
  } else {
    companyPath = item.companyPath
  }

  let arr = []
  companyPath?.forEach((item) => {
    if (item.path) {
      item.pathName = getCompanyPathNameFromObj(item.path)
      let obj = {
        defaultFlag: item.defaultFlag,
        id: item.path.id,
        path: item.pathName,
        companyPath: item.path
      }
      arr.push(obj)
    }
  })
  companyPathListRef.value = arr
}

const getCollectedCustName = (val) => {
  if (val) {
    let custLinkObj = companyCustLinkRef.value.find((item) => item.id === val)
    formData.collectedCustId = custLinkObj.id
    formData.collectedCustName = custLinkObj.name
    formData.collectedCustCode = custLinkObj.code
  }
}
//加减项
const addSubItemListSchemas: EplusFormSchema[] = [
  {
    field: 'addSubItemList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px'
  }
]

//收款计划
const collectionPlanListSchemas: EplusFormSchema[] = [
  {
    field: 'collectionPlanList',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
]

const getReceiveCustName = (val) => {
  if (val) {
    let custLinkObj = companyCustLinkRef.value.find((item) => item.id === val)
    formData.receiveCustId = custLinkObj.id
    formData.receiveCustName = custLinkObj.name
    formData.receiveCustCode = custLinkObj.code
  }
}

const companyIdChange = (data) => {
  formData.companyName = data.name
  formData.companyPath = { id: formData.companyId }
  companyIdList.value = [formData.companyId]
}

const currencyFilter = (arr) => {
  if (currContractType === 3) {
    return arr.filter((item) => item.value !== 'RMB')
  } else {
    return arr
  }
}

let basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'code',
    label: '单据编号',
    component: <el-input disabled={!['create', 'copy'].includes(props.mode)} />
  },
  {
    field: 'custCode',
    label: '客户名称',
    xl: 8,
    lg: 12,
    rules: {
      required: true,
      message: '请选择客户名称'
    }
  },
  {
    field: 'companyPathName',
    label: '订单路径',
    disabled: currContractType === 2,
    rules: {
      required: true,
      message: '请选择订单路径'
    }
  },
  {
    field: 'companyName',
    label: '下单主体',
    disabled: currContractType === 2
  },

  {
    field: 'companyId',
    label: '下单主体',
    disabled: currContractType === 1 || currContractType === 3,
    component: (
      <eplus-custom-select
        api={getcompanySimpleList}
        label="name"
        value="id"
        clearable={false}
        placeholder="请选择"
        class="!w-100%"
        onChange={(data) => companyIdChange(data)}
      />
    ),
    rules: {
      required: true,
      message: '请选择下单主体'
    }
  },
  {
    field: 'collectionAccountBankCode',
    label: '收款账户',
    component: <el-input disabled />
  },
  {
    field: 'custPo',
    label: '客户合同号',
    editable: true,
    component: <el-input />
  },
  {
    field: 'settlementId',
    label: '收款方式',
    editable: true,
    rules: {
      required: true,
      message: '请选择订收款方式'
    }
  },
  {
    field: 'custCountryName',
    label: '客户国别',
    editable: true,
    component: <el-input disabled />
  },
  {
    field: 'currency',
    label: '交易币别',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.CURRENCY_TYPE}
        style="width:100%"
        filter={currencyFilter}
        onChange={changeCurrency}
      />
    ),
    rules: {
      required: true,
      message: '请选择交易币别'
    }
  },
  {
    field: 'exchangeRate',
    label: '币别汇率',
    component: <el-input disabled />
  },
  {
    field: 'settlementTermType',
    label: '价格条款',
    disabled: currContractType === 2,
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.PRICE_TYPE}
        style="width:100%"
        onChange={changeSettlementTermType}
      />
    ),
    rules: {
      required: true,
      message: '请选择价格条款'
    }
  },
  {
    field: 'agentFlag',
    label: '是否联营',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.CONFIRM_TYPE}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择是否联营'
    }
  },
  {
    field: 'collectedCustId',
    label: '付款方',
    editable: true
  },
  {
    field: 'receiveCustId',
    label: '收货方',
    editable: true
  },
  {
    field: 'salesId',
    label: '业务员',
    editable: true,
    component: (
      <eplus-user-select
        v-model={formData.salesId}
        onChange={changeUser}
        simpleUserList={simpleUserList.value}
      ></eplus-user-select>
    ),
    rules: [
      {
        required: true,
        message: '请选择业务员'
      }
    ]
  },
  {
    field: 'salesDeptName',
    label: '业务部门',
    editable: true,
    component: <el-input disabled />
  },
  {
    field: 'managerId',
    label: '业务跟单',
    editable: true,
    component: (
      <eplus-user-select
        v-model={formData.managerId}
        onChange={managerChange}
      ></eplus-user-select>
    )
  },
  {
    field: 'managerDeptName',
    label: '跟单部门',
    component: <el-input disabled />
  },
  {
    field: 'remark',
    label: '备注',
    component: <el-input type="textarea" />
  },
  {
    field: 'annex',
    label: '附件',
    span: 24
  },
  {
    field: 'designDraftList',
    label: '设计稿',
    span: 24
  }
])

let transportSchemas: EplusFormSchema[] = reactive([
  {
    field: 'tradeCountryId',
    label: '进口国',
    component: (
      <eplus-input-select
        dataList={countryListData.countryList}
        label="name"
        value="id"
        lowerCaseFlag
        onChangeEmit={(...$event) => getName($event, 2)}
        class="!w-100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择进口国'
    }
  },
  {
    field: 'tradeCountryArea',
    label: '区域',
    component: <el-input disabled />
  },
  {
    field: 'destinationPortId',
    label: '目的口岸',
    rules: {
      required: true,
      message: '请选择目的口岸',
      trigger: 'change'
    }
  },
  {
    field: 'deliveryAddress',
    label: '送货地址',
    component: <el-input placeholder="请输入" />,
    rules: {
      required: false,
      message: '请输入送货地址'
    }
  },
  {
    field: 'departureCountryId',
    label: '出口国',
    component: (
      <eplus-input-select
        dataList={countryListData.countryList}
        label="name"
        value="id"
        lowerCaseFlag
        onChangeEmit={(...$event) => getName($event, 1)}
        class="!w-100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择出口国'
    }
  },
  {
    field: 'departureCountryArea',
    label: '区域',
    component: <el-input disabled />
  },
  {
    field: 'departurePortId',
    label: '出运口岸',
    editable: true,
    rules: {
      required: true,
      message: '请选择出运口岸',
      trigger: 'change'
    }
  },

  {
    label: '运输方式',
    field: 'transportType',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.TRANSPORT_TYPE}
        style="width:100%"
        onChange={changeTransportType}
      />
    ),
    rules: { required: true, message: '请选择运输方式' }
  },
  {
    field: 'custDeliveryDate',
    label: '合同交期',
    editable: true,
    rules: {
      required: true,
      message: '请选择合同交期',
      trigger: 'change'
    }
  }
])

let feeSchemas: EplusFormSchema[] = reactive([
  {
    field: 'twentyFootCabinetNum',
    label: '20尺柜(个)',
    readOnly: true,
    formatter: (val) => {
      return val ? val : 0
    }
  },
  {
    field: 'fortyFootCabinetNum',
    label: '40尺柜(个)',
    readOnly: true,
    formatter: (val) => {
      return val ? val : 0
    }
  },
  {
    field: 'fortyFootContainerNum',
    label: '40尺高柜(个)',
    readOnly: true,
    formatter: (val) => {
      return val ? val : 0
    }
  },
  {
    field: 'bulkHandlingVolumeFormat',
    label: '散货(m³)',
    readOnly: true,
    formatter: (val) => {
      return val ? val : 0
    }
  },
  {
    field: 'trailerFee',
    label: '拖柜费'
  },
  {
    field: 'bookingFlag',
    label: '是否订舱',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.CONFIRM_TYPE}
        style="width:100%"
        defaultValue={1}
      />
    ),
    rules: {
      required: true,
      message: '请选择是否订舱',
      trigger: 'change'
    }
  },
  {
    field: 'estimatedTotalFreight',
    label: '预估总运费',
    readOnly: true,
    formatter: (val) => {
      return val ? `${getCurrency(val.currency)} ${formatNum(val.amount, moneyPrecision)}` : '0'
    }
  }
])

let sumSchemas: EplusFormSchema[] = reactive([
  {
    field: 'totalBoxes',
    label: '箱数合计',
    readOnly: true,
    formatter: (val) => {
      return val ? val : 0
    }
  },
  {
    field: 'totalGrossweightFormat',
    label: '毛重合计',
    readOnly: true,
    formatter: (val) => {
      return val ? `${val} kg` : 0
    }
  },
  {
    field: 'totalWeightFormat',
    label: '净重合计',
    readOnly: true,
    formatter: (val) => {
      return val ? `${val} kg` : 0
    }
  },
  {
    field: 'totalVolumeFormat',
    label: '体积合计',
    readOnly: true,
    formatter: (val) => {
      return val ? `${val} ${VolumeUnit}` : 0
    }
  },
  {
    field: 'totalGoodsValue',
    label: '货值合计',
    readOnly: true,
    formatter: (val) => {
      return val ? `${getCurrency(val.currency)} ${formatNum(val.amount, moneyTotalPrecision)}` : '0'
    }
  },
  {
    field: 'totalPurchase',
    label: '采购合计',
    readOnly: true,
    formatter: (val) => {
      if (isValidArray(val)) {
        return val
          .map((item) => {
            return `${getCurrency(item.currency)} ${formatNum(item.amount, moneyTotalPrecision)}`
          })
          .join(',')
      } else {
        return 0
      }
    }
  },
  {
    field: 'totalStockCost',
    label: '库存合计',
    readOnly: true,
    formatter: (val) => {
      return val ? `${getCurrency(val.currency)} ${formatNum(val.amount, moneyTotalPrecision)}` : '0'
    }
  },
  {
    field: 'totalVatRefund',
    label: '退税合计',
    readOnly: true,
    formatter: (val) => {
      return val ? `${getCurrency(val.currency)} ${formatNum(val.amount, moneyTotalPrecision)}` : '0'
    }
  },
  {
    field: 'totalQuantityFormat',
    label: '数量合计',
    readOnly: true,
    formatter: (val) => {
      return val ? val : 0
    }
  },
  {
    field: 'orderGrossProfit',
    label: '订单毛利'
  },
  {
    field: 'grossProfitMargin',
    label: '毛利率%'
  },
  {
    field: 'collectionTotal',
    label: '收款合计',
    readOnly: true,
    formatter: (val) => {
      return val ? `${getCurrency(val.currency)} ${formatNum(val.amount, moneyTotalPrecision)}` : '0'
    }
  },
  {
    field: 'totalAmountThisCurrency',
    label: '销售总金额(原币种)',
    labelWidth: '140px',
    readOnly: true,
    formatter: (val) => {
      return val ? `${getCurrency(val.currency)} ${formatNum(val.amount, moneyTotalPrecision)}` : '0'
    }
  },
  {
    field: 'totalAmount',
    label: '销售总金额(RMB)',
    readOnly: true,
    formatter: (val) => {
      return val ? `${getCurrency(val.currency)} ${formatNum(val.amount, moneyTotalPrecision)}` : '0'
    }
  },
  {
    field: 'totalAmountUsd',
    label: '销售总金额(美金)',
    readOnly: true,
    formatter: (val) => {
      return val ? `${getCurrency(val.currency)} ${formatNum(val.amount, moneyTotalPrecision)}` : '0'
    }
  }
])

let platFormFeeSchemas: EplusFormSchema[] = reactive([
  {
    field: 'commission',
    label: '佣金',
    readOnly: true,
    formatter: (val) => {
      return val ? `${getCurrency(val.currency)} ${formatNum(val.amount, moneyPrecision)}` : '0'
    }
  },
  {
    field: 'platformFee',
    label: '平台费',
    readOnly: true,
    formatter: (val) => {
      return val ? `${getCurrency(val.currency)} ${formatNum(val.amount, moneyPrecision)}` : '0'
    }
  }
])

let otherFeeSchemas: EplusFormSchema[] = reactive([
  {
    field: 'insuranceFee',
    label: '保险费(RMB)'
  },
  {
    field: 'sinosureFee',
    label: '中信保费用',
    readOnly: true,
    formatter: (val) => {
      return val ? `${getCurrency(val.currency)} ${formatNum(val.amount, moneyPrecision)}` : '0'
    }
  },
  {
    field: 'additionAmount',
    label: '加项金额(RMB)',
    readOnly: true,
    formatter: (val) => {
      return val ? `${getCurrency(val.currency)} ${formatNum(val.amount, moneyPrecision)}` : '0'
    }
  },
  {
    field: 'deductionAmount',
    label: '减项金额(RMB)',
    readOnly: true,
    formatter: (val) => {
      return val ? `${getCurrency(val.currency)} ${formatNum(val.amount, moneyPrecision)}` : '0'
    }
  },
  {
    field: 'inspectionFee',
    label: '验货费用'
  },
  {
    field: 'estimatedPackingMaterials',
    label: '预计包材合计',
    readOnly: true,
    formatter: (val) => {
      return val ? `${getCurrency(val.currency)} ${formatNum(val.amount, moneyPrecision)}` : '0'
    }
  },
  {
    field: 'accessoriesPurchaseTotal',
    label: '配件采购费用合计',
    readOnly: true,
    formatter: (val) => {
      return val ? `${getCurrency(val.currency)} ${formatNum(val.amount, moneyPrecision)}` : '0'
    }
  }
])

const collectionSchemas: EplusFormSchema[] = [
  {
    field: 'children',
    label: '',
    labelWidth: '0px',
    editable: true,
    span: 24,
    rules: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          callback()
        }
      }
    ]
  }
]
const emit = defineEmits(['handleSuccess', 'sucess'])

//获取本地数据
const getLocalParams = async () => {
  let params = cloneDeep(formData)
  if (saleContractItemRef.value) {
    params.children = await saleContractItemRef.value?.tableListExtend
  }

  if (addSubItemListRef.value) {
    params.addSubItemList = await addSubItemListRef.value?.tableListExtend
  }

  if (collectionPlanListRef.value) {
    params.collectionPlanList = await collectionPlanListRef.value?.tableListExtend
  }
  return params
}

//处理提交前的formdata
const getParams = async (type?) => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params: any = JSON.parse(JSON.stringify(formData))
  params.code = params.code ? params.code.trim() : params.code
  params.saleType = saleType
  let result = await saleContractItemRef.value.checkData(type)
  // 变更新增的产品添加合并编码
  result?.forEach((e) => {
    if (!e.contractCode) {
      e.contractCode = formData.code
    }
  })

  if (result && result.length) {
    params.children = result
  } else {
    return false
  }
  let subItemResult = await addSubItemListRef.value?.checkData(type)
  if (subItemResult) {
    params.addSubItemList = subItemResult
  } else {
    return false
  }
  let collectionPlanResult = await collectionPlanListRef.value?.checkData(type)
  if (collectionPlanResult) {
    params.collectionPlanList = collectionPlanResult
  } else {
    return false
  }

  if (
    formData.collectionTotal?.amount &&
    formData.receivedAmount?.amount &&
    Number(formData.collectionTotal?.amount) < Number(formData.receivedAmount?.amount)
  ) {
    message.warning(
      `已收款${formData.receivedAmount.amount}，变更后总收款金额为${formData.collectionTotal.amount}，无法变更`
    )
    return false
  }
  return { sourceType: 1, submitFlag: type === 'submit' ? 1 : 0, ...params }
}

//表单暂存
const localSaveForm = async () => {
  try {
    let paramsResult = await getLocalParams()
    if (!paramsResult) return
    localStorage.setItem(pagePath, JSON.stringify({ pagePath: pagePath, data: paramsResult }))
    message.success('暂存成功')
    close()
  } catch (e) {
    console.log(e)
  }
}

const submitForm = async (type?) => {
  try {
    let paramsResult = await getParams(type)
    if (!paramsResult) return false
    if (props.mode == 'create' || props.mode == 'copy') {
      let req =
        currContractType === 1
          ? ExportSaleContractApi.createExportSaleContract
          : currContractType === 2
            ? DomesticSaleContractApi.createDomesticSaleContract
            : FactorySaleContractApi.createFactoryContract
      await req(paramsResult)
      message.success('提交成功')
      close()
    } else {
      if (currItemType.value === 'change') {
        paramsResult = {
          saleContractRespVO: paramsResult,
          old_saleContractRespVO: oldSaleContractRespVO
        }
        if (currContractType === 1) {
          let res = await ExportSaleContractApi.submitChange(paramsResult)
          message.notifyPushSuccess('成功转外销合同变更', res, 'export-change')
        } else if (currContractType === 2) {
          let res = await DomesticSaleContractChangeApi.createChange({
            ...paramsResult,
            submitFlag: 1
          })
          message.notifyPushSuccess('成功转内销合同变更', res, 'domestic-change')
        } else if (currContractType === 3) {
          let res = await FactorySaleContractApi.createChange({
            ...paramsResult,
            submitFlag: 1
          })
          message.notifyPushSuccess('成功转外币采购合同变更', res, 'factory-change')
        }
        if (paramsResult.saleContractRespVO.confirmFlag === 0) {
          await ExportSaleContractApi.changeConfirm(paramsResult.saleContractRespVO.id)
        }
        close()
      } else {
        let req =
          currContractType === 1
            ? ExportSaleContractApi.updateExportSaleContract
            : currContractType === 2
              ? DomesticSaleContractApi.updateDomesticSaleContract
              : FactorySaleContractApi.updateFactoryContract
        await req(paramsResult)
        message.success('提交成功')
        close()
      }
    }
  } finally {
    loading.value = false
    let localData = localStorage.getItem(pagePath)
    if (localData && props.mode == 'create' && props?.type != 'change') {
      localStorage.removeItem(pagePath)
    }
  }
}

// 变更数据保存/提交
const changeSubmit = async (type?) => {
  try {
    let paramsResult = await getParams(type)
    if (!paramsResult) return false

    currContractType === 1
      ? await ExportSaleContractChangeApi.updateChange({
          ...paramsResult,
          submitFlag: type
        })
      : await DomesticSaleContractChangeApi.updateChange({
          ...paramsResult,
          submitFlag: type
        })

    message.success('提交成功')
    close()
  } finally {
    loading.value = false
  }
}

const screenHeight = ref(0)
onBeforeMount(() => {
  getName([1757, countryListData.countryList], 1)
})

const showFlag = ref(false)
const custDefaultObj = reactive({})
const tableMaxHeight = ref(0)
const companyList = ref([])
onMounted(async () => {
  console.log('[ExportSaleContractForm] onMounted 开始, loading:', loading.value, 'parentDataReady:', parentDataReady.value)
  try{

  loading.value = true
  formRef.value.resetForm()
  // 获取屏幕高度
  screenHeight.value = window.innerHeight
  // 监听窗口大小变化，更新屏幕高度
  window.addEventListener('resize', () => {
    screenHeight.value = window.innerHeight
  })
  companyList.value = await getcompanySimpleList()

  currItemType.value = globalStore().changeType == 'change' ? 'change' : currItemType.value
  if (currItemType.value === 'change') {
    // 变更列表页面的编辑/提交按钮

    if (props?.changeEdit) {
      updateDialogActions(
        <el-button
          isShow={false}
          onClick={() => {
            changeSubmit(0)
          }}
          key="save"
        >
          保存
        </el-button>,
        <el-button
          onClick={() => {
            changeSubmit(1)
          }}
          key="submit"
        >
          提交
        </el-button>
      )
    } else {
      updateDialogActions(
        <el-button
          type="primary"
          onClick={() => {
            handleChange()
          }}
          key="submit"
        >
          变更
        </el-button>
      )
    }
  } else {
    updateDialogActions(
      <el-button
        onClick={() => {
          submitForm(0)
        }}
        type="primary"
        key="save"
      >
        保存
      </el-button>,
      <el-button
        onClick={() => {
          submitForm('submit')
        }}
        key="submit"
      >
        提交
      </el-button>
    )
  }

  simpleUserList.value = await UserApi.getSimpleUserList()
  if (props.mode === 'edit' || props.mode === 'change' || props.mode === 'copy') {
    let res = null
    // 变更单的编辑
    if (props.changeEdit) {
      res =
        currContractType === 1
          ? await ExportSaleContractChangeApi.getChangeInfo({ id: queryId })
          : await DomesticSaleContractChangeApi.getChangeInfo({ id: queryId })
    } else {
      res =
        currContractType === 1
          ? await ExportSaleContractApi.getExportSaleContract({ id: queryId })
          : currContractType === 2
            ? await DomesticSaleContractApi.getDomesticSaleContract({ id: queryId })
            : await FactorySaleContractApi.getFactoryContract({ id: queryId })
    }
    let custSimpleList = await PurchasePlanApi.getCustSimpleList({ code: res.custCode })
    if (res?.custCode && isValidArray(custSimpleList?.list)) {
      let currentCust = custSimpleList.list.filter((item) => {
        return item.code == res.custCode
      })
      if (currentCust?.length) {
        res.creditFlag = currentCust[0].creditFlag
        setCompanyPathListRef(currentCust[0], 2)
        companyCustLinkRef.value = currentCust[0].custLink
        settlementListData.value = currentCust[0].settlementList
        producedFlag.value = currentCust[0].internalFlag === 1 && currContractType != 1 ? 0 : 1
      }
    }
    if (res?.addSubItemList?.length) {
      res.addSubItemList.forEach((e) => {
        e.amountFormat = e?.amount?.amount
      })
    }
    oldSaleContractRespVO = currItemType.value === 'change' ? cloneDeep(res) : undefined
    // let custRes = await PurchasePlanApi.getCustSimpleList({ name: res.custName })
    Object.assign(custDefaultObj, custSimpleList?.list[0])
    Object.assign(formData, getPageObj(res))
    showFlag.value = true
  } else {
    await setLocalData()
  }
  await setRateList()
  changeSettlementTermType()
  // 等待 DOM 更新
  await nextTick()
  setTimeout(() => {
    if (formRef.value) {
      tableMaxHeight.value = formRef.value.bottomHeight
    }
  }, 100)
  console.log('[ExportSaleContractForm] 数据加载完成，设置parentDataReady=true')
  // 通知子组件父组件数据已加载完成，可以开始触发loaded事件
  parentDataReady.value = true
  console.log('[ExportSaleContractForm] parentDataReady已设置为true, 当前childLoadedCount:', childLoadedCount.value)
  } finally {
    // loading由handleChildLoaded控制
    console.log('[ExportSaleContractForm] onMounted finally, loading:', loading.value, 'childLoadedCount:', childLoadedCount.value)
  }
})

const setLocalData = async () => {
  const localData = localStorage.getItem(pagePath)
  if (localData && props.mode == 'create') {
    localFlag.value = true
    showFlag.value = false
    try {
      await ElMessageBox.confirm('检测到上次有未提交的数据，是否加载？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      const localJsonData = JSON.parse(localData!!)
      let custSimpleList = await PurchasePlanApi.getCustSimpleList({
        code: localJsonData.data.custCode
      })
      if (localJsonData.data?.custCode && isValidArray(custSimpleList?.list)) {
        let currentCust = custSimpleList.list.filter((item) => {
          return item.code == localJsonData.data.custCode
        })
        if (currentCust?.length) {
          localJsonData.data.creditFlag = currentCust[0].creditFlag
          setCompanyPathListRef(currentCust[0], 2)
          companyCustLinkRef.value = currentCust[0].custLink
          settlementListData.value = currentCust[0].settlementList
          producedFlag.value = currentCust[0].internalFlag === 1 && currContractType != 1 ? 0 : 1
        }
      }
      Object.assign(custDefaultObj, custSimpleList?.list[0])
      Object.assign(formData, localJsonData.data)
      showFlag.value = true
    } catch {
      showFlag.value = true
      localStorage.removeItem(pagePath)
    }
  } else {
    localFlag.value = false
    formData.sales = { ...userInfo.user, userId: userInfo.user?.id }
    formData.salesNickname = userInfo.user?.nickname
    formData.salesId = userInfo.user?.id
    formData.salesDeptName = userInfo.user?.deptName
    showFlag.value = true
  }
}

// if (props.mode != 'edit') {
//   basicSchemas = basicSchemas.filter((item, index) => {
//     return item.field != 'code'
//   })
// }

const getPageObj = (res) => {
  if (res.companyPath) {
    res.companyPathName = getCompanyPathNameFromObj(res.companyPath)
    companyIdList.value = getCompanyIdList(res.companyPath)
  }
  let obj = {
    ...res,
    salesNickname: res.sales?.nickname ? res.sales.nickname : '',
    salesDeptName: res.sales?.deptName ? res.sales.deptName : '',
    totalAmountFormat: res.totalAmount.amount ? res.totalAmount.amount.toFixed(3) : '0',
    estimatedTotalFreightFormat: res.estimatedTotalFreight.amount
      ? res.estimatedTotalFreight.amount.toFixed(3)
      : '0',
    commissionFormat: res.commission.amount ? res.commission.amount.toFixed(3) : '0',
    platformFeeFormat: res.platformFee.amount ? res.platformFee.amount.toFixed(3) : '0',
    sinosureFeeFormat: res.sinosureFee.amount ? res.sinosureFee.amount.toFixed(3) : '0',
    additionAmountFormat: res.additionAmount.amount ? res.additionAmount.amount.toFixed(3) : '0',
    deductionAmountFormat: res.deductionAmount.amount ? res.deductionAmount.amount.toFixed(3) : '0',
    totalGoodsValueFormat: res.totalGoodsValue.amount ? res.totalGoodsValue.amount.toFixed(3) : '0',
    totalPurchaseFormat: amountListFormat(res.totalPurchase),
    totalVatRefundFormat: res.totalVatRefund.amount ? res.totalVatRefund.amount.toFixed(3) : '0',
    orderGrossProfitFormat: res.orderGrossProfit.amount
      ? res.orderGrossProfit.amount.toFixed(3)
      : '0',
    grossProfitMarginFormat: Number(res.grossProfitMargin * 100).toFixed(2),
    estimatedPackingMaterialsFormat: res.estimatedPackingMaterials?.amount
      ? res.estimatedPackingMaterials?.amount.toFixed(3)
      : '0',
    totalVolumeFormat: res.totalVolume ? formatNum(res.totalVolume / 1000000, volPrecision) : '0',
    totalGrossweightFormat: res.totalGrossweight.weight ? res.totalGrossweight.weight : 0,
    totalWeightFormat: res.totalWeight.weight ? res.totalWeight.weight : 0,
    totalQuantityFormat: res.totalQuantity ? res.totalQuantity.toFixed(0) : 0,
    bulkHandlingVolumeFormat: res.bulkHandlingVolume ? res.bulkHandlingVolume / 1000000 : 0,
    salesId: res.sales?.userId,
    managerId: res.manager?.userId,
    managerDeptName: res.manager?.deptName,
    code: props.mode === 'copy' ? undefined : res.code,
    changeStatus: props.mode === 'copy' ? 1 : res.changeStatus
  }

  if (props.mode === 'copy') {
    obj.collectionPlanList = setCollectionPlanList(obj.settlementId)
    obj.addSubItemList = []
    obj.children = obj.children.map((item) => {
      return {
        ...item,
        id: undefined,
        realPurchaseWithTaxPrice: undefined,
        inventoryQuantity: undefined,
        currentLockQuantity: undefined,
        realLockQuantity: undefined,
        realPurchaseQuantity: undefined,
        status: undefined,
        changeFlag: null,
        shippedQuantity: undefined,
        transferShippedQuantity: undefined,
        stockLockSaveReqVOList: undefined,
        stockDetailRespVOList: undefined,
        sourceCode: undefined,
        effectRangeList: null,
        uniqueCode: undefined,
        sourceUniqueCode: undefined,
        billStatus: undefined,
        abnormalStatus: undefined,
        abnormalRemark: undefined,
        billQuantity: undefined,
        convertShipmentFlag: undefined,
        shipmentTotalQuantity: undefined,
        skuDeletedFlag: undefined,
        convertPurchaseFlag: undefined,
        lockMsg: undefined,
        reLockFlag: undefined,
        splitFlag: undefined,
        splitPurchaseFlag: undefined,
        splitPurchaseQuantity: undefined,
        splitPurchaseList: undefined,
        stockLockPrice: undefined,
        stockLockTotalPrice: undefined,
        converNoticeFlag: undefined
      }
    })
    return { ...obj, status: undefined, auditStatus: undefined }
  } else {
    return obj
  }
}

const handleChange = async () => {
  let paramsResult = await getParams('change')
  if (!paramsResult) return false
  const res = await ExportSaleContractChangeApi.getChangeEffect(paramsResult)
  changeTipsRef.value.open(res)
}

const submitChange = () => {
  submitForm('submit')
}

onUnmounted(() => {
  window.removeEventListener('resize', () => {
    screenHeight.value = window.innerHeight
  })
})
</script>
<style lang="scss" scoped>
.tab_item {
  width: 120px;
  text-align: center;
  cursor: pointer;
  position: relative;
}

.tab_item.on {
  color: #409eff;
}

// .tab_item ::after {
//   content: '""';
//   display: block;
//   width: 100%;
//   height: 2px;
//   background-color: #409eff;
//   position: absolute;
//   bottom: 0;
//   left: 0;
// }
.text-underline {
  margin: 2px auto 0px;
  height: 2px;
  background-color: #fff;
  border-radius: 5px;
}

.text-underline.on {
  background-color: #409eff;
}
</style>
