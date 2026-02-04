<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'sms:export-sale-contract:create',
      handler: () => submitForm()
    }"
    :submitAction="{
      permi: 'sms:export-sale-contract:submit',
      handler: () => submitForm('submit')
    }"
    :closeAction="{
      handler: close
    }"
  >
    <el-row>
      <el-col
        :span="18"
        :style="{
          height: `${screenHeight}px`,
          overflow: `auto`
        }"
      >
        <eplus-form-items
          title="基本信息"
          :formSchemas="basicSchemas"
        >
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
          <template #settlementName>
            <el-select
              v-model="formData.settlementId"
              clearable
              style="width: 100%"
              @change="changeSettlement"
            >
              <el-option
                v-for="item in settlementListData"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </template>
          <template #annex>
            <UploadList
              ref="UploadListRef"
              :fileList="formData.annex"
              @success="getFileList"
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
          <template #tradeCountryName>
            <el-autocomplete
              v-model="formData.tradeCountryName"
              :fetch-suggestions="queryCountry"
              clearable
              class="inline-input"
              style="width: 100%"
              @clear="() => clearTradeCountry()"
              @select="
                (item) => {
                  formData.tradeCountryId = item.id
                  formData.tradeCountryName = item.name
                  formData.tradeCountryArea = item.regionName
                }
              "
              placeholder="请选择贸易国别"
            />
          </template>
          <template #departureCountryName>
            <el-autocomplete
              v-model="formData.departureCountryName"
              :fetch-suggestions="queryCountry"
              clearable
              class="inline-input"
              style="width: 100%"
              @clear="() => clearDepartureCountry()"
              @select="
                (item) => {
                  formData.departureCountryId = item.id
                  formData.departureCountryName = item.name
                  formData.departureCountryArea = item.regionName
                }
              "
              placeholder="请选择出运国"
            />
          </template>
          <template #destinationPortName>
            <eplus-input-search-select
              v-if="formData.departureCountryId"
              :key="formData.departureCountryId"
              v-model="formData.destinationPortName"
              :api="PortApi.getPortList"
              :params="{
                pageSize: 100,
                pageNo: 1,
                status: PortStatusStatusEnum.NORMAL.status,
                countryId: formData.departureCountryId
              }"
              keyword="nameEng"
              label="nameEng"
              value="nameEng"
              class="!w-100%"
              placeholder="请选择"
              @change-emit="changeDestinationPort"
            />
            <el-select
              v-else
              class="!w-100%"
            />
          </template>
          <template #departurePortName>
            <eplus-input-search-select
              v-if="formData.tradeCountryId"
              :key="formData.tradeCountryId"
              v-model="formData.departurePortName"
              :api="PortApi.getPortList"
              :params="{
                pageSize: 100,
                pageNo: 1,
                status: PortStatusStatusEnum.NORMAL.status,
                countryId: formData.tradeCountryId
              }"
              keyword="nameEng"
              label="nameEng"
              value="nameEng"
              class="!w-100%"
              placeholder="请选择"
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
            />
          </template>
        </eplus-form-items>

        <eplus-form-items
          title="运输费"
          :formSchemas="feeSchemas"
        >
          <template #trailerFee>
            <EplusMoney
              v-model:amount="formData.trailerFee.amount"
              v-model:currency="formData.trailerFee.currency"
              @change="changeTrailerFee(formData.trailerFee.amount)"
            />
          </template>
          <template #estimatedTotalFreight>
            <el-input
              v-model="formData.estimatedTotalFreightFormat"
              disabled
            >
              <template #append>
                <span style="width: 50px">
                  {{ formData.estimatedTotalFreight.currency }}
                </span>
              </template>
            </el-input>
          </template>
        </eplus-form-items>

        <eplus-form-items
          title="佣金和平台费"
          :formSchemas="platFormFeeSchemas"
        >
          <template #commission>
            <el-input
              v-model="formData.commissionFormat"
              disabled
            >
              <template #append>
                <span style="width: 50px">
                  {{ formData.commission.currency }}
                </span>
              </template>
            </el-input>
          </template>
          <template #platformFee>
            <el-input
              v-model="formData.platformFeeFormat"
              disabled
            >
              <template #append>
                <span style="width: 50px">
                  {{ formData.platformFee.currency }}
                </span>
              </template>
            </el-input>
          </template>
        </eplus-form-items>

        <eplus-form-items
          title="其他费用"
          :formSchemas="otherFeeSchemas"
        >
          <template #insuranceFee>
            <EplusMoney
              v-model:amount="formData.insuranceFee.amount"
              v-model:currency="formData.insuranceFee.currency"
              :currencyDisabled="true"
              @change="changeInsuranceFee(formData.insuranceFee.amount)"
            />
          </template>
          <template #sinosureFee>
            <el-input
              v-model="formData.sinosureFeeFormat"
              disabled
            >
              <template #append>
                <span style="width: 50px">
                  {{ formData.sinosureFee.currency }}
                </span>
              </template>
            </el-input>
          </template>
          <template #inspectionFee>
            <EplusMoney
              v-model:amount="formData.inspectionFee.amount"
              v-model:currency="formData.inspectionFee.currency"
            />
          </template>
          <template #additionAmount>
            <el-input
              v-model="formData.additionAmountFormat"
              disabled
            >
              <template #append>
                <span style="width: 50px">
                  {{ formData.additionAmount.currency }}
                </span>
              </template>
            </el-input>
          </template>
          <template #deductionAmount>
            <el-input
              v-model="formData.deductionAmountFormat"
              disabled
            >
              <template #append>
                <span style="width: 50px">
                  {{ formData.deductionAmount.currency }}
                </span>
              </template>
            </el-input>
          </template>
          <template #estimatedPackingMaterials>
            <el-input
              v-model="formData.estimatedPackingMaterialsFormat"
              disabled
            >
              <template #append>
                <span style="width: 50px">
                  {{ formData.estimatedPackingMaterials.currency }}
                </span>
              </template>
            </el-input>
          </template>
          <template #accessoriesPurchaseTotal>
            <el-input
              v-model="formData.accessoriesPurchaseTotalFormat"
              disabled
            >
              <template #append>
                <span style="width: 50px">
                  {{ formData.accessoriesPurchaseTotal.currency }}
                </span>
              </template>
            </el-input>
          </template>
        </eplus-form-items>
      </el-col>
      <el-col :span="6">
        <eplus-form-items
          title="合计信息"
          :formSchemas="sumSchemas"
        >
          <template #totalGrossweightFormat>
            <el-input
              v-model="formData.totalGrossweightFormat"
              disabled
            >
              <template #append>
                <span style="width: 50px">kg</span>
              </template>
            </el-input>
          </template>

          <template #totalWeightFormat>
            <el-input
              v-model="formData.totalWeightFormat"
              disabled
            >
              <template #append>
                <span style="width: 50px">kg</span>
              </template>
            </el-input>
          </template>

          <template #totalVolumeFormat>
            <el-input
              v-model="formData.totalVolumeFormat"
              disabled
            >
              <template #append>
                <span style="width: 50px">m3</span>
              </template>
            </el-input>
          </template>

          <template #totalGoodsValue>
            <el-input
              v-model="formData.totalGoodsValueFormat"
              disabled
            >
              <template #append>
                <span style="width: 50px">
                  {{ formData.totalGoodsValue.currency }}
                </span>
              </template>
            </el-input>
          </template>
          <template #totalPurchase>
            <el-input
              v-model="formData.totalPurchaseFormat"
              disabled
            />
          </template>
          <template #totalVatRefund>
            <el-input
              v-model="formData.totalVatRefundFormat"
              disabled
            >
              <template #append>
                <span style="width: 50px">
                  {{ formData.totalVatRefund.currency }}
                </span>
              </template>
            </el-input>
          </template>
          <template #orderGrossProfit>
            <el-input
              v-model="formData.orderGrossProfitFormat"
              disabled
            >
              <template #append>
                <span style="width: 50px">
                  {{ formData.orderGrossProfit.currency }}
                </span>
              </template>
            </el-input>
          </template>
        </eplus-form-items>
      </el-col>
    </el-row>
    <el-tabs
      v-model="activeName"
      type="card"
      class="demo-tabs mb20px"
    >
      <el-tab-pane
        label="销售明细"
        name="first"
      >
        <eplus-form-items
          title="产品信息"
          :formSchemas="collectionSchemas"
        >
          <template #saleContractItemList>
            <PurchaseProducts
              :formData="formData"
              ref="saleContractItemRef"
              :loading="!parentDataReady"
              @loaded="handleChildLoaded('PurchaseProducts')"
            />
          </template>
        </eplus-form-items>
      </el-tab-pane>
      <el-tab-pane
        label="加减项"
        name="second"
      >
        <eplus-form-items
          title="加减项"
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
      </el-tab-pane>
      <el-tab-pane
        label="收款计划"
        name="third"
      >
        <eplus-form-items
          title="收款计划"
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
      </el-tab-pane>
    </el-tabs>
  </eplus-form>
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import PurchaseProducts from '../components/PurchaseProducts.vue'
import * as ExportSaleContractApi from '@/api/sms/saleContract/export'
import * as ExportSaleContractChangeApi from '@/api/sms/saleContract/export/change'
import * as UserApi from '@/api/system/user'
import UploadList from '@/components/UploadList/index.vue'
import * as PurchasePlanApi from '@/api/scm/purchasePlan'
import { useCountryStore } from '@/store/modules/countrylist'
import { PortStatusStatusEnum } from '@/utils/constants'
import subItemTable from '../components/AddSubItemTable.vue'
import collectionPlanTable from '../components/CollectionPlanTable.vue'
import { useRateStore } from '@/store/modules/rate'
import { DICT_TYPE, getDictLabel, getDictValue } from '@/utils/dict'
import { getCompanyPathNameFromObj } from '@/utils/companyPathUtils'
import * as PortApi from '@/api/system/port'
import { cloneDeep } from 'lodash-es'
import { amountListFormat, getAmountListSumRmb } from '@/utils/index'

