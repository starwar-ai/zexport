<template>
  <div style="margin-bottom: 10px; font-weight: bold">
    <span>查询选项</span>
  </div>
  <el-row
    :gutter="30"
    class="mb-10px"
  >
    <el-col :span="4">
      <el-input
        v-model="invoiceCodeRef"
        placeholder="出运发票号"
        @change="invoiceCodeChange"
        clearable
      />
    </el-col>
    <el-col :span="4">
      <eplus-input-select
        v-if="invoiceCodeRef"
        v-model="venderCodeRef"
        :data-list="venderList"
        keyword="name"
        label="name"
        value="code"
        class="!w-100%"
        placeholder="请选择供应商"
        @change-emit="(...$event) => getVenderName($event)"
      />
      <eplus-input-search-select
        v-else
        v-model="venderCodeRef"
        :api="getVenderSimpleList"
        :params="{ pageSize: 100, pageNo: 1 }"
        keyword="name"
        label="name"
        value="code"
        class="!w-100%"
        placeholder="请选择供应商"
        @change-emit="(...$event) => getVenderName($event)"
      />
    </el-col>
    <el-col :span="4">
      <el-input
        v-model="purchaseContractCode"
        placeholder="采购合同号"
        clearable
      />
    </el-col>
    <el-col :span="4">
      <el-button
        type="primary"
        @click="getPage"
      >
        查询明细
      </el-button>
    </el-col>
  </el-row>
  <eplus-form
    ref="formRef"
    :id="id"
    :loading="loading"
    :mode="mode"
    v-model="formData"
  >
    <eplus-form-items
      title="登记明细"
      :formSchemas="collectionSchemas"
    >
      <template #children>
        <eplus-form-table
          ref="childrenRef"
          selectionFlag
          :defaultVal="{}"
          :list="formData?.children"
          :schema="tableColumns"
          @selection-change="handleSelectionChange"
          :selectable="setSelectable"
          :max-height="600"
        />
      </template>
    </eplus-form-items>
    <eplus-form-items
      title="发票信息"
      :formSchemas="purchaseSchemas"
    />
    <eplus-form-items
      title="附件信息"
      :formSchemas="annexSchemas"
    >
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
import { EplusFormMode, EplusFormSchema } from '@/components/EplusTemplate'
import * as RegistrationApi from '@/api/scm/registration'
import { TableColumn } from '@/types/table'
import { columnWidth } from '@/utils/table'
import UploadList from '@/components/UploadList/index.vue'
import { getVenderSimpleList } from '@/api/common'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import { getDictLabel } from '@/utils/dict'

const emit = defineEmits(['handleSuccess', 'sucess'])
const message = useMessage()

const checkedItems = ref<any[]>([])
const formRef = ref()
const childrenRef = ref()
const props = defineProps<{
  id?: number
  mode: EplusFormMode
}>()
const { close } = inject('dialogEmits') as {
  close: () => void
}
const loading = ref(false)
const formData = reactive({
  purchaseUserId: '',
  purchaseUserDeptName: '',
  remark: '',
  annex: [],
  custName: '',
  custCode: '',
  custId: '',
  children: [],
  companyId: '',
  companyName: '',
  venderCode: '',
  venderName: '',
  taxType: null,
  invoiceAmount: ''
})
const venderList = ref([])
const invoiceCodeRef = ref('')
const venderCodeRef = ref('')
const purchaseContractCode = ref('')

const total = ref(0)
provide('formData', formData)
const { updateDialogActions } = inject('dialogActions') as {
  updateDialogActions: (...args: any[]) => void
}

const invoiceCodeChange = async () => {
  if (invoiceCodeRef.value) {
    venderList.value = await RegistrationApi.getVenderList(invoiceCodeRef.value)
    if (!venderList.value.map((item) => item.code).includes(venderCodeRef.value)) {
      venderCodeRef.value = ''
    }
  }
}

const getVenderName = (e) => {
  if (e[0] && e[1].length > 0) {
    let item = e[1].find((item) => item.code === e[0])
    formData.venderCode = item.code
    formData.venderName = item.name
  }
}

const setSelectable = (row) => {
  return row.selectable
}

