<template>
  <div style="width: 100%">
    <el-table
      :columns="schema"
      headerAlign="center"
      :data="tableData"
      border
      table-layout="auto"
      class="w-100%"
    >
      <el-table-column
        v-for="item in schema"
        :key="item.field"
        :prop="item.field"
        :width="item.width"
        :min-width="item.minWidth || 400"
        :label="isRequired(item) ? item.label : `* ${item.label}`"
        :fixed="item.fixed || false"
      >
        <template #header>
          <span>
            {{ item.label }}
          </span>
          <span
            class="text-red-500"
            v-if="isRequired(item)"
          >
            *
          </span>
        </template>
        <template #default="{ row, $index }">
          <el-button
            link
            v-if="item.field == 'index'"
            @click="addAction"
            :disabled="$index + 1 < tableData.length"
          >
            <Icon
              v-if="$index + 1 == tableData.length"
              icon="ep:circle-plus"
              style="font-size: 24px; color: #409eff"
            />
            <template v-else>{{ $index + 1 }} </template>
          </el-button>
          <!--输入项-->
          <InputSlot
            :row="row"
            :item="item"
            v-else-if="comCheck(item, row)"
          />
          <!--操作项-->
          <ActionSlot
            v-else-if="slotCheck(item, row)"
            :row="row"
            :item="item"
            :index="$index"
          />
          <div v-else-if="item.field && item.field2">
            <!--金额专项处理-->
            {{ row[item.field] ? `${row[item.field]}${row[item.field2]}` : 0 }}
          </div>
          <div v-else>
            <!-- {{ (item.field && row[item.field]) ?? defaultData }} -->
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script lang="tsx" setup>
import { useDesign } from '@/hooks/web/useDesign'
import { EplusFormTableSchema } from '../index'
import { Warning } from '@element-plus/icons-vue'

import Schema from 'async-validator'

const message = useMessage()
const prefixCls = useDesign().getPrefixCls('eplus-table-description')

const comCheck = (item, row) => {
  if (item.isShow) {
    return item.component && item.field
  } else {
    return item.component && item.field && row.isShow
  }
}
const slotCheck = (item, row) => {
  if (item.isShow) {
    return item.slot
  } else {
    return item.slot && row.isShow
  }
}

const errorFieldName = (item: EplusFormTableSchema) => {
  return `__err_${item.field}`
}
const isRequired = (item: EplusFormTableSchema): boolean => {
  let rules = item.rules
  if (!rules) return false
  if (!Array.isArray(rules)) rules = [rules]

  if (rules.findIndex((rule) => rule.required) >= 0) return true
  else return false
}
const props = defineProps({
  title: {
    type: String
  },
  schema: {
    type: Array as PropType<EplusFormTableSchema[]>,
    required: true
  },
  list: {
    type: Array,
    required: true
  },
  defaultVal: {
    type: Object,
    required: false
  }
})

function createDescriptor(schema: EplusFormTableSchema[]) {
  const descriptor = {}
  schema.forEach((item) => {
    if (!item.field || !item.rules) return
    descriptor[item.field] = item.rules
  })

  return descriptor
}

// const formData = inject('formData') as EplusAuditable
const tableData = ref(props.list)
const descriptor = createDescriptor(props.schema)
const validator = new Schema(descriptor)
const InputSlot = (itemProps: { row: Recordable; item: EplusFormTableSchema }) => {
  const item = itemProps.item
  const row = itemProps.row
  const EplusCom = item.component as ReturnType<typeof defineComponent>
  const field = item.field!
  // console.log('row' + row[errorFieldName(item.field)])
  return (
    <div class="flex flex-row items-center justify-start">
      <EplusCom
        vModel={row[field]}
        onChange={() => validateField(item, row)}
        class="!w-90%"
      />
      {row[errorFieldName(item)] ? (
        <el-tooltip
          content={row[errorFieldName(item)]}
          placement="right"
        >
          <el-icon>
            <Warning style="color:red;" />
          </el-icon>
        </el-tooltip>
      ) : (
        <div></div>
      )}
    </div>
  )
}

const addAction = (row?) => {
  const newRecord = {}
  props.schema.forEach((item) => {
    if (item.field) {
      newRecord[item.field] = ''
    }
  })
  if (tableData.value.length === 0) {
    tableData.value.push({ ...newRecord, ...props.defaultVal, defaultFlag: 1 })
  } else {
    tableData.value.push({ ...newRecord, ...props.defaultVal, defaultFlag: 0 })
  }
}
const copyAction = (row) => {
  tableData.value.push({ ...row, defaultFlag: 0 })
}

const deleteAction = (index) => {
  if (tableData.value[index].defaultFlag == 1) {
    tableData.value.splice(index, 1)
    if (tableData.value.length === 0) {
      addAction()
    } else {
      tableData.value[0].defaultFlag = 1
    }
  } else {
    tableData.value.splice(index, 1)
    if (tableData.value.length === 0) {
      addAction()
    }
  }
}
const ActionSlot = (itemProps: { row: Recordable; item: EplusFormTableSchema; index: number }) => {
  const item = itemProps.item
  const row = itemProps.row
  const index = itemProps.index
  if (!item.slot) throw new Error('表格参数设置错误,')
  const EplusSlot = item.slot(item, row, index, deleteAction) as ReturnType<typeof defineComponent>
  return (
    <div class="flex flex-row items-center justify-start">
      <EplusSlot
        row={row}
        item={item}
      />
      {row[errorFieldName(item)] ? (
        <el-tooltip
          content={row[errorFieldName(item)]}
          placement="right"
        >
          <el-icon>
            <Warning style="color:red;" />
          </el-icon>
        </el-tooltip>
      ) : (
        <div></div>
      )}
    </div>
  )
}

const validate = (): boolean => {
  let reqList = []
  let checkFileld = ''

  props.schema.forEach((schema) => {
    if (schema.isShow) {
      checkFileld = schema.field
    }
  })
  tableData.value?.forEach((row, index) => {
    if (row[checkFileld]) {
      props.schema.forEach((schema) => {
        if (schema.field && schema.rules) {
          reqList.push(validateField(schema, row))
        }
      })
    }
  })

  return Promise.all(reqList).then((res) => {
    return !res.includes(false)
  })
}

const validateField = async (item: EplusFormTableSchema, row: Recordable): boolean => {
  let valid = true
  if (!item.rules) return valid
  const field = item.field!
  await validator
    .validate({ [field]: row[field] })
    .then(() => {
      // validation passed or without error message
      delete row[errorFieldName(item)]
    })
    .catch(({ errors, fields }) => {
      let errorItem = errors.find((item) => item.field == field)
      if (errorItem) {
        row[errorFieldName(item)] = errorItem?.message
      } else {
        delete row[errorFieldName(item)]
      }
      valid = errorItem ? false : true
    })
  return valid
}

const defaultData = '-'
defineExpose({ deleteAction, validate, tableData, validateField, addAction, copyAction })
onMounted(() => {
  if (tableData.value?.length == 0 || tableData.value === null || tableData.value === undefined) {
    addAction()
  }
})
</script>
<style scoped>
:deep(.el-popper.is-dark) {
  max-width: 500px !important;
  width: auto !important;
}
</style>
