<template>
  <div class="mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
      >添加行</el-button
    >
    <!-- <el-button @click="handleRemove">移除</el-button> -->
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

defineOptions({ name: 'AddSubItem' })
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
    slot: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <>
          <eplus-input-select
            v-model={row.contractCode}
            dataList={codeList.value}
            label="code"
            value="code"
            class="!w-100%"
            disabled={row.status == 1}
            onChange={() => {
              var arr = props.formData.children.filter((item) => {
                return item.saleContractCode == row.contractCode
              })
              if (arr?.length) {
                row.currency = arr[0].currency
              }
            }}
          />
        </>
      )
    },
    rules: [{ required: true, message: '请选择销售合同' }]
  },
  {
    field: 'calculationType',
    label: '加/减项',
    slot: (item: EplusFormTableSchema, row: Recordable) => {
      return (
        <>
          <eplus-dict-select
            v-model={row.calculationType}
            style="width: 120px"
            dictType={DICT_TYPE.CALCULATION_TYPE}
            disabled={row.status == 1}
          />
        </>
      )
    },
    rules: { required: true, message: '请选择加/减项' }
  },
  {
    field: 'feeName',
    label: '费用名称',
    slot: (item, row) => {
      return (
        <>
          <el-input
            v-model={row.feeName}
            disabled={row.status == 1}
          />
        </>
      )
    },
    rules: { required: true, message: '请输入费用名称' }
  },
  {
    field: 'amountFormat',
    label: '金额',
    slot: (item, row) => {
      return (
        <>
          <el-input-number
            v-model={row.amountFormat}
            style="width: 150px"
            precision={3}
            min={1}
            controls={false}
            clearable={true}
            disabled={row.status == 1}
          />
        </>
      )
    },
    rules: { required: true, message: '请输入金额' }
  },
  {
    field: 'currency',
    label: '币种'
  },
  {
    field: 'action',
    label: '操作',
    width: '150px',
    fixed: 'right',
    align: 'left',
    slot: (item, row: Recordable, index: number) => {
      if (row.status == 1) {
        return ''
      } else {
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
  if (tableList.value.length) {
    let valid = await TableRef.value.validate()
    if (valid) {
      let tableData = cloneDeep(TableRef.value.tableData)
      let arr = tableData.map((item, index) => {
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
defineExpose({ selectedList, tableList, checkData })
watchEffect(() => {
  if (isValidArray(props.formData.addSubItemList)) {
    let tableData = cloneDeep(props.formData.addSubItemList)
    tableList.value = tableData.map((item, index) => {
      return {
        ...item,
        amountFormat: item.amount?.amount
      }
    })
  } else {
    tableList.value = []
  }
  if (isValidArray(props.formData.children)) {
    let codes = []
    props.formData.children.forEach((item: any) => {
      if (!codes.includes(item.saleContractCode)) {
        codes.push(item.saleContractCode)
        codeList.value.push({ code: item.saleContractCode })
      }
    })
  } else {
    codeList.value = []
  }
})
onMounted(async () => {})
</script>