let purchaseSchemas = [
  {
    field: 'companyName',
    label: '付款主体',
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
    field: 'invoiceAmount',
    label: '发票总金额',
    editable: true,
    component: (
      <el-input-number
        disabled
        controls={false}
        precision={3}
        class="!w-100%"
      />
    )
  },
  {
    field: 'invoiceCode',
    label: '税票编号',
    editable: true,
    component: <el-input placeholder="请输入税票号" />,
    rules: [
      {
        required: true,
        message: '请输入税票号'
      }
    ]
  }
  // {
  //   field: 'invoicedDate',
  //   label: '收票日期',
  //   editable: true,
  //   component: (
  //     <el-date-picker
  //       type="date"
  //       placeholder="请选择收票日期"
  //       valueFormat="x"
  //       class="!w-100%"
  //     />
  //   ),
  //   rules: [
  //     {
  //       required: true,
  //       trigger: 'change',
  //       message: '请选择收票日期'
  //     }
  //   ]
  // }
]

const collectionSchemas: EplusFormSchema[] = [
  {
    field: 'children',
    label: '',
    labelWidth: '0px',
    editable: true,
    span: 24,
    rules: [
      {
        required: true,
        trigger: 'click',
        message: '请核对相关登记明细'
      }
    ]
  }
]
let tableColumns = reactive<TableColumn[]>([
  {
    label: '序号',
    field: 'indexnum',
    fixed: 'left',
    width: 80,
    formatter: (item, row, index) => {
      return index + 1
    }
  },
  {
    field: 'purchaseContractCode',
    label: '采购合同号',
    width: columnWidth.l
  },
  {
    field: 'venderName',
    label: '供应商名称',
    width: columnWidth.l
  },
  {
    field: 'shipInvoiceCode',
    label: '出运发票号',
    width: columnWidth.l
  },
  {
    field: 'invoicThisQuantity',
    label: '本次登票数',
    width: columnWidth.l
    // slot: (item, row, index) => {
    //   if (row?.invoicNoticesQuantity) {
    //     return (
    //       <el-input-number
    //         v-model={row.invoicThisQuantity}
    //         max={row?.invoicNoticesQuantity - row?.inveicRegisteredQuantity}
    //         min={1}
    //         controls={false}
    //         disabled
    //         onChange={() => {
    //           if (row?.invoicThisQuantity) {
    //             row.invoicPrice = Number(row?.invoicThisQuantity) * Number(row?.invoicPrice)
    //             setInvoiceAmount()
    //           }
    //         }}
    //       />
    //     )
    //   }
    // }
  },
  {
    field: 'declarationQuantity',
    label: '数量',
    width: columnWidth.l
  },
  {
    field: 'hsMeasureUnit',
    label: '单位',
    width: columnWidth.l,
    slot: (item, row, index) => {
      return <span>{getDictLabel(DICT_TYPE.HS_MEASURE_UNIT, row.hsMeasureUnit)}</span>
    }
  },
  {
    field: 'invoicPrice',
    label: '开票单价',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row.invoicPrice ? (
        <EplusMoneyLabel
          val={{
            amount: row.invoicPrice,
            currency: row.purchaseCurrency
          }}
        />
      ) : (
        '0'
      )
    }
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: columnWidth.l
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: columnWidth.l
  },
  {
    field: 'skuName',
    label: '产品品名',
    width: columnWidth.l
  },
  {
    field: 'invoicSkuName',
    label: '开票品名',
    width: columnWidth.l
  },
  {
    field: 'invoicThisPrice',
    label: '本次开票金额',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row.invoicThisPrice ? (
        <EplusMoneyLabel
          val={{
            amount: row.invoicThisPrice,
            currency: row.purchaseCurrency
          }}
        />
      ) : (
        '0'
      )
    }
  },
  {
    field: 'invoicNoticesQuantity',
    label: '通知开票数量',
    width: columnWidth.l
  },
  {
    field: 'inveicRegisteredQuantity',
    label: '已登票数',
    width: columnWidth.l
  },
  {
    field: 'companyName',
    label: '付款主体',
    width: columnWidth.l
  },
  {
    field: 'skuCode',
    label: '产品编号',
    width: columnWidth.l
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: columnWidth.l
  },
  // {
  //   field: 'qtyPerInnerbox',
  //   label: '已开票金额'
  // },
  {
    field: 'purchaseTotalQuantity',
    label: '总采购数量',
    width: columnWidth.l
  },
  {
    field: 'purchaseWithTaxPrice',
    label: '采购含税单价',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
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
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row.purchaseWithTaxPrice?.amount && row.purchaseTotalQuantity ? (
        <EplusMoneyLabel
          val={{
            amount: row.purchaseTotalQuantity * row.purchaseWithTaxPrice?.amount || 0,
            currency: row.purchaseWithTaxPrice?.currency
          }}
        />
      ) : (
        '0'
      )
    }
  },
  // {
  //   field: 'qtyPerInnerbox',
  //   label: '跟单员姓名'
  // },
  {
    field: 'saleContractCode',
    label: '关联销售合同号',
    width: columnWidth.l
  },
  {
    field: 'hsCode',
    label: 'HS编码',
    width: columnWidth.l
  },
  {
    field: 'venderCode',
    label: '供应商编号',
    width: columnWidth.l
  }
])

