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
import { pick } from 'lodash-es'

defineOptions({ name: 'BankTable' })
const props = defineProps<{
  required: boolean
}>()
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
    rules: { required: false, message: '请输入开户行' }
  },
  {
    field: 'bankAddress',
    label: '开户行地址',
    component: <el-input />,
    rules: { required: false, message: '请输入开户行地址' }
  },
  {
    field: 'bankPoc',
    label: '账户名称',
    component: <el-input />,
    rules: { required: false, message: '请输入账户名称' }
  },
  {
    field: 'bankAccount',
    label: '账户号码',
    component: <el-input />,
    rules: { required: false, message: '请输入账户号码' }
  },
  {
    field: 'bankCode',
    label: '银行行号',
    component: <el-input />,
    rules: { required: false, message: '请输入银行行号' }
  },
  {
    field: 'defaultFlag',
    label: '默认',
    width: '80px',
    fixed: 'right',
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
    field: 'action',
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

//只要银行信息有输入，就必须填写完整
const checkCompleteData = () => {
  let checkResult
  let oldData = TableRef.value.tableData
  oldData.map((current, index, array) => {
    let currentOmit = pick(current, ['bank', 'bankAccount', 'bankPoc'])
    if (Object.values(currentOmit).length === 0) {
      checkResult = true
      return checkResult
    } else if (Object.values(currentOmit).length !== 3) {
      checkResult = false
      return checkResult
    } else {
      if (Object.values(currentOmit).every((item) => item === '')) {
        checkResult = true
      } else if (Object.values(currentOmit).every((item) => item !== '')) {
        checkResult = true
      } else {
        checkResult = false
      }
    }
  })
  return checkResult
}

const saveForm = async (temporary?) => {
  // let valid = await TableRef.value.validate()
  // if (checkCompleteData()) {
  //   if (valid || temporary) {
  //     return TableRef.value.tableData
  //   } else {
  //     message.warning('银行账户提交信息有误')
  //     return false
  //   }
  // } else {
  //   message.warning('请检查银行账户信息开户行、账户名称和账户号码是否完整')
  //   return false
  // }
  return TableRef.value.tableData
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
      if (formData.value?.bankaccountList?.length) {
        TableRef.value.tableData = formData.value.bankaccountList
      }
    }
  )
  if (TableRef.value?.tableData?.length === 1) {
    TableRef.value.tableData[0].defaultFlag = 1
  }
})
</script>
