<template>
  <eplus-description
    title="费用归集"
    :data="tableList"
    :items="feeShareInfo"
  >
    <template #detail>
      <Table
        class="mb20px"
        :columns="tableColumns"
        headerAlign="center"
        align="center"
        :data="tableList"
      />
    </template>
  </eplus-description>
</template>
<script setup lang="tsx">
import { EplusDescriptionItemSchema } from '@/components/EplusDescriptions'
import { formatMoneyColumn } from '@/utils/table'

// const props = defineProps<{
//   info?: any
// }>()

const props = withDefaults(
  defineProps<{
    info?: any
    feeType?: number | string
  }>(),
  { feeType: 1 }
)

const tableList = ref<any[]>([])
onMounted(() => {
  props.info.forEach((item) => {
    tableList.value.push(...item.detailList)
  })
})
const feeShareInfo: EplusDescriptionItemSchema[] = [
  {
    slotName: 'detail',
    field: 'detail',
    label: '',
    span: 24
  }
]

const tableColumns = computed(() => {
  const baseColumns: any[] = [
    {
      type: 'index',
      label: '序号',
      field: 'index'
    },
    {
      field: 'feeShareType',
      label: '归集类型'
    },
    {
      field: 'feeShareName',
      label: '归集内容'
    },
    {
      field: 'descName',
      label: '费用标签'
    }
  ]

  // 只有当 feeType 等于 1 时才显示归集金额字段
  if (props.feeType === 1) {
    baseColumns.push({
      field: 'feeShareAmount',
      label: '归集金额',
      formatter: formatMoneyColumn()
    })
  }

  return baseColumns
})
</script>
