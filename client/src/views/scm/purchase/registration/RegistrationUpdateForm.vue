<template>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
  >
    <eplus-form-items
      title="发票签收信息"
      :formSchemas="purchaseSchemas"
    >
      <!-- <template #amount>
        <EplusMoney
          v-model:amount="formData.amount.amount"
          v-model:currency="formData.amount.currency"
          @currency-change="handleCurrencyChange"
        />
      </template> -->
    </eplus-form-items>
    <eplus-form-items
      title="附件信息"
      :formSchemas="annexSchemas"
    >
      <template #annex>
        <UploadList
          ref="UploadListRef"
          :fileList="formData.annex"
          @success="
            (params) => {
              formData.annex = params
            }
          "
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="发票明细"
      :formSchemas="childrenSchemas"
    >
      <template #children>
        <Table
          :columns="registrationTableColumns"
          headerAlign="center"
          align="center"
          :data="formData?.children"
          showSummary
          sumText="合计"
          :summaryMethod="handleSummary"
          :max-height="430"
        />
      </template>
    </eplus-form-items>
  </eplus-form>
</template>
<script setup lang="tsx">
import { EplusForm, EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import { EplusAuditable } from '@/types/eplus'
import { DICT_TYPE } from '@/utils/dict'
import * as RegistrationApi from '@/api/scm/registration'
import { cloneDeep } from 'lodash-es'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import { formatDictColumn, formatMoneyColumn } from '@/utils/table'
import { formatNum, getCurrency } from '@/utils'
import { isAmount, isNumber, isWeight } from '@/utils/is'

const message = useMessage()
defineOptions({ name: 'RegistrationUpdateForm' })
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const loading = ref(false)

const formData: EplusAuditable = reactive({})
provide('formData', formData)

const { close } = inject('dialogEmits') as {
  close: () => void
}
const { updateDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
}

const purchaseSchemas = [
  {
    field: 'code',
    label: '单据编号',
    component: <el-input disabled />
  },
  {
    field: 'companyName',
    label: '付款单位',
    component: <el-input disabled />
  },
  {
    field: 'venderName',
    label: '供应商名称',
    component: <el-input disabled />
  },
  {
    field: 'venderCode',
    label: '供应商编号',
    component: <el-input disabled />
  },
  {
    field: 'invoiceCode',
    label: '发票编号',
    component: <el-input />,
    rules: {
      required: true,
      message: '请输入发票编号'
    }
  },
  {
    field: 'invoicedDate',
    label: '收票日期',
    component: (
      <el-date-picker
        type="date"
        disabled
      />
    )
  },
  {
    field: 'invoiceAmount',
    label: '发票总金额',
    component: <el-input disabled />
  }
]
const annexSchemas: EplusFormSchema[] = [
  {
    field: 'annex',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
]
const childrenSchemas: EplusFormSchema[] = [
  {
    field: 'children',
    label: '',
    span: 24,
    labelWidth: '0px'
  }
]
let registrationTableColumns = reactive([
  {
    label: '序号',
    type: 'index',
    fixed: 'left',
    width: 80
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同号'
  },
  {
    field: 'venderName',
    label: '供应商名称'
  },
  {
    field: 'shipInvoiceCode',
    label: '出运发票号'
  },
  {
    field: 'shippingQuantity',
    label: '数量'
  },
  {
    field: 'hsMeasureUnit',
    label: '单位',
    width: 120,
    formatter: formatDictColumn(DICT_TYPE.HS_MEASURE_UNIT)
  },
  {
    field: 'invoicPrice',
    label: '开票单价',
    formatter: formatMoneyColumn()
  },
  {
    field: 'invoicSkuName',
    label: '报关品名'
  },
  {
    field: 'invoicThisPrice',
    label: '本次开票金额',
    width: 120,
    formatter: formatMoneyColumn()
  },
  {
    field: 'invoicThisQuantity',
    label: '本次登票数'
  },
  {
    field: 'invoicNoticesQuantity',
    label: '通知开票数量',
    width: 120
  },
  {
    field: 'inveicRegisteredQuantity',
    label: '已登票数量'
  },
  {
    field: 'skuCode',
    label: '产品编号'
  },
  {
    field: 'cskuCode',
    label: '客户货号'
  },
  {
    field: 'purchaseTotalQuantity',
    label: '总采购数量'
  },
  {
    field: 'purchaseWithTaxPrice',
    label: '采购含税单价',
    width: '150',
    formatter: (row) => {
      return row.purchaseWithTaxPrice?.amount ? (
        <EplusMoneyLabel val={row.purchaseWithTaxPrice} />
      ) : (
        '0'
      )
    }
  },
  {
    field: 'purchaseTotalAmount',
    label: '采购总金额',
    width: '150',
    formatter: formatMoneyColumn()
  },
  {
    field: 'saleContractCode',
    label: '关联销售合同号',
    width: 120
  },
  {
    field: 'hsCode',
    label: 'HS编码'
  },
  {
    field: 'venderCode',
    label: '供应商编号'
  }
])
const formRef = ref()

const submitForm = async () => {
  // 校验表单
  if (formRef.value === null) return
  let valid = await formRef.value.validate()
  if (!valid) return
  try {
    await RegistrationApi.updateRegistration({
      ...formData,
      children: oldChildren.value,
      submitFlag: 1
    })
    message.success('提交成功')
    close()
  } catch (e) {
    loading.value = false
    return
  }
}
const oldChildren = ref([])
const getPageInfo = async () => {
  let res = await RegistrationApi.getRegistrationDetail({ id: props?.id })
  oldChildren.value = cloneDeep(res.children)
  res.children.forEach((item) => {
    item.invoicPrice = {
      amount: item.invoicPrice,
      currency: item.purchaseCurrency
    }

    item.invoicThisPrice = {
      amount:
        Number(item.invoicPrice.amount || item.invoicPrice) * Number(item.invoicNoticesQuantity),
      currency: item.purchaseCurrency
    }

    item.purchaseTotalAmount = {
      amount: item.purchaseTotalQuantity * item.purchaseWithTaxPrice?.amount || 0,
      currency: item.purchaseWithTaxPrice?.currency
    }
  })
  Object.assign(formData, res)
}

const handleSummary = (param) => {
  const { columns, data } = param
  const sums: (string | VNode)[] = []
  columns.forEach((column, index) => {
    if (index === 0) {
      sums[index] = '合计'
      return
    }
    const sumFileds = [
      'shippingQuantity',
      'invoicPrice',
      'invoicThisPrice',
      'invoicThisQuantity',
      'invoicNoticesQuantity',
      'inveicRegisteredQuantity',
      'purchaseTotalQuantity',
      'purchaseTotalAmount'
    ]
    let values = []
    if (sumFileds.includes(column.property) && isNumber(data[0][column.property])) {
      values = data.map((item) => Number(item[column.property]))
      let numVal = `${values.reduce((prev, curr) => {
        const value = Number(curr)
        if (!Number.isNaN(value)) {
          return prev + curr
        } else {
          return prev
        }
      }, 0)}`
      sums[index] = formatNum(numVal)
    } else if (sumFileds.includes(column.property) && isAmount(data[0][column.property])) {
      values = data.map((item) => Number(item[column.property].amount))
      let amountVal = `${values.reduce((prev, curr) => {
        const value = Number(curr)
        if (!Number.isNaN(value)) {
          return prev + curr
        } else {
          return prev
        }
      }, 0)}`
      sums[index] = `${getCurrency(data[0][column.property].currency)}${formatNum(amountVal)}`
    } else if (sumFileds.includes(column.property) && isWeight(data[0][column.property])) {
      values = data.map((item) => Number(item[column.property].weight))
      let weightVal = `${values.reduce((prev, curr) => {
        const value = Number(curr)
        if (!Number.isNaN(value)) {
          return prev + curr
        } else {
          return prev
        }
      }, 0)}`
      sums[index] = `${formatNum(weightVal)} ${data[0][column.property].unit}`
    } else {
      sums[index] = ''
    }
  })

  return sums
}

onMounted(async () => {
  await getPageInfo()
  updateDialogActions(<el-button onClick={submitForm}>提交</el-button>)
})
</script>
