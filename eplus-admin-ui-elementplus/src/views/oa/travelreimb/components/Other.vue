<template>
  <eplus-form-table
    ref="TableRef"
    :list="formData?.otherDescList"
    :schema="tableSchema"
    :defaultVal="defaultVal"
  />
  <!-- <div class="total_box">
    <div> 总计 </div>
    <div class="pr15px"> 其他报销金额：<TotalLayout :list="OtherAmountList" /> </div>
  </div> -->

  <!-- <el-button @click="saveForm">保存</el-button> -->
  <InvoiceDia
    ref="InvoiceDiaRef"
    @sure="invoiceSure"
  />
</template>
<script setup lang="tsx">
import { EplusNumInput } from '@/components/EplusMoney'
import { EplusFormTable, EplusFormTableSchema } from '@/components/EplusTemplate'
import { getTotal } from '@/utils/index'
import UploadList from '@/components/UploadList/index.vue'
import { useFeeStore } from '@/store/modules/fee'
import { moneyInputPrecision } from '@/utils/config'
import { columnWidth } from '@/utils/table'

const feeList = useFeeStore().feeList
const dictSubjectList = feeList.filter((item) => item.showFeeFlag != 1)
const feeDescList = feeList.filter((item) => item.showDescFlag == 1)
defineOptions({ name: 'Other' })
const props = defineProps<{
  formData?: any
}>()
const message = useMessage()
const TableRef = ref()
const defaultVal = { expenseCurrency: props.formData.currency, invoice: [], picture: [] }

watch(
  () => props.formData?.currency,
  (newVal) => {
    if (newVal) {
      defaultVal.expenseCurrency = newVal
      TableRef.value.tableData = TableRef.value.tableData.map((item) => {
        item.expenseCurrency = newVal
        return item
      })
    }
  }
)

const InvoiceDiaRef = ref()

const selectInvoice = (list, index) => {
  InvoiceDiaRef.value.open(list || [], index)
}

