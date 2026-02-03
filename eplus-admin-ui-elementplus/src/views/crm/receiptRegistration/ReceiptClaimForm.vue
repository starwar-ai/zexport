<template>
  <eplus-form
    v-if="isShow"
    ref="formRef"
    v-bind="{ id: id, loading: loading, mode: mode }"
    v-model="formData"
    :saveAction="{
      permi: 'fms:cust-claim:create',
      handler: () => openConfirmDialog()
    }"
    scrollFlag
  >
    <template #pageTop>
      <eplus-form-items
        title="收款登记信息"
        :formSchemas="receiptRegistrationSchemas"
      />

      <eplus-form-items
        title="收款对象信息"
        :formSchemas="receiptItemSchemas"
      >
        <template #payeeEntityList>
          <ReceiptItemCom
            mode="edit"
            :formData="formData"
            ref="payeeEntityListRef"
            @update="updateCustClaimTotalAmountObj"
          />
        </template>
      </eplus-form-items>
    </template>
    <template #pageBottomTabs>
      <el-tabs v-model="activeName">
        <el-tab-pane
          label="登记入账信息"
          name="0"
        />

        <el-tab-pane
          label="其他收费"
          name="1"
        />
      </el-tabs>
    </template>
    <template #pageBottom>
      <el-scrollbar always>
        <div v-show="activeName === '0'">
          <eplus-form-table
            ref="custClaimItemRef"
            selectionFlag
            :list="formData?.custClaimItemList"
            :defaultVal="{
              completedFlag: undefined
            }"
            :schema="tableColumns"
            @selection-change="handleSelectionChange"
            :maxHeight="tableMaxHeight - 54"
          />
        </div>
        <div v-show="activeName === '1'">
          <OtherFee
            ref="OtherFeeRef"
            :formData="formData"
            @update="updateOtherFeeList"
          />
        </div>
      </el-scrollbar>
    </template>
  </eplus-form>
  <ReceiptDialog
    ref="ReceiptDialogRef"
    @success="dialogSuccess"
  />
  <ConfirmDialog
    ref="ConfirmDialogRef"
    @success="confirmSuccess"
  />
