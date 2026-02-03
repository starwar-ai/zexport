<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    scrollFlag
    :mainRatio="3"
  >
    <template #pageTop>
      <eplus-form-items
        title="基本信息"
        :formSchemas="basicSchemas"
      >
        <template #departurePortId>
          <eplus-port-select
            v-if="editFlag"
            v-model="formData.departurePortId"
            @change-emit="(...e) => getName(e, 3)"
            :defaultObj="{
              id: formData.departurePortId,
              nameEng: formData.departurePortName
            }"
          />
        </template>
        <template #destinationPortId>
          <eplus-port-select
            v-if="editFlag"
            v-model="formData.destinationPortId"
            @change-emit="(...e) => getName(e, 4)"
            :defaultObj="{
              id: formData.destinationPortId,
              nameEng: formData.destinationPortName
            }"
          />
        </template>
        <template #annex>
          <UploadList
            ref="UploadListRef"
            :fileList="formData.annex"
            @success="getFileList"
          />
        </template>
      </eplus-form-items>
      <el-tabs v-model="activeTab">
        <el-tab-pane
          label="出货信息"
          name="1"
        >
          <eplus-form-items
            title=""
            :formSchemas="shipmentSchema"
          >
            <!-- <template #shipmentCust>
              <ShipmentCust
                v-if="formData?.shipmentCustList?.length > 0"
                :formData="formData"
                ref="ShipmentCustRef"
              />
            </template> -->
          </eplus-form-items>
        </el-tab-pane>
        <el-tab-pane
          label="尺柜信息"
          name="2"
        >
          <eplus-form-items
            title=""
            :formSchemas="cabinetSchema"
          />
        </el-tab-pane>
        <el-tab-pane
          label="船信息"
          name="3"
        >
          <eplus-form-items
            title=""
            :formSchemas="shipSchema"
          />
        </el-tab-pane>
        <el-tab-pane
          label="合计信息"
          name="4"
        >
          <eplus-form-items
            title=""
            :formSchemas="totalSchema"
          >
            <template #totalGoodsValue>
              <EplusMoneyLabel :val="formData.totalGoodsValue || []" />
            </template>
            <template #totalPurchase>
              <EplusMoneyLabel :val="formData.totalPurchase || []" />
            </template>
            <template #totalDeclaration>
              <EplusMoneyLabel :val="formData.totalDeclaration || []" />
            </template>
          </eplus-form-items>
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

        <!-- 单证费用提交前添加的数据，会复制到其他账套，导致申请船代费用数据重复
      2024.12.21  讨论结论，不允许在编辑过程中输入单证费用，避免数据复制
      在审核之后再输入的数据不会重复 
      -->
        <el-tab-pane
          label="单证费用"
          name="4"
          v-if="props?.type === 'change'"
        />
      </el-tabs>
    </template>
    <template #pageBottom>
      <el-scrollbar always>
        <div v-show="activeName === '1'">
          <ContractDetail
            v-if="formData?.children?.length > 0"
            :formData="formData"
            @get-cabinet="getContractCabinet"
            ref="ContractDetailRef"
            :changeFlag="props?.type === 'change' ? true : false"
            :maxHeight="tableMaxHeight - 54"
          />
        </div>
        <div v-show="activeName === '2'">
          <TemporaryPro
            v-if="editFlag"
            :formData="formData"
            ref="TemporaryProRef"
            @get-cabinet="getTemporaryProCabinet"
          />
        </div>
        <div v-show="activeName === '3'">
          <AddSubItem
            :formData="formData"
            ref="AddSubItemRef"
          />
        </div>
        <div v-show="activeName === '4'">
          <ForwarderFee
            channel="update"
            :formData="formData"
            ref="ForwarderFeeRef"
          />
        </div>
      </el-scrollbar>
    </template>
  </eplus-form>
</template>
<script setup lang="tsx">
// import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import { useCountryStore } from '@/store/modules/countrylist'
import { ShipmentDetailStatusEnum, TradeOrderStatusEnum } from '@/utils/constants'
import * as ShipmentApi from '@/api/dms/shipment/index'
import UploadList from '@/components/UploadList/index.vue'
import ContractDetail from './components/ContractDetail.vue'
import AddSubItem from './components/AddSubItem.vue'
import ForwarderFee from './components/ForwarderFee.vue'
import TemporaryPro from './components/TemporaryPro.vue'
import { VolumeUnit } from '@/utils/config'
import { cloneDeep } from 'lodash-es'
import { useUserStore } from '@/store/modules/user'
import { getVenderSimpleList } from '@/api/common'
import { formatNum, formatterPrice } from '@/utils'
import * as ConfigApi from '@/api/infra/config'