const rateList = useRateStore().rateList
const message = useMessage()
const saleType = getDictValue(DICT_TYPE.SALE_TYPE, '外销合同')
defineOptions({ name: 'ContractForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const queryId: string = (props.id || '') as string
const loading = ref(true)
// 父组件数据是否已加载完成（用于通知子组件可以开始触发loaded事件）
const parentDataReady = ref(false)
// 子组件加载计数器
const CHILD_TABLE_COUNT = 3 // PurchaseProducts, subItemTable, collectionPlanTable
const childLoadedCount = ref(0)
const handleChildLoaded = (source?: string) => {
  childLoadedCount.value++
  console.log(`[ChangeForm] 子组件触发loaded: ${source || 'unknown'}, 当前计数: ${childLoadedCount.value}/${CHILD_TABLE_COUNT}`)
  if (childLoadedCount.value >= CHILD_TABLE_COUNT) {
    console.log('[ChangeForm] 所有子组件加载完成，隐藏loading')
    loading.value = false
  }
}
const simpleUserList = ref([])
const settlementListData = ref([])
const companyPathListRef = ref([])
//关联客户
const companyCustLinkRef = ref([])
const activeName = ref('first')
const saleContractItemRef = ref()
const formData = reactive({
  totalVolume: '0',
  totalVolumeFormat: '0',
  addSubItemList: [],
  annex: [],
  collectionPlanList: [],
  fortyFootContainerNum: '0',
  fortyFootCabinetNum: '0',
  twentyFootCabinetNum: '0',
  bulkHandlingVolume: '0',
  bulkHandlingVolumeFormat: '0',
  trailerFee: { amount: '0', currency: 'RMB' },
  estimatedTotalFreightFormat: '0.00',
  estimatedTotalFreight: { amount: '0', currency: 'RMB' },
  commissionFormat: '0.00',
  commission: { amount: '0.00', currency: 'RMB' },
  platformFeeFormat: '0.00',
  platformFee: { amount: '0.00', currency: 'RMB' },
  insuranceFee: { amount: '0.00', currency: 'RMB' },
  sinosureFeeFormat: '0.00',
  sinosureFee: { amount: '0.00', currency: 'RMB' },
  inspectionFee: { amount: '0.00', currency: 'RMB' },
  additionAmountFormat: '0.00',
  additionAmount: { amount: '0.00', currency: 'RMB' },
  deductionAmountFormat: '0.00',
  deductionAmount: { amount: '0.00', currency: 'RMB' },
  totalGoodsValueFormat: '0.00',
  totalGoodsValue: { amount: '0.00', currency: 'RMB' },
  totalPurchaseFormat: '0.00',
  totalPurchase: [],
  totalVatRefundFormat: '0.00',
  totalVatRefund: { amount: '0.00', currency: 'RMB' },
  orderGrossProfitFormat: '0.00',
  orderGrossProfit: { amount: '0.00', currency: 'RMB' },
  estimatedPackingMaterialsFormat: '0.00',
  estimatedPackingMaterials: { amount: '0.00', currency: 'RMB' },
  accessoriesPurchaseTotalFormat: '0.00',
  accessoriesPurchaseTotal: { amount: '0.00', currency: 'RMB' },
  receivableExchange: { amount: '0.00', currency: 'RMB' },
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
  totalBoxes: '0'
})
provide('formData', formData)
const { close } = inject('dialogEmits') as {
  close: () => void
}

const addSubItemListRef = ref([])
watch(
  () => addSubItemListRef.value?.tableList,
  (tableList, oldValue) => {
    if (!oldValue || !oldValue.length) return
    var additionAmount = 0
    var deductionAmount = 0
    tableList.forEach((item) => {
      if (item.calculationType && item.amountFormat) {
        var calculationType = getDictLabel(DICT_TYPE.CALCULATION_TYPE, item.calculationType)
        if (calculationType == '加项') {
          additionAmount = Number(additionAmount) + Number(item.amountFormat)
        } else {
          deductionAmount = Number(deductionAmount) + Number(item.amountFormat)
        }
      }
    })
    formData.additionAmountFormat = Number(additionAmount).toFixed(2)
    formData.additionAmount.amount = Number(additionAmount).toFixed(6)
    changeAdditionAmount(additionAmount)
    formData.deductionAmountFormat = Number(deductionAmount).toFixed(2)
    formData.deductionAmount.amount = Number(deductionAmount).toFixed(6)
  },
  { deep: true }
)

watch(
  () => saleContractItemRef.value?.computedTableData,
  (computedTableData) => {
    //采购合计
    // var totalPurchaseSum = computedTableData.totalPurchaseSum
    var listTotalPurchase = computedTableData.listTotalPurchase
    //数量合计
    var totalQuantitySum = computedTableData.totalQuantitySum
    //体积合计
    var totalVolumeSum = computedTableData.totalVolumeSum
    //佣金金额合计
    var commissionAmountSum = computedTableData.commissionAmountSum
    //箱数合计
    var totalBoxesSum = computedTableData.totalBoxesSum
    //平台费合计
    var platformFeeAmountSum = computedTableData.platformFeeAmountSum
    //货值合计
    var totalGoodsValueAmountSum = computedTableData.totalGoodsValueAmountSum
    //毛重合计
    var totalGrossWeightSum = computedTableData.totalGrossWeightSum
    //净重合计
    var totalWeightSum = computedTableData.totalWeightSum
    //退税率合计
    var totalVatRefundAmountSum = computedTableData.totalVatRefundAmountSum
    //预计包材合计
    var estimatedPackingMaterialsSum = computedTableData.estimatedPackingMaterialsSum
    //预估总运费
    var estimatedTotalFreightSum = computedTableData.estimatedTotalFreightSum
    //40尺高柜
    var fortyFootContainerNum = computedTableData.fortyFootContainerNum
    //20尺柜
    var twentyFootCabinetNum = computedTableData.twentyFootCabinetNum
    //40尺柜
    var fortyFootCabinetNum = computedTableData.fortyFootCabinetNum
    //散货
    var bulkHandlingVolume = computedTableData.bulkHandlingVolume
    //配件采购费合计
    var accessoriesPurchaseTotalSum = computedTableData.accessoriesPurchaseTotalSum

    if (totalVolumeSum || totalVolumeSum == 0) {
      //体积合计页面显示字段  单位立方米 保留6位小数
      formData.totalVolumeFormat = (Number(totalVolumeSum) / 1000000).toFixed(6)
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

    if (listTotalPurchase?.length) {
      formData.totalPurchase = listTotalPurchase
      formData.totalPurchaseFormat = amountListFormat(listTotalPurchase)
    }

    //总佣金金额
    if (commissionAmountSum || commissionAmountSum == 0) {
      formData.commissionFormat = Number(commissionAmountSum).toFixed(2)
      formData.commission.amount = Number(commissionAmountSum).toFixed(6)
    }
    //总箱数
    if (totalBoxesSum || totalBoxesSum == 0) {
      formData.totalBoxes = Number(totalBoxesSum).toFixed(0)
    }
    //总平台费
    if (platformFeeAmountSum || platformFeeAmountSum == 0) {
      formData.platformFeeFormat = Number(platformFeeAmountSum).toFixed(2)
      formData.platformFee.amount = Number(platformFeeAmountSum).toFixed(6)
    }
    //货值合计
    if (totalGoodsValueAmountSum || totalGoodsValueAmountSum == 0) {
      //应收汇款 = 货值合计
      formData.receivableExchange.amount = Number(totalGoodsValueAmountSum).toFixed(6)
      formData.totalGoodsValue.amount = Number(totalGoodsValueAmountSum).toFixed(6)
      formData.totalGoodsValueFormat = Number(totalGoodsValueAmountSum).toFixed(2)
      collectionPlanListRef.value.resetItems()
    }
    //净重合计
    if (totalWeightSum || totalWeightSum == 0) {
      formData.totalWeightFormat = Number(totalWeightSum).toFixed(2)
      formData.totalWeight.weight = Number(totalWeightSum).toFixed(6)
    }
    //毛重合计
    if (totalGrossWeightSum || totalGrossWeightSum == 0) {
      formData.totalGrossweightFormat = Number(totalGrossWeightSum).toFixed(2)
      formData.totalGrossweight.weight = Number(totalGrossWeightSum).toFixed(6)
    }
    //退税率合计
    if (totalVatRefundAmountSum || totalVatRefundAmountSum == 0) {
      formData.totalVatRefund.amount = Number(totalVatRefundAmountSum).toFixed(6)
      formData.totalVatRefundFormat = Number(totalVatRefundAmountSum).toFixed(2)
    }
    //数量合计
    if (totalQuantitySum || totalQuantitySum == 0) {
      formData.totalQuantityFormat = Number(totalQuantitySum).toFixed(0)
      formData.totalQuantity = Number(totalQuantitySum)
    }
    //预计包材合计
    if (estimatedPackingMaterialsSum || estimatedPackingMaterialsSum == 0) {
      formData.estimatedPackingMaterialsFormat = Number(estimatedPackingMaterialsSum).toFixed(2)
      formData.estimatedPackingMaterials.amount = Number(estimatedPackingMaterialsSum).toFixed(6)
    }
    //预估总运费
    if (estimatedTotalFreightSum || estimatedTotalFreightSum == 0) {
      formData.estimatedTotalFreightFormat = Number(estimatedTotalFreightSum).toFixed(2)
      formData.estimatedTotalFreight.amount = Number(estimatedTotalFreightSum).toFixed(6)
    }
    //配件采购费
    if (accessoriesPurchaseTotalSum || accessoriesPurchaseTotalSum == 0) {
      formData.accessoriesPurchaseTotalFormat = Number(accessoriesPurchaseTotalSum).toFixed(2)
      formData.accessoriesPurchaseTotal.amount = Number(accessoriesPurchaseTotalSum).toFixed(6)
    }
    //毛利润=收入-成本
    //其中收入=货值合计*汇率+加项金额+退税金额
    //其中成本=采购合计+包材+配件+总运费+验货费+平台费+减项金额+保险费+中信保费
    //采购合计=采购总金额*对人民币汇率，需要看采购币种
    var rate = 0
    if (formData.currency) {
      rate = rateList[formData.currency]
    }
    //收入
    var addAmount = 0
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
    var subAmount = 0
    //采购合计
    if (listTotalPurchase?.length) {
      var tmp = Number(getAmountListSumRmb(listTotalPurchase, rateList))
      if (tmp) {
        subAmount = tmp
      }
    }
    //包材
    if (formData.estimatedPackingMaterials.amount) {
      subAmount = Number(subAmount) + Number(formData.estimatedPackingMaterials.amount)
    }
    //配件
    if (formData.accessoriesPurchaseTotal.amount) {
      subAmount = Number(subAmount) + Number(formData.accessoriesPurchaseTotal.amount)
    }
    //总运费
    if (formData.estimatedTotalFreight.amount) {
      subAmount = Number(subAmount) + Number(formData.estimatedTotalFreight.amount)
    }
    //验货费
    if (formData.inspectionFee.amount) {
      subAmount = Number(subAmount) + Number(formData.inspectionFee.amount)
    }
    //平台费
    if (formData.platformFee.amount) {
      subAmount = Number(subAmount) + Number(formData.platformFee.amount)
    }
    //减项金额
    if (formData.deductionAmount.amount) {
      subAmount = Number(subAmount) + Number(formData.deductionAmount.amount)
    }
    //保险费
    if (formData.insuranceFee.amount) {
      subAmount = Number(subAmount) + Number(formData.insuranceFee.amount)
    }
    //中中信保费用
    if (formData.sinosureFee.amount) {
      subAmount = Number(subAmount) + Number(formData.sinosureFee.amount)
    }
    var orderGrossProfitSum = addAmount - subAmount
    if (orderGrossProfitSum || orderGrossProfitSum == 0) {
      formData.orderGrossProfit.amount = Number(orderGrossProfitSum).toFixed(6)
      formData.orderGrossProfitFormat = Number(orderGrossProfitSum).toFixed(2)
    }

    //毛利率=毛利润/收入
    var grossProfitMarginSum = 0
    if (orderGrossProfitSum == 0) {
      formData.grossProfitMarginFormat = '0'
      formData.grossProfitMargin = '0'
    } else if (orderGrossProfitSum && addAmount) {
      grossProfitMarginSum = Number(orderGrossProfitSum) / Number(addAmount)
      formData.grossProfitMarginFormat = Number(grossProfitMarginSum * 100).toFixed(6)
      formData.grossProfitMargin = Number(grossProfitMarginSum).toFixed(6)
    }
  },
  { deep: true }
)

const collectionPlanListRef = ref([])

const clearTradeCountry = () => {
  formData.tradeCountryId = ''
  formData.tradeCountryName = ''
  formData.tradeCountryArea = ''
}

const clearDepartureCountry = () => {
  formData.departureCountryId = ''
  formData.departureCountryName = ''
  formData.departureCountryArea = ''
}

const changeAdditionAmount = (val) => {
  if (!val) {
    val = 0
  }
  saleContractItemRef.value?.setParentParams({ key: 'additionAmountContract', value: val })
}

const changeCurrency = (val) => {
  //佣金，货值合计币种和单据币种一致
  formData.commission.currency = val
  formData.totalGoodsValue.currency = val
  saleContractItemRef.value?.setParentParams({ key: 'currencyContract', value: val })
  saleContractItemRef.value?.tableList.forEach((item) => {
    saleContractItemRef.value?.refershTableRow(item)
  })
}
//运输方式
const changeTransportType = () => {
  saleContractItemRef.value?.setParentParams({
    key: 'transportTypeContract',
    value: formData.transportType
  })
  saleContractItemRef.value?.tableList.forEach((item) => {
    saleContractItemRef.value?.refershTableRow(item)
  })
}
//价格条款
const changeSettlementTermType = () => {
  saleContractItemRef.value?.setParentParams({
    key: 'settlementTermTypeContract',
    value: formData.settlementTermType
  })
  saleContractItemRef.value?.tableList.forEach((item) => {
    saleContractItemRef.value?.refershTableRow(item)
  })
}

const changeTrailerFee = (val) => {
  if (!val) {
    val = 0
  }
  saleContractItemRef.value?.setParentParams({ key: 'trailerFeeContract', value: val })
  saleContractItemRef.value?.tableList.forEach((item) => {
    saleContractItemRef.value?.refershTableRow(item)
  })
}

const changeInsuranceFee = (val) => {
  if (!val) {
    val = 0
  }
  saleContractItemRef.value?.setParentParams({ key: 'insuranceFeeContract', value: val })
  saleContractItemRef.value?.tableList.forEach((item) => {
    saleContractItemRef.value?.refershTableRow(item)
  })
}

//收款方式改变
const changeSettlement = (val) => {
  if (val) {
    var collectionPlanList = []
    var settlementList = settlementListData.value.filter((item) => item.id == val)
    if (settlementList?.length) {
      formData.settlementName = settlementList[0].name
      if (settlementList[0].collectionPlanList?.length) {
        collectionPlanList = settlementList[0].collectionPlanList
      }
    }
    formData.collectionPlanList = collectionPlanList
  }
}

const changecompanyPathName = (val) => {
  if (val) {
    let pathObj = companyPathListRef.value.find((item) => item.path === val)
    formData.companyId = pathObj.id
    formData.companyName = pathObj.path.split('->')[0]
    formData.companyPath = pathObj.companyPath
    formData.companyTitle = pathObj.companyPath.companyTitle
  }
}

const changeDeparturePort = (val, list) => {
  let item = list.find((item) => item.nameEng === val)
  formData.departurePortId = item.id
}
const changeDestinationPort = (val, list) => {
  let item = list.find((item) => item.nameEng === val)
  formData.destinationPortId = item.id
}
const changeUser = (val) => {
  if (val) {
    formData.salesDeptName = val.deptName
    formData.sales = { userId: val?.id, ...val }
  }
}
const countryListData = useCountryStore()
const createFilter = (queryString: string) => {
  return (restaurant) => {
    return restaurant.name.toLowerCase().indexOf(queryString.toLowerCase()) === 0
  }
}
const queryCountry = (queryString: string, fn: any) => {
  const results = queryString
    ? countryListData.countryList.filter(createFilter(queryString))
    : countryListData.countryList
  fn(
    results.map((r: any) => {
      return { ...r, value: r.name }
    })
  )
}

const getCustName = (val) => {
  if (val.length === 2) {
    if (val[1]) {
      val[1].filter((item) => {
        if (item.id === val[0]) {
          formData.custId = item.id
          formData.custName = item.name
          formData.custCode = item.code
          formData.custCountryId = item.countryId
          formData.custCountryName = item.countryName
          formData.custAreaName = item.regionName
          //订单路径
          if (item.companyPath?.length) {
            item.companyPath?.forEach((item) => {
              if (item.path) {
                item.pathName = getCompanyPathNameFromObj(item.path)
                if (item.defaultFlag == 1) {
                  formData.companyPathName = item.pathName
                  formData.companyId = item.path.id
                  formData.companyName = item.pathName.split('->')[0]
                  formData.companyPath = item.path
                  formData.companyTitle = item.path.companyTitle
                }
              }
            })
            setCompanyPathListRef(item)
          } else {
            formData.companyPathName = null
            formData.companyId = null
            formData.companyName = null
            formData.companyTitle = null
          }

          //关联客户
          if (item.custLink?.length) {
            companyCustLinkRef.value = item.custLink
          } else {
            companyCustLinkRef.value = []
          }

          //收款方式
          if (item.settlementList?.length) {
            settlementListData.value = item.settlementList
          } else {
            settlementListData.value = []
          }
        }
      })
    }
  }
}

const setCompanyPathListRef = (item) => {
  let arr = []
  item.companyPath?.forEach((item) => {
    if (item.path) {
      item.pathName = getCompanyPathNameFromObj(item.path)
      let obj = {
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
    editable: true,
    labelWidth: '0px',
    rules: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          if (collectionPlanListRef.value.tableList?.length === 0) {
            callback(new Error('收款计划不可为空'))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ]
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

let basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'custId',
    label: '客户名称',
    editable: true,
    component: (
      <eplus-input-search-select
        api={PurchasePlanApi.getCustSimpleList}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="name"
        label="name"
        value="id"
        class="!w-100%"
        placeholder="请选择"
        onChangeEmit={(...$event) => getCustName($event)}
      />
    ),
    span: 8,
    rules: {
      required: true,
      message: '请选择客户名称'
    }
  },
  {
    field: 'companyPathName',
    label: '订单路径',
    editable: true,
    span: 8,
    rules: {
      required: true,
      message: '请选择订单路径'
    }
  },
  {
    field: 'companyName',
    label: '下单主体',
    editable: true,
    span: 8
  },
  {
    field: 'custPo',
    label: '客户合同号',
    editable: true,
    component: <el-input />,
    span: 8
  },
  {
    field: 'settlementName',
    label: '收款方式',
    editable: true,
    span: 8,
    rules: {
      required: true,
      message: '请选择订收款方式'
    }
  },
  {
    field: 'custCountryName',
    label: '客户国别',
    editable: true,
    component: <el-input disabled />,
    span: 8
  },
  {
    field: 'currency',
    label: '交易币别',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.CURRENCY_TYPE}
        style="width:100%"
        onChange={changeCurrency}
      />
    ),
    span: 8,
    rules: {
      required: true,
      message: '请选择交易币别'
    }
  },
  {
    field: 'settlementTermType',
    label: '价格条款',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.PRICE_TYPE}
        style="width:100%"
        onChange={changeSettlementTermType}
      />
    ),
    span: 8,
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
    span: 8
  },
  {
    field: 'collectedCustId',
    label: '收款方',
    editable: true,
    span: 8
  },
  {
    field: 'receiveCustId',
    label: '收货方',
    editable: true,
    span: 8
  },
  {
    field: 'salesNickname',
    label: '业务员',
    editable: true,
    component: (
      <eplus-user-select
        onChange={changeUser}
        simpleUserList={simpleUserList.value}
      ></eplus-user-select>
    ),
    span: 8,
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
    component: <el-input disabled />,
    span: 8
  },
  {
    field: 'code',
    label: '外销合同号',
    editable: true,
    component: <el-input disabled />,
    span: 8
  },
  {
    field: 'annex',
    label: '附件',
    span: 24
  }
])

let transportSchemas: EplusFormSchema[] = reactive([
  {
    field: 'tradeCountryName',
    label: '贸易国别',
    rules: {
      required: true,
      message: '请选择贸易国别'
    }
  },
  {
    field: 'tradeCountryArea',
    label: '区域',
    component: <el-input disabled />
  },
  {
    field: 'departurePortName',
    label: '出运口岸',
    editable: true,
    span: 8,
    rules: {
      required: true,
      message: '请选择出运口岸',
      trigger: 'click'
    }
  },
  {
    field: 'departureCountryName',
    label: '出运国',
    rules: {
      required: true,
      message: '请选择出运国'
    }
  },
  {
    field: 'departureCountryArea',
    label: '区域',
    component: <el-input disabled />
  },
  {
    field: 'destinationPortName',
    label: '目的口岸',
    editable: true,
    span: 8,
    rules: {
      required: true,
      message: '请选择目的口岸',
      trigger: 'click'
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
    span: 8,
    rules: { required: true, message: '请选择运输方式' }
  },
  {
    field: 'custDeliveryDate',
    label: '客户交期',
    editable: true,
    span: 8,
    rules: {
      required: true,
      message: '请选择客户交期',
      trigger: 'click'
    }
  }
])

let feeSchemas: EplusFormSchema[] = reactive([
  {
    field: 'twentyFootCabinetNum',
    label: '20尺柜(个)',
    component: <el-input disabled />
  },
  {
    field: 'fortyFootCabinetNum',
    label: '40尺柜(个)',
    component: <el-input disabled />
  },
  {
    field: 'fortyFootContainerNum',
    label: '40尺高柜(个)',
    component: <el-input disabled />,
    span: 8
  },
  {
    field: 'bulkHandlingVolumeFormat',
    label: '散货(m³)',
    component: <el-input disabled />,
    span: 8
  },
  {
    field: 'trailerFee',
    label: '拖柜费',
    span: 8
  },
  {
    field: 'bookingFlag',
    label: '是否订舱',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.CONFIRM_TYPE}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择是否订舱',
      trigger: 'click'
    },
    span: 8
  },
  {
    field: 'estimatedTotalFreight',
    label: '预估总运费',
    span: 8
  }
])

