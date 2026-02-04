<template>
  <div class="mb-2">
    <el-button @click="handleAdd"> 选择产品 </el-button>
    <el-button @click="handleRemove"> 移除产品 </el-button>
  </div>
  <eplus-form-table
    ref="TableRef"
    selectionFlag
    :list="tableList"
    :defaultVal="{}"
    :schema="tableColumns"
    @selection-change="handleSelectionChange"
  />

  <SelectSkuStock
    ref="selectSkuStockRef"
    @sure="handleSure"
  />
</template>
<script setup lang="tsx">
import { formatNum } from '@/utils'
import { VolumeUnit } from '@/utils/config'
import { getOuterbox, getOuterboxVal } from '@/utils/outboxSpec'
import { columnWidth } from '@/utils/table'
import SplitBoxCom from '@/views/pms/product/main/components/SplitBoxCom.vue'
import SelectSkuStock from './SelectSkuStock.vue'
import { isValidArray } from '@/utils/is'
import { cloneDeep } from 'lodash-es'

defineOptions({ name: 'CreateProCom' })
const props = defineProps<{
  formData
}>()

const message = useMessage()
let tableList = ref<any[]>([])

const selectSkuStockRef = ref()
const handleAdd = () => {
  selectSkuStockRef.value.open(tableList.value)
}

const selectedList = ref<any[]>([])
const handleSelectionChange = (list) => {
  selectedList.value = list
}

const handleRemove = () => {
  if (isValidArray(selectedList.value)) {
    tableList.value = tableList.value.filter((item) => !selectedList.value.includes(item))
  } else {
    message.warning('请选择要移除的产品')
  }
}

const handleSure = (list) => {
  tableList.value = [
    ...tableList.value,
    ...list.map((item) => {
      return {
        ...item,
        orderQuantity: item.outQuantity,
        stockId: item.id
      }
    })
  ]
}

let tableColumns = reactive([
  {
    field: 'skuCode',
    label: '产品编号',
    width: columnWidth.l
  },
  {
    field: 'basicSkuCode',
    label: '基础产品编号',
    width: columnWidth.l
  },
  {
    field: 'oskuCode',
    label: '自营产品货号',
    width: columnWidth.xl
  },
  {
    field: 'cskuCode',
    label: '客户货号',
    width: columnWidth.l
  },
  {
    field: 'skuName',
    label: '中文名称',
    width: columnWidth.l
  },
  {
    field: 'purchaseUserName',
    label: '采购员',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return row.purchaseUser?.nickname
    }
  },
  {
    field: 'venderName',
    label: '供应商名称',
    width: columnWidth.l
  },
  {
    field: 'outQuantity',
    label: '可出库数量',
    width: columnWidth.l
  },
  {
    field: 'orderQuantity',
    label: '通知出库数量',
    width: columnWidth.l,
    slot: (item: any, row: any) => {
      return (
        <>
          <el-input-number
            v-model={row.orderQuantity}
            min={1}
            max={row.outQuantity}
            controls={false}
            precision={0}
            class="!w-90%"
          />
        </>
      )
    },
    rules: [{ required: true, message: '请输入通知入库数量' }]
  },
  {
    field: 'shippedAddress',
    label: '发货地点',
    width: columnWidth.l,
    component: <eplus-dict-select dictType={DICT_TYPE.SHIPPED_ADDRESS} />,
    rules: [{ required: true, message: '请选择发货地点' }]
  },
  {
    field: 'qtyPerInnerbox',
    label: '内盒装量',
    width: columnWidth.l
  },
  {
    field: 'qtyPerOuterbox',
    label: '外箱装量',
    width: columnWidth.l
  },
  {
    field: 'splitBoxFlag',
    label: '是否分箱',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return <SplitBoxCom row={row} />
    }
  },
  {
    field: 'orderBoxQuantity',
    label: '总出库箱数',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return <span>{formatNum(row.orderBoxQuantity * (row.specificationList?.length || 1))}</span>
    }
  },
  {
    field: 'outerbox',
    label: '外箱规格',
    width: columnWidth.l,
    formatter: (item, row, index) => {
      return <div style="white-space: pre-line;">{getOuterbox(row, 'spec')}</div>
    }
  },
  {
    field: 'outerboxVolume',
    label: '外箱体积',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <div>{getOuterbox(row, 'vol')}</div>
    }
  },
  {
    field: 'outerboxGrossweight',
    label: '单箱毛重',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return <div>{getOuterbox(row, 'grossweight')}</div>
    }
  },
  {
    field: 'totalVolume',
    label: '总体积',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return (
        <div>
          {formatNum(row.totalVolume)}
          {VolumeUnit}
        </div>
      )
    }
  },
  {
    field: 'totalWeight',
    label: '总毛重',
    width: columnWidth.l,
    formatter: (item: EplusFormTableSchema, row: Recordable, index: number) => {
      return row.totalWeight ? `${row.totalWeight.weight} ${row.totalWeight.unit}` : '-'
    }
  },

  {
    field: 'remark',
    label: '备注',
    width: '200',
    component: <el-input />
  }
])

watch(
  () => tableList.value,
  (list) => {
    list?.forEach((item: any) => {
      item.orderBoxQuantity = Math.ceil(Number(item.orderQuantity) / Number(item.qtyPerOuterbox))
      item.totalVolume = (getOuterboxVal(item, 'vol') * item.orderBoxQuantity) / 1000000
      if (item.totalWeight) {
        item.totalWeight.weight =
          Number(item.orderBoxQuantity) * Number(item.outerboxGrossweight.weight)
      }
    })
  },
  { immediate: true, deep: true }
)

const TableRef = ref()
const checkData = async () => {
  let valid = await TableRef.value.validate()
  if (valid) {
    return TableRef.value.tableData
  } else {
    message.warning('产品信息提交有误')
    return false
  }
}
defineExpose({ tableList, checkData })
watchEffect(() => {
  if (isValidArray(props.formData.stockNoticeItemRespVOList)) {
    let list = cloneDeep(props.formData.stockNoticeItemRespVOList)
    tableList.value = list.map((item) => {
      return {
        ...item,
        outQuantity: item.outQuantity + item.orderQuantity
      }
    })
  } else {
    tableList.value = []
  }
})
</script>
