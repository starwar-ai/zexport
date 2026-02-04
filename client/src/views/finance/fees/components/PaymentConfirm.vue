<template>
  <div :class="[prefixCls]">
    <Dialog
      v-model="dialogVisible"
      append-to="#appView"
      :destroy-on-close="true"
      :title="formData?.checkedNum === 1 ? '确认付款' : '批量付款'"
      width="500px"
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
          <template #checkedNum>
            <span>{{ formData?.checkedNum ? formData.checkedNum + '条' : '-' }}</span>
          </template>
          <template #applyAmount>
            <EplusMoneyLabel :val="formData.applyAmount" />
          </template>
          <template #paidAmount>
            <EplusMoneyLabel :val="formData.paidAmount" />
          </template>

          <template #amount>
            <EplusMoney
              v-if="isEdit"
              v-model:amount="formData.amount.amount"
              v-model:currency="formData.amount.currency"
              :currencyDisabled="true"
            />
            <EplusMoneyLabel
              v-else
              :val="formData.amount"
            />
          </template>

          <template #payMethod>
            <eplus-dict-select
              v-model="formData.payMethod"
              style="width: 100%"
              :dictType="DICT_TYPE.PAY_METHOD"
              clearable
              @change-emit="
                (val) => {
                  payMethodChange(val)
                }
              "
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
              label="id"
              @change-emit="changeBank"
              class="!w-100%"
            />
          </template>
          <template #annex>
            <UploadList
              ref="UploadListRef"
              :fileList="formData.annex"
              @success="
                (list) => {
                  formData.annex = list
                }
              "
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
import { EplusMoney, EplusMoneyLabel } from '@/components/EplusMoney'
import { useUserStore } from '@/store/modules/user'
import * as CommonApi from '@/api/infra/acountConfig'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'

const user = useUserStore().getUser
const message = useMessage()
const { getPrefixCls } = useDesign()
const prefixCls = getPrefixCls('eplus-dialog')
const formRef = ref()
const props = defineProps({
  destroyOnClose: propTypes.bool.def(true),
  beforeClose: propTypes.string.def('')
})
const bankList = ref([])
const formData = reactive({
  inputUser: '',
  checkedNum: 0,
  paymentIds: [],
  paymentDate: 0,
  applyAmount: {},
  paidAmount: {},
  amount: {
    amount: 0,
    currency: 'RMB'
  },
  actualAmount: {
    amount: 0,
    currency: 'RMB'
  },
  companyId: '',
  payMethod: '',
  bank: '',
  bankAccount: '',
  annex: [],
  acceptanceDays: ''
})
provide('formData', formData)
const loading = ref(false)
const emit = defineEmits(['success', 'failure'])
const dialogVisible = ref<boolean>(false)
let feesFormSchemas = reactive([
  {
    field: 'applyAmount',
    label: '付款金额',
    span: 22,
    disabled: true
  },
  {
    field: 'paidAmount',
    label: '已付款金额',
    span: 22,
    disabled: true
  },
  {
    field: 'amount',
    label: '实际付款金额',
    span: 22,
    disabled: true,
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        let regx = /^(\d{1,11})(\.?\d{1,3})?$/
        if (!isEdit.value) {
          callback()
        } else {
          if (value.amount <= 0) {
            callback(new Error(`付款金额必须大于0`))
          } else if (!regx.test(value.amount)) {
            callback(new Error('付款金额只能输入正数,最多11位整数,3位小数'))
          } else if (value.amount > maxAmount.value) {
            formData.amount.amount = maxAmount.value
            callback(new Error(`付款金额不能大于${maxAmount.value}`))
          } else {
            callback()
          }
        }
      }
    }
  },
  {
    field: 'paymentDate',
    label: '付款日',
    span: 22,
    component: (
      <el-date-picker
        type="date"
        placeholder="请选择"
        valueFormat="x"
        class="!w-100%"
        disabledDate={(date) => {
          const dateTime = new Date(date).getTime()
          let today = new Date() // 获取当前时间
          today.setHours(0, 0, 0, 0) // 将当前时间的小时、分钟、秒和毫秒设置为0，得到今天0点的时间
          let todayTimestamp = today.getTime()
          return dateTime > todayTimestamp
        }}
      />
    ),
    rules: [
      {
        required: true,
        message: '请选择付款日',
        trigger: 'change'
      }
    ]
  },
  {
    field: 'inputUser',
    label: '付款人',
    span: 22,
    component: (
      <el-input
        validate-event={false}
        disabled={true}
      />
    ),
    editable: true
  },
  {
    field: 'payMethod',
    label: '支付方式',
    span: 22,
    rules: [
      {
        required: true,
        message: '请选择支付方式',
        trigger: 'change'
      }
    ]
  },
  {
    field: 'acceptanceDays',
    label: '承兑天数',
    span: 22,
    disabled: true,
    rules: {
      required: true,
      message: '请选择承兑天数',
      trigger: 'change'
    }
  },
  {
    field: 'bank',
    label: '付款银行',
    span: 22,
    rules: {
      required: true,
      message: '请选择付款银行',
      trigger: 'change'
    }
  },
  {
    field: 'annex',
    label: '附件',
    span: 22
  }
])

