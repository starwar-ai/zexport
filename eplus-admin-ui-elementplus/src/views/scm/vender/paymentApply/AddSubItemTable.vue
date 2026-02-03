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

  <SelectContract
    ref="SelectContractRef"
    @sure="handleSure"
  />
</template>
<script setup lang="tsx">
import { TableColumn } from '@/types/table'
import { cloneDeep } from 'lodash-es'
// import { getPurchaseContractSimpleList } from '@/api/scm/purchaseContract'
import { isValidArray } from '@/utils/is'
import SelectContract from './SelectContract.vue'
import { moneyInputPrecision } from '@/utils/config'
import EplusNumInput from '@/components/EplusMoney/src/EplusNumInput.vue'

//
defineOptions({ name: 'AddSubItemTable' })
const props = defineProps<{
  formData
  type: string
}>()
// const parentData: any = inject('formData')
const TableRef = ref()
const message = useMessage()
const tableList = ref([])
// const applyTotalAmount = cloneDeep(props.formData?.applyTotalAmount)
const emit = defineEmits(['addSubItemChange'])

let tableColumns = reactive<TableColumn[]>([
  {
    field: 'contractCode',
    label: '采购合同',
    width: '220px',
    fixed: 'left',
    slot: (item, row: Recordable, index: number) => {
      return (
        <div>
          <span>{row.contractCode || '-'}</span>
          <el-button
            class="ml5px"
            onClick={() => {
              hansleSelect(row, index)
            }}
          >
            选择合同
          </el-button>
        </div>
      )
    },
    rules: { required: true, message: '请选择采购合同' }
  },
  {
    field: 'calculationType',
    label: '加/减项',
    width: '220px',
    component: (
      <eplus-dict-select
        style="width: 120px"
        dictType={DICT_TYPE.CALCULATION_TYPE}
        clearable={false}
      />
    ),
    rules: { required: true, message: '请选择加/减项' }
  },
  {
    field: 'feeName',
    label: '费用名称',
    width: '220px',
    component: <el-input />,
    rules: { required: true, message: '请输入费用名称' }
  },
  {
    field: 'amountFormat',
    label: '金额',
    width: '220px',
    slot: (item, row: Recordable, index: number) => {
      return (
        <EplusNumInput
          v-model={row.amountFormat}
          precision={moneyInputPrecision}
          min={0}
          clearable={true}
          onChange={() => {
            amountChange()
          }}
        />
      )
    },
    rules: {
      required: true,
      validator: (rule: any, value: any, callback: any) => {
        if (!value) {
          callback(new Error('请输入金额'))
        } else {
          callback()
        }
      }
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

const SelectContractRef = ref()
const hansleSelect = (row, index) => {
  SelectContractRef.value?.open(row, index)
}

const handleSure = (data) => {
  tableList.value[data.index].contractCode = data.code
}
const setScorllBar = () => {
  nextTick(() => {
    TableRef.value.setScorllBar()
  })
}

const handleAdd = async () => {
  const newRecord = {
    sortNum: tableList.value.length + 1,
    calculationType: 1,
    contractCode: ''
  }
  tableList.value.push(newRecord)
  setScorllBar()
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

const amountChange = () => {
  let amountSum = tableList.value.map((item: any) => {
    return item.calculationType === 1 ? item.amountFormat || 0 : -Number(item.amountFormat || 0)
  })
  let amount = amountSum.reduce((prev, cur) => prev + cur, 0)
  emit('addSubItemChange', amount)
}

// watch(
//   () => tableList.value,
//   (list) => {
//     // if (Math.abs(amount) > parentData?.applyTotalAmount) {
//     //   message.warning('加减项金额不能大于申请金额')
//     //   return
//     // } else {
//     //   emit('addSubItemChange', amount)
//     // }
//   },
//   {
//     immediate: true,
//     deep: true
//   }
// )

const checkData = async () => {
  tableList.value.forEach((item: any) => {
    item?.id && delete item?.id
  })
  if (tableList.value.length) {
    let valid = await TableRef.value.validate()
    if (valid) {
      let tableData = cloneDeep(TableRef.value.tableData)
      return tableData
    } else {
      message.warning('加/减项提交有误')
      return false
    }
  } else {
    return []
  }
}
defineExpose({ selectedList, tableList, checkData, setScorllBar })
watchEffect(() => {
  if (isValidArray(props.formData.purchaseAddSubTermList)) {
    let tableData = cloneDeep(props.formData.purchaseAddSubTermList)
    tableList.value = tableData.map((item, index) => {
      return {
        ...item,
        amountFormat: item.amount?.amount
      }
    })
  } else {
    tableList.value = []
  }
  nextTick(() => {
    amountChange()
  })
})
onMounted(async () => {})
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
