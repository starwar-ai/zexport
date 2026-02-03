<template>
  <div class="mb15px">
    <el-button @click="handleAdd">添加行</el-button>
    <el-button @click="handleRemove">移除</el-button>
  </div>
  <eplus-form-table
    ref="TableRef"
    selectionFlag
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
    @selection-change="handleSelectionChange"
  />
</template>
<script setup lang="tsx">
import { TableColumn } from '@/types/table'
import { formatterPrice } from '@/utils/index'
import { columnWidth } from '@/utils/table'
import { isValidArray } from '@/utils/is'
import { cloneDeep } from 'lodash-es'

defineOptions({ name: 'OtherFeeTable' })
const props = defineProps<{
  formData
}>()
const TableRef = ref()
const message = useMessage()
const tableList = ref([])
let tableColumns = reactive<TableColumn[]>([
  {
    field: 'feeName',
    label: '费用说明',
    component: <el-input />,
    width: columnWidth.xxl,
    rules: { required: true, message: '请输入费用名称' }
  },
  {
    field: 'amountFormat',
    label: '金额',
    width: columnWidth.l,
    component: (
      <el-input-number
        precision={3}
        min={1}
        controls={false}
        clearable={true}
      />
    ),
    rules: { required: true, message: '请输入金额' }
  },
  {
    field: '',
    label: ''
  },
  {
    field: 'action',
    label: '操作',
    width: columnWidth.l,
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

const handleAdd = async () => {
  var index = tableList.value.length - 1
  const newRecord = {
    sortNum: index
  }
  tableList.value.push(newRecord)
}

const handleRemove = () => {
  if (selectedList.value?.length > 0) {
    let delArr = selectedList.value.map((el) => {
      return el.sortNum
    })
    tableList.value = tableList.value.filter((item, index) => {
      if (!delArr.includes(item.sortNum)) {
        return item
      }
    })
    tableList.value.forEach((item, index) => {
      return (item.sortNum = index + 1)
    })
  } else {
    message.error('请选择移除的数据')
  }
}

const delRow = (index) => {
  tableList.value.splice(index, 1)
  tableList.value.forEach((item, index) => {
    return (item.sortNum = index + 1)
  })
}
const selectedList = ref([])
const handleSelectionChange = (val) => {
  selectedList.value = val
}

const checkData = async () => {
  tableList.value.forEach((item: any) => {
    item?.id && delete item?.id
  })
  if (tableList.value.length) {
    let valid = await TableRef.value.validate()
    if (valid) {
      let tableData = cloneDeep(TableRef.value.tableData)
      let arr = tableData.map((item, index) => {
        return {
          ...item,
          amount: { amount: formatterPrice(item.amountFormat), currency: 'RMB' }
        }
      })
      return arr
    } else {
      message.warning('其他费用信息提交有误')
      return false
    }
  } else {
    return []
  }
}
defineExpose({ selectedList, tableList, checkData })

onMounted(async () => {
  if (isValidArray(props.formData.otherFeeList)) {
    let tableData = cloneDeep(props.formData.otherFeeList)
    tableList.value = tableData.map((item, index) => {
      return {
        ...item,
        amountFormat: item.amount?.amount
      }
    })
  } else {
    tableList.value = []
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
