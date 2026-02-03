<template>
  <eplus-form-table
    prop="children"
    ref="TableRef"
    :schema="tableSchemaAll"
    :list="tableList"
    :height="650"
  />
</template>
<script setup lang="tsx">
import { EplusFormTable, EplusFormTableSchema } from '@/components/EplusTemplate'
import { isValidArray } from '@/utils/is'
import { cloneDeep } from 'lodash-es'
import { getDictOptions } from '@/utils/dict'
import type { EplusAuditable } from '@/types/eplus'

const effctOptions = ref<any[]>([])
const currentOptions = ref<any[]>([])
defineOptions({ name: 'CompanyPathTable' })
const props = defineProps<{
  required: boolean
}>()
const formData = inject('formData') as EplusAuditable
const TableRef = ref()
const showAllRef = ref(false)
let tableList = reactive([])
const required = props.required
const configRange = [
  {
    path: 'exportSaleContractChange',
    effectRange: 1
  },
  {
    path: 'domesticSaleContractChange',
    effectRange: 1
  },
  {
    path: 'purchaseChange',
    effectRange: 2
  },
  {
    path: 'shippingChange',
    effectRange: 3
  }
]
const changeLevel = (val, index) => {
  //   tableList[index].changeLevel = val
  // if (val === 2 && formData.instanceFlag) {
  //   tableList[index].effectDisabled = false
  // } else {
  //   tableList[index].effectDisabled = true
  //   tableList[index].effectMainInstanceFlag = 0
  // }
  TableRef.value.tableData[index].changeLevel = val

  if (val === 2 && formData.instanceFlag) {
    TableRef.value.tableData[index].effectDisabled = false
  } else {
    TableRef.value.tableData[index].effectDisabled = true
    TableRef.value.tableData[index].effectMainInstanceFlag = 0
  }
}
const tableSchemaAll: EplusFormTableSchema[] = [
  {
    label: '编号',
    field: 'index',
    fixed: 'left',
    width: 80
  },
  {
    label: '字段类型(中文)',
    field: 'name',
    component: <el-input />,
    rules: { required: required, message: '请输入中文字段类型' }
  },
  {
    label: '字段类型(英文)',
    field: 'nameEng',
    component: <el-input />,
    rules: { required: required, message: '请输入英文字段类型' }
  },
  {
    label: '变更级别',
    field: 'changeLevel',
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.changeLevel}
          dictType={DICT_TYPE.CHANGE_LEVEL}
          onChange={(value) => {
            changeLevel(value, index)
          }}
        />
      )
    }
  },
  {
    label: '是否影响主流程',
    field: 'effectMainInstanceFlag',
    slot: (item, row: Recordable, index: number) => {
      return (
        <eplus-dict-select
          v-model={row.effectMainInstanceFlag}
          dictType={DICT_TYPE.IS_INT}
          onChange={(value) => {
            if (!value) {
              row.effectRange = null
            }
          }}
          // disabled={row.effectDisabled}
        />
      )
    }
  },
  {
    label: '影响范围',
    field: 'effectRange',
    slot: (item, row: Recordable, index: number) => {
      if (row.effectMainInstanceFlag) {
        return (
          <el-select
            v-model={row.effectRange}
            clearable
            style="width: 100%"
            multiple={true}
            validate-event={false}
          >
            {currentOptions.value.length &&
              currentOptions.value.map((item: any, index) => {
                return (
                  <el-option
                    v-for={item in currentOptions}
                    key={item.value}
                    label={item.label}
                    value={item.value}
                  />
                )
              })}
          </el-select>
        )
      } else {
        return (
          <el-select
            v-model={row.effectRange}
            clearable
            style="width: 100%"
            multiple={true}
            validate-event={false}
            disabled
          >
            {currentOptions.value.length &&
              currentOptions.value.map((item: any, index) => {
                return (
                  <el-option
                    v-for={item in currentOptions}
                    key={item.value}
                    label={item.label}
                    value={item.value}
                  />
                )
              })}
          </el-select>
        )
      }
    }
  },
  {
    label: '是否显示帮助',
    field: 'showRemarkFlag',
    component: (
      <el-switch
        activeValue={1}
        inactiveValue={0}
      />
    )
  },
  {
    label: '备注',
    field: 'remark',
    component: <el-input />
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
    message.warning('字段信息有误')
    return false
  }
}

defineExpose({ tableList, saveForm })
getDictOptions(DICT_TYPE.EFFECT_RANGE).forEach((dict) => {
  effctOptions.value.push(dict)
})
watch(
  formData,
  (val) => {
    formData.children?.forEach((item) => {
      if (val?.instanceFlag && val?.changeLevel === 2) {
        item.effectDisabled = false
      } else {
        // item.effectMainInstanceFlag = 0
        item.effectDisabled = true
      }
    })
    if (!val?.instanceFlag) {
    }
  },
  { deep: true }
)
watchEffect(() => {
  const path = formData.path
  const temp = configRange.filter((cr) => {
    return cr.path == path
  })
  currentOptions.value = []
  effctOptions.value.forEach((eo) => {
    if (!temp.length || Number(eo.value) > Number(temp[0].effectRange)) {
      eo.value = Number(eo.value)
      currentOptions.value.push(eo)
    }
  })
  if (isValidArray(formData.children)) {
    let tableData = cloneDeep(formData.children)
    TableRef.value.tableData = tableData.map((item, index) => {
      return {
        ...item
      }
    })
  }
})
</script>
