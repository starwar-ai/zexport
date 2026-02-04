<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'oa:payment-app:create',
      handler: saveForm
    }"
    :submitAction="{
      permi: 'oa:payment-app:submit',
      handler: submitForm
    }"
    :closeAction="{
      handler: close
    }"
  >
    <eplus-form-items
      title="支付说明"
      :formSchemas="explainSchemas"
    >
      <!--  -->
      <template #prepaidFlag>
        <EplusDictRadio
          v-model="formData.prepaidFlag"
          :dictType="DICT_TYPE.PAYMENT_APPLY_TYPE"
          :filter="prepaidFlagFilter"
        />
      </template>
      <template #currency>
        <eplus-dict-select
          v-model="formData.currency"
          :dictType="DICT_TYPE.CURRENCY_TYPE"
          class="!w-100%"
          defaultValue="RMB"
          :clearable="false"
          :disabled="disabledFlag || sendDisabledFlag || formData.prepaidFlag == 3"
        />
      </template>
      <template #amount>
        <EplusMoney
          v-model:amount="formData.amount.amount"
          v-model:currency="formData.amount.currency"
          :currencyDisabled="true"
          :amountDisabled="disabledFlag || sendDisabledFlag || formData.prepaidFlag == 3"
        />
      </template>
    </eplus-form-items>

    <eplus-form-items
      title="支付信息"
      :formSchemas="infoSchemas"
    >
      <template #companyId>
        <eplus-custom-select
          v-model="formData.companyId"
          :api="TravelReimbApi.getcompanySimpleList"
          label="name"
          value="id"
          placeholder="请选择"
          @change="getCompanyName"
          :disabled="disabledFlag"
        />
      </template>
      <template #businessSubjectType>
        <EplusDictRadio
          v-model="formData.businessSubjectType"
          :disabled="disabledFlag || sendDisabledFlag"
          :dictType="DICT_TYPE.BUSINESS_SUBJECT_TYPE"
          :filter="businessSubjectTypeFilter"
        />
      </template>

      <template #businessSubjectCode>
        <eplus-input-search-select
          v-model="formData.businessSubjectCode"
          v-if="formData.businessSubjectType === 3 && !loading"
          :api="corporatePaymentsApi.getCustList"
          :params="{ pageSize: 100, pageNo: 1 }"
          keyword="custName"
          label="name"
          value="code"
          class="!w-100%"
          placeholder="请选择"
          @change-emit="getBankList"
          :defaultObj="custobj"
        />
        <eplus-input-search-select
          v-if="formData.businessSubjectType === 2 && !loading"
          v-model="formData.businessSubjectCode"
          :api="corporatePaymentsApi.getVenderList"
          :params="{ pageSize: 100, pageNo: 1, venderType: 2 }"
          keyword="name"
          label="name"
          value="code"
          class="!w-100%"
          placeholder="请选择"
          @change-emit="getBankList"
          :disabled="disabledFlag"
          :defaultObj="administrationVenderobj"
        />
        <eplus-input-search-select
          v-if="formData.businessSubjectType === 1 && !loading"
          v-model="formData.businessSubjectCode"
          :api="corporatePaymentsApi.getVenderList"
          :params="{ pageSize: 100, pageNo: 1, venderType: 1 }"
          keyword="name"
          label="name"
          value="code"
          class="!w-100%"
          placeholder="请选择"
          @change-emit="getBankList"
          :disabled="disabledFlag"
          :defaultObj="businessVenderobj"
        />
      </template>

      <template #bankList>
        <Table
          :columns="bankListColumns"
          headerAlign="center"
          align="center"
          :data="bankList"
          @row-click="getRow"
        />
      </template>
      <template #paymentAppList>
        <Table
          v-if="formData.prepaidFlag > 1"
          ref="paymentAppListRef"
          :columns="selectionPaymentAppListColumns"
          headerAlign="center"
          align="center"
          :data="paymentList"
          @selection-change="handleSelectionChange"
        />
        <Table
          v-else
          :columns="paymentAppListColumns"
          headerAlign="center"
          align="center"
          :data="paymentList"
        />
      </template>
      <template #totalPaymentAmount>
        <EplusMoneyLabel :val="formData.totalPaymentAmount" />
      </template>
      <template #totalInvoiceAmount>
        <EplusMoneyLabel :val="formData.totalInvoiceAmount" />
      </template>
      <template #invoice>
        <UploadList
          v-model="formData.invoice"
          :fileList="formData.invoice"
          @success="getInvoice"
        />
      </template>
      <template #invoiceAmount>
        <EplusMoney
          v-model:amount="formData.invoiceAmount.amount"
          v-model:currency="formData.invoiceAmount.currency"
          :currencyDisabled="true"
        />
      </template>
      <template #payAmount>
        <EplusMoney
          v-model:amount="formData.payAmount.amount"
          v-model:currency="formData.payAmount.currency"
          :currencyDisabled="true"
        />
      </template>
      <template #invoiceFlag>
        <EplusDictRadio
          v-model="formData.invoiceFlag"
          :dictType="DICT_TYPE.IS_INT"
          :disabled="formData.prepaidFlag == 3"
        />
      </template>
    </eplus-form-items>
    <FeeShareOrderFormCom
      ref="FeeShareOrderFormComRef"
      v-if="formData.auxiliaryType == 1"
      :info="formData.feeShare"
      :feeShareAmount="formData.amount"
    />
  </eplus-form>
  <OrderFeeShare
    ref="OrderFeeShareRef"
    @submit="handleSubmit"
  />
  <!-- <el-drawer
    v-model="drawerFlag"
    title="智能检测"
    direction="rtl"
  >
    <span>Hi, there!</span>
  </el-drawer> -->
