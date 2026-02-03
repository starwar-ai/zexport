<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
  >
    <eplus-form-items
      title="质检单信息"
      :formSchemas="purchaseSchemas"
    >
      <template #inspectionAddress>
        <el-input
          v-model="formData.inspectionAddress"
          disabled
        />
      </template>

      <template #inspectionTime>
        <el-date-picker
          v-model="formData.inspectionTime"
          clearable
          valueFormat="x"
          type="date"
          style="width: 100%"
        />
      </template>

      <template #amount>
        <EplusMoney
          v-model:amount="formData.amount.amount"
          v-model:currency="formData.amount.currency"
        />
      </template>

      <template #taxType>
        <el-select
          v-model="formData.taxType"
          clearable
          style="width: 100%"
          placeholder=" "
          :validate-event="false"
        >
          <el-option
            v-for="dict in taxTypeList"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="质检结果"
      :formSchemas="annexSchemas"
    >
      <template #resultAnnex>
        <UploadList
          ref="UploadRef"
          :fileList="formData?.resultAnnex"
          @success="getFileList"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title=""
      :formSchemas="collectionSchemas"
    >
      <template #children>
        <eplus-form-table
          ref="TableRef"
          :list="tableList"
          :defaultVal="{}"
          :schema="tableColumns"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import * as QualityInspectionApi from '@/api/qms/qualityinspection' // 验货单接口集合
import { DICT_TYPE, getDictLabel, getIntDictOptions } from '@/utils/dict'
import UploadList from '@/components/UploadList/index.vue'
import { TableColumn } from '@/types/table'
import { useUserStore } from '@/store/modules/user'

const message = useMessage()
defineOptions({ name: 'ContractForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const queryId: string = (props.id || '') as string
const loading = ref(false)
const simpleUserList = ref([])
const TableRef = ref()
const tableList = ref([]) // 纸质结果列表
const selectedList = ref([])
const failDescFlag = ref(false)

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }

const formData = reactive({
  inspectionTime: Date.now(),
  purchaseUserId: '',
  trackUserId: '',
  inspectionAddress: '',
  remark: '',
  amount: { amount: 0, currency: 'RMB' },
  resultAnnex: {}
})

// 限制日期
const disabledDate = (time: Date) => {
  return time.getTime() < Date.now()
}

provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}

// 监听总验货费用
// watch(
//   () => formData.amount.amount,
//   (newValue) => {
//     share(formData.allocationType == 2)
//   }
// )

// 监听分摊规则
// watch(
//   () => formData.allocationType,
//   (newValue) => {
//     share(newValue)
//   }
// )
// const share = (newValue) => {
//   let totalCost = formData.amount.amount
//   let totalQuantity = 0
//   let totalProcur = 0

//   if (newValue == 2) {
//     formData.children.forEach((e) => {
//       totalQuantity += e.quantity
//     })

//     let price = totalCost / totalQuantity
//     formData.children.forEach((item) => {
//       item.priceAmount = (price * item.quantity).toFixed(0)
//     })
//   } else {
//     formData.children.forEach((e) => {
//       totalProcur += e.totalPriceAmount
//     })

//     formData.children.forEach((item) => {
//       item.priceAmount = ((item.totalPriceAmount / totalProcur) * totalCost).toFixed(0)
//     })
//   }

// 补差价
//   let amountMoney = 0 // 产品总价
//   let difference = 0 // 差价
//   formData.children.forEach((e) => {
//     amountMoney += e.priceAmount * 1
//   })
//   difference = totalCost - amountMoney
//   let lastItem = formData.children[formData.children.length - 1]
//   if (difference > 0) {
//     formData.children[formData.children.length - 1].priceAmount = lastItem.priceAmount + difference
//   } else {
//     formData.children[formData.children.length - 1].priceAmount = lastItem.priceAmount - difference
//   }
// }

