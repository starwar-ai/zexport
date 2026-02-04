<template>
  <div class="mb15px">
    <el-button @click="handleAdd">添加行</el-button>
    <el-button @click="handleRemove">移除</el-button>
  </div>
  <eplus-form-table
    ref="TableRef"
    selectionFlag
    :list="tableData"
    :defaultVal="{}"
    :schema="tableColumns"
    @selection-change="handleSelectionChange"
  />
</template>
<script setup lang="tsx">
import { TableColumn } from '@/types/table'
import { formatterPrice } from '@/utils/index'
import { isValidArray } from '@/utils/is'
import { cloneDeep } from 'lodash-es'

defineOptions({ name: 'CostAttrsCom' })
const props = defineProps<{
  formData
  type: string
  loading?: boolean
}>()
const emit = defineEmits(['loaded'])
const TableRef = ref()
const message = useMessage()
const tableData = ref([])
let tableColumns = reactive<TableColumn[]>([
  {
    field: 'calculationType',
    label: '加/减项',
    component: (
      <eplus-dict-select
        style="width: 120px"
        dictType={DICT_TYPE.CALCULATION_TYPE}
      />
    ),
    rules: { required: true, message: '请选择加/减项' }
  },
  {
    field: 'feeName',
    label: '费用名称',
    component: <el-input />,
    rules: { required: true, message: '请输入费用名称' }
  },
  {
    field: 'amountFormat',
    label: '金额',
    component: (
      <el-input-number
        style="width: 150px"
        precision={3}
        min={1}
        controls={false}
        clearable={true}
      />
    ),
    rules: { required: true, message: '请输入金额' }
  },
  {
    field: 'currency',
    label: '币种',
    component: (
      <eplus-dict-select
        dictType={DICT_TYPE.CURRENCY_TYPE}
        style="width:100%"
      />
    ),
    rules: {
      required: true,
      message: '请选择币种'
    }
  },
  {
    field: 'action',
    label: '操作',
    width: '150px',
    fixed: 'right',
    align: 'left',
    slot: (item, row: Recordable, index: number) => {
      return (
        <el-button
          link
          type="primary"
          onClick={async () => {
            await delRow(index)
          }}
        >
          移除
        </el-button>
      )
    }
  }
])

const tableList = computed(() => {
  var result = tableData.value.map((item, index) => {
    if (item.amountFormat) {
      return {
        ...item,
        amount: { amount: formatterPrice(item.amountFormat), currency: item.currency }
      }
    } else {
      return {
        ...item,
        amount: { amount: 0, currency: item.currency }
      }
    }
  })
  return result
})

const handleAdd = async () => {
  var index = tableData.value.length - 1
  const newRecord = {
    sortNum: index,
    currency: 'RMB'
  }
  tableData.value.push(newRecord)
}

const handleRemove = () => {
  if (selectedList.value?.length > 0) {
    let delArr = selectedList.value.map((el) => {
      return el.sortNum
    })
    tableData.value = tableData.value.filter((item, index) => {
      if (!delArr.includes(item.sortNum)) {
        return item
      }
    })
    tableData.value.forEach((item, index) => {
      return (item.sortNum = index + 1)
    })
  } else {
    message.error('请选择移除的数据')
  }
}

const delRow = (index) => {
  tableData.value.splice(index, 1)
  tableData.value.forEach((item, index) => {
    return (item.sortNum = index + 1)
  })
}
const selectedList = ref([])
const handleSelectionChange = (val) => {
  selectedList.value = val
}

const tableListExtend = computed(() => {
  let tableDataResult = cloneDeep(TableRef.value.tableData)
  let arr = tableDataResult.map((item, index) => {
    return {
      ...item,
      amount: { amount: formatterPrice(item.amountFormat), currency: item.currency }
    }
  })
  return arr
})

const checkData = async (submitFlag?) => {
  // tableData.value.forEach((item: any) => {
  //   item?.id && delete item?.id
  // })
  if (tableData.value.length) {
    let valid = await TableRef.value.validate()
    if (valid || !submitFlag) {
      let tableDataResult = cloneDeep(TableRef.value.tableData)
      let arr = tableDataResult.map((item, index) => {
        return {
          ...item,
          amount: { amount: formatterPrice(item.amountFormat), currency: item.currency }
        }
      })
      return arr
    } else {
      message.warning('加/减项提交有误')
      return false
    }
  } else {
    return []
  }
}

const resetTable = () => {
  tableData.value = []
}
const setScorllBar = () => {
  TableRef.value.setScorllBar()
}
defineExpose({ selectedList, tableList, tableListExtend, checkData, setScorllBar, resetTable })
const hasLoadedData = ref(false)
const isUpdating = ref(false)
watch(
  () => props.formData.addSubItemList,
  async (newList) => {
    console.log('[AddSubItemTable] 数据watch触发, 数据长度:', newList?.length, 'isUpdating:', isUpdating.value, 'hasLoadedData:', hasLoadedData.value)
    if (isUpdating.value) return
    if (isValidArray(newList)) {
      isUpdating.value = true
      try {
        // 深拷贝数据，避免修改原始props
        let addSubItemList = cloneDeep(newList)
        addSubItemList.forEach((item) => {
          item.currency = item.amount?.currency
        })
        tableData.value = addSubItemList
        await nextTick()
        console.log('[AddSubItemTable] 从数据watch触发loaded事件')
        hasLoadedData.value = true
        emit('loaded')
      } finally {
        isUpdating.value = false
      }
    } else {
      tableData.value = []
      // 只在第一次已有数据的情况下才emit loaded，避免初始化时过早触发
      if (hasLoadedData.value) {
        console.log('[AddSubItemTable] 数据变空，从数据watch触发loaded事件')
        emit('loaded')
      }
    }
  },
  { immediate: true }
)
// 监听父组件loading状态变化，当父组件数据加载完成且本组件数据为空时触发loaded
watch(
  () => props.loading,
  async (newVal, oldVal) => {
    console.log('[AddSubItemTable] loading变化:', oldVal, '->', newVal, 'hasLoadedData:', hasLoadedData.value, 'isUpdating:', isUpdating.value)
    // 只有在数据不在更新中且没有加载过数据时才触发loaded
    if (oldVal === true && newVal === false && !hasLoadedData.value && !isUpdating.value) {
      await nextTick()
      if (!hasLoadedData.value && !isUpdating.value) {
        console.log('[AddSubItemTable] 从loading watch触发loaded事件')
        hasLoadedData.value = true
        emit('loaded')
      }
    }
  }
)
onMounted(async () => {
  console.log('[AddSubItemTable] onMounted, loading:', props.loading, 'hasLoadedData:', hasLoadedData.value, 'isUpdating:', isUpdating.value)
  // 新增模式下数组为空，watchEffect不会触发loaded事件，需要在mounted时触发
  // 只有在父组件数据已加载完成时才触发
  await nextTick()
  if (!props.loading && !hasLoadedData.value && !isUpdating.value) {
    console.log('[AddSubItemTable] 从onMounted触发loaded事件')
    hasLoadedData.value = true
    emit('loaded')
  }
})
</script>
<style lang="scss" scoped>
:deep(.el-badge__content) {
  position: relative;
  left: -55px;
  width: 30px;
}

:deep(.el-badge__content--primary) {
  color: #7cc440;
  background-color: rgba(0, 0, 0, 0);
}

.total-header {
  width: 100%;
}
</style>
