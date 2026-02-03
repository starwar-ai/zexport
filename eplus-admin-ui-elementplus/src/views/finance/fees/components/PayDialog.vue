<template>
  <div :class="[prefixCls]">
    <Dialog
      v-model="dialogVisible"
      append-to="#appView"
      :destroy-on-close="true"
      :title="'计划付款'"
      width="70%"
    >
      <el-form
        :model="formData"
        :loading="loading"
        ref="formRef"
      >
        <eplus-form-items
          title=""
          :formSchemas="feesFormSchemas"
        >
          <template #applyAmount>
            <EplusMoneyLabel :val="formData?.applyAmount" />
          </template>
          <template #paidAmount>
            <EplusMoneyLabel :val="formData?.paidAmount" />
          </template>
          <template #planAmount>
            <EplusMoney
              v-model:amount="formData.planAmount.amount"
              v-model:currency="formData.planAmount.currency"
              :currencyDisabled="true"
            />
          </template>
          <template #businessType>
            <eplus-dict-select
              v-model="formData.businessType"
              style="width: 100%"
              :dictType="DICT_TYPE.BUSINESS_TYPE"
              clearable
              disabled
          /></template>
          <template #payMethod>
            <eplus-dict-select
              v-model="formData.payMethod"
              style="width: 100%"
              :dictType="DICT_TYPE.PAY_METHOD"
              clearable
              @change-emit="() => payMethodChange()"
            />
          </template>
          <template #acceptanceDays>
            <eplus-dict-select
              style="width: 100%"
              v-model="formData.acceptanceDays"
              :dictType="DICT_TYPE.ACCEPTANCE_DAYS"
              :disabled="formData.payMethod != 3"
            />
          </template>
          <template #businessSubjectType>
            <eplus-dict-select
              v-model="formData.businessSubjectType"
              style="width: 100%"
              :dictType="DICT_TYPE.BUSINESS_SUBJECT_TYPE"
              clearable
              disabled
            />
          </template>
          <template #bank>
            <eplus-input-select
              v-model="formData.bank"
              :dataList="bankList"
              :formatLabel="
                (item) => {
                  return `${item.bankName}${item.bankCode.slice(-4)}`
                }
              "
              value="bankName"
              label="bankName"
              @change-emit="changeBank"
            />
          </template>
        </eplus-form-items>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button
            type="primary"
            @click="payAction"
          >
            确认
          </el-button>
          <el-button @click="close">取消</el-button>
        </div>
      </template>
    </Dialog>
  </div>
</template>

<script setup lang="tsx">
import { useDesign } from '@/hooks/web/useDesign'
import { propTypes } from '@/utils/propTypes'
import * as FeesApi from '@/api/finance/fees'
import * as CommonApi from '@/api/infra/acountConfig'
import { DICT_TYPE } from '@/utils/dict'
import { formatDictColumn } from '@/utils/table'

