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
          placeholder="Select"
          style="width: 100%"
          @change="handleChange"
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
          :currencyDisabled="true"
        />
      </template>
      <template #sendTime>
        <el-date-picker
          v-model="formData.sendTime"
          clearable
          valueFormat="x"
          type="date"
          style="width: 100%"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
  <el-tabs v-model="activeName">
    <el-tab-pane
      label="寄件明细"
      name="1"
    >
      <InspectInfoList :info="formData.children" />
    </el-tab-pane>
  </el-tabs>
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { DICT_TYPE } from '@/utils/dict'
import * as EmsListApi from '@/api/ems/emsList' // 寄件接口集合
import InspectInfoList from './components/InspectInfoList.vue'

defineOptions({ name: 'SendUpdateNumber' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()

const { updateDialogActions, clearDialogActions } = props.id
  ? (inject('dialogActions') as {
      updateDialogActions: (...args: any[]) => void
      clearDialogActions: () => void
    })
  : { updateDialogActions: () => {}, clearDialogActions: () => {} }
const message = useMessage()

const queryId: string = (props.id || '') as string
const loading = ref(false)
const simpleEmsList = ref([])

const activeName = ref('1')
const getDefaultSendTime = () => Date.now()
const formData = reactive({
  id: undefined,
  venderId: undefined,
  estCost: {
    amount: 0,
    currency: 'RMB'
  },
  remark: '',
  belongFlag: 0,

  number: '',
  sendTime: getDefaultSendTime()
})
provide('formData', formData)
const { close } = inject('dialogEmits') as {
  close: () => void
}

// 信息
let purchaseSchemas: EplusFormSchema[] = reactive([
  {
    field: 'inputUserName',
    label: '录入人',
    editable: true,
    component: <el-input disabled />,

    span: 6
  },
  {
    field: 'actualUserName',
    label: '实际寄件人',
    editable: true,
    component: <el-input disabled />,
    span: 6
  },
  {
    field: 'sendRegion',
    label: '寄件区域',
    editable: true,
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.SEND_REGION}
        style="width:100%"
        disabled
      />
    ),
    span: 6
  },
  {
    field: 'goodsType',
    label: '物件类型',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.SEND_PRODUCT_TYPE}
        style="width:100%"
        disabled
      />
    ),
    span: 6
  },
  {
    field: 'goodsSource',
    label: '物件来源',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.GOODS_SOURCE}
        style="width:100%"
        disabled
      />
    ),
    span: 6
  },
  {
    field: 'receiveMsg',
    label: '收件信息',
    editable: true,
    component: <el-input disabled />,
    span: 12
  },
  // {
  //   field: 'receivePhone',
  //   label: '收件电话',
  //   editable: true,
  //   component: <el-input disabled />,
  //   span: 6
  // },
  // {
  //   field: 'receiveAddress',
  //   label: '收件地址',
  //   editable: true,
  //   component: <el-input disabled />,
  //   span: 6
  // },
  {
    field: 'venderId',
    label: '快递公司',
    span: 6,
    rules: {
      required: true,
      message: '请选择快递公司'
    }
  },
  {
    field: 'payType',
    label: '付款方式',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.SEND_PAY_TYPE}
        style="width:100%"
        disabled
      />
    ),
    span: 6
  },
  {
    field: 'estCost',
    label: '预估费用',
    span: 6
  },
  {
    field: 'number',
    label: '快递单号',
    editable: true,
    component: <el-input />,
    span: 6,
    rules: {
      trigger: 'blur',
      required: true,
      message: '请输入快递单号'
    }
  },
  {
    field: 'sendTime',
    label: '寄件日期',
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
        disabled
      />
    ),
    span: 6
  }
])

const handleChange = (val) => {
  let item = simpleEmsList.value.find((item) => item.id === val)
  formData.venderId = item.id
  formData.venderName = item.name
  formData.venderShortName = item.nameShort
}

const emit = defineEmits(['handleSuccess', 'sucess'])
const formRef = ref()

const submit = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let data = {
    number: formData.number,
    id: formData.id,
    venderId: formData.venderId
  }
  await EmsListApi.uploadNumber(data)
  message.success('提交成功')
  close()
}

onMounted(async () => {
  let emsListRes = await EmsListApi.getSimpleList()
  simpleEmsList.value = emsListRes.list
  let res = await EmsListApi.getEmSDetail(props.id)
  let obj = {
    ...res,
    actualUserName: res.actualUser?.nickname
  }
  Object.assign(formData, obj)
  formData.sendTime = res?.sendTime ?? getDefaultSendTime()
  updateDialogActions(
    <el-button
      onClick={() => {
        submit()
      }}
      key="uploadNumber"
      hasPermi="ems:send:upload-number"
      isShow={formData.sendStatus == 2 ? true : false}
    >
      提交
    </el-button>
  )
})
</script>
