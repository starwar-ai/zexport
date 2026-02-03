<template>
  <eplus-form-table
    ref="TableRef"
    prop="custCompanyPathList"
    :schema="tableSchema"
    :list="formData.custCompanyPathList"
  />
  <!-- <el-button @click="saveForm">保存</el-button> -->
</template>
<script setup lang="tsx">
import type { EplusAuditable } from '@/types/eplus'
import { EplusFormTable, EplusFormTableSchema } from '@/components/EplusTemplate'
import * as CompanyPathApi from '@/api/system/companyPath'
import { getCompanyPathNameFromObj } from '@/utils/companyPathUtils'
import { isValidArray } from '@/utils/is'

defineOptions({ name: 'CompanyPathTable' })
const props = defineProps<{
  required: boolean
}>()

const TableRef = ref()
const formData = inject('formData') as EplusAuditable
const companyPathListData = ref([])
const tableList = computed(() => {
  return formData.value.custCompanyPathList
})
const tableSchema: EplusFormTableSchema[] = [
  {
    label: '序号',
    field: 'index',
    fixed: 'left',
    width: 80
  },
  {
    field: 'companyPathId',
    label: '订单路径',
    width: '300px',
    component: (
      <el-select
        v-model={formData.companyPathId}
        clearable
        ref="custCompanyPathListRef"
        style="width: 100%"
      >
        {companyPathListData.value.length &&
          companyPathListData.value.map((item: any, index) => {
            return (
              <el-option
                v-for={item in companyPathListData}
                key={item.id}
                label={item.pathName}
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
          if (!value) {
            callback(new Error('请选择订单路径'))
          } else {
            let valueArr = TableRef.value.tableData.map((item) => item.companyPathId)
            valueArr.splice(
              valueArr.findIndex((el) => el == value),
              1
            )
            if (valueArr.includes(value)) {
              callback(new Error('此订单路径已存在'))
            } else {
              callback()
            }
          }
        }
      }
    ]
  },
  {
    label: '',
    field: ''
  },
  {
    field: 'defaultFlag',
    label: '默认',
    fixed: 'right',
    width: '80px',
    slot: (item, row, index) => {
      return (
        <div onClick={() => setDefault(index, formData.value.custCompanyPathList)}>
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

const saveForm = async (temporary?) => {
  let valid = await TableRef.value.validate()
  if (valid || temporary) {
    return TableRef.value.tableData
  } else {
    message.warning('订单路径提交信息有误')
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

watch(
  () => formData.value.internalCompanyId,
  (val) => {
    setPathList(val)
  },
  {
    immediate: true
  }
)

const setPathList = async (companyId?) => {
  let params = companyId ? { internalCustFlag: 1, companyId: companyId } : {}
  const res = await CompanyPathApi.getCompanyPathSimple(params)
  res.list.forEach((item) => {
    if (item.path) {
      item.pathName = getCompanyPathNameFromObj(item.path)
    }
  })
  companyPathListData.value = res.list
}
onMounted(async () => {
  if (isValidArray(formData.value?.companyPath)) {
    TableRef.value.tableData = formData.value.custCompanyPathList
  }
  setPathList()
})
</script>