const message = useMessage()
const { getPrefixCls } = useDesign()
const prefixCls = getPrefixCls('eplus-dialog')
const props = defineProps({
  destroyOnClose: propTypes.bool.def(true),
  beforeClose: propTypes.string.def('')
})
const bankList = ref([])
const formData = ref({
  amount: {
    amount: 0,
    currency: 'RMB'
  },
  businessSubjectType: '',
  payMethod: undefined as number | undefined,
  businessType: '',
  applyAmount: [
    {
      amount: 0,
      currency: ''
    }
  ],
  planAmount: {},
  bank: '',
  bankAccount: '',
  acceptanceDays: undefined
})
provide('formData', formData)
const loading = ref(false)
const emit = defineEmits(['success', 'failure'])
const dialogVisible = ref<boolean>(false)
let feesFormSchemas = reactive([
  {
    field: 'companyName',
    label: '付款单位',
    span: 8,
    component: (
      <el-input
        validate-event={false}
        disabled={true}
      />
    )
  },
  {
    field: 'applyAmount',
    label: '付款金额',
    span: 8
  },
  {
    field: 'paidAmount',
    label: '已付款金额',
    span: 8
  },
  {
    field: 'planAmount',
    label: '计划付款金额',
    span: 8,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        let regx = /^(\d{1,11})(\.?\d{1,3})?$/
        if (value.amount <= 0) {
          callback(new Error(`付款金额必须大于0`))
        } else if (!regx.test(value.amount)) {
          callback(new Error('付款金额只能输入正数,最多11位整数,3位小数'))
        } else if (value.amount > maxAmount.value) {
          formData.value.planAmount.amount = maxAmount.value
          callback(new Error(`付款金额不能大于${maxAmount.value}`))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'businessSubjectType',
    label: '付款对象类型',
    span: 8
  },
  {
    field: 'businessSubjectName',
    label: '付款对象名称',
    span: 8,
    component: (
      <el-input
        validate-event={false}
        disabled={true}
      />
    )
  },
  // {
  //   field: 'currency',
  //   label: '币别',
  //   span: 8,
  //   editable: true,
  //   component: (
  //     <el-input
  //       validate-event={false}
  //       disabled={true}
  //     />
  //   )
  // },
  {
    field: 'bankAccount',
    label: '银行账号',
    span: 8,
    component: (
      <el-input
        validate-event={false}
        disabled={true}
      />
    )
  },
  {
    field: 'code',
    label: '付款申请单',
    span: 8,
    component: (
      <el-input
        validate-event={false}
        disabled={true}
      />
    )
  },
  {
    field: 'businessType',
    label: '费用类型',
    span: 8,
    formatter: formatDictColumn(DICT_TYPE.BUSINESS_TYPE)
  },
  {
    field: 'payMethod',
    label: '支付方式',
    span: 8
  },
  {
    field: 'acceptanceDays',
    label: '承兑天数',
    span: 8
  },
  {
    field: 'bank',
    label: '付款银行',
    span: 8,
    rules: {
      required: true,
      message: '请选择付款银行',
      trigger: 'change'
    }
  }
])

const payMethodChange = () => {
  formData.value.acceptanceDays = undefined
  if (formData.value.payMethod == 1) {
    feesFormSchemas[feesFormSchemas.length - 1].rules!.required = false
  } else {
    feesFormSchemas[feesFormSchemas.length - 1].rules!.required = true
  }
}

const payAction = async () => {
  FeesApi.submitPlanFees({
    paymentId: formData.value.id,
    paymentMethod: formData.value.payMethod,
    acceptanceDays: formData.value.acceptanceDays,
    bank: formData.value.bank,
    bankAccount: formData.value.bankAccount,
    amount: formData.value.planAmount
  })
    .then(() => {
      message.success('计划付款成功')
      emit('success')
      close()
    })
    .catch(() => {
      message.warning('计划付款失败，请重试')
    })
}
const formRef = ref()
const close = async () => {
  formRef.value.resetFields()
  dialogVisible.value = false
}
const changeBank = (val) => {
  if (val) {
    let bankObj = bankList.value.find((item) => item.bankName === val)
    formData.value.bank = bankObj.bankName
    formData.value.bankAccount = bankObj.bankCode
  }
}
const maxAmount = ref(0)
const open = async (row) => {
  if (row) {
    const res = await CommonApi.getAcountConfig({ id: row?.companyId })
    bankList.value = res?.companyBankList
    Object.assign(formData.value, row)
    formData.value.bank = row.paymentBank
    formData.value.bankAccount = row.paymentBackAccount
    formData.value.applyAmount = row?.applyAmount[0]
    formData.value.payMethod = row.paymentMethod || 2
    maxAmount.value =
      Number(formData.value.applyAmount.amount) - Number(formData.value.paidAmount.amount || 0)
    formData.value.planAmount = {
      amount:
        Number(formData.value.applyAmount.amount) - Number(formData.value.paidAmount.amount || 0),
      currency: formData.value.applyAmount.currency
    }
  }
  dialogVisible.value = true
}
defineExpose({ open, close })

onMounted(() => {})
</script>

<style lang="scss" scoped>
$prefix-cls: #{$namespace}-eplus-dialog;

.#{$prefix-cls} {
  :deep(.el-overlay),
  :deep(.el-overlay-dialog) {
    position: absolute;
  }

  :deep(.el-dialog) {
    display: flex;
    flex-direction: column;
  }

  :deep(.el-dialog__body) {
    flex: 1 1 auto;
    overflow: auto;
  }

  .el-form {
    padding-top: 15px;
  }
}
</style>