const permissions = useUserStore().getPermissions

const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'ShippingForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
  params?: any
  type: string
}>()
const loading = ref(false)
let oldPurchaseContractInfoRespVO = reactive({})
const formData: EplusAuditable = reactive({
  auditStatus: 0,
  totalGrossweight: { weight: 0, unit: 'kg' },
  totalWeight: { weight: 0, unit: 'kg' }
})
provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }
const disabledDate = (time: Date) => {
  return time.getTime() < Date.now()
}
const countryListData = useCountryStore()

const getName = (e, type) => {
  let item = e[1].find((item) => item.id === e[0])
  if (type == 1) {
    formData.departureCountryName = item?.name
    formData.departureCountryArea = item.regionName
  } else if (type == 2) {
    formData.tradeCountryName = item?.name
    formData.tradeCountryArea = item.regionName
  } else if (type == 3) {
    formData.departurePortName = item?.nameEng
  } else if (type == 4) {
    formData.destinationPortName = item?.nameEng
  } else if (type == 5) {
    formData.forwarderCompanyName = item?.name
  }
}
const basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'invoiceCode',
    label: '发票号',
    component: <el-input />,
    rules: {
      required: true,
      message: '请输入发票号'
    }
  },
  {
    field: 'invoiceDate',
    label: '发票日期',
    component: (
      <el-date-picker
        type="date"
        placeholder="请选择"
        value-format="x"
        class="!w-100%"
      />
    ),
    rules: {
      required: true,
      message: '请输入发票日期'
    }
  },
  {
    field: 'foreignTradeCompanyName',
    label: '报关公司',
    component: (
      <el-input
        placeholder="-"
        disabled
      />
    )
  },
  {
    field: 'shipmentPlanCode',
    label: '出运计划单号',
    component: (
      <el-input
        placeholder="-"
        disabled
      />
    )
  },
  {
    field: 'code',
    label: '出运单号',
    component: (
      <el-input
        placeholder="-"
        disabled
      />
    )
  },

  {
    field: 'custDeliveryDate',
    label: '客户交期',
    component: (
      <el-date-picker
        type="date"
        placeholder="请选择"
        value-format="x"
        disabled-date={disabledDate}
        class="!w-100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择客户交期'
    }
  },
  {
    field: 'custName',
    label: '客户名称',
    xl: 8,
    lg: 12,
    component: (
      <el-input
        placeholder="-"
        disabled
      />
    )
  },
  {
    field: 'saleContractCode',
    label: '销售合同号',
    xl: 8,
    lg: 12,
    component: (
      <el-input
        placeholder="-"
        disabled
      />
    )
  },
  {
    field: 'custPo',
    label: '客户PO号',
    xl: 8,
    lg: 12,
    component: <el-input placeholder="请输入客户PO号" />
  },
  {
    field: 'tradeCountryId',
    label: '贸易国别',
    component: (
      <eplus-input-select
        dataList={countryListData.countryList}
        label="name"
        value="id"
        lowerCaseFlag
        onChangeEmit={(...e) => getName(e, 2)}
        class="!w-100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择贸易国别'
    }
  },
  {
    field: 'destinationPortId',
    label: '目的口岸',
    rules: {
      required: true,
      message: '请输入目的口岸'
    }
  },
  {
    field: 'departureCountryId',
    label: '出运国',
    component: (
      <eplus-input-select
        dataList={countryListData.countryList}
        label="name"
        value="id"
        lowerCaseFlag
        onChangeEmit={(...e) => getName(e, 1)}
        class="!w-100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择出运国'
    }
  },
  {
    field: 'departurePortId',
    label: '出运口岸',
    rules: {
      required: true,
      message: '请选择出运口岸'
    }
  },
  {
    field: 'transportType',
    label: '运输方式',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.TRANSPORT_TYPE}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择运输方式'
    }
  },
  {
    field: 'documenterId',
    label: '单证人员',
    component: (
      <eplus-user-select
        class="!w-100%"
        onChange={(...e) => {
          formData.documenter = {
            ...e[0],
            userId: e[0]?.id
          }
        }}
      ></eplus-user-select>
    ),
    rules: {
      required: true,
      message: '请选择单证人员'
    }
  },
  {
    field: 'inboundDate',
    label: '实际拉柜/进仓日期',
    labelWidth: '150px',
    component: (
      <el-date-picker
        type="date"
        placeholder="请选择"
        value-format="x"
      />
    )
  },
  {
    field: 'estDepartureTime',
    label: '实际开船日期',
    component: (
      <el-date-picker
        type="date"
        placeholder="请选择"
        value-format="x"
        class="!w-100%"
      />
    ),
    rules: {
      required: TradeOrderStatusEnum,
      message: '请选择实际开船日期'
    }
  },
  {
    field: 'remark',
    label: '备注',
    component: <el-input type="textarea" />,
    span: 24
  },
  {
    field: 'annex',
    label: '附件',
    span: 24
  }
])
const shipSchema: EplusFormSchema[] = reactive([
  {
    field: 'forwarderCompanyId',
    label: '船代公司',
    component: (
      <eplus-input-search-select
        api={getVenderSimpleList}
        params={{ pageSize: 100, pageNo: 1, administrationVenderType: 2 }}
        keyword="name"
        label="name"
        value="id"
        class="!w-100%"
        placeholder="请选择"
        onChangeEmit={(...e) => getName(e, 5)}
        defaultObj={{
          name: formData.forwarderCompanyName || undefined,
          id: formData.forwarderCompanyId || undefined
        }}
      />
    ),
    rules: {
      required: false,
      message: '请选择船代公司'
    }
  },
  {
    field: 'shipNum',
    label: '航名/船次',
    component: <el-input />,
    rules: {
      required: false,
      message: '请输入航名/船次'
    }
  },
  {
    field: 'billLadingNum',
    label: '提单号/进仓编号',
    component: <el-input />
  }

  // {
  //   field: 'estDepartureTime',
  //   label: '预计结港时间',
  //   component: (
  //     <el-date-picker
  //       type="datetime"
  //       placeholder="请选择"
  //       value-format="x"
  //       disabled-date={disabledDate}
  //       class="!w-100%"
  //     />
  //   ),
  //   rules: {
  //     required: false,
  //     message: '请选择预计结港时间'
  //   }
  // },
  // {
  //   field: 'estClearanceTime',
  //   label: '预计结关时间',
  //   component: (
  //     <el-date-picker
  //       type="datetime"
  //       placeholder="请选择"
  //       value-format="x"
  //       disabled-date={disabledDate}
  //       class="!w-100%"
  //     />
  //   ),
  //   rules: {
  //     required: false,
  //     message: '请选择预计结关时间'
  //   }
  // },
  // {
  //   field: 'estClosingTime',
  //   label: '预计结单时间',
  //   component: (
  //     <el-date-picker
  //       type="datetime"
  //       placeholder="请选择"
  //       value-format="x"
  //       disabled-date={disabledDate}
  //       class="!w-100%"
  //     />
  //   ),
  //   rules: {
  //     required: false,
  //     message: '请选择预计结单时间'
  //   }
  // }
])