</template>
<script setup lang="tsx">
import { formatterPrice, maskBankAccount } from '@/utils/index'
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import { DICT_TYPE } from '@/utils/dict'
import { cloneDeep } from 'lodash-es'
import * as TravelReimbApi from '@/api/oa/travelreimb'
import * as corporatePaymentsApi from '@/api/oa/corporatePayments'
import { EplusMoney } from '@/components/EplusMoney'
import OrderFeeShare from '@/views/oa/generalExpense/components/GeneralExpenseFeeShareForm.vue'
import FeeShareOrderFormCom from '@/views/ems/send/components/FeeShare/FeeShareForm.vue'
import UploadList from '@/components/UploadList/index.vue'
import { isValidArray } from '@/utils/is'
import { useFeeStore } from '@/store/modules/fee'
import { EplusDictRadio } from '@/components/EplusSelect'
import { formatMoneyColumn } from '@/utils/table'
import { moneyInputPrecision } from '@/utils/config'
import { formatNum } from '@/utils'
import { usePageCache } from '@/hooks/web/usePageCache'

const feeList = useFeeStore().feeList
const dictSubjectList = feeList.filter((item) => item.showFeeFlag != 1)
const feeDescList = feeList.filter((item) => item.showDescFlag == 1)
const message = useMessage()
const { t } = useI18n()

// 添加页面缓存管理
const { isActivated, isFirstMount } = usePageCache({
  onActivatedCallback: () => {
    // 页面激活时的处理
    if (!isFirstMount.value) {
      console.log('对公付款表单页面从缓存中恢复')
    }
  },
  autoRestoreScroll: true
})

defineOptions({ name: 'CorporatePaymentsForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
  emsParams?: any
  contractParams?: any
  forwarderFeeInfo?: any
  feeShareFlag?: number
}>()
const loading = ref(false)
let cloneFormData = reactive({})
const drawerFlag = ref(false)
const sendDisabledFlag = ref(false)
const disabledFlag = ref(false) //船代公司业务类型判断是否禁用
let formData: EplusAuditable = reactive({
  currency: 'RMB',
  auditStatus: 0,
  prepaidFlag: 1,
  invoiceFlag: 1,
  invoiceAmount: {
    amount: '',
    currency: 'RMB'
  },
  payAmount: {
    amount: '',
    currency: 'RMB'
  },
  amount: {
    amount: '',
    currency: 'RMB'
  },
  reason: '',
  companyId: '',
  businessSubjectType: null,
  businessSubjectCode: '',
  bank: '',
  bankAddress: '',
  bankAccount: '',
  bankPoc: '',
  bankCode: '',
  paymentAppList: [],
  totalPaymentAmount: {
    amount: '',
    currency: 'RMB'
  },
  totalInvoiceAmount: {
    amount: '',
    currency: 'RMB'
  },
  relationCode: null
})

provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}

