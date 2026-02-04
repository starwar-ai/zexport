<template>
  <div class="mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
      >添加配件</el-button
    >
    <el-button @click="handleRemove">移除配件</el-button>
  </div>
  <Table
    :columns="tableColumns"
    headerAlign="center"
    align="center"
    :data="tableList"
    @selection-change="handleSelectionChange"
  />

  <SelectProductDialog
    ref="SelectProductDialogRef"
    @sure="handleSure"
  />
</template>
<script setup lang="tsx">
import Table from '@/components/Table/src/Table.vue'
import { DICT_TYPE } from '@/utils/dict'
import { formatDictColumn } from '@/utils/table'
import * as TravelApi from '@/api/oa/travelapp'
import SelectProductDialog from './SelectProductDialog.vue'
import { LengthUnit } from '@/utils/config'
import { EplusMoneyLabel } from '@/components/EplusMoney'

defineOptions({ name: 'AccessoriesList' })
const props = defineProps<{
  formData
}>()

const message = useMessage()
const SelectProductDialogRef = ref()
const tableList = ref([])
const { tableMethods, tableObject } = useTable({
  getListApi: async (ps) => {
    const res = await TravelApi.getSimpleList(ps)
    return {
      list: res.list,
      total: res.total
    }
  }
})

let tableColumns = reactive<TableColumn[]>([
  {
    type: 'selection',
    fixed: 'left',
    width: '60px'
  },
  {
    label: '图片',
    minWidth: '100px',
    slots: {
      default: (data) => {
        const { row } = data
        if (row.picture.length > 0) {
          let url = row.picture.find((item) => item.mainFlag == 1)?.fileUrl
          return <EplusImgEnlarge mainPicture={url} />
        } else {
          return '-'
        }
      }
    }
  },
  {
    field: 'name',
    label: '中文品名',
    minWidth: '100px'
  },
  {
    field: 'code',
    label: '产品编号',
    minWidth: '100px'
  },
  {
    field: 'measureUnit',
    label: '计量单位',
    minWidth: '100px',
    formatter: formatDictColumn(DICT_TYPE.MEASURE_UNIT)
  },
  {
    field: 'material',
    label: '产品材质',
    minWidth: '100px'
  },
  // {
  //   field: 'purchaseUserName',
  //   label: '采购员',
  //   minWidth: '100px'
  // },
  {
    field: 'totalAmountList',
    label: '单品规格/净重',
    minWidth: '180px',
    slots: {
      default: (data) => {
        const { row } = data
        if (row.singleNetweight?.weight) {
          return `${row.specLength}*${row.specWidth}*${row.specHeight} ${LengthUnit}/${row.singleNetweight.weight} ${row.singleNetweight.unit}`
        } else {
          return `${row.specLength}*${row.specWidth}*${row.specHeight} ${LengthUnit}/-`
        }
      }
    }
  },
  {
    field: 'delivery',
    label: '采购交期',
    minWidth: '100px',
    slots: {
      default: (data) => {
        const { row } = data
        return row.delivery ? `${row.delivery}天` : '-'
      }
    }
  },
  {
    label: '采购成本',
    minWidth: '100px',
    slots: {
      default: (data) => {
        const { row } = data
        if (row.withTaxPrice?.amount) {
          return <EplusMoneyLabel val={row.withTaxPrice} />
        } else {
          return '-'
        }
      }
    }
  },
  {
    label: '单品毛重',
    minWidth: '100px',
    slots: {
      default: (data) => {
        const { row } = data
        return `${row.singleGrossweight.weight} ${row.singleGrossweight.unit}`
      }
    }
  },
  {
    field: 'action',
    label: '操作',
    width: '90px',
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
])

const handleAdd = async () => {
  SelectProductDialogRef.value.open(tableList, '添加配件', [3])
}

const handleRemove = () => {
  if (selectedList.value?.length > 0) {
    let delArr = selectedList.value.map((el) => {
      return el.code
    })
    tableList.value = tableList.value.filter((item, index) => {
      if (!delArr.includes(item.code)) {
        return item
      }
    })
  } else {
    message.error('请选择移除的数据')
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

const checkData = () => {
  // if (tableList.value.length == 0) {
  //   message.warning(`请选择配件`)
  //   return false
  // } else {
  //   return true
  // }
  return true
}
defineExpose({ tableList, checkData })

watchEffect(() => {
  tableList.value = props.formData.accessoriesList || []
})
</script>
