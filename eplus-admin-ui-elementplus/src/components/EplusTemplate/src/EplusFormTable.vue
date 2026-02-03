<template>
  <div style="width: 100%">
    <el-table
      v-loading="props.loading"
      :columns="schema"
      headerAlign="center"
      :data="tableData"
      ref="tableRef"
      border
      table-layout="auto"
      class="w-100%"
      @selection-change="handleSelectionChange"
      style="font-size: 14px"
      :show-summary="props.showSummary || false"
      :summary-method="summaryMethod"
      :max-height="props.maxHeight"
      :height="props.height"
      :empty-text="props.loading ? ' ' : '暂无数据'"
    >
      <el-table-column
        v-if="props.selectionFlag"
        type="selection"
        width="55"
        fixed="left"
        :selectable="setSelectable"
      />
      <el-table-column
        v-for="item in schema"
        :key="item.field"
        :prop="item.field"
        :width="item.width"
        :min-width="item.minWidth || item.width || '250px'"
        :label="isRequired(item) ? item.label : `* ${item.label}`"
        :fixed="item.fixed || false"
        :sortable="item.sortable"
        :show-overflow-tooltip="item.showOverflowTooltip || false"
      >
        <template #header>
          <span>
            {{ item.label }}
          </span>
          <span v-if="item.hint">
            <el-popover
              :placement="item.hintPlacement || 'right'"
              :width="item.hintWidth || 800"
              trigger="hover"
            >
              <template #reference>
                <Icon
                  icon="ep:question-filled"
                  class="relative top-1px ml-1px text-#409EFF"
                />
              </template>
              <template #default>
                <component :is="item.hint" />
              </template>
            </el-popover>
          </span>
          <span
            class="text-red-500"
            v-if="isRequired(item)"
          >
            *
          </span>
          <span v-if="item.batchEditFlag">
            <el-popover
              placement="top"
              :width="500"
              :visible="item.batchEditVisible"
            >
              <template #reference>
                <Icon
                  icon="ep:edit"
                  class="text-#409EFF ml10px cursor-pointer"
                  @click="() => batchEditOpen(item)"
                />
              </template>
              <template #default>
                <div class="batchPopTitle mb10px">批量填充</div>
                <div class="flex">
                  <div
                    class="!w-120px"
                    style="text-align: right"
                  >
                    {{ item.label }}:
                  </div>
                  <component
                    v-model="batchEditVal"
                    :is="item.component || item.batchEditCom"
                    class="ml20px !w-220px"
                  />
                  <el-button
                    class="ml20px"
                    @click="() => batchEditClose(item)"
                    >确认
                  </el-button>
                  <el-button
                    class="ml20px"
                    @click="() => batchEditClose(item, 'cancel')"
                    >取消
                  </el-button>
                </div>
              </template>
            </el-popover>
          </span>
        </template>
        <template #default="{ row, $index }">
          <template v-if="item.field == 'index'">
            <el-button
              v-if="props.showIndexPlus"
              link
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
            <span v-else>{{ $index + 1 }}</span>
          </template>

          <!--输入项-->
          <InputSlot
            :row="row"
            :item="item"
            v-else-if="item.component && item.field"
          />
          <!--静态数据处理项-->
          <FormatterSlot
            v-else-if="item.formatter"
            :row="row"
            :item="item"
            :index="$index"
          />
          <!--操作项-->
          <ActionSlot
            v-else-if="item.slot"
            :row="row"
            :item="item"
            :index="$index"
          />
          <div v-else-if="item.field && item.field2">
            <!--金额专项处理-->
            {{ row[item.field] ? `${row[item.field]}${row[item.field2]}` : 0 }}
          </div>
          <div v-else>
            <!--显示项-->
            {{
              (item.field && row[item.field]) || row[item.field] === 0
                ? row[item.field]
                : defaultData
            }}
          </div>
        </template>
      </el-table-column>
      <template #append>
        <div
          v-if="scollFlag"
          style="height: 10px"
        ></div>
      </template>
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
const tableRef = ref()
const errorFieldName = (item: EplusFormTableSchema) => {
  return `__err_${item.field}`
}

const tableStyle = () => {
  if (props.height) {
    return {
      height: props.height
    }
  } else {
    return {}
  }
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
    type: Array,
    required: true
  },
  list: {
    type: Array,
    required: true
  },
  defaultVal: {
    type: Object,
    required: false
  },
  selectionFlag: {
    type: Boolean,
    required: false
  },
  selectable: {
    type: Function,
    required: false
  },
  allSelectable: {
    type: Boolean,
    required: false
  },
  maxHeight: {
    type: [Number, String],
    required: false
  },
  height: {
    type: [Number, String],
    required: false
  },
  max: {
    type: [Number, String],
    required: false
  },
  showSummary: {
    type: Boolean,
    required: false
  },
  summaryMethod: {
    type: Function as PropType<(param: { columns: any[]; data: any[] }) => any[]>,
    required: false,
    default: () => undefined
  },
  showIndexPlus: {
    type: Boolean,
    default: true
  },
  loading: {
    type: Boolean,
    default: false
  }
})