let sumSchemas: EplusFormSchema[] = reactive([
  {
    field: 'totalBoxes',
    label: '箱数合计',
    component: <el-input disabled />,
    span: 24
  },
  {
    field: 'totalGrossweightFormat',
    label: '毛重合计',
    span: 24
  },
  {
    field: 'totalWeightFormat',
    label: '净重合计',
    span: 24
  },
  {
    field: 'totalVolumeFormat',
    label: '体积合计',
    span: 24
  },
  {
    field: 'totalGoodsValue',
    label: '货值合计',
    span: 24
  },
  {
    field: 'totalPurchase',
    label: '采购合计',
    span: 24
  },
  {
    field: 'totalVatRefund',
    label: '退税合计',
    span: 24
  },
  {
    field: 'totalQuantityFormat',
    label: '数量合计',
    component: <el-input disabled />,
    span: 24
  },
  {
    field: 'orderGrossProfit',
    label: '订单毛利',
    span: 24
  },
  {
    field: 'grossProfitMarginFormat',
    label: '毛利率%',
    component: <el-input disabled />,
    span: 24
  }
])

let platFormFeeSchemas: EplusFormSchema[] = reactive([
  {
    field: 'commission',
    label: '佣金',
    span: 8
  },
  {
    field: 'platformFee',
    label: '平台费',
    span: 8
  }
])

