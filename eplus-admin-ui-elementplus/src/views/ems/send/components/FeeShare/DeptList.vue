<template>
  <div class="mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
      >新增</el-button
    >
  </div>
  <Table
    :columns="tableColumns"
    headerAlign="center"
    align="center"
    :data="tableList"
  />
</template>
<script setup lang="tsx">
import Table from '@/components/Table/src/Table.vue'
import type { TableColumn } from '@/components/Table/src/types'
// import { DICT_TYPE, getDictLabels } from '@/utils/dict'
// import { formatDateColumn } from '@/utils/table'
import * as DeptApi from '@/api/system/dept'
import { isValidArray } from '@/utils/is'
import { handleTree } from '@/utils/tree'
import EplusNumInput from '@/components/EplusMoney/src/EplusNumInput.vue'
import { moneyInputPrecision } from '@/utils/config'

defineOptions({ name: 'CrmList' })
const props = defineProps<{
  formData
  currency?: string
  index?: number
  feeType?: any
}>()

const message = useMessage()
const tableList = ref([])
const deptList = ref<Tree[]>([])

onMounted(async () => {
  deptList.value = handleTree(await DeptApi.getSimpleDeptList())
})

// 基础列配置
const baseColumns: TableColumn[] = [
  {
    type: 'index',
    field: 'index'
  },
  {
    field: 'code',
    label: '部门名称',
    slots: {
      default: (data) => {
        const { row } = data
        return (
          <el-tree-select
            v-model={row.code}
            data={deptList.value}
            props={{
              children: 'children',
              label: 'name',
              value: 'code',
              isLeaf: 'leaf',
              emitPath: false // 用于 cascader 组件：在选中节点改变时，是否返回由该节点所在的各级菜单的值所组成的数组，若设置 false，则只返回该节点的值
            }}
            check-strictly
            node-key="code"
            placeholder="请选择归属部门"
          />
        )
      }
    }
  }
]

// itemAmount列配置
const itemAmountColumn: TableColumn = {
  field: 'itemAmount',
  label: '费用金额',
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

const handleAdd = async () => {
  let obj = {
    code: '',
    itemAmount: ''
  }
  tableList.value.push(obj)
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
    deep: true
  }
)
// const selectedList = ref([])
// const handleSelectionChange = (val) => {
//   selectedList.value = val
// }

// const handleSure = (list) => {
//   tableList.value = list
// }

const checkData = () => {
  if (tableList.value.length > 0) {
    if (props.feeType === 1) {
      for (let index = 0; index < tableList.value.length; index++) {
        const item: any = tableList.value[index]
        if (!item.code) {
          message.warning(`请选择部门`)
          return false
        }
        if (!item.itemAmount || item.itemAmount <= 0) {
          message.warning(`部门列表分摊金额须大于0`)
          return false
        }
      }
    } else {
      for (let index = 0; index < tableList.value.length; index++) {
        const item: any = tableList.value[index]
        if (!item.code) {
          message.warning(`请选择部门`)
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
    message.warning('请添加部门')
    return false
  }
}
defineExpose({ tableList, checkData, totalAmount })
watchEffect(() => {
  if (isValidArray(props.formData?.deptChildren)) {
    tableList.value = props.formData?.deptChildren.map((item) => {
      return {
        ...item,
        itemAmount: Number(item.amount?.amount) || 0
      }
    })
  } else {
    tableList.value = []
  }
})
</script>