const ContractData = ref({})
// const TemporaryProData = ref({})
const getContractCabinet = (obj) => {
  ContractData.value = obj
  getCabinet(ContractData.value)
}
const getTemporaryProCabinet = (obj) => {
  // TemporaryProData.value = obj
  // getCabinet(ContractData.value, TemporaryProData.value)
}
const getCabinet = (obj = {}) => {
  formData.totalGoodsValue = obj.listTotalGoodsValue || []
  formData.totalPurchase = obj.listTotalPurchase || []
  formData.totalQuantity = obj.listTotalQuantity
  // formData.totalBoxes = obj.listTotalBoxes + obj2?.listTotalBoxes
  // formData.totalGrossweight.weight = obj.listTotalGrossweight + obj2?.listTotalGrossweight
  // formData.totalWeight.weight = obj.listTotalWeight + obj2?.listTotalWeight
  // formData.totalVolume = obj.listTotalVolume + obj2?.listTotalVolume
  formData.totalBoxes = obj.listTotalBoxes
  formData.totalGrossweight.weight = formatterPrice(obj.listTotalGrossweight, 2)
  formData.totalWeight.weight = formatterPrice(obj.listTotalWeight, 2)
  formData.totalVolume = obj.listTotalVolume
  formData.totalDeclaration = obj.listTotalDeclaration || []
  formData.totalTaxRefundPrice = obj.listTotalTaxRefundPrice
  formData.commissionAmount = obj.listCommissionAmount
  formData.insuranceFee = obj.listInsuranceFee

  let totalVolumeFormat = Number(formData.totalVolume) / 1000000
  //容器计算
  let remainNum = Number(totalVolumeFormat)
  if (Number(remainNum) > 68) {
    formData.fortyFootContainerNum = (Number(totalVolumeFormat) / 68) | 0
    remainNum = Number(remainNum) - 68 * Number(formData.fortyFootContainerNum)
  }
  if (Number(remainNum) > 59 && Number(remainNum) <= 68) {
    formData.fortyFootContainerNum += 1
  } else {
    if (Number(remainNum) > 49 && Number(remainNum) <= 59) {
      formData.fortyFootCabinetNum = 1
    } else {
      if (Number(remainNum) > 29 && Number(remainNum) <= 49) {
        formData.twentyFootCabinetNum = 1
        remainNum = Number(remainNum) - 29
      }
      if (Number(remainNum) >= 20 && Number(remainNum) <= 29) {
        formData.twentyFootCabinetNum += 1
      } else {
        let bulkHandlingMoney = bulkHandlingStartFeeConfigValue.value
        if (remainNum > 1) {
          bulkHandlingMoney =
            Number(bulkHandlingMoney) +
            Number(bulkHandlingFeeConfigValue.value) * (Math.ceil(remainNum) - 1)
        }
        if (Number(bulkHandlingMoney - twentyFootCabinetFeeConfigValue.value) > 0) {
          formData.twentyFootCabinetNum += 1
          formData.bulkHandlingVolume = 0
          bulkHandlingMoney =
            Number(bulkHandlingMoney) - Number(twentyFootCabinetFeeConfigValue.value)
        } else {
          formData.bulkHandlingVolume = formatterPrice(remainNum, 6)
        }
      }
    }
  }
}

