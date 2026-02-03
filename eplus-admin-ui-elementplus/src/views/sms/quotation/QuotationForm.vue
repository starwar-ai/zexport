<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'sms:quotation:create',
      handler: () => submitForm()
    }"
    :submitAction="{
      permi: 'sms:quotation:submit',
      handler: () => submitForm('submit')
    }"
    :closeAction="{
      handler: close
    }"
  >
    <eplus-form-items
      title="基本信息"
      :formSchemas="basicSchemas"
    >
      <template #currency>
        <eplus-input-select
          v-if="isValidArray(currencyList)"
          v-model="formData.currency"
          :dataList="currencyList"
          label="name"
          value="value"
          class="!w-100%"
        />
        <eplus-dict-select
          v-else
          v-model="formData.currency"
          :dictType="DICT_TYPE.CURRENCY_TYPE"
          class="!w-100%"
          :clearable="false"
        />
      </template>
      <template #custPocId>
        <el-select
          v-model="formData.custPocId"
          clearable
          filterable
          style="width: 100%"
          @change="changeCustPoc"
        >
          <el-option
            v-for="item in custPocListRef"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </template>
      <template #departurePortName>
        <eplus-input-search-select
          v-if="formData.countryId"
          :key="formData.countryId"
          v-model="formData.departurePortName"
          :api="PortApi.getPortList"
          :params="{
            pageSize: 100,
            pageNo: 1,
            status: PortStatusStatusEnum.NORMAL.status,
            countryId: formData.countryId
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
      <template #validPeriod>
        <el-date-picker
          v-model="formData.validPeriod"
          clearable
          valueFormat="x"
          type="date"
          :shortcuts="shortcuts"
          style="width: 100%"
        />
      </template>
    </eplus-form-items>
    <el-tabs
      v-model="activeName"
      class="demo-tabs mb20px"
    >
      <el-tab-pane
        label="产品报价表"
        name="first"
      >
        <eplus-form-items
          title=""
          :formSchemas="collectionSchemas"
        >
          <template #children>
            <PurchaseProducts
              v-if="childrenFlag"
              :formData="formData"
              ref="childrenRef"
              @open="openProductInfo"
            />
          </template>
        </eplus-form-items>
      </el-tab-pane>
      <el-tab-pane
        label="其他费用信息"
        name="second"
      >
        <eplus-form-items
          title=""
          :formSchemas="otherFeeListSchemas"
        >
          <template #otherFeeList>
            <OtherFeeTable
              v-if="childrenFlag"
              :formData="formData"
              ref="otherFeeListRef"
              :required="true"
            />
          </template>
        </eplus-form-items>
      </el-tab-pane>
    </el-tabs>
  </eplus-form>
  <ProductInfo ref="ProductInfoRef" />
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import * as quotationApi from '@/api/sms/quotation'
import { getcompanySimpleList, getCustSimpleList } from '@/api/common'
import { useCountryStore } from '@/store/modules/countrylist'
import * as PortApi from '@/api/system/port'
import { PortStatusStatusEnum } from '@/utils/constants'
import OtherFeeTable from './components/OtherFeeTable.vue'
import PurchaseProducts from './components/PurchaseProducts.vue'
import ProductInfo from './components/ProductInfo.vue'
import { useRateStore } from '@/store/modules/rate'
import { isValidArray } from '@/utils/is'
import { DICT_TYPE } from '@/utils/dict'
import { EplusCountrySelect } from '@/components/EplusSelect'

