<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
    :saveAction="{
      permi: 'oa:travel-reimb:create',
      handler: saveForm
    }"
    :submitAction="{
      textContent: props.mode === 'edit' ? '提交' : '下一步',
      permi: 'oa:loan-reimb:submit',
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
      <template #TravelList>
        <TravelListCom
          :formData="formData"
          ref="TravelListComRef"
        />
      </template>
    </eplus-form-items>

    <eplus-form-items
      title="费用小项"
      :formSchemas="detailSchemas"
    >
      <template #reimbItems>
        <EplusDictCheckbox
          v-model="formData.reimbItems"
          :dictType="DICT_TYPE.EXPENSE_TYPE"
          :filter="(list) => reimbItemsFilter(list)"
        />
      </template>
      <template #Expense1Com>
        <TrafficFee
          :formData="formData"
          ref="Expense1ComRef"
        />
      </template>
      <template #Expense2Com>
        <SelfDriving
          :formData="formData"
          ref="Expense2ComRef"
        />
      </template>
      <template #Expense3Com>
        <AccommodationFee
          :formData="formData"
          ref="Expense3ComRef"
        />
      </template>
      <template #Expense4Com>
        <TravelAllowance
          :formData="formData"
          ref="Expense4ComRef"
        />
      </template>
      <template #other>
        <Other
          :formData="formData"
          ref="OtherRef"
        />
      </template>
      <template #LoanAppListCom>
        <LoanAppListCom
          :formData="formData"
          ref="LoanAppListComRef"
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
        <EplusMoneyLabel :val="formData.invoiceAmountList || []" />
      </template>
      <template #repayAmountList>
        <EplusMoneyLabel :val="formData.repayAmountList" />
      </template>
      <template #amountList>
        <EplusMoneyLabel :val="formData.amountList" />
      </template>
    </eplus-form-items>
    <!-- 费用归集 -->
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
import { formatterPrice, getTotal, maskBankAccount } from '@/utils/index'
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { DICT_TYPE } from '@/utils/dict'
import TravelListCom from './TravelListCom.vue'
import Other from './components/Other.vue'
import TrafficFee from './components/TrafficFee.vue'
import SelfDriving from './components/SelfDriving.vue'
import AccommodationFee from './components/AccommodationFee.vue'
import TravelAllowance from './components/TravelAllowance.vue'
import LoanAppListCom from './LoanAppListCom.vue'
import { EplusDictCheckbox } from '@/components/EplusCheckbox'
import { useUserStore } from '@/store/modules/user'
import * as TravelReimbApi from '@/api/oa/travelreimb'
import { cloneDeep } from 'lodash-es'
import { isValidArray } from '@/utils/is'
import GeneralExpenseFeeShareForm from '../generalExpense/components/GeneralExpenseFeeShareForm.vue'
import FeeShareFormCom from '@/views/ems/send/components/FeeShare/FeeShareForm.vue'
import { getConfigKey } from '@/api/common'
import { moneyTotalPrecision } from '@/utils/config'
import EplusMoneyLabel from '@/components/EplusMoney/src/EplusMoneyLabel.vue'
import { batchGetFeeShare } from '@/api/oa/entertain'

const message = useMessage()
const { t } = useI18n()
defineOptions({ name: 'TravelAppForm' })
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
  reimbUserDeptId: useUserStore().user.deptId,
  companyId: '',
  auxiliaryType: '',
  auxiliaryList: [],
  trafficFeeList: [],
  accommodationFeeList: [],
  selfDrivingList: [],
  travelAllowanceList: [],
  repayFlag: 0,
  loanAppList: [],
  reimbuserId: useUserStore().user.id,
  auditStatus: 0,
  reimbItems: [],
  invoiceList: [],
  amountList: [],
  invoiceAmountList: [] as { currency: string; amount: number | string }[],
  totalAmountList: [] as { currency: string; amount: number | string }[],
  repayAmountList: [] as { currency: string; amount: number | string }[],
  otherDescList: [] as { currency: string; amount: number | string }[],
  bank: '',
  bankAccount: '',
  bankAddress: '',
  bankPoc: '',
  bankCode: '',
  reimbType: 1,
  currency: 'RMB',
  actualUserId: useUserStore().user.id
})
provide('formData', formData)

