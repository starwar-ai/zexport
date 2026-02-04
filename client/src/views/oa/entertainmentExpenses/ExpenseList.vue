<template>
  <eplus-form-table
    ref="TableRef"
    :list="formData?.reimbDetailList"
    :schema="tableSchema"
    :defaultVal="defaultVal"
  />
  <!-- <div class="total_box">
    <div> 总计 </div>
    <div class="pr15px">
      <span class="pr20px">发票金额：<TotalLayout :list="invoiceAmountList" /></span>
      <span>报销金额：<TotalLayout :list="AmountList" /></span>
    </div>
  </div> -->
  <InvoiceDia
    ref="InvoiceDiaRef"
    @sure="invoiceSure"
  />
</template>
<script setup lang="tsx">
import { DICT_TYPE } from '@/utils/dict'
import { EplusFormTable, EplusFormTableSchema } from '@/components/EplusTemplate'
import { getTotal } from '@/utils/index'
import UploadList from '@/components/UploadList/index.vue'
import { getSubject } from '@/api/common/index'
import { useFeeStore } from '@/store/modules/fee'
import { moneyInputPrecision } from '@/utils/config'
import { EplusNumInput } from '@/components/EplusMoney'
import { columnWidth } from '@/utils/table'

const feeList = useFeeStore().feeList
const dictSubjectList = feeList.filter((item) => item.showFeeFlag != 1)
const feeDescList = feeList.filter((item) => item.showDescFlag == 1)
defineOptions({ name: 'ExpenseList' })
const props = defineProps<{
  formData?: any
}>()

const message = useMessage()
const TableRef = ref()
const defaultVal = {
  expenseCurrency: props.formData.currency,
  invoice: [],
  picture: []
}

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
const expenseTypeChange = async (e, row, type) => {
  let res = await getSubject({
    systemDictType: type,
    systemDictDataValue: e
  })
  if (res) {
    row.feeName = res[0]?.feeName || ''
    row.dictSubjectId = res[0]?.id || ''
    row.financialSubjectId = res[0]?.subjectId || ''
    row.financialSubjectName = res[0]?.subjectName || ''
  }
}

// const getDes = (type) => {
//   let expenseTypeVal = getDictLabel(DICT_TYPE.ENTERTAIN_EXPENSE_TYPE, type),
//     entertainLevelval = getDictLabel(DICT_TYPE.LEVEL_TYPE, formData?.entertainLevel)
//   if (expenseTypeVal == '餐饮费' && entertainLevelval == '普通') {
//     return '食堂便餐或简餐150元/人以内'
//   } else if (expenseTypeVal == '餐饮费' && entertainLevelval == '重要') {
//     return '200元/人以内或2000元/桌以内'
//   } else if (expenseTypeVal == '餐饮费' && entertainLevelval == '特级') {
//     return '400元/人以内或4000元/桌以内'
//   } else if (expenseTypeVal == '水果食品费' && entertainLevelval == '普通') {
//     return '50元以内'
//   } else if (expenseTypeVal == '水果食品费' && entertainLevelval == '重要') {
//     return '200元以内'
//   } else if (expenseTypeVal == '水果食品费' && entertainLevelval == '特级') {
//     return '300元以内'
//   } else if (expenseTypeVal == '礼品费' && entertainLevelval == '普通') {
//     return '人均300元以内'
//   } else if (expenseTypeVal == '礼品费' && entertainLevelval == '重要') {
//     return '人均500元以内'
//   } else if (expenseTypeVal == '礼品费' && entertainLevelval == '特级') {
//     return '人均1000元以内'
//   } else {
//     return ''
//   }
// }
const dictSubjectListChange = (row) => {
  row.feeName = ''
  row.expenseUseToFormalId = row.dictSubjectId
}
const InvoiceDiaRef = ref()
const selectInvoice = (list, index) => {
  InvoiceDiaRef.value.open(list || [], index)
}

const invoiceSure = (list, amountToTal, index, dictSubjectId) => {
  let row = props.formData?.reimbDetailList[index]
  row.invoice = list
  row.invoiceAmount = row.invoiceAmount ? row.invoiceAmount : Number(amountToTal)
  row.dictSubjectId = row.dictSubjectId ? row.dictSubjectId : Number(dictSubjectId)
}
const tableSchema: EplusFormTableSchema[] = [
  {
    label: '序号',
    field: 'index',
    fixed: 'left',
    width: 80
  },
  {
    label: '类别',
    field: 'expenseType',
    width: 150,
    slot: (
      item: EplusFormTableSchema,
      row: Recordable,
      index: number,
      deleteAction: (index: number) => void
    ) => {
      return (
        <>
          <eplus-dict-select
            v-model={row.expenseType}
            style={'width:150px'}
            class="mr8px"
            dictType={DICT_TYPE.ENTERTAIN_EXPENSE_TYPE}
            onChangeEmit={(e) => expenseTypeChange(e, row, DICT_TYPE.ENTERTAIN_EXPENSE_TYPE)}
          />
        </>
      )
    },
    rules: { required: true, message: '请选择类别' }
  },
  {
    label: '发票名称',
    field: 'dictSubjectId',
    width: 150,
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
  //   width: '220px',
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
    width: 150,
    component: (
      <EplusNumInput
        min={0}
        precision={moneyInputPrecision}
        class="!w-90%"
      />
    ),
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        let regx = new RegExp(`^(\\d{1,11})(\\.?\\d{1,${moneyInputPrecision}})?$`)
        if (value <= 0) {
          callback(new Error(`发票金额必须大于0`))
        } else if (!regx.test(value)) {
          callback(new Error(`金额只能输入正数,最多11位整数,${moneyInputPrecision}位小数`))
        } else {
          callback()
        }
      }
    }
  },

  {
    label: '报销金额',
    field: 'expenseAmount',
    width: 150,
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
          let regx = /^(\d{1,11})(\.?\d{1,2})?$/
          if (value <= 0) {
            callback(new Error(`金额必须大于0`))
          } else if (!regx.test(value)) {
            callback(new Error('金额只能输入正数,最多11位整数,2位小数'))
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
    width: 150,
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
    width: 150,
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
    // rules: {
    //   required: true,
    //   validator: (rule: any, value: any, callback: any) => {
    //     callback()
    //   }
    // }
  },
  {
    label: '备注',
    field: 'remark',
    width: 150,
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
const invoiceAmountList = ref([])
const AmountList = ref([])
watch(
  () => TableRef.value?.tableData,
  (list) => {
    if (list) {
      invoiceAmountList.value = getTotal(list, 'invoiceAmount')
      AmountList.value = getTotal(list)
    }
  },
  { immediate: true, deep: true }
)

const saveForm = async () => {
  let valid = await TableRef.value?.validate()
  for (let index = 0; index < TableRef.value.tableData.length; index++) {
    const item = TableRef.value.tableData[index]
    if (Number(item.expenseAmount) > Number(item.invoiceAmount)) {
      message.warning(`费用小项信息第${index + 1}条数据 报销金额大于发票金额`)
      return false
    }
    // 附件必填校验：发票附件或支付截图二选一
    const hasInvoice = Array.isArray(item.invoice) && item.invoice.length > 0
    const hasPicture = Array.isArray(item.picture) && item.picture.length > 0
    if (!hasInvoice && !hasPicture) {
      message.warning(`费用小项信息第${index + 1}条数据,请上传发票附件或支付截图（二选一）`)
      return false
    }
  }
  if (valid) {
    return TableRef.value?.tableData
  } else {
    message.warning('费用小项提交信息有误')
    return false
  }
}

defineExpose({ saveForm, AmountList, invoiceAmountList })
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