const getInvoice = (params) => {
  formData.invoice = params
}

let explainSchemas: EplusFormSchema[] = reactive([
  {
    field: 'prepaidFlag',
    label: '单据类型',
    xl: 8,
    lg: 12,
    rules: {
      required: true,
      message: '请选择单据类型'
    }
  },
  {
    field: 'currency',
    label: '支付币种',
    rules: [
      {
        required: true,
        message: '请选择报销币种'
      }
    ]
  },
  {
    field: 'amount',
    label: '本次支付金额',
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        let regx = new RegExp(`^(\\d{1,11})(\\.?\\d{1,${moneyInputPrecision}})?$`)
        if (formData.amount.amount === '') {
          callback(new Error('请输入本次支付金额'))
        } else if (!regx.test(formData.amount.amount)) {
          callback(new Error(`金额只能输入正数,最多11位整数,${moneyInputPrecision}位小数`))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'reason',
    label: '支付事由',
    component: <el-input type="textarea" />,
    rules: {
      required: true,
      message: '请输入支付事由'
    }
  },
  {
    field: 'remark',
    label: '备注',
    component: <el-input type="textarea" />
  }
])
const paymentList = ref([])
const getHistoryList = async () => {
  if (formData.businessSubjectType && formData.businessSubjectCode && formData.companyId) {
    paymentList.value = await corporatePaymentsApi.getHistoryList(
      formData.businessSubjectType,
      formData.businessSubjectCode,
      formData.paymentAppList,
      formData.companyId,
      1
    )
    nextTick(() => {
      if (isValidArray(formData.paymentAppList)) {
        paymentList.value.forEach((item: any) => {
          formData.paymentAppList.forEach((el) => {
            if (item.code == el) {
              selectedList.value.push(item)
              paymentAppListRef.value!.elTableRef.toggleRowSelection(item, undefined)
            }
          })
        })
      }
    })
  } else {
    paymentList.value = []
  }

  // if (isValidArray(paymentList.value)) {
  //   infoSchemas[9].disabled = false
  // } else {
  //   infoSchemas[9].disabled = true
  // }
}
const getBankList = async (val, list) => {
  let item = list.find((item) => item.code === val)
  //
  if (item) {
    formData.businessSubjectName = item.name
    bankList.value = item.bankAccountList || item.bankAccountDTOList || []
    if (bankList.value.length > 0) {
      bankList.value.forEach((item) => {
        if (formData.bankAccount) {
          if (item.bankAccount === formData.bankAccount) {
            getRow(item)
          } else {
            if (item.defaultFlag == 1) {
              getRow(item)
            }
          }
        } else {
          if (item.defaultFlag == 1) {
            getRow(item)
          }
        }
      })
    }
  } else {
    bankList.value = []
  }
  await getHistoryList()
  if (formData.businessSubjectType === 2) {
    drawerFlag.value = true
  }
}

const getCompanyName = async (item) => {
  formData.companyName = item.name
  await getHistoryList()
}

const pagePath = useRoute().path

const prepaidFlagFilter = (arr) => {
  if (props.contractParams?.relationType === 4) {
    return arr.filter((item) => item.value < 3)
  } else {
    return arr
  }
}
const businessSubjectTypeFilter = (arr) => {
  if (pagePath === '/oa/payment/corporate-payments-business') {
    formData.businessSubjectType = 1
    return arr.filter((item) => item.value != 2 && item.value < 4)
  } else if (pagePath === '/oa/payment/corporate-payments-administration') {
    formData.businessSubjectType = 2
    return arr.filter((item) => item.value != 1 && item.value < 4)
  } else if (pagePath === '/oa/payment/corporate-payments') {
    formData.businessSubjectType = 1
    return arr.filter((item) => item.value < 4)
  } else {
    if (props.contractParams) {
      formData.businessSubjectType = 1
    } else {
      formData.businessSubjectType = 2
    }
    return arr.filter((item) => item.value < 4)
  }
}
let infoSchemas: EplusFormSchema[] = reactive([
  {
    field: 'companyId',
    label: '公司主体',
    rules: {
      required: true,
      message: '请选择公司主体'
    }
  },
  {
    field: 'businessSubjectType',
    label: '支付对象类型',
    xl: 8,
    lg: 12,
    disabled: false,
    rules: {
      required: true,
      message: '请选择支付对象类型'
    }
  },
  {
    field: 'businessSubjectCode',
    label: '支付对象',
    xl: 8,
    lg: 12,
    rules: {
      required: true,
      message: '请选择支付对象'
    }
  },
  {
    field: 'bankList',
    label: '账户信息',
    span: 24
  },
  {
    field: 'invoiceFlag',
    label: '是否有发票',
    span: 24,
    disabled: true
  },

  {
    field: 'dictSubjectId',
    label: '发票名称',
    disabled: true,
    hint: (
      <el-table
        data={feeDescList}
        size="small"
        max-height="250"
      >
        <el-table-column
          width="100"
          property="feeName"
          label="发票名称"
        />
        <el-table-column
          width="660"
          property="feeDesc"
          label="费用描述"
        />
      </el-table>
    ),
    component: (
      <eplus-input-select
        dataList={dictSubjectList}
        label="feeName"
        value="id"
        class="!w-100%"
      />
    ),
    rules: { required: true, message: '请选择发票名称' }
  },
  {
    field: 'invoiceAmount',
    label: '发票金额',
    disabled: true,
    rules: {
      required: false,
      validator: (rule: any, value: any, callback: any) => {
        let regx = new RegExp(`^(\\d{1,11})(\\.?\\d{1,${moneyInputPrecision}})?$`)
        if (formData.invoiceAmount.amount && !regx.test(formData.invoiceAmount.amount)) {
          callback(new Error(`金额只能输入正数,最多11位整数,${moneyInputPrecision}位小数`))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'invoice',
    label: '发票附件',
    span: 12,
    disabled: true
  },
  // {
  //   field: 'payAmount',
  //   label: '支付金额',
  //   disabled: true,
  //   rules: {
  //     validator: (rule: any, value: any, callback: any) => {
  //       let regx = /^(\d{1,11})(\.?\d{1,3})?$/
  //       if (formData.invoiceAmount.amount && !regx.test(formData.invoiceAmount.amount)) {
  //         callback(new Error('金额只能输入正数,最多11位整数,3位小数'))
  //       } else {
  //         callback()
  //       }
  //     }
  //   }
  // },
  {
    field: 'paymentAppList',
    label: '关联历史付款',
    disabled: true,
    span: 24,
    rules: {
      required: false,
      validator: (rule: any, value: any, callback: any) => {
        if (formData.prepaidFlag == 3 && !isValidArray(selectedList.value)) {
          callback(new Error('请选择关联历史付款'))
        } else {
          callback()
        }
      }
    }
  },
  {
    field: 'totalPaymentAmount',
    label: '累计支付金额'
  },
  {
    field: 'totalInvoiceAmount',
    label: '累计发票金额'
  }
])
watch(
  () => formData.invoiceFlag,
  (val) => {
    //     dictSubjectId
    // invoiceAmount
    // invoice
    // paymentAppList
    if (val === 1) {
      infoSchemas[5].disabled = false
      infoSchemas[6].disabled = false
      infoSchemas[7].disabled = false
      infoSchemas[8].disabled = false
    } else {
      infoSchemas[5].disabled = true
      infoSchemas[6].disabled = true
      infoSchemas[7].disabled = true
      infoSchemas[8].disabled = true
      formData.dictSubjectId = undefined
      formData.invoiceAmount.amount = undefined
      formData.invoice = []
    }
  },
  {
    immediate: true
  }
)
watch(
  () => formData.currency,
  (val) => {
    formData.payAmount.currency = val
    formData.invoiceAmount.currency = val
    formData.amount.currency = val
    formData.totalPaymentAmount.currency = val
    formData.totalInvoiceAmount.currency = val
  },
  {
    immediate: true
  }
)
const bankListColumns = reactive([
  {
    label: '选择',
    width: '60px',
    slots: {
      default: (data) => {
        const { row, $index } = data
        return (
          <el-radio
            v-model={radioobj.val}
            label={row.bankAccount}
          >
            &nbsp;
          </el-radio>
        )
      }
    }
  },
  {
    field: 'bank',
    label: '开户行'
  },
  {
    field: 'bankAddress',
    label: '开户行地址'
  },
  {
    field: 'bankPoc',
    label: '账户名称'
  },
  {
    field: 'bankAccount',
    label: '账户号码',
    slots: {
      default: (data) => {
        const { row } = data
        return maskBankAccount(row.bankAccount)
      }
    }
  },
  {
    field: 'bankCode',
    label: '银行行号'
  }
])

let paymentAppListColumns = reactive([
  {
    field: 'code',
    label: '付款单号'
  },
  {
    field: 'companyName',
    label: '公司主体'
  },
  {
    field: 'invoiceAmount',
    label: '发票金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'amount',
    label: '预付金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'totalPaymentAmount',
    label: '累计金额',
    formatter: formatMoneyColumn()
  },
  {
    field: 'reason',
    label: '支付事由'
  }
])

let selectionPaymentAppListColumns = reactive([{ type: 'selection' }, ...paymentAppListColumns])

const selectedList = ref([])
const handleSelectionChange = (val) => {
  selectedList.value = val
}
const bankList = ref([])

let radioobj = reactive({
  val: undefined
})
const getRow = (row: any) => {
  radioobj.val = row.bankAccount
  formData.bank = row.bank
  formData.bankAccount = row.bankAccount
  formData.bankAddress = row.bankAddress
  formData.bankCode = row.bankCode
  formData.bankPoc = row.bankPoc
}

let custobj = reactive({})
let administrationVenderobj = reactive({})
let businessVenderobj = reactive({})
const setDefaultObj = async () => {
  if (formData.businessSubjectType === 3) {
    let custList = await corporatePaymentsApi.getCustList({ code: formData.businessSubjectCode })
    custobj = custList.list[0]
  } else {
    let venderList = await corporatePaymentsApi.getVenderList({
      code: formData.businessSubjectCode
    })
    if (formData.businessSubjectType === 1) {
      businessVenderobj = venderList.list[0]
    } else {
      administrationVenderobj = venderList.list[0]
    }
  }
}
const setBackInfo = () => {
  if (formData.businessSubjectType === cloneFormData.businessSubjectType) {
    formData.businessSubjectCode = cloneFormData.businessSubjectCode
    let row = {
      bank: cloneFormData.bank,
      bankAccount: cloneFormData.bankAccount,
      bankAddress: cloneFormData.bankAddress,
      bankCode: cloneFormData.bankCode,
      bankPoc: cloneFormData.bankPoc
    }
    if (!radioobj.val) {
      radioobj.val = row.bankAccount
      bankList.value.push(row)
    }
  } else {
    formData.bankAccount = ''
    formData.businessSubjectCode = ''
    bankList.value = []
  }
}
watch(
  () => formData.businessSubjectType,
  (val, old) => {
    radioobj.val = undefined
    if (val && old) {
      setBackInfo()
    }
  }
)
const getTotalAmount = (list, val1, val2) => {
  formData.totalPaymentAmount.amount = 0
  formData.totalInvoiceAmount.amount = 0
  if (isValidArray(list)) {
    list.forEach((item) => {
      formData.totalPaymentAmount.amount += item.totalPaymentAmount?.amount
      formData.totalInvoiceAmount.amount += item.invoiceAmount?.amount || 0
    })
  }
  formData.totalPaymentAmount.amount += val1 || 0
  formData.totalPaymentAmount.currency = formData.amount?.currency
  formData.totalInvoiceAmount.amount += val2 || 0
  formData.totalInvoiceAmount.currency = formData.invoiceAmount?.currency
}
watch(
  () => [selectedList.value, formData.amount?.amount, formData.invoiceAmount?.amount],
  ([list, val1, val2]) => {
    getTotalAmount(list, val1, val2)
  },
  {
    immediate: true,
    deep: true
  }
)

watch(
  () => formData.prepaidFlag,
  (val) => {
    let item = infoSchemas.find((item) => item.field == 'invoiceAmount')
    let item2 = infoSchemas.find((item) => item.field == 'paymentAppList')
    formData.invoiceFlag = val === 1 ? 0 : 1
    item!.rules!.required = val > 1
    if (val == 3) {
      formData.currency = 'RMB'
      formData.amount.amount = 0
      item2!.rules!.required = true
    } else {
      item2!.rules!.required = false
    }
  },
  {
    immediate: true
  }
)

const FeeShareOrderFormComRef = ref()
const emit = defineEmits(['handleSuccess'])
const formRef = ref()

const getParams = async (type) => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  const data = cloneDeep(formRef.value.formData)
  if (isValidArray(selectedList.value)) {
    for (let index = 0; index < selectedList.value.length; index++) {
      const item: any = selectedList.value[index]
      if (item.companyId != data.companyId) {
        message.error('选中的关联历史付款公司主体与支付信息公司主体不一致')
        return false
      }
    }
  }
  if (data.invoiceFlag == 1) {
    if (
      formatNum(data.totalPaymentAmount.amount, 2) > formatNum(data.totalInvoiceAmount.amount, 2)
    ) {
      message.error('累计支付金额不能大于累计发票金额')
      return false
    }
  }

  return {
    ...data,
    paymentAppList: selectedList.value.map((item) => item.code),
    invoiceAmountList: [data.invoiceAmount],
    feeShareFlag: props.feeShareFlag,
    submitFlag: type
  }
}
const saveForm = async () => {
  loading.value = true
  try {
    let paramsResult = await getParams(0)
    if (!paramsResult) return false
    if (FeeShareOrderFormComRef.value && paramsResult.auxiliaryType === 1) {
      let feeShareParams = await FeeShareOrderFormComRef.value.getParams()
      if (feeShareParams) {
        paramsResult.auxiliaryType = 1
        paramsResult.feeShare = feeShareParams
      } else {
        return false
      }
    }

    if (props.mode == 'create') {
      let res = await corporatePaymentsApi.createPaymentApp(paramsResult)
      if (sendDisabledFlag.value || disabledFlag.value) {
        message.notifyPushSuccess('申请对公支付成功', res, 'payments')
      } else {
        message.success('新增成功')
      }
      close()
    } else {
      await corporatePaymentsApi.updatePaymentApp(paramsResult)
      message.success(t('common.updateSuccess'))
      close()
    }
  } finally {
    loading.value = false
  }
}
const OrderFeeShareRef = ref()
const submitForm = async () => {
  let paramsResult = await getParams(1)
  if (!paramsResult) return false

  if (props.feeShareFlag == 0 || paramsResult.relationType) {
    if (paramsResult.id == undefined) {
      let res = await corporatePaymentsApi.createPaymentApp(paramsResult)
      if (sendDisabledFlag.value || disabledFlag.value) {
        message.notifyPushSuccess('申请对公支付成功', res, 'payments')
      } else {
        message.success(t('common.submitSuccess'))
      }
      close()
    } else {
      await corporatePaymentsApi.updatePaymentApp(paramsResult)
      message.success(t('common.submitSuccess'))
      close()
    }
  } else {
    if (paramsResult.auxiliaryType === 1) {
      if (FeeShareOrderFormComRef.value) {
        let feeShareParams = await FeeShareOrderFormComRef.value.getParams()
        if (feeShareParams) {
          paramsResult.auxiliaryType = 1
          paramsResult.feeShare = feeShareParams
        } else {
          return false
        }
      }
      handleCreate(paramsResult)
    } else {
      if (paramsResult.amount.amount > 0) {
        //打开费用归集
        OrderFeeShareRef.value.handleCreate(null, paramsResult.amount)
      } else {
        handleCreate(paramsResult)
      }
    }
  }
}

const handleCreate = async (paramsResult) => {
  if (paramsResult.id == undefined) {
    let res = await corporatePaymentsApi.createPaymentApp(paramsResult)
    if (sendDisabledFlag.value || disabledFlag.value) {
      message.notifyPushSuccess('申请对公支付成功', res, 'payments')
    } else {
      message.success(t('common.submitSuccess'))
    }
    close()
  } else {
    await corporatePaymentsApi.updatePaymentApp(paramsResult)
    message.success(t('common.submitSuccess'))
    close()
  }
}
const handleSubmit = async (feeShareData) => {
  try {
    loading.value = true
    let paramsResult = await getParams(1)
    if (!paramsResult) return false
    const params = {
      ...paramsResult,
      auxiliaryType: feeShareData ? 1 : 0,
      feeShare: feeShareData ? feeShareData : null
    }

    if (params.id == undefined) {
      let res = await corporatePaymentsApi.createPaymentApp(params)
      if (sendDisabledFlag.value || disabledFlag.value) {
        message.notifyPushSuccess('申请对公支付成功', res, 'payments')
      } else {
        message.success(t('common.submitSuccess'))
      }
      close()
    } else {
      await corporatePaymentsApi.updatePaymentApp(params)
      message.success(t('common.submitSuccess'))
      close()
    }
  } finally {
    loading.value = false
  }
}

const paymentAppListRef = ref()

onMounted(async () => {
  // custList.value = await corporatePaymentsApi.getCustList('')
  // venderList.value = await corporatePaymentsApi.getVenderList('')
  // setVenderType()
  if (props.mode === 'edit') {
    loading.value = true
    let res = await corporatePaymentsApi.getPaymentAppInfo({ id: props.id })
    //  = { ...res }
    Object.assign(formData, res)
    formData.invoiceAmount = formData.invoiceAmountList
      ? formData.invoiceAmountList[0]
      : { amount: '', currency: 'RMB' }
    formData.currency = formData.amount?.currency

    cloneFormData = cloneDeep(formData)
    await setDefaultObj()
    await setBackInfo()
    loading.value = false
    await getHistoryList()
  } else {
    if (props.emsParams) {
      formData.currency = props.emsParams.paymentAmount.currency
      formData.amount = {
        amount: formatterPrice(props.emsParams.paymentAmount.amount, moneyInputPrecision),
        currency: props.emsParams.paymentAmount.currency
      }
      formData.relationType = props.emsParams.relationType
      formData.relationCode = props.emsParams.relationCode
      formData.businessSubjectCode = props.emsParams.venderCode
      formData.businessSubjectType = 2
      formData.reason = '快递费'
      sendDisabledFlag.value = true
    }
    if (props.forwarderFeeInfo) {
      formData.amount = {
        amount: formatterPrice(props.forwarderFeeInfo.amount.amount, moneyInputPrecision),
        currency: props.forwarderFeeInfo.amount.currency
      }
      formData.currency = props.forwarderFeeInfo.amount.currency
      formData.companyId = props.forwarderFeeInfo.companyId
      formData.relationType = props.forwarderFeeInfo.relationType
      formData.relationCode = props.forwarderFeeInfo.codeList
      formData.businessSubjectType = 2
      formData.businessSubjectCode = props.forwarderFeeInfo.venderCode
      formData.businessSubjectName = props.forwarderFeeInfo.venderName
      formData.reason = '单证费用'
      formData.prepaidFlag = 2
      let item = dictSubjectList.find((item: any) => item.feeName == '单证费')
      formData.dictSubjectId = item?.id
      disabledFlag.value = true
      // 设置供应商默认对象并加载银行账户信息
      await setDefaultObj()
      // 获取供应商银行账户列表
      if (administrationVenderobj && administrationVenderobj.code) {
        await getBankList(administrationVenderobj.code, [administrationVenderobj])
      }
    }
    if (props.contractParams) {
      formData.currency = props.contractParams.paymentAmount.currency
      formData.amount = {
        amount: formatterPrice(props.contractParams.paymentAmount.amount, moneyInputPrecision),
        currency: props.contractParams.paymentAmount.currency
      }
      formData.relationType = props.contractParams.relationType
      formData.relationCode = props.contractParams.relationCode
      formData.businessSubjectCode = props.contractParams.venderCode
      formData.businessSubjectName = props.contractParams.venderName
      formData.businessSubjectType = 1
      formData.reason = '包材采购费用'
      formData.companyId = props.contractParams.companyId
      disabledFlag.value = true
      formData.prepaidFlag = 2
    }
  }
})
</script>
