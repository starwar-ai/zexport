<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :mode="mode"
    :loading="loading"
    v-model="formData"
    :saveAction="{
      permi: 'scm:purchase-contract:create',
      handler: () => submitForm()
    }"
    :submitAction="{
      permi: 'scm:purchase-contract:submit',
      handler: () => submitForm('submit')
    }"
    :closeAction="{
      handler: close
    }"
  >
    <eplus-form-items
      title="基本信息"
      :formSchemas="basicSchemas"
    >
      <template #applyer>
        <el-input
          :value="formData?.applyer?.nickname || ''"
          disabled
        />
      </template>
      <template #applyDeptName>
        <el-input
          :value="formData?.applyer?.deptName || ''"
          disabled
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="应付供应商信息"
      :formSchemas="payableSchemas"
    />
    <eplus-form-items
      title="付款计划信息"
      :formSchemas="paymentPlanSchemas"
    >
      <template #applyPaymentPlanList>
        <eplus-form-table
          ref="TableRef"
          :list="formData?.applyPaymentPlanList || []"
          :defaultVal="{}"
          :schema="applyPaymentPlanTableColumns"
        />
      </template>
    </eplus-form-items>
    <el-tabs
      v-model="activeName"
      class="demo-tabs mb20px"
      @tab-change="handleTabChange"
    >
      <el-tab-pane
        label="采购明细"
        name="first"
      >
        <eplus-form-items
          title=""
          :formSchemas="purchaseItemSchemas"
        >
          <template #applyerPurchaseItemList>
            <PaymentPurchaseItem
              ref="childrenRef"
              type="payment"
              :formData="formData"
              @apply-amount-change="paymentPurchaseChange"
            />
          </template>
        </eplus-form-items>
      </el-tab-pane>
      <el-tab-pane
        label="加减项"
        name="second"
      >
        <eplus-form-items
          title="加减项"
          :formSchemas="addSubItemListSchemas"
        >
          <template #purchaseAddSubTermList>
            <AddSubItemTable
              v-if="!loading"
              :formData="formData"
              ref="addSubItemListRef"
              :required="true"
              type="payment"
              @add-sub-item-change="addSubItemChange"
            />
          </template>
        </eplus-form-items>
      </el-tab-pane>
    </el-tabs>
  </eplus-form>

  <PaymentPlanListDia ref="PaymentPlanListDiaRef" />
