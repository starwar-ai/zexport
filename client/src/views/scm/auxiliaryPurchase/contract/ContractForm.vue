<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'auxiliary-purchase-contract:create',
      handler: () => submitForm()
    }"
    :submitAction="{
      permi: 'scm:auxiliary-purchase-contract:submit',
      handler: () => submitForm('submit')
    }"
    :closeAction="{
      handler: close
    }"
  >
    <eplus-form-items
      title="采购信息"
      :formSchemas="purchaseSchemas"
    >
      <template #venderCode>
        <eplus-input-search-select
          v-if="!loading"
          v-model="formData.venderCode"
          :api="getVenderSimpleList"
          :params="{ pageSize: 100, pageNo: 1, venderType: 1, skuQuoteFlag: 1 }"
          keyword="name"
          label="name"
          value="code"
          placeholder="请选择供应商"
          :defaultObj="{
            id: formData.venderId,
            code: formData.venderCode,
            name: formData.venderName
          }"
          @change-emit="
            (val, list) => {
              list.map((item) => {
                if (item.code === val) {
                  formData.venderId = item.id
                  formData.venderName = item.name
                }
              })
            }
          "
        />
      </template>
      <template #purchaseUserDeptName>
        <el-input
          v-model="formData.purchaseUserDeptName"
          disabled
        />
      </template>
      <template #paymentId>
        <el-select
          v-model="formData.paymentId"
          clearable
          style="width: 100%"
          placeholder="请选择付款方式"
          :validate-event="false"
          @change="
            (val) => {
              formData.paymentName = paymentListData.paymentList.list.find(
                (item) => item.id === val
              )?.name
            }
          "
        >
          <el-option
            v-for="dict in paymentListData.paymentList.list"
            :key="dict.id"
            :label="dict.name"
            :value="dict.id"
          />
        </el-select>
      </template>
      <template #deliveryDate>
        <el-date-picker
          v-model="formData.deliveryDate"
          clearable
          valueFormat="x"
          type="date"
          style="width: 100%"
          :disabledDate="
            (date) => {
              const dateStr = new Date(date)
              const dateTime = dateStr.getTime()
              let today = new Date() // 获取当前时间
              today.setHours(0, 0, 0, 0) // 将当前时间的小时、分钟、秒和毫秒设置为0，得到今天0点的时间
              let todayTimestamp = today.getTime()
              if (dateTime < todayTimestamp) {
                return true
              } else {
                return false
              }
            }
          "
        />
      </template>
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
      <template #taxType>
        <el-select
          v-model="formData.taxType"
          clearable
          style="width: 100%"
          placeholder=" "
          :validate-event="false"
          :disabled="true"
        >
          <el-option
            v-for="dict in taxTypeList"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </template>
      <template #portName>
        <eplus-input-search-select
          v-model="formData.portName"
          :api="PurchaseContractApi.getPortList"
          :params="{ pageSize: 100, pageNo: 1, status: PortStatusStatusEnum.NORMAL.status }"
          keyword="name"
          label="name"
          value="code"
          class="!w-100%"
          placeholder="请选择"
          @change-emit="changePort"
        />
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
        <AuxiliaryProducts
          :formData="formData"
          ref="childrenRef"
          type="contract"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
// import { DICT_TYPE } from '@/utils/dict'
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import AuxiliaryProducts from '../components/AuxiliaryContractProducts.vue'
import * as PurchaseContractApi from '@/api/scm/auxiliaryPurchaseContract'
import { DICT_TYPE, getIntDictOptions } from '@/utils/dict'
import UploadList from '@/components/UploadList/index.vue'
import * as VenderApi from '@/api/scm/vender'
import { PortStatusStatusEnum } from '@/utils/constants'
import { usePaymentStore } from '@/store/modules/payment'
import { cloneDeep } from 'lodash-es'
import { getVenderSimpleList } from '@/api/common'

const paymentListData = usePaymentStore() //付款方式表
const message = useMessage()
defineOptions({ name: 'ContractForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const queryId: string = (props.id || '') as string
const loading = ref(false)

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }

const formData = reactive({
  purchaseUserId: '',
  trackUserId: '',
  purchaseUserDeptName: '',
  remark: '',
  freight: { amount: '', currency: 'RMB' },
  otherCost: { amount: '', currency: 'RMB' },
  portName: '',
  paymentId: '',
  paymentName: ''
})
provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}
const childrenRef = ref()

const changeUser = (val) => {
  if (val) {
    formData.purchaseUserDeptName = val.deptName
    formData.purchaseUserName = val.nickname
    formData.purchaseUserDeptId = val.deptId
  }
}

const changePort = (val) => {
  if (val) {
    formData.portId = Number(val)
  }
}

const changeManager = (val) => {
  if (val) {
    const manager = {
      userId: val.id,
      uerCode: val.code,
      ...val
    }
    formData.manager = manager
  }
}