const childrenRef = ref()
const handleSelect = (val) => {
  // 1  金额  2  数量
}
const changeUser = (val) => {
  if (val) {
    formData.inspectorName = val.nickname
    formData.inspectorId = val.id
  }
}
const changeVender = (val) => {
  if (val) {
    formData.venderId = Number(val)
  }
}
const changePort = (val) => {
  if (val) {
    formData.portId = Number(val)
  }
}
const changeTrackUser = (val) => {
  if (val) {
    formData.trackUserName = val.nickname
  }
}
const getCustName = (val) => {
  if (val.length === 2) {
    if (val[1]) {
      val[1].filter((item) => {
        if (item.id === val[0]) {
          formData.custId = item.id
          formData.custName = item.name
          formData.custCode = item.code
        }
      })
    }
  }
}
let taxTypeList = ref<any>([])
taxTypeList.value = getIntDictOptions(DICT_TYPE.TAX_TYPE)
// 质检单信息
let purchaseSchemas: EplusFormSchema[] = reactive([
  {
    field: 'code',
    label: '验货单号',
    editable: true,
    component: <el-input disabled />
  },
  {
    field: 'inspectorName',
    label: '验货人',
    editable: true,
    component: (
      <eplus-user-select
        onChange={changeUser}
        simpleUserList={simpleUserList.value}
      ></eplus-user-select>
    ),
    rules: [
      {
        trigger: 'change',
        required: true,
        message: '请选择采购员'
      }
    ]
  },
  {
    field: 'inspectionTime',
    label: '实际验货时间',
    editable: true,
    rules: [
      {
        required: true,
        message: '请选择实际验货时间'
      }
    ]
  },
  {
    field: 'venderName',
    label: '供应商',
    editable: true,
    component: <el-input disabled />
  },
  {
    field: 'inspectionAddress',
    label: '工厂地址',
    editable: true
  },
  {
    field: 'specialAttentionNotice',
    label: '特别注意事项',
    component: <el-input />
  },
  // {
  //   field: 'amount',
  //   label: '总验货费',
  //   editable: true,
  //   span: 6,
  //   component: <el-input />,
  //   rules: {
  //     trigger: 'blur',
  //     required: true,
  //     message: '请填写总验货费'
  //   }
  // },
  {
    field: 'amount',
    label: '总验货费',
    rules: {
      validator: (rule: any, value: any, callback: any) => {
        if (value.amount <= 0 && ![5, 6, 7].includes(formData.inspectionType)) {
          callback(new Error('请输入总验货费'))
        } else {
          callback()
        }
      }
    }
  },

  {
    field: 'purchaseContractCode',
    label: '采购合同',
    component: <el-input disabled />
  },
  {
    field: 'saleContractCode',
    label: '销售合同',
    component: <el-input disabled />
  },
  {
    field: 'custName',
    label: '客户名称',
    component: <el-input disabled />
  },
  {
    field: 'inspectionNode',
    label: '验货节点',
    editable: true,
    component: (
      <eplus-dict-select
        disabled
        dictType={DICT_TYPE.INSPECTION_NODE}
        style="width:100%"
      />
    )
  },

  {
    field: 'createTime',
    label: '创建日期',
    component: (
      <el-date-picker
        disabled
        style="width:100%"
        valueFormat="x"
        type="date"
      />
    )
  },
  {
    field: 'reinspectionFlag',
    label: '是否重验',
    component: (
      <eplus-dict-select
        disabled
        dictType={DICT_TYPE.IS_INT}
        style="width:100%"
      />
    )
  },
  {
    field: 'purchaseUserName',
    label: '采购员',
    component: <el-input disabled />
  },
  {
    field: 'salesName',
    label: '业务员',
    component: <el-input disabled />
  }

  // {
  //   field: 'allocationType',
  //   label: '分摊规则',
  //   component: (
  //     <eplus-dict-select
  //       onChange={(val) => {
  //         handleSelect(val)
  //       }}
  //       dictType={DICT_TYPE.INSPECTION_ALLOCATION_TYPE}
  //     ></eplus-dict-select>
  //   ),
  //   rules: {
  //     required: true,
  //     message: '请填写分摊规则'
  //   },
  //   span: 6
  // }
])

// 质检结果
let tableColumns = reactive<TableColumn[]>([
  {
    field: 'inspectionStatus',
    label: '验货结果',
    width: 180,
    batchEditFlag: true,
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.INSPECT_RESULT_TYPE}
        clearable={true}
        onChangeEmit={(val) => {
          inspectStatus(val)
        }}
      />
    ),
    rules: [{ required: true, message: '请选择采购类型' }]
  },

  {
    field: 'failDesc',
    label: '问题描述',
    width: '220px',
    batchEditFlag: true,
    component: <el-input style="width: 150px" />
    // rules: [
    //   {
    //     trigger: 'blur',
    //     required: failDescFlag,
    //     message: '请输入问题描述'
    //   }
    // ]
  },
  {
    field: 'lastFailDesc',
    label: '历史出现问题',
    width: '220px',
    showOverflowTooltip: true
  },
  // {
  //   field: 'priceAmount',
  //   label: '验货费',
  //   minWidth: '80px'
  // },
  // {
  //   field: 'totalPriceAmount',
  //   label: '产品总价',
  //   minWidth: '80px'
  // },
  // {
  //   field: 'inspectionStatus',
  //   label: '验货状态',
  //   minWidth: '80px'
  // },

  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    minWidth: '80px'
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    minWidth: '80px'
  },
  {
    field: 'oskuCode',
    label: '自营产品货号',
    minWidth: '80px'
  },
  {
    field: 'skuName',
    label: '产品名称',
    minWidth: '80px'
  },

  {
    field: 'quantity',
    label: '数量',
    width: '80px'
  },
  // {
  //   field: 'purchaseUserName',
  //   label: '采购员'
  // },
  // {
  //   field: 'purchaseUserName',
  //   label: '业务员'
  // },
  // {
  //   field: 'purchaseUserName',
  //   label: '跟单员'
  // },

  {
    field: 'qtyPerInnerbox',
    label: '内盒数量'
  },

  {
    field: 'qtyPerOuterbox',
    label: '外箱装量'
  },
  {
    field: 'boxCount',
    label: '计算箱数'
  },
  {
    field: 'remark',
    label: '备注',
    width: '150px'
  }
])

