<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: ['dms:shipment-plan:update', 'sms:export-sale-contract:to-shipment-plan'],
      handler: saveForm,
      textContent: mode == 'create' ? '下推出运计划' : '保存'
    }"
    :submitAction="submitAction"
    scrollFlag
  >
    <template #pageTop>
      <eplus-form-items
        title="基本信息"
        :formSchemas="basicSchemas"
      >
        <template #departurePortId>
          <eplus-port-select
            v-if="!loading"
            v-model="formData.departurePortId"
            @change-emit="(...$event) => getName($event, 3)"
            :defaultObj="{
              id: formData.departurePortId,
              nameEng: formData.departurePortName
            }"
          />
        </template>
        <template #destinationPortId>
          <eplus-port-select
            v-if="!loading"
            v-model="formData.destinationPortId"
            @change-emit="(...$event) => getName($event, 4)"
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

      <el-tabs
        v-model="activeTab"
        class="demo-tabs"
      >
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
          label="合计信息"
          name="3"
        >
          <eplus-form-items
            title=""
            :formSchemas="totalSchema"
          >
            <template #totalGoodsValue>
              <EplusMoneyLabel :val="formData.totalGoodsValue || []" />
            </template>
            <template #purchaseTotalAmount>
              <EplusMoneyLabel :val="formData.purchaseTotalAmount || []" />
            </template>
          </eplus-form-items>
        </el-tab-pane>
      </el-tabs>
    </template>
    <template #pageBottomTabs>
      <el-tabs
        v-model="activeName"
        class="demo-tabs"
      >
        <el-tab-pane
          label="销售合同明细"
          name="1"
        />

        <el-tab-pane
          label="加减项"
          name="2"
        />
      </el-tabs>
    </template>
    <template #pageBottom>
      <el-scrollbar always>
        <div v-show="activeName === '1'">
          <ContractDetail
            v-if="formData?.children?.length > 0"
            :formData="formData"
            @get-cabinet="getCabinet"
            ref="ContractDetailRef"
            :maxHeight="tableMaxHeight - 54"
          />
        </div>
        <div v-show="activeName === '2'">
          <AddSubItem
            v-if="formData?.children?.length > 0"
            :formData="formData"
            ref="AddSubItemRef"
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
import * as ShippingPlanApi from '@/api/dms/shippingPlan/index'
import UploadList from '@/components/UploadList/index.vue'
import ContractDetail from './components/ContractDetail.vue'
import AddSubItem from './components/AddSubItem.vue'
import { VolumeUnit } from '@/utils/config'
import { cloneDeep } from 'lodash-es'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import * as ConfigApi from '@/api/infra/config'
import { formatNum, formatterPrice, formatWeight } from '@/utils'
//20尺柜
const twentyFootCabinetFeeConfigValue = ref(0)
//散货
const bulkHandlingFeeConfigValue = ref(0)
//散货起始价格
const bulkHandlingStartFeeConfigValue = ref(0)

const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'ShippingPlanForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
  params?: any
}>()
const loading = ref(false)

const formData: EplusAuditable = reactive({
  auditStatus: 0
})
provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}
const disabledDate = (time: Date) => {
  return time.getTime() < Date.now()
}
const countryListData = useCountryStore()

