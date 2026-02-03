<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'scm:purchase-contract-change:create',
      handler: () => submitForm(0)
    }"
    :submitAction="{
      permi: 'scm:purchase-contract-change:submit',
      handler: () => submitForm(1)
    }"
  >
    <eplus-form-items
      title="采购信息"
      :formSchemas="purchaseSchemas"
    >
      <template #freight>
        <EplusMoney
          v-model:amount="formData.freight.amount"
          v-model:currency="formData.freight.currency"
          :currencyDisabled="true"
        />
      </template>
      <template #otherCost>
        <EplusMoney
          v-model:amount="formData.otherCost.amount"
          v-model:currency="formData.otherCost.currency"
          :currencyDisabled="true"
        />
      </template>
      <template #venderPaymentId>
        <el-select
          v-model="formData.venderPaymentId"
          clearable
          style="width: 100%"
          placeholder="请选择付款方式"
          :validate-event="false"
        >
          <el-option
            v-for="dict in paymentListData.paymentList.list"
            :key="dict.id"
            :label="dict.name"
            :value="dict.id"
          />
        </el-select>
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="附件信息"
      :formSchemas="annexSchemas"
    >
      <template #annex>
        <UploadList
          ref="UploadRef"
          :fileList="formData?.annex"
          @success="getFileList"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="产品信息"
      :formSchemas="collectionSchemas"
    >
      <template #children>
        <PurchaseProducts
          :formData="formData"
          ref="childrenRef"
          @loaded="handleChildLoaded"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
// import ChangePurchaseProducts from '../components/ChangePurchaseProducts.vue'
import PurchaseProducts from '../components/PurchaseProducts.vue'
import { cloneDeep } from 'lodash-es'
import * as PurchaseContractApi from '@/api/scm/purchaseContract'
import * as PurchaseChangeApi from '@/api/scm/purchaseChange'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import UploadList from '@/components/UploadList/index.vue'
import * as CommonApi from '@/api/common'
import { usePaymentStore } from '@/store/modules/payment'
import { PortStatusStatusEnum } from '@/utils/constants'

let oldPurchaseContractInfoRespVO = reactive({})
const paymentListData = usePaymentStore() //付款方式表
const message = useMessage()
defineOptions({ name: 'ChangeForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const queryId: string = (props.id || '') as string
const loading = ref(true)
// 子组件加载计数器
const CHILD_TABLE_COUNT = 1 // PurchaseProducts
const childLoadedCount = ref(0)
const handleChildLoaded = () => {
  childLoadedCount.value++
  if (childLoadedCount.value >= CHILD_TABLE_COUNT) {
    loading.value = false
  }
}

const formData = reactive({
  purchaseUserId: '',
  remark: '',
  freight: { amount: '', currency: 'RMB' },
  otherCost: { amount: '', currency: 'RMB' },
  purchaseUserDeptName: '',
  purchaseUserName: '',
  purchaseUserDeptId: '',
  venderName: '',
  venderCode: '',
  custCode: '',
  trackUserName: '',
  annex: []
})
provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}
const childrenRef = ref()

const purchaseUserChange = (val) => {
  if (val) {
    formData.purchaseUserDeptName = val.deptName
    formData.purchaseUserName = val.nickname
    formData.purchaseUserDeptId = val.deptId
  }
}
const portChange = (val, list) => {
  let item = list.find((item) => item.id === val)
  formData.portName = item.name
}
const venderChange = (val, list) => {
  let item = list.find((item) => item.id === val)
  formData.venderName = item.name
  formData.venderCode = item.code
}
const custChange = (val, list) => {
  let item = list.find((item) => item.id === val)
  formData.custCode = item.code
}

const trackUserIdChange = (val) => {
  formData.trackUserName = val.nickname
}
let taxTypeList = ref<any>([])
taxTypeList.value = getIntDictOptions(DICT_TYPE.TAX_TYPE)
let purchaseSchemas: EplusFormSchema[] = reactive([
  {
    field: 'code',
    label: '采购合同编号',
    component: <el-input disabled />,
    span: 6
  },
  {
    field: 'purchaseUserId',
    label: '采购员',
    component: (
      <eplus-user-select
        onChange={purchaseUserChange}
        disabled
      ></eplus-user-select>
    ),
    span: 6,
    rules: [
      {
        required: true,
        message: '请选择采购员'
      }
    ]
  },
  {
    field: 'purchaseUserDeptName',
    label: '采购员部门',
    component: <el-input disabled />,
    span: 6
  },
  {
    field: 'trackUserId',
    label: '跟单员',
    component: <eplus-user-select onChange={trackUserIdChange}></eplus-user-select>,
    span: 6,
    rules: [
      {
        required: true,
        message: '请选择跟单员'
      }
    ]
  },
  {
    field: 'venderName',
    label: '供应商名称',
    component: <el-input disabled />,
    span: 6
  },
  {
    field: 'venderPaymentId',
    label: '付款方式',
    span: 6
  },
  {
    field: 'taxType',
    label: '发票类型',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.TAX_TYPE}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择发票类型'
    },
    span: 6
  },
  {
    field: 'plannedArrivalTime',
    label: '预计交期',
    component: (
      <el-date-picker
        clearable
        valueFormat="x"
        type="date"
        style="width: 100%"
      />
    ),
    span: 6
  },
  {
    field: 'smsContractId',
    label: '销售合同',
    component: <el-input disabled />,
    span: 6
  },
  {
    field: 'custId',
    label: '客户名称',
    component: (
      <eplus-input-search-select
        api={CommonApi.getCustSimpleList}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="name"
        label="name"
        value="id"
        class="!w-100%"
        placeholder="请选择"
        onChangeEmit={custChange}
      />
    ),
    span: 6
  },
  {
    field: 'portId',
    label: '目的口岸',
    component: (
      <eplus-input-search-select
        api={PurchaseChangeApi.portList}
        params={{ pageSize: 100, pageNo: 1, status: PortStatusStatusEnum.NORMAL.status }}
        keyword="name"
        label="name"
        value="id"
        class="!w-100%"
        placeholder="请选择"
        onChangeEmit={portChange}
      />
    ),
    span: 6
  },
  {
    field: 'freight',
    label: '运费',
    span: 6
  },
  {
    field: 'otherCost',
    label: '其他费用',
    span: 6
  },
  {
    field: 'equallyType',
    label: '费用分配方式',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.EQUALLY_TYPE}
        style="width:100%"
      />
    ),
    span: 6
  },
  {
    field: 'remark',
    label: '备注',
    component: (
      <el-input
        placeholder="请输入备注"
        type="textarea"
        maxlength={100}
        show-word-limit
      />
    ),
    span: 24
  }
])