let taxTypeList = ref<any>([])
taxTypeList.value = getIntDictOptions(DICT_TYPE.TAX_TYPE)
let purchaseSchemas: EplusFormSchema[] = reactive([
  {
    field: 'code',
    label: '采购合同编号',
    component: <el-input disabled />
  },
  {
    field: 'companyName',
    label: '公司名称',
    component: <el-input disabled />
  },
  {
    field: 'venderCode',
    label: '供应商名称',
    rules: [
      {
        required: true,
        message: '请选择供应商'
      }
    ]
  },
  {
    field: 'purchaseUserId',
    label: '采购员',

    component: (
      <eplus-user-select
        onChange={changeUser}
        disabled
      ></eplus-user-select>
    ),
    rules: [
      {
        required: true,
        message: '请选择采购员'
      }
    ]
  },
  {
    field: 'purchaseUserDeptName',
    label: '采购员部门'
  },
  {
    field: 'managerId',
    label: '跟单员',
    component: <eplus-user-select onChange={changeManager}></eplus-user-select>,
    rules: [
      {
        required: true,
        message: '请选择跟单员'
      }
    ]
  },
  {
    field: 'deliveryDate',
    label: '交货日期'
  },
  {
    field: 'paymentId',
    label: '付款方式',
    rules: {
      required: true,
      message: '请选择付款方式',
      trigger: 'click'
    }
  },
  {
    field: 'freight',
    label: '运费'
  },
  {
    field: 'otherCost',
    label: '其他费用'
  },
  {
    field: 'deliveryAddress',
    label: '送货地址',
    component: <el-input placeholder="请输入" />,
    rules: [
      {
        required: true,
        message: '请输入送货地址'
      }
    ]
  },
  {
    field: 'minimumBaseQuantity',
    label: '最低备品比例%',
    component: (
      <el-input-number
        min={0}
        controls={false}
        precision={0}
        class="!w-90%"
      />
    )
  },
  {
    field: 'restockingDeadline',
    label: '乙方补货时限t',
    component: (
      <el-input-number
        min={0}
        controls={false}
        precision={0}
        class="!w-90%"
      />
    )
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

if (props.mode === 'create') {
  purchaseSchemas = purchaseSchemas.filter((item) => {
    return item.field !== 'code'
  })
}
const UploadRef = ref()
const getFileList = (params) => {
  formData.annex = params
}
const annexSchemas: EplusFormSchema[] = [
  {
    field: 'annex',
    label: '',
    span: 8,

    labelWidth: '0px'
  }
]
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
const getParams = async (type?) => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let result = await childrenRef.value.checkData()
  if (result) {
    let params: any = JSON.parse(JSON.stringify(formData))
    params.children = JSON.parse(JSON.stringify(childrenRef.value.tableList))
    params.children.forEach((item, index) => {
      if (!item.skuId) {
        item.skuId = item.id
      }
    })
    return { sourceType: 1, submitFlag: type === 'submit' ? 1 : 0, ...params }
  } else {
    return false
  }
}

const submitForm = async (type?) => {
  try {
    let valid = await formRef.value.validate()
    if (!valid) return false
    loading.value = true
    let paramsResult = await getParams(type)
    if (!paramsResult) return false
    if (props.mode == 'create') {
      await PurchaseContractApi.createPurchaseContract(paramsResult)
        .then(() => {
          message.success('提交成功')
          close()
          emit('handleSuccess', 'success')
        })
        .catch(() => {
          message.error('提交失败')
        })
    } else {
      await PurchaseContractApi.updatePurchaseContract(paramsResult)
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

const oldData = ref()
// import * as AuxiliaryApi from '@/api/scm/auxiliaryPurchaseContract'
const handleChange = async () => {
  try {
    let valid = await formRef.value.validate()
    if (!valid) return false
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    let res = await PurchaseContractApi.auxiliaryChange({
      old_purchaseContractInfoRespVO: oldData.value,
      purchaseContractInfoRespVO: paramsResult,
      submitFlag: 1
    })
    if (paramsResult.confirmFlag === 0) {
      await PurchaseContractApi.changeConfirm(paramsResult.id)
    }
    message.notifyPushSuccess('变更成功', res, 'auxiliary-change')
    close()
    emit('handleSuccess', 'success')
  } finally {
    loading.value = false
  }
}

const setBtns = () => {
  if (props.mode === 'change') {
    clearDialogActions()
    updateDialogActions(
      <el-button
        onClick={() => {
          handleChange()
        }}
        type="primary"
        key="save"
      >
        变更
      </el-button>
    )
  }
}
onMounted(async () => {
  await VenderApi.getPayableVender()
  if (props.mode === 'edit' || props.mode === 'change') {
    loading.value = true
    let res = await PurchaseContractApi.getPurchaseContract({ id: queryId })
    oldData.value = cloneDeep(res)
    let obj = {
      ...res,
      freight: res.freight ? res.freight : { amount: 0, currency: 'RMB' },
      otherCost: res.otherCost ? res.otherCost : { amount: 0, currency: 'RMB' },
      paymentId: res.paymentId
        ? res.paymentId
        : res.paymentList.find((item) => item.defaultFlag === 1)?.id,
      managerId: res.manager?.userId
    }
    Object.assign(formData, obj)
    childrenRef.value.tableList = formData?.children
    loading.value = false
  }
  setBtns()
})
</script>