</template>
<script setup lang="tsx">
import { EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import * as CustClaimApi from '@/api/finance/custClaim'
import { cloneDeep } from 'lodash-es'
import ReceiptItemCom from './components/ReceiptItemCom.vue'
import ReceiptDialog from './components/ReceiptDialog.vue'
import ConfirmDialog from './components/ConfirmDialog.vue'
import OtherFee from './components/OtherFee.vue'
import { useRateStore } from '@/store/modules/rate'
import { convertNum, formatterPrice, formatTime } from '@/utils/index'
import { isValidArray } from '@/utils/is'
import EplusMoneyLabel from '@/components/EplusMoney/src/EplusMoneyLabel.vue'
import * as ConfigApi from '@/api/infra/config'
import { moneyTotalPrecision } from '@/utils/config'
import EplusNumInput from '@/components/EplusMoney/src/EplusNumInput.vue'

const rateList = ref({})
const message = useMessage()
const formRef = ref()
const loading = ref(false)
const isShow = ref(false)
const ReceiptDialogRef = ref()
const ConfirmDialogRef = ref()
const custClaimItemRef = ref()
const dialogSuccess = (val) => {
  formData.custClaimItemList[val.indexRef.value - 1].differenceReason = val.tableList
  formData.custClaimItemList[val.indexRef.value - 1].differenceAmount = val.differenceAmount
  // console.log(formData?.custClaimItemList[val.indexRef].differenceReason, 'val')
}

const activeName = ref('0')

const confirmSuccess = async (val) => {
  submitForm()
}
const openReceiptDialog = (index, row) => {
  ReceiptDialogRef.value.open(index, row)
}
const openConfirmDialog = async () => {
  let valid = await custClaimItemRef.value.validate()
  let otherValid = await OtherFeeRef.value.checkData()
  if (!valid && !otherValid) {
    message.warning('登记入账信息或其他收费提交信息有误')
    return false
  }
  // let otherFeeValid = await OtherFeeRef.value.checkData()
  // if (!otherFeeValid) return false

  let claimedTotalAmount: any = Object.values(custClaimTotalAmountObj).reduce(
    (acc, curr: any) => acc + curr,
    0
  )
  let differenceAmount = seletedList.value.reduce((acc, curr: any) => {
    return acc + Number(curr.differenceAmount)
  }, 0)
  let financeAmount = seletedList.value.reduce((acc, curr: any) => {
    return acc + Number(curr.financeAmount.amount)
  }, 0)
  claimedTotalAmount = claimedTotalAmount - differenceAmount - financeAmount
  if (claimedTotalAmount === 0) {
    message.warning('请选择登记入账信息或添加其他收费信息')
    return
  } else if (
    convertNum(formatterPrice(claimedTotalAmount, moneyTotalPrecision)) >
    convertNum(formatterPrice(formData.unclaimedAmount, moneyTotalPrecision))
  ) {
    message.warning('本次认领金额不能大于待认领金额')
    return
  }
  if (isValidArray(seletedList.value)) {
    for (let i = 0; i < seletedList.value.length; i++) {
      let item: any = seletedList.value[i]
      if (!item.disabledFlag && (item.completedFlag == undefined || item.completedFlag == null)) {
        message.warning('收款完成标识不能为空')
        return false
      }
    }
  }

  let result: any = await getParams()
  if (result) {
    ConfirmDialogRef.value.open(result.payeeEntityList)
  }
}
const props = defineProps<{
  id?: number
  mode: EplusFormMode
  param?
}>()
const payeeEntityListRef = ref()
const { close } = inject('dialogEmits') as {
  close: () => void
}
interface FormData {
  code: string
  otherFeeList: any[] | undefined
  custClaimItemList: any[] | undefined
  unclaimedAmount: string | number
  payeeEntityList: any[] | undefined
  currency: string
}
let formData = reactive<FormData>({
  code: '',
  otherFeeList: [],
  custClaimItemList: [],
  payeeEntityList: [],
  unclaimedAmount: '',
  currency: ''
})
provide('formData', formData)
let seletedList = ref([])

const handleSelectionChange = (val) => {
  seletedList.value = val
  let childIndexList = val.map((item) => {
    return `${item.id}${item.itemId}${item.invoiceCode}`
  })
  formData?.custClaimItemList?.map((item) => {
    if (childIndexList.includes(`${item.id}${item.itemId}${item.invoiceCode}`)) {
      item.disabledFlag = false
      item.completedFlag = item.completedFlag != null ? item.completedFlag : null
      item.claimedAmount = item.claimedAmount || 0
      item.differenceAmount = getDifferenceAmount(item)
    } else {
      item.differenceReason = null
      item.completedFlag = undefined
      item.claimedAmount = undefined
      item.differenceAmount = getDifferenceAmount(item)
      item.contractAmount = ''
      item.financeAmount = { amount: '', currency: item.currency }
      item.disabledFlag = true
    }
  })
}

watch(
  () => formData?.custClaimItemList,
  (list) => {
    setCustClaimTotalAmount(list, formData.otherFeeList)
  },
  {
    deep: true
  }
)

const OtherFeeRef = ref()
const updateCustClaimTotalAmountObj = (obj, list) => {
  custClaimTotalAmountObj = obj
  OtherFeeRef.value?.setCustList(list)
}

const updateOtherFeeList = (list) => {
  formData.otherFeeList = list
  setCustClaimTotalAmount(formData.custClaimItemList, list)
}

const setCustClaimTotalAmount = (list1, list2) => {
  let custCodeList = Object.keys(custClaimTotalAmountObj)
  custCodeList.forEach((item) => {
    custClaimTotalAmountObj[item] = 0
  })
  let list = list1.concat(list2)
  list.forEach((item) => {
    if (item.claimedAmount > 0 && custCodeList.includes(item.custCode)) {
      custClaimTotalAmountObj[item.custCode] +=
        Number(item.claimedAmount) +
        Number(item.differenceAmount) +
        Number(item.financeAmount.amount)
    }
  })
  list1.forEach((item) => {
    rateFormat(Number(item.claimedAmount), item)
  })
  if (payeeEntityListRef.value) {
    payeeEntityListRef.value.setAmount(custClaimTotalAmountObj)
  }
}
const freeOfFinanceUsdRef = ref(100)

const getParams = async () => {
  let valid = await formRef.value.validate()
  if (!valid) return false
  let params: any = cloneDeep(formData)
  if (payeeEntityListRef.value) {
    let result = await payeeEntityListRef.value?.saveForm()
    if (result) {
      let checkEmpty = result
        .filter((item) => {
          return item.payeeName !== '' && convertNum(item.claimTotalAmount) > 0
        })
        .map((el) => {
          return {
            ...el,
            claimTotalAmount: convertNum(el.claimTotalAmount)
          }
        })
      params.payeeEntityList = checkEmpty
    } else {
      params.payeeEntityList = []
      message.warning('收款对象信息提交有误')
      return false
    }
  }
  params.custClaimItemList = params.custClaimItemList
    .filter((item) => {
      return item.disabledFlag === false
    })
    .map((item) => {
      const { id, ...res } = item
      return { ...res, type: 0 }
    })
  //财务费用大于100美金，差异原因必填
  if (params.custClaimItemList?.length) {
    const config = await ConfigApi.getConfigSimplelist({})
    if (config?.length) {
      let freeOfFinanceUsdConfig = config.filter((item) => item.category == 'freeOf_financeUsd')
      if (freeOfFinanceUsdConfig?.length) {
        freeOfFinanceUsdRef.value = freeOfFinanceUsdConfig[0].value
      }
    }
    let financeValidateArr = params.custClaimItemList?.filter((item) => {
      if (item.financeAmount.amount) {
        let financeAmountUSD =
          (Number(item.financeAmount.amount) *
            Number(rateList.value[item.financeAmount.currency])) /
          Number(rateList.value['USD'])
        return Number(financeAmountUSD) > freeOfFinanceUsdRef.value && !item.differenceAmount
      }
    })
    if (financeValidateArr?.length) {
      message.warning(
        '存在财务费用大于' + freeOfFinanceUsdRef.value + '美金，但是未填写差异原因记录信息！'
      )
      return false
    }
  }
  if (OtherFeeRef.value) {
    // eslint-disable-next-line no-unsafe-optional-chaining
    params.custClaimItemList = [...params.custClaimItemList, ...OtherFeeRef.value?.tableList]
  }
  return params
}
let receiptRegistrationSchemas: EplusFormSchema[] = reactive([
  {
    field: 'companyTitle',
    label: '付款抬头',
    readOnly: true
  },
  {
    field: 'companyName',
    label: '入账单位',
    readOnly: true
  },
  {
    field: 'bank',
    label: '入账银行',
    readOnly: true
  },
  {
    field: 'currency',
    label: '入账币种',
    readOnly: true
  },
  {
    field: 'amount',
    label: '入账金额',
    readOnly: true
  },
  {
    field: 'unclaimedAmount',
    label: '待认领金额',
    readOnly: true
  },
  {
    field: 'registeredByer',
    label: '登记人',
    readOnly: true
  },
  {
    field: 'remark',
    label: '备注',
    readOnly: true
  }
])
const receiptItemSchemas: EplusFormSchema[] = reactive([
  {
    field: 'payeeEntityList',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
])

const getMaxVal = (row) => {
  let maxVal =
    Number(row.receivableAmount) - Number(row.receivedAmount) - Number(row.differenceAmount)
  let rateExchange = rateList.value[row.currency]
  let formDataRate = rateList.value[formData.currency]
  if (formDataRate && rateExchange) {
    maxVal = (Number(maxVal * rateExchange) / Number(formDataRate)).toFixed(3)
  }
  return maxVal
}

const rateFormat = (val, row) => {
  let rateExchange = rateList.value[row.currency]
  let formDataRate = rateList.value[formData.currency]
  if (formDataRate && rateExchange) {
    row.contractAmount = formatterPrice(
      Number(val * formDataRate) / Number(rateExchange),
      moneyTotalPrecision
    )
  }

  // row.differenceAmount = (
  //   Number(row.contractAmount) -
  //   (Number(row.receivableAmount) - Number(row.receivedAmount))
  // ).toFixed(2)
}
const tableColumns = reactive([
  {
    field: 'contractCode',
    label: '订单合同号',
    width: '150px',
    showOverflowTooltip: false,
    fixed: 'left'
  },
  {
    field: 'custName',
    label: '客户名称',
    width: '150px',
    showOverflowTooltip: false,
    fixed: 'left'
  },
  {
    field: 'settlementName',
    label: '收款方式',
    width: '150px',
    showOverflowTooltip: false
  },
  {
    field: 'source',
    label: '来源',
    width: '150px',
    showOverflowTooltip: false
  },
  // {
  //   field: 'invoiceCode',
  //   label: '出运发票号',
  //   width: '150px',
  //   showOverflowTooltip: false
  // },
  {
    field: 'currency',
    label: '订单币种',
    width: '150px',
    showOverflowTooltip: false
  },
  {
    field: 'receivableAmount',
    label: '应收金额',
    width: '150px',
    component: (
      <EplusNumInput
        precision={moneyTotalPrecision}
        class="!w-90%"
        disabled
      />
    ),
    showOverflowTooltip: false
  },
  {
    field: 'receivedAmount',
    label: '已收金额',
    width: '150px',
    component: (
      <EplusNumInput
        precision={moneyTotalPrecision}
        class="!w-90%"
        disabled
      />
    ),
    showOverflowTooltip: false
  },
  {
    field: 'claimedAmount',
    label: '本次入账币种认领金额',
    width: '180px',
    showOverflowTooltip: false,
    slot: (item, row: Recordable, index: number) => {
      return (
        <EplusNumInput
          disabled={row?.disabledFlag}
          v-model={row.claimedAmount}
          min={0}
          max={getMaxVal(row)}
          precision={moneyTotalPrecision}
          class="!w-90%"
          onChange={() => {
            getFinanceAmount(row)
          }}
        />
      )
    },
    rules: [
      {
        validator: (rule: any, value: any, callback: any) => {
          if (typeof value === 'number' && value <= 0) {
            callback(new Error(`本次入账币种认领金额必须大于0`))
          } else {
            callback()
          }
        }
      }
    ]
  },
  {
    field: 'contractAmount',
    label: '订单币种认领金额',
    width: '150px',
    showOverflowTooltip: false,
    component: (
      <EplusNumInput
        precision={3}
        class="!w-90%"
        disabled
      />
    )
  },
  {
    field: 'completedFlag',
    label: '收款完成',
    width: '150px',
    showOverflowTooltip: false,
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.completedFlag}
          disabled={row?.disabledFlag}
          dictType={DICT_TYPE.CONFIRM_TYPE}
          style="width:100%"
          clearable={false}
          onChangeEmit={() => {
            getFinanceAmount(row)
          }}
        />
      )
    },
    rules: [
      {
        validator: (rule: any, value: any, callback: any) => {
          if (value === null) {
            callback(new Error(`请选择是否收款完成`))
          } else {
            callback()
          }
        }
      }
    ]
  },
  {
    field: 'differenceAmount',
    label: '差异总金额',
    width: '150px',
    showOverflowTooltip: false,
    component: (
      <EplusNumInput
        disabled
        controls={false}
        precision={moneyTotalPrecision}
        class="!w-90%"
      />
    )
  },
  {
    field: 'remark',
    label: '备注',
    width: '150px',
    component: <el-input class="!w-90%" />
  },
  {
    label: '差异原因',
    width: 100,
    fixed: 'right',
    slot: (item, row: Recordable, index: number) => {
      return (
        <>
          {!row?.disabledFlag ? (
            <el-button
              link
              type="primary"
              onClick={() => {
                openReceiptDialog(row?.index, row)
              }}
            >
              录入
            </el-button>
          ) : (
            ''
          )}
        </>
      )
    }
  },
  {
    field: 'financeAmount',
    label: '财务费用',
    width: 150,
    fixed: 'right',
    slot: (item, row: Recordable, index: number) => {
      return <EplusMoneyLabel val={row.financeAmount} />
    }
  }
])

