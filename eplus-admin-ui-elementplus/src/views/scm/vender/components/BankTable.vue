<template>
  <eplus-form-table
    ref="TableRef"
    prop="bankaccountList"
    :schema="tableSchema"
    :list="formData.bankaccountList"
  />
</template>
<script setup lang="tsx">
import type { EplusAuditable } from '@/types/eplus'
import { EplusFormTable, EplusFormTableSchema } from '@/components/EplusTemplate'

defineOptions({ name: 'BankTable' })
const props = withDefaults(
  defineProps<{
    isRequired?: boolean
  }>(),
  { isRequired: true }
)
const TableRef = ref()
const formData = inject('formData') as EplusAuditable
const tableList = computed(() => {
  return formData.value.bankaccountList
})
const tableSchema: EplusFormTableSchema[] = [
  {
    label: '序号',
    field: 'index',
    fixed: 'left',
    width: 80
  },
  {
    field: 'bank',
    label: '开户行',
    component: <el-input />,
    rules: { required: props.isRequired, message: '请输入开户行' }
  },
  {
    field: 'bankAddress',
    label: '开户行地址',
    component: <el-input />
  },
  {
    field: 'bankPoc',
    label: '账户名称',
    component: <el-input />,
    rules: { required: props.isRequired, message: '请输入账户名称' }
  },
  {
    field: 'bankAccount',
    label: '账户号码',
    component: <el-input />,
    rules: { required: props.isRequired, message: '请输入账户号码' }
  },
  {
    field: 'bankCode',
    label: '银行行号',
    component: <el-input />
  },
  {
    field: 'defaultFlag',
    label: '默认',
    fixed: 'right',
    width: '80px',
    slot: (item, row, index) => {
      return (
        <div onClick={() => setDefault(index, formData.value.bankaccountList)}>
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
    label: '操作',
    width: '80px',
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

const message = useMessage()

const saveForm = async (temporary) => {
  let valid = await TableRef.value.validate()
  let dataList = []
  if (valid || temporary) {
    TableRef.value.tableData?.forEach((item) => {
      if (item.bank || item.bankAddress || item.bankPoc || item.bankAccount || item.bankCode) {
        dataList.push(item)
      }
    })
    return dataList
  } else {
    message.warning('银行账户提交信息有误')
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
      TableRef.value.tableData = formData.value.bankaccountList
    }
  )
  if (TableRef.value?.tableData?.length === 1) {
    TableRef.value.tableData[0].defaultFlag = 1
  }
})
</script>
