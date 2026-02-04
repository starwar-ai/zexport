<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: ['oa:loanapp:save', 'oa:loan-app:update'],
      handler: saveForm
    }"
    :submitAction="{
      permi: ['oa:loanapp:save', 'oa:loan-app:update'],
      handler: submitForm
    }"
    :closeAction="{
      handler: close
    }"
  >
    <eplus-form-items
      title=""
      :formSchemas="basicSchemas"
    >
      <template #amount>
        <EplusMoney
          v-model:amount="formData.amount.amount"
          v-model:currency="formData.amount.currency"
          @currency-change="handleCurrencyChange"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
import { maskBankAccount } from '@/utils/index'
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import * as LoanAppApi from '@/api/oa/loanapp'
import { EplusMoney } from '@/components/EplusMoney'
import { useUserStore } from '@/store/modules/user'
import { useRateStore } from '@/store/modules/rate'

const rateList = useRateStore().rateList
const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'LoanAppForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const loading = ref(false)

const formData: EplusAuditable = reactive({
  amount: { amount: '', currency: 'RMB' },
  auditStatus: 0,
  exchangeRate: '1' // 添加汇率字段
})
provide('formData', formData)

// 汇率缓存
const rateLoading = ref(false) // 汇率加载状态

//获取汇率
const handleCurrencyChange = async (currency: string) => {
  if (currency) {
    formData.exchangeRate = rateList[currency]
  } else {
    formData.exchangeRate = ''
  }
}

watch(
  () => formData.loanType,
  (loanType) => {
    if ('转账' === getDictLabel(DICT_TYPE.LOAN_TYPE, loanType)) {
      basicSchemas[2].disabled = false
      basicSchemas[3].disabled = false
      basicSchemas[4].disabled = false
      getBankInfo()
    } else {
      basicSchemas[2].disabled = true
      basicSchemas[3].disabled = true
      basicSchemas[4].disabled = true
    }
  }
)

const getBankInfo = async () => {
  let res = await LoanAppApi.bankaccountInfo(useUserStore().getUser.id)
  formData.bank = res.bank
  formData.bankAccount = res.bankAccount
  formData.bankAccountVal = maskBankAccount(res.bankAccount)
  formData.bankPoc = res.bankPoc
}

const { close } = inject('dialogEmits') as {
  close: () => void
}
const basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'amount',
    label: '借款金额',
    span: 13,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        let regx = /^(\d{1,11})(\.?\d{1,3})?$/
        if (formData.amount.amount === '') {
          callback(new Error('请输入借款金额'))
        } else if (!regx.test(formData.amount.amount)) {
          callback(new Error('金额只能输入数字,最多11位整数,3位小数'))
        } else if (!formData.amount.currency) {
          callback(new Error('请选择币种'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'loanType',
    label: '借款方式',
    span: 13,
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.LOAN_TYPE}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请输入借款类型'
    }
  },
  {
    field: 'bank',
    label: '开户行',
    disabled: true,
    span: 13,
    component: (
      <el-input
        placeholder="-"
        disabled
      />
    )
  },
  {
    field: 'bankAccountVal',
    label: '银行账户',
    disabled: true,
    span: 13,
    component: (
      <el-input
        placeholder="-"
        disabled
      />
    )
  },
  {
    field: 'bankPoc',
    label: '账户名称',
    disabled: true,
    span: 13,
    component: (
      <el-input
        placeholder="-"
        disabled
      />
    )
  },
  {
    field: 'companyId',
    label: '公司主体',
    span: 13,
    component: (
      <eplus-custom-select
        api={LoanAppApi.getcompanySimpleList}
        label="name"
        value="id"
        placeholder="请选择"
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择公司主体'
    }
  },
  {
    field: 'purpose',
    label: '借款事由',
    editable: true,
    span: 13,
    component: <el-input type="textarea" />,
    rules: {
      required: true,
      message: '请输入借款事由'
    }
  },
  {
    field: 'exchangeRate',
    span: 13,
    label: '汇率',
    component: <el-input disabled />
  }
])
const emit = defineEmits(['handleSuccess'])
const formRef = ref()
const saveForm = async () => {
  // 校验表单
  if (formRef.value === null) return

  let valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  loading.value = true
  try {
    const data = formRef.value.formData as unknown as LoanAppApi.LoanAppVO
    const params = {
      ...data,
      submitFlag: 0
    }
    if (props.mode == 'create') {
      await LoanAppApi.createLoanApp(params)
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess', 'success')
    } else {
      await LoanAppApi.updateLoanApp(params)
      message.success(t('common.updateSuccess'))
      close()
      emit('handleSuccess', 'success')
    }
  } finally {
    loading.value = false
  }
}

const submitForm = async () => {
  // 校验表单
  if (formRef.value === null) return

  let valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  loading.value = true
  const data = formRef.value.formData as unknown as LoanAppApi.LoanAppVO
  const params = {
    ...data,
    submitFlag: 1
  }
  try {
    //判断是否存在id，不存在时 新建，存在时更新
    if (data.id == undefined) {
      data.id = await LoanAppApi.createLoanApp(params)
    } else {
      await LoanAppApi.updateLoanApp(params)
    }
    message.success(t('common.submitSuccess'))
    close()
    emit('handleSuccess', 'success')
  } catch (e) {
    loading.value = false
    return
  }
}

onMounted(async () => {
  formRef.value.resetForm()
  if (props.mode === 'edit') {
    let res = await LoanAppApi.getLoanApp({ id: props.id })
    Object.assign(formData, res)
  }
  if (formData.amount?.currency) {
    formData.exchangeRate = rateList[formData.amount.currency]
  }
})
</script>