const getFinanceAmount = (row) => {
  if (row.completedFlag) {
    row.financeAmount = {
      amount:
        Number(formatterPrice(row.receivableAmount, moneyTotalPrecision)) -
        Number(row.receivedAmount || 0) -
        Number(row.contractAmount || 0) -
        Number(row.differenceAmount || 0),
      currency: row.currency
    }
  } else {
    row.financeAmount = { amount: 0, currency: row.currency }
  }
}

let custClaimTotalAmountObj = reactive({})
const getDifferenceAmount = (row) => {
  if (isValidArray(row.differenceReason)) {
    return row.differenceReason?.reduce((pre, cur) => {
      return pre + cur.differenceAmount
    }, 0)
  } else if (isValidArray(row.hisDifferenceReason)) {
    return row.hisDifferenceReason?.reduce((pre, cur) => {
      return pre + cur.differenceAmount
    }, 0)
  } else {
    return 0
  }
}
const tableMaxHeight = ref(0)
onMounted(async () => {
  try {
    let res = await CustClaimApi.getCustClaim({ id: props?.id })
    const resultFormat = cloneDeep(res)

    let rateRes = await CustClaimApi.getDateRate(formatTime(res.bankPostingDate, 'yyyy.MM.dd'))
    if (isValidArray(rateRes)) {
      rateRes.forEach((item) => {
        rateList.value[item.dailyCurrName] = item.dailyCurrRate
      })
    } else {
      rateList.value = useRateStore().rateList
    }

    resultFormat?.custClaimItemList?.forEach((item, index) => {
      item.index = index + 1
      item.disabledFlag = true
      item.financeAmount = {
        amount: 0,
        currency: item.currency
      }
      item.differenceAmount = getDifferenceAmount(item)
    })
    resultFormat.payeeEntityList = resultFormat?.payeeEntityList ? resultFormat.payeeEntityList : []
    resultFormat.payeeEntityList.forEach((item) => {
      custClaimTotalAmountObj[item.payeeCode] = Number(item.claimTotalAmount) || 0
    })
    resultFormat.registeredByer = res?.registeredBy?.nickname
    Object.assign(formData, resultFormat)

    await nextTick()
    isShow.value = true
    setTimeout(() => {
      tableMaxHeight.value = formRef.value.bottomHeight
    }, 1000)
  } catch (err) {
    console.log(err, 'err')
  } finally {
  }
})
const submitForm = async () => {
  try {
    let result: any = await getParams()
    try {
      // openConfirmDialog(result)
      await CustClaimApi.saveCustClaim(result)
      message.success('提交成功')
      close()
    } catch (err) {
      console.log(err, 'err')
    }
  } finally {
    loading.value = false
  }
}
</script>
<style scoped lang="scss"></style>
