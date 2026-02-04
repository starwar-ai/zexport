<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'oa:entertain-reimb:create',
      handler: () => saveForm()
    }"
    :submitAction="{
      textContent: props.mode === 'edit' ? '提交' : '下一步',
      permi: 'oa:entertain-reimb:submit',
      handler: () => submitForm('submit')
    }"
  >
    <eplus-form-items
      title="报销说明"
      :formSchemas="explainSchemas"
    >
      <!-- <template #businessSubjectCode>
        <eplus-input-search-select
          v-model="formData.businessSubjectCode"
          api=""
          label="name"
          value="code"
          style="width: 100%"
        />
      </template> -->
      <template #reimbList>
        <ReimbListCom
          ref="ReimbListComRef"
          :formData="formData"
        />
      </template>
    </eplus-form-items>

    <eplus-form-items
      title="费用小项"
      :formSchemas="expenseSchemas"
    >
      <template #ExpenseList>
        <ExpenseList
          v-if="!tabLoading"
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
    <FeeShareOrderFormCom
      ref="FeeShareOrderFormComRef"
      v-if="formData.auxiliaryType == 1"
      :info="formData.feeShare"
      :feeShareAmount="formData.amountList[0]"
    />
  </eplus-form>
  <OrderFeeShare
    ref="OrderFeeShareRef"
    @submit="handleSubmit"
  />
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import { DICT_TYPE } from '@/utils/dict'
import { isValidArray } from '@/utils/is'
import { cloneDeep } from 'lodash-es'
import * as TravelReimbApi from '@/api/oa/travelreimb'
import * as EntertainApi from '@/api/oa/entertain'
import ExpenseList from './ExpenseList.vue'
import { useUserStore } from '@/store/modules/user'
import { formatterPrice, maskBankAccount } from '@/utils/index'
import OrderFeeShare from '@/views/oa/generalExpense/components/GeneralExpenseFeeShareForm.vue'
import FeeShareOrderFormCom from '@/views/ems/send/components/FeeShare/FeeShareForm.vue'
import LoanAppListCom from '@/views/oa/components/LoanAppListCom.vue'
import { getConfigKey } from '@/api/common'
import ReimbListCom from './ReimbListCom.vue'
import { moneyTotalPrecision } from '@/utils/config'

const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'EntertainmentExpensesForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
  homeCard?: Object
  params?: any
}>()
const loading = ref(false)
const tabLoading = ref(false)
const ReimbListComRef = ref()
const formData: EplusAuditable = reactive({
  auditStatus: 0,
  entertainTime: '',
  reimbType: 3,
  entertainType: undefined,
  entertainName: '',
  entertainLevel: 1,
  entertainNum: 1,
  entertainEntourageIds: [],
  entertainEntourage: [],
  reimbDetailList: [],
  totalAmountList: [] as { currency: string; amount: number | string }[],
  repayAmountList: [] as { currency: string; amount: number | string }[],
  amountList: [] as { currency: string; amount: number | string }[],
  invoiceAmountList: [] as { currency: string; amount: number | string }[],
  invoiceList: [],
  bank: '',
  bankAccount: '',
  bankAddress: '',
  bankPoc: '',
  bankCode: '',
  repayFlag: 0,
  actualUserId: undefined
})
provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}

const explainSchemas: EplusFormSchema[] = reactive([
  {
    field: 'companyId',
    label: '费用主体',
    editable: true,
    component: (
      <eplus-custom-select
        api={TravelReimbApi.getcompanySimpleList}
        label="name"
        value="id"
        class="!w-100%"
        placeholder="请选择"
      />
    ),
    rules: [
      {
        required: true,
        message: '请选择公司主体'
      }
    ]
  },
  {
    field: 'currency',
    label: '报销币种',
    editable: true,
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
    label: '账户名称',
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
      if (val) {
        return maskBankAccount(val)
      } else {
        return '-'
      }
    }
  },
  // {
  //   field: 'expenseUseToOccur',
  //   label: '费用实际用途说明',
  //   labelWidth: '150px',
  //   xl: 8,
  //   lg: 12,
  //   component: (
  //     <el-input
  //       type="testarea"
  //       placeholder="字数不小于10个字，标注关键信息，含客户/供应商/展会/项目等具体名称之一，或者费用产生的主要原因"
  //     />
  //   ),
  //   rules: [
  //     {
  //       required: false,
  //       message: '请输入费用实际用途说明'
  //     }
  //   ]
  // },
  {
    field: 'remark',
    label: '备注',
    component: <el-input placeholder="请输入备注" />
  },
  {
    field: 'reimbList',
    label: '关联申请单',
    span: 24,
    disabled: true,
    rules: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          if (ReimbListComRef.value.tableData.length === 0) {
            callback(new Error('请选择申请单'))
          } else {
            callback()
          }
        }
      }
    ]
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
  { immediate: true }
)

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