const rateList = useRateStore().rateList
const countryListData = useCountryStore()
const activeName = ref('first')
const message = useMessage()
const otherFeeListRef = ref([])
defineOptions({ name: 'QuotationForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const queryId: string = (props.id || '') as string
const loading = ref(false)

const formData: EplusAuditable = reactive({})
provide('formData', formData)
const custPocListRef = ref([])
const { close } = inject('dialogEmits') as {
  close: () => void
}

const shortcuts = [
  {
    text: '今天',
    value: new Date()
  },
  {
    text: '30日后',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() + 3600 * 1000 * 24 * 30)
      return date
    }
  },
  {
    text: '60日后',
    value: () => {
      const date = new Date()
      date.setTime(date.getTime() + 3600 * 1000 * 24 * 60)
      return date
    }
  }
]
const rateRef = ref()
const basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'companyId',
    label: '公司主体',
    editable: true,
    component: (
      <eplus-custom-select
        api={getcompanySimpleList}
        label="name"
        value="id"
        placeholder="请选择"
        style="width:100%"
        onChange={(...$event) => getCompanyName($event)}
      />
    ),
    rules: {
      required: true,
      message: '请选择公司主体'
    }
  },
  {
    field: 'custId',
    label: '客户名称',
    editable: true,
    component: (
      <eplus-input-search-select
        api={getCustSimpleList}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="name"
        label="name"
        value="id"
        clearable={false}
        class="!w-100%"
        placeholder="请选择"
        onChangeEmit={(...$event) => getCustName($event)}
      />
    ),
    rules: {
      required: true,
      message: '请选择客户名称'
    }
  },
  {
    field: 'settlementTermType',
    label: '价格条款',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.PRICE_TYPE}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择价格条款'
    }
  },
  {
    field: 'custPocId',
    label: '客户联系人',
    editable: true
  },
  {
    field: 'currency',
    label: '币种',
    rules: {
      required: true,
      message: '请选择币种'
    }
  },
  {
    field: 'countryId',
    label: '出运国家',
    component: (
      <EplusCountrySelect
        onChange={(item) => getCountryName(item)}
        class="!w-100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择出运国家'
    }
  },
  {
    field: 'departurePortName',
    label: '出运口岸',
    editable: true,
    rules: {
      required: true,
      message: '请选择出运口岸'
    }
  },
  {
    field: 'validPeriod',
    label: '有效期止',
    rules: {
      required: true,
      message: '请选择有效期止'
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
          if (childrenRef.value.tableList?.length === 0) {
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

const ProductInfoRef = ref()
const openProductInfo = (data) => {
  ProductInfoRef.value.open(data)
}

//其他费用
const otherFeeListSchemas: EplusFormSchema[] = [
  {
    field: 'otherFeeList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px'
  }
]
const changeCustPoc = (val) => {
  let item = custPocListRef.value.find((item) => item.id === val)
  formData.custPocName = item.name
}

const emit = defineEmits(['handleSuccess'])
const formRef = ref()
const childrenRef = ref()
//处理提交前的formdata
const getParams = async (type?) => {
  let valid = await formRef.value.validate()
  if (!valid) {
    message.error('有未填写项必填项目,请确认！')
    return false
  }
  let params: any = JSON.parse(JSON.stringify(formData))
  let result = await childrenRef.value.checkData()
  if (result) {
    params.children = result
  } else {
    return false
  }
  let otherFeeResult = await otherFeeListRef.value.checkData()
  if (otherFeeResult) {
    params.otherFeeList = otherFeeResult
  } else {
    return false
  }
  return { sourceType: 1, submitFlag: type === 'submit' ? 1 : 0, ...params }
}
const getCompanyName = async (val) => {
  formData.companyName = val[0].name
}

const getCountryName = (item) => {
  formData.countryName = item.name
}

const currencyList = ref([])

const getCustName = (val) => {
  if (val.length === 2) {
    if (val[1]) {
      val[1].filter((item) => {
        if (item.id === val[0]) {
          formData.custId = item.id
          formData.custName = item.name
          formData.custCode = item.code

          formData.settlementTermType = item.settlementTermType || ''
          if (item.currency) {
            formData.currency = item.currency
            rateRef.value = rateList[formData.currency]
          } else {
            formData.currency = ''
            rateRef.value = ''
          }
          currencyList.value = item.currencyList

          //需要返回联系人
          if (item.custPocList?.length) {
            custPocListRef.value = item.custPocList
            formData.custPocId = item.custPocList.find((el) => el.defaultFlag)?.id
            formData.custPocName = item.custPocList.find((el) => el.defaultFlag)?.name
          } else {
            custPocListRef.value = []
            formData.custPocId = ''
            formData.custPocName = ''
          }
        }
      })
    }
  }
}
watch(
  () => formData.currency,
  (val, oldVal) => {
    if (val) {
      if (rateList[val]) {
        if (childrenRef.value) {
          childrenRef.value.setQuotation()
        }
      } else {
        message.warning('该币种暂无汇率信息，请选择其他币种！')
        formData.currency = oldVal
      }
    }
  },
  {
    immediate: true
  }
)

const submitForm = async (type?) => {
  try {
    let paramsResult = await getParams(type)
    if (!paramsResult) return false
    if (props.mode == 'create') {
      await quotationApi
        .createQuotation(paramsResult)
        .then(() => {
          message.success('提交成功')
          close()
          emit('handleSuccess', 'success')
        })
        .catch(() => {
          message.error('提交失败')
        })
    } else {
      await quotationApi
        .updateQuotation(paramsResult)
        .then(() => {
          message.success('提交成功')
          close()
          emit('handleSuccess', 'success')
        })
        .catch(() => {
          message.error('提交失败')
        })
    }
  } finally {
    loading.value = false
  }
}

const changeDeparturePort = (val, list) => {
  let item = list.find((item) => item.name === val)
  formData.departurePortId = item.id
}
const childrenFlag = ref(false)
onMounted(async () => {
  formRef.value.resetForm()

  if (props.mode === 'edit') {
    let res = await quotationApi.getQuotation(queryId)
    let custSimpleList = await getCustSimpleList({})
    if (res.custId) {
      var currentCust = custSimpleList.list.filter((item) => {
        return item.id == res.custId
      })
      if (currentCust.length) {
        if (currentCust[0].currency) {
          formData.currency = currentCust[0].currency
          rateRef.value = rateList[formData.currency]
        }
        //需要返回联系人
        if (currentCust[0].custPocList?.length) {
          custPocListRef.value = currentCust[0].custPocList
        } else {
          custPocListRef.value = []
        }
      }
    }
    Object.assign(formData, res)
    childrenFlag.value = true
  } else {
    // 公司主体默认值
    let companyList = await getcompanySimpleList()
    formData.companyId = companyList.find((item) => item.name == '泛太机电')?.id
    formData.companyName = '泛太机电'
    // 出运国家默认值
    let countryItem = countryListData.countryList.find((item) => item.name.toLowerCase() == 'china')
    formData.countryName = countryItem.name
    formData.countryId = countryItem.id
    childrenFlag.value = true
  }
})
</script>
