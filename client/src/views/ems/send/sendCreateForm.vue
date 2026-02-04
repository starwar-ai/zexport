<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'ems:send:create',
      handler: () => submitForm('')
    }"
    :submitAction="{
      permi: 'ems:send:submit',
      handler: () => submitForm('submit')
    }"
    :closeAction="{
      // handler: close
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
          :defaultObj="{
            name: formData.receiveName || undefined,
            code: formData.receiveCode || undefined
          }"
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
          :defaultObj="{
            name: formData.receiveName || undefined,
            code: formData.receiveCode || undefined
          }"
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
  <eplus-dialog
    ref="FeeShareDialogRef"
    @success="handleRefresh"
    @failure="handleRefresh"
  >
    <template #edit="{ key }">
      <FeeShareForm
        channel="“send"
        :id="key"
        mode="edit"
      />
    </template>
    <template #create="{ key }">
      <FeeShareForm
        :getApi="EmsListApi.getEmSDetail"
        :updateApi="EmsListApi.updateFeeShare"
        channel="send"
        :id="key"
        @save="save"
        mode="create"
      />
    </template>
  </eplus-dialog>
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { DICT_TYPE } from '@/utils/dict'
import PurchaseProducts from './components/PurchaseProducts.vue'
import { getCustSimpleList, getVenderSimpleList } from '@/api/common'
import * as UserApi from '@/api/system/user'
import * as EmsListApi from '@/api/ems/emsList'
import { useUserStore } from '@/store/modules/user'
import FeeShareForm from './components/FeeShare/FeeShareForm.vue'

const dialogVisible = ref(false)
const message = useMessage()
defineOptions({ name: 'PlanForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const queryId: string = (props.id || '') as string
const loading = ref(false)
const simpleUserList = ref([])
const simpleEmsList = ref([])
const custCodeRef = ref()

let formData = reactive({
  inputUserId: useUserStore().getUser.id,
  inputUserName: useUserStore().getUser.nickname,
  actualUserId: useUserStore().getUser.id,
  estCost: {
    amount: 0,
    currency: 'RMB'
  },
  remark: '',
  belongFlag: 0,
  venderName: '',
  goodsType: 2,
  sendRegion: 1,
  payType: 1,
  receiveType: 1,
  receiveCode: '',
  receiveName: '',
  receiveMsg: ''
})
provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}
const childrenRef = ref()
const addProductFlag = ref(true)

const onChangeType = (val) => {
  addProductFlag.value = val == 1 ? false : true
}

const handleExpressChange = (val) => {
  let item = simpleEmsList.value.find((item) => item.id == val)
  formData.venderName = item.name
  formData.venderCode = item.code
  formData.venderShortName = item.nameShort
}

let purchaseSchemas: EplusFormSchema[] = reactive([
  {
    field: 'inputUserName',
    label: '录入人',
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
    // component: (
    //   <eplus-dict-select
    //     dictType={DICT_TYPE.SEND_PRODUCT_TYPE}
    //     style="width:100%"
    //   />
    // ),
    rules: {
      trigger: 'blur',
      required: true,
      message: '请选择任务类型'
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
    label: '预估费用',
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value.amount) {
          callback(new Error(`请输入预估费用`))
        } else {
          callback()
        }
      }
    }
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
    editable: true,
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
      message: '请填写收件信息'
    }
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

const getReceiveMsg = (val, list) => {
  let row = list.find((item) => item.code === val)
  formData.receiveName = row.name || ''
  if (formData.receiveType === 2) {
    let item = row?.pocList.find((el) => el.defaultFlag == 1)
    formData.receiveMsg = `${item.name} ${item.mobile} ${row.deliveryAreaName}${row.deliveryAddress}`
  } else if (formData.receiveType === 1) {
    let item = row?.custPocList.find((el) => el.defaultFlag == 1)
    formData.receiveMsg = `${item.name} ${item.mobile} ${item.address}`
  }
}

const emit = defineEmits(['handleSuccess', 'sucess'])
const formRef = ref()

//处理提交前的formdata
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

const save = async (data) => {
  formData.feeShare = data == false ? null : data
  await EmsListApi.createSend(formData)
    .then(() => {
      message.success('提交成功')
      close()
      emit('handleSuccess', 'success')
    })
    .catch(() => {
      message.error('提交失败')
    })
}

const submitForm = async (type?) => {
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

    // if (type == 'submit') {
    //   handleCreateFeeShare()
    // } else {
    await EmsListApi.createSend(formData)
      .then(() => {
        message.success('提交成功')
        close()
        emit('handleSuccess', 'success')
      })
      .catch(() => {
        message.error('提交失败')
      })
    // }
  } finally {
    loading.value = false
  }
}

const FeeShareDialogRef = ref()

const handleCreateFeeShare = (id: number) => {
  FeeShareDialogRef.value?.openCreate('费用归集', id)
}

onMounted(async () => {
  simpleUserList.value = await UserApi.getSimpleUserList()
  let emsListRes = await EmsListApi.getSimpleList()
  simpleEmsList.value = emsListRes?.list
})
</script>
