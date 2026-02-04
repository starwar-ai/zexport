<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'oa:general-reimb:create',
      handler: saveForm
    }"
    :submitAction="{
      textContent: props.mode === 'edit' ? '提交' : '下一步',
      permi: 'oa:general-reimb:submit',
      handler: submitForm
    }"
    :closeAction="{
      handler: close
    }"
  >
    <eplus-form-items
      title="报销说明"
      :formSchemas="explainSchemas"
    >
      <template #totalAmountList>
        <EplusMoney
          v-model:amount="formData.totalAmountList[0].amount"
          v-model:currency="formData.totalAmountList[0].currency"
          :currencyDisabled="true"
        />
      </template>

      <template #applyList>
        <ApplyListCom
          :formData="formData"
          ref="ApplyListComRef"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="费用小项"
      :formSchemas="expenseSchemas"
    >
      <template #ExpenseList>
        <ExpenseList
          ref="ExpenseListRef"
          :formData="formData"
        />
      </template>
      <template #LoanAppList>
        <LoanAppListCom
          ref="LoanAppListComRef"
          :formData="formData"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="费用明细"
      :formSchemas="feeDetailSchemas"
    >
      <template #totalAmountList>
        <EplusMoneyLabel :val="formData.totalAmountList" />
      </template>
      <template #invoiceAmountList>
        <EplusMoneyLabel :val="formData.invoiceAmountList" />
      </template>
      <template #repayAmountList>
        <EplusMoneyLabel :val="formData.repayAmountList" />
      </template>
      <template #amountList>
        <EplusMoneyLabel :val="formData.amountList" />
      </template>
    </eplus-form-items>
    <FeeShareFormCom
      ref="FeeShareFormComRef"
      v-if="formData.auxiliaryType === 1"
      :info="formData.feeShare"
      :feeShareAmount="formData.amountList[0]"
    />
  </eplus-form>

  <GeneralExpenseFeeShareForm
    ref="GeneralExpenseFeeShareFormRef"
    @submit="handleSubmit"
  />
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { formatterPrice, maskBankAccount } from '@/utils/index'
import GeneralExpenseFeeShareForm from './components/GeneralExpenseFeeShareForm.vue'
import ExpenseList from './components/ExpenseList.vue'
import { useUserStore } from '@/store/modules/user'
import * as GeneralApi from '@/api/oa/generalExpense'
import { cloneDeep } from 'lodash-es'
import { EplusMoney } from '@/components/EplusMoney'
import * as TravelReimbApi from '@/api/oa/travelreimb'
import FeeShareFormCom from '@/views/ems/send/components/FeeShare/FeeShareForm.vue'
import { useFeeStore } from '@/store/modules/fee'
import { getConfigKey } from '@/api/common'
import LoanAppListCom from '@/views/oa/components/LoanAppListCom.vue'
import ApplyListCom from './components/ApplyListCom.vue'
import { batchGetFeeShare } from '@/api/oa/entertain'
import { moneyTotalPrecision } from '@/utils/config'
import { isValidArray } from '@/utils/is'

const feeList = useFeeStore().feeList
const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'GeneralAppForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
  homeCard?: Object
  params?: any
}>()
const loading = ref(false)
const formData = reactive({
  description: '',
  reimbCode: '',
  companyId: '',
  totalAmountList: [] as { currency: string; amount: number | string }[],
  repayAmountList: [] as { currency: string; amount: number | string }[],
  amountList: [] as { currency: string; amount: number | string }[],
  invoiceAmountList: [] as { currency: string; amount: number | string }[],
  actualUserId: undefined,
  deptName: '',
  bank: '',
  bankAccount: '',
  bankAddress: '',
  bankPoc: '',
  bankCode: '',
  reimbDetailList: [],
  repayFlag: 0
})
const reimbId = ref('')
provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}
const explainSchemas: EplusFormSchema[] = reactive([
  {
    field: 'companyId',
    label: '费用主体',
    component: (
      <eplus-custom-select
        api={GeneralApi.getcompanySimpleList}
        label="name"
        value="id"
        placeholder="请选择费用主体"
        style="width:100%"
      />
    ),
    rules: [
      {
        required: true,
        message: '请选择费用主体'
      }
    ]
  },
  {
    field: 'currency',
    label: '报销币种',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.CURRENCY_TYPE}
        class="!w-100%"
        defaultValue="RMB"
        clearable={false}
      />
    ),
    rules: [
      {
        required: true,
        message: '请选择报销币种'
      }
    ]
  },
  {
    field: 'actualUserId',
    label: '实际报销人',
    component: <eplus-user-select />,
    rules: {
      required: true,
      message: '请选择实际报销人'
    }
  },
  {
    field: 'bankPoc',
    label: '开户行联系人',
    readOnly: true
  },
  {
    field: 'bank',
    label: '开户银行',
    readOnly: true
  },
  {
    field: 'bankAccount',
    label: '银行帐号',
    readOnly: true,
    formatter: (val) => {
      return val ? maskBankAccount(val) : '-'
    }
  },
  {
    field: 'description',
    label: '备注',
    span: 8,
    component: <el-input placeholder="请输入备注" />
  },
  {
    field: 'applyList',
    label: '关联申请单',
    span: 24,
    disabled: true,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (ApplyListComRef.value.tableData.length === 0) {
          callback(new Error('请选择申请单'))
        } else {
          callback()
        }
      }
    }
  }
])