function createDescriptor(schema: EplusFormTableSchema[]) {
  const descriptor = {}
  schema.forEach((item) => {
    if (!item.field || !item.rules) return
    descriptor[item.field] = item.rules
    item.batchEditVisible = false
  })

  return descriptor
}

const tableData = ref(props.list)
watch(
  () => props.list,
  (val) => {
    tableData.value = val
  },
  { deep: true }
)
// const formData = inject('formData') as EplusAuditable

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
        v-model={row[field]}
        onChange={() => inputChange(field, item, row)}
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

const inputChange = (field, item, row) => {
  if (typeof row[field] === 'string') {
    row[field] = row[field].trim()
  }

  validateField(item, row)
}

const addAction = () => {
  if (props.max && tableData.value.length >= props.max) {
    message.warning('最多只能添加' + props.max + '条数据')
    return false
  }
  const newRecord = {
    isHandleAdd: true
  }
  props.schema.forEach((item) => {
    if (item.field) {
      newRecord[item.field] = ''
    }
  })
  tableData.value.push({ ...newRecord, ...props.defaultVal })
}

const deleteAction = (index) => {
  if (tableData.value.length > 1) {
    if (tableData.value[index].defaultFlag == 1) {
      tableData.value.splice(index, 1)
      tableData.value[0].defaultFlag = 1
    } else {
      tableData.value.splice(index, 1)
    }
  } else {
    message.warning('最后一条数据不可删除')
    return false
  }
}

const FormatterSlot = (itemProps: {
  row: Recordable
  item: EplusFormTableSchema
  index: number
}) => {
  const item = itemProps.item
  const row = itemProps.row
  const index = itemProps.index
  if (!item.formatter) throw new Error('表格参数设置错误,')
  return item.formatter(item, row, index, deleteAction)
}
const ActionSlot = (itemProps: { row: Recordable; item: EplusFormTableSchema; index: number }) => {
  const item = itemProps.item
  const row = itemProps.row
  const index = itemProps.index
  if (!item.slot) throw new Error('表格参数设置错误,')
  // const EplusSlot = item.slot(item, row, index, deleteAction) as ReturnType<typeof defineComponent>
  const EplusSlot = item.slot(item, row, index, deleteAction)
  return (
    <div class="flex flex-row items-center justify-start">
      <EplusSlot
        row={row}
        item={item}
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

const validate = (): boolean => {
  let reqList = []
  let resultList = []
  tableData.value?.forEach((row, index) => {
    props.schema.forEach((schema) => {
      if (schema.field && schema.rules) {
        reqList.push(validateField(schema, row))
      }
    })
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
const emit = defineEmits(['selectionChange'])
const handleSelectionChange = (val) => {
  emit('selectionChange', val)
}
const handleToggleRowSelection = (row, selected) => {
  tableRef.value.toggleRowSelection(row, selected)
}
const handleAllToggleRowSelection = () => {
  return tableRef.value.toggleAllSelection()
}
const getSelectedRows = () => {
  return tableRef.value.getSelectionRows()
}

const setSelectable = (row) => {
  if (props.selectable) {
    return props.selectable(row)
  } else {
    return true
  }
}

const batchEditVal = ref()

const batchEditOpen = (item) => {
  props.schema.forEach((el: any) => {
    if (el.batchEditVisible) {
      el.batchEditVisible = false
    }
  })
  batchEditVal.value = ''
  item.batchEditVisible = true
}
const batchEditClose = (item, type = 'ok') => {
  if ((batchEditVal.value || batchEditVal.value == 0) && type === 'ok') {
    tableData.value.forEach((row: any) => {
      row[item.field] = batchEditVal.value
    })
  }
  batchEditVal.value = ''
  item.batchEditVisible = false
}

const scollFlag = ref(false)
const setScorllBar = () => {
  nextTick(() => {
    scollFlag.value = false
    let totalWidth = 0
    props.schema.forEach((item: any) => {
      totalWidth += parseInt(item.width) || parseInt(item.minWidth) || 250
    })
    if (props.selectionFlag) {
      totalWidth += 55
    }
    if (tableRef.value) {
      const bodyWrapper = tableRef.value.$refs.bodyWrapper
      const bodyWidth = bodyWrapper.clientWidth
      if (totalWidth > bodyWidth && bodyWidth > 0) {
        scollFlag.value = true
      } else {
        scollFlag.value = false
      }
    }
  })
}
window.addEventListener('resize', () => {
  setScorllBar()
})

defineExpose({
  deleteAction,
  validate,
  tableData,
  validateField,
  handleToggleRowSelection,
  getSelectedRows,
  handleAllToggleRowSelection,
  setScorllBar
})
onMounted(() => {
  if (
    (tableData.value?.length == 0 || tableData.value === null || tableData.value === undefined) &&
    props.schema[0]?.field === 'index'
  ) {
    addAction()
  }
  if (props.allSelectable) {
    handleAllToggleRowSelection()
  }
  setScorllBar()
})
</script>
<style scoped>
:deep(.el-popper.is-dark) {
  max-width: 500px !important;
  width: auto !important;
}
</style>