const formRef = ref()
provide('formRef', formRef)

const { close } = inject('dialogEmits') as {
  close: () => void
}
const TravelListComRef = ref()
const Expense1ComRef = ref()
const Expense2ComRef = ref()
const Expense3ComRef = ref()
const Expense4ComRef = ref()
const OtherRef = ref()
const CostAttrsComRef = ref()
const LoanAppListComRef = ref()
const UploadListRef = ref()
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
        clearable={false}
        placeholder="请选择"
        class="!w-100%"
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
    label: '账户号码',
    readOnly: true,
    formatter: (val) => {
      if (val) {
        return maskBankAccount(val)
      } else {
        return '-'
      }
    }
  },
  {
    field: 'expenseUseToOccur',
    label: '申请事由',
    labelWidth: '150px',
    span: 8,
    component: (
      <el-input
        type="testarea"
        placeholder="字数不小于10个字，标注关键信息，含客户/供应商/展会/项目等具体名称之一，或者费用产生的主要原因"
      />
    ),
    rules: [
      {
        required: false,
        message: '请输入费用实际用途说明'
      }
    ]
  },
  {
    field: 'remark',
    label: '备注',
    editable: true,
    component: (
      <el-input
        type="testarea"
        placeholder="请输入备注"
      />
    )
  },
  {
    field: 'TravelList',
    label: '关联申请单',
    editable: true,
    disabled: true,
    span: 24,
    rules: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          if (TravelListComRef.value.tableData.length === 0) {
            callback(new Error('请选择申请单'))
          } else {
            callback()
          }
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

const reimbItemsFilter = (list) => {
  if (formData.currency !== 'RMB') {
    let index = formData.reimbItems.findIndex((item) => item === '出差补贴')
    if (index > -1) {
      formData.reimbItems.splice(index, 1)
    }
    return list.filter((item) => {
      return item.value !== 4
    })
  } else {
    return list
  }
}

const detailSchemas: EplusFormSchema[] = reactive([
  {
    field: 'reimbItems',
    label: '本次报销项',
    span: 24,
    rules: [
      {
        required: true,
        message: '请选择报销项'
      }
    ]
  },
  {
    field: 'Expense1Com',
    label: '交通费',
    editable: true,
    disabled: true,
    span: 24
  },
  {
    field: 'Expense2Com',
    label: '自驾',
    editable: true,
    disabled: true,
    span: 24
  },
  {
    field: 'Expense3Com',
    label: '住宿费',
    editable: true,
    disabled: true,
    span: 24
  },
  {
    field: 'Expense4Com',
    label: '出差补贴',
    editable: true,
    disabled: true,
    span: 24
  },
  {
    field: 'other',
    label: '过路费/其他',
    disabled: false,
    span: 24
  },
  {
    field: 'repayFlag',
    label: '是否还款',
    editable: true,
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
    field: 'LoanAppListCom',
    label: '借款单',
    editable: true,
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

watch(
  () => formData.companyId,
  (val) => {
    if (val) {
      detailSchemas[6].disabled = false
    } else {
      detailSchemas[6].disabled = true
    }
  },
  {
    immediate: true
  }
)
watch(
  () => formData.reimbItems,
  (item) => {
    let itemList: any = item || []
    detailSchemas[2].disabled = !itemList.includes('自驾')
    detailSchemas[3].disabled = !itemList.includes('住宿费')
    detailSchemas[4].disabled = !itemList.includes('出差补贴')
    detailSchemas[1].disabled = !itemList.includes('交通费')
    detailSchemas[5].disabled = !itemList.includes('过路费/其他')
  },
  { immediate: true, deep: true }
)

watch(
  () => formData.repayFlag,
  (val) => {
    detailSchemas[7].disabled = !(val == 1)
  },
  { immediate: true }
)
watch(
  () => [
    LoanAppListComRef.value?.loanTotalList,
    Expense1ComRef.value?.TrafficFeeAmountList,
    Expense2ComRef.value?.SelfDrivingAmountList,
    Expense3ComRef.value?.AccommodationFeeAmountList,
    Expense4ComRef.value?.TravelAllowanceAmountList,
    OtherRef.value?.OtherAmountList,
    Expense1ComRef.value?.TrafficFeeinvoiceAmountList,
    Expense2ComRef.value?.SelfDrivinginvoiceAmountList,
    Expense3ComRef.value?.AccommodationFeeinvoiceAmountList,
    OtherRef.value?.OtherinvoiceAmountList
  ],
  ([list, list1, list2, list3, list4, list5, list6, list7, list8, list9]) => {
    let loanTotalList = list || [],
      TrafficFeeAmountList = list1 || [],
      SelfDrivingAmountList = list2 || [],
      AccommodationFeeAmountList = list3 || [],
      TravelAllowanceAmountList = list4 || [],
      OtherAmountList = list5 || [],
      TrafficFeeinvoiceAmountList = list6 || [],
      SelfDrivinginvoiceAmountList = list7 || [],
      AccommodationFeeinvoiceAmountList = list8 || [],
      OtherinvoiceAmountList = list9 || []

    let reimbItemTotalList = [
      ...TrafficFeeAmountList,
      ...SelfDrivingAmountList,
      ...AccommodationFeeAmountList,
      ...TravelAllowanceAmountList,
      ...OtherAmountList
    ]
    formData.amountList = []
    formData.repayAmountList = loanTotalList || []
    formData.totalAmountList = []
    formData.amountList = getTotal(reimbItemTotalList, 'amount', 'currency')
    formData.invoiceAmountList =
      getTotal(
        [
          ...TrafficFeeinvoiceAmountList,
          ...SelfDrivinginvoiceAmountList,
          ...AccommodationFeeinvoiceAmountList,
          ...OtherinvoiceAmountList
        ],
        'amount',
        'currency'
      ) || []
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

    // if (Array.isArray(formData.amountList)) {
    //   formData.amountList.forEach((item: any) => {
    //     let isExist = false
    //     loanTotalList.forEach((el: any) => {
    //       if (item.currency == el.currency) {
    //         isExist = true
    //         if (item.amount > el.amount) {
    //           formData.repayAmountList.push({
    //             currency: item.currency,
    //             amount: el.amount
    //           })
    //           formData.totalAmountList.push({
    //             currency: item.currency,
    //             amount: formatterPrice(item.amount - el.amount, moneyTotalPrecision)
    //           })
    //         } else {
    //           formData.repayAmountList.push({
    //             currency: item.currency,
    //             amount: formatterPrice(item.amount, moneyTotalPrecision)
    //           })
    //         }
    //       }
    //     })
    //     if (!isExist) {
    //       formData.totalAmountList.push({
    //         currency: item.currency,
    //         amount: formatterPrice(item.amount, moneyTotalPrecision)
    //       })
    //     }
    //   })
    // }
  },
  { immediate: true, deep: true }
)

const emit = defineEmits(['handleSuccess'])

const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params = cloneDeep(formData)

  // if (!params.bank || !params.bankAccount || !params.bankPoc) {
  //   message.warning('银行信息有误')
  //   return
  // }
  if (Expense1ComRef.value && params.reimbItems.includes('交通费')) {
    let trafficFeeResult = await Expense1ComRef.value?.saveForm()
    if (trafficFeeResult) {
      params.trafficFeeList = trafficFeeResult
    } else {
      params.trafficFeeList = []
      return false
    }
  } else {
    params.trafficFeeList = []
  }
  if (Expense2ComRef.value && params.reimbItems.includes('自驾')) {
    let selfDrivingResult = await Expense2ComRef.value?.saveForm()
    if (selfDrivingResult) {
      params.selfDrivingList = selfDrivingResult
    } else {
      params.selfDrivingList = []
      return false
    }
  } else {
    params.selfDrivingList = []
  }
  if (Expense3ComRef.value && params.reimbItems.includes('住宿费')) {
    let accommodationFeeResult = await Expense3ComRef.value?.saveForm()
    if (accommodationFeeResult) {
      params.accommodationFeeList = accommodationFeeResult
    } else {
      params.accommodationFeeList = []
      return false
    }
  } else {
    params.accommodationFeeList = []
  }
  if (Expense4ComRef.value && params.reimbItems.includes('出差补贴')) {
    let travelAllowanceResult = await Expense4ComRef.value?.saveForm()
    if (travelAllowanceResult) {
      params.travelAllowanceList = travelAllowanceResult
    } else {
      params.travelAllowanceList = []
      return false
    }
  } else {
    params.travelAllowanceList = []
  }
  if (OtherRef.value && params.reimbItems.includes('过路费/其他')) {
    let otherDescResult = await OtherRef.value?.saveForm()
    if (otherDescResult) {
      params.otherDescList = otherDescResult
    } else {
      params.otherDescList = []
      return false
    }
  } else {
    params.otherDescList = []
  }
  // if (params.invoiceList != null && params.invoiceList.length > 0) {
  //   params.invoiceList = params.invoiceList.map((item) => item.id)
  // }

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
  let applyIdList = TravelListComRef.value?.checkData()
  if (applyIdList) {
    params.applyIdList = applyIdList
  }
  return params
}

const saveForm = async () => {
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
    } else {
      paramsResult.auxiliaryType = 0
      paramsResult.feeShare = null
    }
    if (props.mode == 'create') {
      await TravelReimbApi.createTravelReimb({ ...paramsResult, submitFlag: 0 })
      message.success(t('common.createSuccess'))
      close()
      emit('handleSuccess', 'success')
    } else {
      await TravelReimbApi.updateTravelReimb({ ...paramsResult, submitFlag: 0 })
      message.success(t('common.updateSuccess'))
      close()
      emit('handleSuccess', 'success')
    }
  } finally {
    loading.value = false
  }
}
const FeeShareFormComRef = ref()
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
        await TravelReimbApi.createTravelReimb({ ...paramsResult, submitFlag: 1 })
        message.success('提交成功')
        close()
        emit('handleSuccess', 'success')
      } else {
        await TravelReimbApi.updateTravelReimb({ ...paramsResult, submitFlag: 1 })
        message.success('提交成功')
        close()
        emit('handleSuccess', 'success')
      }
    } else {
      const list = await batchGetFeeShare({
        codes: TravelListComRef.value?.tableData.map((item) => item.code).join(','),
        type: 1,
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
      await TravelReimbApi.createTravelReimb({ ...paramsResult, submitFlag: 1 })
      message.success('提交成功')
      close()
      emit('handleSuccess', 'success')
    } else {
      await TravelReimbApi.updateTravelReimb({ ...paramsResult, submitFlag: 1 })
      message.success('提交成功')
      close()
      emit('handleSuccess', 'success')
    }
  } finally {
    loading.value = false
  }
}

