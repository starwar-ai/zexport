<template>
  <el-popover
    placement="top-start"
    :width="800"
    trigger="hover"
    @show="handlePopoverShow()"
    :disabled="popoverDisabled"
  >
    <template #reference>
      {{ isTree ? row.row.lockQuantity : row.row.totalLockQuantity }}
    </template>
    <template #default>
      <div v-if="tableList.length > 0">
        <Table
          :columns="tableColumns"
          headerAlign="center"
          align="center"
          :data="tableList"
        />
      </div>
    </template>
  </el-popover>
</template>
<script setup lang="tsx">
import * as StockApi from '@/api/wms/stock'
import { formatDateColumn, formatDictColumn } from '@/utils/table'

const props = defineProps<{
  row
  isTree
}>()

let tableList = ref([])
const tableColumns = reactive([
  {
    field: 'saleContractCode',
    label: '外销合同编号'
  },
  {
    field: 'sourceOrderType',
    label: '占用来源类型',
    formatter: formatDictColumn(DICT_TYPE.STOCK_LOCK_SOURCE_TYPE)
  },
  {
    field: 'lockQuantity',
    label: '占用数量'
  },
  {
    field: 'lockByUserName',
    label: '占用人名称'
  },
  {
    field: 'lockTime',
    label: '占用时间',
    formatter: formatDateColumn('YYYY-MM-DD HH:mm:ss')
  },
  {
    field: 'remark',
    label: '备注'
  }
])

const popoverDisabled = computed(() => {
  if (props.isTree && props.row.row.lockQuantity > 0) {
    return false
  } else if (!props.isTree && props.row.row.totalLockQuantity > 0) {
    return false
  } else {
    return true
  }
})

onMounted(() => {
  // if (props.row.row.id) {
  //   StockApi.lockList(props.row.row.id).then((res) => {
  //     tableList.value = res
  //   })
  // }
})

// Popover表格展示之后调接口
const handlePopoverShow = () => {
  // 批次
  if (props.isTree) {
    if (props.row.row.id) {
      StockApi.lockList(props.row.row.id).then((res) => {
        tableList.value = res
      })
    }
  } else {
    //产品
    if (props.row.row.id) {
      StockApi.lockList(props.row.row.id).then((res) => {
        tableList.value = res
      })
    }
  }
}
</script>
<style lang="scss" scoped></style>