</template>
<script setup lang="tsx">
import { EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { DICT_TYPE, getDictLabel, getDictValue } from '@/utils/dict'
import * as PurchaseContractApi from '@/api/scm/purchaseContract'
import PaymentPurchaseItem from './PaymentPurchaseItem.vue'
import * as PaymentApi from '@/api/finance/payment'
import AddSubItemTable from './AddSubItemTable.vue'
import { formatNum, formatterPrice, formatTime } from '@/utils/index'
import { cloneDeep } from 'lodash-es'
import PaymentPlanListDia from './PaymentPlanListDia.vue'
import { moneyInputPrecision, moneyTotalPrecision } from '@/utils/config'
import { truncate } from 'node:fs'

const message = useMessage()

const loading = ref(true)
const activeName = ref('first')

const TableRef = ref()
const props = defineProps<{
  id?: number | any
  mode: EplusFormMode
  step?: string
}>()
const { close } = inject('dialogEmits') as {
  close: () => void
}
let formData = reactive({
  applyPaymentPlanList: [],
  applyerPurchaseItemList: [],
  goodsTotalAmount: [],
  purchaseAddSubTermList: [],
  step: undefined
})
const formRef = ref()
const childrenRef = ref()
const addSubItemListRef = ref()
const handleTabChange = (val) => {
  nextTick(() => {
    if (val.props.name === 'second') {
      addSubItemListRef.value.setScorllBar()
    }
  })
}
const addItemVal = ref(0)
const initApplyTotalAmount = ref(0)
const paymentPurchaseChange = (list, flag = false) => {
  //货款总金额计算
  let totalList = list.map((item: any) => {
    return item.applyAmount
  })
  let totalNewAmount = totalList.reduce((prev: any, cur: any) => Number(prev) + Number(cur), 0)
  // formData.goodsTotalAmount = formatterPrice(totalNewAmount, moneyInputPrecision)
  formData.applyTotalAmount = formatterPrice(
    Number(totalNewAmount) + Number(addItemVal.value),
    moneyInputPrecision
  )
  if (flag) {
    // initApplyTotalAmount 应该只包含货款总金额，不包含加减项
    initApplyTotalAmount.value = formatterPrice(totalNewAmount, moneyInputPrecision)
  }
}

const addSubItemChange = (val) => {
  addItemVal.value = formatterPrice(Number(val), moneyInputPrecision)
  formData.subAddTotalAmount = addItemVal.value
  // 申请总金额 = 货款总金额（initApplyTotalAmount） + 加减项总金额
  formData.applyTotalAmount = formatterPrice(
    Number(initApplyTotalAmount.value) + Number(formData.subAddTotalAmount),
    moneyInputPrecision
  )
}
// 限制筛选日期
const disabledDate = (time: Date) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

const paymentMarkTypeFilter = (arr) => {
  let isInspection = formData?.applyerPurchaseItemList.some(
    (item: any) => item.inspectionTime == null
  )
  let isDeparture = formData?.applyerPurchaseItemList.some(
    (item: any) => item.estDepartureTime == null
  )
  if (isInspection && isDeparture) {
    return arr.filter((item) => item.value === 1)
  } else if (!isInspection && !isDeparture) {
    return arr
  } else if (!isInspection) {
    return arr.filter((item) => item.value === 1 || item.value === 2)
  } else {
    return arr
  }
}

let basicSchemas: EplusFormSchema[] = reactive([
  {
    field: 'code',
    label: '申请单编号',
    disabled: props.mode === 'create',
    component: <el-input disabled />
  },
  {
    field: 'companyName',
    label: '付款主体',
    editable: true,
    component: <el-input disabled />
  },
  {
    field: 'applyer',
    label: '申请人',
    editable: true
  },
  {
    field: 'applyDeptName',
    label: '申请人部门',
    editable: true
  },
  {
    field: 'applyDate',
    label: '申请日期',
    editable: true,
    component: (
      <el-date-picker
        clearable
        valueFormat="x"
        type="date"
        style="width: 100%"
        disabled
      />
    )
  },
  {
    field: 'step',
    label: '申请类型',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.PAYMENT_TYPE}
        style="width:100%"
        disabled
      />
    )
  },
  // {
  //   field: 'printFlag',
  //   label: '打印状态',
  //   editable: true,
  //   component: (
  // <eplus-dict-select
  //   dictType={DICT_TYPE.PRINT_FLAG}
  //   style="width:100%"
  // />
  //   ),
  //
  // },
  {
    field: 'applyTotalAmount',
    label: '申请总金额',
    editable: true,
    component: <el-input disabled />
  },
  {
    field: 'subAddTotalAmount',
    label: '加减项总金额',
    component: <el-input disabled />
  },
  {
    field: 'goodsTotalAmount',
    label: '货款总金额',
    editable: true,
    component: <el-input disabled />
  },
  {
    field: 'applyPaymentDate',
    label: '申请付款日',
    editable: true,
    component: (
      <el-date-picker
        clearable
        valueFormat="x"
        type="date"
        disabled-date={disabledDate}
        style="width: 100%"
      />
    ),
    rules: [
      {
        required: true,
        message: '申请付款日不能为空',
        validator: (rule: any, value: any, callback: any) => {
          if (!formData.applyPaymentDate) {
            message.warning('请选择申请付款日')
            callback(new Error('请选择申请付款日'))
          } else {
            callback()
          }
        }
      }
    ]
  },
  {
    field: 'paymentMethod',
    label: '付款方式',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.PAY_METHOD}
        style="width:100%"
        onChangeEmit={(val) => paymentMethodChange(val)}
      />
    ),
    rules: {
      required: true,
      message: '付款方式不能为空'
    }
  },
  {
    field: 'acceptanceDays',
    label: '承兑天数',
    disabled: true,
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.ACCEPTANCE_DAYS}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '承兑天数不能为空'
    }
  },
  {
    field: 'paymentMarkType',
    label: '付款类型',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.PAYMENT_MARK_TYPE}
        filter={paymentMarkTypeFilter}
      />
    ),
    rules: {
      required: true,
      message: '付款类型不能为空'
    }
  },
  {
    field: 'remark',
    label: '申请备注',
    editable: true,
    component: <el-input type="textarea" />,
    span: 12
  }
])

const paymentMethodChange = (val) => {
  const item: any = basicSchemas.find((item) => item.field === 'acceptanceDays')
  item.disabled = getDictLabel(DICT_TYPE.PAY_METHOD, val) !== '承兑汇票'
}

