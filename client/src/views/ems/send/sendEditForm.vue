<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :closeAction="{
      handler: close
    }"
  >
    <eplus-form-items
      title="基本信息"
      :formSchemas="purchaseSchemas"
    >
      <template #venderId>
        <el-select
          v-model="formData.venderId"
          placeholder="请选择快递公司"
          style="width: 100%"
          @change="handleExpressChange"
        >
          <el-option
            v-for="item in simpleEmsList"
            :key="item.id"
            :label="item.nameShort"
            :value="item.id"
          />
        </el-select>
      </template>
      <template #estCost>
        <EplusMoney
          v-model:amount="formData.estCost.amount"
          v-model:currency="formData.estCost.currency"
        />
      </template>
      <template #cost>
        <EplusMoney
          v-model:amount="formData.cost.amount"
          v-model:currency="formData.cost.currency"
        />
      </template>
      <template #goodsType>
        <eplus-dict-select
          v-model="formData.goodsType"
          :dictType="DICT_TYPE.SEND_PRODUCT_TYPE"
          @change="(val) => onChangeType(val)"
          style="width: 100%"
        />
      </template>
      <template #receiveCode>
        <eplus-input-search-select
          v-model="formData.receiveCode"
          v-if="formData.receiveType === 1"
          :api="getCustSimpleList"
          :params="{ pageSize: 100, pageNo: 1, stageTypeFlag: 1 }"
          keyword="custName"
          label="name"
          value="code"
          class="!w-100%"
          placeholder="请选择"
          @change-emit="getReceiveMsg"
        />
        <eplus-input-search-select
          v-else-if="formData.receiveType === 2"
          v-model="formData.receiveCode"
          :api="getVenderSimpleList"
          :params="{ pageSize: 100, pageNo: 1, stageTypeFlag: 1 }"
          keyword="name"
          label="name"
          value="code"
          class="!w-100%"
          placeholder="请选择"
          @change-emit="getReceiveMsg"
        />
        <el-input
          v-else
          v-model="formData.receiveName"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="寄件明细"
      :formSchemas="collectionSchemas"
      v-if="addProductFlag"
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
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { DICT_TYPE } from '@/utils/dict'
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import * as EmsListApi from '@/api/ems/emsList' // 寄件接口集合
import PurchaseProducts from './components/PurchaseProducts.vue'
import { getCustSimpleList, getVenderSimpleList } from '@/api/common'