const annexSchemas: EplusFormSchema[] = [
  {
    field: 'annex',
    label: '',
    span: 24,
    editable: true,
    labelWidth: '0px'
  }
]
//附件信息
const getFileList = (params) => {
  formData.annex = params
}
const handleSelectionChange = (val) => {
  if (val?.length) {
    checkedItems.value = val
    //全选操作，存在不同主体记录信息不可全选
    if (val?.length == formData.children.length) {
      var cId = val[0].companyId
      var temp = val.filter((item) => {
        return item.companyId == cId
      })
      if (temp.length != val.length) {
        ElMessage.warning('存在不同主体记录信息')
        childrenRef.value.handleAllToggleRowSelection()
        checkedItems.value = []
        return
      }
    }
  } else {
    checkedItems.value = []
  }
  //设置发票总金额
  setInvoiceAmount()
  //设置每一行可选状态，selectable为true可选, false不可选
  if (checkedItems.value.length) {
    formData.companyName = checkedItems.value[0].companyName
    formData.companyId = checkedItems.value[0].companyId
    formData.venderCode = checkedItems.value[0]?.venderCode
    formData.venderName = checkedItems.value[0]?.venderName
    formData.children.forEach((cld) => {
      cld.selectable = cld.companyId == formData.companyId && cld.venderCode == formData.venderCode
    })
  } else {
    formData.companyName = ''
    formData.companyId = ''
    formData.venderCode = ''
    formData.venderName = ''
    formData.children.forEach((cld) => {
      cld.selectable = true
    })
  }
}
const setInvoiceAmount = () => {
  let checkedItemsPrice = checkedItems.value.map((item) => {
    return item.invoicThisPrice
  })
  formData.invoiceAmount = sum(checkedItemsPrice)
}
const sum = (val) => {
  return val.reduce((a, b) => a + b, 0)
}
//处理提交前的formdata
const getParams = async (type?) => {
  let params: any = JSON.parse(JSON.stringify(formData))
  if (!checkedItems.value?.length) {
    message.warning('请先选择要登票的数据')
    return false
  }
  let totalPrice = checkedItems.value.map((item) => {
    return item.invoicThisPrice
  })
  if (Number(formData.invoiceAmount) !== sum(totalPrice)) {
    message.warning('发票总金额与已选本次登票金额总额不一致')
    return false
  }
  params.children = checkedItems.value.map((item) => {
    const { id, ...res } = item
    return {
      invoicingNoticesItemId: id,
      ...res
    }
  })
  if (!params?.annex) {
    params.annex = []
  }
  return {
    submitFlag: type === 'submit' ? 1 : 0,
    ...params
  }
}
const submitForm = async (type?) => {
  let valid = await formRef.value?.validate()
  if (!valid) return false
  loading.value = true
  let paramsResult = await getParams(type)
  if (paramsResult) {
    try {
      await RegistrationApi.saveRegistration(paramsResult)
      message.success('提交成功')
      formRef.value?.resetForm()
      invoiceCodeRef.value = ''
      venderCodeRef.value = ''
      purchaseContractCode.value = ''
    } catch (error) {
      message.error('提交失败')
    }
  }
}
const getPage = async () => {
  if (!venderCodeRef.value && !purchaseContractCode.value) {
    message.warning('请选择供应商或输入采购合同号，再进行查询')
    return false
  }
  let params = {
    venderCode: venderCodeRef.value,
    invoiceCode: invoiceCodeRef.value,
    purchaseContractCode: purchaseContractCode.value
  }
  let result = await RegistrationApi.getRegistrationItemList(params)
  // formData.venderCode = result.list[0]?.venderCode
  // formData.venderName = result.list[0]?.venderName
  total.value = result.total

  //设置checkbox可选择状态为true
  result.list.forEach((item) => {
    item.selectable = true
    item.invoicThisQuantity = item.invoicNoticesQuantity - item.inveicRegisteredQuantity
    item.invoicThisPrice = Number(item.invoicPrice) * item.invoicThisQuantity
  })
  formData.children = result.list
}
onMounted(() => {
  updateDialogActions(
    <el-button
      onClick={() => {
        submitForm('submit')
      }}
      key="handlerSure"
      type="primary"
    >
      确认登记
    </el-button>
  )
})
</script>
<style scoped lang="scss">
.table-container {
  width: 100%;
  background-color: rgb(245, 247, 249);
}

:deep(.el-dialog__body) {
  padding: '0px' !important;
}
</style>