let payableSchemas: EplusFormSchema[] = reactive([
  {
    field: 'venderName',
    label: '应付供应商',
    editable: true,
    component: <el-input disabled />
  },
  {
    field: 'venderCode',
    label: '供应商编码',
    editable: true,
    component: <el-input disabled />
  },
  {
    field: 'currency',
    label: '币别',
    editable: true,
    component: <el-input disabled />
  },
  {
    field: 'paymentName',
    label: '付款方式',
    editable: true,
    component: <el-input disabled />
  },
  {
    field: 'taxRate',
    label: '税率',
    editable: true,
    component: <el-input disabled />
  },
  {
    field: 'venderBankAccount',
    label: '银行账号',
    editable: true,
    component: <el-input disabled />
  },
  {
    field: 'venderBank',
    label: '开户行',
    editable: true,
    component: <el-input disabled />
  }
])
let paymentPlanSchemas: EplusFormSchema[] = reactive([
  {
    field: 'applyPaymentPlanList',
    label: '',
    editable: true,
    span: 24,
    labelWidth: '0px',
    slotName: 'applyPaymentPlanList'
  }
])

const PaymentPlanListDiaRef = ref()
let applyPaymentPlanTableColumns = reactive([
  {
    field: 'contractCode',
    label: '采购合同号',
    minWidth: '140px'
  },
  {
    field: 'paymentName',
    label: '付款方式',
    minWidth: '150px'
  },
  {
    field: 'step',
    label: '步骤',
    minWidth: '140px',
    formatter: (item, row, index) => {
      return <>{getDictLabel(DICT_TYPE.COLLECTION_PLAN_STEP, row?.step) || '-'}</>
    }
  },
  {
    field: 'paymentRatio',
    label: '计划付款比例(%)',
    minWidth: '100px'
  },
  // {
  //   field: 'actualRatio',
  //   label: '实际付款比例(%)',
  //   minWidth: '100px',
  //   formatter: (item, row, index) => {
  //     let myTotal = row?.receivableAmount?.amount / (row?.paymentRatio / 100)
  //     if (row?.appliedAmount && row?.appliedAmount?.amount) {
  //       return <>{Number((row?.appliedAmount?.amount * 100) / myTotal).toFixed(2) || '-'}</>
  //     } else {
  //       return '-'
  //     }
  //   }
  // },
  {
    field: 'receivableAmount',
    label: '应付金额',
    minWidth: '145px',
    formatter: (item, row, index) => {
      return (
        <>
          {row?.receivableAmount?.amount
            ? formatNum(row?.receivableAmount?.amount, moneyTotalPrecision)
            : '-'}
        </>
      )
    }
  },
  {
    field: 'expectedReceiptDate',
    label: '计划付款日期',
    minWidth: '175px',
    formatter: (item, row, index) => {
      return (
        <>{row?.expectedReceiptDate ? formatTime(row?.expectedReceiptDate, 'yyyy-MM-dd') : '-'}</>
      )
    }
  },
  {
    field: 'appliedAmount',
    label: '货款已申请金额',
    width: '150px',
    formatter: (item, row, index) => {
      return <>{row?.appliedAmount?.amount || '-'}</>
    }
  },
  {
    field: 'action',
    label: '操作',
    width: '100px',
    fixed: 'right',
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-button
          link
          type="primary"
          onClick={async () => {
            PaymentPlanListDiaRef.value.open(row.contractCode)
          }}
        >
          付款计划
        </el-button>
      )
    }
  }
])

// const get

