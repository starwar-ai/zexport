<template>
  <eplus-form-table
    ref="TableRef"
    prop="settlementList"
    :schema="tableSchema"
    :list="formData.settlementList"
  />
</template>
<script setup lang="tsx">
import type { EplusAuditable } from '@/types/eplus'
import { EplusFormTable, EplusFormTableSchema } from '@/components/EplusTemplate'
import { useSettlementStore } from '@/store/modules/settlementList'

defineOptions({ name: 'SettlementTable' })
const props = defineProps<{
  required: boolean
}>()

const TableRef = ref()
const formData = inject('formData') as EplusAuditable
const settlementListData = useSettlementStore()
const tableSchema: EplusFormTableSchema[] = [
  {
    label: '序号',
    field: 'index',
    fixed: 'left',
    width: 80
  },
  {
    field: 'settlementId',
    label: '收款方式',
    minWidth: '120px',
    component: (
      <el-select
        v-model={formData.settlementList}
        clearable
        ref="settlementListRef"
        style="width: 100%"
      >
        {settlementListData.settlementList.map((item: any, index) => {
          return (
            <el-option
              v-for={item in settlementListData.settlementList}
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
        validator: (rule: any, value: any, callback: any) => {
          let valueArr = TableRef.value.tableData.map((item) => item.settlementId)
          valueArr.splice(
            valueArr.findIndex((el) => el == value),
            1
          )
          if (valueArr.includes(value)) {
            callback(new Error('此收款方式已存在'))
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
    fixed: 'right',
    width: '80px',
    slot: (item, row, index) => {
      return (
        <div onClick={() => setDefault(index, formData.value.settlementList)}>
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

const saveForm = async () => {
  let valid = await TableRef.value.validate()
  if (valid) {
    return TableRef.value.tableData
  } else {
    message.warning('收款方式提交信息有误')
    return false
  }
}
defineExpose({ saveForm })
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
      if (formData.value?.settlementList?.length) {
        TableRef.value.tableData = formData.value.settlementList
      }
    }
  )
  if (TableRef.value?.tableData?.length === 1) {
    TableRef.value.tableData[0].defaultFlag = 1
  }
})
</script>