const expenseSchemas: EplusFormSchema[] = reactive([
  {
    field: 'ExpenseList',
    label: '',
    labelWidth: '30px',
    span: 24
  },
  {
    field: 'repayFlag',
    label: '是否还款',
    disabled: true,
    span: 24,
    component: <EplusDictRadio dictType={DICT_TYPE.IS_INT} />,
    rules: [
      {
        required: true
      }
    ]
  },
  {
    field: 'LoanAppList',
    label: '借款单',
    disabled: true,
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
])

const feeDetailSchemas = reactive([
  {
    field: 'amountList',
    label: '报销总金额'
  },
  {
    field: 'invoiceAmountList',
    label: '发票总金额'
  },
  {
    field: 'repayAmountList',
    label: '本次还款金额'
  },
  {
    field: 'totalAmountList',
    label: '本次报销金额'
  }
])

const getBankInfo = async (userId) => {
  const targetUserId = userId || useUserStore().getUser.id
  let res = await TravelReimbApi.bankaccountInfo(targetUserId)
  formData.bank = res.bank
  formData.bankAccount = res.bankAccount
  formData.bankAddress = res.bankAddress
  formData.bankPoc = res.bankPoc
  formData.bankCode = res.bankCode
}

watch(
  () => formData.actualUserId,
  async (newUserId) => {
    if (newUserId) {
      await getBankInfo(newUserId)
    }
  },
  { immediate: false }
)

watch(
  () => formData.companyId,
  (val) => {
    let item = expenseSchemas.find((el) => el.field === 'repayFlag')
    if (item) {
      item.disabled = !val
    }
  },
  {
    immediate: true
  }
)
watch(
  () => formData.repayFlag,
  (val) => {
    let item = expenseSchemas.find((el) => el.field === 'LoanAppList')
    if (item) {
      item.disabled = !(val == 1)
    }
  },
  { immediate: true }
)

const emit = defineEmits(['handleSuccess'])
const formRef = ref()
provide('formRef', formRef)
const ExpenseListRef = ref()
const LoanAppListComRef = ref()
watch(
  () => [
    ExpenseListRef.value?.AmountList,
    ExpenseListRef.value?.invoiceAmountList,
    LoanAppListComRef.value?.loanTotalList
  ],
  ([list, list2, loanTotalList]) => {
    formData.amountList = list || []
    formData.invoiceAmountList = list2 || []
    formData.repayAmountList = loanTotalList || []

    if (isValidArray(formData.amountList) && isValidArray(formData.repayAmountList)) {
      let val = Number(formData.amountList[0]?.amount) - Number(formData.repayAmountList[0]?.amount)
      formData.totalAmountList = [
        {
          amount: formatterPrice(val > 0 ? val : 0, moneyTotalPrecision),
          currency: formData.amountList[0]?.currency
        }
      ]
    } else if (formData.amountList) {
      formData.totalAmountList = formData.amountList
    }
  },
  { immediate: true, deep: true }
)

const ApplyListComRef = ref()
const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params: any = cloneDeep(formData)

  if (params.repayFlag == 1 && LoanAppListComRef.value) {
    if (LoanAppListComRef.value.selectedDiaData.length > 0) {
      params.loanAppList = LoanAppListComRef.value.selectedDiaData
      if (formData.amountList[0]?.amount < LoanAppListComRef.value?.loanTotalList[0]?.amount) {
        message.warning('本次报销金额须大于等于选中借款单待还金额')
        return false
      }
    } else {
      message.warning('缺少借款单信息')
      return false
    }
  } else {
    params.loanAppList = []
  }

  if (ExpenseListRef.value) {
    let ExpenseListResult = await ExpenseListRef.value?.saveForm()
    if (ExpenseListResult) {
      params.reimbDetailList = ExpenseListResult
    } else {
      params.reimbDetailList = []
      return false
    }
  }

  let applyIdList = ApplyListComRef.value?.checkData()
  if (applyIdList) {
    params.applyIdList = applyIdList
  } else {
    return false
  }

  return params
}
const FeeShareFormComRef = ref()
const saveForm = async () => {
  try {
    loading.value = true
    let paramsResult = await getParams()
    if (!paramsResult) return false
    if (FeeShareFormComRef.value && paramsResult.auxiliaryType === 1) {
      let feeShareParams = await FeeShareFormComRef.value.getParams()
      if (feeShareParams) {
        paramsResult.auxiliaryType = 1
        paramsResult.feeShare = feeShareParams
      } else {
        return false
      }
    }
    if (props.mode == 'create') {
      await GeneralApi.createGeneralReimb({ reimbType: 2, ...paramsResult, submitFlag: 0 })
      message.success(t('common.createSuccess'))
      close()
    } else {
      await GeneralApi.updateGeneralReimb({ reimbType: 2, ...paramsResult, submitFlag: 0 })
      message.success(t('common.updateSuccess'))
      close()
    }
  } finally {
    loading.value = false
  }
}
const GeneralExpenseFeeShareFormRef = ref()
const submitForm = async () => {
  try {
    loading.value = true
    let paramsResult = await getParams()

    if (!paramsResult) return false

    if (paramsResult.auxiliaryType === 1) {
      if (FeeShareFormComRef.value) {
        let feeShareParams = await FeeShareFormComRef.value.getParams()
        if (feeShareParams) {
          paramsResult.auxiliaryType = 1
          paramsResult.feeShare = feeShareParams
        } else {
          return false
        }
      }
      if (props.mode == 'create') {
        await GeneralApi.createGeneralReimb({ reimbType: 2, ...paramsResult, submitFlag: 1 })
        message.success('提交成功')
        close()
      } else {
        await GeneralApi.updateGeneralReimb({ reimbType: 2, ...paramsResult, submitFlag: 1 })
        message.success('提交成功')
        close()
      }
    } else {
      const list = await batchGetFeeShare({
        codes: ApplyListComRef.value?.tableData.map((item) => item.code).join(','),
        type: 2,
        isPre: 1
      })
      //调用费用归集
      GeneralExpenseFeeShareFormRef.value.handleCreate(list, formData.amountList[0])
    }
  } finally {
    loading.value = false
  }
}