const purchaseItemSchemas: EplusFormSchema[] = [
  {
    field: 'applyerPurchaseItemList',
    label: '',
    labelWidth: '0px',
    span: 24
  }
]
//加减项
const addSubItemListSchemas: EplusFormSchema[] = [
  {
    field: 'purchaseAddSubTermList',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px'
  }
]
const getParams = async (type) => {
  let valid = await formRef.value?.validate()
  if (!valid) return false
  let params = cloneDeep(formData)

  if (childrenRef.value) {
    let flag = childrenRef.value.checkData()
    if (flag) {
      message.warning('采购明细中存在本次请款金额为0的数据')
      return false
    } else {
      params.applyerPurchaseItemList = childrenRef.value.tableList
    }
  } else {
    params.applyerPurchaseItemList = []
  }
  let purchaseAddSubTermList = await addSubItemListRef.value.checkData()
  params.purchaseAddSubTermList = purchaseAddSubTermList?.map((item) => {
    return {
      ...item,
      amount: { amount: formatterPrice(item.amountFormat), currency: params?.currency || null }
    }
  })

  //付款计划信息 添加applyAmount 字段
  params.applyPaymentPlanList.forEach((item: any) => {
    item.applyAmount = 0
    params.applyerPurchaseItemList.forEach((el: any) => {
      if (item.contractCode === el?.purchaseContractCode) {
        item.applyAmount += Number(el.applyAmount) || 0
      }
    })
  })
  params.subAddTotalAmount = {
    amount: formatterPrice(params.subAddTotalAmount, moneyInputPrecision),
    currency: params?.currency || null
  }
  return { submitFlag: type === 'submit' ? 1 : 0, ...params }
}
const emit = defineEmits(['success'])
const submitForm = async (type?) => {
  try {
    let res = await getParams(type)
    if (!res) return
    if (props.mode === 'create') {
      let result = await PaymentApi.createPayment(res)
      message.notifyPushSuccess('转付款申请单成功', result, 'purchase-payment-apply')
    } else if (props.mode === 'edit') {
      await PaymentApi.updatePaymentApply(res)
      message.success('操作成功')
    }
    close()
    emit('success')
  } catch (e: any) {
    console.log(e)
  }
}
provide('formData', formData)

function getMinDate(dateStrings) {
  let minDate = null
  for (const dateStr of dateStrings) {
    const currentDate = new Date(dateStr)
    // 跳过无效日期
    if (isNaN(currentDate.getTime())) continue
    // 如果minDate为空或当前日期更小，则更新
    if (minDate === null || currentDate < minDate) {
      minDate = currentDate
    }
  }
  return new Date(minDate).getTime()
}

onMounted(async () => {
  try {
    loading.value = true

    if (props.mode === 'create') {
      let res = await PurchaseContractApi.getPayment({
        step: props.step === 'prepay' ? 1 : props.step === 'payable' ? 2 : undefined,
        idList: props?.id?.join(',')
      })
      // res?.applyerPurchaseItemList.forEach((item) => {
      //   item.applyAmount = item.applyAmount ? Number(item.applyAmount).toFixed(3) : 0
      //   item.maxApplyAmount = item.applyAmount
      // })
      //付款方式默认转账
      if (!res.paymentMethod) {
        res.paymentMethod = Number.parseInt(getDictValue(DICT_TYPE.PAY_METHOD, '转账'))
      }
      res.applyTotalAmount = formatterPrice(res?.applyTotalAmount || 0, moneyInputPrecision)

      res.goodsTotalAmount = formatterPrice(res.goodsTotalAmount || 0, moneyInputPrecision)
      //申请付款日取计划付款信息最小值
      if (!res.applyPaymentDate) {
        const expectedReceiptDateList: any = res?.applyPaymentPlanList
          .filter((item) => {
            return item.expectedReceiptDate
          })
          .map((it) => {
            return it.expectedReceiptDate
          })
        res.applyPaymentDate = getMinDate(expectedReceiptDateList)
      }
      Object.assign(formData, res)
      // 初始化 initApplyTotalAmount，用于加减项计算
      // initApplyTotalAmount 应该只包含货款总金额，不包含加减项
      initApplyTotalAmount.value = formatterPrice(
        Number(res.applyTotalAmount || 0),
        moneyInputPrecision
      )
      // 如果已有加减项金额，需要初始化 addItemVal
      if (res.subAddTotalAmount) {
        addItemVal.value = formatterPrice(Number(res.subAddTotalAmount), moneyInputPrecision)
      }
    } else if (props.mode === 'edit') {
      let res = await PaymentApi.getPaymentApply(props?.id)
      Object.assign(formData, {
        ...res
      })
      // 初始化 initApplyTotalAmount，用于加减项计算
      // initApplyTotalAmount 应该只包含货款总金额，不包含加减项
      initApplyTotalAmount.value = formatterPrice(
        Number(res.applyTotalAmount || 0),
        moneyInputPrecision
      )
      // 如果已有加减项金额，需要初始化 addItemVal
      if (res.subAddTotalAmount) {
        addItemVal.value = formatterPrice(Number(res.subAddTotalAmount), moneyInputPrecision)
      }
      paymentPurchaseChange(res.applyerPurchaseItemList, true)

      paymentMethodChange(res.paymentMethod)
    }
  } catch (e) {
    console.log(e, 'err')
  } finally {
    loading.value = false
  }
})
</script>
<style scoped lang="scss"></style>
