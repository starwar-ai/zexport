<template>
  <el-popover
    placement="top-start"
    :width="800"
    trigger="hover"
    @show="handlePopoverShow()"
    :disabled="popoverDisabled"
  >
    <template #reference>
      {{ getDictLabel(DICT_TYPE.INSPECTION_STATUS, row?.checkStatus) }}
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
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { qmsList } from '@/api/common'
import { formatDictColumn } from '@/utils/table'

const props = defineProps<{
  row
}>()

let tableList = ref([])
const tableColumns = reactive([
  {
    field: 'skuName',
    label: '产品名称'
  },
  {
    field: 'inspectionStatus',
    label: '验货结果',
    formatter: formatDictColumn(DICT_TYPE.INSPECTION_STATUS)
  },
  {
    field: 'handleFlag',
    label: '处理状态',
    formatter: (row) => {
      return getDictLabel(DICT_TYPE.INSPECTION_STATUS, row?.handleFlag) || '-'
    }
  }
])

const popoverDisabled = computed(() => {
  if (props.row?.checkStatus == 0) {
    return true
  } else {
    return false
  }
})

// Popover表格展示之后调接口
const handlePopoverShow = () => {
  if (props.row.saleContractItemId) {
    qmsList({ saleContractItemId: props.row.saleContractItemId }).then((res) => {
      tableList.value = res
    })
  }
}
</script>
<style lang="scss" scoped></style>