const getName = (e, type) => {
  console.log(e)
  let item = e[1].find((item) => item.id === e[0])
  if (!item?.name) {
    return false
  }
  if (type == 1) {
    formData.departureCountryName = item.name
    formData.departureCountryArea = item.regionName
  } else if (type == 2) {
    formData.tradeCountryName = item.name
    formData.tradeCountryArea = item.regionName
  } else if (type == 3) {
    formData.departurePortName = item.nameEng
  } else if (type == 4) {
    formData.destinationPortName = item.nameEng
  }
}
const basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'code',
    label: '计划单号',
    component: (
      <el-input
        placeholder="-"
        disabled
      />
    )
  },
  {
    field: 'foreignTradeCompanyName',
    label: '归属公司',
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
    field: 'tradeCountryId',
    label: '贸易国别',
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
        onChangeEmit={(...$event) => getName($event, 1)}
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
    field: 'estDeliveryDate',
    label: '预计交货日期',
    component: (
      <el-date-picker
        type="date"
        placeholder="请选择"
        value-format="x"
        class="!w-100%"
      />
    )
  },

  {
    field: 'settlementTermType',
    label: '价格条款',
    component: <el-input disabled />
  },
  {
    field: 'managerName',
    label: '业务员',
    component: <el-input disabled />
  },
  {
    field: 'saleContractCode',
    label: '销售合同号',
    component: <el-input disabled />
  },
  {
    field: 'custName',
    label: '客户名称',
    component: <el-input disabled />
  },
  {
    field: 'custPo',
    label: '客户PO',
    component: <el-input disabled />
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

const formatWeightNum = (obj) => {
  if (obj.weight) {
    obj.weight = formatterPrice(obj.weight, 2)
  }
  return obj
}
const getCabinet = (obj) => {
  formData.totalGoodsValue = obj.listTotalGoodsValue || []
  formData.purchaseTotalAmount = obj.listPurchaseTotalAmount || []
  formData.totalQuantity = obj.listTotalQuantity
  formData.totalBoxes = obj.listTotalBoxes
  formData.totalGrossweight = formatWeightNum(obj.listTotalGrossweight)
  formData.totalWeight = formatWeightNum(obj.listTotalWeight)
  formData.totalVolume = obj.listTotalVolume

  let totalVolumeFormat = Number(obj.listTotalVolume) / 1000000
  //容器计算
  let remainNum = Number(totalVolumeFormat)
  if (Number(remainNum) > 68) {
    formData.fortyFootContainerNum = (Number(totalVolumeFormat) / 68) | 0
    remainNum = Number(remainNum) - 68 * Number(formData.fortyFootContainerNum)
  }
  if (Number(remainNum) >= 59 && Number(remainNum) <= 68) {
    formData.fortyFootContainerNum += 1
  } else {
    if (Number(remainNum) >= 50 && Number(remainNum) <= 59) {
      formData.fortyFootCabinetNum = 1
    } else {
      if (Number(remainNum) >= 30 && Number(remainNum) <= 49) {
        formData.twentyFootCabinetNum = 1
        remainNum = Number(remainNum) - 29
      }
      if (Number(remainNum) >= 20 && Number(remainNum) <= 29) {
        formData.twentyFootCabinetNum = 1
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
          formData.bulkHandlingVolume = remainNum
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
    field: 'purchaseTotalAmount',
    label: '采购合计'
  },
  {
    field: 'totalQuantity',
    label: '数量合计',
    readOnly: true,
    formatter: (val) => {
      return formatNum(val)
    }
  },
  {
    field: 'totalBoxes',
    label: '箱数合计',
    readOnly: true,
    formatter: (val) => {
      return formatNum(val)
    }
  },
  {
    field: 'totalGrossweight',
    label: '毛重合计',
    readOnly: true,
    formatter: (val) => {
      return formatWeight(val)
    }
  },
  {
    field: 'totalWeight',
    label: '净重合计',
    readOnly: true,
    formatter: (val) => {
      return formatWeight(val)
    }
  },
  {
    field: 'totalVolume',
    label: '体积合计',
    readOnly: true,
    formatter: (val) => {
      return val ? `${formatNum(val / 1000000, 6)} ${VolumeUnit}` : '-'
    }
  }
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
const ShipmentCustRef = ref()
const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params = cloneDeep(formData)

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
  // if (ShipmentCustRef.value) {
  //   let result = await ShipmentCustRef.value?.checkData()
  //   if (result) {
  //     params.shipmentCustList = ShipmentCustRef.value.custForm.list
  //   } else {
  //     message.warning('出货信息不完整，请完善出货信息！')
  //     params.shipmentCustList = []
  //     return false
  //   }
  // }
  return { ...params, bulkHandlingVolume: params.bulkHandlingVolume * 1000000 }
}
const saveForm = async () => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    if (props.mode == 'create') {
      paramsResult.saleItemIdStr = props.params.childrenIdList
      const res = await ShippingPlanApi.createShipmentPlan(paramsResult)
      message.notifyPushSuccess('出运计划创建成功', res, 'shipment-plan')
      close()
      emit('handleSuccess')
    } else {
      await ShippingPlanApi.updateShipmentPlan(paramsResult)
      message.success(t('common.updateSuccess'))
      close()
      emit('handleSuccess')
    }
  } finally {
    loading.value = false
  }
}
const submitForm = async () => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    await ShippingPlanApi.updateShipmentPlan({ ...paramsResult, submitFlag: 1 })
    message.success('提交成功')
    close()
  } finally {
    loading.value = false
  }
}
const tableMaxHeight = ref(0)
const submitAction = computed(() => {
  if (props.mode == 'create') {
    return undefined
  } else {
    return {
      permi: ['dms:shipment-plan:update'],
      handler: submitForm
    }
  }
})
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
    loading.value = true
    let res = await ShippingPlanApi.getShipmentPlanUpdateInfo({ id: props.id })
    res.bulkHandlingVolume = res.bulkHandlingVolume / 1000000
    Object.assign(formData, res)
    loading.value = false
  } else {
    formRef.value.resetForm()
    let res = await ShippingPlanApi.getSaleContractList(props.params)
    Object.assign(formData, {
      ...res,
      twentyFootCabinetNum: 0,
      fortyFootCabinetNum: 0,
      fortyFootContainerNum: 0,
      bulkHandlingVolume: 0
    })
  }
  setTimeout(() => {
    tableMaxHeight.value = formRef.value.bottomHeight
  }, 1000)
})
</script>
