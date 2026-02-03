<template>
  <div class="mb15px">
    <el-button
      type="primary"
      @click="handleAdd"
      >添加产品
    </el-button>
    <el-button @click="handleRemove">移除产品</el-button>
  </div>
  <eplus-form-table
    ref="TableRef"
    selectionFlag
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
    @selection-change="handleSelectionChange"
  />

  <SelectProductDialog
    ref="SelectProductDialogRef"
    @sure="handleSure"
  />
</template>
<script setup lang="tsx">
import { DICT_TYPE, getDictLabel } from '@/utils/dict'
import SelectProductDialog from './SelectProductDialog.vue'
import { LengthUnit } from '@/utils/config'
import { EplusMoneyLabel } from '@/components/EplusMoney'
import ContractUpdateSku from '@/views/scm/purchase/components/ContractUpdateSku.vue'
import { getSkuInfo } from '@/api/sms/saleContract/domestic'
import { EplusImgEnlarge } from '@/components/EplusTemplate'

defineOptions({ name: 'SubProductList' })
const props = defineProps<{
  formData
}>()

const message = useMessage()
const SelectProductDialogRef = ref()
const tableList = ref([])

let tableColumns = reactive([
  {
    label: '图片',
    minWidth: '100px',
    field: 'picture',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      if (row.mainPicture || row.thumbnail) {
        return (
          <EplusImgEnlarge
            mainPicture={row.mainPicture}
            thumbnail={row.thumbnail}
          />
        )
      } else {
        return '-'
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
    minWidth: '180px',
    slot: (item, row, index) => {
      return (
        <ContractUpdateSku
          row={row}
          onUpdate={() => handleUpdateSku(row, index)}
        />
      )
    }
  },
  {
    label: '数量',
    field: 'qty',
    width: '120px',
    component: (
      <el-input-number
        min={1}
        controls={false}
        precision={0}
        class="!w-90%"
      />
    ),
    rules: [{ required: true, message: '请输入数量' }]
  },
  {
    field: 'measureUnit',
    label: '计量单位',
    minWidth: '100px',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return getDictLabel(DICT_TYPE.MEASURE_UNIT, row?.measureUnit)
    }
  },
  {
    field: 'venderName',
    label: '供应商名称',
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
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      if (row) {
        return `${row?.specLength}*${row?.specWidth}*${row?.specHeight} ${LengthUnit}/${row?.singleNetweight?.weight} ${row?.singleNetweight?.unit}`
      }
    }
  },
  {
    label: '采购成本',
    minWidth: '100px',
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      if (row.withTaxPrice?.amount) {
        return <EplusMoneyLabel val={row.withTaxPrice} />
      } else {
        return '-'
      }
    }
  },
  {
    label: '操作',
    width: 100,
    fixed: 'right',
    slot: (
      item: EplusFormTableSchema,
      row: Recordable,
      index: number,
      deleteAction: (index: number) => void
    ) => {
      return (
        <>
          <el-button
            link
            type="primary"
            onClick={() => delRow(index)}
          >
            移除
          </el-button>
        </>
      )
    }
  }
])

const handleUpdateSku = async (row, index) => {
  let res = await getSkuInfo(row.code)
  tableList.value[index] = Object.assign(tableList.value[index], {
    ...res,
    qty: row.qty
  })
}

const handleAdd = async () => {
  SelectProductDialogRef.value.open(tableList, '添加产品', [1, 2], 1, props.formData.sourceCode)
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
const TableRef = ref()
const checkData = async () => {
  if (tableList.value?.length == 0) {
    message.warning(`请选择产品`)
    return false
  } else {
    let valid = await TableRef.value.validate()
    if (valid) {
      return TableRef.value.tableData.map((item) => {
        return {
          ...item,
          skuCode: item.code
        }
      })
    } else {
      message.warning('组合产品提交信息有误')
      return false
    }
  }
}
defineExpose({ tableList, checkData })
watchEffect(() => {
  tableList.value = props.formData.subProductList || []
})
</script>
