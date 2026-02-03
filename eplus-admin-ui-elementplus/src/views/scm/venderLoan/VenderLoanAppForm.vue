<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: ['scm:vender-loan:create', 'scm:vender-loan:update'],
      handler: () => saveForm(0)
    }"
    :submitAction="{
      permi: ['scm:vender-loan:create', 'scm:vender-loan:update'],
      handler: () => saveForm(1)
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
        />
      </template>
      <template #annex>
        <UploadList
          ref="UploadListRef"
          :fileList="formData.annex"
          @success="getFileList"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import * as VenderLoanApi from '@/api/scm/venderLoan'
import * as emsApi from '@/api/ems/emsList/index'
import { getVenderSimpleList } from '@/api/common'
import { EplusMoney } from '@/components/EplusMoney'
import UploadList from '@/components/UploadList/index.vue'

const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'LoanAppForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const loading = ref(false)
//附件信息
const getFileList = (params) => {
  formData.annex = params
}
const formData: EplusAuditable = reactive({
  amount: { amount: '', currency: 'RMB' },
  auditStatus: 0,
  annex: []
})
provide('formData', formData)

watch(
  () => formData.loanType,
  (loanType) => {
    if ('转账' === getDictLabel(DICT_TYPE.VENDER_LOAN_TYPE, loanType)) {
      basicSchemas[3].disabled = false
      basicSchemas[4].disabled = false
      basicSchemas[5].disabled = false
    } else {
      basicSchemas[5].disabled = true
      basicSchemas[3].disabled = true
      basicSchemas[4].disabled = true
    }
  }
)

const purchaseContractCodeFlag = computed(() => {
  return !(
    '退款' === getDictLabel(DICT_TYPE.VENDER_LOAN_TYPE, formData.loanType) && formData.venderCode
  )
})

const getBankList = async (val, list) => {
  let item = list.find((el) => el.code === val)
  formData.venderId = item.id
  formData.venderName = item.name
  if (item) {
    let res = item.bankAccountList.find((el) => el.defaultFlag === 1)
    formData.bank = res.bank
    formData.bankAccount = res.bankAccount
    formData.bankPoc = res.bankPoc
  } else {
    formData.bank = ''
    formData.bankAccount = ''
    formData.bankPoc = ''
  }
}

const { close } = inject('dialogEmits') as {
  close: () => void
}
const basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'companyId',
    label: '公司主体',
    span: 13,
    component: (
      <eplus-custom-select
        api={VenderLoanApi.getcompanySimpleList}
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
    field: 'venderCode',
    label: '供应商',
    span: 13,
    component: (
      <eplus-input-search-select
        api={getVenderSimpleList}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="name"
        label="name"
        value="code"
        class="!w-100%"
        placeholder="请选择"
        onChangeEmit={getBankList}
      />
    ),
    rules: {
      required: true,
      message: '请选择供应商'
    }
  },
  {
    field: 'loanType',
    label: '借款方式',
    span: 13,
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.VENDER_LOAN_TYPE}
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
    field: 'bankAccount',
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
    field: 'amount',
    label: '欠款金额',
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
    field: 'deadlineTime',
    label: '借款截止日期',
    editable: true,
    span: 13,
    component: (
      <el-date-picker
        type="date"
        placeholder="请选择"
        format="YYYY/MM/DD"
        value-format="x"
      />
    ),
    rules: {
      required: true,
      message: '请选择借款截止日期'
    }
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同编号',
    span: 13,
    disabled: purchaseContractCodeFlag,
    component: (
      <eplus-input-search-select
        api={emsApi.purchaseList}
        params={{ pageSize: 100, pageNo: 1 }}
        keyword="code"
        label="code"
        value="code"
        class="!w-100%"
        placeholder="请选择"
      />
    ),
    rules: {
      required: true,
      message: '请选择供应商'
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
    field: 'annex',
    label: '附件',
    span: 24
  }
])
const emit = defineEmits(['handleSuccess'])
const formRef = ref()
const saveForm = async (type) => {
  let valid = await formRef.value.validate()
  if (!valid) return
  // 提交请求
  loading.value = true
  try {
    const data = formRef.value.formData
    const params = {
      ...data,
      submitFlag: type
    }
    if (props.mode == 'create') {
      await VenderLoanApi.createVenderLoan(params)
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess', 'success')
    } else {
      await VenderLoanApi.updateVenderLoan(params)
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
    let res = await VenderLoanApi.getVenderLoan({ id: props.id })
    Object.assign(formData, res)
  }
})
</script>
