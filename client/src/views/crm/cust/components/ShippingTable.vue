<template>
  <eplus-form-table
    ref="TableRef"
    prop="addressShipping"
    :schema="tableSchema"
    :list="formData.addressShipping"
  />
</template>
<script setup lang="tsx">
import type { EplusAuditable } from '@/types/eplus'
import { EplusFormTable, EplusFormTableSchema } from '@/components/EplusTemplate'

defineOptions({ name: 'ShippingTable' })
const props = defineProps<{
  required: boolean
}>()
const formData = inject('formData') as EplusAuditable
const TableRef = ref()
const tableList = computed(() => {
  return formData.value.addressShipping
})
const tableSchema: EplusFormTableSchema[] = [
  {
    label: '序号',
    field: 'index',
    fixed: 'left',
    width: 80
  },
  {
    field: 'name',
    label: '联系人',
    component: <el-input />,
    rules: { required: props.required, message: '请输入联系人' }
  },
  {
    field: 'address',
    label: '地址',
    component: <el-input />,
    rules: { required: props.required, message: '请输入地址' }
  },
  {
    field: 'addressType',
    label: '地址类型',
    width: '240px',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.ADDRESS_TYPE}
        style="width:100%"
        placeholder=" "
      />
    ),
    rules: { required: props.required, message: '请输入地址' }
  },
  {
    field: 'postalCode',
    label: '邮编',
    component: <el-input />,
    rules: { required: props.required, message: '请输入邮编' }
  },
  {
    field: 'phone',
    label: '电话',
    component: <el-input />,
    rules: [{ required: false, message: '请输入电话' }]
  },
  {
    field: 'email',
    label: '邮箱',
    component: <el-input />,
    rules: [
      { required: false, message: '请输入邮箱' },
      {
        pattern:
          /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/,
        message: '请输入正确的邮箱格式'
      }
    ]
  },
  {
    field: 'remark',
    label: '备注',
    component: <el-input />
  },
  {
    field: 'defaultFlag',
    label: '默认',
    fixed: 'right',
    width: '80px',
    slot: (item, row, index) => {
      return (
        <div onClick={() => setDefault(index, formData.value.addressShipping)}>
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
    fixed: 'right',
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

const saveForm = async (temporary?) => {
  let valid = await TableRef.value.validate()
  if (valid || temporary) {
    return TableRef.value.tableData
  } else {
    message.warning('地址提交信息有误')
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
      if (formData.value?.addressShipping?.length) {
        TableRef.value.tableData = formData.value.addressShipping
      }
    }
  )
  if (TableRef.value?.tableData?.length === 1) {
    TableRef.value.tableData[0].defaultFlag = 1
  }
})
</script>
