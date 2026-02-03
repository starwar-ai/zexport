<template>
  <div class="mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
      >添加行</el-button
    >
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
import { isValidArray } from '@/utils/is'
import { cloneDeep } from 'lodash-es'

defineOptions({ name: 'CostAttrsCom' })
const props = defineProps<{
  formData
  type: string
}>()
const TableRef = ref()
const message = useMessage()
const tableList = ref([])
const codeList = ref([])
let tableColumns = reactive<TableColumn[]>([
  {
    field: 'contractCode',
    label: '销售合同',
    width: '300',
    component: (
      <eplus-input-select
        dataList={codeList.value}
        label="code"
        value="code"
        class="!w-100%"
      />
    ),
    rules: [{ required: true, message: '请选择销售合同' }]
  },
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
      message.warning('加/减项提交有误')
      return false
    }
  } else {
    return []
  }
}
defineExpose({ selectedList, tableList, checkData })
watchEffect(() => {
  if (isValidArray(props.formData.children)) {
    let codes = []
    props.formData.children.forEach((item: any) => {
      if (!codes.includes(item.saleContractCode)) {
        codes.push(item.saleContractCode)
        codeList.value.push({ code: item.saleContractCode })
      }
    })
    let tableData = cloneDeep(props.formData.addSubItemList)
    if (tableData) {
      tableList.value = tableData.map((item, index) => {
        return {
          ...item,
          amountFormat: item.amount?.amount
        }
      })
    } else {
      tableList.value = []
    }
  } else {
    tableList.value = []
  }
})
onMounted(async () => {})
</script>
