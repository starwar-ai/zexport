<template>
  <eplus-form-table
    ref="TableRef"
    prop="paymentList"
    :schema="tableSchema"
    :list="formData.paymentList || []"
    :max="3"
  />
</template>
<script setup lang="tsx">
import type { EplusAuditable } from '@/types/eplus'
import { EplusFormTable, EplusFormTableSchema } from '@/components/EplusTemplate'
import { usePaymentStore } from '@/store/modules/payment'

defineOptions({ name: 'PaymentTable' })
const props = defineProps<{
  required: boolean
}>()

const TableRef = ref()
const formData = inject('formData') as EplusAuditable
const tableList = computed(() => {
  return formData.value.paymentList
})
const paymentListData = usePaymentStore()
const tableSchema: EplusFormTableSchema[] = [
  {
    label: '序号',
    field: 'index',
    fixed: 'left',
    width: 80
  },
  {
    field: 'paymentId',
    label: '付款方式',
    width: '250px',
    component: (
      <el-select
        v-model={formData.paymentList}
        clearable
        ref="paymentListRef"
        style="width: 100%"
      >
        {paymentListData.paymentList.list.map((item: any, index) => {
          return (
            <el-option
              v-for={item in paymentListData.paymentList.list}
              key={item.id}
              label={item.name}
              value={item.id}
            />
          )
        })}
      </el-select>
    ),
    rules: [
      {
        required: true,
        validator: (rule: any, value: any, callback: any) => {
          let valueArr = TableRef.value.tableData.map((item) => item.paymentId)
          valueArr.splice(
            valueArr.findIndex((el) => el == value),
            1
          )
          if (valueArr.includes(value)) {
            callback(new Error('此付款方式已存在'))
          } else if (!value) {
            callback(new Error('请选择付款方式'))
          } else {
            callback()
          }
        }
      }
    ]
  },
  {
    field: 'defaultFlag',
    label: '默认',

    width: '120px',
    slot: (item, row, index) => {
      return (
        <div onClick={() => setDefault(index, formData.value.paymentList)}>
          <el-button
            style="width:10px;height:10px;"
            type={row.defaultFlag == 1 ? 'primary' : ''}
            circle
            size="small"
          ></el-button>
        </div>
      )
    }
  },
  {
    field: 'action',
    label: '操作',
    width: '80px',

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

const message = useMessage()

const saveForm = async (temporary) => {
  let valid = await TableRef.value.validate()
  if (valid || temporary) {
    return TableRef.value.tableData
  } else {
    message.warning('付款方式提交信息有误')
    return false
  }
}
defineExpose({ saveForm, tableList })
//设置为默认
const setDefault = (scope, d) => {
  for (let i = 0; i < d.length; i++) {
    d[i].defaultFlag = scope == i ? 1 : 0
  }
}

onMounted(async () => {
  watch(
    () => formData.value,
    async () => {
      if (formData.value?.paymentList?.length) {
        TableRef.value.tableData = formData.value.paymentList
      }
    }
  )
  if (TableRef.value?.tableData?.length === 1) {
    TableRef.value.tableData[0].defaultFlag = 1
  }
})
</script>
