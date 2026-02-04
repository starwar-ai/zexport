<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'oa:repay-app:create',
      handler: () => saveForm()
    }"
    :submitAction="{
      permi: 'oa:repay-app:submit',
      handler: () => saveForm('submit')
    }"
    :closeAction="{
      handler: close
    }"
  >
    <eplus-form-items
      title="借款信息"
      :formSchemas="loanSchemas"
    >
      <template #loanAppId>
        <!-- <eplus-custom-select
          v-model="formData.loanAppId"
          :api="repayapp.getLoanSimpleList"
          :params="{ applyerId: useUserStore().getUser.id }"
          label="code"
          value="id"
          placeholder="请选择"
          @change="loanAppIdChange"
        /> -->
        <LoanList
          v-if="!props.row"
          @change="loanAppIdChange"
          :formData="formData"
        />
        <el-row style="width: 100%">
          <el-col :span="12">借款单号: {{ loadAppInfo.code }} </el-col>
          <el-col :span="12">公司主体: {{ loadAppInfo.companyName }} </el-col>
          <el-col :span="12">借款金额: {{ loadAppInfo.amount }} </el-col>
          <el-col :span="12">待还金额: {{ loadAppInfo.outstandingAmount }} </el-col>
          <el-col :span="12">已还金额: {{ loadAppInfo.repayAmount }} </el-col>
          <el-col :span="12">还款中: {{ loadAppInfo.inRepaymentAmount }} </el-col>
        </el-row>
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="还款信息"
      :formSchemas="repaySchemas"
    >
      <template #amount>
        <EplusMoney
          class="!w-70%"
          v-model:amount="formData.amount.amount"
          v-model:currency="formData.amount.currency"
          :currencyDisabled="true"
        />
        <el-button
          type="primary"
          class="ml15px"
          :disabled="loadAppInfo.maxVal === 0"
          @click="formData.amount.amount = loadAppInfo.maxVal"
          >全部归还</el-button
        >
      </template>
      <template #annex>
        <UploadList
          v-model="formData.annex"
          :fileList="formData.annex"
          @success="
            (data) => {
              formData.annex = data
            }
          "
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import { DICT_TYPE } from '@/utils/dict'
import { EplusMoney } from '@/components/EplusMoney'
import * as repayapp from '@/api/oa/repayapp'
import LoanList from './components/LoanList.vue'

const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'RepayAppForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
  row?: object
}>()

const loading = ref(false)

let formData: EplusAuditable = reactive({
  auditStatus: 0,
  loanAppId: '',
  amount: {
    amount: '',
    currency: ''
  }
})

const loadAppInfo = reactive({
  code: '',
  amount: '0',
  outstandingAmount: '0',
  transferAmount: '0',
  inRepaymentAmount: '0',
  companyName: '',
  maxVal: 0
})
provide('formData', formData)

const loanAppIdChange = (data, type? = 1) => {
  loadAppInfo.companyName = data.companyName
  if (type == 2) {
    formData.loanAppId = data.loanAppId
    loadAppInfo.code = data.loanAppCode
    loadAppInfo.amount = data.loanAmount
      ? `${data.loanAmount.amount} ${data.loanAmount.currency}`
      : '0'
  } else {
    loadAppInfo.code = data.code
    formData.loanAppId = data.id
    formData.amount.currency = data?.amount?.currency
    loadAppInfo.amount = data.amount ? `${data.amount.amount} ${data.amount.currency}` : '0'
  }

  loadAppInfo.outstandingAmount = data.outstandingAmount
    ? `${data.outstandingAmount.amount} ${data.outstandingAmount.currency}`
    : '0'
  loadAppInfo.repayAmount = data.repayAmount
    ? `${data.repayAmount.amount} ${data.repayAmount.currency}`
    : '0'

  loadAppInfo.inRepaymentAmount = data.inRepaymentAmount
    ? `${data.inRepaymentAmount.amount} ${data.inRepaymentAmount.currency}`
    : '0'

  let max = Number(data.outstandingAmount.amount)
  loadAppInfo.maxVal = max <= 0 ? 0 : max
}

const { close } = inject('dialogEmits') as {
  close: () => void
}
const loanSchemas: EplusFormSchema[] = reactive([
  {
    field: 'loanAppId',
    label: '借款单',
    span: 13,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (formData.loanAppId === '') {
          callback(new Error('请选择借款单号'))
        } else {
          callback()
        }
      }
    }
  }
])
const repaySchemas: EplusFormSchema[] = reactive([
  {
    field: 'amount',
    label: '还款金额',
    span: 12,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        let regx = /^(\d{1,11})(\.?\d{1,3})?$/
        if (formData.amount.amount === '') {
          callback(new Error('请输入借款金额'))
        } else if (!regx.test(formData.amount.amount)) {
          callback(new Error('金额只能输入正数,最多11位整数,3位小数'))
        } else if (formData.amount.amount <= 0) {
          callback(new Error(`还款金额必须大于0`))
        } else if (Number(formData.amount.amount) > Number(loadAppInfo.maxVal)) {
          callback(new Error(`还款金额不能超过${loadAppInfo.maxVal}`))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'repayType',
    label: '还款方式',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.LOAN_TYPE}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择还款方式'
    }
  },
  {
    field: 'annex',
    label: '附件',
    span: 24,
    rules: {
      required: true,
      message: '请上传附件'
    }
  }
])

const emit = defineEmits(['handleSuccess'])
const formRef = ref()
const saveForm = async (type?: string) => {
  let valid = await formRef.value.validate()
  if (!valid) return
  loading.value = true
  try {
    const params = {
      ...formRef.value.formData,
      submitFlag: type === 'submit' ? 1 : 0
    }
    if (props.mode == 'create') {
      await repayapp.createRepayApp(params)
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess', 'success')
    } else {
      await repayapp.updateRepayApp(params)
      message.success(t('common.updateSuccess'))
      close()
      emit('handleSuccess', 'success')
    }
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  formRef.value.resetForm()

  if (props.mode === 'edit') {
    let res = await repayapp.getRepayApp({ id: props.id })
    Object.assign(formData, res)
    loanAppIdChange(formData, 2)
  } else {
    if (props.row) {
      const res = await repayapp.getLoanSimpleList({
        applyerId: props.row?.applyer?.userId,
        auditStatus: 2,
        code: props.row?.code
      })
      loanAppIdChange(res.list[0])
    }
  }
})
</script>