const annexSchemas: EplusFormSchema[] = [
  {
    field: 'annex',
    label: '',
    span: 8,
    labelWidth: '0px'
  }
]
const UploadRef = ref()
const getFileList = (params) => {
  formData.annex = params
}
const collectionSchemas: EplusFormSchema[] = [
  {
    field: 'children',
    label: '',
    labelWidth: '0px',
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

const emit = defineEmits(['handleSuccess', 'sucess'])
const formRef = ref()

//处理提交前的formdata
const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params: any = JSON.parse(JSON.stringify(formData))
  params.children = JSON.parse(JSON.stringify(childrenRef.value.tableList))
  params.children.forEach((item, index) => {
    item.unitPrice = { amount: item.unitPrice, currency: item.currency }
    item.withTaxPrice = { amount: item.withTaxPrice, currency: item.currency }
    // item.totalPrice = { amount: item.totalPrice, currency: item.currency }
    item.amount = { amount: item.amount, currency: item.currency }
  })
  return { ...params, createTime: undefined }
}

const submitForm = async (type) => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    if (props.mode == 'create') {
      await PurchaseChangeApi.createChange({
        old_purchaseContractInfoRespVO: oldPurchaseContractInfoRespVO,
        purchaseContractInfoRespVO: paramsResult,
        submitFlag: type
      })
      message.success('提交成功')
      close()
      emit('handleSuccess', 'success')
    } else {
      await PurchaseChangeApi.updateChange({
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
onMounted(async () => {
  try {
    loading.value = true
    if (props.mode === 'edit') {
      let res = await PurchaseChangeApi.getChangeInfo({ id: queryId })
      let obj = {
        ...res,
        freight: res.freight ? res.freight : { amount: '', currency: 'RMB' },
        otherCost: res.otherCost ? res.otherCost : { amount: '', currency: 'RMB' }
      }
      Object.assign(formData, obj)
      childrenRef.value.tableList = formData?.children
    } else {
      let res = await PurchaseContractApi.getPurchaseContract({ id: queryId })
      oldPurchaseContractInfoRespVO = cloneDeep(res)
      let obj = {
        ...res,
        freight: res.freight ? res.freight : { amount: undefined, currency: 'RMB' },
        otherCost: res.otherCost ? res.otherCost : { amount: undefined, currency: 'RMB' }
      }
      Object.assign(formData, obj)
      childrenRef.value.tableList = formData?.children
    }
    // loading由子组件的loaded事件控制
  } finally {
    // loading由handleChildLoaded控制
  }
})
</script>