const handleSubmit = async (data) => {
  try {
    loading.value = true
    let paramsResult = await getParams()

    if (!paramsResult) return false
    if (data) {
      paramsResult.auxiliaryType = 1
      paramsResult.feeShare = data
    } else {
      paramsResult.auxiliaryType = 0
      paramsResult.feeShare = null
    }
    if (props.mode == 'create') {
      await GeneralApi.createGeneralReimb({ reimbType: 2, ...paramsResult, submitFlag: 1 })
      message.success('提交成功')
      close()
    } else {
      await GeneralApi.updateGeneralReimb({ reimbType: 2, ...paramsResult, submitFlag: 1 })
      message.success('提交成功')
      close()
    }
  } finally {
    loading.value = false
  }
}
const getApplyFlag = async () => {
  const flag = await getConfigKey('oa.other.apply')
  explainSchemas[explainSchemas.length - 1].disabled = !Number(flag)
}

onMounted(async () => {
  await getApplyFlag()
  if (props.mode === 'edit') {
    let res = await GeneralApi.getGeneralReimb(props.id)
    ApplyListComRef.value?.init(res.applyList)
    res.actualUserId = res.actualUser?.userId
    if (res.auxiliaryList && res.auxiliaryList?.length) {
      res.auxiliaryList.map((item) => {
        return (reimbId.value = item?.reimbId || '')
      })
    }
    Object.assign(formData, res)
  } else {
    formData.actualUserId = useUserStore().user.id
    if (props.homeCard) {
      formData.totalAmountList[0].amount = props.homeCard?.reimbAmount
      formData.invoiceHolderId = props.homeCard?.id
    }
    if (props.params) {
      formData.companyId = props.params.companyId
      formData.reimbList = props.params.reimbList
      formData.currency = props.params.amount.currency
      formData.actualUserId = props.params.actualUser.userId
      ApplyListComRef.value?.init(props.params.reimbList)
    }
  }
})
</script>