let otherFeeSchemas: EplusFormSchema[] = reactive([
  {
    field: 'insuranceFee',
    label: '保险费(RMB)',
    span: 8
  },
  {
    field: 'sinosureFee',
    label: '中信保费用',
    span: 8
  },
  {
    field: 'additionAmount',
    label: '加项金额(RMB)',
    span: 8
  },
  {
    field: 'deductionAmount',
    label: '减项金额(RMB)',
    span: 8
  },
  {
    field: 'inspectionFee',
    label: '验货费用',
    span: 8
  },
  {
    field: 'estimatedPackingMaterials',
    label: '预计包材合计',
    span: 8
  },
  {
    field: 'accessoriesPurchaseTotal',
    label: '配件采购费用合计',
    span: 8
  }
])

const getFileList = (params) => {
  formData.annex = params
}
const collectionSchemas: EplusFormSchema[] = [
  {
    field: 'saleContractItemList',
    label: '',
    labelWidth: '0px',
    editable: true,
    span: 24,
    rules: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          if (saleContractItemRef.value.tableList?.length === 0) {
            callback(new Error('请选择产品信息'))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ]
  }
]
const emit = defineEmits(['handleSuccess', 'sucess'])
const formRef = ref()
let oldSaleContractRespVO = reactive({})

//处理提交前的formdata
const getParams = async (type?) => {
  let valid = await formRef.value.validate()
  if (!valid) {
    message.error('有未填写项必填项目,请确认！')
    return false
  }
  let params: any = JSON.parse(JSON.stringify(formData))
  params.saleType = saleType
  let result = await saleContractItemRef.value.checkData()
  if (result) {
    params.children = result
  } else {
    return false
  }
  let subItemResult = await addSubItemListRef.value?.checkData()
  if (subItemResult) {
    params.addSubItemList = subItemResult
  } else {
    return false
  }
  let collectionPlanResult = await collectionPlanListRef.value?.checkData()
  if (collectionPlanResult) {
    params.collectionPlanList = collectionPlanResult
  } else {
    return false
  }
  return { sourceType: 1, submitFlag: type === 'submit' ? 1 : 0, ...params }
}