const FeeShareOrderFormComRef = ref()
const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params: any = cloneDeep(formData)
  if (params.repayFlag == 1 && LoanAppListComRef.value) {
    if (LoanAppListComRef.value.selectedDiaData.length > 0) {
      params.loanAppList = LoanAppListComRef.value.selectedDiaData
      if (formData.amountList[0]?.amount < LoanAppListComRef.value?.loanTotalList[0]?.amount) {
        message.warning('报销金额须大于等于选中借款单待还金额')
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
      return false
    }
  }
  let applyIdList = ReimbListComRef.value?.checkData()
  if (applyIdList) {
    params.applyIdList = applyIdList
  } else {
    return false
  }

  return params
}

const saveForm = async (type?: string) => {
  // 提交请求
  try {
    const params = await getParams()
    if (!params) return false
    if (FeeShareOrderFormComRef.value && params.auxiliaryType === 1) {
      let feeShareParams = await FeeShareOrderFormComRef.value.getParams()
      if (feeShareParams) {
        params.auxiliaryType = 1
        params.feeShare = feeShareParams
      } else {
        return false
      }
    }
    loading.value = true
    if (props.mode == 'create') {
      await EntertainApi.createEntertain(params)
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess', 'success')
    } else {
      await EntertainApi.updateEntertain(params)
      message.success(t('common.updateSuccess'))
      close()
      emit('handleSuccess', 'success')
    }
  } finally {
    loading.value = false
  }
}
const OrderFeeShareRef = ref()
const submitForm = async () => {
  // 提交请求
  try {
    const params = await getParams()
    if (!params) return false
    if (params.auxiliaryType === 1) {
      if (FeeShareOrderFormComRef.value) {
        let feeShareParams = await FeeShareOrderFormComRef.value.getParams()
        if (feeShareParams) {
          params.auxiliaryType = 1
          params.feeShare = feeShareParams
        } else {
          return false
        }
      }
      if (props.mode == 'create') {
        await EntertainApi.createEntertain({ ...params, submitFlag: 1 })
        message.success(t('common.createSuccess'))
        close()
        emit('handleSuccess', 'success')
      } else {
        await EntertainApi.updateEntertain({ ...params, submitFlag: 1 })
        message.success(t('common.updateSuccess'))
        close()
        emit('handleSuccess', 'success')
      }
    } else {
      //打开费用归集
      const list = await EntertainApi.batchGetFeeShare({
        codes: ReimbListComRef.value?.tableData.map((item) => item.code).join(','),
        type: 3,
        isPre: 1
      })

      OrderFeeShareRef.value.handleCreate(list, formData.amountList[0])
    }
  } finally {
    loading.value = false
  }
}

const handleSubmit = async (feeShareData) => {
  try {
    loading.value = true
    const requestParams = await getParams()
    if (!requestParams) return false
    const params = {
      ...requestParams,
      auxiliaryType: feeShareData ? 1 : 0,
      feeShare: feeShareData ? feeShareData : null,
      submitFlag: 1
    }
    if (props.mode == 'create') {
      await EntertainApi.createEntertain(params)
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess', 'success')
    } else {
      await EntertainApi.updateEntertain(params)
      message.success(t('common.updateSuccess'))
      close()
      emit('handleSuccess', 'success')
    }
  } finally {
    loading.value = false
  }
}

const getApplyFlag = async () => {
  const flag = await getConfigKey('oa.entertain.apply')
  explainSchemas[explainSchemas.length - 1].disabled = !Number(flag)
}

onMounted(async () => {
  //
  if (props.mode === 'edit') {
    await getApplyFlag()
    tabLoading.value = true
    let res = await EntertainApi.getEntertainInfo({ id: props.id })
    Object.assign(formData, { ...res, actualUserId: res.actualUser?.userId })
    // UploadRef.value.fileData(formData.invoiceList || [])
    tabLoading.value = false
    ReimbListComRef.value?.init(formData.applyList)
  } else {
    await getApplyFlag()
    getBankInfo()
    formData.actualUserId = useUserStore().user.id
    if (props.homeCard) {
      formData.invoiceList = props.homeCard?.invoice
      formData.reimbDetailList = [
        {
          expenseAmount: props.homeCard?.reimbAmount,
          invoiceAmount: props.homeCard?.invoiceAmount
        }
      ]
      formData.invoiceHolderId = props.homeCard?.id
    }
    if (props.params) {
      formData.companyId = props.params.companyId
      formData.companyName = props.params.companyName
      // formData.reimbList = props.params.reimbList
      ReimbListComRef.value?.init(props.params.reimbList)
    }
  }
})
</script>