const invoiceSure = (list, amountToTal, index, dictSubjectId) => {
  let row = props.formData?.otherDescList[index]
  row.invoice = list
  row.invoiceAmount = row.invoiceAmount ? row.invoiceAmount : Number(amountToTal)
  row.dictSubjectId = row.dictSubjectId ? row.dictSubjectId : Number(dictSubjectId)
}
const dictSubjectListChange = (row) => {
  row.expenseUseToFormalId = row.dictSubjectId
}
const tableSchema: EplusFormTableSchema[] = [
  {
    label: '序号',
    field: 'index',
    fixed: 'left',
    minWidth: columnWidth.m
  },
  {
    label: '说明',
    field: 'otherDesc',
    minWidth: columnWidth.m,
    component: <el-input class="!w-90%" />,
    rules: [{ required: true, message: '请输入备注' }]
  },
  {
    label: '发票名称',
    field: 'dictSubjectId',
    minWidth: columnWidth.m,
    hint: (
      <el-table
        data={feeDescList}
        size="small"
        max-height="450"
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
    slot: (
      item: EplusFormTableSchema,
      row: Recordable,
      index: number,
      deleteAction: (index: number) => void
    ) => {
      return (
        <>
          <eplus-input-select
            v-model={row.dictSubjectId}
            dataList={dictSubjectList}
            label="feeName"
            value="id"
            class="!w-90%"
            onChange={dictSubjectListChange(row)}
          />
        </>
      )
    },
    rules: { required: true, message: '请选择发票名称' }
  },
  // {
  //   label: '费用名称(实际)',
  //   field: 'expenseUseToFormalId',
  //   width: 150,
  //   component: (
  //     <eplus-input-select
  //       dataList={feeList}
  //       label="feeName"
  //       value="id"
  //       class="!w-90%"
  //     />
  //   ),
  //   rules: { required: true, message: '请选择费用名称(实际)' }
  // },
  {
    label: '发票金额',
    field: 'invoiceAmount',
    minWidth: columnWidth.m,
    component: (
      <EplusNumInput
        min={0}
        controls={false}
        precision={moneyInputPrecision}
        class="!w-90%"
      />
    ),
    rules: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          let regx = new RegExp(`^(\\d{1,11})(\\.?\\d{1,${moneyInputPrecision}})?$`)
          if (value <= 0) {
            callback(new Error(`金额必须大于0`))
          } else if (!regx.test(value)) {
            callback(new Error(`金额只能输入正数,最多11位整数,${moneyInputPrecision}位小数`))
          } else {
            callback()
          }
        }
      }
    ]
  },

  {
    label: '报销金额',
    field: 'expenseAmount',
    minWidth: columnWidth.m,
    slot: (
      item: EplusFormTableSchema,
      row: Recordable,
      index: number,
      deleteAction: (index: number) => void
    ) => {
      return (
        <>
          <EplusNumInput
            v-model={row.expenseAmount}
            min={0}
            precision={moneyInputPrecision}
            class="!w-90%"
          />
        </>
      )
    },
    rules: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          let regx = new RegExp(`^(\\d{1,11})(\\.?\\d{1,${moneyInputPrecision}})?$`)
          if (value <= 0) {
            callback(new Error(`金额必须大于0`))
          } else if (!regx.test(value)) {
            callback(new Error(`金额只能输入正数,最多11位整数,${moneyInputPrecision}位小数`))
          } else {
            callback()
          }
        }
      }
    ]
  },
  {
    label: '发票附件',
    field: 'invoice',
    width: columnWidth.xxl,
    slot: (
      item: EplusFormTableSchema,
      row: Recordable,
      index: number,
      deleteAction: (index: number) => void
    ) => {
      return (
        <div style={{ width: '100%' }}>
          <UploadList
            v-model={row.invoice}
            fileList={row.invoice}
            isLabelHide={true}
            onSuccess={(params) => {
              if (params.length > 0) {
                row.invoice = params
              }
            }}
          />
          <el-button
            link
            type="primary"
            onClick={() => selectInvoice(row.invoice, index)}
          >
            票夹子
          </el-button>
        </div>
      )
    }
  },
  {
    label: '支付截图',
    field: 'picture',
    width: columnWidth.xxl,
    slot: (
      item: EplusFormTableSchema,
      row: Recordable,
      index: number,
      deleteAction: (index: number) => void
    ) => {
      return (
        <div style={{ width: '100%' }}>
          <UploadList
            v-model={row.picture}
            fileList={row.picture}
            onSuccess={(params) => {
              if (params.length > 0) {
                row.picture = params
              }
            }}
          />
        </div>
      )
    }
  },
  {
    label: '备注',
    field: 'remark',
    width: columnWidth.xxl,
    component: (
      <el-input
        autosize
        type="textarea"
      />
    )
  },
  {
    label: '操作',
    width: columnWidth.m,
    fixed: 'right',
    slot: (
      item: EplusFormTableSchema,
      row: Recordable,
      index: number,
      deleteAction: (index: number) => void
    ) => {
      return (
        <>
          <el-button
            link
            type="primary"
            onClick={() => deleteAction(index)}
          >
            移除
          </el-button>
        </>
      )
    }
  }
]

const OtherAmountList = ref([])
const OtherinvoiceAmountList = ref([])
watch(
  () => TableRef.value?.tableData,
  (list) => {
    if (list) {
      OtherAmountList.value = getTotal(list)
      OtherinvoiceAmountList.value = getTotal(list, 'invoiceAmount', 'expenseCurrency')
    }
  },
  { immediate: true, deep: true }
)

const saveForm = async () => {
  let valid = await TableRef.value.validate()
  for (let index = 0; index < TableRef.value.tableData.length; index++) {
    const item = TableRef.value.tableData[index]
    // 附件必填校验：发票附件或支付截图二选一
    const hasInvoice = Array.isArray(item.invoice) && item.invoice.length > 0
    const hasPicture = Array.isArray(item.picture) && item.picture.length > 0
    if (!hasInvoice && !hasPicture) {
      message.warning(`其他信息第${index + 1}条数据,请上传发票附件或支付截图（二选一）`)
      return false
    }
  }
  if (valid) {
    return TableRef.value.tableData.map((item) => {
      return {
        ...item,
        expenseType: 5
      }
    })
  } else {
    message.warning('其他信息提交信息有误')
    return false
  }
}

defineExpose({ saveForm, OtherAmountList, OtherinvoiceAmountList })

onMounted(async () => {})
</script>
<style lang="scss" scoped>
.total_box {
  width: 100%;
  display: flex;
  justify-content: space-between;
  height: 40px;
  align-items: center;
}
</style>
