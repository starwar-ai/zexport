<template>
  <div class="mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
      >关联用户
    </el-button>
    <el-button @click="handleRemove">移除用户</el-button>
  </div>
  <Table
    :columns="tableColumns"
    headerAlign="center"
    align="center"
    :data="tableList"
    @selection-change="handleSelectionChange"
  />
  <UserDialog
    ref="UserDialogRef"
    @sure="handleSure"
  />
</template>
<script setup lang="tsx">
import Table from '@/components/Table/src/Table.vue'
import type { TableColumn } from '@/components/Table/src/types'
import { DICT_TYPE } from '@/utils/dict'
import { isValidArray } from '@/utils/is'
import UserDialog from './UserDialog.vue'
import EplusNumInput from '@/components/EplusMoney/src/EplusNumInput.vue'
import { moneyInputPrecision } from '@/utils/config'

defineOptions({ name: 'UserList' })
const props = defineProps<{
  formData
  currency?: string
  index?: number
  feeType?: any
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
    label: '员工编号'
  },
  {
    field: 'nickname',
    label: '用户名称'
  },
  {
    field: 'deptName',
    label: '部门名称'
  },
  {
    field: 'descId',
    label: '费用标签',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <eplus-dict-select
            v-model={row.descId}
            dictType={DICT_TYPE.FEE_SHARE_USER_DESC}
            class="w-100%"
          />
        )
      }
    }
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
      const { row, $index } = data

      return (
        <div class="flex items-center justify-between">
          <el-button
            link
            type="primary"
            onClick={async () => {
              await delRow(row, $index)
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

const UserDialogRef = ref()
const handleAdd = async () => {
  UserDialogRef.value.open(tableList)
}

const handleRemove = () => {
  if (selectedList.value?.length > 0) {
    let delArr = selectedList.value.map((el: any) => {
      emit('del', el.code)
      return el.code
    })
    tableList.value = tableList.value.filter((item: any) => {
      if (!delArr.includes(item.code)) {
        return item
      }
    })
  } else {
    message.error('请选择移除的数据')
  }
}
const emit = defineEmits(['del', 'change'])
const delRow = (row, index) => {
  tableList.value.splice(index, 1)
  emit('del', row.code)
}
const totalAmount = ref(0)
watch(
  () => tableList.value,
  (list) => {
    totalAmount.value = 0
    list.forEach((item: any) => {
      totalAmount.value += Number(item.itemAmount) || 0
    })
    emit('change', totalAmount.value, `vender${props.index}`)
  },
  {
    deep: true,
    immediate: true
  }
)
const selectedList = ref([])
const handleSelectionChange = (val) => {
  selectedList.value = val
}

const handleSure = (list) => {
  tableList.value = list
}

const checkData = () => {
  if (tableList.value.length > 0) {
    if (props.feeType === 1) {
      for (let index = 0; index < tableList.value.length; index++) {
        const item: any = tableList.value[index]
        if (!item.itemAmount) {
          message.warning(`用户列表分摊金额不能为空`)
          return false
        }
        if (!item.descId) {
          message.warning(`费用标签不能为空`)
          return false
        }
      }
    } else {
      for (let index = 0; index < tableList.value.length; index++) {
        const item: any = tableList.value[index]
        if (!item.descId) {
          message.warning(`费用标签不能为空`)
          return false
        }
      }
    }

    return tableList.value.map((item: any) => {
      return {
        ...item,
        amount: { amount: item.itemAmount, currency: props.currency }
      }
    })
  } else {
    message.warning('请添加用户')
    return false
  }
}
defineExpose({ tableList, checkData, totalAmount })
onMounted(() => {
  if (isValidArray(props.formData?.userChildren)) {
    tableList.value = props.formData?.userChildren.map((item) => {
      return {
        ...item,
        itemAmount: item.itemAmount || Number(item.amount?.amount) || 0
      }
    })
  } else {
    tableList.value = []
  }
})
</script>