const submitForm = async (type?) => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    if (props.mode == 'create') {
      var res = await ExportSaleContractChangeApi.createChange({
        old_saleContractRespVO: oldSaleContractRespVO,
        saleContractRespVO: paramsResult
      })
      // message.success('提交成功')
      message.notifyPushSuccess('成功生成销售变更', res, 'sale-change')
      close()
      emit('handleSuccess', 'success')
    } else {
      await ExportSaleContractChangeApi.updateChange({
        ...paramsResult,
        submitFlag: type
      })
      message.success('提交成功')
      close()
      emit('handleSuccess', 'success')
    }
  } finally {
    loading.value = false
  }
}

const screenHeight = ref(0)
onMounted(async () => {
  console.log('[ChangeForm] onMounted 开始, loading:', loading.value, 'parentDataReady:', parentDataReady.value)
  try {
    loading.value = true
    formRef.value.resetForm()
    // 获取屏幕高度
    screenHeight.value = window.innerHeight
    // 监听窗口大小变化，更新屏幕高度
    window.addEventListener('resize', () => {
      screenHeight.value = window.innerHeight
    })
    console.log('[ChangeForm] 开始获取API数据...')
    simpleUserList.value = await UserApi.getSimpleUserList()
    let res
    if (props.mode === 'edit') {
      res = await ExportSaleContractChangeApi.getChangeInfo({ id: queryId })
    } else {
      res = await ExportSaleContractApi.getExportSaleContract({ id: queryId })
      oldSaleContractRespVO = cloneDeep(res)
    }
    let custSimpleList = await PurchasePlanApi.getCustSimpleList({})
    if (res.custId) {
      var currentCust = custSimpleList.list.filter((item) => {
        return item.id == res.custId
      })
      if (currentCust?.length) {
        setCompanyPathListRef(currentCust[0])
        companyCustLinkRef.value = currentCust[0].custLink
      }
    }
    console.log('[ChangeForm] API数据获取完成, children数量:', res.children?.length, 'addSubItemList数量:', res.addSubItemList?.length, 'collectionPlanList数量:', res.collectionPlanList?.length)
    Object.assign(formData, getPageObj(res))
    console.log('[ChangeForm] formData已赋值，设置parentDataReady=true')
    // 通知子组件父组件数据已加载完成，可以开始触发loaded事件
    parentDataReady.value = true
    console.log('[ChangeForm] parentDataReady已设置为true, 当前childLoadedCount:', childLoadedCount.value)
  } finally {
    // loading由handleChildLoaded控制
    console.log('[ChangeForm] onMounted finally, loading:', loading.value, 'childLoadedCount:', childLoadedCount.value)
  }
})
if (props.mode != 'edit') {
  basicSchemas = basicSchemas.filter((item, index) => {
    return item.field != 'code'
  })
}

