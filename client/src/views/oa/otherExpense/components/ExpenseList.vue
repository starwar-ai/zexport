<template>
  <eplus-form-table
    ref="TableRef"
    :list="formData?.reimbDetailList || []"
    :schema="tableSchema"
    :defaultVal="defaultVal"
  />
</template>
<script setup lang="tsx">
import { EplusFormTable, EplusFormTableSchema } from '@/components/EplusTemplate'
import { getTotal } from '@/utils/index'
import UploadList from '@/components/UploadList/index.vue'
import { isValidArray } from '@/utils/is'
import { moneyInputPrecision } from '@/utils/config'
import { EplusNumInput } from '@/components/EplusMoney'

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

const tableSchema: EplusFormTableSchema[] = [
  {
    label: '序号',
    field: 'index',
    fixed: 'left',
    width: 80
  },
  // {
  //   label: '申请事由',
  //   field: 'expenseUseToOccur',
  //   width: '220px',
  //   component: <el-input></el-input>,
  //   rules: { required: true, message: '请输入申请事由' }
  // },
  {
    label: '报销金额',
    field: 'expenseAmount',
    width: 180,
    slot: (item: EplusFormTableSchema, row: Recordable) => {
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
    label: '支付截图',
    field: 'picture',
    slot: (item: EplusFormTableSchema, row: Recordable) => {
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
    },
    rules: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          if (!isValidArray(value)) {
            callback(new Error(`请上传支付截图`))
          } else {
            callback()
          }
        }
      }
    ]
  },
  {
    label: '备注',
    field: 'remark',
    width: 180,
    component: (
      <el-input
        autosize
        type="textarea"
      />
    )
  },
  {
    label: '操作',
    width: 100,
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
const AmountList = ref([])
watch(
  () => TableRef.value?.tableData,
  (list) => {
    if (list) {
      AmountList.value = getTotal(list)
    }
  },
  { immediate: true, deep: true }
)

const saveForm = async () => {
  let valid = await TableRef.value?.validate()
  if (valid) {
    return TableRef.value?.tableData
  } else {
    message.warning('费用小项提交信息有误')
    return false
  }
}

defineExpose({ saveForm, AmountList })
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