const cabinetSchema = reactive([
  {
    field: 'twentyFootCabinetNum',
    label: '20尺柜',
    component: <el-input />
  },
  {
    field: 'fortyFootCabinetNum',
    label: '40尺柜',
    component: <el-input />
  },
  {
    field: 'fortyFootContainerNum',
    label: '40尺高柜',
    component: <el-input />
  },
  {
    field: 'bulkHandlingVolume',
    label: `散货${VolumeUnit}`,
    component: <el-input />
  }
])
const shipmentSchema = reactive([
  // {
  //   field: 'shipmentCust',
  //   label: '',
  //   span: 24,
  //   labelWidth: '0px'
  // }
  {
    field: 'receivePerson',
    label: '收货人',
    span: 8,
    component: (
      <el-input
        type="textarea"
        rows={10}
      />
    ),
    rules: {
      required: true,
      message: '请输入收货人'
    }
  },
  {
    field: 'notifyPerson',
    label: '通知人',
    span: 8,
    component: (
      <el-input
        type="textarea"
        rows={10}
      />
    ),
    rules: {
      required: true,
      message: '请输入通知人'
    }
  },
  {
    field: 'frontShippingMark',
    label: '正面唛头',
    span: 8,
    component: (
      <el-input
        type="textarea"
        rows={10}
      />
    ),
    rules: {
      required: true,
      message: '请输入正面唛头'
    }
  }
])