const payMethodChange = (val) => {
  const item: any = feesFormSchemas.find((item) => item.field === 'acceptanceDays')
  item.disabled = getDictLabel(DICT_TYPE.PAY_METHOD, val) !== '承兑汇票'
  const bankItem: any = feesFormSchemas.find((item) => item.field === 'bank')
  bankItem.rules!.required = getDictLabel(DICT_TYPE.PAY_METHOD, val) !== '现金'
}

const changeBank = (val) => {
  if (val) {
    let bankObj = bankList.value.find((item) => item.id === val)
    formData.bank = bankObj.bankName
    formData.bankAccount = bankObj.bankCode
  }
}
const maxAmount = ref(0)
const isEdit = ref(false)
const batchFlag = ref(false)
const open = async (rows, payMethod, editFlag) => {
  const accountRes = await CommonApi.getAcountConfig({ id: rows?.companyId })
  feesFormSchemas[0].disabled = false
  feesFormSchemas[1].disabled = false
  feesFormSchemas[2].disabled = false
  isEdit.value = editFlag
  batchFlag.value = false
  bankList.value = accountRes?.companyBankList
  maxAmount.value = rows.amount.amount
  formData.payMethod = payMethod || 2
  formData.paymentIds = []
  formData.paymentIds.push({
    id: rows?.id,
    amount: rows.amount
  })

  formData.applyAmount = rows?.applyAmount
  formData.paidAmount = rows?.paidAmount
  formData.companyId = rows?.companyId
  formData.checkedNum = 1
  formData.amount = rows.amount
  formData.inputUser = user.nickname
  formData.paymentDate = Date.now()
  formData.acceptanceDays = rows.acceptanceDays
  formData.bank = rows.bank
  formData.bankAccount = rows.bankAccount

  dialogVisible.value = true
  payMethodChange(formData.payMethod)
}

const batchPay = async (rows, payMethod) => {
  isEdit.value = false
  batchFlag.value = true
  feesFormSchemas[0].disabled = true
  feesFormSchemas[1].disabled = true
  feesFormSchemas[2].disabled = false
  const accountRes = await CommonApi.getAcountConfig({ id: rows?.[0]?.companyId })
  bankList.value = accountRes?.companyBankList
  formData.payMethod = payMethod
  formData.paymentIds = rows.map((item) => {
    return { id: item?.id, amount: item?.amount }
  })
  formData.companyId = rows?.[0]?.companyId
  formData.checkedNum = rows.length
  formData.amount = {
    amount: rows.reduce((total, item) => total + item.amount?.amount, 0),
    currency: rows[0]?.amount?.currency
  }
  formData.inputUser = user.nickname
  formData.paymentDate = Date.now()
  // let groupedByCurrency = rows.reduce((grouped, item) => {
  //   if (!grouped[item.currency]) {
  //     grouped[item.currency] = []
  //   }
  //   grouped[item.currency].push(item?.applyAmount[0]?.amount)
  //   return grouped
  // }, {})
  // let groupedArr = Object.keys(groupedByCurrency).map((key) => {
  //   return groupedByCurrency[key].reduce((total, item) => total + item, 0) + ' ' + key
  // })

  // formData.amount = groupedArr.join(',')
  dialogVisible.value = true
}

const payAction = async () => {
  let valid = await formRef.value?.validate()
  if (valid) {
    let params = {
      paymentIds: batchFlag.value
        ? formData.paymentIds
        : formData.paymentIds.map((item) => {
            return { id: item.id, amount: formData.amount, annex: formData.annex }
          }),
      paymentDate: formData.paymentDate,
      companyId: formData.companyId,
      paymentMethod: formData.payMethod,
      bank: formData.bank,
      bankAccount: formData.bankAccount
    }
    FeesApi.batchPayment(params)
      .then(() => {
        message.success('支付确认成功')
        emit('success')
        close()
      })
      .catch(() => {
        message.warning('支付确认失败，请重试')
      })
  } else {
    message.error('请填写完整信息')
  }
}
const close = async () => {
  formRef.value.resetFields()
  dialogVisible.value = false
}
defineExpose({ open, close, batchPay })

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