const getBankInfo = async (userId) => {
  //根据传入的用户ID获取银行账户信息，如果没有传入则使用当前登录用户
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

const getApplyFlag = async () => {
  const flag = await getConfigKey('oa.travel.apply')
  explainSchemas[explainSchemas.length - 1].disabled = !Number(flag)
}

onMounted(async () => {
  formRef.value.resetForm()
  getBankInfo()
  if (props.mode === 'edit') {
    await getApplyFlag()
    let res = await TravelReimbApi.getTravelReimb(props.id)
    if (isValidArray(res.trafficFeeList)) {
      formData.reimbItems.push('交通费')
    }
    if (isValidArray(res.accommodationFeeList)) {
      formData.reimbItems.push('住宿费')
    }
    if (isValidArray(res.selfDrivingList)) {
      formData.reimbItems.push('自驾')
    }
    if (isValidArray(res.travelAllowanceList)) {
      formData.reimbItems.push('出差补贴')
    }
    if (isValidArray(res.otherDescList)) {
      formData.reimbItems.push('过路费/其他')
    }
    TravelListComRef.value?.init(res.applyList)
    Object.assign(formData, { ...res, actualUserId: res.actualUser?.userId })
  } else {
    await getApplyFlag()
    if (props.homeCard) {
      formData.invoiceList = props.homeCard?.invoice
      formData.reimbItems.push('过路费/其他')
      formData.invoiceHolderId = props.homeCard?.id
      formData.otherDescList = [
        {
          expenseAmount: props.homeCard?.reimbAmount,
          expenseCurrency: 'RMB',
          invoiceAmount: props.homeCard?.invoiceAmount
        }
      ]
    }
    if (props.params) {
      formData.companyId = props.params.companyId
      formData.companyName = props.params.companyName
      //formData.reimbList = props.params.reimbList
      TravelListComRef.value?.init(props.params.reimbList)
    }
  }
})
</script>