const message = useMessage()
defineOptions({ name: 'ConfirmInspection' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()

const loading = ref(true)
const simpleEmsList = ref([])

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }

const formData = reactive({
  // inputUserId: useUserStore().getUser.id,
  // inputUserName: useUserStore().getUser.nickname,
  estCost: {
    amount: 0,
    currency: 'RMB'
  },
  cost: {
    amount: 0,
    currency: 'RMB'
  },
  remark: '',
  belongFlag: 0,
  venderName: ''
})
provide('formData', formData)
const childrenRef = ref()
const { close } = inject('dialogEmits') as {
  close: () => void
}
const addProductFlag = ref(true)
const onChangeType = (val) => {
  addProductFlag.value = val == 1 ? false : true
}

const quoteitemListInfo: EplusDescriptionItemSchema[] = [
  {
    slotName: 'quoteitemList',
    field: 'quoteitemList',
    label: '',
    span: 24
  }
]

// 信息
let purchaseSchemas: EplusFormSchema[] = reactive([
  {
    field: 'inputUserName',
    label: '录入人',
    editable: true,
    component: <el-input disabled />
  },
  {
    field: 'actualUserId',
    label: '实际寄件人',
    component: <eplus-user-select />,
    rules: {
      required: true,
      message: '请选择实际寄件人'
    }
  },
  {
    field: 'sendRegion',
    label: '寄件区域',
    editable: true,
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.SEND_REGION}
        style="width:100%"
      />
    ),
    rules: [
      {
        required: true,
        message: '请选择寄件区域'
      }
    ]
  },
  {
    field: 'goodsType',
    label: '物件类型',
    rules: {
      trigger: 'blur',
      required: true,
      message: '请选择物件类型'
    }
  },
  {
    field: 'payType',
    label: '付款方式',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.SEND_PAY_TYPE}
        style="width:100%"
      />
    ),
    rules: {
      trigger: 'blur',
      required: true,
      message: '请选择付款方式'
    }
  },

  {
    field: 'venderId',
    label: '快递公司',
    rules: {
      trigger: 'blur',
      required: true,
      message: '请选择快递公司'
    }
  },
  {
    field: 'expressCode',
    label: '物流单号',
    component: <el-input />
  },
  {
    field: 'estCost',
    label: '预估金额',
    rules: {
      required: true,
      message: '请输入预估金额'
    }
  },
  {
    field: 'cost',
    label: '实际金额'
  },
  {
    field: 'sendTime',
    label: '寄件日期',
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
      message: '请选择寄件日期'
    }
  },
  {
    field: 'sendStatus',
    label: '单据状态',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.SEND_STATUS}
        style="width:100%"
        disabled
      />
    )
  },
  {
    field: 'payStatus',
    label: '付款状态',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.PAYMENT_STATUS}
        style="width:100%"
        disabled
      />
    )
  },
  {
    field: 'receiveType',
    label: '收件方类型',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.EMS_RECEIVE_TYPE}
        style="width:100%"
        onChange={() => {
          formData.receiveName = ''
          formData.receiveCode = ''
          formData.receiveMsg = ''
        }}
      />
    ),
    rules: {
      required: true,
      message: '请选择收件方类型'
    }
  },
  {
    field: 'receiveCode',
    label: '收件方',
    xl: 8,
    lg: 12,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (formData.receiveType === 3 && !formData.receiveName) {
          callback(new Error(`请输入收件方`))
        } else if (formData.receiveType < 3 && !formData.receiveCode) {
          callback(new Error(`请选择收件方`))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'receiveMsg',
    label: '收件信息',
    component: (
      <el-input
        placeholder="请填写收件人名字、手机号、地址"
        type="textarea"
        maxlength={1000}
        show-word-limit
      />
    ),
    span: 12,
    rules: {
      trigger: 'blur',
      required: true,
      message: '请选择快递公司'
    }
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
    span: 12
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

const handleExpressChange = (val) => {
  let item = simpleEmsList.value.find((item) => item.id === val)
  formData.venderId = item.id
  formData.venderName = item.name
  formData.venderCode = item.code
  formData.venderShortName = item.nameShort
}

const emit = defineEmits(['handleSuccess', 'sucess'])
const formRef = ref()

const getParams = async () => {
  let params = {}
  if (addProductFlag.value) {
    let result = await childrenRef.value.checkData()
    if (result) {
      params.children = JSON.parse(JSON.stringify(childrenRef.value.tableList))
      params.children.forEach((item, index) => {
        item.picture = item.mainPicture
      })
      return {
        ...params
      }
    } else {
      return false
    }
  }
}

const updateForm = async (type) => {
  try {
    let valid = await formRef.value.validate()
    if (!valid) return false
    let paramsResult = await getParams()
    if (addProductFlag.value && paramsResult == false) {
      return false
    }
    formData.children = addProductFlag.value ? childrenRef.value?.tableList : []
    loading.value = true

    formData.submitFlag = type == 'submit' ? 1 : 0
    await EmsListApi.updateEms({ ...formData })
    message.success('修改成功')
    close()
  } catch (error) {
    message.error('提交失败')
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    loading.value = true
    let emsListRes = await EmsListApi.getSimpleList()
    simpleEmsList.value = emsListRes.list
    let res = await EmsListApi.getEmSDetail(props.id)
    let obj = {
      ...res,
      actualUserId: res.actualUser?.userId
    }
    Object.assign(formData, obj)

    addProductFlag.value = formData.goodsType == 1 ? false : true
    childrenRef.value.tableList = formData?.children

    updateDialogActions(
      <el-button
        onClick={() => {
          updateForm('')
        }}
        key="sendUpdate"
        hasPermi="ems:send:update"
      >
        保存
      </el-button>,
      <el-button
        onClick={() => {
          updateForm('submit')
        }}
        key="sendSubmit"
        hasPermi="ems:send:submit"
      >
        提交
      </el-button>
    )
    await nextTick()
    setTimeout(() => {
      loading.value = false
    }, 100)
  } finally {
    // loading的隐藏已移到setTimeout中
  }
})
</script>
