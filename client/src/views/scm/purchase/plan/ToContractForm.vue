<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    style="height: 100%; overflow: auto"
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
      :desc="childrenDesc"
      descColor="red"
      :formSchemas="collectionSchemas"
    >
      <template #children>
        <SkuCom
          :maxHeight="maxHeight"
          :formData="formData"
          ref="childrenRef"
          @setwaining="setWaining"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
// import { DICT_TYPE } from '@/utils/dict'
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import SkuCom from './components/SkuCom.vue'
import * as PurchasePlanApi from '@/api/scm/purchasePlan'
import UploadList from '@/components/UploadList/index.vue'
import { PurchaseSourceTypeEnum } from '@/utils/constants'
import { cloneDeep } from 'lodash-es'
import { checkValidVender } from '@/api/common'
import { isValidArray } from '@/utils/is'

const dialogVisible = ref(false)
const message = useMessage()
defineOptions({ name: 'PlanForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const showFlag = ref(false)
const loading = ref(false)
const lastCustRef = ref({ code: '', id: '' })
const custDefaultObj = ref({})

const formData = reactive({
  purchaseUserId: '',
  purchaseUserDeptName: '',
  remark: '',
  annex: null,
  custName: '',
  custCode: '',
  custId: ''
})
provide('formData', formData)

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }
const { close } = inject('dialogEmits') as {
  close: () => void
}
const childrenRef = ref()
const childrenDesc = ref('')
const setWaining = (val: any) => {
  if (val) {
    childrenDesc.value =
      '检测有产品使用了库存，系统将根据产品的待采数量生成采购合同，如待采购数量为0则这部分产品不生成采购合同！！！'
  } else {
    childrenDesc.value = ''
  }
}
// const cancelChange = () => {
//   formData.custCode = lastCustRef.value?.code
//   formData.custId = lastCustRef.value?.id
//   dialogVisible.value = false
// }
// const getCustName = async (val) => {
//   if (Array.isArray(val) && val.length === 2) {
//     const [custId, customers] = val
//     if (custId && customers && customers.length) {
//       const customer = customers.find((item) => item.id === custId)
//       if (customer) {
//         const tableIdList = childrenRef.value?.tableList.map((i) => i.skuId)
//         const res = await PurchasePlanApi.ifChangeCust({
//           custCode: customer.code,
//           skuIdList: tableIdList || []
//         })
//         if (childrenRef.value.tableList?.length > 0 && !res) {
//           formData.custCode = customer.code
//           dialogVisible.value = true
//         } else {
//           formData.custName = customer.name
//           formData.custCode = customer.code
//           lastCustRef.value = { id: customer.id, code: customer.code }
//         }
//       }
//     } else {
//       formData.custName = ''
//       formData.custCode = ''
//     }
//   } else if (!formData.custId) {
//     dialogVisible.value = true
//   }
// }
let purchaseSchemas: EplusFormSchema[] = reactive([
  {
    field: 'saleContractCode',
    label: '销售合同号',
    component: <el-input disabled />
  },
  {
    field: 'code',
    label: '采购计划号',
    component: <el-input disabled />
  },
  {
    field: 'companyName',
    label: '公司主体',
    component: <el-input disabled />
  },
  {
    field: 'custName',
    label: '客户名称',
    component: <el-input disabled />
  },
  {
    field: 'estDeliveryDate',
    label: '预计交期',
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
    field: 'salesInfo',
    label: '业务员',
    component: <el-input disabled />
  },
  {
    field: 'creatorInfo',
    label: '创建人',
    component: <el-input disabled />
  },
  {
    field: 'createTime',
    label: '创建时间',
    component: (
      <el-date-picker
        type="date"
        placeholder="请选择"
        valueFormat="x"
        class="!w-100%"
        disabled
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
const toJson = (list) => {
  return list.map((item, index) => {
    return {
      ...item,
      amount: { amount: item.withTotalTaxPrice, currency: item.currency },
      withTaxPrice: { amount: item.withTaxPrice, currency: item.currency },
      unitPrice: { amount: item.unitPrice, currency: item.currency }
    }
  })
}
//处理提交前的formdata
const getParams = async (type?) => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params: any = cloneDeep(formData)
  let childrenValid = await childrenRef.value.checkData(type)
  if (!childrenValid) return false
  if (type === 1) {
    params.planList = toJson(childrenRef.value.getTableDate('one'))
    params.combineList = [
      ...toJson(childrenRef.value.getTableDate('two')),
      ...toJson(childrenRef.value.getTableDate('three'))
    ]
  } else {
    params.planList = toJson(childrenRef.value.tableList)
    params.combineList = [
      ...toJson(childrenRef.value.twoLevelList),
      ...toJson(childrenRef.value.threeLevelList)
    ]
  }

  if (!params?.annex) {
    params.annex = []
  }
  const venderList = [
    ...params.combineList.map((item) => item.venderCode),
    ...params.planList.map((item) => item.venderCode)
  ]
  await checkValidVender(venderList.join(','))
  return {
    sourceType: PurchaseSourceTypeEnum.MANUALADD.type,
    submitFlag: type,
    ...params
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
        const res = await PurchasePlanApi.purchaseToContract(paramsResult)
        if (isValidArray(res)) {
          message.notifyPushSuccess('生成采购合同成功', res, 'scm-purchase-contract')
        } else {
          message.success('保存成功')
        }
        close()
      } catch (error) {
        message.error('提交失败')
      }
    }
  } finally {
    loading.value = false
  }
}
const paymentBtnShow = () => {
  if (props?.id) {
    updateDialogActions(
      <el-button
        onClick={async () => {
          try {
            submitForm(0)
          } catch {
            message.error('保存失败')
          }
        }}
        type="primary"
        key="toContract"
      >
        保存
      </el-button>,
      <el-button
        onClick={async () => {
          try {
            submitForm(1)
          } catch {
            message.error('提交失败')
          }
        }}
        key="toContract"
      >
        下推采购合同
      </el-button>
    )
  }
}
const maxHeight = ref(600)
onMounted(async () => {
  if (props.mode === 'edit') {
    paymentBtnShow()
    let res = await PurchasePlanApi.getPurchasePlan({ id: props.id })
    Object.assign(formData, {
      ...res,
      salesInfo: `${res.sales?.nickname}(${res.sales?.deptName})`,
      creatorInfo: `${res.creatorName}(${res.creatorDeptName})`
    })

    custDefaultObj.value = {
      code: formData.custCode,
      id: formData.custId,
      name: formData.custName
    }
    showFlag.value = true
    childrenRef.value.init()
  }
  nextTick(() => {
    maxHeight.value = formRef.value.$el.clientHeight - 80
  })
})
</script>
