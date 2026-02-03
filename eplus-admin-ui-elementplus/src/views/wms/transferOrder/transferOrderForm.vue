<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'scm:purchase-plan:create',
      handler: () => submitForm()
    }"
    :closeAction="{
      handler: close
    }"
  >
    <eplus-form-items
      title="基本信息"
      :formSchemas="basicSchemas"
    >
      <template #saleContractCode>
        <el-select
          v-model="formData.saleContractCode"
          filterable
          remote
          reserve-keyword
          placeholder="请输入拨入订单号"
          :remote-method="remoteMethod"
          :loading="loading"
          style="width: 100%"
          @change="changeSaleContractCode"
        >
          <el-option
            v-for="item in contractList"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </template>
      <template #inputUserName>
        <el-input
          v-model="formData.inputUser.nickname"
          disabled
      /></template>
      <template #inputUserDeptName>
        <el-input
          v-model="formData.inputUser.deptName"
          disabled
      /></template>
    </eplus-form-items>
    <eplus-form-items
      title="调拨明细"
      :formSchemas="tansferOrderSchemas"
    >
      <template #children>
        <TransferOrderItem
          ref="childrenRef"
          type="plan"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
// import { DICT_TYPE } from '@/utils/dict'
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import TransferOrderItem from './components/TransferOrderItem.vue'
import * as PurchasePlanApi from '@/api/scm/purchasePlan'
import * as SaleContractApi from '@/api/sms/saleContract/export'
import { useUserStore } from '@/store/modules/user'
import * as TransferOrderApi from '@/api/wms/transferOrder'

const message = useMessage()
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const loading = ref(true)
const formRef = ref<any>(null)
const childrenRef = ref<any>(null)
const { close } = inject('dialogEmits') as {
  close: () => void
}
const contractList = ref<any>([])
const changeSaleContractCode = (val) => {
  if (val) {
    let res = JSON.parse(val)
    formData.custName = res.custName
    formData.custCode = res.custCode
    formData.saleContractCode = res.code
    provide('formData', formData)
  }
}
const remoteMethod = async (query: string) => {
  if (query) {
    let res = await SaleContractApi.getCustSaleContractSimple({
      code: query,
      companyId: formData.companyId
    })
    let formatRes = res.map((item) => {
      return { value: `${JSON.stringify(item)}`, label: `${item.code}` }
    })
    contractList.value = formatRes.filter((item) => {
      return item.label.toLowerCase().includes(query.toLowerCase())
    })
  } else {
    contractList.value = []
  }
}
const formData = reactive({
  purchaseUserId: '',
  purchaseUserDeptName: '',
  remark: '',
  annex: null,
  custName: '',
  custCode: '',
  custId: '',
  saleContractCode: '',
  companyId: null,
  inputUser: { nickname: '', deptName: '' },
  deptName: ''
})
//基本信息表单配置
let basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'companyId',
    label: '库存主体',
    editable: true,
    component: (
      <eplus-custom-select
        api={PurchasePlanApi.getcompanySimpleList}
        label="name"
        value="id"
        placeholder="请选择库存主体"
        class="!w-100%"
      />
    ),
    span: 8,
    rules: [
      {
        required: true,
        message: '请选择库存主体'
      }
    ]
  },
  {
    field: 'transferType',
    label: '调拨类型',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.VENDER_TYPE}
        style="width:100%"
      />
    ),
    span: 8,
    rules: {
      required: true,
      message: '请选择调拨类型'
    }
  },
  {
    field: 'saleContractCode',
    label: '拨入订单号',
    editable: true,
    span: 8,
    rules: {
      required: true,
      message: '请输入拨入订单号',
      trigger: 'click'
    }
  },
  {
    field: 'custCode',
    label: '客户编号',
    editable: true,
    component: <el-input disabled />,
    span: 8
  },
  {
    field: 'custName',
    label: '客户名称',
    editable: true,
    component: <el-input disabled />,
    span: 8
  },
  {
    field: 'inputUserName',
    label: '录入人',
    editable: true,
    span: 8
  },
  {
    field: 'inputUserDeptName',
    label: '录入人部门',
    editable: true,
    span: 8
  }
])
//调拨明细表单配置
const tansferOrderSchemas: EplusFormSchema[] = [
  {
    field: 'children',
    label: '',
    labelWidth: '0px',
    editable: true,
    span: 24
    // rules: [
    //   {
    //     required: true,
    //     validator: (rule: any, value: any, callback: any) => {
    //       if (childrenRef.value.tableList?.length === 0) {
    //         callback(new Error('请选择产品信息'))
    //       } else {
    //         callback()
    //       }
    //     },
    //     trigger: 'blur'
    //   }
    // ]
  }
]
//处理提交前的formdata
const getParams = async (type?) => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let result = await childrenRef.value.checkData()
  if (result) {
    let params: any = JSON.parse(JSON.stringify(formData))
    params.children = JSON.parse(JSON.stringify(childrenRef.value.tableList))
    return {
      submitFlag: type === 'submit' ? 1 : 0,
      ...params
    }
  } else {
    return false
  }
}
const user: any = useUserStore().getUser
onMounted(async () => {
  try {
    loading.value = true
    formData.inputUser = user
  } catch (error) {
    console.log(error)
  } finally {
    loading.value = false
  }
})
provide('formData', formData)
//提交表单
const emit = defineEmits(['handleSuccess', 'sucess'])
const submitForm = async (type?) => {
  try {
    let valid = await formRef.value.validate()
    if (!valid) return false
    loading.value = true
    let paramsResult = await getParams(type)
    if (paramsResult) {
      try {
        props.mode == 'create'
          ? await TransferOrderApi.createTransferOrder(paramsResult)
          : await TransferOrderApi.updateTransferOrder(paramsResult)
        message.success('提交成功')
        close()
        emit('handleSuccess', 'success')
      } catch (error) {
        message.error('提交失败')
      }
    }
  } finally {
    loading.value = false
  }
}
</script>
<style lang="scss" scoped></style>
