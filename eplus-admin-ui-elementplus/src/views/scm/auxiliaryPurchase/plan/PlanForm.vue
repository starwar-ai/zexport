<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'scm:auxiliary-purchase-plan:create',
      handler: () => submitForm()
    }"
    :submitAction="{
      permi: 'scm:auxiliary-purchase-plan:submit',
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
      <template #purchaseUserDeptName>
        <el-input
          v-model="formData.purchaseUserDeptName"
          disabled
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
          type="plan"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
// import { DICT_TYPE } from '@/utils/dict'
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import AuxiliaryProducts from '../components/AuxiliaryPlanProducts.vue'
import * as PurchasePlanApi from '@/api/scm/auxiliaryPurchasePlan'
import * as UserApi from '@/api/system/user'
import UploadList from '@/components/UploadList/index.vue'
import { getNotInnerCompanySimpleList } from '@/api/common'

const message = useMessage()
defineOptions({ name: 'PlanForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const queryId: string = (props.id || '') as string
const loading = ref(false)
const simpleUserList = ref([])

const formData = reactive({
  purchaseUserId: '',
  purchaseUserDeptName: '',
  remark: '',
  annex: null
})
provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}
const childrenRef = ref()

const getCustName = (val) => {
  if (Array.isArray(val) && val.length === 2) {
    val[0] &&
      val[1] &&
      val[1].filter((item) => {
        if (item.id === val[0]) {
          formData.custName = item.name
          formData.custCode = item.code
        }
      })
    if (!val[0]) {
      formData.custName = ''
      formData.custCode = ''
    }
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
      />
    ),
    span: 6,
    rules: [
      {
        required: true,
        message: '请选择采购主体'
      }
    ]
  },
  // {
  //   field: 'custId',
  //   label: '客户名称',
  //   editable: true,
  //   component: (
  //     <eplus-input-search-select
  //       api={PurchasePlanApi.getCustSimpleList}
  //       params={{ pageSize: 100, pageNo: 1 }}
  //       keyword="name"
  //       label="name"
  //       value="id"
  //       class="!w-100%"
  //       placeholder="请选择"
  //       onChangeEmit={(...$event) => getCustName($event)}
  //     />
  //   ),
  //   span: 6,
  //   rules: [
  //     {
  //       required: false,
  //       message: '请输入客户名称'
  //     }
  //   ]
  // },
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
        }}
      />
    ),
    span: 6
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
    span: 24,
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
          callback()
        }
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
      item.purchaseType = 1
      if (!item.skuId) {
        item.skuId = item.id
      }
    })
    if (!params?.annex) {
      params.annex = []
    }
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
    if (paramsResult) {
      try {
        props.mode == 'create'
          ? await PurchasePlanApi.createPurchasePlan(paramsResult)
          : await PurchasePlanApi.updatePurchasePlan(paramsResult)
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
onMounted(async () => {
  simpleUserList.value = await UserApi.getSimpleUserList()
  if (props.mode === 'edit') {
    let res = await PurchasePlanApi.getPurchasePlan({ id: queryId })
    // res.actualUserId = res.actualUser?.nickname
    // res.deptName = res.actualUser?.deptName
    // // UploadListRef.value.fileData(formData.invoiceList || [])
    Object.assign(formData, res)
    childrenRef.value.tableList = formData?.children
  }
})
</script>
