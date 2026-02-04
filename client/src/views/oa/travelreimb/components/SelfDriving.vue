<template>
  <eplus-form-table
    ref="TableRef"
    :list="formData?.selfDrivingList"
    :schema="tableSchema"
    :defaultVal="defaultVal"
  />
  <!-- <div class="total_box">
      <div> 总计 </div>
      <div class="pr15px"> 自驾报销金额：<TotalLayout :list="SelfDrivingAmountList" /> </div>
    </div> -->
  <InvoiceDia
    ref="InvoiceDiaRef"
    @sure="invoiceSure"
  />
</template>
<script setup lang="tsx">
import { columnWidth } from '@/utils/table'
import { EplusNumInput } from '@/components/EplusMoney'
import { EplusFormTable, EplusFormTableSchema } from '@/components/EplusTemplate'
import { getTotal } from '@/utils/index'
import UploadList from '@/components/UploadList/index.vue'
import * as TravelReimbApi from '@/api/oa/travelreimb'
import { useFeeStore } from '@/store/modules/fee'
import { moneyInputPrecision } from '@/utils/config'

const feeList = (useFeeStore().feeList || []) as any[]
const dictSubjectList = feeList.filter((item) => (item as any).showFeeFlag != 1)
const feeDescList = feeList.filter((item) => (item as any).showDescFlag == 1)
defineOptions({ name: 'SelfDriving' })
const message = useMessage()
const TableRef = ref()
const props = defineProps<{
  formData?: any
}>()
const kmPrice = ref(1)
const InvoiceDiaRef = ref()
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

const selectInvoice = (list, index) => {
  InvoiceDiaRef.value.open(list || [], index)
}

const invoiceSure = (list, amount, index) => {
  let row = props.formData?.selfDrivingList[index]
  row.invoice.push(...list)
  row.invoiceAmount = Number(row.invoiceAmount) + Number(amount)
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
    label: '线路说明',
    field: 'routeDesc',
    minWidth: columnWidth.m,
    component: <el-input class="!w-90%" />,
    rules: { required: true, message: '请输入线路说明' }
  },
  {
    label: '出发里程数（km）',
    field: 'mileageStart',
    minWidth: columnWidth.l,
    component: (
      <el-input-number
        min={0}
        controls={false}
        precision={0}
        class="!w-90%"
      />
    ),
    rules: {
      validator: (rule: any, value: any, callback: any) => {
        let regx = /^[1-9]\d*$/
        if (value) {
          if (!regx.test(value)) {
            callback(new Error('请输入大于0的整数'))
          } else {
            callback()
          }
        } else {
          callback()
        }
      }
    }
  },
  {
    label: '最终里程数（km）',
    field: 'mileageEnd',
    minWidth: columnWidth.l,
    component: (
      <el-input-number
        min={0}
        controls={false}
        precision={0}
        class="!w-90%"
      />
    ),
    rules: {
      validator: (rule: any, value: any, callback: any) => {
        let regx = /^[1-9]\d*$/
        if (value) {
          if (!regx.test(value)) {
            callback(new Error('请输入大于0的整数'))
          } else {
            callback()
          }
        } else {
          callback()
        }
      }
    }
  },
  {
    label: '花费里程数（km）',
    field: 'mileage',
    minWidth: columnWidth.l,
    slot: (
      item: EplusFormTableSchema,
      row: Recordable,
      index: number,
      deleteAction: (index: number) => void
    ) => {
      return (
        <>
          <el-input-number
            v-model={row.mileage}
            min={0}
            controls={false}
            precision={0}
            class="!w-90%"
            onChange={rowChange(row)}
          />
        </>
      )
    },
    rules: [
      { required: true, message: '请输入花费里程数' },
      {
        validator: (rule: any, value: any, callback: any) => {
          let regx = /^[1-9]\d*$/
          if (!regx.test(value)) {
            callback(new Error('请输入大于0的整数'))
          } else {
            callback()
          }
        }
      }
    ]
  },
  // {
  //   label: '过路费',
  //   field: 'tollFee',
  //   minWidth: columnWidth.m,
  //   slot: (
  //     item: EplusFormTableSchema,
  //     row: Recordable,
  //     index: number,
  //     deleteAction: (index: number) => void
  //   ) => {
  //     return (
  //       <>
  //         <EplusNumInput
  //           v-model={row.tollFee}
  //           min={0}
  //           precision={moneyTotalPrecision}
  //           class="!w-90%"
  //           onChange={rowChange(row)}
  //         />
  //       </>
  //     )
  //   }
  // },
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
          width="560"
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

const SelfDrivingAmountList = ref([])
const SelfDrivinginvoiceAmountList = ref([])
const rowChange = (item) => {
  if (item.mileageStart < item.mileageEnd) {
    item.mileage = Number(item.mileageEnd) - Number(item.mileageStart)
  }
  if (item.mileage) {
    item.expenseAmount = (item.mileage * kmPrice.value || 0) + (Number(item.tollFee) || 0)
  }
}
watch(
  () => TableRef.value?.tableData,
  (list) => {
    if (list) {
      SelfDrivingAmountList.value = getTotal(list)
      SelfDrivinginvoiceAmountList.value = getTotal(list, 'invoiceAmount', 'expenseCurrency')
    }
  },
  { immediate: true, deep: true }
)

const saveForm = async () => {
  let valid = await TableRef.value.validate()
  for (let index = 0; index < TableRef.value.tableData.length; index++) {
    const item = TableRef.value.tableData[index]
    if (Number(item.expenseAmount) > Number(item.invoiceAmount)) {
      message.warning(`自驾信息第${index + 1}条数据 报销金额大于发票金额`)
      return false
    }
    // 附件必填校验：发票附件或支付截图二选一
    const hasInvoice = Array.isArray(item.invoice) && item.invoice.length > 0
    const hasPicture = Array.isArray(item.picture) && item.picture.length > 0
    if (!hasInvoice && !hasPicture) {
      message.warning(`自驾信息第${index + 1}条数据,请上传发票附件或支付截图（二选一）`)
      return false
    }
  }
  if (valid) {
    return TableRef.value.tableData.map((item) => {
      return {
        ...item,
        expenseType: 3,
        expenseCurrency: props.formData.currency
      }
    })
  } else {
    message.warning('自驾信息提交信息有误')
    return false
  }
}

defineExpose({ saveForm, SelfDrivingAmountList, SelfDrivinginvoiceAmountList })

onMounted(async () => {
  kmPrice.value = await TravelReimbApi.getMileageSubsidy()
})
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