const getPageObj = (res) => {
  if (res.companyPath) {
    res.companyPathName = getCompanyPathNameFromObj(res.companyPath)
  }
  return {
    ...res,
    salesNickname: res.sales?.nickname ? res.sales.nickname : '',
    salesDeptName: res.sales?.deptName ? res.sales.deptName : '',
    estimatedTotalFreightFormat: res.estimatedTotalFreight.amount
      ? res.estimatedTotalFreight.amount.toFixed(2)
      : '0.00',
    commissionFormat: res.commission.amount ? res.commission.amount.toFixed(2) : '0.00',
    platformFeeFormat: res.platformFee.amount ? res.platformFee.amount.toFixed(2) : '0.00',
    sinosureFeeFormat: res.sinosureFee.amount ? res.sinosureFee.amount.toFixed(2) : '0.00',
    additionAmountFormat: res.additionAmount.amount ? res.additionAmount.amount.toFixed(2) : '0.00',
    deductionAmountFormat: res.deductionAmount.amount
      ? res.deductionAmount.amount.toFixed(2)
      : '0.00',
    totalGoodsValueFormat: res.totalGoodsValue.amount
      ? res.totalGoodsValue.amount.toFixed(2)
      : '0.00',
    totalPurchaseFormat: amountListFormat(res.totalPurchase),
    totalVatRefundFormat: res.totalVatRefund.amount ? res.totalVatRefund.amount.toFixed(2) : '0.00',
    orderGrossProfitFormat: res.orderGrossProfit.amount
      ? res.orderGrossProfit.amount.toFixed(2)
      : '0.00',
    estimatedPackingMaterialsFormat: res.estimatedPackingMaterials.amount
      ? res.estimatedPackingMaterials.amount.toFixed(2)
      : '0.00',
    totalVolumeFormat: res.totalVolume ? (res.totalVolume / 1000000).toFixed(6) : '0',
    totalGrossweightFormat: res.totalGrossweight.weight ? res.totalGrossweight.weight : 0,
    totalWeightFormat: res.totalWeight.weight ? res.totalWeight.weight : 0,
    grossProfitMarginFormat: res.grossProfitMargin ? res.grossProfitMargin.toFixed(6) : '0',
    totalQuantityFormat: res.totalQuantity ? res.totalQuantity.toFixed(0) : 0,
    bulkHandlingVolumeFormat: res.bulkHandlingVolume ? res.bulkHandlingVolume / 1000000 : 0
  }
}
onUnmounted(() => {
  window.removeEventListener('resize', () => {
    screenHeight.value = window.innerHeight
  })
})
</script>
<style>
eplus-form-items title {
  font-size: 16px !important;
}
</style>
