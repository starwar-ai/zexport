<template>
  <div class="mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
      >关联采购订单</el-button
    >
    <el-button @click="handleRemove">移除采购订单</el-button>
  </div>
  <Table
    :columns="tableColumns"
    headerAlign="center"
    align="center"
    :data="tableList"
    @selection-change="handleSelectionChange"
  />
  <PurchaseDialog
    ref="PurchaseDialogRef"
    @sure="handleSure"
  />
</template>
<script setup lang="tsx">
import Table from '@/components/Table/src/Table.vue'
import type { TableColumn } from '@/components/Table/src/types'
import { formatDateColumn } from '@/utils/table'
import { isValidArray } from '@/utils/is'
import PurchaseDialog from './PurchaseDialog.vue'
import EplusNumInput from '@/components/EplusMoney/src/EplusNumInput.vue'
import { moneyInputPrecision } from '@/utils/config'

defineOptions({ name: 'PurchaseList' })
const props = defineProps<{
  formData
  currency?: string
  index?: number
  feeType?: any
  venderCodes?: any[]
}>()

const message = useMessage()
const tableList = ref([])

// 基础列配置
const baseColumns: TableColumn[] = [
  {
    type: 'selection',
    field: 'selection'
  },
  {
    field: 'code',
    label: '采购订单编号'
  },
  {
    field: 'venderName',
    label: '供应商名称'
  },
  {
    field: 'custName',
    label: '客户名称'
  },
  {
    field: 'purchaseTime',
    label: '创建时间',
    formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
  }
]

// itemAmount列配置
const itemAmountColumn: TableColumn = {
  field: 'itemAmount',
  label: '分摊金额',
  slots: {
    default: (data) => {
      const { row } = data
      return (
        <EplusNumInput
          v-model={row.itemAmount}
          min={0}
          precision={moneyInputPrecision}
          class="!w-90%"
        />
      )
    }
  }
}

// 操作列配置
const actionColumn: TableColumn = {
  field: 'action',
  label: '操作',
  width: '150px',
  fixed: 'right',
  align: 'left',
  slots: {
    default: (data) => {
      const { $index } = data

      return (
        <div class="flex items-center justify-between">
          <el-button
            link
            type="primary"
            onClick={async () => {
              await delRow($index)
            }}
          >
            移除
          </el-button>
        </div>
      )
    }
  }
}

// 根据feeType动态生成列配置
const tableColumns = computed<TableColumn[]>(() => {
  const columns = [...baseColumns]

  // 当feeType等于1时，显示itemAmount字段
  if (props.feeType === 1) {
    columns.push(itemAmountColumn)
  }

  columns.push(actionColumn)
  return columns
})

const PurchaseDialogRef = ref()
const handleAdd = async () => {
  PurchaseDialogRef.value.open(tableList, props.venderCodes || [])
}

const handleRemove = () => {
  if (selectedList.value?.length > 0) {
    let delArr = selectedList.value.map((el: any) => {
      return el.code
    })
    tableList.value = tableList.value.filter((item: any) => {
      if (!delArr.includes(item.code)) {
        return item
      }
    })
  } else {
    message.warning('请选择移除的数据')
  }
}

const delRow = (index) => {
  tableList.value.splice(index, 1)
}
const selectedList = ref([])
const handleSelectionChange = (val) => {
  selectedList.value = val
}

const handleSure = (list) => {
  tableList.value = list
}

const handleFilter = (code) => {
  tableList.value = tableList.value.filter((item: any) => item.venderCode != code)
}

const emit = defineEmits(['change'])
const totalAmount = ref(0)
watch(
  () => tableList.value,
  (list) => {
    totalAmount.value = 0
    list.forEach((item: any) => {
      totalAmount.value += Number(item.itemAmount) || 0
    })
    emit('change', totalAmount.value, `purchase${props.index}`)
  },
  {
    deep: true,
    immediate: true
  }
)

const checkData = () => {
  if (tableList.value.length > 0) {
    if (props.feeType === 1) {
      for (let index = 0; index < tableList.value.length; index++) {
        const item: any = tableList.value[index]
        if (!item.itemAmount) {
          message.warning(`采购订单分摊金额不能为空`)
          return false
        }
      }
    }

    return tableList.value.map((item: any) => {
      return {
        ...item,
        amount: { amount: item.itemAmount, currency: props.currency },
        businessSubjectType: 4
      }
    })
  } else {
    message.warning('请添加采购订单')
    return false
  }
}
defineExpose({ tableList, checkData, handleFilter, totalAmount })
onMounted(() => {
  if (isValidArray(props.formData?.purchaseChildren)) {
    tableList.value = props.formData?.purchaseChildren.map((item) => {
      return {
        ...item,
        ...item.purchaseContract,
        itemAmount: Number(item.itemAmount) || Number(item.amount?.amount) || 0
      }
    })
  } else {
    tableList.value = []
  }
})
</script>
