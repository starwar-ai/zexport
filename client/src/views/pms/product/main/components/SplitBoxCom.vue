<template>
  <el-popover
    placement="top-start"
    :width="800"
    trigger="hover"
    @show="handlePopoverShow()"
    :disabled="popoverDisabled"
  >
    <template #reference> {{ getDictLabel(DICT_TYPE.IS_INT, row.splitBoxFlag) }} </template>
    <template #default>
      <div v-if="tableList?.length > 0">
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
import { formatNum } from '@/utils'
import { LengthUnit, volPrecision, VolumeUnit, weightUnit } from '@/utils/config'
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import { formatWeightColumn } from '@/utils/table'

const props = defineProps<{
  row
}>()

let tableList = ref([])
watch(
  () => props.row,
  (row) => {
    if (row.specificationList) {
      tableList.value = row.specificationList
    }
  },
  {
    deep: true,
    immediate: true
  }
)

const tableColumns = reactive([
  {
    type: 'index',
    label: '序号'
  },
  {
    field: 'sourceOrderType',
    label: `外箱规格(${LengthUnit})`,
    formatter: (row) => {
      return `${row.outerboxLength}*${row.outerboxWidth}*${row.outerboxHeight}`
    }
  },
  {
    field: 'outerboxVolume',
    label: `外箱体积(${VolumeUnit})`,
    formatter: (row) => {
      return `${formatNum(row.outerboxVolume / 1000000, volPrecision)}`
    }
  },
  {
    field: 'outerboxGrossweight',
    label: `外箱毛重(${weightUnit})`,
    formatter: formatWeightColumn()
  },
  {
    field: 'outerboxNetweight',
    label: `外箱净重(${weightUnit})`,
    formatter: formatWeightColumn()
  }
])

const popoverDisabled = computed(() => {
  if (props.row.splitBoxFlag === 0) {
    return true
  } else {
    return false
  }
})

// Popover表格展示之后调接口
const handlePopoverShow = () => {}
</script>
<style lang="scss" scoped></style>
