<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: ['dms:shipment:update'],
      handler: saveForm
    }"
  >
    <eplus-form-items
      title="基本信息"
      :formSchemas="basicSchemas"
    >
      <template #departurePortId>
        <eplus-input-search-select
          v-if="!loading"
          v-model="formData.departurePortId"
          :api="PortApi.getPortList"
          :params="{
            pageSize: 100,
            pageNo: 1,
            status: PortStatusStatusEnum.NORMAL.status
          }"
          keyword="nameEng"
          label="nameEng"
          value="id"
          class="!w-100%"
          placeholder="请选择"
          :clearable="false"
          @change-emit="(...$event) => getName($event, 3)"
          :defaultObj="{
            id: formData.departurePortId,
            nameEng: formData.departurePortName
          }"
        />
      </template>
      <template #destinationPortId>
        <eplus-input-search-select
          v-if="!loading"
          v-model="formData.destinationPortId"
          :api="PortApi.getPortList"
          :params="{
            pageSize: 100,
            pageNo: 1,
            status: PortStatusStatusEnum.NORMAL.status
          }"
          keyword="nameEng"
          label="nameEng"
          value="id"
          class="!w-100%"
          placeholder="请选择"
          :clearable="false"
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
    <el-tabs v-model="activeName">
      <el-tab-pane
        label="结汇单明细"
        name="1"
      >
        <ContractList
          ref="ContractListRef"
          :formData="formData"
          @customer-names-change="updateCustomerNames"
        />
      </el-tab-pane>
      <el-tab-pane
        label="加减项"
        name="3"
      >
        <Table
          :columns="tableColumns"
          headerAlign="center"
          align="center"
          :data="formData?.addSubItemList"
        />
      </el-tab-pane>
    </el-tabs>
  </eplus-form>
</template>
<script setup lang="tsx">
// import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import { useCountryStore } from '@/store/modules/countrylist'
import { PortStatusStatusEnum } from '@/utils/constants'
import * as PortApi from '@/api/system/port'
import UploadList from '@/components/UploadList/index.vue'
import ContractList from './ContractList.vue'
import { VolumeUnit } from '@/utils/config'
import { useUserStore } from '@/store/modules/user'
import { formatNum, formatTime } from '@/utils'
import * as SettlementFormApi from '@/api/dms/settlementForm/index'
import { formatDictColumn, formatMoneyColumn } from '@/utils/table'
import { cloneDeep } from 'lodash-es'

const permissions = useUserStore().getPermissions

const message = useMessage()
defineOptions({ name: 'SettlementFormMain' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const loading = ref(false)
const formData: EplusAuditable = reactive({})
provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}

// const { updateDialogActions, clearDialogActions } = props.id
//   ? (inject('dialogActions') as {
//       updateDialogActions: (...args: any[]) => void
//       clearDialogActions: () => void
//     })
//   : { updateDialogActions: () => {}, clearDialogActions: () => {} }
const countryListData = useCountryStore()

const getName = (e, type) => {
  let item = e[1].find((item) => item.id === e[0])
  if (type == 1) {
    formData.departureCountryName = item?.name
    formData.departureCountryArea = item.regionName
    formData.departurePortId = undefined
  } else if (type == 2) {
    formData.tradeCountryName = item?.name
    formData.tradeCountryArea = item.regionName
    formData.destinationPortId = undefined
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
    component: <el-input />
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
    field: 'code',
    label: '结汇单号',
    component: (
      <el-input
        placeholder="-"
        disabled
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
      required: true,
      message: '请选择实际开船日期'
    }
  },
  {
    field: 'inputDate',
    label: '制单日期',
    component: (
      <el-date-picker
        type="date"
        placeholder="-"
        disabled
      />
    )
  },
  {
    field: 'inputUserName',
    label: '录入人',
    component: (
      <el-input
        placeholder="-"
        disabled
      />
    )
  },
  {
    field: 'tradeCountryId',
    label: '贸易国别',
    component: (
      <eplus-input-select
        dataList={countryListData.countryList}
        label="name"
        value="id"
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
      message: '请选择目的口岸'
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
    field: 'invoicePackingList',
    label: '发票箱单',
    component: (
      <el-input
        placeholder="请输入发票箱单"
        maxlength="50"
      />
    )
  },
  {
    field: 'shipmentCode',
    label: '来源出运单号',
    component: (
      <el-input
        placeholder="-"
        disabled
      />
    )
  },
  {
    field: 'custName',
    label: '客户名称',
    component: (
      <el-input
        placeholder="-"
        disabled
      />
    )
  }
])
const shipSchema: EplusFormSchema[] = reactive([
  {
    field: 'forwarderCompanyName',
    label: '船代公司',
    readOnly: true
  },
  {
    field: 'shipNum',
    label: '航名/船次',
    readOnly: true
  },
  {
    field: 'billLadingNum',
    label: '提单号',
    readOnly: true
  },
  {
    field: 'estDepartureDate',
    label: '实际开船日期',
    readOnly: true,
    formatter: (val) => {
      return val ? formatTime(val, 'yyyy-MM-dd') : '-'
    }
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
])

const tableColumns = reactive([
  {
    type: 'index',
    label: '序号',
    width: '60',
    align: 'center'
  },
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
])

const UploadListRef = ref()
const getFileList = (list) => {
  formData.annex = list
}
const activeName = ref('1')
const activeTab = ref('1')
const emit = defineEmits(['handleSuccess'])
const formRef = ref()
const ShipmentCustRef = ref()
const ContractListRef = ref()
const updateCustomerNames = (custName: string) => {
  formData.custName = custName
}
const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params = cloneDeep(formData)

  if (ShipmentCustRef.value) {
    let result = await ShipmentCustRef.value?.checkData()
    if (result) {
      params.shipmentCustList = ShipmentCustRef.value.custForm.list
    } else {
      params.shipmentCustList = []
      return false
    }
  }

  if (ContractListRef.value) {
    let result = await ContractListRef.value?.checkData()
    if (result) {
      params.children = result
    } else {
      params.children = []
      return false
    }
  }

  return params
}
const saveForm = async () => {
  try {
    let paramsResult: any = await getParams()
    if (!paramsResult) return false
    await SettlementFormApi.updateSettlement(paramsResult)
    message.success('保存成功!')
    close()
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  if (props.mode === 'edit') {
    loading.value = true
    let res = await SettlementFormApi.getSettlementFormInfo({ id: props.id })
    Object.assign(formData, res)
    loading.value = false
  }
})
</script>
<style>
.el-scrollbar__bar {
  z-index: 2 !important;
}
</style>