const totalSchema = reactive([
  {
    field: 'totalGoodsValue',
    label: '货值合计'
  },
  {
    field: 'totalQuantity',
    label: '数量合计',
    readOnly: true,
    formatter: (val) => {
      return val ? formatNum(val) : '0'
    }
  },

  {
    field: 'totalBoxes',
    label: '箱数合计',
    readOnly: true,
    formatter: (val) => {
      return val ? formatNum(val) : '0'
    }
  },
  {
    field: 'totalGrossweight',
    label: '毛重合计',
    readOnly: true,
    formatter: (val) => {
      return val?.weight ? `${formatNum(val.weight)} ${val.unit}` : '-'
    }
  },
  {
    field: 'totalWeight',
    label: '净重合计',
    readOnly: true,
    formatter: (val) => {
      return val?.weight ? `${formatNum(val.weight)} ${val.unit}` : '-'
    }
  },
  {
    field: 'totalVolume',
    label: '体积合计',
    readOnly: true,
    formatter: (val) => {
      return val ? `${formatNum(val / 1000000)} ${VolumeUnit}` : '-'
    }
  },
  {
    field: 'totalDeclaration',
    label: '报关合计'
  },
  {
    field: 'totalPurchase',
    label: '采购合计'
  }
  // {
  //   field: 'totalTaxRefundPrice',
  //   label: '退税总额',
  //   readOnly: true,
  //   formatter: (val) => {
  //     return val?.amount ? `${Number(val.amount).toFixed(2)} ${val.currency}` : '-'
  //   }
  // },
  // {
  //   field: 'commissionAmount',
  //   label: '佣金金额',
  //   readOnly: true,
  //   formatter: (val) => {
  //     return val?.amount ? `${Number(val.amount).toFixed(2)} ${val.currency}` : '-'
  //   }
  // },
  // {
  //   field: 'receivedNum',
  //   label: '已收货值',
  //   readOnly: true
  // },
  // {
  //   field: 'unreceivedNum',
  //   label: '未收货值',
  //   readOnly: true
  // },
  // {
  //   field: 'insuranceFee',
  //   label: '保险费用',
  //   readOnly: true,
  //   formatter: (val) => {
  //     return val?.amount ? `${Number(val.amount).toFixed(2)} ${val.currency}` : '-'
  //   }
  // }
])

const UploadListRef = ref()
const getFileList = (list) => {
  formData.annex = list
}
const activeName = ref('1')
const activeTab = ref('1')
const emit = defineEmits(['handleSuccess'])
const formRef = ref()
const ContractDetailRef = ref()
const AddSubItemRef = ref()
const ForwarderFeeRef = ref()
const ShipmentCustRef = ref()
const TemporaryProRef = ref()
//20尺柜
const twentyFootCabinetFeeConfigValue = ref(0)
//散货
const bulkHandlingFeeConfigValue = ref(0)
//散货起始价格
const bulkHandlingStartFeeConfigValue = ref(0)
const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params = cloneDeep(formData)
  params.twentyFootCabinetNum = Number(params.twentyFootCabinetNum)
  params.fortyFootCabinetNum = Number(params.fortyFootCabinetNum)
  params.fortyFootContainerNum = Number(params.fortyFootContainerNum)
  params.bulkHandlingVolume = Number(params.bulkHandlingVolume)
  if (ContractDetailRef.value) {
    let result = await ContractDetailRef.value?.checkData()
    if (result) {
      params.children = result
    } else {
      params.children = []
      return false
    }
  }
  if (AddSubItemRef.value) {
    let result = await AddSubItemRef.value?.checkData()
    if (result) {
      params.addSubItemList = result
    } else {
      params.addSubItemList = []
      return false
    }
  }
  if (ForwarderFeeRef.value) {
    let result = await ForwarderFeeRef.value?.checkData()
    if (result) {
      params.forwarderFeeList = result
    } else {
      params.forwarderFeeList = []
      return false
    }
  }
  // if (ShipmentCustRef.value) {
  //   let result = await ShipmentCustRef.value?.checkData()
  //   if (result) {
  //     params.shipmentCustList = ShipmentCustRef.value.custForm.list
  //   } else {
  //     params.shipmentCustList = []
  //     return false
  //   }
  // }
  if (TemporaryProRef.value) {
    let result = await TemporaryProRef.value?.checkData()
    if (result) {
      params.temporarySkuList = result
    } else {
      params.temporarySkuList = []
      return false
    }
  }

  return { ...params, bulkHandlingVolume: params.bulkHandlingVolume * 1000000 }
}
const saveForm = async (status) => {
  try {
    loading.value = true
    let paramsResult: any = await getParams()
    if (!paramsResult) return false
    var res = {}
    if (props?.type === 'change') {
      paramsResult = { shipment: paramsResult, old_shipment: oldPurchaseContractInfoRespVO }
      if (status === 6) {
        res = await ShipmentApi.changeShipment(paramsResult)
        message.notifyPushSuccess('出运明细变更成功', res, 'shipment-change')
      } else {
        res = await ShipmentApi.changeSalesman(paramsResult)
        message.notifyPushSuccess('出运明细变更成功', res, 'shipment-change')
      }
      if (paramsResult.shipment.confirmFlag === 0) {
        await ShipmentApi.changeConfirm(paramsResult.shipment.id)
      }
    } else {
      await ShipmentApi.updateShipment({ ...paramsResult, status: status })
    }
    message.success(t('common.updateSuccess'))
    close()
    emit('handleSuccess')
  } finally {
    loading.value = false
  }
}

