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
    :submitAction="{
      permi: 'scm:purchase-plan:submit',
      handler: () => submitForm('submit')
    }"
    :closeAction="{
      handler: close
    }"
  >
    <eplus-form-items
      title="采购信息"
      :formSchemas="purchaseSchemas"
    />
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
          type="plan"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
  <Dialog
    v-model="dialogVisible"
    title="提示"
    width="500"
    :before-close="cancelChange"
  >
    <span>修改客户名称将会把已填的产品信息重置，是否确认修改？</span>
    <template #footer>
      <div class="dialog-footer">
        <el-button
          type="primary"
          @click="confirmChange"
          >确认修改</el-button
        >
        <el-button @click="cancelChange"> 取消修改 </el-button>
      </div>
    </template>
  </Dialog>
</template>
<script setup lang="tsx">
// import { DICT_TYPE } from '@/utils/dict'
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import PurchaseProducts from '../components/PurchaseProducts.vue'
import * as PurchasePlanApi from '@/api/scm/purchasePlan'
import UploadList from '@/components/UploadList/index.vue'
import { PurchaseSourceTypeEnum } from '@/utils/constants'
import { getNotInnerCompanySimpleList } from '@/api/common'

const dialogVisible = ref(false)
const message = useMessage()
defineOptions({ name: 'PlanForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const queryId: string = (props.id || '') as string
const loading = ref(false)
const lastCustRef = ref({ code: '', id: '', name: '' })

const formData = reactive({
  purchaseUserId: '',
  purchaseUserDeptName: '',
  remark: '',
  annex: null,
  custName: '',
  custCode: '',
  custId: '',
  availableCurrencyList: []
})
provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}
const childrenRef = ref()

//提示框逻辑
const confirmChange = () => {
  lastCustRef.value.code = formData?.custCode
  lastCustRef.value.id = formData?.custId
  lastCustRef.value.name = formData?.custName

  childrenRef.value.clearTableList()
  dialogVisible.value = false
}

const cancelChange = () => {
  console.log(lastCustRef.value)
  formData.custCode = lastCustRef.value?.code
  formData.custId = lastCustRef.value?.id
  dialogVisible.value = false
}
const getCustName = async (val) => {
  if (Array.isArray(val) && val.length === 2) {
    const [custId, customers] = val
    if (custId && customers && customers.length) {
      const customer = customers.find((item) => item.id === custId)
      if (customer) {
        const tableIdList = childrenRef.value?.tableList.map((i) => i.skuId)
        const res = await PurchasePlanApi.ifChangeCust({
          custCode: customer.code,
          skuIdList: tableIdList || []
        })
        if (childrenRef.value.tableList?.length > 0 && !res) {
          formData.custCode = customer.code
          dialogVisible.value = true
        } else {
          formData.custName = customer.name
          formData.custCode = customer.code
          lastCustRef.value = { id: customer.id, code: customer.code, name: customer.name }
        }
      }
    } else {
      formData.custName = ''
      formData.custCode = ''
    }
  } else if (!formData.custId) {
    dialogVisible.value = true
  }
}
let purchaseSchemas: EplusFormSchema[] = reactive([
  {
    field: 'companyId',
    label: '采购主体',
    editable: true,
    component: (
      <eplus-custom-select
        api={getNotInnerCompanySimpleList}
        label="name"
        value="id"
        placeholder="请选择采购主体"
        class="!w-100%"
        onChange={(item) => {
          formData.availableCurrencyList = item.availableCurrencyList
        }}
      />
    ),
    rules: [
      {
        required: true,
        message: '请选择采购主体'
      }
    ]
  },
  {
    field: 'availableCurrencyList',
    label: '可用币种',
    editable: true,
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.CURRENCY_TYPE}
        class="!w-100%"
        disabled
        multiple
      />
    )
  },
  {
    field: 'custId',
    label: '客户名称',
    xl: 8,
    lg: 12,
    component: (
      <eplus-input-search-select
        api={PurchasePlanApi.getCustSimpleList}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="nameCode"
        label="name"
        value="id"
        formatLabel={(item) => {
          return `${item.name}(${item.code})`
        }}
        class="!w-100%"
        placeholder="请选择"
        onChangeEmit={(...$event) => getCustName($event)}
        defaultObj={{
          id: formData?.custId || undefined,
          code: formData?.custCode || undefined,
          name: formData?.custName || undefined
        }}
      />
    ),
    rules: [
      {
        required: false,
        message: '请输入客户名称'
      }
    ]
  },
  {
    field: 'estDeliveryDate',
    label: '预计交期',
    // formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss'),
    component: (
      <el-date-picker
        type="date"
        placeholder="请选择"
        valueFormat="x"
        class="!w-100%"
        disabledDate={(date) => {
          const dateTime = new Date(date).getTime()
          let today = new Date() // 获取当前时间
          today.setHours(0, 0, 0, 0) // 将当前时间的小时、分钟、秒和毫秒设置为0，得到今天0点的时间
          let todayTimestamp = today.getTime()
          return dateTime < todayTimestamp
        }}
      />
    )
  },
  {
    field: 'remark',
    label: '备注',
    editable: true,
    component: (
      <el-input
        placeholder="请输入备注"
        type="textarea"
        maxlength={100}
        show-word-limit
      />
    ),
    span: 12
  }
])

const UploadRef = ref()
const getFileList = (params) => {
  formData.annex = params
}
const annexSchemas: EplusFormSchema[] = [
  {
    field: 'annex',
    label: '',
    span: 8,
    editable: true,
    labelWidth: '0px'
  }
]
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
    if (!params?.annex) {
      params.annex = []
    }
    return {
      sourceType: PurchaseSourceTypeEnum.MANUALADD.type,
      submitFlag: type === 'submit' ? 1 : 0,
      ...params
    }
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
    if (paramsResult) {
      try {
        const req =
          props.mode == 'create'
            ? PurchasePlanApi.createPurchasePlan(paramsResult)
            : PurchasePlanApi.updatePurchasePlan(paramsResult)

        // props.mode == 'create'
        //   ? await PurchasePlanApi.createPurchasePlan(paramsResult)
        //   : await PurchasePlanApi.updatePurchasePlan(paramsResult)
        await req
        message.success('提交成功')
        close()
      } catch (error) {
        message.error('提交失败')
      }
    }
  } finally {
    loading.value = false
  }
}
onMounted(async () => {
  if (props.mode === 'edit') {
    let res = await PurchasePlanApi.getPurchasePlan({ id: queryId })
    Object.assign(formData, res)
    childrenRef.value.tableList = formData?.children
    lastCustRef.value = { id: res.custId, code: res.custCode, name: res.custName }
  }
})
</script>