if (props.mode === 'create') {
  purchaseSchemas = purchaseSchemas.filter((item) => {
    return item.field !== 'code'
  })
}
const inspectStatus = async (val) => {
  failDescFlag.value = val == 2 ? false : true
}

const UploadRef = ref()
const getFileList = (params) => {
  formData.resultAnnex = params
}
const annexSchemas: EplusFormSchema[] = [
  {
    field: 'resultAnnex',
    label: '结果附件',
    span: 8,
    editable: true,
    rules: [
      {
        required: true,
        message: '结果附件不能为空'
      }
    ]
  }
]
const collectionSchemas: EplusFormSchema[] = [
  {
    field: 'children',
    label: '',
    labelWidth: '0px',
    editable: true,
    span: 24
  }
]

const emit = defineEmits(['handleSuccess', 'sucess'])
const formRef = ref()

//处理提交前的formdata
const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false

  let params: any = JSON.parse(JSON.stringify(formData))
  params.children = tableList.value

  let pass = false
  let noPass = false
  let part = false
  params.children.forEach((item, index) => {
    // 赋值验货费

    item.amount = {
      amount: item.priceAmount,
      currency: formData.amount.currency
    }
    if (item.inspectionStatus == '1') {
      // 待定
      part = true
    }
    if (item.inspectionStatus == '3') {
      // 成功
      pass = true
    }
    if (item.inspectionStatus == '2') {
      // 失败
      noPass = true
    }

    // 判断传参
    if (pass && !noPass && !part) {
      // 通过
      params.qualityInspectionStatus = 5 // 通过
    } else if (!pass && noPass && part) {
      params.qualityInspectionStatus = 4 // 失败
    } else if (!pass && noPass && !part) {
      params.qualityInspectionStatus = 4 // 失败
    } else if (!pass && !noPass && part) {
      // 全部待定
      params.qualityInspectionStatus = 4 // 失败
    } else {
      params.qualityInspectionStatus = 6 // 部分
    }
  })

  return { ...params }
}

const submitForm = async () => {
  try {
    let valid = await formRef.value.validate()
    if (!valid) return false

    if (!formData.amount.amount) {
      message.warning(`总验货费不能为空或 0 `)
      return false
    }

    let arr = tableList.value
    for (let i = 0; i < arr.length; i++) {
      if (arr[i].inspectionStatus !== 3 && !arr[i].failDesc) {
        let result = getDictLabel(DICT_TYPE.INSPECT_RESULT_TYPE, arr[i]?.inspectionStatus)
        message.warning(`第${i + 1}条验货结果${result}的数据问题描述不能为空`)
        return false
      }
    }

    let paramsResult = await getParams()
    if (!paramsResult) return false
    loading.value = true
    paramsResult.itemSaveReqVOList = paramsResult.children
    paramsResult.children = []
    await QualityInspectionApi.qmsUploadInspection({ ...paramsResult })
      .then(() => {
        message.success('提交成功')
        close()
        emit('handleSuccess', 'success')
      })
      .catch(() => {
        message.error('提交失败')
      })
  } finally {
    loading.value = false
  }
}
onMounted(async () => {
  updateDialogActions(
    <el-button
      onClick={() => {
        submitForm('submit')
      }}
      key="YsbillFormSubmit"
      hasPermi="wms:bill:update"
      type="primary"
      isShow={props.id ? true : false}
    >
      提交
    </el-button>
  )

  let res = await QualityInspectionApi.getQualityInspectionDetail(props.id)

  res.inspectionTime = res.inspectionTime == null ? Date.now() : res.inspectionTime

  let obj = {
    ...res
  }
  obj.inspectorId = obj.inspectorId == null ? useUserStore().getUser.id : obj.inspectorId // 当前登录的编号
  obj.inspectorName =
    obj.inspectorName == null ? useUserStore().getUser.nickname : obj.inspectorName

  obj.purchaseUserName = obj.purchaseUser?.nickname
  obj.salesName = obj.sales?.nickname
  Object.assign(formData, obj)

  formData.children.forEach((e) => {
    e.totalPriceAmount = e.totalPrice.amount
    e.priceAmount = e.amount?.amount
  })

  tableList.value = formData?.children
  formData.amount = {
    amount: obj.amount?.amount ? obj.amount.amount : 0,
    currency: obj.amount?.currency ? obj.amount.currency : 'RMB'
  }
  formData.allocationType = formData.allocationType == null ? 1 : formData.allocationType

  // let amountItem = purchaseSchemas.find((item) => item.field === 'amount')
})
</script>

<style lang="scss" scoped>
.must-resultAnnex {
  color: #d00;
  margin: 0 5px;
}
</style>