const editFlag = ref(false)
const tableMaxHeight = ref(0)
onMounted(async () => {
  const config = await ConfigApi.getConfigSimplelist({})
  if (config?.length) {
    let twentyFootCabinetFeeConfig = config.filter(
      (item) => item.category == 'twenty_foot_cabinet_fee'
    )
    if (twentyFootCabinetFeeConfig?.length) {
      twentyFootCabinetFeeConfigValue.value = twentyFootCabinetFeeConfig[0].value
    }
    let bulkHandlingFeeConfig = config.filter((item) => item.category == 'bulk_handling_fee')
    if (bulkHandlingFeeConfig?.length) {
      bulkHandlingFeeConfigValue.value = bulkHandlingFeeConfig[0].value
    }
    let bulkHandlingStartFeeConfig = config.filter(
      (item) => item.category == 'bulk_handling_start_fee'
    )
    if (bulkHandlingStartFeeConfig?.length) {
      bulkHandlingStartFeeConfigValue.value = bulkHandlingStartFeeConfig[0].value
    }
  }
  if (props.mode === 'edit') {
    let res = await ShipmentApi.getShipmentInfo({ id: props.id })
    oldPurchaseContractInfoRespVO = props?.type === 'change' ? cloneDeep(res) : undefined
    Object.assign(formData, {
      ...res,
      bulkHandlingVolume: res.bulkHandlingVolume / 1000000,
      documenterId: res.documenter?.userId ? res.documenter?.userId : useUserStore().user.id,
      documenter: res.documenter?.userId
        ? res.documenter
        : { ...useUserStore().user, userId: useUserStore().user.id },
      invoiceDate: res.invoiceDate || res.inputDate
    })
    editFlag.value = true
    if (formData.status === ShipmentDetailStatusEnum.WAITING_PROCESS.status) {
      updateDialogActions(
        <el-button
          //type="primary"
          onClick={() => {
            saveForm(1)
          }}
          key="save"
        >
          保存
        </el-button>,
        <el-button
          type="primary"
          onClick={() => {
            saveForm(2)
          }}
          key="submit"
        >
          提交
        </el-button>
      )
    } else if (
      [
        ShipmentDetailStatusEnum.SHIPPED.status,
        ShipmentDetailStatusEnum.WAITING_SHIPMENT.status
      ].includes(formData.status) &&
      formData.changeStatus !== 2
    ) {
      if (permissions.includes('dms:export-sale-contract-change:update')) {
        //单证人员无需审批
        updateDialogActions(
          <el-button
            type="primary"
            onClick={() => {
              saveForm(6)
            }}
            key="submit"
          >
            提交
          </el-button>
        )
      } else if (permissions.includes('dms:export-sale-contract-change-business:update')) {
        //业务员需要审批
        updateDialogActions(
          <el-button
            type="primary"
            onClick={() => {
              saveForm(7)
            }}
            key="submit"
          >
            提交
          </el-button>
        )
      }
    }
    TemporaryProRef.value?.init()
  } else if (props?.type === 'change') {
    // formRef.value.resetForm()
    // let res = await ShipmentApi.getSaleContractList(props.params)
    // Object.assign(formData, {
    //   ...res,
    //   twentyFootCabinetNum: 0,
    //   fortyFootCabinetNum: 0,
    //   fortyFootContainerNum: 0,
    //   bulkHandlingVolume: 0
    // })
  }
  setTimeout(() => {
    tableMaxHeight.value = formRef.value.bottomHeight
  }, 1000)
})
</script>
<style>
.el-scrollbar__bar {
  z-index: 2 !important;
}
</style>
